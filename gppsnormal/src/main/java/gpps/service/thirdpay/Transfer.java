package gpps.service.thirdpay;

import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

/**
 * 转账
 * @author wangm
 *
 */
public class Transfer {
	private String baseUrl;
	
	private String loanJsonList;//	转账列表	必填
	private String platformMoneymoremore;//	平台乾多多标识	必填
	private String transferAction;//	转账类型	必填
	private String action;//	操作类型	必填
	private String transferType;//	转账方式	必填
	private String needAudit;//	通过是否需要审核	选填
	private String randomTimeStamp;//	随机时间戳	选填
	private String remark1;//	自定义备注	选填
	private String remark2;//	自定义备注	选填
	private String remark3;//	自定义备注	选填
	private String returnURL;//	页面返回网址	手动转账必填
	private String notifyURL;//	后台通知网址	必填
	private String signInfo;//	签名信息	必填
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getLoanJsonList() {
		return loanJsonList;
	}
	public void setLoanJsonList(String loanJsonList) {
		this.loanJsonList = loanJsonList;
	}
	public String getPlatformMoneymoremore() {
		return platformMoneymoremore;
	}
	public void setPlatformMoneymoremore(String platformMoneymoremore) {
		this.platformMoneymoremore = platformMoneymoremore;
	}
	public String getTransferAction() {
		return transferAction;
	}
	public void setTransferAction(String transferAction) {
		this.transferAction = transferAction;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public String getNeedAudit() {
		return needAudit;
	}
	public void setNeedAudit(String needAudit) {
		this.needAudit = needAudit;
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
		//LoanJsonList + PlatformMoneymoremore + TransferAction + Action + TransferType 
		//+ NeedAudit + RandomTimeStamp + Remark1 + Remark2 + Remark3 + ReturnURL + NotifyURL
		// 签名
		sBuilder.append(StringUtil.strFormat(loanJsonList));
		sBuilder.append(StringUtil.strFormat(platformMoneymoremore));
		sBuilder.append(StringUtil.strFormat(transferAction));
		sBuilder.append(StringUtil.strFormat(action));
		sBuilder.append(StringUtil.strFormat(transferType));
		sBuilder.append(StringUtil.strFormat(needAudit));
		sBuilder.append(StringUtil.strFormat(randomTimeStamp));
		sBuilder.append(StringUtil.strFormat(remark1));
		sBuilder.append(StringUtil.strFormat(remark2));
		sBuilder.append(StringUtil.strFormat(remark3));
		sBuilder.append(StringUtil.strFormat(returnURL));
		sBuilder.append(StringUtil.strFormat(notifyURL));
		RsaHelper rsa = RsaHelper.getInstance();
		return rsa.signData(sBuilder.toString(), key);
	}
	public static class LoanJson
	{
		private String LoanOutMoneymoremore = "";// 付款人ID
		private String LoanInMoneymoremore = "";//收款人ID
		private String LoanNo;	//乾多多流水号
		private String OrderNo = "";//订单号
		private String BatchNo = "";//标号
		private String ExchangeBatchNo = "";//流转标标号
		private String AdvanceBatchNo = "";//垫资标号
		private String Amount = "";//金额
		private String FullAmount = "";//满标金额
		private String TransferName = "";//用途
		private String Remark = "";//备注
		private String SecondaryJsonList = "";//二次分配列表
		public String getLoanOutMoneymoremore()
		{
			return LoanOutMoneymoremore;
		}
		
		public void setLoanOutMoneymoremore(String loanOutMoneymoremore)
		{
			LoanOutMoneymoremore = loanOutMoneymoremore;
		}
		
		public String getLoanInMoneymoremore()
		{
			return LoanInMoneymoremore;
		}
		
		public void setLoanInMoneymoremore(String loanInMoneymoremore)
		{
			LoanInMoneymoremore = loanInMoneymoremore;
		}
		public String getLoanNo() {
			return LoanNo;
		}

		public void setLoanNo(String loanNo) {
			LoanNo = loanNo;
		}

		public String getOrderNo()
		{
			return OrderNo;
		}
		
		public void setOrderNo(String orderNo)
		{
			OrderNo = orderNo;
		}
		
		public String getAmount()
		{
			return Amount;
		}
		
		public void setAmount(String amount)
		{
			Amount = amount;
		}
		
		public String getTransferName()
		{
			return TransferName;
		}
		
		public void setTransferName(String transferName)
		{
			TransferName = transferName;
		}
		
		public String getRemark()
		{
			return Remark;
		}
		
		public void setRemark(String remark)
		{
			Remark = remark;
		}
		
		public String getBatchNo()
		{
			return BatchNo;
		}
		
		public void setBatchNo(String batchNo)
		{
			BatchNo = batchNo;
		}
		
		public String getFullAmount()
		{
			return FullAmount;
		}
		
		public void setFullAmount(String fullAmount)
		{
			FullAmount = fullAmount;
		}
		
		public String getSecondaryJsonList()
		{
			return SecondaryJsonList;
		}
		
		public void setSecondaryJsonList(String secondaryJsonList)
		{
			SecondaryJsonList = secondaryJsonList;
		}
		
		public String getExchangeBatchNo()
		{
			return ExchangeBatchNo;
		}
		
		public void setExchangeBatchNo(String exchangeBatchNo)
		{
			ExchangeBatchNo = exchangeBatchNo;
		}
		
		public String getAdvanceBatchNo()
		{
			return AdvanceBatchNo;
		}
		
		public void setAdvanceBatchNo(String advanceBatchNo)
		{
			AdvanceBatchNo = advanceBatchNo;
		}
		
	}
}
