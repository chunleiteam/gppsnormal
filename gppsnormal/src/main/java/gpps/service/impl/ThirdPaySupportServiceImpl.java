package gpps.service.impl;

import gpps.dao.ICardBindingDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.ILenderDao;
import gpps.dao.IPayBackDao;
import gpps.model.Borrower;
import gpps.model.CashStream;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.Submit;
import gpps.service.IAccountService;
import gpps.service.IBorrowerService;
import gpps.service.IGovermentOrderService;
import gpps.service.ILenderService;
import gpps.service.ILoginService;
import gpps.service.IProductService;
import gpps.service.ISubmitService;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.IllegalOperationException;
import gpps.service.exception.InsufficientBalanceException;
import gpps.service.exception.LoginException;
import gpps.service.exception.SMSException;
import gpps.service.message.ILetterSendService;
import gpps.service.message.IMessageService;
import gpps.service.thirdpay.Authorize;
import gpps.service.thirdpay.CardBinding;
import gpps.service.thirdpay.Cash;
import gpps.service.thirdpay.IHttpClientService;
import gpps.service.thirdpay.IThirdPaySupportService;
import gpps.service.thirdpay.Recharge;
import gpps.service.thirdpay.RegistAccount;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.Transfer;
import gpps.service.thirdpay.Transfer.LoanJson;
import gpps.tools.Common;
import gpps.tools.ObjectUtil;
import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;
public class ThirdPaySupportServiceImpl implements IThirdPaySupportService{
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
	private String url="";
	private String platformMoneymoremore;
	private String privateKey;
	private String publicKey;
	private String serverHost;
	private String serverPort;
	@Autowired
	IAccountService accountService;
	@Autowired
	ILenderService lenderService;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	ISubmitService submitService;
	@Autowired
	IProductService productService;
	@Autowired
	IGovermentOrderService orderService;
	@Autowired
	IBorrowerService borrowerService;
	@Autowired
	IHttpClientService httpClientService;
	@Autowired
	ICardBindingDao cardBindingDao;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	IPayBackDao payBackDao;
	@Autowired
	IMessageService messageService;
	@Autowired
	ILetterSendService letterSendService;
	private Logger log=Logger.getLogger(ThirdPaySupportServiceImpl.class);
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getBaseUrl(String action) {
		return url+urls.get(action);
	}
	
	public String getPlatformMoneymoremore() {
		return platformMoneymoremore;
	}

	public void setPlatformMoneymoremore(String platformMoneymoremore) {
		this.platformMoneymoremore = platformMoneymoremore;
	}
	
	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	@Override
	public RegistAccount getRegistAccount() throws LoginException {
		RegistAccount registAccount=new RegistAccount();
		registAccount.setBaseUrl(getBaseUrl(ACTION_REGISTACCOUNT));
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session=req.getSession();
		Object currentUser=session.getAttribute(ILoginService.SESSION_ATTRIBUTENAME_USER);
		if(currentUser==null)
			throw new LoginException("未找到用户信息，请重新登录");
		if(currentUser instanceof Lender)
		{
			registAccount.setAccountType(null);
			Lender lender=(Lender)currentUser;
			registAccount.setEmail(lender.getEmail());
			registAccount.setIdentificationNo(lender.getIdentityCard());
			registAccount.setLoanPlatformAccount("L"+lender.getId());
			registAccount.setMobile(lender.getTel());
			registAccount.setRealName(lender.getName());
		}else if(currentUser instanceof Borrower){
			registAccount.setAccountType(1);
			Borrower borrower=(Borrower)currentUser;
			registAccount.setEmail(borrower.getEmail());
			registAccount.setIdentificationNo(borrower.getLicense());
			registAccount.setLoanPlatformAccount("B"+borrower.getId());
			registAccount.setMobile(borrower.getTel());
			registAccount.setRealName(borrower.getCompanyName());//放置公司名称，公司名称必须以“有限公司”结尾
		}
		else {
			throw new RuntimeException("不支持该用户开户");
		}
		registAccount.setReturnURL(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/account/thirdPartyRegist/response");
		registAccount.setNotifyURL(registAccount.getReturnURL()+"/bg");
		registAccount.setPlatformMoneymoremore(platformMoneymoremore);
		registAccount.setSignInfo(registAccount.getSign(privateKey));
		return registAccount;
	}

	@Override
	public Recharge getRecharge(String amount) throws LoginException {
		Recharge recharge=new Recharge();
		recharge.setBaseUrl(getBaseUrl(ACTION_RECHARGE));
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session=req.getSession();
		Object currentUser=session.getAttribute(ILoginService.SESSION_ATTRIBUTENAME_USER);
		if(currentUser==null)
			throw new LoginException("未找到用户信息，请重新登录");
		recharge.setAmount(amount);
		recharge.setReturnURL(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/account/recharge/response");
		recharge.setNotifyURL(recharge.getReturnURL()+"/bg");
		recharge.setPlatformMoneymoremore(platformMoneymoremore);
		recharge.setSignInfo(recharge.getSign(privateKey));
		Integer cashStreamId = null;
		if(currentUser instanceof Lender)
		{
			Lender lender=(Lender)currentUser;
			recharge.setRechargeMoneymoremore(lender.getThirdPartyAccount());
			cashStreamId = accountService.rechargeLenderAccount(lender.getAccountId(), BigDecimal.valueOf(Double.valueOf(amount)), "充值");
		}else if(currentUser instanceof Borrower){
			Borrower borrower=(Borrower)currentUser;
			recharge.setRechargeMoneymoremore(borrower.getThirdPartyAccount());
			cashStreamId = accountService.rechargeBorrowerAccount(borrower.getAccountId(), BigDecimal.valueOf(Double.valueOf(amount)), "充值");
		}
		else {
			throw new RuntimeException("不支持该用户充值");
		}
		recharge.setOrderNo(String.valueOf(cashStreamId));
		recharge.setSignInfo(recharge.getSign(privateKey));
		return recharge;
	}

	@Override
	public Transfer getTransferToBuy(Integer submitId,String pid) throws InsufficientBalanceException, LoginException {
		Transfer transfer=new Transfer();
		transfer.setBaseUrl(getBaseUrl(ACTION_TRANSFER));
		
		Lender lender=lenderService.getCurrentUser();
		if(lender==null)
			throw new LoginException("未找到用户信息，请重新登录");
		Submit submit = ObjectUtil.checkNullObject(Submit.class, submitService.find(submitId));
		
		if(submit.getState()==Submit.STATE_COMPLETEPAY){
			throw new InsufficientBalanceException("该投标已经支付成功！");
		}
		
		GovermentOrder order=orderService.findGovermentOrderByProduct(submit.getProductId());
		Borrower borrower=borrowerService.find(order.getBorrowerId());
		
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		transfer.setAction("1");
		transfer.setNeedAudit(null);//空.需要审核
		transfer.setReturnURL(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + "/account/buy/response");
		transfer.setNotifyURL(transfer.getReturnURL()+"/bg");
		transfer.setPlatformMoneymoremore(platformMoneymoremore);
		transfer.setRemark1(pid);
		transfer.setTransferAction("1");//投标
		transfer.setTransferType("2");//直连
		
		List<LoanJson> loanJsons=new ArrayList<LoanJson>();
		Integer cashStreamId =null;
		//查看是否有存在的现金流 
		CashStream cashStream=cashStreamDao.findBySubmitAndState(submitId, CashStream.ACTION_FREEZE);
		if(cashStream==null)
			cashStreamId = accountService.freezeLenderAccount(lender.getAccountId(), submit.getAmount(), submitId, "购买");
		else
			cashStreamId=cashStream.getId();
		LoanJson loanJson=new LoanJson();
		loanJson.setLoanOutMoneymoremore(lender.getThirdPartyAccount());
		loanJson.setLoanInMoneymoremore(borrower.getThirdPartyAccount());
		loanJson.setOrderNo(cashStreamId.toString());
		loanJson.setBatchNo(String.valueOf(submit.getProductId()));//????
//		loanJson.setExchangeBatchNo(null);
//		loanJson.setAdvanceBatchNo(null);
		loanJson.setAmount(submit.getAmount().toString());
		loanJson.setFullAmount("");
		loanJson.setTransferName("投标");
		loanJson.setRemark("");
		loanJson.setSecondaryJsonList("");
		loanJsons.add(loanJson);
		transfer.setLoanJsonList(Common.JSONEncode(loanJsons));
		transfer.setSignInfo(transfer.getSign(privateKey));
		try {
			transfer.setLoanJsonList(URLEncoder.encode(transfer.getLoanJsonList(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return transfer;
	}

	@Override
	public void check(List<String> loanNos,int auditType) {
		if(loanNos==null||loanNos.size()==0)
			return;
		String baseUrl=getBaseUrl(ACTION_CHECK);
		StringBuilder loanNoSBuilder=new StringBuilder();
		Map<String,String> params=new HashMap<String, String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
		params.put("AuditType", String.valueOf(auditType));
		params.put("ReturnURL", "http://" + serverHost + ":" + serverPort + "/account/checkBuy/response/bg");
		params.put("NotifyURL", params.get("ReturnURL"));
		for(int i=0;i<loanNos.size();i++)
		{
			if(loanNoSBuilder.length()!=0)
				loanNoSBuilder.append(",");
			loanNoSBuilder.append(loanNos.get(i));
			if((i+1)%200==0)
			{
				params.put("LoanNoList", loanNoSBuilder.toString());
				sendCheck(params,baseUrl);
//				//测试回调
//				sendCheckRollback(params);
				loanNoSBuilder=new StringBuilder();
			}
		}
		if(loanNoSBuilder.length()>0)
		{
			params.put("LoanNoList", loanNoSBuilder.toString());
			sendCheck(params,baseUrl);
//			//测试回调
//			sendCheckRollback(params);
		}
	}
	private void sendCheck(Map<String,String> params,String baseUrl)
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
		try {
			checkBuyProcessor(returnParams);
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (ResultCodeException e) {
			e.printStackTrace();
		}
	}
	private void sendCheckRollback(Map<String,String> params)
	{
		Map<String,String> paramsRollback=new HashMap<String,String>();
		paramsRollback.putAll(params);
		paramsRollback.put("ResultCode", "88");
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(StringUtil.strFormat(paramsRollback.get("LoanNoList")));
		sBuilder.append(StringUtil.strFormat(paramsRollback.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(paramsRollback.get("AuditType")));
		sBuilder.append(StringUtil.strFormat(paramsRollback.get("RandomTimeStamp")));
		sBuilder.append(StringUtil.strFormat(paramsRollback.get("Remark1")));
		sBuilder.append(StringUtil.strFormat(paramsRollback.get("Remark2")));
		sBuilder.append(StringUtil.strFormat(paramsRollback.get("Remark3")));
		sBuilder.append(StringUtil.strFormat(paramsRollback.get("ResultCode")));
		RsaHelper rsa = RsaHelper.getInstance();
		String signInfo=rsa.signData(sBuilder.toString(), privateKey);
		paramsRollback.put("SignInfo", signInfo);
		String body=httpClientService.post(params.get("NotifyURL"), paramsRollback);
		log.info(body);
	}
	@Override
	public CardBinding getCardBinding() throws LoginException {
		CardBinding cardBinding=new CardBinding();
		cardBinding.setBaseUrl(getBaseUrl(ACTION_CARDBINDING));
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session=req.getSession();
		Object currentUser=session.getAttribute(ILoginService.SESSION_ATTRIBUTENAME_USER);
		if(currentUser==null)
			throw new LoginException("未找到用户信息，请重新登录");
		cardBinding.setPlatformMoneymoremore(platformMoneymoremore);
		cardBinding.setAction("2");
//		RsaHelper rsa = RsaHelper.getInstance();
//		cardBinding.setCardNo(cardNo);
		cardBinding.setReturnURL(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() +"/account/cardBinding/response");
		cardBinding.setNotifyURL(cardBinding.getReturnURL()+"/bg");
		if(currentUser instanceof Lender)
		{
			Lender lender=(Lender)currentUser;
			cardBinding.setMoneymoremoreId(lender.getThirdPartyAccount());
		}else if(currentUser instanceof Borrower){
			Borrower borrower=(Borrower)currentUser;
			cardBinding.setMoneymoremoreId(borrower.getThirdPartyAccount());
		}
		else {
			throw new RuntimeException("不支持该用户开户");
		}
		cardBinding.setSignInfo(cardBinding.getSign(privateKey));
//		cardBinding.setCardNo(rsa.encryptData(cardNo, publicKey));
		return cardBinding;
	}

	@Override
	public Cash getCash(String amount) throws InsufficientBalanceException, LoginException, IllegalOperationException {
		Cash cash=new Cash();
		cash.setBaseUrl(getBaseUrl(ACTION_CASH));
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session=req.getSession();
		Object currentUser=session.getAttribute(ILoginService.SESSION_ATTRIBUTENAME_USER);
		if(currentUser==null)
			throw new LoginException("未找到用户信息，请重新登录");
		cash.setPlatformMoneymoremore(platformMoneymoremore);
		cash.setAmount(amount);
		Integer cashStreamId = null;
		String cardNo=null;
		gpps.model.CardBinding cardBinding=null;
		if(currentUser instanceof Lender)
		{
			Lender lender=(Lender)currentUser;
			cash.setWithdrawMoneymoremore(lender.getThirdPartyAccount());
			cashStreamId = accountService.cashLenderAccount(lender.getAccountId(), BigDecimal.valueOf(Double.valueOf(amount)), "提现");
			cardBinding=cardBindingDao.find(lender.getCardBindingId());
		}else if(currentUser instanceof Borrower){
			Borrower borrower=(Borrower)currentUser;
			// 验证是否有正在还款的payback
			List<Integer> states=new ArrayList();
			states.add(PayBack.STATE_REPAYING);
			states.add(PayBack.STATE_WAITFORCHECK);
			int count=payBackDao.countByBorrowerAndState(borrower.getAccountId(), states, -1,-1);
			if(count>0)
				throw new IllegalOperationException("存在正在进行的还款，请等待还款结束再提现.");
			cash.setWithdrawMoneymoremore(borrower.getThirdPartyAccount());
			cashStreamId = accountService.cashBorrowerAccount(borrower.getAccountId(), BigDecimal.valueOf(Double.valueOf(amount)), "提现");
			cardBinding=cardBindingDao.find(borrower.getCardBindingId());
		}
		else {
			throw new RuntimeException("不支持该用户提现");
		}
		if(cardBinding==null)
			throw new IllegalOperationException("未绑定银行卡");
		cardNo=cardBinding.getCardNo();
		cash.setCardNo(cardNo);
		cash.setCardType(String.valueOf(cardBinding.getCardType()));
		cash.setBankCode(cardBinding.getBankCode());
		cash.setBranchBankName(cardBinding.getBranchBankName());
		cash.setProvince(cardBinding.getProvince());
		cash.setCity(cardBinding.getCity());
		cash.setOrderNo(String.valueOf(cashStreamId));
		cash.setReturnURL(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() +"/account/cash/response");
		cash.setNotifyURL(cash.getReturnURL()+"/bg");
		cash.setSignInfo(cash.getSign(privateKey));
		RsaHelper rsa = RsaHelper.getInstance();
		cash.setCardNo(rsa.encryptData(cardNo, publicKey));
		return cash;
	}

	@Override
	public Authorize getAuthorize() throws LoginException {
		Borrower borrower=borrowerService.getCurrentUser();
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		if(borrower==null)
			throw new LoginException("未找到用户信息，请重新登录");
		Authorize authorize=new Authorize();
		authorize.setBaseUrl(getBaseUrl(ACTION_AUTHORIZE));
		
		authorize.setMoneymoremoreId(borrower.getThirdPartyAccount());
		authorize.setPlatformMoneymoremore(platformMoneymoremore);
		authorize.setAuthorizeTypeOpen("2");
		authorize.setReturnURL(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() +"/account/authorize/response");
		authorize.setNotifyURL(authorize.getReturnURL()+"/bg");
		authorize.setSignInfo(authorize.getSign(privateKey));
		return authorize;
	}

	@Override
	public void repay(List<LoanJson> loanJsons, PayBack payback) {
		if(loanJsons==null||loanJsons.size()==0)
			return;
		String baseUrl=getBaseUrl(ACTION_TRANSFER);
		Map<String,String> params=new HashMap<String,String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
		params.put("TransferAction", "2");
		params.put("Action", "2");
		params.put("TransferType", "2");
		
		//还款无需审核
		params.put("NeedAudit", "1");
		
//		//将还款改为需要审核
//		params.put("NeedAudit", null);
		
		params.put("NotifyURL", "http://"+serverHost+":"+serverPort+"/account/repay/response/bg");
		List<LoanJson> temp=new ArrayList<LoanJson>();
		
		Product product = productService.find(payback.getProductId());
		GovermentOrder order = orderService.findGovermentOrderByProduct(payback.getProductId());
		
		for(int i=0;i<loanJsons.size();i++)
		{
			temp.add(loanJsons.get(i));
			if((i+1)%200==0)
			{
				String LoanJsonList=Common.JSONEncode(temp);
				params.put("LoanJsonList", LoanJsonList);
				temp.clear();
				sendRepay(params,baseUrl, order, product, payback);
				//测试
//				sendRepayRollback(LoanJsonList,params.get("NotifyURL"));
			}
		}
		if(temp.size()>0)
		{
			String LoanJsonList=Common.JSONEncode(temp);
			params.put("LoanJsonList", LoanJsonList);
			sendRepay(params,baseUrl, order, product, payback);
//			//测试
//			sendRepayRollback(LoanJsonList,params.get("NotifyURL"));
		}
	}
	private void sendRepay(Map<String,String> params,String baseUrl, GovermentOrder order, Product product, PayBack payback)
	{
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(StringUtil.strFormat(params.get("LoanJsonList")));
		sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(params.get("TransferAction")));
		sBuilder.append(StringUtil.strFormat(params.get("Action")));
		sBuilder.append(StringUtil.strFormat(params.get("TransferType")));
		sBuilder.append(StringUtil.strFormat(params.get("NeedAudit")));
		sBuilder.append(StringUtil.strFormat(params.get("NotifyURL")));
		RsaHelper rsa = RsaHelper.getInstance();
		String signInfo=rsa.signData(sBuilder.toString(), privateKey);
		params.put("SignInfo", signInfo);
		try {
			params.put("LoanNoList",URLEncoder.encode(params.get("LoanJsonList"),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String body=httpClientService.post(baseUrl, params);
		Gson gson = new Gson();
		
		
		//自动、免审核转账(Action=2  NeedAudit=1)，除了会通知NotifyURL外，还会将参数以JSON字符串的形式直接输出在页面上，其中包含了2个JSON，一个action为空，表示转账成功，另一个action=1，表示审核通过
		//因此返回的是两个JSON对象，第一个表示成功，第二个记录了具体的转账信息
		List returnParams=gson.fromJson(body, List.class);
		try {
			repayProcessor((Map<String,String>)returnParams.get(1), order, product, payback);
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (ResultCodeException e) {
			e.printStackTrace();
		}
	}
	private void sendRepayRollback(String LoanJsonList,String notifyURL)
	{
		Map<String,String> paramsRollback=new HashMap<String,String>();
		try {
			paramsRollback.put("LoanJsonList",URLEncoder.encode(LoanJsonList,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		paramsRollback.put("PlatformMoneymoremore", platformMoneymoremore);
		paramsRollback.put("ResultCode", "88");
		paramsRollback.put("Message", "成功");
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(LoanJsonList);
		sBuilder.append(StringUtil.strFormat(paramsRollback.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(paramsRollback.get("ResultCode")));
//		sBuilder.append(StringUtil.strFormat(paramsRollback.get("Message")));
		RsaHelper rsa = RsaHelper.getInstance();
		String signInfo=rsa.signData(sBuilder.toString(), privateKey);
		paramsRollback.put("SignInfo", signInfo);
		String body=httpClientService.post(notifyURL, paramsRollback);
		log.info(body);
	}
	public void checkRollBack(Map<String,String> params,String[] signStrs) throws ResultCodeException, SignatureException
	{
		String resultCode=params.get("ResultCode");
		if(StringUtil.isEmpty(resultCode)||!resultCode.equals("88"))
			throw new ResultCodeException(resultCode, params.get("Message"));
		StringBuilder sBuilder=new StringBuilder();
		for(String str:signStrs)
		{
			sBuilder.append(StringUtil.strFormat(params.get(str)));
		}
		RsaHelper rsa = RsaHelper.getInstance();
		String sign=rsa.signData(sBuilder.toString(), privateKey);
		if(!sign.replaceAll("\r", "").equals(params.get("SignInfo").replaceAll("\r", "")))
			throw new SignatureException("非法的签名");
	}
	public void checkBuyProcessor(Map<String,String> params) throws SignatureException, ResultCodeException
	{
		String[] signStrs={"LoanNoList","LoanNoListFail","PlatformMoneymoremore","AuditType","RandomTimeStamp"
				,"Remark1","Remark2","Remark3","ResultCode"};
		checkRollBack(params, signStrs);
		String auditType=params.get("AuditType");
		if(!StringUtil.isEmpty(params.get("LoanNoList")))
		{
			String[] loanNoList=params.get("LoanNoList").split(",");
			for(String loanNo:loanNoList)
			{
				List<CashStream> cashStreams=cashStreamDao.findSuccessByActionAndLoanNo(-1, loanNo);
				if(cashStreams.size()==2)
					continue;    //重复的命令
				CashStream cashStream=cashStreams.get(0);
				try {
					Integer cashStreamId=null;
					if(auditType.equals("1")) //通过审核
					{
						Submit submit=submitService.find(cashStream.getSubmitId());
						GovermentOrder order=orderService.findGovermentOrderByProduct(submit.getProductId());
						Product product = productService.find(submit.getProductId());
						Borrower borrower=borrowerService.find(order.getBorrowerId());
						cashStreamId=accountService.pay(cashStream.getLenderAccountId(), borrower.getAccountId(),cashStream.getChiefamount().negate(),cashStream.getSubmitId(), "支付");
						
						//每转一笔，都给对应的lender发送短信
						Map<String, String> param = new HashMap<String, String>();
						param.put(IMessageService.PARAM_ORDER_NAME, order.getTitle());
						param.put(IMessageService.PARAM_PRODUCT_SERIES_NAME, product.getProductSeries().getTitle());
						param.put(IMessageService.PARAM_AMOUNT, submit.getAmount().toString());
						
						param.put(ILetterSendService.PARAM_TITLE, "投资启动,开始计息");
						try{
						letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_FINANCINGSUCCESS, ILetterSendService.USERTYPE_LENDER, submit.getLenderId(), param);
						messageService.sendMessage(IMessageService.MESSAGE_TYPE_FINANCINGSUCCESS, IMessageService.USERTYPE_LENDER, submit.getLenderId(), param);
						}catch(SMSException e){
							log.error(e.getMessage());
						}
					}
					else
					{
						Submit submit=submitService.find(cashStream.getSubmitId());
						GovermentOrder order=orderService.findGovermentOrderByProduct(submit.getProductId());
						Product product = productService.find(submit.getProductId());
						cashStreamId=accountService.unfreezeLenderAccount(cashStream.getLenderAccountId(), cashStream.getChiefamount().negate(), cashStream.getSubmitId(), "流标");
						
						//每转一笔，都给对应的lender发送短信
						Map<String, String> param = new HashMap<String, String>();
						param.put(IMessageService.PARAM_ORDER_NAME, order.getTitle());
						param.put(IMessageService.PARAM_PRODUCT_SERIES_NAME, product.getProductSeries().getTitle());
						param.put(IMessageService.PARAM_AMOUNT, submit.getAmount().toString());
						
						param.put(ILetterSendService.PARAM_TITLE, "投资流标,资金解冻");
						try{
						letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_FINANCINGFAIL, ILetterSendService.USERTYPE_LENDER, submit.getLenderId(), param);
						messageService.sendMessage(IMessageService.MESSAGE_TYPE_FINANCINGFAIL, IMessageService.USERTYPE_LENDER, submit.getLenderId(), param);
						}catch(SMSException e){
							log.error(e.getMessage());
						}
					}
					cashStreamDao.updateLoanNo(cashStreamId, loanNo,null);
				} catch (IllegalConvertException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void repayProcessor(Map<String,String> params, GovermentOrder order, Product product, PayBack payback) throws SignatureException, ResultCodeException
	{
		String[] signStrs={"LoanJsonList","PlatformMoneymoremore","Action","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		String loanJsonList = null;
		try {
			loanJsonList=URLDecoder.decode(params.get("LoanJsonList"),"UTF-8");
			params.put("LoanJsonList", loanJsonList);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		checkRollBack(params, signStrs);
		List<Object> loanJsons=Common.JSONDecodeList(loanJsonList, LoanJson.class);
		if(loanJsons==null||loanJsons.size()==0)
			return;
		for(Object obj:loanJsons)
		{
			LoanJson loanJson=(LoanJson)obj;
			Integer cashStreamId = Integer.parseInt(loanJson.getOrderNo());
			String loanNo=loanJson.getLoanNo();
			CashStream cashStream = cashStreamDao.find(cashStreamId);
			if(cashStream.getState()==CashStream.STATE_SUCCESS)
			{
				log.debug("重复的回复");
				continue;
			}
			cashStreamDao.updateLoanNo(cashStreamId, loanNo,null);
			try {
				accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
				
				Map<String, String> param = new HashMap<String, String>();
				param.put(IMessageService.PARAM_ORDER_NAME, order.getTitle());
				param.put(IMessageService.PARAM_PRODUCT_SERIES_NAME, product.getProductSeries().getTitle());
				param.put(IMessageService.PARAM_AMOUNT, cashStream.getChiefamount().add(cashStream.getInterest()).toString());
				param.put(ILetterSendService.PARAM_TITLE, "收到一笔还款");
				Lender lender = lenderDao.findByAccountID(cashStream.getLenderAccountId());
				
				if(lender!=null)
				{
					try {
						letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_PAYBACKSUCCESS, ILetterSendService.USERTYPE_LENDER, lender.getId(), param);
						messageService.sendMessage(IMessageService.MESSAGE_TYPE_PAYBACKSUCCESS,IMessageService.USERTYPE_LENDER,lender.getId(), param);
					} catch (SMSException e) {
						log.error(e.getMessage());
					}
				}
				} catch (IllegalConvertException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void checkCash(Integer cashStreamId)
			throws IllegalOperationException {
		CashStream cashStream=cashStreamDao.find(cashStreamId);
		if(cashStream==null)
			return;
		if(cashStream.getAction()!=CashStream.ACTION_CASH)
			throw new IllegalOperationException("只验证提现");
		if(cashStream.getState()!=CashStream.STATE_SUCCESS)
			throw new IllegalOperationException("只验证已提现的流水");
		String baseUrl=getBaseUrl(ACTION_ORDERQUERY);
		Map<String,String> params=new HashMap<String,String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
		params.put("Action", "2");
		params.put("LoanNo", cashStream.getLoanNo());
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(params.get("Action")));
		sBuilder.append(StringUtil.strFormat(params.get("LoanNo")));
		RsaHelper rsa = RsaHelper.getInstance();
		params.put("SignInfo", rsa.signData(sBuilder.toString(), privateKey));
		String body=httpClientService.post(baseUrl, params);
		Gson gson = new Gson();
		Map<String,String> returnParams=gson.fromJson(body, Map.class);
		try {
			String withdrawsState=returnParams.get("WithdrawsState");
			String loanNo = returnParams.get("LoanNo");
			if(withdrawsState.equals("2"))
			{
				//退回
				accountService.returnCash(Integer.parseInt(returnParams.get("OrderNo")), loanNo);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalConvertException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String balanceQuery(String thirdPartyAccount) {
		Map<String, String> params=new HashMap<String, String>();
		params.put("PlatformId", thirdPartyAccount);
		params.put("PlatformMoneymoremore", platformMoneymoremore);
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(thirdPartyAccount);
		sBuilder.append(platformMoneymoremore);
		RsaHelper rsa = RsaHelper.getInstance();
		String signInfo=rsa.signData(sBuilder.toString(), privateKey);
		params.put("SignInfo", signInfo);
		String body=null;
		try
		{
			body=httpClientService.post(getBaseUrl(ACTION_BALANCEQUERY), params);
		}catch(Throwable e)
		{
			e.printStackTrace();
		}
		return body;
	}

	@Override
	public void checkWithThirdPay(Integer cashStreamId)
			throws IllegalOperationException, IllegalConvertException {
		CashStream cashStream=cashStreamDao.find(cashStreamId);
		if(cashStream==null)
			return;
		if(cashStream.getAction()!=CashStream.ACTION_CASH&&cashStream.getAction()!=CashStream.ACTION_FREEZE&&cashStream.getAction()!=CashStream.ACTION_RECHARGE)
			return;
		if(cashStream.getState()==CashStream.STATE_SUCCESS&&(cashStream.getAction()==CashStream.ACTION_FREEZE||cashStream.getAction()==CashStream.ACTION_RECHARGE))
			return;
		String baseUrl=getBaseUrl(ACTION_ORDERQUERY);
		Map<String,String> params=new HashMap<String,String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
		if(cashStream.getAction()==CashStream.ACTION_CASH)
			params.put("Action", "2");
		else if(cashStream.getAction()==CashStream.ACTION_RECHARGE)
			params.put("Action", "1");
		params.put("OrderNo", String.valueOf(cashStream.getId()));
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(params.get("Action")));
		sBuilder.append(StringUtil.strFormat(params.get("OrderNo")));
		RsaHelper rsa = RsaHelper.getInstance();
		params.put("SignInfo", rsa.signData(sBuilder.toString(), privateKey));
		String body=httpClientService.post(baseUrl, params);
		Gson gson = new Gson();
		Map<String,String> returnParams=(Map<String, String>) (gson.fromJson(body, List.class).get(0));
		if(cashStream.getAction()==CashStream.ACTION_CASH)
		{
			String withdrawsState=returnParams.get("WithdrawsState");
			if(withdrawsState.equals("0")||withdrawsState.equals("1"))
			{
				if(cashStream.getState()==CashStream.STATE_SUCCESS)
					return;
				String loanNo=returnParams.get("LoanNo");
				cashStreamDao.updateLoanNo(cashStreamId, loanNo,new BigDecimal(returnParams.get("FeeWithdraws")));
				accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
			}
			else if(withdrawsState.equals("2"))
			{
				if(cashStream.getState()!=CashStream.STATE_SUCCESS)
					return;
				String loanNo = returnParams.get("LoanNo");
				accountService.returnCash(cashStreamId, loanNo);//退回
			}
		}else if(cashStream.getAction()==CashStream.ACTION_RECHARGE)
		{
			if(cashStream.getState()==CashStream.STATE_SUCCESS)
				return;
			if(returnParams.get("RechargeState").equals("1"))
			{
				//RechargeState:0.未充值;1.成功;2.失败
				String loanNo=returnParams.get("LoanNo");
				cashStreamDao.updateLoanNo(cashStreamId, loanNo,null);
				accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
			}
		}else if(cashStream.getAction()==CashStream.ACTION_FREEZE)
		{
			if(cashStream.getState()==CashStream.STATE_SUCCESS)
				return;
			if(returnParams.get("TransferState").equals("1")&&(returnParams.get("ActState").equals("3")||returnParams.get("ActState").equals("1")))
			{
				//TransferState:0.未转账;1.已转账
				//ActState:0.未操作;1.已通过;2.已退回;3.自动通过
				String loanNo=returnParams.get("LoanNo");
				submitService.confirmBuy(cashStream.getSubmitId());
				cashStreamDao.updateLoanNo(cashStreamId, loanNo,null);
				accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
			}
		}
	}
}
