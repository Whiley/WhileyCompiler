package wyc.builder;

import wyc.lang.Expr;
import wyc.lang.Stmt;
import wyc.lang.WhileyFile;
import wycommon.util.Triple;

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
		for (WhileyFile.Declaration d : file.declarations) {
			check(d);
		}
	}
	
	/**
	 * Perform definite assignment analysis on a single declaration.
	 * 
	 * @param declaration
	 */
	private void check(WhileyFile.Declaration declaration) {
		if(declaration instanceof WhileyFile.Import) {
			// There isn't anything to do here. This is because imports cannot
			// use variables anyway.
		} else if(declaration instanceof WhileyFile.Constant) {
			// There isn't anything to do here. This is because constants cannot
			// use variables anyway.
		} else if(declaration instanceof WhileyFile.Type) {
			// There isn't anything to do here either. This is because variables
			// used in type invariants are already checked by the
			// FlowTypeChecker to ensure they are declared. 
		} else if(declaration instanceof WhileyFile.FunctionOrMethod) {
			check((WhileyFile.FunctionOrMethod) declaration);
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
	private void check(WhileyFile.FunctionOrMethod declaration) {
		// Initialise set of definitely assigned variables to include all
		// parameters.	
		DefintelyAssignedSet defs = new DefintelyAssignedSet();
		for(WhileyFile.Parameter p : declaration.parameters) {
			defs = defs.add(p.name());
		}
		// Iterate through each statement in the body of the function or method,
		// updating the set of definitely assigned variables as appropriate.
		checkStatements(declaration.statements,defs);
	}
	
	/**
	 * Check that all variables used in a given list of statements are
	 * definitely assigned. Furthermore, update the set of definitely assigned
	 * variables to include any which are definitely assigned at the end of
	 * these statements.
	 * 
	 * @param statements
	 *            The list of statements to check.
	 * @param environment
	 *            The set of variables which are definitely assigned.
	 */
	private ControlFlow checkStatements(List<Stmt> statements, DefintelyAssignedSet environment) {
		DefintelyAssignedSet nextEnvironment = environment;
		DefintelyAssignedSet breakEnvironment = null;
		for (Stmt s : statements) {
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
			} else if(statement instanceof Expr.FunctionOrMethodCall) {
				return checkFunctionOrMethodCall((Expr.FunctionOrMethodCall) statement, environment);
			} else if(statement instanceof Stmt.IfElse) {
				return checkIfElse((Stmt.IfElse) statement, environment);
			} else if(statement instanceof Expr.IndirectFunctionOrMethodCall) {
				return checkIndirectFunctionOrMethodCall((Expr.IndirectFunctionOrMethodCall) statement, environment);
			} else if(statement instanceof Stmt.NamedBlock) {
				return checkNamedBlock((Stmt.NamedBlock) statement, environment);
			} else if(statement instanceof Stmt.Return) {
				return checkReturn((Stmt.Return) statement, environment);
			} else if(statement instanceof Stmt.Skip) {
				return checkSkip((Stmt.Skip) statement, environment);
			} else if(statement instanceof Stmt.Switch) {
				return checkSwitch((Stmt.Switch) statement, environment);
			} else if(statement instanceof Stmt.VariableDeclaration) {
				return checkVariableDeclaration((Stmt.VariableDeclaration) statement, environment);
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
		checkExpression(stmt.expr, environment);
		return new ControlFlow(environment,null);	
	}
	
	private ControlFlow checkAssign(Stmt.Assign stmt, DefintelyAssignedSet environment) {
		// left-hand side
		for (Expr lval : stmt.lvals) {
			if (lval instanceof Expr.LocalVariable) {
				// Skip local variables since they are being assigned
			} else {
				checkExpression(lval, environment);
			}
		}
		// right-hand side
		for (Expr rval : stmt.rvals) {
			checkExpression(rval, environment);
		}
		// Update the environment as necessary
		for (Expr lval : stmt.lvals) {
			if (lval instanceof Expr.LocalVariable) {
				Expr.LocalVariable lv = (Expr.LocalVariable) lval;
				environment = environment.add(lv.var);
			}
		}
		//
		return new ControlFlow(environment, null);
	}
	
	private ControlFlow checkAssume(Stmt.Assume stmt, DefintelyAssignedSet environment) {
		checkExpression(stmt.expr, environment);
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
		checkExpression(stmt.expr, environment);
		return new ControlFlow(environment,null);
	}
	
	private ControlFlow checkDoWhile(Stmt.DoWhile stmt, DefintelyAssignedSet environment) {
		//
		ControlFlow flow = checkStatements(stmt.body, environment);
		//
		checkExpression(stmt.condition, flow.nextEnvironment);
		//
		for(Expr e : stmt.invariants) {
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
		checkExpression(stmt.condition, environment);
		//
		ControlFlow left = checkStatements(stmt.trueBranch, environment);
		ControlFlow right = checkStatements(stmt.falseBranch, environment);
		// Now, merge all generated control-flow paths together
		return left.merge(right);
	}
	
	private ControlFlow checkNamedBlock(Stmt.NamedBlock stmt, DefintelyAssignedSet environment) {
		return checkStatements(stmt.body,environment);
	}
	
	private ControlFlow checkReturn(Stmt.Return stmt, DefintelyAssignedSet environment) {
		for(Expr e : stmt.returns) {
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
		
		checkExpression(stmt.expr, environment);
		//
		boolean hasDefault = false;
		for(Stmt.Case c : stmt.cases) {
			ControlFlow cf = checkStatements(c.stmts, environment);
			caseEnvironment = join(caseEnvironment,cf.nextEnvironment);
			breakEnvironment = join(breakEnvironment,cf.breakEnvironment);
			if(c.expr.isEmpty()) {
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
	
	private ControlFlow checkVariableDeclaration(Stmt.VariableDeclaration stmt, DefintelyAssignedSet environment) {
		if (stmt.expr != null) {
			checkExpression(stmt.expr, environment);
			environment = environment.add(stmt.parameter.name);
		}
		return new ControlFlow(environment,null);
	}
	
	private ControlFlow checkWhile(Stmt.While stmt, DefintelyAssignedSet environment) {
		checkExpression(stmt.condition, environment);
		//
		for(Expr e : stmt.invariants) {
			checkExpression(e,environment);
		}
		//
		checkStatements(stmt.body, environment);
		//
		return new ControlFlow(environment,null);
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
			} else if(expression instanceof Expr.BinOp) {
				checkBinOp((Expr.BinOp) expression, environment);
			} else if(expression instanceof Expr.Cast) {
				checkCast((Expr.Cast) expression, environment);
			} else if(expression instanceof Expr.Constant) {
				checkConstant((Expr.Constant) expression, environment);
			} else if(expression instanceof Expr.ConstantAccess) {
				checkConstantAccess((Expr.ConstantAccess) expression, environment);
			} else if(expression instanceof Expr.Dereference) {
				checkDereference((Expr.Dereference) expression, environment);
			} else if(expression instanceof Expr.FieldAccess) {
				checkFieldAccess((Expr.FieldAccess) expression, environment);
			} else if(expression instanceof Expr.FunctionOrMethod) {
				checkFunctionOrMethod((Expr.FunctionOrMethod) expression, environment);
			} else if(expression instanceof Expr.FunctionOrMethodCall) {
				checkFunctionOrMethodCall((Expr.FunctionOrMethodCall) expression, environment);
			} else if(expression instanceof Expr.IndexOf) {
				checkIndexOf((Expr.IndexOf) expression, environment);
			} else if(expression instanceof Expr.IndirectFunctionOrMethodCall) {
				checkIndirectFunctionOrMethodCall((Expr.IndirectFunctionOrMethodCall) expression, environment);
			} else if(expression instanceof Expr.Lambda) {
				checkLambda((Expr.Lambda) expression, environment);
			} else if(expression instanceof Expr.LocalVariable) {
				checkLocalVariable((Expr.LocalVariable) expression, environment);
			} else if(expression instanceof Expr.New) {
				checkNew((Expr.New) expression, environment);
			} else if(expression instanceof Expr.Quantifier) {
				checkQuantifier((Expr.Quantifier) expression, environment);
			} else if(expression instanceof Expr.Record) {
				checkRecord((Expr.Record) expression, environment);
			} else if(expression instanceof Expr.TypeVal) {
				checkTypeVal((Expr.TypeVal) expression, environment);
			} else if(expression instanceof Expr.UnOp) {
				checkUnOp((Expr.UnOp) expression, environment);
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
		for(Expr e : expression.arguments) {
			checkExpression(e,environment);
		}
	}
	
	private void checkArrayGenerator(Expr.ArrayGenerator expression, DefintelyAssignedSet environment) {
		checkExpression(expression.element,environment);
		checkExpression(expression.count,environment);
	}
	
	private void checkBinOp(Expr.BinOp expression, DefintelyAssignedSet environment) {
		checkExpression(expression.lhs,environment);
		checkExpression(expression.rhs,environment);
	}

	private void checkCast(Expr.Cast expression, DefintelyAssignedSet environment) {
		checkExpression(expression.expr,environment);
	}
	
	private void checkConstant(Expr.Constant expression, DefintelyAssignedSet environment) {

	}
	
	private void checkConstantAccess(Expr.ConstantAccess expression, DefintelyAssignedSet environment) {

	}
	
	private void checkDereference(Expr.Dereference expression, DefintelyAssignedSet environment) {
		checkExpression(expression.src,environment);
	}
	
	private void checkFieldAccess(Expr.FieldAccess expression, DefintelyAssignedSet environment) {
		checkExpression(expression.src,environment);
	}
	
	private void checkFunctionOrMethod(Expr.FunctionOrMethod expression, DefintelyAssignedSet environment) {

	}
	
	private ControlFlow checkFunctionOrMethodCall(Expr.FunctionOrMethodCall expression, DefintelyAssignedSet environment) {
		for(Expr p : expression.arguments) {
			checkExpression(p,environment);
		}
		return new ControlFlow(environment,null);
	}
	
	private void checkIndexOf(Expr.IndexOf expression, DefintelyAssignedSet environment) {
		checkExpression(expression.src,environment);
		checkExpression(expression.index,environment);
	}
	
	private ControlFlow checkIndirectFunctionOrMethodCall(Expr.IndirectFunctionOrMethodCall expression, DefintelyAssignedSet environment) {
		checkExpression(expression.src,environment);
		for(Expr p : expression.arguments) {
			checkExpression(p,environment);
		}
		return new ControlFlow(environment,null);
	}
	
	private void checkLambda(Expr.Lambda expression, DefintelyAssignedSet environment) {
		// Add lambda parameters to the set of definitely assigned variables.
		for(WhileyFile.Parameter p : expression.parameters) {
			environment = environment.add(p.name());
		}
		// Check body of the lambda
		checkExpression(expression.body,environment);
	}
	
	private void checkLocalVariable(Expr.LocalVariable expression, DefintelyAssignedSet environment) {
		if (!environment.contains(expression.var)) {
			throw new SyntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), file.getEntry(), expression);
		}
	}
	
	private void checkNew(Expr.New expression, DefintelyAssignedSet environment) {
		checkExpression(expression.expr,environment);
	}
	
	private void checkQuantifier(Expr.Quantifier expression, DefintelyAssignedSet environment) {
		for(Triple<String,Expr,Expr> p : expression.sources) {
			checkExpression(p.second(),environment);
			checkExpression(p.third(),environment);
			environment = environment.add(p.first());
		}
		checkExpression(expression.condition,environment);
	}
	
	private void checkRecord(Expr.Record expression, DefintelyAssignedSet environment) {
		for(Map.Entry<String,Expr> e : expression.fields.entrySet()) {
			checkExpression(e.getValue(),environment);
		}
	}
	
	private void checkTypeVal(Expr.TypeVal expression, DefintelyAssignedSet environment) {

	}
	
	private void checkUnOp(Expr.UnOp expression, DefintelyAssignedSet environment) {
		checkExpression(expression.mhs,environment);
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
		private HashSet<String> variables;

		public DefintelyAssignedSet() {
			this.variables = new HashSet<String>();
		}

		public DefintelyAssignedSet(DefintelyAssignedSet defs) {
			this.variables = new HashSet<String>(defs.variables);
		}

		public boolean contains(String var) {
			return variables.contains(var);
		}

		/**
		 * Add a variable to the set of definitely assigned variables, producing
		 * an updated set.
		 * 
		 * @param var
		 * @return
		 */
		public DefintelyAssignedSet add(String var) {
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
		public DefintelyAssignedSet remove(String var) {
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
			for (String var : variables) {
				if (other.contains(var)) {
					r.variables.add(var);
				}
			}
			return r;
		}
		
		/**
		 * Useful for debugging
		 */
		public String toString() {
			return variables.toString();
		}
	}
}
