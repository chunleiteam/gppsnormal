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
		String message = "【春蕾政采贷】尊敬的用户，ZCD2015004001号项目已完成调查，将于4月23日20点30分发布，标的金额50万元，年化利率16%，期限3个月，按月摊还本息，敬请关注！政采贷平台移动端快捷支付功能已经开通，您可通过手机快捷支付进行充值投资，充值所产生的千分之三点五费用由政采贷平台进行全额垫付，您可进行免费充值，欢迎使用！详情请查看平台网站：www.zhengcaidai.com。";
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
