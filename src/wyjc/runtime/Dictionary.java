package wyjc.runtime;

import java.math.BigInteger;
import java.util.*;


public final class Dictionary extends java.util.HashMap<Object,Object> {	
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
	
	public Dictionary() {
		
	}
	
	Dictionary(Dictionary dict) {
		super(dict);
	}
	
	public String toString() {
		String r = "{";
		boolean firstTime=true;
		ArrayList ss = new ArrayList(this.keySet());
		Collections.sort(ss,Util.COMPARATOR);

		for(Object key : ss) {
			if(!firstTime) {
				r = r + ", ";
			}
			firstTime=false;
			Object val = get(key);			
			r = r + whiley.lang.Any$native.toString(key) + "->" + val;
		}
		return r + "}";
	} 
	
	public java.util.Iterator iterator() {
		return new Iterator(entrySet().iterator());		
	}
	
	// ================================================================================
	// Dictionary Operations
	// ================================================================================	 	

	public static Object get(Dictionary dict, Object key) {
		return dict.get(key);
	}
	
	public static Dictionary put(Dictionary dict, Object key, Object value) {
		if(dict.refCount > 1) {
			Dictionary ndict = new Dictionary(dict);
			HashMap<Object,Object> tmp = ndict;
			for(Map.Entry e : tmp.entrySet()) {
				Util.incRefs(e.getKey());
				Util.incRefs(e.getValue());
			}
			dict = ndict;
		}
		Object val = dict.put(key, value);
		if(val != null) {
			Util.decRefs(val);			
		} else {
			Util.incRefs(key);
		}
		Util.incRefs(value);		
		return dict;
	}
	
	public static BigInteger length(Dictionary dict) {
		return BigInteger.valueOf(dict.size());
	}
	
	public static final class Iterator implements java.util.Iterator {
		public java.util.Iterator<Map.Entry> iter;
		
		public Iterator(java.util.Iterator iter) {
			this.iter = iter;
		}
		
		public boolean hasNext() {
			return iter.hasNext();
		}
		
		public void remove(){
			iter.remove();
		}
		
		public Object next() {
			Map.Entry e = iter.next();
			return new Tuple(e.getKey(),e.getValue());
		}
	}
}
