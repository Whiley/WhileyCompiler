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

package wyjvm.util.dfa;

import wyjvm.attributes.Code;
import wyjvm.lang.*;

import java.util.*;

/**
 * Represents a generic forward dataflow analysis. This user must implement the
 * transfer functions for the different kinds of bytecode, and the analysis will
 * propagate until a fixed point is reached.
 * 
 * @author David J. Pearce
 * 
 */
public abstract class ForwardFlowAnalysis<T> {
	
	/**
	 * 
	 * @param method
	 */
	public T[] apply(ClassFile.Method method) {
		Code attr = method.attribute(Code.class);
		if (attr == null) {
			// sanity check
			throw new IllegalArgumentException(
					"cannot apply forward flow analysis on method without code attribute");
		}
		List<Bytecode> bytecodes = attr.bytecodes();
		
		// Holds the indices of bytecodes still to be processed. When this set
		// is emtpy, we're done.
		HashSet<Integer> worklist = new HashSet<Integer>();
		worklist.add(0);
		
		// Determine the bytecode index of each declared label.
		HashMap<String,Integer> labels = new HashMap<String,Integer>();
		for(int i=0;i!=bytecodes.size();++i) {
			Bytecode bytecode = bytecodes.get(i);
			if(bytecode instanceof Bytecode.Label) {
				Bytecode.Label label = (Bytecode.Label) bytecode;
				labels.put(label.name, i);
			}
		}
		
		// Stores the abstract store which holds immediately before each
		// bytecode.
		T[] stores = initialise(attr,method);
		
		while(!worklist.isEmpty()) {
			int index = select(worklist);				
			Bytecode bytecode = bytecodes.get(index);
			T store = stores[index];
			
			if(bytecode instanceof Bytecode.Label) {
				// basically, a no-op
				merge(index+1,store,worklist,stores);
			} else if(bytecode instanceof Bytecode.Goto) {
				Bytecode.Goto g = (Bytecode.Goto) bytecode;
				int target = labels.get(g.label);
				merge(target,store,worklist,stores);
			} else if(bytecode instanceof Bytecode.If) {
				Bytecode.If i = (Bytecode.If) bytecode;
				T falseBranch = transfer(index,false,i,store);
				T trueBranch = transfer(index,true,i,store);
				merge(index+1,falseBranch,worklist,stores);
				merge(labels.get(i.label),trueBranch,worklist,stores);
			} else if(bytecode instanceof Bytecode.IfCmp) {
				Bytecode.IfCmp i = (Bytecode.IfCmp) bytecode;
				T falseBranch = transfer(index,false,i,store);
				T trueBranch = transfer(index,true,i,store);
				merge(index+1,falseBranch,worklist,stores);
				merge(labels.get(i.label),trueBranch,worklist,stores);
			} else if(bytecode instanceof Bytecode.Switch) {
				// TODO
			} else if(bytecode instanceof Bytecode.Return) {
				transfer(index,(Bytecode.Return) bytecode,store);
			} else if(bytecode instanceof Bytecode.Throw) {
				transfer(index,(Bytecode.Throw) bytecode,store);
			} else { 
				// all of these bytecodes are sequential and produce a single store.
				if(bytecode instanceof Bytecode.Store) {
					store = transfer(index, (Bytecode.Store)bytecode, store);
				} else if(bytecode instanceof Bytecode.Load) {
					store =  transfer(index, (Bytecode.Load)bytecode, store);
				} else if(bytecode instanceof Bytecode.LoadConst) {
					store = transfer(index, (Bytecode.LoadConst)bytecode, store);
				} else if(bytecode instanceof Bytecode.Iinc) {
					store = transfer(index, (Bytecode.Iinc) bytecode, store);
				} else if(bytecode instanceof Bytecode.ArrayLoad) {
					store = transfer(index, (Bytecode.ArrayLoad) bytecode, store);
				} else if(bytecode instanceof Bytecode.ArrayStore) {
					store = transfer(index, (Bytecode.ArrayStore) bytecode, store);
				} else if(bytecode instanceof Bytecode.ArrayLength) {
					store = transfer(index, (Bytecode.ArrayLength) bytecode, store);
				} else if(bytecode instanceof Bytecode.GetField) {
					store = transfer(index, (Bytecode.GetField) bytecode, store);
				} else if(bytecode instanceof Bytecode.PutField) {
					store = transfer(index, (Bytecode.PutField) bytecode, store);
				} else if(bytecode instanceof Bytecode.CheckCast) {
					store = transfer(index, (Bytecode.CheckCast) bytecode, store);
				} else if(bytecode instanceof Bytecode.InstanceOf) {
					store = transfer(index, (Bytecode.InstanceOf) bytecode, store);
				} else if(bytecode instanceof Bytecode.Invoke) {
					store = transfer(index, (Bytecode.Invoke) bytecode, store);
				} else if(bytecode instanceof Bytecode.Conversion) {
					store = transfer(index, (Bytecode.Conversion) bytecode, store);
				} else if(bytecode instanceof Bytecode.Cmp) {
					store = transfer(index, (Bytecode.Cmp) bytecode, store);
				} else if(bytecode instanceof Bytecode.Pop) {
					store = transfer(index, (Bytecode.Pop) bytecode, store);
				} else if(bytecode instanceof Bytecode.Dup) {
					store = transfer(index, (Bytecode.Dup) bytecode, store);
				} else if(bytecode instanceof Bytecode.DupX1) {
					store = transfer(index, (Bytecode.DupX1) bytecode, store);
				} else if(bytecode instanceof Bytecode.DupX2) {
					store = transfer(index, (Bytecode.DupX2) bytecode, store);
				} else if(bytecode instanceof Bytecode.Swap) {
					store = transfer(index, (Bytecode.Swap) bytecode, store);
				} else if(bytecode instanceof Bytecode.BinOp) {
					store = transfer(index, (Bytecode.BinOp) bytecode, store);
				} else if(bytecode instanceof Bytecode.Neg) {
					store = transfer(index, (Bytecode.Neg) bytecode, store);
				} else if(bytecode instanceof Bytecode.New) {
					store = transfer(index, (Bytecode.New) bytecode, store);
				} else if(bytecode instanceof Bytecode.MonitorEnter) {
					store = transfer(index, (Bytecode.MonitorEnter) bytecode, store);
				} else if(bytecode instanceof Bytecode.MonitorExit) {
					store = transfer(index, (Bytecode.MonitorExit) bytecode, store);
				} else if(bytecode instanceof Bytecode.Nop) {
					store = transfer(index, (Bytecode.Nop) bytecode, store);
				} else {
					// unknown bytecode encountered --- should be dead code.
					throw new RuntimeException(
							"Unknown bytecode encountered in ForwardFlowAnalysis ("
									+ bytecode + ")");
				}
				merge(index + 1, store, worklist, stores);
			}
		}
		
		return stores;
	}
	
	protected int select(HashSet<Integer> worklist) {
		int next = worklist.iterator().next();
		worklist.remove(next);
		return next;
	}
	
	protected void merge(int index, T store, HashSet<Integer> worklist, T[] stores) {
		T old = stores[index];
		if(old == null) {
			stores[index] = store;
			worklist.add(index);
		} else {
			if(merge(index,old,store)) {
				worklist.add(index);
			}
		}
	}
	
	/**
	 * Generate an array of stores, one for each bytecode in the given method.
	 * The initial store (i.e. at index zero) is the initial store for the
	 * method.
	 * 
	 * initial store for the given method.
	 * 
	 * @param attribute
	 *            --- code attribute being analysed.
	 * @param method
	 *            --- enclosing method.
	 * @return
	 */
	public abstract T[] initialise(Code attribute, ClassFile.Method method);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * store bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Store code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * load bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Load code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * loadconst bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.LoadConst code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * arrayload bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.ArrayLoad code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * arraystore bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.ArrayStore code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * getfield bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.GetField code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * arraystore bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.PutField code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * arraystore bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.ArrayLength code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * invoke bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Invoke code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * throw bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract void transfer(int index, Bytecode.Throw code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * return bytecode to an incoming store. 
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract void transfer(int index, Bytecode.Return code, T store);
		
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * an iinc bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Iinc code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a binop bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.BinOp code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a neg bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Neg code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a new bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.New code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a checkcast bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.CheckCast code, T store);

	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a convert bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Conversion code, T store);

	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a instanceof bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.InstanceOf code, T store);

	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a pop bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Pop code, T store);

	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a dup bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Dup code, T store);

	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a dup_x1 bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.DupX1 code, T store);

	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a dup_x2 bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.DupX2 code, T store);

	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a swap bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Swap code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a cmp bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Cmp code, T store);

	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a nop bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.Nop code, T store);

	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a monitorenter bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.MonitorEnter code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of
	 * a monitorexit bytecode to an incoming store.
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param bytecode
	 *            --- bytecode to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, Bytecode.MonitorExit code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * given bytecode to an incoming store. In this case, the bytecode in
	 * question is branching, and we must consider which branch is being
	 * analysed (either the true branch, or the false branch).
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param branch
	 *            --- indicates the true or false branch is to be considered.
	 * @param bytecode
	 *            --- to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, boolean branch, Bytecode.If code, T store);
	
	/**
	 * Generate an updated a abstract store by apply the abstract effect(s) of a
	 * given bytecode to an incoming store. In this case, the bytecode in
	 * question is branching, and we must consider which branch is being
	 * analysed (either the true branch, or the false branch).
	 * 
	 * @param index
	 *            --- index in bytecode array of bytecode being analysed.
	 * @param branch
	 *            --- indicates the true or false branch is to be considered.
	 * @param bytecode
	 *            --- to be analysed.
	 * @param store
	 *            --- incoming abstract store.
	 * @return
	 */
	public abstract T transfer(int index, boolean branch, Bytecode.IfCmp code, T store);
	
	/**
	 * Merge one abstract store into another to form a store at a join point in
	 * the control-flow graph.
	 * 
	 * @param original
	 *            --- original store to join "into". In the case of no change,
	 *            this should be returned.
	 * @param update
	 *            --- new store to join "into" the original store.
	 * @return --- true if the store was changed.
	 */
	public abstract boolean merge(int index, T original, T udpate);
}
