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
	
	public Expr(Collection<Attribute> attributes) {
		super(attributes);
	}
	
	// ==================================================================
	// Constructors
	// ==================================================================
	
	public static Variable Variable(String name, Attribute... attributes) {
		return new Variable(name,attributes);
	}
	
	public static Variable Variable(String name, Collection<Attribute> attributes) {
		return new Variable(name,attributes);
	}
	
	public static Constant Constant(wyil.lang.Constant value, Attribute... attributes) {
		return new Constant(value,attributes);
	}
	
	public static Constant Constant(wyil.lang.Constant value, Collection<Attribute> attributes) {
		return new Constant(value,attributes);
	}
	
	public static Unary Unary(Unary.Op op, Expr operand, Attribute... attributes) {
		return new Unary(op, operand,attributes);
	}
	
	public static Unary Unary(Unary.Op op, Expr operand, Collection<Attribute> attributes) {
		return new Unary(op, operand,attributes);
	}
	
	public static Binary Binary(Binary.Op op, Expr leftOperand, Expr rightOperand, Attribute... attributes) {
		return new Binary(op, leftOperand, rightOperand, attributes);
	}
	
	public static Binary Binary(Binary.Op op, Expr leftOperand, Expr rightOperand, Collection<Attribute> attributes) {
		return new Binary(op, leftOperand, rightOperand, attributes);
	}
	
	public static Nary Nary(Nary.Op op, Expr[] operands, Attribute... attributes) {
		return new Nary(op, operands, attributes);
	}
	
	public static Nary Nary(Nary.Op op, Expr[] operands, Collection<Attribute> attributes) {
		return new Nary(op, operands, attributes);
	}
	
	public static ForAll ForAll(Collection<Stmt.Declare> vars, Expr expr, Attribute... attributes) {
		return new ForAll(vars,expr,attributes);
	}
	
	public static ForAll ForAll(Collection<Stmt.Declare> vars, Expr expr, Collection<Attribute> attributes) {
		return new ForAll(vars,expr,attributes);
	}
	
	public static Exists Exists(Collection<Stmt.Declare> vars, Expr expr, Attribute... attributes) {
		return new Exists(vars,expr,attributes);
	}
	
	public static Exists Exists(Collection<Stmt.Declare> vars, Expr expr, Collection<Attribute> attributes) {
		return new Exists(vars,expr,attributes);
	}
	
	// ==================================================================
	// Classes
	// ==================================================================
	
	public static class Variable extends Expr {
		public final String name;
				
		private Variable(String name, Attribute... attributes) {
			super(attributes);
			this.name = name;
		}
		
		private Variable(String name, Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
		}
		
		public String toString() {
			return name;
		}
	}
	
	public static class Constant extends Expr {
		public final wyil.lang.Constant value;
		
		private Constant(wyil.lang.Constant value, Attribute... attributes) {
			super(attributes);
			this.value = value;
		}
		
		private Constant(wyil.lang.Constant value, Collection<Attribute> attributes) {
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
		
		private Unary(Op op, Expr expr, Attribute... attributes) {
			super(attributes);			
			this.op = op;
			this.operand = expr;
		}
		
		private Unary(Op op, Expr expr, Collection<Attribute> attributes) {
			super(attributes);			
			this.op = op;
			this.operand = expr;
		}
		
		public String toString() {
			String o = operand.toString();
			if(operand instanceof Expr.Binary) {
				o = "(" + o + ")";
			}
			switch(this.op) {
			case NOT:
				return "!" + o;
			case NEG:
				return "-" + o;
			case LENGTHOF:
				return "|" + o + "|";
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
			SUBSET(14) {
				public String toString() {
					return Character.toString(Lexer.UC_SUBSETEQ);
				}
			},
			SUBSETEQ(15) {
				public String toString() {
					return Character.toString(Lexer.UC_SUBSETEQ);
				}
			},
			SUPSET(15) {
				public String toString() {
					return Character.toString(Lexer.UC_SUPSET);
				}
			},
			SUPSETEQ(16) {
				public String toString() {
					return Character.toString(Lexer.UC_SUPSETEQ);
				}
			},
			UNION(17) {
				public String toString() {
					return Character.toString(Lexer.UC_SETUNION);
				}
			},
			INTERSECTION(18) {
				public String toString() {
					return Character.toString(Lexer.UC_SETINTERSECTION);
				}
			},
			DIFFERENCE(19) {
				public String toString() {
					return "{=";
				}
			},
			INDEXOF(20) {
				public String toString() {
					return "[]";
				}
			},
			APPEND(21) {
				public String toString() {
					return "++";
				}
			},
			RANGE(22) {
				public String toString() {
					return "..";
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
		
		private Binary(Op op, Expr lhs, Expr rhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.leftOperand = lhs;
			this.rightOperand = rhs;
		}
		
		private Binary(Op op, Expr lhs, Expr rhs, Collection<Attribute> attributes) {
			super(attributes);
			this.op = op;
			this.leftOperand = lhs;
			this.rightOperand = rhs;
		}
		
		public String toString() {
			String lhs = leftOperand.toString();
			String rhs = rightOperand.toString();
			if(leftOperand instanceof Expr.Binary) {
				lhs = "(" + lhs + ")";
			}
			if(rightOperand instanceof Expr.Binary) {
				rhs = "(" + rhs + ")";
			}
			if(op == Op.INDEXOF) {
				return lhs + "[" + rhs + "]";
			} else if(op == Op.RANGE) {
				return lhs + ".." + rhs;
			} else {
				return lhs + " " + op + " " + rhs;
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
			UPDATE(5),
			INVOKE(6);
					
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
		
		public Nary(Op op, Expr[] operands, Collection<Attribute> attributes) {
			super(attributes);			
			this.op = op;
			this.operands = operands;
		}
	}
	
	public static abstract class Quantifier extends Expr {
		public final List<Stmt.Declare> vars; 
		public final Expr expr;
		
		private Quantifier(Collection<Stmt.Declare> vars, Expr expr, Attribute... attributes) {
			super(attributes);			
			this.vars = new CopyOnWriteArrayList<Stmt.Declare>(vars);
			this.expr = expr;
		}
		
		private Quantifier(Collection<Stmt.Declare> vars, Expr expr, Collection<Attribute> attributes) {
			super(attributes);			
			this.vars = new CopyOnWriteArrayList<Stmt.Declare>(vars);
			this.expr = expr;
		}
	}
	
	public static class ForAll extends Quantifier {
		private ForAll(Collection<Stmt.Declare> vars, Expr expr, Attribute... attributes) {
			super(vars, expr, attributes);						
		}
		
		private ForAll(Collection<Stmt.Declare> vars, Expr expr, Collection<Attribute> attributes) {
			super(vars, expr, attributes);						
		}
	}
	
	public static class Exists extends Quantifier {
		private Exists(Collection<Stmt.Declare> vars, Expr expr, Attribute... attributes) {
			super(vars, expr, attributes);						
		}
		
		private Exists(Collection<Stmt.Declare> vars, Expr expr, Collection<Attribute> attributes) {
			super(vars, expr, attributes);						
		}
	}
}
