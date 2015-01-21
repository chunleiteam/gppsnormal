package gpps.service.thirdpay;

import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

import javax.management.loading.PrivateClassLoader;

public class RegistAccount {
	private String baseUrl;
	
	private int registerType=2;//必填
	private Integer accountType;//选填,空表示个人账户,1表示企业账户
	private String mobile;//必填,手机号
	private String email;//必填
	private String realName;//必填,真实姓名
	private String identificationNo;//必填,身份证号/营业执照号
	private String image1;//选填,	身份证/营业执照正面,图片转换成的字符串	
	private String image2;//选填,身份证/营业执照反面,图片转换成的字符串
	private String loanPlatformAccount;//必填,用户在网贷平台的账号	
	private String platformMoneymoremore;//必填,平台乾多多标识
	private String randomTimeStamp;//选填,随机时间戳	
	private String returnURL;//	半自动必填,页面返回网址	
	private String notifyURL;//必填,后台通知网址
	private String signInfo;//必填,签名信息
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public int getRegisterType() {
		return registerType;
	}
	public void setRegisterType(int registerType) {
		this.registerType = registerType;
	}
	public Integer getAccountType() {
		return accountType;
	}
	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getIdentificationNo() {
		return identificationNo;
	}
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}
	public String getImage1() {
		return image1;
	}
	public void setImage1(String image1) {
		this.image1 = image1;
	}
	public String getImage2() {
		return image2;
	}
	public void setImage2(String image2) {
		this.image2 = image2;
	}
	public String getLoanPlatformAccount() {
		return loanPlatformAccount;
	}
	public void setLoanPlatformAccount(String loanPlatformAccount) {
		this.loanPlatformAccount = loanPlatformAccount;
	}
	public String getPlatformMoneymoremore() {
		return platformMoneymoremore;
	}
	public void setPlatformMoneymoremore(String platformMoneymoremore) {
		this.platformMoneymoremore = platformMoneymoremore;
	}
	public String getRandomTimeStamp() {
		return randomTimeStamp;
	}
	public void setRandomTimeStamp(String randomTimeStamp) {
		this.randomTimeStamp = randomTimeStamp;
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
	
	public String getSign(String key)
	{
		StringBuilder sBuilder=new StringBuilder();
		//String dataStr = RegisterType + AccountType + Mobile + Email + RealName + IdentificationNo  
		//+Image1 + Image2 + LoanPlatformAccount + PlatformMoneymoremore + RandomTimeStamp + Remark1 + Remark2 + Remark3 
		//+ ReturnURL + NotifyURL;
		// 签名
		sBuilder.append(registerType);
		sBuilder.append(accountType==null?"":String.valueOf(accountType));
		sBuilder.append(StringUtil.strFormat(mobile));
		sBuilder.append(StringUtil.strFormat(email));
		sBuilder.append(StringUtil.strFormat(realName));
		sBuilder.append(StringUtil.strFormat(identificationNo));
		sBuilder.append(StringUtil.strFormat(loanPlatformAccount));
		sBuilder.append(StringUtil.strFormat(platformMoneymoremore));
		sBuilder.append(StringUtil.strFormat(randomTimeStamp));
		sBuilder.append(StringUtil.strFormat(returnURL));
		sBuilder.append(StringUtil.strFormat(notifyURL));
		RsaHelper rsa = RsaHelper.getInstance();
		return rsa.signData(sBuilder.toString(), key);
	}
}
