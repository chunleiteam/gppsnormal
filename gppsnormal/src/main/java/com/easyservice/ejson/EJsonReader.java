package com.easyservice.ejson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public class EJsonReader {
	
	static char[] SPACE_CHARS = new char[]{' ','\n','\r','\t'};
	
	protected String tokendString ;
	
	protected int _index ;
	
	protected int length ; 
	
	protected int markedIndex ;
	
	protected boolean marked = false;
	
	protected Map<Integer, Object> referenceMap = new HashMap<Integer, Object>();
	
	protected IEJsonIOManager manager = EJsonManager.getDefaultManager();
	
	public EJsonReader(String string) {
		this.tokendString = string;
		this.length = tokendString.length();
	}
	
	

	//greedy
	public String readNextNumber(){
		int first = LA(1,true);
		if(first == -1){
			throw new EJSONDeserializeException();
		}
		char c = (char) first ; 
		boolean hasPointLeftDigit = false;
		boolean pointLeft = true;
		boolean hasPointRightDigit = false;
		boolean eLeft =true; 
		boolean hasERightDigit = false;
		if(Character.isDigit(c)){
			hasPointLeftDigit = true;
		}else if(c=='-'){
		}else{
			throw new EJSONDeserializeException();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(c);
		match(c, true);
		int cur ; 
		while(true){
			cur = LA(1,false);
			if(first == -1){
				if(validNumberState(hasPointLeftDigit, pointLeft, hasPointRightDigit, eLeft, hasERightDigit)){
					return sb.toString();
				}
			}else if(Character.isDigit((char)cur)){
				match((char)cur,false);
				sb.append((char)cur);
				if(!eLeft){
					hasERightDigit = true;
				}else if(!pointLeft){
					hasPointRightDigit = true;
				}else{
					hasPointLeftDigit = true;
				}
			}else if('.' == (char)cur){
				if(!pointLeft||!eLeft){
					if(validNumberState(hasPointLeftDigit, pointLeft, hasPointRightDigit, eLeft, hasERightDigit)){
						return sb.toString();
					}
					throw new EJSONDeserializeException();
				}else if(hasPointLeftDigit){
					pointLeft = false;
					sb.append((char)cur);
					match((char)cur, false);
				}
				
			}else if('e'==(char)cur || 'E' ==(char) cur){
				//2.e, 123e
				if((!eLeft&& !hasERightDigit)||(eLeft && !pointLeft && !hasPointRightDigit)||
						(!hasPointLeftDigit)){
					throw new EJSONDeserializeException();
				}else if(!eLeft){
					if(validNumberState(hasPointLeftDigit, pointLeft, hasPointRightDigit, eLeft, hasERightDigit)){
						return sb.toString();
					}
					//get the number
				}else{//
					eLeft = false;
					match((char)cur, false);
					sb.append((char)cur);
				}
			}else{ //other char
				if(validNumberState(hasPointLeftDigit, pointLeft, hasPointRightDigit, eLeft, hasERightDigit)){
					return sb.toString();
				}
			}
		}
	}
	
	private boolean validNumberState(boolean hasPointLeftDigit, boolean pointLeft, boolean hasPointRightDigit,
			boolean eLeft, boolean hasERightDigit){
		return (!eLeft && hasERightDigit) || (eLeft && !pointLeft && hasPointRightDigit) || 
		(pointLeft && hasPointLeftDigit);
	}
	
	public String readNextQuoteString() {
		match('\"', true);
		StringBuilder sb = new StringBuilder();
		int cur = readNext(false);
		while(true){
			if(cur=='\\'){
				cur = readNext(false);
				switch (cur) {
				case '\\':
					sb.append('\\');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 'b':
					sb.append('\b');
					break;
				case '\'':
					sb.append('\'');
					break;
				case '\"':
					sb.append('"');
					break;
				case '/':
					sb.append('/');
					break;
				case 'u': //is a hex control character
					String digit = readNextStringEndBySpace(false);
					char c = (char)Integer.parseInt(digit, 16);
					sb.append(c);
					break;
				default:
					throw new EJSONDeserializeException(((char)cur) + "[code=" + (int)cur + "]" + ":unknown escape char after \\");
				}
				cur = readNext(false);
			}else if(cur==-1){  // unexcepted end
				throw new EJSONDeserializeException();
			}else if(cur == '\"'){
				break;
			}else{
				sb.append((char)cur);
				cur = readNext(false);
			}
		}
		return sb.toString();
	}

	public int readNextInt() {
		return (Integer)manager.getDeserializer(int.class).deserialize(this, Integer.class);
	}
	
	public long readNextLong(){
		return (Long)manager.getDeserializer(long.class).deserialize(this, Long.class);
	}
	
	public int readNext(){
		return readNext(true);
	}
	
	public int readNext(boolean filterSpace){
		int result = LA(1,filterSpace);
		if(result!=-1){
			match((char) result, filterSpace);
			return result;
		}
		return -1;
	}
	
	
	/**
	 * read next string end by endchar
	 */
	public String readNextString(int endChar){
		return readNextString(endChar, true);
	}
	
	public String readNextString(int endChar,boolean filterLeftSpace){
		if(endChar==-1){
			if(!filterLeftSpace){
				return tokendString.substring(_index);
			}else{
				int i = _index ;
				while(i<tokendString.length()){
					if(!Character.isWhitespace(tokendString.charAt(i))){
						break;
					}
					i++ ;
				}
				if(i==tokendString.length()){
					return null ;
				}else{
					return tokendString.substring(i);
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		boolean match = false; 
		boolean quoteStart = false;
		while(_index < length){
			char cur = tokendString.charAt(_index);
			boolean isSpace = Character.isWhitespace(cur);
			_index ++ ; 
			if(!(filterLeftSpace && !quoteStart && isSpace)){
				if(filterLeftSpace && !quoteStart && !isSpace){
					quoteStart = true ; 
				}
				if(cur == (char)endChar){ //match
					match = true;
					break ; 
				}else{
					sb.append(cur);
				}
			}
		}
		if(match){
			return sb.toString();
		}else if(sb.length()==0){
			return null;
		}
		throw new EJSONDeserializeException("no match"); 
	}
	
	
	public String readNextStringEndBySpace(boolean filterLeftSpace){
		StringBuilder sb = new StringBuilder();
		boolean quoteStart = false;
		while(_index < length){
			char cur = tokendString.charAt(_index);
			_index ++ ; 
			if(Character.isWhitespace(cur)){
				if(!filterLeftSpace||quoteStart){
					break;
				}
			}else{
				if(!quoteStart&&filterLeftSpace){
					quoteStart = true;
				}
				sb.append(cur);
			}
		}
		if(sb.length()==0){
			return null;
		}
		return sb.toString();
	}
	
	public String readNextStringEndBySpace(){
		return readNextStringEndBySpace(true);
	}
	
	public  Object readNull(){
		match("null");
		return null;
	}
	
	public void match(String str){
		int i = 0;
		try{
			for (i = 0; i < str.length(); i++){
				match(str.charAt(i));
			}
		}catch (EJSONDeserializeException e) {
			String meet = "";
			if (i > 0) {
				meet += str.substring(0, i);
			}
			meet += tokendString.charAt(_index);
			throw new EJSONDeserializeException("expect: " + str + ", but meet :" + meet);
		}

	}
	
	public void match(char c){
		match(c, true);
	}
	
	public void match(char c, boolean filterSpace){
		while(_index < tokendString.length()){
			char cur = tokendString.charAt(_index);
			if(Character.isWhitespace(cur)){
				if(filterSpace){
					_index++ ; 
				}else if(cur == c){
					_index++ ; 
					return ;
				}else{
					throw new EJSONDeserializeException();
				}
			}else if(cur==c){
				_index++;
				return ;
			}else{
				throw new EJSONDeserializeException(cur+" no match "+c);
			}
		}
		if(_index >= tokendString.length()){
			throw new EJSONDeserializeException();
		}
	}
	
	public int LA(int i,boolean filterSpace){
		int count = 0 ;
		for(int si=_index ; si<tokendString.length(); si++){
			if(!filterSpace||!Character.isWhitespace(tokendString.charAt(si))){
				if(++count==i){
					return tokendString.charAt(si);
				}
			}
		}
		return -1 ; 
	}
	
	public int LA(int i){
		return LA(i,true);
	}
	
	public void mark(){
		markedIndex = _index;
		marked = true;
	}
	
	public boolean isMarked(){
		return marked;
	}
	
	public void reset(){
		marked = false;
	}
	
	public void rewind(){
		if(marked){
			_index = markedIndex ;
		}else{
			throw new EJsonException("not marked");
		}
		
	}
	
	public  <T> T[] readArray(Class cls){
		return (T[]) manager.getArrayDeserializer().deserialize(this, cls);
	}
	
	public  <T> T[] readArray(){
		return (T[]) manager.getArrayDeserializer().deserialize(this);
	}
	
	public  <T> T readObject(Class ... itemTypes){
		return (T) ComplexDeserializerProxy.deserialize(this, itemTypes);
	}
	
//	public static <T> T readObject(EJsonTokener tokener){
//		return (T) ComplexDeserializerProxy.deserialize(tokener);
//	}
	
	public  <T> T readNumber(Class cls){
		return (T) manager.getNumberDeserializer().deserialize(this, cls);
	}
	
	
	
	public  <T> T readNumber(){
		return (T) manager.getNumberDeserializer().deserialize(this);
	}
	
	public  <T> T readString(){
		return (T) manager.getStringDeserializer().deserialize(this);
	}
	
	public  <T> T read(Class cls, Class[] itemTypes){
		int i = LA(1, true);
		if (i == 'n'){
			return (T)readNull();
		}
		IEJsonDeserializer des = manager.getDeserializer(cls);
		if(des.getClass()!=ObjectDeserializer.class && !Collection.class.isAssignableFrom(cls)&&
				!Set.class.isAssignableFrom(cls)){ //if cls is simple type, date, string or array
			return (T) des.deserialize(this, cls, itemTypes);
		}else{
			return (T) read(itemTypes);
		}
	}

	public Object read(Class ... itemTypes) {
		int i = LA(1, true);
		if(i==-1){
			throw new EJSONDeserializeException("unexcepted end") ; 
		}
		char c = (char) i ;
		if('\"' == c){
			return readString();
		}else if('[' == c){
			return readArray();
		}else if('{' == c){
			return readObject(itemTypes);
		}else if(isDigitStart(c)){
			return readNumber(null);
		}else if (c == 'n'){
			return readNull();
		}else {
			throw new EJSONDeserializeException("unknown literal : " + c + " at " + _index);
		}
	}
	
	private static boolean isDigitStart(char c){
		return Character.isDigit(c)||c=='-';
	}
	
//	public boolean isSpace(int c){
//		for(int i=0; i<4; i++){
//			if(SPACE_CHARS[i]==c){
//				return true;
//			}
//		}
//		return false;
//	}
	class Number{
		String s;
		boolean isInteger ; 
	}

	public Map<Integer, Object> getReferenceMap() {
		return referenceMap;
	}



	public void setReferenceMap(Map<Integer, Object> referenceMap) {
		this.referenceMap = referenceMap;
	}



	public IEJsonIOManager getManager() {
		return manager;
	}



	public void setManager(IEJsonIOManager manager) {
		this.manager = manager;
	}
	
}
