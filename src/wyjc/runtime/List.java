package wyjc.runtime;

import java.math.BigInteger;
import java.util.Collections;


public final class List extends java.util.ArrayList {		
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
		Util.decRefs(list);
		Object item = list.get(index.intValue());
		return Util.incRefs(item);		
	}
			
	public static List set(List list, final BigInteger index, final Object value) {
		Util.countRefs(list);
		if(list.refCount > 1) {			
			Util.countClone(list);			
			// in this case, we need to clone the list in question
			Util.decRefs(list);			
			list = new List(list);						
		} else {
			Util.nlist_inplace_updates++;
		}
		Object v = list.set(index.intValue(),value);
		Util.decRefs(v);
		Util.incRefs(value);
		return list;
	}
	
	public static List sublist(final List list, final BigInteger start, final BigInteger end) {
		Util.countRefs(list);
		int st = start.intValue();
		int en = end.intValue();	
		
		if(list.refCount == 1) {
			Util.nlist_inplace_updates++;
			if(st <= en) {
				for(int i=0;i!=st;++i) {
					Util.decRefs(list.get(i));
				}
				for(int i=en;i!=list.size();++i) {
					Util.decRefs(list.get(i));
				}
				list.removeRange(0,st);
				list.removeRange(en-st,list.size());
				return list;
			} else {
				for(int i=0;i!=en;++i) {
					Util.decRefs(list.get(i));
				}
				for(int i=st;i!=list.size();++i) {
					Util.decRefs(list.get(i));
				}
				list.removeRange(0,en);
				list.removeRange(st-en,list.size());
				Collections.reverse(list);
				return list;
			}
		} else {		
			Util.countClone(list);
			Util.decRefs(list);	
			List r;		
			if(st <= en) {
				r = new List(en-st);
				for (int i = st; i != en; ++i) {
					Object item = list.get(i);
					Util.incRefs(item);
					r.add(item);
				}	
			} else {
				r = new List(st-en);
				for (int i = (st-1); i >= en; --i) {
					Object item = list.get(i);
					Util.incRefs(item);
					r.add(item);					
				}
			}					
			return r;	
		}							
	}
	
	public static BigInteger length(List list) {				
		Util.decRefs(list);
		return BigInteger.valueOf(list.size());
	}
	
	public static List append(List lhs, List rhs) {
		Util.countRefs(lhs);
		Util.countRefs(rhs);
		if(lhs.refCount == 1) {
			Util.nlist_inplace_updates++;			
			Util.decRefs(rhs);
		} else {
			Util.countClone(lhs);
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
		Util.countRefs(list);
		if(list.refCount == 1) {
			Util.nlist_inplace_updates++;						
		} else { 
			Util.countClone(list);
			Util.decRefs(list); 		
			list = new List(list);
		}
		list.add(item);
		Util.incRefs(item);
		return list;
	}
	
	public static List append(final Object item, List list) {
		Util.countRefs(list);
		if(list.refCount == 1) {
			Util.nlist_inplace_updates++;						
		} else { 
			Util.countClone(list);
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
	
	/**
	 * This method is not intended for public consumption. It is used internally
	 * by the compiler during object construction only.
	 * 
	 * @param list
	 * @param item
	 * @return
	 */
	public static List internal_add(List list, final Object item) {			
		list.add(item);
		return list;
	}
	
	/**
	 * This method is not intended for public consumption. It is used internally
	 * by the compiler during imperative updates only.
	 * 
	 * @param list
	 * @param item
	 * @return
	 */
	public static Object internal_get(List list, BigInteger index) {		
		Object item = list.get(index.intValue());
		if(list.refCount > 1) {
			Util.incRefs(item);			
		} 
		return item;
	}
	
	public static java.util.Iterator iterator(List list) {
		return list.iterator();
	}		
}
