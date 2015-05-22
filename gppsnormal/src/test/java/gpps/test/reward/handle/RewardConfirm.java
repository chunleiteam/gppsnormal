package gpps.test.reward.handle;

import java.util.List;

import gpps.dao.ILenderDao;
import gpps.service.IRewardService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class RewardConfirm {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	static IRewardService rewardService = context.getBean(IRewardService.class);
	public static void main(String args[]) throws Exception{
		List<String> res = rewardService.confirmReward(2);
		for(String re:res){
			System.out.println(re);
		}
		
		System.exit(0);
	}
}
