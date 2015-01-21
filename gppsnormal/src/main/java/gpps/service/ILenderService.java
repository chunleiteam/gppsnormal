package gpps.service;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import gpps.model.CardBinding;
import gpps.model.Lender;
import gpps.service.exception.InviteException;
import gpps.service.exception.LoginException;
import gpps.service.exception.ValidateCodeException;

public interface ILenderService extends ILoginService{
	/**
	 * 注册用户(贷款方),privilege默认为0
	 * 创建用户时默认为该用户创建一个初始化账户并与之关联
	 * @param lender 贷款方
	 * @param messageValidateCode 短信验证码
	 * @return 贷款方，增加ID
	 * @throws LoginException 
	 * @throws Exception
	 */
	public Lender register(Lender lender,String messageValidateCode, String graphValidateCode, String inviteCode) throws ValidateCodeException,IllegalArgumentException, LoginException, InviteException;
	
	
	/**
	 * 只是检车图片验证码是否正确
	 * 
	 * */
	public boolean isCodeRight(String code);
	
	public Lender register(Lender lender,String messageValidateCode) throws ValidateCodeException,IllegalArgumentException, LoginException;
	
	public void changeAttri(String name, String value)throws IllegalArgumentException;
	
	public void registerSecondStep(String name,String identityCard,int sex,String address,String annualIncome)throws IllegalArgumentException;
	/**
	 * 更新用户
	 * 待讨论哪些字段能够更新
	 * @param lender
	 * @return
	 * @throws Exception
	 */
//	public Lender update(Lender lender);
	/**
	 * 修改用户角色级别,修改范围为Lender定义的privilege常量
	 * 该方法只有admin有调用权限
	 * @param privilege
	 * @throws Exception
	 */
	public void changeLevel(int id,int level) throws IllegalArgumentException;
	/**
	 * 根据ID查找
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Lender find(int id);
	
	public Lender findByLoginId(String loginId);
	
	/**
	 * 返回所有的用户角色
	 * 供产品购买级别那里选择
	 * @return
	 */
	public int[] findAllLevel();
	/**
	 * 返回Session用户对象
	 * @return
	 */
	public Lender getCurrentUser();
	
	public void registerThirdPartyAccount(Integer id,String thirdPartyAccount,String accountNumber);
	
	public boolean isIdentityAuthentication();
	public boolean isThirdPartyAuthentication();
	/**
	 * 查找贷款人
	 * @param privilege -1表示不限
	 * @param offset
	 * @param recnum
	 * @return
	 */
	public Map<String, Object> findByPrivilegeWithPaging(int privilege,int offset,int recnum);
	/**
	 * 绑定银行卡
	 * @param id
	 * @param cardId
	 */
	public void bindCard(Integer id,Integer cardId);
	
	
	/**
	 * 获得绑定的银行卡
	 * 
	 * */
	public CardBinding getBindingCard(Integer id);
}
