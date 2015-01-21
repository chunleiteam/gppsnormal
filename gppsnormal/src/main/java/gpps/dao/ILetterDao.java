package gpps.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import gpps.model.Letter;

public interface ILetterDao {
	public void create(Letter letter);
	public Letter find(Integer id);
	public int countByReceiver(@Param("markRead")int markRead,@Param("receivertype")int receivertype,@Param("receiverId")Integer receiverId);
	public List<Letter> findByReceiver(@Param("markRead")int markRead,@Param("receivertype")int receivertype,@Param("receiverId")Integer receiverId,@Param("offset")int offset,@Param("recnum")int recnum);
	public void changeMarkRead(@Param("id")Integer id,@Param("markRead")int markRead,@Param("readtime")long readtime);
	public int countAll();
	public List<Letter> findAll(@Param("offset")int offset,@Param("recnum")int recnum);
}
