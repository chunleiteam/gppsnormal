package com.easyservice.support;

import java.lang.reflect.Method;

public class ServiceRequest<T> {
	
//	Object proxy;
	Class<T> interfacClass;
	long beanId;
	HttpParse httpParse;
	Method method;
	Object[] args;
	String target;

	public ServiceRequest()
	{
		
	}
	public ServiceRequest(Class<T> interfacClass, HttpParse httpParse, Method method, Object[] args, String target) {
		super();
		this.interfacClass = interfacClass;
//		this.beanId = beanId;
		this.httpParse = httpParse;
		this.method = method;
		this.args = args;
		this.target = target;
	}
	public Class<T> getInterfacClass() {
		return interfacClass;
	}
	public void setInterfacClass(Class<T> interfacClass) {
		this.interfacClass = interfacClass;
	}
	public long getBeanId() {
		return beanId;
	}
	public void setBeanId(long beanId) {
		this.beanId = beanId;
	}
	public HttpParse getHttpParse() {
		return httpParse;
	}
	public void setHttpParse(HttpParse httpParse) {
		this.httpParse = httpParse;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public void setAttribute(String name, Object value) {
		httpParse.setRequestAttribute(name, value);
	}
}
