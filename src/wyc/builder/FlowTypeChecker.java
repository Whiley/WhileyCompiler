// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyc.builder;

import static wybs.lang.SyntaxError.InternalFailure;
import static wyc.lang.WhileyFile.internalFailure;
import static wyc.lang.WhileyFile.syntaxError;
import static wyil.util.ErrorMessages.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import wyautl_old.lang.Automata;
import wyautl_old.lang.Automaton;
import wybs.lang.*;
import wybs.util.*;
import wyc.lang.*;
import wyc.lang.WhileyFile.Context;
import wycc.util.ArrayUtils;
import wycc.util.Pair;
import wycc.util.Triple;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.Constant;
import wyil.lang.Modifier;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.util.TypeSystem;
import wyil.util.type.LifetimeRelation;

/**
 * Propagates type information in a <i>flow-sensitive</i> fashion from declared
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
 * <li><b>Local Propagation.</b> During this stage, types are propagated through
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
	private WhileyFile file;
	// private WhileyFile.FunctionOrMethod current;

	/**
	 * The constant cache contains a cache of expanded constant values. This is
	 * simply to prevent recomputing them every time.
	 */
	private final HashMap<NameID, Pair<Constant, Type>> constantCache = new HashMap<NameID, Pair<Constant, Type>>();

	public FlowTypeChecker(CompileTask builder) {
		this.builder = builder;
		this.typeSystem = builder.getTypeSystem();
	}

	// =========================================================================
	// WhileyFile(s)
	// =========================================================================

	public void propagate(List<WhileyFile> files) {
		for (WhileyFile wf : files) {
			propagate(wf);
		}
	}

	public void propagate(WhileyFile wf) {
		this.file = wf;

		for (WhileyFile.Declaration decl : wf.declarations) {
			try {
				if (decl instanceof WhileyFile.FunctionOrMethod) {
					propagate((WhileyFile.FunctionOrMethod) decl);
				} else if (decl instanceof WhileyFile.Type) {
					propagate((WhileyFile.Type) decl);
				} else if (decl instanceof WhileyFile.Constant) {
					propagate((WhileyFile.Constant) decl);
				}
			} catch (ResolveError e) {
				throw new SyntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()), file.getEntry(), decl, e);
			} catch (SyntaxError e) {
				throw e;
			} catch (Throwable t) {
				throw new InternalFailure(t.getMessage(), file.getEntry(), decl, t);
			}
		}
	}

	// =========================================================================
	// Declarations
	// =========================================================================

	/**
	 * Resolve types for a given type declaration. If an invariant expression is
	 * given, then we have to propagate and resolve types throughout the
	 * expression.
	 *
	 * @param td
	 *            Type declaration to check.
	 * @throws IOException
	 */
	public void propagate(WhileyFile.Type td) throws IOException {
		try {
			// First, resolve the declared syntactic type into the corresponding
			// nominal type.
			td.resolvedType = builder.toSemanticType(td.parameter.type, td);

			if (typeSystem.isSubtype(Type.T_VOID, td.resolvedType)) {
				// A non-contractive type is one which cannot accept a finite
				// values. For example, the following is a contractive type:
				//
				// type NonContractive is { NonContractive x }
				throw new SyntaxError("empty type encountered", file.getEntry(), td);
			} else if (td.invariant.size() > 0) {
				// Second, an invariant expression is given, so propagate
				// through
				// that.
				Environment environment = addDeclaredParameter(td.parameter, new Environment(), td);
				// Propagate type information through the constraint
				for (int i = 0; i != td.invariant.size(); ++i) {
					Expr invariant = propagate(td.invariant.get(i), environment, td);
					td.invariant.set(i, invariant);
				}
			}
		} catch (ResolveError err) {
			throw new SyntaxError(errorMessage(RESOLUTION_ERROR, err.getMessage()), file.getEntry(), td.parameter.type,
					err);
		}
	}

	/**
	 * Propagate and check types for a given constant declaration.
	 *
	 * @param cd
	 *            Constant declaration to check.
	 * @throws IOException
	 */
	public void propagate(WhileyFile.Constant cd) throws IOException, ResolveError {
		NameID nid = new NameID(cd.file().getEntry().id(), cd.name());
		cd.resolvedValue = resolveAsConstant(nid).first();
	}

	/**
	 * Propagate and check types for a given function or method declaration.
	 *
	 * @param fd
	 *            Function or method declaration to check.
	 * @throws IOException
	 */
	public void propagate(WhileyFile.FunctionOrMethod d) throws IOException {
		// Resolve the types of all parameters and construct an appropriate
		// environment for use in the flow-sensitive type propagation.
		Environment environment = new Environment().declareLifetimeParameters(d.lifetimeParameters);
		environment = addDeclaredParameters(d.parameters, environment, d);
		environment = addDeclaredParameters(d.returns, environment, d);
		// Resolve types for any preconditions (i.e. requires clauses) provided.
		propagateConditions(d.requires, environment, d);
		// Resolve types for any postconditions (i.e. ensures clauses) provided.
		propagateConditions(d.ensures, environment, d);

		// Resolve the overall type for the function or method.
		if (d instanceof WhileyFile.Function) {
			WhileyFile.Function f = (WhileyFile.Function) d;
			f.resolvedType = resolveAsType(f.unresolvedType(), d);
		} else {
			WhileyFile.Method m = (WhileyFile.Method) d;
			m.resolvedType = resolveAsType(m.unresolvedType(), d);
		}

		// Add the "this" lifetime
		environment = environment.startNamedBlock("this");

		// Finally, propagate type information throughout all statements in the
		// function / method body.
		Environment last = propagate(d.statements, environment, d);
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
	private void checkReturnValue(WhileyFile.FunctionOrMethod d, Environment last) {
		if (!d.hasModifier(Modifier.NATIVE) && last != BOTTOM && d.resolvedType().returns().length != 0) {
			// In this case, code reaches the end of the function or method and,
			// furthermore, that this requires a return value. To get here means
			// that there was no explicit return statement given on at least one
			// execution path.
			throw new SyntaxError("missing return statement", file.getEntry(), d);
		}
	}

	/**
	 * Propagate type information through a list of conditions, updating each
	 * one in place. The environment is cloned so as to ensure no interference.
	 *
	 * @param conditions
	 * @param environment
	 * @param context
	 */
	private void propagateConditions(List<Expr> conditions, Environment environment, Context context) {
		for (int i = 0; i != conditions.size(); ++i) {
			Expr condition = conditions.get(i);
			condition = propagate(condition, environment.clone(), context);
			conditions.set(i, condition);
		}
	}

	// =========================================================================
	// Blocks & Statements
	// =========================================================================

	/**
	 * Propagate type information in a flow-sensitive fashion through a block of
	 * statements, whilst type checking each statement and expression.
	 *
	 * @param block
	 *            Block of statements to flow sensitively type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(ArrayList<Stmt> block, Environment environment, Context context) {

		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			if (stmt instanceof Expr) {
				block.set(i, (Stmt) propagate((Expr) stmt, environment, context));
			} else {
				environment = propagate(stmt, environment, context);
			}
		}

		return environment;
	}

	/**
	 * Propagate type information in a flow-sensitive fashion through a given
	 * statement, whilst type checking it at the same time. For statements which
	 * contain other statements (e.g. if, while, etc), then this will
	 * recursively propagate type information through them as well.
	 *
	 *
	 * @param forest
	 *            Block of statements to flow-sensitively type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt stmt, Environment environment, Context context) {
		if (environment == BOTTOM) {
			throw new SyntaxError(errorMessage(UNREACHABLE_CODE), file.getEntry(), stmt);
		}
		try {
			if (stmt instanceof Stmt.VariableDeclaration) {
				return propagate((Stmt.VariableDeclaration) stmt, environment, context);
			} else if (stmt instanceof Stmt.Assign) {
				return propagate((Stmt.Assign) stmt, environment, context);
			} else if (stmt instanceof Stmt.Return) {
				return propagate((Stmt.Return) stmt, environment, context);
			} else if (stmt instanceof Stmt.IfElse) {
				return propagate((Stmt.IfElse) stmt, environment, context);
			} else if (stmt instanceof Stmt.NamedBlock) {
				return propagate((Stmt.NamedBlock) stmt, environment, context);
			} else if (stmt instanceof Stmt.While) {
				return propagate((Stmt.While) stmt, environment, context);
			} else if (stmt instanceof Stmt.Switch) {
				return propagate((Stmt.Switch) stmt, environment, context);
			} else if (stmt instanceof Stmt.DoWhile) {
				return propagate((Stmt.DoWhile) stmt, environment, context);
			} else if (stmt instanceof Stmt.Break) {
				return propagate((Stmt.Break) stmt, environment, context);
			} else if (stmt instanceof Stmt.Continue) {
				return propagate((Stmt.Continue) stmt, environment, context);
			} else if (stmt instanceof Stmt.Assert) {
				return propagate((Stmt.Assert) stmt, environment, context);
			} else if (stmt instanceof Stmt.Assume) {
				return propagate((Stmt.Assume) stmt, environment, context);
			} else if (stmt instanceof Stmt.Fail) {
				return propagate((Stmt.Fail) stmt, environment, context);
			} else if (stmt instanceof Stmt.Debug) {
				return propagate((Stmt.Debug) stmt, environment, context);
			} else if (stmt instanceof Stmt.Skip) {
				return propagate((Stmt.Skip) stmt, environment);
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
	private Environment propagate(Stmt.Assert stmt, Environment environment, Context context) throws ResolveError {
		stmt.expr = propagate(stmt.expr, environment, context);
		checkIsSubtype(Type.T_BOOL, stmt.expr, environment);
		return environment;
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
	private Environment propagate(Stmt.Assume stmt, Environment environment, Context context) throws ResolveError {
		stmt.expr = propagate(stmt.expr, environment, context);
		checkIsSubtype(Type.T_BOOL, stmt.expr, environment);
		return environment;
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
	private Environment propagate(Stmt.Fail stmt, Environment environment, Context context) {
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
	private Environment propagate(Stmt.VariableDeclaration stmt, Environment environment, Context context)
			throws IOException, ResolveError {
		// First, resolve declared type
		stmt.type = builder.toSemanticType(stmt.parameter.type, context);

		// Second, resolve type of initialiser. This must be performed before we
		// update the environment, since this expression is not allowed to refer
		// to the newly declared variable.
		if (stmt.expr != null) {
			stmt.expr = propagate(stmt.expr, environment, context);
			checkIsSubtype(stmt.type, stmt.expr, environment);
		}

		// Third, update environment accordingly. Observe that we can safely
		// assume any variable(s) are not already declared in the enclosing
		// scope because the parser checks this for us.
		environment = addDeclaredParameter(stmt.parameter, environment, context);

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
	private Environment propagate(Stmt.Assign stmt, Environment environment, Context context)
			throws IOException, ResolveError {
		// First, type check each lval that occurs on the left-hand side.
		for (int i = 0; i != stmt.lvals.size(); ++i) {
			stmt.lvals.set(i, propagate(stmt.lvals.get(i), environment, context));
		}
		// Second, type check expressions on right-hand side, and calculate the
		// number of values produced by the right-hand side. This is challenging
		// because the number of explicit rvals given can legitimately be less
		// than the number of values produced. This occurs when an invocation
		// occurs on the right-hand side has multiple return values.
		for (int i = 0; i != stmt.rvals.size(); ++i) {
			stmt.rvals.set(i, propagate(stmt.rvals.get(i), environment, context));
		}
		List<Pair<Expr, Type>> valuesProduced = calculateTypesProduced(stmt.rvals);
		// Check the number of expected values matches the number of values
		// produced by the right-hand side.
		if (stmt.lvals.size() < valuesProduced.size()) {
			throw new SyntaxError("too many values provided on right-hand side", file.getEntry(), stmt);
		} else if (stmt.lvals.size() > valuesProduced.size()) {
			throw new SyntaxError("not enough values provided on right-hand side", file.getEntry(), stmt);
		}
		// For each value produced, check that the variable being assigned
		// matches the value produced.
		for (int i = 0; i != valuesProduced.size(); ++i) {
			Expr.LVal lval = stmt.lvals.get(i);
			Pair<Expr, Type> rval = valuesProduced.get(i);
			checkIsSubtype(getWriteableType(lval, environment), rval.second(), rval.first(), environment);
		}

		return environment;
	}

	/**
	 * Determine the maximal type that can be written to this given lval. For
	 * example, if the lval is just a variable then the declared type is the
	 * writeable type.
	 *
	 * @param lv
	 * @param environment
	 * @return
	 */
	private Type getWriteableType(Expr.LVal lv, Environment environment) {
		if (lv instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable v = (Expr.AssignedVariable) lv;
			return environment.getDeclaredType(v.var);
		} else if (lv instanceof Expr.Dereference) {
			Expr.Dereference pa = (Expr.Dereference) lv;
			return pa.srcType.getWriteableElementType();
		} else if (lv instanceof Expr.IndexOf) {
			Expr.IndexOf la = (Expr.IndexOf) lv;
			return la.srcType.getWriteableElementType();
		} else if (lv instanceof Expr.FieldAccess) {
			Expr.FieldAccess la = (Expr.FieldAccess) lv;
			return la.srcType.getWriteableFieldType(la.name);
		} else {
			throw new InternalFailure("unknown lval: " + lv.getClass().getName(), file.getEntry(), lv);
		}
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
	private Environment propagate(Stmt.Break stmt, Environment environment, Context context) {
		// FIXME: need to propagate environment to the break destination
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
	private Environment propagate(Stmt.Continue stmt, Environment environment, Context context) {
		// FIXME: need to propagate environment to the continue destination
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
	private Environment propagate(Stmt.Debug stmt, Environment environment, Context context) throws ResolveError {
		stmt.expr = propagate(stmt.expr, environment, context);
		checkIsSubtype(Type.Array(Type.T_INT), stmt.expr, environment);
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
	private Environment propagate(Stmt.DoWhile stmt, Environment environment, Context context) throws ResolveError {

		// Type loop body
		environment = propagate(stmt.body, environment, context);

		// Type invariants
		List<Expr> stmt_invariants = stmt.invariants;
		for (int i = 0; i != stmt_invariants.size(); ++i) {
			Expr invariant = stmt_invariants.get(i);
			invariant = propagate(invariant, environment, context);
			stmt_invariants.set(i, invariant);
			checkIsSubtype(Type.T_BOOL, invariant, environment);
		}

		// Type condition assuming its false to represent the terminated loop.
		// This is important if the condition contains a type test, as we'll
		// know that doesn't hold here.
		Pair<Expr, Environment> p = propagateCondition(stmt.condition, false, environment, context);
		stmt.condition = p.first();
		return p.second();
	}

	/**
	 * Type check an if-statement. To do this, we propagate the environment
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
	private Environment propagate(Stmt.IfElse stmt, Environment environment, Context context) throws ResolveError {

		// First, check condition and apply variable retypings.
		Pair<Expr, Environment> p1, p2;

		p1 = propagateCondition(stmt.condition, true, environment.clone(), context);
		p2 = propagateCondition(stmt.condition, false, environment.clone(), context);
		stmt.condition = p1.first();

		Environment trueEnvironment = p1.second();
		Environment falseEnvironment = p2.second();

		// Second, update environments for true and false branches
		if (stmt.trueBranch != null && stmt.falseBranch != null) {
			trueEnvironment = propagate(stmt.trueBranch, trueEnvironment, context);
			falseEnvironment = propagate(stmt.falseBranch, falseEnvironment, context);
		} else if (stmt.trueBranch != null) {
			trueEnvironment = propagate(stmt.trueBranch, trueEnvironment, context);
		} else if (stmt.falseBranch != null) {
			trueEnvironment = environment;
			falseEnvironment = propagate(stmt.falseBranch, falseEnvironment, context);
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
	private Environment propagate(Stmt.Return stmt, Environment environment, Context context)
			throws IOException, ResolveError {
		List<Expr> stmt_returns = stmt.returns;
		for (int i = 0; i != stmt_returns.size(); ++i) {
			stmt_returns.set(i, propagate(stmt_returns.get(i), environment, context));
		}
		List<Pair<Expr, Type>> stmt_types = calculateTypesProduced(stmt_returns);
		// FIXME: this is less than ideal
		Type[] current_returns = ((WhileyFile.FunctionOrMethod) context).resolvedType().returns();

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
	private Environment propagate(Stmt.Skip stmt, Environment environment) {
		return environment;
	}

	/**
	 * Type check a <code>switch</code> statement. This is similar, in some
	 * ways, to the handling of if-statements except that we have n code blocks
	 * instead of just two. Therefore, we propagate type information through
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
	private Environment propagate(Stmt.Switch stmt, Environment environment, Context context) throws IOException {

		stmt.expr = propagate(stmt.expr, environment, context);

		Environment finalEnv = null;
		boolean hasDefault = false;

		for (Stmt.Case c : stmt.cases) {

			// first, resolve the constants

			ArrayList<Constant> values = new ArrayList<Constant>();
			for (Expr e : c.expr) {
				values.add(resolveAsConstant(e, context).first());
			}
			c.constants = values;

			// second, propagate through the statements

			Environment localEnv = environment.clone();
			localEnv = propagate(c.stmts, localEnv, context);

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
	private Environment propagate(Stmt.NamedBlock stmt, Environment environment, Context context) {
		environment = environment.startNamedBlock(stmt.name);
		environment = propagate(stmt.body, environment, context);
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
	private Environment propagate(Stmt.While stmt, Environment environment, Context context) throws ResolveError {
		// Type condition assuming its false to represent the terminated loop.
		// This is important if the condition contains a type test, as we'll
		// know that doesn't hold here.
		Pair<Expr, Environment> p1 = propagateCondition(stmt.condition, true, environment.clone(), context);
		Pair<Expr, Environment> p2 = propagateCondition(stmt.condition, false, environment.clone(), context);
		stmt.condition = p1.first();

		Environment trueEnvironment = p1.second();
		Environment falseEnvironment = p2.second();

		// Type loop invariant(s)
		List<Expr> stmt_invariants = stmt.invariants;
		for (int i = 0; i != stmt_invariants.size(); ++i) {
			Expr invariant = stmt_invariants.get(i);
			invariant = propagate(invariant, environment, context);
			stmt_invariants.set(i, invariant);
			checkIsSubtype(Type.T_BOOL, invariant, environment);
		}

		// Type loop body
		propagate(stmt.body, trueEnvironment, context);

		// Done
		return falseEnvironment;
	}

	// =========================================================================
	// LVals
	// =========================================================================

	private Expr.LVal propagate(Expr.LVal lval, Environment environment, Context context) {
		try {
			if (lval instanceof Expr.AbstractVariable) {
				Expr.AbstractVariable av = (Expr.AbstractVariable) lval;
				Type p = environment.getCurrentType(av.var);
				if (p == null) {
					throw new SyntaxError(errorMessage(UNKNOWN_VARIABLE), file.getEntry(), lval);
				}
				Expr.AssignedVariable lv = new Expr.AssignedVariable(av.var, av.attributes());
				lv.type = p;
				return lv;
			} else if (lval instanceof Expr.Dereference) {
				Expr.Dereference pa = (Expr.Dereference) lval;
				Expr.LVal src = propagate((Expr.LVal) pa.src, environment, context);
				pa.src = src;
				pa.srcType = expandAsEffectiveReference(src, context);
				return pa;
			} else if (lval instanceof Expr.IndexOf) {
				// this indicates either a list, string or dictionary update
				Expr.IndexOf ai = (Expr.IndexOf) lval;
				Expr.LVal src = propagate((Expr.LVal) ai.src, environment, context);
				Expr index = propagate(ai.index, environment, context);
				ai.src = src;
				ai.index = index;
				ai.srcType = expandAsEffectiveArray(src, context);
				return ai;
			} else if (lval instanceof Expr.FieldAccess) {
				// this indicates a record update
				Expr.FieldAccess ad = (Expr.FieldAccess) lval;
				Expr.LVal src = propagate((Expr.LVal) ad.src, environment, context);
				Expr.FieldAccess ra = new Expr.FieldAccess(src, ad.name, ad.attributes());
				Type.EffectiveRecord srcType = expandAsEffectiveRecord(src, context);
				if (!srcType.hasField(ra.name)) {
					throw new SyntaxError(errorMessage(RECORD_MISSING_FIELD, ra.name), file.getEntry(), lval);
				}
				ra.srcType = srcType;
				return ra;
			}
		} catch (SyntaxError e) {
			throw e;
		} catch (Throwable e) {
			throw new InternalFailure(e.getMessage(), file.getEntry(), lval, e);
		}
		throw new InternalFailure("unknown lval: " + lval.getClass().getName(), file.getEntry(), lval);
	}

	// =========================================================================
	// Condition
	// =========================================================================

	/**
	 * <p>
	 * Propagate type information through an expression being used as a
	 * condition, whilst checking it is well-typed at the same time. When used
	 * as a condition (e.g. of an if-statement) an expression may update the
	 * environment in accordance with any type tests used within. This is
	 * important to ensure that variables are retyped in e.g. if-statements. For
	 * example:
	 * </p>
	 *
	 * <pre>
	 * if x is int && x >= 0
	 *    // x is int
	 * else:
	 *    //
	 * </pre>
	 * <p>
	 * Here, the if-condition must update the type of x in the true branch, but
	 * *cannot* update the type of x in the false branch.
	 * </p>
	 * <p>
	 * To handle conditions on the false branch, this function uses a sign flag
	 * rather than expanding them using DeMorgan's laws (for efficiency). When
	 * determining type for the false branch, the sign flag is initially false.
	 * This prevents falsely concluding that e.g. "x is int" holds in the false
	 * branch.
	 * </p>
	 *
	 * @param expr
	 *            Condition expression to type check and propagate through
	 * @param sign
	 *            Indicates how expression should be treated. If true, then
	 *            expression is treated "as is"; if false, then expression
	 *            should be treated as negated
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 * @throws ResolveError
	 *             If a named type within this condition cannot be resolved
	 *             within the enclosing project.
	 */
	public Pair<Expr, Environment> propagateCondition(Expr expr, boolean sign, Environment environment, Context context)
			throws ResolveError {

		// Split up into the compound and non-compound forms.

		if (expr instanceof Expr.UnOp) {
			return propagateCondition((Expr.UnOp) expr, sign, environment, context);
		} else if (expr instanceof Expr.BinOp) {
			return propagateCondition((Expr.BinOp) expr, sign, environment, context);
		} else {
			// For non-compound forms, can just default back to the base rules
			// for general expressions.
			expr = propagate(expr, environment, context);
			checkIsSubtype(Type.T_BOOL, expr, context, environment);
			return new Pair<Expr, Environment>(expr, environment);
		}
	}

	/**
	 * <p>
	 * Propagate type information through a unary expression being used as a
	 * condition and, in fact, only logical not is syntactically valid here.
	 * </p>
	 *
	 * @param expr
	 *            Condition expression to type check and propagate through
	 * @param sign
	 *            Indicates how expression should be treated. If true, then
	 *            expression is treated "as is"; if false, then expression
	 *            should be treated as negated
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 * @throws ResolveError
	 *             If a named type within this condition cannot be resolved
	 *             within the enclosing project.
	 */
	private Pair<Expr, Environment> propagateCondition(Expr.UnOp expr, boolean sign, Environment environment,
			Context context) throws ResolveError {
		Expr.UnOp uop = expr;

		// Check whether we have logical not

		if (uop.op == Expr.UOp.NOT) {
			Pair<Expr, Environment> p = propagateCondition(uop.mhs, !sign, environment, context);
			uop.mhs = p.first();
			checkIsSubtype(Type.T_BOOL, uop.mhs, context, environment);
			uop.type = Type.T_BOOL;
			return new Pair<Expr, Environment>(uop, p.second());
		} else {
			// Nothing else other than logical not is valid at this point.
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, expr);
			return deadCode(expr);
		}
	}

	/**
	 * <p>
	 * Propagate type information through a binary expression being used as a
	 * condition. In this case, only logical connectives ("&&", "||", "^") and
	 * comparators (e.g. "==", "<=", etc) are permitted here.
	 * </p>
	 *
	 * @param expr
	 *            Condition expression to type check and propagate through
	 * @param sign
	 *            Indicates how expression should be treated. If true, then
	 *            expression is treated "as is"; if false, then expression
	 *            should be treated as negated
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 * @throws ResolveError
	 *             If a named type within this condition cannot be resolved
	 *             within the enclosing project.
	 */
	private Pair<Expr, Environment> propagateCondition(Expr.BinOp bop, boolean sign, Environment environment,
			Context context) throws ResolveError {
		Expr.BOp op = bop.op;

		// Split into the two broard cases: logical connectives and primitives.

		switch (op) {
		case AND:
		case OR:
		case XOR:
			return resolveNonLeafCondition(bop, sign, environment, context);
		case EQ:
		case NEQ:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case IS:
			return resolveLeafCondition(bop, sign, environment, context);
		default:
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, bop);
			return null; // dead code
		}
	}

	/**
	 * <p>
	 * Propagate type information through a binary expression being used as a
	 * logical connective ("&&", "||", "^").
	 * </p>
	 *
	 * @param bop
	 *            Binary operator for this expression.
	 * @param sign
	 *            Indicates how expression should be treated. If true, then
	 *            expression is treated "as is"; if false, then expression
	 *            should be treated as negated
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 * @throws ResolveError
	 *             If a named type within this condition cannot be resolved
	 *             within the enclosing project.
	 */
	private Pair<Expr, Environment> resolveNonLeafCondition(Expr.BinOp bop, boolean sign, Environment environment,
			Context context) throws ResolveError {
		Expr.BOp op = bop.op;
		Pair<Expr, Environment> p;
		boolean followOn = (sign && op == Expr.BOp.AND) || (!sign && op == Expr.BOp.OR);

		if (followOn) {
			// In this case, the environment feeds directly from the result of
			// propagating through the lhs into the rhs, and then into the
			// result of this expression. This means that updates to the
			// environment by either the lhs or rhs are visible outside of this
			// method.
			p = propagateCondition(bop.lhs, sign, environment.clone(), context);
			bop.lhs = p.first();
			p = propagateCondition(bop.rhs, sign, p.second(), context);
			bop.rhs = p.first();
			environment = p.second();
		} else {
			// We could do better here
			p = propagateCondition(bop.lhs, sign, environment.clone(), context);
			bop.lhs = p.first();
			Environment local = p.second();
			// Recompute the lhs assuming that it is false. This is necessary to
			// generate the right environment going into the rhs, which is only
			// evaluated if the lhs is false. For example:
			//
			// if(e is int && e > 0):
			// //
			// else:
			// // <-
			//
			// In the false branch, we're determing the environment for
			// !(e is int && e > 0). This becomes !(e is int) || (e <= 0) where
			// on the rhs we require (e is int).
			p = propagateCondition(bop.lhs, !sign, environment.clone(), context);
			// Note, the following is intentional since we're specifically
			// considering the case where the lhs was false, and this case is
			// true.
			p = propagateCondition(bop.rhs, sign, p.second(), context);
			bop.rhs = p.first();
			environment = local.merge(local.keySet(), p.second());
		}

		checkIsSubtype(Type.T_BOOL, bop.lhs, context, environment);
		checkIsSubtype(Type.T_BOOL, bop.rhs, context, environment);
		bop.srcType = Type.T_BOOL;

		return new Pair<Expr, Environment>(bop, environment);
	}

	/**
	 * <p>
	 * Propagate type information through a binary expression being used as a
	 * comparators (e.g. "==", "<=", etc).
	 * </p>
	 *
	 * @param bop
	 *            Binary operator for this expression.
	 * @param sign
	 *            Indicates how expression should be treated. If true, then
	 *            expression is treated "as is"; if false, then expression
	 *            should be treated as negated
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 * @throws ResolveError
	 *             If a named type within this condition cannot be resolved
	 *             within the enclosing project.
	 */
	private Pair<Expr, Environment> resolveLeafCondition(Expr.BinOp bop, boolean sign, Environment environment,
			Context context) throws ResolveError {
		Expr.BOp op = bop.op;

		Expr lhs = propagate(bop.lhs, environment, context);
		Expr rhs = propagate(bop.rhs, environment, context);
		bop.lhs = lhs;
		bop.rhs = rhs;

		Type lhsType = lhs.result();
		Type rhsType = rhs.result();

		switch (op) {
		case IS:
			// this one is slightly more difficult. In the special case that
			// we have a type constant on the right-hand side then we want
			// to check that it makes sense. Otherwise, we just check that
			// it has type meta.

			if (rhs instanceof Expr.TypeVal) {
				// yes, right-hand side is a constant
				Expr.TypeVal tv = (Expr.TypeVal) rhs;
				Type glbForFalseBranch = Type.Intersection(lhs.result(), Type.Negation(tv.type));
				Type glbForTrueBranch = Type.Intersection(lhs.result(), tv.type);
				if (typeSystem.isEmpty(glbForFalseBranch)) {
					// DEFINITE TRUE CASE
					syntaxError(errorMessage(BRANCH_ALWAYS_TAKEN), context, bop);
				} else if (typeSystem.isEmpty(glbForTrueBranch)) {
					// DEFINITE FALSE CASE
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsType, tv.type), context, bop);
				}

				// Finally, if the lhs is local variable then update its
				// type in the resulting environment.
				if (lhs instanceof Expr.LocalVariable) {
					Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
					Type newType;
					if (sign) {
						newType = glbForTrueBranch;
					} else {
						newType = glbForFalseBranch;
					}
					environment = environment.update(lv.var, newType);
				}
			} else {
				// In this case, we can't update the type of the lhs since
				// we don't know anything about the rhs. It may be possible
				// to support bounds here in order to do that, but frankly
				// that's future work :)
				checkIsSubtype(Type.T_META, rhs, context, environment);
			}

			bop.srcType = lhs.result();
			break;
		case LT:
		case LTEQ:
		case GTEQ:
		case GT:
			checkSuptypes(lhs, context, environment, Type.T_INT);
			checkSuptypes(rhs, context, environment, Type.T_INT);
			//
			if (typeSystem.isSubtype(lhsType, rhsType) || typeSystem.isSubtype(rhsType, lhsType)) {
				bop.srcType = lhs.result();
			} else {
				throw new SyntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsType, rhsType), file.getEntry(), bop);
			}
			break;
		case NEQ:
			// following is a sneaky trick for the special case below
			sign = !sign;
		case EQ:

			// first, check for special case of e.g. x != null. This is then
			// treated the same as !(x is null)

			if (lhs instanceof Expr.LocalVariable && rhs instanceof Expr.Constant
					&& ((Expr.Constant) rhs).value == Constant.Null) {
				// bingo, special case
				Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
				Type newType;
				Type glb = Type.Intersection(lhs.result(), Type.T_NULL);
				if (typeSystem.isEmpty(glb)) {
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhs.result(), Type.T_NULL), context, bop);
					return null;
				} else if (sign) {
					newType = glb;
				} else {
					newType = Type.Intersection(lhs.result(), Type.Negation(Type.T_NULL));
				}
				bop.srcType = lhs.result();
				environment = environment.update(lv.var, newType);
			} else {
				// handle general case
				if (typeSystem.isSubtype(lhsType, rhsType, environment.getLifetimeRelation())) {
					bop.srcType = lhs.result();
				} else if (typeSystem.isSubtype(rhsType, lhsType, environment.getLifetimeRelation())) {
					bop.srcType = rhs.result();
				} else {
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsType, rhsType), context, bop);
					return null; // dead code
				}
			}
		}

		return new Pair<Expr, Environment>(bop, environment);
	}

	// =========================================================================
	// Expressions
	// =========================================================================

	/**
	 * Propagate types through a given expression, whilst checking that it is
	 * well typed. In this case, any use of a runtime type test cannot effect
	 * callers of this function.
	 *
	 * @param expr
	 *            Expression to propagate types through.
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 */
	public Expr propagate(Expr expr, Environment environment, Context context) {

		try {
			if (expr instanceof Expr.BinOp) {
				return propagate((Expr.BinOp) expr, environment, context);
			} else if (expr instanceof Expr.UnOp) {
				return propagate((Expr.UnOp) expr, environment, context);
			} else if (expr instanceof Expr.Quantifier) {
				return propagate((Expr.Quantifier) expr, environment, context);
			} else if (expr instanceof Expr.Constant) {
				return propagate((Expr.Constant) expr, environment, context);
			} else if (expr instanceof Expr.Cast) {
				return propagate((Expr.Cast) expr, environment, context);
			} else if (expr instanceof Expr.ConstantAccess) {
				return propagate((Expr.ConstantAccess) expr, environment, context);
			} else if (expr instanceof Expr.FieldAccess) {
				return propagate((Expr.FieldAccess) expr, environment, context);
			} else if (expr instanceof Expr.AbstractFunctionOrMethod) {
				return propagate((Expr.AbstractFunctionOrMethod) expr, environment, context);
			} else if (expr instanceof Expr.AbstractInvoke) {
				return propagate((Expr.AbstractInvoke) expr, environment, context);
			} else if (expr instanceof Expr.AbstractIndirectInvoke) {
				return propagate((Expr.AbstractIndirectInvoke) expr, environment, context);
			} else if (expr instanceof Expr.IndexOf) {
				return propagate((Expr.IndexOf) expr, environment, context);
			} else if (expr instanceof Expr.Lambda) {
				return propagate((Expr.Lambda) expr, environment, context);
			} else if (expr instanceof Expr.LocalVariable) {
				return propagate((Expr.LocalVariable) expr, environment, context);
			} else if (expr instanceof Expr.ArrayInitialiser) {
				return propagate((Expr.ArrayInitialiser) expr, environment, context);
			} else if (expr instanceof Expr.ArrayGenerator) {
				return propagate((Expr.ArrayGenerator) expr, environment, context);
			} else if (expr instanceof Expr.Dereference) {
				return propagate((Expr.Dereference) expr, environment, context);
			} else if (expr instanceof Expr.Record) {
				return propagate((Expr.Record) expr, environment, context);
			} else if (expr instanceof Expr.New) {
				return propagate((Expr.New) expr, environment, context);
			} else if (expr instanceof Expr.TypeVal) {
				return propagate((Expr.TypeVal) expr, environment, context);
			}
		} catch (ResolveError e) {
			syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()), context, expr, e);
		} catch (SyntaxError e) {
			throw e;
		} catch (Throwable e) {
			internalFailure(e.getMessage(), context, expr, e);
			return null; // dead code
		}
		internalFailure("unknown expression: " + expr.getClass().getName(), context, expr);
		return null; // dead code
	}

	private Expr propagate(Expr.BinOp expr, Environment environment, Context context) throws IOException, ResolveError {

		// TODO: split binop into arithmetic and conditional operators. This
		// would avoid the following case analysis since conditional binary
		// operators and arithmetic binary operators actually behave quite
		// differently.

		switch (expr.op) {
		case AND:
		case OR:
		case XOR:
		case EQ:
		case NEQ:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case IS:
			return propagateCondition(expr, true, environment, context).first();
		}

		Expr lhs = propagate(expr.lhs, environment, context);
		Expr rhs = propagate(expr.rhs, environment, context);
		expr.lhs = lhs;
		expr.rhs = rhs;
		Type lhsType = lhs.result();
		Type rhsType = rhs.result();

		Type srcType;

		switch (expr.op) {
		case IS:
		case AND:
		case OR:
		case XOR:
			return propagateCondition(expr, true, environment, context).first();
		case BITWISEAND:
		case BITWISEOR:
		case BITWISEXOR:
			checkIsSubtype(Type.T_BYTE, lhs, context, environment);
			checkIsSubtype(Type.T_BYTE, rhs, context, environment);
			srcType = Type.T_BYTE;
			break;
		case LEFTSHIFT:
		case RIGHTSHIFT:
			checkIsSubtype(Type.T_BYTE, lhs, context, environment);
			checkIsSubtype(Type.T_INT, rhs, context, environment);
			srcType = Type.T_BYTE;
			break;
		case RANGE:
			checkIsSubtype(Type.T_INT, lhs, context, environment);
			checkIsSubtype(Type.T_INT, rhs, context, environment);
			srcType = Type.Array(Type.T_INT);
			break;
		case REM:
			checkIsSubtype(Type.T_INT, lhs, context, environment);
			checkIsSubtype(Type.T_INT, rhs, context, environment);
			srcType = Type.T_INT;
			break;
		default:
			// all other operations go through here
			checkSuptypes(lhs, context, environment, Type.T_INT);
			checkSuptypes(rhs, context, environment, Type.T_INT);
			//
			if (typeSystem.isSubtype(lhsType, rhsType) || typeSystem.isSubtype(rhsType, lhsType)) {
				srcType = lhsType;
			} else {
				throw new SyntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsType, rhsType), file.getEntry(), expr);
			}
		}

		expr.srcType = srcType;

		return expr;
	}

	private Expr propagate(Expr.UnOp expr, Environment environment, Context context) throws IOException, ResolveError {

		if (expr.op == Expr.UOp.NOT) {
			// hand off to special method for conditions
			return propagateCondition(expr, true, environment, context).first();
		}

		Expr src = propagate(expr.mhs, environment, context);
		expr.mhs = src;

		switch (expr.op) {
		case NEG:
			checkSuptypes(src, context, environment, Type.T_INT);
			break;
		case INVERT:
			checkIsSubtype(Type.T_BYTE, src, context, environment);
			break;
		case ARRAYLENGTH: {
			expr.type = expandAsEffectiveArray(expr.mhs, context);
			return expr;
		}
		default:
			internalFailure("unknown operator: " + expr.op.getClass().getName(), context, expr);
		}

		expr.type = src.result();

		return expr;
	}

	private Expr propagate(Expr.Quantifier expr, Environment environment, Context context)
			throws IOException, ResolveError {

		ArrayList<Triple<String, Expr, Expr>> sources = expr.sources;
		Environment local = environment.clone();
		for (int i = 0; i != sources.size(); ++i) {
			Triple<String, Expr, Expr> p = sources.get(i);
			Expr start = propagate(p.second(), local, context);
			Expr end = propagate(p.third(), local, context);
			sources.set(i, new Triple<String, Expr, Expr>(p.first(), start, end));
			checkIsSubtype(Type.T_INT, start, context, environment);
			local = local.declare(p.first(), Type.T_INT, Type.T_INT);
		}

		if (expr.condition != null) {
			expr.condition = propagate(expr.condition, local, context);
		}

		expr.type = Type.T_BOOL;

		local.free();

		return expr;
	}

	private Expr propagate(Expr.Constant expr, Environment environment, Context context) {
		return expr;
	}

	private Expr propagate(Expr.Cast c, Environment environment, Context context) throws IOException, ResolveError {
		c.expr = propagate(c.expr, environment, context);
		c.type = builder.toSemanticType(c.unresolvedType, context);
		Type from = c.expr.result();
		Type to = c.type;
		if (!typeSystem.isExplicitCoerciveSubtype(to, from, environment.getLifetimeRelation())) {
			syntaxError(errorMessage(SUBTYPE_ERROR, to, from), context, c);
		}
		return c;
	}

	private Expr propagate(Expr.AbstractFunctionOrMethod expr, Environment environment, Context context)
			throws IOException, ResolveError {

		if (expr instanceof Expr.FunctionOrMethod) {
			return expr;
		}

		Triple<NameID, Type.FunctionOrMethod, List<String>> p;

		if (expr.paramTypes != null) {
			ArrayList<Type> paramTypes = new ArrayList<Type>();
			for (SyntacticType t : expr.paramTypes) {
				paramTypes.add(builder.toSemanticType(t, context));
			}
			// FIXME: clearly a bug here in the case of message reference
			p = resolveAsFunctionOrMethod(expr.name, paramTypes, expr.lifetimeParameters, context, environment);
		} else {
			p = resolveAsFunctionOrMethod(expr.name, context, environment);
		}

		expr = new Expr.FunctionOrMethod(p.first(), expr.paramTypes, p.third, expr.attributes());
		expr.type = p.second();
		return expr;
	}

	private Expr propagate(Expr.Lambda expr, Environment environment, Context context)
			throws IOException, ResolveError {
		environment = environment.startLambda(expr.contextLifetimes, expr.lifetimeParameters);
		List<WhileyFile.Parameter> expr_parameters = expr.parameters;
		Type[] nomParameterTypes = new Type[expr_parameters.size()];

		for (int i = 0; i != expr_parameters.size(); ++i) {
			WhileyFile.Parameter p = expr_parameters.get(i);
			Type n = builder.toSemanticType(p.type, context);
			nomParameterTypes[i] = n;
			// Now, update the environment to include those declared variables
			String var = p.name();
			if (environment.containsKey(var)) {
				syntaxError(errorMessage(VARIABLE_ALREADY_DEFINED, var), context, p);
			}
			environment = environment.declare(var, n, n);
		}

		Type[] nomReturnTypes;
		expr.body = propagate(expr.body, environment, context);
		if (expr.body instanceof Expr.Multi) {
			Expr.Multi m = (Expr.Multi) expr.body;
			List<Type> returns = m.returns();
			nomReturnTypes = new Type[returns.size()];
			for (int i = 0; i != returns.size(); ++i) {
				nomReturnTypes[i] = returns.get(i);
			}
		} else {
			Type result = expr.body.result();
			nomReturnTypes = new Type[] { result };
		}

		Type type;
		if (Exprs.isPure(expr.body, context)) {
			type = Type.Function(nomParameterTypes, nomReturnTypes);
		} else {
			type = Type.Method(expr.lifetimeParameters, expr.contextLifetimes, nomParameterTypes, nomReturnTypes);
		}

		expr.type = expandAsEffectiveFunctionOrMethod(type, expr, context);

		return expr;
	}

	private Expr propagate(Expr.AbstractIndirectInvoke expr, Environment environment, Context context)
			throws IOException, ResolveError {

		// We can only invoke functions and methods
		expr.src = propagate(expr.src, environment, context);
		Type.FunctionOrMethod funType = expandAsEffectiveFunctionOrMethod(expr.src, context);
		if (funType == null) {
			syntaxError("function or method type expected", context, expr.src);
		}

		// Do we have matching argument count?
		Type[] paramTypes = funType.params();
		ArrayList<Expr> exprArgs = expr.arguments;
		if (paramTypes.length != exprArgs.size()) {
			syntaxError("insufficient arguments for function or method invocation", context, expr.src);
		}

		// resolve through arguments
		ArrayList<Type> argTypes = new ArrayList<Type>();
		for (int i = 0; i != exprArgs.size(); ++i) {
			Expr arg = propagate(exprArgs.get(i), environment, context);
			exprArgs.set(i, arg);
			argTypes.add(arg.result());
		}

		if (funType instanceof Type.Function) {
			// Check parameter types
			for (int i = 0; i != exprArgs.size(); ++i) {
				Type pt = paramTypes[i];
				checkIsSubtype(pt, exprArgs.get(i), context, environment);
			}
			//
			Expr.IndirectFunctionCall ifc = new Expr.IndirectFunctionCall(expr.src, exprArgs, expr.attributes());
			ifc.functionType = (Type.Function) funType;
			return ifc;
		} else {
			// Handle lifetime arguments
			Type.Method methType = (Type.Method) funType;
			List<String> lifetimeParameters = Arrays.asList(methType.lifetimeParams());
			List<String> lifetimeArguments = expr.lifetimeArguments;
			if (lifetimeArguments == null) {
				// First consider the case where no lifetime arguments are
				// specified.
				if (lifetimeParameters.isEmpty()) {
					// No lifetime arguments needed!
					lifetimeArguments = Collections.emptyList();
				} else {
					// We have to guess proper lifetime arguments.
					List<Type> rawArgTypes = stripType(argTypes);
					List<ValidCandidate> validCandidates = new ArrayList<ValidCandidate>();
					guessLifetimeArguments(extractLifetimesFromArguments(rawArgTypes), lifetimeParameters,
							Arrays.asList(funType.params()), rawArgTypes, null, // don't
																				// need
																				// a
																				// name
																				// id
							funType, validCandidates, environment);

					if (validCandidates.isEmpty()) {
						// We were not able to guess lifetime arguments
						syntaxError("no lifetime arguments specified and unable to infer them", context, expr.src);
					}
					if (validCandidates.size() == 1) {
						// All right, we found proper lifetime arguments.
						// Note that at this point we indeed have a method
						// (not a function), because functions don't have
						// lifetime parameters.
						Expr.IndirectMethodCall imc = new Expr.IndirectMethodCall(expr.src, exprArgs,
								validCandidates.get(0).lifetimeArguments, expr.attributes());
						imc.methodType = (Type.Method) funType;
						return imc;
					}

					// Arriving here means we have more than one possible
					// solution. That is an ambiguity error, but we're nice and
					// also print all solutions.
					StringBuilder msg = new StringBuilder(
							"no lifetime arguments specified and unable to infer a unique solution");
					List<String> solutions = new ArrayList<String>(validCandidates.size());
					for (ValidCandidate vc : validCandidates) {
						solutions.add(vc.lifetimeArguments.toString());
					}
					Collections.sort(solutions); // make error message
													// deterministic!
					for (String s : solutions) {
						msg.append("\nfound solution: ");
						msg.append(s);
					}
					syntaxError(msg.toString(), context, expr.src);
				}
			}
			if (lifetimeParameters.size() != lifetimeArguments.size()) {
				// Lifetime arguments specified, but number doesn't match
				syntaxError("insufficient lifetime arguments for method invocation", context, expr.src);
			}

			// Check argument types with respect to specified lifetime arguments
			Map<String, String> substitution = buildSubstitution(lifetimeParameters, lifetimeArguments);
			for (int i = 0; i != exprArgs.size(); ++i) {
				Type pt = paramTypes[i];
				Expr arg = propagate(exprArgs.get(i), environment, context);
				checkIsSubtype(applySubstitution(substitution, pt), arg, context, environment);
				exprArgs.set(i, arg);
			}

			Expr.IndirectMethodCall imc = new Expr.IndirectMethodCall(expr.src, exprArgs, lifetimeArguments,
					expr.attributes());
			imc.methodType = methType;
			return imc;
		}
	}

	private Expr propagate(Expr.AbstractInvoke expr, Environment environment, Context context)
			throws IOException, ResolveError {

		// first, resolve through receiver and parameters.

		Path.ID qualification = expr.qualification;
		ArrayList<Expr> exprArgs = expr.arguments;
		ArrayList<String> lifetimeArgs = expr.lifetimeArguments;
		ArrayList<Type> paramTypes = new ArrayList<Type>();
		for (int i = 0; i != exprArgs.size(); ++i) {
			Expr arg = propagate(exprArgs.get(i), environment, context);
			exprArgs.set(i, arg);
			paramTypes.add(arg.result());
		}

		// second, determine the fully qualified name of this function based on
		// the given function name and any supplied qualifications.
		ArrayList<String> qualifications = new ArrayList<String>();
		if (expr.qualification != null) {
			for (String n : expr.qualification) {
				qualifications.add(n);
			}
		}
		qualifications.add(expr.name);
		NameID name = builder.resolveAsName(qualifications, context);

		// third, lookup the appropriate function or method based on the name
		// and given parameter types.
		Triple<NameID, Type.FunctionOrMethod, List<String>> triple = resolveAsFunctionOrMethod(name, paramTypes,
				lifetimeArgs, context, environment);
		if (triple.second() instanceof Type.Function) {
			Expr.FunctionCall r = new Expr.FunctionCall(name, qualification, exprArgs, expr.attributes());
			r.functionType = (Type.Function) triple.second();
			return r;
		} else {
			Expr.MethodCall r = new Expr.MethodCall(name, qualification, exprArgs, triple.third(), expr.attributes());
			r.methodType = (Type.Method) triple.second();
			return r;
		}
	}

	private Expr propagate(Expr.IndexOf expr, Environment environment, Context context)
			throws IOException, ResolveError {
		expr.src = propagate(expr.src, environment, context);
		expr.index = propagate(expr.index, environment, context);
		Type.EffectiveArray srcType = expandAsEffectiveArray(expr.src, context);

		if (srcType == null) {
			syntaxError(errorMessage(INVALID_ARRAY_EXPRESSION), context, expr.src);
		} else {
			expr.srcType = srcType;
		}

		checkIsSubtype(Type.T_INT, expr.index, context, environment);

		return expr;
	}

	private Expr propagate(Expr.LocalVariable expr, Environment environment, Context context) throws IOException {
		Type type = environment.getCurrentType(expr.var);
		expr.type = type;
		return expr;
	}

	private Expr propagate(Expr.ArrayInitialiser expr, Environment environment, Context context)
			throws IOException, ResolveError {
		Type element = Type.T_VOID;

		ArrayList<Expr> exprs = expr.arguments;
		for (int i = 0; i != exprs.size(); ++i) {
			Expr e = propagate(exprs.get(i), environment, context);
			Type t = e.result();
			exprs.set(i, e);
			element = Type.Union(t, element);
		}

		expr.type = (Type.Array) expandAsEffectiveArray(Type.Array(element), expr, context);

		return expr;
	}

	private Expr propagate(Expr.ArrayGenerator expr, Environment environment, Context context)
			throws ResolveError, IOException {
		expr.element = propagate(expr.element, environment, context);
		expr.count = propagate(expr.count, environment, context);
		expr.type = (Type.Array) expandAsEffectiveArray(Type.Array(expr.element.result()), expr, context);
		checkIsSubtype(Type.T_INT, expr.count, environment);
		return expr;
	}

	private Expr propagate(Expr.Record expr, Environment environment, Context context)
			throws IOException, ResolveError {

		HashMap<String, Expr> exprFields = expr.fields;
		ArrayList<Pair<Type, String>> fieldTypes = new ArrayList<Pair<Type, String>>();

		ArrayList<String> fields = new ArrayList<String>(exprFields.keySet());
		for (String field : fields) {
			Expr e = propagate(exprFields.get(field), environment, context);
			Type t = e.result();
			exprFields.put(field, e);
			fieldTypes.add(new Pair<Type, String>(t, field));
		}

		expr.type = (Type.Record) expandAsEffectiveRecord(Type.Record(false, fieldTypes), expr, context);

		return expr;
	}

	private Expr propagate(Expr.FieldAccess ra, Environment environment, Context context)
			throws IOException, ResolveError {
		ra.src = propagate(ra.src, environment, context);
		Type.EffectiveRecord recType = expandAsEffectiveRecord(ra.src, context);
		if (recType.hasField(ra.name)) {
			ra.srcType = recType;
			return ra;
		} else {
			syntaxError(errorMessage(RECORD_MISSING_FIELD, ra.name), context, ra);
			return deadCode(ra);
		}
	}

	private Expr propagate(Expr.ConstantAccess expr, Environment environment, Context context) throws IOException {
		// First, determine the fully qualified name of this function based on
		// the given function name and any supplied qualifications.
		ArrayList<String> qualifications = new ArrayList<String>();
		if (expr.qualification != null) {
			for (String n : expr.qualification) {
				qualifications.add(n);
			}
		}
		qualifications.add(expr.name);
		try {
			NameID name = builder.resolveAsName(qualifications, context);
			// Second, determine the value of the constant.
			Pair<Constant, Type> ct = resolveAsConstant(name);
			expr.value = ct.first();
			expr.type = ct.second();
			return expr;
		} catch (ResolveError e) {
			syntaxError(errorMessage(UNKNOWN_VARIABLE), context, expr);
			return null;
		}
	}

	private Expr propagate(Expr.Dereference expr, Environment environment, Context context)
			throws IOException, ResolveError {
		Expr src = propagate(expr.src, environment, context);
		expr.src = src;
		Type.Reference srcType = expandAsEffectiveReference(src, context);
		if (srcType == null) {
			syntaxError("invalid reference expression", context, src);
		}
		String lifetime = srcType.lifetime();
		if (!environment.canDereferenceLifetime(lifetime)) {
			syntaxError("lifetime '" + lifetime + "' cannot be dereferenced here", context, expr);
		}
		expr.srcType = srcType;
		return expr;
	}

	private Expr propagate(Expr.New expr, Environment environment, Context context) throws IOException, ResolveError {
		expr.expr = propagate(expr.expr, environment, context);
		expr.type = expandAsEffectiveReference(Type.Reference(expr.lifetime, expr.expr.result()), expr, context);
		return expr;
	}

	private Expr propagate(Expr.TypeVal expr, Environment environment, Context context)
			throws IOException, ResolveError {
		expr.type = builder.toSemanticType(expr.unresolvedType, context);
		return expr;
	}

	private List<Pair<Expr, Type>> calculateTypesProduced(List<Expr> expressions) {
		ArrayList<Pair<Expr, Type>> types = new ArrayList<Pair<Expr, Type>>();
		for (int i = 0; i != expressions.size(); ++i) {
			Expr e = expressions.get(i);
			if (e instanceof Expr.Multi) {
				// The assigned expression actually has multiple returns,
				// therefore extract them all.
				Expr.Multi me = (Expr.Multi) e;
				for (Type ret : me.returns()) {
					types.add(new Pair<Expr, Type>(e, ret));
				}
			} else {
				// The assigned rval is a simple expression which returns a
				// single value
				types.add(new Pair<Expr, Type>(e, e.result()));
			}
		}
		return types;
	}

	// =========================================================================
	// Resolve as Function or Method
	// =========================================================================

	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. To do this, it must find the function/method with the most
	 * precise type that matches the argument types.
	 *
	 * @param nid
	 * @param parameters
	 * @param lifetimeArgs
	 *            --- lifetime arguments passed on method invocation, or null if
	 *            none are passed and the compiler has to figure it out
	 * @return nameid, type, given/inferred lifetime arguments
	 * @throws IOException
	 */
	public Triple<NameID, Type.FunctionOrMethod, List<String>> resolveAsFunctionOrMethod(NameID nid,
			List<Type> parameters, List<String> lifetimeArgs, Context context, Environment environment)
					throws IOException, ResolveError {

		// The set of candidate names and types for this function or method.
		HashSet<Pair<NameID, Type.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Type.FunctionOrMethod>>();

		// First, add all valid candidates to the list without considering which
		// is the most precise.
		addCandidateFunctionsAndMethods(nid, parameters, candidates, context);

		// Second, add to narrow down the list of candidates to a single choice.
		// If this is impossible, then we have an ambiguity error.
		return selectCandidateFunctionOrMethod(nid.name(), parameters, lifetimeArgs, candidates, context, environment);
	}

	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. In this case, no argument types are given. This means that any
	 * match is returned. However, if there are multiple matches, then an
	 * ambiguity error is reported.
	 *
	 * @param name
	 *            --- function or method name whose type to determine.
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return
	 * @throws IOException
	 */
	public Triple<NameID, Type.FunctionOrMethod, List<String>> resolveAsFunctionOrMethod(String name, Context context,
			Environment environment) throws IOException, ResolveError {
		return resolveAsFunctionOrMethod(name, null, null, context, environment);
	}

	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. To do this, it must find the function/method with the most
	 * precise type that matches the argument types.
	 *
	 * @param name
	 *            --- name of function or method whose type to determine.
	 * @param parameters
	 *            --- required parameter types for the function or method.
	 * @param lifetimeArgs
	 *            --- lifetime arguments passed on method invocation, or null if
	 *            none are passed and the compiler has to figure it out
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return nameid, type, given/inferred lifetime arguments
	 * @throws IOException
	 */
	public Triple<NameID, Type.FunctionOrMethod, List<String>> resolveAsFunctionOrMethod(String name,
			List<Type> parameters, List<String> lifetimeArgs, Context context, Environment environment)
					throws IOException, ResolveError {

		HashSet<Pair<NameID, Type.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Type.FunctionOrMethod>>();
		// first, try to find the matching message
		for (WhileyFile.Import imp : context.imports()) {
			String impName = imp.name;
			if (impName == null || impName.equals(name) || impName.equals("*")) {
				Trie filter = imp.filter;
				if (impName == null) {
					// import name is null, but it's possible that a module of
					// the given name exists, in which case any matching names
					// are automatically imported.
					filter = filter.parent().append(name);
				}
				for (Path.ID mid : builder.imports(filter)) {
					NameID nid = new NameID(mid, name);
					addCandidateFunctionsAndMethods(nid, parameters, candidates, context);
				}
			}
		}

		return selectCandidateFunctionOrMethod(name, parameters, lifetimeArgs, candidates, context, environment);
	}

	/**
	 * @param f1_params
	 * @param f2_params
	 * @param environment
	 * @return whether f2_params are strict subtypes of f1_params
	 * @throws ResolveError
	 */
	private boolean paramStrictSubtypes(List<Type> f1_params, List<Type> f2_params, Environment environment)
			throws ResolveError {
		if (f1_params.size() == f2_params.size()) {
			boolean allEquivalent = true;
			for (int i = 0; i != f1_params.size(); ++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);
				if (!typeSystem.isSubtype(f1_param, f2_param, environment.getLifetimeRelation())) {
					return false;
				}
				allEquivalent &= typeSystem.isSubtype(f2_param, f1_param, environment.getLifetimeRelation());
			}
			// This function returns true if the parameters are a strict
			// subtype. Therefore, if they are all equivalent it must return false.
			return !allEquivalent;
		}
		return false;
	}

	private String parameterString(List<Type> paramTypes) {
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

	private String foundCandidatesString(Collection<Pair<NameID, Type.FunctionOrMethod>> candidates) {
		ArrayList<String> candidateStrings = new ArrayList<String>();
		for (Pair<NameID, Type.FunctionOrMethod> c : candidates) {
			candidateStrings.add(c.first() + " : " + c.second());
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
	 * Extract all lifetime names from the types in the given list.
	 *
	 * We just walk through the type automata and collect the lifetime for each
	 * encountered reference.
	 *
	 * The result set will always contain the default lifetime "*".
	 *
	 * @param types
	 *            the types to get the lifetimes from
	 * @return a set of all extracted lifetime names, without "*"
	 */
	private List<String> extractLifetimesFromArguments(Iterable<Type> types) {
		Set<String> result = new HashSet<String>();
		for (Type t : types) {
			extractLifetimes(t, result);
		}
		result.add("*");
		return new ArrayList<String>(result);
	}

	private void extractLifetimes(Type type, Set<String> lifetimes) {
		if (type instanceof Type.Leaf) {
			return;
		} else if (type instanceof Type.Array) {
			Type.Array t = (Type.Array) type;
			extractLifetimes(t.element(), lifetimes);
		} else if (type instanceof Type.Record) {
			Type.Record t = (Type.Record) type;
			for (String name : t.getFieldNames()) {
				extractLifetimes(t.getField(name), lifetimes);
			}
		} else if (type instanceof Type.Reference) {
			Type.Reference t = (Type.Reference) type;
			extractLifetimes(t.element(), lifetimes);
			lifetimes.add(t.lifetime());
		} else if (type instanceof Type.Union) {
			Type.Union t = (Type.Union) type;
			extractLifetimes(t.bounds(), lifetimes);
		} else if (type instanceof Type.Intersection) {
			Type.Intersection t = (Type.Intersection) type;
			extractLifetimes(t.bounds(), lifetimes);
		} else if (type instanceof Type.Function) {
			Type.Function t = (Type.Function) type;
			extractLifetimes(t.params(), lifetimes);
			extractLifetimes(t.returns(), lifetimes);
		} else if (type instanceof Type.Method) {
			Type.Method t = (Type.Method) type;
			extractLifetimes(t.params(), lifetimes);
			extractLifetimes(t.returns(), lifetimes);
			ArrayUtils.addAll(t.contextLifetimes(), lifetimes);
		} else {
			Type.Negation t = (Type.Negation) type;
			extractLifetimes(t.element(), lifetimes);
		}
	}

	private void extractLifetimes(Type[] types, Set<String> lifetimes) {
		for (int i = 0; i != types.length; ++i) {
			extractLifetimes(types[i], lifetimes);
		}
	}

	/**
	 * Container for a function/method candidate during method resolution.
	 */
	private static class ValidCandidate {
		private final NameID id;
		private final Type.FunctionOrMethod type;

		// Either given (lifetimeArgs) or inferred
		private final List<String> lifetimeArguments;

		// Lifetime parameters substituted with (inferred) arguments
		private final List<Type> parameterTypesSubstituted;

		private ValidCandidate(NameID id, Type.FunctionOrMethod type, List<String> lifetimeArguments,
				List<Type> parameterTypesSubstituted) {
			this.id = id;
			this.type = type;
			this.lifetimeArguments = lifetimeArguments;
			this.parameterTypesSubstituted = parameterTypesSubstituted;
		}
	}

	/**
	 * Highly optimized method to validate a function/method candidate.
	 *
	 * @param candidateId
	 * @param candidateType
	 * @param candidateParameterTypes
	 * @param targetParameterTypes
	 * @param lifetimeParameters
	 * @param lifetimeArguments
	 * @param environment
	 * @return
	 * @throws ResolveError
	 */
	private ValidCandidate validateCandidate(NameID candidateId, Type.FunctionOrMethod candidateType,
			List<Type> candidateParameterTypes, List<Type> targetParameterTypes, List<String> lifetimeParameters,
			List<String> lifetimeArguments, Environment environment) throws ResolveError {
		if (!lifetimeParameters.isEmpty()) {
			// Here we *might* need a substitution
			Map<String, String> substitution = buildSubstitution(lifetimeParameters, lifetimeArguments);
			if (!substitution.isEmpty()) {
				// OK, substitution is necessary.
				Iterator<Type> itC = candidateParameterTypes.iterator();
				Iterator<Type> itT = targetParameterTypes.iterator();
				List<Type> parameterTypesSubstituted = new ArrayList<Type>(candidateParameterTypes.size());
				while (itC.hasNext()) {
					Type c = itC.next();
					Type t = itT.next();
					c = applySubstitution(substitution, c);
					if (!typeSystem.isSubtype(c, t, environment.getLifetimeRelation())) {
						return null;
					}
					parameterTypesSubstituted.add(c);
				}
				return new ValidCandidate(candidateId, candidateType, lifetimeArguments, parameterTypesSubstituted);
			}
		}

		// No substitution necessary, just do the check
		Iterator<Type> itC = candidateParameterTypes.iterator();
		Iterator<Type> itT = targetParameterTypes.iterator();
		while (itC.hasNext()) {
			Type c = itC.next();
			Type t = itT.next();
			if (!typeSystem.isSubtype(c, t, environment.getLifetimeRelation())) {
				return null;
			}
		}
		return new ValidCandidate(candidateId, candidateType, Collections.<String> emptyList(),
				candidateParameterTypes);
	}

	private Triple<NameID, Type.FunctionOrMethod, List<String>> selectCandidateFunctionOrMethod(String name,
			List<Type> parameters, List<String> lifetimeArgs,
			Collection<Pair<NameID, Type.FunctionOrMethod>> candidates, Context context, Environment environment)
					throws IOException, ResolveError {

		// We cannot do anything here without candidates
		if (candidates.isEmpty()) {
			throw new ResolveError("no match for " + name + parameterString(parameters));
		}

		// If we don't match parameters, then we don't need to bother about
		// lifetimes. Handle it separately to avoid null checks in further
		// logic.
		if (parameters == null) {
			if (candidates.size() == 1) {
				Pair<NameID, Type.FunctionOrMethod> p = candidates.iterator().next();
				return new Triple<NameID, Type.FunctionOrMethod, List<String>>(p.first(), p.second(), null);
			}

			// More than one candidate and all will match. Clearly ambiguous!
			throw new ResolveError(
					name + parameterString(parameters) + " is ambiguous" + foundCandidatesString(candidates));
		}

		// We chose a method based only on the parameter types, as return
		// type(s) are unknown.
		List<Type> targetParameterTypes = stripType(parameters);

		// In case we don't get lifetime arguments, we have to pick a possible
		// substitution by guessing. To do so, we need all lifetime names that
		// occur in the passed argument types. We will cache it here once we
		// compute it (only compute it if needed.
		List<String> lifetimesUsedInArguments = null;

		// Check each candidate to see if it is valid.
		List<ValidCandidate> validCandidates = new LinkedList<ValidCandidate>();
		for (Pair<NameID, Type.FunctionOrMethod> p : candidates) {
			Type.FunctionOrMethod candidateType = p.second();
			List<Type> candidateParameterTypes = Arrays.asList(candidateType.params());

			// We need a matching parameter count
			if (candidateParameterTypes.size() != targetParameterTypes.size()) {
				continue;
			}

			// If we got lifetime arguments: Lifetime parameter count must match
			List<String> candidateLifetimeParams = getLifetimeParameters(candidateType);
			if (lifetimeArgs != null && candidateLifetimeParams.size() != lifetimeArgs.size()) {
				continue;
			}

			if (candidateLifetimeParams.size() == 0) {
				// We don't need lifetime arguments, so just provide an empty
				// list.
				ValidCandidate vc = validateCandidate(p.first(), candidateType, candidateParameterTypes,
						targetParameterTypes, candidateLifetimeParams, Collections.<String> emptyList(), environment);
				if (vc != null) {
					validCandidates.add(vc);
				}
			} else if (lifetimeArgs != null) {
				// We got some lifetime arguments. Just check it with them.
				ValidCandidate vc = validateCandidate(p.first(), candidateType, candidateParameterTypes,
						targetParameterTypes, candidateLifetimeParams, lifetimeArgs, environment);
				if (vc != null) {
					validCandidates.add(vc);
				}
			} else {
				// Here it is a bit tricky:
				// We need to "guess" suitable lifetime arguments.

				// Make sure we know all lifetime names from our arguments, and
				// cache the result for the next candidate.
				if (lifetimesUsedInArguments == null) {
					lifetimesUsedInArguments = extractLifetimesFromArguments(targetParameterTypes);
				}

				// Guess the lifetime arguments.
				guessLifetimeArguments(lifetimesUsedInArguments, candidateLifetimeParams, candidateParameterTypes,
						targetParameterTypes, p.first(), candidateType, validCandidates, environment);
			}
		}

		// See if we have valid candidates
		if (validCandidates.isEmpty()) {
			// No valid candidates
			throw new ResolveError(
					"no match for " + name + parameterString(parameters) + foundCandidatesString(candidates));
		}

		// More than one candidate
		if (validCandidates.size() != 1) {
			// Idea: We iterate through the list and delete a valid candidate,
			// if there is another one that is a strict subtype.
			// The outer iterator is used to actually modify the list by
			// removing candidates.
			ListIterator<ValidCandidate> it = validCandidates.listIterator();

			// we know that the list is not empty, so do-while is perfectly fine
			// here
			do {
				ValidCandidate c1 = it.next();

				// Let the inner iterator start at the next entry. Note that the
				// list initially had > 1 elements and the outer do-while also
				// checks that there is one more element left, so we can again
				// use do-while here.
				for (ValidCandidate c2 : validCandidates) {
					if (c1 != c2 && paramStrictSubtypes(c1.parameterTypesSubstituted, c2.parameterTypesSubstituted,
							environment)) {
						it.remove();
						break;
					}
				}
			} while (it.hasNext());
		}

		if (validCandidates.size() == 1) {
			// now check protection modifier
			ValidCandidate winner = validCandidates.get(0);
			NameID winnerId = winner.id;
			Type.FunctionOrMethod winnerType = winner.type;
			WhileyFile wf = builder.getSourceFile(winnerId.module());
			if (wf != null) {
				if (wf != context.file()) {
					for (WhileyFile.FunctionOrMethod d : wf.declarations(WhileyFile.FunctionOrMethod.class,
							winnerId.name())) {
						if (d.parameters.equals(winnerType.params())) {
							if (!d.hasModifier(Modifier.PUBLIC)) {
								String msg = winnerId.module() + "." + name + parameterString(parameters)
										+ " is not visible";
								throw new ResolveError(msg);
							}
						}
					}
				}
			} else {
				WyilFile m = builder.getModule(winnerId.module());
				WyilFile.FunctionOrMethod d = m.functionOrMethod(winnerId.name(), winnerType);
				if (!d.hasModifier(Modifier.PUBLIC)) {
					String msg = winnerId.module() + "." + name + parameterString(parameters) + " is not visible";
					throw new ResolveError(msg);
				}
			}

			return new Triple<NameID, Type.FunctionOrMethod, List<String>>(winnerId, winnerType,
					winner.lifetimeArguments);
		}

		// this is an ambiguous error
		StringBuilder msg = new StringBuilder(name + parameterString(parameters) + " is ambiguous");
		ArrayList<String> candidateStrings = new ArrayList<String>();
		for (ValidCandidate c : validCandidates) {
			String s = c.id + " : " + c.type;
			if (!c.lifetimeArguments.isEmpty()) {
				s += " instantiated with <";
				boolean first = true;
				for (String lifetime : c.lifetimeArguments) {
					if (!first) {
						s += ", ";
					} else {
						first = false;
					}
					s += lifetime;
				}
				s += ">";
			}
			candidateStrings.add(s);
		}
		Collections.sort(candidateStrings); // make error message deterministic!
		for (String s : candidateStrings) {
			msg.append("\n\tfound: ");
			msg.append(s);
		}
		throw new ResolveError(msg.toString());
	}

	private static List<String> getLifetimeParameters(Type.FunctionOrMethod fm) {
		if (fm instanceof Type.Method) {
			Type.Method mt = (Type.Method) fm;
			return Arrays.asList(mt.lifetimeParams());
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * Guess lifetime arguments for a method call.
	 *
	 * @param lifetimesUsedInArguments
	 *            possible choices for lifetimes to be used as argument
	 * @param candidateLifetimeParams
	 *            lifetime parameters to be assigned with an argument
	 * @param candidateParameterTypes
	 *            parameter types of the actual declared method
	 * @param targetParameterTypes
	 *            parameter types as needed (extracted from caller arguments)
	 * @param candidateName
	 * @param candidateType
	 * @param validCandidates
	 *            the set where we can put valid substitutions
	 * @param environment
	 * @throws ResolveError
	 */
	private void guessLifetimeArguments(List<String> lifetimesUsedInArguments, List<String> candidateLifetimeParams,
			List<Type> candidateParameterTypes, List<Type> targetParameterTypes, NameID candidateName,
			Type.FunctionOrMethod candidateType, List<ValidCandidate> validCandidates, Environment environment)
					throws ResolveError {
		// Assume we have "exp" lifetime parameters to be filled and
		// "base" choices for each one.
		// That makes base^exp possibilities!
		int base = lifetimesUsedInArguments.size();
		int exp = candidateLifetimeParams.size();
		long count = (long) Math.pow(base, exp);
		for (long guessNumber = 0; guessNumber < count; guessNumber++) {
			// Here we generate a guessed list of lifetime arguments.
			// Basically it is the algorithm to transform guessNumber to
			// base size(lifetimesUsedInArguments).
			List<String> guessedLifetimeArgs = new ArrayList<String>(candidateLifetimeParams.size());
			for (int i = 0; i < exp; i++) {
				int guessed = (int) ((guessNumber / (long) Math.pow(base, i)) % base);
				guessedLifetimeArgs.add(lifetimesUsedInArguments.get(guessed));
			}

			// Now we can check the candidate with our guess
			ValidCandidate vc = validateCandidate(candidateName, candidateType, candidateParameterTypes,
					targetParameterTypes, candidateLifetimeParams, guessedLifetimeArgs, environment);
			if (vc != null) {
				validCandidates.add(vc);
			}
		}
	}

	/**
	 * Add all "candidate" functions or methods matching a fully qualified name.
	 * Candidates are those which the same name and matching number of
	 * arguments. They must also be visible to the given context. The context is
	 * important because some functions and methods will not be visible from all
	 * contexts. For example, a private function is only visible from within the
	 * same source file. Furthermore, the context affects what exactly will be
	 * seen externally. For example, consider a function "f(T x)=>int" where
	 * type "T" is declared as protected within the same file. The external type
	 * of "f" will be "(T)=>int"; however, the internal type will be e.g.
	 * "(int)=>int" if T is defined as int.
	 *
	 * @param nid
	 *            --- Fully qualified name of function being matched
	 * @param parameters
	 *            --- The list of parameter types, although this is only used to
	 *            determine the number of parameters
	 * @param candidates
	 *            --- The list into which all identified candidates will be
	 *            placed (i.e. this is an output parameter)
	 * @param context
	 *            --- The context in which we are looking for the given method.
	 * @throws IOException
	 */
	private void addCandidateFunctionsAndMethods(NameID nid, List<?> parameters,
			Collection<Pair<NameID, Type.FunctionOrMethod>> candidates, Context context)
					throws IOException, ResolveError {
		Path.ID mid = nid.module();

		int nparams = parameters != null ? parameters.size() : -1;

		WhileyFile wf = builder.getSourceFile(mid);
		if (wf != null) {
			for (WhileyFile.FunctionOrMethod f : wf.declarations(WhileyFile.FunctionOrMethod.class, nid.name())) {
				if (nparams == -1 || f.parameters.size() == nparams) {
					Type.FunctionOrMethod ft = (Type.FunctionOrMethod) builder.toSemanticType(f.unresolvedType(), f);
					candidates.add(new Pair<NameID, Type.FunctionOrMethod>(nid, ft));
				}
			}
		} else {
			WyilFile m = builder.getModule(mid);
			for (WyilFile.FunctionOrMethod mm : m.functionOrMethods()) {
				if ((mm.isFunction() || mm.isMethod()) && mm.name().equals(nid.name())
						&& (nparams == -1 || mm.type().params().length == nparams)) {
					Type.FunctionOrMethod t = mm.type();
					candidates.add(new Pair<NameID, Type.FunctionOrMethod>(nid, t));
				}
			}
		}
	}

	private static List<Type> stripType(List<Type> types) {
		ArrayList<Type> r = new ArrayList<Type>();
		for (Type t : types) {
			r.add(t);
		}
		return r;
	}

	/**
	 * Apply a lifetime substitution: Substitute all parameters in original by
	 * their arguments.
	 *
	 * @param lifetimeParameters
	 * @param lifetimeArguments
	 * @param original
	 * @return
	 */
	public static Type applySubstitution(List<String> lifetimeParameters, List<String> lifetimeArguments,
			Type original) {
		if (lifetimeParameters.size() != lifetimeArguments.size()) {
			throw new IllegalArgumentException(
					"lifetime parameter/argument size mismatch!" + lifetimeParameters + " vs. " + lifetimeArguments);
		}
		Map<String, String> substitution = buildSubstitution(lifetimeParameters, lifetimeArguments);
		return applySubstitution(substitution, original);
	}

	private static Type applySubstitution(Map<String, String> substitution, Type type) {
		if (substitution.isEmpty()) {
			return type;
		} else {
			return substitute(substitution, type);
		}
	}

	private static Type substitute(Map<String, String> substitution, Type type) {
		if (type instanceof Type.Primitive) {
			// A primitive cannot contain any lifetime arguments, hence can be
			// returned as itself.
			return type;
		} else if (type instanceof Type.Nominal) {
			// At the moment, nominals also cannot contain unbound lifetime
			// arguments. In the future this will change as nominals will
			// support explicit lifetime arguments.
			return type;
		} else if (type instanceof Type.Array) {
			Type.Array t = (Type.Array) type;
			Type element = substitute(substitution, t.element());
			return Type.Array(element);
		} else if (type instanceof Type.Reference) {
			Type.Reference t = (Type.Reference) type;
			Type element = substitute(substitution, t.element());
			// Apply the substitution!
			String lifetime = substitution.get(t.lifetime());
			if (lifetime == null) {
				// This lifetime variable is not being substituted for whatever
				// reason.
				lifetime = t.lifetime();
			}
			return Type.Reference(lifetime, element);
		} else if (type instanceof Type.Record) {
			Type.Record t = (Type.Record) type;
			String[] fieldNames = t.getFieldNames();
			Pair<Type, String>[] fields = new Pair[fieldNames.length];
			for (int i = 0; i != fieldNames.length; ++i) {
				String fieldName = fieldNames[i];
				// FIXME: would be more efficient to access by field index
				Type element = substitute(substitution, t.getField(fieldName));
				fields[i] = new Pair<Type, String>(element, fieldName);
			}
			return Type.Record(t.isOpen(), fields);
		} else if (type instanceof Type.Function) {
			Type.Function t = (Type.Function) type;
			Type[] parameters = substitute(substitution, t.params());
			Type[] returns = substitute(substitution, t.returns());
			return Type.Function(parameters, returns);
		} else if (type instanceof Type.Method) {
			Type.Method t = (Type.Method) type;
			String[] contextLifetimes = t.contextLifetimes();
			// Apply substitution to all context lifetimes.
			for (int i = 0; i != contextLifetimes.length; ++i) {
				String lifetime = substitution.get(contextLifetimes[i]);
				if (lifetime != null) {
					// Substitution resulted in a change
					if (contextLifetimes == t.contextLifetimes()) {
						contextLifetimes = Arrays.copyOf(contextLifetimes, contextLifetimes.length);
					}
					contextLifetimes[i] = lifetime;
				}
			}
			// Create a clone of the substitution so we can cut out any lifetime
			// parameters declared by this method. This is necessary to avoid
			// any potential variable captures.
			HashMap<String, String> nSubstitution = new HashMap<String, String>(substitution);
			// Remove lifetime parameters from keys in the substitution map
			String[] lifetimeParameters = t.lifetimeParams();
			for (int i = 0; i != lifetimeParameters.length; ++i) {
				String declaredLifetime = lifetimeParameters[i];
				nSubstitution.remove(declaredLifetime);
				if (substitution.containsValue(declaredLifetime)) {
					// This is a big problem. Basically, because we will
					// potentially have an unexpected variable capture if we
					// perform a substitution which yields this value.

					// FIXME: Do something more sensible here. What we need to
					// do is replace the given declared lifetime with a fresh
					// name which doesn't clash with anything else.
					throw new RuntimeException("Need support for lifetime variable capture");
				}
			}
			// Perform the substitution
			Type[] parameters = substitute(nSubstitution, t.params());
			Type[] returns = substitute(nSubstitution, t.returns());
			return Type.Method(lifetimeParameters, contextLifetimes, parameters, returns);
		} else if (type instanceof Type.Union) {
			Type.Union t = (Type.Union) type;
			Type[] bounds = substitute(substitution, t.bounds());
			return Type.Union(bounds);
		} else if (type instanceof Type.Intersection) {
			Type.Intersection t = (Type.Intersection) type;
			Type[] bounds = substitute(substitution, t.bounds());
			return Type.Intersection(bounds);
		} else {
			Type.Negation t = (Type.Negation) type;
			Type element = substitute(substitution, t.element());
			return Type.Negation(element);
		}
	}

	private static Type[] substitute(Map<String, String> substitution, Type[] types) {
		Type[] nTypes = new Type[types.length];
		for (int i = 0; i != types.length; ++i) {
			nTypes[i] = substitute(substitution, types[i]);
		}
		return nTypes;
	}

	private static Map<String, String> buildSubstitution(List<String> lifetimeParameters,
			List<String> lifetimeArguments) {
		Map<String, String> substitution = new HashMap<String, String>();
		Iterator<String> itP = lifetimeParameters.iterator();
		Iterator<String> itA = lifetimeArguments.iterator();
		while (itP.hasNext()) {
			String param = itP.next();
			String arg = itA.next();
			if (!arg.equals(param)) {
				substitution.put(param, arg);
			}
		}
		return substitution;
	}

	// =========================================================================
	// ResolveAsType
	// =========================================================================

	public Type.Function resolveAsType(SyntacticType.Function t, Context context) throws IOException {
		return (Type.Function) resolveAsType((SyntacticType.FunctionOrMethod) t, context);
	}

	public Type.Method resolveAsType(SyntacticType.Method t, Context context) throws IOException {
		return (Type.Method) resolveAsType((SyntacticType.FunctionOrMethod) t, context);
	}

	public Type.FunctionOrMethod resolveAsType(SyntacticType.FunctionOrMethod t, Context context) throws IOException {
		try {
			// We need to sanity check the parameter types we have here, since
			// occasionally we can end up with something other than a function
			// type.
			// This may seem surprising, but it can happen when one of the types
			// involved is contractive (normally by accident).
			for (SyntacticType param : t.paramTypes) {
				Type nominal = builder.toSemanticType(param, context);
				if (typeSystem.isSubtype(Type.T_VOID, nominal)) {
					throw new SyntaxError("empty type encountered", file.getEntry(), param);
				}
			}
			for (SyntacticType ret : t.returnTypes) {
				Type nominal = builder.toSemanticType(ret, context);
				if (typeSystem.isSubtype(Type.T_VOID, nominal)) {
					throw new SyntaxError("empty type encountered", file.getEntry(), ret);
				}
			}
			return (Type.FunctionOrMethod) builder.toSemanticType(t, context);
		} catch (ResolveError e) {
			throw new SyntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()), file.getEntry(), t, e);
		}
	}

	// =========================================================================
	// ResolveAsConstant
	// =========================================================================

	/**
	 * <p>
	 * Resolve a given name as a constant value. This is a global problem, since
	 * a constant declaration in one source file may refer to constants declared
	 * in other compilation units. This function will actually evaluate constant
	 * expressions (e.g. "1+2") to produce actual constant vales.
	 * </p>
	 *
	 * <p>
	 * Constant declarations form a global graph spanning multiple compilation
	 * units. In resolving a given constant, this function must traverse those
	 * portions of the graph which make up the constant. Constants are not
	 * permitted to be declared recursively (i.e. in terms of themselves) and
	 * this function will report an error is such a recursive cycle is detected
	 * in the constant graph.
	 * </p>
	 *
	 * @param nid
	 *            Fully qualified name identifier of constant to resolve
	 * @return Constant value representing named constant
	 * @throws IOException
	 */
	public Pair<Constant, Type> resolveAsConstant(NameID nid) throws IOException, ResolveError {
		return resolveAsConstant(nid, new HashSet<NameID>());
	}

	/**
	 * <p>
	 * Resolve a given <i>constant expression</i> as a constant value. A
	 * constant expression is one which refers only to known and visible
	 * constant values, rather than e.g. local variables. Constant expressions
	 * may still use operators (e.g. "1+2", or "1+c" where c is a declared
	 * constant).
	 * </p>
	 *
	 * <p>
	 * Constant expressions used in a few places in Whiley. In particular, the
	 * cases of a <code>switch</code> statement must be defined using constant
	 * expressions.
	 * </p>
	 *
	 * @param e
	 * @param context
	 * @return
	 */
	public Pair<Constant, Type> resolveAsConstant(Expr e, Context context) {
		e = propagate(e, new Environment(), context);
		return resolveAsConstant(e, context, new HashSet<NameID>());
	}

	/**
	 * Responsible for turning a named constant expression into a value. This is
	 * done by traversing the constant's expression and recursively expanding
	 * any named constants it contains. Simplification of constants is also
	 * performed where possible.
	 *
	 * @param key
	 *            --- name of constant we are expanding.
	 * @param exprs
	 *            --- mapping of all names to their( declared) expressions
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 * @return
	 * @throws IOException
	 */
	private Pair<Constant, Type> resolveAsConstant(NameID key, HashSet<NameID> visited)
			throws IOException, ResolveError {
		Pair<Constant, Type> result = constantCache.get(key);
		if (result != null) {
			return result;
		} else if (visited.contains(key)) {
			throw new ResolveError("cyclic constant definition encountered (" + key + " -> " + key + ")");
		} else {
			visited.add(key);
		}

		WhileyFile wf = builder.getSourceFile(key.module());

		if (wf != null) {
			WhileyFile.Declaration decl = wf.declaration(key.name());
			if (decl instanceof WhileyFile.Constant) {
				WhileyFile.Constant cd = (WhileyFile.Constant) decl;
				if (cd.resolvedValue == null) {
					cd.constant = propagate(cd.constant, new Environment(), cd);
					cd.resolvedValue = resolveAsConstant(cd.constant, cd, visited).first();
				}
				result = new Pair<Constant, Type>(cd.resolvedValue, cd.constant.result());
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		} else {
			WyilFile module = builder.getModule(key.module());
			WyilFile.Constant cd = module.constant(key.name());
			if (cd != null) {
				Constant c = cd.constant();
				result = new Pair<Constant, Type>(c, c.type());
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		}

		constantCache.put(key, result);

		return result;
	}

	/**
	 * The following is a helper method for resolveAsConstant. It takes a given
	 * expression (rather than the name of a constant) and expands to a value
	 * (where possible). If the expression contains, for example, method or
	 * function declarations then this will certainly fail (producing a syntax
	 * error).
	 *
	 * @param key
	 *            --- name of constant we are expanding.
	 * @param context
	 *            --- context in which to resolve this constant.
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 */
	private Pair<Constant, Type> resolveAsConstant(Expr expr, Context context, HashSet<NameID> visited) {
		try {
			if (expr instanceof Expr.Constant) {
				Expr.Constant c = (Expr.Constant) expr;
				return new Pair<Constant, Type>(c.value, c.result());
			} else if (expr instanceof Expr.ConstantAccess) {
				Expr.ConstantAccess c = (Expr.ConstantAccess) expr;
				ArrayList<String> qualifications = new ArrayList<String>();
				if (c.qualification != null) {
					for (String n : c.qualification) {
						qualifications.add(n);
					}
				}
				qualifications.add(c.name);
				try {
					NameID nid = builder.resolveAsName(qualifications, context);
					return resolveAsConstant(nid, visited);
				} catch (ResolveError e) {
					syntaxError(errorMessage(UNKNOWN_VARIABLE), context, expr);
					return null;
				}
			} else if (expr instanceof Expr.BinOp) {
				Expr.BinOp bop = (Expr.BinOp) expr;
				Pair<Constant, Type> lhs = resolveAsConstant(bop.lhs, context, visited);
				Pair<Constant, Type> rhs = resolveAsConstant(bop.rhs, context, visited);
				return new Pair<Constant, Type>(evaluate(bop, lhs.first(), rhs.first(), context), lhs.second());
			} else if (expr instanceof Expr.UnOp) {
				Expr.UnOp uop = (Expr.UnOp) expr;
				Pair<Constant, Type> lhs = resolveAsConstant(uop.mhs, context, visited);
				return new Pair<Constant, Type>(evaluate(uop, lhs.first(), context), lhs.second());
			} else if (expr instanceof Expr.ArrayInitialiser) {
				Expr.ArrayInitialiser nop = (Expr.ArrayInitialiser) expr;
				ArrayList<Constant> values = new ArrayList<Constant>();
				Type element = Type.T_VOID;
				for (Expr arg : nop.arguments) {
					Pair<Constant, Type> e = resolveAsConstant(arg, context, visited);
					values.add(e.first());
					element = Type.Union(element, e.second());
				}
				return new Pair<Constant, Type>(new Constant.Array(values), Type.Array(element));
			} else if (expr instanceof Expr.ArrayGenerator) {
				Expr.ArrayGenerator lg = (Expr.ArrayGenerator) expr;
				Pair<Constant, Type> element = resolveAsConstant(lg.element, context, visited);
				Pair<Constant, Type> count = resolveAsConstant(lg.count, context, visited);
				Constant.Array l = evaluate(lg, element.first(), count.first(), context);
				return new Pair<Constant, Type>(l, Type.Array(element.second()));
			} else if (expr instanceof Expr.Record) {
				Expr.Record rg = (Expr.Record) expr;
				HashMap<String, Constant> values = new HashMap<String, Constant>();
				ArrayList<Pair<Type, String>> types = new ArrayList<Pair<Type, String>>();
				for (Map.Entry<String, Expr> e : rg.fields.entrySet()) {
					Pair<Constant, Type> v = resolveAsConstant(e.getValue(), context, visited);
					if (v == null) {
						return null;
					}
					values.put(e.getKey(), v.first());
					types.add(new Pair<Type, String>(v.second(), e.getKey()));
				}
				return new Pair<Constant, Type>(new Constant.Record(values), Type.Record(false, types));
			} else if (expr instanceof Expr.FunctionOrMethod) {
				// TODO: add support for proper lambdas
				Expr.FunctionOrMethod f = (Expr.FunctionOrMethod) expr;
				return new Pair<Constant, Type>(new Constant.FunctionOrMethod(f.nid, f.type), f.type);
			}
		} catch (SyntaxError.InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			internalFailure(e.getMessage(), context, expr, e);
		}

		internalFailure("unknown constant expression: " + expr.getClass().getName(), context, expr);
		return deadCode(expr);
	}

	// =========================================================================
	// Constant Evaluation
	// =========================================================================

	/**
	 * Evaluate a given unary operator on a given input value.
	 *
	 * @param operator
	 *            Unary operator to evaluate
	 * @param operand
	 *            Operand to apply operator on
	 * @param context
	 *            Context in which to apply operator (useful for error
	 *            reporting)
	 * @return
	 */
	private Constant evaluate(Expr.UnOp operator, Constant operand, Context context) {
		switch (operator.op) {
		case NOT:
			if (operand instanceof Constant.Bool) {
				Constant.Bool b = (Constant.Bool) operand;
				return Constant.Bool(!b.value());
			}
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, operator);
			break;
		case NEG:
			if (operand instanceof Constant.Integer) {
				Constant.Integer b = (Constant.Integer) operand;
				return new Constant.Integer(b.value().negate());
			}
			syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context, operator);
			break;
		case INVERT:
			if (operand instanceof Constant.Byte) {
				Constant.Byte b = (Constant.Byte) operand;
				return new Constant.Byte((byte) ~b.value());
			}
			break;
		}
		syntaxError(errorMessage(INVALID_UNARY_EXPRESSION), context, operator);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant v1, Constant v2, Context context) throws ResolveError {
		Type v1_type = v1.type();
		Type v2_type = v2.type();

		if (typeSystem.isSubtype(Type.T_BOOL, v1_type) && typeSystem.isSubtype(Type.T_BOOL, v2_type)) {
			return evaluateBoolean(bop, (Constant.Bool) v1, (Constant.Bool) v2, context);
		} else if (typeSystem.isSubtype(Type.T_INT, v1_type) && typeSystem.isSubtype(Type.T_INT, v2_type)) {
			return evaluate(bop, (Constant.Integer) v1, (Constant.Integer) v2, context);
		}
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluateBoolean(Expr.BinOp bop, Constant.Bool v1, Constant.Bool v2, Context context) {
		switch (bop.op) {
		case AND:
			return Constant.Bool(v1.value() & v2.value());
		case OR:
			return Constant.Bool(v1.value() | v2.value());
		case XOR:
			return Constant.Bool(v1.value() ^ v2.value());
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Integer v1, Constant.Integer v2, Context context) {
		switch (bop.op) {
		case ADD:
			return new Constant.Integer(v1.value().add(v2.value()));
		case SUB:
			return new Constant.Integer(v1.value().subtract(v2.value()));
		case MUL:
			return new Constant.Integer(v1.value().multiply(v2.value()));
		case DIV:
			return new Constant.Integer(v1.value().divide(v2.value()));
		case REM:
			return new Constant.Integer(v1.value().remainder(v2.value()));
		}
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context, bop);
		return null;
	}

	private Constant.Array evaluate(Expr.ArrayGenerator bop, Constant element, Constant count, Context context) {
		if (count instanceof Constant.Integer) {
			Constant.Integer c = (Constant.Integer) count;
			ArrayList<Constant> items = new ArrayList<Constant>();
			for (int i = 0; i != c.value().intValue(); ++i) {
				items.add(element);
			}
			return new Constant.Array(items);
		}
		syntaxError(errorMessage(INVALID_ARRAY_EXPRESSION), context, bop);
		return null;
	}
	// =========================================================================
	// expandAsType
	// =========================================================================

	public Type.EffectiveArray expandAsEffectiveArray(Expr src, Context context) throws IOException, ResolveError {
		return expandAsEffectiveArray(src.result(), src, context);
	}

	public Type.EffectiveArray expandAsEffectiveArray(Type type, SyntacticElement element, Context context)
			throws IOException, ResolveError {
		Type.EffectiveArray arrType = typeSystem.expandAsEffectiveArray(type);
		if (arrType == null) {
			syntaxError(errorMessage(INVALID_ARRAY_EXPRESSION), context, element);
		}
		return arrType;
	}

	public Type.EffectiveRecord expandAsEffectiveRecord(Expr src, Context context) throws IOException, ResolveError {
		return expandAsEffectiveRecord(src.result(), src, context);
	}

	public Type.EffectiveRecord expandAsEffectiveRecord(Type type, SyntacticElement element, Context context)
			throws IOException, ResolveError {
		Type.EffectiveRecord recType = typeSystem.expandAsEffectiveRecord(type);
		if (recType == null) {
			syntaxError(errorMessage(RECORD_TYPE_REQUIRED, type), context, element);
		}
		return recType;
	}

	public Type.Reference expandAsEffectiveReference(Expr src, Context context) throws IOException, ResolveError {
		return expandAsEffectiveReference(src.result(), src, context);
	}

	public Type.Reference expandAsEffectiveReference(Type type, SyntacticElement element, Context context)
			throws IOException, ResolveError {
		Type.Reference refType = typeSystem.expandAsReference(type);
		if (refType == null) {
			syntaxError(errorMessage(REFERENCE_TYPE_REQUIRED, type), context, element);
		}
		return refType;
	}

	public Type.FunctionOrMethod expandAsEffectiveFunctionOrMethod(Expr src, Context context)
			throws IOException, ResolveError {
		return expandAsEffectiveFunctionOrMethod(src.result(), src, context);
	}

	public Type.FunctionOrMethod expandAsEffectiveFunctionOrMethod(Type type, SyntacticElement element, Context context)
			throws IOException, ResolveError {
		Type.FunctionOrMethod funType = typeSystem.expandAsFunctionOrMethod(type);
		if (funType == null) {
			syntaxError(errorMessage(FUNCTION_OR_METHOD_TYPE_REQUIRED, type), context, element);
		}
		return funType;
	}

	private Environment addDeclaredParameters(List<WhileyFile.Parameter> parameters, Environment environment,
			WhileyFile.Context d) throws IOException {
		for (WhileyFile.Parameter p : parameters) {
			try {
				environment = environment.declare(p.name, builder.toSemanticType(p.type, d),
						builder.toSemanticType(p.type, d));
			} catch (ResolveError e) {
				throw new SyntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()), file.getEntry(), p.type, e);
			}
		}
		return environment;
	}

	private Environment addDeclaredParameter(WhileyFile.Parameter parameter, Environment environment,
			WhileyFile.Context d) throws ResolveError, IOException {
		if (parameter != null) {
			Type type = builder.toSemanticType(parameter.type, d);
			return environment.declare(parameter.name, type, type);
		} else {
			return environment;
		}
	}

	// =========================================================================
	// Misc
	// =========================================================================

	/**
	 * This method is provided to properly handled positions which should be
	 * dead code.
	 *
	 * @param context
	 *            --- Context in which bytecodes are executed
	 */
	private <T> T deadCode(SyntacticElement element) {
		throw new InternalFailure("dead code reached",file.getEntry(),element);
	}

	// Check t1 :> t2
	private void checkIsSubtype(Type t1, Type t2, SyntacticElement elem, Environment environment) throws ResolveError {
		if (!typeSystem.isSubtype(t1, t2, environment.getLifetimeRelation())) {
			throw new SyntaxError(errorMessage(SUBTYPE_ERROR, t1, t2), file.getEntry(), elem);
		}
	}

	private void checkIsSubtype(Type t1, Expr t2, Environment environment) throws ResolveError {
		if (!typeSystem.isSubtype(t1, t2.result(), environment.getLifetimeRelation())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			throw new SyntaxError(errorMessage(SUBTYPE_ERROR, t1, t2.result()), file.getEntry(), t2);
		}
	}

	private void checkIsSubtype(Type t1, Expr t2, Context context, Environment environment) throws ResolveError {
		if (!typeSystem.isSubtype(t1, t2.result(), environment.getLifetimeRelation())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(errorMessage(SUBTYPE_ERROR, t1, t2.result()), context, t2);
		}
	}

	// Check t1 <: t2 or t1 <: t3 ...
	private void checkSuptypes(Expr e, Context context, Environment environment, Type... types) throws ResolveError {
		Type t1 = e.result();
		for (Type t : types) {
			if (typeSystem.isSubtype(t, t1, environment.getLifetimeRelation())) {
				return; // OK
			}
		}
		// Construct the message
		String msg = "expecting ";
		boolean firstTime = true;
		for (Type t : types) {
			if (!firstTime) {
				msg += " or ";
			}
			firstTime = false;
			msg = msg + t;
		}
		msg += ", found " + t1;
		syntaxError(msg, context, e);
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
			currentTypes = new HashMap<String, Type>();
			declaredTypes = new HashMap<String, Type>();
			inLambda = false;
			lambdaLifetimes = new HashSet<String>();
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
