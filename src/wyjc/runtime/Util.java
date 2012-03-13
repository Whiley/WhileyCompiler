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

import static wyil.lang.Type.K_ANY;
import static wyil.lang.Type.K_BOOL;
import static wyil.lang.Type.K_BYTE;
import static wyil.lang.Type.K_CHAR;
import static wyil.lang.Type.K_DICTIONARY;
import static wyil.lang.Type.K_INT;
import static wyil.lang.Type.K_LIST;
import static wyil.lang.Type.K_NEGATION;
import static wyil.lang.Type.K_NULL;
import static wyil.lang.Type.K_RATIONAL;
import static wyil.lang.Type.K_RECORD;
import static wyil.lang.Type.K_SET;
import static wyil.lang.Type.K_STRING;
import static wyil.lang.Type.K_TUPLE;
import static wyil.lang.Type.K_UNION;
import static wyil.lang.Type.K_VOID;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import wyjc.runtime.concurrency.Scheduler;

public class Util {

	private static final boolean debug = false;
	private static final boolean logRefCounts = false;
	private static final ArrayList<Object[]> refCounts = new ArrayList();
	private static long startTime;
	
	static { 
		if(debug) {
			startTime = System.currentTimeMillis();
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run() {
					long totalTime = System.currentTimeMillis() - startTime;
					System.err.println("==================================================");
					System.err.println("STATS");
					System.err.println("==================================================");
					System.err.println("Time: " + totalTime + "ms");
					double avg = nset_elems;
					avg = avg / nset_clones;
					System.err.println("set clones:        " + nset_clones + " / "
							+ (nset_clones + nset_inplace_updates) + " (" + avg + ")");
					avg = nlist_elems;
					avg = avg / nlist_clones;
					System.err.println("list clones:       " + nlist_clones + " / "
							+ (nlist_clones + nlist_inplace_updates)+ " (" + avg + ")");
					avg = ndict_elems;
					avg = avg / ndict_clones;
					System.err.println("dictionary clones: " + ndict_clones
							+ " / " + (ndict_clones + ndict_inplace_updates)+ " (" + avg + ")");
					avg = nrecord_elems;
					avg = avg / nrecord_clones;
					System.err.println("record clones:     " + nrecord_clones
							+ " / " + (nrecord_clones + nrecord_strong_updates)+ " (" + avg + ")");		
					long totalClones = nlist_clones + nset_clones + ndict_clones + nrecord_clones;
					long totalStrongUpdates = nlist_inplace_updates + nset_inplace_updates + ndict_inplace_updates + nrecord_strong_updates;
					double ratio = totalClones;
					ratio = 100 * (ratio / (totalClones+totalStrongUpdates));
					long totalElems = nset_elems + nlist_elems + ndict_elems + nrecord_elems; 
					avg = totalElems;
					avg = (avg / (totalClones));
					System.err.println("--------------------------------------------------");
					System.err.println("Total clones: " + totalClones + " / " + (totalClones+totalStrongUpdates) + " (" + ratio + "%)");
					System.err.println("Average Clone Size: " + totalElems + " / " + totalClones + " (" + avg + ")");
					avg = total_ref_count;
					avg = avg / total_population;
					System.err.println("Avg Reference Count: " + avg);	
					System.err.println("--------------------------------------------------");
					if(logRefCounts) {
						for(Object[] p : refCounts) {
							System.out.println(System.identityHashCode(p[0]) + " : " + p[1]);
						}
					}
				}
			});
		}
	}	
	
	private static long total_ref_count = 0;	
	private static long total_population = 0; // number of update operations
	private static int nlist_clones = 0;
	private static long nlist_elems = 0;
	static int nlist_inplace_updates = 0;
	private static int nset_clones = 0;
	private static long nset_elems = 0;
	static int nset_inplace_updates = 0;
	private static int ndict_clones = 0;
	private static long ndict_elems = 0;
	static int ndict_inplace_updates = 0;
	private static int nrecord_clones = 0;
	private static long nrecord_elems = 0;
	static int nrecord_strong_updates = 0;

	public static void countRefs(List l) {
		total_ref_count += l.refCount;		
		total_population++;
		if(logRefCounts) {
			refCounts.add(new Object[]{l,l.refCount});
		}
	}
	
	public static void countRefs(Set l) {
		total_ref_count += l.refCount;		
		total_population++;
		if(logRefCounts) {
			refCounts.add(new Object[]{l,l.refCount});
		}
	}
	
	public static void countRefs(Dictionary l) {
		total_ref_count += l.refCount;		
		total_population++;
		if(logRefCounts) {
			refCounts.add(new Object[]{l,l.refCount});
		}
	}
	
	public static void countRefs(Record l) {
		total_ref_count += l.refCount;
		total_population++;
		if(logRefCounts) {
			refCounts.add(new Object[]{l,l.refCount});
		}
	}
	
	public static void countClone(List l) {
		nlist_clones ++;		
		nlist_elems += l.size();
	}
	
	public static void countClone(Set l) {
		nset_clones ++;		
		nset_elems += l.size();
	}
	
	public static void countClone(Dictionary l) {
		ndict_clones ++;		
		ndict_elems += l.size();
	}
	
	public static void countClone(Record l) {
		nrecord_clones ++;		
		nrecord_elems += l.size();
	}
	
	public static Method functionRef(String clazz, String name) {
		try {
			Class cl = Class.forName(clazz);
			for(Method m : cl.getDeclaredMethods()) {
				if(m.getName().equals(name)) {
					return m;
				}
			}
			throw new RuntimeException("Method Not Found: " + clazz + ":" + name);
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("Class Not Found: " + clazz);
		}
	}
	
	public static String append(final String lhs, final String rhs) {		
		return lhs + rhs;
	}
	
	public static String append(final String lhs, final char rhs) {		
		return lhs + rhs;
	}
	
	public static String append(final char lhs, final String rhs) {		
		return lhs + rhs;
	}
	
	public static BigInteger stringlength(final String lhs) {
		return BigInteger.valueOf(lhs.length());
	}
	
	public static String substring(final String lhs, final BigInteger _start, final BigInteger _end) {
		int start = _start.intValue();
		int end = _end.intValue();
		return lhs.substring(start,end);
	}
	
	public static String set(final String lhs, BigInteger index, char value) {
		int idx = index.intValue();		
		// hmmm, not exactly efficient!
		StringBuilder sb = new StringBuilder(lhs);
		sb.setCharAt(idx, value);
		return sb.toString();
	}
	
	public static byte leftshift(byte b1, BigInteger b2) {		
		return (byte) ((b1&0xFF) << b2.intValue());		
	}
	
	public static byte rightshift(byte b1, BigInteger b2) {		
		return (byte) ((b1&0xFF) >>> b2.intValue());		
	}
	
	public static List range(BigInteger start, BigInteger end) {
		List l = new List();
		
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
	
	/**
	 * This method is used to convert the arguments supplied to main (which have
	 * type <code>String[]</code>) into an appropriate Whiley List.
	 * 
	 * @param args
	 * @return
	 */
	public static List fromStringList(String[] args) {		
		List r = new List(args.length);
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
	public static List str2cl(String str) {
		List r = new List(str.length());
		for(int i=0;i!=str.length();++i) {
			r.add(str.charAt(i));
		}
		return r;
	}
	
	/**
	 * Coerce a string into a Whiley int list.
	 * @param str
	 * @return
	 */
	public static List str2il(String str) {
		List r = new List(str.length());
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
		if(obj instanceof List) {
			List list = (List) obj;
			list.refCount++;
		} else if(obj instanceof Record) {
			Record rec = (Record) obj;			
			rec.refCount++;			
		} else if(obj instanceof Set) {
			Set set = (Set) obj;
			set.refCount++;			
		} else if(obj instanceof Dictionary) {
			Dictionary dict = (Dictionary) obj;
			dict.refCount++;						
		} else if(obj instanceof Tuple) {
			Tuple tuple = (Tuple) obj;
			tuple.refCount++;			
		}
		return obj;
	}

	public static List incRefs(List obj) {		
		obj.refCount++;
		return obj;
	}
	
	public static Set incRefs(Set obj) {
		obj.refCount++;
		return obj;
	}
	
	public static Record incRefs(Record obj) {
		obj.refCount++;
		return obj;
	}
	
	public static Dictionary incRefs(Dictionary obj) {
		obj.refCount++;
		return obj;
	}
	
	public static Tuple incRefs(Tuple obj) {
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
		if(obj instanceof List) {
			List list = (List) obj;
			list.refCount--;
			if(list.refCount == 0) {
				for(Object o : list) {
					decRefs(o);
				}
			}
		} else if(obj instanceof Record) {
			Record rec = (Record) obj;			
			rec.refCount--;
			if(rec.refCount == 0) {				
				for(Object o : rec.values()) {
					decRefs(o);
				}
			}
		} else if(obj instanceof Set) {
			Set set = (Set) obj;
			set.refCount--;			
			if(set.refCount == 0) {
				for(Object o : set) {
					decRefs(o);
				}
			}
		} else if(obj instanceof Dictionary) {
			Dictionary dict = (Dictionary) obj;
			dict.refCount--;	
			if(dict.refCount == 0) {				
				for(Map.Entry e : dict.entrySet()) {
					decRefs(e.getKey());
					decRefs(e.getValue());
				}
			}
		} else if(obj instanceof Tuple) {
			Tuple tuple = (Tuple) obj;
			tuple.refCount--;			
			if(tuple.refCount == 0) {
				for(Object o : tuple) {
					decRefs(o);
				}
			}
		} 
	}

	public static void decRefs(List list) {		
		list.refCount--;
		if(list.refCount == 0) {
			for(Object o : list) {
				decRefs(o);
			}
		}
	}
	
	public static void decRefs(Set set) {
		set.refCount--;
		if(set.refCount == 0) {
			for(Object o : set) {
				decRefs(o);
			}
		}
	}
	
	public static void decRefs(Record rec) {
		rec.refCount--;
		if(rec.refCount == 0) {				
			for(Object o : rec.values()) {
				decRefs(o);
			}
		}		
	}
	
	public static void decRefs(Dictionary dict) {
		dict.refCount--;
		if(dict.refCount == 0) {				
			for(Map.Entry e : dict.entrySet()) {
				decRefs(e.getKey());
				decRefs(e.getValue());
			}
		}
	}
	
	public static void decRefs(Tuple tuple) {		
		tuple.refCount--;
		if(tuple.refCount == 0) {
			for(Object o : tuple) {
				decRefs(o);
			}
		}
	}
	
	/**
	 * The <code>instanceOf</code> method implements a runtime type test. 
	 */
	public static boolean instanceOf(Object obj, Type t) {			
		switch(t.kind) {
			case K_ANY:
				return true;
			case K_VOID:
				return false;
			case K_NULL:
				return obj == null;
			case K_BOOL:
				return obj instanceof Boolean;
			case K_BYTE:
				return obj instanceof Byte;
			case K_CHAR:
				return obj instanceof Character;
			case K_INT:
				return obj instanceof BigInteger;
			case K_RATIONAL:
				return obj instanceof BigRational;
			case K_STRING:
				return obj instanceof String;
			case K_LIST:
			{
				if(obj instanceof List) {
					List ol = (List) obj;
					Type.List tl = (Type.List) t;
					if(tl.nonEmpty && ol.isEmpty()) {
						return false;
					}
					Type el = tl.element;
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
				if(obj instanceof Set) {
					Set ol = (Set) obj;
					Type.Set tl = (Type.Set) t;
					Type el = tl.element;
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
				if(obj instanceof Tuple) {
					Tuple ol = (Tuple) obj;
					Type.Tuple tl = (Type.Tuple) t;
					Type[] types = tl.types;
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
			case K_DICTIONARY:
			{
				if(obj instanceof Dictionary) {
					Dictionary ol = (Dictionary) obj;
					Type.Dictionary tl = (Type.Dictionary) t;
					Type key = tl.key;
					Type value = tl.value;
					
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
				if(obj instanceof Record) {
					Record ol = (Record) obj;
					Type.Record tl = (Type.Record) t;
					String[] names = tl.names;
					Type[] types = tl.types;					
					if(!tl.isOpen && names.length != ol.size()) {
						return false;
					}
					for(int i=0;i!=names.length;++i) {
						String name = names[i];
						if(ol.containsKey(name)) {
							Type type = types[i];
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
				Type.Negation not = (Type.Negation) t;
				return !instanceOf(obj,not.element);
			}
			case K_UNION:
			{
				Type.Union un = (Type.Union) t;
				for(Type bound : un.bounds) {
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
	public static boolean instanceOf(List object, Type type) {
		if(type instanceof Type.List) {			
			Type.List tl = (Type.List) type;
			Type el = tl.element;
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
	public static boolean instanceOf(Set object, Type type) {
		if(type instanceof Type.Set) {			
			Type.Set tl = (Type.Set) type;
			Type el = tl.element;
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
	public static boolean instanceOf(Dictionary object, Type type) {		
		if(type instanceof Type.Dictionary) {			
			Type.Dictionary tl = (Type.Dictionary) type;
			Type key = tl.key;
			Type value = tl.value;

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
	public static boolean instanceOf(Record object, Type type) {
		if(type instanceof Type.Record) {			
			Type.Record tl = (Type.Record) type;
			String[] names = tl.names;
			Type[] types = tl.types;
			if (!tl.isOpen && names.length != object.size()) {
				return false;
			}
			for(int i=0;i!=names.length;++i) {
				String name = names[i];
				if(object.containsKey(name)) {
					Type fieldType = types[i];
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
	public static boolean instanceOf(Tuple object, Type type) {				
		if(type instanceof Type.Tuple) {			
			Type.Tuple tl = (Type.Tuple) type;
			Type[] types = tl.types;
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
		} else if(o1 instanceof Boolean) {
			return compare((Boolean)o1,o2);
		} else if(o1 instanceof Character) {			
			return compare((Character)o1,o2);
		} else if(o1 instanceof BigInteger) {			
			return compare((BigInteger)o1,o2);
		} else if(o1 instanceof BigRational) {
			return compare((BigRational)o1,o2);
		} else if(o1 instanceof Set) {
			return compare((Set)o1,o2);
		} else if(o1 instanceof List) {
			return compare((List)o1,o2);
		} else if(o1 instanceof Dictionary) {
			return compare((Dictionary)o1,o2);
		} else if(o1 instanceof Tuple) {
			return compare((Tuple)o1,o2);
		} else if(o1 instanceof Record) {
			return compare((Record)o1,o2);
		} else {
			throw new IllegalArgumentException("Invalid object passed to comparator: " + o1);
		}
	}

	public static int compare(Boolean o1, Object o2) {
		if(o2 == null) {
			return 1;
		} else if(o2 instanceof Boolean) {
			Boolean b2 = (Boolean) o2;
			return o1.compareTo(b2);
		} else {
			return -1;
		}
	}

	public static int compare(Character o1, Object o2) {
		if(o2 == null) {
			return 1;
		} else if(o2 instanceof Character) {
			Character c2 = (Character) o2;
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

	public static int compare(BigRational o1, Object o2) {
		if(o2 == null || o2 instanceof Boolean || o2 instanceof BigInteger) {
			return 1;
		} else if(o2 instanceof BigRational) {
			BigRational b2 = (BigRational) o2;
			return o1.compareTo(b2);
		} else {
			return -1;
		}
	}

	public static int compare(Set o1, Object o2) {
		if (o2 == null || o2 instanceof Boolean || o2 instanceof BigInteger
				|| o2 instanceof BigRational) {
			return 1;
		} else if (o2 instanceof Set) {
			return compare(o1, (Set) o2);
		} else {
			return -1;
		}
	}

	public static int compare(Set o1, Set o2) {
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

	public static int compare(List o1, Object o2) {
		if (o2 == null || o2 instanceof Boolean || o2 instanceof BigInteger
				|| o2 instanceof BigRational || o2 instanceof Set) {
			return 1;
		} else if (o2 instanceof List) {
			return compare(o1, (List) o2);
		} else {
			return -1;
		}
	}

	public static int compare(List o1, List o2) {
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

	public static int compare(Tuple o1, Object o2) {
		if (o2 == null || o2 instanceof Boolean || o2 instanceof BigInteger
				|| o2 instanceof BigRational || o2 instanceof Set
				|| o2 instanceof List) {
			return 1;
		} else if (o2 instanceof Tuple) {
			return compare(o1, (Tuple) o2);
		} else {
			return -1;
		}
	}

	public static int compare(Tuple o1, Tuple o2) {
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

	public static int compare(Record o1, Object o2) {
		if (o2 == null || o2 instanceof Boolean || o2 instanceof BigInteger
				|| o2 instanceof BigRational || o2 instanceof Set
				|| o2 instanceof Tuple) {
			return 1;
		} else if (o2 instanceof Record) {
			return compare(o1, (Record) o2);
		} else {
			return -1;
		}
	}

	public static int compare(Record o1, Record o2) {
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
	

	public static Record newSystemConsole(String[] args, Scheduler scheduler) {
		// Not sure what the default value should be yet!!!
		Actor sysout = new Actor(null, scheduler);
		Record data = new Record();
		data.put("out", sysout);		
		data.put("args", fromStringList(args));
		Record console = new Record(data);		
		return console;
	}
	
}
