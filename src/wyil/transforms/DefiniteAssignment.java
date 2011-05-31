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

package wyil.transforms;

import java.util.*;

import wyil.ModuleLoader;
import wyil.Transform;
import wyil.util.*;
import wyil.util.dfa.*;
import wyil.lang.*;
import static wyil.lang.Block.*;
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
		ForwardFlowAnalysis<HashSet<Integer>> implements Transform {
	
	public DefiniteAssignment(ModuleLoader loader) {
		super(loader);
	}
	
	public HashSet<Integer> initialStore() {
		HashSet<Integer> defined = new HashSet<Integer>();
		
		int start = 0;
		
		if(method.type().receiver() != null) {
			defined.add(0);
			start++;
		}
		
		for(int i=start;i!=method.type().params().size();++i) {
			defined.add(i);
		}				
		
		return defined;
	}
	
	public Pair<Entry,HashSet<Integer>> propagate(Entry entry, HashSet<Integer> in) {						
		Code code = entry.code;			
		
		if(code instanceof Code.Store) {
			Code.Store store = (Code.Store) code;
			in = new HashSet<Integer>(in);			
			in.add(store.slot); 
		} else if(code instanceof Code.Load) {
			Code.Load load = (Code.Load) code;
			if(!in.contains(load.slot)) {
				syntaxError("variable might not be initialised",
						filename, entry);
			}
		}		
		
		return new Pair<Entry,HashSet<Integer>>(entry,in);
	}
		
	public Triple<Entry, HashSet<Integer>, HashSet<Integer>> propagate(
			Code.IfGoto igoto, Entry stmt, HashSet<Integer> in) {
		// nothing to do here
		return new Triple(stmt,in,in);
	}
	
	public Pair<Entry, List<HashSet<Integer>>> propagate(Code.Switch sw,
			Entry stmt, HashSet<Integer> in) {
		ArrayList<HashSet<Integer>> stores = new ArrayList();
		for (int i = 0; i != sw.branches.size(); ++i) {
			stores.add(in);
		}
		return new Pair(stmt, stores);
	}
		
	public Pair<Block, HashSet<Integer>> propagate(Code.Loop loop,
			Block body, Entry stmt, HashSet<Integer> in) {
		
		if(loop instanceof Code.ForAll) {
			in = new HashSet<Integer>(in);
			Code.ForAll fall = (Code.ForAll) loop;
			in.add(fall.var);
		} 
		
		Pair<Block,HashSet<Integer>> r = propagate(body,in);
		Block blk = new Block();
		blk.add(loop);
		blk.addAll(r.first());
		blk.add(Code.End(loop.target));
				
		return new Pair<Block,HashSet<Integer>>(blk,join(in,r.second()));		
	}
	
	protected HashSet<Integer> join(HashSet<Integer> s1, HashSet<Integer> s2) {		
		HashSet<Integer> r = new HashSet<Integer>();
		// set intersection
		for (Integer s : s1) {
			if (s2.contains(s)) {
				r.add(s);
			}
		}
		return r;
	}	
}
