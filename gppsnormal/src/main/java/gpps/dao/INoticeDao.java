package gpps.dao;

import gpps.model.Notice;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface INoticeDao {
	public void create(Notice notice);
	public Notice find(Integer id);
	public int count(@Param("publicType")int publicType,@Param("usefors")List<Integer> usefors,@Param("level")int level);
	public List<Notice> findAll(@Param("publicType")int publicType,@Param("usefors")List<Integer> usefors,@Param("level")int level,@Param("offset")int offset,@Param("recnum")int recnum);
}
