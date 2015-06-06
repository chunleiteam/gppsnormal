package gpps.service.impl;

import static gpps.tools.ObjectUtil.checkNullObject;
import gpps.constant.Pagination;
import gpps.dao.ICashStreamDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.ILenderAccountDao;
import gpps.dao.ILenderDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.IStateLogDao;
import gpps.dao.ISubmitDao;
import gpps.model.CashStream;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.LenderAccount;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.StateLog;
import gpps.model.Submit;
import gpps.model.Task;
import gpps.service.IAccountService;
import gpps.service.IGovermentOrderService;
import gpps.service.ILenderService;
import gpps.service.IPayBackService;
import gpps.service.IProductService;
import gpps.service.ISubmitService;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.InsufficientBalanceException;
import gpps.service.exception.InsufficientProductException;
import gpps.service.exception.ProductSoldOutException;
import gpps.service.exception.UnreachBuyLevelException;
import gpps.tools.DateCalculateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@DependsOn({"gpps.service.IGovermentOrderService"})
public class SubmitServiceImpl implements ISubmitService {
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	IProductService productService;
	@Autowired
	ILenderService lenderService;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	IAccountService accountService;
	@Autowired
	ILenderAccountDao lenderAccountDao;
	@Autowired
	IGovermentOrderService orderService;
	@Autowired
	IGovermentOrderDao govermentOrderDao;
	@Autowired
	IProductDao productDao;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	IStateLogDao stateLogDao;
	@Autowired
	IPayBackService payBackService;
	Logger logger=Logger.getLogger(this.getClass());
	@PostConstruct
	public void init() {
		//构建守护线程，检查待支付Submit，到一定时间未支付成功设置为“退订”
		Thread taskThread=new Thread(){
			public void run()
			{
				logger.info("启动Submit过期验证服务线程");
				while(true)
				{
					
					try {
						List<Submit> submits=submitDao.findAllByState(Submit.STATE_WAITFORPAY);
						if(submits!=null&&submits.size()>0)
						{
							for(Submit submit:submits)
							{
								if((submit.getCreatetime()+Submit.PAYEXPIREDTIME)>System.currentTimeMillis())
								{	
									continue;
								}
								processUnsubscribeSubmit(submit);
								logger.info("Submit[id:"+submit.getId()+"]10分钟内未支付，过期退订");
							}
						}
						List<Submit> sub_submits = submitDao.findAllByState(Submit.STATE_SUBSCRIBE_WAITFORPAY);
						if(sub_submits!=null&&!sub_submits.isEmpty()){
							for(Submit submit:sub_submits){
								if((submit.getCreatetime()+Submit.SUBSCRIBE_PAYEXPIREDTIME)>System.currentTimeMillis())
								{
									continue;
								}
								processSubscribe_UnPayedSubmit(submit);
								logger.info("预约Submit[id:"+submit.getId()+"]12小时内未支付，过期退订");
							}
						}
					
					} catch (Throwable e) {
						logger.error(e.getMessage(),e);
					}
					try {
						sleep(10L*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			
		};
		taskThread.setName("SubmitCheckThread");
		taskThread.start();
	}
	@Transactional
	private void processUnsubscribeSubmit(Submit submit) throws IllegalConvertException {
		changeState(submit.getId(), Submit.STATE_UNSUBSCRIBE);
		// 金额回滚
		Product product=productDao.find(submit.getProductId());
		try{
			productDao.buy(product.getId(), submit.getAmount().negate());
			product=orderService.applyFinancingProduct(submit.getProductId(), product.getGovermentorderId());
			product.setRealAmount(product.getRealAmount().subtract(submit.getAmount()));
		}finally
		{
			orderService.releaseFinancingProduct(product);
		}
	}
	@Transactional
	private void processSubscribe_UnPayedSubmit(Submit submit) throws IllegalConvertException {
		changeState(submit.getId(), Submit.STATE_UNSUBSCRIBE);
		// 金额回滚
		productDao.buy(submit.getProductId(), submit.getAmount().negate());
	}
	@Override
	@Transactional
	public Integer buy(Integer productId, int num)
			throws InsufficientBalanceException,ProductSoldOutException,InsufficientProductException,UnreachBuyLevelException {
		//TODO 验证amount格式，例如：1w起之类的
		Product product=productService.find(productId);
		checkNullObject(Product.class, product);
		if(num<product.getMinimum())
			throw new IllegalArgumentException("提交失败，最小投资金额为:"+product.getMinimum());
		if(num%product.getMiniAdd()!=0)
			throw new IllegalArgumentException("提交失败，最小递增金额为:"+product.getMiniAdd());
		
		Lender lender=lenderService.getCurrentUser();
		lender=lenderService.find(lender.getId());
		
		
		//新手单定义为“进取型”并且购买级别为0
		if(product.getProductSeries().getType()==ProductSeries.TYPE_FINISHPAYINTERESTANDCAPITAL && product.getLevelToBuy()==0){
			if(num>100)
			{
				throw new IllegalArgumentException("提交失败，新手产品单笔提交额度不得超过100元");
			}
			int count = submitDao.countByLenderAndState(lender.getId(), Submit.STATE_COMPLETEPAY);
			if(count>0){
				throw new IllegalArgumentException("提交失败，已成功购买过产品的用户无法购买新手产品");
			}
		}
		
		if(product.getProductSeries().getType()==ProductSeries.TYPE_FINISHPAYINTERESTANDCAPITAL && num>10000){
			throw new IllegalArgumentException("提交失败，进取型产品单笔提交额度不得超过10000元");
		}
		
		
		
		int submitsWaitForPay = submitDao.countByLenderAndState(lender.getId(), Submit.STATE_WAITFORPAY);
		if(submitsWaitForPay>0){
			throw new IllegalArgumentException("您有尚未支付的投标，请到<a href=\"myaccountdetail.html?fid=submit&sid=submit-toafford\">我的账户</a>先支付后，才能再次申请投标！");
		}
		
		
		LenderAccount account=lenderAccountDao.find(lender.getAccountId());
		BigDecimal amount=new BigDecimal(num);
		//判断当前账户余额是否足够购买
		if(amount.compareTo(account.getUsable())>0)
			throw new InsufficientBalanceException("您账户的余额不足，请先充值");
		//判断用户购买级别
		if(lender.getLevel()<product.getLevelToBuy())
			throw new UnreachBuyLevelException("您尚未达到购买此产品的级别,查看<a href=\"myaccountdetail.html?fid=mycenter&sid=my-score\" target=\"_blank\">我的积分</a>");
		try
		{
			product=orderService.applyFinancingProduct(productId, product.getGovermentorderId());
			if(product==null)
				throw new ProductSoldOutException("产品已售完");
			if(System.currentTimeMillis()>product.getGovermentOrder().getFinancingEndtime())
				throw new ProductSoldOutException("产品已结束融资");
			if(amount.compareTo(product.getExpectAmount().subtract(product.getRealAmount()))>0)
				throw new InsufficientProductException("产品余额不足");
			
			Submit submit=new Submit();
			submit.setAmount(amount);
			submit.setLenderId(lenderService.getCurrentUser().getId());
			submit.setProductId(productId);
			submit.setState(Submit.STATE_WAITFORPAY);
			submitDao.create(submit);

			StateLog stateLog=new StateLog();
			stateLog.setCreatetime(System.currentTimeMillis());
			stateLog.setRefid(submit.getId());
			stateLog.setTarget(submit.getState());
			stateLog.setType(stateLog.TYPE_SUBMIT);
			stateLogDao.create(stateLog);
			
			productDao.buy(productId, amount);
//			Integer cashStreamId=accountService.freezeLenderAccount(lender.getAccountId(), amount, submit.getId(), null);
			product.setRealAmount(product.getRealAmount().add(amount));
			return submit.getId();
		}finally
		{
			orderService.releaseFinancingProduct(product);
		}
	}
	static int[][] validConverts={
		{Submit.STATE_WAITFORPAY,Submit.STATE_UNSUBSCRIBE},
		{Submit.STATE_WAITFORPAY,Submit.STATE_COMPLETEPAY},
		{Submit.STATE_COMPLETEPAY,Submit.STATE_FAILBIDDING},
		{Submit.STATE_SUBSCRIBE_WAITFORPAY,Submit.STATE_UNSUBSCRIBE},
		{Submit.STATE_SUBSCRIBE_WAITFORPAY,Submit.STATE_COMPLETEPAY},
		{Submit.STATE_COMPLETEPAY,Submit.STATE_WAITFORPURCHASEBACK},
		{Submit.STATE_WAITFORPURCHASEBACK,Submit.STATE_COMPLETEPAY},
		{Submit.STATE_COMPLETEPAY,Submit.STATE_WAITFORPURCHASE},
		{Submit.STATE_WAITFORPURCHASE,Submit.STATE_COMPLETEPAY}
		};
	public void changeState(Integer submitId, int state) throws IllegalConvertException {
		Submit submit = submitDao.find(submitId);
		if (submit == null)
			throw new RuntimeException("submit is not existed");
		for(int[] validStateConvert:validConverts)
		{
			if(submit.getState()==validStateConvert[0]&&state==validStateConvert[1])
			{
				submitDao.changeState(submitId, state,System.currentTimeMillis());
				StateLog stateLog=new StateLog();
				stateLog.setSource(submit.getState());
				stateLog.setTarget(state);
				stateLog.setType(StateLog.TYPE_SUBMIT);
				stateLog.setRefid(submitId);
				stateLogDao.create(stateLog);
				return;
			}
		}
		throw new IllegalConvertException();
	}

	@Override
	public Submit find(Integer id) {
		Submit submit=submitDao.find(id);
		if(submit!=null)
			submit.setProduct(productDao.find(submit.getProductId()));
		return submit;
	}

	@Override
	public Map<String,Object> findMyAllSubmits(int offset,int recnum) {
		Lender lender=lenderService.getCurrentUser();
		int count=submitDao.countByLender(lender.getId());
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<Submit> submits=submitDao.findAllByLender(lender.getId(), offset, recnum);
		for(Submit submit:submits)
		{
			submit.setProduct(productService.find(submit.getProductId()));
			submit.getProduct().setGovermentOrder(govermentOrderDao.find(submit.getProduct().getGovermentorderId()));
			//计算已还款
			if(submit.getState()!=Submit.STATE_COMPLETEPAY)
				continue;
			List<CashStream> cashStreams=findSubmitCashStream(submit.getId());
			if(cashStreams==null||cashStreams.size()==0)
				continue;
			for(CashStream cashStream:cashStreams)
			{
				if(cashStream.getAction()==CashStream.ACTION_REPAY&&cashStream.getState()==CashStream.STATE_SUCCESS)
				{
					submit.setRepayedAmount(submit.getRepayedAmount().add(cashStream.getChiefamount()));
				}
			}
		}
		return Pagination.buildResult(submits,count,offset, recnum);
	}

	@Override
	public List<CashStream> findSubmitCashStream(Integer submitId) {
		return cashStreamDao.findSubmitCashStream(submitId);
	}

	@Override
	public List<Submit> findMyAllRetreatSubmits(){
		Lender lender=lenderService.getCurrentUser();
		List<Integer> states=new ArrayList<Integer>();
		states.add(Submit.STATE_UNSUBSCRIBE);
		List<Submit> submits=submitDao.findAllByLenderAndStates(lender.getId(), states);
		if(submits==null||submits.size()==0)
			return new ArrayList<Submit>(0);
		for(Submit submit:submits)
		{
			submit.setProduct(productService.find(submit.getProductId()));
			submit.getProduct().setGovermentOrder(govermentOrderDao.find(submit.getProduct().getGovermentorderId()));
			submit.setPayExpiredTime(submit.getCreatetime()+Submit.PAYEXPIREDTIME);
		}
		return submits;
	}
	
	@Override
	public List<Submit> findMyAllWaitforPayingSubscribeSubmits(){
		Lender lender=lenderService.getCurrentUser();
		List<Integer> states=new ArrayList<Integer>();
		states.add(Submit.STATE_SUBSCRIBE_WAITFORPAY);
		List<Submit> submits=submitDao.findAllByLenderAndStates(lender.getId(), states);
		if(submits==null||submits.size()==0)
			return new ArrayList<Submit>(0);
		for(Submit submit:submits)
		{
			submit.setProduct(productService.find(submit.getProductId()));
			submit.getProduct().setGovermentOrder(govermentOrderDao.find(submit.getProduct().getGovermentorderId()));
			submit.setPayExpiredTime(submit.getCreatetime()+Submit.SUBSCRIBE_PAYEXPIREDTIME);
		}
		return submits;
	}
	
	@Override
	public List<Submit> findMyAllWaitforPayingSubmits() {
		Lender lender=lenderService.getCurrentUser();
		List<Integer> states=new ArrayList<Integer>();
		states.add(Submit.STATE_WAITFORPAY);
		List<Submit> submits=submitDao.findAllByLenderAndStates(lender.getId(), states);
		if(submits==null||submits.size()==0)
			return new ArrayList<Submit>(0);
		for(Submit submit:submits)
		{
			submit.setProduct(productService.find(submit.getProductId()));
			submit.getProduct().setGovermentOrder(govermentOrderDao.find(submit.getProduct().getGovermentorderId()));
			submit.setPayExpiredTime(submit.getCreatetime()+Submit.PAYEXPIREDTIME);
		}
		return submits;
//		List<Submit> submits=new ArrayList<Submit>();
//		for(int i=0;i<100;i++)
//		{
//			Submit submit=new Submit();
//			submit.setId(i);
//			submit.setAmount(new BigDecimal(10000));
//			submit.setProduct(new Product());
//			submit.getProduct().setId(i);
//			submit.getProduct().setGovermentOrder(new GovermentOrder());
//			submit.getProduct().getGovermentOrder().setTitle("淘宝借款三期");
//			submit.setPayExpiredTime(System.currentTimeMillis()+Submit.PAYEXPIREDTIME);
//			submits.add(submit);
//		}
//		return submits;
	}

	@Override
	public Map<String, Object> findAllSubmitsByStateAndProductStatesAndPurchaseFlag(int state,int productStates, int purchaseFlag,int offset,int recnum){
		List<Integer> stateList=null;
		if(productStates!=-1)
		{
			stateList=new ArrayList<Integer>();
			for(int productState:IProductService.productStates)
			{
				if((productState&productStates)>0)
					stateList.add(productState);
			}
		}
		int count=submitDao.countByStateAndProductStatesAndPurchaseFlag(state, stateList, purchaseFlag);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<Submit> submits=submitDao.findAllByStateAndProductStatesAndPurchaseFlagWithPaged(state, stateList, purchaseFlag, offset, recnum);
		for(Submit submit:submits)
		{
			submit.setProduct(productService.find(submit.getProductId()));
			submit.getProduct().setGovermentOrder(govermentOrderDao.find(submit.getProduct().getGovermentorderId()));
			//计算已还款
			if(submit.getState()!=Submit.STATE_COMPLETEPAY)
				continue;
			List<CashStream> cashStreams=findSubmitCashStream(submit.getId());
			if(cashStreams==null||cashStreams.size()==0)
				continue;
			for(CashStream cashStream:cashStreams)
			{
				if(cashStream.getAction()==CashStream.ACTION_REPAY&&cashStream.getState()==CashStream.STATE_SUCCESS)
				{
					submit.setRepayedAmount(submit.getRepayedAmount().add(cashStream.getChiefamount()).add(cashStream.getInterest()));
				}
			}
			//计算待回款
			List<PayBack> payBacks=payBackService.generatePayBacksBySubmit(submit.getId());
			for(PayBack payBack:payBacks)
			{
				if(payBack.getState()!=PayBack.STATE_WAITFORREPAY)
					continue;
				submit.setWaitforRepayAmount(submit.getWaitforRepayAmount().add(payBack.getChiefAmount()).add(payBack.getInterest()));
			}
		}
		return Pagination.buildResult(submits,count,offset, recnum);
	}
	
	@Override
	public Map<String, Object> findMyAllSubmitsByProductStates(int productStates,int offset,int recnum) {
		Lender lender=lenderService.getCurrentUser();
		List<Integer> stateList=null;
		if(productStates!=-1)
		{
			stateList=new ArrayList<Integer>();
			for(int productState:IProductService.productStates)
			{
				if((productState&productStates)>0)
					stateList.add(productState);
			}
		}
		int count=submitDao.countByLenderAndProductStates(lender.getId(), stateList);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<Submit> submits=submitDao.findAllPayedByLenderAndProductStates(lender.getId(), stateList, offset, recnum);
		for(Submit submit:submits)
		{
			submit.setProduct(productService.find(submit.getProductId()));
			submit.getProduct().setGovermentOrder(govermentOrderDao.find(submit.getProduct().getGovermentorderId()));
			//计算已还款
			if(submit.getState()!=Submit.STATE_COMPLETEPAY)
				continue;
			List<CashStream> cashStreams=findSubmitCashStream(submit.getId());
			if(cashStreams==null||cashStreams.size()==0)
				continue;
			for(CashStream cashStream:cashStreams)
			{
				if(cashStream.getAction()==CashStream.ACTION_REPAY&&cashStream.getState()==CashStream.STATE_SUCCESS)
				{
					submit.setRepayedAmount(submit.getRepayedAmount().add(cashStream.getChiefamount()).add(cashStream.getInterest()));
				}
			}
			//计算待回款
			List<PayBack> payBacks=payBackService.generatePayBacksBySubmit(submit.getId());
			for(PayBack payBack:payBacks)
			{
				if(payBack.getState()!=PayBack.STATE_WAITFORREPAY)
					continue;
				submit.setWaitforRepayAmount(submit.getWaitforRepayAmount().add(payBack.getChiefAmount()).add(payBack.getInterest()));
			}
		}
		return Pagination.buildResult(submits,count,offset, recnum);
	}

	//根据最近90天内参与购买产品的次数计算本次购买得到的积分
	private int calculateGrade(int grade, int amount, Integer lenderid){
		long starttime = (new Date()).getTime()-90L*24*3600*1000;
		List<Integer> states = new ArrayList<Integer>();
		states.add(2);
		List<Submit> submits = submitDao.findAllByLenderAndStatesAndCreatetime(lenderid, states, starttime, null);
		Set<Integer> pids = new HashSet<Integer>();
		for(Submit submit : submits){
			pids.add(submit.getProductId());
		}
		BigDecimal weight = Lender.gradeWeight(pids.size());
		int resGrade = grade + weight.multiply(new BigDecimal(amount)).intValue();
		return resGrade;
	}
	
	@Override
	@Transactional
	public void confirmBuy(Integer submitId) throws IllegalConvertException {
		changeState(submitId, Submit.STATE_COMPLETEPAY);
		Submit submit=submitDao.find(submitId);
		Lender lender=lenderService.find(submit.getLenderId());
		int grade=calculateGrade(lender.getGrade(),submit.getAmount().intValue(), lender.getId());
		int level=Lender.gradeToLevel(grade);
		lenderDao.changeGradeAndLevel(lender.getId(), grade, level);
		lender=lenderService.getCurrentUser();
		if(lender!=null)
		{
			lender.setGrade(grade);
			lender.setLevel(level);
		}
	}
	
	@Override
	@Transactional
	public void confirmPurchase(Integer submitId) throws IllegalConvertException{
		
		Submit submit=submitDao.find(submitId);
		Lender lender=lenderService.getCurrentUser();
		lender = lenderService.find(lender.getId());
		
		
		//为代持账户增加一个购买成功的标的记录
		Submit sub = new Submit();
		sub.setAmount(submit.getAmount());
		sub.setCreatetime(submit.getCreatetime());
		sub.setLastmodifytime(System.currentTimeMillis());
		sub.setLenderId(submit.getLenderId());
		sub.setProductId(submit.getProductId());
		sub.setState(Submit.STATE_PURCHASEDONE);
		submitDao.create(sub);
		
		StateLog stateLog2=new StateLog();
		stateLog2.setCreatetime(System.currentTimeMillis());
		stateLog2.setRefid(sub.getId());
		stateLog2.setTarget(sub.getState());
		stateLog2.setType(StateLog.TYPE_SUBMIT);
		stateLogDao.create(stateLog2);
		
		
		
		//统一修改状态，记录状态转换日志
		changeState(submitId, Submit.STATE_COMPLETEPAY);
		//将purchaseFlag变回0（未被回购）
		submitDao.purchase(lender.getId(), submitId, System.currentTimeMillis());
		
		
		//购买成功，修改购买用户的信用值与级别
		int grade=calculateGrade(lender.getGrade(),submit.getAmount().intValue(), lender.getId());
		int level=Lender.gradeToLevel(grade);
		lenderDao.changeGradeAndLevel(lender.getId(), grade, level);
		lender=lenderService.getCurrentUser();
		if(lender!=null)
		{
			lender.setGrade(grade);
			lender.setLevel(level);
		}
	}
	
	
	

	@Override
	public Map<String, Object> findPayedSubmitsByProduct(Integer productId, int offset,
			int recnum) {
		int count=submitDao.countByProductAndStateWithPaged(productId, Submit.STATE_COMPLETEPAY);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<Submit> submits=submitDao.findAllByProductAndStateWithPaged(productId, Submit.STATE_COMPLETEPAY, offset, recnum);
		for(Submit submit:submits)
		{
			String lenderName = lenderService.find(submit.getLenderId()).getName();
			lenderName = "***"+lenderName.substring(lenderName.length()-1);
			submit.setLenderName(lenderName);
		}
		return Pagination.buildResult(submits, count, offset, recnum);
	}

	@Override
	public Integer buyByAdmin(Integer productId, int num)
			throws InsufficientBalanceException, ProductSoldOutException,
			InsufficientProductException {
		//TODO 验证amount格式，例如：1w起之类的
		Lender lender=lenderService.getCurrentUser();
		lender=lenderService.find(lender.getId());
		LenderAccount account=lenderAccountDao.find(lender.getAccountId());
		BigDecimal amount=new BigDecimal(num);
		//判断当前账户余额是否足够购买
		if(amount.compareTo(account.getUsable())>0)
			throw new InsufficientBalanceException("您账户的余额不足，请先充值");
		Product product=productService.find(productId);
		checkNullObject(Product.class, product);
		try
		{
			product=orderService.applyFinancingProduct(productId, product.getGovermentorderId());
			if(product==null)
				throw new ProductSoldOutException("产品已售完");
			if(amount.compareTo(product.getExpectAmount().subtract(product.getRealAmount()))>0)
				throw new InsufficientProductException("产品余额不足");
			
			Submit submit=new Submit();
			submit.setAmount(amount);
			submit.setLenderId(lenderService.getCurrentUser().getId());
			submit.setProductId(productId);
			submit.setState(Submit.STATE_WAITFORPAY);
			submitDao.create(submit);
			productDao.buy(productId, amount);
//			Integer cashStreamId=accountService.freezeLenderAccount(lender.getAccountId(), amount, submit.getId(), null);
			product.setRealAmount(product.getRealAmount().add(amount));
			return submit.getId();
		}finally
		{
			orderService.releaseFinancingProduct(product);
		}
	
	}
}
