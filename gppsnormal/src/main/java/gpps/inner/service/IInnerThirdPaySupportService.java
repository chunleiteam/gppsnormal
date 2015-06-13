package gpps.inner.service;

import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.ResultCodeException;

import java.security.SignatureException;
import java.util.List;
import java.util.Map;

public interface IInnerThirdPaySupportService {
	public static final String ACTION_REGISTACCOUNT="0";
	public static final String ACTION_RECHARGE="1";
	public static final String ACTION_TRANSFER="2";
	public static final String ACTION_CHECK="3";
	public static final String ACTION_CARDBINDING="4";
	public static final String ACTION_CASH="5";
	public static final String ACTION_AUTHORIZE="6";
	public static final String ACTION_ORDERQUERY="7";
	public static final String ACTION_BALANCEQUERY="8";
	
	
	/**
	 * 根据行为获取url
	 * @param action
	 * @return
	 */
	public String getBaseUrl(String action);
	/**
	 * 返回第三方平台账户,内部调用 
	 * @return
	 */
	public String getPlatformMoneymoremore();
	/**
	 * 返回第三方私钥，内部调用
	 * @return
	 */
	public String getPrivateKey();
	
	public String getServerHost();
	
	public String getServerPort();
	
	public String getPublicKey();
	
	public String getReturnUrl();
	
	public String getNotifyUrl();
	
	public String getAppendFlag();
	
	/**
	 * 审核操作参数签名
	 * 
	 * */
	public String signForAudit(Map<String,String> params, String privateKey);
	
	/**
	 * 处理审核返回的参数
	 * 
	 * */
	public List<String> handleAuditReturnParams(Map<String, String> returnParams)
			throws AlreadyDoneException, ResultCodeException,
			SignatureException;
	
	/**
	 * 校验审核返回的参数
	 * 
	 * */
	public void checkAuditReturnParams(Map<String, String> params)
			throws AlreadyDoneException, ResultCodeException,
			SignatureException;
	
	/**
	 * 校验申请转账时返回的参数
	 * 
	 * */
	public void checkTransferReturnParams(Map<String,String> params) throws AlreadyDoneException, ResultCodeException, SignatureException;
}
