package wycs.solver;

import java.util.HashMap;

import wybs.lang.SyntacticElement;
import wycs.lang.*;

public class TypePropagation {
	private String filename;
	private HashMap<String, SemanticType> fnEnvironment;

	public void propagate(WycsFile wf) {
		fnEnvironment = new HashMap<String, SemanticType>();

		for (Stmt s : wf.stmts()) {
			propagate(s);
		}
	}

	private void propagate(Stmt s) {
		if(s instanceof Stmt.Function) {
			propagate((Stmt.Function)s);
		} else if(s instanceof Stmt.Assert) {
			propagate((Stmt.Assert)s);
		}
	}
	
	private void propagate(Stmt.Function s) {
		
	}
	
	private void propagate(Stmt.Assert s) {
		HashMap<String,SemanticType> environment = new HashMap<String,SemanticType>();
		SemanticType t = propagate(s.expr, environment);
		checkIsSubtype(SemanticType.Bool,t, s.expr);
	}
	
	private SemanticType propagate(Expr e, HashMap<String, SemanticType> fnEnvironment) {
		
	}
	
	/**
	 * Check that t1 :> t2 or, equivalently, that t2 is a subtype of t1
	 * 
	 * @param t1
	 * @param t2
	 * @param element
	 */
	private void checkIsSubtype(SemanticType t1, SemanticType t2, SyntacticElement element) {
		
	}
}
