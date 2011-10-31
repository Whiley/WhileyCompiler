package wyjc.runtime;

import java.math.BigInteger;


public final class List extends java.util.ArrayList {		
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	int refCount = 1; // TODO: implement proper reference counting
	
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
		for(Object o : items) {
			Util.incRefs(o);
		}
	}
	
	public String toString() {
		String r = "[";
		boolean firstTime=true;
		for(Object o : this) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += whiley.lang.Any$native.toString(o);
		}
		return r + "]";
	}
	
	// ================================================================================
	// List Operations
	// ================================================================================	 
		
	public static Object get(List list, BigInteger index) {		
		Object item = list.get(index.intValue());
		return Util.incRefs(item);		
	}
			
	public static List set(List list, final BigInteger index, final Object value) {
		if(list.refCount > 1) {			
			Util.nlist_clones++;
			Util.nlist_clones_nelems += list.size();
			// in this case, we need to clone the list in question
			Util.decRefs(list);			
			list = new List(list);						
		} else {
			Util.nlist_strong_updates++;
		}
		Object v = list.set(index.intValue(),value);
		Util.decRefs(v);
		Util.incRefs(value);
		return list;
	}
	
	public static List sublist(final List list, final BigInteger start, final BigInteger end) {
		Util.decRefs(list);		
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
		Util.decRefs(list);
		return BigInteger.valueOf(list.size());
	}
	
	public static List append(List lhs, List rhs) {			
		if(lhs.refCount == 1) {
			Util.nlist_strong_updates++;			
			Util.decRefs(rhs);
		} else if(rhs.refCount == 1) {
			Util.nlist_strong_updates++;			
			Util.decRefs(lhs);
			List tmp = rhs;
			rhs = lhs;
			lhs = tmp;			
		} else {
			Util.nlist_clones++;
			Util.nlist_clones_nelems += lhs.size();
			Util.decRefs(lhs);
			Util.decRefs(rhs);
			lhs = new List(lhs);				
		} 
		
		lhs.addAll(rhs);
		
		for(Object o : rhs) {
			Util.incRefs(o);
		}
		
		return lhs;
	}
	
	public static List append(List list, final Object item) {	
		if(list.refCount == 1) {
			Util.nlist_strong_updates++;						
		} else { 
			Util.decRefs(list); 		
			list = new List(list);
		}
		list.add(item);
		Util.incRefs(item);
		return list;
	}
	
	public static List append(final Object item, List list) {	
		if(list.refCount == 1) {
			Util.nlist_strong_updates++;						
		} else { 
			Util.decRefs(list);			 	
			list = new List(list);
		}
		list.add(0,item);
		Util.incRefs(item);
		return list;
	}
	
	public static int size(final List list) {		
		return list.size();
	}
	
	public static java.util.Iterator iterator(List list) {
		return list.iterator();
	}		
}
