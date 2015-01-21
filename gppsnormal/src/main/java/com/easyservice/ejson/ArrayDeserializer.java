package com.easyservice.ejson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArrayDeserializer extends AbstractDeserializer<Object> {

	static final Map<Character,Class> ARRAY_CODE_CLASS = new HashMap<Character, Class>();
	
	{
		ARRAY_CODE_CLASS.put('I', int.class);
		ARRAY_CODE_CLASS.put('B', byte.class);
		ARRAY_CODE_CLASS.put('C', char.class);
		ARRAY_CODE_CLASS.put('J', long.class);
		ARRAY_CODE_CLASS.put('S', short.class);
		ARRAY_CODE_CLASS.put('F', float.class);
		ARRAY_CODE_CLASS.put('D', double.class);
		
	}
	
	public Object deserialize(EJsonReader tokener, Class cls) {
		
		try{
			
			tokener.match('[',true);
			
			Class childType =  cls.getComponentType();
			
			int next = tokener.LA(1, true);
			
			if(next == ']'){ //empty array
				tokener.match(']',true);
				return getArray(cls,0);
			}
			ArrayList list = new ArrayList();
			while(true){
				Object arrayElement = tokener.read(childType, null);
				list.add(arrayElement);
				next = tokener.LA(1, true);
				if(next==','){
					tokener.match(',',true);
					continue;
				}else if(next== ']'){
					tokener.match(']',true);
					break ;
				}else{
					throw new EJSONDeserializeException("unexpected array element");
				}	
			}
			Object array = getArray(cls, list.size());
			int index = 0 ;
			for(Object o : list){
				Array.set(array, index++, o);
			}
			return array;
		}catch (Exception e) {
			if (e instanceof EJsonException){
				throw (EJsonException)e;
			}
			throw new EJSONDeserializeException(e);
		}
		
	}
	
	private Object getArray(Class cls, int size) throws NegativeArraySizeException, ClassNotFoundException {
		return Array.newInstance(cls.getComponentType(), size);
	}

	public Object deserialize(EJsonReader tokener) {
		return deserialize(tokener, Object[].class);
	}

	public Object deserialize(EJsonReader tokener, Class cls,
			Class... itemTypes) {
		return deserialize(tokener, cls);
	}
	
	public static void main(String[] args) {
		System.out.println(String[][].class.getName());
		System.out.println(Object[][].class.getName());
		Class s2Class = int[][].class;
		System.out.println(s2Class.getComponentType());;
		System.out.println(s2Class.getComponentType().getComponentType());;
		System.out.print(s2Class.getComponentType().getComponentType().isArray());
	}

}
