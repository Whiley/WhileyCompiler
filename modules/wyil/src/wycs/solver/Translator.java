package wycs.solver;

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
public class Translator {
	/**
	 * The automaton used for rewriting.
	 */
	private Automaton automaton;
	
	/**
	 * The set of constraints being constructed during verification.
	 */
	private ArrayList<Integer> constraints;
	
	public Translator() {
		automaton = new Automaton();
		constraints = new ArrayList<Integer>();
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
		return 0;
	}
}
