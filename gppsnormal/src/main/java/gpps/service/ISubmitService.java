package gpps.service;

import gpps.model.CashStream;
import gpps.model.PayBack;
import gpps.model.Submit;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.InsufficientBalanceException;
import gpps.service.exception.InsufficientProductException;
import gpps.service.exception.ProductSoldOutException;
import gpps.service.exception.UnreachBuyLevelException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ISubmitService {
	/**
	 * 购买产品,贷款人调用， 贷款人信息从Session中获取
	 * 
	 * @param productId
	 *            产品名称
	 * @param amount
	 *            购买数量
	 * @throws InsufficientBalanceException
	 *             余额不足
	 * @throws ProductSoldOutException
	 *             产品已售完
	 * @throws InsufficientProductException
	 *             产品余额不足
	 * @throws UnreachBuyLevelException
	 *             未达购买级别
	 *             
	 * @return 返回订单号
	 */
	public Integer buy(Integer productId, int amount) throws InsufficientBalanceException, ProductSoldOutException, InsufficientProductException, UnreachBuyLevelException;
	/**
	 * 购买完成后确认提交已购买
	 * @param submitId
	 * @throws IllegalConvertException
	 */
	public void confirmBuy(Integer submitId) throws IllegalConvertException;
	/**
	 * 管理员购买,不受融资时间限制
	 * @param productId
	 * @param amount
	 * @return
	 * @throws InsufficientBalanceException
	 * @throws ProductSoldOutException
	 * @throws InsufficientProductException
	 */
	public Integer buyByAdmin(Integer productId,int amount) throws InsufficientBalanceException, ProductSoldOutException,InsufficientProductException;

	/**
	 * 系统任务调用
	 * 
	 * @param submitId
	 *            订单ID
	 * @param state
	 *            订单状态
	 * @throws IllegalConvertException
	 */
	// public void changeState(Integer submitId,int state)throws
	// IllegalConvertException;

	public Submit find(Integer id);

	/**
	 * 返回用户购买的所有订单，按时间倒序排序
	 * 
	 * @return
	 */
	public Map<String, Object> findMyAllSubmits(int offset, int recnum);
	/**
	 * 返回用户所有的待支付提交
	 * @return
	 */
	public List<Submit> findMyAllWaitforPayingSubmits();
	/**
	 * 返回用户所有的退订提交
	 * @return
	 */
	public List<Submit> findMyAllRetreatSubmits();
	/**
	 * 根据产品状态返回用户提交
	 * @param productStates
	 * @param offset
	 * @param recnum
	 * @return
	 */
	public Map<String, Object> findMyAllSubmitsByProductStates(int productStates,int offset,int recnum);

	/**
	 * 找到该订单的资金流,倒序排列返回
	 * 
	 * @param submit
	 * @return
	 */
	public List<CashStream> findSubmitCashStream(Integer submitId);
	/**
	 * 返回某产品已支付的所有提交
	 * @param productId
	 * @param offset
	 * @param recnum
	 * @return
	 */
	public Map<String, Object> findPayedSubmitsByProduct(Integer productId,int offset, int recnum);
	// public void refuseApply(Integer submitId)throws
	// IllegalConvertException;//拒绝申请
	// public void passApply(Integer submitId)throws
	// IllegalConvertException;//通过申请进行待支付

}
