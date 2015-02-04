package gpps.inner.service;

import gpps.model.PayBack;
import gpps.model.ProductSeries;

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
}
