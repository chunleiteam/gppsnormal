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
	 * 针对回购企业的还款
	 * 
	 * total=total+interest
	 * usable=usable+chiefAmount+interest
	 * used=used-chiefAmount
	 * totalfee=totalfee+interest
	 * @param accountId 回购企业账户ID
	 * @param chiefAmount 还款本金
	 * @param interest    还款利息
	 * 
	 * 
	 * */
	public void purchaseBackRepay(@Param("accountId") Integer accountId,@Param("chiefAmount") BigDecimal chiefAmount, @Param("interest")BigDecimal interest);
	
	/**
	 * 债权回购
	 * total=total+fee
	 * usable=usable+chiefAmount(-)+fee
	 * used=used-chiefAmount(-)
	 * totalFee=totalFee+fee
	 * @param accountId 账户ID
	 * @param chiefAmount 回购本金金额
	 * @param fee 回购手续费
	 * */
	public void purchaseBack(@Param("accountId") Integer accountId,@Param("chiefAmount") BigDecimal chiefAmount,@Param("fee") BigDecimal fee);
	
	/**
	 * 用户购买债权
	 * total=total+interest
	 * usable=usable+chiefAmount+interest
	 * used=used-chiefAmount
	 * totalFee=totalFee+interest
	 * @param accountId 账户ID
	 * @param chiefAmount 购买本金金额
	 * @param fee 购买所付利息
	 * */
	public void purchase(@Param("accountId") Integer accountId,@Param("chiefAmount") BigDecimal chiefAmount,@Param("interest") BigDecimal interest);
	
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
