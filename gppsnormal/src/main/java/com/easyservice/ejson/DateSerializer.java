package com.easyservice.ejson;

import java.util.Date;
import java.util.IdentityHashMap;

public class DateSerializer extends AbstractSerializer<Date>{

	public String serialize(Date t, IdentityHashMap<Object, Integer> refMap) {
		StringBuilder sb = new StringBuilder("{");
		sb.append(EJsonConsts.TYPE_STRING);
		sb.append(": \"java.util.Date\",");
		sb.append(EJsonConsts.DATE_VALUE_STRING);
		sb.append(":");
		sb.append(String.valueOf(t.getTime()));
		sb.append("}");
		return sb.toString();
	}


}
