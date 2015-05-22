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
		
		for(Invite invite : invites){
			Statistic sta = null;
			if(res.containsKey(invite.getAttributeTo())){
				sta = res.get(invite.getAttributeTo());
			}else{
				sta = new Statistic();
				res.put(invite.getAttributeTo(), sta);
			}
			sta.total++;
			if(invite.getState()==Invite.STATE_REGISTERED && invite.getRegisterBy()!=null){
				Lender lender = lenderDao.find(invite.getRegisterBy());
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
	}
}
class Statistic{
	public List<Lender> lenders = new ArrayList<Lender>();
	public int total = 0;
	public int unRegister = 0;
}
