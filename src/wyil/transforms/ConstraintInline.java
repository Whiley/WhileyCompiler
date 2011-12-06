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

import java.math.BigInteger;
import java.util.*;

import wyil.*;
import wyil.lang.*;
import wyil.util.Pair;
import wyil.util.ResolveError;
import wyil.util.SyntacticElement;
import static wyil.util.SyntaxError.*;
import wyjc.runtime.BigRational;

/**
 * The purpose of this transform is two-fold:
 * <ol>
 * <li>To inline preconditions for method invocations.</li>
 * <li>To inline preconditions for division and list/dictionary access expressions</li>
 * <li>To inline postcondition checks. This involves generating the appropriate
 * shadows for local variables referenced in post-conditions</li>
 * <li>To inline dispatch choices into call-sites. This offers a useful
 * optimisation in situations when we can statically determine that a subset of
 * cases is the dispatch target.</li>
 * </ol>
 * 
 * @author David J. Pearce
 * 
 */
public class ConstraintInline implements Transform {
	private final ModuleLoader loader;	
	private String filename;
	
	public ConstraintInline(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public void apply(Module module) {
		this.filename = module.filename();
		
		for(Module.TypeDef type : module.types()) {
			module.add(transform(type));
		}		
		for(Module.Method method : module.methods()) {
			module.add(transform(method));
		}
	}
	
	public Module.TypeDef transform(Module.TypeDef type) {
		Block constraint = type.constraint();
		
		if (constraint != null) {
			int freeSlot = constraint.numSlots();
			Block nconstraint = new Block(1);
			for (int i = 0; i != constraint.size(); ++i) {
				Block.Entry entry = constraint.get(i);
				Block nblk = transform(entry, freeSlot, null, null);
				if (nblk != null) {
					nconstraint.append(nblk);
				}
				nconstraint.append(entry);
			}
			constraint = nconstraint;
		}
		
		return new Module.TypeDef(type.modifiers(), type.name(), type.type(), constraint,
				type.attributes());
	}
	
	public Module.Method transform(Module.Method method) {
		ArrayList<Module.Case> cases = new ArrayList<Module.Case>();
		for(Module.Case c : method.cases()) {
			cases.add(transform(c,method));
		}
		return new Module.Method(method.modifiers(), method.name(), method.type(), cases);
	}
	
	public Module.Case transform(Module.Case mcase, Module.Method method) {	
		Block body = mcase.body();				
		Block nbody = new Block(body.numInputs());		
		int freeSlot = buildShadows(nbody,mcase,method);		
		
		for(int i=0;i!=body.size();++i) {
			Block.Entry entry = body.get(i);
			Block nblk = transform(entry,freeSlot,mcase,method);			
			if(nblk != null) {								
				nbody.append(nblk);				
			} 					
			nbody.append(entry);
		}
		
		return new Module.Case(nbody, mcase.precondition(),
				mcase.postcondition(), mcase.locals(), mcase.attributes());
	}	
	
	/**
	 * <p>
	 * The build shadows method is used to create "shadow" copies of a
	 * function/method's parameters on entry to the method. This is necessary
	 * when a postcondition exists, as the postcondition may refer to the
	 * parameter values. In such case, however, the semantics of the language
	 * dictate that the postcondition refers to the parameter values <i>as they
	 * were on entry to the method</i>.
	 * </p>
	 * 
	 * <p>
	 * Thus, we must copy the parameter values into their shadows in the case
	 * that they are modified later on. This is potentially inefficient if none,
	 * or only some of the parameters are mentioned in the postcondition.
	 * However, a later pass could optimise this away as the copying assignmens
	 * would be dead-code.
	 * </p>
	 * 
	 * @param body
	 * @param mcase
	 * @param method
	 * @return
	 */
	public int buildShadows(Block body, Module.Case mcase,
			Module.Method method) {
		int freeSlot = mcase.body().numSlots();
		if (mcase.postcondition() != null) {
			//
			List<Type> params = method.type().params();
			for (int i = 0; i != params.size(); ++i) {
				Type t = params.get(i);
				body.append(Code.Load(t, i));
				body.append(Code.Store(t, i + freeSlot));
			}
			freeSlot += params.size();
		}
		return freeSlot;
	}
	
	public Block transform(Block.Entry entry, int freeSlot,
			Module.Case methodCase, Module.Method method) {
		Code code = entry.code;
		
		try {
			// TODO: add support for indirect invokes and sends
			if(code instanceof Code.Invoke) {
				return transform((Code.Invoke)code, freeSlot, entry);
			} else if(code instanceof Code.Send) {

			} else if(code instanceof Code.ListLoad) {
				return transform((Code.ListLoad)code,freeSlot,entry);
			} else if(code instanceof Code.DictLoad) {

			} else if(code instanceof Code.Update) {

			} else if(code instanceof Code.BinOp) {
				return transform((Code.BinOp)code,freeSlot,entry);
			} else if(code instanceof Code.Return) {
				return transform((Code.Return)code,freeSlot,entry,methodCase,method);
			}
		} catch(ResolveError e) {
			syntaxError(e.getMessage(),filename,entry,e);
		} catch(Throwable e) {
			internalFailure(e.getMessage(),filename,entry,e);
		}
		
		return null;
	}

	/**
	 * For the invoke bytecode, we need to inline any preconditions associated
	 * with the target.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.Invoke code, int freeSlot, SyntacticElement elem) throws ResolveError {		
		Block precondition = findPrecondition(code.name,code.type);		
		if(precondition != null) {			
			Block blk = new Block(0);
			List<Type> paramTypes = code.type.params();
			
			// TODO: mark as check block
			
			HashMap<Integer,Integer> binding = new HashMap<Integer,Integer>();
			for(int i=paramTypes.size()-1;i>=0;--i) {				
				blk.append(Code.Store(paramTypes.get(i), freeSlot+i),attributes(elem));
				binding.put(i,freeSlot+i);
			}
			
			precondition = Block.resource(precondition, elem.attribute(Attribute.Source.class));
			blk.importExternal(precondition,binding);
			
			for(int i=0;i<paramTypes.size();++i) {				
				blk.append(Code.Load(paramTypes.get(i), freeSlot+i),attributes(elem));
			}
			return blk;
		}
		
		return null;
	}		
	
	/**
	 * For the send bytecode, we need to inline any preconditions associated
	 * with the target.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.Send code, SyntacticElement elem) {
		return null;
	}

	/**
	 * For the return bytecode, we need to inline any postcondition associated
	 * with this function/method.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.Return code, int freeSlot, SyntacticElement elem, 
			Module.Case methodCase, Module.Method method) {
		
		if(code.type != Type.T_VOID) {
			Block postcondition = methodCase.postcondition();
			if(postcondition != null) {
				Block blk = new Block(0);				
				blk.append(Code.Store(code.type, freeSlot),attributes(elem));				
				HashMap<Integer,Integer> binding = new HashMap<Integer,Integer>();
				binding.put(0,freeSlot);
				Type.Function mtype = method.type();	
				int pIndex = 1;
				if (mtype instanceof Type.Method
						&& ((Type.Method) mtype).receiver() != null) {
					binding.put(pIndex++, Code.THIS_SLOT);
				}
				int shadowIndex = methodCase.body().numSlots();
				for(Type p : mtype.params()) {
					binding.put(pIndex++, shadowIndex++);
				}
				postcondition = Block.resource(postcondition,elem.attribute(Attribute.Source.class));
				blk.importExternal(postcondition,binding);
				blk.append(Code.Load(code.type, freeSlot),attributes(elem));
				return blk;
			}
		}
		
		return null;
	}

	/**
	 * For the listload bytecode, we need to add a check that the index is
	 * within the bounds of the list. 
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.ListLoad code, int freeSlot, SyntacticElement elem) {		
		Block blk = new Block(0);
		// TODO: mark as check block
		blk.append(Code.Store(Type.T_INT, freeSlot),attributes(elem));
		blk.append(Code.Store(code.type, freeSlot+1),attributes(elem));
		String falseLabel = Block.freshLabel();
		String exitLabel = Block.freshLabel();
		blk.append(Code.Load(Type.T_INT, freeSlot),attributes(elem));	
		blk.append(Code.Const(Value.V_INTEGER(BigInteger.ZERO)),attributes(elem));
		blk.append(Code.IfGoto(Type.T_INT, Code.COp.LT, falseLabel),attributes(elem));
		blk.append(Code.Load(Type.T_INT, freeSlot),attributes(elem));	
		blk.append(Code.Load(code.type, freeSlot+1),attributes(elem));
		blk.append(Code.ListLength(code.type),attributes(elem));
		blk.append(Code.IfGoto(Type.T_INT, Code.COp.LT, exitLabel),attributes(elem));
		blk.append(Code.Label(falseLabel),attributes(elem));
		blk.append(Code.Fail("index out of bounds"),attributes(elem));
		blk.append(Code.Label(exitLabel),attributes(elem));
		blk.append(Code.Load(code.type, freeSlot+1),attributes(elem));
		blk.append(Code.Load(Type.T_INT, freeSlot),attributes(elem));
		return blk;		
	}

	/**
	 * For the dictload bytecode, we need to add a check that the key is
	 * contained in the list.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.DictLoad code, SyntacticElement elem) {
		return null;
	}
	
	/**
	 * For the update bytecode, we need to add a check the indices of any lists 
	 * used in the update are within bounds.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.Update code, SyntacticElement elem) {
		return null;
	}

	/**
	 * For the case of a division operation, we need to check that the divisor
	 * is not zero.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.BinOp code, int freeSlot, SyntacticElement elem) {
		
		if(code.bop == Code.BOp.DIV) {
			Block blk = new Block(0);
			// TODO: mark as check block
			blk.append(Code.Store(code.type, freeSlot),attributes(elem));
			String label = Block.freshLabel();
			blk.append(Code.Load(code.type, freeSlot),attributes(elem));
			if(code.type instanceof Type.Int) { 
				blk.append(Code.Const(Value.V_INTEGER(BigInteger.ZERO)),attributes(elem));
			} else {
				blk.append(Code.Const(Value.V_RATIONAL(BigRational.ZERO)),attributes(elem));
			}
			blk.append(Code.IfGoto(code.type, Code.COp.NEQ, label),attributes(elem));
			blk.append(Code.Fail("division by zero"),attributes(elem));
			blk.append(Code.Label(label),attributes(elem));
			blk.append(Code.Load(code.type, freeSlot),attributes(elem));
			return blk;
		} 
		
		// not a division bytecode, so ignore
		return null;					
	}
	
	protected Block findPrecondition(NameID name, Type.Function fun) throws ResolveError {
		Module m = loader.loadModule(name.module());				
		Module.Method method = m.method(name.name(),fun);
	
		for(Module.Case c : method.cases()) {
			// FIXME: this is a hack for now
			return c.precondition();
		}
		return null;
	}
	
	private java.util.List<Attribute> attributes(SyntacticElement elem) {
		return elem.attributes();
	}
}
