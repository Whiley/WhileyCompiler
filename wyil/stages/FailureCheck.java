package wyil.stages;

import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import static wyil.util.SyntaxError.*;

public class FailureCheck implements ModuleTransform {
	private final ModuleLoader loader;
	private String filename;

	public FailureCheck(ModuleLoader loader) {
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
		boolean inCheck = false;
		boolean reachable = true;
		boolean runtimeChecks = false;
		
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			Code code = stmt.code;

			if(code instanceof Check) {
				reachables.clear();
				inCheck = true;
				reachable = true;
			} else if(code instanceof CheckEnd) {
				inCheck = false;
			} else if (code instanceof Label) {
				Label label = (Label) code;
				reachable = reachable || reachables.contains(label.label);
			} 
				
			if (code instanceof Fail) {
				Fail f = (Fail) code;
				if(reachable) {
					syntaxError(f.msg,filename,stmt);
				} else {
					runtimeChecks = true;
				}
			} else if (!inCheck || (inCheck && !reachable)) {
				continue; 
			} else if (code instanceof Goto) {
				Goto got = (Goto) code;
				reachables.add(got.target);
				reachable = false;
			} else if (code instanceof IfGoto) {
				// note, we could do better here, by considering dominators or
				// similar
				reachable = false;				
			} else if (code instanceof Assign) {
				
			} 		
		}	
		
		if (runtimeChecks) {
			// would be nice to do better than this by actually reporting line
			// numbers, etc
			System.err.println("*** WARNING: runtime check(s) in "
					+ filename + ", " + method.name());
		}
	}
}
