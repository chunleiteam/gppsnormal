package com.easyservice.support.convert.basic;

import java.util.Locale;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringLocaleConverter implements IConverter<String, Locale, String>{

	public Locale convert(String source) throws ConvertException {
		int p = source.indexOf('_');
		if (p > 0){
			return new Locale(source.substring(0, p), source.substring(p+1));
		}
		return new Locale(source);
	}

	public Locale convert(String source, String format) throws ConvertException {
		return convert(source);
	}

	public String getDefalutFormat() {
		return null;
	}

	public String getFormat() {
		return null;
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<Locale> getTargetType() {
		return Locale.class;
	}

	public String parseFormat(String s) throws ConvertException {
		return null;
	}

	public String restore(Locale target) throws ConvertException {
		if (target == null){
			return null;
		}
		return target.toString();
	}

	public String restore(Locale target, String format) throws ConvertException {
		return restore(target);
	}

	public void setFormat(String format) {
	}

}
