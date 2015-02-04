/**
 * 
 */
package gpps.service.impl;

import static gpps.tools.ObjectUtil.checkNullObject;
import gpps.constant.Pagination;
import gpps.dao.IBorrowerDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.IFinancingRequestDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.IProductSeriesDao;
import gpps.dao.IStateLogDao;
import gpps.dao.ISubmitDao;
import gpps.inner.service.IInnerGovermentOrderService;
import gpps.inner.service.IInnerPayBackService;
import gpps.model.Borrower;
import gpps.model.CashStream;
import gpps.model.FinancingRequest;
import gpps.model.GovermentOrder;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.StateLog;
import gpps.model.Submit;
import gpps.model.Task;
import gpps.model.ref.Accessory;
import gpps.model.ref.Accessory.MimeCol;
import gpps.model.ref.Accessory.MimeItem;
import gpps.service.CashStreamSum;
import gpps.service.IBorrowerService;
import gpps.service.IContractService;
import gpps.service.IGovermentOrderService;
import gpps.service.IPayBackService;
import gpps.service.IProductService;
import gpps.service.ITaskService;
import gpps.service.exception.CheckException;
import gpps.service.exception.ExistWaitforPaySubmitException;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.IllegalOperationException;
import gpps.service.exception.SMSException;
import gpps.service.message.ILetterSendService;
import gpps.service.message.IMessageService;
import gpps.service.thirdpay.IAuditBuyService;
import gpps.tools.DateCalculateUtils;
import gpps.tools.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyservice.xml.EasyObjectXMLTransformerImpl;
import com.easyservice.xml.IEasyObjectXMLTransformer;
import com.easyservice.xml.XMLParseException;

/**
 * @author wangm
 *
 */
@Service("gpps.service.IGovermentOrderService")
public class GovermentOrderServiceImpl implements IGovermentOrderService{
	@Autowired
	IGovermentOrderDao govermentOrderDao;
	@Autowired
	IBorrowerDao borrowerDao;
	@Autowired
	IProductDao productDao;
	@Autowired
	ITaskService taskService;
	@Autowired
	IStateLogDao stateLogDao;
	@Autowired
	IBorrowerService borrowerService;
	@Autowired
	IFinancingRequestDao financingRequestDao;
	@Autowired
	IProductService productService;
	@Autowired
	ISubmitDao submitDao;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	IInnerPayBackService innerPayBackService;
	@Autowired
	IPayBackDao payBackDao;
	@Autowired
	IProductSeriesDao productSeriesDao;
	@Autowired
	IContractService contractService;
	@Autowired
	IMessageService messageService;
	@Autowired
	ILetterSendService letterSendService;
	@Autowired
	IInnerGovermentOrderService innerOrderService;
	@Autowired
	IAuditBuyService auditBuyService;
	Logger log = Logger.getLogger(GovermentOrderServiceImpl.class);
	private static final IEasyObjectXMLTransformer xmlTransformer=new EasyObjectXMLTransformerImpl(); 
	static int[] orderStates={
		GovermentOrder.STATE_UNPUBLISH,
		GovermentOrder.STATE_PREPUBLISH,
		GovermentOrder.STATE_FINANCING,
		GovermentOrder.STATE_QUITFINANCING,
		GovermentOrder.STATE_REPAYING,
		GovermentOrder.STATE_WAITINGCLOSE,
		GovermentOrder.STATE_CLOSE
	};
	// Order状态变化，融资金额变化需要修改下面的缓存
	Map<String,GovermentOrder> financingOrders=new HashMap<String,GovermentOrder>();  
	@PostConstruct
	public void init()
	{
		// 启动时将所有正在竞标状态的订单加载到内存中，并分配锁
		List<Integer> states=new ArrayList<Integer>();
		states.add(GovermentOrder.STATE_FINANCING);
		List<GovermentOrder> govermentOrders=govermentOrderDao.findByStates(states);
		if(govermentOrders==null||govermentOrders.size()==0)
			return;
		for(GovermentOrder order:govermentOrders)
		{
			insertGovermentOrderToFinancing(order);
		}
	}
	@Override
	public GovermentOrder create(GovermentOrder govermentOrder) {
		checkNullObject("borrowerId", govermentOrder.getBorrowerId());
		checkNullObject(Borrower.class, borrowerDao.find(govermentOrder.getBorrowerId()));
		govermentOrder.setState(GovermentOrder.STATE_UNPUBLISH);
		checkNullObject("financingRequestId", govermentOrder.getFinancingRequestId());
		FinancingRequest request=checkNullObject(FinancingRequest.class, financingRequestDao.find(govermentOrder.getFinancingRequestId()));
		if(request.getState()!=FinancingRequest.STATE_INIT)
			throw new IllegalArgumentException("创建订单必须选择非处理的融资申请");
//		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		
		//融资起始时间设置为0点0分0秒
		long start = DateCalculateUtils.getStartTime(govermentOrder.getFinancingStarttime());
		govermentOrder.setFinancingStarttime(start);
		
		//融资截止时间设置为23点59分59秒
		long end = DateCalculateUtils.getEndTime(govermentOrder.getFinancingEndtime());
		govermentOrder.setFinancingEndtime(end);
		
		//收益起始时间设置为0点0分0秒
		long incomeStart = DateCalculateUtils.getStartTime(govermentOrder.getIncomeStarttime());
		govermentOrder.setIncomeStarttime(incomeStart);
		
		govermentOrderDao.create(govermentOrder);
		StateLog stateLog=new StateLog();
		stateLog.setCreatetime(System.currentTimeMillis());
		stateLog.setRefid(govermentOrder.getId());
		stateLog.setTarget(govermentOrder.getState());
		stateLog.setType(stateLog.TYPE_GOVERMENTORDER);
		stateLogDao.create(stateLog);
		return govermentOrder;
	}
	@Override
	public List<GovermentOrder> findByStates(int states, int offset, int recnum) {
		List<Integer> list=null;
		if(states!=-1)
		{
			list=new ArrayList<Integer>();
			for(int orderState:orderStates)
			{
				if((orderState&states)>0)
					list.add(orderState);
			}
			if(list.isEmpty())
				return new ArrayList<GovermentOrder>(0);
		}
		return govermentOrderDao.findByStatesWithPaging(list, offset, recnum);
	}
	@Override
	public List<GovermentOrder> findByBorrowerIdAndStates(int borrowerId, int states) {
		List<Integer> list=null;
		if(states!=-1)
		{
			list=new ArrayList<Integer>();
			for(int orderState:orderStates)
			{
				if((orderState&states)>0)
					list.add(orderState);
			}
			if(list.isEmpty())
				return new ArrayList<GovermentOrder>(0);
		}
		List<GovermentOrder> govermentOrders=govermentOrderDao.findByBorrowerIdAndState(borrowerId, list);
		if(govermentOrders==null||govermentOrders.size()==0)
			return govermentOrders;
		for(GovermentOrder govermentOrder:govermentOrders)
		{
			govermentOrder.setProducts(productDao.findByGovermentOrder(govermentOrder.getId()));
		}
		return govermentOrders;
	}

//	@Override
//	public void passApplying(Integer orderId) throws IllegalConvertException {
//		changeState(orderId, GovermentOrder.STATE_PREPUBLISH);
//	}

//	@Override
//	public void refuseApplying(Integer orderId) throws IllegalConvertException {
//		changeState(orderId, GovermentOrder.STATE_REFUSE);
//	}
//
//	@Override
//	public void reviseApplying(Integer orderId) throws IllegalConvertException {
//		changeState(orderId, GovermentOrder.STATE_MODIFY);	
//	}
//
//	@Override
//	public void reApply(Integer orderId) throws IllegalConvertException {
//		changeState(orderId, GovermentOrder.STATE_REAPPLY);
//	}
	@Override
	@Transactional
	public void startFinancing(Integer orderId) throws IllegalConvertException,IllegalOperationException {
		checkNullObject("orderId", orderId);
		GovermentOrder order=checkNullObject(GovermentOrder.class, govermentOrderDao.find(orderId));
		//校验borrower是否已开通第三方
		Borrower borrower=borrowerDao.find(order.getBorrowerId());
		if(StringUtil.isEmpty(borrower.getThirdPartyAccount()))
			throw new IllegalOperationException("借款方尚未开通第三方账户");
		
		
		innerOrderService.changeState(orderId, GovermentOrder.STATE_FINANCING);
		order.setState(GovermentOrder.STATE_FINANCING);
		insertGovermentOrderToFinancing(order);
		
		Map<String, String> param = new HashMap<String, String>();
		param.put(IMessageService.PARAM_ORDER_NAME, order.getTitle());
		param.put(ILetterSendService.PARAM_TITLE, "启动融资");
		try{
			letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_STARTFINANCE, ILetterSendService.USERTYPE_BORROWER, order.getBorrowerId(), param);
			messageService.sendMessage(IMessageService.MESSAGE_TYPE_STARTFINANCE, IMessageService.USERTYPE_BORROWER, order.getBorrowerId(), param);
		}catch(SMSException e){
			log.error(e.getMessage());
		}
	}
	
	@Override
	@Transactional
	public void startRepaying(Integer orderId)  throws IllegalConvertException,IllegalOperationException, ExistWaitforPaySubmitException, CheckException, Exception{
		GovermentOrder order = govermentOrderDao.find(orderId);
		
		if(order==null){
			throw new IllegalOperationException("无效订单！");
		}
		
		long financingEndtime = order.getFinancingEndtime();
		
		List<Product> products = productService.findByGovermentOrder(orderId);
		
		for(Product product:products){
			CashStreamSum sum = cashStreamDao.sumProduct(product.getId(), CashStream.ACTION_FREEZE);
			if((sum==null||sum.getChiefAmount().negate().compareTo(product.getExpectAmount())<0) && financingEndtime>System.currentTimeMillis()){
				throw new IllegalOperationException("金额尚未融满，且融资截止时间未到，无法启动还款！");
			}
		}
		
		for(Product product:products){
			if(!contractService.isComplete(product.getId())){
				throw new IllegalOperationException("合同尚未处理完毕，无法启动融资");
			}
		}
		
		
		Borrower borrower = borrowerDao.find(order.getBorrowerId());
		if(borrower==null){
			throw new IllegalOperationException("订单没有融资方！");
		}else if(borrower.getAuthorizeTypeOpen()!=Borrower.AUTHORIZETYPEOPEN_RECHARGE){
			throw new IllegalOperationException("融资方尚未授权平台自动还款！");
		}
		
		
		try{
			order=applyFinancingOrder(orderId);
			if(order==null){
				throw new CheckException("内存中没有对应的融资中订单！");
			}
		List<Product> pts = order.getProducts();
		
		if(pts==null || pts.isEmpty()){
			throw new CheckException("融资中订单没有找到对应的产品");
		}
		
		for(Product product:pts){
			List<Submit> submits=submitDao.findAllByProductAndState(product.getId(), Submit.STATE_COMPLETEPAY);
			List<String> loanNos = new ArrayList<String>();
			for(Submit submit : submits){
				List<CashStream> cs = cashStreamDao.findBySubmitAndActionAndState(submit.getId(), CashStream.ACTION_FREEZE, CashStream.STATE_SUCCESS);
				if(cs==null || cs.isEmpty()){
					throw new CheckException("已支付的投标【"+submit.getId()+"】没有对应的冻结现金流！");
				}
				loanNos.add(cs.get(0).getLoanNo());
			}
			auditBuyService.auditBuy(loanNos, 1);
		}
		}finally
		{
			releaseFinancingOrder(order);
		}
		
	}
	
//	@Override
//	@Transactional
	public void startRepayingOld(Integer orderId) throws IllegalConvertException,IllegalOperationException, ExistWaitforPaySubmitException, CheckException {
		
		GovermentOrder ord = govermentOrderDao.find(orderId);
		long financingEndtime = ord.getFinancingEndtime();
		
		
		
		
		List<Product> pts = productService.findByGovermentOrder(orderId);
		
		for(Product product:pts){
			CashStreamSum sum = cashStreamDao.sumProduct(product.getId(), CashStream.ACTION_FREEZE);
			if((sum==null||sum.getChiefAmount().negate().compareTo(product.getExpectAmount())<0) && financingEndtime>System.currentTimeMillis()){
				throw new IllegalOperationException("金额尚未融满，且融资截止时间未到，无法启动还款！");
			}
		}
		
		for(Product product:pts){
			if(!contractService.isComplete(product.getId())){
				throw new IllegalOperationException("合同尚未处理完毕，无法启动融资");
			}
		}
		
		
		GovermentOrder od = govermentOrderDao.find(orderId);
		if(od==null){
			throw new IllegalOperationException("无效订单！");
		}
		
		Borrower borrower = borrowerDao.find(od.getBorrowerId());
		if(borrower==null){
			throw new IllegalOperationException("订单没有融资方！");
		}else if(borrower.getAuthorizeTypeOpen()!=Borrower.AUTHORIZETYPEOPEN_RECHARGE){
			throw new IllegalOperationException("融资方尚未授权平台自动还款！");
		}
		
		
		
		GovermentOrder order=null;
		try
		{
			order=applyFinancingOrder(orderId);
			if(order!=null)
			{
				List<Product> products=order.getProducts();
				if(products!=null&&products.size()>0)
				{
//					throw new IllegalOperationException("还有竞标中的产品，请先修改产品状态");
					List<Product> temp=new ArrayList<Product>();
					temp.addAll(products);
					for(Product product:temp)
					{
//						productService.startRepaying(product.getId());
						// 验证是否有待付款的Submit
						int count=submitDao.countByProductAndStateWithPaged(product.getId(), Submit.STATE_WAITFORPAY);
						if(count>0)
							throw new ExistWaitforPaySubmitException("还有"+count+"个待支付的提交,请等待上述提交全部结束，稍后开始还款");
						//校验 Product实际融资额=所有Lender的支付资金流之和
						CashStreamSum sum=cashStreamDao.sumProduct(product.getId(), CashStream.ACTION_FREEZE);
						if(sum.getChiefAmount().negate().compareTo(product.getRealAmount())!=0)
							throw new CheckException("冻结提交总金额与产品实际融资金额不符");
						//从竞标缓存中移除
						productDao.changeState(product.getId(), Product.STATE_REPAYING,System.currentTimeMillis());
						product=order.findProductById(product.getId());
						order.getProducts().remove(product);
						product.setState(Product.STATE_REPAYING);
						//重新计算payback
						//删除
						payBackDao.deleteByProduct(product.getId());
						// 创建还款计划
						ProductSeries productSeries=productSeriesDao.find(product.getProductseriesId());
						List<PayBack> payBacks=innerPayBackService.generatePayBacks(product.getRealAmount().intValue(), product.getRate().doubleValue(),productSeries.getType(), order.getIncomeStarttime(), product.getIncomeEndtime());
						for(PayBack payBack:payBacks)
						{
							payBack.setBorrowerAccountId(borrower.getAccountId());
							payBack.setProductId(product.getId());
							payBackDao.create(payBack);
							
							StateLog stateLog=new StateLog();
							stateLog.setCreatetime(System.currentTimeMillis());
							stateLog.setRefid(payBack.getId());
							stateLog.setTarget(payBack.getState());
							stateLog.setType(stateLog.TYPE_PAYBACK);
							stateLogDao.create(stateLog);
						}
						Task task=new Task();
						task.setProductId(product.getId());
						task.setType(Task.TYPE_PAY);
						taskService.submit(task);
					}
				}
				innerOrderService.changeState(orderId, GovermentOrder.STATE_REPAYING);
				order=financingOrders.remove(orderId.toString());
				order.setState(GovermentOrder.STATE_REPAYING);
				
				
				Map<String, String> param = new HashMap<String, String>();
				param.put(IMessageService.PARAM_ORDER_NAME, order.getTitle());
				param.put(ILetterSendService.PARAM_TITLE, "融资完成，启动还款");
				try{
					letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_FINANCINGSUCCESS, ILetterSendService.USERTYPE_BORROWER, order.getBorrowerId(), param);
					messageService.sendMessage(IMessageService.MESSAGE_TYPE_FINANCINGSUCCESS, IMessageService.USERTYPE_BORROWER, order.getBorrowerId(), param);
				}catch(SMSException e){
					log.error(e.getMessage());
				}
			}
		}finally
		{
			releaseFinancingOrder(order);
		}
	}
	
	
	@Override
	public void reCalculateRepayPlan(Integer orderId) throws IllegalOperationException, Exception{
		
		GovermentOrder order = govermentOrderDao.find(orderId);
		if(order.getState()!=GovermentOrder.STATE_PREPUBLISH && order.getState()!=GovermentOrder.STATE_UNPUBLISH){
			throw new IllegalOperationException("当前状态下的订单无法重新计算还款计划！");
		}
		
		List<Product> products = productService.findByGovermentOrder(orderId);
		
		if(products!=null && !products.isEmpty())
		for(Product product : products)
		{
		//重新计算payback
		innerPayBackService.refreshPayBack(product.getId(), false);
		}
	}
	
	@Override
	public void quitFinancing(Integer orderId) throws IllegalConvertException, IllegalOperationException, ExistWaitforPaySubmitException, CheckException, Exception{
		GovermentOrder order=null;
		try
		{
			order=applyFinancingOrder(orderId);
			
			if(order==null){
				throw new CheckException("内存中没有对应的融资中订单！");
			}
			List<Product> pts = order.getProducts();
		
			if(pts==null || pts.isEmpty()){
				throw new CheckException("融资中订单没有找到对应的产品");
			}
			
			for(Product product:pts)
			{
				
				if(product.getState()==Product.STATE_QUITFINANCING){
					continue;
				}
				
				List<Submit> submits=submitDao.findAllByProductAndState(product.getId(), Submit.STATE_COMPLETEPAY);
				List<String> loanNos = new ArrayList<String>();
				for(Submit submit : submits){
					List<CashStream> cs = cashStreamDao.findBySubmitAndActionAndState(submit.getId(), CashStream.ACTION_FREEZE, CashStream.STATE_SUCCESS);
					if(cs==null || cs.isEmpty()){
						throw new CheckException("已支付的投标【"+submit.getId()+"】没有对应的冻结现金流！");
					}
					loanNos.add(cs.get(0).getLoanNo());
				}
				auditBuyService.auditBuy(loanNos, 2);
			}
		}finally
		{
			releaseFinancingOrder(order);
		}
	}
	
	public void quitFinancingOld(Integer orderId) throws IllegalConvertException, IllegalOperationException, ExistWaitforPaySubmitException, CheckException {
		GovermentOrder order=null;
		try
		{
			order=applyFinancingOrder(orderId);
			if(order!=null)
			{
				List<Product> products=order.getProducts();
				if(products!=null&&products.size()>0)
				{
					List<Product> temp=new ArrayList<Product>();
					temp.addAll(products);
					for(Product product:temp)
					{
						int count=submitDao.countByProductAndStateWithPaged(product.getId(), Submit.STATE_WAITFORPAY);
						if(count>0)
							throw new ExistWaitforPaySubmitException("还有"+count+"个待支付的提交,请等待上述提交全部结束，稍后开始流标");
						//校验 Product实际融资额=所有Lender的支付资金流之和
						CashStreamSum sum=cashStreamDao.sumProduct(product.getId(), CashStream.ACTION_FREEZE);
						if(sum==null){
							sum = new CashStreamSum();
						}
						if(sum.getChiefAmount().negate().compareTo(product.getRealAmount())!=0)
							throw new CheckException("冻结提交总金额与产品实际融资金额不符");
						
						productDao.changeState(product.getId(), Product.STATE_QUITFINANCING,System.currentTimeMillis());
						order.getProducts().remove(product);
						product.setState(Product.STATE_QUITFINANCING);
						Task task=new Task();
						task.setProductId(product.getId());
						task.setType(Task.TYPE_QUITFINANCING);
						taskService.submit(task);
					}
				}
				innerOrderService.changeState(orderId, GovermentOrder.STATE_QUITFINANCING);
				order=financingOrders.remove(orderId.toString());
				order.setState(GovermentOrder.STATE_QUITFINANCING);
				
				Map<String, String> param = new HashMap<String, String>();
				param.put(IMessageService.PARAM_ORDER_NAME, order.getTitle());
				param.put(ILetterSendService.PARAM_TITLE, "产品流标");
				try{
					letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_FINANCINGFAIL, ILetterSendService.USERTYPE_BORROWER, order.getBorrowerId(), param);
					messageService.sendMessage(IMessageService.MESSAGE_TYPE_FINANCINGFAIL, IMessageService.USERTYPE_BORROWER, order.getBorrowerId(), param);
				}catch(SMSException e){
					log.error(e.getMessage());
				}
			}
		}finally
		{
			releaseFinancingOrder(order);
		}
	}
	@Override
	public void closeFinancing(Integer orderId) throws IllegalConvertException {
		innerOrderService.changeState(orderId, GovermentOrder.STATE_WAITINGCLOSE);		
	}
	@Override
	public Product applyFinancingProduct(Integer productId, Integer orderId) {
		GovermentOrder order=financingOrders.get(orderId.toString());
		if(order==null)
			return null;
		order.lock.lock();
		if(order.getState()!=GovermentOrder.STATE_FINANCING)
		{
			order.lock.unlock();
			return null;
		}
		List<Product> products=order.getProducts();
		if(products==null||products.size()==0)
		{
			order.lock.unlock();
			return null;
		}
		for(Product product:products)
		{
			//Integer对象的==操作需注意 ,如:10==new Integer(10)为true,new Integer(10)==new Integer(10)为false
			if((int)productId==(int)(product.getId()))
			{
				if(product.getState()==Product.STATE_FINANCING)
					return product;
				else
				{
					order.lock.unlock();
					return null;
				}
			}
		}
		order.lock.unlock();
		return null;
	}
	@Override
	public void releaseFinancingProduct(Product product) {
		if(product==null)
			return;
		GovermentOrder order=financingOrders.get(product.getGovermentorderId().toString());
		order.lock.unlock();
	}
	public static void main(String[] args)
	{
		Integer a=new Integer(5);
		Integer b=new Integer(5);
		System.out.println(a==(int)b);
		a=new Integer(1000);
		b=new Integer(1000);
		System.out.println(a==(int)b);
	}
	@Override
	public GovermentOrder applyFinancingOrder(Integer orderId) {
		GovermentOrder order=financingOrders.get(orderId.toString());
		if(order==null)
			return null;
		order.lock.lock();
		return order;
	}
	@Override
	public void releaseFinancingOrder(GovermentOrder order) {
		if(order!=null)
			order.lock.unlock();
	}
	private void insertGovermentOrderToFinancing(GovermentOrder order)
	{
		order.lock=new ReentrantLock(true);
		order.setMaterial(null);
		financingOrders.put(order.getId().toString(), order);
		
		List<Product> products=productDao.findByGovermentOrder(order.getId());
		if(products==null||products.size()==0)
			return;
		for(Product product:products)
		{
			if(product.getState()==Product.STATE_FINANCING)
			{
				product.setAccessory(null);
				product.setGovermentOrder(order);
				order.getProducts().add(product);
			}
		}
	}
	@Override
	public Map<String, Object> findGovermentOrderByProductSeries(
			Integer productSeriesId,int states, int offset, int recnum) {
		List<Integer> list=null;
		if(states!=-1)
		{
			list=new ArrayList<Integer>();
			for(int orderState:orderStates)
			{
				if((orderState&states)>0)
					list.add(orderState);
			}
			if(list.isEmpty())
				return Pagination.buildResult(null, 0, offset, recnum);
		}
		int count=productDao.countByProductSeriesAndState(productSeriesId, null);
		if(count==0)
			return Pagination.buildResult(null, 0, offset, recnum);
		List<Product> products=productDao.findByProductSeriesAndState(productSeriesId, list, offset, recnum);
		List<GovermentOrder> orders=new ArrayList<GovermentOrder>();
		for(Product product:products)
		{
			GovermentOrder order=govermentOrderDao.find(product.getGovermentorderId());
			order.getProducts().add(product);
			orders.add(order);
		}
		return Pagination.buildResult(orders, count, offset, recnum);
	}
	@Override
	public Map<String, Object> findByStatesByPage(int states, int offset,
			int recnum) {
		List<Integer> list=null;
		if(states!=-1)
		{
			list=new ArrayList<Integer>();
			for(int orderState:orderStates)
			{
				if((orderState&states)>0)
					list.add(orderState);
			}
			if(list.isEmpty())
				return Pagination.buildResult(null, 0, offset, recnum);
		}
		int count=govermentOrderDao.countByState(list);
		List<GovermentOrder> orders=govermentOrderDao.findByStatesWithPaging(list, offset, recnum);
		if(orders!=null&&orders.size()>0)
		{
			for(GovermentOrder order:orders)
			{
				order.setProducts(productDao.findByGovermentOrder(order.getId()));
			}
		}
		return Pagination.buildResult(orders, count, offset, recnum);
	}
	@Override
	public GovermentOrder findGovermentOrderByProduct(Integer productId) {
		Product product=productDao.find(productId);
		GovermentOrder order=govermentOrderDao.find(product.getGovermentorderId());
		order.setProducts(productDao.findByGovermentOrder(order.getId()));
		return order;
	}
	@Override
	@Transactional
	public void closeComplete(Integer orderId) throws IllegalConvertException {
		innerOrderService.changeState(orderId, GovermentOrder.STATE_CLOSE);
		GovermentOrder order=govermentOrderDao.find(orderId);
		List<Product> products=productDao.findByGovermentOrder(orderId);
		int creditValue=0;
		for(Product product:products)
		{
			creditValue+=product.getRealAmount().intValue();
		}
		if(creditValue>0)
		{
			Borrower borrower=borrowerDao.find(order.getBorrowerId());
			borrowerDao.changeCreditValueAndLevel(borrower.getId(), borrower.getCreditValue()+creditValue, Borrower.creditValueToLevel(borrower.getCreditValue()+creditValue));
		}
	}
	
	public void addAccessory(Integer orderId,int category,MimeItem item) throws XMLParseException
	{
		checkChangeGovermentOrder(orderId);
		String text=govermentOrderDao.findAccessory(orderId);
		Accessory accessory=null;
		if(StringUtil.isEmpty(text))
			accessory=new Accessory();
		else {
			accessory=xmlTransformer.parse(text, Accessory.class);
		}
		if(accessory.getCols()==null)
			accessory.setCols(new ArrayList<Accessory.MimeCol>());
		MimeCol col=accessory.findMimeCol(category);
		if(col==null)
		{
			col=new MimeCol();
			col.setCategory(category);
			accessory.getCols().add(col);
		}
		if(col.getItems()==null)
			col.setItems(new ArrayList<Accessory.MimeItem>());
		col.getItems().add(item);
		text=xmlTransformer.export(accessory);
		govermentOrderDao.updateAccessory(orderId, text);
	}
	@Override
	public void delAccessory(Integer orderId, int category, String itemId)
			throws XMLParseException {
		checkChangeGovermentOrder(orderId);
		String text=govermentOrderDao.findAccessory(orderId);
		if(StringUtil.isEmpty(text))
			return;
		Accessory accessory=xmlTransformer.parse(text, Accessory.class);
		if(accessory.getCols()==null)
			return;
		MimeCol col=accessory.findMimeCol(category);
		if(col==null)
			return;
		List<MimeItem> items=col.getItems();
		if(items==null||items.size()==0)
			return;
		for(int i=0;i<items.size();i++)
		{
			if(items.get(i).getId().equals(itemId))
			{
				items.remove(i);
				break;
			}
		}
		text=xmlTransformer.export(accessory);
		govermentOrderDao.updateAccessory(orderId, text);
	}
	@Override
	public List<MimeItem> findMimeItems(Integer orderId, int category)
			throws XMLParseException {
		String text=govermentOrderDao.findAccessory(orderId);
		if(StringUtil.isEmpty(text))
			return new ArrayList<Accessory.MimeItem>(0);
		Accessory accessory=xmlTransformer.parse(text, Accessory.class);
		MimeCol col=accessory.findMimeCol(category);
		if(col==null)
			return new ArrayList<Accessory.MimeItem>(0);
		return col.getItems();
	}
	
	@Override
	public List<GovermentOrder> findBorrowerOrderByStates(int states) {
		Borrower borrower=borrowerService.getCurrentUser();
		return findByBorrowerIdAndStates(borrower.getId(), states);
	}
	@Override
	public List<FinancingRequest> findBorrowerFinancingRequest(int state) {
		Borrower borrower=borrowerService.getCurrentUser();
		List<FinancingRequest> requests=financingRequestDao.findByBorrowerAndState(borrower.getId(), state);
		if(requests==null||requests.size()==0)
			return requests;
		for(FinancingRequest request:requests)
		{
			if(request.getState()==FinancingRequest.STATE_PROCESSED)
				request.setGovermentOrder(govermentOrderDao.findByFinancingRequest(request.getId()));
		}
		return requests;
	}
	@Override
	@Transactional
	public void publish(Integer orderId) throws IllegalConvertException {
		checkNullObject("orderId", orderId);
		GovermentOrder order=checkNullObject(GovermentOrder.class, govermentOrderDao.find(orderId));
		innerOrderService.changeState(orderId, GovermentOrder.STATE_PREPUBLISH);
		financingRequestDao.changeState(order.getFinancingRequestId(), FinancingRequest.STATE_PROCESSED, System.currentTimeMillis());
		List<Product> products=productDao.findByGovermentOrder(orderId);
		if(products==null||products.size()==0)
			return;
		for(Product product:products)
		{
			productDao.changeState(product.getId(), Product.STATE_FINANCING, System.currentTimeMillis());
		}
	}
	private void checkChangeGovermentOrder(Integer orderId)
	{
		GovermentOrder order=checkNullObject(GovermentOrder.class, govermentOrderDao.find(orderId));
		if(order.getState()!=GovermentOrder.STATE_UNPUBLISH && order.getState()!=GovermentOrder.STATE_PREPUBLISH)
			throw new RuntimeException("订单已发布，不能再修改");
	}
	@Override
	public List<GovermentOrder> findAllUnpublishOrders() {
		return govermentOrderDao.findAllUnpublishOrders();
	}
	@Override
	public void update(Integer id,String title,long financingStarttime,long financingEndtime,long incomeStarttime,String description,String formalName, String formalLevel, String formalAmount, String tenderUnits, String formalLink){
		govermentOrderDao.update(id,title, financingStarttime, financingEndtime, incomeStarttime, description, formalName, formalLevel, formalAmount, tenderUnits, formalLink);
	}
}
