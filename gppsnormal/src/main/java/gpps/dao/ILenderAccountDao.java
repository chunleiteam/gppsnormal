package gpps.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import gpps.model.LenderAccount;

public interface ILenderAccountDao {
	public void create(LenderAccount lenderAccount);
	public LenderAccount find(Integer accountId);
	public int countAll();
	public List<LenderAccount> findAll(@Param("offset")int offset,@Param("recnum")int recnum);
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
	 * usable=usable-amount
	 * freeze=freeze+amount
	 * @param accountId
	 * @param amount
	 */
	public void freeze(@Param("accountId") Integer accountId,@Param("amount") BigDecimal amount);//冻结
	/**
	 * 解冻
	 * freeze=freeze-amount
	 * usable=usable+amount
	 * @param accountId
	 * @param amount
	 */
	public void unfreeze(@Param("accountId") Integer accountId,@Param("amount") BigDecimal amount);
	/**
	 * 支付给借款人
	 * freeze=freeze-principalAmount
	 * used=used+principalAmount
	 * expectedincome=expectedincome+expectedIncomeAmount
	 * @param accountId 账户ID
	 * @param principalAmount 扣除本金金额
//	 * @param expectedIncomeAmount 预期收益金额
	 */
	public void pay(@Param("accountId") Integer accountId,@Param("principalAmount") BigDecimal principalAmount);
	/**
	 * 购买债权
	 * used=used-chiefAmount(-)
	 * total=total+interest(-)
	 * usable=usable+chiefAmount(-)+interest(-)
	 * totalincome=totalincome+interest(-)
	 * 
	 * 
	 * @param accountId 账户ID
	 * @param chiefAmount 扣除本金金额
	 * @param interest 扣除利息金额
	 */
	public void purchase(@Param("accountId") Integer accountId,@Param("chiefAmount") BigDecimal chiefAmount, @Param("interest")BigDecimal interest);
	
	/**
	 * 借款人还款
	 * total=total+incomeAmount
	 * totalincome=totalincome+incomeAmount
	 * expectedincome=expectedincome-expectedIncomeAmount
	 * used=used-principalAmount
	 * usable=usable+principalAmount+incomeAmount
	 * @param accountId 账户ID
	 * @param principalAmount 还款本金金额
	 * @param incomeAmount 还款收益金额
	 */
	public void repay(@Param("accountId") Integer accountId,@Param("principalAmount") BigDecimal principalAmount,@Param("incomeAmount") BigDecimal incomeAmount);
	
	/**
	 * 债权回购
	 * total=total+fee(-)
	 * usable = usable+chiefAmount+fee(-)
	 * used = used-chiefAmount
	 * totalincome = totalincome+fee(-)
	 * @param accountId 账户ID
	 * @param chiefAmount 回购本金金额
	 * @param fee 回购手续费
	 * */
	public void purchaseBack(@Param("accountId") Integer accountId,@Param("chiefAmount") BigDecimal chiefAmount,@Param("fee") BigDecimal fee);
	
	/**
	 * 取现
	 * total=total-amount
	 * usable=usable-amount
	 * @param accountId
	 * @param amount
	 */
	public void cash(@Param("accountId") Integer accountId,@Param("amount") BigDecimal amount);
	/**
	 * 删除
	 * @param accountId
	 */
	public void delete(Integer accountId);
}
