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

import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.Type;

import static wyc.lang.WhileyFile.*;

import wybs.util.AbstractCompilationUnit.Tuple;

/**
 * A simple visitor over all declarations, statements, expressions and types in
 * a given WhileyFile which consumes one data parameter and returns one value.
 * The intention is that this is extended as necessary to provide custom
 * functionality.
 *
 * @author David J. Pearce
 *
 */
public abstract class AbstractFunction<P,R> {

	public R visitWhileyFile(WhileyFile wf, P data) {
		for (Decl decl : wf.getDeclarations()) {
			visitDeclaration(decl, data);
		}
		return null;
	}

	public R visitDeclaration(Decl decl, P data) {
		switch (decl.getOpcode()) {
		case DECL_importfrom:
		case DECL_import:
			return visitImport((Decl.Import) decl, data);
		case DECL_staticvar:
			return visitStaticVariable((Decl.StaticVariable) decl, data);
		case DECL_type:
			return visitType((Decl.Type) decl, data);
		case DECL_function:
		case DECL_method:
		case DECL_property:
			return visitCallable((Decl.Callable) decl, data);
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public R visitImport(Decl.Import decl, P data) {
		return null;
	}


	public R visitLambda(Decl.Lambda decl, P data) {
		visitVariables(decl.getParameters(), data);
		visitExpression(decl.getBody(), data);
		return null;
	}

	public R visitVariables(Tuple<Decl.Variable> vars, P data) {
		for(int i=0;i!=vars.size();++i) {
			Decl.Variable var = vars.get(i);
			visitVariable(var, data);
		}
		return null;
	}

	public R visitVariable(Decl.Variable decl, P data) {
		visitType(decl.getType(), data);
		if(decl.hasInitialiser()) {
			visitExpression(decl.getInitialiser(), data);
		}
		return null;
	}

	public R visitStaticVariable(Decl.StaticVariable decl, P data) {
		visitType(decl.getType(), data);
		if (decl.hasInitialiser()) {
			visitExpression(decl.getInitialiser(), data);
		}
		return null;
	}

	public R visitType(Decl.Type decl, P data) {
		visitVariable(decl.getVariableDeclaration(), data);
		visitExpressions(decl.getInvariant(), data);
		return null;
	}

	public R visitCallable(Decl.Callable decl, P data) {
		switch (decl.getOpcode()) {
		case DECL_function:
		case DECL_method:
			return visitFunctionOrMethod((Decl.FunctionOrMethod) decl, data);
		case DECL_property:
			return visitProperty((Decl.Property) decl, data);
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public R visitFunctionOrMethod(Decl.FunctionOrMethod decl, P data) {
		switch (decl.getOpcode()) {
		case DECL_function:
			return visitFunction((Decl.Function) decl, data);
		case DECL_method:
			return visitMethod((Decl.Method) decl, data);
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public R visitProperty(Decl.Property decl, P data) {
		visitVariables(decl.getParameters(), data);
		visitVariables(decl.getReturns(), data);
		visitExpressions(decl.getInvariant(), data);
		return null;
	}

	public R visitFunction(Decl.Function decl, P data) {
		visitVariables(decl.getParameters(), data);
		visitVariables(decl.getReturns(), data);
		visitExpressions(decl.getRequires(), data);
		visitExpressions(decl.getEnsures(), data);
		visitStatement(decl.getBody(), data);
		return null;
	}

	public R visitMethod(Decl.Method decl, P data) {
		visitVariables(decl.getParameters(), data);
		visitVariables(decl.getReturns(), data);
		visitExpressions(decl.getRequires(), data);
		visitExpressions(decl.getEnsures(), data);
		visitStatement(decl.getBody(), data);
		return null;
	}

	public R visitStatement(Stmt stmt, P data) {
		switch (stmt.getOpcode()) {
		case DECL_variable:
		case DECL_variableinitialiser:
			return visitVariable((Decl.Variable) stmt, data);
		case STMT_assert:
			return visitAssert((Stmt.Assert) stmt, data);
		case STMT_assign:
			return visitAssign((Stmt.Assign) stmt, data);
		case STMT_assume:
			return visitAssume((Stmt.Assume) stmt, data);
		case STMT_block:
			return visitBlock((Stmt.Block) stmt, data);
		case STMT_break:
			return visitBreak((Stmt.Break) stmt, data);
		case STMT_continue:
			return visitContinue((Stmt.Continue) stmt, data);
		case STMT_debug:
			return visitDebug((Stmt.Debug) stmt, data);
		case STMT_dowhile:
			return visitDoWhile((Stmt.DoWhile) stmt, data);
		case STMT_fail:
			return visitFail((Stmt.Fail) stmt, data);
		case STMT_if:
		case STMT_ifelse:
			return visitIfElse((Stmt.IfElse) stmt, data);
		case EXPR_invoke:
			return visitInvoke((Expr.Invoke) stmt, data);
		case EXPR_indirectinvoke:
			return visitIndirectInvoke((Expr.IndirectInvoke) stmt, data);
		case STMT_namedblock:
			return visitNamedBlock((Stmt.NamedBlock) stmt, data);
		case STMT_return:
			return visitReturn((Stmt.Return) stmt, data);
		case STMT_skip:
			return visitSkip((Stmt.Skip) stmt, data);
		case STMT_switch:
			return visitSwitch((Stmt.Switch) stmt, data);
		case STMT_while:
			return visitWhile((Stmt.While) stmt, data);
		default:
			throw new IllegalArgumentException("unknown statement encountered (" + stmt.getClass().getName() + ")");
		}
	}

	public R visitAssert(Stmt.Assert stmt, P data) {
		visitExpression(stmt.getCondition(), data);
		return null;
	}


	public R visitAssign(Stmt.Assign stmt, P data) {
		visitLVals(stmt.getLeftHandSide(), data);
		visitExpressions(stmt.getRightHandSide(), data);
		return null;
	}

	public R visitLVals(Tuple<LVal> lvals, P data) {
		for(int i=0;i!=lvals.size();++i) {
			visitExpression(lvals.get(i), data);
		}
		return null;
	}

	public R visitAssume(Stmt.Assume stmt, P data) {
		visitExpression(stmt.getCondition(), data);
		return null;
	}

	public R visitBlock(Stmt.Block stmt, P data) {
		for(int i=0;i!=stmt.size();++i) {
			visitStatement(stmt.get(i), data);
		}
		return null;
	}

	public R visitBreak(Stmt.Break stmt, P data) {
		return null;
	}

	public R visitContinue(Stmt.Continue stmt, P data) {
		return null;
	}

	public R visitDebug(Stmt.Debug stmt, P data) {
		visitExpression(stmt.getOperand(), data);
		return null;
	}

	public R visitDoWhile(Stmt.DoWhile stmt, P data) {
		visitStatement(stmt.getBody(), data);
		visitExpression(stmt.getCondition(), data);
		visitExpressions(stmt.getInvariant(), data);
		return null;
	}

	public R visitFail(Stmt.Fail stmt, P data) {
		return null;
	}

	public R visitIfElse(Stmt.IfElse stmt, P data) {
		visitExpression(stmt.getCondition(), data);
		visitStatement(stmt.getTrueBranch(), data);
		if(stmt.hasFalseBranch()) {
			visitStatement(stmt.getFalseBranch(), data);
		}
		return null;
	}

	public R visitNamedBlock(Stmt.NamedBlock stmt, P data) {
		visitStatement(stmt.getBlock(), data);
		return null;
	}

	public R visitReturn(Stmt.Return stmt, P data) {
		visitExpressions(stmt.getReturns(), data);
		return null;
	}

	public R visitSkip(Stmt.Skip stmt, P data) {
		return null;
	}

	public R visitSwitch(Stmt.Switch stmt, P data) {
		visitExpression(stmt.getCondition(), data);
		Tuple<Stmt.Case> cases = stmt.getCases();
		for(int i=0;i!=cases.size();++i) {
			visitCase(cases.get(i), data);
		}
		return null;
	}

	public R visitCase(Stmt.Case stmt, P data) {
		visitExpressions(stmt.getConditions(), data);
		visitStatement(stmt.getBlock(), data);
		return null;
	}

	public R visitWhile(Stmt.While stmt, P data) {
		visitExpression(stmt.getCondition(), data);
		visitExpressions(stmt.getInvariant(), data);
		visitStatement(stmt.getBody(), data);
		return null;
	}

	public R visitExpressions(Tuple<Expr> exprs, P data) {
		for (int i = 0; i != exprs.size(); ++i) {
			visitExpression(exprs.get(i), data);
		}
		return null;
	}

	public R visitExpression(Expr expr, P data) {
		switch (expr.getOpcode()) {
		// Terminals
		case EXPR_constant:
			return visitConstant((Expr.Constant) expr, data);
		case EXPR_indirectinvoke:
			return visitIndirectInvoke((Expr.IndirectInvoke) expr, data);
		case EXPR_lambdaaccess:
			return visitLambdaAccess((Expr.LambdaAccess) expr, data);
		case DECL_lambda:
			return visitLambda((Decl.Lambda) expr, data);
		case EXPR_staticvariable:
			return visitStaticVariableAccess((Expr.StaticVariableAccess) expr, data);
		case EXPR_variablecopy:
		case EXPR_variablemove:
			return visitVariableAccess((Expr.VariableAccess) expr, data);
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
			return visitUnaryOperator((Expr.UnaryOperator) expr, data);
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
			return visitBinaryOperator((Expr.BinaryOperator) expr, data);
		// Nary Operators
		case EXPR_logicaland:
		case EXPR_logicalor:
		case EXPR_invoke:
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_arrayinitialiser:
		case EXPR_recordinitialiser:
			return visitNaryOperator((Expr.NaryOperator) expr, data);
		// Ternary Operators
		case EXPR_arrayupdate:
			return visitTernaryOperator((Expr.TernaryOperator) expr, data);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public R visitUnaryOperator(Expr.UnaryOperator expr, P data) {
		switch (expr.getOpcode()) {
		// Unary Operators
		case EXPR_cast:
			return visitCast((Expr.Cast) expr, data);
		case EXPR_integernegation:
			return visitIntegerNegation((Expr.IntegerNegation) expr, data);
		case EXPR_is:
			return visitIs((Expr.Is) expr, data);
		case EXPR_logicalnot:
			return visitLogicalNot((Expr.LogicalNot) expr, data);
		case EXPR_logicalexistential:
			return visitExistentialQuantifier((Expr.ExistentialQuantifier) expr, data);
		case EXPR_logicaluniversal:
			return visitUniversalQuantifier((Expr.UniversalQuantifier) expr, data);
		case EXPR_bitwisenot:
			return visitBitwiseComplement((Expr.BitwiseComplement) expr, data);
		case EXPR_dereference:
			return visitDereference((Expr.Dereference) expr, data);
		case EXPR_staticnew:
		case EXPR_new:
			return visitNew((Expr.New) expr, data);
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			return visitRecordAccess((Expr.RecordAccess) expr, data);
		case EXPR_arraylength:
			return visitArrayLength((Expr.ArrayLength) expr, data);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public R visitBinaryOperator(Expr.BinaryOperator expr, P data) {
		switch (expr.getOpcode()) {
		// Binary Operators
		case EXPR_equal:
			return visitEqual((Expr.Equal) expr, data);
		case EXPR_notequal:
			return visitNotEqual((Expr.NotEqual) expr, data);
		case EXPR_logiaclimplication:
			return visitLogicalImplication((Expr.LogicalImplication) expr, data);
		case EXPR_logicaliff:
			return visitLogicalIff((Expr.LogicalIff) expr, data);
		case EXPR_integerlessthan:
			return visitIntegerLessThan((Expr.IntegerLessThan) expr, data);
		case EXPR_integerlessequal:
			return visitIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr, data);
		case EXPR_integergreaterthan:
			return visitIntegerGreaterThan((Expr.IntegerGreaterThan) expr, data);
		case EXPR_integergreaterequal:
			return visitIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr, data);
		case EXPR_integeraddition:
			return visitIntegerAddition((Expr.IntegerAddition) expr, data);
		case EXPR_integersubtraction:
			return visitIntegerSubtraction((Expr.IntegerSubtraction) expr, data);
		case EXPR_integermultiplication:
			return visitIntegerMultiplication((Expr.IntegerMultiplication) expr, data);
		case EXPR_integerdivision:
			return visitIntegerDivision((Expr.IntegerDivision) expr, data);
		case EXPR_integerremainder:
			return visitIntegerRemainder((Expr.IntegerRemainder) expr, data);
		case EXPR_bitwiseshl:
			return visitBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr, data);
		case EXPR_bitwiseshr:
			return visitBitwiseShiftRight((Expr.BitwiseShiftRight) expr, data);
		case EXPR_arraygenerator:
			return visitArrayGenerator((Expr.ArrayGenerator) expr, data);
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			return visitArrayAccess((Expr.ArrayAccess) expr, data);
		case EXPR_arrayrange:
			return visitArrayRange((Expr.ArrayRange) expr, data);
		case EXPR_recordupdate:
			return visitRecordUpdate((Expr.RecordUpdate) expr, data);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public R visitTernaryOperator(Expr.TernaryOperator expr, P data) {
		switch (expr.getOpcode()) {
		// Ternary Operators
		case EXPR_arrayupdate:
			return visitArrayUpdate((Expr.ArrayUpdate) expr, data);

		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public R visitNaryOperator(Expr.NaryOperator expr, P data) {
		switch (expr.getOpcode()) {
		// Nary Operators
		case EXPR_arrayinitialiser:
			return visitArrayInitialiser((Expr.ArrayInitialiser) expr, data);
		case EXPR_bitwiseand:
			return visitBitwiseAnd((Expr.BitwiseAnd) expr, data);
		case EXPR_bitwiseor:
			return visitBitwiseOr((Expr.BitwiseOr) expr, data);
		case EXPR_bitwisexor:
			return visitBitwiseXor((Expr.BitwiseXor) expr, data);
		case EXPR_invoke:
			return visitInvoke((Expr.Invoke) expr, data);
		case EXPR_logicaland:
			return visitLogicalAnd((Expr.LogicalAnd) expr, data);
		case EXPR_logicalor:
			return visitLogicalOr((Expr.LogicalOr) expr, data);
		case EXPR_recordinitialiser:
			return visitRecordInitialiser((Expr.RecordInitialiser) expr, data);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public R visitArrayAccess(Expr.ArrayAccess expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitArrayLength(Expr.ArrayLength expr, P data) {
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitArrayGenerator(Expr.ArrayGenerator expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitArrayInitialiser(Expr.ArrayInitialiser expr, P data) {
		visitExpressions(expr.getOperands(), data);
		return null;
	}

	public R visitArrayRange(Expr.ArrayRange expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitArrayUpdate(Expr.ArrayUpdate expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		visitExpression(expr.getThirdOperand(), data);
		return null;
	}

	public R visitBitwiseComplement(Expr.BitwiseComplement expr, P data) {
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitBitwiseAnd(Expr.BitwiseAnd expr, P data) {
		visitExpressions(expr.getOperands(), data);
		return null;
	}

	public R visitBitwiseOr(Expr.BitwiseOr expr, P data) {
		visitExpressions(expr.getOperands(), data);
		return null;
	}

	public R visitBitwiseXor(Expr.BitwiseXor expr, P data) {
		visitExpressions(expr.getOperands(), data);
		return null;
	}

	public R visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitBitwiseShiftRight(Expr.BitwiseShiftRight expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitCast(Expr.Cast expr, P data) {
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitConstant(Expr.Constant expr, P data) {

		return null;
	}

	public R visitDereference(Expr.Dereference expr, P data) {
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitEqual(Expr.Equal expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitIntegerLessThan(Expr.IntegerLessThan expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitIntegerGreaterThan(Expr.IntegerGreaterThan expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitIntegerNegation(Expr.IntegerNegation expr, P data) {
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitIntegerAddition(Expr.IntegerAddition expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitIntegerSubtraction(Expr.IntegerSubtraction expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitIntegerMultiplication(Expr.IntegerMultiplication expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitIntegerDivision(Expr.IntegerDivision expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitIntegerRemainder(Expr.IntegerRemainder expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitIs(Expr.Is expr, P data) {
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitLogicalAnd(Expr.LogicalAnd expr, P data) {
		visitExpressions(expr.getOperands(), data);
		return null;
	}

	public R visitLogicalImplication(Expr.LogicalImplication expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitLogicalIff(Expr.LogicalIff expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitLogicalNot(Expr.LogicalNot expr, P data) {
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitLogicalOr(Expr.LogicalOr expr, P data) {
		visitExpressions(expr.getOperands(), data);
		return null;
	}

	public R visitExistentialQuantifier(Expr.ExistentialQuantifier expr, P data) {
		visitVariables(expr.getParameters(), data);
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitUniversalQuantifier(Expr.UniversalQuantifier expr, P data) {
		visitVariables(expr.getParameters(), data);
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitInvoke(Expr.Invoke expr, P data) {
		visitExpressions(expr.getOperands(), data);
		return null;
	}

	public R visitIndirectInvoke(Expr.IndirectInvoke expr, P data) {
		visitExpression(expr.getSource(), data);
		visitExpressions(expr.getArguments(), data);
		return null;
	}

	public R visitLambdaAccess(Expr.LambdaAccess expr, P data) {

		return null;
	}

	public R visitNew(Expr.New expr, P data) {
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitNotEqual(Expr.NotEqual expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitRecordAccess(Expr.RecordAccess expr, P data) {
		visitExpression(expr.getOperand(), data);
		return null;
	}

	public R visitRecordInitialiser(Expr.RecordInitialiser expr, P data) {
		visitExpressions(expr.getOperands(), data);
		return null;
	}

	public R visitRecordUpdate(Expr.RecordUpdate expr, P data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		return null;
	}

	public R visitStaticVariableAccess(Expr.StaticVariableAccess expr, P data) {

		return null;
	}

	public R visitVariableAccess(Expr.VariableAccess expr, P data) {

		return null;
	}

	public R visitTypes(Tuple<Type> type, P data) {
		for (int i = 0; i != type.size(); ++i) {
			visitType(type.get(i), data);
			return null;
		}
		return null;
	}

	public R visitType(Type type, P data) {
		switch (type.getOpcode()) {
		case TYPE_array:
			return visitTypeArray((Type.Array) type, data);
		case TYPE_bool:
			return visitTypeBool((Type.Bool) type, data);
		case TYPE_byte:
			return visitTypeByte((Type.Byte) type, data);
		case TYPE_int:
			return visitTypeInt((Type.Int) type, data);
		case TYPE_nominal:
			return visitTypeNominal((Type.Nominal) type, data);
		case TYPE_null:
			return visitTypeNull((Type.Null) type, data);
		case TYPE_record:
			return visitTypeRecord((Type.Record) type, data);
		case TYPE_reference:
			return visitTypeReference((Type.Reference) type, data);
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
			return visitTypeCallable((Type.Callable) type, data);
		case TYPE_union:
			return visitTypeUnion((Type.Union) type, data);
		case TYPE_unresolved:
			return visitTypeUnresolved((Type.Unresolved) type, data);
		case TYPE_void:
			return visitTypeVoid((Type.Void) type, data);
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public R visitTypeCallable(Type.Callable type, P data) {
		switch (type.getOpcode()) {
		case TYPE_function:
			visitTypeFunction((Type.Function) type, data);
		case TYPE_method:
			visitTypeMethod((Type.Method) type, data);
		case TYPE_property:
			visitTypeProperty((Type.Property) type, data);
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public R visitTypeArray(Type.Array type, P data) {
		visitType(type.getElement(), data);
		return null;
	}

	public R visitTypeBool(Type.Bool type, P data) {
		return null;
	}

	public R visitTypeByte(Type.Byte type, P data) {
		return null;
	}

	public R visitTypeFunction(Type.Function type, P data) {
		visitTypes(type.getParameters(), data);
		visitTypes(type.getReturns(), data);
		return null;
	}

	public R visitTypeInt(Type.Int type, P data) {
		return null;
	}

	public R visitTypeMethod(Type.Method type, P data) {
		visitTypes(type.getParameters(), data);
		visitTypes(type.getReturns(), data);
		return null;
	}

	public R visitTypeNominal(Type.Nominal type, P data) {
		return null;
	}

	public R visitTypeNull(Type.Null type, P data) {
		return null;
	}

	public R visitTypeProperty(Type.Property type, P data) {
		visitTypes(type.getParameters(), data);
		visitTypes(type.getReturns(), data);
		return null;
	}

	public R visitTypeRecord(Type.Record type, P data) {
		visitFields(type.getFields(), data);
		return null;
	}

	public R visitFields(Tuple<Type.Field> fields, P data) {
		for(int i=0;i!=fields.size();++i) {
			visitField(fields.get(i), data);
		}
		return null;
	}

	public R visitField(Type.Field field, P data) {
		visitType(field.getType(), data);
		return null;
	}

	public R visitTypeReference(Type.Reference type, P data) {
		visitType(type.getElement(), data);
		return null;
	}

	public R visitTypeUnion(Type.Union type, P data) {
		for(int i=0;i!=type.size();++i) {
			visitType(type.get(i), data);
		}
		return null;
	}

	public R visitTypeUnresolved(Type.Unresolved type, P data) {
		return null;
	}

	public R visitTypeVoid(Type.Void type, P data) {
		return null;
	}


	public R visitSemanticType(SemanticType type, P data) {
		switch (type.getOpcode()) {
		case SEMTYPE_array:
			return visitSemanticTypeArray((SemanticType.Array) type, data);
		case SEMTYPE_record:
			return visitSemanticTypeRecord((SemanticType.Record) type, data);
		case SEMTYPE_staticreference:
		case SEMTYPE_reference:
			return visitSemanticTypeReference((SemanticType.Reference) type, data);
		case SEMTYPE_union:
			return visitSemanticTypeUnion((SemanticType.Union) type, data);
		case SEMTYPE_intersection:
			return visitSemanticTypeIntersection((SemanticType.Intersection) type, data);
		case SEMTYPE_difference:
			return visitSemanticTypeDifference((SemanticType.Difference) type, data);
		default:
			// Handle leaf cases
			return visitType((Type) type, data);
		}
	}

	public R visitSemanticTypeArray(SemanticType.Array type, P data) {
		visitSemanticType(type.getElement(), data);
		return null;
	}

	public R visitSemanticTypeRecord(SemanticType.Record type, P data) {
		for(SemanticType.Field f : type.getFields()) {
			visitSemanticType(f.getType(), data);
		}
		return null;
	}

	public R visitSemanticTypeReference(SemanticType.Reference type, P data) {
		visitSemanticType(type.getElement(), data);
		return null;
	}

	public R visitSemanticTypeUnion(SemanticType.Union type, P data) {
		for(SemanticType t : type.getAll()) {
			visitSemanticType(t, data);
		}
		return null;
	}

	public R visitSemanticTypeIntersection(SemanticType.Intersection type, P data) {
		for(SemanticType t : type.getAll()) {
			visitSemanticType(t, data);
		}
		return null;
	}

	public R visitSemanticTypeDifference(SemanticType.Difference type, P data) {
		visitSemanticType(type.getLeftHandSide(), data);
		visitSemanticType(type.getRightHandSide(), data);
		return null;
	}

}
