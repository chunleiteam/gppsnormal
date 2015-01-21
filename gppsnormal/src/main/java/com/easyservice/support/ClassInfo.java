package com.easyservice.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.easyservice.reflect.FieldMember;
import com.easyservice.reflect.IMember;
import com.easyservice.reflect.PropertyMember;

public class ClassInfo<C> {
	
	
	static Map<Class, Class> generalGenericImplementMap = new HashMap<Class, Class>();
	
	static {
		generalGenericImplementMap.put(List.class, ArrayList.class);
		generalGenericImplementMap.put(Map.class, HashMap.class);
		generalGenericImplementMap.put(Set.class, HashSet.class);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getGeneralGenericImplement(Class<T> type){
		Class<T> rt =  generalGenericImplementMap.get(type);
		return rt == null ? type : rt;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean  isCollectionOrMap(Class type){
		if (
//				List.class.isAssignableFrom( type ) 
//				|| Set.class.isAssignableFrom( type )
				isCollection(type)
				|| Map.class.isAssignableFrom( type )
				|| type.isArray()
				){
			return true;
		}
		return false;
	}
	
	protected Class<C>  type;
	
//	Map<String, Field> fieldsMap = null;//Collections.synchronizedMap(new HashMap<String, Field>());
//	Map<String, Method> methodsMap = Collections.synchronizedMap(new HashMap<String, Method>());
	protected Map<String, IMember> propertiesMap = new HashMap<String, IMember>();
	protected boolean propertyIndexBuilt = false;
	protected boolean allFiledIndexBuilt = false;
	protected Map<Class, Boolean> fieldIndexStatusMap = new HashMap<Class, Boolean>();
	
	
	public Annotation[] getAnnotations(){
		return type.getAnnotations();
	}
	public <T extends Annotation> T getAnnotation(Class<T> annotationType){
		return type.getAnnotation(annotationType);
	}
	
	public ClassInfo(Class<C>  type){
		this.type = type;
	}
	
	public Collection<IMember> getProperties(){
		return propertiesMap.values();
	}
	
	
//	public boolean initPropertiesMap(){
//		if (propertiesMap == null){
//			propertiesMap = Collections.synchronizedMap(new HashMap<String, IMember>());
//			return true;
//		}
//		return false;
//	}
	
	public boolean isPropertyIndexBuilt(){
		return propertyIndexBuilt;
	}
	
	public boolean isFieldIndexBuilt(Class clz){
		return (allFiledIndexBuilt || fieldIndexStatusMap.get(clz) != null);
	}
	
	protected boolean indexMember( IMember member) {
		String field = member.getName();
		IMember old = propertiesMap.get(field);
		if (old != null ) {
			if (member.isReadOnly()){
				return false;
			}
			if (member.getType().isAssignableFrom(old.getType()) || member.isReadOnly() && old instanceof PropertyMember){
				return false;
			}
		}
		propertiesMap.put(member.getName(), member);
		return true;
	}
	
	public void buildPropertyIndex(){
//		if (propertiesMap == null){
//			initPropertiesMap();
//		}
		if (!propertyIndexBuilt){
			propertyIndexBuilt = true;
			for (Method m : type.getMethods()){
				if (PropertyMember.isPropertyMember(m)){
					String n =  m.getName();
					if (n.startsWith("is")){
						n = "get" + n.substring(2);
					}
					try {
						Method setter = null;
						try{
							setter = type.getMethod("s" +n.substring(1), m.getReturnType());
						}catch(NoSuchMethodException e){ //is read only property
						}
						String field = n.substring("set".length());
						if (field.length() > 1){
							field = field.substring(0, 1).toLowerCase() + field.substring(1);
						}else{
							field = field.substring(0, 1).toLowerCase();
						}
						indexMember(new PropertyMember(this.type, field, m, setter));
					} catch (Throwable e) {
						
					} 
				}
			}
			for (Field f : type.getFields()){
				if (!Modifier.isStatic(f.getModifiers()) )
					indexMember(new FieldMember(f));
			}

		}
		
	}
	
	public void addDeclaredFiledIndex(Class<?> clz){
		if (fieldIndexStatusMap.get(clz) != null){
			return;
		}
		fieldIndexStatusMap.put(clz, Boolean.TRUE);
		for (Field f : clz.getDeclaredFields()){
			indexMember(new FieldMember(f));
		}
	}
	
	public  void addAllFieldIndex(Class<?> clz){
		addDeclaredFiledIndex(clz);
		// Direct superinterfaces, recursively
        Class[] interfaces = clz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            addDeclaredFiledIndex(interfaces[i]);
        }
        // Direct superclass, recursively
        if (!clz.isInterface()) {
            Class c = clz.getSuperclass();
            if (c != null) {
            	addAllFieldIndex(c);
            }
        }
		
	}
	
	public  void addAllFieldIndex(){
		if (allFiledIndexBuilt){
			return;
		}
		addAllFieldIndex(this.type);
		allFiledIndexBuilt = true;
	}
	
	public <T> IMember<T> getProperty(String name){
		IMember<T> t = null;
		if (propertiesMap != null){
			t = propertiesMap.get(name);
		}
		if (t == null && !propertyIndexBuilt){
			try {
				t = new PropertyMember<T>(type, name);
				if (propertiesMap != null){
					indexMember(t);
				}
			} catch (Throwable e) {
				t = null;
			} 
		}
		return t;
	}
	
	public static Class[] getItemTypes(Class type, Type gtype, ClassLoader loader) {
		if (loader == null){
			loader = Thread.currentThread().getContextClassLoader();
		}
		if (type.isArray()){
//			String typeStr = type.toString();
//			String itemTypeStr = typeStr.substring("class [L".length(), typeStr.length() - 1);
//			try {
//				Class clz = ClassInfo.loadClass(itemTypeStr, loader);
//				return  new Class[] {clz};
//			} catch (ClassNotFoundException e) {
//				return  null;
//			}
			return new Class[]{type.getComponentType()};
		}
		if (gtype instanceof ParameterizedType){
			Type[] ts =  ((ParameterizedType)gtype).getActualTypeArguments();
			try {
				Class[] cs = new Class[ts.length];
				for (int i = 0; i < ts.length; i++){
					if (ts[i] instanceof ParameterizedType){
						ts[i] = ((ParameterizedType)ts[i]).getRawType();
					}
					String tmp = ts[i].toString();
					if (tmp.equals("?")){
						cs[i] = Object.class;
					}else{
						cs[i] = ClassInfo.loadClass(tmp.substring(tmp.indexOf(" ")+1), loader);
					}
					
				}
				return  cs;
			} catch (ClassNotFoundException e) {
				return  null;
			}
		}
		return null;
	}
	
	public static Field searchField(Class<?> cls, String fname) {
		Field[] fds = cls.getDeclaredFields();
		for (Field fd : fds){
			if (fd.getName().equals(fname)){
				return fd;
			}
		}
		 // Direct superinterfaces, recursively
        Class[] interfaces = cls.getInterfaces();
        Field rt = null;
        for (int i = 0; i < interfaces.length; i++) {
            Class c = interfaces[i];
            
            if ((rt = searchField(c, fname)) != null) {
                return rt;
            }
        }
        // Direct superclass, recursively
        if (!cls.isInterface()) {
            Class c = cls.getSuperclass();
            if (c != null) {
                if ((rt = searchField(c, fname)) != null) {
                    return rt;
                }
            }
        }
        return null;
		
	}

	public Class<C> getType() {
		return type;
	}

	public void setType(Class<C> type) {
		this.type = type;
	}
	
	public static <P> Class<P> loadClass(String name) throws ClassNotFoundException{
		Class p = null;
		try {
			p = Class.forName(name);
		} catch (ClassNotFoundException e) {
		}
		if (p == null){
			p = Thread.currentThread().getContextClassLoader().loadClass(name);
		}
		return (Class<P>)p;
	}
	
	public static <P> Class<P> loadClass(String name, ClassLoader loader) throws ClassNotFoundException{
		Class p = null;
		if (loader != null){
			try {
				p = loader.loadClass(name);
			} catch (ClassNotFoundException e) {
			}
		}
		if (p == null){
			p = loadClass(name);
		}
		return (Class<P>)p;
	}
	
	public static boolean isCollection(Class<?> c){
		return Collection.class.isAssignableFrom(c);
	}
	
	public static boolean isMap(Class<?> c){
		return Map.class.isAssignableFrom(c);
	}

	public boolean isAllFiledIndexBuilt() {
		return allFiledIndexBuilt;
	}
	
	public static Class[] getItemTypes(Class type){
		return ClassInfo.getItemTypes(type, null, type.getClassLoader());
	}
	
}
