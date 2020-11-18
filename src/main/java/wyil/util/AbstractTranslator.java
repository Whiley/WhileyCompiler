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

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyfs.util.Pair;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.LVal;
import wyil.lang.WyilFile.Stmt;
import wyil.lang.WyilFile.Type;
import wyil.util.Subtyping;

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
public abstract class AbstractTranslator<D,S,E extends S> {
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

	public E visitLambda(Decl.Lambda decl, Environment environment) {
		//
		E body = visitExpression(decl.getBody(), environment);
		return constructLambda(decl,body);
	}

	public D visitStaticVariable(Decl.StaticVariable decl) {
		Environment environment = new Environment();
		E initialiser = visitExpression(decl.getInitialiser(), environment);
		return constructStaticVariable(decl,initialiser);
	}

	public D visitType(Decl.Type decl) {
		Environment environment = new Environment();
		List<E> invariant = visitHomogoneousExpressions(decl.getInvariant(), environment);
		return constructType(decl, invariant);
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
		List<E> clauses = visitHomogoneousExpressions(decl.getInvariant(), environment);
		return constructProperty(decl,clauses);
	}

	public D visitFunction(Decl.Function decl) {
		Environment environment = new Environment();
		List<E> precondition = visitHomogoneousExpressions(decl.getRequires(), environment);
		List<E> postcondition = visitHomogoneousExpressions(decl.getEnsures(), environment);
		S body = visitBlock(decl.getBody(), environment, new FunctionOrMethodScope(decl));
		return constructFunction(decl,precondition,postcondition,body);
	}

	public D visitMethod(Decl.Method decl) {
		// Construct environment relation
		Environment environment = new Environment();
		//
		List<E> precondition = visitHomogoneousExpressions(decl.getRequires(), environment);
		List<E> postcondition = visitHomogoneousExpressions(decl.getEnsures(), environment);
		S body = visitBlock(decl.getBody(), environment, new FunctionOrMethodScope(decl));
		return constructMethod(decl,precondition,postcondition,body);
	}

	public S visitStatement(Stmt stmt, Environment environment, EnclosingScope scope) {
		meter.step("statement");
		//
		switch (stmt.getOpcode()) {
		case STMT_assert:
			return visitAssert((Stmt.Assert) stmt, environment, scope);
		case STMT_assign:
			return visitAssign((Stmt.Assign) stmt, environment, scope);
		case STMT_assume:
			return visitAssume((Stmt.Assume) stmt, environment, scope);
		case STMT_block:
			return visitBlock((Stmt.Block) stmt, environment, scope);
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
		case STMT_for:
			return visitFor((Stmt.For) stmt, environment, scope);
		case STMT_if:
		case STMT_ifelse:
			return visitIfElse((Stmt.IfElse) stmt, environment, scope);
		case STMT_initialiser:
		case STMT_initialiservoid:
				return visitInitialiser((Stmt.Initialiser) stmt, environment);
		case EXPR_invoke:
			return visitInvoke((Expr.Invoke) stmt, environment);
		case EXPR_indirectinvoke:
			return visitIndirectInvoke((Expr.IndirectInvoke) stmt, environment);
		case STMT_namedblock:
			return visitNamedBlock((Stmt.NamedBlock) stmt, environment, scope);
		case STMT_return:
		case STMT_returnvoid:
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
		E operand = visitExpression(stmt.getCondition(), environment);
		return constructAssert(stmt,operand);
	}

	public S visitAssign(Stmt.Assign stmt, Environment environment, EnclosingScope scope) {
		Tuple<Type> targets = stmt.getLeftHandSide().map((LVal l) -> l.getType());
		List<E> lvals = visitLVals(stmt.getLeftHandSide(), environment, scope);
		List<E> rvals = visitExpressions(stmt.getRightHandSide(), environment);
		return constructAssign(stmt,lvals,rvals);
	}

	public List<E> visitLVals(Tuple<LVal> lvals, Environment environment, EnclosingScope scope) {
		ArrayList<E> ls = new ArrayList<>();
		for (int i = 0; i != lvals.size(); ++i) {
			LVal lval = lvals.get(i);
			ls.add(visitLVal(lval, environment));
		}
		return ls;
	}

	public E visitLVal(LVal lval, Environment environment) {
		switch (lval.getOpcode()) {
		case EXPR_arrayaccess:
		case EXPR_arrayborrow: {
			Expr.ArrayAccess e = (Expr.ArrayAccess) lval;
			E src = visitLVal((WyilFile.LVal) e.getFirstOperand(), environment);
			E index = visitExpression(e.getSecondOperand(), environment);
			return constructArrayAccessLVal(e,src,index);
		}
		case EXPR_dereference: {
			Expr.Dereference e = (Expr.Dereference) lval;
			E src = visitLVal((WyilFile.LVal) e.getOperand(), environment);
			return constructDereferenceLVal(e,src);
		}
		case EXPR_fielddereference: {
			Expr.FieldDereference e = (Expr.FieldDereference) lval;
			E src = visitLVal((WyilFile.LVal) e.getOperand(), environment);
			return constructFieldDereferenceLVal(e,src);
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess e = (Expr.RecordAccess) lval;
			E src = visitLVal((WyilFile.LVal) e.getOperand(), environment);
			return constructRecordAccessLVal(e,src);
		}
		case EXPR_tupleinitialiser: {
			Expr.TupleInitialiser e = (Expr.TupleInitialiser) lval;
			Tuple<Expr> operands = e.getOperands();
			List<E> srcs = new ArrayList<>();
			for (int i = 0; i != e.size(); ++i) {
				srcs.add(visitLVal((WyilFile.LVal) operands.get(i), environment));
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

	public S visitAssume(Stmt.Assume stmt, Environment environment, EnclosingScope scope) {
		E operand = visitExpression(stmt.getCondition(), environment);
		return constructAssume(stmt,operand);
	}

	public S visitBlock(Stmt.Block stmt, Environment environment, EnclosingScope scope) {
		ArrayList<S> stmts = new ArrayList<>();
		for (int i = 0; i != stmt.size(); ++i) {
			S s = visitStatement(stmt.get(i), environment, scope);
			if(s != null) {
				stmts.add(s);
			}
		}
		return constructBlock(stmt, stmts);
	}

	public S visitBreak(Stmt.Break stmt, Environment environment, EnclosingScope scope) {
		return constructBreak(stmt);
	}

	public S visitContinue(Stmt.Continue stmt, Environment environment, EnclosingScope scope) {
		return constructContinue(stmt);
	}

	public S visitDebug(Stmt.Debug stmt, Environment environment, EnclosingScope scope) {
		E operand = visitExpression(stmt.getOperand(), environment);
		return constructDebug(stmt,operand);
	}

	public S visitDoWhile(Stmt.DoWhile stmt, Environment environment, EnclosingScope scope) {
		S body = visitStatement(stmt.getBody(), environment, scope);
		E condition = visitExpression(stmt.getCondition(), environment);
		List<E> invariant = visitHomogoneousExpressions(stmt.getInvariant(), environment);
		return constructDoWhile(stmt, body, condition, invariant);
	}

	public S visitFail(Stmt.Fail stmt, Environment environment, EnclosingScope scope) {
		return constructFail(stmt);
	}

	public S visitFor(Stmt.For stmt, Environment environment, EnclosingScope scope) {
		Expr.ArrayRange range = (Expr.ArrayRange) stmt.getVariable().getInitialiser();
		E start = visitExpression(range.getFirstOperand(),environment);
		E end = visitExpression(range.getSecondOperand(),environment);
		List<E> invariant = visitHomogoneousExpressions(stmt.getInvariant(), environment);
		S body = visitStatement(stmt.getBody(), environment, scope);
		return constructFor(stmt, new Pair<>(start, end), invariant, body);
	}

	public S visitIfElse(Stmt.IfElse stmt, Environment environment, EnclosingScope scope) {
		E condition = visitExpression(stmt.getCondition(), environment);
		S trueBranch = visitStatement(stmt.getTrueBranch(), environment, scope);
		S falseBranch = null;
		if (stmt.hasFalseBranch()) {
			falseBranch = visitStatement(stmt.getFalseBranch(), environment, scope);
		}
		return constructIfElse(stmt, condition, trueBranch, falseBranch);
	}

	public S visitInitialiser(Stmt.Initialiser stmt, Environment environment) {
		E initialiser = null;
		if (stmt.hasInitialiser()) {
			initialiser = visitExpression(stmt.getInitialiser(), environment);
		}
		return constructInitialiser(stmt, initialiser);
	}

	public S visitNamedBlock(Stmt.NamedBlock stmt, Environment environment, EnclosingScope scope) {
		Stmt.Block blk = stmt.getBlock();
		// Create an appropriate scope for this block
		scope = new NamedBlockScope(scope, stmt);
		//
		ArrayList<S> stmts = new ArrayList<>();
		for (int i = 0; i != blk.size(); ++i) {
			stmts.add(visitStatement(blk.get(i), environment, scope));
		}
		//
		return constructNamedBlock(stmt,stmts);
	}

	public S visitReturn(Stmt.Return stmt, Environment environment, EnclosingScope scope) {
		if (stmt.hasReturn()) {
			E returns = visitExpression(stmt.getReturn(), environment);
			return constructReturn(stmt, returns);
		} else {
			return constructReturn(stmt, null);
		}
	}

	public S visitSkip(Stmt.Skip stmt, Environment environment, EnclosingScope scope) {
		return constructSkip(stmt);
	}

	public S visitSwitch(Stmt.Switch stmt, Environment environment, EnclosingScope scope) {
		E condition = visitExpression(stmt.getCondition(), environment);
		Tuple<Stmt.Case> cases = stmt.getCases();
		ArrayList<Pair<List<E>,S>> cs = new ArrayList<>();
		for (int i = 0; i != cases.size(); ++i) {
			cs.add(visitCase(cases.get(i), environment, scope));
		}
		return constructSwitch(stmt,condition,cs);
	}

	public Pair<List<E>,S> visitCase(Stmt.Case stmt, Environment environment, EnclosingScope scope) {
		List<E> conditions = visitHomogoneousExpressions(stmt.getConditions(), environment);
		S body = visitStatement(stmt.getBlock(), environment, scope);
		return new Pair<>(conditions,body);
	}

	public S visitWhile(Stmt.While stmt, Environment environment, EnclosingScope scope) {
		E condition = visitExpression(stmt.getCondition(), environment);
		List<E> invariant = visitHomogoneousExpressions(stmt.getInvariant(), environment);
		S body = visitStatement(stmt.getBody(), environment, scope);
		return constructWhile(stmt,condition,invariant,body);
	}

	public List<E> visitExpressions(Tuple<Expr> exprs, Environment environment) {
		ArrayList<E> returns = new ArrayList<>();
		for (int i = 0; i != exprs.size(); ++i) {
			returns.add(visitExpression(exprs.get(i), environment));
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
	public List<E> visitHeterogenousExpressions(Tuple<Expr> exprs, Environment environment) {
		ArrayList<E> results = new ArrayList<>();
		for (int i = 0; i != exprs.size(); ++i) {
			results.add(visitExpression(exprs.get(i), environment));
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
	public List<E> visitHomogoneousExpressions(Tuple<Expr> exprs, Environment environment) {
		ArrayList<E> results = new ArrayList<>();
		for (int i = 0; i != exprs.size(); ++i) {
			results.add(visitExpression(exprs.get(i), environment));
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
	public E visitExpression(Expr expr, Environment environment) {
		meter.step("expression");
		//
		switch (expr.getOpcode()) {
		// Terminals
		case EXPR_constant:
			return visitConstant((Expr.Constant) expr, environment);
		case EXPR_indirectinvoke:
			return visitIndirectInvoke((Expr.IndirectInvoke) expr, environment);
		case EXPR_lambdaaccess:
			return visitLambdaAccess((Expr.LambdaAccess) expr, environment);
		case DECL_lambda:
			return visitLambda((Decl.Lambda) expr, environment);
		case EXPR_staticvariable:
			return visitStaticVariableAccess((Expr.StaticVariableAccess) expr, environment);
		case EXPR_variablecopy:
		case EXPR_variablemove:
			return visitVariableAccess((Expr.VariableAccess) expr, environment);
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
		case EXPR_recordaccess:
		case EXPR_recordborrow:
		case EXPR_arraylength:
			return visitUnaryOperator((Expr.UnaryOperator) expr, environment);
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
			return visitBinaryOperator((Expr.BinaryOperator) expr, environment);
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
			return visitNaryOperator((Expr.NaryOperator) expr, environment);
		// Ternary Operators
		case EXPR_arrayupdate:
			return visitTernaryOperator((Expr.TernaryOperator) expr, environment);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitUnaryOperator(Expr.UnaryOperator expr, Environment environment) {
		switch (expr.getOpcode()) {
		// Unary Operators
		case EXPR_cast:
			return visitCast((Expr.Cast) expr, environment);
		case EXPR_integernegation: {
			return visitIntegerNegation((Expr.IntegerNegation) expr, environment);
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
			return visitDereference((Expr.Dereference) expr, environment);
		case EXPR_fielddereference:
			return visitFieldDereference((Expr.FieldDereference) expr, environment);
		case EXPR_new: {
			return visitNew((Expr.New) expr, environment);
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			return visitRecordAccess((Expr.RecordAccess) expr, environment);
		case EXPR_arraylength: {
			return visitArrayLength((Expr.ArrayLength) expr, environment);
		}
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitBinaryOperator(Expr.BinaryOperator expr, Environment environment) {
		switch (expr.getOpcode()) {
		// Binary Operators
		case EXPR_equal:
			return visitEqual((Expr.Equal) expr, environment);
		case EXPR_notequal:
			return visitNotEqual((Expr.NotEqual) expr, environment);
		case EXPR_logicalimplication:
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
			return visitIntegerAddition((Expr.IntegerAddition) expr, environment);
		}
		case EXPR_integersubtraction: {
			return visitIntegerSubtraction((Expr.IntegerSubtraction) expr, environment);
		}
		case EXPR_integermultiplication: {
			return visitIntegerMultiplication((Expr.IntegerMultiplication) expr, environment);
		}
		case EXPR_integerdivision: {
			return visitIntegerDivision((Expr.IntegerDivision) expr, environment);
		}
		case EXPR_integerremainder: {
			return visitIntegerRemainder((Expr.IntegerRemainder) expr, environment);
		}
		case EXPR_bitwiseshl:
			return visitBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr, environment);
		case EXPR_bitwiseshr:
			return visitBitwiseShiftRight((Expr.BitwiseShiftRight) expr, environment);
		case EXPR_arraygenerator: {
			return visitArrayGenerator((Expr.ArrayGenerator) expr, environment);
		}
		case EXPR_arrayaccess:
		case EXPR_arrayborrow:
			return visitArrayAccess((Expr.ArrayAccess) expr, environment);
		case EXPR_arrayrange: {
			return visitArrayRange((Expr.ArrayRange) expr, environment);
		}
		case EXPR_recordupdate:
			return visitRecordUpdate((Expr.RecordUpdate) expr, environment);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitTernaryOperator(Expr.TernaryOperator expr, Environment environment) {
		switch (expr.getOpcode()) {
		// Ternary Operators
		case EXPR_arrayupdate:
			return visitArrayUpdate((Expr.ArrayUpdate) expr, environment);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitNaryOperator(Expr.NaryOperator expr, Environment environment) {
		switch (expr.getOpcode()) {
		// Nary Operators
		case EXPR_arrayinitialiser:
			return visitArrayInitialiser((Expr.ArrayInitialiser) expr, environment);
		case EXPR_bitwiseand:
			return visitBitwiseAnd((Expr.BitwiseAnd) expr, environment);
		case EXPR_bitwiseor:
			return visitBitwiseOr((Expr.BitwiseOr) expr, environment);
		case EXPR_bitwisexor:
			return visitBitwiseXor((Expr.BitwiseXor) expr, environment);
		case EXPR_invoke:
			return visitInvoke((Expr.Invoke) expr, environment);
		case EXPR_logicaland:
			return visitLogicalAnd((Expr.LogicalAnd) expr, environment);
		case EXPR_logicalor:
			return visitLogicalOr((Expr.LogicalOr) expr, environment);
		case EXPR_recordinitialiser:
			return visitRecordInitialiser((Expr.RecordInitialiser) expr, environment);
		case EXPR_tupleinitialiser:
			return visitTupleInitialiser((Expr.TupleInitialiser) expr, environment);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public E visitArrayAccess(Expr.ArrayAccess expr, Environment environment) {
		E source = visitExpression(expr.getFirstOperand(), environment);
		E index = visitExpression(expr.getSecondOperand(), environment);
		return constructArrayAccess(expr,source,index);
	}

	public E visitArrayLength(Expr.ArrayLength expr,  Environment environment) {
		E source = visitExpression(expr.getOperand(), environment);
		return constructArrayLength(expr,source);
	}

	public E visitArrayGenerator(Expr.ArrayGenerator expr,Environment environment) {
		E value = visitExpression(expr.getFirstOperand(), environment);
		E length = visitExpression(expr.getSecondOperand(), environment);
		return constructArrayGenerator(expr,value,length);
	}

	public E visitArrayInitialiser(Expr.ArrayInitialiser expr, Environment environment) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructArrayInitialiser(expr,operands);
	}

	public E visitArrayRange(Expr.ArrayRange expr, Environment environment) {
		throw new UnsupportedOperationException();
	}

	public E visitArrayUpdate(Expr.ArrayUpdate expr, Environment environment) {
		throw new UnsupportedOperationException();
	}

	public E visitBitwiseComplement(Expr.BitwiseComplement expr, Environment environment) {
		E operand = visitExpression(expr.getOperand(), environment);
		return constructBitwiseComplement(expr,operand);
	}

	public E visitBitwiseAnd(Expr.BitwiseAnd expr, Environment environment) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructBitwiseAnd(expr,operands);
	}

	public E visitBitwiseOr(Expr.BitwiseOr expr, Environment environment) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructBitwiseOr(expr,operands);
	}

	public E visitBitwiseXor(Expr.BitwiseXor expr, Environment environment) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructBitwiseXor(expr,operands);
	}

	public E visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructBitwiseShiftLeft(expr,lhs,rhs);
	}

	public E visitBitwiseShiftRight(Expr.BitwiseShiftRight expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructBitwiseShiftRight(expr,lhs,rhs);
	}

	public E visitCast(Expr.Cast expr, Environment environment) {
		E operand = visitExpression(expr.getOperand(), environment);
		return constructCast(expr,operand);
	}

	public E visitConstant(Expr.Constant expr, Environment environment) {
		return constructConstant(expr);
	}

	public E visitDereference(Expr.Dereference expr, Environment environment) {
		E operand = visitExpression(expr.getOperand(), environment);
		return constructDereference(expr,operand);
	}

	public E visitFieldDereference(Expr.FieldDereference expr, Environment environment) {
		E operand = visitExpression(expr.getOperand(), environment);
		return constructFieldDereference(expr,operand);
	}

	public E visitEqual(Expr.Equal expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructEqual(expr,lhs,rhs);
	}

	public E visitIntegerLessThan(Expr.IntegerLessThan expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerLessThan(expr,lhs,rhs);
	}

	public E visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerLessThanOrEqual(expr,lhs,rhs);
	}

	public E visitIntegerGreaterThan(Expr.IntegerGreaterThan expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerGreaterThan(expr,lhs,rhs);
	}

	public E visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerGreaterThanOrEqual(expr,lhs,rhs);
	}

	public E visitIntegerNegation(Expr.IntegerNegation expr, Environment environment) {
		E operand = visitExpression(expr.getOperand(), environment);
		return constructIntegerNegation(expr,operand);
	}

	public E visitIntegerAddition(Expr.IntegerAddition expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerAddition(expr,lhs,rhs);
	}

	public E visitIntegerSubtraction(Expr.IntegerSubtraction expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerSubtraction(expr,lhs,rhs);
	}

	public E visitIntegerMultiplication(Expr.IntegerMultiplication expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerMultiplication(expr,lhs,rhs);
	}

	public E visitIntegerDivision(Expr.IntegerDivision expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerDivision(expr,lhs,rhs);
	}

	public E visitIntegerRemainder(Expr.IntegerRemainder expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerRemainder(expr,lhs,rhs);
	}

	public E visitIs(Expr.Is expr, Environment environment) {
		E operand = visitExpression(expr.getOperand(), environment);
		return constructIs(expr,operand);
	}

	public E visitLogicalAnd(Expr.LogicalAnd expr, Environment environment) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructLogicalAnd(expr,operands);
	}

	public E visitLogicalImplication(Expr.LogicalImplication expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructLogicalImplication(expr,lhs,rhs);
	}

	public E visitLogicalIff(Expr.LogicalIff expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructLogicalIff(expr,lhs,rhs);
	}

	public E visitLogicalNot(Expr.LogicalNot expr, Environment environment) {
		E operand = visitExpression(expr.getOperand(), environment);
		return constructLogicalNot(expr,operand);
	}

	public E visitLogicalOr(Expr.LogicalOr expr, Environment environment) {
		List<E> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructLogicalOr(expr,operands);
	}

	public E visitExistentialQuantifier(Expr.ExistentialQuantifier expr, Environment environment) {
		Tuple<Decl.StaticVariable> parameters = expr.getParameters();
		ArrayList<Pair<E,E>> ranges = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.StaticVariable parameter = parameters.get(i);
			// NOTE: Currently ranges can only appear in quantifiers. Eventually, this will
			// be deprecated.
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			E start = visitExpression(range.getFirstOperand(), environment);
			E end = visitExpression(range.getSecondOperand(), environment);
			ranges.add(new Pair<>(start,end));
		}
		E body = visitExpression(expr.getOperand(), environment);
		return constructExistentialQuantifier(expr,ranges,body);
	}

	public E visitUniversalQuantifier(Expr.UniversalQuantifier expr, Environment environment) {
		Tuple<Decl.StaticVariable> parameters = expr.getParameters();
		ArrayList<Pair<E,E>> ranges = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.StaticVariable parameter = parameters.get(i);
			// NOTE: Currently ranges can only appear in quantifiers. Eventually, this will
			// be deprecated.
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			E start = visitExpression(range.getFirstOperand(), environment);
			E end = visitExpression(range.getSecondOperand(), environment);
			ranges.add(new Pair<>(start,end));
		}
		E body = visitExpression(expr.getOperand(), environment);
		return constructUniversalQuantifier(expr,ranges,body);
	}

	public E visitInvoke(Expr.Invoke expr, Environment environment) {
		List<E> operands = visitHeterogenousExpressions(expr.getOperands(), environment);
		return constructInvoke(expr, operands);
	}

	public E visitIndirectInvoke(Expr.IndirectInvoke expr, Environment environment) {
		E operand = visitExpression(expr.getSource(), environment);
		List<E> operands = visitHeterogenousExpressions(expr.getArguments(), environment);
		return constructIndirectInvoke(expr, operand, operands);
	}

	public E visitLambdaAccess(Expr.LambdaAccess expr, Environment environment) {
		return constructLambdaAccess(expr);
	}

	public E visitNew(Expr.New expr, Environment environment) {
		E operand = visitExpression(expr.getOperand(), environment);
		return constructNew(expr,operand);
	}

	public E visitNotEqual(Expr.NotEqual expr, Environment environment) {
		E lhs = visitExpression(expr.getFirstOperand(), environment);
		E rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructNotEqual(expr,lhs,rhs);
	}

	public E visitRecordAccess(Expr.RecordAccess expr, Environment environment) {
		E src = visitExpression(expr.getOperand(), environment);
		return constructRecordAccess(expr, src);
	}

	public E visitRecordInitialiser(Expr.RecordInitialiser expr, Environment environment) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		List<E> args = new ArrayList<>();
		for (int i = 0; i != fields.size(); ++i) {
			Expr operand = operands.get(i);
			args.add(visitExpression(operand, environment));
		}
		return constructRecordInitialiser(expr,args);
	}

	public E visitRecordUpdate(Expr.RecordUpdate expr, Environment environment) {
		E src = visitExpression(expr.getFirstOperand(), environment);
		E val = visitExpression(expr.getSecondOperand(), environment);
		// TODO: implement me!
		// return constructRecordUpdate(expr,src,val);
		throw new UnsupportedOperationException();
	}

	public E visitTupleInitialiser(Expr.TupleInitialiser expr, Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		List<E> args = new ArrayList<>();
		for (int i = 0; i != operands.size(); ++i) {
			Expr operand = operands.get(i);
			args.add(visitExpression(operand, environment));
		}
		return constructTupleInitialiser(expr,args);
	}

	public E visitStaticVariableAccess(Expr.StaticVariableAccess expr, Environment environment) {
		return constructStaticVariableAccess(expr);
	}

	public E visitVariableAccess(Expr.VariableAccess expr, Environment environment) {
		return constructVariableAccess(expr);
	}

	// ====================================================================================
	// Declaration Constructors
	// ====================================================================================

	public abstract D constructImport(Decl.Import d);

	public abstract D constructType(Decl.Type d, List<E> invariant);

	public abstract D constructStaticVariable(Decl.StaticVariable d, E initialiser);

	public abstract D constructProperty(Decl.Property decl, List<E> clauses);

	public abstract D constructFunction(Decl.Function d, List<E> precondition, List<E> postcondition, S body);

	public abstract D constructMethod(Decl.Method d, List<E> precondition, List<E> postcondition, S body);

	public abstract E constructLambda(Decl.Lambda decl, S body);

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

	private static final Type.Array TYPE_ARRAY_INT = new Type.Array(Type.Int);

	/**
	 * Provides a very simple environment for tracking the current declared lifetime
	 * relationships.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Environment {

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
	private static class FunctionOrMethodScope extends EnclosingScope {
		private final Decl.FunctionOrMethod declaration;

		public FunctionOrMethodScope(Decl.FunctionOrMethod declaration) {
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
