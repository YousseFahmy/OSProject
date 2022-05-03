package exceptions;

public class VariableDoesNotExistException extends RuntimeException {

	public VariableDoesNotExistException() {
		super();
	}

	public VariableDoesNotExistException(String message) {
		super(message);
	}

}
