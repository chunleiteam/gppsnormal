package gpps.dao;

import gpps.model.FinancingRequest;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IFinancingRequestDao {
	public void create(FinancingRequest financingRequest);
	public void changeState(@Param("id")Integer id,@Param("state")int state, @Param("lastmodifytime")long lastmodifytime);
	/**
	 * 根据借款人找到所有的融资请求
	 * @param borrowerId
	 * @param state -1为不限
	 * @return
	 */
	public FinancingRequest find(Integer id);
	
	public List<FinancingRequest> findByBorrowerAndState(@Param("borrowerId")Integer borrowerId,@Param("state")int state);
	
	public List<FinancingRequest> findByState(@Param("state")int state,@Param("offset")int offset,@Param("recnum")int recnum);
	
	public int countByState(@Param("state")int state);
	
	public void delete(Integer id);
}
