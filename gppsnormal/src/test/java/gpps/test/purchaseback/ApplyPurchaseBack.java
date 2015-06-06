package gpps.test.purchaseback;

import gpps.dao.ILenderDao;
import gpps.service.IPurchaseService;
import gpps.service.PurchaseBackCalculateResult;

import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ApplyPurchaseBack {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	static IPurchaseService purchaseService = context.getBean(IPurchaseService.class);
	public static void main(String args[]) throws Exception{
		PurchaseBackCalculateResult result = purchaseService.preCalPurchaseBack(9, (new Date()).getTime()+45L*24*3600*1000);
		System.out.println("submitAmount"+result.getSubmitAmount().toString());
		System.out.println("totalAmount"+result.getTotalAmount().toString());
		System.out.println("purchaseAmount"+result.getPurchaseAmount().toString());
		System.out.println("interestAlready"+result.getInterestAlready().toString());
		System.out.println("chiefAlread"+result.getChiefAlread().toString());
		System.out.println("interestTo"+result.getInterestTo().toString());
		System.out.println("chiefTo"+result.getChiefTo().toString());
		System.out.println("purchaseBackFee"+result.getPurchaseBackFee().toString());
		System.out.println("holdingDays"+result.getHoldingDays());
		System.out.println("interestAfterPB"+result.getInterestAfterPB().toString());
		System.out.println("rateAfterPB"+result.getRateAfterPB().toString());
		
//		purchaseService.applyPurchaseBack(1);
		
		System.exit(0);
	}
}
