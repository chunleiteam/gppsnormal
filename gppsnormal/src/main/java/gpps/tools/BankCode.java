package gpps.tools;

import java.util.HashMap;
import java.util.Map;

public class BankCode {
	static Map<String, String> bankcode = new HashMap<String, String>();
	static{
		bankcode.put("1", "中国银行");
		bankcode.put("2", "工商银行");
		bankcode.put("3", "农业银行");
		bankcode.put("4", "交通银行");
		bankcode.put("5", "广发银行");
		bankcode.put("7", "建设银行");
		bankcode.put("8", "上海浦发银行");
		bankcode.put("10", "招商银行");
		bankcode.put("11", "邮政储蓄银行");
		bankcode.put("12", "民生银行");
		
		bankcode.put("13", "兴业银行");
		bankcode.put("14", "广东发展银行");
		bankcode.put("17", "中信银行");
		bankcode.put("18", "华夏银行");
		bankcode.put("19", "中国光大银行");
		
		bankcode.put("28", "平安银行");
		bankcode.put("29", "浙商银行");
		bankcode.put("30", "上海农村商业银行");
	}
	
	public static String getName(String code){
		return bankcode.get(code);
	}
}