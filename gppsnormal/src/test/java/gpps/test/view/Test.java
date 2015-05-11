package gpps.test.view;

import gpps.dao.ISubmitDao;
import gpps.dao.ISubscribeDao;
import gpps.model.Lender;
import gpps.model.Submit;
import gpps.model.Subscribe;
import gpps.tools.DateCalculateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Test {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ISubmitDao submitDao = context.getBean(ISubmitDao.class);
	static ISubscribeDao subscribeDao = context.getBean(ISubscribeDao.class);
	public static void main(String args[]) throws Exception{
//		Date date = new Date(1427891400000l);
//		System.out.println(date.toLocaleString());
//		
//		long num = DateCalculateUtils.getFinancingStartTime(1427846400000l);
//		System.out.println(num);
		
//		List<Integer> states = new ArrayList<Integer>();
//		states.add(2);
//		List<Submit> submits = submitDao.findAllByLenderAndStatesAndCreatetime(12200, states, DateCalculateUtils.getStartTime(1423000000000l), DateCalculateUtils.getEndTime(1424000000000l));
//		System.out.println(new Date(DateCalculateUtils.getStartTime(1423000000000l)).toLocaleString()+"::"+submits.size()+"::"+new Date(DateCalculateUtils.getEndTime(1424000000000l)).toLocaleString());
	
//		int grade = calculateGrade(10000, 1000, 12200);
//		System.out.println(grade);
		
		
		Subscribe subscribe = new Subscribe();
		subscribe.setApplyAmount(25000);
		subscribe.setCreateTime((new Date()).getTime());
		subscribe.setDescription("9999999999");
		subscribe.setLenderId(12202);
		subscribe.setProductId(90);
//		subscribeDao.create(subscribe);
//		
//		
//		Subscribe sub = subscribeDao.find(1);
//		System.out.println(sub.getAmount()+"::"+sub.getConfirmTime()+"::"+sub.getCreateTime()+"::"+sub.getDescription()+"::"+sub.getState()+"::"+sub.getLenderId()+"::"+sub.getProductId()+"::");
//		
//		subscribeDao.changeState(1, 1, "新改的，牛逼不");
//		subscribeDao.changeState(2, 2, "一般牛逼吧");
//		subscribeDao.changeState(3, 4, "这就是我");
		
		List<Subscribe> subs = subscribeDao.findAllByProductIdAndState(90, 2);
		System.out.println(subs.size());
		
		System.exit(0);
	}
	
	public static int calculateGrade(int grade, int amount, Integer lenderid){
		long starttime = (new Date()).getTime()-90L*24*3600*1000;
		List<Integer> states = new ArrayList<Integer>();
		states.add(2);
		List<Submit> submits = submitDao.findAllByLenderAndStatesAndCreatetime(lenderid, states, starttime, null);
		Set<Integer> pids = new HashSet<Integer>();
		for(Submit submit : submits){
			pids.add(submit.getProductId());
		}
		BigDecimal weight = Lender.gradeWeight(pids.size());
		int resGrade = grade + weight.multiply(new BigDecimal(amount)).intValue();
		return resGrade;
	}
}
