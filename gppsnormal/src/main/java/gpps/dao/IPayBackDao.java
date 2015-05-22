/**
 * 
 */
package gpps.dao;

import gpps.model.PayBack;
import gpps.service.PayBackDetail;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IPayBackDao {
	public void create(PayBack payback);

	public List<PayBack> findAllByProduct(Integer productId);

	public void changeState(@Param("id") Integer id, @Param("state") int state,@Param("datetime") long datetime);
	
	public PayBack find(Integer id);

	public void delete(Integer id);
	
	public List<PayBack> findByProductsAndState(@Param("productIds")List<Integer> productIds,@Param("state")int state);
	
	public void update(PayBack payBack);
	
	public PayBack findLastest(Integer productId);
	
	public List<PayBack> findByBorrowerAndState(@Param("borrowerAccountId")Integer borrowerAccountId,@Param("states")List<Integer> states,@Param("starttime")long starttime,@Param("endtime")long endtime,@Param("offset")int offset,@Param("recnum")int recnum);
	public int countByBorrowerAndState(@Param("borrowerAccountId")Integer borrowerAccountId,@Param("states")List<Integer> states,@Param("starttime")long starttime,@Param("endtime")long endtime);
	
	public int countByBorrowerAndState2(@Param("borrowerAccountId")Integer borrowerAccountId,@Param("state")Integer state,@Param("starttime")long starttime,@Param("endtime")long endtime);
	
	public PayBackDetail sumBorrowerWillBeRepayedPayBacks(@Param("borrowerAccountId")Integer borrowerAccountId,@Param("states")List<Integer> states,@Param("endtime")long endtime);
	public PayBackDetail sumBorrowerRepayedPayBacks(@Param("borrowerAccountId")Integer borrowerAccountId,@Param("states")List<Integer> states,@Param("starttime")long starttime);
	
	public List<PayBack> findBorrowerWaitForRepayed(Integer borrowerAccountId);
	
	public List<PayBack> findByTimeAndState(@Param("starttime")long starttime, @Param("endtime")long endtime, @Param("state")Integer state);
	
	/**
	 * 主要用于奖励的查询，当type==2的时候，checkResult代表了奖励的批次
	 * 
	 * */
	public List<PayBack> findByTypeAndCheckResult(@Param("type")int type, @Param("checkResult")int checkResult);
	
	public void changeCheckResult(@Param("id")Integer id,@Param("checkResult")int checkResult);
	
	public void deleteByProduct(Integer productId);
}
