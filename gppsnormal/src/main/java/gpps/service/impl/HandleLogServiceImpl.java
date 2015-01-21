package gpps.service.impl;

import gpps.dao.IHandleLogDao;
import gpps.model.HandleLog;
import gpps.service.IAdminService;
import gpps.service.IBorrowerService;
import gpps.service.IHandleLogService;
import gpps.service.ILenderService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class HandleLogServiceImpl implements IHandleLogService{
	@Autowired
	IHandleLogDao handleLogDao;
	private static Map<String,String[]> filterMethods=new HashMap<String,String[]>();
	static{
		filterMethods.put(ILenderService.class.getName(), new String[]{"getCurrentUser"});
		filterMethods.put(IBorrowerService.class.getName(), new String[]{"getCurrentUser"});
		filterMethods.put(IAdminService.class.getName(), new String[]{"getCurrentUser"});
	}
	@Override
	public void create(HandleLog handleLog) {
		String[] methods=filterMethods.get(handleLog.getCallService());
		if(methods!=null&&methods.length>0)
		{
			for(String method:methods)
			{
				if(method.equals("*")||method.equals(handleLog.getCallmethod()))
					return;
			}
		}
		handleLogDao.create(handleLog);
	}

}
