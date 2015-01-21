package com.easyservice.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.easyservice.reflect.IMember;
import com.easyservice.support.ClassInfo;
import com.easyservice.support.convert.IConverter;
import com.easyservice.xml.annotation.CollectionStyleType;
import com.easyservice.xml.annotation.ExportCompressType;
import com.easyservice.xml.annotation.FieldStyleType;
import com.easyservice.xml.annotation.XMLMapping;
import com.easyservice.xml.annotation.XMLNamespace;

@SuppressWarnings({"rawtypes","unchecked"})
public class XMLClassUtil {
	
	static Map<Class, Class> generalGenericImplementMap = new HashMap<Class, Class>();
	
	static {
		generalGenericImplementMap.put(List.class, ArrayList.class);
		generalGenericImplementMap.put(Map.class, HashMap.class);
		generalGenericImplementMap.put(Set.class, HashSet.class);
	}
	
	public static <T> Class<T> getGeneralGenericImplement(Class<T> type){
		Class<T> rt =  generalGenericImplementMap.get(type);
		return rt == null ? type : rt;
	}
	
	public static boolean  isCollectionOrMap(Class type){
		if (List.class.isAssignableFrom( type ) 
				|| Set.class.isAssignableFrom( type )
				|| Map.class.isAssignableFrom( type )
				|| type.isArray()
				){
			return true;
		}
		return false;
	}
	
	public static boolean  isMap(Class type){
		if ( Map.class.isAssignableFrom( type )	){
			return true;
		}
		return false;
	}
	
	
	public static void initXMLInfo(IMember m, XMLNamespaceInfo xni, XMLMappingInfo xmi){
		XMLMapping xm = (XMLMapping)m.getAnnotation(XMLMapping.class);
		XMLNamespace xn = (XMLNamespace)m.getAnnotation(XMLNamespace.class);
		if (xn == null || xn.prefix().length() == 0){ // overwrite by class annotation
			xn = (XMLNamespace)m.getType().getAnnotation(XMLNamespace.class);
		}
		if (xm == null){
			xm =  (XMLMapping)m.getType().getAnnotation(XMLMapping.class);
		}
		if (xm != null){
			xmi.shadowBy(xm);
		}
		if (xmi.itemTypes().length == 0){
			xmi.setItemTypes(m.getItemTypes());
		}
		if (xn != null && xni!= null){
			xni.shadowBy(xn);
		}
		if (xmi.getConverter() != null && xmi.getConverter().length() > 0 && xmi.getFormatedConverter() == null){
			try {
				xmi.setFormatedConverter( (IConverter)m.getDeclaringClass().getClass().getClassLoader().loadClass(xmi.getConverter()).newInstance());
			} catch (Throwable e) {
				e.printStackTrace();
			} 
		}
	}
	
	public static void initXMLInfoOrginal(Class cls, XMLNamespaceInfo xni, XMLMappingInfo xmi){
		XMLMapping xm = (XMLMapping)cls.getAnnotation(XMLMapping.class);
		XMLNamespace xn = (XMLNamespace)cls.getAnnotation(XMLNamespace.class);
		if (xm != null){
			xmi.shadowBy(xm);
		}
		if (xmi.itemTypes().length == 0){
			xmi.setItemTypes(ClassInfo.getItemTypes(cls, null, cls.getClassLoader()));
		}
		if (xn != null && xni!= null){
			xni.shadowBy(xn);
		}
	}
	
	public static void initXMLInfo(Class cls, XMLNamespaceInfo xni, XMLMappingInfo xmi){
		XMLMapping xm = (XMLMapping)cls.getAnnotation(XMLMapping.class);
		XMLNamespace xn = (XMLNamespace)cls.getAnnotation(XMLNamespace.class);
		if (xm != null){
			xmi.shadowBy(xm);
		}
		if (xmi.getCollectionStyle() == CollectionStyleType.INHERIT) {
			xmi.setCollectionStyle(CollectionStyleType.EMBED);
		}
		if (xmi.getFieldStyle() == FieldStyleType.INHERIT) {
			xmi.setFieldStyle(FieldStyleType.ATTR);
		}
		if (xmi.getExportCompressType() == ExportCompressType.INHERIT) {
			xmi.setExportCompressType(ExportCompressType.REFRENCE);
		}
		if (xmi.itemTypes() == null || xmi.itemTypes().length == 0){
			xmi.setItemTypes(ClassInfo.getItemTypes(cls, null, cls.getClassLoader()));
		}
		if (xn != null && xni!= null){
			xni.shadowBy(xn);
		}
	}
	
	
	
	

	public static void initXMLInfo(Method method, int paramIndex,   XMLNamespaceInfo xni, XMLMappingInfo xmi){
		 Class pt = method.getParameterTypes()[paramIndex];
		 Annotation[] annos = method.getParameterAnnotations()[paramIndex];
		 Type gpt = method.getParameterTypes()[paramIndex];
		 initXMLInfo(xmi, pt, gpt, annos);
	}

	public static void initXMLInfo(XMLMappingInfo xmi, Class pt, Type gpt, Annotation[] annos) {
		
		Class[] itemTypes = ClassInfo.getItemTypes(pt, gpt, pt.getClassLoader());
		 XMLClassUtil.initXMLInfo(pt, null, xmi);
		 xmi.setItemTypes(itemTypes);
		 if (annos != null){
			 for (Annotation a : annos){
				 if (a.annotationType() == XMLMapping.class){
					 XMLMappingInfo pxmi = new XMLMappingInfo();
					 pxmi.shadowBy((XMLMapping)a);
					 mergerNonNull(xmi, pxmi);
					 if(pxmi.itemTypes().length > xmi.itemTypes().length){
						 xmi.setItemTypes(pxmi.itemTypes());
					 }
					 break;
				 }
			 }
		 }
	}
	
	public static XMLMappingInfo mergerNonNull(XMLMappingInfo cxmi, XMLMappingInfo mxmi) {
		
	 	if (cxmi.getTag().length() == 0){
			cxmi.setTag(mxmi.getTag());
		}
		
		if (cxmi.getChildTag().length() == 0){
			cxmi.setChildTag(mxmi.getChildTag());
		}
		
		if (cxmi.getKeyTag().length() == 0){
			cxmi.setKeyTag(mxmi.getKeyTag());
		}
		
		if (cxmi.getValueTag().length() == 0){
			cxmi.setValueTag(mxmi.getValueTag());
		}
		
		if (cxmi.getValueTag().length() == 0){
			cxmi.setValueTag(mxmi.getValueTag());
		}
		
		if (cxmi.getItemTypes() == null || cxmi.getItemTypes().length == 0){
			cxmi.setItemTypes(mxmi.getItemTypes());
		}
		return cxmi;
	}
	
	public static XMLMappingInfo getGeneralMemeberTags(IMember m, XMLMappingInfo xmi, XMLNamespaceInfo mxni, XMLMappingInfo mxmi){
		initXMLInfo(m, mxni, mxmi);
		
		mxmi.inherit(xmi);
		if (mxmi.getTag().length() == 0){
			String tag = m.getName();
			if (mxmi.getFieldStyle() == FieldStyleType.ATTR_INITSUPERCASE){
				tag = StringTools.upperCaseFirstChar(tag);
			}
			mxmi.setTag(tag);
		}
//		if (mxmi.getChildTag().length() == 0 && (isCollectionOrMap(m.getType())) ){
//			mxmi.setChildTag("item");
//		}
		return mxmi;
	}
	
	
	public static XMLMappingInfo getMemeberTag(IMember m, XMLMappingInfo xmi, XMLNamespaceInfo mxni, XMLMappingInfo mxmi){
		initXMLInfo(m, mxni, mxmi);
		mxmi.inherit(xmi);
		if (mxmi.getTag().length() == 0){
			String tag = m.getName();
			if (mxmi.getFieldStyle() == FieldStyleType.ATTR_INITSUPERCASE){
				tag = StringTools.upperCaseFirstChar(tag);
			}
			mxmi.setTag(tag);
		}
		return mxmi;
	}
	
	public static XMLMappingInfo getCollectionMemeberTags(IMember m,  XMLMappingInfo xmi, XMLNamespaceInfo mxni, XMLMappingInfo mxmi){
		mxmi.inherit(xmi);
		initXMLInfo(m, mxni, mxmi);
		if (mxmi.getTag().length() == 0){
			String tag = m.getName();
			if (mxmi.getFieldStyle() == FieldStyleType.ATTR_INITSUPERCASE){
				tag = StringTools.upperCaseFirstChar(tag);
			}
			mxmi.setTag(tag);
		}
		if (mxmi.getChildTag().length() == 0){
			mxmi.setChildTag("collectionItem");
		}
		return mxmi;
	}
	
	public static XMLMappingInfo getArrayMemeberTags(IMember m,  XMLMappingInfo xmi, XMLNamespaceInfo mxni, XMLMappingInfo mxmi){
		mxmi.inherit(xmi);
		initXMLInfo(m, mxni, mxmi);
		if (mxmi.getTag().length() == 0){
			String tag = m.getName();
			if (mxmi.getFieldStyle() == FieldStyleType.ATTR_INITSUPERCASE){
				tag = StringTools.upperCaseFirstChar(tag);
			}
			mxmi.setTag(tag);
		}
		if (mxmi.getChildTag().length() == 0){
			mxmi.setChildTag("arrayItem");
		}
		return mxmi;
	}
	
}
