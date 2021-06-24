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

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import wyal.util.NameResolver.ResolutionError;
import wycc.lang.*;
import wycc.util.AbstractCompilationUnit;
import wycc.util.AbstractCompilationUnit.Identifier;
import wycc.util.AbstractCompilationUnit.Tuple;
import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyil.check.FlowTypeUtils.*;
import static wyil.check.FlowTypeUtils.*;
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
 * <b>Flow Typing.</b> The flow typing algorithm distinguishes between the
 * <i>declared type</i> of a variable and its <i>known type</i>. That is, the
 * known type at any given point is permitted to be more precise than the
 * declared type (but not vice versa). For example:
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
 * <p>
 * <b>Type Inference.</b> The algorithm performs <i>bidirectional</i> type
 * inference when type checking expressions. This is useful for, amongst other
 * things, helping with type polymorphism and constrained types. The following
 * illustrates:
 * </p>
 *
 * <pre>
 * type nat is (int x) where x >= 0
 *
 * function one() -> nat:
 * 	  return 1
 * </pre>
 * <p>
 * The above function type checks without the need to cast <code>1</code> to
 * type <code>nat</code> because of forward type inference. Specifically, the
 * return type is propagated forwards and given to the constant value (of
 * course, the safety of this needs to be determined either by evaluating the
 * constraint with the given constant or using a theorem prover).
 * </p>
 * <p>
 * The ability to omit template arguments also stems from the type inference
 * algorithm. This employs a constraint system over type variables to finding
 * suitable bindings for template parameters. The following illustrates:
 * </p>
 *
 * <pre>
 * type Box<T> is null | { T value }
 *
 * function empty<T>() -> Box<T>:
 *  return null
 *
 * function get<T>(Box<T> box, T dEfault) -> T:
 *    if box is null:
 *       return dEfault
 *    else:
 *       return box.value
 * </pre>
 * <p>
 * Using the above definitions, the following type checks without requiring
 * explicit type annotations:
 * </p>
 *
 * <pre>
 * Box<int> b = box(1)
 * assert get(b,0) == 1
 * </pre>
 *
 * <p>
 * In this case, the template parameter <code>T</code> is determine from the
 * type of variable <code>b</code>. However, template parameters can also be
 * bound from return values as well, which the following illustrates:
 * </p>
 *
 * <pre>
 * Box<int> b = empty() assert get(b,0) == 1 </pre
 *
 * <p>
 * Here, the template parameter for <code>empty()</code> is determined from the
 * return value.
 * </p>
 *
 * <h3>References</h3>
 * <ul>
 * <li>
 * <p>
 * Sound and Complete Flow Typing with Unions, Intersections and Negations.
 * David J. Pearce. In Proceedings of the Conference on Verification, Model
 * Checking, and Abstract Interpretation (VMCAI), volume 7737 of Lecture Notes
 * in Computer Science, pages 335--354, 2013. © Springer-Verlag
 * </p>
 * </li>
 * <li>
 * <p>
 * A Calculus for Constraint-Based Flow Typing. David J. Pearce. In Proceedings
 * of the Workshop on Formal Techniques for Java-like Languages (FTFJP), Article
 * 7, 2013.
 * </p>
 * </li>
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
		checkExpression(decl.getInitialiser(), decl.getType(), true, environment);
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
				checkExpression((Expr.Invoke) stmt, null, false, environment);
				return environment;
			case EXPR_indirectinvoke:
				// NOTE: Void signal nothing is expected.
				checkExpression((Expr.IndirectInvoke) stmt, null, false, environment);
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
		checkExpression(stmt.getVariable().getInitialiser(), Type.IntArray, true, environment);
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
			Type rhs = checkExpression(decl.getInitialiser(), lhs, true, environment);
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
			Type actual = checkExpression(rvals.get(i), types[i], true, environment);
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
		checkExpression(stmt.getOperand(), Type.IntArray, true, environment);
		return environment;
	}

	/**
	 * Type check a do-while statement.
	 *
	 * @param stmt        Statement to type check
	 * @param environment Determines the type of all variables immediately going
	 *                    into this block
	 * @return
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
			checkExpression(stmt.getReturn(), type, true, environment);
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
		Type type = checkExpression(stmt.getCondition(), null, true, environment);
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
				checkExpression(e, type, true, environment);
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
			checkExpression(condition, Type.Bool, true, environment);
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
		Type lhsT = checkExpression(expr.getOperand(), null, true, environment);
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
			checkExpression(decl.getInitialiser(), Type.IntArray, true, environment);
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
			src = checkExpression(lval.getFirstOperand(), null, true, environment);
			// Extract array or fail
			arrT = extractType(Type.Array.class, src, EXPECTED_ARRAY, lval.getFirstOperand());
		}
		// Sanity check extraction
		if (arrT != null) {
			// Check for integer subscript
			checkExpression(lval.getSecondOperand(), Type.Int, true, environment);
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
			src = checkExpression(lval.getOperand(), null, true, environment);
			// Extract record or fail
			recT = extractType(Type.Record.class, src, EXPECTED_RECORD, lval.getOperand());
		}
		// Extract the field type
		return extractFieldType(recT, lval.getField());
	}

	public Type checkDereferenceLVal(Expr.Dereference lval, Environment environment) {
		Type src = checkExpression(lval.getOperand(), null, true, environment);
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
		Type src = checkExpression(lval.getOperand(), null, true, environment);
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
	public Type checkExpression(Expr expression, Type target, boolean required, Environment environment) {
		// Construct empty typing to start with
		Typing typing = new Typing(environment);
		//
		if (target == null) {
			typing = pullExpression(expression, required, typing, environment);
		} else {
			typing = pushExpression(expression, typing.push(target), environment);
		}
		// Concretise typing to eliminate type variables
		typing = typing.concretise();
		// Attempt to collapse typings
		typing = typing.fold(Typing.Row.COMPARATOR(environment));
		// Attempt to finalise typing
		boolean ok = typing.finalise();
		// Sanity check what is left
		if (ok) {
			return expression.getType();
		} else {
			status = false;
			return null;
		}
	}

	// =====================================================================================
	// Push Expressions (for Type Inference)
	// =====================================================================================

	/**
	 * <p>
	 * Push a given type forwards through an expression. Pushing occurs when there
	 * is a known target type which can be used as a basis for the push. For
	 * example, consider this:
	 * </p>
	 *
	 * <pre>
	 * nat[]|null var = [1,false]
	 * </pre>
	 *
	 * <p>
	 * This method is responsible for pushing the type <code>nat[]|null</code> into
	 * the expression <code>[1,false]</code>. In turn, this will filter the type
	 * down to <code>nat[]</code> and then push <code>nat</code> through each of the
	 * initialiser expressions. Ultimately, this will lead to a type error as
	 * <code>nat</code> cannot be pushed into <code>false</code>.
	 * </p>
	 * <p>
	 * A type variable is used instead of an actual type because, unfortunately, we
	 * must support multiple possible typings simultaneously. Thus, the type
	 * variable refers to the set of types being simultaneously pushed through the
	 * expression.
	 * </p>
	 *
	 * @param var         The type variable who's types are being pushed through the
	 *                    expression
	 * @param expression  The expression we are pushing through.
	 * @param typing      The current typing matrix. This represents all current
	 *                    typings under consideration.
	 * @param environment The current typing environment applicable for the
	 *                    expression given its context.
	 * @return
	 */
	private Typing pushExpression(int var, Expr expression, Typing typing, Environment environment) {
		meter.step("expression");
		//
		switch (expression.getOpcode()) {
		case EXPR_constant:
			return pushConstant(var, (Expr.Constant) expression, typing, environment);
		case EXPR_variablecopy:
			return pushVariable(var, (Expr.VariableAccess) expression, typing, environment);
		case EXPR_staticvariable:
			return pushStaticVariable(var, (Expr.StaticVariableAccess) expression, typing, environment);
		case EXPR_cast:
			return pushCast(var, (Expr.Cast) expression, typing, environment);
		case EXPR_invoke:
			return pushInvoke(var, (Expr.Invoke) expression, typing, environment);
		case EXPR_indirectinvoke:
			return pushIndirectInvoke(var, (Expr.IndirectInvoke) expression, typing, environment);
		case EXPR_logicalnot:
		case EXPR_logicalor:
		case EXPR_logicaland:
		case EXPR_logicaliff:
		case EXPR_logicalimplication:
		case EXPR_is:
		case EXPR_logicaluniversal:
		case EXPR_logicalexistential:
			return pushConditionExpression(var, expression, typing, environment);
		case EXPR_equal:
		case EXPR_notequal:
			return pushEqualityOperator(var, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
			return pushIntegerComparator(var, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_integernegation:
			return pushIntegerOperator(var, (Expr.UnaryOperator) expression, typing, environment);
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
			return pushIntegerOperator(var, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_bitwisenot:
			return pushBitwiseOperator(var, (Expr.UnaryOperator) expression, typing, environment);
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
			return pushBitwiseOperator(var, (Expr.NaryOperator) expression, typing, environment);
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
			return pushBitwiseShift(var, (Expr.BinaryOperator) expression, typing, environment);
		case EXPR_tupleinitialiser:
			return pushTupleInitialiser(var, (Expr.TupleInitialiser) expression, typing, environment);
		case EXPR_recordinitialiser:
			return pushRecordInitialiser(var, (Expr.RecordInitialiser) expression, typing, environment);
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			return pushRecordAccess(var, (Expr.RecordAccess) expression, typing, environment);
		case EXPR_arraylength:
			return pushArrayLength(var, (Expr.ArrayLength) expression, typing, environment);
		case EXPR_arrayinitialiser:
			return pushArrayInitialiser(var, (Expr.ArrayInitialiser) expression, typing, environment);
		case EXPR_arraygenerator:
			return pushArrayGenerator(var, (Expr.ArrayGenerator) expression, typing, environment);
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			return pushArrayAccess(var, (Expr.ArrayAccess) expression, typing, environment);
		case EXPR_arrayrange:
			return pushArrayRange(var, (Expr.ArrayRange) expression, typing, environment);
		case EXPR_dereference:
			return pushDereference(var, (Expr.Dereference) expression, typing, environment);
		case EXPR_fielddereference:
			return pushFieldDereference(var, (Expr.FieldDereference) expression, typing, environment);
		case EXPR_new:
			return pushNew(var, (Expr.New) expression, typing, environment);
		case EXPR_lambdaaccess:
			return pushLambdaAccess(var, (Expr.LambdaAccess) expression, typing, environment);
		case DECL_lambda:
			return pushLambdaDeclaration(var, (Decl.Lambda) expression, typing, environment);
		default:
			return internalFailure("unknown expression encountered (" + expression.getClass().getSimpleName() + ")",
					expression);
		}
	}

	private Typing  pushExpression(Expr expression, Typing typing, Environment environment) {
		return pushExpression(typing.top(), expression, typing, environment);
	}

	private Typing  pushExpressions(Tuple<Expr> expressions, Type type, Typing typing, Environment environment) {
		for (int i = 0; i != expressions.size(); ++i) {
			typing = pushExpression(expressions.get(i), typing.push(type), environment);
		}
		return typing;
	}

	private Typing  pushExpression(Expr expression, Function<Typing.Row, Type> fn, Typing typing,
			Environment environment) {
		typing = typing.map(row -> row.add(fn.apply(row)));
		return pushExpression(expression, typing, environment);
	}

	private Typing  pushExpressions(Tuple<Expr> expressions, BiFunction<Typing.Row, Integer, Type> fn, Typing typing,
			Environment environment) {
		for (int i = 0; i != expressions.size(); ++i) {
			final int _i = i;
			typing = typing.map(row -> row.add(fn.apply(row, _i)));
			typing = pushExpression(typing.top(), expressions.get(i), typing, environment);
		}
		return typing;
	}

	private Typing pushArrayLength(int var, Expr.ArrayLength expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getOperand(), typing.push(Type.AnyArray), environment);
		// Terminate flow
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Int, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.Int);
		//
		return nTyping;
	}

	private Typing pushArrayInitialiser(int var, Expr.ArrayInitialiser expr, Typing typing, Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Split out incoming array types
		Typing nTyping = typing.project(row -> forkOnArray(row, var, environment));
		// Sanity check for errors
		checkForError(expr, typing, nTyping, var, getNaturalType(expr, environment));
		// >>> Propagate forwards into children
		return pushExpressions(operands, (r, i) -> getArrayElement(r.get(var)), nTyping, environment);
	}

	private Typing pushArrayGenerator(int var, Expr.ArrayGenerator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Split out incoming array types
		Typing nTyping = typing.project(row -> forkOnArray(row, var, environment));
		// Sanity check for errors
		checkForError(expr, typing, nTyping, var, getNaturalType(expr, environment));
		// >>> Propagate forwards into children
		nTyping = pushExpression(expr.getFirstOperand(), r -> getArrayElement(r.get(var)), nTyping, environment);
		return pushExpression(expr.getSecondOperand(), nTyping.push(Type.Int), environment);
	}

	private Typing pushArrayAccess(int var, Expr.ArrayAccess expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// <<< Propagate backwards from source
		typing = pullExpression(expr.getFirstOperand(), true, typing, environment);
		int src = typing.top();
		// Check source is array of appropriate type
		Typing nTyping = typing.filter(row -> (row.get(src).as(Type.Array.class) != null));
		Typing nnTyping = nTyping.map(row -> filterOnSubtype(row, var, getArrayElement(row.get(src)), environment));
		// Report errors
		checkForError(expr.getFirstOperand(), typing, nTyping, var, t -> new Type.Array(t), src);
		checkForError(expr, nTyping, nnTyping, var, src, t -> getArrayElement(t));
		// >>> Propagate forwards into index operand
		return pushExpression(expr.getSecondOperand(), nnTyping.push(Type.Int), environment);
	}

	private Typing pushArrayRange(int var, Expr.ArrayRange expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getFirstOperand(), typing.push(Type.Int), environment);
		return pushExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
	}

	private Typing pushBitwiseOperator(int var, Expr.UnaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getOperand(), typing.push(Type.Byte), environment);
		// Filter target type
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Byte, environment));
		// Report errors
		checkForError(expr, typing, nTyping, var, Type.Byte);
		//
		return nTyping;
	}

	private Typing pushBitwiseOperator(int var, Expr.NaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = pushExpressions(expr.getOperands(), Type.Byte, typing, environment);
		// Filter target type
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Byte, environment));
		// Report errors
		checkForError(expr, typing, nTyping, var, Type.Byte);
		// >>> Propagate forwards into children
		return nTyping;
	}

	private Typing pushBitwiseShift(int var, Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getFirstOperand(), typing.push(Type.Byte), environment);
		typing = pushExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
		// Filter target type
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Byte, environment));
		// Report errors
		checkForError(expr, typing, nTyping, var, Type.Byte);
		//
		return nTyping;
	}

	private Typing pushCast(int var, Expr.Cast expr, Typing typing, Environment environment) {
		// Filter target type
		Typing nTyping = typing.map(row -> filterOnSubtype(row, var, expr.getType(), environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, expr.getType());
		// <<< Propagate backwards from children
		nTyping = pullExpression(expr.getOperand(), true, nTyping, environment);
		int src = nTyping.top();
		// Check cast makes sense
		Typing nnTyping = nTyping.filter(r -> isSensibleCast(expr.getType(), r.get(src), environment));
		// Report errors
		checkForError(expr.getOperand(), nTyping, nnTyping, expr.getType(), src);
		// Done
		return nnTyping;
	}

	private Typing pushConditionExpression(int var, Expr expr, Typing typing, Environment environment) {
		// Filter target type
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Bool, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.Bool);
		// Check condition
		checkCondition(expr, true, environment);
		// Done
		return nTyping;
	}

	/**
	 * Check the type of a given constant expression. This is straightforward since
	 * the determine is fully determined by the kind of constant we have.
	 *
	 * @param expr
	 * @return
	 */
	private Typing pushConstant(int var, Expr.Constant expr, Typing typing, Environment environment) {
		// Extract underling value type
		final Type type = typeOf(expr.getValue());
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Filter target type
		Typing nTyping;
		//
		if (type instanceof Type.Primitive) {
			nTyping = typing.map(row -> filterOnPrimitive(row, var, (Type.Primitive) type, environment));
		} else {
			nTyping = typing.map(row -> filterOnIntArray(row, var, environment));
		}
		// Sanity check for errors
		checkForError(expr, typing, nTyping, var, type);
		// Done
		return nTyping;
	}

	private Typing pushDereference(int var, Expr.Dereference expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// <<< Propagate backwards from source operand
		typing = pullExpression(expr.getOperand(), true, typing, environment);
		int src = typing.top();
		// Check source is reference of appropriate type
		Typing nTyping = typing.filter(row -> (row.get(src).as(Type.Reference.class) != null));
		Typing nnTyping = nTyping.map(row -> filterOnSubtype(row, var, getReferenceElement(row.get(src)), environment));
		// Report errors
		checkForError(expr.getOperand(), typing, nTyping, var, t -> new Type.Reference(t), src);
		checkForError(expr, nTyping, nnTyping, var, src, t -> getReferenceElement(t));
		//
		return nTyping;
	}

	private Typing pushEqualityOperator(int var, Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// Filter target type
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Bool, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.Bool);
		// >>> Propagate forwards into children
		typing = pullExpression(expr.getFirstOperand(), true, typing, environment);
		int lhs = typing.top();
		typing = pullExpression(expr.getSecondOperand(), true, typing, environment);
		int rhs = typing.top();
		// Check operand (in)compatibility
		Typing typing_1 = typing.map(row -> disjoint(row.get(lhs), row.get(rhs), null) ? null : row);
		// Report errors
		checkForError(expr, INCOMPARABLE_OPERANDS, typing, typing_1, lhs, rhs);
		// Done
		return typing_1;
	}

	private Typing pushFieldDereference(int var, Expr.FieldDereference expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// <<< Propagate backwards from source operand
		typing = pullExpression(expr.getOperand(), true, typing, environment);
		int src = typing.top();
		// Check source is reference of appropriate type
		Typing nTyping = typing.filter(row -> isMatchingRecordReference(row.get(src), expr.getField()));
		Typing nnTyping = nTyping
				.map(row -> filterOnSubtype(row, var, getReferenceField(row.get(src), expr.getField()), environment));
		// Report errors
		checkForError(expr, typing, nTyping, var, t -> new Type.Reference(new Type.Record(true, expr.getField(), t)),
				src);
		checkForError(expr, nTyping, nnTyping, var, src, t -> getReferenceField(t, expr.getField()));
		//
		return nTyping;
	}

	private Typing pushIntegerComparator(int var, Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getFirstOperand(), typing.push(Type.Int), environment);
		typing = pushExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
		// Filter target type
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Bool, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.Bool);
		//
		return nTyping;
	}

	private Typing pushIntegerOperator(int var, Expr.UnaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getOperand(), typing.push(Type.Int), environment);
		// Filter target type
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Int, environment));
		// Sanity check typing
		checkForError(expr, typing, nTyping, var, Type.Int);
		//
		return nTyping;
	}

	private Typing pushIntegerOperator(int var, Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getFirstOperand(), typing.push(Type.Int), environment);
		typing = pushExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
		// Filter target type
		Typing nTyping = typing.map(row -> filterOnPrimitive(row, var, Type.Int, environment));
		// Sanity check whether valid typing still possible
		checkForError(expr, typing, nTyping, var, Type.Int);
		//
		return nTyping;
	}

	private Typing pushIndirectInvoke(int var, Expr.IndirectInvoke expr, Typing typing, Environment environment) {
		Tuple<Expr> operands = expr.getArguments();
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// <<< Propagate backwards from source
		typing = pullExpression(expr.getSource(), true, typing, environment);
		int src = typing.top();
		// Check source is suitable callable
		Typing nTyping = typing.filter(row -> isMatchingLambda(row.get(src), operands.size()));
		// Filter target type
		Typing nnTyping = nTyping.map(row -> filterOnSubtype(row, var, getLambdaReturn(row.get(src)), environment));
		// Report errors
		checkForError(expr.getSource(), typing, nTyping, constructExpectedMethod(var, operands, typing, environment),
				src);
		checkForError(expr, nTyping, nnTyping, var, src, t -> getLambdaReturn(t));
		// >>> Propagate forwards into children
		return pushExpressions(operands, (row, i) -> getLambdaParameter(row.get(src), i), nnTyping, environment);
	}

	private Typing pushInvoke(int var, Expr.Invoke expr, Typing typing, Environment environment) {
		// Extract link
		Decl.Link<Decl.Callable> link = expr.getLink();
		// Save argument expressions for later
		Tuple<Expr> arguments = expr.getOperands();
		final int n = arguments.size();
		// Select candidates with matching numbers of arguments
		List<Decl.Callable> candidates = select(link.getCandidates(), c -> c.getParameters().size() == n);
		// Check this invocation is resolvable
		if (!link.isPartiallyResolved() || candidates.size() == 0) {
			syntaxError(link.getName(), AMBIGUOUS_CALLABLE, link.getCandidates());
			return typing.invalidate();
		}
		// Allocate space for the signature variables. This is used to identify each row
		// in the typing matrix with one of candidates.
		typing = typing.push(Type.Any).push(Type.Any).push(Type.Any);
		int v_concrete = typing.top();
		int v_template = v_concrete - 1;
		int v_signature = v_concrete - 2;
		// Register a finaliser to resolve this invocation
		typing.register(typeInvokeExpression(expr, v_signature, v_template));
		// Fork typing into different cases for each overloaded candidate
		typing = typing
				.project(row -> fork(candidates, c -> initialiseCallableFrame(row, expr, c, v_signature, v_concrete, v_template)));
		// <<< Filter return type
		Typing nTyping = typing.map(
				row -> filterOnSubtype(row, var, row.get(v_concrete).as(Type.Callable.class).getReturn(), environment));
		// Sanity check whether valid typing still possible
		checkForError(expr, typing, nTyping, var, v_concrete, t -> getLambdaReturn(t));
		// >>> Propagate forwards into children
		return pushExpressions(arguments, (r, i) -> getLambdaParameter(r.get(v_concrete), i), nTyping, environment);
	}

	private Typing pushLambdaAccess(int var, Expr.LambdaAccess expr, Typing typing, Environment environment) {
		Decl.Link<Decl.Callable> link = expr.getLink();
		List<Decl.Callable> candidates = filterLambdaCandidates(expr);
		// Sanity check for errors
		if (candidates.size() == 0) {
			syntaxError(link.getName(), AMBIGUOUS_CALLABLE, link.getCandidates());
			return typing.invalidate();
		}
		// Allocate variable for concrete signature
		typing = typing.push(Type.Any);
		int sig = typing.top();
		// Allocate a finaliser for this expression
		typing.register(typeLambdaAccess(expr, sig));
		// Split out incoming array types
		Typing nTyping = typing.project(row -> forkOnLambda(row, var, environment));
		// Fork candidates
		nTyping = nTyping.project(row -> fork(candidates, c -> row.set(sig, c.getType())));
		// Filter on signature type
		nTyping = nTyping.map(row -> filterOnSubtype(row, var, row.get(sig), environment));
		// Sanity check for errors
		checkForError(expr, typing, nTyping, var, getNaturalType(expr, environment));
		// Done
		return nTyping;
	}

	private Typing pushLambdaDeclaration(int var, Decl.Lambda expr, Typing typing, Environment environment) {
		// Extract parameters from declaration
		Type params = Decl.Callable.project(expr.getParameters());
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Split out incoming lambda types
		Typing nTyping = typing.project(row -> forkOnLambda(row, var, params, environment));
		// Type check the body of the lambda using the expected return types
		Type returns = checkExpression(expr.getBody(), null, false, environment);
		//
		if (returns == null) {
			// Some kind of error has occurred upstream
			return nTyping.invalidate();
		} else {
			// Determine whether or not this is a pure or impure lambda.
			Type.Callable type = constructLambdaType(expr, params, returns);
			// Filter out problematic cases
			Typing nnTyping = nTyping.map(row -> filterOnSubtype(row, var, type, environment));
			// Report errors
			checkForError(expr, typing, nTyping, var, getNaturalType(expr, environment));
			checkForError(expr, nTyping, nnTyping, var, type);
			//
			return nnTyping;
		}
	}

	private Typing pushNew(int var, Expr.New expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Split out incoming array types
		Typing nTyping = typing.project(row -> forkOnReference(row, var, environment));
		// Report errors
		checkForError(expr, typing, nTyping, var, getNaturalType(expr, environment));
		// >>> Propagate forwards into children
		return pushExpression(expr.getOperand(), r -> getReferenceElement(r.get(var)), nTyping, environment);
	}

	private Typing pushRecordAccess(int var, Expr.RecordAccess expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// <<< Propagate backwards from source
		typing = pullExpression(expr.getOperand(), true, typing, environment);
		int src = typing.top();
		// Check source is array of appropriate type
		//Typing nTyping = typing.filter(row -> (row.get(src).as(Type.Record.class) != null));
		Typing nTyping = typing.filter(row -> (getRecordField(row.get(src), expr.getField()) != null));
		Typing nnTyping = nTyping
				.map(row -> filterOnSubtype(row, var, getRecordField(row.get(src), expr.getField()), environment));
		// Report errors
		checkForError(expr.getOperand(), typing, nTyping, var, t -> new Type.Record(true, expr.getField(), t), src);
		checkForError(expr, nTyping, nnTyping, var, src, t -> getRecordField(t, expr.getField()));
		// >>> Propagate forwards into index operand
		return nnTyping;
	}

	private Typing pushRecordInitialiser(int var, Expr.RecordInitialiser expr, Typing typing, Environment environment) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Split out incoming record types
		Typing nTyping = typing.project(row -> forkOnRecord(row, var, fields, environment));
		// Sanity check for errors
		checkForError(expr, typing, nTyping, var, getNaturalType(expr, environment));
		// >>> Propagate forwards into children
		return pushExpressions(operands, (r, i) -> getRecordFieldWithDefault(r.get(var), fields.get(i), Type.Any),
				nTyping, environment);
	}

	private Typing pushStaticVariable(int var, Expr.StaticVariableAccess expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Extract variable link
		Decl.Link<Decl.StaticVariable> l = expr.getLink();
		// Extract type if applicable
		Type type = l.isResolved() ? l.getTarget().getType() : null;
		// Terminate this typing
		Typing nTyping = typing.map(row -> filterOnSubtype(row, var, type, environment));
		// Sanity check for errors
		checkForError(expr, typing, nTyping, var, type);
		// Done
		return nTyping;
	}

	private Typing pushTupleInitialiser(int var, Expr.TupleInitialiser expr, Typing typing, Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		final int n = operands.size();
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Split out incoming record types
		Typing nTyping = typing.project(row -> forkOnTuple(row, var, n, environment));
		// Sanity check for errors
		checkForError(expr, typing, nTyping, var, getNaturalType(expr, environment));
		// >>> Propagate forwards into children
		return pushExpressions(operands, (r, i) -> getTupleElement(r.get(var), i), nTyping, environment);
	}

	private Typing pushVariable(int var, Expr.VariableAccess expr, Typing typing, Environment environment) {
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, var));
		// Extract variable's active type
		Type type = environment.getType(expr.getVariableDeclaration());
		// Terminate this typing
		Typing nTyping = typing.map(row -> filterOnSubtype(row, var, type, environment));
		// Sanity check for errors
		checkForError(expr, typing, nTyping, var, type);
		// Done
		return nTyping;
	}

	// =====================================================================================
	// Pull Expressions (for Type Inference)
	// =====================================================================================

	/**
	 * <p>
	 * Pull a type backwards out of an expression. Pull occurs when there is no
	 * target type which could be used as a basis for pushing. For example, consider
	 * this:
	 * </p>
	 *
	 * <pre>
	 * assert [1,2,3] == xs
	 * </pre>
	 *
	 * <p>
	 * When typing the expression <code>[1,2,3]</code> there is no target type to
	 * use. Therefore, we pull the "natural" type out of it (which would be
	 * <code>int[]</code> in this case).
	 * </p>
	 * <p>
	 * The type of the expression must be loaded onto the to of the typing returned.
	 * This means we can easily determine the variable allocated for this expression
	 * via Typing.top().
	 * </p>
	 *
	 * @param expression  The expression we are pulling from.
	 * @param required    Flag to indicate whether or not a return value is
	 *                    required.
	 * @param typing      The current typing matrix. This represents all current
	 *                    typings under consideration.
	 * @param environment The current typing environment applicable for the
	 *                    expression given its context.
	 * @return
	 */
	private Typing  pullExpression(Expr expression, boolean required, Typing typing, Environment environment) {
		meter.step("expression");
		//
		switch (expression.getOpcode()) {
		case EXPR_constant:
			return pullConstant((Expr.Constant) expression, typing, environment);
		case EXPR_variablecopy:
			return pullVariable((Expr.VariableAccess) expression, typing, environment);
		case EXPR_staticvariable:
			return pullStaticVariable((Expr.StaticVariableAccess) expression, typing, environment);
		case EXPR_cast:
			return pullCast((Expr.Cast) expression, typing, environment);
		case EXPR_invoke:
			return pullInvoke((Expr.Invoke) expression, required, typing, environment);
		case EXPR_indirectinvoke:
			return pullIndirectInvoke((Expr.IndirectInvoke) expression, required, typing, environment);
		case EXPR_logicalnot:
		case EXPR_logicalor:
		case EXPR_logicaland:
		case EXPR_logicaliff:
		case EXPR_logicalimplication:
		case EXPR_is:
		case EXPR_logicaluniversal:
		case EXPR_logicalexistential:
			return pullConditionExpression(expression, typing, environment);
		case EXPR_equal:
		case EXPR_notequal:
			return pullEqualityOperator((Expr.BinaryOperator) expression, typing, environment);
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
			return pullIntegerComparator((Expr.BinaryOperator) expression, typing, environment);
		case EXPR_integernegation:
			return pullIntegerOperator((Expr.UnaryOperator) expression, typing, environment);
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
			return pullIntegerOperator((Expr.BinaryOperator) expression, typing, environment);
		case EXPR_bitwisenot:
			return pullBitwiseOperator((Expr.UnaryOperator) expression, typing, environment);
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
			return pullBitwiseOperator((Expr.NaryOperator) expression, typing, environment);
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
			return pullBitwiseShift((Expr.BinaryOperator) expression, typing, environment);
		case EXPR_tupleinitialiser:
			return pullTupleInitialiser((Expr.TupleInitialiser) expression, typing, environment);
		case EXPR_recordinitialiser:
			return pullRecordInitialiser((Expr.RecordInitialiser) expression, typing, environment);
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			return pullRecordAccess((Expr.RecordAccess) expression, typing, environment);
		case EXPR_arraylength:
			return pullArrayLength((Expr.ArrayLength) expression, typing, environment);
		case EXPR_arrayinitialiser:
			return pullArrayInitialiser((Expr.ArrayInitialiser) expression, typing, environment);
		case EXPR_arraygenerator:
			return pullArrayGenerator((Expr.ArrayGenerator) expression, typing, environment);
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			return pullArrayAccess((Expr.ArrayAccess) expression, typing, environment);
		case EXPR_dereference:
			return pullDereference((Expr.Dereference) expression, typing, environment);
		case EXPR_fielddereference:
			return pullFieldDereference((Expr.FieldDereference) expression, typing, environment);
		case EXPR_new:
			return pullNew((Expr.New) expression, typing, environment);
		case EXPR_lambdaaccess:
			return pullLambdaAccess((Expr.LambdaAccess) expression, typing, environment);
		case DECL_lambda:
			return pullLambdaDeclaration((Decl.Lambda) expression, typing, environment);
		default:
			return internalFailure("unknown expression encountered (" + expression.getClass().getSimpleName() + ")",
					expression);
		}
	}

	private Typing pullArrayLength(Expr.ArrayLength expr, Typing typing, Environment environment) {
		typing = pushExpression(expr.getOperand(), typing.push(Type.AnyArray), environment);
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		//
		return typing.map(row -> row.add(Type.Int));
	}

	private Typing pullArrayInitialiser(Expr.ArrayInitialiser expr, Typing typing, Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		// Identify allocated variable(s)
		int[] children = new int[operands.size()];
		// >>> Propagate forwards into children
		for (int i = 0; i != children.length; ++i) {
			typing = pullExpression(operands.get(i), true, typing, environment);
			children[i] = typing.top();
		}
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Propagate backwards from children
		return typing.map(row -> row.add(new Type.Array(Type.Union.create(row.getAll(children)))));
	}

	private Typing pullArrayGenerator(Expr.ArrayGenerator expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into children
		typing = pullExpression(expr.getFirstOperand(), true, typing, environment);
		int element = typing.top();
		typing = pushExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Propagate backwards from children
		return typing.map(row -> row.add(new Type.Array(row.get(element))));
	}

	private Typing pullArrayAccess(Expr.ArrayAccess expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into source operand
		typing = pullExpression(expr.getFirstOperand(), true, typing, environment);
		int src = typing.top();
		typing = pushExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Propagate backwards from source operand
		Typing nTyping = typing.map(row -> row.add(getArrayElement(row.get(src))));
		// Sanity check typing
		checkForError(expr.getFirstOperand(), typing, nTyping, Type.AnyArray, src);
		//
		return nTyping;
	}

	private Typing pullBitwiseOperator(Expr.UnaryOperator expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getOperand(), typing.push(Type.Byte), environment);
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Propagate backwards into parent
		return typing.map(row -> row.add(Type.Byte));
	}

	private Typing pullBitwiseOperator(Expr.NaryOperator expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into children
		typing = pushExpressions(expr.getOperands(), Type.Byte, typing, environment);
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Propagate backwards into parent
		return typing.map(row -> row.add(Type.Byte));
	}

	private Typing pullBitwiseShift(Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getFirstOperand(), typing.push(Type.Byte), environment);
		typing = pushExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Propagate backwards into parent
		return typing.map(row -> row.add(Type.Byte));
	}

	private Typing pullCast(Expr.Cast expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into children
		typing = pullExpression(expr.getOperand(), true, typing, environment);
		int src = typing.top();
		// Check cast makes sense
		Typing nTyping = typing.filter(r -> isSensibleCast(expr.getType(), r.get(src), environment));
		// Report errors
		checkForError(expr.getOperand(), typing, nTyping, expr.getType(), src);
		// <<< Propagate backwards into parent
		return nTyping.map(row -> row.add(expr.getType()));
	}

	private Typing pullConditionExpression(Expr expr, Typing typing, Environment environment) {
		// Check condition
		checkCondition(expr, true, environment);
		// <<< Back propagate boolean
		return typing.map(row -> row.add(Type.Bool));
	}

	private Typing pullConstant(Expr.Constant expr, Typing typing, Environment environment) {
		// Extract underling value type
		final Type type = typeOf(expr.getValue());
		// Assign type to expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Back propagate from type
		return typing.map(row -> row.add(type));
	}

	private Typing pullDereference(Expr.Dereference expr, Typing typing, Environment environment) {
		// >>> Propagate forward into children
		typing = pullExpression(expr.getOperand(), true, typing, environment);
		int src = typing.top();
		// >>> Propagate backwards from children
		Typing nTyping = typing.map(row -> row.add(getReferenceElement(row.get(src))));
		// Sanity check source was a reference
		checkForError(expr.getOperand(), typing, nTyping, new Type.Reference(Type.Any), src);
		// Allocate a finaliser for this expression
		nTyping.register(typeStandardExpression(expr, nTyping.top()));
		// Done
		return nTyping;
	}

	private Typing pullEqualityOperator(Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into children
		typing = pullExpression(expr.getFirstOperand(), true, typing, environment);
		int lhs = typing.top();
		typing = pullExpression(expr.getSecondOperand(), true, typing, environment);
		int rhs = typing.top();
		// <<< Check operand (in)compatibility
		Typing typing_1 = typing.map(row -> disjoint(row.get(lhs), row.get(rhs), null) ? null : row);
		// Sanity check whether valid typing still possible
		checkForError(expr, INCOMPARABLE_OPERANDS, typing, typing_1, lhs, rhs);
		// <<< Propagate backwards from children
		return typing_1.map(row -> row.add(Type.Bool));
	}

	private Typing pullFieldDereference(Expr.FieldDereference expr, Typing typing, Environment environment) {
		// >>> Propagate forward into children
		typing = pullExpression(expr.getOperand(), true, typing, environment);
		int src = typing.top();
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, src + 1));
		// >>> Propagate backwards from children
		Typing nTyping = typing.map(row -> row.add(getReferenceField(row.get(src), expr.getField())));
		// Sanity check typing
		checkForError(expr.getOperand(), typing, nTyping,
				new Type.Reference(new Type.Record(true, expr.getField(), Type.Any)), src);
		// Done
		return nTyping;
	}

	private Typing pullIntegerComparator(Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getFirstOperand(), typing.push(Type.Int), environment);
		typing = pushExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
		// <<< Propagate backwards to parent
		return typing.map(row -> row.add(Type.Bool));
	}

	private Typing pullIntegerOperator(Expr.UnaryOperator expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getOperand(), typing.push(Type.Int), environment);
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Propagate backwards
		return typing.map(row -> row.add(Type.Int));
	}

	private Typing pullIntegerOperator(Expr.BinaryOperator expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into children
		typing = pushExpression(expr.getFirstOperand(), typing.push(Type.Int), environment);
		typing = pushExpression(expr.getSecondOperand(), typing.push(Type.Int), environment);
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Propagate backwards
		return typing.map(row -> row.add(Type.Int));
	}

	private Typing pullIndirectInvoke(Expr.IndirectInvoke expr, boolean required, Typing typing,
			Environment environment) {
		Tuple<Expr> operands = expr.getArguments();
		// <<< Propagate backwards from source
		typing = pullExpression(expr.getSource(), true, typing, environment);
		int src = typing.top();
		// Check source is suitable callable
		Typing nTyping = typing.filter(row -> isMatchingLambda(row.get(src), operands.size()));
		// Report errors
		checkForError(expr, typing, nTyping, constructExpectedMethod(Type.Any, operands, typing, environment), src);
		// >>> Propagate forwards into children
		nTyping = pushExpressions(operands, (row, i) -> getLambdaParameter(row.get(src), i), nTyping, environment);
		// Allocate a finaliser for this expression
		nTyping.register(typeStandardExpression(expr, nTyping.top() + 1));
		// <<< Propagate backwards
		return nTyping.map(row -> row.add(getLambdaReturn(row.get(src))));
	}

	private Typing pullInvoke(Expr.Invoke expr, boolean required, Typing typing, Environment environment) {
		// Extract link
		Decl.Link<Decl.Callable> link = expr.getLink();
		// Save argument expressions for later
		Tuple<Expr> arguments = expr.getOperands();
		final int n = arguments.size();
		// Select candidates with matching numbers of arguments
		List<Decl.Callable> candidates = select(link.getCandidates(), c -> c.getParameters().size() == n);
		// Check this invocation is resolvable
		if (!link.isPartiallyResolved() || candidates.size() == 0) {
			syntaxError(link.getName(), AMBIGUOUS_CALLABLE, link.getCandidates());
			return typing.invalidate();
		}
		// Allocate space for the signature variables. This is used to identify each row
		// in the typing matrix with one of candidates.
		typing = typing.push(Type.Any).push(Type.Any).push(Type.Any);
		int v_concrete = typing.top();
		int v_template = v_concrete - 1;
		int v_signature = v_concrete - 2;
		// Register a finaliser to resolve this invocation
		typing.register(typeInvokeExpression(expr, v_signature, v_template));
		// Fork typing into different cases for each overloaded candidate
		typing = typing
				.project(row -> fork(candidates, c -> initialiseCallableFrame(row, expr, c, v_signature, v_concrete, v_template)));
		// >>> Propagate forwards into children
		typing = pushExpressions(arguments, (r, i) -> getLambdaParameter(r.get(v_concrete), i), typing, environment);
		// Pull back return
		return typing.map(row -> row.add(getLambdaReturn(row.get(v_concrete))));
	}

	private Typing.Row initialiseCallableFrame(Typing.Row row, Expr.Invoke expr, Decl.Callable candidate, int v_signature,
			int v_concrete, int v_template) {
		Type.Callable signatureType = candidate.getType();
		Tuple<Template.Variable> template = candidate.getTemplate();
		Tuple<Type> templateArguments = expr.getBinding().getArguments();
		// Check whether need to infer template arguments
		if (template.size() > 0 && templateArguments.size() == 0) {
			// Template required, but no explicit arguments given. Therefore, we create
			// fresh (existential) type for each position and subsitute them through.
			wycc.util.Pair<Typing.Row, Type.Existential[]> p = row.fresh(template.size());
			row = p.first();
			templateArguments = new Tuple<>(p.second());
		}
		// Construct the concrete type.
		Type.Callable concreteType = WyilFile.substituteTypeCallable(signatureType, template, templateArguments);
		// Configure first meta-variable to hold actual signature
		row = row.set(v_signature, signatureType);
		// Configure second meta-variable to hold concrete signature
		row = row.set(v_concrete, concreteType);
		// Configure third meta-variable to hold concrete template
		if(templateArguments.size() > 0) {
			// NOTE: cannot set the template type if there are no template arguments as this
			// defaults to void which then renders the whole typing invalid!
			row = row.set(v_template, Type.Tuple.create(templateArguments.toArray(Type.class)));
		}
		//
		return row;
	}

	private Typing pullLambdaAccess(Expr.LambdaAccess expr, Typing typing, Environment environment) {
		Decl.Link<Decl.Callable> link = expr.getLink();
		List<Decl.Callable> candidates = filterLambdaCandidates(expr);
		// Sanity check for errors
		if (candidates.size() == 0) {
			syntaxError(link.getName(), AMBIGUOUS_CALLABLE, link.getCandidates());
			return typing.invalidate();
		}
		// Allocate a finaliser for this expression
		typing.register(typeLambdaAccess(expr, typing.top() + 1));
		// Fork typing rows based on candidates
		return typing.project(row -> fork(candidates, c -> row.add(c.getType())));
	}

	private Typing pullLambdaDeclaration(Decl.Lambda expr, Typing typing, Environment environment) {
		// Extract parameters from declaration
		Type params = Decl.Callable.project(expr.getParameters());
		// Type check the body of the lambda using the expected return types
		Type returns = checkExpression(expr.getBody(), null, false, environment);
		// Determine whether or not this is a pure or impure lambda.
		Type.Callable type = constructLambdaType(expr, params, returns);
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// Allocate type variable(s) for this expression
		return typing.map(row -> row.add(type));
	}

	public Type.Callable constructLambdaType(Decl.Lambda expr, Type params, Type returns) {
		if (returns == null) {
			// Error has occurred upstream, so simply propagate this down.
			return null;
		} else if (isPure(expr.getBody())) {
			return new Type.Function(params, returns);
		} else {
			return new Type.Method(params, returns);
		}
	}

	private Typing pullNew(Expr.New expr, Typing typing, Environment environment) {
		// >>> Propagate forwards into children
		typing = pullExpression(expr.getOperand(), true, typing, environment);
		// <<< Propagate backwards
		final int element = typing.top();
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, element + 1));
		//
		return typing.map(row -> row.add(new Type.Reference(row.get(element))));
	}

	private Typing pullRecordAccess(Expr.RecordAccess expr, Typing typing, Environment environment) {
		// Recursively check source operand
		typing = typing.push(Type.Any);
		// >>> Propagate forwards into children
		typing = pullExpression(expr.getOperand(), true, typing, environment);
		int src = typing.top();
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, src + 1));
		// <<< Propagate backwards from children
		Typing nTyping = typing.filter(row -> (row.get(src).as(Type.Record.class) != null));
		Typing nnTyping = nTyping.map(row -> row.add(getRecordField(row.get(src), expr.getField())));
		// Report errors
		checkForError(expr.getOperand(), EXPECTED_RECORD, typing, nTyping);
		checkForError(expr.getField(), INVALID_FIELD, nTyping, nnTyping);
		// Done
		return nnTyping;
	}

	private Typing pullRecordInitialiser(Expr.RecordInitialiser expr, Typing typing, Environment environment) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		// >>> Propagate forwards into children
		int[] children = new int[operands.size()];
		for (int i = 0; i != children.length; ++i) {
			typing = pullExpression(operands.get(i), true, typing, environment);
			children[i] = typing.top();
		}
		// Allocate finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Propagate backwards from children
		return typing.map(row -> row.add(new Type.Record(false, fields, new Tuple<>(row.getAll(children)))));
	}

	private Typing pullStaticVariable(Expr.StaticVariableAccess expr, Typing typing, Environment environment) {
		// Extract variable link
		Decl.Link<Decl.StaticVariable> l = expr.getLink();
		// Extract type if applicable
		Type type = l.isResolved() ? l.getTarget().getType() : null;
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// Terminate this typing
		return typing.map(row -> row.add(type));
	}

	private Typing pullTupleInitialiser(Expr.TupleInitialiser expr, Typing typing, Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		// >>> Propagate forwards into children
		int[] children = new int[operands.size()];
		for (int i = 0; i != children.length; ++i) {
			typing = pullExpression(operands.get(i), true, typing, environment);
			children[i] = typing.top();
		}
		// Allocate finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// <<< Propagate backwards from children
		return typing.map(row -> row.add(Type.Tuple.create(row.getAll(children))));
	}

	private Typing pullVariable(Expr.VariableAccess expr, Typing typing, Environment environment) {
		// Extract variable's active type
		Type type = environment.getType(expr.getVariableDeclaration());
		// Allocate a finaliser for this expression
		typing.register(typeStandardExpression(expr, typing.top() + 1));
		// Terminate this typing
		return typing.map(row -> row.add(type));
	}

	// ==========================================================================
	// Helpers
	// ==========================================================================

	/**
	 * Filter the set of candidates for a lambda access expression based on the
	 * presence (or not) of type parameters.
	 *
	 * @param expr
	 * @return
	 */
	private static List<Decl.Callable> filterLambdaCandidates(Expr.LambdaAccess expr) {
		Decl.Link<Decl.Callable> link = expr.getLink();
		Type types = expr.getParameterTypes();
		// There is a problem here in that we cannot distinguish
		// between the case where no parameters were supplied and when
		// exactly zero arguments were supplied.
		if (types.shape() > 0) {
			return select(link.getCandidates(), d -> d.getType().getParameter().equals(types));
		} else if (link.isResolved()) {
			// Link already resolved (e.g. because was only one candidate).
			return link.getCandidates();
		} else if (link.isPartiallyResolved()) {
			// Harder case. No signature given by user, so must fork constraints at this
			// point for each sensible candidate.
			return link.getCandidates();
		} else {
			return Collections.emptyList();
		}
	}

	private Tuple<Type> constructExpectedMethod(int var, Tuple<Expr> operands, Typing typing, Environment environment) {
		// Determine natural type of arguments
		Tuple<Type> params = operands.map(e -> getNaturalType(e, environment));
		// Construct expected parameter
		Type param = Type.Tuple.create(params);
		// Extract expected returns
		Tuple<Type> returns = typing.types(var);
		// Done
		return returns.map(ret -> new Type.Method(param, ret));
	}

	private Type constructExpectedMethod(Type ret, Tuple<Expr> operands, Typing typing, Environment environment) {
		// Determine natural type of arguments
		Tuple<Type> params = operands.map(e -> getNaturalType(e, environment));
		// Construct expected parameter
		Type param = Type.Tuple.create(params);
		// Done
		return new Type.Method(param, ret);
	}

	/**
	 * Check all items contained in one tuple (left) are found in another tuple
	 * (right). However, the right-hand side may contain additional items. Note also
	 * the ordering of items does not matter.
	 *
	 * @param <T>
	 * @param lhs The left-hand side which must be a subset of the right-hand side
	 * @param rhs The right-hand side which must be a superset of the left-hand side
	 * @return
	 */
	private static <T extends SyntacticItem> boolean isSubset(Tuple<T> lhs, Tuple<T> rhs) {
		for (int i = 0; i != lhs.size(); ++i) {
			final T ith = lhs.get(i);
			boolean matched = false;
			for (int j = 0; j != rhs.size(); ++j) {
				final T jth = rhs.get(j);
				if (ith.equals(jth)) {
					matched = true;
					break;
				}
			}
			if (!matched) {
				return false;
			}
		}
		return true;
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

	/**
	 * Determine whether a cast from one type to another is "sensible". That is,
	 * could in principle be implemented.
	 *
	 * @param target    The type being cast to.
	 * @param actual    The type being cast from.
	 * @param subtyping
	 * @return
	 */
	private static boolean isSensibleCast(Type target, Type actual, Subtyping.Environment subtyping) {
		Type underlying = getUnderlyingType(actual, null);
		// NOTE: this doesn't handle type variables properly.
		Type glb = subtyping.greatestLowerBound(underlying, target);
		//
		return !(glb instanceof Type.Void);
	}

	/**
	 * Fork a row based on a given type variable which is expected to yield an
	 * array. For example, consider:
	 *
	 * <pre>
	 * int[]|bool fb = [1,2,3]
	 * </pre>
	 *
	 * <p>
	 * Here, we need to filter <code>int[]|bool</code> down to just
	 * <code>int[]</code>. This can then be pushed into the initialiser expressions
	 * where subtyping checking can be performed.
	 * </p>
	 *
	 * <p>
	 * A key challenge arises when the type variable is actually an existential. The
	 * following illustrates an example:
	 * </p>
	 *
	 * <pre>
	 * function id<T>(T x) -> T:
	 *    return x
	 *
	 * id([1,2])
	 * </pre>
	 *
	 * <p>
	 * In this case, we pull from <code>id([1,2])</code> since the return is
	 * discarded. This results in pushing <code>?0</code> into <code>[1,2]</code>
	 * which prevents filtering based on the underlying array type. To resolve this,
	 * apply a subtyping constraint <code>?0 :> ?1[]</code> for a freshly
	 * constructed variable <code>?1</code>.
	 * </p>
	 *
	 * @param row
	 * @param var
	 * @param subtyping
	 * @return
	 */
	private static Typing.Row[] forkOnArray(Typing.Row row, int var, Subtyping.Environment subtyping) {
		Type type = row.get(var);
		if (type instanceof Type.Any) {
			// NOTE: This case arises only because of open records (I believe). Really, we'd
			// want to pull to get the most precise type. But, it's difficult because this
			// is
			// specific to individual rows.
			return new Typing.Row[] { row.set(var, Type.AnyArray) };
		} else if (type instanceof Type.Existential) {
			wyfs.util.Pair<Typing.Row, Type.Existential[]> p = row.fresh(1);
			Type.Existential element = p.second()[0];
			Type.Array arr_t = new Type.Array(element);
			Subtyping.Constraints constraints = subtyping.isSubtype(type, arr_t);
			return new Typing.Row[] { row.set(var, arr_t).intersect(constraints) };
		} else {
			return forkOnPredicate(row, var, t -> t.as(Type.Array.class) != null);
		}
	}

	/**
	 * Fork a row based on a given type variable which is expected to yield a
	 * recording matching a given set of fields. For example, consider:
	 *
	 * <pre>
	 * {int f}|{int g} r = {f:1}
	 * </pre>
	 *
	 * <p>
	 * Here, we need to filter <code>{int f}|{int g}</code> down to just
	 * <code>{int f}</code>. This can then be pushed into the initialiser
	 * expressions where subtyping checking can be performed.
	 * </p>
	 *
	 * <p>
	 * A key challenge arises when the type variable is actually an existential. The
	 * following illustrates an example:
	 * </p>
	 *
	 * <pre>
	 * function id<T>(T x) -> T:
	 *    return x
	 *
	 * id({f:1})
	 * </pre>
	 *
	 * <p>
	 * In this case, we pull from <code>id({f:1})</code> since the return is
	 * discarded. This results in pushing <code>?0</code> into <code>{f:1}</code>
	 * which prevents filtering based on the underlying record type. To resolve
	 * this, apply a subtyping constraint <code>?0 :> {?1 f}</code> for a freshly
	 * constructed variable <code>?1</code>.
	 * </p>
	 *
	 * @param row
	 * @param var
	 * @param subtyping
	 * @return
	 */
	private static Typing.Row[] forkOnRecord(Typing.Row row, int var, Tuple<Identifier> fields,
			Subtyping.Environment subtyping) {
		Type type = row.get(var);
		if (type instanceof Type.Any) {
			// NOTE: This case arises only because of open records (I believe). Really, we'd
			// want to pull to get the most precise type. But, it's difficult because this
			// is
			// specific to individual rows.
			Tuple<Type.Field> fs = fields.map(n -> new Type.Field(n, Type.Any));
			return new Typing.Row[] { row.set(var, new Type.Record(false, fs)) };
		} else if (type instanceof Type.Existential) {
			wyfs.util.Pair<Typing.Row, Type.Existential[]> p = row.fresh(1);
			Type.Existential element = p.second()[0];
			Type.Array rec_t = new Type.Array(element);
			Subtyping.Constraints constraints = subtyping.isSubtype(type, rec_t);
			return new Typing.Row[] { row.set(var, rec_t).intersect(constraints) };
		} else {
			return forkOnPredicate(row, var, t -> isMatchingRecord(t, fields));
		}
	}

	/**
	 * Fork a row based on a given type variable which is expected to yield a
	 * reference. For example, consider:
	 *
	 * <pre>
	 * &int|int r = new 1
	 * </pre>
	 *
	 * <p>
	 * Here, we need to filter <code>&int|int</code> down to just <code>&int</code>.
	 * This can then be pushed into the initialiser expressions where subtyping
	 * checking can be performed.
	 * </p>
	 *
	 * <p>
	 * A key challenge arises when the type variable is actually an existential. The
	 * following illustrates an example:
	 * </p>
	 *
	 * <pre>
	 * function id<T>(T x) -> T:
	 *    return x
	 *
	 * id(new 1)
	 * </pre>
	 *
	 * <p>
	 * In this case, we pull from <code>id(new 1)</code> since the return is
	 * discarded. This results in pushing <code>?0</code> into <code>new 1</code>
	 * which prevents filtering based on the underlying reference type. To resolve
	 * this, apply a subtyping constraint <code>?0 :> &?1</code> for a freshly
	 * constructed variable <code>?1</code>.
	 * </p>
	 *
	 * @param row
	 * @param var
	 * @param subtyping
	 * @return
	 */
	private static Typing.Row[] forkOnReference(Typing.Row row, int var,
			Subtyping.Environment subtyping) {
		Type type = row.get(var);
		if (type instanceof Type.Any) {
			// NOTE: This case arises only because of open records (I believe). Really, we'd
			// want to pull to get the most precise type. But, it's difficult because this
			// is specific to individual rows.
			Type.Reference t = new Type.Reference(Type.Any);
			return new Typing.Row[] { row.set(var, t) };
		} else if (type instanceof Type.Existential) {
			wyfs.util.Pair<Typing.Row, Type.Existential[]> p = row.fresh(1);
			Type.Existential element = p.second()[0];
			Type.Reference ref_t = new Type.Reference(element);
			Subtyping.Constraints constraints = subtyping.isSubtype(type, ref_t);
			return new Typing.Row[] { row.set(var, ref_t).intersect(constraints) };
		} else {
			return forkOnPredicate(row, var, t -> isMatchingReference(t, subtyping));
		}
	}

	/**
	 * Check whether a given target reference is a match for a given reference
	 * initialiser. Specifically, this comes down to check the respective lifetimes
	 * are compatible.
	 *
	 * @param type
	 * @param expr
	 * @param subtyping
	 * @return
	 */
	private static boolean isMatchingReference(Type type, Subtyping.Environment subtyping) {
		Type.Reference ref = type.as(Type.Reference.class);
		return ref != null;
	}

	/**
	 * Fork a row based on a given type variable which is expected to yield a
	 * reference. For example, consider:
	 *
	 * <pre>
	 * &int|int r = new 1
	 * </pre>
	 *
	 * <p>
	 * Here, we need to filter <code>&int|int</code> down to just <code>&int</code>.
	 * This can then be pushed into the initialiser expressions where subtyping
	 * checking can be performed.
	 * </p>
	 *
	 * <p>
	 * A key challenge arises when the type variable is actually an existential. The
	 * following illustrates an example:
	 * </p>
	 *
	 * <pre>
	 * function id<T>(T x) -> T:
	 *    return x
	 *
	 * id(new 1)
	 * </pre>
	 *
	 * <p>
	 * In this case, we pull from <code>id(new 1)</code> since the return is
	 * discarded. This results in pushing <code>?0</code> into <code>new 1</code>
	 * which prevents filtering based on the underlying reference type. To resolve
	 * this, apply a subtyping constraint <code>?0 :> &?1</code> for a freshly
	 * constructed variable <code>?1</code>.
	 * </p>
	 *
	 * @param row
	 * @param var
	 * @param subtyping
	 * @return
	 */
	private static Typing.Row[] forkOnTuple(Typing.Row row, int var, int n, Subtyping.Environment subtyping) {
		Type type = row.get(var);
		if (type instanceof Type.Existential) {
			wyfs.util.Pair<Typing.Row, Type.Existential[]> p = row.fresh(n);
			Type.Existential[] elements = p.second();
			Type tup_t = Type.Tuple.create(elements);
			Subtyping.Constraints constraints = subtyping.isSubtype(type, tup_t);
			return new Typing.Row[] { row.set(var, tup_t).intersect(constraints) };
		} else {
			return forkOnPredicate(row, var, t -> isMatchingTuple(row.get(var), n));
		}
	}

	/**
	 * Check the target type is a tuple with sufficiently many fields.
	 *
	 * @param type The type to be checked as a tuple
	 * @param size The number of fields the tuple must have
	 * @return
	 */
	private static boolean isMatchingTuple(Type type, int size) {
		Type.Tuple tupe = type.as(Type.Tuple.class);
		return (tupe != null) && (tupe.size() == size);
	}

	private static boolean isMatchingRecordReference(Type type, Identifier field) {
		Type.Reference ref = type.as(Type.Reference.class);
		if (ref == null) {
			return false;
		}
		Type.Record rec = ref.getElement().as(Type.Record.class);
		return (rec != null) && rec.getField(field) != null;
	}

	/**
	 * Check the target type is a record with sufficiently matching fields. For
	 * closed records, this is straightforward as we must have exactly the same
	 * fields. For open records, we require only a subset of the required fields.
	 *
	 * @param type
	 * @param fields
	 * @return
	 */
	private static boolean isMatchingRecord(Type type, Tuple<Identifier> fields) {
		// Check really have a record
		Type.Record rec = type.as(Type.Record.class);
		if (rec == null) {
			return false;
		}
		// Extract field names from target record
		Tuple<Identifier> rec_fields = rec.getFields().map(f -> f.getName());
		// Check sufficiently matching fields.
		return isSubset(rec_fields, fields) && (rec.isOpen() || rec_fields.size() == fields.size());
	}

	/**
	 * Fork a row based on a given type variable which is expected to yield a lambda
	 * with a suitable parameter type. For example, consider:
	 *
	 * <pre>
	 * type fun_t is function(int)->(bool)
	 *
	 * fun_t|bool fb = &(int x -> (x==1))
	 * </pre>
	 *
	 * <p>
	 * Here, we need to filter <code>fun_t|bool</code> down to just
	 * <code>fun_t</code>. This can then be pushed into the lambda declaration where
	 * subtyping checking can be performed.
	 * </p>
	 *
	 * @param row
	 * @param var
	 * @param subtyping
	 * @return
	 */
	private static Typing.Row[] forkOnLambda(Typing.Row row, int var, Type parameter, Subtyping.Environment subtyping) {
		// Fork out all lambdas
		Typing.Row[] rows = forkOnLambda(row, var, subtyping);
		// Filter lambdas based on parameter
		for (int i = 0; i != rows.length; ++i) {
			Typing.Row ith = rows[i];
			if (ith != null) {
				Type.Callable t = ith.get(var).as(Type.Callable.class);
				rows[i] = ith.intersect(subtyping.isSubtype(parameter, t.getParameter()));
			}
		}
		return rows;
	}

	/**
	 * Fork a row based on a given type variable which is expected to yield a
	 * lambda. For example, consider:
	 *
	 * <pre>
	 * type fun_t is function(int)->(bool)
	 *
	 * fun_t|bool fb = &f
	 * </pre>
	 *
	 * <p>
	 * Here, we need to filter <code>fun_t|bool</code> down to just
	 * <code>fun_t</code>. This can then be pushed into the expression
	 * <code>&f</code> where subtyping checking can be performed.
	 * </p>
	 *
	 * <p>
	 * A key challenge arises when the type variable is actually an existential. The
	 * following illustrates an example:
	 * </p>
	 *
	 * <pre>
	 * function id<T>(T x) -> T:
	 *    return x
	 *
	 * id(&f)
	 * </pre>
	 *
	 * <p>
	 * In this case, we pull from <code>id(&f)</code> since the return is discarded.
	 * This results in pushing <code>?0</code> into <code>&f</code> which prevents
	 * filtering based on the underlying lambda type. To resolve this, apply a
	 * subtyping constraint <code>?0 :> function(?1)->(?2)</code> for freshly
	 * constructed variables <code>?1</code> and <code>?2</code>.
	 * </p>
	 *
	 * @param row
	 * @param var
	 * @param subtyping
	 * @return
	 */
	private static Typing.Row[] forkOnLambda(Typing.Row row, int var, Subtyping.Environment subtyping) {
		Type type = row.get(var);
		if (type instanceof Type.Any) {
			// NOTE: This case arises only because of open records (I believe). Really, we'd
			// want to pull to get the most precise type. But, it's difficult because this
			// is
			// specific to individual rows.
			Type.Callable t = new Type.Method(Type.Void, Type.Any);
			return new Typing.Row[] { row.set(var, t) };
		} else if (type instanceof Type.Existential) {
			wyfs.util.Pair<Typing.Row, Type.Existential[]> p = row.fresh(2);
			Type.Existential param = p.second()[0];
			Type.Existential ret = p.second()[1];
			Type.Callable fun_t = new Type.Function(param, ret);
			Subtyping.Constraints constraints = subtyping.isSubtype(type, fun_t);
			return new Typing.Row[] { row.set(var, fun_t).intersect(constraints) };
		} else {
			return forkOnPredicate(row, var, t -> t.as(Type.Callable.class) != null);
		}
	}

	private static boolean isMatchingLambda(Type type, int nParameters) {
		Type.Callable ct = type.as(Type.Callable.class);
		return ct != null && ct.getParameter().shape() == nParameters;
	}

	/**
	 * Filter the type of a given variable based on a given predicate. For example,
	 * consider:
	 *
	 * <pre>
	 * int[]|bool xs = [1,2,3]
	 * </pre>
	 *
	 * To process this, must filter <code>int[]|bool</code> to extract arrays. This,
	 * in turn, allows one to push the element type into the initialiser
	 * expressions.
	 *
	 * @param row
	 * @param var
	 * @param fn
	 * @return
	 */
	private static Typing.Row[] forkOnPredicate(Typing.Row row, int var, Predicate<Type> fn) {
		Type type = row.get(var);
		List<Type> candidates = new ArrayList<>();
		filterOnPredicate(type, fn, candidates);
		//
		Typing.Row[] rows = new Typing.Row[candidates.size()];
		for (int i = 0; i != rows.length; ++i) {
			rows[i] = row.set(var, candidates.get(i));
		}
		return rows;
	}

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
		if (type instanceof Type.Any) {
			// No forward type information available, hence use the natural type.
			return row.set(var, underlying);
		} else if (type instanceof Type.Existential) {
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
		if (type instanceof Type.Any) {
			// No forward type information available, hence use the natural type.
			return row.set(var, Type.IntArray);
		} else {
			List<Type> candidates = new ArrayList<>();
			filterOnUnderlying(type, Type.Array.class, candidates);
			// Filter out any non-integer arrays which were accidentally netted
			candidates = select(candidates, t -> subtyping.isSatisfiableSubtype(Type.IntArray, t));
			// Sanity check only one found, otherwise have ambiguity.
			return (candidates.size() == 1) ? row.set(var, candidates.get(0)) : null;
		}
	}

	/**
	 * Extract atoms with matching underlying types. For example, consider the
	 * following:
	 *
	 * <pre>
	 * type string is (int[] xs) where ...
	 * </pre>
	 *
	 * Then, extracting the underlying array type of <code>int|string|int[]</code>
	 * should return <code>string</code> and <code>int[]</code>. Observe that we
	 * want the most precise array type which includes all nominal information.
	 *
	 * @param candidate
	 * @param underlying
	 * @param candidates
	 */
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
			// This is broken really because we end up with a type for the expression
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
	 * Extract (sub)types based on an underlying predicate. For example, consider
	 * the type <code>int|{int f}|{int g}</code>. Using this method, we can extract
	 * record types which have a field <code>f</code>.
	 *
	 * @param candidate
	 * @param fn
	 * @param candidates
	 */
	private static void filterOnPredicate(Type candidate, Predicate<Type> fn, List<Type> candidates) {
		// FIXME: this method should be moved into wyil.lang.WyilFile.Type to replace
		// the existing filter method.
		if (fn.test(candidate)) {
			// Matched greatest atom
			candidates.add(candidate);
		} else if (candidate instanceof Type.Union) {
			// NOTE: need to break type down in order to catch cases such as e.g. int :>
			// pos|neg.
			Type.Union t = (Type.Union) candidate;
			for (int i = 0; i != t.size(); ++i) {
				filterOnPredicate(t.get(i), fn, candidates);
			}
		} else if (candidate instanceof Type.Nominal) {
			Type.Nominal t = (Type.Nominal) candidate;
			filterOnPredicate(t.getConcreteType(), fn, candidates);
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
		if (supertype instanceof Type.Any) {
			return row.set(var, subtype);
		} else if(subtype instanceof Type.Void) {
			// NOTE: This is an edge case when we try to use the return value from a
			// function which returns void. In such case, we want to kill the typing row
			// dead.
			return row.set(var, subtype);
		} else {
			// Apply the subtype tests which, if it fails, it will falsify this row.
			Subtyping.Constraints constraints = environment.isSubtype(supertype, subtype);
			// Set the type of this variable to the subtype (since this is fixed) and apply
			// any constraints on the underlying type variables.
			return (row == null) ? row : row.intersect(constraints);
		}
	}

	/**
	 * Project a given type as an array and extract its element type. Otherwise,
	 * return null.
	 *
	 * @param t
	 * @return
	 */
	private Type getArrayElement(Type t) {
		Type.Array at = t.as(Type.Array.class);
		return (at == null) ? null : at.getElement();
	}

	/**
	 * Project a given type as a lambda and extract its ith parameter type.
	 * Otherwise, return null.
	 *
	 * @param t
	 * @param i
	 * @return
	 */
	private static Type getLambdaParameter(Type t, int i) {
		Type.Callable ct = t.as(Type.Callable.class);
		return (ct == null || ct.getParameter().shape() <= i) ? null : ct.getParameter().dimension(i);
	}

	/**
	 * Project a given type as a lambda and extract its return type. Otherwise,
	 * return null.
	 *
	 * @param t
	 * @return
	 */
	private static Type getLambdaReturn(Type t) {
		Type.Callable ct = t.as(Type.Callable.class);
		return (ct == null) ? null : ct.getReturn();
	}

	/**
	 * Project a given type as a reference to a record with a given field, and then
	 * extract the type of that field. Otherwise, return null.
	 *
	 * @param t
	 * @param field
	 * @return
	 */
	private static Type getReferenceField(Type t, Identifier field) {
		Type.Reference r = t.as(Type.Reference.class);
		return (r == null) ? null : getRecordField(r.getElement(), field);
	}

	/**
	 * Project a given type as a reference, and then extract the target type of that
	 * reference. Otherwise, return null.
	 *
	 * @param t
	 * @return
	 */
	private Type getReferenceElement(Type type) {
		Type.Reference ref = type.as(Type.Reference.class);
		return (ref == null) ? null : ref.getElement();
	}

	/**
	 * Project a given type as a record with a given field, and then extract the
	 * type of that field. Otherwise, return null.
	 *
	 * @param t
	 * @param field
	 * @return
	 */
	private static Type getRecordField(Type t, Identifier field) {
		Type.Record r = t.as(Type.Record.class);
		return (r == null) ? r : r.getField(field);
	}

	/**
	 * Project a given type as a record with a given field, and then extract the
	 * type of that field. If the original type is not a record, this is an error.
	 * However, if the record does not contain a given field, then simply return a
	 * default.
	 *
	 * @param t
	 * @param field
	 * @return
	 */
	private static Type getRecordFieldWithDefault(Type t, Identifier field, Type deFault) {
		Type.Record r = t.as(Type.Record.class);
		if (r == null) {
			return null;
		}
		Type f = r.getField(field);
		return (f == null) ? deFault : f;
	}

	/**
	 * Project a given type as a tuple and extract the ith field of that tuple.
	 *
	 * @param t
	 * @param field
	 * @return
	 */
	private static Type getTupleElement(Type t, int ith) {
		Type.Tuple r = t.as(Type.Tuple.class);
		return (r == null) ? r : r.get(ith);
	}

	/**
	 * Assign the type of a regular expression.
	 *
	 * @param e
	 * @param var
	 * @return
	 */
	private static Predicate<Typing.Row[]> typeStandardExpression(Expr e, int var) {
		SyntacticHeap heap = e.getHeap();
		return rows -> {
			if (rows.length != 1 || var < 0) {
				// invalid typing
				return false;
			} else {
				Type type = rows[0].get(var);
				e.setType(heap.allocate(type));
				return true;
			}
		};
	}

	private Predicate<Typing.Row[]> typeInvokeExpression(Expr.Invoke e, int signature, int template) {
		Decl.Link<Decl.Callable> link = e.getLink();
		Decl.Binding<Type.Callable, Decl.Callable> binding = e.getBinding();
		return rows -> {
			if (signature < 0) {
				// invalid typing
				return false;
			} else if (rows.length == 1) {
				Type.Callable sig = (Type.Callable) rows[0].get(signature);
				Type tem = rows[0].get(template);
				// Resolve link
				link.resolve(link.lookup(sig));
				Decl.Callable d = link.getTarget();
				// Set binding arguments (if necessary)
				if(d.getTemplate().size() > 0 && binding.getArguments().size() == 0) {
					// Yes, is necessary as we have a declaration with template arguments but
					// currently no specified arguments in the binding.
					Type[] tems = new Type[tem.shape()];
					// Unpack types from shape. This is ugly but is necessary as tuple types
					// dissolve into the underlying type when there is a single component, etc.
					for(int i=0;i!=tem.shape();++i) {
						tems[i] = tem.dimension(i);
					}
					// Finally, update the binding arguments.
					e.getBinding().setArguments(new Tuple<>(tems));
				}
				return true;
			} else if (rows.length > 1) {
				syntaxError(link.getName(), AMBIGUOUS_CALLABLE, link.getCandidates());
			}
			return false;
		};
	}

	private Predicate<Typing.Row[]> typeLambdaAccess(Expr.LambdaAccess e, int sig) {
		Decl.Link<Decl.Callable> link = e.getLink();
		return rows -> {
			if (sig < 0) {
				// invalid typing
				return false;
			} else if (rows.length == 1) {
				Type.Callable signature = rows[0].get(sig).as(Type.Callable.class);
				link.resolve(link.lookup(signature));
				return true;
			} else if (rows.length > 1) {
				syntaxError(link.getName(), AMBIGUOUS_CALLABLE, link.getCandidates());
			}
			return false;
		};
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
	private Type extractFieldType(Type.Record type, Identifier field) {
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
	@SuppressWarnings("unchecked")
	private <T extends Type> T extractType(Class<T> kind, Type type, int errcode, SyntacticItem item) {
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
	private void checkForError(SyntacticItem element, Typing before, Typing after, int lhs, Type rhs) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, before.types(lhs), rhs);
		}
	}

	private void checkForError(SyntacticItem element, Typing before, Typing after, Type lhs, int rhs) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, lhs, before.types(rhs));
		}
	}

	private void checkForError(SyntacticItem element, Typing before, Typing after, Tuple<Type> lhs, int rhs) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, lhs, before.types(rhs));
		}
	}

	private void checkForError(SyntacticItem element, int code, Typing before, Typing after, int lhs, int rhs) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, code, before.types(lhs), before.types(rhs));
		}
	}

	private void checkForError(SyntacticItem element, int code, Typing before, Typing after) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, code);
		}
	}

	private void checkForError(SyntacticItem element, Typing before, Typing after, int lhs, int rhs,
			Function<Type, Type> projection) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, before.types(lhs), before.types(rhs).map(projection));
		}
	}

	private void checkForError(SyntacticItem element, Typing before, Typing after, int lhs,
			Function<Type, Type> projection, int rhs) {
		if (!before.isEmpty() && after.isEmpty()) {
			// Concretise to avoid variables in error messages
			before = before.concretise();
			// No valid typings remain!
			syntaxError(element, SUBTYPE_ERROR, before.types(lhs).map(projection), before.types(rhs));
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

	private static <T> T internalFailure(String msg, SyntacticItem e) {
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
		@SuppressWarnings("unchecked")
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


	/**
	 * Represents the enclosing scope for a function or method declaration.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class FunctionOrMethodScope extends EnclosingScope {
		private final Decl.FunctionOrMethod declaration;

		public FunctionOrMethodScope(Decl.FunctionOrMethod declaration) {
			super(null);
			this.declaration = declaration;
		}

		public Decl.FunctionOrMethod getDeclaration() {
			return declaration;
		}

	}

	private static class NamedBlockScope extends EnclosingScope {
		private final Stmt.NamedBlock stmt;

		public NamedBlockScope(EnclosingScope parent, Stmt.NamedBlock stmt) {
			super(parent);
			this.stmt = stmt;
		}
	}
}
