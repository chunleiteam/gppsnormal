package gpps.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import gpps.model.Subscribe;

public interface ISubscribeDao {
	public void create(Subscribe subscribe);
	public Subscribe find(Integer id);
	public List<Subscribe> findAllByProductIdAndState(@Param("productId")Integer productId, @Param("state")int state);
	public int countByProductIdAndState(@Param("productId")Integer productId, @Param("state")int state);
	public int countByProductIdAndLenderAndState(@Param("productId")Integer productId, @Param("lenderId")Integer lenderId, @Param("state")int state);
	public void changeState(@Param("id")Integer id, @Param("confirmTime")Long confirmTime, @Param("state")int state, @Param("auditAmount")int auditAmount, @Param("description")String description);
}
