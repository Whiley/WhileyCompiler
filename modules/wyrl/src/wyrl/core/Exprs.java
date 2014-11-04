package wyrl.core;

import java.util.HashSet;

import wyrl.util.SyntacticElement;

public class Exprs {

	/**
	 * Determine the set of variables used in a given expression.  Note that this does not include variables which are captured within the expression (e.g. those that are defined in quantifiers)
	 * @param e
	 * @return
	 */
	public static HashSet<String> uses(Expr e) {
		HashSet<String> r = new HashSet<String>();
		uses(e,r);
		return r;
	}

	public static void uses(Expr e, HashSet<String> uses) {
		if(e instanceof Expr.BinOp) {
			Expr.BinOp bop = (Expr.BinOp) e;
			uses(bop.lhs,uses);
			uses(bop.rhs,uses);
		} else if(e instanceof Expr.Cast) {
			Expr.Cast ce = (Expr.Cast) e;
			uses(ce.src,uses);
		} else if(e instanceof Expr.Comprehension) {
			Expr.Comprehension ce = (Expr.Comprehension) e;
			HashSet<String> nuses = new HashSet<String>();
			for(wyrl.util.Pair<Expr.Variable,Expr> p : ce.sources) {
				uses(p.second(),nuses);
			}
			if(ce.condition != null) {
				uses(ce.condition,nuses);
			}
			if(ce.value != null) {
				uses(ce.value,nuses);
			}
			for(wyrl.util.Pair<Expr.Variable,Expr> p : ce.sources) {
				nuses.remove(p.first().var);
			}
			uses.addAll(nuses);
		} else if(e instanceof Expr.Constant) {
			// do nout
		} else if(e instanceof Expr.Constructor) {
			Expr.Constructor ce = (Expr.Constructor) e;
			uses(ce.argument,uses);
		} else if(e instanceof Expr.ListAccess) {
			Expr.ListAccess ce = (Expr.ListAccess) e;
			uses(ce.src,uses);
			uses(ce.index,uses);
		} else if(e instanceof Expr.ListUpdate) {
			Expr.ListUpdate ce = (Expr.ListUpdate) e;
			uses(ce.src,uses);
			uses(ce.index,uses);
			uses(ce.value,uses);
		} else if(e instanceof Expr.NaryOp) {
			Expr.NaryOp ce = (Expr.NaryOp) e;
			for(Expr arg : ce.arguments) {
				uses(arg,uses);
			}
		} else if(e instanceof Expr.Substitute) {
			Expr.Substitute ce = (Expr.Substitute) e;
			uses(ce.src,uses);
			uses(ce.original,uses);
			uses(ce.replacement,uses);
		} else if(e instanceof Expr.TermAccess) {
			Expr.TermAccess ce = (Expr.TermAccess) e;
			uses(ce.src,uses);
		} else if(e instanceof Expr.UnOp) {
			Expr.UnOp ce = (Expr.UnOp) e;
			uses(ce.mhs,uses);
		} else if(e instanceof Expr.Variable) {
			Expr.Variable ce = (Expr.Variable) e;
			if(!ce.isConstructor) {
				uses.add(ce.var);
			}
		} else {
			throw new RuntimeException("Unknown class encountered: " + e.getClass().getName());
		}
	}
}
