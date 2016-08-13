package wyc.lang;

import static wyc.lang.WhileyFile.internalFailure;

import java.util.HashSet;

import wybs.lang.SyntaxError;
import wyc.lang.WhileyFile.Context;
import wycommon.util.Pair;
import wyil.lang.Type;

public class Exprs {

	/**
	 * Determine the set of variable names (and their types) used in a given expression.
	 *
	 * @param expr
	 * @param context
	 * @return
	 */
	public static HashSet<Pair<Nominal,String>> uses(Expr expr, Context context) {
		HashSet<Pair<Nominal,String>> r = new HashSet<Pair<Nominal,String>>();
		uses(expr,context,r);
		return r;
	}

	private static void uses(Expr expr, Context context, HashSet<Pair<Nominal,String>> uses) {
		try {
			if (expr instanceof Expr.Constant) {
				// do nout
			} else if (expr instanceof Expr.LocalVariable) {
				Expr.LocalVariable lv = (Expr.LocalVariable) expr;
				uses.add(new Pair<Nominal,String>(lv.type,lv.var));

			} else if (expr instanceof Expr.ConstantAccess) {
				// do nout
			} else if (expr instanceof Expr.ArrayInitialiser) {
				Expr.ArrayInitialiser e = (Expr.ArrayInitialiser) expr;
				for(Expr p : e.arguments) {
					uses(p, context, uses);
				}

			} else if (expr instanceof Expr.BinOp) {
				Expr.BinOp e = (Expr.BinOp) expr;
				uses(e.lhs, context, uses);
				uses(e.rhs, context, uses);

			} else if (expr instanceof Expr.Dereference) {
				Expr.Dereference e = (Expr.Dereference) expr;
				uses(e.src, context, uses);

			} else if (expr instanceof Expr.Cast) {
				Expr.Cast e = (Expr.Cast) expr;
				uses(e.expr, context, uses);

			} else if (expr instanceof Expr.IndexOf) {
				Expr.IndexOf e = (Expr.IndexOf) expr;
				uses(e.src, context, uses);
				uses(e.index, context, uses);

			} else if (expr instanceof Expr.UnOp) {
				Expr.UnOp e = (Expr.UnOp) expr;
				uses(e.mhs, context, uses);

			} else if (expr instanceof Expr.FunctionCall) {
				Expr.FunctionCall e = (Expr.FunctionCall) expr;
				for(Expr p : e.arguments) {
					uses(p, context, uses);
				}

			} else if (expr instanceof Expr.MethodCall) {
				Expr.MethodCall e = (Expr.MethodCall) expr;
				for(Expr p : e.arguments) {
					uses(p, context, uses);
				}

			} else if (expr instanceof Expr.IndirectFunctionCall) {
				Expr.IndirectFunctionCall e = (Expr.IndirectFunctionCall) expr;
				uses(e.src, context, uses);
				for(Expr p : e.arguments) {
					uses(p, context, uses);
				}

			} else if (expr instanceof Expr.IndirectMethodCall) {
				Expr.IndirectMethodCall e = (Expr.IndirectMethodCall) expr;
				uses(e.src, context, uses);
				for(Expr p : e.arguments) {
					uses(p, context, uses);
				}

			} else if (expr instanceof Expr.Quantifier) {
				Expr.Quantifier e = (Expr.Quantifier) expr;

				for(Pair<String,Expr> p : e.sources) {
					uses(p.second(), context, uses);
				}
				uses(e.condition, context, uses);
			} else if (expr instanceof Expr.FieldAccess) {
				Expr.FieldAccess e = (Expr.FieldAccess) expr;
				uses(e.src, context, uses);

			} else if (expr instanceof Expr.Record) {
				Expr.Record e = (Expr.Record) expr;
				for(Expr p : e.fields.values()) {
					uses(p, context, uses);
				}

			} else if (expr instanceof Expr.FunctionOrMethod) {
				// do nout
			} else if (expr instanceof Expr.New) {
				Expr.New e = (Expr.New) expr;
				uses(e.expr, context, uses);

			} else {
				// should be dead-code
				internalFailure("unknown expression: "
						+ expr.getClass().getName(), context, expr);
			}
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), context, expr, ex);
		}
	}



	/**
	 * Determine whether this expression is "pure" or not. A pure expression has
	 * no non-local side effects. More specifically, it does not contain: a
	 * direct (or indirect) invocation of a method; or, the construction of an
	 * object via new; or, finally, a dereference operation.
	 *
	 * @return
	 */
	public static boolean isPure(Expr expr, Context context) {
		try {
			if (expr instanceof Expr.Constant) {
				return true;
			} else if (expr instanceof Expr.LocalVariable) {
				return true;
			} else if (expr instanceof Expr.ConstantAccess) {
				return true;
			} else if (expr instanceof Expr.ArrayInitialiser) {
				Expr.ArrayInitialiser e = (Expr.ArrayInitialiser) expr;
				for(Expr p : e.arguments) {
					if(!isPure(p, context)) {
						return false;
					}
				}
				return true;

			} else if (expr instanceof Expr.BinOp) {
				Expr.BinOp e = (Expr.BinOp) expr;
				return isPure(e.lhs, context) && isPure(e.rhs, context);

			} else if (expr instanceof Expr.Dereference) {
				return false;

			} else if (expr instanceof Expr.Cast) {
				Expr.Cast e = (Expr.Cast) expr;
				return isPure(e.expr, context);

			} else if (expr instanceof Expr.IndexOf) {
				Expr.IndexOf e = (Expr.IndexOf) expr;
				return isPure(e.src, context) && isPure(e.index, context);

			} else if (expr instanceof Expr.UnOp) {
				Expr.UnOp e = (Expr.UnOp) expr;
				return isPure(e.mhs, context);

			} else if (expr instanceof Expr.FunctionCall) {
				Expr.FunctionCall e = (Expr.FunctionCall) expr;
				for(Expr p : e.arguments) {
					if(!isPure(p, context)) {
						return false;
					}
				}
				return true;

			} else if (expr instanceof Expr.MethodCall) {
				return false;

			} else if (expr instanceof Expr.IndirectFunctionCall) {
				Expr.IndirectFunctionCall e = (Expr.IndirectFunctionCall) expr;
				if (!isPure(e.src, context)) {
					return false;
				}
				for(Expr p : e.arguments) {
					if(!isPure(p, context)) {
						return false;
					}
				}

				return isPure(e.src, context);
			} else if (expr instanceof Expr.IndirectMethodCall) {
				return false;

			} else if (expr instanceof Expr.Quantifier) {
				Expr.Quantifier e = (Expr.Quantifier) expr;

				for(Pair<String,Expr> p : e.sources) {
					if(!isPure(p.second(), context)) {
						return false;
					}
				}

				return (e.condition == null || isPure(e.condition, context));

			} else if (expr instanceof Expr.FieldAccess) {
				Expr.FieldAccess e = (Expr.FieldAccess) expr;
				return isPure(e.src, context);

			} else if (expr instanceof Expr.Record) {
				Expr.Record e = (Expr.Record) expr;
				for(Expr p : e.fields.values()) {
					if(!isPure(p, context)) {
						return false;
					}
				}
				return true;

			} else if (expr instanceof Expr.FunctionOrMethod) {
				Expr.FunctionOrMethod e = (Expr.FunctionOrMethod) expr;
				return e.type.raw() instanceof Type.Function;

			} else if (expr instanceof Expr.New) {
				return false;

			} else {
				// should be dead-code
				internalFailure("unknown expression: "
						+ expr.getClass().getName(), context, expr);
				return false; // deadcode
			}
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), context, expr, ex);
			return false; // deadcode
		}
	}
}
