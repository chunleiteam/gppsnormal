package gpps.model;

public class HandleLog {
	private Integer id;
	public static final int HANDLERTYPE_ANONYMOUS=-1;
	public static final int HANDLERTYPE_LENDER=0;
	public static final int HANDLERTYPE_BORROWER=1;
	public static final int HANDLERTYPE_ADMIN=2;
	private int  handlertype;
	private Integer handlerId;
	private String callService;
	private String callmethod;
	private String callparam;
	private long handletime=System.currentTimeMillis();
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getHandlertype() {
		return handlertype;
	}
	public void setHandlertype(int handlertype) {
		this.handlertype = handlertype;
	}
	public Integer getHandlerId() {
		return handlerId;
	}
	public void setHandlerId(Integer handlerId) {
		this.handlerId = handlerId;
	}
	public String getCallService() {
		return callService;
	}
	public void setCallService(String callService) {
		this.callService = callService;
	}
	public String getCallmethod() {
		return callmethod;
	}
	public void setCallmethod(String callmethod) {
		this.callmethod = callmethod;
	}
	public String getCallparam() {
		return callparam;
	}
	public void setCallparam(String callparam) {
		this.callparam = callparam;
	}
	public long getHandletime() {
		return handletime;
	}
	public void setHandletime(long handletime) {
		this.handletime = handletime;
	}
}
