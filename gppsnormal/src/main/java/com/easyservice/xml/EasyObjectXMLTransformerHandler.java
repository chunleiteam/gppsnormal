package com.easyservice.xml;

import java.lang.reflect.Array;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.easyservice.reflect.IMember;
import com.easyservice.support.ClassInfo;
import com.easyservice.support.convert.ConvertException;
import com.easyservice.support.convert.EasyConverter;
import com.easyservice.support.convert.IConverter;
import com.easyservice.xml.annotation.CollectionStyleType;
import com.easyservice.xml.annotation.XMLMapping;
import static com.easyservice.xml.XMLClassUtil.*;

public class EasyObjectXMLTransformerHandler extends DefaultHandler implements XMLTransformerHandler {

	Throwable error = null;
	Object obj;
	Class rootClass;
	XMLTransformerHandler curHandler;
	static XMLClassInfoPool cpl = new XMLClassInfoPool();
	XMLMappingInfo xMLMappingInfo;
	XMLClassInfo xMLClassInfo;
//	static Object NULL_MAPENTRY_KEY = new Object();
//	static StringConvertSpace DEFAULT_STRINGCONVERT_SPACE = new StringConvertSpace();
	
	EasyConverter converter = new EasyConverter();
	Map<Integer, Object> refs = new HashMap<Integer, Object>();
	
	String lname;
	String qname;
	String uri;
	StringBuffer text;
	Locator locator;
	boolean trimText;
//	/**
//	 * == NULL_MAPENTRY_KEY : type is mapEntry
//	 * != NULL_MAPENTRY_KEY && != null :  type is mapEntry and model is value?
//	 */
//	Object mapEntryKey;

//	public Object getMapEntryKey() {
//		return mapEntryKey;
//	}
//
//	public void setMapEntryKey(Object mapEntryKey) {
//		this.mapEntryKey = mapEntryKey;
//	}
	
	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getQname() {
		return qname;
	}

	public void setQname(String qname) {
		this.qname = qname;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public EasyObjectXMLTransformerHandler(Class rootClass){
		this.rootClass = rootClass;
	}
	
	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}
	
	@Override
	public void endDocument() throws SAXException {
//		obj = curHandler.getObj();
		
		if (obj instanceof List && rootClass.isArray()){
			List tmplist = (List) obj;
			Object[] ao = (Object[])Array.newInstance(xMLMappingInfo.getItemTypes()[0], tmplist.size());
			tmplist.toArray( ao );
			obj = ao;
		}
	}

	@Override
	public void startDocument() throws SAXException {
		curHandler = this;
	}
	
	
	
/*	@SuppressWarnings("unchecked")
	static Object processAttrs(Object o, Attributes attrs) throws ConvertException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (o != null){
			if (EasyObjectXMLTransformerImpl.isSimpleType(o)){
				if (attrs.getLength() > 0){
					return EasyObjectAccesser.convertString(o.getClass(), attrs.getValue(0));
				}else{
					return o;
				}
				
			}
			XMLClassInfo ci = cpl.getClassInfo(o.getClass(), true);
			for (int i = 0; i < attrs.getLength(); i++){
				String lname = attrs.getLocalName(i);
				String qname = attrs.getQName(i);
				String uri = attrs.getURI(i);
				IMember im = ci.getProperty(uri, lname, qname);
				if (im == null){
					throw new NoSuchFieldException(lname + " : no such field or mapping in class " + ci.getType());
				}
				EasyObjectAccesser.setProperty(attrs.getValue(i), o, im);
			}
		}
		return o;
	}*/
	
	
	@SuppressWarnings("unchecked")
	Object processAttrs(XMLTransformerHandler handler, Attributes attrs) throws ConvertException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		
		if ( "ref.id".equals( attrs.getLocalName(0) ) ){
			return  handler.getRoot().refs.get(Integer.parseInt(attrs.getValue(0)));
		}
		
		Class type = handler.getType();
			if (converter.canConvertToSimpleString(type)){
				if (attrs.getLength() > 0){
					return restoreSimpleAttr(attrs.getValue(0), type, handler.getXMLMappingInfo());// converter.convertString(type, attrs.getValue(0));
				}else{
					return null;
				}
				
			}
			Object o = newInstance(type);
			XMLClassInfo ci = handler.getXMLClassInfo();
			XMLMappingInfo mxmi = new XMLMappingInfo();
			for (int i = 0; i < attrs.getLength(); i++){
				String lname = attrs.getLocalName(i);
				String qname = attrs.getQName(i);
				String uri = attrs.getURI(i);
				IMember im = ci.getProperty(uri, lname, qname);
				if (im == null){
					throw new NoSuchFieldException(lname + " : no such field or mapping in class " + ci.getType());
				}
				String v = attrs.getValue(i);
				mxmi.reset();
				mxmi = XMLClassUtil.getMemeberTag(im, handler.getXMLMappingInfo(), null, mxmi);
				if (!im.isReadOnly()){
					im.setValue(o,  restoreSimpleAttr(v, im.getType(), mxmi) );
				}
			}
			handler.getRoot().refs.put(handler.getRoot().refs.size(), o);
			return o;
		
		
	}
	
	@SuppressWarnings("unchecked")
	public static Object addItemToCollectionOrMap(Object cm, Object item, Object key, XMLMappingInfo mapping) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException{
		if (cm instanceof Collection) {
			((Collection) cm).add(item);
		}else if (cm.getClass().isArray()){
			int curlen = Array.getLength(cm);
			Object newcm = Array.newInstance(item.getClass(),  curlen + 1);
			System.arraycopy(cm, 0, newcm, 0, curlen);
			Array.set(newcm, curlen , item);
			cm = newcm;
		}
		else {
			if (item instanceof MapEntry){
				MapEntry me = (MapEntry)item;
				key = me.getKey();
				item = me.getValue();
			}
			if (key == null ) {
				if (mapping != null && mapping.keyTag() != null ) {
					if (item != null){
						XMLClassInfo rtci = cpl.getClassInfo(item.getClass(), true);
						IMember m = rtci.getProperty(mapping.keyTag());
						if (m != null) {
							key = m.getValue(item);
						}
					}
				}
				if (key == null && item == null){
					return cm;
				}
				if (key == null ) {
					key = item.toString();
				}
			}
			((Map) cm).put(key, item);
		}
		return cm;
	}
	
	 @SuppressWarnings("unchecked")
	void placeObject(String uri, String lname, String qname, EasyObjectNodeHandler handler) throws SAXException{
		try{
			if (curHandler != this){
				Class c = curHandler.getType();
				XMLClassInfo ci = curHandler.getXMLClassInfo();
				IMember p = ci.getProperty(uri, lname, qname);
				if (p == null){ // is in collection or map
					p = ci.getPropertyByChildTag(uri, lname, qname);
					XMLMapping xm = ci.getXMLMappingByChildTag(uri, lname, qname);
					XMLMappingInfo xmi = null;
					if (p != null){ // flat style for child
						xmi = new XMLMappingInfo(xm);
						Object cm = p.getValue(curHandler.getObj());
						if (cm == null){ // flat style may not initalize collection
							Class cmtype = p.getType();
							if (cmtype.isArray()){
								cm = Array.newInstance(p.getItemTypes()[0], 0);
							}else{
								cm = getGeneralGenericImplement(p.getType()).newInstance();
							}
							p.setValue(curHandler.getObj(), cm);
						}
						Object newcm = addItemToCollectionOrMap(cm, handler.getObj(), null, xmi);
						if (newcm != cm){
							p.setValue(curHandler.getObj(), cm);
						}
					}else{
						xmi = curHandler.getXMLMappingInfo();
						addItemToCollectionOrMap(curHandler.getObj(), handler.getObj(), null, xmi);
					}
				}else{
//					if (isCollectionOrMap(p.getType())){
//						
//					}else{
						if (p.getType().isArray()){
							List tmplist = (List) handler.getObj();
							Object[] ao = (Object[])Array.newInstance(p.getItemTypes()[0], tmplist.size());
							tmplist.toArray( ao );
							handler.setObj( ao );
						}
						p.setValue(curHandler.getObj(), handler.getObj());
//					}
				}
			
			}
			
		}catch(Exception e){
			throw new XMLTransformerException(e.getMessage(), e,  locator.getLineNumber(), locator.getColumnNumber());
		}
	}
	
	public static Object newInstance(Class type) throws InstantiationException, IllegalAccessException {
		if (EasyObjectXMLTransformerImpl.isSimpleType(type)){
			 return EasyObjectXMLTransformerImpl.getsimpleTypeSample(type);
		}else if (type.isArray()){
			return new ArrayList();
		}else if (isCollectionOrMap(type)){
			Class imp = generalGenericImplementMap.get(type);
			if (imp != null){
				return imp.newInstance();
			}
		}else if (type.isEnum()){
			return null;
		}
		try{
			return type.newInstance();
		}catch (Exception e) {
			e.printStackTrace();
			return null;// TODO: handle exception
		}
	}
	
	
	@SuppressWarnings("unchecked")
	Object createObject(String uri, String lname, String qname, Attributes attrs, EasyObjectNodeHandler handler) throws SAXException{
		try {
			Object rt = null;
			if (curHandler == this){
//				 rootClass = this.getXMLMappingInfo().getMappedType(lname, rootClass, Thread.currentThread().getContextClassLoader());
//				 handler.setType(this.getXMLMappingInfo().rebuildAndGetType(lname, rootClass, Thread.currentThread().getContextClassLoader()));
				 XMLMappingInfo xmi = this.getXMLMappingInfo();
				 if (xmi != null){
					 Map<String, String> typeTagMapping = xmi.getTypeTagMapping();
					 if (typeTagMapping != null && !typeTagMapping.isEmpty()){
						 for (Entry<String, String> te : typeTagMapping.entrySet()){
							 if (te.getValue().equals(lname)){
								 handler.setType(ClassInfo.loadClass(te.getKey()));
								 break;
							 }
						 }
					 }
				 }
				 if (handler.getType() == null){
					 handler.setType(rootClass);
				 }
				 handler.setXMLMappingInfo(this.getXMLMappingInfo());
				 handler.setXMLClassInfo(cpl.getClassInfo(handler.getType(), true));
				 rt = obj = processAttrs(handler, attrs);
			}else{
				Class c = curHandler.getObj().getClass();
//				XMLClassInfo ci  = cpl.getClassInfo(c, true);
				XMLClassInfo ci  = curHandler.getXMLClassInfo();
				IMember p = ci.getProperty(uri, lname, qname);
				if (p == null){ // is in array or collection or map 
					/**
					 * two kinds:
					 * 1)embed style : 
					 * <a> <blist><b/><b/></blist></a>
					 * 2)flat style:
					 * <a><b/><b/></a>
					 */
					Class it = null;					
					p = ci.getPropertyByChildTag(uri, lname, qname);
					XMLMappingInfo mxmi = null;
					if (p != null ){ //xm !=null     flat style, 
						XMLMappingInfo xmi = getGeneralMemeberTags( p, curHandler.getXMLMappingInfo(), null, new XMLMappingInfo());
						xmi.inherit(curHandler.getXMLMappingInfo()); // get right xmi for list or map
						mxmi = new XMLMappingInfo(xmi.getChildTag(), "", xmi.getKeyTag() , xmi.getValueTag(), xmi.getCollectionStyle(), xmi.getFieldStyle(), xmi.getChildSimpleObjValueAttr());
						mxmi.inherit(xmi);
						mxmi.setItemTypes(xmi.getItemTypes());
						if ( xmi.getItemTypes()[0] == MapEntry.class ){
							it =  xmi.itemTypes()[0];
						}else{
							it = xmi.itemTypes()[xmi.itemTypes().length - 1];
						}
						
						handler.setXMLMappingInfo(mxmi);
						
					}else{ // embed style
						XMLMappingInfo xmi = curHandler.getXMLMappingInfo();
						if (xmi.getItemTypes() == null || xmi.getItemTypes().length == 0){
							throw new SAXException("can not recognise the tag: " + qname );
						}
						mxmi = new XMLMappingInfo(xmi.getChildTag(), "", xmi.getKeyTag(), xmi.getValueTag() , xmi.getCollectionStyle(), xmi.getFieldStyle(), xmi.getChildSimpleObjValueAttr());
						mxmi.inherit(xmi);
						XMLMappingInfo cmxmi = new XMLMappingInfo();
						XMLClassUtil.initXMLInfo(xmi.getItemTypes()[0], null, cmxmi);
						if (mxmi.getTag().length() == 0){
							mxmi.setTag(cmxmi.getTag());
						}
						
						if (mxmi.getChildTag().length() == 0){
							mxmi.setChildTag(cmxmi.getChildTag());
						}
						
						if (mxmi.getKeyTag().length() == 0){
							mxmi.setKeyTag(cmxmi.getKeyTag());
						}
						
						if (mxmi.getValueTag().length() == 0){
							mxmi.setValueTag(cmxmi.getValueTag());
						}
						
						if (mxmi.getValueTag().length() == 0){
							mxmi.setValueTag(cmxmi.getValueTag());
						}
						
						mxmi.setItemTypes(cmxmi.getItemTypes());
						if (mxmi.getItemTypes() == null || mxmi.getItemTypes().length == 0) {
							mxmi.setItemTypes(xmi.getItemTypes());
						}
						
						if ( xmi.getItemTypes()[0] == MapEntry.class ){
							it =  xmi.itemTypes()[0];
						}else{
							it = xmi.itemTypes()[xmi.itemTypes().length - 1];
						}
						
						handler.setXMLMappingInfo(mxmi);
						
					}
					if (it == null && (mxmi.itemTypes() == null || mxmi.itemTypes().length == 0)){
						throw new SAXException("can not create object whose lname is " + lname);
					}
					handler.setType(it);
					if (it == MapEntry.class){
						handler.setXMLClassInfo( new MapEntryClassInfo(MapEntry.class, mxmi.keyTag(), mxmi.valueTag(),  mxmi.itemTypes()[1], mxmi.itemTypes()[2]));
					}else{
						handler.setXMLClassInfo(cpl.getClassInfo(handler.getType(), true));
					}
					if (p != null){ // flat style for child
						rt = processAttrs(handler, attrs);
					}else{
						rt = processAttrs(handler, attrs);
					}
					
				}else{
					Object v = p.getValue(curHandler.getObj());
					XMLMappingInfo mxmi = getGeneralMemeberTags(p, curHandler.getXMLMappingInfo(), null, new XMLMappingInfo());
					Class mtype = v != null ? v.getClass() : p.getType();
//					mtype = mxmi.rebuildAndGetType(lname, mtype, Thread.currentThread().getContextClassLoader());
					handler.setXMLMappingInfo(mxmi);
					if (isCollectionOrMap(p.getType())){
						if (v != null && !v.getClass().isArray()){
							rt = v;
						}else{
							rt = newInstance(mtype);
						}
						handler.setType(rt.getClass());
						handler.setXMLClassInfo(cpl.getClassInfo(handler.getType(), true));
						processAttrs(handler, attrs);
					}else{
//						rt = newInstance(p.getType());
						if (rt != null){
							handler.setType(rt.getClass());
						}else{
							handler.setType(mtype);
						}
						handler.setXMLClassInfo(cpl.getClassInfo(handler.getType(), true));
						rt = processAttrs(handler, attrs);
					}
				}
			}
			return rt;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XMLTransformerException(e.getMessage(), e,  locator.getLineNumber(), locator.getColumnNumber());
		} 
		
	}
	
	EasyObjectNodeHandler createNodeHandler(String uri, String lname, String qname, Attributes attrs) throws SAXException{
		EasyObjectNodeHandler rt = new EasyObjectNodeHandler();
		rt.setQname(qname);
		rt.setLname(lname);
		rt.setUri(uri);
		rt.root = this;
		rt.obj = createObject(uri, lname, qname, attrs,  rt);
		rt.containerHandler = this.curHandler;
		this.curHandler = rt;
		return rt;
	}
	
	
	@Override
	public void startElement(String uri, String lname, String qname, Attributes attrs) throws SAXException {
		if (error != null){
			return;
		}
		if (curHandler == this){
				createNodeHandler(uri, lname, qname, attrs);
		}else{
			curHandler.startElement(uri, lname, qname, attrs);
			return;
		}
	}
	@Override
    public void startPrefixMapping (String prefix, String uri)
	throws SAXException {
//    	if (uri.equals("http://www.w3.org/2001/XMLSchema-instance")){
//    		xsiAlias = prefix;
//    	}
//    	ns.add(new String[]{prefix, uri});
    }
	@SuppressWarnings("unchecked")
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		curHandler.characters(ch, start, length);
		
//		if (trace.obj.text == null){
//			trace.obj.text = new String(ch, start, length);
//		}else{
//			if (trace.isOpen){
//				trace.obj.text += new String(ch, start, length);
//			}
//		}
		
	}
	
	protected Object restoreSimpleAttr(String s, Class type, XMLMappingInfo xmi) throws ConvertException {
		IConverter formatedConverter = xmi.getFormatedConverter();
		Object rt = null;
		String format = xmi.getFormat();
		if (formatedConverter != null){
			rt = ((format != null && format.length() != 0) ? formatedConverter.convert(s, formatedConverter.parseFormat(format)) : formatedConverter.convert(s) );
		}else {
			rt = (format != null && format.length() != 0) ? converter.convertString(type, s, format) :  converter.convertString(type, s);
		}
		return rt;
	}
	
	public void endText(String uri, String localName, String qName) throws SAXException{
		try {
			String text = curHandler.getText().toString();
			if (trimText){
				text = text.trim();
			}
			Object o = curHandler.getObj();
			if (o == null){ // use text to convert to Object
				Object supObj = ((EasyObjectNodeHandler)curHandler).containerHandler.getObj();
				Object cObj = supObj;
				Class itemType = curHandler.getType();
				if (itemType == null){
					itemType = curHandler.getXMLMappingInfo().itemTypes()[1];
				}
				if ( isCollectionOrMap(curHandler.getType())  && curHandler.getXMLMappingInfo().getCollectionStyle() == CollectionStyleType.FLAT){
					//nead get supObj from child tag
					XMLClassInfo ci =cpl.getClassInfo(supObj.getClass(), true);
					IMember p = ci.getPropertyByChildTag(curHandler.getUri(), curHandler.getLname(), curHandler.getQname());
					cObj = p.getValue(supObj);
				}else{
					
				}
//				if (cObj == null){
//					System.err.println("error");
//				}
				if (cObj != null && isCollectionOrMap(cObj.getClass())){
					Object item = converter.convertString(itemType, text);;
//					if (cObj instanceof Set){
//						((Set)cObj).add(item);
//					}else if (cObj instanceof List){
//						List sl = (List)cObj;
//						sl.set(sl.size() - 1, item);
//					}
					if (curHandler.getType() == null){
						( (MapEntry)curHandler.getObj() ).setValue(item);
					}else{
						curHandler.setObj(item);
					}
				}else{ //filed style is TEXT
//					IMember im = ci.getProperty(curHandler.getUri(), curHandler.getLname(), curHandler.getQname());
					curHandler.setObj(restoreSimpleAttr(text, curHandler.getType(), curHandler.getXMLMappingInfo()));//   converter.convertString( curHandler.getType() , text));
				}
			}
			else if ( curHandler.getObj() instanceof MapEntry ){
				MapEntry me = (MapEntry)curHandler.getObj();
				if (me.getValue() == null && converter.canConvertToSimpleString(curHandler.getXMLMappingInfo().itemTypes()[1])){
					me.setValue(converter.convertString(curHandler.getXMLMappingInfo().itemTypes()[1], text.toString()));
				}
			}
			else { // is  text propertity
				XMLClassInfo ci = cpl.getClassInfo(o.getClass(), true);
				IMember<String> im = ci.getTextPropertity();
				if (im != null){
					String f = im.getValue(o);
					if (f == null){
						im.setValue(o, text);
					}else{
						im.setValue(o, f+text);
					}
				}
				
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException){
				throw (RuntimeException)e;
			}
			throw new XMLTransformerException(e + " " + e.getMessage(), e,  locator.getLineNumber(), locator.getColumnNumber());
		} 
		
	}
	
	@Override
	public void endElement(String uri, String lname, String qname) throws SAXException{
		if (curHandler != this){
			curHandler.endElement(uri, lname, qname);
		}
	}

	public Object getObj() {
		return obj;
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		this.error = e;
		throw e;
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		this.error = e;
		throw e;
	}
	
	

	@Override
	public void warning(SAXParseException e) throws SAXException {
		this.error = e;
		throw e;
	}


	public void setObj(Object obj) {
		this.obj = obj;
	}

	public StringBuffer getText() {
		return text;
	}

	public Class getType() {
		return rootClass;
	}

	public void setType(Class type) {
		rootClass = type;
	}

	public XMLMappingInfo getXMLMappingInfo() {
		return xMLMappingInfo;
	}

	public void setXMLMappingInfo(XMLMappingInfo mappingInfo) {
		xMLMappingInfo = mappingInfo;
	}

	public EasyObjectXMLTransformerHandler getRoot() {
		return this;
	}

	/**
	 * @return the xMLClassInfo
	 */
	public XMLClassInfo getXMLClassInfo() {
		return xMLClassInfo;
	}

	/**
	 * @param classInfo the xMLClassInfo to set
	 */
	public void setXMLClassInfo(XMLClassInfo classInfo) {
		xMLClassInfo = classInfo;
	}

}
