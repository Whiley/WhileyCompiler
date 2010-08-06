package wyil.transform;

import java.util.ArrayList;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.util.ResolveError;

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
		ArrayList<Module.Method> methods = new ArrayList<Module.Method>();
		
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
		Block constraint = type.constraint();
		if(constraint == null) {
			return type;
		} else {
			constraint = transform(constraint);
			return new Module.TypeDef(type.name(), type.type(), constraint);
		}
	}
	
	public Module.Method transform(Module.Method method) {
		ArrayList<Module.Case> cases = new ArrayList<Module.Case>();
		for(Module.Case c : method.cases()) {
			cases.add(transform(c));
		}
		return new Module.Method(method.name(), method.type(), cases);
	}
	
	public Module.Case transform(Module.Case mcase) {
		Block constraint = mcase.constraint();
		if(constraint != null) {
			constraint = transform(constraint);
		}
		Block body = transform(mcase.body());
		return new Module.Case(mcase.parameterNames(),constraint,body);
	}
	
	public Block transform(Block block) {
		Block nblock = new Block();
		for(Code c : block) {
			if(c instanceof Code.Invoke) {
				nblock.addAll(transform((Code.Invoke) c));												
			} else {
				nblock.add(c);
			}
		}
		return nblock;
	}
	
	public Block transform(Code.Invoke ivk) {
		try {
			Module module = loader.loadModule(ivk.name.module());
			Module.Method method = module.method(ivk.name.name(),
					ivk.type);
			Block blk = new Block();
			int ncases = method.cases().size();
			if(ncases == 1) {
				Module.Case c = method.cases().get(0);
				Block constraint = c.constraint();
				if (constraint != null) {
					// FIXME: substitute parameters properly
					blk.addAll(constraint);
				}
				blk.add(ivk);
			} else {			
				// This is the multi-case option, which is harder. Here, we need
				// to chain together the constrain tests for multiple different
				// method cases. Thus, if one fails we move onto try the next
				// and, only if we try every possible case, do we actually fail.
				int caseNum = 1;
				String exitLabel = Block.freshLabel();
				String nextLabel = null;
				for (Module.Case c : method.cases()) {
					if(caseNum > 1) {
						blk.add(new Code.Label(nextLabel));
					}
					Block constraint = c.constraint();
					if (constraint != null) {
						constraint = Block.relabel(constraint);
						if(caseNum < ncases) {
							nextLabel = Block.freshLabel();
							constraint = chain(nextLabel, constraint);
						}
						// hook up actual arguments to constraint parameters
						for (int i = 0; i != ivk.args.size(); ++i) {
							RVal arg = ivk.args.get(i);
							String target = c.parameterNames().get(i);
							Type type = method.type().params.get(i);
							// FIXME: still a major problem here
							blk.add(new Code.Assign(RVal.VAR(type, target),
									arg));
						}
						blk.addAll(constraint);
					}
					
					blk.add(new Code.Invoke(ivk.type, ivk.name, caseNum, ivk.lhs,
							ivk.args));
					
					if(caseNum++ < ncases) {
						blk.add(new Code.Goto(exitLabel));
					}
				}
				blk.add(new Code.Label(exitLabel));
			}
			return blk;
		} catch(ResolveError rex) {
			throw new RuntimeException(rex.getMessage());
		}
	}
	
	public static Block chain(String target, Block block) {
		Block nblock = new Block();
		for(Code c : block) {
			if(c instanceof Code.Fail) {
				c = new Code.Goto(target);
			}
			nblock.add(c);			
		}
		return nblock;
	}
}
