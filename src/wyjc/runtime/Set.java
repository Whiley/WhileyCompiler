package wyjc.runtime;

import java.math.BigInteger;
import java.util.*;


public final class Set extends java.util.HashSet {	
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
		
	public Set() {		
			
	}
	
	private Set(java.util.Collection items) {
		super(items);
		for(Object o : items) {
			Util.incRefs(o);
		}
	}	
	
	public String toString() {
		String r = "{";
		boolean firstTime=true;
		ArrayList ss = new ArrayList(this);
		Collections.sort(ss,Util.COMPARATOR);

		for(Object o : ss) {
			if(!firstTime) {
				r = r + ", ";
			}
			firstTime=false;
			r = r + whiley.lang.Any$native.toString(o);
		}
		return r + "}";
	} 

	// ================================================================================
	// Set Operations
	// ================================================================================	 	
	
	public static boolean subset(Set lhs, Set rhs) {
		Util.decRefs(lhs);
		Util.decRefs(rhs);
		return rhs.containsAll(lhs) && rhs.size() > lhs.size();
	}
	
	public static boolean subsetEq(Set lhs, Set rhs) {
		Util.decRefs(lhs);
		Util.decRefs(rhs);
		return rhs.containsAll(lhs);
	}
	
	public static Set union(Set lhs, Set rhs) {
		Util.countRefs(lhs);
		Util.countRefs(rhs);
		
		if(lhs.refCount == 1) {
			Util.nset_inplace_updates++;			
			Util.decRefs(rhs);
		} else if(rhs.refCount == 1) {
			Util.nset_inplace_updates++;			
			Util.decRefs(lhs);
			Set tmp = rhs;
			rhs = lhs;
			lhs = tmp;
		} else {
			Util.countClone(lhs);
			Util.decRefs(lhs);
			Util.decRefs(rhs);
			lhs = new Set(lhs);
		}		
		lhs.addAll(rhs);
		for(Object o : rhs) {
			Util.incRefs(o);
		}
		return lhs;
	}
	
	public static Set union(Set lhs, Object rhs) {
		Util.countRefs(lhs);
		
		if(lhs.refCount == 1) {
			Util.nset_inplace_updates++;						
		} else {
			Util.countClone(lhs);
			Util.decRefs(lhs);
			lhs = new Set(lhs);			
		}
		lhs.add(rhs);
		Util.incRefs(rhs);
		return lhs;
	}
	
	public static Set union(Object lhs, Set rhs) {
		Util.countRefs(rhs);
		
		if(rhs.refCount == 1) {
			Util.nset_inplace_updates++;						
		} else {
			Util.countClone(rhs);
			Util.decRefs(rhs);
			rhs = new Set(rhs);			
		}		
		rhs.add(lhs);
		Util.incRefs(lhs);
		return rhs;
	}
	
	public static Set difference(Set lhs, Set rhs) {
		Util.countRefs(lhs);
		Util.countRefs(rhs);
		
		if(lhs.refCount == 1) {
			Util.nset_inplace_updates++;			
			Util.decRefs(rhs);
		} else {
			Util.countClone(lhs);
			Util.decRefs(lhs);
			Util.decRefs(rhs);
			lhs = new Set(lhs);
		}			
		lhs.removeAll(rhs);
		for(Object o : rhs) {
			Util.decRefs(o); // because of constructor increment	
		}
		return lhs;
	}
	
	public static Set difference(Set lhs, Object rhs) {
		Util.countRefs(lhs);
		if(lhs.refCount == 1) {
			Util.nset_inplace_updates++;			
			Util.decRefs(rhs);
		} else {
			Util.countClone(lhs);
			Util.decRefs(lhs);
			Util.decRefs(rhs);
			lhs = new Set(lhs);
		}	
		lhs.remove(rhs);
		Util.decRefs(rhs); // because of constructor increment		
		return lhs;
	}	
	
	public static Set intersect(Set lhs, Set rhs) {
		Util.countRefs(lhs);
		Util.countRefs(rhs);
		if(lhs.refCount == 1) {
			Util.nset_inplace_updates++;			
			Util.decRefs(rhs);
		} else if(rhs.refCount == 1) {
			Util.nset_inplace_updates++;			
			Util.decRefs(lhs);
			Set tmp = rhs;
			rhs = lhs;
			lhs = tmp;
		} else {
			Util.countClone(lhs);
			Util.decRefs(lhs);
			Util.decRefs(rhs);
			lhs = new Set(lhs);
		}	
		lhs.retainAll(rhs);
		for(Object o : rhs) {
			if(!lhs.contains(o)) {
				Util.decRefs(o);				
			}
		}
		return lhs;
	}
	
	public static Set intersect(Set lhs, Object rhs) {
		Util.countRefs(lhs);
		
		if(lhs.refCount == 1) {
			Util.nset_inplace_updates++;			
			Util.decRefs(rhs);
		} else {
			Util.countClone(lhs);
			Util.decRefs(lhs);
			Util.decRefs(rhs);
			lhs = new Set(lhs);
		}
		
		for(Object o : lhs) {
			Util.decRefs(o);
		}
		
		lhs.clear();
		
		if(lhs.contains(rhs)) {			
			Util.incRefs(rhs);
			lhs.add(rhs);
		} 
				
		return lhs;
	}
	
	public static Set intersect(Object lhs, Set rhs) {		
		Util.countRefs(rhs);
		
		if(rhs.refCount == 1) {
			Util.nset_inplace_updates++;			
			Util.decRefs(lhs);
		} else {
			Util.countClone(rhs);
			Util.decRefs(rhs);
			Util.decRefs(lhs);
			rhs = new Set(rhs);
		}
		
		for(Object o : rhs) {
			Util.decRefs(o);
		}
		
		rhs.clear();
		
		if(rhs.contains(lhs)) {			
			Util.incRefs(lhs);
			rhs.add(lhs);
		} 
				
		return rhs;
	}	
	
	public static BigInteger length(Set set) {
		Util.decRefs(set);
		return BigInteger.valueOf(set.size());
	}
	
	/**
	 * This method is not intended for public consumption. It is used internally
	 * by the compiler during object construction only.
	 * 
	 * @param list
	 * @param item
	 * @return
	 */
	public static Set internal_add(Set lhs, Object rhs) {		
		lhs.add(rhs);
		return lhs;
	}
}
