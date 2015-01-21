package com.easyservice.xml;

public class StringTools {
	public static String upperCaseFirstChar(String s){
		if (s.length() > 1){
			s = s.substring(0, 1).toUpperCase() + s.substring(1);
		}else{
			s = s.substring(0, 1).toUpperCase();
		}
		return s;
	}
	
	public static String lowerCaseFirstChar(String s){
		if (s.length() > 1){
			s = s.substring(0, 1).toLowerCase() + s.substring(1);
		}else{
			s = s.substring(0, 1).toLowerCase();
		}
		return s;
	}
}
