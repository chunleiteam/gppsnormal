package gpps.service;

public class SubscribeAudit {
	Integer lenderId;
	Integer subsribeId;
	int applyAmount;
	int auditAmount;
	public Integer getLenderId() {
		return lenderId;
	}
	public void setLenderId(Integer lenderId) {
		this.lenderId = lenderId;
	}
	public Integer getSubsribeId() {
		return subsribeId;
	}
	public void setSubsribeId(Integer subsribeId) {
		this.subsribeId = subsribeId;
	}
	public int getApplyAmount() {
		return applyAmount;
	}
	public void setApplyAmount(int applyAmount) {
		this.applyAmount = applyAmount;
	}
	public int getAuditAmount() {
		return auditAmount;
	}
	public void setAuditAmount(int auditAmount) {
		this.auditAmount = auditAmount;
	}
}
