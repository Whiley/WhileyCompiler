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
package wyll.task;

import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.Stmt;
import wyc.lang.WhileyFile.Type;
import wyc.util.AbstractVisitor;
import wyc.util.WhileyFileResolver;
import wycc.util.ArrayUtils;
import wycc.util.Logger;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyfs.lang.Path.Root;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.subtyping.EmptinessTest;
import wyil.type.subtyping.StrictTypeEmptinessTest;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.util.AbstractTypeFilter;
import wyil.type.util.ConcreteTypeExtractor;
import wyil.type.util.ReadWriteTypeExtractor;
import wyll.core.LowLevel;
import wyll.core.WyllFile;

import static wyc.lang.WhileyFile.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wybs.lang.Build;
import wybs.lang.Build.Graph;
import wybs.lang.Build.Project;
import wybs.lang.CompilationUnit;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;

/**
 *
 * @author David J. Pearce
 *
 */
public class LowLevelCompileTask implements Build.Task {
	/**
	 * The master project for identifying all resources available to the
	 * builder. This includes all modules declared in the project being verified
	 * and/or defined in external resources (e.g. jar files).
	 */
	protected final Build.Project project;

	/**
	 * The type system is useful for managing nominal types and converting them
	 * into their underlying types.
	 */
	protected final NameResolver resolver;
	protected final SubtypeOperator subtypeOperator;
	protected final ReadWriteTypeExtractor rwTypeExtractor;
	protected final ConcreteTypeExtractor concreteTypeExtractor;
	/**
	 * Enable debug mode
	 */
	protected boolean debug = true;

	/**
	 * For logging information.
	 */
	private Logger logger = Logger.NULL;

	public LowLevelCompileTask(Build.Project project) {
		this.project = project;
		this.resolver = new WhileyFileResolver(project);
		EmptinessTest<SemanticType> strictEmptiness = new StrictTypeEmptinessTest(resolver);
		this.subtypeOperator = new SubtypeOperator(resolver, strictEmptiness);
		this.rwTypeExtractor = new ReadWriteTypeExtractor(resolver, subtypeOperator);
		this.concreteTypeExtractor = new ConcreteTypeExtractor(resolver,strictEmptiness);
	}

	@Override
	public Project project() {
		return project;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public Set<Entry<?>> build(Collection<Pair<Entry<?>, Root>> delta, Graph graph) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();

		// ========================================================================
		// Translate files
		// ========================================================================
		HashSet<Path.Entry<?>> generatedFiles = new HashSet<>();

		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Root dst = p.second();
			Path.Entry<WhileyFile> source = (Path.Entry<WhileyFile>) p.first();
			Path.Entry<WyllFile> target = dst.create(source.id(), WyllFile.ContentType);
			graph.registerDerivation(source, target);
			generatedFiles.add(target);
			// Construct the file
			WyllFile contents = build(source, target);
			// Write class file into its destination
			target.write(contents);
		}

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => Wyll: compiled " + delta.size() + " file(s)", endTime - start,
				memory - runtime.freeMemory());

		return generatedFiles;
	}

	private WyllFile build(Path.Entry<WhileyFile> source, Path.Entry<WyllFile> target) throws IOException {
		WyllFile wl = new WyllFile(target);
		WyllFile.Decl.Module module = visitWhileyFile(source.read(),wl);
		wl.allocate(module);
		return wl;
	}

	public WyllFile.Decl.Module visitWhileyFile(WhileyFile wf, WyllFile wlf) {
		Context context = new Context();
		for (Decl decl : wf.getDeclarations()) {
			visitDeclaration(decl, context);
		}
		// Construct any runtime type tests
		constructRuntimeTypeTests(context);
		// Construct any type coercions
		// Done
		List<WyllFile.Decl> declarations =  context.getDeclarations();
		// FIXME: following ilne is a hack
		Name name = wf.getSyntacticItems(Decl.Module.class).get(0).getName();
		return new WyllFile.Decl.Module(name,new Tuple<>(declarations));
	}

	public void visitDeclaration(Decl decl, Context context) {
		switch (decl.getOpcode()) {
		case DECL_importfrom:
		case DECL_import:
			visitImport((Decl.Import) decl, context);
			break;
		case DECL_staticvar:
			visitStaticVariable((Decl.StaticVariable) decl, context);
			break;
		case DECL_type:
			visitType((Decl.Type) decl, context);
			break;
		case DECL_function:
		case DECL_method:
		case DECL_property:
			visitCallable((Decl.Callable) decl, context);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void visitImport(Decl.Import decl, Context context) {
		// An interesting question as to what to do here, since some backends do support
		// includes.
	}

	public void visitStaticVariable(Decl.StaticVariable decl, Context context) {
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		WyllFile.Type type = translateType(decl.getType());
		WyllFile.Expr initialiser = null;
		if (decl.hasInitialiser()) {
			Environment environment = new Environment(context);
			initialiser = visitExpression(decl.getInitialiser(), decl.getType(), environment);
		}
		context.declare(new WyllFile.Decl.StaticVariable(modifiers, decl.getName(), type, initialiser));
	}

	public void visitType(Decl.Type decl, Context context) {
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		Decl.Variable var = decl.getVariableDeclaration();
		WyllFile.Type type = translateType(var.getType());
//		Environment environment = new Environment(context);
//		Tuple<WyllFile.Expr> invariant = visitExpressions(decl.getInvariant(), Type.Bool, environment);
//		return reducer.visitType(decl.getName(), decl.getType(), var.getName(), invariant);
		context.declare(new WyllFile.Decl.Type(modifiers, decl.getName(), type, decl.isRecursive()));
	}

	public void visitCallable(Decl.Callable decl, Context context) {
		switch (decl.getOpcode()) {
		case DECL_function:
		case DECL_method:
			visitFunctionOrMethod((Decl.FunctionOrMethod) decl, context);
			break;
		case DECL_property:
			visitProperty((Decl.Property) decl, context);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void visitFunctionOrMethod(Decl.FunctionOrMethod decl, Context context) {
		switch (decl.getOpcode()) {
		case DECL_function:
			visitFunction((Decl.Function) decl, context);
			break;
		case DECL_method:
			visitMethod((Decl.Method) decl, context);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void visitProperty(Decl.Property decl, Context context) {
//		Environment environment = new Environment();
//		Tuple<Identifier> parameters = decl.getParameters().map(vd -> vd.getName());
//		Tuple<Identifier> returns = decl.getReturns().map(vd -> vd.getName());
//		Tuple<WyllFile.Expr> body = visitExpressions(decl.getInvariant(), Type.Bool, environment);
//		return reducer.visitProperty(decl.getName(), decl.getType(), parameters, returns, body);
		throw new RuntimeException("implement properties");
	}

	public void visitFunction(Decl.Function decl, Context context) {
		Environment environment = new Environment(context);
		//
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		Tuple<WyllFile.Decl.Variable> parameters = translateParameters(decl.getParameters(), context);
		WyllFile.Type ret = translateType(getMultipleReturnType(decl.getType().getReturns()));
		Tuple<WyllFile.Expr> requires = visitExpressions(decl.getRequires(), Type.Bool, environment);
		Tuple<WyllFile.Expr> ensures = visitExpressions(decl.getEnsures(), Type.Bool, environment);
		WyllFile.Stmt.Block body = visitBlock(decl.getBody(), environment, new FunctionOrMethodScope(decl));
		//
		context.declare(new WyllFile.Decl.Method(modifiers, decl.getName(), parameters, ret, body));
	}

	public void visitMethod(Decl.Method decl, Context context) {
		// Construct environment relation
		Environment environment = new Environment(context);
		environment = environment.declareWithin("this", decl.getLifetimes());
		//
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		Tuple<WyllFile.Decl.Variable> parameters = translateParameters(decl.getParameters(), context);
		WyllFile.Type ret = translateType(getMultipleReturnType(decl.getType().getReturns()));
		Tuple<WyllFile.Expr> requires = visitExpressions(decl.getRequires(), Type.Bool, environment);
		Tuple<WyllFile.Expr> ensures = visitExpressions(decl.getEnsures(), Type.Bool, environment);
		WyllFile.Stmt.Block body = visitBlock(decl.getBody(), environment, new FunctionOrMethodScope(decl));
		//
		context.declare(new WyllFile.Decl.Method(modifiers, decl.getName(), parameters, ret, body));
	}


	public Tuple<WyllFile.Decl.Variable> translateParameters(Tuple<WhileyFile.Decl.Variable> vars, Context context) {
		WyllFile.Decl.Variable[] returns = new WyllFile.Decl.Variable[vars.size()];
		for (int i = 0; i != vars.size(); ++i) {
			Decl.Variable var = vars.get(i);
			Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
			WyllFile.Type type = translateType(var.getType());
			returns[i] = new WyllFile.Decl.Variable(modifiers, var.getName(), type);
		}
		return new WyllFile.Tuple<>(returns);
	}

	// ==========================================================================
	// Statements
	// ==========================================================================

	public WyllFile.Stmt visitStatement(Stmt stmt, Environment environment, EnclosingScope scope) {
		switch (stmt.getOpcode()) {
		case DECL_variable:
		case DECL_variableinitialiser:
			return visitVariable((Decl.Variable) stmt, environment);
		case STMT_assert:
			return visitAssert((Stmt.Assert) stmt, environment, scope);
		case STMT_assign:
			return visitAssign((Stmt.Assign) stmt, environment, scope);
		case STMT_assume:
			return visitAssume((Stmt.Assume) stmt, environment, scope);
		case STMT_break:
			return visitBreak((Stmt.Break) stmt, environment, scope);
		case STMT_continue:
			return visitContinue((Stmt.Continue) stmt, environment, scope);
		case STMT_debug:
			return visitDebug((Stmt.Debug) stmt, environment, scope);
		case STMT_dowhile:
			return visitDoWhile((Stmt.DoWhile) stmt, environment, scope);
		case STMT_fail:
			return visitFail((Stmt.Fail) stmt, environment, scope);
		case STMT_if:
		case STMT_ifelse:
			return visitIfElse((Stmt.IfElse) stmt, environment, scope);
		case EXPR_invoke:
			return visitInvoke((Expr.Invoke) stmt, new Tuple<>(), environment);
		case EXPR_indirectinvoke:
			return visitIndirectInvoke((Expr.IndirectInvoke) stmt, new Tuple<>(), environment);
		case STMT_namedblock:
			return visitNamedBlock((Stmt.NamedBlock) stmt, environment, scope);
		case STMT_return:
			return visitReturn((Stmt.Return) stmt, environment, scope);
		case STMT_skip:
			return visitSkip((Stmt.Skip) stmt, environment, scope);
		case STMT_switch:
			return visitSwitch((Stmt.Switch) stmt, environment, scope);
		case STMT_while:
			return visitWhile((Stmt.While) stmt, environment, scope);
		default:
			throw new IllegalArgumentException("unknown statement encountered (" + stmt.getClass().getName() + ")");
		}
	}

	public WyllFile.Stmt visitAssert(Stmt.Assert stmt, Environment environment, EnclosingScope scope) {
		WyllFile.Expr condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		return new WyllFile.Stmt.Assert(condition);
	}

	public WyllFile.Stmt visitAssign(Stmt.Assign stmt, Environment environment, EnclosingScope scope) {
		if(hasMultipleExpression(stmt) || hasInterference(stmt)) {
			return translateComplexAssign(stmt, environment, scope);
		} else {
			return translateSimpleAssign(stmt, environment);
		}
	}

	/**
	 * Check whether a given assignment has a multiple expression on the right-hand
	 * side. This amounts to checking whether or not the number of expressions on
	 * the right-hand size matches the number of lvals on the left-hand side.
	 *
	 * @param stmt
	 * @return
	 */
	public boolean hasMultipleExpression(Stmt.Assign stmt) {
		return stmt.getLeftHandSide().size() != stmt.getRightHandSide().size();
	}

	/**
	 * Check whether or not a variable modified by this assignment will interfere
	 * with an expression on the right-hand side of the assignment.
	 *
	 * @param stmt
	 * @return
	 */
	public boolean hasInterference(Stmt.Assign stmt) {
		Tuple<LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		for(int i=0;i!=lhs.size();++i) {
			Decl.Variable lval = extractVariable(lhs.get(i));
			if(lval != null) {
				for(int j=0;j!=rhs.size();++j) {
					// FIXME: this loop could be optimised as there are situations with interference
					// between lhs and rhs which are not actual instances of interference.
					if(hasInterference(lval,rhs.get(j))) {
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
		switch(lval.getOpcode()) {
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


	/**
	 * Translate a simple statement. This can be a multiple assignment, but it
	 * cannot have a multiple expression on the right-hand side *or* any
	 * interference.
	 */
	public WyllFile.Stmt translateSimpleAssign(Stmt.Assign stmt, Environment environment) {
		Tuple<LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		// Temporary variable declarations needed to handle multiple return values.
		WyllFile.Stmt[] stmts = new WyllFile.Stmt[rhs.size()];
		//
		for (int i = 0; i != rhs.size(); ++i) {
			Expr rv = rhs.get(i);
			Expr lv = lhs.get(i);
			WyllFile.LVal lval = (WyllFile.LVal) visitExpression(lv, lv.getType(), environment);
			WyllFile.Expr rval = visitExpression(rv, lv.getType(), environment);
			stmts[i] = new WyllFile.Stmt.Assign(lval, rval);
		}
		if (stmts.length == 1) {
			return stmts[0];
		} else {
			return new WyllFile.Stmt.Block(stmts);
		}
	}

	/**
	 * Translate a complex assignment. This is multiple assignment which either
	 * contains a multiple expression on the right-hand side or has some form of
	 * assignment interference. For example:
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
	 * This illustrates a multiple expression on the right hand side of the
	 * assignment. The following illustrates interference:
	 *
	 * <pre>
	 * x,y = y,x
	 * </pre>
	 *
	 * The problem here is that the assignment to <code>x</code> on the left-hand
	 * side interfers with its read on the right-hand side (i.e. read-after-write
	 * interference).
	 *
	 * @param stmt
	 * @param context
	 * @return
	 */
	public WyllFile.Stmt translateComplexAssign(Stmt.Assign stmt, Environment environment, EnclosingScope scope) {
		Tuple<LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		ArrayList<WyllFile.Stmt> stmts = new ArrayList<>();
		WyllFile.Decl.Variable[] tmps = new WyllFile.Decl.Variable[rhs.size()];
		//
		for (int i = 0; i != rhs.size(); ++i) {
			WyllFile.Decl.Variable decl;
			Expr rv = rhs.get(i);
			Tuple<Type> rhsTypes = rv.getTypes();
			if (rhsTypes == null) {
				decl = createTemporaryVariable(rv.getType(), rv, environment, scope);
			} else {
				decl = createTemporaryVariable(rhsTypes, rv, environment, scope);
			}
			stmts.add(decl);
			tmps[i] = decl;
		}
		//
		for (int i = 0, j = 0; i != rhs.size(); ++i) {
			Expr rv = rhs.get(i);
			Tuple<Type> rhsTypes = rv.getTypes();
			WyllFile.Decl.Variable decl = tmps[i];
			if (rhsTypes == null) {
				// Easy case for single assignments
				Expr lv = lhs.get(j++);
				WyllFile.LVal lval = (WyllFile.LVal) visitExpression(lv, lv.getType(), environment);
				WyllFile.Expr rval = new WyllFile.Expr.VariableAccess(decl.getType(), decl.getName());
				stmts.add(new WyllFile.Stmt.Assign(lval, rval));
			} else {
				// Harder case for multiple assignments. First, store return value into
				// temporary register. Then load from that. At this time, the only way to have a
				// multiple return is via some kind of invocation.
				WyllFile.Type.Record rec_t = (WyllFile.Type.Record) decl.getType();
				for (int k = 0; k != rhsTypes.size(); ++k) {
					Expr lv = lhs.get(j++);
					WyllFile.LVal lval = (WyllFile.LVal) visitExpression(lv, lv.getType(), environment);
					WyllFile.Expr.VariableAccess tmp = new WyllFile.Expr.VariableAccess(decl.getType(), decl.getName());
					WyllFile.Expr rval = new WyllFile.Expr.RecordAccess(rec_t, tmp, new Identifier("f" + k));
					stmts.add(new WyllFile.Stmt.Assign(lval, rval));
				}
			}
		}
		WyllFile.Stmt[] array = stmts.toArray(new WyllFile.Stmt[stmts.size()]);
		return new WyllFile.Stmt.Block(array);
	}

	public WyllFile.Stmt visitAssume(Stmt.Assume stmt, Environment environment, EnclosingScope scope) {
		WyllFile.Expr operand = visitExpression(stmt.getCondition(), Type.Bool, environment);
		return new WyllFile.Stmt.Assert(operand);
	}

	public WyllFile.Stmt.Block visitBlock(Stmt.Block stmt, Environment environment, EnclosingScope scope) {
		ArrayList<WyllFile.Stmt> block = new ArrayList<>();
		for (int i = 0; i != stmt.size(); ++i) {
			block.add(visitStatement(stmt.get(i), environment, scope));
		}
		return new WyllFile.Stmt.Block(block.toArray(new WyllFile.Stmt[block.size()]));
	}

	public WyllFile.Stmt visitBreak(Stmt.Break stmt, Environment environment, EnclosingScope scope) {
		return new WyllFile.Stmt.Break();
	}

	public WyllFile.Stmt visitContinue(Stmt.Continue stmt, Environment environment, EnclosingScope scope) {
		return new WyllFile.Stmt.Continue();
	}

	public WyllFile.Stmt visitDebug(Stmt.Debug stmt, Environment environment, EnclosingScope scope) {
		// FIXME: Should be Type.Int(0,255)
		Type std_ascii = new Type.Array(Type.Int);
		WyllFile.Expr operand = visitExpression(stmt.getOperand(), std_ascii, environment);
		// FIXME: should implement this somehow!!
		return null;
	}

	public WyllFile.Stmt visitDoWhile(Stmt.DoWhile stmt, Environment environment, EnclosingScope scope) {
		WyllFile.Stmt.Block body = visitBlock(stmt.getBody(), environment, scope);
		WyllFile.Expr condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		Tuple<WyllFile.Expr> invariants = visitExpressions(stmt.getInvariant(), Type.Bool, environment);
		// FIXME: in debug mode should insert invariant checks
		return new WyllFile.Stmt.DoWhile(condition, body);
	}

	public WyllFile.Stmt visitFail(Stmt.Fail stmt, Environment environment, EnclosingScope scope) {
		// FIXME: should add source + line number information here
		return new WyllFile.Stmt.Fail();
	}

	public WyllFile.Stmt visitIfElse(Stmt.IfElse stmt, Environment environment, EnclosingScope scope) {
		// FIXME: need to handle arbitrary chains here
		WyllFile.Expr condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		WyllFile.Stmt.Block trueBranch = visitBlock(stmt.getTrueBranch(), environment, scope);
		WyllFile.Pair<WyllFile.Expr, WyllFile.Stmt.Block>[] branches = new WyllFile.Pair[] {
				new WyllFile.Pair<>(condition, trueBranch) };
		if (stmt.hasFalseBranch()) {
			WyllFile.Stmt.Block falseBranch = visitBlock(stmt.getFalseBranch(), environment, scope);
			return new WyllFile.Stmt.IfElse(new Tuple<>(branches), falseBranch);
		} else {
			return new WyllFile.Stmt.IfElse(new Tuple<>(branches));
		}
	}

	public WyllFile.Stmt visitNamedBlock(Stmt.NamedBlock stmt, Environment environment, EnclosingScope scope) {
		// Updated the environment with new within relations
		LifetimeDeclaration enclosing = scope.getEnclosingScope(LifetimeDeclaration.class);
		String[] lifetimes = enclosing.getDeclaredLifetimes();
		environment = environment.declareWithin(stmt.getName().get(), lifetimes);
		// Create an appropriate scope for this block
		scope = new NamedBlockScope(scope, stmt);
		//
		return visitBlock(stmt.getBlock(), environment, scope);
	}

	public WyllFile.Stmt visitReturn(Stmt.Return stmt, Environment environment, EnclosingScope scope) {
		// FIXME: this will be broken in the case of a method call or other
		// side-effecting operation. The reason being that we may end up duplicating the
		// lhs. When the time comes, we can fix this by introducing an assignment
		// expression. This turns out to be a *very* convenient solution since all
		// target platforms support this. This would also help with the similar problem
		// of multiple returns.
		Decl.FunctionOrMethod enclosing = stmt.getAncestor(Decl.FunctionOrMethod.class);
		Tuple<Type> targets = enclosing.getType().getReturns();
		Tuple<WyllFile.Expr> returns = visitExpressions(stmt.getReturns(), targets, environment);
		//
		WyllFile.Expr ret;
		if (returns.size() == 0) {
			return new WyllFile.Stmt.Return();
		} else if (returns.size() == 1) {
			ret = returns.get(0);
		} else if (returns.size() == targets.size()) {
			WyllFile.Type.Record type = (WyllFile.Type.Record) translateType(getMultipleReturnType(targets));
			Identifier[] fields = new Identifier[targets.size()];
			for (int i = 0; i != fields.length; ++i) {
				fields[i] = new Identifier("f" + i);
			}
			ret = new WyllFile.Expr.RecordInitialiser(type, new Tuple<>(fields), returns);
		} else {
			// Hardest case
			throw new RuntimeException("Need to handle complex multiple return case");
		}
		return new WyllFile.Stmt.Return(ret);
	}

	public WyllFile.Stmt visitSkip(Stmt.Skip stmt, Environment environment, EnclosingScope scope) {
		return null;
	}

	public WyllFile.Stmt visitSwitch(Stmt.Switch stmt, Environment environment, EnclosingScope scope) {
		// Attempt to translate into a low-level switch statement. This may fail if it
		// contains something which cannot be evaluated at compile time.
		Type type = stmt.getCondition().getType();
		WyllFile.Expr condition = visitExpression(stmt.getCondition(), null, environment);
		Tuple<Stmt.Case> cases = stmt.getCases();
		WyllFile.Stmt.Case[] ncases = new WyllFile.Stmt.Case[cases.size()];
		for (int i = 0; i != cases.size(); ++i) {
			ncases[i] = translateCase(cases.get(i), type, environment, scope);
			if(ncases[i] == null) {
				// This indicates we were unable to translate this high-level switch into a
				// low-level switch. Therefore, instead, we will translate it as a chain of
				// if-else statements.
				return translateSwitchAsChain(stmt, environment, scope);
			}
		}
		return new WyllFile.Stmt.Switch(condition, new WyllFile.Tuple<>(ncases));
	}

	public WyllFile.Stmt.Case translateCase(Stmt.Case stmt, Type type, Environment environment, EnclosingScope scope) {
		Tuple<WyllFile.Expr> conditions = visitExpressions(stmt.getConditions(), type, environment);
		Value.Int[] ints = new Value.Int[conditions.size()];
		for (int i = 0; i != ints.length; ++i) {
			WyllFile.Expr c = conditions.get(i);
			if (c instanceof WyllFile.Expr.IntConstant) {
				ints[i] = ((WyllFile.Expr.IntConstant) c).getValue();
			} else {
				// FAILED
				return null;
			}
		}
		WyllFile.Stmt.Block block = visitBlock(stmt.getBlock(), environment, scope);
		return new WyllFile.Stmt.Case(new Tuple<>(ints), block);
	}

	public WyllFile.Stmt translateSwitchAsChain(Stmt.Switch stmt, Environment environment, EnclosingScope scope) {
		Type type = stmt.getCondition().getType();
		WyllFile.Expr condition = visitExpression(stmt.getCondition(), type, environment);
		Tuple<Stmt.Case> cases = stmt.getCases();
		WyllFile.Pair<WyllFile.Expr, WyllFile.Stmt.Block>[] branches = new WyllFile.Pair[cases.size()];
		WyllFile.Stmt.Block defaultBranch = null;
		int j = 0;
		for (int i = 0; i != cases.size(); ++i) {
			Stmt.Case cAse = cases.get(i);
			WyllFile.Stmt.Block block = visitBlock(cAse.getBlock(), environment, scope);
			Tuple<WyllFile.Expr> conditions = visitExpressions(cAse.getConditions(), type, environment);
			//
			if (conditions.size() == 0) {
				// Default case
				defaultBranch = block;
				branches = Arrays.copyOf(branches, branches.length - 1);
			} else if (conditions.size() != 1) {
				throw new IllegalArgumentException("implement me!");
			} else {
				branches[j++] = new WyllFile.Pair<>(new WyllFile.Expr.Equal(condition, conditions.get(0)), block);
			}
		}
		//
		if (defaultBranch != null) {
			return new WyllFile.Stmt.IfElse(new Tuple<>(branches), defaultBranch);
		} else {
			return new WyllFile.Stmt.IfElse(new Tuple<>(branches));
		}
	}

	public WyllFile.Stmt visitWhile(Stmt.While stmt, Environment environment, EnclosingScope scope) {
		WyllFile.Expr condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		// FIXME: runtime assertion checking
		Tuple<WyllFile.Expr> invariants = visitExpressions(stmt.getInvariant(), Type.Bool, environment);
		WyllFile.Stmt.Block body = visitBlock(stmt.getBody(), environment, scope);
		return new WyllFile.Stmt.While(condition, body);
	}

	public WyllFile.Stmt visitVariable(Decl.Variable decl, Environment environment) {
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		WyllFile.Type type = translateType(decl.getType());
		if (decl.hasInitialiser()) {
			WyllFile.Expr initialiser = visitExpression(decl.getInitialiser(), decl.getType(), environment);
			return new WyllFile.Decl.Variable(modifiers, decl.getName(), type, initialiser);
		} else {
			return new WyllFile.Decl.Variable(modifiers, decl.getName(), type);
		}
	}

	// =============================================================================================
	// Expressions
	// =============================================================================================
	public Tuple<WyllFile.Expr> visitExpressions(Tuple<Expr> exprs, Tuple<Type> targets, Environment environment) {
		ArrayList<WyllFile.Expr> operands = new ArrayList<>();
		int j=0;
		for (int i = 0; i != exprs.size(); ++i) {
			Expr e = exprs.get(i);
			// Handle multi expressions
			if(e.getTypes() != null) {
				int len = e.getTypes().size();
				Tuple<Type> types = targets.get(j,j+len);
				operands.add(visitMultiExpression(e,types,environment));
				j = j + len;
			} else {
				// Default to single expression
				operands.add(visitExpression(exprs.get(i), targets.get(j), environment));
				j = j + 1;
			}
		}
		WyllFile.Expr[] arr = operands.toArray(new WyllFile.Expr[operands.size()]);
		return new Tuple<>(arr);
	}

	public Tuple<WyllFile.Expr> visitExpressions(Tuple<Expr> exprs, Type target, Environment environment) {
		WyllFile.Expr[] nexprs = new WyllFile.Expr[exprs.size()];
		for (int i = 0; i != exprs.size(); ++i) {
			nexprs[i] = visitExpression(exprs.get(i), target, environment);
		}
		return new Tuple<>(nexprs);
	}

	public WyllFile.Expr visitMultiExpression(Expr expr, Tuple<Type> types, Environment environment) {
		WyllFile.Expr result;
		switch (expr.getOpcode()) {
		case EXPR_indirectinvoke:
			result = visitIndirectInvoke((Expr.IndirectInvoke) expr, types, environment);
			break;
		case EXPR_invoke:
			result = visitInvoke((Expr.Invoke) expr, null, environment);
			break;
		default:
			return internalFailure("unknown multi-expression", expr);
		}
		// FIXME: what to do about coercions?
		return result;
	}

	/**
	 * Visit a given expression which is being assigned to a location of a given
	 * type.
	 *
	 * @param expr
	 * @param target
	 */
	public WyllFile.Expr visitExpression(Expr expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Terminals
		case EXPR_constant:
			return visitConstant((Expr.Constant) expr, target, environment);
		case EXPR_indirectinvoke:
			return visitIndirectInvoke((Expr.IndirectInvoke) expr, new Tuple<>(target), environment);
		case EXPR_lambdaaccess: {
			Type.Callable lambdaT = selectLambda(target, expr, environment);
			return visitLambdaAccess((Expr.LambdaAccess) expr, lambdaT, environment);
		}
		case DECL_lambda:
			return visitLambda((Decl.Lambda) expr, environment);
		case EXPR_staticvariable:
			return visitStaticVariableAccess((Expr.StaticVariableAccess) expr, target, environment);
		case EXPR_variablecopy:
		case EXPR_variablemove:
			return visitVariableAccess((Expr.VariableAccess) expr, target, environment);
		// Unary Operators
		case EXPR_cast:
		case EXPR_integernegation:
		case EXPR_is:
		case EXPR_logicalnot:
		case EXPR_logicalexistential:
		case EXPR_logicaluniversal:
		case EXPR_bitwisenot:
		case EXPR_dereference:
		case EXPR_staticnew:
		case EXPR_new:
		case EXPR_recordaccess:
		case EXPR_recordborrow:
		case EXPR_arraylength:
			return visitUnaryOperator((Expr.UnaryOperator) expr, target, environment);
		// Binary Operators
		case EXPR_logiaclimplication:
		case EXPR_logicaliff:
		case EXPR_equal:
		case EXPR_notequal:
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
		case EXPR_arrayrange:
		case EXPR_recordupdate:
		case EXPR_arraygenerator:
			return visitBinaryOperator((Expr.BinaryOperator) expr, target, environment);
		// Nary Operators
		case EXPR_logicaland:
		case EXPR_logicalor:
		case EXPR_invoke:
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_arrayinitialiser:
		case EXPR_recordinitialiser:
			return visitNaryOperator((Expr.NaryOperator) expr, target, environment);
		// Ternary Operators
		case EXPR_arrayupdate:
			return visitTernaryOperator((Expr.TernaryOperator) expr, target, environment);
		default:
			return internalFailure("unknown expression encountered",expr);
		}
	}

	public WyllFile.Expr visitUnaryOperator(Expr.UnaryOperator expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Unary Operators
		case EXPR_cast:
			return visitCast((Expr.Cast) expr, target, environment);
		case EXPR_integernegation: {
			Type.Int intT = selectInt(target, expr, environment);
			return visitIntegerNegation((Expr.IntegerNegation) expr, intT, environment);
		}
		case EXPR_is:
			return visitIs((Expr.Is) expr, environment);
		case EXPR_logicalnot:
			return visitLogicalNot((Expr.LogicalNot) expr, environment);
		case EXPR_logicalexistential:
			return visitExistentialQuantifier((Expr.ExistentialQuantifier) expr, environment);
		case EXPR_logicaluniversal:
			return visitUniversalQuantifier((Expr.UniversalQuantifier) expr, environment);
		case EXPR_bitwisenot:
			return visitBitwiseComplement((Expr.BitwiseComplement) expr, environment);
		case EXPR_dereference:
			return visitDereference((Expr.Dereference) expr, target, environment);
		case EXPR_staticnew:
		case EXPR_new: {
			Type.Reference refT = selectReference(target, expr, environment);
			return visitNew((Expr.New) expr, refT, environment);
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			return visitRecordAccess((Expr.RecordAccess) expr, target, environment);
		case EXPR_arraylength: {
			Type.Int intT = selectInt(target, expr, environment);
			return visitArrayLength((Expr.ArrayLength) expr, intT, environment);
		}
		default:
			return internalFailure("unknown unary expression encountered",expr);
		}
	}

	public WyllFile.Expr visitBinaryOperator(Expr.BinaryOperator expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Binary Operators
		case EXPR_equal:
			return visitEqual((Expr.Equal) expr, environment);
		case EXPR_notequal:
			return visitNotEqual((Expr.NotEqual) expr, environment);
		case EXPR_logiaclimplication:
			return visitLogicalImplication((Expr.LogicalImplication) expr, environment);
		case EXPR_logicaliff:
			return visitLogicalIff((Expr.LogicalIff) expr, environment);
		case EXPR_integerlessthan:
			return visitIntegerLessThan((Expr.IntegerLessThan) expr, environment);
		case EXPR_integerlessequal:
			return visitIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr, environment);
		case EXPR_integergreaterthan:
			return visitIntegerGreaterThan((Expr.IntegerGreaterThan) expr, environment);
		case EXPR_integergreaterequal:
			return visitIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr, environment);
		case EXPR_integeraddition: {
			Type.Int intT = selectInt(target, expr, environment);
			return visitIntegerAddition((Expr.IntegerAddition) expr, intT, environment);
		}
		case EXPR_integersubtraction: {
			Type.Int intT = selectInt(target, expr, environment);
			return visitIntegerSubtraction((Expr.IntegerSubtraction) expr, intT, environment);
		}
		case EXPR_integermultiplication: {
			Type.Int intT = selectInt(target, expr, environment);
			return visitIntegerMultiplication((Expr.IntegerMultiplication) expr, intT, environment);
		}
		case EXPR_integerdivision: {
			Type.Int intT = selectInt(target, expr, environment);
			return visitIntegerDivision((Expr.IntegerDivision) expr, intT, environment);
		}
		case EXPR_integerremainder: {
			Type.Int intT = selectInt(target, expr, environment);
			return visitIntegerRemainder((Expr.IntegerRemainder) expr, intT, environment);
		}
		case EXPR_bitwiseshl:
			return visitBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr, environment);
		case EXPR_bitwiseshr:
			return visitBitwiseShiftRight((Expr.BitwiseShiftRight) expr, environment);
		case EXPR_arraygenerator: {
			Type.Array arrayT = selectArray(target, expr, environment);
			return visitArrayGenerator((Expr.ArrayGenerator) expr, arrayT, environment);
		}
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			return visitArrayAccess((Expr.ArrayAccess) expr, target, environment);
		case EXPR_recordupdate:
			Type.Record recordT = selectRecord(target, expr, environment);
			return visitRecordUpdate((Expr.RecordUpdate) expr, recordT, environment);
		default:
			return internalFailure("unknown binary expression encountered",expr);
		}
	}

	public WyllFile.Expr visitTernaryOperator(Expr.TernaryOperator expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Ternary Operators
		case EXPR_arrayupdate:
			Type.Array arrayT = selectArray(target, expr, environment);
			return visitArrayUpdate((Expr.ArrayUpdate) expr, arrayT, environment);
		default:
			return internalFailure("unknown ternary expression encountered",expr);
		}
	}

	public WyllFile.Expr visitNaryOperator(Expr.NaryOperator expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Nary Operators
		case EXPR_arrayinitialiser:
			Type.Array arrayT = selectArray(target, expr, environment);
			return visitArrayInitialiser((Expr.ArrayInitialiser) expr, arrayT, environment);
		case EXPR_bitwiseand:
			return visitBitwiseAnd((Expr.BitwiseAnd) expr, environment);
		case EXPR_bitwiseor:
			return visitBitwiseOr((Expr.BitwiseOr) expr, environment);
		case EXPR_bitwisexor:
			return visitBitwiseXor((Expr.BitwiseXor) expr, environment);
		case EXPR_invoke:
			return visitInvoke((Expr.Invoke) expr, new Tuple<>(target), environment);
		case EXPR_logicaland:
			return visitLogicalAnd((Expr.LogicalAnd) expr, environment);
		case EXPR_logicalor:
			return visitLogicalOr((Expr.LogicalOr) expr, environment);
		case EXPR_recordinitialiser:
			Type.Record recordT = selectRecord(target, expr, environment);
			return visitRecordInitialiser((Expr.RecordInitialiser) expr, recordT, environment);
		default:
			return internalFailure("unknown nary expression encountered",expr);
		}
	}

	public WyllFile.Expr visitArrayAccess(Expr.ArrayAccess expr, Type target, Environment environment) {
		Type.Array type = extractReadableArrayType(expr.getFirstOperand().getType(), environment);
		WyllFile.Type.Array llType = (WyllFile.Type.Array) translateType(type);
		WyllFile.Expr source = visitExpression(expr.getFirstOperand(), expr.getFirstOperand().getType(), environment);
		// FIXME: this should be a USIZE
		WyllFile.Expr index = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return new WyllFile.Expr.ArrayAccess(llType, source, index);
	}

	public WyllFile.Expr visitArrayLength(Expr.ArrayLength expr, Type.Int target, Environment environment) {
		Type.Array type = extractReadableArrayType(expr.getOperand().getType(), environment);
		WyllFile.Type.Array llType = (WyllFile.Type.Array) translateType(type);
		WyllFile.Expr operand = visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
		return new WyllFile.Expr.ArrayLength(llType, operand);
	}

	public WyllFile.Expr visitArrayGenerator(Expr.ArrayGenerator expr, Type.Array target, Environment environment) {
		WyllFile.Type.Array llType = (WyllFile.Type.Array) translateType(target);
		WyllFile.Expr value = visitExpression(expr.getFirstOperand(), target.getElement(), environment);
		// FIXME: this should be a USIZE
		WyllFile.Expr length = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return new WyllFile.Expr.ArrayGenerator(llType, value, length);
	}

	public WyllFile.Expr visitArrayInitialiser(Expr.ArrayInitialiser expr, Type.Array target, Environment environment) {
		WyllFile.Type.Array llType = (WyllFile.Type.Array) translateType(target);
		WyllFile.Tuple<WyllFile.Expr> exprs = visitExpressions(expr.getOperands(), target.getElement(), environment);
		return new WyllFile.Expr.ArrayInitialiser(llType, exprs);
	}

	public WyllFile.Expr visitArrayUpdate(Expr.ArrayUpdate expr, Type.Array target, Environment environment) {
		// FIXME: should handle this at some point.
		throw new UnsupportedOperationException();
	}

	public WyllFile.Expr visitBitwiseComplement(Expr.BitwiseComplement expr, Environment environment) {
		WyllFile.Type llType = translateType(Type.Byte);
		WyllFile.Expr operand = visitExpression(expr.getOperand(), Type.Byte, environment);
		return new WyllFile.Expr.BitwiseComplement(llType, operand);
	}

	public WyllFile.Expr visitBitwiseAnd(Expr.BitwiseAnd expr, Environment environment) {
		WyllFile.Type llType = translateType(Type.Byte);
		Tuple<WyllFile.Expr> operands = visitExpressions(expr.getOperands(), Type.Byte, environment);
		return new WyllFile.Expr.BitwiseAnd(llType, operands);
	}

	public WyllFile.Expr visitBitwiseOr(Expr.BitwiseOr expr, Environment environment) {
		WyllFile.Type llType = translateType(Type.Byte);
		Tuple<WyllFile.Expr> operands = visitExpressions(expr.getOperands(), Type.Byte, environment);
		return new WyllFile.Expr.BitwiseOr(llType, operands);
	}

	public WyllFile.Expr visitBitwiseXor(Expr.BitwiseXor expr, Environment environment) {
		WyllFile.Type llType = translateType(Type.Byte);
		Tuple<WyllFile.Expr> operands = visitExpressions(expr.getOperands(), Type.Byte, environment);
		return new WyllFile.Expr.BitwiseXor(llType, operands);
	}

	public WyllFile.Expr visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, Environment environment) {
		WyllFile.Type llType = translateType(Type.Byte);
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), Type.Byte, environment);
		// FIXME: should be uint
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return new WyllFile.Expr.BitwiseShiftLeft(llType, lhs, rhs);
	}

	public WyllFile.Expr visitBitwiseShiftRight(Expr.BitwiseShiftRight expr, Environment environment) {
		WyllFile.Type llType = translateType(Type.Byte);
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), Type.Byte, environment);
		// FIXME: should be uint
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return new WyllFile.Expr.BitwiseShiftRight(llType, lhs, rhs);
	}

	public WyllFile.Expr visitCast(Expr.Cast expr, Type target, Environment environment) {
		Type operandT = expr.getOperand().getType();
		WyllFile.Expr operand = visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
		return addCoercion(operand, expr.getType(), target, environment.getContext());
	}

	public WyllFile.Expr visitConstant(Expr.Constant expr, Type target, Environment environment) {
		Value value = expr.getValue();
		if (value instanceof Value.Null) {
			return new WyllFile.Expr.NullConstant();
		} else if (value instanceof Value.Bool) {
			Value.Bool b = (Value.Bool) value;
			return new WyllFile.Expr.BoolConstant(b);
		} else if (value instanceof Value.Byte) {
			Value.Byte b = (Value.Byte) value;
			Value.Int val = new Value.Int(BigInteger.valueOf(b.get() & 0xFF));
			return new WyllFile.Expr.IntConstant(WyllFile.Type.Int, val);
		} else if (value instanceof Value.UTF8) {
			Value.UTF8 b = (Value.UTF8) value;
			byte[] bs = b.get();
			WyllFile.Expr[] values = new WyllFile.Expr[bs.length];
			for (int i = 0; i != bs.length; ++i) {
				Value.Int val = new Value.Int(BigInteger.valueOf(bs[i]));
				values[i] = new WyllFile.Expr.IntConstant(WyllFile.Type.Int, val);
			}
			WyllFile.Type.Array std_ascii = new WyllFile.Type.Array(WyllFile.Type.Int);
			return new WyllFile.Expr.ArrayInitialiser(std_ascii, new Tuple<>(values));
		} else {
			Value.Int i = (Value.Int) value;
			// FIXME: extract target type
			return new WyllFile.Expr.IntConstant(WyllFile.Type.Int, i);
		}
	}

	public WyllFile.Expr visitDereference(Expr.Dereference expr, Type target, Environment environment) {
		// Determine reference type being dereferenced
		Type operandT = expr.getOperand().getType();
		Type.Reference type = extractReadableReferenceType(operandT, environment);
		WyllFile.Type.Reference llType = (WyllFile.Type.Reference) translateType(type);
		//
		WyllFile.Expr operand = visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
		return new WyllFile.Expr.Dereference(llType, operand);
	}

	public WyllFile.Expr visitEqual(Expr.Equal expr, Environment environment) {
		Type lhsT = expr.getFirstOperand().getType();
		Type rhsT = expr.getSecondOperand().getType();
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), lhsT, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), rhsT, environment);
		// FIXME: need to determine target type?
		return new WyllFile.Expr.Equal(lhs, rhs);
	}

	public WyllFile.Expr visitIntegerLessThan(Expr.IntegerLessThan expr, Environment environment) {
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), Type.Int, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		// FIXME: need to determine target type?
		return new WyllFile.Expr.IntegerLessThan(lhs, rhs);
	}

	public WyllFile.Expr visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, Environment environment) {
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), Type.Int, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		// FIXME: need to determine target type?
		return new WyllFile.Expr.IntegerLessThanOrEqual(lhs, rhs);
	}

	public WyllFile.Expr visitIntegerGreaterThan(Expr.IntegerGreaterThan expr, Environment environment) {
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), Type.Int, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		// FIXME: need to determine target type?
		return new WyllFile.Expr.IntegerGreaterThan(lhs, rhs);
	}

	public WyllFile.Expr visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, Environment environment) {
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), Type.Int, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		// FIXME: need to determine target type?
		return new WyllFile.Expr.IntegerGreaterThanOrEqual(lhs, rhs);
	}

	public WyllFile.Expr visitIntegerNegation(Expr.IntegerNegation expr, Type.Int target, Environment environment) {
		WyllFile.Type llType = translateType(target);
		WyllFile.Expr operand = visitExpression(expr.getOperand(), target, environment);
		return new WyllFile.Expr.IntegerNegation(llType, operand);
	}

	public WyllFile.Expr visitIntegerAddition(Expr.IntegerAddition expr, Type.Int target, Environment environment) {
		WyllFile.Type llType = translateType(target);
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), target, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), target, environment);
		return new WyllFile.Expr.IntegerAddition(llType, lhs, rhs);
	}

	public WyllFile.Expr visitIntegerSubtraction(Expr.IntegerSubtraction expr, Type.Int target, Environment environment) {
		WyllFile.Type llType = translateType(target);
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), target, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), target, environment);
		return new WyllFile.Expr.IntegerSubtraction(llType, lhs, rhs);
	}

	public WyllFile.Expr visitIntegerMultiplication(Expr.IntegerMultiplication expr, Type.Int target, Environment environment) {
		WyllFile.Type llType = translateType(target);
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), target, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), target, environment);
		return new WyllFile.Expr.IntegerMultiplication(llType, lhs, rhs);
	}

	public WyllFile.Expr visitIntegerDivision(Expr.IntegerDivision expr, Type.Int target, Environment environment) {
		WyllFile.Type llType = translateType(target);
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), target, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), target, environment);
		return new WyllFile.Expr.IntegerDivision(llType, lhs, rhs);
	}

	public WyllFile.Expr visitIntegerRemainder(Expr.IntegerRemainder expr, Type.Int target, Environment environment) {
		WyllFile.Type llType = translateType(target);
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), target, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), target, environment);
		return new WyllFile.Expr.IntegerRemainder(llType, lhs, rhs);
	}

	public WyllFile.Expr visitIs(Expr.Is expr, Environment environment) {
		Type type = expr.getOperand().getType();
		WyllFile.Expr operand = visitExpression(expr.getOperand(), type, environment);
		return callRuntimeTypeTest(operand, type, expr.getTestType(), environment.getContext());
	}

	public WyllFile.Expr visitLogicalAnd(Expr.LogicalAnd expr, Environment environment) {
		Tuple<WyllFile.Expr> operands = visitExpressions(expr.getOperands(), Type.Bool, environment);
		return new WyllFile.Expr.LogicalAnd(operands);
	}

	public WyllFile.Expr visitLogicalImplication(Expr.LogicalImplication expr, Environment environment) {
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), Type.Bool, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), Type.Bool, environment);
		WyllFile.Tuple<WyllFile.Expr> operands = new WyllFile.Tuple<>(new WyllFile.Expr.LogicalNot(lhs), rhs);
		return new WyllFile.Expr.LogicalOr(operands);
	}

	public WyllFile.Expr visitLogicalIff(Expr.LogicalIff expr, Environment environment) {
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), Type.Bool, environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), Type.Bool, environment);
		return new WyllFile.Expr.Equal(lhs, rhs);
	}

	public WyllFile.Expr visitLogicalNot(Expr.LogicalNot expr, Environment environment) {
		WyllFile.Expr operand = visitExpression(expr.getOperand(), Type.Bool, environment);
		return new WyllFile.Expr.LogicalNot(operand);
	}

	public WyllFile.Expr visitLogicalOr(Expr.LogicalOr expr, Environment environment) {
		Tuple<WyllFile.Expr> operands = visitExpressions(expr.getOperands(), Type.Bool, environment);
		return new WyllFile.Expr.LogicalOr(operands);
	}

	public WyllFile.Expr visitExistentialQuantifier(Expr.ExistentialQuantifier expr, Environment environment) {
		return constructQuantifier(expr,environment);
	}

	public WyllFile.Expr visitUniversalQuantifier(Expr.UniversalQuantifier expr, Environment environment) {
		return constructQuantifier(expr,environment);
	}

	public WyllFile.Expr visitInvoke(Expr.Invoke expr, Tuple<Type> targets, Environment environment) {
		Type.Callable signature = expr.getSignature();
		Tuple<Type> parameters = signature.getParameters();
		WyllFile.Type.Method llType = translateCallable(expr.getSignature());
		if (signature instanceof Type.Method) {
			// Must bind lifetime arguments
			Decl.Method decl = resolveMethod(expr.getName(), (Type.Method) expr.getSignature());
			parameters = bind(decl, expr.getLifetimes());
		}
		Tuple<WyllFile.Expr> arguments = visitExpressions(expr.getOperands(), parameters, environment);
		return new WyllFile.Expr.Invoke(expr.getName(), arguments, llType);
	}

	public WyllFile.Expr visitIndirectInvoke(Expr.IndirectInvoke expr, Tuple<Type> targets, Environment environment) {
		Type.Callable signature = asType(expr.getSource().getType(), Type.Callable.class);
		WyllFile.Type llType = translateType(signature);
		WyllFile.Expr source = visitExpression(expr.getSource(), signature, environment);
		Tuple<WyllFile.Expr> arguments = visitExpressions(expr.getArguments(), signature.getParameters(), environment);
		return new WyllFile.Expr.IndirectInvoke(llType, source, arguments);
	}

	public WyllFile.Expr visitLambdaAccess(Expr.LambdaAccess expr, Type.Callable type, Environment environment) {
		WyllFile.Type.Method llType = translateCallable(type);
		return new WyllFile.Expr.LambdaAccess(expr.getName(), llType);
	}

	public WyllFile.Expr visitLambda(Decl.Lambda decl, Environment environment) {
		// Redeclare this within
		environment = environment.declareWithin("this", decl.getLifetimes());
		//
		Tuple<Identifier> parameters = decl.getParameters().map(vd -> vd.getName());;
		WyllFile.Expr body = visitExpression(decl.getBody(), decl.getBody().getType(), environment);
		//return reducer.visitLambda(decl.getType(), parameters, body);
		throw new IllegalArgumentException("Need to implement lambdas");
	}

	public WyllFile.Expr visitNew(Expr.New expr, Type.Reference target, Environment environment) {
		WyllFile.Type.Reference llType = (WyllFile.Type.Reference) translateType(target);
		WyllFile.Expr operand = visitExpression(expr.getOperand(), target.getElement(), environment);
		return new WyllFile.Expr.New(llType, operand);
	}

	public WyllFile.Expr visitNotEqual(Expr.NotEqual expr, Environment environment) {
		// FIXME: determine target types
		Type lhsT = expr.getFirstOperand().getType();
		Type rhsT = expr.getSecondOperand().getType();
		WyllFile.Expr lhs = visitExpression(expr.getFirstOperand(), expr.getFirstOperand().getType(), environment);
		WyllFile.Expr rhs = visitExpression(expr.getSecondOperand(), expr.getSecondOperand().getType(), environment);
		return new WyllFile.Expr.NotEqual(lhs, rhs);
	}

	public WyllFile.Expr visitRecordAccess(Expr.RecordAccess expr, Type target, Environment environment) {
		Type.Record type = extractReadableRecordType(expr.getType(), environment);
		WyllFile.Type.Record llType = (WyllFile.Type.Record) translateType(type);
		WyllFile.Expr src = visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
		return new WyllFile.Expr.RecordAccess(llType, src, expr.getField());
	}

	public WyllFile.Expr visitRecordInitialiser(Expr.RecordInitialiser expr, Type.Record target, Environment environment) {
		Type.Record type = extractReadableRecordType(target, environment);
		WyllFile.Type.Record llType = (WyllFile.Type.Record) translateType(type);
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		WyllFile.Expr[] ops = new WyllFile.Expr[operands.size()];
		for (int i = 0; i != fields.size(); ++i) {
			Expr operand = operands.get(i);
			Type fieldType = target.getField(fields.get(i));
			if (type == null) {
				// NOTE: open records may not have concrete types for fields.
				fieldType = operand.getType();
			}
			ops[i] = visitExpression(operand, fieldType, environment);
		}
		return new WyllFile.Expr.RecordInitialiser(llType, expr.getFields(), new Tuple<>(ops));
	}

	public WyllFile.Expr visitRecordUpdate(Expr.RecordUpdate expr, Type.Record target, Environment environment) {
		// FIXME: this should be implemented one day
		throw new UnsupportedOperationException();
	}

	public WyllFile.Expr visitStaticVariableAccess(Expr.StaticVariableAccess expr, Type target, Environment environment) {
		WyllFile.Type llType = translateType(target);
		// FIXME: coercion required!
		return new WyllFile.Expr.StaticVariableAccess(llType, expr.getName());
	}

	public WyllFile.Expr visitVariableAccess(Expr.VariableAccess expr, Type target, Environment environment) {
		WyllFile.Type llType = translateType(target);
		// FIXME: coercion required!
		return new WyllFile.Expr.VariableAccess(llType, expr.getVariableDeclaration().getName());
	}

	// =============================================================================================
	// Coercions
	// =============================================================================================


	/**
	 * Add an implicit coercion for a given expression going from one type to
	 * another. Such coercions are necessary to ensure data values have the right
	 * representations at the right points. A simple illustration:
	 *
	 * <pre>
	 * function f(int:16 x) -> (int:32 y):
	 *    return x
	 * </pre>
	 *
	 * At the point of the return an implicit coercion should be inserted to change
	 * the representation of <code>x</code> from <code>int:16</code> to
	 * <code>int:32</code>. On many architectures, this may result in a "sign
	 * extension" operation. Another example:
	 *
	 * <pre>
	 * function f(int:16 x) -> (int:16|bool x):
	 *    return x
	 * </pre>
	 *
	 * In this case, the value held by <code>x</code> is moving from an
	 * <i>untagged</i> representation to a <i>tagged</i> representation. In this
	 * particular case only one bit of tag information is required. Therefore, in
	 * the best case scenario, the parameter requires 16 bits and the return
	 * requires 17 bits.
	 *
	 * @param expr
	 *            The value generated from this expression is to be coerced.
	 * @param actual
	 *            The type of the value produced by the expression.
	 * @param target
	 *            The type to which the value should be coerced.
	 * @param context
	 *            The context in which this coercion is taking place.
	 * @return
	 */
	public WyllFile.Expr addCoercion(WyllFile.Expr expr, Type actual, Type target, Context context) {
		// FIXME: need to actually do something here.
		return expr;
	}

	// =============================================================================================
	// Types
	// =============================================================================================

	public Tuple<WyllFile.Type> translateTypes(Tuple<Type> types) {
		WyllFile.Type[] ntypes = new WyllFile.Type[types.size()];
		for (int i = 0; i != types.size(); ++i) {
			ntypes[i] = translateType(types.get(i));
		}
		return new Tuple<>(ntypes);
	}

	public WyllFile.Type translateType(Type type) {
		switch (type.getOpcode()) {
		case TYPE_array:
			return translateArray((Type.Array) type);
		case TYPE_bool:
			return translateBool((Type.Bool) type);
		case TYPE_byte:
			return translateByte((Type.Byte) type);
		case TYPE_int:
			return translateInt((Type.Int) type);
		case TYPE_nominal:
			return translateNominal((Type.Nominal) type);
		case TYPE_null:
			return translateNull((Type.Null) type);
		case TYPE_record:
			return translateRecord((Type.Record) type);
		case TYPE_staticreference:
		case TYPE_reference:
			return translateReference((Type.Reference) type);
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
			return translateCallable((Type.Callable) type);
		case TYPE_union:
			return translateUnion((Type.Union) type);
		case TYPE_void:
			return translateVoid((Type.Void) type);
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public WyllFile.Type.Method translateCallable(Type.Callable type) {
		switch (type.getOpcode()) {
		case TYPE_function:
			return translateFunction((Type.Function) type);
		case TYPE_method:
			return translateMethod((Type.Method) type);
		case TYPE_property:
			return translateProperty((Type.Property) type);
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public WyllFile.Type.Array translateArray(Type.Array type) {
		WyllFile.Type element = translateType(type.getElement());
		return new WyllFile.Type.Array(element);
	}

	public WyllFile.Type.Bool translateBool(Type.Bool type) {
		return new WyllFile.Type.Bool();
	}

	public WyllFile.Type translateByte(Type.Byte type) {
		return new WyllFile.Type.Int();
	}

	public WyllFile.Type.Method translateFunction(Type.Function type) {
		Tuple<WyllFile.Type> parameters = translateTypes(type.getParameters());
		WyllFile.Type returns = translateType(getMultipleReturnType(type.getReturns()));
		return new WyllFile.Type.Method(parameters, returns);
	}

	public WyllFile.Type translateInt(Type.Int type) {
		return new WyllFile.Type.Int();
	}

	public WyllFile.Type.Method translateMethod(Type.Method type) {
		Tuple<WyllFile.Type> parameters = translateTypes(type.getParameters());
		WyllFile.Type returns = translateType(getMultipleReturnType(type.getReturns()));
		return new WyllFile.Type.Method(parameters, returns);
	}

	public WyllFile.Type translateNominal(Type.Nominal type) {
		try {
			WhileyFile.Decl.Type decl = resolver.resolveExactly(type.getName(), WhileyFile.Decl.Type.class);
			if (decl.isRecursive()) {
				// FIXME: is this always the correct translation?
				return new WyllFile.Type.Nominal(type.getName());
			} else {
				return translateType(decl.getType());
			}
		} catch (NameResolver.ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	public WyllFile.Type translateNull(Type.Null type) {
		return new WyllFile.Type.Null();
	}

	public WyllFile.Type.Method translateProperty(Type.Property type) {
		Tuple<WyllFile.Type> parameters = translateTypes(type.getParameters());
		WyllFile.Type returns = translateType(getMultipleReturnType(type.getReturns()));
		return new WyllFile.Type.Method(parameters, returns);
	}

	public WyllFile.Type.Record translateRecord(Type.Record type) {
		Tuple<WhileyFile.Type.Field> fields = type.getFields();
		WyllFile.Decl.Variable[] returns = new WyllFile.Decl.Variable[fields.size()];
		for (int i = 0; i != fields.size(); ++i) {
			WhileyFile.Type.Field decl = fields.get(i);
			Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
			WyllFile.Type declType = translateType(decl.getType());
			returns[i] = new WyllFile.Decl.Variable(modifiers, decl.getName(), declType);
		}
		return new WyllFile.Type.Record(type.isOpen(), new Tuple<>(returns));
	}

	public WyllFile.Type translateReference(Type.Reference type) {
		WyllFile.Type element = translateType(type.getElement());
		return new WyllFile.Type.Reference(element);
	}

	public WyllFile.Type translateUnion(Type.Union type) {
		WyllFile.Type[] types = new WyllFile.Type[type.size()];
		for (int i = 0; i != type.size(); ++i) {
			types[i] = translateType(type.get(i));
		}
		return new WyllFile.Type.Union(types);
	}

	public WyllFile.Type translateVoid(Type.Void type) {
		return new WyllFile.Type.Void();
	}

	// =============================================================================================
	// Type invariants
	// =============================================================================================

	// =============================================================================================
	// Coercions
	// =============================================================================================

	// =============================================================================================
	// Runtime Type Tests
	// =============================================================================================

	/**
	 * Construct an invocation to a runtime type test. In some cases, this could
	 * potentially inline the test. For now, it always calls a method which gets
	 * generated.
	 *
	 * @param expr
	 * @param actual
	 * @param test
	 * @param context
	 * @return
	 */
	public WyllFile.Expr callRuntimeTypeTest(WyllFile.Expr expr, Type actual, Type test, Context context) {
		WyllFile.Type parameter = translateType(actual);
		WyllFile.Type.Method signature = new WyllFile.Type.Method(new Tuple<>(parameter), WyllFile.Type.Bool);
		Name name = context.registerRuntimeTypeTest(actual,test);
		return new WyllFile.Expr.Invoke(name, new Tuple<>(expr), signature);
	}

	public void constructRuntimeTypeTests(Context context) {
		// This goes through the list of register runtime type tests and creates them.
		List<Pair<Type, Type>> typeTests = context.runtimeTypeTests();
		for (int i = 0; i != typeTests.size(); ++i) {
			Pair<Type, Type> rtt = typeTests.get(i);
			WyllFile.Stmt.Block body = constructRuntimeTypeTest(rtt.first(), rtt.second(), context);
			// FIXME: what modifiers should this have?
			Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
			WyllFile.Type type = translateType(rtt.first());
			WyllFile.Decl.Variable parameter = new WyllFile.Decl.Variable(modifiers, new Identifier("x"), type);
			Identifier name = new Identifier("is$" + i);
			Tuple<WyllFile.Decl.Variable> parameters = new Tuple<>(parameter);
			WyllFile.Decl.Method method = new WyllFile.Decl.Method(modifiers, name, parameters, WyllFile.Type.Bool,
					body);
			context.declarations.add(method);
		}
	}

	public WyllFile.Stmt.Block constructRuntimeTypeTest(Type actual, Type test, Context context) {
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
				return constructUnionUnionRuntimeTypeTest((Type.Union) actual, (Type.Union) test, context);
			case TYPE_nominal:
				return constructNominalNominalRuntimeTypeTest((Type.Nominal) actual, (Type.Nominal) test, context);
			default:
				return internalFailure("need to implement type tests", test);
			}
		} else if (actual instanceof Type.Nominal) {
			return constructNominalTypeRuntimeTypeTest((Type.Nominal) actual, test, context);
		} else if (actual instanceof Type.Union) {
			return constructUnionTypeRuntimeTypeTest((Type.Union) actual, test, context);
		} else if (test instanceof Type.Union) {
			return constructTypeUnionRuntimeTypeTest(actual, (Type.Union) test, context);
		} else if (test instanceof Type.Nominal) {
			return constructTypeNominalRuntimeTypeTest(actual, (Type.Nominal) test, context);
		} else {
			return internalFailure("need to implement type tests", test);
		}
	}

	public WyllFile.Stmt.Block constructTrivialRuntimeTypeTest() {
		WyllFile.Stmt.Return ret = new WyllFile.Stmt.Return(new WyllFile.Expr.BoolConstant(new WyllFile.Value.Bool(true)));
		return new WyllFile.Stmt.Block(ret);
	}

	public WyllFile.Stmt.Block constructNominalTypeRuntimeTypeTest(Type.Nominal actual, Type test, Context context) {
		try {
			WhileyFile.Decl.Type decl = resolver.resolveExactly(actual.getName(), WhileyFile.Decl.Type.class);
			return constructRuntimeTypeTest(decl.getType(), test, context);
		} catch (ResolutionError e) {
			return internalFailure(e.getMessage(), test, e);
		}
	}

	public WyllFile.Stmt.Block constructTypeNominalRuntimeTypeTest(Type actual, Type.Nominal test, Context context) {
		try {
			WhileyFile.Decl.Type decl = resolver.resolveExactly(test.getName(), WhileyFile.Decl.Type.class);
			return constructRuntimeTypeTest(actual, decl.getType(), context);
		} catch (ResolutionError e) {
			return internalFailure(e.getMessage(), test, e);
		}
	}

	public WyllFile.Stmt.Block constructNominalNominalRuntimeTypeTest(Type.Nominal actual, Type.Nominal test,
			Context context) {
		try {
			WhileyFile.Decl.Type decl = resolver.resolveExactly(test.getName(), WhileyFile.Decl.Type.class);
			return constructRuntimeTypeTest(actual,decl.getType(),context);
		} catch (ResolutionError e) {
			return internalFailure(e.getMessage(),test,e);
		}
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
	public WyllFile.Stmt.Block constructTypeUnionRuntimeTypeTest(Type actual, Type.Union test, Context context) {
		WyllFile.Stmt.Return ret = new WyllFile.Stmt.Return(new WyllFile.Expr.BoolConstant(new WyllFile.Value.Bool(true)));
		return new WyllFile.Stmt.Block(ret);
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
	 *   return #x == 0
	 * }
	 * </pre>
	 *
	 *
	 *
	 * @param actual
	 * @param test
	 * @return
	 */
	public WyllFile.Stmt.Block constructUnionTypeRuntimeTypeTest(Type.Union actual, Type test, Context context) {
		int tag = determineTag(actual,test);
		WyllFile.Type llActual = translateType(actual);
		WyllFile.Expr var = new WyllFile.Expr.VariableAccess(llActual, new Identifier("x"));
		WyllFile.Expr lhs = new WyllFile.Expr.UnionAccess(llActual, var);
		WyllFile.Expr rhs = new WyllFile.Expr.IntConstant(WyllFile.Type.Int, new Value.Int(tag));
		WyllFile.Expr condition = new WyllFile.Expr.Equal(lhs, rhs);
		WyllFile.Stmt.Return ret = new WyllFile.Stmt.Return(condition);
		return new WyllFile.Stmt.Block(ret);
	}

	public WyllFile.Stmt.Block constructUnionUnionRuntimeTypeTest(Type.Union actual, Type.Union test, Context context) {
		WyllFile.Stmt.Return ret = new WyllFile.Stmt.Return(new WyllFile.Expr.BoolConstant(new WyllFile.Value.Bool(true)));
		return new WyllFile.Stmt.Block(ret);
	}

	// =============================================================================================
	// Quantifiers
	// =============================================================================================

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
	public WyllFile.Expr constructQuantifier(Expr.Quantifier expr, Environment environment) {
		Context context = environment.getContext();
		boolean isUniversal = expr instanceof Expr.UniversalQuantifier;
		// Determine the set of used variables within the quantifier body. This is
		// necessary to ensure that these are passed appropriately as parameters.
		Set<WhileyFile.Decl.Variable> uses = determineUsedVariables(expr);
		// Construct the set of parameters which will be passed into the quantifier
		// method.
		Tuple<WyllFile.Decl.Variable> parameters = constructQuantifierParameters(uses);
		// Construct the loop nest
		WyllFile.Expr condition = visitExpression(expr.getOperand(), Type.Bool, environment);
		WyllFile.Stmt loop = constructQuantifierBody(expr,0,condition,environment);
		// Construct final return statement to catch where have come through entire loop.
		WyllFile.Expr retval = new WyllFile.Expr.BoolConstant(new Value.Bool(isUniversal));
		WyllFile.Stmt.Return ret = new WyllFile.Stmt.Return(retval);
		// Construct method itself
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		Identifier name = new Identifier("expr$" + context.declarations.size());
		WyllFile.Stmt.Block body = new WyllFile.Stmt.Block(loop, ret);
		context.declarations.add(new WyllFile.Decl.Method(modifiers, name, parameters, WyllFile.Type.Bool, body));
		// Construct method invocation
		Tuple<WyllFile.Type> parameterTypes = parameters.map((WyllFile.Decl.Variable x) -> x.getType());
		WyllFile.Type.Method signature = new WyllFile.Type.Method(parameterTypes, WyllFile.Type.Bool);
		return new WyllFile.Expr.Invoke(new Name(name), constructQuantifierArguments(parameters), signature);
	}

	public Tuple<WyllFile.Decl.Variable> constructQuantifierParameters(Set<WhileyFile.Decl.Variable> uses) {
		WyllFile.Decl.Variable[] parameters = new WyllFile.Decl.Variable[uses.size()];
		int index = 0;
		for (WhileyFile.Decl.Variable use : uses) {
			WyllFile.Type type = translateType(use.getType());
			parameters[index++] = new WyllFile.Decl.Variable(new Tuple<>(), use.getName(), type);
		}
		return new Tuple<>(parameters);
	}

	public Tuple<WyllFile.Expr> constructQuantifierArguments(Tuple<WyllFile.Decl.Variable> parameters) {
		WyllFile.Expr[] args = new WyllFile.Expr[parameters.size()];
		for (int i = 0; i != parameters.size(); ++i) {
			WyllFile.Decl.Variable parameter = parameters.get(i);
			args[i] = new WyllFile.Expr.VariableAccess(parameter.getType(), parameter.getName());
		}
		return new Tuple<>(args);
	}

	public WyllFile.Stmt constructQuantifierBody(Expr.Quantifier expr, int index, WyllFile.Expr condition, Environment environment) {
		boolean isUniversal = expr instanceof Expr.UniversalQuantifier;
		Tuple<Decl.Variable> parameters = expr.getParameters();
		if (index == parameters.size()) {
			// This indicates we are now within the innermost loop body. Therefore, we
			// create the necessary test for the quantifier condition.
			WyllFile.Expr retval = new WyllFile.Expr.BoolConstant(new Value.Bool(!isUniversal));
			WyllFile.Stmt retstmt = new WyllFile.Stmt.Return(retval);
			WyllFile.Stmt.Block block = new WyllFile.Stmt.Block(retstmt);
			if(isUniversal) {
				condition = new WyllFile.Expr.LogicalNot(condition);
			}
			WyllFile.Pair<WyllFile.Expr,WyllFile.Stmt.Block> branch = new WyllFile.Pair<>(condition,block);
			return new WyllFile.Stmt.IfElse(new Tuple<>(branch));
		} else {
			// This is the recursive case. For each parameter we create a nested foreach
			// loop which iterates over the given range
			Decl.Variable parameter = parameters.get(index);
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			// FIXME: should be usize
			WyllFile.Expr start = visitExpression(range.getFirstOperand(), Type.Int, environment);
			WyllFile.Expr end = visitExpression(range.getSecondOperand(), Type.Int, environment);
			// Construct index variable
			WyllFile.Type type = translateType(parameter.getType());
			WyllFile.Decl.Variable var = new WyllFile.Decl.Variable(new Tuple<>(), parameter.getName(), type);
			// Recursively create nested loops for remaining parameters
			WyllFile.Stmt body = constructQuantifierBody(expr, index + 1, condition, environment);
			// Return the loop for this parameter
			return new WyllFile.Stmt.ForEach(var, start, end, new WyllFile.Stmt.Block(body));
		}
	}

	// =============================================================================================
	// Misc
	// =============================================================================================

	public Type.Int selectInt(Type target, Expr expr, Environment environment) {
		Type.Int type = asType(expr.getType(), Type.Int.class);
		Type.Int[] ints = TYPE_INT_FILTER.apply(target, resolver);
		return selectCandidate(ints, type, environment);
	}

	/**
	 * For a give method type, substitute all declared lifetimes with the actual
	 * lifetime arguments used. For example, consider this method:
	 *
	 * <pre>
	 * method <a> m(&a:int ptr) -> int:
	 *    return *ptr
	 * </pre>
	 *
	 * Suppose we are visiting an invocation <code>m<this>(p)</code>. Then, the
	 * (declared) method type will be <code>method<a>(&a:int)</code>, and the actual
	 * lifetime argument will be <code>this</code>. The result from this method
	 * would then be <code>method(&this:int)</code>.
	 *
	 * @param type
	 * @param actual
	 * @param declared
	 * @return
	 */
	public Tuple<Type> bind(Decl.Method decl, Tuple<Identifier> actual) {
		Type.Method type = decl.getType();
		Tuple<Identifier> declared = type.getLifetimeParameters();
		HashMap<Identifier, Identifier> binding = new HashMap<>();
		for (int i = 0; i != declared.size(); ++i) {
			binding.put(declared.get(i), actual.get(i));
		}
		return WhileyFile.substitute(type.getParameters(),binding);
	}

	public Decl.Method resolveMethod(Name name, Type.Method signature) {
		// NOTE: this method should be deprecated when resolution is done away with
		// finally.
		try {
			List<Decl.Method> methods = resolver.resolveAll(name, Decl.Method.class);
			for (int i = 0; i != methods.size(); ++i) {
				Decl.Method method = methods.get(i);
				if (method.getType().equals(signature)) {
					return method;
				}
			}
		} catch (ResolutionError e) {
		}
		return internalFailure("unknown method", name);
	}

	/**
	 * Given a general type which is know to represent an array type, extract the
	 * readable array type.
	 *
	 * @param type
	 * @param environment
	 * @return
	 */
	public Type.Array extractReadableArrayType(Type type, Environment environment) {
		SemanticType.Array r = rwTypeExtractor.apply(type, environment, ReadWriteTypeExtractor.READABLE_ARRAY);
		return (Type.Array) concreteTypeExtractor.apply(r, environment);
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
	 * <p>
	 * Given an arbitrary target type, filter out the target array types. For
	 * example, consider the following method:
	 * </p>
	 *
	 *
	 * <pre>
	 * method f(int x):
	 *    null|int[] xs = [x]
	 *    ...
	 * </pre>
	 * <p>
	 * When type checking the expression <code>[x]</code> the flow type checker will
	 * attempt to determine an <i>expected</i> array type. In order to then
	 * determine the appropriate expected type for expression <code>x</code> it
	 * filters <code>null|int[]</code> down to just <code>int[]</code>.
	 * </p>
	 *
	 * @param target
	 *            Target type for this value
	 * @param expr
	 *            Source expression for this value
	 * @author David J. Pearce
	 *
	 */
	public Type.Array selectArray(Type target, Expr expr, Environment environment) {
		Type.Array type = asType(expr.getType(), Type.Array.class);
		Type.Array[] records = TYPE_ARRAY_FILTER.apply(target, resolver);
		return selectCandidate(records, type, environment);
	}

	/**
	 * <p>
	 * Given an arbitrary target type, filter out the target record types. For
	 * example, consider the following method:
	 * </p>
	 *
	 *
	 * <pre>
	 * method f(int x):
	 *    {int f}|null xs = {f: x}
	 *    ...
	 * </pre>
	 * <p>
	 * When type checking the expression <code>{f: x}</code> the flow type checker
	 * will attempt to determine an <i>expected</i> record type. In order to then
	 * determine the appropriate expected type for field initialiser expression
	 * <code>x</code> it filters <code>{int f}|null</code> down to just
	 * <code>{int f}</code>.
	 * </p>
	 *
	 * @param target
	 *            Target type for this value
	 * @param expr
	 *            Source expression for this value
	 * @author David J. Pearce
	 *
	 */
	public Type.Record selectRecord(Type target, Expr expr, Environment environment) {
		Type.Record type = asType(expr.getType(), Type.Record.class);
		Type.Record[] records = TYPE_RECORD_FILTER.apply(target, resolver);
		return selectCandidate(records, type, environment);
	}

	/**
	 * <p>
	 * Given an arbitrary target type, filter out the target reference types. For
	 * example, consider the following method:
	 * </p>
	 *
	 *
	 * <pre>
	 * method f(int x):
	 *    &int|null xs = new(x)
	 *    ...
	 * </pre>
	 * <p>
	 * When type checking the expression <code>new(x)</code> the flow type checker
	 * will attempt to determine an <i>expected</i> reference type. In order to then
	 * determine the appropriate expected type for element expression <code>x</code>
	 * it filters <code>&int|null</code> down to just <code>&int</code>.
	 * </p>
	 *
	 * @param target
	 *            Target type for this value
	 * @param expr
	 *            Source expression for this value
	 *
	 * @author David J. Pearce
	 *
	 */
	public Type.Reference selectReference(Type target, Expr expr, Environment environment) {
		Type.Reference type = asType(expr.getType(), Type.Reference.class);
		Type.Reference[] references = TYPE_REFERENCE_FILTER.apply(target, resolver);
		return selectCandidate(references, type, environment);
	}

	/**
	 * <p>
	 * Given an arbitrary target type, filter out the target lambda types. For
	 * example, consider the following method:
	 * </p>
	 *
	 *
	 * <pre>
	 * type fun_t is function(int)->(int)
	 *
	 * method f(int x):
	 *    fun_t|null xs = &(int y -> y+1)
	 *    ...
	 * </pre>
	 * <p>
	 * When type checking the expression <code>&(int y -> y+1)</code> the flow type
	 * checker will attempt to determine an <i>expected</i> lambda type. In order to
	 * then determine the appropriate expected type for the lambda body
	 * <code>y+1</code> it filters <code>fun_t|null</code> down to just
	 * <code>fun_t</code>.
	 * </p>
	 *
	 * @param target
	 *            Target type for this value
	 * @param expr
	 *            Source expression for this value
	 * @author David J. Pearce
	 *
	 */
	public Type.Callable selectLambda(Type target, Expr expr, Environment environment) {
		Type.Callable type = asType(expr.getType(), Type.Callable.class);
		// Construct the default case for matching against any
		Type.Callable anyType = new Type.Function(type.getParameters(), TUPLE_ANY);
		// Create the filter itself
		AbstractTypeFilter<Type.Callable> filter = new AbstractTypeFilter<>(Type.Callable.class, anyType);
		//
		return selectCandidate(filter.apply(target, resolver), type, environment);
	}

	private static Tuple<Type> TUPLE_ANY = new Tuple<>(Type.Any);

	/**
	 * Given an array of candidate types, select the most precise match for a actual
	 * type. If no such candidate exists, return null (which should be impossible
	 * for type correct code).
	 *
	 * @param candidates
	 * @param actual
	 * @return
	 */
	public <T extends Type> T selectCandidate(T[] candidates, T actual, Environment environment) {
		//
		try {
			T candidate = null;
			for (int i = 0; i != candidates.length; ++i) {
				T next = candidates[i];
				if (subtypeOperator.isSubtype(next, actual, environment)) {
					if (candidate == null) {
						candidate = next;
					} else {
						candidate = selectCandidate(candidate, next, actual, environment);
					}
				}
			}
			//
			return candidate;
		} catch (ResolutionError e) {
			return internalFailure(e.getMessage(), actual, e);
		}
	}

	/**
	 * Given two candidates, return the more precise one. If no viable candidate,
	 * return null;
	 *
	 * @param candidate
	 *            The current best candidate.
	 * @param next
	 *            The next candidate being considered
	 * @param environment
	 * @return
	 * @throws ResolutionError
	 */
	public <T extends Type> T selectCandidate(T candidate, T next, T actual, Environment environment)
			throws ResolutionError {
		// Found a viable candidate
		boolean left = subtypeOperator.isSubtype(candidate, next, environment);
		boolean right = subtypeOperator.isSubtype(next, candidate, environment);
		if (left && !right) {
			// Yes, is better than current candidate
			return next;
		} else if (right && !left) {
			return candidate;
		}
		// Unable to distinguish options based on subtyping alone
		left = isDerivation(next, actual);
		right = isDerivation(candidate, actual);
		if (left && !right) {
			// Yes, is better than current candidate
			return next;
		} else if (right && !left) {
			return candidate;
		} else {
			return null;
		}

	}

	/**
	 * Check whether one type is a derivation of another. For example, in this
	 * scenario:
	 *
	 * <pre>
	 * type parent is (int p) where ...
	 * type child is (parent c) where ...
	 * </pre>
	 *
	 * @param parent
	 *            The type being derived to
	 * @param child
	 *            The type we are trying to derive
	 * @return
	 */
	public boolean isDerivation(Type parent, Type child) {
		if (child.equals(parent)) {
			return true;
		} else if (child instanceof Type.Nominal) {
			try {
				Type.Nominal t = (Type.Nominal) child;
				Decl.Type decl = resolver.resolveExactly(t.getName(), Decl.Type.class);
				return isDerivation(parent, decl.getType());
			} catch (NameResolver.ResolutionError e) {
				return internalFailure(e.getMessage(), child, e);
			}
		} else {
			return false;
		}
	}

	/**
	 * Unwrap a given type to reveal its underlying kind. For example, the type
	 * <code>int</code> can be unwrapped only to <code>int</code>. A more complex
	 * example:
	 *
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * </pre>
	 *
	 * The type <code>nat</code> can be unwrapped to an <code>int</code>. In
	 * general, the unwrapping process expands all nominal types until an atom is
	 * encountered.
	 *
	 * @param type
	 * @param kind
	 * @return
	 */
	public <T extends Type> T asType(Type type, Class<T> kind) {
		if (kind.isInstance(type)) {
			return (T) type;
		} else if (type instanceof Type.Nominal) {
			try {
				Type.Nominal t = (Type.Nominal) type;
				Decl.Type decl = resolver.resolveExactly(t.getName(), Decl.Type.class);
				return asType(decl.getType(), kind);
			} catch (NameResolver.ResolutionError e) {
				return internalFailure(e.getMessage(), type, e);
			}
		} else {
			return internalFailure("invalid type encoutnered", type);
		}
	}

	private static final AbstractTypeFilter<Type.Int> TYPE_INT_FILTER = new AbstractTypeFilter<>(Type.Int.class,
			Type.Int);

	private static final AbstractTypeFilter<Type.Array> TYPE_ARRAY_FILTER = new AbstractTypeFilter<>(Type.Array.class,
			new Type.Array(Type.Any));

	private static final AbstractTypeFilter<Type.Record> TYPE_RECORD_FILTER = new AbstractTypeFilter<>(
			Type.Record.class, new Type.Record(true, new Tuple<>()));

	private static final AbstractTypeFilter<Type.Reference> TYPE_REFERENCE_FILTER = new AbstractTypeFilter<>(
			Type.Reference.class, new Type.Reference(Type.Any));

	private static final Type.Array TYPE_ARRAY_INT = new Type.Array(Type.Int);

	/**
	 * Every type has a default value which is used for initialisation purposes.
	 * This method simply constructs that value.
	 *
	 * @param type
	 * @return
	 */
	public WyllFile.Expr getDefaultValue(WyllFile.Type type) {
		Value value;
		switch (type.getOpcode()) {
		case WyllFile.TYPE_nominal:
		case WyllFile.TYPE_reference:
		case WyllFile.TYPE_null:
			return new WyllFile.Expr.NullConstant();
		case WyllFile.TYPE_bool:
			return new WyllFile.Expr.BoolConstant(new Value.Bool(false));
		case WyllFile.TYPE_int:
			return new WyllFile.Expr.IntConstant((WyllFile.Type.Int) type, new Value.Int(0));
		case WyllFile.TYPE_array: {
			WyllFile.Expr v = getDefaultValue(((WyllFile.Type.Array) type).getElement());
			WyllFile.Expr zero = new WyllFile.Expr.IntConstant(WyllFile.Type.Int, new Value.Int(0));
			return new WyllFile.Expr.ArrayGenerator((WyllFile.Type.Array) type, v, zero);
		}
		case WyllFile.TYPE_record: {
			WyllFile.Type.Record record = (WyllFile.Type.Record) type;
			Tuple<WyllFile.Decl.Variable> fields = record.getFields();
			WyllFile.Expr[] operands = new WyllFile.Expr[fields.size()];
			for (int i = 0; i != fields.size(); ++i) {
				operands[i] = getDefaultValue(fields.get(i).getType());
			}
			Tuple<Identifier> names = fields.map((WyllFile.Decl.Variable d) -> d.getName());
			return new WyllFile.Expr.RecordInitialiser((WyllFile.Type.Record) type, names, new Tuple<>(operands));
		}
		case WyllFile.TYPE_union: {
			WyllFile.Type.Union union = (WyllFile.Type.Union) type;
			return getDefaultValue(union.get(0));
		}
		default:
			throw new UnsupportedOperationException(
					"cannot construct default value (" + type.getClass().getName() + ")");
		}
	}

	/**
	 * Determine the appropriate type tag for a identifying a given child in a given
	 * parent type.
	 *
	 * @param parent
	 * @param child
	 * @return
	 */
	public int determineTag(Type.Union parent, Type child) {
		for (int i = 0; i != parent.size(); ++i) {
			if (isSubtype(parent.get(i), child)) {
				return i;
			}
		}
		throw new IllegalArgumentException("cannot determine appropriate tag (" + parent + "<-" + child + ")");
	}

	/**
	 * Determine the appropriate type tag for a identifying a given child in a given
	 * parent type.
	 *
	 * @param parent
	 * @param child
	 * @return
	 */
	public int determineTag(Type parent, Type.Union child) {
		for (int i = 0; i != child.size(); ++i) {
			if (isSubtype(parent, child.get(i))) {
				return i;
			}
		}
		throw new IllegalArgumentException("cannot determine appropriate tag (" + parent + "<-" + child + ")");
	}

	public boolean isSubtype(Type parent, Type child) {
		try {
			// FIXME: need to handle lifetimes properly (just need the environment for
			// this).
			return subtypeOperator.isSubtype(parent, child, null);
		} catch (ResolutionError e) {
			throw new RuntimeException("internal failure");
		}
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
	 * Create a temporary variable which is guaranteed not to clash with any other
	 * temporaries create, or existing variables in the given scope.
	 *
	 * @param type
	 * @param initialiser
	 * @return
	 */
	public WyllFile.Decl.Variable createTemporaryVariable(Tuple<Type> _types, Expr _init, Environment environment, EnclosingScope scope) {
		Identifier name = new Identifier(environment.getContext().createTemporaryVariable(scope));
		WyllFile.Type type;
		WyllFile.Expr init;
		if (_types.size() == 1) {
			type = translateType(_types.get(0));
			init = visitExpression(_init, _types.get(0), environment);
		} else {
			Type.Record retType = (Type.Record) getMultipleReturnType(_types);
			type = translateType(retType);
			init = visitMultiExpression(_init, _types, environment);
		}
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		return new WyllFile.Decl.Variable(modifiers, name, type, init);
	}

	public WyllFile.Decl.Variable createTemporaryVariable(Type _type, Expr _init, Environment environment, EnclosingScope scope) {
		Identifier name = new Identifier(environment.getContext().createTemporaryVariable(scope));
		WyllFile.Expr init;
		WyllFile.Type type = translateType(_type);
		init = visitExpression(_init, _type, environment);
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		return new WyllFile.Decl.Variable(modifiers, name, type, init);
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
	 * Signal an unrecoverable internal failure has occurred.
	 *
	 * @param msg
	 * @param e
	 * @return
	 */
	private <T> T internalFailure(String msg, SyntacticItem e) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new InternalFailure(msg, cu.getEntry(), e);
	}

	/**
	 * Signal an unrecoverable internal failure has occurred as a result of some
	 * exception being raised.
	 *
	 * @param msg
	 * @param e
	 * @return
	 */
	private <T> T internalFailure(String msg, SyntacticItem e, Throwable ex) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new InternalFailure(msg, cu.getEntry(), e, ex);
	}

	// ==========================================================================
	// Environment
	// ==========================================================================

	/**
	 * Provides a very simple environment for tracking the current declared lifetime
	 * relationships.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Environment implements LifetimeRelation {
		private final Map<String, String[]> withins;
		private final Context context;

		public Environment(Context context) {
			this.withins = new HashMap<>();
			this.context = context;
		}

		public Environment(Map<String, String[]> withins, Context context) {
			this.withins = new HashMap<>(withins);
			this.context = context;
		}

		public Context getContext() {
			return context;
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
			Environment nenv = new Environment(withins, context);
			nenv.withins.put(inner, outers);
			return nenv;
		}
	}

	/**
	 * Context provides a mechanism by which auxillary methods can be declared.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class Context {
		private final List<WyllFile.Decl> declarations;
		/**
		 * The list of register type tests to be generated.
		 */
		private final ArrayList<Pair<Type,Type>> typeTests;
		/**
		 * Maps each top-level declaration to the set of variable names used within.
		 * This is used to ensure temporary variable names do not clash.
		 */
		private final IdentityHashMap<WhileyFile.Decl,HashSet<String>> variables;

		public Context() {
			this.declarations = new ArrayList<>();
			this.variables = new IdentityHashMap<>();
			this.typeTests = new ArrayList<>();
		}

		public List<WyllFile.Decl> getDeclarations() {
			return declarations;
		}

		public void declare(WyllFile.Decl decl) {
			declarations.add(decl);
		}

		public String createTemporaryVariable(EnclosingScope scope) {
			FunctionOrMethodScope fm = scope.getEnclosingScope(FunctionOrMethodScope.class);
			// Get set of variables
			HashSet<String> vars = variables.get(fm.declaration);
			if(vars == null) {
				vars = determineDeclaredVariables(fm.declaration);
				variables.put(fm.declaration, vars);
			}
			// Now create temporary variable name
			String name = "tmp";
			int count = 0;
			while(vars.contains(name)){
				count = count + 1;
				name = "tmp" + count;
			}
			// Register the newly create variable name
			vars.add(name);
			return name;
		}

		public HashSet<String> determineDeclaredVariables(WhileyFile.Decl d) {
			HashSet<String> vars = new HashSet<>();
			AbstractVisitor visitor = new AbstractVisitor() {
				@Override
				public void visitVariable(Decl.Variable var) {
					vars.add(var.getName().toString());
				}
			};
			// Run visitor to collect names
			visitor.visitDeclaration(d);
			//
			return vars;
		}

		public Name registerRuntimeTypeTest(Type actual, Type test) {
			// Check whether type test already register.
			for (int i = 0; i != typeTests.size(); ++i) {
				Pair<Type, Type> p = typeTests.get(i);
				if (actual.equals(p.first()) && test.equals(p.second())) {
					// Yes, found existing test
					return new Name(new Identifier("is$" + i));
				}
			}
			// No existing test found. Register a new one.
			Name name = new Name(new Identifier("is$" + typeTests.size()));
			typeTests.add(new Pair<>(actual, test));
			return name;
		}

		public List<Pair<Type,Type>> runtimeTypeTests() {
			return typeTests;
		}
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
		 * Get the list of all environment declared by this or an enclosing scope. That
		 * is the complete set of environment available at this point.
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
				Tuple<Identifier> environment = meth.getLifetimes();
				String[] arr = new String[environment.size() + 1];
				for (int i = 0; i != environment.size(); ++i) {
					arr[i] = environment.get(i).get();
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
