package com.easyservice.support.convert.basic;

import java.io.FileInputStream;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;


public class FileByteConverter implements IConverter<byte[], String,Object> {

	public byte[] restore(String source) throws ConvertException {
		if (source == null || source.length() == 0){
			return null;
		}
		try{
			FileInputStream f = new FileInputStream(source);
			byte[] rt = new byte[f.available()];
			f.read(rt);
			f.close();
			return rt;
		}catch (Exception e) {
			throw new ConvertException("can not read file : "+source);
		}
		
	}

	public byte[] restore(String source, Object format) throws ConvertException {
		return restore(source);
	}

	public Object getDefalutFormat() {
		return null;
	}

	public Object getFormat() {
		return null;
	}

	public Class<String> getTargetType() {
		return String.class;
	}

	public Class<byte[]> getSourceType() {
		return byte[].class;
	}

	public Object parseFormat(String s) throws ConvertException {
		return null;
	}

	public String convert(byte[] target) throws ConvertException {
		return null;
	}

	public String convert(byte[] target, Object format) throws ConvertException {
		return null;
	}

	public void setFormat(Object format) {
	}
	
}
