package gpps.inner.service.impl;

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
import gpps.model.CashStream;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.StateLog;
import gpps.model.Submit;
import gpps.service.CashStreamSum;
import gpps.service.exception.CheckException;
import gpps.service.exception.IllegalOperationException;
import gpps.tools.PayBackCalculateUtils;
import gpps.tools.SinglePayBack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class InnerPayBackServiceImpl implements IInnerPayBackService {
	@Autowired
	IGovermentOrderDao orderDao;
	@Autowired
	IProductDao productDao;
	@Autowired
	IPayBackDao payBackDao;
	@Autowired
	IBorrowerDao borrowerDao;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	IProductSeriesDao productSeriesDao;
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	IInnerThirdPaySupportService innerThirdPayService;
	@Autowired
	IStateLogDao stateLogDao;
	Logger log = Logger.getLogger(InnerPayBackServiceImpl.class);
	
	@Override
	public List<PayBack> generatePayBacks(int amount, double rate,
			int payBackModel, long from, long to) {
		List<PayBack> payBacks=new ArrayList<PayBack>();
		if(payBackModel==ProductSeries.TYPE_AVERAGECAPITALPLUSINTEREST)//等额本息
		{
			payBacks = PayBackCalculateUtils.calclatePayBacksForDEBX(amount, rate, from, to);
		}else if(payBackModel==ProductSeries.TYPE_FINISHPAYINTERESTANDCAPITAL||payBackModel==ProductSeries.TYPE_FIRSTINTERESTENDCAPITAL)
		{
			payBacks = PayBackCalculateUtils.calclatePayBacksForXXHB(amount, rate, from, to);
		}
		return payBacks;
	}
	@Override
	public void refreshPayBack(int productId, boolean startrepay) throws Exception{
		// 重新计算payback
		// 删除
		payBackDao.deleteByProduct(productId);
		
		Product product = productDao.find(productId);
		GovermentOrder order = orderDao.find(product.getGovermentorderId());
		Borrower borrower = borrowerDao.find(order.getBorrowerId());
		
		// 创建还款计划
		ProductSeries productSeries = productSeriesDao.find(product.getProductseriesId());
		List<PayBack> payBacks = null;
		if(startrepay==true)
		{
			if(product.getRealAmount().compareTo(new BigDecimal(0))==0){
				throw new Exception("本产品实际融资额度为0");
			}
			payBacks = generatePayBacks(product.getRealAmount().intValue(), product.getRate().doubleValue(),productSeries.getType(), order.getIncomeStarttime(), product.getIncomeEndtime());
		}else{
			payBacks = generatePayBacks(product.getExpectAmount().intValue(), product.getRate().doubleValue(),productSeries.getType(), order.getIncomeStarttime(), product.getIncomeEndtime());
		}
		for (PayBack payBack : payBacks) {
			payBack.setBorrowerAccountId(borrower.getAccountId());
			payBack.setProductId(product.getId());
			payBackDao.create(payBack);

			StateLog stateLog = new StateLog();
			stateLog.setCreatetime(System.currentTimeMillis());
			stateLog.setRefid(payBack.getId());
			stateLog.setTarget(payBack.getState());
			stateLog.setType(StateLog.TYPE_PAYBACK);
			stateLogDao.create(stateLog);
			log.info("重新为产品【"+productId+"】创建还款列表项【"+payBack.getId()+"】");
		}
	}
	
	@Override
	public void validatePayBackSequence(int payBackId) throws IllegalOperationException{
		PayBack payBack = payBackDao.find(payBackId);
		if(payBack==null){
			throw new IllegalOperationException("无效的还款！");
		}
		Product currentProduct = productDao.find(payBack.getProductId());
		
		if(currentProduct==null){
			throw new IllegalOperationException("本次还款未找到对应的订单！");
		}
		currentProduct.setProductSeries(productSeriesDao.find(currentProduct.getProductseriesId()));
		// 验证还款顺序
		List<Product> products = productDao.findByGovermentOrder(currentProduct.getGovermentorderId());
			for (Product product : products) {
				if (product.getId() == (int) (currentProduct.getId())) {
					//按还款时间顺序排列
					List<PayBack> payBacks = findAll(product.getId());
					for (PayBack pb : payBacks) {
						//确保所有在该还款之前的所有还款都已经执行还款完毕
						if(pb.getDeadline()<payBack.getDeadline() && pb.getState() != PayBack.STATE_FINISHREPAY){
							throw new IllegalOperationException("请按时间顺序进行还款");
						}else if(pb.getDeadline()==payBack.getDeadline() && pb.getState() != PayBack.STATE_WAITFORREPAY){
							throw new IllegalOperationException("不处于待还款状态");
						}
					}
				}else
				{
					product.setProductSeries(productSeriesDao.find(product.getProductseriesId()));
					if (product.getProductSeries().getType() < currentProduct.getProductSeries().getType()) {
						List<PayBack> payBacks = findAll(product.getId());
						for (PayBack pb : payBacks) {
							if (pb.getDeadline() <= payBack.getDeadline() && pb.getState() == PayBack.STATE_FINISHREPAY)
							{
								continue;
							}else if(pb.getDeadline() <= payBack.getDeadline() && pb.getState() != PayBack.STATE_FINISHREPAY){
								throw new IllegalOperationException("请先还完稳健型/平衡型产品再进行此次还款");
							}
						}
					}
				}
			}
	}
	
	@Override
	public List<PayBack> findAll(Integer productId) {
		List<PayBack> payBacks=payBackDao.findAllByProduct(productId);
		if(payBacks==null||payBacks.size()==0)
			return new ArrayList<PayBack>(0);
		return payBacks;
	}
	
	@Override
	public List<SinglePayBack> calculatePayBacks(Integer payBackId) throws CheckException{
		List<SinglePayBack> res = new ArrayList<SinglePayBack>();
		PayBack payBack = payBackDao.find(payBackId);
		List<Submit> submits=submitDao.findAllByProductAndState(payBack.getProductId(), Submit.STATE_COMPLETEPAY);
		if(submits==null||submits.size()==0)
			return res;
		Product product=productDao.find(payBack.getProductId());
		GovermentOrder order=orderDao.find(product.getGovermentorderId());
		Borrower borrower=borrowerDao.find(order.getBorrowerId());
		
		BigDecimal totalChiefAmount=payBack.getChiefAmount();
		BigDecimal totalInterest=payBack.getInterest();
		
		for(int i=0;i<submits.size();i++)
		{
			Submit submit=submits.get(i);
			Lender lender=lenderDao.find(submit.getLenderId());
			
			//如果已经执行过还款了，则直接添加到结果列表中
			List<CashStream> cashStreams=cashStreamDao.findRepayCashStream(submit.getId(), payBack.getId());
			if(cashStreams!=null&&cashStreams.size()>0)
			{
				CashStream cashStream=cashStreams.get(0);
				totalChiefAmount.subtract(cashStream.getChiefamount());
				totalInterest.subtract(cashStream.getInterest());
					
				SinglePayBack spb = new SinglePayBack();
				spb.setState(SinglePayBack.STATE_REPAY_SUCCESS);
				spb.setChief(cashStream.getChiefamount());
				spb.setInterest(cashStream.getInterest());
					
				spb.setFromAccountId(cashStream.getBorrowerAccountId());
				spb.setFromMoneyMoreMore(borrower.getThirdPartyAccount());
				spb.setFromname(borrower.getCompanyName());
					
				spb.setToAccountId(cashStream.getLenderAccountId());
				spb.setToMoneyMoreMore(lender.getThirdPartyAccount());
				spb.setToname(lender.getName());
					
				spb.setSubmitAmount(submit.getAmount());
				spb.setSubmitId(submit.getId());
					
				res.add(spb);
				continue;
			}
			
			BigDecimal lenderChiefAmount=null;
			BigDecimal lenderInterest=null;
			if(payBack.getType()==PayBack.TYPE_LASTPAY)
			{
				//最后一笔还款根据纵向计算，即投资额等于所有还款的本金之和
				cashStreams=cashStreamDao.findRepayCashStream(submit.getId(), null);
				BigDecimal repayedChiefAmount=BigDecimal.ZERO;
				if(cashStreams!=null&&cashStreams.size()>0)
				{
					for(CashStream cashStream:cashStreams)
					{
						repayedChiefAmount=repayedChiefAmount.add(cashStream.getChiefamount());
					}
				}
				lenderChiefAmount=submit.getAmount().subtract(repayedChiefAmount);
			}
			else {
				if(i==(submits.size()-1))
				{
					//在同一笔还款中，还给最后一个人的本金额等于本次还款总额减去前面所有人的还款本金之和
					lenderChiefAmount=totalChiefAmount;
				}
				else
				{
					//不是最后一个人的还款，直接计算，小数点两位后除不尽的直接舍弃
					lenderChiefAmount=payBack.getChiefAmount().multiply(submit.getAmount()).divide(product.getRealAmount(), 2, BigDecimal.ROUND_DOWN);
				}
			}
			
			//利息就是直接计算，小数点两位后除不尽的直接舍弃
			lenderInterest=payBack.getInterest().multiply(submit.getAmount()).divide(product.getRealAmount(), 2, BigDecimal.ROUND_DOWN);
			
			//还款的本金总额减去每一笔还款本金额，用于计算给最后一个人的还款本金
			totalChiefAmount=totalChiefAmount.subtract(lenderChiefAmount);
			
			//还款的利息总额减去每一笔还款利息额，由于还给每一个人的利息都是用舍的，因此用于计算最后存零的值，最后存零的值范围一定在value>=0&&value<submitcount*0.01
			totalInterest=totalInterest.subtract(lenderInterest);
			
			SinglePayBack spb = new SinglePayBack();
			spb.setState(SinglePayBack.STATE_UNREPAY);
			spb.setChief(lenderChiefAmount);
			spb.setInterest(lenderInterest);
			
			spb.setFromAccountId(borrower.getAccountId());
			spb.setFromMoneyMoreMore(borrower.getThirdPartyAccount());
			spb.setFromname(borrower.getCompanyName());
			
			spb.setToAccountId(lender.getAccountId());
			spb.setToMoneyMoreMore(lender.getThirdPartyAccount());
			spb.setToname(lender.getName());
			
			spb.setSubmitAmount(submit.getAmount());
			spb.setSubmitId(submit.getId());
			
			res.add(spb);
		}
		
		
		if(totalChiefAmount.compareTo(new BigDecimal(0))!=0){
			throw new CheckException("计算有问题，本次还款本金总额不等于各笔还款的本金总和");
		}
		
		if(totalInterest.compareTo(BigDecimal.ZERO)>0 && totalInterest.compareTo(new BigDecimal(0.01*submits.size()))<=0){
			
			SinglePayBack spb = new SinglePayBack();
			spb.setState(SinglePayBack.STATE_UNREPAY);
			spb.setChief(new BigDecimal(0));
			spb.setInterest(totalInterest);
			
			spb.setFromAccountId(borrower.getAccountId());
			spb.setFromMoneyMoreMore(borrower.getThirdPartyAccount());
			spb.setFromname(borrower.getCompanyName());
			
			spb.setToAccountId(-1);
			spb.setToMoneyMoreMore(innerThirdPayService.getPlatformMoneymoremore());
			spb.setToname("政采贷平台");
			
			spb.setSubmitAmount(new BigDecimal(0));
			spb.setSubmitId(null);
			
			res.add(spb);
		}else if(totalInterest.compareTo(BigDecimal.ZERO)==0){
			
		}else{
			throw new CheckException("还款"+payBack.getId()+"金额计算有问题，利息不在合理范围内，请检查！");
		}
		
		return res;
	}
	
	@Override
	public List<SinglePayBack> justCheckOutPayBackById(Integer payBackId, String executeStep) throws CheckException{
		PayBack payBack=payBackDao.find(payBackId);
		Product product=productDao.find(payBack.getProductId());
		List<SinglePayBack> spbs = null;
		try{
			spbs = calculatePayBacks(payBackId);
		}catch(Exception e){
			throw new CheckException(e.getMessage());
		}
		StringBuilder sb = new StringBuilder();
		sb.append(executeStep+"【"+payBackId+"】:");
		BigDecimal totalC=BigDecimal.ZERO;
		BigDecimal totalI=BigDecimal.ZERO;
		for(SinglePayBack spb:spbs)
		{
			BigDecimal amount = spb.getChief();
			totalC=totalC.add(amount);
			BigDecimal interest = spb.getInterest();
			totalI=totalI.add(interest);
			String singlePayback = spb.getToname()+": 本金="+spb.getChief()+", 利息="+spb.getInterest()+";\n";
			sb.append(singlePayback);
		}
		log.info(sb.toString());
		if(totalC.compareTo(payBack.getChiefAmount())!=0)
		{
			String emsg = executeStep+"失败：当前还款金额计算不符,本金总额不等于各笔还款本金之和";
			log.warn(emsg);
			throw new CheckException(emsg);
			
		}
		if(totalI.compareTo(payBack.getInterest())!=0)
		{
			String emsg = executeStep+"失败：当前还款金额计算不符,利息总额不等于各笔还款利息之和";
			log.warn(emsg);
			throw new CheckException(emsg);
		}
		
		// 最后一次验证,验证之前的payback是否符合
		if(payBack.getType()==PayBack.TYPE_LASTPAY)
		{
			BigDecimal amount=BigDecimal.ZERO;
			List<PayBack> payBacks=findAll(payBack.getProductId());
			for(PayBack pb:payBacks)
			{
				if(pb.getType()==PayBack.TYPE_LASTPAY)
				{
					continue;
				}
				if(pb.getState()==PayBack.STATE_REPAYING)
				{
					String emsg = executeStep+"失败：之前还有执行中的还款";
					log.warn(emsg);
					throw new CheckException(emsg);
				}
				if(pb.getState()!=PayBack.STATE_FINISHREPAY)
				{
					String emsg = executeStep+"失败：还有尚未成功的还款";
					log.warn(emsg);
					throw new CheckException(emsg);
				}
				
				//统计针对某一次还款的所有执行成功的还款现金流的本金与利息总和
				CashStreamSum sum=cashStreamDao.sumPayBackByAction(pb.getId(), CashStream.ACTION_REPAY);
				
				//存零的金额
				CashStreamSum sum2=cashStreamDao.sumPayBackByAction(pb.getId(), CashStream.ACTION_STORECHANGE);
				if(sum2==null)
				{
					sum2 = new CashStreamSum();
				}
				
				//实际执行的还款的本金与利息总和与payback的本金与利息总额应该一致
				if(sum.getChiefAmount().add(sum2.getChiefAmount()).compareTo(pb.getChiefAmount())!=0||sum.getInterest().add(sum2.getInterest()).compareTo(pb.getInterest())!=0)
				{
					String emsg = executeStep+"失败：还款[id:"+pb.getId()+"]金额计算不符";
					log.warn(emsg);
					throw new CheckException(emsg);
				}
				
				amount=amount.add(pb.getChiefAmount());
			}
			if(amount.add(payBack.getChiefAmount()).compareTo(product.getRealAmount())!=0)
			{
				String emsg = executeStep+"失败：还款总额与产品不符";
				log.warn(emsg);
				throw new CheckException(emsg);
			}
		}
		log.info(executeStep+"成功！");
		
		SinglePayBack spb = new SinglePayBack();
		spb.setToname("总计");
		spb.setChief(payBack.getChiefAmount());
		spb.setInterest(payBack.getInterest());
		spb.setSubmitAmount(product.getRealAmount());
		spbs.add(spb);
		
		return spbs;
	}
	
	@Override
	public void justCheckOutPayBackByCS(List<CashStream> css, Integer payBackId, String executeStep) throws CheckException{
		PayBack payBack=payBackDao.find(payBackId);
		Product product=productDao.find(payBack.getProductId());
		
		StringBuilder sb = new StringBuilder();
		sb.append(executeStep+"【"+payBackId+"】");
		BigDecimal totalC=BigDecimal.ZERO;
		BigDecimal totalI=BigDecimal.ZERO;
		for(CashStream cs:css)
		{
			BigDecimal amount = cs.getChiefamount();
			totalC=totalC.add(amount);
			BigDecimal interest = cs.getInterest();
			totalI=totalI.add(interest);
		}
		log.info(sb.toString());
		if(totalC.compareTo(payBack.getChiefAmount().negate())!=0)
		{
			String emsg = executeStep+"失败：当前还款金额计算不符,本金总额不等于各笔还款本金之和";
			log.warn(emsg);
			throw new CheckException(emsg);
			
		}
		if(totalI.compareTo(payBack.getInterest().negate())!=0)
		{
			String emsg = executeStep+"失败：当前还款金额计算不符,利息总额不等于各笔还款利息之和";
			log.warn(emsg);
			throw new CheckException(emsg);
		}
		
		// 最后一次验证,验证之前的payback是否符合
		if(payBack.getType()==PayBack.TYPE_LASTPAY)
		{
			BigDecimal amount=BigDecimal.ZERO;
			List<PayBack> payBacks=findAll(payBack.getProductId());
			for(PayBack pb:payBacks)
			{
				if(pb.getType()==PayBack.TYPE_LASTPAY)
				{
					continue;
				}
				if(pb.getState()==PayBack.STATE_REPAYING)
				{
					String emsg = executeStep+"失败：之前还有执行中的还款";
					log.warn(emsg);
					throw new CheckException(emsg);
				}
				if(pb.getState()!=PayBack.STATE_FINISHREPAY)
				{
					String emsg = executeStep+"失败：还有尚未成功的还款";
					log.warn(emsg);
					throw new CheckException(emsg);
				}
				
				//统计针对某一次还款的所有执行成功的还款现金流的本金与利息总和
				CashStreamSum sum=cashStreamDao.sumPayBackByAction(pb.getId(), CashStream.ACTION_REPAY);
				
				//存零的金额
				CashStreamSum sum2=cashStreamDao.sumPayBackByAction(pb.getId(), CashStream.ACTION_STORECHANGE);
				
				//实际执行的还款的本金与利息总和与payback的本金与利息总额应该一致
				if(sum.getChiefAmount().add(sum2.getChiefAmount()).compareTo(pb.getChiefAmount())!=0||sum.getInterest().add(sum2.getInterest()).compareTo(pb.getInterest())!=0)
				{
					String emsg = executeStep+"失败：还款[id:"+pb.getId()+"]金额计算不符";
					log.warn(emsg);
					throw new CheckException(emsg);
				}
				
				amount=amount.add(pb.getChiefAmount());
			}
			if(amount.add(payBack.getChiefAmount()).compareTo(product.getRealAmount())!=0)
			{
				String emsg = executeStep+"失败：还款总额与产品不符";
				log.warn(emsg);
				throw new CheckException(emsg);
			}
		}
		log.info(executeStep+"成功！");
	}
	
	@Override
	public List<SinglePayBack> justCheckOutPayBackBySPB(List<SinglePayBack> spbs, Integer payBackId, String executeStep) throws CheckException{
		PayBack payBack=payBackDao.find(payBackId);
		Product product=productDao.find(payBack.getProductId());
		StringBuilder sb = new StringBuilder();
		sb.append(executeStep+"【"+payBackId+"】:");
		BigDecimal totalC=BigDecimal.ZERO;
		BigDecimal totalI=BigDecimal.ZERO;
		for(SinglePayBack spb:spbs)
		{
			BigDecimal amount = spb.getChief();
			totalC=totalC.add(amount);
			BigDecimal interest = spb.getInterest();
			totalI=totalI.add(interest);
			String singlePayback = spb.getToname()+": 本金="+spb.getChief()+", 利息="+spb.getInterest()+";\n";
			sb.append(singlePayback);
		}
		log.info(sb.toString());
		if(totalC.compareTo(payBack.getChiefAmount())!=0)
		{
			String emsg = executeStep+"失败：当前还款金额计算不符,本金总额不等于各笔还款本金之和";
			log.warn(emsg);
			throw new CheckException(emsg);
			
		}
		if(totalI.compareTo(payBack.getInterest())!=0)
		{
			String emsg = executeStep+"失败：当前还款金额计算不符,利息总额不等于各笔还款利息之和";
			log.warn(emsg);
			throw new CheckException(emsg);
		}
		
		// 最后一次验证,验证之前的payback是否符合
		if(payBack.getType()==PayBack.TYPE_LASTPAY)
		{
			BigDecimal amount=BigDecimal.ZERO;
			List<PayBack> payBacks=findAll(payBack.getProductId());
			for(PayBack pb:payBacks)
			{
				if(pb.getType()==PayBack.TYPE_LASTPAY)
				{
					continue;
				}
				if(pb.getState()==PayBack.STATE_REPAYING)
				{
					String emsg = executeStep+"失败：之前还有执行中的还款";
					log.warn(emsg);
					throw new CheckException(emsg);
				}
				if(pb.getState()!=PayBack.STATE_FINISHREPAY)
				{
					String emsg = executeStep+"失败：还有尚未成功的还款";
					log.warn(emsg);
					throw new CheckException(emsg);
				}
				
				//统计针对某一次还款的所有执行成功的还款现金流的本金与利息总和
				CashStreamSum sum=cashStreamDao.sumPayBackByAction(pb.getId(), CashStream.ACTION_REPAY);
				
				//存零的金额
				CashStreamSum sum2=cashStreamDao.sumPayBackByAction(pb.getId(), CashStream.ACTION_STORECHANGE);
				
				//实际执行的还款的本金与利息总和与payback的本金与利息总额应该一致
				if(sum.getChiefAmount().add(sum2.getChiefAmount()).compareTo(pb.getChiefAmount())!=0||sum.getInterest().add(sum2.getInterest()).compareTo(pb.getInterest())!=0)
				{
					String emsg = executeStep+"失败：还款[id:"+pb.getId()+"]金额计算不符";
					log.warn(emsg);
					throw new CheckException(emsg);
				}
				
				amount=amount.add(pb.getChiefAmount());
			}
			if(amount.add(payBack.getChiefAmount()).compareTo(product.getRealAmount())!=0)
			{
				String emsg = executeStep+"失败：还款总额与产品不符";
				log.warn(emsg);
				throw new CheckException(emsg);
			}
		}
		log.info(executeStep+"成功！");
		
//		SinglePayBack spb = new SinglePayBack();
//		spb.setToname("总计");
//		spb.setChief(payBack.getChiefAmount());
//		spb.setInterest(payBack.getInterest());
//		spb.setSubmitAmount(product.getRealAmount());
//		spbs.add(spb);
		
		return spbs;
	}
}
