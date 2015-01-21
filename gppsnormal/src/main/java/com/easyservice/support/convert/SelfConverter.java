package com.easyservice.support.convert;

public class SelfConverter<T> implements IConverter<T, T, Object>{

	
	public T convert(T source) throws ConvertException {
		return source;
	}

	public T convert(T source, Object format) throws ConvertException {
		return source;
	}

	public Object getDefalutFormat() {
		return null;
	}

	public Object getFormat() {
		return null;
	}

	public Class<T> getSourceType() {
		return null;
	}

	public Class<T> getTargetType() {
		return null;
	}

	public T restore(T target) throws ConvertException {
		return target;
	}

	public T restore(T target, Object format) throws ConvertException {
		return target;
	}

	public void setFormat(Object format) {
	}

	public Object parseFormat(String s) throws ConvertException {
		return s;
	}
	
}
