// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.stages;

import static wyil.util.SyntaxError.syntaxError;

import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.lang.CExpr.*;
import wyil.util.*;
import wyil.util.dfa.*;

public class TypePropagation extends ForwardFlowAnalysis<TypePropagation.Env> {

	public TypePropagation(ModuleLoader loader) {
		super(loader);
	}
		
	public Module.TypeDef propagate(Module.TypeDef type) {
		Block constraint = type.constraint();
		if (constraint == null) {
			return type;
		} else {
			this.stores = new HashMap<String,Env>();
			Env environment = new Env();
			environment.put("$", type.type());
			constraint = propagate(constraint, environment).first();
			return new Module.TypeDef(type.name(), type.type(), constraint);
		}
	}
	
	public Env initialStore() {
		Env environment = new Env();
		
		List<String> paramNames = methodCase.parameterNames();
		List<Type> paramTypes = method.type().params;
		
		for (int i = 0; i != paramNames.size(); ++i) {
			Type t = paramTypes.get(i);
			environment.put(paramNames.get(i), t);
			if (method.type().receiver == null
					&& Type.isSubtype(Type.T_PROCESS(Type.T_ANY), t)) {
				// FIXME: add source information
				syntaxError("function argument cannot have process type",
						filename, methodCase);
			}
		}
		
		if(method.type().receiver != null) {					
			environment.put("this", method.type().receiver);
		}
		
		return environment;
	}
	
	public Module.Case propagate(Module.Case mcase) {		
		this.methodCase = mcase;
		this.stores = new HashMap<String,Env>();
		
		Env environment = initialStore();
		
		Block precondition = mcase.precondition();
		if(precondition != null) {
			Env preenv = new Env(environment);
			precondition = propagate(precondition, preenv).first();
		}
		Block postcondition = mcase.postcondition();
		if(postcondition != null) {
			Env postenv = new Env(environment);
			postenv.put("$",method.type().ret);
			postcondition = propagate(postcondition, postenv).first();			
		}
		
		Block body = propagate(mcase.body(), environment).first();	
		
		return new Module.Case(mcase.parameterNames(), precondition,
				postcondition, body);
	}
	
	protected Pair<Stmt, Env> propagate(Stmt stmt,
			Env environment) {
		
		Code code = stmt.code;
		if(code instanceof Assign) {
			return infer((Assign)code,stmt,environment);
		} else if(code instanceof Debug) {
			code = infer((Debug)code,stmt,environment);
		} else if(code instanceof Return) {
			code = infer((Return)code,stmt,environment);
		}
		
		stmt = new Stmt(code,stmt.attributes());
		return new Pair<Stmt,Env>(stmt,environment);
	}
	
	protected Pair<Stmt, Env> infer(Code.Assign code, Stmt stmt, Env environment) {		
		environment = new Env(environment);				
		
		CExpr rhs = infer(code.rhs,stmt,environment);
		CExpr.LVal lhs = null;
		
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
			
			lhs = (CExpr.LVal) typeInference(lhs,rhs.type(),environment);			
		}
		
		stmt = new Stmt(new Code.Assign(lhs, rhs), stmt.attributes());
		return new Pair<Stmt,Env>(stmt,environment);
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
			Type.List tl = (Type.List) la.src.type();
			Type elem_t = Type.leastUpperBound(tl.element,type);
			lhs = typeInference(la.src,Type.T_LIST(elem_t),environment);
			return CExpr.LISTACCESS(lhs, la.index);
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
			HashMap<String,Type> types = new HashMap<String,Type>(rt.types);
			types.put(field, type);
			return Type.T_RECORD(types);
		} else if(src instanceof Type.Union) {
			Type.Union tu = (Type.Union) src;
			Type t = Type.T_VOID;
			for(Type b : tu.bounds) {
				t = Type.leastUpperBound(t,updateRecordFieldType(b,field,type));
			}
			return t;
		} else if(src instanceof Type.Recursive) {			
			Type.Recursive rt = (Type.Recursive) src;
			return updateRecordFieldType(Type.unfold(rt),field,type);
		} else if(src instanceof Type.Named) {
			Type.Named nd = (Type.Named) src;
			return Type.T_NAMED(nd.name, updateRecordFieldType(nd.type,field,type));
		} 
		
		// no can do
		return type;
	}
	
	protected Code infer(Code.Return code, Stmt stmt, Env environment) {
		CExpr rhs = code.rhs;
		Type ret_t = method.type().ret;
		
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
	
	protected Code infer(Code.Debug code, Stmt stmt, Env environment) {
		CExpr rhs = infer(code.rhs,stmt, environment);
		checkIsSubtype(Type.T_LIST(Type.T_INT),rhs.type(),stmt);
		return new Code.Debug(rhs);
	}
	
	protected Triple<Stmt,Env,Env> propagate(Code.IfGoto code, Stmt stmt, Env environment) {
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
			checkIsSubtype(Type.T_SET(Type.T_ANY),rhs_t,stmt);
			Type element;
			if(rhs_t instanceof Type.List){
				element = ((Type.List)rhs_t).element;
			} else {
				element = ((Type.Set)rhs_t).element;
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
			stmt = new Stmt(ncode,stmt.attributes());
			if(code.op == Code.COP.SUBTYPEEQ) {
				return new Triple(stmt,trueEnv,falseEnv);
			} else {
				// environments are the other way around!
				return new Triple(stmt,falseEnv,trueEnv);
			}
		}
		
		code = new Code.IfGoto(code.op, lhs, rhs, code.target);
		stmt = new Stmt(code,stmt.attributes());
		return new Triple<Stmt,Env,Env>(stmt,environment,environment);
	}
	
	protected void typeInference(CExpr lhs, Type trueType, Type falseType,
			HashMap<String, Type> trueEnv, HashMap<String, Type> falseEnv) {
		
		// System.out.println(lhs + " => " + trueType + " => " + falseType);
		
		// Now, perform the actual type inference
		if (lhs instanceof CExpr.Variable) {			
			CExpr.Variable v = (CExpr.Variable) lhs;			
			Type glb = Type.greatestLowerBound(v.type, trueType);
			Type gdiff = Type.greatestDifference(v.type, falseType);	
			//System.out.println("\nGLB(1): " + Type.toShortString(trueType)
			//		+ " & " + Type.toShortString(v.type) + " = "
			//		+ Type.toShortString(glb));
			//System.out.println("GDIFF(1): " + Type.toShortString(v.type) + " - "
			//		+ Type.toShortString(falseType) + " = "
			//		+ Type.toShortString(gdiff));
			trueEnv.put(v.name, glb);			
			falseEnv.put(v.name, gdiff);			
		} else if (lhs instanceof CExpr.Register) {
			CExpr.Register reg = (CExpr.Register) lhs;
			String name = "%" + reg.index;						
			Type glb = Type.greatestLowerBound(reg.type,trueType);
			Type gdiff = Type.greatestDifference(reg.type, falseType);
//			System.out.println("\nGLB(2): " + Type.toShortString(trueType)
//					+ " & " + Type.toShortString(reg.type) + " = "
//					+ Type.toShortString(glb));
//			System.out.println("GDIFF(2): " + Type.toShortString(reg.type) + " - "
//					+ Type.toShortString(falseType) + " = "
//					+ Type.toShortString(gdiff));
			trueEnv.put(name, glb);
			falseEnv.put(name, gdiff);
		} else if (lhs instanceof RecordAccess) {
			RecordAccess ta = (RecordAccess) lhs;
			Type.Record lhs_t = Type.effectiveRecordType(ta.lhs.type());
			if (lhs_t != null) {
				HashMap<String, Type> ttypes = new HashMap<String, Type>();
				HashMap<String, Type> ftypes = new HashMap<String, Type>();
				for (Map.Entry<String, Type> e : lhs_t.types.entrySet()) {
					String key = e.getKey();
					ttypes.put(key, e.getValue());
					ftypes.put(key, Type.T_VOID);
				}
				Type glb = Type.greatestLowerBound(trueType, lhs_t.types
						.get(ta.field));	
				
				//System.out.println("\nGLB(3): " + trueType + " & " + lhs_t.types.get(ta.field) + " = " + glb);
				
				ttypes.put(ta.field, glb);
				ftypes.put(ta.field, glb);
				typeInference(ta.lhs, Type.T_RECORD(ttypes), Type
						.T_RECORD(ftypes), trueEnv, falseEnv);
			}
		}
	}
	
	protected Pair<Block, Env> propagate(Code.Start start, Code.End end,
			Block body, Stmt stmt, Env environment) {
		if (start instanceof Code.Forall) {
			return propagate((Code.Forall) start, (Code.ForallEnd) end, body,
					stmt, environment);
		} else if (start instanceof Code.Loop) {
			return propagate((Code.Loop) start, (Code.LoopEnd) end, body, stmt,
					environment);
		} else if (start instanceof Code.Induct) {
			return propagate((Code.Induct) start, (Code.InductEnd) end, body,
					stmt, environment);
		}

		// Other blocks are not so tricky to handle.
		Block blk = new Block();
		blk.add(start);
		Pair<Block, Env> r = propagate(body, environment);
		blk.addAll(r.first());
		blk.add(end);		
		return new Pair<Block, Env>(blk, r.second());
	}
	
	protected Pair<Block, Env> propagate(Code.Forall start, Code.ForallEnd end,
			Block body, Stmt stmt, Env environment) {
		
		// First, create modifies set and type the invariant
		HashSet<String> modifies = new HashSet<String>();
		Block invariant = start.invariant;
				
		for(Stmt s : body) {
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
		
		checkIsSubtype(Type.T_SET(Type.T_ANY),src_t,stmt);				
		
		Type elem_t;
		if(src_t instanceof Type.List) {
			elem_t = ((Type.List)src_t).element;
		} else {
			elem_t = ((Type.Set)src_t).element;
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
	
	protected Pair<Block, Env> propagate(Code.Induct start, Code.InductEnd end,
			Block body, Stmt stmt, Env environment) {
		CExpr src = infer(start.source, stmt, environment);
		Type src_t = src.type();			
		
		// First, create modifies set and type the invariant
		// create environment specific for loop body
		Env loopEnv = new Env(environment);		
		String lvar = "%" + start.variable.index;
		loopEnv.put(lvar, src_t);
	
		Pair<Block,Env> r = propagate(body,loopEnv);
		
		// Finally, update the code
		Block blk = new Block();
		blk.add(new Code.Induct(start.label, CExpr.REG(src_t,
				start.variable.index), src), stmt.attributes());
		blk.addAll(r.first());
		blk.add(end);
					
		return new Pair<Block,Env>(blk,join(environment,r.second()));
	}
	
	protected Pair<Block, Env> propagate(Code.Loop start, Code.LoopEnd end,
			Block body, Stmt stmt, Env environment) {
		
		HashSet<String> modifies = new HashSet<String>();
		Block invariant = start.invariant;
		
		for(Stmt s : body) {
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
	
	protected CExpr infer(CExpr e, Stmt stmt, HashMap<String,Type> environment) {

		if (e instanceof Value) {
			return e;
		} else if (e instanceof Variable) {
			return infer((Variable) e, stmt, environment);
		} else if (e instanceof Register) {
			return infer((Register) e, stmt, environment);
		} else if (e instanceof BinOp) {
			return infer((BinOp) e, stmt, environment);
		} else if (e instanceof UnOp) {
			return infer((UnOp) e, stmt, environment);
		} else if (e instanceof NaryOp) {
			return infer((NaryOp) e, stmt, environment);
		} else if (e instanceof ListAccess) {
			return infer((ListAccess) e, stmt, environment);
		} else if (e instanceof Record) {
			return infer((Record) e, stmt, environment);
		} else if (e instanceof RecordAccess) {
			return infer((RecordAccess) e, stmt, environment);
		} else if (e instanceof Invoke) {
			return infer((Invoke) e, stmt, environment);
		}

		syntaxError("unknown expression encountered: " + e, filename, stmt);
		return null; // unreachable
	}
	
	protected CExpr infer(Variable v, Stmt stmt, HashMap<String,Type> environment) {
		Type type = environment.get(v.name);
		if(type == null) {
			syntaxError("variable " + v.name()  + " is undefined",filename,stmt);
		}
		return CExpr.VAR(type,v.name);
	}
	
	protected CExpr infer(Register v, Stmt stmt, HashMap<String,Type> environment) {
		String name = "%" + v.index;
		Type type = environment.get(name);
		if(type == null) {
			syntaxError("register " + name + " is undefined",filename,stmt);
		}
		return CExpr.REG(type,v.index);
	}
	
	protected CExpr infer(UnOp v, Stmt stmt, HashMap<String,Type> environment) {
		CExpr rhs = infer(v.rhs, stmt, environment);
		Type rhs_t = rhs.type();
		switch(v.op) {
			case NEG:
				checkIsSubtype(Type.T_REAL,rhs_t,stmt);
				return CExpr.UNOP(v.op,rhs);
			case LENGTHOF:
				checkIsSubtype(Type.T_SET(Type.T_ANY),rhs_t,stmt);
				return CExpr.UNOP(v.op,rhs);
			case PROCESSACCESS:
				checkIsSubtype(Type.T_PROCESS(Type.T_ANY),rhs_t,stmt);
				return CExpr.UNOP(v.op,rhs);
			case PROCESSSPAWN:
				return CExpr.UNOP(v.op,rhs);
		}
		syntaxError("unknown unary operation: " + v.op,filename,stmt);
		return null;
	}
	
	protected CExpr infer(BinOp v, Stmt stmt, HashMap<String,Type> environment) {
		CExpr lhs = infer(v.lhs, stmt, environment);
		CExpr rhs = infer(v.rhs, stmt, environment);
		Type lub = Type.leastUpperBound(lhs.type(),rhs.type());

		if(Type.isSubtype(Type.T_LIST(Type.T_ANY),lub)) {
			switch(v.op) {
				case APPEND:
				case ADD:															
					return CExpr.BINOP(CExpr.BOP.APPEND,lhs,rhs);
				default:
					syntaxError("Invalid operation on lists",filename,stmt);		
			}	
		} else if(Type.isSubtype(Type.T_SET(Type.T_ANY),lub)) {
			switch(v.op) {
				case ADD:											
				case UNION:
					return CExpr.BINOP(CExpr.BOP.UNION,lhs,rhs);
				case DIFFERENCE:
				case SUB:															
					return CExpr.BINOP(CExpr.BOP.DIFFERENCE,lhs,rhs);
				case INTERSECT:															
					return CExpr.BINOP(v.op,lhs,rhs);
				default:
					syntaxError("Invalid operation on sets: " + v.op,filename,stmt);			
			}
		} 
		
		// FIXME: more cases, including elem of
		
		checkIsSubtype(Type.T_REAL,lub,stmt);	
		return CExpr.BINOP(v.op,lhs,rhs);				
	}
	
	protected CExpr infer(NaryOp v, Stmt stmt, HashMap<String,Type> environment) {
		ArrayList<CExpr> args = new ArrayList<CExpr>();
		for(CExpr arg : v.args) {
			args.add(infer(arg,stmt,environment));
		}
		
		switch(v.op) {
			case SETGEN:				
			case LISTGEN:
				return CExpr.NARYOP(v.op, args);
			case SUBLIST:						
				if(args.size() != 3) {
					syntaxError("Invalid arguments for sublist operation",filename,stmt);
				}
				checkIsSubtype(Type.T_LIST(Type.T_ANY),args.get(0).type(),stmt);
				checkIsSubtype(Type.T_INT,args.get(1).type(),stmt);
				checkIsSubtype(Type.T_INT,args.get(2).type(),stmt);
				return CExpr.NARYOP(v.op, args);
		}
		
		syntaxError("Unknown nary operation",filename,stmt);
		return null;
	}
	
	protected CExpr infer(ListAccess e, Stmt stmt, HashMap<String,Type> environment) {
		CExpr src = infer(e.src,stmt,environment);
		CExpr idx = infer(e.index,stmt,environment);
		checkIsSubtype(Type.T_LIST(Type.T_ANY),src.type(),stmt);
		checkIsSubtype(Type.T_INT,idx.type(),stmt);
		return CExpr.LISTACCESS(src,idx);
	}
		
	protected CExpr infer(RecordAccess e, Stmt stmt, HashMap<String,Type> environment) {								
		CExpr lhs = infer(e.lhs,stmt,environment);					
		Type.Record ett = Type.effectiveRecordType(lhs.type());		
		if (ett == null) {
			syntaxError("tuple type required, got: " + lhs.type(), filename, stmt);
		}
		Type ft = ett.types.get(e.field);		
		if (ft == null) {
			syntaxError("type has no field named " + e.field, filename, stmt);
		}
		return CExpr.RECORDACCESS(lhs, e.field);
	}
	
	protected CExpr infer(Record e, Stmt stmt, HashMap<String,Type> environment) {
		HashMap<String, CExpr> args = new HashMap<String, CExpr>();
		for (Map.Entry<String, CExpr> v : e.values.entrySet()) {
			args.put(v.getKey(), infer(v.getValue(), stmt, environment));
		}
		return CExpr.RECORD(args);
	}
	
	protected CExpr infer(Invoke ivk, Stmt stmt,
			HashMap<String,Type> environment) {
		
		ArrayList<CExpr> args = new ArrayList<CExpr>();
		ArrayList<Type> types = new ArrayList<Type>();
		CExpr receiver = ivk.receiver;
		Type.ProcessName receiverT = null;
		if(receiver != null) {
			receiver = infer(receiver, stmt, environment);
			receiverT = checkType(receiver.type(),Type.ProcessName.class,stmt);
		}
		for (CExpr arg : ivk.args) {
			arg = infer(arg, stmt, environment);
			args.add(arg);
			types.add(arg.type());
		}
		
		try {
			Type.Fun funtype = bindFunction(ivk.name, receiverT, types, stmt);

			return CExpr.INVOKE(funtype, ivk.name, ivk.caseNum, receiver, args);
		} catch (ResolveError ex) {
			syntaxError(ex.getMessage(), filename, stmt);
			return null; // unreachable
		}
	}
	
	/**
	 * Bind function is responsible for determining the true type of a method or
	 * function being invoked. To do this, it must find the function/method
	 * with the most precise type that matches the argument types.
	 * 	 * 
	 * @param nid
	 * @param receiver
	 * @param paramTypes
	 * @param elem
	 * @return
	 * @throws ResolveError
	 */
	protected Type.Fun bindFunction(NameID nid, Type.ProcessName receiver,
			List<Type> paramTypes, SyntacticElement elem) throws ResolveError {
		
		Type.Fun target = Type.T_FUN(receiver, Type.T_ANY,paramTypes);
		Type.Fun candidate = null;				
		
		List<Type.Fun> targets = lookupMethod(nid.module(),nid.name()); 
		
		for (Type.Fun ft : targets) {										
			Type funrec = ft.receiver;			
			if (receiver == funrec
					|| (receiver != null && funrec != null && Type.isSubtype(
							funrec, receiver))) {
				// receivers match up OK ...
				if (ft.params.size() == paramTypes.size()						
						&& Type.isSubtype(ft, target)
						&& (candidate == null || Type.isSubtype(candidate, ft))) {
					// This declaration is a candidate. Now, we need to see if
					// our
					// candidate type signature is as precise as possible.
					if (candidate == null) {
						candidate = ft;
					} else if (Type.isSubtype(candidate, ft)) {
						candidate = ft;
					}
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
					msg += "\n\tfound: " + nid.name() +  parameterString(ft.params);
				} else {
					msg += "\n\tand: " + nid.name() +  parameterString(ft.params);
				}				
				if(++count < targets.size()) {
					msg += ",";
				}
			}
			
			syntaxError(msg + "\n",filename,elem);
		}
		
		return candidate;
	}
	
	private String parameterString(List<Type> paramTypes) {
		String paramStr = "(";
		boolean firstTime = true;
		for(Type t : paramTypes) {
			if(!firstTime) {
				paramStr += ",";
			}
			firstTime=false;
			paramStr += Type.toShortString(t);
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
		if(t instanceof Type.Named) {
			t = ((Type.Named)t).type;
		}
		if (clazz.isInstance(t)) {
			return (T) t;
		} else {
			syntaxError("expected type " + clazz.getName() + ", found "
					+ Type.toShortString(t), filename, elem);
			return null;
		}
	}
	
	// Check t1 :> t2
	protected void checkIsSubtype(Type t1, Type t2, SyntacticElement elem) {
		if (!Type.isSubtype(t1, t2)) {
			syntaxError("expected type " + Type.toShortString(t1) + ", found "
					+ Type.toShortString(t2), filename, elem);
		}
	}

	public Env join(Env env1, Env env2) {
		if (env2 == null) {
			return env1;
		} else if (env1 == null) {
			return env2;
		}
		HashSet<String> keys = new HashSet<String>(env1.keySet());
		keys.addAll(env2.keySet());
		Env env = new Env();
		for (String key : keys) {
			Type mt = env1.get(key);
			Type ot = env2.get(key);
			if (ot != null && mt != null) {
				env.put(key, Type.leastUpperBound(mt, ot));
			}
		}
		return env;
	}
	
	public static class Env extends HashMap<String,Type> {
		public Env() {
		}
		public Env(Map<String,Type> v) {
			super(v);
		}
	}
}
