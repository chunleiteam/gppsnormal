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


public class CashAuditTest extends TestAssistance {
	
	public static void main(String args[]) throws Exception{
		IAuditRepayService auditRepayService = context.getBean(IAuditRepayService.class);
		IThirdPaySupportNewService payNewService = context.getBean(IThirdPaySupportNewService.class);
		
		List<String> loanNos = new ArrayList<String>();
		loanNos.add("LN13225141502092133041298345");
//		loanNos.add("LN16139081502100126591892736");
		
		
//		payNewService.auditBuy(loanNos, ThirdPartyState.THIRD_AUDITTYPE_RETURN);
		auditRepayService.auditRepay(loanNos, ThirdPartyState.THIRD_AUDITTYPE_PASS);
		
//		checkBuy(loanNos, ThirdPartyState.THIRD_AUDITTYPE_RETURN);
		System.exit(0);
	}
}
