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

package wyc.stages;

import static wyil.util.SyntaxError.*;
import static wyil.util.ErrorMessages.*;

import java.math.BigInteger;
import java.util.*;

import wyc.stages.TypePropagation.Env;
import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Block.Entry;
import wyil.lang.Code.*;
import wyil.util.*;
import wyil.util.dfa.BackwardFlowAnalysis;
import wyjc.runtime.BigRational;

public class BackPropagation extends BackwardFlowAnalysis<BackPropagation.Env> {	
	private static final HashMap<Integer,Block> afterInserts = new HashMap<Integer,Block>();
	private static final HashMap<Integer,Block.Entry> rewrites = new HashMap<Integer,Block.Entry>();
	
	public BackPropagation(ModuleLoader loader) {
		super(loader);
	}
	
	@Override
	public Module.TypeDef propagate(Module.TypeDef type) {		
		// TODO: back propagate through type constraints
		return type;		
	}
	
	@Override
	public Env lastStore() {				
		Env environment = new Env();		

		for (int i = 0; i < methodCase.body().numSlots(); i++) {
			environment.add(Type.T_VOID);
		}		
		
		return environment;				
	}
	
	@Override
	public Module.Case propagate(Module.Case mcase) {		

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
		
		return new Module.Case(nbody, mcase.precondition(),
				mcase.postcondition(), mcase.locals(), mcase.attributes());
	}

	@Override
	public Env propagate(int index, Entry entry, Env environment) {						
		Code code = entry.code;			
		
		// reset the rewrites for this code, in case it changes
		afterInserts.remove(index);
		
		environment = (Env) environment.clone();
		
		if(code instanceof Assert) {
			infer(index,(Assert)code,entry,environment);
		} else if(code instanceof BinOp) {
			infer(index,(BinOp)code,entry,environment);
		} else if(code instanceof Convert) {
			infer(index,(Convert)code,entry,environment);
		} else if(code instanceof Const) {
			infer(index,(Const)code,entry,environment);
		} else if(code instanceof Debug) {
			infer(index,(Debug)code,entry,environment);
		} else if(code instanceof Destructure) {
			infer(index,(Destructure)code,entry,environment);
		} else if(code instanceof DictLength) {
			infer(index,(DictLength)code,entry,environment);
		} else if(code instanceof DictLoad) {
			infer(index,(DictLoad)code,entry,environment);
		} else if(code instanceof Fail) {
			// skip
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
		} else if(code instanceof ListAppend) {
			infer(index,(ListAppend)code,entry,environment);
		} else if(code instanceof ListLength) {
			infer(index,(ListLength)code,entry,environment);
		} else if(code instanceof SubList) {
			infer(index,(SubList)code,entry,environment);
		} else if(code instanceof ListLoad) {
			infer(index,(ListLoad)code,entry,environment);
		} else if(code instanceof Load) {
			infer(index,(Load)code,entry,environment);
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
		} else if(code instanceof ProcLoad) {
			infer(index,(ProcLoad)code,entry,environment);
		} else if(code instanceof Return) {
			infer(index,(Return)code,entry,environment);
		} else if(code instanceof Skip) {
			// skip			
		} else if(code instanceof Send) {
			infer(index,(Send)code,entry,environment);
		} else if(code instanceof Store) {
			infer(index,(Store)code,entry,environment);
		} else if(code instanceof SetUnion) {
			infer(index,(SetUnion)code,entry,environment);
		} else if(code instanceof SetDifference) {
			infer(index,(SetDifference)code,entry,environment);
		} else if(code instanceof SetIntersect) {
			infer(index,(SetIntersect)code,entry,environment);
		} else if(code instanceof SetLength) {
			infer(index,(SetLength)code,entry,environment);
		} else if(code instanceof StringAppend) {
			infer(index,(StringAppend)code,entry,environment);
		} else if(code instanceof StringLength) {
			infer(index,(StringLength)code,entry,environment);
		} else if(code instanceof StringLoad) {
			infer(index,(StringLoad)code,entry,environment);
		} else if(code instanceof SubString) {
			infer(index,(SubString)code,entry,environment);
		} else if(code instanceof Spawn) {
			infer(index,(Spawn)code,entry,environment);
		} else if(code instanceof Throw) {
			infer(index,(Throw)code,entry,environment);
		} else {			
			internalFailure("unknown wyil code (" + code + ")",filename,entry);
			return null;
		}	
		
		return environment;
	}
	
	public void infer(int index, Code.Assert code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(int index, Code.BinOp code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();		
		coerceAfter(req,code.type,index,entry);	
		if(code.bop == BOp.LEFTSHIFT || code.bop == BOp.RIGHTSHIFT) {
			environment.push(code.type);
			environment.push(Type.T_INT);
		} else if(code.bop == BOp.RANGE){
			environment.push(Type.T_INT);
			environment.push(Type.T_INT);
		} else {		
			environment.push(code.type);
			environment.push(code.type);
		}
	}
	
	public void infer(int index, Code.Convert code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		// TODO: add insertion?
		environment.push(code.from);
	}
	
	public void infer(int index, Code.Const code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		coerceAfter(req,code.constant.type(),index,entry);		
	}
	
	public void infer(int index, Code.Debug code, Block.Entry entry,
			Env environment) {
		environment.push(Type.T_STRING);
	}
	
	public void infer(int index, Code.Destructure code, Block.Entry entry,
			Env environment) {
		
		if(code.type instanceof Type.Tuple) {
			Type.Tuple tup = (Type.Tuple) code.type;
			for(Type t : tup.elements()) {
				// FIXME: no coercion?
				environment.pop();
			}			
		} else {
			// FIXME: no coercion?
			environment.pop();
			environment.pop();
		} 
		
		environment.push(code.type);
	}
		
	public void infer(int index, Code.DictLength code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		coerceAfter(req,Type.T_INT,index,entry);
		environment.push(code.type);
	}
	
	public void infer(int index, Code.DictLoad code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		coerceAfter(req,code.type.value(),index,entry);		
		environment.push(code.type);
		environment.push(code.type.key());				
	}		
	
	public void infer(int index, Code.FieldLoad code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		Type field = code.type.fields().get(code.field);
		coerceAfter(req,field,index,entry);		
		environment.push(code.type);				
	}
	
	public void infer(int index, Code.IndirectInvoke code, Block.Entry entry,
			Env environment) {

		if(code.type.ret() != Type.T_VOID && code.retval) {
			Type req = environment.pop();
			coerceAfter(req,code.type.ret(),index,entry);			
		}
		
		environment.push(code.type);
		
		if(code.type instanceof Type.Method) {
			Type.Method mt = (Type.Method) code.type;
			if(mt.receiver() != null) {
				environment.push(mt.receiver());
			}
		}
		
		for(Type t : code.type.params()) {
			environment.push(t);
		}		
	}
	
	public void infer(int index, Code.IndirectSend code, Block.Entry entry,
			Env environment) {				
		
		if(code.type.ret() != Type.T_VOID && code.retval) {
			Type req = environment.pop();
			coerceAfter(req,code.type.ret(),index,entry);			
		}
				
		environment.push(code.type);							
		environment.push(code.type.receiver());		
		
		for(Type t : code.type.params()) {
			environment.push(t);
		}		
	}
	
	public void infer(int index, Code.Invoke code, Block.Entry entry,
			Env environment) {

		if(code.type.ret() != Type.T_VOID && code.retval) {
			Type req = environment.pop();
			coerceAfter(req,code.type.ret(),index,entry);			
		}
		

		if(code.type instanceof Type.Method) {
			Type.Method mt = (Type.Method) code.type;
			if(mt.receiver() != null) {
				environment.push(mt.receiver());
			}						
		}
		
		for(Type t : code.type.params()) {
			environment.push(t);
		}	
		
	}
	
	public void infer(int index, Code.Invert code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		// FIXME: add support for dictionaries
		coerceAfter(req,code.type,index,entry);
		environment.push(code.type);
	}
	
	public void infer(int index, Code.ListAppend code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		coerceAfter(req,code.type,index,entry);
		if(code.dir == OpDir.UNIFORM) { 
			environment.push(code.type);
			environment.push(code.type);
		} else if(code.dir == OpDir.LEFT) {
			environment.push(code.type);
			environment.push(code.type.element());					
		} else {					
			environment.push(code.type.element());
			environment.push(code.type);
		}
	}
	
	public void infer(int index, Code.ListLength code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		coerceAfter(req,Type.T_INT,index,entry);
		environment.push(code.type);
	}
	
	public void infer(int index, Code.SubList code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		coerceAfter(req,code.type,index,entry);
		environment.push(code.type);
		environment.push(Type.T_INT);
		environment.push(Type.T_INT);
	}
	
	public void infer(int index, Code.ListLoad code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		coerceAfter(req,code.type.element(),index,entry);		
		environment.push(code.type);
		environment.push(Type.T_INT);				
	}
	
	public void infer(int index, Code.Load code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		coerceAfter(req,code.type,index,entry);
		environment.set(code.slot,code.type);		
	}
	
	public void infer(int index, Code.Update code, Block.Entry stmt,
			Env environment) {
		
		Type src = environment.get(code.slot);
		
		if(src == Type.T_VOID) {
			src = code.afterType;
		}
		
		// the following is necessary to deal with constraints being propagated
		// through slots, rather than on the stack.
//		if (!code.afterType.equals(src)) {
//			rewrites.put(
//					index,
//					new Block.Entry(Code.Update(src, code.slot, code.level,
//							code.fields), stmt.attributes()));
//		} else {
//			rewrites.remove(index);
//		}
		
		// The first job is to make sure we've got the right types for indices
		// and key values loaded onto the stack.
				
		for(Code.LVal lv : code) {		
			if (lv instanceof Code.StringLVal || lv instanceof ListLVal) {
				environment.push(Type.T_INT);
			} else if (lv instanceof DictLVal) {
				DictLVal dlv = (DictLVal) lv;
				environment.push(dlv.type().key());
			} else {
				// RecordLVal has no stack requirement
			}
		}
		
		// The second job is to try and determine whether there is any general
		// requirement on the value being assigned.
		
		environment.push(code.rhs());		
		environment.set(code.slot, code.beforeType);		
	}
	
	public void infer(int index, Code.NewDict code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		
		// TODO: could do better here by rewriting bytecode. For example, if we
		// require a set then changing bytecode to newset makes sense!
	 	
		coerceAfter(req,code.type,index,entry);
		
		Type key = code.type.key();
		Type value = code.type.value();
		for(int i=0;i!=code.nargs;++i) {
			environment.push(key);
			environment.push(value);					
		}					
	}
	
	public void infer(int index, Code.NewRecord code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		coerceAfter(req,code.type,index,entry);
		ArrayList<String> keys = new ArrayList<String>(code.type.keys());
		Collections.sort(keys);
		Map<String,Type> fields = code.type.fields();
		for (String key : keys) {
			environment.push(fields.get(key));			
		}		
	}
	
	public void infer(int index, Code.NewList code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		// TODO: could do better here by rewriting bytecode. For example, if we
		// require a set then changing bytecode to newset makes sense!
	 	
		coerceAfter(req,code.type,index,entry);		
		Type value = code.type.element();
		for(int i=0;i!=code.nargs;++i) {
			environment.push(value);					
		}	
	}
	
	public void infer(int index, Code.NewSet code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		coerceAfter(req,code.type,index,entry);		
		Type value = code.type.element();
		for(int i=0;i!=code.nargs;++i) {
			environment.push(value);					
		}
	}
	
	public void infer(int index, Code.NewTuple code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		coerceAfter(req,code.type,index,entry);				
		for(Type t : code.type.elements()) {
			environment.push(t);					
		}
	}
	
	public void infer(int index, Code.Return code, Block.Entry entry,
			Env environment) {
		if(code.type != Type.T_VOID) {
			environment.push(code.type);
		}
	}
	
	public void infer(int index, Code.Send code, Block.Entry entry,
			Env environment) {		
		
		if(code.type.ret() != Type.T_VOID && code.retval) {
			Type req = environment.pop();
			coerceAfter(req,code.type.ret(),index,entry);					
		}
		
		environment.push(code.type.receiver());
		
		for(Type t : code.type.params()) {
			environment.push(t);
		}		
	}
	
	public void infer(int index, Code.Store code, Block.Entry entry,
			Env environment) {
		Type src = environment.get(code.slot);
		
		if(src == Type.T_VOID) { src = code.type; }
		
		// the following is necessary to deal with constraints being propagated
		// through slots, rather than on the stack.
		
		if (!code.type.equals(src)) {
			rewrites.put(index, new Block.Entry(Code.Store(src, code.slot),
					entry.attributes()));
		} else {
			rewrites.remove(index);
		}
		
		environment.push(src);
		environment.set(code.slot,Type.T_VOID);
	}
	
	public void infer(int index, Code.SetUnion code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		
		coerceAfter(req,code.type,index,entry);
		if(code.dir == OpDir.UNIFORM) { 
			environment.push(code.type);
			environment.push(code.type);
		} else if(code.dir == OpDir.LEFT) {
			environment.push(code.type);
			environment.push(code.type.element());					
		} else {					
			environment.push(code.type.element());
			environment.push(code.type);
		}
	}

	public void infer(int index, Code.SetIntersect code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		
		coerceAfter(req,code.type,index,entry);
		if(code.dir == OpDir.UNIFORM) { 
			environment.push(code.type);
			environment.push(code.type);
		} else if(code.dir == OpDir.LEFT) {
			environment.push(code.type);
			environment.push(code.type.element());					
		} else {					
			environment.push(code.type.element());
			environment.push(code.type);
		}
	}
	
	public void infer(int index, Code.SetDifference code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		
		coerceAfter(req,code.type,index,entry);
		if(code.dir == OpDir.UNIFORM) { 
			environment.push(code.type);
			environment.push(code.type);
		} else {
			environment.push(code.type);
			environment.push(code.type.element());					
		} 
	}
	
	public void infer(int index, Code.SetLength code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();
		coerceAfter(req,Type.T_INT,index,entry);
		environment.push(code.type);						
	}
	
	public void infer(int index, Code.StringAppend code, Block.Entry entry,
			Env environment) {				
		Type req = environment.pop();
		coerceAfter(req,Type.T_STRING,index,entry);
		if(code.dir == OpDir.UNIFORM) { 
			environment.push(Type.T_STRING);
			environment.push(Type.T_STRING);
		} else if(code.dir == OpDir.LEFT) {
			environment.push(Type.T_STRING);
			environment.push(Type.T_CHAR);					
		} else {					
			environment.push(Type.T_CHAR);
			environment.push(Type.T_STRING);
		}
	}
	
	public void infer(int index, Code.StringLoad code, Block.Entry entry,
			Env environment) {				
		Type req = environment.pop();
		coerceAfter(req,Type.T_CHAR,index,entry);		
		environment.push(Type.T_STRING);
		environment.push(Type.T_INT);
	}
	
	public void infer(int index, Code.StringLength code, Block.Entry entry,
			Env environment) {				
		Type req = environment.pop();
		coerceAfter(req,Type.T_INT,index,entry);
		environment.push(Type.T_STRING);
	}
	
	public void infer(int index, Code.SubString code, Block.Entry entry,
			Env environment) {				
		Type req = environment.pop();
		coerceAfter(req,Type.T_STRING,index,entry);
		environment.push(Type.T_STRING);
		environment.push(Type.T_INT);
		environment.push(Type.T_INT);		
	}
	
	public void infer(int index, Code.Negate code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		coerceAfter(req,code.type,index,entry);
		environment.push(code.type);
	}
	
	public void infer(int index, Code.Spawn code, Block.Entry entry,
			Env environment) {
		Type req = environment.pop();
		Type.Process tp = (Type.Process) req;
		// I'm not sure where we should be really applying conversions
		// here??
		// coerce(tp.element(),code.type,index,entry);
		environment.push(tp.element());
	}
	
	public void infer(int index, Code.Throw code, Block.Entry entry,
			Env environment) {
		environment.push(code.type);
	}
				
	public void infer(int index, Code.ProcLoad code, Block.Entry entry,
			Env environment) {		
		Type req = environment.pop();	
		coerceAfter(req,code.type.element(),index,entry);		
		environment.push(code.type);
	}	
	
	@Override
	public Env propagate(int index,
			Code.IfGoto igoto, Entry stmt, Env trueEnv, Env falseEnv) {
		
		Env environment = join(trueEnv,falseEnv);
		
		if(igoto.op == Code.COp.ELEMOF) {
			Type src = igoto.type;
			Type element;
			
			// FIXME: this is soooo broken
			
			if(src instanceof Type.Set) {
				Type.Set s = (Type.Set) src;
				element = s.element();
			} else {
				Type.List s = (Type.List) src;
				element = s.element();
			}
						
			environment.push(element);
			environment.push(igoto.type);
		} else {		
			environment.push(igoto.type);
			environment.push(igoto.type);
		}
		
		return environment;
	}
	
	@Override
	public Env propagate(int index,
			Code.IfType code, Entry stmt, Env trueEnv, Env falseEnv) {
		
		Env environment = join(trueEnv,falseEnv);
		
		if(code.slot < 0) {			
			environment.push(code.type);			
		} else {
			environment.set(code.slot,code.type);
		}
		
		return environment;
	}
	
	@Override
	public Env propagate(int index, Code.Switch sw,
			Entry stmt, List<Env> environments, Env defEnv) {
		
		Env environment = defEnv;
		
		for(int i=0;i!=sw.branches.size();++i) {
			environment = join(environment,environments.get(i));
		} 
		
		environment.push(sw.type);
		
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
	public Env propagate(int start, int end, Code.Loop loop,
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
			environment.push(fall.type);			
			// FIXME: a conversion here might be necessary?			
			environment.set(fall.slot,Type.T_VOID);
		} 		
		
		return environment;		
	}
	
	public void coerceAfter(Type to, Type from, int index, SyntacticElement elem) {					
		
		if (to.equals(from) || to == Type.T_VOID) {
			afterInserts.remove(index);
		} else if(to == Type.T_STRING) {
			// this indicates a string conversion is required			
			Pair<Type.Function, NameID> p = choseToString(from);
			to = p.first().params().get(0);

			Block block = new Block(0);
			if (!from.equals(to)) {
				block.append(Code.Convert(from, to), elem.attributes());
			}
			block.append(Code.Invoke(p.first(), p.second(), true),
					elem.attributes());
			afterInserts.put(index, block);
		} else {
			Block block = new Block(0);
			block.append(Code.Convert(from, to), elem.attributes());
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
			name = new NameID(ModuleID.fromString("whiley.lang.Byte"),
					"toString");
		} else if (type == Type.T_CHAR) {
			ft = (Type.Function) Type.Function(Type.T_STRING, Type.T_VOID,
					Type.T_CHAR);
			name = new NameID(ModuleID.fromString("whiley.lang.Char"),
					"toString");
		} else {
			ft = (Type.Function) Type.Function(Type.T_STRING, Type.T_VOID,
					Type.T_ANY);
			name = new NameID(ModuleID.fromString("whiley.lang.Any"),
					"toString");
		}
		
		return new Pair<Type.Function,NameID>(ft,name);
	}
	
	public Env join(Env env1, Env env2) {
		if (env2 == null) {
			return env1;
		} else if (env1 == null) {
			return env2;
		}
		Env env = new Env();
		for (int i = 0; i != Math.min(env1.size(), env2.size()); ++i) {
			env.add(Type.Union(env1.get(i), env2.get(i)));
		}

		return env;
	}
	
	public static class Env extends ArrayList<Type> {
		public Env() {
		}
		public Env(Collection<Type> v) {
			super(v);
		}
		public void push(Type t) {
			add(t);
		}
		public Type top() {
			return get(size()-1);
		}
		public Type pop() {
			return remove(size()-1);			
		}
		public Env clone() {
			return new Env(this);
		}
	}
}
