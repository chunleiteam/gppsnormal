package gpps.service.impl;

import gpps.dao.IGovermentOrderDao;
import gpps.dao.IProductDao;
import gpps.dao.IProductSeriesDao;
import gpps.dao.ISubmitDao;
import gpps.dao.ISubscribeDao;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.Submit;
import gpps.model.Subscribe;
import gpps.service.ILenderService;
import gpps.service.ISubscribeService;
import gpps.service.SubscribeAudit;
import gpps.service.exception.InsufficientBalanceException;
import gpps.service.exception.InsufficientProductException;
import gpps.service.exception.SMSException;
import gpps.service.exception.SubscribeException;
import gpps.service.exception.UnreachBuyLevelException;
import gpps.service.message.IMessageSupportService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscribeServiceImpl implements ISubscribeService {
@Autowired
ISubscribeDao subscribeDao;
@Autowired
ILenderService lenderService;
@Autowired
IProductDao productDao;
@Autowired
IGovermentOrderDao orderDao;
@Autowired
IProductSeriesDao productSeriesDao;
@Autowired
ISubmitDao submitDao;
@Autowired
IMessageSupportService messageService;
	@Override
	public void subscribe(Integer productId, int amount)
			throws InsufficientBalanceException, InsufficientProductException, UnreachBuyLevelException {
		Product product = productDao.find(productId);
		
		if(amount>product.getExpectAmount().intValue()){
			throw new InsufficientBalanceException("预约金额大于产品预计融资金额");
		}
		
		if(amount<product.getMinimum()){
			throw new InsufficientBalanceException("预约金额小于产品要求最小金额");
		}
		
		if((amount-product.getMinimum())%product.getMiniAdd()!=0){
			throw new InsufficientBalanceException("产品要求递增金额为:"+product.getMiniAdd());
		}
		
		GovermentOrder order = orderDao.find(product.getGovermentorderId());
		
		if(order.getState()!=GovermentOrder.STATE_PREPUBLISH){
			throw new InsufficientProductException("产品不是预发布状态，无法预约");
		}
		
		
		//如果距离融资启动时间小于13小时以内
		if(order.getFinancingStarttime()-(new Date()).getTime()<=13L*3600*1000){
			throw new InsufficientProductException("已超过预约截止时间，无法预约");
		}
		
		Lender lender=lenderService.getCurrentUser();
		lender=lenderService.find(lender.getId());
		
		if(lender.getIdentityCard()==null || "".equals(lender.getIdentityCard())){
			throw new UnreachBuyLevelException("您尚未在平台上实名认证，请先实名认证再执行预约！");
		}
		
		if(lender.getThirdPartyAccount()==null || "".equals(lender.getThirdPartyAccount())){
			throw new UnreachBuyLevelException("您尚未注册第三方账户，请先注册第三方账户再执行预约！");
		}
		
		if(lender.getLevel()<product.getLevelToBuy()){
			throw new UnreachBuyLevelException("您的级别不够，无法预约该产品");
		}
		
		int doneCount = subscribeDao.countByProductIdAndLenderAndState(productId, lender.getId(), Subscribe.STATE_APPLY);
		if(doneCount>=3){
			throw new InsufficientProductException("本产品您已预约超过三次，不能再次预约，请等待审核");
		}
		
		Subscribe subscribe = new Subscribe();
		subscribe.setApplyAmount(amount);
		subscribe.setCreateTime((new Date()).getTime());
		subscribe.setLenderId(lender.getId());
		subscribe.setProductId(productId);
		subscribe.setState(Subscribe.STATE_APPLY);
		subscribeDao.create(subscribe);
	}

	@Override
	public int countSubscribe(Integer productId, int state) {
		int count = subscribeDao.countByProductIdAndState(productId, state);
		return count;
	}
	
	
	@Override
	public boolean isAudited(Integer productId) throws SubscribeException{
		Product product = productDao.find(productId);
		if(product==null){
			throw new SubscribeException("产品【"+productId+"】不存在");
		}
		
		GovermentOrder order = orderDao.find(product.getGovermentorderId());
		
		List<Subscribe> unaudited = subscribeDao.findAllByProductIdAndState(productId, Subscribe.STATE_APPLY);
		
		long subscribeEndTime = order.getFinancingStarttime() - Subscribe.SUBSCRIBE_END_TIME_INTERVAL*3600*1000;  //融资起始前13小时
		
		if((unaudited==null || unaudited.isEmpty()) && System.currentTimeMillis()>subscribeEndTime){
			return true;
		}
		return false;
	}
	
	public List<Subscribe> findAllByProductIdAndState(Integer productId, int state){
		List<Subscribe> res = null;
		res = subscribeDao.findAllByProductIdAndState(productId, state);
		if(res==null){
			return new ArrayList<Subscribe>();
		}
		for(Subscribe sub : res){
			sub.setLender(lenderService.find(sub.getLenderId()));
		}
		return res;
	}
	
	@Override
	public void audit(Integer productId, List<SubscribeAudit> subs) throws SubscribeException, SMSException{
		
		if(subs==null || subs.isEmpty()){
			return;
		}
		
		Product product = productDao.find(productId);
		if(product==null){
			throw new SubscribeException("产品【"+productId+"】不存在");
		}
		GovermentOrder order = orderDao.find(product.getGovermentorderId());
		if(order==null){
			throw new SubscribeException("产品【"+productId+"】所属订单不存在");
		}
		if(order.getState()!=GovermentOrder.STATE_PREPUBLISH){
			throw new SubscribeException("产品状态不是预约中，无法审核");
		}
		long now = System.currentTimeMillis();
		long jiezhi = order.getFinancingStarttime()-13L*3600*1000;
		if(now<jiezhi){
			throw new SubscribeException("产品预约期尚未结束，无法审核");
		}
		List<Subscribe> dbsubs = subscribeDao.findAllByProductIdAndState(productId, Subscribe.STATE_APPLY);
		if(subs.size()!=dbsubs.size()){
			throw new SubscribeException("审核的预约个数与数据库中待审核预约个数不相符，无法审核");
		}
		
		HashSet<Integer> ids = new HashSet<Integer>();
		
		for(Subscribe sub : dbsubs){
			ids.add(sub.getId());
		}
		BigDecimal total = new BigDecimal(0);
		for(SubscribeAudit sub : subs){
			if(!ids.contains(sub.getSubsribeId())){
				throw new SubscribeException("预约审核ID【"+sub.getSubsribeId()+"】无法在库中找到对应的预约，无法审核");
			}
			
			int state = Subscribe.STATE_APPLY;
			if(sub.getAuditAmount()<=0){
				state = Subscribe.STATE_CONFIRM_REFUSE;
			}else if(sub.getAuditAmount()==sub.getApplyAmount()){
				state = Subscribe.STATE_CONFIRM_PASS_ALL;
			}else{
				state = Subscribe.STATE_CONFIRM_PASS_MODIFY;
			}
			
			if(state==Subscribe.STATE_APPLY){
				throw new SubscribeException("预约审核ID【"+sub.getSubsribeId()+"】没有给出审核意见，无法审核");
			}
			
			if(state==Subscribe.STATE_CONFIRM_PASS_ALL || state==Subscribe.STATE_CONFIRM_PASS_MODIFY){
				total = total.add(new BigDecimal(sub.getAuditAmount()));
			}
		}
		
		if(total.compareTo(product.getExpectAmount())>0){
			throw new SubscribeException("通过审核的预约总金额大于产品融资额度，无法审核");
		}
		
		ProductSeries pseries = productSeriesDao.find(product.getProductseriesId());
		
		
		BigDecimal realAmount = new BigDecimal(0);
		Map<Integer, SubscribeMessage> lenderSubMessages = new HashMap<Integer, SubscribeMessage>();
		for(SubscribeAudit sub : subs){
			int state = Subscribe.STATE_APPLY;
			if(sub.getAuditAmount()<=0){
				state = Subscribe.STATE_CONFIRM_REFUSE;
			}else if(sub.getAuditAmount()==sub.getApplyAmount()){
				state = Subscribe.STATE_CONFIRM_PASS_ALL;
			}else{
				state = Subscribe.STATE_CONFIRM_PASS_MODIFY;
			}
			
			SubscribeMessage sm = null;
			if(lenderSubMessages.containsKey(sub.getLenderId())){
				sm = lenderSubMessages.get(sub.getLenderId());
			}else{
				sm = new SubscribeMessage(sub.getLenderId());
				lenderSubMessages.put(sub.getLenderId(), sm);
			}
			
			Subscribe dbsub = subscribeDao.find(sub.getSubsribeId());
			sm.addApplyAmount(dbsub.getApplyAmount());
			
			if(state == Subscribe.STATE_CONFIRM_REFUSE){
				subscribeDao.changeState(sub.getSubsribeId(), jiezhi, state, 0, "");
			}else{
				Submit submit = new Submit();
				submit.setAmount(new BigDecimal(sub.getAuditAmount()));
				submit.setCreatetime(jiezhi);
				submit.setLenderId(sub.getLenderId());
				submit.setProductId(productId);
				submit.setState(Submit.STATE_SUBSCRIBE_WAITFORPAY);
				submitDao.create(submit);
				subscribeDao.changeState(sub.getSubsribeId(), jiezhi, state, sub.getAuditAmount(), "");
				realAmount = realAmount.add(submit.getAmount());
				sm.addRealAmount(sub.getAuditAmount());
			}
		}
		
		for(Integer lenderId : lenderSubMessages.keySet()){
			Lender lender = lenderService.find(lenderId);
			SubscribeMessage sm = lenderSubMessages.get(lenderId);
			List<String> tels = new ArrayList<String>();
			tels.add(lender.getTel());
			if(sm.refuse==true){
				messageService.sendSMS(tels, "【春蕾政采贷】尊敬的客户，非常抱歉的通知您，由于"+order.getTitle()+"【"+pseries.getTitle()+"】预约额度已满，您的投标预约未审核通过，由此给您带来的不便敬请谅解。");
			}else{
				messageService.sendSMS(tels,"【春蕾政采贷】尊敬的客户，您预约的"+order.getTitle()+"【"+pseries.getTitle()+"】已经审核完毕，您申请预约的总额度为"+sm.applyAmount.intValue()+"元，审核通过的总额度为"+sm.realAmount.intValue()+"元。请您在12小时内登录我的账户完成支付，否则分配给您的额度将会退回。");
			}
		}
		//把所有审核通过的额度加起来，保存到产品的realAmount中去，如果超时未支付，则额度回滚。
		productDao.buy(product.getId(), realAmount);
	}

}

class SubscribeMessage{
	Integer lenderId;
	BigDecimal applyAmount = BigDecimal.ZERO;
	BigDecimal realAmount = BigDecimal.ZERO;
	boolean refuse = true;
	public SubscribeMessage(Integer lenderId){
		this.lenderId = lenderId;
	}
	public void addApplyAmount(int amount){
		this.applyAmount = this.applyAmount.add(new BigDecimal(amount));
	}
	public void addRealAmount(int amount){
		this.realAmount = this.realAmount.add(new BigDecimal(amount));
		if(this.realAmount.compareTo(BigDecimal.ZERO)>0){
			refuse = false;
		}
	}
}
