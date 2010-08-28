package wyil.stages;

import static wyil.util.SyntaxError.syntaxError;

import java.math.BigInteger;
import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.lang.CExpr.*;
import wyil.util.ResolveError;
import wyil.util.SyntacticElement;
import wyil.util.dfa.*;

public class ConstantPropagation implements ModuleTransform {
	private final ModuleLoader loader;
	private String filename;

	public ConstantPropagation(ModuleLoader loader) {
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
			HashMap<String, Value> environment = new HashMap<String, Value>();
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
		HashMap<String,Value> environment = new HashMap<String,Value>();
				
		Block precondition = mcase.precondition();
		if(precondition != null) {			
			precondition = transform(precondition, environment, method);
		}
		Block postcondition = mcase.postcondition();
		if(postcondition != null) {			
			postcondition = transform(postcondition, environment, method);			
		}
				
		Block body = transform(mcase.body(), environment, method);		
		return new Module.Case(mcase.parameterNames(), precondition,
				postcondition, body);
	}
	
	protected Block transform(Block block, HashMap<String, Value> environment,
			Module.Method method) {
		Block nblock = new Block();
		HashMap<String, HashMap<String, Value>> flowsets = new HashMap<String, HashMap<String, Value>>();

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
			} 
			
			if(environment == null) {				
				continue; // this indicates dead-code
			} else if(code instanceof Goto) {
				Goto got = (Goto) code;
				merge(got.target,environment,flowsets);
				environment = null;
			} else if(code instanceof IfGoto) {
				IfGoto igot = (IfGoto) code;					
				code = infer((Code.IfGoto)code,stmt,environment);
				// Observe that the following is needed because type inference
				// can determine that an if-statement definitely is taken, or
				// definitely isn't taken.
				if(code instanceof Code.IfGoto) {
					merge(igot.target,environment,flowsets);
				} else if(code instanceof Code.Goto) {					
					merge(igot.target,environment,flowsets);
					environment = null;
				} 
			} else if(code instanceof Assign) {
				code = infer((Code.Assign)code,stmt,environment);
			} else if(code instanceof Return) {
				code = infer((Code.Return)code,stmt,environment,method);
				environment = null;
			} else if(code instanceof Fail) {				
				environment = null;
			} else if(code instanceof Forall) {
				Code.Forall fall = (Code.Forall) code;
				fall = infer(fall,stmt,environment);				
								
				if(fall.source instanceof Value) {
					// This indicates a loop over a constant source set. In such
					// cases, we can unroll the loop in order that it might be
					// completely optimised away.
					Block body = new Block();
					while(++i < block.size()) {
						stmt = block.get(i);
						if(stmt.code instanceof Code.ForallEnd) {
							Code.ForallEnd fend = (Code.ForallEnd) stmt.code;
							if(fend.target.equals(fall.label)){
								// end of loop body found
								break;
							}
						}
						body.add(stmt.code,stmt.attributes());
					}					
					Block blk = unrollFor(fall,body);
					if(i<block.size()) {
						blk.addAll(block.subblock(i+1,block.size()));
					}
					i=-1; 
					block = blk;					
					continue;
				} 
								
				code = fall;
			} else if(code instanceof Debug) {
				code = infer((Code.Debug)code,stmt,environment);
			} 
			
			nblock.add(code, stmt.attributes());
		}
		return nblock;
	}
	
	protected Block unrollFor(Code.Forall fall, Block body) {		
		Block blk = new Block();
		Collection<Value> values;
		if(fall.source instanceof Value.List) {
			Value.List l = (Value.List) fall.source;
			values = l.values;
		} else {
			Value.Set s = (Value.Set) fall.source;
			values = s.values;
		}
		HashMap<String,CExpr> binding = new HashMap<String,CExpr>();
		String var = fall.variable.name();
		for(Value v : values) {
			// first, relabel to avoid conflicts
			Block tmp = Block.relabel(body);
			// second, substitute value
			binding.put(var, v);			
			tmp = Block.substitute(binding,tmp);			
			// finally,add to the target blk
			blk.addAll(tmp);
		}
		return blk;
	}
	
	protected void merge(String target, HashMap<String,Value> env,
			HashMap<String, HashMap<String,Value>> flowsets) {
		
		HashMap<String,Value> e = flowsets.get(target);
		if(e == null) {
			flowsets.put(target, new HashMap<String,Value>(env));
		} else {
			join(e,env);
			flowsets.put(target, e);
		}
	}
	
	protected Code.Forall infer(Code.Forall code, Stmt stmt,
			HashMap<String,Value> environment) {
		
		CExpr src = infer(code.source, stmt, environment);		
		// Ok, could unroll loops here
		return new Code.Forall(code.label, code.variable, src);
	}
	
	protected Code infer(Code.Assign code, Stmt stmt, HashMap<String,Value> environment) {
		CExpr.LVal lhs = code.lhs;
		CExpr rhs = infer(code.rhs,stmt,environment);

		if(lhs instanceof Variable && rhs instanceof Value) {
			Variable v = (Variable) lhs;
			environment.put(v.name, (Value) rhs);			
		} else if(lhs instanceof Register && rhs instanceof Value) {
			Register v = (Register) lhs;
			environment.put("%" + v.index, (Value) rhs);
		} else if(lhs != null) {
			// FIXME: we could do better here, actually. Particularly in the
			// case of tuple accesses. Lists are harder, unless the index is
			// itself a constant.			
			LVar lv = CExpr.extractLVar(lhs);
			// Now, remove the constant we have stored for this variable, since
			// the variables value has changed in some manner that we haven't
			// or can't fully track.
			environment.remove(lv.name());
		}
		
		return new Code.Assign(lhs,rhs);
	}
	
	
	protected Code infer(Code.IfGoto code, Stmt stmt,
			HashMap<String, Value> environment) {
		CExpr lhs = infer(code.lhs, stmt, environment);
		CExpr rhs = infer(code.rhs, stmt, environment);

		if (lhs instanceof Value && rhs instanceof Value) {
			boolean v = Value.evaluate(code.op, (Value) lhs, (Value) rhs);
			if (v) {
				return new Code.Goto(code.target);
			} else {
				return new Code.Skip();
			}
		}

		return new Code.IfGoto(code.op, lhs, rhs, code.target);
	}	
	protected Code infer(Code.Return code, Stmt stmt, HashMap<String,Value> environment,
			Module.Method method) {
		CExpr rhs = code.rhs;
		
		if(rhs != null) {
			Type ret_t = method.type().ret;
			if(ret_t == Type.T_VOID) {
				syntaxError(
						"cannot return value, as method has void return type",
						filename, stmt);
			}
			rhs = infer(rhs,stmt,environment);			
		}
		
		return new Code.Return(rhs);
	}
	
	protected Code infer(Code.Debug code, Stmt stmt, HashMap<String,Value> environment) {
		CExpr rhs = infer(code.rhs,stmt, environment);		
		return new Code.Debug(rhs);
	}
	
	protected CExpr infer(CExpr e, Stmt stmt, HashMap<String,Value> environment) {

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
	
	protected CExpr infer(Variable v, Stmt stmt, HashMap<String,Value> environment) {
		Value val = environment.get(v.name);
		if(val != null) {
			return val;
		} else {
			return v;
		}		
	}
	
	protected CExpr infer(Register v, Stmt stmt, HashMap<String,Value> environment) {
		String name = "%" + v.index;
		Value val = environment.get(name);
		if(val != null) {
			return val;
		} else {
			return v;
		}
	}
	
	protected CExpr infer(UnOp v, Stmt stmt, HashMap<String,Value> environment) {
		CExpr rhs = infer(v.rhs, stmt, environment);
		if(rhs instanceof Value) {
			return Value.evaluate(v.op, (Value) rhs);
		}
		return CExpr.UNOP(v.op, rhs);
	}
	
	protected CExpr infer(BinOp v, Stmt stmt, HashMap<String,Value> environment) {
		CExpr lhs = infer(v.lhs, stmt, environment);
		CExpr rhs = infer(v.rhs, stmt, environment);
		
		if(lhs instanceof Value && rhs instanceof Value) {
			return Value.evaluate(v.op, (Value) lhs, (Value) rhs);
		}
		
		return CExpr.BINOP(v.op,lhs,rhs);				
	}
	
	protected CExpr infer(NaryOp v, Stmt stmt, HashMap<String,Value> environment) {
		ArrayList args = new ArrayList<CExpr>();
		boolean allValues = true;
		for(CExpr arg : v.args) {
			arg = infer(arg,stmt,environment);
			args.add(arg);
			allValues &= arg instanceof Value;
		}
		
		if(allValues) {			
			return Value.evaluate(v.op, args);
		}
		
		return CExpr.NARYOP(v.op, args);		
	}
	
	protected CExpr infer(ListAccess e, Stmt stmt, HashMap<String,Value> environment) {
		CExpr src = infer(e.src,stmt,environment);
		CExpr idx = infer(e.index,stmt,environment);

		if(src instanceof Value.List && idx instanceof Value.Int) {
			Value.List s = (Value.List) src;
			Value.Int i = (Value.Int) idx;
			int v = i.value.intValue();
			if(v < 0 || v >= s.values.size()) {
				syntaxError("list access out-of-bounds",filename,stmt);
			}
			return s.values.get(v);
		}
		
		return CExpr.LISTACCESS(src,idx);
	}
		
	protected CExpr infer(TupleAccess e, Stmt stmt,
			HashMap<String, Value> environment) {
		CExpr lhs = infer(e.lhs, stmt, environment);
		if (lhs instanceof Value.Tuple) {
			Value.Tuple t = (Value.Tuple) lhs;
			Value v = t.values.get(e.field);
			if (v == null) {
				syntaxError("tuple does not have field " + e.field, filename,
						stmt);
			}
			return v;
		}
		return CExpr.TUPLEACCESS(lhs, e.field);
	}
	
	protected CExpr infer(Tuple e, Stmt stmt, HashMap<String,Value> environment) {
		HashMap args = new HashMap();
		boolean allValues = true;
		for (Map.Entry<String, CExpr> v : e.values.entrySet()) {
			CExpr arg = infer(v.getValue(), stmt, environment);
			args.put(v.getKey(), arg);
			allValues &= arg instanceof Value;
		}
		if(allValues) {
			return Value.V_TUPLE(args);
		} else {
			return CExpr.TUPLE(args);
		}
	}
	
	protected CExpr infer(Invoke ivk, Stmt stmt,
			HashMap<String,Value> environment) {		
		ArrayList<CExpr> args = new ArrayList<CExpr>();		
		CExpr receiver = ivk.receiver;		
		if(receiver != null) {
			receiver = infer(receiver, stmt, environment);			
		}
		for (CExpr arg : ivk.args) {
			arg = infer(arg, stmt, environment);
			args.add(arg);
		}
		
		return CExpr.INVOKE(ivk.type, ivk.name, ivk.caseNum, receiver, args);		
	}
		
	public static void join(HashMap<String,Value> env1,
			HashMap<String,Value> env2) {		
		if (env2 == null) { return; }
		HashSet<String> keys = new HashSet<String>(env1.keySet());
		keys.addAll(env2.keySet());
		for (String key : keys) {
			Value mt = env1.get(key);
			Value ot = env2.get(key);
			if (ot instanceof Value && mt instanceof Value && ot.equals(mt)) {
				// ok
			} else {
				env1.remove(key);
			}
		}
	}
}
