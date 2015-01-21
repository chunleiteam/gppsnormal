package gpps.model;

public class Notice {
	private Integer id;
	private String title;
	private String content;
	private long publishtime = System.currentTimeMillis();
	public static final int USEFOR_ALL = 0;
	public static final int USEFOR_LENDER = 1;
	public static final int USEFOR_BORROWER = 2;
	private int usefor =USEFOR_ALL;//0 ：All,1:lender 2：borrower
	private int level = 0;
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
	public int getUsefor() {
		return usefor;
	}
	public void setUsefor(int usefor) {
		this.usefor = usefor;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getPublicType() {
		return publicType;
	}
	public void setPublicType(int publicType) {
		this.publicType = publicType;
	}
}
