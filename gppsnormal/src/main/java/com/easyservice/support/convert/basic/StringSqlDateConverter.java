package com.easyservice.support.convert.basic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringSqlDateConverter implements IConverter<String, java.sql.Date, DateFormat> {

	
	static DateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
	protected DateFormat format = (DateFormat)defaultFormat.clone();
	
	public java.sql.Date convert(String source) throws ConvertException {
		return convert(source, getFormat());
	}

	public java.sql.Date convert(String source, DateFormat format) throws ConvertException {
		if (source == null){
			return null;
		}
		try {
			return (java.sql.Date) format.parse(source);
		} catch (ParseException e) {
			throw new ConvertException(e);
		}
	}

	public DateFormat getDefalutFormat() {
		//SimpleDateFormat is not thread safe
		return (DateFormat)defaultFormat.clone();
	}

	public DateFormat getFormat() {
		return format == null ? null : (DateFormat)format.clone();
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<java.sql.Date> getTargetType() {
		return java.sql.Date.class;
	}

	public DateFormat parseFormat(String s) throws ConvertException {
		return new SimpleDateFormat(s);
	}

	public String restore(java.sql.Date target) throws ConvertException {
		return restore(target, getFormat());
	}

	public String restore(java.sql.Date target, DateFormat format) throws ConvertException {
		if (target == null){
			return null;
		}
		return format.format(target);
	}

	public void setFormat(DateFormat format) {
		this.format = format;
	}
	
	
	
}
