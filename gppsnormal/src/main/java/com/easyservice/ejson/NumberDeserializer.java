package com.easyservice.ejson;


import com.easyservice.support.convert.StringConvertSpace;

public class NumberDeserializer extends AbstractDeserializer<Object> {

	private static StringConvertSpace stringConvertSpace = new StringConvertSpace();
	

	public Object deserialize(EJsonReader tokener, Class cls) {
		if(cls == null){
			return deserialize(tokener);
		}else{
			return resolveNumber(tokener.readNextNumber(), cls);
		}
//		try{
//			if(stringConvertSpace.canConvert(cls)){
//				return stringConvertSpace.getConvert(cls).convert(tokener.readNextNumber());
//			}
//		}catch (Exception e) { // should never happen
//			throw new EJSONDeserializeException(e);
//		}
//		throw new EJSONDeserializeException("not support cls: "+cls.getName());
	}

	public Object deserialize(EJsonReader tokener) {
		try{
			String numberString = tokener.readNextNumber();
			if(numberString.indexOf('e')!=-1||numberString.indexOf('E')!=-1||numberString.indexOf('.')!=-1){// is float
				double d = (Double) resolveNumber(numberString, Double.class);
				if(d == (float)d){
					return (float)d;
				}else{
					return d ; 
				}
			}else{
				long l = (Long)resolveNumber(numberString, Long.class);
				if(l == (int)l){
					return (int)l ; 
				}else{
					return l ; 
				}
			}
		}catch (Exception e) {
			if (e instanceof EJsonException){
				throw (EJsonException)e;
			}
			throw new EJSONDeserializeException(e);
		}
		
	}

	public Object deserialize(EJsonReader tokener, Class cls,
			Class... itemTypes) {
		return deserialize(tokener, cls);
	}
	
	private Object resolveNumber(String s, Class cls){
		try{
			if(stringConvertSpace.canConvert(cls)){
				return stringConvertSpace.getConvert(cls).convert(s);
			}
		}catch (Exception e) { // should never happen
			throw new EJSONDeserializeException(e);
		}
		throw new EJSONDeserializeException("not support cls: "+cls.getName());
	}

}
