package wyone.theory.type;

import java.util.*;
import wyil.lang.Type;
import wyone.core.*;
import static wyone.core.Constructor.*;

public class WTypeDecl extends Uninterpreted<Variable> implements WConstraint {
	protected final Type type;
	
	public WTypeDecl(Type type, Variable var) {
		super("<:" + type.toString(),var);
		this.type = type;	
	}
	
	public Type declType() {
		return type;
	}
	
	public Variable var() {
		return (Variable) subterms().get(0);
	}		
	
	public Type type(Solver.State st) {
		return Type.T_BOOL;
	}
		
	public WConstraint substitute(Map<Constructor,Constructor> binding) {
		return null;
	}	
}
