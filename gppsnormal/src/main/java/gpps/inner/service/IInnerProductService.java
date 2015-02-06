package gpps.inner.service;

import gpps.model.Product;
import gpps.service.exception.IllegalConvertException;

public interface IInnerProductService {
	/**
	 * 修改产品的状态，包括校验状态转换是否合理，以及创建状态转换日志
	 * @param orderId
	 * @param state
	 * 
	 * @throws IllegalConvertException
	 * 
	 * */
	public void changeState(int productId, int state) throws IllegalConvertException;
	
	/**
	 * 创建产品，并执行相应的附带操作：创建状态转换日志、写日志
	 * @param product
	 * 
	 * @throws IllegalArgumentException
	 * */
	public void create(Product product) throws IllegalArgumentException;
	
	/**
	 * 将产品状态从融资中修改为还款中，并执行相应的附带操作：创建状态转换日志、写日志、判断所在的订单对应的产品是否全部改变完毕，如果完毕的话调用innerorderservice修改订单状态为还款中
	 * 
	 * @param productId
	 * 
	 * */
	public void startRepaying(int productId) throws IllegalConvertException;
	
	/**
	 * 将产品状态从融资中修改为流标，并执行相应的附带操作：创建状态转换日志、写日志、判断所在的订单对应的产品是否全部改变完毕，如果完毕的话调用innerorderservice修改订单状态为流标
	 * 
	 * @param productId
	 * 
	 * */
	public void quitFinancing(int productId) throws IllegalConvertException;
	
	/**
	 * 将产品状态从还款中改为还款完毕，并执行相应的附带操作：创建状态转换日志、写日志、判断所在订单对应的产品是否全部还款完毕，如果完毕的话调用innerorderservice修改订单状态为待关闭
	 * 
	 * */
	public void finishRepay(int productId) throws IllegalConvertException;
}
