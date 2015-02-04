package gpps.service.thirdpay;

public interface IBalanceWithTPService {
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
