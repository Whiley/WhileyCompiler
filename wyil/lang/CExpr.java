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
		}
	}
	
	/**
	 * Substitute all occurrences of variable from with variable to.
	 * 
	 * @param c
	 * @param uses
	 */
	public static CExpr substitute(HashMap<String,CExpr> binding, CExpr r) {
		if (r instanceof Variable) {
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
			return LISTACCESS(la.type, (LVal) substitute(binding, la.src),
					substitute(binding, la.index));
		} else if (r instanceof BinOp) {
			BinOp bop = (BinOp) r;
			return BINOP(bop.type, bop.op, substitute(binding, bop.lhs),
					substitute(binding, bop.rhs));
		}
		return r;
	}
		
	public static CExpr registerShift(int shift, CExpr r) {
		if (r instanceof Register) {
			Register v = (Register) r;
			return new Register(v.type,v.index + shift);
		}
		return r;
	}
	
	public static Variable VAR(Type t, String v) {
		return get(new Variable(t,v));
	}
	
	public static Register REG(Type t, int index) {
		return get(new Register(t,index));
	}
	
	public static ListAccess LISTACCESS(Type.List t, CExpr src, CExpr index) {
		return get(new ListAccess(t, src, index));
	}

	public static BinOp BINOP(Type t, BOP bop, CExpr lhs, CExpr rhs) {
		return get(new BinOp(t, bop, lhs, rhs));
	}
	
	public static UnOp UNOP(Type t, UOP uop, CExpr mhs) {
		return get(new UnOp(t, uop, mhs));
	}
	
	public static NaryOp NARYOP(Type t, NOP nop, CExpr... args) {
		return get(new NaryOp(t, nop, args));
	}
	
	public static NaryOp NARYOP(Type t, NOP nop, Collection<CExpr> args) {
		return get(new NaryOp(t, nop, args));
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
			return "(" + type + ") " + name;
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
			return "(" + type + ") %" + index;
		}
	}
	
	public static class ListAccess extends LVal {
		public final Type.List type;
		public final CExpr src;
		public final CExpr index;

		ListAccess(Type.List type, CExpr src, CExpr index) {
			this.type = type;
			this.src = src;
			this.index = index;
		}

		public Type type() {
			return type.element;
		}

		public int hashCode() {
			return type.hashCode() + src.hashCode() + index.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof ListAccess) {
				ListAccess v = (ListAccess) o;
				return type.equals(v.type) && src.equals(v.src)
						&& index.equals(v.index);
			}
			return false;
		}

		public String toString() {
			return "(" + type + ") " + src + "[" + index + "]";
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
		public final Type type;
		public final CExpr lhs;
		public final CExpr rhs;

		BinOp(Type type, BOP op, CExpr lhs, CExpr rhs) {
			this.op = op;
			this.type = type;
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public Type type() {
			return type;
		}

		public boolean equals(Object o) {
			if (o instanceof BinOp) {
				BinOp a = (BinOp) o;
				return op == a.op && type.equals(a.type) && lhs.equals(a.lhs)
						&& rhs.equals(a.rhs);

			}
			return false;
		}

		public int hashCode() {
			return op.hashCode() + type.hashCode() + lhs.hashCode()
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
		}
	};	
	
	public final static class UnOp extends CExpr {
		public final UOP op;		
		public final Type type;
		public final CExpr rhs;		
		
		UnOp(Type type, UOP op, CExpr rhs) {
			this.op = op;			
			this.type = type;
			this.rhs = rhs;
		}
		
		public Type type() {
			return type;
		}
		
		public boolean equals(Object o) {
			if (o instanceof UnOp) {
				UnOp a = (UnOp) o;
				return op == a.op && type.equals(a.type) && rhs.equals(a.rhs);
			}
			return false;
		}
		
		public int hashCode() {
			return op.hashCode() + type.hashCode()
					+ rhs.hashCode();
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
		public final Type type;
		public final List<CExpr> args;		
		
		NaryOp(Type type, NOP op, CExpr... args) {
			this.op = op;			
			this.type = type;
			ArrayList<CExpr> tmp = new ArrayList<CExpr>();
			for(CExpr r : args) {
				tmp.add(r);
			}
			this.args = Collections.unmodifiableList(tmp); 
		}
		
		public NaryOp(Type type, NOP op, Collection<CExpr> args) {
			this.op = op;			
			this.type = type;
			this.args = Collections.unmodifiableList(new ArrayList<CExpr>(args));			
		}
		
		public Type type() {
			return type;
		}
		
		public boolean equals(Object o) {
			if(o instanceof NaryOp) {
				NaryOp a = (NaryOp) o;
				return op == a.op && type.equals(a.type)
						&& args.equals(a.args);
				
			}
			return false;
		}
		
		public int hashCode() {
			return op.hashCode() + type.hashCode()
					+ args.hashCode();
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
