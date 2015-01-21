package com.easyservice.support.convert.basic;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringSqlTimestampConverter implements IConverter<String, Timestamp, DateFormat> {

	static DateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'Z");
	protected DateFormat format = (DateFormat)defaultFormat.clone();
	
	public Timestamp convert(String source) throws ConvertException {
		return convert(source, getFormat());
	}

	public Timestamp convert(String source, DateFormat format) throws ConvertException {
		Timestamp rt = null;
		try {
			rt = new Timestamp(format.parse(source).getTime());
			
		} catch (Throwable e) {
			throw new ConvertException(e);
		}
		if (format.format(rt).startsWith(source.trim())) {
			return rt;
		}
		throw new ConvertException(source + " does not match format: " + format.toString());
	}

	public DateFormat getDefalutFormat() {
		return (DateFormat)defaultFormat.clone();
	}

	public DateFormat getFormat() {
		return format == null ? null : (DateFormat)format.clone();
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<Timestamp> getTargetType() {
		return Timestamp.class;
	}

	public String restore(Timestamp target) throws ConvertException {
		return restore(target, getFormat());
	}

	public String restore(Timestamp target, DateFormat format) throws ConvertException {
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
