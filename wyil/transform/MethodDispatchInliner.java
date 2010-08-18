package wyil.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.CExpr.BinOp;
import wyil.lang.CExpr.Convert;
import wyil.lang.CExpr.Invoke;
import wyil.lang.CExpr.LVal;
import wyil.lang.CExpr.ListAccess;
import wyil.lang.CExpr.NaryOp;
import wyil.lang.CExpr.Register;
import wyil.lang.CExpr.Tuple;
import wyil.lang.CExpr.TupleAccess;
import wyil.lang.CExpr.UnOp;
import wyil.lang.CExpr.Variable;
import wyil.lang.Code.Assign;
import wyil.lang.Code.Debug;
import wyil.lang.Code.Forall;
import wyil.lang.Code.IfGoto;
import wyil.lang.Code.Return;
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
	private int regTarget;
	
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
			// Now, calculate the register target. This is used to determine a safe
			// register number that is guaranteed not to interfere with any register
			// already being used in the body.
			HashSet<CExpr.Register> uses = new HashSet<CExpr.Register>();				
			Block.match(constraint, CExpr.Register.class, uses);
			int regTarget = uses.size();
			constraint = transform(regTarget,constraint);
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
		Block precondition = mcase.precondition();
		if(precondition != null) {
			// calculate reg target (see below)
			HashSet<CExpr.Register> uses = new HashSet<CExpr.Register>();				
			Block.match(precondition, CExpr.Register.class, uses);			
			int regTarget = uses.size();			
			precondition = transform(regTarget,precondition);
		}
		Block postcondition = mcase.postcondition();
		if(postcondition != null) {
			// calculate reg target (see below)
			HashSet<CExpr.Register> uses = new HashSet<CExpr.Register>();				
			Block.match(postcondition, CExpr.Register.class, uses);
			int regTarget = uses.size();
			postcondition = transform(regTarget,postcondition);
		}
		
		// Now, calculate the register target. This is used to determine a safe
		// register number that is guaranteed not to interfere with any register
		// already being used in the body.		
		HashSet<CExpr.Register> uses = new HashSet<CExpr.Register>();				
		Block.match(mcase.body(), CExpr.Register.class, uses);
		int regTarget = uses.size();
		
		Block body = transform(regTarget,mcase.body());		
		return new Module.Case(mcase.parameterNames(), precondition,
				postcondition, body);
	}
	
	public Block transform(int regTarget, Block block) {
		Block nblock = new Block();
		for (Stmt stmt : block) {
			Code c = stmt.code;
			Block inserts = new Block();
			this.regTarget = regTarget;
			if (c instanceof Assign) {
				Assign a = (Assign) c;
				LVal lhs = null;
				if (a.lhs != null) {
					lhs = (LVal) transform(a.lhs, stmt, inserts);
				}
				CExpr rhs = transform(a.rhs, stmt, inserts);
				if(rhs != null) {
					c = new Assign(lhs, rhs);
				} else {
					c = null;
				}
			} else if (c instanceof Debug) {
				Debug a = (Debug) c;
				c = new Debug(transform(a.rhs, stmt, inserts));
			} else if (c instanceof IfGoto) {
				IfGoto u = (IfGoto) c;
				c = new IfGoto(u.type, u.op, transform(u.lhs, stmt, inserts),
						transform(u.rhs, stmt, inserts), u.target);
			} else if (c instanceof Return) {
				Return a = (Return) c;
				if (a.rhs != null) {
					c = new Return(transform(a.rhs, stmt, inserts));
				}
			} else if (c instanceof Forall) {
				Forall a = (Forall) c;
				c = new Forall(a.label, (CExpr.Register) transform(a.variable,
						stmt, inserts), transform(a.source, stmt, inserts));
			}
			nblock.addAll(inserts);
			if(c != null) {
				nblock.add(c, stmt.attributes());
			}
		}
		return nblock;
	}
	
	public CExpr transform(CExpr r, Stmt stmt, Block inserts) {
		if(r instanceof ListAccess) {
			ListAccess la = (ListAccess) r;
			return CExpr.LISTACCESS(transform(la.src, stmt, inserts),
					transform(la.index, stmt, inserts));
		} else if (r instanceof BinOp) {
			BinOp bop = (BinOp) r;
			return CExpr.BINOP(bop.op, transform(bop.lhs, stmt, inserts),
					transform(bop.rhs, stmt, inserts));
		} else if (r instanceof UnOp) {
			UnOp bop = (UnOp) r;
			return CExpr.UNOP(bop.op,transform(bop.rhs, stmt, inserts));
		} else if (r instanceof Convert) {
			Convert c = (Convert) r;
			return CExpr.CONVERT(c.type,transform(c.rhs, stmt, inserts));
		} else if (r instanceof NaryOp) {
			NaryOp bop = (NaryOp) r;
			ArrayList<CExpr> args = new ArrayList<CExpr>();
			for(CExpr arg : bop.args) {
				args.add(transform(arg, stmt, inserts));
			}
			return CExpr.NARYOP(bop.op, args);
		} else if (r instanceof Tuple) {
			Tuple tup = (Tuple) r;
			HashMap<String,CExpr> values = new HashMap<String,CExpr>();
			for(Map.Entry<String,CExpr> e : tup.values.entrySet()) {
				values.put(e.getKey(),transform(e.getValue(), stmt, inserts));				
			}
			return CExpr.TUPLE(values);
		} else if (r instanceof TupleAccess) {
			TupleAccess ta = (TupleAccess) r;
			return CExpr.TUPLEACCESS(transform(ta.lhs, stmt, inserts),
					ta.field);
		} else if(r instanceof Invoke) {
			Invoke a = (Invoke) r;									
			ArrayList<CExpr> args = new ArrayList<CExpr>();
			for(CExpr arg : a.args){
				args.add(transform(arg,stmt,inserts));
			}			
			CExpr.Invoke ivk = CExpr.INVOKE(a.type,a.name,a.caseNum,args);
			inserts.addAll(transform(regTarget,ivk,stmt));
			if(ivk.type.ret == Type.T_VOID) {
				return null;
			} else {
				return CExpr.REG(a.type(),regTarget);
			}
		} 
		
		return r;	
	}
	
	public Block transform(int regTarget, CExpr.Invoke ivk, Stmt stmt) {
		try {
			CExpr.LVar lhs = null;
			if(ivk.type.ret != Type.T_VOID) {
				lhs = CExpr.REG(ivk.type.ret,regTarget);
			}
			Module module = loader.loadModule(ivk.name.module());
			Module.Method method = module.method(ivk.name.name(),
					ivk.type);
			Block blk = new Block();
			int ncases = method.cases().size();
			Attribute.Source src = stmt.attribute(Attribute.Source.class);
			if(ncases == 1) {				
				Module.Case c = method.cases().get(0);
				Block constraint = c.precondition();
				if (constraint != null) {
					blk.addAll(transformConstraint(regTarget,constraint,ivk,src,c));
				}				
				blk.add(new Code.Assign(lhs,ivk),stmt.attributes());				
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
					Block constraint = c.precondition();
					if (constraint != null) {						
						constraint = transformConstraint(regTarget,constraint,ivk,src,c);
						if(caseNum < ncases) {
							nextLabel = Block.freshLabel();
							constraint = Block.chain(nextLabel, constraint);
						}						
						blk.addAll(constraint);
					}
						
					blk.add(new Code.Assign(lhs, CExpr.INVOKE(ivk.type,
							ivk.name, caseNum, ivk.args)), stmt.attributes());

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
	
	public Block transformConstraint(int regTarget, Block constraint,
			CExpr.Invoke ivk, Attribute.Source src, Module.Case c) {
		
		// Update the source number information
		constraint = resource(constraint,src); 
		
		// We need to perform the register shift. This is ensure that
		// registers used in the constraint do not interfere with registers
		// currently in use at the point where we inline it.
		constraint = Block.registerShift(this.regTarget,constraint);

		// Similarly, we need to make sure any labels used in the constraint do
		// not collide with labels used at the inline point.
		constraint = Block.relabel(constraint);		
		
		// Hook up actual arguments to constraint parameters.
		// This is done by substituting the parameter names for
		// their actual arguments. This works only because we
		// know that the parameters will never be assigned
		// within the constraint (i.e since they were generated
		// from a condition expression, only registers could be
		// assigned).
		HashMap<String,CExpr> binding = new HashMap<String,CExpr>();
		for (int i = 0; i != ivk.args.size(); ++i) {
			CExpr arg = ivk.args.get(i);
			String target = c.parameterNames().get(i);
			// FIXME: feels like some kind of problem related to
			// typing here. That is, if we need a conversion
			// between the argument type and the constraint
			// parameter type, then aren't we losing this?
			binding.put(target, arg);
			
		}									
		
		return Block.substitute(binding, constraint);
	}
	
	public static Block resource(Block block, Attribute.Source nsrc) {
		Block nblock = new Block();
		for(Stmt s : block) {
			List<Attribute> attrs = s.attributes();
			Attribute src = s.attribute(Attribute.Source.class);
			attrs.remove(src);
			attrs.add(nsrc);
			nblock.add(s.code,attrs);
		}
		return nblock;
	}
}
