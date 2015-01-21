package com.easyservice.ejson;

import java.util.Collection;
import java.util.IdentityHashMap;

import com.easyservice.reflect.IMember;
import com.easyservice.support.ClassInfo;

/**
 	* simple :
 			{
	 * 		"$TYPE":	"com.cnebula.jsj.demo.Card" ,
	 * 		 "id" : "card001"
	 * 		 "type" : "library"
	 * 		 "status": 1
	 * 
	 * object ref
	 * {
	 * 		"$TYPE":	"com.cnebula.jsj.demo.Card" ,
	 * 		"$REFID":   1
	 * 
	 * 	} 
 *
 */
public class ObjectSerializer extends AbstractSerializer<Object>{
	
	
	public String serialize(Object o, IdentityHashMap<Object, Integer> refMap) {
		try{
			if (o == null){
				return EJsonConsts.EJ_NULL_VALUE;
			}
			//prepare type
			StringBuilder sb = new StringBuilder("{").append(EJsonConsts.TYPE_STRING).append(": \"");
			sb.append(o.getClass().getName());
			
			if(refMap.containsKey(o)){
				sb.append("\",").append(EJsonConsts.REFID_STRING).append(":");
				sb.append(refMap.get(o));
				sb.append("}");
			}else{
				Integer id = refMap.size();
				refMap.put(o, id);
				sb.append("\",").append(EJsonConsts.ID_STRING).append(":");
				sb.append(id);
				sb.append(",");
				//preapare value
				ClassInfo clsInfo = EJsonManager.getClassInfo(o.getClass());
				for(IMember member : (Collection<IMember>)clsInfo.getProperties()){
					Object po = member.getValue(o);
					if(!member.getType().isPrimitive()&&po==null){ // igonre null object
						continue ; 
					}
					if(EJsonConsts.isJSExclude(member.getName())){
						sb.append("_");
						
					}
					sb.append(member.getName()).append(":");
					sb.append(_serialize0(po, refMap));
					sb.append(",");
				}
				if(sb.substring(sb.length()-1, sb.length()).equals(",")){
					sb.deleteCharAt(sb.length()-1);
				}
				sb.append("}");
			}
			return sb.toString() ; 
		}catch (Exception e) {
			if (e instanceof EJSONSerializeException) {
				throw (EJSONSerializeException)e;
			}
			throw new EJSONSerializeException(e);
		}
		
	}
	
	private String _serialize0(Object o, IdentityHashMap<Object, Integer> refMap) {
		
		IEJsonSerializer serializer = manager.getSerializer(o.getClass());
		if(serializer == null){
			throw new EJsonException("can't find serializer for class "+o.getClass());
		}
		return serializer.serialize(o, refMap);
	}
	
}
