package com.easyservice.exception;

import com.easyservice.support.ServiceResponse.ExceptionType;

public class PermissionException extends SuperException{
	public PermissionException()
	{
		super();
	}
	public PermissionException(String message)
	{
		super(message);
	}
	public PermissionException(Throwable e,ExceptionType type)
	{
		super(e, type);
	}
}
