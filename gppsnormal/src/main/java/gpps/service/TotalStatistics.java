package gpps.service;

import java.math.BigDecimal;

public class TotalStatistics {
	private BigDecimal raiseAmount=BigDecimal.ZERO;//募集资金
	private int investment;//投资人次
	private BigDecimal incomeAmount=BigDecimal.ZERO;//收益数量
	private int lenderNum;//注册人数
	public BigDecimal getRaiseAmount() {
		return raiseAmount;
	}
	public void setRaiseAmount(BigDecimal raiseAmount) {
		this.raiseAmount = raiseAmount;
	}
	public int getInvestment() {
		return investment;
	}
	public void setInvestment(int investment) {
		this.investment = investment;
	}
	public BigDecimal getIncomeAmount() {
		return incomeAmount;
	}
	public void setIncomeAmount(BigDecimal incomeAmount) {
		this.incomeAmount = incomeAmount;
	}
	public int getLenderNum() {
		return lenderNum;
	}
	public void setLenderNum(int lenderNum) {
		this.lenderNum = lenderNum;
	}
}
