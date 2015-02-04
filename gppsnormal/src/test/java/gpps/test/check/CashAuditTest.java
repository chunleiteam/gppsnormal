package gpps.test.check;

import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.IAuditRepayService;
import gpps.service.thirdpay.IThirdPaySupportNewService;
import gpps.service.thirdpay.LoanFromTP;
import gpps.service.thirdpay.TransferFromTP;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.ThirdPartyAssistent;
import gpps.service.thirdpay.ThirdPartyState;
import gpps.service.thirdpay.Transfer.LoanJson;
import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;


public class CashAuditTest extends ThirdPartyAssistent {
	
	public static void main(String args[]) throws Exception{
		IAuditRepayService auditRepayService = context.getBean(IAuditRepayService.class);
		IThirdPaySupportNewService payNewService = context.getBean(IThirdPaySupportNewService.class);
		
		List<String> loanNos = new ArrayList<String>();
		loanNos.add("LN13190221502030108226252330");
		loanNos.add("LN13190221502030108227966432");
		loanNos.add("LN13190221502030108229536501");
		
		payNewService.auditBuy(loanNos, ThirdPartyState.THIRD_AUDITTYPE_RETURN);
//		auditRepayService.auditRepay(loanNos, ThirdPartyState.THIRD_AUDITTYPE_RETURN);
		
//		checkBuy(loanNos, ThirdPartyState.THIRD_AUDITTYPE_RETURN);
		System.exit(0);
	}
	
	
	//提交第三方审核购买列表
	public static void checkBuy(List<String> loanNos,int auditType) throws SignatureException, ResultCodeException, Exception{
		if(loanNos==null||loanNos.size()==0)
			throw new Exception("无效的审核列表！");
		
		String baseUrl=getBaseUrl(ACTION_CHECK);
		StringBuilder loanNoSBuilder=new StringBuilder();
		Map<String,String> params=new HashMap<String, String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
		params.put("AuditType", String.valueOf(auditType));
		params.put("ReturnURL", "http://www.aaccbb.com/account/checkBuy/response/bg");
		params.put("NotifyURL", params.get("ReturnURL"));
		for(int i=0;i<loanNos.size();i++)
		{
			if(loanNoSBuilder.length()!=0)
				loanNoSBuilder.append(",");
			loanNoSBuilder.append(loanNos.get(i));
			params.put("LoanNoList", loanNoSBuilder.toString());
		}
		
		sendCheck(params,baseUrl, ThirdPartyState.THIRD_TRANSFERACTION_BUY);
	}
	
	//提交第三方审核还款列表
	public static void checkRepay(List<String> loanNos,int auditType) throws SignatureException, ResultCodeException, Exception{
		if(loanNos==null||loanNos.size()==0)
			throw new Exception("无效的审核列表！");
		
		String baseUrl=getBaseUrl(ACTION_CHECK);
		StringBuilder loanNoSBuilder=new StringBuilder();
		Map<String,String> params=new HashMap<String, String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
		params.put("AuditType", String.valueOf(auditType));
		params.put("ReturnURL", "/account/checkRepay/response/bg");
		params.put("NotifyURL", params.get("ReturnURL"));
		for(int i=0;i<loanNos.size();i++)
		{
			if(loanNoSBuilder.length()!=0)
				loanNoSBuilder.append(",");
			loanNoSBuilder.append(loanNos.get(i));
			params.put("LoanNoList", loanNoSBuilder.toString());
		}
		
		sendCheck(params,baseUrl, ThirdPartyState.THIRD_TRANSFERACTION_REPAY);
	}
	
	
	//对于一批审核的账单，确认其是否全部已经审核完毕
	private static List<LoanFromTP> confirm(List<String> loanNos) throws Exception{
		List<LoanFromTP> result = new ArrayList<LoanFromTP>();
		for(String loanNo : loanNos){
			
			//只要有一个根据loanNo找不到对应信息（也就是尚未审核）,则抛出异常，整体执行不成功！
			LoanFromTP loan = CashStreamView.viewByLoanNo(loanNo);
			result.add(loan);
		}
		return result;
	}
	
	private static void sendCheck(Map<String,String> params,String baseUrl, String transferAction) throws SignatureException, ResultCodeException, Exception
	{
		//签名
		String signInfo = signForAudit(params, privateKey);
		params.put("SignInfo", signInfo);
		
		//将处理好的参数post到第三方，并接受其马上返回的参数
		String body=httpClientService.post(baseUrl, params);
		
		
		//校验返回结果签名，并解析返回参数
		List<String> loanNos = handleReturnParams(body);
		
		//根据返回参数处理平台相关信息，维护与第三方的一致性
		
		Map<String, String> messageToSend = null;
		if(ThirdPartyState.THIRD_TRANSFERACTION_BUY.equals(transferAction))
		{
			//如果是投标，就调用投标的返回处理程序
			messageToSend = returnBuyProcessor(loanNos);
		}else if(ThirdPartyState.THIRD_TRANSFERACTION_REPAY.equals(transferAction)){
			//如果是还款，就调用还款的返回处理程序
			messageToSend = returnRepayProcessor(loanNos);
		}
		
		//如果messageToSend不为空，说明有需要发送短信和站内信的内容
		if(messageToSend!=null && !messageToSend.isEmpty()){
//			messageService.sendMessage(messageToSend);
			System.out.println(messageToSend);
		}
	}
	
	private static Map<String, String> returnBuyProcessor(List<String> loanNos){
		
		//TODO: 首先判断是否已经执行了相应的操作，如果是的话，直接返回null
		
		Map<String, String> result = new HashMap<String, String>();
		
		for(String loanNo : loanNos){
			result.put(loanNo, "购买审核完毕");
		}
		
		return result;
	}
	
	private static Map<String, String> returnRepayProcessor(List<String> loanNos){
		
		//TODO: 首先判断是否已经执行了相应的操作，如果是的话，直接返回null
		
		Map<String, String> result = new HashMap<String, String>();
		
		for(String loanNo : loanNos){
			result.put(loanNo, "还款审核完毕");
		}
		
		return result;
	}
	
	
	
	public static String signForAudit(Map<String,String> params, String privateKey){
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
		return signInfo;
	}
	
	public static List<String> handleReturnParams(String retJson) throws AlreadyDoneException, ResultCodeException, SignatureException{
		List<String> result = new ArrayList<String>();
		Gson gson = new Gson();
		Map<String,String> returnParams=gson.fromJson(retJson, Map.class);
		try{
		checkTransferReturnParams(returnParams);
		}catch(SignatureException e){
			throw e;
		}catch(ResultCodeException e){
			throw e;
		}catch(AlreadyDoneException e){
			throw e;
		}
		
		if(!StringUtil.isEmpty(returnParams.get("LoanNoList")))
		{
			String[] loanNoList=returnParams.get("LoanNoList").split(",");
			for(String loanNo:loanNoList)
			{
				result.add(loanNo);
			}
		}
		
		return result;
	}
	
	//校验审核转账时返回的参数
	public static void checkTransferReturnParams(Map<String,String> params) throws AlreadyDoneException, ResultCodeException, SignatureException{
		String[] signStrs={"LoanNoList","LoanNoListFail","PlatformMoneymoremore","AuditType","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		checkReturnParam(params,signStrs);
	}
}
