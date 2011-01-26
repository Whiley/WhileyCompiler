package wyone.theory.type;

import java.util.*;
import wyil.lang.Type;
import wyone.core.*;

public class WTypeDecl extends WConstructor<WVariable> implements WConstraint {
	protected final Type type;
	
	public WTypeDecl(Type type, WVariable var) {
		super("<:" + type.toString(),var);
		this.type = type;	
	}
	
	public Type declType() {
		return type;
	}
	
	public WVariable var() {
		return (WVariable) subterms().get(0);
	}		
	
	public Type type(SolverState st) {
		return Type.T_BOOL;
	}
		
	public WConstraint substitute(Map<WExpr,WExpr> binding) {
		return null;
	}	
}
