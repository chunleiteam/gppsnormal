package com.easyservice.support.convert.basic;

import java.math.BigDecimal;
import java.math.MathContext;

import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.IConverter;

public class StringBigDecimalConverter implements IConverter<String, BigDecimal, MathContext> {

	static MathContext defaultFormat = new MathContext(0);
	MathContext format = defaultFormat;
	
	public BigDecimal convert(String source) throws ConvertException {
		return convert(source, format);
	}

	public BigDecimal convert(String source, MathContext format) throws ConvertException {
		try {
			return new BigDecimal (source, format);
		} catch (Throwable e) {
			throw new ConvertException(e);
		}
	}

	public MathContext getDefalutFormat() {
		return defaultFormat;
	}

	public MathContext getFormat() {
		return format;
	}

	public Class<String> getSourceType() {
		return String.class;
	}

	public Class<BigDecimal> getTargetType() {
		return BigDecimal.class;
	}

	public String restore(BigDecimal target) throws ConvertException {
		return restore(target, format);
	}

	public String restore(BigDecimal target, MathContext format) throws ConvertException {
		return target.toPlainString();
	}

	public void setFormat(MathContext format) {
		this.format = format;
	}

	public MathContext parseFormat(String s) throws ConvertException {
		return new MathContext(Integer.parseInt(s));
	}

}
