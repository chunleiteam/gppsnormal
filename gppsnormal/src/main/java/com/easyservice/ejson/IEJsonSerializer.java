package com.easyservice.ejson;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;

public interface IEJsonSerializer <T> {

	public String serialize(T t, IdentityHashMap<Object, Integer> refMap);
	public String serialize(T t);
	public void setManager(IEJsonIOManager manager);
	public IEJsonIOManager getManager();
	
}
