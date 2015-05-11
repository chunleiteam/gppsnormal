package gpps.test.subscribe.handle;

import java.util.ArrayList;
import java.util.List;

import gpps.dao.IProductDao;
import gpps.dao.ISubmitDao;
import gpps.dao.ISubscribeDao;
import gpps.model.Subscribe;
import gpps.service.ISubmitService;
import gpps.service.ISubscribeService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Audit {
//	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
//	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
//	static ISubmitService submitService = context.getBean(ISubmitService.class);
//	static ISubmitDao submitDao = context.getBean(ISubmitDao.class);
//	static IProductDao productDao = context.getBean(IProductDao.class);
//	static ISubscribeDao subscribeDao = context.getBean(ISubscribeDao.class);
//	static ISubscribeService subscribeService = context.getBean(ISubscribeService.class);
//	public static void main(String args[]) throws Exception{
//		List<Subscribe> subs = new ArrayList<Subscribe>();
//		Subscribe sub = new Subscribe();
//		sub.setId(30);
//		sub.setAmount(100000);
//		sub.setLenderId(12200);
//		sub.setProductId(12226);
//		sub.setState(Subscribe.STATE_CONFIRM_PASS_ALL);
//		subs.add(sub);
//		
//		Subscribe sub1 = new Subscribe();
//		sub1.setId(31);
//		sub1.setAmount(50000);
//		sub1.setLenderId(12200);
//		sub1.setProductId(12226);
//		sub1.setState(Subscribe.STATE_CONFIRM_PASS_ALL);
//		subs.add(sub1);
//		
//		Subscribe sub2 = new Subscribe();
//		sub2.setId(32);
//		sub2.setAmount(50000);
//		sub2.setLenderId(12200);
//		sub2.setProductId(12226);
//		sub2.setState(Subscribe.STATE_CONFIRM_PASS_MODIFY);
//		sub2.setDescription("部分审核通过，原来申请70000，审核通过为50000");
//		subs.add(sub2);
//		
//		
//		Subscribe sub3 = new Subscribe();
//		sub3.setId(33);
//		sub3.setAmount(20000);
//		sub3.setLenderId(12201);
//		sub3.setProductId(12226);
//		sub3.setState(Subscribe.STATE_CONFIRM_REFUSE);
//		subs.add(sub3);
//		
//		Subscribe sub4 = new Subscribe();
//		sub4.setId(34);
//		sub4.setAmount(90000);
//		sub4.setLenderId(12201);
//		sub4.setProductId(12226);
//		sub4.setState(Subscribe.STATE_CONFIRM_PASS_ALL);
//		subs.add(sub4);
//		
//		Subscribe sub5 = new Subscribe();
//		sub5.setId(35);
//		sub5.setAmount(70000);
//		sub5.setLenderId(12201);
//		sub5.setProductId(12226);
//		sub5.setState(Subscribe.STATE_CONFIRM_REFUSE);
//		subs.add(sub5);
//		
//		
//		Subscribe sub6 = new Subscribe();
//		sub6.setId(36);
//		sub6.setAmount(10000);
//		sub6.setLenderId(12202);
//		sub6.setProductId(12226);
//		sub6.setState(Subscribe.STATE_CONFIRM_REFUSE);
//		subs.add(sub6);
//		
//		subscribeService.audit(12226, subs);
//	}
}
