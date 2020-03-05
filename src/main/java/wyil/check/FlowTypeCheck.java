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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import wyal.util.NameResolver.ResolutionError;
import wybs.lang.*;
import wybs.util.ResolveError;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyil.check.FlowTypeUtils.*;
import wyil.util.*;
import wyil.util.Subtyping.Constraints.Solution;
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
				// NOTE: Void signal nothing is expected.
				checkExpression((Expr.Invoke) stmt, Type.Void, environment);
				return environment;
			case EXPR_indirectinvoke:
				// NOTE: Void signal nothing is expected.
				checkExpression((Expr.IndirectInvoke) stmt, Type.Void, environment);
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
		if (stmt.hasReturn() && type instanceof Type.Void) {
			// This doesn't make sense!!
			syntaxError(stmt.getReturn(), SUBTYPE_ERROR, Type.Void, getNaturalType(stmt.getReturn(), environment));
		} else if (stmt.hasReturn()) {
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
		//
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
			src = checkExpression(lval.getFirstOperand(), Type.Any, environment);
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
			src = checkExpression(lval.getOperand(), Type.Any, environment);
			// Extract record or fail
			recT = extractType(Type.Record.class, src, EXPECTED_RECORD, lval.getOperand());
		}
		// Extract the field type
		return extractFieldType(recT, lval.getField());
	}

	public Type checkDereferenceLVal(Expr.Dereference lval, Environment environment) {
		Type src = checkExpression(lval.getOperand(), Type.Any, environment);
		// Extract writeable reference type
		Type.Reference refT = extractType(Type.Reference.class, src, EXPECTED_REFERENCE, lval.getOperand());
		// Sanity check writability of reference
		if (refT != null) {
			checkIsWritable(refT.getElement(), lval.getOperand());
			return refT.getElement();
		} else {
			return null;
		}
	}

	public Type checkFieldDereferenceLVal(Expr.FieldDereference lval, Environment environment) {
		Type src = checkExpression(lval.getOperand(), Type.Any, environment);
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
		// Allocate variable for expression
		typing = typing.push((target == null) ? Type.Any : target);
		// >>> Propagate forwards into expression
		typing = inferExpression(typing.top(), expression, typing, environment);
		// Concretise typing to eliminate type variables
		typing = typing.concretise();
		// Attempt to collapse typings
		typing = typing.fold(Typing.Row.COMPARATOR(environment));
		// Attempt to finalise typing
		boolean ok = typing.finalise();
		// Sanity check what is left
		if(ok) {
			return expression.getType();
		} else {
			status = false;
			return null;
		}
	}

	public Typing inferExpression(int var, Expr expression, Typing typing, Environment environment) {
		meter.step("expression");
		//
		switch (expression.getOpcode()) {
		case EXPR_constant:
			return inferConstant(var, (Expr.Constant) expression, typing, environment);
		case EXPR_variablecopy:
			return inferVariable(var, (Expr.VariableAccess) expression, typing, environment);
		case EXPR_staticvariable:
			return inferStaticVariable(var, (Expr.StaticVariableAccess) expression, typing, environment);
		case EXPR_cast:
			return inferCast(var, (Expr.Cast) expression, typing, environment);
		case EXPR_invoke:
			return inferInvoke(var, (Expr.Invoke) expression, typing, environment);
		case EXPR_indirectinvoke:
			return inferIndirectInvoke(var, (Expr.IndirectInvoke) expression, typing, environment);
		case EXPR_logicalnot:
		case EXPR_logicalor:
		case EXPR_logicaland:
		case EXPR_logicaliff:
		case EXPR_logicalimplication:
		case EXPR_is:
		case EXPR_logicaluniversal:
		case EXPR_logicalexistential:
			return inferConditionExpression(var, expression, typing, environment);
		case EXPR_equal:
		case EXPR_notequal:
			return inferEqualityOperator(var, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
			return inferIntegerComparator(var, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_integernegation:
			return inferIntegerOperator(var, (Expr.UnaryOperator) expression, typing, environment);
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
			return inferIntegerOperator(var, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_bitwisenot:
			return inferBitwiseOperator(var, (Expr.UnaryOperator) expression, typing, environment);
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
			return inferBitwiseOperator(var, (Expr.NaryOperator) expression, typing, environment);
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
			return inferBitwiseShift(var, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_tupleinitialiser:
			return inferTupleInitialiser(var, (Expr.TupleInitialiser) expression, typing, environment);
		case EXPR_recordinitialiser:
			return inferRecordInitialiser(var, (Expr.RecordInitialiser) expression, typing, environment);
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			return inferRecordAccess(var, (Expr.RecordAccess) expression, typing, environment);
		case EXPR_arraylength:
			return inferArrayLength(var, (Expr.ArrayLength) expression, typing, environment);
		case EXPR_arrayinitialiser:
			return inferArrayInitialiser(var, (Expr.ArrayInitialiser) expression, typing, environment);
		case EXPR_arraygenerator:
			return inferArrayGenerator(var, (Expr.ArrayGenerator) expression, typing, environment);
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			return inferArrayAccess(var, (Expr.ArrayAccess) expression, typing, environment);
		case EXPR_arrayrange:
			return inferArrayRange(var, (Expr.ArrayRange) expression, typing, environment);
		case EXPR_dereference:
			return inferDereference(var, (Expr.Dereference) expression, typing, environment);
		case EXPR_fielddereference:
			return inferFieldDereference(var, (Expr.FieldDereference) expression, typing, environment);
		case EXPR_staticnew:
		case EXPR_new:
			return inferNew(var, (Expr.New) expression, typing, environment);
		case EXPR_lambdaaccess:
			return inferLambdaAccess(var, (Expr.LambdaAccess) expression, typing, environment);
		case DECL_lambda:
			return inferLambdaDeclaration(var, (Decl.Lambda) expression, typing, environment);
		default:
			return internalFailure("unknown expression encountered (" + expression.getClass().getSimpleName() + ")",
					expression);
		}
	}
	
	public Typing inferExpression(Expr expression, Typing typing, Environment environment) {
		return inferExpression(typing.top(), expression, typing, environment);
	}

	public Typing inferExpressions(Tuple<Expr> expressions, Typing typing, Environment environment) {
		return inferExpressions(typing.top(), expressions, typing, environment);
	}
	
	public Typing inferExpressions(int v_expr, Tuple<Expr> expressions, Typing typing, Environment environment) {
		for (int i = 0; i != expressions.size(); ++i) {
			typing = inferExpression(v_expr, expressions.get(i), typing, environment);
		}
		return typing;
	}

	private Typing inferExpressions(int[] children, Tuple<Expr> expressions, Typing typing, Environment environment) {
		for (int i = 0; i != expressions.size(); ++i) {
			// Forward propagate
			typing = inferExpression(children[i], expressions.get(i), typing, environment);
		}
		return typing;
	}

	private Typing inferArrayLength(int var, Expr.ArrayLength expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Terminate flow
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Int, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.Int);
		// >>> Propagate forwards into children
		return inferExpression(expr.getOperand(), typing.push(Type.AnyArray), environment);
	}

	private Typing inferArrayInitialiser(int var, Expr.ArrayInitialiser expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		Tuple<Expr> operands = expr.getOperands();
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Allocate variable for each operand
		Typing typing_1 = typing.project(row -> pushArrayElement(row, operands.size(), var, environment));
		// Sanity check for errors
		checkForError(expr, typing, typing_1, var, getNaturalType(expr, environment));
		// Identify allocated variable(s)
		int[] children = typing_1.top(operands.size());
		// >>> Propagate forwards into children
		typing_1 = inferExpressions(children, operands, typing_1, environment);
		// <<< Propagate backwards from children
		Typing typing_2;
		if(children.length == 0) {
			typing_2 = typing_1.pull(var,
					row -> filter(environment, row.get(var), Type.VoidArray));
		} else {
			typing_2 = typing_1.map(row -> pullArrayInitialiser(row, var, new Type.Array(Type.Union.create(row.getAll(children))),environment));
		}
		// Sanity check typing
		checkForError(expr, typing_1, typing_2, var, getNaturalType(expr, environment));		
		// Done
		return typing_2;
	}

	private Typing inferArrayGenerator(int var, Expr.ArrayGenerator expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Allocate variables for operands
		Typing typing_1 = typing.project(row -> pushArrayElement(row, 1, var, environment)).push(Type.Int);
		// Sanity check typing
		checkForError(expr, typing, typing_1, var, getNaturalType(expr, environment));
		// Identify allocated variable(s)
		int[] children = typing_1.top(2);
		// >>> Propagate forwards into children
		typing_1 = inferExpression(children[0], expr.getFirstOperand(), typing_1, environment);
		typing_1 = inferExpression(children[1], expr.getSecondOperand(), typing_1, environment);
		// <<< Propagate backwards from children
		Typing typing_2 = typing_1.pull(var,
				row -> filter(environment, row.get(var), new Type.Array(row.get(children[0]))));
		// Sanity check typing
		checkForError(expr, typing_1, typing_2, var, Type.Int);
		// Done
		return typing_2;
	}

	private Typing inferArrayAccess(int var, Expr.ArrayAccess expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Allocate variable for source operand
		typing = typing.map(row -> row.add(new Type.Array(row.get(var))));
		int src = typing.top();
		// >>> Propagate forwards into source operand
		typing = inferExpression(src, expr.getFirstOperand(), typing, environment);
		// <<< Propagate backwards from source operand
		Typing nTyping = typing.pull(var, src, t -> projectArrayElement(t));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.AnyArray);
		// >>> Propagate forwards into index operand
		return inferExpression(expr.getSecondOperand(), nTyping.push(Type.Int), environment);
	}

	private Typing inferArrayRange(int var, Expr.ArrayRange expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Convert source into array
		Typing nTyping = typing.map(row -> filterOnIntArray(row, var, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.IntArray);
		// >>> Propagate forwards into children
		nTyping = inferExpression(expr.getFirstOperand(), nTyping.push(Type.Int), environment);
		return inferExpression(expr.getSecondOperand(), nTyping.push(Type.Int), environment);
	}

	private Typing inferBitwiseOperator(int var, Expr.UnaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = inferExpression(expr.getOperand(), typing.push(Type.Byte), environment);
		// <<< Propagate backwards into parent
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Byte, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.Byte);
		// Done
		return nTyping;
	}

	private Typing inferBitwiseOperator(int var, Expr.NaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = inferExpressions(expr.getOperands(), typing.push(Type.Byte), environment);
		// <<< Propagate backwards into parent
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Byte, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.Byte);
		//
		return nTyping;
	}

	private Typing inferBitwiseShift(int var, Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// FIXME: needs updating --- order propagation
		
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// <<< Propagate backwards into parent
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Byte, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.Byte);
		// >>> Propagate forwards into children
		nTyping = inferExpression(expr.getFirstOperand(), nTyping.push(Type.Byte), environment);
		return inferExpression(expr.getSecondOperand(), nTyping.push(Type.Int), environment);
	}

	private Typing inferCast(int var, Expr.Cast expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		typing = typing.push(Type.Any);
		int child = typing.top();
		// >>> Propagate forwards into children
		typing = inferExpression(child, expr.getOperand(), typing, environment);
		// <<< Propagate backwards into parent
		Typing nTyping = typing.pull(var, row -> projectCast(environment, expr.getType(), row.get(child)));
		// Sanity check whether valid typing still possible
		checkForError(expr, typing, nTyping, expr.getType(), child);
		// Done
		return nTyping;
	}

	/**
	 * Project an actual type into a target type following a cast. This is tricky
	 * because we don't want to allow all possible casts. For example:
	 *
	 * <pre>
	 * int x = (int) "hello"
	 * </pre>
	 *
	 * This should not be permitted since there is no valid way to implement the
	 * cast.
	 *
	 * @param environment
	 * @param target
	 * @param actual
	 * @return
	 */
	private Type projectCast(Environment environment, Type target, Type actual) {
		// Strip away any invariants
		Type underlying = getUnderlyingType(actual,null);
		// NOTE: this doesn't handle type variables properly.
		Type glb = environment.greatestLowerBound(underlying, target);
		// Cannot actually return glb here, as must return target type to enable
		// upcasting for disambiguation purposes.
		return (glb instanceof Type.Void) ? glb : target;
	}

	private Typing inferConditionExpression(int var, Expr expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Bool, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.Bool);
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
	private Typing inferConstant(int var, Expr.Constant expr, Typing typing, Environment environment) {
		// Extract underling value type
		final Type type = typeOf(expr.getValue());
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// <<< Back propagate from type
		Typing typing_1;
		if(type instanceof Type.Primitive) { 
			typing_1 = typing.map(row -> filterOnPrimitive(row, var, (Type.Primitive) type, environment));
		} else {
			typing_1 = typing.map(row -> filterOnIntArray(row, var, environment));
		}
		// Sanity check whether valid typing still possible
		checkForError(expr, typing, typing_1, var, type);
		// Done
		return typing_1;
	}

	private Typing inferDereference(int var, Expr.Dereference expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Recursively check source operand
		typing = typing.push(Type.Any);
		int src = typing.top();
		// >>> Propagate forward into children
		typing = inferExpression(src, expr.getOperand(), typing, environment);
		// >>> Propagate backwards from children
		Typing nTyping = typing.pull(var, src, t -> projectReferenceElement(t));
		// Sanity check whether valid typing still possible
		checkForError(expr.getOperand(), typing, nTyping, var, t -> new Type.Reference(t), src);
		// Done
		return nTyping;
	}

	private Typing inferEqualityOperator(int var, Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = typing.push(Type.Any);
		int lhs = typing.top();
		typing = inferExpression(lhs, expr.getFirstOperand(), typing, environment);
		typing = typing.push(Type.Any);
		int rhs = typing.top();
		typing = inferExpression(rhs, expr.getSecondOperand(), typing, environment);
		// <<< Check operand (in)compatibility
		Typing typing_1 = typing.map(row -> disjoint(row.get(lhs), row.get(rhs), null) ? null : row);
		// Sanity check whether valid typing still possible
		checkForError(expr, INCOMPARABLE_OPERANDS, typing, typing_1, lhs, rhs);
		// <<< Propagate backwards from children
		Typing typing_2 = typing.map(row -> filterOnPrimitive(row, var, Type.Bool, environment));
		// Sanity check typing
		checkForError(expr, typing_1, typing_2, var, Type.Bool);
		// Done
		return typing_2;
	}

	private Typing inferFieldDereference(int var, Expr.FieldDereference expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Recursively check source operand
		typing = typing.push(Type.Any);
		// Identify allocated variable(s)
		int src = typing.top();
		// >>> Propagate forward into children
		typing = inferExpression(src, expr.getOperand(), typing, environment);
		// >>> Propagate backwards from children
		Typing nTyping = typing.pull(var, src, t -> projectReferenceField(t, expr.getField()));
		// Sanity check typing
		checkForError(expr.getOperand(), typing, nTyping, var, t -> fieldDeref(expr.getField(), t), src);
		// Done
		return nTyping;
	}

	private static Type.Reference fieldDeref(Identifier field, Type type) {
		Type.Field[] fields = new Type.Field[] {new Type.Field(field, type)};
		Type.Record r = new Type.Record(true, new Tuple(fields));
		return new Type.Reference(r);
	}

	private Typing inferIntegerComparator(int var, Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = inferExpression(expr.getFirstOperand(), typing.push(Type.Int), environment);
		typing = inferExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
		// <<< Propagate backwards
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Bool, environment));
		// Sanity check whether valid typing still possible
		checkForError(expr, typing, nTyping, var, Type.Bool);
		// Done
		return nTyping;
	}

	private Typing inferIntegerOperator(int var, Expr.UnaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = inferExpression(expr.getOperand(), typing.push(Type.Int), environment);
		// <<< Propagate backwards
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Int, environment));
		// Sanity check whether valid typing still possible
		checkForError(expr, typing, nTyping, var, Type.Int);
		// Done
		return nTyping;
	}

	/**
	 * Check the type for a given arithmetic operator. Such an operator has the type
	 * int, and all children should also produce values of type int.
	 *
	 * @param expr
	 * @return
	 */
	private Typing inferIntegerOperator(int var, Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = inferExpression(expr.getFirstOperand(), typing.push(Type.Int), environment);
		typing = inferExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
		// <<< Propagate backwards
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Int, environment));
		// Sanity check whether valid typing still possible
		checkForError(expr, typing, nTyping, var, Type.Int);
		// Done
		return nTyping;
	}

	private Typing inferIndirectInvoke(int var, Expr.IndirectInvoke expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Save argument expressions for later
		Tuple<Expr> arguments = expr.getArguments();
		// Allocate space for source operand
		typing = typing.pushAll(1,
				(row, i) -> new Type[] { new Type.Method(Type.Void, row.get(var), new Tuple<>(), new Tuple<>()) });
		int src = typing.top();
		// >>> Propagate forwards into children
		typing = inferExpression(src, expr.getSource(), typing, environment);
		// NOTE: this is unfortunately due to void versus tuple problem
		Typing tmp = typing.map(row -> hasSufficientArguments(row.get(src), arguments.size()) ? row : null);
		// Sanity check whether valid typing still possible
		if (!typing.isEmpty() && tmp.isEmpty()) {
			// Concretise to avoid variables in error messages
			typing = typing.concretise();
			// No valid typings remain!
			syntaxError(expr, INSUFFICIENT_ARGUMENTS);
		}
		//
		for (int i = 0; i != arguments.size(); ++i) {
			final int _i = i;
			// Project out the ith parameter type
			typing = typing.map(row -> row.add(projectAsParameter(row.get(src), _i)));
			// Forward propagate into argument expression
			typing = inferExpression(typing.top(), arguments.get(i), typing, environment);
		}
		// <<< Propagate backwards from return type
		Typing nTyping = typing.pull(var, src, t -> projectAsReturn(t));
		// Sanity check whether valid typing still possible
		checkForError(expr, typing, nTyping, var, src, t -> projectAsReturn(t));
		// Done
		return nTyping;
	}

	public static boolean hasSufficientArguments(Type t, int n) {
		Type.Callable c = t.as(Type.Callable.class);
		return c != null && c.getParameter().shape() == n;
	}

	private Typing inferInvoke(int var, Expr.Invoke expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		// Extract link
		Decl.Link<Decl.Callable> link = expr.getLink();
		// Attempt to resolve this invocation (if we can).
		if (link.isPartiallyResolved()) {
			// Save argument expressions for later
			Tuple<Expr> arguments = expr.getOperands();
			final int n = arguments.size();
			// Select candidates with matching numbes of arguments
			List<Decl.Callable> candidates = select(link.getCandidates(), c -> c.getParameters().size() == n);
			// Sanity check we actually have some candidates!
			if(candidates.size() == 0) {
				syntaxError(link.getName(), AMBIGUOUS_CALLABLE, link.getCandidates());
				typing = typing.invalidate();
			}
			// Allocate space for the signature variables. This is used to identify each row
			// in the typing matrix with one of candidates.
			typing = typing.push(Type.Any).push(Type.Any);
			int v_concrete = typing.top();
			int v_signature = v_concrete - 1;
			// Register a finaliser to resolve this invocation
			typing.register(typeInvokeExpression(expr, v_signature));
			// Fork typing into different cases for each overloaded candidate
			typing = typing.project(
					row -> fork(candidates, c -> initialiseCallableFrame(row, expr, c, v_signature, v_concrete)));
			// >>> Propagate forwards into children
			for (int i = 0; i < n; ++i) {
				final int _i = i;
				// Project out the ith parameter type
				typing = typing.map(row -> row.add(projectAsParameter(row.get(v_concrete), _i)));
				// Forward propagate into argument expression
				typing = inferExpression(typing.top(), arguments.get(i), typing, environment);
			}
			// <<< Propagate backwards from children
			Typing nTyping = typing.pull(var, v_concrete, t -> projectAsReturn(t));
			// Sanity check whether valid typing still possible
			if (!typing.isEmpty() && nTyping.isEmpty()) {
				// Concretise to avoid variables in error messages
				typing = typing.concretise();
				// Extract return types
				Tuple<Type> returns = typing.types(v_concrete).map(t -> projectAsReturn(t));
				// No valid typings remain!
				syntaxError(expr, SUBTYPE_ERROR, typing.types(var), returns);
			}
			//
			return nTyping;
		} else {
			// NOTE: don't need to report syntax error since this is already done in
			// NameResolution.
			return typing.invalidate();
		}
	}

	private Typing.Row initialiseCallableFrame(Typing.Row row, Expr.Invoke expr, Decl.Callable candidate, int v_sig,
			int v_concrete) {
		Type.Callable signatureType = candidate.getType();
		Tuple<Template.Variable> template = candidate.getTemplate();
		Tuple<SyntacticItem> templateArguments = expr.getBinding().getArguments();
		// Check whether need to infer template arguments
		if (template.size() > 0 && templateArguments.size() == 0) {
			// Template required, but no explicit arguments given. Therefore, we create
			// fresh (existential) type for each position and subsitute them through.
			wycc.util.Pair<Typing.Row,Type.Existential[]> p = row.fresh(template.size());
			row = p.first();
			templateArguments = new Tuple<>(p.second());
		}
		// Construct the concrete type.
		Type.Callable concreteType = WyilFile.substitute(signatureType, template, templateArguments);
		// Configure first meta-variable to hold actual signature
		row = row.set(v_sig, signatureType);
		// Configure second meta-variable to hold concrete signature
		row = row.set(v_concrete, concreteType);
		//
		return row;
	}

	private Type projectAsReturn(Type t) {
		Type.Callable c = t.as(Type.Callable.class);
		return (c != null) ? c.getReturn() : null;
	}

	private Type projectAsParameter(Type t, int i) {
		Type.Callable c = t.as(Type.Callable.class);
		if (c != null) {
			Type parameter = c.getParameter();
			if (i < parameter.shape()) {
				return parameter.dimension(i);
			}
		}
		return Type.Void;
	}

	private Typing inferLambdaAccess(int var, Expr.LambdaAccess expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		Decl.Link<Decl.Callable> link = expr.getLink();
		Type types = expr.getParameterTypes();
		//
		List<Decl.Callable> candidates;
		// FIXME: there is a problem here in that we cannot distinguish
		// between the case where no parameters were supplied and when
		// exactly zero arguments were supplied.
		if (types.shape() > 0) {
			candidates = select(link.getCandidates(), d -> d.getType().getParameter().equals(types));
		} else if (link.isResolved()) {
			// Link already resolved (e.g. because was only one candidate).
			candidates = select(link.getCandidates(), d -> true);
		} else if (link.isPartiallyResolved()) {
			// Harder case. No signature given by user, so must fork constraints at this
			// point for each sensible candidate.
			candidates = link.getCandidates();
		} else {
			return typing.invalidate();
		}
		typing = typing.push(Type.Any);
		int sig = typing.top();
		// Fork typing rows based on candidates
		typing = typing.project(row -> fork(candidates, c -> row.set(sig, c.getType())));
		// Allocate a finaliser for this expression
		typing.register(typeLambdaAccess(expr, typing.top()));
		// Sanity check expected type
		Typing nTyping = typing.pull(var, sig, t -> t);
		// Check whether valid typing still possible
		if (!typing.isEmpty() && nTyping.isEmpty()) {
			// Concretise to avoid variables in error messages
			typing = typing.concretise();
			// Extract all types from candidates
			Type[] ts = candidates.stream().map(c -> c.getType()).toArray(Type[]::new);
			// No valid typings remain!
			syntaxError(expr, SUBTYPE_ERROR, typing.types(var), new Tuple<>(ts));
		}
		return nTyping;
	}

	private static <T> List<T> select(List<T> items, Predicate<T> critereon) {
		ArrayList<T> result = new ArrayList<>();
		for (int i = 0; i != items.size(); ++i) {
			T ith = items.get(i);
			if (critereon.test(ith)) {
				result.add(ith);
			}
		}
		return result;
	}

	private Typing inferLambdaDeclaration(int var, Decl.Lambda expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Extract parameters from declaration
		Type params = Decl.Callable.project(expr.getParameters());
		// Type check the body of the lambda using the expected return types
		Type returns = checkExpression(expr.getBody(), Type.Void, environment);
		// Determine whether or not this is a pure or impure lambda.
		Type.Callable type;
		//
		if (returns == null) {
			// Error has occurred upstream, so simply propagate this down.
			return typing.invalidate();
		} else if (isPure(expr.getBody())) {
			type = new Type.Function(params, returns);
		} else {
			type = new Type.Method(params, returns, expr.getCapturedLifetimes(), expr.getLifetimes());
		}
		// Allocate type variable(s) for this expression
		Typing nTyping = typing.pull(var, row -> filter(environment, row.get(var), type));
		// Sanity check whether valid typing still possible
		checkForError(expr, typing, nTyping, var, type);
		//
		return nTyping;
	}

	private Typing inferNew(int var, Expr.New expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Allocate reference element
		Typing nTyping = typing.pushAll(1, (row, i) -> pushReferenceElement(row, var));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, getNaturalType(expr, environment));
		int elm = nTyping.top();
		// Recursively check source operand
		typing = inferExpression(elm, expr.getOperand(), nTyping, environment);
		if(expr.hasLifetime()) {
			nTyping = typing.pull(var, elm, t -> new Type.Reference(t,expr.getLifetime()));
		} else {
			nTyping = typing.pull(var, elm, t -> new Type.Reference(t));
		}
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, getNaturalType(expr, environment));
		//
		return nTyping;
	}

	private Typing inferRecordAccess(int var, Expr.RecordAccess expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Recursively check source operand
		typing = typing.push(Type.Any);
		int src = typing.top();
		// >>> Propagate forwards into children
		typing = inferExpression(src, expr.getOperand(), typing, environment);
		// <<< Propagate backwards from children
		Typing nTyping = typing.pull(var, src, t -> projectRecordField(t, expr.getField()));
		// Sanity check typing
		if (!typing.isEmpty() && nTyping.isEmpty()) {
			Type.Record src_t = getNaturalType(expr.getOperand(),environment).as(Type.Record.class);
			// Concretise to avoid variables in error messages
			typing = typing.concretise();
			// Decide what kind of problem we have
			if(src_t != null && src_t.getField(expr.getField()) == null) {
				syntaxError(expr, INVALID_FIELD, expr.getField());
			} else if(src_t != null) {
				syntaxError(expr, SUBTYPE_ERROR, typing.types(var), src_t.getField(expr.getField()));
			} else {
				// Fall back
				Tuple<Type> r = typing.types(var).map(t -> new Type.Record(true,new Tuple<>(new Type.Field[] {new Type.Field(expr.getField(), t)})));
				// No valid typings remain!
				syntaxError(expr.getOperand(), SUBTYPE_ERROR, r, typing.types(src));
			}
		}
		// Recursively check index operand
		return nTyping;
	}

	private Typing inferRecordInitialiser(int var, Expr.RecordInitialiser expr, Typing typing,
			Environment environment) {
		// FIXME: needs updating
		//
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		// Allocate finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Allocate variable for each operand
		Typing typing_1 = typing.pushAll(operands.size(), (row, i) -> pushRecordField(row, var, i, fields));
		// Sanity check for errors
		checkForError(expr, typing, typing_1, var, getNaturalType(expr, environment));
		// Identify allocated variable(s)
		int[] children = typing_1.top(operands.size());
		// >>> Propagate forwards into children
		typing_1 = inferExpressions(children, operands, typing_1, environment);
		// <<< Propagate backwards from children
		Typing typing_2 = typing_1.map(row -> pullRecordInitialiser(row, var, children, fields, environment));
		// Sanity check
		checkForError(expr, typing_1, typing_2, var, children, ts -> new Type.Record(false, fields, ts));
		// Done
		return typing_2;
	}

	private Typing inferStaticVariable(int var, Expr.StaticVariableAccess expr, Typing typing,
			Environment environment) {
		// Extract variable link
		Decl.Link<Decl.StaticVariable> l = expr.getLink();
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Extract type if applicable
		Type type = l.isResolved() ? l.getTarget().getType() : null;
		// Terminate this typing
		Typing nTyping = typing.map(row -> filterOnSubtype(row, var, type, environment));
		// Sanity check for errors
		checkForError(expr, typing, nTyping, var, type);
		// Done
		return nTyping;
	}

	private Typing inferTupleInitialiser(int var, Expr.TupleInitialiser expr, Typing typing, Environment environment) {
		// FIXME: needs updating
		//
		Tuple<Expr> operands = expr.getOperands();
		final int n = operands.size();
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Allocate variable for each operand
		Typing typing_1 = typing.pushAll(operands.size(), (row, i) -> pushTupleElement(row, var, i, n));
		// Sanity check for errors
		checkForError(expr, typing, typing_1, var, getNaturalType(expr, environment));
		// Identify allocated variable(s)
		int[] children = typing_1.top(operands.size());
		// >>> Propagate forwards into children
		typing_1 = inferExpressions(children, operands, typing_1, environment);
		// <<< Propagate backwards from children
		Typing typing_2 = typing_1.map(row -> pullTupleInitialiser(row, var, children, environment));
		// Sanity check for errors
		checkForError(expr, typing_1, typing_2, var, getNaturalType(expr, environment));
		// Done
		return typing_2;
	}

	/**
	 * Check the type of a given variable access. This is straightforward since the
	 * determine is fully determined by the declared type for the variable in
	 * question.
	 *
	 * @param expr
	 * @return
	 */
	private Typing inferVariable(int var, Expr.VariableAccess expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Extract variable's active type
		Type type = environment.getType(expr.getVariableDeclaration());
		// Terminate this typing
		Typing nTyping = typing.map(row -> filterOnSubtype(row, var, type, environment));
		// Sanity check whether valid typing still possible
		checkForError(expr, typing, nTyping, var, type);
		//
		return nTyping;
	}

	// ==========================================================================
	// Helpers
	// ==========================================================================

	

	/**
	 * Filter an initial (over approximated) type based on a given primitive type.
	 * For example:
	 *
	 * <pre>
	 * nat|null x = 1
	 * </pre>
	 *
	 * For expression <code>1</code>, the initial type would be
	 * <code>nat|null</code> which we want to filter down to just <code>nat</code>
	 * based on the expression's natural type <code>int</code>.
	 *
	 * @param row  The row in which this filtering is occurring.
	 * @param var  The variables whose type is being filtered.
	 * @param kind The underlying primitive kind used for filtering.
	 * @return
	 */
	private static Typing.Row filterOnPrimitive(Typing.Row row, int var, Type.Primitive underlying,
			Subtyping.Environment subtyping) {
		Type type = row.get(var);
		if (type instanceof Type.Any || type instanceof Type.Void) {
			// In this case, no forward information about variable types. Hence, must use
			// its natural type.
			return row.set(var, underlying);
		} else if (type instanceof Type.Existential) {
			// NOTE: this is something of a bandaid because there are other cases which
			// could manifest themselves, such as unions involving type variables. This also
			// doesn't cover the case where the type variable needs to be a subtype of the
			// underlying type.
			return row.intersect(subtyping.isSubtype(type, underlying));
		} else {
			List<Type> candidates = new ArrayList<>();
			Class<? extends Type.Primitive> kind = underlying.getClass();
			filterOnUnderlying(type, kind, candidates);
			// Sanity check only one found, otherwise have ambiguity.
			if (candidates.size() == 1) {
				return row.set(var, candidates.get(0));
			} else {
				return null;
			}
		}
	}

	/**
	 * Filter an initial (over approximated) type based on a string. For example:
	 *
	 * <pre>
	 * string|null x = "hello"
	 * </pre>
	 *
	 * For expression <code>"hello"</code>, the initial type would be
	 * <code>string|null</code> which we want to filter down to just
	 * <code>string</code> based on the expression's natural type
	 * <code>int[]</code>.
	 *
	 * @param row  The row in which this filtering is occurring.
	 * @param var  The variables whose type is being filtered.
	 * @param kind The underlying primitive kind used for filtering.
	 * @return
	 */
	private static Typing.Row filterOnIntArray(Typing.Row row, int var, Subtyping.Environment subtyping) {
		Type type = row.get(var);
		if (type instanceof Type.Any || type instanceof Type.Void) {
			// In this case, no forward information about variable types. Hence, must use
			// its natural type.
			return row.set(var, Type.IntArray);
		} else {
			List<Type> candidates = new ArrayList<>();
			filterOnUnderlying(type, Type.Array.class, candidates);
			// Filter out any non-integer arrays which were accidentally netted
			candidates = select(candidates, t -> subtyping.isSatisfiableSubtype(Type.IntArray, t));
			// Sanity check only one found, otherwise have ambiguity.
			if (candidates.size() == 1) {
				return row.set(var, candidates.get(0));
			} else {
				return null;
			}
		}
	}

	private static void filterOnUnderlying(Type candidate, Class<? extends Type> underlying, List<Type> candidates) {
		//
		Type atom = candidate.as(underlying);
		//
		if (atom != null) {
			// Matched greatest atom
			candidates.add(candidate);
		} else if (candidate instanceof Type.Union) {
			// NOTE: need to break type down in order to catch cases such as e.g. int :>
			// pos|neg.
			Type.Union t = (Type.Union) candidate;
			for (int i = 0; i != t.size(); ++i) {
				filterOnUnderlying(t.get(i), underlying, candidates);
			}
		} else if (candidate instanceof Type.Nominal) {
			// FIXME: this is broken really because we end up with a type for the expression
			// which is not a subtype of its parent. The following illustrates:
			//
			// > type nint is null|int
			// >
			// > nint x = 1
			//
			// Here, we end up with 1 having type int.
			Type.Nominal t = (Type.Nominal) candidate;
			filterOnUnderlying(t.getConcreteType(), underlying, candidates);
		}
	}

	/**
	 * Filter an initial (over approximated) type based on a given subtype. For
	 * example:
	 *
	 * <pre>
	 * nat x = 1
	 * int|null y = x
	 * </pre>
	 *
	 * For expression <code>x</code>, the initial type would be
	 * <code>int|null</code> which we want to filter down to <code>int</code>.
	 *
	 * @param row  The row in which this filtering is occurring.
	 * @param var  The variables whose type is being filtered.
	 * @param kind The underlying primitive kind used for filtering.
	 * @return
	 */
	private static Typing.Row filterOnSubtype(Typing.Row row, int var, Type subtype, Environment environment) {
		Type supertype = row.get(var);
		if (supertype instanceof Type.Any || supertype instanceof Type.Void) {
			return row.set(var, subtype);
		} else {
			// Apply the subtype tests which, if it fails, it will falsify this row.
			Subtyping.Constraints constraints = environment.isSubtype(supertype, subtype);
			// Set the type of this variable to the subtype (since this is fixed) and apply
			// any constraints on the underlying type variables.
			return row.set(var, subtype).intersect(constraints);
		}
	}

	/**
	 * Check whether two types are completely disjoint. For example,
	 * <code>bool</code> and <code>int</code> are disjoint, whilst
	 * <code>int|null</code> and <code>int</code> are not. This is used to determine
	 * whether an equality comparison makes any possible sense. More specifically,
	 * whether there is any interpretation under which these two types could be
	 * equal or not.
	 *
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static boolean disjoint(Type t1, Type t2, Set<Name> visited) {
		int t1_opcode = Subtyping.AbstractEnvironment.normalise(t1.getOpcode());
		int t2_opcode = Subtyping.AbstractEnvironment.normalise(t2.getOpcode());
		//
		if (t1_opcode == t2_opcode) {
			switch (t1_opcode) {
			case TYPE_any:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
			case TYPE_null:
			case TYPE_void:
			case TYPE_existential:
				return false;
			case TYPE_universal: {
				Type.Universal v1 = (Type.Universal) t1;
				Type.Universal v2 = (Type.Universal) t2;
				return !v1.getOperand().toString().equals(v2.getOperand().toString());
			}
			case TYPE_array: {
				Type.Array a1 = (Type.Array) t1;
				Type.Array a2 = (Type.Array) t2;
				return disjoint(a1.getElement(), a2.getElement(), visited);
			}
			case TYPE_staticreference:
			case TYPE_reference: {
				Type.Reference a1 = (Type.Reference) t1;
				Type.Reference a2 = (Type.Reference) t2;
				// NOTE: could potentially do better here by examining lifetimes.
				return disjoint(a1.getElement(), a2.getElement(), visited);
			}
			case TYPE_function:
			case TYPE_method:
			case TYPE_property: {
				Type.Callable c1 = (Type.Callable) t1;
				Type.Callable c2 = (Type.Callable) t2;
				return disjoint(c1.getParameter(), c2.getParameter(), visited)
						|| disjoint(c1.getReturn(), c2.getReturn(), visited);
			}
			case TYPE_nominal: {
				Type.Nominal n1 = (Type.Nominal) t1;
				Type.Nominal n2 = (Type.Nominal) t2;
				Name n = n1.getLink().getName();
				if (visited != null && visited.contains(n)) {
					return false;
				} else {
					visited = (visited == null) ? new HashSet<>() : new HashSet<>(visited);
					visited.add(n);
					return disjoint(n1.getConcreteType(), n2.getConcreteType(), visited);
				}
			}
			case TYPE_tuple: {
				Type.Tuple u1 = (Type.Tuple) t1;
				Type.Tuple u2 = (Type.Tuple) t2;
				if (u1.size() != u2.size()) {
					return true;
				} else {
					for (int i = 0; i != u1.size(); ++i) {
						if (disjoint(u1.get(i), u2.get(i), visited)) {
							return true;
						}
					}
					return false;
				}
			}
			case TYPE_union: {
				Type.Union u1 = (Type.Union) t1;
				Type.Union u2 = (Type.Union) t2;
				for (int i = 0; i != u1.size(); ++i) {
					for (int j = 0; j != u2.size(); ++j) {
						if (!disjoint(u1.get(i), u2.get(i), visited)) {
							return false;
						}
					}
				}
				return true;
			}
			case TYPE_record: {
				Type.Record r1 = (Type.Record) t1;
				Type.Record r2 = (Type.Record) t2;
				Tuple<Type.Field> r1fs = r1.getFields();
				Tuple<Type.Field> r2fs = r2.getFields();
				//
				if (r1fs.size() < r2fs.size() && !r1.isOpen()) {
					return true;
				} else if (r1fs.size() > r2fs.size() && !r2.isOpen()) {
					return true;
				}
				for (int i = 0; i != r1fs.size(); ++i) {
					Type.Field f1 = r1fs.get(i);
					Type ft2 = r2.getField(f1.getName());
					if (ft2 != null && disjoint(f1.getType(), ft2, visited)) {
						return true;
					}
				}
				return false;
			}
			}
		} else if (t1 instanceof Type.Any || t2 instanceof Type.Any) {
			return false;
		} else if (t1 instanceof Type.Void || t2 instanceof Type.Void) {
			// NOTE: should only be possible for empty arrays
			return false;
		} else if (t1 instanceof Type.Existential || t2 instanceof Type.Existential) {
			return false;
		} else if (t1 instanceof Type.Union) {
			Type.Union u1 = (Type.Union) t1;
			for (int i = 0; i != u1.size(); ++i) {
				if (!disjoint(u1.get(i), t2, visited)) {
					return false;
				}
			}
			return true;
		} else if (t2 instanceof Type.Union) {
			Type.Union u2 = (Type.Union) t2;
			for (int i = 0; i != u2.size(); ++i) {
				if (!disjoint(t1, u2.get(i), visited)) {
					return false;
				}
			}
			return true;
		} else if (t1 instanceof Type.Nominal) {
			Type.Nominal n1 = (Type.Nominal) t1;
			Name n = n1.getLink().getName();
			if (visited != null && visited.contains(n)) {
				return false;
			} else {
				visited = (visited == null) ? new HashSet<>() : new HashSet<>(visited);
				visited.add(n);
				return disjoint(n1.getConcreteType(), t2, visited);
			}
		} else if (t2 instanceof Type.Nominal) {
			Type.Nominal n2 = (Type.Nominal) t2;
			return disjoint(t1, n2.getConcreteType(), visited);
		}

		return true;
	}

	/**
	 * A helper method which identifies the "natural" type of an expression.
	 *
	 * @param expression
	 * @param environment
	 * @return
	 */
	private Type getNaturalType(Expr expression, Environment environment) {
		switch (expression.getOpcode()) {
		case EXPR_constant:
			return typeOf(((Expr.Constant) expression).getValue());
		case EXPR_variablecopy:
			return environment.getType(((Expr.VariableAccess) expression).getVariableDeclaration());
		case EXPR_staticvariable: {
			Decl.Link<Decl.StaticVariable> l = ((Expr.StaticVariableAccess) expression).getLink();
			// Extract type if applicable
			return l.isResolved() ? l.getTarget().getType() : null;
		}
		case EXPR_cast: {
			Expr.Cast c = (Expr.Cast) expression;
			return c.getType();
		}
		case EXPR_invoke: {
			Expr.Invoke l = (Expr.Invoke) expression;
			List<Decl.Callable> types = l.getLink().getCandidates();
			Type[] ts = new Type[types.size()];
			for (int i = 0; i != ts.length; ++i) {
				ts[i] = types.get(i).getType().getReturn();
			}
			return Type.Union.create(ts);
		}
		case EXPR_indirectinvoke: {
			Expr.IndirectInvoke r = (Expr.IndirectInvoke) expression;
			Type.Callable src = getNaturalType(r.getSource(), environment).as(Type.Callable.class);
			return (src == null) ? Type.Any : src.getReturn();
		}
		case EXPR_logicalnot:
		case EXPR_logicalor:
		case EXPR_logicaland:
		case EXPR_logicaliff:
		case EXPR_logicalimplication:
		case EXPR_is:
		case EXPR_logicaluniversal:
		case EXPR_logicalexistential:
		case EXPR_equal:
		case EXPR_notequal:
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
			return Type.Bool;
		case EXPR_integernegation:
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
			return Type.Int;
		case EXPR_bitwisenot:
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
			return Type.Byte;
		case EXPR_tupleinitialiser: {
			Type[] types = getBackwardsTypes(((Expr.TupleInitialiser) expression).getOperands(), environment);
			return Type.Tuple.create(types);
		}
		case EXPR_recordinitialiser: {
			Expr.RecordInitialiser r = (Expr.RecordInitialiser) expression;
			Tuple<Identifier> fields = r.getFields();
			Type[] types = getBackwardsTypes(r.getOperands(), environment);
			Type.Field[] fs = new Type.Field[types.length];
			for (int i = 0; i != fields.size(); ++i) {
				fs[i] = new Type.Field(fields.get(i), types[i]);
			}
			return new Type.Record(false, new Tuple<>(fs));
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess r = (Expr.RecordAccess) expression;
			Type.Record src = getNaturalType(r.getOperand(), environment).as(Type.Record.class);
			if (src != null && src.getField(r.getField()) != null) {
				return src.getField(r.getField());
			} else {
				return Type.Any;
			}
		}
		case EXPR_arraylength:
			return Type.Int;
		case EXPR_arrayinitialiser: {
			Expr.ArrayInitialiser r = (Expr.ArrayInitialiser) expression;
			Type[] types = getBackwardsTypes(r.getOperands(), environment);
			return new Type.Array(Type.Union.create(types));
		}
		case EXPR_arraygenerator: {
			Expr.ArrayGenerator r = (Expr.ArrayGenerator) expression;
			return new Type.Array(getNaturalType(r.getFirstOperand(), environment));
		}
		case EXPR_arrayaccess:
		case EXPR_arrayborrow: {
			Expr.ArrayAccess r = (Expr.ArrayAccess) expression;
			Type.Array src = getNaturalType(r.getFirstOperand(), environment).as(Type.Array.class);
			return (src == null) ? Type.Any : src.getElement();
		}
		case EXPR_arrayrange:
			return Type.IntArray;
		case EXPR_dereference: {
			Expr.Dereference r = (Expr.Dereference) expression;
			Type.Reference src = getNaturalType(r.getOperand(), environment).as(Type.Reference.class);
			return (src == null) ? Type.Any : src.getElement();
		}
		case EXPR_fielddereference: {
			Expr.FieldDereference r = (Expr.FieldDereference) expression;
			Type.Reference src = getNaturalType(r.getOperand(), environment).as(Type.Reference.class);
			if (src != null) {
				Type.Record rec = src.getElement().as(Type.Record.class);
				if (rec != null && rec.getField(r.getField()) != null) {
					return rec.getField(r.getField());
				}
			}
			return Type.Any;
		}
		case EXPR_staticnew: {
			Expr.New r = (Expr.New) expression;
			return new Type.Reference(getNaturalType(r.getOperand(), environment));
		}
		case EXPR_new: {
			Expr.New r = (Expr.New) expression;
			return new Type.Reference(getNaturalType(r.getOperand(), environment), r.getLifetime());
		}
		case EXPR_lambdaaccess: {
			Expr.LambdaAccess l = (Expr.LambdaAccess) expression;
			List<Decl.Callable> types = l.getLink().getCandidates();
			Type[] ts = new Type[types.size()];
			for (int i = 0; i != ts.length; ++i) {
				ts[i] = types.get(i).getType();
			}
			return Type.Union.create(ts);
		}
		case DECL_lambda: {
			Decl.Lambda l = (Decl.Lambda) expression;
			Type ret = getNaturalType(l.getBody(), environment);
			Tuple<Type> params = l.getParameters().map(v -> v.getType());
			// FIXME: not much more we can do here
			return new Type.Function(Type.Tuple.create(params), ret);
		}
		default:
			return internalFailure("unknown expression encountered (" + expression.getClass().getSimpleName() + ")",
					expression);
		}
	}

	private Type[] getBackwardsTypes(Tuple<Expr> expressions, Environment environment) {
		Type[] types = new Type[expressions.size()];
		for (int i = 0; i != types.length; ++i) {
			types[i] = getNaturalType(expressions.get(i), environment);
		}
		return types;
	}

	/**
	 * Filter a given type based on an underlying type. For example, consider this:
	 *
	 * <pre>
	 * nat|null x = 1
	 * </pre>
	 *
	 * Here, the underlying type of <code>1</code> is <code>int</code>, and we want
	 * to use this to select <code>nat</code> from <code>nat|null</code>.
	 *
	 *
	 * @param environment
	 * @param type
	 * @param selector
	 * @return
	 */
	private static Type filter(Environment environment, Type type, Type selector) {
		if (type instanceof Type.Any || type instanceof Type.Void) {
			return selector;
		} else if (type instanceof Type.Existential) {
			return selector;
		}
		// Type f = filter2(environment,upper,lower);
		Type underlying = getUnderlyingType(type, null);
		//
		if (environment.isSatisfiableSubtype(underlying, selector)) {
			return type;
		} else {
			return Type.Void;
		}
	}

	private static Typing.Row pullArrayInitialiser(Typing.Row row, int var, Type lower, Environment environment) {
		Type type = row.get(var);
		//
		if (type instanceof Type.Any || type instanceof Type.Void) {
			// No forwards information provided, hence back propagate raw tuple.
			return row.set(var, lower);
		} else {
			// FIXME: the big question is why we would ever need to do anything else here?
//			// Forwards information provided, hence check it works
//			List<? extends Type.Array> ts = type.filter(Type.Array.class);
//			// Look for matching subtypes
//			Subtyping.Constraints constraints = isExactSubtype(ts, lower, environment);
//			//
//			return constraints == null ? null : row.intersect(constraints);
			return row;
		}
	}

	/**
	 * Project a given type as an array and extract its element type. Otherwise,
	 * return void.
	 *
	 * @param t
	 * @return
	 */
	private Type projectArrayElement(Type t) {
		Type.Array at = t.as(Type.Array.class);
		return (at == null) ? Type.Void : at.getElement();
	}

	/**
	 * Project a given type as a reference to a record with a given field, and then
	 * extract the type of that field. Otherwise, return void.
	 *
	 * @param t
	 * @param field
	 * @return
	 */
	private static Type projectReferenceField(Type t, Identifier field) {
		Type.Reference r = t.as(Type.Reference.class);
		return (r == null) ? Type.Void : projectRecordField(r.getElement(), field);
	}

	/**
	 * Project a given type as a record with a given field, and then extract the
	 * type of that field. Otherwise, return void.
	 *
	 * @param t
	 * @param field
	 * @return
	 */
	private static Type projectRecordField(Type t, Identifier field) {
		Type.Record r = t.as(Type.Record.class);
		if (r == null) {
			return Type.Void;
		} else {
			Type f = r.getField(field);
			return (f == null) ? Type.Void : f;
		}
	}

	/**
	 * Project a given type as a reference, and extract its element type. Otherwise,
	 * return void.
	 *
	 * @param t
	 * @return
	 */
	private static Type projectReferenceElement(Type t) {
		Type.Reference r = t.as(Type.Reference.class);
		return (r == null) ? Type.Void : r.getElement();
	}

	private static Typing.Row pullTupleInitialiser(Typing.Row row, int var, int[] elems, Environment environment) {
		Type type = row.get(var);
		Type[] types = row.getAll(elems);
		Type tup = Type.Tuple.create(types);
		//
		if (type instanceof Type.Any || type instanceof Type.Void) {
			// No forwards information provided, hence back propagate raw tuple.
			return row.set(var, tup);
		} else {
			// Forwards information provided, hence check it works
			List<? extends Type.Tuple> ts = type.filter(Type.Tuple.class);
			ts = select(ts, t -> t.size() == elems.length);
			// Look for matching subtypes
			Subtyping.Constraints constraints = isExactSubtype(ts, tup, environment);
			//
			return constraints == null ? null : row.intersect(constraints);

		}
	}

	private static Typing.Row pullRecordInitialiser(Typing.Row row, int var, int[] elems, Tuple<Identifier> fields,
			Environment environment) {
		Type type = row.get(var);
		Type.Field[] fs = project(row.getAll(elems), new Type.Field[elems.length],
				(t, i) -> new Type.Field(fields.get(i), t));
		Type.Record rec = new Type.Record(false, new Tuple<>(fs));
		//
		if (type instanceof Type.Any || type instanceof Type.Void) {
			// No forwards information provided, hence back propagate raw tuple.
			return row.set(var, rec);
		} else {
			// Forwards information provided, hence check it works
			List<? extends Type.Record> ts = type.filter(Type.Record.class);
			ts = select(ts, t -> hasMatchingFields(t, fields));
			// Look for matching subtypes
			Subtyping.Constraints constraints = isExactSubtype(ts, rec, environment);
			//
			return constraints == null ? null : row.intersect(constraints);
		}
	}

	private static Typing.Row[] pushArrayElement(Typing.Row row, int n, int var, Environment environment) {
		Type type = row.get(var);
		if (type instanceof Type.Any || type instanceof Type.Void) {
			return new Typing.Row[] { row.addAll(n, Type.Any) };
		} else if (type instanceof Type.Existential) {
			wycc.util.Pair<Typing.Row, Type.Existential[]> p = row.fresh(1);
			row = p.first();
			Type.Existential v = p.second()[0];
			Subtyping.Constraints constraints = environment.isSubtype(type, new Type.Array(v));
			row = row.intersect(constraints);
			return new Typing.Row[] { row.addAll(n, v) };
		} else {
			List<Type.Array> ts = type.filter(Type.Array.class);
			Typing.Row[] rows = new Typing.Row[ts.size()];
			for (int i = 0; i != rows.length; ++i) {
				Type elem = ts.get(i).getElement();
				rows[i] = row.addAll(n, elem);
			}
			return rows;
		}
	}

	private static Type[] pushTupleElement(Typing.Row row, int var, int index, int n) {
		Type type = row.get(var);
		if (type instanceof Type.Any || type instanceof Type.Void) {
			type = Type.Any;
		} else {
			List<Type.Tuple> types = type.filter(Type.Tuple.class);
			types = select(types, t -> t.size() == n);
			if (types.size() == 1) {
				type = types.get(0).get(index);
			} else {
				type = Type.Void;
			}
		}
		return new Type[] { type };
	}

	private static Type[] pushRecordField(Typing.Row row, int var, int index, Tuple<Identifier> fields) {
		Type type = row.get(var);
		if (type instanceof Type.Any || type instanceof Type.Void) {
			return new Type[] { Type.Any };
		} else {
			List<Type.Record> types = type.filter(Type.Record.class);
			types = select(types, t -> hasMatchingFields(t, fields));
			Type[] ts = new Type[types.size()];
			for (int i = 0; i != types.size(); ++i) {
				Type.Record r = types.get(i);
				Type ft = r.getField(fields.get(index));
				if (ft == null && r.isOpen()) {
					ts[i] = Type.Any;
				} else if (ft != null) {
					ts[i] = ft;
				} else {
					// Should be impossible
					ts[i] = Type.Void;
				}
			}
			return ts;
		}
	}

	/**
	 * Create a new variable representing the element of a reference type. For
	 * example:
	 *
	 * <pre>
	 * &nat p = new 1
	 * </pre>
	 *
	 * To type constant <code>1</code> we create a new variable whose type is the
	 * element of the target type <code>&nat</code> (i.e. <code>nat</code>). If the
	 * target is <code>any</code> then it means we have no useful target type and,
	 * hence, we backpropagate the type from the operand of <code>new</code> itself.
	 *
	 * @param row
	 * @param var
	 * @return
	 */
	private static Type[] pushReferenceElement(Typing.Row row, int var) {
		Type type = row.get(var);
		if (type instanceof Type.Any || type instanceof Type.Void) {
			return new Type[] { Type.Any };
		} else {
			List<Type.Reference> ts = type.filter(Type.Reference.class);
			Type[] types = new Type[ts.size()];
			for (int i = 0; i != types.length; ++i) {
				types[i] = ts.get(i).getElement();
			}
			return types;
		}
	}

	/**
	 * Extract all underlying types which match any one of a given set of kinds. For
	 * example, <code>nat</code> matches kind <code>int</code>.
	 *
	 * @param type
	 * @param kinds
	 * @return
	 */
	public static List<Type> filterAll(Type type, Class<? extends Type>... kinds) {
		ArrayList<Type> ts = new ArrayList<>();
		for (int i = 0; i != kinds.length; ++i) {
			ts.addAll(type.filter(kinds[i]));
		}
		return ts;
	}

	/**
	 * Check whether a given record type has matching fields. For example,
	 * <code>{int f, int g}</code> matches fields <code>f</code> and <code>g</code>
	 * whilst <code>{int f}</code> does not. One issue here is that we must also
	 * account for open records and, in this case, only the number of fields in the
	 * record is permitted to different from the target set (though must always be a
	 * subset). For example, <code>{int f, ...}</code> matches fields <code>f</code>
	 * and <code>g</code>.
	 *
	 * @param type
	 * @param fields
	 * @return
	 */
	private static boolean hasMatchingFields(Type.Record type, Tuple<Identifier> fields) {
		Tuple<Type.Field> t_fields = type.getFields();
		if (!type.isOpen() && fields.size() != t_fields.size()) {
			return false;
		} else if (fields.size() < t_fields.size()) {
			return false;
		} else {
			int n = 0;
			for (int i = 0; i != fields.size(); ++i) {
				if (type.getField(fields.get(i)) != null) {
					n++;
				}
			}
			return n == t_fields.size();
		}
	}

	private static Subtyping.Constraints isExactSubtype(List<? extends Type> uppers, Type lower,
			Environment environment) {
		Subtyping.Constraints cs = null;
		//
		for (int i = 0; i != uppers.size(); ++i) {
			Subtyping.Constraints ith = environment.isSubtype(uppers.get(i), lower);
			if (ith.isSatisfiable()) {
				if (cs != null) {
					return null;
				} else {
					cs = ith;
				}
			}
		}
		return cs;
	}

	private static <S, T> T[] project(S[] sources, T[] targets, BiFunction<S, Integer, T> fn) {
		for (int i = 0; i != sources.length; ++i) {
			targets[i] = fn.apply(sources[i], i);
		}
		return targets;
	}

	/**
	 * Assign the type of a regular expression.
	 *
	 * @param e
	 * @param var
	 * @return
	 */
	public Predicate<Typing.Row[]> typeStandardExpression(Expr e, int var) {
		SyntacticHeap heap = e.getHeap();
		return rows -> {
			if (rows.length != 1) {
				// invalid typing
				return false;
			} else {
				Type type = rows[0].get(var);
				e.setType(heap.allocate(type));
				return true;
			}
		};
	}

	public Predicate<Typing.Row[]> typeInvokeExpression(Expr.Invoke e, int sig) {
		Decl.Link<Decl.Callable> link = e.getLink();
		return rows -> {
			if (rows.length == 1) {
				Type.Callable signature = (Type.Callable) rows[0].get(sig);
				link.resolve(link.lookup(signature));
				return true;
			} else if (rows.length > 1) {
				syntaxError(link.getName(), AMBIGUOUS_CALLABLE, link.getCandidates());
			}
			return false;
		};
	}

	public Predicate<Typing.Row[]> typeLambdaAccess(Expr.LambdaAccess e, int sig) {
		Decl.Link<Decl.Callable> link = e.getLink();
		return rows -> {
			if (rows.length == 1) {
				Type.Callable signature = (Type.Callable) rows[0].get(sig);
				link.resolve(link.lookup(signature));
				return true;
			} else if (rows.length > 1) {
				syntaxError(link.getName(), AMBIGUOUS_CALLABLE, link.getCandidates());
			}
			return false;
		};
	}

	/**
	 * Determine the underlying type of a given constant value. For example,
	 * <code>1</code> has type <code>int</code>, etc.
	 *
	 * @param v The value to type
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
	public Typing.Row[] fork(List<Decl.Callable> candidates, Function<Decl.Callable, Typing.Row> fn) {
		Typing.Row[] constraints = new Typing.Row[candidates.size()];
		for (int i = 0; i != candidates.size(); ++i) {
			constraints[i] = fn.apply(candidates.get(i));
		}
		// Remove any invalid environments which have arisen.
		return ArrayUtils.removeAll(constraints, null);
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
		switch (type.getOpcode()) {
		case TYPE_null:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
		case TYPE_void:
		case TYPE_universal:
		case TYPE_existential:
			return type;
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
		case TYPE_staticreference:
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
		case TYPE_tuple: {
			Type.Tuple t = (Type.Tuple) type;
			Type[] types = getUnderlyingTypes(t.getAll(), visited);
			return Type.Tuple.create(types);
		}
		case TYPE_union: {
			Type.Union t = (Type.Union) type;
			Type[] types = getUnderlyingTypes(t.getAll(), visited);
			return new Type.Union(types);
		}
		}
		throw new IllegalArgumentException("invalid type (" + type + "," + type.getClass().getName() + ")");
	}

	public static Type[] getUnderlyingTypes(Type[] types, Set<Name> visited) {
		Type[] uTypes = new Type[types.length];
		for (int i = 0; i != types.length; ++i) {
			uTypes[i] = getUnderlyingType(types[i], visited);
		}
		return uTypes;
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

	/**
	 * Check whether an error has arisen between two typings. Specifically, if the
	 * typing before is satisfiable but the typing after is not, then we have an
	 * error.
	 * 
	 * @param element
	 * @param before
	 * @param after
	 * @param lhs
	 * @param rhs
	 */
	public void checkForError(SyntacticItem element, Typing before, Typing after, int lhs, Type rhs) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, before.types(lhs), rhs);
		}
	}
	
	public void checkForError(SyntacticItem element, Typing before, Typing after, Type lhs, int rhs) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, lhs, before.types(rhs));
		}
	}
	
	public void checkForError(SyntacticItem element, int code, Typing before, Typing after, int lhs, int rhs) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, code, before.types(lhs), before.types(rhs));
		}
	}
	
	public void checkForError(SyntacticItem element, Typing before, Typing after, int lhs, int rhs) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, before.types(lhs), before.types(rhs));
		}
	}
	
	public void checkForError(SyntacticItem element, Typing before, Typing after, int lhs, int rhs,
			Function<Type, Type> projection) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, before.types(lhs), before.types(rhs).map(projection));
		}
	}
	
	public void checkForError(SyntacticItem element, Typing before, Typing after, int lhs,
			Function<Type, Type> projection, int rhs) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, before.types(lhs).map(projection), before.types(rhs));
		}
	}

	public void checkForError(SyntacticItem element, Typing before, Typing after, int lhs, int[] rhs,
			Function<Tuple<Type>, Type> projection) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, before.types(lhs), before.types(rhs).map(projection));
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
