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
	
	// ================================================================================
	// Generic Operations
	// ================================================================================

	public WySet() {

	}

	private WySet(java.util.Collection items) {
		super(items);		
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
			r = r + whiley.lang.Any$native.toRealString(o);
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
		lhs = new WySet(lhs);
		lhs.addAll(rhs);		
		return lhs;
	}

	public static WySet union(WySet lhs, Object rhs) {
		lhs = new WySet(lhs);
		lhs.add(rhs);
		return lhs;
	}

	public static WySet union(Object lhs, WySet rhs) {
		rhs = new WySet(rhs);
		rhs.add(lhs);
		return rhs;
	}

	public static WySet difference(WySet lhs, WySet rhs) {
		lhs = new WySet(lhs);
		lhs.removeAll(rhs);		
		return lhs;
	}

	public static WySet difference(WySet lhs, Object rhs) {
		lhs = new WySet(lhs);
		lhs.remove(rhs);
		return lhs;
	}

	public static WySet intersect(WySet lhs, WySet rhs) {
		lhs = new WySet(lhs);
		lhs.retainAll(rhs);
		return lhs;
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
