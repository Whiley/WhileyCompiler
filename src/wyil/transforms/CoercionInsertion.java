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

public class CoercionInsertion extends ForwardFlowAnalysis<CoercionInsertion.Env> {	
	private static final HashMap<Integer,Block.Entry> insertions = new HashMap<Integer,Block.Entry>();
	
	public CoercionInsertion(ModuleLoader loader) {
		super(loader);
	}
	
	public Module.TypeDef transform(Module.TypeDef type) {		
		return type;		
	}
	
	public Env initialStore() {				
		Env environment = new Env();		
		int nvars = methodCase.locals().size();
		
		for (int i=0; i != nvars; ++i) {			
			environment.add(null);			
		}				
					
		return environment;				
	}
	
	public Module.Case propagate(Module.Case mcase) {		
		methodCase = mcase;
		stores = new HashMap<String,Env>();
		insertions.clear();
		
		Env environment = initialStore();		
		propagate(0,mcase.body().size(), environment);	
		
		// At this point, we apply the inserts
		Block body = mcase.body();
		Block nbody = new Block();		
		for(int i=0;i!=body.size();++i) {
			Block.Entry insertion = insertions.get(i);			
			if(insertion != null) {								
				nbody.add(insertion);				
			} 				
			nbody.add(body.get(i));			
		}
		
		return new Module.Case(nbody,mcase.locals(),mcase.attributes());
	}

	public Env propagate(int index, Entry entry, Env environment) {						
		Code code = entry.code;			
		
		// reset the rewrites for this code, in case it changes
		insertions.remove(index);
		
		environment = (Env) environment.clone();
		
		if(code instanceof Assert) {
			infer((Assert)code,entry,environment);
		} else if(code instanceof BinOp) {
			infer(index,(BinOp)code,entry,environment);
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
			infer(index,(FieldLoad)code,entry,environment);			
		} else if(code instanceof IndirectInvoke) {
			infer((IndirectInvoke)code,entry,environment);
		} else if(code instanceof IndirectSend) {
			infer((IndirectSend)code,entry,environment);
		} else if(code instanceof Invoke) {
			infer((Invoke)code,entry,environment);
		} else if(code instanceof Label) {
			// skip			
		} else if(code instanceof ListOp) {
			infer(index,(ListOp)code,entry,environment);
		} else if(code instanceof ListLoad) {
			infer(index,(ListLoad)code,entry,environment);
		} else if(code instanceof Load) {
			infer(index,(Load)code,entry,environment);
		} else if(code instanceof MultiStore) {
			infer((MultiStore)code,entry,environment);
		} else if(code instanceof NewDict) {
			infer(index,(NewDict)code,entry,environment);
		} else if(code instanceof NewList) {
			infer(index,(NewList)code,entry,environment);
		} else if(code instanceof NewRecord) {
			infer(index,(NewRecord)code,entry,environment);
		} else if(code instanceof NewSet) {
			infer(index,(NewSet)code,entry,environment);
		} else if(code instanceof Return) {
			infer((Return)code,entry,environment);
		} else if(code instanceof Send) {
			infer((Send)code,entry,environment);
		} else if(code instanceof Store) {
			infer((Store)code,entry,environment);
		} else if(code instanceof SetOp) {
			infer(index,(SetOp)code,entry,environment);
		} else if(code instanceof UnOp) {
			infer(index,(UnOp)code,entry,environment);
		} else {
			syntaxError("Need to finish type inference " + code,filename,entry);
			return null;
		}	
		
		return environment;
	}
	
	public void infer(Code.Assert code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(int index, Code.BinOp code, Block.Entry entry,
			Env environment) {
		Type rhs = environment.pop();
		Type lhs = environment.pop();
		environment.push(code.type);
	}
	
	public void infer(Code.Convert code, Block.Entry entry,
			Env environment) {
		
		Type type = environment.pop();
		
		// need to apply conversion here
		
		environment.push(code.to);
	}
	
	public void infer(Code.Const code, Block.Entry entry,
			Env environment) {
		environment.push(code.constant.type());		
	}
	
	public void infer(Code.Debug code, Block.Entry entry,
			Env environment) {
		environment.pop();
	}
	
	public void infer(int index, Code.FieldLoad code, Block.Entry entry,
			Env environment) {
		
		environment.pop();
		environment.push(code.type.fields().get(code.field));				
	}
	
	public void infer(Code.IndirectInvoke code, Block.Entry entry,
			Env environment) {
		
		for(int i=0;i!=code.type.params().size();++i) {
			environment.pop();
		}
		
		environment.pop(); // target
		
		if(code.type.ret() != Type.T_VOID && code.retval) {
			environment.push(code.type.ret());
		}
	}
	
	public void infer(Code.IndirectSend code, Block.Entry entry,
			Env environment) {
		// FIXME: need to do something here
	}
	
	public void infer(Code.Invoke code, Block.Entry entry,
			Env environment) {
		for(int i=0;i!=code.type.params().size();++i) {
			environment.pop();
		}
		
		if(code.type.ret() != Type.T_VOID && code.retval) {
			environment.push(code.type.ret());
		}
	}
	
	public void infer(int index, Code.ListOp code, Block.Entry entry,
			Env environment) {
		
		switch(code.lop) {
			case SUBLIST: {
				Type end = environment.pop();
				Type start = environment.pop();
				Type list = environment.pop();				
				environment.push(list);				
				break;
			}
			case APPEND:
			{				
				Type rhs = environment.pop();
				Type lhs = environment.pop();
				environment.push(code.type);				
				break;
			}
			case LENGTHOF:
			{
				environment.pop();
				environment.push(code.type.element());
				
				break;
			}
		}
	}
	
	public void infer(int index, Code.ListLoad code, Block.Entry entry,
			Env environment) {
		Type idx = environment.pop();
		Type src = environment.pop();					
		environment.push(code.type.element());		
	}
	
	public void infer(int index, Code.Load code, Block.Entry entry,
			Env environment) {
		environment.push(environment.get(code.slot));
	}
	
	public void infer(Code.MultiStore code, Block.Entry stmt,
			Env environment) {
		
		ArrayList<Type> path = new ArrayList();
		Type val = environment.pop();
		for(int i=code.fields.size();i!=code.level;++i) {
			path.add(environment.pop());
		}
		
		Type src = environment.get(code.slot);		
		Type iter = src;
		
		if(code.slot == 0 && Type.isSubtype(Type.T_PROCESS(Type.T_ANY), src)) {
			Type.Process p = (Type.Process) src;
			iter = p.element();
		}
		
		int fi = 0;
		int pi = 0;
		for(int i=0;i!=code.level;++i) {				
			if(Type.isSubtype(Type.T_DICTIONARY(Type.T_ANY, Type.T_ANY),iter)) {			
				// this indicates a dictionary access, rather than a list access			
				Type.Dictionary dict = Type.effectiveDictionaryType(iter);							
				Type idx = path.get(pi++);				
				iter = dict.value();				
			} else if(Type.isSubtype(Type.T_LIST(Type.T_ANY),iter)) {			
				Type.List list = Type.effectiveListType(iter);							
				Type idx = path.get(pi++);
				iter = list.element();
			} else {
				Type.Record rec = Type.effectiveRecordType(iter);				
				String field = code.fields.get(fi++);
				iter = rec.fields().get(field);				
			}
		}
		
		// Now, we need to determine the (potentially) updated type of the
		// variable in question. For example, if we assign a real into a [int]
		// then we'll end up with a [real].
		Type ntype = TypePropagation.typeInference(src,val,code.level,0,code.fields);
		environment.set(code.slot,ntype);
	}
	
	public void infer(int index, Code.NewDict code, Block.Entry entry,
			Env environment) {
		for(int i=0;i!=code.nargs;++i) {
			Type val = environment.pop();
			Type key = environment.pop();			
		}		
		environment.push(code.type);		
	}
	
	public void infer(int index, Code.NewRecord code, Block.Entry entry,
			Env environment) {
		
		for (String key : code.type.keys()) {
			Type val = environment.pop();			
		}
		
		environment.push(code.type);
	}
	
	public void infer(int index, Code.NewList code, Block.Entry entry,
			Env environment) {
		
		for (int i=0;i!=code.nargs;++i) {
			Type val = environment.pop();			
		}		
		
		environment.push(code.type);
	}
	
	public void infer(int index, Code.NewSet code, Block.Entry entry,
			Env environment) {
		
		for (int i=0;i!=code.nargs;++i) {
			Type val = environment.pop();			
		}		
		
		environment.push(code.type);
	}
	
	public void infer(Code.Return code, Block.Entry entry,
			Env environment) {
		if(code.type != Type.T_VOID) {
			environment.pop();
		}
	}
	
	public void infer(Code.Send code, Block.Entry entry,
			Env environment) {

		for(int i=0;i!=code.type.params().size();++i) {
			environment.pop();
		}
		
		environment.pop(); // receiver
		
		if (code.type.ret() != Type.T_VOID && code.synchronous && code.retval) {
			environment.push(null);
		}
	}
	
	public void infer(Code.Store code, Block.Entry entry,
			Env environment) {
		environment.set(code.slot, environment.pop());
	}
	
	public void infer(int index, Code.SetOp code, Block.Entry entry,
			Env environment) {
		Type rhs = environment.pop();
		Type lhs = environment.pop();
		Type result = null;

		switch (code.sop) {
		case UNION:
		case DIFFERENCE:
		case INTERSECT: {
			if (code.dir == OpDir.UNIFORM) {
				// nout
			} else if (code.dir == OpDir.LEFT) {
				rhs = Type.T_SET(rhs);
			} else {
				lhs = Type.T_SET(lhs);
			}
			result = Type.leastUpperBound(lhs, rhs);
		}
		}

		environment.push(result);
	}
	
	public void infer(int index, Code.UnOp code, Block.Entry entry,
			Env environment) {
		Type val = environment.pop();
		
		switch(code.uop) {
			case NEG:
			{
				environment.push(val); 
			}
			break;
			case PROCESSACCESS:
			{
				Type.Process tp = (Type.Process)val; 
				environment.push(tp.element());
				break;
			}
			case PROCESSSPAWN:
			{
				environment.push(Type.T_PROCESS(val));
				break;
			}					
		}		
	}
	
	public Pair<Env, Env> propagate(int index,
			Code.IfGoto igoto, Entry stmt, Env environment) {
		
		Type rhs = environment.pop();
		Type lhs = environment.pop();
		
		return new Pair(environment, environment);
	}
	
	public Pair<Env, Env> propagate(int index,
			Code.IfType code, Entry stmt, Env environment) {
		
		if(code.slot < 0) {			
			Type lhs = environment.pop();			
		} 
		
		return new Pair(environment, environment);
	}
	
	public List<Env> propagate(int index, Code.Switch sw,
			Entry stmt, Env environment) {
		
		Type val = environment.pop();
		
		ArrayList<Env> stores = new ArrayList();
		for (int i = 0; i != sw.branches.size(); ++i) {
			stores.add(environment);
		}
		
		return stores;
	}
		
	public Env propagate(int start, int end, Code.Loop loop,
			Entry stmt, Env environment) {
		
		if(loop instanceof Code.ForAll) {
			Code.ForAll fall = (Code.ForAll) loop; 
			environment.pop();		
			
			// TO DO: could unroll loop if src collection is a value.
			
			environment.set(fall.slot,null);
		} 
		
		// Now, kill every variable which is modified in the loop. This is a
		// safety precaution, and it's possible we could do better here in some
		// circumstances (e.g. by unrolling the loop).
		
		for(int slot : loop.modifies) {
			environment.set(slot,null);
		}
		
		Env oldEnv = null;
		Env newEnv = null;
		
		do {			
			// iterate until a fixed point reached
			oldEnv = newEnv != null ? newEnv : environment;
			newEnv = propagate(start+1,end, oldEnv);
			
		} while (!newEnv.equals(oldEnv));

		return join(environment,newEnv);		
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
