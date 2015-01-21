package com.easyservice.ejson;
import java.util.Date;


public class ClassDeserializer extends AbstractDeserializer<Class> {

	/**
	 * should create according cls
	 */
	public Class deserialize(EJsonReader tokener, Class cls) {
		return deserialize(tokener);
	}

	public Class deserialize(EJsonReader tokener) {
		
		tokener.match('{', true);
		
		//first find type, optimize later
		String property = tokener.readNextString(':').trim();
		
		if(!property.equals(EJsonConsts.TYPE_STRING)){
			throw new EJSONDeserializeException("$TYPE is not given first");
		}
		
		String type = tokener.readNextQuoteString();
		
		if(!type.equals("java.lang.Class")){
			throw new EJSONDeserializeException("$TYPE is not java.lang.Class");
		}
		
		tokener.match(',',true);
		
		String valueHead = tokener.readNextString(':').trim();
		
		if(!valueHead.equals("name")){
			throw new EJSONDeserializeException("name is not given first");
		}
		
		String value = tokener.readNextQuoteString();
		
		tokener.match('}',true);
		
		return loadClass(value, null);
	}

	public Class deserialize(EJsonReader tokener, Class cls, Class... itemTypes) {
		return deserialize(tokener);
	}

}
