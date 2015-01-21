package com.easyservice.ejson;

public class BooleanDeserializer extends AbstractDeserializer<Boolean>{

	public Boolean deserialize(EJsonReader tokener) {
		int first = tokener.readNext(true);
		if(first == -1){
			throw new EJSONDeserializeException("unexpected end");
		}
		char c = (char) first ; 
		if(c=='t'){
			readAndexpect('r', tokener);
			readAndexpect('u', tokener);
			readAndexpect('e', tokener);
			return Boolean.TRUE;
		}else if(c == 'f'){
			readAndexpect('a', tokener);
			readAndexpect('l', tokener);
			readAndexpect('s', tokener);
			readAndexpect('e', tokener);
			return Boolean.FALSE;
		}else{
			throw new EJSONDeserializeException("unexpected end");
		}
	}

	public Boolean deserialize(EJsonReader tokener, Class cls) {
		return deserialize(tokener);
	}

	public Boolean deserialize(EJsonReader tokener, Class cls,
			Class... itemTypes) {
		return deserialize(tokener);
	}
	
	private void readAndexpect(char expect , EJsonReader tokener){
		int c1 = tokener.readNext(false);
		if(expect!=(char)c1){
			throw new EJSONDeserializeException("unexpected end");
		}
	}

}
