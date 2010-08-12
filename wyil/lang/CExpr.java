package wyil.lang;

import java.util.*;

public abstract class CExpr {
	public abstract Type type();
	
	public static abstract class LVal extends CExpr {}
	public static abstract class LVar extends LVal {}
	
	public static void usedVariables(CExpr r, Set<String> uses) {
		if (r instanceof Variable) {
			Variable v = (Variable) r;
			uses.add(v.name);
		} else if(r instanceof Register) {
			Register v = (Register) r;
			uses.add("%" + v.index);
		} else if(r instanceof ListAccess) {
			ListAccess v = (ListAccess) r;
			usedVariables(v.src,uses);
			usedVariables(v.index,uses);
		} else if(r instanceof BinOp) {
			BinOp v = (BinOp) r;
			usedVariables(v.lhs,uses);
			usedVariables(v.rhs,uses);
		} else if(r instanceof UnOp) {
			UnOp v = (UnOp) r;			
			usedVariables(v.rhs,uses);
		} else if(r instanceof NaryOp) {
			NaryOp v = (NaryOp) r;
			for(CExpr arg : v.args) {
				usedVariables(arg,uses);
			}
		} else if (r instanceof Tuple) {
			Tuple tup = (Tuple) r;			
			for(Map.Entry<String,CExpr> e : tup.values.entrySet()) {
				usedVariables(e.getValue(),uses);				
			}			
		} else if (r instanceof TupleAccess) {
			TupleAccess ta = (TupleAccess) r;
			usedVariables(ta.lhs,uses);
		} else if(r instanceof Convert) {
			Convert v = (Convert) r;			
			usedVariables(v.rhs,uses);
		} else if(r instanceof Value) {
			
		} else {
			throw new IllegalArgumentException("Unknown expression encountered: " + r);
		}
	}
	
	/**
	 * Substitute all occurrences of variable from with variable to.
	 * 
	 * @param c
	 * @param uses
	 */
	public static CExpr substitute(HashMap<String,CExpr> binding, CExpr r) {
		if(r instanceof Value) {
			
		} else if (r instanceof Variable) {
			Variable v = (Variable) r;
			CExpr rv = binding.get(v.name);
			if (rv != null) {
				return rv;
			} 
		} else if (r instanceof Register) {
			Register v = (Register) r;
			// FIXME: should changing the type of binding
			CExpr rv = binding.get(v.toString());
			if (rv != null) {
				return rv;
			} 
		} else if(r instanceof ListAccess) {
			ListAccess la = (ListAccess) r;
			return LISTACCESS(substitute(binding, la.src),
					substitute(binding, la.index));
		} else if (r instanceof BinOp) {
			BinOp bop = (BinOp) r;
			return BINOP(bop.op, substitute(binding, bop.lhs),
					substitute(binding, bop.rhs));
		} else if (r instanceof UnOp) {
			UnOp bop = (UnOp) r;
			return UNOP(bop.op,substitute(binding, bop.rhs));
		} else if (r instanceof Convert) {
			Convert c = (Convert) r;
			return CONVERT(c.type,substitute(binding, c.rhs));
		} else if (r instanceof NaryOp) {
			NaryOp bop = (NaryOp) r;
			ArrayList<CExpr> args = new ArrayList<CExpr>();
			for(CExpr arg : bop.args) {
				args.add(substitute(binding, arg));
			}
			return NARYOP(bop.op, args);
		} else if (r instanceof Tuple) {
			Tuple tup = (Tuple) r;
			HashMap<String,CExpr> values = new HashMap<String,CExpr>();
			for(Map.Entry<String,CExpr> e : tup.values.entrySet()) {
				values.put(e.getKey(),substitute(binding, e.getValue()));				
			}
			return TUPLE(values);
		} else if (r instanceof TupleAccess) {
			TupleAccess ta = (TupleAccess) r;
			return TUPLEACCESS(substitute(binding, ta.lhs),
					ta.field);
		} else {
			throw new IllegalArgumentException("Invalid CExpr: " + r);
		}
		
		return r;
	}
		
	public static CExpr registerShift(int shift, CExpr r) {
		if(r instanceof Variable || r instanceof Value) {
			
		} else if (r instanceof Register) {
			Register v = (Register) r;
			return new Register(v.type,v.index + shift);
		} else if(r instanceof ListAccess) {
			ListAccess la = (ListAccess) r;
			return LISTACCESS(registerShift(shift, la.src),
					registerShift(shift, la.index));
		} else if (r instanceof BinOp) {
			BinOp bop = (BinOp) r;
			return BINOP(bop.op, registerShift(shift, bop.lhs),
					registerShift(shift, bop.rhs));
		} else if (r instanceof UnOp) {
			UnOp bop = (UnOp) r;
			return UNOP(bop.op,registerShift(shift, bop.rhs));
		} else if (r instanceof Convert) {
			Convert c = (Convert) r;
			return CONVERT(c.type,registerShift(shift, c.rhs));
		} else if (r instanceof NaryOp) {
			NaryOp bop = (NaryOp) r;
			ArrayList<CExpr> args = new ArrayList<CExpr>();
			for(CExpr arg : bop.args) {
				args.add(registerShift(shift, arg));
			}
			return NARYOP(bop.op, args);
		} else if (r instanceof Tuple) {
			Tuple tup = (Tuple) r;
			HashMap<String,CExpr> values = new HashMap<String,CExpr>();
			for(Map.Entry<String,CExpr> e : tup.values.entrySet()) {
				values.put(e.getKey(),registerShift(shift, e.getValue()));				
			}
			return TUPLE(values);
		} else if (r instanceof TupleAccess) {
			TupleAccess ta = (TupleAccess) r;
			return TUPLEACCESS(registerShift(shift, ta.lhs),
					ta.field);
		} else {
			throw new IllegalArgumentException("Invalid CExpr: " + r);
		}
		return r;
	}
	
	public static Variable VAR(Type t, String v) {
		return get(new Variable(t,v));
	}
	
	public static Register REG(Type t, int index) {
		return get(new Register(t,index));
	}
	
	public static ListAccess LISTACCESS(CExpr src, CExpr index) {
		return get(new ListAccess(src, index));
	}

	public static BinOp BINOP(BOP bop, CExpr lhs, CExpr rhs) {
		return get(new BinOp(bop, lhs, rhs));
	}
	
	public static UnOp UNOP(UOP uop, CExpr mhs) {
		return get(new UnOp(uop, mhs));
	}
	
	public static Convert CONVERT(Type t, CExpr mhs) {
		return get(new Convert(t, mhs));
	}
	
	public static NaryOp NARYOP(NOP nop, CExpr... args) {
		return get(new NaryOp(nop, args));
	}
	
	public static NaryOp NARYOP(NOP nop, Collection<CExpr> args) {
		return get(new NaryOp(nop, args));
	}
	
	public static Tuple TUPLE(Map<String,CExpr> values) {
		return get(new Tuple(values));
	}
	
	public static TupleAccess TUPLEACCESS(CExpr lhs, String field) {
		return get(new TupleAccess(lhs,field));
	}
	
	public static class Variable extends LVar {
		public final String name;
		public final Type type;
		
		Variable(Type type, String name) {
			this.name = name;
			this.type = type;
			if (this.name.contains("%")) {
				throw new IllegalArgumentException(
						"wyil.lang.RVal.Variable name cannot contain \"$\" --- reserved for registers");
			}
		}
		public Type type() {
			return type;
		}
		public int hashCode() {
			return type.hashCode() + name.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Variable) {
				Variable v = (Variable) o;
				return type.equals(v.type) && name.equals(v.name);
			}
			return false;
		}
		public String toString() {
			return name + "[" + type + "]";
		}
	}

	/**
	 * A register is different from a variable because it's explicitly
	 * designated as temporary. In contract, a Variable corresponds to a program
	 * variable from the original source language.
	 * 
	 * @author djp
	 * 
	 */
	public static class Register extends LVar {
		public final int index;
		public final Type type;
		
		Register(Type type, int index) {
			this.index = index;
			this.type = type;
		}
		public Type type() {
			return type;
		}
		public int hashCode() {
			return type.hashCode() + index;
		}
		public boolean equals(Object o) {
			if(o instanceof Register) {
				Register v = (Register) o;
				return type.equals(v.type) && index == v.index;
			}
			return false;
		}
		public String toString() {
			return "%" + index + "[" + type + "]";			
		}
	}
	
	public static class ListAccess extends LVal {		
		public final CExpr src;
		public final CExpr index;

		ListAccess(CExpr src, CExpr index) {			
			this.src = src;
			this.index = index;
		}

		public Type type() {
			Type.List l = (Type.List) src.type();
			return l.element;
		}

		public int hashCode() {
			return src.hashCode() + index.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof ListAccess) {
				ListAccess v = (ListAccess) o;
				return src.equals(v.src)
						&& index.equals(v.index);
			}
			return false;
		}

		public String toString() {
			return src + "[" + index + "]";
		}
	}
	
	/**
	 * This represents a simple assignment between two variables.
	 * 
	 * @author djp
	 * 
	 */
	public final static class BinOp extends CExpr {
		public final BOP op;
		public final CExpr lhs;
		public final CExpr rhs;

		BinOp(BOP op, CExpr lhs, CExpr rhs) {
			this.op = op;			
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public Type type() {
			return Type.leastUpperBound(lhs.type(),rhs.type());
		}

		public boolean equals(Object o) {
			if (o instanceof BinOp) {
				BinOp a = (BinOp) o;
				return op == a.op && lhs.equals(a.lhs)
						&& rhs.equals(a.rhs);

			}
			return false;
		}

		public int hashCode() {
			return op.hashCode() + lhs.hashCode()
					+ rhs.hashCode();
		}

		public String toString() {
			return lhs + " " + op + " " + rhs;
		}
	}
	
	public enum BOP { 
		ADD{
			public String toString() { return "+"; }
		},
		SUB{
			public String toString() { return "-"; }
		},
		MUL{
			public String toString() { return "*"; }
		},
		DIV{
			public String toString() { return "/"; }
		},
		UNION{
			public String toString() { return "+"; }
		},
		INTERSECT{
			public String toString() { return "&"; }
		},
		DIFFERENCE{
			public String toString() { return "-"; }
		},
		APPEND{
			public String toString() { return "+"; }
		}
	};	
	
	public final static class Convert extends CExpr {		
		public final Type type;
		public final CExpr rhs;		
		
		Convert(Type type, CExpr rhs) {				
			this.type = type;
			this.rhs = rhs;
		}
		
		public Type type() {
			return type;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Convert) {
				Convert a = (Convert) o;
				return type.equals(a.type) && rhs.equals(a.rhs);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode() + rhs.hashCode();
		}
		
		public String toString() {
			return "(" + type + ") " + rhs;
		}		
	}
	
	public final static class UnOp extends CExpr {
		public final UOP op;				
		public final CExpr rhs;		
		
		UnOp(UOP op, CExpr rhs) {
			this.op = op;						
			this.rhs = rhs;
		}
		
		public Type type() {
			if(op == UOP.LENGTHOF) {
				return Type.T_INT;
			} else {
				return rhs.type();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof UnOp) {
				UnOp a = (UnOp) o;
				return op == a.op && rhs.equals(a.rhs);
			}
			return false;
		}
		
		public int hashCode() {
			return op.hashCode() + rhs.hashCode();
		}
		
		public String toString() {
			if(op == UOP.LENGTHOF){
				return "|" + rhs + "|";
			} else {
				return op.toString() + rhs;
			}
		}		
	}
	
	public enum UOP { 
		NEG() {
			public String toString() { return "-"; }
		},
		NOT() {
			public String toString() { return "!"; }
		},
		LENGTHOF() {
			public String toString() { return "||"; }
		}
	}
	
	public final static class NaryOp extends CExpr {
		public final NOP op;		
		public final List<CExpr> args;		
		
		NaryOp(NOP op, CExpr... args) {
			this.op = op;						
			ArrayList<CExpr> tmp = new ArrayList<CExpr>();
			for(CExpr r : args) {
				tmp.add(r);
			}
			this.args = Collections.unmodifiableList(tmp); 
		}
		
		public NaryOp(NOP op, Collection<CExpr> args) {
			this.op = op;			
			this.args = Collections.unmodifiableList(new ArrayList<CExpr>(args));			
		}
		
		public Type type() {
			Type t = Type.T_VOID;
			for(CExpr arg : args) {
				t = Type.leastUpperBound(t,arg.type());
			}
			if(op == NOP.SETGEN){
				return Type.T_SET(t);
			} else {
				return Type.T_LIST(t);
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof NaryOp) {
				NaryOp a = (NaryOp) o;
				return op == a.op && args.equals(a.args);
				
			}
			return false;
		}
		
		public int hashCode() {
			return op.hashCode() + args.hashCode();
		}
		
		public String toString() {
			String rhs = "";
			switch (op) {
			case SETGEN: {
				rhs += "{";
				boolean firstTime = true;
				for (CExpr r : args) {
					if (!firstTime) {
						rhs += ",";
					}
					firstTime = false;
					rhs += r;
				}
				rhs += "}";
				break;
			}
			case LISTGEN: {
				rhs += "[";
				boolean firstTime = true;
				for (CExpr r : args) {
					if (!firstTime) {
						rhs += ",";
					}
					firstTime = false;
					rhs += r;
				}
				rhs += "]";
				break;
			}
			case SUBLIST:
				rhs += args.get(0) + "[" + args.get(1) + ":" + args.get(2)
						+ "]";
				break;
			}
			return rhs;
		}
	}
	
	public enum NOP { 
		SETGEN,
		LISTGEN,
		SUBLIST
	}
	
	public final static class Tuple extends CExpr {			
		public final Map<String,CExpr> values;		
		
		Tuple(Map<String,CExpr> values) {
			this.values = Collections.unmodifiableMap(values); 
		}
		
		public Type type() {
			HashMap<String,Type> types = new HashMap<String,Type>();
			for(Map.Entry<String,CExpr> e : values.entrySet()) {
				Type t = e.getValue().type();
				types.put(e.getKey(), t);
			}
			return Type.T_TUPLE(types);
		}
		
		public boolean equals(Object o) {
			if(o instanceof Tuple) {
				Tuple a = (Tuple) o;
				return values.equals(a.values);
				
			}
			return false;
		}
		
		public int hashCode() {
			return values.hashCode();
		}
		
		public String toString() {
			String r = "(";
			ArrayList<String> keys = new ArrayList<String>(values.keySet());
			Collections.sort(keys);
			for(String key : keys) {
				r += key + ":" + values.get(key);
			}
			return r + ")";
		}
	}
	
	public final static class TupleAccess extends LVal {		
		public final CExpr lhs;
		public final String field;

		TupleAccess(CExpr lhs, String field) {			
			this.lhs = lhs;
			this.field = field;
		}

		public Type type() {
			Type t = lhs.type();
			// FIXME: bugs here for effective tuple types
			Type.Tuple tt = (Type.Tuple) t;
			Type r = tt.types.get(field);
			if(r == null) {
				return Type.T_VOID;
			} else {
				return r;
			}
		}

		public boolean equals(Object o) {
			if (o instanceof TupleAccess) {
				TupleAccess a = (TupleAccess) o;
				return lhs.equals(a.lhs)
						&& field.equals(a.field);

			}
			return false;
		}

		public int hashCode() {
			return field.hashCode() + lhs.hashCode();
		}

		public String toString() {
			return lhs + "." + field;
		}
	}
		
	private static final ArrayList<CExpr> values = new ArrayList<CExpr>();
	private static final HashMap<CExpr,Integer> cache = new HashMap<CExpr,Integer>();
	
	private static <T extends CExpr> T get(T type) {
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
