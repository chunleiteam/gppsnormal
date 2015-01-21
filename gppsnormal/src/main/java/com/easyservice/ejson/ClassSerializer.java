package com.easyservice.ejson;

import java.util.IdentityHashMap;

public class ClassSerializer extends AbstractSerializer<Class> {

	public String serialize(Class t, IdentityHashMap<Object, Integer> refMap) {
		StringBuilder sb = new StringBuilder("{");
		sb.append(EJsonConsts.TYPE_STRING);
		sb.append(":\"java.lang.Class\",");
		sb.append("name");
		sb.append(":");
		sb.append(t.getName());
		sb.append("}");
		return sb.toString();
	}


}
