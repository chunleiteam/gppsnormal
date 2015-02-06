package gpps.inner.service;

import gpps.model.CashStream;
import gpps.model.PayBack;
import gpps.model.ProductSeries;
import gpps.service.exception.CheckException;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.IllegalOperationException;
import gpps.tools.SinglePayBack;

import java.util.List;

public interface IInnerPayBackService {
	/**
	 * 单纯生成还款计划，并不持久化
	 * @param amount 贷款金额
	 * @param rate 年利率
	 * @param payBackModel 还款模型{@link ProductSeries.type}
	 * @param from 起始时间
	 * @param to 结束时间
	 * @return
	 */
	public List<PayBack> generatePayBacks(int amount,double rate,int payBackModel, long from, long to);
	
	/**
	 * 重新生成还款计划，将原来的还款计划删除，重新计算还款计划并持久化，记录相应的状态转换及日志
	 * @param productId
	 * 
	 * */
	public void refreshPayBack(int productId, boolean startrepay) throws Exception;
	
	public void validatePayBackSequence(int payBackId) throws IllegalOperationException;
	
	
	/**
	 * 找到产品对应的所有还款实体
	 * @param productId
	 * 
	 * */
	public List<PayBack> findAll(Integer productId);
	
	/**
	 * 修改还款的状态，以及后续的操作：状态转换记录及日志
	 * 
	 * @param paybackId
	 * @param state
	 * 
	 * */
	public void changeState(Integer paybackId,int state);
	
	
	/**
	 * 计算某一次还款应该给对应的各个投资者各多少钱，根据还款所对应的产品的投标情况进行计算，前面的人是靠比例算出来的，最后一个人是靠减出来的
	 * 根据payBackId计算出本次还款的具体详情
	 * @param payBackId
	 * 
	 * */
	public List<SinglePayBack> calculatePayBacks(Integer payBackId) throws CheckException;
	
	/**
	 * 单纯的对还款计划进行校验，并返回校验报告
	 * @param payBackId
	 * @param executeStep 生成日志和报告时，显示校验执行步骤
	 * 
	 * 
	 * */
	public List<SinglePayBack> justCheckOutPayBackById(Integer payBackId, String executeStep) throws CheckException;

	
	/**
	 * 单纯的对用cashstream表现出的回款计划进行校验，并返回校验报告
	 * @param css
	 * @param payBackId
	 * @param executeStep 生成日志和报告时，显示校验执行步骤
	 * 
	 * */
	public void justCheckOutPayBackByCS(List<CashStream> css, Integer payBackId, String executeStep) throws CheckException;
	
	
	/**
	 * 单纯的对用SinglePayBack表现出的回款计划进行校验，并返回校验报告
	 * @param spbs
	 * @param payBackId
	 * @param executeStep 生成日志和报告时，显示校验执行步骤
	 * 
	 * */
	public List<SinglePayBack> justCheckOutPayBackBySPB(List<SinglePayBack> spbs, Integer payBackId, String executeStep) throws CheckException;
	
	/**
	 * 单笔还款执行完毕，以及后续的操作：创建状态转换日志、写日志、发送短信与站内信、判断是否是产品的最后一笔还款，如果是的话调用innerProductService修改产品状态为“还款完毕”
	 * 
	 * @param payBackId
	 * 
	 * */
	public void finishPayBack(Integer payBackId) throws IllegalConvertException;
}
