package com.easyservice.ejson;

import java.util.IdentityHashMap;

public class EJsonWriter {
	
	public final static int S_COMPACT_STYLE = 1 ; 
	public final static int S_HIERARCHY_STYLE = 2 ; 
	
	IEJsonIOManager manager = EJsonManager.getDefaultManager(); 
	
	public <T> String write(T t) {
		
		IEJsonSerializer serializer = manager.getSerializer(t.getClass());
		if(serializer == null){
			throw new EJsonException("can't find serializer for class "+t.getClass());
		}
		IdentityHashMap<Object, Integer> refMap = new IdentityHashMap<Object, Integer>();
		return serializer.serialize(t, refMap);
	}
	
//	public <T> String write(T t, int options) {
//		ObjectRefMap objectRefMap = EJsonManager.getObjectRefMap();
//		if(objectRefMap.isDirty()){
//			objectRefMap.clear();
//		}
//		objectRefMap.setOptions(options);
//		IEJsonSerializer serializer = manager.getSerializer(t.getClass());
//		if(serializer == null){
//			throw new EJsonException("can't find serializer for class "+t.getClass());
//		}
//		String result = serializer.serialize(t);
//		
//		return result;
//	}

	public IEJsonIOManager getManager() {
		return manager;
	}

	public void setManager(IEJsonIOManager manager) {
		this.manager = manager;
	}
	
}
