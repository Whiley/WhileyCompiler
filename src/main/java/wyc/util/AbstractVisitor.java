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
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.Type;

import static wyc.lang.WhileyFile.*;

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
public abstract class AbstractVisitor {

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

	public void visitLambda(Decl.Lambda decl) {
		visitVariables(decl.getParameters());
		visitExpression(decl.getBody());
	}

	public void visitVariables(Tuple<Decl.Variable> vars) {
		for (int i = 0; i != vars.size(); ++i) {
			Decl.Variable var = vars.get(i);
			visitVariable(var);
		}
	}

	public void visitVariable(Decl.Variable decl) {
		visitType(decl.getType());
		if (decl.hasInitialiser()) {
			visitExpression(decl.getInitialiser());
		}
	}

	public void visitStaticVariable(Decl.StaticVariable decl) {
		visitType(decl.getType());
		if (decl.hasInitialiser()) {
			visitExpression(decl.getInitialiser());
		}
	}

	public void visitType(Decl.Type decl) {
		visitVariable(decl.getVariableDeclaration());
		visitExpressions(decl.getInvariant());
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
		visitVariables(decl.getParameters());
		visitVariables(decl.getReturns());
		visitExpressions(decl.getInvariant());
	}

	public void visitFunction(Decl.Function decl) {
		visitVariables(decl.getParameters());
		visitVariables(decl.getReturns());
		visitExpressions(decl.getRequires());
		visitExpressions(decl.getEnsures());
		visitStatement(decl.getBody());
	}

	public void visitMethod(Decl.Method decl) {
		visitVariables(decl.getParameters());
		visitVariables(decl.getReturns());
		visitExpressions(decl.getRequires());
		visitExpressions(decl.getEnsures());
		visitStatement(decl.getBody());
	}

	public void visitStatement(Stmt stmt) {
		switch (stmt.getOpcode()) {
		case DECL_variable:
		case DECL_variableinitialiser:
			visitVariable((Decl.Variable) stmt);
			break;
		case STMT_assert:
			visitAssert((Stmt.Assert) stmt);
			break;
		case STMT_assign:
			visitAssign((Stmt.Assign) stmt);
			break;
		case STMT_assume:
			visitAssume((Stmt.Assume) stmt);
			break;
		case STMT_block:
			visitBlock((Stmt.Block) stmt);
			break;
		case STMT_break:
			visitBreak((Stmt.Break) stmt);
			break;
		case STMT_continue:
			visitContinue((Stmt.Continue) stmt);
			break;
		case STMT_debug:
			visitDebug((Stmt.Debug) stmt);
			break;
		case STMT_dowhile:
			visitDoWhile((Stmt.DoWhile) stmt);
			break;
		case STMT_fail:
			visitFail((Stmt.Fail) stmt);
			break;
		case STMT_if:
		case STMT_ifelse:
			visitIfElse((Stmt.IfElse) stmt);
			break;
		case EXPR_invoke:
			visitInvoke((Expr.Invoke) stmt);
			break;
		case EXPR_indirectinvoke:
			visitIndirectInvoke((Expr.IndirectInvoke) stmt);
			break;
		case STMT_namedblock:
			visitNamedBlock((Stmt.NamedBlock) stmt);
			break;
		case STMT_return:
			visitReturn((Stmt.Return) stmt);
			break;
		case STMT_skip:
			visitSkip((Stmt.Skip) stmt);
			break;
		case STMT_switch:
			visitSwitch((Stmt.Switch) stmt);
			break;
		case STMT_while:
			visitWhile((Stmt.While) stmt);
			break;
		default:
			throw new IllegalArgumentException("unknown statement encountered (" + stmt.getClass().getName() + ")");
		}
	}

	public void visitAssert(Stmt.Assert stmt) {
		visitExpression(stmt.getCondition());
	}

	public void visitAssign(Stmt.Assign stmt) {
		visitLVals(stmt.getLeftHandSide());
		visitExpressions(stmt.getRightHandSide());
	}

	public void visitLVals(Tuple<LVal> lvals) {
		for (int i = 0; i != lvals.size(); ++i) {
			visitExpression(lvals.get(i));
		}
	}

	public void visitAssume(Stmt.Assume stmt) {
		visitExpression(stmt.getCondition());
	}

	public void visitBlock(Stmt.Block stmt) {
		for (int i = 0; i != stmt.size(); ++i) {
			visitStatement(stmt.get(i));
		}
	}

	public void visitBreak(Stmt.Break stmt) {

	}

	public void visitContinue(Stmt.Continue stmt) {

	}

	public void visitDebug(Stmt.Debug stmt) {
		visitExpression(stmt.getOperand());
	}

	public void visitDoWhile(Stmt.DoWhile stmt) {
		visitStatement(stmt.getBody());
		visitExpression(stmt.getCondition());
		visitExpressions(stmt.getInvariant());
	}

	public void visitFail(Stmt.Fail stmt) {

	}

	public void visitIfElse(Stmt.IfElse stmt) {
		visitExpression(stmt.getCondition());
		visitStatement(stmt.getTrueBranch());
		if (stmt.hasFalseBranch()) {
			visitStatement(stmt.getFalseBranch());
		}
	}

	public void visitNamedBlock(Stmt.NamedBlock stmt) {
		visitStatement(stmt.getBlock());
	}

	public void visitReturn(Stmt.Return stmt) {
		visitExpressions(stmt.getReturns());
	}

	public void visitSkip(Stmt.Skip stmt) {

	}

	public void visitSwitch(Stmt.Switch stmt) {
		visitExpression(stmt.getCondition());
		Tuple<Stmt.Case> cases = stmt.getCases();
		for (int i = 0; i != cases.size(); ++i) {
			visitCase(cases.get(i));
		}
	}

	public void visitCase(Stmt.Case stmt) {
		visitExpressions(stmt.getConditions());
		visitStatement(stmt.getBlock());
	}

	public void visitWhile(Stmt.While stmt) {
		visitExpression(stmt.getCondition());
		visitExpressions(stmt.getInvariant());
		visitStatement(stmt.getBody());
	}

	public void visitExpressions(Tuple<Expr> exprs) {
		for (int i = 0; i != exprs.size(); ++i) {
			visitExpression(exprs.get(i));
		}
	}

	public void visitExpression(Expr expr) {
		switch (expr.getOpcode()) {
		// Terminals
		case EXPR_constant:
			visitConstant((Expr.Constant) expr);
			break;
		case EXPR_indirectinvoke:
			visitIndirectInvoke((Expr.IndirectInvoke) expr);
			break;
		case EXPR_lambdaaccess:
			visitLambdaAccess((Expr.LambdaAccess) expr);
			break;
		case DECL_lambda:
			visitLambda((Decl.Lambda) expr);
			break;
		case EXPR_staticvariable:
			visitStaticVariableAccess((Expr.StaticVariableAccess) expr);
			break;
		case EXPR_variablecopy:
		case EXPR_variablemove:
			visitVariableAccess((Expr.VariableAccess) expr);
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
			visitUnaryOperator((Expr.UnaryOperator) expr);
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
			visitBinaryOperator((Expr.BinaryOperator) expr);
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
			visitNaryOperator((Expr.NaryOperator) expr);
			break;
		// Ternary Operators
		case EXPR_arrayupdate:
			visitTernaryOperator((Expr.TernaryOperator) expr);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitUnaryOperator(Expr.UnaryOperator expr) {
		switch (expr.getOpcode()) {
		// Unary Operators
		case EXPR_cast:
			visitCast((Expr.Cast) expr);
			break;
		case EXPR_integernegation:
			visitIntegerNegation((Expr.IntegerNegation) expr);
			break;
		case EXPR_is:
			visitIs((Expr.Is) expr);
			break;
		case EXPR_logicalnot:
			visitLogicalNot((Expr.LogicalNot) expr);
			break;
		case EXPR_logicalexistential:
			visitExistentialQuantifier((Expr.ExistentialQuantifier) expr);
			break;
		case EXPR_logicaluniversal:
			visitUniversalQuantifier((Expr.UniversalQuantifier) expr);
			break;
		case EXPR_bitwisenot:
			visitBitwiseComplement((Expr.BitwiseComplement) expr);
			break;
		case EXPR_dereference:
			visitDereference((Expr.Dereference) expr);
			break;
		case EXPR_staticnew:
		case EXPR_new:
			visitNew((Expr.New) expr);
			break;
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			visitRecordAccess((Expr.RecordAccess) expr);
			break;
		case EXPR_arraylength:
			visitArrayLength((Expr.ArrayLength) expr);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitBinaryOperator(Expr.BinaryOperator expr) {
		switch (expr.getOpcode()) {
		// Binary Operators
		case EXPR_equal:
			visitEqual((Expr.Equal) expr);
			break;
		case EXPR_notequal:
			visitNotEqual((Expr.NotEqual) expr);
			break;
		case EXPR_logiaclimplication:
			visitLogicalImplication((Expr.LogicalImplication) expr);
			break;
		case EXPR_logicaliff:
			visitLogicalIff((Expr.LogicalIff) expr);
			break;
		case EXPR_integerlessthan:
			visitIntegerLessThan((Expr.IntegerLessThan) expr);
			break;
		case EXPR_integerlessequal:
			visitIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr);
			break;
		case EXPR_integergreaterthan:
			visitIntegerGreaterThan((Expr.IntegerGreaterThan) expr);
			break;
		case EXPR_integergreaterequal:
			visitIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr);
			break;
		case EXPR_integeraddition:
			visitIntegerAddition((Expr.IntegerAddition) expr);
			break;
		case EXPR_integersubtraction:
			visitIntegerSubtraction((Expr.IntegerSubtraction) expr);
			break;
		case EXPR_integermultiplication:
			visitIntegerMultiplication((Expr.IntegerMultiplication) expr);
			break;
		case EXPR_integerdivision:
			visitIntegerDivision((Expr.IntegerDivision) expr);
			break;
		case EXPR_integerremainder:
			visitIntegerRemainder((Expr.IntegerRemainder) expr);
			break;
		case EXPR_bitwiseshl:
			visitBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr);
			break;
		case EXPR_bitwiseshr:
			visitBitwiseShiftRight((Expr.BitwiseShiftRight) expr);
			break;
		case EXPR_arraygenerator:
			visitArrayGenerator((Expr.ArrayGenerator) expr);
			break;
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			visitArrayAccess((Expr.ArrayAccess) expr);
			break;
		case EXPR_arrayrange:
			visitArrayRange((Expr.ArrayRange) expr);
			break;
		case EXPR_recordupdate:
			visitRecordUpdate((Expr.RecordUpdate) expr);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitTernaryOperator(Expr.TernaryOperator expr) {
		switch (expr.getOpcode()) {
		// Ternary Operators
		case EXPR_arrayupdate:
			visitArrayUpdate((Expr.ArrayUpdate) expr);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitNaryOperator(Expr.NaryOperator expr) {
		switch (expr.getOpcode()) {
		// Nary Operators
		case EXPR_arrayinitialiser:
			visitArrayInitialiser((Expr.ArrayInitialiser) expr);
			break;
		case EXPR_bitwiseand:
			visitBitwiseAnd((Expr.BitwiseAnd) expr);
			break;
		case EXPR_bitwiseor:
			visitBitwiseOr((Expr.BitwiseOr) expr);
			break;
		case EXPR_bitwisexor:
			visitBitwiseXor((Expr.BitwiseXor) expr);
			break;
		case EXPR_invoke:
			visitInvoke((Expr.Invoke) expr);
			break;
		case EXPR_logicaland:
			visitLogicalAnd((Expr.LogicalAnd) expr);
			break;
		case EXPR_logicalor:
			visitLogicalOr((Expr.LogicalOr) expr);
			break;
		case EXPR_recordinitialiser:
			visitRecordInitialiser((Expr.RecordInitialiser) expr);
			break;
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public void visitArrayAccess(Expr.ArrayAccess expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitArrayLength(Expr.ArrayLength expr) {
		visitExpression(expr.getOperand());
	}

	public void visitArrayGenerator(Expr.ArrayGenerator expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitArrayInitialiser(Expr.ArrayInitialiser expr) {
		visitExpressions(expr.getOperands());
	}

	public void visitArrayRange(Expr.ArrayRange expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitArrayUpdate(Expr.ArrayUpdate expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
		visitExpression(expr.getThirdOperand());
	}

	public void visitBitwiseComplement(Expr.BitwiseComplement expr) {
		visitExpression(expr.getOperand());
	}

	public void visitBitwiseAnd(Expr.BitwiseAnd expr) {
		visitExpressions(expr.getOperands());
	}

	public void visitBitwiseOr(Expr.BitwiseOr expr) {
		visitExpressions(expr.getOperands());
	}

	public void visitBitwiseXor(Expr.BitwiseXor expr) {
		visitExpressions(expr.getOperands());
	}

	public void visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitBitwiseShiftRight(Expr.BitwiseShiftRight expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitCast(Expr.Cast expr) {
		visitExpression(expr.getOperand());
	}

	public void visitConstant(Expr.Constant expr) {

	}

	public void visitDereference(Expr.Dereference expr) {
		visitExpression(expr.getOperand());
	}

	public void visitEqual(Expr.Equal expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitIntegerLessThan(Expr.IntegerLessThan expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitIntegerGreaterThan(Expr.IntegerGreaterThan expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitIntegerNegation(Expr.IntegerNegation expr) {
		visitExpression(expr.getOperand());
	}

	public void visitIntegerAddition(Expr.IntegerAddition expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitIntegerSubtraction(Expr.IntegerSubtraction expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitIntegerMultiplication(Expr.IntegerMultiplication expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitIntegerDivision(Expr.IntegerDivision expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitIntegerRemainder(Expr.IntegerRemainder expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitIs(Expr.Is expr) {
		visitExpression(expr.getOperand());
	}

	public void visitLogicalAnd(Expr.LogicalAnd expr) {
		visitExpressions(expr.getOperands());
	}

	public void visitLogicalImplication(Expr.LogicalImplication expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitLogicalIff(Expr.LogicalIff expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitLogicalNot(Expr.LogicalNot expr) {
		visitExpression(expr.getOperand());
	}

	public void visitLogicalOr(Expr.LogicalOr expr) {
		visitExpressions(expr.getOperands());
	}

	public void visitExistentialQuantifier(Expr.ExistentialQuantifier expr) {
		visitVariables(expr.getParameters());
		visitExpression(expr.getOperand());
	}

	public void visitUniversalQuantifier(Expr.UniversalQuantifier expr) {
		visitVariables(expr.getParameters());
		visitExpression(expr.getOperand());
	}

	public void visitInvoke(Expr.Invoke expr) {
		visitExpressions(expr.getOperands());
	}

	public void visitIndirectInvoke(Expr.IndirectInvoke expr) {
		visitExpression(expr.getSource());
		visitExpressions(expr.getArguments());
	}

	public void visitLambdaAccess(Expr.LambdaAccess expr) {

	}

	public void visitNew(Expr.New expr) {
		visitExpression(expr.getOperand());
	}

	public void visitNotEqual(Expr.NotEqual expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitRecordAccess(Expr.RecordAccess expr) {
		visitExpression(expr.getOperand());
	}

	public void visitRecordInitialiser(Expr.RecordInitialiser expr) {
		visitExpressions(expr.getOperands());
	}

	public void visitRecordUpdate(Expr.RecordUpdate expr) {
		visitExpression(expr.getFirstOperand());
		visitExpression(expr.getSecondOperand());
	}

	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr) {

	}

	public void visitVariableAccess(Expr.VariableAccess expr) {

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
		for(int i=0;i!=fields.size();++i) {
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
		for(SemanticType.Field f : type.getFields()) {
			visitSemanticType(f.getType());
		}
	}

	public void visitSemanticTypeReference(SemanticType.Reference type) {
		visitSemanticType(type.getElement());
	}

	public void visitSemanticTypeUnion(SemanticType.Union type) {
		for(SemanticType t : type.getAll()) {
			visitSemanticType(t);
		}
	}

	public void visitSemanticTypeIntersection(SemanticType.Intersection type) {
		for(SemanticType t : type.getAll()) {
			visitSemanticType(t);
		}
	}

	public void visitSemanticTypeDifference(SemanticType.Difference type) {
		visitSemanticType(type.getLeftHandSide());
		visitSemanticType(type.getRightHandSide());
	}
}
