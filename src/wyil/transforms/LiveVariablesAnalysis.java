package wyil.transforms;

import java.util.*;

import wyc.stages.BackPropagation.Env;
import wyil.ModuleLoader;
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
	public Env propagate(int index, Entry entry, Env environment) {		
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
		return join(trueEnv,falseEnv);
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
			oldEnv = newEnv != null ? newEnv : environment;
			newEnv = propagate(start+1,end, oldEnv);
			
		} while (!newEnv.equals(oldEnv));
		
		environment = join(environment,newEnv);
		
		if(loop instanceof Code.ForAll) {
			Code.ForAll fall = (Code.ForAll) loop; 											
		} 		
		
		return environment;		
	}
	
	private Env join(Env env1, Env env2) {
		if (env2 == null) {
			return env1;
		} else if (env1 == null) {
			return env2;
		}
		// implements set intersection
		Env r = new Env();
		for(Integer i : env1) {
			if(env2.contains(i)) {
				r.add(i);
			}
		}
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
