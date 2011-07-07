package wyjc.runtime;

import java.math.BigInteger;


public final class Tuple extends java.util.ArrayList {		
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
	
	public Tuple() {
		super();				
	}
	
	public Tuple(int size) {
		super(size);			
	}
	
	Tuple(java.util.Collection items) {
		super(items);			
	}
	
	public boolean add(Object o) {		
		return super.add(o);
	}
		
	public String toString() {
		String r = "(";
		boolean firstTime=true;
		for(Object o : this) {
			if(!firstTime) {
				r += ",";
			}
			firstTime=false;
			r += Util.str(o);
		}
		return r + ")";
	}
	
	// ================================================================================
	// List Operations
	// ================================================================================	 
		
	public static Object get(Tuple list, BigInteger index) {		
		return list.get(index.intValue());
	}
			
	public static Tuple set(Tuple list, final BigInteger index, final Object value) {
		if(list.refCount > 1) {			
			// in this case, we need to clone the list in question
			list.refCount--;
			Tuple nlist = new Tuple(list);
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
	
	public static Tuple sublist(final Tuple list, final BigInteger start, final BigInteger end) {
		int st = start.intValue();
		int en = end.intValue();
		Tuple r = new Tuple(en-st);
		for (int i = st; i != en; ++i) {
			r.add(list.get(i));
		}
		return r;		
	}
	
	public static BigInteger length(Tuple list) {
		return BigInteger.valueOf(list.size());
	}
	
	public static Tuple append(final Tuple lhs, final Tuple rhs) {		
		Tuple r = new Tuple(lhs);
		r.addAll(rhs);
		return r;
	}
	
	public static Tuple append(final Tuple list, final Object item) {
		Tuple r = new Tuple(list);
		r.add(item);
		return r;
	}
	
	public static Tuple append(final Object item, final Tuple list) {
		Tuple r = new Tuple(list);
		r.add(0,item);
		return r;
	}
	
	public static int size(final Tuple list) {
		return list.size();
	}
	
	public static java.util.Iterator iterator(Tuple list) {
		return list.iterator();
	}		
}
