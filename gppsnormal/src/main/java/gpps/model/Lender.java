package gpps.model;

import java.math.BigDecimal;

import com.easyservice.security.Permit;

public class Lender implements Permit {
	private Integer id;//主键ID
	private String name;//名称
	private String tel;//手机
	private String email;//email
	private String loginId;//登录名
	private String password;//md5加密密码
	private String identityCard;//身份证
	private Integer accountId;//账户ID
	private long createtime=System.currentTimeMillis();//创建时间
	public static final int PRIVILEGE_UNOFFICIAL=0;//非正式用户
	public static final int PRIVILEGE_OFFICIAL=1;//正式用户
	private int privilege=PRIVILEGE_UNOFFICIAL;//用户角色
	public static final int LEVEL_COMMON=0;//普通用户
	public static final int LEVEL_VIP1=1;//VIP1
	private int level=LEVEL_COMMON;
	private int grade=0;//评分
	private int sex=0;//男：0；女：1
	private String address;//通信地址
	private String annualIncome;//年收入
	private String thirdPartyAccount;//用户的乾多多标识
	private String accountNumber;//多多号
	private Integer cardBindingId;
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
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
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
	public int getPrivilege() {
		return privilege;
	}
	public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getThirdPartyAccount() {
		return thirdPartyAccount;
	}
	public void setThirdPartyAccount(String thirdPartyAccount) {
		this.thirdPartyAccount = thirdPartyAccount;
	}
	public String getAnnualIncome() {
		return annualIncome;
	}
	public void setAnnualIncome(String annualIncome) {
		this.annualIncome = annualIncome;
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
	
	
	//辅助对象
	private CardBinding cardBinding;
	public CardBinding getCardBinding() {
		return cardBinding;
	}
	public void setCardBinding(CardBinding cardBinding) {
		this.cardBinding = cardBinding;
	}
	public static int gradeToLevel(int grade)
	{
		if(grade>10000000)
			return 5;//钻石VIP用户
		if(grade>1000000)
			return 4;//白金VIP用户
		if(grade>200000)
			return 3;//黄金VIP用户
		if(grade>50000)
			return 2;//白银VIP用户
		if(grade>10000)
			return 1;//VIP用户
		return 0;//普通用户
	}
	
	/**
	 * 根据90天内购买的次数来决定购买金额的权重
	 * 
	 * */
	public static BigDecimal gradeWeight(int count){
		if(count<=5)
		{
			return new BigDecimal(1);
		}else if(count>5 && count<=10){
			return new BigDecimal(1.5);
		}else if(count>10 && count<=20){
			return new BigDecimal(2);
		}else{
			return new BigDecimal(3);
		}
	}
}
