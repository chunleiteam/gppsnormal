package com.easyservice.security;
public class PermissionRule {
	private String entityLimitType;
	private String operations;
	public String getEntityLimitType() {
		return entityLimitType;
	}

	public void setEntityLimitType(String entityLimitType) {
		this.entityLimitType = entityLimitType;
	}

	public String getOperations() {
		return operations;
	}

	public void setOperations(String operations) {
		this.operations = operations;
	}
}

