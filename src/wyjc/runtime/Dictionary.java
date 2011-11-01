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
	int refCount = 1; 

	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
	
	public Dictionary() {
		
	}
	
	Dictionary(Dictionary dict) {
		super(dict);
		for(Map.Entry e : dict.entrySet()) {
			Util.incRefs(e.getKey());
			Util.incRefs(e.getValue());
		}
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
		Object item = dict.get(key);
		Util.decRefs(key);
		Util.incRefs(item);
		return item;
	}
	
	public static Dictionary put(Dictionary dict, Object key, Object value) {
		if(dict.refCount > 1) {
			Util.ndict_clones++;
			Util.ndict_clones_nelems += dict.size();
			Util.decRefs(dict);
			dict = new Dictionary(dict);			
		} else {
			Util.ndict_strong_updates++;
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
		Util.decRefs(dict);
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
