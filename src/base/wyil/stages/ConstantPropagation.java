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

import java.math.BigInteger;
import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.lang.CExpr.*;
import wyil.util.*;
import wyil.util.dfa.ForwardFlowAnalysis;

public class ConstantPropagation extends ForwardFlowAnalysis<HashMap<String,Value>> {	
	public ConstantPropagation(ModuleLoader loader) {
		super(loader);
	}
	
	public Module.TypeDef transform(Module.TypeDef type) {		
		return type;		
	}
	
	public HashMap<String,Value> initialStore() {		
		return new HashMap<String,Value>();
				
	}
	
	protected Pair<Block, HashMap<String, Value>> propagate(Code.Start start,
			Code.End end, Block body, Stmt stmt, HashMap<String, Value> environment) {
					
		if(start instanceof Forall) {
			Code.Forall fall = (Code.Forall) start;
			CExpr source = infer(fall.source,stmt,environment);
			// Determine and eliminate loop-carried dependencies
			environment = new HashMap<String,Value>(environment);
			for(LVar v : fall.modifies) {
				environment.remove(v.name());
			}
			// Propagate constants into invariant
			Block invariant = fall.invariant;
			if(invariant != null) {
				invariant = propagate(invariant,environment).first();
			}
			
			// Update code
			fall = new Code.Forall(fall.label, invariant, fall.variable, source, fall.modifies);
			start = fall;
			
			// Unroll loop if possible
			if(source instanceof Value) {
				// ok, we can unroll the loop body --- yay!
				body = unrollFor(fall,body);
				return propagate(body,environment);
			}						
		} else if(start instanceof Loop) {
			Loop loop = (Loop) start;
			
			// Determine and eliminate loop-carried dependencies
			environment = new HashMap<String,Value>(environment);
			for(LVar v : loop.modifies) {
				environment.remove(v.name());
			}
			
			// Propagate constants into invariant
			Block invariant = loop.invariant;
			if(invariant != null) {
				invariant = propagate(invariant,environment).first();
			}
			
			// Update code
			start = new Code.Loop(start.label, invariant, loop.modifies);
			
			// Can we unroll the loop here I wonder?
		}
		
		Pair<Block,HashMap<String,Value>> r = propagate(body,environment);
		Block blk = new Block();
		blk.add(start);
		blk.addAll(r.first());
		blk.add(end);
		
		if(start instanceof Loop) { 
			return new Pair(blk,join(environment,r.second()));
		} else {
			return new Pair(blk,r.second());
		}
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
	
	protected Pair<Stmt,HashMap<String,Value>> propagate(Stmt stmt, HashMap<String,Value> environment) {
		
		
		Code code = stmt.code;
		if(stmt.code instanceof Code.Assign) {
			return propagate((Code.Assign)stmt.code,stmt,environment);
		} else if(stmt.code instanceof Code.Debug) {
			code = propagate((Code.Debug)stmt.code,stmt,environment);
		} else if(stmt.code instanceof Code.Return) {
			code = propagate((Code.Return)stmt.code,stmt,environment);
		}
		
		return new Pair(new Stmt(code,stmt.attributes()),environment);
	}
	
	protected Pair<Stmt,HashMap<String,Value>> propagate(Code.Assign code, Stmt stmt, HashMap<String,Value> environment) {
		CExpr.LVal lhs = code.lhs;
		CExpr rhs = infer(code.rhs,stmt,environment);

		if(lhs instanceof Variable && rhs instanceof Value) {
			Variable v = (Variable) lhs;
			environment = new HashMap<String,Value>(environment);
			environment.put(v.name, (Value) rhs);			
		} else if(lhs instanceof Register && rhs instanceof Value) {
			Register v = (Register) lhs;
			environment = new HashMap<String,Value>(environment);
			environment.put("%" + v.index, (Value) rhs);
		} else if(lhs != null) {
			// FIXME: we could do better here, actually. Particularly in the
			// case of tuple accesses. Lists are harder, unless the index is
			// itself a constant.			
			LVar lv = CExpr.extractLVar(lhs);
			environment = new HashMap<String,Value>(environment);
			// Now, remove the constant we have stored for this variable, since
			// the variables value has changed in some manner that we haven't
			// or can't fully track.
			environment.remove(lv.name());
		}
		
		stmt = new Stmt(new Code.Assign(lhs,rhs),stmt.attributes());
		return new Pair(stmt,environment);
	}
	
	protected Code propagate(Code.Return code, Stmt stmt, HashMap<String,Value> environment) {
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
	
	protected Code propagate(Code.Debug code, Stmt stmt, HashMap<String,Value> environment) {
		CExpr rhs = infer(code.rhs,stmt, environment);		
		return new Code.Debug(rhs);
	}
	

	protected Triple<Stmt, HashMap<String, Value>, HashMap<String, Value>> propagate(
			Code.IfGoto code, Stmt stmt, HashMap<String, Value> environment) {
		CExpr lhs = infer(code.lhs, stmt, environment);
		CExpr rhs = infer(code.rhs, stmt, environment);
		Code ncode;
		
		if (lhs instanceof Value && rhs instanceof Value) {
			boolean v = Value.evaluate(code.op, (Value) lhs, (Value) rhs);
			if (v) {
				ncode = new Code.Goto(code.target);
			} else {
				ncode = new Code.Skip();
			}
		} else {
			ncode = new Code.IfGoto(code.op, lhs, rhs, code.target); 
		}

		stmt = new Stmt(ncode,stmt.attributes());
		return new Triple(stmt,environment,environment);
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
		
	protected CExpr infer(RecordAccess e, Stmt stmt,
			HashMap<String, Value> environment) {
		CExpr lhs = infer(e.lhs, stmt, environment);
		if (lhs instanceof Value.Record) {
			Value.Record t = (Value.Record) lhs;
			Value v = t.values.get(e.field);
			if (v == null) {
				syntaxError("tuple does not have field " + e.field, filename,
						stmt);
			}
			return v;
		}
		return CExpr.RECORDACCESS(lhs, e.field);
	}
	
	protected CExpr infer(Record e, Stmt stmt, HashMap<String,Value> environment) {
		HashMap args = new HashMap();
		boolean allValues = true;
		for (Map.Entry<String, CExpr> v : e.values.entrySet()) {
			CExpr arg = infer(v.getValue(), stmt, environment);
			args.put(v.getKey(), arg);
			allValues &= arg instanceof Value;
		}
		if(allValues) {
			return Value.V_RECORD(args);
		} else {
			return CExpr.RECORD(args);
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
		
	public HashMap<String, Value> join(HashMap<String, Value> env1,
			HashMap<String, Value> env2) {
		if(env1 == null) {
			return env2;
		} else if(env2 == null) {
			return env1;
		}
		HashMap<String, Value> r = new HashMap<String, Value>();
		HashSet<String> keys = new HashSet<String>(env1.keySet());
		keys.addAll(env2.keySet());
		for (String key : keys) {
			Value mt = env1.get(key);
			Value ot = env2.get(key);
			if (ot instanceof Value && mt instanceof Value && ot.equals(mt)) {
				r.put(key, ot);
			}
		}
		return r;
	}
}
