package wyone.core;

import java.util.*;
import wyil.lang.Type;
import wyone.core.*;
import static wyone.core.Constructor.*;

public class Subtype extends Base<Constructor> implements Constraint {
	private boolean sign;
	protected final Type type;
	
	public Subtype(boolean sign, Type type, Constructor e) {
		super(sign ? (":>"+type) : (":!>"+type),e);		
		this.sign = sign;
		this.type = type;	
	}
	
	public boolean sign() {
		return sign;
	}
	
	public Type lhs() {
		return type;
	}
	
	public Constructor rhs() {
		return subterms().get(0);
	}		
	
	public Type type(Solver.State st) {
		return Type.T_BOOL;
	}
	
	public Subtype not() {
		return new Subtype(!sign,lhs(),rhs());
	}
	public Constraint substitute(Map<Constructor,Constructor> binding) {
		Constructor rhs = rhs();		
		Constructor nrhs = rhs.substitute(binding);
		
		// need to check the type here!
		if(nrhs instanceof Value) {
			Value v = (Value) nrhs;
			if(Type.isSubtype(type,v.type(null))) {
				return sign ? Value.TRUE : Value.FALSE;
			} else {
				return sign ? Value.FALSE : Value.TRUE;				
			}
		}
		
		Subtype r;
		
		if(rhs != nrhs) {
			r = new Subtype(sign,type,nrhs); 			
		} else {
			r = this;
		}
		
		Constraint tmp = (Constraint) binding.get(r);
		return tmp != null ? tmp : r;		
	}	
	
	/**
	 * Determine the type of a given expression; that is, the type of the value
	 * that this will evaluate to.
	 */
	public static Type type(Variable e, Solver.State state) {
		Type t = Type.T_ANY;

		// An interesting question here, is whether or not we really do need to
		// build up the lub. If the type combining inference rule is
		// functioning, then shouldn't there only ever be one instance of a
		// matching type declaration?
		
		for(Constraint f : state) {
			if(f instanceof Subtype) {
				// FIXME: probably would make more sense to build up a GLB from
				// all possible types.
				Subtype st = (Subtype) f;
				if(st.rhs().equals(e)) {
					t = Type.leastUpperBound(t,st.lhs());
				}
			}
		}

		return t;
	}
	
	/**
	 * <p>The subtype closure rule simply checks for conflicting or redundant type
	 * constraints on a particular variable. For example, if we have
	 * <code>x <: int</code> and <code>x <: [int]</code> then we have a conflict.
	 * Similarly, if we have <code>x <: int</code> and <code>x <: real</code> then
	 * the latter constraint is redundant, and can be eliminated.</p>
	 * 
	 * @author djp
	 * 
	 */
	public static class Closure implements Solver.Rule {
		
		public String name() {
			return "Type Closure";
		}
		
		public void infer(Constraint nlit, Solver.State state, Solver solver) {				
			if(nlit instanceof Subtype) {			
				inferSubtype((Subtype)nlit,state,solver);			
			}		
		}
		
		protected void inferSubtype(Subtype ws, Solver.State state,
				Solver solver) {
			
			boolean wsign = ws.sign();
			Constructor lhs = ws.rhs();
			Type rhs = ws.lhs();
			
			// FIXME: this loop can be further improved now that I have brought
			// in the proper wyil Type
			for(Constraint f : state) {			
				if(f instanceof Subtype && f!=ws) {				
					Subtype st = (Subtype) f;
					Type st_rhs = st.lhs();				
					if(st.lhs().equals(lhs)) {					
						boolean subst = Type.isSubtype(rhs,st_rhs);
						boolean stsub = Type.isSubtype(st_rhs,rhs);
						boolean signs = wsign == st.sign();
						// ok, this is icky
						if(subst && wsign && signs) {
							// ws is subsumed by st
							state.eliminate(ws);
							return;
						} else if(stsub && wsign && signs) {
							// st is subsumed by ws
							state.eliminate(st);
						} else if(stsub && wsign && !signs) {
							// error
							state.infer(Value.FALSE, solver);
							return;
						} else if(subst && !wsign && !signs) {
							// error
							state.infer(Value.FALSE, solver);
							return;
						} else if(!subst && !stsub && wsign && signs) {
							state.infer(Value.FALSE, solver);
							return;
						}											
					}
				}
			}
		}
	}

}
