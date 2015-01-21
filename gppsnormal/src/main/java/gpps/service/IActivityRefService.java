package gpps.service;

import gpps.model.ActivityRef;
import gpps.service.exception.IllegalOperationException;
import gpps.service.exception.LoginException;
import gpps.service.exception.ValidateCodeException;

import java.util.Map;

public interface IActivityRefService {
	/**
	 * 当前贷款人申请参加活动
	 * @param activityId
	 * @throws IllegalOperationException
	 */
	public void applyActivity(Integer activityId) throws IllegalOperationException;
	public ActivityRef find(Integer id);
	/**
	 * 找到某活动所有的关联(参加人)
	 * @param activityId
	 * @param offset
	 * @param recnum
	 * @return
	 */
	public Map<String, Object> findByActivity(Integer activityId,int offset,int recnum);
	/**
	 * 匿名申请参加活动
	 * @param name
	 * @param phone
	 * @param email
	 * @param messageValidateCode
	 * @param activityId
	 * @throws IllegalArgumentException
	 * @throws ValidateCodeException
	 * @throws LoginException
	 */
	public void applyActivityByAnonymous(String name, String phone,String email, String messageValidateCode,Integer activityId) throws IllegalArgumentException, ValidateCodeException, LoginException;
	/**
	 * 是否已申请该活动
	 * @param activityId
	 * @param lenderId
	 * @return
	 */
	public boolean isApply(Integer activityId,Integer lenderId);
	/**
	 * 找到当前贷款人参加的所有活动关联
	 * @param lenderId
	 * @param offset
	 * @param recnum
	 * @return
	 */
	public Map<String, Object> findByLender(Integer lenderId,int offset,int recnum);
}
