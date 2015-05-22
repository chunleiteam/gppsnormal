package gpps.model;

import java.math.BigDecimal;

public class PayBack {
//	public static final BigDecimal BASELINE=new BigDecimal(10*10000);
	private Integer id;
	private Integer borrowerAccountId;
	private Integer productId;
	/**
	 * 0：待还款 1：正在还款 2:已还款 3：延期(待确定) 4:失效 5 待审核 6申请提前还款
	 */
	public static int STATE_WAITFORREPAY=0;
	public static int STATE_REPAYING=1;
	public static int STATE_FINISHREPAY=2;
	public static int STATE_DELAY=3;
	public static int STATE_INVALID=4;
	public static int STATE_WAITFORCHECK=5;
	public static int STATE_APPLYREPAYINADVANCE=6;
	private int state;
	private BigDecimal chiefAmount=BigDecimal.ZERO;
	private BigDecimal interest=BigDecimal.ZERO;
	private long deadline;//还款时间
	private long realtime;//实际还款时间
	private long checktime;//验证时间
	public static final int TYPE_INTERESTANDCHIEF=0;
	public static final int TYPE_LASTPAY=1;
	public static final int TYPE_REWARD=2;
	private int type;//还款方式 0:利息和本息;1:一次性本金;2:发放奖励
	public static final int CHECK_NOT=0;
	public static final int CHECK_SUCCESS=1;
	public static final int CHECK_FAIL=2;
	private int checkResult=CHECK_NOT;  //当type==2时，checkResult代表了发放奖励的批次
//	private Integer paybackId;
//	/**
//	 * 0：正常还款 1：新增（指向延期）
//	 */
//	private int type;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getBorrowerAccountId() {
		return borrowerAccountId;
	}
	public void setBorrowerAccountId(Integer borrowerAccountId) {
		this.borrowerAccountId = borrowerAccountId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public BigDecimal getChiefAmount() {
		return chiefAmount;
	}
	public void setChiefAmount(BigDecimal chiefAmount) {
		this.chiefAmount = chiefAmount;
	}
	public BigDecimal getInterest() {
		return interest;
	}
	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}
	public long getDeadline() {
		return deadline;
	}
	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}
	public long getRealtime() {
		return realtime;
	}
	public void setRealtime(long realtime) {
		this.realtime = realtime;
	}
	public long getChecktime() {
		return checktime;
	}
	public void setChecktime(long checktime) {
		this.checktime = checktime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(int checkResult) {
		this.checkResult = checkResult;
	}




	//辅助
	private Product product;
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
}
