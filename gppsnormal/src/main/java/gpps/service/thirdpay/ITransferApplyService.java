package gpps.service.thirdpay;

import gpps.model.PayBack;
import gpps.service.thirdpay.Transfer.LoanJson;

import java.security.SignatureException;
import java.util.List;
import java.util.Map;

public interface ITransferApplyService {
	/**
	 * 将还款申请冻结的现金流组织成第三方模式LoanJson发送至第三方进行处理
	 * 
	 * @Param loanJsons 转账信息列表
	 * @Param payback 平台上对应的还款实体
	 * 
	 * */
	public void repayApply(List<LoanJson> loanJsons, PayBack payback) throws Exception;
	
	
	/**
	 * 将转账申请（由申请授权还款的企业到个人）冻结的现金流组织成第三方模式LoanJson发送至第三方进行处理【主要用途为给投资者发放奖励】
	 * 
	 * @Param loanJsons 转账信息列表
	 * 
	 * 
	 * */
	public List<LoanFromTP> justTransferApplyNeedAudit(List<LoanJson> loanJsons) throws Exception;
	
	/**
	 * 将转账申请冻结的现金流组织成第三方模式LoanJson发送至第三方进行处理【无需审核直接转账】
	 * 
	 * @Param loanJsons 转账信息列表
	 * 
	 * 
	 * 
	 * */
	public List<LoanFromTP> justTransferApplyNoNeedAudit(List<LoanJson> loanJsons) throws Exception;
	
	/**
	 * 处理对于申请还款的返回信息，修改平台上相应的实体状体与账户额度，维护与第三方的一致性
	 * 
	 * @param retJson 第三方返回的JSON格式的结果参数
	 * 
	 * */
	public void repayApplyProcessor(Map<String, String> returnParams) throws AlreadyDoneException, ResultCodeException, SignatureException, Exception;
}
