package gpps.model;

public class Invite {
	public static final int STATE_INIT = 1;
	public static final int STATE_HANDLEING = 2;
	public static final int STATE_REGISTERED = 4;
	private Integer id;
	private String code;
	private Integer attributeTo;
	private Integer registerBy;
	private Integer batchCode;
	
	private int state=STATE_INIT;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getAttributeTo() {
		return attributeTo;
	}
	public void setAttributeTo(Integer attributeTo) {
		this.attributeTo = attributeTo;
	}
	public Integer getRegisterBy() {
		return registerBy;
	}
	public void setRegisterBy(Integer registerBy) {
		this.registerBy = registerBy;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Integer getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(Integer batchCode) {
		this.batchCode = batchCode;
	}
}
