package gpps.service.impl;

import gpps.constant.Pagination;
import gpps.dao.IBorrowerAccountDao;
import gpps.dao.IBorrowerDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.ILenderDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.IProductSeriesDao;
import gpps.dao.IStateLogDao;
import gpps.dao.ISubmitDao;
import gpps.inner.service.IInnerPayBackService;
import gpps.inner.service.IInnerThirdPaySupportService;
import gpps.model.Borrower;
import gpps.model.BorrowerAccount;
import gpps.model.CashStream;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.StateLog;
import gpps.model.Submit;
import gpps.model.Task;
import gpps.service.CashStreamSum;
import gpps.service.IAccountService;
import gpps.service.IBorrowerService;
import gpps.service.IGovermentOrderService;
import gpps.service.IPayBackService;
import gpps.service.IProductService;
import gpps.service.ITaskService;
import gpps.service.PayBackToView;
import gpps.service.exception.CheckException;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.IllegalOperationException;
import gpps.service.exception.InsufficientBalanceException;
import gpps.service.exception.SMSException;
import gpps.service.exception.UnSupportRepayInAdvanceException;
import gpps.service.message.ILetterSendService;
import gpps.service.message.IMessageService;
import gpps.service.thirdpay.IAuditRepayService;
import gpps.service.thirdpay.ITransferApplyService;
import gpps.service.thirdpay.Transfer.LoanJson;
import gpps.tools.DateCalculateUtils;
import gpps.tools.SinglePayBack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class PayBackServiceImpl implements IPayBackService {
	@Autowired
	IPayBackDao payBackDao;
	@Autowired
	IInnerPayBackService innerPayBackService;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	IProductDao productDao;
	@Autowired
	IProductSeriesDao productSeriesDao;
	@Autowired
	IGovermentOrderService orderSerivce;
	@Autowired
	IStateLogDao stateLogDao;
	@Autowired
	IBorrowerService borrowerService;
	@Autowired
	IGovermentOrderDao govermentOrderDao;
	@Autowired
	IProductService productService;
	@Autowired
	IAccountService accountService;
	@Autowired
	ITaskService taskService;
	@Autowired
	IGovermentOrderService orderService;
	@Autowired
	IBorrowerDao borrowerDao;
	@Autowired
	IBorrowerAccountDao borrowerAccountDao;
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	IMessageService messageService;
	@Autowired
	ILetterSendService letterSendService;
	@Autowired
	ITransferApplyService transferApplyService;
	@Autowired
	IAuditRepayService auditRepayService;
	@Autowired
	IInnerThirdPaySupportService innerThirdPaySupportService;
	Logger log=Logger.getLogger(PayBackServiceImpl.class);
	@Override
	public void create(PayBack payback) {
		payBackDao.create(payback);
		StateLog stateLog=new StateLog();
		stateLog.setCreatetime(System.currentTimeMillis());
		stateLog.setRefid(payback.getId());
		stateLog.setTarget(payback.getState());
		stateLog.setType(stateLog.TYPE_PAYBACK);
		stateLogDao.create(stateLog);
	}


	@Override
	public PayBack find(Integer id) {
		PayBack payBack=payBackDao.find(id);
		return payBack;
	}
	
	public static final int ADVANCE_DAY = 3;
	@Override
	@Transactional
	public void applyRepayInAdvance(Integer payBackId, long repayDate) throws UnSupportRepayInAdvanceException{
		PayBack payBack=find(payBackId);
		if(payBack.getState()!=PayBack.STATE_WAITFORREPAY)
			throw new UnSupportRepayInAdvanceException("本次还款不处于待还款状态！");
		if(payBack.getType()!=PayBack.TYPE_LASTPAY)
			throw new UnSupportRepayInAdvanceException("只有最后一次还款允许提前");
		Product product=productDao.find(payBack.getProductId());
		ProductSeries productSeries=productSeriesDao.find(product.getProductseriesId());
		if(productSeries.getType()==ProductSeries.TYPE_AVERAGECAPITALPLUSINTEREST)
			throw new UnSupportRepayInAdvanceException("当前产品不支持提前还贷");
		
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(repayDate);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
		if(cal.getTimeInMillis()>payBack.getDeadline())
			throw new UnSupportRepayInAdvanceException("时间非法,申请还款时间比最迟还款时间还要晚！");
		
		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(System.currentTimeMillis());
		today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE), 0, 0, 0);
		
		//必须提前ADVANCE_DAY天申请提前还款
		if(cal.getTimeInMillis()<(today.getTimeInMillis()+24L*3600*1000*ADVANCE_DAY)){
			throw new UnSupportRepayInAdvanceException("时间非法,最少提前"+ADVANCE_DAY+"天申请提前还款！");
		}
		
		
		GovermentOrder order=orderSerivce.findGovermentOrderByProduct(payBack.getProductId());
		List<Product> products=order.getProducts();
		//验证先还完稳健型或平衡型
		for(Product pro:products)
		{
			if(pro.getId()==(int)(product.getId()))
					continue;
			ProductSeries proSeries=productSeriesDao.find(pro.getProductseriesId());
			if(proSeries.getType()==ProductSeries.TYPE_AVERAGECAPITALPLUSINTEREST&&(pro.getState()==Product.STATE_REPAYING||pro.getState()==Product.STATE_POSTPONE))
				throw new UnSupportRepayInAdvanceException("必须先还完稳健型产品，才允许该产品提前还贷");
			else if(proSeries.getType()==ProductSeries.TYPE_FIRSTINTERESTENDCAPITAL&&(pro.getState()==Product.STATE_REPAYING||pro.getState()==Product.STATE_POSTPONE))
				throw new UnSupportRepayInAdvanceException("必须先还完平衡型产品，才允许该产品提前还贷");
		}
		List<PayBack> payBacks=innerPayBackService.findAll(product.getId());
		long lastRepaytime=order.getIncomeStarttime();
		long nextRepaytime=product.getIncomeEndtime(); 
		for(PayBack pb:payBacks)
		{
			if(pb.getId()==(int)(payBack.getId()))
				continue;
			if(pb.getState()==PayBack.STATE_FINISHREPAY||pb.getState()==PayBack.STATE_REPAYING)
			{
				if(pb.getDeadline()>lastRepaytime)
					lastRepaytime=pb.getDeadline();
				continue;
			}
			if(pb.getState()==PayBack.STATE_WAITFORREPAY){
				if(pb.getDeadline()<nextRepaytime){
					nextRepaytime=pb.getDeadline();
				}
			}
			if(pb.getState()==PayBack.STATE_DELAY|| pb.getState()==PayBack.STATE_WAITFORCHECK || pb.getDeadline()<=cal.getTimeInMillis())
				throw new UnSupportRepayInAdvanceException("请先将前面的还款还清才允许提前还贷");
		}
		
		if(cal.getTimeInMillis()>nextRepaytime || cal.getTimeInMillis()<lastRepaytime){
			throw new UnSupportRepayInAdvanceException("申请提前还款日期不能小于上次还款日期，也不能晚于下一次还款日期！");
		}
		
		
		for(PayBack pb:payBacks)
		{
			if(pb.getId()==(int)(payBack.getId()))
				continue;
			if(pb.getState()==PayBack.STATE_FINISHREPAY||pb.getState()==PayBack.STATE_REPAYING)
			{
				continue;
			}
			innerPayBackService.changeState(pb.getId(), PayBack.STATE_INVALID);
		}
		
		
		//重新计算最后还款
		Calendar lastCal=Calendar.getInstance();
		lastCal.setTimeInMillis(lastRepaytime);
		int days=DateCalculateUtils.getDays(lastCal, cal);
		if(days<0)
			days=0;
		payBack.setInterest(product.getRealAmount().multiply(product.getRate()).multiply(new BigDecimal(days)).divide(new BigDecimal(365),2,BigDecimal.ROUND_UP));
		payBack.setDeadline(cal.getTimeInMillis());
		payBack.setChiefAmount(product.getRealAmount());
		payBackDao.update(payBack);
		//修改本次还款的状态为“申请提前还款”,以及后续的日志和状态转换记录
		innerPayBackService.changeState(payBackId, PayBack.STATE_APPLYREPAYINADVANCE);
	}
	
	@Override
	public void auditRepayInAdvance(Integer payBackId) throws CheckException{
		PayBack payBack=find(payBackId);
		if(payBack.getState()!=PayBack.STATE_APPLYREPAYINADVANCE)
		{
			throw new CheckException("不处于申请提前还款的状态！");
		}
		innerPayBackService.changeState(payBackId, PayBack.STATE_WAITFORREPAY);
	}
	
	
	

	@Transactional
	public void applyRepayInAdvance(Integer payBackId) throws UnSupportRepayInAdvanceException {
		PayBack payBack=find(payBackId);
		if(payBack.getState()!=PayBack.STATE_WAITFORREPAY)
			return;
		if(payBack.getType()!=PayBack.TYPE_LASTPAY)
			throw new UnSupportRepayInAdvanceException("只有最后一次还款允许提前");
		Product product=productDao.find(payBack.getProductId());
		ProductSeries productSeries=productSeriesDao.find(product.getProductseriesId());
		if(productSeries.getType()==ProductSeries.TYPE_AVERAGECAPITALPLUSINTEREST)
			throw new UnSupportRepayInAdvanceException("当前产品不支持提前还贷");
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
		if(cal.getTimeInMillis()>payBack.getDeadline())
			throw new UnSupportRepayInAdvanceException("时间非法");
		
		GovermentOrder order=orderSerivce.findGovermentOrderByProduct(payBack.getProductId());
		List<Product> products=order.getProducts();
		//验证先还完稳健型或平衡型
		for(Product pro:products)
		{
			if(pro.getId()==(int)(product.getId()))
					continue;
			ProductSeries proSeries=productSeriesDao.find(pro.getProductseriesId());
			if(proSeries.getType()==ProductSeries.TYPE_AVERAGECAPITALPLUSINTEREST&&(pro.getState()==Product.STATE_REPAYING||pro.getState()==Product.STATE_POSTPONE))
				throw new UnSupportRepayInAdvanceException("必须先还完稳健型产品，才允许该产品提前还贷");
			else if(proSeries.getType()==ProductSeries.TYPE_FIRSTINTERESTENDCAPITAL&&(pro.getState()==Product.STATE_REPAYING||pro.getState()==Product.STATE_POSTPONE))
				throw new UnSupportRepayInAdvanceException("必须先还完平衡型产品，才允许该产品提前还贷");
		}
		List<PayBack> payBacks=innerPayBackService.findAll(product.getId());
		for(PayBack pb:payBacks)
		{
			if(pb.getId()==(int)(payBack.getId()))
				continue;
			if(pb.getState()==PayBack.STATE_FINISHREPAY||pb.getState()==PayBack.STATE_REPAYING)
				continue;
			if(pb.getState()==PayBack.STATE_DELAY||pb.getDeadline()<=cal.getTimeInMillis())
				throw new UnSupportRepayInAdvanceException("请先将前面的还款还清才允许提前还贷");
		}
		long lastRepaytime=order.getIncomeStarttime();
		for(PayBack pb:payBacks)
		{
			if(pb.getId()==(int)(payBack.getId()))
				continue;
			if(pb.getState()==PayBack.STATE_FINISHREPAY||pb.getState()==PayBack.STATE_REPAYING)
			{
				if(pb.getDeadline()>lastRepaytime)
					lastRepaytime=pb.getDeadline();
				continue;
			}
			innerPayBackService.changeState(pb.getId(), PayBack.STATE_INVALID);
		}
		//重新计算最后还款
		Calendar lastCal=Calendar.getInstance();
		lastCal.setTimeInMillis(lastRepaytime);
		int days=DateCalculateUtils.getDays(lastCal, cal);
		if(days<0)
			days=0;
		payBack.setInterest(product.getRealAmount().multiply(product.getRate()).multiply(new BigDecimal(days)).divide(new BigDecimal(365),2,BigDecimal.ROUND_UP));
		payBack.setDeadline(cal.getTimeInMillis());
		payBack.setChiefAmount(product.getRealAmount());
		payBackDao.update(payBack);
	}

	@Override
	@Transactional
	public void delay(Integer payBackId) {
		PayBack payBack=payBackDao.find(payBackId);
		innerPayBackService.changeState(payBackId, PayBack.STATE_DELAY);
		productDao.changeState(payBack.getProductId(), Product.STATE_POSTPONE,System.currentTimeMillis());
	}

	

//	private int getDays(Calendar starttime,Calendar endtime)
//	{
//		if(starttime.get(Calendar.YEAR)>endtime.get(Calendar.YEAR))
//			return 0;
//		if(starttime.get(Calendar.YEAR)==endtime.get(Calendar.YEAR))
//			return endtime.get(Calendar.DAY_OF_YEAR)-starttime.get(Calendar.DAY_OF_YEAR);
//		else {
//			return starttime.getActualMaximum(Calendar.DAY_OF_YEAR)-starttime.get(Calendar.DAY_OF_YEAR)+endtime.get(Calendar.DAY_OF_YEAR);
//		}
//	}

	@Override
	public List<PayBack> generatePayBacks(Integer productId, int amount) {
		Product product=productDao.find(productId);
		BigDecimal real = product.getRealAmount();
		
		if(product.getState()==Product.STATE_FINANCING || product.getState()==Product.STATE_QUITFINANCING){
			real = product.getExpectAmount();
		}
		
		BigDecimal samount = new BigDecimal(amount);
		
		if(product.getState()==Product.STATE_QUITFINANCING){
			samount = new BigDecimal(0);
		}
		
		List<PayBack> payBacks=payBackDao.findAllByProduct(productId);
		if(payBacks==null||payBacks.size()==0)
			return new ArrayList<PayBack>(0);
		for(PayBack payBack : payBacks){
			payBack.setChiefAmount(payBack.getChiefAmount().multiply(samount).divide(real,2,BigDecimal.ROUND_UP));
			payBack.setInterest(payBack.getInterest().multiply(samount).divide(real,2,BigDecimal.ROUND_UP));
			
		}
		return payBacks;
	}
	
	@Override
	public List<PayBack> generatePayBacksBySubmit(Integer submitId){
		Submit submit = submitDao.find(submitId);
		List<PayBack> pbs = generatePayBacks(submit.getProductId(), submit.getAmount().intValue());
		int i = 0;
		BigDecimal total = new BigDecimal(0);
		for(PayBack pb:pbs){
			i++;
			if(pb.getState()==PayBack.STATE_FINISHREPAY){
				List<CashStream> pbcs = cashStreamDao.findRepayCashStream(submitId, pb.getId());
				if(pbcs!=null && !pbcs.isEmpty()){
				CashStream pbc = pbcs.get(0);
				pb.setChiefAmount(pbc.getChiefamount());
				pb.setInterest(pbc.getInterest());
				}
			}
			//最后一笔不加
			if(i<pbs.size()){
				total = total.add(pb.getChiefAmount());
			}
		}
		pbs.get(pbs.size()-1).setChiefAmount(submit.getAmount().subtract(total));
		return pbs;
	}

	@Override
	public Map<String, Object> findBorrowerPayBacks(int state, long starttime,
			long endtime,int offset,int recnum) {
		List<Integer> states = null;
		if(state!=-1)
		{
			states=new ArrayList<Integer>();
			states.add(state);
		}
		if(state==PayBack.STATE_FINISHREPAY)
			states.add(PayBack.STATE_REPAYING);
		Borrower borrower=borrowerService.getCurrentUser();
		int count=payBackDao.countByBorrowerAndState(borrower.getAccountId(),states, starttime, endtime);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<PayBack> payBacks=payBackDao.findByBorrowerAndState(borrower.getAccountId(),states, starttime, endtime, offset, recnum);
		for(PayBack payBack:payBacks)
		{
			Product product=productDao.find(payBack.getProductId());
			payBack.setProduct(product);
			product.setGovermentOrder(govermentOrderDao.find(product.getGovermentorderId()));
			product.setProductSeries(productSeriesDao.find(product.getProductseriesId()));
		}
		return Pagination.buildResult(payBacks, count, offset, recnum);
	}

	@Override
	public List<PayBack> findBorrowerCanBeRepayedPayBacks() {
		Borrower borrower=borrowerService.getCurrentUser();
		List<PayBack> payBacks=payBackDao.findBorrowerWaitForRepayed(borrower.getAccountId());
		if(payBacks==null||payBacks.size()==0)
			return new ArrayList<PayBack>(0);
		List<PayBack> canBeRepayedPayBacks=new ArrayList<PayBack>();
		for(PayBack payBack:payBacks)
		{
			if(canRepay(payBack.getId()))
			{
				Product product=productDao.find(payBack.getProductId());
				payBack.setProduct(product);
				product.setGovermentOrder(govermentOrderDao.find(product.getGovermentorderId()));
				product.setProductSeries(productSeriesDao.find(product.getProductseriesId()));
				canBeRepayedPayBacks.add(payBack);
			}
		}
		return canBeRepayedPayBacks;
	}
	@Override
	public List<PayBack> findBorrowerCanBeRepayedInAdvancePayBacks() {
		Borrower borrower=borrowerService.getCurrentUser();
		List<PayBack> payBacks=payBackDao.findBorrowerWaitForRepayed(borrower.getAccountId());
		if(payBacks==null||payBacks.size()==0)
			return new ArrayList<PayBack>(0);
		List<PayBack> canBeRepayedInAdvancePayBacks=new ArrayList<PayBack>();
		for(PayBack payBack:payBacks)
		{
			if(canRepayInAdvance(payBack.getId()))
			{
				Product product=productDao.find(payBack.getProductId());
				payBack.setProduct(product);
				product.setGovermentOrder(govermentOrderDao.find(product.getGovermentorderId()));
				product.setProductSeries(productSeriesDao.find(product.getProductseriesId()));
				canBeRepayedInAdvancePayBacks.add(payBack);
			}
		}
		return canBeRepayedInAdvancePayBacks;
	}
	@Override
	public boolean canRepay(Integer payBackId) {
		PayBack payBack=find(payBackId);
		if(payBack.getState()!=PayBack.STATE_WAITFORREPAY)
			return false;
		//本产品是否为最后一次还款
		List<PayBack> payBacks=innerPayBackService.findAll(payBack.getProductId());
		for(PayBack pb:payBacks)
		{
			if(pb.getId()==(int)(payBack.getId()))
				continue;
			if(pb.getState()==PayBack.STATE_FINISHREPAY||pb.getState()==PayBack.STATE_REPAYING)
				continue;
			if(pb.getState()==PayBack.STATE_DELAY||pb.getDeadline()<payBack.getDeadline())
				return false;
		}
		Product product=productDao.find(payBack.getProductId());
		ProductSeries productSeries=productSeriesDao.find(product.getProductseriesId());
		if(productSeries.getType()==ProductSeries.TYPE_AVERAGECAPITALPLUSINTEREST)
			return true;
		List<Product> products=productDao.findByGovermentOrder(product.getGovermentorderId());
		for(Product pro:products)
		{
			if((int)(pro.getId())==(int)(product.getId()))
				continue;
			ProductSeries proSeries=productSeriesDao.find(pro.getProductseriesId());
			if(proSeries.getType()>=productSeries.getType())
				continue;
			payBacks=innerPayBackService.findAll(pro.getId());
			for(PayBack pb:payBacks)
			{
				if(pb.getState()==PayBack.STATE_FINISHREPAY||pb.getState()==PayBack.STATE_REPAYING)
					continue;
				if(pb.getState()==PayBack.STATE_DELAY||pb.getDeadline()<=payBack.getDeadline())
					return false;
			}
		}
		return true;
	}

	@Override
	public boolean canRepayInAdvance(Integer payBackId) {
		PayBack payBack=find(payBackId);
		if(payBack.getState()!=PayBack.STATE_WAITFORREPAY)
			return false;
		if(payBack.getType()!=PayBack.TYPE_LASTPAY)
			return false;
		if(payBack.getDeadline()<=System.currentTimeMillis())
			return false;
		Product product=productDao.find(payBack.getProductId());
		ProductSeries productSeries=productSeriesDao.find(product.getProductseriesId());
		if(productSeries.getType()==ProductSeries.TYPE_AVERAGECAPITALPLUSINTEREST)
			return false;
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
		if(cal.getTimeInMillis()>payBack.getDeadline())
			return false;
		GovermentOrder order=orderSerivce.findGovermentOrderByProduct(payBack.getProductId());
		List<Product> products=order.getProducts();
		//验证先还完稳健型或平衡型
		for(Product pro:products)
		{
			if(pro.getId()==(int)(product.getId()))
					continue;
			ProductSeries proSeries=productSeriesDao.find(pro.getProductseriesId());
			if(proSeries.getType()==ProductSeries.TYPE_AVERAGECAPITALPLUSINTEREST&&(pro.getState()==Product.STATE_REPAYING||pro.getState()==Product.STATE_POSTPONE))
				return false;
			else if(proSeries.getType()==ProductSeries.TYPE_FIRSTINTERESTENDCAPITAL&&(pro.getState()==Product.STATE_REPAYING||pro.getState()==Product.STATE_POSTPONE))
				return false;
		}
		List<PayBack> payBacks=innerPayBackService.findAll(product.getId());
		for(PayBack pb:payBacks)
		{
			if(pb.getId()==(int)(payBack.getId()))
				continue;
			if(pb.getState()==PayBack.STATE_FINISHREPAY||pb.getState()==PayBack.STATE_REPAYING)
				continue;
			if(pb.getState()==PayBack.STATE_DELAY||pb.getDeadline()<=cal.getTimeInMillis())
				return false;
		}
		return true;
	}

	@Override
	public List<PayBack> findBorrowerWaitForRepayed() {
		Borrower borrower=borrowerService.getCurrentUser();
		List<PayBack> payBacks=payBackDao.findBorrowerWaitForRepayed(borrower.getAccountId());
		if(payBacks==null||payBacks.size()==0)
			return new ArrayList<PayBack>(0);
		for(PayBack payBack:payBacks)
		{
			Product product=productDao.find(payBack.getProductId());
			payBack.setProduct(product);
			product.setGovermentOrder(govermentOrderDao.find(product.getGovermentorderId()));
			product.setProductSeries(productSeriesDao.find(product.getProductseriesId()));
		}
		return payBacks;
	}

	@Override
	public void repay(Integer payBackId) throws IllegalStateException,IllegalOperationException, InsufficientBalanceException, IllegalConvertException, CheckException {
		//确定融资方已授权平台执行还款
		Borrower borrower=borrowerService.getCurrentUser();
		if((borrower.getAuthorizeTypeOpen()&Borrower.AUTHORIZETYPEOPEN_RECHARGE)==0)
			throw new IllegalOperationException("请先授权还款权限，然后再执行还款");
		
		
		PayBack payBack = find(payBackId);
		if(payBack.getState()!=PayBack.STATE_WAITFORREPAY)
		{
		if(payBack.getState()==PayBack.STATE_WAITFORCHECK)
		{
			throw new IllegalOperationException("重复操作，本次还款已处于待审核状态！");
		}
		else if(payBack.getState()==PayBack.STATE_REPAYING)
		{
			throw new IllegalOperationException("重复操作，本次还款正在还款中！");
		}
		else if(payBack.getState()==PayBack.STATE_FINISHREPAY)
		{
			throw new IllegalOperationException("重复操作，本次还款已经执行完毕！");
		}else{
			throw new IllegalOperationException("重复操作，本次还款状态有问题！");
		}
		}
		
		//确定当前产品处于还款中状态
		Product currentProduct = productService.find(payBack.getProductId());
		if (currentProduct.getState() != Product.STATE_REPAYING) 
			throw new IllegalStateException("该产品尚未进入还款阶段");
		
		BorrowerAccount baccount = borrowerAccountDao.find(borrower.getAccountId());
		if(payBack.getChiefAmount().add(payBack.getInterest()).compareTo(baccount.getUsable())>0){
			throw new IllegalStateException("账户余额不足！");
		}
		
		// 验证还款顺序
		innerPayBackService.validatePayBackSequence(payBackId);
		
		List<SinglePayBack> spbs = null;
		
		//计算还款详细列表
		spbs = innerPayBackService.calculatePayBacks(payBackId);
		
		//校验还款详情
		innerPayBackService.justCheckOutPayBackBySPB(spbs, payBackId, "申请还款");
		
		for(SinglePayBack spb : spbs){
			List<CashStream> cs = cashStreamDao.findRepayCashStreamByAction(spb.getSubmitId(), payBackId, CashStream.ACTION_FREEZE);
			if(cs.size()>0){
				//已经有相关的冻结现金流了
				continue;
			}
			
			//首先创建还款冻结现金流，现金流状态为init
			Integer cashStreamId = accountService.freezeBorrowerAccount(payBack.getBorrowerAccountId(), spb.getChief(), spb.getInterest(), spb.getSubmitId(), payBackId, "还款冻结");
			
			//然后修改账户金额，执行账户冻结，并将现金流状态变为success,此操作为@Transactional容器托管事务处理
			accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
		}
		
		//最后将payback的状态修改为"待审核"
		innerPayBackService.changeState(payBack.getId(), PayBack.STATE_WAITFORCHECK);
		
		log.debug("申请还款：borroweraccountid="+borrower.getAccountId()+" amount=" + payBack.getChiefAmount().add(payBack.getInterest()) + ",共涉及冻结了"+spbs.size()+"笔还款");
	}

	@Override
	public void check(Integer payBackId) throws IllegalConvertException, IllegalOperationException, CheckException{
		PayBack payback=find(payBackId);
		if(payback==null)
		{
			throw new CheckException("无效的还款！");
		}
		if(payback.getState()!=PayBack.STATE_WAITFORCHECK){
			throw new CheckException("还款状态无效，不处于待审核状态！");
		}
		if(payback.getCheckResult()!=PayBack.CHECK_SUCCESS)
		{
			throw new IllegalOperationException("请先验证成功再审核");
		}
		
		List<CashStream> css = cashStreamDao.findByRepayAndActionAndState(payBackId, CashStream.ACTION_FREEZE, CashStream.STATE_SUCCESS);
		
		//根据融资方申请还款时创建的冻结现金流来校验本次还款
		innerPayBackService.justCheckOutPayBackByCS(css, payBackId, "正式还款审核");
		
		List<String> loans=new ArrayList<String>();
		for(CashStream cs : css){
			loans.add(cs.getLoanNo());
		}
		
		//还款转账审核
		try{
			auditRepayService.auditRepay(loans, 1);
		}catch(Exception e){
			throw new CheckException(e.getMessage());
		}
	}
	
	
	@Transactional
	public void checkOld(Integer payBackId) throws IllegalConvertException, IllegalOperationException {
		PayBack payback=find(payBackId);
		if(payback==null||payback.getState()!=PayBack.STATE_WAITFORCHECK)
			return;
		if(payback.getCheckResult()!=PayBack.CHECK_SUCCESS)
			throw new IllegalOperationException("请先验证成功再审核");
		// 增加还款任务
		innerPayBackService.changeState(payback.getId(), PayBack.STATE_REPAYING);
		Task task = new Task();
		task.setCreateTime(System.currentTimeMillis());
		task.setPayBackId(payback.getId());
		task.setProductId(payback.getProductId());
		task.setState(Task.STATE_INIT);
		task.setType(Task.TYPE_REPAY);
		
		Product product = productService.find(payback.getProductId());
		GovermentOrder order = orderSerivce.findGovermentOrderByProduct(payback.getProductId());
		Borrower borrower = borrowerDao.findByAccountID(payback.getBorrowerAccountId());
		
		taskService.submit(task);
		
		// 添加进任务以后，任务已经执行成功，就修改payback的状态为成功
		innerPayBackService.changeState(payback.getId(), PayBack.STATE_FINISHREPAY);
		
		
//		accountService.unfreezeBorrowerAccount(payback.getBorrowerAccountId(),payback.getChiefAmount().add(payback.getInterest()), 10, payback.getId(), "还款解冻");
		if (payback.getType() == PayBack.TYPE_LASTPAY) {
			// TODO 金额正确，设置产品状态为还款完毕
			productService.finishRepay(payback.getProductId());
			
			Map<String, String> param = new HashMap<String, String>();
			param.put(IMessageService.PARAM_ORDER_NAME, order.getTitle());
			param.put(IMessageService.PARAM_PRODUCT_SERIES_NAME, product.getProductSeries().getTitle());
			param.put(IMessageService.PARAM_AMOUNT, payback.getChiefAmount().add(payback.getInterest()).toString());
			param.put(ILetterSendService.PARAM_TITLE, "产品最后一笔还款完成");
			try{
			letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_LASTPAYBACKSUCCESS, ILetterSendService.USERTYPE_BORROWER, borrower.getId(), param);
			messageService.sendMessage(IMessageService.MESSAGE_TYPE_LASTPAYBACKSUCCESS, IMessageService.USERTYPE_BORROWER, borrower.getId(), param);
			}catch(SMSException e){
				log.error(e.getMessage());
			}
			
			List<Product> allProducts = productService.findByGovermentOrder(product.getGovermentorderId());
			boolean isAllRepay = true;
			for (Product pro : allProducts) {
				if (pro.getState() != Product.STATE_FINISHREPAY) {
					isAllRepay = false;
					break;
				}
			}
			if (isAllRepay)
			{
				orderService.closeFinancing(product.getGovermentorderId());
			}
		}else{
			Map<String, String> param = new HashMap<String, String>();
			param.put(IMessageService.PARAM_ORDER_NAME, order.getTitle());
			param.put(IMessageService.PARAM_PRODUCT_SERIES_NAME, product.getProductSeries().getTitle());
			param.put(IMessageService.PARAM_AMOUNT, payback.getChiefAmount().add(payback.getInterest()).toString());
			param.put(ILetterSendService.PARAM_TITLE, "还款完成");
			try{
			letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_PAYBACKSUCCESS, ILetterSendService.USERTYPE_BORROWER, borrower.getId(), param);
			messageService.sendMessage(IMessageService.MESSAGE_TYPE_PAYBACKSUCCESS, IMessageService.USERTYPE_BORROWER, borrower.getId(), param);
			}catch(SMSException e){
				log.error(e.getMessage());
			}
		}
	}

	@Override
	public List<PayBack> findApplyToRepayInAdvance(){
		List<PayBack> paybacks = payBackDao.findByProductsAndState(null, PayBack.STATE_APPLYREPAYINADVANCE);
		for(PayBack payBack:paybacks)
		{
			Product product=productDao.find(payBack.getProductId());
			
			payBack.setProduct(product);
			product.setGovermentOrder(govermentOrderDao.find(product.getGovermentorderId()));
			product.setProductSeries(productSeriesDao.find(product.getProductseriesId()));
		}
		return paybacks;
	}
	
	@Override
	public List<PayBack> findWaitforCheckPayBacks() {
		List<PayBack> paybacks = payBackDao.findByProductsAndState(null, PayBack.STATE_WAITFORCHECK);
		for(PayBack payBack:paybacks)
		{
			Product product=productDao.find(payBack.getProductId());
			
//			payBack.setChiefAmount(payBack.getChiefAmount().multiply(product.getRealAmount()).divide(PayBack.BASELINE,2,BigDecimal.ROUND_UP));
//			payBack.setInterest(payBack.getInterest().multiply(product.getRealAmount()).divide(PayBack.BASELINE,2,BigDecimal.ROUND_UP));
			
			payBack.setProduct(product);
			product.setGovermentOrder(govermentOrderDao.find(product.getGovermentorderId()));
			product.setProductSeries(productSeriesDao.find(product.getProductseriesId()));
		}
		return paybacks;
	}
	
	@Override
	public List<SinglePayBack> checkoutPayBack(Integer payBackId) throws CheckException {
		PayBack payback = payBackDao.find(payBackId);
		
		if(payback==null)
		{
			throw new CheckException("无效的还款！");
		}
		if(payback.getState()!=PayBack.STATE_WAITFORCHECK){
			throw new CheckException("还款状态无效，不处于待审核状态！");
		}
		if(payback.getCheckResult()!=PayBack.CHECK_NOT)
		{
			throw new CheckException("本次还款已经审核过了");
		}
		
		Product product = productDao.find(payback.getProductId());
		Borrower borrower = borrowerDao.findByAccountID(payback.getBorrowerAccountId());
		List<CashStream> css = cashStreamDao.findByRepayAndActionAndState(payBackId, CashStream.ACTION_FREEZE, CashStream.STATE_SUCCESS);
		
		List<SinglePayBack> spbs = new ArrayList<SinglePayBack>();
		
		//根据融资方申请还款时创建的冻结现金流来校验本次还款
		innerPayBackService.justCheckOutPayBackByCS(css, payBackId, "预审核");
		
		List<LoanJson> loanJsons=new ArrayList<LoanJson>();
		for(CashStream cs : css){
			//已成功通知第三方执行冻结，并保存了第三方返回的loanNo
			if(cs.getState()==CashStream.STATE_SUCCESS&&(cs.getLoanNo()!=null&&!"".equals(cs))){
				continue;
			}
			
			String toMoneyMoreMore = null;
			Submit submit = submitDao.find(cs.getSubmitId());
			if(submit==null){
				toMoneyMoreMore = innerThirdPaySupportService.getPlatformMoneymoremore();
			}else{
				Lender lender = lenderDao.find(submit.getLenderId());
				if(lender==null){
					throw new CheckException("投资无法找到投资人！");
				}
				toMoneyMoreMore = lender.getThirdPartyAccount();
			}
			
				LoanJson loadJson=new LoanJson();
				loadJson.setLoanOutMoneymoremore(borrower.getThirdPartyAccount());
				loadJson.setLoanInMoneymoremore(toMoneyMoreMore);
				loadJson.setOrderNo(String.valueOf(cs.getId()));
				loadJson.setBatchNo(String.valueOf(product.getId()));
				loadJson.setAmount(cs.getChiefamount().add(cs.getInterest()).negate().toString());
				loanJsons.add(loadJson);
		}
		
		//还款转账申请
		try{
			transferApplyService.repayApply(loanJsons, payback);
		}catch(Exception e){
			throw new CheckException(e.getMessage());
		}
		
		for(CashStream cs : css){
			if(cs.getSubmitId()==null){	//如果submitId为空，说明是转账给平台账户的“存零”操作
				SinglePayBack spb = new SinglePayBack();
				spb.setChief(cs.getChiefamount());
				spb.setFromAccountId(cs.getBorrowerAccountId());
				spb.setFromMoneyMoreMore(borrower.getThirdPartyAccount());
				spb.setFromname(borrower.getCompanyName());
				spb.setInterest(cs.getInterest());
				spb.setSubmitAmount(new BigDecimal(0));
				spb.setSubmitId(null);
				spb.setToAccountId(null);
				spb.setToMoneyMoreMore(innerThirdPaySupportService.getPlatformMoneymoremore());
				spb.setToname("政采贷平台");
				spbs.add(spb);
			}else{
			
			Submit submit = submitDao.find(cs.getSubmitId());
			
			Lender lender = lenderDao.find(submit.getLenderId());
			
			SinglePayBack spb = new SinglePayBack();
			spb.setChief(cs.getChiefamount());
			spb.setFromAccountId(cs.getBorrowerAccountId());
			spb.setFromMoneyMoreMore(borrower.getThirdPartyAccount());
			spb.setFromname(borrower.getCompanyName());
			spb.setInterest(cs.getInterest());
			spb.setSubmitAmount(submit.getAmount());
			spb.setSubmitId(submit.getId());
			spb.setToAccountId(lender.getAccountId());
			spb.setToMoneyMoreMore(lender.getThirdPartyAccount());
			spb.setToname(lender.getName());
			spbs.add(spb);
			}
		}
		
		return spbs;
	}
	
	public List<SinglePayBack> checkoutPayBackOld(Integer payBackId) throws CheckException {
		PayBack payback = payBackDao.find(payBackId);
		List<CashStream> css = cashStreamDao.findByRepayAndActionAndState(payBackId, CashStream.ACTION_FREEZE, CashStream.STATE_SUCCESS);
		
		List<SinglePayBack> spbs = new ArrayList<SinglePayBack>();
		
		//根据融资方申请还款时创建的冻结现金流来校验本次还款
		innerPayBackService.justCheckOutPayBackByCS(css, payBackId, "预审核");
		
		
		Task task = new Task();
		task.setCreateTime(System.currentTimeMillis());
		task.setPayBackId(payBackId);
		task.setProductId(payback.getProductId());
		task.setState(Task.STATE_INIT);
		task.setType(Task.TYPE_CHECK_REPAY);   //审核还款也需要扔到任务中处理
		
		payBackDao.changeCheckResult(payBackId, PayBack.CHECK_SUCCESS);
		
		taskService.submit(task);
		
		Borrower borrower = borrowerDao.findByAccountID(payback.getBorrowerAccountId());
		
		for(CashStream cs : css){
			
			if(cs.getSubmitId()==null){
				SinglePayBack spb = new SinglePayBack();
				spb.setChief(cs.getChiefamount());
				spb.setFromAccountId(cs.getBorrowerAccountId());
				spb.setFromMoneyMoreMore(borrower.getThirdPartyAccount());
				spb.setFromname(borrower.getCompanyName());
				spb.setInterest(cs.getInterest());
				spb.setSubmitAmount(new BigDecimal(0));
				spb.setSubmitId(null);
				spb.setToAccountId(null);
				spb.setToMoneyMoreMore(innerThirdPaySupportService.getPlatformMoneymoremore());
				spb.setToname("政采贷平台");
				spbs.add(spb);
			}else{
			
			Submit submit = submitDao.find(cs.getSubmitId());
			
			Lender lender = lenderDao.find(submit.getLenderId());
			
			SinglePayBack spb = new SinglePayBack();
			spb.setChief(cs.getChiefamount());
			spb.setFromAccountId(cs.getBorrowerAccountId());
			spb.setFromMoneyMoreMore(borrower.getThirdPartyAccount());
			spb.setFromname(borrower.getCompanyName());
			spb.setInterest(cs.getInterest());
			spb.setSubmitAmount(submit.getAmount());
			spb.setSubmitId(submit.getId());
			spb.setToAccountId(lender.getAccountId());
			spb.setToMoneyMoreMore(lender.getThirdPartyAccount());
			spb.setToname(lender.getName());
			spbs.add(spb);
			}
		}
		
		return spbs;
	}
	
	public List<PayBackToView> getWaitingForPayBacksByLeftDays(int leftDays) throws Exception{
		long n = System.currentTimeMillis();
		long starttime = 0;
		long endtime = 0;
		if(leftDays<=0){
			starttime = -1;
		}else{
			starttime = n;
		}
		endtime = n + 24L*3600*1000*leftDays;
		
		List<PayBackToView> res = new ArrayList<PayBackToView>();
		
		List<PayBack> pbs = payBackDao.findByTimeAndState(starttime, endtime, PayBack.STATE_WAITFORREPAY);
		if(pbs==null){
			return res;
		}
		for(PayBack pb : pbs){
			Borrower borrower = borrowerDao.findByAccountID(pb.getBorrowerAccountId());
			GovermentOrder order = orderSerivce.findGovermentOrderByProduct(pb.getProductId());
			Product product = productService.find(pb.getProductId());
			PayBackToView pbtv = new PayBackToView();
			pbtv.setBorrowerid(borrower.getId());
			pbtv.setBorrowerName(borrower.getCompanyName());
			pbtv.setChief(pb.getChiefAmount());
//			pbtv.setContactor(borrowerService.findContactor(borrower.getId()));
			pbtv.setDeadline(pb.getDeadline());
			pbtv.setInterest(pb.getInterest());
			pbtv.setOrderTitle(order.getTitle());
			pbtv.setSeriesTitle(product.getProductSeries().getTitle());
			pbtv.setTel(borrower.getTel());
			pbtv.setId(pb.getId());
			pbtv.setProductid(pb.getProductId());
			res.add(pbtv);
		}
		
		return res;
	}
	
	@Override
	public List<PayBack> findAll(Integer productId) {
		return innerPayBackService.findAll(productId);
	}
}
