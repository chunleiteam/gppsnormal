package gpps.model;

public class Activity {
	private Integer id;
	private String name;
	private long applystarttime;
	private long applyendtime;
	private long starttime;
	private long createtime;
	// 0:未发布;1：报名;2：进行;3：结束
	public static final int STATE_UNPUBLISHED = 0;
	public static final int STATE_APPLY = 1;
	public static final int STATE_FINISH = 2;
	private int state = STATE_UNPUBLISHED;
	private String url;
	private String accessory;
	private String description;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getApplystarttime() {
		return applystarttime;
	}
	public void setApplystarttime(long applystarttime) {
		this.applystarttime = applystarttime;
	}
	public long getApplyendtime() {
		return applyendtime;
	}
	public void setApplyendtime(long applyendtime) {
		this.applyendtime = applyendtime;
	}
	public long getStarttime() {
		return starttime;
	}
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAccessory() {
		return accessory;
	}
	public void setAccessory(String accessory) {
		this.accessory = accessory;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
