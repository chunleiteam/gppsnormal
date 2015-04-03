package gpps.service.impl;

import static gpps.tools.ObjectUtil.checkNullObject;
import static gpps.tools.StringUtil.checkNullAndTrim;
import gpps.service.ILoginService;
import gpps.service.exception.FrozenException;
import gpps.service.exception.SMSException;
import gpps.service.exception.ValidateCodeException;
import gpps.service.message.IMessageService;
import gpps.tools.GraphValidateCode;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public abstract class AbstractLoginServiceImpl implements ILoginService {
	@Autowired
	IMessageService messageService;
	protected Logger logger=Logger.getLogger(AbstractLoginServiceImpl.class);
	@Override
	public void loginOut() {
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		session.removeAttribute(SESSION_ATTRIBUTENAME_USER);
	}

	@Override
	public void sendMessageValidateCode(String phone) throws FrozenException{
		//TODO 发送短信
		HttpSession session=getCurrentSession();
		if(session.getAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME)!=null)
		{
			if((Long)(session.getAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME))+MESSAGEVALIDATECODEINTERVAL>System.currentTimeMillis())
				throw new FrozenException("验证码二十分钟内有效，五分钟内不可重复发送");
		}
			
		String validateCode=getRandomValidateCode(5);
		System.out.println("messageValidateCode="+validateCode);
		session.setAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE, validateCode);
		session.setAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME, System.currentTimeMillis());
		
		Map<String, String> param = new HashMap<String, String>();
		param.put(IMessageService.PARAM_PHONE, phone);
		param.put(IMessageService.PARAM_VALIDATE_CODE, validateCode);
		
		try{
			messageService.sendMessage(IMessageService.MESSAGE_TYPE_SENDVALIDATECODE, IMessageService.USERTYPE_LENDER, 0, param);
		}catch(SMSException e){
			logger.error(e.getMessage());
		}
	}

	@Override
	public void writeGraphValidateCode(OutputStream os)throws IOException {
		GraphValidateCode validateCode=new GraphValidateCode(160, 40, 4, 40);
		getCurrentSession().setAttribute(SESSION_ATTRIBUTENAME_GRAPHVALIDATECODE, validateCode.getCode());
		validateCode.write(os);
	}
	public HttpSession getCurrentSession()
	{
		 HttpSession session=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		 return session;
	}
	protected void checkMessageValidateCode(String messageValidateCode) throws ValidateCodeException
	{
		HttpSession session =getCurrentSession();
		messageValidateCode=checkNullAndTrim("messageValidateCode", messageValidateCode);
		String originalMessageValidateCode=String.valueOf(checkNullObject("originalMessageValidateCode", session.getAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE)));
		
		long messageValidateCodeSendTime=(Long)session.getAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME);
		
		
		
		if(messageValidateCodeSendTime+MESSAGEVALIDATECODEEXPIRETIME<System.currentTimeMillis()){
			//如果验证码过期，删除掉内存中的验证码并报过期的异常
			session.removeAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE);
			session.removeAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME);
			throw new ValidateCodeException("验证码过期");
		}else if(!originalMessageValidateCode.equals(messageValidateCode)){
			//如果用户只是输错了验证码，不删除掉内存中的验证码，只是报验证码不正确异常
			throw new ValidateCodeException("短信验证码错误,请输入正确验证码，短信验证码20分钟内有效");
		}else{
			//如果用户输入正确，则删除掉内存中的验证码，不报异常
			session.removeAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODE);
			session.removeAttribute(SESSION_ATTRIBUTENAME_MESSAGEVALIDATECODESENDTIME);
		}
	}
	
	
	protected boolean onlyCheckGraphValidateCode(String graphValidateCode) throws ValidateCodeException{
		HttpSession session =getCurrentSession();
		graphValidateCode=checkNullAndTrim("graphValidateCode", graphValidateCode);
		String originalGraphValidateCode=String.valueOf(checkNullObject("originalGraphValidateCode", session.getAttribute(SESSION_ATTRIBUTENAME_GRAPHVALIDATECODE)));
		if(!originalGraphValidateCode.toLowerCase().equals(graphValidateCode.toLowerCase()))
			throw new ValidateCodeException("图片验证码不正确");
		
		return true;
	}
	
	protected void checkGraphValidateCode(String graphValidateCode) throws ValidateCodeException
	{
		HttpSession session =getCurrentSession();
		graphValidateCode=checkNullAndTrim("graphValidateCode", graphValidateCode);
		String originalGraphValidateCode=String.valueOf(checkNullObject("originalGraphValidateCode", session.getAttribute(SESSION_ATTRIBUTENAME_GRAPHVALIDATECODE)));
		session.removeAttribute(SESSION_ATTRIBUTENAME_GRAPHVALIDATECODE);//用过一次即删除
		if(!originalGraphValidateCode.toLowerCase().equals(graphValidateCode.toLowerCase()))
			throw new ValidateCodeException("图片验证码不正确");
	}
	protected String getProcessedPassword(String password)
	{
		String psw = checkNullAndTrim("password", password)+PASSWORDSEED;
		byte[] pswarr = null;
		try{
			pswarr = psw.getBytes("utf-8");
		}catch(UnsupportedEncodingException e){
			
		}
		return DigestUtils.md5Hex(pswarr);
	}
//	private char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
//			'K', 'L', 'M', 'N',  'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
//			'X', 'Y', 'Z','a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
//			'k', 'l', 'm', 'n',  'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
//			'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	private char[] codeSequence = {'1', '2', '3', '4', '5', '6', '7', '8', '9','0'};
	private String getRandomValidateCode(int length)
	{
		StringBuffer randomCode = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}
}
