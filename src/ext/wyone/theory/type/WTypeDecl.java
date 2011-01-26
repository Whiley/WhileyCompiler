package wyone.theory.type;

import java.util.*;
import wyil.lang.Type;
import wyone.core.*;

public class WTypeDecl extends WConstructor<WExpr> implements WConstraint {
	protected final Type type;
	
	public WTypeDecl(Type type, WVariable var) {
		super("<:" + type.toString(),var);
		this.type = type;	
	}
	
	public Type type(SolverState st) {
		return Type.T_BOOL;
	}
	
	public int compareTo(WExpr e) {
		return 0;
	}
	
	public WConstraint substitute(Map<WExpr,WExpr> binding) {
		return null;
	}
	
	public List<WExpr> subterms() {
		return null;
	}
}
