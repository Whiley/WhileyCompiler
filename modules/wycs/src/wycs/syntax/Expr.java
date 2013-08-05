package wycs.syntax;

import java.util.*;

import wybs.lang.Attribute;
import wybs.io.Token;
import wybs.util.Pair;
import wybs.util.Triple;
import wybs.lang.SyntacticElement;
import wycs.core.Value;

public abstract class Expr extends SyntacticElement.Impl implements SyntacticElement {
	
	public Expr(Attribute... attributes) {
		super(attributes);
	}
	
	public Expr(Collection<Attribute> attributes) {
		super(attributes);
	}
		
	public abstract void freeVariables(Set<String> matches);
	
	public abstract Expr instantiate(Map<String,SyntacticType> binding);
	
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
	
	public static Ternary Ternary(Ternary.Op op, Expr first, Expr second, Expr third, Attribute... attributes) {
		return new Ternary(op, first, second, third, attributes);
	}
	
	public static Ternary Ternary(Ternary.Op op, Expr first, Expr second,
			Expr third, Collection<Attribute> attributes) {
		return new Ternary(op, first, second, third, attributes);
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
	
	public static IndexOf IndexOf(Expr src, Expr index, Attribute... attributes) {
		return new IndexOf(src, index, attributes);
	}
	
	public static IndexOf IndexOf(Expr src, Expr index, Collection<Attribute> attributes) {
		return new IndexOf(src, index, attributes);
	}
		
	public static FunCall FunCall(String name, SyntacticType[] generics, Expr operand, Attribute... attributes) {
		return new FunCall(name,generics,operand,attributes);
	}
	
	public static FunCall FunCall(String name, SyntacticType[] generics, Expr operand, Collection<Attribute> attributes) {
		return new FunCall(name,generics,operand,attributes);
	}
	
	public static ForAll ForAll(TypePattern variable, Expr expr,
			Attribute... attributes) {
		return new ForAll(variable,expr,attributes);
	}
	
	public static ForAll ForAll(TypePattern variable, Expr expr, Collection<Attribute> attributes) {
		return new ForAll(variable,expr,attributes);
	}
	
	public static Exists Exists(TypePattern variable, Expr expr, Attribute... attributes) {
		return new Exists(variable,expr,attributes);
	}
	
	public static Exists Exists(TypePattern variable, Expr expr, Collection<Attribute> attributes) {
		return new Exists(variable,expr,attributes);
	}
	
	// ==================================================================
	// Classes
	// ==================================================================
	
	public static class Variable extends Expr {
		public final String name;
				
		private Variable(String name, Attribute... attributes) {
			super(attributes);
			if(!isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: " + name);
			}
			this.name = name;
		}
		
		private Variable(String name, Collection<Attribute> attributes) {
			super(attributes);
			if(!isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: " + name);
			}
			this.name = name;
		}
		
		public void freeVariables(Set<String> matches) {
			matches.add(name);
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {
			return this;
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
		
		public void freeVariables(Set<String> matches) {			
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {
			return this;
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
		
		public void freeVariables(Set<String> matches) {			
			operand.freeVariables(matches);
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {
			Expr expr = operand.instantiate(binding);
			if(expr == operand) {
				return this;
			} else {
				return Expr.Unary(op, expr, attributes());
			}
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
					//return Character.toString(Token.UC_LESSEQUALS);
					return "<=";
				}				
			},
			GT(12) {
				public String toString() {
					return ">";
				}
			},
			GTEQ(13) {
				public String toString() {
					//return Character.toString(Token.UC_GREATEREQUALS);					
					return ">=";
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
					// FIXME: need to figure out why this is necessary
					//return Character.toString(Token.UC_SUBSETEQ);
					return "{=";
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
			},
			SETUNION(18) {
				public String toString() {
					return Character.toString(Token.UC_SETUNION);					
				}
			},
			SETINTERSECTION(19) {
				public String toString() {
					return Character.toString(Token.UC_SETINTERSECTION);					
				}
			},
			SETDIFFERENCE(20) {
				public String toString() {
					return "-";					
				}
			},
			LISTAPPEND(21) {
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
		
		public void freeVariables(Set<String> matches) {			
			leftOperand.freeVariables(matches);
			rightOperand.freeVariables(matches);
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {
			Expr lhs = leftOperand.instantiate(binding);
			Expr rhs = rightOperand.instantiate(binding);
			if(lhs == leftOperand && rhs == rightOperand) {
				return this;
			} else {
				return Expr.Binary(op, lhs, rhs, attributes());
			}
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
		
	public static class Ternary extends Expr {
		public enum Op {			
			UPDATE(1),
			SUBLIST(2);
			
			public int offset;

			private Op(int offset) {
				this.offset = offset;
			}
		};

		public final Op op;
		public Expr firstOperand;
		public Expr secondOperand;
		public Expr thirdOperand;
		
		private Ternary(Op op, Expr first, Expr second, Expr third, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.firstOperand = first;
			this.secondOperand = second;
			this.thirdOperand = third;
		}
		
		private Ternary(Op op, Expr first, Expr second, Expr third, Collection<Attribute> attributes) {
			super(attributes);
			this.op = op;
			this.firstOperand = first;
			this.secondOperand = second;
			this.thirdOperand = third;
		}
		
		public void freeVariables(Set<String> matches) {			
			firstOperand.freeVariables(matches);
			secondOperand.freeVariables(matches);
			thirdOperand.freeVariables(matches);
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {
			Expr first = firstOperand.instantiate(binding);
			Expr second = secondOperand.instantiate(binding);
			Expr third = thirdOperand.instantiate(binding);
			if(first == firstOperand && second == secondOperand && third == thirdOperand) {
				return this;
			} else {
				return Expr.Ternary(op, first, second, third, attributes());
			}
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr first = firstOperand.substitute(binding);
			Expr second = secondOperand.substitute(binding);
			Expr third = thirdOperand.substitute(binding);
			if(first == firstOperand && second == secondOperand && third == thirdOperand) {
				return this;
			} else {
				return Expr.Ternary(op, first, second, third, attributes());
			}
		}
		
		public String toString() {
			switch(op) {
			case UPDATE:
				return firstOperand + "[" + secondOperand + ":=" + thirdOperand + "]";
			case SUBLIST:
				return firstOperand + "[" + secondOperand + ".." + thirdOperand + "]";
			}
			return "";
		}
	}
	
	public static class Nary extends Expr {
		public enum Op {
			AND(0),
			OR(1),
			TUPLE(2),
			SET(3),
			MAP(4),
			LIST(5);
							
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
		
		public void freeVariables(Set<String> matches) {			
			for(Expr operand : operands) {
				operand.freeVariables(matches);
			}
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {			
			Expr[] r_operands = operands;
			for(int i=0;i!=operands.length;++i) {
				Expr o = operands[i];
				Expr e = o.instantiate(binding);				
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
			case LIST:
				beg = "[";
				end = "]";
				sep = ", ";
				break;
			case TUPLE:
				beg = "(";
				end = ")";
				sep = ", ";
				break;		
			case MAP:
				String r = "{";
				for(int i=0;i!=operands.length;i=i+2) {
					if(i != 0) {
						r = r + ",";
					}		
					String os = operands[i].toString();
					if(needsBraces(operands[i])) {
						r = r + "(" + os + ")";	
					} else {
						r = r + os;
					}
					r = r + "=>";
					os = operands[i+1].toString();
					if(needsBraces(operands[i+1])) {
						r = r + "(" + os + ")";	
					} else {
						r = r + os;
					}
				}
				return r + "}";		
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
	
	public static class IndexOf extends Expr {
		public Expr operand;
		public Expr index;
		
		private IndexOf(Expr expr, Expr index, Attribute... attributes) {
			super(attributes);			
			this.operand = expr;
			this.index = index;
		}
		
		private IndexOf(Expr expr, Expr index, Collection<Attribute> attributes) {
			super(attributes);			
			this.index = index;
			this.operand = expr;
		}
		
		public void freeVariables(Set<String> matches) {
			operand.freeVariables(matches);
			index.freeVariables(matches);
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {
			Expr nOperand = operand.instantiate(binding);
			Expr nIndex = index.instantiate(binding);
			if(nOperand == operand && nIndex == index) {
				return this;
			} else {
				return Expr.IndexOf(nOperand, nIndex, attributes());
			}
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr nOperand = operand.substitute(binding);
			Expr nIndex = index.substitute(binding);
			if(nOperand == operand && index == nIndex) {
				return this;
			} else {
				return Expr.IndexOf(nOperand, nIndex, attributes());
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
			if(!isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: " + name);
			}
			this.name = name;
			this.generics = generics;
			this.operand = operand;
		}
		
		private FunCall(String name, SyntacticType[] generics, Expr operand, Collection<Attribute> attributes) {
			super(attributes);	
			if(!isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: " + name);
			}
			this.name = name;
			this.generics = generics;
			this.operand = operand;
		}
		
		public void freeVariables(Set<String> matches) {
			operand.freeVariables(matches);
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {
			Expr expr = operand.instantiate(binding);
			if(expr == operand) {
				return this;
			} else {
				return Expr.FunCall(name, generics, expr, attributes());
			}
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
		public TypePattern pattern;
		public Expr operand;
		
		private Quantifier(TypePattern variable, Expr operand,
				Attribute... attributes) {
			super(attributes);			
			this.pattern = variable;
			this.operand = operand;
		}
		
		private Quantifier(TypePattern variable, Expr operand, Collection<Attribute> attributes) {
			super(attributes);			
			this.pattern = variable;			
			this.operand = operand;
		}
		
		public void freeVariables(Set<String> matches) {
			HashSet<String> myVars = new HashSet<String>();
			operand.freeVariables(myVars);		
			addFreeVariables(pattern,myVars);			
			removeNamedVariables(pattern,myVars);			
			matches.addAll(myVars);
		}
		
		public Expr instantiate(Map<String, SyntacticType> binding) {			
			Expr op = operand.instantiate(binding);
			TypePattern p = pattern.instantiate(binding);			
			if (op == operand && p == pattern) {
				return this;
			} else if (this instanceof ForAll) {
				return Expr.ForAll(p, op, attributes());
			} else {
				return Expr.Exists(p, op, attributes());
			}
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr op = operand.substitute(binding);
			TypePattern p = pattern.substitute(binding);			
			if (op == operand && p == pattern) {
				return this;
			} else if (this instanceof ForAll) {
				return Expr.ForAll(p, op,
						attributes());
			} else {
				return Expr.Exists(p, op,
						attributes());
			}
		}
					
		public String toString() {
			String r = "[ ";
			boolean firstTime = true;
			r = r + pattern.toString();			
			return r + " : " + operand + " ]";
		}
	}
	
	public static class ForAll extends Quantifier {
		private ForAll(TypePattern variable,
				Expr expr, Attribute... attributes) {
			super(variable, expr, attributes);
		}

		private ForAll(TypePattern variable,
				Expr expr, Collection<Attribute> attributes) {
			super(variable, expr, attributes);
		}
		
		public String toString() {
			return "all " + super.toString();
		}
	}
	
	public static class Exists extends Quantifier {
		private Exists(TypePattern variable,
				Expr expr, Attribute... attributes) {
			super(variable, expr, attributes);
		}

		private Exists(TypePattern variable,
				Expr expr, Collection<Attribute> attributes) {
			super(variable, expr, attributes);
		}
		
		public String toString() {
			return "exists " + super.toString();
		}
	}
	
	public static void removeNamedVariables(TypePattern p, Set<String> freeVariables) {
		if(p instanceof TypePattern.Tuple) {
			TypePattern.Tuple tt = (TypePattern.Tuple) p;
			for(TypePattern pattern : tt.patterns) {
				removeNamedVariables(pattern,freeVariables);
			}
		}
		if(p.var != null) {
			freeVariables.remove(p.var);
		}
	}
	
	public static void addFreeVariables(TypePattern p, Set<String> freeVariables) {
		if(p instanceof TypePattern.Tuple) {
			TypePattern.Tuple tt = (TypePattern.Tuple) p;
			for(TypePattern pattern : tt.patterns) {
				addFreeVariables(pattern,freeVariables);
			}
		}
		if(p.source != null) {
			p.source.freeVariables(freeVariables);
		}
		if(p.constraint != null) {
			p.constraint.freeVariables(freeVariables);
		}
	}
	
	private static boolean needsBraces(Expr e) {
		if (e instanceof Expr.Binary) {			
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
	
	public static boolean isValidIdentifier(String x) {
		if (x.length() == 0) {
			return false;
		}
		if (!Character.isJavaIdentifierStart(x.charAt(0))) {
			return false;
		}
		for (int i = 1; i != x.length(); ++i) {
			if (!Character.isJavaIdentifierPart(x.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
