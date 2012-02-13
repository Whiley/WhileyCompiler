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


public final class Tuple extends java.util.ArrayList {		
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
	
	public Tuple() {
		super();				
	}
	
	public Tuple(int size) {
		super(size);			
	}
	
	Tuple(java.util.Collection items) {
		super(items);			
		for(Object item : items) {
			Util.incRefs(item);
		}
	}
	
	Tuple(Object... items) {
		super();
		for(Object item : items) {
			add(item);
			Util.incRefs(item);
		}
	}	
		
	public String toString() {
		String r = "(";
		boolean firstTime=true;
		for(Object o : this) {
			if(!firstTime) {
				r += ",";
			}
			firstTime=false;
			r += whiley.lang.Any$native.toString(o);
		}
		return r + ")";
	}
	
	// ================================================================================
	// List Operations
	// ================================================================================	 
	
	public static Object get(Tuple tuple, int index) {
		Util.decRefs(tuple);
		Object item = tuple.get(index);
		Util.incRefs(item);
		return item;
	}
		
	public static BigInteger length(Tuple tuple) {
		Util.decRefs(tuple);
		return BigInteger.valueOf(tuple.size());
	}
	
	public static int size(final Tuple list) {
		return list.size();
	}
	
	public static java.util.Iterator iterator(Tuple list) {
		return list.iterator();
	}		
	
	/**
	 * This method is not intended for public consumption. It is used internally
	 * by the compiler during object construction only.
	 * 
	 * @param list
	 * @param item
	 * @return
	 */
	public static Tuple internal_add(Tuple lhs, Object rhs) {		
		lhs.add(rhs);
		return lhs;
	}
}
