package gpps.inner.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import gpps.inner.service.IInnerThirdPaySupportService;
import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.ResultCodeException;
import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

public class InnerThirdPaySupportServiceImpl implements IInnerThirdPaySupportService {
//	private static Map<String, String> urls=new HashMap<String, String>();
//	static {
//		urls.put(ACTION_REGISTACCOUNT, "/loan/toloanregisterbind.action");
//		urls.put(ACTION_RECHARGE, "/loan/toloanrecharge.action");
//		urls.put(ACTION_TRANSFER, "/loan/loan.action");
//		urls.put(ACTION_CHECK, "/loan/toloantransferaudit.action");
//		urls.put(ACTION_CARDBINDING, "/loan/toloanfastpay.action");
//		urls.put(ACTION_CASH, "/loan/toloanwithdraws.action");
//		urls.put(ACTION_AUTHORIZE, "/loan/toloanauthorize.action");
//		urls.put(ACTION_ORDERQUERY, "/loan/loanorderquery.action");
//		urls.put(ACTION_BALANCEQUERY, "/loan/balancequery.action");
//	}
	private String url="";
	private String platformMoneymoremore;
	private String privateKey;
	private String publicKey;
	private String serverHost;
	private String serverPort;
	
	private String urlregister;
	private String urlrecharge;
	private String urltransfer;
	private String urlcheck;
	private String urlcardbinding;
	private String urlcash;
	private String urlauthorize;
	private String urlorderquery;
	private String urlbalancequery;
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
		Map<String, String> urls=new HashMap<String, String>();
			urls.put(ACTION_REGISTACCOUNT, this.urlregister);
			urls.put(ACTION_RECHARGE, this.urlrecharge);
			urls.put(ACTION_TRANSFER, this.urltransfer);
			urls.put(ACTION_CHECK, this.urlcheck);
			urls.put(ACTION_CARDBINDING, this.urlcardbinding);
			urls.put(ACTION_CASH, this.urlcash);
			urls.put(ACTION_AUTHORIZE, this.urlauthorize);
			urls.put(ACTION_ORDERQUERY, this.urlorderquery);
			urls.put(ACTION_BALANCEQUERY, this.urlbalancequery);
		return urls.get(action);
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
	
	public String getUrlregister() {
		return urlregister;
	}

	public void setUrlregister(String urlregister) {
		this.urlregister = urlregister;
	}

	public String getUrlrecharge() {
		return urlrecharge;
	}

	public void setUrlrecharge(String urlrecharge) {
		this.urlrecharge = urlrecharge;
	}

	public String getUrltransfer() {
		return urltransfer;
	}

	public void setUrltransfer(String urltransfer) {
		this.urltransfer = urltransfer;
	}

	public String getUrlcheck() {
		return urlcheck;
	}

	public void setUrlcheck(String urlcheck) {
		this.urlcheck = urlcheck;
	}

	public String getUrlcardbinding() {
		return urlcardbinding;
	}

	public void setUrlcardbinding(String urlcardbinding) {
		this.urlcardbinding = urlcardbinding;
	}

	public String getUrlcash() {
		return urlcash;
	}

	public void setUrlcash(String urlcash) {
		this.urlcash = urlcash;
	}

	public String getUrlauthorize() {
		return urlauthorize;
	}

	public void setUrlauthorize(String urlauthorize) {
		this.urlauthorize = urlauthorize;
	}

	public String getUrlorderquery() {
		return urlorderquery;
	}

	public void setUrlorderquery(String urlorderquery) {
		this.urlorderquery = urlorderquery;
	}

	public String getUrlbalancequery() {
		return urlbalancequery;
	}

	public void setUrlbalancequery(String urlbalancequery) {
		this.urlbalancequery = urlbalancequery;
	}
	
	
	public void checkReturnParam(Map<String,String> params,String[] signStrs) throws AlreadyDoneException, ResultCodeException, SignatureException
	{
		String resultCode=params.get("ResultCode");
		if(StringUtil.isEmpty(resultCode)||(!resultCode.equals("88")&&!resultCode.equals("18")))
		{
			throw new ResultCodeException(resultCode, params.get("Message"));
		}
		
		if(resultCode.equals("18")){
			throw new AlreadyDoneException(params.get("Message"));
		}
		
		StringBuilder sBuilder=new StringBuilder();
		for(String str:signStrs)
		{
			sBuilder.append(StringUtil.strFormat(params.get(str)));
		}
		RsaHelper rsa = RsaHelper.getInstance();
		String sign=rsa.signData(sBuilder.toString(), privateKey);
		if(!sign.replaceAll("\r", "").equals(params.get("SignInfo").replaceAll("\r", "")))
			throw new SignatureException("非法的签名");
	}
	
	//审核时候进行的签名
	@Override
	public String signForAudit(Map<String,String> params, String privateKey){
			StringBuilder sBuilder=new StringBuilder();
			sBuilder.append(StringUtil.strFormat(params.get("LoanNoList")));
			sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
			sBuilder.append(StringUtil.strFormat(params.get("AuditType")));
			sBuilder.append(StringUtil.strFormat(params.get("RandomTimeStamp")));
			sBuilder.append(StringUtil.strFormat(params.get("Remark1")));
			sBuilder.append(StringUtil.strFormat(params.get("Remark2")));
			sBuilder.append(StringUtil.strFormat(params.get("Remark3")));
			sBuilder.append(StringUtil.strFormat(params.get("ReturnURL")));
			sBuilder.append(StringUtil.strFormat(params.get("NotifyURL")));
			RsaHelper rsa = RsaHelper.getInstance();
			String signInfo=rsa.signData(sBuilder.toString(), privateKey);
			return signInfo;
	}
	
	// 解析并校验审核返回结果参数
	@Override
	public List<String> handleAuditReturnParams(Map<String, String> returnParams)
			throws AlreadyDoneException, ResultCodeException,
			SignatureException {
		List<String> result = new ArrayList<String>();
		try {
			checkAuditReturnParams(returnParams);
		} catch (SignatureException e) {
			throw e;
		} catch (ResultCodeException e) {
			throw e;
		} catch (AlreadyDoneException e) {
			throw e;
		}

		if (!StringUtil.isEmpty(returnParams.get("LoanNoList"))) {
			String[] loanNoList = returnParams.get("LoanNoList").split(",");
			for (String loanNo : loanNoList) {
				result.add(loanNo);
			}
		}

		return result;
	}

	// 校验审核转账时返回的参数
	@Override
	public void checkAuditReturnParams(Map<String, String> params)
			throws AlreadyDoneException, ResultCodeException,
			SignatureException {
		String[] signStrs = { "LoanNoList", "LoanNoListFail",
				"PlatformMoneymoremore", "AuditType", "RandomTimeStamp",
				"Remark1", "Remark2", "Remark3", "ResultCode" };
		checkReturnParam(params, signStrs);
	}
	
	
	//校验转账申请时候返回的参数
	@Override
	public void checkTransferReturnParams(Map<String,String> params) throws AlreadyDoneException, ResultCodeException, SignatureException{
		String[] signStrs={"LoanJsonList","PlatformMoneymoremore","Action","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		String loanJsonList = null;
		try {
			loanJsonList=URLDecoder.decode(params.get("LoanJsonList"),"UTF-8");
			params.put("LoanJsonList", loanJsonList);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		checkReturnParam(params,signStrs);
	}
}
