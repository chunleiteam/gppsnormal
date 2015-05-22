package gpps.validate;

import gpps.dao.ICashStreamDao;
import gpps.dao.IPayBackDao;
import gpps.dao.IProductDao;
import gpps.dao.IProductSeriesDao;
import gpps.dao.ISubmitDao;
import gpps.model.CashStream;
import gpps.model.PayBack;
import gpps.model.Product;
import gpps.model.ProductSeries;
import gpps.model.Submit;
import gpps.service.CashStreamSum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CheckProduct {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	protected static IProductDao productDao = context.getBean(IProductDao.class);
	protected static ISubmitDao submitDao = context.getBean(ISubmitDao.class);
	protected static IPayBackDao paybackDao = context.getBean(IPayBackDao.class);
	protected static ICashStreamDao cashStreamDao = context.getBean(ICashStreamDao.class);
	protected static IProductSeriesDao productSeriesDao = context.getBean(IProductSeriesDao.class);
	public static void main(String args[]){
		
		
		int total = 0;
		int success = 0;
		int error = 0;
		StringBuilder log = new StringBuilder();
		
		List<Integer> states = new ArrayList<Integer>();
		states.add(Product.STATE_UNPUBLISH);
		states.add(Product.STATE_FINANCING);
		states.add(Product.STATE_REPAYING);
		states.add(Product.STATE_QUITFINANCING);
		states.add(Product.STATE_FINISHREPAY);
		List<Product> products = productDao.findByState(states, 0, 10000);
		
		ProductSeries ps = productSeriesDao.findByType(ProductSeries.TYPE_FINISHPAYINTERESTANDCAPITAL);
		List<Product> nps = productDao.findByProductSeriesAndBuyLevelAndState(ps.getId(), 0, states, 0, 10000);
		
		if(nps!=null){
			products.addAll(nps);
		}
		
		for(Product product : products){
			total++;
			try{
				boolean flag = checkSingleProduct(product);
				if(flag==true){
					success++;
				}else{
					error++;
				}
			}catch(Exception e){
				error++;
				log.append(e.getMessage()).append("\n");
			}
		}
		log.append("产品校验    total:"+total+", success:"+success+", error:"+error);
		System.out.println(log.toString());
		System.exit(0);
	}
	
	public static boolean checkSingleProduct(Product product) throws Exception{
		boolean flag = false;
		if(product.getState()==Product.STATE_UNPUBLISH || product.getState()==Product.STATE_FINANCING){
			flag = checkFinancingProduct(product);
		}else if(product.getState()==Product.STATE_REPAYING){
			flag = checkRepayingProduct(product);
		}else if(product.getState()==Product.STATE_FINISHREPAY){
			flag = checkCompleteProduct(product);
		}else if(product.getState()==Product.STATE_QUITFINANCING){
			flag = checkQuitProduct(product);
		}else{
			throw new Exception("产品["+product.getId()+"]状态不对！");
		}
		return flag;
	}
	
	
	public static boolean checkCompleteProduct(Product product) throws Exception{
		List<Submit> submits_complete = submitDao.findAllByProductAndState(product.getId(), Submit.STATE_COMPLETEPAY);
		
		BigDecimal realFinancingAmount = new BigDecimal(0);
		for(Submit submit:submits_complete){
			realFinancingAmount = realFinancingAmount.add(submit.getAmount());
		}
		
		boolean realamountflag = realFinancingAmount.compareTo(product.getRealAmount())==0;
		CashStreamSum sum = cashStreamDao.sumProduct(product.getId(), CashStream.ACTION_PAY);
		boolean realbuycashflag = sum.getChiefAmount().negate().compareTo(product.getRealAmount())==0;
		
		
		
		List<Integer> proIds = new ArrayList<Integer>();
		proIds.add(product.getId());
		List<PayBack> paybacks = paybackDao.findByProductsAndState(proIds, PayBack.STATE_FINISHREPAY);
		BigDecimal paybackChiefAmount = new BigDecimal(0);
		BigDecimal realPayCashAmount = new BigDecimal(0);
		for(PayBack payback:paybacks){
			paybackChiefAmount = paybackChiefAmount.add(payback.getChiefAmount());
			
			//选择用解冻来统计还款的金额
			CashStreamSum paysum = cashStreamDao.sumPayBackByAction(payback.getId(), CashStream.ACTION_UNFREEZE);
			realPayCashAmount = realPayCashAmount.add(paysum.getChiefAmount());
		}
		
		
		boolean paybackflag = paybackChiefAmount.compareTo(product.getRealAmount())==0;
		boolean realpaycashflag = realPayCashAmount.compareTo(product.getRealAmount())==0;
		
		StringBuilder errormessage = new StringBuilder();
		errormessage.append("产品["+product.getId()+"]错误信息:").append("\n");
		if(!paybackflag){
			errormessage.append("产品["+product.getId()+"]实际还款本金总额不等于实际融资总额！");
		}
		if(!realbuycashflag){
			errormessage.append("产品["+product.getId()+"]实际融资总额总额不等于购买现金流之和！");
		}
		if(!realamountflag){
			errormessage.append("产品["+product.getId()+"]实际融资总额不等于所有已投标的[已支付]额度之和！");
		}
		if(!realpaycashflag){
			errormessage.append("产品["+product.getId()+"]还款现金流之和总额不等于实际融资总额！");
		}
		if(paybackflag && realamountflag && realbuycashflag && realpaycashflag){
			return true;
		}else{
			throw new Exception(errormessage.toString());
		}
		
	}
	
	public static boolean checkQuitProduct(Product product) throws Exception{
		List<Submit> submits_complete = submitDao.findAllByProductAndState(product.getId(), Submit.STATE_COMPLETEPAY);
		
		if(submits_complete==null){
			submits_complete = new ArrayList<Submit>();
		}
		
		BigDecimal realFinancingAmount = new BigDecimal(0);
		for(Submit submit:submits_complete){
			realFinancingAmount = realFinancingAmount.add(submit.getAmount());
		}
		
		boolean realamountflag = realFinancingAmount.compareTo(product.getRealAmount())==0;
		
		CashStreamSum sum = cashStreamDao.sumProduct(product.getId(), CashStream.ACTION_FREEZE);
		if(sum==null){
			sum=new CashStreamSum();
		}
		boolean realbuycashflag = sum.getChiefAmount().negate().compareTo(product.getRealAmount())==0;
		
		List<PayBack> paybacks = paybackDao.findAllByProduct(product.getId());
		
		if(paybacks==null){
			paybacks = new ArrayList<PayBack>();
		}
		
		BigDecimal paybackChiefAmount = new BigDecimal(0);
		for(PayBack payback:paybacks){
			paybackChiefAmount = paybackChiefAmount.add(payback.getChiefAmount());
		}
		
		boolean paybackflag = paybackChiefAmount.compareTo(product.getExpectAmount())==0;
		
		StringBuilder errormessage = new StringBuilder();
		errormessage.append("产品["+product.getId()+"]错误信息:").append("\n");
		if(!paybackflag){
			errormessage.append("产品["+product.getId()+"]计算还款本金总额不等于预计融资总额！");
		}
		if(!realamountflag){
			errormessage.append("产品["+product.getId()+"]实际融资总额不等于所有已投标的[已支付+待支付]额度之和！");
		}
		if(!realbuycashflag){
			errormessage.append("产品["+product.getId()+"]实际融资总额总额不等于购买现金流之和！");
		}
		if(paybackflag && realamountflag && realbuycashflag){
			return true;
		}else{
			throw new Exception(errormessage.toString());
		}
		
	}
	
	public static boolean checkRepayingProduct(Product product) throws Exception{
		
		StringBuilder errormessage = new StringBuilder();
		errormessage.append("产品["+product.getId()+"]错误信息:").append("\n");
		
		List<Submit> submits_complete = submitDao.findAllByProductAndState(product.getId(), Submit.STATE_COMPLETEPAY);
		
		List<PayBack> paybacks = paybackDao.findAllByProduct(product.getId());
		BigDecimal paybackChiefAmount = new BigDecimal(0);
		for(PayBack payback:paybacks){
			if(payback.getState() == PayBack.STATE_FINISHREPAY){
				CashStreamSum sum = cashStreamDao.sumPayBackByAction(payback.getId(), CashStream.ACTION_UNFREEZE);
				if(sum.getChiefAmount().compareTo(payback.getChiefAmount())==0 && sum.getInterest().compareTo(payback.getInterest())==0){
					paybackChiefAmount = paybackChiefAmount.add(payback.getChiefAmount());
				}else{
					errormessage.append("产品["+product.getId()+"]的单笔还款["+payback.getId()+"]出错，已还款现金流本金总和不等于应还款本金总额！");
					paybackChiefAmount = paybackChiefAmount.add(sum.getChiefAmount());
				}
			}else{
				paybackChiefAmount = paybackChiefAmount.add(payback.getChiefAmount());
			}
			
		}
		boolean paybackflag = paybackChiefAmount.compareTo(product.getRealAmount())==0;
		
		CashStreamSum sum = cashStreamDao.sumProduct(product.getId(), CashStream.ACTION_PAY);
		boolean realbuycashflag = sum.getChiefAmount().negate().compareTo(product.getRealAmount())==0;
		
		BigDecimal realFinancingAmount = new BigDecimal(0);
		for(Submit submit:submits_complete){
			realFinancingAmount = realFinancingAmount.add(submit.getAmount());
		}
		
		boolean realamountflag = realFinancingAmount.compareTo(product.getRealAmount())==0;
		
		
		if(!paybackflag){
			errormessage.append("产品["+product.getId()+"]计算还款本金总额不等于实际融资总额！");
		}
		if(!realamountflag){
			errormessage.append("产品["+product.getId()+"]实际融资总额不等于所有已投标的[已支付+待支付]额度之和！");
		}
		if(!realbuycashflag){
			errormessage.append("产品["+product.getId()+"]还款[已还+待还]总和与产品实际融资额不一致！");
		}
		if(paybackflag && realamountflag && realbuycashflag){
			return true;
		}else{
			throw new Exception(errormessage.toString());
		}
	}
	
	public static boolean checkFinancingProduct(Product product) throws Exception{
		List<Submit> submits_complete = submitDao.findAllByProductAndState(product.getId(), Submit.STATE_COMPLETEPAY);
		List<Submit> submits_waitingforpay = submitDao.findAllByProductAndState(product.getId(), Submit.STATE_WAITFORPAY);
		
		List<PayBack> paybacks = paybackDao.findAllByProduct(product.getId());
		BigDecimal paybackChiefAmount = new BigDecimal(0);
		for(PayBack payback:paybacks){
			paybackChiefAmount = paybackChiefAmount.add(payback.getChiefAmount());
		}
		
		boolean paybackflag = paybackChiefAmount.compareTo(product.getExpectAmount())==0;
		
		BigDecimal realFinancingAmount = new BigDecimal(0);
		for(Submit submit:submits_complete){
			realFinancingAmount = realFinancingAmount.add(submit.getAmount());
		}
		for(Submit submit:submits_waitingforpay){
			realFinancingAmount = realFinancingAmount.add(submit.getAmount());
		}
		
		boolean realamountflag = realFinancingAmount.compareTo(product.getRealAmount())==0;
		
		StringBuilder errormessage = new StringBuilder();
		errormessage.append("产品["+product.getId()+"]错误信息:").append("\n");
		if(!paybackflag){
			errormessage.append("产品["+product.getId()+"]计算还款本金总额不等于预计融资总额！");
		}
		if(!realamountflag){
			errormessage.append("产品["+product.getId()+"]实际融资总额不等于所有已投标的[已支付+待支付]额度之和！");
		}
		if(paybackflag && realamountflag){
			return true;
		}else{
			throw new Exception(errormessage.toString());
		}
	}
}
