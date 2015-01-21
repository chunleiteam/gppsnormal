package com.easyservice.ejson;
import java.math.BigDecimal;
import java.util.IdentityHashMap;

public class BigDecimalSerializer extends AbstractSerializer<BigDecimal> {

	public String serialize(BigDecimal t,
			IdentityHashMap<Object, Integer> refMap) {
		if (t == null){
			return EJsonConsts.EJ_NULL_VALUE;
		}
		StringBuilder sb = new StringBuilder("{").append(EJsonConsts.TYPE_STRING).append(":\"");
		sb.append("java.math.BigDecimal");
		if(refMap.containsKey(t)){
			sb.append("\",").append(EJsonConsts.REFID_STRING).append(":");
			sb.append(refMap.get(t));
			sb.append("}");
		}else{
			
			Integer id = refMap.size();
			refMap.put(t, id);
			sb.append("\",").append(EJsonConsts.ID_STRING).append(":");
			sb.append(id);
			sb.append(",");
			sb.append("value");
			sb.append(":\"");
			sb.append(t.toPlainString());
			sb.append("\"}");
		}
		
		return sb.toString();
	}

}
