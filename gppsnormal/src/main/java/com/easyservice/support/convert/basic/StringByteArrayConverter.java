package com.easyservice.support.convert.basic;

import java.io.UnsupportedEncodingException;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;
import com.easyservice.utils.StringEnDeCoder;

public class StringByteArrayConverter implements IConverter<String, byte[], String> {

	
	public byte[] convert(String source) throws ConvertException {
		return convert(source, null);
	}

	public byte[] convert(String source, String format) throws ConvertException {
		if (source == null){
			return null;
		}
		try {
			return StringEnDeCoder.Decode(StringEnDeCoder.CT_BASE64, source);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
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

	public Class<byte[]> getTargetType() {
		return byte[].class;
	}

	public String parseFormat(String s) throws ConvertException {
		return null;
	}

	public String restore(byte[] target) throws ConvertException {
		return restore(target, null);
	}

	public String restore(byte[] target, String format) throws ConvertException {
		if (target == null){
			return null;
		}
		if (target.length == 0){
			return "";
		}
		try {
			return StringEnDeCoder.Encode(StringEnDeCoder.CT_BASE64, target).toString();
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public void setFormat(String format) {
	}

}
