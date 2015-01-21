package com.easyservice.xml;

import com.easyservice.exception.SuperException;

public class XMLParseException extends SuperException {

	public XMLParseException() {
	}

	public XMLParseException(String message) {
		super(message);
	}

	public XMLParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
