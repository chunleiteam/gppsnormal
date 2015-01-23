package gpps.tools;

import gpps.model.PayBack;
import gpps.model.Product;
import gpps.service.exception.IllegalOperationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PayBackCalculateUtils {
	/**
	 * 等额本息产品的还款计算
	 * 
	 * 
	 * */
	public static List<PayBack> calclatePayBacksForDEBX(int amount,  double rate, long from, long to){
		List<PayBack> payBacks=new ArrayList<PayBack>();
		PayBack payBack=null;
		Calendar starttime=Calendar.getInstance();
		starttime.setTimeInMillis(from);
		starttime.set(starttime.get(Calendar.YEAR), starttime.get(Calendar.MONTH), starttime.get(Calendar.DATE), 0, 0, 0);
		Calendar endtime=Calendar.getInstance();
		endtime.setTimeInMillis(to);
		endtime.set(endtime.get(Calendar.YEAR), endtime.get(Calendar.MONTH), endtime.get(Calendar.DATE), 0, 0, 0);
		
		int monthNum=(endtime.get(Calendar.YEAR)-starttime.get(Calendar.YEAR))*12+(endtime.get(Calendar.MONTH)-starttime.get(Calendar.MONTH));
		if(endtime.get(Calendar.DAY_OF_MONTH)>starttime.get(Calendar.DAY_OF_MONTH))
			monthNum++;
		

		BigDecimal mRate=new BigDecimal(rate).divide(new BigDecimal(12),10,BigDecimal.ROUND_HALF_EVEN);//月利息
		//每月还款额
		BigDecimal amountEachMonth=new BigDecimal(amount).multiply(mRate).multiply(mRate.add(new BigDecimal(1)).pow(monthNum)).divide(mRate.add(new BigDecimal(1)).pow(monthNum).subtract(new BigDecimal(1)),2,BigDecimal.ROUND_HALF_EVEN);//每月归还额
		BigDecimal repayedChiefAmount=BigDecimal.ZERO;
		for(int i=0;i<monthNum;i++)
		{
			Calendar paybackCal;
			if(i+1==monthNum)
				paybackCal=endtime;
			else
			{
				paybackCal=(Calendar)(starttime.clone());
				paybackCal.add(Calendar.MONTH, i+1);
			}
			//计算第n月利息，  第n月还款利息＝（a×i－b）×（1＋i）^（n－1）＋b
			//贷款额为a，月利率为i，还款月数为n，每月还款额为b
			BigDecimal interest=new BigDecimal(amount).multiply(mRate).subtract(amountEachMonth).multiply(mRate.add(new BigDecimal(1)).pow(i)).add(amountEachMonth).setScale(2, BigDecimal.ROUND_HALF_EVEN);
			payBack=new PayBack();
			payBack.setInterest(interest);
			payBack.setState(PayBack.STATE_WAITFORREPAY);
			if(i+1==monthNum)
			{
				payBack.setType(PayBack.TYPE_LASTPAY);
				payBack.setChiefAmount(new BigDecimal(amount).subtract(repayedChiefAmount));
			}
			else
			{
				payBack.setType(PayBack.TYPE_INTERESTANDCHIEF);
				payBack.setChiefAmount(amountEachMonth.subtract(interest));
				repayedChiefAmount=repayedChiefAmount.add(payBack.getChiefAmount());
			}
			paybackCal.add(Calendar.DAY_OF_YEAR, 1);
			payBack.setDeadline(paybackCal.getTimeInMillis());
			payBacks.add(payBack);
		}
		return payBacks;
	}
	
	/**
	 * 按月还息，最后还本产品的还款计算
	 * 
	 * 
	 * */
	public static List<PayBack> calclatePayBacksForXXHB(int amount,  double rate, long from, long to){
		List<PayBack> payBacks=new ArrayList<PayBack>();
		PayBack payBack=null;
		Calendar starttime=Calendar.getInstance();
		starttime.setTimeInMillis(from);
		starttime.set(starttime.get(Calendar.YEAR), starttime.get(Calendar.MONTH), starttime.get(Calendar.DATE), 0, 0, 0);
		Calendar endtime=Calendar.getInstance();
		endtime.setTimeInMillis(to);
		endtime.set(endtime.get(Calendar.YEAR), endtime.get(Calendar.MONTH), endtime.get(Calendar.DATE), 0, 0, 0);
		
		int monthNum=(endtime.get(Calendar.YEAR)-starttime.get(Calendar.YEAR))*12+(endtime.get(Calendar.MONTH)-starttime.get(Calendar.MONTH));
		if(endtime.get(Calendar.DAY_OF_MONTH)>starttime.get(Calendar.DAY_OF_MONTH))
			monthNum++;
		
		

		BigDecimal allInterest = new BigDecimal(0);
		int wholeDays = DateCalculateUtils.getDays(starttime, endtime);
		
		//根据实际总天数算出来的应付总利息， 一年按365天计。小数点两位后的数值直接舍弃
		BigDecimal calculateInterest = (new BigDecimal(amount)).multiply(new BigDecimal(rate)).multiply(new BigDecimal(wholeDays)).divide(new BigDecimal(365),2,BigDecimal.ROUND_DOWN);
		for(int i=0;i<monthNum;i++)
		{
			Calendar currentMonthStart=(Calendar)(starttime.clone());
			currentMonthStart.add(Calendar.MONTH, i);
			Calendar currentMonthEnd=null;
			if(i+1==monthNum)
				currentMonthEnd=(Calendar)(endtime.clone());
			else
			{
				currentMonthEnd=(Calendar)(starttime.clone());
				currentMonthEnd.add(Calendar.MONTH, i+1);
			}
			int days=DateCalculateUtils.getDays(currentMonthStart, currentMonthEnd);
			payBack=new PayBack();
			
			payBack.setState(PayBack.STATE_WAITFORREPAY);
			if(i+1==monthNum)
			{
				payBack.setChiefAmount(new BigDecimal(amount));
				
				//最后一笔还款的利息用计算出的总利息，减去前面所有还款的利息之和
				payBack.setInterest(calculateInterest.subtract(allInterest));
				payBack.setType(PayBack.TYPE_LASTPAY);
			}
			else
			{
				//非最后一笔还款，每个月应还的利息按当月的实际天数计算，一年按365天计。小数点两位后的数值直接舍弃
				BigDecimal thisInterest = new BigDecimal(amount).multiply(new BigDecimal(rate)).multiply(new BigDecimal(days)).divide(new BigDecimal(365),2,BigDecimal.ROUND_DOWN);
				payBack.setInterest(thisInterest);
				
				//将非最后一笔还款的每一笔利息都累加起来，用于计算最后一笔还款的利息
				allInterest = allInterest.add(thisInterest);
				payBack.setType(PayBack.TYPE_INTERESTANDCHIEF);
			}
			currentMonthEnd.add(Calendar.DAY_OF_YEAR, 1);
			payBack.setDeadline(currentMonthEnd.getTimeInMillis());
			payBacks.add(payBack);
		}
		return payBacks;
	}
	
}
