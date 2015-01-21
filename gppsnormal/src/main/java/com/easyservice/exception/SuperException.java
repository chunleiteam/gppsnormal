package com.easyservice.exception;

import com.easyservice.support.ServiceResponse.ExceptionType;

public class SuperException extends Exception{
	private Throwable throwable;
	private ExceptionType type;
	public SuperException()
	{
		
	}
	public SuperException(String message)
	{
		super(message);
	}
	public SuperException(String message,Throwable e)
	{
		super(message);
		this.throwable=e;
	}
	public SuperException(Throwable e,ExceptionType type)
	{
		super(e);
		this.throwable=e;
		this.type=type;
	}
	public ExceptionType getType() {
		return type;
	}
	public void setType(ExceptionType type) {
		this.type = type;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
}
