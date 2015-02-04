package gpps.service.thirdpay;

import gpps.tools.StringUtil;

import java.util.Map;

public class WithDrawFromTP extends LoanFromTP {
	String WithdrawMoneymoremore;
	String FeeWithdraws;
	String WithdrawsState;
	String WithdrawsTime;
	String PlatformAuditState;
	String PlatformAuditTime;
	String WithdrawsBackTime;
	
	public WithDrawFromTP(){
		
	}
	public WithDrawFromTP(Map<String, Object> param){
		init(param);
	}
	
	public String getWithdrawMoneymoremore() {
		return WithdrawMoneymoremore;
	}
	public void setWithdrawMoneymoremore(String withdrawMoneymoremore) {
		WithdrawMoneymoremore = withdrawMoneymoremore;
	}
	public String getFeeWithdraws() {
		return FeeWithdraws;
	}
	public void setFeeWithdraws(String feeWithdraws) {
		FeeWithdraws = feeWithdraws;
	}
	public String getWithdrawsState() {
		return WithdrawsState;
	}
	public void setWithdrawsState(String withdrawsState) {
		WithdrawsState = withdrawsState;
	}
	public String getWithdrawsTime() {
		return WithdrawsTime;
	}
	public void setWithdrawsTime(String withdrawsTime) {
		WithdrawsTime = withdrawsTime;
	}
	public String getPlatformAuditState() {
		return PlatformAuditState;
	}
	public void setPlatformAuditState(String platformAuditState) {
		PlatformAuditState = platformAuditState;
	}
	public String getPlatformAuditTime() {
		return PlatformAuditTime;
	}
	public void setPlatformAuditTime(String platformAuditTime) {
		PlatformAuditTime = platformAuditTime;
	}
	public String getWithdrawsBackTime() {
		return WithdrawsBackTime;
	}
	public void setWithdrawsBackTime(String withdrawsBackTime) {
		WithdrawsBackTime = withdrawsBackTime;
	}
	public void init(Map<String, Object> param){
		super.init(param);
		this.WithdrawMoneymoremore = StringUtil.strFormat(param.get("WithdrawMoneymoremore"));
		this.FeeWithdraws = StringUtil.strFormat(param.get("FeeWithdraws"));
		this.WithdrawsState = StringUtil.strFormat(param.get("WithdrawsState"));
		this.WithdrawsTime = StringUtil.strFormat(param.get("WithdrawsTime"));
		this.PlatformAuditState = StringUtil.strFormat(param.get("PlatformAuditState"));
		this.PlatformAuditTime = StringUtil.strFormat(param.get("PlatformAuditTime"));
		this.WithdrawsBackTime = StringUtil.strFormat(param.get("WithdrawsBackTime"));
	}
}
