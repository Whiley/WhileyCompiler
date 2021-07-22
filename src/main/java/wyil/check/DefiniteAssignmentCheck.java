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

import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.util.AbstractFunction;

import java.util.BitSet;

import wycc.lang.*;
import wyc.util.ErrorMessages;

/**
 * <p>
 * Responsible for checking that all variables are defined before they are used.
 * The algorithm for checking this involves a depth-first search through the
 * control-flow graph of the method. Throughout this, a list of the defined
 * variables is maintained. For example:
 * </p>
 *
 * <pre>
 * function f() -> int:
 * 	int z
 * 	return z + 1
 * </pre>
 *
 * <p>
 * In the above example, variable z is used in the return statement before it
 * has been defined any value. This is considered a syntax error in whiley.
 * </p>
 *
 *
 * @author David J. Pearce
 *
 */
public class DefiniteAssignmentCheck
		extends AbstractFunction<DefiniteAssignmentCheck.DefinitelyAssignedSet, DefiniteAssignmentCheck.ControlFlow>
		implements Compiler.Check {
	private boolean status = true;

	public DefiniteAssignmentCheck(Build.Meter meter) {
		super(meter.fork(DefiniteAssignmentCheck.class.getSimpleName()));
	}

	@Override
	public boolean check(WyilFile wf) {
		//
		visitModule(wf, null);
		//
		meter.done();
		//
		return status;
	}

	@Override
	public ControlFlow visitExternalUnit(Decl.Unit unit, DefinitelyAssignedSet dummy) {
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
	public ControlFlow visitFunctionOrMethod(Decl.FunctionOrMethod declaration, DefinitelyAssignedSet dummy) {
		DefinitelyAssignedSet environment = new DefinitelyAssignedSet();
		// Definitely assigned variables includes all parameters.
		environment = environment.addAll(declaration.getParameters());
		// Preconditions can only refer to parameters
		visitExpressions(declaration.getRequires(),environment);
		// Postconditions can refer to parameterts and returns
		{
			DefinitelyAssignedSet postEnvironment = environment.addAll(declaration.getReturns());
			visitExpressions(declaration.getEnsures(),postEnvironment);
		}
		// Iterate through each statement in the body of the function or method,
		// updating the set of definitely assigned variables as appropriate.
		visitStatement(declaration.getBody(),environment);
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
	public ControlFlow visitProperty(Decl.Property declaration, DefinitelyAssignedSet dummy) {
		DefinitelyAssignedSet environment = new DefinitelyAssignedSet();
		// Definitely assigned variables includes all parameters.
		environment = environment.addAll(declaration.getParameters());
		// Iterate through each statement in the body of the function or method,
		// updating the set of definitely assigned variables as appropriate.
		visitExpressions(declaration.getInvariant(),environment);
		//
		return null;
	}

	@Override
	public ControlFlow visitStaticVariable(Decl.StaticVariable declaration, DefinitelyAssignedSet dummy) {
		DefinitelyAssignedSet environment = new DefinitelyAssignedSet();
		//
		visitExpression(declaration.getInitialiser(), environment);
		//
		return null;
	}

	@Override
	public ControlFlow visitType(Decl.Type declaration, DefinitelyAssignedSet dummy) {
		DefinitelyAssignedSet environment = new DefinitelyAssignedSet();
		//
		environment = environment.add(declaration.getVariableDeclaration());
		visitExpressions(declaration.getInvariant(), environment);
		//
		return null;
	}

	/**
	 * Check that all variables used in a given list of statements are
	 * definitely assigned. Furthermore, update the set of definitely assigned
	 * variables to include any which are definitely assigned at the end of
	 * these statements.
	 *
	 * @param block
	 *            The list of statements to visit.
	 * @param environment
	 *            The set of variables which are definitely assigned.
	 */
	@Override
	public ControlFlow visitBlock(Stmt.Block block, DefinitelyAssignedSet environment) {
		DefinitelyAssignedSet nextEnvironment = environment;
		DefinitelyAssignedSet breakEnvironment = null;
		for(int i=0;i!=block.size();++i) {
			Stmt s = block.get(i);
			ControlFlow nf = visitStatement(s, nextEnvironment);
			nextEnvironment = nf.nextEnvironment;
			breakEnvironment = join(breakEnvironment,nf.breakEnvironment);
			// NOTE: following can arise when block contains unreachable code.
			if(nextEnvironment == null) {
				break;
			}
		}
		return new ControlFlow(nextEnvironment,breakEnvironment);
	}

	@Override
	public ControlFlow visitAssert(Stmt.Assert stmt, DefinitelyAssignedSet environment) {
		visitExpression(stmt.getCondition(), environment);
		return new ControlFlow(environment,null);
	}

	@Override
	public ControlFlow visitAssign(Stmt.Assign stmt, DefinitelyAssignedSet environment) {
		// left-hand side
		visitLValExpressions(stmt.getLeftHandSide(), environment);
		// right-hand side
		visitExpressions(stmt.getRightHandSide(), environment);
		// Update environment as necessary
		environment = visitAssignedLVals(stmt.getLeftHandSide(),environment);
		// Done
		return new ControlFlow(environment, null);
	}

	private void visitLValExpressions(Tuple<? extends Expr> lvals, DefinitelyAssignedSet environment) {
		for (Expr lval : lvals) {
			if (lval instanceof Expr.VariableAccess) {
				// Skip local variables since they are being assigned and, otherwise, could
				// raise an error.
			} else if(lval instanceof Expr.TupleInitialiser) {
				Expr.TupleInitialiser e = (Expr.TupleInitialiser) lval;
				visitLValExpressions(e.getOperands(), environment);
			} else {
				visitExpression(lval, environment);
			}
		}
	}

	private DefinitelyAssignedSet visitAssignedLVals(Tuple<? extends Expr> lvals, DefinitelyAssignedSet environment) {
		for (Expr lval : lvals) {
			if (lval instanceof Expr.VariableAccess) {
				Expr.VariableAccess lv = (Expr.VariableAccess) lval;
				environment = environment.add(lv.getVariableDeclaration());
			} else if(lval instanceof Expr.TupleInitialiser) {
				Expr.TupleInitialiser e = (Expr.TupleInitialiser) lval;
				environment = visitAssignedLVals(e.getOperands(), environment);
			}
		}
		return environment;
	}

	@Override
	public ControlFlow visitAssume(Stmt.Assume stmt, DefinitelyAssignedSet environment) {
		visitExpression(stmt.getCondition(), environment);
		return new ControlFlow(environment,null);
	}

	@Override
	public ControlFlow visitBreak(Stmt.Break stmt, DefinitelyAssignedSet environment) {
		return new ControlFlow(null,environment);
	}

	@Override
	public ControlFlow visitContinue(Stmt.Continue stmt, DefinitelyAssignedSet environment) {
		// Here we can just treat a continue in the same way as a return
		// statement. It makes no real difference.
		return new ControlFlow(null,null);
	}

	@Override
	public ControlFlow visitDebug(Stmt.Debug stmt, DefinitelyAssignedSet environment) {
		visitExpression(stmt.getOperand(), environment);
		return new ControlFlow(environment,null);
	}

	@Override
	public ControlFlow visitDoWhile(Stmt.DoWhile stmt, DefinitelyAssignedSet environment) {
		//
		ControlFlow flow = visitBlock(stmt.getBody(), environment);
		//
		visitExpression(stmt.getCondition(), flow.nextEnvironment);
		visitExpressions(stmt.getInvariant(), flow.nextEnvironment);
		environment = join(flow.nextEnvironment,flow.breakEnvironment);
		//
		return new ControlFlow(environment,null);
	}

	@Override
	public ControlFlow visitFail(Stmt.Fail stmt, DefinitelyAssignedSet environment) {
		return new ControlFlow(null,null);
	}

	@Override
	public ControlFlow visitFor(Stmt.For stmt, DefinitelyAssignedSet environment) {
		Decl.StaticVariable var = stmt.getVariable();
		visitExpression(var.getInitialiser(), environment);
		environment = environment.add(var);
		visitExpressions(stmt.getInvariant(), environment);
		visitBlock(stmt.getBody(), environment);
		//
		return new ControlFlow(environment,null);
	}

	@Override
	public ControlFlow visitIfElse(Stmt.IfElse stmt, DefinitelyAssignedSet environment) {
		visitExpression(stmt.getCondition(), environment);
		//
		ControlFlow left = visitBlock(stmt.getTrueBranch(), environment);
		if(stmt.hasFalseBranch()) {
			ControlFlow right = visitBlock(stmt.getFalseBranch(), environment);
			// Now, merge all generated control-flow paths together
			return left.merge(right);
		} else {
			return new ControlFlow(environment,null);
		}
	}

	@Override
	public ControlFlow visitInvoke(Expr.Invoke stmt, DefinitelyAssignedSet environment) {
		super.visitInvoke(stmt, environment);
		return new ControlFlow(environment,null);
	}

	@Override
	public ControlFlow visitIndirectInvoke(Expr.IndirectInvoke stmt, DefinitelyAssignedSet environment) {
		super.visitIndirectInvoke(stmt, environment);
		return new ControlFlow(environment,null);
	}

	@Override
	public ControlFlow visitInitialiser(Stmt.Initialiser stmt, DefinitelyAssignedSet environment) {
		if(stmt.hasInitialiser()) {
			visitExpression(stmt.getInitialiser(), environment);
			for (Decl.Variable v : stmt.getVariables()) {
				environment = environment.add(v);
			}
		}
		return new ControlFlow(environment,null);
	}

	@Override
	public ControlFlow visitNamedBlock(Stmt.NamedBlock stmt, DefinitelyAssignedSet environment) {
		return visitBlock(stmt.getBlock(),environment);
	}

	@Override
	public ControlFlow visitReturn(Stmt.Return stmt, DefinitelyAssignedSet environment) {
		if(stmt.hasReturn()) {
			visitExpression(stmt.getReturn(), environment);
		}
		return new ControlFlow(null,null);
	}

	@Override
	public ControlFlow visitSkip(Stmt.Skip stmt, DefinitelyAssignedSet environment) {
		return new ControlFlow(environment,null);
	}

	@Override
	public ControlFlow visitSwitch(Stmt.Switch stmt, DefinitelyAssignedSet environment) {
		DefinitelyAssignedSet caseEnvironment = null;
		DefinitelyAssignedSet breakEnvironment = null;

		visitExpression(stmt.getCondition(), environment);
		//
		boolean hasDefault = false;
		for(Stmt.Case c : stmt.getCases()) {
			ControlFlow cf = visitBlock(c.getBlock(), environment);
			caseEnvironment = join(caseEnvironment,cf.nextEnvironment);
			breakEnvironment = join(breakEnvironment,cf.breakEnvironment);
			if(c.getConditions().size() == 0) {
				hasDefault = true;
			}
		}
		//
		if(hasDefault) {
			// Having a default makes a big difference. Without one, then
			// everything that wasn't defined beforehand remains undefined. So,
			// it's only in the case that there is a default statement that
			// individual case statements can have an effect on the resulting
			// set of definitely assigned variables.
			environment = caseEnvironment;
		}
		//
		return new ControlFlow(environment,breakEnvironment);
	}

	@Override
	public ControlFlow visitWhile(Stmt.While stmt, DefinitelyAssignedSet environment) {
		visitExpression(stmt.getCondition(), environment);
		visitExpressions(stmt.getInvariant(), environment);
		visitBlock(stmt.getBody(), environment);
		//
		return new ControlFlow(environment,null);
	}

	@Override
	public ControlFlow visitLambda(Decl.Lambda expression, DefinitelyAssignedSet environment) {
		// Add lambda parameters to the set of definitely assigned variables.
		environment = environment.addAll(expression.getParameters());
		// Check body of the lambda
		visitExpression(expression.getBody(),environment);
		//
		return null;
	}

	@Override
	public ControlFlow visitUniversalQuantifier(Expr.UniversalQuantifier expression, DefinitelyAssignedSet environment) {
		Tuple<Decl.StaticVariable> parameters = expression.getParameters();
		for(int i=0;i!=parameters.size();++i) {
			Decl.StaticVariable var = parameters.get(i);
			visitExpression(var.getInitialiser(), environment);
			environment = environment.add(var);
		}
		visitExpression(expression.getOperand(), environment);
		return null;
	}

	@Override
	public ControlFlow visitExistentialQuantifier(Expr.ExistentialQuantifier expression, DefinitelyAssignedSet environment) {
		Tuple<Decl.StaticVariable> parameters = expression.getParameters();
		for(int i=0;i!=parameters.size();++i) {
			Decl.StaticVariable var = parameters.get(i);
			visitExpression(var.getInitialiser(), environment);
			environment = environment.add(var);
		}
		visitExpression(expression.getOperand(), environment);
		return null;
	}

	@Override
	public ControlFlow visitVariableAccess(Expr.VariableAccess expression, DefinitelyAssignedSet environment) {
		Decl.Variable vd = expression.getVariableDeclaration();
		if (!environment.contains(vd)) {
			syntaxError(expression, VARIABLE_POSSIBLY_UNITIALISED);
		}
		return null;
	}

	@Override
	public ControlFlow visitType(Type type, DefinitelyAssignedSet environment) {
		// No need to visit types, as these cannot cause definite assignment errors.
		return null;
	}


	public class ControlFlow {
		/**
		 * The set of definitely assigned variables on this path which fall
		 * through to the next logical statement.
		 */
		public final DefinitelyAssignedSet nextEnvironment;

		/**
		 * The set of definitely assigned variables on this path which are on
		 * the control-flow path caused by a break statement.
		 */
		public final DefinitelyAssignedSet breakEnvironment;

		public ControlFlow(DefinitelyAssignedSet nextEnvironment, DefinitelyAssignedSet breakEnvironment) {
			this.nextEnvironment = nextEnvironment;
			this.breakEnvironment = breakEnvironment;
		}

		public ControlFlow merge(ControlFlow other) {
			DefinitelyAssignedSet n = join(nextEnvironment,other.nextEnvironment);
			DefinitelyAssignedSet b = join(breakEnvironment,other.breakEnvironment);
			return new ControlFlow(n,b);
		}
	}

	/**
	 * join two sets of definitely assigned variables together. This allows for
	 * the possibility that either or both arguments are null. The join itself
	 * is corresponds to the intersection of both sets.
	 *
	 * @param left
	 * @param right
	 * @return
	 */
	public static DefinitelyAssignedSet join(DefinitelyAssignedSet left, DefinitelyAssignedSet right) {
		if(left == null && right == null) {
			return null;
		} else if(left == null) {
			return right;
		} else if(right == null) {
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
	public class DefinitelyAssignedSet {
		// FIXME: this could be made for efficient for handling files with a large
		// number of components. Specifically, by including some notion of relative
		// offset.
		private BitSet variables;

		public DefinitelyAssignedSet() {
			this.variables = new BitSet();
		}

		public DefinitelyAssignedSet(DefinitelyAssignedSet defs) {
			this.variables = new BitSet();
			this.variables.or(defs.variables);
		}

		public boolean contains(Decl.Variable var) {
			return variables.get(var.getIndex());
		}

		/**
		 * Add a variable to the set of definitely assigned variables, producing
		 * an updated set.
		 *
		 * @param var
		 * @return
		 */
		public DefinitelyAssignedSet add(Decl.Variable var) {
			DefinitelyAssignedSet r = new DefinitelyAssignedSet(this);
			r.variables.set(var.getIndex());
			return r;
		}

		/**
		 * Add a variable to the set of definitely assigned variables, producing
		 * an updated set.
		 *
		 * @param var
		 * @return
		 */
		public DefinitelyAssignedSet addAll(Tuple<Decl.Variable> vars) {
			DefinitelyAssignedSet r = new DefinitelyAssignedSet(this);
			for(int i=0;i!=vars.size();++i) {
				Decl.Variable var = vars.get(i);
				r.variables.set(var.getIndex());
			}
			return r;
		}

		/**
		 * Join two sets together, where the result contains a variable only if
		 * it is definitely assigned on both branches.
		 *
		 * @param other
		 * @return
		 */
		public DefinitelyAssignedSet join(DefinitelyAssignedSet other) {
			DefinitelyAssignedSet r = new DefinitelyAssignedSet(this);
			r.variables.and(other.variables);
			return r;
		}

		/**
		 * Useful for debugging
		 */
		public String toString(SyntacticHeap src) {
			String r = "";
			for (int i = variables.nextSetBit(0); i != -1; i = variables.nextSetBit(i + 1)) {
				if(r.length() != 0) {
					r = r + ",";
				}
				Decl.Variable v = (Decl.Variable) src.getSyntacticItem(i);
				r = r + v.getName();

			}
			return "{" + r + "}";
		}
	}

	private void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context);
	}
}
