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

public final class WyMap extends java.util.HashMap<Object,Object> implements Iterable {
	
	// ================================================================================
	// Generic Operations
	// ================================================================================

	public WyMap() {

	}

	WyMap(WyMap dict) {
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
			r = r + whiley.lang.Any$native.toRealString(key) + "=>" + val;
		}
		return r + "}";
	}

	public java.util.Iterator iterator() {
		return new Iterator(entrySet().iterator());
	}

	// ================================================================================
	// Dictionary Operations
	// ================================================================================

	public static Object get(WyMap dict, Object key) {
		Object item = dict.get(key);
		return item;
	}

	public static WyMap put(WyMap dict, Object key, Object value) {
		dict = new WyMap(dict); 
		Object val = dict.put(key, value);		
		return dict;
	}

	public static BigInteger length(WyMap dict) {
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
			return new WyTuple(e.getKey(),e.getValue());
		}
	}

	/**
	 * This method is not intended for public consumption. It is used internally
	 * by the compiler during imperative updates only.
	 *
	 * @param list
	 * @param item
	 * @return
	 */
	public static Object internal_get(WyMap dict, Object key) {
		return dict.get(key);
	}
}
