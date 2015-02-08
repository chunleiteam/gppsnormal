package gpps.test.handle;

import java.util.ArrayList;
import java.util.List;

import gpps.service.thirdpay.IAuditBuyService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ReAuditBuyProduct {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static IAuditBuyService auditBuyService = context.getBean(IAuditBuyService.class);
	public static void main(String args[]) throws Exception{
		List<String> loanNos = new ArrayList<String>();
//		loanNos.add("LN18147041502082322113450260");
//		loanNos.add("LN17437821502082333438927354");
//		loanNos.add("LN17437821502082333544078204");
		
		loanNos.add("LN17437821502082334082205589");
		loanNos.add("LN18147041502082321330954774");
		loanNos.add("LN18147041502082321104547118");
		auditBuyService.buyAuditSuccessHandle(loanNos);
	}
}
