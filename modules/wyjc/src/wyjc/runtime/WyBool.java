package wyjc.runtime;

public class WyBool {
	private boolean value;

	private WyBool(boolean value) {
		this.value = value;
	}

	public boolean value() {
		return value;
	}
	
	public WyBool not() {
		return new WyBool(!value);
	}
	
	public WyBool and(WyBool b) {
		return new WyBool(value & b.value);
	}
	
	public WyBool or(WyBool b) {
		return new WyBool(value | b.value);
	}
	
	public boolean equals(Object o) {
		if (o instanceof WyBool) {
			WyBool b = (WyBool) o;
			return value == b.value;
		}
		return false;
	}

	public int hashCode() {
		return value ? 0 : 1;
	}
	
	public String toString() {
		return value ? "true" : "false";
	}

	public static WyBool valueOf(boolean b) {
		if (b) {
			return TRUE;
		} else {
			return FALSE;
		}
	}

	public final static WyBool TRUE = new WyBool(true);
	public final static WyBool FALSE = new WyBool(false);
}
