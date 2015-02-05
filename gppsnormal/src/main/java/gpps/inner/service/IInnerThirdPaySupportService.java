package gpps.inner.service;

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
}
