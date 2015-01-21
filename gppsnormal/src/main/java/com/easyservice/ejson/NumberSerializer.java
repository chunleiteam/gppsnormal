package com.easyservice.ejson;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * support int, long, float , double ,long
 * default int serializer ; 
 *int a = 5 ; 
 *
 *{"$TYPE":"int","$VALUE":5}
 *
 */

public class NumberSerializer extends AbstractSerializer<Object>{

	public String serialize(Object t, IdentityHashMap<Object, Integer> refMap) {
		return t.toString();
	}

}
