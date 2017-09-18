// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.stage;

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
import wyc.type.TypeSystem;
import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyc.lang.WhileyFile;
import wyc.task.CompileTask;

import static wyc.lang.WhileyFile.*;
import static wyc.util.ErrorMessages.*;

/**
 * checks type information in a <i>flow-sensitive</i> fashion from declared
 * parameter and return types through variable declarations and assigned
 * expressions, to determine types for all intermediate expressions and
 * variables. During this propagation, type checking is performed to ensure
 * types are used soundly. For example:
 *
 * <pre>
 * function sum(int[] data) -> int:
 *     int r = 0      // declared int type for r
 *     for v in data: // infers int type for v, based on type of data
 *         r = r + v  // infers int type for r + v, based on type of operands
 *     return r       // infers int type for return expression
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
 * function id(int x) -> int:
 *    return x
 *
 * function f(int y) -> int:
 *    int|null x = y
 *    f(x)
 * </pre>
 *
 * <p>
 * The above example is considered type safe because the known type of
 * <code>x</code> at the function call is <code>int</code>, which differs from
 * its declared type (i.e. <code>int|null</code>).
 * </p>
 *
 * <p>
 * Loops present an interesting challenge for type propagation. Consider this
 * example:
 * </p>
 *
 * <pre>
 * function loopy(int max) -> real:
 *     var i = 0
 *     while i < max:
 *         i = i + 0.5
 *     return i
 * </pre>
 *
 * <p>
 * On the first pass through the loop, variable <code>i</code> is inferred to
 * have type <code>int</code> (based on the type of the constant <code>0</code>
 * ). However, the add expression is inferred to have type <code>real</code>
 * (based on the type of the rhs) and, hence, the resulting type inferred for
 * <code>i</code> is <code>real</code>. At this point, the loop must be
 * reconsidered taking into account this updated type for <code>i</code>.
 * </p>
 *
 * <p>
 * The operation of the flow type checker splits into two stages:
 * </p>
 * <ul>
 * <li><b>Global Propagation.</b> During this stage, all named types are checked
 * and expanded.</li>
 * <li><b>Local Propagation.</b> During this stage, types are checkd through
 * statements and expressions (as above).</li>
 * </ul>
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
		if (decl instanceof Decl.StaticVariable) {
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
		checkNonEmpty(decl.getVariableDeclaration());
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
	public void checkStaticVariableDeclaration(Decl.Variable decl) {
		Environment environment = new Environment();
		// Check the initialiser
		checkExpression(decl.getInitialiser(), environment);
	}

	/**
	 * Type check a given function or method declaration.
	 *
	 * @param fd
	 *            Function or method declaration to check.
	 * @throws IOException
	 */
	public void checkFunctionOrMethodDeclaration(Decl.FunctionOrMethod d) {
		// Check parameters and returns are not empty (i.e. are not equivalent
		// to void, as this is non-sensical).
		checkNonEmpty(d.getParameters());
		checkNonEmpty(d.getReturns());
		// Construct initial environment
		Environment environment = new Environment();
		// Check any preconditions (i.e. requires clauses) provided.
		checkConditions(d.getRequires(), true, environment);
		// Check any postconditions (i.e. ensures clauses) provided.
		checkConditions(d.getEnsures(), true, environment);
		// FIXME: Add the "this" lifetime
		// Create scope representing this declaration
		EnclosingScope scope = new FunctionOrMethodScope(d);
		// Check type information throughout all statements in body.
		Environment last = checkBlock(d.getBody(), environment, scope);
		// Check return value
		checkReturnValue(d, last);
	}

	/**
	 * Check that a return value is provided when it is needed. For example, a
	 * return value is not required for a method that has no return type.
	 * Likewise, we don't expect one from a native method since there was no
	 * body to analyse.
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
		// Check parameters and returns are not empty (i.e. are not equivalent
		// to void, as this is non-sensical).
		checkNonEmpty(d.getParameters());
		checkNonEmpty(d.getReturns());
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
	 * @param block
	 *            Block of statements to flow sensitively type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
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
	 * check type information in a flow-sensitive fashion through a given
	 * statement, whilst type checking it at the same time. For statements which
	 * contain other statements (e.g. if, while, etc), then this will
	 * recursively check type information through them as well.
	 *
	 *
	 * @param forest
	 *            Block of statements to flow-sensitively type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
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
	 * Type check an assertion statement. This requires checking that the
	 * expression being asserted is well-formed and has boolean type. An assert
	 * statement can affect the resulting environment in certain cases, such as
	 * when a type test is assert. For example, after
	 * <code>assert x is int</code> the environment will regard <code>x</code>
	 * as having type <code>int</code>.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment checkAssert(Stmt.Assert stmt, Environment environment, EnclosingScope scope) {
		return checkCondition(stmt.getCondition(), true, environment);
	}

	/**
	 * Type check an assume statement. This requires checking that the
	 * expression being assumed is well-formed and has boolean type. An assume
	 * statement can affect the resulting environment in certain cases, such as
	 * when a type test is assert. For example, after
	 * <code>assert x is int</code> the environment will regard <code>x</code>
	 * as having type <code>int</code>.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
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
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment checkFail(Stmt.Fail stmt, Environment environment, EnclosingScope scope) {
		return BOTTOM;
	}

	/**
	 * Type check a variable declaration statement. In particular, when an
	 * initialiser is given we must check it is well-formed and that it is a
	 * subtype of the declared type.
	 *
	 * @param decl
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment checkVariableDeclaration(Decl.Variable decl, Environment environment,
			EnclosingScope scope) throws IOException {
		// Check type of initialiser.
		if (decl.hasInitialiser()) {
			Type type = checkExpression(decl.getInitialiser(), environment);
			checkIsSubtype(decl.getType(), type, decl.getInitialiser());
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
	 *            Determines the type of all variables immediately going into
	 *            this block
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
			// FIXME: need to handle writable versus readable types. The problem
			// is that checkExpression will return the readable type, not the
			// writeable type.
			checkIsSubtype(lval, rval.getSecond(), rval.getFirst());
		}
		return environment;
	}

	/**
	 * Type check a break statement. This requires propagating the current
	 * environment to the block destination, to ensure that the actual types of
	 * all variables at that point are precise.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment checkBreak(Stmt.Break stmt, Environment environment, EnclosingScope scope) {
		// FIXME: need to check environment to the break destination
		return BOTTOM;
	}

	/**
	 * Type check a continue statement. This requires propagating the current
	 * environment to the block destination, to ensure that the actual types of
	 * all variables at that point are precise.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment checkContinue(Stmt.Continue stmt, Environment environment, EnclosingScope scope) {
		// FIXME: need to check environment to the continue destination
		return BOTTOM;
	}

	/**
	 * Type check an assume statement. This requires checking that the
	 * expression being printed is well-formed and has string type.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment checkDebug(Stmt.Debug stmt, Environment environment, EnclosingScope scope) {
		Type type = checkExpression(stmt.getOperand(), environment);
		checkIsSubtype(new Type.Array(Type.Int), type, stmt.getOperand());
		return environment;
	}

	/**
	 * Type check a do-while statement.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 * @throws ResolveError
	 *             If a named type within this statement cannot be resolved
	 *             within the enclosing project.
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
	 * Type check an if-statement. To do this, we check the environment through
	 * both sides of condition expression. Each can produce a different
	 * environment in the case that runtime type tests are used. These
	 * potentially updated environments are then passed through the true and
	 * false blocks which, in turn, produce updated environments. Finally, these
	 * two environments are joined back together. The following illustrates:
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
	 * <code>int|null</code> before the first statement of the function body. On
	 * the true branch of the type test this is updated to <code>null</code>,
	 * whilst on the false branch it is updated to <code>int</code>. Finally,
	 * the type of <code>x</code> at the end of each block is <code>int</code>
	 * and, hence, its type after the if-statement is <code>int</code>.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 * @throws ResolveError
	 *             If a named type within this statement cannot be resolved
	 *             within the enclosing project.
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
	 * Type check a <code>return</code> statement. If a return expression is
	 * given, then we must check that this is well-formed and is a subtype of
	 * the enclosing function or method's declared return type. The environment
	 * after a return statement is "bottom" because that represents an
	 * unreachable program point.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @param scope
	 *            The stack of enclosing scopes
	 * @return
	 * @throws ResolveError
	 *             If a named type within this statement cannot be resolved
	 *             within the enclosing project.
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
			checkIsSubtype(t, p.getSecond(), p.getFirst());
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
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment checkSkip(Stmt.Skip stmt, Environment environment, EnclosingScope scope) {
		return environment;
	}

	/**
	 * Type check a <code>switch</code> statement. This is similar, in some
	 * ways, to the handling of if-statements except that we have n code blocks
	 * instead of just two. Therefore, we check type information through each
	 * block, which produces n potentially different environments and these are
	 * all joined together to produce the environment which holds after this
	 * statement. For example:
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
	 * Here, the environment after the declaration of <code>y</code> has its
	 * actual type as <code>void</code> since no value has been assigned yet.
	 * For each of the case blocks, this initial environment is (separately)
	 * updated to produce three different environments. Finally, each of these
	 * is joined back together to produce the environment going into the
	 * <code>return</code> statement.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
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
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment checkNamedBlock(Stmt.NamedBlock stmt, Environment environment, EnclosingScope scope) {
		// FIXME: need to declare the named block as an enclosing scope
		return checkBlock(stmt.getBlock(), environment, scope);
	}

	/**
	 * Type check a <code>whiley</code> statement.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 * @throws ResolveError
	 *             If a named type within this statement cannot be resolved
	 *             within the enclosing project.
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
		determineModifiedVariables(block,modified);
		return new Tuple<>(modified);
	}

	private void determineModifiedVariables(Stmt.Block block,Set<Decl.Variable> modified) {
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			switch(stmt.getOpcode()) {
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
	 * Type check a sequence of zero or more conditions, such as the requires
	 * clause of a function or method. The environment from each condition is
	 * fed into the following. This means that, in principle, type tests
	 * influence both subsequent conditions and the remainder. The following
	 * illustrates:
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
	 * this would not type check. Finally, it is an interesting question as to
	 * why the above ever make sense. In general, it's better to simply declare
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
	 * Type check a given condition in a given environment with a given sign
	 * (which indicates whether the condition is known to hold or not). In
	 * certain situations (e.g. an if-statement) a condition may update the
	 * environment in accordance with any type tests used within. This is
	 * important to ensure that variables are <i>retyped</i> in e.g.
	 * if-statements. The simplest possible example is the following:
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
	 * When (for example) typing <code>x &gt; 0</code> here, the environment
	 * would simply map <code>x</code> to its declared type <code>int</code>.
	 * However, because Whiley supports "flow typing", it's not always the case
	 * that the declared type of a variable is the right one to use. Consider a
	 * more complex case.
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
	 * This time, when typing (for example) typing <code>x &gt; 0</code>, we
	 * need to account for the fact that <code>x is int</code> is known. As
	 * such, the calculated type for <code>x</code> would be
	 * <code>(int|null)&int</code> when typing both <code>x &gt; 0</code> and
	 * <code>x + 1</code>.
	 * </p>
	 * <p>
	 * The purpose of the "sign" is to aid flow typing in the presence of
	 * negations. In essence, the sign indicates whether the statement being
	 * type checked is positive (i.e. sign=<code>true</code>) or negative (i.e.
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
	 * To type check this example, the type checker needs to effectively "push"
	 * the logical negation through the disjunction to give
	 * <code>!(x is null) && x &gt;= 0</code>. The purpose of the sign is to
	 * enable this without actually rewriting the source code.
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
		case EXPR_lnot:
			return checkLogicalNegation((Expr.LogicalNot) condition, sign, environment);
		case EXPR_lor:
			return checkLogicalDisjunction((Expr.LogicalOr) condition, sign, environment);
		case EXPR_land:
			return checkLogicalConjunction((Expr.LogicalAnd) condition, sign, environment);
		case EXPR_liff:
			return checkLogicalIff((Expr.LogicalIff) condition, sign, environment);
		case EXPR_limplies:
			return checkLogicalImplication((Expr.LogicalImplication) condition, sign, environment);
		case EXPR_is:
			return checkIs((Expr.Is) condition, sign, environment);
		case EXPR_lall:
		case EXPR_lsome:
			return checkQuantifier((Expr.Quantifier) condition, sign, environment);
		default:
			Type t = checkExpression(condition, environment);
			checkIsSubtype(Type.Bool, t, condition);
			return environment;
		}
	}

	/**
	 * Type check a logical negation. This is relatively straightforward as we
	 * just flip the sign. Thus, if something is assumed to hold, then it is now
	 * assumed not to hold, etc. The following illustrates:
	 *
	 * <pre>
	 * function f(int|null x) -> (bool r):
	 *     return !(x is null) && x >= 0
	 * </pre>
	 *
	 * The effect of the negation <code>!(x is null)</code> is that the type
	 * test is now evaluated assuming it fails. Thus, it effects the environment
	 * by asserting <code>x</code> has type <code>(int|null)&!null</code> which
	 * is equivalent to <code>int</code>.
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
	 * In this case, we are assuming the environments are exclusive from each
	 * other (i.e. this is the opposite of threading them through). For example,
	 * consider this case:
	 *
	 * <pre>
	 * function f(int|null x) -> (bool r):
	 *   return (x is null) || (x >= 0)
	 * </pre>
	 *
	 * The environment produced by the left condition is <code>{x->null}</code>.
	 * We cannot thread this environment into the right condition as, clearly,
	 * it's not correct. Instead, we want to thread through the environment
	 * which arises on the assumption the fist case is false. That would be
	 * <code>{x->!null}</code>. Finally, the resulting environment is simply the
	 * union of the two environments from each case.
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
	 * <code>{x->(int|null)}</code>. The environment coming out of this
	 * statement will be <code>{x-&gt;int}</code> and this is just threaded
	 * directly into the next statement <code>x &gt; 0</code>
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
		Tuple<Expr> operands = expr.getOperands();
		if(operands.size() != 2) {
			// FIXME: this is just a temporary hack for now.
			throw new IllegalArgumentException("cannot currently support implication with > 2 arguments");
		}
		if (sign) {
			// First case assumes the if body doesn't hold.
			Environment left = checkCondition(operands.get(0), false, environment);
			// Second case assumes the if body holds ...
			environment = checkCondition(operands.get(0), true, environment);
			// ... and then passes this into the then body
			Environment right = checkCondition(operands.get(1), true, environment);
			//
			return union(left, right);
		} else {
			// Effectively, this is a conjunction equivalent to A && !B
			environment = checkCondition(operands.get(0), true, environment);
			environment = checkCondition(operands.get(1), false, environment);
			return environment;
		}
	}

	private Environment checkLogicalIff(Expr.LogicalIff expr, boolean sign, Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		for (int i = 0; i != operands.size(); ++i) {
			environment = checkCondition(operands.get(i), sign, environment);
		}
		return environment;
	}

	private Environment checkIs(Expr.Is expr, boolean sign, Environment environment) {
		try {
			Expr lhs = expr.getOperand();
			Type rhsT = expr.getTestType();
			//
			Type lhsT = checkExpression(expr.getOperand(), environment);
			// Sanity check operands for this type test
			Type glbForFalseBranch = new Type.Intersection(lhsT, negate(rhsT));
			Type glbForTrueBranch = new Type.Intersection(lhsT, rhsT);
			if (typeSystem.isVoid(glbForFalseBranch)) {
				// DEFINITE TRUE CASE
				syntaxError(errorMessage(BRANCH_ALWAYS_TAKEN), expr);
			} else if (typeSystem.isVoid(glbForTrueBranch)) {
				// DEFINITE FALSE CASE
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsT, rhsT), expr);
			}
			// Account for case when this test is inverted
			rhsT = sign ? rhsT : negate(rhsT);
			// TODO: implement a proper intersection test here to ensure lhsT and
			// rhs types make sense (i.e. have some intersection).
			Pair<Decl.Variable, Type> extraction = extractTypeTest(lhs, rhsT);
			if (extraction != null) {
				Decl.Variable var = extraction.getFirst();
				// Update the typing environment accordingly.
				environment = environment.refineType(var, extraction.getSecond());
			}
			//
			return environment;
		} catch (ResolutionError e) {
			return syntaxError(e.getMessage(), expr);
		}
	}

	/**
	 * <p>
	 * Extract the "true" test from a given type test in order that we might try
	 * to retype it. This does not always succeed if, for example, the
	 * expression being tested cannot be retyped. An example would be a test
	 * like <code>arr[i] is int</code> as, in this case, we cannot retype
	 * <code>arr[i]</code>.
	 * </p>
	 *
	 * <p>
	 * In the simple case of e.g. <code>x is int</code> we just extract
	 * <code>x</code> and type <code>int</code>. The more interesting case
	 * arises when there is at least one field access involved. For example,
	 * <code>x.f is int</code> extracts variable <code>x</code> with type
	 * <code>{int f, ...}</code> (which is a safe approximation).
	 * </p>
	 *
	 * @param expr
	 * @param type
	 * @return A pair on successful extraction, or null if possible extraction.
	 */
	private Pair<Decl.Variable, Type> extractTypeTest(Expr expr, Type type) {
		if (expr instanceof Expr.VariableAccess) {
			Expr.VariableAccess var = (Expr.VariableAccess) expr;
			return new Pair<>(var.getVariableDeclaration(), type);
		} else if (expr instanceof Expr.RecordAccess) {
			Expr.RecordAccess ra = (Expr.RecordAccess) expr;
			Decl.Variable field = new Decl.Variable(new Tuple<>(), ((Expr.RecordAccess) expr).getField(),
					type);
			Type.Record recT = new Type.Record(true, new Tuple<>(field));
			return extractTypeTest(ra.getOperand(), recT);
		} else {
			// no extraction is possible
			return null;
		}
	}

	private Environment checkQuantifier(Expr.Quantifier stmt, boolean sign, Environment env) {
		checkNonEmpty(stmt.getParameters());
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
		if (left == right) {
			return left;
		} else {
			Environment result = new Environment();

			for (Decl.Variable var : left.getRefinedVariables()) {
				Type declT = var.getType();
				Type rightT = right.getType(var);
				if (rightT != declT) {
					// We have a refinement on both branches
					Type leftT = left.getType(var);
					result = result.refineType(var, union(leftT, rightT));
				}
			}
			return result;
		}
	}

	/**
	 * Union two types together whilst trying to maintain simplicity.
	 *
	 * @param left
	 * @param right
	 * @return
	 */
	public Type union(Type left, Type right) {
		// FIXME: a more comprehensive simplification strategy would make sense
		// here.
		if (left == right || left.equals(right)) {
			return left;
		} else {
			return new Type.Union(new Type[] { left, right });
		}
	}

	/**
	 * Negate a given type whilst trying to maintain simplicity. For example,
	 * negating <code>int</code> gives <code>!int</code>. However, negating
	 * <code>!int</code> gives <code>int</code> (i.e. rather than
	 * <code>!!int</code>).
	 *
	 * @param type
	 * @return
	 */
	public Type negate(Type type) {
		// FIXME: a more comprehensive simplification strategy would make sense
		// here.
		if (type instanceof Type.Negation) {
			Type.Negation nt = (Type.Negation) type;
			return nt.getElement();
		} else {
			return new Type.Negation(type);
		}
	}

	/**
	 * Type check a given lval assuming an initial environment. This returns the
	 * largest type which can be safely assigned to the lval. Observe that this
	 * type is determined by the declared type of the variable being assigned.
	 *
	 * @param expression
	 * @param environment
	 * @return
	 * @throws ResolutionError
	 */
	public Type checkLVal(LVal lval, Environment environment) {
		Type type;
		switch (lval.getOpcode()) {
		case EXPR_varcopy:
			type = checkVariableLVal((Expr.VariableAccess) lval, environment);
			break;
		case EXPR_staticvar:
			type = checkStaticVariableLVal((Expr.StaticVariableAccess) lval, environment);
			break;
		case EXPR_aread:
			type = checkArrayLVal((Expr.ArrayAccess) lval, environment);
			break;
		case EXPR_rread:
			type = checkRecordLVal((Expr.RecordAccess) lval, environment);
			break;
		case EXPR_pread:
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
		// FIXME: bug here as need writeable array type?
		Type.Array readableArrayT = checkIsArrayType(sourceT, source);
		Type subscriptT = checkExpression(subscript, environment);
		checkIsSubtype(new Type.Int(), subscriptT, subscript);
		//
		return readableArrayT.getElement();
	}

	public Type checkRecordLVal(Expr.RecordAccess lval, Environment environment) {
		Type src = checkExpression(lval.getOperand(), environment);
		// FIXME: bug here as need writeable recordy type?
		Type.Record readableRecordT = checkIsRecordType(src, lval.getOperand());
		//
		Type type = readableRecordT.getField(lval.getField());
		if (type == null) {
			return syntaxError("invalid field access", lval.getField());
		} else {
			return type;
		}
	}

	public Type checkDereferenceLVal(Expr.Dereference lval, Environment environment) {
		Type operandT = checkExpression(lval.getOperand(), environment);
		//
		Type.Reference refT = checkIsReferenceType(operandT, lval.getOperand());
		//
		return refT.getElement();
	}

	// =========================================================================
	// Expressions
	// =========================================================================

	/**
	 * Type check a sequence of zero or more multi-expressions, assuming a given
	 * initial environment. A multi-expression is one which may have multiple
	 * return values. There are relatively few situations where this can arise,
	 * particular assignments and return statements. This returns a sequence of
	 * one or more pairs, each of which corresponds to a single return for a
	 * given expression. Thus, each expression generates one or more pairs in
	 * the result.
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
		case EXPR_varcopy:
			type = checkVariable((Expr.VariableAccess) expression, environment);
			break;
		case EXPR_staticvar:
			type = checkStaticVariable((Expr.StaticVariableAccess) expression, environment);
			break;
		case EXPR_cast:
			return checkCast((Expr.Cast) expression, environment);
		case EXPR_invoke: {
			Tuple<Type> types = checkInvoke((Expr.Invoke) expression, environment);
			// Deal with potential for multiple values
			if (types.size() == 0) {
				syntaxError("no return value", expression);
			} else if (types.size() > 1) {
				syntaxError("too many return values", expression);
			} else {
				return types.get(0);
			}
		}
		case EXPR_indirectinvoke: {
			Tuple<Type> types = checkIndirectInvoke((Expr.IndirectInvoke) expression, environment);
			// Deal with potential for multiple values
			if (types.size() == 0) {
				syntaxError("no return value", expression);
			} else if (types.size() > 1) {
				syntaxError("too many return values", expression);
			} else {
				return types.get(0);
			}
		}
		// Conditions
		case EXPR_lnot:
		case EXPR_lor:
		case EXPR_land:
		case EXPR_liff:
		case EXPR_limplies:
		case EXPR_is:
		case EXPR_lall:
		case EXPR_lsome:
			checkCondition(expression, true, environment);
			return Type.Bool;
		// Comparators
		case EXPR_eq:
		case EXPR_neq:
		case EXPR_ilt:
		case EXPR_ile:
		case EXPR_igt:
		case EXPR_ige:
			return checkComparisonOperator((Expr.NaryOperator) expression, environment);
		// Arithmetic Operators
		case EXPR_ineg:
			type = checkArithmeticOperator((Expr.UnaryOperator) expression, environment);
			break;
		case EXPR_iadd:
		case EXPR_isub:
		case EXPR_imul:
		case EXPR_idiv:
		case EXPR_irem:
			type = checkArithmeticOperator((Expr.NaryOperator) expression, environment);
			break;
		// Bitwise expressions
		case EXPR_bnot:
			type = checkBitwiseOperator((Expr.UnaryOperator) expression, environment);
			break;
		case EXPR_band:
		case EXPR_bor:
		case EXPR_bxor:
			type = checkBitwiseOperator((Expr.NaryOperator) expression, environment);
			break;
		case EXPR_bshl:
		case EXPR_bshr:
			type = checkBitwiseShift((Expr.BinaryOperator) expression, environment);
			break;
		// Record Expressions
		case EXPR_rinit:
			type = checkRecordInitialiser((Expr.RecordInitialiser) expression, environment);
			break;
		case EXPR_rread:
			type = checkRecordAccess((Expr.RecordAccess) expression, environment);
			break;
		case EXPR_rwrite:
			type = checkRecordUpdate((Expr.RecordUpdate) expression, environment);
			break;
		// Array expressions
		case EXPR_alen:
			type = checkArrayLength(environment, (Expr.ArrayLength) expression);
			break;
		case EXPR_ainit:
			type = checkArrayInitialiser((Expr.ArrayInitialiser) expression, environment);
			break;
		case EXPR_agen:
			type = checkArrayGenerator((Expr.ArrayGenerator) expression, environment);
			break;
		case EXPR_aread:
			type = checkArrayAccess((Expr.ArrayAccess) expression, environment);
			break;
		case EXPR_awrite:
			type = checkArrayUpdate((Expr.ArrayUpdate) expression, environment);
			break;
		// Reference expressions
		case EXPR_pread:
			type = checkDereference((Expr.Dereference) expression, environment);
			break;
		case EXPR_pinit:
			type = checkNew((Expr.New) expression, environment);
			break;
		case EXPR_lread:
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
	 * Check the type of a given constant expression. This is straightforward
	 * since the determine is fully determined by the kind of constant we have.
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
	 * Check the type of a given variable access. This is straightforward since
	 * the determine is fully determined by the declared type for the variable
	 * in question.
	 *
	 * @param expr
	 * @return
	 */
	private Type checkVariable(Expr.VariableAccess expr, Environment env) {
		Decl.Variable var = expr.getVariableDeclaration();
		return env.getType(var);
	}

	private Type checkStaticVariable(Expr.StaticVariableAccess expr, Environment env) {
		try {
			// Resolve variable declaration being accessed
			Decl.StaticVariable decl = typeSystem.resolveExactly(expr.getName(),
					Decl.StaticVariable.class);
			//
			return decl.getType();
		} catch (ResolutionError e) {
			return syntaxError(errorMessage(RESOLUTION_ERROR, expr.getName().toString()), expr, e);
		}
	}

	private Type checkCast(Expr.Cast expr, Environment env) {
		Type rhsT = checkExpression(expr.getOperand(), env);
		checkIsSubtype(expr.getType(), rhsT, expr);
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
		Decl.Callable decl = resolveAsCallable(expr.getName(), types);
		// Assign descriptor to this expression
		expr.setSignature(expr.getHeap().allocate(decl.getType()));
		// Finally, return the declared returns/
		return decl.getType().getReturns();
	}

	private Tuple<Type> checkIndirectInvoke(Expr.IndirectInvoke expr, Environment env) {
		// Determine signature type from source
		Type type = checkExpression(expr.getSource(), env);
		Type.Callable sig = checkIsCallableType(type, expr.getSource());
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
			Type arg = checkExpression(arguments.get(i), env);
			// Check argument is subtype of parameter
			checkIsSubtype(parameters.get(i), arg, arguments.get(i));
		}
		//
		return sig.getReturns();
	}

	private Type checkComparisonOperator(Expr.NaryOperator expr, Environment environment) {
		switch (expr.getOpcode()) {
		case EXPR_eq:
		case EXPR_neq:
			return checkEqualityOperator(expr, environment);
		default:
			return checkArithmeticComparator(expr, environment);
		}
	}

	private Type checkEqualityOperator(Expr.NaryOperator expr, Environment environment) {
		try {
			Tuple<Expr> operands = expr.getOperands();
			Type last = null;
			for (int i = 0; i != operands.size(); ++i) {
				Type next = checkExpression(operands.get(i), environment);
				if (i > 0) {
					// Sanity check that the types of operands are actually comparable.
					Type glb = new Type.Intersection(last, next);
					if (typeSystem.isVoid(glb)) {
						syntaxError(errorMessage(INCOMPARABLE_OPERANDS, last, next), expr);
						return null;
					}
				}
				last = next;
			}
			return Type.Bool;
		} catch (ResolutionError e) {
			return syntaxError(e.getMessage(), expr);
		}
	}

	private Type checkArithmeticComparator(Expr.NaryOperator expr, Environment environment) {
		checkOperands(Type.Int, expr.getOperands(), environment);
		return Type.Bool;
	}

	private Type checkArithmeticOperator(Expr.UnaryOperator expr, Environment environment) {
		Type lhsT = checkExpression(expr.getOperand(), environment);
		checkIsSubtype(Type.Int, lhsT, expr.get(0));
		return Type.Int;
	}

	/**
	 * Check the type for a given arithmetic operator. Such an operator has the
	 * type int, and all children should also produce values of type int.
	 *
	 * @param expr
	 * @return
	 */
	private Type checkArithmeticOperator(Expr.NaryOperator expr, Environment environment) {
		checkOperands(Type.Int, expr.getOperands(), environment);
		return Type.Int;
	}

	private Type checkBitwiseOperator(Expr.UnaryOperator expr, Environment environment) {
		Type lhsT = checkExpression(expr.getOperand(), environment);
		checkIsSubtype(Type.Byte, lhsT, expr.get(0));
		return Type.Byte;
	}

	private Type checkBitwiseOperator(Expr.NaryOperator expr, Environment environment) {
		checkOperands(Type.Byte, expr.getOperands(), environment);
		return Type.Byte;
	}

	private Type checkBitwiseShift(Expr.BinaryOperator expr, Environment environment) {
		Type lhsT = checkExpression(expr.getFirstOperand(), environment);
		Type rhsT = checkExpression(expr.getSecondOperand(), environment);
		checkIsSubtype(Type.Byte, lhsT, expr.get(0));
		checkIsSubtype(Type.Int, rhsT, expr.get(1));
		return Type.Byte;
	}

	private Type checkRecordAccess(Expr.RecordAccess expr, Environment env) {
		Type src = checkExpression(expr.getOperand(), env);
		Type.Record effectiveRecord = checkIsRecordType(src, expr.getOperand());
		//
		Type type = effectiveRecord.getField(expr.getField());
		if (type == null) {
			return syntaxError("invalid field access", expr.getField());
		} else {
			return type;
		}
	}

	private Type checkRecordUpdate(Expr.RecordUpdate expr, Environment env) {
		Type src = checkExpression(expr.getFirstOperand(), env);
		Type val = checkExpression(expr.getSecondOperand(), env);
		Type.Record effectiveRecord = checkIsRecordType(src, expr.getFirstOperand());
		//
		Tuple<Decl.Variable> fields = effectiveRecord.getFields();
		String actualFieldName = expr.getField().get();
		for (int i = 0; i != fields.size(); ++i) {
			Decl.Variable vd = fields.get(i);
			String declaredFieldName = vd.getName().get();
			if (declaredFieldName.equals(actualFieldName)) {
				// Matched the field type
				checkIsSubtype(vd.getType(), val, expr.getSecondOperand());
				return src;
			}
		}
		//
		return syntaxError("invalid field update", expr.getField());
	}

	private Type checkRecordInitialiser(Expr.RecordInitialiser expr, Environment env) {
		Tuple<Identifier> fields=expr.getFields();
		Tuple<Expr> operands=expr.getOperands();
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
		checkIsArrayType(src, expr.getOperand());
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
		Type lengthT = checkExpression(length, env);
		//
		checkIsSubtype(new Type.Int(), lengthT, length);
		return new Type.Array(valueT);
	}

	private Type checkArrayAccess(Expr.ArrayAccess expr, Environment env) {
		Expr source = expr.getFirstOperand();
		Expr subscript = expr.getSecondOperand();
		//
		Type sourceT = checkExpression(source, env);
		Type subscriptT = checkExpression(subscript, env);
		//
		Type.Array sourceArrayT = checkIsArrayType(sourceT, source);
		checkIsSubtype(new Type.Int(), subscriptT, subscript);
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
		Type.Array sourceArrayT = checkIsArrayType(sourceT, source);
		checkIsSubtype(new Type.Int(), subscriptT, subscript);
		checkIsSubtype(sourceArrayT.getElement(), valueT, value);
		return sourceArrayT;
	}

	private Type checkDereference(Expr.Dereference expr, Environment env) {
		Type operandT = checkExpression(expr.getOperand(), env);
		//
		Type.Reference refT = checkIsReferenceType(operandT, expr.getOperand());
		//
		return refT.getElement();
	}

	private Type checkNew(Expr.New expr, Environment env) {
		Type operandT = checkExpression(expr.getOperand(), env);
		//
		return new Type.Reference(operandT);
	}

	private Type checkLambdaAccess(Expr.LambdaAccess expr, Environment env) {
		Decl.Callable decl;
		Tuple<Type> types = expr.getParameterTypes();
		// FIXME: there is a problem here in that we cannot distinguish
		// between the case where no parameters were supplied and when
		// exactly zero arguments were supplied.
		if (types.size() > 0) {
			// Parameter types have been given, so use them to help resolve
			// declaration.
			decl = resolveAsCallable(expr.getName(), types.toArray(Type.class));
		} else {
			// No parameters we're given, therefore attempt to resolve
			// uniquely.
			decl = resolveAsCallable(expr.getName(), expr);
		}
		// Set descriptor for this expression
		expr.setSignature(expr.getHeap().allocate(decl.getType()));
		//
		return decl.getType();
	}

	private Type checkLambdaDeclaration(Decl.Lambda expr, Environment env) {
		Tuple<Decl.Variable> parameters = expr.getParameters();
		checkNonEmpty(parameters);
		Tuple<Type> parameterTypes = parameters.project(2, Type.class);
		Type result = checkExpression(expr.getBody(), env);
		// Determine whether or not this is a pure or impure lambda.
		if (isPure(expr.getBody())) {
			// This is a pure lambda, hence it has function type.
			return new Type.Function(parameterTypes, new Tuple<>(result));
		} else {
			// This is an impure lambda, hence it has method type.
			return new Type.Method(parameterTypes, new Tuple<>(result), expr.getCaptures(), expr.getLifetimes());
		}
	}

	/**
	 * Determine whether a given expression calls an impure method, dereferences
	 * a reference or accesses a static variable. This is done by exploiting the
	 * uniform nature of syntactic items. Essentially, we just traverse the
	 * entire tree representing the syntactic item looking for expressions of
	 * any kind.
	 *
	 * @param item
	 * @return
	 */
	private boolean isPure(SyntacticItem item) {
		// Examine expression to determine whether this expression is impure.
		if (item instanceof Expr.StaticVariableAccess || item instanceof Expr.Dereference) {
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
	 * Check whether a given type is an array type of some sort.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	private Type.Array checkIsArrayType(Type type, SyntacticItem element) {
		try {
			Type.Array arrT = typeSystem.extractReadableArray(type);
			if (arrT == null) {
				syntaxError("expected array type", element);
			}
			return arrT;
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
	private Decl.FunctionOrMethod resolveAsCallable(Name name, SyntacticItem context) {
		try {
			// Identify all function or macro declarations which should be
			// considered
			List<Decl.FunctionOrMethod> candidates = typeSystem.resolveAll(name,
					Decl.FunctionOrMethod.class);
			if (candidates.isEmpty()) {
				return syntaxError(errorMessage(RESOLUTION_ERROR,name.toString()), context);
			} else if (candidates.size() > 1) {
				return syntaxError(errorMessage(AMBIGUOUS_RESOLUTION,foundCandidatesString(candidates)), context);
			} else {
				return candidates.get(0);
			}
		} catch (ResolutionError e) {
			return syntaxError(e.getMessage(), context);
		}
	}

	/**
	 * Attempt to determine the declared function or macro to which a given
	 * invocation refers. To resolve this requires considering the name, along
	 * with the argument types as well.
	 *
	 * @param name
	 * @param args
	 * @return
	 */
	private Decl.Callable resolveAsCallable(Name name, Type... args) {
		try {
			// Identify all function or macro declarations which should be
			// considered
			List<Decl.Callable> candidates = typeSystem.resolveAll(name, Decl.Callable.class);
			// Based on given argument types, select the most precise signature
			// from the candidates.
			Decl.Callable selected = selectCallableCandidate(name, candidates,args);
			return selected;
		} catch (ResolutionError e) {
			return syntaxError(e.getMessage(), name);
		}
	}

	/**
	 * Given a list of candidate function or method declarations, determine the
	 * most precise match for the supplied argument types. The given argument
	 * types must be applicable to this function or macro declaration, and it
	 * must be a subtype of all other applicable candidates.
	 *
	 * @param candidates
	 * @param args
	 * @return
	 */
	private Decl.Callable selectCallableCandidate(Name name, List<Decl.Callable> candidates,
			Type... args) {
		Decl.Callable best = null;
		//
		for (int i = 0; i != candidates.size(); ++i) {
			Decl.Callable candidate = candidates.get(i);
			// Check whether the given candidate is a real candidate or not. A
			if (isApplicable(candidate, args)) {
				// Yes, this candidate is applicable.
				if(best == null) {
					// No other candidates are applicable so far. Hence, this
					// one is automatically promoted to the best seen so far.
					best = candidate;
				} else {
					boolean bsubc = isSubtype(best, candidate);
					boolean csubb = isSubtype(candidate, best);
					//
					// FIXME: this is certainly broken.
					//
					if (csubb && !bsubc) {
						// This candidate is a subtype of the best seen so far.
						// Hence, it is now the best seen so far.
						best = candidate;
					} else if (bsubc && !csubb) {
						// This best so far is a subtype of this candidate.
						// Therefore, we can simply discard this candidate from
						// consideration.
					} else {
						// This is the awkward case. Neither the best so far, nor
						// the candidate, are subtypes of each other. In this case,
						// we report an error.
						return syntaxError(errorMessage(AMBIGUOUS_RESOLUTION,foundCandidatesString(candidates)), name);
					}
				}
			}
		}
		// Having considered each candidate in turn, do we now have a winner?
		if (best != null) {
			// Yes, we have a winner.
			return best;
		} else {
			// No, there was no winner. In fact, there must have been no
			// applicable candidates to get here.
			return syntaxError("unable to resolve name (no match for " + name + parameterString(args) + ")"
					+ foundCandidatesString(candidates), name);
		}
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
			// FIXME: this is very ugly
			Path.ID mid = ((WhileyFile)c.getHeap()).getEntry().id();
			candidateStrings.add(mid + ":" + c.getName() + " : " + c.getType());
		}
		Collections.sort(candidateStrings); // make error message deterministic!
		StringBuilder msg = new StringBuilder();
		for (String s : candidateStrings) {
			msg.append("\n\tfound: ");
			msg.append(s);
		}
		return msg.toString();
}

	/**
	 * Determine whether a given function or method declaration is applicable to
	 * a given set of argument types. If there number of arguments differs, it's
	 * definitely not applicable. Otherwise, we need every argument type to be a
	 * subtype of its corresponding parameter type.
	 *
	 * @param candidate
	 * @param args
	 * @return
	 */
	private boolean isApplicable(Decl.Callable candidate, Type... args) {
		Tuple<Decl.Variable> parameters = candidate.getParameters();
		if (parameters.size() != args.length) {
			// Differing number of parameters / arguments. Since we don't
			// support variable-length argument lists (yet), there is nothing
			// more to consider.
			return false;
		}
		try {
			// Number of parameters matches number of arguments. Now, check that
			// each argument is a subtype of its corresponding parameter.
			for (int i = 0; i != args.length; ++i) {
				Type param = parameters.get(i).getType();
				if (!typeSystem.isRawCoerciveSubtype(param, args[i])) {
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
	 * Check whether the type signature for a given function or method
	 * declaration is a super type of a given child declaration.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private boolean isSubtype(Decl.Callable lhs, Decl.Callable rhs) {
		Tuple<Decl.Variable> parentParams = lhs.getParameters();
		Tuple<Decl.Variable> childParams = rhs.getParameters();
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
				Type parentParam = parentParams.get(i).getType();
				Type childParam = childParams.get(i).getType();
				if (!typeSystem.isRawCoerciveSubtype(parentParam, childParam)) {
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
	 * Check whether a given type is a record type of some sort.
	 *
	 * @param type
	 * @return
	 */
	private Type.Record checkIsRecordType(Type type, SyntacticItem element) {
		try {
			Type.Record recT = typeSystem.extractReadableRecord(type);
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
	private Type.Reference checkIsReferenceType(Type type, SyntacticItem element) {
		try {
			Type.Reference refT = typeSystem.extractReadableReference(type);
			if (refT == null) {
				syntaxError("expected reference type", element);
			}
			return refT;
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
	private Type.Callable checkIsCallableType(Type type, SyntacticItem element) {
		try {
			Type.Callable refT = typeSystem.extractReadableLambda(type);
			if (refT == null) {
				syntaxError("expected lambda type", element);
			}
			return refT;
		} catch (NameResolver.ResolutionError e) {
			return syntaxError(e.getMessage(), e.getName(), e);
		}
	}

	private void checkOperands(Type type, Tuple<Expr> operands, Environment environment) {
		for (int i = 0; i != operands.size(); ++i) {
			Expr operand = operands.get(i);
			checkIsSubtype(type, checkExpression(operand, environment), operand);
		}
	}
	// ==========================================================================
	// Helpers
	// ==========================================================================

	private void checkIsSubtype(Type lhs, Type rhs, SyntacticItem element) {
		try {
			if (!typeSystem.isRawCoerciveSubtype(lhs, rhs)) {
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
	 * Check a given set of variable declarations are not "empty". That is,
	 * their declared type is not equivalent to void.
	 *
	 * @param decls
	 */
	private void checkNonEmpty(Tuple<Decl.Variable> decls) {
		for (int i = 0; i != decls.size(); ++i) {
			checkNonEmpty(decls.get(i));
		}
	}

	/**
	 * Check that a given variable declaration is not empty. That is, the
	 * declared type is not equivalent to void. This is an important sanity
	 * check.
	 *
	 * @param d
	 */
	private void checkNonEmpty(Decl.Variable d) {
		try {
			Type type = d.getType();
			if (typeSystem.isVoid(type)) {
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

	private static final Environment BOTTOM = new Environment();

	// ==========================================================================
	// Enclosing Scope
	// ==========================================================================

	/**
	 * An enclosing scope captures the nested of declarations, blocks and other
	 * statements (e.g. loops). It is used to store information associated with
	 * these things such they can be accessed further down the chain. It can
	 * also be used to propagate information up the chain (for example, the
	 * environments arising from a break or continue statement).
	 *
	 * @author David J. Pearce
	 *
	 */
	private abstract static class EnclosingScope {
		private final EnclosingScope parent;

		public EnclosingScope(EnclosingScope parent) {
			this.parent = parent;
		}

		/**
		 * Get the innermost enclosing block of a given kind. For example, when
		 * processing a return statement we may wish to get the enclosing
		 * function or method declaration such that we can type check the return
		 * types.
		 *
		 * @param kind
		 */
		public <T extends EnclosingScope> T getEnclosingScope(Class<T> kind) {
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

	/**
	 * Provides a very simple typing environment which defaults to using the
	 * declared type for a variable (this is the "null" case). However, the
	 * environment can also be updated to override the declared type with a new type
	 * as appropriate.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Environment {
		private final Map<Decl.Variable,Type> refinements;

		public Environment() {
			this.refinements = new HashMap<>();
		}

		public Environment(Map<Decl.Variable,Type> refinements) {
			this.refinements = new HashMap<>(refinements);
		}

		public Type getType(Decl.Variable var) {
			Type refined = refinements.get(var);
			if(refined != null) {
				return refined;
			} else {
				return var.getType();
			}
		}

		public Environment refineType(Decl.Variable var, Type refinement) {
			Type type = intersect(getType(var),refinement);
			Environment r = new Environment(this.refinements);
			r.refinements.put(var,type);
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

		private Type intersect(Type left, Type right) {
			// FIXME: a more comprehensive simplification strategy would make sense
			// here.
			if(left == right || left.equals(right)) {
				return left;
			} else {
				return new Type.Intersection(new Type[]{left,right});
			}
		}
	}
}
