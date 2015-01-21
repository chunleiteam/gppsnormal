package com.easyservice.support.convert.basic;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringFloatConverter implements IConverter<String, Float, Object> {

	
	public Float convert(String source) throws ConvertException {
		return convert(source, getDefalutFormat());
	}

	public Float convert(String source, Object format) throws ConvertException {
		try{
			return Float.parseFloat(source);
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

	public Class<Float> getTargetType() {
		return Float.class;
	}

	public String restore(Float target) throws ConvertException {
		return restore(target, null);
	}

	public String restore(Float target, Object format) throws ConvertException {
		return Float.toString(target);
	}

	public void setFormat(Object format) {
	}

	public Object parseFormat(String s) throws ConvertException {
		return null;
	}

}
