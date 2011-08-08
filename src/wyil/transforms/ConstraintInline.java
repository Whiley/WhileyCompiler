package wyil.transforms;

import java.util.ArrayList;
import java.util.HashMap;

import wyil.*;
import wyil.lang.*;
import wyil.util.SyntacticElement;

/**
 * The purpose of this transform is two-fold:
 * <ol>
 * <li>To inline preconditions for method invocations.</li>
 * <li>To inline preconditions for division and list access expressions</li>
 * <li>To inline postcondition checks. This involves generating the appropriate
 * shadows for local variables referenced in post-conditions</li>
 * <li>To inline dispatch choices into call-sites. This offers a useful
 * optimisation in situations when we can statically determine that a subset of
 * cases is the dispatch target.</li>
 * </ol>
 * 
 * @author djp
 * 
 */
public class ConstraintInline implements Transform {
	private final ModuleLoader loader;
	private int regTarget;
	private String filename;
	
	public ConstraintInline(ModuleLoader loader) {
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
		Block constraint = type.constraint();
		
		if (constraint != null) {
			Block nconstraint = new Block();
			for (int i = 0; i != constraint.size(); ++i) {
				Block.Entry entry = constraint.get(i);
				Block nblk = transform(entry);
				if (nblk != null) {
					nconstraint.addAll(nblk);
				}
				nconstraint.add(entry);
			}
			constraint = nconstraint;
		}
		
		return new Module.TypeDef(type.name(), type.type(), constraint,
				type.attributes());
	}
	
	public Module.Method transform(Module.Method method) {
		ArrayList<Module.Case> cases = new ArrayList<Module.Case>();
		for(Module.Case c : method.cases()) {
			cases.add(transform(c));
		}
		return new Module.Method(method.name(), method.type(), cases);
	}
	
	public Module.Case transform(Module.Case mcase) {	
		Block body = mcase.body();
		Block nbody = new Block();		
		for(int i=0;i!=body.size();++i) {
			Block.Entry entry = body.get(i);
			Block nblk = transform(entry);			
			if(nblk != null) {								
				nbody.addAll(nblk);				
			} 					
			nbody.add(entry);
		}
		
		return new Module.Case(nbody, mcase.precondition(),
				mcase.postcondition(), mcase.locals(), mcase.attributes());
	}	
	
	public Block transform(Block.Entry entry) {
		Code code = entry.code;
		
		// TODO: add support for indirect invokes and sends
		if(code instanceof Code.Invoke) {
			return transform((Code.Invoke)code, entry);
		} else if(code instanceof Code.Send) {
			
		} else if(code instanceof Code.ListLoad) {
			
		} else if(code instanceof Code.Update) {
			
		} else if(code instanceof Code.BinOp) {
			
		} else if(code instanceof Code.Return) {
			
		}
		
		return null;
	}

	/**
	 * For the invoke bytecode, we need to inline any preconditions associated
	 * with the target.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.Invoke code, SyntacticElement elem) {
		return null;
	}
	
	/**
	 * For the send bytecode, we need to inline any preconditions associated
	 * with the target.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.Send code, SyntacticElement elem) {
		return null;
	}

	/**
	 * For the return bytecode, we need to inline any postcondition associated
	 * with this function/method.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.Return code, SyntacticElement elem) {
		return null;
	}

	/**
	 * For the listload bytecode, we need to add a check that the index is
	 * within the bounds of the list. 
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.ListLoad code, SyntacticElement elem) {
		return null;
	}

	/**
	 * For the update bytecode, we need to add a check the indices of any lists
	 * used in the update are within bounds.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.Update code, SyntacticElement elem) {
		return null;
	}

	/**
	 * For the case of a division operation, we need to check that the divisor
	 * is not zero.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.BinOp code, SyntacticElement elem) {
		return null;
	}
	
}
