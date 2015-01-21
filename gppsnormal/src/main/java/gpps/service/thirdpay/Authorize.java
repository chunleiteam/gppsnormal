package gpps.service.thirdpay;

import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

public class Authorize {
	private String baseUrl;
	
	private String moneymoremoreId;//用户乾多多标识 必填 
	private String platformMoneymoremore;//	平台乾多多标识	必填
	private String authorizeTypeOpen;//	开启授权类型	选填	开启和关闭类型不能同时为空
	private String authorizeTypeClose;//关闭授权类型	选填	开启和关闭类型不能同时为空
	private String randomTimeStamp;//	随机时间戳	选填
	private String remark1;//自定义备注	选填
	private String remark2;//	自定义备注	选填
	private String remark3;//	自定义备注	选填	
	private String returnURL;//	页面返回网址	必填
	private String notifyURL;//	后台通知网址	必填
	private String signInfo;//签名信息
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getMoneymoremoreId() {
		return moneymoremoreId;
	}
	public void setMoneymoremoreId(String moneymoremoreId) {
		this.moneymoremoreId = moneymoremoreId;
	}
	public String getPlatformMoneymoremore() {
		return platformMoneymoremore;
	}
	public void setPlatformMoneymoremore(String platformMoneymoremore) {
		this.platformMoneymoremore = platformMoneymoremore;
	}
	public String getAuthorizeTypeOpen() {
		return authorizeTypeOpen;
	}
	public void setAuthorizeTypeOpen(String authorizeTypeOpen) {
		this.authorizeTypeOpen = authorizeTypeOpen;
	}
	public String getAuthorizeTypeClose() {
		return authorizeTypeClose;
	}
	public void setAuthorizeTypeClose(String authorizeTypeClose) {
		this.authorizeTypeClose = authorizeTypeClose;
	}
	public String getRandomTimeStamp() {
		return randomTimeStamp;
	}
	public void setRandomTimeStamp(String randomTimeStamp) {
		this.randomTimeStamp = randomTimeStamp;
	}
	public String getRemark1() {
		return remark1;
	}
	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}
	public String getRemark2() {
		return remark2;
	}
	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}
	public String getRemark3() {
		return remark3;
	}
	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}
	public String getNotifyURL() {
		return notifyURL;
	}
	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
	}
	public String getSign(String privateKey)
	{
		StringBuilder sBuilder=new StringBuilder();
		//MoneymoremoreId + PlatformMoneymoremore + AuthorizeTypeOpen + AuthorizeTypeClose 
		//+ RandomTimeStamp + Remark1 + Remark2 + Remark3 + ReturnURL + NotifyURL
		sBuilder.append(StringUtil.strFormat(moneymoremoreId));
		sBuilder.append(StringUtil.strFormat(platformMoneymoremore));
		sBuilder.append(StringUtil.strFormat(authorizeTypeOpen));
		sBuilder.append(StringUtil.strFormat(authorizeTypeClose));
		sBuilder.append(StringUtil.strFormat(randomTimeStamp));
		sBuilder.append(StringUtil.strFormat(remark1));
		sBuilder.append(StringUtil.strFormat(remark2));
		sBuilder.append(StringUtil.strFormat(remark3));
		sBuilder.append(StringUtil.strFormat(returnURL));
		sBuilder.append(StringUtil.strFormat(notifyURL));
		RsaHelper rsa = RsaHelper.getInstance();
		return rsa.signData(sBuilder.toString(), privateKey);
	}
}
