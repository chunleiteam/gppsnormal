package com.easyservice.ejson;

import java.util.IdentityHashMap;

public abstract class AbstractSerializer<T> implements IEJsonSerializer<T> {

	protected IEJsonIOManager manager = EJsonManager.getDefaultManager();
	
	public IEJsonIOManager getManager() {
		return manager;
	}

	public void setManager(IEJsonIOManager manager) {
		this.manager = manager;
	}
	
	public String serialize(T t) {
		return serialize(t, new IdentityHashMap<Object, Integer>());
	}
	
}
