// Copyright (c) 2012, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil.transforms;

import java.util.*;

import wybs.lang.Builder;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wyil.lang.*;
import wyil.lang.Code;
import wyil.util.ErrorMessages;
import wyil.util.BigRational;
import wyil.util.Pair;
import static wybs.lang.SyntaxError.*;
import static wyil.util.ErrorMessages.errorMessage;
import wyil.Transform;
import wyone.core.Automaton;
import wyil.util.ConstraintSolver;
import static wyil.util.ConstraintSolver.*;

/**
 * Responsible for compile-time checking of constraints. This involves
 * converting WYIL into the appropriate form for the automated theorem prover
 * (wyone).  
 * 
 * @author David J. Pearce
 * 
 */
public class VerificationCheck implements Transform {	
	/**
	 * limit on number of steps theorem prover is allowed to take.
	 */
	private int timeout = getTimeout();	
	
	/**
	 * Determines whether verification is enabled or not.
	 */
	private boolean enabled = getEnable();
	
	private Builder builder;
	
	private String filename;	
	
	public VerificationCheck(Builder builder) {
		this.builder = builder;
	}
	
	public static String describeEnable() {
		return "Enable/disable compile-time verification";
	}
	
	public static boolean getEnable() {
		return false; // default value
	}
	
	public void setEnable(boolean flag) {
		this.enabled = flag;
	}
	
	public static int getTimeout() {
		return 250;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void apply(WyilFile module) {
		if(enabled) {
			this.filename = module.filename();
			for(WyilFile.TypeDeclaration type : module.types()) {
				transform(type);
			}		
			for(WyilFile.MethodDeclaration method : module.methods()) {
				transform(method);
			}		
		}
	}
	
	protected void transform(WyilFile.TypeDeclaration def) {
		
	}
	
	protected void transform(WyilFile.MethodDeclaration method) {		
		for(WyilFile.Case c : method.cases()) {
			transform(c,method);
		}
	}
	
	protected void transform(WyilFile.Case methodCase, WyilFile.MethodDeclaration method) {
		Automaton constraint = new Automaton(ConstraintSolver.SCHEMA); 
		int root = constraint.add(True);		
				
		// add type information available from parameters
		Type.FunctionOrMethod fmm = method.type();
		int paramStart = 0;
		for(int i=paramStart;i!=fmm.params().size();++i) {
			Type paramType = fmm.params().get(i);
			
			// FIXME: add type information
			
//			WVariable pv = new WVariable(i + "$" + 0);
//			constraint = WFormulas.and(constraint,
//					WTypes.subtypeOf(pv, convert(paramType)));
		}
		
		Block precondition = methodCase.precondition();				
		
		if(precondition != null) {
			Automaton precon = transform(constraint, true, precondition);
		}
		
		transform(constraint,false,methodCase.body());
	}
	
	
	public static class Scope {
		public final int end;
		
		public Scope(int end) {
			this.end = end;
		}
	}
	
	private static class LoopScope<T extends Code.Loop> extends Scope {
		public final T loop;
		
		public LoopScope(T loop, int end) {
			super(end);
			this.loop = loop;
		}
	}
	
	private static class ForScope extends LoopScope<Code.ForAll> {
		public final WExpr src;
		public final WVariable var;

		public ForScope(Code.ForAll forall, int end, WExpr src, WVariable var) {
			super(forall, end);
			this.src = src;
			this.var = var;
		}
	}
	
	/**
	 * Represents a path through the control-flow graph which has not yet been
	 * explored.
	 * 
	 * @author djp
	 * 
	 */
	private static class Branch {
		public final int pc;
		public final int root; // constraint root
		public final Automaton constraint;
		public final int[] environment;
		public final ArrayList<Scope> scopes;

		public Branch(int pc, int root, Automaton constraint, int[] environment,
				ArrayList<Scope> scopes) {
			this.pc = pc;
			this.root = root;
			this.constraint = constraint;
			this.environment = Arrays.copyOf(environment,environment.length);
			this.scopes = new ArrayList<Scope>(scopes);
		}
	}
	
	protected int transform(int root, Automaton constraint, boolean assumes, Block blk) {
		ArrayList<Branch> branches = new ArrayList<Branch>();
		ArrayList<Scope> scopes = new ArrayList<Scope>();
		int[] environment = new int[blk.numSlots()];

		// take initial branch
		root = transform(0, root, constraint, environment, scopes,
				branches, assumes, blk);

		// continue any resulting branches
		while (!branches.isEmpty()) {
			int last = branches.size() - 1;
			Branch branch = branches.get(last);
			branches.remove(last);
			root = Or(
					constraint,
					root,
					transform(branch.pc, branch.root, branch.constraint,
							branch.environment, branch.scopes, branches,
							assumes, blk));
		}
		
		// The following is necessary to prevent any possible clashes between
		// temporary variables used in pre- and post-conditions which are then
		// merged into the running constraint.
		HashMap<WExpr,WExpr> binding = new HashMap<WExpr,WExpr>();
		for(int i=blk.numInputs();i<blk.numSlots();++i) {
			for(int j=0;j<=environment[i];++j) {
				binding.put(new WVariable(i + "$" + j), WVariable.freshVar());
			}
		}

		return constraint.substitute(binding);
	}
	
	protected int transform(int pc, int root, Automaton constraint, int[] environment,
			ArrayList<Scope> scopes,
			ArrayList<Branch> branches, boolean assumes, Block body) {
		
		// the following is necessary for branches generated from conditionals
		
		int bodySize = body.size();		
		for (int i = pc; i != bodySize; ++i) {	
			constraint = exitScope(constraint,environment,scopes,i);
			
			Block.Entry entry = body.get(i);			
			Code code = entry.code;
			
			if(code instanceof Code.Goto) {
				Code.Goto g = (Code.Goto) code;
				i = findLabel(i,g.target,body);					
			} else if(code instanceof Code.If) {
				Code.If ifgoto = (Code.If) code;
				WFormula test = buildTest(ifgoto.op, entry, ifgoto.leftOperand,
						ifgoto.rightOperand, environment);				
				int targetpc = findLabel(i,ifgoto.target,body)	;
				branches.add(new Branch(targetpc, WFormulas.and(constraint,
						test), environment, scopes));
				constraint = WFormulas.and(constraint,test.not());
			} else if(code instanceof Code.IfIs) {
				// TODO: implement me!
			} else if(code instanceof Code.ForAll) {
				Code.ForAll forall = (Code.ForAll) code; 
				int end = findLabel(i,forall.target,body);
				WExpr src = operand(forall.sourceOperand,environment);
				WVariable var = new WVariable(forall.indexOperand + "$"
						+ environment[forall.indexOperand]);
				constraint = WFormulas.and(constraint,
						WTypes.subtypeOf(var, convert(forall.type.element())));
				
				if (forall.type instanceof Type.EffectiveList) {
					// We have to treat lists differently from sets because of the
					// way wyone handles list quantification. It's kind of annoying,
					// but there's not much we can do.
					WVariable index = WVariable.freshVar();
					constraint = WFormulas.and(constraint,
							WExprs.equals(var, new WListAccess(src,index)),
							WNumerics.lessThanEq(WNumber.ZERO, index),
							WNumerics.lessThan(index, new WLengthOf(src)),
							WTypes.subtypeOf(index, WIntType.T_INT));
					scopes.add(new ForScope(forall,end,src,index));
				} else if (forall.type instanceof Type.EffectiveSet) {
					Type.EffectiveSet es = (Type.EffectiveSet) forall.type;
					constraint = WFormulas.and(constraint, WSets.elementOf(var, src));
					scopes.add(new ForScope(forall,end,src,var));
				} else if (forall.type instanceof Type.EffectiveMap) {
					// TODO
				}
				
				// FIXME: assume loop invariant?
			} else if(code instanceof Code.Loop) {
				Code.Loop loop = (Code.Loop) code; 
				int end = findLabel(i,loop.target,body);
				scopes.add(new LoopScope(loop,end));
				// FIXME: assume loop invariant?
				// FIXME: assume condition?
			} else if(code instanceof Code.Return) {
				// we don't need to do anything for a return!
				return constraint;
			} else {
				constraint = transform(entry, constraint, environment, assumes);
			}
		}
		
		return constraint;
	}
	
	private static WFormula exitScope(WFormula constraint,
			int[] environment, ArrayList<Scope> scopes, int pc) {
		
		while (!scopes.isEmpty() && top(scopes).end <= pc) {
			// yes, we're exiting a scope
			Scope scope = pop(scopes);
			
			if(scope instanceof LoopScope) {
				LoopScope lscope = (LoopScope) scope;

				// trash modified variables
				for (int register : lscope.loop.modifiedOperands) {
					environment[register] = environment[register] + 1;
				}
				if(lscope instanceof ForScope) {
					ForScope fscope = (ForScope) lscope;
					// existing for all scope so existentially quantify generated
					// formula
					WVariable var = fscope.var;
					// Split for the formula into those bits which need to be
					// quantified, and those which don't
					Pair<WFormula, WFormula> split = splitFormula(var.name(),
							constraint);
					
					if(pc == fscope.end) { 						
						constraint = WFormulas.and(
								split.second(),
								new WBoundedForall(true, var, fscope.src, split
										.first()));
					} else {
						constraint = WFormulas.and(
								split.second(),
								new WBoundedForall(false, var, fscope.src, split
										.first().not()));						
					}
				}
			}			
		}
		
		return constraint;
	}
	
	private static int findLabel(int i, String label, Block body) {
		for(;i!=body.size();++i) {
			Code code = body.get(i).code;
			if(code instanceof Code.Label) {
				Code.Label l = (Code.Label) code;
				if(l.label.equals(label)) {
					return i;
				}
			}
		}
		// technically, this is dead-code.
		return i;
	}
	
	/**
	 * Transform the given constraint according to the abstract semantics of the
	 * given (simple) instruction (entry). The environment maps slots to their
	 * current single assignment number. Likewise, the stack models the stack
	 * and accumulates expressions.
	 * 
	 * @param entry
	 *            --- contains bytecode in question to apply.
	 * @param constraint
	 *            --- strongest condition which holds before this bytecode.
	 * @param environment
	 *            --- map of variables to their current single assignment
	 *            number.
	 * @param stack
	 *            --- current stack of intermediate expressions.
	 * @param assume
	 *            --- if true, indicates assumption mode.
	 * @return
	 */
	protected int transform(Block.Entry entry, Automaton constraint,
			int[] environment, boolean assume) {
		Code code = entry.code;		
		
		try {
			if(code instanceof Code.Assert) {
				return transform((Code.Assert)code,entry,constraint,environment,assume);
			} else if(code instanceof Code.BinArithOp) {
				return transform((Code.BinArithOp)code,entry,constraint,environment);
			} else if(code instanceof Code.Convert) {
				return transform((Code.Convert)code,entry,constraint,environment);
			} else if(code instanceof Code.Const) {
				return transform((Code.Const)code,entry,constraint,environment);
			} else if(code instanceof Code.Debug) {
				// skip
			} else if(code instanceof Code.FieldLoad) {
				return transform((Code.FieldLoad)code,entry,constraint,environment);			
			} else if(code instanceof Code.IndirectInvoke) {
				return transform((Code.IndirectInvoke)code,entry,constraint,environment);
			} else if(code instanceof Code.Invoke) {
				return transform((Code.Invoke)code,entry,constraint,environment);
			} else if(code instanceof Code.Invert) {
				return transform((Code.Invert)code,entry,constraint,environment);
			} else if(code instanceof Code.Label) {
				// skip			
			} else if(code instanceof Code.BinListOp) {
				return transform((Code.BinListOp)code,entry,constraint,environment);
			} else if(code instanceof Code.LengthOf) {
				return transform((Code.LengthOf)code,entry,constraint,environment);
			} else if(code instanceof Code.SubList) {
				return transform((Code.SubList)code,entry,constraint,environment);
			} else if(code instanceof Code.IndexOf) {
				return transform((Code.IndexOf)code,entry,constraint,environment);
			} else if(code instanceof Code.Move) {
				return transform((Code.Move)code,entry,constraint,environment);
			} else if(code instanceof Code.Assign) {
				return transform((Code.Assign)code,entry,constraint,environment);
			} else if(code instanceof Code.Update) {
				return transform((Code.Update)code,entry,constraint,environment);
			} else if(code instanceof Code.NewMap) {
				return transform((Code.NewMap)code,entry,constraint,environment);
			} else if(code instanceof Code.NewList) {
				return transform((Code.NewList)code,entry,constraint,environment);
			} else if(code instanceof Code.NewRecord) {
				return transform((Code.NewRecord)code,entry,constraint,environment);
			} else if(code instanceof Code.NewSet) {
				return transform((Code.NewSet)code,entry,constraint,environment);
			} else if(code instanceof Code.NewTuple) {
				return transform((Code.NewTuple)code,entry,constraint,environment);
			} else if(code instanceof Code.UnArithOp) {
				return transform((Code.UnArithOp)code,entry,constraint,environment);
			} else if(code instanceof Code.Dereference) {
				return transform((Code.Dereference)code,entry,constraint,environment);
			} else if(code instanceof Code.Nop) {
				// skip			
			} else if(code instanceof Code.BinSetOp) {
				return transform((Code.BinSetOp)code,entry,constraint,environment);
			} else if(code instanceof Code.BinStringOp) {
				return transform((Code.BinStringOp)code,entry,constraint,environment);
			} else if(code instanceof Code.SubString) {
				return transform((Code.SubString)code,entry,constraint,environment);
			} else if(code instanceof Code.NewObject) {
				return transform((Code.NewObject)code,entry,constraint,environment);
			} else if(code instanceof Code.Throw) {
				return transform((Code.Throw)code,entry,constraint,environment);
			} else if(code instanceof Code.TupleLoad) {
				return transform((Code.TupleLoad)code,entry,constraint,environment);
			} else {			
				internalFailure("unknown: " + code.getClass().getName(),filename,entry);			
			}
		} catch(InternalFailure e) {
			throw e;
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable e) {
			internalFailure(e.getMessage(),filename,entry,e);
		}
		return -1; // dead-code
	}
	
	protected int transform(Code.Assert code, Block.Entry entry,
			int root, Automaton constraint, int[] environment,
			boolean assume) {
		// At this point, what we do is invert the condition being asserted and
		// check that it is unsatisfiable.
		int test = buildTest(constraint, code.op, entry, code.leftOperand, code.rightOperand, environment);
		
		if (assume) {
			// in assumption mode we don't assert the test; rather, we assume
			// it. 
		} else {
//			System.out.println("======================================");
//			System.out.println("CHECKING: " + test.not() + " && " + constraint);
//			System.out.println("======================================");

			// FIXME: run the solver!
			
			// Pass constraint through the solver to check for unsatisfiability			
//			Proof tp = Solver.checkUnsatisfiable(timeout,
//					WFormulas.and(test.not(), constraint),
//					wyone.Main.heuristic, wyone.Main.theories);
			
		}
		
		return test;
	}
	
	protected int transform(Code.BinArithOp code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		int lhs = operand(constraint, code.leftOperand,environment);
		int rhs = operand(constraint, code.rightOperand,environment);
		int result;
		
		switch(code.kind) {
		case ADD:
			result = Sum(constraint, lhs, rhs);
			break;
		case SUB:
			result = Sum(constraint, lhs, Neg(constraint, rhs));
			break;
		case MUL:
			result = Mul(constraint, lhs, rhs);
			break;
		case DIV:
			result = Div(constraint, lhs, rhs);			
			break;	
		default:
			internalFailure("unknown binary operator",filename,entry);
			return -1;
		}
				
		return update(code.target,result,environment,constraint);
	}

	protected int transform(Code.Convert code, Block.Entry entry,
			Automaton constraint, int[] environment) {
		int result = operand(constraint, code.operand, environment);
		return update(code.target, result, environment, constraint);
	}

	protected int transform(Code.Const code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		int rhs = convert(code.constant, constraint, entry);
		return update(code.target, rhs, environment, constraint);
	}

	protected int transform(Code.FieldLoad code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.IndirectInvoke code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.Invoke code, Block.Entry entry,
			 Automaton constraint, int[] environment)
			throws Exception {
		
		// first, take arguments off the stack
//		Type.FunctionOrMethod ft = code.type;
//		List<Type> ft_params = code.type.params();
//		ArrayList<WExpr> args = new ArrayList<WExpr>();
//		HashMap<WExpr,WExpr> binding = new HashMap<WExpr,WExpr>();
//		int[] code_operands = code.operands;
//		for(int i=0;i!=code_operands.length;++i) {
//			WExpr arg = operand(code_operands[i],environment);
//			args.add(arg);
//			binding.put(new WVariable(i + "$0"), arg);
//		}			
//		
//		// second, setup return value
//		if (code.target != Code.NULL_REG) {
//			WVariable rhs = new WVariable(code.name.toString(), args);
//
//			constraint = Automatons.and(constraint,
//					WTypes.subtypeOf(rhs, convert(ft.ret())));
//
//			// now deal with post-condition
//			Block postcondition = findPostcondition(code.name, ft, entry);
//			if (postcondition != null) {
//				Automaton pc = transform(WBool.TRUE, true, postcondition);
//				binding.put(new WVariable("0$0"), rhs);
//				constraint = Automatons.and(constraint, pc.substitute(binding));
//			}
//
//			return update(code.target, rhs, environment, constraint);
//		}
//		
//		return constraint;
		return constraint.add(False); // TODO
	}

	protected int transform(Code.Invert code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.BinListOp code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.LengthOf code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		int src = operand(constraint, code.operand, environment);
		int result = LengthOf(constraint, src);
		return update(code.target, result, environment, constraint);
	}

	protected int transform(Code.SubList code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.IndexOf code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		int src = operand(constraint, code.leftOperand, environment);
		int idx = operand(constraint, code.rightOperand, environment);
		int result = IndexOf(constraint, src, idx);
		return update(code.target, result, environment, constraint);
	}

	protected int transform(Code.Move code, Block.Entry entry,
			Automaton constraint, int[] environment) {
		return update(code.target,
				operand(constraint, code.operand, environment), environment,
				constraint);
	}
	
	protected int transform(Code.Assign code, Block.Entry entry,
			Automaton constraint, int[] environment) {
		return update(code.target,
				operand(constraint, code.operand, environment), environment,
				constraint);
	}

	protected int transform(Code.Update code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.NewMap code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.NewList code, Block.Entry entry,
			Automaton constraint, int[] environment) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = operand(constraint, code_operands[i], environment);
		}
		int result = List(constraint, vals);
		return update(code.target, result, environment, constraint);
	}

	protected int transform(Code.NewSet code, Block.Entry entry,
			Automaton constraint, int[] environment) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = operand(constraint, code_operands[i], environment);
		}
		int result = Set(constraint, vals);
		return update(code.target, result, environment, constraint);
	}

	protected int transform(Code.NewRecord code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		Type.Record type = code.type;
		ArrayList<String> fields = new ArrayList<String>(type.fields().keySet());
		return constraint.add(False); // TODO
	}

	protected int transform(Code.NewTuple code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = operand(constraint, code_operands[i], environment);
		}
		int result = Tuple(constraint, vals);
		return update(code.target, result, environment, constraint);		
	}

	protected int transform(Code.UnArithOp code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		if(code.kind == Code.UnArithKind.NEG) {
			int expr = operand(constraint, code.operand, environment);
			int result = Neg(constraint, expr);
			return update(code.target, result, environment, constraint);
		} else {
			return constraint.add(False); // TODO
		}
	}

	protected int transform(Code.Dereference code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.BinSetOp code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
//		WVariable lhs = operand(code.leftOperand, environment);
//		WVariable rhs = operand(code.rightOperand, environment);
//		WVariable target = update(code.target, environment);
//
//		WVariable tmp = WVariable.freshVar();
//		WSetConstructor sc = new WSetConstructor(tmp);
//		
//		HashMap<WVariable, WExpr> vars = new HashMap();
//		vars.put(tmp, target);
//		
//		switch(code.kind) {
//		case UNION:	{
//			
//			Automaton allc = WFormulas.or(WSets.subsetEq(sc, lhs),
//				WSets.subsetEq(sc, rhs));
//			constraint = WFormulas.and(constraint, WSets.subsetEq(lhs, target), WSets
//					.subsetEq(rhs, target), new WBoundedForall(true, vars, allc));
//			break;
//		}
//		case DIFFERENCE: {
//			Automaton left = new WBoundedForall(true, vars, WFormulas.and(WSets
//					.subsetEq(sc, lhs), WSets.subsetEq(sc, rhs).not()));
//
//			constraint = WFormulas
//					.and(constraint, WSets.subsetEq(lhs, target), left);		
//			break;
//		}
//		case INTERSECTION:
//			Automaton left = new WBoundedForall(true, vars, WFormulas.and(WSets
//					.subsetEq(sc, lhs), WSets.subsetEq(sc, rhs)));
//			vars = new HashMap();
//			vars.put(tmp, lhs);
//			Automaton right = new WBoundedForall(true, vars, WFormulas.implies(WSets
//					.subsetEq(sc, rhs), WSets.subsetEq(sc, target)));
//			
//			constraint = WFormulas
//					.and(constraint, left, right, WSets.subsetEq(target, lhs), WSets.subsetEq(target, rhs));
//			break;
//			default:
//				internalFailure("missing support for left/right set operations",filename,entry);
//		}
//
//		return constraint;
		return constraint.add(False); // TODO
	}
	
	protected int transform(Code.BinStringOp code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.SubString code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.NewObject code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.Throw code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		return constraint.add(False); // TODO
	}

	protected int transform(Code.TupleLoad code, Block.Entry entry,
			 Automaton constraint, int[] environment) {
		int src = operand(constraint, code.operand, environment);
		int idx = constraint.add(new Automaton.Int(code.index));
		int result = IndexOf(constraint, src, idx);
		return update(code.target, result, environment, constraint);		
	}
	
	protected Block findPostcondition(NameID name, Type.FunctionOrMethod fun,
			SyntacticElement elem) throws Exception {
		Path.Entry<WyilFile> e = builder.namespace().get(name.module(),
				WyilFile.ContentType);
		if (e == null) {
			syntaxError(
					errorMessage(ErrorMessages.RESOLUTION_ERROR, name.module()
							.toString()), filename, elem);
		}
		WyilFile m = e.read();
		WyilFile.MethodDeclaration method = m.method(name.name(), fun);

		for (WyilFile.Case c : method.cases()) {
			// FIXME: this is a hack for now
			return c.postcondition();
		}
		return null;
	}
	
	// The following method splits a formula into two components: those bits
	// which use the given variable (left), and those which don't (right). This
	// is done to avoid quantifying more than is necessary when dealing with
	// loops.
//	protected static Pair<WFormula, WFormula> splitFormula(String var, WFormula f) {
//		if (f instanceof WConjunct) {
//			WConjunct c = (WConjunct) f;
//			WFormula ts = WBool.TRUE;
//			WFormula fs = WBool.TRUE;
//			for (WFormula st : c.subterms()) {
//				Pair<WFormula, WFormula> r = splitFormula(var, st);
//				ts = WFormulas.and(ts, r.first());
//				fs = WFormulas.and(fs, r.second());
//			}
//			return new Pair<WFormula, WFormula>(ts, fs);
//		} else {
//			// not a conjunct, so check whether or not this uses var or not.
//			Set<WVariable> uses = WExprs.match(WVariable.class, f);
//			for (WVariable v : uses) {
//				if (v.name().equals(var)) {
//					return new Pair<WFormula, WFormula>(f, WBool.TRUE);
//				}
//			}
//			// Ok, doesn't use variable
//			return new Pair<WFormula, WFormula>(WBool.TRUE, f);
//		}
//	}
	/**
	 * Convert between a WYIL value and a WYONE value. Basically, this is really
	 * stupid and it would be good for them to be the same.
	 * 
	 * @param value
	 * @return
	 */
	private int convert(wyil.lang.Constant value, Automaton constraint, SyntacticElement elem) {
		if(value instanceof wyil.lang.Constant.Bool) {
			wyil.lang.Constant.Bool b = (wyil.lang.Constant.Bool) value;
			return b.value ? constraint.add(True) : constraint.add(False);
		} else if(value instanceof wyil.lang.Constant.Byte) {
			wyil.lang.Constant.Byte v = (wyil.lang.Constant.Byte) value;
			return Num(constraint, v.value);
		} else if(value instanceof wyil.lang.Constant.Char) {
			wyil.lang.Constant.Char v = (wyil.lang.Constant.Char) value;
			// Simple, but mostly good translation
			return Num(constraint, v.value);
		} else if(value instanceof wyil.lang.Constant.Map) {
			return constraint.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.FunctionOrMethod) {
			return constraint.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.Integer) {
			wyil.lang.Constant.Integer v = (wyil.lang.Constant.Integer) value;
			return Num(constraint, v.value);
		} else if(value instanceof wyil.lang.Constant.Null) {
			return constraint.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.List) {
			Constant.List vl = (Constant.List) value;
			int[] vals = new int[vl.values.size()];
			for(int i=0;i!=vals.length;++i) {				
				vals[i] = convert(vl.values.get(i),constraint,elem);
			}
			return List(constraint, vals);
		} else if(value instanceof wyil.lang.Constant.Set) {
			Constant.Set vs = (Constant.Set) value;			
			int[] vals = new int[vs.values.size()];
			int i=0;
			for(Constant c : vs.values) {				
				vals[i++] = convert(c,constraint,elem);
			}
			return Set(constraint,vals);
		} else if(value instanceof wyil.lang.Constant.Rational) {
			wyil.lang.Constant.Rational v = (wyil.lang.Constant.Rational) value;
			BigRational br = v.value;
			return constraint.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.Record) {
			Constant.Record vt = (Constant.Record) value;
			return constraint.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.Strung) {
			return constraint.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.Tuple) {
			Constant.Tuple vt = (Constant.Tuple) value;
			return constraint.add(False); // TODO
		} else {
			internalFailure("unknown value encountered (" + value + ")",filename,elem);
			return -1;
		}
	}
		
	/**
	 * Generate a formula representing a condition from an Code.IfCode or
	 * Code.Assert bytecodes.
	 * 
	 * @param op
	 * @param stack
	 * @param elem
	 * @return
	 */
	private int buildTest(Automaton constraint, Code.Comparator op,
			SyntacticElement elem, int leftOperand, int rightOperand,
			int[] environment) {
		int lhs = operand(constraint,leftOperand,environment);
		int rhs = operand(constraint,rightOperand,environment);
		
		switch(op) {
		case EQ:
			return Equals(constraint, lhs, rhs);
		case NEQ:
			return NotEquals(constraint, lhs, rhs);
		case GTEQ:
			return GreaterThanEq(constraint, lhs, rhs);
		case GT:
			return GreaterThan(constraint, lhs, rhs);
		case LTEQ:
			return LessThanEq(constraint, lhs, rhs);
		case LT:
			return LessThan(constraint, lhs, rhs);
		case SUBSET:
			return SubSet(constraint, lhs, rhs);
		case SUBSETEQ:
			return SubSetEq(constraint, lhs, rhs);
		case ELEMOF:
			return ElementOf(constraint, lhs, rhs);
		default:
			internalFailure("unknown comparator (" + op + ")",filename,elem);
			return -1;
		}
	}
	
	private static int operand(Automaton constraint, int register,
			int[] environment) {
		return Var(constraint, register + "$" + environment[register]);
	}

	private static int update(int target, int[] environment, Automaton constraint) {
		int nval = environment[target] + 1;
		environment[target] = nval;
		return Var(constraint, target + "$" + nval);		
	}
	
	private static int update(int lhs, int rhs, int[] environment,
			Automaton constraint) {
		int nval = environment[lhs] + 1;
		environment[lhs] = nval;
		return Equals(constraint, Var(constraint, lhs + "$" + nval), rhs);
	}
	
	private static <T> T pop(ArrayList<T> stack) {
		int last = stack.size()-1;
		T c = stack.get(last);
		stack.remove(last);
		return c;
	}
	
	private static <T> T top(ArrayList<T> stack) {
		int last = stack.size()-1;
		T c = stack.get(last);
		return c;
	}
}
