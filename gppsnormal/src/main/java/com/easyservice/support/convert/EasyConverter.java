package com.easyservice.support.convert;

import java.lang.reflect.InvocationTargetException;

import com.easyservice.reflect.IMember;
import com.easyservice.support.convert.basic.EnumStyle;
import com.easyservice.support.convert.basic.SimpleTypeBoxingConverter;
import com.easyservice.support.convert.basic.StringEnumConverter;

public class EasyConverter {
	static StringConvertSpace DEFAULT_STRINGCONVERT_SPACE = new StringConvertSpace();
	
	StringConvertSpace stringConvertSpace = DEFAULT_STRINGCONVERT_SPACE;
	
	static EasyConverter defaultInstance = new EasyConverter();
	
	public EasyConverter() {
		stringConvertSpace.registerConverter(new SimpleTypeBoxingConverter(Integer.TYPE, Integer.class));
		stringConvertSpace.registerConverter(new SimpleTypeBoxingConverter(Long.TYPE, Long.class));
		stringConvertSpace.registerConverter(new SimpleTypeBoxingConverter(Float.TYPE, Float.class));
		stringConvertSpace.registerConverter(new SimpleTypeBoxingConverter(Short.TYPE, Short.class));
		stringConvertSpace.registerConverter(new SimpleTypeBoxingConverter(Character.TYPE, Character.class));
		stringConvertSpace.registerConverter(new SimpleTypeBoxingConverter(Double.TYPE, Double.class));
		stringConvertSpace.registerConverter(new SimpleTypeBoxingConverter(Boolean.TYPE, Boolean.class));
	}
	
	public void setEnumFormat(Class enumType, EnumStyle format){
		if (stringConvertSpace == DEFAULT_STRINGCONVERT_SPACE){
			stringConvertSpace = new StringConvertSpace(DEFAULT_STRINGCONVERT_SPACE);
		}
		stringConvertSpace.registerConverter(new StringEnumConverter(enumType));
	}
	
	public void registerConverter(IConverter converter){
		if (stringConvertSpace == DEFAULT_STRINGCONVERT_SPACE){
			stringConvertSpace = new StringConvertSpace(DEFAULT_STRINGCONVERT_SPACE);
		}
		stringConvertSpace.registerConverter(converter);
	}
	
//	public void setDateFormat(String dateFormat){
//		this.dateFormat = new SimpleDateFormat(dateFormat);
//	}
	
	public <V> Object setProperty(V v, Object obj, IMember<V> p) throws ConvertException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object rv = v;
		Class ptype = p.getType();
		if (v != null && v.getClass() == String.class && ptype != v.getClass()){
			String strFormat = v.toString();
			rv = convertString(ptype, strFormat);
		}
		p.setValue(obj, (V)rv);
		return rv;
	}
	
	public <T> boolean canConvertToSimpleString(Class<T> ptype){
		return stringConvertSpace.canConvert(ptype);
	}

	public <T> String restoreString(T o) throws ConvertException{
		if (o == null || o instanceof String){
			return (String)o;
		}
		IConverter<String, T, Object>   c = null; 
		Class  ptype = o.getClass();
		if (ptype.isEnum() && stringConvertSpace.getConvert(ptype) == null){
//			c = DEFAULT_STRINGCONVERT_SPACE.getConvert(Enum.class);
			stringConvertSpace.registerConverter(new StringEnumConverter(ptype));
//			return c.convert(strFormat, ptype);
		}
		return stringConvertSpace.convert(o, String.class);
	}
	
	public <T, C>  C convert(T o, Class<C> c) throws ConvertException{
		if (o == null){
			return null;
		}
		if (c == String.class){
			return (C)restoreString(o);
		}else if (o.getClass() == String.class){
			return (C)convertString(c, o.toString());
		}
		else{
			return stringConvertSpace.convert(o, c);
		}
	}
	
	public <T, C>  C convert(T o, Class<C> c, Object format) throws ConvertException{
		if (o == null){
			return null;
		}
		if (c == String.class){
			return (C)restoreString(o, format);
		}else if (o.getClass() == String.class){
			return (C)convertString(c, o.toString(), format);
		}
		else{
			return stringConvertSpace.convert(o, c, format);
		}
	}
	
	public  <T> String restoreString(T o, Object format) throws ConvertException{
		if (o == null || o instanceof String){
			return (String)o;
		}
		IConverter<String, T, Object>   c = null; 
		Class  ptype = o.getClass();
		if (ptype.isEnum() && stringConvertSpace.getConvert(ptype) == null){
//			c = DEFAULT_STRINGCONVERT_SPACE.getConvert(Enum.class);
			stringConvertSpace.registerConverter(new StringEnumConverter(ptype));
//			return c.convert(strFormat, ptype);
		}
		c = stringConvertSpace.getConvert(ptype);
		return stringConvertSpace.convert(o, String.class, format);
	}
	
	@SuppressWarnings("unchecked")
	public  <T> Object convertString(Class<T> ptype, String strFormat) throws ConvertException {
		if (ptype == String.class){
			return strFormat;
		}
		IConverter<String, T, Object>   c = null; 
		if (ptype.isEnum() && stringConvertSpace.getConvert(ptype) == null){
//			c = DEFAULT_STRINGCONVERT_SPACE.getConvert(Enum.class);
			stringConvertSpace.registerConverter(new StringEnumConverter(ptype));
//			return c.convert(strFormat, ptype);
		}
//		c = DEFAULT_STRINGCONVERT_SPACE.getConvert(ptype);
		return stringConvertSpace.convert(strFormat, ptype);
//		return c.convert(strFormat);
	}
	
	@SuppressWarnings("unchecked")
	public  <T> Object convertString(Class<T> ptype, String strFormat, Object format) throws ConvertException {
		if (ptype == String.class){
			return strFormat;
		}
		IConverter<String, T, Object>   c = null; 
		if (ptype.isEnum() && stringConvertSpace.getConvert(ptype) == null){
//			c = DEFAULT_STRINGCONVERT_SPACE.getConvert(Enum.class);
			stringConvertSpace.registerConverter(new StringEnumConverter(ptype));
//			return c.convert(strFormat, ptype);
		}
//		c = DEFAULT_STRINGCONVERT_SPACE.getConvert(ptype);
		return stringConvertSpace.convert(strFormat, ptype, format);
//		return c.convert(strFormat);
	}

	public IConverter getConverter(Class sourceType, Class targetType) {
		return stringConvertSpace.getConverter(sourceType, targetType);
		
	}
	
	/**
	 * @return the defaultInstance
	 */
	public static EasyConverter getDefaultInstance() {
		return defaultInstance;
	}

}
