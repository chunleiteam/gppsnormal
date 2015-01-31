package gpps.test.check;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class LoanHelper {
	public static List<LoanFromTP> parseJSON(String json) throws Exception{
		List<LoanFromTP> loans = new ArrayList<LoanFromTP>();
		Gson gson = new Gson();
		List returnParams=gson.fromJson(json, List.class);
		if(returnParams==null || returnParams.isEmpty()){
			throw new Exception("找不到对应的转账信息");
		}
		for(Object obj : returnParams){
			Map<String, Object> temp = (Map<String, Object>)obj;
			LoanFromTP loan = new LoanFromTP(temp);
			loans.add(loan);
		}
		return loans;
	}
	
	public static void main(String args[]) throws Exception{
		List<LoanFromTP> tps = parseJSON(null);
		System.out.println(tps);
	}
}
