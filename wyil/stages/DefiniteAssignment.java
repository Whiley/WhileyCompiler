// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.stages;

import java.util.*;
import wyil.util.*;
import wyil.util.dfa.*;
import wyil.lang.*;
import static wyil.util.SyntaxError.*;

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
public class DefiniteAssignment extends
		ForwardAnalysis<IntersectionFlowSet<String>> implements ModuleTransform {
	private String filename;
	
	public Module apply(Module module) {
		filename = module.filename();
		for(Module.Method method : module.methods()) {
			check(method);
		}
		return module;
	}
	
	public void check(Module.Method method) {
		for(Module.Case cas : method.cases()) {
			check(cas,method);
		}
	}
	
	public void check(Module.Case cas,Module.Method method) {
		// the undefined set contains the names of all variables and registers
		// which are defined at any given point.
		HashSet<String> defined = new HashSet<String>();
		
		for(String p : cas.parameterNames()) {
			defined.add(p);
		}
	
		if(method.type().receiver != null) {
			defined.add("this");
		}
		
		start(cas, new IntersectionFlowSet<String>(defined),
				new IntersectionFlowSet<String>());
	}
	
	public IntersectionFlowSet<String> transfer(Stmt stmt, IntersectionFlowSet<String> in) {		
		Code code = stmt.code;
		HashSet<String> uses = Code.usedVariables(code);		
		String nvar = null;
		if(code instanceof Code.Assign) {
			Code.Assign ca = (Code.Assign) code;			
			if(ca.lhs instanceof CExpr.Variable) {
				CExpr.Variable v = (CExpr.Variable) ca.lhs;				
				uses.remove(v.name);
				nvar = v.name;				
			} else if(ca.lhs instanceof CExpr.Register) {
				CExpr.Register v = (CExpr.Register) ca.lhs;				
				uses.remove("%" + v.index);				
				nvar = "%" + v.index;
			} 
		} else if(code instanceof Code.Forall) {
			Code.Forall ca = (Code.Forall) code;			
			CExpr.Register v = (CExpr.Register) ca.variable;				
			uses.remove("%" + v.index);				
			nvar = "%" + v.index;			
		} else if(code instanceof Code.Induct) {
			Code.Induct ca = (Code.Induct) code;			
			CExpr.Register v = (CExpr.Register) ca.variable;				
			uses.remove("%" + v.index);				
			nvar = "%" + v.index;			
		}		
		
		// FIXME: there is a bug here for the value of a variable after a forall
		// or induction block. The bug is that variables defined by these
		// looping blocks may never be initialise, in the case of immediate
		// termination (e.g. the source is empty).
		
		checkUses(uses,in,stmt);
		
		if(nvar != null) {
			in = in.add(nvar);
		}
		
		return in;
	}
	
	private void checkUses(HashSet<String> uses,
			IntersectionFlowSet<String> in, SyntacticElement elem) {
		for (String v : uses) {
			if (!in.contains(v)) {
				syntaxError("variable " + v + " might not be initialised",
						filename, elem);
			}
		}
	}
	
	public IntersectionFlowSet<String> transfer(boolean branch, Stmt stmt,
			IntersectionFlowSet<String> in) {
		HashSet<String> uses = Code.usedVariables(stmt.code);		
		checkUses(uses,in,stmt);
		return in;
	}
}
