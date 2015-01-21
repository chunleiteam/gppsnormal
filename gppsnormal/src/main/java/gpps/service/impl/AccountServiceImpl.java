/**
 * 
 */
package gpps.service.impl;

import static gpps.tools.ObjectUtil.checkNullObject;
import gpps.constant.Pagination;
import gpps.dao.IBorrowerAccountDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.ILenderAccountDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.IStateLogDao;
import gpps.dao.ISubmitDao;
import gpps.model.Borrower;
import gpps.model.BorrowerAccount;
import gpps.model.CashStream;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.LenderAccount;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.StateLog;
import gpps.model.Submit;
import gpps.service.IAccountService;
import gpps.service.IBorrowerService;
import gpps.service.ILenderService;
import gpps.service.IProductService;
import gpps.service.PayBackDetail;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.IllegalOperationException;
import gpps.service.exception.InsufficientBalanceException;
import gpps.service.message.IMessageService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wangm
 *
 */
@Service
public class AccountServiceImpl implements IAccountService {
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	ILenderAccountDao lenderAccountDao;
	@Autowired
	IBorrowerAccountDao borrowerAccountDao;
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	IPayBackDao payBackDao;
	@Autowired
	IProductDao productDao;
	@Autowired
	ILenderService lenderService;
	@Autowired
	IBorrowerService borrowerService;
	@Autowired
	IGovermentOrderDao orderDao;
	@Autowired
	IProductService productService;
	@Autowired
	IStateLogDao stateLogDao;
	@Autowired
	IMessageService messageService;
	@Override
	public Integer rechargeLenderAccount(Integer lenderAccountId, BigDecimal amount, String description) {
		checkNullObject(LenderAccount.class, lenderAccountDao.find(lenderAccountId));
		CashStream cashStream = new CashStream();
		cashStream.setLenderAccountId(lenderAccountId);
		cashStream.setChiefamount(amount);
		cashStream.setDescription(description);
		cashStream.setAction(CashStream.ACTION_RECHARGE);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
		return cashStream.getId();
	}

	@Override
	public Integer rechargeBorrowerAccount(Integer borrowerAccountId, BigDecimal amount, String description) {
		checkNullObject(BorrowerAccount.class, borrowerAccountDao.find(borrowerAccountId));
		CashStream cashStream = new CashStream();
		cashStream.setBorrowerAccountId(borrowerAccountId);
		cashStream.setChiefamount(amount);
		cashStream.setDescription(description);
		cashStream.setAction(CashStream.ACTION_RECHARGE);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
		return cashStream.getId();
	}

	@Override
	@Transactional
	public Integer freezeLenderAccount(Integer lenderAccountId, BigDecimal amount, Integer submitId, String description) throws InsufficientBalanceException {
		LenderAccount lenderAccount=checkNullObject(LenderAccount.class, lenderAccountDao.find(lenderAccountId));
		checkNullObject(Submit.class, submitDao.find(submitId));
		if(amount.compareTo(lenderAccount.getUsable())>0)
			throw new InsufficientBalanceException();
		CashStream cashStream=new CashStream();
		cashStream.setLenderAccountId(lenderAccountId);
		cashStream.setChiefamount(amount.negate());
		cashStream.setSubmitId(submitId);
		cashStream.setDescription(description);
		cashStream.setAction(CashStream.ACTION_FREEZE);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
//		changeCashStreamState(cashStream.getId(), CashStream.STATE_SUCCESS);
		//TODO 调用第三方接口冻结,如不成功则事务回滚
		return cashStream.getId();
	}

	@Override
	public Integer freezeBorrowerAccount(Integer borrowerAccountId, BigDecimal amount, Integer paybackId, String description) throws InsufficientBalanceException {
		BorrowerAccount borrowerAccount=checkNullObject(BorrowerAccount.class, borrowerAccountDao.find(borrowerAccountId));
		if(amount.compareTo(borrowerAccount.getUsable())>0)
			throw new InsufficientBalanceException();
		CashStream cashStream=new CashStream();
		cashStream.setBorrowerAccountId(borrowerAccountId);
		cashStream.setChiefamount(amount.negate());
		cashStream.setDescription(description);
		cashStream.setAction(CashStream.ACTION_FREEZE);
		cashStream.setPaybackId(paybackId);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
//		changeCashStreamState(cashStream.getId(), CashStream.STATE_SUCCESS);
		//TODO 调用第三方接口冻结,如不成功则事务回滚
		return cashStream.getId();
	}
	@Override
	@Transactional
	public Integer unfreezeLenderAccount(Integer lenderAccountId, BigDecimal amount, Integer submitId, String description) throws IllegalConvertException {
		checkNullObject(LenderAccount.class, lenderAccountDao.find(lenderAccountId));
		CashStream cashStream=new CashStream();
		cashStream.setLenderAccountId(lenderAccountId);
		cashStream.setChiefamount(amount);
		cashStream.setSubmitId(submitId);
		cashStream.setDescription(description);
		cashStream.setAction(CashStream.ACTION_UNFREEZE);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
		changeCashStreamState(cashStream.getId(), CashStream.STATE_SUCCESS);
		return cashStream.getId();
	}
	@Override
	@Transactional
	public Integer unfreezeBorrowerAccount(Integer borrowerAccountId,
			BigDecimal amount, Integer paybackId, String description)
			throws IllegalConvertException {
		checkNullObject(BorrowerAccount.class, borrowerAccountDao.find(borrowerAccountId));
		CashStream cashStream=new CashStream();
		cashStream.setBorrowerAccountId(borrowerAccountId);
		cashStream.setChiefamount(amount);
		cashStream.setPaybackId(paybackId);
		cashStream.setAction(CashStream.ACTION_UNFREEZE);
		cashStream.setDescription(description);
		cashStream.setState(CashStream.STATE_SUCCESS);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
		return cashStream.getId();
	}
	@Override
	@Transactional
	public Integer pay(Integer lenderAccountId, Integer borrowerAccountId, BigDecimal chiefamount, Integer submitId, String description) throws IllegalConvertException {
		checkNullObject(LenderAccount.class, lenderAccountDao.find(lenderAccountId));
		checkNullObject(BorrowerAccount.class, borrowerAccountDao.find(borrowerAccountId));
		checkNullObject(Submit.class, submitDao.find(submitId));
		//支付先解冻
		CashStream cashStream=new CashStream();
		cashStream.setLenderAccountId(lenderAccountId);
		cashStream.setChiefamount(chiefamount);
		cashStream.setSubmitId(submitId);
		cashStream.setDescription("支付解冻");
		cashStream.setAction(CashStream.ACTION_UNFREEZE);
		cashStream.setState(CashStream.STATE_SUCCESS);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
		
		cashStream=new CashStream();
		cashStream.setLenderAccountId(lenderAccountId);
		cashStream.setBorrowerAccountId(borrowerAccountId);
		cashStream.setChiefamount(chiefamount.negate());
		cashStream.setSubmitId(submitId);
		cashStream.setDescription(description);
		cashStream.setAction(CashStream.ACTION_PAY);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
		changeCashStreamState(cashStream.getId(), CashStream.STATE_SUCCESS);
		//批量解冻，不需要第三方操作
		return cashStream.getId();
	}

	@Override
	@Transactional
	public Integer repay(Integer lenderAccountId, Integer borrowerAccountId, BigDecimal chiefamount, BigDecimal interest, Integer submitId, Integer paybackId, String description) throws IllegalConvertException {
		checkNullObject(LenderAccount.class, lenderAccountDao.find(lenderAccountId));
		checkNullObject(BorrowerAccount.class, borrowerAccountDao.find(borrowerAccountId));
		checkNullObject(Submit.class, submitDao.find(submitId));
		checkNullObject(PayBack.class, paybackId);
		CashStream cashStream=new CashStream();
		cashStream.setLenderAccountId(lenderAccountId);
		cashStream.setBorrowerAccountId(borrowerAccountId);
		cashStream.setChiefamount(chiefamount);
		cashStream.setInterest(interest);
		cashStream.setSubmitId(submitId);
		cashStream.setPaybackId(paybackId);
		cashStream.setDescription(description);
		cashStream.setAction(CashStream.ACTION_REPAY);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
//		changeCashStreamState(cashStream.getId(), CashStream.STATE_SUCCESS);
		//批量还款，不需要第三方操作
		return cashStream.getId();
	}

	@Override
	public Integer cashLenderAccount(Integer lenderAccountId, BigDecimal amount, String description) throws InsufficientBalanceException {
		LenderAccount account=checkNullObject(LenderAccount.class, lenderAccountDao.find(lenderAccountId));
		if(account.getUsable().compareTo(amount)<0)
			throw new InsufficientBalanceException();
		CashStream cashStream=new CashStream();
		cashStream.setLenderAccountId(lenderAccountId);
		cashStream.setChiefamount(amount.negate());
		cashStream.setDescription(description);
		cashStream.setAction(CashStream.ACTION_CASH);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
		
		return cashStream.getId();
	}

	@Override
	public Integer cashBorrowerAccount(Integer borrowerAccountId, BigDecimal amount, String description) throws InsufficientBalanceException {
		BorrowerAccount account=checkNullObject(BorrowerAccount.class, borrowerAccountDao.find(borrowerAccountId));
		if(account.getUsable().compareTo(amount)<0)
			throw new InsufficientBalanceException();
		CashStream cashStream=new CashStream();
		cashStream.setBorrowerAccountId(borrowerAccountId);
		cashStream.setChiefamount(amount.negate());
		cashStream.setDescription(description);
		cashStream.setAction(CashStream.ACTION_CASH);
		cashStreamDao.create(cashStream);
		recordStateLogWithCreate(cashStream);
		return cashStream.getId();
	}

	static int[][] validConverts = { 
		{ CashStream.STATE_INIT, CashStream.STATE_FAIL }, 
		{ CashStream.STATE_INIT, CashStream.STATE_SUCCESS }, 
		{ CashStream.STATE_FAIL, CashStream.STATE_SUCCESS }, 
		{ CashStream.STATE_INIT, CashStream.STATE_RETURN }, 
		{ CashStream.STATE_FAIL, CashStream.STATE_RETURN }, 
		{ CashStream.STATE_SUCCESS, CashStream.STATE_RETURN }};

	@Override
	@Transactional
	public void changeCashStreamState(Integer cashStreamId, int state) throws IllegalConvertException {
		CashStream cashStream = cashStreamDao.find(cashStreamId);
		checkNullObject(CashStream.class, cashStream);
		for (int[] validStateConvert : validConverts) {
			if (cashStream.getState() == validStateConvert[0] && state == validStateConvert[1]) {
				cashStreamDao.changeCashStreamState(cashStreamId, state);
				StateLog stateLog=new StateLog();
				stateLog.setCreatetime(System.currentTimeMillis());
				stateLog.setRefid(cashStreamId);
				stateLog.setSource(cashStream.getState());
				stateLog.setTarget(state);
				stateLog.setType(StateLog.TYPE_CASHSTREAM);
				stateLogDao.create(stateLog);
				if(state!=CashStream.STATE_SUCCESS)
					return;
				switch (cashStream.getAction()) {
				case CashStream.ACTION_RECHARGE:
					if(cashStream.getLenderAccountId()!=null&&cashStream.getBorrowerAccountId()==null)
						lenderAccountDao.recharge(cashStream.getLenderAccountId(), cashStream.getChiefamount());
					else if(cashStream.getBorrowerAccountId()!=null&&cashStream.getLenderAccountId()==null)
						borrowerAccountDao.recharge(cashStream.getBorrowerAccountId(), cashStream.getChiefamount());
					else
						throw new RuntimeException();
					break;
				case CashStream.ACTION_FREEZE:
					if(cashStream.getLenderAccountId()!=null&&cashStream.getBorrowerAccountId()==null)
						lenderAccountDao.freeze(cashStream.getLenderAccountId(), cashStream.getChiefamount().negate());
					else if(cashStream.getBorrowerAccountId()!=null&&cashStream.getLenderAccountId()==null)
						borrowerAccountDao.freeze(cashStream.getBorrowerAccountId(), cashStream.getChiefamount().negate());
					else
						throw new RuntimeException();
					break;
				case CashStream.ACTION_UNFREEZE:
					if(cashStream.getLenderAccountId()!=null&&cashStream.getBorrowerAccountId()==null)
						lenderAccountDao.unfreeze(cashStream.getLenderAccountId(), cashStream.getChiefamount());
					else if(cashStream.getBorrowerAccountId()!=null&&cashStream.getLenderAccountId()==null)
						borrowerAccountDao.unfreeze(cashStream.getBorrowerAccountId(), cashStream.getChiefamount());
					else 
						throw new RuntimeException();
					break;
				case CashStream.ACTION_PAY:
					lenderAccountDao.pay(cashStream.getLenderAccountId(), cashStream.getChiefamount().negate());//该利息为期望利息
					borrowerAccountDao.pay(cashStream.getBorrowerAccountId(), cashStream.getChiefamount().negate());
					break;
				case CashStream.ACTION_REPAY:
					//TODO 待确认期待的利益是否一定与实际利息一致
					lenderAccountDao.repay(cashStream.getLenderAccountId(), cashStream.getChiefamount(), cashStream.getInterest());
					borrowerAccountDao.repay(cashStream.getBorrowerAccountId(), cashStream.getChiefamount().add(cashStream.getInterest()));
					break;
				case CashStream.ACTION_CASH:
					if(cashStream.getLenderAccountId()!=null&&cashStream.getBorrowerAccountId()==null)
						lenderAccountDao.cash(cashStream.getLenderAccountId(), cashStream.getChiefamount().negate());
					else if(cashStream.getBorrowerAccountId()!=null&&cashStream.getLenderAccountId()==null)
						borrowerAccountDao.cash(cashStream.getBorrowerAccountId(), cashStream.getChiefamount().negate());
					else
						throw new RuntimeException();
					break;
				case CashStream.ACTION_STORECHANGE:
					borrowerAccountDao.repay(cashStream.getBorrowerAccountId(), cashStream.getChiefamount().add(cashStream.getInterest()));
					break;
				default:
					throw new UnsupportedOperationException();
				}
				return;
			}
		}
		throw new IllegalConvertException();
	}

	@Override
	public void checkThroughThirdPlatform(Integer cashStreamId) {
		
	}

	@Override
	public List<CashStream> findAllDirtyCashStream() {
		return cashStreamDao.findByState(CashStream.STATE_INIT);
	}

	@Override
	public Map<String, Object> findLenderCashStreamByActionAndState(int action,
			int state, int offset, int recnum) {
		Lender lender=lenderService.getCurrentUser();
		int count=cashStreamDao.countByActionAndState(lender.getAccountId(), null, action, state);
		return Pagination.buildResult(cashStreamDao.findByActionAndState(lender.getAccountId(), null, action, state, offset, recnum), count, offset, recnum);
	}

	@Override
	public Map<String, Object> findBorrowerCashStreamByActionAndState(
			int action, int state, int offset, int recnum) {
		Borrower borrower =borrowerService.getCurrentUser();
		int count=cashStreamDao.countByActionAndState(null, borrower.getAccountId(), action, state);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<CashStream> cashStreams=cashStreamDao.findByActionAndState(null, borrower.getAccountId(), action, state, offset, recnum);
		for(CashStream cashStream:cashStreams)
		{
			if(cashStream.getAction()==CashStream.ACTION_PAY||cashStream.getAction()==CashStream.ACTION_REPAY || cashStream.getAction()==CashStream.ACTION_STORECHANGE)
			{
				cashStream.setChiefamount(cashStream.getChiefamount().negate());
				cashStream.setInterest(cashStream.getInterest().negate());
			}
		}
		return Pagination.buildResult(cashStreams, count, offset, recnum);
	}

	@Override
	public Map<String, Object> findLenderRepayCashStream(int offset, int recnum) {
		Lender lender=lenderService.getCurrentUser();
		int count=cashStreamDao.countByActionAndState(lender.getAccountId(), null, CashStream.ACTION_REPAY, CashStream.STATE_SUCCESS);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<CashStream> cashStreams=cashStreamDao.findByActionAndState(lender.getAccountId(), null, CashStream.ACTION_REPAY, CashStream.STATE_SUCCESS, offset, recnum);
		for(CashStream cashStream:cashStreams)
		{
			cashStream.setSubmit(submitDao.find(cashStream.getSubmitId()));
			cashStream.getSubmit().setProduct(productService.find(cashStream.getSubmit().getProductId()));
			cashStream.getSubmit().getProduct().setGovermentOrder(orderDao.find(cashStream.getSubmit().getProduct().getGovermentorderId()));
		}
		return Pagination.buildResult(cashStreams, count, offset, recnum);
//		List<CashStream> cashStreams=new ArrayList<CashStream>();
//		int count=100;
//		for(int i=0;i<10;i++)
//		{
//			CashStream cashStream=new CashStream();
//			cashStream.setChiefamount(new BigDecimal(10000));
//			cashStream.setInterest(new BigDecimal(1000));
//			cashStreams.add(cashStream);
//			cashStream.setSubmit(new Submit());
//			cashStream.getSubmit().setProduct(new Product());
//			cashStream.getSubmit().getProduct().setId(123);
//			cashStream.getSubmit().getProduct().setGovermentOrder(new GovermentOrder());
//			cashStream.getSubmit().getProduct().getGovermentOrder().setTitle("淘宝借钱二期");
//		}
//		return Pagination.buildResult(cashStreams, count, offset, recnum);
	}

	@Override
	public List<PayBack> findLenderWaitforRepay() {
		Lender lender=lenderService.getCurrentUser();
		List<Integer> productStates=new ArrayList<Integer>();
		productStates.add(Product.STATE_REPAYING);
		List<Submit> submits=submitDao.findAllPayedByLenderAndProductStates(lender.getId(), productStates,0,Integer.MAX_VALUE);
		if(submits==null||submits.size()==0)
			return new ArrayList<PayBack>(0);
		Map<String, Integer> productIds=new HashMap<String, Integer>();
		for(Submit submit:submits)
		{
			productIds.put(submit.getProductId().toString(), submit.getProductId());
		}
		List<PayBack> payBacks=payBackDao.findByProductsAndState(new ArrayList<Integer>(productIds.values()), PayBack.STATE_WAITFORREPAY);
		List<PayBack> lenderPayBacks=new ArrayList<PayBack>();
		if(payBacks==null||payBacks.size()==0)
			return new ArrayList<PayBack>(0);
		for(PayBack payBack:payBacks)
		{
			Product product=productService.find(payBack.getProductId());
			List<Submit> list=findSubmits(submits, payBack.getProductId());
			if(list==null||list.size()==0)
				continue;
			for(Submit submit:list)
			{
				PayBack lenderPayBack=new PayBack();
				lenderPayBack.setBorrowerAccountId(payBack.getBorrowerAccountId());
				if(product.getState()==Product.STATE_UNPUBLISH||product.getState()==Product.STATE_FINANCING||product.getState()==Product.STATE_QUITFINANCING)
				{
					lenderPayBack.setChiefAmount(payBack.getChiefAmount().multiply(submit.getAmount()).divide(product.getExpectAmount(),2,BigDecimal.ROUND_DOWN));
					lenderPayBack.setInterest(payBack.getInterest().multiply(submit.getAmount()).divide(product.getExpectAmount(),2,BigDecimal.ROUND_DOWN));
				}
				else
				{
					lenderPayBack.setChiefAmount(payBack.getChiefAmount().multiply(submit.getAmount()).divide(product.getRealAmount(),2,BigDecimal.ROUND_DOWN));
					lenderPayBack.setInterest(payBack.getInterest().multiply(submit.getAmount()).divide(product.getRealAmount(),2,BigDecimal.ROUND_DOWN));
				}
				lenderPayBack.setDeadline(payBack.getDeadline());
				lenderPayBack.setId(payBack.getId());
				lenderPayBack.setProduct(product);
				lenderPayBack.getProduct().setGovermentOrder(orderDao.find(lenderPayBack.getProduct().getGovermentorderId()));
				lenderPayBack.setProductId(payBack.getProductId());
				lenderPayBack.setState(payBack.getState());
				lenderPayBack.setType(payBack.getType());
				lenderPayBacks.add(lenderPayBack);
			}
		}
		return lenderPayBacks;
//		List<PayBack> payBacks=new ArrayList<PayBack>();
//		for(int i=0;i<100;i++)
//		{
//			PayBack payBack=new PayBack();
//			payBack.setChiefAmount(new BigDecimal(10000));
//			payBack.setInterest(new BigDecimal(1000));
//			payBack.setDeadline(System.currentTimeMillis());
//			payBack.setProduct(new Product());
//			payBack.getProduct().setId(123);
//			payBack.getProduct().setGovermentOrder(new GovermentOrder());
//			payBack.getProduct().getGovermentOrder().setTitle("淘宝借钱三期");
//			payBacks.add(payBack);
//		}
//		return payBacks;
	}
	private List<Submit> findSubmits(List<Submit> submits,Integer productId)
	{
		if(submits==null||submits.size()==0)
			return null;
		List<Submit> list=new ArrayList<Submit>();
		for(Submit submit:submits)
		{
			if((int)productId==submit.getProductId())
				list.add(submit);
		}
		return list;
	}

	@Override
	public Map<String, PayBackDetail> getLenderRepayedDetail() {
		Lender lender=lenderService.getCurrentUser();
		Map<String, PayBackDetail> map=new HashMap<String, PayBackDetail>();
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		Calendar cal=Calendar.getInstance();
		long endtime=cal.getTimeInMillis();
		cal.add(Calendar.YEAR, -1);
		PayBackDetail detail=cashStreamDao.sumLenderRepayed(lender.getAccountId(), cal.getTimeInMillis(), endtime);
		map.put(PayBackDetail.ONEYEAR, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, 6);
		detail=cashStreamDao.sumLenderRepayed(lender.getAccountId(), cal.getTimeInMillis(), endtime);
		map.put(PayBackDetail.HALFYEAR, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, 3);
		detail=cashStreamDao.sumLenderRepayed(lender.getAccountId(), cal.getTimeInMillis(), endtime);
		map.put(PayBackDetail.THREEMONTH, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, 1);
		detail=cashStreamDao.sumLenderRepayed(lender.getAccountId(), cal.getTimeInMillis(), endtime);
		map.put(PayBackDetail.TWOMONTH, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, 1);
		detail=cashStreamDao.sumLenderRepayed(lender.getAccountId(), cal.getTimeInMillis(), endtime);
		map.put(PayBackDetail.ONEMONTH, detail==null?new PayBackDetail():detail);
		return map;
	}

	@Override
	public Map<String, PayBackDetail> getLenderWillBeRepayedDetail() {
		List<PayBack> payBacks=findLenderWaitforRepay();
		Map<String, PayBackDetail> map=new HashMap<String, PayBackDetail>();
		map.put(PayBackDetail.ONEYEAR, new PayBackDetail());
		map.put(PayBackDetail.HALFYEAR, new PayBackDetail());
		map.put(PayBackDetail.THREEMONTH, new PayBackDetail());
		map.put(PayBackDetail.TWOMONTH, new PayBackDetail());
		map.put(PayBackDetail.ONEMONTH, new PayBackDetail());
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		long afterOneYear=cal.getTimeInMillis();
		cal.add(Calendar.MONTH, -6);
		long afterHalfYear=cal.getTimeInMillis();
		cal.add(Calendar.MONTH, -3);
		long afterThreeMonth=cal.getTimeInMillis();
		cal.add(Calendar.MONTH, -1);
		long afterTwoMonth=cal.getTimeInMillis();
		cal.add(Calendar.MONTH, -1);
		long afterOneMonth=cal.getTimeInMillis();
		if(payBacks==null||payBacks.size()==0)
			return map;
		for(PayBack payBack:payBacks)
		{
			if(payBack.getDeadline()<=afterOneYear)
			{
				PayBackDetail detail=map.get(PayBackDetail.ONEYEAR);
				detail.setChiefAmount(detail.getChiefAmount().add(payBack.getChiefAmount()));
				detail.setInterest(detail.getInterest().add(payBack.getInterest()));
			}
			if(payBack.getDeadline()<=afterHalfYear)
			{
				PayBackDetail detail=map.get(PayBackDetail.HALFYEAR);
				detail.setChiefAmount(detail.getChiefAmount().add(payBack.getChiefAmount()));
				detail.setInterest(detail.getInterest().add(payBack.getInterest()));
			}
			if(payBack.getDeadline()<=afterThreeMonth)
			{
				PayBackDetail detail=map.get(PayBackDetail.THREEMONTH);
				detail.setChiefAmount(detail.getChiefAmount().add(payBack.getChiefAmount()));
				detail.setInterest(detail.getInterest().add(payBack.getInterest()));
			}
			if(payBack.getDeadline()<=afterTwoMonth)
			{
				PayBackDetail detail=map.get(PayBackDetail.TWOMONTH);
				detail.setChiefAmount(detail.getChiefAmount().add(payBack.getChiefAmount()));
				detail.setInterest(detail.getInterest().add(payBack.getInterest()));
			}
			if(payBack.getDeadline()<=afterOneMonth)
			{
				PayBackDetail detail=map.get(PayBackDetail.ONEMONTH);
				detail.setChiefAmount(detail.getChiefAmount().add(payBack.getChiefAmount()));
				detail.setInterest(detail.getInterest().add(payBack.getInterest()));
			}
		}
		return map;
	}

	@Override
	public Map<String, PayBackDetail> getBorrowerRepayedDetail() {
		Borrower borrower=borrowerService.getCurrentUser();
		List<Integer> states=new ArrayList<Integer>();
		states.add(PayBack.STATE_REPAYING);
		states.add(PayBack.STATE_FINISHREPAY);
		Map<String, PayBackDetail> map=new HashMap<String, PayBackDetail>();
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		Calendar cal=Calendar.getInstance();
//		long endtime=cal.getTimeInMillis();
		cal.add(Calendar.YEAR, -1);
		PayBackDetail detail=payBackDao.sumBorrowerRepayedPayBacks(borrower.getAccountId(), states, cal.getTimeInMillis());
		map.put(PayBackDetail.ONEYEAR, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, 6);
		detail=payBackDao.sumBorrowerRepayedPayBacks(borrower.getAccountId(), states, cal.getTimeInMillis());
		map.put(PayBackDetail.HALFYEAR, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, 3);
		detail=payBackDao.sumBorrowerRepayedPayBacks(borrower.getAccountId(), states, cal.getTimeInMillis());
		map.put(PayBackDetail.THREEMONTH, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, 1);
		detail=payBackDao.sumBorrowerRepayedPayBacks(borrower.getAccountId(), states, cal.getTimeInMillis());
		map.put(PayBackDetail.TWOMONTH, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, 1);
		detail=payBackDao.sumBorrowerRepayedPayBacks(borrower.getAccountId(), states, cal.getTimeInMillis());
		map.put(PayBackDetail.ONEMONTH, detail==null?new PayBackDetail():detail);
		return map;
	}

	@Override
	public Map<String, PayBackDetail> getBorrowerWillBeRepayedDetail() {
		Borrower borrower=borrowerService.getCurrentUser();
		List<Integer> states=new ArrayList<Integer>();
		states.add(PayBack.STATE_WAITFORREPAY);
		Map<String, PayBackDetail> map=new HashMap<String, PayBackDetail>();
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		PayBackDetail detail=payBackDao.sumBorrowerWillBeRepayedPayBacks(borrower.getAccountId(), states, cal.getTimeInMillis());
		map.put(PayBackDetail.ONEYEAR, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, -6);
		detail=payBackDao.sumBorrowerWillBeRepayedPayBacks(borrower.getAccountId(), states, cal.getTimeInMillis());
		map.put(PayBackDetail.HALFYEAR, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, -3);
		detail=payBackDao.sumBorrowerWillBeRepayedPayBacks(borrower.getAccountId(), states, cal.getTimeInMillis());
		map.put(PayBackDetail.THREEMONTH, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, -1);
		detail=payBackDao.sumBorrowerWillBeRepayedPayBacks(borrower.getAccountId(), states, cal.getTimeInMillis());
		map.put(PayBackDetail.TWOMONTH, detail==null?new PayBackDetail():detail);
		cal.add(Calendar.MONTH, -1);
		detail=payBackDao.sumBorrowerWillBeRepayedPayBacks(borrower.getAccountId(), states, cal.getTimeInMillis());
		map.put(PayBackDetail.ONEMONTH, detail==null?new PayBackDetail():detail);
		return map;
	}

	@Override
	public Integer storeChange(Integer borrowerAccountId,Integer paybackId,BigDecimal chiefamount,BigDecimal interest,
			String description) {
		CashStream cashStream=new CashStream();
		cashStream.setAction(CashStream.ACTION_STORECHANGE);
		cashStream.setPaybackId(paybackId);
		cashStream.setChiefamount(chiefamount);
		cashStream.setInterest(interest);
		cashStream.setDescription(description);
		cashStream.setBorrowerAccountId(borrowerAccountId);
		cashStreamDao.create(cashStream);
		return cashStream.getId();
	}

	@Override
	@Transactional
	public void returnCash(Integer cashStreamId, String loanNo) throws IllegalConvertException {
		CashStream cashStream = cashStreamDao.find(cashStreamId);
		if (cashStream.getState() == CashStream.STATE_RETURN)
			return;
		if (cashStream.getState() != cashStream.STATE_SUCCESS)
			changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
		changeCashStreamState(cashStreamId, CashStream.STATE_RETURN);
		// 退回资金
		Integer csId = null;
		
		//退回资金就是给该账户充值相应的资金，本资金应该是取现现金流的金额的相反（取现现金流金额为负值）
		if (cashStream.getLenderAccountId() != null) {
			csId = rechargeLenderAccount(cashStream.getLenderAccountId(),
					cashStream.getChiefamount().negate(), "提现退回");
		} else {
			csId = rechargeBorrowerAccount(cashStream.getBorrowerAccountId(),
					cashStream.getChiefamount().negate(), "提现退回");
		}
		changeCashStreamState(csId, CashStream.STATE_SUCCESS);
		
		//提现退回的现金流也保存提现一样的LoanNo
		cashStreamDao.updateLoanNo(csId, loanNo, null);
	}
	private void recordStateLogWithCreate(CashStream cashStream)
	{
		StateLog stateLog=new StateLog();
		stateLog.setCreatetime(System.currentTimeMillis());
		stateLog.setRefid(cashStream.getId());
		stateLog.setTarget(cashStream.getState());
		stateLog.setType(stateLog.TYPE_CASHSTREAM);
		stateLogDao.create(stateLog);
	}
}
