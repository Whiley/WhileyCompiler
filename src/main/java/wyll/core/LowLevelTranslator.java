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
package wyll.core;

import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.Stmt;
import wyc.lang.WhileyFile.Type;
import wycc.util.ArrayUtils;
import wycc.util.Pair;
import wycc.util.Triple;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.subtyping.EmptinessTest;
import wyil.type.subtyping.StrictTypeEmptinessTest;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.util.AbstractTypeFilter;
import wyil.type.util.ConcreteTypeExtractor;
import wyil.type.util.ReadWriteTypeExtractor;

import static wyc.lang.WhileyFile.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;

/**
 *
 * @author David J. Pearce
 *
 */
public class LowLevelTranslator<D,S,E extends S> {
	protected final NameResolver resolver;
	protected final SubtypeOperator subtypeOperator;
	protected final ReadWriteTypeExtractor rwTypeExtractor;
	protected final ConcreteTypeExtractor concreteTypeExtractor;
	protected final Reducer<D,S,E> reducer;

	public LowLevelTranslator(NameResolver resolver, Reducer<D,S,E> reducer) {
		this.resolver = resolver;
		EmptinessTest<SemanticType> strictEmptiness = new StrictTypeEmptinessTest(resolver);
		this.subtypeOperator = new SubtypeOperator(resolver, strictEmptiness);
		this.rwTypeExtractor = new ReadWriteTypeExtractor(resolver, subtypeOperator);
		this.concreteTypeExtractor = new ConcreteTypeExtractor(resolver,strictEmptiness);
		this.reducer = reducer;
	}

	public List<D> visitWhileyFile(WhileyFile wf) {
		ArrayList<D> declarations = new ArrayList<>();
		for (Decl decl : wf.getDeclarations()) {
			D declaration = visitDeclaration(decl);
			if(declaration != null) {
				declarations.add(declaration);
			}
		}
		return declarations;
	}

	public D visitDeclaration(Decl decl) {
		switch (decl.getOpcode()) {
		case DECL_importfrom:
		case DECL_import:
			return visitImport((Decl.Import) decl);
		case DECL_staticvar:
			return visitStaticVariable((Decl.StaticVariable) decl);
		case DECL_type:
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

	public E visitLambda(Decl.Lambda decl, Environment environment) {
		// Redeclare this within
		environment = environment.declareWithin("this", decl.getLifetimes());
		//
		Tuple<Identifier> parameters = decl.getParameters().map(vd -> vd.getName());;
		E body = visitExpression(decl.getBody(), decl.getBody().getType(), environment);
		return reducer.visitLambda(decl.getType(), parameters, body);
	}

	public S visitVariable(Decl.Variable decl, Environment environment) {
		E initialiser = null;
		if (decl.hasInitialiser()) {
			initialiser = visitExpression(decl.getInitialiser(), decl.getType(), environment);
		}
		return reducer.visitVariableDeclaration(decl.getType(), decl.getName(), initialiser);
	}

	public D visitStaticVariable(Decl.StaticVariable decl) {
		E initialiser = null;
		if (decl.hasInitialiser()) {
			Environment environment = new Environment();
			initialiser = visitExpression(decl.getInitialiser(), decl.getType(), environment);
		}
		return reducer.visitStaticVariable(decl.getName(), decl.getType(), initialiser);
	}

	public D visitType(Decl.Type decl) {
		Decl.Variable var = decl.getVariableDeclaration();
		Environment environment = new Environment();
		List<E> invariant = visitExpressions(decl.getInvariant(), Type.Bool, environment);
		return reducer.visitType(decl.getName(), decl.getType(), var.getName(), invariant);
	}

	public D visitCallable(Decl.Callable decl) {
		switch (decl.getOpcode()) {
		case DECL_function:
		case DECL_method:
			return visitFunctionOrMethod((Decl.FunctionOrMethod) decl);
		case DECL_property:
			return visitProperty((Decl.Property) decl);
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public D visitFunctionOrMethod(Decl.FunctionOrMethod decl) {
		switch (decl.getOpcode()) {
		case DECL_function:
			return visitFunction((Decl.Function) decl);
		case DECL_method:
			return visitMethod((Decl.Method) decl);
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public D visitProperty(Decl.Property decl) {
		Environment environment = new Environment();
		Tuple<Identifier> parameters = decl.getParameters().map(vd -> vd.getName());
		Tuple<Identifier> returns = decl.getReturns().map(vd -> vd.getName());
		List<E> body = visitExpressions(decl.getInvariant(), Type.Bool, environment);
		return reducer.visitProperty(decl.getName(), decl.getType(), parameters, returns, body);
	}

	public D visitFunction(Decl.Function decl) {
		Environment environment = new Environment();
		//
		Tuple<Identifier> parameters = decl.getParameters().map(vd -> vd.getName());
		Tuple<Identifier> returns = decl.getReturns().map(vd -> vd.getName());
		List<E> requires = visitExpressions(decl.getRequires(), Type.Bool, environment);
		List<E> ensures = visitExpressions(decl.getEnsures(), Type.Bool, environment);
		List<S> body = visitBlock(decl.getBody(), environment, new FunctionOrMethodScope(decl));
		//
		return reducer.visitFunction(decl.getName(), decl.getType(), parameters, returns, requires, ensures, body);
	}

	public D visitMethod(Decl.Method decl) {
		// Construct environment relation
		Environment environment = new Environment();
		environment = environment.declareWithin("this", decl.getLifetimes());
		//
		Tuple<Identifier> parameters = decl.getParameters().map(vd -> vd.getName());
		Tuple<Identifier> returns = decl.getReturns().map(vd -> vd.getName());
		List<E> requires = visitExpressions(decl.getRequires(), Type.Bool, environment);
		List<E> ensures = visitExpressions(decl.getEnsures(), Type.Bool, environment);
		List<S> body = visitBlock(decl.getBody(), environment, new FunctionOrMethodScope(decl));
		//
		return reducer.visitMethod(decl.getName(), decl.getType(), parameters, returns, requires, ensures, body);
	}

	public S visitStatement(Stmt stmt, Environment environment, EnclosingScope scope) {
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

	public S visitAssert(Stmt.Assert stmt, Environment environment, EnclosingScope scope) {
		E operand = visitExpression(stmt.getCondition(), Type.Bool, environment);
		return reducer.visitAssume(operand);
	}

	public S visitAssign(Stmt.Assign stmt, Environment environment, EnclosingScope scope) {
		// Extract the target types
		Tuple<Type> targets = stmt.getLeftHandSide().map((LVal l) -> l.getType());
		// Extract the source types
		Tuple<Expr> sources = stmt.getRightHandSide();
		// Construct lval expressions
		List<E> lvals = visitLVals(stmt.getLeftHandSide(), environment, scope);
		// Generate list of assignments
		ArrayList<Triple<Tuple<Type>,List<E>,E>> assignments = new ArrayList<>();
		for(int i=0,j=0;i!=sources.size();++i) {
			Expr source = sources.get(i);
			Tuple<Type> sourceTypes = source.getTypes();
			List<E> lhs = new ArrayList<>();
			E rhs;
			if(sourceTypes == null) {
				// Simple assignment
				sourceTypes = new Tuple<>(targets.get(j));
				rhs = visitExpression(source, targets.get(j), environment);
				lhs.add(lvals.get(j));
				j = j + 1;
			} else {
				// Complex assignment
				int next = j + sourceTypes.size();
				Tuple<Type> subtargets = targets.get(j, next);
				rhs = visitMultiExpression(source, subtargets, environment);
				lhs.addAll(lvals.subList(j, next));
				j = next;
			}
			assignments.add(new Triple<>(sourceTypes, lhs, rhs));
		}
		// FIXME: certainly broken for multiple assignments.
		return reducer.visitAssign(assignments);
	}

	public List<E> visitLVals(Tuple<LVal> lvals, Environment environment, EnclosingScope scope) {
		ArrayList<E> ls = new ArrayList<>();
		for (int i = 0; i != lvals.size(); ++i) {
			Expr lval = lvals.get(i);
			ls.add(visitExpression(lval, lval.getType(), environment));
		}
		return ls;
	}

	public S visitAssume(Stmt.Assume stmt, Environment environment, EnclosingScope scope) {
		E operand = visitExpression(stmt.getCondition(), Type.Bool, environment);
		return reducer.visitAssume(operand);
	}

	public List<S> visitBlock(Stmt.Block stmt, Environment environment, EnclosingScope scope) {
		ArrayList<S> block = new ArrayList<>();
		for (int i = 0; i != stmt.size(); ++i) {
			block.add(visitStatement(stmt.get(i), environment, scope));
		}
		return block;
	}

	public S visitBreak(Stmt.Break stmt, Environment environment, EnclosingScope scope) {
		return reducer.visitBreak();
	}

	public S visitContinue(Stmt.Continue stmt, Environment environment, EnclosingScope scope) {
		return reducer.visitContinue();
	}

	public S visitDebug(Stmt.Debug stmt, Environment environment, EnclosingScope scope) {
		// FIXME: Should be Type.Int(0,255)
		Type std_ascii = new Type.Array(Type.Int);
		E operand = visitExpression(stmt.getOperand(), std_ascii, environment);
		return reducer.visitDebug(operand);
	}

	public S visitDoWhile(Stmt.DoWhile stmt, Environment environment, EnclosingScope scope) {
		List<S> body = visitBlock(stmt.getBody(), environment, scope);
		E condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		List<E> invariants = visitExpressions(stmt.getInvariant(), Type.Bool, environment);
		return reducer.visitDoWhile(condition, invariants, body);
	}

	public S visitFail(Stmt.Fail stmt, Environment environment, EnclosingScope scope) {
		return reducer.visitFail();
	}

	public S visitIfElse(Stmt.IfElse stmt, Environment environment, EnclosingScope scope) {
		E condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		List<S> trueBranch = visitBlock(stmt.getTrueBranch(), environment, scope);
		List<S> falseBranch = new ArrayList<>();
		if (stmt.hasFalseBranch()) {
			falseBranch = visitBlock(stmt.getFalseBranch(), environment, scope);
		}
		ArrayList<Pair<E,List<S>>> branches = new ArrayList<>();
		branches.add(new Pair<>(condition,trueBranch));
		branches.add(new Pair<>(null,falseBranch));
		return reducer.visitIfElse(branches);
	}

	public S visitNamedBlock(Stmt.NamedBlock stmt, Environment environment, EnclosingScope scope) {
		// Updated the environment with new within relations
		LifetimeDeclaration enclosing = scope.getEnclosingScope(LifetimeDeclaration.class);
		String[] lifetimes = enclosing.getDeclaredLifetimes();
		environment = environment.declareWithin(stmt.getName().get(), lifetimes);
		// Create an appropriate scope for this block
		scope = new NamedBlockScope(scope, stmt);
		//
		S body = visitStatement(stmt.getBlock(), environment, scope);
		return reducer.visitNamedBlock(stmt.getName(),body);
	}

	public S visitReturn(Stmt.Return stmt, Environment environment, EnclosingScope scope) {
		Decl.FunctionOrMethod enclosing = stmt.getAncestor(Decl.FunctionOrMethod.class);
		Tuple<Type> returnTypes = enclosing.getType().getReturns();
		List<E> operands = visitExpressions(stmt.getReturns(), returnTypes, environment);
		ArrayList<Pair<Type,E>> returns = new ArrayList<>();
		for(int i=0;i!=operands.size();++i) {
			// FIXME: broken for multiple returns
			returns.add(new Pair<>(returnTypes.get(i),operands.get(i)));
		}
		return reducer.visitReturn(returns);
	}

	public S visitSkip(Stmt.Skip stmt, Environment environment, EnclosingScope scope) {
		return reducer.visitSkip();
	}

	public S visitSwitch(Stmt.Switch stmt, Environment environment, EnclosingScope scope) {
		Type target = stmt.getCondition().getType();
		E condition = visitExpression(stmt.getCondition(), target, environment);
		Tuple<Stmt.Case> cases = stmt.getCases();
		ArrayList<Pair<List<E>,S>> branches = new ArrayList<>();
		for (int i = 0; i != cases.size(); ++i) {
			branches.add(visitCase(cases.get(i), target, environment, scope));
		}
		return reducer.visitSwitch(target, condition, branches);
	}

	public Pair<List<E>, S> visitCase(Stmt.Case stmt, Type target, Environment environment, EnclosingScope scope) {
		List<E> cases = visitExpressions(stmt.getConditions(), target, environment);
		S body = visitStatement(stmt.getBlock(), environment, scope);
		return new Pair<>(cases, body);
	}

	public S visitWhile(Stmt.While stmt, Environment environment, EnclosingScope scope) {
		E condition = visitExpression(stmt.getCondition(), Type.Bool, environment);
		List<E> invariants = visitExpressions(stmt.getInvariant(), Type.Bool, environment);
		List<S> body = visitBlock(stmt.getBody(), environment, scope);
		return reducer.visitWhile(condition, invariants, body);
	}

	public List<E> visitExpressions(Tuple<Expr> exprs, Tuple<Type> targets, Environment environment) {
		ArrayList<E> operands = new ArrayList<>();
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
		return operands;
	}

	public List<E> visitExpressions(Tuple<Expr> exprs, Type target, Environment environment) {
		ArrayList<E> operands = new ArrayList<>();
		for (int i = 0; i != exprs.size(); ++i) {
			operands.add(visitExpression(exprs.get(i), target, environment));
		}
		return operands;
	}

	public E visitMultiExpression(Expr expr, Tuple<Type> types, Environment environment) {
		if (expr instanceof Expr.Invoke) {
			return visitInvoke((Expr.Invoke) expr, types, environment);
		} else {
			return visitIndirectInvoke((Expr.IndirectInvoke) expr, types, environment);
		}
	}

	/**
	 * Visit a given expression which is being assigned to a location of a given
	 * type.
	 *
	 * @param expr
	 * @param target
	 */
	public E visitExpression(Expr expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Terminals
		case EXPR_constant:
			return visitConstant((Expr.Constant) expr, target, environment);
		case EXPR_indirectinvoke:
			return visitIndirectInvoke((Expr.IndirectInvoke) expr, new Tuple<>(target), environment);
		case EXPR_lambdaaccess:
			return visitLambdaAccess((Expr.LambdaAccess) expr, target, environment);
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
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitUnaryOperator(Expr.UnaryOperator expr, Type target, Environment environment) {
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
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitBinaryOperator(Expr.BinaryOperator expr, Type target, Environment environment) {
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
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitTernaryOperator(Expr.TernaryOperator expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Ternary Operators
		case EXPR_arrayupdate:
			Type.Array arrayT = selectArray(target, expr, environment);
			return visitArrayUpdate((Expr.ArrayUpdate) expr, arrayT, environment);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitNaryOperator(Expr.NaryOperator expr, Type target, Environment environment) {
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
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitArrayAccess(Expr.ArrayAccess expr, Type target, Environment environment) {
		Type.Array type = extractReadableArrayType(expr.getFirstOperand().getType(), environment);
		E source = visitExpression(expr.getFirstOperand(), expr.getFirstOperand().getType(), environment);
		E index = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return reducer.visitArrayAccess(type, source, index);
	}

	public E visitArrayLength(Expr.ArrayLength expr, Type.Int target, Environment environment) {
		Type.Array type = extractReadableArrayType(expr.getOperand().getType(), environment);
		E operand = visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
		return reducer.visitArrayLength(type, operand);
	}

	public E visitArrayGenerator(Expr.ArrayGenerator expr, Type.Array target, Environment environment) {
		E element = visitExpression(expr.getFirstOperand(), target.getElement(), environment);
		E length = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return reducer.visitArrayGenerator(target, element, length);
	}

	public E visitArrayInitialiser(Expr.ArrayInitialiser expr, Type.Array target, Environment environment) {
		List<E> operands = visitExpressions(expr.getOperands(), target.getElement(), environment);
		return reducer.visitArrayInitialiser(target, operands);
	}

	public E visitArrayUpdate(Expr.ArrayUpdate expr, Type.Array target, Environment environment) {
		E src = visitExpression(expr.getFirstOperand(), target, environment);
		E index = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		E value = visitExpression(expr.getThirdOperand(), target.getElement(), environment);
		return reducer.visitArrayUpdate(target,src,index,value);
	}

	public E visitBitwiseComplement(Expr.BitwiseComplement expr, Environment environment) {
		E operand = visitExpression(expr.getOperand(), Type.Byte, environment);
		return reducer.visitBitwiseNot(Type.Byte, operand);
	}

	public E visitBitwiseAnd(Expr.BitwiseAnd expr, Environment environment) {
		List<E> operands = visitExpressions(expr.getOperands(), Type.Byte, environment);
		return reducer.visitBitwiseAnd(Type.Byte, operands);
	}

	public E visitBitwiseOr(Expr.BitwiseOr expr, Environment environment) {
		List<E> operands = visitExpressions(expr.getOperands(), Type.Byte, environment);
		return reducer.visitBitwiseOr(Type.Byte, operands);
	}

	public E visitBitwiseXor(Expr.BitwiseXor expr, Environment environment) {
		List<E> operands = visitExpressions(expr.getOperands(), Type.Byte, environment);
		return reducer.visitBitwiseXor(Type.Byte, operands);
	}

	public E visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), Type.Byte, environment);
		E rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return reducer.visitBitwiseShl(Type.Byte, lhs, rhs);
	}

	public E visitBitwiseShiftRight(Expr.BitwiseShiftRight expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), Type.Byte, environment);
		E rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return reducer.visitBitwiseShr(Type.Byte, lhs, rhs);
	}

	public E visitCast(Expr.Cast expr, Type target, Environment environment) {
		Type operandT = expr.getOperand().getType();
		E operand = visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
		return reducer.visitCast(operandT,operand,target);
	}

	public E visitConstant(Expr.Constant expr, Type target, Environment environment) {
		Value value = expr.getValue();
		if (value instanceof Value.Null) {
			return reducer.visitNullInitialiser();
		} else if (value instanceof Value.Bool) {
			Value.Bool b = (Value.Bool) value;
			return reducer.visitLogicalInitialiser(b.get());
		} else if (value instanceof Value.Byte) {
			Value.Byte b = (Value.Byte) value;
			return reducer.visitIntegerInitialiser(Type.Int, BigInteger.valueOf(b.get() & 0xFF));
		} else if (value instanceof Value.UTF8) {
			Value.UTF8 b = (Value.UTF8) value;
			byte[] bs = b.get();
			ArrayList<E> values = new ArrayList<>();
			for (int i = 0; i != bs.length; ++i) {
				values.add(reducer.visitIntegerInitialiser(Type.Int, BigInteger.valueOf(bs[i])));
			}
			return reducer.visitArrayInitialiser(new Type.Array(Type.Int), values);
		} else {
			Value.Int i = (Value.Int) value;
			return reducer.visitIntegerInitialiser(Type.Int, i.get());
		}
	}

	public E visitDereference(Expr.Dereference expr, Type target, Environment environment) {
		// Determine reference type being dereferenced
		Type operandT = expr.getOperand().getType();
		Type.Reference type = extractReadableReferenceType(operandT, environment);
		//
		E operand = visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
		return reducer.visitDereference(type, operand);
	}

	public E visitEqual(Expr.Equal expr, Environment environment) {
		Type lhsT = expr.getFirstOperand().getType();
		Type rhsT = expr.getSecondOperand().getType();
		E lhs = visitExpression(expr.getFirstOperand(), lhsT, environment);
		E rhs = visitExpression(expr.getSecondOperand(), rhsT, environment);
		return reducer.visitEqual(lhsT, rhsT, lhs, rhs);
	}

	public E visitIntegerLessThan(Expr.IntegerLessThan expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), Type.Int, environment);
		E rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return reducer.visitIntegerLessThan(Type.Int, lhs, rhs);
	}

	public E visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), Type.Int, environment);
		E rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return reducer.visitIntegerLessThanOrEqual(Type.Int, lhs, rhs);
	}

	public E visitIntegerGreaterThan(Expr.IntegerGreaterThan expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), Type.Int, environment);
		E rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return reducer.visitIntegerGreaterThan(Type.Int, lhs, rhs);
	}

	public E visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), Type.Int, environment);
		E rhs = visitExpression(expr.getSecondOperand(), Type.Int, environment);
		return reducer.visitIntegerGreaterThanOrEqual(Type.Int, lhs, rhs);
	}

	public E visitIntegerNegation(Expr.IntegerNegation expr, Type.Int target, Environment environment) {
		E operand = visitExpression(expr.getOperand(), target, environment);
		return reducer.visitIntegerNegate(Type.Int, operand);
	}

	public E visitIntegerAddition(Expr.IntegerAddition expr, Type.Int target, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), target, environment);
		E rhs = visitExpression(expr.getSecondOperand(), target, environment);
		return reducer.visitIntegerAdd(Type.Int, lhs, rhs);
	}

	public E visitIntegerSubtraction(Expr.IntegerSubtraction expr, Type.Int target, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), target, environment);
		E rhs = visitExpression(expr.getSecondOperand(), target, environment);
		return reducer.visitIntegerSubtract(Type.Int, lhs, rhs);
	}

	public E visitIntegerMultiplication(Expr.IntegerMultiplication expr, Type.Int target, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), target, environment);
		E rhs = visitExpression(expr.getSecondOperand(), target, environment);
		return reducer.visitIntegerMultiply(Type.Int, lhs, rhs);
	}

	public E visitIntegerDivision(Expr.IntegerDivision expr, Type.Int target, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), target, environment);
		E rhs = visitExpression(expr.getSecondOperand(), target, environment);
		return reducer.visitIntegerDivide(Type.Int, lhs, rhs);
	}

	public E visitIntegerRemainder(Expr.IntegerRemainder expr, Type.Int target, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), target, environment);
		E rhs = visitExpression(expr.getSecondOperand(), target, environment);
		return reducer.visitIntegerRemainder(Type.Int, lhs, rhs);
	}

	public E visitIs(Expr.Is expr, Environment environment) {
		Type type = expr.getOperand().getType();
		E operand = visitExpression(expr.getOperand(), type, environment);
		return reducer.visitIs(type, operand, expr.getTestType());
	}

	public E visitLogicalAnd(Expr.LogicalAnd expr, Environment environment) {
		List<E> operands = visitExpressions(expr.getOperands(), Type.Bool, environment);
		return reducer.visitLogicalAnd(operands);
	}

	public E visitLogicalImplication(Expr.LogicalImplication expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), Type.Bool, environment);
		E rhs = visitExpression(expr.getSecondOperand(), Type.Bool, environment);
		return reducer.visitLogicalIff(lhs,rhs);
	}

	public E visitLogicalIff(Expr.LogicalIff expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), Type.Bool, environment);
		E rhs = visitExpression(expr.getSecondOperand(), Type.Bool, environment);
		return reducer.visitLogicalIff(lhs,rhs);
	}

	public E visitLogicalNot(Expr.LogicalNot expr, Environment environment) {
		E operand = visitExpression(expr.getOperand(), Type.Bool, environment);
		return reducer.visitLogicalNot(operand);
	}

	public E visitLogicalOr(Expr.LogicalOr expr, Environment environment) {
		List<E> operands = visitExpressions(expr.getOperands(), Type.Bool, environment);
		return reducer.visitLogicalOr(operands);
	}

	public E visitExistentialQuantifier(Expr.ExistentialQuantifier expr, Environment environment) {
		ArrayList<Triple<Identifier, E, E>> initialisers = new ArrayList<>();
		Tuple<Decl.Variable> parameters = expr.getParameters();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.Variable parameter = parameters.get(i);
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			E start = visitExpression(range.getFirstOperand(), TYPE_ARRAY_INT, environment);
			E end = visitExpression(range.getSecondOperand(), TYPE_ARRAY_INT, environment);
			initialisers.add(new Triple<>(parameter.getName(), start, end));
		}
		E body = visitExpression(expr.getOperand(), Type.Bool, environment);
		return reducer.visitExistentialQuantifier(initialisers, body);
	}

	public E visitUniversalQuantifier(Expr.UniversalQuantifier expr, Environment environment) {
		ArrayList<Triple<Identifier, E, E>> initialisers = new ArrayList<>();
		Tuple<Decl.Variable> parameters = expr.getParameters();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.Variable parameter = parameters.get(i);
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			E start = visitExpression(range.getFirstOperand(), TYPE_ARRAY_INT, environment);
			E end = visitExpression(range.getSecondOperand(), TYPE_ARRAY_INT, environment);
			initialisers.add(new Triple<>(parameter.getName(), start, end));
		}
		E body = visitExpression(expr.getOperand(), Type.Bool, environment);
		return reducer.visitUniversalQuantifier(initialisers,body);
	}

	public E visitInvoke(Expr.Invoke expr, Tuple<Type> targets, Environment environment) {
		Type.Callable signature = expr.getSignature();
		Tuple<Type> parameters = signature.getParameters();
		if(signature instanceof Type.Method) {
			// Must bind lifetime arguments
			Decl.Method decl = resolveMethod(expr.getName(), (Type.Method) expr.getSignature());
			parameters = bind(decl,expr.getLifetimes());
		}
		List<E> arguments = visitExpressions(expr.getOperands(), parameters, environment);
		return reducer.visitInvoke(signature,expr.getName(),arguments);
	}

	public E visitIndirectInvoke(Expr.IndirectInvoke expr, Tuple<Type> targets, Environment environment) {
		Type.Callable sourceT = asType(expr.getSource().getType(), Type.Callable.class);
		E source = visitExpression(expr.getSource(), sourceT, environment);
		List<E> arguments = visitExpressions(expr.getArguments(), sourceT.getParameters(), environment);
		return reducer.visitIndirectInvoke(sourceT, source, arguments);
	}

	public E visitLambdaAccess(Expr.LambdaAccess expr, Type type, Environment environment) {
		return reducer.visitLambdaAccess(expr.getSignature(), expr.getName());
	}

	public E visitNew(Expr.New expr, Type.Reference target, Environment environment) {
		E operand = visitExpression(expr.getOperand(), target.getElement(), environment);
		return reducer.visitNew(target, operand);
	}

	public E visitNotEqual(Expr.NotEqual expr, Environment environment) {
		Type lhsT = expr.getFirstOperand().getType();
		Type rhsT = expr.getSecondOperand().getType();
		E lhs = visitExpression(expr.getFirstOperand(), expr.getFirstOperand().getType(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), expr.getSecondOperand().getType(), environment);
		return reducer.visitNotEqual(lhsT, rhsT, lhs, rhs);
	}

	public E visitRecordAccess(Expr.RecordAccess expr, Type target, Environment environment) {
		Type.Record type = extractReadableRecordType(expr.getType(), environment);
		E src = visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
		return reducer.visitRecordAccess(type, src, expr.getField());
	}

	public E visitRecordInitialiser(Expr.RecordInitialiser expr, Type.Record target, Environment environment) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		List<E> ops = new ArrayList<>();
		for (int i = 0; i != fields.size(); ++i) {
			Expr operand = operands.get(i);
			Type type = target.getField(fields.get(i));
			if (type == null) {
				// NOTE: open records may not have concrete types for fields.
				type = operand.getType();
			}
			ops.add(visitExpression(operand, type, environment));
		}
		return reducer.visitRecordInitialiser(target, ops);
	}

	public E visitRecordUpdate(Expr.RecordUpdate expr, Type.Record target, Environment environment) {
		// FIXME: this should be implemented one day
		throw new UnsupportedOperationException();
	}

	public E visitStaticVariableAccess(Expr.StaticVariableAccess expr, Type target, Environment environment) {
		return reducer.visitStaticVariableAccess(target, expr.getName());
	}

	public E visitVariableAccess(Expr.VariableAccess expr, Type target, Environment environment) {
		return reducer.visitVariableAccess(target, expr.getVariableDeclaration().getName());
	}

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
		throw new IllegalArgumentException("invalid method signature (" + name + ":" + signature + ")");
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
			throw new IllegalArgumentException(e);
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
				throw new IllegalArgumentException("invalid type: " + child);
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
				throw new IllegalArgumentException("invalid type: " + type);
			}
		} else {
			throw new IllegalArgumentException("invalid type: " + type);
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

	// ==========================================================================
	// Reducer
	// ==========================================================================

	public interface Reducer<D,S,E> {

		// ===================================================
		// Declarations
		// ===================================================

		public D visitStaticVariable(Identifier name, Type type, E initialiser);

		public D visitType(Identifier name, Type definition, Identifier parameter, List<E> invariant);

		public D visitProperty(Identifier name, Type.Property type, Tuple<Identifier> parameters, Tuple<Identifier> returns,
				List<E> body);

		public D visitFunction(Identifier name, Type.Function type, Tuple<Identifier> parameters, Tuple<Identifier> returns,
				List<E> requires, List<E> ensures, List<S> body);

		public D visitMethod(Identifier name, Type.Method type, Tuple<Identifier> parameters, Tuple<Identifier> returns,
				List<E> requires, List<E> ensures, List<S> body);

		// ===================================================
		// Statements
		// ===================================================

		public S visitAssert(E condition);

		public S visitAssume(E condition);

		public S visitAssign(List<Triple<Tuple<Type>,List<E>, E>> assignments);

		public S visitBreak();

		public S visitContinue();

		public S visitDebug(E operand);

		public S visitDoWhile(E condition, List<E> invariants, List<S> body);

		public S visitFail();

		public S visitIfElse(List<Pair<E, List<S>>> branches);

		public S visitNamedBlock(Identifier name, S body);

		public S visitReturn(List<Pair<Type,E>> operands);

		public S visitSkip();

		public S visitSwitch(Type type, E condition, List<Pair<List<E>, S>> branches);

		public S visitVariableDeclaration(Type type, Identifier name, E initialiser);

		public S visitWhile(E condition, List<E> invariants, List<S> body);

		// ===================================================
		// General Expressions
		// ===================================================

		public E visitNullInitialiser();

		public E visitVariableAccess(Type type, Identifier name);

		public E visitLambdaAccess(Type.Callable type, Name name);

		public E visitLambda(Type.Callable type, Tuple<Identifier> parameters, E body);

		public E visitStaticVariableAccess(Type type, Name name);

		public E visitInvoke(Type.Callable type, Name name, List<E> arguments);

		public E visitIndirectInvoke(Type.Callable type, E receiver, List<E> arguments);

		public E visitIs(Type type, E operand, Type test);

		public E visitEqual(Type lhsT, Type rhsT, E lhs, E rhs);

		public E visitNotEqual(Type lhsT, Type rhsT, E lhs, E rhs);

		public E visitCast(Type type, E expr, Type cast);

		// ===================================================
		// Logical Expressions
		// ===================================================

		public E visitLogicalInitialiser(boolean value);

		public E visitLogicalAnd(List<E> operands);

		public E visitLogicalOr(List<E> operands);

		public E visitLogicalNot(E expr);

		public E visitLogicalIff(E lhs, E rhs);

		public E visitLogicalImplication(E lhs, E rhs);

		public E visitUniversalQuantifier(List<Triple<Identifier,E,E>> paraemters, E body);

		public E visitExistentialQuantifier(List<Triple<Identifier,E,E>> paraemters, E body);

		// ===================================================
		// Integer Expressions
		// ===================================================

		public E visitIntegerInitialiser(Type.Int type, BigInteger value);

		public E visitIntegerLessThan(Type.Int type, E lhs, E rhs);

		public E visitIntegerLessThanOrEqual(Type.Int type, E lhs, E rhs);

		public E visitIntegerGreaterThan(Type.Int type, E lhs, E rhs);

		public E visitIntegerGreaterThanOrEqual(Type.Int type, E lhs, E rhs);

		public E visitIntegerAdd(Type.Int type, E lhs, E rhs);

		public E visitIntegerSubtract(Type.Int type, E lhs, E rhs);

		public E visitIntegerMultiply(Type.Int type, E lhs, E rhs);

		public E visitIntegerDivide(Type.Int type, E lhs, E rhs);

		public E visitIntegerRemainder(Type.Int type, E lhs, E rhs);

		public E visitIntegerNegate(Type.Int type, E expr);

		public E visitIntegerCoercion(Type.Int target, Type.Int actual, E expr);

		// ===================================================
		// Bitwise Expressions
		// ===================================================

		public E visitBitwiseAnd(Type.Byte target, List<E> operands);

		public E visitBitwiseOr(Type.Byte target, List<E> operands);

		public E visitBitwiseXor(Type.Byte target, List<E> operands);

		public E visitBitwiseShl(Type.Byte target, E lhs, E rhs);

		public E visitBitwiseShr(Type.Byte target, E lhs, E rhs);

		public E visitBitwiseNot(Type.Byte target, E expr);

		// ===================================================
		// Array Expressions
		// ===================================================

		public E visitArrayInitialiser(Type.Array type, List<E> operands);

		public E visitArrayGenerator(Type.Array type, E value, E length);

		public E visitArrayLength(Type.Array type, E source);

		public E visitArrayAccess(Type.Array type, E source, E index);

		public E visitArrayUpdate(Type.Array type, E source, E index, E value);

		// ===================================================
		// Record Expressions
		// ===================================================

		public E visitRecordInitialiser(Type.Record type, List<E> operands);

		public E visitRecordAccess(Type.Record type, E source, Identifier field);

		public E visitRecordCoercion(Type.Int target, Type.Int actual, E expr);

		// ===================================================
		// Reference Expressions
		// ===================================================

		public E visitNew(Type.Reference type, E operand);

		public E visitDereference(Type.Reference type, E operand);
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
