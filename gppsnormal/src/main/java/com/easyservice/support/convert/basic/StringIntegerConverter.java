package com.easyservice.support.convert.basic;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringIntegerConverter implements IConverter<String, Integer, Integer> {

	static Integer defaultFormat = 10;
	Integer format = defaultFormat;
	
	public Integer convert(String source) throws ConvertException {
		return convert(source, format);
	}

	public Integer convert(String source, Integer format) throws ConvertException {
		try{
			return Integer.parseInt(source, format);
		}catch (Throwable e) {
			throw new ConvertException(e);
		}
	
	}

	public Integer getDefalutFormat() {
		return defaultFormat;
	}

	public Integer getFormat() {
		return format;
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<Integer> getTargetType() {
		return Integer.class;
	}

	public String restore(Integer target) throws ConvertException {
		return restore(target, format);
	}

	public String restore(Integer target, Integer format) throws ConvertException {
		return Integer.toString(target, format);
	}

	public void setFormat(Integer format) {
		this.format = format;
	}

	public Integer parseFormat(String s) throws ConvertException {
		return Integer.parseInt(s);
	}

}
