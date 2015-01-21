package com.easyservice.ejson;

import java.util.Date;


public class DateDeserializer extends AbstractDeserializer<Date> {

	/**
	 * should create according cls
	 */
	public Date deserialize(EJsonReader tokener, Class cls) {
		return deserialize(tokener);
	}

	public Date deserialize(EJsonReader tokener) {
		
		tokener.match('{', true);
		
		//first find type, optimize later
		String property = tokener.readNextString(':').trim();
		
		if(!property.equals(EJsonConsts.TYPE_STRING)){
			throw new EJSONDeserializeException("$TYPE is not given first");
		}
		
		String type = tokener.readNextQuoteString();
		
		if(!type.equals("java.util.Date")){
			throw new EJSONDeserializeException("$TYPE is not java.util.date");
		}
		
		tokener.match(',',true);
		
		String valueHead = tokener.readNextString(':').trim();
		
		if(!valueHead.equals(EJsonConsts.DATE_VALUE_STRING)){
			throw new EJSONDeserializeException("$VALUES is not given first");
		}
		
		long value = tokener.readNextLong();
		
		tokener.match('}',true);
		
		return new Date(value);
	}

	public Date deserialize(EJsonReader tokener, Class cls, Class... itemTypes) {
		return deserialize(tokener);
	}

}
