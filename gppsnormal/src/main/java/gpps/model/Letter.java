package gpps.model;

public class Letter {
	private Integer id;
	private String title;
	public static final int RECEIVERTYPE_LENDER=0;
	public static final int RECEIVERTYPE_BORROWER=1;
	private int receivertype;
	private Integer receiverId;
	private String content;
	private long createtime=System.currentTimeMillis();
	public static final int MARKREAD_NO=0;
	public static final int MARKREAD_YES=1;
	private int markRead=MARKREAD_NO;
	private long readtime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getReceivertype() {
		return receivertype;
	}
	public void setReceivertype(int receivertype) {
		this.receivertype = receivertype;
	}
	public Integer getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public int getMarkRead() {
		return markRead;
	}
	public void setMarkRead(int markRead) {
		this.markRead = markRead;
	}
	public long getReadtime() {
		return readtime;
	}
	public void setReadtime(long readtime) {
		this.readtime = readtime;
	}
}
