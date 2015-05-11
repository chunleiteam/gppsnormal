package gpps.test.subscribe.handle;

import java.util.List;

import gpps.dao.IProductDao;
import gpps.dao.ISubmitDao;
import gpps.dao.ISubscribeDao;
import gpps.model.Subscribe;
import gpps.service.ISubmitService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ViewSubscribe {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ISubmitService submitService = context.getBean(ISubmitService.class);
	static ISubmitDao submitDao = context.getBean(ISubmitDao.class);
	static IProductDao productDao = context.getBean(IProductDao.class);
	static ISubscribeDao subscribeDao = context.getBean(ISubscribeDao.class);
	public static void main(String args[]) throws Exception{
		List<Subscribe> subs = subscribeDao.findAllByProductIdAndState(12226, -1);
		for(Subscribe sub : subs){
			System.out.println(sub.getId()+"::"+sub.getApplyAmount()+":::"+sub.getAuditAmount()+":::"+sub.getState()+":::"+sub.getLenderId());
		}
		
		System.exit(0);
	}
}
