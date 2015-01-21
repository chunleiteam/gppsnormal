package com.easyservice.support.convert.basic;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringCharacterConverter implements IConverter<String, Character, Object> {

	
	public Character convert(String source) throws ConvertException {
		return convert(source, getDefalutFormat());
	}

	public Character convert(String source, Object format) throws ConvertException {
		try{
			return  (source == null || source.length() == 0 ) ? null : Character.valueOf(source.charAt(0));
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

	public Class<Character> getTargetType() {
		return Character.class;
	}

	public String restore(Character target) throws ConvertException {
		return restore(target, null);
	}

	public String restore(Character target, Object format) throws ConvertException {
		return Character.toString(target);
	}

	public void setFormat(Object format) {
	}

	public Object parseFormat(String s) throws ConvertException {
		return s;
	}

}
