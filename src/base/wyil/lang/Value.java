// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.lang;

import java.math.BigInteger;
import java.util.*;
import wyil.jvm.rt.BigRational;
import wyil.util.Pair;

public abstract class Value extends CExpr {	

	public static final Null V_NULL = new Null();
	
	public static Bool V_BOOL(boolean value) {
		return get(new Bool(value));
	}
	
	public static Int V_INT(BigInteger value) {
		return get(new Int(value));
	}

	public static Real V_REAL(BigRational value) {
		return get(new Real(value));
	}	

	public static Set V_SET(Collection<Value> values) {
		return get(new Set(values));
	}

	public static List V_LIST(Collection<Value> values) {
		return get(new List(values));
	}
	
	public static Record V_RECORD(Map<String,Value> values) {
		return get(new Record(values));
	}

	public static Dictionary V_DICTIONARY(
			java.util.Set<Pair<Value, Value>> values) {
		return get(new Dictionary(values));
	}

	public static TypeConst V_TYPE(Type type) {
		return get(new TypeConst(type));
	}
	
	/**
	 * Evaluate the given operation on the given values. If the evaluation is
	 * impossible, then return null.
	 * 
	 * @param op
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Value evaluate(CExpr.BOP op, Value lhs, Value rhs) {
		Type lub = Type.leastUpperBound(lhs.type(),rhs.type());		
		lhs = convert(lub,lhs);
		rhs = convert(lub,rhs);
		if(lhs == null || rhs == null) {
			throw new IllegalArgumentException("Invalid arguments supplied to evaluate(BOP,Value,Value)");
		} else if(lub instanceof Type.Int || lub instanceof Type.Real) {
			return evaluateArith(op,lhs,rhs);
		} else if(lub instanceof Type.Set) {
			return evaluateSet(op,(Value.Set) lhs, (Value.Set) rhs);
		} else if(lub instanceof Type.List) {
			return evaluateList(op,(Value.List) lhs, (Value.List) rhs);
		}
		throw new IllegalArgumentException("Missing cases in evaluate(BOP,Value,Value)");
	}
	
	private static Value evaluateArith(CExpr.BOP op, Value lhs, Value rhs) {
		if(lhs instanceof Int){
			Int lv = (Int) lhs;
			Int rv = (Int) rhs;
			switch(op) {
			case ADD:
				return V_INT(lv.value.add(rv.value));
			case SUB:
				return V_INT(lv.value.subtract(rv.value));
			case MUL:
				return V_INT(lv.value.multiply(rv.value));
			case DIV:
				return V_INT(lv.value.divide(rv.value));
			}
		} else if(lhs instanceof Real) {
			Real lv = (Real) lhs;
			Real rv = (Real) rhs;
			switch(op) {
			case ADD:
				return V_REAL(lv.value.add(rv.value));
			case SUB:
				return V_REAL(lv.value.subtract(rv.value));
			case MUL:
				return V_REAL(lv.value.multiply(rv.value));
			case DIV:
				return V_REAL(lv.value.divide(rv.value));
			}
		}
		return null;
	}
	
	private static Value evaluateSet(CExpr.BOP op, Value.Set lhs, Value.Set rhs) {		
		switch(op) {
		case UNION:
		{			
			HashSet<Value> r = new HashSet<Value>(lhs.values);
			r.addAll(rhs.values);
			return V_SET(r);
		}
		case DIFFERENCE:
		{			
			HashSet<Value> r = new HashSet<Value>();
			for(Value v : lhs.values) {
				if(!(rhs.values.contains(v))) {
					r.add(v);
				}
			}
			return V_SET(r);
		}
		case INTERSECT:
			HashSet<Value> r = new HashSet<Value>();
			for(Value v : lhs.values) {
				if(rhs.values.contains(v)) {
					r.add(v);
				}
			}
			return V_SET(r);
		}
		return null;
	}
	
	private static Value evaluateList(CExpr.BOP op, Value.List lhs, Value.List rhs) {		
		switch(op) {
		case APPEND:
		{			
			ArrayList<Value> r = new ArrayList<Value>(lhs.values);
			r.addAll(rhs.values);
			return V_LIST(r);
		}		
		}
		return null;
	}
	
	public static CExpr evaluate(CExpr.NOP op, java.util.List<Value> args) {
		if(op == CExpr.NOP.LISTGEN) {
			return Value.V_LIST(args); 
		} else if(op == CExpr.NOP.SETGEN) {
			return Value.V_SET(args);
		} else if(op == CExpr.NOP.SUBLIST) {
			Value src = args.get(0);
			Value start = args.get(1);
			Value end = args.get(2);
			if (src instanceof List && start instanceof Int
					&& end instanceof Int) {
				List l = (List) src;
				Int s = (Int) start;
				Int e = (Int) end;
				// FIXME: potential bug here
				int si = s.value.intValue();
				int ei = e.value.intValue();
				if (si >= 0 && ei >= 0 && si < l.values.size()
						&& ei <= l.values.size()) {
					java.util.List nl = l.values.subList(si, ei);
					return V_LIST(nl);
				} else {
					return CExpr.NARYOP(op, l,s,e);
				}
			}
		} 		
		throw new IllegalArgumentException("Invalid operands to Value.evaluate(NOP,Value...)");		
	}
	
	public static CExpr evaluate(CExpr.UOP op, Value mhs) {
		switch(op) {
		case NEG:	
			if(mhs instanceof Int) {
				Int i = (Int) mhs;
				return V_INT(i.value.negate());
			} else if(mhs instanceof Real) {
				Real i = (Real) mhs;
				return V_REAL(i.value.negate());
			}
			break;
		case LENGTHOF:		
			if(mhs instanceof List) {
				List l = (List) mhs;
				return V_INT(BigInteger.valueOf(l.values.size()));
			} else if(mhs instanceof Set) {
				Set l = (Set) mhs;
				return V_INT(BigInteger.valueOf(l.values.size()));				
			}
			break;
		case PROCESSSPAWN:
		case PROCESSACCESS:
			return CExpr.UNOP(op,mhs);		
		}
		throw new IllegalArgumentException("Invalid operands to Value.evaluate(UOP,Value)");			
	}
	
	public static Boolean evaluate(Code.COP op, Value lhs, Value rhs) {
		Type lhs_t = lhs.type();
		Type rhs_t = rhs.type();
		Type lub = Type.leastUpperBound(lhs_t,rhs_t);
		
		if(lub instanceof Type.Int || lub instanceof Type.Real) {
			return evaluateArith(op,lhs,rhs);
		} else if(op == Code.COP.EQ) {
			return lhs.equals(rhs);
		} else if(op == Code.COP.NEQ) {
			return !lhs.equals(rhs);
		} else if (op == Code.COP.ELEMOF && rhs instanceof Value.Set) {
			Value.Set set = (Value.Set) rhs;
			return set.values.contains(lhs);
		} else if (op == Code.COP.ELEMOF && rhs instanceof Value.List) {
			Value.List list = (Value.List) rhs;
			return list.values.contains(lhs);
		} else if (op == Code.COP.SUBSET || op == Code.COP.SUBSETEQ) {
			return evaluateSet(op, lhs, rhs);
		} else if (op == Code.COP.SUBTYPEEQ) {
			TypeConst tc = (TypeConst) rhs;
			return Type.isSubtype(tc.type, lhs_t);
		} else if(rhs instanceof TypeConst) {
			TypeConst tc = (TypeConst) rhs;
			return !Type.isSubtype(tc.type,lhs_t);					
		} else {
			throw new IllegalArgumentException("Invalid operands to Value.evaluate(COP,Value,Value)");
		}
	}
	
	public static Boolean evaluateSet(Code.COP op, Value lhs, Value rhs) {
		Type lub = Type.leastUpperBound(lhs.type(),rhs.type());		
		Value.Set lv = (Value.Set) convert(lub,lhs); 
		Value.Set rv = (Value.Set) convert(lub,rhs);
		
		if(op == Code.COP.SUBSETEQ){			
			return rv.values.containsAll(lv.values);
		} else {
			return rv.values.containsAll(lv.values)
					&& rv.values.size() != lv.values.size();
		}
	}
	
	public static Boolean evaluateArith(Code.COP op, Value lhs, Value rhs) {		
		Type lub = Type.leastUpperBound(lhs.type(),rhs.type());		
		lhs = convert(lub,lhs);
		rhs = convert(lub,rhs);
		
		Comparable lv;
		Comparable rv;
		
		if(lub instanceof Type.Int) {
			lv = ((Int)lhs).value;
			rv = ((Int)rhs).value;			
		} else {
			lv = ((Real)lhs).value;
			rv = ((Real)rhs).value;			
		}		
		
		switch(op) {
		case LT:
			return lv.compareTo(rv) < 0;
		case LTEQ:
			return lv.compareTo(rv) <= 0;
		case GT:
			return lv.compareTo(rv) > 0;
		case GTEQ:
			return lv.compareTo(rv) >= 0;
		case EQ:
			return lv.equals(rv);
		case NEQ:
			return !lv.equals(rv);
		}
		
		throw new IllegalArgumentException("Invalid operands to Value.evaluateArith(COP,Value,Value)");
	}
	
	public static Value convert(Type t, Value val) {
		if (val.type().equals(t)) {
			return val;
		} else if (t instanceof Type.Real && val instanceof Int) {
			Int i = (Int) val;
			return new Real(new BigRational(i.value));
		} else if(t instanceof Type.Set) {
			Type.Set st = (Type.Set) t;
			if(val instanceof List) {
				List l = (List) val;
				ArrayList<Value> vs = new ArrayList<Value>();
				for(Value v : l.values) {
					vs.add(convert(st.element,v));
				}
				return V_SET(vs);
			} else if(val instanceof Set) {
				Set s = (Set) val;
				ArrayList<Value> vs = new ArrayList<Value>();
				for(Value v : s.values) {
					vs.add(convert(st.element,v));
				}
				return V_SET(vs);
			}
		} else if(t instanceof Type.List && val instanceof List) {
			Type.List st = (Type.List) t;			
			List l = (List) val;
			ArrayList<Value> vs = new ArrayList<Value>();
			for(Value v : l.values) {
				vs.add(convert(st.element,v));
			}
			return V_LIST(vs);			
		}
		return null;
	}

	public static final class Null extends Value {				
		public Type type() {
			return Type.T_NULL;
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
	}
	
	public static final class Bool extends Value {
		public final boolean value;
		private Bool(boolean value) {
			this.value = value;
		}
		public Type type() {
			return Type.T_BOOL;
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
		public String toString() {
			if(value) { return "true"; }
			else {
				return "false";
			}
		}
	}
	public static final class Int extends Value {
		public final BigInteger value;
		private Int(BigInteger value) {
			this.value = value;
		}
		public Type type() {
			return Type.T_INT;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Int) {
				Int i = (Int) o;
				return value.equals(i.value);
			}
			return false;
		}
		public String toString() {
			return value.toString();
		}
	}
	
	public static final class Real extends Value {
		public final BigRational value;
		private Real(BigRational value) {
			this.value = value;
		}
		public Type type() {
			return Type.T_REAL;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Int) {
				Int i = (Int) o;
				return value.equals(i.value);
			}
			return false;
		}
		public String toString() {
			return value.toString();
		}
	}
	
	public static class List extends Value {
		public final ArrayList<Value> values;
		private List(Collection<Value> value) {
			this.values = new ArrayList<Value>(value);
		}
		public Type type() {
			if(values.isEmpty()) {
				return Type.T_LIST(Type.T_VOID);
			} else {
				// FIXME: need to use lub here
				return Type.T_LIST(values.get(0).type());
			}
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
		public String toString() {
			String r = "[";
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
	}
	
	public static class Set extends Value {
		public final HashSet<Value> values;
		private Set(Collection<Value> value) {
			this.values = new HashSet<Value>(value);
		}
		public Type type() {
			if(values.isEmpty()) {
				return Type.T_SET(Type.T_VOID);
			} else {
				// FIXME: need to use lub here
				return Type.T_SET(values.iterator().next().type());
			}
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
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "}";
		}
	}
	
	public static class Record extends Value {
		public final HashMap<String,Value> values;
		private Record(Map<String,Value> value) {
			this.values = new HashMap<String,Value>(value);
		}

		public Type type() {
			HashMap<String, Type> types = new HashMap<String, Type>();
			for (Map.Entry<String, Value> e : values.entrySet()) {
				types.put(e.getKey(), e.getValue().type());
			}
			return Type.T_RECORD(types);
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
	
	public static class Dictionary extends Value {
		public final HashMap<Value,Value> values;
		private Dictionary(Map<Value,Value> value) {
			this.values = new HashMap<Value,Value>(value);
		}
		private Dictionary(java.util.Set<Pair<Value,Value>> values) {
			this.values = new HashMap<Value,Value>();
			for(Pair<Value,Value> p : values) {
				this.values.put(p.first(), p.second());
			}
		}
		public Type type() {
			Type key = Type.T_VOID;
			Type value = Type.T_VOID;
			for (Map.Entry<Value, Value> e : values.entrySet()) {
				key = Type.leastUpperBound(key,e.getKey().type());
				value = Type.leastUpperBound(value,e.getKey().type());
			}
			return Type.T_DICTIONARY(key,value);
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Dictionary) {
				Dictionary i = (Dictionary) o;
				return values.equals(i.values);
			}
			return false;
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			ArrayList<String> keystr = new ArrayList<String>();
			HashMap<String,Value> keymap = new HashMap<String,Value>();
			for(Value key : values.keySet()) {
				keystr.add(key.toString());
				keymap.put(key.toString(), key);
			}
			Collections.sort(keystr);
			for(String key : keystr) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				Value k = keymap.get(key); 
				r += k + "->" + values.get(k);
			}
			return r + "}";
		}
	}
	
	public static final class TypeConst extends Value {
		public final Type type;
		private TypeConst(Type type) {
			this.type = type;
		}
		public Type type() {
			return Type.T_META;
		}
		public int hashCode() {
			return type.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof TypeConst) {
				TypeConst i = (TypeConst) o;
				return type == i.type;
			}
			return false;
		}
		public String toString() {
			return type.toString();
		}
	}
	private static final ArrayList<Value> values = new ArrayList<Value>();
	private static final HashMap<Value,Integer> cache = new HashMap<Value,Integer>();
	
	private static <T extends Value> T get(T type) {
		Integer idx = cache.get(type);
		if(idx != null) {
			return (T) values.get(idx);
		} else {					
			cache.put(type, values.size());
			values.add(type);
			return type;
		}
	}
}
