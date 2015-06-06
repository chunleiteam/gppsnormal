package gpps.servlet;

import gpps.dao.IBorrowerDao;
import gpps.dao.ICardBindingDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.ILenderDao;
import gpps.dao.IProductSeriesDao;
import gpps.inner.service.IInnerThirdPaySupportService;
import gpps.model.Borrower;
import gpps.model.CardBinding;
import gpps.model.CashStream;
import gpps.model.GovermentOrder;
import gpps.model.Lender;
import gpps.model.Product;
import gpps.model.Submit;
import gpps.service.IAccountService;
import gpps.service.IBorrowerService;
import gpps.service.IGovermentOrderService;
import gpps.service.ILenderService;
import gpps.service.IPayBackService;
import gpps.service.IProductService;
import gpps.service.ISubmitService;
import gpps.service.ITaskService;
import gpps.service.exception.IllegalConvertException;
import gpps.service.exception.SMSException;
import gpps.service.message.ILetterSendService;
import gpps.service.message.IMessageService;
import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.IThirdPaySupportNewService;
import gpps.service.thirdpay.IThirdPaySupportService;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.ThirdPartyState;
import gpps.service.thirdpay.Transfer.LoanJson;
import gpps.tools.Common;
import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountServlet {
	@Autowired
	IAccountService accountService;
	@Autowired
	ILenderService lenderService;
	@Autowired
	ISubmitService submitService;
	@Autowired
	ICashStreamDao cashStreamDao;
	@Autowired
	IPayBackService payBackService;
	@Autowired
	ITaskService taskService;
	@Autowired
	IProductService productService;
	@Autowired
	IGovermentOrderService orderService;
	@Autowired
	IProductSeriesDao productSeriesDao;
	@Autowired
	IBorrowerService borrowerService;
	@Autowired
	IThirdPaySupportService thirdPaySupportService;
	@Autowired
	IInnerThirdPaySupportService innerThirdPaySupportService;
	@Autowired
	IThirdPaySupportNewService thirdPaySupportNewService;
	@Autowired
	ICardBindingDao cardBindingDao;
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	IBorrowerDao borrowerDao;
	@Autowired
	IMessageService messageService;
	@Autowired
	ILetterSendService letterSendService;
	Logger log = Logger.getLogger(AccountServlet.class);
	public static final String AMOUNT = "amount";
	public static final String CASHSTREAMID = "cashStreamId";
	public static final String SUBMITID = "submitId";
	public static final String PAYBACKID = "paybackId";

	@RequestMapping(value = { "/account/thirdPartyRegist/response" })
	public void completeThirdPartyRegist(HttpServletRequest req, HttpServletResponse resp) {
		String message=null;
		try {
			completeThirdPartyRegistProcessor(req, resp);
		} catch (SignatureException e) {
			log.error(e.getMessage(),e);
			message=e.getMessage();
		} catch (ResultCodeException e) {
			//TODO 返回页面提示信息
			log.debug(e.getMessage(),e);
			message=e.getMessage();
		}
		//重定向到指定页面
		writeMsg(resp,message,"/myaccount.html?fid=mycenter");
	}
	
	@RequestMapping(value = { "/account/thirdPartyRegist/response/bg" })
	public void completeThirdPartyRegistBg(HttpServletRequest req, HttpServletResponse resp) {
		try {
			completeThirdPartyRegistProcessor(req, resp);
		} catch (SignatureException e) {
			log.error(e.getMessage(),e);
			return;
		} catch (ResultCodeException e) {
			log.debug(e.getMessage(),e);
			return;
		}
		writeSuccess(resp);
	}
	private void completeThirdPartyRegistProcessor(HttpServletRequest req, HttpServletResponse resp) throws SignatureException, ResultCodeException
	{
		log.debug("开户回调:"+req.getRequestURI());
		Map<String,String> params=getAllParams(req);
		String[] signStrs={"AccountType","AccountNumber","Mobile","Email","RealName","IdentificationNo","LoanPlatformAccount",
				"MoneymoremoreId","PlatformMoneymoremore","AuthFee","AuthState","RandomTimeStamp",
				"Remark1","Remark2","Remark3","ResultCode"};
		try {
			thirdPaySupportService.checkRollBack(params, signStrs);
		} catch (ResultCodeException e) {
			if(!e.getResultCode().equals("16"))
				throw e;
		}
		String thirdPartyAccount = params.get("MoneymoremoreId");
		String accountType=params.get("AccountType");
		String loanPlatformAccount=params.get("LoanPlatformAccount");
		String accountNumber=params.get("AccountNumber");
		Integer id=Integer.parseInt(loanPlatformAccount.substring(1, loanPlatformAccount.length()));
		String email=params.get("Email");
		
		//只更新邮箱地址，不更新手机号
//		String tel=params.get("Mobile");
		if (StringUtil.isEmpty(accountType)) {
			lenderService.registerThirdPartyAccount(id,thirdPartyAccount,accountNumber);
			Lender lender=lenderDao.find(id);
			if(StringUtil.isEmpty(email))
				email=lender.getEmail();
//			if(StringUtil.isEmpty(tel))
				String tel=lender.getTel();
			lenderDao.updateTelAndEmail(id, tel, email);
		} else if (accountType.equals("1")) {
			borrowerService.registerThirdPartyAccount(id,thirdPartyAccount,accountNumber);
			Borrower borrower=borrowerDao.find(id);
			if(StringUtil.isEmpty(email))
				email=borrower.getEmail();
//			if(StringUtil.isEmpty(tel))
				String tel=borrower.getTel();
			borrowerDao.updateTelAndEmail(id, tel, email);
		}
	}

	@RequestMapping(value = { "/account/recharge/response" })
	public void completeRecharge(HttpServletRequest req, HttpServletResponse resp) {
		Map<String,String> params=getAllParams(req);
		String amount = params.get("Amount");
		String message="充值已成功， 充值额度"+amount;
		try {
			completeRechargeProcessor(req, resp);
		} catch (SignatureException e) {
			log.error(e.getMessage(),e);
			message=e.getMessage();
		} catch (ResultCodeException e) {
			log.debug(e.getMessage(),e);
			message=e.getMessage();
		}
		writeMsg(resp,message,"/myaccount.html?fid=cash&sid=cash-recharge");
	}
	@RequestMapping(value = { "/account/recharge/response/bg" })
	public void completeRechargeBg(HttpServletRequest req, HttpServletResponse resp) {
		try {
			try{
			Thread.sleep(200);
			}catch(InterruptedException e){
				
			}
			completeRechargeProcessor(req, resp);
		} catch (SignatureException e) {
			log.error(e.getMessage(),e);
			return;
		} catch (ResultCodeException e) {
			log.debug(e.getMessage(),e);
		}
		writeSuccess(resp); 
	}
	private void completeRechargeProcessor(HttpServletRequest req, HttpServletResponse resp) throws SignatureException, ResultCodeException
	{
		log.debug("充值回调:"+req.getRequestURI());
		Map<String,String> params=getAllParams(req);
		String[] signStrs={"RechargeMoneymoremore","PlatformMoneymoremore","LoanNo","OrderNo","Amount","Fee","FeePlatform",
				"RechargeType","FeeType","CardNoList","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		thirdPaySupportService.checkRollBack(params, signStrs);
		Integer cashStreamId = Integer.parseInt(StringUtil.checkNullAndTrim("cashStreamId", StringUtil.strFormat(params.get("OrderNo"))));
		String loanNo=params.get("LoanNo");
		log.debug("充值成功");
		CashStream cashStream=cashStreamDao.find(cashStreamId);
		if(cashStream.getState()==CashStream.STATE_SUCCESS)
		{
			log.debug("重复的回复");
			return;
		}
		cashStreamDao.updateLoanNo(cashStreamId, loanNo,null);
		try {
			accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
		} catch (IllegalConvertException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = { "/account/cash/response" })
	public void completeCash(HttpServletRequest req, HttpServletResponse resp) {
		Map<String,String> params=getAllParams(req);
		String amount = params.get("Amount");
		String fee = params.get("FeeWithdraws");
		String message="您的资金申请提取成功， 申请提取额度"+amount+"，手续费"+fee+". 到账时间以所在银行为准！";
		try {
			completeCashProcessor(req,resp);
		} catch (SignatureException e) {
			log.error(e.getMessage(),e);
			message=e.getMessage();
		} catch (ResultCodeException e) {
			log.debug(e.getMessage(),e);
			message=e.getMessage();
		}
		writeMsg(resp,message,"/myaccount.html?fid=cash&sid=cash-withdraw");
	}
	@RequestMapping(value = { "/account/cash/response/bg" })
	public void completeCashBg(HttpServletRequest req, HttpServletResponse resp) {
		try {
			try{
				Thread.sleep(200);
				}catch(InterruptedException e){
					
				}
			completeCashProcessor(req,resp);
		} catch (SignatureException e) {
			log.error(e.getMessage(),e);
			return;
		} catch (ResultCodeException e) {
			log.debug(e.getMessage(),e);
		}
		writeSuccess(resp); 
	}
	private void completeCashProcessor(HttpServletRequest req,HttpServletResponse reps) throws SignatureException, ResultCodeException
	{
		log.debug("提现回调:"+req.getRequestURI());
		Map<String,String> params=getAllParams(req);
		String[] signStrs={"WithdrawMoneymoremore","PlatformMoneymoremore","LoanNo","OrderNo","Amount","FeeMax","FeeWithdraws",
				"FeePercent","Fee","FreeLimit","FeeRate","FeeSplitting","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		Integer cashStreamId = Integer.parseInt(StringUtil.checkNullAndTrim("cashStreamId", StringUtil.strFormat(params.get("OrderNo"))));
		try{
			thirdPaySupportService.checkRollBack(params, signStrs);
		}catch(ResultCodeException e)
		{
			String resultCode=e.getResultCode();
			if(resultCode.equals("89"))
			{
				try {
					String loanNo=params.get("LoanNo");
					accountService.returnCash(cashStreamId, loanNo);
				} catch (IllegalConvertException e1) {
					e1.printStackTrace();
				}
			}
			else 
				throw e;
		}
		String loanNo=params.get("LoanNo");
		try {
			log.debug("取现成功");
			CashStream cashStream=cashStreamDao.find(cashStreamId);
			if(cashStream.getState()==CashStream.STATE_SUCCESS)
			{
				log.debug("重复的回复");
				return;
			}
//			if(cashStream.getChiefamount().negate().compareTo(new BigDecimal(StringUtil.strFormat(params.get("Amount"))))!=0)
//			{
//				write(resp, "取现金额不符，请联系管理员解决.");
//				return;
//			}
			cashStreamDao.updateLoanNo(cashStreamId, loanNo,new BigDecimal(params.get("FeeWithdraws")));
			accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
			
			Map<String, String> param = new HashMap<String, String>();
			param.put(IMessageService.PARAM_AMOUNT, cashStream.getChiefamount().negate().toString());
			param.put(IMessageService.PARAM_FEE, params.get("FeeWithdraws"));
			param.put(ILetterSendService.PARAM_TITLE, "资金提现");
			
			
			//发送短信提醒
			if(cashStream.getLenderAccountId()!=null)
			{
				Lender lender = lenderDao.findByAccountID(cashStream.getLenderAccountId());
				try{
				letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_CASHOUTSUCCESS, ILetterSendService.USERTYPE_LENDER, lender.getId(), param);
				messageService.sendMessage(IMessageService.MESSAGE_TYPE_CASHOUTSUCCESS, IMessageService.USERTYPE_LENDER, lender.getId(), param);
				}catch(SMSException e){
					log.error(e.getMessage());
				}
			}else if(cashStream.getBorrowerAccountId()!=null){
				Borrower borrower = borrowerDao.findByAccountID(cashStream.getBorrowerAccountId());
				try{
					letterSendService.sendMessage(ILetterSendService.MESSAGE_TYPE_CASHOUTSUCCESS, ILetterSendService.USERTYPE_BORROWER, borrower.getId(), param);
					messageService.sendMessage(IMessageService.MESSAGE_TYPE_CASHOUTSUCCESS, IMessageService.USERTYPE_BORROWER, borrower.getId(), param);
				}catch(SMSException e){
					log.error(e.getMessage());
				}
			}
			
		} catch (IllegalConvertException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	
	@RequestMapping(value = { "/account/purchase/response" })
	public void completePurchase(HttpServletRequest req, HttpServletResponse resp) {
		Map<String,String> params=getAllParams(req);
		String message=null;
		try {
			completePurchaseProcessor(req, resp);
		} catch (SignatureException e) {
			log.error(e.getMessage());
			message=e.getMessage();
		} catch (ResultCodeException e) {
			log.error(e.getMessage());
			message=e.getMessage();
		} catch (Exception e){
			log.error(e.getMessage());
			message = e.getMessage();
		}
		if(!StringUtil.isEmpty(message)){
			writeMsg(resp,message,"/myaccount.html");
			return;
		}
		writeMsg(resp,message,"/myaccount.html?fid=submit&sid=submit-all");
	}
	
	@RequestMapping(value = { "/account/purchase/response/bg" })
	public void completePurchaseBg(HttpServletRequest req, HttpServletResponse resp) {
		try {
			try{
				Thread.sleep(200);
				}catch(InterruptedException e){
					
				}
			completePurchaseProcessor(req, resp);
		} catch (SignatureException e) {
			log.error(e.getMessage());
			return;
		} catch (ResultCodeException e) {
			log.error(e.getMessage());
			return;
		} catch (Exception e){
			log.error(e.getMessage());
			return;
		}
		writeSuccess(resp);
	}
	
	
	private void completePurchaseProcessor(HttpServletRequest req,HttpServletResponse resp) throws SignatureException, ResultCodeException, Exception
	{
		log.debug("购买债权回调:"+req.getRequestURI());
		Map<String,String> params=getAllParams(req);
		String[] signStrs={"LoanJsonList","PlatformMoneymoremore","Action","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		String loanJsonList = null;
		try {
			loanJsonList=URLDecoder.decode(params.get("LoanJsonList"),"UTF-8");
			params.put("LoanJsonList", loanJsonList);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		thirdPaySupportService.checkRollBack(params, signStrs);
		List<Object> loanJsons=Common.JSONDecodeList(loanJsonList, LoanJson.class);
		String pid = params.get("Remark1");
		req.setAttribute("pid", pid);
		LoanJson loanJson=(LoanJson)(loanJsons.get(0));
		Integer cashStreamId = Integer.parseInt(StringUtil.checkNullAndTrim(CASHSTREAMID, loanJson.getOrderNo()));
		String loanNo=loanJson.getLoanNo();
		CashStream cashStream = cashStreamDao.find(cashStreamId);
		if(cashStream.getState()==CashStream.STATE_SUCCESS)
		{
			log.debug("重复的回复");
			return;
		}
		
		try {
			submitService.confirmPurchase(cashStream.getSubmitId());
			cashStreamDao.updateLoanNo(cashStreamId, loanNo,null);
			accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
		} catch (IllegalConvertException e) {
			log.error(e.getMessage());
		}
	}
	
	
	
	
	

	@RequestMapping(value = { "/account/buy/response" })
	public void completeBuy(HttpServletRequest req, HttpServletResponse resp) {
		Map<String,String> params=getAllParams(req);
		String message=null;
		try {
			completeBuyProcessor(req, resp);
		} catch (SignatureException e) {
//			e.printStackTrace();
			log.error(e.getMessage());
			message=e.getMessage();
		} catch (ResultCodeException e) {
//			e.printStackTrace();
			log.error(e.getMessage());
			message=e.getMessage();
		} catch (Exception e){
//			e.printStackTrace();
			log.error(e.getMessage());
			message = e.getMessage();
		}
		String pid=(String) req.getAttribute("pid");
		if(!StringUtil.isEmpty(message)){
			writeMsg(resp,message,"/myaccount.html");
			return;
		}
		if (!StringUtil.isEmpty(pid))
		{
			writeMsg(resp,null,"/buyresponse.html?pid="+pid);
		}
		else {
			writeMsg(resp,message,"/myaccount.html?fid=submit&sid=submit-all");
		}
	}
	@RequestMapping(value = { "/account/buy/response/bg" })
	public void completeBuyBg(HttpServletRequest req, HttpServletResponse resp) {
		try {
			try{
				Thread.sleep(200);
				}catch(InterruptedException e){
					
				}
			completeBuyProcessor(req, resp);
		} catch (SignatureException e) {
			log.error(e.getMessage());
//			e.printStackTrace();
			return;
		} catch (ResultCodeException e) {
			log.error(e.getMessage());
//			e.printStackTrace();
			return;
		} catch (Exception e){
			log.error(e.getMessage());
//			e.printStackTrace();
			return;
		}
		writeSuccess(resp);
	}
	private void completeBuyProcessor(HttpServletRequest req,HttpServletResponse resp) throws SignatureException, ResultCodeException, Exception
	{
		log.debug("购买回调:"+req.getRequestURI());
		Map<String,String> params=getAllParams(req);
		String[] signStrs={"LoanJsonList","PlatformMoneymoremore","Action","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		String loanJsonList = null;
		try {
			loanJsonList=URLDecoder.decode(params.get("LoanJsonList"),"UTF-8");
			params.put("LoanJsonList", loanJsonList);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		thirdPaySupportService.checkRollBack(params, signStrs);
		List<Object> loanJsons=Common.JSONDecodeList(loanJsonList, LoanJson.class);
		String pid = params.get("Remark1");
		req.setAttribute("pid", pid);
		LoanJson loanJson=(LoanJson)(loanJsons.get(0));
		Integer cashStreamId = Integer.parseInt(StringUtil.checkNullAndTrim(CASHSTREAMID, loanJson.getOrderNo()));
		String loanNo=loanJson.getLoanNo();
		CashStream cashStream = cashStreamDao.find(cashStreamId);
		if(cashStream.getState()==CashStream.STATE_SUCCESS)
		{
			log.debug("重复的回复");
			return;
		}
		
		if("支付超时".equals(cashStream.getDescription())){
			log.debug("支付超时的退款操作已经执行过，避免重复执行！");
			return;
		}
		
		Submit submit = submitService.find(cashStream.getSubmitId());
		if(submit.getState()==Submit.STATE_UNSUBSCRIBE){
			
			List<String> lns = new ArrayList<String>();
			lns.add(loanNo);
			try{
				thirdPaySupportNewService.justAuditBuy(lns, ThirdPartyState.THIRD_AUDITTYPE_RETURN);
				cashStreamDao.updateDescription(cashStreamId, "支付超时");
			}catch(Exception e){
				throw new Exception(e.getMessage());
			}
			throw new Exception("支付超时");
		}
		
		try {
			submitService.confirmBuy(cashStream.getSubmitId());
			cashStreamDao.updateLoanNo(cashStreamId, loanNo,null);
			accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
		} catch (IllegalConvertException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = { "/account/buyaudit/response/bg" })
	public void checkBuyBg(HttpServletRequest req,HttpServletResponse resp)
	{
//		try {
//			log.debug("购买审核确认回调:"+req.getRequestURI());
			Map<String,String> params=getAllParams(req);
//			thirdPaySupportNewService.auditBuyProcessor(params);;
//		} catch (SignatureException e) {
//			log.info("购买审核确认回调执行有问题："+e.getMessage());
//			return;
//		} catch (ResultCodeException e) {
//			log.info("购买审核确认回调执行有问题："+e.getMessage());
//			return;
//		} catch (AlreadyDoneException e){
//			log.info("购买审核确认回调重复执行："+e.getMessage());
//		} catch(Exception e){
//			log.info("购买审核确认回调执行有问题："+e.getMessage());
//			return;
//		}
		log.info("接受了一次购买审核后台返回调用，参数如下，目前动作为忽略");
		log.info(params);
		writeSuccess(resp);
	}
	
	@RequestMapping(value = { "/account/repayaudit/response/bg" })
	public void checkRepayBg(HttpServletRequest req,HttpServletResponse resp)
	{
//		try {
//			log.debug("还款审核确认回调:"+req.getRequestURI());
			Map<String,String> params=getAllParams(req);
//			thirdPaySupportNewService.auditRepayProcessor(params);;
//		} catch (SignatureException e) {
//			log.info("还款审核确认回调执行有问题："+e.getMessage());
//			return;
//		} catch (ResultCodeException e) {
//			log.info("还款审核确认回调执行有问题："+e.getMessage());
//			return;
//		} catch (AlreadyDoneException e){
//			log.info("还款审核确认回调重复执行："+e.getMessage());
//		} catch(Exception e){
//			log.info("还款审核确认回调执行有问题："+e.getMessage());
//			return;
//		}
			
			log.info("接受了一次还款审核后台返回调用，参数如下，目前动作为忽略");
			log.info(params);
		writeSuccess(resp);
	}
	
	@RequestMapping(value = {"/account/repay/response/bg"})
	public void repayBg(HttpServletRequest req,HttpServletResponse resp){
//		try {
//			log.debug("还款申请确认回调:"+req.getRequestURI());
			Map<String,String> params=getAllParams(req);
//			thirdPaySupportNewService.repayApplyProcessor(params);
//		} catch (SignatureException e) {
//			log.info("还款申请确认回调执行有问题："+e.getMessage());
//			return;
//		} catch (ResultCodeException e) {
//			log.info("还款申请确认回调执行有问题："+e.getMessage());
//			return;
//		} catch (AlreadyDoneException e){
//			log.info("还款申请确认回调重复执行："+e.getMessage());
//		} catch(Exception e){
//			log.info("还款申请确认回调执行有问题："+e.getMessage());
//			return;
//		}
			
		log.info("接受了一次申请还款后台返回调用，参数如下，目前动作为忽略");
		log.info(params);
		writeSuccess(resp);
	}
	
	@RequestMapping(value = { "/account/cardBinding/response" })
	public void completeCardBinding(HttpServletRequest req, HttpServletResponse resp) {
		String message=null;
		try {
			completeCardBindingProcessor(req, resp);
		} catch (SignatureException e) {
			e.printStackTrace();
			message=e.getMessage();
		} catch (ResultCodeException e) {
			e.printStackTrace();
			message=e.getMessage();
		}
		//重定向到指定页面
		writeMsg(resp,message,"/myaccount.html?fid=mycenter");
	}

	@RequestMapping(value = { "/account/cardBinding/response/bg" })
	public void completeCardBindingBg(HttpServletRequest req, HttpServletResponse resp) {
		try{
			Thread.sleep(200);
			}catch(InterruptedException e){
				
			}
		try {
			completeCardBindingProcessor(req, resp);
		} catch (SignatureException e) {
			e.printStackTrace();
			return;
		} catch (ResultCodeException e) {
			e.printStackTrace();
			return;
		}
		writeSuccess(resp);
	}
	private void completeCardBindingProcessor(HttpServletRequest req,HttpServletResponse resp) throws SignatureException, ResultCodeException
	{
		log.debug("购买回调:"+req.getRequestURI());
		Map<String,String> params=getAllParams(req);
		String[] signStrs={"MoneymoremoreId","PlatformMoneymoremore","Action","CardType","BankCode","CardNo","BranchBankName",
				"Province","City","WithholdBeginDate","WithholdEndDate","SingleWithholdLimit","TotalWithholdLimit+ "
						+ "RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		RsaHelper rsa = RsaHelper.getInstance();
		String cardNo=rsa.decryptData(params.get("CardNo"), innerThirdPaySupportService.getPrivateKey());
		params.put("CardNo", cardNo);
		thirdPaySupportService.checkRollBack(params, signStrs);
		String moneymoremoreId=params.get("MoneymoremoreId");
		Lender lender=lenderDao.findByThirdPartyAccount(moneymoremoreId);
		if(lender!=null)
		{
			if(lender.getCardBindingId()!=null)
			{
				CardBinding orignal=cardBindingDao.find(lender.getCardBindingId());
				if(orignal!=null&&orignal.getCardNo().equals(cardNo))
				{
					//已绑定
					lender=lenderService.getCurrentUser();
					if(lender!=null){
						lender.setCardBindingId(orignal.getId());
						lender.setCardBinding(orignal);
					}
					return;
				}
			}
			CardBinding cardBinding=new CardBinding();
			cardBinding.setBankCode(params.get("BankCode"));
			cardBinding.setBranchBankName(params.get("BranchBankName"));
			cardBinding.setCardNo(cardNo);
			cardBinding.setCardType(Integer.parseInt(params.get("CardType")));
			cardBinding.setCity(params.get("City"));
			cardBinding.setProvince(params.get("Province"));
			cardBindingDao.create(cardBinding);
			lenderService.bindCard(lender.getId(), cardBinding.getId());
			//加一分钱
			Integer cashStreamId=accountService.rechargeLenderAccount(lender.getAccountId(), new BigDecimal(0.01), "快捷支付充值");
			try {
				accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
			} catch (IllegalConvertException e) {
				e.printStackTrace();
			}
		}
		else 
		{
			Borrower borrower=borrowerDao.findByThirdPartyAccount(moneymoremoreId);
			if(borrower!=null)
			{
				if(borrower.getCardBindingId()!=null)
				{
					CardBinding orignal=cardBindingDao.find(borrower.getCardBindingId());
					if(orignal!=null&&orignal.getCardNo().equals(cardNo))
					{
						//已绑定
						borrower=borrowerService.getCurrentUser();
						if(borrower!=null)
						{
							borrower.setCardBindingId(orignal.getId());
							borrower.setCardBinding(orignal);
						}
						return;
					}
				}
				CardBinding cardBinding=new CardBinding();
				cardBinding.setBankCode(params.get("BankCode"));
				cardBinding.setBranchBankName(params.get("BranchBankName"));
				cardBinding.setCardNo(cardNo);
				cardBinding.setCardType(Integer.parseInt(params.get("CardType")));
				cardBinding.setCity(params.get("City"));
				cardBinding.setProvince(params.get("Province"));
				cardBindingDao.create(cardBinding);
				borrowerService.bindCard(borrower.getId(), cardBinding);
				//加一分钱
				Integer cashStreamId=accountService.rechargeBorrowerAccount(borrower.getAccountId(), new BigDecimal(0.01), "快捷支付充值");
				try {
					accountService.changeCashStreamState(cashStreamId, CashStream.STATE_SUCCESS);
				} catch (IllegalConvertException e) {
					e.printStackTrace();
				}
			}
		}
	}
	@RequestMapping(value = { "/account/authorize/response" })
	public void completeAuthorize(HttpServletRequest req,HttpServletResponse resp)
	{
		String message=null;
		try {
			completeAuthorizeProcessor(req,resp);
		} catch (SignatureException e) {
			e.printStackTrace();
			message=e.getMessage();
		} catch (ResultCodeException e) {
			e.printStackTrace();
			message=e.getMessage();
		}
		writeMsg(resp,message,"/myaccount.html?fid=mycenter");
	}
	@RequestMapping(value = { "/account/authorize/response/bg" })
	public void completeAuthorizeBg(HttpServletRequest req,HttpServletResponse resp)
	{
		try {
			completeAuthorizeProcessor(req,resp);
		} catch (SignatureException e) {
			e.printStackTrace();
			return;
		} catch (ResultCodeException e) {
			e.printStackTrace();
			return;
		}
		writeSuccess(resp);
	}
	
	
	@RequestMapping(value = { "/account/lenderauthorize/response" })
	public void completeLenderAuthorize(HttpServletRequest req,HttpServletResponse resp)
	{
		String message=null;
		try {
			completeLenderAuthorizeProcessor(req,resp);
		} catch (SignatureException e) {
			e.printStackTrace();
			message=e.getMessage();
		} catch (ResultCodeException e) {
			e.printStackTrace();
			message=e.getMessage();
		}
		writeMsg(resp,message,"/purchaseraccount.html");
	}
	
	@RequestMapping(value = { "/account/lenderauthorize/response/bg" })
	public void completeLenderAuthorizeBg(HttpServletRequest req,HttpServletResponse resp)
	{
		try {
			completeLenderAuthorizeProcessor(req,resp);
		} catch (SignatureException e) {
			e.printStackTrace();
			return;
		} catch (ResultCodeException e) {
			e.printStackTrace();
			return;
		}
		writeSuccess(resp);
	}
	
	/**
	 * 回购企业代持账户授权回调操作流程
	 * 
	 * 
	 * */
	private void completeLenderAuthorizeProcessor(HttpServletRequest req,HttpServletResponse resp) throws SignatureException, ResultCodeException
	{
		log.debug("授权回调:"+req.getRequestURI());
		Map<String,String> params=getAllParams(req);
		String[] signStrs={"MoneymoremoreId","PlatformMoneymoremore","AuthorizeTypeOpen","AuthorizeTypeClose","AuthorizeType","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		thirdPaySupportService.checkRollBack(params, signStrs);
		String authorizeTypeOpen=params.get("AuthorizeTypeOpen");
		String authorizeTypeClose=params.get("AuthorizeTypeClose");
		String moneymoremoreId=params.get("MoneymoremoreId");
		Lender lender=lenderDao.findByThirdPartyAccount(moneymoremoreId);
		
		int originalAuthorizeTypeOpen=lender.getAuthorizeTypeOpen();
		if(!StringUtil.isEmpty(authorizeTypeOpen))
		{
			String[] strs=authorizeTypeOpen.split(",");
			for(String str:strs)
			{
				if(str.equals("3"))
					originalAuthorizeTypeOpen=originalAuthorizeTypeOpen|Borrower.AUTHORIZETYPEOPEN_SECORD;
				else
					originalAuthorizeTypeOpen=originalAuthorizeTypeOpen|Integer.parseInt(str);
			}
		}
		if(!StringUtil.isEmpty(authorizeTypeClose))
		{
			String[] strs=authorizeTypeOpen.split(",");
			for(String str:strs)
			{
				if(str.equals("3"))
					originalAuthorizeTypeOpen=originalAuthorizeTypeOpen-(originalAuthorizeTypeOpen&Borrower.AUTHORIZETYPEOPEN_SECORD);
				else
					originalAuthorizeTypeOpen=originalAuthorizeTypeOpen-(originalAuthorizeTypeOpen&Integer.parseInt(str));
			}
		}
		lenderDao.updateAuthorizeTypeOpen(lender.getId(), originalAuthorizeTypeOpen);
		Borrower borrower=borrowerService.getCurrentUser();
		if(borrower!=null)
		{
			lender.setAuthorizeTypeOpen(originalAuthorizeTypeOpen);
			borrower.setLender(lender);
		}
	}
	
	
	
	
	private void completeAuthorizeProcessor(HttpServletRequest req,HttpServletResponse resp) throws SignatureException, ResultCodeException
	{
		log.debug("授权回调:"+req.getRequestURI());
		Map<String,String> params=getAllParams(req);
		String[] signStrs={"MoneymoremoreId","PlatformMoneymoremore","AuthorizeTypeOpen","AuthorizeTypeClose","AuthorizeType","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		thirdPaySupportService.checkRollBack(params, signStrs);
		String authorizeTypeOpen=params.get("AuthorizeTypeOpen");
		String authorizeTypeClose=params.get("AuthorizeTypeClose");
		String moneymoremoreId=params.get("MoneymoremoreId");
		Borrower borrower=borrowerDao.findByThirdPartyAccount(moneymoremoreId);
		int originalAuthorizeTypeOpen=borrower.getAuthorizeTypeOpen();
		if(!StringUtil.isEmpty(authorizeTypeOpen))
		{
			String[] strs=authorizeTypeOpen.split(",");
			for(String str:strs)
			{
				if(str.equals("3"))
					originalAuthorizeTypeOpen=originalAuthorizeTypeOpen|Borrower.AUTHORIZETYPEOPEN_SECORD;
				else
					originalAuthorizeTypeOpen=originalAuthorizeTypeOpen|Integer.parseInt(str);
			}
		}
		if(!StringUtil.isEmpty(authorizeTypeClose))
		{
			String[] strs=authorizeTypeOpen.split(",");
			for(String str:strs)
			{
				if(str.equals("3"))
					originalAuthorizeTypeOpen=originalAuthorizeTypeOpen-(originalAuthorizeTypeOpen&Borrower.AUTHORIZETYPEOPEN_SECORD);
				else
					originalAuthorizeTypeOpen=originalAuthorizeTypeOpen-(originalAuthorizeTypeOpen&Integer.parseInt(str));
			}
		}
		borrowerDao.updateAuthorizeTypeOpen(borrower.getId(), originalAuthorizeTypeOpen);
		borrower=borrowerService.getCurrentUser();
		if(borrower!=null)
			borrower.setAuthorizeTypeOpen(originalAuthorizeTypeOpen);
	}
	private void writeMsg(HttpServletResponse resp, String message,String redirct) {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append("<head><script>");
		if(!StringUtil.isEmpty(message))
		{
			sBuilder.append("alert('").append(message).append("');");
		}
		sBuilder.append("window.location.href='").append(redirct).append("'</script></head>");
		PrintWriter writer = null;
		try {
			writer = resp.getWriter();
			writer.write(sBuilder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}

	}

	private Map<String, String> getAllParams(HttpServletRequest req) {
		Map<String, String> params = new HashMap<String, String>();
		try {
			req.setCharacterEncoding("UTF-8");
			Map m = req.getParameterMap();
			Iterator it = m.keySet().iterator();
			while (it.hasNext()) {
				String key = String.valueOf(it.next());
				String[] values = (String[]) (m.get(key));
				String value=values[0];
				log.info(key + "=" + value);
				params.put(key, value);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(),e);
		}
		return params;
	}
	private void writeSuccess(HttpServletResponse resp)
	{
		resp.setCharacterEncoding("UTF-8");
		resp.setStatus(200);
		PrintWriter writer = null;
		try {
			writer = resp.getWriter();
			writer.write("SUCCESS");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}
}
