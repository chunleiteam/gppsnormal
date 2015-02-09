package gpps.service.impl;

import gpps.inner.service.IInnerThirdPaySupportService;
import gpps.service.thirdpay.IBalanceWithTPService;
import gpps.service.thirdpay.IHttpClientService;
import gpps.service.thirdpay.LoanFromTP;
import gpps.service.thirdpay.LoanHelper;
import gpps.service.thirdpay.ThirdPartyAssistent;
import gpps.tools.RsaHelper;
import gpps.tools.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceWithTPServiceImpl implements IBalanceWithTPService {
@Autowired
IInnerThirdPaySupportService innerThirdPayService;
@Autowired
IHttpClientService httpClientService;
	@Override
	public LoanFromTP viewByOrderNo(String orderNo, String action) throws Exception {
		String baseUrl=innerThirdPayService.getBaseUrl(IInnerThirdPaySupportService.ACTION_ORDERQUERY);
		Map<String,String> params=new HashMap<String,String>();
		params.put("PlatformMoneymoremore", innerThirdPayService.getPlatformMoneymoremore());
			params.put("Action", action);
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(params.get("Action")));
		params.put("OrderNo", orderNo);
		sBuilder.append(StringUtil.strFormat(params.get("OrderNo")));
		RsaHelper rsa = RsaHelper.getInstance();
		params.put("SignInfo", rsa.signData(sBuilder.toString(), innerThirdPayService.getPrivateKey()));
		String body=httpClientService.post(baseUrl, params);
		
		List<LoanFromTP> tps = LoanHelper.parseJSON(body, action);
		if(tps==null || tps.isEmpty()){
			throw new Exception("找不到对应的转账信息");
		}
		return tps.get(0);
	}

	@Override
	public LoanFromTP viewByLoanNo(String loanNo, String action) throws Exception {
		String baseUrl=innerThirdPayService.getBaseUrl(IInnerThirdPaySupportService.ACTION_ORDERQUERY);
		Map<String,String> params=new HashMap<String,String>();
		params.put("PlatformMoneymoremore", innerThirdPayService.getPlatformMoneymoremore());
			params.put("Action", action);
		StringBuilder sBuilder=new StringBuilder();
		sBuilder.append(StringUtil.strFormat(params.get("PlatformMoneymoremore")));
		sBuilder.append(StringUtil.strFormat(params.get("Action")));
		params.put("LoanNo", loanNo);
		sBuilder.append(StringUtil.strFormat(params.get("LoanNo")));
		RsaHelper rsa = RsaHelper.getInstance();
		params.put("SignInfo", rsa.signData(sBuilder.toString(), innerThirdPayService.getPrivateKey()));
		String body=httpClientService.post(baseUrl, params);
		List<LoanFromTP> tps = LoanHelper.parseJSON(body, action);
		if(tps==null || tps.isEmpty()){
			throw new Exception("找不到对应的转账信息");
		}
		return tps.get(0);
	}

}
