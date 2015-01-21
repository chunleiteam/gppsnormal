package com.easyservice.ejson;

import java.util.Collection;
import java.util.Map;

public class CollcetionDeserializer extends AbstractDeserializer<Collection>{

	
	public Collection deserialize(EJsonReader tokener, Class cls) {
		return deserialize(tokener, cls, null);
	}

	public Collection deserialize(EJsonReader tokener, Class cls,
			Class... itemTypes) {

		tokener.match('{',true);
		
		//first find type, optimize later
		String property = tokener.readNextString(':').trim();
		
		if(!property.equals(EJsonConsts.TYPE_STRING)){
			throw new EJSONDeserializeException(EJsonConsts.TYPE_STRING + "is not given first");
		}
		
		String type = tokener.readNextQuoteString();
		Class typeCls = null ; 
		try{
			typeCls = Thread.currentThread().getContextClassLoader().loadClass(type);
			if(cls!=null && typeCls.isAssignableFrom(cls)){
				typeCls = cls ; 
			}
		}catch (Exception e) {
			if (e instanceof EJsonException){
				throw (EJsonException)e;
			}
			throw new EJSONDeserializeException(typeCls + " no found",e);
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
		Map<Integer, Object> objectDeserRefMap = tokener.getReferenceMap();
		try{
			if(idProperty.equals(EJsonConsts.ID_STRING)){ // need create a new object
				tokener.match(',', true);
				Collection rootObj = (Collection) typeCls.newInstance();
				//begin to create collection element
				String s = tokener.readNextString(':',true).trim();
				if(!s.equals(EJsonConsts.VALUE_STRING)){
					throw new EJSONDeserializeException();
				}
				tokener.match('[', true);
				int ahead  = tokener.LA(1);
				if(ahead!=']'){ //not a empty collection
					while (true) {
						Object o = null ;
						if(itemTypes!=null && itemTypes.length>0){
							o = tokener.read(itemTypes[0], null);
						}else{
							o = tokener.read();
						}
						rootObj.add(o);
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
				objectDeserRefMap.put(refId, rootObj);
				return rootObj ; 
			
			}else{ // "$REFID"
				tokener.match('}',true);
				return (Collection)objectDeserRefMap.get(refId);
			}
		}catch (Exception e) {
			if (e instanceof EJSONDeserializeException){
				throw (EJSONDeserializeException)e;
			}
			throw new EJSONDeserializeException(e);
		}
	
	}

	@SuppressWarnings("unchecked")
	public Collection deserialize(EJsonReader tokener) {
//		return (Collection) ComplexDeserializerProxy.deserialize(tokener, null);
		return deserialize(tokener, null, null);
	}
	

}
