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
import wyone.util.BigRational;
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
	
	/**
	 * Enables debugging information to be printed.
	 */
	private boolean debug = getDebug();
		
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
	
	public static String describeDebug() {
		return "Enable/disable debugging information";
	}
	
	public static boolean getDebug() {
		return false; // default value
	}
	
	public void setDebug(boolean flag) {
		this.debug = flag;
	}
	
	public static String describeMaxSteps() {
		return "Set maximum number of steps constraint solver can apply for a given assertion";
	}
	
	public static long getMaxSteps() {
		return ConstraintSolver.MAX_STEPS;
	}
	
	public void setMaxSteps(long steps) {		
		ConstraintSolver.MAX_STEPS = steps;
	}
	
	public void setMaxSteps(int steps) {		
		ConstraintSolver.MAX_STEPS = steps;
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
		if(debug) {
			System.err.println("============================================");
			Type.FunctionOrMethod fmt = method.type();
			String paramString = fmt.params().toString();
			paramString = paramString.substring(1,paramString.length()-1);
			if(method.type() instanceof Type.Function) {
				System.err.println("FUNCTION: " + fmt.ret() + " " + method.name() + "(" + paramString + ")");
			} else {
				System.err.println("METHOD: " + fmt.ret() + " " + method.name() + "(" + paramString + ")");
			}
		}
		Type.FunctionOrMethod fmm = method.type();
		int paramStart = 0;
		for(int i=paramStart;i!=fmm.params().size();++i) {
			Type paramType = fmm.params().get(i);
			
			// FIXME: add type information
			
//			WVariable pv = new WVariable(i + "$" + 0);
//			constraint = WFormulas.and(branch.automaton(),
//					WTypes.subtypeOf(pv, convert(paramType)));
		}
		
		Block body = methodCase.body();
		Block precondition = methodCase.precondition();				
		
		VerificationBranch master = new VerificationBranch("", new Automaton(
				ConstraintSolver.SCHEMA), body);
		
		if(precondition != null) {
			VerificationBranch precond = new VerificationBranch("$",master.automaton(),precondition);
			int constraint = transform(true,precond);
		} 
		
		transform(false,master);
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
	 * Transform a given branch into a set of constraints (stored in the
	 * automaton) which are known to hold at the end of that branch.
	 * 
	 * @param assumes
	 * @param branch
	 * @return
	 */
	protected int transform(boolean assumes,
			VerificationBranch branch) {
		ArrayList<VerificationBranch> branches = new ArrayList<VerificationBranch>();

		// take initial branch
		transform(assumes, branch, branches);

		// continue any resulting branches
		while (!branches.isEmpty()) {
			int last = branches.size() - 1;
			VerificationBranch b = branches.get(last);
			branches.remove(last);
			transform(assumes, b, branches);
			branch.join(b);
		}

		return branch.constraints();
	}
	
	protected VerificationBranch transform(boolean assumes,
			VerificationBranch branch, ArrayList<VerificationBranch> branches) {
	
		Automaton constraint = branch.automaton();				
		ArrayList<Scope> scopes = null;
				
		do {	
			//constraint = exitScope(constraint,environment,scopes,i);
			
			Block.Entry entry = branch.entry();			
			Code code = entry.code;
			
			if(code instanceof Code.Goto) {
				Code.Goto g = (Code.Goto) code;
				branch.goTo(g.target);					
			} else if(code instanceof Code.If) {
				Code.If ifgoto = (Code.If) code;
				int test = buildTest(ifgoto.op, entry, ifgoto.leftOperand,
						ifgoto.rightOperand, branch);
				VerificationBranch trueBranch = branch.fork();
				trueBranch.goTo(ifgoto.target);
				trueBranch.assume(test);
				branches.add(trueBranch);
				branch.assume(Not(constraint,test));
			} else if(code instanceof Code.IfIs) {
				// TODO: implement me!
			} else if(code instanceof Code.ForAll) {
				Code.ForAll forall = (Code.ForAll) code; 
				//int end = findLabel(branch.pc(),forall.target,body);
				int src = branch.read(forall.sourceOperand);
				int var = branch.read(forall.indexOperand);

//              FIXME!				
//				constraint = WFormulas.and(constraint,
//						WTypes.subtypeOf(var, convert(forall.type.element())));
//				
				branch.assume(ElementOf(branch.automaton(), var, src));
				//scopes.add(new ForScope(forall,end,src,var));
								
				// FIXME: assume loop invariant?
			} else if(code instanceof Code.Loop) {
				Code.Loop loop = (Code.Loop) code; 
				//int end = findLabel(branch.pc(), loop.target, body);
				//scopes.add(new LoopScope(loop,end));
				// FIXME: assume loop invariant?
				// FIXME: assume condition?
			} else if(code instanceof Code.Return) {
				// we don't need to do anything for a return!
				break;
			} else {
				transform(entry, assumes, branch);
			}			
		} while(branch.next());
		
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
	protected void transform(Block.Entry entry, boolean assume, VerificationBranch branch) {
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
			boolean assume, VerificationBranch branch) {
		// At this point, what we do is invert the condition being asserted and
		// check that it is unsatisfiable.
		int test = buildTest(code.op, entry, code.leftOperand,
				code.rightOperand, branch);

		if (assume) {
			branch.assume(test);
		} else if (!branch.assertTrue(test, debug)) {
			syntaxError(code.msg, filename, entry);
		}
	}
	
	protected void transform(Code.BinArithOp code, Block.Entry entry,
			 VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		int lhs = branch.read(code.leftOperand);
		int rhs = branch.read(code.rightOperand);
		int result;
		
		switch(code.kind) {
		case ADD:
			result = Sum(automaton, automaton.add(new Automaton.Real(0)),
					automaton.add(new Automaton.Bag(lhs, rhs)));
			break;
		case SUB:
			result = Sum(automaton, automaton.add(new Automaton.Real(0)), 
					automaton.add(new Automaton.Bag(lhs, Mul(automaton,
							automaton.add(new Automaton.Real(-1)),
							automaton.add(new Automaton.Bag(rhs))))));
			break;
		case MUL:
			result = Mul(automaton, automaton.add(new Automaton.Real(1)),
					automaton.add(new Automaton.Bag(lhs, rhs)));
			break;
		case DIV:
			result = Div(automaton, lhs, rhs);			
			break;	
		default:
			internalFailure("unknown binary operator",filename,entry);
			return;
		}
		
		branch.write(code.target,result);
	}

	protected void transform(Code.Convert code, Block.Entry entry,
			VerificationBranch branch) {
		int result = branch.read(code.operand);
		// TODO: actually implement some or all coercions?
		branch.write(code.target, result);
	}

	protected void transform(Code.Const code, Block.Entry entry,
			 VerificationBranch branch) {
		int rhs = convert(code.constant, entry, branch);
		branch.write(code.target, rhs);
	}

	protected void transform(Code.FieldLoad code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.IndirectInvoke code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.Invoke code, Block.Entry entry, VerificationBranch branch)
			throws Exception {

		// first, maps arguments
		Type.FunctionOrMethod ft = code.type;
		List<Type> ft_params = code.type.params();
		int[] code_operands = code.operands;

		// second, setup return value
		if (code.target != Code.NULL_REG) {
			int target = branch.read(code.target);

			// FIXME: assign target RHS representing function application.

			// now deal with post-condition
			Block postcondition = findPostcondition(code.name, ft, entry);
			if (postcondition != null) {
				// FIXME: 
//				int[] saved = branch.binding;
//				int[] binding = new int[postcondition.numSlots()];
//				binding[0] = target;
//				for (int i = 1; i != code_operands.length; ++i) {
//					binding[i] = branch.read(code_operands[i]);
//				}
//				// FIXME: broken if numSlots exceeds num of arguments
//				branch.binding = binding;
//				branch = transform(true, branch, postcondition);
//				branch.binding = saved;
			}
		}
	}

	protected void transform(Code.Invert code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.BinListOp code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.LengthOf code, Block.Entry entry,
			 VerificationBranch branch) {
		int src = branch.read(code.operand);
		int result = LengthOf(branch.automaton(), src);
		branch.write(code.target, result);
	}

	protected void transform(Code.SubList code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.IndexOf code, Block.Entry entry,
			 VerificationBranch branch) {
		int src = branch.read(code.leftOperand);
		int idx = branch.read(code.rightOperand);
		int result = IndexOf(branch.automaton(), src, idx);
		branch.write(code.target, result);
	}

	protected void transform(Code.Move code, Block.Entry entry,
			VerificationBranch branch) {
		branch.write(code.target, branch.read(code.operand));
	}
	
	protected void transform(Code.Assign code, Block.Entry entry,
			VerificationBranch branch) {
		branch.write(code.target, branch.read(code.operand));
	}

	protected void transform(Code.Update code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.NewMap code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.NewList code, Block.Entry entry,
			VerificationBranch branch) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		int result = List(branch.automaton(), vals);
		branch.write(code.target, result);
	}

	protected void transform(Code.NewSet code, Block.Entry entry,
			VerificationBranch branch) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		int result = Set(branch.automaton(), vals);
		branch.write(code.target, result);
	}

	protected void transform(Code.NewRecord code, Block.Entry entry,
			 VerificationBranch branch) {
		Type.Record type = code.type;
		ArrayList<String> fields = new ArrayList<String>(type.fields().keySet());
		// TODO
	}

	protected void transform(Code.NewTuple code, Block.Entry entry,
			 VerificationBranch branch) {
		int[] code_operands = code.operands;
		int[] vals = new int[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		int result = Tuple(branch.automaton(), vals);
		branch.write(code.target, result);		
	}

	protected void transform(Code.UnArithOp code, Block.Entry entry,
			 VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		if(code.kind == Code.UnArithKind.NEG) {
			int result = Mul(automaton, automaton.add(new Automaton.Real(-1)),
					automaton.add(new Automaton.Bag(branch.read(code.operand))));
			branch.write(code.target, result);
		} else {
			// TODO
		}
	}

	protected void transform(Code.Dereference code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.BinSetOp code, Block.Entry entry,
			 VerificationBranch branch) {
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
//			constraint = WFormulas.and(branch.automaton(), WSets.subsetEq(lhs, target), WSets
//					.subsetEq(rhs, target), new WBoundedForall(true, vars, allc));
//			break;
//		}
//		case DIFFERENCE: {
//			Automaton left = new WBoundedForall(true, vars, WFormulas.and(WSets
//					.subsetEq(sc, lhs), WSets.subsetEq(sc, rhs).not()));
//
//			constraint = WFormulas
//					.and(branch.automaton(), WSets.subsetEq(lhs, target), left);		
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
//					.and(branch.automaton(), left, right, WSets.subsetEq(target, lhs), WSets.subsetEq(target, rhs));
//			break;
//			default:
//				internalFailure("missing support for left/right set operations",filename,entry);
//		}
//
//		return constraint;
		// TODO
	}
	
	protected void transform(Code.BinStringOp code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.SubString code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.NewObject code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.Throw code, Block.Entry entry,
			 VerificationBranch branch) {
		// TODO
	}

	protected void transform(Code.TupleLoad code, Block.Entry entry,
			 VerificationBranch branch) {
		int src = branch.read(code.operand);
		int idx = branch.automaton().add(new Automaton.Int(code.index));
		int result = IndexOf(branch.automaton(), src, idx);
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
	
	/**
	 * Convert between a WYIL value and a WYONE value. Basically, this is really
	 * stupid and it would be good for them to be the same.
	 * 
	 * @param value
	 * @return
	 */
	private int convert(wyil.lang.Constant value, SyntacticElement elem, VerificationBranch branch) {
		Automaton automaton = branch.automaton();
		
		if(value instanceof wyil.lang.Constant.Bool) {
			wyil.lang.Constant.Bool b = (wyil.lang.Constant.Bool) value;
			return b.value ? automaton.add(True) : automaton.add(False);
		} else if(value instanceof wyil.lang.Constant.Byte) {
			wyil.lang.Constant.Byte v = (wyil.lang.Constant.Byte) value;
			return Num(branch.automaton(), BigRational.valueOf(v.value));
		} else if(value instanceof wyil.lang.Constant.Char) {
			wyil.lang.Constant.Char v = (wyil.lang.Constant.Char) value;
			// Simple, but mostly good translation
			return Num(branch.automaton(), v.value);
		} else if(value instanceof wyil.lang.Constant.Map) {
			return automaton.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.FunctionOrMethod) {
			return automaton.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.Integer) {
			wyil.lang.Constant.Integer v = (wyil.lang.Constant.Integer) value;
			return Num(branch.automaton(), BigRational.valueOf(v.value));
		} else if(value instanceof wyil.lang.Constant.Rational) {
			wyil.lang.Constant.Rational v = (wyil.lang.Constant.Rational) value;
			wyil.util.BigRational br = v.value;
			return Num(branch.automaton(), new BigRational(br.numerator(),br.denominator()));
		} else if(value instanceof wyil.lang.Constant.Null) {
			return automaton.add(False); // TODO
		} else if(value instanceof wyil.lang.Constant.List) {
			Constant.List vl = (Constant.List) value;
			int[] vals = new int[vl.values.size()];
			for(int i=0;i!=vals.length;++i) {				
				vals[i] = convert(vl.values.get(i),elem,branch);
			}
			return List(branch.automaton(), vals);
		} else if(value instanceof wyil.lang.Constant.Set) {
			Constant.Set vs = (Constant.Set) value;			
			int[] vals = new int[vs.values.size()];
			int i=0;
			for(Constant c : vs.values) {				
				vals[i++] = convert(c,elem,branch);
			}
			return Set(branch.automaton(),vals);
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
			VerificationBranch branch) {
		int lhs = branch.read(leftOperand);
		int rhs = branch.read(rightOperand);
		
		switch(op) {
		case EQ:
			return Equals(branch.automaton(), lhs, rhs);
		case NEQ:
			return Not(branch.automaton(), Equals(branch.automaton(), lhs, rhs));
		case GTEQ:
			return Or(branch.automaton(),LessThan(branch.automaton(), rhs, lhs),Equals(branch.automaton(), rhs, lhs));
		case GT:
			return LessThan(branch.automaton(), rhs, lhs);
		case LTEQ:
			// TODO: investigate whether better to represent LessThanEq explcitly in constraint solver
			return Or(branch.automaton(),LessThan(branch.automaton(), lhs, rhs),Equals(branch.automaton(), lhs, rhs));
		case LT:
			return LessThan(branch.automaton(), lhs, rhs);
		case SUBSET:
			return SubSet(branch.automaton(), lhs, rhs);
		case SUBSETEQ:
			// TODO: investigate whether better to represent SubSetEq explcitly in constraint solver
			return Or(branch.automaton(),Equals(branch.automaton(), lhs, rhs),SubSet(branch.automaton(), lhs, rhs));
		case ELEMOF:
			return ElementOf(branch.automaton(), lhs, rhs);
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
