package wyjc.runtime;

import java.util.ArrayList;
import java.util.Collections;


public final class Dictionary extends java.util.HashMap {	
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
	
	public String toString() {
		String r = "{";
		boolean firstTime=true;
		ArrayList<Comparable> ss = new ArrayList<Comparable>(this.keySet());
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
	
	// ================================================================================
	// Dictionary Operations
	// ================================================================================	 	

	public static Object get(Dictionary dict, Object key) {
		return dict.get(key);
	}
	
	public static Dictionary put(Dictionary dict, Object key, Object value) {
		dict.put(key,value);
		return dict;
	}
	
	public static int size(Dictionary dict) {
		return dict.size();
	}
}
