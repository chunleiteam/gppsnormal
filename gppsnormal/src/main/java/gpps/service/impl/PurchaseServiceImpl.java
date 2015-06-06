package gpps.service.impl;

import gpps.dao.ICashStreamDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.ILenderAccountDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.IProductSeriesDao;
import gpps.dao.IStateLogDao;
import gpps.dao.ISubmitDao;
import gpps.model.Borrower;
import gpps.model.CashStream;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.LenderAccount;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.StateLog;
import gpps.model.Submit;
import gpps.service.IAccountService;
import gpps.service.IBorrowerService;
import gpps.service.ILenderService;
import gpps.service.IPurchaseService;
import gpps.service.ISubmitService;
import gpps.service.PurchaseBackCalculateResult;
import gpps.service.PurchaseCalculateResult;
import gpps.service.exception.InsufficientBalanceException;
import gpps.service.thirdpay.ITransferApplyService;
import gpps.service.thirdpay.LoanFromTP;
import gpps.service.thirdpay.Transfer.LoanJson;
import gpps.tools.DateCalculateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseServiceImpl implements IPurchaseService {
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	ISubmitService submitService;
	@Autowired
	ILenderService lenderService;
	@Autowired
	ILenderAccountDao lenderAccountDao;
	@Autowired
	IBorrowerService borrowerService;
	@Autowired
	IProductDao productDao;
	@Autowired
	IProductSeriesDao productSeriesDao;
	@Autowired
	IGovermentOrderDao orderDao;
	@Autowired
	IPayBackDao paybackDao;
	@Autowired
	IAccountService accountService;
	@Autowired
	ITransferApplyService transferService;
	@Autowired
	IStateLogDao stateLogDao;
	
	@Override
	public PurchaseCalculateResult preCalPurchase(Integer submitid, long date) throws Exception{
		Submit submit = submitDao.find(submitid);
		Product product = productDao.find(submit.getProductId());
		GovermentOrder order = orderDao.find(product.getGovermentorderId());
		
		long repayStarttime = order.getIncomeStarttime();
		
		PurchaseCalculateResult result = new PurchaseCalculateResult();
		
		result.setSubmitAmount(submit.getAmount());
		
		List<CashStream> css = cashStreamDao.findBySubmitAndActionAndState(submit.getId(), CashStream.ACTION_REPAY, CashStream.STATE_SUCCESS);
		BigDecimal alreadChief = new BigDecimal(0);
		for(CashStream cs : css){
			alreadChief = alreadChief.add(cs.getChiefamount());
		}
		result.setChiefAlread(alreadChief.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		result.setChiefTo(submit.getAmount().subtract(alreadChief).setScale(2, BigDecimal.ROUND_HALF_UP));
		
		List<PayBack> pbs = paybackDao.findAllByProduct(product.getId());
		long lastRepayTime = repayStarttime;
		PayBack nextpb = null;
		for(PayBack pb : pbs){
			if(pb.getState()==PayBack.STATE_FINISHREPAY){
				lastRepayTime = pb.getDeadline();
			}else if(pb.getState()==PayBack.STATE_WAITFORREPAY){
				nextpb = pb;
				break;
			}
		}
		
		if(date-lastRepayTime>31L*24*3600*1000 || nextpb.getDeadline()-date>31L*24*3600*1000){
			throw new Exception("还款周期有异常，间隔超过31天！");
		}
		
		
		int currentHDays = DateCalculateUtils.getDays(lastRepayTime, date);
		
		result.setHoldingDays(currentHDays);
		
		int currentInterDays = DateCalculateUtils.getDays(lastRepayTime, nextpb.getDeadline());
		
		BigDecimal interestTo = nextpb.getInterest().multiply(result.getSubmitAmount()).divide(product.getRealAmount(), 2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(currentHDays)).divide(new BigDecimal(currentInterDays), 2, BigDecimal.ROUND_DOWN);
		result.setInterestTo(interestTo);
		
		result.setPurchaseFee(BigDecimal.ZERO);
		result.setPurchaseAmount(result.getChiefTo().add(result.getInterestTo()));
		
		return result;
	}
	
	
	@Override
	public PurchaseBackCalculateResult preCalPurchaseBack(Integer submitId, long date)
			throws Exception {
		
		Submit submit = submitDao.find(submitId);
		Product product = productDao.find(submit.getProductId());
		GovermentOrder order = orderDao.find(product.getGovermentorderId());
		ProductSeries ps = productSeriesDao.find(product.getProductseriesId());
		
		PurchaseBackCalculateResult result = null;
		if(ps.getType()==ProductSeries.TYPE_FINISHPAYINTERESTANDCAPITAL || ps.getType()==ProductSeries.TYPE_FIRSTINTERESTENDCAPITAL){
			result = preCalPurchaseBackForJHAndJQ(submit, product, order, ps.getType(), date);
		}else{
			result = preCalPurchaseBackForWJ(submit, product, order, date);
		}
		
		
		return result;
	}
	private PurchaseBackCalculateResult preCalPurchaseBackForWJ(Submit submit, Product product, GovermentOrder order, long date) throws Exception{
		
		long repayStarttime = order.getIncomeStarttime();
		int holdingDays = DateCalculateUtils.getDays(repayStarttime, date);
		PurchaseBackCalculateResult result = new PurchaseBackCalculateResult();
		result.setHoldingDays(holdingDays);
		result.setSubmitAmount(submit.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
		
		List<CashStream> css = cashStreamDao.findBySubmitAndActionAndState(submit.getId(), CashStream.ACTION_REPAY, CashStream.STATE_SUCCESS);
		BigDecimal alreadChief = new BigDecimal(0);
		BigDecimal alreadInterest = new BigDecimal(0);
		for(CashStream cs : css){
			alreadChief = alreadChief.add(cs.getChiefamount());
			alreadInterest = alreadInterest.add(cs.getInterest());
		}
		result.setChiefAlread(alreadChief.setScale(2, BigDecimal.ROUND_HALF_UP));
		result.setInterestAlready(alreadInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		result.setChiefTo(submit.getAmount().subtract(alreadChief).setScale(2, BigDecimal.ROUND_HALF_UP));
		
		List<PayBack> pbs = paybackDao.findAllByProduct(product.getId());
		long lastRepayTime = repayStarttime;
		PayBack nextpb = null;
		for(PayBack pb : pbs){
			if(pb.getState()==PayBack.STATE_FINISHREPAY){
				lastRepayTime = pb.getDeadline();
			}else if(pb.getState()==PayBack.STATE_WAITFORREPAY){
				nextpb = pb;
				break;
			}
		}
		
		if(date-lastRepayTime>31L*24*3600*1000 || nextpb.getDeadline()-date>31L*24*3600*1000){
			throw new Exception("还款周期有异常，间隔超过31天！");
		}
		
		int currentHDays = DateCalculateUtils.getDays(lastRepayTime, date);
		
		int currentInterDays = DateCalculateUtils.getDays(lastRepayTime, nextpb.getDeadline());
		
		
		BigDecimal interestTo = nextpb.getInterest().multiply(result.getSubmitAmount()).divide(product.getRealAmount(), 2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(currentHDays)).divide(new BigDecimal(currentInterDays), 2, BigDecimal.ROUND_DOWN);
		result.setInterestTo(interestTo);
		
		
		result.setRateAfterPB(product.getRate());
		BigDecimal interestAfterPB = result.getInterestAlready().add(result.getInterestTo());
		result.setInterestAfterPB(interestAfterPB);
		
		result.setPurchaseBackFee(result.getInterestAlready().subtract(result.getInterestAfterPB()));
		
		
		result.setPurchaseAmount(result.getChiefTo().subtract(result.getPurchaseBackFee()));
		
		result.setTotalAmount(result.getChiefAlread().add(result.getInterestAlready()).add(result.getPurchaseAmount()));
		return result;
	}
	private PurchaseBackCalculateResult preCalPurchaseBackForJHAndJQ(Submit submit, Product product, GovermentOrder order, int productSeriesType, long date) throws Exception{
		int holdingDays = 0;	//用户实际持有的天数
		if(submit.getHoldingstarttime()<order.getIncomeStarttime())
		{
			holdingDays = DateCalculateUtils.getDays(order.getIncomeStarttime(), date);
		}else{
			holdingDays = DateCalculateUtils.getDays(submit.getHoldingstarttime(), date);
		}
		
		PurchaseBackCalculateResult result = new PurchaseBackCalculateResult();
		result.setHoldingDays(holdingDays);
		result.setSubmitAmount(submit.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
		
		List<CashStream> css = cashStreamDao.findBySubmitAndActionAndState(submit.getId(), CashStream.ACTION_REPAY, CashStream.STATE_SUCCESS);
		BigDecimal alreadInterest = new BigDecimal(0);
		for(CashStream cs : css){
			//只有在持有期间内获得的利息才累加起来
			if(cs.getCreatetime()>submit.getHoldingstarttime()){
				alreadInterest = alreadInterest.add(cs.getInterest());
			}
		}
		result.setChiefAlread(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));  //对于均衡和进取型来说，不摊还本金，所以在还款完毕之前所还本金肯定是0
		result.setInterestAlready(alreadInterest.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		result.setChiefTo(submit.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
		
		BigDecimal holdingInterestTotal = submit.getAmount().multiply(product.getRate()).multiply(new BigDecimal(holdingDays)).divide(new BigDecimal(365), 2, BigDecimal.ROUND_DOWN);
		BigDecimal holdingInterestShould = null;
		
		
		if(productSeriesType==ProductSeries.TYPE_FIRSTINTERESTENDCAPITAL)
		{
			result.setRateAfterPB(IPurchaseService.AFTER_PURCHASE_BACK_RATE_JH);
			holdingInterestShould = submit.getAmount().multiply(IPurchaseService.AFTER_PURCHASE_BACK_RATE_JH).multiply(new BigDecimal(holdingDays)).divide(new BigDecimal(365), 2, BigDecimal.ROUND_DOWN);
			result.setInterestAfterPB(holdingInterestShould);
		}else{
			result.setRateAfterPB(IPurchaseService.AFTER_PURCHASE_BACK_RATE_JQ);
			holdingInterestShould = submit.getAmount().multiply(IPurchaseService.AFTER_PURCHASE_BACK_RATE_JQ).multiply(new BigDecimal(holdingDays)).divide(new BigDecimal(365), 2, BigDecimal.ROUND_DOWN);
			result.setInterestAfterPB(holdingInterestShould);
		}
		
		
		result.setInterestTo(holdingInterestTotal.subtract(alreadInterest));
		
		
		result.setPurchaseBackFee(result.getInterestAlready().subtract(result.getInterestAfterPB()).setScale(2, BigDecimal.ROUND_HALF_UP));
		
		
		result.setPurchaseAmount(result.getSubmitAmount().subtract(result.getPurchaseBackFee()).setScale(2, BigDecimal.ROUND_HALF_UP));
		
		result.setTotalAmount(result.getChiefAlread().add(result.getInterestAlready()).add(result.getPurchaseAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
		return result;
	}

	
	@Override
	public void canApplyPurchase(Integer submitId, long date) throws Exception{
		Submit submit = submitDao.find(submitId);
		if(submit==null){
			throw new Exception("系统中未找到对应的投标！");
		}
		if(submit.getState()!=Submit.STATE_COMPLETEPAY || submit.getPurchaseFlag()!=Submit.PURCHASE_FLAG_PURCHASEBACK){
			throw new Exception("投标状态异常，无法申请回购！");
		}
		Product product = productDao.find(submit.getProductId());
		if(product==null || product.getState()!=Product.STATE_REPAYING){
			throw new Exception("投标对应的产品状态不是还款中，无法申请回购！");
		}
	}
	
	@Override
	public void canApplyPurchaseBack(Integer submitId, long date) throws Exception{
		Submit submit = submitDao.find(submitId);
		if(submit==null){
			throw new Exception("系统中未找到对应的投标！");
		}
		if(submit.getState()!=Submit.STATE_COMPLETEPAY){
			throw new Exception("投标状态异常，无法申请回购！");
		}
		Product product = productDao.find(submit.getProductId());
		if(product==null || product.getState()!=Product.STATE_REPAYING){
			throw new Exception("投标对应的产品状态不是还款中，无法申请回购！");
		}
		
		GovermentOrder order = orderDao.find(product.getGovermentorderId());
		
		long holdingstarttime = submit.getHoldingstarttime()==0?order.getIncomeStarttime():submit.getHoldingstarttime();
		
		List<PayBack> pbs = paybackDao.findAllByProduct(product.getId());
		
		if(date<=holdingstarttime+15L*24*3600*1000){
			throw new Exception("持有不足15天，无法申请回购！");
		}
		
		for(PayBack pb : pbs){
			
			if(pb.getState()==PayBack.STATE_FINISHREPAY){
				continue;
			}
			
			if(pb.getState()==PayBack.STATE_WAITFORCHECK || pb.getState()==PayBack.STATE_APPLYREPAYINADVANCE){
				throw new Exception("本产品正处于申请还款/申请提前还款期，不允许申请回购！");
			}
			
			
			if(date>=(pb.getDeadline()+2L*24*3600*1000)){
				throw new Exception("本产品已逾期超过两天，暂停回购！");
			}else if((pb.getDeadline()-date)<=3L*24*3600*1000){
				throw new Exception("已进入还款周期【还款日三天内】不允许申请回购！");
			}else{
				return;   //没有问题，可以申请回购
			}
		}
	}
	
	@Override
	public void applyPurchaseBack(Integer submitId) throws Exception {
		Submit submit = submitDao.find(submitId);
		if(submit==null){
			throw new Exception("系统中未找到对应的投标！");
		}
		
		Lender lender=lenderService.getCurrentUser();
		lender=lenderService.find(lender.getId());
		
		if(submit.getLenderId().intValue()!=lender.getId().intValue()){
			throw new Exception("您无权申请其他人所有债权的回购！");
		}
		
		
		
		long date = System.currentTimeMillis();
		
		
		canApplyPurchaseBack(submitId, date);
		
		PurchaseBackCalculateResult res = preCalPurchaseBack(submitId, date);
		
		submitService.changeState(submitId, Submit.STATE_WAITFORPURCHASEBACK);
		accountService.applyPurchaseBack(lender.getAccountId(), submitId, res.getChiefTo(), res.getPurchaseBackFee().negate(), "回购");
	}

	@Override
	public void purchaseBack(Integer submitId, Integer borrowerId) throws Exception {
		Submit submit = submitDao.find(submitId);
		
		if(submit==null){
			throw new Exception("没有找到对应申请回购的投标！");
		}
		if(submit.getState()!=Submit.STATE_WAITFORPURCHASEBACK){
			throw new Exception("该投标不处于待回购中！");
		}
		
		Lender lender = lenderService.find(submit.getLenderId());
		Product product = productDao.find(submit.getProductId());
		
		Borrower borrower = borrowerService.find(borrowerId);
		
		if(borrower==null || borrower.getLender()==null){
			throw new Exception("回购企业用户不存在，或回购代持用户不存在！");
		}
		
		List<CashStream> css = cashStreamDao.findBySubmitAndActionAndState(submitId, CashStream.ACTION_PURCHASEBACK, CashStream.STATE_INIT);
		
		if(css==null || css.isEmpty()){
			throw new Exception("该申请回购的投标对应的现金流状态不正确！");
		}
		
		CashStream cs = css.get(0);
		cashStreamDao.updateBorrowerId(cs.getId(), borrower.getAccountId());
		
		List<LoanJson> loanJsons=new ArrayList<LoanJson>();
			
		String toMoneyMoreMore = lender.getThirdPartyAccount();
			
		LoanJson loadJson=new LoanJson();
		loadJson.setLoanOutMoneymoremore(borrower.getThirdPartyAccount());
		loadJson.setLoanInMoneymoremore(toMoneyMoreMore);
		loadJson.setOrderNo(String.valueOf(cs.getId()));
		loadJson.setBatchNo(String.valueOf(product.getId()));
		loadJson.setAmount(cs.getChiefamount().add(cs.getInterest()).toString());
		loanJsons.add(loadJson);
		
		//无需审核直接转账
		List<LoanFromTP> loans = transferService.justTransferApplyNoNeedAudit(loanJsons);
		
		LoanFromTP loan = loans.get(0);
		
		cashStreamDao.updateLoanNo(cs.getId(), loan.getLoanNo(), null);
		accountService.changeCashStreamState(cs.getId(), CashStream.STATE_SUCCESS);
		
		submitDao.purchaseBack(borrower.getId(),borrower.getLender().getId(), submitId);
		
		StateLog stateLog=new StateLog();
		stateLog.setCreatetime(System.currentTimeMillis());
		stateLog.setRefid(submitId);
		stateLog.setTarget(Submit.STATE_COMPLETEPAY);
		stateLog.setType(stateLog.TYPE_SUBMIT);
		stateLogDao.create(stateLog);
		
		Submit sub = new Submit();
		sub.setAmount(submit.getAmount());
		sub.setCreatetime(submit.getCreatetime());
		sub.setLastmodifytime(System.currentTimeMillis());
		sub.setLenderId(lender.getId());
		sub.setProductId(submit.getProductId());
		sub.setState(Submit.STATE_PURCHASEBACKDONE);
		submitDao.create(sub);
		
		StateLog stateLog2=new StateLog();
		stateLog2.setCreatetime(System.currentTimeMillis());
		stateLog2.setRefid(sub.getId());
		stateLog2.setTarget(sub.getState());
		stateLog2.setType(stateLog.TYPE_SUBMIT);
		stateLogDao.create(stateLog2);
	}
	
	@Override
	public void synchronizeAccount(Integer borrowerId) throws Exception{
		Borrower borrower = borrowerService.find(borrowerId);
		
		if(borrower==null || borrower.getLender()==null){
			throw new Exception("回购企业账户不存在，或者回购企业对应的代持账户不存在");
		}
		
		LenderAccount lenderAccount = lenderAccountDao.find(borrower.getLender().getAccountId());
		if(lenderAccount.getTotal().compareTo(BigDecimal.ZERO)==0 && lenderAccount.getTotalincome().compareTo(BigDecimal.ZERO)==0 && lenderAccount.getUsable().compareTo(BigDecimal.ZERO)==0 && lenderAccount.getUsed().compareTo(BigDecimal.ZERO)==0){
			throw new Exception("已同步完毕，无需同步");
		}
		
		Integer csId = accountService.synchronizeAccount(lenderAccount.getId(), borrower.getAccountId(), lenderAccount.getUsed(), lenderAccount.getTotalincome().negate(), "回购企业账户同步");
		
		CashStream cs = cashStreamDao.find(csId);
		
		List<LoanJson> loanJsons=new ArrayList<LoanJson>();
		
		String toMoneyMoreMore = borrower.getThirdPartyAccount();
			
		LoanJson loadJson=new LoanJson();
		loadJson.setLoanOutMoneymoremore(borrower.getLender().getThirdPartyAccount());
		loadJson.setLoanInMoneymoremore(toMoneyMoreMore);
		loadJson.setOrderNo(String.valueOf(cs.getId()));
		loadJson.setBatchNo("0"); //账户同步的现金流标号统一设置成0
		loadJson.setAmount(cs.getChiefamount().add(cs.getInterest()).negate().toString()); //现金流中记录的本金与利息都是负的
		loanJsons.add(loadJson);
		
		//无需审核直接转账
		List<LoanFromTP> loans = transferService.justTransferApplyNoNeedAudit(loanJsons);
		
		LoanFromTP loan = loans.get(0);
		
		cashStreamDao.updateLoanNo(cs.getId(), loan.getLoanNo(), null);
		accountService.changeCashStreamState(cs.getId(), CashStream.STATE_SUCCESS);
		
	}

	@Transactional
	@Override
	public Integer purchase(Integer submitId) throws Exception {
		Lender lender=lenderService.getCurrentUser();
		lender=lenderService.find(lender.getId());
		
		long date = System.currentTimeMillis();
		PurchaseCalculateResult result = preCalPurchase(submitId, date);
		
		LenderAccount account=lenderAccountDao.find(lender.getAccountId());
		//判断当前账户余额是否足够购买
		if(result.getPurchaseAmount().compareTo(account.getUsable())>0)
			throw new InsufficientBalanceException("您账户的余额不足，请先充值");
		
		//上锁，确保同一时刻只有一个线程在执行本段代码
		synchronized(this){
			canApplyPurchase(submitId, date);
			submitService.changeState(submitId, Submit.STATE_WAITFORPURCHASE);
			Integer csID = accountService.purchase(lender.getAccountId(), submitId, result.getChiefTo().negate(), result.getInterestTo().negate(), "购买债权");
			return csID;
		}
	}
}
