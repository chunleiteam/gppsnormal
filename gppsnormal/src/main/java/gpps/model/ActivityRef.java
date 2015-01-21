package gpps.model;

public class ActivityRef {
	private Integer id;
	public static final int PARTICIPATORTYPE_LENDER = 0;
	public static final int PARTICIPATORTYPE_BORROWER = 1;
	private int participatorType = PARTICIPATORTYPE_LENDER;
	private Integer participatorId;
	private Integer activityId;
	private long applyTime = System.currentTimeMillis();
	public static final int PARTICIPATE_YES = 0;
	public static final int PARTICIPATE_NO = 1;
	private int participate = PARTICIPATE_YES;
	private String awarddetail;
	private String description;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getParticipatorType() {
		return participatorType;
	}
	public void setParticipatorType(int participatorType) {
		this.participatorType = participatorType;
	}
	public Integer getParticipatorId() {
		return participatorId;
	}
	public void setParticipatorId(Integer participatorId) {
		this.participatorId = participatorId;
	}
	public Integer getActivityId() {
		return activityId;
	}
	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}
	public long getApplyTime() {
		return applyTime;
	}
	public void setApplyTime(long applyTime) {
		this.applyTime = applyTime;
	}
	public String getAwarddetail() {
		return awarddetail;
	}
	public void setAwarddetail(String awarddetail) {
		this.awarddetail = awarddetail;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getParticipate() {
		return participate;
	}
	public void setParticipate(int participate) {
		this.participate = participate;
	}
	
	
	private Lender lender;
	private Activity activity;
	public Lender getLender() {
		return lender;
	}
	public void setLender(Lender lender) {
		this.lender = lender;
	}
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
}
