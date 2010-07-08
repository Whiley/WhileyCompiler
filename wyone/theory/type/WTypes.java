package wyone.theory.type;

import wyone.core.*;
import wyone.theory.logic.*;

public class WTypes {

	public static WSubtype subtypeOf(WExpr lhs, WType rhs) {
		return new WSubtype(true,lhs,rhs);
	}
	
	/**
	 * Determine the type of a given expression; that is, the type of the value
	 * that this will evaluate to.
	 */
	public static WType type(WExpr e, SolverState state) {
		// NOTE: would be nice to make this more efficient
		for(WFormula f : state) {
			if(f instanceof WSubtype) {
				// FIXME: probably would make more sense to build up a LUB from
				// all possible types.
				WSubtype st = (WSubtype) f;
				if(e.equals(st.lhs())) {
					WType t = st.rhs();					
					return t;
				}
			}
		}
		return WAnyType.T_ANY;
	}	
	
	public static WVariable newSkolem(WType t, SolverState state,
			Solver solver) {
		WVariable v = WVariable.freshVar();
		state.infer(WTypes.subtypeOf(v, t), solver);
		return v;
	}
}
