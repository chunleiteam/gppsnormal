package gpps.service.impl;

import gpps.constant.Pagination;
import gpps.dao.IActivityDao;
import gpps.dao.IActivityRefDao;
import gpps.model.ActivityRef;
import gpps.model.Lender;
import gpps.service.IActivityRefService;
import gpps.service.ILenderService;
import gpps.service.ILoginService;
import gpps.service.exception.IllegalOperationException;
import gpps.service.exception.LoginException;
import gpps.service.exception.ValidateCodeException;
import gpps.tools.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
@Service
public class ActivityRefServiceImpl implements IActivityRefService{
	@Autowired
	IActivityRefDao activityRefDao;
	@Autowired
	ILenderService lenderService;
	@Autowired
	IActivityDao activityDao;
	@Override
	public ActivityRef find(Integer id) {
		ActivityRef ref= activityRefDao.find(id);
		ref.setActivity(activityDao.find(ref.getActivityId()));
		return ref;
	}

	@Override
	public Map<String, Object> findByActivity(Integer activityId,int offset,int recnum) {
		int count=activityRefDao.countByActivity(activityId);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<ActivityRef> refs= activityRefDao.findByActivity(activityId,offset,recnum);
		for(ActivityRef ref:refs)
		{
			if(ref.getParticipatorType()==ActivityRef.PARTICIPATORTYPE_LENDER)
				ref.setLender(lenderService.find(ref.getParticipatorId()));
		}
		return Pagination.buildResult(refs, count, offset, recnum);
	}

	@Override
	public void applyActivity(Integer activityId) throws IllegalOperationException {
		HttpSession session=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		Object user=session.getAttribute(ILoginService.SESSION_ATTRIBUTENAME_USER);
		ActivityRef ref=new ActivityRef();
		ref.setActivityId(activityId);
		ref.setParticipate(ActivityRef.PARTICIPATE_NO);
		ref.setApplyTime(System.currentTimeMillis());
		if(user instanceof Lender)
		{
			if(isApply(activityId, ((Lender)user).getId()))
			{
				throw new IllegalOperationException("该活动您已报名");
			}
			ref.setParticipatorType(ActivityRef.PARTICIPATORTYPE_LENDER);
			ref.setParticipatorId(((Lender)user).getId());
		}
		else
			throw new RuntimeException("不支持的用户类型");
		activityRefDao.create(ref);
	}

	@Override
	public void applyActivityByAnonymous(String name, String phone, String email, String messageValidateCode, Integer activityId) throws IllegalArgumentException, ValidateCodeException, LoginException {
		Lender lender=new Lender();
		lender.setName(StringUtil.checkNullAndTrim("name", name));
		lender.setTel(StringUtil.checkNullAndTrim("tel", phone));
		lender.setEmail(StringUtil.checkNullAndTrim("email", email));
		lender.setLoginId("L"+lender.getTel());
		lender.setPassword("L"+lender.getTel());
		lender=lenderService.register(lender, messageValidateCode);
		try {
			applyActivity(activityId);
		} catch (IllegalOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isApply(Integer activityId, Integer lenderId) {
		return activityRefDao.findByActivityAndLender(activityId, lenderId)==null?false:true;
	}

	@Override
	public Map<String, Object> findByLender(Integer lenderId,int offset,int recnum) {
		int count=activityRefDao.countByLender(lenderId);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		List<ActivityRef> refs=activityRefDao.findByLender(lenderId, offset, recnum);
		for(ActivityRef ref:refs)
		{
			ref.setActivity(activityDao.find(ref.getActivityId()));
		}
		return Pagination.buildResult(refs, count, offset, recnum);
	}

}
