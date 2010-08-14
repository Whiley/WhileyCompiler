package wyil.check;

import java.util.*;
import wyil.util.*;
import wyil.lang.*;
import wyil.dfa.*;

/**
 * <p>
 * The purpose of this class is to check that all variables are defined before
 * being used. For example:
 * </p>
 * 
 * <pre>
 * int f() {
 * 	int z;
 * 	return z + 1;
 * }
 * </pre>
 * 
 * <p>
 * In the above example, variable z is used in the return statement before it
 * has been defined any value. This is considered a syntax error in whiley.
 * </p>
 * @author djp
 * 
 */
public class DefiniteAssignment extends ForwardAnalysis<IntersectionFlowSet<String>> implements ModuleCheck {
	private String filename;
	
	public void check(Module module) {
		filename = module.filename();
		for(Module.Method method : module.methods()) {
			check(method);
		}
	}
	
	public void check(Module.Method method) {
		for(Module.Case cas : method.cases()) {
			check(cas);
		}
	}
	
	public void check(Module.Case cas) {
		// the undefined set contains the names of all variables and registers
		// which are defined at any given point.
		HashSet<String> defined = new HashSet<String>();
		
		for(String p : cas.parameterNames()) {
			defined.add(p);
		}
	
		start(cas,new IntersectionFlowSet<String>(defined),new IntersectionFlowSet<String>());
	}
	
	public IntersectionFlowSet<String> transfer(Code stmt, IntersectionFlowSet<String> in) {		
		HashSet<String> uses = new HashSet<String>(); 
		Code.usedVariables(stmt,uses);
		
		if(stmt instanceof Code.Assign) {
			Code.Assign ca = (Code.Assign) stmt;			
			if(ca.lhs instanceof CExpr.Variable) {
				CExpr.Variable v = (CExpr.Variable) ca.lhs;
				uses.remove(v.name);
				checkUses(uses,in);
				in = in.add(v.name);
			} else if(ca.lhs instanceof CExpr.Variable) {
				CExpr.Register v = (CExpr.Register) ca.lhs;
				uses.remove("%" + v.index);
				checkUses(uses,in);
				in = in.add("%" + v.index);
			}
		} else {
			checkUses(uses,in);
		}
		return in;
	}
	
	private void checkUses(HashSet<String> uses,
			IntersectionFlowSet<String> in, Attribute.Source src) {		
		for(String v : uses) {			
			if(!in.contains(v)) {				
				throw new SyntaxError("variable " + v
						+ " might not be initialised", filename, src.start,
						src.end);
			}
		}
	}
	
	public IntersectionFlowSet<String> transfer(boolean branch, Code.IfGoto stmt,
			IntersectionFlowSet<String> in) {
		HashSet<String> uses = new HashSet<String>(); 
		Code.usedVariables(stmt,uses);
		checkUses(uses,in);
		return in;
	}
}
