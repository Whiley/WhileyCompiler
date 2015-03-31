package wyjc.runtime;

import java.math.BigInteger;
import java.util.Map;

public class WyCollection {

	public static java.util.Iterator iterator(Object col) {
		if(col instanceof java.util.Collection) {
			java.util.Collection c = (java.util.Collection) col;
			return c.iterator();
		} else {
			WyMap m = (WyMap) col;
			return m.iterator();
		} 
	}

	public static BigInteger length(Object col) {
		if(col instanceof java.util.Collection) {
			java.util.Collection c = (java.util.Collection) col;
			return BigInteger.valueOf(c.size());
		} else {
			java.util.Map m = (java.util.Map) col;
			return BigInteger.valueOf(m.size());
		} 
	}

	public static Object indexOf(Object col, Object key) {
		if(col instanceof WyList) {
			WyList l = (WyList) col;
			BigInteger index = (BigInteger) key;
			return l.get(index.intValue());
		} else  {
			WyMap d = (WyMap) col;
			return d.get(key);
		} 
	}
}
