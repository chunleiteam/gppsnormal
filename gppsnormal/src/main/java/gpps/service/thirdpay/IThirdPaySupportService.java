package gpps.service.thirdpay;

import gpps.model.GovermentOrder;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.IllegalOperationException;
import gpps.service.exception.InsufficientBalanceException;
import gpps.service.exception.LoginException;
import gpps.service.thirdpay.Transfer.LoanJson;

import java.security.SignatureException;
import java.util.List;
import java.util.Map;

public interface IThirdPaySupportService {
	/**
	 * 返回开户表单提交信息
	 * @return
	 * @throws LoginException
	 */
	public RegistAccount getRegistAccount() throws LoginException;
	/**
	 * 返回网银充值表单提交信息
	 * @param amount
	 * @return
	 * @throws LoginException
	 */
	public Recharge getRecharge(String amount) throws LoginException;
	
	/**
	 * 返回快捷支付充值表单提交信息
	 * @param amount
	 * @return
	 * @throws LoginException
	 */
	public Recharge getQuickRecharge(String amount) throws LoginException;
	
	/**
	 * 返回企业网银充值表单提交信息
	 * @param amount
	 * @return
	 * @throws LoginException
	 */
	public Recharge getCompanyRecharge(String amount) throws LoginException;
	
	/**
	 * 返回购买表单提交信息
	 * @param submitId
	 * @param pid
	 * @return
	 * @throws InsufficientBalanceException
	 * @throws LoginException
	 */
	public Transfer getTransferToBuy(Integer submitId,String pid) throws InsufficientBalanceException, LoginException;
	
	/**
	 * 后台审核服务
	 * @param loanNos 所有流水号用英文逗号(,)连成一个字符串
	 * @param auditType 1.通过
						2.退回
						3.二次分配同意
						4.二次分配不同意
						5.提现通过
						6.提现退回
	 */
	public void check(List<String> loanNos,int auditType);
	/**
	 * 返回绑卡表单提交信息 
	 * @return
	 * @throws LoginException
	 */
	public CardBinding getCardBinding() throws LoginException;
	/**
	 * 返回取现表单提交信息
	 * @param amount
	 * @return
	 * @throws InsufficientBalanceException
	 * @throws LoginException
	 * @throws IllegalOperationException
	 */
	public Cash getCash(String amount) throws InsufficientBalanceException, LoginException, IllegalOperationException;
	/**
	 * 返回授权表单提交信息
	 * @return
	 * @throws LoginException
	 */
	public Authorize getAuthorize() throws LoginException;
	
	/**
	 * 将还款流水组织成第三方模式LoanJson进行处理
	 * @param loanJsons
	 */
	public void repay(List<String> loanNos, int auditType);
	
	
	/**
	 * 将还款申请冻结的现金流组织成第三方模式LoanJson发送至第三方进行处理
	 * 
	 * */
	public void submitForCheckRepay(List<LoanJson> loanJsons, PayBack payback);
	
	
	
	/**
	 * 验证第三方回调的请求，验证结果码为“88”，并且签名正确
	 * @param params
	 * @param signStrs
	 * @throws ResultCodeException
	 * @throws SignatureException
	 */
	public void checkRollBack(Map<String,String> params,String[] signStrs) throws ResultCodeException, SignatureException;
	/**
	 * 执行购买审核时从第三方返回结果处理
	 * @param params
	 * @throws SignatureException
	 * @throws ResultCodeException
	 */
	public void checkBuyProcessor(Map<String,String> params) throws SignatureException, ResultCodeException;
	/**
	 * 执行还款时从第三方返回结果处理
	 * @param params
	 * @throws SignatureException
	 * @throws ResultCodeException
	 */
	public void repayProcessor(Map<String,String> params, GovermentOrder order, Product product, PayBack payback) throws SignatureException, ResultCodeException;
	/**
	 * 取款成功后，同步取款在第三方的状态
	 * @param cashStreamId
	 * @throws IllegalOperationException
	 */
	public void checkCash(Integer cashStreamId) throws IllegalOperationException;
	/**
	 * 从第三方获取账户金额信息
	 * @param thirdPartyAccount
	 * @return
	 */
	public String balanceQuery(String thirdPartyAccount);
	/**
	 * 资金流对账
	 * @param cashStreamId
	 * @throws IllegalOperationException
	 * @throws IllegalConvertException
	 */
	public void checkWithThirdPay(Integer cashStreamId) throws IllegalOperationException, IllegalConvertException;
}
