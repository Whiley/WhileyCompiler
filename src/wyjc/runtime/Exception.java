package wyjc.runtime;

public final class Exception extends RuntimeException {
	public final Object value;
	
	public Exception(Object v) {		
		this.value = v;
	}
	
	public String toString() {
		return value.toString();
	}
}
