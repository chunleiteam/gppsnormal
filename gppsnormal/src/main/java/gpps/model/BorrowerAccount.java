package gpps.model;

import java.math.BigDecimal;

public class BorrowerAccount {
	private Integer id;
	private BigDecimal total = BigDecimal.ZERO; //总金额
	private BigDecimal freeze = BigDecimal.ZERO;//冻结金额
	private BigDecimal usable = BigDecimal.ZERO;//可用金额
	
	private BigDecimal used = BigDecimal.ZERO; //已投金额
	private BigDecimal totalincome = BigDecimal.ZERO;//已收益,不包括期望收益
	private BigDecimal totalFee = BigDecimal.ZERO;//已获手续费
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getFreeze() {
		return freeze;
	}
	public void setFreeze(BigDecimal freeze) {
		this.freeze = freeze;
	}
	public BigDecimal getUsable() {
		return usable;
	}
	public void setUsable(BigDecimal usable) {
		this.usable = usable;
	}
	public BigDecimal getUsed() {
		return used;
	}
	public void setUsed(BigDecimal used) {
		this.used = used;
	}
	public BigDecimal getTotalincome() {
		return totalincome;
	}
	public void setTotalincome(BigDecimal totalincome) {
		this.totalincome = totalincome;
	}
	public BigDecimal getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(BigDecimal totalFee) {
		this.totalFee = totalFee;
	}
}
