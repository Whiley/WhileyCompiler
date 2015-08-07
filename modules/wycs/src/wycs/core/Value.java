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

package wycs.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import wycc.util.Pair;
import wyautl.util.BigRational;

public abstract class Value implements Comparable<Value> {

	public abstract SemanticType type();

	public static final Null Null = new Null();

	public static Bool Bool(boolean value) {
		return get(new Bool(value));
	}

	public static Decimal Decimal(BigDecimal value) {
		return get(new Decimal(value));
	}

	public static Integer Integer(BigInteger value) {
		return get(new Integer(value));
	}

	public static String String(java.lang.String string) {
		return get(new String(string));
	}

	public static Array Array(Collection<Value> values) {
		return get(new Array(values));
	}

	public static Tuple Tuple(Collection<Value> values) {
		return get(new Tuple(values));
	}

	public static final class Null extends Value {
		private Null() {
		}
		public int hashCode() {
			return 0;
		}
		public boolean equals(Object o) {
			return o instanceof Null;
		}
		public int compareTo(Value v) {
			if(v instanceof Bool) {
				return 1;
			} else {
				return -1;
			}
		}
		public java.lang.String toString() {
			return "null";
		}

		public SemanticType type() {
			return SemanticType.Null;
		}
	}

	public static final class Bool extends Value {
		public final boolean value;
		private Bool(boolean value) {
			this.value = value;
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
		public int compareTo(Value v) {
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
		public java.lang.String toString() {
			if(value) { return "true"; }
			else {
				return "false";
			}
		}

		public SemanticType type() {
			return SemanticType.Bool;
		}
	}

	public static final class Decimal extends Value {
		public final BigDecimal value;
		private Decimal(BigDecimal value) {
			this.value = value;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Decimal) {
				Decimal i = (Decimal) o;
				return value.equals(i.value);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Decimal) {
				Decimal i = (Decimal) v;
				return value.compareTo(i.value);
			} else if(v instanceof Null || v instanceof Bool || v instanceof Integer) {
				return 1;
			}
			return -1;
		}
		public java.lang.String toString() {
			java.lang.String r = value.toString();
			// We need to force the string to include the decimal point.
			if(!r.contains(".")) {
				return r + ".0";
			} else {
				return r;
			}
		}

		public Value.Decimal add(Value.Decimal val) {
			return Value.Decimal(value.add(val.value));
		}
		public Value.Decimal subtract(Value.Decimal val) {
			return Value.Decimal(value.subtract(val.value));
		}
		public Value.Decimal multiply(Value.Decimal val) {
			return Value.Decimal(value.multiply(val.value));
		}
		public Value.Decimal divide(Value.Decimal val) {
			return Value.Decimal(value.divide(val.value));
		}
		public SemanticType type() {
			return SemanticType.Real;
		}
	}

	public static final class Integer extends Value {
		public final BigInteger value;
		private Integer(BigInteger value) {
			this.value = value;
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
		public int compareTo(Value v) {
			if(v instanceof Integer) {
				Integer i = (Integer) v;
				return value.compareTo(i.value);
			} else if (v instanceof Null || v instanceof Bool || v instanceof Decimal) {
				return 1;
			}
			return -1;
		}
		public java.lang.String toString() {
			return value.toString();
		}

		public Value.Integer add(Value.Integer val) {
			return Value.Integer(value.add(val.value));
		}
		public Value.Integer subtract(Value.Integer val) {
			return Value.Integer(value.subtract(val.value));
		}
		public Value.Integer multiply(Value.Integer val) {
			return Value.Integer(value.multiply(val.value));
		}
		public Value.Integer divide(Value.Integer val) {
			return Value.Integer(value.divide(val.value));
		}
		public Value.Integer remainder(Value.Integer val) {
			return Value.Integer(value.remainder(val.value));
		}
		public SemanticType type() {
			return SemanticType.Int;
		}
	}

	public static final class String extends Value {
		public final java.lang.String value;
		private String(java.lang.String value) {
			this.value = value;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof String) {
				String i = (String) o;
				return value.equals(i.value);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof String) {
				String i = (String) v;
				return value.compareTo(i.value);
			} else if (v instanceof Null || v instanceof Bool || v instanceof Decimal
					|| v instanceof Integer) {
				return 1;
			}
			return -1;
		}
		public java.lang.String toString() {
			return "\"" + value.toString() + "\"";
		}

		public SemanticType type() {
			return SemanticType.String;
		}
	}

	public static final class Array extends Value {
		public final ArrayList<Value> values;
		private Array() {
			this.values = new ArrayList<Value>();
		}
		private Array(Collection<Value> value) {
			this.values = new ArrayList<Value>(value);
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Array) {
				Array i = (Array) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Array) {
				Array l = (Array) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					// this case is slightly awkward, since we can't rely on the
					// iteration order for HashSet.
					ArrayList<Value> vs1 = new ArrayList<Value>(values);
					ArrayList<Value> vs2 = new ArrayList<Value>(l.values);
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						Value v1 = vs1.get(i);
						Value v2 = vs2.get(i);
						int c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null
					|| v instanceof Bool
					|| v instanceof Decimal
					|| v instanceof Integer
					|| v instanceof String
					|| v instanceof Tuple) {
				return 1;
			}
			return -1;
		}
		public java.lang.String toString() {
			java.lang.String r = "[";
			boolean firstTime=true;
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "]";
		}

		public Array union(Array rhs) {
			Value.Array nset = new Value.Array(values);
			nset.values.addAll(rhs.values);
			return nset;

		}

		public Array add(Value val) {
			Value.Array nset = new Value.Array(values);
			nset.values.add(val);
			return nset;

		}

		public Array difference(Array rhs) {
			Value.Array nset = new Value.Array(values);
			nset.values.removeAll(rhs.values);
			return nset;
		}

		public Array remove(Value val) {
			Value.Array nset = new Value.Array(values);
			nset.values.remove(val);
			return nset;

		}

		public Array intersect(Array rhs) {
			Value.Array nset = new Value.Array();
			for(Value v : values) {
				if(rhs.values.contains(v)) {
					nset.values.add(v);
				}
			}
			return nset;
		}

		public SemanticType type() {
			SemanticType[] types = new SemanticType[values.size()];
			int i = 0;
			for(Value v : values) {
				types[i++] = v.type();
			}
			return SemanticType.Array(SemanticType.Or(types));			
		}
	}

	public static final class Tuple extends Value {
		public final ArrayList<Value> values;
		private Tuple(Collection<Value> values) {
			this.values = new ArrayList<Value>(values);
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
		public int compareTo(Value v) {
			if(v instanceof Tuple) {
				Tuple l = (Tuple) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					ArrayList<Value> vs1 = values;
					ArrayList<Value> vs2 = l.values;
					for(int i=0;i!=values.size();++i) {
						Value s1 = vs1.get(i);
						Value s2 = vs2.get(i);
						int c = s1.compareTo(s2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null
					|| v instanceof Bool
					|| v instanceof Decimal
					|| v instanceof Integer
					|| v instanceof String
					|| v instanceof Array) {
				return 1;
			}
			return -1;
		}

		public java.lang.String toString() {
			java.lang.String r = "(";
			boolean firstTime=true;
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + ")";
		}

		public SemanticType type() {
			SemanticType[] types = new SemanticType[values.size()];
			int i = 0;
			for (Value v : values) {
				types[i++] = v.type();
			}
			return SemanticType.Tuple(types);
		}
	}

	private static final ArrayList<Value> values = new ArrayList<Value>();
	private static final HashMap<Value,java.lang.Integer> cache = new HashMap<Value,java.lang.Integer>();

	private static <T extends Value> T get(T type) {
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
