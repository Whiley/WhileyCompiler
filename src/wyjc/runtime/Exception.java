package wyjc.runtime;

public class Exception extends RuntimeException {
	private final Any value;
	public Exception(Any v) {		
		this.value = v;
	}
	
	public String toString() {
		return value.toString();
	}
}
