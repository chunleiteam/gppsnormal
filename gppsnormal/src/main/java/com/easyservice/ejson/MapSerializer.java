package com.easyservice.ejson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
/**
 * may be should be adjusted for support of key is a complex object
 * and the problem of the null key
 * {
 * 	  "$TYPE" : "java.util.HashMap" ,
 *    "$VALUE" : {
 *    	"k1" : "TOM"
 *    	"K2" : "GREEN"
 *    }
 * }
 *
 */
public class MapSerializer extends AbstractSerializer<Map>{

	final static Map<Class, String> STARTMAP = new  HashMap<Class, String>();  
	final static String END = "}" ;
	public static final String START = "{ " + EJsonConsts.TYPE_STRING + ": \"" ; 
	
	public String serialize(Map t, IdentityHashMap<Object, Integer> refMap) {
		
		StringBuilder sb = new StringBuilder("{");
		sb.append(EJsonConsts.TYPE_STRING);
		sb.append(": \"");
		sb.append(t.getClass().getName());
		
		if(refMap.containsKey(t)){
			sb.append("\",").append(EJsonConsts.REFID_STRING).append(":");
			sb.append(refMap.get(t));
			sb.append("}");
		}else{
			Integer id = refMap.size();
			refMap.put(t, id);
			sb.append("\",").append(EJsonConsts.ID_STRING).append(":");
			sb.append(id);
			
			Set<Entry> entrys = (Set<Entry>)t.entrySet() ;
//			if(isSimpleKey(t)){ // not use now
//			for(Entry entry : (Set<Entry>)entrys){
//				builder.append(",");
//				Object key = entry.getKey();
//				if(key!=null){ // should ingore null key ?
//					builder.append(manager.getSerializer(key.getClass()).serialize(key));
//				}else{
//					builder.append("_").append(EJsonConsts.EJ_NULL_VALUE);
//				}
//				builder.append(":");
//				Object value = entry.getValue();
//				if(value!=null){
//					builder.append(manager.getSerializer(value.getClass()).serialize(value));
//				}else{
//					builder.append("_").append(EJsonConsts.EJ_NULL_VALUE);
//				}
//			}
//		}else{ // complex object
			sb.append(",").append(EJsonConsts.MAP_TABLE_STRING).append(": [");
			int index = 0 , length = entrys.size() ; 
			for(Entry entry : (Set<Entry>)entrys){
				sb.append("{").append(EJsonConsts.MAP_KEY_STRING).append(":");
				Object key = entry.getKey();
				if(key!=null){ // should ingore null key ?
					sb.append(manager.getSerializer(key.getClass()).serialize(key, refMap));
				}else{
					sb.append(EJsonConsts.EJ_NULL_VALUE);
				}
				sb.append(",").append(EJsonConsts.MAP_VALUE_STRING).append(":");
				Object value = entry.getValue();
				if(value!=null){
					sb.append(manager.getSerializer(value.getClass()).serialize(value, refMap));
				}else{
					sb.append(EJsonConsts.EJ_NULL_VALUE);
				}
				if(length != ++index){
					sb.append("},");
				}else{
					sb.append("}");
				}
				
			}
			sb.append("]");
//		}
			sb.append("}");
		}
		return sb.toString();
	}
	
//	public String serializeWithStyle(Map t) {
//		StringBuilder builder ;
//		ObjectRefMap objectRefMap = EJsonManager.getObjectRefMap();
//		if(STARTMAP.containsKey(t.getClass())){
//			appendWithLevel(sb, s, objectRefMap.getCurrentLevel(), appendRowEnd);
//			builder  = new StringBuilder(HashMap_START);
//		}else{
//			builder =  new StringBuilder("{\"$TYPE\":\"");
//			builder.append(t.getClass());
//			builder.append("\"");
//		}
//		IEJsonIOManager manager = EJsonManager.getManager();
//		Set<Entry> entrys = (Set<Entry>)t.entrySet() ; 
//		if(isSimpleKey(t)){ // not use now
//			for(Entry entry : (Set<Entry>)entrys){
//				builder.append(",");
//				Object key = entry.getKey();
//				if(key!=null){ // should ingore null key ?
//					builder.append(manager.getSerializer(key.getClass()).serialize(key));
//				}else{
//					builder.append("_").append(EJsonConsts.EJ_NULL_VALUE);
//				}
//				builder.append(":");
//				Object value = entry.getValue();
//				if(value!=null){
//					builder.append(manager.getSerializer(value.getClass()).serialize(value));
//				}else{
//					builder.append("_").append(EJsonConsts.EJ_NULL_VALUE);
//				}
//			}
//		}else{ // complex object
//			builder.append(",$VALUE = [");
//			int index = 0 , length = entrys.size() ; 
//			for(Entry entry : (Set<Entry>)entrys){
//				builder.append("[");
//				Object key = entry.getKey();
//				if(key!=null){ // should ingore null key ?
//					builder.append(manager.getSerializer(key.getClass()).serialize(key));
//				}else{
//					builder.append("_").append(EJsonConsts.EJ_NULL_VALUE);
//				}
//				builder.append(",");
//				Object value = entry.getValue();
//				if(value!=null){
//					builder.append(manager.getSerializer(value.getClass()).serialize(value));
//				}else{
//					builder.append("_").append(EJsonConsts.EJ_NULL_VALUE);
//				}
//				if(length != ++index){
//					builder.append("],");
//				}else{
//					builder.append("]");
//				}
//				
//			}
//			builder.append("]");
//		}
//		builder.append(END);
//		return builder.toString();
//	}
	
//	private void appendWithLevel(StringBuilder sb, String s , int level, boolean appendRowEnd){
//		if(level!=-1){
//			for(int i=0 ; i < level ; i++){
//				sb.append("\t");
//			}
//		}
//		sb.append(s);
//		if(appendRowEnd){
//			sb.append("\n");
//		}
//	}
//
//	private boolean isSimpleKey(Map t) {
//		return false;
//	}

}
