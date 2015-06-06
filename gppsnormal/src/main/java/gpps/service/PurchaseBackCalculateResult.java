package gpps.service;

import java.math.BigDecimal;

public class PurchaseBackCalculateResult {
	
	//投标时的额度（原始标的额度）
	BigDecimal submitAmount;
	//回购完毕后，连本带息总共获得的金额（等于本次回购获得的金额+以前还款所得金额）
	BigDecimal totalAmount;
	//本次回购共得到的金额
	BigDecimal purchaseAmount;
	
	//已经获得的利息
	BigDecimal interestAlready;
	//已经获得的本金
	BigDecimal chiefAlread;
	//在本还款周期内已经累计但尚未还款的利息
	BigDecimal interestTo;
	
	//剩余的本金余额
	BigDecimal chiefTo;
	
	//本次回购需要交纳的手续费
	BigDecimal purchaseBackFee;
	
	//共持有天数
	int holdingDays;
	
	//回购执行完成后，共获得的利息（总利息-手续费）
	BigDecimal interestAfterPB;
	//回购执行完成后，在持有天数内获得的年华利率
	BigDecimal rateAfterPB;
	
	public BigDecimal getPurchaseAmount() {
		return purchaseAmount;
	}
	public void setPurchaseAmount(BigDecimal purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}
	public BigDecimal getSubmitAmount() {
		return submitAmount;
	}
	public void setSubmitAmount(BigDecimal submitAmount) {
		this.submitAmount = submitAmount;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getInterestAlready() {
		return interestAlready;
	}
	public void setInterestAlready(BigDecimal interestAlready) {
		this.interestAlready = interestAlready;
	}
	public BigDecimal getChiefAlread() {
		return chiefAlread;
	}
	public void setChiefAlread(BigDecimal chiefAlread) {
		this.chiefAlread = chiefAlread;
	}
	public BigDecimal getInterestTo() {
		return interestTo;
	}
	public void setInterestTo(BigDecimal interestTo) {
		this.interestTo = interestTo;
	}
	public BigDecimal getChiefTo() {
		return chiefTo;
	}
	public void setChiefTo(BigDecimal chiefTo) {
		this.chiefTo = chiefTo;
	}
	public BigDecimal getPurchaseBackFee() {
		return purchaseBackFee;
	}
	public void setPurchaseBackFee(BigDecimal purchaseBackFee) {
		this.purchaseBackFee = purchaseBackFee;
	}
	public int getHoldingDays() {
		return holdingDays;
	}
	public void setHoldingDays(int holdingDays) {
		this.holdingDays = holdingDays;
	}
	public BigDecimal getInterestAfterPB() {
		return interestAfterPB;
	}
	public void setInterestAfterPB(BigDecimal interestAfterPB) {
		this.interestAfterPB = interestAfterPB;
	}
	public BigDecimal getRateAfterPB() {
		return rateAfterPB;
	}
	public void setRateAfterPB(BigDecimal rateAfterPB) {
		this.rateAfterPB = rateAfterPB;
	}
}
