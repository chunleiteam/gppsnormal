package com.easyservice.ejson;

import java.lang.reflect.Array;
import java.util.IdentityHashMap;

public class ArraySerializer extends AbstractSerializer<Object> {

	static final String START = "[" ;
	static final String END = "]" ;
	
	public String serialize(Object t, IdentityHashMap<Object, Integer> refMap) {
		
		StringBuilder builder = new StringBuilder(START);
		IEJsonIOManager manager = getManager();
		for(int i=0; i<Array.getLength(t); i++){
			Object o = Array.get(t, i);
			if (o == null){
				builder.append("null");
			}else {
				builder.append(manager.getSerializer(o.getClass()).serialize(o, refMap));
			}
			if(i!=Array.getLength(t)-1){
				builder.append(",");
			}
		}
		builder.append(END);
		return builder.toString() ; 
	}

}
