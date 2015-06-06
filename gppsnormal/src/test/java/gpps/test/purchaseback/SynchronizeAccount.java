package gpps.test.purchaseback;

import gpps.dao.ILenderDao;
import gpps.service.IPurchaseService;
import gpps.service.PurchaseBackCalculateResult;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SynchronizeAccount {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	static IPurchaseService purchaseService = context.getBean(IPurchaseService.class);
	public static void main(String args[]) throws Exception{
		purchaseService.synchronizeAccount(12400);
		System.exit(0);
	}
}
