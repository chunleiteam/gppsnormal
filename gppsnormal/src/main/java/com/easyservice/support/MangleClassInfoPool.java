package com.easyservice.support;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class MangleClassInfoPool {
	
	Map<String, MangleClassInfo> managleClassInfoMap = new HashMap<String, MangleClassInfo>();
//	Map<String, WeakReference<Class> > classMap = new HashMap<String, WeakReference<Class>>();
	
	static MangleClassInfoPool defaultMangleClassInfoPool = new MangleClassInfoPool();
	
	public MangleClassInfoPool() {
		
	}
	
	public synchronized MangleClassInfo getMangleClassInfo(String clz) throws ClassNotFoundException {
		MangleClassInfo rt = managleClassInfoMap.get(clz);
		if (rt == null){
			managleClassInfoMap.put(clz, rt = new MangleClassInfo(clz, Thread.currentThread().getContextClassLoader()));
		}
		return rt;
	}
	
	public static MangleClassInfoPool getDefault() {
		return defaultMangleClassInfoPool;
	}
	
	
	
}
