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


public final class WyTuple extends java.util.ArrayList {
	
	// ================================================================================
	// Generic Operations
	// ================================================================================

	public WyTuple() {
		super();
	}

	public WyTuple(int size) {
		super(size);
	}

	WyTuple(java.util.Collection items) {
		super(items);		
	}

	WyTuple(Object... items) {
		super();
		for(Object o : items) {
			add(o);
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
			r += whiley.lang.Any$native.toRealString(o);
		}
		return r + ")";
	}

	// ================================================================================
	// List Operations
	// ================================================================================

	public static Object get(WyTuple tuple, int index) {
		return tuple.get(index);		
	}

	public static BigInteger length(WyTuple tuple) {
		return BigInteger.valueOf(tuple.size());
	}

	public static int size(final WyTuple list) {
		return list.size();
	}

	public static java.util.Iterator iterator(WyTuple list) {
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
	public static WyTuple internal_add(WyTuple lhs, Object rhs) {
		lhs.add(rhs);
		return lhs;
	}
}
