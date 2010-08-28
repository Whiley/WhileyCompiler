package wyil.stages;

import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import static wyil.util.SyntaxError.*;

public class FunctionCheck implements ModuleTransform {
	private final ModuleLoader loader;
	private String filename;

	public FunctionCheck(ModuleLoader loader) {
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
		if (method.type().receiver == null) {
			for (Module.Case c : method.cases()) {
				check(c.body(), method);
			}
		}
	}
	
	protected void check(Block block,  Module.Method method) {		
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			Code code = stmt.code;
			
			// Check for message sends
			ArrayList<CExpr.Invoke> ivks = new ArrayList<CExpr.Invoke>();
			Code.match(code,CExpr.Invoke.class,ivks);
			for(CExpr.Invoke ivk : ivks) {
				if(ivk.receiver != null) {
					syntaxError("cannot send message from function",filename,stmt);
				}
			}
			
			// Check for spawns and process accesses
			ArrayList<CExpr.UnOp> uops = new ArrayList<CExpr.UnOp>();
			Code.match(code,CExpr.UnOp.class,uops);
			for(CExpr.UnOp uop : uops) {
				if(uop.op == CExpr.UOP.PROCESSSPAWN) {
					syntaxError("cannot spawn process from function",filename,stmt);
				}
				if(uop.op == CExpr.UOP.PROCESSACCESS) {
					syntaxError("cannot access process from function",filename,stmt);
				}
			}
		}	
	}
}
