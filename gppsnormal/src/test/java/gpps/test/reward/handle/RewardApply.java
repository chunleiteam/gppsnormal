package gpps.test.reward.handle;

import java.util.ArrayList;
import java.util.List;

import gpps.dao.ILenderDao;
import gpps.service.IRewardService;
import gpps.service.Reward;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class RewardApply {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	static IRewardService rewardService = context.getBean(IRewardService.class);
	public static void main(String args[]) throws Exception{
		List<Reward> rewards = new ArrayList<Reward>();
		Reward r1 = new Reward();
		r1.setLenderId(12298);
		r1.setAmount(30);
		rewards.add(r1);
		
		Reward r2 = new Reward();
		r2.setLenderId(12299);
		r2.setAmount(140);
		rewards.add(r2);
		
		Reward r3 = new Reward();
		r3.setLenderId(12298);
		r3.setAmount(230);
		rewards.add(r3);
		
		List<String> res = rewardService.reward(rewards, 2);
		for(String re:res){
			System.out.println(re);
		}
		
		System.exit(0);
	}
}
