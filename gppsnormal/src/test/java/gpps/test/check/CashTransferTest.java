package gpps.test.check;

import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.IThirdPaySupportNewService;
import gpps.service.thirdpay.ITransferApplyService;
import gpps.service.thirdpay.LoanFromTP;
import gpps.service.thirdpay.LoanHelper;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.ThirdPartyAssistent;
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

import com.google.gson.Gson;

public class CashTransferTest extends ThirdPartyAssistent{
	public static void main(String args[]) throws Exception{
		
		ITransferApplyService transferApplyService = context.getBean(ITransferApplyService.class);
		IThirdPaySupportNewService payNewService = context.getBean(IThirdPaySupportNewService.class);
		
		List<LoanJson> loanJsons = new ArrayList<LoanJson>();
		
//		LoanJson loadJson2=new LoanJson();
//		loadJson2.setLoanOutMoneymoremore("m21219");
//		loadJson2.setLoanInMoneymoremore("m21002");
//		loadJson2.setOrderNo("9015");
//		loadJson2.setBatchNo("20");
//		loadJson2.setAmount("100");
//		loanJsons.add(loadJson2);
//		
//		LoanJson loadJson3=new LoanJson();
//		loadJson3.setLoanOutMoneymoremore("m21219");
//		loadJson3.setLoanInMoneymoremore("m21002");
//		loadJson3.setOrderNo("9016");
//		loadJson3.setBatchNo("20");
//		loadJson3.setAmount("200");
//		loanJsons.add(loadJson3);

		
		LoanJson loadJson=new LoanJson();
		loadJson.setLoanOutMoneymoremore("m21219");
		loadJson.setLoanInMoneymoremore("m21002");
		loadJson.setOrderNo("9019");
		loadJson.setBatchNo("20");
		loadJson.setAmount("300");
		loanJsons.add(loadJson);
		
		payNewService.repayApply(loanJsons, null);
//		transferApplyService.repayApply(loanJsons, null);
		
//		transfer(loanJsons, "2", "2", "2");
		System.exit(0);
	}
	
	//批量自动需要审核转账只有还款一种情况
	public static void transfer(List<LoanJson> loanJsons, String transferAction, String action, String transferType) throws SignatureException, ResultCodeException, Exception{
		
		if(loanJsons==null || loanJsons.isEmpty()){
			throw new Exception("无效的转账列表！");
		}
		
		List<LoanFromTP> result = new ArrayList<LoanFromTP>();
		
		Transfer transfer = new Transfer();
		transfer.setPlatformMoneymoremore(platformMoneymoremore);
		transfer.setTransferAction(transferAction);
		transfer.setAction(action);
		transfer.setTransferType(transferType);
		
		//转账设置为需要审核
		transfer.setNeedAudit(null);
		
		//TODO:非手动转账，不需要returnURL,只需要提供后台处理页面
		transfer.setNotifyURL("http://162.105.139.39:8080/account/repay/response/bg");
		
		//签名
		transfer.setLoanJsonList(Common.JSONEncode(loanJsons));
		transfer.setSignInfo(transfer.getSign(privateKey));
		
		//将转账列表编码
		try {
			transfer.setLoanJsonList(URLEncoder.encode(transfer.getLoanJsonList(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Map<String,String> params=transfer.getParams();
		
		
		String baseUrl = getBaseUrl(ACTION_TRANSFER);
		
		//将处理好的参数post到第三方，并接受其马上返回的参数
		String body=httpClientService.post(baseUrl, params);
		
		//校验返回结果签名，并解析返回参数
		result = handleReturnParams(body);
		
		//根据返回参数处理平台相关信息，维护与第三方的一致性
		Map<String, String> messageToSend = null;
		messageToSend = returnRepayApplyProcessor(result);
		
		//如果messageToSend不为空，说明有需要发送短信和站内信的内容
		if(messageToSend!=null && !messageToSend.isEmpty()){
			messageService.sendMessage(messageToSend);
		}
	}
	
	private static Map<String, String> returnRepayApplyProcessor(List<LoanFromTP> loans){
		
		//TODO: 首先判断是否已经执行了相应的操作，如果是的话，直接返回null
		
		Map<String, String> result = new HashMap<String, String>();
		
		return result;
	}
	
	public static List<LoanFromTP> handleReturnParams(String retJson) throws AlreadyDoneException, ResultCodeException, SignatureException, Exception
	{
		Gson gson = new Gson();
		
		//自动、需要审核转账(Action=2  NeedAudit=null)，只包含一个json，记录了具体的转账信息
		Map returnParams=gson.fromJson(retJson, Map.class);
		
		try{
			checkTransferReturnParams(returnParams);
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
	
	public List<LoanFromTP> confirm(List<String> orderNos) throws Exception{
		List<LoanFromTP> result = new ArrayList<LoanFromTP>();
		
		//对于乾多多已经执行的情况，需要根据cashstreamId(orderNo)逐个的对账，得到钱多多的loanNo
		for(String orderNo : orderNos){
			LoanFromTP loan = CashStreamView.viewByOrderNo(orderNo);
			result.add(loan);
		}
		System.out.println("本批次转账已经执行成功！重复提交了");
		return result;
	}
	
	
	public static void checkTransferReturnParams(Map<String,String> params) throws AlreadyDoneException, ResultCodeException, SignatureException{
		String[] signStrs={"LoanJsonList","PlatformMoneymoremore","Action","RandomTimeStamp","Remark1","Remark2","Remark3","ResultCode"};
		String loanJsonList = null;
		try {
			loanJsonList=URLDecoder.decode(params.get("LoanJsonList"),"UTF-8");
			params.put("LoanJsonList", loanJsonList);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		checkReturnParam(params,signStrs);
	}
}
