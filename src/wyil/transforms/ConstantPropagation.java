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

import java.math.BigInteger;
import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Block.Entry;
import wyil.lang.Code.*;
import wyil.transforms.TypePropagation.Env;
import wyil.util.*;
import wyil.util.dfa.ForwardFlowAnalysis;
import wyjc.runtime.BigRational;

public class ConstantPropagation extends ForwardFlowAnalysis<ConstantPropagation.Env> {	
	public ConstantPropagation(ModuleLoader loader) {
		super(loader);
	}
	
	public Module.TypeDef transform(Module.TypeDef type) {		
		return type;		
	}
	
	public Env initialStore() {		
		Env environment = new Env();		

		if(method.type().receiver() != null) {					
			environment.add(null);
		}
		List<Type> paramTypes = method.type().params();
		
		for (int i=0; i != paramTypes.size(); ++i) {			
			environment.add(null);			
		}				
				
		return environment;				
	}
	
	/*
	
	protected Block unrollFor(Code.ForAll fall, Block body) {		
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
	
	*/
	

	public Env propagate(int idx, Entry entry, Env environment) {						
		Code code = entry.code;			
		
		environment = (Env) environment.clone();
		
		if(code instanceof Assert) {
			infer((Assert)code,entry,environment);
		} else if(code instanceof BinOp) {
			infer((BinOp)code,entry,environment);
		} else if(code instanceof Convert) {
			infer((Convert)code,entry,environment);
		} else if(code instanceof Const) {
			infer((Const)code,entry,environment);
		} else if(code instanceof Debug) {
			infer((Debug)code,entry,environment);
		} else if(code instanceof ExternJvm) {
			// skip
		} else if(code instanceof Fail) {
			// skip
		} else if(code instanceof FieldLoad) {
			infer((FieldLoad)code,entry,environment);			
		} else if(code instanceof IndirectInvoke) {
			infer((IndirectInvoke)code,entry,environment);
		} else if(code instanceof IndirectSend) {
			infer((IndirectSend)code,entry,environment);
		} else if(code instanceof Invoke) {
			infer((Invoke)code,entry,environment);
		} else if(code instanceof Label) {
			// skip			
		} else if(code instanceof ListOp) {
			infer((ListOp)code,entry,environment);
		} else if(code instanceof ListLoad) {
			infer((ListLoad)code,entry,environment);
		} else if(code instanceof Load) {
			infer((Load)code,entry,environment);
		} else if(code instanceof MultiStore) {
			infer((MultiStore)code,entry,environment);
		} else if(code instanceof NewDict) {
			infer((NewDict)code,entry,environment);
		} else if(code instanceof NewList) {
			infer((NewList)code,entry,environment);
		} else if(code instanceof NewRecord) {
			infer((NewRecord)code,entry,environment);
		} else if(code instanceof NewSet) {
			infer((NewSet)code,entry,environment);
		} else if(code instanceof Return) {
			infer((Return)code,entry,environment);
		} else if(code instanceof Send) {
			infer((Send)code,entry,environment);
		} else if(code instanceof Store) {
			infer((Store)code,entry,environment);
		} else if(code instanceof SetOp) {
			infer((SetOp)code,entry,environment);
		} else if(code instanceof UnOp) {
			infer((UnOp)code,entry,environment);
		} else {
			syntaxError("Need to finish type inference " + code,filename,entry);
			return null;
		}	
		
		return environment;
	}
	
	public void infer(Code.Assert code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.BinOp code, Block.Entry entry,
			Env environment) {
		Value rhs = environment.pop();
		Value lhs = environment.pop();
		
		// should evaluate here
		Value result = null;
		
		environment.push(result);
	}
	
	public void infer(Code.Convert code, Block.Entry entry,
			Env environment) {
		Value val = environment.pop();
		
		// need to apply conversion here
		
		environment.push(null);
	}
	
	public void infer(Code.Const code, Block.Entry entry,
			Env environment) {
		environment.push(code.constant);
	}
	
	public void infer(Code.Debug code, Block.Entry entry,
			Env environment) {
		environment.pop();
	}
	
	public void infer(Code.FieldLoad code, Block.Entry entry,
			Env environment) {
		Value src = environment.pop();
		if(src instanceof Value.Record) {
			Value.Record rec = (Value.Record) src;
			environment.push(rec.values.get(code.field));
		} else {
			environment.push(null);
		}
	}
	
	public void infer(Code.IndirectInvoke code, Block.Entry entry,
			Env environment) {
		
		// TODO: in principle we can do better here in the case that the target
		// is a constant. This seems pretty unlikely though ...
		
		for(int i=0;i!=code.type.params().size();++i) {
			environment.pop();
		}
		
		environment.pop(); // target
		
		if(code.type.ret() != Type.T_VOID && code.retval) {
			environment.push(null);
		}
	}
	
	public void infer(Code.IndirectSend code, Block.Entry entry,
			Env environment) {
		// FIXME: need to do something here
	}
	
	public void infer(Code.Invoke code, Block.Entry entry,
			Env environment) {
		
		// TODO: in the case of a function call (rather than an internal message
		// send), we could potentially evaluate the function in question to give
		// a constant value.
		
		for(int i=0;i!=code.type.params().size();++i) {
			environment.pop();
		}
		
		if(code.type.ret() != Type.T_VOID && code.retval) {
			environment.push(null);
		}
	}
	
	public void infer(Code.ListOp code, Block.Entry entry,
			Env environment) {
		
		switch(code.lop) {
			case SUBLIST: {
				Value end = environment.pop();
				Value start = environment.pop();
				Value list = environment.pop();
				if (list instanceof Value.List && start instanceof Value.Number
						&& end instanceof Value.Number) {
					Value.Number en = (Value.Number) end;
					Value.Number st = (Value.Number) start;
					if (en.value.isInteger() && st.value.isInteger()) {
						Value.List li = (Value.List) list;
						int eni = en.value.intValue();
						int sti = st.value.intValue();
						if (BigRational.valueOf(eni).equals(en.value)
								&& eni >= 0 && eni <= li.values.size()
								&& BigRational.valueOf(sti).equals(st.value)
								&& sti >= 0 && sti <= li.values.size()) {
							ArrayList<Value> nvals = new ArrayList<Value>();
							for (int i = sti; i < eni; ++i) {
								nvals.add(li.values.get(i));
							}
							environment.push(Value.V_LIST(nvals));
						}
					}
				}
				environment.push(null);
				break;
			}
			case APPEND:
			{
				Value lhs = environment.pop();
				Value rhs = environment.pop();
				if(code.dir == OpDir.UNIFORM && lhs instanceof Value.List && rhs instanceof Value.List) {
					Value.List left = (Value.List) lhs;
					Value.List right = (Value.List) rhs;
					ArrayList<Value> values = new ArrayList<Value>(left.values);
					values.addAll(right.values);
					environment.push(Value.V_LIST(values));
				} else if(code.dir == OpDir.LEFT && lhs instanceof Value.List && rhs instanceof Value) {
					Value.List left = (Value.List) lhs;
					Value right = (Value) rhs;
					ArrayList<Value> values = new ArrayList<Value>(left.values);
					values.add(right);
					environment.push(Value.V_LIST(values));
				} else if(code.dir == OpDir.RIGHT && lhs instanceof Value && rhs instanceof Value.List) {
					Value left = (Value) lhs;
					Value.List right = (Value.List) rhs;
					ArrayList<Value> values = new ArrayList<Value>();
					values.add(left);
					values.addAll(right.values);
					environment.push(Value.V_LIST(values));
				} else {
					environment.push(null);
				}
				break;
			}
			case LENGTHOF:
			{
				Value val = environment.pop();
				if(val instanceof Value.List) {
					Value.List list = (Value.List) val;
					environment.push(Value.V_NUMBER(BigRational
							.valueOf(list.values.size())));
				} else {
					environment.push(null);
				}
				break;
			}
		}
	}
	
	public void infer(Code.ListLoad code, Block.Entry entry,
			Env environment) {
		Value idx = environment.pop();
		Value src = environment.pop();
		if (idx instanceof Value.Number && src instanceof Value.List) {
			Value.Number num = (Value.Number) idx;
			Value.List list = (Value.List) src;
			if(num.value.isInteger()) {
				int i = num.value.intValue();
				if (BigRational.valueOf(i).equals(num.value) && i >= 0
						&& i < list.values.size()) {
					environment.push(list.values.get(i));
					return;
				}
			}			
		} 
		
		environment.push(null);		
	}
	
	public void infer(Code.Load code, Block.Entry entry,
			Env environment) {
		environment.push(environment.get(code.slot));
	}
	
	public void infer(Code.MultiStore code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.NewDict code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.NewRecord code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.NewList code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.NewSet code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.Return code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.Send code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.Store code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.SetOp code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.UnOp code, Block.Entry entry,
			Env environment) {
		
	}
	
	public Pair<Env, Env> propagate(int index,
			Code.IfGoto igoto, Entry stmt, Env in) {
		
		return new Pair(in, in);
	}
	
	public Pair<Env, Env> propagate(int index,
			Code.IfType iftype, Entry stmt, Env in) {
		
		return new Pair(in,in);
	}
	
	public List<Env> propagate(int index, Code.Switch sw,
			Entry stmt, Env in) {
		ArrayList<Env> stores = new ArrayList();
		for (int i = 0; i != sw.branches.size(); ++i) {
			stores.add(in);
		}
		return stores;
	}
		
	public Env propagate(int start, int end, Code.Loop loop,
			Entry stmt, Env in) {
		
		if(loop instanceof Code.ForAll) {
			
		} 
		
		Env r = propagate(start+1,end,in);
		return join(in,r);		
	}
	
	public Env join(Env env1, Env env2) {
		if (env2 == null) {
			return env1;
		} else if (env1 == null) {
			return env2;
		}
		Env env = new Env();
		for (int i = 0; i != Math.min(env1.size(), env2.size()); ++i) {
			Value mt = env1.get(i);
			Value ot = env2.get(i);
			if (ot instanceof Value && mt instanceof Value && ot.equals(mt)) {
				env.add(mt);
			} else {
				env.add(null);
			}			
		}

		return env;
	}	
	
	public static class Env extends ArrayList<Value> {
		public Env() {
		}
		public Env(Collection<Value> v) {
			super(v);
		}
		public void push(Value t) {
			add(t);
		}
		public Value top() {
			return get(size()-1);
		}
		public Value pop() {
			return remove(size()-1);			
		}
		public Env clone() {
			return new Env(this);
		}
	}
}
