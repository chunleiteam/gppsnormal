package com.easyservice.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.easyservice.reflect.FieldMember;
import com.easyservice.reflect.IMember;
import com.easyservice.reflect.PropertyMember;
import com.easyservice.support.ClassInfo;
import com.easyservice.xml.annotation.CollectionStyleType;
import com.easyservice.xml.annotation.ExportCompressType;
import com.easyservice.xml.annotation.FieldStyleType;
import com.easyservice.xml.annotation.XMLMapping;
import com.easyservice.xml.annotation.XMLText;

public class XMLClassInfo<C> extends ClassInfo<C> {

	protected Map<String, XMLMapping> qName2xmlMapingMap = new HashMap<String, XMLMapping>();
	protected Map<XMLMapping, IMember>  xmlMaping2memberMap = new IdentityHashMap<XMLMapping, IMember>();
	protected Map<String, XMLMapping> flatChildrenMap = new HashMap<String, XMLMapping>();
	protected IMember textPropertity;
	
	protected List<IMember> properties = new ArrayList<IMember>();
	
	public XMLClassInfo(Class<C> type) {
		super(type);
	}

	@Override
	public void buildPropertyIndex(){
//		if (propertiesMap == null){
//			initPropertiesMap();
//		}
//		System.out.println("build " + type);
//		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
//		for (StackTraceElement st : sts){
//			System.out.println(st.toString());
//		}
//		try{
//			ClassPool cpl = ESClassFileTransformer.createClassPool(new ClassLoader[]{type.getClassLoader()});
//			CtClass clz = cpl.get(type.getName());
//			
//			for (MethodInfo m : (List<MethodInfo>)clz.getClassFile().getMethods()){
//				String n =  m.getName();
////				System.out.println("in ct:" + n);
//			}
//		}catch(Throwable e){
//			
//		}
		
		if (!propertyIndexBuilt){
			propertyIndexBuilt = true;
			for (Method m : type.getMethods()){
				if (PropertyMember.isPropertyMember(m)){
					String n =  m.getName();
					if (n.startsWith("is")){
						n = "get" + n.substring(2);
					}
//					System.out.println(m.getName());
					try {
						Method setter = null;
						try{
							setter = type.getMethod("s" +n.substring(1), m.getReturnType());
						}catch(NoSuchMethodException e){ //is read only property
//							throw e;
						}
						String field = n.substring("get".length());
						if (field.length() > 1){
							field = field.substring(0, 1).toLowerCase() + field.substring(1);
						}else{
							field = field.substring(0, 1).toLowerCase();
						}
						IMember member = new PropertyMember(this.type, field, m, setter);
						index(member);
					} catch (Throwable e) {
						throw new RuntimeException(e.getMessage(), e);
					} 
				}
			}
			for (Field f : type.getFields()){
				if (!Modifier.isStatic( f.getModifiers()) ) {
					index(new FieldMember(f));
				}
			}
			if (textPropertity != null){
				properties.add(textPropertity);
			}
		}
		
	}

	/**
	 * 索引member，如果发现是textPropertity, 则返回此true，并不加入properties，由调用方决定加入
	 * @param member
	 */
	private boolean index( IMember member) {
		String field = member.getName();
		IMember old = propertiesMap.get(field);
		if (old != null ) {
			if (member.isReadOnly()){
				return false;
			}
			if (member.getDeclaringClass() != type && member.getType().isAssignableFrom(old.getType()) || member.isReadOnly() && old instanceof PropertyMember){
				return false;
			}else{
				properties.remove(old);
			}
		}
		XMLText xt = (XMLText) member.getAnnotation(XMLText.class);
		propertiesMap.put(field, member);
		if (xt == null){
			properties.add(member);
		}
		XMLMapping xm = (XMLMapping) member.getAnnotation(XMLMapping.class);
		if (xm != null){
			if (xm.tagTypeMapping().length > 0){
				for (String m : xm.tagTypeMapping()){
					String[] mp = m.split("\\=");
					XMLMapping vxm = new VirtualXMLMapping(xm, mp[0]);
					qName2xmlMapingMap.put(mp[0], vxm);
					try {
						xmlMaping2memberMap.put(vxm, new VirtualMember(member, ClassInfo.loadClass(mp[1], type.getClassLoader())));
					} catch (ClassNotFoundException e) {
						throw new RuntimeException("can't load class in tagTypeMapping from " + mp[1], e);
					}
				}
			}else{
				if (xm.collectionStyle() == CollectionStyleType.EMBED){
					qName2xmlMapingMap.put(xm.tag().length() != 0 ? xm.tag() : field, xm);
				}else if (xm.collectionStyle() == CollectionStyleType.FLAT){
					flatChildrenMap.put(xm.childTag().length() != 0 ? xm.childTag() : field, xm);
				}else{ //xm.collectionStyle() == CollectionStyleType.INHERIT
					qName2xmlMapingMap.put(xm.tag().length() != 0 ? xm.tag() : field, xm);
					flatChildrenMap.put(xm.childTag().length() != 0 ? xm.childTag() : field, xm);
				}
				xmlMaping2memberMap.put(xm, member);
			}
			
		}
		
		if (xt != null){
			textPropertity = member;
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public <T> IMember<T> getProperty(String uri, String lname, String qname){
		XMLMapping xm = qName2xmlMapingMap.get(lname);
		if (xm == null){
			IMember<T> rt =  getProperty(lname);
			if (rt == null){
				rt = getProperty(StringTools.lowerCaseFirstChar(lname));
			}
			return rt;
		}else{
			return xmlMaping2memberMap.get(xm);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> IMember<T> getPropertyByChildTag(String uri, String lname, String qname){
		XMLMapping xm = flatChildrenMap.get(lname);
		return xm != null ? xmlMaping2memberMap.get(xm) : getProperty(lname);
	}
	
	public XMLMapping getXMLMapping(String uri, String lname, String qname){
		return qName2xmlMapingMap.get(lname);
	}
	
	public XMLMapping getXMLMappingByChildTag(String uri, String lname, String qname){
		return flatChildrenMap.get(lname);
	}
	
	@SuppressWarnings("unchecked")
	public XMLMapping getPropertyXMLMapping(IMember m){
		return (XMLMapping)m.getAnnotation(XMLMapping.class);
	}

	public IMember getTextPropertity() {
		return textPropertity;
	}

	@Override
	public Collection<IMember> getProperties() {
		return properties;
	}
	
	public static class VirtualXMLMapping implements XMLMapping {
		public XMLMapping xm;
		public String tag;
		public VirtualXMLMapping(XMLMapping xm, String tag){
			this.xm = xm;
			this.tag = tag;
		}
		
		
		public String childSimpleObjValueAttr() {
			return xm.childSimpleObjValueAttr();
		}

		public String childTag() {
			return xm.childTag();
		}

		public CollectionStyleType collectionStyle() {
			return xm.collectionStyle();
		}

		public String converter() {
			return xm.converter();
		}

		public ExportCompressType exportCompressType() {
			return xm.exportCompressType();
		}

		public FieldStyleType fieldStyle() {
			return xm.fieldStyle();
		}

		public String format() {
			return xm.format();
		}

		public Class[] itemTypes() {
			return xm.itemTypes();
		}

		public String keyTag() {
			return xm.keyTag();
		}

		public String[] orderedProperties() {
			return xm.orderedProperties();
		}

		public String tag() {
			return tag;
		}

		public String[] tagTypeMapping() {
			return xm.tagTypeMapping();
		}

		public String valueTag() {
			return xm.valueTag();
		}

		public Class<? extends Annotation> annotationType() {
			return xm.annotationType();
		}
		
	}
	
	public static class VirtualMember implements IMember {
		
		public IMember m;
		public Class type;
		public VirtualMember(IMember m, Class type){
			this.m = m;
			this.type = type;
		}

		public Annotation getAnnotation(Class annotationType) {
			return m.getAnnotation(annotationType);
		}

		public Annotation[] getAnnotations() {
			return m.getAnnotations();
		}

		public Class getDeclaringClass() {
			return m.getDeclaringClass();
		}

		public Class[] getItemTypes() {
			return m.getItemTypes();
		}

		public String getName() {
			return m.getName();
		}

		public Class getType() {
			return type;
		}

		public Object getValue(Object obj) throws IllegalArgumentException,
				IllegalAccessException, InvocationTargetException {
			return m.getValue(obj);
		}

		public boolean isReadOnly() {
			return m.isReadOnly();
		}

		public void setValue(Object obj, Object v)
				throws IllegalArgumentException, IllegalAccessException,
				InvocationTargetException {
			m.setValue(obj, v);
		}
		
	}
	
}
