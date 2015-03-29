package gpps.service.thirdpay;

import gpps.model.PayBack;
import gpps.service.thirdpay.Transfer.LoanJson;

import java.security.SignatureException;
import java.util.List;
import java.util.Map;

public interface IThirdPaySupportNewService {
	/**
	 * 后台审核投标服务
	 * @param loanNos 所有流水号用英文逗号(,)连成一个字符串
	 * @param auditType 1.通过
						2.退回
						3.二次分配同意
						4.二次分配不同意
						5.提现通过
						6.提现退回
	 */
	public void auditBuy(List<String> loanNos,int auditType) throws Exception;
	
	
	/**
	 * 单纯的给第三方发送审核服务（通过、退回），不执行后续的操作auditBuyProcessor
	 * @param loanNos 所有流水号用英文逗号(,)连成一个字符串
	 * @param auditType 1.通过
						2.退回
						3.二次分配同意
						4.二次分配不同意
						5.提现通过
						6.提现退回
	 */
	public void justAuditBuy(List<String> loanNos, int auditType) throws Exception;
	
	/**
	 * 后台审核还款服务
	 * @param loanNos 所有流水号用英文逗号(,)连成一个字符串
	 * @param auditType 1.通过
						2.退回
						
	 */
	public void auditRepay(List<String> loanNos, int auditType) throws Exception;
	
	
	/**
	 * 将还款申请冻结的现金流组织成第三方模式LoanJson发送至第三方进行处理
	 * 
	 * @Param loanJsons 转账信息列表
	 * @Param payback 平台上对应的还款实体
	 * 
	 * */
	public void repayApply(List<LoanJson> loanJsons, PayBack payback) throws Exception;
	
	
	
	/**
	 * 处理对于购买审核的返回信息，修改平台上相应的实体状体与账户额度，维护与第三方的一致性
	 * 并根据是否需要发送短信与站内信
	 * 
	 * @param retJson 第三方返回的JSON格式的结果参数
	 * 
	 * 
	 * */
	public void auditBuyProcessor(Map<String, String> returnParams) throws AlreadyDoneException, ResultCodeException, SignatureException, Exception;

	
	/**
	 * 处理对于购买审核的返回信息，修改平台上相应的实体状体与账户额度，维护与第三方的一致性
	 * 并根据是否需要发送短信与站内信
	 * 
	 * @param retJson 第三方返回的JSON格式的结果参数
	 * 
	 * */
	public void auditRepayProcessor(Map<String, String> returnParams) throws AlreadyDoneException, ResultCodeException, SignatureException, Exception;
	
	
	/**
	 * 处理对于申请还款的返回信息，修改平台上相应的实体状体与账户额度，维护与第三方的一致性
	 * 
	 * @param retJson 第三方返回的JSON格式的结果参数
	 * 
	 * */
	public void repayApplyProcessor(Map<String, String> returnParams) throws AlreadyDoneException, ResultCodeException, SignatureException, Exception;
	
	/**
	 * 根据平台现金流ID（第三方平台上记录为orderNo）查询转账记录
	 * 
	 * @param orderNo 平台上现金流ID，也就是第三方上的orderNo
	 * @Param action  账单的操作
	 * 					空：转账
	 * 					1： 充值
	 * 					2： 提现
	 * 
	 * */
	public LoanFromTP viewByOrderNo(String orderNo, String action) throws Exception;
	
	
	/**
	 * 根据第三方转账记录号查询转账记录
	 * 
	 * @param loanNo 第三方转账记录号
	 * @Param action  账单的操作
	 * 					空：转账
	 * 					1： 充值
	 * 					2： 提现
	 * */
	public LoanFromTP viewByLoanNo(String loanNo, String action) throws Exception;
}
