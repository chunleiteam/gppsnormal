package com.easyservice.ejson;

import java.util.Collection;
import java.util.Map;

public class MapDeserializer extends AbstractDeserializer<Map> {

	
	public Map deserialize(EJsonReader tokener) {
		return deserialize(tokener, null, null);
	}

	
	public Map deserialize(EJsonReader tokener, Class cls) {
		return deserialize(tokener, cls, null);
	}


	public Map deserialize(EJsonReader tokener, Class cls, Class... itemTypes) {

		tokener.match('{',true);
		
		//first find type, optimize later
		String property = tokener.readNextString(':').trim();
		
		if(!property.equals(EJsonConsts.TYPE_STRING)){
			throw new EJSONDeserializeException("$TYPE is not given first");
		}
		
		String type = tokener.readNextQuoteString();
		Class typeCls = null ; 
		try{
			typeCls = Thread.currentThread().getContextClassLoader().loadClass(type);
			if(cls!=null && typeCls.isAssignableFrom(cls)){
				typeCls = cls ; 
			}
		}catch (Exception e) {
			throw new EJSONDeserializeException("type no found",e);
		}
		
//		if(!type.equals(cls.getName())){
//			throw new EJSONDeserializeException(type +"not the same as "+cls.getName());
//		}
		
		tokener.match(',', true);
		
		String idProperty = tokener.readNextString(':').trim();
		
		if(!idProperty.equals(EJsonConsts.ID_STRING)&&!idProperty.equals(EJsonConsts.REFID_STRING)){
			throw new EJSONDeserializeException("no id");
		}
		
		int refId = tokener.readNextInt();
		tokener.match(',', true);
		try{
			if(idProperty.equals(EJsonConsts.ID_STRING)){ // need create a new object
				Map rootObj = (Map) typeCls.newInstance();
				tokener.getReferenceMap().put(refId, rootObj);
				//begin to create collection element
				String s = tokener.readNextString(':',true).trim();
				if(!s.equals(EJsonConsts.MAP_TABLE_STRING)){
					throw new EJSONDeserializeException("not find field: " + EJsonConsts.MAP_VALUE_STRING + " in map");
				}
				tokener.match('[', true);
				int ahead  = tokener.LA(1);
				if(ahead!=']'){ //not a empty collection
					while (true) {
						tokener.match('{');
						tokener.match(EJsonConsts.MAP_KEY_STRING);
						tokener.match(':');
						//read a map entry
						Object k = null ;
						if(itemTypes!=null && itemTypes.length>0){
							k = tokener.read(itemTypes[0], null);
						}else{
							k = tokener.read();
						}
						
						tokener.match(',');
						tokener.match(EJsonConsts.MAP_VALUE_STRING);
						tokener.match(':');
						Object v = null;
						
						if(itemTypes!=null && itemTypes.length>1){
							v = tokener.read(itemTypes[1], null);
						}else{
							v = tokener.read();
						}
						
						tokener.match('}');
						rootObj.put(k, v);
						ahead = tokener.LA(1);
						if(ahead == ','){
							tokener.match(',');
						}else if(ahead == ']'){
							tokener.match(']');
							break ; 
						}else{ //
							throw new EJSONDeserializeException();
						}
					}
				}else{
					tokener.match(']');
				}
				tokener.match('}',true);
				
				return rootObj ; 
			
			}else{ // "$REFID"
				tokener.match('}',true);
				return (Map)tokener.getReferenceMap().get(refId);
			}
		}catch (Exception e) {
			if (e instanceof EJsonException){
				throw (EJsonException)e;
			}
			throw new EJSONDeserializeException(e);
		}
	
	}

	
}
