package gpps.dao;

import gpps.model.Submit;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ISubmitDao {
	public int countAll();
	public void create(Submit submit);
	public List<Submit> findAllByLender(@Param("lenderId")Integer lenderId,@Param("offset")int offset,@Param("recnum")int recnum);
	public int countByLender(Integer lenderId);
	/**
	 * 
	 * @param lenderId
	 * @param states null为不限
	 * @return
	 */
	public List<Submit> findAllByLenderAndStates(@Param("lenderId") Integer lenderId,@Param("states")List<Integer> states);
	
	public List<Submit> findAllByLenderAndStatesAndCreatetime(@Param("lenderId") Integer lenderId,@Param("states")List<Integer> states, @Param("starttime")Long starttime, @Param("endtime")Long endtime);
	
	public List<Submit> findAllByProduct(Integer productId);
	public List<Submit> findAllByProductAndState(@Param("productId")Integer productId,@Param("state")int state);
	public Submit find(Integer id);
	public void changeState(@Param("id")Integer id,@Param("state")int state,@Param("lastmodifytime")long lastmodifytime);
	public void delete(Integer id);
	
	/**
	 * 若lenderId==null,则不限lender
	 * 
	 * */
	public List<Submit> findAllPayedByLenderAndProductStates(@Param("lenderId")Integer lenderId,@Param("productStates") List<Integer> productStates,@Param("offset")int offset,@Param("recnum")int recnum);
	/**
	 * 若lenderId==null,则不限lender
	 * 
	 * */
	public int countByLenderAndProductStates(@Param("lenderId")Integer lenderId,@Param("productStates") List<Integer> productStates);
	
	public int countByLenderAndStateAndProductStatesAndPurchaseFlag(@Param("lenderId")Integer lenderId, @Param("state")int state,@Param("productStates") List<Integer> productStates, @Param("purchaseFlag")int purchaseFlag);
	public List<Submit> findAllByLenderAndStateAndProductStatesAndPurchaseFlagWithPaged(@Param("lenderId")Integer lenderId, @Param("state")int state,@Param("productStates") List<Integer> productStates, @Param("purchaseFlag")int purchaseFlag,@Param("offset")int offset,@Param("recnum")int recnum);
	
	
	public List<Submit> findAllByProductAndStateWithPaged(@Param("productId")Integer productId,@Param("state")int state,@Param("offset")int offset,@Param("recnum")int recnum);
	public int countByProductAndStateWithPaged(@Param("productId")Integer productId,@Param("state")int state);
	
	public int countByLenderAndProductAndState(@Param("lenderId")Integer lenderId, @Param("productId")Integer productId,@Param("state")int state);
	
	public int countByLenderAndState(@Param("lenderId")Integer lenderId,@Param("state")int state);
	
	public List<Submit> findAllByState(int state);
	
	
	/**
	 * 回购时对对应的submit的修改
	 * @param borrowerId 回购企业的ID
	 * @param lenderId   回购时代持用户的ID
	 * @param id 		 回购的标的的ID
	 * 
	 * */
	public void purchaseBack(@Param("borrowerId")Integer borrowerId, @Param("lenderId")Integer lenderId, @Param("id")Integer id);
	
	/**
	 * 购买债权时对对应的submit的修改
	 * @param lenderId   购买用户的ID
	 * @param id 		 标的的ID
	 * @param holdingstarttime   购买时间
	 * */
	public void purchase(@Param("lenderId")Integer lenderId, @Param("id")Integer id, @Param("holdingstarttime")long holdingstarttime);
}
