package gpps.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import gpps.model.Admin;
import gpps.model.Lender;
import gpps.service.exception.LoginException;
import gpps.service.exception.ValidateCodeException;

public interface IAdminService extends ILoginService{
	public Admin register(Admin admin) throws IllegalArgumentException, LoginException;
	public Admin find(int id);
	public Admin getCurrentUser();
	public List<Admin> findAll();
	public void changePrivilege(Integer id,int privilege);
}
