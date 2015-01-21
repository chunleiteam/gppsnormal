package com.easyservice.ejson;

import static com.easyservice.ejson.EJsonConsts.ID_STRING;
import static com.easyservice.ejson.EJsonConsts.REFID_STRING;
import static com.easyservice.ejson.EJsonConsts.VALUE_STRING;

import java.util.Collection;
import java.util.IdentityHashMap;
/**
 * support all collection:
 * arraylist 
 * a : { "$TYPE":"ArrayList" ,
 * 		 "$VALUE" :["s1", "s2"]
 * 	   }
 */
public class CollectionSerializer extends AbstractSerializer<Collection> {

	
//	static String LIST_START = "\"$TYPE\":\"List\" , \"$VALUE\" : [" ;
//	static String COLLECTION_START = "\"$TYPE\":\"Collection\" , \"$VALUE\" : [";
//	static String SET_START = "\"$TYPE\":\"Set\" , \"$VALUE\" : [";
//	final static  String ArrayList_START = "{$TYPE:\"java.util.ArrayList\" , $VALUE : [" ;
//	final static  String HashSet_START = "{$TYPE:\"java.util.HashSet\" , $VALUE : [" ;
//	final static Map<Class, String> STARTMAP = new  HashMap<Class, String>();  
	public static final String START = "{ " + EJsonConsts.TYPE_STRING+ ": \"" ; 
//	final static String END = "]}" ;
//	static {
//		STARTMAP.put(ArrayList.class, ArrayList_START);
//		STARTMAP.put(HashSet.class, HashSet_START);
//	}
	
	
	public String serialize(Collection t, IdentityHashMap<Object, Integer> refMap) {
		
		StringBuilder sb = new StringBuilder(START);
		sb.append(t.getClass().getName());
		
		if(refMap.containsKey(t)){ //$REFID:
			sb.append("\",").append(REFID_STRING).append(":");
			sb.append(refMap.get(t));
			sb.append("}");
		}else{
			Integer id = refMap.size();
			refMap.put(t, id);
			sb.append("\",").append(ID_STRING).append(":");
			sb.append(id);
			sb.append(",").append(VALUE_STRING).append(":[");
			int index = 1 ;
			for(Object o : t){
				if (o == null){
					sb.append("null");
				}else {
					IEJsonSerializer child = manager.getSerializer(o.getClass());
					sb.append(child.serialize(o, refMap));
				}
				
				if(index++!=t.size()){
					sb.append(",");
				}
			}
			sb.append("]}");
		}
		return sb.toString();
	} 
	
//	public String getStart(String type){
//		StringBuilder sb = new StringBuilder("{$TYPE:\"");
//		sb.append(type);
//		sb.append("\" , $VALUE : [");
//		return sb.toString();
//	}
	
	

}
