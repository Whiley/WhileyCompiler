package wyil.lang;

import java.math.BigInteger;
import java.util.*;
import wyil.jvm.rt.BigRational;

public abstract class Value {	
	public abstract Type type();
	
	public static Int V_INT(BigInteger value) {
		return get(new Int(value));
	}
	
	public static class Int extends Value {
		public final BigInteger value;
		private Int(BigInteger value) {
			this.value = value;
		}
		public Type type() {
			return Type.T_INT;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Int) {
				Int i = (Int) o;
				return value.equals(i.value);
			}
			return false;
		}
	}
	
	public static class Real extends Value {
		public final BigRational value;
		private Real(BigRational value) {
			this.value = value;
		}
		public Type type() {
			return Type.T_REAL;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Int) {
				Int i = (Int) o;
				return value.equals(i.value);
			}
			return false;
		}
	}
	
	private static final ArrayList<Value> values = new ArrayList<Value>();
	private static final HashMap<Value,Integer> cache = new HashMap<Value,Integer>();
	
	private static <T extends Value> T get(T type) {
		Integer idx = cache.get(type);
		if(idx != 0) {
			return (T) values.get(idx);
		} else {
			values.add(type);			
			cache.put(type, values.size());
			return type;
		}
	}
}
