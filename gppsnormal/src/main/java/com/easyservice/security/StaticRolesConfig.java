package com.easyservice.security;

import java.util.ArrayList;
import java.util.List;

import com.easyservice.xml.annotation.CollectionStyleType;
import com.easyservice.xml.annotation.XMLMapping;

public class StaticRolesConfig {
	List<Role> roles = new ArrayList<Role>();
	boolean enabled=false;
	@XMLMapping(collectionStyle = CollectionStyleType.FLAT, childTag = "Role")
	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
