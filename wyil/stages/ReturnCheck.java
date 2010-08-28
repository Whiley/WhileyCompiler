package wyil.stages;

import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import static wyil.util.SyntaxError.*;

public class ReturnCheck implements ModuleTransform {
	private final ModuleLoader loader;
	private String filename;

	public ReturnCheck(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public Module apply(Module module) {
		filename = module.filename();
		
		for(Module.Method method : module.methods()) {
			check(method);
		}
		return module;
	}
		
	public void check(Module.Method method) {		
		for (Module.Case c : method.cases()) {
			check(c.body(),method);
		}		
	}
	
	protected void check(Block block,  Module.Method method) {		
		// The reachables set contains those labels which are known to be
		// definitely reachable.
		HashSet<String> reachables = new HashSet<String>();
		boolean lastReturn = false;
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			Code code = stmt.code;
			if(code instanceof Return) {
				Return r = (Return) code;
				lastReturn = true;
				
			} else {
				lastReturn = false;
			}
			
		}	
	
		
	}
}
