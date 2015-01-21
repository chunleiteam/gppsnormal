package gpps.service.thirdpay;

public class ResultCodeException extends Exception{
	private String resultCode;
	public ResultCodeException()
	{
		super();
	}
	public ResultCodeException(String message)
	{
		super(message);
	}
	public ResultCodeException(String resultCode,String message)
	{
		super(message);
		this.resultCode=resultCode;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	
}
