package com.easyservice.xml;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.easyservice.support.convert.IConverter;
import com.easyservice.xml.annotation.CollectionStyleType;
import com.easyservice.xml.annotation.ExportCompressType;
import com.easyservice.xml.annotation.FieldStyleType;
import com.easyservice.xml.annotation.XMLMapping;

public class XMLMappingInfo {
	String tag="";
	String childTag="";
	String keyTag="";
	String valueTag = "";
	CollectionStyleType collectionStyle = CollectionStyleType.INHERIT;
	FieldStyleType fieldStyle = FieldStyleType.INHERIT;
	String childSimpleObjValueAttr = "";
	Class[] itemTypes={};
	String[] orderedProperties = {};
	IConverter formatedConverter;
	String converter="";
	String format = "";
	ExportCompressType exportCompressType = ExportCompressType.INHERIT;
	Map<String, String> typeTagMapping = Collections.EMPTY_MAP;
	


	public Map<String, String> getTypeTagMapping() {
		return typeTagMapping;
	}

	public void setTypeTagMapping(Map<String, String> typeTagMapping) {
		this.typeTagMapping = typeTagMapping;
	}

	public XMLMappingInfo(){
		
	}
	
	public XMLMappingInfo(String tag, CollectionStyleType collectionStyle, FieldStyleType fieldStyle) {
		this.tag = tag;
		this.collectionStyle = collectionStyle;
		this.fieldStyle = fieldStyle;
	}
	
	public XMLMappingInfo(String tag, String childTag, CollectionStyleType collectionStyle, FieldStyleType fieldStyle, String childSimpleObjValueAttr) {
		this.tag = tag;
		this.childTag = childTag;
		this.collectionStyle = collectionStyle;
		this.fieldStyle = fieldStyle;
		this.childSimpleObjValueAttr = childSimpleObjValueAttr;
	}
	
	public XMLMappingInfo(String tag, String childTag, String mapKey, CollectionStyleType collectionStyle, FieldStyleType fieldStyle, String childSimpleObjValueAttr) {
		this.tag = tag;
		this.childTag = childTag;
		this.keyTag = mapKey;
		this.collectionStyle = collectionStyle;
		this.fieldStyle = fieldStyle;
		this.childSimpleObjValueAttr = childSimpleObjValueAttr;
	}
	
	public XMLMappingInfo(String tag, String childTag, String mapKey, String valueTag, CollectionStyleType collectionStyle, FieldStyleType fieldStyle, String childSimpleObjValueAttr) {
		this.tag = tag;
		this.childTag = childTag;
		this.keyTag = mapKey;
		this.collectionStyle = collectionStyle;
		this.valueTag = valueTag;
		this.fieldStyle = fieldStyle;
		this.childSimpleObjValueAttr = childSimpleObjValueAttr;
		
	}

	public IConverter getFormatedConverter() {
		return formatedConverter;
	}

	public void setFormatedConverter(IConverter formatedConverter) {
		this.formatedConverter = formatedConverter;
	}

	public String getConverter() {
		return converter;
	}

	public void setConverter(String converter) {
		this.converter = converter;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public XMLMappingInfo(XMLMapping xm){
		shadowBy(xm);
	}
	
	public final XMLMappingInfo shadowBy(XMLMapping xm){
		this.tag = xm.tag();
		this.childTag = xm.childTag();
		this.keyTag = xm.keyTag();
		this.collectionStyle = xm.collectionStyle();
		this.fieldStyle = xm.fieldStyle();
		this.childSimpleObjValueAttr = xm.childSimpleObjValueAttr();
		this.itemTypes = xm.itemTypes();
		this.valueTag = xm.valueTag();
		this.converter = xm.converter();
		this.format = xm.format();
		this.exportCompressType = xm.exportCompressType();
		this.orderedProperties = xm.orderedProperties();
		this.converter = xm.converter();
		if (xm.tagTypeMapping().length != 0){
			this.typeTagMapping = new HashMap<String, String>();
			for (String m : xm.tagTypeMapping()){
				String[] mp = m.split("\\=");
				typeTagMapping.put(mp[1], mp[0]);
			}
		}
		
		return this;
	}
	
	public final XMLMappingInfo shadowBy(XMLMappingInfo xmi){
		this.tag = xmi.tag;
		this.childTag = xmi.childTag;
		this.keyTag = xmi.keyTag;
		this.collectionStyle = xmi.collectionStyle;
		this.fieldStyle = xmi.fieldStyle;
		this.childSimpleObjValueAttr = xmi.childSimpleObjValueAttr;
		this.itemTypes = xmi.itemTypes;
		this.valueTag = xmi.valueTag;
		this.format = xmi.format;
		this.exportCompressType = xmi.exportCompressType;
		this.formatedConverter = xmi.formatedConverter;
		this.orderedProperties = xmi.orderedProperties;
		this.converter = xmi.converter;
		this.typeTagMapping = xmi.typeTagMapping;
		return this;
	}
	
	public final XMLMappingInfo reset(){
		tag="";
		childTag="";
		keyTag="";
		collectionStyle = CollectionStyleType.INHERIT;
		fieldStyle = FieldStyleType.INHERIT;
		childSimpleObjValueAttr = "";
		itemTypes=new Class[]{};
		format = "";
		formatedConverter = null;
		converter = "";
		typeTagMapping = Collections.EMPTY_MAP;
		return this;
	}
	
	public final XMLMappingInfo inherit(XMLMappingInfo superXm){
		if (this.collectionStyle == CollectionStyleType.INHERIT){
			this.collectionStyle = superXm.collectionStyle;
		}
		if (this.fieldStyle == FieldStyleType.INHERIT){
			this.fieldStyle = superXm.fieldStyle;
		}
		if (this.exportCompressType == ExportCompressType.INHERIT) {
			this.exportCompressType = superXm.exportCompressType;
		}
		return this;
	}
	
	public String getChildTag() {
		return childTag;
	}
	public void setChildTag(String childTag) {
		this.childTag = childTag;
	}
	public CollectionStyleType getCollectionStyle() {
		return collectionStyle;
	}
	public void setCollectionStyle(CollectionStyleType collectionStyle) {
		this.collectionStyle = collectionStyle;
	}
	public FieldStyleType getFieldStyle() {
		return fieldStyle;
	}
	public void setFieldStyle(FieldStyleType fieldStyle) {
		this.fieldStyle = fieldStyle;
	}
	public String getKeyTag() {
		return keyTag;
	}
	public void setKeyTag(String mapKey) {
		this.keyTag = mapKey;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}




	public String getChildSimpleObjValueAttr() {
		return childSimpleObjValueAttr;
	}




	public void setChildSimpleObjValueAttr(String childSimpleObjValueAttr) {
		this.childSimpleObjValueAttr = childSimpleObjValueAttr;
	}




	public Class[] getItemTypes() {
		return itemTypes;
	}




	public void setItemTypes(Class[] itemTypes) {
		this.itemTypes = itemTypes;
	}
	

	public String tag(){
		return tag;
	}
	public String childTag(){
		return childTag;
	}
	public String childSimpleObjValueAttr(){
		return childSimpleObjValueAttr;
	}
	public String keyTag(){
		return keyTag;
	}
	
	public String valueTag(){
		return valueTag;
	}
	
	public Class[] itemTypes(){
		return itemTypes;
	}
	public CollectionStyleType collectionStyle() {
		return collectionStyle;
	}
	public  FieldStyleType fieldStyle() {
		return fieldStyle;
	}




	/**
	 * @return the valueTag
	 */
	public String getValueTag() {
		return valueTag;
	}




	/**
	 * @param valueTag the valueTag to set
	 */
	public void setValueTag(String valueTag) {
		this.valueTag = valueTag;
	}

	public ExportCompressType getExportCompressType() {
		return exportCompressType;
	}
	
	public void setExportCompressType(ExportCompressType exportCompressType) {
		this.exportCompressType = exportCompressType;
	}

	public String[] getOrderedProperties() {
		return orderedProperties;
	}

	public void setOrderedProperties(String[] orderedProperties) {
		this.orderedProperties = orderedProperties;
	}

	
	
}
