package gpps.service.exception;

public class InsufficientBalanceException extends Exception{
	public InsufficientBalanceException()
	{
		super("余额不足");
	}
	public InsufficientBalanceException(String msg)
	{
		super(msg);
	}
}
