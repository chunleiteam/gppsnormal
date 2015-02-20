package gpps.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import gpps.model.CashStream;
import gpps.model.PayBack;
import gpps.service.CashStreamSum;
import gpps.service.PayBackDetail;

public interface ICashStreamDao {
	public void create(CashStream cashStream);
	public void changeCashStreamState(@Param("cashStreamId") Integer cashStreamId,@Param("state") int state);
	public List<CashStream> findSubmitCashStream(Integer submitId);
	public CashStream find(Integer id);
	public void delete(Integer id);
	public List<CashStream> findByState(int state);
	public List<CashStream> findByAction(int action);
	public void deleteByLenderAccountId(Integer accountId);//测试使用
	public void deleteByBorrowerAccountId(Integer accountId);//测试使用
	/**
	 * 
	 * @param action -1为不限
	 * @param state  -1为不限
	 * @return
	 */
	public List<CashStream> findByActionAndState(@Param("lenderAccountId")Integer lenderAccountId,@Param("borrowerAccountId")Integer borrowerAccountId,@Param("action")int action,@Param("state")int state,@Param("offset")int offset,@Param("recnum")int recnum);
	
	/**
	 * @param action -1为不限
	 * @param start -1为不限
	 * @param end -1为不限
	 * 
	 * */
	public List<CashStream> findByActionAndTime(@Param("action")int action, @Param("start")long start, @Param("end")long end);
	
	public int countByActionAndState(@Param("lenderAccountId")Integer lenderAccountId,@Param("borrowerAccountId")Integer borrowerAccountId,@Param("action")int action,@Param("state")int state);
	public PayBackDetail sumLenderRepayed(@Param("lenderAccountId")Integer lenderAccountId,@Param("starttime")long starttime,@Param("endtime")long endtime);
	/**
	 * 找到订单的所有还款
	 * @param submitId
	 * @param payBackId null为不限
	 * @return
	 */
	public List<CashStream> findRepayCashStream(@Param("submitId")Integer submitId,@Param("payBackId")Integer payBackId);
	
	public List<CashStream> findBySubmitAndPayBackAndActionAndState(@Param("submitId")Integer submitId,@Param("payBackId")Integer payBackId,@Param("action")int action,@Param("state")int state);
	
	/**
	 * 找到针对某笔投资的某笔还款的指定状态的现金流
	 * @param submitId
	 * @param payBackId null为不限
	 * @param action 动作
	 * @return
	 */
	public List<CashStream> findRepayCashStreamByAction(@Param("submitId")Integer submitId,@Param("payBackId")Integer payBackId,@Param("action")int action);
	
	/**
	 * 找到针对某一次还款的指定状态的所有现金流
	 * @param payBackId 还款ID
	 * @param action 动作
	 * 
	 * */
	public List<CashStream> findByRepayAndAction(@Param("payBackId")Integer payBackId,@Param("action")int action);
	
	
	
	public void updateLoanNo(@Param("cashStreamId") Integer cashStreamId,@Param("loanNo") String loanNo,@Param("fee")BigDecimal fee);
	/**
	 * 根据action及loanNo查找流水
	 * @param action -1为不限
	 * @param loanNo 
	 * @return
	 */
	public List<CashStream> findSuccessByActionAndLoanNo(@Param("action")int action,@Param("loanNo")String loanNo);
	
	public CashStreamSum sumCashStream(@Param("lenderAccountId")Integer lenderAccountId,@Param("borrowerAccountId")Integer borrowerAccountId,@Param("actions")List<Integer> actions);
	public CashStreamSum sumProduct(@Param("productId")Integer productId,@Param("action")int action);
	public CashStreamSum sumPayBackByAction(@Param("paybackId")Integer paybackId, @Param("action")int action);
	
	public CashStream findBySubmitAndState(@Param("submitId")Integer submitId,@Param("action")int action);
	
	public List<CashStream> findBySubmitAndActionAndState(@Param("submitId")Integer submitId,@Param("action")int action,@Param("state")int state);
}
