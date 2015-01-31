package gpps.test.check;

import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.ResultCodeException;
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
		List<String> loanNos = new ArrayList<String>();
//		loanNos.add("LN13190221501311907231259908");
//		loanNos.add("LN13190221501311850432969129");
//		loanNos.add("LN13190221501311907239999999");
		
		loanNos.add("LN13190221502010134191713137");
		loanNos.add("LN13190221502010134190150964");
		loanNos.add("LN13190221502010134188595868");
		loanNos.add("LN13190221501311907239999999");
		
		List<String> result = check(loanNos, ThirdPartyState.THIRD_AUDITTYPE_RETURN);
		System.out.println(result);
		System.exit(0);
	}
	
	public static List<String> check(List<String> loanNos,int auditType) throws SignatureException, ResultCodeException, Exception{
		if(loanNos==null||loanNos.size()==0)
			throw new Exception("无效的审核列表！");
		
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
			params.put("LoanNoList", loanNoSBuilder.toString());
		}
		
		List<String> result = sendCheck(params,baseUrl,loanNos);
		
		return result;
		
	}
	
	private static List<String> sendCheck(Map<String,String> params,String baseUrl,List<String> loanNos) throws SignatureException, ResultCodeException, Exception
	{
		List<String> result = new ArrayList<String>();
		
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
		try{
		checkTransferReturnParams(returnParams);
		}catch(SignatureException e){
			throw e;
		}catch(ResultCodeException e){
			String resultCode = returnParams.get("ResultCode");
			//当异常代码等于01的时候，有可能是已经审核通过的状况
			if("01".equals(resultCode)){
				for(String loanNo : loanNos){
					LoanFromTP loan = CashStreamView.viewByLoanNo(loanNo);
					result.add(loan.getLoanNo());
				}
				System.out.println("本批次审核已经执行成功！重复提交了");
				return result;
			}else{
				throw e;
			}
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
	
	public static void checkTransferReturnParams(Map<String,String> params) throws AlreadyDoneException, ResultCodeException, SignatureException{
		String[] signStrs={"LoanNoList","LoanNoListFail","PlatformMoneymoremore","AuditType","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		checkReturnParam(params,signStrs);
	}
}
