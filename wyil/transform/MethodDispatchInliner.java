package wyil.transform;

import java.util.ArrayList;

import wyil.ModuleLoader;
import wyil.lang.*;

/**
 * The purpose of the method dispatch inliner is to inline dispatch choices into
 * call-sites. This offers a useful optimisation in situations when we can
 * statically determine that a subset of cases is the dispatch target.
 * 
 * @author djp
 * 
 */
public class MethodDispatchInliner implements ModuleTransform {
	private final ModuleLoader loader;
	public MethodDispatchInliner(ModuleLoader loader) {
		this.loader = loader;
	}
	public Module apply(Module module) {
		ArrayList<Module.TypeDef> types = new ArrayList<Module.TypeDef>();
		ArrayList<Module.ConstDef> constants = new ArrayList<Module.ConstDef>();
		ArrayList<Module.Method> methods = new ArrayList<Module.Method>();
		System.out.println("DISPATCH INLINE CALLED");
		return module;
	}
}
