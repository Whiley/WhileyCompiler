package wyone.theory.type;

import java.util.*;
import wyil.lang.Type;
import wyone.core.*;
import wyone.core.Constructor.Variable;
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
			if(type.isSubtype(v.type(null), Collections.EMPTY_MAP)) {
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
}
