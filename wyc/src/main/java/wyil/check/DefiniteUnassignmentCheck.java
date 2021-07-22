// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.check;

import static wyil.lang.WyilFile.*;

import wyc.util.ErrorMessages;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.Compiler;
import wyil.util.AbstractFunction;

import java.util.BitSet;

import wycc.lang.Build;
import wycc.lang.SyntacticItem;

/**
 * <p>
 * Responsible for checking that all final variables defined at most once. The
 * algorithm for checking this involves a depth-first search through the
 * control-flow graph of the method. Throughout this, a list of the definetely
 * unassigned variables is maintained. For example:
 * </p>
 *
 * <pre>
 * function abs(int x) -> int:
 * 	if x < 0:
 *     x = -x
 *  return x
 * </pre>
 *
 * <p>
 * In the above example, parameter x is implicitly declared final and may not be
 * reassigned within the body of the function. As another example:
 * </p>
 *
 * <pre>
 * function f(int x) -> int:
 * 	final int y = x
 *  if x < 0:
 *     y = -x
 *  //
 *  return y
 * </pre>
 *
 * <p>
 * Here, variable y is declared final and cannot be reassigned in the true
 * branch of the conditional.
 * </p>
 *
 *
 *
 * @author David J. Pearce
 *
 */
public class DefiniteUnassignmentCheck
		extends AbstractFunction<DefiniteUnassignmentCheck.MaybeAssignedSet, DefiniteUnassignmentCheck.ControlFlow> implements Compiler.Check {

	/**
	 * NOTE: the following is left in place to facilitate testing for the final
	 * parameters RFC. This RFC may not be accepted, in which case this feature
	 * should eventually be removed.
	 */
	private boolean finalParameters = false;

	private boolean status = true;

	public DefiniteUnassignmentCheck(Build.Meter meter) {
		super(meter.fork(DefiniteUnassignmentCheck.class.getSimpleName()));
	}

	@Override
	public boolean check(WyilFile wf) {
		// Only proceed if no errors in earlier stages
		visitModule(wf, null);
		meter.done();
		//
		return status;
	}

	@Override
	public ControlFlow visitExternalUnit(Decl.Unit unit, MaybeAssignedSet dummy) {
		// NOTE: we override this to prevent unnecessarily traversing units
		return null;
	}

	/**
	 * Check a function or method declaration for definite assignment.
	 *
	 * @param declaration
	 * @return
	 */
	@Override
	public ControlFlow visitFunctionOrMethod(Decl.FunctionOrMethod declaration, MaybeAssignedSet dummy) {
		MaybeAssignedSet environment = new MaybeAssignedSet();
		// Definitely assigned variables includes all parameters.
		environment = environment.addAll(declaration.getParameters());
		// Iterate through each statement in the body of the function or method,
		// updating the set of definitely assigned variables as appropriate.
		visitStatement(declaration.getBody(), environment);
		//
		return null;
	}

	/**
	 * Check a function or method declaration for definite assignment.
	 *
	 * @param declaration
	 * @return
	 */
	@Override
	public ControlFlow visitProperty(Decl.Property declaration, MaybeAssignedSet dummy) {
		MaybeAssignedSet environment = new MaybeAssignedSet();
		// Definitely assigned variables includes all parameters.
		environment = environment.addAll(declaration.getParameters());
		//
		return null;
	}

	@Override
	public ControlFlow visitVariable(Decl.Variable decl, MaybeAssignedSet environment) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ControlFlow visitStaticVariable(Decl.StaticVariable decl, MaybeAssignedSet environment) {
		//
		return new ControlFlow(environment, null);
	}

	@Override
	public ControlFlow visitType(Decl.Type declaration, MaybeAssignedSet dummy) {
		MaybeAssignedSet environment = new MaybeAssignedSet();
		//
		environment = environment.add(declaration.getVariableDeclaration());
		//
		return null;
	}

	/**
	 * Check that all variables used in a given list of statements are definitely
	 * assigned. Furthermore, update the set of definitely assigned variables to
	 * include any which are definitely assigned at the end of these statements.
	 *
	 * @param block
	 *            The list of statements to visit.
	 * @param environment
	 *            The set of variables which are definitely assigned.
	 */
	@Override
	public ControlFlow visitBlock(Stmt.Block block, MaybeAssignedSet environment) {
		MaybeAssignedSet nextEnvironment = environment;
		MaybeAssignedSet breakEnvironment = null;
		for (int i = 0; i != block.size(); ++i) {
			Stmt s = block.get(i);
			ControlFlow nf = visitStatement(s, nextEnvironment);
			nextEnvironment = nf.nextEnvironment;
			breakEnvironment = join(breakEnvironment, nf.breakEnvironment);
			// NOTE: following can arise when block contains unreachable code.
			if(nextEnvironment == null) {
				break;
			}
		}
		return new ControlFlow(nextEnvironment, breakEnvironment);
	}

	@Override
	public ControlFlow visitAssert(Stmt.Assert stmt, MaybeAssignedSet environment) {
		return new ControlFlow(environment, null);
	}

	@Override
	public ControlFlow visitAssign(Stmt.Assign stmt, MaybeAssignedSet environment) {
		// left-hand side
		for (LVal lval : stmt.getLeftHandSide()) {
			visitLVal(lval, environment);
		}
		// Update environment as necessary
		for (Expr lval : stmt.getLeftHandSide()) {
			if (lval instanceof Expr.VariableAccess) {
				Expr.VariableAccess lv = (Expr.VariableAccess) lval;
				environment = environment.add(lv.getVariableDeclaration());
			}
		}
		//
		return new ControlFlow(environment, null);
	}

	public void visitLVal(LVal lval, MaybeAssignedSet environment) {
		switch (lval.getOpcode()) {
		case EXPR_variablecopy:
		case EXPR_variablemove: {
			visitVariableAssignment((Expr.VariableAccess) lval, environment);
			break;
		}
		case EXPR_staticvariable: {
			visitStaticVariableAssignment((Expr.StaticVariableAccess) lval, environment);
			break;
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess aa = (Expr.RecordAccess) lval;
			visitLVal((LVal) aa.getOperand(), environment);
			break;
		}
		case EXPR_arrayaccess:
		case EXPR_arrayborrow: {
			Expr.ArrayAccess aa = (Expr.ArrayAccess) lval;
			visitLVal((LVal) aa.getFirstOperand(), environment);
			break;
		}
		case EXPR_tupleinitialiser: {
			Expr.TupleInitialiser ti = (Expr.TupleInitialiser) lval;
			Tuple<Expr> operands = ti.getOperands();
			for(int i=0;i!=operands.size();++i) {
				visitLVal((LVal) operands.get(i), environment);
			}
			break;
		}
		case EXPR_dereference:
		case EXPR_fielddereference:
			// NOTE: don't need to handle these cases
			break;
		default:
			throw new UnsupportedOperationException("unknown lval (" + lval.getClass().getName() + ")");
		}
	}

	public void visitVariableAssignment(Expr.VariableAccess lval, MaybeAssignedSet environment) {
		Decl.Variable var = lval.getVariableDeclaration();
		if (finalParameters && isParameter(var)) {
			syntaxError(lval,PARAMETER_REASSIGNED);
		} else if (isFinal(var) && environment.contains(var)) {
			syntaxError(lval, FINAL_VARIABLE_REASSIGNED);
		}
	}

	public void visitStaticVariableAssignment(Expr.StaticVariableAccess lval, MaybeAssignedSet environment) {
		// Check whether this declaration was resolved or not.
		Decl.Link<Decl.StaticVariable> nl = lval.getLink();
		if (nl.isResolved()) {
			Decl.StaticVariable var = nl.getTarget();
			if (isFinal(var)) {
				syntaxError(lval, FINAL_VARIABLE_REASSIGNED);
			}
		}
	}

	public boolean isParameter(Decl.Variable var) {
		// FIXME: this might be a little inefficient
		Decl.FunctionOrMethod parent = var.getAncestor(Decl.FunctionOrMethod.class);
		Tuple<Decl.Variable> parameters = parent.getParameters();
		for (int i = 0; i != parameters.size(); ++i) {
			if (parameters.get(i) == var) {
				return true;
			}
		}
		return false;
	}

	public boolean isFinal(Decl.Variable var) {
		return var.getModifiers().match(Modifier.Final.class) != null;
	}

	@Override
	public ControlFlow visitAssume(Stmt.Assume stmt, MaybeAssignedSet environment) {
		return new ControlFlow(environment, null);
	}

	@Override
	public ControlFlow visitBreak(Stmt.Break stmt, MaybeAssignedSet environment) {
		return new ControlFlow(null, environment);
	}

	@Override
	public ControlFlow visitContinue(Stmt.Continue stmt, MaybeAssignedSet environment) {
		// Here we can just treat a continue in the same way as a return
		// statement. It makes no real difference.
		return new ControlFlow(null, null);
	}

	@Override
	public ControlFlow visitDebug(Stmt.Debug stmt, MaybeAssignedSet environment) {
		return new ControlFlow(environment, null);
	}

	@Override
	public ControlFlow visitDoWhile(Stmt.DoWhile stmt, MaybeAssignedSet environment) {
		//
		ControlFlow flow = visitBlock(stmt.getBody(), environment);
		//
		environment = join(flow.nextEnvironment, flow.breakEnvironment);
		//
		return new ControlFlow(environment, null);
	}

	@Override
	public ControlFlow visitFail(Stmt.Fail stmt, MaybeAssignedSet environment) {
		return new ControlFlow(null, null);
	}

	@Override
	public ControlFlow visitFor(Stmt.For stmt, MaybeAssignedSet environment) {
		// Mark index variable as assigned
		environment = environment.add(stmt.getVariable());
		return visitBlock(stmt.getBody(), environment);
	}

	@Override
	public ControlFlow visitIfElse(Stmt.IfElse stmt, MaybeAssignedSet environment) {
		//
		ControlFlow left = visitBlock(stmt.getTrueBranch(), environment);
		ControlFlow right;
		if (stmt.hasFalseBranch()) {
			right = visitBlock(stmt.getFalseBranch(), environment);
		} else {
			right = new ControlFlow(environment, null);
		}
		// Now, merge all generated control-flow paths together
		return left.merge(right);
	}

	@Override
	public ControlFlow visitInitialiser(Stmt.Initialiser stmt, MaybeAssignedSet environment) {
		if(stmt.hasInitialiser()) {
			for (Decl.Variable decl : stmt.getVariables()) {
				environment = environment.add(decl);
			}
		}
		return new ControlFlow(environment, null);
	}

	@Override
	public ControlFlow visitInvoke(Expr.Invoke stmt, MaybeAssignedSet environment) {
		return new ControlFlow(environment, null);
	}

	@Override
	public ControlFlow visitIndirectInvoke(Expr.IndirectInvoke stmt, MaybeAssignedSet environment) {
		return new ControlFlow(environment, null);
	}

	@Override
	public ControlFlow visitNamedBlock(Stmt.NamedBlock stmt, MaybeAssignedSet environment) {
		return visitBlock(stmt.getBlock(), environment);
	}

	@Override
	public ControlFlow visitReturn(Stmt.Return stmt, MaybeAssignedSet environment) {
		return new ControlFlow(null, null);
	}

	@Override
	public ControlFlow visitSkip(Stmt.Skip stmt, MaybeAssignedSet environment) {
		return new ControlFlow(environment, null);
	}

	@Override
	public ControlFlow visitSwitch(Stmt.Switch stmt, MaybeAssignedSet environment) {
		MaybeAssignedSet caseEnvironment = null;
		MaybeAssignedSet breakEnvironment = null;
		//
		for (Stmt.Case c : stmt.getCases()) {
			ControlFlow cf = visitBlock(c.getBlock(), environment);
			caseEnvironment = join(caseEnvironment, cf.nextEnvironment);
			breakEnvironment = join(breakEnvironment, cf.breakEnvironment);
		}
		//
		return new ControlFlow(caseEnvironment, breakEnvironment);
	}

	@Override
	public ControlFlow visitWhile(Stmt.While stmt, MaybeAssignedSet environment) {
		return visitBlock(stmt.getBody(), environment);
	}

	@Override
	public ControlFlow visitExpression(Expr expr, MaybeAssignedSet environment) {
		// NOTE: following is here to help prevent unnecessary work traversing
		// expressions when we don't need to.
		throw new UnsupportedOperationException();
	}

	@Override
	public ControlFlow visitType(Type expr, MaybeAssignedSet environment) {
		// NOTE: following is here to help prevent unnecessary work traversing
		// expressions when we don't need to.
		throw new UnsupportedOperationException();
	}

	public class ControlFlow {
		/**
		 * The set of definitely assigned variables on this path which fall through to
		 * the next logical statement.
		 */
		public final MaybeAssignedSet nextEnvironment;

		/**
		 * The set of definitely assigned variables on this path which are on the
		 * control-flow path caused by a break statement.
		 */
		public final MaybeAssignedSet breakEnvironment;

		public ControlFlow(MaybeAssignedSet nextEnvironment, MaybeAssignedSet breakEnvironment) {
			this.nextEnvironment = nextEnvironment;
			this.breakEnvironment = breakEnvironment;
		}

		public ControlFlow merge(ControlFlow other) {
			MaybeAssignedSet n = join(nextEnvironment, other.nextEnvironment);
			MaybeAssignedSet b = join(breakEnvironment, other.breakEnvironment);
			return new ControlFlow(n, b);
		}
	}

	/**
	 * join two sets of definitely assigned variables together. This allows for the
	 * possibility that either or both arguments are null. The join itself is
	 * corresponds to the intersection of both sets.
	 *
	 * @param left
	 * @param right
	 * @return
	 */
	public static MaybeAssignedSet join(MaybeAssignedSet left, MaybeAssignedSet right) {
		if (left == null && right == null) {
			return null;
		} else if (left == null) {
			return right;
		} else if (right == null) {
			return left;
		} else {
			return left.join(right);
		}
	}

	/**
	 * A simple class representing an immutable set of definitely assigned
	 * variables.
	 *
	 * @author David J. Pearce
	 *
	 */
	public class MaybeAssignedSet {
		// FIXME: this could be made for efficient for handling files with a large
		// number of components. Specifically, by including some notion of relative
		// offset.
		private BitSet variables;

		public MaybeAssignedSet() {
			this.variables = new BitSet();
		}

		public MaybeAssignedSet(MaybeAssignedSet defs) {
			this.variables = new BitSet();
			this.variables.or(defs.variables);
		}

		public boolean contains(Decl.Variable var) {
			return variables.get(var.getIndex());
		}

		/**
		 * Add a variable to the set of definitely assigned variables, producing an
		 * updated set.
		 *
		 * @param var
		 * @return
		 */
		public MaybeAssignedSet add(Decl.Variable var) {
			MaybeAssignedSet r = new MaybeAssignedSet(this);
			r.variables.set(var.getIndex());
			return r;
		}

		/**
		 * Add a variable to the set of definitely assigned variables, producing an
		 * updated set.
		 *
		 * @param var
		 * @return
		 */
		public MaybeAssignedSet addAll(Tuple<Decl.Variable> vars) {
			MaybeAssignedSet r = new MaybeAssignedSet(this);
			for (int i = 0; i != vars.size(); ++i) {
				Decl.Variable var = vars.get(i);
				r.variables.set(var.getIndex());
			}
			return r;
		}

		/**
		 * Join two sets together, where the result contains a variable if it maybe
		 * assigned on either branch.
		 *
		 * @param other
		 * @return
		 */
		public MaybeAssignedSet join(MaybeAssignedSet other) {
			MaybeAssignedSet r = new MaybeAssignedSet(this);
			r.variables.or(other.variables);
			return r;
		}

		/**
		 * Useful for debugging
		 */
		@Override
		public String toString() {
			return variables.toString();
		}
	}

	private void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context);
	}
}
