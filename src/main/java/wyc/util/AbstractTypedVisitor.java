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
package wyc.util;

import wyc.check.FlowTypeUtils.Environment;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.Stmt;
import wyc.lang.WhileyFile.Type;
import wycc.util.ArrayUtils;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.util.AbstractTypeFilter;

import static wyc.lang.WhileyFile.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wybs.lang.CompilationUnit;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;

/**
 * A simple visitor over all declarations, statements, expressions and types in
 * a given WhileyFile which accepts no additional data parameters and returns
 * nothing. The intention is that this is extended as necessary to provide
 * custom functionality.
 *
 * @author David J. Pearce
 *
 */
public abstract class AbstractTypedVisitor {
	protected final NameResolver resolver;
	protected final SubtypeOperator subtypeOperator;

	public AbstractTypedVisitor(NameResolver resolver, SubtypeOperator subtypeOperator) {
		this.resolver = resolver;
		this.subtypeOperator = subtypeOperator;
	}

	public void visitWhileyFile(WhileyFile wf) {
		for (Decl decl : wf.getDeclarations()) {
			visitDeclaration(decl);
		}
	}

	public void visitDeclaration(Decl decl) {
		switch (decl.getOpcode()) {
		case DECL_importfrom:
		case DECL_import:
			visitImport((Decl.Import) decl);
			break;
		case DECL_staticvar:
			visitStaticVariable((Decl.StaticVariable) decl);
			break;
		case DECL_type:
			visitType((Decl.Type) decl);
			break;
		case DECL_function:
		case DECL_method:
		case DECL_property:
			visitCallable((Decl.Callable) decl);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void visitImport(Decl.Import decl) {

	}

	public void visitLambda(Decl.Lambda decl, Environment environment) {
		// Redeclare this within
		environment = environment.declareWithin("this", decl.getLifetimes());
		//
		visitVariables(decl.getParameters(), environment);
		visitExpression(decl.getBody(), decl.getBody().getType(), environment);
	}

	public void visitVariables(Tuple<Decl.Variable> vars, Environment environment) {
		for (int i = 0; i != vars.size(); ++i) {
			Decl.Variable var = vars.get(i);
			visitVariable(var, environment);
		}
	}

	public void visitVariable(Decl.Variable decl, Environment environment) {
		visitType(decl.getType());
		if (decl.hasInitialiser()) {
			visitExpression(decl.getInitialiser(), decl.getType(), environment);
		}
	}

	public void visitStaticVariable(Decl.StaticVariable decl) {
		visitType(decl.getType());
		if (decl.hasInitialiser()) {
			Environment environment = new Environment();
			visitExpression(decl.getInitialiser(), decl.getType(), environment);
		}
	}

	public void visitType(Decl.Type decl) {
		Environment environment = new Environment();
		visitVariable(decl.getVariableDeclaration(), environment);
		visitExpressions(decl.getInvariant(), Type.Bool, environment);
	}

	public void visitCallable(Decl.Callable decl) {
		switch (decl.getOpcode()) {
		case DECL_function:
		case DECL_method:
			visitFunctionOrMethod((Decl.FunctionOrMethod) decl);
			break;
		case DECL_property:
			visitProperty((Decl.Property) decl);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void visitFunctionOrMethod(Decl.FunctionOrMethod decl) {
		switch (decl.getOpcode()) {
		case DECL_function:
			visitFunction((Decl.Function) decl);
			break;
		case DECL_method:
			visitMethod((Decl.Method) decl);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void visitProperty(Decl.Property decl) {
		Environment environment = new Environment();
		visitVariables(decl.getParameters(), environment);
		visitVariables(decl.getReturns(), environment);
		visitExpressions(decl.getInvariant(), Type.Bool, environment);
	}

	public void visitFunction(Decl.Function decl) {
		Environment environment = new Environment();
		visitVariables(decl.getParameters(), environment);
		visitVariables(decl.getReturns(), environment);
		visitExpressions(decl.getRequires(), Type.Bool, environment);
		visitExpressions(decl.getEnsures(), Type.Bool, environment);
		visitStatement(decl.getBody(), environment, new FunctionOrMethodScope(decl));
	}

	public void visitMethod(Decl.Method decl) {
		// Construct environment relation
		Environment environment = new Environment();
		environment = environment.declareWithin("this", decl.getLifetimes());
		//
		visitVariables(decl.getParameters(), environment);
		visitVariables(decl.getReturns(), environment);
		visitExpressions(decl.getRequires(), Type.Bool, environment);
		visitExpressions(decl.getEnsures(), Type.Bool, environment);
		visitStatement(decl.getBody(), environment, new FunctionOrMethodScope(decl));
	}

	public void visitStatement(Stmt stmt, Environment environment, EnclosingScope scope) {
		switch (stmt.getOpcode()) {
		case DECL_variable:
		case DECL_variableinitialiser:
			visitVariable((Decl.Variable) stmt, environment);
			break;
		case STMT_assert:
			visitAssert((Stmt.Assert) stmt, environment, scope);
			break;
		case STMT_assign:
			visitAssign((Stmt.Assign) stmt, environment, scope);
			break;
		case STMT_assume:
			visitAssume((Stmt.Assume) stmt, environment, scope);
			break;
		case STMT_block:
			visitBlock((Stmt.Block) stmt, environment, scope);
			break;
		case STMT_break:
			visitBreak((Stmt.Break) stmt, environment, scope);
			break;
		case STMT_continue:
			visitContinue((Stmt.Continue) stmt, environment, scope);
			break;
		case STMT_debug:
			visitDebug((Stmt.Debug) stmt, environment, scope);
			break;
		case STMT_dowhile:
			visitDoWhile((Stmt.DoWhile) stmt, environment, scope);
			break;
		case STMT_fail:
			visitFail((Stmt.Fail) stmt, environment, scope);
			break;
		case STMT_if:
		case STMT_ifelse:
			visitIfElse((Stmt.IfElse) stmt, environment, scope);
			break;
		case EXPR_invoke:
			visitInvoke((Expr.Invoke) stmt, Type.Void, environment);
			break;
		case EXPR_indirectinvoke:
			visitIndirectInvoke((Expr.IndirectInvoke) stmt, Type.Void, environment);
			break;
		case STMT_namedblock:
			visitNamedBlock((Stmt.NamedBlock) stmt, environment, scope);
			break;
		case STMT_return:
			visitReturn((Stmt.Return) stmt, environment, scope);
			break;
		case STMT_skip:
			visitSkip((Stmt.Skip) stmt, environment, scope);
			break;
		case STMT_switch:
			visitSwitch((Stmt.Switch) stmt, environment, scope);
			break;
		case STMT_while:
			visitWhile((Stmt.While) stmt, environment, scope);
			break;
		default:
			throw new IllegalArgumentException("unknown statement encountered (" + stmt.getClass().getName() + ")");
		}
	}

	public void visitAssert(Stmt.Assert stmt, Environment environment, EnclosingScope scope) {
		visitExpression(stmt.getCondition(), Type.Bool, environment);
	}

	public void visitAssign(Stmt.Assign stmt, Environment environment, EnclosingScope scope) {
		Tuple<Type> targets = stmt.getLeftHandSide().map((LVal l) -> l.getType());
		visitLVals(stmt.getLeftHandSide(), environment, scope);
		visitExpressions(stmt.getRightHandSide(), targets, environment);
	}

	public void visitLVals(Tuple<LVal> lvals, Environment environment, EnclosingScope scope) {
		for (int i = 0; i != lvals.size(); ++i) {
			Expr lval = lvals.get(i);
			visitExpression(lval, lval.getType(), environment);
		}
	}

	public void visitAssume(Stmt.Assume stmt, Environment environment, EnclosingScope scope) {
		visitExpression(stmt.getCondition(), Type.Bool, environment);
	}

	public void visitBlock(Stmt.Block stmt, Environment environment, EnclosingScope scope) {
		for (int i = 0; i != stmt.size(); ++i) {
			visitStatement(stmt.get(i), environment, scope);
		}
	}

	public void visitBreak(Stmt.Break stmt, Environment environment, EnclosingScope scope) {

	}

	public void visitContinue(Stmt.Continue stmt, Environment environment, EnclosingScope scope) {

	}

	public void visitDebug(Stmt.Debug stmt, Environment environment, EnclosingScope scope) {
		// FIXME: Should be Type.Int(0,255)
		Type std_ascii = new Type.Array(Type.Int);
		visitExpression(stmt.getOperand(), std_ascii, environment);
	}

	public void visitDoWhile(Stmt.DoWhile stmt, Environment environment, EnclosingScope scope) {
		visitStatement(stmt.getBody(), environment, scope);
		visitExpression(stmt.getCondition(), Type.Bool, environment);
		visitExpressions(stmt.getInvariant(), Type.Bool, environment);
	}

	public void visitFail(Stmt.Fail stmt, Environment environment, EnclosingScope scope) {

	}

	public void visitIfElse(Stmt.IfElse stmt, Environment environment, EnclosingScope scope) {
		visitExpression(stmt.getCondition(), Type.Bool, environment);
		visitStatement(stmt.getTrueBranch(), environment, scope);
		if (stmt.hasFalseBranch()) {
			visitStatement(stmt.getFalseBranch(), environment, scope);
		}
	}

	public void visitNamedBlock(Stmt.NamedBlock stmt, Environment environment, EnclosingScope scope) {
		// Updated the environment with new within relations
		LifetimeDeclaration enclosing = scope.getEnclosingScope(LifetimeDeclaration.class);
		String[] lifetimes = enclosing.getDeclaredLifetimes();
		environment = environment.declareWithin(stmt.getName().get(), lifetimes);
		// Create an appropriate scope for this block
		scope = new NamedBlockScope(scope, stmt);
		//
		visitStatement(stmt.getBlock(), environment, scope);
	}

	public void visitReturn(Stmt.Return stmt, Environment environment, EnclosingScope scope) {
		Decl.FunctionOrMethod enclosing = stmt.getAncestor(Decl.FunctionOrMethod.class);
		visitExpressions(stmt.getReturns(), enclosing.getType().getReturns(), environment);
	}

	public void visitSkip(Stmt.Skip stmt, Environment environment, EnclosingScope scope) {

	}

	public void visitSwitch(Stmt.Switch stmt, Environment environment, EnclosingScope scope) {
		Type target = stmt.getCondition().getType();
		visitExpression(stmt.getCondition(), target, environment);
		Tuple<Stmt.Case> cases = stmt.getCases();
		for (int i = 0; i != cases.size(); ++i) {
			visitCase(cases.get(i), target, environment, scope);
		}
	}

	public void visitCase(Stmt.Case stmt, Type target, Environment environment, EnclosingScope scope) {
		visitExpressions(stmt.getConditions(), target, environment);
		visitStatement(stmt.getBlock(), environment, scope);
	}

	public void visitWhile(Stmt.While stmt, Environment environment, EnclosingScope scope) {
		visitExpression(stmt.getCondition(), Type.Bool, environment);
		visitExpressions(stmt.getInvariant(), Type.Bool, environment);
		visitStatement(stmt.getBody(), environment, scope);
	}

	public void visitExpressions(Tuple<Expr> exprs, Tuple<Type> targets, Environment environment) {
		for (int i = 0; i != exprs.size(); ++i) {
			visitExpression(exprs.get(i), targets.get(i), environment);
		}
	}

	public void visitExpressions(Tuple<Expr> exprs, Type target, Environment environment) {
		for (int i = 0; i != exprs.size(); ++i) {
			visitExpression(exprs.get(i), target, environment);
		}
	}

	/**
	 * Visit a given expression which is being assigned to a location of a given
	 * type.
	 *
	 * @param expr
	 * @param target
	 */
	public void visitExpression(Expr expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Terminals
		case EXPR_constant:
			visitConstant((Expr.Constant) expr, target, environment);
			break;
		case EXPR_indirectinvoke:
			visitIndirectInvoke((Expr.IndirectInvoke) expr, target, environment);
			break;
		case EXPR_lambdaaccess:
			visitLambdaAccess((Expr.LambdaAccess) expr, target, environment);
			break;
		case DECL_lambda:
			visitLambda((Decl.Lambda) expr, environment);
			break;
		case EXPR_staticvariable:
			visitStaticVariableAccess((Expr.StaticVariableAccess) expr, target, environment);
			break;
		case EXPR_variablecopy:
		case EXPR_variablemove:
			visitVariableAccess((Expr.VariableAccess) expr, target, environment);
			break;
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
			visitUnaryOperator((Expr.UnaryOperator) expr, target, environment);
			break;
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
			visitBinaryOperator((Expr.BinaryOperator) expr, target, environment);
			break;
		// Nary Operators
		case EXPR_logicaland:
		case EXPR_logicalor:
		case EXPR_invoke:
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_arrayinitialiser:
		case EXPR_recordinitialiser:
			visitNaryOperator((Expr.NaryOperator) expr, target, environment);
			break;
		// Ternary Operators
		case EXPR_arrayupdate:
			visitTernaryOperator((Expr.TernaryOperator) expr, target, environment);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitUnaryOperator(Expr.UnaryOperator expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Unary Operators
		case EXPR_cast:
			visitCast((Expr.Cast) expr, target, environment);
			break;
		case EXPR_integernegation: {
			Type.Int intT = selectInt(target, expr, environment);
			visitIntegerNegation((Expr.IntegerNegation) expr, intT, environment);
			break;
		}
		case EXPR_is:
			visitIs((Expr.Is) expr, environment);
			break;
		case EXPR_logicalnot:
			visitLogicalNot((Expr.LogicalNot) expr, environment);
			break;
		case EXPR_logicalexistential:
			visitExistentialQuantifier((Expr.ExistentialQuantifier) expr, environment);
			break;
		case EXPR_logicaluniversal:
			visitUniversalQuantifier((Expr.UniversalQuantifier) expr, environment);
			break;
		case EXPR_bitwisenot:
			visitBitwiseComplement((Expr.BitwiseComplement) expr, environment);
			break;
		case EXPR_dereference:
			visitDereference((Expr.Dereference) expr, target, environment);
			break;
		case EXPR_staticnew:
		case EXPR_new: {
			Type.Reference refT = selectReference(target, expr, environment);
			visitNew((Expr.New) expr, refT, environment);
			break;
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			visitRecordAccess((Expr.RecordAccess) expr, target, environment);
			break;
		case EXPR_arraylength: {
			Type.Int intT = selectInt(target, expr, environment);
			visitArrayLength((Expr.ArrayLength) expr, intT, environment);
			break;
		}
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitBinaryOperator(Expr.BinaryOperator expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Binary Operators
		case EXPR_equal:
			visitEqual((Expr.Equal) expr, environment);
			break;
		case EXPR_notequal:
			visitNotEqual((Expr.NotEqual) expr, environment);
			break;
		case EXPR_logiaclimplication:
			visitLogicalImplication((Expr.LogicalImplication) expr, environment);
			break;
		case EXPR_logicaliff:
			visitLogicalIff((Expr.LogicalIff) expr, environment);
			break;
		case EXPR_integerlessthan:
			visitIntegerLessThan((Expr.IntegerLessThan) expr, environment);
			break;
		case EXPR_integerlessequal:
			visitIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr, environment);
			break;
		case EXPR_integergreaterthan:
			visitIntegerGreaterThan((Expr.IntegerGreaterThan) expr, environment);
			break;
		case EXPR_integergreaterequal:
			visitIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr, environment);
			break;
		case EXPR_integeraddition: {
			Type.Int intT = selectInt(target, expr, environment);
			visitIntegerAddition((Expr.IntegerAddition) expr, intT, environment);
			break;
		}
		case EXPR_integersubtraction: {
			Type.Int intT = selectInt(target, expr, environment);
			visitIntegerSubtraction((Expr.IntegerSubtraction) expr, intT, environment);
			break;
		}
		case EXPR_integermultiplication: {
			Type.Int intT = selectInt(target, expr, environment);
			visitIntegerMultiplication((Expr.IntegerMultiplication) expr, intT, environment);
			break;
		}
		case EXPR_integerdivision: {
			Type.Int intT = selectInt(target, expr, environment);
			visitIntegerDivision((Expr.IntegerDivision) expr, intT, environment);
			break;
		}
		case EXPR_integerremainder: {
			Type.Int intT = selectInt(target, expr, environment);
			visitIntegerRemainder((Expr.IntegerRemainder) expr, intT, environment);
			break;
		}
		case EXPR_bitwiseshl:
			visitBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr, environment);
			break;
		case EXPR_bitwiseshr:
			visitBitwiseShiftRight((Expr.BitwiseShiftRight) expr, environment);
			break;
		case EXPR_arraygenerator: {
			Type.Array arrayT = selectArray(target, expr, environment);
			visitArrayGenerator((Expr.ArrayGenerator) expr, arrayT, environment);
			break;
		}
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			visitArrayAccess((Expr.ArrayAccess) expr, target, environment);
			break;
		case EXPR_arrayrange: {
			Type.Array arrayT = selectArray(target, expr, environment);
			visitArrayRange((Expr.ArrayRange) expr, arrayT, environment);
			break;
		}
		case EXPR_recordupdate:
			Type.Record recordT = selectRecord(target, expr, environment);
			visitRecordUpdate((Expr.RecordUpdate) expr, recordT, environment);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitTernaryOperator(Expr.TernaryOperator expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Ternary Operators
		case EXPR_arrayupdate:
			Type.Array arrayT = selectArray(target, expr, environment);
			visitArrayUpdate((Expr.ArrayUpdate) expr, arrayT, environment);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitNaryOperator(Expr.NaryOperator expr, Type target, Environment environment) {
		switch (expr.getOpcode()) {
		// Nary Operators
		case EXPR_arrayinitialiser:
			Type.Array arrayT = selectArray(target, expr, environment);
			visitArrayInitialiser((Expr.ArrayInitialiser) expr, arrayT, environment);
			break;
		case EXPR_bitwiseand:
			visitBitwiseAnd((Expr.BitwiseAnd) expr, environment);
			break;
		case EXPR_bitwiseor:
			visitBitwiseOr((Expr.BitwiseOr) expr, environment);
			break;
		case EXPR_bitwisexor:
			visitBitwiseXor((Expr.BitwiseXor) expr, environment);
			break;
		case EXPR_invoke:
			visitInvoke((Expr.Invoke) expr, target, environment);
			break;
		case EXPR_logicaland:
			visitLogicalAnd((Expr.LogicalAnd) expr, environment);
			break;
		case EXPR_logicalor:
			visitLogicalOr((Expr.LogicalOr) expr, environment);
			break;
		case EXPR_recordinitialiser:
			Type.Record recordT = selectRecord(target, expr, environment);
			visitRecordInitialiser((Expr.RecordInitialiser) expr, recordT, environment);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitArrayAccess(Expr.ArrayAccess expr, Type target, Environment environment) {
		visitExpression(expr.getFirstOperand(), expr.getFirstOperand().getType(), environment);
		visitExpression(expr.getSecondOperand(), Type.Int, environment);
	}

	public void visitArrayLength(Expr.ArrayLength expr, Type.Int target, Environment environment) {
		visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
	}

	public void visitArrayGenerator(Expr.ArrayGenerator expr, Type.Array target, Environment environment) {
		visitExpression(expr.getFirstOperand(), target.getElement(), environment);
		visitExpression(expr.getSecondOperand(), Type.Int, environment);
	}

	public void visitArrayInitialiser(Expr.ArrayInitialiser expr, Type.Array target, Environment environment) {
		visitExpressions(expr.getOperands(), target.getElement(), environment);
	}

	public void visitArrayRange(Expr.ArrayRange expr, Type.Array target, Environment environment) {
		visitExpression(expr.getFirstOperand(), Type.Int, environment);
		visitExpression(expr.getSecondOperand(), Type.Int, environment);
	}

	public void visitArrayUpdate(Expr.ArrayUpdate expr, Type.Array target, Environment environment) {
		visitExpression(expr.getFirstOperand(), target, environment);
		visitExpression(expr.getSecondOperand(), Type.Int, environment);
		visitExpression(expr.getThirdOperand(), target.getElement(), environment);
	}

	public void visitBitwiseComplement(Expr.BitwiseComplement expr, Environment environment) {
		visitExpression(expr.getOperand(), Type.Byte, environment);
	}

	public void visitBitwiseAnd(Expr.BitwiseAnd expr, Environment environment) {
		visitExpressions(expr.getOperands(), Type.Byte, environment);
	}

	public void visitBitwiseOr(Expr.BitwiseOr expr, Environment environment) {
		visitExpressions(expr.getOperands(), Type.Byte, environment);
	}

	public void visitBitwiseXor(Expr.BitwiseXor expr, Environment environment) {
		visitExpressions(expr.getOperands(), Type.Byte, environment);
	}

	public void visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, Environment environment) {
		visitExpression(expr.getFirstOperand(), Type.Byte, environment);
		visitExpression(expr.getSecondOperand(), Type.Int, environment);
	}

	public void visitBitwiseShiftRight(Expr.BitwiseShiftRight expr, Environment environment) {
		visitExpression(expr.getFirstOperand(), Type.Byte, environment);
		visitExpression(expr.getSecondOperand(), Type.Int, environment);
	}

	public void visitCast(Expr.Cast expr, Type target, Environment environment) {
		visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
	}

	public void visitConstant(Expr.Constant expr, Type target, Environment environment) {

	}

	public void visitDereference(Expr.Dereference expr, Type target, Environment environment) {
		visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
	}

	public void visitEqual(Expr.Equal expr, Environment environment) {
		visitExpression(expr.getFirstOperand(), expr.getFirstOperand().getType(), environment);
		visitExpression(expr.getSecondOperand(), expr.getSecondOperand().getType(), environment);
	}

	public void visitIntegerLessThan(Expr.IntegerLessThan expr, Environment environment) {
		visitExpression(expr.getFirstOperand(), Type.Int, environment);
		visitExpression(expr.getSecondOperand(), Type.Int, environment);
	}

	public void visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, Environment environment) {
		visitExpression(expr.getFirstOperand(), Type.Int, environment);
		visitExpression(expr.getSecondOperand(), Type.Int, environment);
	}

	public void visitIntegerGreaterThan(Expr.IntegerGreaterThan expr, Environment environment) {
		visitExpression(expr.getFirstOperand(), Type.Int, environment);
		visitExpression(expr.getSecondOperand(), Type.Int, environment);
	}

	public void visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, Environment environment) {
		visitExpression(expr.getFirstOperand(), Type.Int, environment);
		visitExpression(expr.getSecondOperand(), Type.Int, environment);
	}

	public void visitIntegerNegation(Expr.IntegerNegation expr, Type.Int target, Environment environment) {
		visitExpression(expr.getOperand(), target, environment);
	}

	public void visitIntegerAddition(Expr.IntegerAddition expr, Type.Int target, Environment environment) {
		visitExpression(expr.getFirstOperand(), target, environment);
		visitExpression(expr.getSecondOperand(), target, environment);
	}

	public void visitIntegerSubtraction(Expr.IntegerSubtraction expr, Type.Int target, Environment environment) {
		visitExpression(expr.getFirstOperand(), target, environment);
		visitExpression(expr.getSecondOperand(), target, environment);
	}

	public void visitIntegerMultiplication(Expr.IntegerMultiplication expr, Type.Int target, Environment environment) {
		visitExpression(expr.getFirstOperand(), target, environment);
		visitExpression(expr.getSecondOperand(), target, environment);
	}

	public void visitIntegerDivision(Expr.IntegerDivision expr, Type.Int target, Environment environment) {
		visitExpression(expr.getFirstOperand(), target, environment);
		visitExpression(expr.getSecondOperand(), target, environment);
	}

	public void visitIntegerRemainder(Expr.IntegerRemainder expr, Type.Int target, Environment environment) {
		visitExpression(expr.getFirstOperand(), target, environment);
		visitExpression(expr.getSecondOperand(), target, environment);
	}

	public void visitIs(Expr.Is expr, Environment environment) {
		visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
	}

	public void visitLogicalAnd(Expr.LogicalAnd expr, Environment environment) {
		visitExpressions(expr.getOperands(), Type.Bool, environment);
	}

	public void visitLogicalImplication(Expr.LogicalImplication expr, Environment environment) {
		visitExpression(expr.getFirstOperand(), Type.Bool, environment);
		visitExpression(expr.getSecondOperand(), Type.Bool, environment);
	}

	public void visitLogicalIff(Expr.LogicalIff expr, Environment environment) {
		visitExpression(expr.getFirstOperand(), Type.Bool, environment);
		visitExpression(expr.getSecondOperand(), Type.Bool, environment);
	}

	public void visitLogicalNot(Expr.LogicalNot expr, Environment environment) {
		visitExpression(expr.getOperand(), Type.Bool, environment);
	}

	public void visitLogicalOr(Expr.LogicalOr expr, Environment environment) {
		visitExpressions(expr.getOperands(), Type.Bool, environment);
	}

	public void visitExistentialQuantifier(Expr.ExistentialQuantifier expr, Environment environment) {
		Tuple<Decl.Variable> parameters = expr.getParameters();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.Variable parameter = parameters.get(i);
			visitExpression(parameter.getInitialiser(), TYPE_ARRAY_INT, environment);
		}
		visitExpression(expr.getOperand(), Type.Bool, environment);
	}

	public void visitUniversalQuantifier(Expr.UniversalQuantifier expr, Environment environment) {
		Tuple<Decl.Variable> parameters = expr.getParameters();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.Variable parameter = parameters.get(i);
			visitExpression(parameter.getInitialiser(), TYPE_ARRAY_INT, environment);
		}
		visitExpression(expr.getOperand(), Type.Bool, environment);
	}

	public void visitInvoke(Expr.Invoke expr, Type target, Environment environment) {
		Type.Callable signature = expr.getSignature();
		Tuple<Type> parameters = signature.getParameters();
		if(signature instanceof Type.Method) {
			// Must bind lifetime arguments
			Decl.Method decl = resolveMethod(expr.getName(), (Type.Method) expr.getSignature());
			parameters = bind(decl,expr.getLifetimes());
		}
		visitExpressions(expr.getOperands(), parameters, environment);
	}

	public void visitIndirectInvoke(Expr.IndirectInvoke expr, Type target, Environment environment) {
		Type.Callable sourceT = asType(expr.getSource().getType(), Type.Callable.class);
		visitExpression(expr.getSource(), sourceT, environment);
		visitExpressions(expr.getArguments(), sourceT.getParameters(), environment);
	}

	public void visitLambdaAccess(Expr.LambdaAccess expr, Type type, Environment environment) {

	}

	public void visitNew(Expr.New expr, Type.Reference target, Environment environment) {
		visitExpression(expr.getOperand(), target.getElement(), environment);
	}

	public void visitNotEqual(Expr.NotEqual expr, Environment environment) {
		visitExpression(expr.getFirstOperand(), expr.getFirstOperand().getType(), environment);
		visitExpression(expr.getSecondOperand(), expr.getSecondOperand().getType(), environment);
	}

	public void visitRecordAccess(Expr.RecordAccess expr, Type target, Environment environment) {
		visitExpression(expr.getOperand(), expr.getOperand().getType(), environment);
	}

	public void visitRecordInitialiser(Expr.RecordInitialiser expr, Type.Record target, Environment environment) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		for (int i = 0; i != fields.size(); ++i) {
			Expr operand = operands.get(i);
			Type type = target.getField(fields.get(i));
			if (type == null) {
				// NOTE: open records may not have concrete types for fields.
				type = operand.getType();
			}
			visitExpression(operand, type, environment);
		}
	}

	public void visitRecordUpdate(Expr.RecordUpdate expr, Type.Record target, Environment environment) {
		visitExpression(expr.getFirstOperand(), target, environment);
		visitExpression(expr.getSecondOperand(), target.getField(expr.getField()), environment);
	}

	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, Type target, Environment environment) {

	}

	public void visitVariableAccess(Expr.VariableAccess expr, Type target, Environment environment) {

	}

	public void visitTypes(Tuple<Type> type) {
		for (int i = 0; i != type.size(); ++i) {
			visitType(type.get(i));
		}
	}

	public void visitType(Type type) {
		switch (type.getOpcode()) {
		case TYPE_array:
			visitTypeArray((Type.Array) type);
			break;
		case TYPE_bool:
			visitTypeBool((Type.Bool) type);
			break;
		case TYPE_byte:
			visitTypeByte((Type.Byte) type);
			break;
		case TYPE_int:
			visitTypeInt((Type.Int) type);
			break;
		case TYPE_nominal:
			visitTypeNominal((Type.Nominal) type);
			break;
		case TYPE_null:
			visitTypeNull((Type.Null) type);
			break;
		case TYPE_record:
			visitTypeRecord((Type.Record) type);
			break;
		case TYPE_staticreference:
		case TYPE_reference:
			visitTypeReference((Type.Reference) type);
			break;
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
			visitTypeCallable((Type.Callable) type);
			break;
		case TYPE_union:
			visitTypeUnion((Type.Union) type);
			break;
		case TYPE_unresolved:
			visitTypeUnresolved((Type.Unresolved) type);
			break;
		case TYPE_void:
			visitTypeVoid((Type.Void) type);
			break;
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public void visitTypeCallable(Type.Callable type) {
		switch (type.getOpcode()) {
		case TYPE_function:
			visitTypeFunction((Type.Function) type);
			break;
		case TYPE_method:
			visitTypeMethod((Type.Method) type);
			break;
		case TYPE_property:
			visitTypeProperty((Type.Property) type);
			break;
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public void visitTypeArray(Type.Array type) {
		visitType(type.getElement());
	}

	public void visitTypeBool(Type.Bool type) {

	}

	public void visitTypeByte(Type.Byte type) {

	}

	public void visitTypeFunction(Type.Function type) {
		visitTypes(type.getParameters());
		visitTypes(type.getReturns());
	}

	public void visitTypeInt(Type.Int type) {

	}

	public void visitTypeMethod(Type.Method type) {
		visitTypes(type.getParameters());
		visitTypes(type.getReturns());
	}

	public void visitTypeNominal(Type.Nominal type) {

	}

	public void visitTypeNull(Type.Null type) {

	}

	public void visitTypeProperty(Type.Property type) {
		visitTypes(type.getParameters());
		visitTypes(type.getReturns());
	}

	public void visitTypeRecord(Type.Record type) {
		visitFields(type.getFields());
	}

	public void visitFields(Tuple<Type.Field> fields) {
		for (int i = 0; i != fields.size(); ++i) {
			visitField(fields.get(i));
		}
	}

	public void visitField(Type.Field field) {
		visitType(field.getType());
	}

	public void visitTypeReference(Type.Reference type) {
		visitType(type.getElement());
	}

	public void visitTypeUnion(Type.Union type) {
		for (int i = 0; i != type.size(); ++i) {
			visitType(type.get(i));
		}
	}

	public void visitTypeUnresolved(Type.Unresolved type) {

	}

	public void visitTypeVoid(Type.Void type) {

	}

	public void visitSemanticType(SemanticType type) {
		switch (type.getOpcode()) {
		case SEMTYPE_array:
			visitSemanticTypeArray((SemanticType.Array) type);
			break;
		case SEMTYPE_record:
			visitSemanticTypeRecord((SemanticType.Record) type);
			break;
		case SEMTYPE_staticreference:
		case SEMTYPE_reference:
			visitSemanticTypeReference((SemanticType.Reference) type);
			break;
		case SEMTYPE_union:
			visitSemanticTypeUnion((SemanticType.Union) type);
			break;
		case SEMTYPE_intersection:
			visitSemanticTypeIntersection((SemanticType.Intersection) type);
			break;
		case SEMTYPE_difference:
			visitSemanticTypeDifference((SemanticType.Difference) type);
			break;
		default:
			// Handle leaf cases
			visitType((Type) type);
		}
	}

	public void visitSemanticTypeArray(SemanticType.Array type) {
		visitSemanticType(type.getElement());
	}

	public void visitSemanticTypeRecord(SemanticType.Record type) {
		for (SemanticType.Field f : type.getFields()) {
			visitSemanticType(f.getType());
		}
	}

	public void visitSemanticTypeReference(SemanticType.Reference type) {
		visitSemanticType(type.getElement());
	}

	public void visitSemanticTypeUnion(SemanticType.Union type) {
		for (SemanticType t : type.getAll()) {
			visitSemanticType(t);
		}
	}

	public void visitSemanticTypeIntersection(SemanticType.Intersection type) {
		for (SemanticType t : type.getAll()) {
			visitSemanticType(t);
		}
	}

	public void visitSemanticTypeDifference(SemanticType.Difference type) {
		visitSemanticType(type.getLeftHandSide());
		visitSemanticType(type.getRightHandSide());
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
	  // FIXME: where should this call be done?
	  candidates = ArrayUtils.removeDuplicates(candidates);
	  //
		try {
			T candidate = null;
			for (int i = 0; i != candidates.length; ++i) {
				T next = candidates[i];
				if (subtypeOperator.isSubtype(next, actual, environment)) {
					if (candidate == null) {
						candidate = next;
					} else {
						candidate = selectCandidate(candidate, next, environment);
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
	public <T extends Type> T selectCandidate(T candidate, T next, Environment environment) throws ResolutionError {
		// Found a viable candidate
		boolean left = subtypeOperator.isSubtype(candidate, next, environment);
		boolean right = subtypeOperator.isSubtype(next, candidate, environment);
		if (left && !right) {
			// Yes, is better than current candidate
			return next;
		} else if (right && !left) {
			return candidate;
		}
		return null;
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
