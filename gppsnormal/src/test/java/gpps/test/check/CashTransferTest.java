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

public class CashTransferTest extends TestAssistance{
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
		loadJson.setLoanOutMoneymoremore("m23075");
		loadJson.setLoanInMoneymoremore("m23072");
		loadJson.setOrderNo("12199");
		loadJson.setBatchNo("20");
		loadJson.setAmount("100");
		loanJsons.add(loadJson);
		
		payNewService.repayApply(loanJsons, null);
		System.exit(0);
	}
}
