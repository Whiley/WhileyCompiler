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

package wyil.lang;

import java.math.BigInteger;
import java.util.*;

import wyil.util.Pair;
import wyil.util.BigRational;

public abstract class Constant implements Comparable<Constant> {	

	public static final Null V_NULL = new Null();

	public abstract wyil.lang.Type type();
	
	public static Bool V_BOOL(boolean value) {
		return get(new Bool(value));
	}
	
	public static Byte V_BYTE(byte value) {
		return get(new Byte(value));
	}
	
	public static Char V_CHAR(char value) {
		return get(new Char(value));
	}
	
	public static Rational V_RATIONAL(BigRational value) {
		return get(new Rational(value));
	}	

	public static Integer V_INTEGER(BigInteger value) {
		return get(new Integer(value));
	}
	
	public static Strung V_STRING(String value) {
		return get(new Strung(value));
	}
	
	public static Set V_SET(Collection<Constant> values) {
		return get(new Set(values));
	}

	public static List V_LIST(Collection<Constant> values) {
		return get(new List(values));
	}	
	
	public static Record V_RECORD(java.util.Map<String,Constant> values) {
		return get(new Record(values));
	}

	public static Map V_MAP(
			java.util.Set<Pair<Constant, Constant>> values) {
		return get(new Map(values));
	}

	public static Map V_MAP(java.util.Map<Constant, Constant> values) {
		return get(new Map(values));
	}
	
	public static Type V_TYPE(wyil.lang.Type type) {
		return get(new Type(type));
	}
	
	public static Tuple V_TUPLE(Collection<Constant> values) {
		return get(new Tuple(values));
	}
	
	public static Lambda V_LAMBDA(NameID name,
			wyil.lang.Type.FunctionOrMethod type) {
		return get(new Lambda(name, type));
	}		
	
	public static final class Null extends Constant {				
		public wyil.lang.Type type() {
			return wyil.lang.Type.T_NULL;
		}
		public int hashCode() {
			return 0;
		}
		public boolean equals(Object o) {			
			return o instanceof Null;
		}
		public String toString() {
			return "null";
		}
		public int compareTo(Constant v) {
			if(v instanceof Null) {
				return 0;
			} else {
				return 1; // everything is above null
			}
		}
	}
	
	public static final class Bool extends Constant {
		public final boolean value;
		private Bool(boolean value) {
			this.value = value;
		}
		public wyil.lang.Type type() {
			return wyil.lang.Type.T_BOOL;
		}
		public int hashCode() {
			return value ? 1 : 0;
		}
		public boolean equals(Object o) {
			if(o instanceof Bool) {
				Bool i = (Bool) o;
				return value == i.value;
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Bool) {
				Bool b = (Bool) v;
				if(value == b.value) {
					return 0;
				} else if(value) {
					return 1;
				} 
			} else if(v instanceof Null) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			if(value) { return "true"; }
			else {
				return "false";
			}
		}		
	}
		
	public static final class Rational extends Constant {
		public final BigRational value;
		private Rational(BigRational value) {
			this.value = value;
		}
		public wyil.lang.Type type() {				
			return wyil.lang.Type.T_REAL;			
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Rational) {
				Rational i = (Rational) o;
				return value.equals(i.value);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Rational) {
				Rational i = (Rational) v;
				return value.compareTo(i.value); 
			} else if(v instanceof Null || v instanceof Bool || v instanceof Byte || v instanceof Char || v instanceof Integer) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return value.toString();
		}
		
		public Constant.Rational add(Constant.Rational val) {
			return Constant.V_RATIONAL(value.add(val.value));
		}
		public Constant.Rational subtract(Constant.Rational val) {
			return Constant.V_RATIONAL(value.subtract(val.value));
		}
		public Constant.Rational multiply(Constant.Rational val) {
			return Constant.V_RATIONAL(value.multiply(val.value));
		}
		public Constant.Rational divide(Constant.Rational val) {
			return Constant.V_RATIONAL(value.divide(val.value));
		}		
	}		
	
	public static final class Byte extends Constant {
		public final byte value;
		private Byte(byte value) {
			this.value = value;
		}
		public wyil.lang.Type type() {				
			return wyil.lang.Type.T_BYTE;			
		}
		public int hashCode() {
			return value;
		}
		public boolean equals(Object o) {
			if(o instanceof Byte) {
				Byte i = (Byte) o;
				return value == i.value;
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Byte) {
				Byte i = (Byte) v;
				if(value < i.value) {
					return -1;
				} else if(value > i.value) {
					return 1;
				} else {
					return 0;
				}
			} else if(v instanceof Null || v instanceof Bool) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			String r = "b";
			byte v = value;
			for(int i=0;i!=8;++i) {
				if((v&0x1) == 1) {
					r = "1" + r;
				} else {
					r = "0" + r;
				}
				v = (byte) (v >>> 1);
			}
			return r;			
		}
	}
	
	public static final class Char extends Constant {
		public final char value;
		private Char(char value) {
			this.value = value;
		}
		public wyil.lang.Type type() {				
			return wyil.lang.Type.T_CHAR;			
		}
		public int hashCode() {
			return value;
		}
		public boolean equals(Object o) {
			if(o instanceof Char) {
				Char i = (Char) o;
				return value == i.value;
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Char) {
				Char i = (Char) v;
				if(value < i.value) {
					return -1;
				} else if(value > i.value) {
					return 1;
				} else {
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Byte) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return "'" + value + "'";			
		}
	}
	
	public static final class Integer extends Constant {
		public final BigInteger value;
		private Integer(BigInteger value) {
			this.value = value;
		}
		public wyil.lang.Type type() {				
			return wyil.lang.Type.T_INT;			
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Integer) {
				Integer i = (Integer) o;
				return value.equals(i.value);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Integer) {
				Integer i = (Integer) v;
				return value.compareTo(i.value); 
			} else if(v instanceof Null || v instanceof Byte || v instanceof Char || v instanceof Bool) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return value.toString();
		}
		
		public Constant.Integer add(Constant.Integer val) {
			return Constant.V_INTEGER(value.add(val.value));
		}
		public Constant.Integer subtract(Constant.Integer val) {
			return Constant.V_INTEGER(value.subtract(val.value));
		}
		public Constant.Integer multiply(Constant.Integer val) {
			return Constant.V_INTEGER(value.multiply(val.value));
		}
		public Constant.Integer divide(Constant.Integer val) {
			return Constant.V_INTEGER(value.divide(val.value));
		}		
		public Constant.Integer remainder(Constant.Integer val) {
			return Constant.V_INTEGER(value.remainder(val.value));
		}
	}
	
	public static final class Strung extends Constant {
		public final String value;
		private Strung(String value) {
			this.value = value;
		}
		public wyil.lang.Type type() {
			return wyil.lang.Type.T_STRING;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Strung) {
				Strung i = (Strung) o;
				return value.equals(i.value);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Strung) {
				Strung i = (Strung) v;
				return value.compareTo(i.value); 
			} else if(v instanceof Null || v instanceof Bool || v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return "\"" + value.toString() + "\"";
		}
	}
	
	public static final class List extends Constant {
		public final ArrayList<Constant> values;
		private List(Collection<Constant> value) {
			this.values = new ArrayList<Constant>(value);
		}
		public wyil.lang.Type.List type() {
			wyil.lang.Type t = wyil.lang.Type.T_VOID;
			for(Constant arg : values) {
				t = wyil.lang.Type.Union(t,arg.type());
			}
			return wyil.lang.Type.List(t, !values.isEmpty());			
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof List) {
				List i = (List) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof List) {
				List l = (List) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					for(int i=0;i!=values.size();++i) {
						Constant v1 = values.get(i);
						Constant v2 = l.values.get(i);
						int c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer || v instanceof Strung) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			String r = "[";
			boolean firstTime=true;
			for(Constant v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "]";
		}
	}
	
	public static final class Set extends Constant {
		public final HashSet<Constant> values;
		private Set() {
			this.values = new HashSet<Constant>();
		}
		private Set(Collection<Constant> value) {
			this.values = new HashSet<Constant>(value);
		}
		public wyil.lang.Type.Set type() {
			wyil.lang.Type t = wyil.lang.Type.T_VOID;
			for(Constant arg : values) {
				t = wyil.lang.Type.Union(t,arg.type());
			}
			return wyil.lang.Type.Set(t, !values.isEmpty());	
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Set) {
				Set i = (Set) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Set) {
				Set l = (Set) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					// this case is slightly awkward, since we can't rely on the
					// iteration order for HashSet.
					ArrayList<Constant> vs1 = new ArrayList<Constant>(values);
					ArrayList<Constant> vs2 = new ArrayList<Constant>(l.values);
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						Constant v1 = vs1.get(i);
						Constant v2 = vs2.get(i);
						int c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer || v instanceof Strung
					|| v instanceof List || v instanceof Tuple) {
				return 1;
			}
			return -1;			
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			for(Constant v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "}";
		}
		
		public Set union(Set rhs) {
			Constant.Set nset = new Constant.Set(values);
			nset.values.addAll(rhs.values);
			return nset;
			
		}
		
		public Set add(Constant val) {
			Constant.Set nset = new Constant.Set(values);
			nset.values.add(val);
			return nset;
			
		}
		
		public Set difference(Set rhs) {
			Constant.Set nset = new Constant.Set(values);
			nset.values.removeAll(rhs.values);
			return nset;
		}
		
		public Set remove(Constant val) {
			Constant.Set nset = new Constant.Set(values);
			nset.values.remove(val);
			return nset;
			
		}
		
		public Set intersect(Set rhs) {
			Constant.Set nset = new Constant.Set();
			for(Constant v : values) {
				if(rhs.values.contains(v)) {
					nset.values.add(v);
				}
			}			
			return nset;
		}		
	}
	
	public static final class Record extends Constant {
		public final HashMap<String,Constant> values;
		private Record(java.util.Map<String,Constant> value) {
			this.values = new HashMap<String,Constant>(value);
		}

		public wyil.lang.Type.Record type() {
			HashMap<String, wyil.lang.Type> types = new HashMap<String, wyil.lang.Type>();
			for (java.util.Map.Entry<String, Constant> e : values.entrySet()) {
				types.put(e.getKey(), e.getValue().type());
			}
			return wyil.lang.Type.Record(false,types);
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Record) {
				Record i = (Record) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Record) {
				Record l = (Record) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					ArrayList<String> vs1 = new ArrayList<String>(values.keySet());
					ArrayList<String> vs2 = new ArrayList<String>(l.values.keySet());
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						String s1 = vs1.get(i);
						String s2 = vs2.get(i);
						int c = s1.compareTo(s2);
						if(c != 0) { return c; }
						Constant v1 = values.get(s1);
						Constant v2 = l.values.get(s1);
						c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer || v instanceof Strung
					|| v instanceof Set || v instanceof List || v instanceof Tuple) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			ArrayList<String> keys = new ArrayList<String>(values.keySet());
			Collections.sort(keys);
			for(String key : keys) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += key + ":=" + values.get(key);
			}
			return r + "}";
		}
	}
	
	public static final class Map extends Constant {
		public final HashMap<Constant,Constant> values;
		private Map(java.util.Map<Constant,Constant> value) {
			this.values = new HashMap<Constant,Constant>(value);
		}
		private Map(java.util.Set<Pair<Constant,Constant>> values) {
			this.values = new HashMap<Constant,Constant>();
			for(Pair<Constant,Constant> p : values) {
				this.values.put(p.first(), p.second());
			}
		}
		public wyil.lang.Type.Map type() {
			wyil.lang.Type key = wyil.lang.Type.T_VOID;
			wyil.lang.Type value = wyil.lang.Type.T_VOID;
			for (java.util.Map.Entry<Constant, Constant> e : values.entrySet()) {
				key = wyil.lang.Type.Union(key,e.getKey().type());
				value = wyil.lang.Type.Union(value,e.getKey().type());
			}
			return wyil.lang.Type.Map(key,value);
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Map) {
				Map i = (Map) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Map) {
				Map l = (Map) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					ArrayList<Constant> vs1 = new ArrayList<Constant>(values.keySet());
					ArrayList<Constant> vs2 = new ArrayList<Constant>(l.values.keySet());
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						Constant k1 = vs1.get(i);
						Constant k2 = vs2.get(i);
						int c = k1.compareTo(k2);
						if(c != 0) { return c; }
						Constant v1 = values.get(k1);
						Constant v2 = l.values.get(k1);
						c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer || v instanceof Strung
					|| v instanceof Set || v instanceof List || v instanceof Tuple
					|| v instanceof Record) {
				return 1;
			}
			return -1;			
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			ArrayList<String> keystr = new ArrayList<String>();
			HashMap<String,Constant> keymap = new HashMap<String,Constant>();
			for(Constant key : values.keySet()) {
				keystr.add(key.toString());
				keymap.put(key.toString(), key);
			}
			Collections.sort(keystr);
			for(String key : keystr) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				Constant k = keymap.get(key); 
				r += k + "->" + values.get(k);
			}
			return r + "}";
		}
	}
	
	public static final class Type extends Constant {
		public final wyil.lang.Type type;
		private Type(wyil.lang.Type type) {
			this.type = type;
		}
		public wyil.lang.Type.Meta type() {
			return wyil.lang.Type.T_META;
		}
		public int hashCode() {
			return type.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Type) {
				Type i = (Type) o;
				return type == i.type;
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Type) {
				Type t = (Type) v;
				// FIXME: following is an ugly hack!
				return type.toString().compareTo(t.toString());
			} else {
				return 1; // everything is above a type constant
			}					
		}
		public String toString() {
			return type.toString();
		}
	}
	
	public static final class Lambda extends Constant {
		public final NameID name;
		public final wyil.lang.Type.FunctionOrMethod type;
		
		private Lambda(NameID name, wyil.lang.Type.FunctionOrMethod type) {
			this.name = name;
			this.type = type;
		}

		public wyil.lang.Type.FunctionOrMethod type() {
			if (type == null) {
				return wyil.lang.Type.Function(wyil.lang.Type.T_ANY,
						wyil.lang.Type.T_ANY);
			} else {
				return type;
			}
		}
		public int hashCode() {
			if(type != null) {
				return type.hashCode() + name.hashCode();
			} else {
				return name.hashCode();
			}
		}
		public boolean equals(Object o) {
			if(o instanceof Lambda) {
				Lambda i = (Lambda) o;
				return name.equals(i.name)
						&& (type == i.type || (type != null && type
								.equals(i.type)));
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Lambda) {
				Lambda t = (Lambda) v;
				// FIXME: following is an ugly hack!
				return type.toString().compareTo(t.toString());
			} else {
				return 1; // everything is above a type constant
			}					
		}
		public String toString() {
			return "&" + name.toString() + ":" + type.toString();
		}
	}
	
	public static final class Tuple extends Constant {
		public final ArrayList<Constant> values;
		private Tuple(Collection<Constant> values) {
			this.values = new ArrayList<Constant>(values);
		}

		public wyil.lang.Type.Tuple type() {
			ArrayList<wyil.lang.Type> types = new ArrayList<wyil.lang.Type>();			
			for (Constant e : values) {
				types.add(e.type());				
			}
			return wyil.lang.Type.Tuple(types);
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Tuple) {
				Tuple i = (Tuple) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Tuple) {
				Tuple l = (Tuple) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					ArrayList<Constant> vs1 = values;
					ArrayList<Constant> vs2 = l.values;
					for(int i=0;i!=values.size();++i) {
						Constant s1 = vs1.get(i);
						Constant s2 = vs2.get(i);
						int c = s1.compareTo(s2);
						if(c != 0) { return c; }						
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer || v instanceof Strung
					|| v instanceof Set || v instanceof List) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			String r = "(";
			boolean firstTime=true;			
			for(Constant v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + ")";
		}
	}
		
	private static final ArrayList<Constant> values = new ArrayList<Constant>();
	private static final HashMap<Constant,java.lang.Integer> cache = new HashMap<Constant,java.lang.Integer>();
	
	private static <T extends Constant> T get(T type) {
		java.lang.Integer idx = cache.get(type);
		if(idx != null) {
			return (T) values.get(idx);
		} else {					
			cache.put(type, values.size());
			values.add(type);
			return type;
		}
	}
}
