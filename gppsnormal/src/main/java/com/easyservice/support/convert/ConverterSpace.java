package com.easyservice.support.convert;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConverterSpace {
	
	static SelfConverter selfConverter = new SelfConverter();
	
	protected Map<Class, Map<Class, IConverter > > converterMap = new HashMap<Class, Map<Class, IConverter >  >();
	protected Set<Class> targetTypeSet = new HashSet<Class>();
	protected Map<IConverter, Set<Object> > suggestFormatMap = new HashMap<IConverter, Set<Object>>();
	 
	
	public ConverterSpace(){
		
	}
	
	public ConverterSpace(Map<Class, Map<Class, IConverter > > converterMap){
		this.converterMap = converterMap;
	}
	
	public void registerSuggestFormat(IConverter c, Object format){
		Set<Object> formats = suggestFormatMap.get(c);
		if (formats == null){
			suggestFormatMap.put(c, formats = new HashSet<Object>());
		}
		formats.add(format);
	}
	
	public void registerSuggestFormat(IConverter c, Set<Object> fs){
		Set<Object> formats = suggestFormatMap.get(c);
		if (formats == null){
			suggestFormatMap.put(c, formats = new HashSet<Object>());
		}
		formats.addAll(fs);
	}
	
	public IConverter getConverter(Class sourceType, Class targetType){
		if (sourceType == targetType){
			return selfConverter;
		}
		IConverter rt = null;
		Map<Class, IConverter> mi = converterMap.get(sourceType);
		if (mi != null){
			rt =  mi.get(targetType);
		}
		if (rt == null){
			mi = converterMap.get(targetType);
			if (mi != null){
				rt =  mi.get(sourceType);
			}
		}
		if (rt == null && targetType.isAssignableFrom(sourceType)){
			return selfConverter;
		}
		return rt;
	}
	
	@SuppressWarnings("unchecked")
	public IConverter setFormat(Class sourceType, Class targetType, Object format){
		IConverter rt = getConverter(sourceType, targetType);
		if (rt != null){
			rt.setFormat(format);
		}
		return rt;
	}
	
	public  boolean  canConvert(Class sourceType, Class targetType){
		if (sourceType == targetType){
			return true;
		}
		return getConverter(sourceType, targetType) != null;
	}
	
	@SuppressWarnings("unchecked")
	public <S, T> T convert(S s, Class<T> targetType) throws ConvertException{
		return convert(s, targetType, null);
	}
	
	@SuppressWarnings("unchecked")
	public <S, T> T convert(S s, Class<T> targetType, Object format) throws ConvertException{
		if (targetType == s.getClass()){
			return (T)s;
		}
		IConverter c = getConverter(s.getClass(), targetType);
		if (c == null){
			throw new ConvertException("can not get converter for " + s.getClass() + "-->" + targetType);
		}
		Set<Object> formats = suggestFormatMap.get(c) ;
		if (format == null){
			format = c.getFormat();
			if (format == null){
				format = c.getDefalutFormat();
			}
		}else if (format instanceof String){
			format  = c.parseFormat((String)format);
		}
		boolean rightOrder = c.getTargetType() == targetType || c.getSourceType() == s.getClass();
		ConvertException last = null;
		try{
			return rightOrder ?  (T)c.convert(s, format) : (T)c.restore(s, format);
		}catch(Exception e){
			if (formats == null ){
				if (c == null){
					throw new ConvertException("can not get converter for " + s.getClass() + "-->" + targetType, e);
				}else{
					if (e instanceof ConvertException){
						throw (ConvertException)e;
					}else{
						throw new ConvertException("convert error for " + s.getClass() + "-->" + targetType + ", value : " + s, e);
					}
				}
				
			}
				for (Object f : formats){
					try{
						return rightOrder ?  (T)c.convert(s, f) : (T)c.restore(s, f);
					}catch (ConvertException ex) {
						last = ex;
					}
				}
		}
		throw last;
	}
	
	@SuppressWarnings("unchecked")
	public boolean extend(Map<Class, Map<Class, IConverter > > extended){
//		Set newSTSet = new HashSet();
//		newSTSet.addAll(converterMap.keySet());
//		Set newTTSet = new HashSet();
//		newTTSet.addAll(targetTypeSet);
		return false;
	}
}
