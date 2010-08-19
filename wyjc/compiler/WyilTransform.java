package wyjc.compiler;

import wyil.lang.Module;
import wyil.util.Logger;
import wyil.util.transform.ModuleTransform;

public class WyilTransform implements Compiler.Stage {
	private String name;
	private ModuleTransform transform;
	
	public WyilTransform(String name, ModuleTransform transform) {
		this.name = name;
		this.transform = transform;
	}
	
	public Module process(Module module, Logger logout) {
		long start = System.currentTimeMillis();
			
		try {
			logout.logTimedMessage("[" + module.filename() + "] applied " + name,
					System.currentTimeMillis() - start);
			return transform.apply(module);
		} catch(RuntimeException ex) {
			logout.logTimedMessage("[" + module.filename()
					+ "] failed on " + name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start);			
			throw ex;			
		}
	}
}
