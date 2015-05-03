package gpps.service.impl;

import static gpps.tools.StringUtil.checkNullAndTrim;
import gpps.constant.Pagination;
import gpps.dao.ICardBindingDao;
import gpps.dao.ILenderAccountDao;
import gpps.dao.ILenderDao;
import gpps.model.Borrower;
import gpps.model.CardBinding;
import gpps.model.Lender;
import gpps.model.LenderAccount;
import gpps.service.IInviteService;
import gpps.service.ILenderService;
import gpps.service.exception.InviteException;
import gpps.service.exception.LoginException;
import gpps.service.exception.SMSException;
import gpps.service.exception.ValidateCodeException;
import gpps.service.message.IMessageSupportService;
import gpps.tools.Area;
import gpps.tools.BankBindingUtils;
import gpps.tools.BankCode;
import gpps.tools.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class LenderServiceImpl extends AbstractLoginServiceImpl implements ILenderService{
	@Autowired
	ILenderDao lenderDao;
	@Autowired
	ILenderAccountDao lenderAccountDao;
	@Autowired
	ICardBindingDao cardBindingDao;
	@Autowired
	IInviteService inviteService;
	@Autowired
	IMessageSupportService messageSupportService;
	@Override
	public void login(String loginId, String password, String graphValidateCode)
			throws LoginException, ValidateCodeException {
		checkGraphValidateCode(graphValidateCode);
		HttpSession session =getCurrentSession();
		loginId=checkNullAndTrim("loginId", loginId);
		password=getProcessedPassword(checkNullAndTrim("password", password)+PASSWORDSEED);
		Lender lender=lenderDao.findByLoginIdAndPassword(loginId, password);
		if(lender==null)
			throw new LoginException("用户名或密码错误!!");
		if(lender.getCardBindingId()!=null)
		lender.setCardBinding(cardBindingDao.find(lender.getCardBindingId()));
		
		session.setAttribute(SESSION_ATTRIBUTENAME_USER, lender);
	}

	@Override
	public void changePassword(String loginId, String password,
			String messageValidateCode) throws LoginException,ValidateCodeException {
		checkMessageValidateCode(messageValidateCode);
		Lender lender=lenderDao.findByLoginId(loginId);
		if(lender==null)
		{
			List<Lender> lenders = lenderDao.findByTel(loginId);
			if(lenders!=null && !lenders.isEmpty()){
				lender = lenders.get(0);
			}
		}
		if(lender==null)
			throw new LoginException("LoginId is not existed");
		lenderDao.changePassword(lender.getId(), getProcessedPassword(checkNullAndTrim("password", password)+PASSWORDSEED));
	}

	@Override
	public boolean isLoginIdExist(String loginId) {
		Lender lender=lenderDao.findByLoginId(loginId);
		return lender==null?false:true;
	}

	@Override
	public String getProcessedTel(String loginId) {
		Lender lender=lenderDao.findByLoginId(loginId);
		if(lender==null)
			return null;
		String tel=lender.getTel();
		char[] processedTel=tel.toCharArray();
		for(int i=4;i<8;i++)
		{
			processedTel[i]='*';
		}
		return String.valueOf(processedTel);
	}

	@Override
	public boolean isCodeRight(String code){
		boolean flag = false;
		try{
			flag = onlyCheckGraphValidateCode(code);
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}
	
	
	@Override
	public Lender register(Lender lender, String messageValidateCode, String graphValidateCode, String inviteCode)
			throws ValidateCodeException, IllegalArgumentException, LoginException, InviteException {
		checkGraphValidateCode(graphValidateCode);
		inviteService.check(inviteCode);
		
		
		
		try{
		checkMessageValidateCode(messageValidateCode);	
		
		lender.setLoginId(checkNullAndTrim("loginId", lender.getLoginId()));
		if(StringUtil.isDigit(lender.getLoginId()))
			throw new IllegalArgumentException("登录名不能全部为数字");
		lender.setPassword(getProcessedPassword(checkNullAndTrim("password", lender.getPassword())+PASSWORDSEED));
		lender.setCreatetime(System.currentTimeMillis());
		lender.setPrivilege(Lender.PRIVILEGE_UNOFFICIAL);
		lender.setLevel(Lender.LEVEL_VIP1);
		lender.setTel(checkNullAndTrim("tel", lender.getTel()));
		lender.setGrade(10000);
		if(isLoginIdExist(lender.getLoginId()))
			throw new LoginException("LoginId is existed");
		if(isPhoneNumberExist(lender.getTel()))
			throw new LoginException("Tel is existed");
		LenderAccount account=new LenderAccount();
		lenderAccountDao.create(account);
		lender.setAccountId(account.getId());
		lenderDao.create(lender);
		lender.setPassword(null);
		getCurrentSession().setAttribute(SESSION_ATTRIBUTENAME_USER, lender);
		
		inviteService.register(inviteCode, lender.getId());
		
		}catch(ValidateCodeException e){
			inviteService.release(inviteCode);
			throw e;
		}
		catch(IllegalArgumentException e){
			inviteService.release(inviteCode);
			throw e;
		}catch(LoginException e){
			inviteService.release(inviteCode);
			throw e;
		}catch(InviteException e)
		{
			inviteService.release(inviteCode);
			throw e;
		}catch(Exception e){
			inviteService.release(inviteCode);
			throw new LoginException(e.getMessage());
		}
		
		return lender;
	}
	
	
	@Override
	public Lender register(Lender lender, String messageValidateCode)
			throws ValidateCodeException, IllegalArgumentException, LoginException {
		checkMessageValidateCode(messageValidateCode);
		lender.setLoginId(checkNullAndTrim("loginId", lender.getLoginId()));
		if(StringUtil.isDigit(lender.getLoginId()))
			throw new IllegalArgumentException("登录名不能全部为数字");
		lender.setPassword(getProcessedPassword(checkNullAndTrim("password", lender.getPassword())+PASSWORDSEED));
		lender.setCreatetime(System.currentTimeMillis());
		lender.setPrivilege(Lender.PRIVILEGE_UNOFFICIAL);
		lender.setLevel(Lender.LEVEL_COMMON);
		lender.setTel(checkNullAndTrim("tel", lender.getTel()));
		lender.setGrade(0);
		if(isLoginIdExist(lender.getLoginId()))
			throw new LoginException("LoginId is existed");
		if(isPhoneNumberExist(lender.getTel()))
			throw new LoginException("Tel is existed");
		LenderAccount account=new LenderAccount();
		lenderAccountDao.create(account);
		lender.setAccountId(account.getId());
		lenderDao.create(lender);
		lender.setPassword(null);
		getCurrentSession().setAttribute(SESSION_ATTRIBUTENAME_USER, lender);
		return lender;
	}
	
//	@Override
//	public Lender update(Lender lender) {
//		lenderDao.update(lender);
//		return lender;
//	}

	@Override
	public void changeLevel(int id, int level)
			throws IllegalArgumentException {
		lenderDao.changeLevel(id, level);
	}

	@Override
	public Lender find(int id) {
		Lender lender = lenderDao.find(id);
		if(lender!=null && lender.getCardBindingId()!=null){
			lender.setCardBinding(cardBindingDao.find(lender.getCardBindingId()));
		}
		return lender;
	}
	
	@Override
	public Lender findByLoginId(String loginId){
		return lenderDao.findByLoginId(loginId);
	}

	@Override
	public int[] findAllLevel() {
		int[] privileges={Lender.LEVEL_COMMON,Lender.LEVEL_VIP1};
		return privileges;
	}

	@Override
	public Lender getCurrentUser() {
		HttpSession session=getCurrentSession();
		if(session==null)
			return null;
		Object user=session.getAttribute(SESSION_ATTRIBUTENAME_USER);
		if(user instanceof Lender)
			return (Lender)session.getAttribute(SESSION_ATTRIBUTENAME_USER);
		return null;
	}

	@Override
	public boolean isPhoneNumberExist(String phoneNumber) {
		List<Lender> lenders=lenderDao.findByTel(phoneNumber);
		return (lenders==null || lenders.isEmpty())?false:true;
	}

	@Override
	public void changeAttri(String name, String value)throws IllegalArgumentException{
		value = checkNullAndTrim(name, value);
		Lender lender = getCurrentUser();
		if(name.equals("name")){
			lenderDao.updateName(lender.getId(), value);
			lender.setName(value);
		}else if(name.equals("email")){
			lenderDao.updateEmail(lender.getId(), value);
			lender.setEmail(value);
		}else if(name.equals("address")){
			lenderDao.updateAddress(lender.getId(), value);
			lender.setAddress(value);
		}else{
			throw new IllegalArgumentException("申请修改了无法修改的字段！");
		}
	}
	
	@Override
	public void registerSecondStep(String name, String identityCard, int sex, String address,String annualIncome) throws IllegalArgumentException {
		name=checkNullAndTrim("name", name);
		identityCard=checkNullAndTrim("identityCard", identityCard);
		Lender lender=lenderDao.findByIdentityCard(identityCard);
		if(lender!=null)
			throw new IllegalArgumentException("身份证号已经存在");
		lender=getCurrentUser();
		lenderDao.registerSecondStep(lender.getId(), name, identityCard, sex, address,annualIncome);
		lender.setName(name);
		lender.setIdentityCard(identityCard);
		lender.setSex(sex);
		lender.setAddress(address);
		lender.setAnnualIncome(annualIncome);
	}

	@Override
	public void registerThirdPartyAccount(Integer id,String thirdPartyAccount,String accountNumber) {
		thirdPartyAccount=checkNullAndTrim("thirdPartyAccount", thirdPartyAccount);
		lenderDao.registerThirdPartyAccount(id, thirdPartyAccount,accountNumber);
		Lender lender=getCurrentUser();
		if(lender!=null)
		{
			lender.setThirdPartyAccount(thirdPartyAccount);
			lender.setAccountNumber(accountNumber);
		}
	}

	@Override
	public boolean isIdentityAuthentication() {
		Lender lender=getCurrentUser();
		return StringUtil.isEmpty(lender.getName())||StringUtil.isEmpty(lender.getIdentityCard())?false:true;
	}

	@Override
	public boolean isThirdPartyAuthentication() {
		Lender lender=getCurrentUser();
		return StringUtil.isEmpty(lender.getThirdPartyAccount())?false:true;
	}

	@Override
	public boolean isIdentityCardExist(String identityCard) {
		return lenderDao.findByIdentityCard(identityCard)==null?false:true;
	}

	@Override
	public Map<String, Object> findByPrivilegeWithPaging(int privilege, int offset,
			int recnum) {
		int count=lenderDao.countByPrivilege(privilege);
		if(count==0)
			return Pagination.buildResult(null, count, offset, recnum);
		return Pagination.buildResult(lenderDao.findByPrivilegeWithPaging(privilege, offset, recnum), count, offset, recnum);
	}

	@Override
	public void bindCard(Integer id, Integer cardId) {
		lenderDao.bindCard(id, cardId);
		Lender lender=getCurrentUser();
		if(lender!=null)
		{
			lender.setCardBindingId(cardId);
			lender.setCardBinding(cardBindingDao.find(cardId));
		}
	}
	
	@Override
	public void bindCard(Integer id, CardBinding cardBinding) {
		cardBindingDao.create(cardBinding);
		lenderDao.bindCard(id, cardBinding.getId());
		Lender lender=getCurrentUser();
		if(lender!=null)
		{
			lender.setCardBindingId(cardBinding.getId());
			lender.setCardBinding(cardBinding);
		}
	}
	
	@Override
	public boolean isEmailExist(String email) {
		List<Lender> lender=lenderDao.findByEmail(email.trim());
		return (lender==null || lender.isEmpty())?false:true;
	}
	
	@Override
	public CardBinding getBindingCard(Integer id){
		CardBinding cb = cardBindingDao.find(id);
		cb.setBankCode(BankCode.getName(cb.getBankCode()));
		return cb;
	}
	
	@Override
	public void sendMessageToAllLender(String message) throws SMSException{
		int usercount = lenderDao.countAll();
		List<Lender> lenders = lenderDao.findAll(0, usercount);
		if(lenders==null || lenders.isEmpty()){
			return;
		}
		List<String> phones = new ArrayList<String>();
		for(Lender lender : lenders){
			phones.add(lender.getTel());
			if(phones.size()>=200)
			{
				messageSupportService.sendSMS(phones, message);
				phones.clear();
				phones = new ArrayList<String>();
			}
		}
		if(phones.size()>0){
			messageSupportService.sendSMS(phones, message);
		}
	}
	
	@Override
	public List<Area> getProvinceCity(){
		return BankBindingUtils.getProvinceCity();
	}
}
