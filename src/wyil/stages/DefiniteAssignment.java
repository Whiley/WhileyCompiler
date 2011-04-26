// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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
		
		if(method.type().receiver() != null) {
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
	
	public Pair<Stmt, List<HashSet<String>>> propagate(Code.Switch sw,
			Stmt stmt, HashSet<String> in) {
		HashSet<String> uses = Code.usedVariables(stmt.code);
		checkUses(uses, in, stmt);
		ArrayList<HashSet<String>> stores = new ArrayList();
		for (int i = 0; i != sw.branches.size(); ++i) {
			stores.add(in);
		}
		return new Pair(stmt, stores);
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
