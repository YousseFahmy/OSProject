package exceptions;

public class ProgramFinishedException extends RuntimeException {
	
	public ProgramFinishedException(){
		super();
	}
	
	public ProgramFinishedException(String message) {
		super(message);
	}
}
