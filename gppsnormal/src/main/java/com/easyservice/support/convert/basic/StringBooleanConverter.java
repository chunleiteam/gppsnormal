package com.easyservice.support.convert.basic;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringBooleanConverter implements IConverter<String, Boolean, Object> {

	
	public Boolean convert(String source) throws ConvertException {
		return convert(source, getDefalutFormat());
	}

	public Boolean convert(String source, Object format) throws ConvertException {
		try{
			return Boolean.parseBoolean(source);
		}catch (Throwable e) {
			throw new ConvertException(e);
		}
		
	}

	public Object getDefalutFormat() {
		return null;
	}

	public Object getFormat() {
		return null;
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<Boolean> getTargetType() {
		return Boolean.class;
	}

	public String restore(Boolean target) throws ConvertException {
		return restore(target, null);
	}

	public String restore(Boolean target, Object format) throws ConvertException {
		return Boolean.toString(target);
	}

	public void setFormat(Object format) {
	}

	public Object parseFormat(String s) throws ConvertException {
		return s;
	}

}
