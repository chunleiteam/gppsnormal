package gpps.service;

import java.util.List;

import gpps.model.Subscribe;
import gpps.service.exception.InsufficientBalanceException;
import gpps.service.exception.InsufficientProductException;
import gpps.service.exception.SMSException;
import gpps.service.exception.SubscribeException;
import gpps.service.exception.UnreachBuyLevelException;

public interface ISubscribeService {
	/**
	 * 预约产品,投资人调用, 投资人信息从Session中获取
	 * 
	 * @param productId
	 *            产品名称
	 * @param amount
	 *            预约数量
	 * @throws InsufficientBalanceException
	 *             余额不足
	 * @throws InsufficientProductException
	 *             产品无法预约
	 * @throws UnreachBuyLevelException
	 *             未达购买级别
	 *             
	 * @return 返回订单号
	 */
	public void subscribe(Integer productId, int amount) throws InsufficientBalanceException, InsufficientProductException, UnreachBuyLevelException;
	
	public int countSubscribe(Integer productId, int state);
	
	public void audit(Integer productId, List<SubscribeAudit> subs) throws SubscribeException, SMSException;
	
	public boolean isAudited(Integer productId) throws SubscribeException;
	
	public List<Subscribe> findAllByProductIdAndState(Integer productId, int state);
}
