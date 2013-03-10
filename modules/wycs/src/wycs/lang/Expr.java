package wycs.lang;

import static wybs.lang.SyntaxError.internalFailure;

import java.util.*;

import wybs.lang.Attribute;
import wybs.io.Token;
import wybs.util.Pair;
import wybs.lang.SyntacticElement;

public abstract class Expr extends SyntacticElement.Impl implements SyntacticElement {
	
	public Expr(Attribute... attributes) {
		super(attributes);
	}
	
	public Expr(Collection<Attribute> attributes) {
		super(attributes);
	}
	
	public abstract Expr substitute(Map<String,Expr> binding);
	
	// ==================================================================
	// Constructors
	// ==================================================================
	
	public static Variable Variable(String name, Attribute... attributes) {
		return new Variable(name,attributes);
	}
	
	public static Variable Variable(String name, Collection<Attribute> attributes) {
		return new Variable(name,attributes);
	}
	
	public static Constant Constant(Value value, Attribute... attributes) {
		return new Constant(value,attributes);
	}
	
	public static Constant Constant(Value value, Collection<Attribute> attributes) {
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
	
	public static Nary Nary(Nary.Op op, Collection<Expr> operands, Attribute... attributes) {
		return new Nary(op, operands, attributes);
	}
	
	public static Nary Nary(Nary.Op op, Collection<Expr> operands, Collection<Attribute> attributes) {
		return new Nary(op, operands, attributes);
	}
	
	public static TupleLoad TupleLoad(Expr src, int index, Attribute... attributes) {
		return new TupleLoad(src, index, attributes);
	}
	
	public static TupleLoad TupleLoad(Expr src, int index, Collection<Attribute> attributes) {
		return new TupleLoad(src, index, attributes);
	}
	
	public static FunCall FunCall(String name, SyntacticType[] generics, Expr operand, Attribute... attributes) {
		return new FunCall(name,generics,operand,attributes);
	}
	
	public static FunCall FunCall(String name, SyntacticType[] generics, Expr operand, Collection<Attribute> attributes) {
		return new FunCall(name,generics,operand,attributes);
	}
	
	public static ForAll ForAll(Pair<TypePattern, Expr>[] variables, Expr expr,
			Attribute... attributes) {
		return new ForAll(variables,expr,attributes);
	}
	
	public static ForAll ForAll(Pair<TypePattern, Expr>[] variables, Expr expr, Collection<Attribute> attributes) {
		return new ForAll(variables,expr,attributes);
	}
	
	public static Exists Exists(Pair<TypePattern, Expr>[] variables, Expr expr, Attribute... attributes) {
		return new Exists(variables,expr,attributes);
	}
	
	public static Exists Exists(Pair<TypePattern, Expr>[] variables, Expr expr, Collection<Attribute> attributes) {
		return new Exists(variables,expr,attributes);
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
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr r = binding.get(name);
			if(r != null) {
				// FIXME: should clone here!!!
				return r;
			} else {
				return this;
			}
		}
		
		public String toString() {
			return name;
		}
	}
	
	public static class Constant extends Expr {
		public final Value value;
		
		private Constant(Value value, Attribute... attributes) {
			super(attributes);
			this.value = value;
		}
		
		private Constant(Value value, Collection<Attribute> attributes) {
			super(attributes);
			this.value = value;
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			return this;
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
		public Expr operand;
		
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
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr expr = operand.substitute(binding);
			if(expr == operand) {
				return this;
			} else {
				return Expr.Unary(op, expr, attributes());
			}
		}
		
		public String toString() {
			String o = operand.toString();
			if(needsBraces(operand)) {
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
			ADD(1) {
				public String toString() {
					return "+";
				}
			},
			SUB(2) {
				public String toString() {
					return "-";
				}
			},
			MUL(3) {
				public String toString() {
					return "*";
				}
			},
			DIV(4) {
				public String toString() {
					return "/";
				}
			},
			REM(5) {
				public String toString() {
					return "%";
				}
			},
			EQ(6) {
				public String toString() {
					return "==";
				}
			},
			NEQ(7) {
				public String toString() {
					return "!=";
				}
			},
			IMPLIES(8) {
				public String toString() {
					return "==>";
				}
			},
			IFF(9) {
				public String toString() {
					return "<==>";
				}
			},
			LT(10) {
				public String toString() {
					return "<";
				}
			},
			LTEQ(11) {
				public String toString() {
					return Character.toString(Token.UC_LESSEQUALS);
				}				
			},
			GT(12) {
				public String toString() {
					return ">";
				}
			},
			GTEQ(13) {
				public String toString() {
					return Character.toString(Token.UC_GREATEREQUALS);					
				}
			},
			IN(14) {
				public String toString() {
					//return Character.toString(Token.UC_ELEMENTOF);
					return "in";
				}
			},
			SUBSET(14) {
				public String toString() {
					return Character.toString(Token.UC_SUBSET);
				}
			},
			SUBSETEQ(15) {
				public String toString() {
					return Character.toString(Token.UC_SUBSETEQ);
				}
			},
			SUPSET(16) {
				public String toString() {
					return Character.toString(Token.UC_SUPSET);
				}
			},
			SUPSETEQ(17) {
				public String toString() {
					return Character.toString(Token.UC_SUPSETEQ);
				}
			};
			
			public int offset;

			private Op(int offset) {
				this.offset = offset;
			}
		};

		public final Op op;
		public Expr leftOperand;
		public Expr rightOperand;
		
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
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr lhs = leftOperand.substitute(binding);
			Expr rhs = rightOperand.substitute(binding);
			if(lhs == leftOperand && rhs == rightOperand) {
				return this;
			} else {
				return Expr.Binary(op, lhs, rhs, attributes());
			}
		}
		
		public String toString() {
			String lhs = leftOperand.toString();
			String rhs = rightOperand.toString();
			if(needsBraces(leftOperand)) {
				lhs = "(" + lhs + ")";
			}
			if(needsBraces(rightOperand)) {
				rhs = "(" + rhs + ")";
			}			
			return lhs + " " + op + " " + rhs;			
		}
	}
	
	public static class Nary extends Expr {
		public enum Op {
			AND(0),
			OR(1),
			SET(2),
			TUPLE(3);			
					
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
		
		public Nary(Op op, Collection<Expr> operands, Attribute... attributes) {
			super(attributes);			
			this.op = op;
			this.operands = new Expr[operands.size()];
			int i = 0;
			for(Expr e : operands) {
				this.operands[i++] = e;
			}
		}
		
		public Nary(Op op, Collection<Expr> operands, Collection<Attribute> attributes) {
			super(attributes);			
			this.op = op;
			this.operands = new Expr[operands.size()];
			int i = 0;
			for(Expr e : operands) {
				this.operands[i++] = e;
			}
		}
	
		public Expr substitute(Map<String,Expr> binding) {			
			Expr[] r_operands = operands;
			for(int i=0;i!=operands.length;++i) {
				Expr o = operands[i];
				Expr e = o.substitute(binding);				
				if(e != o && r_operands == operands) {
					r_operands = Arrays.copyOf(operands, operands.length);
				}
				r_operands[i] = e;
			}
			if(r_operands == operands) {
				return this;
			} else {
				return Expr.Nary(op, r_operands, attributes());
			}
		}
				
		public String toString() {
			String beg;
			String end;
			String sep;
			switch(this.op) {
			case AND:
				beg = "";
				end = "";
				sep = " && ";
				break;
			case OR:
				beg = "";
				end = "";
				sep = " || ";
				break;
			case SET:
				beg = "{";
				end = "}";
				sep = ", ";
				break;
			case TUPLE:
				beg = "(";
				end = ")";
				sep = ", ";
				break;						
			default:
				return "";
			}

			String r = beg;
			for(int i=0;i!=operands.length;++i) {
				if(i != 0) {
					r = r + sep;
				}		
				String os = operands[i].toString();
				if(needsBraces(operands[i])) {
					r = r + "(" + operands[i].toString() + ")";	
				} else {
					r = r + operands[i].toString();
				}
			}
			return r + end;
		}
	}	
	
	public static class TupleLoad extends Expr {
		public Expr operand;
		public int index;
		
		private TupleLoad(Expr expr, int index, Attribute... attributes) {
			super(attributes);			
			this.operand = expr;
			this.index = index;
		}
		
		private TupleLoad(Expr expr, int index, Collection<Attribute> attributes) {
			super(attributes);			
			this.index = index;
			this.operand = expr;
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr expr = operand.substitute(binding);
			if(expr == operand) {
				return this;
			} else {
				return Expr.TupleLoad(expr, index, attributes());
			}
		}
		
		public String toString() {
			return operand + "[" + index + "]";
		}
	}
	
	public static class FunCall extends Expr {
		public final SyntacticType[] generics;
		public final Expr operand;
		public final String name;
		
		private FunCall(String name, SyntacticType[] generics, Expr operand, Attribute... attributes) {
			super(attributes);			
			this.name = name;
			this.generics = generics;
			this.operand = operand;
		}
		
		private FunCall(String name, SyntacticType[] generics, Expr operand, Collection<Attribute> attributes) {
			super(attributes);			
			this.name = name;
			this.generics = generics;
			this.operand = operand;
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr expr = operand.substitute(binding);
			if(expr == operand) {
				return this;
			} else {
				return Expr.FunCall(name, generics, expr, attributes());
			}
		}
		
		public String toString() {
			String r = name;
			if(generics.length > 0) {
				r = r + "<";
				for(int i=0;i!=generics.length;++i) {
					if(i != 0) { r += ", "; }									
					r += generics[i];
				}
				r = r + ">";
			}
			return r + operand;
		}
	}
	
	public static abstract class Quantifier extends Expr {
		public Pair<TypePattern, Expr>[] variables;
		public Expr operand;
		
		private Quantifier(Pair<TypePattern, Expr>[] variables, Expr operand,
				Attribute... attributes) {
			super(attributes);			
			this.variables = variables;
			this.operand = operand;
		}
		
		private Quantifier(Pair<TypePattern, Expr>[] variables, Expr operand, Collection<Attribute> attributes) {
			super(attributes);			
			this.variables = variables;			
			this.operand = operand;
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Pair<TypePattern,Expr>[] nVariables;
			Expr op = operand.substitute(binding);
			if(op != operand) {
				nVariables = Arrays.copyOf(variables, variables.length);
			} else {
				nVariables = variables;
			}
			
			for(int i=0;i!=variables.length;++i) {
				Pair<TypePattern,Expr> p = variables[i];
				Expr o = p.second();
				if(o == null) { continue; }
				Expr e = o.substitute(binding);				
				if (e != o) {
					if (nVariables == variables) {
						nVariables = Arrays.copyOf(variables,
								variables.length);
					}
					nVariables[i] = new Pair<TypePattern, Expr>(p.first(), e);
				} else {
					nVariables[i] = p;
				}
			}
			if (nVariables == variables) {
				return this;
			} else if (this instanceof ForAll) {
				return Expr.ForAll(nVariables, op,
						attributes());
			} else {
				return Expr.Exists(nVariables, op,
						attributes());
			}
		}
			
		
		public String toString() {
			String r = "[ ";
			boolean firstTime = true;
			for (Pair<TypePattern,Expr> p : variables) {
				TypePattern tp = p.first();
				Expr e = p.second();
				if (!firstTime) {
					r = r + ",";
				}
				firstTime = false;
				r = r + tp;
				if(e != null) {
					r = r + " in " + e;
				}				
			}
			return r + " : " + operand + " ]";
		}
	}
	
	public static class ForAll extends Quantifier {
		private ForAll(Pair<TypePattern, Expr>[] variables, Expr expr,
				Attribute... attributes) {
			super(variables, expr, attributes);
		}

		private ForAll(Pair<TypePattern, Expr>[] variables, Expr expr,
				Collection<Attribute> attributes) {
			super(variables, expr, attributes);
		}
		
		public String toString() {
			return "all " + super.toString();
		}
	}
	
	public static class Exists extends Quantifier {
		private Exists(Pair<TypePattern, Expr>[] variables, Expr expr, Attribute... attributes) {
			super(variables, expr, attributes);						
		}
		
		private Exists(Pair<TypePattern, Expr>[] variables, Expr expr, Collection<Attribute> attributes) {
			super(variables, expr, attributes);						
		}
		
		public String toString() {
			return "exists " + super.toString();
		}
	}
	
	private static boolean needsBraces(Expr e) {
		 if(e instanceof Expr.Binary) {			
			 return true;
		 } else if(e instanceof Expr.Nary) {
			 Expr.Nary ne = (Expr.Nary) e;
			 switch(ne.op) {
			 case AND:
			 case OR:
				 return true;
			 }
		 } else if(e instanceof Quantifier) {
			 return true;
		 }
		 return false;
	}
}
