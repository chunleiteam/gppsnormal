package gpps.tools;

import java.math.BigDecimal;

public class SinglePayBack {
	public static final int STATE_UNREPAY = 0;
	public static final int STATE_REPAY_SUCCESS = 1;
	
	int state = STATE_UNREPAY;
	String fromname;
	Integer fromAccountId;
	String fromMoneyMoreMore;
	String toname;
	Integer toAccountId;
	String toMoneyMoreMore;
	BigDecimal chief;
	BigDecimal interest;
	BigDecimal submitAmount;
	Integer submitId;
	
	public Integer getSubmitId() {
		return submitId;
	}
	public void setSubmitId(Integer submitId) {
		this.submitId = submitId;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getFromname() {
		return fromname;
	}
	public void setFromname(String fromname) {
		this.fromname = fromname;
	}
	public Integer getFromAccountId() {
		return fromAccountId;
	}
	public void setFromAccountId(Integer fromAccountId) {
		this.fromAccountId = fromAccountId;
	}
	public String getFromMoneyMoreMore() {
		return fromMoneyMoreMore;
	}
	public void setFromMoneyMoreMore(String fromMoneyMoreMore) {
		this.fromMoneyMoreMore = fromMoneyMoreMore;
	}
	public String getToname() {
		return toname;
	}
	public void setToname(String toname) {
		this.toname = toname;
	}
	public Integer getToAccountId() {
		return toAccountId;
	}
	public void setToAccountId(Integer toAccountId) {
		this.toAccountId = toAccountId;
	}
	public String getToMoneyMoreMore() {
		return toMoneyMoreMore;
	}
	public void setToMoneyMoreMore(String toMoneyMoreMore) {
		this.toMoneyMoreMore = toMoneyMoreMore;
	}
	public BigDecimal getChief() {
		return chief;
	}
	public void setChief(BigDecimal chief) {
		this.chief = chief;
	}
	public BigDecimal getInterest() {
		return interest;
	}
	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}
	public BigDecimal getSubmitAmount() {
		return submitAmount;
	}
	public void setSubmitAmount(BigDecimal submitAmount) {
		this.submitAmount = submitAmount;
	}
}
