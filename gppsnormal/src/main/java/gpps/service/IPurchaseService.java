package gpps.service;

import java.math.BigDecimal;

/**
 * 债权的回购及购买服务，允许用户卖回或者买入债权
 * 
 * 
 * */
public interface IPurchaseService {
	public static final BigDecimal AFTER_PURCHASE_BACK_RATE_JH = new BigDecimal("0.12");
	public static final BigDecimal AFTER_PURCHASE_BACK_RATE_JQ = new BigDecimal("0.10");
	
	
	/**
	 * 判断该笔投资是否可申请回购
	 * @param submitId
	 * @param date
	 * */
	public void canApplyPurchaseBack(Integer submitId, long date) throws Exception;
	
	
	/**
	 * 判断该笔投资是否可购买
	 * @param submitId
	 * @param date
	 * */
	public void canApplyPurchase(Integer submitId, long date) throws Exception;
	
	
	
	
	/**
	 * 用户申请回购债权的时候，预先计算一下整个的收益情况，包括手续费是多少等等
	 * @param submitId
	 * @param date
	 * @return PurchaseBackCalculateResult
	 * */
	public PurchaseBackCalculateResult preCalPurchaseBack(Integer submitId, long date) throws Exception;
	
	
	
	/**
	 * 用户购买债权的时候，预先计算一下整个的债权的情况
	 * @param submitId
	 * @param date
	 * @return PurchaseCalculateResult
	 * */
	public PurchaseCalculateResult preCalPurchase(Integer submitid, long date) throws Exception;
	
	
	
	
	
	/**
	 * 用户申请企业回购自己持有的“还款中”的债权
	 * @param submitId
	 * 
	 * */
	public void applyPurchaseBack(Integer submitId) throws Exception;
	
	/**
	 * 企业实际执行“回购”
	 * @param submitId
	 * 
	 * */
	public void purchaseBack(Integer submitId, Integer borrowerId) throws Exception;
	
	
	public void synchronizeAccount(Integer borrowerId) throws Exception;
	
	
	
	
	/**
	 * 用户购买回头企业持有的债权
	 * @param submitId
	 * 
	 * */
	public Integer purchase(Integer submitId) throws Exception;
	
}
