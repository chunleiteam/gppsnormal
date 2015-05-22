package gpps.test.reward.handle;

import java.util.List;

import gpps.dao.IInviteDao;
import gpps.dao.ILenderDao;
import gpps.model.Invite;
import gpps.model.Lender;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ViewInviteTree {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	static IInviteDao inviteDao = context.getBean(IInviteDao.class);
	public static void main(String args[]) throws Exception{
		List<Invite> invites = inviteDao.queryByAttriToAndBatchCode(7, null);
		for(Invite invite : invites){
			showInvite(invite, "", null);
		}
		System.exit(0);
	}
	
	public static void showInvite(Invite invite, String prefix, Integer batchCode){
		Integer lenderId = invite.getRegisterBy();
		
		if(lenderId==null){
			return;
		}
		
		Lender lender = lenderDao.find(lenderId);
		System.out.println(prefix+"::"+lender.getLoginId());
		
		List<Invite> invites = inviteDao.queryByAttriToAndBatchCode(lenderId, batchCode);
		if(invites==null){
			return;
		}
		for(Invite inv : invites){
			showInvite(inv, prefix+"----",batchCode);
		}
	}
}
