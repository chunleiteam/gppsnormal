package com.easyservice.ejson;

import java.util.HashMap;

public class ObjectDeserRefMap {
	
	HashMap<Integer, Object> innerMap = new HashMap<Integer, Object>();
	
	public Object getObject(int refId){
		return innerMap.get(refId);
	}
	
	public void addObject(Object object, int refId){
		innerMap.put(refId, object);
	}
	
	public boolean isDirty(){
		return innerMap.size() != 0 ;
	}
	
}
