package exceptions;

public class VariableDoesNotExistException extends OSException {

	public VariableDoesNotExistException() {
		super();
	}

	public VariableDoesNotExistException(String message) {
		super(message);
	}

}
