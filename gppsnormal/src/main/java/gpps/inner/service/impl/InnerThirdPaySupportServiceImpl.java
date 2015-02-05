package gpps.inner.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import gpps.inner.service.IInnerThirdPaySupportService;

public class InnerThirdPaySupportServiceImpl implements IInnerThirdPaySupportService {
	private static Map<String, String> urls=new HashMap<String, String>();
	static {
		urls.put(ACTION_REGISTACCOUNT, "/loan/toloanregisterbind.action");
		urls.put(ACTION_RECHARGE, "/loan/toloanrecharge.action");
		urls.put(ACTION_TRANSFER, "/loan/loan.action");
		urls.put(ACTION_CHECK, "/loan/toloantransferaudit.action");
		urls.put(ACTION_CARDBINDING, "/loan/toloanfastpay.action");
		urls.put(ACTION_CASH, "/loan/toloanwithdraws.action");
		urls.put(ACTION_AUTHORIZE, "/loan/toloanauthorize.action");
		urls.put(ACTION_ORDERQUERY, "/loan/loanorderquery.action");
		urls.put(ACTION_BALANCEQUERY, "/loan/balancequery.action");
	}
	private String url="";
	private String platformMoneymoremore;
	private String privateKey;
	private String publicKey;
	private String serverHost;
	private String serverPort;
	
	@Override
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getBaseUrl(String action) {
		return url+urls.get(action);
	}
	
	public String getPlatformMoneymoremore() {
		return platformMoneymoremore;
	}

	public void setPlatformMoneymoremore(String platformMoneymoremore) {
		this.platformMoneymoremore = platformMoneymoremore;
	}
	@Override
	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}
	@Override
	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	@Override
	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
}
