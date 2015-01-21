package com.easyservice.support.convert.basic;

import java.math.BigInteger;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringBigIntegerConverter implements IConverter<String, BigInteger, Integer> {

	static int defaultFormat = 10;
	int format = defaultFormat;
	
	public BigInteger convert(String source) throws ConvertException {
		return convert(source, format);
	}

	public BigInteger convert(String source, Integer format) throws ConvertException {
		try {
			return new BigInteger(source, format);
		} catch (Throwable e) {
			throw new ConvertException(e);
		}
	}

	public Integer getDefalutFormat() {
		return defaultFormat;
	}

	public Integer getFormat() {
		return format;
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<BigInteger> getTargetType() {
		return BigInteger.class;
	}

	public String restore(BigInteger target) throws ConvertException {
		return restore(target, format);
	}

	public String restore(BigInteger target, Integer format) throws ConvertException {
		return target.toString(format);
	}

	public void setFormat(Integer format) {
		this.format = format;
	}

	public Integer parseFormat(String s) throws ConvertException {
		return Integer.parseInt(s);
	}

}
