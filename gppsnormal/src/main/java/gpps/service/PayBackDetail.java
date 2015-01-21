package gpps.service;

import java.math.BigDecimal;

public class PayBackDetail {
	public static final String ONEYEAR="oneyear";
	public static final String HALFYEAR="halfyear";
	public static final String THREEMONTH="threemonth";
	public static final String TWOMONTH="twomonth";
	public static final String ONEMONTH="onemonth";
	private BigDecimal chiefAmount=BigDecimal.ZERO;
	private BigDecimal interest=BigDecimal.ZERO;
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
}
