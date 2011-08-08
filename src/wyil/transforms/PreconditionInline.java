package wyil.transforms;

import java.util.ArrayList;

import wyil.*;
import wyil.lang.Module;

/**
 * The purpose of this transform is two-fold:
 * <ol>
 * <li>To inline dispatch choices into call-sites. This offers a useful optimisation in situations when we can
 * statically determine that a subset of cases is the dispatch target.</li>
 * <li>To inline preconditions for division and list access expressions</li>
 * </ol>
 * 
 * @author djp
 * 
 */
public class PreconditionInline implements Transform {
	private final ModuleLoader loader;
	private int regTarget;
	private String filename;
	
	public PreconditionInline(ModuleLoader loader) {
		this.loader = loader;
	}
	public Module apply(Module module) {
		ArrayList<Module.TypeDef> types = new ArrayList<Module.TypeDef>();		
		ArrayList<Module.Method> methods = new ArrayList<Module.Method>();
		
		this.filename = module.filename();
		
		for(Module.TypeDef type : module.types()) {
			types.add(transform(type));
		}		
		for(Module.Method method : module.methods()) {
			methods.add(transform(method));
		}
		return new Module(module.id(), module.filename(), methods, types,
				module.constants());
	}
	
	public Module.TypeDef transform(Module.TypeDef type) {
	}
	
	public Module.Method transform(Module.Method method) {
		ArrayList<Module.Case> cases = new ArrayList<Module.Case>();
		for(Module.Case c : method.cases()) {
			cases.add(transform(c));
		}
		return new Module.Method(method.name(), method.type(), cases);
	}
	
	public Module.Case transform(Module.Case mcase) {
	}	
}
