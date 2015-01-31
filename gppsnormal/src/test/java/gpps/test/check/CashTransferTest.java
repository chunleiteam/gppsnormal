package gpps.test.check;

import gpps.service.thirdpay.AlreadyDoneException;
import gpps.service.thirdpay.ResultCodeException;
import gpps.service.thirdpay.Transfer.LoanJson;
import gpps.tools.Common;
import gpps.tools.RsaHelper;
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
		List<LoanJson> loanJsons = new ArrayList<LoanJson>();
		
		LoanJson loadJson2=new LoanJson();
		loadJson2.setLoanOutMoneymoremore("m21219");
		loadJson2.setLoanInMoneymoremore("m21002");
		loadJson2.setOrderNo("9005");
		loadJson2.setBatchNo("20");
		loadJson2.setAmount("100");
		loanJsons.add(loadJson2);
		
		LoanJson loadJson3=new LoanJson();
		loadJson3.setLoanOutMoneymoremore("m21219");
		loadJson3.setLoanInMoneymoremore("m21002");
		loadJson3.setOrderNo("9006");
		loadJson3.setBatchNo("20");
		loadJson3.setAmount("100");
		loanJsons.add(loadJson3);
		
		LoanJson loadJson=new LoanJson();
		loadJson.setLoanOutMoneymoremore("m21219");
		loadJson.setLoanInMoneymoremore("m21002");
		loadJson.setOrderNo("9007");
		loadJson.setBatchNo("20");
		loadJson.setAmount("2000");
		loanJsons.add(loadJson);
		
		
		List<LoanFromTP> loans = transfer(loanJsons, "2", "2", "2");
		for(LoanFromTP loan : loans)
		System.out.println(loan.getLoanNo());
		
		System.exit(0);
	}
	
	
	public static List<LoanFromTP> transfer(List<LoanJson> loanJsons, String transferAction, String action, String transferType) throws SignatureException, ResultCodeException, Exception{
		
		if(loanJsons==null || loanJsons.isEmpty()){
			throw new Exception("无效的转账列表！");
		}
		
		List<LoanFromTP> result = new ArrayList<LoanFromTP>();
		
		String baseUrl = getBaseUrl(ACTION_TRANSFER);
		
		Map<String,String> params=new HashMap<String,String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
		params.put("TransferAction", transferAction);
		params.put("Action", action);
		params.put("TransferType", transferType);
		
		//转账设置为需要审核
		params.put("NeedAudit", null);
		
		//由于批量转账是由任务来保证其完成的，因此无需后台返回机制。
		//TODO:该后台返回只是输出一个成功码便可
		params.put("NotifyURL", "http://localhost:8080/account/repay/response/bg");
		
		
		String LoanJsonList=Common.JSONEncode(loanJsons);
		params.put("LoanJsonList", LoanJsonList);
		
		
		//利用私有key完成签名
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
		
		//将转账列表字符串进行编码
		try {
			params.put("LoanNoList",URLEncoder.encode(params.get("LoanJsonList"),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String body=httpClientService.post(baseUrl, params);
		Gson gson = new Gson();
		
		//自动、需要审核转账(Action=2  NeedAudit=null)，只包含一个json，记录了具体的转账信息
		Map returnParams=gson.fromJson(body, Map.class);
		
		try{
			checkTransferReturnParams(returnParams);
		}
		catch(SignatureException e){
			throw e;
		}catch(ResultCodeException e){
			throw e;
		}catch(AlreadyDoneException e){
			//对于乾多多已经执行的情况，需要根据cashstreamId(orderNo)逐个的对账，得到钱多多的loanNo
			for(LoanJson lj : loanJsons){
				String orderNo = lj.getOrderNo();
				LoanFromTP loan = CashStreamView.viewByOrderNo(orderNo);
				result.add(loan);
			}
			System.out.println("本批次转账已经执行成功！重复提交了");
			return result;
		}
		
		
		String loanJonListStr=null;
		try{
		loanJonListStr = URLDecoder.decode(StringUtil.strFormat(returnParams.get("LoanJsonList")),"UTF-8");
		}catch(UnsupportedEncodingException e){
			
		}
		
		result = LoanHelper.parseJSON(loanJonListStr);
		
		if(result==null || result.isEmpty()){
			throw new Exception("无效的转账列表！");
		}
		
//		List<Object> rtljs=Common.JSONDecodeList(loanJonListStr, LoanJson.class);
//		if(loanJsons==null||loanJsons.size()==0)
//			return;
		
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
