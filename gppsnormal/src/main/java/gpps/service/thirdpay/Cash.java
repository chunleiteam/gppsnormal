package gpps.service.thirdpay;

import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

public class Cash {
	public String baseUrl;
	
	private String withdrawMoneymoremore;//	提现人乾多多标识	必填
	private String platformMoneymoremore;//	平台乾多多标识	必填
	private String orderNo;//	平台的提现订单号	必填
	private String amount;//	金额	必填
	/**
	 * 	0表示手续费100%由用户承担，	100表示手续费100%由平台承担，默认是0
	 */
	private String feePercent;//	平台承担的手续费比例	选填 0-100之间的数
	private String feeMax;//	用户承担的最高手续费	选填
	/**
	 * 最多四位小数，由平台指定本次提现的手续费率，不得低于签约基准费率，如果为空，则按照签约基准费率计算手续费
	 */
	private String feeRate;//	上浮费率	选填
	/**
	 * 原卡号需经过加密后抛送，SignInfo中的卡号是未经加密的原卡号
	 */
	private String cardNo;//	银行卡号	必填
	private String cardType;//	银行卡类型	第一次提现(银行卡未绑定乾多多)时必填
	private String bankCode;//	银行代码	第一次提现(银行卡未绑定乾多多)时必填
	private String branchBankName;//	开户行支行名称	选填
	private String province;//	开户行省份	第一次提现(银行卡未绑定乾多多)时必填
	private String city;//	开户行城市	第一次提现(银行卡未绑定乾多多)时必填
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
	public String getWithdrawMoneymoremore() {
		return withdrawMoneymoremore;
	}
	public void setWithdrawMoneymoremore(String withdrawMoneymoremore) {
		this.withdrawMoneymoremore = withdrawMoneymoremore;
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
	public String getFeePercent() {
		return feePercent;
	}
	public void setFeePercent(String feePercent) {
		this.feePercent = feePercent;
	}
	public String getFeeMax() {
		return feeMax;
	}
	public void setFeeMax(String feeMax) {
		this.feeMax = feeMax;
	}
	public String getFeeRate() {
		return feeRate;
	}
	public void setFeeRate(String feeRate) {
		this.feeRate = feeRate;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBranchBankName() {
		return branchBankName;
	}
	public void setBranchBankName(String branchBankName) {
		this.branchBankName = branchBankName;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
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
		//WithdrawMoneymoremore + PlatformMoneymoremore + OrderNo + Amount + FeePercent + FeeMax 
		//+ FeeRate + CardNo + CardType + BankCode + BranchBankName + Province + City 
		//+ RandomTimeStamp + Remark1 + Remark2 + Remark3 + ReturnURL + NotifyURL
		sBuilder.append(StringUtil.strFormat(withdrawMoneymoremore));
		sBuilder.append(StringUtil.strFormat(platformMoneymoremore));
		sBuilder.append(StringUtil.strFormat(orderNo));
		sBuilder.append(StringUtil.strFormat(amount));
		sBuilder.append(StringUtil.strFormat(feePercent));
		sBuilder.append(StringUtil.strFormat(feeMax));
		sBuilder.append(StringUtil.strFormat(feeRate));
		sBuilder.append(StringUtil.strFormat(cardNo));
		sBuilder.append(StringUtil.strFormat(cardType));
		sBuilder.append(StringUtil.strFormat(bankCode));
		sBuilder.append(StringUtil.strFormat(branchBankName));
		sBuilder.append(StringUtil.strFormat(province));
		sBuilder.append(StringUtil.strFormat(city));
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
