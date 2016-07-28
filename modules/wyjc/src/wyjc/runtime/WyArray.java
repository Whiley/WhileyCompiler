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
import java.util.Collections;
import java.util.Map;

public final class WyArray extends java.util.ArrayList {

	// ================================================================================
	// Generic Operations
	// ================================================================================

	public WyArray() {
		super();
	}

	public WyArray(int size) {
		super(size);
	}

	WyArray(java.util.Collection items) {
		super(items);		
	}

	public String toString() {
		String r = "[";
		boolean firstTime=true;
		for(Object o : this) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += whiley.lang.Any$native.toRealString(o);
		}
		return r + "]";
	}

	// ================================================================================
	// List Operations
	// ================================================================================

	public static Object get(WyArray list, BigInteger index) {
		return list.get(index.intValue());
	}

	public static WyArray set(WyArray list, final BigInteger index, final Object value) {		
		// Clone the list to be safe.
		list = new WyArray(list);		
		list.set(index.intValue(),value);
		return list;
	}

	public BigInteger length() {
		return BigInteger.valueOf(size());
	}

	public static WyArray append(WyArray lhs, WyArray rhs) {				
		lhs = new WyArray(lhs);
		lhs.addAll(rhs);
		return lhs;
	}

	public static WyArray append(WyArray list, final Object item) {			
		list = new WyArray(list);
		list.add(item);
		return list;
	}

	public static WyArray append(final Object item, WyArray list) {
		list = new WyArray(list);
		list.add(0,item);
		return list;
	}

	public static int size(final WyArray list) {
		return list.size();
	}

	/**
	 * Return a list constructed from the range of two integers.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static WyArray range(BigInteger start, BigInteger end) {
		WyArray l = new WyArray();

		long st = start.longValue();
		long en = start.longValue();
		if (BigInteger.valueOf(st).equals(start)
				&& BigInteger.valueOf(en).equals(end)) {
			int dir = st < en ? 1 : -1;
			while(st != en) {
				l.add(BigInteger.valueOf(st));
				st = st + dir;
			}
		} else {
			BigInteger dir;
			if(start.compareTo(end) < 0) {
				dir = BigInteger.ONE;
			} else {
				dir = BigInteger.valueOf(-1);
			}
			while(!start.equals(end)) {
				l.add(start);
				start = start.add(dir);
			}
		}

		return l;
	}
	
	public static WyArray generate(final BigInteger count, final Object element) {
		int n = count.intValue();
		WyArray list = new WyArray();
		for(int i=0;i<n;++i) {
			list.add(element);
		}
		return list;
	}
	
	// ========================================================
	// Helpers
	// ========================================================
	
	/**
	 * This method is not intended for public consumption. It is used internally
	 * by the compiler during object construction only.
	 *
	 * @param list
	 * @param item
	 * @return
	 */
	public static WyArray internal_add(WyArray list, final Object item) {
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
	public static Object internal_get(WyArray list, BigInteger index) {
		return list.get(index.intValue());		
	}

	public static java.util.Iterator iterator(WyArray list) {
		return list.iterator();
	}
}
