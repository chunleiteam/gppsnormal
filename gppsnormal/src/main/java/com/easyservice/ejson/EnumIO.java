package com.easyservice.ejson;

import java.util.IdentityHashMap;

public class EnumIO<T extends Enum> implements IEJsonDeserializer<T>, IEJsonSerializer<T> {
	
	IEJsonIOManager manager = EJsonManager.getDefaultManager();
	
	public T deserialize(EJsonReader tokener) {
		return null;
	}

	public T deserialize(EJsonReader tokener, Class cls) {
		return (T)Enum.valueOf(cls, tokener.readNextQuoteString());
	}

	public T deserialize(EJsonReader tokener, Class cls, Class... itemTypes) {
		return deserialize(tokener, cls);
	}

	public String serialize(T t) {
		return "\"" + t.toString() + "\"";
	}

	public IEJsonIOManager getManager() {
		return manager;
	}
	
	public void setManager(IEJsonIOManager manager) {
		this.manager = manager;
	}

	public String serialize(T t, IdentityHashMap<Object, Integer> refMap) {
		return serialize(t);
	}
	
	

}
