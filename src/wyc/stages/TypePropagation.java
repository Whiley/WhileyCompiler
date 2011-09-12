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

import static wyil.util.SyntaxError.syntaxError;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.util.*;
import wyil.util.dfa.*;

import static wyil.lang.Block.*;

/**
 * <p>
 * The type propagation stage propagates type information in a flow-sensitive
 * fashion from declared parameter and return types through assigned
 * expressions, to determine types for all intermediate expressions and
 * variables. In some cases, this process will actually update the underlying
 * expressions to reflect the correct operator. For example, an expression
 * <code>a+b</code> will always initially be parsed as a CExpr.BinOp with ADD
 * operator. However, if type propagation determines that <code>a</code> and
 * <code>b</code> have set type, then the operator will be updated to a UNION.
 * </p>
 * <p>
 * <b<Note:</b> currently, this stage does not propagate through type definitions.
 * </p>
 * 
 * @author djp
 * 
 */
public class TypePropagation extends ForwardFlowAnalysis<TypePropagation.Env> {

	/**
	 * The rewrites map maps bytecode indices to blocks of code which they are
	 * rewriten into.
	 */
	private final HashMap<Integer,Block> rewrites = new HashMap<Integer,Block>();
	
	public TypePropagation(ModuleLoader loader) {
		super(loader);
	}
	
	public Module.TypeDef propagate(Module.TypeDef type) {
		Block constraint = type.constraint();		
		if(constraint != null) {
			Env environment = new Env();		
			environment.add(type.type());		
			// Now, add space for any other slots needed in the block. This can
			// arise as a result of temporary loop variables, etc.
			int maxSlots = constraint.numSlots();
			for(int i=1;i!=maxSlots;++i) {
				environment.add(Type.T_VOID);
			}
			constraint = doPropagation(constraint,environment);	
			return new Module.TypeDef(type.name(), type.type(), constraint, type.attributes());
		} else {
			return type;
		}
	}
	
	public Module.ConstDef propagate(Module.ConstDef def) {
		// We need to perform type propagation over values in order to handle
		// function constants which have not yet been bound.
		Value v = (Value) infer(def.constant(),def);
		return new Module.ConstDef(def.name(), v, def.attributes());
	}
	
	public Env initialStore() {
				
		Env environment = new Env();		
		
		if(method.type() instanceof Type.Meth) {
			Type.Meth mt = (Type.Meth) method.type();
			if(mt.receiver() != null) {
				environment.add(mt.receiver());
			}
		}
		List<Type> paramTypes = method.type().params();
				
		int i = 0;		
		for (; i != paramTypes.size(); ++i) {
			Type t = paramTypes.get(i);
			environment.add(t);
			if (!(method.type() instanceof Type.Meth)
					&& Type.isCoerciveSubtype(Type.T_PROCESS(Type.T_ANY), t)) {
				// FIXME: add source information
				syntaxError("function argument cannot have process type",
						filename, methodCase);
			}
		}				
				
		return environment;
	}		
	
	public Module.Case propagate(Module.Case mcase) {		
		this.methodCase = mcase;
		
		Env environment = initialStore();
		int start = method.type().params().size();
		if(method.type() instanceof Type.Meth) {
			Type.Meth mt = (Type.Meth) method.type();
			if(mt.receiver() != null) {
				start++;
			}
		}
		
		Block precondition = mcase.precondition();		
		if(precondition != null) {
			Env tmp = environment.clone();
			// Now, add space for any other slots needed in the block. This can
			// arise as a result of temporary loop variables, etc.
			int maxSlots = precondition.numSlots() - precondition.numInputs();
			for(int i=0;i<maxSlots;++i) {
				tmp.add(Type.T_VOID);
			}
			precondition = doPropagation(precondition,tmp);
		}
		
		Block postcondition = mcase.postcondition();		
		if(postcondition != null) {
			Env tmp = environment.clone();
			// Set the return type for special variable "$"
			tmp.add(0,method.type().ret());
			// Now, add space for any other slots needed in the block. This can
			// arise as a result of temporary loop variables, etc.
			int maxSlots = postcondition.numSlots() - postcondition.numInputs();
			for(int i=0;i<maxSlots;++i) {
				tmp.add(Type.T_VOID);
			}
			postcondition = doPropagation(postcondition,tmp);
		}
		
		// Now, propagate through the body
		for (int i = start; i < mcase.body().numSlots(); i++) {
			environment.add(Type.T_VOID);
		}	
						
		Block nbody = doPropagation(mcase.body(),environment);		
				
		// TODO: propagate over pre and post conditions
		
		return new Module.Case(nbody, precondition,
				postcondition, mcase.locals(), mcase.attributes());
	}
	
	protected Block doPropagation(Block blk, Env environment) {
		// reset some of the global state
		this.rewrites.clear();
		this.stores = new HashMap<String,Env>();
		this.block = blk;
		
		// now, perform the propagation
		propagate(0,blk.size(), environment);	
				
		// finally, apply any and all rewrites
		Block nblk = new Block(blk.numInputs());				
		for(int i=0;i!=blk.size();++i) {
			Block rewrite = rewrites.get(i);
			if(rewrite != null) {
				nblk.append(rewrite);
			} else {				
				nblk.append(blk.get(i));
			}
		}
		return nblk;
	}
	
	protected Env propagate(int index, Entry entry,
			Env environment) {
		
		Code code = entry.code;				
		
		environment = (Env) environment.clone();
		
		if(code instanceof Assert) {
			code = infer((Assert)code,entry,environment);
		} else if(code instanceof BinOp) {
			code = infer((BinOp)code,entry,environment);
		} else if(code instanceof Convert) {
			code = infer((Convert)code,entry,environment);
		} else if(code instanceof Const) {
			code = infer((Const)code,entry,environment);
		} else if(code instanceof Debug) {
			code = infer((Debug)code,entry,environment);
		} else if(code instanceof Destructure) {
			code = infer((Destructure)code,entry,environment);
		} else if(code instanceof ExternJvm) {
			// skip
		} else if(code instanceof Fail) {
			code = infer((Fail)code,entry,environment);
		} else if(code instanceof FieldLoad) {
			Block block = infer((FieldLoad)code,entry,environment);
			rewrites.put(index, block);
			return environment;
		} else if(code instanceof IndirectInvoke) {
			code = infer((IndirectInvoke)code,entry,environment);
		} else if(code instanceof IndirectSend) {
			code = infer((IndirectSend)code,entry,environment);
		} else if(code instanceof Invoke) {
			code = infer((Invoke)code,entry,environment);
		} else if(code instanceof Invert) {
			code = infer((Invert)code,entry,environment);
		} else if(code instanceof Label) {
			// skip			
		} else if(code instanceof ListLength) {
			code = infer((ListLength)code,entry,environment);
		} else if(code instanceof SubList) {
			code = infer((SubList)code,entry,environment);
		} else if(code instanceof ListLoad) {
			code = infer((ListLoad)code,entry,environment);
		} else if(code instanceof Load) {
			code = infer((Load)code,entry,environment);
		} else if(code instanceof Update) {
			code = infer((Update)code,entry,environment);
		} else if(code instanceof Negate) {
			code = infer((Negate)code,entry,environment);
		} else if(code instanceof NewDict) {
			code = infer((NewDict)code,entry,environment);
		} else if(code instanceof NewList) {
			code = infer((NewList)code,entry,environment);
		} else if(code instanceof NewRecord) {
			code = infer((NewRecord)code,entry,environment);
		} else if(code instanceof NewSet) {
			code = infer((NewSet)code,entry,environment);
		} else if(code instanceof NewTuple) {
			code = infer((NewTuple)code,entry,environment);
		} else if(code instanceof ProcLoad) {
			code = infer((ProcLoad)code,entry,environment);
		} else if(code instanceof Return) {
			code = infer((Return)code,entry,environment);
		} else if(code instanceof Send) {
			code = infer((Send)code,entry,environment);
		} else if(code instanceof Store) {
			code = infer((Store)code,entry,environment);
		} else if(code instanceof SetUnion) {
			code = infer((Code.SetUnion)code,entry,environment);
		} else if(code instanceof SetDifference) {
			code = infer((Code.SetDifference)code,entry,environment);
		} else if(code instanceof SetIntersect) {
			code = infer((Code.SetIntersect)code, entry,environment);
		} else if(code instanceof Spawn) {
			code = infer((Spawn)code,entry,environment);
		} else if(code instanceof Throw) {
			code = infer((Throw)code,entry,environment);
		} else {
			syntaxError("Unknown wyil code encountered: " + code,filename,entry);
			return null;
		}
		
		Block block = new Block(0);
		block.append(code,entry.attributes());		
		rewrites.put(index, block);
		
		return environment;
	}
	
	protected Code infer(Code.Assert code, Entry stmt, Env environment) {
		return code;
	}
	
	protected Code infer(BinOp v, Entry stmt, Env environment) {		
		Code code = v;
		Type rhs = environment.pop();
		Type lhs = environment.pop();
		Type result;

		boolean lhs_set = Type.isSubtype(Type.T_SET(Type.T_ANY),lhs);
		boolean rhs_set = Type.isSubtype(Type.T_SET(Type.T_ANY),rhs);		
		boolean lhs_list = Type.isSubtype(Type.T_LIST(Type.T_ANY),lhs);
		boolean rhs_list = Type.isSubtype(Type.T_LIST(Type.T_ANY),rhs);
		boolean lhs_str = Type.isSubtype(Type.T_STRING,lhs);
		boolean rhs_str = Type.isSubtype(Type.T_STRING,rhs);
		
		if(lhs_set || rhs_set) {
			environment.push(lhs);
			environment.push(rhs);
			switch(v.bop) {
				case ADD:			
					return inferSetUnion(OpDir.UNIFORM,stmt,environment);
				case SUB:
					return inferSetDifference(OpDir.UNIFORM,stmt,environment);
				default:
					syntaxError("invalid set operation: " + v.bop,filename,stmt);
					result = null;
			}						
		} else if(lhs_str || rhs_str) {			
			Code.OpDir dir;
			
			if(lhs_str && rhs_str) {				
				dir = OpDir.UNIFORM;
			} else if(lhs_str) {				
				dir = OpDir.LEFT;
				checkIsSubtype(Type.T_CHAR,rhs,stmt);
			} else {				
				dir = OpDir.RIGHT;
				checkIsSubtype(Type.T_CHAR,lhs,stmt);
			} 
			
			switch(v.bop) {				
				case ADD:																				
					code = Code.StringAppend(dir);
					break;
				default:
					syntaxError("Invalid string operation: " + v.bop,filename,stmt);					
			}
			
			result = Type.T_STRING;
		} else if(lhs_list || rhs_list) {
			Type.List type;
			Code.OpDir dir;
			
			if(lhs_list && rhs_list) {				
				dir = OpDir.UNIFORM;
			} else if(lhs_list) {
				rhs = Type.T_LIST(rhs);
				dir = OpDir.LEFT;
			} else {
				lhs = Type.T_LIST(lhs);				
				dir = OpDir.RIGHT;
			}
			
			type = Type.effectiveListType(Type.leastUpperBound(lhs,rhs));
			
			switch(v.bop) {				
				case ADD:																				
					code = Code.ListAppend(type,dir);
					break;
				default:
					syntaxError("Invalid list operation: " + v.bop,filename,stmt);		
			}
			
			result = type;
			
		} else {						
			if (v.bop == BOp.BITWISEAND || v.bop == BOp.BITWISEOR
					|| v.bop == BOp.BITWISEXOR) {
				checkIsSubtype(Type.T_BYTE,lhs,stmt);
				checkIsSubtype(Type.T_BYTE,rhs,stmt);
				result = Type.T_BYTE;
			} else if (v.bop == BOp.LEFTSHIFT || v.bop == BOp.RIGHTSHIFT) {
				checkIsSubtype(Type.T_BYTE,lhs,stmt);
				checkIsSubtype(Type.T_INT,rhs,stmt);
				result = Type.T_BYTE;
			} else if(v.bop == BOp.RANGE) {
				checkIsSubtype(Type.T_INT,lhs,stmt);
				checkIsSubtype(Type.T_INT,rhs,stmt);
				result = Type.T_LIST(Type.T_INT);
			} else if(v.bop == BOp.REM) {
				// remainder is a special case which requires both operands to
				// be integers.
				checkIsSubtype(Type.T_INT,lhs,stmt);
				checkIsSubtype(Type.T_INT,rhs,stmt);
				result = Type.T_INT;
			} else if(Type.isCoerciveSubtype(lhs,rhs)) {
				checkIsSubtype(Type.T_REAL,lhs,stmt);
				result = lhs;
			} else {
				checkIsSubtype(Type.T_REAL,lhs,stmt);
				checkIsSubtype(Type.T_REAL,rhs,stmt);				
				result = rhs;
			} 
			if(result == Type.T_CHAR) {
				result = Type.T_INT;
			}
			code = Code.BinOp(result,v.bop);
		}				
		
		environment.push(result);
		
		return code;				
	}
	
	protected Code infer(Code.Convert code, Entry stmt, Env environment) {
		Type from = environment.pop();
		checkIsSubtype(code.to,from,stmt);
		environment.push(code.to);
		return Code.Convert(from, code.to);
	}
	
	protected Code infer(Code.Const code, Entry stmt, Env environment) {
		// we must perform type propagation across values here in order to
		// handle function constants which have not yet been bound.
		code = Code.Const(infer(code.constant,stmt));		
		environment.push(code.constant.type());
		return code;
	}
	
	protected Value infer(Value val, SyntacticElement elem) {
		if (val instanceof Value.Rational || val instanceof Value.Bool
				|| val instanceof Value.Integer || val instanceof Value.Null
				|| val instanceof Value.Strung || val instanceof Value.Char
				|| val instanceof Value.Byte) {
			return val;
		} else if (val instanceof Value.Set) {
			return infer((Value.Set)val,elem);
		} else if (val instanceof Value.List) {
			return infer((Value.List)val,elem);
		} else if (val instanceof Value.Dictionary) {
			return infer((Value.Dictionary)val,elem);
		} else if (val instanceof Value.Record) {
			return infer((Value.Record)val,elem);
		} else if (val instanceof Value.Tuple) {
			return infer((Value.Tuple)val,elem);
		} else {
			return infer((Value.FunConst)val,elem);
		}
	}
	
	protected Value infer(Value.Set val, SyntacticElement elem) {
		ArrayList<Value> nvals =  new ArrayList<Value>();
		for(Value v : val.values) {
			nvals.add(infer(v,elem));
		}
		return Value.V_SET(nvals);
	}
	
	protected Value infer(Value.Tuple val, SyntacticElement elem) {
		ArrayList<Value> nvals =  new ArrayList<Value>();
		for(Value v : val.values) {
			nvals.add(infer(v,elem));
		}
		return Value.V_TUPLE(nvals);
	}
	
	protected Value infer(Value.List val, SyntacticElement elem) {
		ArrayList<Value> nvals =  new ArrayList<Value>();
		for(Value v : val.values) {
			nvals.add(infer(v,elem));
		}
		return Value.V_LIST(nvals);
	}
	
	protected Value infer(Value.Dictionary dict, SyntacticElement elem) {
		HashMap<Value,Value> nvals =  new HashMap<Value,Value>();
		for(Map.Entry<Value,Value> v : dict.values.entrySet()) {
			Value key = infer(v.getKey(),elem);
			Value val = infer(v.getValue(),elem);
			nvals.put(key, val);
		}
		return Value.V_DICTIONARY(nvals);
	}	
	
	protected Value infer(Value.Record record, SyntacticElement elem) {
		HashMap<String,Value> nfields =  new HashMap<String,Value>();
		for(Map.Entry<String,Value> v : record.values.entrySet()) {
			String key = v.getKey();
			Value val = infer(v.getValue(),elem);
			nfields.put(key, val);
		}
		return Value.V_RECORD(nfields);
	}
	
	protected Value infer(Value.FunConst fc, SyntacticElement elem) {
		try {
			List<Type.Fun> targets = lookupMethod(fc.name.module(),fc.name.name());
			String msg;
			if(fc.type == null) {
				if(targets.size() == 1) {
					return Value.V_FUN(fc.name, targets.get(0));
				} 
				msg = "ambiguous function or method reference";					
			} else {
				msg = "no match for " + fc;
				for(Type.Fun ft : targets) {
					if(fc.type.params().equals(ft.params())) {
						return Value.V_FUN(fc.name, ft);
					}
				}					
			}

			// failed to find an appropriate match
			boolean firstTime = true;
			int count = 0;
			for(Type.Fun ft : targets) {
				if(firstTime) {
					msg += "\n\tfound: " + fc.name.name() + parameterString(ft.params());
				} else {
					msg += "\n\tand: " + fc.name.name() + parameterString(ft.params());
				}
				if(++count < targets.size()) {
					msg += ",";
				}
			}
			syntaxError(msg + "\n",filename,elem);
		} catch(ResolveError ex) {
			syntaxError(ex.getMessage(),filename,elem);				
		}			
		return null;
	}
	
	protected Code infer(Code.Debug code, Entry stmt, Env environment) {
		Type rhs_t = environment.pop();		
		// FIXME: should be updated to string
		checkIsSubtype(Type.T_STRING,rhs_t,stmt);
		return code;
	}
	
	protected Code infer(Code.Destructure code, Entry stmt, Env environment) {
		Type type = environment.pop();	
		
		if(type instanceof Type.Tuple) {
			Type.Tuple tup = (Type.Tuple) type;
			for(Type t : tup.elements()) {
				environment.push(t);
			}
		} else if(Type.isSubtype(Type.T_REAL, type)){
			environment.push(Type.T_INT);
			environment.push(Type.T_INT);
		} else {
			syntaxError("invalid destructuring operation",filename,stmt);
		}

		return Code.Destructure(type);
	}
	
	protected Code infer(Code.Fail code, Entry stmt, Env environment) {
		// no change to stack
		return code;
	}
		
	protected Block infer(FieldLoad e, Entry stmt, Env environment) {	
		Block blk = new Block(0);
		Type lhs_t = environment.pop();		
		
		if (Type.isCoerciveSubtype(Type.T_PROCESS(Type.T_ANY), lhs_t)) {
			Type.Process tp = (Type.Process) lhs_t;
			blk.append(Code.ProcLoad(tp),stmt.attributes());
			lhs_t = tp.element();
		}
		
		Type.Record ett = Type.effectiveRecordType(lhs_t);		
		if (ett == null) {
			syntaxError("record required, got: " + lhs_t, filename, stmt);
		}
		Type ft = ett.fields().get(e.field);		
		if (ft == null) {
			syntaxError("record has no field named " + e.field, filename, stmt);
		}
		
		environment.push(ft);
		
		blk.append(Code.FieldLoad(ett, e.field),stmt.attributes());
		return blk;
	}
	
	protected Code infer(IndirectSend ivk, Entry stmt, Env environment) {
		ArrayList<Type> types = new ArrayList<Type>();			
		for(int i=0;i!=ivk.type.params().size();++i) {
			types.add(0,environment.pop());
		}
		Collections.reverse(types);
		Type receiver = environment.pop();
		Type target = environment.pop();		
		
		Type.Meth ft = checkType(target,Type.Meth.class,stmt);	
		
		List<Type> ft_params = ft.params();
		for(int i=0;i!=ft_params.size();++i) {
			Type param = ft_params.get(i);
			Type arg = types.get(i);
			checkIsSubtype(param,arg,stmt);
		}
		
		if(ft.ret() != Type.T_VOID && ivk.retval) {
			environment.push(ft.ret());
		}
		
		return Code.IndirectSend(ft,ivk.synchronous,ivk.retval);		
	}
	
	protected Code infer(Invoke ivk, Entry stmt, Env environment) {			
		ArrayList<Type> types = new ArrayList<Type>();			
		
		for(int i=0;i!=ivk.type.params().size();++i) {
			types.add(environment.pop());
		}
				
		Type.Process receiver = null;
		if(ivk.type instanceof Type.Meth) {			
			receiver = (Type.Process) environment.pop(); 							
		}
		
		Collections.reverse(types);		
		
		try {						
			Type.Fun funtype;
			if(ivk.type instanceof Type.Meth) {
				funtype = bindMethod(ivk.name,  receiver, types, stmt);
			} else {
				funtype = bindFunction(ivk.name,  types, stmt);				
			}			
			if(funtype.ret() != Type.T_VOID && ivk.retval) {
				environment.push(funtype.ret());
			}
			return Code.Invoke(funtype, ivk.name, ivk.retval);			
		} catch (ResolveError ex) {
			syntaxError(ex.getMessage(), filename, stmt);
			return null; // unreachable
		}		
	}
	
	protected Code infer(IndirectInvoke ivk, Entry stmt,
			Env environment) {
		
		ArrayList<Type> types = new ArrayList<Type>();			
		for(int i=0;i!=ivk.type.params().size();++i) {
			types.add(0,environment.pop());
		}
		Collections.reverse(types);
		Type target = environment.pop();
		Type.Fun ft = checkType(target,Type.Fun.class,stmt);			
		List<Type> ft_params = ft.params();
		for(int i=0;i!=ft_params.size();++i) {
			Type param = ft_params.get(i);
			Type arg = types.get(i);
			checkIsSubtype(param,arg,stmt);
		}
		
		if(ft.ret() != Type.T_VOID && ivk.retval) {
			environment.push(ft.ret());
		}
		
		return Code.IndirectInvoke(ft,ivk.retval);		
	}
	
	protected Code infer(Invert v, Entry stmt, Env environment) {
		Type rhs_t = environment.pop();

		// FIXME: add support for dictionaries
		checkIsSubtype(Type.T_BYTE,rhs_t,stmt);		
		environment.add(rhs_t);
		return Code.Invert(rhs_t);						
	}
	
	protected Code infer(ListLoad e, Entry stmt, Env environment) {
		Type idx = environment.pop();
		Type src = environment.pop();
		if(Type.isCoerciveSubtype(Type.T_DICTIONARY(Type.T_ANY, Type.T_ANY),src)) {			
			// this indicates a dictionary access, rather than a list access			
			Type.Dictionary dict = Type.effectiveDictionaryType(src);			
			if(dict == null) {
				syntaxError("expected dictionary",filename,stmt);
			}
			checkIsSubtype(dict.key(),idx,stmt);
			environment.push(dict.value());
			// OK, it's a hit
			return Code.DictLoad(dict);
		} else if(Type.isCoerciveSubtype(Type.T_STRING,src)) {
			checkIsSubtype(Type.T_INT,idx,stmt);
			environment.push(Type.T_CHAR);
			return Code.StringLoad();
		} else {		
			Type.List list = Type.effectiveListType(src);			
			if(list == null) {
				syntaxError("expected list",filename,stmt);
			}			
			checkIsSubtype(Type.T_INT,idx,stmt);
			environment.push(list.element());
			return Code.ListLoad(list);
		}
	}
		
	protected Code infer(Update e, Entry stmt, Env environment) {		
		ArrayList<Type> path = new ArrayList();
		Type val = environment.pop();
		for(int i=e.fields.size();i!=e.level;++i) {
			path.add(environment.pop());
		}
		
		Type src = environment.get(e.slot);		
		Type iter = src;
		
		if(e.slot == Code.THIS_SLOT && Type.isCoerciveSubtype(Type.T_PROCESS(Type.T_ANY), src)) {
			Type.Process p = (Type.Process) src;
			iter = p.element();
		}
		
		int fi = 0;
		int pi = 0;
		ArrayList<Type> indices = new ArrayList<Type>();
		for(int i=0;i!=e.level;++i) {				
			if(Type.isSubtype(Type.T_DICTIONARY(Type.T_ANY, Type.T_ANY),iter)) {			
				// this indicates a dictionary access, rather than a list access			
				Type.Dictionary dict = Type.effectiveDictionaryType(iter);			
				if(dict == null) {
					syntaxError("expected dictionary",filename,stmt);
				}
				indices.add(path.get(pi++));
				// We don't  
				// checkIsSubtype(dict.key(),idx,stmt);
				iter = dict.value();				
			} else if(Type.isSubtype(Type.T_STRING,iter)) {							
				Type idx = path.get(pi++);
				checkIsSubtype(Type.T_INT,idx,stmt);
				checkIsSubtype(Type.T_CHAR,val,stmt);	
				iter = Type.T_CHAR;				
			} else if(Type.isSubtype(Type.T_LIST(Type.T_ANY),iter)) {			
				Type.List list = Type.effectiveListType(iter);			
				if(list == null) {
					syntaxError("expected list",filename,stmt);
				}
				Type idx = path.get(pi++);
				checkIsSubtype(Type.T_INT,idx,stmt);				
				iter = list.element();
			} else {
				Type.Record rec = Type.effectiveRecordType(iter);
				if(rec == null) {
					syntaxError("expected record, found " + iter,filename,stmt);
				}
				String field = e.fields.get(fi++);
				iter = rec.fields().get(field);
				if(iter == null) {
					syntaxError("expected field \"" + field + "\"",filename,stmt);
				}				
			}			
		}
		
		// Now, we need to determine the (potentially) updated type of the
		// variable in question. For example, if we assign a real into a [int]
		// then we'll end up with a [real].
		Type ntype = typeInference(src,val,e.level,0,e.fields,0,indices);
		environment.set(e.slot,ntype);
		
		return Code.Update(src,e.slot,e.level,e.fields);
	}

	/**
	 * The purpose of this method is to update a given type after some
	 * subcomponent has been assigned a new value.  Examples include:
	 * <pre>
	 * x : [int] => [real]           // after e.g. x[i] = 1.2
	 * x : { real op } => { int op}  // after e.g. x.op = 1
	 * </pre>
	 * 
	 * @param oldtype
	 * @param level
	 * @param fieldLevel
	 * @param fields
	 * @return
	 */
	public static Type typeInference(Type oldtype, Type newtype, int level,
			int fieldLevel, ArrayList<String> fields, int indexLevel,
			ArrayList<Type> indices) {
		if(level == 0 && fieldLevel == fields.size()) {
			// this is the base case of the recursion.
			return newtype;			
		} else if(Type.isSubtype(Type.T_PROCESS(Type.T_ANY),oldtype)) {
			Type.Process tp = (Type.Process) oldtype;
			Type nelement = typeInference(tp.element(),newtype,level,fieldLevel,fields,indexLevel,indices);
			return Type.T_PROCESS(nelement);
		} else if(Type.isSubtype(Type.T_DICTIONARY(Type.T_ANY, Type.T_ANY),oldtype)) {
			// Dictionary case is straightforward. Since only one key-value pair
			// is being updated, we must assume other key-value pairs are not
			// --- hence, the original type must be preserved. However, in the
			// case that we're assigning a more general value for some key then
			// we need to generalise the value type accordingly. 
			Type.Dictionary dict = Type.effectiveDictionaryType(oldtype);
			Type nkey = indices.get(indexLevel);			
			Type nvalue = typeInference(dict.value(), newtype, level - 1,
					fieldLevel, fields, indexLevel + 1, indices);
			return Type.T_DICTIONARY(Type.leastUpperBound(dict.key(), nkey),
					Type.leastUpperBound(dict.value(), nvalue));			
		} else if(Type.isSubtype(Type.T_STRING,oldtype)) {
			Type nelement = typeInference(Type.T_CHAR, newtype, level - 1,
					fieldLevel, fields, indexLevel, indices);			
			return oldtype;
		} else if(Type.isSubtype(Type.T_LIST(Type.T_ANY),oldtype)) {		
			// List case is basicaly same as for dictionary above.
			Type.List list = Type.effectiveListType(oldtype);
			Type nelement = typeInference(list.element(), newtype, level - 1,
					fieldLevel, fields, indexLevel, indices);
			return Type.leastUpperBound(oldtype,Type.T_LIST(nelement));
		
		} else if(Type.effectiveRecordType(oldtype) != null){			
			// Record case is more interesting as we may be able to actually
			// perform a "strong" update of the type. This is because we know
			// exactly which field is being updated.
			String field = fields.get(fieldLevel);
			if(oldtype instanceof Type.Record) {
				Type.Record rt = (Type.Record) oldtype;
				Type ntype = typeInference(rt.fields().get(field), newtype,
						level - 1, fieldLevel + 1, fields, indexLevel, indices);
				HashMap<String,Type> types = new HashMap<String,Type>(rt.fields());				
				types.put(field, ntype);
				return Type.T_RECORD(types);
			} else {
				Type.Union tu = (Type.Union) oldtype;
				Type t = Type.T_VOID;
				for(Type b : tu.bounds()) {					
					t = Type.leastUpperBound(
							t,
							typeInference(b, newtype, level, fieldLevel,
									fields, indexLevel, indices));
				}
				return t;
			} 			
		} else {
			throw new IllegalArgumentException("invalid type passed to type inference: " + oldtype);
		}
	}

	protected Code infer(Load e, Entry stmt, Env environment) {
		e = Code.Load(environment.get(e.slot), e.slot);		
		environment.push(e.type);
		return e;
	}	
	
	protected Code infer(NewRecord e, Entry stmt, Env environment) {
		HashMap<String,Type> fields = new HashMap<String,Type>();
		ArrayList<String> keys = new ArrayList<String>(e.type.keys());
		Collections.sort(keys);		
		for(int i=keys.size()-1;i>=0;--i) {
			fields.put(keys.get(i),environment.pop());
		}		
		Type.Record type = Type.T_RECORD(fields);
		environment.push(type);
		return Code.NewRecord(type);
	}
	
	protected Code infer(NewDict e, Entry stmt, Env environment) {
		Type key = Type.T_VOID;
		Type value = Type.T_VOID;
		
		for(int i=0;i!=e.nargs;++i) {
			value = Type.leastUpperBound(value,environment.pop());
			key = Type.leastUpperBound(key,environment.pop());
			
		}
		
		Type.Dictionary type = Type.T_DICTIONARY(key,value);
		environment.push(type);
		return Code.NewDict(type,e.nargs);
	}
	
	protected Code infer(NewList e, Entry stmt, Env environment) {
		Type elem = Type.T_VOID;		
		
		for(int i=0;i!=e.nargs;++i) {
			elem = Type.leastUpperBound(elem,environment.pop());						
		}
		
		Type.List type = Type.T_LIST(elem);
		environment.push(type);
		return Code.NewList(type,e.nargs);
	}
	
	
	protected Code infer(NewSet e, Entry stmt, Env environment) {
		Type elem = Type.T_VOID;		
		
		for(int i=0;i!=e.nargs;++i) {
			elem = Type.leastUpperBound(elem,environment.pop());						
		}
		
		Type.Set type = Type.T_SET(elem);
		environment.push(type);
		return Code.NewSet(type,e.nargs);
	}
	
	protected Code infer(NewTuple e, Entry stmt, Env environment) {
		ArrayList<Type> types = new ArrayList<Type>();		
		
		for(int i=0;i!=e.nargs;++i) {
			types.add(environment.pop());						
		}
		Collections.reverse(types);
		Type.Tuple type = Type.T_TUPLE(types);
		environment.push(type);
		return Code.NewTuple(type,e.nargs);
	}
	
	protected Code infer(Send ivk, Entry stmt, Env environment) {
		ArrayList<Type> types = new ArrayList<Type>();	
		
		for(int i=0;i!=ivk.type.params().size();++i) {
			types.add(environment.pop());
		}
		Collections.reverse(types);
		
		Type _rec = environment.pop();
		
		// FIXME: problem here as process check doesn't cover all cases.		
		checkIsSubtype(Type.T_PROCESS(Type.T_ANY),_rec,stmt);
		// FIXME: bug here as we need an effectiveProcessType
		Type.Process rec = (Type.Process) _rec; 		

		try {
			Type.Meth funtype = bindMethod(ivk.name, rec, types, stmt);
			if (funtype.ret() != Type.T_VOID && ivk.synchronous && ivk.retval) {
				environment.push(funtype.ret());
			}
			return Code.Send(funtype, ivk.name, ivk.synchronous, ivk.retval);			
		} catch (ResolveError ex) {
			syntaxError(ex.getMessage(), filename, stmt);
			return null; // unreachable
		}		
	}
	
	protected Code infer(Store e, Entry stmt, Env environment) {		
		e = Code.Store(environment.pop(), e.slot);		
		environment.set(e.slot, e.type);
		return e;
	}
	
	protected Code infer(Code.ListLength code, Entry stmt, Env environment) {
		Type src = environment.pop();
		if(Type.isCoerciveSubtype(Type.T_STRING,src)) {
			environment.add(Type.T_INT);
			return Code.StringLength();
		} else if(Type.isCoerciveSubtype(Type.T_LIST(Type.T_ANY),src)) {
			environment.add(Type.T_INT);
			return Code.ListLength(Type.effectiveListType(src));
		} else if(Type.isCoerciveSubtype(Type.T_SET(Type.T_ANY),src)) {
			environment.add(Type.T_INT);
			return Code.SetLength(Type.effectiveSetType(src));
		} else {
			syntaxError("expected list or set, found " + src,filename,stmt);
			return null;
		}
	}
	
	protected Code infer(Code.SubList code, Entry stmt, Env environment) {
		Type end = environment.pop();
		Type start = environment.pop();
		Type src = environment.pop();
		
		checkIsSubtype(Type.T_INT,start,stmt);
		checkIsSubtype(Type.T_INT,end,stmt);
		Code r;
		
		if(Type.isCoerciveSubtype(Type.T_STRING, src)) {
			r = Code.SubString();
		} else {
			checkIsSubtype(Type.T_LIST(Type.T_ANY),src,stmt);
			r = Code.SubList(Type.effectiveListType(src));
		}
		
		environment.push(src);
				
		return r;
	}			
	
	protected Code infer(Code.Return code, Entry stmt, Env environment) {		
		Type ret_t = method.type().ret();		
		
		if(environment.size() > methodCase.body().numSlots()) {			
			if(ret_t == Type.T_VOID) {
				syntaxError(
						"cannot return value from method with void return type",
						filename, stmt);
			}
			
			Type rhs_t = environment.pop();
			
			checkIsSubtype(ret_t,rhs_t,stmt);
		} else if(ret_t != Type.T_VOID) {
			syntaxError(
					"missing return value",filename, stmt);
		}
		
		return Code.Return(ret_t);
	}
	
	protected Code infer(Code.SetUnion code, Entry stmt, Env environment) {
		return inferSetUnion(code.dir,stmt,environment);
	}
	
	protected Code inferSetUnion(OpDir dir, Entry stmt, Env environment) {
		Type rhs = environment.pop();
		Type lhs = environment.pop();
		Type result;
		
		boolean lhs_set = Type.isSubtype(Type.T_SET(Type.T_ANY),lhs);
		boolean rhs_set = Type.isSubtype(Type.T_SET(Type.T_ANY),rhs);			
		
		if(dir == OpDir.UNIFORM && (lhs_set || rhs_set)) {					
			if(lhs_set && rhs_set) {
				result = Type.leastUpperBound(lhs,rhs);		
			} else if(lhs_set && Type.isCoerciveSubtype(lhs, rhs)) {
				result = lhs;
			} else  if(rhs_set && Type.isCoerciveSubtype(rhs, lhs)) {
				result = rhs;				
			} else {
				syntaxError("invalid set operation on types",filename,stmt);
				result = null;
			}						
		} else if(dir == OpDir.LEFT && lhs_set) {
			result = Type.leastUpperBound(lhs,Type.T_SET(rhs));
		} else if(dir == OpDir.RIGHT && rhs_set) {
			result = Type.leastUpperBound(Type.T_SET(lhs),rhs);			
		} else {
			syntaxError("expecting set type",filename,stmt);
			return null; // dead-code
		}
				
		environment.push(result);
		return Code.SetUnion(Type.effectiveSetType(result), dir);	
	}

	protected Code infer(Code.SetIntersect code, Entry stmt, Env environment) {
		Type rhs = environment.pop();
		Type lhs = environment.pop();
		Type result;
		
		boolean lhs_set = Type.isSubtype(Type.T_SET(Type.T_ANY),lhs);
		boolean rhs_set = Type.isSubtype(Type.T_SET(Type.T_ANY),rhs);			
		
		if(lhs_set || rhs_set) {				
			if(lhs_set && rhs_set) {
				result = Type.greatestLowerBound(lhs,rhs);		
			} else if(lhs_set && Type.isCoerciveSubtype(lhs, rhs)) {
				result = lhs;
			} else  if(rhs_set && Type.isCoerciveSubtype(rhs, lhs)) {
				result = rhs;				
			} else {
				syntaxError("invalid set operation on types",filename,stmt);
				result = null;
			}						
		} else {
			syntaxError("expecting set type",filename,stmt);
			return null; // dead-code
		}
				
		environment.push(result);
		return Code.SetIntersect(Type.effectiveSetType(result), OpDir.UNIFORM);	
	}
	
	protected Code infer(Code.SetDifference code, Entry stmt, Env environment) {
		return inferSetDifference(code.dir,stmt,environment);
	}
		
	protected Code inferSetDifference(OpDir dir, Entry stmt, Env environment) {
		Type rhs = environment.pop();
		Type lhs = environment.pop();
		Type result;
		
		boolean lhs_set = Type.isSubtype(Type.T_SET(Type.T_ANY),lhs);
					
		
		if(lhs_set) {	
			checkIsSubtype(lhs,rhs,stmt);
			result = lhs;			
		} else {
			syntaxError("expecting set type",filename,stmt);
			return null; // dead-code
		}
				
		environment.push(result);
		return Code.SetDifference(Type.effectiveSetType(result), dir);	
	}

	protected Code infer(Negate v, Entry stmt, Env environment) {
		Type rhs_t = environment.pop();

		checkIsSubtype(Type.T_REAL,rhs_t,stmt);
		if(rhs_t != Type.T_INT) {
			// this is an implicit coercion
			rhs_t = Type.T_REAL;
		}
		environment.add(rhs_t);
		return Code.Negate(rhs_t);						
	}
	
	protected Code infer(ProcLoad v, Entry stmt, Env environment) {
		Type rhs_t = environment.pop();
		checkIsSubtype(Type.T_PROCESS(Type.T_ANY),rhs_t,stmt);
		Type.Process tp = (Type.Process)rhs_t; 
		environment.push(tp.element());
		return Code.ProcLoad(tp);
	}
	
	protected Code infer(Spawn v, Entry stmt, Env environment) {
		Type rhs_t = environment.pop();
		environment.add(Type.T_PROCESS(rhs_t));
		return Code.Spawn(Type.T_PROCESS(rhs_t));						
	}			
	
	protected Code infer(Code.Throw code, Entry stmt, Env environment) {
		Type val = environment.pop();
		// TODO: check throws clause
		// Type ret_t = method.type().throws						
		// checkIsSubtype(ret_t,val,stmt);
		return Code.Throw(val);
	}
	
	protected Pair<Env, Env> propagate(int index, Code.IfGoto code, Entry stmt,
			Env environment) {
		environment = (Env) environment.clone();
		
		Type rhs_t = environment.pop();
		Type lhs_t = environment.pop();
		Type lub;
		
		if(code.op == Code.COp.ELEMOF) {
			// this is the only asymmetric binary operation. So, it's handled
			// with a special case.
			Type element;
			if(rhs_t instanceof Type.List){
				element = ((Type.List)rhs_t).element();
			} else if(rhs_t instanceof Type.Set){
				element = ((Type.Set)rhs_t).element();
			} else {
				syntaxError("expected set or list, found: " + rhs_t,filename,stmt);
				return null;
			}
			if (!Type.isCoerciveSubtype(element, lhs_t)) {
				syntaxError("incomparable types: " + lhs_t + " and " + rhs_t,
						filename, stmt);
			}			
			lub = rhs_t;
		} else if(Type.isCoerciveSubtype(lhs_t, rhs_t)) {
			lub = lhs_t;
		} else if(Type.isCoerciveSubtype(rhs_t, lhs_t)) {
			lub = rhs_t;
		} else {
			syntaxError("incomparable types: " + lhs_t + " and " + rhs_t,
					filename, stmt);
			lub = null; // unreachable
		}
		
		switch(code.op) {
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
			checkIsSubtype(Type.T_REAL, lub, stmt);
			break;
		case EQ:
		case NEQ:
			// no specific requirements here			
			break;
		case ELEMOF:
		{			
			break; // handled with special case
		}	
		case SUBSET:
		case SUBSETEQ:						
			checkIsSubtype(Type.T_SET(Type.T_ANY),lub,stmt);			
			break;		
		}
		
		Block blk = new Block(0);
		blk.append(Code.IfGoto(lub, code.op, code.target),stmt.attributes());		
		rewrites.put(index, blk);
		
		return new Pair<Env,Env>(environment,environment);
	}
	
	protected Pair<Env, Env> propagate(int index, Code.IfType code, Entry stmt,
			Env environment) {
		environment = (Env) environment.clone();
		Type lhs_t;
		
		if(code.slot >= 0) {
			lhs_t = environment.get(code.slot);
		} else {
			lhs_t = environment.pop();
		}
		
		Code ncode = code;
		Env trueEnv = null;
		Env falseEnv = null;								
		Type glb = Type.greatestLowerBound(lhs_t, code.test);
		
		if(Type.isSubtype(code.test,lhs_t)) {								
			// DEFINITE TRUE CASE										
			//trueEnv = environment;
			//ncode = Code.Goto(code.target);										
			syntaxError("branch always taken",filename,methodCase.body().get(index));
		} else if (glb == Type.T_VOID) {				
			// DEFINITE FALSE CASE				
			//falseEnv = environment;							
			//ncode = Code.Skip;							
			syntaxError("incomparable operands: " + lhs_t + " and " + code.test,filename,stmt);
		} else {
			ncode = Code.IfType(lhs_t, code.slot, code.test, code.target);				
			trueEnv = new Env(environment);
			falseEnv = new Env(environment);		
			if(code.slot >= 0) {						
				Type gdiff = Type.leastDifference(lhs_t, code.test);							
				trueEnv.set(code.slot, glb);			
				falseEnv.set(code.slot, gdiff);								
			}
		}
		
		Block blk = new Block(0);
		blk.append(ncode,stmt.attributes());		
		rewrites.put(index, blk);
		
		return new Pair(trueEnv,falseEnv);		
	}		

	protected List<Env> propagate(int index, Code.Switch code, Entry stmt,
			Env environment) {
		Type val = environment.pop();
		ArrayList<Env> envs = new ArrayList<Env>();
		// TODO: update this code to support type inference of types. That is,
		// if we switch on a type value then this will update the type of the
		// value.
		for(Pair<Value, String> e : code.branches) {
			Value cv = e.first();
			checkIsSubtype(val,cv.type(),stmt);
			envs.add(environment);
		}

		Block blk = new Block(0);
		blk.append(Code.Switch(val,code.defaultTarget,code.branches),stmt.attributes());		
		rewrites.put(index, blk);
		
		return envs;
	}	
	
	protected Env propagate(int start, int end, Code.ForAll forloop,
			Entry stmt, ArrayList<Integer> modifies, Env environment) {
						
		// Now, type the source 		
		Type src_t = environment.pop();						
		
		Type elem_t;
		if(src_t instanceof Type.List) {
			elem_t = ((Type.List)src_t).element();
		} else if(src_t instanceof Type.Set){
			elem_t = ((Type.Set)src_t).element();
		} else if(src_t == Type.T_STRING){
			elem_t = Type.T_CHAR;
		} else if(src_t instanceof Type.Dictionary){
			Type.Dictionary d = (Type.Dictionary) src_t;			
			elem_t = Type.T_TUPLE(d.key(),d.value());
		} else {
			syntaxError("expected set or list, found: " + src_t,filename,stmt);
			return null; // deadcode
		}
		
		if (elem_t == Type.T_VOID) {
			// This indicates a loop over an empty list. This legitimately can
			// happen as a result of substitution for contraints or pre/post
			// conditions.
			for (int i = start; i <= end; ++i) {
				rewrites.put(i, new Block(0));
			}			
			
			return environment;
		}				
		
		// create environment specific for loop body
		Env loopEnv = new Env(environment);		
		loopEnv.set(forloop.slot, elem_t);			
		
		Env newEnv = null;
		Env oldEnv = loopEnv;
		do {					
			// iterate until a fixed point reached
			oldEnv = newEnv;			 			
			newEnv = join(loopEnv,propagate(start+1,end,oldEnv));
		 } while(!newEnv.equals(oldEnv));				
		
		// following line is necessary to get rid of the loop variable
		environment = join(environment,newEnv);		
				
		Block blk = new Block(0);
		blk.append(Code.ForAll(src_t, forloop.slot, forloop.target, modifies),stmt.attributes());		
		rewrites.put(start, blk);
		
		return join(environment,newEnv);
	}
	
	protected Env propagate(int start, int end, Code.Loop loop,
			Entry stmt, Env environment) {

		// First, calculate the modifies set
		ArrayList<Integer> modifies = new ArrayList<Integer>();
		for(int i=start;i<end;++i) {
			Code code = block.get(i).code;
			if(code instanceof Store) {
				Store s = (Store) code;
				modifies.add(s.slot);
			} else if(code instanceof Update) {
				Update s = (Update) code;
				modifies.add(s.slot);
			}
		}
		
		// Now, type the loop body
		
		if (loop instanceof Code.ForAll) {
			return propagate(start, end, (Code.ForAll) loop, stmt, modifies, environment);
		}
		
		Env newEnv = environment;
		Env oldEnv = null;
		do {
			// iterate until a fixed point reached
			oldEnv = newEnv;
			newEnv = join(environment,propagate(start+1,end, oldEnv));
		} while (!newEnv.equals(oldEnv));		
				
		environment = join(environment,newEnv);		
				
		Block blk = new Block(0);
		blk.append(Code.Loop(loop.target, modifies),stmt.attributes());		
		rewrites.put(start, blk);
		
		return environment;
	}
	
	/**
	 * Bind function is responsible for determining the true type of a method or
	 * function being invoked. To do this, it must find the function/method
	 * with the most precise type that matches the argument types.
	 * 
	 * @param nid
	 * @param receiver
	 * @param paramTypes
	 * @param elem
	 * @return
	 * @throws ResolveError
	 */
	protected Type.Fun bindFunction(NameID nid, 
			List<Type> paramTypes, SyntacticElement elem) throws ResolveError {

		Type.Fun target = Type.T_FUN(Type.T_ANY,paramTypes);
		Type.Fun candidate = null;				
		
		List<Type.Fun> targets = lookupMethod(nid.module(),nid.name()); 
		
		for (Type.Fun ft : targets) {										
			if(ft instanceof Type.Meth) {
				// in this case, we want to check if this is a definite method
				// call with a receiver. If so, then we don't consider it. We do
				// consider "headless" method calls, since these cannot be
				// distinguished from function calls in module builder.  
				Type.Meth mt = (Type.Meth) ft;
				if(mt.receiver() != null) {
					continue;
				}
			}
			if (ft.params().size() == paramTypes.size()						
					&& paramSubtypes(ft, target)
					&& (candidate == null || paramSubtypes(candidate,ft))) {					
				candidate = ft;								
			}
		}				
		
		// Check whether we actually found something. If not, print a useful
		// error message.
		if(candidate == null) {
			String msg = "no match for " + nid.name() + parameterString(paramTypes);
			boolean firstTime = true;
			int count = 0;
			for(Type.Fun ft : targets) {
				if(firstTime) {
					msg += "\n\tfound: " + nid.name() +  parameterString(ft.params());
				} else {
					msg += "\n\tand: " + nid.name() +  parameterString(ft.params());
				}				
				if(++count < targets.size()) {
					msg += ",";
				}
			}
			
			syntaxError(msg + "\n",filename,elem);
		}
		
		return candidate;
	}
	
	protected Type.Meth bindMethod(NameID nid, Type.Process receiver,
			List<Type> paramTypes, SyntacticElement elem) throws ResolveError {

		Type.Meth target = Type.T_METH(receiver, Type.T_ANY,paramTypes);
		Type.Meth candidate = null;				
		
		List<Type.Fun> targets = lookupMethod(nid.module(),nid.name()); 
		
		for (Type.Fun ft : targets) {
			if(ft instanceof Type.Meth) {
				Type.Meth mt = (Type.Meth) ft; 
				Type funrec = mt.receiver();			
				if (receiver == funrec
						|| (receiver != null && funrec != null && Type
								.isCoerciveSubtype(receiver, funrec))) {
					// receivers match up OK ...				
					if (ft.params().size() == paramTypes.size()						
							&& paramSubtypes(ft, target)
							&& (candidate == null || paramSubtypes(candidate,ft))) {					
						candidate = mt;					
					}
				}
			}
		}				
		
		// Check whether we actually found something. If not, print a useful
		// error message.
		if(candidate == null) {
			String rec = "::";
			if(receiver != null) {				
				rec = receiver.toString() + "::";
			}
			String msg = "no match for " + rec + nid.name() + parameterString(paramTypes);
			boolean firstTime = true;
			int count = 0;
			for(Type.Fun ft : targets) {
				rec = "";
				if(ft instanceof Type.Meth) {
					Type.Meth mt = (Type.Meth) ft;
					if(mt.receiver() != null) {
						rec = mt.receiver().toString();
					}
					rec = rec + "::";
				}
				if(firstTime) {
					msg += "\n\tfound: " + rec + nid.name() +  parameterString(ft.params());
				} else {
					msg += "\n\tand: " + rec + nid.name() +  parameterString(ft.params());
				}				
				if(++count < targets.size()) {
					msg += ",";
				}
			}
			
			syntaxError(msg + "\n",filename,elem);
		}
		
		return candidate;
	}
	
	private boolean paramSubtypes(Type.Fun f1, Type.Fun f2) {
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if(f1_params.size() == f2_params.size()) {
			for(int i=0;i!=f1_params.size();++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);
				if(!Type.isCoerciveSubtype(f1_param,f2_param)) {					
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	private String parameterString(List<Type> paramTypes) {
		String paramStr = "(";
		boolean firstTime = true;
		for(Type t : paramTypes) {
			if(!firstTime) {
				paramStr += ",";
			}
			firstTime=false;
			paramStr += t;
		}
		return paramStr + ")";
	}
	
	protected List<Type.Fun> lookupMethod(ModuleID mid, String name)
			throws ResolveError {
		
		Module module = loader.loadModule(mid);
		ArrayList<Type.Fun> rs = new ArrayList<Type.Fun>();
		for (Module.Method m : module.method(name)) {
			rs.add(m.type());
		}
		return rs;		
	}
	
	protected <T extends Type> T checkType(Type t, Class<T> clazz,
			SyntacticElement elem) {		
		if (clazz.isInstance(t)) {
			return (T) t;
		} else {
			syntaxError("expected type " + clazz.getName() + ", found "
					+ t, filename, elem);
			return null;
		}
	}
	
	// Check t1 :> t2
	protected void checkIsSubtype(Type t1, Type t2, SyntacticElement elem) {		
		if (!Type.isCoerciveSubtype(t1, t2)) {
			syntaxError("expected type " + t1 + ", found " + t2, filename, elem);
		}		
	}

	public Env join(Env env1, Env env2) {
		if (env2 == null) {
			return env1;
		} else if (env1 == null) {
			return env2;
		}
		Env env = new Env();
		for (int i = 0; i != Math.min(env1.size(), env2.size()); ++i) {
			env.add(Type.leastUpperBound(env1.get(i), env2.get(i)));
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
