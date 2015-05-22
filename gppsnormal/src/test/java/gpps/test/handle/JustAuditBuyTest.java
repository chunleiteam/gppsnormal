package gpps.test.handle;

import gpps.inner.service.IInnerThirdPaySupportService;
import gpps.service.thirdpay.IAuditBuyService;
import gpps.service.thirdpay.IHttpClientService;
import gpps.service.thirdpay.ThirdPartyState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class JustAuditBuyTest {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static IAuditBuyService auditBuyService = context.getBean(IAuditBuyService.class);
	static IInnerThirdPaySupportService innerThirdPayService = context.getBean(IInnerThirdPaySupportService.class);
	static IHttpClientService httpClientService = context.getBean(IHttpClientService.class);
	public static void main(String args[]) throws Exception{
		justAuditBuy2();
	}
	
	public static void justAuditBuy2() throws Exception{
		List<String> loanNos = new ArrayList<String>();
		loanNos.add("LN11243741505111747171565846");
		
		auditBuyService.justAuditBuy(loanNos, ThirdPartyState.THIRD_AUDITTYPE_PASS);
	}
	
	public static void justAuditBuy(){
		List<String> loanNos = new ArrayList<String>();
		loanNos.add("LN13036791503212030555384137");
		
		
		String baseUrl=innerThirdPayService.getBaseUrl(IInnerThirdPaySupportService.ACTION_CHECK);
		StringBuilder loanNoSBuilder=new StringBuilder();
		Map<String,String> params=new HashMap<String, String>();
		params.put("PlatformMoneymoremore", innerThirdPayService.getPlatformMoneymoremore());
		params.put("AuditType", String.valueOf(ThirdPartyState.THIRD_AUDITTYPE_RETURN));
		params.put("ReturnURL", "http://" + innerThirdPayService.getServerHost() + ":" + innerThirdPayService.getServerPort() + "/account/buyaudit/response/bg");
		params.put("NotifyURL", params.get("ReturnURL"));
		for(int i=0;i<loanNos.size();i++)
		{
			if(loanNoSBuilder.length()!=0)
				loanNoSBuilder.append(",");
			loanNoSBuilder.append(loanNos.get(i));
		}
		
		params.put("LoanNoList", loanNoSBuilder.toString());
		
		//签名
		String signInfo = innerThirdPayService.signForAudit(params, innerThirdPayService.getPrivateKey());
		params.put("SignInfo", signInfo);
				
		//将处理好的参数post到第三方，并接受其马上返回的参数
		String body=httpClientService.post(baseUrl, params);
		
		System.out.println(body);
		System.exit(0);
	}
}
