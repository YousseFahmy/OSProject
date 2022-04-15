public class Variable<T> {
	private String identifier;
	private T value;
	
	public Variable(String identifier, T data){
		this.identifier = identifier;
		this.value = data;
	}
	
	public T getValue() {
		return this.value;
	}
	
	public void setValue(T newValue) {
		this.value = newValue;
	}
	
	public String getIdentifier() {
		return this.identifier;
	}
}
