package gpps.service.thirdpay;

import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

public class CardBinding {
	private String baseUrl;
	
	private String moneymoremoreId;//	用户乾多多标识	必填
	private String platformMoneymoremore;//	平台乾多多标识	必填
	private String action;//	操作类型	必填
	private String cardNo;//	银行卡号	汇款绑卡确认、取消代扣授权必填
	private String withholdBeginDate;//	代扣开始日期	代扣授权选填
	private String withholdEndDate;//	代扣结束日期	代扣授权选填
	private String singleWithholdLimit;//	单笔代扣限额	代扣授权选填
	private String totalWithholdLimit;//	代扣总限额	代扣授权选填
	private String randomTimeStamp;//	随机时间戳	选填
	private String remark1;//	自定义备注	选填
	private String remark2;//	自定义备注	选填
	private String remark3;//	自定义备注	选填
	private String returnURL;//	页面返回网址	必填
	private String notifyURL;//	后台通知网址	必填
	private String signInfo;//	签名信息	必填
	
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
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getWithholdBeginDate() {
		return withholdBeginDate;
	}
	public void setWithholdBeginDate(String withholdBeginDate) {
		this.withholdBeginDate = withholdBeginDate;
	}
	public String getWithholdEndDate() {
		return withholdEndDate;
	}
	public void setWithholdEndDate(String withholdEndDate) {
		this.withholdEndDate = withholdEndDate;
	}
	public String getSingleWithholdLimit() {
		return singleWithholdLimit;
	}
	public void setSingleWithholdLimit(String singleWithholdLimit) {
		this.singleWithholdLimit = singleWithholdLimit;
	}
	public String getTotalWithholdLimit() {
		return totalWithholdLimit;
	}
	public void setTotalWithholdLimit(String totalWithholdLimit) {
		this.totalWithholdLimit = totalWithholdLimit;
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
	public String getSign(String key)
	{
		StringBuilder sBuilder=new StringBuilder();
		//MoneymoremoreId + PlatformMoneymoremore + Action + CardNo + WithholdBeginDate 
		//+ WithholdEndDate + SingleWithholdLimit + TotalWithholdLimit + RandomTimeStamp 
		//+ Remark1 + Remark2 + Remark3 + ReturnURL + NotifyURL
		sBuilder.append(StringUtil.strFormat(moneymoremoreId));
		sBuilder.append(StringUtil.strFormat(platformMoneymoremore));
		sBuilder.append(StringUtil.strFormat(action));
		sBuilder.append(StringUtil.strFormat(cardNo));
		sBuilder.append(StringUtil.strFormat(withholdBeginDate));
		sBuilder.append(StringUtil.strFormat(withholdEndDate));
		sBuilder.append(StringUtil.strFormat(singleWithholdLimit));
		sBuilder.append(StringUtil.strFormat(totalWithholdLimit));
		sBuilder.append(StringUtil.strFormat(randomTimeStamp));
		sBuilder.append(StringUtil.strFormat(remark1));
		sBuilder.append(StringUtil.strFormat(remark2));
		sBuilder.append(StringUtil.strFormat(remark3));
		sBuilder.append(StringUtil.strFormat(returnURL));
		sBuilder.append(StringUtil.strFormat(notifyURL));
		RsaHelper rsa = RsaHelper.getInstance();
		return rsa.signData(sBuilder.toString(), key);
	}
}
