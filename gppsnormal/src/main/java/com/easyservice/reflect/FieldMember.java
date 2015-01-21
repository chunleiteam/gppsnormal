package com.easyservice.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.easyservice.support.ClassInfo;

public class FieldMember<T> implements IMember<T> {

	Field field = null;
	Class[] itemTypes = nullItemTypes;
	Class<?> hostType;
	
	public String getName(){
		return field.getName();
	}
	
	public FieldMember(Field field){
		this.field = field;
		this.hostType = field.getDeclaringClass();
	}
	
	public FieldMember(Class<?> host, String field) throws NoSuchFieldException{
		hostType = host;
		this.field  = ClassInfo.searchField(host, field);
		if (this.field == null){
			throw new NoSuchFieldException(field + " in " + host);
		}
	}
	
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return field.getAnnotation(annotationType);
	}

	public Annotation[] getAnnotations() {
		return field.getAnnotations();
	}

	public Class[] getItemTypes() {
		
		if (itemTypes != nullItemTypes){
			return itemTypes;
		}
		Class type = field.getType();
		if (type.isArray()){
			String typeStr = type.toString();
//			String itemTypeStr = typeStr.substring("class [L".length(), typeStr.length() - 1);
//			try {
//				Class clz = ClassInfo.loadClass(itemTypeStr, this.hostType.getClassLoader());
				return itemTypes = new Class[] {type.getComponentType()};
//			} catch (ClassNotFoundException e) {
//				return itemTypes = null;
//			}
			
			
		}
		if (field.getGenericType() instanceof ParameterizedType){
			Type[] ts =  ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
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
						cs[i] = ClassInfo.loadClass(tmp.substring(tmp.indexOf(" ")+1), this.hostType.getClassLoader());
					}
					
				}
				return itemTypes = cs;
			} catch (ClassNotFoundException e) {
				return itemTypes = null;
			}
		}
		return null;
	}

	public Class<T> getType() {
		return (Class<T>)field.getType();
	}

	public T getValue(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		boolean a = this.field.isAccessible();
		if (!a){
			field.setAccessible(!a);
		}
		try {
			return (T)this.field.get(obj);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}finally{
			if (!a){
				field.setAccessible(a);
			}
		}
		return null;
	}

	public void setValue(Object obj, T v) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		boolean a = this.field.isAccessible();
		if (!a){
			field.setAccessible(!a);
		}
		try {
			 this.field.set(obj, v);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}finally{
			if (!a){
				field.setAccessible(a);
			}
		}
		
	}
	
	public Class getDeclaringClass(){
		return field.getDeclaringClass();
	}

	public boolean isReadOnly() {
		return false;
	}

}
