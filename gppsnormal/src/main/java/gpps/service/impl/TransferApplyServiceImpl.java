package gpps.service.impl;

import gpps.model.PayBack;
import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.IHttpClientService;
import gpps.service.thirdpay.IThirdPaySupportService;
import gpps.service.thirdpay.ITransferApplyService;
import gpps.service.thirdpay.LoanFromTP;
import gpps.service.thirdpay.LoanHelper;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.ThirdPartyAssistent;
import gpps.service.thirdpay.ThirdPartyState;
import gpps.service.thirdpay.Transfer;
import gpps.service.thirdpay.Transfer.LoanJson;
import gpps.service.thirdpay.TransferFromTP;
import gpps.tools.Common;
import gpps.tools.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
@Service
public class TransferApplyServiceImpl implements ITransferApplyService {
	@Autowired
	IThirdPaySupportService thirdPayService;
	@Autowired
	IHttpClientService httpClientService;
	@Override
	public void repayApply(List<LoanJson> loanJsons, PayBack payback)
			throws Exception {
		if(loanJsons==null || loanJsons.isEmpty()){
			throw new Exception("无效的转账列表！");
		}
		
		List<TransferFromTP> result = new ArrayList<TransferFromTP>();
		
		Transfer transfer = new Transfer();
		transfer.setPlatformMoneymoremore(thirdPayService.getPlatformMoneymoremore());
		transfer.setTransferAction(ThirdPartyState.THIRD_TRANSFERACTION_REPAY);
		transfer.setAction(ThirdPartyState.THIRD_ACTION_AUTO);
		transfer.setTransferType(ThirdPartyState.THIRD_TRANSFERTYPE_DIRECT);
		
		//转账设置为需要审核
		transfer.setNeedAudit(null);
		
		//非手动转账，不需要returnURL,只需要提供后台处理页面
		transfer.setNotifyURL("http://www.aaabbc.com/account/repay/response/bg");
		
		//签名
		transfer.setLoanJsonList(Common.JSONEncode(loanJsons));
		transfer.setSignInfo(transfer.getSign(thirdPayService.getPrivateKey()));
		
		//将转账列表编码
		try {
			transfer.setLoanJsonList(URLEncoder.encode(transfer.getLoanJsonList(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Map<String,String> params=transfer.getParams();
		
		
		String baseUrl = thirdPayService.getBaseUrl(ThirdPartyAssistent.ACTION_TRANSFER);
		
		//将处理好的参数post到第三方，并接受其马上返回的参数
		String body=httpClientService.post(baseUrl, params);
		
		//由于申请还款（自动）会前后台都返回处理结果，因此能接收到直接返回结果，因此直接就执行相应的后续处理
		repayApplyProcessor(body);
	}

	@Override
	public void repayApplyProcessor(String retJson)
			throws AlreadyDoneException, ResultCodeException,
			SignatureException, Exception {
			//校验返回结果签名，并解析返回参数
				List<LoanFromTP> result = handleReturnParams(retJson);
				
				//根据返回参数处理平台相关信息，维护与第三方的一致性
				Map<String, String> messageToSend = null;
				messageToSend = repayApplyHandle(result);
				
				//如果messageToSend不为空，说明有需要发送短信和站内信的内容
				if(messageToSend!=null && !messageToSend.isEmpty()){
//					messageService.sendMessage(messageToSend);
					System.out.println(messageToSend);
				}

	}
	
	
	public static List<LoanFromTP> handleReturnParams(String retJson) throws AlreadyDoneException, ResultCodeException, SignatureException, Exception
	{
		Gson gson = new Gson();
		
		//自动、需要审核转账(Action=2  NeedAudit=null)，只包含一个json，记录了具体的转账信息
		Map returnParams=gson.fromJson(retJson, Map.class);
		
		try{
			ThirdPartyAssistent.checkTransferReturnParams(returnParams);
		}
		catch(SignatureException e){
			throw e;
		}catch(ResultCodeException e){
			throw e;
		}catch(AlreadyDoneException e){
			throw e;
		}
		
		
		String loanJonListStr=null;
		try{
		loanJonListStr = URLDecoder.decode(StringUtil.strFormat(returnParams.get("LoanJsonList")),"UTF-8");
		}catch(UnsupportedEncodingException e){
			
		}
		
		List<LoanFromTP> result = LoanHelper.parseJSON(loanJonListStr, null);
		
		if(result==null || result.isEmpty()){
			throw new Exception("无效的转账列表！");
		}
		
		return result;
	}
	
	
	// 审核提交给第三方后，对第三方返回的执行结果参数的后续处理，并将需要发送的短信信息返回
	private static Map<String, String> repayApplyHandle(List<LoanFromTP> loanNos) {
		// TODO: 首先判断是否已经执行了相应的操作，如果是的话，直接返回null
		Map<String, String> result = new HashMap<String, String>();
		for (LoanFromTP loanNo : loanNos) {
			result.put(loanNo.getLoanNo(), "还款申请完毕");
		}
		return result;
	}

}
