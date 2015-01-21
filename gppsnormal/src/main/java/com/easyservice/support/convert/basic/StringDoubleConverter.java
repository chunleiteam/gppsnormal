package com.easyservice.support.convert.basic;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringDoubleConverter implements IConverter<String, Double, Object> {

	
	public Double convert(String source) throws ConvertException {
		return convert(source, getDefalutFormat());
	}

	public Double convert(String source, Object format) throws ConvertException {
		try{
			return Double.parseDouble(source);
		}catch(Throwable e){
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

	public Class<Double> getTargetType() {
		return Double.class;
	}

	public String restore(Double target) throws ConvertException {
		return restore(target, null);
	}

	public String restore(Double target, Object format) throws ConvertException {
		return Double.toString(target);
	}

	public void setFormat(Object format) {
	}

	public Object parseFormat(String s) throws ConvertException {
		return null;
	}

}
