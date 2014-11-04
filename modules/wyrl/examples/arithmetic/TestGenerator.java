import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public final class TestGenerator {

	public interface Expr {
		public boolean isConstant();
		public int evaluate(Map<String,Integer> environment);
	}

	public static final class Variable implements Expr {
		public final String var;

		public Variable(String var) {
			this.var = var;
		}

		public boolean isConstant() {
			return false;
		}

		public int evaluate(Map<String,Integer> environment) {
			return environment.get(var);
		}

		public String toString() {
			return var;
		}
	}

	public static final class Number implements Expr {
		public final int value;

		public Number(int value) {
			this.value = value;
		}

		public boolean isConstant() {
			return true;
		}

		public int evaluate(Map<String,Integer> environment) {
			return value;
		}

		public String toString() {
			return Integer.toString(value);
		}
	}

	public enum BinaryOp {
		ADD{
			public String toString() { return "+"; }
		},
		SUB{
			public String toString() { return "-"; }
		},
		MUL{
			public String toString() { return "*"; }
		},
//		DIV{
//			public String toString() { return "/"; }
//		}
	};

	public static final class Binary implements Expr {
		public final BinaryOp op;
		public final Expr lhs;
		public final Expr rhs;

		public Binary(BinaryOp op, Expr lhs, Expr rhs) {
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public boolean isConstant() {
			return lhs.isConstant() && rhs.isConstant();
		}

		public int evaluate(Map<String, Integer> environment) {
			int l = lhs.evaluate(environment);
			int r = rhs.evaluate(environment);

			switch (op) {
			case ADD:
				return l + r;
			case SUB:
				return l - r;
			case MUL:
				return l * r;
			}

			throw new RuntimeException("Deadcode reached");
		}

		public String toString() {
			String l = lhs.toString();
			String r = rhs.toString();
			if(lhs instanceof Binary) {
				l = "(" + l + ")";
			}
			if(rhs instanceof Binary) {
				r = "(" + r + ")";
			}
			return l + " " + op + " " + r;
		}
	}

	public enum CondOp {
		EQ{
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
		}
	};

	public static final class Condition {
		public final CondOp op;
		public final Expr lhs;
		public final Expr rhs;

		public Condition(CondOp op, Expr lhs, Expr rhs) {
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public boolean evaluate(Map<String, Integer> environment) {
			int l = lhs.evaluate(environment);
			int r = rhs.evaluate(environment);

			switch (op) {
			case EQ:
				return l == r;
			case NEQ:
				return l != r;
			case LT:
				return l < r;
			case LTEQ:
				return l <= r;
			}

			throw new RuntimeException("Deadcode reached");
		}


		public String toString() {
			String l = lhs.toString();
			String r = rhs.toString();
			return l + " " + op + " " + r;
		}
	}

	public static ArrayList<Expr> generateAll(int depth, String[] variables, Integer[] numbers) {
		if(depth == 0) {
			ArrayList<Expr> result = new ArrayList<Expr>();
			for(Integer i : numbers) {
				result.add(new Number(i));
			}
			for(String v : variables) {
				result.add(new Variable(v));
			}
			return result;
		} else {
			ArrayList<Expr> result = generateAll(depth-1,variables,numbers);
			int size = result.size();
			for(int i=0;i!=size;++i) {
				Expr lhs = result.get(i);
				for(int j=0;j!=size;++j) {
					Expr rhs = result.get(j);
//					for(BinaryOp op : BinaryOp.values()) {
//						result.add(new Binary(op,lhs,rhs));
//					}
					result.add(new Binary(BinaryOp.ADD,lhs,rhs));
				}
			}
			return result;
		}
	}

	public static ArrayList<Condition> generateAll(ArrayList<Expr> expressions) {
		Expr zero = new Number(0);
		ArrayList<Condition> result = new ArrayList<Condition>();
		for(int i=0;i!=expressions.size();++i) {
			Expr expr = expressions.get(i);
			if(expr.isConstant()) {
				continue; // skip constant conditions altogether
			}
			for(CondOp op : CondOp.values()) {
					result.add(new Condition(op,zero,expr));
			}
		}
		return result;
	}

	public static void generateClauses(int nTerms,
			ArrayList<Condition> conditions, ArrayList<Condition[]> clauses, ArrayList<Condition> clause) {
		if(nTerms == 0) {
			clauses.add(clause.toArray(new Condition[clause.size()]));
		} else {
			for(int i=0;i!=conditions.size();++i) {
				Condition term = conditions.get(i);
				if(!clause.contains(term)) {
					clause.add(term);
					generateClauses(nTerms-1,conditions,clauses,clause);
					clause.remove(clause.size()-1);
				}
			}
		}
	}

	public static boolean isSatisfiable(Condition[] clause, int var,
			Map<String, Integer> model, String[] variables) {
		if (var == variables.length) {
			for (Condition c : clause) {
				if (!c.evaluate(model)) {
					return false;
				}
			}
			return true;
		} else {
			for (int i = -10; i < 10; ++i) {
				model.put(variables[var], i);
				if (isSatisfiable(clause, var + 1, model, variables)) {
					return true;
				}
			}
			return false;
		}
	}

	public static boolean isSatisfiable(Condition[] clause, String[] variables) {
		return isSatisfiable(clause, 0, new HashMap<String, Integer>(),
				variables);
	}

	public static void splitClauses(ArrayList<Condition[]> clauses,
			ArrayList<Condition[]> sat, ArrayList<Condition[]> unsat,
			String[] variables) {
		for(Condition[] clause : clauses) {
			if(isSatisfiable(clause, variables)) {
				sat.add(clause);
			} else {
				unsat.add(clause);
			}
		}
	}

	public static String toString(Condition[] clause) {
		String r = "";
		for(int i=0;i!=clause.length;++i) {
			if(i != 0) {
				r = r + ", ";
			}
			r = r + clause[i].toString();
		}
		return r;
	}

	public static void main(String[] args) {
		String[] variables = { "x", "y" };
		Integer[] numbers = { -1, 0, 1 };

		ArrayList<Expr> exprs = generateAll(1, variables, numbers);
		ArrayList<Condition> conditions = generateAll(exprs);
		ArrayList<Condition[]> clauses = new ArrayList();
		generateClauses(2,conditions,clauses,new ArrayList<Condition>());
		ArrayList<Condition[]> satisfiable = new ArrayList();
		ArrayList<Condition[]> unsatisfiable = new ArrayList();
		splitClauses(clauses,satisfiable,unsatisfiable,variables);

		for (int i = 0; i != satisfiable.size(); ++i) {
			System.out.println("s " + toString(satisfiable.get(i)));
		}
		for (int i = 0; i != unsatisfiable.size(); ++i) {
			System.out.println("u " + toString(unsatisfiable.get(i)));
		}
	}
}
