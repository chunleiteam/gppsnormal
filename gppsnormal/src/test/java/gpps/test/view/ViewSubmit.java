package gpps.test.view;

import java.util.List;

import gpps.dao.ILenderDao;
import gpps.dao.ISubmitDao;
import gpps.model.Lender;
import gpps.model.Submit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ViewSubmit {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static ISubmitDao submitDao = context.getBean(ISubmitDao.class);
	static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	public static void main(String args[]){
	List<Submit> submits = submitDao.findAllByProductAndState(14, Submit.STATE_COMPLETEPAY);
	System.out.println("用户姓名\t登录名\t\t\t身份证\t投标额度\t合同编号");
	for(Submit submit : submits){
		Lender lender = lenderDao.find(submit.getLenderId());
		System.out.println(lender.getName()+"\t"+lender.getLoginId()+"\t"+lender.getIdentityCard()+"\t"+submit.getAmount().floatValue()+"\t"+submit.getId());
	}
	System.exit(0);
	}
}
