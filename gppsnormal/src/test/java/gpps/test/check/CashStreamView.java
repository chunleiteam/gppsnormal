package gpps.test.check;

import gpps.service.thirdpay.IBalanceWithTPService;
import gpps.service.thirdpay.LoanFromTP;
import gpps.service.thirdpay.LoanHelper;
import gpps.service.thirdpay.ThirdPartyAssistent;
import gpps.service.thirdpay.TransferFromTP;
import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashStreamView extends ThirdPartyAssistent{
	public static LoanFromTP viewByOrderNo(String orderNo) throws Exception{
		String baseUrl=getBaseUrl(ACTION_ORDERQUERY);
		Map<String,String> params=new HashMap<String,String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
			params.put("Action", null);
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(params.get("Action")));
		params.put("OrderNo", orderNo);
		sBuilder.append(StringUtil.strFormat(params.get("OrderNo")));
		RsaHelper rsa = RsaHelper.getInstance();
		params.put("SignInfo", rsa.signData(sBuilder.toString(), privateKey));
		String body=httpClientService.post(baseUrl, params);
		
		List<LoanFromTP> tps = LoanHelper.parseJSON(body, null);
		if(tps==null || tps.isEmpty()){
			throw new Exception("找不到对应的转账信息");
		}
		return tps.get(0);
	}
	
	public static LoanFromTP viewByLoanNo(String loanNo) throws Exception{
		String baseUrl=getBaseUrl(ACTION_ORDERQUERY);
		Map<String,String> params=new HashMap<String,String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
			params.put("Action", null);
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(params.get("Action")));
		params.put("LoanNo", loanNo);
		sBuilder.append(StringUtil.strFormat(params.get("LoanNo")));
		RsaHelper rsa = RsaHelper.getInstance();
		params.put("SignInfo", rsa.signData(sBuilder.toString(), privateKey));
		String body=httpClientService.post(baseUrl, params);
		List<LoanFromTP> tps = LoanHelper.parseJSON(body, null);
		if(tps==null || tps.isEmpty()){
			throw new Exception("找不到对应的转账信息");
		}
		return tps.get(0);
	}
	
	
	public static void main(String args[]) throws Exception{
		IBalanceWithTPService balanceService = context.getBean(IBalanceWithTPService.class);
		
		LoanFromTP tp = balanceService.viewByLoanNo("T20150126001422209093718", "2");
//		LoanFromTP tp = balanceService.viewByOrderNo("8029", "2");
		
//		viewByOrderNo("8143");
//		LoanFromTP tp = viewByLoanNo("LN14070871501300004000935565");
		System.out.println(tp.getOrderNo()+":::"+tp.getLoanNo());
		System.exit(0);
	}
}
