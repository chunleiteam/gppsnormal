package gpps.service;

import gpps.model.ref.Contactor;
import gpps.model.ref.Contactor.Single;

import java.math.BigDecimal;
import java.util.List;

public class PayBackToView {
	String orderTitle;
	String seriesTitle;
	Integer productid;
	Integer id;
	BigDecimal chief = new BigDecimal(0);
	BigDecimal interest = new BigDecimal(0);
	long deadline;
	Integer borrowerid;
	String borrowerName;
	String tel;
	List<Single> contactor;
	public String getOrderTitle() {
		return orderTitle;
	}
	public void setOrderTitle(String orderTitle) {
		this.orderTitle = orderTitle;
	}
	public String getSeriesTitle() {
		return seriesTitle;
	}
	public void setSeriesTitle(String seriesTitle) {
		this.seriesTitle = seriesTitle;
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
	public long getDeadline() {
		return deadline;
	}
	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}
	public Integer getBorrowerid() {
		return borrowerid;
	}
	public void setBorrowerid(Integer borrowerid) {
		this.borrowerid = borrowerid;
	}
	public String getBorrowerName() {
		return borrowerName;
	}
	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public List<Single> getContactor() {
		return contactor;
	}
	public void setContactor(List<Single> contactor) {
		this.contactor = contactor;
	}
	public Integer getProductid() {
		return productid;
	}
	public void setProductid(Integer productid) {
		this.productid = productid;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
