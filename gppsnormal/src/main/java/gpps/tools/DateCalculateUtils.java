package gpps.tools;

import java.util.Calendar;
import java.util.Date;

public class DateCalculateUtils {
	public static long getStartTime(long start){
		Calendar starttime=Calendar.getInstance();
		starttime.setTimeInMillis(start);
		starttime.set(starttime.get(Calendar.YEAR), starttime.get(Calendar.MONTH), starttime.get(Calendar.DATE), 0, 0, 0);
		return starttime.getTimeInMillis();
	}
	public static long getEndTime(long end){
		Calendar endtime = Calendar.getInstance();
		endtime.setTimeInMillis(end);
		endtime.set(endtime.get(Calendar.YEAR), endtime.get(Calendar.MONTH), endtime.get(Calendar.DATE), 23, 59, 59);
		return endtime.getTimeInMillis();
	}
	public static int getDays(Calendar starttime,Calendar endtime)
	{
		if(starttime.get(Calendar.YEAR)>endtime.get(Calendar.YEAR))
			return 0;
		if(starttime.get(Calendar.YEAR)==endtime.get(Calendar.YEAR))
			return endtime.get(Calendar.DAY_OF_YEAR)-starttime.get(Calendar.DAY_OF_YEAR);
		else {
			return starttime.getActualMaximum(Calendar.DAY_OF_YEAR)-starttime.get(Calendar.DAY_OF_YEAR)+endtime.get(Calendar.DAY_OF_YEAR);
		}
	}
	
	public static int getDays(long start,long end)
	{
		Calendar starttime=Calendar.getInstance();
		starttime.setTimeInMillis(start);
		Calendar endtime = Calendar.getInstance();
		endtime.setTimeInMillis(end);
		return getDays(starttime, endtime);
	}
	
	public static void main(String args[]) throws Exception{
		Date date = new Date();
		System.out.println(date.toLocaleString());
		Date datestart = new Date(getStartTime(date.getTime()));
		System.out.println(datestart.toLocaleString());
		Date dateend = new Date(getEndTime(date.getTime()));
		System.out.println(dateend.toLocaleString());
		
		Date from = new Date(2015-1900,1-1,15,0,0,0);
		Date to = new Date(2015-1900,1-1,15,23,59,59);
		
		
		int day = getDays(from.getTime(), to.getTime());
		System.out.println(day);
	}
}
