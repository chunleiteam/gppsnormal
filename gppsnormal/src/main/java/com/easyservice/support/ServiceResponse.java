package com.easyservice.support;

public class ServiceResponse<T> {
	
	public enum ExceptionType {
		ET_SE_START,
		ET_SE_PROTOCOL_NOT_SUPPORT,
		ET_SE_BINDING_NOT_SUPPORT,
		ET_SE_SERVICE_NOT_FOUND,
		ET_SE_NO_PERMISSION,
		ET_SE_CLASS_NOT_FOUND,
		ET_SE_METHOD_NOT_FOUND,
		ET_SE_WRONG_PARAMTYPE,
		ET_SE_BADREQ,
		ET_SE_METHOD_NORMAL_EXCEPTION,
		ET_SE_INNER_ERROR,
		ET_SE_CONNECTION_ERROR,
		ET_SE_END,
		ET_CE_START,
		ET_CE_INNER_ERROR,
		ET_CE_CONNECTION_ERROR,
		ET_CE_SERVER_DOWN,
		ET_CE_END,
		UNKNOWN
	};
	String id;
	T result;
	Throwable exception;
	ExceptionType exceptionType;
	
	public Throwable getException() {
		return exception;
	}
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	public T getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}
	public ExceptionType getExceptionType() {
		return exceptionType;
	}
	public void setExceptionType(ExceptionType exceptionType) {
		this.exceptionType = exceptionType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
