package com.easyservice.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.stereotype.Component;

import com.easyservice.reflect.IMember;
import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.EasyConverter;
import com.easyservice.support.convert.IConverter;
import com.easyservice.utils.ZStringUtils;
import com.easyservice.xml.annotation.CollectionStyleType;
import com.easyservice.xml.annotation.ExportCompressType;
import com.easyservice.xml.annotation.FieldStyleType;
import com.easyservice.xml.annotation.XMLIgnore;
import com.easyservice.xml.annotation.XMLMapping;
import com.easyservice.xml.annotation.XMLNamespace;

import static com.easyservice.xml.XMLClassUtil.*;
@Component
public class EasyObjectXMLTransformerImpl implements IEasyObjectXMLTransformer {

	SAXParserFactory factory;
	EasyConverter converter = new EasyConverter();
	boolean prettyPrint;
//	protected void setFactory(SAXParserFactory factory){
//		this.factory = factory;
//	}
//	
//	protected void unsetFactory(SAXParserFactory factory){
//		this.factory = factory;
//	}
	
	
//	public static EasyObjectXMLTransformerImpl getMock() {
//		
//		EasyObjectXMLTransformerImpl imp = (EasyObjectXMLTransformerImpl) EasyServiceManagerImpMock.getMock().getService(IEasyObjectXMLTransformer.class.getName(), null);
//		if (imp == null){
//			EasyServiceManagerImpMock.getMock().registerService(IEasyObjectXMLTransformer.class.getName(), null, imp = new EasyObjectXMLTransformerImpl());
//		}
//		return imp;
//	}
	
	public boolean isPrettyPrint() {
		return prettyPrint;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}

	public <T> T parse(URL url, Class<T> type)  throws XMLParseException {
		InputStream in = null;
		try{
			try {
				in = url.openStream();
			} catch (IOException e) {
				throw new XMLParseException(e.getMessage(),e);
			}
			return parse(in, type);
		}finally{
			if (in != null){
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		
	}
	
	public <T> T parse(URL url, Class<T> type, XMLMappingInfo xmi) throws XMLParseException {
		InputStream in = null;
		try{
			try {
				in = url.openStream();
			} catch (IOException e) {
				throw new XMLParseException(e.getMessage(),e);
			}
			return parse(in, type, xmi);
		}finally{
			if (in != null){
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	
	public <T> T parse(InputStream in, Class<T> type, CollectionStyleType cs, FieldStyleType fs) throws XMLParseException {
	     XMLMappingInfo xmi = new XMLMappingInfo();
	     initXMLInfo(type, null, xmi);
	     xmi.setFieldStyle(fs);
	     xmi.setCollectionStyle(cs);
	     return parse(in, type, xmi);
	        
	}
	
	public <T> T parse(InputStream in, Class<T> type, XMLMappingInfo xmi) throws XMLParseException {
		T t = null;
		try {
			if (factory == null){
				factory = SAXParserFactory.newInstance();
			}
	        factory.setNamespaceAware(true);
	        SAXParser saxParser = factory.newSAXParser();
	        EasyObjectXMLTransformerHandler handler = new EasyObjectXMLTransformerHandler(type);
	        handler.trimText = prettyPrint;
	        handler.setXMLMappingInfo(xmi);
	        saxParser.parse(in, handler);
	        if (handler.error != null){
//	        	handler.error.printStackTrace();
	        	throw  new XMLParseException(handler.error.getMessage(),handler.error);
	        }
	        t = (T)handler.obj;
		} catch (Exception e) {
			if (e instanceof XMLParseException){
				throw (XMLParseException)e;
			}
//			throw XMLParseException.wrapFor(e, new XMLParseException());
			if (e instanceof RuntimeException){
				throw (RuntimeException)e;
			}
			throw new  XMLParseException(e.getMessage(), e);
		}
		return t;
	}

	public <T> T parse(InputStream in, Class<T> type) throws XMLParseException {
		XMLMappingInfo xmi = new XMLMappingInfo();
	    initXMLInfo(type, null, xmi);
		return parse(in, type, xmi);
	}


	public <T> T parse(String str, Class<T> type, XMLMappingInfo xmi) throws XMLParseException {
		String h = "encoding=\"";
		int ecs = str.indexOf(h);
		String encode = null;
		if (ecs > 0){
			encode = str.substring(ecs+h.length(), str.indexOf("\"", ecs+h.length()));
		}
		try {
			return parse(encode == null ? new ByteArrayInputStream(str.getBytes()) :  new ByteArrayInputStream(str.getBytes(encode)), type, xmi) ;
		} catch (UnsupportedEncodingException e) {
			throw new XMLParseException(e.getMessage(),e);
		}
		
	}
	
	public <T> T parse(String str, Class<T> type) throws XMLParseException {
		String h = "encoding=\"";
		int ecs = str.indexOf(h);
		String encode = null;
		if (ecs > 0){
			encode = str.substring(ecs+h.length(), str.indexOf("\"", ecs+h.length()));
		}
		try {
			return parse(encode == null ? new ByteArrayInputStream(str.getBytes()) :  new ByteArrayInputStream(str.getBytes(encode)), type) ;
		} catch (UnsupportedEncodingException e) {
			throw new XMLParseException(e.getMessage(),e);
		}
		
	}

	
	public static HashSet<Class> simpleTypeSet = new HashSet<Class>();
	public static Map<Class, Object > simpleTypeSampleMap = new HashMap<Class, Object>();
	
	static {
		simpleTypeSet.add(String.class);
		simpleTypeSampleMap.put(String.class, new String(""));
		simpleTypeSet.add(Date.class);
		simpleTypeSampleMap.put(Date.class, new Date(0));
		simpleTypeSet.add(Boolean.class);
		simpleTypeSampleMap.put(Boolean.class, new Boolean(true));
		simpleTypeSet.add(Long.class);
		simpleTypeSampleMap.put(Long.class, new Long(0));
		simpleTypeSet.add(Integer.class);
		simpleTypeSampleMap.put(Integer.class, new Integer(0));
		simpleTypeSet.add(Short.class);
		simpleTypeSampleMap.put(Short.class, new Short((short)0));
		simpleTypeSet.add(Float.class);
		simpleTypeSampleMap.put(Integer.class, new Integer(0));
		simpleTypeSet.add(Double.class);
		simpleTypeSampleMap.put(Double.class, new Double(0));
		
		simpleTypeSet.add(Boolean.TYPE);
		simpleTypeSampleMap.put(Boolean.TYPE, new Boolean(true));
		simpleTypeSet.add(Long.TYPE);
		simpleTypeSampleMap.put(Long.TYPE, new Long(0));
		simpleTypeSet.add(Integer.TYPE);
		simpleTypeSampleMap.put(Integer.TYPE, new Integer(0));
		simpleTypeSet.add(Short.TYPE);
		simpleTypeSampleMap.put(Short.TYPE, new Short((short)0));
		simpleTypeSet.add(Float.TYPE);
		simpleTypeSampleMap.put(Integer.TYPE, new Integer(0));
		simpleTypeSet.add(Double.TYPE);
		simpleTypeSampleMap.put(Double.TYPE, new Double(0));
	}
	
	public static Object getsimpleTypeSample(Class st){
		return simpleTypeSampleMap.get(st);
	}
	
	public static boolean isSimpleType(Object o){
		return isSimpleType(o.getClass());
	}
	
	public static boolean isSimpleType(Class cls){
		return simpleTypeSet.contains(cls);
	}
	
	PrintStream print(PrintStream out, String s, int level){
	    if (prettyPrint){
			for (int i = 0; i < level; i++){
				out.print(" ");
			}
	    }
		out.print(s);
		return out;
	}
	
	PrintStream println(PrintStream out, String s, int level){
	    if (prettyPrint){
			for (int i = 0; i < level; i++){
				out.print(" ");
			}
	    }
		out.print(s);
		if (prettyPrint){
			out.print("\n");
		}
		return out;
	}
	
	StringBuilder getTagFront(String tag, XMLNamespaceInfo xni){
		StringBuilder nstr = new StringBuilder("<");
		String prefix = xni.getPrefix();
		String schema = xni.getSchemaDeclare();
		String[] ns = xni.getNamespaces();
		if (prefix != null && prefix.length() >  0){
			nstr.append(prefix).append(":");
		}
		nstr.append(tag);
		if (schema != null && schema.length() > 0){
			nstr.append(" ").append(schema);
		}
		if (ns != null && ns.length > 0){
			for (String n: ns){
				nstr.append(" ").append(n);
			}
		}
		return nstr;
	}
	
	StringBuilder getTagEnd(String tag, XMLNamespaceInfo xni){
		StringBuilder nstr = new StringBuilder("</");
		String prefix = xni.getPrefix();
		if (prefix != null && prefix.length() >  0){
			nstr.append(prefix).append(":");
		}
		nstr.append(tag).append(">");
		return nstr;
	}
	
	PrintStream startTag(PrintStream out, String tag, XMLNamespaceInfo xni, int level, boolean hasChild, boolean hasText){
		StringBuilder nstr = getTagFront(tag, xni);
		if (hasChild || hasText){
			nstr.append(">");
		}else{
			nstr.append("/>");
			if (prettyPrint){
				nstr.append("\n");
	        }
		}
		if (prettyPrint && hasChild){
			nstr.append("\n");
	    }

		print(out, nstr.toString(), level);
		return out;
	}
	
	PrintStream endTag(PrintStream out, String tag, XMLNamespaceInfo xni, boolean hasChild, String text, int level){
//		if (text != null){
//			out.print(text);
//		}
		StringBuilder estr = getTagEnd(tag, xni);
		if (!hasChild){
			level = 0;
		}
		println(out, estr.toString(), level);
		return out;
	}
	
	PrintStream startTag(PrintStream out, XMLMappingInfo xmi, XMLNamespaceInfo xni, List<IMember> spls, Object o, int level, boolean hasChild, boolean hasText) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ConvertException{
		StringBuilder nstr = getTagFront(xmi.getTag(), xni);
		XMLMappingInfo mxmi = new XMLMappingInfo();
		if (spls != null && !spls.isEmpty()){
			for (IMember m : spls){
				Object v = m.getValue(o);
				mxmi = getMemeberTag(m, xmi, null, mxmi.reset());
				if (v != null){
					nstr.append(" ").append(mxmi.getTag()).append("=").append("\"")
					.append(ZStringUtils.escapeXML(convertSimpleAttr(v, mxmi), false).toString()).append("\"");
				}
			}
		}
		if (hasChild || hasText){
			nstr.append(">");
		}else{
//			nstr.append("/>\n");
			nstr.append("/>");
			if (prettyPrint){
				nstr.append("\n");
		    }
		}
		if (prettyPrint && hasChild){
			nstr.append("\n");
	    }

		print(out, nstr.toString(), level);
		return out;
	}
	
	PrintStream startTag(PrintStream out, String tag, XMLNamespaceInfo xni, String attr, String attrValue, int level, boolean hasChild, boolean hasText) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		StringBuilder nstr = getTagFront(tag, xni);
//		print(out, "<"+tag, level);
		if (attrValue != null){
			nstr.append(" ").append(attr).append("=").append("\"").append(ZStringUtils.escapeXML(attrValue, false).toString()).append("\"");
		}
		if (hasChild || hasText){
			nstr.append(">");
		}else{
			nstr.append("/>");
			if (prettyPrint){
				nstr.append("\n");
			}
		}
		if (prettyPrint && hasChild){
			nstr.append("\n");
		}
		print(out, nstr.toString(), level);
		return out;
	}
	
	String getText(Object o){
		XMLClassInfo ci = EasyObjectXMLTransformerHandler.cpl.getClassInfo(o.getClass(), true);
		IMember tm = ci.getTextPropertity();
		if (tm != null){
			try {
				Object t = tm.getValue(o);
				return t == null ? null : t.toString();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	PrintStream export(Entry en, XMLNamespaceInfo xni, XMLMappingInfo xmi, XMLMappingInfo mxmi, XMLClassInfo ci, PrintStream out, int level,  Map<Object, Integer> refs){
		Class[] types = xmi.itemTypes();
		String entryTag = xmi.childTag() == null || xmi.childTag().length() == 0 ? "entry" : xmi.childTag();
		String keyTag = xmi.keyTag() == null || xmi.keyTag().length() == 0 ? "key" : xmi.keyTag();
		String valueTag = xmi.valueTag() == null || xmi.valueTag().length() == 0 ? "key" : xmi.valueTag();
		StringBuilder nstr = getTagFront(xmi.childTag(), xni);
		boolean attrKey = false;
		boolean attrValue = false;
		try{
			if (xmi.fieldStyle() == FieldStyleType.ATTR || xmi.fieldStyle() == FieldStyleType.ATTR_INITSUPERCASE){
				if (isSimpleType(types[1])){
					nstr.append(" ").append(keyTag).append("=\"").append(ZStringUtils.escapeXML(converter.restoreString(en.getKey()),false)).append("\"");
					attrKey = true;
				}
				if (isSimpleType(types[2])){
					nstr.append(" ").append(valueTag).append("=\"").append(ZStringUtils.escapeXML(converter.restoreString(en.getValue()),false)).append("\"");
					attrValue = true;
				}
				if (attrKey && attrValue){
					nstr.append("/>");
				}else {
					nstr.append(">");
				}
				if (prettyPrint){
					nstr.append("\n");
			    }
				print(out, nstr.toString(), level);
			}
		}catch (ConvertException e) {
			throw new RuntimeException("can not convert to String", e);
		}
		
		if (!attrKey){
			mxmi.setTag(keyTag);
			export(en.getKey(), xni, mxmi,  null, out, level+1, refs);
		}
		if (!attrValue){
			mxmi.setTag(valueTag);
			export(en.getValue(), xni, mxmi,  null, out, level+1, refs);
		}
		mxmi.setTag(entryTag);
		if (!attrKey ||  !attrValue){
			endTag(out, xmi.childTag(), xni, true, null, level);
		}
		return out;
	}
	
	PrintStream export(Map map, XMLNamespaceInfo xni, XMLMappingInfo xmi, PrintStream out, int level, Map<Object, Integer> refs){
		int childLevel = level;
		String text = getText(map);
		boolean hasChild = !map.isEmpty();
		boolean hasText = (text != null);
		if (xmi.getCollectionStyle() ==  CollectionStyleType.EMBED){
			startTag(out, xmi.getTag(), xni, level, hasChild, hasText);
			childLevel = level + 1;
		}
		if (!map.isEmpty()){
			XMLClassInfo ci = getXMLClassInfo(xmi, map.values().iterator().next());
			XMLMappingInfo mxmi = new XMLMappingInfo();
			XMLNamespaceInfo mxni = new XMLNamespaceInfo();
			mergeCollectionItemMappingInfo(ci.getType(), xni, xmi, mxni, mxmi);
			
			if (xmi.valueTag().length() > 0){
				for (Entry en : (Set<Entry>)map.entrySet()){
					export(en, mxni, xmi, mxmi, null, out, childLevel, refs);
				}
			}else{
				for (Object o : map.values()){
					export(o, mxni, mxmi, null, out, childLevel, refs);
				}
			}
		}
		
		if (xmi.getCollectionStyle() ==  CollectionStyleType.EMBED){
			if (hasChild || hasText){
				endTag(out, xmi.getTag(), xni, hasChild, text, level);
			}
		}
		return out;
	}

	private XMLClassInfo getXMLClassInfo(XMLMappingInfo xmi, Object sample) {
		Class<? extends Object> it = null;
		if (xmi == null || xmi.itemTypes() == null || xmi.itemTypes().length == 0){
			return EasyObjectXMLTransformerHandler.cpl.getClassInfo(sample.getClass(), true);
		}
		if ( xmi.getItemTypes()[0] == MapEntry.class ){
			it =  xmi.itemTypes()[0];
		}else{
			it = xmi.itemTypes()[xmi.itemTypes().length - 1];
		}
		
		XMLClassInfo ci = null;
		if (it == MapEntry.class){
			ci = ( new MapEntryClassInfo(Entry.class, xmi.keyTag(), xmi.valueTag(),  xmi.itemTypes()[1], xmi.itemTypes()[2]));
		}else{
			ci = (EasyObjectXMLTransformerHandler.cpl.getClassInfo(it, true));
		}
		return ci;
	}
	
	/**
	 * 
	 * @param itemType
	 * @param xni container xni
	 * @param xmi container xmi
	 * @param mxmi item xmi
	 */
	static void mergeCollectionItemMappingInfo(Class itemType, XMLNamespaceInfo xni, XMLMappingInfo xmi, XMLNamespaceInfo mxni, XMLMappingInfo mxmi){
		
		XMLMappingInfo tmpxmi = new XMLMappingInfo();
		XMLNamespaceInfo tmpxni = new XMLNamespaceInfo();
		initXMLInfoOrginal(itemType, tmpxni, tmpxmi);
		if (mxni != null){
			mxni.inherit(tmpxni);
		}
		if (xni != null){
			mxni.inherit(xni);
		}
		mxmi.inherit(tmpxmi);
		mxmi.inherit(xmi);
		if (xmi.childTag().length() > 0){
			mxmi.setTag(xmi.childTag());
		}
		if (mxmi.tag().length() == 0){
			mxmi.setTag(tmpxmi.tag());
		}
		if (mxmi.getOrderedProperties().length == 0) {
			mxmi.setOrderedProperties(tmpxmi.getOrderedProperties());
		}
		if (xmi.childSimpleObjValueAttr().length() > 0){
			mxmi.setChildSimpleObjValueAttr(xmi.getChildSimpleObjValueAttr());
		}
	}
	
	PrintStream exportArray(Object c, XMLNamespaceInfo xni, XMLMappingInfo xmi, PrintStream out, int level, Map<Object, Integer> refs){
		int childLevel = level;
		String text = getText(c);
		int length = Array.getLength(c);
		boolean hasChild = length > 0;
		boolean hasText = false;
		if (xmi.getCollectionStyle() ==  CollectionStyleType.EMBED){
			startTag(out, xmi.getTag(), xni, level, hasChild, hasText);
			childLevel = level + 1;
		}
//		XMLMappingInfo mxmi = new XMLMappingInfo(xmi.getChildTag(), "", xmi.getKeyTag() , xmi.getValueTag(),xmi.getCollectionStyle(), xmi.getFieldStyle(), xmi.getChildSimpleObjValueAttr());
//		mxmi.inherit(xmi);
//		XMLNamespaceInfo mxni = xni;
		if (length > 0){
			XMLClassInfo ci = getXMLClassInfo(xmi, Array.get(c, 0));
			XMLMappingInfo mxmi = new XMLMappingInfo();
			XMLNamespaceInfo mxni = new XMLNamespaceInfo();
			mergeCollectionItemMappingInfo(ci.getType(), xni, xmi, mxni, mxmi);
			
			for (int i = 0 ; i < length; i++){
//				mxmi.reset();
//				mxmi.inherit(xmi);
				export(Array.get(c, i), mxni, mxmi, ci, out, childLevel, refs);
			}
		}
		
		if (xmi.getCollectionStyle() ==  CollectionStyleType.EMBED){
			if (hasChild || hasText){
				endTag(out, xmi.getTag(), xni, hasChild, text, level);
			}
		}
		return out;
	}
	
	PrintStream export(Collection c, XMLNamespaceInfo xni, XMLMappingInfo xmi, PrintStream out, int level, Map<Object, Integer> refs){
		int childLevel = level;
		String text = getText(c);
		boolean hasChild = !c.isEmpty();
		boolean hasText = (text != null);
		if (xmi.getCollectionStyle() ==  CollectionStyleType.EMBED){
			startTag(out, xmi.getTag(), xni, level, hasChild, hasText);
			childLevel = level + 1;
		}
		if (!c.isEmpty()) {
			Object fitem = null;
			for (Object oi : c){
				if (oi != null){
					fitem = oi;
				}
			}
			XMLClassInfo ci = getXMLClassInfo(xmi,fitem );
			XMLMappingInfo mxmi = new XMLMappingInfo();
			XMLNamespaceInfo mxni = new XMLNamespaceInfo();
			if (fitem != null){
				initXMLInfoOrginal(fitem.getClass(), mxni, mxmi);
			}
			mergeCollectionItemMappingInfo(ci.getType(), xni, xmi, mxni, mxmi);
			for (Object o : c){
				if (o == null){
					if (xmi.getItemTypes().length > 0 && converter.canConvertToSimpleString(xmi.getItemTypes()[0])){
						startTag(out, mxmi.getTag(), mxni, childLevel, false,false);
					}
				}else{
					export(o, mxni, mxmi, ci, out, childLevel, refs);
				}
			}
		}
		if (xmi.getCollectionStyle() ==  CollectionStyleType.EMBED){
			if (hasChild || hasText){
				endTag(out, xmi.getTag(), xni, hasChild, text, level);
			}
		}
		return out;
	}
	
	

	@SuppressWarnings("unchecked")
	private PrintStream export(Object o, XMLNamespaceInfo xni, XMLMappingInfo xmi, XMLClassInfo ci, PrintStream out, int level, Map<Object, Integer> refs) {
		
		if (o == null){
			return out;
		}
		if (ci == null){
			ci = EasyObjectXMLTransformerHandler.cpl.getClassInfo(o.getClass(), true);
		}
		Class<? extends Object> type = o.getClass();
		if (xni == null){
			XMLNamespace xn = type.getAnnotation(XMLNamespace.class);
			xni = new XMLNamespaceInfo();
			if (xn != null){
				xni.shadowBy(xn);
			}
		}
		if (xmi == null){
			XMLMapping xm = type.getAnnotation(XMLMapping.class);
			xmi = new XMLMappingInfo();
			if (xm != null){
				xmi.shadowBy(xm);
			}
		}
		
		if (xmi.tag().length() == 0){
			if (type.isArray()){
				String at = type.getSimpleName();
				xmi.setTag(at.substring(0, at.length()-3)+"Array");
			}else{
				xmi.setTag(type.getSimpleName());
			}
		}
		IConverter formatedConverter = xmi.getFormatedConverter();
		
		if (xmi.getExportCompressType() == ExportCompressType.REFRENCE && !isSimpleType(o)){
			Integer rid = refs.get(o);
			if (rid != null){
				StringBuilder refStr = new StringBuilder();
				refStr.append("<").append(xmi.tag()).append(" ref.id=\"").append(rid).append("\"/>");
				if (prettyPrint){
					refStr.append("\n");
			    }
				return print(out, refStr.toString(), level);
			}
			refs.put(o, refs.size());
		}
		if (o instanceof Map) {// export map
			Map map = (Map) o;
			if (map.isEmpty()){
				refs.remove(o);
				return out;
			}
			return export(map, xni, xmi, out, level, refs);
		} else if (o instanceof Collection) {// export Collection
			Collection c = (Collection) o;
			if (c.isEmpty() ){
				refs.remove(o);
				return out;
			}
			return export(c, xni, xmi, out, level, refs);
		}else if (o.getClass().isArray() && !converter.canConvertToSimpleString(o.getClass())){
			if (Array.getLength(o) == 0 ){
				refs.remove(o);
				return out;
			}
			return exportArray(o, xni, xmi, out, level, refs);
		}
		
		List<IMember> splList = null;
		List<IMember> cpxList = null;
		boolean hasChild = false;
		boolean hasText = false;
		String text = null;
		try {
//			if (isSimpleType(o) || o.getClass().isEnum()) {
			if (converter.canConvertToSimpleString(o.getClass())){
				// find value attr
				String attr = xmi.getChildSimpleObjValueAttr();
				String attrvalue = convertSimpleAttr(o, xmi);
				if (attr != null && attr.length() > 0) {
					startTag(out, xmi.getTag(),xni, attr, attrvalue, level, hasChild, hasText);
				}else{
//					text = o.toString();
					text = attrvalue;
					if (text != null){
						hasText = true;
					}
					startTag(out, xmi.getTag(),xni, level, hasChild, hasText);
					if (text != null){
						out.print(ZStringUtils.escapeXML(text, false));
					}
				}
			} else {
				
//				XMLClassInfo ci = null;
//				
//				if (o instanceof Entry ){
//					ci = EasyObjectXMLTransformerHandler.cpl.getClassInfo(Entry.class, true);
//				}else{
//					ci = EasyObjectXMLTransformerHandler.cpl.getClassInfo(o.getClass(), true);
//				}
				List<IMember> pts = (List<IMember>)ci.getProperties();
				if (xmi.getOrderedProperties().length > 0) {
					pts = new ArrayList<IMember>();
					for (String p : xmi.getOrderedProperties()) {
						pts.add( ci.getProperty(p) );
					}
				}
				
				splList = new ArrayList<IMember>();
				cpxList = new ArrayList<IMember>();
				for (IMember m : pts) {
					if (m.getAnnotation(XMLIgnore.class) != null || m.getDeclaringClass() == Object.class) {
						continue;
					}
					
					if (m != ci.getTextPropertity() && ( isSimpleType(m.getType()) || m.getType().isEnum() ) && ( xmi.getFieldStyle() == FieldStyleType.ATTR || xmi.getFieldStyle() == FieldStyleType.ATTR_INITSUPERCASE)) {
						splList.add(m);
					} else {
						hasChild = true;
						cpxList.add(m);
					}
				}

				// export simple field
				startTag(out, xmi, xni, splList, o, level, hasChild, hasText);
				
				
				if (cpxList != null && !cpxList.isEmpty()) {
					XMLMappingInfo mxmi = new XMLMappingInfo();
					XMLNamespaceInfo mxni = new XMLNamespaceInfo();
					for (IMember m : cpxList) {
						
						if (m == ci.getTextPropertity()){
							Object texto =  m.getValue(o);
							if (texto != null){
								text = texto.toString();
								hasText = true;
								out.print(ZStringUtils.escapeXML(text,false));
								if (prettyPrint){
									out.print("\n");
								}
							}
							continue;
						}
						mxmi.reset();
						mxni.reset();
						Object v = m.getValue(o);
						if (v == null) {
							continue;
						}
						/*if (v instanceof Map) {// export map
							Map map = (Map) v;
							getMapMemeberTags(m, xmi, mxni, mxmi);
							mxmi.inherit(xmi);
							mxni.inherit(xni);
							export(map, mxni, mxmi, out, level + 1);
						} else if (v instanceof Collection) {// export Collection
							Collection c = (Collection) v;
							getCollectionMemeberTags(m, xmi, mxni, mxmi);
							mxmi.inherit(xmi);
							mxni.inherit(xni);
							export(c, mxni, mxmi, out, level + 1);
						}else if (v.getClass().isArray()){
							Object[] c = (Object[]) v;
							getArrayMemeberTags(m, xmi, mxni, mxmi);
							mxmi.inherit(xmi);
							mxni.inherit(xni);
							export(c, mxni, mxmi, out, level + 1);
						}
						else {// export general object
							getMemeberTag(m, xmi, mxni, mxmi);
							mxmi.inherit(xmi);
							mxni.inherit(xni);
							export(v, mxni, mxmi, out, level + 1);
							// throw new XMLExportException("can not support ");
						}*/
						getGeneralMemeberTags(m, xmi, mxni, mxmi);
						mxmi.inherit(xmi);
						mxni.inherit(xni);
						Map<String,String> typeTagMapping = mxmi.getTypeTagMapping();
						if (!typeTagMapping.isEmpty()){
							String mappedTag = typeTagMapping.get(v.getClass().getName());
							if (mappedTag != null){
								mxmi.setTag(mappedTag);
							}
						}
						export(v, mxni, mxmi,  EasyObjectXMLTransformerHandler.cpl.getClassInfo(v.getClass(), true), out, level + 1, refs);
					}
				}
				
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ConvertException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (hasChild || hasText) {
			endTag(out, xmi.getTag(), xni, hasChild, text, level);
		}
		return out;
	}

	protected String convertSimpleAttr(Object o, XMLMappingInfo xmi) throws ConvertException {
		IConverter formatedConverter = xmi.getFormatedConverter();
		String attrvalue = null;
		String format = xmi.getFormat();
		if (formatedConverter != null){
			attrvalue = (String)((format != null && format.length() != 0) ? formatedConverter.restore(o, formatedConverter.parseFormat(format)) : formatedConverter.restore(o) );
		}else {
			attrvalue = (format != null && format.length() != 0) ? converter.convert(o,String.class, format) :  converter.convert(o, String.class);
		}
		return attrvalue;
	}
	
	private String getXmlHeader(String encode){
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"").append(encode).append("\"?>");
		if (prettyPrint){
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public PrintStream export(Object o, XMLNamespaceInfo xni, XMLMappingInfo xmi, PrintStream out, String encode, boolean needHead){
		if (needHead){
			out.print(getXmlHeader(encode));
		}
		export(o,xni, xmi, out, 0);
		return out;
	}
	
	public String export(Object o, XMLNamespaceInfo xni, XMLMappingInfo xmi, String encode, boolean needHead) throws UnsupportedEncodingException{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bo, true, encode);
		if (needHead){
			out.print(getXmlHeader(encode));
		}
		export(o,xni, xmi, out, 0);
		return new String(bo.toByteArray(), encode);
	}
	
	public PrintStream export(Object o, String tag, PrintStream out, String encode, boolean needHead){
		if (needHead){
			out.print(getXmlHeader(encode));
		}
		XMLMappingInfo xmi = new XMLMappingInfo();
		XMLNamespaceInfo xni = new XMLNamespaceInfo();
		initXMLInfo(o.getClass(), xni, xmi);
		if (tag != null){
			xmi.setTag(tag);
		}
//		xmi.setCollectionStyle(CollectionStyleType.EMBED);
//		xmi.setFieldStyle(FieldStyleType.ATTR);
//		XMLNamespaceInfo xni = null;
		export(o, xni, xmi, out, 0);
		return out;
	}
	
	public OutputStream export(Object o, String tag, OutputStream out, String encode, boolean needHead) throws UnsupportedEncodingException{
		export(o, tag, new PrintStream(out, true, encode),encode,  needHead);
		return out;
	}
	
	public String export(Object o){
		String rt = null;
		try {
			rt = export(o, null, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return rt;
	}
	
	public String export(Object o, String tag, String encode) throws UnsupportedEncodingException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		export(o, tag, new PrintStream(out, true, encode),  encode, true);
		return new String(out.toByteArray(), encode);
	}

	public PrintStream export(Object o, XMLNamespaceInfo xni, XMLMappingInfo xmi, PrintStream out, int level) {
		return export(o, xni, xmi, null, out, level, new HashMap<Object, Integer>());
	}
	
	public IEasyObjectXMLTransformer createNewInstance(){
		EasyObjectXMLTransformerImpl rt = new EasyObjectXMLTransformerImpl();
		rt.factory = factory;
		return rt;
	}
}
