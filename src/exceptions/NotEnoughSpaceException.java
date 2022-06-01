package exceptions;

public class NotEnoughSpaceException extends OSException{
    public NotEnoughSpaceException(){
		super();
	}
	
	public NotEnoughSpaceException(String message) {
		super(message);
	}
}
