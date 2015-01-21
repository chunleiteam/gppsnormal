package com.easyservice.support.convert.basic;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringClassConverter implements IConverter<String, Class<?>, ClassLoader >{

	ClassLoader format;
	
	public Class<?> convert(String source) throws ConvertException {
		return convert(source, format == null ? getDefalutFormat() : format);
	}

	public Class<?> convert(String source, ClassLoader format) throws ConvertException {
		try {
			return format.loadClass(source);
		} catch (ClassNotFoundException e) {
			throw new ConvertException(e);
		}
	}

	public ClassLoader getDefalutFormat() {
		return Thread.currentThread().getContextClassLoader();
	}

	public ClassLoader getFormat() {
		return format;
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<Class<?>> getTargetType() {
		return (Class<Class<?>>) Class.class.getClass();
	}

	public String restore(Class<?> target) throws ConvertException {
		return restore(target, null);
	}

	public String restore(Class<?> target, ClassLoader format) throws ConvertException {
		return target.getName();
	}

	public void setFormat(ClassLoader format) {
		this.format = format;
	}

	public ClassLoader parseFormat(String s) throws ConvertException {
		return null;
	}

}
