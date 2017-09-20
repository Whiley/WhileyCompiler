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
package wyc.check;

import static wyc.lang.WhileyFile.*;
import static wyc.util.ErrorMessages.VARIABLE_POSSIBLY_UNITIALISED;
import static wyc.util.ErrorMessages.errorMessage;

import wyc.lang.WhileyFile;
import wyc.util.AbstractFunction;

import java.util.BitSet;
import java.util.HashSet;

import wybs.lang.SyntaxError;

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
public class DefiniteAssignmentCheck extends AbstractFunction<DefiniteAssignmentCheck.DefinitelyAssignedSet,DefiniteAssignmentCheck.ControlFlow> {

	public void check(WhileyFile wf) {
		visitWhileyFile(wf, null);
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
		if(declaration.hasInitialiser()) {
			visitExpression(declaration.getInitialiser(), environment);
		}
		//
		return null;
	}

	@Override
	public ControlFlow visitVariable(Decl.Variable decl, DefinitelyAssignedSet environment) {
		//
		if(decl.hasInitialiser()) {
			visitExpression(decl.getInitialiser(), environment);
			environment = environment.add(decl);
		}
		//
		return new ControlFlow(environment,null);
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
		for (Expr lval : stmt.getLeftHandSide()) {
			if (lval instanceof Expr.VariableAccess) {
				// Skip local variables since they are being assigned and, otherwise, could
				// raise an error.
			} else {
				visitExpression(lval, environment);
			}
		}
		// right-hand side
		visitExpressions(stmt.getRightHandSide(), environment);
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
	public ControlFlow visitNamedBlock(Stmt.NamedBlock stmt, DefinitelyAssignedSet environment) {
		return visitBlock(stmt.getBlock(),environment);
	}

	@Override
	public ControlFlow visitReturn(Stmt.Return stmt, DefinitelyAssignedSet environment) {
		visitExpressions(stmt.getReturns(), environment);
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
		Tuple<Decl.Variable> parameters = expression.getParameters();
		for(int i=0;i!=parameters.size();++i) {
			Decl.Variable var = parameters.get(i);
			if(var.hasInitialiser()) {
				visitExpression(var.getInitialiser(), environment);
			}
			environment = environment.add(var);
		}
		visitExpression(expression.getOperand(), environment);
		return null;
	}

	@Override
	public ControlFlow visitExistentialQuantifier(Expr.ExistentialQuantifier expression, DefinitelyAssignedSet environment) {
		Tuple<Decl.Variable> parameters = expression.getParameters();
		for(int i=0;i!=parameters.size();++i) {
			Decl.Variable var = parameters.get(i);
			if(var.hasInitialiser()) {
				visitExpression(var.getInitialiser(), environment);
			}
			environment = environment.add(var);
		}
		visitExpression(expression.getOperand(), environment);
		return null;
	}

	@Override
	public ControlFlow visitVariableAccess(Expr.VariableAccess expression, DefinitelyAssignedSet environment) {
		Decl.Variable vd = expression.getVariableDeclaration();
		if (!environment.contains(vd)) {
			WhileyFile file = ((WhileyFile) expression.getHeap());
			throw new SyntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), file.getEntry(), expression);
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
		@Override
		public String toString() {
			return variables.toString();
		}
	}
}
