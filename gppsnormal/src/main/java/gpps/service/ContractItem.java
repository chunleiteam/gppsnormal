package gpps.service;

import java.math.BigDecimal;

public class ContractItem {
	private String orderName;
	private String seriesName;
	private int submitID;
	private String lenderName;
	private BigDecimal amount;
	private boolean exist;
	public boolean isExist() {
		return exist;
	}
	public void setExist(boolean exist) {
		this.exist = exist;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public String getSeriesName() {
		return seriesName;
	}
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}
	public int getSubmitID() {
		return submitID;
	}
	public void setSubmitID(int submitID) {
		this.submitID = submitID;
	}
	public String getLenderName() {
		return lenderName;
	}
	public void setLenderName(String lenderName) {
		this.lenderName = lenderName;
	}
}
