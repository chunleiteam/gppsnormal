package com.easyservice.xml;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class EasyObjectNodeHandler extends DefaultHandler implements XMLTransformerHandler {
	
//	boolean isOpen;
	XMLTransformerHandler containerHandler;
	Object obj;
	EasyObjectXMLTransformerHandler root;
	XMLMappingInfo xMLMappingInfo;
	XMLClassInfo xMLClassInfo;
	
	String lname;
	String qname;
	String uri;
	
	
	StringBuffer text;
	Class type;
	
	EasyObjectNodeHandler(){
		
	}
	
//	public EasyObjectNodeHandler() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
//		this.root = root;
//		this.containerHandler = root.curHandler;
//		root.curHandler = this;
//		this.obj = obj;
//	}

//	public boolean isOpen() {
//		return isOpen;
//	}

	
	
	@Override
	public void startElement(String uri, String lname, String qname, Attributes attrs) throws SAXException {
		if (root.error != null){
			return;
		}
		if (root.curHandler == this){
				root.createNodeHandler(uri, lname, qname, attrs);
		}else{
			root.curHandler.startElement(uri, lname, qname, attrs);
			return;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (text != null){
			root.endText(uri, localName, qName);
		}
		if (containerHandler.getObj() == null){
			containerHandler.setObj(root.curHandler.getObj());
		}
		root.curHandler = containerHandler;
		
		root.placeObject(uri, lname, qname, this);
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}


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

	public StringBuffer getText() {
		return text;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (text == null){
			text = new StringBuffer();
		}
		text.append(ch, start, length);
	}

	public Class getType() {
		return type;
	}

	public void setType(Class type) {
		this.type = type;
	}

	public XMLMappingInfo getXMLMappingInfo() {
		return xMLMappingInfo;
	}

	public void setXMLMappingInfo(XMLMappingInfo mappingInfo) {
		xMLMappingInfo = mappingInfo;
	}


	/**
	 * @return the root
	 */
	public EasyObjectXMLTransformerHandler getRoot() {
		return root;
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
