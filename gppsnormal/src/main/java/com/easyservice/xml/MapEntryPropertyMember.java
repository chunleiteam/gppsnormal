package com.easyservice.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import com.easyservice.reflect.IMember;

public class MapEntryPropertyMember<T> implements IMember<T> {

	Class<T> type;
	IMember<T> delegate;
	String name;
	
	
	public MapEntryPropertyMember(String name, Class<T> type, IMember<T> delegate) {
		super();
		this.name = name;
		this.type = type;
		this.delegate = delegate;
	}

	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return delegate.getAnnotation(annotationType);
	}

	public Annotation[] getAnnotations() {
		return delegate.getAnnotations();
	}

	public Class getDeclaringClass() {
		return delegate.getDeclaringClass();
	}

	public Class[] getItemTypes() {
		return delegate.getItemTypes();
	}

	public String getName() {
		return name;
	}

	public Class<T> getType() {
		return type;
	}

	public T getValue(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return delegate.getValue(obj);
	}

	public void setValue(Object obj, T v) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		delegate.setValue(obj, v);
	}

	public boolean isReadOnly() {
		return false;
	}



}
