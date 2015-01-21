package com.easyservice.support.convert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.easyservice.support.convert.basic.StringBigDecimalConverter;
import com.easyservice.support.convert.basic.StringBigIntegerConverter;
import com.easyservice.support.convert.basic.StringBooleanConverter;
import com.easyservice.support.convert.basic.StringByteArrayConverter;
import com.easyservice.support.convert.basic.StringByteConverter;
import com.easyservice.support.convert.basic.StringCharacterConverter;
import com.easyservice.support.convert.basic.StringClassConverter;
import com.easyservice.support.convert.basic.StringDataSourceConverter;
import com.easyservice.support.convert.basic.StringDateConverter;
import com.easyservice.support.convert.basic.StringDoubleConverter;
import com.easyservice.support.convert.basic.StringEnumConverter;
import com.easyservice.support.convert.basic.StringFloatConverter;
import com.easyservice.support.convert.basic.StringIntegerConverter;
import com.easyservice.support.convert.basic.StringLocaleConverter;
import com.easyservice.support.convert.basic.StringLongConverter;
import com.easyservice.support.convert.basic.StringShortConverter;
import com.easyservice.support.convert.basic.StringSqlDateConverter;
import com.easyservice.support.convert.basic.StringSqlTimestampConverter;


public class StringConvertSpace extends ConverterSpace {

	public final static Class[] BASIC_CONVERTER_TYPES = {
		StringBigDecimalConverter.class,
		StringBigIntegerConverter.class,
		StringBooleanConverter.class,
		StringByteConverter.class,
		StringCharacterConverter.class,
		StringClassConverter.class,
		StringSqlDateConverter.class,
		StringSqlTimestampConverter.class,
		StringByteArrayConverter.class,
		StringDateConverter.class,
		StringDoubleConverter.class,
		StringFloatConverter.class,
		StringIntegerConverter.class,
		StringLongConverter.class,
		StringShortConverter.class,
		StringDataSourceConverter.class
	};
	
	public StringConvertSpace() {
		Map<Class, IConverter> cmap = new ConcurrentHashMap<Class, IConverter>();
		for (Class cc : BASIC_CONVERTER_TYPES){
			IConverter c;
			try {
				c = (IConverter)cc.newInstance();
				cmap.put(c.getTargetType(), c);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		cmap.put(Enum.class, StringEnumConverter.DEFAULT_STRINGENUM_CONVERTER);
		cmap.put(Integer.TYPE, new StringIntegerConverter());
		cmap.put(Float.TYPE, new StringFloatConverter());
		cmap.put(Short.TYPE, new StringShortConverter());
		cmap.put(Double.TYPE, new StringDoubleConverter());
		cmap.put(Long.TYPE, new StringLongConverter());
		cmap.put(Byte.TYPE, new StringByteConverter());
		cmap.put(Character.TYPE, new StringCharacterConverter());
		cmap.put(Boolean.TYPE, new StringBooleanConverter());
		cmap.put(Locale.class, new StringLocaleConverter());
		converterMap.put(String.class, cmap);
		IConverter sdc = converterMap.get(String.class).get(Date.class);
		registerSuggestFormat(sdc, new SimpleDateFormat("yyyy-MM-dd"));
	}
	public StringConvertSpace(StringConvertSpace sc) {
		
		for (Class k :  sc.converterMap.keySet()){
			Map<Class, IConverter> cmap = new HashMap<Class, IConverter>();
			cmap.putAll(sc.converterMap.get(k));
			this.converterMap.put(k, cmap);
		}
		
		
		for (Entry<IConverter, Set<Object>> en : sc.suggestFormatMap.entrySet()){
			registerSuggestFormat(en.getKey(), en.getValue());
		}
	}
	
	public boolean canConvert(Class targetType) {
		return super.canConvert(String.class, targetType);
	}

	public IConverter getConvert(Class targetType) {
		return super.getConverter(String.class, targetType);
	}

	public IConverter setFormat(Class targetType, Object format) {
		return super.setFormat(String.class, targetType, format);
	}
	
	public void registerConverter(IConverter c){
		if (c.getSourceType() == String.class){
			converterMap.get(String.class).put(c.getTargetType(), c);
		}else{
			//reasonable?
			Class sc = c.getSourceType();
			Map<Class, IConverter> cm =  converterMap.get(sc);
			if (cm == null){
				converterMap.put(sc, cm = new HashMap<Class, IConverter>());
			}
			cm.put(c.getTargetType(), c);
		}
		
	}
	
}
