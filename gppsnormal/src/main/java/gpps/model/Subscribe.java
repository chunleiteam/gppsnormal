package gpps.model;
/**
 * 
 * 预约实体
 * 
 * */
public class Subscribe {
	
	public static final long SUBSCRIBE_END_TIME_INTERVAL = 13;   //预约截止到融资开始的时间间隔(小时)
	
	public static final int STATE_APPLY = 0;
	public static final int STATE_CONFIRM_PASS_ALL = 1;
	public static final int STATE_CONFIRM_PASS_MODIFY = 2;
	public static final int STATE_CONFIRM_REFUSE = 4;
	int applyAmount=0;
	int auditAmount=0;
	int id;
	
	Integer lenderId;
	Integer productId;
	long createTime;
	long confirmTime;
	int state = STATE_APPLY;
	String description;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getLenderId() {
		return lenderId;
	}
	public void setLenderId(Integer lenderId) {
		this.lenderId = lenderId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getConfirmTime() {
		return confirmTime;
	}
	public void setConfirmTime(long confirmTime) {
		this.confirmTime = confirmTime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getApplyAmount() {
		return applyAmount;
	}
	public void setApplyAmount(int applyAmount) {
		this.applyAmount = applyAmount;
	}
	public int getAuditAmount() {
		return auditAmount;
	}
	public void setAuditAmount(int auditAmount) {
		this.auditAmount = auditAmount;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	//辅助属性
	Lender lender = new Lender();

	public Lender getLender() {
		return lender;
	}
	public void setLender(Lender lender) {
		this.lender = lender;
	}
	
}
