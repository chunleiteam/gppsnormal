package com.easyservice.security;

import java.util.ArrayList;
import java.util.List;

import com.easyservice.xml.annotation.CollectionStyleType;
import com.easyservice.xml.annotation.XMLMapping;

public class Role{
	private String id;
	private String privilege;//多个权限用“|”分离
	public static final int LIMITEDTYPE_NOTLIMITED=1;//不限
	public static final int LIMITEDTYPE_PARTPERMIT=0;//部分许可
	public static final int LIMITEDTYPE_ALLLIMITED=-1;//全部许可
	private int limitedType=LIMITEDTYPE_PARTPERMIT;//限制类型
	private List<PermissionRule> permissionRules=new ArrayList<PermissionRule>();
	private String extendsRoleIds;
	@XMLMapping(collectionStyle=CollectionStyleType.FLAT,childTag="PermissionRule")
	public List<PermissionRule> getPermissionRules() {
		return permissionRules;
	}
	public void setPermissionRules(List<PermissionRule> permissionRules) {
		this.permissionRules = permissionRules;
	}
	public String getPrivilege() {
		return privilege;
	}
	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}
	public int getLimitedType() {
		return limitedType;
	}
	public void setLimitedType(int limitedType) {
		this.limitedType = limitedType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExtendsRoleIds() {
		return extendsRoleIds;
	}
	public void setExtendsRoleIds(String extendsRoleIds) {
		this.extendsRoleIds = extendsRoleIds;
	}
	//辅助
	private List<Role> extendsRoles=new ArrayList<Role>();
	public List<Role> getExtendsRoles() {
		return extendsRoles;
	}
	public void setExtendsRoles(List<Role> extendsRoles) {
		this.extendsRoles = extendsRoles;
	}
}

