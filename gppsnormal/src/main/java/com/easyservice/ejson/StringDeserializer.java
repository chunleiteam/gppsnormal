package com.easyservice.ejson;

public class StringDeserializer extends AbstractDeserializer<String> {

	public String deserialize(EJsonReader tokener, Class cls) {
		return tokener.readNextQuoteString();
	}

	public String deserialize(EJsonReader tokener) {
		return tokener.readNextQuoteString();
	}

	public String deserialize(EJsonReader tokener, Class cls,
			Class... itemTypes) {
		return tokener.readNextQuoteString();
	}

}
