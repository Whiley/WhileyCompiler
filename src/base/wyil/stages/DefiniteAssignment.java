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

import wyil.ModuleLoader;
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
		ForwardFlowAnalysis<HashSet<String>> implements ModuleTransform {
	
	public DefiniteAssignment(ModuleLoader loader) {
		super(loader);
	}
			
	public HashSet<String> initialStore() {
		HashSet<String> defined = new HashSet<String>();
		
		for(String p : methodCase.parameterNames()) {
			defined.add(p);
		}
		
		if(method.type().receiver != null) {
			defined.add("this");
		}
		
		return defined;
	}
	
	public Pair<Stmt,HashSet<String>> propagate(Stmt stmt, HashSet<String> in) {						
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
		} 	
				
		checkUses(uses,in,stmt);
		
		if(nvar != null) {
			in = new HashSet<String>(in);			
			in.add(nvar);
		}
		
		return new Pair<Stmt,HashSet<String>>(stmt,in);
	}
		
	public Triple<Stmt, HashSet<String>, HashSet<String>> propagate(
			Code.IfGoto igoto, Stmt stmt, HashSet<String> in) {
		HashSet<String> uses = Code.usedVariables(stmt.code);		
		checkUses(uses,in,stmt);
		return new Triple(stmt,in,in);
	}
	
	public Pair<Block, HashSet<String>> propagate(Code.Start start,
			Code.End end, Block body, Stmt stmt, HashSet<String> in) {
		
		if(start instanceof Code.Forall) {
			in = new HashSet<String>(in);
			Code.Forall fall = (Code.Forall) start;
			in.add(fall.variable.name());
		} else if(start instanceof Code.Induct) {
			in = new HashSet<String>(in);
			Code.Induct ind = (Code.Induct) start;
			in.add(ind.variable.name());
		}
		
		Pair<Block,HashSet<String>> r = propagate(body,in);
		Block blk = new Block();
		blk.add(start);
		blk.addAll(r.first());
		blk.add(end);
		
		if(start instanceof Code.Loop) {
			return new Pair<Block,HashSet<String>>(blk,join(in,r.second()));
		} else {
			return new Pair<Block,HashSet<String>>(blk,r.second());
		}
	}
	
	protected HashSet<String> join(HashSet<String> s1, HashSet<String> s2) {		
		HashSet<String> r = new HashSet<String>();
		// set intersection
		for (String s : s1) {
			if (s2.contains(s)) {
				r.add(s);
			}
		}
		return r;
	}
	
	private void checkUses(HashSet<String> uses,
			HashSet<String> in, SyntacticElement elem) {
		for (String v : uses) {
			if (!in.contains(v)) {
				syntaxError("variable " + v + " might not be initialised",
						filename, elem);
			}
		}
	}
}
