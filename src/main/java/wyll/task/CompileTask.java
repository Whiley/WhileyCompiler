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

import static wyc.lang.WhileyFile.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wybs.lang.Build;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.LVal;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.Stmt;
import wyc.lang.WhileyFile.Type;
import wyc.util.AbstractConsumer;
import wyc.util.AbstractVisitor;
import wyil.type.subtyping.EmptinessTest;
import wyil.type.subtyping.StrictTypeEmptinessTest;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.util.ConcreteTypeExtractor;
import wyil.type.util.ReadWriteTypeExtractor;
import wyll.core.WyllFile;
import wyll.util.StdTypeMangler;

public class CompileTask {
	protected final NameResolver resolver;
	protected final SubtypeOperator subtypeOperator;
	protected final ReadWriteTypeExtractor rwTypeExtractor;
	protected final ConcreteTypeExtractor concreteTypeExtractor;

	public CompileTask(NameResolver resolver) {
		this.resolver = resolver;
		EmptinessTest<SemanticType> strictEmptiness = new StrictTypeEmptinessTest(resolver);
		this.subtypeOperator = new SubtypeOperator(resolver, strictEmptiness);
		this.rwTypeExtractor = new ReadWriteTypeExtractor(resolver, subtypeOperator);
		this.concreteTypeExtractor = new ConcreteTypeExtractor(resolver,strictEmptiness);
	}

	public WyllFile compile(WhileyFile wf) {
		WyllFile wl = new WyllFile(null);
		List<WhileyFile.Decl.Module> modules = wf.getSyntacticItems(WhileyFile.Decl.Module.class);
		for (int i = 0; i != modules.size(); ++i) {
			WhileyFile.Decl.Module module = modules.get(i);
			Context context = new Context();
			translateWhileyFile(wf, context);
			//
			List<WyllFile.Decl> declarations = context.getDeclarations();
			for (int j = 0; j != declarations.size(); ++j) {
				wl.allocate(declarations.get(j));
			}
			wl.allocate(new WyllFile.Decl.Module(module.getName(), new Tuple<>(declarations)));
		}
		//
		return wl;
	}

	public WyllFile.Expr translateWhileyFile(WhileyFile wf, Context context) {
		for (Decl decl : wf.getDeclarations()) {
			translateDeclaration(decl, context);
		}
		return null;
	}

	// ==========================================================================
	// Declarations
	// ==========================================================================

	public void translateDeclaration(Decl decl, Context context) {
		switch (decl.getOpcode()) {
		case DECL_importfrom:
		case DECL_import:
			translateImport((Decl.Import) decl, context);
			break;
		case DECL_staticvar:
			translateStaticVariable((Decl.StaticVariable) decl, context);
			break;
		case DECL_type:
		case DECL_rectype:
			translateType((Decl.Type) decl, context);
			break;
		case DECL_function:
		case DECL_method:
		case DECL_property:
			translateCallable((Decl.Callable) decl, context);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void translateImport(Decl.Import decl, Context context) {
		// An interesting question as to what to do here, since some backends do support
		// includes.
	}

	public void translateStaticVariable(Decl.StaticVariable decl, Context context) {
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		WyllFile.Type type = translateType(decl.getType());
		if (decl.hasInitialiser()) {
			WyllFile.Expr init = translateExpression(decl.getInitialiser(), decl.getType(), context);
		}
		context.declare(new WyllFile.Decl.StaticVariable(modifiers, decl.getName(), type, init));
	}

	public void translateType(Decl.Type decl, Context context) {
		translateVariable(decl.getVariableDeclaration(), context);
		translateExpressions(decl.getInvariant(), Type.Bool, context);
	}

	public void translateCallable(Decl.Callable decl, Context context) {
		switch (decl.getOpcode()) {
		case DECL_function:
		case DECL_method:
			translateFunctionOrMethod((Decl.FunctionOrMethod) decl, context);
			break;
		case DECL_property:
			translateProperty((Decl.Property) decl, context);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void translateProperty(Decl.Property decl, Context context) {
		translateVariables(decl.getParameters(), context);
		translateVariables(decl.getReturns(), context);
		translateExpressions(decl.getInvariant(), Type.Bool, context);
	}

	public void translateFunctionOrMethod(Decl.FunctionOrMethod decl, Context context) {
		// First, determine the mangled name;
		Identifier name = getNameMangle(decl.getName(), decl.getType());
		if (decl.getModifiers().match(WhileyFile.Modifier.Export.class) == null
				&& decl.getModifiers().match(WhileyFile.Modifier.Native.class) == null) {
			// FIXME: need a trampoline!
		}
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		Tuple<WyllFile.Decl.Variable> parameters = translateVariables(decl.getParameters(), context);
		Tuple<WyllFile.Decl.Variable> returns = translateVariables(decl.getReturns(), context);
		// translateExpressions(decl.getRequires());
		// translateExpressions(decl.getEnsures());
		WyllFile.Stmt.Block body = translateBlock(decl.getBody(), context);

		context.declare(new WyllFile.Decl.Method(modifiers, name, parameters, returns, body));
	}

	public WyllFile.Expr translateLambda(Decl.Lambda decl, Type target, Context context) {
		Tuple<WyllFile.Decl.Variable> parameters = translateVariables(decl.getParameters(), context);
		Tuple<WyllFile.Decl.Variable> returns = translateVariables(decl.getReturns(), context);
		WyllFile.Expr body = translateExpression(decl.getBody(), null, context);
		return new WyllFile.Expr.Lambda(parameters, returns, decl.getCaptures(), decl.getLifetimes(), body);
	}

	public Tuple<WyllFile.Decl.Variable> translateVariables(Tuple<WhileyFile.Decl.Variable> vars, Context context) {
		WyllFile.Decl.Variable[] returns = new WyllFile.Decl.Variable[vars.size()];
		for (int i = 0; i != vars.size(); ++i) {
			Decl.Variable var = vars.get(i);
			returns[i] = translateVariable(var, context);
		}
		return new WyllFile.Tuple<>(returns);
	}

	public WyllFile.Decl.Variable translateVariable(Decl.Variable decl, Context context) {
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		WyllFile.Type type = translateType(decl.getType());
		if (decl.hasInitialiser()) {
			WyllFile.Expr initialiser = translateExpression(decl.getInitialiser(), decl.getType(), context);
			return new WyllFile.Decl.Variable(modifiers, decl.getName(), type, initialiser);
		} else {
			return new WyllFile.Decl.Variable(modifiers, decl.getName(), type);
		}
	}

	// ==========================================================================
	// Statements
	// ==========================================================================

	public List<WyllFile.Stmt> translateStatement(Stmt stmt, Context context) {
		WyllFile.Stmt tmp;
		switch (stmt.getOpcode()) {
		case DECL_variable:
		case DECL_variableinitialiser:
			tmp = translateVariable((Decl.Variable) stmt, context);
			break;
		case STMT_assert:
			tmp = translateAssert((Stmt.Assert) stmt, context);
			break;
		case STMT_assign:
			return translateAssign((Stmt.Assign) stmt, context);
		case STMT_assume:
			tmp = translateAssume((Stmt.Assume) stmt, context);
			break;
		case STMT_block:
			tmp = translateBlock((Stmt.Block) stmt, context);
			break;
		case STMT_break:
			tmp = translateBreak((Stmt.Break) stmt, context);
			break;
		case STMT_continue:
			tmp = translateContinue((Stmt.Continue) stmt, context);
			break;
		case STMT_debug:
			tmp = translateDebug((Stmt.Debug) stmt, context);
			break;
		case STMT_dowhile:
			tmp = translateDoWhile((Stmt.DoWhile) stmt, context);
			break;
		case STMT_fail:
			tmp = translateFail((Stmt.Fail) stmt, context);
			break;
		case STMT_if:
		case STMT_ifelse:
			tmp = translateIfElse((Stmt.IfElse) stmt, context);
			break;
		case EXPR_invoke:
			tmp = translateInvoke((Expr.Invoke) stmt, null, context);
			break;
		case EXPR_indirectinvoke:
			tmp = translateIndirectInvoke((Expr.IndirectInvoke) stmt, null, context);
			break;
		case STMT_namedblock:
			tmp = translateNamedBlock((Stmt.NamedBlock) stmt, context);
			break;
		case STMT_return:
			tmp = translateReturn((Stmt.Return) stmt, context);
			break;
		case STMT_skip:
			tmp = translateSkip((Stmt.Skip) stmt, context);
			break;
		case STMT_switch:
			tmp = translateSwitch((Stmt.Switch) stmt, context);
			break;
		case STMT_while:
			tmp = translateWhile((Stmt.While) stmt, context);
			break;
		default:
			throw new IllegalArgumentException("unknown statement encountered (" + stmt.getClass().getName() + ")");
		}
		//
		if (tmp == null) {
			return Collections.EMPTY_LIST;
		} else {
			ArrayList<WyllFile.Stmt> list = new ArrayList<>();
			list.add(tmp);
			return list;
		}
	}

	public WyllFile.Stmt translateAssert(Stmt.Assert stmt, Context context) {
		if (debug) {
			WyllFile.Expr condition = translateExpression(stmt.getCondition(), Type.Bool, context);
			return new WyllFile.Stmt.Assert(condition);
		} else {
			return null;
		}
	}

	public List<WyllFile.Stmt> translateAssign(Stmt.Assign stmt, Context context) {
		if(hasMultipleExpression(stmt) || hasInterference(stmt)) {
			return translateComplexAssign(stmt, context);
		} else {
			return translateSimpleAssign(stmt, context);
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
	public List<WyllFile.Stmt> translateSimpleAssign(Stmt.Assign stmt, Context context) {
		Tuple<LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		// Temporary variable declarations needed to handle multiple return values.
		ArrayList<WyllFile.Stmt> stmts = new ArrayList<>();
		//
		for (int i = 0; i != rhs.size(); ++i) {
			Expr rv = rhs.get(i);
			Expr lv = lhs.get(i);
			WyllFile.LVal lval = (WyllFile.LVal) translateExpression(lv, null, context);
			WyllFile.Expr rval = translateExpression(rv, lv.getType(), context);
			stmts.add(new WyllFile.Stmt.Assign(lval, rval));
		}
		return stmts;
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
	public List<WyllFile.Stmt> translateComplexAssign(Stmt.Assign stmt, Context context) {
		Tuple<LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		ArrayList<WyllFile.Stmt> stmts = new ArrayList<>();
		WyllFile.Decl.Variable[] tmps = new WyllFile.Decl.Variable[rhs.size()];
		//
		for (int i = 0,j = 0; i != rhs.size(); ++i) {
			WyllFile.Decl.Variable decl;
			Expr rv = rhs.get(i);
			Tuple<Type> rhsTypes = rv.getTypes();
			if (rhsTypes == null) {
				decl = createTemporaryVariable(rv.getType(), rv, context);
			} else {
				decl = createTemporaryVariable(rhsTypes, rv, context);
			}
			stmts.add(decl);
			tmps[i] = decl;
		}

		//
		for (int i = 0,j = 0; i != rhs.size(); ++i) {
			Expr rv = rhs.get(i);
			Tuple<Type> rhsTypes = rv.getTypes();
			WyllFile.Decl.Variable decl = tmps[i];
			if (rhsTypes == null) {
				// Easy case for single assignments
				Expr lv = lhs.get(j++);
				WyllFile.LVal lval = (WyllFile.LVal) translateExpression(lv, null, context);
				WyllFile.Expr rval = new WyllFile.Expr.VariableAccess(decl.getType(),decl.getName());
				stmts.add(new WyllFile.Stmt.Assign(lval, rval));
			} else {
				// Harder case for multiple assignments. First, store return value into
				// temporary register. Then load from that. At this time, the only way to have a
				// multiple return is via some kind of invocation.
				WyllFile.Type.Record rec_t = (WyllFile.Type.Record) decl.getType();
				for (int k = 0; k != rhsTypes.size(); ++k) {
					Expr lv = lhs.get(j++);
					WyllFile.LVal lval = (WyllFile.LVal) translateExpression(lv, null, context);
					WyllFile.Expr.VariableAccess tmp = new WyllFile.Expr.VariableAccess(decl.getType(), decl.getName());
					WyllFile.Expr rval = new WyllFile.Expr.RecordAccess(rec_t, tmp, new Identifier("f" + k));
					stmts.add(new WyllFile.Stmt.Assign(lval, rval));
				}
			}
		}
		return stmts;
	}

	public WyllFile.Stmt translateAssume(Stmt.Assume stmt, Context context) {
		if (debug) {
			WyllFile.Expr operand = translateExpression(stmt.getCondition(), Type.Bool, context);
			return new WyllFile.Stmt.Assert(operand);
		} else {
			return null;
		}
	}

	public WyllFile.Stmt.Block translateBlock(Stmt.Block stmt, Context context) {
		List<WyllFile.Stmt> stmts = new ArrayList<>();
		for (int i = 0; i != stmt.size(); ++i) {
			stmts.addAll(translateStatement(stmt.get(i), context));
		}
		return new WyllFile.Stmt.Block(stmts.toArray(new WyllFile.Stmt[stmts.size()]));
	}

	public WyllFile.Stmt translateBreak(Stmt.Break stmt, Context context) {
		return new WyllFile.Stmt.Break();
	}

	public WyllFile.Stmt translateContinue(Stmt.Continue stmt, Context context) {
		return new WyllFile.Stmt.Continue();
	}

	public WyllFile.Stmt translateDebug(Stmt.Debug stmt, Context context) {
		// translateExpression(stmt.getOperand());
		return null;
	}

	public WyllFile.Stmt translateDoWhile(Stmt.DoWhile stmt, Context context) {
		WyllFile.Stmt.Block body = translateBlock(stmt.getBody(), context);
		WyllFile.Expr condition = translateExpression(stmt.getCondition(), Type.Bool, context);
		// FIXME: in debug mode should insert invariant checks
		// translateExpressions(stmt.getInvariant());
		return new WyllFile.Stmt.DoWhile(condition, body);
	}

	public WyllFile.Stmt translateFail(Stmt.Fail stmt, Context context) {
		// FIXME: should add source + line number information here
		return new WyllFile.Stmt.Fail();
	}

	public WyllFile.Stmt translateIfElse(Stmt.IfElse stmt, Context context) {
		// FIXME: need to handle arbitrary chains here
		WyllFile.Expr condition = translateExpression(stmt.getCondition(), Type.Bool, context);
		WyllFile.Stmt.Block trueBranch = translateBlock(stmt.getTrueBranch(), context);
		Tuple<Pair<WyllFile.Expr,WyllFile.Stmt.Block>> branches = new Tuple<>(new Pair<>(condition,trueBranch));
		if (stmt.hasFalseBranch()) {
			WyllFile.Stmt.Block falseBranch = translateBlock(stmt.getFalseBranch(), context);
			return new WyllFile.Stmt.IfElse(branches, falseBranch);
		} else {
			return new WyllFile.Stmt.IfElse(branches);
		}
	}

	public WyllFile.Stmt translateNamedBlock(Stmt.NamedBlock stmt, Context context) {
		translateStatement(stmt.getBlock(), context);
		return null;
	}

	public WyllFile.Stmt translateReturn(Stmt.Return stmt, Context context) {
		// FIXME: this will be broken in the case of a method call or other
		// side-effecting operation. The reason being that we may end up duplicating the
		// lhs. When the time comes, we can fix this by introducing an assignment
		// expression. This turns out to be a *very* convenient solution since all
		// target platforms support this. This would also help with the similar problem
		// of multiple returns.
		Decl.FunctionOrMethod parent = stmt.getAncestor(Decl.FunctionOrMethod.class);
		Tuple<Type> targets = parent.getType().getReturns();
		Tuple<WyllFile.Expr> returns = translateExpressions(stmt.getReturns(), targets, context);
		//
		WyllFile.Expr ret;
		if(returns.size() == 0) {
			return new WyllFile.Stmt.Return();
		} else if(returns.size() == 1) {
			ret = returns.get(0);
		} else if(returns.size() == targets.size()){
			WyllFile.Type.Record type = (WyllFile.Type.Record) translateType(createMultipleReturnType(targets));
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

	public WyllFile.Stmt translateSkip(Stmt.Skip stmt, Context context) {
		return null;
	}

	public WyllFile.Stmt translateSwitch(Stmt.Switch stmt, Context context) {
		// Attempt to translate into a low-level switch statement. This may fail if it
		// contains something which cannot be evaluated at compile time.
		Type type = stmt.getCondition().getType();
		WyllFile.Expr condition = translateExpression(stmt.getCondition(), null, context);
		Tuple<Stmt.Case> cases = stmt.getCases();
		WyllFile.Stmt.Case[] ncases = new WyllFile.Stmt.Case[cases.size()];
		for (int i = 0; i != cases.size(); ++i) {
			ncases[i] = translateCase(cases.get(i), type, context);
			if(ncases[i] == null) {
				// This indicates we were unable to translate this high-level switch into a
				// low-level switch. Therefore, instead, we will translate it as a chain of
				// if-else statements.
				return translateSwitchAsChain(stmt,context);
			}
		}
		return new WyllFile.Stmt.Switch(condition, new WyllFile.Tuple<>(ncases));
	}

	public WyllFile.Stmt.Case translateCase(Stmt.Case stmt, Type type, Context context) {
		Tuple<WyllFile.Expr> conditions = translateExpressions(stmt.getConditions(), type, context);
		Value.Int[] ints = new Value.Int[conditions.size()];
		for (int i = 0; i != ints.length; ++i) {
			WyllFile.Expr c = conditions.get(i);
			if (c instanceof WyllFile.Expr.Constant) {
				Value constant = ((WyllFile.Expr.Constant) c).getValue();
				if (constant instanceof Value.Int) {
					ints[i] = (Value.Int) constant;
				} else {
					// FAILED
					return null;
				}
			} else {
				// FAILED
				return null;
			}
		}
		WyllFile.Stmt.Block block = translateBlock(stmt.getBlock(), context);
		return new WyllFile.Stmt.Case(new Tuple<>(ints), block);
	}

	public WyllFile.Stmt translateSwitchAsChain(Stmt.Switch stmt, Context context) {
		Type type = stmt.getCondition().getType();
		WyllFile.Expr condition = translateExpression(stmt.getCondition(), null, context);
		Tuple<Stmt.Case> cases = stmt.getCases();
		Pair<WyllFile.Expr, WyllFile.Stmt.Block>[] branches = new Pair[cases.size()];
		WyllFile.Stmt.Block defaultBranch = null;
		int j = 0;
		for (int i = 0; i != cases.size(); ++i) {
			Stmt.Case cAse = cases.get(i);
			WyllFile.Stmt.Block block = translateBlock(cAse.getBlock(), context);
			Tuple<WyllFile.Expr> conditions = translateExpressions(cAse.getConditions(), type, context);
			//
			if (conditions.size() == 0) {
				// Default case
				defaultBranch = block;
				branches = Arrays.copyOf(branches, branches.length - 1);
			} else if (conditions.size() != 1) {
				throw new IllegalArgumentException("implement me!");
			} else {
				branches[j++] = new Pair<>(new WyllFile.Expr.Equal(condition, conditions.get(0)), block);
			}
		}
		//
		if (defaultBranch != null) {
			return new WyllFile.Stmt.IfElse(new Tuple<>(branches), defaultBranch);
		} else {
			return new WyllFile.Stmt.IfElse(new Tuple<>(branches));
		}
	}

	public WyllFile.Stmt translateWhile(Stmt.While stmt, Context context) {
		WyllFile.Expr condition = translateExpression(stmt.getCondition(), Type.Bool, context);
		// FIXME: runtime assertion checking
		Tuple<WyllFile.Expr> invariant = translateExpressions(stmt.getInvariant(), Type.Bool, context);
		WyllFile.Stmt.Block body = translateBlock(stmt.getBody(), context);
		return new WyllFile.Stmt.While(condition, body);
	}

	// ==========================================================================
	// Expressions
	// ==========================================================================

	public Tuple<WyllFile.Expr> translateExpressions(Tuple<Expr> exprs, Type target, Context context) {
		WyllFile.Expr[] nexprs = new WyllFile.Expr[exprs.size()];
		for (int i = 0; i != exprs.size(); ++i) {
			nexprs[i] = translateExpression(exprs.get(i), target, context);
		}
		return new WyllFile.Tuple<>(nexprs);
	}

	public Tuple<WyllFile.Expr> translateExpressions(Tuple<Expr> exprs, Tuple<Type> targets, Context context) {
		WyllFile.Expr[] nexprs = new WyllFile.Expr[exprs.size()];
		for (int i = 0; i != exprs.size(); ++i) {
			nexprs[i] = translateExpression(exprs.get(i), targets.get(i), context);
		}
		return new WyllFile.Tuple<>(nexprs);
	}

	public WyllFile.Expr translateMultiExpression(Expr expr, Tuple<Type> targets, Context context) {
		WyllFile.Expr result;
		switch (expr.getOpcode()) {
		case EXPR_indirectinvoke:
			result = translateIndirectInvoke((Expr.IndirectInvoke) expr, null, context);
			break;
		case EXPR_invoke:
			result = translateInvoke((Expr.Invoke) expr, null, context);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
		// FIXME: what to do about coercions?
		return result;
	}

	public WyllFile.Expr translateExpression(Expr expr, Type target, Context context) {
		WyllFile.Expr result;
		Type actual = expr.getType();
		// NOTE: target is allowed to be null in the case that no constraint is place on
		// the target type.
		if(target == null) { target = actual; }

		switch (expr.getOpcode()) {
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			result = translateArrayAccess((Expr.ArrayAccess) expr, target, context);
			break;
		case EXPR_arraylength:
			result = translateArrayLength((Expr.ArrayLength) expr, Type.Int, context);
			break;
		case EXPR_arraygenerator:
			result = translateArrayGenerator((Expr.ArrayGenerator) expr, extractArrayType(actual,target), context);
			break;
		case EXPR_arrayinitialiser:
			result = translateArrayInitialiser((Expr.ArrayInitialiser) expr, extractArrayType(actual,target), context);
			break;
		case EXPR_arrayupdate:
			result = translateArrayUpdate((Expr.ArrayUpdate) expr, extractArrayType(actual,target), context);
			break;
		case EXPR_bitwiseand:
			result = translateBitwiseAnd((Expr.BitwiseAnd) expr, Type.Byte, context);
			break;
		case EXPR_bitwiseor:
			result = translateBitwiseOr((Expr.BitwiseOr) expr, Type.Byte, context);
			break;
		case EXPR_bitwisexor:
			result = translateBitwiseXor((Expr.BitwiseXor) expr, Type.Byte, context);
			break;
		case EXPR_bitwisenot:
			result = translateBitwiseComplement((Expr.BitwiseComplement) expr, Type.Byte, context);
			break;
		case EXPR_bitwiseshl:
			result = translateBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr, Type.Byte, context);
			break;
		case EXPR_bitwiseshr:
			result = translateBitwiseShiftRight((Expr.BitwiseShiftRight) expr, Type.Byte, context);
			break;
		case EXPR_cast:
			result = translateCast((Expr.Cast) expr, target, context);
			break;
		case EXPR_constant:
			result = translateConstant((Expr.Constant) expr, target, context);
			break;
		case EXPR_dereference:
			result = translateDereference((Expr.Dereference) expr, target, context);
			break;
		case EXPR_equal:
			result = translateEqual((Expr.Equal) expr, Type.Bool, context);
			break;
		case EXPR_integerlessthan:
			result = translateIntegerLessThan((Expr.IntegerLessThan) expr, Type.Bool, context);
			break;
		case EXPR_integerlessequal:
			result = translateIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr, Type.Bool, context);
			break;
		case EXPR_integergreaterthan:
			result = translateIntegerGreaterThan((Expr.IntegerGreaterThan) expr, Type.Bool, context);
			break;
		case EXPR_integergreaterequal:
			result = translateIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr, Type.Bool, context);
			break;
		case EXPR_integernegation:
			result = translateIntegerNegation((Expr.IntegerNegation) expr, Type.Int, context);
			break;
		case EXPR_integeraddition:
			result = translateIntegerAddition((Expr.IntegerAddition) expr, Type.Int, context);
			break;
		case EXPR_integersubtraction:
			result = translateIntegerSubtraction((Expr.IntegerSubtraction) expr, Type.Int, context);
			break;
		case EXPR_integermultiplication:
			result = translateIntegerMultiplication((Expr.IntegerMultiplication) expr, Type.Int, context);
			break;
		case EXPR_integerdivision:
			result = translateIntegerDivision((Expr.IntegerDivision) expr, Type.Int, context);
			break;
		case EXPR_integerremainder:
			result = translateIntegerRemainder((Expr.IntegerRemainder) expr, Type.Int, context);
			break;
		case EXPR_indirectinvoke:
			result = translateIndirectInvoke((Expr.IndirectInvoke) expr, target, context);
			break;
		case EXPR_invoke:
			result = translateInvoke((Expr.Invoke) expr, target, context);
			break;
		case EXPR_is:
			return translateIs((Expr.Is) expr, Type.Bool, context);
		case EXPR_lambdaaccess:
			result = translateLambdaAccess((Expr.LambdaAccess) expr, target, context);
			break;
		case DECL_lambda:
			result = translateLambda((Decl.Lambda) expr, target, context);
			break;
		case EXPR_logicaland:
			result = translateLogicalAnd((Expr.LogicalAnd) expr, Type.Bool, context);
			break;
		case EXPR_logicalor:
			result = translateLogicalOr((Expr.LogicalOr) expr, Type.Bool, context);
			break;
		case EXPR_logicalnot:
			result = translateLogicalNot((Expr.LogicalNot) expr, Type.Bool, context);
			break;
		case EXPR_logiaclimplication:
			result = translateLogicalImplication((Expr.LogicalImplication) expr, Type.Bool, context);
			break;
		case EXPR_logicaliff:
			result = translateLogicalIff((Expr.LogicalIff) expr, Type.Bool, context);
			break;
		case EXPR_logicalexistential:
		case EXPR_logicaluniversal:
			result = translateQuantifier((Expr.Quantifier) expr, Type.Bool, context);
			break;
		case EXPR_new:
		case EXPR_staticnew:
			result = translateNew((Expr.New) expr, extractReferenceType(target), context);
			break;
		case EXPR_notequal:
			result = translateNotEqual((Expr.NotEqual) expr, Type.Bool, context);
			break;
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			result = translateRecordAccess((Expr.RecordAccess) expr, target, context);
			break;
		case EXPR_recordinitialiser:
			result = translateRecordInitialiser((Expr.RecordInitialiser) expr, extractRecordType(actual,target), context);
			break;
		case EXPR_recordupdate:
			result = translateRecordUpdate((Expr.RecordUpdate) expr, extractRecordType(actual,target), context);
			break;
		case EXPR_staticvariable:
			result = translateStaticVariableAccess((Expr.StaticVariableAccess) expr, target, context);
			break;
		case EXPR_variablecopy:
		case EXPR_variablemove: {
			Expr.VariableAccess va = (Expr.VariableAccess) expr;
			result = translateVariableAccess(va, target, context);
			// NOTE: the following does feel like something of a hack. The essential problem
			// is that the stored type for the variable access may differ from that of its
			// declaring variable. In such case, we must insert a coercion.
			actual = va.getVariableDeclaration().getType();
			break;
		}
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
		// Add coercions here if necessary
		try {
			actual = typeSystem.extractRepresentationType(actual, null);
			target = typeSystem.extractRepresentationType(target, null);
			return addCoercion(result, actual, target, context);
		} catch (NameResolver.ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	public WyllFile.Expr translateArrayAccess(Expr.ArrayAccess expr, Type target, Context context) {
		WyllFile.Type.Array type = (WyllFile.Type.Array) translateType(expr.getFirstOperand().getType());
		WyllFile.Expr src = translateExpression(expr.getFirstOperand(), null, context);
		// FIXME: this should be a USIZE
		WyllFile.Expr idx = translateExpression(expr.getSecondOperand(), Type.Int, context);
		WyllFile.Expr nexpr = new WyllFile.Expr.ArrayAccess(type, src, idx);
		if (!expr.isMove()) {
			nexpr = new WyllFile.Expr.Clone(type, nexpr);
		}
		return nexpr;
	}

	public WyllFile.Expr translateArrayLength(Expr.ArrayLength expr, Type.Int target, Context context) {
		WyllFile.Type.Array type = (WyllFile.Type.Array) translateType(expr.getOperand().getType());
		WyllFile.Expr src = translateExpression(expr.getOperand(), null, context);
		return new WyllFile.Expr.ArrayLength(type, src);
	}

	public WyllFile.Expr translateArrayGenerator(Expr.ArrayGenerator expr, Type.Array target, Context context) {
		WyllFile.Type.Array type = (WyllFile.Type.Array) translateType(expr.getType());
		WyllFile.Expr value = translateExpression(expr.getFirstOperand(), null, context);
		// FIXME: should be usize
		WyllFile.Expr length = translateExpression(expr.getSecondOperand(), Type.Int, context);
		return new WyllFile.Expr.ArrayGenerator(type, value, length);
	}

	public WyllFile.Expr translateArrayInitialiser(Expr.ArrayInitialiser expr, Type.Array target, Context context) {
		WyllFile.Type.Array type = (WyllFile.Type.Array) translateType(expr.getType());
		WyllFile.Tuple<WyllFile.Expr> exprs = translateExpressions(expr.getOperands(), target.getElement(), context);
		return new WyllFile.Expr.ArrayInitialiser(type, exprs);
	}

	public WyllFile.Expr translateArrayUpdate(Expr.ArrayUpdate expr, Type.Array target, Context context) {
		throw new UnsupportedOperationException();
	}

	public WyllFile.Expr translateBitwiseComplement(Expr.BitwiseComplement expr, Type.Byte target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr operand = translateExpression(expr.getOperand(), target, context);
		return new WyllFile.Expr.BitwiseComplement(type, operand);
	}

	public WyllFile.Expr translateBitwiseAnd(Expr.BitwiseAnd expr, Type.Byte target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		Tuple<WyllFile.Expr> operands = translateExpressions(expr.getOperands(), target, context);
		return new WyllFile.Expr.BitwiseAnd(type, operands);
	}

	public WyllFile.Expr translateBitwiseOr(Expr.BitwiseOr expr, Type.Byte target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		Tuple<WyllFile.Expr> operands = translateExpressions(expr.getOperands(), target, context);
		return new WyllFile.Expr.BitwiseOr(type, operands);
	}

	public WyllFile.Expr translateBitwiseXor(Expr.BitwiseXor expr, Type.Byte target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		Tuple<WyllFile.Expr> operands = translateExpressions(expr.getOperands(), target, context);
		return new WyllFile.Expr.BitwiseXor(type, operands);
	}

	public WyllFile.Expr translateBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, Type.Byte target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), target, context);
		// FIXME: should be uint
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), Type.Int, context);
		return new WyllFile.Expr.BitwiseShiftLeft(type, lhs, rhs);
	}

	public WyllFile.Expr translateBitwiseShiftRight(Expr.BitwiseShiftRight expr, Type.Byte target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), target, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), Type.Int, context);
		return new WyllFile.Expr.BitwiseShiftRight(type, lhs, rhs);
	}

	public WyllFile.Expr translateCast(Expr.Cast expr, Type target, Context context) {
		WyllFile.Expr rhs = translateExpression(expr.getOperand(), null, context);
		return addCoercion(rhs, expr.getType(), target, context);
	}

	public WyllFile.Expr translateConstant(Expr.Constant expr, Type target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		return new WyllFile.Expr.Constant(type, expr.getValue());
	}

	public WyllFile.Expr translateDereference(Expr.Dereference expr, Type target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr e = translateExpression(expr.getOperand(), null, context);
		return new WyllFile.Expr.Dereference(type, e);
	}

	public WyllFile.Expr translateEqual(Expr.Equal expr, Type.Bool target, Context context) {
		// FIXME: need to determine target type
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), null, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), null, context);
		return new WyllFile.Expr.Equal(lhs, rhs);
	}

	public WyllFile.Expr translateIntegerLessThan(Expr.IntegerLessThan expr, Type.Bool target, Context context) {
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), Type.Int, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), Type.Int, context);
		return new WyllFile.Expr.IntegerLessThan(lhs, rhs);
	}

	public WyllFile.Expr translateIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, Type.Bool target, Context context) {
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), Type.Int, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), Type.Int, context);
		return new WyllFile.Expr.IntegerLessThanOrEqual(lhs, rhs);
	}

	public WyllFile.Expr translateIntegerGreaterThan(Expr.IntegerGreaterThan expr, Type.Bool target, Context context) {
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), Type.Int, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), Type.Int, context);
		return new WyllFile.Expr.IntegerGreaterThan(lhs, rhs);
	}

	public WyllFile.Expr translateIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, Type.Bool target, Context context) {
		// FIXME: need to determine target type
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), Type.Int, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), Type.Int, context);
		return new WyllFile.Expr.IntegerGreaterThanOrEqual(lhs, rhs);
	}

	public WyllFile.Expr translateIntegerNegation(Expr.IntegerNegation expr, Type.Int target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr operand = translateExpression(expr.getOperand(), target, context);
		return new WyllFile.Expr.IntegerNegation(type, operand);
	}

	public WyllFile.Expr translateIntegerAddition(Expr.IntegerAddition expr, Type.Int target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), target, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), target, context);
		return new WyllFile.Expr.IntegerAddition(type, lhs, rhs);
	}

	public WyllFile.Expr translateIntegerSubtraction(Expr.IntegerSubtraction expr, Type.Int target, Context context) {

		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), target, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), target, context);
		return new WyllFile.Expr.IntegerSubtraction(type, lhs, rhs);
	}

	public WyllFile.Expr translateIntegerMultiplication(Expr.IntegerMultiplication expr, Type.Int target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), target, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), target, context);
		return new WyllFile.Expr.IntegerMultiplication(type, lhs, rhs);
	}

	public WyllFile.Expr translateIntegerDivision(Expr.IntegerDivision expr, Type.Int target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), target, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), target, context);
		return new WyllFile.Expr.IntegerDivision(type, lhs, rhs);
	}

	public WyllFile.Expr translateIntegerRemainder(Expr.IntegerRemainder expr, Type.Int target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), target, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), target, context);
		return new WyllFile.Expr.IntegerRemainder(type, lhs, rhs);
	}

	public WyllFile.Expr translateIs(Expr.Is expr, Type.Bool target, Context context) {
		WyllFile.Expr lhs = translateExpression(expr.getOperand(), null, context);
		// FIXME: this is broken in the case of a method call or other side-effecting
		// operation. The reason being that we may end up duplicating the lhs. When the
		// time comes, we can fix this by introducing an assignment expression. This
		// turns out to be a *very* convenient solution since all target platforms
		// support this. This would also help with the similar problem of multiple
		// returns.
		return constructRuntimeTypeTest(lhs,expr.getOperand().getType(),expr.getTestType(),context);
	}

	public WyllFile.Expr constructRuntimeTypeTest(WyllFile.Expr expr, Type actual, Type test, Context context) {
		if(actual instanceof Type.Nominal) {
			return constructRuntimeTypeTest(expr, (Type.Nominal) actual, test, context);
		} else if(test instanceof Type.Nominal) {
			return constructRuntimeTypeTest(expr, actual, (Type.Nominal) test, context);
		}
		switch(actual.getOpcode()) {
		case TYPE_int:
			return constructFiniteRuntimeTypeTest(expr, (Type.Int) actual, test, context);
		case TYPE_union:
			return constructFiniteRuntimeTypeTest(expr, (Type.Union) actual, test, context);
		default:
			throw new RuntimeException("need to work harder on type tests (" + actual + " is " + test + ")");
		}
	}

	public WyllFile.Expr constructRuntimeTypeTest(WyllFile.Expr expr, Type.Nominal actual, Type test,
			Context context) {
		try {
			WhileyFile.Decl.Type decl = typeSystem.resolveExactly(actual.getName(), WhileyFile.Decl.Type.class);
			return constructRuntimeTypeTest(expr,decl.getType(),test,context);
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	public WyllFile.Expr constructRuntimeTypeTest(WyllFile.Expr expr, Type actual, Type.Nominal test,
			Context context) {
		try {
			WhileyFile.Decl.Type decl = typeSystem.resolveExactly(test.getName(), WhileyFile.Decl.Type.class);
			return constructRuntimeTypeTest(expr,actual,decl.getType(),context);
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	public WyllFile.Expr constructFiniteRuntimeTypeTest(WyllFile.Expr expr, Type.Int actual, Type test,
			Context context) {
		// FIXME: need to actually do a runtime test here
		return new WyllFile.Expr.Constant(WyllFile.Type.Bool, new WyllFile.Value.Bool(true));
	}

	public WyllFile.Expr constructFiniteRuntimeTypeTest(WyllFile.Expr expr, Type.Union actual, Type test,
			Context context) {
		int tag = determineTag(actual, test);
		Type refined = actual.get(tag);
		expr = new WyllFile.Expr.UnionAccess(WyllFile.Type.Int, expr);
		expr = new WyllFile.Expr.Equal(expr, new WyllFile.Expr.Constant(WyllFile.Type.Int, new Value.Int(tag)));
		// FIXME: type invariants
		if(!refined.equals(test)) {
			// FIXME: there maybe other situations where actual is equivalent to test, or
			// perhaps smaller than test?
			WyllFile.Expr rest = constructRuntimeTypeTest(addCoercion(expr, actual, refined, context), refined, test, context);
			expr = new WyllFile.Expr.LogicalAnd(new Tuple<>(expr,rest));
		}
		return expr;
	}

	public WyllFile.Expr translateLogicalAnd(Expr.LogicalAnd expr, Type.Bool target, Context context) {
		Tuple<WyllFile.Expr> operands = translateExpressions(expr.getOperands(), target, context);
		return new WyllFile.Expr.LogicalAnd(operands);
	}

	public WyllFile.Expr translateLogicalImplication(Expr.LogicalImplication expr, Type.Bool target, Context context) {
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), target, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), target, context);
		WyllFile.Tuple<WyllFile.Expr> operands = new WyllFile.Tuple<>(new WyllFile.Expr.LogicalNot(lhs), rhs);
		return new WyllFile.Expr.LogicalOr(operands);
	}

	public WyllFile.Expr translateLogicalIff(Expr.LogicalIff expr, Type.Bool target, Context context) {
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), target, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), target, context);
		return new WyllFile.Expr.Equal(lhs, rhs);
	}

	public WyllFile.Expr translateLogicalNot(Expr.LogicalNot expr, Type.Bool target, Context context) {
		WyllFile.Expr operand = translateExpression(expr.getOperand(), target, context);
		return new WyllFile.Expr.LogicalNot(operand);
	}

	public WyllFile.Expr translateLogicalOr(Expr.LogicalOr expr, Type.Bool target, Context context) {
		Tuple<WyllFile.Expr> operands = translateExpressions(expr.getOperands(), target, context);
		return new WyllFile.Expr.LogicalOr(operands);
	}

	public WyllFile.Expr translateQuantifier(Expr.Quantifier expr, Type.Bool target, Context context) {
		// The low-level language has no notion of a quantifier expression. Therefore,
		// we construct an internal method which contains the necessary loop(s) to
		// evaluate the quantifier, and return an invocation to this. A key part of the
		// challenge is to determine what variables are used in the quantifier condition
		// aside from its own parameters.
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		String kind = (expr instanceof Expr.UniversalQuantifier) ? "all" : "some";
		Identifier name = new Identifier(kind + "$" + expr.getIndex());
		Set<WhileyFile.Decl.Variable> uses = determineUsedVariables(expr);
		Tuple<WyllFile.Decl.Variable> parameters = constructQuantifierParameters(uses);
		Tuple<WyllFile.Decl.Variable> returns = constructQuantifierReturns(uses);
		WyllFile.Expr condition = translateExpression(expr.getOperand(), target, context);
		// Create the sequence of nested quantifier for loops
		WyllFile.Stmt loops = constructQuantifierBody(expr, 0, condition, context);
		// Determine whether getting through the loops should return true or false
		WyllFile.Value retbool = new WyllFile.Value.Bool(expr instanceof Expr.UniversalQuantifier);
		WyllFile.Expr retval = new WyllFile.Expr.Constant(new WyllFile.Type.Bool(), retbool);
		WyllFile.Stmt.Return retstmt = new WyllFile.Stmt.Return(retval);
		// Create the method body from the loops and the additional return statement
		WyllFile.Stmt.Block body = new WyllFile.Stmt.Block(loops, retstmt);
		WyllFile.Decl.Method method = new WyllFile.Decl.Method(modifiers, name, parameters, returns, body);
		// Declare the new method in the surrounding context
		context.declare(method);
		// Create an invocation to the block
		return new WyllFile.Expr.Invoke(new Name(name), constructQuantifierArguments(uses),
				constructQuantifierType(parameters, returns));
	}

	public Tuple<WyllFile.Decl.Variable> constructQuantifierParameters(Set<WhileyFile.Decl.Variable> uses) {
		WyllFile.Decl.Variable[] parameters = new WyllFile.Decl.Variable[uses.size()];
		int i = 0;
		for (WhileyFile.Decl.Variable use : uses) {
			Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
			WyllFile.Type type = translateType(use.getType());
			parameters[i++] = new WyllFile.Decl.Variable(modifiers, use.getName(), type);
		}
		return new Tuple<>(parameters);
	}

	public Tuple<WyllFile.Decl.Variable> constructQuantifierReturns(Set<WhileyFile.Decl.Variable> uses) {
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		return new Tuple<>(new WyllFile.Decl.Variable(modifiers, new Identifier("$"), WyllFile.Type.Bool));
	}

	public Tuple<WyllFile.Expr> constructQuantifierArguments(Set<WhileyFile.Decl.Variable> uses) {
		WyllFile.Expr[] arguments = new WyllFile.Expr[uses.size()];
		int i = 0;
		for (WhileyFile.Decl.Variable use : uses) {
			WyllFile.Type type = translateType(use.getType());
			arguments[i++] = new WyllFile.Expr.VariableAccess(type, use.getName());
		}
		return new Tuple<>(arguments);
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

	public WyllFile.Type.Method constructQuantifierType(Tuple<WyllFile.Decl.Variable> parameters,
			Tuple<WyllFile.Decl.Variable> returns) {
		Tuple<WyllFile.Type> paramTypes = parameters.project(2, WyllFile.Type.class);
		Tuple<WyllFile.Type> returnTypes = returns.project(2, WyllFile.Type.class);
		return new WyllFile.Type.Method(paramTypes, returnTypes);
	}

	public WyllFile.Stmt constructQuantifierBody(Expr.Quantifier expr, int index, WyllFile.Expr condition,
			Context context) {
		Tuple<Decl.Variable> parameters = expr.getParameters();
		if (index == parameters.size()) {
			// This indicates we are now within the innermost loop body. Therefore, we
			// create the necessary test for the quantifier condition.
			WyllFile.Value retbool = new WyllFile.Value.Bool(expr instanceof Expr.ExistentialQuantifier);
			WyllFile.Expr retval = new WyllFile.Expr.Constant(new WyllFile.Type.Bool(), retbool);
			WyllFile.Stmt.Return retstmt = new WyllFile.Stmt.Return(retval);
			WyllFile.Stmt.Block trueblock = new WyllFile.Stmt.Block(retstmt);
			return new WyllFile.Stmt.IfElse(new Tuple<>(new Pair<>(condition,trueblock)));
		} else {
			// This is the recursive case. For each parameter we create a nested foreach
			// loop which iterates over the given range
			Decl.Variable parameter = parameters.get(index);
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			Tuple<WyllFile.Modifier> modifiers = new WyllFile.Tuple<>();
			WyllFile.Decl.Variable var = new WyllFile.Decl.Variable(modifiers, parameter.getName(),
					translateType(parameter.getType()));
			// FIXME: should be usize
			WyllFile.Expr start = translateExpression(range.getFirstOperand(), Type.Int, context);
			WyllFile.Expr end = translateExpression(range.getSecondOperand(), Type.Int, context);
			// Recursively create nested loops for remaining parameters
			WyllFile.Stmt body = constructQuantifierBody(expr, index + 1, condition, context);
			// Return the loop for this parameter
			return new WyllFile.Stmt.ForEach(var, start, end, new WyllFile.Stmt.Block(body));
		}
	}

	public WyllFile.Expr translateInvoke(Expr.Invoke expr, Type target, Context context) {
		Name name = getNameMangle(expr.getName(), expr.getSignature());
		WyllFile.Type.Method type = translateCallable(expr.getSignature());
		Tuple<WyllFile.Expr> arguments = translateExpressions(expr.getOperands(), expr.getSignature().getParameters(),
				context);
		return new WyllFile.Expr.Invoke(name, arguments, type);
	}

	public WyllFile.Expr translateIndirectInvoke(Expr.IndirectInvoke expr, Type target, Context context) {
		Type.Callable signature = extractLambdaType(expr.getSource().getType());
		WyllFile.Type type = translateType(signature);
		WyllFile.Expr source = translateExpression(expr.getSource(), null, context);
		Tuple<WyllFile.Expr> arguments = translateExpressions(expr.getArguments(), signature.getParameters(), context);
		return new WyllFile.Expr.IndirectInvoke(type, source, arguments);
	}

	public WyllFile.Expr translateLambdaAccess(Expr.LambdaAccess expr, Type target, Context context) {
		Name name = getNameMangle(expr.getName(), expr.getSignature());
		WyllFile.Type.Method type = translateCallable(expr.getSignature());
		return new WyllFile.Expr.LambdaAccess(name, type);
	}

	public WyllFile.Expr translateNew(Expr.New expr, Type.Reference target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr operand = translateExpression(expr.getOperand(), target.getElement(), context);
		return new WyllFile.Expr.New(type, operand);
	}

	public WyllFile.Expr translateNotEqual(Expr.NotEqual expr, Type.Bool target, Context context) {
		// FIXME: determine target types
		WyllFile.Expr lhs = translateExpression(expr.getFirstOperand(), null, context);
		WyllFile.Expr rhs = translateExpression(expr.getSecondOperand(), null, context);
		return new WyllFile.Expr.NotEqual(lhs, rhs);
	}

	public WyllFile.Expr translateRecordAccess(Expr.RecordAccess expr, Type target, Context context) {
		// FIXME: clearly broken
		WyllFile.Type.Record type = (WyllFile.Type.Record) translateType(expr.getOperand().getType());
		WyllFile.Expr src = translateExpression(expr.getOperand(), null, context);
		WyllFile.Expr nexpr = new WyllFile.Expr.RecordAccess(type, src, expr.getField());
		if (!expr.isMove()) {
			nexpr = new WyllFile.Expr.Clone(type, nexpr);
		}
		return nexpr;
	}

	public WyllFile.Expr translateRecordInitialiser(Expr.RecordInitialiser expr, Type.Record target, Context context) {
		// FIXME: clearly broken
		WyllFile.Type.Record type = (WyllFile.Type.Record) translateType(expr.getType());
		Tuple<Type> fieldTypes = target.getFields().project(2, Type.class);
		Tuple<WyllFile.Expr> operands = translateExpressions(expr.getOperands(), fieldTypes, context);
		return new WyllFile.Expr.RecordInitialiser(type, expr.getFields(), operands);
	}

	public WyllFile.Expr translateRecordUpdate(Expr.RecordUpdate expr, Type.Record target, Context context) {
		throw new UnsupportedOperationException("Implement Record Updates");
	}

	public WyllFile.Expr translateStaticVariableAccess(Expr.StaticVariableAccess expr, Type target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		return new WyllFile.Expr.StaticVariableAccess(type, expr.getName());
	}

	public WyllFile.Expr translateVariableAccess(Expr.VariableAccess expr, Type target, Context context) {
		WyllFile.Type type = translateType(expr.getType());
		WyllFile.Expr nexpr = new WyllFile.Expr.VariableAccess(type, expr.getVariableDeclaration().getName());
		if (!expr.isMove()) {
			nexpr = new WyllFile.Expr.Clone(type, nexpr);
		}
		return nexpr;
	}

	// ==========================================================================
	// Types
	// ==========================================================================

	public Tuple<WyllFile.Type> translateSimpleTypes(Tuple<Type> types) {
		WyllFile.Type[] ntypes = new WyllFile.Type[types.size()];
		for (int i = 0; i != types.size(); ++i) {
			ntypes[i] = translateSimpleType(types.get(i));
		}
		return new Tuple<>(ntypes);
	}

	public WyllFile.Type translateType(Type type) {
		// FIXME: use of representation type would be helpful here.
		try {
			Type simplified = typeSystem.extractRepresentationType(type, null);
			if(simplified == null) {
				// FIXME: serious hack!!
				simplified = type;
			}
			return translateSimpleType(simplified);
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}

	}

	public WyllFile.Type translateSimpleType(Type type) {
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
		case TYPE_intersection:
		case TYPE_negation:
			throw new IllegalArgumentException("invalid simple type encountered (" + type.getClass().getName() + ")");
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
		WyllFile.Type element = translateSimpleType(type.getElement());
		return new WyllFile.Type.Array(element);
	}

	public WyllFile.Type.Bool translateBool(Type.Bool type) {
		return new WyllFile.Type.Bool();
	}

	public WyllFile.Type translateByte(Type.Byte type) {
		return new WyllFile.Type.Int();
	}

	public WyllFile.Type.Method translateFunction(Type.Function type) {
		Tuple<WyllFile.Type> parameters = translateSimpleTypes(type.getParameters());
		Tuple<WyllFile.Type> returns = translateSimpleTypes(type.getReturns());
		return new WyllFile.Type.Method(parameters, returns);
	}

	public WyllFile.Type translateInt(Type.Int type) {
		return new WyllFile.Type.Int();
	}

	public WyllFile.Type.Method translateMethod(Type.Method type) {
		Tuple<WyllFile.Type> parameters = translateSimpleTypes(type.getParameters());
		Tuple<WyllFile.Type> returns = translateSimpleTypes(type.getReturns());
		return new WyllFile.Type.Method(parameters, returns);
	}

	public WyllFile.Type translateNominal(Type.Nominal type) {
		try {
			WhileyFile.Decl.Type decl = typeSystem.resolveExactly(type.getName(), WhileyFile.Decl.Type.class);
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
		Tuple<WyllFile.Type> parameters = translateSimpleTypes(type.getParameters());
		Tuple<WyllFile.Type> returns = translateSimpleTypes(type.getReturns());
		return new WyllFile.Type.Method(parameters, returns);
	}

	public WyllFile.Type.Record translateRecord(Type.Record type) {
		Tuple<WhileyFile.Decl.Variable> fields = type.getFields();
		WyllFile.Decl.Variable[] returns = new WyllFile.Decl.Variable[fields.size()];
		for (int i = 0; i != fields.size(); ++i) {
			Decl.Variable decl = fields.get(i);
			Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
			WyllFile.Type declType = translateSimpleType(decl.getType());
			returns[i] = new WyllFile.Decl.Variable(modifiers, decl.getName(), declType);
		}
		return new WyllFile.Type.Record(type.isOpen(), new Tuple<>(returns));
	}

	public WyllFile.Type translateReference(Type.Reference type) {
		WyllFile.Type element = translateSimpleType(type.getElement());
		return new WyllFile.Type.Reference(element);
	}

	public WyllFile.Type translateUnion(Type.Union type) {
		WyllFile.Type[] types = new WyllFile.Type[type.size()];
		for (int i = 0; i != type.size(); ++i) {
			types[i] = translateSimpleType(type.get(i));
		}
		return new WyllFile.Type.Union(types);
	}

	public WyllFile.Type translateVoid(Type.Void type) {
		return new WyllFile.Type.Void();
	}

	public Type.Array extractArrayType(Type actual, Type target) {
		// FIXME: what are we actually doing here???
		try {
			// NOTE: need to intersect actual and target since actual may not be an array.
			target = new Type.Intersection(actual, target);
			Type.Array got = typeSystem.extractReadableArray(target, null);
			return got;
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	public Type.Record extractRecordType(Type actual, Type target) {
		// FIXME: what are we actually doing here???
		try {
			// NOTE: need to intersect actual and target since actual may not be a record.
			target = new Type.Intersection(actual, target);
			return typeSystem.extractReadableRecord(target, null);
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	public Type.Reference extractReferenceType(Type target) {
		// FIXME: what are we actually doing here???
		try {
			return typeSystem.extractReadableReference(target, null);
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	public Type.Callable extractLambdaType(Type target) {
		// FIXME: what are we actually doing here???
		try {
			return typeSystem.extractReadableLambda(target, null);
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	// ==========================================================================
	// Coercions
	// ==========================================================================

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
		//
		if (target == null || equivalent(actual, target)) {
			// no coercion required
			return expr;
		} else if (actual.getOpcode() == target.getOpcode()) {
			switch (actual.getOpcode()) {
			case TYPE_int:
				return addIntCoercion(expr, (Type.Int) actual, (Type.Int) target, context);
			case TYPE_array:
				return addArrayCoercion(expr, (Type.Array) actual, (Type.Array) target, context);
			case TYPE_record:
				return addRecordCoercion(expr, (Type.Record) actual, (Type.Record) target, context);
			case TYPE_reference:
				return addReferenceCoercion(expr, (Type.Reference) actual, (Type.Reference) target, context);
			case TYPE_union:
				return addUnionCoercion(expr, (Type.Union) actual, (Type.Union) target, context);
			}
		} else if(target instanceof Type.Nominal)  {
			return addNominalCoercion(expr, actual,  (Type.Nominal) target, context);
		} else if(actual instanceof Type.Nominal)  {
			return addNominalCoercion(expr, (Type.Nominal) actual, target, context);
		} else if (actual instanceof Type.Union) {
			return addUnionCoercion(expr, (Type.Union) actual, target, context);
		} else if (target instanceof Type.Union) {
			return addUnionCoercion(expr, actual, (Type.Union) target, context);
		} else {
			throw new IllegalArgumentException("unknown coercion: " + actual + " => " + target);
		}
		//
		return expr;
	}

	public boolean equivalent(Type actual, Type target) {
		return actual.equals(target) || (isSubtype(actual, target) && isSubtype(target, actual));
	}

	public WyllFile.Expr addIntCoercion(WyllFile.Expr expr, Type.Int _actual, Type.Int _target, Context context) {
		WyllFile.Type.Int actual = WyllFile.Type.Int;
		WyllFile.Type.Int target = WyllFile.Type.Int;
		return new WyllFile.Expr.IntegerCoercion(target, actual, expr);
	}

	private static int coercionCounter = 0;

	public WyllFile.Expr addArrayCoercion(WyllFile.Expr expr, Type.Array actual, Type.Array target, Context context) {
		if (actual.getElement() instanceof Type.Void) {
			// NOTE: this is a very special case to handle empty array initialisers.
			// FIXME: need to ensure intialiser is typed
			return expr;
		} else {
			Identifier var = new Identifier("xs");
			Tuple<WyllFile.Modifier> modifiers = new WyllFile.Tuple<>();
			WyllFile.Type parameterType = translateType(actual);
			WyllFile.Type returnType = translateType(target);
			Tuple<WyllFile.Decl.Variable> parameters = new Tuple<>(
					new WyllFile.Decl.Variable(modifiers, var, parameterType));
			Tuple<WyllFile.Decl.Variable> returns = new Tuple<>(new WyllFile.Decl.Variable(modifiers, var, returnType));
			Tuple<WyllFile.Expr> arguments = new Tuple<>(expr);
			Identifier name = new Identifier("coerce$" + coercionCounter++);
			WyllFile.Stmt.Block body = createArrayCoercionBody(actual, target, context);
			WyllFile.Decl.Method method = new WyllFile.Decl.Method(modifiers, name, parameters, returns, body);
			// Declare the new method in the surrounding context
			context.declare(method);
			// Create an invocation to the block
			return new WyllFile.Expr.Invoke(new Name(name), arguments,
					new WyllFile.Type.Method(new WyllFile.Tuple<>(parameterType), new WyllFile.Tuple<>(returnType)));
		}
	}

	public WyllFile.Stmt.Block createArrayCoercionBody(Type.Array actual, Type.Array target, Context context) {
		WyllFile.Type.Array param_t = translateArray(actual);
		WyllFile.Type.Array target_t = translateArray(target);
		WyllFile.Type targetElem_t = translateType(target.getElement());
		//
		WyllFile.Type var_t = WyllFile.Type.Int; // FIXME: should be usize
		WyllFile.Expr.VariableAccess var = new WyllFile.Expr.VariableAccess(var_t, new Identifier("i"));
		WyllFile.Decl.Variable vardec = new WyllFile.Decl.Variable(new Tuple<>(), new Identifier("i"), var_t);
		WyllFile.Expr.VariableAccess param = new WyllFile.Expr.VariableAccess(param_t, new Identifier("xs"));
		//
		WyllFile.Expr initialValue = getDefaultValue(targetElem_t);
		WyllFile.Expr.ArrayGenerator init = new WyllFile.Expr.ArrayGenerator(target_t, initialValue,
				new WyllFile.Expr.ArrayLength(target_t, param));
		WyllFile.Decl.Variable resultdecl = new WyllFile.Decl.Variable(new Tuple<>(), new Identifier("ys"), target_t,
				init);
		WyllFile.Expr.VariableAccess result = new WyllFile.Expr.VariableAccess(target_t, new Identifier("ys"));
		WyllFile.Expr start = new WyllFile.Expr.Constant(var_t, new Value.Int(0));
		WyllFile.Expr end = new WyllFile.Expr.ArrayLength(target_t, param);
		WyllFile.Expr.ArrayAccess paramAccess = new WyllFile.Expr.ArrayAccess(param_t, param, var);
		WyllFile.Expr.ArrayAccess resultAccess = new WyllFile.Expr.ArrayAccess(target_t, result, var);
		WyllFile.Stmt assignment = new WyllFile.Stmt.Assign(resultAccess,
				addCoercion(paramAccess, actual.getElement(), target.getElement(), context));
		WyllFile.Stmt.Block loopBody = new WyllFile.Stmt.Block(assignment);
		WyllFile.Stmt.ForEach loop = new WyllFile.Stmt.ForEach(vardec, start, end, loopBody);
		WyllFile.Stmt.Return ret = new WyllFile.Stmt.Return(result);
		return new WyllFile.Stmt.Block(resultdecl, loop, ret);
	}

	public WyllFile.Expr addRecordCoercion(WyllFile.Expr expr, Type.Record _actual, Type.Record _target,
			Context context) {
		WyllFile.Type.Record actual = translateRecord(_actual);
		WyllFile.Type.Record target = translateRecord(_target);
		Tuple<WyllFile.Decl.Variable> fields = target.getFields();
		WyllFile.Expr[] args = new WyllFile.Expr[fields.size()];
		// FIXME: what to do for conversion to/from open record??
		for (int i = 0; i != fields.size(); ++i) {
			Identifier field = fields.get(i).getName();
			// FIXME: this is completely broken in the context of method invocations or
			// other sideeffecting operations.
			WyllFile.Expr fexpr = new WyllFile.Expr.RecordAccess(actual, expr, field);
			args[i] = addCoercion(fexpr, _actual.getField(field), _target.getField(field), context);
		}
		//
		Tuple<WyllFile.Identifier> fieldNames = fields.project(1, Identifier.class);
		return new WyllFile.Expr.RecordInitialiser(target, fieldNames, new Tuple<>(args));
	}

	public WyllFile.Expr addReferenceCoercion(WyllFile.Expr expr, Type.Reference actual, Type.Reference target,
			Context context) {
		// FIXME: what do we want to do here? On the JVM, we'll need to insert a cast as
		// a minimum (I think). Or, maybe it's impossible to get here?
		return expr;
	}

	public WyllFile.Expr addUnionCoercion(WyllFile.Expr expr, Type.Union actual, Type target, Context context) {
		WyllFile.Type type = translateType(target);
		int tag = determineTag(target, actual);
		expr = new WyllFile.Expr.UnionLeave(type, new Value.Int(tag), expr);
		return addCoercion(expr, actual.get(tag), target, context);
	}

	public WyllFile.Expr addUnionCoercion(WyllFile.Expr expr, Type actual, Type.Union target, Context context) {
		WyllFile.Type type = translateType(target);
		int tag = determineTag(target, actual);
		expr = addCoercion(expr, actual, target.get(tag), context);
		return new WyllFile.Expr.UnionEnter(type, new Value.Int(tag), expr);
	}

	public WyllFile.Expr addUnionCoercion(WyllFile.Expr expr, Type.Union actual, Type.Union target, Context context) {
		return expr;
	}

	public WyllFile.Expr addNominalCoercion(WyllFile.Expr expr, Type actual, Type.Nominal target, Context context) {
		WhileyFile.Decl.Type decl;
		try {
			// FIXME: this is broken
			decl = typeSystem.resolveExactly(target.getName(), WhileyFile.Decl.Type.class);
			return addCoercion(expr, actual, decl.getType(), context);
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	public WyllFile.Expr addNominalCoercion(WyllFile.Expr expr, Type.Nominal actual, Type target, Context context) {
		WhileyFile.Decl.Type decl;
		try {
			// FIXME: this is broken
			decl = typeSystem.resolveExactly(actual.getName(), WhileyFile.Decl.Type.class);
			return addCoercion(expr, decl.getType(), target, context);
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
	}

	// ==========================================================================
	// Misc
	// ==========================================================================
	/**
	 * Create a temporary variable which is guaranteed not to clash with any other
	 * temporaries create, or existing variables in the given scope.
	 *
	 * @param type
	 * @param initialiser
	 * @return
	 */
	public WyllFile.Decl.Variable createTemporaryVariable(Tuple<Type> _types, Expr _init, Context context) {
		// FIXME: this needs to be improved!!
		Identifier name = new Identifier("tmp" + tmpVarID++);
		WyllFile.Type type;
		WyllFile.Expr init;
		if (_types.size() == 1) {
			type = translateType(_types.get(0));
			init = translateExpression(_init, _types.get(0), context);
		} else {
			Type.Record retType = createMultipleReturnType(_types);
			type = translateType(retType);
			init = translateMultiExpression(_init, _types, context);
		}
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		return new WyllFile.Decl.Variable(modifiers, name, type, init);
	}

	public WyllFile.Decl.Variable createTemporaryVariable(Type _type, Expr _init, Context context) {
		// FIXME: this needs to be improved!!
		Identifier name = new Identifier("tmp" + tmpVarID++);
		WyllFile.Expr init;
		WyllFile.Type type = translateType(_type);
		init = translateExpression(_init, _type, context);
		Tuple<WyllFile.Modifier> modifiers = new Tuple<>();
		return new WyllFile.Decl.Variable(modifiers, name, type, init);
	}

	/**
	 * Generate the psuedo type used to implement multiple returns behind the
	 * scenes. This is a record with a matching number of fields names "f0" through
	 * "fn".
	 *
	 * @param types
	 * @return
	 */
	public WhileyFile.Type.Record createMultipleReturnType(Tuple<Type> types) {
		Decl.Variable[] fields = new Decl.Variable[types.size()];
		for (int i = 0; i != fields.length; ++i) {
			fields[i] = new Decl.Variable(new Tuple<>(), new Identifier("f" + i), types.get(i));
		}
		return new WhileyFile.Type.Record(false, new Tuple<>(fields));
	}
	private static int tmpVarID = 0;

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
			value = new Value.Null();
			break;
		case WyllFile.TYPE_bool:
			value = new Value.Bool(false);
			break;
		case WyllFile.TYPE_int:
			value = new Value.Int(0);
			break;
		case WyllFile.TYPE_array: {
			WyllFile.Expr v = getDefaultValue(((WyllFile.Type.Array) type).getElement());
			WyllFile.Expr zero = new WyllFile.Expr.Constant(WyllFile.Type.Int, new Value.Int(0));
			return new WyllFile.Expr.ArrayGenerator((WyllFile.Type.Array) type, v, zero);
		}
		case WyllFile.TYPE_record: {
			WyllFile.Type.Record record = (WyllFile.Type.Record) type;
			Tuple<WyllFile.Decl.Variable> fields = record.getFields();
			WyllFile.Expr[] operands = new WyllFile.Expr[fields.size()];
			for (int i = 0; i != fields.size(); ++i) {
				operands[i] = getDefaultValue(fields.get(i).getType());
			}
			return new WyllFile.Expr.RecordInitialiser((WyllFile.Type.Record) type, fields.project(1, Identifier.class),
					new Tuple<>(operands));
		}
		case WyllFile.TYPE_union: {
			WyllFile.Type.Union union = (WyllFile.Type.Union) type;
			return getDefaultValue(union.get(0));
		}
		default:
			throw new UnsupportedOperationException(
					"cannot construct default value (" + type.getClass().getName() + ")");
		}
		//
		return new WyllFile.Expr.Constant(type, value);
	}

	public int determineTag(Type.Union parent, Type child) {
		for (int i = 0; i != parent.size(); ++i) {
			if (isSubtype(parent.get(i), child)) {
				return i;
			}
		}
		throw new IllegalArgumentException("cannot determine appropriate tag (" + parent + "<-" + child + ")");
	}

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
			// FIXME: need to handle lifetimes properly
			return typeSystem.isRawSubtype(parent, child, null);
		} catch (ResolutionError e) {
			throw new RuntimeException("internal failure");
		}
	}

	public Name getNameMangle(Name name, Type.Callable sig) {
		int last = name.size() - 1;
		Identifier[] components = name.toArray(Identifier.class);
		components[last] = getNameMangle(components[last], sig);
		return new Name(components);
	}

	public Identifier getNameMangle(Identifier name, Type.Callable sig) {
		Tuple<Identifier> lifetimes;
		if (sig instanceof Type.Method) {
			lifetimes = ((Type.Method) sig).getLifetimeParameters();
		} else {
			lifetimes = new Tuple<>();
		}
		String mangle = mangler.getMangle(sig.getParameters(), lifetimes);
		return new Identifier(name.get() + mangle);
	}

	private static class Context {
		private final List<WyllFile.Decl> declarations;

		public Context() {
			this.declarations = new ArrayList<>();
		}

		public List<WyllFile.Decl> getDeclarations() {
			return declarations;
		}

		public void declare(WyllFile.Decl decl) {
			declarations.add(decl);
		}
	}
}
