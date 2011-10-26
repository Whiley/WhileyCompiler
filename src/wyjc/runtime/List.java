package wyjc.runtime;

import java.math.BigInteger;


public final class List extends java.util.ArrayList {		
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	int refCount = 100; // TODO: implement proper reference counting
	
	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
	
	public List() {
		super();				
	}
	
	public List(int size) {
		super(size);			
	}
	
	List(java.util.Collection items) {
		super(items);			
	}
	
	public String toString() {
		String r = "[";
		boolean firstTime=true;
		for(Object o : this) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += whiley.lang.String$native.str(o);
		}
		return r + "]";
	}
	
	// ================================================================================
	// List Operations
	// ================================================================================	 
		
	public static Object get(List list, BigInteger index) {		
		return list.get(index.intValue());
	}
			
	public static List set(List list, final BigInteger index, final Object value) {
		if(list.refCount > 1) {			
			// in this case, we need to clone the list in question
			list.refCount--;
			List nlist = new List(list);
			for(Object item : nlist) {
				Util.incRefs(item);
			}
			list = nlist;
		}
		Object v = list.set(index.intValue(),value);
		Util.decRefs(v);
		Util.incRefs(value);
		return list;
	}
	
	public static List sublist(final List list, final BigInteger start, final BigInteger end) {
		int st = start.intValue();
		int en = end.intValue();	
		List r;		
		if(st <= en) {
			r = new List(en-st);
			for (int i = st; i != en; ++i) {
				r.add(list.get(i));
			}	
		} else {
			r = new List(st-en);
			for (int i = st; i != en; --i) {
				r.add(list.get(i-1));
			}
		}		
		return r;		
	}
	
	public static BigInteger length(List list) {
		return BigInteger.valueOf(list.size());
	}
	
	public static List append(final List lhs, final List rhs) {		
		List r = new List(lhs);
		r.addAll(rhs);
		return r;
	}
	
	public static List append(final List list, final Object item) {
		List r = new List(list);
		r.add(item);
		return r;
	}
	
	public static List append(final Object item, final List list) {
		List r = new List(list);
		r.add(0,item);
		return r;
	}
	
	public static int size(final List list) {
		return list.size();
	}
	
	public static java.util.Iterator iterator(List list) {
		return list.iterator();
	}		
}
