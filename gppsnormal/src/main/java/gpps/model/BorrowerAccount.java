package gpps.model;

import java.math.BigDecimal;

public class BorrowerAccount {
	private Integer id;
	private BigDecimal total = BigDecimal.ZERO; //总金额
	private BigDecimal freeze = BigDecimal.ZERO;//冻结金额
	private BigDecimal usable = BigDecimal.ZERO;//可用金额
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
}
