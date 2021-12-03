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

import java.util.ArrayList;
import java.util.List;

import jbfs.core.Build;
import jbfs.util.Pair;
import wycc.util.AbstractCompilationUnit.Identifier;
import wycc.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.LVal;
import wyil.lang.WyilFile.Stmt;
import wyil.lang.WyilFile.Type;

import static wyil.lang.WyilFile.*;

/**
 * A more complex visitor over all declarations, statements, expressions and
 * types in a given WhileyFile which accepts no additional data parameters and
 * returns nothing. The intention is that this is extended as necessary to
 * provide custom functionality. The key distinction from
 * <code>AbstractVisitor</code> is that this produces <i>concrete</i> type
 * information for all expressions based on a downward propagation (i.e.
 * propagating from assigned locations down through expressions). This gives a
 * more accurate view of type information and allows, for example, ambiguous
 * coercions to be detetected.
 *
 * @author David J. Pearce
 *
 */
public abstract class AbstractTranslator<D, S, E> {
	protected final Build.Meter meter;
	protected final Subtyping.Environment subtypeOperator;

	public AbstractTranslator(Build.Meter meter, Subtyping.Environment subtypeOperator) {
		this.meter = meter;
		this.subtypeOperator = subtypeOperator;
	}

	public D visitDeclaration(Decl decl) {
		meter.step("declaration");
		switch (decl.getOpcode()) {
		case DECL_unit:
			return visitUnit((Decl.Unit) decl);
		case DECL_importwith:
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
		case DECL_variant:
			return visitCallable((Decl.Callable) decl);
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public D visitUnit(Decl.Unit unit) {
		for (Decl decl : unit.getDeclarations()) {
			visitDeclaration(decl);
		}
		return null;
	}

	public D visitImport(Decl.Import decl) {
		return constructImport(decl);
	}

	public E visitLambda(Decl.Lambda decl) {
		//
		E body = visitExpression(decl.getBody());
		return constructLambda(decl,body);
	}

	public D visitStaticVariable(Decl.StaticVariable decl) {
		E initialiser = visitExpression(decl.getInitialiser());
		return constructStaticVariable(decl,initialiser);
	}

	public D visitType(Decl.Type decl) {
		List<E> invariant = visitHomogoneousExpressions(decl.getInvariant());
		return constructType(decl, invariant);
	}

	public D visitCallable(Decl.Callable decl) {
		switch (decl.getOpcode()) {
		case DECL_function:
		case DECL_method:
			return visitFunctionOrMethod((Decl.FunctionOrMethod) decl);
		case DECL_property:
			return visitProperty((Decl.Property) decl);
		case DECL_variant:
			return visitVariant((Decl.Variant) decl);
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
		E body = visitExpression(decl.getBody());
		return constructProperty(decl,body);
	}

	public D visitVariant(Decl.Variant decl) {
		List<E> clauses = visitHomogoneousExpressions(decl.getInvariant());
		return constructVariant(decl,clauses);
	}

	public D visitFunction(Decl.Function decl) {
		List<E> precondition = visitHomogoneousExpressions(decl.getRequires());
		List<E> postcondition = visitHomogoneousExpressions(decl.getEnsures());
		S body = visitBlock(decl.getBody(), new FunctionOrMethodOrPropertyScope(decl));
		return constructFunction(decl,precondition,postcondition,body);
	}

	public D visitMethod(Decl.Method decl) {
		// Construct environment relation
		List<E> precondition = visitHomogoneousExpressions(decl.getRequires());
		List<E> postcondition = visitHomogoneousExpressions(decl.getEnsures());
		S body = visitBlock(decl.getBody(), new FunctionOrMethodOrPropertyScope(decl));
		return constructMethod(decl,precondition,postcondition,body);
	}

	public S visitStatement(Stmt stmt, EnclosingScope scope) {
		meter.step("statement");
		//
		switch (stmt.getOpcode()) {
		case STMT_assert:
			return visitAssert((Stmt.Assert) stmt, scope);
		case STMT_assign:
			return visitAssign((Stmt.Assign) stmt, scope);
		case STMT_assume:
			return visitAssume((Stmt.Assume) stmt, scope);
		case STMT_block:
			return visitBlock((Stmt.Block) stmt, scope);
		case STMT_break:
			return visitBreak((Stmt.Break) stmt, scope);
		case STMT_continue:
			return visitContinue((Stmt.Continue) stmt, scope);
		case STMT_debug:
			return visitDebug((Stmt.Debug) stmt, scope);
		case STMT_dowhile:
			return visitDoWhile((Stmt.DoWhile) stmt, scope);
		case STMT_fail:
			return visitFail((Stmt.Fail) stmt, scope);
		case STMT_for:
			return visitFor((Stmt.For) stmt, scope);
		case STMT_if:
		case STMT_ifelse:
			return visitIfElse((Stmt.IfElse) stmt, scope);
		case STMT_initialiser:
		case STMT_initialiservoid:
				return visitInitialiser((Stmt.Initialiser) stmt);
		case EXPR_invoke:
			return visitInvokeStmt((Expr.Invoke) stmt);
		case EXPR_indirectinvoke:
			return visitIndirectInvokeStmt((Expr.IndirectInvoke) stmt);
		case STMT_namedblock:
			return visitNamedBlock((Stmt.NamedBlock) stmt, scope);
		case STMT_return:
		case STMT_returnvoid:
			return visitReturn((Stmt.Return) stmt, scope);
		case STMT_skip:
			return visitSkip((Stmt.Skip) stmt, scope);
		case STMT_switch:
			return visitSwitch((Stmt.Switch) stmt, scope);
		case STMT_while:
			return visitWhile((Stmt.While) stmt, scope);
		default:
			throw new IllegalArgumentException("unknown statement encountered (" + stmt.getClass().getName() + ")");
		}
	}

	public S visitAssert(Stmt.Assert stmt, EnclosingScope scope) {
		E operand = visitExpression(stmt.getCondition());
		return constructAssert(stmt,operand);
	}

	public S visitAssign(Stmt.Assign stmt, EnclosingScope scope) {
		Tuple<Type> targets = stmt.getLeftHandSide().map((LVal l) -> l.getType());
		List<E> lvals = visitLVals(stmt.getLeftHandSide(), scope);
		List<E> rvals = visitExpressions(stmt.getRightHandSide());
		return constructAssign(stmt,lvals,rvals);
	}

	public List<E> visitLVals(Tuple<LVal> lvals, EnclosingScope scope) {
		ArrayList<E> ls = new ArrayList<>();
		for (int i = 0; i != lvals.size(); ++i) {
			LVal lval = lvals.get(i);
			ls.add(visitLVal(lval));
		}
		return ls;
	}

	public E visitLVal(LVal lval) {
		switch (lval.getOpcode()) {
		case EXPR_arrayaccess:
		case EXPR_arrayborrow: {
			Expr.ArrayAccess e = (Expr.ArrayAccess) lval;
			E src = visitLVal((WyilFile.LVal) e.getFirstOperand());
			E index = visitExpression(e.getSecondOperand());
			return constructArrayAccessLVal(e,src,index);
		}
		case EXPR_dereference: {
			Expr.Dereference e = (Expr.Dereference) lval;
			E src = visitLVal((WyilFile.LVal) e.getOperand());
			return constructDereferenceLVal(e,src);
		}
		case EXPR_fielddereference: {
			Expr.FieldDereference e = (Expr.FieldDereference) lval;
			E src = visitLVal((WyilFile.LVal) e.getOperand());
			return constructFieldDereferenceLVal(e,src);
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess e = (Expr.RecordAccess) lval;
			E src = visitLVal((WyilFile.LVal) e.getOperand());
			return constructRecordAccessLVal(e,src);
		}
		case EXPR_tupleinitialiser: {
			Expr.TupleInitialiser e = (Expr.TupleInitialiser) lval;
			Tuple<Expr> operands = e.getOperands();
			List<E> srcs = new ArrayList<>();
			for (int i = 0; i != e.size(); ++i) {
				srcs.add(visitLVal((WyilFile.LVal) operands.get(i)));
			}
			return constructTupleInitialiserLVal(e, srcs);
		}
		case EXPR_variablecopy:
		case EXPR_variablemove: {
			Expr.VariableAccess e = (Expr.VariableAccess) lval;
			return constructVariableAccessLVal(e);
		}
		default:
			throw new IllegalArgumentException("invalid lval: " + lval);
		}
	}

	public S visitAssume(Stmt.Assume stmt, EnclosingScope scope) {
		E operand = visitExpression(stmt.getCondition());
		return constructAssume(stmt,operand);
	}

	public S visitBlock(Stmt.Block stmt, EnclosingScope scope) {
		ArrayList<S> stmts = new ArrayList<>();
		for (int i = 0; i != stmt.size(); ++i) {
			S s = visitStatement(stmt.get(i), scope);
			if(s != null) {
				stmts.add(s);
			}
		}
		return constructBlock(stmt, stmts);
	}

	public S visitBreak(Stmt.Break stmt, EnclosingScope scope) {
		return constructBreak(stmt);
	}

	public S visitContinue(Stmt.Continue stmt, EnclosingScope scope) {
		return constructContinue(stmt);
	}

	public S visitDebug(Stmt.Debug stmt, EnclosingScope scope) {
		E operand = visitExpression(stmt.getOperand());
		return constructDebug(stmt,operand);
	}

	public S visitDoWhile(Stmt.DoWhile stmt, EnclosingScope scope) {
		S body = visitStatement(stmt.getBody(), scope);
		E condition = visitExpression(stmt.getCondition());
		List<E> invariant = visitHomogoneousExpressions(stmt.getInvariant());
		return constructDoWhile(stmt, body, condition, invariant);
	}

	public S visitFail(Stmt.Fail stmt, EnclosingScope scope) {
		return constructFail(stmt);
	}

	public S visitFor(Stmt.For stmt, EnclosingScope scope) {
		Expr.ArrayRange range = (Expr.ArrayRange) stmt.getVariable().getInitialiser();
		E start = visitExpression(range.getFirstOperand());
		E end = visitExpression(range.getSecondOperand());
		List<E> invariant = visitHomogoneousExpressions(stmt.getInvariant());
		S body = visitStatement(stmt.getBody(), scope);
		return constructFor(stmt, new Pair<>(start, end), invariant, body);
	}

	public S visitIfElse(Stmt.IfElse stmt, EnclosingScope scope) {
		E condition = visitExpression(stmt.getCondition());
		S trueBranch = visitStatement(stmt.getTrueBranch(), scope);
		S falseBranch = null;
		if (stmt.hasFalseBranch()) {
			falseBranch = visitStatement(stmt.getFalseBranch(), scope);
		}
		return constructIfElse(stmt, condition, trueBranch, falseBranch);
	}

	public S visitInitialiser(Stmt.Initialiser stmt) {
		E initialiser = null;
		if (stmt.hasInitialiser()) {
			initialiser = visitExpression(stmt.getInitialiser());
		}
		return constructInitialiser(stmt, initialiser);
	}

	public S visitInvokeStmt(Expr.Invoke stmt) {
		List<E> operands = visitHeterogenousExpressions(stmt.getOperands());
		return constructInvokeStmt(stmt, operands);
	}

	public S visitIndirectInvokeStmt(Expr.IndirectInvoke stmt) {
		E operand = visitExpression(stmt.getSource());
		List<E> operands = visitHeterogenousExpressions(stmt.getArguments());
		return constructIndirectInvokeStmt(stmt, operand, operands);
	}

	public S visitNamedBlock(Stmt.NamedBlock stmt, EnclosingScope scope) {
		Stmt.Block blk = stmt.getBlock();
		// Create an appropriate scope for this block
		scope = new NamedBlockScope(scope, stmt);
		//
		ArrayList<S> stmts = new ArrayList<>();
		for (int i = 0; i != blk.size(); ++i) {
			stmts.add(visitStatement(blk.get(i), scope));
		}
		//
		return constructNamedBlock(stmt,stmts);
	}

	public S visitReturn(Stmt.Return stmt, EnclosingScope scope) {
		if (stmt.hasReturn()) {
			E returns = visitExpression(stmt.getReturn());
			return constructReturn(stmt, returns);
		} else {
			return constructReturn(stmt, null);
		}
	}

	public S visitSkip(Stmt.Skip stmt, EnclosingScope scope) {
		return constructSkip(stmt);
	}

	public S visitSwitch(Stmt.Switch stmt, EnclosingScope scope) {
		E condition = visitExpression(stmt.getCondition());
		Tuple<Stmt.Case> cases = stmt.getCases();
		ArrayList<Pair<List<E>,S>> cs = new ArrayList<>();
		for (int i = 0; i != cases.size(); ++i) {
			cs.add(visitCase(cases.get(i), scope));
		}
		return constructSwitch(stmt,condition,cs);
	}

	public Pair<List<E>,S> visitCase(Stmt.Case stmt, EnclosingScope scope) {
		List<E> conditions = visitHomogoneousExpressions(stmt.getConditions());
		S body = visitStatement(stmt.getBlock(), scope);
		return new Pair<>(conditions,body);
	}

	public S visitWhile(Stmt.While stmt, EnclosingScope scope) {
		E condition = visitExpression(stmt.getCondition());
		List<E> invariant = visitHomogoneousExpressions(stmt.getInvariant());
		S body = visitStatement(stmt.getBody(), scope);
		return constructWhile(stmt,condition,invariant,body);
	}

	public List<E> visitExpressions(Tuple<Expr> exprs) {
		ArrayList<E> returns = new ArrayList<>();
		for (int i = 0; i != exprs.size(); ++i) {
			returns.add(visitExpression(exprs.get(i)));
		}
		return returns;
	}

	/**
	 * Translate a sequence of expressions, all of which have different types.
	 *
	 * @param exprs
	 * @param target
	 * @param environment
	 * @return
	 */
	public List<E> visitHeterogenousExpressions(Tuple<Expr> exprs) {
		ArrayList<E> results = new ArrayList<>();
		for (int i = 0; i != exprs.size(); ++i) {
			results.add(visitExpression(exprs.get(i)));
		}
		return results;
	}

	/**
	 * Translate a sequence of expressions, all of which have the same type.
	 *
	 * @param exprs
	 * @param target
	 * @param environment
	 * @return
	 */
	public List<E> visitHomogoneousExpressions(Tuple<Expr> exprs) {
		ArrayList<E> results = new ArrayList<>();
		for (int i = 0; i != exprs.size(); ++i) {
			results.add(visitExpression(exprs.get(i)));
		}
		return results;
	}

	/**
	 * Visit a given expression which is being assigned to a location of a given
	 * type.
	 *
	 * @param expr
	 * @param target
	 */
	public E visitExpression(Expr expr) {
		meter.step("expression");
		//
		switch (expr.getOpcode()) {
		// Terminals
		case EXPR_constant:
			return visitConstant((Expr.Constant) expr);
		case EXPR_indirectinvoke:
			return visitIndirectInvoke((Expr.IndirectInvoke) expr);
		case EXPR_lambdaaccess:
			return visitLambdaAccess((Expr.LambdaAccess) expr);
		case DECL_lambda:
			return visitLambda((Decl.Lambda) expr);
		case EXPR_staticvariable:
			return visitStaticVariableAccess((Expr.StaticVariableAccess) expr);
		case EXPR_variablecopy:
		case EXPR_variablemove:
			return visitVariableAccess((Expr.VariableAccess) expr);
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
			return visitUnaryOperator((Expr.UnaryOperator) expr);
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
			return visitBinaryOperator((Expr.BinaryOperator) expr);
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
			return visitNaryOperator((Expr.NaryOperator) expr);
		// Ternary Operators
		case EXPR_arrayupdate:
			return visitTernaryOperator((Expr.TernaryOperator) expr);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitUnaryOperator(Expr.UnaryOperator expr) {
		switch (expr.getOpcode()) {
		// Unary Operators
		case EXPR_cast:
			return visitCast((Expr.Cast) expr);
		case EXPR_integernegation: {
			return visitIntegerNegation((Expr.IntegerNegation) expr);
		}
		case EXPR_is:
			return visitIs((Expr.Is) expr);
		case EXPR_logicalnot:
			return visitLogicalNot((Expr.LogicalNot) expr);
		case EXPR_logicalexistential:
			return visitExistentialQuantifier((Expr.ExistentialQuantifier) expr);
		case EXPR_logicaluniversal:
			return visitUniversalQuantifier((Expr.UniversalQuantifier) expr);
		case EXPR_bitwisenot:
			return visitBitwiseComplement((Expr.BitwiseComplement) expr);
		case EXPR_dereference:
			return visitDereference((Expr.Dereference) expr);
		case EXPR_fielddereference:
			return visitFieldDereference((Expr.FieldDereference) expr);
		case EXPR_new:
			return visitNew((Expr.New) expr);
		case EXPR_old:
			return visitOld((Expr.Old) expr);
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			return visitRecordAccess((Expr.RecordAccess) expr);
		case EXPR_arraylength: {
			return visitArrayLength((Expr.ArrayLength) expr);
		}
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitBinaryOperator(Expr.BinaryOperator expr) {
		switch (expr.getOpcode()) {
		// Binary Operators
		case EXPR_equal:
			return visitEqual((Expr.Equal) expr);
		case EXPR_notequal:
			return visitNotEqual((Expr.NotEqual) expr);
		case EXPR_logicalimplication:
			return visitLogicalImplication((Expr.LogicalImplication) expr);
		case EXPR_logicaliff:
			return visitLogicalIff((Expr.LogicalIff) expr);
		case EXPR_integerlessthan:
			return visitIntegerLessThan((Expr.IntegerLessThan) expr);
		case EXPR_integerlessequal:
			return visitIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr);
		case EXPR_integergreaterthan:
			return visitIntegerGreaterThan((Expr.IntegerGreaterThan) expr);
		case EXPR_integergreaterequal:
			return visitIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr);
		case EXPR_integeraddition: {
			return visitIntegerAddition((Expr.IntegerAddition) expr);
		}
		case EXPR_integersubtraction: {
			return visitIntegerSubtraction((Expr.IntegerSubtraction) expr);
		}
		case EXPR_integermultiplication: {
			return visitIntegerMultiplication((Expr.IntegerMultiplication) expr);
		}
		case EXPR_integerdivision: {
			return visitIntegerDivision((Expr.IntegerDivision) expr);
		}
		case EXPR_integerremainder: {
			return visitIntegerRemainder((Expr.IntegerRemainder) expr);
		}
		case EXPR_bitwiseshl:
			return visitBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr);
		case EXPR_bitwiseshr:
			return visitBitwiseShiftRight((Expr.BitwiseShiftRight) expr);
		case EXPR_arraygenerator: {
			return visitArrayGenerator((Expr.ArrayGenerator) expr);
		}
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			return visitArrayAccess((Expr.ArrayAccess) expr);
		case EXPR_arrayrange: {
			return visitArrayRange((Expr.ArrayRange) expr);
		}
		case EXPR_recordupdate:
			return visitRecordUpdate((Expr.RecordUpdate) expr);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitTernaryOperator(Expr.TernaryOperator expr) {
		switch (expr.getOpcode()) {
		// Ternary Operators
		case EXPR_arrayupdate:
			return visitArrayUpdate((Expr.ArrayUpdate) expr);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitNaryOperator(Expr.NaryOperator expr) {
		switch (expr.getOpcode()) {
		// Nary Operators
		case EXPR_arrayinitialiser:
			return visitArrayInitialiser((Expr.ArrayInitialiser) expr);
		case EXPR_bitwiseand:
			return visitBitwiseAnd((Expr.BitwiseAnd) expr);
		case EXPR_bitwiseor:
			return visitBitwiseOr((Expr.BitwiseOr) expr);
		case EXPR_bitwisexor:
			return visitBitwiseXor((Expr.BitwiseXor) expr);
		case EXPR_invoke:
			return visitInvoke((Expr.Invoke) expr);
		case EXPR_logicaland:
			return visitLogicalAnd((Expr.LogicalAnd) expr);
		case EXPR_logicalor:
			return visitLogicalOr((Expr.LogicalOr) expr);
		case EXPR_recordinitialiser:
			return visitRecordInitialiser((Expr.RecordInitialiser) expr);
		case EXPR_tupleinitialiser:
			return visitTupleInitialiser((Expr.TupleInitialiser) expr);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitArrayAccess(Expr.ArrayAccess expr) {
		E source = visitExpression(expr.getFirstOperand());
		E index = visitExpression(expr.getSecondOperand());
		return constructArrayAccess(expr,source,index);
	}

	public E visitArrayLength(Expr.ArrayLength expr) {
		E source = visitExpression(expr.getOperand());
		return constructArrayLength(expr,source);
	}

	public E visitArrayGenerator(Expr.ArrayGenerator expr) {
		E value = visitExpression(expr.getFirstOperand());
		E length = visitExpression(expr.getSecondOperand());
		return constructArrayGenerator(expr,value,length);
	}

	public E visitArrayInitialiser(Expr.ArrayInitialiser expr) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands());
		return constructArrayInitialiser(expr,operands);
	}

	public E visitArrayRange(Expr.ArrayRange expr) {
		throw new UnsupportedOperationException();
	}

	public E visitArrayUpdate(Expr.ArrayUpdate expr) {
		throw new UnsupportedOperationException();
	}

	public E visitBitwiseComplement(Expr.BitwiseComplement expr) {
		E operand = visitExpression(expr.getOperand());
		return constructBitwiseComplement(expr,operand);
	}

	public E visitBitwiseAnd(Expr.BitwiseAnd expr) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands());
		return constructBitwiseAnd(expr,operands);
	}

	public E visitBitwiseOr(Expr.BitwiseOr expr) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands());
		return constructBitwiseOr(expr,operands);
	}

	public E visitBitwiseXor(Expr.BitwiseXor expr) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands());
		return constructBitwiseXor(expr,operands);
	}

	public E visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructBitwiseShiftLeft(expr,lhs,rhs);
	}

	public E visitBitwiseShiftRight(Expr.BitwiseShiftRight expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructBitwiseShiftRight(expr,lhs,rhs);
	}

	public E visitCast(Expr.Cast expr) {
		E operand = visitExpression(expr.getOperand());
		return constructCast(expr,operand);
	}

	public E visitConstant(Expr.Constant expr) {
		return constructConstant(expr);
	}

	public E visitDereference(Expr.Dereference expr) {
		E operand = visitExpression(expr.getOperand());
		return constructDereference(expr,operand);
	}

	public E visitFieldDereference(Expr.FieldDereference expr) {
		E operand = visitExpression(expr.getOperand());
		return constructFieldDereference(expr,operand);
	}

	public E visitEqual(Expr.Equal expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructEqual(expr,lhs,rhs);
	}

	public E visitIntegerLessThan(Expr.IntegerLessThan expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructIntegerLessThan(expr,lhs,rhs);
	}

	public E visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructIntegerLessThanOrEqual(expr,lhs,rhs);
	}

	public E visitIntegerGreaterThan(Expr.IntegerGreaterThan expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructIntegerGreaterThan(expr,lhs,rhs);
	}

	public E visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructIntegerGreaterThanOrEqual(expr,lhs,rhs);
	}

	public E visitIntegerNegation(Expr.IntegerNegation expr) {
		E operand = visitExpression(expr.getOperand());
		return constructIntegerNegation(expr,operand);
	}

	public E visitIntegerAddition(Expr.IntegerAddition expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructIntegerAddition(expr,lhs,rhs);
	}

	public E visitIntegerSubtraction(Expr.IntegerSubtraction expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructIntegerSubtraction(expr,lhs,rhs);
	}

	public E visitIntegerMultiplication(Expr.IntegerMultiplication expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructIntegerMultiplication(expr,lhs,rhs);
	}

	public E visitIntegerDivision(Expr.IntegerDivision expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructIntegerDivision(expr,lhs,rhs);
	}

	public E visitIntegerRemainder(Expr.IntegerRemainder expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructIntegerRemainder(expr,lhs,rhs);
	}

	public E visitIs(Expr.Is expr) {
		E operand = visitExpression(expr.getOperand());
		return constructIs(expr,operand);
	}

	public E visitLogicalAnd(Expr.LogicalAnd expr) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands());
		return constructLogicalAnd(expr,operands);
	}

	public E visitLogicalImplication(Expr.LogicalImplication expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructLogicalImplication(expr,lhs,rhs);
	}

	public E visitLogicalIff(Expr.LogicalIff expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructLogicalIff(expr,lhs,rhs);
	}

	public E visitLogicalNot(Expr.LogicalNot expr) {
		E operand = visitExpression(expr.getOperand());
		return constructLogicalNot(expr,operand);
	}

	public E visitLogicalOr(Expr.LogicalOr expr) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands());
		return constructLogicalOr(expr,operands);
	}

	public E visitExistentialQuantifier(Expr.ExistentialQuantifier expr) {
		Tuple<Decl.StaticVariable> parameters = expr.getParameters();
		ArrayList<Pair<E,E>> ranges = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.StaticVariable parameter = parameters.get(i);
			// NOTE: Currently ranges can only appear in quantifiers. Eventually, this will
			// be deprecated.
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			E start = visitExpression(range.getFirstOperand());
			E end = visitExpression(range.getSecondOperand());
			ranges.add(new Pair<>(start,end));
		}
		E body = visitExpression(expr.getOperand());
		return constructExistentialQuantifier(expr,ranges,body);
	}

	public E visitUniversalQuantifier(Expr.UniversalQuantifier expr) {
		Tuple<Decl.StaticVariable> parameters = expr.getParameters();
		ArrayList<Pair<E,E>> ranges = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.StaticVariable parameter = parameters.get(i);
			// NOTE: Currently ranges can only appear in quantifiers. Eventually, this will
			// be deprecated.
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			E start = visitExpression(range.getFirstOperand());
			E end = visitExpression(range.getSecondOperand());
			ranges.add(new Pair<>(start,end));
		}
		E body = visitExpression(expr.getOperand());
		return constructUniversalQuantifier(expr,ranges,body);
	}

	public E visitInvoke(Expr.Invoke expr) {
		List<E> operands = visitHeterogenousExpressions(expr.getOperands());
		return constructInvoke(expr, operands);
	}

	public E visitIndirectInvoke(Expr.IndirectInvoke expr) {
		E operand = visitExpression(expr.getSource());
		List<E> operands = visitHeterogenousExpressions(expr.getArguments());
		return constructIndirectInvoke(expr, operand, operands);
	}

	public E visitLambdaAccess(Expr.LambdaAccess expr) {
		return constructLambdaAccess(expr);
	}

	public E visitNew(Expr.New expr) {
		E operand = visitExpression(expr.getOperand());
		return constructNew(expr,operand);
	}

	public E visitOld(Expr.Old expr) {
		E operand = visitExpression(expr.getOperand());
		return constructOld(expr,operand);
	}

	public E visitNotEqual(Expr.NotEqual expr) {
		E lhs = visitExpression(expr.getFirstOperand());
		E rhs = visitExpression(expr.getSecondOperand());
		return constructNotEqual(expr,lhs,rhs);
	}

	public E visitRecordAccess(Expr.RecordAccess expr) {
		E src = visitExpression(expr.getOperand());
		return constructRecordAccess(expr, src);
	}

	public E visitRecordInitialiser(Expr.RecordInitialiser expr) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		List<E> args = new ArrayList<>();
		for (int i = 0; i != fields.size(); ++i) {
			Expr operand = operands.get(i);
			args.add(visitExpression(operand));
		}
		return constructRecordInitialiser(expr,args);
	}

	public E visitRecordUpdate(Expr.RecordUpdate expr) {
		E src = visitExpression(expr.getFirstOperand());
		E val = visitExpression(expr.getSecondOperand());
		// TODO: implement me!
		// return constructRecordUpdate(expr,src,val);
		throw new UnsupportedOperationException();
	}

	public E visitTupleInitialiser(Expr.TupleInitialiser expr) {
		Tuple<Expr> operands = expr.getOperands();
		List<E> args = new ArrayList<>();
		for (int i = 0; i != operands.size(); ++i) {
			Expr operand = operands.get(i);
			args.add(visitExpression(operand));
		}
		return constructTupleInitialiser(expr,args);
	}

	public E visitStaticVariableAccess(Expr.StaticVariableAccess expr) {
		return constructStaticVariableAccess(expr);
	}

	public E visitVariableAccess(Expr.VariableAccess expr) {
		return constructVariableAccess(expr);
	}

	// ====================================================================================
	// Declaration Constructors
	// ====================================================================================

	public abstract D constructImport(Decl.Import d);

	public abstract D constructType(Decl.Type d, List<E> invariant);

	public abstract D constructStaticVariable(Decl.StaticVariable d, E initialiser);

	public abstract D constructProperty(Decl.Property decl, E body);

	public abstract D constructVariant(Decl.Variant decl, List<E> clauses);

	public abstract D constructFunction(Decl.Function d, List<E> precondition, List<E> postcondition, S body);

	public abstract D constructMethod(Decl.Method d, List<E> precondition, List<E> postcondition, S body);

	public abstract E constructLambda(Decl.Lambda decl, E body);

	// ====================================================================================
	// Statement Constructors
	// ====================================================================================

	public abstract S constructAssert(Stmt.Assert stmt, E condition);

	public abstract S constructAssign(Stmt.Assign stmt, List<E> lvals, List<E> rvals);

	public abstract S constructAssume(Stmt.Assume stmt, E condition);

	public abstract S constructBlock(Stmt.Block stmt, List<S> stmts);

	public abstract S constructBreak(Stmt.Break stmt);

	public abstract S constructContinue(Stmt.Continue stmt);

	public abstract S constructDebug(Stmt.Debug stmt, E operand);

	public abstract S constructDoWhile(Stmt.DoWhile stmt, S body, E condition, List<E> invariant);

	public abstract S constructFail(Stmt.Fail stmt);

	public abstract S constructFor(Stmt.For stmt, Pair<E,E> range, List<E> invariant, S body);

	public abstract S constructIfElse(Stmt.IfElse stmt, E condition, S trueBranch, S falseBranch);

	public abstract S constructInitialiser(Stmt.Initialiser stmt, E initialiser);

	public abstract S constructInvokeStmt(Expr.Invoke expr, List<E> arguments);

	public abstract S constructIndirectInvokeStmt(Expr.IndirectInvoke expr, E source, List<E> arguments);

	public abstract S constructNamedBlock(Stmt.NamedBlock stmt, List<S> stmts);

	public abstract S constructReturn(Stmt.Return stmt, E ret);

	public abstract S constructSkip(Stmt.Skip stmt);

	public abstract S constructSwitch(Stmt.Switch stmt, E condition, List<Pair<List<E>,S>> cases);

	public abstract S constructWhile(Stmt.While stmt, E condition, List<E> invariant, S body);

	// ====================================================================================
	// LVal Constructors
	// ====================================================================================

	public abstract E constructArrayAccessLVal(Expr.ArrayAccess expr, E source, E index);

	public abstract E constructDereferenceLVal(Expr.Dereference expr, E operand);

	public abstract E constructFieldDereferenceLVal(Expr.FieldDereference expr, E operand);

	public abstract E constructRecordAccessLVal(Expr.RecordAccess expr, E source);

	public abstract E constructTupleInitialiserLVal(Expr.TupleInitialiser expr, List<E> source);

	public abstract E constructVariableAccessLVal(Expr.VariableAccess expr);

	// ====================================================================================
	// Expression Constructors
	// ====================================================================================

	public abstract E constructArrayAccess(Expr.ArrayAccess expr, E source, E index);

	public abstract E constructArrayLength(Expr.ArrayLength expr, E source);

	public abstract E constructArrayGenerator(Expr.ArrayGenerator expr, E value, E length);

	public abstract E constructArrayInitialiser(Expr.ArrayInitialiser expr, List<E> values);

	public abstract E constructBitwiseComplement(Expr.BitwiseComplement expr, E operand);

	public abstract E constructBitwiseAnd(Expr.BitwiseAnd expr, List<E> operands);

	public abstract E constructBitwiseOr(Expr.BitwiseOr expr, List<E> operands);

	public abstract E constructBitwiseXor(Expr.BitwiseXor expr, List<E> operands);

	public abstract E constructBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, E lhs, E rhs);

	public abstract E constructBitwiseShiftRight(Expr.BitwiseShiftRight expr, E lhs, E rhs);

	public abstract E constructCast(Expr.Cast expr, E operand);

	public abstract E constructConstant(Expr.Constant expr);

	public abstract E constructDereference(Expr.Dereference expr, E operand);

	public abstract E constructFieldDereference(Expr.FieldDereference expr, E operand);

	public abstract E constructEqual(Expr.Equal expr, E lhs, E rhs);

	public abstract E constructIntegerLessThan(Expr.IntegerLessThan expr, E lhs, E rhs);

	public abstract E constructIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, E lhs, E rhs);

	public abstract E constructIntegerGreaterThan(Expr.IntegerGreaterThan expr, E lhs, E rhs);

	public abstract E constructIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, E lhs, E rhs);

	public abstract E constructIntegerNegation(Expr.IntegerNegation expr, E operand);

	public abstract E constructIntegerAddition(Expr.IntegerAddition expr, E lhs, E rhs);

	public abstract E constructIntegerSubtraction(Expr.IntegerSubtraction expr, E lhs, E rhs);

	public abstract E constructIntegerMultiplication(Expr.IntegerMultiplication expr, E lhs, E rhs);

	public abstract E constructIntegerDivision(Expr.IntegerDivision expr, E lhs, E rhs);

	public abstract E constructIntegerRemainder(Expr.IntegerRemainder expr, E lhs, E rhs);

	public abstract E constructIs(Expr.Is expr, E operand);

	public abstract E constructLogicalAnd(Expr.LogicalAnd expr, List<E> operands);

	public abstract E constructLogicalImplication(Expr.LogicalImplication expr, E lhs, E rhs);

	public abstract E constructLogicalIff(Expr.LogicalIff expr, E lhs, E rhs);

	public abstract E constructLogicalNot(Expr.LogicalNot expr, E operand);

	public abstract E constructLogicalOr(Expr.LogicalOr expr, List<E> operands);

	public abstract E constructExistentialQuantifier(Expr.ExistentialQuantifier expr, List<Pair<E,E>> ranges, E body);

	public abstract E constructUniversalQuantifier(Expr.UniversalQuantifier expr, List<Pair<E,E>> ranges, E body);

	public abstract E constructInvoke(Expr.Invoke expr, List<E> arguments);

	public abstract E constructIndirectInvoke(Expr.IndirectInvoke expr, E source, List<E> arguments);

	public abstract E constructLambdaAccess(Expr.LambdaAccess expr);

	public abstract E constructNew(Expr.New expr, E operand);

	public abstract E constructNotEqual(Expr.NotEqual expr, E lhs, E rhs);

	public abstract E constructOld(Expr.Old expr, E operand);

	public abstract E constructRecordAccess(Expr.RecordAccess expr, E source);

	public abstract E constructRecordInitialiser(Expr.RecordInitialiser expr, List<E> operands);

	public abstract E constructTupleInitialiser(Expr.TupleInitialiser expr, List<E> operands);

	public abstract E constructStaticVariableAccess(Expr.StaticVariableAccess expr);

	public abstract E constructVariableAccess(Expr.VariableAccess expr);

	// ====================================================================================
	// Coercions
	// ====================================================================================

	/**
	 * Apply an implicit coercion (where appropriate) from a term representing the
	 * given source type to a term representing the given target type. This may be a
	 * no-operation if no coercion is required.
	 *
	 * @param target
	 * @param source
	 * @param expr
	 * @return
	 */
	public abstract S applyImplicitCoercion(Type target, Type source, S expr);

	// ====================================================================================
	// Helpers
	// ====================================================================================

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
			Type.Nominal t = (Type.Nominal) child;
			Decl.Type decl = t.getLink().getTarget();
			return isDerivation(parent, decl.getType());
		} else {
			return false;
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
	public abstract static class EnclosingScope {
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

	/**
	 * Represents the enclosing scope for a function or method declaration.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class FunctionOrMethodOrPropertyScope extends EnclosingScope {
		private final Decl.FunctionOrMethod declaration;

		public FunctionOrMethodOrPropertyScope(Decl.FunctionOrMethod declaration) {
			super(null);
			this.declaration = declaration;
		}

		public Decl.FunctionOrMethod getDeclaration() {
			return declaration;
		}
	}

	private static class NamedBlockScope extends EnclosingScope {
		private final Stmt.NamedBlock stmt;

		public NamedBlockScope(EnclosingScope parent, Stmt.NamedBlock stmt) {
			super(parent);
			this.stmt = stmt;
		}
	}
}