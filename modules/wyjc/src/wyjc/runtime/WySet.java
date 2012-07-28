// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyjc.runtime;

import java.math.BigInteger;
import java.util.*;


public final class WySet extends java.util.HashSet {	
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	int refCount = 100;  // temporary measure 

	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
		
	public WySet() {		
			
	}
	
	private WySet(java.util.Collection items) {
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
	
	public static boolean subset(WySet lhs, WySet rhs) {
		return rhs.containsAll(lhs) && rhs.size() > lhs.size();
	}
	
	public static boolean subsetEq(WySet lhs, WySet rhs) {
		return rhs.containsAll(lhs);
	}
	
	public static WySet union(WySet lhs, WySet rhs) {
		Util.countRefs(lhs);
		Util.countRefs(rhs);
		
		if(lhs.refCount == 0) {
			Util.nset_inplace_updates++;						
		} else if(rhs.refCount == 0) {
			Util.nset_inplace_updates++;						
			WySet tmp = rhs;
			rhs = lhs;
			lhs = tmp;
		} else {
			Util.countClone(lhs);
			lhs = new WySet(lhs);
		}		
		lhs.addAll(rhs);
		for(Object o : rhs) {
			Util.incRefs(o);
		}
		return lhs;
	}
	
	public static WySet union(WySet lhs, Object rhs) {
		Util.countRefs(lhs);
		
		if(lhs.refCount == 0) {
			Util.nset_inplace_updates++;						
		} else {
			Util.countClone(lhs);			
			lhs = new WySet(lhs);			
		}
		lhs.add(rhs);
		Util.incRefs(rhs);
		return lhs;
	}
	
	public static WySet union(Object lhs, WySet rhs) {
		Util.countRefs(rhs);
		
		if(rhs.refCount == 0) {
			Util.nset_inplace_updates++;						
		} else {
			Util.countClone(rhs);			
			rhs = new WySet(rhs);			
		}		
		rhs.add(lhs);
		Util.incRefs(lhs);
		return rhs;
	}
	
	public static WySet difference(WySet lhs, WySet rhs) {
		Util.countRefs(lhs);
		Util.countRefs(rhs);
		
		if(lhs.refCount == 0) {
			Util.nset_inplace_updates++;						
		} else {
			Util.countClone(lhs);
			lhs = new WySet(lhs);
		}			
		lhs.removeAll(rhs);
		for(Object o : rhs) {
			Util.decRefs(o); // because of constructor increment	
		}
		return lhs;
	}
	
	public static WySet difference(WySet lhs, Object rhs) {
		Util.countRefs(lhs);
		if(lhs.refCount == 0) {
			Util.nset_inplace_updates++;						
		} else {
			Util.countClone(lhs);
			lhs = new WySet(lhs);
		}	
		lhs.remove(rhs);
		Util.decRefs(rhs); // because of constructor increment		
		return lhs;
	}	
	
	public static WySet intersect(WySet lhs, WySet rhs) {
		Util.countRefs(lhs);
		Util.countRefs(rhs);
		if(lhs.refCount == 0) {
			Util.nset_inplace_updates++;						
		} else if(rhs.refCount == 0) {
			Util.nset_inplace_updates++;			
			WySet tmp = rhs;
			rhs = lhs;
			lhs = tmp;
		} else {
			Util.countClone(lhs);
			lhs = new WySet(lhs);
		}	
		lhs.retainAll(rhs);
		for(Object o : rhs) {
			if(!lhs.contains(o)) {
				Util.decRefs(o);				
			}
		}
		return lhs;
	}
	
	public static WySet intersect(WySet lhs, Object rhs) {
		Util.countRefs(lhs);
		
		if(lhs.refCount == 0) {
			Util.nset_inplace_updates++;						
		} else {
			Util.countClone(lhs);
			lhs = new WySet(lhs);
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
	
	public static WySet intersect(Object lhs, WySet rhs) {		
		Util.countRefs(rhs);
		
		if(rhs.refCount == 0) {
			Util.nset_inplace_updates++;						
		} else {
			Util.countClone(rhs);
			rhs = new WySet(rhs);
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
	
	public static BigInteger length(WySet set) {		
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
	public static WySet internal_add(WySet lhs, Object rhs) {		
		lhs.add(rhs);
		return lhs;
	}
}
