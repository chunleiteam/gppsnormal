package gpps.test.check;

import gpps.dao.IBorrowerDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.ILenderDao;
import gpps.inner.service.IInnerThirdPaySupportService;
import gpps.service.IAccountService;
import gpps.service.ISubmitService;
import gpps.service.message.IMessageService;
import gpps.service.thirdpay.IHttpClientService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public abstract class TestAssistance {
	public static final String ACTION_REGISTACCOUNT="0";
	public static final String ACTION_RECHARGE="1";
	public static final String ACTION_TRANSFER="2";
	public static final String ACTION_CHECK="3";
	public static final String ACTION_CARDBINDING="4";
	public static final String ACTION_CASH="5";
	public static final String ACTION_AUTHORIZE="6";
	public static final String ACTION_ORDERQUERY="7";
	public static final String ACTION_BALANCEQUERY="8";
	protected static Map<String, String> urls=new HashMap<String, String>();
	static {
		urls.put(ACTION_REGISTACCOUNT, "/loan/toloanregisterbind.action");
		urls.put(ACTION_RECHARGE, "/loan/toloanrecharge.action");
		urls.put(ACTION_TRANSFER, "/loan/loan.action");
		urls.put(ACTION_CHECK, "/loan/toloantransferaudit.action");
		urls.put(ACTION_CARDBINDING, "/loan/toloanfastpay.action");
		urls.put(ACTION_CASH, "/loan/toloanwithdraws.action");
		urls.put(ACTION_AUTHORIZE, "/loan/toloanauthorize.action");
		urls.put(ACTION_ORDERQUERY, "/loan/loanorderquery.action");
		urls.put(ACTION_BALANCEQUERY, "/loan/balancequery.action");
	}
//	protected static String url="http://218.4.234.150:88/main";
//	protected static String platformMoneymoremore="p401";
//	protected static String privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIQffWLA9OLXsb6tnVGpvxFRNLy/umRaUfnKS4DI6To44fDHLOXs/HMSfK4tExe4uBIrRM5LaQxUwyjnP2xeZp3+mQ3GYsWWAkXm/L6FuIUk6Ndjzb4UTBoLskznRinIp0MJyndia6Mgubyn8Kse7YbxxfsQTWo5f5CfPqHlSqU/AgMBAAECgYBAJjnu1NkRusBmYE1d9Rj8A32jl0Ocre3XZk06flIfHrc0/L/j8yivhm5a8y+t+NYGnFOQBjU+83i+R2kX6M3RfcLHu0tXVfxmDSERzJJP0OmNfogXXJnLyVUGpVifqFfcvgVWMpG5Hy4KZZD+i73H+cYTVTOsuqRvUI88EInqoQJBALyMBcsDGCeCR3B4tteQWB9fp+e6a2pLfLR+v6mNuFDVaybn6pMtp4haucr8KzFYh2rCH6AKu62wO+z6vPTGSHsCQQCzY+hthvtEUBN/Pm5bUnVnKX3w+fLzwIHEvTCrXfls0VHWUVC6dC2Y5Iy60N/aj3cdmxEfTQ+NooTlQGnpzrUNAkAmdBBCZTEp7aIQSC5SLHgsfd/KnPSHSzn1vdvtAqBSrBQcbTQkLC1827QEuAU/HSURGuJES6wXMlgmbsTWzxG9AkBpPncRMvzdIiGeKFF0UFdCk8wogWuw58L6WohgMXzxA4kAtKopCZnqtkN+IqcCQeL/Qod0FrDGRo+zM+wvWK9NAkEAkINUcB2aNnPsW3VuTTtNdehFjmPGs1CWMRWvV7Bsp8G0cZcyodv1bkd+PyneWv5EpG+n0UD/tv2JDk8u7qRX4w==";
//	protected static String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEH31iwPTi17G+rZ1Rqb8RUTS8v7pkWlH5ykuAyOk6OOHwxyzl7PxzEnyuLRMXuLgSK0TOS2kMVMMo5z9sXmad/pkNxmLFlgJF5vy+hbiFJOjXY82+FEwaC7JM50YpyKdDCcp3YmujILm8p/CrHu2G8cX7EE1qOX+Qnz6h5UqlPwIDAQAB";
//	protected static String serverHost="218.4.234.150";
//	protected static String serverPort="88";
	
	protected static String url="http://moneymoremore.com";
	protected static String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL+4GgqVtvbEYewHKCBzXFEvQJ+YT8z7wtTupV2wb4EuvLl/k9HcEqqWznb4emmJXDCBXmw3XgBMNSfvMjYq4/0Mjfx3aEXCvrhFvKzdKXLd6nvzutO8OrcHMVY4WuMoD0TTk86i4LUtdAUm8S2PUagX4QuVJniG7bLYs8pW3J1XAgMBAAECgYEAuQUj5JkttKI7Wjxh5kdOjW7Lr/me8dT55uLUpkESqxn+ugo8DuiS/xyA7mHISTdtx8Xh6Q/Z4PGqzvpRe+MSl8RmzkoSmk8I4amQZRSdsOiZXdLbcp7+WWAlwldThOb/PVpPodGo+sYPKiJsdFhGXKBs3Rfckfkqivnj5UtVd1ECQQDmkOSEyZG5WtG9xomD6bX4KkwJq1bm3eEBelE+FTxkoqrE9ldU857bK28lIkpFXc5FXIX7fSBC15ZcMNPVduV/AkEA1N4v6P/JurdAGWFUI31HpQ50T2RCVNSblKsW0K3kmdJ6ohcd50NWE0twUkGeYAmdhpgrxb/kBfpBRPh6D3QkKQJBAMQu/YLPUKN614S27kPEFQX4V4LSu7rtiIsNXRuKTj0C/HNjeKPcN4sdxhC+wJuNWfpC4+49Zbn6XwJKubSk2lkCQB3npQVvwfBoRCoMoVxnAkSu9iodJLB0OfM14fupIl910WKqKrpNnG8TfxEfRNerIiVBhYy/e5JXaQn+em9nWpkCQEVEILXBWjr3KsNFhlmYr5+tDSaNjIddngnXdmX+7jPWN6jlcCQlWiKMmSAoIwJK1/gsP7Cm7pyToROf+MxQhug=";
	protected static String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/uBoKlbb2xGHsByggc1xRL0CfmE/M+8LU7qVdsG+BLry5f5PR3BKqls52+HppiVwwgV5sN14ATDUn7zI2KuP9DI38d2hFwr64Rbys3Sly3ep787rTvDq3BzFWOFrjKA9E05POouC1LXQFJvEtj1GoF+ELlSZ4hu2y2LPKVtydVwIDAQAB";
	protected static String platformMoneymoremore="p241";
	
	
	protected static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	
	protected static ICashStreamDao cashStreamDao = context.getBean(ICashStreamDao.class);
	protected static IHttpClientService httpClientService = context.getBean(IHttpClientService.class);
	protected static IAccountService accountService = context.getBean(IAccountService.class);
	protected static ISubmitService submitService = context.getBean(ISubmitService.class);
	
	protected static IMessageService messageService = context.getBean(IMessageService.class);
	
	protected static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	protected static IBorrowerDao borrowerDao = context.getBean(IBorrowerDao.class);
	protected static IInnerThirdPaySupportService innerThirdPayService = context.getBean(IInnerThirdPaySupportService.class);
}
