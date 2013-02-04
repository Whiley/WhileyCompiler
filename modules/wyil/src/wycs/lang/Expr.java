package wycs.lang;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import wyil.lang.Attribute;
import wybs.lang.SyntacticElement;
import wycs.io.Lexer;

public abstract class Expr extends SyntacticElement.Impl implements SyntacticElement {
	
	public Expr(Attribute... attributes) {
		super(attributes);
	}
	
	public static class Variable extends Expr {
		public final String name;
				
		public Variable(String name, Attribute... attributes) {
			super(attributes);
			this.name = name;
		}
		
		public String toString() {
			return name;
		}
	}
	
	public static class Constant extends Expr {
		public final wyil.lang.Constant value;
		
		public Constant(wyil.lang.Constant value, Attribute... attributes) {
			super(attributes);
			this.value = value;
		}
		
		public String toString() {
			return value.toString();
		}
	}
	
	public static class Unary extends Expr {
		public enum Op {
			NOT(0),			
			NEG(1),			
			LENGTHOF(2);
						
			public int offset;

			private Op(int offset) {
				this.offset = offset;
			}			
		}
		
		public final Op op;
		public final Expr operand;
		
		public Unary(Op op, Expr expr, Attribute... attributes) {
			super(attributes);			
			this.op = op;
			this.operand = expr;
		}
		
		public String toString() {
			switch(this.op) {
			case NOT:
				return "!" + operand;
			case NEG:
				return "-" + operand;
			case LENGTHOF:
				return "|" + operand + "|";
			}
			return null;
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
					return Character.toString(Lexer.UC_LESSEQUALS);
				}				
			},
			GT(11) {
				public String toString() {
					return ">";
				}
			},
			GTEQ(12) {
				public String toString() {
					return Character.toString(Lexer.UC_GREATEREQUALS);
				}
			},
			IN(13) {
				public String toString() {
					return "in";
				}
			},
			SUBSETEQ(14) {
				public String toString() {
					return Character.toString(Lexer.UC_SUBSETEQ);
				}
			},
			UNION(15) {
				public String toString() {
					return Character.toString(Lexer.UC_SETUNION);
				}
			},
			INTERSECTION(16) {
				public String toString() {
					return Character.toString(Lexer.UC_SETINTERSECTION);
				}
			},
			DIFFERENCE(17) {
				public String toString() {
					return "{=";
				}
			},
			INDEXOF(18) {
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
		public final Expr leftOperand;
		public final Expr rightOperand;
		
		public Binary(Op op, Expr lhs, Expr rhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.leftOperand = lhs;
			this.rightOperand = rhs;
		}
		
		public String toString() {
			String lhs = leftOperand.toString();
			String rhs = rightOperand.toString();
			if(op == Op.INDEXOF) {
				return lhs + "[" + rhs + "]";
			} else {
				return lhs + op + rhs;
			}
		}
	}
	
	public static class Nary extends Expr {
		public enum Op {			
			SET(0),
			MAP(1),
			LIST(2),
			TUPLE(3),
			SUBLIST(4),
			RANGE(5),
			UPDATE(6),
			INVOKE(7);
					
			public int offset;

			private Op(int offset) {
				this.offset = offset;
			}			
		}
		
		public final Op op;
		public final Expr[] operands;
		
		public Nary(Op op, Expr[] operands, Attribute... attributes) {
			super(attributes);			
			this.op = op;
			this.operands = operands;
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
