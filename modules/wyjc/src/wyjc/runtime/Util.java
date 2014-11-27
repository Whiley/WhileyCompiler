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

import java.lang.reflect.Method;
import java.math.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static wyil.lang.Type.K_VOID;
import static wyil.lang.Type.K_ANY;
import static wyil.lang.Type.K_NULL;
import static wyil.lang.Type.K_BOOL;
import static wyil.lang.Type.K_BYTE;
import static wyil.lang.Type.K_CHAR;
import static wyil.lang.Type.K_INT;
import static wyil.lang.Type.K_RATIONAL;
import static wyil.lang.Type.K_STRING;
import static wyil.lang.Type.K_TUPLE;
import static wyil.lang.Type.K_SET;
import static wyil.lang.Type.K_LIST;
import static wyil.lang.Type.K_MAP;
import static wyil.lang.Type.K_RECORD;
import static wyil.lang.Type.K_UNION;
import static wyil.lang.Type.K_NEGATION;

public class Util {


	public static String append(final String lhs, final String rhs) {
		return lhs + rhs;
	}

	public static String append(final String lhs, final WyChar rhs) {
		return lhs + rhs.value();
	}

	public static String append(final WyChar lhs, final String rhs) {
		return lhs.value() + rhs;
	}

	public static BigInteger stringlength(final String lhs) {
		return BigInteger.valueOf(lhs.length());
	}

	public static String substring(final String lhs, final BigInteger _start, final BigInteger _end) {
		int start = _start.intValue();
		int end = _end.intValue();
		return lhs.substring(start,end);
	}

	public static String set(final String lhs, BigInteger index, WyChar c) {
		int idx = index.intValue();
		// hmmm, not exactly efficient!
		StringBuilder sb = new StringBuilder(lhs);
		sb.setCharAt(idx, c.value());
		return sb.toString();
	}
	
	/**
	 * This method is used to convert the arguments supplied to main (which have
	 * type <code>String[]</code>) into an appropriate Whiley List.
	 *
	 * @param args
	 * @return
	 */
	public static WyList fromStringList(String[] args) {
		WyList r = new WyList(args.length);
		for(int i=0;i!=args.length;++i) {
			r.add(args[i]);
		}
		return r;
	}

	/**
	 * Coerce a string into a Whiley char list.
	 * @param str
	 * @return
	 */
	public static WyList str2cl(String str) {
		WyList r = new WyList(str.length());
		for(int i=0;i!=str.length();++i) {
			r.add(WyChar.valueOf(str.charAt(i)));
		}
		return r;
	}

	/**
	 * Coerce a string into a Whiley int list.
	 * @param str
	 * @return
	 */
	public static WyList str2il(String str) {
		WyList r = new WyList(str.length());
		for(int i=0;i!=str.length();++i) {
			r.add(BigInteger.valueOf(str.charAt(i)));
		}
		return r;
	}

	/**
	 * Coerce a string into a Whiley char set.
	 * @param str
	 * @return
	 */
	public static WySet str2cs(String str) {
		WySet r = new WySet();
		for(int i=0;i!=str.length();++i) {
			r.add(WyChar.valueOf(str.charAt(i)));
		}
		return r;
	}

	/**
	 * Coerce a string into a Whiley int list.
	 * @param str
	 * @return
	 */
	public static WySet str2is(String str) {
		WySet r = new WySet();
		for(int i=0;i!=str.length();++i) {
			r.add(BigInteger.valueOf(str.charAt(i)));
		}
		return r;
	}

	/**
	 * This method is used for the special case when the left-hand side of an
	 * equality operation may be null.
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean equals(Object o1, Object o2) {
		return (o1 != null && o1.equals(o2)) || (o1 == o2);
	}

	/**
	 * The following method is used for printing debug output arising from debug
	 * statements.
	 *
	 * @param list
	 */
	public static void debug(String str) {
		System.out.print(str);
	}

	/**
	 * Increment the reference count for an object. In some cases, this may
	 * have no effect. In other cases, the current reference count will be
	 * maintained and in-place updates can only occur when the reference count is
	 * one.
	 */
	public static Object incRefs(Object obj) {
		if(obj instanceof WyList) {
			WyList list = (WyList) obj;
			list.refCount++;
		} else if(obj instanceof WyRecord) {
			WyRecord rec = (WyRecord) obj;
			rec.refCount++;
		} else if(obj instanceof WySet) {
			WySet set = (WySet) obj;
			set.refCount++;
		} else if(obj instanceof WyMap) {
			WyMap dict = (WyMap) obj;
			dict.refCount++;
		} else if(obj instanceof WyTuple) {
			WyTuple tuple = (WyTuple) obj;
			tuple.refCount++;
		}
		return obj;
	}

	public static WyList incRefs(WyList obj) {
		obj.refCount++;
		return obj;
	}

	public static WySet incRefs(WySet obj) {
		obj.refCount++;
		return obj;
	}

	public static WyRecord incRefs(WyRecord obj) {
		obj.refCount++;
		return obj;
	}

	public static WyMap incRefs(WyMap obj) {
		obj.refCount++;
		return obj;
	}

	public static WyTuple incRefs(WyTuple obj) {
		obj.refCount++;
		return obj;
	}

	/**
	 * Decrement the reference count for this object. In some cases, this may
	 * have no effect. In other cases, the current reference count will be
	 * maintained and in-place updates can only occur when the reference count is
	 * one.
	 */
	public static void decRefs(Object obj) {
//		if(obj instanceof List) {
//			List list = (List) obj;
//			list.refCount--;
//			if(list.refCount == 0) {
//				for(Object o : list) {
//					decRefs(o);
//				}
//			}
//		} else if(obj instanceof Record) {
//			Record rec = (Record) obj;
//			rec.refCount--;
//			if(rec.refCount == 0) {
//				for(Object o : rec.values()) {
//					decRefs(o);
//				}
//			}
//		} else if(obj instanceof Set) {
//			Set set = (Set) obj;
//			set.refCount--;
//			if(set.refCount == 0) {
//				for(Object o : set) {
//					decRefs(o);
//				}
//			}
//		} else if(obj instanceof Dictionary) {
//			Dictionary dict = (Dictionary) obj;
//			dict.refCount--;
//			if(dict.refCount == 0) {
//				for(Map.Entry e : dict.entrySet()) {
//					decRefs(e.getKey());
//					decRefs(e.getValue());
//				}
//			}
//		} else if(obj instanceof Tuple) {
//			Tuple tuple = (Tuple) obj;
//			tuple.refCount--;
//			if(tuple.refCount == 0) {
//				for(Object o : tuple) {
//					decRefs(o);
//				}
//			}
//		}
	}

	public static void decRefs(WyList list) {
//		list.refCount--;
//		if(list.refCount == 0) {
//			for(Object o : list) {
//				decRefs(o);
//			}
//		}
	}

	public static void decRefs(WySet set) {
//		set.refCount--;
//		if(set.refCount == 0) {
//			for(Object o : set) {
//				decRefs(o);
//			}
//		}
	}

	public static void decRefs(WyRecord rec) {
//		rec.refCount--;
//		if(rec.refCount == 0) {
//			for(Object o : rec.values()) {
//				decRefs(o);
//			}
//		}
	}

	public static void decRefs(WyMap dict) {
//		dict.refCount--;
//		if(dict.refCount == 0) {
//			for(Map.Entry e : dict.entrySet()) {
//				decRefs(e.getKey());
//				decRefs(e.getValue());
//			}
//		}
	}

	public static void decRefs(WyTuple tuple) {
//		tuple.refCount--;
//		if(tuple.refCount == 0) {
//			for(Object o : tuple) {
//				decRefs(o);
//			}
//		}
	}

	/**
	 * The <code>instanceOf</code> method implements a runtime type test.
	 */
	public static boolean instanceOf(Object obj, WyType t) {
		switch(t.kind) {
			case K_ANY:
				return true;
			case K_VOID:
				return false;
			case K_NULL:
				return obj == null;
			case K_BOOL:
				return obj instanceof WyBool;
			case K_BYTE:
				return obj instanceof WyByte;
			case K_CHAR:
				return obj instanceof WyChar;
			case K_INT:
				return obj instanceof BigInteger;
			case K_RATIONAL:
				return obj instanceof WyRat;
			case K_STRING:
				return obj instanceof String;
			case K_LIST:
			{
				if(obj instanceof WyList) {
					WyList ol = (WyList) obj;
					WyType.List tl = (WyType.List) t;
					if(tl.nonEmpty && ol.isEmpty()) {
						return false;
					}
					WyType el = tl.element;
					if(el.kind == K_ANY) {
						return true;
					} else if(el.kind == K_VOID) {
						return ol.isEmpty();
					} else {
						for(Object elem : ol) {
							if(!instanceOf(elem,el)) {
								return false;
							}
						}
						return true;
					}
				}
				break;
			}
			case K_SET:
			{
				if(obj instanceof WySet) {
					WySet ol = (WySet) obj;
					WyType.Set tl = (WyType.Set) t;
					WyType el = tl.element;
					if(el.kind == K_ANY) {
						return true;
					} else if(el.kind == K_VOID) {
						return ol.isEmpty();
					} else {
						for(Object elem : ol) {
							if(!instanceOf(elem,el)) {
								return false;
							}
						}
						return true;
					}
				}
				break;
			}
			case K_TUPLE:
			{
				if(obj instanceof WyTuple) {
					WyTuple ol = (WyTuple) obj;
					WyType.Tuple tl = (WyType.Tuple) t;
					WyType[] types = tl.types;
					if(types.length == ol.size()) {
						int i=0;
						for(Object o : ol) {
							if(!instanceOf(o,types[i++])) {
								return false;
							}
						}
						return true;
					}
				}
				break;
			}
			case K_MAP:
			{
				if(obj instanceof WyMap) {
					WyMap ol = (WyMap) obj;
					WyType.Dictionary tl = (WyType.Dictionary) t;
					WyType key = tl.key;
					WyType value = tl.value;

					if (key.kind == K_ANY && value.kind == K_ANY) {
						return true;
					} else if(key.kind == K_VOID || value.kind == K_VOID) {
						return ol.isEmpty();
					} else {
						for (java.util.Map.Entry<Object, Object> elem : ol
								.entrySet()) {
							if (!instanceOf(elem.getKey(), key)
									|| !instanceOf(elem.getValue(), value)) {
								return false;
							}
						}
						return true;
					}
				}
				break;
			}
			case K_RECORD:
			{
				if(obj instanceof WyRecord) {
					WyRecord ol = (WyRecord) obj;
					WyType.Record tl = (WyType.Record) t;
					String[] names = tl.names;
					WyType[] types = tl.types;
					if(!tl.isOpen && names.length != ol.size()) {
						return false;
					}
					for(int i=0;i!=names.length;++i) {
						String name = names[i];
						if(ol.containsKey(name)) {
							WyType type = types[i];
							Object val = ol.get(name);
							if(!instanceOf(val,type)) {
								return false;
							}
						} else {
							return false;
						}
					}
					return true;
				}
				break;
			}
			case K_NEGATION:
			{
				WyType.Negation not = (WyType.Negation) t;
				return !instanceOf(obj,not.element);
			}
			case K_UNION:
			{
				WyType.Union un = (WyType.Union) t;
				for(WyType bound : un.bounds) {
					if(instanceOf(obj,bound)) {
						return true;
					}
				}
				break;
			}
		}
		return false;
	}

	/**
	 * This method gets called when we're testing a list object against some
	 * type. To reduce the number of cases, we can narrow down the possible
	 * types by a process of deduction. The type cannot be <code>void</code> or
	 * <code>any</code> (since the test would already have been eliminated).
	 * Likewise, it cannot be e.g. a record, since again the test would already
	 * have been eliminated. In fact, the type can only be a list or its
	 * negation.
	 *
	 * @param object
	 *            --- object being tested against.
	 * @param type
	 *            --- type to test against.
	 * @return
	 */
	public static boolean instanceOf(WyList object, WyType type) {
		if(type instanceof WyType.List) {
			WyType.List tl = (WyType.List) type;
			WyType el = tl.element;
			if(el.kind == K_ANY) {
				return true;
			} else if(el.kind == K_VOID) {
				return object.isEmpty();
			} else {
				for(Object elem : object) {
					if(!instanceOf(elem,el)) {
						return false;
					}
				}
				return true;
			}
		} else {
			return instanceOf((Object)object,type);
		}
	}

	/**
	 * This method gets called when we're testing a set object against some
	 * type. To reduce the number of cases, we can narrow down the possible
	 * types by a process of deduction. The type cannot be <code>void</code> or
	 * <code>any</code> (since the test would already have been eliminated).
	 * Likewise, it cannot be e.g. a record, since again the test would already
	 * have been eliminated. In fact, the type can only be a set or its
	 * negation.
	 *
	 * @param object
	 *            --- object being tested against.
	 * @param type
	 *            --- type to test against.
	 * @return
	 */
	public static boolean instanceOf(WySet object, WyType type) {
		if(type instanceof WyType.Set) {
			WyType.Set tl = (WyType.Set) type;
			WyType el = tl.element;
			if(el.kind == K_ANY) {
				return true;
			} else if(el.kind == K_VOID) {
				return object.isEmpty();
			} else {
				for(Object elem : object) {
					if(!instanceOf(elem,el)) {
						return false;
					}
				}
				return true;
			}
		} else {
			return instanceOf((Object)object,type);
		}
	}

	/**
	 * This method gets called when we're testing a dictionary object against some
	 * type. To reduce the number of cases, we can narrow down the possible
	 * types by a process of deduction. The type cannot be <code>void</code> or
	 * <code>any</code> (since the test would already have been eliminated).
	 * Likewise, it cannot be e.g. a record, since again the test would already
	 * have been eliminated. In fact, the type can only be a dictionary or its
	 * negation.
	 *
	 * @param object
	 *            --- object being tested against.
	 * @param type
	 *            --- type to test against.
	 * @return
	 */
	public static boolean instanceOf(WyMap object, WyType type) {
		if(type instanceof WyType.Dictionary) {
			WyType.Dictionary tl = (WyType.Dictionary) type;
			WyType key = tl.key;
			WyType value = tl.value;

			if (key.kind == K_ANY && value.kind == K_ANY) {
				return true;
			} else if(key.kind == K_VOID || value.kind == K_VOID) {
				return object.isEmpty();
			} else {
				for (java.util.Map.Entry<Object, Object> elem : object
						.entrySet()) {
					if (!instanceOf(elem.getKey(), key)
							|| !instanceOf(elem.getValue(), value)) {
						return false;
					}
				}
				return true;
			}
		} else {
			return instanceOf((Object)object,type);
		}
	}

	/**
	 * This method gets called when we're testing a record object against some
	 * type. To reduce the number of cases, we can narrow down the possible
	 * types by a process of deduction. The type cannot be <code>void</code> or
	 * <code>any</code> (since the test would already have been eliminated).
	 * Likewise, it cannot be e.g. a list, since again the test would already
	 * have been eliminated. In fact, the type can only be a record or its
	 * negation.
	 *
	 * @param object
	 *            --- object being tested against.
	 * @param type
	 *            --- type to test against.
	 * @return
	 */
	public static boolean instanceOf(WyRecord object, WyType type) {
		if(type instanceof WyType.Record) {
			WyType.Record tl = (WyType.Record) type;
			String[] names = tl.names;
			WyType[] types = tl.types;
			if (!tl.isOpen && names.length != object.size()) {
				return false;
			}
			for(int i=0;i!=names.length;++i) {
				String name = names[i];
				if(object.containsKey(name)) {
					WyType fieldType = types[i];
					Object val = object.get(name);
					if(!instanceOf(val,fieldType)) {
						return false;
					}
				} else {
					return false;
				}
			}
			return true;
		} else {
			return instanceOf((Object)object,type);
		}
	}

	/**
	 * This method gets called when we're testing a tuple object against some
	 * type. To reduce the number of cases, we can narrow down the possible
	 * types by a process of deduction. The type cannot be <code>void</code> or
	 * <code>any</code> (since the test would already have been eliminated).
	 * Likewise, it cannot be e.g. a record, since again the test would already
	 * have been eliminated. In fact, the type can only be a tuple or its
	 * negation.
	 *
	 * @param object
	 *            --- object being tested against.
	 * @param type
	 *            --- type to test against.
	 * @return
	 */
	public static boolean instanceOf(WyTuple object, WyType type) {
		if(type instanceof WyType.Tuple) {
			WyType.Tuple tl = (WyType.Tuple) type;
			WyType[] types = tl.types;
			if(types.length == object.size()) {
				int i=0;
				for(Object o : object) {
					if(!instanceOf(o,types[i++])) {
						return false;
					}
				}
				return true;
			}
			return false;
		} else {
			return instanceOf((Object)object,type);
		}
	}

	public static final Comparator COMPARATOR = new Comparator();

	public static final class Comparator implements java.util.Comparator {
		private Comparator() {}

		public final int compare(Object o1, Object o2) {
			return Util.compare(o1,o2);
		}
	}

	public static int compare(Object o1, Object o2) {
		if(o1 == null) {
			return o2 == null ? 0 : -1;
		} else if(o1 instanceof WyChar) {
			return compare((WyChar)o1,o2);
		} else if(o1 instanceof BigInteger) {
			return compare((BigInteger)o1,o2);
		} else if(o1 instanceof WyRat) {
			return compare((WyRat)o1,o2);
		} else if(o1 instanceof WySet) {
			return compare((WySet)o1,o2);
		} else if(o1 instanceof WyList) {
			return compare((WyList)o1,o2);
		} else if(o1 instanceof WyMap) {
			return compare((WyMap)o1,o2);
		} else if(o1 instanceof WyTuple) {
			return compare((WyTuple)o1,o2);
		} else if(o1 instanceof WyRecord) {
			return compare((WyRecord)o1,o2);
		} else {
			throw new IllegalArgumentException("Invalid object passed to comparator: " + o1);
		}
	}

	public static int compare(WyChar o1, Object o2) {
		if(o2 == null) {
			return 1;
		} else if(o2 instanceof WyChar) {
			WyChar c2 = (WyChar) o2;
			return o1.compareTo(c2);
		} else {
			return -1;
		}
	}

	public static int compare(BigInteger o1, Object o2) {
		if(o2 == null || o2 instanceof Boolean) {
			return 1;
		} else if(o2 instanceof BigInteger) {
			BigInteger b2 = (BigInteger) o2;
			return o1.compareTo(b2);
		} else {
			return -1;
		}
	}

	public static int compare(WyRat o1, Object o2) {
		if(o2 == null || o2 instanceof Boolean || o2 instanceof BigInteger) {
			return 1;
		} else if(o2 instanceof WyRat) {
			WyRat b2 = (WyRat) o2;
			return o1.compareTo(b2);
		} else {
			return -1;
		}
	}

	public static int compare(WySet o1, Object o2) {
		if (o2 == null || o2 instanceof Boolean || o2 instanceof BigInteger
				|| o2 instanceof WyRat) {
			return 1;
		} else if (o2 instanceof WySet) {
			return compare(o1, (WySet) o2);
		} else {
			return -1;
		}
	}

	public static int compare(WySet o1, WySet o2) {
		int s1_size = o1.size();
		int s2_size = o2.size();
		if(s1_size < s2_size) {
			return -1;
		} else if(s1_size > s2_size) {
			return 1;
		} else {
			// this is ugly
			ArrayList a1 = new ArrayList(o1);
			ArrayList a2 = new ArrayList(o2);
			Collections.sort(a1,COMPARATOR);
			Collections.sort(a2,COMPARATOR);
			for(int i=0;i!=s1_size;++i) {
				Object e1 = a1.get(i);
				Object e2 = a2.get(i);
				int c = compare(e1,e2);
				if(c != 0) {
					return c;
				}
			}
			return 0;
		}
	}

	public static int compare(WyList o1, Object o2) {
		if (o2 == null || o2 instanceof Boolean || o2 instanceof BigInteger
				|| o2 instanceof WyRat || o2 instanceof WySet) {
			return 1;
		} else if (o2 instanceof WyList) {
			return compare(o1, (WyList) o2);
		} else {
			return -1;
		}
	}

	public static int compare(WyList o1, WyList o2) {
		int s1_size = o1.size();
		int s2_size = o2.size();
		if(s1_size < s2_size) {
			return -1;
		} else if(s1_size > s2_size) {
			return 1;
		} else {
			for(int i=0;i!=s1_size;++i) {
				Object e1 = o1.get(i);
				Object e2 = o2.get(i);
				int c = compare(e1,e2);
				if(c != 0) {
					return c;
				}
			}
			return 0;
		}
	}

	public static int compare(WyTuple o1, Object o2) {
		if (o2 == null || o2 instanceof Boolean || o2 instanceof BigInteger
				|| o2 instanceof WyRat || o2 instanceof WySet
				|| o2 instanceof WyList) {
			return 1;
		} else if (o2 instanceof WyTuple) {
			return compare(o1, (WyTuple) o2);
		} else {
			return -1;
		}
	}

	public static int compare(WyTuple o1, WyTuple o2) {
		int s1_size = o1.size();
		int s2_size = o2.size();
		if(s1_size < s2_size) {
			return -1;
		} else if(s1_size > s2_size) {
			return 1;
		} else {
			for(int i=0;i!=s1_size;++i) {
				Object e1 = o1.get(i);
				Object e2 = o2.get(i);
				int c = compare(e1,e2);
				if(c != 0) {
					return c;
				}
			}
			return 0;
		}
	}

	public static int compare(WyRecord o1, Object o2) {
		if (o2 == null || o2 instanceof Boolean || o2 instanceof BigInteger
				|| o2 instanceof WyRat || o2 instanceof WySet
				|| o2 instanceof WyTuple) {
			return 1;
		} else if (o2 instanceof WyRecord) {
			return compare(o1, (WyRecord) o2);
		} else {
			return -1;
		}
	}

	public static int compare(WyRecord o1, WyRecord o2) {
		ArrayList<String> mKeys = new ArrayList<String>(o1.keySet());
		ArrayList<String> tKeys = new ArrayList<String>(o2.keySet());
		Collections.sort(mKeys);
		Collections.sort(tKeys);

		for(int i=0;i!=Math.min(mKeys.size(),tKeys.size());++i) {
			String mk = mKeys.get(i);
			String tk = tKeys.get(i);
			int c = mk.compareTo(tk);
			if(c != 0) {
				return c;
			}
			String mv = o1.get(mk).toString();
			String tv = o2.get(tk).toString();
			c = mv.compareTo(tv);
			if(c != 0) {
				return c;
			}
		}

		if(mKeys.size() < tKeys.size()) {
			return -1;
		} else if(mKeys.size() > tKeys.size()) {
			return 1;
		} else {
			return 0;
		}
	}


	public static WyRecord systemConsole(String[] args) {
		WyRecord sysout = new WyRecord();

		sysout.put("print", new WyLambda((Object[]) null) {
			public Object call(Object[] arguments) {
				Object argument = arguments[0];
				if(argument == null || argument instanceof String) {
					System.out.print(argument);
				} else {
					System.out.print(argument.toString());
				}
				return null;
			}
		});
		sysout.put("println", new WyLambda((Object[]) null) {
			public Object call(Object[] arguments) {
				Object argument = arguments[0];
				if(argument == null || argument instanceof String) {
					System.out.println(argument);
				} else {
					System.out.println(argument.toString());
				}
				return null;
			}
		});
		WyRecord console = new WyRecord();
		console.put("out", sysout);
		console.put("args",fromStringList(args));
		return console;
	}

}
