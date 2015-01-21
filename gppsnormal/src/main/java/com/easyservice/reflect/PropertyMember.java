package com.easyservice.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.easyservice.support.ClassInfo;

public class PropertyMember<T> implements IMember<T> {
	
	
	
	Class<?> hostType;
	String name;
	Method getter;
	Method setter;
	Class[] itemTypes = nullItemTypes;
	
	
	public PropertyMember() {
		super();
	}

	void correntAccess() {
		if (setter != null){
			setter.setAccessible(true);
		}
		getter.setAccessible(true);
	}

	public PropertyMember(Class<?> hostType, String name, Method getter, Method setter) {
		super();
		this.name = name;
		this.getter = getter;
		this.setter = setter;
		this.hostType = hostType;
		correntAccess();
	}
	
	

	public PropertyMember(Class<?> hostType, String field) throws SecurityException, NoSuchMethodException{
		if (hostType == null){
			return;
		}
		this.hostType = hostType;
		this.name = field;
		String getterName = "get" + field.substring(0, 1).toUpperCase();
		String setterName = "set"+ field.substring(0, 1).toUpperCase();
		if (field.length() > 1){
			getterName += field.substring(1);
			setterName += field.substring(1);
		}
		getter = hostType.getMethod(getterName);
		setter = hostType.getMethod(setterName, getter.getReturnType());
		correntAccess();
	}
	
	/* (non-Javadoc)
	 * @see com.cnebula.common.reflect.IMember#getAnnotations()
	 */
	public Annotation[] getAnnotations(){
		return getter.getAnnotations();
	}
	
	/* (non-Javadoc)
	 * @see com.cnebula.common.reflect.IMember#getAnnotation(java.lang.Class)
	 */
	public <A extends Annotation> A getAnnotation(Class<A> annotationType){
		return getter.getAnnotation(annotationType);
	}
	
	/* (non-Javadoc)
	 * @see com.cnebula.common.reflect.IMember#getType()
	 */
	public Class<T> getType(){
		return (Class<T>) getter.getReturnType();
	}
	
	/* (non-Javadoc)
	 * @see com.cnebula.common.reflect.IMember#getValue(java.lang.Object)
	 */
	public T getValue(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		return (T)getter.invoke(obj, null);
	}
	
	/* (non-Javadoc)
	 * @see com.cnebula.common.reflect.IMember#setValue(java.lang.Object, T)
	 */
	public void setValue(Object obj, T v) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		if (setter == null){
			throw new IllegalAccessError("readonly property can not be set");
		}
		setter.invoke(obj, new Object[]{v});
	}
	
	public static boolean isPropertyMember(Method m){
		String n = m.getName();
		return ( n.startsWith("get") || ((n.startsWith("is") && ( m.getReturnType() == Boolean.TYPE || m.getReturnType() == Boolean.class ) ) ) ) 
		&& m.getDeclaringClass() != Object.class && ( (m.getModifiers() & Modifier.STATIC) == 0) && (m.getParameterTypes().length == 0);
	}
	
	/* (non-Javadoc)
	 * @see com.cnebula.common.reflect.IMember#getItemTypes()
	 */
	public Class[] getItemTypes(){
		if (itemTypes != nullItemTypes){
			return itemTypes;
		}
		
		return itemTypes = ClassInfo.getItemTypes(getter.getReturnType(), getter.getGenericReturnType(), this.hostType.getClassLoader());

//		
//		Class type = getter.getReturnType();
//		if (type.isArray()){
//			String typeStr = type.toString();
//			String itemTypeStr = typeStr.substring("class [L".length(), typeStr.length() - 1);
//			try {
//				Class clz = ClassInfo.loadClass(itemTypeStr, this.hostType.getClassLoader());
//				return itemTypes = new Class[] {clz};
//			} catch (ClassNotFoundException e) {
//				return itemTypes = null;
//			}
//			
//			
//		}
//		if (getter.getGenericReturnType() instanceof ParameterizedType){
//			Type[] ts =  ((ParameterizedType)getter.getGenericReturnType()).getActualTypeArguments();
//			try {
//				Class[] cs = new Class[ts.length];
//				for (int i = 0; i < ts.length; i++){
//					if (ts[i] instanceof ParameterizedType){
//						ts[i] = ((ParameterizedType)ts[i]).getRawType();
//					}
//					String tmp = ts[i].toString();
//					if (tmp.equals("?")){
//						cs[i] = Object.class;
//					}else{
//						cs[i] = ClassInfo.loadClass(tmp.substring(tmp.indexOf(" ")+1), this.hostType.getClassLoader());
//					}
//					
//				}
//				return itemTypes = cs;
//			} catch (ClassNotFoundException e) {
//				return itemTypes = null;
//			}
//		}
//		return null;

	}
 	
	public String getName(){
		return name;
	}



	public Class getDeclaringClass() {
		return getter.getDeclaringClass();
	}

	public boolean isReadOnly() {
		return setter == null;
	}
}
