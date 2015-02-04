package gpps.service.thirdpay;

import gpps.tools.StringUtil;

import java.util.Map;

public class RechargeFromTP extends LoanFromTP {
	String RechargeMoneymoremore;
	String RechargeState;
	String RechargeTime;
	
	public RechargeFromTP(){
		
	}
	public RechargeFromTP(Map<String, Object> param){
		init(param);
	}
	
	public String getRechargeMoneymoremore() {
		return RechargeMoneymoremore;
	}
	public void setRechargeMoneymoremore(String rechargeMoneymoremore) {
		RechargeMoneymoremore = rechargeMoneymoremore;
	}
	public String getRechargeState() {
		return RechargeState;
	}
	public void setRechargeState(String rechargeState) {
		RechargeState = rechargeState;
	}
	public String getRechargeTime() {
		return RechargeTime;
	}
	public void setRechargeTime(String rechargeTime) {
		RechargeTime = rechargeTime;
	}
	public void init(Map<String, Object> param){
		super.init(param);
		this.RechargeMoneymoremore = StringUtil.strFormat(param.get("RechargeMoneymoremore"));
		this.RechargeState = StringUtil.strFormat(param.get("RechargeState"));
		this.RechargeTime = StringUtil.strFormat(param.get("RechargeTime"));
	}
}
