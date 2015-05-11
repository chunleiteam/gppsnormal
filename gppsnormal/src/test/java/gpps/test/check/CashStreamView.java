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

public class CashStreamView extends TestAssistance{
	public static LoanFromTP viewByOrderNo(String orderNo) throws Exception{
		String baseUrl=innerThirdPayService.getBaseUrl(ACTION_ORDERQUERY);
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
		String baseUrl=innerThirdPayService.getBaseUrl(ACTION_ORDERQUERY);
		Map<String,String> params=new HashMap<String,String>();
		params.put("PlatformMoneymoremore", platformMoneymoremore);
			params.put("Action", "1");
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
		
//		LoanFromTP tp = balanceService.viewByLoanNo("1429416651405644", "1");
		LoanFromTP tp = balanceService.viewByOrderNo("998", "1");
		
//		viewByOrderNo("8143");
//		LoanFromTP tp = viewByLoanNo("1429416651405644");
		System.out.println(tp.getOrderNo()+":::"+tp.getLoanNo());
		System.exit(0);
	}
}
