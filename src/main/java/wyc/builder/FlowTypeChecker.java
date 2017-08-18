// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.builder;

import static wybs.lang.SyntaxError.InternalFailure;
import static wyil.util.ErrorMessages.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import wybs.lang.*;
import wybs.util.*;
import wyc.lang.*;
import wycc.util.ArrayUtils;
import wycc.util.Pair;
import wycc.util.Triple;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.WyilFile;
import static wyil.lang.WyilFile.*;
import wyil.util.TypeSystem;

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
public class FlowTypeChecker {

	private final CompileTask builder;
	private final TypeSystem typeSystem;

	public FlowTypeChecker(CompileTask builder) {
		this.builder = builder;
		this.typeSystem = builder.getTypeSystem();
	}

	// =========================================================================
	// WhileyFile(s)
	// =========================================================================

	public void check(List<WyilFile> files) {
		for (WyilFile wf : files) {
			check(wf);
		}
	}

	public void check(WyilFile wf) {
		for (Declaration decl : wf.getDeclarations()) {
			check(decl);
		}
	}

	// =========================================================================
	// Declarations
	// =========================================================================

	public void check(Declaration decl) {
		if(decl instanceof Declaration.Constant) {
			check((Declaration.Constant) decl);
		} else if(decl instanceof Declaration.Type) {
			check((Declaration.Type) decl);
		} else {
			check((Declaration.Callable) decl);
		}
	}

	/**
	 * Resolve types for a given type declaration. If an invariant expression is
	 * given, then we have to check and resolve types throughout the
	 * expression.
	 *
	 * @param td
	 *            Type declaration to check.
	 * @throws IOException
	 */
	public void check(Declaration.Type decl) {

	}

	/**
	 * check and check types for a given constant declaration.
	 *
	 * @param cd
	 *            Constant declaration to check.
	 * @throws IOException
	 */
	public void check(Declaration.Constant decl) {

	}

	/**
	 * check and check types for a given function or method declaration.
	 *
	 * @param fd
	 *            Function or method declaration to check.
	 * @throws IOException
	 */
	public void check(Declaration.FunctionOrMethod d)  {
		// Resolve the types of all parameters and construct an appropriate
		// environment for use in the flow-sensitive type propagation.
		Environment environment = new Environment().declareLifetimeParameters(d.getLifetimes());
		// Resolve types for any preconditions (i.e. requires clauses) provided.
		checkConditions(d.getRequires(), environment);
		// Resolve types for any postconditions (i.e. ensures clauses) provided.
		checkConditions(d.getEnsures(), environment);
		// Add the "this" lifetime
		environment = environment.startNamedBlock("this");
		// Finally, check type information throughout all statements in the
		// function / method body.
		Environment last = checkBlock(d.getBody(), environment);
		//
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
	private void checkReturnValue(Declaration.Callable d, Environment last) {
		if (!d.hasModifier(Modifier.NATIVE) && last != BOTTOM && d.resolvedType().returns().length != 0
				&& !(d instanceof WhileyFile.Property)) {
			// In this case, code reaches the end of the function or method and,
			// furthermore, that this requires a return value. To get here means
			// that there was no explicit return statement given on at least one
			// execution path.
			throw new SyntaxError("missing return statement", file.getEntry(), d);
		}
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
	private Environment checkBlock(Tuple<Stmt> block, Environment environment) {
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.getOperand(i);
			environment = check(stmt, environment);
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
	private Environment check(Stmt stmt, Environment environment) {
		if (environment == BOTTOM) {
			throw new SyntaxError(errorMessage(UNREACHABLE_CODE), file.getEntry(), stmt);
		}
		try {
			if (stmt instanceof Declaration.Variable) {
				return check((Declaration.Variable) stmt, environment);
			} else if (stmt instanceof Stmt.Assign) {
				return check((Stmt.Assign) stmt, environment);
			} else if (stmt instanceof Stmt.Return) {
				return check((Stmt.Return) stmt, environment);
			} else if (stmt instanceof Stmt.IfElse) {
				return check((Stmt.IfElse) stmt, environment);
			} else if (stmt instanceof Stmt.NamedBlock) {
				return check((Stmt.NamedBlock) stmt, environment);
			} else if (stmt instanceof Stmt.While) {
				return check((Stmt.While) stmt, environment);
			} else if (stmt instanceof Stmt.Switch) {
				return check((Stmt.Switch) stmt, environment);
			} else if (stmt instanceof Stmt.DoWhile) {
				return check((Stmt.DoWhile) stmt, environment);
			} else if (stmt instanceof Stmt.Break) {
				return check((Stmt.Break) stmt, environment);
			} else if (stmt instanceof Stmt.Continue) {
				return check((Stmt.Continue) stmt, environment);
			} else if (stmt instanceof Stmt.Assert) {
				return check((Stmt.Assert) stmt, environment);
			} else if (stmt instanceof Stmt.Assume) {
				return check((Stmt.Assume) stmt, environment);
			} else if (stmt instanceof Stmt.Fail) {
				return check((Stmt.Fail) stmt, environment);
			} else if (stmt instanceof Stmt.Debug) {
				return check((Stmt.Debug) stmt, environment);
			} else if (stmt instanceof Stmt.Skip) {
				return check((Stmt.Skip) stmt, environment);
			} else {
				throw new InternalFailure("unknown statement: " + stmt.getClass().getName(), file.getEntry(), stmt);
			}
		} catch (ResolveError e) {
			throw new SyntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()), file.getEntry(), stmt, e);
		} catch (SyntaxError e) {
			throw e;
		} catch (Throwable e) {
			throw new InternalFailure(e.getMessage(), file.getEntry(), stmt, e);
		}
	}

	/**
	 * Type check an assertion statement. This requires checking that the
	 * expression being asserted is well-formed and has boolean type.
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
	private Environment check(Stmt.Assert stmt, Environment environment) throws ResolveError {
		return checkCondition(stmt.getCondition(), true, environment);
	}

	/**
	 * Type check an assume statement. This requires checking that the
	 * expression being asserted is well-formed and has boolean type.
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
	private Environment check(Stmt.Assume stmt, Environment environment) throws ResolveError {
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
	private Environment check(Stmt.Fail stmt, Environment environment) {
		return BOTTOM;
	}

	/**
	 * Type check a variable declaration statement. This must associate the
	 * given variable with either its declared and actual type in the
	 * environment. If no initialiser is given, then the actual type is the void
	 * (since the variable is not yet defined). Otherwise, the actual type is
	 * the type of the initialiser expression. Additionally, when an initialiser
	 * is given we must check it is well-formed and that it is a subtype of the
	 * declared type.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment check(Declaration.Variable stmt, Environment environment)
			throws IOException, ResolveError {
		// Check type of initialiser.
		if (stmt.hasInitialiser()) {
			Type type = checkExpression(stmt.getInitialiser(), environment);
			checkIsSubtype(stmt.getType(), type, stmt.getInitialiser());
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
	private Environment check(Stmt.Assign stmt, Environment environment)
			throws IOException, ResolveError {
		return null;
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
	private Environment check(Stmt.Break stmt, Environment environment) {
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
	private Environment check(Stmt.Continue stmt, Environment environment) {
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
	 * @throws ResolveError
	 */
	private Environment check(Stmt.Debug stmt, Environment environment) throws ResolveError {
		Type type = checkExpression(stmt.getCondition(), environment);
		checkIsSubtype(new Type.Array(Type.Int), type, stmt.getCondition());
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
	private Environment check(Stmt.DoWhile stmt, Environment environment) throws ResolveError {
		// Type loop body
		environment = check(stmt.getBody(), environment);
		// Type invariants
		checkConditions(stmt.getInvariant(), environment);
		// Type condition assuming its false to represent the terminated loop.
		// This is important if the condition contains a type test, as we'll
		// know that doesn't hold here.
		return checkCondition(stmt.getCondition(), false, environment);
	}

	/**
	 * Type check an if-statement. To do this, we check the environment
	 * through both sides of condition expression. Each can produce a different
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
	 *        x = 0
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
	private Environment check(Stmt.IfElse stmt, Environment environment) throws ResolveError {

		// First, check condition and apply variable retypings.
		Pair<Expr, Environment> p1, p2;

		p1 = checkCondition(stmt.condition, true, environment.clone());
		p2 = checkCondition(stmt.condition, false, environment.clone());
		stmt.condition = p1.first();

		Environment trueEnvironment = p1.second();
		Environment falseEnvironment = p2.second();

		// Second, update environments for true and false branches
		if (stmt.trueBranch != null && stmt.falseBranch != null) {
			trueEnvironment = check(stmt.trueBranch, trueEnvironment);
			falseEnvironment = check(stmt.falseBranch, falseEnvironment);
		} else if (stmt.trueBranch != null) {
			trueEnvironment = check(stmt.trueBranch, trueEnvironment);
		} else if (stmt.falseBranch != null) {
			trueEnvironment = environment;
			falseEnvironment = check(stmt.falseBranch, falseEnvironment);
		}

		// Finally, join results back together
		return trueEnvironment.merge(environment.keySet(), falseEnvironment);
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
	 * @return
	 * @throws ResolveError
	 *             If a named type within this statement cannot be resolved
	 *             within the enclosing project.
	 */
	private Environment check(Stmt.Return stmt, Environment environment)
			throws IOException, ResolveError {
		List<Expr> stmt_returns = stmt.returns;
		for (int i = 0; i != stmt_returns.size(); ++i) {
			stmt_returns.set(i, check(stmt_returns.get(i), environment));
		}
		List<Pair<Expr, Type>> stmt_types = calculateTypesProduced(stmt_returns);
		// FIXME: this is less than ideal
		Type[] current_returns = ((WhileyFile.Callable) context).resolvedType().returns();

		if (stmt_types.size() < current_returns.length) {
			// In this case, a return statement was provided with too few return
			// values compared with the number declared for the enclosing
			// method.
			throw new SyntaxError("not enough return values provided", file.getEntry(), stmt);
		} else if (stmt_types.size() > current_returns.length) {
			// In this case, a return statement was provided with too many
			// return
			// values compared with the number declared for the enclosing
			// method.
			throw new SyntaxError("too many return values provided", file.getEntry(), stmt);
		}

		// Number of return values match number declared for enclosing
		// function/method. Now, check they have appropriate types.
		for (int i = 0; i != current_returns.length; ++i) {
			Pair<Expr, Type> p = stmt_types.get(i);
			Type t = current_returns[i];
			checkIsSubtype(t, p.second(), p.first(), environment);
		}

		environment.free();
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
	private Environment check(Stmt.Skip stmt, Environment environment) {
		return environment;
	}

	/**
	 * Type check a <code>switch</code> statement. This is similar, in some
	 * ways, to the handling of if-statements except that we have n code blocks
	 * instead of just two. Therefore, we check type information through
	 * each block, which produces n potentially different environments and these
	 * are all joined together to produce the environment which holds after this
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
	private Environment check(Stmt.Switch stmt, Environment environment) throws IOException {

		stmt.expr = check(stmt.expr, environment);

		Environment finalEnv = null;
		boolean hasDefault = false;

		for (Stmt.Case c : stmt.cases) {

			// first, resolve the constants

			ArrayList<Constant> values = new ArrayList<>();
			for (Expr e : c.expr) {
				values.add(resolveAsConstant(e).first());
			}
			c.constants = values;

			// second, check through the statements

			Environment localEnv = environment.clone();
			localEnv = check(c.stmts, localEnv);

			if (finalEnv == null) {
				finalEnv = localEnv;
			} else {
				finalEnv = finalEnv.merge(environment.keySet(), localEnv);
			}

			// third, keep track of whether a default
			hasDefault |= c.expr.isEmpty();
		}

		if (!hasDefault) {

			// in this case, there is no default case in the switch. We must
			// therefore assume that there are values which will fall right
			// through the switch statement without hitting a case. Therefore,
			// we must include the original environment to accound for this.

			finalEnv = finalEnv.merge(environment.keySet(), environment);
		} else {
			environment.free();
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
	private Environment check(Stmt.NamedBlock stmt, Environment environment) {
		environment = environment.startNamedBlock(stmt.name);
		environment = check(stmt.body, environment);
		return environment.endNamedBlock(stmt.name);
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
	private Environment check(Stmt.While stmt, Environment environment) throws ResolveError {
		// Type condition assuming its false to represent the terminated loop.
		// This is important if the condition contains a type test, as we'll
		// know that doesn't hold here.
		Pair<Expr, Environment> p1 = checkCondition(stmt.condition, true, environment.clone());
		Pair<Expr, Environment> p2 = checkCondition(stmt.condition, false, environment.clone());
		stmt.condition = p1.first();

		Environment trueEnvironment = p1.second();
		Environment falseEnvironment = p2.second();

		// Type loop invariant(s)
		List<Expr> stmt_invariants = stmt.invariants;
		for (int i = 0; i != stmt_invariants.size(); ++i) {
			Expr invariant = stmt_invariants.get(i);
			invariant = check(invariant, environment);
			stmt_invariants.set(i, invariant);
			checkIsSubtype(Type.Bool, invariant, environment);
		}

		// Type loop body
		check(stmt.body, trueEnvironment);

		// Done
		return falseEnvironment;
	}

	// =========================================================================
	// LVals
	// =========================================================================

	// =========================================================================
	// Condition
	// =========================================================================

	public Environment checkConditions(Tuple<Expr> condition, boolean sign, Environment environment) {

	}

	public Environment checkCondition(Expr condition, boolean sign, Environment environment) {

	}

	// =========================================================================
	// Expressions
	// =========================================================================

	public Type checkExpression(Expr condition, Environment environment) {

	}

	// ==========================================================================
	// Helpers
	// ==========================================================================

	private void checkIsSubtype(Type lhs, Type rhs, SyntacticElement element) {
		try {
			if (!types.isRawSubtype(lhs, rhs)) {
				throw new SyntaxError("type " + rhs + " not subtype of " + lhs, originatingEntry, element);
			}
		} catch (NameResolver.ResolutionError e) {
			throw new SyntaxError(e.getMessage(), originatingEntry, e.getName(), e);
		}
	}

	// ==========================================================================
	// Environment Class
	// ==========================================================================

	/**
	 * <p>
	 * Responsible for mapping source-level variables to their declared and
	 * actual types, at any given program point. Since the flow-type checker
	 * uses a flow-sensitive approach to type checking, then the typing
	 * environment will change as we move through the statements of a function
	 * or method.
	 * </p>
	 *
	 * <p>
	 * This class is implemented in a functional style to minimise possible
	 * problems related to aliasing (which have been a problem in the past). To
	 * improve performance, reference counting is to ensure that cloning the
	 * underling map is only performed when actually necessary.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	private static final class Environment {

		/**
		 * The mapping of variables to their declared type.
		 */
		private final HashMap<String, Type> declaredTypes;

		/**
		 * The mapping of variables to their current type.
		 */
		private final HashMap<String, Type> currentTypes;

		/**
		 * The reference count, which indicate how many references to this
		 * environment there are. When there is only one reference, then the put
		 * and putAll operations will perform an "inplace" update (i.e. without
		 * cloning the underlying collection).
		 */
		private int count; // refCount

		/**
		 * Whether we are currently inside a Lambda body
		 */
		private boolean inLambda;

		/**
		 * The lifetimes that are allowed to be dereferenced in a lambda body.
		 * These are lifetime parameters to the lambda expression and declared
		 * context lifetimes.
		 */
		private final HashSet<String> lambdaLifetimes;

		/**
		 * The lifetime relation remembers how lifetimes are ordered (they are
		 * in a partial order).
		 */
		private final LifetimeRelation lifetimeRelation;

		/**
		 * Construct an empty environment. Initially the reference count is 1.
		 */
		public Environment() {
			count = 1;
			currentTypes = new HashMap<>();
			declaredTypes = new HashMap<>();
			inLambda = false;
			lambdaLifetimes = new HashSet<>();
			lifetimeRelation = new LifetimeRelation();
		}

		/**
		 * Construct a fresh environment as a copy of another map. Initially the
		 * reference count is 1.
		 */
		private Environment(Environment environment) {
			count = 1;
			this.currentTypes = (HashMap<String, Type>) environment.currentTypes.clone();
			this.declaredTypes = (HashMap<String, Type>) environment.declaredTypes.clone();
			inLambda = environment.inLambda;
			lambdaLifetimes = (HashSet<String>) environment.lambdaLifetimes.clone();
			lifetimeRelation = new LifetimeRelation(environment.lifetimeRelation);
		}

		/**
		 * Get the type associated with a given variable at the current program
		 * point, or null if that variable is not declared.
		 *
		 * @param variable
		 *            Variable to return type for.
		 * @return
		 */
		public Type getCurrentType(String variable) {
			return currentTypes.get(variable);
		}

		/**
		 * Get the declared type of a given variable, or null if that variable
		 * is not declared.
		 *
		 * @param variable
		 *            Variable to return type for.
		 * @return
		 */
		public Type getDeclaredType(String variable) {
			return declaredTypes.get(variable);
		}

		/**
		 * Check whether a given variable is declared within this environment.
		 *
		 * @param variable
		 * @return
		 */
		public boolean containsKey(String variable) {
			return declaredTypes.containsKey(variable);
		}

		/**
		 * Return the set of declared variables in this environment (a.k.a the
		 * domain).
		 *
		 * @return
		 */
		public Set<String> keySet() {
			return declaredTypes.keySet();
		}

		/**
		 * Declare a new variable with a given type. In the case that this
		 * environment has a reference count of 1, then an "in place" update is
		 * performed. Otherwise, a fresh copy of this environment is returned
		 * with the given variable associated with the given type, whilst this
		 * environment is unchanged.
		 *
		 * @param variable
		 *            Name of variable to be declared with given type
		 * @param declared
		 *            Declared type of the given variable
		 * @param initial
		 *            Initial type of given variable
		 * @return An updated version of the environment which contains the new
		 *         association.
		 */
		public Environment declare(String variable, Type declared, Type initial) {
			// TODO: check that lifetimes and variables are disjoint
			if (declaredTypes.containsKey(variable)) {
				throw new RuntimeException("Variable already declared - " + variable);
			}
			if (count == 1) {
				declaredTypes.put(variable, declared);
				currentTypes.put(variable, initial);
				return this;
			} else {
				Environment nenv = new Environment(this);
				nenv.declaredTypes.put(variable, declared);
				nenv.currentTypes.put(variable, initial);
				count--;
				return nenv;
			}
		}

		/**
		 * Declare lifetime parameters for a method. In the case that this
		 * environment has a reference count of 1, then an "in place" update is
		 * performed. Otherwise, a fresh copy of this environment is returned
		 * with the given variable associated with the given type, whilst this
		 * environment is unchanged.
		 *
		 * @param lifetimeParameters
		 * @return An updated version of the environment which contains the
		 *         lifetime parameters.
		 */
		public Environment declareLifetimeParameters(List<String> lifetimeParameters) {
			// TODO: check duplicated variable/lifetime names
			if (count == 1) {
				this.lifetimeRelation.addParameters(lifetimeParameters);
				return this;
			} else {
				Environment nenv = new Environment(this);
				nenv.lifetimeRelation.addParameters(lifetimeParameters);
				count--;
				return nenv;
			}
		}

		/**
		 * Declare a lifetime for a named block. In the case that this
		 * environment has a reference count of 1, then an "in place" update is
		 * performed. Otherwise, a fresh copy of this environment is returned
		 * with the given variable associated with the given type, whilst this
		 * environment is unchanged.
		 *
		 * @param lifetime
		 * @return An updated version of the environment which contains the
		 *         named block.
		 */
		public Environment startNamedBlock(String lifetime) {
			// TODO: check duplicated variable/lifetime names
			if (count == 1) {
				this.lifetimeRelation.startNamedBlock(lifetime);
				return this;
			} else {
				Environment nenv = new Environment(this);
				nenv.lifetimeRelation.startNamedBlock(lifetime);
				count--;
				return nenv;
			}
		}

		/**
		 * End the last named block, i.e. remove its declared lifetime. In the
		 * case that this environment has a reference count of 1, then an
		 * "in place" update is performed. Otherwise, a fresh copy of this
		 * environment is returned with the given variable associated with the
		 * given type, whilst this environment is unchanged.
		 *
		 * @param lifetime
		 * @return An updated version of the environment without the given named
		 *         block.
		 */
		public Environment endNamedBlock(String lifetime) {
			if (count == 1) {
				this.lifetimeRelation.endNamedBlock(lifetime);
				return this;
			} else {
				Environment nenv = new Environment(this);
				nenv.lifetimeRelation.endNamedBlock(lifetime);
				count--;
				return nenv;
			}
		}

		/**
		 * Update the current type of a given variable. If that variable already
		 * had a current type, then this is overwritten. In the case that this
		 * environment has a reference count of 1, then an "in place" update is
		 * performed. Otherwise, a fresh copy of this environment is returned
		 * with the given variable associated with the given type, whilst this
		 * environment is unchanged.
		 *
		 * @param variable
		 *            Name of variable to be associated with given type
		 * @param type
		 *            Type to associated with given variable
		 * @return An updated version of the environment which contains the new
		 *         association.
		 */
		public Environment update(String variable, Type type) {
			if (!declaredTypes.containsKey(variable)) {
				throw new RuntimeException("Variable not declared - " + variable);
			}
			if (count == 1) {
				currentTypes.put(variable, type);
				return this;
			} else {
				Environment nenv = new Environment(this);
				nenv.currentTypes.put(variable, type);
				count--;
				return nenv;
			}
		}

		/**
		 * Remove a variable and any associated type from this environment. In
		 * the case that this environment has a reference count of 1, then an
		 * "in place" update is performed. Otherwise, a fresh copy of this
		 * environment is returned with the given variable and any association
		 * removed.
		 *
		 * @param variable
		 *            Name of variable to be removed from the environment
		 * @return An updated version of the environment in which the given
		 *         variable no longer exists.
		 */
		public Environment remove(String key) {
			if (count == 1) {
				declaredTypes.remove(key);
				currentTypes.remove(key);
				return this;
			} else {
				Environment nenv = new Environment(this);
				nenv.currentTypes.remove(key);
				nenv.declaredTypes.remove(key);
				count--;
				return nenv;
			}
		}

		/**
		 * Create a fresh copy of this environment, but set the lambda flag and
		 * remember the given context lifetimes and lifetime parameters.
		 *
		 * @param contextLifetimes
		 *            the declared context lifetimes
		 * @param lifetimeParameters
		 *            The lifetime names that are allowed to be dereferenced
		 *            inside the lambda.
		 */
		public Environment startLambda(Collection<String> contextLifetimes, Collection<String> lifetimeParameters) {
			Environment nenv = new Environment(this);
			nenv.inLambda = true;
			nenv.lambdaLifetimes.clear();
			nenv.lambdaLifetimes.addAll(contextLifetimes);
			nenv.lambdaLifetimes.addAll(lifetimeParameters);
			return nenv;
		}

		/**
		 * Check whether we are allowed to dereference the given lifetime.
		 * Inside a lambda, only "*", the declared context lifetimes and the
		 * lifetime parameters can be dereferenced.
		 *
		 * @param lifetime
		 * @return
		 */
		public boolean canDereferenceLifetime(String lifetime) {
			return !inLambda || lifetime.equals("*") || lambdaLifetimes.contains(lifetime);
		}

		/**
		 * Get the current lifetime relation.
		 *
		 * @return
		 */
		public LifetimeRelation getLifetimeRelation() {
			return this.lifetimeRelation;
		}

		/**
		 * Merge a given environment with this environment to produce an
		 * environment representing their join. Only variables from a given set
		 * are included in the result, and all such variables are required to be
		 * declared in both environments. The type of each variable included is
		 * the union of its type in this environment and the other environment.
		 *
		 * @param declared
		 *            The set of declared variables which should be included in
		 *            the result. The intuition is that these are the variables
		 *            which were declared in both environments before whatever
		 *            updates were made.
		 * @param env
		 *            The given environment to be merged with this environment.
		 * @return
		 */
		public final Environment merge(Set<String> declared, Environment env) {

			// first, need to check for the special bottom value case.

			if (this == BOTTOM) {
				return env;
			} else if (env == BOTTOM) {
				return this;
			}

			// ok, not bottom so compute intersection.

			this.free();
			env.free();

			Environment result = new Environment();
			for (String variable : declared) {
				Type lhs_t = this.getCurrentType(variable);
				Type rhs_t = env.getCurrentType(variable);
				result.declare(variable, this.getDeclaredType(variable), Type.Union(lhs_t, rhs_t));
			}
			result.lifetimeRelation.replaceWithMerge(this.lifetimeRelation, env.lifetimeRelation);

			return result;
		}

		/**
		 * Create a fresh copy of this environment. In fact, this operation
		 * simply increments the reference count of this environment and returns
		 * it.
		 */
		@Override
		public Environment clone() {
			count++;
			return this;
		}

		/**
		 * Decrease the reference count of this environment by one.
		 */
		public void free() {
			--count;
		}

		@Override
		public String toString() {
			return currentTypes.toString();
		}

		@Override
		public int hashCode() {
			return 31 * currentTypes.hashCode() + lambdaLifetimes.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Environment) {
				Environment r = (Environment) o;
				return currentTypes.equals(r.currentTypes) && lambdaLifetimes.equals(r.lambdaLifetimes);
			}
			return false;
		}
	}

	private static final Environment BOTTOM = new Environment();

}
