package gpps.test.view;

import gpps.dao.ILenderAccountDao;
import gpps.dao.ILenderDao;
import gpps.model.Lender;
import gpps.model.LenderAccount;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ViewAllLender {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	static ILenderAccountDao lenderAccountDao = context.getBean(ILenderAccountDao.class);
	public static void main(String args[]) throws Exception{
		List<Lender> lenders = lenderDao.findAll(0, 100);
		for(Lender lender : lenders){
			LenderAccount la = lenderAccountDao.find(lender.getAccountId());
			System.out.println(lender.getName()+"\t\t"+lender.getLoginId()+"\t\t"+lender.getTel()+"\t\t"+la.getUsable().floatValue());
		}
		System.exit(0);
	}
}
