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


import static wybs.lang.SyntaxError.*;
import static wyil.util.ErrorMessages.*;

import wybs.lang.Builder;
import wybs.lang.Path;
import wyil.lang.Block;
import wyil.lang.Code;
import wyil.lang.WyilFile;
import wyil.lang.Type;
import wyil.lang.Block.Entry;
import wyil.util.Pair;
import wyil.util.dfa.*;

/**
 * <p>
 * Implements a classic live variables analysis to enable efficient reference
 * counting. Compound structures in Whiley (e.g. lists, sets, records, etc) are
 * <i>reference counted</i>. This means we count the number of aliases to them
 * in the heap and on the stack. Then, when a compound structure is updated, we
 * can perform an <i>inplace update</i> if the reference count is 1; otherwise,
 * we have to clone the structure. Cloning is potentially expensive, and we want
 * to avoid it as much as possible. Therefore, as soon as a variable is no
 * longer live, we should decrement its reference count. This is signalled using
 * the special <code>void</code> bytecode which, literally, "voids" a given
 * register thereby allowing us to release it.
 * </p>
 * <p>
 * The purpose of this class is to determine when a variable is live, and when
 * it is not. The live variables analysis operates as a backwards analysis,
 * propagating information from variable uses (i.e. <code>load</code> bytecodes)
 * back to their definitions (i.e. <code>store</code> bytecodes). For more
 * information on Live Variables Analysis, see <a
 * href="http://en.wikipedia.org/wiki/Live_variable_analysis">the Wikipedia
 * page</a>.
 * </p>
 * 
 * @author David J. Pearce, 2011
 * 
 */
public class LiveVariablesAnalysis extends BackwardFlowAnalysis<LiveVariablesAnalysis.Env>{
	private static final HashMap<Integer,Block.Entry> afterInserts = new HashMap<Integer,Block.Entry>();
	private static final HashMap<Integer,Block.Entry> rewrites = new HashMap<Integer,Block.Entry>();
	private static final HashSet<Integer> deadcode = new HashSet<Integer>();
	
	/**
	 * Determines whether constant propagation is enabled or not.
	 */
	private boolean enabled = getEnable();
	
	public LiveVariablesAnalysis(Builder builder) {
		
	}	
	
	@Override
	public void apply(WyilFile module) {
		if(enabled) {
			super.apply(module);
		}
	}
	

	public static String describeEnable() {
		return "Enable/disable live variables analysis";
	}
	
	public static boolean getEnable() {
		return true; // default value
	}
	
	public void setEnable(boolean flag) {
		this.enabled = flag;
	}
	@Override
	public WyilFile.TypeDeclaration propagate(WyilFile.TypeDeclaration type) {		
		// TODO: back propagate through type constraints
		return type;		
	}
	
	/**
	 * Last store for the live variables analysis is empty, because all
	 * variables are considered to be dead at the end of a method/function.
	 * 
	 * @return
	 */
	@Override
	public Env lastStore() { return EMPTY_ENV; }
	
	@Override
	public WyilFile.Case propagate(WyilFile.Case mcase) {		

		// TODO: back propagate through pre- and post-conditions		
		methodCase = mcase;
		stores = new HashMap<String,Env>();
		afterInserts.clear();
		rewrites.clear();
		deadcode.clear();
		Block body = mcase.body();
		Env environment = lastStore();		
		propagate(0,body.size(), environment, Collections.EMPTY_LIST);	
		
		// First, check and report any dead-code
		for(Integer i : deadcode) {
			syntaxError(errorMessage(DEAD_CODE),
					filename, body.get(i));		
		}
		
		// At this point, we apply the inserts	
		Block nbody = new Block(body.numInputs());		
		for(int i=0;i!=body.size();++i) {
			Block.Entry rewrite = rewrites.get(i);			
			if(rewrite != null) {								
				nbody.append(rewrite);				
			} else {
				nbody.append(body.get(i));
			}
			Block.Entry afters = afterInserts.get(i);			
			if(afters != null) {								
				nbody.append(afters);				
			} 							
		}
		
		return new WyilFile.Case(nbody, mcase.precondition(),
				mcase.postcondition(), mcase.locals(), mcase.attributes());
	}
	
	@Override
	public Env propagate(int index, Entry entry, Env environment) {		
		rewrites.put(index,null);
		Code code = entry.code;		
		boolean isLive = true;
		
		if(code instanceof Code.AbstractAssignable) { 
			Code.AbstractAssignable aa = (Code.AbstractAssignable) code;
			isLive = environment.remove(aa.target);
		} 
		
		if ((isLive && code instanceof Code.AbstractUnaryAssignable)
				|| (code instanceof Code.Dereference)) {
			Code.AbstractUnaryAssignable c = (Code.AbstractUnaryAssignable) code;
			environment.add(c.operand);
		} else if(isLive && code instanceof Code.AbstractUnaryOp) {
			Code.AbstractUnaryOp c = (Code.AbstractUnaryOp) code;
			environment.add(c.operand);
		} else if(isLive && code instanceof Code.AbstractBinaryAssignable) {
			Code.AbstractBinaryAssignable c = (Code.AbstractBinaryAssignable) code;
			environment.add(c.leftOperand);
			environment.add(c.rightOperand);
		} else if(isLive && code instanceof Code.AbstractBinaryOp) {
			Code.AbstractBinaryOp c = (Code.AbstractBinaryOp) code;
			environment.add(c.leftOperand);
			environment.add(c.rightOperand);
		} else if ((isLive && code instanceof Code.AbstractNaryAssignable)
				|| (code instanceof Code.Invoke && ((Code.Invoke) code).type instanceof Type.Method)) {
			Code.AbstractNaryAssignable c = (Code.AbstractNaryAssignable) code;
			for(int operand : c.operands) {
				environment.add(operand);
			}
		} else if ((isLive && code instanceof Code.AbstractSplitNaryAssignable)
				|| (code instanceof Code.IndirectInvoke && ((Code.IndirectInvoke) code).type instanceof Type.Method)
				|| (code instanceof Code.Update && ((Code.Update) code).type instanceof Type.Reference)) {
			Code.AbstractSplitNaryAssignable c = (Code.AbstractSplitNaryAssignable) code;
			environment.add(c.operand);
			for(int operand : c.operands) {
				environment.add(operand);
			}
			
		} else if(!isLive) {
			entry = new Block.Entry(Code.Nop,
					entry.attributes());
			rewrites.put(index, entry);
		} else {
			// const
		}
		
		return environment;
	}
	
	@Override
	public Env propagate(int index, Code.If code, Entry entry, Env trueEnv,
			Env falseEnv) {
		Env r = join(trueEnv, falseEnv);

		r.add(code.leftOperand);
		r.add(code.rightOperand);

		return r;
	}
	
	@Override
	protected Env propagate(Type handler, Env normalEnv, Env exceptionEnv) {
		
		return join(normalEnv, exceptionEnv);
	}
	
	@Override
	public Env propagate(int index,
			Code.IfIs code, Entry entry, Env trueEnv, Env falseEnv) {
		Env r = join(trueEnv,falseEnv);
				
		r.add(code.operand);		
		
		return r;
	}
	
	@Override
	public Env propagate(int index, Code.Switch code,
			Entry stmt, List<Env> environments, Env defEnv) {
		Env environment = defEnv;
		
		for(int i=0;i!=code.branches.size();++i) {
			environment = join(environment,environments.get(i));
		} 		
		
		environment.add(code.operand);
		
		return environment;
	}
		
	public Env propagate(int start, int end, Code.Loop loop,
			Entry stmt, Env environment, List<Pair<Type,String>> handlers) {

		 
		Env oldEnv = null;
		Env newEnv = null;
		
		if(loop instanceof Code.ForAll) {
			environment = new Env(environment);	
		} else {
			environment = EMPTY_ENV;
		}
		
		do {			
			// iterate until a fixed point reached
			oldEnv = newEnv != null ? newEnv : environment;
			newEnv = propagate(start+1,end, oldEnv, handlers);
			
		} while (!newEnv.equals(oldEnv));
		
		environment = newEnv;
		
		if(loop instanceof Code.ForAll) {
			Code.ForAll fall = (Code.ForAll) loop; 		
			// FIXME: is the following really necessary?
			environment.remove(fall.indexOperand);
		} 		
		
		return environment;		
	}
	
	private Env join(Env env1, Env env2) {		
		// implements set union
		Env r = new Env(env1);
		r.addAll(env2);
		return r;
	}
	
	private static final Env EMPTY_ENV = new Env();
	
	public static final class Env extends HashSet<Integer> {
		public Env() {
			super();
		}
		
		public Env(Env env) {
			super(env);
		}
	}
}
