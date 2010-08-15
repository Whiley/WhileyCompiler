package wyjc.compiler;

import wyil.lang.Module;
import wyil.check.ModuleCheck;
import wyil.util.Logger;

public class WyilCheck implements Compiler.Stage {
	private String name;
	private ModuleCheck check;
	
	public WyilCheck(String name, ModuleCheck check) {
		this.name = name;
		this.check = check;
	}
	
	public Module process(Module module, Logger logout) {
		long start = System.currentTimeMillis();
			
		try {
			logout.logTimedMessage("[" + module.filename() + "] performed " + name,
					System.currentTimeMillis() - start);
			check.check(module);
			return module;
		} catch(RuntimeException ex) {
			logout.logTimedMessage("[" + module.filename()
					+ "] failed on " + name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start);			
			throw ex;			
		}
	}
}
