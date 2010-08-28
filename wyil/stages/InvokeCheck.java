package wyil.stages;

import static wyil.util.SyntaxError.syntaxError;

import java.util.ArrayList;

import wyil.lang.Block;
import wyil.lang.CExpr;
import wyil.lang.Code;
import wyil.lang.Module;
import wyil.lang.Stmt;
import wyil.lang.Code.IfGoto;

public class InvokeCheck implements ModuleTransform {
	private String filename;

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
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			Code code = stmt.code;

			if (code instanceof IfGoto) {
				System.out.println("MATCHED: " + code);
				ArrayList<CExpr.Invoke> matches = new ArrayList<CExpr.Invoke>();
				Code.match(code, CExpr.Invoke.class, matches);
				for (CExpr.Invoke ivk : matches) {
					System.out.println("TESTING: " + ivk);
					if (ivk.receiver != null) {
						syntaxError("Cannot send message from condition",
								filename, stmt);
					}
				}
			}
		}	
	}
}
