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

import static wybs.lang.SyntaxError.InternalFailure;
import static wybs.util.AbstractCompilationUnit.ITEM_bool;
import static wybs.util.AbstractCompilationUnit.ITEM_int;
import static wybs.util.AbstractCompilationUnit.ITEM_null;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import wybs.lang.*;
import wybs.lang.NameResolver.ResolutionError;
import wybs.util.*;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.lang.*;
import wyc.util.AbstractVisitor;
import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.type.SubtypeOperator.LifetimeRelation;
import wyil.type.SubtypeOperator.SemanticType;
import wyil.type.TypeSystem;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Type;
import wyc.task.CompileTask;

import static wyc.lang.WhileyFile.*;
import static wyc.util.ErrorMessages.*;

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
 *        return y
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
public class FlowTypeCheck {

	private final CompileTask builder;
	private final TypeSystem typeSystem;

	public FlowTypeCheck(CompileTask builder) {
		this.builder = builder;
		this.typeSystem = builder.getTypeSystem();
	}

	// =========================================================================
	// WhileyFile(s)
	// =========================================================================

	public void check(List<WhileyFile> files) {
		// Perform necessary type checking of Whiley files
		for (WhileyFile wf : files) {
			check(wf);
		}
	}

	public void check(WhileyFile wf) {
		for (Decl decl : wf.getDeclarations()) {
			check(decl);
		}
	}

	// =========================================================================
	// Declarations
	// =========================================================================

	public void check(Decl decl) {
		if (decl instanceof Decl.Import) {
			// Can ignore
		} else if (decl instanceof Decl.StaticVariable) {
			checkStaticVariableDeclaration((Decl.StaticVariable) decl);
		} else if (decl instanceof Decl.Type) {
			checkTypeDeclaration((Decl.Type) decl);
		} else if (decl instanceof Decl.FunctionOrMethod) {
			checkFunctionOrMethodDeclaration((Decl.FunctionOrMethod) decl);
		} else {
			checkPropertyDeclaration((Decl.Property) decl);
		}
	}

	/**
	 * Resolve types for a given type declaration. If an invariant expression is
	 * given, then we have to check and resolve types throughout the expression.
	 *
	 * @param td
	 *            Type declaration to check.
	 * @throws IOException
	 */
	public void checkTypeDeclaration(Decl.Type decl) {
		Environment environment = new Environment();
		// Check type is contractive
		checkContractive(decl);
		// Check variable declaration is not empty
		checkNonEmpty(decl.getVariableDeclaration(), environment);
		// Check the type invariant
		checkConditions(decl.getInvariant(), true, environment);
	}

	/**
	 * check and check types for a given constant declaration.
	 *
	 * @param cd
	 *            Constant declaration to check.
	 * @throws IOException
	 */
	public void checkStaticVariableDeclaration(Decl.StaticVariable decl) {
		Environment environment = new Environment();
		if (decl.hasInitialiser()) {
			Type type = checkExpression(decl.getInitialiser(), environment);
			checkIsSubtype(decl.getType(), type, environment, decl.getInitialiser());
		}
	}

	/**
	 * Type check a given function or method declaration.
	 *
	 * @param fd
	 *            Function or method declaration to check.
	 * @throws IOException
	 */
	public void checkFunctionOrMethodDeclaration(Decl.FunctionOrMethod d) {
		// Construct initial environment
		Environment environment = new Environment();
		// Update environment so this within declared lifetimes
		environment = declareThisWithin(d, environment);
		// Check parameters and returns are not empty (i.e. are not equivalent
		// to void, as this is non-sensical).
		checkNonEmpty(d.getParameters(), environment);
		checkNonEmpty(d.getReturns(), environment);
		// Check any preconditions (i.e. requires clauses) provided.
		checkConditions(d.getRequires(), true, environment);
		// Check any postconditions (i.e. ensures clauses) provided.
		checkConditions(d.getEnsures(), true, environment);
		// FIXME: Add the "this" lifetime
		if (d.getModifiers().match(Modifier.Native.class) == null) {
			// Create scope representing this declaration
			EnclosingScope scope = new FunctionOrMethodScope(d);
			// Check type information throughout all statements in body.
			Environment last = checkBlock(d.getBody(), environment, scope);
			// Check return value
			checkReturnValue(d, last);
		} else {
			// NOTE: we obviously don't need to check the body of a native function or
			// method. Attempting to do so causes problems because checkReturnValue will
			// fail.
		}
	}

	/**
	 * Update the environment to reflect the fact that the special "this" lifetime
	 * is contained within all declared lifetime parameters. Observe that this only
	 * makes sense if the enclosing declaration is for a method.
	 *
	 * @param decl
	 * @param environment
	 * @return
	 */
	public Environment declareThisWithin(Decl.FunctionOrMethod decl, Environment environment) {
		if (decl instanceof Decl.Method) {
			Decl.Method method = (Decl.Method) decl;
			environment = environment.declareWithin("this", method.getLifetimes());
		}
		return environment;
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
		if (d.match(Modifier.Native.class) == null && last != BOTTOM && d.getReturns().size() != 0) {
			// In this case, code reaches the end of the function or method and,
			// furthermore, that this requires a return value. To get here means
			// that there was no explicit return statement given on at least one
			// execution path.
			syntaxError("missing return statement", d);
		}
	}

	public void checkPropertyDeclaration(Decl.Property d) {
		// Construct initial environment
		Environment environment = new Environment();
		// Check parameters and returns are not empty (i.e. are not equivalent
		// to void, as this is non-sensical).
		checkNonEmpty(d.getParameters(), environment);
		checkNonEmpty(d.getReturns(), environment);
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
	 * @param block
	 *            Block of statements to flow sensitively type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
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
	 * @param forest
	 *            Block of statements to flow-sensitively type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 */
	private Environment checkStatement(Stmt stmt, Environment environment, EnclosingScope scope) {
		try {
			if (environment == BOTTOM) {
				// Sanity check incoming environment
				return syntaxError(errorMessage(UNREACHABLE_CODE), stmt);
			} else if (stmt instanceof Decl.Variable) {
				return checkVariableDeclaration((Decl.Variable) stmt, environment, scope);
			} else if (stmt instanceof Stmt.Assign) {
				return checkAssign((Stmt.Assign) stmt, environment, scope);
			} else if (stmt instanceof Stmt.Return) {
				return checkReturn((Stmt.Return) stmt, environment, scope);
			} else if (stmt instanceof Stmt.IfElse) {
				return checkIfElse((Stmt.IfElse) stmt, environment, scope);
			} else if (stmt instanceof Stmt.NamedBlock) {
				return checkNamedBlock((Stmt.NamedBlock) stmt, environment, scope);
			} else if (stmt instanceof Stmt.While) {
				return checkWhile((Stmt.While) stmt, environment, scope);
			} else if (stmt instanceof Stmt.Switch) {
				return checkSwitch((Stmt.Switch) stmt, environment, scope);
			} else if (stmt instanceof Stmt.DoWhile) {
				return checkDoWhile((Stmt.DoWhile) stmt, environment, scope);
			} else if (stmt instanceof Stmt.Break) {
				return checkBreak((Stmt.Break) stmt, environment, scope);
			} else if (stmt instanceof Stmt.Continue) {
				return checkContinue((Stmt.Continue) stmt, environment, scope);
			} else if (stmt instanceof Stmt.Assert) {
				return checkAssert((Stmt.Assert) stmt, environment, scope);
			} else if (stmt instanceof Stmt.Assume) {
				return checkAssume((Stmt.Assume) stmt, environment, scope);
			} else if (stmt instanceof Stmt.Fail) {
				return checkFail((Stmt.Fail) stmt, environment, scope);
			} else if (stmt instanceof Stmt.Debug) {
				return checkDebug((Stmt.Debug) stmt, environment, scope);
			} else if (stmt instanceof Stmt.Skip) {
				return checkSkip((Stmt.Skip) stmt, environment, scope);
			} else if (stmt instanceof Expr.Invoke) {
				checkInvoke((Expr.Invoke) stmt, environment);
				return environment;
			} else if (stmt instanceof Expr.IndirectInvoke) {
				checkIndirectInvoke((Expr.IndirectInvoke) stmt, environment);
				return environment;
			} else {
				return internalFailure("unknown statement: " + stmt.getClass().getName(), stmt);
			}
		} catch (SyntaxError e) {
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
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
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
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 */
	private Environment checkAssume(Stmt.Assume stmt, Environment environment, EnclosingScope scope) {
		return checkCondition(stmt.getCondition(), true, environment);
	}

	/**
	 * Type check a fail statement. The environment after a fail statement is
	 * "bottom" because that represents an unreachable program point.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 */
	private Environment checkFail(Stmt.Fail stmt, Environment environment, EnclosingScope scope) {
		return BOTTOM;
	}

	/**
	 * Type check a variable declaration statement. In particular, when an
	 * initialiser is given we must check it is well-formed and that it is a subtype
	 * of the declared type.
	 *
	 * @param decl
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 */
	private Environment checkVariableDeclaration(Decl.Variable decl, Environment environment, EnclosingScope scope)
			throws IOException {
		// Check type of initialiser.
		if (decl.hasInitialiser()) {
			Type type = checkExpression(decl.getInitialiser(), environment);
			checkIsSubtype(decl.getType(), type, environment, decl.getInitialiser());
		}
		// Done.
		return environment;
	}

	/**
	 * Type check an assignment statement.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 */
	private Environment checkAssign(Stmt.Assign stmt, Environment environment, EnclosingScope scope)
			throws IOException {
		Tuple<LVal> lvals = stmt.getLeftHandSide();
		List<Pair<Expr, Type>> rvals = checkMultiExpressions(stmt.getRightHandSide(), environment);
		// Check the number of expected values matches the number of values
		// produced by the right-hand side.
		if (lvals.size() < rvals.size()) {
			syntaxError("too many values provided on right-hand side", stmt);
		} else if (lvals.size() > rvals.size()) {
			syntaxError("not enough values provided on right-hand side", stmt);
		}
		// For each value produced, check that the variable being assigned
		// matches the value produced.
		for (int i = 0; i != rvals.size(); ++i) {
			Type lval = checkLVal(lvals.get(i), environment);
			Pair<Expr, Type> rval = rvals.get(i);
			checkIsSubtype(lval, rval.getSecond(), environment, rval.getFirst());
		}
		return environment;
	}

	/**
	 * Type check a break statement. This requires propagating the current
	 * environment to the block destination, to ensure that the actual types of all
	 * variables at that point are precise.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 */
	private Environment checkBreak(Stmt.Break stmt, Environment environment, EnclosingScope scope) {
		// FIXME: need to check environment to the break destination
		return BOTTOM;
	}

	/**
	 * Type check a continue statement. This requires propagating the current
	 * environment to the block destination, to ensure that the actual types of all
	 * variables at that point are precise.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 */
	private Environment checkContinue(Stmt.Continue stmt, Environment environment, EnclosingScope scope) {
		// FIXME: need to check environment to the continue destination
		return BOTTOM;
	}

	/**
	 * Type check an assume statement. This requires checking that the expression
	 * being printed is well-formed and has string type.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 */
	private Environment checkDebug(Stmt.Debug stmt, Environment environment, EnclosingScope scope) {
		Type type = checkExpression(stmt.getOperand(), environment);
		checkIsSubtype(new Type.Array(Type.Int), type, environment, stmt.getOperand());
		return environment;
	}

	/**
	 * Type check a do-while statement.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 * @throws ResolveError
	 *             If a named type within this statement cannot be resolved within
	 *             the enclosing project.
	 */
	private Environment checkDoWhile(Stmt.DoWhile stmt, Environment environment, EnclosingScope scope) {
		// Type check loop body
		environment = checkBlock(stmt.getBody(), environment, scope);
		// Type check invariants
		checkConditions(stmt.getInvariant(), true, environment);
		// Determine and update modified variables
		Tuple<Decl.Variable> modified = determineModifiedVariables(stmt.getBody());
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
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 * @throws ResolveError
	 *             If a named type within this statement cannot be resolved within
	 *             the enclosing project.
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
		return union(trueEnvironment, falseEnvironment);
	}

	/**
	 * Type check a <code>return</code> statement. If a return expression is given,
	 * then we must check that this is well-formed and is a subtype of the enclosing
	 * function or method's declared return type. The environment after a return
	 * statement is "bottom" because that represents an unreachable program point.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @param scope
	 *            The stack of enclosing scopes
	 * @return
	 * @throws ResolveError
	 *             If a named type within this statement cannot be resolved within
	 *             the enclosing project.
	 */
	private Environment checkReturn(Stmt.Return stmt, Environment environment, EnclosingScope scope)
			throws IOException {
		// Type check the operands for the return statement (if any)
		List<Pair<Expr, Type>> returns = checkMultiExpressions(stmt.getReturns(), environment);
		// Determine the set of return types for the enclosing function or
		// method. This then allows us to check the given operands are
		// appropriate subtypes.
		Decl.FunctionOrMethod fm = scope.getEnclosingScope(FunctionOrMethodScope.class).getDeclaration();
		Tuple<Type> types = fm.getReturns().project(2, Type.class);
		// Sanity check the number of arguments being returned.
		if (returns.size() < types.size()) {
			// In this case, a return statement was provided with too few return
			// values compared with the number declared for the enclosing
			// method.
			syntaxError("not enough return values provided", stmt);
		} else if (returns.size() > types.size()) {
			// In this case, a return statement was provided with too many
			// return values compared with the number declared for the enclosing
			// method. Therefore, identify first unnecessary return
			Expr extra = returns.get(types.size()).getFirst();
			// And, generate syntax error for that
			syntaxError("too many return values provided", extra);
		}
		// Number of return values match number declared for enclosing
		// function/method. Now, check they have appropriate types.
		for (int i = 0; i != types.size(); ++i) {
			Pair<Expr, Type> p = returns.get(i);
			Type t = types.get(i);
			checkIsSubtype(t, p.getSecond(), environment, p.getFirst());
		}
		// Return bottom as following environment to signal that control-flow
		// cannot continue here. Thus, any following statements will encounter
		// the BOTTOM environment and, hence, report an appropriate error.
		return BOTTOM;
	}

	/**
	 * Type check a <code>skip</code> statement, which has no effect on the
	 * environment.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
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
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 */
	private Environment checkSwitch(Stmt.Switch stmt, Environment environment, EnclosingScope scope)
			throws IOException {
		// Type check the expression being switched upon
		checkExpression(stmt.getCondition(), environment);
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
				checkExpression(e, environment);
			}
			// Check case block
			Environment localEnv = environment;
			localEnv = checkBlock(c.getBlock(), localEnv, scope);
			// Merge resulting environment
			if (finalEnv == null) {
				finalEnv = localEnv;
			} else {
				finalEnv = union(finalEnv, localEnv);
			}
			// Keep track of whether a default
			hasDefault |= (c.getConditions().size() == 0);
		}

		if (!hasDefault) {
			// in this case, there is no default case in the switch. We must
			// therefore assume that there are values which will fall right
			// through the switch statement without hitting a case. Therefore,
			// we must include the original environment to accound for this.
			finalEnv = union(finalEnv, environment);
		}

		return finalEnv;
	}

	/**
	 * Type check a <code>NamedBlock</code> statement.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
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
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into this
	 *            block
	 * @return
	 * @throws ResolveError
	 *             If a named type within this statement cannot be resolved within
	 *             the enclosing project.
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
		Tuple<Decl.Variable> modified = determineModifiedVariables(stmt.getBody());
		stmt.setModified(stmt.getHeap().allocate(modified));
		// Return false environment to represent flow after loop.
		return falseEnvironment;
	}

	/**
	 * Determine the set of modifier variables for a given statement block. A
	 * modified variable is one which is assigned.
	 *
	 * @param block
	 * @param scope
	 * @param modified
	 */
	private Tuple<Decl.Variable> determineModifiedVariables(Stmt.Block block) {
		HashSet<Decl.Variable> modified = new HashSet<>();
		determineModifiedVariables(block, modified);
		return new Tuple<>(modified);
	}

	private void determineModifiedVariables(Stmt.Block block, Set<Decl.Variable> modified) {
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			switch (stmt.getOpcode()) {
			case STMT_assign: {
				Stmt.Assign s = (Stmt.Assign) stmt;
				for (LVal lval : s.getLeftHandSide()) {
					Expr.VariableAccess lv = extractAssignedVariable(lval);
					if (lv == null) {
						// FIXME: this is not an ideal solution long term. In
						// particular, we really need this method to detect not
						// just modified variables, but also modified locations
						// in general (e.g. assignments through references, etc)
						continue;
					} else {
						modified.add(lv.getVariableDeclaration());
					}
				}
				break;
			}
			case STMT_dowhile: {
				Stmt.DoWhile s = (Stmt.DoWhile) stmt;
				determineModifiedVariables(s.getBody(), modified);
				break;
			}
			case STMT_if:
			case STMT_ifelse: {
				Stmt.IfElse s = (Stmt.IfElse) stmt;
				determineModifiedVariables(s.getTrueBranch(), modified);
				if (s.hasFalseBranch()) {
					determineModifiedVariables(s.getFalseBranch(), modified);
				}
				break;
			}
			case STMT_namedblock: {
				Stmt.NamedBlock s = (Stmt.NamedBlock) stmt;
				determineModifiedVariables(s.getBlock(), modified);
				break;
			}
			case STMT_switch: {
				Stmt.Switch s = (Stmt.Switch) stmt;
				for (Stmt.Case c : s.getCases()) {
					determineModifiedVariables(c.getBlock(), modified);
				}
				break;
			}
			case STMT_while: {
				Stmt.While s = (Stmt.While) stmt;
				determineModifiedVariables(s.getBody(), modified);
				break;
			}
			}
		}
	}

	/**
	 * Determine the modified variable for a given LVal. Almost all lvals modify
	 * exactly one variable, though dereferences don't.
	 *
	 * @param lval
	 * @param scope
	 * @return
	 */
	private Expr.VariableAccess extractAssignedVariable(LVal lval) {
		if (lval instanceof Expr.VariableAccess) {
			return (Expr.VariableAccess) lval;
		} else if (lval instanceof Expr.RecordAccess) {
			Expr.RecordAccess e = (Expr.RecordAccess) lval;
			return extractAssignedVariable((LVal) e.getOperand());
		} else if (lval instanceof Expr.ArrayAccess) {
			Expr.ArrayAccess e = (Expr.ArrayAccess) lval;
			return extractAssignedVariable((LVal) e.getFirstOperand());
		} else if (lval instanceof Expr.Dereference) {
			return null;
		} else {
			internalFailure(errorMessage(INVALID_LVAL_EXPRESSION), lval);
			return null; // dead code
		}
	}

	// =========================================================================
	// LVals
	// =========================================================================

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
	 * @param condition
	 *            The condition being type checked
	 * @param sign
	 *            The assumed outcome of the condition (either true or false).
	 * @param environment
	 *            The environment going into the condition
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
		case EXPR_logiaclimplication:
			return checkLogicalImplication((Expr.LogicalImplication) condition, sign, environment);
		case EXPR_is:
			return checkIs((Expr.Is) condition, sign, environment);
		case EXPR_logicaluniversal:
		case EXPR_logicalexistential:
			return checkQuantifier((Expr.Quantifier) condition, sign, environment);
		default:
			Type t = checkExpression(condition, environment);
			checkIsSubtype(Type.Bool, t, environment, condition);
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
	 * @param operands
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
			return union(refinements);
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
	 * @param operands
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
			return union(refinements);
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
			return union(left, right);
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
		try {
			Expr lhs = expr.getOperand();
			Type lhsT = checkExpression(expr.getOperand(), environment);
			Type rhsT = expr.getTestType();
			SemanticType lhsST = toSemanticType(lhsT);
			SemanticType rhsST = toSemanticType(rhsT);
			// Sanity check operands for this type test
			SemanticType glbForTrueBranch = lhsST.intersect(rhsST);
			SemanticType glbForFalseBranch = lhsST.subtract(rhsST);
			if (typeSystem.isVoid(glbForFalseBranch, environment)) {
				// DEFINITE TRUE CASE
				syntaxError(errorMessage(BRANCH_ALWAYS_TAKEN), expr);
			} else if (typeSystem.isVoid(glbForTrueBranch, environment)) {
				// DEFINITE FALSE CASE
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsT, rhsT), expr);
			}
			//
			Pair<Decl.Variable, Type> extraction = extractTypeTest(lhs, expr.getTestType(), environment);
			if (extraction != null) {
				Decl.Variable var = extraction.getFirst();
				SemanticType refinement = toSemanticType(extraction.getSecond());
				// Update the typing environment accordingly.
				environment = environment.refineType(var, refinement, sign);
			}
			//
			return environment;
		} catch (ResolutionError e) {
			return syntaxError(e.getMessage(), expr);
		}
	}

	/**
	 * <p>
	 * Extract the "true" test from a given type test in order that we might try to
	 * retype it. This does not always succeed if, for example, the expression being
	 * tested cannot be retyped. An example would be a test like
	 * <code>arr[i] is int</code> as, in this case, we cannot retype
	 * <code>arr[i]</code>.
	 * </p>
	 *
	 * <p>
	 * In the simple case of e.g. <code>x is int</code> we just extract
	 * <code>x</code> and type <code>int</code>. The more interesting case arises
	 * when there is at least one field access involved. For example,
	 * <code>x.f is int</code> extracts variable <code>x</code> with type
	 * <code>{int f, ...}</code> (which is a safe approximation).
	 * </p>
	 *
	 * @param expr
	 * @param type
	 * @return A pair on successful extraction, or null if possible extraction.
	 */
	private Pair<Decl.Variable, Type> extractTypeTest(Expr expr, Type type, Environment environment) {
		if (expr instanceof Expr.VariableAccess) {
			Expr.VariableAccess var = (Expr.VariableAccess) expr;
			Decl.Variable decl = var.getVariableDeclaration();
			return new Pair<>(var.getVariableDeclaration(), type);
		} else if (expr instanceof Expr.RecordAccess) {
			Expr.RecordAccess ra = (Expr.RecordAccess) expr;
			Decl.Variable field = new Decl.Variable(new Tuple<>(), ((Expr.RecordAccess) expr).getField(), type);
			Type.Record recT = new Type.Record(true, new Tuple<>(field));
			return extractTypeTest(ra.getOperand(), recT, environment);
		} else {
			// no extraction is possible
			return null;
		}
	}

	private Environment checkQuantifier(Expr.Quantifier stmt, boolean sign, Environment env) {
		checkNonEmpty(stmt.getParameters(), env);
		// NOTE: We throw away the returned environment from the body. This is
		// because any type tests within the body are ignored outside.
		checkCondition(stmt.getOperand(), true, env);
		return env;
	}

	protected Environment union(Environment... environments) {
		Environment result = environments[0];
		for (int i = 1; i != environments.length; ++i) {
			result = union(result, environments[i]);
		}
		//
		return result;
	}

	public Environment union(Environment left, Environment right) {
		if (left == right || right == BOTTOM) {
			return left;
		} else if (left == BOTTOM) {
			return right;
		} else {
			Environment result = new Environment();
			Set<Decl.Variable> leftRefinements = left.getRefinedVariables();
			Set<Decl.Variable> rightRefinements = right.getRefinedVariables();
			for (Decl.Variable var : leftRefinements) {
				if (rightRefinements.contains(var)) {
					// We have a refinement on both branches
					SemanticType leftT = left.getType(var);
					SemanticType rightT = right.getType(var);
					result = result.setType(var, leftT.union(rightT));
				}
			}
			return result;
		}
	}

	/**
	 * Type check a given lval assuming an initial environment. This returns the
	 * largest type which can be safely assigned to the lval. Observe that this type
	 * is determined by the declared type of the variable being assigned.
	 *
	 * @param expression
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
		default:
			return internalFailure("unknown lval encountered (" + lval.getClass().getSimpleName() + ")", lval);
		}
		lval.setType(lval.getHeap().allocate(type));
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
		try {
			// Resolve variable declaration being accessed
			Decl.StaticVariable decl = typeSystem.resolveExactly(lval.getName(), Decl.StaticVariable.class);
			//
			return decl.getType();
		} catch (ResolutionError e) {
			return syntaxError(errorMessage(RESOLUTION_ERROR, lval.getName().toString()), lval, e);
		}
	}

	public Type checkArrayLVal(Expr.ArrayAccess lval, Environment environment) {
		Expr source = lval.getFirstOperand();
		Expr subscript = lval.getSecondOperand();
		//
		Type sourceT = checkExpression(source, environment);
		Type.Array writeableArrayT = checkIsArrayType(sourceT, AccessMode.WRITING, environment, source);
		Type subscriptT = checkExpression(subscript, environment);
		checkIsSubtype(new Type.Int(), subscriptT, environment, subscript);
		//
		return writeableArrayT.getElement();
	}

	public Type checkRecordLVal(Expr.RecordAccess lval, Environment environment) {
		Type src = checkExpression(lval.getOperand(), environment);
		Type.Record writeableRecordT = checkIsRecordType(src, AccessMode.WRITING, environment, lval.getOperand());
		//
		Type type = writeableRecordT.getField(lval.getField());
		if (type == null) {
			return syntaxError("invalid field access", lval.getField());
		} else {
			return type;
		}
	}

	public Type checkDereferenceLVal(Expr.Dereference lval, Environment environment) {
		Type operandT = checkExpression(lval.getOperand(), environment);
		Type.Reference writeableReferenceT = checkIsReferenceType(operandT, AccessMode.WRITING, environment,
				lval.getOperand());
		//
		return writeableReferenceT.getElement();
	}

	// =========================================================================
	// Expressions
	// =========================================================================

	/**
	 * Type check a sequence of zero or more multi-expressions, assuming a given
	 * initial environment. A multi-expression is one which may have multiple return
	 * values. There are relatively few situations where this can arise, particular
	 * assignments and return statements. This returns a sequence of one or more
	 * pairs, each of which corresponds to a single return for a given expression.
	 * Thus, each expression generates one or more pairs in the result.
	 *
	 * @param expressions
	 * @param environment
	 */
	public List<Pair<Expr, Type>> checkMultiExpressions(Tuple<Expr> expressions, Environment environment) {
		ArrayList<Pair<Expr, Type>> rs = new ArrayList<>();
		for (Expr expression : expressions) {
			Tuple<Type> types = checkMultiExpression(expression, environment);
			for (int i = 0; i != types.size(); ++i) {
				rs.add(new Pair<>(expression, types.get(i)));
			}
		}
		return rs;
	}

	public Tuple<Type> checkMultiExpression(Expr expression, Environment environment) {
		switch (expression.getOpcode()) {
		case EXPR_invoke:
			return checkInvoke((Expr.Invoke) expression, environment);
		case EXPR_indirectinvoke:
			return checkIndirectInvoke((Expr.IndirectInvoke) expression, environment);
		default:
			Type type = checkExpression(expression, environment);
			return new Tuple<>(type);
		}
	}

	/**
	 * Type check a given expression assuming an initial environment.
	 *
	 * @param expression
	 * @param environment
	 * @return
	 * @throws ResolutionError
	 */
	public Type checkExpression(Expr expression, Environment environment) {
		Type type;

		switch (expression.getOpcode()) {
		case EXPR_constant:
			type = checkConstant((Expr.Constant) expression, environment);
			break;
		case EXPR_variablecopy:
			type = checkVariable((Expr.VariableAccess) expression, environment);
			break;
		case EXPR_staticvariable:
			type = checkStaticVariable((Expr.StaticVariableAccess) expression, environment);
			break;
		case EXPR_cast:
			type = checkCast((Expr.Cast) expression, environment);
			break;
		case EXPR_invoke: {
			Tuple<Type> types = checkInvoke((Expr.Invoke) expression, environment);
			// Deal with potential for multiple values
			if (types.size() == 0) {
				return syntaxError("no return value", expression);
			} else if (types.size() > 1) {
				return syntaxError("too many return values", expression);
			} else {
				return types.get(0);
			}
		}
		case EXPR_indirectinvoke: {
			Tuple<Type> types = checkIndirectInvoke((Expr.IndirectInvoke) expression, environment);
			// Deal with potential for multiple values
			if (types.size() == 0) {
				return syntaxError("no return value", expression);
			} else if (types.size() > 1) {
				return syntaxError("too many return values", expression);
			} else {
				type = types.get(0);
			}
			break;
		}
		// Conditions
		case EXPR_logicalnot:
		case EXPR_logicalor:
		case EXPR_logicaland:
		case EXPR_logicaliff:
		case EXPR_logiaclimplication:
		case EXPR_is:
		case EXPR_logicaluniversal:
		case EXPR_logicalexistential:
			checkCondition(expression, true, environment);
			return Type.Bool;
		// Comparators
		case EXPR_equal:
		case EXPR_notequal:
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
			return checkComparisonOperator((Expr.BinaryOperator) expression, environment);
		// Arithmetic Operators
		case EXPR_integernegation:
			type = checkIntegerOperator((Expr.UnaryOperator) expression, environment);
			break;
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
			type = checkIntegerOperator((Expr.BinaryOperator) expression, environment);
			break;
		// Bitwise expressions
		case EXPR_bitwisenot:
			type = checkBitwiseOperator((Expr.UnaryOperator) expression, environment);
			break;
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
			type = checkBitwiseOperator((Expr.NaryOperator) expression, environment);
			break;
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
			type = checkBitwiseShift((Expr.BinaryOperator) expression, environment);
			break;
		// Record Expressions
		case EXPR_recordinitialiser:
			type = checkRecordInitialiser((Expr.RecordInitialiser) expression, environment);
			break;
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			type = checkRecordAccess((Expr.RecordAccess) expression, environment);
			break;
		case EXPR_recordupdate:
			type = checkRecordUpdate((Expr.RecordUpdate) expression, environment);
			break;
		// Array expressions
		case EXPR_arraylength:
			type = checkArrayLength(environment, (Expr.ArrayLength) expression);
			break;
		case EXPR_arrayinitialiser:
			type = checkArrayInitialiser((Expr.ArrayInitialiser) expression, environment);
			break;
		case EXPR_arraygenerator:
			type = checkArrayGenerator((Expr.ArrayGenerator) expression, environment);
			break;
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			type = checkArrayAccess((Expr.ArrayAccess) expression, environment);
			break;
		case EXPR_arrayupdate:
			type = checkArrayUpdate((Expr.ArrayUpdate) expression, environment);
			break;
		// Reference expressions
		case EXPR_dereference:
			type = checkDereference((Expr.Dereference) expression, environment);
			break;
		case EXPR_new:
			type = checkNew((Expr.New) expression, environment);
			break;
		case EXPR_lambdaaccess:
			return checkLambdaAccess((Expr.LambdaAccess) expression, environment);
		case DECL_lambda:
			return checkLambdaDeclaration((Decl.Lambda) expression, environment);
		default:
			return internalFailure("unknown expression encountered (" + expression.getClass().getSimpleName() + ")",
					expression);
		}
		// Allocate and set type for expression
		expression.setType(expression.getHeap().allocate(type));
		return type;
	}

	/**
	 * Check the type of a given constant expression. This is straightforward since
	 * the determine is fully determined by the kind of constant we have.
	 *
	 * @param expr
	 * @return
	 */
	private Type checkConstant(Expr.Constant expr, Environment env) {
		Value item = expr.getValue();
		switch (item.getOpcode()) {
		case ITEM_null:
			return new Type.Null();
		case ITEM_bool:
			return new Type.Bool();
		case ITEM_int:
			return new Type.Int();
		case ITEM_byte:
			return new Type.Byte();
		case ITEM_utf8:
			// FIXME: this is not an optimal solution. The reason being that we
			// have lost nominal information regarding whether it is an instance
			// of std::ascii or std::utf8, for example.
			return new Type.Array(new Type.Int());
		default:
			return internalFailure("unknown constant encountered: " + expr, expr);
		}
	}

	/**
	 * Check the type of a given variable access. This is straightforward since the
	 * determine is fully determined by the declared type for the variable in
	 * question.
	 *
	 * @param expr
	 * @return
	 */
	private Type checkVariable(Expr.VariableAccess expr, Environment env) {
		Decl.Variable var = expr.getVariableDeclaration();
		// FIXME: this is where we need to do some serious work!!
		return env.getType(var);
	}

	private Type checkStaticVariable(Expr.StaticVariableAccess expr, Environment env) {
		try {
			// Resolve variable declaration being accessed
			Decl.StaticVariable decl = typeSystem.resolveExactly(expr.getName(), Decl.StaticVariable.class);
			//
			return decl.getType();
		} catch (ResolutionError e) {
			return syntaxError(errorMessage(RESOLUTION_ERROR, expr.getName().toString()), expr, e);
		}
	}

	private Type checkCast(Expr.Cast expr, Environment env) {
		Type rhsT = checkExpression(expr.getOperand(), env);
		checkIsSubtype(expr.getType(), rhsT, env, expr);
		return expr.getType();
	}

	private Tuple<Type> checkInvoke(Expr.Invoke expr, Environment env) {
		// Determine the argument types
		Tuple<Expr> arguments = expr.getOperands();
		Type[] types = new Type[arguments.size()];
		for (int i = 0; i != arguments.size(); ++i) {
			types[i] = checkExpression(arguments.get(i), env);
		}
		// Determine the declaration being invoked
		Binding binding = resolveAsCallable(expr.getName(), types, expr.getLifetimes(), env);
		// Assign descriptor to this expression
		expr.setSignature(expr.getHeap().allocate(binding.getCandidiateDeclaration().getType()));
		// Finally, return the declared returns/
		return binding.getConcreteType().getReturns();
	}

	private Tuple<Type> checkIndirectInvoke(Expr.IndirectInvoke expr, Environment environment) {
		// Determine signature type from source
		Type type = checkExpression(expr.getSource(), environment);
		Type.Callable sig = checkIsCallableType(type, environment, expr.getSource());
		// Determine the argument types
		Tuple<Expr> arguments = expr.getArguments();
		Tuple<Type> parameters = sig.getParameters();
		// Sanity check number of arguments provided
		if (parameters.size() != arguments.size()) {
			syntaxError("insufficient arguments for function or method invocation", expr);
		}
		// Sanity check types of arguments provided
		for (int i = 0; i != arguments.size(); ++i) {
			// Determine argument type
			Type arg = checkExpression(arguments.get(i), environment);
			// Check argument is subtype of parameter
			checkIsSubtype(parameters.get(i), arg, environment, arguments.get(i));
		}
		//
		return sig.getReturns();
	}

	private Type checkComparisonOperator(Expr.BinaryOperator expr, Environment environment) {
		switch (expr.getOpcode()) {
		case EXPR_equal:
		case EXPR_notequal:
			return checkEqualityOperator(expr, environment);
		default:
			return checkIntegerComparator(expr, environment);
		}
	}

	private Type checkEqualityOperator(Expr.BinaryOperator expr, Environment environment) {
		try {
			Type lhs = checkExpression(expr.getFirstOperand(), environment);
			Type rhs = checkExpression(expr.getSecondOperand(), environment);
			// Sanity check that the types of operands are actually comparable.
			SemanticType glb = toSemanticType(lhs).intersect(toSemanticType(rhs));
			if (typeSystem.isVoid(glb, environment)) {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhs, rhs), expr);
				return null;
			}
			return Type.Bool;
		} catch (ResolutionError e) {
			return syntaxError(e.getMessage(), expr);
		}
	}

	private Type checkIntegerComparator(Expr.BinaryOperator expr, Environment environment) {
		checkOperand(Type.Int, expr.getFirstOperand(), environment);
		checkOperand(Type.Int, expr.getSecondOperand(), environment);
		return Type.Bool;
	}

	private Type checkIntegerOperator(Expr.UnaryOperator expr, Environment environment) {
		checkOperand(Type.Int, expr.getOperand(), environment);
		return Type.Int;
	}

	/**
	 * Check the type for a given arithmetic operator. Such an operator has the type
	 * int, and all children should also produce values of type int.
	 *
	 * @param expr
	 * @return
	 */
	private Type checkIntegerOperator(Expr.BinaryOperator expr, Environment environment) {
		checkOperand(Type.Int, expr.getFirstOperand(), environment);
		checkOperand(Type.Int, expr.getSecondOperand(), environment);
		return Type.Int;
	}

	private Type checkBitwiseOperator(Expr.UnaryOperator expr, Environment environment) {
		checkOperand(Type.Byte, expr.getOperand(), environment);
		return Type.Byte;
	}

	private Type checkBitwiseOperator(Expr.NaryOperator expr, Environment environment) {
		checkOperands(Type.Byte, expr.getOperands(), environment);
		return Type.Byte;
	}

	private Type checkBitwiseShift(Expr.BinaryOperator expr, Environment environment) {
		checkOperand(Type.Byte, expr.getFirstOperand(), environment);
		checkOperand(Type.Int, expr.getSecondOperand(), environment);
		return Type.Byte;
	}

	private Type checkRecordAccess(Expr.RecordAccess expr, Environment env) {
		Type src = checkExpression(expr.getOperand(), env);
		Type.Record readableRecordT = checkIsRecordType(src, AccessMode.READING, env, expr.getOperand());
		//
		Type type = readableRecordT.getField(expr.getField());
		if (type == null) {
			return syntaxError("invalid field access", expr.getField());
		} else {
			return type;
		}
	}

	private Type checkRecordUpdate(Expr.RecordUpdate expr, Environment env) {
		Type src = checkExpression(expr.getFirstOperand(), env);
		Type val = checkExpression(expr.getSecondOperand(), env);
		Type.Record readableRecordT = checkIsRecordType(src, AccessMode.READING, env, expr.getFirstOperand());
		//
		Tuple<Decl.Variable> fields = readableRecordT.getFields();
		String actualFieldName = expr.getField().get();
		for (int i = 0; i != fields.size(); ++i) {
			Decl.Variable vd = fields.get(i);
			String declaredFieldName = vd.getName().get();
			if (declaredFieldName.equals(actualFieldName)) {
				// Matched the field type
				checkIsSubtype(vd.getType(), val, env, expr.getSecondOperand());
				return src;
			}
		}
		//
		return syntaxError("invalid field update", expr.getField());
	}

	private Type checkRecordInitialiser(Expr.RecordInitialiser expr, Environment env) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		Decl.Variable[] decls = new Decl.Variable[operands.size()];
		for (int i = 0; i != operands.size(); ++i) {
			Identifier field = fields.get(i);
			Type fieldType = checkExpression(operands.get(i), env);
			decls[i] = new Decl.Variable(new Tuple<>(), field, fieldType);
		}
		//
		return new Type.Record(false, new Tuple<>(decls));
	}

	private Type checkArrayLength(Environment env, Expr.ArrayLength expr) {
		Type src = checkExpression(expr.getOperand(), env);
		checkIsArrayType(src, AccessMode.READING, env, expr.getOperand());
		return new Type.Int();
	}

	private Type checkArrayInitialiser(Expr.ArrayInitialiser expr, Environment env) {
		Tuple<Expr> operands = expr.getOperands();
		Type[] ts = new Type[operands.size()];
		for (int i = 0; i != ts.length; ++i) {
			ts[i] = checkExpression(operands.get(i), env);
		}
		ts = ArrayUtils.removeDuplicates(ts);
		Type element = ts.length == 1 ? ts[0] : new Type.Union(ts);
		return new Type.Array(element);
	}

	private Type checkArrayGenerator(Expr.ArrayGenerator expr, Environment env) {
		Expr value = expr.getFirstOperand();
		Expr length = expr.getSecondOperand();
		//
		Type valueT = checkExpression(value, env);
		checkOperand(Type.Int, length, env);
		//
		return new Type.Array(valueT);
	}

	private Type checkArrayAccess(Expr.ArrayAccess expr, Environment env) {
		Expr source = expr.getFirstOperand();
		Expr subscript = expr.getSecondOperand();
		//
		Type sourceT = checkExpression(source, env);
		Type subscriptT = checkExpression(subscript, env);
		//
		Type.Array sourceArrayT = checkIsArrayType(sourceT, AccessMode.READING, env, source);
		checkIsSubtype(new Type.Int(), subscriptT, env, subscript);
		//
		return sourceArrayT.getElement();
	}

	private Type checkArrayUpdate(Expr.ArrayUpdate expr, Environment env) {
		Expr source = expr.getFirstOperand();
		Expr subscript = expr.getSecondOperand();
		Expr value = expr.getThirdOperand();
		//
		Type sourceT = checkExpression(source, env);
		Type subscriptT = checkExpression(subscript, env);
		Type valueT = checkExpression(value, env);
		//
		Type.Array sourceArrayT = checkIsArrayType(sourceT, AccessMode.READING, env, source);
		checkIsSubtype(new Type.Int(), subscriptT, env, subscript);
		checkIsSubtype(sourceArrayT.getElement(), valueT, env, value);
		return sourceArrayT;
	}

	private Type checkDereference(Expr.Dereference expr, Environment env) {
		Type operandT = checkExpression(expr.getOperand(), env);
		Type.Reference readableReferenceT = checkIsReferenceType(operandT, AccessMode.READING, env, expr.getOperand());
		//
		return readableReferenceT.getElement();
	}

	private Type checkNew(Expr.New expr, Environment env) {
		Type operandT = checkExpression(expr.getOperand(), env);
		//
		if (expr.hasLifetime()) {
			return new Type.Reference(operandT, expr.getLifetime());
		} else {
			return new Type.Reference(operandT);
		}
	}

	private Type checkLambdaAccess(Expr.LambdaAccess expr, Environment env) {
		Binding binding;
		Tuple<Type> types = expr.getParameterTypes();
		// FIXME: there is a problem here in that we cannot distinguish
		// between the case where no parameters were supplied and when
		// exactly zero arguments were supplied.
		if (types.size() > 0) {
			// Parameter types have been given, so use them to help resolve
			// declaration.
			binding = resolveAsCallable(expr.getName(), types.toArray(Type.class), new Tuple<Identifier>(), env);
		} else {
			// No parameters we're given, therefore attempt to resolve
			// uniquely.
			binding = resolveAsCallable(expr.getName(), expr);
		}
		// Set descriptor for this expression
		expr.setSignature(expr.getHeap().allocate(binding.getCandidiateDeclaration().getType()));
		//
		return binding.getConcreteType();
	}

	private Type checkLambdaDeclaration(Decl.Lambda expr, Environment env) {
		Tuple<Decl.Variable> parameters = expr.getParameters();
		checkNonEmpty(parameters, env);
		Tuple<Type> parameterTypes = parameters.project(2, Type.class);
		Type result = checkExpression(expr.getBody(), env);
		// Determine whether or not this is a pure or impure lambda.
		Type.Callable signature;
		if (isPure(expr.getBody())) {
			// This is a pure lambda, hence it has function type.
			signature = new Type.Function(parameterTypes, new Tuple<>(result));
		} else {
			// This is an impure lambda, hence it has method type.
			signature = new Type.Method(parameterTypes, new Tuple<>(result), expr.getCapturedLifetimes(), expr.getLifetimes());
		}
		// Update with inferred signature
		expr.setType(expr.getHeap().allocate(signature));
		// Done
		return signature;
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
	private boolean isPure(SyntacticItem item) {
		// Examine expression to determine whether this expression is impure.
		if (item instanceof Expr.StaticVariableAccess || item instanceof Expr.Dereference || item instanceof Expr.New) {
			return false;
		} else if (item instanceof Expr.Invoke) {
			Expr.Invoke e = (Expr.Invoke) item;
			if (e.getSignature() instanceof Decl.Method) {
				// This expression is definitely not pure
				return false;
			}
		} else if (item instanceof Expr.IndirectInvoke) {
			Expr.IndirectInvoke e = (Expr.IndirectInvoke) item;
			// FIXME: need to do something here.
			throw new RuntimeException("implement me");
		}
		// Recursively examine any subexpressions. The uniform nature of
		// syntactic items makes this relatively easy.
		boolean result = true;
		//
		for (int i = 0; i != item.size(); ++i) {
			result &= isPure(item.get(i));
		}
		return result;
	}

	/**
	 * The access mode is used to determine whether we are extracting a type in a
	 * read or write position.
	 *
	 * @author David J. Peare
	 *
	 */
	private enum AccessMode {
		READING, WRITING
	}

	/**
	 * Check whether a given type is an array type of some sort.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	private Type.Array checkIsArrayType(Type type, AccessMode mode, LifetimeRelation lifetimes, SyntacticItem element) {
		try {
			Type.Array arrT;
			if (mode == AccessMode.READING) {
				arrT = typeSystem.extractReadableArray(type, lifetimes);
			} else {
				arrT = typeSystem.extractWriteableArray(type, lifetimes);
			}
			if (arrT == null) {
				syntaxError("expected array type", element);
			}
			return arrT;
		} catch (NameResolver.ResolutionError e) {
			return syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	/**
	 * Check whether a given type is a record type of some sort.
	 *
	 * @param type
	 * @return
	 */
	private Type.Record checkIsRecordType(Type type, AccessMode mode, LifetimeRelation lifetimes,
			SyntacticItem element) {
		try {
			Type.Record recT;
			if (mode == AccessMode.READING) {
				recT = typeSystem.extractReadableRecord(type, lifetimes);
			} else {
				recT = typeSystem.extractWriteableRecord(type, lifetimes);
			}
			if (recT == null) {
				syntaxError("expected record type", element);
			}
			return recT;
		} catch (NameResolver.ResolutionError e) {
			return syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	/**
	 * Check whether a given type is a reference type of some sort.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	private Type.Reference checkIsReferenceType(Type type, AccessMode mode, LifetimeRelation lifetimes,
			SyntacticItem element) {
		try {
			Type.Reference refT;
			if (mode == AccessMode.READING) {
				refT = typeSystem.extractReadableReference(type, lifetimes);
			} else {
				refT = typeSystem.extractWriteableReference(type, lifetimes);
			}
			if (refT == null) {
				syntaxError("expected reference type", element);
			}
			return refT;
		} catch (NameResolver.ResolutionError e) {
			return syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	/**
	 * Attempt to determine the declared function or macro to which a given
	 * invocation refers, without any additional type information. For this to
	 * succeed, there can be only one candidate for consideration.
	 *
	 * @param name
	 * @param args
	 * @return
	 */
	private Binding resolveAsCallable(Name name, SyntacticItem context) {
		try {
			// Identify all function or macro declarations which should be
			// considered
			List<Decl.FunctionOrMethod> candidates = typeSystem.resolveAll(name, Decl.FunctionOrMethod.class);
			if (candidates.isEmpty()) {
				return syntaxError(errorMessage(RESOLUTION_ERROR, name.toString()), context);
			} else if (candidates.size() > 1) {
				return syntaxError(errorMessage(AMBIGUOUS_RESOLUTION, foundCandidatesString(candidates)), context);
			} else {
				Decl.FunctionOrMethod candidate = candidates.get(0);
				return new Binding(candidate,candidate.getType());
			}
		} catch (ResolutionError e) {
			return syntaxError(e.getMessage(), context);
		}
	}

	/**
	 * Attempt to determine the declared function or macro to which a given
	 * invocation refers. To resolve this requires considering the name, along with
	 * the argument types as well.
	 *
	 * @param name
	 * @param arguments
	 *            Inferred Argument Types
	 * @param lifetimeArguments
	 *            Explicit lifetime arguments (if provided)
	 * @param lifetimes
	 *            Within relationship beteween declared lifetimes
	 *
	 * @return
	 */
	private Binding resolveAsCallable(Name name, Type[] arguments, Tuple<Identifier> lifetimeArguments, LifetimeRelation lifetimes) {
		try {
			// Identify all function or macro declarations which should be
			// considered
			List<Decl.Callable> candidates = typeSystem.resolveAll(name, Decl.Callable.class);
			// Bind candidate types to given argument types which, in particular, will
			// produce bindings for lifetime variables
			List<Binding> bindings = bindCallableCandidates(candidates, arguments, lifetimeArguments, lifetimes);
			// Sanity check bindings generated
			if (bindings.isEmpty()) {
				return syntaxError("unable to resolve name (no match for " + name + parameterString(arguments) + ")"
						+ foundCandidatesString(candidates), name);
			}
			// Select the most precise signature from the candidate bindings
			Binding selected = selectCallableCandidate(name, bindings, lifetimes, arguments);
			// Sanity check result
			if (selected == null) {
				return syntaxError(errorMessage(AMBIGUOUS_RESOLUTION, foundBindingsString(bindings)), name);
			}
			return selected;
		} catch (ResolutionError e) {
			return syntaxError(e.getMessage(), name);
		}
	}

	/**
	 * <p>
	 * Give a list of candidate declarations, go through and determine which (if
	 * any) can be bound to the given type arguments. There are two aspects to this:
	 * firstly, we must consider all possible lifetime instantiations; secondly, any
	 * binding must produce a type for which each argument is applicable. The
	 * following illustrates a simple example:
	 * </p>
	 *
	 * <pre>
	 * function f() -> (int r):
	 *    return 0
	 *
	 * function f(int x) -> (int r):
	 *    return x
	 *
	 * function g(int x) -> (int r):
	 *    return g(x)
	 * </pre>
	 * <p>
	 * For the above example, name resolution will identify both declarations for
	 * <code>f</code> as candidates. However, this method will produce only one
	 * "binding", namely that corresponding to the second declaration. This is
	 * because the first declaration is not applicable to the given arguments.
	 * </p>
	 * <p>
	 * The presence of lifetime parameters makes this process more complex. To
	 * understand why, consider this scenario:
	 * </p>
	 *
	 * <pre>
	 * method <a,b> f(&a:int p, &a:int q, &b:int r) -> (&b:int r):
	 *    return r
	 *
	 * method g():
	 *    &this:int x = new 1
	 *    &this:int y = new 2
	 *    &this:int z = new 3
	 *    f(x,y,z)
	 *    ...
	 * </pre>
	 * <p>
	 * For the invocation of <code>f(x,y,z)</code> we initially have only one
	 * candidates, namely <code>method<a,b>(&a:int,&a:int,&b:int)</code>. Observe
	 * that, by itself, this is not immediately applicable. Specifically,
	 * <code>&this:int</code> is not a subtype of <code>&a:int</code>. Instead, we
	 * must determine the binding <code>a->this,b->this</code>.
	 * </p>
	 * <p>
	 * Unfortunately, things are yet more complicated as we must be able to
	 * <i>generalise bindings</i>. Consider this alternative implementation of
	 * <code>g()</code>:
	 * </p>
	 *
	 * <pre>
	 * method <l> g(&l:int p) -> (&l:int r):
	 *    &this:int q = new 1
	 *    return f(p,q,p)
	 * </pre>
	 * <p>
	 * In this case, there are at least two possible bindings for the invocation,
	 * namely: <code>{a->this,b->l}</code> and <code>{a->l,b->l}</code>. We can
	 * safely discount e.g. <code>{a->this,b->this}</code> as <code>b->this</code>
	 * never occurs in practice and, indeed, failure to discount this would prevent
	 * the method from type checking.
	 * </p>
	 *
	 * @param candidates
	 * @param arguments
	 *            Inferred Argument Types
	 * @param lifetimeArguments
	 *            Explicit lifetime arguments (if provided)
	 * @param lifetimes
	 *            Within relationship beteween declared lifetimes
	 * @return
	 */
	private List<Binding> bindCallableCandidates(List<Decl.Callable> candidates, Type[] arguments,
			Tuple<Identifier> lifetimeArguments, LifetimeRelation lifetimes) {
		ArrayList<Binding> bindings = new ArrayList<>();
		for (int i = 0; i != candidates.size(); ++i) {
			Decl.Callable candidate = candidates.get(i);
			Type.Callable type = candidate.getType();
			// Generate all potential bindings based on arguments
			if(candidate instanceof Decl.Method) {
				// Complex case where lifetimes must be considered
				generateApplicableBindings((Decl.Method) candidate, bindings, arguments, lifetimeArguments, lifetimes);
			} else if(isApplicable(type,lifetimes,arguments)){
				// Easier case where lifetimes are not considered and, hence, we can avoid the
				// complex binding procedure.
				bindings.add(new Binding(candidate,type));
			}
		}
		// Done
		return bindings;
	}

	private void generateApplicableBindings(Decl.Method candidate, List<Binding> bindings,
			Type[] arguments, Tuple<Identifier> lifetimeArguments, LifetimeRelation lifetimes) {
		Type.Method type = candidate.getType();
		Tuple<Identifier> lifetimeParameters = type.getLifetimeParameters();
		Tuple<Type> parameters = type.getParameters();
		//
		if (parameters.size() != arguments.length
				|| (lifetimeArguments.size() > 0 && lifetimeArguments.size() != lifetimeParameters.size())) {
			// Differing number of parameters / arguments. Since we don't
			// support variable-length argument lists (yet), there is nothing
			// more to consider.
			return;
		} else if(lifetimeParameters.size() == 0 || lifetimeArguments.size() > 0) {
			// In this case, either the method accepts no lifetime parameters, or explicit
			// lifetime parameters were given. Eitherway, we can avoid all the machinery for
			// guessing appropriate bindings.
			Type.Method concreteType = substitute(type, lifetimeArguments);
			if(isApplicable(concreteType,lifetimes,arguments)){
				bindings.add(new Binding(candidate,concreteType));
			}
		} else {
			// Extract all lifetimes used in the type arguments
			Identifier[] lifetimeOccurences = extractLifetimes(arguments);
			// Generate all lifetime permutations for substitution
			for (Map<Identifier, Identifier> binding : generatePermutations(lifetimeParameters, lifetimeOccurences)) {
				Type.Method substitution = substitute(type,binding);
				if (isApplicable(substitution, lifetimes, arguments)) {
					bindings.add(new Binding(candidate,substitution,binding));
				}
			}
			// Done
		}
	}

	/**
	 * Apply an explicit binding to a given method via substituteion.
	 * @param method
	 * @param lifetimeArguments
	 * @return
	 */
	private Type.Method substitute(Type.Method type, Tuple<Identifier> lifetimeArguments) {
		Tuple<Identifier> lifetimeParameters = type.getLifetimeParameters();
		HashMap<Identifier, Identifier> binding = new HashMap<>();
		//
		for (int i = 0; i != lifetimeArguments.size(); ++i) {
			Identifier parameter = lifetimeParameters.get(i);
			Identifier argument = lifetimeArguments.get(i);
			binding.put(parameter, argument);
		}
		//
		return substitute(type, binding);
	}

	/**
	 * Apply a given binding to a given method via substitution. Observe that we
	 * cannot use Type.substitute directly for this, since it will not allow the
	 * declared lifetimes to be captured.
	 *
	 * @param method
	 * @param binding
	 * @return
	 */
	private Type.Method substitute(Type.Method method, Map<Identifier,Identifier> binding) {
		// Proceed with the potentially updated binding
		Tuple<Type> parameters = WhileyFile.substitute(method.getParameters(), binding);
		Tuple<Type> returns = WhileyFile.substitute(method.getReturns(), binding);
		return new Type.Method(parameters, returns, method.getCapturedLifetimes(), new Tuple<>());
	}

	/**
	 * Generate an iterator over all possible mappings from lifetimeParameters to
	 * lifetimes. For example, suppose we have <code>(a,b)</code> for
	 * lifetimeParameters and <code>*,this,l</code> for lifetimes. Then, we generate
	 * the following iteration space:
	 * <pre>
	 * { a => *,    b => * }
	 * { a => this, b => * }
	 * { a => l,    b => * }
	 * { a => *,    b => this }
	 * { a => this, b => this }
	 * { a => l,    b => this }
	 * { a => *,    b => l }
	 * { a => this, b => l }
	 * { a => l,    b => l }
	 * </pre>
	 *
	 * @param lifetimeParameters
	 * @param lifetimes
	 * @return
	 */
	private Iterable<Map<Identifier, Identifier>> generatePermutations(Tuple<Identifier> lifetimeParameters,
			Identifier[] lifetimes) {
		// The following hashmap will store each binding as its generated
		HashMap<Identifier, Identifier> binding = new HashMap<>();
		// Construct an iterator over the permutation space
		return new Iterable<Map<Identifier, Identifier>>() {
			private int[] counters = new int[lifetimeParameters.size()];

			@Override
			public Iterator<Map<Identifier, Identifier>> iterator() {
				return new Iterator<Map<Identifier, Identifier>>() {

					@Override
					public boolean hasNext() {
						return counters != null;
					}

					@Override
					public Map<Identifier, Identifier> next() {
						// First, assign current state to binding
						for (int i = 0; i != counters.length; ++i) {
							Identifier lifetimeParameter = lifetimeParameters.get(i);
							binding.put(lifetimeParameter, lifetimes[counters[i]]);
						}
						// Increment counts;
						incrementCounters(lifetimes.length);
						// Done
						return binding;
					}
				};
			}

			private void incrementCounters(int max) {
				for (int i = 0; i != counters.length; ++i) {
					counters[i] = (counters[i] + 1) % max;
					if (counters[i] != 0) {
						return;
					}
				}
				counters = null;
			}
		};
	}

	/**
	 * Extract the set of all lifetimes used in any of the type arguments or
	 * component thereof.
	 *
	 * @param args
	 * @return
	 */
	private Identifier[] extractLifetimes(Type... args) {
		final HashSet<Identifier> lifetimes = new HashSet<>();
		// Construct the type visitor
		AbstractVisitor visitor = new AbstractVisitor() {
			@Override
			public void visitReference(Type.Reference ref) {
				lifetimes.add(ref.getLifetime());
			}
		};
		// Apply visitor to each argument
		for (int i = 0; i != args.length; ++i) {
			visitor.visitType(args[i]);
		}
		// Done
		return lifetimes.toArray(new Identifier[lifetimes.size()]);
	}

	private static class Binding {
		private final HashMap<Identifier,Identifier> binding;
		private final Decl.Callable candidate;
		private final Type.Callable concreteType;

		public Binding(Decl.Callable candidate, Type.Callable concreteType) {
			this.candidate = candidate;
			this.concreteType = concreteType;
			this.binding = null;
		}

		public Binding(Decl.Callable candidate, Type.Method concreteType, Map<Identifier,Identifier> binding) {
			this.candidate = candidate;
			this.concreteType = concreteType;
			this.binding = new HashMap<>(binding);
		}

		public Decl.Callable getCandidiateDeclaration() {
			return candidate;
		}

		public Type.Callable getConcreteType() {
			return concreteType;
		}

		public Map<Identifier,Identifier> getBinding() {
			return binding;
		}
	}

	/**
	 * Determine whether a given function or method declaration is applicable to a
	 * given set of argument types. If there number of arguments differs, it's
	 * definitely not applicable. Otherwise, we need every argument type to be a
	 * subtype of its corresponding parameter type.
	 *
	 * @param candidate
	 * @param args
	 * @return
	 */
	private boolean isApplicable(Type.Callable candidate, LifetimeRelation lifetimes, Type... args) {
		Tuple<Type> parameters = candidate.getParameters();
		if (parameters.size() != args.length) {
			// Differing number of parameters / arguments. Since we don't
			// support variable-length argument lists (yet), there is nothing
			// more to consider.
			return false;
		} else {
			try {
				// Number of parameters matches number of arguments. Now, check that
				// each argument is a subtype of its corresponding parameter.
				for (int i = 0; i != args.length; ++i) {
					SemanticType param = toSemanticType(parameters.get(i));
					if (!typeSystem.isRawCoerciveSubtype(param, toSemanticType(args[i]), lifetimes)) {
						return false;
					}
				}
				//
				return true;
			} catch (NameResolver.ResolutionError e) {
				return syntaxError(e.getMessage(), e.getName(), e);
			}
		}
	}

	/**
	 * Given a list of candidate function or method declarations, determine the most
	 * precise match for the supplied argument types. The given argument types must
	 * be applicable to this function or macro declaration, and it must be a subtype
	 * of all other applicable candidates.
	 *
	 * @param candidates
	 * @param args
	 * @return
	 */
	private Binding selectCallableCandidate(Name name, List<Binding> candidates, LifetimeRelation lifetimes,
			Type... args) {
		Binding best = null;
		Type.Callable bestType = null;
		boolean bestValidWinner = false;
		//
		for (int i = 0; i != candidates.size(); ++i) {
			Binding candidate = candidates.get(i);
			Type.Callable candidateType = candidate.getConcreteType();
			// Check whether the given candidate is a real candidate or not. A
			// if (isApplicable(candidate, lifetimes, args)) {
			// Yes, this candidate is applicable.
			if (best == null) {
				// No other candidates are applicable so far. Hence, this
				// one is automatically promoted to the best seen so far.
				best = candidate;
				bestType = candidate.getConcreteType();
				bestValidWinner = true;
			} else {
				boolean csubb = isSubtype(bestType, candidateType, lifetimes);
				boolean bsubc = isSubtype(candidateType, bestType, lifetimes);
				//
				if (csubb && !bsubc) {
					// This candidate is a subtype of the best seen so far. Hence, it is now the
					// best seen so far.
					best = candidate;
					bestType = candidate.getConcreteType();
					bestValidWinner = true;
				} else if (bsubc && !csubb) {
					// This best so far is a subtype of this candidate. Therefore, we can simply
					// discard this candidate from consideration since it's definitely not the best.
				} else if(!csubb && !bsubc){
					// This is the awkward case. Neither the best so far, nor the candidate, are
					// subtypes of each other. In this case, we report an error. NOTE: must perform
					// an explicit equality check above due to the present of type invariants.
					// Specifically, without this check, the system will treat two declarations with
					// identical raw types (though non-identical actual types) as the same.
					return null;
				} else {
					// This is a tricky case. We have two types after instantiation which are
					// considered identical under the raw subtype test. As such, they may not be
					// actually identical (e.g. if one has a type invariant). Furthermore, we cannot
					// stop at this stage as, in principle, we could still find an outright winner.
					bestValidWinner = false;
				}
			}
		}
		return bestValidWinner ? best : null;
	}

	private String parameterString(Type... paramTypes) {
		String paramStr = "(";
		boolean firstTime = true;
		if (paramTypes == null) {
			paramStr += "...";
		} else {
			for (Type t : paramTypes) {
				if (!firstTime) {
					paramStr += ",";
				}
				firstTime = false;
				paramStr += t;
			}
		}
		return paramStr + ")";
	}

	private String foundCandidatesString(Collection<? extends Decl.Callable> candidates) {
		ArrayList<String> candidateStrings = new ArrayList<>();
		for (Decl.Callable c : candidates) {
			candidateStrings.add(candidateString(c,null));
		}
		Collections.sort(candidateStrings); // make error message deterministic!
		StringBuilder msg = new StringBuilder();
		for (String s : candidateStrings) {
			msg.append("\n\tfound ");
			msg.append(s);
		}
		return msg.toString();
	}

	private String foundBindingsString(Collection<? extends Binding> candidates) {
		ArrayList<String> candidateStrings = new ArrayList<>();
		for (Binding b : candidates) {
			Decl.Callable c = b.getCandidiateDeclaration();
			candidateStrings.add(candidateString(c,b.getBinding()));
		}
		Collections.sort(candidateStrings); // make error message deterministic!
		StringBuilder msg = new StringBuilder();
		for (String s : candidateStrings) {
			msg.append("\n\tfound ");
			msg.append(s);
		}
		return msg.toString();
	}

	private String candidateString(Decl.Callable decl, Map<Identifier, Identifier> binding) {
		String r;
		if (decl instanceof Decl.Method) {
			r = "method ";
		} else if (decl instanceof Decl.Function) {
			r = "function ";
		} else {
			r = "property ";
		}
		Type.Callable type = decl.getType();
		return r + decl.getQualifiedName() + bindingString(decl,binding) + type.getParameters() + "->" + type.getReturns();
	}

	private String bindingString(Decl.Callable decl, Map<Identifier,Identifier> binding) {
		if(binding != null && decl instanceof Decl.Method) {
			Decl.Method method = (Decl.Method) decl;
			String r = "<";

			Tuple<Identifier> lifetimes = method.getLifetimes();
			for(int i=0;i!=lifetimes.size();++i) {
				Identifier lifetime = lifetimes.get(i);
				if(i != 0) {
					r += ",";
				}
				r = r + lifetime + "=" + binding.get(lifetime);
			}
			return r + ">";
		} else {
			return "";
		}
	}

	/**
	 * Check whether the type signature for a given function or method declaration
	 * is a super type of a given child declaration.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private boolean isSubtype(Type.Callable lhs, Type.Callable rhs, LifetimeRelation lifetimes) {
		Tuple<Type> parentParams = lhs.getParameters();
		Tuple<Type> childParams = rhs.getParameters();
		if (parentParams.size() != childParams.size()) {
			// Differing number of parameters / arguments. Since we don't
			// support variable-length argument lists (yet), there is nothing
			// more to consider.
			return false;
		}
		try {
			// Number of parameters matches number of arguments. Now, check that
			// each argument is a subtype of its corresponding parameter.
			for (int i = 0; i != parentParams.size(); ++i) {
				SemanticType parentParam = toSemanticType(parentParams.get(i));
				SemanticType childParam = toSemanticType(childParams.get(i));
				if (!typeSystem.isRawCoerciveSubtype(parentParam, childParam, lifetimes)) {
					return false;
				}
			}
			//
			return true;
		} catch (NameResolver.ResolutionError e) {
			return syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	/**
	 * Check whether a given type is a callable type of some sort.
	 *
	 * @param type
	 * @return
	 */
	private Type.Callable checkIsCallableType(Type type, LifetimeRelation lifetimes, SyntacticItem element) {
		try {
			Type.Callable refT = typeSystem.extractReadableLambda(type, lifetimes);
			if (refT == null) {
				syntaxError("expected lambda type", element);
			}
			return refT;
		} catch (NameResolver.ResolutionError e) {
			return syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	private void checkOperand(Type type, Expr operand, Environment environment) {
		checkIsSubtype(type, checkExpression(operand, environment), environment, operand);
	}

	private void checkOperands(Type type, Tuple<Expr> operands, Environment environment) {
		for (int i = 0; i != operands.size(); ++i) {
			Expr operand = operands.get(i);
			checkOperand(type, operand, environment);
		}
	}
	// ==========================================================================
	// Helpers
	// ==========================================================================

	public SemanticType toSemanticType(Type type) {
		return typeSystem.toSemanticType(type);
	}

	private void checkIsSubtype(Type lhs, Type rhs, LifetimeRelation lifetimes, SyntacticItem element) {
		try {
			if (!typeSystem.isRawCoerciveSubtype(toSemanticType(lhs), toSemanticType(rhs), lifetimes)) {
				syntaxError(errorMessage(SUBTYPE_ERROR, lhs, rhs), element);
			}
		} catch (NameResolver.ResolutionError e) {
			syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	private void checkContractive(Decl.Type d) {
		try {
			if (!typeSystem.isContractive(d.getQualifiedName().toNameID(), d.getType())) {
				syntaxError("empty type encountered", d.getName());
			}
		} catch (NameResolver.ResolutionError e) {
			syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	/**
	 * Check a given set of variable declarations are not "empty". That is, their
	 * declared type is not equivalent to void.
	 *
	 * @param decls
	 */
	private void checkNonEmpty(Tuple<Decl.Variable> decls, LifetimeRelation lifetimes) {
		for (int i = 0; i != decls.size(); ++i) {
			checkNonEmpty(decls.get(i), lifetimes);
		}
	}

	/**
	 * Check that a given variable declaration is not empty. That is, the declared
	 * type is not equivalent to void. This is an important sanity check.
	 *
	 * @param d
	 */
	private void checkNonEmpty(Decl.Variable d, LifetimeRelation lifetimes) {
		try {
			Type type = d.getType();
			if (typeSystem.isVoid(toSemanticType(type), lifetimes)) {
				syntaxError("empty type encountered", type);
			}
		} catch (NameResolver.ResolutionError e) {
			syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	private <T> T syntaxError(String msg, SyntacticItem e) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntaxError(msg, cu.getEntry(), e);
	}

	private <T> T syntaxError(String msg, SyntacticItem e, Throwable ex) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntaxError(msg, cu.getEntry(), e, ex);
	}

	private <T> T internalFailure(String msg, SyntacticItem e) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new InternalFailure(msg, cu.getEntry(), e);
	}

	private <T> T internalFailure(String msg, SyntacticItem e, Throwable ex) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new InternalFailure(msg, cu.getEntry(), e, ex);
	}

	private final Environment BOTTOM = new Environment();

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
				Tuple<Identifier> lifetimes = meth.getLifetimes();
				String[] arr = new String[lifetimes.size() + 1];
				for (int i = 0; i != lifetimes.size(); ++i) {
					arr[i] = lifetimes.get(i).get();
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

	/**
	 * Provides a very simple typing environment which defaults to using the
	 * declared type for a variable (this is the "null" case). However, the
	 * environment can also be updated to override the declared type with a new type
	 * as appropriate.
	 *
	 * @author David J. Pearce
	 *
	 */
	public class Environment implements LifetimeRelation {
		private final Map<Decl.Variable, SemanticType> refinements;
		private final Map<String, String[]> withins;

		public Environment() {
			this.refinements = new HashMap<>();
			this.withins = new HashMap<>();
		}

		public Environment(Map<Decl.Variable, SemanticType> refinements, Map<String, String[]> withins) {
			this.refinements = new HashMap<>(refinements);
			this.withins = new HashMap<>(withins);
		}

		public SemanticType getType(Decl.Variable var) {
			SemanticType refined = refinements.get(var);
			if (refined == null) {
				// Lazily populate the environment
				refined = typeSystem.toSemanticType(var.getType());
				refinements.put(var, refined);
			}
			return refined;
		}

		public Environment setType(Decl.Variable var, SemanticType refinement) {
			Environment r = new Environment(this.refinements, this.withins);
			r.refinements.put(var, refinement);
			return r;
		}

		public Environment refineType(Decl.Variable var, SemanticType refinement, boolean sign) {
			SemanticType type = getType(var);
			if(sign) {
				refinement = refinement.intersect(type);
			} else {
				refinement = refinement.subtract(type);
			}
			Environment r = new Environment(this.refinements, this.withins);
			r.refinements.put(var, refinement);
			return r;
		}

		public Set<Decl.Variable> getRefinedVariables() {
			return refinements.keySet();
		}

		@Override
		public String toString() {
			String r = "{";
			boolean firstTime = true;
			for (Decl.Variable var : refinements.keySet()) {
				if (!firstTime) {
					r += ", ";
				}
				firstTime = false;
				r += var.getName() + "->" + getType(var);
			}
			return r + "}";
		}

		@Override
		public boolean isWithin(String inner, String outer) {
			//
			if (outer.equals("*") || inner.equals(outer)) {
				// Cover easy cases first
				return true;
			} else {
				String[] outers = withins.get(inner);
				return outers != null && (ArrayUtils.firstIndexOf(outers, outer) >= 0);
			}
		}

		public Environment declareWithin(String inner, Tuple<Identifier> outers) {
			String[] outs = new String[outers.size()];
			for (int i = 0; i != outs.length; ++i) {
				outs[i] = outers.get(i).get();
			}
			return declareWithin(inner, outs);
		}

		public Environment declareWithin(String inner, String... outers) {
			Environment nenv = new Environment(refinements, withins);
			nenv.withins.put(inner, outers);
			return nenv;
		}
	}
}
