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

import static wybs.util.AbstractCompilationUnit.ITEM_bool;
import static wybs.util.AbstractCompilationUnit.ITEM_byte;
import static wybs.util.AbstractCompilationUnit.ITEM_int;
import static wybs.util.AbstractCompilationUnit.ITEM_null;
import static wybs.util.AbstractCompilationUnit.ITEM_utf8;
import static wyil.lang.WyilFile.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import wyal.util.NameResolver.ResolutionError;
import wybs.lang.*;
import wybs.util.ResolveError;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyil.check.FlowTypeUtils.*;
import wyil.util.*;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.LVal;
import wyil.lang.WyilFile.Modifier;
import wyil.lang.WyilFile.Stmt;
import wyil.lang.WyilFile.Template;
import wyil.lang.WyilFile.Type;
import wyil.lang.WyilFile.Decl.Link;
import wyil.lang.WyilFile.Type.*;

/**
 * <p>
 * Propagates type information in a <i>flow-sensitive</i> fashion from declared
 * parameter and return types through variable declarations and assigned
 * expressions, whilst inferring types for all intermediate expressions and
 * variables. During this propagation, type checking is performed to ensure
 * types are used soundly. For example:
 * </p>
 *
 * <pre>
 * function sum(int[] data) -> int:
 *     int r = 0                // declared int type for r
 *     int i = 0                // declared int type for i
 *     while i < |data|:        // checks int operands and bool condition
 *         r = r + data[i]      // checks int operands and int subscript
 *         i = i + 1            // checks int operands
 *     return r                 // checks int operand
 * </pre>
 *
 * <p>
 * The flow typing algorithm distinguishes between the <i>declared type</i> of a
 * variable and its <i>known type</i>. That is, the known type at any given
 * point is permitted to be more precise than the declared type (but not vice
 * versa). For example:
 * </p>
 *
 * <pre>
 * function extract(int|null x) -> int:
 *    if x is int:
 *        return x
 *    else:
 *        return 0
 * </pre>
 *
 * <p>
 * The above example is considered type safe because the known type of
 * <code>x</code> at the first return is <code>int</code>, which differs from
 * its declared type (i.e. <code>int|null</code>).
 * </p>
 *
 * <h3>References</h3>
 * <ul>
 * <li>
 * <p>
 * David J. Pearce and James Noble. Structural and Flow-Sensitive Types for
 * Whiley. Technical Report, Victoria University of Wellington, 2010.
 * </p>
 * </li>
 * </ul>
 *
 * @author David J. Pearce
 *
 */
public class FlowTypeCheck implements Compiler.Check {
	private final Build.Meter meter;
	private boolean status = true;

	public FlowTypeCheck(Build.Meter meter) {
		this.meter = meter.fork(FlowTypeCheck.class.getSimpleName());
	}

	// =========================================================================
	// WhileyFile(s)
	// =========================================================================

	@Override
	public boolean check(WyilFile wf) {
		for (Decl decl : wf.getModule().getUnits()) {
			checkDeclaration(decl);
		}
		meter.done();
		return status;
	}

	// =========================================================================
	// Declarations
	// =========================================================================

	public void checkDeclaration(Decl decl) {
		meter.step("declaration");
		switch (decl.getOpcode()) {
		case DECL_unit:
			checkUnit((Decl.Unit) decl);
			break;
		case DECL_importwith:
		case DECL_importfrom:
		case DECL_import:
			// can ignore
			break;
		case DECL_staticvar:
			checkStaticVariableDeclaration((Decl.StaticVariable) decl);
			break;
		case DECL_type:
		case DECL_rectype:
			checkTypeDeclaration((Decl.Type) decl);
			break;
		case DECL_function:
		case DECL_method:
			checkFunctionOrMethodDeclaration((Decl.FunctionOrMethod) decl);
			break;
		default:
		case DECL_property:
			checkPropertyDeclaration((Decl.Property) decl);
		}
	}

	public void checkUnit(Decl.Unit unit) {
		for (Decl decl : unit.getDeclarations()) {
			checkDeclaration(decl);
		}
	}

	/**
	 * Resolve types for a given type declaration. If an invariant expression is
	 * given, then we have to check and resolve types throughout the expression.
	 *
	 * @param decl Type declaration to check.
	 * @throws IOException
	 */
	public void checkTypeDeclaration(Decl.Type decl) {
		Environment environment = new Environment();
		// Check type is contractive
		checkContractive(decl, environment);
		// Check the type invariant
		checkConditions(decl.getInvariant(), true, environment);
	}

	/**
	 * check and check types for a given constant declaration.
	 *
	 * @param decl Constant declaration to check.
	 * @throws IOException
	 */
	public void checkStaticVariableDeclaration(Decl.StaticVariable decl) {
		Environment environment = new Environment();
		// Check initialiser matches declared type
		checkExpression(decl.getInitialiser(), decl.getType(), environment);
	}

	/**
	 * Type check a given function or method declaration.
	 *
	 * @param decl Function or method declaration to check.
	 * @throws IOException
	 */
	public void checkFunctionOrMethodDeclaration(Decl.FunctionOrMethod decl) {
		// Construct initial environment
		Environment environment = new Environment();
		// Update environment so this within declared lifetimes
		environment = FlowTypeUtils.declareThisWithin(decl, environment);
		// Check any preconditions (i.e. requires clauses) provided.
		checkConditions(decl.getRequires(), true, environment);
		// Check any postconditions (i.e. ensures clauses) provided.
		checkConditions(decl.getEnsures(), true, environment);
		// FIXME: Add the "this" lifetime
		if (decl.getModifiers().match(Modifier.Native.class) == null) {
			// Create scope representing this declaration
			EnclosingScope scope = new FunctionOrMethodScope(decl);
			// Check type information throughout all statements in body.
			Environment last = checkBlock(decl.getBody(), environment, scope);
			// Check return value
			checkReturnValue(decl, last);
		} else {
			// NOTE: we obviously don't need to check the body of a native function or
			// method. Attempting to do so causes problems because checkReturnValue will
			// fail.
		}
	}

	/**
	 * Check that a return value is provided when it is needed. For example, a
	 * return value is not required for a method that has no return type. Likewise,
	 * we don't expect one from a native method since there was no body to analyse.
	 *
	 * @param d
	 * @param last
	 */
	private void checkReturnValue(Decl.FunctionOrMethod d, Environment last) {
		if (d.match(Modifier.Native.class) == null && last != FlowTypeUtils.BOTTOM && d.getReturns().size() != 0) {
			// In this case, code reaches the end of the function or method and,
			// furthermore, that this requires a return value. To get here means
			// that there was no explicit return statement given on at least one
			// execution path.
			syntaxError(d, MISSING_RETURN_STATEMENT);
		}
	}

	public void checkPropertyDeclaration(Decl.Property d) {
		// Construct initial environment
		Environment environment = new Environment();
		// Check invariant (i.e. requires clauses) provided.
		checkConditions(d.getInvariant(), true, environment);
	}

	// =========================================================================
	// Blocks & Statements
	// =========================================================================

	/**
	 * check type information in a flow-sensitive fashion through a block of
	 * statements, whilst type checking each statement and expression.
	 *
	 * @param block       Block of statements to flow sensitively type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkBlock(Stmt.Block block, Environment environment, EnclosingScope scope) {
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			environment = checkStatement(stmt, environment, scope);
		}
		return environment;
	}

	/**
	 * check type information in a flow-sensitive fashion through a given statement,
	 * whilst type checking it at the same time. For statements which contain other
	 * statements (e.g. if, while, etc), then this will recursively check type
	 * information through them as well.
	 *
	 *
	 * @param stmt        Statement to flow-sensitively type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkStatement(Stmt stmt, Environment environment, EnclosingScope scope) {
		meter.step("statement");
		try {
			if (environment == FlowTypeUtils.BOTTOM) {
				// Sanity check incoming environment
				syntaxError(stmt, UNREACHABLE_CODE);
				return environment;
			}
			switch (stmt.getOpcode()) {
			case STMT_assert:
				return checkAssert((Stmt.Assert) stmt, environment, scope);
			case STMT_assign:
				return checkAssign((Stmt.Assign) stmt, environment, scope);
			case STMT_assume:
				return checkAssume((Stmt.Assume) stmt, environment, scope);
			case STMT_block:
				return checkBlock((Stmt.Block) stmt, environment, scope);
			case STMT_break:
				return checkBreak((Stmt.Break) stmt, environment, scope);
			case STMT_continue:
				return checkContinue((Stmt.Continue) stmt, environment, scope);
			case STMT_debug:
				return checkDebug((Stmt.Debug) stmt, environment, scope);
			case STMT_dowhile:
				return checkDoWhile((Stmt.DoWhile) stmt, environment, scope);
			case STMT_fail:
				return checkFail((Stmt.Fail) stmt, environment, scope);
			case STMT_for:
				return checkFor((Stmt.For) stmt, environment, scope);
			case STMT_if:
			case STMT_ifelse:
				return checkIfElse((Stmt.IfElse) stmt, environment, scope);
			case STMT_initialiser:
			case STMT_initialiservoid:
				return checkInitialiser((Stmt.Initialiser) stmt, environment, scope);
			case EXPR_invoke:
				checkExpression((Expr.Invoke) stmt, Type.Any, environment);
				return environment;
			case EXPR_indirectinvoke:
				checkExpression((Expr.IndirectInvoke) stmt, Type.Any, environment);
				return environment;
			case STMT_namedblock:
				return checkNamedBlock((Stmt.NamedBlock) stmt, environment, scope);
			case STMT_return:
			case STMT_returnvoid:
				return checkReturn((Stmt.Return) stmt, environment, scope);
			case STMT_skip:
				return checkSkip((Stmt.Skip) stmt, environment, scope);
			case STMT_switch:
				return checkSwitch((Stmt.Switch) stmt, environment, scope);
			case STMT_while:
				return checkWhile((Stmt.While) stmt, environment, scope);
			default:
				return internalFailure("unknown statement: " + stmt.getClass().getName(), stmt);
			}
		} catch (SyntacticException e) {
			throw e;
		} catch (Throwable e) {
			return internalFailure(e.getMessage(), stmt, e);
		}
	}

	/**
	 * Type check an assertion statement. This requires checking that the expression
	 * being asserted is well-formed and has boolean type. An assert statement can
	 * affect the resulting environment in certain cases, such as when a type test
	 * is assert. For example, after <code>assert x is int</code> the environment
	 * will regard <code>x</code> as having type <code>int</code>.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkAssert(Stmt.Assert stmt, Environment environment, EnclosingScope scope) {
		return checkCondition(stmt.getCondition(), true, environment);
	}

	/**
	 * Type check an assume statement. This requires checking that the expression
	 * being assumed is well-formed and has boolean type. An assume statement can
	 * affect the resulting environment in certain cases, such as when a type test
	 * is assert. For example, after <code>assert x is int</code> the environment
	 * will regard <code>x</code> as having type <code>int</code>.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkAssume(Stmt.Assume stmt, Environment environment, EnclosingScope scope) {
		return checkCondition(stmt.getCondition(), true, environment);
	}

	/**
	 * Type check a fail statement. The environment after a fail statement is
	 * "bottom" because that represents an unreachable program point.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkFail(Stmt.Fail stmt, Environment environment, EnclosingScope scope) {
		return FlowTypeUtils.BOTTOM;
	}

	/**
	 * Type check a for each statement.
	 *
	 * @param stmt
	 * @param environment
	 * @param scope
	 * @return
	 */
	private Environment checkFor(Stmt.For stmt, Environment environment, EnclosingScope scope) {
		// Type loop invariant(s).
		checkConditions(stmt.getInvariant(), true, environment);
		// Type variable initialiser
		checkExpression(stmt.getVariable().getInitialiser(), Type.IntArray, environment);
		// Type loop body using true environment
		checkBlock(stmt.getBody(), environment, scope);
		// Determine and update modified variables
		Tuple<Decl.Variable> modified = FlowTypeUtils.determineModifiedVariables(stmt.getBody());
		stmt.setModified(stmt.getHeap().allocate(modified));
		// Return environment unchanged
		return environment;
	}

	/**
	 * Type check a variable declaration statement. In particular, when an
	 * initialiser is given we must check it is well-formed and that it is a subtype
	 * of the declared type.
	 *
	 * @param decl        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkInitialiser(Stmt.Initialiser decl, Environment environment, EnclosingScope scope) {
		// Check type of initialiser.
		if (decl.hasInitialiser()) {
			Type lhs = decl.getType();
			Type rhs = checkExpression(decl.getInitialiser(), lhs, environment);
			// Apply type refinement (if applicable)
			if (rhs != null) {
				Tuple<Decl.Variable> vars = decl.getVariables();
				for (int i = 0; i != Math.min(lhs.shape(), rhs.shape()); ++i) {
					// Refine the declared type
					Type refined = refine(lhs.dimension(i), rhs.dimension(i));
					// Update the typing environment accordingly.
					environment = environment.refineType(vars.get(i), refined);
				}
			}
		}
		// Done.
		return environment;
	}

	/**
	 * Type check an assignment statement.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkAssign(Stmt.Assign stmt, Environment environment, EnclosingScope scope)
			throws IOException {
		Tuple<LVal> lvals = stmt.getLeftHandSide();
		Tuple<Expr> rvals = stmt.getRightHandSide();
		Type[] types = new Type[lvals.size()];
		for (int i = 0; i != lvals.size(); ++i) {
			types[i] = checkLVal(lvals.get(i), environment);
		}
		// NOTE: need to use min here for case where insufficient lvals or rvals given
		for (int i = 0; i != Math.min(lvals.size(), rvals.size()); ++i) {
			Type actual = checkExpression(rvals.get(i), types[i], environment);
			// Apply type refinement (if applicable)
			if (actual != null) {
				// ignore upstream errors
				Pair<Decl.Variable, Type> extraction = FlowTypeUtils.extractTypeTest(lvals.get(i), actual);
				if (extraction != null) {
					Decl.Variable decl = extraction.getFirst();
					// Refine the declared type
					Type type = refine(decl.getType(), extraction.getSecond());
					// Update the typing environment accordingly.
					environment = environment.refineType(extraction.getFirst(), type);
				}
			}
		}
		//
		if (lvals.size() < rvals.size()) {
			syntaxError(stmt, TOO_MANY_RETURNS);
		} else if (lvals.size() > rvals.size()) {
			syntaxError(stmt, INSUFFICIENT_RETURNS);
		}
		//
		return environment;
	}

	/**
	 * Type check a break statement. This requires propagating the current
	 * environment to the block destination, to ensure that the actual types of all
	 * variables at that point are precise.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkBreak(Stmt.Break stmt, Environment environment, EnclosingScope scope) {
		// FIXME: need to check environment to the break destination
		return FlowTypeUtils.BOTTOM;
	}

	/**
	 * Type check a continue statement. This requires propagating the current
	 * environment to the block destination, to ensure that the actual types of all
	 * variables at that point are precise.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkContinue(Stmt.Continue stmt, Environment environment, EnclosingScope scope) {
		// FIXME: need to check environment to the continue destination
		return FlowTypeUtils.BOTTOM;
	}

	/**
	 * Type check an assume statement. This requires checking that the expression
	 * being printed is well-formed and has string type.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkDebug(Stmt.Debug stmt, Environment environment, EnclosingScope scope) {
		checkExpression(stmt.getOperand(), Type.IntArray, environment);
		return environment;
	}

	/**
	 * Type check a do-while statement.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 * @throws ResolveError If a named type within this statement cannot be resolved
	 *                      within the enclosing project.
	 */
	private Environment checkDoWhile(Stmt.DoWhile stmt, Environment environment, EnclosingScope scope) {
		// Type check loop body
		environment = checkBlock(stmt.getBody(), environment, scope);
		// Type check invariants
		checkConditions(stmt.getInvariant(), true, environment);
		// Determine and update modified variables
		Tuple<Decl.Variable> modified = FlowTypeUtils.determineModifiedVariables(stmt.getBody());
		stmt.setModified(stmt.getHeap().allocate(modified));
		// Type condition assuming its false to represent the terminated loop.
		// This is important if the condition contains a type test, as we'll
		// know that doesn't hold here.
		return checkCondition(stmt.getCondition(), false, environment);
	}

	/**
	 * Type check an if-statement. To do this, we check the environment through both
	 * sides of condition expression. Each can produce a different environment in
	 * the case that runtime type tests are used. These potentially updated
	 * environments are then passed through the true and false blocks which, in
	 * turn, produce updated environments. Finally, these two environments are
	 * joined back together. The following illustrates:
	 *
	 * <pre>
	 *                    //  Environment
	 * function f(int|null x) -> int:
	 *                    // {x : int|null}
	 *    if x is null:
	 *                    // {x : null}
	 *        return 0
	 *                    // {x : int}
	 *    else:
	 *                    // {x : int}
	 *        x = x + 1
	 *                    // {x : int}
	 *    // --------------------------------------------------
	 *                    // {x : int} o {x : int} => {x : int}
	 *    return x
	 * </pre>
	 *
	 * Here, we see that the type of <code>x</code> is initially
	 * <code>int|null</code> before the first statement of the function body. On the
	 * true branch of the type test this is updated to <code>null</code>, whilst on
	 * the false branch it is updated to <code>int</code>. Finally, the type of
	 * <code>x</code> at the end of each block is <code>int</code> and, hence, its
	 * type after the if-statement is <code>int</code>.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 * @throws ResolveError If a named type within this statement cannot be resolved
	 *                      within the enclosing project.
	 */
	private Environment checkIfElse(Stmt.IfElse stmt, Environment environment, EnclosingScope scope) {
		// Check condition and apply variable retypings.
		Environment trueEnvironment = checkCondition(stmt.getCondition(), true, environment);
		Environment falseEnvironment = checkCondition(stmt.getCondition(), false, environment);
		// Update environments for true and false branches
		if (stmt.hasFalseBranch()) {
			trueEnvironment = checkBlock(stmt.getTrueBranch(), trueEnvironment, scope);
			falseEnvironment = checkBlock(stmt.getFalseBranch(), falseEnvironment, scope);
		} else {
			trueEnvironment = checkBlock(stmt.getTrueBranch(), trueEnvironment, scope);
		}
		// Join results back together
		return FlowTypeUtils.union(trueEnvironment, falseEnvironment);
	}

	/**
	 * Type check a <code>return</code> statement. If a return expression is given,
	 * then we must check that this is well-formed and is a subtype of the enclosing
	 * function or method's declared return type. The environment after a return
	 * statement is "bottom" because that represents an unreachable program point.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @param scope       The stack of enclosing scopes
	 * @return
	 * @throws ResolveError If a named type within this statement cannot be resolved
	 *                      within the enclosing project.
	 */
	private Environment checkReturn(Stmt.Return stmt, Environment environment, EnclosingScope scope)
			throws IOException {
		// Determine the set of return types for the enclosing function or
		// method. This then allows us to check the given operands are
		// appropriate subtypes.
		Decl.FunctionOrMethod fm = scope.getEnclosingScope(FunctionOrMethodScope.class).getDeclaration();
		Type type = fm.getType().getReturn();
		// Type check the operand for the return statement (if applicable)
		if (stmt.hasReturn()) {
			// Type check the operands for the return statement (if any)
			checkExpression(stmt.getReturn(), type, environment);
		} else {
			// Sanity check no return expected!
			checkIsSubtype(Type.Void, type, environment, stmt);
		}
		// Return bottom as following environment to signal that control-flow
		// cannot continue here. Thus, any following statements will encounter
		// the BOTTOM environment and, hence, report an appropriate error.
		return FlowTypeUtils.BOTTOM;
	}

	/**
	 * Type check a <code>skip</code> statement, which has no effect on the
	 * environment.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkSkip(Stmt.Skip stmt, Environment environment, EnclosingScope scope) {
		return environment;
	}

	/**
	 * Type check a <code>switch</code> statement. This is similar, in some ways, to
	 * the handling of if-statements except that we have n code blocks instead of
	 * just two. Therefore, we check type information through each block, which
	 * produces n potentially different environments and these are all joined
	 * together to produce the environment which holds after this statement. For
	 * example:
	 *
	 * <pre>
	 *                    //  Environment
	 * function f(int x) -> int|null:
	 *    int|null y
	 *                    // {x : int, y : void}
	 *    switch x:
	 *       case 0:
	 *                    // {x : int, y : void}
	 *           return 0
	 *                    // { }
	 *       case 1,2,3:
	 *                    // {x : int, y : void}
	 *           y = x
	 *                    // {x : int, y : int}
	 *       default:
	 *                    // {x : int, y : void}
	 *           y = null
	 *                    // {x : int, y : null}
	 *    // --------------------------------------------------
	 *                    // {} o
	 *                    // {x : int, y : int} o
	 *                    // {x : int, y : null}
	 *                    // => {x : int, y : int|null}
	 *    return y
	 * </pre>
	 *
	 * Here, the environment after the declaration of <code>y</code> has its actual
	 * type as <code>void</code> since no value has been assigned yet. For each of
	 * the case blocks, this initial environment is (separately) updated to produce
	 * three different environments. Finally, each of these is joined back together
	 * to produce the environment going into the <code>return</code> statement.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkSwitch(Stmt.Switch stmt, Environment environment, EnclosingScope scope)
			throws IOException {
		// Type check the expression being switched upon
		Type type = checkExpression(stmt.getCondition(), Type.Any, environment);
		// The final environment determines what flow continues after the switch
		// statement
		Environment finalEnv = null;
		// The record is whether a default case is given or not is important. If
		// not, then final environment always matches initial environment.
		boolean hasDefault = false;
		//
		for (Stmt.Case c : stmt.getCases()) {
			// Resolve the constants
			for (Expr e : c.getConditions()) {
				checkExpression(e, type, environment);
			}
			// Check case block
			Environment localEnv = environment;
			localEnv = checkBlock(c.getBlock(), localEnv, scope);
			// Merge resulting environment
			if (finalEnv == null) {
				finalEnv = localEnv;
			} else {
				finalEnv = FlowTypeUtils.union(finalEnv, localEnv);
			}
			// Keep track of whether a default
			hasDefault |= (c.getConditions().size() == 0);
		}

		if (!hasDefault) {
			// in this case, there is no default case in the switch. We must
			// therefore assume that there are values which will fall right
			// through the switch statement without hitting a case. Therefore,
			// we must include the original environment to accound for this.
			finalEnv = FlowTypeUtils.union(finalEnv, environment);
		}

		return finalEnv;
	}

	/**
	 * Type check a <code>NamedBlock</code> statement.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 */
	private Environment checkNamedBlock(Stmt.NamedBlock stmt, Environment environment, EnclosingScope scope) {
		// Updated the environment with new within relations
		LifetimeDeclaration enclosing = scope.getEnclosingScope(LifetimeDeclaration.class);
		String[] lifetimes = enclosing.getDeclaredLifetimes();
		environment = environment.declareWithin(stmt.getName().get(), lifetimes);
		// Create an appropriate scope for this block
		scope = new NamedBlockScope(scope, stmt);
		return checkBlock(stmt.getBlock(), environment, scope);
	}

	/**
	 * Type check a <code>whiley</code> statement.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
	 * @throws ResolveError If a named type within this statement cannot be resolved
	 *                      within the enclosing project.
	 */
	private Environment checkWhile(Stmt.While stmt, Environment environment, EnclosingScope scope) {
		// Type loop invariant(s).
		checkConditions(stmt.getInvariant(), true, environment);
		// Type condition assuming its true to represent inside a loop
		// iteration.
		// Important if condition contains a type test, as we'll know it holds.
		Environment trueEnvironment = checkCondition(stmt.getCondition(), true, environment);
		// Type condition assuming its false to represent the terminated loop.
		// Important if condition contains a type test, as we'll know it doesn't
		// hold.
		Environment falseEnvironment = checkCondition(stmt.getCondition(), false, environment);
		// Type loop body using true environment
		checkBlock(stmt.getBody(), trueEnvironment, scope);
		// Determine and update modified variables
		Tuple<Decl.Variable> modified = FlowTypeUtils.determineModifiedVariables(stmt.getBody());
		stmt.setModified(stmt.getHeap().allocate(modified));
		// Return false environment to represent flow after loop.
		return falseEnvironment;
	}

	// =========================================================================
	// Condition
	// =========================================================================

	/**
	 * Type check a sequence of zero or more conditions, such as the requires clause
	 * of a function or method. The environment from each condition is fed into the
	 * following. This means that, in principle, type tests influence both
	 * subsequent conditions and the remainder. The following illustrates:
	 *
	 * <pre>
	 * function f(int|null x) -> (int r)
	 * requires x is int
	 * requires x >= 0:
	 *    //
	 *    return x
	 * </pre>
	 *
	 * This type checks because of the initial type test <code>x is int</code>.
	 * Observe that, if the order of <code>requires</code> clauses was reversed,
	 * this would not type check. Finally, it is an interesting question as to why
	 * the above ever make sense. In general, it's better to simply declare
	 * <code>x</code> as type <code>int</code>. However, in some cases we may be
	 * unable to do this (for example, to preserve binary compatibility with a
	 * previous interface).
	 *
	 * @param conditions
	 * @param sign
	 * @param environment
	 * @return
	 */
	public Environment checkConditions(Tuple<Expr> conditions, boolean sign, Environment environment) {
		for (Expr e : conditions) {
			// Thread environment through from before
			environment = checkCondition(e, sign, environment);
		}
		return environment;
	}

	/**
	 * <p>
	 * Type check a given condition in a given environment with a given sign (which
	 * indicates whether the condition is known to hold or not). In certain
	 * situations (e.g. an if-statement) a condition may update the environment in
	 * accordance with any type tests used within. This is important to ensure that
	 * variables are <i>retyped</i> in e.g. if-statements. The simplest possible
	 * example is the following:
	 * </p>
	 *
	 * <pre>
	 * function f(int x) -> (int r):
	 *     if x &gt; 0:
	 *        return x + 1
	 *     else:
	 *        return 0
	 * </pre>
	 *
	 * <p>
	 * When (for example) typing <code>x &gt; 0</code> here, the environment would
	 * simply map <code>x</code> to its declared type <code>int</code>. However,
	 * because Whiley supports "flow typing", it's not always the case that the
	 * declared type of a variable is the right one to use. Consider a more complex
	 * case.
	 * </p>
	 *
	 * <pre>
	 * function g(int|null x) -> (int r):
	 *     if (x is int) && (x &gt; 0):
	 *        return x + 1
	 *     else:
	 *        return 0
	 * </pre>
	 *
	 * <p>
	 * This time, when typing (for example) typing <code>x &gt; 0</code>, we need to
	 * account for the fact that <code>x is int</code> is known. As such, the
	 * calculated type for <code>x</code> would be <code>(int|null)&int</code> when
	 * typing both <code>x &gt; 0</code> and <code>x + 1</code>.
	 * </p>
	 * <p>
	 * The purpose of the "sign" is to aid flow typing in the presence of negations.
	 * In essence, the sign indicates whether the statement being type checked is
	 * positive (i.e. sign=<code>true</code>) or negative (i.e.
	 * sign=<code>false</code>). In the latter case, the application of any type
	 * tests will be inverted. The following illustrates an interesting example:
	 * </p>
	 *
	 * <pre>
	 * function h(int|null x) -> (int r):
	 *     if !(x is null || x &lt; 0)
	 *        return x + 1
	 *     else:
	 *        return 0
	 * </pre>
	 *
	 * <p>
	 * To type check this example, the type checker needs to effectively "push" the
	 * logical negation through the disjunction to give
	 * <code>!(x is null) && x &gt;= 0</code>. The purpose of the sign is to enable
	 * this without actually rewriting the source code.
	 * </p>
	 *
	 * @param condition   The condition being type checked
	 * @param sign        The assumed outcome of the condition (either true or
	 *                    false).
	 * @param environment The environment going into the condition
	 * @return The (potentially updated) typing environment for this statement.
	 */
	public Environment checkCondition(Expr condition, boolean sign, Environment environment) {
		switch (condition.getOpcode()) {
		case EXPR_logicalnot:
			return checkLogicalNegation((Expr.LogicalNot) condition, sign, environment);
		case EXPR_logicalor:
			return checkLogicalDisjunction((Expr.LogicalOr) condition, sign, environment);
		case EXPR_logicaland:
			return checkLogicalConjunction((Expr.LogicalAnd) condition, sign, environment);
		case EXPR_logicaliff:
			return checkLogicalIff((Expr.LogicalIff) condition, sign, environment);
		case EXPR_logicalimplication:
			return checkLogicalImplication((Expr.LogicalImplication) condition, sign, environment);
		case EXPR_is:
			return checkIs((Expr.Is) condition, sign, environment);
		case EXPR_logicaluniversal:
		case EXPR_logicalexistential:
			return checkQuantifier((Expr.Quantifier) condition, sign, environment);
		default:
			Type t = checkExpression(condition, Type.Bool, environment);
			return environment;
		}
	}

	/**
	 * Type check a logical negation. This is relatively straightforward as we just
	 * flip the sign. Thus, if something is assumed to hold, then it is now assumed
	 * not to hold, etc. The following illustrates:
	 *
	 * <pre>
	 * function f(int|null x) -> (bool r):
	 *     return !(x is null) && x >= 0
	 * </pre>
	 *
	 * The effect of the negation <code>!(x is null)</code> is that the type test is
	 * now evaluated assuming it fails. Thus, it effects the environment by
	 * asserting <code>x</code> has type <code>(int|null)&!null</code> which is
	 * equivalent to <code>int</code>.
	 *
	 * @param expr
	 * @param sign
	 * @param environment
	 * @return
	 */
	private Environment checkLogicalNegation(Expr.LogicalNot expr, boolean sign, Environment environment) {
		return checkCondition(expr.getOperand(), !sign, environment);
	}

	/**
	 * In this case, we are assuming the environments are exclusive from each other
	 * (i.e. this is the opposite of threading them through). For example, consider
	 * this case:
	 *
	 * <pre>
	 * function f(int|null x) -> (bool r):
	 *   return (x is null) || (x >= 0)
	 * </pre>
	 *
	 * The environment produced by the left condition is <code>{x->null}</code>. We
	 * cannot thread this environment into the right condition as, clearly, it's not
	 * correct. Instead, we want to thread through the environment which arises on
	 * the assumption the fist case is false. That would be <code>{x->!null}</code>.
	 * Finally, the resulting environment is simply the union of the two
	 * environments from each case.
	 *
	 * @param expr
	 * @param sign
	 * @param environment
	 *
	 * @return
	 */
	private Environment checkLogicalDisjunction(Expr.LogicalOr expr, boolean sign, Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		if (sign) {
			Environment[] refinements = new Environment[operands.size()];
			for (int i = 0; i != operands.size(); ++i) {
				refinements[i] = checkCondition(operands.get(i), sign, environment);
				// The clever bit. Recalculate assuming opposite sign.
				environment = checkCondition(operands.get(i), !sign, environment);
			}
			// Done.
			return FlowTypeUtils.union(refinements);
		} else {
			for (int i = 0; i != operands.size(); ++i) {
				environment = checkCondition(operands.get(i), sign, environment);
			}
			return environment;
		}
	}

	/**
	 * In this case, we are threading each environment as is through to the next
	 * statement. For example, consider this example:
	 *
	 * <pre>
	 * function f(int|null x) -> (bool r):
	 *   return (x is int) && (x >= 0)
	 * </pre>
	 *
	 * The environment going into <code>x is int</code> will be
	 * <code>{x->(int|null)}</code>. The environment coming out of this statement
	 * will be <code>{x-&gt;int}</code> and this is just threaded directly into the
	 * next statement <code>x &gt; 0</code>
	 *
	 * @param expr
	 * @param sign
	 * @param environment
	 *
	 * @return
	 */
	private Environment checkLogicalConjunction(Expr.LogicalAnd expr, boolean sign, Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		if (sign) {
			for (int i = 0; i != operands.size(); ++i) {
				environment = checkCondition(operands.get(i), sign, environment);
			}
			return environment;
		} else {
			Environment[] refinements = new Environment[operands.size()];
			for (int i = 0; i != operands.size(); ++i) {
				refinements[i] = checkCondition(operands.get(i), sign, environment);
				// The clever bit. Recalculate assuming opposite sign.
				environment = checkCondition(operands.get(i), !sign, environment);
			}
			// Done.
			return FlowTypeUtils.union(refinements);
		}
	}

	private Environment checkLogicalImplication(Expr.LogicalImplication expr, boolean sign, Environment environment) {
		// To understand this, remember that A ==> B is equivalent to !A || B.
		if (sign) {
			// First case assumes the if body doesn't hold.
			Environment left = checkCondition(expr.getFirstOperand(), false, environment);
			// Second case assumes the if body holds ...
			environment = checkCondition(expr.getFirstOperand(), true, environment);
			// ... and then passes this into the then body
			Environment right = checkCondition(expr.getSecondOperand(), true, environment);
			//
			return FlowTypeUtils.union(left, right);
		} else {
			// Effectively, this is a conjunction equivalent to A && !B
			environment = checkCondition(expr.getFirstOperand(), true, environment);
			environment = checkCondition(expr.getSecondOperand(), false, environment);
			return environment;
		}
	}

	private Environment checkLogicalIff(Expr.LogicalIff expr, boolean sign, Environment environment) {
		environment = checkCondition(expr.getFirstOperand(), sign, environment);
		environment = checkCondition(expr.getSecondOperand(), sign, environment);
		return environment;
	}

	private Environment checkIs(Expr.Is expr, boolean sign, Environment environment) {
		Expr lhs = expr.getOperand();
		Type lhsT = checkExpression(expr.getOperand(), Type.Any, environment);
		Type rhsT = expr.getTestType();
		// Sanity check operands for this type test
		checkIsSubtype(lhsT, rhsT, environment, rhsT);
		// Extract variable being tested
		Pair<Decl.Variable, Type> extraction = FlowTypeUtils.extractTypeTest(lhs, expr.getTestType());
		if (extraction != null) {
			Decl.Variable var = extraction.getFirst();
			Type refinementT = extraction.getSecond();
			if (!sign) {
				refinementT = environment.subtract(environment.getType(var), refinementT);
				// Sanity check for definite branch taken
				if (refinementT instanceof Type.Void) {
					// DEFINITE FALSE CASE
					syntaxError(expr, BRANCH_ALWAYS_TAKEN);
				}
			}
			// Update the typing environment accordingly.
			environment = environment.refineType(var, refinementT);
		}
		//
		return environment;
	}

	private Environment checkQuantifier(Expr.Quantifier stmt, boolean sign, Environment environment) {
		// Check array initialisers
		for (Decl.StaticVariable decl : stmt.getParameters()) {
			checkExpression(decl.getInitialiser(), Type.IntArray, environment);
		}
		// NOTE: We throw away the returned environment from the body. This is
		// because any type tests within the body are ignored outside.
		checkCondition(stmt.getOperand(), true, environment);
		//
		return environment;
	}

	// =========================================================================
	// LVals
	// =========================================================================

	/**
	 * Type check a given lval assuming an initial environment. This returns the
	 * largest type which can be safely assigned to the lval. Observe that this type
	 * is determined by the declared type of the variable being assigned.
	 *
	 * @param lval
	 * @param environment
	 * @return
	 * @throws ResolutionError
	 */
	public Type checkLVal(LVal lval, Environment environment) {
		Type type;
		switch (lval.getOpcode()) {
		case EXPR_variablecopy:
			type = checkVariableLVal((Expr.VariableAccess) lval, environment);
			break;
		case EXPR_staticvariable:
			type = checkStaticVariableLVal((Expr.StaticVariableAccess) lval, environment);
			break;
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			type = checkArrayLVal((Expr.ArrayAccess) lval, environment);
			break;
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			type = checkRecordLVal((Expr.RecordAccess) lval, environment);
			break;
		case EXPR_dereference:
			type = checkDereferenceLVal((Expr.Dereference) lval, environment);
			break;
		case EXPR_fielddereference:
			type = checkFieldDereferenceLVal((Expr.FieldDereference) lval, environment);
			break;
		case EXPR_tupleinitialiser:
			type = checkTupleInitialiserLVal((Expr.TupleInitialiser) lval, environment);
			break;
		default:
			return internalFailure("unknown lval encountered (" + lval.getClass().getSimpleName() + ")", lval);
		}
		// Sanity check type. This can be non-sensical in the case of an upsteam type
		// error
		if (type != null) {
			lval.setType(lval.getHeap().allocate(type));
		}
		return type;
	}

	public Type checkVariableLVal(Expr.VariableAccess lval, Environment environment) {
		// At this point, we return the declared type of the variable rather
		// than the potentially refined type held in the environment. This
		// is critical as, otherwise, the current refinement would
		// unnecessarily restrict what we could assign to this variable.
		return lval.getVariableDeclaration().getType();
	}

	public Type checkStaticVariableLVal(Expr.StaticVariableAccess lval, Environment environment) {
		Decl.Link<Decl.StaticVariable> l = lval.getLink();
		// Check whether this access was successfully resolved or not.
		if (l.isResolved()) {
			// Yes, it was resolved so proceed as normal
			return l.getTarget().getType();
		} else {
			// No, it wasn't resolved so proceed with dummy
			return null;
		}
	}

	public Type checkArrayLVal(Expr.ArrayAccess lval, Environment environment) {
		// NOTE: must check lval first, not expression (#950)
		Type src = checkLVal((LVal) lval.getFirstOperand(), environment);
		// Attempt to view src as array
		Type.Array arrT = src.as(Type.Array.class);
		// Check declared type
		if (arrT == null) {
			// Fall back on flow type
			src = checkExpression(lval.getFirstOperand(), null, environment);
			// Extract array or fail
			arrT = extractType(Type.Array.class, src, EXPECTED_ARRAY, lval.getFirstOperand());
		}
		// Sanity check extraction
		if (arrT != null) {
			// Check for integer subscript
			checkExpression(lval.getSecondOperand(), Type.Int, environment);
			// Convert to concrete type
			return arrT.getElement();
		} else {
			// typing failure upstream
			return null;
		}
	}

	public Type checkRecordLVal(Expr.RecordAccess lval, Environment environment) {
		// NOTE: must check lval first, not expression (#950)
		Type src = checkLVal((LVal) lval.getOperand(), environment);
		// Attempt to view src as a record
		Type.Record recT = src.as(Type.Record.class);
		// Check declared type
		if (recT == null || recT.getField(lval.getField()) == null) {
			// Fall back on flow type
			src = checkExpression(lval.getOperand(), null, environment);
			// Extract record or fail
			recT = extractType(Type.Record.class, src, EXPECTED_RECORD, lval.getOperand());
		}
		// Extract the field type
		return extractFieldType(recT, lval.getField());
	}

	public Type checkDereferenceLVal(Expr.Dereference lval, Environment environment) {
		Type src = checkExpression(lval.getOperand(), (Type) null, environment);
		// Extract writeable reference type
		Type.Reference refT = extractType(Type.Reference.class, src, EXPECTED_REFERENCE, lval.getOperand());
		// Sanity check writability of reference
		if(refT != null) {
			checkIsWritable(refT.getElement(), lval.getOperand());
			return refT.getElement();
		} else {
			return null;
		}
	}

	public Type checkFieldDereferenceLVal(Expr.FieldDereference lval, Environment environment) {
		Type src = checkExpression(lval.getOperand(), (Type) null, environment);
		// Extract writeable reference type
		Type.Reference refT = extractType(Type.Reference.class, src, EXPECTED_REFERENCE, lval.getOperand());
		// Extact target type
		Type target = refT == null ? null : refT.getElement();
		// Extract underlying record
		Type.Record recT = extractType(Type.Record.class, target, EXPECTED_RECORD, lval.getOperand());
		// extract field type
		return extractFieldType(recT, lval.getField());
	}

	public Type checkTupleInitialiserLVal(Expr.TupleInitialiser lval, Environment environment) {
		Tuple<Expr> operands = lval.getOperands();
		Type[] types = new Type[operands.size()];
		for (int i = 0; i != types.length; ++i) {
			Type type = checkLVal((LVal) operands.get(i), environment);
			;
			if (type == null) {
				return null;
			}
			types[i] = type;
		}
		return Type.Tuple.create(types);
	}

	// =========================================================================
	// Expressions
	// =========================================================================

	/**
	 * Type check a given expression assuming an initial environment.
	 *
	 * @param expression  The expression to be checked.
	 *
	 * @param target      indicates the expected type from this expression (maybe
	 *                    void).
	 *
	 * @param environment The environment in which this expression is to be typed
	 * @return
	 * @throws ResolutionError
	 */
	public Type checkExpression(Expr expression, Type target, Environment environment) {
		// Construct empty typing to start with
		Typing typing = new Typing(environment);
		// Apply backwards type generation
		typing = inferExpression(target, expression, typing, environment);
		// Finalise the typing by attempt to type every subexpression.
		boolean ok = typing.finalise(expression);
		// Finally done. By this point, we need all links to have been resolved to be
		// sure the following makes sense.
		return ok ? expression.getType() : null;
	}

	//	public Typing inferExpressions(Tuple<Expr> expressions, Typing typing, Environment environment) {
	//		for (int i = 0; i != expressions.size(); ++i) {
	//			typing = inferExpression(expressions.get(i), typing, environment);
	//		}
	//		return typing;
	//	}

	public Typing inferExpressions(Type target, Tuple<Expr> expressions, Typing typing,
			Environment environment) {
		for (int i = 0; i != expressions.size(); ++i) {
			Expr ith = expressions.get(i);
			// Generate ith type
			typing = inferExpression(target, ith, typing, environment);
		}
		return typing;
	}

	public Typing inferExpression(Type target, Expr expression, Typing typing, Environment environment) {
		meter.step("expression");
		//
		switch (expression.getOpcode()) {
		case EXPR_constant:
			return inferConstant(target, (Expr.Constant) expression, typing, environment);
		case EXPR_variablecopy:
			return inferVariable(target, (Expr.VariableAccess) expression, typing, environment);
		case EXPR_staticvariable:
			return inferStaticVariable(target, (Expr.StaticVariableAccess) expression, typing, environment);
		case EXPR_cast:
			return inferCast(target, (Expr.Cast) expression, typing, environment);
		case EXPR_invoke:
			return inferInvoke(target, (Expr.Invoke) expression, typing, environment);
		case EXPR_indirectinvoke:
			return inferIndirectInvoke(target, (Expr.IndirectInvoke) expression, typing, environment);
		case EXPR_logicalnot:
		case EXPR_logicalor:
		case EXPR_logicaland:
		case EXPR_logicaliff:
		case EXPR_logicalimplication:
		case EXPR_is:
		case EXPR_logicaluniversal:
		case EXPR_logicalexistential:
			return inferConditionExpression(target, expression, typing, environment);
		case EXPR_equal:
		case EXPR_notequal:
			return inferEqualityOperator(target, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
			return inferIntegerComparator(target, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_integernegation:
			return inferIntegerOperator(target, (Expr.UnaryOperator) expression, typing, environment);
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
			return inferIntegerOperator(target, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_bitwisenot:
			return inferBitwiseOperator(target, (Expr.UnaryOperator) expression, typing, environment);
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
			return inferBitwiseOperator(target, (Expr.NaryOperator) expression, typing, environment);
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
			return inferBitwiseShift(target, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_tupleinitialiser:
			return inferTupleInitialiser(target, (Expr.TupleInitialiser) expression, typing, environment);
		case EXPR_recordinitialiser:
			return inferRecordInitialiser(target, (Expr.RecordInitialiser) expression, typing, environment);
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			return inferRecordAccess(target, (Expr.RecordAccess) expression, typing, environment);
		case EXPR_arraylength:
			return inferArrayLength(target, (Expr.ArrayLength) expression, typing, environment);
		case EXPR_arrayinitialiser:
			return inferArrayInitialiser(target, (Expr.ArrayInitialiser) expression, typing, environment);
		case EXPR_arraygenerator:
			return inferArrayGenerator(target, (Expr.ArrayGenerator) expression, typing, environment);
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			return inferArrayAccess(target, (Expr.ArrayAccess) expression, typing, environment);
		case EXPR_arrayrange:
			return inferArrayRange(target, (Expr.ArrayRange) expression, typing, environment);
		case EXPR_dereference:
			return inferDereference(target, (Expr.Dereference) expression, typing, environment);
		case EXPR_fielddereference:
			return inferFieldDereference(target, (Expr.FieldDereference) expression, typing, environment);
		case EXPR_staticnew:
		case EXPR_new:
			return inferNew(target, (Expr.New) expression, typing, environment);
		case EXPR_lambdaaccess:
			return inferLambdaAccess(target, (Expr.LambdaAccess) expression, typing, environment);
		case DECL_lambda:
			return inferLambdaDeclaration(target, (Decl.Lambda) expression, typing, environment);
		}
		return internalFailure("unknown expression encountered (" + expression.getClass().getSimpleName() + ")",
				expression);
	}

	private Typing inferArrayLength(Type target, Expr.ArrayLength expr, Typing typing, Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, Type.Int, expr);
		// Sanity check typing
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, target, expr.getType());
		}
		// Recursively infer source operand
		return inferExpression(Type.AnyArray, expr.getOperand(), nTyping, environment);
	}

	private Typing inferArrayInitialiser(Type target, Expr.ArrayInitialiser expr, Typing typing,
			Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr);
		Type.Existential v1 = nTyping.top();
		// Sanity check typing
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			System.out.println("GOT: " + typing + " => " + nTyping);
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), v1);
		}
		// Recursively check operands
		for (int i = 0; i != operands.size(); ++i) {
			nTyping = inferExpression(new Type.ProjectArray(v1), operands.get(i), nTyping, environment);
		}
		// Compute least upper bound of element types
		return nTyping;
	}

	private Typing inferArrayGenerator(Type target, Expr.ArrayGenerator expr, Typing typing,
			Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr);
		Type.Existential v1 = typing.top();
		// Sanity check typing
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), v1);
		}
		// Recursively check element operand
		nTyping = inferExpression(new Type.ProjectArray(v1), expr.getFirstOperand(), nTyping, environment);
		// Recursively check length operand
		return inferExpression(Type.Int, expr.getSecondOperand(), nTyping, environment);
	}

	private Typing inferArrayAccess(Type target, Expr.ArrayAccess expr, Typing typing, Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr);
		// Extract allocated variable
		Type.Existential v1 = nTyping.top();
		// Recursively check source operand
		nTyping = inferExpression(new Type.Array(v1), expr.getFirstOperand(), nTyping, environment);
		// Recursively check index operand
		return inferExpression(Type.Int, expr.getSecondOperand(), nTyping, environment);
	}

	private Typing inferArrayRange(Type  target, Expr.ArrayRange expr, Typing typing, Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, Type.IntArray, expr);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), Type.IntArray);
		}
		// Recursively check start operand
		nTyping = inferExpression(Type.Int, expr.getFirstOperand(), nTyping, environment);
		// Recursively check end operand
		return inferExpression(Type.Int, expr.getSecondOperand(), nTyping, environment);
	}

	private Typing inferBitwiseOperator(Type target, Expr.UnaryOperator expr, Typing typing,
			Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, Type.Byte, expr);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), Type.Byte);
		}
		// Recursively check children
		return inferExpression(Type.Byte, expr.getOperand(), nTyping, environment);
	}

	private Typing inferBitwiseOperator(Type target, Expr.NaryOperator expr, Typing typing,
			Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, Type.Byte, expr);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), Type.Byte);
		}
		return inferExpressions(Type.Byte, expr.getOperands(), nTyping, environment);
	}

	private Typing inferBitwiseShift(Type target, Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, Type.Byte, expr);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), Type.Byte);
		}
		// Recursively check children
		nTyping = inferExpression(Type.Byte, expr.getFirstOperand(), nTyping, environment);
		return inferExpression(Type.Int, expr.getSecondOperand(), nTyping, environment);
	}

	private Typing inferCast(Type target, Expr.Cast expr, Typing typing, Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr, expr.getType());
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), expr.getType());
		}
		// Allocate existential to pass forward
		typing = nTyping.push(getUnderlyingType(expr.getType(), null), expr);
		Type.Existential v = typing.top();
		// Recursively check children
		return inferExpression(v, expr.getOperand(), nTyping, environment);
	}

	private Typing inferConditionExpression(Type target, Expr expr, Typing typing, Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr, Type.Bool);
		// Sanity check typing
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), Type.Bool);
		}
		// Check condition
		checkCondition(expr, true, environment);
		// Always return bool
		return nTyping;
	}

	/**
	 * Check the type of a given constant expression. This is straightforward since
	 * the determine is fully determined by the kind of constant we have.
	 *
	 * @param expr
	 * @return
	 */
	private Typing inferConstant(Type target, Expr.Constant expr, Typing typing, Environment environment) {
		// Determine underlying type
		Type type = typeOf(expr.getValue());
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, type, expr);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), type);
		}
		return nTyping;
	}

	private Typing inferDereference(Type target, Expr.Dereference expr, Typing typing, Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr);
		Type.Existential v1 = typing.top();
		// Recursively check source operand
		return inferExpression(new Type.Reference(v1), expr.getOperand(), nTyping, environment);
	}

	private Typing inferEqualityOperator(Type target, Expr.BinaryOperator expr, Typing typing,
			Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, Type.Bool, expr);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), Type.Bool);
		}
		// Construct fresh variables representing equality type
		nTyping = inferExpression(Type.Any, expr.getFirstOperand(), nTyping, environment);
		return inferExpression(Type.Any, expr.getSecondOperand(), nTyping, environment);
	}

	private Typing inferFieldDereference(Type target, Expr.FieldDereference expr, Typing typing,
			Environment environment) {
		//		// Allocate existential to represent reference element type
		//		wycc.util.Pair<Typing.Environment, Type.Existential> p = typing.allocate();
		//		// Force target to accept reference
		//		typing = p.first().bind(target, p.second());
		//		// Construct reference type we're expecting
		//		Type.Field[] fields = new Type.Field[] { new Type.Field(expr.getField(), p.second())};
		//		Type.Reference type = new Type.Reference(new Type.Record(true, new Tuple<>(fields)));
		//		// Recursively check source operand
		//		return inferExpression(type, expr.getOperand(), typing, environment);
		throw new RuntimeException("implement me");
	}

	private Typing inferIntegerComparator(Type target, Expr.BinaryOperator expr, Typing typing,
			Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, Type.Bool, expr);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), Type.Bool);
		}
		// Recursively check children
		nTyping = inferExpression(Type.Int, expr.getFirstOperand(), nTyping, environment);
		return inferExpression(Type.Int, expr.getSecondOperand(), nTyping, environment);
	}

	private Typing inferIntegerOperator(Type target, Expr.UnaryOperator expr, Typing typing,
			Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, Type.Int, expr);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), Type.Int);
		}
		// Recursively check children
		return inferExpression(Type.Int, expr.getOperand(), nTyping, environment);
	}

	/**
	 * Check the type for a given arithmetic operator. Such an operator has the type
	 * int, and all children should also produce values of type int.
	 *
	 * @param expr
	 * @return
	 */
	private Typing inferIntegerOperator(Type target, Expr.BinaryOperator expr, Typing typing,
			Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, Type.Int, expr);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), Type.Int);
		}
		// Recursively check children
		nTyping = inferExpression(Type.Int, expr.getFirstOperand(), nTyping, environment);
		return inferExpression(Type.Int, expr.getSecondOperand(), nTyping, environment);
	}

	private Typing inferIndirectInvoke(Type target, Expr.IndirectInvoke expr, Typing typing,
			Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr);
		// Save argument expressions for later
		Tuple<Expr> arguments = expr.getArguments();
		// Type source operand
		nTyping = inferExpression(Type.Any, expr.getSource(), nTyping, environment);
		// Sanity check types of arguments provided
		nTyping = inferExpressions(Type.Any, arguments,nTyping,environment);
		// FIXME: there is definitely more we need to do here.
		return typing;
		//		// Perform projection from source to its return type
		//		Typing nTyping = typing.map(row -> row.set(v_expr, getCallableReturn(row.get(v_source), v_operands.length)));
		//		// Finally, check for errors. This is tricky because there are lots of corner
		//		// cases.
		//		if (!typing.empty() && nTyping.empty()) {
		//			// Some kind of problem, but need to disambiguate first. To do this, we try to
		//			// project the source type into a callable. If this succeeds then we know a
		//			// different problem was the cause.
		//			Typing c1 = typing.map(row -> row.set(v_source, row.get(v_source).as(Type.Callable.class)));
		//			// Check whether have lambda
		//			if (c1.empty()) {
		//				syntaxError(expr.getSource(), EXPECTED_LAMBDA);
		//			} else {
		//				syntaxError(expr, INSUFFICIENT_ARGUMENTS);
		//			}
		//		} else {
		//			// Sanity check lambda parameters against arguments
		//			nTyping = checkInvocationParameters(nTyping,arguments,v_source);
		//		}
		//		// Done
		//		return nTyping;
	}

	private Typing inferInvoke(Type target, Expr.Invoke expr, Typing typing, Environment environment) {
		// Save argument expressions for later
		Tuple<Expr> arguments = expr.getOperands();
		//
		Decl.Link<Decl.Callable> link = expr.getLink();
		// Attempt to resolve this invocation (if we can).
		if (link.isPartiallyResolved()) {
			// Harder case. No signature given by user, so must fork constraints at this
			// point for each sensible candidate.
			List<Decl.Callable> candidates = link.getCandidates();
			if (candidates.size() > 1) {
				throw new IllegalArgumentException("implement me");
			}
			Decl.Callable candidate = candidates.get(0);
			Type.Callable t = candidate.getType();
			Type parameter = t.getParameter();
			// Allocate type variable(s) for this expression
			Typing nTyping = typing.push(target, expr, t.getReturn());
			// Sanity check parameters against arguments
			for (int i = 0; i != arguments.size(); ++i) {
				Type ith = parameter.dimension(i);
				nTyping = inferExpression(ith, arguments.get(i), nTyping, environment);
			}
			//
			return nTyping;
		} else {
			// NOTE: don't need to report syntax error since this is already done in
			// NameResolution.
			return typing.invalidate();
		}
	}

	/**
	 * Check the arguments for a given invocation (indirect or direct) can be bound
	 * to the expected parameter types.
	 *
	 * @param typing     The current typing
	 * @param arguments  The set of expressions which constitute the parameter types
	 * @param v_concrete The type variable in typing which identifies the invocation
	 *                   signature. This is necessary to extract the expected
	 *                   parameter types. The challenge is that this is located in
	 *                   different places for a direct invocation versus an indirect
	 *                   invocation.
	 * @return
	 */
	private Typing checkInvocationParameters(Typing typing, Tuple<Expr> arguments, int v_concrete) {
		//		// Save handle(s) for later
		//		int[] v_operands = typing.indexOf(arguments);
		//		// Sanity check parameters against arguments
		//		for (int i = 0; i != v_operands.length; ++i) {
		//			final int _i = i;
		//			int ith = v_operands[i];
		//			// Save current typing to enable identification of an error.
		//			Typing oTyping = typing;
		//			// Bind ith parameter type with ith argument type
		//			typing.map(row -> {
		//				return row;
		//			});
		//			typing = typing.map(row -> row.bind(getCallableArgument(row.get(v_concrete), _i), row.get(ith)));
		//			// Sanity check for errors
		//			if (!oTyping.empty() && typing.empty()) {
		//				// Concretize to eliminate existentials
		//				oTyping = oTyping.concretise();
		//				// An error has arisen as a direct result of binding this variable, therefore it
		//				// must be problematic.
		//				Tuple<Type> targs = new Tuple<>(map(oTyping.types(v_concrete), t -> getCallableArgument(t, _i)));
		//				// A type error has arisen directly as a result of this argument position.
		//				syntaxError(arguments.get(i), SUBTYPE_ERROR, targs, new Tuple<>(oTyping.types(arguments.get(i))));
		//			}
		//		}
		//		return typing;
		throw new RuntimeException("implement me");
	}

	private Typing inferLambdaAccess(Type target, Expr.LambdaAccess expr, Typing typing, Environment environment) {
		Decl.Link<Decl.Callable> link = expr.getLink();
		List<Decl.Callable> candidates = filterCandidates(link, expr.getParameterTypes());
		// Sanity check what was left
		if (candidates.size() == 0) {
			syntaxError(expr, AMBIGUOUS_CALLABLE);
			return typing.invalidate();
		} else {
			// Allocate type variable(s) for this expression
			typing = typing.push(target, expr);
			Type.Existential v = typing.top();
			// Fork typing rows based on candidates
			Typing nTyping = typing
					.project(row -> fork(candidates, c -> row.intersect(environment.isSubtype(v, c.getType()))));
			// Sanity check whether valid typing still possible
			if (!typing.empty() && nTyping.empty()) {
				// Extract all types from candidates
				Type[] ts = candidates.stream().map(c -> c.getType()).toArray(Type[]::new);
				// No valid typings remain!
				syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), new Tuple<>(ts));
			}
			return nTyping;
		}
	}

	private List<Decl.Callable> filterCandidates(Decl.Link<Decl.Callable> link, Type parameter) {
		ArrayList<Decl.Callable> result = new ArrayList<>();
		// FIXME: there is a problem here in that we cannot distinguish
		// between the case where no parameters were supplied and when
		// exactly zero arguments were supplied.
		if (parameter.shape() > 0) {
			// Easy case. Signature given by user, so just need to select and find it.
			List<Decl.Callable> candidates = link.getCandidates();
			// Select match
			for (int i = 0; i != candidates.size(); ++i) {
				Decl.Callable ith = candidates.get(i);
				Type p = ith.getType().getParameter();
				if (p.equals(parameter)) {
					// Matched!
					result.add(ith);
				}
			}
		} else if (link.isPartiallyResolved()) {
			// Harder case. No signature given by user, so must fork constraints at this
			// point for each sensible candidate.
			final List<Decl.Callable> candidates = link.getCandidates();
			// Allocate type variable(s) for this expression
			result.addAll(candidates);
		} else if (link.isResolved()) {
			result.add(link.getTarget());;
		}
		//
		return result;
	}

	private Typing inferLambdaDeclaration(Type target, Decl.Lambda expr, Typing typing, Environment environment) {
		Type params = Decl.Callable.project(expr.getParameters());
		// Type check the body of the lambda using the expected return types
		Type returns = checkExpression(expr.getBody(), Type.Any, environment);
		// Determine whether or not this is a pure or impure lambda.
		Type.Callable type;
		//
		if (isPure(expr.getBody())) {
			type = new Type.Function(params,returns);
		} else {
			type = new Type.Method(params,returns, expr.getCapturedLifetimes(), expr.getLifetimes());
		}
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr, type);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), new Tuple<>(type));
		}
		return nTyping;
	}

	private Typing inferNew(Type target, Expr.New expr, Typing typing,
			Environment environment) {
		//		wycc.util.Pair<Typing.Environment, Type.Existential> p = typing.allocate();
		//		// Determine reference (upper) bound
		//		Type element = p.second();
		//		// Force target to accept a reference
		//		Typing.Environment nTyping = bindExistential(p.first(), expr, target, new Type.Reference(element));
		//		// Sanity check typing
		//		if (!typing.empty() && nTyping.empty()) {
		//			// No valid typings remain!
		//			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), new Type.Reference(element));
		//		}
		//		// Recursively check operand
		//		return inferExpression(element, expr.getOperand(), typing, environment);
		throw new RuntimeException("implement me");
	}

	private Typing inferRecordAccess(Type target, Expr.RecordAccess expr, Typing typing,
			Environment environment) {
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr);
		Type.Existential v1 = typing.top();
		// Construct reference type we're expecting
		Type.Field[] fields = new Type.Field[] { new Type.Field(expr.getField(), v1)};
		Type.Record type = new Type.Record(true, new Tuple<>(fields));
		// Recursively check source operand
		return inferExpression(type, expr.getOperand(), nTyping, environment);
	}

	private Typing inferRecordInitialiser(Type target, Expr.RecordInitialiser expr, Typing typing,
			Environment environment) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr);
		Type.Existential v1 = typing.top();
		// Recursively infer children
		for (int i = 0; i != fields.size(); ++i) {
			Identifier field = fields.get(i);
			// Type field initialiser
			nTyping = inferExpression(new Type.ProjectRecord(v1,field,fields), operands.get(i), nTyping, environment);
		}
		// Done
		return nTyping;
	}

	private Typing inferStaticVariable(Type target, Expr.StaticVariableAccess expr, Typing typing,
			Environment environment) {
		// Extract variable link
		Decl.Link<Decl.StaticVariable> l = expr.getLink();
		// Extract type if applicable
		Type type = l.isResolved() ? l.getTarget().getType() : null;
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr, type);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), type);
		}
		return nTyping;
	}

	private Typing inferTupleInitialiser(Type target, Expr.TupleInitialiser expr, Typing typing,
			Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		final int n = operands.size();
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr);
		Type.Existential v1 = typing.top();
		// Recursively infer children
		for (int i = 0; i != n; ++i) {
			// Type field initialiser
			nTyping = inferExpression(new Type.ProjectTuple(v1,i,n), operands.get(i), nTyping, environment);
		}
		// Done
		return nTyping;
	}

	/**
	 * Check the type of a given variable access. This is straightforward since the
	 * determine is fully determined by the declared type for the variable in
	 * question.
	 *
	 * @param expr
	 * @return
	 */
	private Typing inferVariable(Type target, Expr.VariableAccess expr, Typing typing,
			Environment environment) {
		// Extract variable's active type
		Type type = environment.getType(expr.getVariableDeclaration());
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.push(target, expr, type);
		// Sanity check whether valid typing still possible
		if (!typing.empty() && nTyping.empty()) {
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, new Tuple<>(target), type);
		}
		return nTyping;
	}


	// ==========================================================================
	// Helpers
	// ==========================================================================

	/**
	 * Determine the underlying type of a given value.
	 *
	 * @param v
	 * @return
	 */
	private Type typeOf(Value v) {
		switch (v.getOpcode()) {
		case ITEM_null:
			return Type.Null;
		case ITEM_bool:
			return Type.Bool;
		case ITEM_byte:
			return Type.Byte;
		case ITEM_int:
			return Type.Int;
		case ITEM_utf8:
			return Type.IntArray;
			// break;
		default:
			return internalFailure("unknown constant encountered: " + v, v);
		}
	}

	/**
	 * Apply a given forking function to produce an array of forked environments.
	 *
	 * @param candidates The list of candidates for which the environment is forked
	 *                   where each candidate is forked into a new environment.
	 * @param fn         The forking function which takes a given callable
	 *                   declaration and produces a forked environment. This may
	 *                   return <code>null</code> to indicate no valid environment
	 *                   exists for the given declaration.
	 * @return
	 */
	public Subtyping.Constraints[] fork(List<Decl.Callable> candidates, Function<Decl.Callable, Subtyping.Constraints> fn) {
		Subtyping.Constraints[] constraints = new Subtyping.Constraints[candidates.size()];
		for(int i=0;i!=candidates.size();++i) {
			constraints[i] = fn.apply(candidates.get(i));
		}
		// Remove any invalid environments which have arisen.
		return ArrayUtils.removeAll(constraints,null);
	}

	public Subtyping.Constraints initialiseCallableFrame(Subtyping.Constraints environment, Expr.Invoke expr,
			Decl.Callable candidate, int v_expr) {
		//		// Save handle(s) for later
		//		int v_signature = v_expr + 1;
		//		int v_concrete = v_expr + 2;
		//		int v_template = v_expr + 3;
		//		//
		//		Type.Callable signatureType = candidate.getType();
		//		// Sanity callable is even applicable
		//		if(signatureType.getParameter().shape() != expr.getOperands().size()) {
		//			// Declaration is not applicable as it doesn't accept the right number of
		//			// arguments.
		//			return null;
		//		} else {
		//			Tuple<Template.Variable> template = candidate.getTemplate();
		//			Tuple<SyntacticItem> templateArguments = expr.getBinding().getArguments();
		//			Type templateType = Type.Void;
		//			// Check whether need to infer template arguments
		//			if (template.size() > 0 && templateArguments.size() == 0) {
		//				// Template required, but no explicit arguments given. Therefore, we create
		//				// fresh (existential) type for each position and subsitute them through.
		//				wycc.util.Pair<Subtyping.Constraints, Type.Existential[]> p = environment.allocate(template.size());
		//				environment = p.first();
		//				templateArguments = new Tuple<>(p.second());
		//				templateType = Type.Tuple.create(p.second());
		//			}
		//			// Construct the concrete type.
		//			Type.Callable concreteType = WyilFile.substitute(signatureType, template, templateArguments);
		//			// Configure first meta-variable to hold actual signature
		//			environment = environment.set(v_signature, signatureType);
		//			// Configure second meta-variable to hold concrete signature
		//			environment = environment.set(v_concrete, concreteType);
		//			// Configure third meta-variable to hold template arguments
		//			environment = environment.set(v_template, templateType);
		//			// Done
		//			return environment.set(v_expr, concreteType.getReturn());
		//		}
		throw new RuntimeException("implement me");
	}

	/**
	 * Get the underlying type for a given type. For example, consider the following
	 * declarations:
	 *
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * type rec_t is {nat f, int g}
	 * </pre>
	 *
	 * Here, the underlying type of <code>nat</code> is <code>int</code>, whilst the
	 * underlying type of <code>rec_t</code> is <code>{int f, int g}</code>.
	 *
	 * @param type
	 * @param visited
	 * @return
	 */
	public static Type getUnderlyingType(Type type, Set<Name> visited) {
		// FIXME: this will need a visited cache
		switch (type.getOpcode()) {
		case TYPE_null:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
			return type;
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			Type element = t.getElement();
			Type nElement = getUnderlyingType(element, visited);
			if (element == nElement) {
				return type;
			} else {
				return new Type.Array(nElement);
			}
		}
		case TYPE_reference: {
			Type.Reference t = (Type.Reference) type;
			Type element = t.getElement();
			Type nElement = getUnderlyingType(element, visited);
			if (element == nElement) {
				return type;
			} else {
				return new Type.Reference(nElement);
			}
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> fields = t.getFields();
			Type.Field[] nFields = new Type.Field[fields.size()];
			boolean changed = false;
			for (int i = 0; i != nFields.length; ++i) {
				Type.Field field = fields.get(i);
				Type element = field.getType();
				Type nElement = getUnderlyingType(element, visited);
				if (element == nElement) {
					nFields[i] = field;
				} else {
					nFields[i] = new Type.Field(field.getName(), nElement);
					changed = true;
				}
			}
			if (changed) {
				return new Type.Record(t.isOpen(), new Tuple<>(nFields));
			} else {
				return type;
			}
		}
		case TYPE_property:
		case TYPE_function:
		case TYPE_method: {
			Type.Callable t = (Type.Callable) type;
			Type tParam = t.getParameter();
			Type tReturn = t.getReturn();
			Type nParam = getUnderlyingType(tParam, visited);
			Type nReturn = getUnderlyingType(tReturn, visited);
			if (tParam == nParam && tReturn == nReturn) {
				return type;
			} else if (t instanceof Type.Property) {
				return new Type.Property(nParam, nReturn);
			} else if (t instanceof Type.Function) {
				return new Type.Function(nParam, nReturn);
			} else {
				Type.Method m = (Type.Method) t;
				return new Type.Method(nParam, nReturn, m.getCapturedLifetimes(), m.getLifetimeParameters());
			}
		}
		case TYPE_nominal: {
			Type.Nominal t = (Type.Nominal) type;
			Name n = t.getLink().getName();
			if (visited != null && visited.contains(n)) {
				return type;
			} else {
				visited = (visited == null) ? new HashSet<>() : new HashSet<>(visited);
				visited.add(n);
				return getUnderlyingType(t.getConcreteType(), visited);
			}
		}
		}
		throw new IllegalArgumentException("invalid type");
	}

	public static <T> T[] map(T[] items, Function<T, T> fn) {
		T[] nitems = Arrays.copyOf(items, items.length);
		for (int i = 0; i != items.length; ++i) {
			nitems[i] = fn.apply(items[i]);
		}
		return nitems;
	}

	public Type refine(Type declared, Type selector) {
		// FIXME: this method is a hack for now really, until such time as I resolve
		// issues around subtyping and how to create proper type morphisms, etc.
		Type.Selector s = TypeSelector.create(declared, selector);
		if (s == Type.Selector.BOTTOM) {
			// Something went wrong
			return declared;
		} else {
			return s.apply(declared);
		}
	}

	/**
	 * Determine whether a given expression calls an impure method, dereferences a
	 * reference or accesses a static variable. This is done by exploiting the
	 * uniform nature of syntactic items. Essentially, we just traverse the entire
	 * tree representing the syntactic item looking for expressions of any kind.
	 *
	 * @param item
	 * @return
	 */
	private boolean isPure(Expr e) {
		FlowTypeUtils.PurityVisitor visitor = new FlowTypeUtils.PurityVisitor(meter);
		visitor.visitExpression(e);
		return visitor.pure;
	}

	private void checkIsWritable(Type type, SyntacticItem element) {
		if (type != null && !type.isWriteable()) {
			syntaxError(element, DEREFERENCED_DYNAMICALLY_SIZED, element);
		}
	}

	private void checkIsSubtype(Type lhs, Type rhs, Subtyping.Environment subtyping, SyntacticItem element) {
		if (lhs == null || rhs == null) {
			// A type error of some kind has occurred which has produced null instead of a
			// type. At this point, we proceed assuming everything is hunky dory untill we
			// can categorically find another problem.
		} else if (!subtyping.isSatisfiableSubtype(lhs, rhs)) {
			syntaxError(element, SUBTYPE_ERROR, lhs, rhs);
		}
	}

	private void checkContractive(Decl.Type d, Subtyping.Environment subtyping) {
		if (!subtyping.isEmpty(d.getQualifiedName(), d.getType())) {
			syntaxError(d.getName(), EMPTY_TYPE);
		}
	}

	/**
	 * From a given record type, extract type for a given field.
	 */
	public Type extractFieldType(Type.Record type, Identifier field) {
		if (type == null) {
			return null;
		} else {
			Type fieldType = type.getField(field);
			if (fieldType == null) {
				// Indicates an invalid field selection
				syntaxError(field, INVALID_FIELD);
			}
			return fieldType;
		}
	}

	/**
	 * From an arbitrary type, extract a particular kind of type.
	 *
	 * @param kind
	 * @param type
	 * @param item
	 * @param errcode
	 * @return
	 */
	public <T extends Type> T extractType(Class<T> kind, Type type, int errcode, SyntacticItem item) {
		if (type == null) {
			// indicates failure upstream
			return null;
		} else if (kind.isInstance(type)) {
			return (T) type;
		} else if (type instanceof Type.Nominal) {
			Type.Nominal t = (Type.Nominal) type;
			return extractType(kind, t.getConcreteType(), errcode, item);
		} else {
			syntaxError(item, errcode);
			return null; // deadcode
		}
	}

	private void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context);
	}

	private void syntaxError(SyntacticItem e, int code, List<? extends SyntacticItem> context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context.toArray(new SyntacticItem[context.size()]));
	}

	private <T> T internalFailure(String msg, SyntacticItem e) {
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntacticException(msg, cu.getEntry(), e);
	}

	private <T> T internalFailure(String msg, SyntacticItem e, Throwable ex) {
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntacticException(msg, cu.getEntry(), e, ex);
	}

	// ==========================================================================
	// Enclosing Scope
	// ==========================================================================

	/**
	 * An enclosing scope captures the nested of declarations, blocks and other
	 * statements (e.g. loops). It is used to store information associated with
	 * these things such they can be accessed further down the chain. It can also be
	 * used to propagate information up the chain (for example, the environments
	 * arising from a break or continue statement).
	 *
	 * @author David J. Pearce
	 *
	 */
	private abstract static class EnclosingScope {
		protected final EnclosingScope parent;

		public EnclosingScope(EnclosingScope parent) {
			this.parent = parent;
		}

		/**
		 * Get the innermost enclosing block of a given kind. For example, when
		 * processing a return statement we may wish to get the enclosing function or
		 * method declaration such that we can type check the return types.
		 *
		 * @param kind
		 */
		public <T> T getEnclosingScope(Class<T> kind) {
			if (kind.isInstance(this)) {
				return (T) this;
			} else if (parent != null) {
				return parent.getEnclosingScope(kind);
			} else {
				// FIXME: better error propagation?
				return null;
			}
		}
	}
		private interface LifetimeDeclaration {
			/**
			 * Get the list of all lifetimes declared by this or an enclosing scope. That is
			 * the complete set of lifetimes available at this point.
			 *
			 * @return
			 */
			public String[] getDeclaredLifetimes();
		}

	/**
	 * Represents the enclosing scope for a function or method declaration.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class FunctionOrMethodScope extends EnclosingScope implements LifetimeDeclaration {
		private final Decl.FunctionOrMethod declaration;

		public FunctionOrMethodScope(Decl.FunctionOrMethod declaration) {
			super(null);
			this.declaration = declaration;
		}

		public Decl.FunctionOrMethod getDeclaration() {
			return declaration;
		}

		@Override
		public String[] getDeclaredLifetimes() {
			if (declaration instanceof Decl.Method) {
				Decl.Method meth = (Decl.Method) declaration;
				Identifier[] lifetimes = meth.getLifetimes();
				String[] arr = new String[lifetimes.length + 1];
				for (int i = 0; i != lifetimes.length; ++i) {
					arr[i] = lifetimes[i].get();
				}
				arr[arr.length - 1] = "this";
				return arr;
			} else {
				return new String[] { "this" };
			}
		}
	}

	private static class NamedBlockScope extends EnclosingScope implements LifetimeDeclaration {
		private final Stmt.NamedBlock stmt;

		public NamedBlockScope(EnclosingScope parent, Stmt.NamedBlock stmt) {
			super(parent);
			this.stmt = stmt;
		}

		@Override
		public String[] getDeclaredLifetimes() {
			LifetimeDeclaration enclosing = parent.getEnclosingScope(LifetimeDeclaration.class);
			String[] declared = enclosing.getDeclaredLifetimes();
			declared = Arrays.copyOf(declared, declared.length + 1);
			declared[declared.length - 1] = stmt.getName().get();
			return declared;
		}
	}
}
