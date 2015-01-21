package gpps.service;

import java.math.BigDecimal;

public class CashStreamSum {
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
	@Override
	public String toString() {
		return "Sum [chiefAmount=" + chiefAmount + ", interest="
				+ interest + "]";
	}
}
