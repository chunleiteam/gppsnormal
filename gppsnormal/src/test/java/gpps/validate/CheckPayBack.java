package gpps.validate;

import gpps.dao.ICashStreamDao;
import gpps.dao.IGovermentOrderDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.ISubmitDao;
import gpps.model.CashStream;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.Submit;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CheckPayBack {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	static IProductDao productDao = context.getBean(IProductDao.class);
	static IGovermentOrderDao orderDao = context.getBean(IGovermentOrderDao.class);
	static IPayBackDao payBackDao = context.getBean(IPayBackDao.class);
	static ISubmitDao submitDao = context.getBean(ISubmitDao.class);
	static ICashStreamDao cashStreamDao = context.getBean(ICashStreamDao.class);
	public static void main(String args[]) throws Exception{
		Integer paybackId = 12;
		printPayBackCondition(paybackId);
		System.exit(0);
	}
	
	public static void printPayBackCondition(Integer paybackId){
		PayBack pb = payBackDao.find(paybackId);
		Product product = productDao.find(pb.getProductId());
		List<CashStream> css = cashStreamDao.findByRepayAndActionAndState(paybackId, CashStream.ACTION_FREEZE, CashStream.STATE_SUCCESS);
		
		BigDecimal totalAmount = new BigDecimal(0);
		BigDecimal totalChief = new BigDecimal(0);
		BigDecimal totalInterest = new BigDecimal(0);
		for(CashStream cs : css){
			if(cs.getSubmitId()!=null)
			{
			Submit submit = submitDao.find(cs.getSubmitId());
				totalAmount = totalAmount.add(submit.getAmount());
				System.out.println("购买额度："+submit.getAmount().floatValue()+",偿还利息："+cs.getChiefamount().floatValue()+"/"+cs.getInterest().floatValue());
				totalChief = totalChief.add(cs.getChiefamount());
				totalInterest = totalInterest.add(cs.getInterest());
			}else{
				System.out.println("余零到平台账户："+cs.getChiefamount().floatValue()+"/"+cs.getInterest().floatValue());
				totalChief = totalChief.add(cs.getChiefamount());
				totalInterest = totalInterest.add(cs.getInterest());
			}
		}
		System.out.println("总购买额度："+totalAmount.floatValue()+",总偿还利息："+totalChief.floatValue()+"/"+totalInterest.floatValue());
		System.out.println("------------------------------------------------------------------------");
		System.out.println("产品募集额度："+product.getRealAmount().floatValue()+",预计偿还利息："+pb.getChiefAmount().floatValue()+"/"+pb.getInterest().floatValue());
	}
}
