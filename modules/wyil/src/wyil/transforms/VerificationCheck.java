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
import wyil.util.ErrorMessages;
import wyil.util.BigRational;
import wyil.util.Pair;
import static wybs.lang.SyntaxError.*;
import static wyil.util.ErrorMessages.errorMessage;
import wyil.Transform;
import wyone.core.Automaton;
import wyone.io.PrettyAutomataWriter;
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
		// add type information available from parameters
		Type.FunctionOrMethod fmm = method.type();
		int paramStart = 0;
		for(int i=paramStart;i!=fmm.params().size();++i) {
			Type paramType = fmm.params().get(i);
			
			// FIXME: add type information
			
//			WVariable pv = new WVariable(i + "$" + 0);
//			constraint = WFormulas.and(branch.automaton,
//					WTypes.subtypeOf(pv, convert(paramType)));
		}
		
		Block body = methodCase.body();
		Block precondition = methodCase.precondition();				
		
		Branch branch = new Branch(0,body.numSlots()); 
		if(precondition != null) {
			branch = transform(true, branch, precondition);
			branch.pc = 0; // must reset
		}
		
		transform(false,branch,body);
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
		public final int src;
		public final int var;

		public ForScope(Code.ForAll forall, int end, int src, int var) {
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
	private static final class Branch {
		public int pc;
		public final int[] environment;
		public final ArrayList<Scope> scopes;		
		public final Automaton automaton;
		private final ArrayList<Integer> constraints;

		public Branch(int pc, int numVariables) {
			this.pc = pc;
			this.automaton = new Automaton(SCHEMA);
			this.constraints = new ArrayList<Integer>();
			this.environment = new int[numVariables];
			this.scopes = new ArrayList<Scope>();
		}

		private Branch(int pc, int[] environment, List<Scope> scopes,
				List<Integer> constraints, Automaton automaton) {
			this.pc = pc;
			this.automaton = new Automaton(automaton);
			this.constraints = new ArrayList<Integer>(constraints);
			this.environment = environment.clone();
			this.scopes = new ArrayList<Scope>(scopes);
		}
		
		public Branch clone() {
			return new Branch(pc, environment, scopes, constraints, automaton);
		}

		/**
		 * Assert that the given constraint holds.
		 * 
		 * @return
		 */
		public boolean assertTrue(int test) {
			try {
				System.out.println("================================================");
				System.out.print("ASSERTING: " + constraints);				
				System.out.println("\n================================================");
				Automaton tmp = new Automaton(automaton);
				int root = And(tmp,constraints);
				root = And(tmp,root,Not(tmp,test));
				int mark = tmp.mark(root);				
				new PrettyAutomataWriter(System.out,SCHEMA).write(tmp);
				System.out.println("\n --------");				
				infer(tmp);
				new PrettyAutomataWriter(System.out,SCHEMA).write(tmp);
				System.out.println();
				// assertion holds if a constradiction is shown.
				return tmp.get(tmp.root(mark)).equals(ConstraintSolver.False);
			} catch(Exception e) {
				e.printStackTrace();
			}
			return false;
		}
				
		public void assume(int constraint) {
			constraints.add(constraint);
		}
		
		public void join(Branch b) {
			// TODO: kind of important
		}
		
		public int read(int register) {
			return Var(automaton, register + "$" + environment[register]);
		}

		public void write(int lhs, int rhs) {
			int nval = environment[lhs] + 1;
			environment[lhs] = nval;
			constraints.add(Equals(automaton, Var(automaton, lhs + "$" + nval),
					rhs));
		}
	}
	
	protected Branch transform(boolean assumes, Branch branch, Block blk) {
		ArrayList<Branch> branches = new ArrayList<Branch>();

		// take initial branch
		transform(assumes, blk, branch, branches);

		// continue any resulting branches
		while (!branches.isEmpty()) {
			int last = branches.size() - 1;
			Branch b = branches.get(last);
			branches.remove(last);
			transform(assumes, blk, b, branches);
			branch.join(b);
		}
		
		// The following is necessary to prevent any possible clashes between
		// temporary variables used in pre- and post-conditions which are then
		// merged into the running constraint.
		
		// TODO: fix this!
		
//		HashMap<WExpr,WExpr> binding = new HashMap<WExpr,WExpr>();
//		for(int i=blk.numInputs();i<blk.numSlots();++i) {
//			for(int j=0;j<=environment[i];++j) {
//				binding.put(new WVariable(i + "$" + j), WVariable.freshVar());
//			}
//		}

		//return constraint.substitute(binding);
		return branch;
	}
	
	protected Branch transform(boolean assumes, Block body, Branch branch,
			ArrayList<Branch> branches) {
	
		int pc = branch.pc;
		Automaton constraint = branch.automaton;		
		ArrayList<Integer> constraints = branch.constraints;
		ArrayList<Scope> scopes = branch.scopes;
		int[] environment = branch.environment;
		
		int bodySize = body.size();		
		for (int i = pc; i != bodySize; ++i) {	
			//constraint = exitScope(constraint,environment,scopes,i);
			
			Block.Entry entry = body.get(i);			
			Code code = entry.code;
			
			if(code instanceof Code.Goto) {
				Code.Goto g = (Code.Goto) code;
				i = findLabel(i,g.target,body);					
			} else if(code instanceof Code.If) {
				Code.If ifgoto = (Code.If) code;
				int test = buildTest(ifgoto.op, entry, ifgoto.leftOperand,
						ifgoto.rightOperand, branch);
				Branch trueBranch = branch.clone();
				trueBranch.pc = findLabel(i,ifgoto.target,body)	;
				trueBranch.constraints.add(test);
				branches.add(trueBranch);
				constraints.add(Not(constraint,test));
			} else if(code instanceof Code.IfIs) {
				// TODO: implement me!
			} else if(code instanceof Code.ForAll) {
				Code.ForAll forall = (Code.ForAll) code; 
				int end = findLabel(i,forall.target,body);
				int src = branch.read(forall.sourceOperand);
				int var = branch.read(forall.indexOperand);

//              FIXME!				
//				constraint = WFormulas.and(constraint,
//						WTypes.subtypeOf(var, convert(forall.type.element())));
//				
//				if (forall.type instanceof Type.EffectiveList) {
//					// We have to treat lists differently from sets because of the
//					// way wyone handles list quantification. It's kind of annoying,
//					// but there's not much we can do.
//					WVariable index = WVariable.freshVar();
//					constraint = WFormulas.and(constraint,
//							WExprs.equals(var, new WListAccess(src,index)),
//							WNumerics.lessThanEq(WNumber.ZERO, index),
//							WNumerics.lessThan(index, new WLengthOf(src)),
//							WTypes.subtypeOf(index, WIntType.T_INT));
//					scopes.add(new ForScope(forall,end,src,index));
//				} else if (forall.type instanceof Type.EffectiveSet) {
//					Type.EffectiveSet es = (Type.EffectiveSet) forall.type;
//					constraint = WFormulas.and(constraint, WSets.elementOf(var, src));
//					scopes.add(new ForScope(forall,end,src,var));
//				} else if (forall.type instanceof Type.EffectiveMap) {
//					// TODO
//				}
				
				// FIXME: assume loop invariant?
			} else if(code instanceof Code.Loop) {
				Code.Loop loop = (Code.Loop) code; 
				int end = findLabel(i,loop.target,body);
				scopes.add(new LoopScope(loop,end));
				// FIXME: assume loop invariant?
				// FIXME: assume condition?
			} else if(code instanceof Code.Return) {
				// we don't need to do anything for a return!
				break;
			} else {
				transform(entry, assumes, branch);
			}
		}
		return branch;
	}
	
//	private static WFormula exitScope(WFormula constraint,
//			int[] environment, ArrayList<Scope> scopes, int pc) {
//		
//		while (!scopes.isEmpty() && top(scopes).end <= pc) {
//			// yes, we're exiting a scope
//			Scope scope = pop(scopes);
//			
//			if(scope instanceof LoopScope) {
//				LoopScope lscope = (LoopScope) scope;
//
//				// trash modified variables
//				for (int register : lscope.loop.modifiedOperands) {
//					environment[register] = environment[register] + 1;
//				}
//				if(lscope instanceof ForScope) {
//					ForScope fscope = (ForScope) lscope;
//					// existing for all scope so existentially quantify generated
//					// formula
//					WVariable var = fscope.var;
//					// Split for the formula into those bits which need to be
//					// quantified, and those which don't
//					Pair<WFormula, WFormula> split = splitFormula(var.name(),
//							constraint);
//					
//					if(pc == fscope.end) { 						
//						constraint = WFormulas.and(
//								split.second(),
//								new WBoundedForall(true, var, fscope.src, split
//										.first()));
//					} else {
//						constraint = WFormulas.and(
//								split.second(),
//								new WBoundedForall(false, var, fscope.src, split
//										.first().not()));						
//					}
//				}
//			}			
//		}
//		
//		return constraint;
//	}
	
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
	protected void transform(Block.Entry entry, boolean assume, Branch branch) {
		Code code = entry.code;		
		try {
			if(code instanceof Code.Assert) {
				transform((Code.Assert)code,entry,assume,branch);
			} else if(code instanceof Code.BinArithOp) {
				transform((Code.BinArithOp)code,entry,branch);
			} else if(code instanceof Code.Convert) {
				transform((Code.Convert)code,entry,branch);
			} else if(code instanceof Code.Const) {
				transform((Code.Const)code,entry,branch);
			} else if(code instanceof Code.Debug) {
				// skip
			} else if(code instanceof Code.FieldLoad) {
				transform((Code.FieldLoad)code,entry,branch);			
			} else if(code instanceof Code.IndirectInvoke) {
				transform((Code.IndirectInvoke)code,entry,branch);
			} else if(code instanceof Code.Invoke) {
				transform((Code.Invoke)code,entry,branch);
			} else if(code instanceof Code.Invert) {
				transform((Code.Invert)code,entry,branch);
			} else if(code instanceof Code.Label) {
				// skip			
			} else if(code instanceof Code.BinListOp) {
				transform((Code.BinListOp)code,entry,branch);
			} else if(code instanceof Code.LengthOf) {
				transform((Code.LengthOf)code,entry,branch);
			} else if(code instanceof Code.SubList) {
				transform((Code.SubList)code,entry,branch);
			} else if(code instanceof Code.IndexOf) {
				transform((Code.IndexOf)code,entry,branch);
			} else if(code instanceof Code.Move) {
				transform((Code.Move)code,entry,branch);
			} else if(code instanceof Code.Assign) {
				transform((Code.Assign)code,entry,branch);
			} else if(code instanceof Code.Update) {
				transform((Code.Update)code,entry,branch);
			} else if(code instanceof Code.NewMap) {
				transform((Code.NewMap)code,entry,branch);
			} else if(code instanceof Code.NewList) {
				transform((Code.NewList)code,entry,branch);
			} else if(code instanceof Code.NewRecord) {
				transform((Code.NewRecord)code,entry,branch);
			} else if(code instanceof Code.NewSet) {
				transform((Code.NewSet)code,entry,branch);
			} else if(code instanceof Code.NewTuple) {
				transform((Code.NewTuple)code,entry,branch);
			} else if(code instanceof Code.UnArithOp) {
				transform((Code.UnArithOp)code,entry,branch);
			} else if(code instanceof Code.Dereference) {
				transform((Code.Dereference)code,entry,branch);
			} else if(code instanceof Code.Nop) {
				// skip			
			} else if(code instanceof Code.BinSetOp) {
				transform((Code.BinSetOp)code,entry,branch);
			} else if(code instanceof Code.BinStringOp) {
				transform((Code.BinStringOp)code,entry,branch);
			} else if(code instanceof Code.SubString) {
				transform((Code.SubString)code,entry,branch);
			} else if(code instanceof Code.NewObject) {
				transform((Code.NewObject)code,entry,branch);
			} else if(code instanceof Code.Throw) {
				transform((Code.Throw)code,entry,branch);
			} else if(code instanceof Code.TupleLoad) {
				transform((Code.TupleLoad)code,entry,branch);
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
	}
	
	protected void transform(Code.Assert code, Block.Entry entry,
			boolean assume, Branch branch) {
		// At this point, what we do is invert the condition being asserted and
		// check that it is unsatisfiable.
		int test = buildTest(code.op, entry, code.leftOperand, code.rightOperand, branch);
		
		if (assume) {
			branch.assume(test);			 
		} else if(!branch.assertTrue(test)){
			syntaxError(code.msg,filename,entry);
		}		
	}
	
	protected void transform(Code.BinArithOp code, Block.Entry entry,
			 Branch branch) {
		int lhs = branch.read(code.leftOperand);
		int rhs = branch.read(code.rightOperand);
		int result;
		
		switch(code.kind) {
		case ADD:
			result = Sum(branch.automaton, lhs, rhs);
			break;
		case SUB:
			result = Sum(branch.automaton, lhs, Neg(branch.automaton, rhs));
			break;
		case MUL:
			result = Mul(branch.automaton, lhs, rhs);
			break;
		case DIV:
			result = Div(branch.automaton, lhs, rhs);			
			break;	
		default:
			internalFailure("unknown binary operator",filename,entry);
			return;
		}
		
		branch.write(code.target,result);
	}

	protected void transform(Code.Convert code, Block.Entry entry,
			Branch branch) {
		int result = branch.read(code.operand);
		branch.write(code.target, result);
	}

	protected void transform(Code.Const code, Block.Entry entry,
			 Branch branch) {
		int rhs = convert(code.constant, entry, branch);
		branch.write(code.target, rhs);
	}

	protected void transform(Code.FieldLoad code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.IndirectInvoke code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.Invoke code, Block.Entry entry,
			 Branch branch)
			throws Exception {
		
		// first, take arguments off the stack
//		Type.FunctionOrMethod ft = code.type;
//		List<Type> ft_params = code.type.params();
//		ArrayList<WExpr> args = new ArrayList<WExpr>();
//		HashMap<WExpr,WExpr> binding = new HashMap<WExpr,WExpr>();
//		int[] code_operands = code.operands;
//		for(int i=0;i!=code_operands.length;++i) {
//			WExpr arg = branch.read(code_operands[i],environment);
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
//			branch.write(code.target, rhs, environment, constraint);
//		}
//		
//		return constraint;
		// TODO
	}

	protected void transform(Code.Invert code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.BinListOp code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.LengthOf code, Block.Entry entry,
			 Branch branch) {
		int src = branch.read(code.operand);
		int result = LengthOf(branch.automaton, src);
		branch.write(code.target, result);
	}

	protected void transform(Code.SubList code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.IndexOf code, Block.Entry entry,
			 Branch branch) {
		int src = branch.read(code.leftOperand);
		int idx = branch.read(code.rightOperand);
		int result = IndexOf(branch.automaton, src, idx);
		branch.write(code.target, result);
	}

	protected void transform(Code.Move code, Block.Entry entry,
			Branch branch) {
		branch.write(code.target, branch.read(code.operand));
	}
	
	protected void transform(Code.Assign code, Block.Entry entry,
			Branch branch) {
		branch.write(code.target, branch.read(code.operand));
	}

	protected void transform(Code.Update code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.NewMap code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.NewList code, Block.Entry entry,
			Branch branch) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		int result = List(branch.automaton, vals);
		branch.write(code.target, result);
	}

	protected void transform(Code.NewSet code, Block.Entry entry,
			Branch branch) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		int result = Set(branch.automaton, vals);
		branch.write(code.target, result);
	}

	protected void transform(Code.NewRecord code, Block.Entry entry,
			 Branch branch) {
		Type.Record type = code.type;
		ArrayList<String> fields = new ArrayList<String>(type.fields().keySet());
		// TODO
	}

	protected void transform(Code.NewTuple code, Block.Entry entry,
			 Branch branch) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		int result = Tuple(branch.automaton, vals);
		branch.write(code.target, result);		
	}

	protected void transform(Code.UnArithOp code, Block.Entry entry,
			 Branch branch) {
		if(code.kind == Code.UnArithKind.NEG) {
			int result = Neg(branch.automaton, branch.read(code.operand));
			branch.write(code.target, result);
		} else {
			// TODO
		}
	}

	protected void transform(Code.Dereference code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.BinSetOp code, Block.Entry entry,
			 Branch branch) {
//		WVariable lhs = branch.read(code.leftOperand, environment);
//		WVariable rhs = branch.read(code.rightOperand, environment);
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
//			constraint = WFormulas.and(branch.automaton, WSets.subsetEq(lhs, target), WSets
//					.subsetEq(rhs, target), new WBoundedForall(true, vars, allc));
//			break;
//		}
//		case DIFFERENCE: {
//			Automaton left = new WBoundedForall(true, vars, WFormulas.and(WSets
//					.subsetEq(sc, lhs), WSets.subsetEq(sc, rhs).not()));
//
//			constraint = WFormulas
//					.and(branch.automaton, WSets.subsetEq(lhs, target), left);		
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
//					.and(branch.automaton, left, right, WSets.subsetEq(target, lhs), WSets.subsetEq(target, rhs));
//			break;
//			default:
//				internalFailure("missing support for left/right set operations",filename,entry);
//		}
//
//		return constraint;
		// TODO
	}
	
	protected void transform(Code.BinStringOp code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.SubString code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.NewObject code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.Throw code, Block.Entry entry,
			 Branch branch) {
		// TODO
	}

	protected void transform(Code.TupleLoad code, Block.Entry entry,
			 Branch branch) {
		int src = branch.read(code.operand);
		int idx = branch.automaton.add(new Automaton.Int(code.index));
		int result = IndexOf(branch.automaton, src, idx);
		branch.write(code.target, result);		
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
	private int convert(wyil.lang.Constant value, SyntacticElement elem, Branch branch) {
		Automaton automaton = branch.automaton;
		
		if(value instanceof wyil.lang.Constant.Bool) {
			wyil.lang.Constant.Bool b = (wyil.lang.Constant.Bool) value;
			return b.value ? automaton.add(True) : automaton.add(False);
		} else if(value instanceof wyil.lang.Constant.Byte) {
			wyil.lang.Constant.Byte v = (wyil.lang.Constant.Byte) value;
			return Num(branch.automaton, v.value);
		} else if(value instanceof wyil.lang.Constant.Char) {
			wyil.lang.Constant.Char v = (wyil.lang.Constant.Char) value;
			// Simple, but mostly good translation
			return Num(branch.automaton, v.value);
		} else if(value instanceof wyil.lang.Constant.Map) {
			return automaton.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.FunctionOrMethod) {
			return automaton.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.Integer) {
			wyil.lang.Constant.Integer v = (wyil.lang.Constant.Integer) value;
			return Num(branch.automaton, v.value);
		} else if(value instanceof wyil.lang.Constant.Null) {
			return automaton.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.List) {
			Constant.List vl = (Constant.List) value;
			int[] vals = new int[vl.values.size()];
			for(int i=0;i!=vals.length;++i) {				
				vals[i] = convert(vl.values.get(i),elem,branch);
			}
			return List(branch.automaton, vals);
		} else if(value instanceof wyil.lang.Constant.Set) {
			Constant.Set vs = (Constant.Set) value;			
			int[] vals = new int[vs.values.size()];
			int i=0;
			for(Constant c : vs.values) {				
				vals[i++] = convert(c,elem,branch);
			}
			return Set(branch.automaton,vals);
		} else if(value instanceof wyil.lang.Constant.Rational) {
			wyil.lang.Constant.Rational v = (wyil.lang.Constant.Rational) value;
			BigRational br = v.value;
			return automaton.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.Record) {
			Constant.Record vt = (Constant.Record) value;
			return automaton.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.Strung) {
			return automaton.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.Tuple) {
			Constant.Tuple vt = (Constant.Tuple) value;
			return automaton.add(False); // TODO
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
	private int buildTest(Code.Comparator op,
			SyntacticElement elem, int leftOperand, int rightOperand,
			Branch branch) {
		int lhs = branch.read(leftOperand);
		int rhs = branch.read(rightOperand);
		
		switch(op) {
		case EQ:
			return Equals(branch.automaton, lhs, rhs);
		case NEQ:
			return Not(branch.automaton, Equals(branch.automaton, lhs, rhs));
		case GTEQ:
			return Or(branch.automaton,LessThan(branch.automaton, rhs, lhs),Equals(branch.automaton, rhs, lhs));
		case GT:
			return LessThan(branch.automaton, rhs, lhs);
		case LTEQ:
			// TODO: investigate whether better to represent LessThanEq explcitly in constraint solver
			return Or(branch.automaton,LessThan(branch.automaton, lhs, rhs),Equals(branch.automaton, lhs, rhs));
		case LT:
			return LessThan(branch.automaton, lhs, rhs);
		case SUBSET:
			return SubSet(branch.automaton, lhs, rhs);
		case SUBSETEQ:
			// TODO: investigate whether better to represent SubSetEq explcitly in constraint solver
			return Or(branch.automaton,Equals(branch.automaton, lhs, rhs),SubSet(branch.automaton, lhs, rhs));
		case ELEMOF:
			return ElementOf(branch.automaton, lhs, rhs);
		default:
			internalFailure("unknown comparator (" + op + ")",filename,elem);
			return -1;
		}
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
