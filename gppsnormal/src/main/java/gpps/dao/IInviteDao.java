package gpps.dao;

import org.apache.ibatis.annotations.Param;

import gpps.model.Invite;

public interface IInviteDao {
	public void create(Invite invite);
	public Invite find(Integer id);
	public Invite findByCode(String code);
	public void update(@Param("code")String code, @Param("userId")Integer userId, @Param("state")int state);
	public Integer getMaxId();
}
