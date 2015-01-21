package gpps.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import gpps.model.Activity;

public interface IActivityDao {
	public void create(Activity activity);
	public Activity find(Integer id);
	public int countByState(@Param("state")int state);
	public List<Activity> findByState(@Param("state")int state,@Param("offset")int offset,@Param("recnum")int recnum);
	public void update(@Param("id")Integer id,@Param("name")String name,@Param("applyendtime")long applyendtime,@Param("starttime")long starttime,@Param("url")String url,@Param("state")int state, @Param("description")String description);
	public String findAccessory(Integer id);
	public void updateAccessory(@Param("id")Integer id,@Param("accessory")String accessory);
}