package com.easyservice.support;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import com.easyservice.reflect.IMember;

public class ClassInfoPool {
	
	static ClassInfoPool defaultPool = new ClassInfoPool();
	
	protected Map<Class, ClassInfo> caches =  Collections.synchronizedMap(new WeakHashMap<Class, ClassInfo>());
	
	public static ClassInfoPool getDafault(){
		return defaultPool;
	}
	
	public <C> ClassInfo<C> getClassInfo(Class<C> c){
		return getClassInfo(c, true);
	}
	
	public synchronized <C> ClassInfo<C>  getClassInfo(Class<C> c, boolean buildPropertyIndex){
		ClassInfo<C> ci = null;
		if ( (ci = caches.get(c)) == null ){
			caches.put(c, ci = new ClassInfo<C>(c));
		}
		if (buildPropertyIndex){
			ci.buildPropertyIndex();
		}
		return ci;
	}
	
	public synchronized <C> ClassInfo<C> getClassInfo(Class<C> c, boolean buildPropertyIndex, boolean buildFiledIndex ){
		ClassInfo<C> ci = null;
		if ( (ci = caches.get(c)) == null ){
			caches.put(c, ci = new ClassInfo<C>(c));
		}
		if (buildPropertyIndex){
			ci.buildPropertyIndex();
		}
		if (buildFiledIndex){
			ci.addAllFieldIndex();
		}
		return ci;
	}
	
	public synchronized <C> ClassInfo<C> getClassInfo(Class<C> c, boolean buildPropertyIndex, Class ...clzs2buildFiledIndex){
		ClassInfo<C> ci = null;
		if ( (ci = caches.get(c)) == null ){
			caches.put(c, ci = new ClassInfo<C>(c));
		}
		if (buildPropertyIndex && !ci.isPropertyIndexBuilt()){
			ci.buildPropertyIndex();
		}
		if (!ci.isAllFiledIndexBuilt()){
			for (Class clz : clzs2buildFiledIndex){
				ci.addDeclaredFiledIndex(clz);
			}
		}
		return ci;
	}
	
	public Object getPropertityOrMapValue(Object v, String field) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		if (v == null){
			return null;
		}
		ClassInfo ci =  getClassInfo(v.getClass());
		IMember m = ci.getProperty(field);
		if (m == null){
			if ( ClassInfo.isMap(v.getClass()) ){
				return ( (Map) v).get(field);
			}
			return null;
		}else{
			return m.getValue(v);
		}
		
	}
	
	public Object setPropertityOrMapValue(Object v, String field, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		if (v == null){
			throw new IllegalArgumentException("null value");
		}
		Object rt = null;
		ClassInfo ci =  getClassInfo(v.getClass());
		IMember m = ci.getProperty(field);
		if (m == null){
			if ( ClassInfo.isMap(v.getClass()) ){
				rt = ( (Map) v).put(field, value);
			}else 
				throw new IllegalArgumentException("null value at " + field);
		}else{
			rt = m.getValue(v);
			m.setValue(v, value);
		}
		return rt;
	}
	
	public Class resolveClass(Class root, String dotExp){
		if (dotExp == null || dotExp.length() == 0){
			return root;
		}
		String pn = dotExp;
		int dot = dotExp.indexOf(".");
		while (dot > 0){
			pn = dotExp.substring(0, dot);
			IMember m = getClassInfo(root).getProperty(pn);
			if (m == null){
				return null;
			}
			if (ClassInfo.isCollection(m.getType())) {
				root = m.getItemTypes()[m.getItemTypes().length-1];
			}else {
				root = m.getType();
			}
			dotExp = dotExp.substring(dot+1);
			dot = dotExp.indexOf(".");
		}
		pn = dotExp;
		IMember pm =  getClassInfo(root).getProperty(pn);
		return pm == null ? null : pm.getType();
	}
	
	public  <T> IMember<T> resolvePropertity(Class root, String dotExp){
		
		if (dotExp == null || dotExp.length() == 0){
			return null;
		}
		String pn = dotExp;
		int dot = dotExp.indexOf(".");
		while (dot > 0){
			pn = dotExp.substring(0, dot);
			IMember m = getClassInfo(root).getProperty(pn);
			if (m == null){
				throw new IllegalArgumentException("no such property : " + pn + " at " + root.getName());
			}
			root = m.getType();
			dotExp = dotExp.substring(dot+1);
			dot = dotExp.indexOf(".");
		}
		pn = dotExp;
		return getClassInfo(root).getProperty(pn);
	}
	
	
	public Class resolvePropertityOrMapValueType(Object v, String dotExp) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (v == null){
			return null;
		}
		ClassInfo ci =  getClassInfo(v.getClass());
		String pn = dotExp;
		int dot = dotExp.indexOf(".");
		while (dot > 0){
			pn = dotExp.substring(0, dot);
			v = getPropertityOrMapValue(v, pn);
			if (v == null){
				return null;
			}
			dotExp = dotExp.substring(dot+1);
			dot = dotExp.indexOf(".");
			ci = getClassInfo(v.getClass());
		}
		if (v == null){
			return resolveClass(ci.getType(), dotExp);
		}
		pn = dotExp;
		v = getPropertityOrMapValue(v, pn);
		return v == null ? resolveClass(ci.getType(), dotExp) : v.getClass();
	}
	
	public  Object resolvePropertityValue(Object v, String dotExp) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		if (v == null){
			return null;
		}
		ClassInfo ci =  null;
		String pn = dotExp;
		int dot = dotExp.indexOf(".");
		while (dot > 0){
			pn = dotExp.substring(0, dot);
			v = getPropertityOrMapValue(v, pn);
			if (v == null){
				return null;
			}
			dotExp = dotExp.substring(dot+1);
			dot = dotExp.indexOf(".");
		}
		if (v == null){
			return null;
		}
		ci = getClassInfo(v.getClass());
		pn = dotExp;
		return getPropertityOrMapValue(v, pn);
	}
	
	public  Object updatePropertityOrMapValue(Object v, String dotExp, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		ClassInfo ci =  null;
		String pn = dotExp;
		int dot = dotExp.indexOf(".");
		while (dot > 0){
			ci =  getClassInfo(v.getClass());
			pn = dotExp.substring(0, dot);
			v = getPropertityOrMapValue(v, pn);
			if (v == null){
				throw new IllegalArgumentException("null value in exp : " + dotExp + " at " + pn);
			}
			dotExp = dotExp.substring(dot+1);
			dot = dotExp.indexOf(".");
		}
		if (v == null){
			throw new IllegalArgumentException("null value in exp : " + dotExp + " at " + pn);
		}
		ci = getClassInfo(v.getClass());
		pn = dotExp;
		return setPropertityOrMapValue(v, pn, value);
	}

//	public   String i18n(Class type, String k,  I18nService menuI18nService, Locale locale) {
//		if (k.length() > 0 && k.charAt(0) == '$'){ // page ui element
//			return menuI18nService.getString(k, locale);
//		}
//		String gk = null;
//		int dot = k.indexOf('.');
//		String name = k;
//		StringBuffer rt = new StringBuffer();
//		while (dot > 0){
//			name = k.substring(0, dot);
//			gk = type.getName();
//			String v = menuI18nService.getGlobalString(gk, name, locale);
//			rt.append(v);
//			k = k.substring(dot+1);
//			dot = k.indexOf('.');
//			IMember m = getClassInfo(type).getProperty(name);
//			if (m == null){
//				return rt.append(k).toString();
//			}else {
//				if (ClassInfo.isCollectionOrMap(m.getType()) ){
//					type = m.getItemTypes()[m.getItemTypes().length - 1];
//				}else{
//					type  = m.getType();
//				}
//			}
//		}
//		name = k;
//		gk = type.getName();
//		IMember m = getClassInfo(type).getProperty(name);
//		if (m == null && name.length() > 0){
//			rt.append(k).toString();
//		}else{
//			String v = menuI18nService.getGlobalString(gk, name, locale);
//			if (v == null){
//				rt.append( name );
//			}else{
//				rt.append( v );
//			}
//		}
//		return rt.toString();
//	}
	
	public  Object updatePropertity(Object v, String dotExp, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		ClassInfo ci =  null;
		String pn = dotExp;
		int dot = dotExp.indexOf(".");
		while (dot > 0){
			ci =  getClassInfo(v.getClass());
			pn = dotExp.substring(0, dot);
			IMember m = ci.getProperty(pn);
			if (m == null){
				throw new IllegalArgumentException("no such property in exp : " + dotExp + " at " + pn);
			}
			v = m.getValue(v);
			if (v == null){
				throw new IllegalArgumentException("null value in exp : " + dotExp + " at " + pn);
			}
			dotExp = dotExp.substring(dot+1);
			dot = dotExp.indexOf(".");
		}
		if (v == null){
			throw new IllegalArgumentException("null value in exp : " + dotExp + " at " + pn);
		}
		ci = getClassInfo(v.getClass());
		pn = dotExp;
		return setPropertityOrMapValue(v, pn, value);
	}
	
//	public String getProfile(Object o) {
//		return getProfile(o, false);
//	}
	
//	public String getProfile(Object o, boolean complex) {
//		
//		if (o == null){
//			return "null";
//		}
//		
//		return getProfile(o, new StringBuilder(), 0, new IdentityHashMap<Object, Object>(), complex).toString();
//	}
	
	private StringBuilder insertSpace(StringBuilder rt, int level){
		for (int i = 0; i < level; i++){
			rt.append(" ");
		}
		return rt;
	}
	
//	public StringBuilder getProfile(Object o, StringBuilder rt, int level, IdentityHashMap<Object, Object> exported, boolean complex){
//		if (o == null){
//			return rt.append("null");
//		}
//		if (exported.containsKey(o)){
//			return rt.append("@exported=").append(exported.size());
//		}
//		if (!EasyConverter.getDefaultInstance().canConvertToSimpleString(o.getClass())){
//			exported.put(o, true);
//		}
//		try{
//			if (EasyConverter.getDefaultInstance().canConvertToSimpleString(o.getClass()) || o.getClass().getMethod("toString").getDeclaringClass() == o.getClass()){
//				return rt.append(o.toString());
//			}
//			if (o instanceof Collection) {
//				Collection c = (Collection) o;
//				int i = 0;
//				for (Object item : c){
//					insertSpace(rt, level+1);
//					rt.append("@").append(i++).append("=");
//					getProfile(item, rt, level+1, exported, complex);
//				}
//				return rt;
//			}
//			if (o instanceof Map) {
//				Map m = (Map) o;
//				for (Entry en : (Set<Map.Entry>)m.entrySet()){
//					insertSpace(rt, level+1);
//					rt.append("@").append(en.getKey()).append("=");
//					getProfile(en.getValue(), rt, level+1, exported, complex);
//				}
//				return rt;
//			}
//			ClassInfo ci = getClassInfo(o.getClass());
////			insertSpace(rt, level);
//			rt.append("{");
//			insertSpace(rt, level+1);
//			for (IMember im : (Collection<IMember>)ci.getProperties()){
//				rt.append(im.getName()).append("=");
//				Object imv = im.getValue(o);
//				if (imv == null){
//					rt.append("null");
//				}else if (imv != null && EasyConverter.getDefaultInstance().canConvertToSimpleString(imv.getClass())){
//					getProfile(imv, rt, level+1, exported, complex);
//				}else if (complex) {
//					getProfile(imv, rt, level+1, exported, complex);
//				}
//				rt.append(",");
//			}
//			insertSpace(rt, level);
//			rt.append("}");
//		}catch (Exception e) {
//			throw new SuperRuntimeException(e.getMessage());
//		}
//		return rt;
//	}
}
