package com.easyservice.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public interface IMember<T> {

	public static Class[] nullItemTypes = {};
	
	public abstract Annotation[] getAnnotations();

	public abstract <A extends Annotation> A getAnnotation(Class<A> annotationType);

	public abstract Class<T> getType();

	public abstract T getValue(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

	public abstract void setValue(Object obj, T v) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

	public abstract Class[] getItemTypes();

	public abstract String getName();
	
	public boolean isReadOnly();
	
	public abstract Class getDeclaringClass();
}
