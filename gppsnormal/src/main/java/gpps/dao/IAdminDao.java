package gpps.dao;

import gpps.model.Admin;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface IAdminDao {
	public int countAll();
	public List<Admin> findAll();
	public Admin find(Integer id);
	public Admin findByLoginId(String loginId);
	public Admin findByLoginIdAndPassword(@Param("loginId") String loginId,@Param("password") String password);
	public Admin findByTel(String tel);
	public Admin findByEmail(String email);
	public void create(Admin admin);
	public void changePrivilege(@Param("id") Integer id,@Param("privilege") int privilege);
	public void changePassword(@Param("id") Integer id,@Param("password") String password);
	public void delete(Integer id);
}
