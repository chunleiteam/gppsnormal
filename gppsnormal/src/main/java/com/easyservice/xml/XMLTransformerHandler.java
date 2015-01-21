package com.easyservice.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

public interface XMLTransformerHandler extends EntityResolver, DTDHandler, ContentHandler, ErrorHandler{
	
//	public boolean isOpen();
	public Object getObj();
	public void setObj(Object obj);
	
	/**
	 * if this type is entry type, return null, should get key and value type from xmlmappingInfo.getItemTypes
	 * @return
	 */
	public Class getType();
	public XMLClassInfo getXMLClassInfo();
	public void setXMLClassInfo(XMLClassInfo xMLClassInfo);
	public void setType(Class type);
	
	public XMLMappingInfo getXMLMappingInfo();
	public void setXMLMappingInfo(XMLMappingInfo mapping);
	public String getQname();
	public String getLname();
	public String getUri();
	public StringBuffer getText();
//	public Object getMapEntryKey() ;
//	public void setMapEntryKey(Object mapEntryKey) ;
	public EasyObjectXMLTransformerHandler getRoot();
}
