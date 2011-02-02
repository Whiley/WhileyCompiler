package wyone.core;

import java.util.*;

import wyil.lang.Type;
import wyone.theory.numeric.*;
import wyone.util.Pair;
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
	
	public Subtype not() {
		return new Subtype(!sign,lhs(),rhs());
	}
	public Constraint substitute(Map<Constructor,Constructor> binding) {
		Constructor rhs = rhs();		
		Constructor nrhs = rhs.substitute(binding);
		
		// need to check the type here!
		if(nrhs instanceof Value) {
			Value v = (Value) nrhs;
			if(Type.isSubtype(type,v.type())) {				
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
	 * <p>
	 * The subtype closure rule simply checks for conflicting or redundant type
	 * constraints on a particular variable. For example, if we have
	 * <code>x <: int</code> and <code>x <: [int]</code> then we have a
	 * conflict. Similarly, if we have <code>x <: int</code> and
	 * <code>x <: real</code> then the latter constraint is redundant, and can
	 * be eliminated.
	 * </p>
	 * <p>
	 * Subtype closure must also rewrite complex subtype constraints into
	 * simpler ones, where possible. For example, <code>2*x <: int</code>
	 * rewrites to <code>2*x == #1 && int #1</code> (where #1 is a fresh
	 * variable).
	 * </p>
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
				Subtype st = (Subtype) nlit;
				if(st.rhs() instanceof Rational) {
					rewriteSubtype(st,state,solver);
				} else {
					inferSubtype(st,state,solver);
				}
			}		
		}
		
		protected void rewriteSubtype(Subtype ws, Solver.State state, Solver solver) {
			// FIXME: somehow this method seems rather like a cludge ...			
			Rational rhs = (Rational) ws.rhs();			
			state.eliminate(ws);
			Variable nv = Variable.freshVar();
			state.infer(new Subtype(ws.sign(),ws.lhs(),nv), solver);			
			Constructor atom = rhs.subterms().get(0);
			Pair<Polynomial,Polynomial> p = rhs.rearrangeFor(atom);
			Constructor l = Numerics.normalise(new Rational(p.first()));
			Constructor f = Numerics.normalise(new Rational(p.second()));			
			Constructor nrhs = Numerics.multiply(Numerics.subtract(l,nv), new Rational(rhs.denominator()));								
			nrhs = Numerics.divide(nrhs,f);			
			// finally, infer the equality that binds this altogether
			state.infer(Equality.equals(atom,nrhs), solver);						
		}
		
		protected void inferSubtype(Subtype ws, Solver.State state,
				Solver solver) {			
			Type glb = ws.lhs();
			Type diff = Type.T_VOID;
			
			// FIXME: this loop can be further improved now that I have brought
			// in the proper wyil Type
			for(Constraint f : state) {			
				if(f instanceof Subtype) {				
					Subtype st = (Subtype) f;						
					if(st.rhs().equals(ws.rhs())) {
						if(st.sign()) {
							glb = Type.greatestLowerBound(glb,st.lhs());  
						} else {
							// FIXME: will need to do something here. The
							// problem with the idea below, is simply that it's
							// not precise enough.
							// diff = Type.leastUpperBound(diff,st.lhs());
						}
					}
				}
			}
					
			if(ws.sign() && !ws.lhs().equals(glb)) {					
				state.eliminate(ws);
				state.infer(new Subtype(true,glb,ws.rhs()), solver);
			}
		}
	}

}
