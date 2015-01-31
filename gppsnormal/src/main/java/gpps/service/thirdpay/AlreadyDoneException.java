package gpps.service.thirdpay;

public class AlreadyDoneException extends Exception {
	public AlreadyDoneException(){
		super();
	}
	public AlreadyDoneException(String message){
		super(message);
	}
}
