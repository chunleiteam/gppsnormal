package gpps.inner.service.impl;

import static gpps.tools.ObjectUtil.checkNullObject;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpps.dao.IGovermentOrderDao;
import gpps.dao.IProductDao;
import gpps.dao.IProductSeriesDao;
import gpps.dao.IStateLogDao;
import gpps.inner.service.IInnerGovermentOrderService;
import gpps.inner.service.IInnerProductService;
import gpps.model.GovermentOrder;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.StateLog;
import gpps.service.exception.IllegalConvertException;
import gpps.tools.DateCalculateUtils;
@Service
public class InnerProductServiceImpl implements IInnerProductService {
	@Autowired
	IProductDao productDao;
	@Autowired
	IProductSeriesDao productSeriesDao;
	@Autowired
	IGovermentOrderDao orderDao;
	@Autowired
	IStateLogDao stateLogDao;
	@Autowired
	IInnerGovermentOrderService innerOrderService;
	Logger log = Logger.getLogger(InnerProductServiceImpl.class);
	static int[][] validConverts={
		{Product.STATE_UNPUBLISH,Product.STATE_FINANCING},
		{Product.STATE_FINANCING,Product.STATE_REPAYING},
		{Product.STATE_FINANCING,Product.STATE_QUITFINANCING},
		{Product.STATE_REPAYING,Product.STATE_FINISHREPAY},
		{Product.STATE_REPAYING,Product.STATE_POSTPONE},
		{Product.STATE_POSTPONE,Product.STATE_FINISHREPAY},
		{Product.STATE_FINISHREPAY,Product.STATE_APPLYTOCLOSE},
		{Product.STATE_APPLYTOCLOSE,Product.STATE_CLOSE}
		};
	@Override
	public void changeState(int productId, int state) throws IllegalConvertException {
		Product product = productDao.find(productId);
		if (product == null)
			throw new RuntimeException("product is not existed");
		for(int[] validStateConvert:validConverts)
		{
			if(product.getState()==validStateConvert[0]&&state==validStateConvert[1])
			{
				StateLog stateLog=new StateLog();
				stateLog.setSource(product.getState());
				stateLog.setTarget(state);
				stateLog.setType(StateLog.TYPE_PRODUCT);
				stateLog.setRefid(productId);
				productDao.changeState(productId, state,System.currentTimeMillis());
				stateLogDao.create(stateLog);
				log.info("产品【"+productId+"】状态由"+stateLog.getSource()+"变为"+stateLog.getTarget());
				return;
			}
		}
		throw new IllegalConvertException();
	}
	
	@Override
	public void startRepaying(int productId) throws IllegalConvertException{
		changeState(productId, Product.STATE_REPAYING);
		
		Product product = productDao.find(productId);
		
		// 查询本产品对应订单下面的所有产品，如果状态均改为“还款中”，则说明本订单对应所有产品状态都修改完毕，则将订单状态修改为“还款中”
		List<Product> pros = productDao.findByGovermentOrder(product.getGovermentorderId());
				boolean doneFlag = true;
				for (Product pro : pros) {
					if (pro.getState() != Product.STATE_REPAYING) {
						doneFlag = false;
						break;
					}
				}
				if (doneFlag == true) {
					innerOrderService.startRepaying(product.getGovermentorderId());
				}
	}
	
	@Override
	public void quitFinancing(int productId) throws IllegalConvertException{
		changeState(productId, Product.STATE_QUITFINANCING);
		Product product = productDao.find(productId);
		
		// 查询本产品对应订单下面的所有产品，如果状态均改为“流标”，则说明本订单对应所有产品状态都修改完毕，则将订单状态修改为“流标”
		List<Product> pros = productDao.findByGovermentOrder(product.getGovermentorderId());
				boolean doneFlag = true;
				for (Product pro : pros) {
					if (pro.getState() != Product.STATE_QUITFINANCING) {
						doneFlag = false;
						break;
					}
				}
				if (doneFlag == true) {
					innerOrderService.quitFinancing(product.getGovermentorderId());
				}
	}
	
	@Override
	public void finishRepay(int productId) throws IllegalConvertException{
		changeState(productId, Product.STATE_FINISHREPAY);
		Product product = productDao.find(productId);
		
		//查询本产品对应订单下面的所有产品，如果状态均改为“还款完毕”，则说明本订单对应的所有产品状态都修改完毕，则将订单状态修改为“待关闭”
		List<Product> pros = productDao.findByGovermentOrder(product.getGovermentorderId());
		boolean doneFlag = true;
		for (Product pro : pros) {
			if (pro.getState() != Product.STATE_FINISHREPAY) {
				doneFlag = false;
				break;
			}
		}
		if (doneFlag == true) {
			innerOrderService.finishRepay(product.getGovermentorderId());
		}
	}
	
	@Override
	public void create(Product product) throws IllegalArgumentException{
		checkNullObject("orderId", product.getGovermentorderId());
		GovermentOrder order=orderDao.find(product.getGovermentorderId());
		checkNullObject(GovermentOrder.class, order);
		if(order.getState()!=GovermentOrder.STATE_UNPUBLISH)
			throw new IllegalArgumentException("只能为未发布的订单添加产品");
		product.setState(Product.STATE_UNPUBLISH);
		product.setCreatetime(System.currentTimeMillis());
		checkNullObject("productseriesId", product.getProductseriesId());
		ProductSeries productSeries=productSeriesDao.find(product.getProductseriesId());
		checkNullObject(ProductSeries.class,productSeries);
		checkNullObject("expectAmount", product.getExpectAmount());
		checkNullObject("rate", product.getRate());
		product.setRealAmount(BigDecimal.ZERO);
		
		long end = DateCalculateUtils.getStartTime(product.getIncomeEndtime());
		product.setIncomeEndtime(end);
		
		productDao.create(product);
		StateLog productStateLog=new StateLog();
		productStateLog.setCreatetime(System.currentTimeMillis());
		productStateLog.setRefid(product.getId());
		productStateLog.setTarget(product.getState());
		productStateLog.setType(StateLog.TYPE_PRODUCT);
		stateLogDao.create(productStateLog);
		log.info("产品【"+product.getId()+"】创建成功");
	}

}
