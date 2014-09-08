package wycs.syntax;

import java.util.*;

import wycc.io.Token;
import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;
import wycc.util.Pair;
import wycc.util.Triple;
import wycs.core.Value;
import wyfs.lang.Path;

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
	// Classes
	// ==================================================================
	
	public static class Variable extends Expr {
		public final String name;
				
		public Variable(String name, Attribute... attributes) {
			super(attributes);
			if(!isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: " + name);
			}
			this.name = name;
		}
		
		public Variable(String name, Collection<Attribute> attributes) {
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
		
		public Constant(Value value, Attribute... attributes) {
			super(attributes);
			this.value = value;
		}
		
		public Constant(Value value, Collection<Attribute> attributes) {
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
	
	public static class ConstantAccess extends Expr {
		public final String name;
		public final Path.ID qualification;
		
		public ConstantAccess(String name, Path.ID qualification, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.qualification = qualification;
		}
		
		public ConstantAccess(String name, Path.ID qualification, Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.qualification = qualification;
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
			return name;
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
		
		public Unary(Op op, Expr expr, Attribute... attributes) {
			super(attributes);			
			this.op = op;
			this.operand = expr;
		}
		
		public Unary(Op op, Expr expr, Collection<Attribute> attributes) {
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
				return new Expr.Unary(op, expr, attributes());
			}
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr expr = operand.substitute(binding);
			if(expr == operand) {
				return this;
			} else {
				return new Expr.Unary(op, expr, attributes());
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
	
	public static class Cast extends Expr {
		public final SyntacticType type;
		public Expr operand;
		
		public Cast(SyntacticType type, Expr expr, Attribute... attributes) {
			super(attributes);			
			this.type = type;
			this.operand = expr;
		}
		
		public Cast(SyntacticType type, Expr expr, Collection<Attribute> attributes) {
			super(attributes);			
			this.type = type;
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
				return new Expr.Cast(type, expr, attributes());
			}
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr expr = operand.substitute(binding);
			if(expr == operand) {
				return this;
			} else {
				return new Expr.Cast(type, expr, attributes());
			}
		}
		
		public String toString() {
			return "(" + type + ") " + operand;
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
			AND(8) {
				public String toString() {
					return "&&";
				}
			},
			OR(9) {
				public String toString() {
					return "||";
				}
			},
			IMPLIES(10) {
				public String toString() {
					return "==>";
				}
			},
			IFF(11) {
				public String toString() {
					return "<==>";
				}
			},
			LT(12) {
				public String toString() {
					return "<";
				}
			},
			LTEQ(13) {
				public String toString() {
					//return Character.toString(Token.UC_LESSEQUALS);
					return "<=";
				}				
			},
			GT(14) {
				public String toString() {
					return ">";
				}
			},
			GTEQ(15) {
				public String toString() {
					//return Character.toString(Token.UC_GREATEREQUALS);					
					return ">=";
				}
			},
			IN(16) {
				public String toString() {
					//return Character.toString(Token.UC_ELEMENTOF);
					return "in";
				}
			},
			SUBSET(17) {
				public String toString() {
					return Character.toString(Token.UC_SUBSET);
					
				}
			},
			SUBSETEQ(18) {
				public String toString() {
					// FIXME: need to figure out why this is necessary
					//return Character.toString(Token.UC_SUBSETEQ);
					return "{=";
				}
			},
			SUPSET(19) {
				public String toString() {
					return Character.toString(Token.UC_SUPSET);
				}
			},
			SUPSETEQ(20) {
				public String toString() {
					return Character.toString(Token.UC_SUPSETEQ);					
				}
			},
			SETUNION(21) {
				public String toString() {
					return Character.toString(Token.UC_SETUNION);					
				}
			},
			SETINTERSECTION(22) {
				public String toString() {
					return Character.toString(Token.UC_SETINTERSECTION);					
				}
			},
			SETDIFFERENCE(23) {
				public String toString() {
					return "-";					
				}
			},
			LISTAPPEND(24) {
				public String toString() {
					return "++";					
				}
			},
			RANGE(25) {
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
		
		public Binary(Op op, Expr lhs, Expr rhs, Attribute... attributes) {
			super(attributes);
			if(lhs == null || rhs == null) {
				throw new IllegalArgumentException("invalid left or right operand");
			}
			this.op = op;
			this.leftOperand = lhs;
			this.rightOperand = rhs;
		}
		
		public Binary(Op op, Expr lhs, Expr rhs, Collection<Attribute> attributes) {			
			super(attributes);
			if(lhs == null || rhs == null) {
				throw new IllegalArgumentException("invalid left or right operand");
			}
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
				return new Expr.Binary(op, lhs, rhs, attributes());
			}
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr lhs = leftOperand.substitute(binding);
			Expr rhs = rightOperand.substitute(binding);
			if(lhs == leftOperand && rhs == rightOperand) {
				return this;
			} else {
				return new Expr.Binary(op, lhs, rhs, attributes());
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
		
		public Ternary(Op op, Expr first, Expr second, Expr third, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.firstOperand = first;
			this.secondOperand = second;
			this.thirdOperand = third;
		}
		
		public Ternary(Op op, Expr first, Expr second, Expr third, Collection<Attribute> attributes) {
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
				return new Expr.Ternary(op, first, second, third, attributes());
			}
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr first = firstOperand.substitute(binding);
			Expr second = secondOperand.substitute(binding);
			Expr third = thirdOperand.substitute(binding);
			if(first == firstOperand && second == secondOperand && third == thirdOperand) {
				return this;
			} else {
				return new Expr.Ternary(op, first, second, third, attributes());
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
			TUPLE(0),
			SET(1),
			MAP(2),
			LIST(3);
							
			public int offset;

			private Op(int offset) {
				this.offset = offset;
			}			
		}
		
		public final Op op;
		public final ArrayList<Expr> operands;
		
		public Nary(Op op, List<Expr> operands, Attribute... attributes) {
			super(attributes);			
			this.op = op;
			this.operands = new ArrayList<Expr>(operands);
		}
		
		public Nary(Op op, List<Expr> operands, Collection<Attribute> attributes) {
			super(attributes);			
			this.op = op;
			this.operands = new ArrayList<Expr>(operands);
		}
		
		public Nary(Op op, Expr[] operands, Attribute... attributes) {
			super(attributes);			
			this.op = op;
			this.operands = new ArrayList<Expr>();
			for(int i=0;i!=operands.length;++i) {
				this.operands.add(operands[i]);
			}
		}
		
		public Nary(Op op, Expr[] operands, Collection<Attribute> attributes) {
			super(attributes);			
			this.op = op;
			this.operands = new ArrayList<Expr>();
			for(int i=0;i!=operands.length;++i) {
				this.operands.add(operands[i]);
			}
		}
		
		public void freeVariables(Set<String> matches) {			
			for(Expr operand : operands) {
				operand.freeVariables(matches);
			}
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {			
			ArrayList<Expr> r_operands = operands;
			for(int i=0;i!=operands.size();++i) {
				Expr o = operands.get(i);
				Expr e = o.instantiate(binding);				
				if(e != o && r_operands == operands) {
					r_operands = new ArrayList<Expr>(operands);
				}
				r_operands.set(i,e);
			}
			if(r_operands == operands) {
				return this;
			} else {
				return new Expr.Nary(op, r_operands, attributes());
			}
		}
			
		public Expr substitute(Map<String,Expr> binding) {			
			ArrayList<Expr> r_operands = operands;
			for(int i=0;i!=operands.size();++i) {
				Expr o = operands.get(i);
				Expr e = o.substitute(binding);				
				if(e != o && r_operands == operands) {
					r_operands = new ArrayList<Expr>(operands);
				}
				r_operands.set(i,e);
			}
			if(r_operands == operands) {
				return this;
			} else {
				return new Expr.Nary(op, r_operands, attributes());
			}
		}
		
		public String toString() {
			String beg;
			String end;
			String sep;
			switch(this.op) {			
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
				for(int i=0;i!=operands.size();i=i+2) {
					if(i != 0) {
						r = r + ",";
					}		
					String os = operands.get(i).toString();
					if(needsBraces(operands.get(i))) {
						r = r + "(" + os + ")";	
					} else {
						r = r + os;
					}
					r = r + "=>";
					os = operands.get(i+1).toString();
					if(needsBraces(operands.get(i+1))) {
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
			for(int i=0;i!=operands.size();++i) {
				if(i != 0) {
					r = r + sep;
				}		
				String os = operands.get(i).toString();
				if(needsBraces(operands.get(i))) {
					r = r + "(" + operands.get(i).toString() + ")";	
				} else {
					r = r + operands.get(i).toString();
				}
			}
			return r + end;
		}
	}	
	
	public static class Record extends Expr {
		public ArrayList<Pair<String,Expr>> operands;
		
		public Record(List<Pair<String,Expr>> operands, Attribute... attributes) {
			super(attributes);			
			this.operands = new ArrayList<Pair<String,Expr>>(operands);
		}
		
		public Record(List<Pair<String,Expr>> operands, Collection<Attribute> attributes) {
			super(attributes);			
			this.operands = new ArrayList<Pair<String,Expr>>(operands);
		}
		
		public void freeVariables(Set<String> matches) {
			for(Pair<String,Expr> p : operands) {
				p.second().freeVariables(matches);
			}
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {
			// TODO
			return null;
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			// TODO
						return null;
		}		
	}
	
	public static class IndexOf extends Expr {
		public Expr operand;
		public Expr index;
		
		public IndexOf(Expr expr, Expr index, Attribute... attributes) {
			super(attributes);			
			this.operand = expr;
			this.index = index;
		}
		
		public IndexOf(Expr expr, Expr index, Collection<Attribute> attributes) {
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
				return new Expr.IndexOf(nOperand, nIndex, attributes());
			}
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr nOperand = operand.substitute(binding);
			Expr nIndex = index.substitute(binding);
			if(nOperand == operand && index == nIndex) {
				return this;
			} else {
				return new Expr.IndexOf(nOperand, nIndex, attributes());
			}
		}
		
		public String toString() {
			return operand + "[" + index + "]";
		}
	}	
	
	public static class FieldAccess extends Expr {
		public Expr operand;
		public String name;
		
		public FieldAccess(Expr expr, String name, Attribute... attributes) {
			super(attributes);			
			this.operand = expr;
			this.name = name;
		}
		
		public FieldAccess(Expr expr, String name, Collection<Attribute> attributes) {
			super(attributes);			
			this.operand = expr;
			this.name = name;
		}
		
		public void freeVariables(Set<String> matches) {
			operand.freeVariables(matches);
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {
			Expr nOperand = operand.instantiate(binding);
			if(nOperand == operand) {
				return this;
			} else {
				return new Expr.FieldAccess(nOperand, name, attributes());
			}
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr nOperand = operand.substitute(binding);
			if(nOperand == operand) {
				return this;
			} else {
				return new Expr.FieldAccess(nOperand, name, attributes());
			}
		}
		
		public String toString() {
			return operand + "." + name;
		}
	}
	
	public static class Invoke extends Expr {
		public final ArrayList<SyntacticType> generics;
		public final Expr operand;
		public final String name;
		public Path.ID qualification;
		
		public Invoke(String name, Path.ID qualification,
				List<SyntacticType> generics, Expr operand,
				Attribute... attributes) {
			super(attributes);
			if (!isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: "
						+ name);
			}
			this.name = name;
			this.qualification = qualification;
			this.generics = new ArrayList<SyntacticType>(generics);
			this.operand = operand;
		}

		public Invoke(String name, Path.ID qualification,
				List<SyntacticType> generics, Expr operand,
				Collection<Attribute> attributes) {
			super(attributes);
			if (!isValidIdentifier(name)) {
				throw new IllegalArgumentException("illegal identifier: "
						+ name);
			}
			this.name = name;
			this.qualification = qualification;
			this.generics = new ArrayList<SyntacticType>(generics);
			this.operand = operand;
		}
		
		public void freeVariables(Set<String> matches) {			
			operand.freeVariables(matches);
		}
		
		public Expr instantiate(Map<String,SyntacticType> binding) {
			Expr.Nary r_operand = (Expr.Nary) operand.instantiate(binding);
			if(r_operand == operand) {
				return this;
			} else {
				return new Expr.Invoke(name, qualification, generics, r_operand, attributes());
			}			
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr.Nary r_operand = (Expr.Nary) operand.substitute(binding);
			if(r_operand == operand) {
				return this;
			} else {			
				return new Expr.Invoke(name, qualification, generics, r_operand, attributes());
			}	
		}
		
		public String toString() {
			String r = name;
			if(generics.size() > 0) {
				r = r + "<";
				for(int i=0;i!=generics.size();++i) {
					if(i != 0) { r += ", "; }									
					r += generics.get(i);
				}
				r = r + ">";
			}						
			return r + operand;
		}
	}
	
	public static class IndirectInvoke extends Expr {
		public final ArrayList<SyntacticType> generics;
		public final Expr operand;
		public Expr source;
		
		public IndirectInvoke(Expr source,
				List<SyntacticType> generics, Expr operand,
				Attribute... attributes) {
			super(attributes);
			this.source = source;
			this.generics = new ArrayList<SyntacticType>(generics);
			this.operand = operand;
		}

		public IndirectInvoke(Expr source,
				List<SyntacticType> generics, Expr operand,
				Collection<Attribute> attributes) {
			super(attributes);
			this.source = source;
			this.generics = new ArrayList<SyntacticType>(generics);
			this.operand = operand;
		}
		
		public void freeVariables(Set<String> matches) {
			source.freeVariables(matches);
			operand.freeVariables(matches);			
		}
		
		public Expr instantiate(Map<String, SyntacticType> binding) {
			Expr r_source = source.instantiate(binding);
			Expr.Nary r_operand = (Expr.Nary) operand.instantiate(binding);			
			if (r_source == source && r_operand == operand) {
				return this;
			} else {
				return new Expr.IndirectInvoke(r_source, generics, r_operand,
						attributes());
			}
		}
		
		public Expr substitute(Map<String, Expr> binding) {
			Expr r_source = source.substitute(binding);
			Expr.Nary r_operand = (Expr.Nary) operand.substitute(binding);			
			if (r_source == source && r_operand == operand) {
				return this;
			} else {
				return new Expr.IndirectInvoke(r_source, generics, r_operand,
						attributes());
			}
		}

		public String toString() {
			String r = source.toString();
			if(generics.size() > 0) {
				r = r + "<";
				for(int i=0;i!=generics.size();++i) {
					if(i != 0) { r += ", "; }									
					r += generics.get(i);
				}
				r = r + ">";
			}			
			return r + operand;
		}
	}
	
	public static abstract class Quantifier extends Expr {
		public TypePattern pattern;
		public Expr operand;
		
		public Quantifier(TypePattern variable, Expr operand,
				Attribute... attributes) {
			super(attributes);			
			this.pattern = variable;
			this.operand = operand;
		}
		
		public Quantifier(TypePattern variable, Expr operand, Collection<Attribute> attributes) {
			super(attributes);			
			this.pattern = variable;			
			this.operand = operand;
		}
		
		public void freeVariables(Set<String> matches) {
			HashSet<String> myVars = new HashSet<String>();
			HashSet<String> declaredVars = new HashSet<String>();
			operand.freeVariables(myVars);		
			pattern.addDeclaredVariables(declaredVars);			
			myVars.removeAll(declaredVars);			
			matches.addAll(myVars);
		}
		
		public Expr instantiate(Map<String, SyntacticType> binding) {			
			Expr op = operand.instantiate(binding);
			TypePattern p = pattern.instantiate(binding);			
			if (op == operand && p == pattern) {
				return this;
			} else if (this instanceof ForAll) {
				return new Expr.ForAll(p, op, attributes());
			} else {
				return new Expr.Exists(p, op, attributes());
			}
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr op = operand.substitute(binding);		
			if (op == operand) {
				return this;
			} else if (this instanceof ForAll) {
				return new Expr.ForAll(pattern, op,
						attributes());
			} else {
				return new Expr.Exists(pattern, op,
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
		public ForAll(TypePattern variable,
				Expr expr, Attribute... attributes) {
			super(variable, expr, attributes);
		}

		public ForAll(TypePattern variable,
				Expr expr, Collection<Attribute> attributes) {
			super(variable, expr, attributes);
		}
		
		public String toString() {
			return "all " + super.toString();
		}
	}
	
	public static class Exists extends Quantifier {
		public Exists(TypePattern variable,
				Expr expr, Attribute... attributes) {
			super(variable, expr, attributes);
		}

		public Exists(TypePattern variable,
				Expr expr, Collection<Attribute> attributes) {
			super(variable, expr, attributes);
		}
		
		public String toString() {
			return "exists " + super.toString();
		}
	}

	private static boolean needsBraces(Expr e) {
		if (e instanceof Expr.Binary) {			
			 return true;
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
