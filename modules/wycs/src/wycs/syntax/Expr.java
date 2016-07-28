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

	public abstract boolean equivalent(Expr e);
	
	public abstract Expr copy();
	
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
		
		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof Variable) {
				Variable v = (Variable) e;
				return name.equals(v.name);
			} else {
				return false;
			}
		}

		@Override
		public Expr.Variable copy() {
			return new Expr.Variable(name,attributes());
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

		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof Constant) {
				Constant c = (Constant) e;
				return value.equals(c.value);
			} else {
				return false;
			}
		}
		
		@Override
		public Expr.Constant copy() {
			return new Expr.Constant(value,attributes());
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

		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof ConstantAccess) {
				ConstantAccess c = (ConstantAccess) e;
				return name.equals(c.name);
			} else {
				return false;
			}
		}
		
		@Override
		public Expr.ConstantAccess copy() {
			return new Expr.ConstantAccess(name,qualification,attributes());
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
		
		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof Unary) {
				Unary v = (Unary) e;
				return op == v.op && operand.equivalent(v.operand);
			} else {
				return false;
			}
		}
		
		@Override
		public Expr.Unary copy() {
			return new Expr.Unary(op,operand.copy(),attributes());
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

		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof Cast) {
				Cast v = (Cast) e;
				return type.equivalent(v.type) && operand.equivalent(v.operand);
			} else {
				return false;
			}
		}
		
		@Override
		public Expr.Cast copy() {
			return new Expr.Cast(type.copy(),operand.copy(),attributes());
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
			ARRAYGEN(16) {
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
		
		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof Binary) {
				Binary v = (Binary) e;
				return op == v.op && leftOperand.equivalent(v.leftOperand)
						&& rightOperand.equivalent(v.rightOperand);
			} else {
				return false;
			}
		}
		
		@Override
		public Expr.Binary copy() {
			return new Expr.Binary(op,leftOperand.copy(),rightOperand.copy(),attributes());
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
			TUPLE(0),
			ARRAY(3);

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

		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof Nary) {
				Nary v = (Nary) e;
				if (operands.size() != v.operands.size()) {
					return false;
				}
				for (int i = 0; i != operands.size(); ++i) {
					if (!operands.get(i).equivalent(v.operands.get(i))) {
						return false;
					}
				}
				return op == v.op;
			} else {
				return false;
			}
		}

		@Override
		public Expr.Nary copy() {
			return new Expr.Nary(op, clone(operands), attributes());
		}
		
		public String toString() {
			String beg;
			String end;
			String sep;
			switch(this.op) {
			case ARRAY:
				beg = "[";
				end = "]";
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

		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof Record) {
				Record v = (Record) e;
				if (operands.size() != v.operands.size()) {
					return false;
				}
				for (int i = 0; i != operands.size(); ++i) {
					Pair<String, Expr> operand = operands.get(i);
					Pair<String, Expr> v_operand = v.operands.get(i);
					if (!operand.first().equals(v_operand.first())
							|| !operand.second().equivalent(v_operand.second())) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public Expr.Record copy() {
			ArrayList<Pair<String,Expr>> nOperands = new ArrayList<>();
			for (int i = 0; i != operands.size(); ++i) {
				Pair<String, Expr> operand = operands.get(i);
				nOperands.add(new Pair<>(operand.first(), operand.second()
						.copy()));
			}
			return new Expr.Record(nOperands, attributes());
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
		
		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof IndexOf) {
				IndexOf v = (IndexOf) e;
				return operand.equivalent(v.operand)
						&& index.equivalent(v.index);
			} else {
				return false;
			}
		}

		@Override
		public Expr.IndexOf copy() {
			return new Expr.IndexOf(operand.copy(), index.copy(),
					attributes());
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


		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof FieldAccess) {
				FieldAccess v = (FieldAccess) e;
				return operand.equivalent(v.operand) && name.equals(v.name);
			} else {
				return false;
			}
		}

		@Override
		public Expr.FieldAccess copy() {
			return new Expr.FieldAccess(operand.copy(), name, attributes());
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

		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof Invoke) {
				Invoke v = (Invoke) e;
				if (qualification == v.qualification) {
					// if null then don't need to do anything
				} else if (qualification != null) {
					// In this case, both are non-null;
					if (!qualification.equals(v.qualification)) {
						return false;
					}
				} else {
					// in this case, qualification == null and v.qualification
					// != null
					return false;
				}
				// Qualitications are identical.
				return name.equals(v.name) && equivalent(generics, v.generics)
						&& operand.equivalent(v.operand);
			} else {
				return false;
			}
		}
		
		@Override
		public Expr.Invoke copy() {
			return new Expr.Invoke(name, qualification, cloneTypes(generics),
					operand.copy(), attributes());
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

		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof IndirectInvoke) {
				IndirectInvoke v = (IndirectInvoke) e;
				// Qualitications are identical.
				return equivalent(generics, v.generics)
						&& source.equivalent(v.source)
						&& operand.equivalent(v.operand);
			} else {
				return false;
			}
		}
		
		@Override
		public Expr.IndirectInvoke copy() {
			return new Expr.IndirectInvoke(source.copy(),
					cloneTypes(generics), operand.copy(), attributes());
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

	public static class Is extends Expr {
		
		public Expr leftOperand;
		public SyntacticType rightOperand;

		public Is(Expr lhs, SyntacticType rhs, Attribute... attributes) {
			super(attributes);
			if(lhs == null || rhs == null) {
				throw new IllegalArgumentException("invalid left or right operand");
			}
			this.leftOperand = lhs;
			this.rightOperand = rhs;
		}

		public Is(Expr lhs, SyntacticType rhs, Collection<Attribute> attributes) {
			super(attributes);
			if(lhs == null || rhs == null) {
				throw new IllegalArgumentException("invalid left or right operand");
			}
			this.leftOperand = lhs;
			this.rightOperand = rhs;
		}

		public void freeVariables(Set<String> matches) {
			leftOperand.freeVariables(matches);
		}

		public Expr instantiate(Map<String,SyntacticType> binding) {
			Expr lhs = leftOperand.instantiate(binding);
			if(lhs == leftOperand) {
				return this;
			} else {
				return new Expr.Is(lhs, rightOperand, attributes());
			}
		}

		public Expr substitute(Map<String,Expr> binding) {
			Expr lhs = leftOperand.substitute(binding);
			if(lhs == leftOperand) {
				return this;
			} else {
				return new Expr.Is(lhs, rightOperand, attributes());
			}
		}
		
		@Override
		public boolean equivalent(Expr e) {
			if (e instanceof Is) {
				Is v = (Is) e;
				return leftOperand.equivalent(v.leftOperand)
						&& rightOperand.equivalent(v.rightOperand);
			} else {
				return false;
			}
		}

		@Override
		public Expr.Is copy() {
			return new Expr.Is(leftOperand.copy(), rightOperand.copy(),
					attributes());
		}
		
		public String toString() {
			String lhs = leftOperand.toString();
			String rhs = rightOperand.toString();
			if(needsBraces(leftOperand)) {
				lhs = "(" + lhs + ")";
			}			
			return lhs + " is " + rhs;
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

		@Override
		public boolean equivalent(Expr e) {
			if (getClass() == e.getClass()) {
				Quantifier v = (Quantifier) e;
				// Qualitications are identical.
				return pattern.equivalent(v.pattern)
						&& operand.equivalent(v.operand);
			} else {
				return false;
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

		@Override
		public Expr.ForAll copy() {
			return new Expr.ForAll(pattern.copy(),
					operand.copy(), attributes());
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

		@Override
		public Expr.Exists copy() {
			return new Expr.Exists(pattern.copy(),
					operand.copy(), attributes());
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
		if (!isValidIdentifierStart(x.charAt(0))) {
			return false;
		}
		for (int i = 1; i != x.length(); ++i) {
			if (!isValidIdentifierPart(x.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isValidIdentifierStart(char c) {
		return Character.isJavaIdentifierStart(c) || c == '$' || c == '%';
	}
	
	public static boolean isValidIdentifierPart(char c) {
		return Character.isJavaIdentifierPart(c) || c == '$' || c == '%';
	}
	
	public static boolean equivalent(List<SyntacticType> l1, List<SyntacticType> l2) {
		if(l1.size() != l2.size()) {
			return false;
		} else {
			for(int i=0;i!=l1.size();++i) {
				if(!l1.get(i).equivalent(l2.get(i))) {
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Clone all expressions in a list.
	 * 
	 * @param expressions
	 * @return
	 */
	public static List<Expr> clone(List<Expr> expressions) {
		ArrayList<Expr> result = new ArrayList<Expr>();
		for(int i=0;i!=expressions.size();++i) {
			result.add(expressions.get(i).copy());
		}
		return result;
	}
	
	/**
	 * Clone all types in a list.
	 * 
	 * @param expressions
	 * @return
	 */
	public static List<SyntacticType> cloneTypes(List<SyntacticType> expressions) {
		ArrayList<SyntacticType> result = new ArrayList<SyntacticType>();
		for(int i=0;i!=expressions.size();++i) {
			result.add(expressions.get(i).copy());
		}
		return result;
	}
}
