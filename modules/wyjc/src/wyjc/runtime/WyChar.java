package wyjc.runtime;

import java.math.BigInteger;

public class WyChar implements Comparable<WyChar> {
	private char value;

	private WyChar(char value) {
		this.value = value;
	}

	public WyChar add(WyChar v) {
		return valueOf((char) (value + v.value));
	}
	
	public WyChar subtract(WyChar v) {
		return valueOf((char) (value - v.value));
	}
	
	public WyChar multiply(WyChar v) {
		return valueOf((char) (value * v.value));
	}
	
	public WyChar divide(WyChar v) {
		return valueOf((char) (value / v.value));		
	}
	
	public WyChar remainder(WyChar v) {
		return valueOf((char) (value % v.value));
	}
	
	@Override
	public int compareTo(WyChar o) {
		if(value < o.value) { 
			return -1;
		} else if(value > o.value) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof WyChar) {
			WyChar b = (WyChar) o;
			return value == b.value;
		}
		return false;
	}

	public int hashCode() {
		return value;
	}
	
	public char value() {
		return value;
	}
	
	public String toString() {
		return "'" + Character.toString(value) + "'";
	}

	public static WyChar valueOf(char val) {
		return new WyChar(val);
	}
	
	public static WyChar valueOf(BigInteger val) {
		return new WyChar((char) val.intValue());
	}
}
