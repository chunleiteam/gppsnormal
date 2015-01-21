package gpps.model;

public class StateLog {
	private Integer id;
	/**
	 * 0：submit 1：product 2：Govermentorder,3:payback 4:CashStream
	 */
	public static final int TYPE_SUBMIT=0;
	public static final int TYPE_PRODUCT=1;
	public static final int TYPE_GOVERMENTORDER=2;
	public static final int TYPE_PAYBACK=3;
	public static final int TYPE_CASHSTREAM=4;
	private int type;
	private long createtime = System.currentTimeMillis();
	private int source=-99;//原状态,-99表示无source
	private int target;//目标状态
	private Integer refid;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public Integer getRefid() {
		return refid;
	}
	public void setRefid(Integer refid) {
		this.refid = refid;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public int getTarget() {
		return target;
	}
	public void setTarget(int target) {
		this.target = target;
	}
}
