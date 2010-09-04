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
	
	public Module.Case propagate(Module.Case mcase, Module.Method method) {		
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
		
		CExpr.LVal lhs = code.lhs;
		
		CExpr rhs = infer(code.rhs,stmt,environment);

		if(lhs instanceof LVar) {			
			checkIsSubtype(lhs.type(),rhs.type(), stmt);
		} else if(lhs != null) {			
			lhs = (CExpr.LVal) infer(lhs,stmt,environment);
			checkIsSubtype(lhs.type(),rhs.type(), stmt);
		}

		if(lhs instanceof Variable) {
			Variable v = (Variable) lhs;
			environment.put(v.name, rhs.type());
			lhs = CExpr.VAR(rhs.type(),v.name);
		} else if(lhs instanceof Register) {
			Register v = (Register) lhs;
			environment.put("%" + v.index, rhs.type());
			lhs = CExpr.REG(rhs.type(),v.index);
		} // FIXME: other cases
		
		stmt = new Stmt(new Code.Assign(lhs, rhs), stmt.attributes());
		return new Pair<Stmt,Env>(stmt,environment);
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
				if (code.op == Code.COP.SUBTYPEEQ) {
					ncode = new Code.Goto(code.target);
					trueEnv = environment;
				} else {					
					ncode = new Code.Skip();
					falseEnv = environment;
				}
			} else if (!Type.isSubtype(lhs_t, tc.type)) {				
				// DEFINITE FALSE CASE
				if (code.op == Code.COP.NSUBTYPEEQ) {
					ncode = new Code.Goto(code.target);
					trueEnv = environment;
				} else {					
					ncode = new Code.Skip();
					falseEnv = environment;
				}
			} else {
				trueEnv = new Env(environment);
				falseEnv = new Env(environment);
				typeInference(lhs,tc.type,trueEnv, falseEnv);
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
	
	protected void typeInference(CExpr lhs, Type type,
			HashMap<String, Type> trueEnv, HashMap<String, Type> falseEnv) {
		// Now, perform the actual type inference
		if (lhs instanceof CExpr.Variable) {
			CExpr.Variable v = (CExpr.Variable) lhs;			
			Type glb = Type.greatestLowerBound(type, v.type);
			Type gdiff = Type.greatestDifference(v.type, type);			
			trueEnv.put(v.name, glb);
			falseEnv.put(v.name, gdiff);
		} else if (lhs instanceof CExpr.Register) {
			CExpr.Register reg = (CExpr.Register) lhs;
			String name = "%" + reg.index;			
			trueEnv.put(name, Type.greatestLowerBound(type, reg.type));
			falseEnv.put(name, Type.greatestDifference(reg.type, type));
		} else if (lhs instanceof TupleAccess) {
			TupleAccess ta = (TupleAccess) lhs;
			Type.Tuple lhs_t = Type.effectiveTupleType(ta.lhs.type());
			if (lhs_t != null) {
				HashMap<String, Type> ntypes = new HashMap<String, Type>(
						lhs_t.types);
				Type glb = Type.greatestLowerBound(type, lhs_t.types.get(ta.field));				
				ntypes.put(ta.field, glb);
				// FIXME: there is some kind of problem here, as we're replacing
				// one type with an effective type ... seems dodgy.
				typeInference(ta.lhs, Type.T_TUPLE(ntypes), trueEnv, falseEnv);
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
				
		blk.add(new Code.Forall(start.label, CExpr.REG(elem_t,
				start.variable.index), src),stmt.attributes());
				
		// create environment specific for loop body
		Env loopEnv = new Env(environment);
		loopEnv.put("%" + start.variable.index, elem_t);
	
		Pair<Block,Env> r = propagate(body,loopEnv);
		blk.addAll(r.first());
		
		// FIXME: at this point we should continue iterating until a fixed-point
		// is reached.
		
		blk.add(end);
		
		// FIXME: need to generate the modifies set
		return new Pair<Block,Env>(blk,join(environment,r.second()));
	}
	
	protected Pair<Block, Env> propagate(Code.Loop start, Code.LoopEnd end,
			Block body, Stmt stmt, Env environment) {
		
		HashSet<String> modifies = new HashSet<String>();
		
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
		
		// FIXME: we should keep iterating to reach a fixed point here
		environment = join(environment,r.second());
		
		// now construct final modifies set		
		HashSet<CExpr.LVar> mods = new HashSet<CExpr.LVar>();
		for(String v : modifies) {
			Type t = environment.get(v);
			if(v.charAt(0) == '%') {
				mods.add(CExpr.REG(t, Integer.parseInt(v.substring(1))));
			} else {
				mods.add(CExpr.VAR(t, v));
			}
		}
		
		blk.add(new Loop(start.label,mods),stmt.attributes());
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
		} else if (e instanceof Tuple) {
			return infer((Tuple) e, stmt, environment);
		} else if (e instanceof TupleAccess) {
			return infer((TupleAccess) e, stmt, environment);
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
		
	protected CExpr infer(TupleAccess e, Stmt stmt, HashMap<String,Type> environment) {
		CExpr lhs = infer(e.lhs,stmt,environment);				
		Type.Tuple ett = Type.effectiveTupleType(lhs.type());				
		if (ett == null) {
			syntaxError("tuple type required, got: " + lhs.type(), filename, stmt);
		}
		Type ft = ett.types.get(e.field);
		if (ft == null) {
			syntaxError("type has no field named " + e.field, filename, stmt);
		}
		return CExpr.TUPLEACCESS(lhs, e.field);
	}
	
	protected CExpr infer(Tuple e, Stmt stmt, HashMap<String,Type> environment) {
		HashMap<String, CExpr> args = new HashMap<String, CExpr>();
		for (Map.Entry<String, CExpr> v : e.values.entrySet()) {
			args.put(v.getKey(), infer(v.getValue(), stmt, environment));
		}
		return CExpr.TUPLE(args);
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

			if (funtype == null) {
				if (receiver == null) {
					syntaxError("invalid or ambiguous function call", filename,
							stmt);
				} else {
					syntaxError("invalid or ambiguous method call", filename,
							stmt);
				}
			}
			
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
		
		for (Type.Fun ft : lookupMethod(nid.module(),nid.name())) {										
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
		
		return candidate;
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
			syntaxError("expected type " + clazz.getName() + ", found " + t,filename,
					elem);
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
