package com.easyservice.ejson;

import java.util.Map;

public interface IEJsonIOManager {
	
	public  void register(Class cls, IEJsonSerializer serializer);
	public  void register(Class cls, IEJsonDeserializer deserializer);
	public  void register(IEJsonIOManager manager);	
	
	public  IEJsonSerializer getSerializer(Class cls);
	public  IEJsonSerializer getSerializer(Object o);
	public  IEJsonSerializer getExactSerializer(Class cls);
	
	public  IEJsonSerializer getDeserializer(String type);
	public  IEJsonDeserializer getDeserializer(Class cls);
	public  IEJsonDeserializer getExactDeserializer(Class cls);
	
	public IEJsonDeserializer getArrayDeserializer();
	
	public IEJsonDeserializer getNumberDeserializer();
	
	public IEJsonDeserializer getStringDeserializer();
	
	public Map<Class, IEJsonDeserializer> getDefaultDeserializers();
	
}
