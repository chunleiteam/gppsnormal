package com.easyservice.support.convert.basic;

import javax.naming.CompositeName;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringDataSourceConverter implements IConverter<String, DataSource, String> {

	String format = "JTA"; // "JTA or .."
	
	public StringDataSourceConverter() {
	}

	public DataSource convert(String source) throws ConvertException {
		return convert(source, format);
	}

	public DataSource convert(String source, String format) throws ConvertException {
		try {
			if (source.startsWith("java:") && !source.startsWith("java:/")) {
				source = "java:/" + source.substring("java:".length());
			}
			return (DataSource)new InitialContext().lookup(new CompositeName(source));
		} catch (NamingException e) {
			throw new ConvertException(e);
		}
	}

	public String getDefalutFormat() {
		return "JTA";
	}

	public String getFormat() {
		return format;
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<DataSource> getTargetType() {
		return DataSource.class;
	}

	public String restore(DataSource target) throws ConvertException {
		throw new ConvertException("not implemented");
	}

	public String restore(DataSource target, String format) throws ConvertException {
		throw new ConvertException("not implemented");
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String parseFormat(String s) throws ConvertException {
		return s;
	}


}
