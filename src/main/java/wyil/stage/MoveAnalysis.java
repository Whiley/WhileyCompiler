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
package wyil.stage;

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.util.AbstractConsumer;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.Stmt;

import static wyc.lang.WhileyFile.*;

/**
 * <p>
 * Responsible for determining when a value of a dynamically sized data type can
 * be "moved" or must be "copied". Moving is preferable (when permitted) because
 * then the original reference can be used without copying the underlying data
 * (hence, is significantly more efficient). The following provides a useful
 * example:
 * </p>
 *
 * <pre>
 * function g(int[] xs, int i) -> (int[] ys):
 *     xs[j] = f(xs)
 *     return xs
 * </pre>
 *
 * <p>
 * In the invocation <code>f(xs)</code> the array <code>xs</code> cannot be
 * moved since it is live afterwards. Instead, we must clone <code>xs</code> at
 * this point. However, the subsequent use of <code>xs</code> in the
 * <code>return</code> statement does not require a clone and can be moved
 * (since <code>xs</code> is no longer live).
 * </p>
 *
 * <p>
 * The following illustrates another situation where temporary moves or
 * "borrows" are permitted:
 * </p>
 *
 * <pre>
 * function get(int[] xs, int i, int j) -> (int r):
 *     int sum = xs[i]
 *     sum = sum + xs[j]
 *     return sum
 * </pre>
 *
 * Clearly, there is no need to clone <code>xs</code> when it is used in the
 * initialiser for <code>sum</code>. This is because the use is temporary and
 * does not modify <code>xs</code>. We say that <code>xs</code> is not
 * <em>consumed</em>.
 *
 * @author David J. Pearce
 *
 */
public class MoveAnalysis extends AbstractConsumer<Boolean> implements Build.Stage<WhileyFile> {
	public MoveAnalysis(Build.Task builder) {

	}

	@Override
	public void apply(WhileyFile module) {
		visitWhileyFile(module,null);
	}

	// ===========================================================================
	// DECLARATIONS
	// ===========================================================================

	@Override
	public void visitType(Decl.Type t, Boolean consumed) {
		visitExpressions(t.getInvariant(), false);
	}

	@Override
	public void visitFunctionOrMethod(Decl.FunctionOrMethod fm, Boolean consumed) {
		visitExpressions(fm.getRequires(), false);
		visitExpressions(fm.getEnsures(), false);
		visitStatement((fm.getBody()),false);
	}

	@Override
	public void visitVariable(Decl.Variable stmt, Boolean consumed) {
		if (stmt.hasInitialiser()) {
			visitExpression(stmt.getInitialiser(), true);
		}
	}

	// ===========================================================================
	// STATEMENTS
	// ===========================================================================

	@Override
	public void visitAssert(Stmt.Assert stmt, Boolean consumed) {
		visitExpression(stmt.getCondition(), false);
	}

	@Override
	public void visitAssign(Stmt.Assign stmt, Boolean consumed) {
		visitLVals(stmt.getLeftHandSide(), false);
		visitExpressions(stmt.getRightHandSide(), true);
	}

	@Override
	public void visitAssume(Stmt.Assume stmt, Boolean consumed) {
		visitExpression(stmt.getCondition(), false);
	}

	@Override
	public void visitDebug(Stmt.Debug stmt, Boolean consumed) {
		visitExpression(stmt.getOperand(), false);
	}

	@Override
	public void visitDoWhile(Stmt.DoWhile stmt, Boolean consumed) {
		visitStatement(stmt.getBody(), null);
		visitExpression(stmt.getCondition(),false);
		visitExpressions(stmt.getInvariant(),false);
	}


	@Override
	public void visitIfElse(Stmt.IfElse stmt, Boolean consumed) {
		visitExpression(stmt.getCondition(),false);
		visitStatement(stmt.getTrueBranch(),null);
		if (stmt.hasFalseBranch()) {
			visitStatement(stmt.getFalseBranch(),null);
		}
	}
	@Override
	public void visitReturn(Stmt.Return stmt, Boolean consumed) {
		visitExpressions(stmt.getReturns(), true);
	}

	@Override
	public void visitSwitch(Stmt.Switch stmt, Boolean consumed) {
		visitExpression(stmt.getCondition(), false);
		Tuple<Stmt.Case> cases = stmt.getCases();
		for (int i = 0; i != cases.size(); ++i) {
			visitCase((Stmt.Case) cases.get(i), null);
		}
	}

	@Override
	public void visitCase(Stmt.Case stmt, Boolean consume) {
		visitExpressions(stmt.getConditions(), false);
		visitStatement(stmt.getBlock(),null);
	}

	@Override
	public void visitWhile(Stmt.While stmt, Boolean consumed) {
		visitExpression(stmt.getCondition(),false);
		visitExpressions(stmt.getInvariant(),false);
		visitStatement(stmt.getBody(),null);
	}

	// ===========================================================================
	// GENERAL EXPRESSIONS
	// ===========================================================================

	@Override public void visitInvoke(Expr.Invoke expr, Boolean consumed) {
		visitExpressions(expr.getOperands(), true);
	}

	@Override public void visitIndirectInvoke(Expr.IndirectInvoke expr, Boolean consumed) {
		visitExpression(expr.getSource(), false);
		visitExpressions(expr.getArguments(), true);
	}

	@Override public void visitIs(Expr.Is expr, Boolean consumed) {
		visitExpression(expr.getOperand(), false);
	}

	@Override
	public void visitVariableAccess(Expr.VariableAccess expr, Boolean consumed) {
		if (!consumed) {
			// In this case, we have identified a variable access which is
			// not consumed and therefore can be implemented as a move.
			expr.setMove();
		}
	}

	@Override
	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, Boolean consumed) {

	}

	@Override
	public void visitCast(Expr.Cast expr, Boolean consumed) {
		visitExpression(expr.getOperand(), false);
	}

	@Override
	public void visitConstant(Expr.Constant expr, Boolean consumed) {

	}

	@Override
	public void visitEqual(Expr.Equal expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitNotEqual(Expr.NotEqual expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(), false);
		visitExpression(expr.getSecondOperand(), false);
	}

	// ===========================================================================
	// ARRAY EXPRESSIONS
	// ===========================================================================

	@Override
	public void visitArrayAccess(Expr.ArrayAccess expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
		if(!consumed) {
			expr.setMove();
		}
	}

	@Override
	public void visitArrayLength(Expr.ArrayLength expr, Boolean consumed) {
		visitExpression(expr.getOperand(),false);
	}

	@Override
	public void visitArrayGenerator(Expr.ArrayGenerator expr, Boolean consumed) {
		// FIXME: am unsure about this one.
		visitExpression(expr.getFirstOperand(), false);
		visitExpression(expr.getSecondOperand(), false);
	}

	@Override
	public void visitArrayInitialiser(Expr.ArrayInitialiser expr, Boolean consumed) {
		visitExpressions(expr.getOperands(), true);
	}

	@Override
	public void visitArrayRange(Expr.ArrayRange expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(), false);
		visitExpression(expr.getSecondOperand(), false);
	}

	@Override
	public void visitArrayUpdate(Expr.ArrayUpdate expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(), false);
		visitExpression(expr.getSecondOperand(), false);
		visitExpression(expr.getThirdOperand(), true);
	}

	// ===========================================================================
	// BITWISE EXPRESSIONS
	// ===========================================================================


	@Override
	public void visitBitwiseComplement(Expr.BitwiseComplement expr, Boolean consumed) {
		visitExpression(expr.getOperand(),false);
	}

	@Override
	public void visitBitwiseAnd(Expr.BitwiseAnd expr, Boolean consumed) {
		visitExpressions(expr.getOperands(),false);
	}

	@Override
	public void visitBitwiseOr(Expr.BitwiseOr expr, Boolean consumed) {
		visitExpressions(expr.getOperands(),false);
	}

	@Override
	public void visitBitwiseXor(Expr.BitwiseXor expr, Boolean consumed) {
		visitExpressions(expr.getOperands(),false);
	}

	@Override
	public void visitBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitBitwiseShiftRight(Expr.BitwiseShiftRight expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	// ===========================================================================
	// INTEGER EXPRESSIONS
	// ===========================================================================

	@Override
	public void visitIntegerLessThan(Expr.IntegerLessThan expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitIntegerGreaterThan(Expr.IntegerGreaterThan expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitIntegerNegation(Expr.IntegerNegation expr, Boolean consumed) {
		visitExpression(expr.getOperand(),false);
	}

	@Override
	public void visitIntegerAddition(Expr.IntegerAddition expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitIntegerSubtraction(Expr.IntegerSubtraction expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitIntegerMultiplication(Expr.IntegerMultiplication expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitIntegerDivision(Expr.IntegerDivision expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitIntegerRemainder(Expr.IntegerRemainder expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	// ===========================================================================
	// LOGICAL EXPRESSIONS
	// ===========================================================================


	@Override
	public void visitLogicalAnd(Expr.LogicalAnd expr, Boolean consumed) {
		visitExpressions(expr.getOperands(), false);
	}

	@Override
	public void visitLogicalImplication(Expr.LogicalImplication expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitLogicalIff(Expr.LogicalIff expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(),false);
		visitExpression(expr.getSecondOperand(),false);
	}

	@Override
	public void visitLogicalNot(Expr.LogicalNot expr, Boolean consumed) {
		visitExpression(expr.getOperand(),false);
	}

	@Override
	public void visitLogicalOr(Expr.LogicalOr expr, Boolean consumed) {
		visitExpressions(expr.getOperands(),false);
	}

	@Override
	public void visitUniversalQuantifier(Expr.UniversalQuantifier expr, Boolean consumed) {
		visitVariables(expr.getParameters(),true);
		visitExpression(expr.getOperand(), false);
	}

	@Override
	public void visitExistentialQuantifier(Expr.ExistentialQuantifier expr, Boolean consumed) {
		visitVariables(expr.getParameters(),true);
		visitExpression(expr.getOperand(), false);
	}

	// ===========================================================================
	// RECORD EXPRESSIONS
	// ===========================================================================

	@Override
	public void visitRecordAccess(Expr.RecordAccess expr, Boolean consumed) {
		visitExpression(expr.getOperand(),false);
		if(!consumed) {
			expr.setMove();
		}
	}

	@Override
	public void visitRecordInitialiser(Expr.RecordInitialiser expr, Boolean consumed) {
		visitExpressions(expr.getOperands(),true);
	}

	@Override
	public void visitRecordUpdate(Expr.RecordUpdate expr, Boolean consumed) {
		visitExpression(expr.getFirstOperand(), false);
		visitExpression(expr.getSecondOperand(), true);
	}

	// ===========================================================================
	// REFERENCE EXPRESSIONS
	// ===========================================================================

	@Override
	public void visitDereference(Expr.Dereference expr, Boolean consumed) {
		visitExpression(expr.getOperand(),false);
	}


	@Override
	public void visitNew(Expr.New expr, Boolean consumed) {
		visitExpression(expr.getOperand(), true);
	}

	// ===========================================================================
	// TYPES
	// ===========================================================================

	@Override
	public void visitType(Type type, Boolean consumed) {
		// no need to visit types at all
		return;
	}
}
