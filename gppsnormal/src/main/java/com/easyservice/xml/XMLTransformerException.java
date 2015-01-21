package com.easyservice.xml;

import org.xml.sax.SAXException;

public class XMLTransformerException extends SAXException {

	int line;
	int col;
	
	public XMLTransformerException() {
	}

	public XMLTransformerException(String message, int line, int col) {
		super(message);
		this.line = line;
		this.col = col;
	}

	public XMLTransformerException(Exception e) {
		super(e);
	}
	
	public XMLTransformerException(Exception e, int line, int col) {
		super(e);
		this.line = line;
		this.col = col;
	}

	public XMLTransformerException(String message, Exception e) {
		super(message, e);
	}
	
	public XMLTransformerException(String message, Exception e, int line, int col) {
		super(message, e);
		this.line = line;
		this.col = col;
	}
	
	@Override
	public String getMessage() {
		return "at line: " + line + ", column: " + col + ", " + super.getMessage() ;
	}
	
	@Override
	public String toString() {
		return getMessage();
	}

}
