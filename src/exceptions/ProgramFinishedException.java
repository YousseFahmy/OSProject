package exceptions;

public class ProgramFinishedException extends OSException {
	
	public ProgramFinishedException(){
		super();
	}
	
	public ProgramFinishedException(String message) {
		super(message);
	}
}
