package com.easyservice.ejson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.easyservice.support.ClassInfo;
import com.easyservice.support.ClassInfoPool;


public class EJsonManager implements IEJsonIOManager{
	
	static ClassInfoPool classInfoPool  = new ClassInfoPool();
	static EJsonManager defaultManager = new EJsonManager();
	
//	private static ThreadLocal<ObjectRefMap> threadLocalObjectRefMap = new ThreadLocal<ObjectRefMap>();
	
//	private static ThreadLocal<ObjectDeserRefMap> threadLocalObjectDesRefMap = new ThreadLocal<ObjectDeserRefMap>();
	
//	private static ThreadLocal<EJsonManager> threadLocalManager = new ThreadLocal<EJsonManager>();
	
	private List<IEJsonIOManager> managerList = new ArrayList<IEJsonIOManager>();
	
	static Map<Class, IEJsonSerializer>  defaultSerializerMap = new HashMap<Class, IEJsonSerializer>();
	Map<Class, IEJsonSerializer> type2SerializerMap = new HashMap<Class, IEJsonSerializer>();
	
	static NumberSerializer numberSerializer  = new NumberSerializer(); 
	static DateSerializer dateSerializer = new DateSerializer();
	static CollectionSerializer collectionSerializer = new CollectionSerializer();
	static MapSerializer mapSerializer = new MapSerializer();
	static StringSerializer stringSerializer = new StringSerializer();
	static ObjectSerializer objectSerializer = new ObjectSerializer();
	static ArraySerializer arraySerializer = new ArraySerializer();
	static ClassSerializer classSerializer = new ClassSerializer();
	static BigDecimalSerializer bigDecimalSerializer = new BigDecimalSerializer();
	
	static Map<Class, IEJsonDeserializer> defaultDeserializerMap = new HashMap<Class, IEJsonDeserializer>();
	Map<Class, IEJsonDeserializer> type2DeserializerMap = new HashMap<Class, IEJsonDeserializer>();
	
	static NumberDeserializer numberDeserializer = new NumberDeserializer();
	static DateDeserializer dateDeserializer = new DateDeserializer();
	static CollcetionDeserializer collcetionDeserializer = new CollcetionDeserializer();
	static MapDeserializer mapDeserializer = new MapDeserializer();
	static StringDeserializer stringDeserializer = new StringDeserializer();
	static ObjectDeserializer objectDeserializer = new ObjectDeserializer();
	static ArrayDeserializer arrayDeserializer = new ArrayDeserializer();
	static BooleanDeserializer booleanDeserializer = new BooleanDeserializer();
	static ClassDeserializer classDeserializer = new ClassDeserializer();
	static BigDecimalDeSerializer bigDecimalDeSerializer = new BigDecimalDeSerializer();
	static EnumIO enumIO = new EnumIO();
	static{
		
		defaultSerializerMap.put(Byte.class, numberSerializer);
		defaultSerializerMap.put(Short.class, numberSerializer);
		defaultSerializerMap.put(Integer.class, numberSerializer);
		defaultSerializerMap.put(Long.class, numberSerializer);
		defaultSerializerMap.put(Character.class, numberSerializer);
		defaultSerializerMap.put(Boolean.class, numberSerializer);
		
		defaultSerializerMap.put(Float.class, numberSerializer);
		defaultSerializerMap.put(Double.class, numberSerializer);
		
		
		defaultSerializerMap.put(byte.class, numberSerializer);
		defaultSerializerMap.put(short.class, numberSerializer);
		defaultSerializerMap.put(int.class, numberSerializer);
		defaultSerializerMap.put(long.class, numberSerializer);
		defaultSerializerMap.put(char.class, numberSerializer);
		defaultSerializerMap.put(boolean.class, numberSerializer);
		
		defaultSerializerMap.put(float.class, numberSerializer);
		defaultSerializerMap.put(double.class, numberSerializer);
		
		
		defaultSerializerMap.put(Date.class, dateSerializer);
		
		defaultSerializerMap.put(String.class, stringSerializer);
		
		defaultSerializerMap.put(Collection.class, collectionSerializer);
		defaultSerializerMap.put(List.class, collectionSerializer);
		defaultSerializerMap.put(Set.class, collectionSerializer);
		defaultSerializerMap.put(Queue.class, collectionSerializer);
		defaultSerializerMap.put(ArrayList.class, collectionSerializer);
		defaultSerializerMap.put(LinkedList.class, collectionSerializer);
		defaultSerializerMap.put(HashSet.class, collectionSerializer);
		
		
		defaultSerializerMap.put(Map.class, mapSerializer);
		defaultSerializerMap.put(HashMap.class, mapSerializer);
		defaultSerializerMap.put(LinkedHashMap.class, mapSerializer);
		defaultSerializerMap.put(Enum.class, enumIO);
		defaultSerializerMap.put(Class.class, classSerializer);
		
		defaultSerializerMap.put(BigDecimal.class, bigDecimalSerializer);
		
		defaultDeserializerMap.put(Enum.class, enumIO);
		defaultDeserializerMap.put(Class.class, classDeserializer);
//		defaultSerializerMap.put(Object.class, objectSerializer);
		
		
		//default deserializer map init
		defaultDeserializerMap.put(Byte.class, numberDeserializer);
		defaultDeserializerMap.put(Short.class, numberDeserializer);
		defaultDeserializerMap.put(Integer.class, numberDeserializer);
		defaultDeserializerMap.put(Long.class, numberDeserializer);
		defaultDeserializerMap.put(Character.class, numberDeserializer);
		defaultDeserializerMap.put(Boolean.class, booleanDeserializer);
		
		defaultDeserializerMap.put(Float.class, numberDeserializer);
		defaultDeserializerMap.put(Double.class, numberDeserializer);
		
		
		defaultDeserializerMap.put(byte.class, numberDeserializer);
		defaultDeserializerMap.put(short.class, numberDeserializer);
		defaultDeserializerMap.put(int.class, numberDeserializer);
		defaultDeserializerMap.put(long.class, numberDeserializer);
		defaultDeserializerMap.put(char.class, numberDeserializer);
		defaultDeserializerMap.put(boolean.class, booleanDeserializer);
		
		defaultDeserializerMap.put(float.class, numberDeserializer);
		defaultDeserializerMap.put(double.class, numberDeserializer);
		
		
		defaultDeserializerMap.put(Date.class, dateDeserializer);
		defaultDeserializerMap.put(String.class, stringDeserializer);
		
		defaultDeserializerMap.put(Collection.class, collcetionDeserializer);
		defaultDeserializerMap.put(List.class, collcetionDeserializer);
		defaultDeserializerMap.put(Set.class, collcetionDeserializer);
		defaultDeserializerMap.put(Queue.class, collcetionDeserializer);
		defaultDeserializerMap.put(ArrayList.class, collcetionDeserializer);
		defaultDeserializerMap.put(LinkedList.class, collcetionDeserializer);
		defaultDeserializerMap.put(HashSet.class, collcetionDeserializer);
		
		
		defaultDeserializerMap.put(Map.class, mapDeserializer);
		defaultDeserializerMap.put(HashMap.class, mapDeserializer);
		defaultDeserializerMap.put(LinkedHashMap.class, mapDeserializer);
		
		defaultDeserializerMap.put(BigDecimal.class, bigDecimalDeSerializer);
		
		defaultDeserializerMap = Collections.unmodifiableMap(defaultDeserializerMap);
		
	}
	
	private EJsonManager() {
	}
	
	/**
	 * which will get the most appropriate serializer for the class.
	 */
	public  IEJsonSerializer getSerializer(Class cls) {
		return (IEJsonSerializer) getSOrD0(cls, true);
	}
	
	public  IEJsonDeserializer getDeserializer(Class cls) {
		return (IEJsonDeserializer) getSOrD0(cls, false);
	}
	
	private Object getSOrD0(Class cls, boolean isSerialize){
		
		Map type2SOrD = isSerialize ? type2SerializerMap : type2DeserializerMap; 
		
		Object result = null ; 
		
		if((result=getExactSOrD(cls, isSerialize))!=null){
			return result ; 
		}
		
		if(cls.isArray()){
			return isSerialize ? arraySerializer : arrayDeserializer;
		}
		
		if(cls == Object.class){
			return isSerialize ? objectSerializer:objectDeserializer;
		}
		
		Class superCls = cls.getSuperclass();
		while(superCls!=Object.class){
			if((result = getExactSOrD(superCls, isSerialize))!=null){
				type2SOrD.put(cls, result);
				return result ;
			}
			 if (superCls == null){
	                break;
	            }
			superCls = superCls.getSuperclass() ; 
		}
		
		Class[] interfaces = cls.getInterfaces();
		if(interfaces ==null || interfaces.length==0){
			return isSerialize ? objectSerializer : objectDeserializer; 
		}
//		Queue<Class> queue = new PriorityQueue<Class>();
//		for(Class c : interfaces){
//			System.out.println(c);
//			queue.offer(c);
//		}
		for (Class interfaze : interfaces) {
			if((result = getExactSOrD(cls, isSerialize))!=null){
				type2SOrD.put(cls, result);
				return result ;
			}
		}
		return isSerialize ? objectSerializer : objectDeserializer; 
	}
	

	public void register(Class cls, IEJsonSerializer serializer) {
		type2SerializerMap.put(cls, serializer);
		serializer.setManager(this);
	}
	
	public void register(Class cls, IEJsonDeserializer deserializer) {
		type2DeserializerMap.put(cls, deserializer);
		deserializer.setManager(this);
	}
	
	

//	public <T> void unRegister(Class<T> cls, IEJsonSerializer<T> serializer) {
//	}

	public void register(IEJsonIOManager manager) {
		managerList.add(manager);
	}
	
	
	
	public static EJsonManager getDefaultManager(){
		return defaultManager;
	}
	
	public static EJsonManager createManager() {
		return new EJsonManager();
	}
	

	public  IEJsonSerializer getExactSerializer(Class cls) {
		return (IEJsonSerializer) getExactSOrD(cls, true) ; 
	}
	
	public  IEJsonDeserializer getExactDeserializer(Class cls) {
		return (IEJsonDeserializer) getExactSOrD(cls,false);
	}
	
	
	private Object getExactSOrD(Class cls, boolean isSerialize){
		Map type2SOrD = isSerialize ? type2SerializerMap : type2DeserializerMap; 
		Map defaultSOrD = isSerialize ? defaultSerializerMap : defaultDeserializerMap;
		if(type2SOrD.containsKey(cls)){
			return type2SOrD.get(cls);
		}
		for(IEJsonIOManager m : managerList){
			Object result ; 
			if((result =(isSerialize ? m.getExactSerializer(cls) : m.getDeserializer(cls)))!=null){
				return result ; 
			}
		}
		if(defaultSOrD.containsKey(cls)){
			return defaultSOrD.get(cls);
		}
		return null ; 
	}
	
	public static ClassInfoPool getClassPool(){
		return classInfoPool ; 
	}
	
//	public static ObjectRefMap getObjectRefMap(){
//		ObjectRefMap curThread = threadLocalObjectRefMap.get();
//		if(curThread == null){
//			curThread = new ObjectRefMap();
//			threadLocalObjectRefMap.set(curThread);
//		}
//		return curThread ; 
//	}
//	
//	public static ObjectDeserRefMap getObjectDesRefMap(){
//		ObjectDeserRefMap curThread = threadLocalObjectDesRefMap.get();
//		if(curThread == null){
//			curThread = new ObjectDeserRefMap();
//			threadLocalObjectDesRefMap.set(curThread);
//		}
//		return curThread ; 
//	}
	
	public static ClassInfo getClassInfo(Class cls){
		return classInfoPool.getClassInfo(cls, true);
	}
	
	public IEJsonDeserializer getArrayDeserializer(){
		return arrayDeserializer;
	}

	public IEJsonDeserializer getNumberDeserializer() {
		return numberDeserializer;
	}

	public  Map<Class, IEJsonDeserializer> getDefaultDeserializers() {
		return defaultDeserializerMap;
	}

	public IEJsonDeserializer getStringDeserializer() {
		return stringDeserializer;
	}

	public IEJsonSerializer getDeserializer(String type) {
		if (type == null){
			return objectSerializer;
		}
		return getSerializer(objectDeserializer.loadClass(type, null));
	}

	public IEJsonSerializer getSerializer(Object o) {
		if (o == null){
			return objectSerializer;
		}
		return getSerializer(o.getClass());
	}
	
}
