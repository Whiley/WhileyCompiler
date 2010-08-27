package wyil.stages;

import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import static wyil.util.SyntaxError.*;

public class FailureChecker implements ModuleTransform {
	private final ModuleLoader loader;
	private String filename;

	public FailureChecker(ModuleLoader loader) {
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
		boolean reachable = true;

		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			Code code = stmt.code;

			if (code instanceof Label) {
				Label label = (Label) code;
				reachable = reachables.contains(label.label);
			}

			if (!reachable) {
				continue; // this potentially dead-code
			} else if (code instanceof Goto) {
				Goto got = (Goto) code;
				reachables.add(got.target);
				reachable = false;
			} else if (code instanceof IfGoto) {
				IfGoto igot = (IfGoto) code;
				reachables.add(igot.target);				
			} else if (code instanceof Assign) {
				
			} else if (code instanceof Fail) {
				// If we get here, then there is a path that is definitely
				// executable.  Hence, there is an error in the code.
				Fail f = (Fail) code;
				syntaxError(f.msg,filename,stmt);
			} 			
		}		
	}
}
