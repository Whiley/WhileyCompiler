package wyil.lang;

import java.math.BigInteger;
import java.util.*;
import wyil.jvm.rt.BigRational;

public abstract class Value implements RVal {	
	public abstract Type type();
	
	public static Bool V_BOOL(boolean value) {
		return get(new Bool(value));
	}
	
	public static Int V_INT(BigInteger value) {
		return get(new Int(value));
	}

	public static Real V_REAL(BigRational value) {
		return get(new Real(value));
	}	

	public static Set V_SET(Collection<Value> values) {
		return get(new Set(values));
	}

	public static List V_LIST(Collection<Value> values) {
		return get(new List(values));
	}

	public static final class Bool extends Value {
		public final boolean value;
		private Bool(boolean value) {
			this.value = value;
		}
		public Type type() {
			return Type.T_BOOL;
		}
		public int hashCode() {
			return value ? 1 : 0;
		}
		public boolean equals(Object o) {
			if(o instanceof Bool) {
				Bool i = (Bool) o;
				return value == i.value;
			}
			return false;
		}
		public String toString() {
			if(value) { return "true"; }
			else {
				return "false";
			}
		}
	}
	public static final class Int extends Value {
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
		public String toString() {
			return value.toString();
		}
	}
	
	public static final class Real extends Value {
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
		public String toString() {
			return value.toString();
		}
	}
	
	public static class List extends Value {
		public final ArrayList<Value> values;
		private List(Collection<Value> value) {
			this.values = new ArrayList<Value>(value);
		}
		public Type type() {
			if(values.isEmpty()) {
				return Type.T_LIST(Type.T_VOID);
			} else {
				// FIXME: need to use lub here
				return Type.T_LIST(values.get(0).type());
			}
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof List) {
				List i = (List) o;
				return values.equals(i.values);
			}
			return false;
		}
		public String toString() {
			String r = "[";
			boolean firstTime=true;
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "]";
		}
	}
	
	public static class Set extends Value {
		public final HashSet<Value> values;
		private Set(Collection<Value> value) {
			this.values = new HashSet<Value>(value);
		}
		public Type type() {
			if(values.isEmpty()) {
				return Type.T_SET(Type.T_VOID);
			} else {
				// FIXME: need to use lub here
				return Type.T_SET(values.iterator().next().type());
			}
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Set) {
				Set i = (Set) o;
				return values.equals(i.values);
			}
			return false;
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "}";
		}
	}
	
	private static final ArrayList<Value> values = new ArrayList<Value>();
	private static final HashMap<Value,Integer> cache = new HashMap<Value,Integer>();
	
	private static <T extends Value> T get(T type) {
		Integer idx = cache.get(type);
		if(idx != null) {
			return (T) values.get(idx);
		} else {					
			cache.put(type, values.size());
			values.add(type);
			return type;
		}
	}
}
