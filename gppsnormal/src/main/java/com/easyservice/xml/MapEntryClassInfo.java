package com.easyservice.xml;

import java.util.Map.Entry;

import com.easyservice.reflect.IMember;

public class MapEntryClassInfo extends XMLClassInfo {

//	XMLClassInfo delegate;
	
	@SuppressWarnings("unchecked")
	public MapEntryClassInfo(Class<? extends Entry>  clz, String keyTag, String valueTag, Class keyType, Class valueType) {
		super(clz);
		buildPropertyIndex();
		IMember kp = (IMember)propertiesMap.get("key");
		IMember vp = (IMember)propertiesMap.get("value");
		propertiesMap.clear();
		properties.clear();
		propertiesMap.put(keyTag, kp = new MapEntryPropertyMember(keyTag, keyType, kp));
		propertiesMap.put( valueTag == null || valueTag.length() == 0? "value" : valueTag, vp = new MapEntryPropertyMember(valueTag, valueType, vp));
		properties.add(kp);
		properties.add(vp);
	}
	
//	@Override
//	public <T> IMember<T> getPropertyByChildTag(String uri, String lname, String qname) {
//		return delegate.getPropertyByChildTag(uri, lname, qname);
//	}
//	
	@Override
	public  IMember getProperty(String uri, String lname, String qname) {
		IMember rt = super.getProperty(uri, lname, qname);
		return rt == null ? (IMember)super.getProperty("value") : rt ;
	}
	
	
}
