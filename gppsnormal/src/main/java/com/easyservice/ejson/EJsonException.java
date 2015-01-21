package com.easyservice.ejson;

public class EJsonException extends RuntimeException {

	public EJsonException() {
		super();
	}

	public EJsonException(String message, Throwable cause) {
		super(message, cause);
	}

	public EJsonException(String message) {
		super(message);
	}

	public EJsonException(Throwable cause) {
		super(cause);
	}

}
