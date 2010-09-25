// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.stages;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import wyil.ModuleLoader;
import wyil.jvm.rt.BigRational;
import wyil.lang.*;
import wyil.lang.CExpr.*;
import wyil.lang.Code.*;
import wyil.util.ResolveError;
import wyil.util.SyntaxError;
import static wyil.util.SyntaxError.*;

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
public class PreconditionInline implements ModuleTransform {
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
		Block constraint = type.constraint();
		if(constraint == null) {
			return type;
		} else {
			// Now, calculate the register target. This is used to determine a safe
			// register number that is guaranteed not to interfere with any register
			// already being used in the body.
			HashSet<CExpr.Register> uses = new HashSet<CExpr.Register>();				
			Block.match(constraint, CExpr.Register.class, uses);
			int regTarget = CExpr.maxRegister(uses)+1;
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
			int regTarget = CExpr.maxRegister(uses)+1;			
			precondition = transform(regTarget,precondition);
		}
		Block postcondition = mcase.postcondition();
		if(postcondition != null) {
			// calculate reg target (see below)
			HashSet<CExpr.Register> uses = new HashSet<CExpr.Register>();				
			Block.match(postcondition, CExpr.Register.class, uses);
			int regTarget = CExpr.maxRegister(uses)+1;
			postcondition = transform(regTarget,postcondition);
		}
		
		// Now, calculate the register target. This is used to determine a safe
		// register number that is guaranteed not to interfere with any register
		// already being used in the body.		
		HashSet<CExpr.Register> uses = new HashSet<CExpr.Register>();				
		Block.match(mcase.body(), CExpr.Register.class, uses);
		int regTarget = CExpr.maxRegister(uses)+1;
		
		Block body = transform(regTarget,mcase.body());		
		return new Module.Case(mcase.parameterNames(), precondition,
				postcondition, body);
	}
	
	public Block transform(int regTarget, Block block) {
		Block nblock = new Block();
		for (Stmt stmt : block) {
			try {
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
					c = new IfGoto(u.op, transform(u.lhs, stmt, inserts),
							transform(u.rhs, stmt, inserts), u.target);
				} else if (c instanceof Return) {
					Return a = (Return) c;
					if (a.rhs != null) {
						c = new Return(transform(a.rhs, stmt, inserts));
					}
				} else if (c instanceof Forall) {
					Forall a = (Forall) c;
					Block invariant = a.invariant;
					if(invariant != null) {
						invariant = transform(regTarget,invariant);
					}
					c = new Forall(a.label, invariant,a.variable,
							transform(a.source, stmt, inserts), a.modifies);
				} else if (c instanceof Loop) {
					Loop l = (Loop) c;
					Block invariant = l.invariant;
					if(invariant != null) {
						invariant = transform(regTarget,invariant);
					}					
					c = new Loop(l.label, invariant, l.modifies);
				} 
				nblock.addAll(inserts);
				if(c != null) {
					nblock.add(c, stmt.attributes());
				}
			} catch(SyntaxError sex) {
				throw sex;
			} catch(Exception ex) {
				syntaxError("internal failure",filename,stmt,ex);
			}
		}
		return nblock;
	}
	
	public CExpr transform(CExpr r, Stmt stmt, Block inserts) {		
		if(r instanceof ListAccess) {
			return transform((ListAccess)r,stmt,inserts);			
		} else if (r instanceof BinOp) {
			return transform((BinOp)r,stmt,inserts);			
		} else if (r instanceof UnOp) {
			UnOp bop = (UnOp) r;
			return CExpr.UNOP(bop.op,transform(bop.rhs, stmt, inserts));
		} else if (r instanceof NaryOp) {
			return transform((NaryOp)r,stmt,inserts);			
		} else if (r instanceof Record) {
			Record tup = (Record) r;
			HashMap<String,CExpr> values = new HashMap<String,CExpr>();
			for(Map.Entry<String,CExpr> e : tup.values.entrySet()) {
				values.put(e.getKey(),transform(e.getValue(), stmt, inserts));				
			}
			return CExpr.RECORD(values);
		} else if (r instanceof RecordAccess) {
			RecordAccess ta = (RecordAccess) r;
			return CExpr.RECORDACCESS(transform(ta.lhs, stmt, inserts),
					ta.field);
		} else if(r instanceof Invoke) {
			Invoke a = (Invoke) r;							
			CExpr receiver = a.receiver;
			if(receiver != null) {
				receiver = transform(receiver,stmt,inserts);
			}
			ArrayList<CExpr> args = new ArrayList<CExpr>();			
			for(CExpr arg : a.args){
				args.add(transform(arg,stmt,inserts));
			}			
			CExpr.Invoke ivk = CExpr.INVOKE(a.type, a.name, a.caseNum,
					receiver, args);
			inserts.addAll(transform(regTarget,ivk,stmt));
			if(ivk.type.ret == Type.T_VOID) {
				return null;
			} else {
				return CExpr.REG(a.type(),regTarget++);
			}
		} 
		
		return r;	
	}
	
	public CExpr transform(ListAccess la, Stmt stmt, Block inserts) {		
		CExpr src = transform(la.src, stmt, inserts);
		CExpr index = transform(la.index, stmt, inserts);
		
		// First, perform lower bound check
		String exitLabel = Block.freshLabel();
		String checkLabel = Block.freshLabel();
		Attribute.Source attr = stmt.attribute(Attribute.Source.class);
		inserts.add(new Code.Check(checkLabel),attr);		
		inserts.add(new IfGoto(Code.COP.GTEQ, index, Value
				.V_INT(BigInteger.ZERO), exitLabel), attr);	
		inserts.add(new Code.Fail("negative list index possible"), attr);
		inserts.add(new Code.Label(exitLabel), attr);
		inserts.add(new Code.CheckEnd(checkLabel), attr);

		// Second, perform upper bound check
		exitLabel = Block.freshLabel();
		checkLabel = Block.freshLabel();
		inserts.add(new Code.Check(checkLabel),attr);				
		inserts.add(new IfGoto(Code.COP.LT, index, CExpr.UNOP(
					CExpr.UOP.LENGTHOF, src), exitLabel), attr);		
		inserts.add(new Code.Fail("out-of-bounds list index possible"), attr);
		inserts.add(new Code.Label(exitLabel), attr);
		inserts.add(new Code.CheckEnd(checkLabel), attr);
		
		return CExpr.LISTACCESS(src, index);
	}
	
	public CExpr transform(BinOp bop, Stmt stmt, Block inserts) {		
		CExpr lhs = transform(bop.lhs, stmt, inserts);
		CExpr rhs = transform(bop.rhs, stmt, inserts);
		if(bop.op == CExpr.BOP.DIV) {
			String exitLabel = Block.freshLabel();
			String checkLabel = Block.freshLabel();
			Attribute.Source attr = stmt.attribute(Attribute.Source.class);
			inserts.add(new Code.Check(checkLabel),attr);
			if(rhs.type() == Type.T_INT) {
				inserts.add(new IfGoto(Code.COP.NEQ, rhs, Value
						.V_INT(BigInteger.ZERO), exitLabel), attr);
			} else {
				inserts.add(new IfGoto(Code.COP.NEQ, rhs, Value
						.V_REAL(BigRational.ZERO), exitLabel), attr);
			}
			inserts.add(new Code.Fail("divide by zero"), attr);
			inserts.add(new Code.Label(exitLabel), attr);
			inserts.add(new Code.CheckEnd(checkLabel), attr);
		}
		return CExpr.BINOP(bop.op, lhs, rhs);
	}

	public CExpr transform(NaryOp bop, Stmt stmt, Block inserts) {		
		ArrayList<CExpr> args = new ArrayList<CExpr>();
		for(CExpr arg : bop.args) {
			args.add(transform(arg, stmt, inserts));
		}
		if (bop.op == CExpr.NOP.SUBLIST) {
			CExpr src = args.get(0);
			CExpr start = args.get(1);
			CExpr end = args.get(2);			

			// First, perform lower bound start check
			String exitLabel = Block.freshLabel();
			String checkLabel = Block.freshLabel();
			Attribute.Source attr = stmt.attribute(Attribute.Source.class);
			inserts.add(new Code.Check(checkLabel), attr);
			inserts.add(new IfGoto(Code.COP.GTEQ, start, Value
					.V_INT(BigInteger.ZERO), exitLabel), attr);
			inserts.add(new Code.Fail("negative sublist start possible"), attr);
			inserts.add(new Code.Label(exitLabel), attr);
			inserts.add(new Code.CheckEnd(checkLabel), attr);

			// Second, perform lower bound end check
			exitLabel = Block.freshLabel();
			checkLabel = Block.freshLabel();			
			inserts.add(new Code.Check(checkLabel), attr);
			inserts.add(new IfGoto(Code.COP.GTEQ, end, Value
					.V_INT(BigInteger.ZERO), exitLabel), attr);
			inserts.add(new Code.Fail("negative sublist end possible"), attr);
			inserts.add(new Code.Label(exitLabel), attr);
			inserts.add(new Code.CheckEnd(checkLabel), attr);

			// Third, perform upper bound start check
			exitLabel = Block.freshLabel();
			checkLabel = Block.freshLabel();
			inserts.add(new Code.Check(checkLabel), attr);
			inserts.add(new IfGoto(Code.COP.LTEQ, start, CExpr.UNOP(
					CExpr.UOP.LENGTHOF, src), exitLabel), attr);
			inserts.add(new Code.Fail("sublist start out-of-bounds"), attr);
			inserts.add(new Code.Label(exitLabel), attr);
			inserts.add(new Code.CheckEnd(checkLabel), attr);
			
			// Fourth, perform upper bound end check
			exitLabel = Block.freshLabel();
			checkLabel = Block.freshLabel();
			inserts.add(new Code.Check(checkLabel), attr);
			inserts.add(new IfGoto(Code.COP.LTEQ, start, CExpr.UNOP(
					CExpr.UOP.LENGTHOF, src), exitLabel), attr);
			inserts.add(new Code.Fail("sublist end out-of-bounds"), attr);
			inserts.add(new Code.Label(exitLabel), attr);
			inserts.add(new Code.CheckEnd(checkLabel), attr);
			
			// Fifth, perform comparison check
			exitLabel = Block.freshLabel();
			checkLabel = Block.freshLabel();
			inserts.add(new Code.Check(checkLabel), attr);
			inserts.add(new IfGoto(Code.COP.LTEQ, start, end, exitLabel), attr);
			inserts.add(new Code.Fail("sublist start > end"), attr);
			inserts.add(new Code.Label(exitLabel), attr);
			inserts.add(new Code.CheckEnd(checkLabel), attr);		

		}
		return CExpr.NARYOP(bop.op, args);
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
					String lab = Block.freshLabel();
					blk.add(new Code.Check(lab),stmt.attribute(Attribute.Source.class));
					blk.addAll(transformConstraint(regTarget,constraint,ivk,src,c,method));
					blk.add(new Code.CheckEnd(lab),stmt.attribute(Attribute.Source.class));
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
				String lab = Block.freshLabel();
				// I'm not entirely sure what the effect of putting all of this
				// into a check block really is.  But, I think it should work ...
				blk.add(new Code.Check(lab),stmt.attribute(Attribute.Source.class));
				for (Module.Case c : method.cases()) {
					if(caseNum > 1) {
						blk.add(new Code.Label(nextLabel));
					}
					Block constraint = c.precondition();
					if (constraint != null) {						
						constraint = transformConstraint(regTarget,constraint,ivk,src,c,method);
						if(caseNum < ncases) {
							nextLabel = Block.freshLabel();
							constraint = Block.chain(nextLabel, constraint);
						}						
						blk.addAll(constraint);
					}
						
					blk.add(new Code.Assign(lhs, CExpr.INVOKE(ivk.type,
							ivk.name, caseNum, ivk.receiver, ivk.args)), stmt
							.attributes());

					if(caseNum++ < ncases) {
						blk.add(new Code.Goto(exitLabel));
					}
				}
				blk.add(new Code.CheckEnd(lab),stmt.attribute(Attribute.Source.class));
				blk.add(new Code.Label(exitLabel));
			}
			return blk;
		} catch(ResolveError rex) {
			throw new RuntimeException(rex.getMessage());
		}
	}
	
	public Block transformConstraint(int regTarget, Block constraint,
			CExpr.Invoke ivk, Attribute.Source src, Module.Case c,
			Module.Method method) {
		
		// Update the source number information
		constraint = Block.resource(constraint,src); 
		
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
		
		return  Block.substitute(binding, constraint);		
	}
}
