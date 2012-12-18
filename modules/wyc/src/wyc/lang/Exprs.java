package wyc.lang;

import static wyc.lang.WhileyFile.internalFailure;

import java.util.HashSet;

import wybs.lang.SyntaxError;
import wyc.lang.WhileyFile.Context;
import wyil.lang.Type;
import wyil.util.Pair;

public class Exprs {

	/**
	 * Determine the set of variable names (and their types) used in a given expression.
	 * 
	 * @param expr
	 * @param context
	 * @return
	 */
	public static HashSet<Pair<Type,String>> uses(Expr expr, Context context) {
		HashSet<Pair<Type,String>> r = new HashSet<Pair<Type,String>>();
		uses(expr,context,r);
		return r;
	}
	
	private static void uses(Expr expr, Context context, HashSet<Pair<Type,String>> uses) {
		try {
			if (expr instanceof Expr.Constant) {
				// do nout
			} else if (expr instanceof Expr.LocalVariable) {
				Expr.LocalVariable lv = (Expr.LocalVariable) expr;
				uses.add(new Pair<Type,String>(lv.type.raw(),lv.var));
				
			} else if (expr instanceof Expr.ConstantAccess) {
				// do nout
			} else if (expr instanceof Expr.Set) {
				Expr.Set e = (Expr.Set) expr;
				for(Expr p : e.arguments) { 
					uses(p, context, uses);
				}
				
			} else if (expr instanceof Expr.List) {
				Expr.List e = (Expr.List) expr;
				for(Expr p : e.arguments) { 
					uses(p, context, uses);
				}
				
			} else if (expr instanceof Expr.SubList) {
				Expr.SubList e = (Expr.SubList) expr;
				uses(e.src, context, uses);
				uses(e.start, context, uses);
				uses(e.end, context, uses);
				
			} else if (expr instanceof Expr.SubString) {
				Expr.SubString e = (Expr.SubString) expr;
				uses(e.src, context, uses);
				uses(e.start, context, uses);
				uses(e.end, context, uses);
				
			} else if (expr instanceof Expr.BinOp) {
				Expr.BinOp e = (Expr.BinOp) expr;
				uses(e.lhs, context, uses);
				uses(e.rhs, context, uses);
				
			} else if (expr instanceof Expr.LengthOf) {
				Expr.LengthOf e = (Expr.LengthOf) expr;
				uses(e.src, context, uses);
				
			} else if (expr instanceof Expr.Dereference) {
				Expr.Dereference e = (Expr.Dereference) expr;				
				uses(e.src, context, uses);
				
			} else if (expr instanceof Expr.Convert) {
				Expr.Convert e = (Expr.Convert) expr;
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
				
			} else if (expr instanceof Expr.Comprehension) {
				Expr.Comprehension e = (Expr.Comprehension) expr;
				
				for(Pair<String,Expr> p : e.sources) { 
					uses(p.second(), context, uses);
				}
				uses(e.value, context, uses);
				uses(e.condition, context, uses);
				
			} else if (expr instanceof Expr.RecordAccess) {
				Expr.RecordAccess e = (Expr.RecordAccess) expr;
				uses(e.src, context, uses);
				
			} else if (expr instanceof Expr.Record) {
				Expr.Record e = (Expr.Record) expr;
				for(Expr p : e.fields.values()) { 
					uses(p, context, uses);
				}
				
			} else if (expr instanceof Expr.Tuple) {
				Expr.Tuple e = (Expr.Tuple) expr;
				for(Expr p : e.fields) { 
					uses(p, context, uses);
				}
				
			} else if (expr instanceof Expr.Map) {
				Expr.Map e = (Expr.Map) expr;
				for(Pair<Expr,Expr> p : e.pairs) { 
					uses(p.first(), context, uses);
					uses(p.second(), context, uses);
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
	
	
}
