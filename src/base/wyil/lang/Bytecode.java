package wyil.lang;

public abstract class Bytecode {
	
	public enum BOp { 
		ADD{
			public String toString() { return "add"; }
		},
		SUB{
			public String toString() { return "sub"; }
		},
		MUL{
			public String toString() { return "mul"; }
		},
		DIV{
			public String toString() { return "div"; }
		},
		REM{
			public String toString() { return "rem"; }
		},
		UNION{
			public String toString() { return "union"; }
		},
		INTERSECT{
			public String toString() { return "intersect"; }
		},
		DIFFERENCE{
			public String toString() { return "difference"; }
		},
		APPEND{
			public String toString() { return "append"; }
		},
		RANGE{
			public String toString() { return "range"; }
		},
		AND{
			public String toString() { return "and"; }
		},
		OR{
			public String toString() { return "or"; }
		},
		XOR{
			public String toString() { return "xor"; }
		}		
	};

	/**
	 * A binary operation (e.g. +,-,*,/) takes two items off the stack and
	 * pushes a single result.
	 * 
	 * @author djp
	 * 
	 */
	public static final class BinOp extends Bytecode {		
		public final BOp bop;
		public final Type type;
		
		public BinOp(BOp bop, Type type) {
			this.bop = bop;
			this.type = type;
		}
		
		public int hashCode() {
			return type.hashCode() + bop.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof BinOp) {
				BinOp bo = (BinOp) o;
				return type.equals(bo.type) && bop.equals(bo.bop); 
			}
			return false;
		}
				
		public String toString() {
			return bop + " " + type;
		}
	}

	/**
	 * A catch bytecode is similar to a switch. It identifies a block within
	 * which exception handlers are active.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Catch extends Bytecode {
		
	}

	/**
	 * A convert operation represents a conversion from a value of one type to
	 * another. The purpose of the conversion bytecode is to make it easy for
	 * implementations which have different representations of data types to
	 * convert between them.
	 */
	public static final class Convert extends Bytecode {
		public final Type from;
		public final Type to;
		
		public Convert(Type from, Type to) {
			this.from = from;
			this.to = to;
		}
		
		public int hashCode() {
			return from.hashCode() + to.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Convert) {
				Convert c = (Convert) o;
				return from.equals(c.from) && to.equals(c.to);  
			}
			return false;
		}
				
		public String toString() {
			return "convert " + from + " => " + to;
		}
	}
	
	/**
	 * A const bytecode pushes a constant value onto the stack. This includes
	 * integer constants, rational constants, list constants, set constants,
	 * dictionary constants, function constants, etc.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Const extends Bytecode {		
		public final Value constant;
		
		public Const(Value constant) {
			this.constant = constant;
		}
		
		public int hashCode() {
			return constant.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Const) {
				Const c = (Const) o;
				return constant.equals(c.constant);  
			}
			return false;
		}
		
		public String toString() {
			return "const " + constant;
		}
	}
	
	public static final class Goto extends Bytecode {
		public final String target;
		
		public Goto(String target) {
			this.target = target;
		}
		
		public int hashCode() {
			return target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Goto) {
				return target.equals(((Goto)o).target);
			}
			return false;
		}
		
		public String toString() {
			return "goto " + target;
		}		
	}
	
	public static final class IfGoto extends Bytecode {
		public final COp op;
		public final String target;

		public IfGoto(COp op, String target) {
			this.op = op;						
			this.target = target;
		}
		
		public int hashCode() {
			return op.hashCode() + target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof IfGoto) {
				IfGoto ig = (IfGoto) o;
				return op == ig.op 						
						&& target.equals(ig.target);
			}
			return false;
		}
	
		public String toString() {
			return "if" + op + " " + target;
		}
	}
	
	public enum COp { 
		EQ() {
			public String toString() { return "=="; }
		},
		NEQ{
			public String toString() { return "!="; }
		},
		LT{
			public String toString() { return "<"; }
		},
		LTEQ{
			public String toString() { return "<="; }
		},
		GT{
			public String toString() { return ">"; }
		},
		GTEQ{
			public String toString() { return ">="; }
		},
		ELEMOF{
			public String toString() { return "in"; }
		},
		SUBSET{
			public String toString() { return "<"; }
		},
		SUBSETEQ{
			public String toString() { return "<="; }
		},
		SUBTYPEEQ() {
			public String toString() { return "<:"; }
		},
		NSUBTYPEEQ() {
			public String toString() { return "<!"; }
		}
	};		
		
	public static final class IndirectLoad extends Bytecode {
		public final Type type;
		public final boolean list;
		public final String field;
	}
	
	public static final class IndirectStore extends Bytecode {
		public final Type type;
		public final boolean list;
		public final String field;
	}
	
	public static final class IndirectInvoke extends Bytecode {		
		public final Type.Fun type;
		
		public IndirectInvoke(Type.Fun type) {
			this.type = type;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof IndirectInvoke) {
				IndirectInvoke i = (IndirectInvoke) o;
				return type.equals(i.type);
			}
			return false;
		}
	
		public String toString() {
			return "indirectinvoke " + type;
		}		
	}
	
	public static final class Invoke extends Bytecode {		
		public final Type.Fun type;
		public final NameID name;
		
		
		public Invoke(Type.Fun type, NameID name) {
			this.type = type;
			this.name = name;
		}
		
		public int hashCode() {
			return type.hashCode() + name.hashCode();
		}
		
		public boolean equals(Object o) {
			if (o instanceof Invoke) {
				Invoke i = (Invoke) o;
				return type.equals(i.type) && name.equals(i.name);
			}
			return false;
		}
	
		public String toString() {
			return "invoke " + name + ":" + type;
		}	
		
	}
	
	public static final class Label extends Bytecode {
		public final String label;
		
		public Label(String label) {
			this.label = label;
		}
		
		public int hashCode() {
			return label.hashCode();
		}
		
		public boolean equals(Object o) {
			if (o instanceof Label) {
				return label.equals(((Label) o).label);
			}
			return false;
		}
		
		public String toString() {
			return "." + label;
		}
	}
	
	public static final class Load extends Bytecode {		
		public final Type type;
		public final int slot;		
	}		
	
	public static final class Loop extends Bytecode {
		
	}
	
	public static final class Move extends Load {
		
	}

	public enum NOp {
		SET,
		LIST,
		DICTIONARY,
		RECORD
	}
	
	public static final class New extends Bytecode {
		public final NOp kind;
	}
	
	public static final class Nop extends Bytecode {
		
	}	
	
	public static final class Pop extends Bytecode {
		
	}

	public static final class Return extends Bytecode {
		
	}

	public static final class Store extends Bytecode {
		public final Type type;
		public final int slot;		
	}
	
	public static final class SubList extends Bytecode {
		
	}

	public static final class Switch extends Bytecode {
		
	}

	public static final class Send extends Bytecode {
		 public final boolean asynchronous;
	}

	public static final class Throw extends Bytecode {
		
	}
	
	public static final class UnOp extends Bytecode {
		public final Type type;
		public final UOp uop; 
	}
	
	public enum UOp { 
		NEG() {
			public String toString() { return "neg"; }
		},
		FLOOR() {
			public String toString() { return "floor"; }
		},
		CEIL() {
			public String toString() { return "ceil"; }
		},
		SPLIT() {
			public String toString() { return "split"; }
		},
		LENGTHOF() {
			public String toString() { return "lengthof"; }
		},
		PROCESSACCESS() {
			public String toString() { return "procload"; }
		},
		PROCESSSPAWN() {
			public String toString() { return "spawn"; }
		},		
		DEBUG() {
			public String toString() { return "debug"; }
		},
		FAIL() {
			public String toString() { return "fail"; }
		}
	}
	
	/**
	 * The void bytecode is used to indicate that a given register is no longer live.
	 * @author djp
	 *
	 */
	public static class Void extends Bytecode {
		public final int slot;
	}
}
