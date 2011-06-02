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

	public TypePropagation(ModuleLoader loader) {
		super(loader);
	}
	
	public Module.TypeDef propagate(Module.TypeDef type) {
		return type;		
	}
	
	public Module.ConstDef propagate(Module.ConstDef def) {		
		return def;
	}
	
	public Env initialStore() {
				
		Env environment = new Env();		

		if(method.type().receiver() != null) {					
			environment.add(method.type().receiver());
		}
		List<Type> paramTypes = method.type().params();
		
		int i = 0;
		for (; i != paramTypes.size(); ++i) {
			Type t = paramTypes.get(i);
			environment.add(t);
			if (method.type().receiver() == null
					&& Type.isSubtype(Type.T_PROCESS(Type.T_ANY), t)) {
				// FIXME: add source information
				syntaxError("function argument cannot have process type",
						filename, methodCase);
			}
		}				
		
		return environment;
	}
	
	public Module.Case propagate(Module.Case mcase) {		
		this.methodCase = mcase;
		this.stores = new HashMap<String,Env>();
		
		Env environment = initialStore();
		int start = method.type().params().size();
		if(method.type().receiver() != null) { start++; }
		for (int i = start; i < mcase.maxLocals(); i++) {
			environment.add(Type.T_VOID);
		}	
		
		Block body = propagate(mcase.body(), environment).first();	
		
		return new Module.Case(body,mcase.maxLocals(),mcase.attributes());
	}
	
	protected Pair<Entry, Env> propagate(Entry entry,
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
		} else if(code instanceof ExternJvm) {
			// skip
		} else if(code instanceof Fail) {
			code = infer((Fail)code,entry,environment);
		} else if(code instanceof FieldLoad) {
			code = infer((FieldLoad)code,entry,environment);
		} else if(code instanceof IndirectInvoke) {
			code = infer((IndirectInvoke)code,entry,environment);
		} else if(code instanceof IndirectSend) {
			code = infer((IndirectSend)code,entry,environment);
		} else if(code instanceof Invoke) {
			code = infer((Invoke)code,entry,environment);
		} else if(code instanceof Label) {
			// skip
		} else if(code instanceof ListLoad) {
			code = infer((ListLoad)code,entry,environment);
		} else if(code instanceof Load) {
			code = infer((Load)code,entry,environment);
		} else if(code instanceof MultiStore) {
			code = infer((MultiStore)code,entry,environment);
		} else if(code instanceof NewDict) {
			code = infer((NewDict)code,entry,environment);
		} else if(code instanceof NewList) {
			code = infer((NewList)code,entry,environment);
		} else if(code instanceof NewRecord) {
			code = infer((NewRecord)code,entry,environment);
		} else if(code instanceof NewSet) {
			code = infer((NewSet)code,entry,environment);
		} else if(code instanceof Return) {
			code = infer((Return)code,entry,environment);
		} else if(code instanceof Store) {
			code = infer((Store)code,entry,environment);
		} else if(code instanceof UnOp) {
			code = infer((UnOp)code,entry,environment);
		} else {
			syntaxError("Need to finish type inference",filename,entry);
			return null;
		}
		
		return new Pair<Entry, Env>(new Block.Entry(code, entry.attributes()),
				environment);
	}
	
	protected Code infer(Code.Assert code, Entry stmt, Env environment) {
		return null;
	}
	
	protected Code infer(BinOp v, Entry stmt, Env environment) {
		Type lhs = environment.pop();
		Type rhs = environment.pop();
		Type lub = Type.leastUpperBound(lhs,rhs);
		Code.BOp bop = v.bop;

		if(Type.isSubtype(Type.T_LIST(Type.T_ANY),lub)) {
			switch(v.bop) {
				case APPEND:
				case ADD:															
					bop = Code.BOp.APPEND;
					break;
				default:
					syntaxError("Invalid operation on lists",filename,stmt);		
			}	
		} else if(Type.isSubtype(Type.T_SET(Type.T_ANY),lub)) {
			switch(v.bop) {
				case ADD:											
				case UNION:
					bop = Code.BOp.UNION;
					break;
				case DIFFERENCE:
				case SUB:			
					bop = Code.BOp.DIFFERENCE;
					break;
				case INTERSECT:
					break;					
				default:
					syntaxError("Invalid operation on sets: " + bop,filename,stmt);			
			}
		} else {		
			// FIXME: more cases, including elem of		
			checkIsSubtype(Type.T_REAL,lub,stmt);
		}
		return Code.BinOp(lub,bop);				
	}
	
	protected Code infer(Code.Convert code, Entry stmt, Env environment) {
		Type from = environment.pop();
		checkIsSubtype(code.to,from,stmt);
		environment.push(code.to);
		return Code.Convert(from, code.to);
	}
	
	protected Code infer(Code.Const code, Entry stmt, Env environment) {
		environment.push(code.constant.type());
		return code;
	}
	
	protected Code infer(Code.Debug code, Entry stmt, Env environment) {
		Type rhs_t = environment.pop();		
		// FIXME: should be updated to string
		checkIsSubtype(Type.T_LIST(Type.T_INT),rhs_t,stmt);
		return code;
	}
	
	protected Code infer(Code.Fail code, Entry stmt, Env environment) {
		// no change to stack
		return code;
	}
		
	protected Code infer(FieldLoad e, Entry stmt, Env environment) {	
		Type lhs_t = environment.pop();
		
		// TODO:  what about process accesses here?
		
		Type.Record ett = Type.effectiveRecordType(lhs_t);		
		if (ett == null) {
			syntaxError("record required, got: " + lhs_t, filename, stmt);
		}
		Type ft = ett.fields().get(e.field);		
		if (ft == null) {
			syntaxError("record has no field named " + e.field, filename, stmt);
		}
		
		environment.push(ft);
		
		return Code.FieldLoad(ett, e.field); 
	}
	
	protected Code infer(IndirectSend e, Entry stmt, Env environment) {
		return null;
	}
	
	protected Code infer(Invoke ivk, Entry stmt, Env environment) {			
		ArrayList<Type> types = new ArrayList<Type>();	
			
		for(int i=0;i!=ivk.type.params().size();++i) {
			types.add(environment.pop());
		}
		
		try {
			Type.Fun funtype = bindFunction(ivk.name, null, types, stmt);
			if(funtype.ret() != Type.T_VOID) {
				environment.push(funtype.ret());
			}
			return Code.Invoke(funtype, ivk.name);			
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
		Type target = environment.pop();
		Type.Fun ft = checkType(target,Type.Fun.class,stmt);			
		List<Type> ft_params = ft.params();
		for(int i=0;i!=ft_params.size();++i) {
			Type param = ft_params.get(i);
			Type arg = types.get(i);
			checkIsSubtype(param,arg,stmt);
		}
		
		if(ft.ret() != Type.T_VOID) {
			environment.push(ft.ret());
		}
		
		return Code.IndirectInvoke(ft);		
	}
	
	protected Code infer(ListLoad e, Entry stmt, Env environment) {
		Type idx = environment.pop();
		Type src = environment.pop();
		if(Type.isSubtype(Type.T_DICTIONARY(Type.T_ANY, Type.T_ANY),src)) {			
			// this indicates a dictionary access, rather than a list access			
			Type.Dictionary dict = Type.effectiveDictionaryType(src);			
			if(dict == null) {
				syntaxError("expected dictionary",filename,stmt);
			}
			checkIsSubtype(dict.key(),idx,stmt);
			environment.push(dict.value());
			// OK, it's a hit
			return Code.DictLoad(dict);
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
		
	protected Code infer(MultiStore e, Entry stmt, Env environment) {		
		ArrayList<Type> path = new ArrayList();
		Type val = environment.pop();
		for(int i=e.fields.size();i!=e.level;++i) {
			path.add(environment.pop());
		}
		Type src = environment.get(e.slot);
		Type iter = src;
		
		int fi = 0;
		int pi = 0;
		for(int i=0;i!=e.level;++i) {				
			if(Type.isSubtype(Type.T_DICTIONARY(Type.T_ANY, Type.T_ANY),iter)) {			
				// this indicates a dictionary access, rather than a list access			
				Type.Dictionary dict = Type.effectiveDictionaryType(iter);			
				if(dict == null) {
					syntaxError("expected dictionary",filename,stmt);
				}
				Type idx = path.get(pi++);
				checkIsSubtype(dict.key(),idx,stmt);
				iter = dict.value();				
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
					syntaxError("expected record",filename,stmt);
				}
				String field = e.fields.get(fi++);
				iter = rec.fields().get(field);
				if(iter == null) {
					syntaxError("expected field \"" + field + "\"",filename,stmt);
				}				
			}
		}
		
		// FIXME: broken as should just update src variable.
		checkIsSubtype(iter,val,stmt);
		
		return Code.MultiStore(src,e.slot,e.level,e.fields);
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
		return Code.NewRec(type);
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
	
	protected Code infer(Store e, Entry stmt, Env environment) {
		e = Code.Store(environment.pop(), e.slot);		
		environment.set(e.slot, e.type);
		return e;
	}
	
	protected Code infer(Code.Return code, Entry stmt, Env environment) {		
		Type ret_t = method.type().ret();		
		
		if(environment.size() > methodCase.maxLocals()) {			
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
	

	protected Code infer(UnOp v, Entry stmt, Env environment) {
		Type rhs_t = environment.pop();

		switch(v.uop) {
			case NEG:
				checkIsSubtype(Type.T_REAL,rhs_t,stmt);
				environment.add(rhs_t);
				return Code.UnOp(rhs_t,v.uop);
			case LENGTHOF:
				if(rhs_t instanceof Type.List || rhs_t instanceof Type.Set) {
					environment.add(Type.T_INT);
					return Code.UnOp(rhs_t,v.uop);
				} else {
					syntaxError("expected list or set, found " + rhs_t,filename,stmt);
				}				
			case PROCESSACCESS:
				checkIsSubtype(Type.T_PROCESS(Type.T_ANY),rhs_t,stmt);
				environment.add(Type.greatestLowerBound(Type.T_ANY,rhs_t));
				return Code.UnOp(rhs_t,v.uop);
			case PROCESSSPAWN:
				environment.add(Type.T_PROCESS(rhs_t));
				return Code.UnOp(rhs_t,v.uop);				
		}
		
		syntaxError("unknown unary operation: " + v.uop,filename,stmt);
		return null;
	}
	
	
	
	
	/*
	protected Pair<Entry, Env> infer(Code.Assign code, Entry stmt, Env environment) {		
		environment = new Env(environment);				
				
		CExpr.LVal lhs = null;
		CExpr rhs;
		
		if(code.lhs != null) {
			if(code.lhs instanceof CExpr.LVar) {
				CExpr.LVar lv = (CExpr.LVar) code.lhs; 
				lhs = lv;
				
				// Simple test
				if(lv.name().equals("this")) {
					syntaxError("cannot assign to variable this",filename,stmt);
				}
			} else {	
				lhs = (CExpr.LVal) infer(code.lhs,stmt,environment);
			}	
			// Update the type of the lhs						
			rhs = infer(code.rhs,stmt,environment);
			lhs = (CExpr.LVal) typeInference(lhs,rhs.type(),environment);			
		} else {
			// I by pass infer(CExpr) here, since that method requires all
			// expressions to have a non-void return type.
			if(code.rhs instanceof DirectInvoke) {				
				rhs = infer((DirectInvoke)code.rhs,stmt,environment);
			} else if(code.rhs instanceof IndirectInvoke) {
				rhs = infer((IndirectInvoke)code.rhs,stmt,environment);
			} else {
				syntaxError("invalid expression",filename,stmt);
				return null; // dead code
			}
		}
		
		stmt = new Entry(new Code.Assign(lhs, rhs), stmt.attributes());
		return new Pair<Entry,Env>(stmt,environment);
	}
	
	protected CExpr typeInference(CExpr lhs, Type type, Env environment) {
		if(lhs instanceof Variable) {
			Variable v = (Variable) lhs;
			environment.put(v.name, type);
			return CExpr.VAR(type,v.name);
		} else if(lhs instanceof Register) {
			Register v = (Register) lhs;
			environment.put("%" + v.index, type);
			return CExpr.REG(type,v.index);
		} else if(lhs instanceof ListAccess) {
			ListAccess la = (ListAccess) lhs;			
			Type.List la_src_t = Type.effectiveListType(la.src.type());
			if(la_src_t instanceof Type.List) {
				Type elem_t = Type.leastUpperBound(la_src_t.element(),type);
				lhs = typeInference(la.src,Type.T_LIST(elem_t),environment);
				return CExpr.LISTACCESS(lhs, la.index);
			} else {
				Type.Dictionary tl = (Type.Dictionary) Type.effectiveDictionaryType(la.src.type());
				Type key_t = Type.leastUpperBound(tl.key(),la.index.type());
				Type val_t = Type.leastUpperBound(tl.value(),type);
				lhs = typeInference(la.src,Type.T_DICTIONARY(key_t,val_t),environment);
				return CExpr.LISTACCESS(lhs, la.index);
			}
		} else if(lhs instanceof RecordAccess) {		
			RecordAccess r = (RecordAccess) lhs;		
			Type lhs_t = updateRecordFieldType(r.lhs.type(),r.field,type);			
			lhs = typeInference(r.lhs, lhs_t, environment);			
			return CExpr.RECORDACCESS(lhs, r.field);
		}	
		
		// default, don't do anything
		return lhs;
	}
	
	protected Type updateRecordFieldType(Type src, String field, Type type) {
		if(src instanceof Type.Record) {
			Type.Record rt = (Type.Record) src;
			HashMap<String,Type> types = new HashMap<String,Type>(rt.fields());
			types.put(field, type);
			return Type.T_RECORD(types);
		} else if(src instanceof Type.Union) {
			Type.Union tu = (Type.Union) src;
			Type t = Type.T_VOID;
			for(Type b : tu.bounds()) {
				t = Type.leastUpperBound(t,updateRecordFieldType(b,field,type));
			}
			return t;
		} 
		
		// no can do
		return type;
	}
	*/
	
	
	protected Triple<Entry,Env,Env> propagate(Code.IfGoto code, Entry stmt, Env environment) {
		environment = (Env) environment.clone();
		
		Type lhs_t = environment.pop();
		Type rhs_t = environment.pop();
		Type lub = Type.leastUpperBound(lhs_t,rhs_t);
		
		switch(code.op) {
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
			checkIsSubtype(Type.T_REAL, lub, stmt);
			break;
		case EQ:
		case NEQ:
			if (!Type.isSubtype(lhs_t, rhs_t) && !Type.isSubtype(rhs_t, lhs_t)) {
				syntaxError("incomparable types: " + lhs_t + " and " + rhs_t,
						filename, stmt);
			}
			break;
		case ELEMOF:
		{			
			Type element;
			if(rhs_t instanceof Type.List){
				element = ((Type.List)rhs_t).element();
			} else if(rhs_t instanceof Type.Set){
				element = ((Type.Set)rhs_t).element();
			} else {
				syntaxError("expected set or list, found: " + rhs_t,filename,stmt);
				return null;
			}
			if (!Type.isSubtype(element, lhs_t)) {
				syntaxError("incomparable types: " + lhs_t + " and " + rhs_t,
						filename, stmt);
			}			
			break;
		}	
		case SUBSET:
		case SUBSETEQ:			
			if (!Type.isSubtype(lhs_t, rhs_t) && !Type.isSubtype(rhs_t, lhs_t)) {
				syntaxError("incomparable types: " + lhs_t + " and " + rhs_t,
						filename, stmt);
			}
			checkIsSubtype(Type.T_SET(Type.T_ANY),lhs_t,stmt);
			checkIsSubtype(Type.T_SET(Type.T_ANY),rhs_t,stmt);
			break;		
		}
		
		code = Code.IfGoto(lub, code.op, code.target);
		stmt = new Entry(code,stmt.attributes());
		return new Triple<Entry,Env,Env>(stmt,environment,environment);
	}
	
	protected Triple<Entry,Env,Env> propagate(Code.IfType code, Entry stmt, Env environment) {
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

		if(Type.isSubtype(code.test,lhs_t)) {								
			// DEFINITE TRUE CASE										
			trueEnv = environment;
			ncode = Code.Goto(code.target);							
		} else if (Type.greatestLowerBound(lhs_t, code.test) == Type.T_VOID) {				
			// DEFINITE FALSE CASE				
			falseEnv = environment;							
			ncode = Code.Skip;							
		} else {
			ncode = Code.IfType(lhs_t, code.slot, code.test, code.target);				
			trueEnv = new Env(environment);
			falseEnv = new Env(environment);		
			if(code.slot >= 0) {
				Type glb = Type.greatestLowerBound(lhs_t, code.test);
				Type gdiff = Type.leastDifference(lhs_t, code.test);	
				trueEnv.set(code.slot, glb);			
				falseEnv.set(code.slot, gdiff);								
			}
		}
		
		stmt = new Entry(ncode,stmt.attributes());		
		return new Triple(stmt,trueEnv,falseEnv);		
	}		

	protected Pair<Entry,List<Env>> propagate(Code.Switch code, Entry stmt, Env environment) {
		Type val = environment.pop();
		ArrayList<Env> envs = new ArrayList<Env>();
		// TODO: update this code to support type inference of types. That is,
		// if we switch on a type value then this will update the type of the
		// value.
		for(Map.Entry<Value, String> e : code.branches.entrySet()) {
			Value cv = e.getKey();
			checkIsSubtype(val,cv.type(),stmt);
			envs.add(environment);
		}
		Code ncode = new Code.Switch(val,code.defaultTarget,code.branches);
		return new Pair(new Entry(ncode,stmt.attributes()),envs);
	}
		
	protected Pair<Block, Env> propagate(Code.ForAll forloop, 
			Block body, Entry stmt, Env environment) {
						
		// Now, type the source 		
		Type src_t = environment.pop();						
		
		Type elem_t;
		if(src_t instanceof Type.List) {
			elem_t = ((Type.List)src_t).element();
		} else if(src_t instanceof Type.Set){
			elem_t = ((Type.Set)src_t).element();
		} else {
			syntaxError("expected set or list, found: " + src_t,filename,stmt);
			return null; // deadcode
		}
		
		Block blk = new Block();
		
		if (elem_t == Type.T_VOID) {
			// This indicates a loop over an empty list. This legitimately can
			// happen as a result of substitution for contraints or pre/post
			// conditions.
			return new Pair<Block,Env>(blk,environment);
		}
				
		
		// create environment specific for loop body
		Env loopEnv = new Env(environment);		
		loopEnv.set(forloop.var, elem_t);
	
		Pair<Block,Env> r = null;
		Env old = null;
		do {
			// iterate until a fixed point reached
			old = r != null ? r.second() : loopEnv;			 			
			r = propagate(body,old);
		 } while(!r.second().equals(old));				
		
		environment = join(environment,r.second());		
		
		// Finally, update the code
		blk.add(Code.ForAll(forloop.var, forloop.target, forloop.modified), stmt.attributes());
		blk.addAll(r.first());
		blk.add(Code.End(forloop.target));
					
		return new Pair<Block,Env>(blk,join(environment,r.second()));
	}
	
	protected Pair<Block, Env> propagate(Code.Loop loop, Block body,
			Entry stmt, Env environment) {

		if (loop instanceof Code.ForAll) {
			return propagate((Code.ForAll) loop, body, stmt, environment);
		}

		Block blk = new Block();
		Pair<Block, Env> r = propagate(body, environment);
		Env old = null;
		do {
			// iterate until a fixed point reached
			old = r != null ? r.second() : environment;
			r = propagate(body, old);
		} while (!r.second().equals(old));

		environment = join(environment, r.second());

		blk.add(Code.Loop(loop.target, loop.modified), stmt.attributes());
		blk.addAll(r.first());
		blk.add(Code.End(loop.target));

		return new Pair<Block, Env>(blk, environment);
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
	protected Type.Fun bindFunction(NameID nid, Type.Process receiver,
			List<Type> paramTypes, SyntacticElement elem) throws ResolveError {
		
		Type.Fun target = Type.T_FUN(receiver, Type.T_ANY,paramTypes);
		Type.Fun candidate = null;				
		
		List<Type.Fun> targets = lookupMethod(nid.module(),nid.name()); 
		
		for (Type.Fun ft : targets) {										
			Type funrec = ft.receiver();			
			if (receiver == funrec
					|| (receiver != null && funrec != null && Type.isSubtype(
							funrec, receiver))) {
				// receivers match up OK ...				
				if (ft.params().size() == paramTypes.size()						
						&& paramSubtypes(ft, target)
						&& (candidate == null || paramSubtypes(candidate,ft))) {					
					candidate = ft;					
				}
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
	
	private boolean paramSubtypes(Type.Fun f1, Type.Fun f2) {
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if(f1_params.size() == f2_params.size()) {
			for(int i=0;i!=f1_params.size();++i) {
				if(!Type.isSubtype(f1_params.get(i),f2_params.get(i))) {
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
		if (!Type.isSubtype(t1, t2)) {
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
