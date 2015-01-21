package gpps.service;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpSession;

import com.easyservice.support.EasyServiceConstant;

import gpps.service.exception.FrozenException;
import gpps.service.exception.LoginException;
import gpps.service.exception.ValidateCodeException;

public interface ILoginService {
	public static final long MESSAGEVALIDATECODEEXPIRETIME=5*60*1000;//短信验证码有效时间:5分钟
	public static final long MESSAGEVALIDATECODEINTERVAL=60*1000;//获取短信验证码间隔时间:1分钟
	public static final String SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME="messageValidateCodeSendTime";//短信验证码发送时间在Session中的KEY常量，value为long类型
	public static final String SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE="messageValidateCode";//短信验证码在Session中的KEY常量
	public static final String SESSION_ATTRIBUTENAME_GRAPHVALIDATECODE="graphValidateCode";//图形验证码在Session中的KEY常量
	public static final String PASSWORDSEED="PASSWORDSEED";
	public static final String SESSION_ATTRIBUTENAME_USER=EasyServiceConstant.SESSION_ATTRIBUTENAME_USER;
	/**
	 * 登录
	 * @param loginId 登录名
	 * @param password 密码
	 * @param graphValidateCode 图形验证码
	 * @throws Exception
	 */
	public void login(String loginId,String password,String graphValidateCode) throws LoginException,ValidateCodeException;
	/**
	 * 用户修改密码
	 * @param loginId 登录ID/手机号
	 * @param password 修改密码
	 * @param messageValidateCode 短信验证码
	 * @throws LoginException 
	 * @throws Exception
	 */
	public void changePassword(String loginId,String password,String messageValidateCode) throws ValidateCodeException, LoginException;
	/**
	 * 当前用户登出
	 */
	public void loginOut();
	/**
	 * 发送短信验证码（注册以及修改密码时用到）
	 * 获取后放入用户session中，并记录发送时间，待用户注册/修改密码时进行验证
	 * @throws FrozenException 冻结(未到发送间隔时间)
	 */
	public void sendMessageValidateCode(String phone) throws FrozenException;
	
	public void writeGraphValidateCode(OutputStream os) throws IOException;
	/**
	 * 注册登录名是否存在
	 * 用于用户注册/密码找回
	 * @param loginId 待注册的登录名
	 * @return true:重复；false：不重复
	 */
	public boolean isLoginIdExist(String loginId);
	
	public boolean isPhoneNumberExist(String phoneNumber);
	
	public boolean isIdentityCardExist(String identityCard);
	
	public boolean isEmailExist(String email);
	/**
	 * 用户密码找回
	 * @param loginId 登录ID
	 * @return	处理后的Tel，形如：135****4536
	 */
	public String getProcessedTel(String loginId);
	
	public HttpSession getCurrentSession();
}
