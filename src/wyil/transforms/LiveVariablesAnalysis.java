package wyil.transforms;

import java.util.*;


import static wyil.util.SyntaxError.*;
import static wyil.util.ErrorMessages.*;

import wyc.stages.BackPropagation.Env;
import wyil.ModuleLoader;
import wyil.lang.Block;
import wyil.lang.Code;
import wyil.lang.Module;
import wyil.lang.Type;
import wyil.lang.Block.Entry;
import wyil.util.dfa.*;

/**
 * <p>
 * Implements a classic live variables analysis to enable efficient reference
 * counting. Compound structures in Whiley (e.g. lists, sets, records, etc) are
 * <i>reference counted</i>. This means we count the number of aliases to them
 * in the heap and on the stack. Then, when a compound structure is updated, we
 * can perform an <i>inplace update</i> if the reference count is 1; otherwise,
 * we have to clone the structure. Cloning is potentially expensive, and we want
 * to avoid it as much as possible. Therefore, as soon as a variable is no
 * longer live, we should decrement its reference count. This is signalled using
 * the special <code>void</code> bytecode which, literally, "voids" a given
 * register thereby allowing us to release it.
 * </p>
 * <p>
 * The purpose of this class is to determine when a variable is live, and when
 * it is not. The live variables analysis operates as a backwards analysis,
 * propagating information from variable uses (i.e. <code>load</code> bytecodes)
 * back to their definitions (i.e. <code>store</code> bytecodes). For more
 * information on Live Variables Analysis, see <a
 * href="http://en.wikipedia.org/wiki/Live_variable_analysis">the Wikipedia
 * page</a>.
 * </p>
 * 
 * @author David J. Pearce, 2011
 * 
 */
public class LiveVariablesAnalysis extends BackwardFlowAnalysis<LiveVariablesAnalysis.Env>{
	private static final HashMap<Integer,Block.Entry> afterInserts = new HashMap<Integer,Block.Entry>();
	private static final HashMap<Integer,Block.Entry> rewrites = new HashMap<Integer,Block.Entry>();
	private static final HashSet<Integer> deadcode = new HashSet<Integer>();
	
	public LiveVariablesAnalysis(ModuleLoader loader) {
		super(loader);
	}	
	
	public Module.TypeDef transform(Module.TypeDef type) {		
		// TODO: back propagate through type constraints
		return type;		
	}
	
	/**
	 * Last store for the live variables analysis is empty, because all
	 * variables are considered to be dead at the end of a method/function.
	 * 
	 * @return
	 */
	@Override
	public Env lastStore() { return EMPTY_ENV; }
	
	@Override
	public Module.Case propagate(Module.Case mcase) {		

		// TODO: back propagate through pre- and post-conditions
		
		methodCase = mcase;
		stores = new HashMap<String,Env>();
		afterInserts.clear();
		rewrites.clear();
		deadcode.clear();
		Block body = mcase.body();
		Env environment = lastStore();		
		propagate(0,body.size(), environment);	
		
		// First, check and report any dead-code
		for(Integer i : deadcode) {
			syntaxError(errorMessage(DEAD_CODE),
					filename, body.get(i));		
		}
		
		// At this point, we apply the inserts	
		Block nbody = new Block(body.numInputs());		
		for(int i=0;i!=body.size();++i) {
			Block.Entry rewrite = rewrites.get(i);			
			if(rewrite != null) {								
				nbody.append(rewrite);				
			} else {
				nbody.append(body.get(i));
			}
			Block.Entry afters = afterInserts.get(i);			
			if(afters != null) {								
				nbody.append(afters);				
			} 							
		}
		
		return new Module.Case(nbody, mcase.precondition(),
				mcase.postcondition(), mcase.locals(), mcase.attributes());
	}
	
	@Override
	public Env propagate(int index, Entry entry, Env environment) {		
		Code code = entry.code;
		if(code instanceof Code.Load) {
			Code.Load load = (Code.Load) code;
			if(!environment.contains(load.slot)) {
				rewrites.put(
						index,
						new Block.Entry(Code.Move(load.type, load.slot), entry
								.attributes()));				
			} else {
				rewrites.put(index, null);
			}
			environment = new Env(environment);
			environment.add(load.slot);
		} else if(code instanceof Code.Store) {
			Code.Store store = (Code.Store) code;
			environment = new Env(environment);
			environment.remove(store.slot);
		} else if(code instanceof Code.Update) {
			Code.Update update = (Code.Update) code;
			if(update.beforeType instanceof Type.Process) {
				// updating a field on this constitutes a use
				environment = new Env(environment);
				environment.add(update.slot);
			} else if(!environment.contains(update.slot)) {
				deadcode.add(index);					
			} else {
				deadcode.remove(index);
			}
		} 
		
		return environment;
	}
	
	@Override
	public Env propagate(int index,
			Code.IfGoto igoto, Entry stmt, Env trueEnv, Env falseEnv) {
		return join(trueEnv,falseEnv);
	}
	
	@Override
	public Env propagate(int index,
			Code.IfType code, Entry stmt, Env trueEnv, Env falseEnv) {
		Env r = join(trueEnv,falseEnv);
		
		if(code.slot >= 0) {
			r.add(code.slot);
		}
		
		return r;
	}
	
	@Override
	public Env propagate(int index, Code.Switch sw,
			Entry stmt, List<Env> environments, Env defEnv) {
		Env environment = defEnv;
		
		for(int i=0;i!=sw.branches.size();++i) {
			environment = join(environment,environments.get(i));
		} 		
		
		return environment;
	}
	
	@Override
	public Env propagate(int index, Code.TryCatch sw, Entry stmt,
			List<Env> environments, Env defEnv) {
		// this is of course completely broken.

		Env environment = defEnv;

		for (int i = 0; i != sw.catches.size(); ++i) {
			Env catchEnvironment = (Env) environments.get(i).clone();
			environment = join(environment, catchEnvironment);
		}

		return environment;
	}
	
	public Env propagate(int start, int end, Code.Loop loop,
			Entry stmt, Env environment) {

		environment = new Env(environment); 

		Env oldEnv = null;
		Env newEnv = null;
		
		do {			
			// iterate until a fixed point reached
			oldEnv = newEnv != null ? newEnv : EMPTY_ENV;
			newEnv = propagate(start+1,end, oldEnv);
			
		} while (!newEnv.equals(oldEnv));
		
		environment = join(environment,newEnv);
		
		if(loop instanceof Code.ForAll) {
			Code.ForAll fall = (Code.ForAll) loop; 		
			// FIXME: is the following really necessary?
			environment.remove(fall.slot);
		} 		
		
		return environment;		
	}
	
	private Env join(Env env1, Env env2) {		
		// implements set union
		Env r = new Env(env1);
		r.addAll(env2);
		return r;
	}
	
	private static final Env EMPTY_ENV = new Env();
	
	public static final class Env extends HashSet<Integer> {
		public Env() {
			super();
		}
		
		public Env(Env env) {
			super(env);
		}
	}
}
