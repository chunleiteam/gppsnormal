package gpps.validate;

import gpps.dao.IBorrowerDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.ILenderDao;
import gpps.model.Borrower;
import gpps.model.CashStream;
import gpps.model.Lender;
import gpps.service.IAccountService;
import gpps.service.ISubmitService;
import gpps.service.thirdpay.IHttpClientService;
import gpps.tools.DateCalculateUtils;
import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.google.gson.Gson;

public class CheckSingleCashStream {
	
	public static final String ACTION_REGISTACCOUNT="0";
	public static final String ACTION_RECHARGE="1";
	public static final String ACTION_TRANSFER="2";
	public static final String ACTION_CHECK="3";
	public static final String ACTION_CARDBINDING="4";
	public static final String ACTION_CASH="5";
	public static final String ACTION_AUTHORIZE="6";
	public static final String ACTION_ORDERQUERY="7";
	public static final String ACTION_BALANCEQUERY="8";
	private static Map<String, String> urls=new HashMap<String, String>();
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
	private static String url="http://218.4.234.150:88/main";
	private static String platformMoneymoremore="p401";
	private static String privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIQffWLA9OLXsb6tnVGpvxFRNLy/umRaUfnKS4DI6To44fDHLOXs/HMSfK4tExe4uBIrRM5LaQxUwyjnP2xeZp3+mQ3GYsWWAkXm/L6FuIUk6Ndjzb4UTBoLskznRinIp0MJyndia6Mgubyn8Kse7YbxxfsQTWo5f5CfPqHlSqU/AgMBAAECgYBAJjnu1NkRusBmYE1d9Rj8A32jl0Ocre3XZk06flIfHrc0/L/j8yivhm5a8y+t+NYGnFOQBjU+83i+R2kX6M3RfcLHu0tXVfxmDSERzJJP0OmNfogXXJnLyVUGpVifqFfcvgVWMpG5Hy4KZZD+i73H+cYTVTOsuqRvUI88EInqoQJBALyMBcsDGCeCR3B4tteQWB9fp+e6a2pLfLR+v6mNuFDVaybn6pMtp4haucr8KzFYh2rCH6AKu62wO+z6vPTGSHsCQQCzY+hthvtEUBN/Pm5bUnVnKX3w+fLzwIHEvTCrXfls0VHWUVC6dC2Y5Iy60N/aj3cdmxEfTQ+NooTlQGnpzrUNAkAmdBBCZTEp7aIQSC5SLHgsfd/KnPSHSzn1vdvtAqBSrBQcbTQkLC1827QEuAU/HSURGuJES6wXMlgmbsTWzxG9AkBpPncRMvzdIiGeKFF0UFdCk8wogWuw58L6WohgMXzxA4kAtKopCZnqtkN+IqcCQeL/Qod0FrDGRo+zM+wvWK9NAkEAkINUcB2aNnPsW3VuTTtNdehFjmPGs1CWMRWvV7Bsp8G0cZcyodv1bkd+PyneWv5EpG+n0UD/tv2JDk8u7qRX4w==";
	private static String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEH31iwPTi17G+rZ1Rqb8RUTS8v7pkWlH5ykuAyOk6OOHwxyzl7PxzEnyuLRMXuLgSK0TOS2kMVMMo5z9sXmad/pkNxmLFlgJF5vy+hbiFJOjXY82+FEwaC7JM50YpyKdDCcp3YmujILm8p/CrHu2G8cX7EE1qOX+Qnz6h5UqlPwIDAQAB";
	private static String serverHost="218.4.234.150";
	private static String serverPort="88";
	
	
//	private static String url="http://moneymoremore.com";
//	private static String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAL+4GgqVtvbEYewHKCBzXFEvQJ+YT8z7wtTupV2wb4EuvLl/k9HcEqqWznb4emmJXDCBXmw3XgBMNSfvMjYq4/0Mjfx3aEXCvrhFvKzdKXLd6nvzutO8OrcHMVY4WuMoD0TTk86i4LUtdAUm8S2PUagX4QuVJniG7bLYs8pW3J1XAgMBAAECgYEAuQUj5JkttKI7Wjxh5kdOjW7Lr/me8dT55uLUpkESqxn+ugo8DuiS/xyA7mHISTdtx8Xh6Q/Z4PGqzvpRe+MSl8RmzkoSmk8I4amQZRSdsOiZXdLbcp7+WWAlwldThOb/PVpPodGo+sYPKiJsdFhGXKBs3Rfckfkqivnj5UtVd1ECQQDmkOSEyZG5WtG9xomD6bX4KkwJq1bm3eEBelE+FTxkoqrE9ldU857bK28lIkpFXc5FXIX7fSBC15ZcMNPVduV/AkEA1N4v6P/JurdAGWFUI31HpQ50T2RCVNSblKsW0K3kmdJ6ohcd50NWE0twUkGeYAmdhpgrxb/kBfpBRPh6D3QkKQJBAMQu/YLPUKN614S27kPEFQX4V4LSu7rtiIsNXRuKTj0C/HNjeKPcN4sdxhC+wJuNWfpC4+49Zbn6XwJKubSk2lkCQB3npQVvwfBoRCoMoVxnAkSu9iodJLB0OfM14fupIl910WKqKrpNnG8TfxEfRNerIiVBhYy/e5JXaQn+em9nWpkCQEVEILXBWjr3KsNFhlmYr5+tDSaNjIddngnXdmX+7jPWN6jlcCQlWiKMmSAoIwJK1/gsP7Cm7pyToROf+MxQhug=";
//	private static String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/uBoKlbb2xGHsByggc1xRL0CfmE/M+8LU7qVdsG+BLry5f5PR3BKqls52+HppiVwwgV5sN14ATDUn7zI2KuP9DI38d2hFwr64Rbys3Sly3ep787rTvDq3BzFWOFrjKA9E05POouC1LXQFJvEtj1GoF+ELlSZ4hu2y2LPKVtydVwIDAQAB";
//	private static String platformMoneymoremore="p241";
	
	
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	
	protected static ICashStreamDao cashStreamDao = context.getBean(ICashStreamDao.class);
	protected static IHttpClientService httpClientService = context.getBean(IHttpClientService.class);
	protected static IAccountService accountService = context.getBean(IAccountService.class);
	protected static ISubmitService submitService = context.getBean(ISubmitService.class);
	
	protected static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	protected static IBorrowerDao borrowerDao = context.getBean(IBorrowerDao.class);
	
	public static void main(String args[]) throws Exception{
//		withdrawSingleCashStream("LN19029242014122113491557888");
		
		Date date = new Date(2015-1900,1-1,27,8,0,0);
		
		long start = DateCalculateUtils.getStartTime(date.getTime());
		long end = DateCalculateUtils.getEndTime(date.getTime())+1000;
		
		
//		List<CashStream> css = cashStreamDao.findByActionAndTime(-1, start, end);
		List<CashStream> css = cashStreamDao.findByActionAndTime(-1, -1, -1);
		
//		List<CashStream> css = new ArrayList<CashStream>();
//		css.add(cashStreamDao.find(56));
//		css.add(cashStreamDao.find(57));
		
		int total = 0;
		int success = 0;
		int wrong = 0;
		
		StringBuilder errorlog = new StringBuilder();
		
		for(CashStream cs : css){
			total++;
			try{
			checkSingleCashStream(cs.getId());
			success++;
			}catch(Exception e){
				System.err.println(e.getMessage());
				errorlog.append(e.getMessage()).append("\n");
				wrong++;
			}
		}
		
		
		
		System.out.println(errorlog.toString());
		System.out.println("现金流审核执行完毕：总共"+total+"条，成功"+success+"条，失败"+wrong+"条！");
		
		System.exit(0);
	}
	
	public static String getBaseUrl(String action) {
		return url+urls.get(action);
	}
	
	public static boolean checkSingleCashStream(Integer cashStreamId) throws Exception{
		CashStream cashStream=cashStreamDao.find(cashStreamId);
		if(cashStream==null)
			return true;
		
		
		if(cashStream.getChiefamount().add(cashStream.getInterest()).compareTo(new BigDecimal(0))==0){
			return true;
		}
		
		String baseUrl=getBaseUrl(ACTION_ORDERQUERY);
		Map<String,String> params=new HashMap<String,String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
		if(cashStream.getAction()==CashStream.ACTION_CASH || (cashStream.getAction()==CashStream.ACTION_RECHARGE && "提现退回".equals(cashStream.getDescription())))
			params.put("Action", "2");
		else if(cashStream.getAction()==CashStream.ACTION_RECHARGE)
			params.put("Action", "1");
		
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(params.get("Action")));
		
		
		String body="";
//		if(cashStream.getAction()==CashStream.ACTION_PAY || cashStream.getAction()==CashStream.ACTION_UNFREEZE)
//		{
			if(cashStream.getLoanNo()!=null){
			params.put("LoanNo", cashStream.getLoanNo());
			sBuilder.append(StringUtil.strFormat(params.get("LoanNo")));
			RsaHelper rsa = RsaHelper.getInstance();
			params.put("SignInfo", rsa.signData(sBuilder.toString(), privateKey));
			body=httpClientService.post(baseUrl, params);
			}
			
			else{
				params.put("OrderNo", String.valueOf(cashStream.getId()));
				sBuilder.append(StringUtil.strFormat(params.get("OrderNo")));
				RsaHelper rsa = RsaHelper.getInstance();
				params.put("SignInfo", rsa.signData(sBuilder.toString(), privateKey));
				body=httpClientService.post(baseUrl, params);
			}
			
			
			
//		}else{
//			params.put("OrderNo", String.valueOf(cashStream.getId()));
//			sBuilder.append(StringUtil.strFormat(params.get("OrderNo")));
//			RsaHelper rsa = RsaHelper.getInstance();
//			params.put("SignInfo", rsa.signData(sBuilder.toString(), privateKey));
//			body=httpClientService.post(baseUrl, params);
//		}
		
		
		
		
		try{
		boolean flag = false;
		
		if(cashStream.getAction()==CashStream.ACTION_RECHARGE)
		{
			flag = checkRechargeResult(cashStream, body);
		}else if(cashStream.getAction()==CashStream.ACTION_CASH){
			flag = checkWithDrawResult(cashStream, body);
		}else if(cashStream.getAction()==CashStream.ACTION_REPAY){
			flag = checkPayBackResult(cashStream, body);
		}else if(cashStream.getAction()==CashStream.ACTION_PAY){
			flag = checkPayResult(cashStream, body);
		}else if(cashStream.getAction()==CashStream.ACTION_FREEZE){
			flag = checkFreezeResult(cashStream, body);
		}
		else if(cashStream.getAction()==CashStream.ACTION_UNFREEZE){
			flag = checkUnFreezeResult(cashStream, body);
		}else if(cashStream.getAction()==CashStream.ACTION_STORECHANGE){
			flag = checkStoreResult(cashStream, body);
		}else{
			throw new Exception("现金流[ID:"+cashStream.getId()+"]有问题: 未知状态！");
		}
		 return flag;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	public static boolean checkUnFreezeResult(CashStream cashStream, String body) throws Exception{
		boolean flag = true;
		Gson gson = new Gson();
		List<Map<String, String>> res = (List<Map<String,String>>)gson.fromJson(body, List.class);
		
		if( (res==null || res.isEmpty()) && cashStream.getState() == cashStream.STATE_INIT){
			return true;
		}else if((res==null || res.isEmpty()) && cashStream.getLoanNo()==null && !"流标".equals(cashStream.getDescription())){
			return true;
		}else if((res==null || res.isEmpty())){
			throw new Exception("现金流[ID:"+cashStream.getId()+"]有问题: 流标解冻找不到第三方上对应的记录！");
		}
		
		String laccount = "";
		
			Lender lender = lenderDao.findByAccountID(cashStream.getLenderAccountId());
			if(lender!=null){
				laccount = lender.getThirdPartyAccount();
			}
		
		
		
		
		Map<String, String> result = res.get(0);
		
		//付款人乾多多标识
		String LoanOutMoneymoremore = result.get("LoanOutMoneymoremore");
		
		//收款人乾多多标识
		String LoanInMoneymoremore = result.get("LoanInMoneymoremore");
		
		//额度
		String Amount = result.get("Amount");
		//乾多多流水号
		String LoanNo = result.get("LoanNo");
		
		//转账类型
		String TransferAction = result.get("TransferAction");
		
		//转账状态
		String TransferState = result.get("TransferState");
		
		//操作状态
		String ActState = result.get("ActState");
		
		boolean stateflag = false;
		if(TransferState.equals("0") && ActState.equals("0") && cashStream.getState()==CashStream.STATE_INIT){
			stateflag=true;
		}else if(TransferState.equals("1") && ActState.equals("2") && cashStream.getState()==CashStream.STATE_SUCCESS){
			stateflag=true;
		}
		
		boolean accountflag = laccount.equals(LoanOutMoneymoremore);
		
		boolean loanflag = cashStream.getLoanNo()==null? false : cashStream.getLoanNo().equals(LoanNo);
		
		//投标
		boolean actionflag = TransferAction.equals("1");
		
		boolean totalflag = cashStream.getChiefamount().compareTo(new BigDecimal(Amount))==0;
		
		flag = flag && loanflag && totalflag && accountflag && actionflag && stateflag;
		
		StringBuilder message = new StringBuilder();
		
			message.append("现金流[ID:"+cashStream.getId()+"]: ");
			
		if(loanflag==false){
			message.append(" 乾多多流水号不一致:[平台"+cashStream.getLoanNo()+"][钱多多"+LoanNo+"] ");
		}
		
		if(totalflag==false){
			message.append(" 金额不一致:[平台"+cashStream.getChiefamount().toString()+"][钱多多"+Amount+"] ");
		}
		
		if(accountflag==false){
			message.append(" 账户不一致:[平台:"+laccount+"][钱多多 :"+LoanOutMoneymoremore+"] ");
		}
		
		if(actionflag==false){
			message.append(" 行为不一致:[平台 投标][钱多多 还款] ");
		}
		
		if(stateflag==false){
			message.append(" 操作状态不一致:[平台"+cashStream.getState()+"][钱多多"+ActState+"]" );
		}
		
		if(!flag){
			throw new Exception(message.toString());
		}
		
		return flag;
	}
	
	public static boolean checkFreezeResult(CashStream cashStream, String body) throws Exception{
		boolean flag = true;
		Gson gson = new Gson();
		List<Map<String, String>> res = (List<Map<String,String>>)gson.fromJson(body, List.class);
		
		if( (res==null || res.isEmpty()) && cashStream.getState() == cashStream.STATE_INIT){
			return true;
		}else if((res==null || res.isEmpty()) && cashStream.getLenderAccountId()==null && cashStream.getBorrowerAccountId()!=null){
			return true;
		}else if(cashStream.getState()==cashStream.STATE_INIT){
			List<CashStream> cash = cashStreamDao.findBySubmitAndActionAndState(cashStream.getSubmitId(), cashStream.getAction(), CashStream.STATE_SUCCESS);
			if(cash!=null && !cash.isEmpty()){
				return true;
			}else{ 
				Map<String, String> result = res.get(0);
				if("0".equals(result.get("TransferState")) && "0".equals(result.get("ActState"))){
				return true;
				}else{
				throw new Exception("现金流[ID:"+cashStream.getId()+"]有问题: 平台未处理的现金流在第三方上有对应的记录！");
				}
			}
		}
		else if((res==null || res.isEmpty())){
			throw new Exception("现金流[ID:"+cashStream.getId()+"]有问题: 投标冻结找不到第三方上对应的记录！");
		}
		
		String laccount = "";
		
			Lender lender = lenderDao.findByAccountID(cashStream.getLenderAccountId());
			
			if(lender!=null){
				laccount = lender.getThirdPartyAccount();
			}else{
				laccount = borrowerDao.findByAccountID(cashStream.getBorrowerAccountId()).getThirdPartyAccount();
			}
		
		
		
		
		Map<String, String> result = res.get(0);
		
		//付款人乾多多标识
		String LoanOutMoneymoremore = result.get("LoanOutMoneymoremore");
		
		//收款人乾多多标识
		String LoanInMoneymoremore = result.get("LoanInMoneymoremore");
		
		//额度
		String Amount = result.get("Amount");
		//乾多多流水号
		String LoanNo = result.get("LoanNo");
		
		//转账类型
		String TransferAction = result.get("TransferAction");
		
		
		boolean accountflag = laccount.equals(LoanOutMoneymoremore);
		
		boolean loanflag = cashStream.getLoanNo()==null? false : cashStream.getLoanNo().equals(LoanNo);
		
		//投标
		boolean actionflag = lender==null? TransferAction.equals("2") : TransferAction.equals("1");
		
		boolean totalflag = cashStream.getChiefamount().add(cashStream.getInterest()).negate().compareTo(new BigDecimal(Amount))==0;
		
		flag = flag && loanflag && totalflag && accountflag && actionflag;
		
		StringBuilder message = new StringBuilder();
		
			message.append("现金流[ID:"+cashStream.getId()+"]: ");
			
		if(loanflag==false){
			message.append(" 乾多多流水号不一致:[平台"+cashStream.getLoanNo()+"][钱多多"+LoanNo+"] ");
		}
		
		if(totalflag==false){
			message.append(" 金额不一致:[平台"+cashStream.getChiefamount().negate().toString()+"][钱多多"+Amount+"] ");
		}
		
		if(accountflag==false){
			message.append(" 账户不一致:[平台:"+laccount+"][钱多多 :"+LoanOutMoneymoremore+"] ");
		}
		
		if(actionflag==false){
			message.append(" 行为不一致:[平台 投标][钱多多 还款] ");
		}
		
		if(!flag){
			throw new Exception(message.toString());
		}
		
		return flag;
	}
	
	public static boolean checkPayResult(CashStream cashStream, String body) throws Exception{
		boolean flag = true;
		Gson gson = new Gson();
		List<Map<String, String>> res = (List<Map<String,String>>)gson.fromJson(body, List.class);
		
		if( (res==null || res.isEmpty()) && cashStream.getState() == cashStream.STATE_INIT){
			return true;
		}else if((res==null || res.isEmpty())){
			throw new Exception("现金流[ID:"+cashStream.getId()+"]有问题: 找不到第三方上对应的记录！");
		}
		
		String baccount = "";
		String laccount = "";
		
			Lender lender = lenderDao.findByAccountID(cashStream.getLenderAccountId());
			if(lender!=null){
				laccount = lender.getThirdPartyAccount();
			}
			Borrower borrower = borrowerDao.findByAccountID(cashStream.getBorrowerAccountId());
			if(borrower!=null){
				baccount = borrower.getThirdPartyAccount();
			}
		
		
		
		
		Map<String, String> result = res.get(0);
		
		//付款人乾多多标识
		String LoanOutMoneymoremore = result.get("LoanOutMoneymoremore");
		
		//收款人乾多多标识
		String LoanInMoneymoremore = result.get("LoanInMoneymoremore");
		
		//额度
		String Amount = result.get("Amount");
		//乾多多流水号
		String LoanNo = result.get("LoanNo");
		
		//转账类型
		String TransferAction = result.get("TransferAction");
		
		//转账状态
		String TransferState = result.get("TransferState");
		
		//操作状态
		String ActState = result.get("ActState");
		
		
		boolean accountflag = laccount.equals(LoanOutMoneymoremore) && baccount.equals(LoanInMoneymoremore);
		
		boolean loanflag = cashStream.getLoanNo()==null? false : cashStream.getLoanNo().equals(LoanNo);
		
		//投标
		boolean actionflag = TransferAction.equals("1");
		
		boolean stateflag = false;
		if(ActState.equals("0")&&TransferState.equals("0")&&cashStream.getState()==1)
		{
			stateflag=true;
		}else if(ActState.equals("1")&&TransferState.equals("1")&&cashStream.getState()==2){
			stateflag=true;
		}else if(ActState.equals("2")&&TransferState.equals("1")&&cashStream.getState()==4){
			stateflag=true;
		}else if(ActState.equals("3")&&TransferState.equals("1")&&cashStream.getState()==2){
			stateflag=true;
		}
		boolean totalflag = cashStream.getChiefamount().negate().compareTo(new BigDecimal(Amount))==0;
		
		flag = flag && loanflag && stateflag && totalflag && accountflag && actionflag;
		
		StringBuilder message = new StringBuilder();
		
			message.append("现金流[ID:"+cashStream.getId()+"]: ");
		if(loanflag==false){
			message.append(" 乾多多流水号不一致:[平台"+cashStream.getLoanNo()+"][钱多多"+LoanNo+"] ");
		}
		if(stateflag==false){
			message.append(" 操作状态不一致:[平台"+cashStream.getState()+"][钱多多"+ActState+"] ");
		}
		if(totalflag==false){
			message.append(" 金额不一致:[平台"+cashStream.getChiefamount().negate().toString()+"][钱多多"+Amount+"] ");
		}
		
		if(accountflag==false){
			message.append(" 账户不一致:[平台 borrower:"+baccount+" lender:"+laccount+"][钱多多 borrower:"+LoanInMoneymoremore+" lender:"+LoanOutMoneymoremore+"] ");
		}
		
		if(actionflag==false){
			message.append(" 行为不一致:[平台 投标][钱多多 还款] ");
		}
		
		if(!flag){
			throw new Exception(message.toString());
		}
		
		return flag;
	}
	
	public static boolean checkStoreResult(CashStream cashStream, String body) throws Exception{

		boolean flag = true;
		Gson gson = new Gson();
		List<Map<String, String>> res = (List<Map<String,String>>)gson.fromJson(body, List.class);
		
		if( (res==null || res.isEmpty()) && cashStream.getState() == cashStream.STATE_INIT){
			return true;
		}else if((res==null || res.isEmpty())){
			throw new Exception("现金流[ID:"+cashStream.getId()+"]有问题: 找不到第三方上对应的记录！");
		}
		
		
		String out = "";
		
			Borrower borrower = borrowerDao.findByAccountID(cashStream.getBorrowerAccountId());
			if(borrower!=null){
				out = borrower.getThirdPartyAccount();
			}
		
		
		
		
		Map<String, String> result = res.get(0);
		
		//付款人乾多多标识
		String LoanOutMoneymoremore = result.get("LoanOutMoneymoremore");
		
		//收款人乾多多标识
		String LoanInMoneymoremore = result.get("LoanInMoneymoremore");
		
		//额度
		String Amount = result.get("Amount");
		//乾多多流水号
		String LoanNo = result.get("LoanNo");
		
		//转账类型
		String TransferAction = result.get("TransferAction");
		
		//转账状态
		String TransferState = result.get("TransferState");
		
		//操作状态
		String ActState = result.get("ActState");
		
		
		
		//还款
		boolean actionflag = TransferAction.equals("2");
		
		
		boolean accountflag = out.equals(LoanOutMoneymoremore);
		
		boolean loanflag = cashStream.getLoanNo()==null? false : cashStream.getLoanNo().equals(LoanNo);
		
		boolean stateflag = false;
		if(ActState.equals("0")&&TransferState.equals("0")&&cashStream.getState()==1)
		{
			stateflag=true;
		}else if(ActState.equals("1")&&TransferState.equals("1")&&cashStream.getState()==2){
			stateflag=true;
		}else if(ActState.equals("2")&&TransferState.equals("1")&&cashStream.getState()==4){
			stateflag=true;
		}else if(ActState.equals("3")&&TransferState.equals("1")&&cashStream.getState()==2){
			stateflag=true;
		}
		boolean totalflag = cashStream.getChiefamount().add(cashStream.getInterest()).compareTo(new BigDecimal(Amount))==0;
		
		flag = flag && loanflag && stateflag && totalflag && accountflag && actionflag;
		
		StringBuilder message = new StringBuilder();
		
			message.append("现金流[ID:"+cashStream.getId()+"]: ");
		if(loanflag==false){
			message.append(" 乾多多流水号不一致:[平台"+cashStream.getLoanNo()+"][钱多多"+LoanNo+"] ");
		}
		if(stateflag==false){
			message.append(" 操作状态不一致:[平台"+cashStream.getState()+"][钱多多"+ActState+"] ");
		}
		if(totalflag==false){
			message.append(" 金额不一致:[平台"+cashStream.getChiefamount().toString()+"][钱多多"+Amount+"] ");
		}
		
		if(accountflag==false){
			message.append(" 账户不一致:[平台:"+out+"][钱多多:"+LoanOutMoneymoremore+"] ");
		}
		
		if(actionflag==false){
			message.append(" 行为不一致:[平台 还款][钱多多 投标] ");
		}
		
		if(!flag){
			throw new Exception(message.toString());
		}
		
		return flag;
	}
	
	public static boolean checkPayBackResult(CashStream cashStream, String body) throws Exception{

		boolean flag = true;
		Gson gson = new Gson();
		List<Map<String, String>> res = (List<Map<String,String>>)gson.fromJson(body, List.class);
		
		if( (res==null || res.isEmpty()) && cashStream.getState() == cashStream.STATE_INIT){
			return true;
		}else if((res==null || res.isEmpty())){
			throw new Exception("现金流[ID:"+cashStream.getId()+"]有问题: 找不到第三方上对应的记录！");
		}
		
		
		String out = "";
		String in = "";
		
			Lender lender = lenderDao.findByAccountID(cashStream.getLenderAccountId());
			if(lender!=null){
				in = lender.getThirdPartyAccount();
			}
			Borrower borrower = borrowerDao.findByAccountID(cashStream.getBorrowerAccountId());
			if(borrower!=null){
				out = borrower.getThirdPartyAccount();
			}
		
		
		
		
		Map<String, String> result = res.get(0);
		
		//付款人乾多多标识
		String LoanOutMoneymoremore = result.get("LoanOutMoneymoremore");
		
		//收款人乾多多标识
		String LoanInMoneymoremore = result.get("LoanInMoneymoremore");
		
		//额度
		String Amount = result.get("Amount");
		//乾多多流水号
		String LoanNo = result.get("LoanNo");
		
		//转账类型
		String TransferAction = result.get("TransferAction");
		
		//转账状态
		String TransferState = result.get("TransferState");
		
		//操作状态
		String ActState = result.get("ActState");
		
		
		
		//还款
		boolean actionflag = TransferAction.equals("2");
		
		
		boolean accountflag = out.equals(LoanOutMoneymoremore) && in.equals(LoanInMoneymoremore);
		
		boolean loanflag = cashStream.getLoanNo()==null? false : cashStream.getLoanNo().equals(LoanNo);
		
		boolean stateflag = false;
		if(ActState.equals("0")&&TransferState.equals("0")&&cashStream.getState()==1)
		{
			stateflag=true;
		}else if(ActState.equals("1")&&TransferState.equals("1")&&cashStream.getState()==2){
			stateflag=true;
		}else if(ActState.equals("2")&&TransferState.equals("1")&&cashStream.getState()==4){
			stateflag=true;
		}else if(ActState.equals("3")&&TransferState.equals("1")&&cashStream.getState()==2){
			stateflag=true;
		}
		boolean totalflag = cashStream.getChiefamount().add(cashStream.getInterest()).compareTo(new BigDecimal(Amount))==0;
		
		flag = flag && loanflag && stateflag && totalflag && accountflag && actionflag;
		
		StringBuilder message = new StringBuilder();
		
			message.append("现金流[ID:"+cashStream.getId()+"]: ");
		if(loanflag==false){
			message.append(" 乾多多流水号不一致:[平台"+cashStream.getLoanNo()+"][钱多多"+LoanNo+"] ");
		}
		if(stateflag==false){
			message.append(" 操作状态不一致:[平台"+cashStream.getState()+"][钱多多"+ActState+"] ");
		}
		if(totalflag==false){
			message.append(" 金额不一致:[平台"+cashStream.getChiefamount().toString()+"][钱多多"+Amount+"] ");
		}
		
		if(accountflag==false){
			message.append(" 账户不一致:[平台 borrower:"+out+" lender:+"+in+"][钱多多 borrower:"+LoanOutMoneymoremore+" lender:"+LoanInMoneymoremore+"] ");
		}
		
		if(actionflag==false){
			message.append(" 行为不一致:[平台 还款][钱多多 投标] ");
		}
		
		if(!flag){
			throw new Exception(message.toString());
		}
		
		return flag;
	}
	
	private static boolean checkRechargeResult(CashStream cashStream, String body) throws Exception{
		
		boolean flag = true;
		
		Gson gson = new Gson();
		List<Map<String, String>> res = (List<Map<String,String>>)gson.fromJson(body, List.class);
		
		if( (res==null || res.isEmpty()) && cashStream.getState() == cashStream.STATE_INIT){
			return true;
		}else if((res==null || res.isEmpty())){
			if("快捷支付充值".equals(cashStream.getDescription())){
				//快捷支付充值
				return true;
			}else{
			throw new Exception("现金流[ID:"+cashStream.getId()+"]有问题: 找不到第三方上对应的记录！");
			}
		}
		
		
		
		String thirdPartyAccount = "";
		
		if(cashStream.getLenderAccountId()!=null){
			Lender lender = lenderDao.findByAccountID(cashStream.getLenderAccountId());
			if(lender!=null){
				thirdPartyAccount = lender.getThirdPartyAccount();
			}
		}else if(cashStream.getBorrowerAccountId()!=null){
			Borrower borrower = borrowerDao.findByAccountID(cashStream.getBorrowerAccountId());
			if(borrower!=null){
				thirdPartyAccount = borrower.getThirdPartyAccount();
			}
		}
		
		
		
		
		Map<String, String> result = res.get(0);
		
		
		
		
		
		//处理提现失败退回金额的时候，用了一次描述为“提现退回”的充值操作，因此校验在充值动作里执行
		if("提现退回".equals(cashStream.getDescription())){
			//额度
			String Amount = result.get("Amount");
			String WithdrawMoneymoremore = result.get("WithdrawMoneymoremore");
			String WithdrawsState = result.get("WithdrawsState");
			String LoanNo = result.get("LoanNo");
			boolean accountflag = thirdPartyAccount.equals(WithdrawMoneymoremore);
			boolean loanflag = cashStream.getLoanNo()==null? (LoanNo==null? true : false) : cashStream.getLoanNo().equals(LoanNo);
			boolean stateflag = cashStream.getState()==CashStream.STATE_SUCCESS && WithdrawsState.equals("2");
			boolean totalflag = cashStream.getChiefamount().compareTo(new BigDecimal(Amount))==0;
			flag = flag && loanflag && stateflag && totalflag && accountflag;
			
			StringBuilder message = new StringBuilder();
			
				message.append("现金流[ID:"+cashStream.getId()+"]: ");
			if(loanflag==false){
				message.append(" 乾多多流水号不一致:[平台"+cashStream.getLoanNo()+"][钱多多"+LoanNo+"] ");
			}
			if(stateflag==false){
				message.append(" 操作状态不一致:[平台"+cashStream.getState()+"][钱多多"+WithdrawsState+"] ");
			}
			if(totalflag==false){
				message.append(" 金额不一致:[平台"+cashStream.getChiefamount().toString()+"][钱多多"+Amount+"] ");
			}
			if(accountflag==false){
				message.append(" 账户不一致:[平台"+thirdPartyAccount+"][钱多多"+WithdrawMoneymoremore+"] ");
			}
			
			if(!flag){
				throw new Exception(message.toString());
			}
			
			return flag;
		}
		
		
		
		
		
		
		//额度
		String Amount = result.get("Amount");
		//乾多多流水号
		String LoanNo = result.get("LoanNo");
				
		//充值账号
		String RechargeMoneymoremore = result.get("RechargeMoneymoremore");
		
		//充值费用
		String Fee = result.get("Fee");
				
		//充值状态
		String RechargeState = result.get("RechargeState");
		
		boolean accountflag = thirdPartyAccount.equals(RechargeMoneymoremore);
		
		boolean loanflag = cashStream.getLoanNo()==null? (LoanNo==null? true : false) : cashStream.getLoanNo().equals(LoanNo);
		boolean stateflag = false;
		if(RechargeState.equals("0")&&cashStream.getState()==1)
		{
			return true;
		}else if(RechargeState.equals("1")&&cashStream.getState()==2){
			stateflag=true;
		}else if(RechargeState.equals("2")&&cashStream.getState()==4){
			stateflag=true;
		}
		boolean totalflag = cashStream.getChiefamount().compareTo(new BigDecimal(Amount))==0;
		boolean feeflag = cashStream.getFee().compareTo(new BigDecimal(Fee))==0;
		
		flag = flag && loanflag && stateflag && totalflag && feeflag && accountflag;
		
		StringBuilder message = new StringBuilder();
		
			message.append("现金流[ID:"+cashStream.getId()+"]: ");
		if(loanflag==false){
			message.append(" 乾多多流水号不一致:[平台"+cashStream.getLoanNo()+"][钱多多"+LoanNo+"] ");
		}
		if(stateflag==false){
			message.append(" 操作状态不一致:[平台"+cashStream.getState()+"][钱多多"+RechargeState+"] ");
		}
		if(totalflag==false){
			message.append(" 金额不一致:[平台"+cashStream.getChiefamount().toString()+"][钱多多"+Amount+"] ");
		}
		if(feeflag==false){
			message.append(" 手续费不一致:[平台"+cashStream.getFee().toString()+"][钱多多"+Fee+"] ");
		}
		
		if(accountflag==false){
			message.append(" 账户不一致:[平台"+thirdPartyAccount+"][钱多多"+RechargeMoneymoremore+"] ");
		}
		
		if(!flag){
			throw new Exception(message.toString());
		}
		
		return flag;
	}
	
	private static boolean checkWithDrawResult(CashStream cashStream, String body) throws Exception{
		boolean flag = true;
		
		Gson gson = new Gson();
		List<Map<String, String>> res = (List<Map<String,String>>)gson.fromJson(body, List.class);
		
		if( (res==null || res.isEmpty()) && cashStream.getState() == cashStream.STATE_INIT){
			return true;
		}
		
		
		
		String thirdPartyAccount = "";
		
		if(cashStream.getLenderAccountId()!=null){
			Lender lender = lenderDao.findByAccountID(cashStream.getLenderAccountId());
			if(lender!=null){
				thirdPartyAccount = lender.getThirdPartyAccount();
			}
		}else if(cashStream.getBorrowerAccountId()!=null){
			Borrower borrower = borrowerDao.findByAccountID(cashStream.getBorrowerAccountId());
			if(borrower!=null){
				thirdPartyAccount = borrower.getThirdPartyAccount();
			}
		}
		
		
		Map<String, String> result = res.get(0);
		//额度
		String Amount = result.get("Amount");
		//乾多多流水号
		String LoanNo = result.get("LoanNo");
				
		//提现费用
		String FeeWithdraws = result.get("FeeWithdraws");
				
		//提现状态
		String WithdrawsState = result.get("WithdrawsState");
		
		//提现账号
		String WithdrawMoneymoremore = result.get("WithdrawMoneymoremore");
		
		boolean accountflag = thirdPartyAccount.equals(WithdrawMoneymoremore);
		
		boolean loanflag = cashStream.getLoanNo()==null? (LoanNo==null? true : false) : cashStream.getLoanNo().equals(LoanNo);
		boolean stateflag = false;
		if(WithdrawsState.equals("0")&&cashStream.getState()==2)
		{
			stateflag=true;
		}else if(WithdrawsState.equals("1")&&cashStream.getState()==2){
			stateflag=true;
		}else if(WithdrawsState.equals("2")&&cashStream.getState()==8){
			stateflag=true;
		}
		boolean totalflag = cashStream.getChiefamount().negate().compareTo(new BigDecimal(Amount))==0;
		boolean feeflag = cashStream.getFee().compareTo(new BigDecimal(FeeWithdraws))==0;
		
		flag = flag && loanflag && stateflag && totalflag && feeflag && accountflag;
		
		StringBuilder message = new StringBuilder();
		
			message.append("现金流[ID:"+cashStream.getId()+"]: ");
		if(loanflag==false){
			message.append(" 乾多多流水号不一致:[平台"+cashStream.getLoanNo()+"][钱多多"+LoanNo+"] ");
		}
		if(stateflag==false){
			message.append(" 操作状态不一致:[平台"+cashStream.getState()+"][钱多多"+WithdrawsState+"] ");
		}
		if(totalflag==false){
			message.append(" 金额不一致:[平台"+cashStream.getChiefamount().negate().toString()+"][钱多多"+Amount+"] ");
		}
		if(feeflag==false){
			message.append(" 手续费不一致:[平台"+cashStream.getFee().toString()+"][钱多多"+FeeWithdraws+"] ");
		}
		
		if(accountflag==false){
			message.append(" 账户不一致:[平台"+thirdPartyAccount+"][钱多多"+WithdrawMoneymoremore+"] ");
		}
		
		if(!flag){
			throw new Exception(message.toString());
		}
		
		return flag;
	}
	
	
	
//	public static void withdrawSingleCashStream(String loanNo){
//		String baseUrl=getBaseUrl(ACTION_CHECK);
//		Map<String,String> params=new HashMap<String, String>();
//		params.put("PlatformMoneymoremore", platformMoneymoremore);
//		params.put("AuditType", String.valueOf(2));
//		params.put("ReturnURL", "http://" + serverHost + ":" + serverPort + "/account/checkBuy/response/bg");
//		params.put("NotifyURL", params.get("ReturnURL"));
//		
//		params.put("LoanNoList", loanNo);
//		sendCheck(params,baseUrl);
//		
//	}
	
	private static void sendCheck(Map<String,String> params,String baseUrl)
	{
		//LoanNoList + PlatformMoneymoremore + AuditType + RandomTimeStamp + Remark1 + Remark2 + Remark3 + ReturnURL + NotifyURL
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(StringUtil.strFormat(params.get("LoanNoList")));
		sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(params.get("AuditType")));
		sBuilder.append(StringUtil.strFormat(params.get("RandomTimeStamp")));
		sBuilder.append(StringUtil.strFormat(params.get("Remark1")));
		sBuilder.append(StringUtil.strFormat(params.get("Remark2")));
		sBuilder.append(StringUtil.strFormat(params.get("Remark3")));
		sBuilder.append(StringUtil.strFormat(params.get("ReturnURL")));
		sBuilder.append(StringUtil.strFormat(params.get("NotifyURL")));
		RsaHelper rsa = RsaHelper.getInstance();
		String signInfo=rsa.signData(sBuilder.toString(), privateKey);
		params.put("SignInfo", signInfo);
		String body=httpClientService.post(baseUrl, params);
		Gson gson = new Gson();
		Map<String,String> returnParams=gson.fromJson(body, Map.class);
//		try {
//			checkBuyProcessor(returnParams);
//		} catch (SignatureException e) {
//			e.printStackTrace();
//		} catch (ResultCodeException e) {
//			e.printStackTrace();
//		}
	}
}
