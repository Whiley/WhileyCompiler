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
package wyil.util;

import static wyil.lang.WyilFile.*;

import jbfs.core.Build;
import wycc.lang.SyntacticItem;
import wycc.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.LVal;
import wyil.lang.WyilFile.Stmt;
import wyil.lang.WyilFile.Type;

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
	protected final Build.Meter meter;

	public AbstractVisitor(Build.Meter meter) {
		this.meter = meter;
	}

	public void visitModule(WyilFile wf) {
		Decl.Module module = wf.getModule();
		//
		for (Decl.Unit decl : module.getUnits()) {
			visitUnit(decl);
		}
		for (Decl.Unit decl : module.getExterns()) {
			visitExternalUnit(decl);
		}
	}

	public void visitDeclaration(Decl decl) {
		meter.step("declaration");
		switch (decl.getOpcode()) {
		case DECL_unit:
			visitUnit((Decl.Unit) decl);
			break;
		case DECL_importwith:
		case DECL_importfrom:
		case DECL_import:
			visitImport((Decl.Import) decl);
			break;
		case DECL_staticvar:
			visitStaticVariable((Decl.StaticVariable) decl);
			break;
		case DECL_type:
		case DECL_rectype:
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

	public void visitUnit(Decl.Unit unit) {
		meter.step("unit");
		for (Decl decl : unit.getDeclarations()) {
			visitDeclaration(decl);
		}
	}

	public void visitExternalUnit(Decl.Unit unit) {
		visitUnit(unit);
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
	}

	public void visitStaticVariables(Tuple<Decl.StaticVariable> vars) {
		for (int i = 0; i != vars.size(); ++i) {
			Decl.StaticVariable var = vars.get(i);
			visitStaticVariable(var);
		}
	}
	public void visitStaticVariable(Decl.StaticVariable decl) {
		visitType(decl.getType());
		visitExpression(decl.getInitialiser());
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
		meter.step("statement");
		switch (stmt.getOpcode()) {
		case DECL_variable:
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
		case STMT_for:
			visitFor((Stmt.For) stmt);
			break;
		case STMT_if:
		case STMT_ifelse:
			visitIfElse((Stmt.IfElse) stmt);
			break;
		case STMT_initialiser:
		case STMT_initialiservoid:
			visitInitialiser((Stmt.Initialiser) stmt);
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
		case STMT_returnvoid:
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
			visitExpression((Expr) stmt);
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

	public void visitFor(Stmt.For stmt) {
		visitStaticVariable(stmt.getVariable());
		visitExpressions(stmt.getInvariant());
		visitStatement(stmt.getBody());
	}


	public void visitIfElse(Stmt.IfElse stmt) {
		visitExpression(stmt.getCondition());
		visitStatement(stmt.getTrueBranch());
		if (stmt.hasFalseBranch()) {
			visitStatement(stmt.getFalseBranch());
		}
	}


	public void visitInitialiser(Stmt.Initialiser stmt) {
		for (Decl.Variable v : stmt.getVariables()) {
			visitVariable(v);
		}
		if(stmt.hasInitialiser()) {
			visitExpression(stmt.getInitialiser());
		}
	}

	public void visitNamedBlock(Stmt.NamedBlock stmt) {
		visitStatement(stmt.getBlock());
	}

	public void visitReturn(Stmt.Return stmt) {
		if(stmt.hasReturn()) {
			visitExpression(stmt.getReturn());
		}
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
		meter.step("expression");
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
		case EXPR_fielddereference:
		case EXPR_new:
		case EXPR_old:
		case EXPR_recordaccess:
		case EXPR_recordborrow:
		case EXPR_arraylength:
			visitUnaryOperator((Expr.UnaryOperator) expr);
			break;
		// Binary Operators
		case EXPR_logicalimplication:
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
		case EXPR_tupleinitialiser:
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
		case EXPR_fielddereference:
			visitFieldDereference((Expr.FieldDereference) expr);
			break;
		case EXPR_new:
			visitNew((Expr.New) expr);
			break;
		case EXPR_old:
			visitOld((Expr.Old) expr);
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
		case EXPR_logicalimplication:
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
		case EXPR_tupleinitialiser:
			visitTupleInitialiser((Expr.TupleInitialiser) expr);
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
		visitType(expr.getType());
		visitExpression(expr.getOperand());
	}

	public void visitConstant(Expr.Constant expr) {

	}

	public void visitDereference(Expr.Dereference expr) {
		visitExpression(expr.getOperand());
	}

	public void visitFieldDereference(Expr.FieldDereference expr) {
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
		visitType(expr.getTestType());
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
		visitStaticVariables(expr.getParameters());
		visitExpression(expr.getOperand());
	}

	public void visitUniversalQuantifier(Expr.UniversalQuantifier expr) {
		visitStaticVariables(expr.getParameters());
		visitExpression(expr.getOperand());
	}

	public void visitInvoke(Expr.Invoke expr) {
		for(SyntacticItem arg : expr.getBinding().getArguments()) {
			if(arg instanceof Type) {
				visitType((Type) arg);
			}
		}
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

	public void visitOld(Expr.Old expr) {
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

	public void visitTupleInitialiser(Expr.TupleInitialiser expr) {
		visitExpressions(expr.getOperands());
	}

	public void visitVariableAccess(Expr.VariableAccess expr) {

	}

	public void visitType(Type type) {
		meter.step("type");
		switch (type.getOpcode()) {
		case TYPE_any:
			visitTypeAny((Type.Any) type);
			break;
		case TYPE_array:
			visitTypeArray((Type.Array) type);
			break;
		case TYPE_bool:
			visitTypeBool((Type.Bool) type);
			break;
		case TYPE_byte:
			visitTypeByte((Type.Byte) type);
			break;
		case TYPE_existential:
			visitTypeExistential((Type.Existential) type);
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
		case TYPE_reference:
			visitTypeReference((Type.Reference) type);
			break;
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
			visitTypeCallable((Type.Callable) type);
			break;
		case TYPE_tuple:
			visitTypeTuple((Type.Tuple) type);
			break;
		case TYPE_union:
			visitTypeUnion((Type.Union) type);
			break;
		case TYPE_unknown:
			visitTypeUnresolved((Type.Unknown) type);
			break;
		case TYPE_void:
			visitTypeVoid((Type.Void) type);
			break;
		case TYPE_universal:
			visitTypeVariable((Type.Universal) type);
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

	public void visitTypeAny(Type.Any type) {

	}

	public void visitTypeArray(Type.Array type) {
		visitType(type.getElement());
	}

	public void visitTypeBool(Type.Bool type) {

	}

	public void visitTypeByte(Type.Byte type) {

	}

	public void visitTypeFunction(Type.Function type) {
		visitType(type.getParameter());
		visitType(type.getReturn());
	}

	public void visitTypeExistential(Type.Existential type) {

	}

	public void visitTypeInt(Type.Int type) {

	}

	public void visitTypeMethod(Type.Method type) {
		visitType(type.getParameter());
		visitType(type.getReturn());
	}

	public void visitTypeNominal(Type.Nominal type) {
		Tuple<Type> parameters = type.getParameters();
		for(int i=0;i!=parameters.size();++i) {
			visitType(parameters.get(i));
		}
	}

	public void visitTypeNull(Type.Null type) {

	}

	public void visitTypeProperty(Type.Property type) {
		visitType(type.getParameter());
		visitType(type.getReturn());
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

	public void visitTypeTuple(Type.Tuple type) {
		for (int i = 0; i != type.size(); ++i) {
			visitType(type.get(i));
		}
	}


	public void visitTypeUnion(Type.Union type) {
		for (int i = 0; i != type.size(); ++i) {
			visitType(type.get(i));
		}
	}

	public void visitTypeUnresolved(Type.Unknown type) {

	}

	public void visitTypeVoid(Type.Void type) {

	}

	public void visitTypeVariable(Type.Universal type) {
	}
}
