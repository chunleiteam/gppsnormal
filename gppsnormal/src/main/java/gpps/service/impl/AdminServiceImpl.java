package gpps.service.impl;

import static gpps.tools.StringUtil.checkNullAndTrim;
import gpps.dao.IAdminDao;
import gpps.model.Admin;
import gpps.service.IAdminService;
import gpps.service.exception.LoginException;
import gpps.service.exception.ValidateCodeException;
import gpps.tools.StringUtil;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class AdminServiceImpl extends AbstractLoginServiceImpl implements IAdminService{
	@Autowired
	IAdminDao adminDao;
	@Override
	public void login(String loginId, String password, String graphValidateCode)
			throws LoginException, ValidateCodeException {
		checkGraphValidateCode(graphValidateCode);
		HttpSession session =getCurrentSession();
		loginId=checkNullAndTrim("loginId", loginId);
		password=getProcessedPassword(checkNullAndTrim("password", password)+PASSWORDSEED);
		Admin admin=adminDao.findByLoginIdAndPassword(loginId, password);
		if(admin==null)
			throw new LoginException("Login fail!!");
		session.setAttribute(SESSION_ATTRIBUTENAME_USER, admin);
	}

	@Override
	public void changePassword(String loginId, String password,
			String messageValidateCode) throws LoginException,ValidateCodeException {
		Admin admin=adminDao.findByLoginId(loginId);
		if(admin==null)
			admin=adminDao.findByTel(loginId);
		if(admin==null)
			throw new LoginException("LoginId is not existed");
		adminDao.changePassword(admin.getId(), getProcessedPassword(checkNullAndTrim("password", password)+PASSWORDSEED));
	}

	@Override
	public boolean isLoginIdExist(String loginId) {
		Admin admin=adminDao.findByLoginId(loginId);
		return admin==null?false:true;
	}

	@Override
	public String getProcessedTel(String loginId) {
		Admin admin=adminDao.findByLoginId(loginId);
		if(admin==null)
			return null;
		String tel=admin.getTel();
		char[] processedTel=tel.toCharArray();
		for(int i=4;i<8;i++)
		{
			processedTel[i]='*';
		}
		return String.valueOf(processedTel);
	}

	@Override
	public Admin register(Admin admin)
			throws  IllegalArgumentException, LoginException {
		admin.setLoginId(checkNullAndTrim("loginId", admin.getLoginId()));
		if(StringUtil.isDigit(admin.getLoginId()))
			throw new IllegalArgumentException("登录名不能全部为数字");
		admin.setPassword(getProcessedPassword(checkNullAndTrim("password", admin.getPassword())+PASSWORDSEED));
		admin.setCreatetime(System.currentTimeMillis());
		admin.setTel(checkNullAndTrim("tel", admin.getTel()));
		if(isLoginIdExist(admin.getLoginId()))
			throw new LoginException("LoginId is existed");
		if(isPhoneNumberExist(admin.getTel()))
			throw new LoginException("Tel is existed");
		adminDao.create(admin);
		admin.setPassword(null);
		getCurrentSession().setAttribute(SESSION_ATTRIBUTENAME_USER, admin);
		return admin;
	}
	@Override
	public Admin find(int id) {
		return adminDao.find(id);
	}

	@Override
	public Admin getCurrentUser() {
		HttpSession session=getCurrentSession();
		if(session==null)
			return null;
		Object user=session.getAttribute(SESSION_ATTRIBUTENAME_USER);
		if(user instanceof Admin)
			return (Admin)session.getAttribute(SESSION_ATTRIBUTENAME_USER);
		return null;
	}

	@Override
	public boolean isPhoneNumberExist(String phoneNumber) {
		Admin admin=adminDao.findByTel(phoneNumber);
		return admin==null?false:true;
	}
	@Override
	public boolean isEmailExist(String email) {
		Admin admin=adminDao.findByEmail(email.trim());
		return admin==null?false:true;
	}

	@Override
	public boolean isIdentityCardExist(String identityCard) {
		return false;
	}

	@Override
	public List<Admin> findAll() {
		return adminDao.findAll();
	}

	@Override
	public void changePrivilege(Integer id, int privilege) {
		adminDao.changePrivilege(id, privilege);
	}
}
