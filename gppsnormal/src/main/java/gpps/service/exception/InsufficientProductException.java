package gpps.service.exception;

public class InsufficientProductException extends Exception{
	public InsufficientProductException()
	{
		super();
	}
	public InsufficientProductException(String msg)
	{
		super(msg);
	}
}
