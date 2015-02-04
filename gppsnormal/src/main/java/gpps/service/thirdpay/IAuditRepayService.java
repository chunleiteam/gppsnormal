package gpps.service.thirdpay;

import java.security.SignatureException;
import java.util.List;

public interface IAuditRepayService {
	/**
	 * 后台审核还款服务
	 * @param loanNos 所有流水号用英文逗号(,)连成一个字符串
	 * @param auditType 1.通过
						2.退回
						
	 */
	public void auditRepay(List<String> loanNos, int auditType) throws Exception;
	
	/**
	 * 处理对于购买审核的返回信息，修改平台上相应的实体状体与账户额度，维护与第三方的一致性
	 * 并根据是否需要发送短信与站内信
	 * 
	 * @param retJson 第三方返回的JSON格式的结果参数
	 * 
	 * */
	public void auditRepayProcessor(String retJson) throws AlreadyDoneException, ResultCodeException, SignatureException, Exception;
}
