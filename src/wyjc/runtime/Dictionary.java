package wyjc.runtime;

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
		ArrayList<Comparable> ss = new ArrayList(this.keySet());
		Collections.sort(ss);

		for(Object key : ss) {
			if(!firstTime) {
				r = r + ", ";
			}
			firstTime=false;
			Object val = get(key);			
			r = r + Util.str(key) + "->" + val;
		}
		return r + "}";
	} 
	
	public Iterator iterator() {
		return new Iterator() {
			public boolean hasNext() {
				return false;
			}
			public Object next() {
				return null;
			}
			public void remove() {
				
			}
		};
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
	
	public static int size(Dictionary dict) {
		return dict.size();
	}
}
