package gpps.test.handle;

import gpps.model.PayBack;
import gpps.service.IPayBackService;
import gpps.service.PayBackToView;

import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class TestPayBackLeftDays {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static IPayBackService payBackService = context.getBean(IPayBackService.class);
	public static void main(String args[]) throws Exception{
		List<PayBackToView> pbs = payBackService.getWaitingForPayBacksByLeftDays(80);
		for(PayBackToView pb:pbs){
			System.out.print(pb.getOrderTitle());
			System.out.print("\t");
			System.out.print(pb.getSeriesTitle());
			System.out.print("\t");
			System.out.print(pb.getBorrowerid());
			System.out.print("\t");
			System.out.print(pb.getBorrowerName());
			System.out.print("\t");
			System.out.print(pb.getChief().floatValue());
			System.out.print("\t");
			System.out.print(pb.getInterest().floatValue());
			System.out.print("\t");
			System.out.print((new Date(pb.getDeadline())).toLocaleString());
			System.out.println();
		}
		System.exit(0);
	}
}
