package com.easyservice.xml;

import com.easyservice.xml.annotation.XMLNamespace;

public class XMLNamespaceInfo {
	
	String prefix =  "";
	String schemaDeclare = "";
	String[] namespaces = {};
	
	public XMLNamespaceInfo(){
		
	}

	public XMLNamespaceInfo(XMLNamespace xn){
		shadowBy(xn);
	}
	
	
	public final XMLNamespaceInfo inherit(XMLNamespaceInfo xni){
		if (this.prefix.equals("")){
			this.prefix = xni.prefix;
		}
		return this;
	}
	
	
	public final XMLNamespaceInfo shadowBy(XMLNamespaceInfo xni){
		this.prefix = xni.prefix;
		this.schemaDeclare = xni.schemaDeclare;
		this.namespaces = xni.namespaces;
		return this;
	}
	
	public final XMLNamespaceInfo shadowBy(XMLNamespace xn){
		this.prefix = xn.prefix();
		this.schemaDeclare = xn.schemaDeclare();
		this.namespaces = xn.namespaces();
		return this;
	}
	
	public final XMLNamespaceInfo reset(){
		prefix =  "";
		schemaDeclare = "";
		namespaces = new String[]{};
		return this;
	}
	
	public String[] getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(String[] namespaces) {
		this.namespaces = namespaces;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSchemaDeclare() {
		return schemaDeclare;
	}

	public void setSchemaDeclare(String schemaDeclare) {
		this.schemaDeclare = schemaDeclare;
	}
	
	
	
}
