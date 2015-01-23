package gpps.test.handle;

import static gpps.tools.StringUtil.checkNullAndTrim;
import gpps.dao.ICashStreamDao;
import gpps.service.IAdminService;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;


public class PasswordHandle {
	public static final String PASSWORDSEED="PASSWORDSEED";
	
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	
	protected static IAdminService adminService = context.getBean(IAdminService.class);
	
	
	
	public static void main(String args[]) throws Exception{
		
		String psw = new String("111111".getBytes(), "utf-8");
		
		String password1 = getProcessedPassword(psw + PASSWORDSEED);
		String password2 = DigestUtils.md5Hex(psw.getBytes("iso-8859-1"));
		String password3 = DigestUtils.md5Hex(psw.getBytes("utf-8"));
		String password4 = DigestUtils.md5Hex(psw.getBytes("GBK"));
		System.out.println(password1);
		System.out.println(password2);
		System.out.println(password3);
		System.out.println(password4);
	}
	
	protected static String getProcessedPassword(String password)
	{
		return DigestUtils.md5Hex(checkNullAndTrim("password", password)+PASSWORDSEED);
	}
}
