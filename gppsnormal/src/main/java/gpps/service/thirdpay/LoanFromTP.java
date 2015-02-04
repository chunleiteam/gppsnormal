package gpps.service.thirdpay;

import gpps.tools.StringUtil;

import java.util.Map;

public class LoanFromTP {
	String LoanNo;
	String OrderNo;
	String Amount;
	String PlatformMoneymoremore;
	public String getLoanNo() {
		return LoanNo;
	}
	public void setLoanNo(String loanNo) {
		LoanNo = loanNo;
	}
	public String getOrderNo() {
		return OrderNo;
	}
	public void setOrderNo(String orderNo) {
		OrderNo = orderNo;
	}
	public String getPlatformMoneymoremore() {
		return PlatformMoneymoremore;
	}
	public void setPlatformMoneymoremore(String platformMoneymoremore) {
		PlatformMoneymoremore = platformMoneymoremore;
	}
	public String getAmount() {
		return Amount;
	}
	public void setAmount(String amount) {
		Amount = amount;
	}
	public void init(Map<String, Object> param){
		this.LoanNo = StringUtil.strFormat(param.get("LoanNo"));
		this.OrderNo = StringUtil.strFormat(param.get("OrderNo"));
		this.PlatformMoneymoremore = StringUtil.strFormat(param.get("PlatformMoneymoremore"));
		this.Amount = StringUtil.strFormat(param.get("Amount"));
	}
}
