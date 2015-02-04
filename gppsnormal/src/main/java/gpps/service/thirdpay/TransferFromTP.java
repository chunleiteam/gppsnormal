package gpps.service.thirdpay;

import gpps.tools.StringUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

/**
 * 从第三方返回的账单（现金流）列表
 * 
 * */
public class TransferFromTP extends LoanFromTP{
	String LoanOutMoneymoremore;
	String LoanInMoneymoremore;
	String BatchNo;
	String TransferAction;
	String Action;
	String TransferType;
	String TransferState;
	String TransferTime;
	String ActState;
	String ActTime;
	String ActNo;
	String TransferName;
	
	public TransferFromTP(){
		
	}
	
	public TransferFromTP(Map<String, Object> param){
		init(param);
	}
	
	public String getLoanOutMoneymoremore() {
		return LoanOutMoneymoremore;
	}
	public void setLoanOutMoneymoremore(String loanOutMoneymoremore) {
		LoanOutMoneymoremore = loanOutMoneymoremore;
	}
	public String getLoanInMoneymoremore() {
		return LoanInMoneymoremore;
	}
	public void setLoanInMoneymoremore(String loanInMoneymoremore) {
		LoanInMoneymoremore = loanInMoneymoremore;
	}
	public String getBatchNo() {
		return BatchNo;
	}
	public void setBatchNo(String batchNo) {
		BatchNo = batchNo;
	}
	
	public String getTransferAction() {
		return TransferAction;
	}
	public void setTransferAction(String transferAction) {
		TransferAction = transferAction;
	}
	public String getAction() {
		return Action;
	}
	public void setAction(String action) {
		Action = action;
	}
	public String getTransferType() {
		return TransferType;
	}
	public void setTransferType(String transferType) {
		TransferType = transferType;
	}
	public String getTransferState() {
		return TransferState;
	}
	public void setTransferState(String transferState) {
		TransferState = transferState;
	}
	public String getTransferTime() {
		return TransferTime;
	}
	public void setTransferTime(String transferTime) {
		TransferTime = transferTime;
	}
	public String getActState() {
		return ActState;
	}
	public void setActState(String actState) {
		ActState = actState;
	}
	public String getActTime() {
		return ActTime;
	}
	public void setActTime(String actTime) {
		ActTime = actTime;
	}
	public String getActNo() {
		return ActNo;
	}
	public void setActNo(String actNo) {
		ActNo = actNo;
	}
	public String getTransferName() {
		return TransferName;
	}
	public void setTransferName(String transferName) {
		TransferName = transferName;
	}
	
	public void init(Map<String, Object> param){
		super.init(param);
		this.Action = StringUtil.strFormat(param.get("Action"));
		this.ActNo = StringUtil.strFormat(param.get("ActNo"));
		this.ActState = StringUtil.strFormat(param.get("ActState"));
		this.ActTime = StringUtil.strFormat(param.get("ActTime"));
		this.BatchNo = StringUtil.strFormat(param.get("BatchNo"));
		this.LoanInMoneymoremore = StringUtil.strFormat(param.get("LoanInMoneymoremore"));
		this.LoanOutMoneymoremore = StringUtil.strFormat(param.get("LoanOutMoneymoremore"));
		this.TransferAction = StringUtil.strFormat(param.get("TransferAction"));
		this.TransferName = StringUtil.strFormat(param.get("TransferName"));
		this.TransferState = StringUtil.strFormat(param.get("TransferState"));
		this.TransferTime = StringUtil.strFormat(param.get("TransferTime"));
		this.TransferType = StringUtil.strFormat(param.get("TransferType"));
	}
}
