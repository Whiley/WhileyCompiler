package wyjc.compiler;

import wyil.lang.Module;
import wyil.transform.ModuleTransform;
import wyil.util.Logger;

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
			logout.logTimedMessage("[" + module.filename() + "] applied transform " + name,
					System.currentTimeMillis() - start);
			return transform.apply(module);
		} catch(Throwable ex) {
			logout.logTimedMessage("[" + module.filename()
					+ "] transform " + name + " failed (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start);
			return null;
		}
	}
}
