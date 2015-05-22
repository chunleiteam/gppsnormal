package gpps.service;

import gpps.constant.Pagination;
import gpps.model.CashStream;
import gpps.model.PayBack;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.IllegalOperationException;
import gpps.service.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
/**
 * 账户服务
 * 该服务对外不可见，前端不可直接调用
 * 账户操作涉及到实际调用第三方平台进行操作时，需要一个预操作记录
 * 该服务中各对账户的操作只是在CashStream中记录预处理状态，在changeCashStreamState中当state=1在实际进行账户的修改
 * @author wmsdu@aliyun.com
 *
 */
public interface IAccountService {
	/**
	 * 贷款人账户充值
	 * @param amount
	 */
	public Integer rechargeLenderAccount(Integer lenderAccountId,BigDecimal amount,String description);
	/**
	 * 借款人账户充值
	 * @param amount
	 */
	public Integer rechargeBorrowerAccount(Integer borrowerAccountId,BigDecimal amount,String description);
	/**
	 * 贷款人账户冻结
	 * @param amount
	 * @exception InsufficientBalanceException 余额不足
	 */
	public Integer freezeLenderAccount(Integer lenderAccountId,BigDecimal amount,Integer submitid,String description) throws InsufficientBalanceException;
	/**
	 * 贷款人账户解冻
	 * @param lenderAccountId
	 * @param amount
	 * @param submitid
	 * @param description
	 * @return
	 * @throws IllegalConvertException 
	 */
	public Integer unfreezeLenderAccount(Integer lenderAccountId,BigDecimal amount,Integer submitid,String description) throws IllegalConvertException;
	/**
	 * 借款人账户冻结
	 * @param amount
	 * @throws IllegalConvertException 
	 */
	public Integer freezeBorrowerAccount(Integer borrowerAccountId,BigDecimal chiefAmount, BigDecimal interest, Integer submitId,Integer paybackId,String description) throws InsufficientBalanceException;
	
	/**
	 * 平台自有账户冻结（仅限于发放奖励）
	 * @param chiefAmount 奖励额度
	 * @param paybackId   本批次对应的奖励paybackId
	 * @param description 冻结说明
	 * */
	public Integer freezeAdminAccount(Integer lenderId, BigDecimal chiefAmount, Integer paybackId, String description) throws Exception;
	
	/**
	 * 借款人账户解冻
	 * @param borrowerAccountId
	 * @param amount
	 * @param paybackId
	 * @param description
	 * @return
	 * @throws IllegalConvertException
	 */
	public Integer unfreezeBorrowerAccount(Integer borrowerAccountId,BigDecimal amount, Integer submitId,Integer paybackId,String description) throws IllegalConvertException;
	/**
	 * 贷款人将冻结资金支付给借款人
	 * @param amount
	 * @throws IllegalConvertException 
	 */
	public Integer pay(Integer lenderAccountId,Integer borrowerAccountId, BigDecimal chiefamount,Integer submitid,String description) throws IllegalConvertException;
	/**
	 * 还款
	 * @param amount
	 * @throws IllegalConvertException 
	 */
	public Integer repay(Integer lenderAccountId,Integer borrowerAccountId,BigDecimal chiefamount,BigDecimal interest,Integer submitid,Integer paybackId,String description) throws IllegalConvertException;
	
	/**
	 * 发放奖励
	 * 
	 * 
	 * */
	public Integer reward(Integer cashStreamId, Integer lenderAccountId, BigDecimal amount,Integer paybackId, String description) throws IllegalConvertException;
	
	/**
	 * 贷款人取现
	 * @param amount
	 * @exception InsufficientBalanceException 余额不足
	 */
	public Integer cashLenderAccount(Integer lenderAccountId,BigDecimal amount,String description) throws InsufficientBalanceException;
	/**
	 * 借款人取现
	 * @param amount
	 * @exception InsufficientBalanceException 余额不足
	 */
	public Integer cashBorrowerAccount(Integer borrowerAccountId,BigDecimal amount,String description) throws InsufficientBalanceException;
	
	/**
	 * 将还款除不尽的零钱放入平台自有账户
	 * @param paybackId
	 * @param amount
	 * @param description
	 * @return
	 */
	public Integer storeChange(Integer borrowerAccountId,Integer paybackId,BigDecimal chiefamount,BigDecimal interest,String description) throws IllegalConvertException;
	/**
	 * 修改资金流的状态
	 * 在该方法中实际对账户(借款人/贷款人)进行修改
	 * @param cashStreamId 资金流ID
	 * @param state
	 * @throws IllegalStateException
	 */
	public void changeCashStreamState(Integer cashStreamId,int state) throws IllegalConvertException;
	
	/**
	 * 调用第三方平台验证
	 * @param cashStreamId
	 */
	public void checkThroughThirdPlatform(Integer cashStreamId);
	/**
	 * 找到未处理的现金流
	 * @return
	 */
	public List<CashStream> findAllDirtyCashStream();
	/**
	 * 获取当前贷款人资金流
	 * @param action 资金流行为{@link CashStream.cation},-1为不限
	 * @param state 资金流状态,-1为不限
	 * @param offset 翻页偏移量，从0开始
	 * @param recnum 返回结果数
	 * @return 分页结果，详细Key查看{@link Pagination}
	 */
	public Map<String, Object> findLenderCashStreamByActionAndState(int action,int state,int offset,int recnum);
	/**
	 * 获取当前借款人的资金流
	 * @param action 资金流行为{@link CashStream.cation},-1为不限
	 * @param state 资金流状态,-1为不限
	 * @param offset 翻页偏移量，从0开始
	 * @param recnum 返回结果数
	 * @return 分页结果，详细Key查看{@link Pagination}
	 */
	public Map<String, Object> findBorrowerCashStreamByActionAndState(int action,int state,int offset,int recnum);
	/**
	 * 找到所有贷款人的已还款
	 * @param offset 翻页偏移量，从0开始
	 * @param recnum 返回结果数
	 * @return
	 */
	public Map<String,Object> findLenderRepayCashStream(int offset,int recnum);
	/**
	 * 找到所有待还款的计划，即时计算所有Lender购买的还款中产品所有未还款的payback，同时将payback的金额替换为Lender的金额
	 * @return
	 */
	public List<PayBack> findLenderWaitforRepay();
	/**
	 * 返回贷款人的已还款总览
	 * @return
	 */
	public Map<String,PayBackDetail> getLenderRepayedDetail();
	/**
	 * 返回贷款人的待还款总览
	 * @return
	 */
	public Map<String,PayBackDetail> getLenderWillBeRepayedDetail();
	/**
	 * 返回借款人的已还款总览
	 * @return
	 */
	public Map<String,PayBackDetail> getBorrowerRepayedDetail();
	/**
	 * 返回借款人的待还款总览
	 * @return
	 */
	public Map<String,PayBackDetail> getBorrowerWillBeRepayedDetail();
	/**
	 * 提现退回
	 * @throws IllegalConvertException 
	 */
	public void returnCash(Integer cashStreamId, String loanNo) throws IllegalConvertException;
}
