package gpps.service.thirdpay;

import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

public class Recharge {
	private String baseUrl;
	
	private String rechargeMoneymoremore;//	充值人乾多多标识	必填
	private String platformMoneymoremore;//	平台乾多多标识	必填
	private String orderNo;//	平台的充值订单号	必填
	private String amount;//	金额	必填
	private String rechargeType;//	充值类型	选填
	private String feeType;//	手续费类型	快捷支付、汇款充值、企业网银必填
	private String cardNo;//	银行卡号	选填
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
	public String getRechargeMoneymoremore() {
		return rechargeMoneymoremore;
	}
	public void setRechargeMoneymoremore(String rechargeMoneymoremore) {
		this.rechargeMoneymoremore = rechargeMoneymoremore;
	}
	public String getPlatformMoneymoremore() {
		return platformMoneymoremore;
	}
	public void setPlatformMoneymoremore(String platformMoneymoremore) {
		this.platformMoneymoremore = platformMoneymoremore;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getRechargeType() {
		return rechargeType;
	}
	public void setRechargeType(String rechargeType) {
		this.rechargeType = rechargeType;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
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
		//RechargeMoneymoremore + PlatformMoneymoremore + OrderNo + Amount + RechargeType + 
		//FeeType + CardNo + RandomTimeStamp + Remark1 + Remark2 + Remark3 + ReturnURL + NotifyURL
		// 签名
		sBuilder.append(StringUtil.strFormat(rechargeMoneymoremore));
		sBuilder.append(StringUtil.strFormat(platformMoneymoremore));
		sBuilder.append(StringUtil.strFormat(orderNo));
		sBuilder.append(StringUtil.strFormat(amount));
		sBuilder.append(StringUtil.strFormat(rechargeType));
		sBuilder.append(StringUtil.strFormat(feeType));
		sBuilder.append(StringUtil.strFormat(cardNo));
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
