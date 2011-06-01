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
		
		for (int i = 0; i != paramTypes.size(); ++i) {
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
		Block body = propagate(mcase.body(), environment).first();	
		
		return new Module.Case(body,mcase.attributes());
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
			return new Pair(entry,environment);
		} else if(code instanceof Fail) {
			code = infer((Fail)code,entry,environment);
		} else if(code instanceof FieldLoad) {
			code = infer((FieldLoad)code,entry,environment);
		} else if(code instanceof FieldStore) {
			code = infer((FieldStore)code,entry,environment);
		} else if(code instanceof IndirectInvoke) {
			code = infer((IndirectInvoke)code,entry,environment);
		} else if(code instanceof IndirectSend) {
			code = infer((IndirectSend)code,entry,environment);
		} else if(code instanceof Invoke) {
			code = infer((Invoke)code,entry,environment);
		} else if(code instanceof Label) {
			return new Pair(entry,environment);
		} else if(code instanceof ListLoad) {
			code = infer((ListLoad)code,entry,environment);
		} else if(code instanceof ListStore) {
			code = infer((ListStore)code,entry,environment);
		} else if(code instanceof Load) {
			code = infer((Load)code,entry,environment);
		} else if(code instanceof Return) {
			code = infer((Return)code,entry,environment);
		} else if(code instanceof UnOp) {
			code = infer((UnOp)code,entry,environment);
		} else {
			syntaxError("Need to finish type inference",filename,entry);
			return null;
		}
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
		Type t = environment.pop();
		checkIsSubtype(code.to,t,stmt);
		environment.push(code.to);
		return null;
	}
	
	protected Code infer(Code.Const code, Entry stmt, Env environment) {
		return null;
	}
	
	protected Code infer(Code.Debug code, Entry stmt, Env environment) {
		CExpr rhs = infer(code.rhs,stmt, environment);
		checkIsSubtype(Type.T_LIST(Type.T_INT),rhs.type(),stmt);
		return new Code.Debug(rhs);
	}
	
	protected Code infer(Code.Fail code, Entry stmt, Env environment) {
		return null;
	}
		
	protected Code infer(FieldLoad e, Entry stmt, Env environment) {								
		CExpr lhs = infer(e.lhs,stmt,environment);					
		// FIXME: would help to have effective process type
		if(lhs.type() instanceof Type.Process) {
			// this indicates a process dereference
			lhs = CExpr.UNOP(UOP.PROCESSACCESS,lhs);
		}
		Type.Record ett = Type.effectiveRecordType(lhs.type());		
		if (ett == null) {
			syntaxError("tuple type required, got: " + lhs.type(), filename, stmt);
		}
		Type ft = ett.fields().get(e.field);		
		if (ft == null) {
			syntaxError("type has no field named " + e.field, filename, stmt);
		}
		return CExpr.RECORDACCESS(lhs, e.field);
	}
	
	protected Code infer(FieldStore e, Entry stmt, Env environment) {
		return null;
	}
	
	protected Code infer(IndirectInvoke e, Entry stmt, Env environment) {
		return null;
	}
	
	protected Code infer(IndirectSend e, Entry stmt, Env environment) {
		return null;
	}
	
	protected Code infer(Invoke ivk, Entry stmt, Env environment) {
		
		ArrayList<CExpr> args = new ArrayList<CExpr>();
		ArrayList<Type> types = new ArrayList<Type>();
		CExpr receiver = ivk.receiver;
		Type.Process receiverT = null;
		if(receiver != null) {
			receiver = infer(receiver, stmt, environment);
			receiverT = checkType(receiver.type(),Type.Process.class,stmt);
		}
		for (CExpr arg : ivk.args) {
			arg = infer(arg, stmt, environment);
			args.add(arg);
			types.add(arg.type());
		}
		
		try {
			Type.Fun funtype = bindFunction(ivk.name, receiverT, types, stmt);

			return CExpr.DIRECTINVOKE(funtype, ivk.name, ivk.caseNum, receiver, ivk.synchronous, args);
		} catch (ResolveError ex) {
			syntaxError(ex.getMessage(), filename, stmt);
			return null; // unreachable
		}
	}
	
	protected Code infer(IndirectInvoke ivk, Entry stmt,
			HashMap<String,Type> environment) {
		
		ArrayList<CExpr> args = new ArrayList<CExpr>();		
		CExpr receiver = ivk.receiver;
		CExpr target = ivk.target;
		
		Type.Process receiverT = null;
		if(receiver != null) {
			receiver = infer(receiver, stmt, environment);
			receiverT = checkType(receiver.type(),Type.Process.class,stmt);
		}
		
		target = infer(target, stmt, environment);		
		checkType(target.type(),Type.Fun.class,stmt);
		
		for (CExpr arg : ivk.args) {
			arg = infer(arg, stmt, environment);
			args.add(arg);			
		}						
		
		return CExpr.INDIRECTINVOKE(target, receiver, args);		
	}
	
	protected Code infer(ListLoad e, Entry stmt, Env environment) {
		CExpr src = infer(e.src,stmt,environment);
		CExpr idx = infer(e.index,stmt,environment);
		if(Type.isSubtype(Type.T_DICTIONARY(Type.T_ANY, Type.T_ANY),src.type())) {			
			// this indicates a dictionary access, rather than a list access			
			// FIXME: need "effective dictionary type" or similar here
			Type.Dictionary dict = (Type.Dictionary) src.type();			
			checkIsSubtype(dict.key(),idx.type(),stmt);
			// OK, it's a hit
			return CExpr.LISTACCESS(src,idx);
		} else {
			checkIsSubtype(Type.T_LIST(Type.T_ANY),src.type(),stmt);
			checkIsSubtype(Type.T_INT,idx.type(),stmt);
			return CExpr.LISTACCESS(src,idx);
		}
	}
		
	protected Code infer(ListStore e, Entry stmt, Env environment) {
		return null;
	}
	
	protected Code infer(Load e, Entry stmt, Env environment) {
		e = Code.Load(environment.get(e.slot), e.slot);		
		environment.push(e.type);
		return e;
	}	
	

	protected CExpr infer(NewRecord e, Entry stmt, HashMap<String,Type> environment) {
		HashMap<String, CExpr> args = new HashMap<String, CExpr>();
		for (Map.Entry<String, CExpr> v : e.values.entrySet()) {
			args.put(v.getKey(), infer(v.getValue(), stmt, environment));
		}
		return CExpr.RECORD(args);
	}
	
	protected CExpr infer(NewDict e, Entry stmt, HashMap<String,Type> environment) {
		HashSet<Pair<CExpr,CExpr>> args = new HashSet();
		for (Pair<CExpr,CExpr> v : e.values) {
			CExpr key = infer(v.first(), stmt, environment);
			CExpr value = infer(v.second(), stmt, environment);
			args.add(new Pair<CExpr,CExpr>(key,value));			
		}
		return CExpr.DICTIONARY(args);
	}
	
	protected Code infer(Store e, Entry stmt, Env environment) {
		e = Code.Store(environment.pop(), e.slot);		
		environment.set(e.slot, e.type);
		return e;
	}
	
	protected Code infer(Code.Return code, Entry stmt, Env environment) {
		CExpr rhs = code.rhs;
		Type ret_t = method.type().ret();
		
		if(rhs != null) {
			if(ret_t == Type.T_VOID) {
				syntaxError(
						"cannot return value from method with void return type",
						filename, stmt);
			}
			
			rhs = infer(rhs,stmt,environment);
			
			checkIsSubtype(ret_t,rhs.type(),stmt);
		} else if(ret_t != Type.T_VOID) {
			syntaxError(
					"missing return value",filename, stmt);
		}
		
		return new Code.Return(rhs);
	}
	

	protected Code infer(UnOp v, Entry stmt, Env environment) {
		CExpr rhs = infer(v.rhs, stmt, environment);
		Type rhs_t = rhs.type();
		switch(v.op) {
			case NEG:
				checkIsSubtype(Type.T_REAL,rhs_t,stmt);
				return CExpr.UNOP(v.op,rhs);
			case LENGTHOF:
				if(rhs_t instanceof Type.List || rhs_t instanceof Type.Set) {				
					return CExpr.UNOP(v.op,rhs);
				} else {
					syntaxError("expected list or set, found " + rhs_t,filename,stmt);
				}
			case PROCESSACCESS:
				checkIsSubtype(Type.T_PROCESS(Type.T_ANY),rhs_t,stmt);
				return CExpr.UNOP(v.op,rhs);
			case PROCESSSPAWN:
				return CExpr.UNOP(v.op,rhs);
		}
		syntaxError("unknown unary operation: " + v.op,filename,stmt);
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
		CExpr lhs = infer(code.lhs,stmt,environment);
		CExpr rhs = infer(code.rhs,stmt,environment);
		Type lhs_t = lhs.type();
		Type rhs_t = rhs.type();
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
		case NSUBTYPEEQ:			
		case SUBTYPEEQ:
			Value.TypeConst tc = (Value.TypeConst) rhs;
						
			Code ncode = code;
			Env trueEnv = null;
			Env falseEnv = null;
			
			if(Type.isSubtype(tc.type,lhs_t)) {								
				// DEFINITE TRUE CASE										
				trueEnv = environment;
				if (code.op == Code.COP.SUBTYPEEQ) {					
					ncode = new Code.Goto(code.target);					
				} else {					
					ncode = new Code.Skip();					
				}
			} else if (Type.greatestLowerBound(lhs_t, tc.type) == Type.T_VOID) {				
				// DEFINITE FALSE CASE				
				falseEnv = environment;
				if (code.op == Code.COP.NSUBTYPEEQ) {					
					ncode = new Code.Goto(code.target);					
				} else {								
					ncode = new Code.Skip();					
				}
			} else {
				ncode = new Code.IfGoto(code.op, lhs, rhs, code.target);				
				trueEnv = new Env(environment);
				falseEnv = new Env(environment);						
				typeInference(lhs,tc.type,tc.type,trueEnv, falseEnv);				
			}
			stmt = new Entry(ncode,stmt.attributes());
			if(code.op == Code.COP.SUBTYPEEQ) {
				return new Triple(stmt,trueEnv,falseEnv);
			} else {
				// environments are the other way around!
				return new Triple(stmt,falseEnv,trueEnv);
			}
		}
		
		code = new Code.IfGoto(code.op, lhs, rhs, code.target);
		stmt = new Entry(code,stmt.attributes());
		return new Triple<Entry,Env,Env>(stmt,environment,environment);
	}
	
	protected void typeInference(CExpr lhs, Type trueType, Type falseType,
			HashMap<String, Type> trueEnv, HashMap<String, Type> falseEnv) {
		
		// System.out.println(lhs + " => " + trueType + " => " + falseType);
		
		// Now, perform the actual type inference
		if (lhs instanceof CExpr.Variable) {			
			CExpr.Variable v = (CExpr.Variable) lhs;			
			Type glb = Type.greatestLowerBound(v.type, trueType);
			Type gdiff = Type.leastDifference(v.type, falseType);	

//			 System.out.println("\nGLB(1): " + trueType
//			 + " & " + v.type + " = " + glb);
//			 System.out.println("GDIFF(1): " + v.type + " - "
//			 + falseType + " = " + gdiff);
//			
			trueEnv.put(v.name, glb);			
			falseEnv.put(v.name, gdiff);			
		} else if (lhs instanceof CExpr.Register) {
			CExpr.Register reg = (CExpr.Register) lhs;
			String name = "%" + reg.index;						
			Type glb = Type.greatestLowerBound(reg.type,trueType);
			Type gdiff = Type.leastDifference(reg.type, falseType);
//			System.out.println("\nGLB(2): " + trueType
//					+ " & " + reg.type + " = " + glb);
//			System.out.println("GDIFF(2): " + reg.type + " - "
//					+ falseType + " = " + gdiff);
			trueEnv.put(name, glb);
			falseEnv.put(name, gdiff);
		} else if (lhs instanceof RecordAccess) {
			RecordAccess ta = (RecordAccess) lhs;
			Type.Record lhs_t = Type.effectiveRecordType(ta.lhs.type());
			if (lhs_t != null) {
				HashMap<String, Type> ttypes = new HashMap<String, Type>();
				HashMap<String, Type> ftypes = new HashMap<String, Type>();
				for (Map.Entry<String, Type> e : lhs_t.fields().entrySet()) {
					String key = e.getKey();
					ttypes.put(key, e.getValue());
					ftypes.put(key, Type.T_VOID);
				}
				Type glb = Type.greatestLowerBound(trueType, lhs_t.fields()
						.get(ta.field));	
				
				//System.out.println("\nGLB(3): " + trueType + " & " + lhs_t.types.get(ta.field) + " = " + glb);
				
				ttypes.put(ta.field, glb);
				ftypes.put(ta.field, glb);
				typeInference(ta.lhs, Type.T_RECORD(ttypes), Type
						.T_RECORD(ftypes), trueEnv, falseEnv);
			}
		}
	}
	

	protected Pair<Entry,List<Env>> propagate(Code.Switch code, Entry stmt, Env environment) {
		CExpr value = infer(code.value,stmt,environment);
		ArrayList<Env> envs = new ArrayList<Env>();
		// TODO: update this code to support type inference of types. That is,
		// if we switch on a type value then this will update the type of the
		// value.
		for(int i=0;i!=code.branches.size();++i) {
			envs.add(environment);
		}
		Code ncode = new Code.Switch(value,code.defaultTarget,code.branches);
		return new Pair(new Entry(ncode,stmt.attributes()),envs);
	}
		
	protected Pair<Block, Env> propagate(Code.ForAll start, 
			Block body, Entry stmt, Env environment) {
						
		// First, create modifies set and type the invariant
		HashSet<String> modifies = new HashSet<String>();
		Block invariant = start.invariant;
				
		for(Entry s : body) {
			if(s.code instanceof Code.Assign) {
				Code.Assign a = (Code.Assign) s.code;
				if(a.lhs != null) {
					LVar v = CExpr.extractLVar(a.lhs);						
					modifies.add(v.name());
				}
			}
		}
		
		// Now, type the source 
		CExpr src = infer(start.source, stmt, environment);
		Type src_t = src.type();						
		
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
		String loopVar = "%" + start.variable.index;
		loopEnv.put(loopVar, elem_t);
	
		Pair<Block,Env> r = null;
		Env old = null;
		do {
			// iterate until a fixed point reached
			old = r != null ? r.second() : loopEnv;			 			
			r = propagate(body,old);
		 } while(!r.second().equals(old));				
		
		environment = join(environment,r.second());
		
		if(invariant != null) {
			// we have to propagate the invariant here, since we must wait until
			// the proper environment is known.
			invariant = propagate(invariant,environment).first();			
		}
				
		// now construct final modifies set						
		HashSet<CExpr.LVar> mods = new HashSet<CExpr.LVar>();
		for(String v : modifies) {
			Type t = environment.get(v);
			if(t == null) { continue; }
			if(v.charAt(0) == '%') {
				mods.add(CExpr.REG(t, Integer.parseInt(v.substring(1))));
			} else {
				mods.add(CExpr.VAR(t, v));
			}
		}
		
		// Finally, update the code
		blk.add(new Code.Forall(start.label, invariant, CExpr.REG(elem_t,
				start.variable.index), src, mods), stmt.attributes());
		blk.addAll(r.first());
		blk.add(end);
					
		return new Pair<Block,Env>(blk,join(environment,r.second()));
	}
	protected Pair<Block, Env> propagate(Code.Loop start, Code.LoopEnd end,
			Block body, Entry stmt, Env environment) {
		
		HashSet<String> modifies = new HashSet<String>();
		Block invariant = start.invariant;
		
		for(Entry s : body) {
			if(s.code instanceof Code.Assign) {
				Code.Assign a = (Code.Assign) s.code;
				if(a.lhs != null) {
					LVar v = CExpr.extractLVar(a.lhs);						
					modifies.add(v.name());
				}
			}
		}

		Block blk = new Block();		
		Pair<Block,Env> r = propagate(body,environment);
		Env old = null;
		do {
			// iterate until a fixed point reached
			old = r != null ? r.second() : environment;			 			
			r = propagate(body,old);
		 } while(!r.second().equals(old));
		
		environment = join(environment,r.second());
		
		if(invariant != null) {
			// we have to propagate the invariant here, since we must wait until
			// the proper environment is known.
			invariant = propagate(invariant,environment).first();			
		}
						
		// now construct final modifies set		
		HashSet<CExpr.LVar> mods = new HashSet<CExpr.LVar>();
		for(String v : modifies) {
			Type t = environment.get(v);
			if(t == null) { continue; }
			if(v.charAt(0) == '%') {
				mods.add(CExpr.REG(t, Integer.parseInt(v.substring(1))));
			} else {
				mods.add(CExpr.VAR(t, v));
			}
		}
		
		blk.add(new Loop(start.label,invariant,mods),stmt.attributes());
		blk.addAll(r.first());
		blk.add(end);
		
		return new Pair<Block,Env>(blk,environment);
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
		public Env(List<Type> v) {
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
	}
}
