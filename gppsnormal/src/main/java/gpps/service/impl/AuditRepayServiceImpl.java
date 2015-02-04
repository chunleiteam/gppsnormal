package gpps.service.impl;

import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.IAuditRepayService;
import gpps.service.thirdpay.IHttpClientService;
import gpps.service.thirdpay.IThirdPaySupportService;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.ThirdPartyAssistent;
import gpps.tools.StringUtil;
@Service
public class AuditRepayServiceImpl implements IAuditRepayService {
	@Autowired
	IThirdPaySupportService thirdPayService;
	@Autowired
	IHttpClientService httpClientService;
	@Override
	public void auditRepay(List<String> loanNos, int auditType)
			throws Exception {
		if(loanNos==null||loanNos.size()==0)
			throw new Exception("无效的审核列表！");
		
		String baseUrl=thirdPayService.getBaseUrl(ThirdPartyAssistent.ACTION_CHECK);
		StringBuilder loanNoSBuilder=new StringBuilder();
		Map<String,String> params=new HashMap<String, String>();
		params.put("PlatformMoneymoremore", thirdPayService.getPlatformMoneymoremore());
		params.put("AuditType", String.valueOf(auditType));
		params.put("ReturnURL", "http://www.aaccbb.com/account/checkRepay/response/bg");
		params.put("NotifyURL", params.get("ReturnURL"));
		for(int i=0;i<loanNos.size();i++)
		{
			if(loanNoSBuilder.length()!=0)
				loanNoSBuilder.append(",");
			loanNoSBuilder.append(loanNos.get(i));
		}
		
		params.put("LoanNoList", loanNoSBuilder.toString());
		
		//签名
		String signInfo = ThirdPartyAssistent.signForAudit(params, thirdPayService.getPrivateKey());
		params.put("SignInfo", signInfo);
				
		//将处理好的参数post到第三方，并接受其马上返回的参数
		String body=httpClientService.post(baseUrl, params);
		
		
		//由于还款审核会前后台都返回处理结果，因此能接收到直接返回结果，因此直接就执行相应的后续处理
		auditRepayProcessor(body);

	}

	@Override
	public void auditRepayProcessor(String retJson)
			throws AlreadyDoneException, ResultCodeException,
			SignatureException, Exception {
		//校验返回结果签名，并解析返回参数
				List<String> loanNos = ThirdPartyAssistent.handleAuditReturnParams(retJson);
				
				//根据返回参数处理平台相关信息，维护与第三方的一致性
				
				Map<String, String> messageToSend = repayAuditHandle(loanNos);
				
				
				//如果messageToSend不为空，说明有需要发送短信和站内信的内容
				if(messageToSend!=null && !messageToSend.isEmpty()){
//					messageService.sendMessage(messageToSend);
					System.out.println(messageToSend);
				}

	}
	
	
		//审核提交给第三方后，对第三方返回的执行结果参数的后续处理，并将需要发送的短信信息返回
		private static Map<String, String> repayAuditHandle(List<String> loanNos){
			//TODO: 首先判断是否已经执行了相应的操作，如果是的话，直接返回null
			Map<String, String> result = new HashMap<String, String>();
			for(String loanNo : loanNos){
				result.put(loanNo, "还款审核完毕");
			}
			return result;
		}

}
