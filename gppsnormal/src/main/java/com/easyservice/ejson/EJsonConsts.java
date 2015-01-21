package com.easyservice.ejson;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 *hierarchy
 */
public class EJsonConsts {
	
	public static final String TYPE_STRING = "_t_";//"$TYPE" ; 
	public static final String VALUE_STRING = "values";//"$VALUES" ; 
	public static final String DATE_VALUE_STRING = "time";//"$VALUES" ; 
	public static final String MAP_TABLE_STRING = "table";//"$VALUES" ; 
	public static final String MAP_KEY_STRING = "key";//"$VALUES" ; 
	public static final String MAP_VALUE_STRING = "value";//"$VALUES" ; 
	public static final String ID_STRING = "_i_";//"$ID" ; 
	public static final String REFID_STRING = "_r_";//"$REFID" ; 
 
	
	public static final String EJ_NULL_VALUE = "null" ; 
	
	
	public static final HashSet<String> JSEXCLUDE = new HashSet<String>();
	
	/**
	 * reserved keyword of javascript and ECMA extension
	 */
	static{
		JSEXCLUDE.add("typeof");
		JSEXCLUDE.add("var");
		JSEXCLUDE.add("with");
		JSEXCLUDE.add("debugger");
		JSEXCLUDE.add("export");
		JSEXCLUDE.add("in");
		//object proeprty
		JSEXCLUDE.add("prototype"); // support by jscript
		JSEXCLUDE.add("constructor");
		
		//object method
		JSEXCLUDE.add("propertyIsEnumerable");
		JSEXCLUDE.add("toLocaleString");
		JSEXCLUDE.add("hasOwnProperty"); 
		JSEXCLUDE.add("isPrototypeOf");
		JSEXCLUDE.add("valueOf");
		JSEXCLUDE.add("toLocaleString");

		JSEXCLUDE.add(REFID_STRING) ; 
		JSEXCLUDE.add(ID_STRING);
		JSEXCLUDE.add(TYPE_STRING) ; 
	}
	
	
	public static boolean isJSExclude(String o){
		return JSEXCLUDE.contains(o);
	}
	
	
}
