package wyil.lang;

import java.util.*;

public abstract class CExpr {
	public abstract Type type();
	
	public static abstract class LVal extends CExpr {}
	
	public static void usedVariables(CExpr r, Set<String> uses) {
		if (r instanceof Variable) {
			Variable v = (Variable) r;
			uses.add(v.name);
		} else if(r instanceof Register) {
			Register v = (Register) r;
			uses.add("%" + v.index);
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
	
	public static class Variable extends LVal {
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
	public static class Register extends LVal {
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
