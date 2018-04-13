package wyll.core;

import static wyc.lang.WhileyFile.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.check.FlowTypeUtils.Environment;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.LVal;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.Stmt;
import wyc.lang.WhileyFile.Type;
import wyc.util.AbstractVisitor;
import wycc.util.ArrayUtils;
import wycc.util.Pair;
import wyil.type.subtyping.EmptinessTest;
import wyil.type.subtyping.StrictTypeEmptinessTest;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.util.ConcreteTypeExtractor;
import wyil.type.util.ReadWriteTypeExtractor;
import wyll.task.TypeMangler;
import wyll.util.StdTypeMangler;

public class LowLevelDriver<D, S, E extends S> {
	private final NameResolver resolver;
	private final SubtypeOperator subtypeOperator;
	private final ReadWriteTypeExtractor rwTypeExtractor;
	private final ConcreteTypeExtractor concreteTypeExtractor;
	private final TypeMangler mangler;
	private final LowLevel.Visitor<D, S, E> visitor;
	/**
	 * The auxillaries are the list of additional declarations created as
	 * intermediates during the translation process.
	 */
	private final ArrayList<D> auxillaries = new ArrayList<>();

	private final ArrayList<Pair<Type,Type>> runtimeTypeTests = new ArrayList<>();

	public LowLevelDriver(NameResolver resolver, LowLevel.Visitor<D, S, E> visitor) {
		this.resolver = resolver;
		EmptinessTest<SemanticType> strictEmptiness = new StrictTypeEmptinessTest(resolver);
		this.subtypeOperator = new SubtypeOperator(resolver, strictEmptiness);
		this.rwTypeExtractor = new ReadWriteTypeExtractor(resolver, subtypeOperator);
		this.concreteTypeExtractor = new ConcreteTypeExtractor(resolver,strictEmptiness);
		this.mangler = new StdTypeMangler();
		this.visitor = visitor;
	}

	public List<D> visitWhileyFile(WhileyFile wf) {
		auxillaries.clear();
		ArrayList<D> declarations = new ArrayList<>();
		for (Decl decl : wf.getDeclarations()) {
			D d = visitDeclaration(decl);
			if (d != null) {
				declarations.add(d);
			}
		}
		constructRuntimeTypeTests(new Environment());
		declarations.addAll(auxillaries);
		return declarations;
	}

	// ==========================================================================
	// Declarations
	// ==========================================================================

	public D visitDeclaration(Decl decl) {
		switch (decl.getOpcode()) {
		case DECL_importfrom:
		case DECL_import:
			return visitImport((Decl.Import) decl);
		case DECL_staticvar:
			return visitStaticVariable((Decl.StaticVariable) decl);
		case DECL_type:
		case DECL_rectype:
			return visitType((Decl.Type) decl);
		case DECL_function:
		case DECL_method:
		case DECL_property:
			return visitCallable((Decl.Callable) decl);
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public D visitImport(Decl.Import decl) {
		return null;
	}

	public D visitStaticVariable(Decl.StaticVariable decl) {
		E initialiser = null;
		LowLevel.Type type = visitType(decl.getType());
		if (decl.hasInitialiser()) {
			// Construct initial environment
			Environment environment = new Environment();
			// Translate initialiser
			initialiser = visitExpression(decl.getInitialiser(), decl.getType(), environment);
		}
		return visitor.visitStaticVariable(decl.getName().toString(), type, initialiser);
	}

	public D visitType(Decl.Type decl) {
		LowLevel.Type type = visitType(decl.getVariableDeclaration().getType());
		if(decl.getInvariant().size() > 0) {
			auxillaries.add(createInvariantMethod(decl));
		}
		return visitor.visitType(decl.getName().toString(), type);
	}

	public D createInvariantMethod(Decl.Type decl) {
		// Construct initial environment
		Environment environment = new Environment();
		Tuple<Expr> invariant = decl.getInvariant();
		String name = decl.getName().toString() + "$inv";
		LowLevel.Type paramT = visitType(decl.getType());
		LowLevel.Type varT = visitBool(Type.Bool);
		ArrayList<S> body = new ArrayList<>();
		if (invariant.size() == 1) {
			// Simple case: just evaluate and return invariant
			body.add(visitor.visitReturn(visitExpression(invariant.get(0), Type.Bool, environment)));
		} else {
			// Complex case: evaluate each clause in turn
			String var = createTemporaryVariable(decl.getIndex());
			body.add(visitor.visitVariableDeclaration(varT, var, visitor.visitLogicalInitialiser(true)));
			for (int i = 0; i != invariant.size(); ++i) {
				E lhs = visitor.visitVariableAccess(varT, var);
				E rhs = visitExpression(invariant.get(i), Type.Bool, environment);
				rhs = visitor.visitLogicalAnd(lhs, rhs);
				body.add(visitor.visitAssign(lhs, rhs));
			}
			body.add(visitor.visitReturn(visitor.visitVariableAccess(varT, var)));
		}
		ArrayList<wycc.util.Pair<LowLevel.Type, String>> parameters = new ArrayList<>();
		parameters.add(new wycc.util.Pair<>(paramT, decl.getVariableDeclaration().getName().toString()));
		return visitor.visitMethod(name, parameters, varT, body);
	}

	public D visitCallable(Decl.Callable decl) {
		// Construct initial environment
		Environment environment = new Environment();
		// Update environment so this within declared lifetimes
		environment = declareThisWithin(decl, environment);
		// Determine appropriate name mangle
		String name = getMangledName(decl);
		// Construct parameter list
		Tuple<Decl.Variable> parameters = decl.getParameters();
		ArrayList<wycc.util.Pair<LowLevel.Type, String>> nParameters = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.Variable parameter = parameters.get(i);
			LowLevel.Type parameterType = visitType(parameter.getType());
			nParameters.add(new wycc.util.Pair<>(parameterType, parameter.getName().toString()));
		}
		// Determine appropriate return type which properly encodes multiple returns
		// when present. If no return value is given then this is null.
		LowLevel.Type retType = visitType(getMultipleReturnType(decl.getType().getReturns()));
		// Construct function or method body
		if (decl instanceof Decl.FunctionOrMethod) {
			Decl.FunctionOrMethod fm = (Decl.FunctionOrMethod) decl;
			// Done!!
			List<S> body = visitStatement(fm.getBody(),environment);
			return visitor.visitMethod(name, nParameters, retType, body);
		} else {
			Decl.Property property = (Decl.Property) decl;
			Tuple<Expr> invariant = property.getInvariant();
			ArrayList<S> body = new ArrayList<>();
			String var = createTemporaryVariable(decl.getIndex());
			LowLevel.Type varT = visitBool(Type.Bool);
			body.add(visitor.visitVariableDeclaration(varT, var, visitor.visitLogicalInitialiser(true)));
			for (int i = 0; i != invariant.size(); ++i) {
				E lhs = visitor.visitVariableAccess(varT, var);
				E rhs = visitExpression(invariant.get(i), Type.Bool, environment);
				rhs = visitor.visitLogicalAnd(lhs, rhs);
				body.add(visitor.visitAssign(lhs, rhs));
			}
			body.add(visitor.visitReturn(visitor.visitVariableAccess(varT, var)));
			return visitor.visitMethod(name, nParameters, retType, body);
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
	public Environment declareThisWithin(Decl.Callable decl, Environment environment) {
		if (decl instanceof Decl.Method) {
			Decl.Method method = (Decl.Method) decl;
			environment = environment.declareWithin("this", method.getLifetimes());
		}
		return environment;
	}

	// ==========================================================================
	// Statements
	// ==========================================================================

	public List<S> visitStatement(Stmt stmt, Environment environment) {
		switch (stmt.getOpcode()) {
		case STMT_assign:
			return visitAssign((Stmt.Assign) stmt, environment);
		case STMT_block:
			return visitBlock((Stmt.Block) stmt, environment);
		case STMT_namedblock:
			return visitNamedBlock((Stmt.NamedBlock) stmt, environment);
		default: {
			S s = visitUnitStatement(stmt, environment);
			if (s == null) {
				return Collections.EMPTY_LIST;
			} else {
				ArrayList<S> list = new ArrayList<>();
				list.add(s);
				return list;
			}
		}
		}
	}

	/**
	 * A unit statement is simply one whose translation is guaranteed to produce at
	 * most a single statement.
	 *
	 * @param stmt
	 * @return
	 */
	public S visitUnitStatement(Stmt stmt, Environment environment) {
		switch (stmt.getOpcode()) {
		case DECL_variable:
		case DECL_variableinitialiser:
			return visitVariable((Decl.Variable) stmt, environment);
		case STMT_assert:
			return visitAssert((Stmt.Assert) stmt, environment);
		case STMT_assume:
			return visitAssume((Stmt.Assume) stmt, environment);
		case STMT_break:
			return visitBreak((Stmt.Break) stmt, environment);
		case STMT_continue:
			return visitContinue((Stmt.Continue) stmt, environment);
		case STMT_debug:
			return visitDebug((Stmt.Debug) stmt, environment);
		case STMT_dowhile:
			return visitDoWhile((Stmt.DoWhile) stmt, environment);
		case STMT_fail:
			return visitFail((Stmt.Fail) stmt, environment);
		case STMT_if:
		case STMT_ifelse:
			return visitIfElse((Stmt.IfElse) stmt, environment);
		case EXPR_invoke: {
			Expr.Invoke ivk = (Expr.Invoke) stmt;
			return visitInvoke(ivk, Type.Void, environment);
		}
		case EXPR_indirectinvoke: {
			Expr.IndirectInvoke ivk = (Expr.IndirectInvoke) stmt;
			return visitIndirectInvoke(ivk, ivk.getType(), environment);
		}
		case STMT_return:
			return visitReturn((Stmt.Return) stmt, environment);
		case STMT_skip:
			return visitSkip((Stmt.Skip) stmt, environment);
		case STMT_switch:
			return visitSwitch((Stmt.Switch) stmt, environment);
		case STMT_while:
			return visitWhile((Stmt.While) stmt, environment);
		default:
			throw new IllegalArgumentException("unknown statement encountered (" + stmt.getClass().getName() + ")");
		}
	}

	public S visitVariable(Decl.Variable stmt, Environment environment) {
		LowLevel.Type type = visitType(stmt.getType());
		E initialiser = null;
		if (stmt.hasInitialiser()) {
			initialiser = visitExpression(stmt.getInitialiser(), stmt.getType(), environment);
		}
		return visitor.visitVariableDeclaration(type, stmt.getName().toString(), initialiser);
	}

	public S visitAssert(Stmt.Assert stmt, Environment environment) {
		E condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		return visitor.visitAssert(condition);
	}

	public S visitAssume(Stmt.Assume stmt, Environment environment) {
		E condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		return visitor.visitAssert(condition);
	}

	public List<S> visitAssign(Stmt.Assign stmt, Environment environment) {
		// Check whether or not we have to resort to a more complex translation of the
		// assignment statement.
		if (hasMultipleExpression(stmt) || hasInterference(stmt)) {
			return translateComplexAssign(stmt, environment);
		} else {
			return translateSimpleAssign(stmt, environment);
		}
	}

	/**
	 * Translate a "simple statement". This can be a multiple assignment, but it
	 * cannot have a multiple expression on the right-hand side *or* any
	 * interference. Thus, it is translated directly as a sequence of simple
	 * assignments. For example:
	 *
	 * <pre>
	 * x,y = 1,a+2
	 * </pre>
	 *
	 * This is translated as the following sequence of assignments:
	 *
	 * <pre>
	 * x = 1
	 * y = a+2
	 * </pre>
	 *
	 * Such a translation offers the simplest and cleanest representation of the
	 * original code. But, it is only safe with the right conditions.
	 */
	public List<S> translateSimpleAssign(Stmt.Assign stmt, Environment environment) {
		// ASSERT: |lvals| == |rvals|
		Tuple<LVal> lvals = stmt.getLeftHandSide();
		Tuple<Expr> rvals = stmt.getRightHandSide();
		ArrayList<S> stmts = new ArrayList<>();
		//
		for (int i = 0; i != lvals.size(); ++i) {
			LVal lval = lvals.get(i);
			E lhs = visitLVal(lval, environment);
			E rhs = visitExpression(rvals.get(i), lval.getType(), environment);
			stmts.add(visitor.visitAssign(lhs, rhs));
		}
		//
		return stmts;
	}

	/**
	 * <p>
	 * Translate a complex assignment. This is multiple assignment which either
	 * contains a multiple expression on the right-hand side or has some form of
	 * assignment interference. For example:
	 * </p>
	 *
	 * <pre>
	 * function swap(int x, int y) -> (int a, int b):
	 *    return y, x
	 *
	 * ...
	 *
	 * c,d = swap(c,d)
	 * </pre>
	 *
	 * <p>
	 * This illustrates a multiple expression on the right hand side of the
	 * assignment. The following illustrates interference:
	 * </p>
	 *
	 * <pre>
	 * x,y = y,x
	 * </pre>
	 *
	 * <p>
	 * The problem here is that the assignment to <code>x</code> on the left-hand
	 * side interferes with its read on the right-hand side (i.e. read-after-write
	 * interference). This issue of interference is resolved through the use of
	 * temporary variables. For example, we might translate the above multiple
	 * assignment as follows:
	 * </p>
	 *
	 * <pre>
	 * int tmp1 = y
	 * int tmp2 = x
	 * x = tmp1
	 * y = tmp2
	 * </pre>
	 *
	 * <p>
	 * This is a little more ugly, but has the obvious advantage of being correct.
	 * To handle multiple returns from functions, we wrap them in records as
	 * follows:
	 * </p>
	 *
	 * <pre>
	 * function f(int x) -> (int a, int b):
	 *    ...
	 *
	 * ...
	 * x,y = f(0)
	 * </pre>
	 *
	 * <p>
	 * Since we assume the underlying platform cannot handle a multiple assignment
	 * like this, we translate the above into something like this:
	 * </p>
	 *
	 * <pre>
	 * function f(int x) -> {int a, int b}:
	 *    ...
	 *
	 * ...
	 * {int a, int b} tmp = f(0)
	 * x = tmp.a
	 * y = tmp.b
	 * </pre>
	 *
	 * <p>
	 * Again, this translation is a little ugly but it basically works quite well.
	 * </p>
	 *
	 * @param stmt
	 * @param context
	 * @return
	 */
	public List<S> translateComplexAssign(Stmt.Assign stmt, Environment environment) {
		Tuple<LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		ArrayList<S> stmts = new ArrayList<>();
		Type[] temporaryTypes = new Type[rhs.size()];
		String[] temporaryVars = new String[rhs.size()];
		// Declare temporary variables with appropriate types and initialise them with
		// the rvals of this assignment. For the case of rvals with multiple returns,
		// generate the appropriate wrapper type.
		for (int i = 0; i != rhs.size(); ++i) {
			Expr rval = rhs.get(i);
			E initialiser;
			// Create a temporary variable name. This should not clash with any other
			// variables in the given scope.
			temporaryVars[i] = createTemporaryVariable(rval.getIndex());
			// Determine appropriate type for variable and translate initialiser.
			if (rval.getTypes() == null) {
				temporaryTypes[i] = rval.getType();
				initialiser = visitExpression(rval, rval.getType(), environment);
			} else {
				temporaryTypes[i] = getMultipleReturnType(rval.getTypes());
				initialiser = visitMultipleExpression(rval, rval.getTypes(), environment);
			}
			LowLevel.Type temporaryType = visitType(temporaryTypes[i]);
			stmts.add(visitor.visitVariableDeclaration(temporaryType, temporaryVars[i], initialiser));
		}
		// For each lval create an appropriate assignment. We need take care here to
		// ensure that, in the case of a multiple return, we extract the correct field
		// from the temporaries wrapper (i.e. record) type.
		for (int i = 0, j = 0; i != rhs.size(); ++i) {
			Expr rv = rhs.get(i);
			Tuple<Type> rhsTypes = rv.getTypes();
			String temporaryVar = temporaryVars[i];
			Type temporaryType = temporaryTypes[i];
			LowLevel.Type llTemporaryType = visitType(temporaryType);
			if (rhsTypes == null) {
				// Easy case for single assignments
				Expr lv = lhs.get(j++);
				E lval = visitExpression(lv, lv.getType(), environment);
				E rval = visitor.visitVariableAccess(llTemporaryType, temporaryVar);
				// Apply any coercions required of the assignment.
				rval = applyCoercion(lv.getType(), temporaryType, rval, environment);
				stmts.add(visitor.visitAssign(lval, rval));
			} else {
				// Harder case for multiple assignments. First, store return value into
				// temporary register. Then load from that. At this time, the only way to have a
				// multiple return is via some kind of invocation.
				LowLevel.Type.Record llRecT = (LowLevel.Type.Record) llTemporaryType;
				for (int k = 0; k != rhsTypes.size(); ++k) {
					Expr lv = lhs.get(j++);
					E lval = visitExpression(lv, lv.getType(), environment);
					E rval = visitor.visitVariableAccess(llTemporaryType, temporaryVar);
					rval = visitor.visitRecordAccess(llRecT, rval, "f" + k);
					// Apply any coercions required of the assignment.
					rval = applyCoercion(lv.getType(), rhsTypes.get(k), rval, environment);
					stmts.add(visitor.visitAssign(lval, rval));
				}
			}
		}
		// Done.
		return stmts;
	}

	/**
	 * Check whether a given assignment has a multiple expression on the right-hand
	 * side. This amounts to checking whether or not the number of expressions on
	 * the right-hand size matches the number of lvals on the left-hand side. This
	 * is necessary to determine when we need to handle multiple assignments.
	 *
	 * @param stmt
	 * @return
	 */
	public boolean hasMultipleExpression(Stmt.Assign stmt) {
		return stmt.getLeftHandSide().size() != stmt.getRightHandSide().size();
	}

	/**
	 * Check whether or not a variable modified by an assignment will interfere with
	 * an expression on the right-hand side of the assignment. The following
	 * illustrates this:
	 *
	 * <pre>
	 * function swap(int x, int y) -> (int a, int b):
	 *    x,y = y,x
	 *    return x,y
	 * </pre>
	 *
	 * The interference comes in the assignment. We can see it more clearly if we
	 * attempt to naively translate this multiple assignment into a series of simple
	 * assignments:
	 *
	 * <pre>
	 *   x = y
	 *   y = x
	 * </pre>
	 *
	 * This obviously does not achieve the intended aim, since the original value of
	 * <code>x</code> is lost. Thus, the assignment to <code>x</code> is said
	 * "interfere" with the assignment to <code>y<code>.
	 *
	 * @param stmt
	 * @return
	 */
	public boolean hasInterference(Stmt.Assign stmt) {
		Tuple<LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		for (int i = 0; i != lhs.size(); ++i) {
			Decl.Variable lval = extractVariable(lhs.get(i));
			if (lval != null) {
				for (int j = (i + 1); j != rhs.size(); ++j) {
					// FIXME: this loop could be optimised as there are situations with interference
					// between lhs and rhs which are not actual instances of interference.
					if (hasInterference(lval, rhs.get(j))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Check whether a given rhs expression "interferes" with a given lhs variable.
	 * That is, whether or not it uses the variable.
	 *
	 * @param lval
	 * @param rhs
	 * @return
	 */
	public boolean hasInterference(Decl.Variable lval, Expr rhs) {
		HashSet<Decl.Variable> uses = new HashSet<>();
		AbstractVisitor visitor = new AbstractVisitor() {
			@Override
			public void visitVariableAccess(Expr.VariableAccess var) {
				uses.add(var.getVariableDeclaration());
			}
		};
		visitor.visitExpression(rhs);
		return uses.contains(lval);
	}

	public Decl.Variable extractVariable(LVal lval) {
		switch (lval.getOpcode()) {
		case EXPR_variablecopy:
		case EXPR_variablemove: {
			Expr.VariableAccess va = (Expr.VariableAccess) lval;
			return va.getVariableDeclaration();
		}
		case EXPR_arrayaccess:
		case EXPR_arrayborrow: {
			Expr.ArrayAccess aa = (Expr.ArrayAccess) lval;
			return extractVariable((LVal) aa.getFirstOperand());
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess aa = (Expr.RecordAccess) lval;
			return extractVariable((LVal) aa.getOperand());
		}
		case EXPR_dereference:
			return null;
		default:
			throw new IllegalArgumentException("unknown lval encountered (" + lval.getClass().getName() + ")");
		}
	}

	public List<S> visitBlock(Stmt.Block stmt, Environment environment) {
		ArrayList<S> block = new ArrayList<>();
		for (int i = 0; i != stmt.size(); ++i) {
			block.addAll(visitStatement(stmt.get(i),environment));
		}
		return block;
	}

	public S visitBreak(Stmt.Break stmt, Environment environment) {
		return visitor.visitBreak();
	}

	public S visitContinue(Stmt.Continue stmt, Environment environment) {
		return visitor.visitContinue();
	}

	public S visitDebug(Stmt.Debug stmt, Environment environment) {
		return null;
	}

	/**
	 * Translation of DoWhile statements is relatively straightforward. The only
	 * interesting issue is how loop invariants are checked in debug mode.
	 *
	 * @param stmt
	 * @return
	 */
	public S visitDoWhile(Stmt.DoWhile stmt, Environment environment) {
		E condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		Stmt.Block block = stmt.getBody();
		ArrayList<S> body = new ArrayList<>();
		for (int i = 0; i != block.size(); ++i) {
			body.addAll(visitStatement(block.get(i),environment));
		}
		return visitor.visitDoWhile(condition, body);
	}

	public S visitFail(Stmt.Fail stmt, Environment environment) {
		return visitor.visitAssert(visitor.visitLogicalInitialiser(false));
	}

	public S visitIfElse(Stmt.IfElse stmt, Environment environment) {
		List<wycc.util.Pair<E, List<S>>> branches = new ArrayList<>();
		// First, translate true branch
		E condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		List<S> trueBranch = visitBlock(stmt.getTrueBranch(),environment);
		branches.add(new wycc.util.Pair<>(condition, trueBranch));
		// Second, translate false branch (if applicable)
		if (stmt.hasFalseBranch()) {
			List<S> falseBranch = visitBlock(stmt.getFalseBranch(),environment);
			branches.add(new wycc.util.Pair<>(null, falseBranch));
		}
		// Done
		return visitor.visitIfElse(branches);
	}

	public List<S> visitNamedBlock(Stmt.NamedBlock stmt, Environment environment) {
		return null;
	}

	/**
	 * Translating a return statement is straightforward in most cases. The main
	 * difficulty arises (as usual) from the potential for multiple return values.
	 * There are two main cases: multiple returns, and multiple return values. For
	 * example:
	 *
	 * <pre>
	 * function swap(int x, int y) -> (int a, int b):
	 *    return y,x
	 * </pre>
	 *
	 * This is a relatively straightforward example of a return statement with
	 * multiple returns. It will be translated into something like the following:
	 *
	 * <pre>
	 * function swap(int x, int y) -> {int a, int b}:
	 *    return {a:y,b:x}
	 * </pre>
	 *
	 * The more complicate case arises from a return value which itself has multiple
	 * returns. The following illustrates:
	 *
	 * <pre>
	 *   ...
	 *   return swap(1,2)
	 * </pre>
	 *
	 * Here, we have a more complex situation. The only real way to resolve this is
	 * by storing the result in a temporary variable.
	 *
	 * @param stmt
	 * @return
	 */
	public S visitReturn(Stmt.Return stmt, Environment environment) {
		// FIXME: this will be broken in the case of a method call or other
		// side-effecting operation. The reason being that we may end up duplicating the
		// lhs. When the time comes, we can fix this by introducing an assignment
		// expression. This turns out to be a *very* convenient solution since all
		// target platforms support this. This would also help with the similar problem
		// of multiple returns.
		Tuple<Expr> returns = stmt.getReturns();
		E rval = null;
		Decl.FunctionOrMethod parent = stmt.getAncestor(Decl.FunctionOrMethod.class);
		Tuple<Type> targets = parent.getType().getReturns();
		if (targets.size() == 1) {
			rval = visitExpression(returns.get(0), targets.get(0), environment);
		} else if (targets.size() > 1) {
			// FIXME: I think this is also broken in the case of mutliple return
			// expressions.
			Type.Record type = (Type.Record) getMultipleReturnType(targets);
			LowLevel.Type.Record llType = visitRecord(type);
			Identifier[] fields = new Identifier[targets.size()];
			ArrayList<E> results = new ArrayList<>();
			for (int i = 0; i != fields.length; ++i) {
				fields[i] = new Identifier("f" + i);
				results.add(visitExpression(returns.get(i), targets.get(i), environment));
			}
			rval = visitor.visitRecordInitialiser(llType, results);
		}
		return visitor.visitReturn(rval);
	}

	/**
	 * Translate a switch statement into a low level switch statement. The key
	 * challenge here is that switches in Whiley are much more flexible that
	 * low-level switches. Specifically, the former can switch on arbitrary values
	 * whilst the latter can only switch on integer values. To deal with this, we
	 * assume that the Whiley switch statement can be translated into a low-level
	 * switch. Then, if at some point we realise our assumption is false, we revert
	 * to a chaining approach.
	 *
	 * @param stmt
	 * @return
	 */
	public S visitSwitch(Stmt.Switch stmt, Environment environment) {
		E condition = visitExpression(stmt.getCondition(), Type.Int, environment);
		List<wycc.util.Pair<Integer, List<S>>> branches = new ArrayList<>();
		Tuple<Stmt.Case> cases = stmt.getCases();
		for (int i = 0; i != cases.size(); ++i) {
			Stmt.Case cAse = cases.get(i);
			Tuple<Expr> conditions = cAse.getConditions();
			List<S> body = visitBlock(cAse.getBlock(), environment);
			if (cAse.isDefault()) {
				branches.add(new wycc.util.Pair<>(null, new ArrayList<>(body)));
			} else {
				for (int j = 0; j != conditions.size(); ++j) {
					Integer constant = extractIntegerConstant(conditions.get(j));
					if (constant == null) {
						// FIXME: handle this case
						return visitSwitchChain(stmt, environment);
					}
					branches.add(new wycc.util.Pair<>(constant, new ArrayList<>(body)));
				}
			}
		}
		return visitor.visitSwitch(condition, branches);
	}

	public S visitSwitchChain(Stmt.Switch stmt, Environment environment) {
		Type lhsT = stmt.getCondition().getType();
		LowLevel.Type llLhsT = visitType(lhsT);
		E lhs = visitExpression(stmt.getCondition(), lhsT, environment);
		List<wycc.util.Pair<E, List<S>>> branches = new ArrayList<>();
		Tuple<Stmt.Case> cases = stmt.getCases();
		for (int i = 0; i != cases.size(); ++i) {
			Stmt.Case cAse = cases.get(i);
			Tuple<Expr> conditions = cAse.getConditions();
			List<S> body = visitBlock(cAse.getBlock(),environment);
			for (int j = 0; j != conditions.size(); ++j) {
				Expr e = conditions.get(j);
				E rhs = visitExpression(e, e.getType(), environment);
				LowLevel.Type llRhsT = visitType(e.getType());
				E condition = visitor.visitEqual(llLhsT, llRhsT, lhs, rhs);
				branches.add(new wycc.util.Pair<>(condition, new ArrayList<>(body)));
			}
		}
		return visitor.visitIfElse(branches);
	}

	public S visitSkip(Stmt.Skip stmt, Environment environment) {
		return null;
	}

	/**
	 * Translation of While statements is relatively straightforward. The only
	 * interesting issue is how loop invariants are checked in debug mode.
	 *
	 * @param stmt
	 * @return
	 */
	public S visitWhile(Stmt.While stmt, Environment environment) {
		E condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		Stmt.Block block = stmt.getBody();
		ArrayList<S> body = new ArrayList<>();
		for (int i = 0; i != block.size(); ++i) {
			body.addAll(visitStatement(block.get(i),environment));
		}
		return visitor.visitWhile(condition, body);
	}

	// ==========================================================================
	// LVals
	// ==========================================================================

	/**
	 * Visit an LVal expression. That is, an expression which may appear on the
	 * left-hand side of an assignment. Such expressions require careful attention
	 * in some cases. For example, consider this assignment:
	 *
	 * <pre>
	 * type nint is null|int
	 * type nbool is null|bool
	 * type arr_t is (nint[])|(nbool[])
	 *
	 * function f(arr_t xs) -> arr_t:
	 *    //
	 *    xs[0] = null
	 *    return xs
	 * </pre>
	 *
	 * This should effectively be viewed as follows:
	 *
	 * <pre>
	 *function f(arr_t xs) -> arr_t:
	 *    //
	 *    if xs is nint[]:
	 *       xs[0] = null
	 *    else:
	 *       xs[0] = null
	 *    return xs
	 * </pre>
	 *
	 * In this case, both assignments now have lval's with array type and can be
	 * translated down to concrete low-level array assignments.
	 *
	 * @param lval
	 * @param target
	 * @return
	 */
	public E visitLVal(LVal lval, Environment environment) {
		switch(lval.getOpcode()) {
		case EXPR_dereference:
			return visitDereferenceLVal((Expr.Dereference) lval, environment);
		default:
			return visitExpression(lval,lval.getType(), environment);
		}
	}

	public E visitDereferenceLVal(Expr.Dereference lval, Environment environment) {
		Type.Reference type = extractWriteableReferenceType(lval.getOperand().getType(),environment);
		LowLevel.Type.Reference llType = visitReference(type);
		E operand = visitExpression(lval.getOperand(), type, environment);
		return visitor.visitReferenceAccess(llType,operand);
	}

	// ==========================================================================
	// Expressions
	// ==========================================================================

	/**
	 * Translate a (potentially) multiple expression whose values will be stored in
	 * several locations. In the case that there is only one target type, then this
	 * defaults to translating an expression in the usual fashion. However, in the
	 * case we're expecting more than one result, then it creates an appropriate
	 * wrapper (i.e. record) type to pass as the target type to the standard
	 * translation. For example, consider this:
	 *
	 * <pre>
	 * function swap(int x, int y) -> (int a, int b):
	 *    ...
	 *
	 * function other(...) -> (int a, int b)
	 *    ...
	 *    return swap(x,y)
	 * </pre>
	 *
	 * The <code>return</code> statement will call this method with two
	 * <code>int</code> target types. These are then wrapped into a wrapper and
	 * passed up the chain to the invocation.
	 *
	 * @param expr
	 * @param targets
	 * @return
	 */
	public E visitMultipleExpression(Expr expr, Tuple<Type> targets, Environment environment) {
		Type type;
		if (targets.size() == 1) {
			// Standard case, no multiple return required.
			type = targets.get(0);
		} else {
			// Complex case, multiple return is required.
			type = getMultipleReturnType(targets);
		}
		return visitExpression(expr, type, environment);
	}

	/**
	 * Translate zero or more expressions into a corresponding number of
	 * productions. All translation is deferred to the single expression translator,
	 * including all issues related to coercions.
	 *
	 * @param expr
	 *            Expressions to be translated
	 * @param target
	 *            Target types for each expression. It's assumed this matches the
	 *            number of expressions.
	 * @return
	 */
	public List<E> visitExpressions(Tuple<Expr> exprs, Tuple<Type> targets, Environment environment) {
		// REQUIRES exprs.size() == targets.size();
		ArrayList<E> result = new ArrayList<>();
		for (int i = 0; i != exprs.size(); ++i) {
			result.add(visitExpression(exprs.get(i), targets.get(i), environment));
		}
		return result;
	}

	/**
	 * Translate zero or more expressions into a corresponding number of
	 * productions. All translation is deferred to the single expression translator,
	 * including all issues related to coercions.
	 *
	 * @param expr
	 *            Expressions to be translated
	 * @param target
	 *            Target types for each expression. It's assumed this matches the
	 *            number of expressions.
	 * @return
	 */
	public List<E> visitExpressions(Tuple<Expr> exprs, Type target, Environment environment) {
		// REQUIRES exprs.size() == targets.size();
		ArrayList<E> result = new ArrayList<>();
		for (int i = 0; i != exprs.size(); ++i) {
			result.add(visitExpression(exprs.get(i), target, environment));
		}
		return result;
	}

	/**
	 * Translate a given expression whose value will be stored in a location of a
	 * given target type. Implicit datatype coercions will be inserted as necessary
	 * to ensure a value of the appropriate representation is returned. For example:
	 *
	 * <pre>
	 * int:16 y
	 * int:32 x = y
	 * </pre>
	 *
	 * This will introduce a coercion from <code>int:16</code> to
	 * <code>int:32</code>.
	 *
	 * Finally, since the target type is known to be an atom, tagging is not
	 * necessary.
	 *
	 * @param expr
	 *            The expression being translated.
	 * @param target
	 *            The target type for the result of this expression. If necessary,
	 *            coercions should be inserted to ensure this is the case.
	 * @return
	 */
	public E visitExpression(Expr expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		case EXPR_arraygenerator:
			return visitArrayGenerator((Expr.ArrayGenerator) expr, target, environment);
		case EXPR_arrayinitialiser:
			return visitArrayInitialiser((Expr.ArrayInitialiser) expr, target, environment);
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			return visitArrayAccess((Expr.ArrayAccess) expr, target, environment);
		case EXPR_arraylength:
			return visitArrayLength((Expr.ArrayLength) expr, target, environment);
		case EXPR_bitwisenot:
			return visitBitwiseComplement((Expr.BitwiseComplement) expr, target, environment);
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
			return visitBitwiseNaryOperator((Expr.NaryOperator) expr, target, environment);
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
			return visitBitwiseShiftOperator((Expr.BinaryOperator) expr, target, environment);
		case EXPR_cast:
			return visitCast((Expr.Cast) expr, target, environment);
		case EXPR_constant:
			return visitConstantInitialiser((Expr.Constant) expr, target, environment);
		case EXPR_dereference:
			return visitDereference((Expr.Dereference) expr, target, environment);
		case EXPR_equal:
		case EXPR_notequal:
			return visitEquality((Expr.BinaryOperator) expr, target, environment);
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
			return visitIntegerComparator((Expr.BinaryOperator) expr, target, environment);
		case EXPR_integernegation:
			return visitIntegerNegation((Expr.IntegerNegation) expr, target, environment);
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
			return visitIntegerOperator((Expr.BinaryOperator) expr, target, environment);
		case EXPR_indirectinvoke:
			return visitIndirectInvoke((Expr.IndirectInvoke) expr, target, environment);
		case EXPR_invoke:
			return visitInvoke((Expr.Invoke) expr, target, environment);
		case EXPR_is:
			return visitIs((Expr.Is) expr, environment);
		case DECL_lambda:
			return visitLambda((Decl.Lambda) expr, target, environment);
		case EXPR_lambdaaccess:
			return visitLambdaAccess((Expr.LambdaAccess) expr, target, environment);
		case EXPR_logicalnot:
			return visitLogicalNot((Expr.LogicalNot) expr, environment);
		case EXPR_logiaclimplication:
		case EXPR_logicaliff:
			return visitLogicalBinaryOperator((Expr.BinaryOperator) expr, environment);
		case EXPR_logicaland:
		case EXPR_logicalor:
			return visitLogicalNaryOperator((Expr.NaryOperator) expr, environment);
		case EXPR_logicalexistential:
		case EXPR_logicaluniversal:
			return visitQuantifier((Expr.Quantifier) expr, environment);
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			return visitRecordAccess((Expr.RecordAccess) expr, target, environment);
		case EXPR_recordinitialiser:
			return visitRecordInitialiser((Expr.RecordInitialiser) expr, target, environment);
		case EXPR_staticvariable:
			return visitStaticVariableAccess((Expr.StaticVariableAccess) expr, target, environment);
		case EXPR_variablecopy:
		case EXPR_variablemove:
			return visitVariableAccess((Expr.VariableAccess) expr, target, environment);
		case EXPR_new:
		case EXPR_staticnew:
			return visitNew((Expr.New) expr, target, environment);
		default:
			throw new IllegalArgumentException("invalid expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	/**
	 * <p>
	 * Translation of array accesses is straightforward in the simple case, and
	 * non-trivial in the complex case. A key factor here is that we will not
	 * attempt to change the representation of the source array, since this would be
	 * potentially very expensive. The following illustrates:
	 * </p>
	 *
	 * <pre>
	 * int:16[] xs
	 * int:32 x = xs[0]
	 * </pre>
	 *
	 * <p>
	 * Some kind of coercion is necessary here and there are two ways we can do it.
	 * We could first coerce the entire <code>xs</code> array to be
	 * <code>int:32[]</code> (obviously a bad idea). The alternative is to cast the
	 * result after reading the item from <code>xs[0]</code>.
	 * </p>
	 *
	 * <p>
	 * The second main problem relates to the concept of a <i>readable array
	 * type</i>. For example, the type <code>int[]|bool[]</code> has a readable
	 * array type of <code>(int|bool)[]</code>. This means the following will type
	 * check:
	 * </p>
	 *
	 * <pre>
	 * function read(int[]|bool[] xs) -> (int|bool x):
	 *     return xs[0]
	 * </pre>
	 *
	 * <p>
	 * Whilst the benefits of allowing this may be unclear for arrays, it is
	 * important for records (and the mechanism is largely the same). Eitherway,
	 * performing an optimal translation of this depends somewhat on the underlying
	 * platform. In some cases we can read the element directly without problem; in
	 * otherwise, we need to include an "accessor" function which examines the
	 * relevant type tags.
	 * </p>
	 *
	 * @param expr
	 * @param _target
	 * @return
	 */
	public E visitArrayAccess(Expr.ArrayAccess expr, Type target, Environment environment) {
		Type sourceT = expr.getFirstOperand().getType();
		E source = visitExpression(expr.getFirstOperand(), sourceT, environment);
		// FIXME: should be usize?
		E index = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		List<LowLevel.Type.Array> types = extractArrayTypes(visitType(sourceT));
		E result = visitor.visitArrayAccess(types, source, index);
		// Apply any coercions as necessary. This is especially important here as we
		// won't change the representation of the source array (for performance
		// reasons). Thus, coercions are likely to be required.
		return applyCoercion(target, expr.getType(), result, environment);
	}

	/**
	 * An array generator is not an expression supported in most languages.
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitArrayGenerator(Expr.ArrayGenerator expr, Type target, Environment environment) {
		Type.Array type = extractTargetType(target, (Type.Array) expr.getType());
		E value = visitExpression(expr.getFirstOperand(), type.getElement(), environment);
		E length = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return visitor.visitArrayGenerator(visitArray(type), value, length);
	}

	/**
	 * Construct an appropriate array initialiser. In the simplest case, no
	 * coercions are necessary:
	 *
	 * <pre>
	 * bool[] x = [true,false,true]
	 * </pre>
	 *
	 * In a more complex case, element coercions maybe necessary:
	 *
	 * <pre>
	 * int:16[] x = [1,2,6] // elements => int:16
	 * </pre>
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitArrayInitialiser(Expr.ArrayInitialiser expr, Type.Array target, Environment environment) {
		LowLevel.Type.Array llType = visitArray(target);
		// Translate the initialiser operands
		Tuple<Expr> operands = expr.getOperands();
		List<E> nOperands = new ArrayList<>();
		for (int i = 0; i != operands.size(); i++) {
			nOperands.add(visitExpression(operands.get(i), target.getElement(), environment));
		}
		// Construct the initialiser itself
		E result = visitor.visitArrayInitialiser(llType, nOperands);
		// Apply any coercions as necessary
		return applyCoercion(_target, target, result, environment);
	}

	public E visitArrayLength(Expr.ArrayLength expr, Type target, Environment environment) {
		// FIXME: The following is completely broken because we might have a readable
		// array type which doesn't make sense here.
		Type.Array sourceT = extractTargetArrayType(expr.getOperand().getType(), null, environment);
		LowLevel.Type.Array llType = visitArray(sourceT);
		E source = visitExpression(expr.getOperand(), sourceT, environment);
		E result = visitor.visitArrayLength(llType, source);
		return applyCoercion(target, expr.getType(), result, environment);
	}

	public E visitBitwiseComplement(Expr.BitwiseComplement expr, Type target, Environment environment) {
		E operand = visitExpression(expr.getOperand(), Type.Byte, environment);
		LowLevel.Type.Int llType = visitByte(Type.Byte);
		return visitor.visitBitwiseNot(llType, operand);
	}

	public E visitBitwiseNaryOperator(Expr.NaryOperator expr, Type target, Environment environment) {
		List<E> args = visitExpressions(expr.getOperands(), Type.Byte, environment);
		LowLevel.Type.Int llType = visitByte(Type.Byte);
		E result = args.get(0);
		// Construct final operation
		for (int i = 1; i != args.size(); ++i) {
			switch (expr.getOpcode()) {
			case EXPR_bitwiseand:
				result = visitor.visitBitwiseAnd(llType, result, args.get(i));
				break;
			case EXPR_bitwiseor:
				result = visitor.visitBitwiseOr(llType, result, args.get(i));
				break;
			case EXPR_bitwisexor:
				result = visitor.visitBitwiseXor(llType, result, args.get(i));
				break;
			default:
				throw new IllegalArgumentException("invalid logical operator");
			}
		}
		return result;
	}

	public E visitBitwiseShiftOperator(Expr.BinaryOperator expr, Type target, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), Type.Byte, environment);
		E rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		LowLevel.Type.Int llType = visitByte(Type.Byte);
		switch (expr.getOpcode()) {
		case EXPR_bitwiseshl:
			return visitor.visitBitwiseShl(llType, lhs, rhs);
		case EXPR_bitwiseshr:
			return visitor.visitBitwiseShr(llType, lhs, rhs);
		default:
			throw new IllegalArgumentException("invalid bitwise shift operator");
		}
	}

	/**
	 * A cast forces an upstream coercion. For example, consider this scenario:
	 *
	 * <pre>
	 * int:16 x = ...
	 * int:32 y = (int:32) (x + 1)
	 * </pre>
	 *
	 * This will then force a coercion on <code>x</code> at the point it is read
	 * and, likewise, will result in <code>1</code> being automatically loaded as a
	 * 32bit value.
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitCast(Expr.Cast expr, Type target, Environment environment) {
		E lhs = visitExpression(expr.getOperand(), expr.getType(), environment);
		return applyCoercion(target, expr.getType(), lhs, environment);
	}

	/**
	 * Initialise a primitive value into a location of a specific type. For example:
	 *
	 * <pre>
	 * int:16 x = 0
	 * </pre>
	 *
	 * In this case, we're initialising an <code>int:16</code> location with the bit
	 * representation for zero.
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitConstantInitialiser(Expr.Constant expr, Type target, Environment environment) {
		Value value = expr.getValue();
		E result;
		if (value instanceof Value.Null) {
			result = visitor.visitNullInitialiser();
		} else if (value instanceof Value.Bool) {
			Value.Bool b = (Value.Bool) value;
			result = visitor.visitLogicalInitialiser(b.get());
		} else if (value instanceof Value.Byte) {
			Value.Byte b = (Value.Byte) value;
			LowLevel.Type.Int type = visitor.visitTypeInt(8);
			result = visitor.visitIntegerInitialiser(type, BigInteger.valueOf(b.get() & 0xFF));
		} else if (value instanceof Value.UTF8) {
			Value.UTF8 b = (Value.UTF8) value;
			byte[] bs = b.get();
			ArrayList<E> values = new ArrayList<>();
			LowLevel.Type.Int type = visitor.visitTypeInt(8);
			for (int i = 0; i != bs.length; ++i) {
				values.add(visitor.visitIntegerInitialiser(type, BigInteger.valueOf(bs[i])));
			}
			result = visitor.visitArrayInitialiser(visitor.visitTypeArray(type), values);
		} else {
			Type.Int t = extractTargetIntegerType(target, expr.getType(), environment);
			Value.Int i = (Value.Int) value;
			// FIXME: t should encode the required width. For now, assuming it's always
			// unbounded (i.e. -1)
			LowLevel.Type.Int type = visitor.visitTypeInt(-1);
			result = visitor.visitIntegerInitialiser(type, i.get());
		}
		// Finally, apply any necessary coercions. For example, if this is going into a
		// union.
		return applyCoercion(target, expr.getType(), result, environment);
	}

	public E visitDereference(Expr.Dereference expr, Type target, Environment environment) {
		// Determine reference type being dereferenced
		Type operandT = expr.getOperand().getType();
		Type.Reference type = extractReadableReferenceType(operandT, environment);
		LowLevel.Type.Reference llType = visitReference(type);
		// Translate operand expression
		E operand = visitExpression(expr.getOperand(), type, environment);
		// Construct reference access
		E result = visitor.visitReferenceAccess(llType, operand);
		// Apply coercions as necessary
		return applyCoercion(target, expr.getType(), result, environment);
	}

	/**
	 * Translate an equality expression. There are three main cases. In the first
	 * (simple) case, we are comparing items of primitive type (and the compile
	 * already ensures they are the same). In the second case, we are comparing
	 * compound items of the same type, and this may require some traversal. In the
	 * final (hardest) case, we are comparing one or more items of different type
	 * and this necessarily involves a union somewhere.
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitEquality(Expr.BinaryOperator expr, Type target, Environment environment) {
		Expr lhs = expr.getFirstOperand();
		Expr rhs = expr.getSecondOperand();
		Type lhsT = lhs.getType();
		Type rhsT = rhs.getType();
		LowLevel.Type llLhsT = visitType(lhsT);
		LowLevel.Type llRhsT = visitType(rhsT);
		// Translate operands as need to do this regardless
		E left = visitExpression(lhs, lhsT, environment);
		E right = visitExpression(rhs, rhsT, environment);
		// Apply any coercions as necessary
		boolean lhsSupRhs = isSubtype(lhsT,rhsT, environment);
		boolean rhsSupLhs = isSubtype(rhsT,lhsT, environment);
		if(lhsSupRhs && rhsSupLhs) {
			// In this case, the types are identical and therefore we can simply do nothing.
		} else if(lhsSupRhs) {
			// lhs :> rhs but not rhs :> lhs
			right = applyCoercion(lhsT,rhsT,right, environment);
		} else if(lhsSupRhs) {
			// rhs :> lhs but not lhs :> rhs
			left = applyCoercion(rhsT,lhsT,left, environment);
		} else {
			// Harder case.
			// FIXME:
			throw new RuntimeException("GOT HERE");
		}
		// Now decide what situation we're in.
		if (expr instanceof Expr.Equal) {
			return visitor.visitEqual(llLhsT, llRhsT, left, right);
		} else {
			return visitor.visitNotEqual(llLhsT, llRhsT, left, right);
		}
	}

	public E visitIs(Expr.Is expr, Environment environment) {
		Type lhsT = expr.getOperand().getType();
		Type rhsT = expr.getTestType();
		E operand = visitExpression(expr.getOperand(), lhsT, environment);
		return callRuntimeTypeTest(operand, lhsT, rhsT);
	}

	public E visitIntegerNegation(Expr.IntegerNegation expr, Type target, Environment environment) {
		Type.Int type = extractTargetIntegerType(target, expr.getType(), environment);
		E operand = visitExpression(expr.getOperand(), type, environment);
		LowLevel.Type.Int llType = visitInt(type);
		E result = visitor.visitIntegerNegate(llType, operand);
		return applyCoercion(target, type, result, environment);
	}

	/**
	 * An integer comparator needs to ensure the argument types have the same width,
	 * and must coerce them as necessary. For example:
	 *
	 * <pre>
	 * function f(int:8 x, int:16 y) -> (bool r):
	 *     if x > y:
	 *        return true
	 *     else:
	 *        return false
	 * </pre>
	 *
	 * In order for the comparison to be (in some sense) meaningful we want the
	 * operands to have the same width. Therefore, we coerce <code>x</code> to an
	 * <code>int:32</code>.
	 *
	 * @param expr
	 * @return
	 */
	public E visitIntegerComparator(Expr.BinaryOperator expr, Type target, Environment environment) {
		Type.Int leftT = extractTargetIntegerType(expr.getFirstOperand().getType(), Type.Int, environment);
		Type.Int rightT = extractTargetIntegerType(expr.getSecondOperand().getType(), Type.Int, environment);
		// Determine the "operating type" as this is the only safe type for which the
		// operation can succeed without overflow.
		Type.Int operatingT = max(leftT, rightT);
		// Translate the operands
		E lhs = visitExpression(expr.getFirstOperand(), operatingT, environment);
		E rhs = visitExpression(expr.getSecondOperand(), operatingT, environment);
		E result;
		LowLevel.Type.Int type = visitInt(operatingT);
		// Construct final operation
		switch (expr.getOpcode()) {
		case EXPR_equal:
			result = visitor.visitIntegerEqual(type, lhs, rhs);
			break;
		case EXPR_notequal:
			result = visitor.visitIntegerNotEqual(type, lhs, rhs);
			break;
		case EXPR_integerlessthan:
			result = visitor.visitIntegerLessThan(type, lhs, rhs);
			break;
		case EXPR_integerlessequal:
			result = visitor.visitIntegerLessThanOrEqual(type, lhs, rhs);
			break;
		case EXPR_integergreaterthan:
			result = visitor.visitIntegerGreaterThan(type, lhs, rhs);
			break;
		case EXPR_integergreaterequal:
			result = visitor.visitIntegerGreaterThanOrEqual(type, lhs, rhs);
			break;
		default:
			throw new IllegalArgumentException("invalid integer comparator");
		}
		// Finally, apply a coercion (if necessary) to ensure the result is represented
		// correctly for the target type. This might involve tagging as necessary.
		return applyCoercion(target, Type.Bool, result, environment);
	}

	/**
	 * An integer operator needs to ensure the argument types have the same width
	 * and the result cannot overflow. For example:
	 *
	 * <pre>
	 * int:8 x = ...
	 * int:16 y = ...
	 * int:32 z = x + y
	 * </pre>
	 *
	 * The verified guarantees that the result will fit in an <code>int:32</code>.
	 * Therefore, the operation has to be performed with 32bits worth of precision,
	 * meaning both <code>x</code> and <code>y</code> must be coerced. In contract,
	 * consider this:
	 *
	 * <pre>
	 * int:32 x = ...
	 * int:16 y = ...
	 * int:8 z = x + y
	 * </pre>
	 *
	 * This may seem somewhat counter-intuitive. Again, the verifier will guarantee
	 * the result fits in an <code>int:8</code>. But it doesn't guarantee that
	 * <code>x</code> or <code>y</code> can without losing precision. Hence, again,
	 * the operation is performed with 32bits worth of precision. This means we must
	 * coerce <code>y</code> to an <code>int:32</code> and then coerce the result to
	 * an <code>int:8</code>.
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitIntegerOperator(Expr.BinaryOperator expr, Type target, Environment environment) {
		Type.Int resT = extractTargetIntegerType(target, expr.getType(), environment);
		Type.Int leftT = extractTargetIntegerType(expr.getFirstOperand().getType(), Type.Int, environment);
		Type.Int rightT = extractTargetIntegerType(expr.getSecondOperand().getType(), Type.Int, environment);
		// Determine the "operating type" as this is the only safe type for which the
		// operation can succeed without overflow.
		Type.Int operatingT = max(leftT, rightT, resT);
		// Translate the operands
		E lhs = visitExpression(expr.getFirstOperand(), operatingT, environment);
		E rhs = visitExpression(expr.getSecondOperand(), operatingT, environment);
		E result;
		LowLevel.Type.Int type = visitInt(operatingT);
		// Construct final operation
		switch (expr.getOpcode()) {
		case EXPR_integeraddition:
			result = visitor.visitIntegerAdd(type, lhs, rhs);
			break;
		case EXPR_integersubtraction:
			result = visitor.visitIntegerSubtract(type, lhs, rhs);
			break;
		case EXPR_integermultiplication:
			result = visitor.visitIntegerMultiply(type, lhs, rhs);
			break;
		case EXPR_integerdivision:
			result = visitor.visitIntegerDivide(type, lhs, rhs);
			break;
		case EXPR_integerremainder:
			result = visitor.visitIntegerRemainder(type, lhs, rhs);
			break;
		default:
			throw new IllegalArgumentException("invalid integer operator");
		}
		// Finally, apply a coercion (if necessary) to ensure the result is represented
		// correctly for the target type. This might involve an integer coercion or
		// tagging as necessary.
		return applyCoercion(target, operatingT, result, environment);
	}

	/**
	 * Translate an invocation for a function, method or property into a low level
	 * method invocation. This is relatively straightforward, with the only
	 * complication arising with multiple returns.
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitInvoke(Expr.Invoke expr, Type target, Environment environment) {
		Type.Callable type = expr.getSignature();
		String name = getMangledName(expr.getName().toString(), type);
		LowLevel.Type.Method llType = (LowLevel.Type.Method) visitType(type);
		// Translate invocation arguments
		List<E> arguments = visitExpressions(expr.getOperands(), type.getParameters(), environment);
		// Construct the invocation
		E result = visitor.visitDirectInvocation(llType, name, arguments);
		// Finally, apply a coercion (if necessary) to ensure the result is represented
		// correctly for the target type. Care needs to be taken when handling multiple
		// returns.
		return applyCoercion(target, getMultipleReturnType(type.getReturns()), result, environment);
	}

	public E visitIndirectInvoke(Expr.IndirectInvoke expr, Type target, Environment environment) {
		Expr source = expr.getSource();
		Type.Callable type = extractCallableType(source.getType(), environment);
		LowLevel.Type.Method llType = (LowLevel.Type.Method) visitType(type);
		// Translate indirect target
		E llSource = visitExpression(source, type, environment);
		// Translate invocation arguments
		List<E> llArguments = visitExpressions(expr.getArguments(), type.getParameters(), environment);
		//
		return visitor.visitIndirectInvocation(llType, llSource, llArguments);
	}

	public E visitLambda(Decl.Lambda expr, Type target, Environment environment) {
		// Determine lambda type
		Type.Callable type = extractCallableType(target, environment);
		LowLevel.Type.Method llType = (LowLevel.Type.Method) visitType(type);
		// Update environment
		// Translate lambda body
		E body = visitMultipleExpression(expr.getBody(), type.getReturns(), environment);
		// Construct parameter array
		Tuple<Decl.Variable> exprParameters = expr.getParameters();
		ArrayList<String> parameters = new ArrayList<>();
		for (int i = 0; i != exprParameters.size(); ++i) {
			parameters.add(exprParameters.get(i).getName().get());
		}
		// Done
		return visitor.visitLambda(llType, parameters, body);
	}

	public E visitLambdaAccess(Expr.LambdaAccess expr, Type target, Environment environment) {
		String name = getMangledName(expr.getName().toString(), expr.getSignature());
		LowLevel.Type.Method llType = (LowLevel.Type.Method) visitType(expr.getSignature());
		return visitor.visitLambdaAccess(llType, name);
	}

	public E visitLogicalNot(Expr.LogicalNot expr, Environment environment) {
		E e = visitExpression(expr.getOperand(), Type.Bool, environment);
		return visitor.visitLogicalNot(e);
	}

	public E visitLogicalBinaryOperator(Expr.BinaryOperator expr, Environment environment) {
		// Translate the operands
		E lhs = visitExpression(expr.getFirstOperand(), Type.Bool, environment);
		E rhs = visitExpression(expr.getSecondOperand(), Type.Bool, environment);
		// Construct final operation
		switch (expr.getOpcode()) {
		case EXPR_logiaclimplication:
			E tmp = visitor.visitLogicalNot(lhs);
			return visitor.visitLogicalOr(tmp, rhs);
		case EXPR_logicaliff:
			return visitor.visitLogicalEqual(lhs, rhs);
		default:
			throw new IllegalArgumentException("invalid logical operator");
		}
	}

	public E visitLogicalNaryOperator(Expr.NaryOperator expr, Environment environment) {
		// Translate the operands
		List<E> args = visitExpressions(expr.getOperands(), Type.Bool, environment);
		E result = args.get(0);
		// Construct final operation
		for (int i = 1; i != args.size(); ++i) {
			switch (expr.getOpcode()) {
			case EXPR_logicaland:
				result = visitor.visitLogicalAnd(result, args.get(i));
				break;
			case EXPR_logicalor:
				result = visitor.visitLogicalOr(result, args.get(i));
				break;
			default:
				throw new IllegalArgumentException("invalid logical operator");
			}
		}
		return result;
	}
	/**
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitNew(Expr.New expr, Type target, Environment environment) {
		// Extract target reference type. This is necessary to ensure we create the
		// *right* reference type.
		Type.Reference type = extractReadableReferenceType(target, environment);
		LowLevel.Type.Reference llType = visitReference(type);
		// Translate operand value
		E operand = visitExpression(expr.getOperand(), type.getElement(), environment);
		// Construct record initialiser
		return visitor.visitReferenceInitialiser(llType, operand);
	}

	/**
	 * <p>
	 * Translation of record accesses is straightforward in the simple case, and
	 * non-trivial in the complex case. A key factor here is that we will not
	 * attempt to change the representation of the source record as, in most cases,
	 * this would be more expensive. The following illustrates:
	 * </p>
	 *
	 * <pre>
	 * {int:16 f} rec = ...
	 * int:32 x = rec.f
	 * </pre>
	 *
	 * <p>
	 * Some kind of coercion is necessary here and there are two ways we can do it.
	 * We could first coerce the record <code>reco</code> array to be
	 * <code>{int:32 f}</code> (most likely a bad idea). The alternative is to cast
	 * the result after reading the item from <code>rec.f</code>.
	 * </p>
	 *
	 * <p>
	 * The second main problem relates to the concept of a <i>readable record
	 * type</i>. For example, the type <code>{int f}|{bool f}</code> has a readable
	 * array type of <code>{int|bool f}</code>. This means the following will type
	 * check:
	 * </p>
	 *
	 * <pre>
	 * function read({int f}|{bool f} xs) -> (int|bool x):
	 *     return xs.f
	 * </pre>
	 *
	 * <p>
	 * This feature is actually quite important for enable simple interactions with
	 * record families, and shares similarity with the concept of a "common initial
	 * sequence" in C. However, performing an optimal translation of this depends
	 * somewhat on the underlying platform. In some cases we can read the element
	 * directly without problem; in otherwise, we need to include an "accessor"
	 * function which examines the relevant type tags, and then performs a low-level
	 * read based on this.
	 * </p>
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitRecordAccess(Expr.RecordAccess expr, Type target, Environment environment) {
		Type operandT = expr.getOperand().getType();
		Type.Record type = extractReadableRecordType(operandT, environment);
		//
		E source = visitExpression(expr.getOperand(), operandT, environment);
		// FIXME: this is clearly broken
		LowLevel.Type.Record llType = (LowLevel.Type.Record) visitType(type);
		E result = visitor.visitRecordAccess(llType, source, expr.getField().toString());
		// Apply any coercions as necessary. This is especially important here as we
		// won't change the representation of the source array (for performance
		// reasons). Thus, coercions are likely to be required.
		return applyCoercion(target, expr.getType(), result, environment);
	}

	/**
	 * Construct an appropriate record initialiser. In the simplest case, no
	 * coercions are necessary:
	 *
	 * <pre>
	 * {bool f} rec = {f:false}
	 * </pre>
	 *
	 * In a more complex case, element coercions maybe necessary:
	 *
	 * <pre>
	 * int:8 x
	 * {int:16 f} rec = {f:x} // x => int:16
	 * </pre>
	 *
	 * One complication is that the order of fields between the initialiser and the
	 * target type may not line up properly. For example:
	 *
	 * <pre>
	 * {int x, bool y} f = {y:false,x:0}
	 * </pre>
	 *
	 * In this case, it's important that reorder the initialiser appropriately.
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitRecordInitialiser(Expr.RecordInitialiser expr, Type _target, Environment environment) {
		Type.Record target = extractTargetType(_target, expr.getType());
		LowLevel.Type.Record llType = visitRecord(target);
		// Translate the initialiser operands
		Tuple<Expr> operands = expr.getOperands();
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Type.Field> targets = target.getFields();
		List<E> nOperands = new ArrayList<>();
		// FIXME: there is a problem here with the reordering process, in that it can
		// have observable effects in the case of expressions with side effects. To work
		// around this, we need to first evaluate them in the correct order into
		// temporaries. However, it's worth noting that not all platforms will actually
		// care about this.
		for (int i = 0; i != targets.size(); i++) {
		  Type.Field field = targets.get(i);
			// Determine corresponding field initialiser. For various exciting reasons,
			// these may not line up exactly with the target type.
			int index = getFieldIndex(fields, field.getName());
			// Translate field initialiser into correct position for target type.
			nOperands.add(visitExpression(operands.get(index), field.getType(), environment));
		}
		// Construct the initialiser
		E result = visitor.visitRecordInitialiser(llType, nOperands);
		// Apply any coercions as necessary
		return applyCoercion(_target, target, result, environment);
	}

	/**
	 * Determine the index of a given field within a sequence of field names. It is
	 * assumed that field is present in the sequence.
	 *
	 * @param fields
	 * @param field
	 * @return
	 */
	private int getFieldIndex(Tuple<Identifier> fields, Identifier field) {
		for (int i = 0; i != fields.size(); ++i) {
			if (fields.get(i).equals(field)) {
				return i;
			}
		}
		throw new IllegalArgumentException("invalid field index");
	}

	/**
	 * A variable access operation is relatively straightforward, though care must
	 * be taken to ensure coercions are applied as necessary. This is necessary is
	 * where we are simply assigning the variable to a union, such as follows:
	 *
	 * <pre>
	 * int x = ...
	 * int|null y = x
	 * </pre>
	 *
	 * Here, a tagging operation is required to convert <code>x</code> for
	 * <code>int</code> to <code>int|null</code>.
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitStaticVariableAccess(Expr.StaticVariableAccess expr, Type target, Environment environment) {
		try {
			WhileyFile.Decl.StaticVariable decl = resolver.resolveExactly(expr.getName(),
					WhileyFile.Decl.StaticVariable.class);
			LowLevel.Type llType = visitType(decl.getType());
			E result = visitor.visitStaticVariableAccess(llType, decl.getName().toString());
			// Apply any coercions as necessary to ensure return value is in the correct
			// form.
			return applyCoercion(target, decl.getType(), result, environment);
		} catch (ResolutionError e) {
			// Should be deadcode
			throw new RuntimeException(e);
		}
	}

	/**
	 * A variable access operation is relatively straightforward, though care must
	 * be taken to ensure coercions are applied as necessary. There are two
	 * scenarios which arise. The first is where are simply assigning the variable
	 * to a union, such as follows:
	 *
	 * <pre>
	 * int x = ...
	 * int|null y = x
	 * </pre>
	 *
	 * Here, a tagging operationg is required to convert <code>x</code> for
	 * <code>int</code> to <code>int|null</code>. The second scenario arises from
	 * flow typing:
	 *
	 * <pre>
	 * function f(int|null x) -> (int r):
	 *    if x is int:
	 *       return x
	 *    ...
	 * </pre>
	 *
	 * Here, <code>x</code> needs to be coerced down from <code>int|null</code> to
	 * <code>int</code>. The flow type checker ensures this is a valid thing to do
	 * in this situation.
	 *
	 * @param expr
	 * @param target
	 * @return
	 */
	public E visitVariableAccess(Expr.VariableAccess expr, Type target, Environment environment) {
		Decl.Variable decl = expr.getVariableDeclaration();
		LowLevel.Type llType = visitType(decl.getType());
		E result = visitor.visitVariableAccess(llType, decl.getName().toString());
		// Check whether a clone operation is required or not
		if (!expr.isMove()) {
			// Yes, variable must be cloned
			result = constuctExpressionClone(llType, result);
		}
		// Apply any coercions as necessary to ensure return value is in the correct
		// form.
		return applyCoercion(target, decl.getType(), result, environment);
	}

	/**
	 * Construct a clone of a given expression. For the moment, this just defers to
	 * the low level visitor. However, this is a temporary solution as real
	 * low-level targets, such as C, will require the method be provided.
	 *
	 * @param type
	 *            The type of the expression being clone
	 * @param expr
	 *            The expression being cloned.
	 * @return
	 */
	public E constuctExpressionClone(LowLevel.Type type, E expr) {
		// FIXME: this is not satisfactory.
		return visitor.visitClone(type, expr);
	}

	/**
	 * Quantifier expressions are interesting because they generally have no direct
	 * counterpart in the target language. Instead, they are implemented as for
	 * loops over the array in question. To support the embedding of a statement
	 * block as an expression, we employ internal methods. For example:
	 *
	 * <pre>
	 * assert some { k in 0..|xs| | xs[k] == 0 }
	 * </pre>
	 *
	 * This Whiley statement is translated into the following low-level statement:
	 *
	 * <pre>
	 * assert expr$1(xs);
	 * </pre>
	 *
	 * Where the internal method <code>expr$1</code> is defined as follows:
	 *
	 * <pre>
	 * method bool expr$1(int[] xs) {
	 *   for(int k=0;k!=|xs|;k=k+1) {
	 *      if(xs[k] == 0) {
	 *        return true;
	 *      }
	 *   }
	 *   return false;
	 * }
	 * </pre>
	 *
	 * This provides a relatively straightforward implementation of quantifiers. In
	 * principle, this could be optimised by inlining the method where appropriate.
	 *
	 * @param expr
	 * @return
	 */
	public E visitQuantifier(Expr.Quantifier expr, Environment environment) {
		String name = "expr$" + expr.getIndex();
		Set<WhileyFile.Decl.Variable> uses = determineUsedVariables(expr);
		List<wycc.util.Pair<LowLevel.Type, String>> parameters = constructQuantifierParameters(uses);
		E condition = visitExpression(expr.getOperand(), Type.Bool, environment);
		// Create the method body, which contains the sequence of nested quantifier for
		// loops and the final return statement.
		List<S> body = constructQuantifierBody(expr, 0, condition, environment);
		// Determine whether getting through the loops should return true or false
		E retval = visitor.visitLogicalInitialiser(expr instanceof Expr.UniversalQuantifier);
		body.add(visitor.visitReturn(retval));
		// Create the method body from the loops and the additional return statement
		auxillaries.add(visitor.visitMethod(name, parameters, visitor.visitTypeBool(), body));
		// Declare the method somehow?
		// Create an invocation to the block
		return visitor.visitDirectInvocation(constructQuantifierType(parameters), name,
				constructQuantifierArguments(uses));
	}

	public List<wycc.util.Pair<LowLevel.Type, String>> constructQuantifierParameters(
			Set<WhileyFile.Decl.Variable> uses) {
		ArrayList<wycc.util.Pair<LowLevel.Type, String>> parameters = new ArrayList<>();
		for (WhileyFile.Decl.Variable use : uses) {
			LowLevel.Type type = visitType(use.getType());
			parameters.add(new wycc.util.Pair<>(type, use.getName().toString()));
		}
		return parameters;
	}

	public List<S> constructQuantifierBody(Expr.Quantifier expr, int index, E condition, Environment environment) {
		Tuple<Decl.Variable> parameters = expr.getParameters();
		ArrayList<S> stmts = new ArrayList<>();
		if (index == parameters.size()) {
			// This indicates we are now within the innermost loop body. Therefore, we
			// create the necessary test for the quantifier condition.
			E retval = visitor.visitLogicalInitialiser(expr instanceof Expr.ExistentialQuantifier);
			S retstmt = visitor.visitReturn(retval);
			ArrayList<wycc.util.Pair<E, List<S>>> branches = new ArrayList<>();
			ArrayList<S> trueBlock = new ArrayList<>();
			ArrayList<S> falseBlock = new ArrayList<>();
			branches.add(new wycc.util.Pair<>(condition, trueBlock));
			if(expr instanceof Expr.UniversalQuantifier) {
				falseBlock.add(retstmt);
				branches.add(new wycc.util.Pair<>(null, falseBlock));
			} else {
				trueBlock.add(retstmt);
			}
			stmts.add(visitor.visitIfElse(branches));
		} else {
			// This is the recursive case. For each parameter we create a nested foreach
			// loop which iterates over the given range
			Decl.Variable parameter = parameters.get(index);
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			// FIXME: should be usize
			E start = visitExpression(range.getFirstOperand(), Type.Int, environment);
			E end = visitExpression(range.getSecondOperand(), Type.Int, environment);
			// Construct index variable
			LowLevel.Type.Int varType = visitor.visitTypeInt(-1);
			S var = visitor.visitVariableDeclaration(varType, parameter.getName().get(), start);
			E varAccess = visitor.visitVariableAccess(varType, parameter.getName().get());
			E loopCondition = visitor.visitIntegerLessThan(varType, varAccess, end);
			E one = visitor.visitIntegerInitialiser(varType, BigInteger.ONE);
			S increment = visitor.visitAssign(varAccess, visitor.visitIntegerAdd(varType, varAccess, one));
			// Recursively create nested loops for remaining parameters
			List<S> body = constructQuantifierBody(expr, index + 1, condition, environment);
			// Return the loop for this parameter
			stmts.add(visitor.visitFor(var, loopCondition, increment, body));
		}
		return stmts;
	}

	public List<E> constructQuantifierArguments(Set<WhileyFile.Decl.Variable> uses) {
		ArrayList<E> arguments = new ArrayList<>();
		for (WhileyFile.Decl.Variable use : uses) {
			LowLevel.Type type = visitType(use.getType());
			arguments.add(visitor.visitVariableAccess(type, use.getName().get()));
		}
		return arguments;
	}

	public LowLevel.Type.Method constructQuantifierType(List<wycc.util.Pair<LowLevel.Type, String>> parameters) {
		List<LowLevel.Type> result = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			result.add(parameters.get(i).first());
		}
		return visitor.visitTypeMethod(result, visitor.visitTypeBool());
	}

	/**
	 * Apply a coercion from a given actual type to a required target type. This can
	 * involve physical data coercions as necessary, as well as tagging and
	 * untagging operations. For example:
	 *
	 * <pre>
	 * int|null x = 1
	 * </pre>
	 *
	 * Here, the target type of the right-hand side would be <code>int|null</code>
	 * and the actual type would be <code>int</code>. Hence, we need to perform a
	 * tagging operation. As another example:
	 *
	 * <pre>
	 * int:16 x = 1
	 * int:32 y = x
	 * </pre>
	 *
	 * For the second assignment, the target type of the right-hand side is
	 * <code>int:32</code> and its actual type is <code>int:16</code>. Hence, we
	 * need to apply an integer coercion (e.g. sign extension). As a final example
	 * to illustrate coercions of compound structures:
	 *
	 * <pre>
	 * int:16[] xs = [1,2,3]
	 * int:32[] ys = xs
	 * </pre>
	 *
	 * For the second assignment, we have to coerce the entire <code>xs</code> array
	 * into the appropriate format. This ammounts to a complete clone of the array,
	 * although this depends exactly on the target architecture. For example, on
	 * some architectures, <code>int:16</code> and <code>int:32</code> may have the
	 * same underlying representation and, hence, no coercion would be necessary.
	 *
	 * @param target
	 *            The required target type for the given expression. This is the
	 *            type that the value returned by the expression must meet.
	 * @param actual
	 *            The actual type returned by the given expression. This is the type
	 *            which we have, though it may not match the required target type.
	 *            If they don't match, then some kind of coercion is necessary.
	 * @param expr
	 * @return
	 */
	public E applyCoercion(Type target, Type actual, E expr, Environment environment) {
		if (target.equals(actual) || actual instanceof Type.Void) {
			// no coercion required in this case
			return expr;
		} else if (target instanceof Type.Int && actual instanceof Type.Int) {
			return applyIntCoercion((Type.Int) target, (Type.Int) actual, expr);
		} else if (target instanceof Type.Array && actual instanceof Type.Array) {
			return applyArrayCoercion((Type.Array) target, (Type.Array) actual, expr, environment);
		} else if (target instanceof Type.Record && actual instanceof Type.Record) {
			return applyRecordCoercion((Type.Record) target, (Type.Record) actual, expr);
		} else if (target instanceof Type.Reference && actual instanceof Type.Reference) {
			return applyReferenceCoercion((Type.Reference) target, (Type.Reference) actual, expr);
		} else if (target instanceof Type.Nominal) {
			return applyNominalCoercion((Type.Nominal) target, actual, expr, environment);
		} else if (actual instanceof Type.Nominal) {
			return applyNominalCoercion(target, (Type.Nominal) actual, expr, environment);
		} else if (target instanceof Type.Union && actual instanceof Type.Union) {
			return applyUnionCoercion((Type.Union) target, (Type.Union) actual, expr, environment);
		} else if (target instanceof Type.Union) {
			return applyUnionCoercion((Type.Union) target, actual, expr, environment);
		} else if (actual instanceof Type.Union) {
			return applyUnionCoercion(target, (Type.Union) actual, expr, environment);
		} else {
			throw new IllegalArgumentException("unknown coercion: " + actual + " => " + target);
		}
	}

	private static int coercionIndex = 0;

	public E applyIntCoercion(Type.Int _target, Type.Int _actual, E expr) {
		LowLevel.Type.Int target = visitInt(_target);
		LowLevel.Type.Int actual = visitInt(_actual);
		return visitor.visitIntegerCoercion(target, actual, expr);
	}

	public E applyArrayCoercion(Type.Array target, Type.Array actual, E expr, Environment environment) {
		String name = "coercion$" + coercionIndex++;
		D body = constructArrayArrayCoercion(name,target,actual,environment);
		return constructCoercionMethod(name,target,actual,expr,body);
	}

	public D constructArrayArrayCoercion(String name, Type.Array target, Type.Array actual, Environment environment) {
		LowLevel.Type.Int llIndexT = visitor.visitTypeInt(-1);
		LowLevel.Type.Array llTarget = visitArray(target);
		LowLevel.Type.Array llActual = visitArray(actual);
		ArrayList<S> body = new ArrayList<>();
		// Initialise empty array of same length as input
		E parameter = visitor.visitVariableAccess(llActual, "x");
		E length = visitor.visitArrayLength(llActual, parameter);
		E initialiser = visitor.visitArrayInitialiser(llTarget, length);
		// Declare output variable
		body.add(visitor.visitVariableDeclaration(llTarget, "r", initialiser));
		// Construct the coercion loop body
		ArrayList<S> loopBody = new ArrayList<>();
		E actualVar = visitor.visitVariableAccess(llActual, "x");
		E targetVar = visitor.visitVariableAccess(llTarget, "r");
		E indexVar = visitor.visitVariableAccess(llIndexT, "i");
		ArrayList<LowLevel.Type.Array> llTargets = new ArrayList<>();
		llTargets.add(llTarget);
		E lhs = visitor.visitArrayAccess(llTargets, targetVar, indexVar);
		ArrayList<LowLevel.Type.Array> llActuals = new ArrayList<>();
		llActuals.add(llActual);
		E rhs = applyCoercion(target.getElement(),actual.getElement(),visitor.visitArrayAccess(llActuals, actualVar, indexVar), environment);
		loopBody.add(visitor.visitAssign(lhs, rhs));
		// Construct the coercion loop itself
		S decl = visitor.visitVariableDeclaration(llIndexT, "i",
				visitor.visitIntegerInitialiser(llIndexT, BigInteger.ZERO));
		E condition = visitor.visitIntegerLessThan(llIndexT, indexVar, length);
		S increment = visitor.visitAssign(indexVar,
				visitor.visitIntegerAdd(llIndexT, indexVar, visitor.visitIntegerInitialiser(llIndexT, BigInteger.ONE)));
		body.add(visitor.visitFor(decl, condition, increment, loopBody));
		//
		body.add(visitor.visitReturn(targetVar));
		// Done
		ArrayList<Pair<LowLevel.Type, String>> parameters = new ArrayList<>();
		parameters.add(new Pair<>(llActual, "x"));
		return visitor.visitMethod(name, parameters, llTarget, body);
	}

	public E applyRecordCoercion(Type.Record target, Type.Record actual, E expr) {
		String name = "coercion$" + coercionIndex++;
		D body = constructRecordRecordCoercion(name,target,actual);
		return constructCoercionMethod(name,target,actual,expr,body);
	}

	public D constructRecordRecordCoercion(String name, Type.Record target, Type.Record actual) {
		LowLevel.Type.Record llTarget = visitRecord(target);
		LowLevel.Type.Record llActual = visitRecord(actual);
		E parameter = visitor.visitVariableAccess(llActual, "x");
		ArrayList<S> body = new ArrayList<>();
		//
		Tuple<Type.Field> fields = target.getFields();
		ArrayList<E> operands = new ArrayList<>();
		for (int i = 0; i != fields.size(); ++i) {
			Type.Field field = fields.get(i);
			LowLevel.Type targetType = visitType(field.getType());
			E initialiser = visitor.visitRecordAccess(llActual, parameter, field.getName().toString());
			body.add(visitor.visitVariableDeclaration(targetType, "f" + i, initialiser));
			operands.add(visitor.visitVariableAccess(targetType, "f" + i));
		}
		//
		E rval = visitor.visitRecordInitialiser(llTarget, operands);
		body.add(visitor.visitReturn(rval));
		//
		ArrayList<Pair<LowLevel.Type, String>> parameters = new ArrayList<>();
		parameters.add(new Pair<>(llActual, "x"));
		return visitor.visitMethod(name, parameters, llTarget, body);
	}

	public E applyReferenceCoercion(Type.Reference target, Type.Reference actual, E expr) {
		throw new RuntimeException("implement me!");
	}

	public E applyNominalCoercion(Type.Nominal target, Type actual, E expr, Environment environment) {
		try {
			WhileyFile.Decl.Type decl = resolver.resolveExactly(target.getName(), WhileyFile.Decl.Type.class);
			return applyCoercion(decl.getType(), actual, expr, environment);
		} catch (ResolutionError e) {
			throw new IllegalArgumentException("invalid nominal type (" + target + ")");
		}
	}

	public E applyNominalCoercion(Type target, Type.Nominal actual, E expr, Environment environment) {
		try {
			WhileyFile.Decl.Type decl = resolver.resolveExactly(actual.getName(), WhileyFile.Decl.Type.class);
			return applyCoercion(target, decl.getType(), expr, environment);
		} catch (ResolutionError e) {
			throw new IllegalArgumentException("invalid nominal type (" + target + ")");
		}
	}

	public E applyUnionCoercion(Type.Union target, Type.Union actual, E expr, Environment environment) {
		// Construct coercion method
		String name = "coercion$" + coercionIndex++;
		D body = constructUnionUnionCoercion(name,target,actual, environment);
		return constructCoercionMethod(name,target,actual,expr,body);
	}

	public D constructUnionUnionCoercion(String name, Type.Union target, Type.Union actual, Environment environment) {
		LowLevel.Type.Union paramType = visitUnion(actual);
		LowLevel.Type retType = visitType(target);
		E parameter = visitor.visitVariableAccess(paramType, "val");
		E tag = visitor.visitUnionAccess(paramType, parameter);
		ArrayList<S> stmts = new ArrayList<>();
		ArrayList<wycc.util.Pair<Integer, List<S>>> branches = new ArrayList();
		for(int i=0;i!=actual.size();++i) {
			Integer c = (i+1) == actual.size() ? null : i;
			List<S> branch = new ArrayList<>();
			E data = visitor.visitUnionLeave(paramType, i, parameter);
			E coercion = applyCoercion(target,actual.get(i),data, environment);
			branch.add(visitor.visitReturn(coercion));
			branches.add(new wycc.util.Pair<>(c,branch));
		}
		stmts.add(visitor.visitSwitch(tag,branches));
		List<wycc.util.Pair<LowLevel.Type, String>> parameters = new ArrayList<>();
		parameters.add(new wycc.util.Pair<>(paramType,"val"));
		return visitor.visitMethod(name, parameters, retType, stmts);
	}

	public E applyUnionCoercion(Type.Union target, Type actual, E expr, Environment environment) {
		int tag = determineTag(target,actual,environment);
		Type element = target.get(tag);
		expr = applyCoercion(element,actual,expr, environment);
		LowLevel.Type.Union llTarget = visitUnion(target);
		return visitor.visitUnionEnter(llTarget,tag,expr);
	}

	public E applyUnionCoercion(Type target, Type.Union actual, E expr, Environment environment) {
		int tag = determineTag(target,actual,environment);
		Type element = actual.get(tag);
		expr = applyCoercion(target,element,expr,environment);
		LowLevel.Type.Union llActual = visitUnion(actual);
		return visitor.visitUnionLeave(llActual, tag, expr);
	}

	public E constructCoercionMethod(String name, Type target, Type actual, E expr, D body) {
		// Construct coercion method
		auxillaries.add(body);
		// Construct invocation to coercion method
		List<E> arguments = new ArrayList<>();
		arguments.add(expr);
		List<LowLevel.Type> parameterTypes = new ArrayList<>();
		parameterTypes.add(visitType(actual));
		LowLevel.Type.Method type = visitor.visitTypeMethod(parameterTypes, visitType(target));
		return visitor.visitDirectInvocation(type, name, arguments);
	}

	// ==========================================================================
	// Type
	// ==========================================================================
	public List<LowLevel.Type> visitTypes(Tuple<Type> types) {
		ArrayList<LowLevel.Type> result = new ArrayList<>();
		for (int i = 0; i != types.size(); ++i) {
			result.add(visitType(types.get(i)));
		}
		return result;
	}

	public LowLevel.Type visitType(Type type) {
		switch (type.getOpcode()) {
		case TYPE_array:
			return visitArray((Type.Array) type);
		case TYPE_bool:
			return visitBool((Type.Bool) type);
		case TYPE_byte:
			return visitByte((Type.Byte) type);
		case TYPE_int:
			return visitInt((Type.Int) type);
		case TYPE_nominal:
			return visitNominal((Type.Nominal) type);
		case TYPE_null:
			return visitNull((Type.Null) type);
		case TYPE_record:
			return visitRecord((Type.Record) type);
		case TYPE_staticreference:
		case TYPE_reference:
			return visitReference((Type.Reference) type);
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
			return visitCallable((Type.Callable) type);
		case TYPE_union:
			return visitUnion((Type.Union) type);
		case TYPE_void:
			return visitVoid((Type.Void) type);
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public LowLevel.Type.Array visitArray(Type.Array type) {
		LowLevel.Type element = visitType(type.getElement());
		return visitor.visitTypeArray(element);
	}

	public LowLevel.Type.Void visitVoid(Type.Void type) {
		return visitor.visitTypeVoid();
	}

	public LowLevel.Type.Bool visitBool(Type.Bool type) {
		return visitor.visitTypeBool();
	}

	public LowLevel.Type.Int visitByte(Type.Byte type) {
		return visitor.visitTypeInt(8);
	}

	public LowLevel.Type.Int visitInt(Type.Int type) {
		// FIXME: currently all integer types are unbound.
		return visitor.visitTypeInt(-1);
	}

	public LowLevel.Type.Null visitNull(Type.Null type) {
		return visitor.visitTypeNull();
	}

	public LowLevel.Type.Record visitRecord(Type.Record type) {
		Tuple<Type.Field> fields = type.getFields();
		ArrayList<wycc.util.Pair<LowLevel.Type, String>> nFields = new ArrayList<>();
		for (int i = 0; i != fields.size(); ++i) {
			Type.Field field = fields.get(i);
			LowLevel.Type fieldType = visitType(field.getType());
			nFields.add(new wycc.util.Pair<>(fieldType, field.getName().toString()));
		}
		// FIXME: what to do about open records?
		return visitor.visitTypeRecord(nFields);
	}

	public LowLevel.Type.Reference visitReference(Type.Reference type) {
		LowLevel.Type element = visitType(type.getElement());
		return visitor.visitTypeReference(element);
	}

	public LowLevel.Type.Method visitCallable(Type.Callable type) {
		List<LowLevel.Type> parameters = visitTypes(type.getParameters());
		LowLevel.Type returns = visitType(getMultipleReturnType(type.getReturns()));
		return visitor.visitTypeMethod(parameters, returns);
	}

	public LowLevel.Type.Union visitUnion(Type.Union type) {
		ArrayList<LowLevel.Type> elements = new ArrayList<>();
		for (int i = 0; i != type.size(); ++i) {
			elements.add(visitType(type.get(i)));
		}
		return visitor.visitTypeUnion(elements);
	}

	public LowLevel.Type visitNominal(Type.Nominal type) {
		try {
			WhileyFile.Decl.Type decl = resolver.resolveExactly(type.getName(), WhileyFile.Decl.Type.class);
			if (decl.isRecursive()) {
				// FIXME: is this always the correct translation?
				return visitor.visitTypeRecursive(type.getName().toString());
			} else {
				return visitType(decl.getType());
			}
		} catch (NameResolver.ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	// ==========================================================================
	// Runtime Type Tests
	// ==========================================================================

	public E callRuntimeTypeTest(E argument, Type actual, Type test) {
		// Construct invocation
		LowLevel.Type llActual = visitType(actual);
		// Construct method type
		ArrayList<LowLevel.Type> parameters = new ArrayList<>();
		parameters.add(llActual);
		LowLevel.Type.Method type = visitor.visitTypeMethod(parameters, visitBool(Type.Bool));
		// Construct artgument list
		ArrayList<E> arguments = new ArrayList<>();
		arguments.add(argument);
		//
		Pair<Type,Type> rtt = new Pair<>(actual,test);
		int i;
		for(i=0;i!=runtimeTypeTests.size();++i) {
			if(runtimeTypeTests.get(i).equals(rtt)) {
				return visitor.visitDirectInvocation(type, "is$" + i, arguments);
			}
		}
		runtimeTypeTests.add(rtt);
		// Done
		return visitor.visitDirectInvocation(type, "is$" + i, arguments);
	}

	public void constructRuntimeTypeTests(Environment environment) {
		LowLevel.Type retType = visitor.visitTypeBool();
		for(int i=0;i!=runtimeTypeTests.size();++i) {
			// Construct body
			Pair<Type,Type> rtt = runtimeTypeTests.get(i);
			List<S> body = constructRuntimeTypeTest(rtt.first(),rtt.second(), environment);
			ArrayList<Pair<LowLevel.Type, String>> parameters = new ArrayList<>();
			parameters.add(new Pair<>(visitType(rtt.first()),"x"));
			// Construct auxillary method
			D method = visitor.visitMethod("is$" + i, parameters, retType, body);
			auxillaries.add(method);
		}
	}

	public List<S> constructRuntimeTypeTest(Type actual, Type test, Environment environment) {
		if(actual.equals(test)) {
			// FIXME: should prevent this
			return constructTrivialRuntimeTypeTest();
		} else if (actual.getOpcode() == test.getOpcode()) {
			switch (actual.getOpcode()) {
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				// FIXME: should prevent this
				return constructTrivialRuntimeTypeTest();
			case TYPE_union:
				return constructUnionUnionRuntimeTypeTest((Type.Union) actual, (Type.Union) test);
			case TYPE_nominal:
				return constructNominalNominalRuntimeTypeTest((Type.Nominal) actual, (Type.Nominal) test);
			default:
				throw new RuntimeException("need to work harder on type tests (" + actual + " is " + test + ")");
			}
		} else if (actual instanceof Type.Nominal) {
			return constructNominalTypeRuntimeTypeTest((Type.Nominal) actual, test);
		} else if (actual instanceof Type.Union) {
			return constructUnionTypeRuntimeTypeTest((Type.Union) actual, test, environment);
		} else if (test instanceof Type.Union) {
			return constructTypeUnionRuntimeTypeTest(actual, (Type.Union) test);
		} else if (test instanceof Type.Nominal) {
			return constructTypeNominalRuntimeTypeTest(actual, (Type.Nominal) test);
		} else {
			throw new IllegalArgumentException("need to implement runtime tests better");
		}
	}

	public List<S> constructTrivialRuntimeTypeTest() {
		ArrayList<S> stmts = new ArrayList<>();
		stmts.add(visitor.visitReturn(visitor.visitLogicalInitialiser(true)));
		return stmts;
	}

	public List<S> constructNominalNominalRuntimeTypeTest(Type.Nominal actual, Type.Nominal test) {
		// FIXME: could further improve this
		return constructNominalTypeRuntimeTypeTest(actual,test);
	}

	public List<S> constructUnionUnionRuntimeTypeTest(Type.Union actual, Type.Union test) {
		throw new RuntimeException("GOT HERE: " + actual + " is " + test);
	}

	public List<S> constructNominalTypeRuntimeTypeTest(Type.Nominal actual, Type test) {
		try {
			ArrayList<S> stmts = new ArrayList<>();
			LowLevel.Type type = visitType(actual);
			WhileyFile.Decl.Type decl = resolver.resolveExactly(actual.getName(), WhileyFile.Decl.Type.class);
			stmts.add(visitor.visitReturn(callRuntimeTypeTest(visitor.visitVariableAccess(type, "x"),decl.getType(), test)));
			return stmts;
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	public List<S> constructTypeNominalRuntimeTypeTest(Type actual, Type.Nominal test) {
		try {
			LowLevel.Type parameter = visitType(actual);
			ArrayList<S> stmts = new ArrayList<>();
			WhileyFile.Decl.Type decl = resolver.resolveExactly(test.getName(), WhileyFile.Decl.Type.class);
			E result = callRuntimeTypeTest(visitor.visitVariableAccess(parameter, "x"), actual, decl.getType());
			if (decl.getInvariant().size() > 0) {
				// Type invariants are present so invoke invariant method to check them
				List<LowLevel.Type> parameters = new ArrayList<>();
				parameters.add(parameter);
				LowLevel.Type.Method type = visitor.visitTypeMethod(parameters, visitBool(Type.Bool));
				String name = decl.getName().toString() + "$inv";
				List<E> arguments = new ArrayList<>();
				arguments.add(visitor.visitVariableAccess(parameter, "x"));
				result = visitor.visitLogicalAnd(result, visitor.visitDirectInvocation(type, name, arguments));
			}
			stmts.add(visitor.visitReturn(result));
			return stmts;
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Translate a runtime type test such as the following:
	 *
	 * <pre>
	 * function f(int|null x) -> bool:
	 * 	 return x is int
	 * </pre>
	 *
	 * This is expanded roughly speaking into the following low level code:
	 *
	 * <pre>
	 * bool f(int|null x) {
	 *   return is$0(x);
	 * }
	 *
	 * bool is$0(int|null x) {
	 *   return x.tag == 0
	 * }
	 * </pre>
	 *
	 *
	 *
	 * @param actual
	 * @param test
	 * @return
	 */
	public List<S> constructUnionTypeRuntimeTypeTest(Type.Union actual, Type test, Environment environment) {
		LowLevel.Type.Int tagT = visitInt(Type.Int);
		LowLevel.Type.Union llActualT = visitUnion(actual);
		int tag = determineTag(actual, test, environment);
		Type refined = actual.get(tag);
		E expr = visitor.visitVariableAccess(llActualT, "x");
		expr = visitor.visitUnionAccess(llActualT, expr);
		expr = visitor.visitIntegerEqual(tagT, expr, visitor.visitIntegerInitialiser(tagT, BigInteger.valueOf(tag)));
		//
		// FIXME: may need to call type test recursively on accessed value
		//
		ArrayList<S> stmts = new ArrayList<>();
		stmts.add(visitor.visitReturn(expr));
		return stmts;
	}

	/**
	 * Translate a runtime type test such as the following:
	 *
	 * <pre>
	 * type neg is (int p) where p < 0
	 * type pos is (int p) where p > 0
	 *
	 * function f(int x) -> bool:
	 *     return x is pos|neg
	 * </pre>
	 *
	 * This is expanded into roughly the following lowlevel code:
	 *
	 * <pre>
	 * bool neg$inv(int p) {
	 * 	return p < 0;
	 * }
	 *
	 * bool pos$inv(int p) {
	 * 	return p > 0;
	 * }
	 *
	 * bool f(int x) {
	 * 	return is$0(x)
	 * }
	 *
	 * bool is$0(int x) {
	 *   return neg$inv(x) || pos$inv(x);
	 * }
	 * </pre>
	 *
	 * The key is that the different cases in the test union are translated into
	 * logical disjunctions.
	 *
	 * @param expr
	 * @param actual
	 * @param test
	 * @return
	 */
	public List<S> constructTypeUnionRuntimeTypeTest(Type actual, Type.Union test) {
		LowLevel.Type llActual = visitType(actual);
		LowLevel.Type type = visitor.visitTypeBool();
		ArrayList<S> stmts = new ArrayList<>();
		//
		for (int tag = 0; tag != test.size(); ++tag) {
			E clause = callRuntimeTypeTest(visitor.visitVariableAccess(llActual, "x"), actual, test.get(tag));
			if (stmts.size() == 0) {
				stmts.add(visitor.visitVariableDeclaration(type, "r", clause));
			} else {
				E var = visitor.visitVariableAccess(type, "r");
				clause = visitor.visitLogicalOr(var, clause);
				stmts.add(visitor.visitAssign(var, clause));
			}
		}
		//
		stmts.add(visitor.visitReturn(visitor.visitVariableAccess(type, "r")));
		//
		return stmts;
	}

	// ==========================================================================
	// Helpers
	// ==========================================================================

	public int determineTag(Type.Union parent, Type child, Environment environment) {
		for (int i = 0; i != parent.size(); ++i) {
			if (isSubtype(parent.get(i), child, environment)) {
				return i;
			}
		}
		throw new IllegalArgumentException("cannot determine appropriate tag (" + parent + "<-" + child + ")");
	}

	public int determineTag(Type parent, Type.Union child, Environment environment) {
		for (int i = 0; i != child.size(); ++i) {
			if (isSubtype(parent, child.get(i), environment)) {
				return i;
			}
		}
		throw new IllegalArgumentException("cannot determine appropriate tag (" + parent + "<-" + child + ")");
	}

  public boolean isSubtype(Type parent, Type child, Environment environment) {
    try {
      // FIXME: need to handle lifetimes properly
      return subtypeOperator.isSubtype(parent, child, environment);
    } catch (ResolutionError e) {
      throw new RuntimeException("internal failure");
    }
  }

	/**
	 * Extract an integer constant from am integer constexpr or, if not, return
	 * null.
	 *
	 * @param e
	 * @return
	 */
	public Integer extractIntegerConstant(Expr e) {
		try {
			if (e instanceof Expr.Constant) {
				Expr.Constant c = (Expr.Constant) e;
				WhileyFile.Value v = c.getValue();
				if (v instanceof WhileyFile.Value.Int) {
					BigInteger bi = ((WhileyFile.Value.Int) v).get();
					return bi.intValueExact();
				}
			} else if (e instanceof Expr.IntegerNegation) {
				Expr.IntegerNegation ineg = (Expr.IntegerNegation) e;
				Integer r = extractIntegerConstant(ineg.getOperand());
				return r != null ? -r : null;
			}
		} catch (ArithmeticException exp) {
			// This can be thrown from the intValueExact method which indicates we cannot
			// extract an integer from this expression, hence we just return null.
			System.out.println("STAGE 4");
		}

		// No dice.
		return null;
	}

	/**
	 * Determine the set of used variables in a given expression. A used variable is
	 * simply one that is accessed from within the expression. Care needs to be
	 * taken for expressions which declare parameters in order to avoid capturing
	 * these.
	 *
	 * @param expr
	 * @return
	 */
	public Set<WhileyFile.Decl.Variable> determineUsedVariables(WhileyFile.Expr expr) {
		final HashSet<WhileyFile.Decl.Variable> used = new HashSet<>();
		// Create a translateor to extract all uses from the given expression.
		final AbstractVisitor translateor = new AbstractVisitor() {
			@Override
			public void visitVariableAccess(WhileyFile.Expr.VariableAccess expr) {
				used.add(expr.getVariableDeclaration());
			}

			@Override
			public void visitUniversalQuantifier(WhileyFile.Expr.UniversalQuantifier expr) {
				visitVariables(expr.getParameters());
				visitExpression(expr.getOperand());
				removeAllDeclared(expr.getParameters());
			}

			@Override
			public void visitExistentialQuantifier(WhileyFile.Expr.ExistentialQuantifier expr) {
				visitVariables(expr.getParameters());
				visitExpression(expr.getOperand());
				removeAllDeclared(expr.getParameters());
			}

			@Override
			public void visitType(WhileyFile.Type type) {
				// No need to visit types
			}

			private void removeAllDeclared(Tuple<Decl.Variable> parameters) {
				for (int i = 0; i != parameters.size(); ++i) {
					used.remove(parameters.get(i));
				}
			}
		};
		//
		translateor.visitExpression(expr);
		return used;
	}

	/**
	 * Determined the mangled name for a given function, method or property
	 * declaration. Mangling is required to handle overloading. Essentially, it
	 * encodes parameter type information into the name of the callable declaration
	 * to ensure overloaded declarations are distinct. For example, consider this:
	 *
	 * <pre>
	 * function id(int x) -> (int r):
	 *    return x
	 *
	 * function id(bool x) -> (bool r):
	 *    return x
	 * </pre>
	 *
	 * On a native platform we need to distinguish these two functions. For example,
	 * we might generate something like this (for JavaScript):
	 *
	 * <pre>
	 * function id_I(int x) -> (int r):
	 *    return x
	 *
	 * function id_B(bool x) -> (bool r):
	 *    return x
	 * </pre>
	 *
	 * Thus, we see that <code>_I</code> denotes a function which accepts an
	 * <code>int</code> parameter and <code>_B</code> one which accepts a boolean
	 * parameter. This uses the provided type mangler to construct the mangle.
	 * Observe that some declarations (e.g. native or export) are not mangled.
	 *
	 * @param decl
	 * @return
	 */
	public String getMangledName(Decl.Callable decl) {
		Tuple<Modifier> modifiers = decl.getModifiers();
		// First, check whether a mangle is actually required.
		if (modifiers.match(Modifier.Export.class) == null && modifiers.match(Modifier.Native.class) == null) {
			// Yes, mangle is required. Therefore, use mangler to generate it.
			return getMangledName(decl.getName().toString(), decl.getType());
		} else {
			// No mangle is required, therefore return name untouched.
			return decl.getName().toString();
		}
	}

	public String getMangledName(String name, Type.Callable type) {
		Tuple<Identifier> lifetimes;
		if (type instanceof Type.Method) {
			lifetimes = ((Type.Method) type).getLifetimeParameters();
		} else {
			lifetimes = new Tuple<>();
		}
		String mangle = mangler.getMangle(type.getParameters(), lifetimes);
		return name + mangle;
	}

	public String createTemporaryVariable(int i) {
		// FIXME: need to do better here!!!
		return "tmp$" + i;
	}

	/**
	 * Determine the appropriate return type for a given callable declaration. The
	 * key challenge here is that we assume target architectures do not support
	 * multiple return values (though, in principle, some might such as LLVM). In
	 * the case that multiple returns are employed, then a record is used to wrap
	 * them into a single return value. The following illustrates both scenarios:
	 *
	 * <pre>
	 * function id(int x) -> (int r):
	 *    return x
	 *
	 * function swap(int x, int y) -> (int a, int b):
	 *    return y,x
	 * </pre>
	 *
	 * These are effectively translated into the following equivalent forms:
	 *
	 * <pre>
	 * function id(int x) -> (int r):
	 *    return x
	 *
	 * function swap(int x, int y) -> {int a, int b}:
	 *    return {a:y, b:x}
	 * </pre>
	 *
	 * We see here that, when only one return is present, then this is used as is.
	 * However, when more than one is required, then they are just wrapped into a
	 * record.
	 *
	 * @param returns
	 * @return
	 */
	public Type getMultipleReturnType(Tuple<Type> returns) {
		if (returns.size() == 0) {
			// No single return value
			return Type.Void;
		} else if (returns.size() == 1) {
			// One return value, so use as is.
			return returns.get(0);
		} else {
			Type.Field[] fields = new Type.Field[returns.size()];
			for (int i = 0; i != fields.length; ++i) {
				Type type = returns.get(i);
				Identifier name = new Identifier("f" + i);
				fields[i] = new Type.Field(name, type);
			}
			return new Type.Record(false, new Tuple<>(fields));
		}
	}

	/**
	 * Given a general type which is know to represent a record type, extract the
	 * readable record type.
	 *
	 * @param type
	 * @param environment
	 * @return
	 */
	public Type.Record extractReadableRecordType(Type type, Environment environment) {
		SemanticType.Record r = rwTypeExtractor.apply(type, environment, ReadWriteTypeExtractor.READABLE_RECORD);
		return (Type.Record) concreteTypeExtractor.apply(r, environment);
	}

	/**
	 * Given a general type which is know to represent a reference type, extract the
	 * readable reference type.
	 *
	 * @param type
	 * @param environment
	 * @return
	 */
	public Type.Reference extractReadableReferenceType(Type type, Environment environment) {
		SemanticType.Reference r = rwTypeExtractor.apply(type, environment, ReadWriteTypeExtractor.READABLE_REFERENCE);
		return (Type.Reference) concreteTypeExtractor.apply(r, environment);
	}
	/**
	 * Given a general type which is know to represent a reference type, extract the
	 * writable reference type.
	 *
	 * @param type
	 * @param environment
	 * @return
	 */
	public Type.Reference extractWriteableReferenceType(Type type, Environment environment) {
		SemanticType.Reference r = rwTypeExtractor.apply(type, environment, ReadWriteTypeExtractor.WRITEABLE_REFERENCE);
		return (Type.Reference) concreteTypeExtractor.apply(r, environment);
	}
	/**
	 * Given a general type which is know to represent a function or method type,
	 * extract this type (which is only readable).
	 *
	 * @param type
	 * @param environment
	 * @return
	 */
	public Type.Callable extractCallableType(Type type, Environment environment) {
		return rwTypeExtractor.apply(type, environment, ReadWriteTypeExtractor.READABLE_CALLABLE);
	}

	/**
	 * Given a target type for the result of an expression and the actual type of an
	 * expression, extract the target integer type. For example, consider this
	 * simple situation:
	 *
	 * <pre>
	 * null|int:16 x = 1
	 * </pre>
	 *
	 * The right-hand side is the expression of interest. The target type for this
	 * is <code>null|int:16</code>, whilst its actual type is just <code>int</code>.
	 * Thus, the target integer type is <code>int:16</code>.
	 *
	 * @param target
	 *            The overall target type for the expression in question
	 * @param actual
	 *            The stated result type for the expression in question. This is
	 *            necessary, for example, to determine which component of a union is
	 *            the actual target.
	 * @return
	 */
	public Type.Int extractTargetIntegerType(Type target, Type actual, Environment environment) {
		try {
			if (target instanceof Type.Int) {
				return (Type.Int) target;
			} else if (target instanceof Type.Union) {
				Type.Union type = (Type.Union) target;
				for (int i = 0; i != type.size(); ++i) {
					Type element = type.get(i);
					if (subtypeOperator.isSubtype(element, actual, environment)) {
						return extractTargetIntegerType(element, actual, environment);
					}
				}
				throw new RuntimeException("deadcode reached");
			} else if (target instanceof Type.Nominal) {
				Type.Nominal type = (Type.Nominal) target;
				WhileyFile.Decl.Type decl = resolver.resolveExactly(type.getName(), WhileyFile.Decl.Type.class);
				return extractTargetIntegerType(decl.getType(), actual, environment);
			} else {
				throw new UnsupportedOperationException(
						"implement target integer extraction (" + target + "<=" + actual + ")");
			}
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Given a target type for the result of an expression and the actual type of an
	 * expression, extract the target array type. For example, consider this simple
	 * situation:
	 *
	 * <pre>
	 * int:16[]|null x = [1]
	 * </pre>
	 *
	 * The right-hand side is the expression of interest. The target type for this
	 * is <code>int:16[]|null</code>, whilst its actual type is just
	 * <code>int[]</code>. Thus, the target array type is <code>int:16[]</code>.
	 *
	 * @param target
	 *            The overall target type for the expression in question
	 * @param actual
	 *            The stated result type for the expression in question. This is
	 *            necessary, for example, to determine which component of a union is
	 *            the actual target.
	 * @return
	 */
	public Type.Array extractTargetArrayType(Type target, Type.Array actual, Environment environment) {
		try {
			if (target instanceof Type.Array) {
				return (Type.Array) target;
			} else if (target instanceof Type.Union) {
				Type.Union type = (Type.Union) target;
				for (int i = 0; i != type.size(); ++i) {
					Type element = type.get(i);
					if (subtypeOperator.isSubtype(element, actual, environment)) {
						return extractTargetArrayType(element, actual, environment);
					}
				}
				throw new RuntimeException("deadcode reached");
			} else if (target instanceof Type.Nominal) {
				Type.Nominal type = (Type.Nominal) target;
				WhileyFile.Decl.Type decl = resolver.resolveExactly(type.getName(), WhileyFile.Decl.Type.class);
				return extractTargetArrayType(decl.getType(), actual, environment);
			} else {
				throw new UnsupportedOperationException("implement target array extraction (" + target + ")");
			}
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Given a target type for the result of an expression and the actual type of an
	 * expression, extract the target type for that expression. For example,
	 * consider this simple situation:
	 *
	 * <pre>
	 * {int:16 f}|null x = {f:1}
	 * </pre>
	 *
	 * The right-hand side is the expression of interest. The target type for this
	 * is <code>{int:16 f}|null</code>, whilst its actual type is just
	 * <code>{int f}</code>. Thus, the target type of the expression is
	 * <code>{int:16 f}</code>. The following illustrates another similar example:
	 *
	 * <pre>
	 * int:16[]|null x = [1]
	 * </pre>
	 *
	 * The right-hand side is the expression of interest. The target type for this
	 * is <code>int:16[]|null</code>, whilst its actual type is just
	 * <code>int[]</code>. Thus, the target array type is <code>int:16[]</code>.
	 *
	 * @param target
	 *            The overall target type for the expression in question
	 * @param actual
	 *            The stated result type for the expression in question. This is
	 *            necessary, for example, to determine which component of a union is
	 *            the actual target.
	 * @return
	 */
	public <T extends Type> T extractTargetType(Type target, T actual) {
		try {
			if (target.getClass() == actual.getClass()) {
				return (T) target;
			} else if (target instanceof Type.Union) {
				Type.Union type = (Type.Union) target;
				for (int i = 0; i != type.size(); ++i) {
					Type element = type.get(i);
					if (subtypeOperator.isSubtype(element, actual, null)) {
						return extractTargetType(element, actual);
					}
				}
				throw new RuntimeException("deadcode reached");
			} else if (target instanceof Type.Nominal) {
				Type.Nominal type = (Type.Nominal) target;
				WhileyFile.Decl.Type decl = resolver.resolveExactly(type.getName(), WhileyFile.Decl.Type.class);
				return extractTargetType(decl.getType(), actual);
			} else {
				throw new UnsupportedOperationException("implement target array extraction (" + target + ")");
			}
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Given a type which is either an array or a union of arrays, extract the array
	 * type.
	 *
	 * @param type
	 * @return
	 */
	public List<LowLevel.Type.Array> extractArrayTypes(LowLevel.Type type) {
		ArrayList<LowLevel.Type.Array> types = new ArrayList<>();
		if (type instanceof LowLevel.Type.Array) {
			types.add((LowLevel.Type.Array) type);
		} else if (type instanceof LowLevel.Type.Union) {
			LowLevel.Type.Union ut = (LowLevel.Type.Union) type;
			for (int i = 0; i != ut.size(); ++i) {
				types.addAll(extractArrayTypes(ut.getElement(i)));
			}
		} else {
			throw new UnsupportedOperationException("Array type required");
		}
		return types;
	}

	/**
	 * Determine the maximum-width integer type for a bunch of integer types. For
	 * example, the max of <code>int:16</code> and <code>int:32</code> would be
	 * <code>int:32</code>. This is necessary to ensure that arithmetic operation on
	 * integers do not overflow.
	 *
	 * @param types
	 * @return
	 */
	public Type.Int max(Type.Int... types) {
		// FIXME: this needs to be updated when we have fixed-width integers.
		// NOTE: this could potentially just call extractIntegerType on a union of the
		// above.
		return Type.Int;
	}

	public static class Environment implements LifetimeRelation {
		private final Map<String, String[]> withins;

		public Environment() {
			this.withins = new HashMap<>();
		}

		public Environment(Map<String, String[]> withins) {
			this.withins = new HashMap<>(withins);
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
			Environment nenv = new Environment(withins);
			nenv.withins.put(inner, outers);
			return nenv;
		}
	}
}
