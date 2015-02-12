package gpps.model;

import java.math.BigDecimal;

public class Submit {
	public static final long PAYEXPIREDTIME=3L*60*1000;
	private Integer id;
	/**
//	 * 1:申请竞标-> 2:待支付 （支付） 4:竞标中 (融资审核成功) 8:还款中 16：还款完毕
//	 * 
//	 * 32:流标(融资审核不成功) 64:退订（未支付） 128：异常（额度不足）申请不成功
//	 * 
//	 * 已购买;流标;
//	 * 其他状态从产品中获取
//	 * 
	 * 1:待付款;2:购买成功;4:退订;8:流标;
	 */
	public static final int STATE_WAITFORPAY=1;//1
	public static final int STATE_COMPLETEPAY=1<<1;//2
	public static final int STATE_UNSUBSCRIBE=1<<2;//4
	public static final int STATE_FAILBIDDING=1<<3;//8
	private int state=STATE_WAITFORPAY;
	private long createtime = System.currentTimeMillis();
	private Integer lenderId;
	private Integer productId;
	private long lastmodifytime = System.currentTimeMillis();
	private BigDecimal amount = BigDecimal.ZERO;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Integer getLenderId() {
		return lenderId;
	}
	public void setLenderId(Integer lenderId) {
		this.lenderId = lenderId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public long getLastmodifytime() {
		return lastmodifytime;
	}
	public void setLastmodifytime(long lastmodifytime) {
		this.lastmodifytime = lastmodifytime;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	//辅助对象
	private Product product;
	private BigDecimal repayedAmount=new BigDecimal(0);//已还款
	private BigDecimal waitforRepayAmount=new BigDecimal(0);//待回款
	private long payExpiredTime;
	private String lenderName;
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public BigDecimal getRepayedAmount() {
		return repayedAmount;
	}
	public void setRepayedAmount(BigDecimal repayedAmount) {
		this.repayedAmount = repayedAmount;
	}
	public long getPayExpiredTime() {
		return payExpiredTime;
	}
	public void setPayExpiredTime(long payExpiredTime) {
		this.payExpiredTime = payExpiredTime;
	}
	public String getLenderName() {
		return lenderName;
	}
	public void setLenderName(String lenderName) {
		this.lenderName = lenderName;
	}
	public BigDecimal getWaitforRepayAmount() {
		return waitforRepayAmount;
	}
	public void setWaitforRepayAmount(BigDecimal waitforRepayAmount) {
		this.waitforRepayAmount = waitforRepayAmount;
	}
}
