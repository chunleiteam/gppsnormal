package gpps.test.reward.handle;

import gpps.dao.IInviteDao;
import gpps.dao.ILenderDao;
import gpps.model.Invite;
import gpps.model.Lender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ViewInvite {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	static IInviteDao inviteDao = context.getBean(IInviteDao.class);
	public static void main(String args[]) throws Exception{
		List<Invite> invites = inviteDao.queryByBatchCode(1);
		
		Map<Integer, Statistic> res = new HashMap<Integer, Statistic>();
		
		int totalInviteNum = 0;
		int totalRegisterNum = 0;
		int totalBuyNum = 0;
		int totalMoney = 0;
		
		for(Invite invite : invites){
			Statistic sta = null;
			if(res.containsKey(invite.getAttributeTo())){
				sta = res.get(invite.getAttributeTo());
			}else{
				sta = new Statistic();
				res.put(invite.getAttributeTo(), sta);
			}
			sta.total++;
			totalInviteNum++;
			if(invite.getState()==Invite.STATE_REGISTERED && invite.getRegisterBy()!=null){
				totalRegisterNum++;
				Lender lender = lenderDao.find(invite.getRegisterBy());
				
				if(lender.getGrade()>10000){
					totalBuyNum++;
					totalMoney+=100;
				}else{
					totalMoney+=20;
				}
				
				sta.lenders.add(lender);
			}else{
				sta.unRegister++;
			}
		}
		
		
		for(Integer lId : res.keySet()){
			Lender lender = lenderDao.find(lId);
			System.out.println(lender.getName()+"::"+res.get(lId).total+"::"+res.get(lId).lenders.size()+"::"+res.get(lId).unRegister);
			
			int money = 0;
			for(Lender le : res.get(lId).lenders){
				System.out.println("----"+le.getLoginId()+"::"+le.getName()+"::"+le.getGrade());
				if(le.getGrade()<=10000){
					money+=20;
				}else{
					money+=100;
				}
			}
			System.out.println("应给的奖励金额为"+money+"元");
			System.out.println("-------------------------------------------------------------------------------");
		}
		
		System.out.println("共发送邀请码"+totalInviteNum+"个");
		System.out.println("共注册用户"+totalRegisterNum+"个");
		System.out.println("参与购买用户"+totalBuyNum+"个");
		System.out.println("共支付推广费用"+totalMoney+"元");
		
		System.exit(0);
	}
}
class Statistic{
	public List<Lender> lenders = new ArrayList<Lender>();
	public int total = 0;
	public int unRegister = 0;
}
