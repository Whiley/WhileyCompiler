package wyil.transforms;

import java.math.BigInteger;
import java.util.*;

import wyil.*;
import wyil.lang.*;
import wyil.util.ResolveError;
import wyil.util.SyntacticElement;
import static wyil.util.SyntaxError.*;
import wyjc.runtime.BigRational;

/**
 * The purpose of this transform is two-fold:
 * <ol>
 * <li>To inline preconditions for method invocations.</li>
 * <li>To inline preconditions for division and list/dictionary access expressions</li>
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
	private String filename;
	
	public ConstraintInline(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public void apply(Module module) {
		this.filename = module.filename();
		
		for(Module.TypeDef type : module.types()) {
			module.add(transform(type));
		}		
		for(Module.Method method : module.methods()) {
			module.add(transform(method));
		}
	}
	
	public Module.TypeDef transform(Module.TypeDef type) {
		Block constraint = type.constraint();
		
		if (constraint != null) {
			int freeSlot = constraint.numSlots();
			Block nconstraint = new Block(1);
			for (int i = 0; i != constraint.size(); ++i) {
				Block.Entry entry = constraint.get(i);
				Block nblk = transform(entry, freeSlot, null);
				if (nblk != null) {
					nconstraint.append(nblk);
				}
				nconstraint.append(entry);
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
		int freeSlot = Math.max(mcase.locals().size(),body.numSlots());
		Block nbody = new Block(body.numInputs());		
		for(int i=0;i!=body.size();++i) {
			Block.Entry entry = body.get(i);
			Block nblk = transform(entry,freeSlot,mcase);			
			if(nblk != null) {								
				nbody.append(nblk);				
			} 					
			nbody.append(entry);
		}
		
		return new Module.Case(nbody, mcase.precondition(),
				mcase.postcondition(), mcase.locals(), mcase.attributes());
	}	
	
	public Block transform(Block.Entry entry, int freeSlot, Module.Case methodCase) {
		Code code = entry.code;
		
		try {
			// TODO: add support for indirect invokes and sends
			if(code instanceof Code.Invoke) {
				return transform((Code.Invoke)code, freeSlot, entry);
			} else if(code instanceof Code.Send) {

			} else if(code instanceof Code.ListLoad) {
				return transform((Code.ListLoad)code,freeSlot,entry);
			} else if(code instanceof Code.DictLoad) {

			} else if(code instanceof Code.Update) {

			} else if(code instanceof Code.BinOp) {
				return transform((Code.BinOp)code,freeSlot,entry);
			} else if(code instanceof Code.Return) {
				return transform((Code.Return)code,freeSlot,entry,methodCase);
			}
		} catch(ResolveError e) {
			syntaxError("internal failure",filename,entry,e);
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
	public Block transform(Code.Invoke code, int freeSlot, SyntacticElement elem) throws ResolveError {		
		Block precondition = findPrecondition(code.name,code.type);
		if(precondition != null) {
			Block blk = new Block(0);
			List<Type> paramTypes = code.type.params();
			
			// TODO: mark as check block
			
			for(int i=paramTypes.size()-1;i>=0;--i) {				
				blk.append(Code.Store(paramTypes.get(i), freeSlot+i),attributes(elem));
			}
			
			blk.append(precondition.shift(freeSlot).relabel());
			
			for(int i=0;i<paramTypes.size();++i) {
				blk.append(Code.Load(paramTypes.get(i), freeSlot+i),attributes(elem));
			}
			return blk;
		}
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
	public Block transform(Code.Return code, int freeSlot, SyntacticElement elem, 
			Module.Case methodCase) {
		
		if(code.type != Type.T_VOID) {
			Block postcondition = methodCase.postcondition();
			if(postcondition != null) {
				Block blk = new Block(0);
				// FIXME: need to support shadows here!!
				blk.append(Code.Store(code.type, freeSlot),attributes(elem));
				blk.append(postcondition.shift(freeSlot).relabel());
				blk.append(Code.Load(code.type, freeSlot),attributes(elem));
				return blk;
			}
		}
		
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
	public Block transform(Code.ListLoad code, int freeSlot, SyntacticElement elem) {
		Block blk = new Block(0);
		// TODO: mark as check block
		blk.append(Code.Store(Type.T_INT, freeSlot),attributes(elem));
		blk.append(Code.Store(code.type, freeSlot+1),attributes(elem));
		String falseLabel = Block.freshLabel();
		String exitLabel = Block.freshLabel();
		blk.append(Code.Load(Type.T_INT, freeSlot),attributes(elem));	
		blk.append(Code.Const(Value.V_INTEGER(BigInteger.ZERO)),attributes(elem));
		blk.append(Code.IfGoto(Type.T_INT, Code.COp.LT, falseLabel),attributes(elem));
		blk.append(Code.Load(Type.T_INT, freeSlot),attributes(elem));	
		blk.append(Code.Load(code.type, freeSlot+1),attributes(elem));
		blk.append(Code.ListLength(code.type),attributes(elem));
		blk.append(Code.IfGoto(Type.T_INT, Code.COp.LT, exitLabel),attributes(elem));
		blk.append(Code.Label(falseLabel),attributes(elem));
		blk.append(Code.Fail("index out of bounds"),attributes(elem));
		blk.append(Code.Label(exitLabel),attributes(elem));
		blk.append(Code.Load(code.type, freeSlot+1),attributes(elem));
		blk.append(Code.Load(Type.T_INT, freeSlot),attributes(elem));
		return blk;		
	}

	/**
	 * For the dictload bytecode, we need to add a check that the key is
	 * contained in the list.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Block transform(Code.DictLoad code, SyntacticElement elem) {
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
	public Block transform(Code.BinOp code, int freeSlot, SyntacticElement elem) {
		
		if(code.bop == Code.BOp.DIV) {
			Block blk = new Block(0);
			// TODO: mark as check block
			blk.append(Code.Store(code.type, freeSlot),attributes(elem));
			String label = Block.freshLabel();
			blk.append(Code.Load(code.type, freeSlot),attributes(elem));
			if(code.type instanceof Type.Int) { 
				blk.append(Code.Const(Value.V_INTEGER(BigInteger.ZERO)),attributes(elem));
			} else {
				blk.append(Code.Const(Value.V_RATIONAL(BigRational.ZERO)),attributes(elem));
			}
			blk.append(Code.IfGoto(code.type, Code.COp.NEQ, label),attributes(elem));
			blk.append(Code.Fail("division by zero"),attributes(elem));
			blk.append(Code.Label(label),attributes(elem));
			blk.append(Code.Load(code.type, freeSlot),attributes(elem));
			return blk;
		} 
		
		// not a division bytecode, so ignore
		return null;					
	}
	
	protected Block findPrecondition(NameID name, Type.Fun fun) throws ResolveError {
		Module m = loader.loadModule(name.module());				
		Module.Method method = m.method(name.name(),fun);
		
		for(Module.Case c : method.cases()) {
			// FIXME: this is a hack for now
			return c.precondition();
		}
		return null;
	}
	
	private java.util.List<Attribute> attributes(SyntacticElement elem) {
		return elem.attributes();
	}
}
