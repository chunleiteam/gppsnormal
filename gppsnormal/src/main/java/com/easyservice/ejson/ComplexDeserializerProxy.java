package com.easyservice.ejson;

public class ComplexDeserializerProxy {
	
	public static Object deserialize(EJsonReader tokener, Class ... itemTypes){
		Class cls = null ;
		try{
			tokener.mark();
			tokener.match('{',true);
			String property = tokener.readNextString(':').trim();
			if(!property.equals(EJsonConsts.TYPE_STRING) ){
				if ( (itemTypes == null || itemTypes.length == 0)){
					throw new EJSONDeserializeException("$TYPE is not given first");
				}else {
					cls = itemTypes[0];
				}
			}else {
				String type = tokener.readNextQuoteString();
				cls = loadClass(type);
			}
			
		}catch (Exception e) {
			if (e instanceof EJsonException){
				throw (EJsonException)e;
			}
			throw new EJsonException(e);
		}
		tokener.rewind();
		IEJsonDeserializer deserializer = tokener.getManager().getDeserializer(cls);
		return deserializer.deserialize(tokener, cls, itemTypes);
	}
	
	public static Class loadClass(String name) throws ClassNotFoundException{
		return Thread.currentThread().getContextClassLoader().loadClass(name);
	}

}
