package wyjc.runtime;

import java.math.BigInteger;


public final class Tuple extends java.util.ArrayList {		
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	int refCount = 1;
	
	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
	
	public Tuple() {
		super();				
	}
	
	public Tuple(int size) {
		super(size);			
	}
	
	Tuple(java.util.Collection items) {
		super(items);			
		for(Object item : items) {
			Util.incRefs(item);
		}
	}
	
	Tuple(Object... items) {
		super();
		for(Object item : items) {
			add(item);
			Util.incRefs(item);
		}
	}	
		
	public String toString() {
		String r = "(";
		boolean firstTime=true;
		for(Object o : this) {
			if(!firstTime) {
				r += ",";
			}
			firstTime=false;
			r += whiley.lang.Any$native.toString(o);
		}
		return r + ")";
	}
	
	// ================================================================================
	// List Operations
	// ================================================================================	 
	
	public static Object get(Tuple list, BigInteger index) {
		Util.decRefs(list);
		Object item = list.get(index.intValue());
		Util.incRefs(item);
		return item;
	}
		
	public static BigInteger length(Tuple tuple) {
		Util.decRefs(tuple);
		return BigInteger.valueOf(tuple.size());
	}
	
	public static int size(final Tuple list) {
		return list.size();
	}
	
	public static java.util.Iterator iterator(Tuple list) {
		return list.iterator();
	}		
	
	/**
	 * This method is not intended for public consumption. It is used internally
	 * by the compiler during object construction only.
	 * 
	 * @param list
	 * @param item
	 * @return
	 */
	public static Tuple internal_add(Tuple lhs, Object rhs) {		
		lhs.add(rhs);
		Util.incRefs(rhs);
		return lhs;
	}
}
