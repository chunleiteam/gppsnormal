package gpps.validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import gpps.dao.IBorrowerAccountDao;
import gpps.dao.IBorrowerDao;
import gpps.dao.ICashStreamDao;
import gpps.dao.ILenderAccountDao;
import gpps.dao.ILenderDao;
import gpps.model.Borrower;
import gpps.model.BorrowerAccount;
import gpps.model.CashStream;
import gpps.model.Lender;
import gpps.model.LenderAccount;
import gpps.service.CashStreamSum;
import gpps.service.thirdpay.IThirdPaySupportService;
import gpps.tools.StringUtil;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class CheckSingleAccount {
	static String SPRINGCONFIGPATH="/src/main/webapp/WEB-INF/spring/root-context.xml";
	protected static ApplicationContext context =new FileSystemXmlApplicationContext(SPRINGCONFIGPATH);
	protected static ILenderDao lenderDao = context.getBean(ILenderDao.class);
	protected static ILenderAccountDao lenderAccountDao = context.getBean(ILenderAccountDao.class);
	protected static IBorrowerDao borrowerDao = context.getBean(IBorrowerDao.class);
	protected static IBorrowerAccountDao borrowerAccountDao = context.getBean(IBorrowerAccountDao.class);
	protected static ICashStreamDao cashStreamDao = context.getBean(ICashStreamDao.class);
	protected static IThirdPaySupportService thirdPaySupportService = context.getBean(IThirdPaySupportService.class);
	private static final String NEWLINE="\r\n";
	public static void main(String args[]) throws Exception{
		
		Integer[] lenderIds = {12298,12299};
		Integer[] borrowerIds = {12300};
		
		
		StringBuilder sBuilder = new StringBuilder();
		
		for(Integer t:lenderIds){
			checkSingleLender(sBuilder, t);
		}
		
		sBuilder.append("----------------------------------------"+NEWLINE);
		
		for(Integer t:borrowerIds){
			checkSingleBorrower(sBuilder, t);
		}
		System.out.println(sBuilder.toString());
		
		System.exit(0);
	}
	
	public static void checkSingleBorrower(StringBuilder sBuilder, Integer id){
		Borrower borrower = borrowerDao.find(id);
		boolean flag = true;
		BorrowerAccount account = borrowerAccountDao.find(borrower.getAccountId());
		//验证平台账户金额与第三方账户金额一致
		flag = checkBorrowerWithTP(sBuilder, borrower, account);
		if(flag==false){
//			System.out.println(sBuilder.toString());
			return;
		}
		
		//验证：总金额=可用金额+冻结金额
		if(account.getFreeze().add(account.getUsable()).compareTo(account.getTotal())!=0)
		{
			flag = false;
			appendMsg(sBuilder, Borrower.class, borrower.getId(), borrower.getThirdPartyAccount(), "账户金额错误,总金额不等于可用金额+冻结金额");
		}
		//可用金额=(充值、取现、冻结、解冻)现金流之和+（支付、还款、存零）现金流之和取反
		List<Integer> actions=new ArrayList<Integer>();
		actions.add(CashStream.ACTION_FREEZE);
		actions.add(CashStream.ACTION_UNFREEZE);
		actions.add(CashStream.ACTION_CASH);
		actions.add(CashStream.ACTION_RECHARGE);
		CashStreamSum sum=cashStreamDao.sumCashStream(null, borrower.getAccountId(), actions);
		sum=(sum==null)?new CashStreamSum():sum;
		actions=new ArrayList<Integer>();
		actions.add(CashStream.ACTION_PAY);
		actions.add(CashStream.ACTION_REPAY);
		actions.add(CashStream.ACTION_STORECHANGE);
		CashStreamSum sum2=cashStreamDao.sumCashStream(null, borrower.getAccountId(), actions);
		sum2=(sum2==null)?new CashStreamSum():sum2;
		sum.setChiefAmount(sum.getChiefAmount().add(sum2.getChiefAmount().negate()));
		sum.setInterest(sum.getInterest().add(sum2.getInterest().negate()));
		if(account.getUsable().compareTo(sum.getChiefAmount().add(sum.getInterest()))!=0)
		{
			flag = false;
			appendMsg(sBuilder, Borrower.class, borrower.getId(), borrower.getThirdPartyAccount(), "可用金额与现金流验证错误,可用金额:"+account.getUsable().toString()+",现金流:"+sum);
		}
		
		//冻结金额=冻结+解冻
		actions=new ArrayList<Integer>();
		actions.add(CashStream.ACTION_FREEZE);
		actions.add(CashStream.ACTION_UNFREEZE);
		sum=cashStreamDao.sumCashStream(null, borrower.getAccountId(), actions);
		sum=(sum==null)?new CashStreamSum():sum;
		if(account.getFreeze().negate().compareTo(sum.getChiefAmount().add(sum.getInterest()))!=0)
		{
			flag = false;
			appendMsg(sBuilder, Borrower.class, borrower.getId(), borrower.getThirdPartyAccount(), "冻结金额与现金流验证错误,冻结金额:"+account.getFreeze().toString()+",现金流:"+sum);
		}
		
		if(flag==true){
			appendMsg(sBuilder, Borrower.class, borrower.getId(), borrower.getThirdPartyAccount(), "企业账户"+id+"没问题");
		}
		
	}
	
	public static void checkSingleLender(StringBuilder sBuilder, Integer id){
		Lender lender = lenderDao.find(id);
		
		boolean flag = true;
		
		LenderAccount account=lenderAccountDao.find(lender.getAccountId());
		
		
		//验证平台账户金额与第三方账户金额一致
		flag = checkLenderWithTP(sBuilder, lender, account);
		if(flag==false){
//			System.out.println(sBuilder.toString());
			return;
		}
		
		
		
		
		//验证：总金额=可用金额+冻结金额+已投资金额
		if(account.getFreeze().add(account.getUsable()).add(account.getUsed()).compareTo(account.getTotal())!=0)
		{
			flag=false;
			appendMsg(sBuilder, Lender.class, lender.getId(), lender.getThirdPartyAccount(), "账户金额错误,总金额不等于可用金额+冻结金额+已投资金额");
		}
		//可用金额=所有现金流之和
		CashStreamSum sum=cashStreamDao.sumCashStream(lender.getAccountId(), null, null);
		sum=(sum==null)?new CashStreamSum():sum;
		if(account.getUsable().compareTo(sum.getChiefAmount().add(sum.getInterest()))!=0)
		{
			flag=false;
			appendMsg(sBuilder, Lender.class, lender.getId(), lender.getThirdPartyAccount(), "可用金额与现金流验证错误,可用金额:"+account.getUsable().toString()+",现金流:"+sum);
		}
		//冻结金额=冻结+解冻
		List<Integer> actions=new ArrayList<Integer>();
		actions.add(CashStream.ACTION_FREEZE);
		actions.add(CashStream.ACTION_UNFREEZE);
		sum=cashStreamDao.sumCashStream(lender.getAccountId(), null, actions);
		sum=(sum==null)?new CashStreamSum():sum;
		if(account.getFreeze().negate().compareTo(sum.getChiefAmount().add(sum.getInterest()))!=0)
		{
			flag=false;
			appendMsg(sBuilder, Lender.class, lender.getId(), lender.getThirdPartyAccount(), "冻结金额与现金流验证错误,冻结金额:"+account.getFreeze().toString()+",现金流:"+sum);
		}
		//已投资金额=购买+回款（本金）
		actions=new ArrayList<Integer>();
		actions.add(CashStream.ACTION_PAY);
		actions.add(CashStream.ACTION_REPAY);
		sum=cashStreamDao.sumCashStream(lender.getAccountId(), null, actions);
		sum=(sum==null)?new CashStreamSum():sum;
		if(account.getUsed().negate().compareTo(sum.getChiefAmount())!=0)
		{
			flag=false;
			appendMsg(sBuilder, Lender.class, lender.getId(), lender.getThirdPartyAccount(), "已用金额与现金流验证错误,已用金额:"+account.getFreeze().toString()+",现金流:"+sum.getChiefAmount());
		}
		//利息=回款（利息）
		actions=new ArrayList<Integer>();
		actions.add(CashStream.ACTION_REPAY);
		sum=cashStreamDao.sumCashStream(lender.getAccountId(), null, actions);
		sum=(sum==null)?new CashStreamSum():sum;
		if(account.getTotalincome().compareTo(sum.getInterest())!=0)
		{
			flag=false;
			appendMsg(sBuilder, Lender.class, lender.getId(), lender.getThirdPartyAccount(), "已收益金额与现金流验证错误,已收益金额:"+account.getTotalincome().toString()+",现金流:"+sum.getInterest());
		}
		
		//总金额=充值+回款（利息）+奖励-提现
		actions = new ArrayList<Integer>();
		actions.add(CashStream.ACTION_REPAY);
		CashStreamSum repaysum=cashStreamDao.sumCashStream(lender.getAccountId(), null, actions);
		repaysum=(repaysum==null)?new CashStreamSum():repaysum;
		
		actions = new ArrayList<Integer>();
		actions.add(CashStream.ACTION_RECHARGE);
		CashStreamSum rechargeSum=cashStreamDao.sumCashStream(lender.getAccountId(), null, actions);
		rechargeSum=(rechargeSum==null)?new CashStreamSum():rechargeSum;
		
		actions = new ArrayList<Integer>();
		actions.add(CashStream.ACTION_AWARD);
		CashStreamSum awardSum=cashStreamDao.sumCashStream(lender.getAccountId(), null, actions);
		awardSum=(awardSum==null)?new CashStreamSum():awardSum;
		
		actions = new ArrayList<Integer>();
		actions.add(CashStream.ACTION_CASH);
		CashStreamSum cashSum=cashStreamDao.sumCashStream(lender.getAccountId(), null, actions);
		cashSum=(cashSum==null)?new CashStreamSum():cashSum;
		
		System.out.println("总金额:"+account.getTotal().toString()+",充值:"+rechargeSum.getChiefAmount().doubleValue()+"//奖励："+awardSum.getChiefAmount().doubleValue()+"//回款利息："+repaysum.getInterest().doubleValue()+"//提现: "+cashSum.getChiefAmount().doubleValue());
		if(rechargeSum.getChiefAmount().add(awardSum.getChiefAmount()).add(repaysum.getInterest()).add(cashSum.getChiefAmount()).compareTo(account.getTotal())!=0){
			flag=false;
			appendMsg(sBuilder, Lender.class, lender.getId(), lender.getThirdPartyAccount(), "总金额不等于充值+回款利息-提现,总金额:"+account.getTotal().toString()+",充值:"+rechargeSum.getChiefAmount().doubleValue()+"//奖励："+awardSum.getChiefAmount().doubleValue()+"//回款利息："+repaysum.getInterest().doubleValue()+"//提现: "+cashSum.getChiefAmount().doubleValue());
		}
		
		if(flag==true){
			appendMsg(sBuilder, Lender.class, lender.getId(), lender.getThirdPartyAccount(), "账户"+id+"没问题");
		}
	}
	
	private static boolean checkBorrowerWithTP(StringBuilder sBuilder, Borrower borrower, BorrowerAccount account){
		if(StringUtil.isEmpty(borrower.getThirdPartyAccount()))
		{
			return true;
		}
		//与第三方验证
		//网贷平台子账户可用余额|总可用余额(子账户可用余额+公共账户可用余额)|子账户冻结余额”（例:100.00|200.00|10.00）
		String text=thirdPaySupportService.balanceQuery(borrower.getThirdPartyAccount());
		if(StringUtil.isEmpty(text))
		{
			appendMsg(sBuilder, Borrower.class, borrower.getId(), borrower.getThirdPartyAccount(), "从第三方支付平台获取账户信息失败.");
			return false;
		}
		
		String[] thirdAccount=text.split("\\|");
		if(thirdAccount.length!=3)
		{
			appendMsg(sBuilder, Borrower.class, borrower.getId(), borrower.getThirdPartyAccount(), 
					"查询账户的乾多多标识错误:"+borrower.getThirdPartyAccount());
			return false;
		}
		else if(!compareAccount(thirdAccount[0], account.getUsable())||!compareAccount(thirdAccount[2], account.getFreeze()))
		{
			appendMsg(sBuilder, Borrower.class, borrower.getId(), borrower.getThirdPartyAccount(), 
					"本地账户与第三方支付平台不符,本地可用|冻结金额为"+account.getUsable().toString()+"|"+account.getFreeze().toString()+";"
					+"第三方可用|冻结金额为"+thirdAccount[0]+"|"+thirdAccount[2]);
			return false;
		}
		return true;
	}
	
	private static boolean checkLenderWithTP(StringBuilder sBuilder, Lender lender, LenderAccount account){
		if(StringUtil.isEmpty(lender.getThirdPartyAccount()))
		{
			return true;
		}
		//与第三方验证
		//网贷平台子账户可用余额|总可用余额(子账户可用余额+公共账户可用余额)|子账户冻结余额”（例:100.00|200.00|10.00）
		String text=thirdPaySupportService.balanceQuery(lender.getThirdPartyAccount());
		if(StringUtil.isEmpty(text))
		{
			appendMsg(sBuilder, Lender.class, lender.getId(), lender.getThirdPartyAccount(), "从第三方支付平台获取账户信息失败.");
			return false;
		}
		
		try{
			Thread.sleep(10);
		}catch(Exception e){
			
		}
		
		
		String[] thirdAccount=text.split("\\|");
		if(thirdAccount.length!=3)
		{
			appendMsg(sBuilder, Lender.class, lender.getId(), lender.getThirdPartyAccount(), 
					"查询账户的乾多多标识错误:"+lender.getThirdPartyAccount());
			return false;
		}
		else if(!compareAccount(thirdAccount[0], account.getUsable())||!compareAccount(thirdAccount[2], account.getFreeze()))
		{
			appendMsg(sBuilder, Lender.class, lender.getId(), lender.getThirdPartyAccount(), 
					"本地账户与第三方支付平台不符,本地可用|冻结金额为"+account.getUsable().toString()+"|"+account.getFreeze().toString()+";"
					+"第三方可用|冻结金额为"+thirdAccount[0]+"|"+thirdAccount[2]);
			return false;
		}
		return true;
	}
	
	
	
	private static void appendMsg(StringBuilder sBuilder,Class cls,Integer id,String thirdPartyAccount,String msg)
	{
		sBuilder.append(cls.getSimpleName()).append("[").append("id:").append(id).append(",")
		.append("thirdPartyAccount:").append(thirdPartyAccount).append("]").append(" ").append(msg).append(NEWLINE);
	}
	private static boolean compareAccount(String str,BigDecimal bigDecimal)
	{
		
		if(StringUtil.isEmpty(str)|| !StringUtil.isDigit(str))
			return false;
		if(new BigDecimal(str).compareTo(bigDecimal)==0)
				return true;
		return false;
	}
}
