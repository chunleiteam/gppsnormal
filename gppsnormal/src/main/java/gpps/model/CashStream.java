package gpps.model;

import java.math.BigDecimal;

public class CashStream {
	private Integer id;
	/**
	 * 0:向账户充值 1:冻结(包括借款方竞标冻结/融资方还钱冻结) 2：解冻 3:贷款人将冻结资金支付给借款人 4:借款人还款 5:从账户提现;6将除不尽的零钱存入平台账户
	 */
	public static final int ACTION_RECHARGE=0;
	public static final int ACTION_FREEZE=1;
	public static final int ACTION_UNFREEZE=2;
	public static final int ACTION_PAY=3;
	public static final int ACTION_REPAY=4;
	public static final int ACTION_CASH=5;
	public static final int ACTION_STORECHANGE=6;
	private int action;
	private long createtime = System.currentTimeMillis();
	private Integer lenderAccountId;
	private Integer borrowerAccountId;
	private Integer submitId;
	private BigDecimal chiefamount = BigDecimal.ZERO;//本金
	private BigDecimal interest = BigDecimal.ZERO;//利息
	private BigDecimal fee = BigDecimal.ZERO;//手续费
	private String description;
	private Integer paybackId;
	/**
	 * 1:预操作；2:操作完成;4操作失败;8 提现退回
	 */
	public static final int STATE_INIT=1;
	public static final int STATE_SUCCESS=1<<1;
	public static final int STATE_FAIL=1<<2;
	public static final int STATE_RETURN=1<<3;
	private int state=STATE_INIT;
	private String loanNo;//钱多多流水号
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public Integer getLenderAccountId() {
		return lenderAccountId;
	}
	public void setLenderAccountId(Integer lenderAccountId) {
		this.lenderAccountId = lenderAccountId;
	}
	public Integer getBorrowerAccountId() {
		return borrowerAccountId;
	}
	public void setBorrowerAccountId(Integer borrowerAccountId) {
		this.borrowerAccountId = borrowerAccountId;
	}
	public Integer getSubmitId() {
		return submitId;
	}
	public void setSubmitId(Integer submitId) {
		this.submitId = submitId;
	}
	public BigDecimal getChiefamount() {
		return chiefamount;
	}
	public void setChiefamount(BigDecimal chiefamount) {
		this.chiefamount = chiefamount;
	}
	public BigDecimal getInterest() {
		return interest;
	}
	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getPaybackId() {
		return paybackId;
	}
	public void setPaybackId(Integer paybackId) {
		this.paybackId = paybackId;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getLoanNo() {
		return loanNo;
	}
	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}
	public BigDecimal getFee() {
		return fee;
	}
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}






	//辅助对象
	private Submit submit;
	public Submit getSubmit() {
		return submit;
	}
	public void setSubmit(Submit submit) {
		this.submit = submit;
	}
}
