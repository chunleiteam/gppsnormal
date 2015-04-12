package com.easyservice.security;

import gpps.tools.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.easyservice.exception.PermissionException;
import com.easyservice.xml.EasyObjectXMLTransformerImpl;
import com.easyservice.xml.IEasyObjectXMLTransformer;
public class AuthorityValidateServiceImpl implements IAuthorityValidateService{
	private IEasyObjectXMLTransformer xmlTransformer=new EasyObjectXMLTransformerImpl();
	private Resource configLocation;//权限配置文件路径
	private Logger logger=Logger.getLogger(AuthorityValidateServiceImpl.class);
	private StaticRolesConfig config;
	@Override
	public boolean checkPermission(Object user,boolean fetchServiceSdl, String applyService,
			String applyMethod) throws PermissionException {
		if(config==null||!config.enabled)
			return true;
		int privilege=-1;
		if(user!=null)
		{
			if(!(user instanceof Permit))
				throw new PermissionException("user entity must implements Permit");
			privilege=((Permit)user).getPrivilege();
		}
//		if(fetchServiceSdl)
//			logger.debug("用户角色：{privilege:"+privilege+"},申请获取服务:"+applyService+"定义");
//		else
//			logger.debug("用户角色：{privilege:"+privilege+"},申请执行服务:"+applyService+"方法:"+applyMethod);
		Role role=getRoleByPrivilege(privilege);
		if (role == null) {
			if (fetchServiceSdl)
				logger.error("用户角色：{privilege:" + privilege + "},申请获取服务:"
						+ applyService + "定义,未找到该用户角色配置");
			else
				logger.error("用户角色：{privilege:" + privilege + "},申请执行服务:"
						+ applyService + "方法:" + applyMethod+",未找到该用户角色配置");
			return false;
		}
		if(role.getLimitedType()==Role.LIMITEDTYPE_ALLLIMITED)
		{
			return false;
		}
		else if(role.getLimitedType()==Role.LIMITEDTYPE_NOTLIMITED)
		{
			return true;
		}
		else if(role.getLimitedType()==Role.LIMITEDTYPE_PARTPERMIT)
		{
			boolean success=checkPermissionRules(role, applyService, applyMethod, fetchServiceSdl);
			if(success)
				return true;
			else
			{
				if (fetchServiceSdl)
					logger.error("用户角色：{privilege:" + privilege + "},申请获取服务:"
							+ applyService + "定义,未找到该用户角色配置");
				else
					logger.error("用户角色：{privilege:" + privilege + "},申请执行服务:"
							+ applyService + "方法:" + applyMethod+",未找到该用户角色配置");
				return false;
			}
		}else if(role.getLimitedType()==Role.LIMITEDTYPE_PARTFORBID){
			boolean success = checkForbidRules(role, applyService, applyMethod, fetchServiceSdl);
			if(success){
				return true;
			}else
			{
				if (fetchServiceSdl)
					logger.error("用户角色：{privilege:" + privilege + "},申请获取服务:"
							+ applyService + "定义,该权限被禁止");
				else
					logger.error("用户角色：{privilege:" + privilege + "},申请执行服务:"
							+ applyService + "方法:" + applyMethod+",该权限被禁止");
				return false;
			}
		}else{
			logger.error("用户角色：{privilege:" + privilege + "},申请获取服务:"
							+ applyService + "定义,角色权限类型：{"+role.getLimitedType()+"}有问题");
			return false;
		}
	}
	private Role getRoleByPrivilege(int privilege)
	{
		List<Role> roles=config.getRoles();
		if(roles==null||roles.size()==0)
			return null;
		for (Role role : config.roles) {
			if(StringUtil.isEmpty(role.getPrivilege()))
				continue;
			String[] privileges=role.getPrivilege().split("\\|");
			for(String str:privileges)
			{
				if(str.equals(String.valueOf(privilege)))
					return role;
			}
		}
		return null;
	}
	
	private boolean checkForbidRules(Role role, String applyService, String applyMethod, boolean fetchServiceSdl){
		if(role==null)
			return true;
		if(role.getPermissionRules()!=null&&role.getPermissionRules().size()>0)
		{
			for (PermissionRule rule : role.getPermissionRules()) {
				if (rule.getEntityLimitType().equals(applyService))
				{
					if(fetchServiceSdl)
						return false;
					if(rule.getOperations()==null||rule.getOperations().trim().length()==0)
						return false;
					String[] ops=rule.getOperations().split(";");
					for(String op:ops)
					{
						if(op.equals(applyMethod))
							return false;
					}
					break;
				}
			}
		}
		if(role.getExtendsRoles()==null||role.getExtendsRoles().size()==0)
			return true;
		for(Role extendsRole:role.getExtendsRoles())
		{
			if(!checkForbidRules(extendsRole,applyService,applyMethod,fetchServiceSdl))
				return false;
		}
		return true;
	}
	
	private boolean checkPermissionRules(Role role,String applyService,String applyMethod,boolean fetchServiceSdl)
	{
		if(role==null)
			return false;
		if(role.getPermissionRules()!=null&&role.getPermissionRules().size()>0)
		{
			for (PermissionRule rule : role.getPermissionRules()) {
				if (rule.getEntityLimitType().equals(applyService))
				{
					if(fetchServiceSdl)
						return true;
					if(rule.getOperations()==null||rule.getOperations().trim().length()==0)
						return true;
					String[] ops=rule.getOperations().split(";");
					for(String op:ops)
					{
						if(op.equals(applyMethod))
							return true;
					}
					break;
				}
			}
		}
		if(role.getExtendsRoles()==null||role.getExtendsRoles().size()==0)
			return false;
		for(Role extendsRole:role.getExtendsRoles())
		{
			if(checkPermissionRules(extendsRole,applyService,applyMethod,fetchServiceSdl))
				return true;
		}
		return false;
	}
	public Resource getConfigLocation() {
		return configLocation;
	}
	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}
	@PostConstruct
	public void init() {
		//加载权限配置文件到内存中
		try {
			config=xmlTransformer.parse(configLocation.getInputStream(), StaticRolesConfig.class);
			
			//添加关联角色
			List<Role> roles= config.getRoles();
			if(roles!=null&&roles.size()>0)
			{
				Map<String,Role> m=new HashMap<String,Role>();
				for(Role role:roles)
				{
					if(!StringUtil.isEmpty(role.getId()))
						m.put(role.getId(), role);
				}
				if(!m.isEmpty())
				{
					for(Role role:roles)
					{
						if(StringUtil.isEmpty(role.getExtendsRoleIds()))
							continue;
						String[] extendsRoleIds=role.getExtendsRoleIds().split("\\|");
						for(String extendsRoleId:extendsRoleIds)
						{
							Role extendsRole=m.get(extendsRoleId);
							if(extendsRole!=null)
								role.getExtendsRoles().add(extendsRole);
						}
					}
				}
			}
		} catch (Throwable e)
		{
			logger.error(e.getMessage(),e);
		}
		logger.info("权限配置文件装载完毕,可提供默认权限服务");
	}
}
