package wyil.lang;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class RVal {
	
	public static Variable VAR(String v) {
		return get(new Variable(v));
	}
	
	public static class Variable extends RVal {
		public final String name;
		Variable(String name) {
			this.name = name;
		}
		public int hashCode() {
			return name.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Variable) {
				return name.equals(((Variable)o).name);
			}
			return false;
		}
		public String toString() {
			return name;
		}
	}
	
	private static final ArrayList<RVal> values = new ArrayList<RVal>();
	private static final HashMap<RVal,Integer> cache = new HashMap<RVal,Integer>();
	
	private static <T extends RVal> T get(T type) {
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
