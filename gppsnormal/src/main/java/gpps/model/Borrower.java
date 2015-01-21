package gpps.model;

import com.easyservice.security.Permit;

public class Borrower implements Permit{
	private Integer id;//主键ID
	private String name;//名称
	private String companyName;//公司名称
	private String tel;//手机
	private String email;//email
	private String loginId;//登录名
	private String password;//md5加密密码
	private String identityCard;//法人身份证
	private Integer accountId;//账户ID
	private String material;//附件
	private String contactor; //记录若干个联系人
	private String request;
	private long createtime=System.currentTimeMillis();
	public static final int PRIVILEGE_VIEW=10;//有查看权限的企业用户
	public static final int PRIVILEGE_APPLY=11;//申请融资权限的企业用户
	public static final int PRIVILEGE_FINANCING=12;//有融资权限的企业用户
	public static final int PRIVILEGE_REFUSE=14;//审核未通过
	private int privilege=PRIVILEGE_VIEW;//用户角色
	private int creditValue=0;//信用值
	private String license;// 企业营业执照
	private String corporationPhone;//法人电话
	private String corporationName;//法人姓名
	private String corporationAddr;//法人联系地址
	private String thirdPartyAccount;//第三方账户
	private int level=0;//信用等级
	private long lastModifyTime=System.currentTimeMillis();
	private String brange;//经营范围
	private Integer cardBindingId;
	public static final int AUTHORIZETYPEOPEN_RECHARGE=1<<1;//还款授权
	public static final int AUTHORIZETYPEOPEN_SECORD=1<<2;//二次分配授权
	private int authorizeTypeOpen=0;
	private String accountNumber;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIdentityCard() {
		return identityCard;
	}
	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getContactor() {
		return contactor;
	}
	public void setContactor(String contactor) {
		this.contactor = contactor;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public int getPrivilege() {
		return privilege;
	}
	public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public int getCreditValue() {
		return creditValue;
	}
	public void setCreditValue(int creditValue) {
		this.creditValue = creditValue;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getCorporationPhone() {
		return corporationPhone;
	}
	public void setCorporationPhone(String corporationPhone) {
		this.corporationPhone = corporationPhone;
	}
	public String getCorporationName() {
		return corporationName;
	}
	public void setCorporationName(String corporationName) {
		this.corporationName = corporationName;
	}
	public String getCorporationAddr() {
		return corporationAddr;
	}
	public void setCorporationAddr(String corporationAddr) {
		this.corporationAddr = corporationAddr;
	}
	public String getThirdPartyAccount() {
		return thirdPartyAccount;
	}
	public void setThirdPartyAccount(String thirdPartyAccount) {
		this.thirdPartyAccount = thirdPartyAccount;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public long getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	
	public String getBrange() {
		return brange;
	}
	public void setBrange(String brange) {
		this.brange = brange;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public Integer getCardBindingId() {
		return cardBindingId;
	}
	public void setCardBindingId(Integer cardBindingId) {
		this.cardBindingId = cardBindingId;
	}
	public int getAuthorizeTypeOpen() {
		return authorizeTypeOpen;
	}
	public void setAuthorizeTypeOpen(int authorizeTypeOpen) {
		this.authorizeTypeOpen = authorizeTypeOpen;
	}
	
	private CardBinding cardBinding;
	public CardBinding getCardBinding() {
		return cardBinding;
	}
	public void setCardBinding(CardBinding cardBinding) {
		this.cardBinding = cardBinding;
	}
	public static int creditValueToLevel(int creditValue)
	{
		//TODO borrower级别修改
		if(creditValue>10000000)
			return 5;//钻石VIP用户
		if(creditValue>1000000)
			return 4;//白金VIP用户
		if(creditValue>200000)
			return 3;//黄金VIP用户
		if(creditValue>50000)
			return 2;//白银VIP用户
		if(creditValue>10000)
			return 1;//VIP用户
		return 0;//普通用户
	}
}
