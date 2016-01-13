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

import static wyc.lang.WhileyFile.internalFailure;
import static wyc.lang.WhileyFile.syntaxError;
import static wycc.lang.SyntaxError.internalFailure;
import static wycc.lang.SyntaxError.syntaxError;
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
import wycc.lang.NameID;
import wycc.lang.SyntacticElement;
import wycc.lang.SyntaxError;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wycc.util.Triple;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.Constant;
import wyil.lang.Modifier;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.util.TypeExpander;

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

	private final WhileyBuilder builder;
	private final TypeExpander expander;
	private String filename;
	private WhileyFile.FunctionOrMethod current;

	/**
	 * The constant cache contains a cache of expanded constant values. This is
	 * simply to prevent recomputing them every time.
	 */
	private final HashMap<NameID, Pair<Constant, Nominal>> constantCache = new HashMap<NameID, Pair<Constant, Nominal>>();

	public FlowTypeChecker(WhileyBuilder builder) {
		this.builder = builder;
		this.expander = new TypeExpander(builder.project());
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
		this.filename = wf.filename;

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
				syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()),
						filename, decl, e);
			} catch (SyntaxError e) {
				throw e;
			} catch (Throwable t) {
				internalFailure(t.getMessage(), filename, decl, t);
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

		// First, resolve the declared syntactic type into the corresponding
		// nominal type.
		td.resolvedType = resolveAsType(td.parameter.type, td);

		if (Type.isSubtype(Type.T_VOID, td.resolvedType.raw())) {
			// A non-contractive type is one which cannot accept a finite
			// values.  For example, the following is a contractive type:
			//
			// type NonContractive is { NonContractive x }
			syntaxError("empty type encountered", filename, td);
		} else if (td.invariant.size() > 0) {
			// Second, an invariant expression is given, so propagate through
			// that.
			Environment environment = addDeclaredParameter(td.parameter, new Environment(), td);
			// Propagate type information through the constraint
			for(int i=0;i!=td.invariant.size();++i) {
				Expr invariant = propagate(td.invariant.get(i), environment, td);
				td.invariant.set(i, invariant);
			}
		}
	}

	/**
	 * Propagate and check types for a given constant declaration.
	 *
	 * @param cd
	 *            Constant declaration to check.
	 * @throws IOException
	 */
	public void propagate(WhileyFile.Constant cd) throws IOException,
			ResolveError {
		NameID nid = new NameID(cd.file().module, cd.name());
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
		this.current = d; // ugly
		
		// Resolve the types of all parameters and construct an appropriate
		// environment for use in the flow-sensitive type propagation.
		Environment environment = addDeclaredParameters(d.parameters,new Environment(),d);		
		// Resolve types for any preconditions (i.e. requires clauses) provided.
		propagateConditions(d.requires, environment, d);		
		// Resolve types for any postconditions (i.e. ensures clauses) provided.
		Environment ensuresEnvironment = addDeclaredParameter(d.returnType, environment.clone(), d);
		propagateConditions(d.ensures, ensuresEnvironment, d);

		// Resolve the overall type for the function or method.
		if (d instanceof WhileyFile.Function) {
			WhileyFile.Function f = (WhileyFile.Function) d;
			f.resolvedType = resolveAsType(f.unresolvedType(), d);
		} else {
			WhileyFile.Method m = (WhileyFile.Method) d;
			m.resolvedType = resolveAsType(m.unresolvedType(), d);
		}
		
		// Finally, propagate type information throughout all statements in the
		// function / method body.
		Environment last = propagate(d.statements, environment);
		//
		checkReturnValue(d,last);		
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
		if (!d.hasModifier(Modifier.NATIVE) && last != BOTTOM
				&& !(current.resolvedType().ret().raw() instanceof Type.Void)) {
			// In this case, code reaches the end of the function or method and,
			// furthermore, that this requires a return value. To get here means
			// that there was no explicit return statement given on at least one
			// execution path.
			syntaxError("missing return statement", filename, d);
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
	private Environment propagate(ArrayList<Stmt> block, Environment environment) {

		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			if (stmt instanceof Expr) {
				block.set(i,
						(Stmt) propagate((Expr) stmt, environment, current));
			} else {
				environment = propagate(stmt, environment);
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
	 * @param block
	 *            Block of statements to flow-sensitively type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt stmt, Environment environment) {

		try {
			if (stmt instanceof Stmt.VariableDeclaration) {
				return propagate((Stmt.VariableDeclaration) stmt, environment);
			} else if (stmt instanceof Stmt.Assign) {
				return propagate((Stmt.Assign) stmt, environment);
			} else if (stmt instanceof Stmt.Return) {
				return propagate((Stmt.Return) stmt, environment);
			} else if (stmt instanceof Stmt.IfElse) {
				return propagate((Stmt.IfElse) stmt, environment);
			} else if (stmt instanceof Stmt.While) {
				return propagate((Stmt.While) stmt, environment);
			} else if (stmt instanceof Stmt.Switch) {
				return propagate((Stmt.Switch) stmt, environment);
			} else if (stmt instanceof Stmt.DoWhile) {
				return propagate((Stmt.DoWhile) stmt, environment);
			} else if (stmt instanceof Stmt.Break) {
				return propagate((Stmt.Break) stmt, environment);
			} else if (stmt instanceof Stmt.Assert) {
				return propagate((Stmt.Assert) stmt, environment);
			} else if (stmt instanceof Stmt.Assume) {
				return propagate((Stmt.Assume) stmt, environment);
			} else if (stmt instanceof Stmt.Debug) {
				return propagate((Stmt.Debug) stmt, environment);
			} else if (stmt instanceof Stmt.Skip) {
				return propagate((Stmt.Skip) stmt, environment);
			} else {
				internalFailure("unknown statement: "
						+ stmt.getClass().getName(), filename, stmt);
				return null; // deadcode
			}
		} catch (ResolveError e) {
			syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()),
					filename, stmt, e);
			return null; // dead code
		} catch (SyntaxError e) {
			throw e;
		} catch (Throwable e) {
			internalFailure(e.getMessage(), filename, stmt, e);
			return null; // dead code
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
	 */
	private Environment propagate(Stmt.Assert stmt, Environment environment) {
		stmt.expr = propagate(stmt.expr, environment, current);
		checkIsSubtype(Type.T_BOOL, stmt.expr);
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
	 */
	private Environment propagate(Stmt.Assume stmt, Environment environment) {
		stmt.expr = propagate(stmt.expr, environment, current);
		checkIsSubtype(Type.T_BOOL, stmt.expr);
		return environment;
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
	private Environment propagate(Stmt.VariableDeclaration stmt,
			Environment environment) throws IOException, ResolveError {
		// First, resolve declared type
		stmt.type = resolveAsType(stmt.parameter.type, current);

		// Second, resolve type of initialiser. This must be performed before we
		// update the environment, since this expression is not allowed to refer
		// to the newly declared variable.
		if (stmt.expr != null) {
			stmt.expr = propagate(stmt.expr, environment, current);
			checkIsSubtype(stmt.type, stmt.expr);
		}

		// Third, update environment accordingly. Observe that we can safely
		// assume any variable(s) are not already declared in the enclosing
		// scope because the parser checks this for us.
		environment = addDeclaredParameter(stmt.parameter, environment, current);

		// Fourth, set the current type of the assigned variable if an
		// initialiser is used. This is because the current type may differ
		// from the declared type.
		if (stmt.expr != null) {
			environment = environment.update(stmt.parameter.name, stmt.expr.result());
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
	private Environment propagate(Stmt.Assign stmt, Environment environment)
			throws IOException, ResolveError {

		Expr.LVal lhs = propagate(stmt.lhs, environment);
		Expr rhs = propagate(stmt.rhs, environment, current);

		if (lhs instanceof Expr.RationalLVal) {
			// represents a destructuring assignment
			Expr.RationalLVal tv = (Expr.RationalLVal) lhs;
			Pair<Expr.AssignedVariable, Expr.AssignedVariable> avs = inferAfterType(
					tv, rhs);
			String numVar = avs.first().var;
			String denVar = avs.second().var;
			checkIsSubtype(environment.getDeclaredType(numVar),
					avs.first().afterType, avs.first());
			checkIsSubtype(environment.getDeclaredType(denVar),
					avs.second().afterType, avs.second());
			environment = environment.update(numVar, avs.first().afterType);
			environment = environment.update(denVar, avs.second().afterType);
		} else {
			// represents element or field update
			Expr.AssignedVariable av = inferAfterType(lhs, rhs.result());
			checkIsSubtype(environment.getDeclaredType(av.var), av.afterType,
					av);
			environment = environment.update(av.var, av.afterType);
		}

		stmt.lhs = (Expr.LVal) lhs;
		stmt.rhs = rhs;

		return environment;
	}

	private Pair<Expr.AssignedVariable, Expr.AssignedVariable> inferAfterType(
			Expr.RationalLVal tv, Expr rhs) throws IOException {
		Nominal afterType = rhs.result();

		if (!Type.isSubtype(Type.T_REAL, afterType.raw())) {
			syntaxError("real value expected, got " + afterType, filename, rhs);
		}

		if (tv.numerator instanceof Expr.AssignedVariable
				&& tv.denominator instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable lv = (Expr.AssignedVariable) tv.numerator;
			Expr.AssignedVariable rv = (Expr.AssignedVariable) tv.denominator;
			lv.type = Nominal.T_VOID;
			rv.type = Nominal.T_VOID;
			lv.afterType = Nominal.T_INT;
			rv.afterType = Nominal.T_INT;
			return new Pair<Expr.AssignedVariable, Expr.AssignedVariable>(lv,
					rv);
		} else {
			syntaxError(errorMessage(INVALID_TUPLE_LVAL), filename, tv);
			return null; // dead code
		}
	}

	private Expr.AssignedVariable inferAfterType(Expr.LVal lv, Nominal afterType) {
		if (lv instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable v = (Expr.AssignedVariable) lv;
			v.afterType = afterType;
			return v;
		} else if (lv instanceof Expr.Dereference) {
			Expr.Dereference pa = (Expr.Dereference) lv;
			// The before and after types are the same since an assignment
			// through a reference does not change its type.
			checkIsSubtype(pa.srcType, Nominal.Reference(afterType), lv);
			return inferAfterType((Expr.LVal) pa.src, pa.srcType);
		} else if (lv instanceof Expr.IndexOf) {
			Expr.IndexOf la = (Expr.IndexOf) lv;
			Nominal.Array srcType = la.srcType;
			afterType = (Nominal) srcType.update(la.index.result(), afterType);
			return inferAfterType((Expr.LVal) la.src, afterType);
		} else if (lv instanceof Expr.FieldAccess) {
			Expr.FieldAccess la = (Expr.FieldAccess) lv;
			Nominal.Record srcType = la.srcType;
			// I know I can modify this hash map, since it's created fresh
			// in Nominal.Record.fields().
			afterType = (Nominal) srcType.update(la.name, afterType);
			return inferAfterType((Expr.LVal) la.src, afterType);
		} else {
			internalFailure("unknown lval: " + lv.getClass().getName(),
					filename, lv);
			return null; // deadcode
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
	private Environment propagate(Stmt.Break stmt, Environment environment) {
		// FIXME: need to propagate environment to the break destination
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
	private Environment propagate(Stmt.Debug stmt, Environment environment) {
		stmt.expr = propagate(stmt.expr, environment, current);
		checkIsSubtype(Type.Array(Type.T_INT,false), stmt.expr);
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
	 */
	private Environment propagate(Stmt.DoWhile stmt, Environment environment) {

		// Iterate to a fixed point
		environment = computeFixedPoint(environment, stmt.body, stmt.condition,
				true, stmt);

		// Type invariants
		List<Expr> stmt_invariants = stmt.invariants;
		for (int i = 0; i != stmt_invariants.size(); ++i) {
			Expr invariant = stmt_invariants.get(i);
			invariant = propagate(invariant, environment, current);
			stmt_invariants.set(i, invariant);
			checkIsSubtype(Type.T_BOOL, invariant);
		}

		// Type condition assuming its false to represent the terminated loop.
		// This is important if the condition contains a type test, as we'll
		// know that doesn't hold here.
		Pair<Expr, Environment> p = propagateCondition(stmt.condition, false,
				environment, current);
		stmt.condition = p.first();
		environment = p.second();

		return environment;
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
	 */

	private Environment propagate(Stmt.IfElse stmt, Environment environment) {

		// First, check condition and apply variable retypings.
		Pair<Expr, Environment> p1, p2;

		p1 = propagateCondition(stmt.condition, true, environment.clone(),
				current);
		p2 = propagateCondition(stmt.condition, false, environment.clone(),
				current);
		stmt.condition = p1.first();

		Environment trueEnvironment = p1.second();
		Environment falseEnvironment = p2.second();

		// Second, update environments for true and false branches
		if (stmt.trueBranch != null && stmt.falseBranch != null) {
			trueEnvironment = propagate(stmt.trueBranch, trueEnvironment);
			falseEnvironment = propagate(stmt.falseBranch, falseEnvironment);
		} else if (stmt.trueBranch != null) {
			trueEnvironment = propagate(stmt.trueBranch, trueEnvironment);
		} else if (stmt.falseBranch != null) {
			trueEnvironment = environment;
			falseEnvironment = propagate(stmt.falseBranch, falseEnvironment);
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
	 */
	private Environment propagate(Stmt.Return stmt, Environment environment)
			throws IOException {

		if (stmt.expr != null) {
			stmt.expr = propagate(stmt.expr, environment, current);
			Nominal rhs = stmt.expr.result();
			checkIsSubtype(current.resolvedType().ret(), rhs, stmt.expr);
		} else if (!(current.resolvedType().ret().raw() instanceof Type.Void)) {
			// In this case, we have an unusual situation. A return statement
			// was provided without a return value, but the enclosing method or
			// function requires a return value.
			syntaxError("missing return value", filename, stmt);
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
	private Environment propagate(Stmt.Switch stmt, Environment environment)
			throws IOException {

		stmt.expr = propagate(stmt.expr, environment, current);

		Environment finalEnv = null;
		boolean hasDefault = false;

		for (Stmt.Case c : stmt.cases) {

			// first, resolve the constants

			ArrayList<Constant> values = new ArrayList<Constant>();
			for (Expr e : c.expr) {
				values.add(resolveAsConstant(e, current).first());
			}
			c.constants = values;

			// second, propagate through the statements

			Environment localEnv = environment.clone();
			localEnv = propagate(c.stmts, localEnv);

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
	 * Type check a <code>whiley</code> statement.
	 *
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.While stmt, Environment environment) {

		// Determine typing at beginning of loop
		environment = computeFixedPoint(environment, stmt.body, stmt.condition,
				false, stmt);

		// Type loop invariant(s)
		List<Expr> stmt_invariants = stmt.invariants;
		for (int i = 0; i != stmt_invariants.size(); ++i) {
			Expr invariant = stmt_invariants.get(i);
			invariant = propagate(invariant, environment, current);
			stmt_invariants.set(i, invariant);
			checkIsSubtype(Type.T_BOOL, invariant);
		}

		// Type condition assuming its false to represent the terminated loop.
		// This is important if the condition contains a type test, as we'll
		// know that doesn't hold here.
		Pair<Expr, Environment> p = propagateCondition(stmt.condition, false,
				environment, current);
		stmt.condition = p.first();
		environment = p.second();

		return environment;
	}

	// =========================================================================
	// LVals
	// =========================================================================

	private Expr.LVal propagate(Expr.LVal lval, Environment environment) {
		try {
			if (lval instanceof Expr.AbstractVariable) {
				Expr.AbstractVariable av = (Expr.AbstractVariable) lval;
				Nominal p = environment.getCurrentType(av.var);
				if (p == null) {
					syntaxError(errorMessage(UNKNOWN_VARIABLE), filename, lval);
				}
				Expr.AssignedVariable lv = new Expr.AssignedVariable(av.var,
						av.attributes());
				lv.type = p;
				return lv;
			} else if (lval instanceof Expr.RationalLVal) {
				Expr.RationalLVal av = (Expr.RationalLVal) lval;
				av.numerator = propagate(av.numerator, environment);
				av.denominator = propagate(av.denominator, environment);
				return av;
			} else if (lval instanceof Expr.Dereference) {
				Expr.Dereference pa = (Expr.Dereference) lval;
				Expr.LVal src = propagate((Expr.LVal) pa.src, environment);
				pa.src = src;
				pa.srcType = expandAsReference(src.result());
				return pa;
			} else if (lval instanceof Expr.IndexOf) {
				// this indicates either a list, string or dictionary update
				Expr.IndexOf ai = (Expr.IndexOf) lval;
				Expr.LVal src = propagate((Expr.LVal) ai.src, environment);
				Expr index = propagate(ai.index, environment, current);
				ai.src = src;
				ai.index = index;
				Nominal.Array srcType = expandAsEffectiveList(src.result());
				if (srcType == null) {
					syntaxError(errorMessage(INVALID_LVAL_EXPRESSION),
							filename, lval);
				}
				ai.srcType = srcType;
				return ai;
			} else if (lval instanceof Expr.FieldAccess) {
				// this indicates a record update
				Expr.FieldAccess ad = (Expr.FieldAccess) lval;
				Expr.LVal src = propagate((Expr.LVal) ad.src, environment);
				Expr.FieldAccess ra = new Expr.FieldAccess(src, ad.name,
						ad.attributes());
				Nominal.Record srcType = expandAsEffectiveRecord(src
						.result());
				if (srcType == null) {
					syntaxError(errorMessage(INVALID_LVAL_EXPRESSION),
							filename, lval);
				} else if (srcType.field(ra.name) == null) {
					syntaxError(errorMessage(RECORD_MISSING_FIELD, ra.name),
							filename, lval);
				}
				ra.srcType = srcType;
				return ra;
			} 
		} catch (SyntaxError e) {
			throw e;
		} catch (Throwable e) {
			internalFailure(e.getMessage(), filename, lval, e);
			return null; // dead code
		}
		internalFailure("unknown lval: " + lval.getClass().getName(), filename,
				lval);
		return null; // dead code
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
	 */
	public Pair<Expr, Environment> propagateCondition(Expr expr, boolean sign,
			Environment environment, Context context) {

		// Split up into the compound and non-compound forms.

		if (expr instanceof Expr.UnOp) {
			return propagateCondition((Expr.UnOp) expr, sign, environment,
					context);
		} else if (expr instanceof Expr.BinOp) {
			return propagateCondition((Expr.BinOp) expr, sign, environment,
					context);
		} else {
			// For non-compound forms, can just default back to the base rules
			// for general expressions.
			expr = propagate(expr, environment, context);
			checkIsSubtype(Type.T_BOOL, expr, context);
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
	 */
	private Pair<Expr, Environment> propagateCondition(Expr.UnOp expr,
			boolean sign, Environment environment, Context context) {
		Expr.UnOp uop = (Expr.UnOp) expr;

		// Check whether we have logical not

		if (uop.op == Expr.UOp.NOT) {
			Pair<Expr, Environment> p = propagateCondition(uop.mhs, !sign,
					environment, context);
			uop.mhs = p.first();
			checkIsSubtype(Type.T_BOOL, uop.mhs, context);
			uop.type = Nominal.T_BOOL;
			return new Pair(uop, p.second());
		} else {
			// Nothing else other than logical not is valid at this point.
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, expr);
			return null; // deadcode
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
	 */
	private Pair<Expr, Environment> propagateCondition(Expr.BinOp bop,
			boolean sign, Environment environment, Context context) {
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
	 */
	private Pair<Expr, Environment> resolveNonLeafCondition(Expr.BinOp bop,
			boolean sign, Environment environment, Context context) {
		Expr.BOp op = bop.op;
		Pair<Expr, Environment> p;
		boolean followOn = (sign && op == Expr.BOp.AND)
				|| (!sign && op == Expr.BOp.OR);

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

		checkIsSubtype(Type.T_BOOL, bop.lhs, context);
		checkIsSubtype(Type.T_BOOL, bop.rhs, context);
		bop.srcType = Nominal.T_BOOL;

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
	 */
	private Pair<Expr, Environment> resolveLeafCondition(Expr.BinOp bop,
			boolean sign, Environment environment, Context context) {
		Expr.BOp op = bop.op;

		Expr lhs = propagate(bop.lhs, environment, context);
		Expr rhs = propagate(bop.rhs, environment, context);
		bop.lhs = lhs;
		bop.rhs = rhs;

		Type lhsRawType = lhs.result().raw();
		Type rhsRawType = rhs.result().raw();

		switch (op) {
		case IS:
			// this one is slightly more difficult. In the special case that
			// we have a type constant on the right-hand side then we want
			// to check that it makes sense. Otherwise, we just check that
			// it has type meta.

			if (rhs instanceof Expr.TypeVal) {
				// yes, right-hand side is a constant
				Expr.TypeVal tv = (Expr.TypeVal) rhs;
				Nominal unconstrainedTestType = resolveAsUnconstrainedType(
						tv.unresolvedType, context);

				/**
				 * Determine the types guaranteed to hold on the true and false
				 * branches respectively. We have to use the negated
				 * unconstrainedTestType for the false branch because only that
				 * is guaranteed if the test fails. For example:
				 *
				 * <pre>
				 * define nat as int where $ &gt;= 0
				 * define listnat as [int]|nat
				 * 
				 * int f([int]|int x):
				 *    if x if listnat:
				 *        x : [int]|int
				 *        ...
				 *    else:
				 *        x : int
				 * </pre>
				 *
				 * The unconstrained type of listnat is [int], since nat is a
				 * constrained type.
				 */
				Nominal glbForFalseBranch = Nominal.intersect(lhs.result(),
						Nominal.Negation(unconstrainedTestType));
				Nominal glbForTrueBranch = Nominal.intersect(lhs.result(),
						tv.type);

				if (glbForFalseBranch.raw() == Type.T_VOID) {
					// DEFINITE TRUE CASE
					syntaxError(errorMessage(BRANCH_ALWAYS_TAKEN), context, bop);
				} else if (glbForTrueBranch.raw() == Type.T_VOID) {
					// DEFINITE FALSE CASE
					syntaxError(
							errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,
									tv.type.raw()), context, bop);
				}

				// Finally, if the lhs is local variable then update its
				// type in the resulting environment.
				if (lhs instanceof Expr.LocalVariable) {
					Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
					Nominal newType;
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
				checkIsSubtype(Type.T_META, rhs, context);
			}

			bop.srcType = lhs.result();
			break;
		case LT:
		case LTEQ:
		case GTEQ:
		case GT:
			checkSuptypes(lhs, context, Nominal.T_INT, Nominal.T_REAL);
			checkSuptypes(rhs, context, Nominal.T_INT, Nominal.T_REAL);
			//
			if (!lhsRawType.equals(rhsRawType)) {
				syntaxError(
						errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,
								rhsRawType), filename, bop);
				return null;
			} else {
				bop.srcType = lhs.result();
			}
			break;
		case NEQ:
			// following is a sneaky trick for the special case below
			sign = !sign;
		case EQ:

			// first, check for special case of e.g. x != null. This is then
			// treated the same as !(x is null)

			if (lhs instanceof Expr.LocalVariable
					&& rhs instanceof Expr.Constant
					&& ((Expr.Constant) rhs).value == Constant.V_NULL) {
				// bingo, special case
				Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
				Nominal newType;
				Nominal glb = Nominal.intersect(lhs.result(), Nominal.T_NULL);
				if (glb.raw() == Type.T_VOID) {
					syntaxError(
							errorMessage(INCOMPARABLE_OPERANDS, lhs.result()
									.raw(), Type.T_NULL), context, bop);
					return null;
				} else if (sign) {
					newType = glb;
				} else {
					newType = Nominal
							.intersect(lhs.result(), Nominal.T_NOTNULL);
				}
				bop.srcType = lhs.result();
				environment = environment.update(lv.var, newType);
			} else {
				// handle general case
				if (Type.isSubtype(lhsRawType, rhsRawType)) {
					bop.srcType = lhs.result();
				} else if (Type.isSubtype(rhsRawType, lhsRawType)) {
					bop.srcType = rhs.result();
				} else {
					syntaxError(
							errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,
									rhsRawType), context, bop);
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
				return propagate((Expr.ConstantAccess) expr, environment,
						context);
			} else if (expr instanceof Expr.FieldAccess) {
				return propagate((Expr.FieldAccess) expr, environment, context);
			} else if (expr instanceof Expr.AbstractFunctionOrMethod) {
				return propagate((Expr.AbstractFunctionOrMethod) expr,
						environment, context);
			} else if (expr instanceof Expr.AbstractInvoke) {
				return propagate((Expr.AbstractInvoke) expr, environment,
						context);
			} else if (expr instanceof Expr.AbstractIndirectInvoke) {
				return propagate((Expr.AbstractIndirectInvoke) expr,
						environment, context);
			} else if (expr instanceof Expr.IndexOf) {
				return propagate((Expr.IndexOf) expr, environment, context);
			} else if (expr instanceof Expr.Lambda) {
				return propagate((Expr.Lambda) expr, environment, context);
			} else if (expr instanceof Expr.LengthOf) {
				return propagate((Expr.LengthOf) expr, environment, context);
			} else if (expr instanceof Expr.LocalVariable) {
				return propagate((Expr.LocalVariable) expr, environment,
						context);
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
			syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()),
					context, expr, e);
		} catch (SyntaxError e) {
			throw e;
		} catch (Throwable e) {
			internalFailure(e.getMessage(), context, expr, e);
			return null; // dead code
		}
		internalFailure("unknown expression: " + expr.getClass().getName(),
				context, expr);
		return null; // dead code
	}

	private Expr propagate(Expr.BinOp expr, Environment environment,
			Context context) throws IOException {

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
		Type lhsRawType = lhs.result().raw();
		Type rhsRawType = rhs.result().raw();

		Type srcType;

		switch (expr.op) {
		case IS:
		case AND:
		case OR:
		case XOR:
			return propagateCondition(expr, true, environment, context)
					.first();
		case BITWISEAND:
		case BITWISEOR:
		case BITWISEXOR:
			checkIsSubtype(Type.T_BYTE, lhs, context);
			checkIsSubtype(Type.T_BYTE, rhs, context);
			srcType = Type.T_BYTE;
			break;
		case LEFTSHIFT:
		case RIGHTSHIFT:
			checkIsSubtype(Type.T_BYTE, lhs, context);
			checkIsSubtype(Type.T_INT, rhs, context);
			srcType = Type.T_BYTE;
			break;
		case RANGE:
			checkIsSubtype(Type.T_INT, lhs, context);
			checkIsSubtype(Type.T_INT, rhs, context);
			srcType = Type.Array(Type.T_INT, false);
			break;
		case REM:
			checkIsSubtype(Type.T_INT, lhs, context);
			checkIsSubtype(Type.T_INT, rhs, context);
			srcType = Type.T_INT;
			break;
		default:
			// all other operations go through here
			checkSuptypes(lhs, context, Nominal.T_INT, Nominal.T_REAL);
			checkSuptypes(rhs, context, Nominal.T_INT, Nominal.T_REAL);
			//
			if (!lhsRawType.equals(rhsRawType)) {
				syntaxError(
						errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,
								rhsRawType), filename, expr);
				return null;
			} else {
				srcType = lhsRawType;
			}
		}		

		// FIXME: loss of nominal information
		expr.srcType = Nominal.construct(srcType, srcType);

		return expr;
	}

	private Expr propagate(Expr.UnOp expr, Environment environment,
			Context context) throws IOException {

		if (expr.op == Expr.UOp.NOT) {
			// hand off to special method for conditions
			return propagateCondition(expr, true, environment, context).first();
		}

		Expr src = propagate(expr.mhs, environment, context);
		expr.mhs = src;

		switch (expr.op) {
		case NEG:
			checkSuptypes(src, context, Nominal.T_INT, Nominal.T_REAL);
			break;
		case INVERT:
			checkIsSubtype(Type.T_BYTE, src, context);
			break;

		default:
			internalFailure(
					"unknown operator: " + expr.op.getClass().getName(),
					context, expr);
		}

		expr.type = src.result();

		return expr;
	}

	private Expr propagate(Expr.Quantifier expr, Environment environment,
			Context context) throws IOException, ResolveError {

		ArrayList<Triple<String, Expr, Expr>> sources = expr.sources;
		Environment local = environment.clone();
		for (int i = 0; i != sources.size(); ++i) {
			Triple<String, Expr, Expr> p = sources.get(i);
			Expr start = propagate(p.second(), local, context);
			Expr end = propagate(p.third(), local, context);
			sources.set(i,
					new Triple<String, Expr, Expr>(p.first(), start, end));
			checkIsSubtype(Type.T_INT, start, context);
			local = local.declare(p.first(), Nominal.T_INT, Nominal.T_INT);
		}

		if (expr.condition != null) {
			expr.condition = propagate(expr.condition, local, context);
		}

		expr.type = Nominal.T_BOOL;

		local.free();

		return expr;
	}

	private Expr propagate(Expr.Constant expr, Environment environment,
			Context context) {
		return expr;
	}

	private Expr propagate(Expr.Cast c, Environment environment, Context context)
			throws IOException {
		c.expr = propagate(c.expr, environment, context);
		c.type = resolveAsType(c.unresolvedType, context);
		Type from = c.expr.result().raw();
		Type to = c.type.raw();
		if (!Type.isExplicitCoerciveSubtype(to, from)) {
			syntaxError(errorMessage(SUBTYPE_ERROR, to, from), context, c);
		}
		return c;
	}

	private Expr propagate(Expr.AbstractFunctionOrMethod expr,
			Environment environment, Context context) throws IOException,
			ResolveError {

		if (expr instanceof Expr.FunctionOrMethod) {
			return expr;
		}

		Pair<NameID, Nominal.FunctionOrMethod> p;

		if (expr.paramTypes != null) {
			ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
			for (SyntacticType t : expr.paramTypes) {
				paramTypes.add(resolveAsType(t, context));
			}
			// FIXME: clearly a bug here in the case of message reference
			p = (Pair<NameID, Nominal.FunctionOrMethod>) resolveAsFunctionOrMethod(
					expr.name, paramTypes, context);
		} else {
			p = resolveAsFunctionOrMethod(expr.name, context);
		}

		expr = new Expr.FunctionOrMethod(p.first(), expr.paramTypes,
				expr.attributes());
		expr.type = p.second();
		return expr;
	}

	private Expr propagate(Expr.Lambda expr, Environment environment,
			Context context) throws IOException {

		ArrayList<Type> rawTypes = new ArrayList<Type>();
		ArrayList<Type> nomTypes = new ArrayList<Type>();

		for (WhileyFile.Parameter p : expr.parameters) {
			Nominal n = resolveAsType(p.type, context);
			rawTypes.add(n.raw());
			nomTypes.add(n.nominal());
			// Now, update the environment to include those declared variables
			String var = p.name();
			if (environment.containsKey(var)) {
				syntaxError(errorMessage(VARIABLE_ALREADY_DEFINED, var),
						context, p);
			}
			environment = environment.declare(var, n, n);
		}

		expr.body = propagate(expr.body, environment, context);

		Type.FunctionOrMethod rawType;
		Type.FunctionOrMethod nomType;

		if (Exprs.isPure(expr.body, context)) {
			rawType = Type.Function(expr.body.result().raw(), rawTypes);
			nomType = Type.Function(expr.body.result().nominal(), nomTypes);
		} else {
			rawType = Type.Method(expr.body.result().raw(), rawTypes);
			nomType = Type.Method(expr.body.result().nominal(), nomTypes);
		}

		expr.type = (Nominal.FunctionOrMethod) Nominal.construct(nomType,
				rawType);
		return expr;
	}

	private Expr propagate(Expr.AbstractIndirectInvoke expr,
			Environment environment, Context context) throws IOException,
			ResolveError {

		expr.src = propagate(expr.src, environment, context);
		Nominal.FunctionOrMethod funType = expandAsFunctionOrMethod(expr.src.result());
		if (funType == null) {
			syntaxError("function or method type expected", context, expr.src);
		}

		List<Nominal> paramTypes = funType.params();
		ArrayList<Expr> exprArgs = expr.arguments;

		if (paramTypes.size() != exprArgs.size()) {
			syntaxError(
					"insufficient arguments for function or method invocation",
					context, expr.src);
		}

		for (int i = 0; i != exprArgs.size(); ++i) {
			Nominal pt = paramTypes.get(i);
			Expr arg = propagate(exprArgs.get(i), environment, context);
			checkIsSubtype(pt, arg, context);
			exprArgs.set(i, arg);

		}

		if (funType instanceof Nominal.Function) {
			Expr.IndirectFunctionCall ifc = new Expr.IndirectFunctionCall(
					expr.src, exprArgs, expr.attributes());
			ifc.functionType = (Nominal.Function) funType;
			return ifc;
		} else {
			Expr.IndirectMethodCall imc = new Expr.IndirectMethodCall(expr.src,
					exprArgs, expr.attributes());
			imc.methodType = (Nominal.Method) funType;
			return imc;
		}

	}

	private Expr propagate(Expr.AbstractInvoke expr, Environment environment,
			Context context) throws IOException, ResolveError {

		// first, resolve through receiver and parameters.

		Path.ID qualification = expr.qualification;
		ArrayList<Expr> exprArgs = expr.arguments;
		ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
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
		NameID name = resolveAsName(qualifications, context);

		// third, lookup the appropriate function or method based on the name
		// and given parameter types.
		Nominal.FunctionOrMethod funType = resolveAsFunctionOrMethod(name,
				paramTypes, context);
		if (funType instanceof Nominal.Function) {
			Expr.FunctionCall r = new Expr.FunctionCall(name, qualification,
					exprArgs, expr.attributes());
			r.functionType = (Nominal.Function) funType;
			return r;
		} else {
			Expr.MethodCall r = new Expr.MethodCall(name, qualification,
					exprArgs, expr.attributes());
			r.methodType = (Nominal.Method) funType;
			return r;
		}
	}

	private Expr propagate(Expr.IndexOf expr, Environment environment,
			Context context) throws IOException, ResolveError {
		expr.src = propagate(expr.src, environment, context);
		expr.index = propagate(expr.index, environment, context);
		Nominal.Array srcType = expandAsEffectiveList(expr.src
				.result());

		if (srcType == null) {
			syntaxError(errorMessage(INVALID_ARRAY_EXPRESSION), context,
					expr.src);
		} else {
			expr.srcType = srcType;
		}

		checkIsSubtype(srcType.key(), expr.index, context);

		return expr;
	}

	private Expr propagate(Expr.LengthOf expr, Environment environment,
			Context context) throws IOException, ResolveError {
		expr.src = propagate(expr.src, environment, context);

		Nominal.Array srcType = expandAsEffectiveList(expr.src
				.result());

		if (srcType == null) {
			syntaxError("found " + expr.src.result().nominal()
					+ ", expected string, set, list or dictionary.", context,
					expr.src);
		} else {
			expr.srcType = srcType;
		}

		return expr;
	}

	private Expr propagate(Expr.LocalVariable expr, Environment environment,
			Context context) throws IOException {
		Nominal type = environment.getCurrentType(expr.var);
		expr.type = type;
		return expr;
	}

	private Expr propagate(Expr.ArrayInitialiser expr, Environment environment,
			Context context) {
		Nominal element = Nominal.T_VOID;

		ArrayList<Expr> exprs = expr.arguments;
		for (int i = 0; i != exprs.size(); ++i) {
			Expr e = propagate(exprs.get(i), environment, context);
			Nominal t = e.result();
			exprs.set(i, e);
			element = Nominal.Union(t, element);
		}

		expr.type = Nominal.Array(element, false);

		return expr;
	}
	
	private Expr propagate(Expr.ArrayGenerator expr, Environment environment,
			Context context) {
		expr.element = propagate(expr.element, environment, context);
		expr.count = propagate(expr.count, environment, context);
		expr.type = Nominal.Array(expr.element.result(), true);
		return expr;
	}
	
	private Expr propagate(Expr.Record expr, Environment environment,
			Context context) {

		HashMap<String, Expr> exprFields = expr.fields;
		HashMap<String, Nominal> fieldTypes = new HashMap<String, Nominal>();

		ArrayList<String> fields = new ArrayList<String>(exprFields.keySet());
		for (String field : fields) {
			Expr e = propagate(exprFields.get(field), environment, context);
			Nominal t = e.result();
			exprFields.put(field, e);
			fieldTypes.put(field, t);
		}

		expr.type = Nominal.Record(false, fieldTypes);

		return expr;
	}

	private Expr propagate(Expr.FieldAccess ra, Environment environment,
			Context context) throws IOException, ResolveError {
		ra.src = propagate(ra.src, environment, context);
		Nominal srcType = ra.src.result();
		Nominal.Record recType = expandAsEffectiveRecord(srcType);
		if (recType == null) {
			syntaxError(errorMessage(RECORD_TYPE_REQUIRED, srcType.raw()),
					context, ra);
		}
		Nominal fieldType = recType.field(ra.name);
		if (fieldType == null) {
			syntaxError(errorMessage(RECORD_MISSING_FIELD, ra.name), context,
					ra);
		}
		ra.srcType = recType;
		return ra;
	}

	private Expr propagate(Expr.ConstantAccess expr, Environment environment,
			Context context) throws IOException {
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
			NameID name = resolveAsName(qualifications, context);
			// Second, determine the value of the constant.
			Pair<Constant,Nominal> ct = resolveAsConstant(name);
			expr.value = ct.first();
			expr.type = ct.second();
			return expr;
		} catch (ResolveError e) {
			syntaxError(errorMessage(UNKNOWN_VARIABLE), context, expr);
			return null;
		}
	}

	private Expr propagate(Expr.Dereference expr, Environment environment,
			Context context) throws IOException, ResolveError {
		Expr src = propagate(expr.src, environment, context);
		expr.src = src;
		Nominal.Reference srcType = expandAsReference(src.result());
		if (srcType == null) {
			syntaxError("invalid reference expression", context, src);
		}
		expr.srcType = srcType;
		return expr;
	}

	private Expr propagate(Expr.New expr, Environment environment,
			Context context) {
		expr.expr = propagate(expr.expr, environment, context);
		expr.type = Nominal.Reference(expr.expr.result());
		return expr;
	}

	private Expr propagate(Expr.TypeVal expr, Environment environment,
			Context context) throws IOException {
		expr.type = resolveAsType(expr.unresolvedType, context);		
		return expr;
	}

	// =========================================================================
	// Compute fixed point
	// =========================================================================

	/**
	 * Compute the fixed point of an environment across a body of statements.
	 * The fixed point is the environment which, starting from the initial
	 * environment, doesn't change after being put through body. For example:
	 *
	 * <pre>
	 * x = 1
	 * while i < 10:
	 *    // x -> int, i -> int
	 *    x = null
	 *    i = i + 1
	 *    // x -> null, i -> int
	 * </pre>
	 *
	 * <p>
	 * Here, we see the environment before the loop body, along with that after.
	 * The fixed point for this example, then, is {x -> int|null, i -> int}
	 * </p>
	 *
	 * <p>
	 * <b>NOTE:</b> The fixed-point computation is technically not guaranteed to
	 * terminate (i.e. because the lattice has infinite height). As a simplistic
	 * step, for now, the computatino just bails out after 10 iterations. In
	 * principle, one can do better and this is discussed in the following
	 * paper:
	 *
	 * <ul>
	 * <li>A Calculus for Constraint-Based Flow Typing. David J. Pearce. In
	 * Proceedings of the Workshop on Formal Techniques for Java-like Languages
	 * (FTFJP), Article 7, 2013.</li>
	 * </ul>
	 * (Aaaahhh, the irony that I haven't implemented by own paper :)
	 * </p>
	 *
	 * @param environment
	 *            The initial environment, which is guaranteed not to be changed
	 *            by this method.
	 * @param body
	 *            The statement body which is to be iterated over.
	 * @param condition
	 *            An optional condition which is to be included in the
	 *            computation. Maybe null.
	 * @param doWhile
	 *            Indicates whether this is a do-while loop or not. A do-while
	 *            loop is different because the condition does not hold on the
	 *            first iteration.
	 * @return
	 */
	private Environment computeFixedPoint(Environment environment,
			ArrayList<Stmt> body, Expr condition, boolean doWhile,
			SyntacticElement element) {
		// The count is used simply to guarantee termination.
		int count = 0;
		// The original environment is an exact copy of the initial environment.
		// This is needed to feed into the iteration.
		Environment original = environment.clone();
		// We clone the original environment again to force the refcount > 1
		original = original.clone();
		// Precompute the set of variables to be merged
		Set<String> variables = original.keySet();
		// The old environment is used to compare the environment after one
		// iteration with previous "old" environment to see whether anything has
		// changed.
		Environment old;
		// The temporary environment is used simply to hold the environment in
		// between the condition and the statement body.
		Environment tmp;
		do {
			// First, take a copy of environment so we can later tell whether
			// anything changed.
			old = environment.clone();
			// Second, propagate through condition (if applicable). This may
			// update the environment if one or more type tests are used.
			if (condition != null && !doWhile) {
				tmp = propagateCondition(condition, true, old.clone(), current)
						.second();
			} else {
				tmp = old;
				doWhile = false;
			}
			// Merge updated environment with original environment to produce
			// potentially updated environment.
			environment = original.merge(variables, propagate(body, tmp));
			old.free(); // hacky, but safe
			// Finally, check loop count to force termination
			if (count++ == 10) {
				internalFailure("Unable to type loop", filename, element);
			}
		} while (!environment.equals(old));

		return environment;
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
	 * @return
	 * @throws IOException
	 */
	public Nominal.FunctionOrMethod resolveAsFunctionOrMethod(NameID nid,
			List<Nominal> parameters, Context context) throws IOException,
			ResolveError {

		// Thet set of candidate names and types for this function or method.
		HashSet<Pair<NameID, Nominal.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Nominal.FunctionOrMethod>>();

		// First, add all valid candidates to the list without considering which
		// is the most precise.
		addCandidateFunctionsAndMethods(nid, parameters, candidates, context);

		// Second, add to narrow down the list of candidates to a single choice.
		// If this is impossible, then we have an ambiguity error.
		return selectCandidateFunctionOrMethod(nid.name(), parameters,
				candidates, context).second();
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
	public Pair<NameID, Nominal.FunctionOrMethod> resolveAsFunctionOrMethod(
			String name, Context context) throws IOException, ResolveError {
		return resolveAsFunctionOrMethod(name, null, context);
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
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return
	 * @throws IOException
	 */
	public Pair<NameID, Nominal.FunctionOrMethod> resolveAsFunctionOrMethod(
			String name, List<Nominal> parameters, Context context)
			throws IOException, ResolveError {

		HashSet<Pair<NameID, Nominal.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Nominal.FunctionOrMethod>>();
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
					addCandidateFunctionsAndMethods(nid, parameters,
							candidates, context);
				}
			}
		}

		return selectCandidateFunctionOrMethod(name, parameters, candidates,
				context);
	}

	private boolean paramSubtypes(Type.FunctionOrMethod f1,
			Type.FunctionOrMethod f2) {
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if (f1_params.size() == f2_params.size()) {
			for (int i = 0; i != f1_params.size(); ++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);
				if (!Type.isSubtype(f1_param, f2_param)) {
					return false;
				}
			}

			return true;
		}
		return false;
	}

	private boolean paramStrictSubtypes(Type.FunctionOrMethod f1,
			Type.FunctionOrMethod f2) {
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if (f1_params.size() == f2_params.size()) {
			boolean allEqual = true;
			for (int i = 0; i != f1_params.size(); ++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);
				if (!Type.isSubtype(f1_param, f2_param)) {
					return false;
				}
				allEqual &= f1_param.equals(f2_param);
			}

			// This function returns true if the parameters are a strict
			// subtype. Therefore, if they are all equal it must return false.

			return !allEqual;
		}
		return false;
	}

	private String parameterString(List<Nominal> paramTypes) {
		String paramStr = "(";
		boolean firstTime = true;
		if (paramTypes == null) {
			paramStr += "...";
		} else {
			for (Nominal t : paramTypes) {
				if (!firstTime) {
					paramStr += ",";
				}
				firstTime = false;
				paramStr += t.nominal();
			}
		}
		return paramStr + ")";
	}

	private Pair<NameID, Nominal.FunctionOrMethod> selectCandidateFunctionOrMethod(
			String name, List<Nominal> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates,
			Context context) throws IOException, ResolveError {

		List<Type> rawParameters;
		Type.Function target;

		if (parameters != null) {
			rawParameters = stripNominal(parameters);
			target = (Type.Function) Type.Function(Type.T_ANY,rawParameters);
		} else {
			rawParameters = null;
			target = null;
		}

		NameID candidateID = null;
		Nominal.FunctionOrMethod candidateType = null;
		for (Pair<NameID, Nominal.FunctionOrMethod> p : candidates) {
			Nominal.FunctionOrMethod nft = p.second();
			Type.FunctionOrMethod ft = nft.raw();
			if (parameters == null || paramSubtypes(ft, target)) {
				// this is now a genuine candidate
				if (candidateType == null
						|| paramStrictSubtypes(candidateType.raw(), ft)) {
					candidateType = nft;
					candidateID = p.first();
				} else if (!paramStrictSubtypes(ft, candidateType.raw())) {
					// this is an ambiguous error
					String msg = name + parameterString(parameters)
							+ " is ambiguous";
					// FIXME: should report all ambiguous matches here
					msg += "\n\tfound: " + candidateID + " : "
							+ candidateType.nominal();
					msg += "\n\tfound: " + p.first() + " : "
							+ p.second().nominal();
					throw new ResolveError(msg);
				}
			}
		}

		if (candidateType == null) {
			// second, didn't find matching message so generate error message
			String msg = "no match for " + name + parameterString(parameters);

			for (Pair<NameID, Nominal.FunctionOrMethod> p : candidates) {
				msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
			}

			throw new ResolveError(msg);
		} else {
			// now check protection modifier
			WhileyFile wf = builder.getSourceFile(candidateID.module());
			if (wf != null) {
				if (wf != context.file()) {
					for (WhileyFile.FunctionOrMethod d : wf.declarations(
							WhileyFile.FunctionOrMethod.class,
							candidateID.name())) {
						if (d.parameters.equals(candidateType.params())) {
							if (!d.hasModifier(Modifier.PUBLIC)) {
								String msg = candidateID.module() + "." + name
										+ parameterString(parameters)
										+ " is not visible";
								throw new ResolveError(msg);
							}
						}
					}
				}
			} else {
				WyilFile m = builder.getModule(candidateID.module());
				WyilFile.FunctionOrMethod d = m.functionOrMethod(
						candidateID.name(), candidateType.nominal());
				if (!d.hasModifier(Modifier.PUBLIC)) {
					String msg = candidateID.module() + "." + name
							+ parameterString(parameters) + " is not visible";
					throw new ResolveError(msg);
				}
			}
		}

		return new Pair<NameID, Nominal.FunctionOrMethod>(candidateID,
				candidateType);
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
	private void addCandidateFunctionsAndMethods(NameID nid,
			List<?> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates,
			Context context) throws IOException,ResolveError {
		Path.ID mid = nid.module();

		int nparams = parameters != null ? parameters.size() : -1;

		WhileyFile wf = builder.getSourceFile(mid);
		if (wf != null) {
			for (WhileyFile.FunctionOrMethod f : wf.declarations(
					WhileyFile.FunctionOrMethod.class, nid.name())) {
				if (nparams == -1 || f.parameters.size() == nparams) {
					Nominal.FunctionOrMethod ft = (Nominal.FunctionOrMethod) resolveAsType(
							f.unresolvedType(), f);
					candidates.add(new Pair<NameID, Nominal.FunctionOrMethod>(
							nid, ft));
				}
			}
		} else {
			WyilFile m = builder.getModule(mid);
			for (WyilFile.FunctionOrMethod mm : m
					.functionOrMethods()) {
				if ((mm.isFunction() || mm.isMethod())
						&& mm.name().equals(nid.name())
						&& (nparams == -1 || mm.type().params().size() == nparams)) {
					// FIXME: loss of visibility information (e.g if this
					// function is declared in terms of a protected type)
					Type.FunctionOrMethod t = (Type.FunctionOrMethod) mm.type();
					Nominal.FunctionOrMethod fom;					
					if (t instanceof Type.Function) {
						Type.Function ft = (Type.Function) t;						
						fom = new Nominal.Function(ft, (Type.Function) expander.getUnderlyingType(ft));
					} else {
						Type.Method mt = (Type.Method) t;
						fom = new Nominal.Method(mt, (Type.Method) expander.getUnderlyingType(mt));
					}
					candidates.add(new Pair<NameID, Nominal.FunctionOrMethod>(
							nid, fom));
				}
			}
		}
	}

	private static List<Type> stripNominal(List<Nominal> types) {
		ArrayList<Type> r = new ArrayList<Type>();
		for (Nominal t : types) {
			r.add(t.raw());
		}
		return r;
	}

	// =========================================================================
	// ResolveAsName
	// =========================================================================

	/**
	 * <p>
	 * Responsible for resolve names, types, constants and functions / methods
	 * at the global level. Resolution is determined by the context in which a
	 * given name/type/constant/function/method appears. That is, what imports
	 * are active in the enclosing WhileyFile. For example, consider this:
	 * </p>
	 *
	 * <pre>
	 * import whiley.lang.*
	 * 
	 * type nat is Int.uint
	 * 
	 * import whiley.ui.*
	 * </pre>
	 *
	 * <p>
	 * In this example, the statement "<code>import whiley.lang.*</code>" is
	 * active for the type declaration, whilst the statement "
	 * <code>import whiley.ui.*</code>". The context of the type declaration is
	 * everything in the enclosing file up to the declaration itself. Therefore,
	 * in resolving the name <code>Int.uint</code>, this will examine the
	 * package whiley.lang to see whether a compilation unit named "Int" exists.
	 * If so, it will then resolve the name <code>Int.uint</code> to
	 * <code>whiley.lang.Int.uint</code>.
	 * </p>
	 *
	 * @param name
	 *            A module name without package specifier.
	 * @param context
	 *            --- context in which to resolve.
	 * @return The resolved name.
	 * @throws IOException
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(String name, Context context)
			throws IOException, ResolveError {
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
					if (builder.isName(nid)) {
						// ok, we have found the name in question. But, is it
						// visible?
						if (isNameVisible(nid, context)) {
							return nid;
						} else {
							throw new ResolveError(nid + " is not visible");
						}
					}
				}
			}
		}

		throw new ResolveError("name not found: " + name);
	}

	/**
	 * This methods attempts to resolve the given list of names into a single
	 * named item (e.g. type, method, constant, etc). For example,
	 * <code>["whiley","lang","Math","max"]</code> would be resolved, since
	 * <code>whiley.lang.Math.max</code> is a valid function name. In contrast,
	 * <code>["whiley","lang","Math"]</code> does not resolve since
	 * <code>whiley.lang.Math</code> refers to a module.
	 *
	 * @param names
	 *            A list of components making up the name, which may include the
	 *            package and enclosing module.
	 * @param context
	 *            --- context in which to resolve *
	 * @return The resolved name.
	 * @throws IOException
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(List<String> names, Context context)
			throws IOException, ResolveError {
		if (names.size() == 1) {
			return resolveAsName(names.get(0), context);
		} else if (names.size() == 2) {
			String name = names.get(1);
			Path.ID mid = resolveAsModule(names.get(0), context);
			NameID nid = new NameID(mid, name);
			if (builder.isName(nid)) {
				if (isNameVisible(nid, context)) {
					return nid;
				} else {
					throw new ResolveError(nid + " is not visible");
				}
			}
		} else {
			String name = names.get(names.size() - 1);
			String module = names.get(names.size() - 2);
			Path.ID pkg = Trie.ROOT;
			for (int i = 0; i != names.size() - 2; ++i) {
				pkg = pkg.append(names.get(i));
			}
			Path.ID mid = pkg.append(module);
			NameID nid = new NameID(mid, name);
			if (builder.isName(nid)) {
				if (isNameVisible(nid, context)) {
					return nid;
				} else {
					throw new ResolveError(nid + " is not visible");
				}
			}
		}

		String name = null;
		for (String n : names) {
			if (name != null) {
				name = name + "." + n;
			} else {
				name = n;
			}
		}
		throw new ResolveError("name not found: " + name);
	}

	/**
	 * This method attempts to resolve a name as a module in a given name
	 * context.
	 *
	 * @param name
	 *            --- name to be resolved
	 * @param context
	 *            --- context in which to resolve
	 * @return
	 * @throws IOException
	 */
	public Path.ID resolveAsModule(String name, Context context)
			throws IOException, ResolveError {

		for (WhileyFile.Import imp : context.imports()) {
			Trie filter = imp.filter;
			String last = filter.last();
			if (last.equals("*")) {
				// this is generic import, so narrow the filter.
				filter = filter.parent().append(name);
			} else if (!last.equals(name)) {
				continue; // skip as not relevant
			}

			for (Path.ID mid : builder.imports(filter)) {
				return mid;
			}
		}

		throw new ResolveError("module not found: " + name);
	}

	// =========================================================================
	// ResolveAsType
	// =========================================================================

	public Nominal.Function resolveAsType(SyntacticType.Function t,
			Context context) {
		return (Nominal.Function) resolveAsType((SyntacticType.FunctionOrMethod) t, context);
	}

	public Nominal.Method resolveAsType(SyntacticType.Method t, Context context) { 
		return (Nominal.Method) resolveAsType((SyntacticType.FunctionOrMethod) t, context);
	}

	public Nominal.FunctionOrMethod resolveAsType(SyntacticType.FunctionOrMethod t, Context context) {
		// We need to sanity check the parameter types we have here, since
		// occasionally we can end up with something other than a function type.
		// This may seem surprising, but it can happen when one of the types
		// involved is contractive (normally by accident).
		for (SyntacticType param : t.paramTypes) {
			Nominal nominal = resolveAsType(param, context);
			if (Type.isSubtype(Type.T_VOID, nominal.raw())) {
				syntaxError("empty type encountered", filename, param);
			}
		}
		Nominal ret = resolveAsType(t.returnType, context);
		if (!(t.returnType instanceof SyntacticType.Void) && Type.isSubtype(Type.T_VOID, ret.raw())) {
			syntaxError("empty type encountered", filename, t.returnType);
		}
		return (Nominal.FunctionOrMethod) resolveAsType((SyntacticType) t, context);
	}
	
	/**
	 * Resolve a type in a given context by identifying all unknown names and
	 * replacing them with nominal types. The context is that declaration (e.g.
	 * type, constant, function, etc) which encloses the give type. The context
	 * determines what import statements are visible to help resolving external
	 * names.
	 *
	 * @param type
	 *            --- type to be resolved.
	 * @param context
	 *            --- context in which to resolve the type.
	 * @return
	 * @throws IOException
	 */
	public Nominal resolveAsType(SyntacticType type, Context context) {
		Type nominalType = resolveAsType(type, context, true, false);
		Type rawType = resolveAsType(type, context, false, false);
		return Nominal.construct(nominalType, rawType);
	}

	/**
	 * Resolve a type in a given context by identifying all unknown names and
	 * replacing them with nominal types. In this case, any constrained types
	 * are treated as void. This is critical for properly dealing with type
	 * tests, which may otherwise assume types are unconstrained.
	 *
	 * @param type
	 *            --- type to be resolved.
	 * @param context
	 *            --- context in which to resolve the type.
	 * @return
	 * @throws IOException
	 */
	public Nominal resolveAsUnconstrainedType(SyntacticType type,
			Context context) {
		Type nominalType = resolveAsType(type, context, true, true);
		Type rawType = resolveAsType(type, context, false, true);
		return Nominal.construct(nominalType, rawType);
	}

	private Type resolveAsType(SyntacticType t, Context context,
			boolean nominal, boolean unconstrained) {

		if (t instanceof SyntacticType.Primitive) {
			if (t instanceof SyntacticType.Any) {
				return Type.T_ANY;
			} else if (t instanceof SyntacticType.Void) {
				return Type.T_VOID;
			} else if (t instanceof SyntacticType.Null) {
				return Type.T_NULL;
			} else if (t instanceof SyntacticType.Bool) {
				return Type.T_BOOL;
			} else if (t instanceof SyntacticType.Byte) {
				return Type.T_BYTE;
			} else if (t instanceof SyntacticType.Int) {
				return Type.T_INT;
			} else if (t instanceof SyntacticType.Real) {
				return Type.T_REAL;
			} else {
				internalFailure("unrecognised type encountered ("
						+ t.getClass().getName() + ")", context, t);
				return null; // deadcode
			}
		} else {
			ArrayList<Automaton.State> states = new ArrayList<Automaton.State>();
			HashMap<NameID, Integer> roots = new HashMap<NameID, Integer>();
			resolveAsType(t, context, states, roots, nominal, unconstrained);
			return Type.construct(new Automaton(states));
		}
	}

	/**
	 * The following method resolves a syntactic type in a given context.
	 *
	 * @param type
	 *            --- type to be resolved
	 * @param context
	 *            --- context in which to resolve the type
	 * @param states
	 * @param roots
	 * @return
	 * @throws IOException
	 */
	private int resolveAsType(SyntacticType type, Context context,
			ArrayList<Automaton.State> states, HashMap<NameID, Integer> roots,
			boolean nominal, boolean unconstrained) {

		if (type instanceof SyntacticType.Primitive) {
			return resolveAsType((SyntacticType.Primitive) type, context,
					states);
		}

		int myIndex = states.size();
		int myKind;
		int[] myChildren;
		Object myData = null;
		boolean myDeterministic = true;

		states.add(null); // reserve space for me

		if (type instanceof SyntacticType.Array) {
			SyntacticType.Array lt = (SyntacticType.Array) type;
			myKind = Type.K_LIST;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(lt.element, context, states, roots,
					nominal, unconstrained);
			myData = false;
		} else if (type instanceof SyntacticType.Record) {
			SyntacticType.Record tt = (SyntacticType.Record) type;
			HashMap<String, SyntacticType> ttTypes = tt.types;
			Type.Record.State fields = new Type.Record.State(tt.isOpen,
					ttTypes.keySet());
			Collections.sort(fields);
			myKind = Type.K_RECORD;
			myChildren = new int[fields.size()];
			for (int i = 0; i != fields.size(); ++i) {
				String field = fields.get(i);
				myChildren[i] = resolveAsType(ttTypes.get(field), context,
						states, roots, nominal, unconstrained);
			}
			myData = fields;
		} else if (type instanceof SyntacticType.Nominal) {
			// This case corresponds to a user-defined type. This will be
			// defined in some module (possibly ours), and we need to identify
			// what module that is here, and save it for future use.
			// Furthermore, we need to determine whether the name is visible
			// (i.e. non-private) and/or whether the body of the type is visible
			// (i.e. non-protected).
			SyntacticType.Nominal dt = (SyntacticType.Nominal) type;
			NameID nid;
			try {
				// Determine the full qualified name of this nominal type. This
				// will additionally ensure that the name is visible
				nid = resolveAsName(dt.names, context);
				if (nominal || !isTypeVisible(nid, context)) {
					myKind = Type.K_NOMINAL;
					myData = nid;
					myChildren = Automaton.NOCHILDREN;
				} else {
					// At this point, we're going to expand the given nominal
					// type. We're going to use resolveAsType(NameID,...) to do
					// this which will load the expanded type onto states at the
					// current point. Therefore, we need to remove the initial
					// null we loaded on.
					states.remove(myIndex);
					return resolveAsType(nid, states, roots, unconstrained);
				}
			} catch (ResolveError e) {
				syntaxError(e.getMessage(), context, dt, e);
				return 0; // dead-code
			} catch (SyntaxError e) {
				throw e;
			} catch (Throwable e) {
				internalFailure(e.getMessage(), context, dt, e);
				return 0; // dead-code
			}
		} else if (type instanceof SyntacticType.Negation) {
			SyntacticType.Negation ut = (SyntacticType.Negation) type;
			myKind = Type.K_NEGATION;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element, context, states, roots,
					nominal, unconstrained);
		} else if (type instanceof SyntacticType.Union) {
			SyntacticType.Union ut = (SyntacticType.Union) type;
			ArrayList<SyntacticType.NonUnion> utTypes = ut.bounds;
			myKind = Type.K_UNION;
			myChildren = new int[utTypes.size()];
			for (int i = 0; i != utTypes.size(); ++i) {
				myChildren[i] = resolveAsType(utTypes.get(i), context, states,
						roots, nominal, unconstrained);
			}
			myDeterministic = false;
		} else if (type instanceof SyntacticType.Intersection) {
			internalFailure("intersection types not supported yet", context,
					type);
			return 0; // dead-code
		} else if (type instanceof SyntacticType.Reference) {
			SyntacticType.Reference ut = (SyntacticType.Reference) type;
			myKind = Type.K_REFERENCE;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element, context, states, roots,
					nominal, unconstrained);
		} else {
			SyntacticType.FunctionOrMethod ut = (SyntacticType.FunctionOrMethod) type;			
			ArrayList<SyntacticType> utParamTypes = ut.paramTypes;

			if (ut instanceof SyntacticType.Method) {
				myKind = Type.K_METHOD;
			} else {
				myKind = Type.K_FUNCTION;
			}

			myChildren = new int[utParamTypes.size()+1];

			myChildren[0] = resolveAsType(ut.returnType, context, states, roots,
					nominal, unconstrained);
			for (int i = 0; i != utParamTypes.size(); ++i) {
				SyntacticType pt = utParamTypes.get(i);
				myChildren[i + 1] = resolveAsType(pt, context, states, roots, nominal, unconstrained);
			}
		}

		states.set(myIndex, new Automaton.State(myKind, myData,
				myDeterministic, myChildren));

		return myIndex;
	}

	private int resolveAsType(NameID key, ArrayList<Automaton.State> states,
			HashMap<NameID, Integer> roots, boolean unconstrained)
			throws IOException, ResolveError {

		// First, check the various caches we have
		Integer root = roots.get(key);
		if (root != null) {
			return root;
		}

		// check whether this type is external or not
		WhileyFile wf = builder.getSourceFile(key.module());
		if (wf == null) {
			// indicates a non-local key which we can resolve immediately
			WyilFile mi = builder.getModule(key.module());
			WyilFile.Type td = mi.type(key.name());
			// FIXME: this is a hack to temporarily deal with the situation
			// where a type loaded from a WyIL file is not expanded. In the
			// future, this will be the norm and we'll need to handle this is a
			// better fashion.
			return expander.getTypeHelper(td.type(),false,states,roots);
		}

		WhileyFile.Type td = wf.typeDecl(key.name());
		if (td == null) {			
			throw new ResolveError("type not found: " + key);
		}

		// following is needed to terminate any recursion
		roots.put(key, states.size());
		SyntacticType type = td.parameter.type;

		// now, expand the given type fully
		if (unconstrained && td.invariant.size() > 0) {
			int myIndex = states.size();
			int kind = Type.leafKind(Type.T_VOID);
			Object data = null;
			states.add(new Automaton.State(kind, data, true,
					Automaton.NOCHILDREN));
			return myIndex;
		} else if (type instanceof Type.Leaf) {
			//
			// FIXME: I believe this code is now redundant, and should be
			// removed or updated. The problem is that SyntacticType no longer
			// extends Type.
			//
			int myIndex = states.size();
			int kind = Type.leafKind((Type.Leaf) type);
			Object data = Type.leafData((Type.Leaf) type);
			states.add(new Automaton.State(kind, data, true,
					Automaton.NOCHILDREN));
			return myIndex;
		} else {
			return resolveAsType(type, td, states, roots, false, unconstrained);
		}

		// TODO: performance can be improved here, but actually assigning the
		// constructed type into a cache of previously expanded types cache.
		// This is challenging, in the case that the type may not be complete at
		// this point. In particular, if it contains any back-links above this
		// index there could be an issue.
	}

	private int resolveAsType(SyntacticType.Primitive t, Context context,
			ArrayList<Automaton.State> states) {
		int myIndex = states.size();
		int kind;
		if (t instanceof SyntacticType.Any) {
			kind = Type.K_ANY;
		} else if (t instanceof SyntacticType.Void) {
			kind = Type.K_VOID;
		} else if (t instanceof SyntacticType.Null) {
			kind = Type.K_NULL;
		} else if (t instanceof SyntacticType.Bool) {
			kind = Type.K_BOOL;
		} else if (t instanceof SyntacticType.Byte) {
			kind = Type.K_BYTE;
		} else if (t instanceof SyntacticType.Int) {
			kind = Type.K_INT;
		} else if (t instanceof SyntacticType.Real) {
			kind = Type.K_RATIONAL;
		} else {
			internalFailure("unrecognised type encountered ("
					+ t.getClass().getName() + ")", context, t);
			return 0; // dead-code
		}
		states.add(new Automaton.State(kind, null, true, Automaton.NOCHILDREN));
		return myIndex;
	}

	private static int append(Type type, ArrayList<Automaton.State> states) {
		int myIndex = states.size();
		Automaton automaton = Type.destruct(type);
		Automaton.State[] tStates = automaton.states;
		int[] rmap = new int[tStates.length];
		for (int i = 0, j = myIndex; i != rmap.length; ++i, ++j) {
			rmap[i] = j;
		}
		for (Automaton.State state : tStates) {
			states.add(Automata.remap(state, rmap));
		}
		return myIndex;
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
	public Pair<Constant,Nominal> resolveAsConstant(NameID nid) throws IOException,
			ResolveError {
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
	public Pair<Constant,Nominal> resolveAsConstant(Expr e, Context context) {
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
	private Pair<Constant,Nominal> resolveAsConstant(NameID key, HashSet<NameID> visited)
			throws IOException, ResolveError {
		Pair<Constant,Nominal> result = constantCache.get(key);
		if (result != null) {
			return result;
		} else if (visited.contains(key)) {
			throw new ResolveError("cyclic constant definition encountered ("
					+ key + " -> " + key + ")");
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
				result = new Pair<Constant,Nominal>(cd.resolvedValue,cd.constant.result());
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		} else {
			WyilFile module = builder.getModule(key.module());
			WyilFile.Constant cd = module.constant(key.name());
			if (cd != null) {
				Constant c = cd.constant();
				Nominal t = Nominal.construct(c.type(), expander.getUnderlyingType(c.type()));
				result = new Pair<Constant,Nominal>(c,t);
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
	private Pair<Constant,Nominal> resolveAsConstant(Expr expr, Context context,
			HashSet<NameID> visited) {
		try {
			if (expr instanceof Expr.Constant) {
				Expr.Constant c = (Expr.Constant) expr;
				return new Pair<Constant,Nominal>(c.value,c.result());
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
					NameID nid = resolveAsName(qualifications, context);
					return resolveAsConstant(nid, visited);
				} catch (ResolveError e) {
					syntaxError(errorMessage(UNKNOWN_VARIABLE), context, expr);
					return null;
				}
			} else if (expr instanceof Expr.BinOp) {
				Expr.BinOp bop = (Expr.BinOp) expr;
				Pair<Constant,Nominal> lhs = resolveAsConstant(bop.lhs, context, visited);
				Pair<Constant,Nominal> rhs = resolveAsConstant(bop.rhs, context, visited);
				return new Pair<Constant, Nominal>(evaluate(bop, lhs.first(), rhs.first(), context), lhs.second());
			} else if (expr instanceof Expr.UnOp) {
				Expr.UnOp uop = (Expr.UnOp) expr;
				Pair<Constant,Nominal> lhs = resolveAsConstant(uop.mhs, context, visited);
				return new Pair<Constant, Nominal>(evaluate(uop, lhs.first(), context), lhs.second());
			} else if (expr instanceof Expr.ArrayInitialiser) {
				Expr.ArrayInitialiser nop = (Expr.ArrayInitialiser) expr;
				ArrayList<Constant> values = new ArrayList<Constant>();
				Nominal element = Nominal.T_VOID;
				for (Expr arg : nop.arguments) {
					Pair<Constant,Nominal> e = resolveAsConstant(arg, context, visited);
					values.add(e.first());
					element = Nominal.Union(element, e.second());
				}
				return new Pair<Constant, Nominal>(Constant.V_ARRAY(values),
						Nominal.Array(element, !nop.arguments.isEmpty()));
			} else if (expr instanceof Expr.ArrayGenerator) {
				Expr.ArrayGenerator lg = (Expr.ArrayGenerator) expr;				
				Pair<Constant,Nominal> element = resolveAsConstant(lg.element, context, visited);
				Pair<Constant,Nominal> count = resolveAsConstant(lg.count, context, visited);
				Constant.Array l = evaluate(lg,element.first(),count.first(),context);
				return new Pair<Constant,Nominal>(l,Nominal.Array(element.second(), !l.values.isEmpty()));
			} else if (expr instanceof Expr.Record) {
				Expr.Record rg = (Expr.Record) expr;
				HashMap<String, Constant> values = new HashMap<String, Constant>();
				HashMap<String, Nominal> types =  new HashMap<String, Nominal>();
				for (Map.Entry<String, Expr> e : rg.fields.entrySet()) {
					Pair<Constant,Nominal> v = resolveAsConstant(e.getValue(), context,
							visited);
					if (v == null) {
						return null;
					}
					values.put(e.getKey(), v.first());
					types.put(e.getKey(), v.second());
				}
				return new Pair<Constant,Nominal>(Constant.V_RECORD(values),Nominal.Record(false, types));
			} else if (expr instanceof Expr.FunctionOrMethod) {
				// TODO: add support for proper lambdas
				Expr.FunctionOrMethod f = (Expr.FunctionOrMethod) expr;
				return new Pair<Constant, Nominal>(Constant.V_LAMBDA(f.nid, f.type.nominal()), f.type);
			}
		} catch (SyntaxError.InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			internalFailure(e.getMessage(), context, expr, e);
		}

		internalFailure("unknown constant expression: "
				+ expr.getClass().getName(), context, expr);
		return null; // deadcode
	}

	/**
	 * Determine whether a name is visible in a given context. This effectively
	 * corresponds to checking whether or not the already name exists in the
	 * given context; or, a public or protected named is imported from another
	 * file.
	 *
	 * @param nid
	 *            Name to check modifiers of
	 * @param context
	 *            Context in which we are trying to access named item
	 *
	 * @return True if given context permitted to access name
	 * @throws IOException
	 */
	public boolean isNameVisible(NameID nid, Context context)
			throws IOException {
		// Any element in the same file is automatically visible
		if (nid.module().equals(context.file().module)) {
			return true;
		} else {
			return hasModifier(nid, context, Modifier.PUBLIC);
		}
	}

	/**
	 * Determine whether a named type is fully visible in a given context. This
	 * effectively corresponds to checking whether or not the already type
	 * exists in the given context; or, a public type is imported from another
	 * file.
	 *
	 * @param nid
	 *            Name to check modifiers of
	 * @param context
	 *            Context in which we are trying to access named item
	 *
	 * @return True if given context permitted to access name
	 * @throws IOException
	 */
	public boolean isTypeVisible(NameID nid, Context context)
			throws IOException {
		// Any element in the same file is automatically visible
		if (nid.module().equals(context.file().module)) {
			return true;
		} else {
			return hasModifier(nid, context, Modifier.PUBLIC);
		}
	}

	/**
	 * Determine whether a named item has a modifier matching one of a given
	 * list. This is particularly useful for checking visibility (e.g. public,
	 * private, etc) of named items.
	 *
	 * @param nid
	 *            Name to check modifiers of
	 * @param context
	 *            Context in which we are trying to access named item
	 * @param modifiers
	 *
	 * @return True if given context permitted to access name
	 * @throws IOException
	 */
	public boolean hasModifier(NameID nid, Context context, Modifier modifier)
			throws IOException {
		Path.ID mid = nid.module();

		// Attempt to access source file first.
		WhileyFile wf = builder.getSourceFile(mid);
		if (wf != null) {
			// Source file location, so check visible of element.
			WhileyFile.NamedDeclaration nd = wf.declaration(nid.name());
			return nd != null && nd.hasModifier(modifier);
		} else {
			// Source file not being compiled, therefore attempt to access wyil
			// file directly.

			// we have to do the following basically because we don't load
			// modifiers properly out of jvm class files (at the moment).
			// return false;
			WyilFile w = builder.getModule(mid);
			List<WyilFile.Block> blocks = w.blocks();
			for (int i = 0; i != blocks.size(); ++i) {
				WyilFile.Block d = blocks.get(i);
				if (d instanceof WyilFile.Declaration) {
					WyilFile.Declaration nd = (WyilFile.Declaration) d;
					return nd != null && nd.hasModifier(modifier);
				}
			}
			return false;
		}
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
	private Constant evaluate(Expr.UnOp operator, Constant operand,
			Context context) {
		switch (operator.op) {
		case NOT:
			if (operand instanceof Constant.Bool) {
				Constant.Bool b = (Constant.Bool) operand;
				return Constant.V_BOOL(!b.value);
			}
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context,
					operator);
			break;
		case NEG:
			if (operand instanceof Constant.Integer) {
				Constant.Integer b = (Constant.Integer) operand;
				return Constant.V_INTEGER(b.value.negate());
			} else if (operand instanceof Constant.Decimal) {
				Constant.Decimal b = (Constant.Decimal) operand;
				return Constant.V_DECIMAL(b.value.negate());
			}
			syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context,
					operator);
			break;
		case INVERT:
			if (operand instanceof Constant.Byte) {
				Constant.Byte b = (Constant.Byte) operand;
				return Constant.V_BYTE((byte) ~b.value);
			}
			break;
		}
		syntaxError(errorMessage(INVALID_UNARY_EXPRESSION), context, operator);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant v1, Constant v2,
			Context context) {
		Type v1_type = v1.type();
		Type v2_type = v2.type();
		Type lub = Type.Union(v1_type, v2_type);

		// FIXME: there are bugs here related to coercions.

		if (Type.isSubtype(Type.T_BOOL, lub)) {
			return evaluateBoolean(bop, (Constant.Bool) v1, (Constant.Bool) v2,
					context);
		} else if (Type.isSubtype(Type.T_INT, lub)) {
			return evaluate(bop, (Constant.Integer) v1, (Constant.Integer) v2,
					context);
		} else if (Type.isSubtype(Type.T_REAL, v1_type)
				&& Type.isSubtype(Type.T_REAL, v1_type)) {
			if (v1 instanceof Constant.Integer) {
				Constant.Integer i1 = (Constant.Integer) v1;
				v1 = Constant.V_DECIMAL(new BigDecimal(i1.value));
			} else if (v2 instanceof Constant.Integer) {
				Constant.Integer i2 = (Constant.Integer) v2;
				v2 = Constant.V_DECIMAL(new BigDecimal(i2.value));
			}
			return evaluate(bop, (Constant.Decimal) v1, (Constant.Decimal) v2,
					context);
		} else if (Type.isSubtype(Type.T_ARRAY_ANY, lub)) {
			return evaluate(bop, (Constant.Array) v1, (Constant.Array) v2,
					context);
		} 
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluateBoolean(Expr.BinOp bop, Constant.Bool v1,
			Constant.Bool v2, Context context) {
		switch (bop.op) {
		case AND:
			return Constant.V_BOOL(v1.value & v2.value);
		case OR:
			return Constant.V_BOOL(v1.value | v2.value);
		case XOR:
			return Constant.V_BOOL(v1.value ^ v2.value);
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Integer v1,
			Constant.Integer v2, Context context) {
		switch (bop.op) {
		case ADD:
			return Constant.V_INTEGER(v1.value.add(v2.value));
		case SUB:
			return Constant.V_INTEGER(v1.value.subtract(v2.value));
		case MUL:
			return Constant.V_INTEGER(v1.value.multiply(v2.value));
		case DIV:
			return Constant.V_INTEGER(v1.value.divide(v2.value));
		case REM:
			return Constant.V_INTEGER(v1.value.remainder(v2.value));
		}
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Decimal v1,
			Constant.Decimal v2, Context context) {
		switch (bop.op) {
		case ADD:
			return Constant.V_DECIMAL(v1.value.add(v2.value));
		case SUB:
			return Constant.V_DECIMAL(v1.value.subtract(v2.value));
		case MUL:
			return Constant.V_DECIMAL(v1.value.multiply(v2.value));
		case DIV:
			return Constant.V_DECIMAL(v1.value.divide(v2.value));
		}
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context, bop);
		return null;
	}

	private Constant.Array evaluate(Expr.ArrayGenerator bop, Constant element,
			Constant count, Context context) {
		if(count instanceof Constant.Integer) {
			Constant.Integer c = (Constant.Integer)count;
			ArrayList<Constant> items = new ArrayList<Constant>();
			for(int i=0;i!=c.value.intValue();++i) {
				items.add(element);
			}
			return Constant.V_ARRAY(items);
		}
		syntaxError(errorMessage(INVALID_ARRAY_EXPRESSION), context, bop);
		return null;
	}
	// =========================================================================
	// expandAsType
	// =========================================================================

	public Nominal.Array expandAsEffectiveList(Nominal lhs)
			throws IOException, ResolveError {
		Type raw = lhs.raw();
		if (raw instanceof Type.EffectiveArray) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.EffectiveArray)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.Array) Nominal.construct(nominal, raw);
		} else {
			return null;
		}
	}

	public Nominal.Record expandAsEffectiveRecord(Nominal lhs)
			throws IOException, ResolveError {
		Type raw = lhs.raw();

		if (raw instanceof Type.Record) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.Record)) {
				nominal = (Type) raw; // discard nominal information
			}
			return (Nominal.Record) Nominal.construct(nominal, raw);
		} else if (raw instanceof Type.UnionOfRecords) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.UnionOfRecords)) {
				nominal = (Type) raw; // discard nominal information
			}
			return (Nominal.Record) Nominal.construct(nominal, raw);
		}
		{
			return null;
		}
	}

	public Nominal.Reference expandAsReference(Nominal lhs) throws IOException,
			ResolveError {
		Type.Reference raw = Type.effectiveReference(lhs.raw());
		if (raw != null) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.Reference)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.Reference) Nominal.construct(nominal, raw);
		} else {
			return null;
		}
	}

	public Nominal.FunctionOrMethod expandAsFunctionOrMethod(Nominal lhs)
			throws IOException, ResolveError {
		Type.FunctionOrMethod raw = Type.effectiveFunctionOrMethod(lhs.raw());
		if (raw != null) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.FunctionOrMethod)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.FunctionOrMethod) Nominal.construct(nominal, raw);
		} else {
			return null;
		}
	}

	private Type expandOneLevel(Type type) throws IOException, ResolveError {
		if (type instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.name();
			Path.ID mid = nid.module();

			WhileyFile wf = builder.getSourceFile(mid);
			Type r = null;

			if (wf != null) {
				WhileyFile.Declaration decl = wf.declaration(nid.name());
				if (decl instanceof WhileyFile.Type) {
					WhileyFile.Type td = (WhileyFile.Type) decl;
					r = resolveAsType(td.parameter.type, td)
							.nominal();
				}
			} else {
				WyilFile m = builder.getModule(mid);
				WyilFile.Type td = m.type(nid.name());
				if (td != null) {
					r = td.type();
				}
			}
			if (r == null) {
				throw new ResolveError("unable to locate " + nid);
			}
			return expandOneLevel(r);
		} else if (type instanceof Type.Leaf || type instanceof Type.Reference
				|| type instanceof Type.Array || type instanceof Type.Record
				|| type instanceof Type.FunctionOrMethod
				|| type instanceof Type.Negation) {
			return type;
		} else {
			Type.Union ut = (Type.Union) type;
			ArrayList<Type> bounds = new ArrayList<Type>();
			for (Type b : ut.bounds()) {
				bounds.add(expandOneLevel(b));
			}
			return Type.Union(bounds);
		}
	}
	

	private Environment addDeclaredParameters(List<WhileyFile.Parameter> parameters, Environment environment,
			WhileyFile.Context d) {
		for (WhileyFile.Parameter p : parameters) {
			environment = environment.declare(p.name, resolveAsType(p.type, d), resolveAsType(p.type, d));
		}
		return environment;
	}
	
	private Environment addDeclaredParameter(WhileyFile.Parameter parameter, Environment environment,
			WhileyFile.Context d) {
		if(parameter != null) {
			Nominal type = resolveAsType(parameter.type, d);
			return environment.declare(parameter.name, type, type);
		} else {
			return environment;
		}
	}
	

	// =========================================================================
	// Misc
	// =========================================================================

	// Check t1 :> t2
	private void checkIsSubtype(Nominal t1, Nominal t2, SyntacticElement elem) {
		if (!Type.isSubtype(t1.raw(), t2.raw())) {
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.nominal()),
					filename, elem);
		}
	}

	private void checkIsSubtype(Nominal t1, Expr t2) {
		if (!Type.isSubtype(t1.raw(), t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.result()
							.nominal()), filename, t2);
		}
	}

	private void checkIsSubtype(Type t1, Expr t2) {
		if (!Type.isSubtype(t1, t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(errorMessage(SUBTYPE_ERROR, t1, t2.result().nominal()),
					filename, t2);
		}
	}

	// Check t1 :> t2
	private void checkIsSubtype(Nominal t1, Nominal t2, SyntacticElement elem,
			Context context) {
		if (!Type.isSubtype(t1.raw(), t2.raw())) {
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.nominal()),
					context, elem);
		}
	}

	private void checkIsSubtype(Nominal t1, Expr t2, Context context) {
		if (!Type.isSubtype(t1.raw(), t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.result()
							.nominal()), context, t2);
		}
	}

	private void checkIsSubtype(Type t1, Expr t2, Context context) {
		if (!Type.isSubtype(t1, t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(errorMessage(SUBTYPE_ERROR, t1, t2.result().nominal()),
					context, t2);
		}
	}

	// Check t1 <: t2 or t1 <: t3 ...
	private void checkSuptypes(Expr e, Context context, Nominal... types) {
		Nominal t1 = e.result();
		for (Nominal t : types) {
			if (Type.isSubtype(t.raw(), t1.raw())) {
				return; // OK
			}
		}
		// Construct the message
		String msg = "expecting ";
		boolean firstTime = true;
		for (Nominal t : types) {
			if (!firstTime) {
				msg += " or ";
			}
			firstTime = false;
			msg = msg + t.nominal();
		}
		msg += ", found " + t1.nominal();
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
		private final HashMap<String, Nominal> declaredTypes;

		/**
		 * The mapping of variables to their current type.
		 */
		private final HashMap<String, Nominal> currentTypes;

		/**
		 * The reference count, which indicate how many references to this
		 * environment there are. When there is only one reference, then the put
		 * and putAll operations will perform an "inplace" update (i.e. without
		 * cloning the underlying collection).
		 */
		private int count; // refCount

		/**
		 * Construct an empty environment. Initially the reference count is 1.
		 */
		public Environment() {
			count = 1;
			currentTypes = new HashMap<String, Nominal>();
			declaredTypes = new HashMap<String, Nominal>();
		}

		/**
		 * Construct a fresh environment as a copy of another map. Initially the
		 * reference count is 1.
		 */
		private Environment(Environment environment) {
			count = 1;
			this.currentTypes = (HashMap<String, Nominal>) environment.currentTypes
					.clone();
			this.declaredTypes = (HashMap<String, Nominal>) environment.declaredTypes
					.clone();
		}

		/**
		 * Get the type associated with a given variable at the current program
		 * point, or null if that variable is not declared.
		 *
		 * @param variable
		 *            Variable to return type for.
		 * @return
		 */
		public Nominal getCurrentType(String variable) {
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
		public Nominal getDeclaredType(String variable) {
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
		public Environment declare(String variable, Nominal declared,
				Nominal initial) {
			if (declaredTypes.containsKey(variable)) {
				throw new RuntimeException("Variable already declared - "
						+ variable);
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
		public Environment update(String variable, Nominal type) {
			if (!declaredTypes.containsKey(variable)) {
				throw new RuntimeException("Variable not declared - "
						+ variable);
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
				Nominal lhs_t = this.getCurrentType(variable);
				Nominal rhs_t = env.getCurrentType(variable);
				result.declare(variable, this.getDeclaredType(variable),
						Nominal.Union(lhs_t, rhs_t));
			}

			return result;
		}

		/**
		 * Create a fresh copy of this environment. In fact, this operation
		 * simply increments the reference count of this environment and returns
		 * it.
		 */
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

		public String toString() {
			return currentTypes.toString();
		}

		public int hashCode() {
			return currentTypes.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Environment) {
				Environment r = (Environment) o;
				return currentTypes.equals(r.currentTypes);
			}
			return false;
		}
	}

	private static final Environment BOTTOM = new Environment();

}
