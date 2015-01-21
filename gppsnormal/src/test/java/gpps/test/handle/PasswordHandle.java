package gpps.test.handle;

import static gpps.tools.StringUtil.checkNullAndTrim;

import org.apache.commons.codec.digest.DigestUtils;


public class PasswordHandle {
	public static final String PASSWORDSEED="PASSWORDSEED";
	
	
	
	
	
	public static void main(String args[]){
		String password = DigestUtils.md5Hex("gppsnormal123"+PASSWORDSEED);
		String password2 = DigestUtils.md5Hex(checkNullAndTrim("password", "gppsnormal123")+PASSWORDSEED);
		System.out.println(password);
		System.out.println(password2);
	}
}
