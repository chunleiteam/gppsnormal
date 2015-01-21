package com.easyservice.support.convert.basic;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringShortConverter implements IConverter<String, Short, Integer> {

	static Integer defaultFormat = 10;
	Integer format = defaultFormat;
	
	public Short convert(String source) throws ConvertException {
		return convert(source, format);
	}

	public Short convert(String source, Integer format) throws ConvertException {
		try{
			return Short.parseShort(source, format);
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

	public Class<Short> getTargetType() {
		return Short.class;
	}

	public String restore(Short target) throws ConvertException {
		return restore(target, format);
	}

	public String restore(Short target, Integer format) throws ConvertException {
		return Integer.toString(target, format);
	}

	public void setFormat(Integer format) {
		this.format = format;
	}

	public Integer parseFormat(String s) throws ConvertException {
		return Integer.parseInt(s);
	}

}
