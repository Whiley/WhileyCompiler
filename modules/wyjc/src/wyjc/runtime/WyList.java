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

public final class WyList extends java.util.ArrayList {

	// ================================================================================
	// Generic Operations
	// ================================================================================

	public WyList() {
		super();
	}

	public WyList(int size) {
		super(size);
	}

	WyList(java.util.Collection items) {
		super(items);
		for(Object o : items) {
			Util.incRefs(o);
		}
	}

	public String toString() {
		String r = "[";
		boolean firstTime=true;
		for(Object o : this) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += whiley.lang.Any$native.toString(o);
		}
		return r + "]";
	}

	// ================================================================================
	// List Operations
	// ================================================================================

	public static Object get(WyList list, BigInteger index) {
		Object item = list.get(index.intValue());
		return Util.incRefs(item);
	}

	public static WyList set(WyList list, final BigInteger index, final Object value) {		
		if(list.refCount > 0) {
			// in this case, we need to clone the list in question
			list = new WyList(list);
		} 
		Object v = list.set(index.intValue(),value);
		Util.decRefs(v);
		Util.incRefs(value);
		return list;
	}

	public BigInteger length() {
		return BigInteger.valueOf(size());
	}
	
	public static WyList sublist(final WyList list, final BigInteger start, final BigInteger end) {
		int st = start.intValue();
		int en = end.intValue();

		if(list.refCount == 0) {
			if(st <= en) {
				for(int i=0;i!=st;++i) {
					Util.decRefs(list.get(i));
				}
				for(int i=en;i!=list.size();++i) {
					Util.decRefs(list.get(i));
				}
				list.removeRange(0,st);
				list.removeRange(en-st,list.size());
				return list;
			} else {
				for(int i=0;i!=en;++i) {
					Util.decRefs(list.get(i));
				}
				for(int i=st;i!=list.size();++i) {
					Util.decRefs(list.get(i));
				}
				list.removeRange(0,en);
				list.removeRange(st-en,list.size());
				Collections.reverse(list);
				return list;
			}
		} else {
			WyList r;
			if(st <= en) {
				r = new WyList(en-st);
				for (int i = st; i != en; ++i) {
					Object item = list.get(i);
					Util.incRefs(item);
					r.add(item);
				}
			} else {
				r = new WyList(st-en);
				for (int i = (st-1); i >= en; --i) {
					Object item = list.get(i);
					Util.incRefs(item);
					r.add(item);
				}
			}
			return r;
		}
	}

	public static WyList append(WyList lhs, WyList rhs) {		
		if(lhs.refCount > 0) {
			lhs = new WyList(lhs);
		}

		lhs.addAll(rhs);

		for(Object o : rhs) {
			Util.incRefs(o);
		}

		return lhs;
	}

	public static WyList append(WyList list, final Object item) {
		if(list.refCount > 0) {			
			list = new WyList(list);
		}
		list.add(item);
		Util.incRefs(item);
		return list;
	}

	public static WyList append(final Object item, WyList list) {
		if(list.refCount > 0) {
			list = new WyList(list);
		}
		list.add(0,item);
		Util.incRefs(item);
		return list;
	}

	public static int size(final WyList list) {
		return list.size();
	}

	/**
	 * Return a list constructed from the range of two integers.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static WyList range(BigInteger start, BigInteger end) {
		WyList l = new WyList();

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
	public static WyList internal_add(WyList list, final Object item) {
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
	public static Object internal_get(WyList list, BigInteger index) {
		Object item = list.get(index.intValue());
		if(list.refCount > 0) {
			Util.incRefs(item);
		}
		return item;
	}

	public static java.util.Iterator iterator(WyList list) {
		return list.iterator();
	}
}
