package gpps.model;
public class Task {
	private Integer id;
	public static final int TYPE_PAY=0;//完成融资支付
	public static final int TYPE_REPAY=1;//执行还款
	public static final int TYPE_QUITFINANCING=2;//流标
	public static final int TYPE_CHECK_REPAY=3; //还款审核
	private int type;
	private long createTime=System.currentTimeMillis();
	public static final int STATE_INIT=0;
	public static final int STATE_PROCESSING=1;
	public static final int STATE_FINISH=2;
	private int state=STATE_INIT;
	private Integer productId;
	private Integer payBackId;
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
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getPayBackId() {
		return payBackId;
	}
	public void setPayBackId(Integer payBackId) {
		this.payBackId = payBackId;
	}
	@Override
	public String toString() {
		return "Task [id=" + id + ", type=" + type + ", createTime="
				+ createTime + ", state=" + state + ", productId=" + productId
				+ ", payBackId=" + payBackId + "]";
	}
}
