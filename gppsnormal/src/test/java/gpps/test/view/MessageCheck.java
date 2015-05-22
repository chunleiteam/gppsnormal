package gpps.test.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class MessageCheck {
	public static Set<String> words = new HashSet<String>();
	static{
		BufferedReader br = null;
		try{
		 br = new BufferedReader(new InputStreamReader(MessageCheck.class.getResourceAsStream("blackword.txt")));
		String temp = br.readLine();
		while(temp!=null){
			words.add(temp);
			temp = br.readLine();
		}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
			br.close();
			}catch(Exception e){
				
			}
		}
	}
	
	public static void main(String args[]) throws Exception{
		String message = "【春蕾政采贷】尊敬的用户，新手专享ZCD2015NEW001号项目已启动，仅限于初次购买客户，期限1个月，年化利率36%，每人限额100元，欢迎体验和推荐。详情请查看http://www.zhengcaidai.com/productdetail.html?pid=16。";
		System.out.println(check(message));
	}
	
	public static boolean check(String message){
		for(String word : words){
			if(message.contains(word)){
				return false;
			}
		}
		return true;
	}
}
