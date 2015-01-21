package com.easyservice.support.convert.basic;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class SimpleTypeBoxingConverter implements IConverter {

	Class sourceType;
	Class targetClass;
	
	
	
	public SimpleTypeBoxingConverter(Class sourceType, Class targetClass) {
		super();
		this.sourceType = sourceType;
		this.targetClass = targetClass;
	}

	public Object convert(Object source) throws ConvertException {
		return source;
	}

	public Object convert(Object source, Object format) throws ConvertException {
		return source;
	}

	public Object getDefalutFormat() {
		return null;
	}

	public Object getFormat() {
		return null;
	}

	public Class getSourceType() {
		return sourceType;
	}

	public Class getTargetType() {
		return targetClass;
	}

	public Object parseFormat(String s) throws ConvertException {
		return null;
	}

	public Object restore(Object target) throws ConvertException {
		return target;
	}

	public Object restore(Object target, Object format) throws ConvertException {
		return target;
	}

	public void setFormat(Object format) {

	}

}
