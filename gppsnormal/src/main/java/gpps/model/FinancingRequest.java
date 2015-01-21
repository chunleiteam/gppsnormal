package gpps.model;

public class FinancingRequest {
	private Integer id;
	private String govermentOrderName;
	private String govermentOrderDetail;
	private int applyFinancingAmount=0;
	private String rate;
	public static final int STATE_INIT=0;//初始
	public static final int STATE_PROCESSED=1;//已处理
	public static final int STATE_REFUSE=2;//拒绝
	private int state=STATE_INIT;
	private long createtime=System.currentTimeMillis();
	private long lastModifyTime=System.currentTimeMillis();
	private Integer borrowerID;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGovermentOrderName() {
		return govermentOrderName;
	}
	public void setGovermentOrderName(String govermentOrderName) {
		this.govermentOrderName = govermentOrderName;
	}
	public String getGovermentOrderDetail() {
		return govermentOrderDetail;
	}
	public void setGovermentOrderDetail(String govermentOrderDetail) {
		this.govermentOrderDetail = govermentOrderDetail;
	}
	public int getApplyFinancingAmount() {
		return applyFinancingAmount;
	}
	public void setApplyFinancingAmount(int applyFinancingAmount) {
		this.applyFinancingAmount = applyFinancingAmount;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public long getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	public Integer getBorrowerID() {
		return borrowerID;
	}
	public void setBorrowerID(Integer borrowerID) {
		this.borrowerID = borrowerID;
	}
	
	//辅助
	private GovermentOrder govermentOrder;
	private Borrower borrower;
	public GovermentOrder getGovermentOrder() {
		return govermentOrder;
	}
	public void setGovermentOrder(GovermentOrder govermentOrder) {
		this.govermentOrder = govermentOrder;
	}
	public Borrower getBorrower() {
		return borrower;
	}
	public void setBorrower(Borrower borrower) {
		this.borrower = borrower;
	}
}
