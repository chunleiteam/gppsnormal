package gpps.model;

public class Help {
	private Integer id;
	public static final int TYPE_PUBLIC = 0;
	public static final int TYPE_PRIVATE = 1;
	public static final int TYPE_PRIVATE_ANSWERED=2;
	private int type = TYPE_PUBLIC;
	public static final int PUBLICTYPE_BEGINNER = 0;// 新手帮助
	public static final int PUBLICTYPE_COMMON = 1;// 常见问题
	private int publicType;
	public static final int QUESTIONERTYPE_LENDER = 0;//
	public static final int QUESTIONERTYPE_BORROWER = 1;//
	private int questionerType;
	private Integer questionerId;
	private String question;
	private String answer;
	private long createtime=System.currentTimeMillis();
	private long answertime;
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
	public int getPublicType() {
		return publicType;
	}
	public void setPublicType(int publicType) {
		this.publicType = publicType;
	}
	public int getQuestionerType() {
		return questionerType;
	}
	public void setQuestionerType(int questionerType) {
		this.questionerType = questionerType;
	}
	public Integer getQuestionerId() {
		return questionerId;
	}
	public void setQuestionerId(Integer questionerId) {
		this.questionerId = questionerId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public long getCreatetime() {
		return createtime;
	}
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}
	public long getAnswertime() {
		return answertime;
	}
	public void setAnswertime(long answertime) {
		this.answertime = answertime;
	}
}
