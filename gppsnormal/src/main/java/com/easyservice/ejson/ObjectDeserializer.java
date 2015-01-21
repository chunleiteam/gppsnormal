package com.easyservice.ejson;

import java.util.Map;

import com.easyservice.reflect.IMember;
import com.easyservice.support.ClassInfo;

public class ObjectDeserializer extends AbstractDeserializer<Object> {


	public Object deserialize(EJsonReader tokener, Class cls) {
		
		tokener.match('{', true);
		
		//first find type, optimize later
	
		Class typeCls = cls;
		String property = tokener.readNextString(':').trim();
		if(!property.equals(EJsonConsts.TYPE_STRING) ){
			if ( (cls == null)){
				throw new EJSONDeserializeException(EJsonConsts.TYPE_STRING+" is not given first");
			}
			tokener.rewind();
			tokener.match('{', true);
		}else {
			String type = tokener.readNextQuoteString();
			typeCls = loadClass(type, cls) ; 
			tokener.match(',',true);
		}
		
		
	
		
		
		
		String idProperty = tokener.readNextString(':');
		
		if(!idProperty.equals(EJsonConsts.ID_STRING)&&!idProperty.equals(EJsonConsts.REFID_STRING)){
			throw new EJSONDeserializeException("expect: id, but meet :" + idProperty);
		}
		
		int refId = tokener.readNextInt();
		Map<Integer, Object>  objectDeserRefMap = tokener.getReferenceMap();
		Throwable t ; 
		if(idProperty.equals(EJsonConsts.ID_STRING)){ // need create a new object
			try{
				int cur = tokener.readNext();
				Object rootObj = typeCls.newInstance();
				objectDeserRefMap.put(refId, rootObj);
				if(cur=='}'){ 
					
				}else if(cur==','){
					ClassInfo clsInfo = EJsonManager.getClassInfo(typeCls);
					while((property=tokener.readNextString(':'))!=null){
						if(property.startsWith("_")&&EJsonConsts.isJSExclude(property.substring(1))){
							property = property.substring(1);
						}
//						for (IMember m : (Collection<IMember>)clsInfo.getProperties()){
//							System.out.println(m.getName());
//						}
//						System.out.println("[" + property+"]");
						IMember member = clsInfo.getProperty(property.trim());
						Object mObj = tokener.read( member.getType(), member.getItemTypes());
						if (!member.isReadOnly()) {
							member.setValue(rootObj, mObj);
						}
						cur = tokener.readNext();
						if(cur=='}'){
							break;
						}else if(cur==','){
							continue ;
						}else{
							t =  new EJSONDeserializeException("unexcepted "+ new Character((char)cur));
						}
					}
				}else{
					t =  new EJSONDeserializeException("unexcepted "+ new Character((char)cur));
				}
				
				return rootObj ; 
			}catch (Exception e) {
				t =  new EJSONDeserializeException(e);
			}
			throw (EJSONDeserializeException)t ; 
		}else{ // "$REFID"
			tokener.match('}',true);
			return objectDeserRefMap.get(refId);
		}
		
	}

	public Object deserialize(EJsonReader tokener) {
		return deserialize(tokener, null);
	}

	public Object deserialize(EJsonReader tokener, Class cls,
			Class... itemTypes) {
		return deserialize(tokener, cls);
	}

}
