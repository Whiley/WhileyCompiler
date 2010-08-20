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
			Env environment = new Env();
			environment.put("$", type.type());
			constraint = transform(constraint, environment);
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
		Env environment = new Env();
		
		List<String> paramNames = mcase.parameterNames();
		List<Type> paramTypes = method.type().params;
		for(int i=0;i!=paramNames.size();++i) {
			environment.put(paramNames.get(i), paramTypes.get(i));
		}
		
		Block precondition = mcase.precondition();
		if(precondition != null) {
			precondition = transform(precondition, environment);
		}
		Block postcondition = mcase.postcondition();
		if(postcondition != null) {
			environment.put("$",method.type().ret);
			postcondition = transform(postcondition, environment);
			environment.remove("$");
		}
				
		Block body = transform(mcase.body(), environment);		
		return new Module.Case(mcase.parameterNames(), precondition,
				postcondition, body);
	}
	
	protected Block transform(Block block, Env environment) {
		Block nblock = new Block();
		HashMap<String,Env> flowsets = new HashMap<String,Env>();		
		for(int i=0;i!=block.size();++i) {
			Stmt stmt = block.get(i);
			Code code = stmt.code;
			
			if(code instanceof Goto) {
				Goto got = (Goto) code;
				merge(got.target,environment,flowsets);
				environment = null;
			} else if(code instanceof IfGoto) {
				IfGoto igot = (IfGoto) code;	
				// FIXME: broken!
				code = infer((Code.IfGoto)code,stmt,environment);
				merge(igot.target,environment,flowsets);
			} else if(code instanceof Assign) {
				code = infer((Code.Assign)code,stmt,environment);
			} else if(code instanceof Return) {
				code = infer((Code.Return)code,stmt,environment);
			} else if(code instanceof Forall) {
				Code.Forall fall = (Code.Forall) code;
				code = infer(fall,stmt,environment);
			} else if(code instanceof Debug) {
				code = infer((Code.Debug)code,stmt,environment);
			} else if(code instanceof Label) {
				Label label = (Label) code;
				if(environment == null) {
					environment = flowsets.get(label.label);
				} else {
					environment.join(flowsets.get(label.label));
				}
			}
			
			nblock.add(code, stmt.attributes());
		}
		return nblock;
	}
	
	protected void merge(String target, Env env,
			HashMap<String, Env> flowsets) {
		
		Env e = flowsets.get(target);
		if(e == null) {
			flowsets.put(target, env);
		} else {
			e.join(env);
			flowsets.put(target, env);
		}
	}
	
	protected Code infer(Code.Forall code, Stmt stmt,
			Env environment) {
		
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
	
	protected Code infer(Code.Assign code, Stmt stmt, Env environment) {
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
		
	protected Code infer(Code.IfGoto code, Stmt stmt, Env environment) {
		CExpr lhs = infer(code.lhs,stmt,environment);
		CExpr rhs = infer(code.rhs,stmt,environment);
		// FIXME: PERFORM TYPE INFERENCE
		return new Code.IfGoto(code.op, lhs, rhs, code.target);
	}
	
	protected Code infer(Code.Return code, Stmt stmt, Env environment) {
		CExpr rhs = code.rhs;
		
		if(rhs != null) {
			rhs = infer(rhs,stmt,environment);
		}
		
		return new Code.Return(rhs);
	}
	
	protected Code infer(Code.Debug code, Stmt stmt, Env environment) {
		CExpr rhs = infer(code.rhs,stmt, environment);
		checkIsSubtype(Type.T_LIST(Type.T_INT),rhs.type(),stmt);
		return new Code.Debug(rhs);
	}
	
	protected CExpr infer(CExpr e, Stmt stmt, Env environment) {

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
	
	protected CExpr infer(Variable v, Stmt stmt, Env environment) {
		Type type = environment.get(v.name);
		if(type == null) {
			syntaxError("unknown variable: " + v,filename,stmt);
		}
		return CExpr.VAR(type,v.name);
	}
	
	protected CExpr infer(Register v, Stmt stmt, Env environment) {
		String name = "%" + v.index;
		Type type = environment.get(name);
		if(type == null) {
			syntaxError("unknown register: " + name,filename,stmt);
		}
		return CExpr.REG(type,v.index);
	}
	
	protected CExpr infer(UnOp v, Stmt stmt, Env environment) {
		CExpr rhs = infer(v.rhs, stmt, environment);
		Type rhs_t = rhs.type();
		switch(v.op) {
			case LENGTHOF:
				checkIsSubtype(Type.T_SET(Type.T_ANY),rhs_t,stmt);
				return CExpr.UNOP(v.op,rhs);
			case PROCESSACCESS:
				checkIsSubtype(Type.T_PROCESS(Type.T_ANY),rhs_t,stmt);
				return CExpr.UNOP(v.op,rhs);
			case PROCESSSPAWN:
				return CExpr.UNOP(v.op,rhs);
		}
		syntaxError("unknown unary operation",filename,stmt);
		return null;
	}
	
	protected CExpr infer(BinOp v, Stmt stmt, Env environment) {
		CExpr lhs = infer(v.lhs, stmt, environment);
		CExpr rhs = infer(v.rhs, stmt, environment);
		Type lub = Type.leastUpperBound(lhs.type(),rhs.type());
		
		if(Type.isSubtype(Type.T_LIST(Type.T_ANY),lub)) {
			switch(v.op) {
				case ADD:															
					return CExpr.BINOP(CExpr.BOP.APPEND,convert(lub,lhs),convert(lub,rhs));
				default:
					syntaxError("Invalid operation on lists",filename,stmt);		
			}	
		} else if(Type.isSubtype(Type.T_SET(Type.T_ANY),lub)) {
			switch(v.op) {
				case ADD:															
					return CExpr.BINOP(CExpr.BOP.UNION,convert(lub,lhs),convert(lub,rhs));
				case SUB:															
					return CExpr.BINOP(CExpr.BOP.DIFFERENCE,convert(lub,lhs),convert(lub,rhs));
				case INTERSECT:															
					return CExpr.BINOP(v.op,convert(lub,lhs),convert(lub,rhs));
				default:
					syntaxError("Invalid operation on sets",filename,stmt);			
			}
		} 
		
		// FIXME: more cases, including elem of
		
		checkIsSubtype(Type.T_REAL,lub,stmt);	
		return CExpr.BINOP(v.op,convert(lub,lhs),convert(lub,rhs));				
	}
	
	protected CExpr infer(NaryOp v, Stmt stmt, Env environment) {
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
	
	protected CExpr infer(ListAccess e, Stmt stmt, Env environment) {
		CExpr src = infer(e.src,stmt,environment);
		CExpr idx = infer(e.index,stmt,environment);
		checkIsSubtype(Type.T_LIST(Type.T_ANY),src.type(),stmt);
		checkIsSubtype(Type.T_INT,idx.type(),stmt);
		return CExpr.LISTACCESS(e.src,e.index);
	}
		
	protected CExpr infer(TupleAccess e, Stmt stmt, Env environment) {
		CExpr lhs = infer(e.lhs,stmt,environment);				
		Type.Tuple ett = Type.effectiveTupleType(lhs.type());				
		if (ett == null) {
			syntaxError("tuple type required", filename, stmt);
		}
		Type ft = ett.types.get(e.field);
		if (ft == null) {
			syntaxError("type has no field named " + e.field, filename, stmt);
		}
		return CExpr.TUPLEACCESS(lhs, e.field);
	}
	
	protected CExpr infer(Tuple e, Stmt stmt, Env environment) {
		HashMap<String, CExpr> args = new HashMap<String, CExpr>();
		for (Map.Entry<String, CExpr> v : e.values.entrySet()) {
			args.put(v.getKey(), infer(v.getValue(), stmt, environment));
		}
		return CExpr.TUPLE(args);
	}
	
	protected CExpr infer(Invoke ivk, Stmt stmt,
			Env environment) {
		
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
				Type type = types.get(i);
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

	private static class Env extends HashMap<String,Type> {
		public Env() {}
		public Env(Map<String,Type> e) {
			super(e);
		}

		public void join(Env other) {
			if(other == null) { return; }
			HashSet<String> keys = new HashSet<String>(keySet());
			keys.addAll(other.keySet());
			for (String key : keys) {
				Type mt = get(key);
				Type ot = other.get(key);
				if (ot == null || mt == null) {
					remove(key);
				} else {
					put(key, Type.leastUpperBound(mt, ot));
				}
			}
		}
		
		public Env clone() {
			return new Env(this);
		}
	}
}
