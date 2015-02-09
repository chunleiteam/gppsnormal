package gpps.service.thirdpay;

public class ThirdPartyAssistent {
//	public static final String ACTION_REGISTACCOUNT="0";
//	public static final String ACTION_RECHARGE="1";
//	public static final String ACTION_TRANSFER="2";
//	public static final String ACTION_CHECK="3";
//	public static final String ACTION_CARDBINDING="4";
//	public static final String ACTION_CASH="5";
//	public static final String ACTION_AUTHORIZE="6";
//	public static final String ACTION_ORDERQUERY="7";
//	public static final String ACTION_BALANCEQUERY="8";
//	protected static Map<String, String> urls=new HashMap<String, String>();
//	static {
//		urls.put(ACTION_REGISTACCOUNT, "/loan/toloanregisterbind.action");
//		urls.put(ACTION_RECHARGE, "/loan/toloanrecharge.action");
//		urls.put(ACTION_TRANSFER, "/loan/loan.action");
//		urls.put(ACTION_CHECK, "/loan/toloantransferaudit.action");
//		urls.put(ACTION_CARDBINDING, "/loan/toloanfastpay.action");
//		urls.put(ACTION_CASH, "/loan/toloanwithdraws.action");
//		urls.put(ACTION_AUTHORIZE, "/loan/toloanauthorize.action");
//		urls.put(ACTION_ORDERQUERY, "/loan/loanorderquery.action");
//		urls.put(ACTION_BALANCEQUERY, "/loan/balancequery.action");
//	}
//	protected static String url="http://218.4.234.150:88/main";
//	protected static String platformMoneymoremore="p401";
//	protected static String privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIQffWLA9OLXsb6tnVGpvxFRNLy/umRaUfnKS4DI6To44fDHLOXs/HMSfK4tExe4uBIrRM5LaQxUwyjnP2xeZp3+mQ3GYsWWAkXm/L6FuIUk6Ndjzb4UTBoLskznRinIp0MJyndia6Mgubyn8Kse7YbxxfsQTWo5f5CfPqHlSqU/AgMBAAECgYBAJjnu1NkRusBmYE1d9Rj8A32jl0Ocre3XZk06flIfHrc0/L/j8yivhm5a8y+t+NYGnFOQBjU+83i+R2kX6M3RfcLHu0tXVfxmDSERzJJP0OmNfogXXJnLyVUGpVifqFfcvgVWMpG5Hy4KZZD+i73H+cYTVTOsuqRvUI88EInqoQJBALyMBcsDGCeCR3B4tteQWB9fp+e6a2pLfLR+v6mNuFDVaybn6pMtp4haucr8KzFYh2rCH6AKu62wO+z6vPTGSHsCQQCzY+hthvtEUBN/Pm5bUnVnKX3w+fLzwIHEvTCrXfls0VHWUVC6dC2Y5Iy60N/aj3cdmxEfTQ+NooTlQGnpzrUNAkAmdBBCZTEp7aIQSC5SLHgsfd/KnPSHSzn1vdvtAqBSrBQcbTQkLC1827QEuAU/HSURGuJES6wXMlgmbsTWzxG9AkBpPncRMvzdIiGeKFF0UFdCk8wogWuw58L6WohgMXzxA4kAtKopCZnqtkN+IqcCQeL/Qod0FrDGRo+zM+wvWK9NAkEAkINUcB2aNnPsW3VuTTtNdehFjmPGs1CWMRWvV7Bsp8G0cZcyodv1bkd+PyneWv5EpG+n0UD/tv2JDk8u7qRX4w==";
//	protected static String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEH31iwPTi17G+rZ1Rqb8RUTS8v7pkWlH5ykuAyOk6OOHwxyzl7PxzEnyuLRMXuLgSK0TOS2kMVMMo5z9sXmad/pkNxmLFlgJF5vy+hbiFJOjXY82+FEwaC7JM50YpyKdDCcp3YmujILm8p/CrHu2G8cX7EE1qOX+Qnz6h5UqlPwIDAQAB";
//	protected static String serverHost="218.4.234.150";
//	protected static String serverPort="88";
//	
////	protected static String url="http://moneymoremore.com";
////	protected static String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL+4GgqVtvbEYewHKCBzXFEvQJ+YT8z7wtTupV2wb4EuvLl/k9HcEqqWznb4emmJXDCBXmw3XgBMNSfvMjYq4/0Mjfx3aEXCvrhFvKzdKXLd6nvzutO8OrcHMVY4WuMoD0TTk86i4LUtdAUm8S2PUagX4QuVJniG7bLYs8pW3J1XAgMBAAECgYEAuQUj5JkttKI7Wjxh5kdOjW7Lr/me8dT55uLUpkESqxn+ugo8DuiS/xyA7mHISTdtx8Xh6Q/Z4PGqzvpRe+MSl8RmzkoSmk8I4amQZRSdsOiZXdLbcp7+WWAlwldThOb/PVpPodGo+sYPKiJsdFhGXKBs3Rfckfkqivnj5UtVd1ECQQDmkOSEyZG5WtG9xomD6bX4KkwJq1bm3eEBelE+FTxkoqrE9ldU857bK28lIkpFXc5FXIX7fSBC15ZcMNPVduV/AkEA1N4v6P/JurdAGWFUI31HpQ50T2RCVNSblKsW0K3kmdJ6ohcd50NWE0twUkGeYAmdhpgrxb/kBfpBRPh6D3QkKQJBAMQu/YLPUKN614S27kPEFQX4V4LSu7rtiIsNXRuKTj0C/HNjeKPcN4sdxhC+wJuNWfpC4+49Zbn6XwJKubSk2lkCQB3npQVvwfBoRCoMoVxnAkSu9iodJLB0OfM14fupIl910WKqKrpNnG8TfxEfRNerIiVBhYy/e5JXaQn+em9nWpkCQEVEILXBWjr3KsNFhlmYr5+tDSaNjIddngnXdmX+7jPWN6jlcCQlWiKMmSAoIwJK1/gsP7Cm7pyToROf+MxQhug=";
////	protected static String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/uBoKlbb2xGHsByggc1xRL0CfmE/M+8LU7qVdsG+BLry5f5PR3BKqls52+HppiVwwgV5sN14ATDUn7zI2KuP9DI38d2hFwr64Rbys3Sly3ep787rTvDq3BzFWOFrjKA9E05POouC1LXQFJvEtj1GoF+ELlSZ4hu2y2LPKVtydVwIDAQAB";
////	protected static String platformMoneymoremore="p241";
//	
//	
//	protected static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
//	
//	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
//	
//	protected static ICashStreamDao cashStreamDao = context.getBean(ICashStreamDao.class);
//	protected static IHttpClientService httpClientService = context.getBean(IHttpClientService.class);
//	protected static IAccountService accountService = context.getBean(IAccountService.class);
//	protected static ISubmitService submitService = context.getBean(ISubmitService.class);
//	
//	protected static IMessageService messageService = context.getBean(IMessageService.class);
//	
//	protected static ILenderDao lenderDao = context.getBean(ILenderDao.class);
//	protected static IBorrowerDao borrowerDao = context.getBean(IBorrowerDao.class);
//	
//	public static String getBaseUrl(String action) {
//		return url+urls.get(action);
//	}
//	
//	public static void checkReturnParam(Map<String,String> params,String[] signStrs) throws AlreadyDoneException, ResultCodeException, SignatureException
//	{
//		String resultCode=params.get("ResultCode");
//		if(StringUtil.isEmpty(resultCode)||(!resultCode.equals("88")&&!resultCode.equals("18")))
//		{
//			throw new ResultCodeException(resultCode, params.get("Message"));
//		}
//		
//		if(resultCode.equals("18")){
//			throw new AlreadyDoneException(params.get("Message"));
//		}
//		
//		StringBuilder sBuilder=new StringBuilder();
//		for(String str:signStrs)
//		{
//			sBuilder.append(StringUtil.strFormat(params.get(str)));
//		}
//		RsaHelper rsa = RsaHelper.getInstance();
//		String sign=rsa.signData(sBuilder.toString(), privateKey);
//		if(!sign.replaceAll("\r", "").equals(params.get("SignInfo").replaceAll("\r", "")))
//			throw new SignatureException("非法的签名");
//	}
//	
//	//审核时候进行的签名
//	public static String signForAudit(Map<String,String> params, String privateKey){
//			StringBuilder sBuilder=new StringBuilder();
//			sBuilder.append(StringUtil.strFormat(params.get("LoanNoList")));
//			sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
//			sBuilder.append(StringUtil.strFormat(params.get("AuditType")));
//			sBuilder.append(StringUtil.strFormat(params.get("RandomTimeStamp")));
//			sBuilder.append(StringUtil.strFormat(params.get("Remark1")));
//			sBuilder.append(StringUtil.strFormat(params.get("Remark2")));
//			sBuilder.append(StringUtil.strFormat(params.get("Remark3")));
//			sBuilder.append(StringUtil.strFormat(params.get("ReturnURL")));
//			sBuilder.append(StringUtil.strFormat(params.get("NotifyURL")));
//			RsaHelper rsa = RsaHelper.getInstance();
//			String signInfo=rsa.signData(sBuilder.toString(), privateKey);
//			return signInfo;
//	}
//	
//	// 解析并校验审核返回结果参数
//	public static List<String> handleAuditReturnParams(Map<String, String> returnParams)
//			throws AlreadyDoneException, ResultCodeException,
//			SignatureException {
//		List<String> result = new ArrayList<String>();
//		try {
//			checkAuditReturnParams(returnParams);
//		} catch (SignatureException e) {
//			throw e;
//		} catch (ResultCodeException e) {
//			throw e;
//		} catch (AlreadyDoneException e) {
//			throw e;
//		}
//
//		if (!StringUtil.isEmpty(returnParams.get("LoanNoList"))) {
//			String[] loanNoList = returnParams.get("LoanNoList").split(",");
//			for (String loanNo : loanNoList) {
//				result.add(loanNo);
//			}
//		}
//
//		return result;
//	}
//
//	// 校验审核转账时返回的参数
//	public static void checkAuditReturnParams(Map<String, String> params)
//			throws AlreadyDoneException, ResultCodeException,
//			SignatureException {
//		String[] signStrs = { "LoanNoList", "LoanNoListFail",
//				"PlatformMoneymoremore", "AuditType", "RandomTimeStamp",
//				"Remark1", "Remark2", "Remark3", "ResultCode" };
//		ThirdPartyAssistent.checkReturnParam(params, signStrs);
//	}
//	
//	
//	//校验转账申请时候返回的参数
//	public static void checkTransferReturnParams(Map<String,String> params) throws AlreadyDoneException, ResultCodeException, SignatureException{
//		String[] signStrs={"LoanJsonList","PlatformMoneymoremore","Action","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
//		String loanJsonList = null;
//		try {
//			loanJsonList=URLDecoder.decode(params.get("LoanJsonList"),"UTF-8");
//			params.put("LoanJsonList", loanJsonList);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		checkReturnParam(params,signStrs);
//	}
}
