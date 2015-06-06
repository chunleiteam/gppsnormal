package gpps.service;

import java.math.BigDecimal;

public class PurchaseCalculateResult {
	//投标时的额度（原始标的额度）
	BigDecimal submitAmount;
	//已还款本金
	BigDecimal chiefAlread;
	//剩余本金金额
	BigDecimal chiefTo;
	//在本还款周期内已经累计但尚未还款的利息
	BigDecimal interestTo;
	//本次购买需要交纳的手续费
	BigDecimal purchaseFee;    //目前为0
	//本次购买需要的金额
	BigDecimal purchaseAmount;
	//本还款周期内已经持有的天数
	int holdingDays;
	
	public int getHoldingDays() {
		return holdingDays;
	}
	public void setHoldingDays(int holdingDays) {
		this.holdingDays = holdingDays;
	}
	public BigDecimal getSubmitAmount() {
		return submitAmount;
	}
	public void setSubmitAmount(BigDecimal submitAmount) {
		this.submitAmount = submitAmount;
	}
	public BigDecimal getChiefAlread() {
		return chiefAlread;
	}
	public void setChiefAlread(BigDecimal chiefAlread) {
		this.chiefAlread = chiefAlread;
	}
	public BigDecimal getChiefTo() {
		return chiefTo;
	}
	public void setChiefTo(BigDecimal chiefTo) {
		this.chiefTo = chiefTo;
	}
	public BigDecimal getInterestTo() {
		return interestTo;
	}
	public void setInterestTo(BigDecimal interestTo) {
		this.interestTo = interestTo;
	}
	public BigDecimal getPurchaseFee() {
		return purchaseFee;
	}
	public void setPurchaseFee(BigDecimal purchaseFee) {
		this.purchaseFee = purchaseFee;
	}
	public BigDecimal getPurchaseAmount() {
		return purchaseAmount;
	}
	public void setPurchaseAmount(BigDecimal purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}
}
