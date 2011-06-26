package wyjc.runtime;

public final class Exception extends RuntimeException {
	private final Object value;
	public Exception(Object v) {		
		this.value = v;
	}
	
	public String toString() {
		return value.toString();
	}
}
