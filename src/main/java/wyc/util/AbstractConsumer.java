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

import static wyc.lang.WhileyFile.*;

/**
 * A simple visitor over all declarations, statements, expressions and types in
 * a given WhileyFile which consumes one data parameter but returns nothing. The
 * intention is that this is extended as necessary to provide custom
 * functionality.
 *
 * @author David J. Pearce
 *
 */
public abstract class AbstractConsumer<T> {

	public void visitWhileyFile(WhileyFile wf, T data) {
		for (Decl decl : wf.getDeclarations()) {
			visitDeclaration(decl, data);
		}
	}

	public void visitDeclaration(Decl decl, T data) {
		switch (decl.getOpcode()) {
		case DECL_importfrom:
		case DECL_import:
			visitImport((Decl.Import) decl, data);
			break;
		case DECL_staticvar:
			visitStaticVariable((Decl.StaticVariable) decl, data);
			break;
		case DECL_type:
			visitType((Decl.Type) decl, data);
			break;
		case DECL_function:
		case DECL_method:
		case DECL_property:
			visitCallable((Decl.Callable) decl, data);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void visitImport(Decl.Import decl, T data) {

	}

	public void visitLambda(Decl.Lambda decl, T data) {
		visitVariables(decl.getParameters(), data);
		visitExpression(decl.getBody(), data);
	}

	public void visitVariables(Tuple<Decl.Variable> vars, T data) {
		for(int i=0;i!=vars.size();++i) {
			Decl.Variable var = vars.get(i);
			visitVariable(var, data);
		}
	}

	public void visitVariable(Decl.Variable decl, T data) {
		visitType(decl.getType(), data);
		if(decl.hasInitialiser()) {
			visitExpression(decl.getInitialiser(), data);
		}
	}

	public void visitStaticVariable(Decl.StaticVariable decl, T data) {
		visitType(decl.getType(), data);
		if (decl.hasInitialiser()) {
			visitExpression(decl.getInitialiser(), data);
		}
	}

	public void visitType(Decl.Type decl, T data) {
		visitVariable(decl.getVariableDeclaration(), data);
		visitExpressions(decl.getInvariant(), data);
	}

	public void visitCallable(Decl.Callable decl, T data) {
		switch (decl.getOpcode()) {
		case DECL_function:
		case DECL_method:
			visitFunctionOrMethod((Decl.FunctionOrMethod) decl, data);
			break;
		case DECL_property:
			visitProperty((Decl.Property) decl, data);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void visitFunctionOrMethod(Decl.FunctionOrMethod decl, T data) {
		switch (decl.getOpcode()) {
		case DECL_function:
			visitFunction((Decl.Function) decl, data);
			break;
		case DECL_method:
			visitMethod((Decl.Method) decl, data);
			break;
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public void visitProperty(Decl.Property decl, T data) {
		visitVariables(decl.getParameters(), data);
		visitVariables(decl.getReturns(), data);
		visitExpressions(decl.getInvariant(), data);
	}

	public void visitFunction(Decl.Function decl, T data) {
		visitVariables(decl.getParameters(), data);
		visitVariables(decl.getReturns(), data);
		visitExpressions(decl.getRequires(), data);
		visitExpressions(decl.getEnsures(), data);
		visitStatement(decl.getBody(), data);
	}

	public void visitMethod(Decl.Method decl, T data) {
		visitVariables(decl.getParameters(), data);
		visitVariables(decl.getReturns(), data);
		visitExpressions(decl.getRequires(), data);
		visitExpressions(decl.getEnsures(), data);
		visitStatement(decl.getBody(), data);
	}

	public void visitStatement(Stmt stmt, T data) {
		switch (stmt.getOpcode()) {
		case DECL_variable:
		case DECL_variableinitialiser:
			visitVariable((Decl.Variable) stmt, data);
			break;
		case STMT_assert:
			visitAssert((Stmt.Assert) stmt, data);
			break;
		case STMT_assign:
			visitAssign((Stmt.Assign) stmt, data);
			break;
		case STMT_assume:
			visitAssume((Stmt.Assume) stmt, data);
			break;
		case STMT_block:
			visitBlock((Stmt.Block) stmt, data);
			break;
		case STMT_break:
			visitBreak((Stmt.Break) stmt, data);
			break;
		case STMT_continue:
			visitContinue((Stmt.Continue) stmt, data);
			break;
		case STMT_debug:
			visitDebug((Stmt.Debug) stmt, data);
			break;
		case STMT_dowhile:
			visitDoWhile((Stmt.DoWhile) stmt, data);
			break;
		case STMT_fail:
			visitFail((Stmt.Fail) stmt, data);
			break;
		case STMT_if:
		case STMT_ifelse:
			visitIfElse((Stmt.IfElse) stmt, data);
			break;
		case EXPR_invoke:
			visitInvoke((Expr.Invoke) stmt, data);
			break;
		case EXPR_indirectinvoke:
			visitIndirectInvoke((Expr.IndirectInvoke) stmt, data);
			break;
		case STMT_namedblock:
			visitNamedBlock((Stmt.NamedBlock) stmt, data);
			break;
		case STMT_return:
			visitReturn((Stmt.Return) stmt, data);
			break;
		case STMT_skip:
			visitSkip((Stmt.Skip) stmt, data);
			break;
		case STMT_switch:
			visitSwitch((Stmt.Switch) stmt, data);
			break;
		case STMT_while:
			visitWhile((Stmt.While) stmt, data);
			break;
		default:
			throw new IllegalArgumentException("unknown statement encountered (" + stmt.getClass().getName() + ")");
		}
	}


	public void visitAssert(Stmt.Assert stmt, T data) {
		visitExpression(stmt.getCondition(), data);
	}


	public void visitAssign(Stmt.Assign stmt, T data) {
		visitLVals(stmt.getLeftHandSide(), data);
		visitExpressions(stmt.getRightHandSide(), data);
	}

	public void visitLVals(Tuple<LVal> lvals, T data) {
		for(int i=0;i!=lvals.size();++i) {
			visitExpression(lvals.get(i), data);
		}
	}

	public void visitAssume(Stmt.Assume stmt, T data) {
		visitExpression(stmt.getCondition(), data);
	}

	public void visitBlock(Stmt.Block stmt, T data) {
		for(int i=0;i!=stmt.size();++i) {
			visitStatement(stmt.get(i), data);
		}
	}

	public void visitBreak(Stmt.Break stmt, T data) {

	}

	public void visitContinue(Stmt.Continue stmt, T data) {

	}

	public void visitDebug(Stmt.Debug stmt, T data) {
		visitExpression(stmt.getOperand(), data);
	}

	public void visitDoWhile(Stmt.DoWhile stmt, T data) {
		visitStatement(stmt.getBody(), data);
		visitExpression(stmt.getCondition(), data);
		visitExpressions(stmt.getInvariant(), data);
	}

	public void visitFail(Stmt.Fail stmt, T data) {

	}

	public void visitIfElse(Stmt.IfElse stmt, T data) {
		visitExpression(stmt.getCondition(), data);
		visitStatement(stmt.getTrueBranch(), data);
		if(stmt.hasFalseBranch()) {
			visitStatement(stmt.getFalseBranch(), data);
		}
	}

	public void visitNamedBlock(Stmt.NamedBlock stmt, T data) {
		visitStatement(stmt.getBlock(), data);
	}

	public void visitReturn(Stmt.Return stmt, T data) {
		visitExpressions(stmt.getReturns(), data);
	}

	public void visitSkip(Stmt.Skip stmt, T data) {

	}

	public void visitSwitch(Stmt.Switch stmt, T data) {
		visitExpression(stmt.getCondition(), data);
		Tuple<Stmt.Case> cases = stmt.getCases();
		for(int i=0;i!=cases.size();++i) {
			visitCase((Stmt.Case) cases.get(i), data);
		}
	}

	public void visitCase(Stmt.Case stmt, T data) {
		visitExpressions(stmt.getConditions(), data);
		visitStatement(stmt.getBlock(), data);
	}

	public void visitWhile(Stmt.While stmt, T data) {
		visitExpression(stmt.getCondition(), data);
		visitExpressions(stmt.getInvariant(), data);
		visitStatement(stmt.getBody(), data);
	}

	public void visitExpressions(Tuple<Expr> exprs, T data) {
		for (int i = 0; i != exprs.size(); ++i) {
			visitExpression(exprs.get(i), data);
		}
	}

	public void visitExpression(Expr expr, T data) {
		switch (expr.getOpcode()) {
		// Terminals
		case EXPR_constant:
			visitConstant((Expr.Constant) expr, data);
			break;
		case EXPR_indirectinvoke:
			visitIndirectInvoke((Expr.IndirectInvoke) expr, data);
			break;
		case EXPR_lambdaaccess:
			visitLambdaAccess((Expr.LambdaAccess) expr, data);
			break;
		case DECL_lambda:
			visitLambda((Decl.Lambda) expr, data);
			break;
		case EXPR_staticvariable:
			visitStaticVariableAccess((Expr.StaticVariableAccess) expr, data);
			break;
		case EXPR_variablecopy:
		case EXPR_variablemove:
			visitVariableAccess((Expr.VariableAccess) expr, data);
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
			visitUnaryOperator((Expr.UnaryOperator) expr, data);
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
			visitBinaryOperator((Expr.BinaryOperator) expr, data);
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
			visitNaryOperator((Expr.NaryOperator) expr, data);
			break;
		// Ternary Operators
		case EXPR_arrayupdate:
			visitTernaryOperator((Expr.TernaryOperator) expr, data);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitUnaryOperator(Expr.UnaryOperator expr, T data) {
		switch (expr.getOpcode()) {
		// Unary Operators
		case EXPR_cast:
			visitCast((Expr.Cast) expr, data);
			break;
		case EXPR_integernegation:
			visitIntegerNegation((Expr.IntegerNegation) expr, data);
			break;
		case EXPR_is:
			visitIs((Expr.Is) expr, data);
			break;
		case EXPR_logicalnot:
			visitLogicalNot((Expr.LogicalNot) expr, data);
			break;
		case EXPR_logicalexistential:
			visitExistentialQuantifier((Expr.ExistentialQuantifier) expr, data);
			break;
		case EXPR_logicaluniversal:
			visitUniversalQuantifier((Expr.UniversalQuantifier) expr, data);
			break;
		case EXPR_bitwisenot:
			visitBitwiseComplement((Expr.BitwiseComplement) expr, data);
			break;
		case EXPR_dereference:
			visitDereference((Expr.Dereference) expr, data);
			break;
		case EXPR_staticnew:
		case EXPR_new:
			visitNew((Expr.New) expr, data);
			break;
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			visitRecordAccess((Expr.RecordAccess) expr, data);
			break;
		case EXPR_arraylength:
			visitArrayLength((Expr.ArrayLength) expr, data);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitBinaryOperator(Expr.BinaryOperator expr, T data) {
		switch (expr.getOpcode()) {
		// Binary Operators
		case EXPR_equal:
			visitEqual((Expr.Equal) expr, data);
			break;
		case EXPR_notequal:
			visitNotEqual((Expr.NotEqual) expr, data);
			break;
		case EXPR_logiaclimplication:
			visitLogicalImplication((Expr.LogicalImplication) expr, data);
			break;
		case EXPR_logicaliff:
			visitLogicalIff((Expr.LogicalIff) expr, data);
			break;
		case EXPR_integerlessthan:
			visitIntegerLessThan((Expr.IntegerLessThan) expr, data);
			break;
		case EXPR_integerlessequal:
			visitIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr, data);
			break;
		case EXPR_integergreaterthan:
			visitIntegerGreaterThan((Expr.IntegerGreaterThan) expr, data);
			break;
		case EXPR_integergreaterequal:
			visitIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr, data);
			break;
		case EXPR_integeraddition:
			visitIntegerAddition((Expr.IntegerAddition) expr, data);
			break;
		case EXPR_integersubtraction:
			visitIntegerSubtraction((Expr.IntegerSubtraction) expr, data);
			break;
		case EXPR_integermultiplication:
			visitIntegerMultiplication((Expr.IntegerMultiplication) expr, data);
			break;
		case EXPR_integerdivision:
			visitIntegerDivision((Expr.IntegerDivision) expr, data);
			break;
		case EXPR_integerremainder:
			visitIntegerRemainder((Expr.IntegerRemainder) expr, data);
			break;
		case EXPR_bitwiseshl:
			visitBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr, data);
			break;
		case EXPR_bitwiseshr:
			visitBitwiseShiftRight((Expr.BitwiseShiftRight) expr, data);
			break;
		case EXPR_arraygenerator:
			visitArrayGenerator((Expr.ArrayGenerator) expr, data);
			break;
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			visitArrayAccess((Expr.ArrayAccess) expr, data);
			break;
		case EXPR_arrayrange:
			visitArrayRange((Expr.ArrayRange) expr, data);
			break;
		case EXPR_recordupdate:
			visitRecordUpdate((Expr.RecordUpdate) expr, data);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitTernaryOperator(Expr.TernaryOperator expr, T data) {
		switch (expr.getOpcode()) {
		// Ternary Operators
		case EXPR_arrayupdate:
			visitArrayUpdate((Expr.ArrayUpdate) expr, data);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitNaryOperator(Expr.NaryOperator expr, T data) {
		switch (expr.getOpcode()) {
		// Nary Operators
		case EXPR_arrayinitialiser:
			visitArrayInitialiser((Expr.ArrayInitialiser) expr, data);
			break;
		case EXPR_bitwiseand:
			visitBitwiseAnd((Expr.BitwiseAnd) expr, data);
			break;
		case EXPR_bitwiseor:
			visitBitwiseOr((Expr.BitwiseOr) expr, data);
			break;
		case EXPR_bitwisexor:
			visitBitwiseXor((Expr.BitwiseXor) expr, data);
			break;
		case EXPR_invoke:
			visitInvoke((Expr.Invoke) expr, data);
			break;
		case EXPR_logicaland:
			visitLogicalAnd((Expr.LogicalAnd) expr, data);
			break;
		case EXPR_logicalor:
			visitLogicalOr((Expr.LogicalOr) expr, data);
			break;
		case EXPR_recordinitialiser:
			visitRecordInitialiser((Expr.RecordInitialiser) expr, data);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitArrayAccess(Expr.ArrayAccess expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitArrayLength(Expr.ArrayLength expr, T data) {
		visitExpression(expr.getOperand(), data);
	}

	public void visitArrayGenerator(Expr.ArrayGenerator expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitArrayInitialiser(Expr.ArrayInitialiser expr, T data) {
		visitExpressions(expr.getOperands(), data);
	}

	public void visitArrayRange(Expr.ArrayRange expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitArrayUpdate(Expr.ArrayUpdate expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
		visitExpression(expr.getThirdOperand(), data);
	}

	public void visitBitwiseComplement(Expr.BitwiseComplement expr, T data) {
		visitExpression(expr.getOperand(), data);
	}

	public void visitBitwiseAnd(Expr.BitwiseAnd expr, T data) {
		visitExpressions(expr.getOperands(), data);
	}

	public void visitBitwiseOr(Expr.BitwiseOr expr, T data) {
		visitExpressions(expr.getOperands(), data);
	}

	public void visitBitwiseXor(Expr.BitwiseXor expr, T data) {
		visitExpressions(expr.getOperands(), data);
	}

	public void visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitBitwiseShiftRight(Expr.BitwiseShiftRight expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitCast(Expr.Cast expr, T data) {
		visitExpression(expr.getOperand(), data);
	}

	public void visitConstant(Expr.Constant expr, T data) {

	}

	public void visitDereference(Expr.Dereference expr, T data) {
		visitExpression(expr.getOperand(), data);
	}

	public void visitEqual(Expr.Equal expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitIntegerLessThan(Expr.IntegerLessThan expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitIntegerGreaterThan(Expr.IntegerGreaterThan expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitIntegerNegation(Expr.IntegerNegation expr, T data) {
		visitExpression(expr.getOperand(), data);
	}

	public void visitIntegerAddition(Expr.IntegerAddition expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitIntegerSubtraction(Expr.IntegerSubtraction expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitIntegerMultiplication(Expr.IntegerMultiplication expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitIntegerDivision(Expr.IntegerDivision expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitIntegerRemainder(Expr.IntegerRemainder expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitIs(Expr.Is expr, T data) {
		visitExpression(expr.getOperand(), data);
	}

	public void visitLogicalAnd(Expr.LogicalAnd expr, T data) {
		visitExpressions(expr.getOperands(), data);
	}

	public void visitLogicalImplication(Expr.LogicalImplication expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitLogicalIff(Expr.LogicalIff expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitLogicalNot(Expr.LogicalNot expr, T data) {
		visitExpression(expr.getOperand(), data);
	}

	public void visitLogicalOr(Expr.LogicalOr expr, T data) {
		visitExpressions(expr.getOperands(), data);
	}

	public void visitExistentialQuantifier(Expr.ExistentialQuantifier expr, T data) {
		visitVariables(expr.getParameters(), data);
		visitExpression(expr.getOperand(), data);
	}

	public void visitUniversalQuantifier(Expr.UniversalQuantifier expr, T data) {
		visitVariables(expr.getParameters(), data);
		visitExpression(expr.getOperand(), data);
	}

	public void visitInvoke(Expr.Invoke expr, T data) {
		visitExpressions(expr.getOperands(), data);
	}

	public void visitIndirectInvoke(Expr.IndirectInvoke expr, T data) {
		visitExpression(expr.getSource(), data);
		visitExpressions(expr.getArguments(), data);
	}

	public void visitLambdaAccess(Expr.LambdaAccess expr, T data) {

	}

	public void visitNew(Expr.New expr, T data) {
		visitExpression(expr.getOperand(), data);
	}

	public void visitNotEqual(Expr.NotEqual expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitRecordAccess(Expr.RecordAccess expr, T data) {
		visitExpression(expr.getOperand(), data);
	}

	public void visitRecordInitialiser(Expr.RecordInitialiser expr, T data) {
		visitExpressions(expr.getOperands(), data);
	}

	public void visitRecordUpdate(Expr.RecordUpdate expr, T data) {
		visitExpression(expr.getFirstOperand(), data);
		visitExpression(expr.getSecondOperand(), data);
	}

	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, T data) {

	}

	public void visitVariableAccess(Expr.VariableAccess expr, T data) {

	}

	public void visitTypes(Tuple<Type> type, T data) {
		for(int i=0;i!=type.size();++i) {
			visitType(type.get(i), data);
		}
	}

	public void visitType(Type type, T data) {
		switch (type.getOpcode()) {
		case TYPE_array:
			visitArray((Type.Array) type, data);
			break;
		case TYPE_bool:
			visitBool((Type.Bool) type, data);
			break;
		case TYPE_byte:
			visitByte((Type.Byte) type, data);
			break;
		case TYPE_int:
			visitInt((Type.Int) type, data);
			break;
		case TYPE_nominal:
			visitNominal((Type.Nominal) type, data);
			break;
		case TYPE_null:
			visitNull((Type.Null) type, data);
			break;
		case TYPE_record:
			visitRecord((Type.Record) type, data);
			break;
		case TYPE_staticreference:
		case TYPE_reference:
			visitReference((Type.Reference) type, data);
			break;
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
			visitCallable((Type.Callable) type, data);
			break;
		case TYPE_union:
			visitUnion((Type.Union) type, data);
			break;
		case TYPE_unresolved:
			visitUnresolved((Type.Unresolved) type, data);
			break;
		case TYPE_void:
			visitVoid((Type.Void) type, data);
			break;
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public void visitCallable(Type.Callable type, T data) {
		switch (type.getOpcode()) {
		case TYPE_function:
			visitFunction((Type.Function) type, data);
			break;
		case TYPE_method:
			visitMethod((Type.Method) type, data);
			break;
		case TYPE_property:
			visitProperty((Type.Property) type, data);
			break;
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public void visitArray(Type.Array type, T data) {
		visitType(type.getElement(), data);
	}

	public void visitBool(Type.Bool type, T data) {

	}

	public void visitByte(Type.Byte type, T data) {

	}

	public void visitFunction(Type.Function type, T data) {
		visitTypes(type.getParameters(), data);
		visitTypes(type.getReturns(), data);
	}

	public void visitInt(Type.Int type, T data) {

	}

	public void visitMethod(Type.Method type, T data) {
		visitTypes(type.getParameters(), data);
		visitTypes(type.getReturns(), data);
	}

	public void visitNominal(Type.Nominal type, T data) {

	}

	public void visitNull(Type.Null type, T data) {

	}

	public void visitProperty(Type.Property type, T data) {
		visitTypes(type.getParameters(), data);
		visitTypes(type.getReturns(), data);
	}

	public void visitRecord(Type.Record type, T data) {
		visitVariables(type.getFields(), data);
	}

	public void visitReference(Type.Reference type, T data) {
		visitType(type.getElement(), data);
	}

	public void visitUnion(Type.Union type, T data) {
		for(int i=0;i!=type.size();++i) {
			visitType(type.get(i), data);
		}
	}

	public void visitUnresolved(Type.Unresolved type, T data) {

	}

	public void visitVoid(Type.Void type, T data) {

	}
}
