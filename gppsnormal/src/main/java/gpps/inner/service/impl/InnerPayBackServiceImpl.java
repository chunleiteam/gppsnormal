package gpps.inner.service.impl;

import gpps.dao.IBorrowerDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.IProductSeriesDao;
import gpps.dao.IStateLogDao;
import gpps.inner.service.IInnerPayBackService;
import gpps.model.Borrower;
import gpps.model.GovermentOrder;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.StateLog;
import gpps.tools.PayBackCalculateUtils;

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
	IProductSeriesDao productSeriesDao;
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
}
