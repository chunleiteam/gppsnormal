package gpps.dao;

import gpps.model.Task;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ITaskDao {
	public void create(Task task);
	public Task find(Integer id);
	public List<Task> findByState(int state);
	public void changeState(@Param("id")Integer id,@Param("state")int state);
	public void delete(Integer id);
}
