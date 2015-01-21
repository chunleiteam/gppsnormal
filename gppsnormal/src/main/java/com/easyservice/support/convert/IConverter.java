package com.easyservice.support.convert;

public interface IConverter<S, T, F> {
	
	public Class<S> getSourceType();
	public Class<T> getTargetType();
	public T convert(S source) throws ConvertException;
	public S restore(T target) throws ConvertException;
	public T convert(S source, F format) throws ConvertException;
	public S restore(T target, F format) throws ConvertException;
	public void setFormat(F format);
	public F parseFormat(String s) throws ConvertException;
	public F getFormat();
	public F getDefalutFormat();
	
}
