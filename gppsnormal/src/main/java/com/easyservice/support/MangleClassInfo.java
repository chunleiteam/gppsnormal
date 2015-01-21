package com.easyservice.support;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.caucho.services.server.AbstractSkeleton;

public class MangleClassInfo {
	
	WeakReference<Class> typeRef;
	Map<Method, String> method2StringMap = new WeakHashMap<Method, String>();
	Map<String, Method> string2MethodMap = new HashMap<String, Method>();
	Map<Method, String[]> method2ParamListMap = new WeakHashMap<Method, String[]>();
	
	public final static int STYLE_SIMPLE = 1;
	public final static int STYLE_SHORT = 2;
	public final static int STYLE_FULL = 0;
	
	int methodNameStyle = 1; // 0 simple,  1 shot parameter type,   2 full
	
	public MangleClassInfo(Class type, int methodNameStyle){
		this.typeRef = new WeakReference<Class>(type);
		for (Method m: type.getMethods()){
			if (m.getDeclaringClass() == Object.class) {
				continue;
			}
			String ms = null;
			if ( (methodNameStyle & STYLE_SIMPLE) != 0 ) {
				ms = m.getName() + "_" + m.getParameterTypes().length;
				method2StringMap.put(m, ms);
				string2MethodMap.put(ms, m);
			}
			if ( (methodNameStyle & STYLE_SHORT) != 0 ) {
				ms = AbstractSkeleton.mangleName(m, false);
				method2StringMap.put(m, ms);
				string2MethodMap.put(ms, m);
			}
			if (methodNameStyle == STYLE_FULL) {
				ms = AbstractSkeleton.mangleName(m, true);
				method2StringMap.put(m, ms);
				string2MethodMap.put(ms, m);
			}
			
			method2StringMap.put(m, ms);
			string2MethodMap.put(ms, m);
			
			
			ParamList pl = m.getAnnotation(ParamList.class);
			if (pl != null){
				method2ParamListMap.put(m, pl.value());
			}
//			else{
//				int psize = m.getParameterTypes().length;
//				String[] paramNames = new String[psize];
//				for (int i = 0; i < psize; i++){
//					paramNames[i] = "$" + i;
//				}
//				method2ParamListMap.put(m, paramNames);
//			}
		}
	}
	
	public MangleClassInfo(String type, ClassLoader loader) throws ClassNotFoundException{
		this(loader.loadClass(type), STYLE_SIMPLE | STYLE_SHORT);
	}
	
	public MangleClassInfo(String type, int methodNameStyle, ClassLoader loader) throws ClassNotFoundException{
		this(loader.loadClass(type), methodNameStyle);
	}
	
	public Class getType(){
		return typeRef.get();
	}
	
	public String getMethodString(Method m){
		return method2StringMap.get(m);
	}
	
	public Method getMethod(String mengle){
		return string2MethodMap.get(mengle);
	}
	
	public Collection<Method> getMethods() {
		return method2StringMap.keySet();
	}
	
	public String[] getParamList(Method m){
		return method2ParamListMap.get(m);
	}
	
	public String[] getParamList(String mengle){
		Method m = string2MethodMap.get(mengle);
		return  m == null ? null : method2ParamListMap.get(m);
	}
}
