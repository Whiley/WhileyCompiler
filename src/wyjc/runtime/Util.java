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

import java.math.*;
import java.util.ArrayList;
import java.util.Map;

import wyil.lang.Value;

public class Util {

	private static final boolean debug = false;
	
	static { 
		if(debug) {
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run() {
					System.err.println("===========================================");
					System.err.println("CLONING STATS");
					System.err.println("===========================================");
					System.err.println("list clones: " + nlist_clones);
					System.err.println("set clones: " + nset_clones);
					System.err.println("record clones: " + nrecord_clones);
				}
			});
		}
	}	
	
	private static int nlist_clones = 0;
	private static int nset_clones = 0;
	private static int nrecord_clones = 0;

	public static String append(final String lhs, final String rhs) {
		return lhs + rhs;
	}
	
	public static String append(final String lhs, final Character rhs) {
		return lhs + rhs;
	}
	
	public static String append(final Character lhs, final String rhs) {
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
	
	public static String set(final String lhs, BigInteger index, Character value) {
		int idx = index.intValue();
		char c = value.charValue();
		// hmmm, not exactly efficient!
		StringBuilder sb = new StringBuilder(lhs);
		sb.setCharAt(idx, c);
		return sb.toString();
	}
	
	public static Byte bitand(Byte b1, Byte b2) {
		byte a = b1;
		byte b = b2;
		int c = a & b;
		return Byte.valueOf((byte)c);
	}
	
	public static Byte bitor(Byte b1, Byte b2) {
		byte a = b1;
		byte b = b2;
		int c = a | b;
		return Byte.valueOf((byte)c);
	}
	
	public static Byte bitxor(Byte b1, Byte b2) {
		byte a = b1;
		byte b = b2;
		int c = a ^ b;
		return Byte.valueOf((byte)c);
	}
	
	public static Byte leftshift(Byte b1, BigInteger b2) {
		byte a = b1;		
		int c = a << b2.intValue();
		return Byte.valueOf((byte)c);
	}
	
	public static Byte rightshift(Byte b1, BigInteger b2) {
		byte a = b1;		
		int c = a >>> b2.intValue();
		return Byte.valueOf((byte)c);
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
	 * Convert a given Whiley object into a string
	 * @param o
	 * @return
	 */
	public static String str(Object o) {
		if(o == null) {
			return "null";
		} else if(o instanceof String) {
			String s = (String) o;
			return "\"" + s + "\"";
		} else if(o instanceof Character) {
			Character s = (Character) o;
			return "\'" + s + "\'";
		} else if(o instanceof Byte) {
			Byte b = (Byte) o;
			String r = "b";
			byte v = b.byteValue();
			for(int i=0;i!=8;++i) {
				if((v&0x1) == 1) {
					r = "1" + r;
				} else {
					r = "0" + r;
				}
				v = (byte) (v >>> 1);
			}
			return r;
		} else {
			return o.toString();
		}
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
	 * Increment the reference count for this object. In some cases, this may
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
	
	/**
	 * Decrement the reference count for this object. In some cases, this may
	 * have no effect. In other cases, the current reference count will be
	 * maintained and in-place updates can only occur when the reference count is
	 * one.
	 */
	public static Object decRefs(Object obj) {
		if(obj instanceof List) {
			List list = (List) obj;
			list.refCount--;
		} else if(obj instanceof Record) {
			Record rec = (Record) obj;			
			rec.refCount--;
		} else if(obj instanceof Set) {
			Set set = (Set) obj;
			set.refCount--;			
		} else if(obj instanceof Dictionary) {
			Dictionary dict = (Dictionary) obj;
			dict.refCount--;			
		} 
		return obj;
	}

	public static List decRefs(List obj) {		
		obj.refCount--;
		return obj;
	}
	
	public static Set decRefs(Set obj) {
		obj.refCount--;
		return obj;
	}
	
	public static Record decRefs(Record obj) {
		obj.refCount--;
		return obj;
	}
	
	public static Dictionary decRefs(Dictionary obj) {
		obj.refCount--;
		return obj;
	}
	
	/**
	 * The <code>instanceOf</code> method implements a runtime type test. 
	 */
	public static boolean instanceOf(Object obj, Type t) {			
		switch(t.kind) {
			case Type.K_ANY:
				return true;
			case Type.K_VOID:
				return false;
			case Type.K_NULL:
				return obj == null;
			case Type.K_BOOL:
				return obj instanceof Boolean;
			case Type.K_BYTE:
				return obj instanceof Byte;
			case Type.K_CHAR:
				return obj instanceof Character;
			case Type.K_INT:
				return obj instanceof BigInteger;
			case Type.K_RATIONAL:
				return obj instanceof BigRational;
			case Type.K_STRING:
				return obj instanceof String;
			case Type.K_LIST:
			{
				if(obj instanceof List) {
					List ol = (List) obj;
					Type.List tl = (Type.List) t;
					Type el = tl.element;
					if(el.kind == Type.K_ANY) {
						return true;
					} else if(el.kind == Type.K_VOID) {
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
			case Type.K_SET:
			{
				if(obj instanceof Set) {
					Set ol = (Set) obj;
					Type.Set tl = (Type.Set) t;
					Type el = tl.element;
					if(el.kind == Type.K_ANY) {
						return true;
					} else if(el.kind == Type.K_VOID) {
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
			case Type.K_TUPLE:
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
			case Type.K_DICTIONARY:
			{
				if(obj instanceof Dictionary) {
					Dictionary ol = (Dictionary) obj;
					Type.Dictionary tl = (Type.Dictionary) t;
					Type key = tl.key;
					Type value = tl.value;
					
					if (key.kind == Type.K_ANY && value.kind == Type.K_ANY) {
						return true;						
					} else if(key.kind == Type.K_VOID || value.kind == Type.K_VOID) {
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
			case Type.K_RECORD:
			{
				if(obj instanceof Record) {
					Record ol = (Record) obj;
					Type.Record tl = (Type.Record) t;
					String[] names = tl.names;
					Type[] types = tl.types;
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
			case Type.K_UNION:
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

	public static boolean instanceOf(List ol, Type t) {
		Type.List tl = (Type.List) t;
		Type el = tl.element;
		if(el.kind == Type.K_ANY) {
			return true;
		} else if(el.kind == Type.K_VOID) {
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
	
	public static boolean instanceOf(Set ol, Type t) {
		Type.Set tl = (Type.Set) t;
		Type el = tl.element;
		if(el.kind == Type.K_ANY) {
			return true;
		} else if(el.kind == Type.K_VOID) {
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
	
	public static boolean instanceOf(Dictionary ol, Type t) {		
		Type.Dictionary tl = (Type.Dictionary) t;
		Type key = tl.key;
		Type value = tl.value;
		
		if (key.kind == Type.K_ANY && value.kind == Type.K_ANY) {
			return true;						
		} else if(key.kind == Type.K_VOID || value.kind == Type.K_VOID) {
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
	
	public static boolean instanceOf(Record ol, Type t) {			
		Type.Record tl = (Type.Record) t;
		String[] names = tl.names;
		Type[] types = tl.types;
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
	
	public static boolean instanceOf(Tuple ol, Type t) {				
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
		return false;
	}
	
	/**
	 * The <code>coerce</code> method forces this object to conform to a given
	 * type.
	 */
	public static Object coerce(Object obj, Type t) {			
		if(obj instanceof BigInteger) {
			return coerce((BigInteger)obj,t);
		} else if(obj instanceof List) {
			return coerce((List)obj,t);
		} else if(obj instanceof Set) {
			return coerce((Set)obj,t);
		} else if(obj instanceof Dictionary) {
			return coerce((Dictionary)obj,t);
		} else if(obj instanceof Record) {
			return coerce((Record)obj,t);
		} else if(obj instanceof String) {
			return coerce((String)obj,t);
		} else if(obj instanceof Tuple) {
			return coerce((Tuple)obj,t);
		} 
				
		return obj;
	}
	
	public static Object coerce(BigInteger obj, Type t) {
		if(t.kind == Type.K_UNION) {
			Type.Union un = (Type.Union) t;
			for(Type b : un.bounds) {
				if(b.kind == Type.K_INT || b.kind == Type.K_ANY) {
					return obj;					
				} else if(b.kind == Type.K_RATIONAL) {
					t = b;
				}
			}
		}
		if(t.kind == Type.K_INT) {
			return obj;
		} else if(t.kind == Type.K_RATIONAL) {
			return BigRational.valueOf(obj);
		} 
		throw new RuntimeException("invalid integer coercion (" + obj + " => " + t + ")");
	}
	
	public static Object coerce(Tuple obj, Type t) {	
		if(t.kind == Type.K_TUPLE) {
			Type.Tuple tup = (Type.Tuple) t;			
			Type[] types = tup.types;
			if(tup.types.length == obj.size()) {			
				Tuple r = new Tuple();
				int i = 0;
				for(Object o : obj) {
					r.add(coerce(o,types[i++]));
				}
				return r;
			}
		}
		throw new RuntimeException("invalid list coercion (" + obj + " => " + t + ")");
	}
	
	public static Object coerce(List obj, Type t) {		
		if(t.kind == Type.K_LIST) {
			Type.List tl = (Type.List) t;
			List r = new List(obj.size());
			for(Object o : obj) {
				r.add(coerce(o,tl.element));
			}
			return r;
		} else if(t.kind == Type.K_DICTIONARY) {
			Type.Dictionary tl = (Type.Dictionary) t;
			Dictionary r = new Dictionary();			
			for (int i = 0; i != obj.size(); ++i) {
				Object key = coerce(BigInteger.valueOf(i),tl.key);
				Object value = coerce(obj.get(i), tl.value);					
				r.put(key, value);
			}			
			return r;
		} else if(t.kind == Type.K_SET) {
			Type.Set tl = (Type.Set) t;
			Set r = new Set();			
			for (Object value : obj) {						
				r.add(coerce(value, tl.element));
			}			
			return r;
		}
		throw new RuntimeException("invalid list coercion (" + obj + " => " + t + ")");
	}
	
	public static Object coerce(String obj, Type t) {		
		if(t.kind == Type.K_STRING) {
			return obj;
		} else if(t.kind == Type.K_LIST) {
			Type.List tl = (Type.List) t;
			List r = new List(obj.length());
			for(int i=0;i!=obj.length();++i) {
				Object index = BigInteger.valueOf(obj.charAt(i));				
				r.add(coerce(index,tl.element));
			}
			return r;
		} 
		throw new RuntimeException("invalid string coercion (" + obj + " => " + t + ")");
	}
	
	public static Object coerce(Set obj, Type t) {
		if(t.kind == Type.K_SET) {
			Type.Set tl = (Type.Set) t;
			Set r = new Set();
			for(Object o : obj) {
				r.add(coerce(o,tl.element));
			}
			return r;
		} 
		throw new RuntimeException("invalid set coercion (" + obj + " => " + t + ")");
	}
	
	public static Object coerce(Dictionary obj, Type t) {
		if(t.kind == Type.K_DICTIONARY) {
			Type.Dictionary tl = (Type.Dictionary) t;
			Dictionary r = new Dictionary();
			for(Map.Entry<Object,Object> o : obj.entrySet()) {
				Object key = coerce(o.getKey(),tl.key);
				Object value = coerce(o.getValue(),tl.value); 
				r.put(key,value);
			}
			return r;
		}
		throw new RuntimeException("invalid dictionary coercion (" + obj + " => " + t + ")");
	}
	
	public static Object coerce(Record obj, Type t) {
		if(t.kind == Type.K_RECORD) {			
			Type.Record tr = (Type.Record) t;
			Record r = new Record();
			String[] names = tr.names;
			Type[] types = tr.types;
			for(int i=0;i!=names.length;++i) {
				String name = names[i];
				Type type = types[i];
				r.put(name,coerce(obj.get(name),type));
			}
			return r;
		}
		throw new RuntimeException("invalid record coercion (" + obj + " => " + t + ")");
	}
}
