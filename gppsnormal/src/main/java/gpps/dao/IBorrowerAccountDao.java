package gpps.dao;

import gpps.model.BorrowerAccount;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;

public interface IBorrowerAccountDao {
	public void create(BorrowerAccount borrowerAccount);
	public BorrowerAccount find(Integer accountId);
	/**
	 * 充值
	 * total=total+amount
	 * usable=usable+amount
	 * @param accountId
	 * @param amount
	 */
	public void recharge(@Param("accountId") Integer accountId,@Param("amount") BigDecimal amount);
	/**
	 * 冻结
	 * freeze=freeze+amount
	 * usable=usable-amount
	 * @param accountId
	 * @param amount
	 */
	public void freeze(@Param("accountId") Integer accountId,@Param("amount") BigDecimal amount);
	
	/**
	 * 解冻
	 * @param accountId
	 * @param amount
	 */
	public void unfreeze(@Param("accountId") Integer accountId,@Param("amount") BigDecimal amount);
//	/**
//	 * 解冻
//	 * freeze=freeze-amount
//	 * usable=usable+amount
//	 * @param accountId
//	 * @param amount
//	 */
//	public void unfreeze(@Param("accountId") Integer accountId,@Param("amount") BigDecimal amount);
	/**
	 * 支付给借款人
	 * total=total+amount
	 * usable=usable+amount
	 * @param accountId 账户ID
	 * @param amount 支付金额
	 */
	public void pay(@Param("accountId") Integer accountId,@Param("amount") BigDecimal amount);
	/**
	 * 借款人还款
	 * total=total-amount
	 * freeze=freeze-amount
	 * @param accountId 账户ID
	 * @param amount 还款金额
	 */
	public void repay(@Param("accountId") Integer accountId,@Param("amount") BigDecimal amount);
	/**
	 * 取现
	 * total=total-amount
	 * usable=usable-amount
	 * @param accountId
	 * @param amount
	 */
	public void cash(@Param("accountId") Integer accountId,@Param("amount") BigDecimal amount);
	public void delete(Integer id);
}
