package wyil.util.transform;

import static wyil.util.SyntaxError.syntaxError;

import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.lang.CExpr.*;
import wyil.util.ResolveError;
import wyil.util.SyntacticElement;
import wyil.util.dfa.*;

public class TypeInference implements ModuleTransform {
	private final ModuleLoader loader;
	private String filename;

	public TypeInference(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public Module apply(Module module) {
		ArrayList<Module.TypeDef> types = new ArrayList<Module.TypeDef>();		
		ArrayList<Module.Method> methods = new ArrayList<Module.Method>();
		
		filename = module.filename();
		
		for(Module.TypeDef type : module.types()) {
			types.add(transform(type));
		}		
		for(Module.Method method : module.methods()) {
			methods.add(transform(method));
		}
		return new Module(module.id(), module.filename(), methods, types,
				module.constants());
	}
	
	public Module.TypeDef transform(Module.TypeDef type) {
		Block constraint = type.constraint();
		if (constraint == null) {
			return type;
		} else {
			HashMap<String,Type> environment = new HashMap<String,Type>();
			environment.put("$", type.type());
			constraint = transform(constraint, environment, null);
			return new Module.TypeDef(type.name(), type.type(), constraint);
		}
	}
	
	public Module.Method transform(Module.Method method) {
		ArrayList<Module.Case> cases = new ArrayList<Module.Case>();
		for (Module.Case c : method.cases()) {
			cases.add(transform(c,method));
		}
		return new Module.Method(method.name(), method.type(), cases);
	}
	
	public Module.Case transform(Module.Case mcase, Module.Method method) {		
		HashMap<String,Type> environment = new HashMap<String,Type>();
		
		List<String> paramNames = mcase.parameterNames();
		List<Type> paramTypes = method.type().params;
		for(int i=0;i!=paramNames.size();++i) {
			environment.put(paramNames.get(i), paramTypes.get(i));
		}
		
		if(method.type().receiver != null) {
			environment.put("this", method.type().receiver);
		}
		
		Block precondition = mcase.precondition();
		if(precondition != null) {
			precondition = transform(precondition, environment, method);
		}
		Block postcondition = mcase.postcondition();
		if(postcondition != null) {
			environment.put("$",method.type().ret);
			postcondition = transform(postcondition, environment, method);
			environment.remove("$");
		}
				
		Block body = transform(mcase.body(), environment, method);		
		return new Module.Case(mcase.parameterNames(), precondition,
				postcondition, body);
	}
	
	protected Block transform(Block block, HashMap<String,Type> environment, Module.Method method) {
		Block nblock = new Block();
		HashMap<String,HashMap<String,Type>> flowsets = new HashMap<String,HashMap<String,Type>>();		
		for(int i=0;i!=block.size();++i) {
			Stmt stmt = block.get(i);
			Code code = stmt.code;
			
			if(code instanceof Label) {
				Label label = (Label) code;
				if(environment == null) {
					environment = flowsets.get(label.label);
				} else {
					join(environment,flowsets.get(label.label));
				}				
			} else if(environment == null) {				
				continue; // this indicates dead-code
			} else if(code instanceof Goto) {
				Goto got = (Goto) code;
				merge(got.target,environment,flowsets);
				environment = null;
			} else if(code instanceof IfGoto) {
				IfGoto igot = (IfGoto) code;	
				HashMap<String,Type> tenv = new HashMap<String,Type>(environment);
				code = infer((Code.IfGoto)code,stmt,tenv,environment);
				// Observe that the following is needed because type inference
				// can determine that an if-statement definitely is taken, or
				// definitely isn't taken.
				if(code instanceof Code.IfGoto) {
					merge(igot.target,tenv,flowsets);
				} else if(code instanceof Code.Goto) {
					merge(igot.target,tenv,flowsets);
					environment = null;
				}								
			} else if(code instanceof Assign) {
				code = infer((Code.Assign)code,stmt,environment);
			} else if(code instanceof Return) {
				code = infer((Code.Return)code,stmt,environment,method);
			} else if(code instanceof Forall) {
				Code.Forall fall = (Code.Forall) code;
				code = infer(fall,stmt,environment);
			} else if(code instanceof Debug) {
				code = infer((Code.Debug)code,stmt,environment);
			} 
			
			nblock.add(code, stmt.attributes());
		}
		return nblock;
	}
	
	protected void merge(String target, HashMap<String,Type> env,
			HashMap<String, HashMap<String,Type>> flowsets) {
		
		HashMap<String,Type> e = flowsets.get(target);
		if(e == null) {
			flowsets.put(target, env);
		} else {
			join(e,env);
			flowsets.put(target, env);
		}
	}
	
	protected Code infer(Code.Forall code, Stmt stmt,
			HashMap<String,Type> environment) {
		
		CExpr src = infer(code.source, stmt, environment);
		Type src_t = src.type();				
		
		checkIsSubtype(Type.T_SET(Type.T_ANY),src_t,stmt);
		
		Type elem_t;
		if(src_t instanceof Type.List) {
			elem_t = ((Type.List)src_t).element;
		} else {
			elem_t = ((Type.Set)src_t).element;
		}
		
		environment.put("%" + code.variable.index, elem_t);
		
		return new Code.Forall(code.label, CExpr.REG(elem_t,
				code.variable.index), src);
	}
	
	protected Code infer(Code.Assign code, Stmt stmt, HashMap<String,Type> environment) {
		CExpr.LVal lhs = code.lhs;
		

		CExpr rhs = infer(code.rhs,stmt,environment);

		if(lhs instanceof LVar) {
			// do nothing
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
		
		return new Code.Assign(lhs,rhs);
	}
		
	protected Code infer(Code.IfGoto code, Stmt stmt, HashMap<String,Type> trueEnv, HashMap<String,Type> falseEnv) {
		CExpr lhs = infer(code.lhs,stmt,trueEnv);
		CExpr rhs = infer(code.rhs,stmt,trueEnv);
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
			
			return new Code.IfGoto(code.op, convert(element, lhs), rhs,
					code.target);
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
			// this is a tad sneaky
			HashMap<String,Type> tmp = trueEnv;
			trueEnv = falseEnv;
			falseEnv = tmp;
		case SUBTYPEEQ:
			Value.TypeConst tc = (Value.TypeConst) rhs; 				
			if (!Type.isSubtype(lhs_t, tc.type)
					&& !Type.isSubtype(tc.type, lhs_t)) {				
				if (code.op == Code.COP.NSUBTYPEEQ) {
					return new Code.Goto(code.target);					
				} else {					
					return new Code.Skip();
				}
			}
			// Now, perform the actual type inference
			if(lhs instanceof CExpr.Variable) {
				CExpr.Variable v = (CExpr.Variable) lhs;
				// FIXME: want to use the GLB here, so that if we have a more
				// precise type for the variable already then we don't
				// compromise that.
				trueEnv.put(v.name, tc.type);
				// FIXME: want to use type difference here, so that we can
				// register the fact that this definitely is not a particular
				// type.
			} else if(lhs instanceof CExpr.Register) {
				CExpr.Register reg = (CExpr.Register) lhs;
				// FIXME: want to use the GLB here, so that if we have a more
				// precise type for the variable already then we don't
				// compromise that.
				trueEnv.put("%" + reg.index, tc.type);
				// FIXME: want to use type difference here, so that we can
				// register the fact that this definitely is not a particular
				// type.				
			}
			
			return new Code.IfGoto(code.op, lhs, rhs,code.target);
		}
				
		return new Code.IfGoto(code.op, convert(lub, lhs), convert(lub, rhs),
				code.target);
	}
	
	protected Code infer(Code.Return code, Stmt stmt, HashMap<String,Type> environment,
			Module.Method method) {
		CExpr rhs = code.rhs;
		
		if(rhs != null) {
			Type ret_t = method.type().ret;
			if(ret_t == Type.T_VOID) {
				syntaxError(
						"cannot return value, as method has void return type",
						filename, stmt);
			}
			rhs = convert(ret_t,infer(rhs,stmt,environment));
		}
		
		return new Code.Return(rhs);
	}
	
	protected Code infer(Code.Debug code, Stmt stmt, HashMap<String,Type> environment) {
		CExpr rhs = infer(code.rhs,stmt, environment);
		checkIsSubtype(Type.T_LIST(Type.T_INT),rhs.type(),stmt);
		return new Code.Debug(rhs);
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
		} else if (e instanceof Convert) {
			return infer((Convert) e, stmt, environment);
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
			syntaxError("unknown variable: " + v,filename,stmt);
		}
		return CExpr.VAR(type,v.name);
	}
	
	protected CExpr infer(Register v, Stmt stmt, HashMap<String,Type> environment) {
		String name = "%" + v.index;
		Type type = environment.get(name);
		if(type == null) {
			syntaxError("unknown register: " + name,filename,stmt);
		}
		return CExpr.REG(type,v.index);
	}
	
	protected CExpr infer(Convert v, Stmt stmt, HashMap<String,Type> environment) {
		CExpr rhs = infer(v.rhs, stmt, environment);
		return CExpr.CONVERT(v.type,rhs);
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
					return CExpr.BINOP(CExpr.BOP.APPEND,convert(lub,lhs),convert(lub,rhs));
				default:
					syntaxError("Invalid operation on lists",filename,stmt);		
			}	
		} else if(Type.isSubtype(Type.T_SET(Type.T_ANY),lub)) {
			switch(v.op) {
				case ADD:											
				case UNION:
					return CExpr.BINOP(CExpr.BOP.UNION,convert(lub,lhs),convert(lub,rhs));
				case DIFFERENCE:
				case SUB:															
					return CExpr.BINOP(CExpr.BOP.DIFFERENCE,convert(lub,lhs),convert(lub,rhs));
				case INTERSECT:															
					return CExpr.BINOP(v.op,convert(lub,lhs),convert(lub,rhs));
				default:
					syntaxError("Invalid operation on sets: " + v.op,filename,stmt);			
			}
		} 
		
		// FIXME: more cases, including elem of
		
		checkIsSubtype(Type.T_REAL,lub,stmt);	
		return CExpr.BINOP(v.op,convert(lub,lhs),convert(lub,rhs));				
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
		return CExpr.LISTACCESS(e.src,e.index);
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

			for (int i=0;i!=args.size();++i) {
				Type type = funtype.params.get(i);
				CExpr arg = args.get(i);
				args.set(i,convert(type,arg));
			}
			
			return CExpr.INVOKE(funtype, ivk.name, 0, receiver, args);
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
	

	protected CExpr convert(Type toType, CExpr expr) {
		Type fromType = expr.type();		
		if (toType.equals(fromType)) {			
			return expr;
		} else {			
			return CExpr.CONVERT(toType, expr);
		}
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

	public static void join(HashMap<String, Type> env1,
			HashMap<String, Type> env2) {
		if (env2 == null) {
			return;
		}
		HashSet<String> keys = new HashSet<String>(env1.keySet());
		keys.addAll(env2.keySet());
		for (String key : keys) {
			Type mt = env1.get(key);
			Type ot = env2.get(key);
			if (ot == null || mt == null) {
				env1.remove(key);
			} else {
				env1.put(key, Type.leastUpperBound(mt, ot));
			}
		}
	}
}
