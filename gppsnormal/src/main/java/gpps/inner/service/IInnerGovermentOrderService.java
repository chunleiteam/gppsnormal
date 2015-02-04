package gpps.inner.service;

import gpps.service.exception.IllegalConvertException;

public interface IInnerGovermentOrderService {
	/**
	 * 修改订单的状态，包括校验状态转换是否合理，以及创建状态转换日志
	 * @param orderId
	 * @param state
	 * 
	 * @throws IllegalConvertException
	 * 
	 * */
	public void changeState(int orderId, int state) throws IllegalConvertException;
	
	
	/**
	 * 将订单状态从融资中修改为还款中，并执行相应的附带操作：创建状态转换日志、写日志、发送短信和站内信
	 * 
	 * @param productId
	 * 
	 * */
	public void startRepaying(int orderId) throws IllegalConvertException;
}
