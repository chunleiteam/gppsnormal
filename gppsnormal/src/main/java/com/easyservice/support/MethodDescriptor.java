package com.easyservice.support;

public class MethodDescriptor {
	
	String name;
	String returnType;
	ParamDescriptor[] params;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the returnType
	 */
	public String getReturnType() {
		return returnType;
	}
	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	/**
	 * @return the params
	 */
	public ParamDescriptor[] getParams() {
		return params;
	}
	/**
	 * @param params the params to set
	 */
	public void setParams(ParamDescriptor[] params) {
		this.params = params;
	}
	
	
	
	
}
