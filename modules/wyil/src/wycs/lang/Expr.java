package wycs.lang;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import wyone.core.Attribute;
import wyone.util.SyntacticElement;

public abstract class Expr extends SyntacticElement.Impl {
	
	public Expr(Attribute... attributes) {
		super(attributes);
	}
	
	public static class Var extends Expr {
		public final String name;
				
		public Var(String name, Attribute... attributes) {
			super(attributes);
			this.name = name;
		}
	}
	
	public static class Constant extends Expr {
		public final wyil.lang.Constant constant;
		
		public Constant(wyil.lang.Constant constant, Attribute... attributes) {
			super(attributes);
			this.constant = constant;
		}
	}
	
	public static class Unary extends Expr {
		public enum Op {
			NOT(0) {
				public String toString() {
					return "1";
				}
			},
			
			NEG(1) {
				public String toString() {
					return "-";
				}
			};
						
			public int offset;

			private Op(int offset) {
				this.offset = offset;
			}			
		}
		
		public final Op op;
		public final Expr expr;
		
		public Unary(Op op, Expr expr, Attribute... attributes) {
			super(attributes);			
			this.op = op;
			this.expr = expr;
		}
	}
		
	public static class Binary extends Expr {
		public enum Op {
			AND(0) {
				public String toString() {
					return "&&";
				}
			},
			
			OR(1) {
				public String toString() {
					return "||";
				}
			},
			
			ADD(2) {
				public String toString() {
					return "+";
				}
			},
			SUB(3) {
				public String toString() {
					return "-";
				}
			},
			MUL(4) {
				public String toString() {
					return "*";
				}
			},
			DIV(5) {
				public String toString() {
					return "/";
				}
			},
			REM(6) {
				public String toString() {
					return "%";
				}
			},
			EQ(7) {
				public String toString() {
					return "==";
				}
			},
			NEQ(8) {
				public String toString() {
					return "!=";
				}
			},
			LT(9) {
				public String toString() {
					return "<";
				}
			},
			LTEQ(10) {
				public String toString() {
					return "<=";
				}
			},
			GT(11) {
				public String toString() {
					return ">";
				}
			},
			GTEQ(12) {
				public String toString() {
					return ">=";
				}
			},
			IN(13) {
				public String toString() {
					return "in";
				}
			},
			SUBSETEQ(14) {
				public String toString() {
					return "{=";
				}
			},
			INDEXOF(15) {
				public String toString() {
					return "[]";
				}
			};
			
			public int offset;

			private Op(int offset) {
				this.offset = offset;
			}
		};

		public final Op op;
		public final Expr lhs;
		public final Expr rhs;
		
		public Binary(Op op, Expr lhs, Expr rhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
		}
	}
	
	public static abstract class Quantifier extends Expr {
		public final List<Stmt.Declare> vars; 
		public final Expr expr;
		
		public Quantifier(Collection<Stmt.Declare> vars, Expr expr, Attribute... attributes) {
			super(attributes);			
			this.vars = new CopyOnWriteArrayList<Stmt.Declare>(vars);
			this.expr = expr;
		}
	}
	
	public static class ForAll extends Quantifier {
		public ForAll(Collection<Stmt.Declare> vars, Expr expr, Attribute... attributes) {
			super(vars, expr, attributes);						
		}
	}
	
	public static class Exists extends Quantifier {
		public Exists(Collection<Stmt.Declare> vars, Expr expr, Attribute... attributes) {
			super(vars, expr, attributes);						
		}
	}
}
