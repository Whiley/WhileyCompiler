package wyil.jvm.rt;

public class Exception extends RuntimeException {
	private final Object value;
	public Exception(Object v) {
		super("Uncaught Exception");
		this.value = v;
	}
}
