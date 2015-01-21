package com.easyservice.support.convert.basic;

import java.lang.reflect.Method;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringEnumConverter<T extends Enum> implements IConverter<String, T,  EnumStyle>{

	public static StringEnumConverter<Enum> DEFAULT_STRINGENUM_CONVERTER = new StringEnumConverter<Enum>(Enum.class);
	
	Class<T> targetType;
	EnumStyle format = EnumStyle.VALUE;
	
	@SuppressWarnings("unchecked")
	public StringEnumConverter(){
	}
	
	public StringEnumConverter(Class<T> targetType){
		this.targetType = targetType;
	}
	
	public T convert(String source) throws ConvertException {
		if (targetType == null){
			throw new ConvertException("can not convert enum because  targetType == null");
		}
		return convert(source, format);
	}

	@SuppressWarnings("unchecked")
	public T convert(String source, EnumStyle format) throws ConvertException {
		try{
			if (format == EnumStyle.ORDINAL){
				Method m = targetType.getMethod("values");
				Enum[] ems = (Enum[])m.invoke(null);
				for (Enum em : ems){
					if (em.ordinal() == Integer.parseInt(source)){
						return (T)em;
					}
				}
			}
			if (format == EnumStyle.STRING){
				return (T) Enum.valueOf(targetType, source);
			}
			Method m = targetType.getMethod("values");
			Enum[] ems = (Enum[])m.invoke(null);
			for (Enum em : ems){
				if (em.toString().equals(source)){
					return (T)em;
				}
			}
		}catch (Throwable e) {
			throw  new ConvertException(e.getMessage(), e);
		}
		throw new ConvertException(("can not convert " + source + " to " + format +", because in this type no enum statifies: toString() equals  \"" + source+"\""));
	}

	public EnumStyle getDefalutFormat() {
		return DEFAULT_STRINGENUM_CONVERTER.getFormat();
	}

	public EnumStyle getFormat() {
		return format;
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<T> getTargetType() {
		return targetType;
	}

	public String restore(T target) throws ConvertException {
		return target.toString();
	}

	public String restore(T target, EnumStyle format) throws ConvertException {
		return target.toString();
	}

	public void setFormat(EnumStyle format) {
		this.format = format;
	}

	public EnumStyle parseFormat(String s) throws ConvertException {
		return EnumStyle.valueOf(s);
	}

}
