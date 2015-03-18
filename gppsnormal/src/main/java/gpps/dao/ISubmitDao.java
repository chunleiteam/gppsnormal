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
	public List<Submit> findAllByProduct(Integer productId);
	public List<Submit> findAllByProductAndState(@Param("productId")Integer productId,@Param("state")int state);
	public Submit find(Integer id);
	public void changeState(@Param("id")Integer id,@Param("state")int state,@Param("lastmodifytime")long lastmodifytime);
	public void delete(Integer id);
	public List<Submit> findAllPayedByLenderAndProductStates(@Param("lenderId")Integer lenderId,@Param("productStates") List<Integer> productStates,@Param("offset")int offset,@Param("recnum")int recnum);
	public int countByLenderAndProductStates(@Param("lenderId")Integer lenderId,@Param("productStates") List<Integer> productStates);
	
	public List<Submit> findAllByProductAndStateWithPaged(@Param("productId")Integer productId,@Param("state")int state,@Param("offset")int offset,@Param("recnum")int recnum);
	public int countByProductAndStateWithPaged(@Param("productId")Integer productId,@Param("state")int state);
	
	public int countByLenderAndProductAndState(@Param("lenderId")Integer lenderId, @Param("productId")Integer productId,@Param("state")int state);
	
	public List<Submit> findAllByState(int state);
}
