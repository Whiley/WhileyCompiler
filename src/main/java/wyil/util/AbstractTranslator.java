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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyal.util.NameResolver.ResolutionError;
import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyfs.util.ArrayUtils;
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
public abstract class AbstractTranslator<S> {
	protected final Build.Meter meter;
	protected final Subtyping.Environment subtypeOperator;

	public AbstractTranslator(Build.Meter meter, Subtyping.Environment subtypeOperator) {
		this.meter = meter;
		this.subtypeOperator = subtypeOperator;
	}

	public S visitDeclaration(Decl decl) {
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

	public S visitUnit(Decl.Unit unit) {
		for (Decl decl : unit.getDeclarations()) {
			visitDeclaration(decl);
		}
		return null;
	}

	public S visitImport(Decl.Import decl) {
		return constructImport(decl);
	}

	public S visitLambda(Decl.Lambda decl, Environment environment) {
		//
		S body = visitExpression(decl.getBody(), environment);
		return constructLambda(decl,body);
	}

	public S visitStaticVariable(Decl.StaticVariable decl) {
		Environment environment = new Environment();
		S initialiser = visitExpression(decl.getInitialiser(), environment);
		return constructStaticVariable(decl,initialiser);
	}

	public S visitType(Decl.Type decl) {
		Environment environment = new Environment();
		List<S> invariant = visitHomogoneousExpressions(decl.getInvariant(), environment);
		return constructType(decl, invariant);
	}

	public S visitCallable(Decl.Callable decl) {
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

	public S visitFunctionOrMethod(Decl.FunctionOrMethod decl) {
		switch (decl.getOpcode()) {
		case DECL_function:
			return visitFunction((Decl.Function) decl);
		case DECL_method:
			return visitMethod((Decl.Method) decl);
		default:
			throw new IllegalArgumentException("unknown declaration encountered (" + decl.getClass().getName() + ")");
		}
	}

	public S visitProperty(Decl.Property decl) {
		Environment environment = new Environment();
		List<S> clauses = visitHomogoneousExpressions(decl.getInvariant(), environment);
		return constructProperty(decl,clauses);
	}

	public S visitFunction(Decl.Function decl) {
		Environment environment = new Environment();
		List<S> precondition = visitHomogoneousExpressions(decl.getRequires(), environment);
		List<S> postcondition = visitHomogoneousExpressions(decl.getEnsures(), environment);
		S body = visitBlock(decl.getBody(), environment, new FunctionOrMethodScope(decl));
		return constructFunction(decl,precondition,postcondition,body);
	}

	public S visitMethod(Decl.Method decl) {
		// Construct environment relation
		Environment environment = new Environment();
		//
		List<S> precondition = visitHomogoneousExpressions(decl.getRequires(), environment);
		List<S> postcondition = visitHomogoneousExpressions(decl.getEnsures(), environment);
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
		S operand = visitExpression(stmt.getCondition(), environment);
		return constructAssert(stmt,operand);
	}

	public S visitAssign(Stmt.Assign stmt, Environment environment, EnclosingScope scope) {
		Tuple<Type> targets = stmt.getLeftHandSide().map((LVal l) -> l.getType());
		List<S> lvals = visitLVals(stmt.getLeftHandSide(), environment, scope);
		List<S> rvals = visitExpressions(stmt.getRightHandSide(), environment);
		return constructAssign(stmt,lvals,rvals);
	}

	public List<S> visitLVals(Tuple<LVal> lvals, Environment environment, EnclosingScope scope) {
		ArrayList<S> ls = new ArrayList<>();
		for (int i = 0; i != lvals.size(); ++i) {
			LVal lval = lvals.get(i);
			ls.add(visitLVal(lval, environment));
		}
		return ls;
	}

	public S visitLVal(LVal lval, Environment environment) {
		switch (lval.getOpcode()) {
		case EXPR_arrayaccess:
		case EXPR_arrayborrow: {
			Expr.ArrayAccess e = (Expr.ArrayAccess) lval;
			S src = visitLVal((WyilFile.LVal) e.getFirstOperand(), environment);
			S index = visitExpression(e.getSecondOperand(), environment);
			return constructArrayAccessLVal(e,src,index);
		}
		case EXPR_dereference: {
			Expr.Dereference e = (Expr.Dereference) lval;
			S src = visitLVal((WyilFile.LVal) e.getOperand(), environment);
			return constructDereferenceLVal(e,src);
		}
		case EXPR_fielddereference: {
			Expr.FieldDereference e = (Expr.FieldDereference) lval;
			S src = visitLVal((WyilFile.LVal) e.getOperand(), environment);
			return constructFieldDereferenceLVal(e,src);
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess e = (Expr.RecordAccess) lval;
			S src = visitLVal((WyilFile.LVal) e.getOperand(), environment);
			return constructRecordAccessLVal(e,src);
		}
		case EXPR_tupleinitialiser: {
			Expr.TupleInitialiser e = (Expr.TupleInitialiser) lval;
			Tuple<Expr> operands = e.getOperands();
			List<S> srcs = new ArrayList<>();
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
		S operand = visitExpression(stmt.getCondition(), environment);
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
		S operand = visitExpression(stmt.getOperand(), environment);
		return constructDebug(stmt,operand);
	}

	public S visitDoWhile(Stmt.DoWhile stmt, Environment environment, EnclosingScope scope) {
		S body = visitStatement(stmt.getBody(), environment, scope);
		S condition = visitExpression(stmt.getCondition(), environment);
		List<S> invariant = visitHomogoneousExpressions(stmt.getInvariant(), environment);
		return constructDoWhile(stmt, body, condition, invariant);
	}

	public S visitFail(Stmt.Fail stmt, Environment environment, EnclosingScope scope) {
		return constructFail(stmt);
	}

	public S visitFor(Stmt.For stmt, Environment environment, EnclosingScope scope) {
		Expr.ArrayRange range = (Expr.ArrayRange) stmt.getVariable().getInitialiser();
		S start = visitExpression(range.getFirstOperand(),environment);
		S end = visitExpression(range.getSecondOperand(),environment);
		List<S> invariant = visitHomogoneousExpressions(stmt.getInvariant(), environment);
		S body = visitStatement(stmt.getBody(), environment, scope);
		return constructFor(stmt, new Pair<>(start, end), invariant, body);
	}

	public S visitIfElse(Stmt.IfElse stmt, Environment environment, EnclosingScope scope) {
		S condition = visitExpression(stmt.getCondition(), environment);
		S trueBranch = visitStatement(stmt.getTrueBranch(), environment, scope);
		S falseBranch = null;
		if (stmt.hasFalseBranch()) {
			falseBranch = visitStatement(stmt.getFalseBranch(), environment, scope);
		}
		return constructIfElse(stmt, condition, trueBranch, falseBranch);
	}

	public S visitInitialiser(Stmt.Initialiser stmt, Environment environment) {
		S initialiser = null;
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
			S returns = visitExpression(stmt.getReturn(), environment);
			return constructReturn(stmt, returns);
		} else {
			return constructReturn(stmt, null);
		}
	}

	public S visitSkip(Stmt.Skip stmt, Environment environment, EnclosingScope scope) {
		return constructSkip(stmt);
	}

	public S visitSwitch(Stmt.Switch stmt, Environment environment, EnclosingScope scope) {
		S condition = visitExpression(stmt.getCondition(), environment);
		Tuple<Stmt.Case> cases = stmt.getCases();
		ArrayList<Pair<List<S>,S>> cs = new ArrayList<>();
		for (int i = 0; i != cases.size(); ++i) {
			cs.add(visitCase(cases.get(i), environment, scope));
		}
		return constructSwitch(stmt,condition,cs);
	}

	public Pair<List<S>,S> visitCase(Stmt.Case stmt, Environment environment, EnclosingScope scope) {
		List<S> conditions = visitHomogoneousExpressions(stmt.getConditions(), environment);
		S body = visitStatement(stmt.getBlock(), environment, scope);
		return new Pair<>(conditions,body);
	}

	public S visitWhile(Stmt.While stmt, Environment environment, EnclosingScope scope) {
		S condition = visitExpression(stmt.getCondition(), environment);
		List<S> invariant = visitHomogoneousExpressions(stmt.getInvariant(), environment);
		S body = visitStatement(stmt.getBody(), environment, scope);
		return constructWhile(stmt,condition,invariant,body);
	}

	public List<S> visitExpressions(Tuple<Expr> exprs, Environment environment) {
		ArrayList<S> returns = new ArrayList<>();
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
	public List<S> visitHeterogenousExpressions(Tuple<Expr> exprs, Environment environment) {
		ArrayList<S> results = new ArrayList<>();
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
	public List<S> visitHomogoneousExpressions(Tuple<Expr> exprs, Environment environment) {
		ArrayList<S> results = new ArrayList<>();
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
	public S visitExpression(Expr expr, Environment environment) {
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

	public S visitUnaryOperator(Expr.UnaryOperator expr, Environment environment) {
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

	public S visitBinaryOperator(Expr.BinaryOperator expr, Environment environment) {
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

	public S visitTernaryOperator(Expr.TernaryOperator expr, Environment environment) {
		switch (expr.getOpcode()) {
		// Ternary Operators
		case EXPR_arrayupdate:
			return visitArrayUpdate((Expr.ArrayUpdate) expr, environment);
		default:
			throw new IllegalArgumentException("unknown expression encountered (" + expr.getClass().getName() + ")");
		}
	}

	public S visitNaryOperator(Expr.NaryOperator expr, Environment environment) {
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

	public S visitArrayAccess(Expr.ArrayAccess expr, Environment environment) {
		S source = visitExpression(expr.getFirstOperand(), environment);
		S index = visitExpression(expr.getSecondOperand(), environment);
		return constructArrayAccess(expr,source,index);
	}

	public S visitArrayLength(Expr.ArrayLength expr,  Environment environment) {
		S source = visitExpression(expr.getOperand(), environment);
		return constructArrayLength(expr,source);
	}

	public S visitArrayGenerator(Expr.ArrayGenerator expr,Environment environment) {
		S value = visitExpression(expr.getFirstOperand(), environment);
		S length = visitExpression(expr.getSecondOperand(), environment);
		return constructArrayGenerator(expr,value,length);
	}

	public S visitArrayInitialiser(Expr.ArrayInitialiser expr, Environment environment) {
		List<S> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructArrayInitialiser(expr,operands);
	}

	public S visitArrayRange(Expr.ArrayRange expr, Environment environment) {
		throw new UnsupportedOperationException();
	}

	public S visitArrayUpdate(Expr.ArrayUpdate expr, Environment environment) {
		throw new UnsupportedOperationException();
	}

	public S visitBitwiseComplement(Expr.BitwiseComplement expr, Environment environment) {
		S operand = visitExpression(expr.getOperand(), environment);
		return constructBitwiseComplement(expr,operand);
	}

	public S visitBitwiseAnd(Expr.BitwiseAnd expr, Environment environment) {
		List<S> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructBitwiseAnd(expr,operands);
	}

	public S visitBitwiseOr(Expr.BitwiseOr expr, Environment environment) {
		List<S> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructBitwiseOr(expr,operands);
	}

	public S visitBitwiseXor(Expr.BitwiseXor expr, Environment environment) {
		List<S> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructBitwiseXor(expr,operands);
	}

	public S visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructBitwiseShiftLeft(expr,lhs,rhs);
	}

	public S visitBitwiseShiftRight(Expr.BitwiseShiftRight expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructBitwiseShiftRight(expr,lhs,rhs);
	}

	public S visitCast(Expr.Cast expr, Environment environment) {
		S operand = visitExpression(expr.getOperand(), environment);
		return constructCast(expr,operand);
	}

	public S visitConstant(Expr.Constant expr, Environment environment) {
		return constructConstant(expr);
	}

	public S visitDereference(Expr.Dereference expr, Environment environment) {
		S operand = visitExpression(expr.getOperand(), environment);
		return constructDereference(expr,operand);
	}

	public S visitFieldDereference(Expr.FieldDereference expr, Environment environment) {
		S operand = visitExpression(expr.getOperand(), environment);
		return constructFieldDereference(expr,operand);
	}

	public S visitEqual(Expr.Equal expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructEqual(expr,lhs,rhs);
	}

	public S visitIntegerLessThan(Expr.IntegerLessThan expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerLessThan(expr,lhs,rhs);
	}

	public S visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerLessThanOrEqual(expr,lhs,rhs);
	}

	public S visitIntegerGreaterThan(Expr.IntegerGreaterThan expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerGreaterThan(expr,lhs,rhs);
	}

	public S visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerGreaterThanOrEqual(expr,lhs,rhs);
	}

	public S visitIntegerNegation(Expr.IntegerNegation expr, Environment environment) {
		S operand = visitExpression(expr.getOperand(), environment);
		return constructIntegerNegation(expr,operand);
	}

	public S visitIntegerAddition(Expr.IntegerAddition expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerAddition(expr,lhs,rhs);
	}

	public S visitIntegerSubtraction(Expr.IntegerSubtraction expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerSubtraction(expr,lhs,rhs);
	}

	public S visitIntegerMultiplication(Expr.IntegerMultiplication expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerMultiplication(expr,lhs,rhs);
	}

	public S visitIntegerDivision(Expr.IntegerDivision expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerDivision(expr,lhs,rhs);
	}

	public S visitIntegerRemainder(Expr.IntegerRemainder expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructIntegerRemainder(expr,lhs,rhs);
	}

	public S visitIs(Expr.Is expr, Environment environment) {
		S operand = visitExpression(expr.getOperand(), environment);
		return constructIs(expr,operand);
	}

	public S visitLogicalAnd(Expr.LogicalAnd expr, Environment environment) {
		List<S> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructLogicalAnd(expr,operands);
	}

	public S visitLogicalImplication(Expr.LogicalImplication expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructLogicalImplication(expr,lhs,rhs);
	}

	public S visitLogicalIff(Expr.LogicalIff expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructLogicalIff(expr,lhs,rhs);
	}

	public S visitLogicalNot(Expr.LogicalNot expr, Environment environment) {
		S operand = visitExpression(expr.getOperand(), environment);
		return constructLogicalNot(expr,operand);
	}

	public S visitLogicalOr(Expr.LogicalOr expr, Environment environment) {
		List<S> operands = visitHomogoneousExpressions(expr.getOperands(), environment);
		return constructLogicalOr(expr,operands);
	}

	public S visitExistentialQuantifier(Expr.ExistentialQuantifier expr, Environment environment) {
		Tuple<Decl.StaticVariable> parameters = expr.getParameters();
		ArrayList<Pair<S,S>> ranges = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.StaticVariable parameter = parameters.get(i);
			// NOTE: Currently ranges can only appear in quantifiers. Eventually, this will
			// be deprecated.
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			S start = visitExpression(range.getFirstOperand(), environment);
			S end = visitExpression(range.getSecondOperand(), environment);
			ranges.add(new Pair<>(start,end));
		}
		S body = visitExpression(expr.getOperand(), environment);
		return constructExistentialQuantifier(expr,ranges,body);
	}

	public S visitUniversalQuantifier(Expr.UniversalQuantifier expr, Environment environment) {
		Tuple<Decl.StaticVariable> parameters = expr.getParameters();
		ArrayList<Pair<S,S>> ranges = new ArrayList<>();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.StaticVariable parameter = parameters.get(i);
			// NOTE: Currently ranges can only appear in quantifiers. Eventually, this will
			// be deprecated.
			Expr.ArrayRange range = (Expr.ArrayRange) parameter.getInitialiser();
			S start = visitExpression(range.getFirstOperand(), environment);
			S end = visitExpression(range.getSecondOperand(), environment);
			ranges.add(new Pair<>(start,end));
		}
		S body = visitExpression(expr.getOperand(), environment);
		return constructUniversalQuantifier(expr,ranges,body);
	}

	public S visitInvoke(Expr.Invoke expr, Environment environment) {
		List<S> operands = visitHeterogenousExpressions(expr.getOperands(), environment);
		return constructInvoke(expr, operands);
	}

	public S visitIndirectInvoke(Expr.IndirectInvoke expr, Environment environment) {
		S operand = visitExpression(expr.getSource(), environment);
		List<S> operands = visitHeterogenousExpressions(expr.getArguments(), environment);
		return constructIndirectInvoke(expr, operand, operands);
	}

	public S visitLambdaAccess(Expr.LambdaAccess expr, Environment environment) {
		return constructLambdaAccess(expr);
	}

	public S visitNew(Expr.New expr, Environment environment) {
		S operand = visitExpression(expr.getOperand(), environment);
		return constructNew(expr,operand);
	}

	public S visitNotEqual(Expr.NotEqual expr, Environment environment) {
		S lhs = visitExpression(expr.getFirstOperand(), environment);
		S rhs = visitExpression(expr.getSecondOperand(), environment);
		return constructNotEqual(expr,lhs,rhs);
	}

	public S visitRecordAccess(Expr.RecordAccess expr, Environment environment) {
		S src = visitExpression(expr.getOperand(), environment);
		return constructRecordAccess(expr, src);
	}

	public S visitRecordInitialiser(Expr.RecordInitialiser expr, Environment environment) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		List<S> args = new ArrayList<>();
		for (int i = 0; i != fields.size(); ++i) {
			Expr operand = operands.get(i);
			args.add(visitExpression(operand, environment));
		}
		return constructRecordInitialiser(expr,args);
	}

	public S visitRecordUpdate(Expr.RecordUpdate expr, Environment environment) {
		S src = visitExpression(expr.getFirstOperand(), environment);
		S val = visitExpression(expr.getSecondOperand(), environment);
		// TODO: implement me!
		// return constructRecordUpdate(expr,src,val);
		throw new UnsupportedOperationException();
	}

	public S visitTupleInitialiser(Expr.TupleInitialiser expr, Environment environment) {
		Tuple<Expr> operands = expr.getOperands();
		List<S> args = new ArrayList<>();
		for (int i = 0; i != operands.size(); ++i) {
			Expr operand = operands.get(i);
			args.add(visitExpression(operand, environment));
		}
		return constructTupleInitialiser(expr,args);
	}

	public S visitStaticVariableAccess(Expr.StaticVariableAccess expr, Environment environment) {
		return constructStaticVariableAccess(expr);
	}

	public S visitVariableAccess(Expr.VariableAccess expr, Environment environment) {
		return constructVariableAccess(expr);
	}

	// ====================================================================================
	// Declaration Constructors
	// ====================================================================================

	public abstract S constructImport(Decl.Import d);

	public abstract S constructType(Decl.Type d, List<S> invariant);

	public abstract S constructStaticVariable(Decl.StaticVariable d, S initialiser);

	public abstract S constructProperty(Decl.Property decl, List<S> clauses);

	public abstract S constructFunction(Decl.Function d, List<S> precondition, List<S> postcondition, S body);

	public abstract S constructMethod(Decl.Method d, List<S> precondition, List<S> postcondition, S body);

	public abstract S constructLambda(Decl.Lambda decl, S body);

	// ====================================================================================
	// Statement Constructors
	// ====================================================================================

	public abstract S constructAssert(Stmt.Assert stmt, S condition);

	public abstract S constructAssign(Stmt.Assign stmt, List<S> lvals, List<S> rvals);

	public abstract S constructAssume(Stmt.Assume stmt, S condition);

	public abstract S constructBlock(Stmt.Block stmt, List<S> stmts);

	public abstract S constructBreak(Stmt.Break stmt);

	public abstract S constructContinue(Stmt.Continue stmt);

	public abstract S constructDebug(Stmt.Debug stmt, S operand);

	public abstract S constructDoWhile(Stmt.DoWhile stmt, S body, S condition, List<S> invariant);

	public abstract S constructFail(Stmt.Fail stmt);

	public abstract S constructFor(Stmt.For stmt, Pair<S,S> range, List<S> invariant, S body);

	public abstract S constructIfElse(Stmt.IfElse stmt, S condition, S trueBranch, S falseBranch);

	public abstract S constructInitialiser(Stmt.Initialiser stmt, S initialiser);

	public abstract S constructNamedBlock(Stmt.NamedBlock stmt, List<S> stmts);

	public abstract S constructReturn(Stmt.Return stmt, S ret);

	public abstract S constructSkip(Stmt.Skip stmt);

	public abstract S constructSwitch(Stmt.Switch stmt, S condition, List<Pair<List<S>,S>> cases);

	public abstract S constructWhile(Stmt.While stmt, S condition, List<S> invariant, S body);

	// ====================================================================================
	// LVal Constructors
	// ====================================================================================

	public abstract S constructArrayAccessLVal(Expr.ArrayAccess expr, S source, S index);

	public abstract S constructDereferenceLVal(Expr.Dereference expr, S operand);

	public abstract S constructFieldDereferenceLVal(Expr.FieldDereference expr, S operand);

	public abstract S constructRecordAccessLVal(Expr.RecordAccess expr, S source);

	public abstract S constructTupleInitialiserLVal(Expr.TupleInitialiser expr, List<S> source);

	public abstract S constructVariableAccessLVal(Expr.VariableAccess expr);

	// ====================================================================================
	// Expression Constructors
	// ====================================================================================

	public abstract S constructArrayAccess(Expr.ArrayAccess expr, S source, S index);

	public abstract S constructArrayLength(Expr.ArrayLength expr, S source);

	public abstract S constructArrayGenerator(Expr.ArrayGenerator expr, S value, S length);

	public abstract S constructArrayInitialiser(Expr.ArrayInitialiser expr, List<S> values);

	public abstract S constructBitwiseComplement(Expr.BitwiseComplement expr, S operand);

	public abstract S constructBitwiseAnd(Expr.BitwiseAnd expr, List<S> operands);

	public abstract S constructBitwiseOr(Expr.BitwiseOr expr, List<S> operands);

	public abstract S constructBitwiseXor(Expr.BitwiseXor expr, List<S> operands);

	public abstract S constructBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, S lhs, S rhs);

	public abstract S constructBitwiseShiftRight(Expr.BitwiseShiftRight expr, S lhs, S rhs);

	public abstract S constructCast(Expr.Cast expr, S operand);

	public abstract S constructConstant(Expr.Constant expr);

	public abstract S constructDereference(Expr.Dereference expr, S operand);

	public abstract S constructFieldDereference(Expr.FieldDereference expr, S operand);

	public abstract S constructEqual(Expr.Equal expr, S lhs, S rhs);

	public abstract S constructIntegerLessThan(Expr.IntegerLessThan expr, S lhs, S rhs);

	public abstract S constructIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, S lhs, S rhs);

	public abstract S constructIntegerGreaterThan(Expr.IntegerGreaterThan expr, S lhs, S rhs);

	public abstract S constructIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, S lhs, S rhs);

	public abstract S constructIntegerNegation(Expr.IntegerNegation expr, S operand);

	public abstract S constructIntegerAddition(Expr.IntegerAddition expr, S lhs, S rhs);

	public abstract S constructIntegerSubtraction(Expr.IntegerSubtraction expr, S lhs, S rhs);

	public abstract S constructIntegerMultiplication(Expr.IntegerMultiplication expr, S lhs, S rhs);

	public abstract S constructIntegerDivision(Expr.IntegerDivision expr, S lhs, S rhs);

	public abstract S constructIntegerRemainder(Expr.IntegerRemainder expr, S lhs, S rhs);

	public abstract S constructIs(Expr.Is expr, S operand);

	public abstract S constructLogicalAnd(Expr.LogicalAnd expr, List<S> operands);

	public abstract S constructLogicalImplication(Expr.LogicalImplication expr, S lhs, S rhs);

	public abstract S constructLogicalIff(Expr.LogicalIff expr, S lhs, S rhs);

	public abstract S constructLogicalNot(Expr.LogicalNot expr, S operand);

	public abstract S constructLogicalOr(Expr.LogicalOr expr, List<S> operands);

	public abstract S constructExistentialQuantifier(Expr.ExistentialQuantifier expr, List<Pair<S,S>> ranges, S body);

	public abstract S constructUniversalQuantifier(Expr.UniversalQuantifier expr, List<Pair<S,S>> ranges, S body);

	public abstract S constructInvoke(Expr.Invoke expr, List<S> arguments);

	public abstract S constructIndirectInvoke(Expr.IndirectInvoke expr, S source, List<S> arguments);

	public abstract S constructLambdaAccess(Expr.LambdaAccess expr);

	public abstract S constructNew(Expr.New expr, S operand);

	public abstract S constructNotEqual(Expr.NotEqual expr, S lhs, S rhs);

	public abstract S constructRecordAccess(Expr.RecordAccess expr, S source);

	public abstract S constructRecordInitialiser(Expr.RecordInitialiser expr, List<S> operands);

	public abstract S constructTupleInitialiser(Expr.TupleInitialiser expr, List<S> operands);

	public abstract S constructStaticVariableAccess(Expr.StaticVariableAccess expr);

	public abstract S constructVariableAccess(Expr.VariableAccess expr);

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
