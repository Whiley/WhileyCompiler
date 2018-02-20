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
import java.util.function.BiFunction;
import java.util.function.Function;

import wybs.lang.*;
import wybs.lang.NameResolver.ResolutionError;
import wybs.util.*;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.check.FlowTypeUtils.Environment;
import wyc.lang.*;
import wyc.util.AbstractVisitor;
import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.subtyping.RelaxedTypeEmptinessTest;
import wyil.type.subtyping.StrictTypeEmptinessTest;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.util.AbstractTypeFilter;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Type;
import wyc.lang.WhileyFile.Decl.Variable;
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
	private final NameResolver resolver;
	private final SubtypeOperator relaxedSubtypeOperator;
	private final SubtypeOperator strictSubtypeOperator;

	public FlowTypeCheck(CompileTask builder) {
		this.builder = builder;
		this.resolver = builder.getNameResolver();
		this.relaxedSubtypeOperator = new SubtypeOperator(resolver,
				new RelaxedTypeEmptinessTest(resolver));
		this.strictSubtypeOperator = new SubtypeOperator(resolver,
				new StrictTypeEmptinessTest(resolver));
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
			SemanticType type = checkExpression(decl.getInitialiser(), environment, decl.getType());
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
		environment = FlowTypeUtils.declareThisWithin(d, environment);
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
			if (environment == FlowTypeUtils.BOTTOM) {
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
		return FlowTypeUtils.BOTTOM;
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
			checkExpression(decl.getInitialiser(), environment, decl.getType());
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
		Type[] types = new Type[lvals.size()];
		for (int i = 0; i != lvals.size(); ++i) {
			types[i] = checkLVal(lvals.get(i), environment);
		}
		checkMultiExpressions(stmt.getRightHandSide(), environment, new Tuple<>(types));
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
		return FlowTypeUtils.BOTTOM;
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
		return FlowTypeUtils.BOTTOM;
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
		// FIXME: want to refine integer type here
		Type std_ascii = new Type.Array(Type.Int);
		checkExpression(stmt.getOperand(), environment, std_ascii);
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
		return FlowTypeUtils.union(trueEnvironment, falseEnvironment);
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
		// Determine the set of return types for the enclosing function or
		// method. This then allows us to check the given operands are
		// appropriate subtypes.
		Decl.FunctionOrMethod fm = scope.getEnclosingScope(FunctionOrMethodScope.class).getDeclaration();
		Tuple<Type> types = fm.getReturns().project(2, Type.class);
		// Type check the operands for the return statement (if any)
		checkMultiExpressions(stmt.getReturns(), environment, types);
		// Return bottom as following environment to signal that control-flow
		// cannot continue here. Thus, any following statements will encounter
		// the BOTTOM environment and, hence, report an appropriate error.
		return FlowTypeUtils.BOTTOM;
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
		checkExpression(stmt.getCondition(), environment, Type.Any);
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
				checkExpression(e, environment, Type.Any);
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
			checkExpression(condition, environment, Type.Bool);
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
		try {
			Expr lhs = expr.getOperand();
			SemanticType lhsT = checkExpression(expr.getOperand(), environment, Type.Any);
			SemanticType rhsT = expr.getTestType();
			// Sanity check operands for this type test
			SemanticType trueBranchRefinementT = new SemanticType.Intersection(lhsT, rhsT);
			SemanticType falseBranchRefinementT = new SemanticType.Difference(lhsT, rhsT);
			//
			// NOTE: it's a little unclear to me whether use the strict subtype operator
			// here makes sense in the long run. However, using the relaxed subtype operator
			// definitely results in problems!  See #845
			if (strictSubtypeOperator.isVoid(trueBranchRefinementT, environment)) {
				// DEFINITE TRUE CASE
				syntaxError(errorMessage(BRANCH_ALWAYS_TAKEN), expr);
			} else if (strictSubtypeOperator.isVoid(falseBranchRefinementT, environment)) {
				// DEFINITE FALSE CASE
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsT, rhsT), expr);
			}
			//
			Pair<Decl.Variable, Type> extraction = FlowTypeUtils.extractTypeTest(lhs, expr.getTestType());
			if (extraction != null) {
				Decl.Variable var = extraction.getFirst();
				SemanticType varT = environment.getType(var);
				SemanticType refinementT = extraction.getSecond();
				if (sign) {
					refinementT = new Type.Intersection(varT, refinementT);
				} else {
					refinementT = new SemanticType.Difference(varT, refinementT);
				}
				// Update the typing environment accordingly.
				environment = environment.refineType(var, refinementT);
			}
			//
			return environment;
		} catch (ResolutionError e) {
			return syntaxError(e.getMessage(), expr);
		}
	}

	private Environment checkQuantifier(Expr.Quantifier stmt, boolean sign, Environment env) {
		checkNonEmpty(stmt.getParameters(), env);
		// NOTE: We throw away the returned environment from the body. This is
		// because any type tests within the body are ignored outside.
		checkCondition(stmt.getOperand(), true, env);
		return env;
	}

	// =========================================================================
	// LVals
	// =========================================================================

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
			Decl.StaticVariable decl = resolver.resolveExactly(lval.getName(), Decl.StaticVariable.class);
			return decl.getType();
		} catch (ResolutionError e) {
			return syntaxError(errorMessage(RESOLUTION_ERROR, lval.getName().toString()), lval, e);
		}
	}

	public Type checkArrayLVal(Expr.ArrayAccess lval, Environment environment) {
		Type srcT = checkLVal((LVal) lval.getFirstOperand(), environment);
		// NOTE: the following cast is safe because, given a Type, we cannot extract a
		// SemanticType. Furthermore, since we know the result is an instanceof
		// SemanticType.Array, it follows that it must be an instance of Type.Array (or
		// null).
		Type.Array arrT = (Type.Array) FlowTypeUtils.typeArrayExtractor(srcT, environment, strictSubtypeOperator, resolver);
		if(arrT == null) {
			return syntaxError("expected array type", lval);
		} else {
			checkExpression(lval.getSecondOperand(), environment, Type.Int);
			//
			return arrT.getElement();
		}
	}

	public Type checkRecordLVal(Expr.RecordAccess lval, Environment environment) {
		Type src = checkLVal((LVal) lval.getOperand(), environment);
		// NOTE: the following cast is safe because, given a Type, we cannot extract a
		// SemanticType. Furthermore, since we know the result is an instanceof
		// SemanticType.Record, it follows that it must be an instance of Type.Record
		// (or null).
		Type.Record recT = (Type.Record) FlowTypeUtils.typeRecordExtractor(src, environment, strictSubtypeOperator,
				resolver);
		if (recT == null) {
			return syntaxError("expected record type", lval);
		} else {
			return recT.getField(lval.getField());
		}
	}

	public Type checkDereferenceLVal(Expr.Dereference lval, Environment environment) {
		Type srcT = checkLVal((LVal) lval.getOperand(), environment);
		// NOTE: the following cast is safe because, given a Type, we cannot extract a
		// SemanticType. Furthermore, since we know the result is an instance of
		// SemanticType.Reference, it follows that it must be an instance of
		// Type.Reference (or null).
		Type.Reference refT = (Type.Reference) FlowTypeUtils.typeReferenceExtractor(srcT, environment, strictSubtypeOperator,
				resolver);
		if(refT == null) {
			return syntaxError("expected reference type", lval);
		} else {
			return refT.getElement();
		}
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
	public final void checkMultiExpressions(Tuple<Expr> expressions, Environment environment, Tuple<Type> expected) {
		for (int i=0,j=0;i!=expressions.size();++i) {
			Expr expression = expressions.get(i);
			switch (expression.getOpcode()) {
			case EXPR_invoke: {
				Tuple<Type> results = checkInvoke((Expr.Invoke) expression, environment);
				// FIXME: THIS LOOP IS UGLY
				for(int k=0;k!=results.size();++k) {
					checkIsSubtype(new Type[] {expected.get(j+k)},results.get(k),environment,expression);
				}
				j = j + results.size();
				break;
			}
			case EXPR_indirectinvoke: {
				Tuple<Type> results = checkIndirectInvoke((Expr.IndirectInvoke) expression, environment);
				// FIXME: THIS LOOP IS UGLY
				for(int k=0;k!=results.size();++k) {
					checkIsSubtype(new Type[] {expected.get(j+k)},results.get(k),environment,expression);
				}
				j = j + results.size();
				break;
			}
			default:
				if ((expected.size()-j) < 1) {
					syntaxError("too many return values", expression);
				} else if ((i+1) == expressions.size() && (expected.size()-j) > 1) {
					syntaxError("too few return values", expression);
				}
				checkExpression(expression, environment, expected.get(j));
				j = j + 1;
			}
		}
	}

	private void checkExpressions(Tuple<Expr> operands, Environment environment, Type... expected) {
		for (int i = 0; i != operands.size(); ++i) {
			Expr operand = operands.get(i);
			checkExpression(operand, environment, expected);
		}
	}

	/**
	 * Type check a given expression assuming an initial environment.
	 *
	 * @param expression
	 *            The expression to be checked.
	 * @param target
	 *            The target type of this expression.
	 * @param expected
	 *            The (concrete) type this expression will be assigned to.
	 * @param environment
	 *            The environment in which this expression is to be typed
	 * @return
	 * @throws ResolutionError
	 */
	public SemanticType checkExpression(Expr expression, Environment environment, Type... expected) {
		SemanticType type;

		switch (expression.getOpcode()) {
		case EXPR_constant:
			type = checkConstant((Expr.Constant) expression, environment, expected);
			break;
		case EXPR_variablecopy:
			type = checkVariable((Expr.VariableAccess) expression, environment, expected);
			break;
		case EXPR_staticvariable:
			type = checkStaticVariable((Expr.StaticVariableAccess) expression, environment, expected);
			break;
		case EXPR_cast:
			type = checkCast((Expr.Cast) expression, environment, expected);
			break;
		case EXPR_invoke: {
			Tuple<Type> types = checkInvoke((Expr.Invoke) expression, environment);
			// Sanity check
			switch(types.size()) {
			case 0:
				syntaxError("too many return values", expression);
			case 1:
				break;
			default:
				syntaxError("too few return values", expression);
			}
			return types.get(0);
		}
		case EXPR_indirectinvoke: {
			Tuple<Type> types = checkIndirectInvoke((Expr.IndirectInvoke) expression, environment);
			// Sanity check
			switch(types.size()) {
			case 0:
				syntaxError("too many return values", expression);
			case 1:
				break;
			default:
				syntaxError("too few return values", expression);
			}
			return types.get(0);
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
			return checkComparisonOperator((Expr.BinaryOperator) expression, environment, expected);
		// Arithmetic Operators
		case EXPR_integernegation:
			type = checkIntegerOperator((Expr.UnaryOperator) expression, environment, expected);
			break;
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
			type = checkIntegerOperator((Expr.BinaryOperator) expression, environment, expected);
			break;
		// Bitwise expressions
		case EXPR_bitwisenot:
			type = checkBitwiseOperator((Expr.UnaryOperator) expression, environment, expected);
			break;
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
			type = checkBitwiseOperator((Expr.NaryOperator) expression, environment, expected);
			break;
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
			type = checkBitwiseShift((Expr.BinaryOperator) expression, environment, expected);
			break;
		// Record Expressions
		case EXPR_recordinitialiser:
			type = checkRecordInitialiser((Expr.RecordInitialiser) expression, environment, expected);
			break;
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			type = checkRecordAccess((Expr.RecordAccess) expression, environment, expected);
			break;
		case EXPR_recordupdate:
			type = checkRecordUpdate((Expr.RecordUpdate) expression, environment, expected);
			break;
		// Array expressions
		case EXPR_arraylength:
			type = checkArrayLength((Expr.ArrayLength) expression, environment, expected);
			break;
		case EXPR_arrayinitialiser:
			type = checkArrayInitialiser((Expr.ArrayInitialiser) expression, environment, expected);
			break;
		case EXPR_arraygenerator:
			type = checkArrayGenerator((Expr.ArrayGenerator) expression, environment, expected);
			break;
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			type = checkArrayAccess((Expr.ArrayAccess) expression, environment, expected);
			break;
		case EXPR_arrayupdate:
			type = checkArrayUpdate((Expr.ArrayUpdate) expression, environment, expected);
			break;
		// Reference expressions
		case EXPR_dereference:
			type = checkDereference((Expr.Dereference) expression, environment, expected);
			break;
		case EXPR_new:
			type = checkNew((Expr.New) expression, environment, expected);
			break;
		case EXPR_lambdaaccess:
			return checkLambdaAccess((Expr.LambdaAccess) expression, environment, expected);
		case DECL_lambda:
			return checkLambdaDeclaration((Decl.Lambda) expression, environment, expected);
		default:
			return internalFailure("unknown expression encountered (" + expression.getClass().getSimpleName() + ")",
					expression);
		}
		// Allocate and set type for expression
		Type concreteType = determineConcreteType(expected, type, environment, expression);
		expression.setType(expression.getHeap().allocate(concreteType));
		return type;
	}

	public Tuple<Type>[] toTupleTypes(Type[] expected) {
		Tuple<Type>[] tupleTypes = new Tuple[expected.length];
		for (int i = 0; i != expected.length; ++i) {
			tupleTypes[i] = new Tuple<>(expected[i]);
		}
		return tupleTypes;
	}

	public Type determineConcreteType(Type[] expected, SemanticType actual, Environment environment,
			SyntacticItem element) {
		try {
			Type least = null;
			for (int i = 0; i != expected.length; ++i) {
				Type candidate = expected[i];
				if (relaxedSubtypeOperator.isSubtype(candidate, actual, environment)) {
					if (least == null || relaxedSubtypeOperator.isSubtype(least, candidate, environment)) {
						least = candidate;
					} else if (relaxedSubtypeOperator.isSubtype(candidate, least, environment)) {
						// ignore,
					} else {
						// Ambiguous coercion
						syntaxError("ambiguous coercion required", element);
					}
				}
			}
			if (least == null) {
				return syntaxError("invalid coercion required", element);
			} else {
				return least;
			}
		} catch (NameResolver.ResolutionError e) {
			return syntaxError(e.getMessage(), element);
		}
	}

	/**
	 * Check the type of a given constant expression. This is straightforward since
	 * the determine is fully determined by the kind of constant we have.
	 *
	 * @param expr
	 * @return
	 */
	private SemanticType checkConstant(Expr.Constant expr, Environment env, Type... expected) {
		Value item = expr.getValue();
		switch (item.getOpcode()) {
		case ITEM_null:
			return Type.Null;
		case ITEM_bool:
			return Type.Bool;
		case ITEM_int:
			return Type.Int;
		case ITEM_byte:
			return Type.Byte;
		case ITEM_utf8:
			// FIXME: this is not an optimal solution. The reason being that we
			// have lost nominal information regarding whether it is an instance
			// of std::ascii or std::utf8, for example.
			return new SemanticType.Array(Type.Int);
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
	private SemanticType checkVariable(Expr.VariableAccess expr, Environment environment, Type... expected) {
		Decl.Variable var = expr.getVariableDeclaration();
		checkIsSubtype(expected, environment.getType(var), environment, expr); // FIXME: needs refinement
		return environment.getType(var);
	}

	private SemanticType checkStaticVariable(Expr.StaticVariableAccess expr, Environment env, Type... expected) {
		try {
			// Resolve variable declaration being accessed
			Decl.StaticVariable decl = resolver.resolveExactly(expr.getName(), Decl.StaticVariable.class);
			checkIsSubtype(expected, decl.getType(), env, expr);
			//
			return decl.getType();
		} catch (ResolutionError e) {
			return syntaxError(errorMessage(RESOLUTION_ERROR, expr.getName().toString()), expr, e);
		}
	}

	private SemanticType checkCast(Expr.Cast expr, Environment env, Type... expected) {
		checkIsSubtype(expected, expr.getType(), env, expr);
		SemanticType rhsT = checkExpression(expr.getOperand(), env, expr.getType());
		return expr.getType();
	}

	private Tuple<Type> checkInvoke(Expr.Invoke expr, Environment environment) {
		try {
			// Determine the argument types
			Tuple<Expr> arguments = expr.getOperands();
			List<Decl.Callable> candidates = resolver.resolveAll(expr.getName(), Decl.Callable.class);
			filterCandidateTypesByLength(candidates,arguments.size());
			// Filter candidates based on what we find
			for (int j = 0; j != arguments.size(); ++j) {
				Type[] parameterCandidates = extractParameterTypeCandidates(candidates, j);
				SemanticType type = checkExpression(arguments.get(j), environment, parameterCandidates); // FIXME
				filterCandidateTypesByParameter(candidates, j, type, environment);
			}
			if(candidates.isEmpty()) {
				return syntaxError(errorMessage(RESOLUTION_ERROR, expr.getName().toString()), expr.getName());
			} else if (candidates.size() > 1) {
				return syntaxError(errorMessage(AMBIGUOUS_RESOLUTION, foundCandidatesString(candidates)), expr.getName());
			}
			// FIXME: need to bind lifetime parameters somehow
			Type.Callable selected = candidates.get(0).getType();
			// Assign descriptor to this expression
			expr.setSignature(expr.getHeap().allocate(selected));
			// Finally, return the declared returns/
			return selected.getReturns();
		} catch (ResolutionError e) {
			return syntaxError(errorMessage(RESOLUTION_ERROR, expr.getName().toString()), expr, e);
		}
	}

	private Type[] extractParameterTypeCandidates(List<Decl.Callable> candidates, int argument) {
		Type[] types = new Type[candidates.size()];
		for(int i=0;i!=types.length;++i) {
			types[i] = candidates.get(i).getType().getParameters().get(argument);
		}
		return types;
	}

	private void filterCandidateTypesByLength(List<Decl.Callable> candidates, int length) {
		for (int i = 0; i != candidates.size(); ++i) {
			Type.Callable candidate = candidates.get(i).getType();
			if(candidate.getParameters().size() != length) {
				// This candidate is no longer applicable because it doesn't accept the right
				// number of parameter types.
				candidates.remove(i);
				i = i - 1;
			}
		}
	}

	private void filterCandidateTypesByParameter(List<Decl.Callable> candidates, int argument, SemanticType actual,
			Environment environment) throws ResolutionError {
		for (int i = 0; i != candidates.size(); ++i) {
			Type.Callable candidate = candidates.get(i).getType();
			Type parameter = candidate.getParameters().get(argument);
			if (!relaxedSubtypeOperator.isSubtype(parameter, actual, environment)) {
				// This candidate is no longer applicable for whatever reason, therefore remove
				// it from the list being considered.
				candidates.remove(i);
				i = i - 1;
			}
		}
	}

	private Tuple<Type> checkIndirectInvoke(Expr.IndirectInvoke expr, Environment environment) {
		// Determine signature type from source
		SemanticType type = checkExpression(expr.getSource(), environment, Type.Any);
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
			SemanticType arg = checkExpression(arguments.get(i), environment, parameters.get(i));
			// Check argument is subtype of parameter
			checkIsSubtype(parameters.get(i), arg, environment, arguments.get(i));
		}
		//
		return sig.getReturns();
	}

	private SemanticType checkComparisonOperator(Expr.BinaryOperator expr, Environment environment, Type... expected) {
		switch (expr.getOpcode()) {
		case EXPR_equal:
		case EXPR_notequal:
			return checkEqualityOperator(expr, environment, expected);
		default:
			return checkIntegerComparator(expr, environment, expected);
		}
	}

	private SemanticType checkEqualityOperator(Expr.BinaryOperator expr, Environment environment, Type... expected) {
		//try {
		checkIsSubtype(expected,Type.Bool,environment,expr);
		SemanticType lhs = checkExpression(expr.getFirstOperand(), environment, Type.Any);
		SemanticType rhs = checkExpression(expr.getSecondOperand(), environment, Type.Any);
			// Sanity check that the types of operands are actually comparable.
// FIXME: restore this
//			SemanticType glb = toSemanticType(lhs).intersect(toSemanticType(rhs));
//			if (typeSystem.isVoid(glb, environment)) {
//				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhs, rhs), expr);
//				return null;
//			}
			return Type.Bool;
//		} catch (ResolutionError e) {
//			return syntaxError(e.getMessage(), expr);
//		}
	}

	private SemanticType checkIntegerComparator(Expr.BinaryOperator expr, Environment environment, Type... expected) {
		checkIsSubtype(expected,Type.Bool,environment,expr);
		checkExpression(expr.getFirstOperand(), environment, Type.Int);
		checkExpression(expr.getSecondOperand(), environment, Type.Int);
		return Type.Bool;
	}

	private SemanticType checkIntegerOperator(Expr.UnaryOperator expr, Environment environment, Type... expected) {
		checkIsSubtype(expected,Type.Int,environment,expr);
		checkExpression(expr.getOperand(), environment, expected);
		return Type.Int;
	}

	/**
	 * Check the type for a given arithmetic operator. Such an operator has the type
	 * int, and all children should also produce values of type int.
	 *
	 * @param expr
	 * @return
	 */
	private SemanticType checkIntegerOperator(Expr.BinaryOperator expr, Environment environment, Type... expected) {
		checkIsSubtype(expected,Type.Int,environment,expr);
		checkExpression(expr.getFirstOperand(), environment, expected);
		checkExpression(expr.getSecondOperand(), environment, expected);
		return Type.Int;
	}

	private SemanticType checkBitwiseOperator(Expr.UnaryOperator expr, Environment environment, Type... expected) {
		checkIsSubtype(expected,Type.Byte,environment,expr);
		checkExpression(expr.getOperand(), environment, expected);
		return Type.Byte;
	}

	private SemanticType checkBitwiseOperator(Expr.NaryOperator expr, Environment environment, Type... expected) {
		checkIsSubtype(expected,Type.Byte,environment,expr);
		checkExpressions(expr.getOperands(), environment, expected);
		return Type.Byte;
	}

	private SemanticType checkBitwiseShift(Expr.BinaryOperator expr, Environment environment, Type... expected) {
		checkIsSubtype(expected,Type.Byte,environment,expr);
		checkExpression(expr.getFirstOperand(), environment, expected);
		checkExpression(expr.getSecondOperand(), environment, Type.Int);
		return Type.Byte;
	}

	private SemanticType checkRecordAccess(Expr.RecordAccess expr, Environment env, Type... expected) {
		// Determine set of expected record types as best possible
		Type.Record[] expectedRecords = FlowTypeUtils.typeRecordConstructor(expr.getField(),expected);
		// Check expression against expected record types
		SemanticType src = checkExpression(expr.getOperand(), env, expectedRecords);
		// NOTE: following safe because expression check ensures src has field
		return FlowTypeUtils.typeRecordExtractor(src, env, strictSubtypeOperator, resolver).getField(expr.getField());
	}

	private SemanticType checkRecordUpdate(Expr.RecordUpdate expr, Environment environment, Type... expected) {
		// Filter out all non-record types as these are bogus.
		Type.Record[] expectedRecordTypes = FlowTypeUtils.typeRecordFilter(expected, resolver);
		// Sanity check there is something left
		if(expectedRecordTypes.length == 0) {
			return syntaxError("expected record type", expr);
		}
		// Construct expected field types
		Type[] expectedFieldTypes = FlowTypeUtils.typeRecordFieldConstructor(expectedRecordTypes,expr.getField());
		// Check src and value expressions
		SemanticType src = checkExpression(expr.getFirstOperand(), environment, expected);
		SemanticType val = checkExpression(expr.getSecondOperand(), environment, expectedFieldTypes);
		SemanticType.Record readableRecordT = FlowTypeUtils.typeRecordExtractor(src, environment, strictSubtypeOperator, resolver);
		//
		String actualFieldName = expr.getField().get();
		Tuple<? extends SemanticType.Field> fields = readableRecordT.getFields();
		for (int i = 0; i != fields.size(); ++i) {
			SemanticType.Field vd = fields.get(i);
			String declaredFieldName = vd.getName().get();
			if (declaredFieldName.equals(actualFieldName)) {
				// Matched the field type
				checkIsSubtype(vd.getType(), val, environment, expr.getSecondOperand());
				return src;
			}
		}
		//
		return syntaxError("invalid field update", expr.getField());
	}

	private SemanticType checkRecordInitialiser(Expr.RecordInitialiser expr, Environment environment, Type... expected) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		// Filter out all non-record expected types as these are bogus.
		Type.Record[] records = FlowTypeUtils.typeRecordFilter(expected, resolver);
		// Filter out all expected record types which don't match
		records = FlowTypeUtils.typeRecordFieldFilter(records, fields);
		// Sanity check there is something left
		if(records.length == 0) {
			return syntaxError("expected record type", expr);
		}
		//
		SemanticType.Field[] decls = new SemanticType.Field[operands.size()];
		// Check field initialiser expressions one by one
		for (int i = 0; i != operands.size(); ++i) {
			Identifier field = fields.get(i);
			Type[] expectedFieldType = FlowTypeUtils.typeRecordFieldConstructor(records,field);
			if(expectedFieldType == null) {
				syntaxError("field not used", field);
			}
			SemanticType fieldType = checkExpression(operands.get(i), environment, expectedFieldType);
			decls[i] = new SemanticType.Field(field, fieldType);
		}
		//
		return new SemanticType.Record(false, new Tuple<>(decls));
	}

	private SemanticType checkArrayLength(Expr.ArrayLength expr, Environment environment, Type... expected) {
		Type.Array arr = new Type.Array(Type.Any);
		SemanticType src = checkExpression(expr.getOperand(), environment, arr);
		checkIsSubtype(expected, Type.Int, environment, expr);
		return Type.Int;
	}

	private SemanticType checkArrayInitialiser(Expr.ArrayInitialiser expr, Environment environment, Type... expected) {
		// Filter out all non-array expected types as these are bogus.
		Type.Array[] expectedArrayTypes = FlowTypeUtils.typeArrayFilter(expected, resolver);
		// Sanity check there is something left
		if(expectedArrayTypes.length == 0) {
			return syntaxError("expected array type", expr);
		}
		// Construct expected element types
		Type[] expectedElementTypes = FlowTypeUtils.typeArrayElementConstructor(expectedArrayTypes);
		// Check initialiser expressions
		Tuple<Expr> operands = expr.getOperands();
		SemanticType[] ts = new SemanticType[operands.size()];
		for (int i = 0; i != ts.length; ++i) {
			ts[i] = checkExpression(operands.get(i), environment, expectedElementTypes);
		}
		ts = ArrayUtils.removeDuplicates(ts);
		SemanticType element;
		switch(ts.length) {
		case 0:
			element = Type.Void;
			break;
		case 1:
			element = ts[0];
			break;
		default: {
			// TODO: update SemanticType.Union to hold multiple elements
			element = ts[0];
			for(int i=1;i!=ts.length;++i) {
				element = new SemanticType.Union(element, ts[i]);
			}
		}
		}
		return new SemanticType.Array(element);
	}

	private SemanticType checkArrayGenerator(Expr.ArrayGenerator expr, Environment environment, Type... expected) {
		// Filter out all non-array expected types as these are bogus.
		Type.Array[] expectedArrayTypes = FlowTypeUtils.typeArrayFilter(expected, resolver);
		// Sanity check there is something left
		if(expectedArrayTypes.length == 0) {
			return syntaxError("expected array type", expr);
		}
		// Construct expected element types
		Type[] expectedElementTypes = FlowTypeUtils.typeArrayElementConstructor(expectedArrayTypes);
		// Check generation operands
		Expr value = expr.getFirstOperand();
		Expr length = expr.getSecondOperand();
		//
		SemanticType valueT = checkExpression(value, environment, expectedElementTypes);
		checkExpression(length, environment, Type.Int);
		//
		return new SemanticType.Array(valueT);
	}

	private SemanticType checkArrayAccess(Expr.ArrayAccess expr, Environment environment, Type... expected) {
		Type.Array[] expectedArrays = FlowTypeUtils.typeArrayConstructor(expected);
		Expr source = expr.getFirstOperand();
		Expr subscript = expr.getSecondOperand();
		//
		SemanticType sourceT = checkExpression(source, environment, expectedArrays);
		SemanticType subscriptT = checkExpression(subscript, environment, Type.Int);
		//
		return FlowTypeUtils.typeArrayExtractor(sourceT, environment, strictSubtypeOperator, resolver).getElement();
	}

	private SemanticType checkArrayUpdate(Expr.ArrayUpdate expr, Environment environment, Type... expected) {
		Expr source = expr.getFirstOperand();
		Expr subscript = expr.getSecondOperand();
		Expr value = expr.getThirdOperand();
		// Filter out all non-array expected types as these are bogus.
		Type.Array[] expectedArrayTypes = FlowTypeUtils.typeArrayFilter(expected, resolver);
		// Sanity check there is something left
		if(expectedArrayTypes.length == 0) {
			return syntaxError("expected array type", expr);
		}
		// Construct expected element types
		Type[] expectedElementTypes = FlowTypeUtils.typeArrayElementConstructor(expectedArrayTypes);
		// Check operand expressions
		SemanticType sourceT = checkExpression(source, environment, expected);
		checkExpression(subscript, environment, Type.Int);
		checkExpression(value, environment, expectedElementTypes);
		// Extract the determined array type
		return FlowTypeUtils.typeArrayExtractor(sourceT, environment, strictSubtypeOperator, resolver);
	}

	private SemanticType checkDereference(Expr.Dereference expr, Environment environment, Type... expected) {
		// For references, we do not further propagate the set of expected types. This
		// may seem strange but it makes logical sense. Consider this scenario:
		//
		// method f(&i8 ptr) -> (i16 r):
		// return *ptr
		//
		// Where do we want the coercion from i18=>i16 to occur? We cannot coerce the
		// referent of ptr as this does not make any logical sense (i.e. because its
		// representation is invariant). Therefore, we must coerce after the dereference
		// and this is the effect that ignoring expected types at this point has.
		SemanticType operandT = checkExpression(expr.getOperand(), environment, Type.Any);
		// Extract an appropriate reference type form the source.
		SemanticType.Reference readableReferenceT = FlowTypeUtils.typeReferenceExtractor(operandT, environment,
				strictSubtypeOperator, resolver);
		// Since we ignored the expected types before, we must now check that we were
		// able to successfully extract a reference type.
		if(readableReferenceT == null) {
			return syntaxError("expected reference type", expr);
		}
		//
		return readableReferenceT.getElement();
	}

	private SemanticType checkNew(Expr.New expr, Environment environment, Type... expected) {
		Type.Reference[] expectedReferences = FlowTypeUtils.typeReferenceFilter(expected, resolver);
		// Sanity check there is something left
		if (expectedReferences.length == 0) {
			return syntaxError("expected reference type", expr);
		}
		// Determine set of expected element types
		Type[] expectedElementTypes = FlowTypeUtils.typeReferenceElementConstructor(expectedReferences);
		// Check expression type against expected element types
		SemanticType operandT = checkExpression(expr.getOperand(), environment, expectedElementTypes);
		//
		if (expr.hasLifetime()) {
			return new SemanticType.Reference(operandT, expr.getLifetime());
		} else {
			return new SemanticType.Reference(operandT);
		}
	}

	private SemanticType checkLambdaAccess(Expr.LambdaAccess expr, Environment env, Type... expected) {
		Binding binding;
		Tuple<Type> types = expr.getParameterTypes();
		// FIXME: there is a problem here in that we cannot distinguish
		// between the case where no parameters were supplied and when
		// exactly zero arguments were supplied.
		if (types.size() > 0) {
			// Parameter types have been given, so use them to help resolve
			// declaration.
			binding = resolveAsCallable(expr.getName(), types, new Tuple<Identifier>(), env);
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

	private SemanticType checkLambdaDeclaration(Decl.Lambda expr, Environment env, Type... expected) {
		Tuple<Decl.Variable> parameters = expr.getParameters();
		Tuple<Type> parameterTypes = parameters.project(2, Type.class);
		// Filter out all non-record types as these are bogus.
		Type.Callable[] expectedCallableTypes = FlowTypeUtils.typeLambdaFilter(expected, parameterTypes, resolver);
		// Sanity check there is something left
		if(expectedCallableTypes.length == 0) {
			return syntaxError("expected function or method type", expr);
		}
		// Project out all return values from expected callable types
		Type[] expectedReturnTypes = FlowTypeUtils.typeLambdaReturnConstructor(expectedCallableTypes);
		// Sanity check no parameter is "empty", that is equivalent to void.
		checkNonEmpty(parameters, env);
		// Type check the body of the lambda using the expected return types
		SemanticType result = checkExpression(expr.getBody(), env, expectedReturnTypes);
		// Determine whether or not this is a pure or impure lambda.
		Type.Callable signature;
		if(!(result instanceof Type)) {
			// NOTE: this case is difficult, but not impossible, to reach. Specifically,
			// since the body is an expression, flow typing would appear not to be in play.
			// However, it's possible that a variable from the enclosing scope has been
			// refined, leading to a potential problem.
			return syntaxError("cannot determine concrete return type",expr);
		} else if (FlowTypeUtils.isPure(expr.getBody())) {
			// This is a pure lambda, hence it has function type.
			signature = new Type.Function(parameterTypes, new Tuple<>((Type) result));
		} else {
			// This is an impure lambda, hence it has method type.
			signature = new Type.Method(parameterTypes, new Tuple<>((Type) result), expr.getCapturedLifetimes(),
					expr.getLifetimes());
		}
		// Update lambda declaration with inferred signature.
		expr.setType(expr.getHeap().allocate(signature));
		// Done
		return signature;
	}

	// ===========================================================================================
	// Reference Helpers
	// ===========================================================================================

	/**
	 * Check whether a given type is a callable type of some sort.
	 *
	 * @param type
	 * @return
	 */
	private Type.Callable checkIsCallableType(SemanticType type, LifetimeRelation lifetimes, SyntacticItem element) {
		// FIXME: this prohibits effective callable types
		Type.Callable t = type.asCallable(resolver);
		if (t != null) {
			return t;
		} else {
			return syntaxError("expected lambda type", element);
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
			List<Decl.FunctionOrMethod> candidates = resolver.resolveAll(name, Decl.FunctionOrMethod.class);
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
	private Binding resolveAsCallable(Name name, Tuple<Type> arguments, Tuple<Identifier> lifetimeArguments, LifetimeRelation lifetimes) {
		try {
			// Identify all function or macro declarations which should be
			// considered
			List<Decl.Callable> candidates = resolver.resolveAll(name, Decl.Callable.class);
			// Bind candidate types to given argument types which, in particular, will
			// produce bindings for lifetime variables
			List<Binding> bindings = bindCallableCandidates(candidates, arguments, lifetimeArguments, lifetimes);
			// Sanity check bindings generated
			if (bindings.isEmpty()) {
				return syntaxError("unable to resolve name (no match for " + name + parameterString(arguments) + ")"
						+ foundCandidatesString(candidates), name);
			}
			// Select the most precise signature from the candidate bindings
			Binding selected = selectCallableCandidate(name, bindings, lifetimes);
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
	private List<Binding> bindCallableCandidates(List<Decl.Callable> candidates, Tuple<Type> arguments,
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
			Tuple<Type> arguments, Tuple<Identifier> lifetimeArguments, LifetimeRelation lifetimes) {
		Type.Method type = candidate.getType();
		Tuple<Identifier> lifetimeParameters = type.getLifetimeParameters();
		Tuple<Type> parameters = type.getParameters();
		//
		if (parameters.size() != arguments.size()
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
	private Identifier[] extractLifetimes(Tuple<Type> args) {
		final HashSet<Identifier> lifetimes = new HashSet<>();
		// Construct the type visitor
		AbstractVisitor visitor = new AbstractVisitor() {
			@Override
			public void visitTypeReference(Type.Reference ref) {
				super.visitTypeReference(ref);
				lifetimes.add(ref.getLifetime());
			}

			@Override
			public void visitSemanticTypeReference(SemanticType.Reference ref) {
				super.visitSemanticTypeReference(ref);
				lifetimes.add(ref.getLifetime());
			}
		};
		// Apply visitor to each argument
		for (int i = 0; i != args.size(); ++i) {
			visitor.visitSemanticType(args.get(i));
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
	private boolean isApplicable(Type.Callable candidate, LifetimeRelation lifetimes, Tuple<Type> args) {
		Tuple<Type> parameters = candidate.getParameters();
		if (parameters.size() != args.size()) {
			// Differing number of parameters / arguments. Since we don't
			// support variable-length argument lists (yet), there is nothing
			// more to consider.
			return false;
		} else {
			try {
				// Number of parameters matches number of arguments. Now, check that
				// each argument is a subtype of its corresponding parameter.
				for (int i = 0; i != args.size(); ++i) {
					SemanticType param = parameters.get(i);
					if (!relaxedSubtypeOperator.isSubtype(param, args.get(i), lifetimes)) {
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
	private Binding selectCallableCandidate(Name name, List<Binding> candidates, LifetimeRelation lifetimes) {
		Binding best = null;
		Type.Callable bestType = null;
		boolean bestValidWinner = false;
		//
		for (int i = 0; i != candidates.size(); ++i) {
			Binding candidate = candidates.get(i);
			Type.Callable candidateType = candidate.getConcreteType();
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

	private String parameterString(Tuple<Type> paramTypes) {
		String paramStr = "(";
		boolean firstTime = true;
		if (paramTypes == null) {
			paramStr += "...";
		} else {
			for (SemanticType t : paramTypes) {
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
				SemanticType parentParam = parentParams.get(i);
				SemanticType childParam = childParams.get(i);
				if (!relaxedSubtypeOperator.isSubtype(parentParam, childParam, lifetimes)) {
					return false;
				}
			}
			//
			return true;
		} catch (NameResolver.ResolutionError e) {
			return syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	// ==========================================================================
	// Helpers
	// ==========================================================================

	private void checkIsSubtype(Type[] lhs, SemanticType rhs, LifetimeRelation lifetimes, SyntacticItem element) {
		// FIXME: perhaps could optimise this by having Type-oriented subtype operator
		// to avoid conversion to SemanticType.
		for(int i=0;i!=lhs.length;++i) {
			try {
				if (relaxedSubtypeOperator.isSubtype(lhs[i], rhs, lifetimes)) {
					return;
				}
			} catch (NameResolver.ResolutionError e) {
				syntaxError(e.getMessage(), e.getName(), e);
			}
		}
		String str = "";
		for(int i=0;i!=lhs.length;++i) {
			if(i != 0) {
				str += " or ";
			}
			str += lhs[i];
		}
		syntaxError(errorMessage(SUBTYPE_ERROR, str, rhs), element);
	}

	private void checkIsSubtype(SemanticType lhs, SemanticType rhs, LifetimeRelation lifetimes, SyntacticItem element) {
		try {
			if (!relaxedSubtypeOperator.isSubtype(lhs,rhs, lifetimes)) {
				syntaxError(errorMessage(SUBTYPE_ERROR, lhs, rhs), element);
			}
		} catch (NameResolver.ResolutionError e) {
			syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	private void checkContractive(Decl.Type d) {
		try {
			if (!relaxedSubtypeOperator.isContractive(d.getQualifiedName().toNameID(), d.getType())) {
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
			// FIXME: conversion to semantic type seems unnecessary here?
			if (relaxedSubtypeOperator.isVoid(d.getType(), lifetimes)) {
				syntaxError("empty type encountered", d.getType());
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

}
