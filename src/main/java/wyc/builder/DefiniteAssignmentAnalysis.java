// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.
package wyc.builder;

import static wyc.lang.WhileyFile.*;
import wyc.lang.WhileyFile;
import wycc.util.Triple;

import static wybs.lang.SyntaxError.*;
import static wyil.util.ErrorMessages.VARIABLE_POSSIBLY_UNITIALISED;
import static wyil.util.ErrorMessages.errorMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
public class DefiniteAssignmentAnalysis {
	/**
	 * The whiley source file being checked for definite assignment.
	 */
	private final WhileyFile file;

	public DefiniteAssignmentAnalysis(WhileyFile file) {
		this.file = file;
	}

	public void check() {
		for (WhileyFile.Declaration d : file.getDeclarations()) {
			check(d);
		}
	}

	/**
	 * Perform definite assignment analysis on a single declaration.
	 *
	 * @param declaration
	 */
	private void check(WhileyFile.Declaration declaration) {
		if(declaration instanceof Declaration.Import) {
			// There isn't anything to do here. This is because imports cannot
			// use variables anyway.
		} else if(declaration instanceof Declaration.Constant) {
			// There isn't anything to do here. This is because constants cannot
			// use variables anyway.
		} else if(declaration instanceof Declaration.Type) {
			// There isn't anything to do here either. This is because variables
			// used in type invariants are already checked by the
			// FlowTypeChecker to ensure they are declared.
		} else if(declaration instanceof Declaration.FunctionOrMethod) {
			check((Declaration.FunctionOrMethod) declaration);
		} else if(declaration instanceof Declaration.Property) {
			check((Declaration.Property) declaration);
		} else {
			throw new InternalFailure("unknown declaration encountered",file.getEntry(),declaration);
		}
	}

	/**
	 * Check a function or method declaration for definite assignment.
	 *
	 * @param declaration
	 * @return
	 */
	private void check(Declaration.FunctionOrMethod declaration) {
		// Initialise set of definitely assigned variables to include all
		// parameters.
		DefintelyAssignedSet environment = new DefintelyAssignedSet();
		for(Declaration.Variable p : declaration.getParameters()) {
			environment = environment.add(p.getName());
		}
		// Check the preconditions
		checkExpressions(declaration.getRequires(),environment);
		// Check the postconditions
		{
			DefintelyAssignedSet postEnvironment = environment;
			for(Declaration.Variable p : declaration.getReturns()) {
				postEnvironment = postEnvironment.add(p.getName());
			}
			checkExpressions(declaration.getEnsures(),postEnvironment);
		}
		// Iterate through each statement in the body of the function or method,
		// updating the set of definitely assigned variables as appropriate.
		checkBlock(declaration.getBody(),environment);
	}

	/**
	 * Check a function or method declaration for definite assignment.
	 *
	 * @param declaration
	 * @return
	 */
	private void check(Declaration.Property declaration) {
		// Initialise set of definitely assigned variables to include all
		// parameters.
		DefintelyAssignedSet defs = new DefintelyAssignedSet();
		for(Declaration.Variable p : declaration.getParameters()) {
			defs = defs.add(p.getName());
		}
		// Iterate through each statement in the body of the function or method,
		// updating the set of definitely assigned variables as appropriate.
		checkExpressions(declaration.getInvariant(),defs);
	}

	/**
	 * Check that all variables used in a given list of statements are
	 * definitely assigned. Furthermore, update the set of definitely assigned
	 * variables to include any which are definitely assigned at the end of
	 * these statements.
	 *
	 * @param block
	 *            The list of statements to check.
	 * @param environment
	 *            The set of variables which are definitely assigned.
	 */
	private ControlFlow checkBlock(Stmt.Block block, DefintelyAssignedSet environment) {
		DefintelyAssignedSet nextEnvironment = environment;
		DefintelyAssignedSet breakEnvironment = null;
		for(int i=0;i!=block.size();++i) {
			Stmt s = block.getOperand(i);
			ControlFlow nf = checkStatement(s, nextEnvironment);
			nextEnvironment = nf.nextEnvironment;
			breakEnvironment = join(breakEnvironment,nf.breakEnvironment);
		}
		return new ControlFlow(nextEnvironment,breakEnvironment);
	}

	/**
	 * Check that all variables used in a given statement are definitely
	 * assigned. Furthermore, update the set of definitely assigned variables to
	 * include any which are definitely assigned after this statement.
	 *
	 * @param statement
	 *            The statement to check.
	 * @param environment
	 *            The set of variables which are definitely assigned.
	 * @return The updated set of variables which are now definitely assigned,
	 *         or null if the method has terminated.
	 */
	private ControlFlow checkStatement(Stmt statement, DefintelyAssignedSet environment) {
		try {
			if(statement instanceof Stmt.Assert) {
				return checkAssert((Stmt.Assert) statement, environment);
			} else if(statement instanceof Stmt.Assign) {
				return checkAssign((Stmt.Assign) statement, environment);
			} else if(statement instanceof Stmt.Assume) {
				return checkAssume((Stmt.Assume) statement, environment);
			} else if(statement instanceof Stmt.Break) {
				return checkBreak((Stmt.Break) statement, environment);
			} else if(statement instanceof Stmt.Continue) {
				return checkContinue((Stmt.Continue) statement, environment);
			} else if(statement instanceof Stmt.Debug) {
				return checkDebug((Stmt.Debug) statement, environment);
			} else if(statement instanceof Stmt.DoWhile) {
				return checkDoWhile((Stmt.DoWhile) statement, environment);
			} else if(statement instanceof Stmt.Fail) {
				return check((Stmt.Fail) statement, environment);
			} else if(statement instanceof Expr.Invoke) {
				return checkFunctionOrMethodCall((Expr.Invoke) statement, environment);
			} else if(statement instanceof Stmt.IfElse) {
				return checkIfElse((Stmt.IfElse) statement, environment);
			} else if(statement instanceof Expr.IndirectInvoke) {
				return checkIndirectFunctionOrMethodCall((Expr.IndirectInvoke) statement, environment);
			} else if(statement instanceof Stmt.NamedBlock) {
				return checkNamedBlock((Stmt.NamedBlock) statement, environment);
			} else if(statement instanceof Stmt.Return) {
				return checkReturn((Stmt.Return) statement, environment);
			} else if(statement instanceof Stmt.Skip) {
				return checkSkip((Stmt.Skip) statement, environment);
			} else if(statement instanceof Stmt.Switch) {
				return checkSwitch((Stmt.Switch) statement, environment);
			} else if(statement instanceof Declaration.Variable) {
				return checkVariableDeclaration((Declaration.Variable) statement, environment);
			} else if(statement instanceof Stmt.While) {
				return checkWhile((Stmt.While) statement, environment);
			} else {
				throw new InternalFailure("unknown statement encountered",file.getEntry(),statement);
			}
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable t) {
			throw new InternalFailure(t.getMessage(),file.getEntry(),statement,t);
		}
	}

	private ControlFlow checkAssert(Stmt.Assert stmt, DefintelyAssignedSet environment) {
		checkExpression(stmt.getCondition(), environment);
		return new ControlFlow(environment,null);
	}

	private ControlFlow checkAssign(Stmt.Assign stmt, DefintelyAssignedSet environment) {
		// left-hand side
		for (Expr lval : stmt.getLeftHandSide()) {
			if (lval instanceof Expr.VariableAccess) {
				// Skip local variables since they are being assigned
			} else {
				checkExpression(lval, environment);
			}
		}
		// right-hand side
		for (Expr rval : stmt.getRightHandSide()) {
			checkExpression(rval, environment);
		}
		// Update the environment as necessary
		for (Expr lval : stmt.getLeftHandSide()) {
			if (lval instanceof Expr.VariableAccess) {
				Expr.VariableAccess lv = (Expr.VariableAccess) lval;
				environment = environment.add(lv.getVariableDeclaration().getName());
			}
		}
		//
		return new ControlFlow(environment, null);
	}

	private ControlFlow checkAssume(Stmt.Assume stmt, DefintelyAssignedSet environment) {
		checkExpression(stmt.getCondition(), environment);
		return new ControlFlow(environment,null);
	}

	private ControlFlow checkBreak(Stmt.Break stmt, DefintelyAssignedSet environment) {
		return new ControlFlow(null,environment);
	}

	private ControlFlow checkContinue(Stmt.Continue stmt, DefintelyAssignedSet environment) {
		// Here we can just treat a continue in the same way as a return
		// statement. It makes no real difference.
		return new ControlFlow(null,null);
	}

	private ControlFlow checkDebug(Stmt.Debug stmt, DefintelyAssignedSet environment) {
		checkExpression(stmt.getCondition(), environment);
		return new ControlFlow(environment,null);
	}

	private ControlFlow checkDoWhile(Stmt.DoWhile stmt, DefintelyAssignedSet environment) {
		//
		ControlFlow flow = checkBlock(stmt.getBody(), environment);
		//
		checkExpression(stmt.getCondition(), flow.nextEnvironment);
		//
		for(Expr e : stmt.getInvariant()) {
			checkExpression(e,flow.nextEnvironment);
		}
		//
		environment = join(flow.nextEnvironment,flow.breakEnvironment);
		//
		return new ControlFlow(environment,null);
	}

	private ControlFlow check(Stmt.Fail stmt, DefintelyAssignedSet environment) {
		return new ControlFlow(null,null);
	}

	private ControlFlow checkIfElse(Stmt.IfElse stmt, DefintelyAssignedSet environment) {
		checkExpression(stmt.getCondition(), environment);
		//
		ControlFlow left = checkBlock(stmt.getTrueBranch(), environment);
		ControlFlow right = checkBlock(stmt.getFalseBranch(), environment);
		// Now, merge all generated control-flow paths together
		return left.merge(right);
	}

	private ControlFlow checkNamedBlock(Stmt.NamedBlock stmt, DefintelyAssignedSet environment) {
		return checkBlock(stmt.getBlock(),environment);
	}

	private ControlFlow checkReturn(Stmt.Return stmt, DefintelyAssignedSet environment) {
		for(Expr e : stmt.getOperand()) {
			checkExpression(e, environment);
		}
		return new ControlFlow(null,null);
	}

	private ControlFlow checkSkip(Stmt.Skip stmt, DefintelyAssignedSet environment) {
		return new ControlFlow(environment,null);
	}

	private ControlFlow checkSwitch(Stmt.Switch stmt, DefintelyAssignedSet environment) {
		DefintelyAssignedSet caseEnvironment = null;
		DefintelyAssignedSet breakEnvironment = null;

		checkExpression(stmt.getCondition(), environment);
		//
		boolean hasDefault = false;
		for(Stmt.Case c : stmt.getCases()) {
			ControlFlow cf = checkBlock(c.getBlock(), environment);
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

	private ControlFlow checkVariableDeclaration(Declaration.Variable stmt, DefintelyAssignedSet environment) {
		if (stmt.hasInitialiser()) {
			checkExpression(stmt.getInitialiser(), environment);
			environment = environment.add(stmt.getName());
		}
		return new ControlFlow(environment,null);
	}

	private ControlFlow checkWhile(Stmt.While stmt, DefintelyAssignedSet environment) {
		checkExpression(stmt.getCondition(), environment);
		//
		for(Expr e : stmt.getInvariant()) {
			checkExpression(e,environment);
		}
		//
		checkBlock(stmt.getBody(), environment);
		//
		return new ControlFlow(environment,null);
	}

	private void checkExpressions(Tuple<Expr> expressions, DefintelyAssignedSet environment) {
		for(Expr e : expressions) {
			checkExpression(e,environment);
		}
	}

	/**
	 * Check that all variables used in a given expression are definitely
	 * assigned.
	 *
	 * @param expr
	 *            The expression to check.
	 * @param environment
	 *            The set of variables which are definitely assigned.
	 */
	private void checkExpression(Expr expression, DefintelyAssignedSet environment) {
		try {
			if(expression instanceof Expr.ArrayInitialiser) {
				checkArrayInitialiser((Expr.ArrayInitialiser) expression, environment);
			} else if(expression instanceof Expr.ArrayGenerator) {
				checkArrayGenerator((Expr.ArrayGenerator) expression, environment);
			} else if(expression instanceof Expr.Operator) {
				checkOperator((Expr.Operator) expression, environment);
			} else if(expression instanceof Expr.Cast) {
				checkCast((Expr.Cast) expression, environment);
			} else if(expression instanceof Expr.Constant) {
				checkConstant((Expr.Constant) expression, environment);
			} else if(expression instanceof Expr.StaticVariableAccess) {
				checkConstantAccess((Expr.StaticVariableAccess) expression, environment);
			} else if(expression instanceof Expr.Dereference) {
				checkDereference((Expr.Dereference) expression, environment);
			} else if(expression instanceof Expr.RecordAccess) {
				checkFieldAccess((Expr.RecordAccess) expression, environment);
			} else if(expression instanceof Expr.LambdaConstant) {
				checkFunctionOrMethod((Expr.LambdaConstant) expression, environment);
			} else if(expression instanceof Expr.Invoke) {
				checkFunctionOrMethodCall((Expr.Invoke) expression, environment);
			} else if(expression instanceof Expr.ArrayAccess) {
				checkIndexOf((Expr.ArrayAccess) expression, environment);
			} else if(expression instanceof Expr.IndirectInvoke) {
				checkIndirectFunctionOrMethodCall((Expr.IndirectInvoke) expression, environment);
			} else if(expression instanceof Expr.LambdaInitialiser) {
				checkLambda((Expr.LambdaInitialiser) expression, environment);
			} else if(expression instanceof Expr.VariableAccess) {
				checkLocalVariable((Expr.VariableAccess) expression, environment);
			} else if(expression instanceof Expr.New) {
				checkNew((Expr.New) expression, environment);
			} else if(expression instanceof Expr.Quantifier) {
				checkQuantifier((Expr.Quantifier) expression, environment);
			} else if(expression instanceof Expr.RecordInitialiser) {
				checkRecord((Expr.RecordInitialiser) expression, environment);
			} else {
				throw new InternalFailure("unknown expression encountered",file.getEntry(),expression);
			}
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable t) {
			throw new InternalFailure("internal failure",file.getEntry(),expression,t);
		}
	}

	private void checkArrayInitialiser(Expr.ArrayInitialiser expression, DefintelyAssignedSet environment) {
		for(int i=0;i!=expression.size();++i) {
			checkExpression(expression.getOperand(i),environment);
		}
	}

	private void checkArrayGenerator(Expr.ArrayGenerator expression, DefintelyAssignedSet environment) {
		checkExpression(expression.getValue(),environment);
		checkExpression(expression.getLength(),environment);
	}

	private void checkOperator(Expr.Operator expression, DefintelyAssignedSet environment) {
		for(int i=0;i!=expression.size();++i) {
			checkExpression(expression.getOperand(i),environment);
		}
	}

	private void checkCast(Expr.Cast expression, DefintelyAssignedSet environment) {
		checkExpression(expression.getCastedExpr(),environment);
	}

	private void checkConstant(Expr.Constant expression, DefintelyAssignedSet environment) {

	}

	private void checkConstantAccess(Expr.StaticVariableAccess expression, DefintelyAssignedSet environment) {

	}

	private void checkDereference(Expr.Dereference expression, DefintelyAssignedSet environment) {
		checkExpression(expression.getOperand(),environment);
	}

	private void checkFieldAccess(Expr.RecordAccess expression, DefintelyAssignedSet environment) {
		checkExpression(expression.getSource(),environment);
	}

	private void checkFunctionOrMethod(Expr.LambdaConstant expression, DefintelyAssignedSet environment) {

	}

	private ControlFlow checkFunctionOrMethodCall(Expr.Invoke expression, DefintelyAssignedSet environment) {
		for(Expr p : expression.getArguments()) {
			checkExpression(p,environment);
		}
		return new ControlFlow(environment,null);
	}

	private void checkIndexOf(Expr.ArrayAccess expression, DefintelyAssignedSet environment) {
		checkExpression(expression.getSource(),environment);
		checkExpression(expression.getSubscript(),environment);
	}

	private ControlFlow checkIndirectFunctionOrMethodCall(Expr.IndirectInvoke expression, DefintelyAssignedSet environment) {
		checkExpression(expression.getSource(),environment);
		for(Expr p : expression.getArguments()) {
			checkExpression(p,environment);
		}
		return new ControlFlow(environment,null);
	}

	private void checkLambda(Expr.LambdaInitialiser expression, DefintelyAssignedSet environment) {
		// Add lambda parameters to the set of definitely assigned variables.
		for(Declaration.Variable p : expression.getParameterTypes()) {
			environment = environment.add(p.getName());
		}
		// Check body of the lambda
		checkExpression(expression.getBody(),environment);
	}

	private void checkLocalVariable(Expr.VariableAccess expression, DefintelyAssignedSet environment) {
		Declaration.Variable vd = expression.getVariableDeclaration();
		if (!environment.contains(vd.getName())) {
			throw new SyntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), file.getEntry(), expression);
		}
	}

	private void checkNew(Expr.New expression, DefintelyAssignedSet environment) {
		checkExpression(expression.getOperand(),environment);
	}

	private void checkQuantifier(Expr.Quantifier expression, DefintelyAssignedSet environment) {
		for(Declaration.Variable p : expression.getParameters()) {
			checkExpression(p.getInitialiser(),environment);
			environment = environment.add(p.getName());
		}
		checkExpression(expression.getBody(),environment);
	}

	private void checkRecord(Expr.RecordInitialiser expression, DefintelyAssignedSet environment) {
		for(int i = 0;i!=expression.size();++i) {
			Pair<Identifier,Expr> e = expression.getOperand(i);
			checkExpression(e.getSecond(),environment);
		}
	}

	private class ControlFlow {
		/**
		 * The set of definitely assigned variables on this path which fall
		 * through to the next logical statement.
		 */
		public final DefintelyAssignedSet nextEnvironment;

		/**
		 * The set of definitely assigned variables on this path which are on
		 * the control-flow path caused by a break statement.
		 */
		public final DefintelyAssignedSet breakEnvironment;

		public ControlFlow(DefintelyAssignedSet nextEnvironment, DefintelyAssignedSet breakEnvironment) {
			this.nextEnvironment = nextEnvironment;
			this.breakEnvironment = breakEnvironment;
		}

		public ControlFlow merge(ControlFlow other) {
			DefintelyAssignedSet n = join(nextEnvironment,other.nextEnvironment);
			DefintelyAssignedSet b = join(breakEnvironment,other.breakEnvironment);
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
	private static DefintelyAssignedSet join(DefintelyAssignedSet left, DefintelyAssignedSet right) {
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
	private class DefintelyAssignedSet {
		private HashSet<Identifier> variables;

		public DefintelyAssignedSet() {
			this.variables = new HashSet<>();
		}

		public DefintelyAssignedSet(DefintelyAssignedSet defs) {
			this.variables = new HashSet<>(defs.variables);
		}

		public boolean contains(Identifier var) {
			return variables.contains(var);
		}

		/**
		 * Add a variable to the set of definitely assigned variables, producing
		 * an updated set.
		 *
		 * @param var
		 * @return
		 */
		public DefintelyAssignedSet add(Identifier var) {
			DefintelyAssignedSet r = new DefintelyAssignedSet(this);
			r.variables.add(var);
			return r;
		}

		/**
		 * Remove a variable from the set of definitely assigned variables, producing
		 * an updated set.
		 *
		 * @param var
		 * @return
		 */
		public DefintelyAssignedSet remove(Identifier var) {
			DefintelyAssignedSet r = new DefintelyAssignedSet(this);
			r.variables.remove(var);
			return r;
		}

		/**
		 * Join two sets together, where the result contains a variable only if
		 * it is definitely assigned on both branches.
		 *
		 * @param other
		 * @return
		 */
		public DefintelyAssignedSet join(DefintelyAssignedSet other) {
			DefintelyAssignedSet r = new DefintelyAssignedSet();
			for (Identifier var : variables) {
				if (other.contains(var)) {
					r.variables.add(var);
				}
			}
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
