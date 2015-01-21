package com.easyservice.xml;

import com.easyservice.support.ClassInfo;
import com.easyservice.support.ClassInfoPool;

public class XMLClassInfoPool extends ClassInfoPool {

	public XMLClassInfoPool() {
	}
	
	public synchronized <C> XMLClassInfo<C> getClassInfo(Class<C> c, boolean buildPropertyIndex){
		ClassInfo<C> ci = null;
		if ( (ci = caches.get(c)) == null ){
			caches.put(c, ci = new XMLClassInfo<C>(c));
		}
		if (!ci.isPropertyIndexBuilt() && buildPropertyIndex){
			ci.buildPropertyIndex();
		}
		return (XMLClassInfo<C>)ci;
	}
}
