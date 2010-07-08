package wyone.theory.type;

import wyone.core.*;
import wyone.theory.logic.*;

public class WTypes {

	/**
	 * Determine the type of a given expression; that is, the type of the value
	 * that this will evaluate to.
	 */
	public static WType type(WExpr e, SolverState state) {
		for(WFormula f : state) {
			if(f instanceof WSubtype) {
				
			}
		}
		return WAnyType.T_ANY;
	}	
}
