package gpps.service;

import gpps.model.CashStream;
import gpps.model.PayBack;
import gpps.model.ProductSeries;
import gpps.service.exception.CheckException;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.IllegalOperationException;
import gpps.service.exception.InsufficientBalanceException;
import gpps.service.exception.UnSupportRepayInAdvanceException;
import gpps.tools.SinglePayBack;

import java.util.List;
import java.util.Map;

public interface IPayBackService {
	public void create(PayBack payback);
	public void changeState(Integer paybackId,int state);
	public PayBack find(Integer id);
//	public List<PayBackDetail> getMyPaybackDetail(int paybackState);
	/**
	 * 申请延期
	 * @param payBackId
	 * @param delayTo
	 * @return
	 * @throws UnSupportDelayException
	 */
	public void applyRepayInAdvance(Integer payBackId) throws UnSupportRepayInAdvanceException;
	/**
	 * 设置payback 延时
	 * @param payBackId
	 */
	public void delay(Integer payBackId);
	
	/**
	 * 根据产品生成还款计划
	 * @param productId
	 * @param amount
	 * @return
	 */
	public List<PayBack> generatePayBacks(Integer productId,int amount);
	
	/**
	 * 根据投标生成还款详情
	 * 		已还款的按实际还款，未还款的新计算出来
	 * @param submitId
	 * @return
	 * */
	public List<PayBack> generatePayBacksBySubmit(Integer submitId);
	
	/**
	 * 获取当前借款人的还款
	 * @param state 还款状态，-1为不限
	 * @param starttime 开始时间，-1为不限
	 * @param endtime 截止时间，-1为不限
	 * @return
	 */
	public Map<String, Object> findBorrowerPayBacks(int state,long starttime,long endtime,int offset,int recnum);
	/**
	 * 返回当前借款人所有的可还款
	 * @return
	 */
	public List<PayBack> findBorrowerCanBeRepayedPayBacks();
	/**
	 * 返回当前借款人所有的可提前还款
	 * @return
	 */
	public List<PayBack> findBorrowerCanBeRepayedInAdvancePayBacks();
	/**
	 * 返回当前借款人所有的待还款
	 * @return
	 */
	public List<PayBack> findBorrowerWaitForRepayed();
	/**
	 * 该payback是否可还
	 * @param payBackId
	 * @return
	 */
	public boolean canRepay(Integer payBackId);
	/**
	 * 该payback是否可提前还
	 * @param payBackId
	 * @return
	 */
	public boolean canRepayInAdvance(Integer payBackId);
	/**
	 * 借款人还款
	 * @param payBackId
	 * @throws IllegalStateException
	 * @throws IllegalOperationException
	 * @throws InsufficientBalanceException
	 * @throws IllegalConvertException
	 */
	public void repay(Integer payBackId) throws IllegalStateException, IllegalOperationException, InsufficientBalanceException, IllegalConvertException, CheckException;
	/**
	 * 管理员验证通过还款
	 * @param payBackId
	 * @throws IllegalConvertException
	 * @throws IllegalOperationException
	 */
	public void check(Integer payBackId) throws IllegalConvertException, IllegalOperationException;
	/**
	 * 对该次还款进行校验,校验通过才可调用check方法
	 * @param payBackId
	 * @throws CheckException
	 */
	public List<SinglePayBack> checkoutPayBack(Integer payBackId) throws CheckException;
	/**
	 * 返回所有等待审核的还款
	 * @return
	 */
	public List<PayBack> findWaitforCheckPayBacks();
	
	/**
	 * 查询剩余指定天数以内的待还款
	 * 
	 * */
	public List<PayBackToView> getWaitingForPayBacksByLeftDays(int leftDays) throws Exception;
}
