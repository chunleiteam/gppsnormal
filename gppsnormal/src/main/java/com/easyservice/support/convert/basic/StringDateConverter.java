package com.easyservice.support.convert.basic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringDateConverter implements IConverter<String, Date, DateFormat> {

	static DateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'Z");
	protected DateFormat format = (DateFormat)defaultFormat.clone();
	
	public Date convert(String source) throws ConvertException {
		return convert(source, getFormat());
	}

	public Date convert(String source, DateFormat format) throws ConvertException {
		Date rt = null;
		try {
			rt = format.parse(source);
			
		} catch (Throwable e) {
			throw new ConvertException(e);
		}
		if (format.format(rt).startsWith(source.trim())) {
			return rt;
		}
		throw new ConvertException(source + " does not match format: " + format.toString());
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

	public Class<Date> getTargetType() {
		return Date.class;
	}

	public String restore(Date target) throws ConvertException {
		return restore(target, getFormat());
	}

	public String restore(Date target, DateFormat format) throws ConvertException {
		return format.format(target);
	}

	public void setFormat(DateFormat format) {
		this.format = format;
	}

	public DateFormat parseFormat(String s) throws ConvertException {
		SimpleDateFormat sf = new SimpleDateFormat(s);
		sf.setLenient(false);
		return sf;
	}

}
