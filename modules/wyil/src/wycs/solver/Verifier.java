package wycs.solver;

import static wybs.lang.SyntaxError.internalFailure;

import java.util.*;
import wyautl.core.*;
import wycs.lang.*;

/**
 * Responsible for converting a <code>WycsFile</code> into an automaton that can
 * then be simplified to set for satisfiability. The key challenge here is to
 * break down the rich language of expressions described by the
 * <code>WycsFile</code> format, such that they can be handled effectively by
 * the <code>Solver</code>.
 * 
 * @author David J. Pearce
 * 
 */
public class Verifier {
	/**
	 * The automaton used for rewriting.
	 */
	private Automaton automaton;
	
	/**
	 * The set of constraints being constructed during verification.
	 */
	private ArrayList<Integer> constraints;
	
	private final String filename;
	
	public Verifier(String filename) {
		this.automaton = new Automaton();
		this.constraints = new ArrayList<Integer>();
		this.filename = filename;
	}
	
	/**
	 * Verify the given list of Wycs statements.
	 * 
	 * @param statements
	 * @return the set of failing assertions (if any).
	 */
	public ArrayList<Integer> verify(List<Stmt> statements) {
		ArrayList<Integer> results = new ArrayList<Integer>();
		for(int i=0;i!=statements.size();++i) {
			Stmt stmt = statements.get(i);
			
			if(stmt instanceof Stmt.Assume) {
				Stmt.Assume a = (Stmt.Assume) stmt;
				constraints.add(translate(a.expr));
			} else if(stmt instanceof Stmt.Assert) {
				Stmt.Assert a = (Stmt.Assert) stmt;
				int assertion = translate(a.expr);
				// TODO: add something here?
			}
		}
		return results;
	}
	
	private int translate(Expr expr) {
		if(expr instanceof Expr.Constant) {
			return translate((Expr.Constant) expr);
		} else if(expr instanceof Expr.Variable) {
			return translate((Expr.Variable) expr);
		} else if(expr instanceof Expr.Binary) {
			return translate((Expr.Binary) expr);
		} else if(expr instanceof Expr.Unary) {
			return translate((Expr.Unary) expr);
		} else if(expr instanceof Expr.Quantifier) {
			return translate((Expr.Quantifier) expr);
		} else {
			internalFailure("unknown: " + expr.getClass().getName(),
					filename, expr);
			return -1; // dead code
		}
	}
	
	private int translate(Expr.Constant expr) {
		
	}
	
	private int translate(Expr.Variable expr) {
		
	}
	
	private int translate(Expr.Binary expr) {
		
	}
	
	private int translate(Expr.Unary expr) {
		
	}
	
	private int translate(Expr.Quantifier expr) {
		
	}
}
