package gpps.constant;

import java.util.HashMap;
import java.util.Map;

//分页常量
public class Pagination {
	public static final String TOTAL="total";
	public static final String RESULT="result";
	public static final String OFFSET="offset";
	public static final String RECNUM="recnum";
	public static Map<String,Object> buildResult(Object result,int total,int offset,int recnum)
	{
		Map<String,Object> m=new HashMap<String,Object>();
		m.put(TOTAL, total);
		m.put(RESULT, result);
		m.put(OFFSET, offset);
		m.put(RECNUM, recnum);
		return m;
	}
}
