package com.easyservice.ejson;

import java.math.BigDecimal;
import java.util.Map;

public class BigDecimalDeSerializer extends AbstractDeserializer<BigDecimal> {

	public BigDecimal deserialize(EJsonReader tokener) {

		
		tokener.match('{', true);
		
		//first find type, optimize later
		String property = tokener.readNextString(':').trim();
		
		if(!property.equals(EJsonConsts.TYPE_STRING)){
			throw new EJSONDeserializeException("$TYPE is not given first");
		}
		
		String type = tokener.readNextQuoteString();
		
		if(!type.equals("java.math.BigDecimal")){
			throw new EJSONDeserializeException("$TYPE is not java.math.BigDecimal");
		}
		
		tokener.match(',',true);
		
		
		String idProperty = tokener.readNextString(':');
		
		if(!idProperty.equals(EJsonConsts.ID_STRING)&&!idProperty.equals(EJsonConsts.REFID_STRING)){
			throw new EJSONDeserializeException("expect: id, but meet :" + idProperty);
		}
		
		int refId = tokener.readNextInt();
		Map<Integer, Object>  objectDeserRefMap = tokener.getReferenceMap();
		if(idProperty.equals(EJsonConsts.ID_STRING)){ // need create a new object
			tokener.match(',',true);
			String valueHead = tokener.readNextString(':').trim();
			if(!valueHead.equals("value")){
				throw new EJSONDeserializeException("value is not given first");
			}
			
			String value = tokener.readNextQuoteString();
			
			tokener.match('}',true);
			
			return new BigDecimal(value);
		}else{ // "$REFID"
			tokener.match('}',true);
			return (BigDecimal) objectDeserRefMap.get(refId);
		}
		
		
		
	
	}

	public BigDecimal deserialize(EJsonReader tokener, Class cls) {
		return deserialize(tokener);
	}

	public BigDecimal deserialize(EJsonReader tokener, Class cls,
			Class... itemTypes) {
		return deserialize(tokener);
	}

}
