package gpps.model;

public class News {
	private Integer id;
	private String title;
	private String content;
	private long publishtime = System.currentTimeMillis();
	private int publicType;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getPublishtime() {
		return publishtime;
	}
	public void setPublishtime(long publishtime) {
		this.publishtime = publishtime;
	}
	public int getPublicType() {
		return publicType;
	}
	public void setPublicType(int publicType) {
		this.publicType = publicType;
	}
}
