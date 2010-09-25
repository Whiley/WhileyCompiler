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

import java.util.*;

public abstract class CExpr {
	public abstract Type type();
	
	public static abstract class LVal extends CExpr {}
	public static abstract class LVar extends LVal {
		public abstract String name();
	}
	
	public static <T> void match(CExpr r, Class<T> match, Collection<T> uses) {
		if(match.isInstance(r)) {
			uses.add((T)r);
		}
		if (r instanceof Variable) {
			
		} else if(r instanceof Register) {
			
		} else if(r instanceof ListAccess) {
			ListAccess v = (ListAccess) r;
			match(v.src,match,uses);
			match(v.index,match,uses);
		} else if(r instanceof BinOp) {
			BinOp v = (BinOp) r;
			match(v.lhs,match,uses);
			match(v.rhs,match,uses);
		} else if(r instanceof UnOp) {
			UnOp v = (UnOp) r;			
			match(v.rhs,match,uses);
		} else if(r instanceof NaryOp) {
			NaryOp v = (NaryOp) r;
			for(CExpr arg : v.args) {
				match(arg,match,uses);
			}
		} else if (r instanceof Record) {
			Record tup = (Record) r;			
			for(Map.Entry<String,CExpr> e : tup.values.entrySet()) {
				match(e.getValue(),match,uses);				
			}			
		} else if (r instanceof RecordAccess) {
			RecordAccess ta = (RecordAccess) r;
			match(ta.lhs,match,uses);
		} else if(r instanceof Invoke) {
			Invoke a = (Invoke) r;			
			if(a.receiver != null) {
				CExpr.match(a.receiver, match, uses);
			}
			for(CExpr arg : a.args){
				CExpr.match(arg,match,uses);
			}			
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
			CExpr rv = binding.get(v.name());
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
		} else if (r instanceof NaryOp) {
			NaryOp bop = (NaryOp) r;
			ArrayList<CExpr> args = new ArrayList<CExpr>();
			for(CExpr arg : bop.args) {
				args.add(substitute(binding, arg));
			}
			return NARYOP(bop.op, args);
		} else if (r instanceof Record) {
			Record tup = (Record) r;
			HashMap<String,CExpr> values = new HashMap<String,CExpr>();
			for(Map.Entry<String,CExpr> e : tup.values.entrySet()) {
				values.put(e.getKey(),substitute(binding, e.getValue()));				
			}
			return RECORD(values);
		} else if (r instanceof RecordAccess) {
			RecordAccess ta = (RecordAccess) r;
			return RECORDACCESS(substitute(binding, ta.lhs),
					ta.field);
		} else if(r instanceof Invoke) {
			Invoke a = (Invoke) r;									
			ArrayList<CExpr> args = new ArrayList<CExpr>();
			CExpr receiver = a.receiver;
			if(receiver != null) {
				receiver = CExpr.substitute(binding,receiver);
			}
			for(CExpr arg : a.args){
				args.add(CExpr.substitute(binding,arg));
			}			
			return INVOKE(a.type,a.name,a.caseNum,receiver,args);
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
		} else if (r instanceof NaryOp) {
			NaryOp bop = (NaryOp) r;
			ArrayList<CExpr> args = new ArrayList<CExpr>();
			for(CExpr arg : bop.args) {
				args.add(registerShift(shift, arg));
			}
			return NARYOP(bop.op, args);
		} else if (r instanceof Record) {
			Record tup = (Record) r;
			HashMap<String,CExpr> values = new HashMap<String,CExpr>();
			for(Map.Entry<String,CExpr> e : tup.values.entrySet()) {
				values.put(e.getKey(),registerShift(shift, e.getValue()));				
			}
			return RECORD(values);
		} else if (r instanceof RecordAccess) {
			RecordAccess ta = (RecordAccess) r;
			return RECORDACCESS(registerShift(shift, ta.lhs),
					ta.field);
		} else if(r instanceof Invoke) {
			Invoke a = (Invoke) r;						
			ArrayList<CExpr> args = new ArrayList<CExpr>();
			CExpr receiver = a.receiver;
			if(receiver != null) {
				receiver = registerShift(shift,receiver);
			}
			for(CExpr arg : a.args){
				args.add(CExpr.registerShift(shift,arg));
			}						
			return INVOKE(a.type,a.name,a.caseNum,receiver,args);
		} else {
			throw new IllegalArgumentException("Invalid CExpr: " + r);
		}
		return r;
	}

	/**
	 * This method accepts a collection of registers, identifies the highest
	 * numbered register it contains, and returns its index. In this way, we can
	 * identify the index above which all registers slots are unused.
	 * 
	 * @param registers
	 * @return --- the highest numbered register, or -1 if empty collection.
	 *         Thus, it's always the case that adding one to the return value
	 *         gives you an unused register and, furthermore, all indices above
	 *         it are also guarnteed free.
	 */
	public static int maxRegister(Collection<Register> registers) {
		int max = -1;
		for(Register r : registers) {
			max = Math.max(max, r.index);
		}
		return max;
	}

	/**
	 * This method accepts an expression which is used as the lhs of an
	 * assignment. It returns the LVar which is actually updated by the
	 * assignment.
	 * 
	 * @param lhs
	 * @return
	 */
	public static LVar extractLVar(CExpr lhs) {
		if(lhs instanceof LVar) {
			return (LVar) lhs;
		} else if(lhs instanceof ListAccess) {
			ListAccess la = (ListAccess) lhs;
			return extractLVar(la.src);
		} else if(lhs instanceof RecordAccess) {
			RecordAccess la = (RecordAccess) lhs;
			return extractLVar(la.lhs);
		} else if(lhs instanceof UnOp) {
			UnOp la = (UnOp) lhs;
			if(la.op == CExpr.UOP.PROCESSACCESS){ 
				return extractLVar((LVal) la.rhs);
			}
		}
		return null;
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
	
	public static NaryOp NARYOP(NOP nop, CExpr... args) {
		return get(new NaryOp(nop, args));
	}
	
	public static NaryOp NARYOP(NOP nop, Collection<CExpr> args) {
		return get(new NaryOp(nop, args));
	}
	
	public static Record RECORD(Map<String,CExpr> values) {
		return get(new Record(values));
	}
	
	public static RecordAccess RECORDACCESS(CExpr lhs, String field) {
		return get(new RecordAccess(lhs,field));
	}
	
	public static Invoke INVOKE(Type.Fun type, NameID name, int casenum,
			CExpr receiver, CExpr... args) {
		return get(new Invoke(type,name,casenum,receiver,args));
	}
	

	public static Invoke INVOKE(Type.Fun type, NameID name, int casenum,
			CExpr receiver, Collection<CExpr> args) {
		return get(new Invoke(type,name,casenum,receiver,args));
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
		public String name() {
			return name;
		}
		public boolean equals(Object o) {
			if(o instanceof Variable) {
				Variable v = (Variable) o;
				return type.equals(v.type) && name.equals(v.name);
			}
			return false;
		}
		public String toString() {
			return name + "!" + type;
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
		public String name() {
			return "%" + index;
		}
		public String toString() {			
			return "%" + index + "!" + type;			
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
			Type t = src.type();
			if(t instanceof Type.List){ 
				Type.List l = (Type.List) t;
				return l.element;
			} else {
				return Type.T_VOID;
			}
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
			public String toString() { return "++"; }
		}
	};	
	
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
			} else if(op == UOP.PROCESSACCESS) {
				// FIXME: need to flattern
				Type.Process pt = (Type.Process) rhs.type();
				return pt.element;
			} else if(op == UOP.PROCESSSPAWN) {
				return Type.T_PROCESS(rhs.type());				
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
		LENGTHOF() {
			public String toString() { return "||"; }
		},
		PROCESSACCESS() {
			public String toString() { return "*"; }
		},
		PROCESSSPAWN() {
			public String toString() { return "spawn "; }
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
			if(op == NOP.SUBLIST) {
				return args.get(0).type();
			} else {
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
	
	public final static class Record extends CExpr {			
		public final Map<String,CExpr> values;		
		
		Record(Map<String,CExpr> values) {
			this.values = Collections.unmodifiableMap(values); 
		}
		
		public Type type() {
			HashMap<String,Type> types = new HashMap<String,Type>();
			for(Map.Entry<String,CExpr> e : values.entrySet()) {
				Type t = e.getValue().type();
				types.put(e.getKey(), t);
			}
			return Type.T_RECORD(types);
		}
		
		public boolean equals(Object o) {
			if(o instanceof Record) {
				Record a = (Record) o;
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
			boolean firstTime=true;
			for(String key : keys) {
				if(!firstTime) {
					r += ", ";
				}
				firstTime=false;
				r += key + ":" + values.get(key);
			}
			return r + ")";
		}
	}
	
	public final static class RecordAccess extends LVal {		
		public final CExpr lhs;
		public final String field;

		RecordAccess(CExpr lhs, String field) {			
			this.lhs = lhs;
			this.field = field;
		}

		public Type type() {					
			Type.Record tt = Type.effectiveRecordType(lhs.type());			
			if(tt == null) {
				return Type.T_VOID;
			}
			Type r = tt.types.get(field);
			if(r == null) {
				return Type.T_VOID;
			} else {
				return r;
			}
		}

		public boolean equals(Object o) {
			if (o instanceof RecordAccess) {
				RecordAccess a = (RecordAccess) o;
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
	
	public final static class Invoke extends CExpr {
		public final Type.Fun type;
		public final NameID name;
		public final CExpr receiver;
		public final List<CExpr> args;
		public final int caseNum;

		Invoke(Type.Fun type, NameID name, int caseNum, CExpr receiver, CExpr... args) {
			this.type = type;
			this.name = name;
			this.caseNum = caseNum;
			this.receiver = receiver;
			ArrayList<CExpr> tmp = new ArrayList<CExpr>();
			for(CExpr r : args) {
				tmp.add(r);
			}
			this.args = Collections.unmodifiableList(tmp); 
		}

		Invoke(Type.Fun type, NameID name, int caseNum,
				CExpr receiver, Collection<CExpr> args) {
			this.type = type;
			this.name = name;
			this.caseNum = caseNum;
			this.receiver = receiver;
			this.args = Collections.unmodifiableList(new ArrayList<CExpr>(args));
		}

		public Type type() {
			return type.ret;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Invoke) {
				Invoke a = (Invoke) o;
				if(receiver == null) {
					return a.receiver == null && type.equals(a.type)
							&& name.equals(a.name) && caseNum == a.caseNum
							&& args.equals(a.args);
				} else {
					return type.equals(a.type) && name.equals(a.name)
							&& caseNum == a.caseNum && args.equals(a.args);
				}
			}
			return false;
		}

		public int hashCode() {			
			return name.hashCode() + caseNum + type.hashCode() + args.hashCode();			
		}

		public String toString() {
			String rhs = "";
			boolean firstTime = true;
			for (CExpr v : args) {
				if (!firstTime) {
					rhs += ",";
				}
				firstTime = false;
				rhs += v;
			}
			String n = name.toString();
			if(caseNum > 0) {
				n += "$" + caseNum;
			}			
			if(receiver == null) {
				return n + "(" + rhs + ")!" + type;
			} else {
				return receiver + "->" + n + "(" + rhs + ")!" + type;
			}
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
