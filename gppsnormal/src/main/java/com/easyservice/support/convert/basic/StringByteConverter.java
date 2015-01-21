package com.easyservice.support.convert.basic;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringByteConverter implements IConverter<String, Byte, Integer> {

	static Integer defaultFormat = 10;
	Integer format = defaultFormat;
	
	public Byte convert(String source) throws ConvertException {
		return convert(source, format);
	}

	public Byte convert(String source, Integer format) throws ConvertException {
		try{
			return Byte.parseByte(source, format);
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

	public Class<Byte> getTargetType() {
		return Byte.class;
	}

	public String restore(Byte target) throws ConvertException {
		return restore(target, format);
	}

	public String restore(Byte target, Integer format) throws ConvertException {
		return Integer.toString(target, format);
	}

	public void setFormat(Integer format) {
		this.format = format;
	}

	public Integer parseFormat(String s) throws ConvertException {
		return Integer.parseInt(s);
	}

}
