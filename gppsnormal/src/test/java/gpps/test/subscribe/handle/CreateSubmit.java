package gpps.test.subscribe.handle;

import java.math.BigDecimal;
import java.util.Date;

import gpps.dao.IProductDao;
import gpps.dao.ISubmitDao;
import gpps.model.Submit;
import gpps.service.IProductService;
import gpps.service.ISubmitService;
import gpps.service.thirdpay.IAuditBuyService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CreateSubmit {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ISubmitService submitService = context.getBean(ISubmitService.class);
	static ISubmitDao submitDao = context.getBean(ISubmitDao.class);
	static IProductDao productDao = context.getBean(IProductDao.class);
	public static void main(String args[]) throws Exception{
		Submit submit = new Submit();
		submit.setAmount(new BigDecimal(30000));
		submit.setCreatetime((new Date()).getTime());
		submit.setLastmodifytime((new Date()).getTime());
		submit.setLenderId(12200);
		submit.setProductId(12221);
		submit.setState(Submit.STATE_SUBSCRIBE_WAITFORPAY);
		submitDao.create(submit);
		productDao.buy(12221, new BigDecimal(30000));
		System.exit(0);
	}
}