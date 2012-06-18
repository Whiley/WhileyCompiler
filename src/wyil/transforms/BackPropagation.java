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

import static wybs.lang.SyntaxError.*;

import java.util.*;

import wybs.lang.Builder;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wybs.util.Trie;
import wyil.lang.*;
import wyil.lang.Block.Entry;
import wyil.lang.Code.*;
import wyil.util.*;
import wyil.util.dfa.BackwardFlowAnalysis;
import wyjc.runtime.BigRational;

/**
 * <p>
 * Inserts implict coercions into those methods and functions of a WYIL module.
 * This is done using a backwards dataflow analysis (hence, the name). The idea
 * is that, at any point where the type of a variable changes, a coercion should
 * be inserted. As a simple example, consider the following Whiley function:
 * </p>
 * 
 * <pre>
 * real f(int x):
 *    return x
 * </pre>
 * <p>
 * Here, the type of variable <code>x</code> changes from <code>int</code> to
 * <code>real</code> in the <code>return</code> statement. Before back
 * propagation is applied, the WYIL code for this function looks like this:
 * </p>
 * 
 * <pre>
 * real f(int x):
 *     load x : int                            
 *     return : real
 * </pre>
 * <p>
 * We can see that, at one point, the variable <code>x</code> has type
 * <code>int</code> and it is then immediately assumed to have type
 * <code>real</code>. The semantics of WYIL code dictates that we must
 * explicitly coerce variables in such situations. Thus, after back propagation
 * is applied, we have the following:
 * </p>
 * 
 * <pre>
 * real f(int x):
 *     load x : int 
 *     convert int to real             
 *     return : real
 * </pre>
 * <p>
 * Here, the <code>convert</code> bytecode is responsible for changing the
 * representation of <code>x</code> from <code>int</code> to <code>real</code>.
 * On some architectures, this will correspond to a physical change in the
 * underlying representation. On others, it may simply correspond to a no-op.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class BackPropagation extends BackwardFlowAnalysis<BackPropagation.Env> {	
	private static final HashMap<Integer,Block> afterInserts = new HashMap<Integer,Block>();
	private static final HashMap<Integer,Block.Entry> rewrites = new HashMap<Integer,Block.Entry>();
	
	public BackPropagation(Builder builder) {
		super();
	}
	
	@Override
	protected WyilFile.TypeDef propagate(WyilFile.TypeDef type) {		
		// TODO: back propagate through type constraints
		return type;		
	}
	
	@Override
	protected Env lastStore() {				
		Env environment = new Env();		

		for (int i = 0; i < methodCase.body().numSlots(); i++) {
			environment.add(Type.T_VOID);
		}		
		
		return environment;				
	}
	
	@Override
	protected WyilFile.Case propagate(WyilFile.Case mcase) {		

		// TODO: back propagate through pre- and post-conditions
		
		methodCase = mcase;
		stores = new HashMap<String,Env>();
		afterInserts.clear();
		rewrites.clear();
		
		Env environment = lastStore();		
		propagate(0,mcase.body().size(), environment, Collections.EMPTY_LIST);	
		
		// At this point, we apply the inserts
		Block body = mcase.body();
		Block nbody = new Block(body.numInputs());		
		for(int i=0;i!=body.size();++i) {
			Block.Entry rewrite = rewrites.get(i);			
			if(rewrite != null) {								
				nbody.append(rewrite);				
			} else {
				nbody.append(body.get(i));
			}
			Block afters = afterInserts.get(i);			
			if(afters != null) {								
				nbody.append(afters);				
			} 							
		}
		
		return new WyilFile.Case(nbody, mcase.precondition(),
				mcase.postcondition(), mcase.locals(), mcase.attributes());
	}

	@Override
	protected Env propagate(int index, Entry entry, Env environment) {						
		Code code = entry.code;							
		
		// reset the rewrites for this code, in case it changes
		afterInserts.remove(index);		
		environment = (Env) environment.clone();
		
		if(code instanceof BinOp) {
			infer(index,(BinOp)code,entry,environment);
		} else if(code instanceof Convert) {
			infer(index,(Convert)code,entry,environment);
		} else if(code instanceof Const) {
			infer(index,(Const)code,entry,environment);
		} else if(code instanceof Debug) {
			infer(index,(Debug)code,entry,environment);
		} else if(code instanceof Assert) {
			infer(index,(Assert)code,entry,environment);
		} else if(code instanceof FieldLoad) {
			infer(index,(FieldLoad)code,entry,environment);			
		} else if(code instanceof IndirectInvoke) {
			infer(index,(IndirectInvoke)code,entry,environment);
		} else if(code instanceof IndirectSend) {
			infer(index,(IndirectSend)code,entry,environment);
		} else if(code instanceof Invoke) {
			infer(index,(Invoke)code,entry,environment);
		} else if(code instanceof Invert) {
			infer(index,(Invert)code,entry,environment);
		} else if(code instanceof Label) {
			// skip			
		} else if(code instanceof ListOp) {
			infer(index,(ListOp)code,entry,environment);
		} else if(code instanceof LengthOf) {
			infer(index,(LengthOf)code,entry,environment);
		} else if(code instanceof SubList) {
			infer(index,(SubList)code,entry,environment);
		} else if(code instanceof IndexOf) {
			infer(index,(IndexOf)code,entry,environment);
		} else if(code instanceof Copy) {
			infer(index,(Copy)code,entry,environment);
		} else if(code instanceof Update) {
			infer(index,(Update)code,entry,environment);
		} else if(code instanceof NewDict) {
			infer(index,(NewDict)code,entry,environment);
		} else if(code instanceof NewList) {
			infer(index,(NewList)code,entry,environment);
		} else if(code instanceof NewRecord) {
			infer(index,(NewRecord)code,entry,environment);
		} else if(code instanceof NewSet) {
			infer(index,(NewSet)code,entry,environment);
		} else if(code instanceof NewTuple) {
			infer(index,(NewTuple)code,entry,environment);
		} else if(code instanceof Negate) {
			infer(index,(Negate)code,entry,environment);
		} else if(code instanceof Dereference) {
			infer(index,(Dereference)code,entry,environment);
		} else if(code instanceof Return) {
			infer(index,(Return)code,entry,environment);
		} else if(code instanceof Nop) {
			// skip			
		} else if(code instanceof Send) {
			infer(index,(Send)code,entry,environment);
		} else if(code instanceof SetOp) {
			infer(index,(SetOp)code,entry,environment);
		} else if(code instanceof StringOp) {
			infer(index,(StringOp)code,entry,environment);
		} else if(code instanceof SubString) {
			infer(index,(SubString)code,entry,environment);
		} else if(code instanceof New) {
			infer(index,(New)code,entry,environment);
		} else if(code instanceof Throw) {
			infer(index,(Throw)code,entry,environment);
		} else if(code instanceof TupleLoad) {
			infer(index,(TupleLoad)code,entry,environment);
		} else {			
			internalFailure("unknown: " + code.getClass().getName(),filename,entry);
			return null;
		}	
		
		return environment;
	}

	protected Env infer(int index,
			Code.Assert code, Block.Entry stmt, Env environment) {
		if(code.op == Code.COp.ELEMOF) {
			Type.EffectiveCollection src = (Type.EffectiveCollection) code.type;		
			environment.set(code.leftOperand,src.element());
			environment.set(code.rightOperand,code.type);
		} else {		
			environment.set(code.leftOperand,code.type);
			environment.set(code.rightOperand,code.type);
		}
		
		return environment;
	}
	
	private void infer(int index, Code.BinOp code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);		
		coerceAfter(req,code.type,code.target,index,entry);	
		if(code.bop == BOp.LEFTSHIFT || code.bop == BOp.RIGHTSHIFT) {
			environment.set(code.leftOperand,code.type);
			environment.set(code.rightOperand,Type.T_INT);
		} else if(code.bop == BOp.RANGE){
			environment.set(code.leftOperand,Type.T_INT);
			environment.set(code.rightOperand,Type.T_INT);
		} else {		
			environment.set(code.leftOperand,code.type);
			environment.set(code.rightOperand,code.type);
		}
	}
	
	private void infer(int index, Code.Convert code, Block.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		// TODO: add insertion?
		environment.set(code.operand,code.type);
	}
	
	private void infer(int index, Code.Const code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req,code.constant.type(),code.target,index,entry);		
	}
	
	private void infer(int index, Code.Debug code, Block.Entry entry,
			Env environment) {
		environment.set(code.operand,Type.T_STRING);
	}
	
	private void infer(int index, Code.FieldLoad code, Block.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		Type field = code.type.fields().get(code.field);
		coerceAfter(req,field,code.target,index,entry);		
		environment.set(code.operand, (Type) code.type);				
	}
	
	private void infer(int index, Code.IndirectInvoke code, Block.Entry entry,
			Env environment) {

		if(code.type.ret() != Type.T_VOID && code.target >= 0) {
			Type req = environment.get(code.target);
			coerceAfter(req,code.type.ret(),code.target,index,entry);			
		}
		
		environment.set(code.operand,code.type);
				
		for(int i=0;i!=code.operands.length;++i) {
			int operand = code.operands[i];
			Type type = code.type.params().get(i);
			environment.set(operand,type);
		}		
	}
	
	private void infer(int index, Code.IndirectSend code, Block.Entry entry,
			Env environment) {

		if (code.type.ret() != Type.T_VOID && code.target >= 0) {
			Type req = environment.get(code.target);
			coerceAfter(req, code.type.ret(),code.target, index, entry);
		}

		environment.set(code.operand, code.type);
		environment.set(code.operands[0], code.type.receiver());

		for (int i = 0; i != code.operands.length; ++i) {
			int operand = code.operands[i + 1];
			Type type = code.type.params().get(i);
			environment.set(operand, type);
		}
	}
	
	private void infer(int index, Code.Invoke code, Block.Entry entry,
			Env environment) {

		if(code.type.ret() != Type.T_VOID && code.target >= 0) {
			Type req = environment.get(code.target);
			coerceAfter(req,code.type.ret(),code.target,index,entry);			
		}	
		
		for(int i=0;i!=code.operands.length;++i) {
			int operand = code.operands[i];
			Type type = code.type.params().get(i);
			environment.set(operand,type);
		}			
	}
	
	private void infer(int index, Code.Invert code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		// FIXME: add support for dictionaries
		coerceAfter(req,code.type,code.target,index,entry);
		environment.set(code.operand, code.type);
	}
	
	private void infer(int index, Code.ListOp code, Block.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		Type codeType = (Type) code.type;
		coerceAfter(req,codeType,code.target,index,entry);
		switch(code.operation) {
		case APPEND:
			environment.set(code.leftOperand,codeType);
			environment.set(code.rightOperand,codeType);
			break;
		case LEFT_APPEND:
			environment.set(code.leftOperand,codeType);
			environment.set(code.rightOperand,code.type.element());
			break;
		case RIGHT_APPEND:
			environment.set(code.leftOperand,code.type.element());
			environment.set(code.rightOperand,codeType);
			break;
		}
	}
	
	private void infer(int index, Code.LengthOf code, Block.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		coerceAfter(req,Type.T_INT,code.target,index,entry);
		environment.set(code.operand,(Type) code.type);
	}
	
	private void infer(int index, Code.SubList code, Block.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		Type codeType = (Type) code.type;
		coerceAfter(req,codeType,code.target,index,entry);
		environment.set(code.sourceOperand,codeType);
		environment.set(code.leftOperand,Type.T_INT);
		environment.set(code.rightOperand,Type.T_INT);
	}
	
	private void infer(int index, Code.IndexOf code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req,code.type.value(),code.target,index,entry);		
		environment.set(code.leftOperand,(Type) code.type);
		environment.set(code.rightOperand,code.type.key());				
	}
	
	private void infer(int index, Code.Copy code, Block.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		coerceAfter(req,code.type,code.target,index,entry);
		environment.set(code.operand,code.type);		
	}
	
	private void infer(int index, Code.Update code, Block.Entry stmt,
			Env environment) {
		
		Type src = environment.get(code.target);
		
		if(src == Type.T_VOID) {
			src = code.afterType;
		}
		
		// The first job is to make sure we've got the right types for indices
		// and key values loaded onto the stack.
				
		int i = 0;
		for (Code.LVal lv : code) {
			if (lv instanceof Code.StringLVal || lv instanceof ListLVal) {
				environment.set(code.operands[i++], Type.T_INT);
			} else if (lv instanceof DictLVal) {
				DictLVal dlv = (DictLVal) lv;
				environment.set(code.operands[i++], dlv.type().key());
			} else {
				// RecordLVal and ProcessLVal have no stack requirement
			}
		}
		
		// The second job is to try and determine whether there is any general
		// requirement on the value being assigned.
		
		environment.set(code.operand,code.rhs());		
		environment.set(code.target, code.beforeType);		
	}
	
	private void infer(int index, Code.NewDict code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		
		// TODO: could do better here by rewriting bytecode. For example, if we
		// require a set then changing bytecode to newset makes sense!
	 	
		coerceAfter(req,code.type,code.target,index,entry);
		
		Type key = code.type.key();
		Type value = code.type.value();
		for(int i=0;i!=code.operands.length;++i) {
			int operand = code.operands[i];
			environment.set(operand,key);
			environment.set(operand,value);					
		}					
	}
	
	private void infer(int index, Code.NewRecord code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req, code.type,code.target,index, entry);
		ArrayList<String> keys = new ArrayList<String>(code.type.keys());
		Collections.sort(keys);
		Map<String, Type> fields = code.type.fields();
		for (int i = 0; i != keys.size(); ++i) {
			String key = keys.get(i);
			int operand = code.operands[i];
			environment.set(operand, fields.get(key));
		}
	}
	
	private void infer(int index, Code.NewList code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		// TODO: could do better here by rewriting bytecode. For example, if we
		// require a set then changing bytecode to newset makes sense!

		coerceAfter(req, code.type, code.target, index, entry);
		Type value = code.type.element();
		for (int operand : code.operands) {
			environment.set(operand, value);
		}
	}
	
	private void infer(int index, Code.NewSet code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req,code.type,code.target,index,entry);		
		Type value = code.type.element();
		for (int operand : code.operands) {
			environment.set(operand, value);
		}		
	}
	
	private void infer(int index, Code.NewTuple code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req,code.type,code.target,index,entry);				
		for(int i=0;i!=code.operands.length;++i) {
			int operand = code.operands[i];
			Type type = code.type.element(i);
			environment.set(operand,type);					
		}
	}
	
	private void infer(int index, Code.Return code, Block.Entry entry,
			Env environment) {
		if(code.type != Type.T_VOID) {
			environment.set(code.operand,code.type);
		}
	}
	
	private void infer(int index, Code.Send code, Block.Entry entry,
			Env environment) {		
		
		if(code.type.ret() != Type.T_VOID && code.target >= 0) {
			Type req = environment.get(code.target);
			coerceAfter(req,code.type.ret(),code.target,index,entry);					
		}
		
		environment.set(code.operands[0],code.type.receiver());
		
		for(int i=0;i!=code.operands.length;++i) {
			int operand = code.operands[i+1];
			Type type = code.type.params().get(i);
			environment.set(operand,type);
		}	
	}
	
	private void infer(int index, Code.SetOp code, Block.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		Type codeType = (Type) code.type;
		coerceAfter(req,codeType,code.target,index,entry);
		switch(code.operation) {
		case UNION:
		case INTERSECTION:
		case DIFFERENCE:
			environment.set(code.leftOperand,codeType);
			environment.set(code.rightOperand,codeType);
			break;
		case LEFT_UNION:
		case LEFT_INTERSECTION:
		case LEFT_DIFFERENCE:
			environment.set(code.leftOperand,codeType);
			environment.set(code.rightOperand,code.type.element());
			break;
		case RIGHT_UNION:
		case RIGHT_INTERSECTION:	
			environment.set(code.leftOperand,code.type.element());
			environment.set(code.rightOperand,codeType);
			break;
		}
	}
	
	private void infer(int index, Code.StringOp code, Block.Entry entry,
			Env environment) {				
		Type req = environment.get(code.target);
		coerceAfter(req,Type.T_STRING,code.target,index,entry);
		switch(code.operation) {
		case APPEND:
			environment.set(code.leftOperand,Type.T_STRING);
			environment.set(code.rightOperand,Type.T_STRING);
			break;
		case LEFT_APPEND:
			environment.set(code.leftOperand,Type.T_STRING);
			environment.set(code.rightOperand,Type.T_CHAR);
			break;
		case RIGHT_APPEND:					
			environment.set(code.leftOperand,Type.T_CHAR);
			environment.set(code.rightOperand,Type.T_STRING);
			break;
		}
	}
		
	private void infer(int index, Code.SubString code, Block.Entry entry,
			Env environment) {				
		Type req = environment.get(code.target);
		coerceAfter(req,Type.T_STRING,code.target,index,entry);
		environment.set(code.sourceOperand,Type.T_STRING);
		environment.set(code.leftOperand,Type.T_INT);
		environment.set(code.rightOperand,Type.T_INT);		
	}
	
	private void infer(int index, Code.Negate code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req,code.type,code.target,index,entry);
		environment.set(code.operand,code.type);
	}
	
	private void infer(int index, Code.New code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		Type.Reference tp = (Type.Reference) req;
		// I'm not sure where we should be really applying conversions
		// here??
		// coerce(tp.element(),code.type,index,entry);
		environment.set(code.operand,tp.element());
	}
	
	private void infer(int index, Code.Throw code, Block.Entry entry,
			Env environment) {
		environment.set(code.operand,code.type);
	}
	
	private void infer(int index, Code.TupleLoad code, Block.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req,code.type.elements().get(code.index),code.target,index,entry);		
		environment.set(code.operand,(Type) code.type);
	}
	
	private void infer(int index, Code.Dereference code, Block.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);	
		coerceAfter(req,code.type.element(),code.target,index,entry);		
		environment.set(code.operand,code.type);
	}	
	
	@Override
	protected Env propagate(int index,
			Code.IfGoto code, Entry stmt, Env trueEnv, Env falseEnv) {
		
		Env environment = join(trueEnv,falseEnv);
		
		if(code.op == Code.COp.ELEMOF) {
			Type.EffectiveCollection src = (Type.EffectiveCollection) code.type;		
			environment.set(code.leftOperand,src.element());
			environment.set(code.rightOperand,code.type);
		} else {		
			environment.set(code.leftOperand,code.type);
			environment.set(code.rightOperand,code.type);
		}
		
		return environment;
	}
	
	@Override
	protected Env propagate(int index,
			Code.IfType code, Entry stmt, Env trueEnv, Env falseEnv) {
		
		Env environment = join(trueEnv,falseEnv);
		
		environment.set(code.leftOperand,code.type);
		
		return environment;
	}
	
	@Override
	protected Env propagate(int index, Code.Switch code, Entry stmt,
			List<Env> environments, Env defEnv) {

		Env environment = defEnv;

		for (int i = 0; i != code.branches.size(); ++i) {
			environment = join(environment, environments.get(i));
		}

		environment.set(code.operand, code.type);
		return environment;
	}
	
	@Override
	protected Env propagate(Type handler, Env normalEnv, Env exceptionEnv) {
		// FIXME: if there are any constraints coming from the exceptional
		// environment, then we need to translate them into coercions at the
		// beginning of the catch handler.
		return normalEnv;
	}
		
	@Override
	protected Env propagate(int start, int end, Code.Loop loop,
			Entry stmt, Env environment, List<Pair<Type,String>> handlers) {

		environment = new Env(environment); 

		Env oldEnv = null;
		Env newEnv = null;
		
		do {			
			// iterate until a fixed point reached
			oldEnv = newEnv != null ? newEnv : environment;
			newEnv = propagate(start+1,end, oldEnv, handlers);
			
		} while (!newEnv.equals(oldEnv));
		
		environment = join(environment,newEnv);
		
		if(loop instanceof Code.ForAll) {
			Code.ForAll fall = (Code.ForAll) loop; 								
			environment.set(fall.sourceOperand, (Type) fall.type);			
			// FIXME: a conversion here might be necessary?			
			environment.set(fall.indexOperand,Type.T_VOID);
		} 		
		
		return environment;		
	}
	
	/**
	 * Insert an implicit coercion after a given bytecode.
	 * 
	 * @param to
	 *            --- type being coerced into.
	 * @param from
	 *            --- type being coerced from.
	 * @param target
	 *            --- target register being coerced.
	 * @param index
	 *            --- index of bytecode in function/method body.
	 * @param elem
	 *            --- syntactic element for bytecode.
	 */
	private void coerceAfter(Type to, Type from, int target, int index, SyntacticElement elem) {					
		
		if (to.equals(from) || to == Type.T_VOID) {
			afterInserts.remove(index);
		} else if(to == Type.T_STRING) {
			// this indicates a string conversion is required
			Pair<Type.Function, NameID> p = choseToString(from);
			to = p.first().params().get(0);

			Block block = new Block(0);
			if (!from.equals(to)) {
				block.append(Code.Convert(from, target, target, to),
						elem.attributes());
			}
			block.append(
					Code.Invoke(p.first(), target, new int[] { target },
							p.second()), elem.attributes());
			afterInserts.put(index, block);
		} else {
			Block block = new Block(0);
			block.append(Code.Convert(from, target, target, to), elem.attributes());
			afterInserts.put(index,block);
		}
		
		// this method *should* be structured as follows:
		
//		if (to.equals(from)) {
//			///
//		} else if(Type.isExplicitCoerciveSubtype(to,from)) {					
//			...
//		} else if(to == Type.T_STRING) {
//			...
//		} else {
//			...
//		}
	}	
	
	/**
	 * Choose the best toString method based on the given type.
	 * 
	 * @param from
	 * @return
	 */
	private static Pair<Type.Function,NameID> choseToString(Type type) {
		Type.Function ft;
		NameID name;
		
		if (type == Type.T_BYTE) {
			ft = (Type.Function) Type.Function(Type.T_STRING, Type.T_VOID,
					Type.T_BYTE);
			name = new NameID(Trie.fromString("whiley/lang/Byte"),
					"toString");
		} else if (type == Type.T_CHAR) {
			ft = (Type.Function) Type.Function(Type.T_STRING, Type.T_VOID,
					Type.T_CHAR);
			name = new NameID(Trie.fromString("whiley/lang/Char"),
					"toString");
		} else {
			ft = (Type.Function) Type.Function(Type.T_STRING, Type.T_VOID,
					Type.T_ANY);
			name = new NameID(Trie.fromString("whiley/lang/Any"),
					"toString");
		}
		
		return new Pair<Type.Function,NameID>(ft,name);
	}
	
	private Env join(Env env1, Env env2) {
		if (env2 == null) {
			return env1;
		} else if (env1 == null) {
			return env2;
		}
		
		Env env = new Env();
		for (int i = 0; i != Math.min(env1.size(), env2.size()); ++i) {
			env.add(Type.Union(env1.get(i), env2.get(i)));
		}

		/**
		 * <p>
		 * The following may seem strange, but it's necessary to support
		 * constraint failures which can happen in the middle of expressions. In
		 * such case, a conditional is inserted by the "constraint inline"
		 * phase, where it fails on one side but succeeds on the other. Stack
		 * requirements may be present from the succeeding branch and we need to
		 * propagate those backwards still.
		 * </p>
		 * <p>
		 * There are possibly other ways this could be handled. For example, we
		 * might use null to signal that a particular branch is heading
		 * immediately into a fail statement.
		 * </p>
		 */		
		if(env1.size() > env2.size()) {
			for(int i=env.size();i!=env1.size();++i) {
				env.add(env1.get(i));
			}
		} else if(env2.size() > env1.size()) {
			for(int i=env.size();i!=env2.size();++i) {
				env.add(env2.get(i));
			}
		}				
		
		return env;
	}
	
	protected static class Env extends ArrayList<Type> {
		public Env() {
		}
		public Env(Collection<Type> v) {
			super(v);
		}		
		public Env clone() {
			return new Env(this);
		}
	}
}
