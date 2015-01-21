package com.easyservice.ejson;


public interface IEJsonDeserializer <T> {

	public T deserialize(EJsonReader tokener);
	
	public T deserialize(EJsonReader tokener, Class cls);
	
	public T deserialize(EJsonReader tokener, Class cls, Class ... itemTypes);
	
	public void setManager(IEJsonIOManager manager);
	
	public IEJsonIOManager getManager();
}
