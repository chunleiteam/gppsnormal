package gpps.model;

import java.math.BigDecimal;

public class Product {
	private Integer id;
	/**
	 * 1：融资中;
	 * 2：还款中;
	 * 4:流标
	 * 8:还款完成
	 * 16：延期 （暂时未用）
	 * 32：申请关闭(放弃)
	 * 64：已关闭(放弃)
	 */
	public static final int STATE_UNPUBLISH=0;
	public static final int STATE_FINANCING=1;
	public static final int STATE_REPAYING=1<<1;
	public static final int STATE_QUITFINANCING=1<<2;
	public static final int STATE_FINISHREPAY=1<<3;
	public static final int STATE_POSTPONE=1<<4;
	public static final int STATE_APPLYTOCLOSE=1<<5;
	public static final int STATE_CLOSE=1<<6;
	private int state=STATE_FINANCING;
	private Integer govermentorderId;
	private BigDecimal expectAmount = BigDecimal.ZERO;
	private BigDecimal realAmount = BigDecimal.ZERO;
	private BigDecimal rate = BigDecimal.ZERO;
	/**
	 * 0： 等额本息 1：按月还息，到期还本 2：按月还息,本金以订单回款为准
	 */
	public static final int PAYBACKMODEL_AVERAGECAPITALPLUSINTEREST=0;
	public static final int PAYBACKMODEL_FIRSTINTERESTENDCAPITAL=1;
	public static final int PAYBACKMODEL_FINISHPAYINTERESTANDCAPITAL=2;
	private int paybackmodel;
	private String accessory;
	private Integer productseriesId;
	private int levelToBuy;
	private long createtime=System.currentTimeMillis();
	private int minimum=1;//最小额度,单位元
	private int miniAdd=1;//最小追加金额
	private String description;
	private long incomeEndtime;//收益截止时间
	private long lastmodifytime=System.currentTimeMillis();//上次更改时间
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
	public Integer getGovermentorderId() {
		return govermentorderId;
	}
	public void setGovermentorderId(Integer govermentorderId) {
		this.govermentorderId = govermentorderId;
	}
	public BigDecimal getExpectAmount() {
		return expectAmount;
	}
	public void setExpectAmount(BigDecimal expectAmount) {
		this.expectAmount = expectAmount;
	}
	public BigDecimal getRealAmount() {
		return realAmount;
	}
	public void setRealAmount(BigDecimal realAmount) {
		this.realAmount = realAmount;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public int getPaybackmodel() {
		return paybackmodel;
	}
	public void setPaybackmodel(int paybackmodel) {
		this.paybackmodel = paybackmodel;
	}
	public String getAccessory() {
		return accessory;
	}
	public void setAccessory(String accessory) {
		this.accessory = accessory;
	}
	public Integer getProductseriesId() {
		return productseriesId;
	}
	public void setProductseriesId(Integer productseriesId) {
		this.productseriesId = productseriesId;
	}
	public int getLevelToBuy() {
		return levelToBuy;
	}
	public void setLevelToBuy(int levelToBuy) {
		this.levelToBuy = levelToBuy;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public int getMinimum() {
		return minimum;
	}
	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}
	public int getMiniAdd() {
		return miniAdd;
	}
	public void setMiniAdd(int miniAdd) {
		this.miniAdd = miniAdd;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getIncomeEndtime() {
		return incomeEndtime;
	}
	public void setIncomeEndtime(long incomeEndtime) {
		this.incomeEndtime = incomeEndtime;
	}
	public long getLastmodifytime() {
		return lastmodifytime;
	}
	public void setLastmodifytime(long lastmodifytime) {
		this.lastmodifytime = lastmodifytime;
	}






	//辅助对象
	private GovermentOrder govermentOrder;
	public GovermentOrder getGovermentOrder() {
		return govermentOrder;
	}
	public void setGovermentOrder(GovermentOrder govermentOrder) {
		this.govermentOrder = govermentOrder;
	}
	private ProductSeries productSeries;
	public ProductSeries getProductSeries() {
		return productSeries;
	}
	public void setProductSeries(ProductSeries productSeries) {
		this.productSeries = productSeries;
	}
	private int sCount=0;
	public int getsCount() {
		return sCount;
	}
	public void setsCount(int sCount) {
		this.sCount = sCount;
	}
	
}
