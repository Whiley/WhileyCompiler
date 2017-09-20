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
import static wyc.lang.WhileyFile.*;

/**
 * <p>
 * Responsible for determining when a value of a dynamically sized data type can
 * be "moved" or must be "copied". Moving is preferable preferable (when
 * permitted) because the original reference can be used without copying the
 * underlying data. The following provides a useful example:
 * </p>
 *
 * <pre>
 * function g(int[] xs, int i) -> (int[] ys):
 *     xs[j] = f(xs)
 *     return xs
 * </pre>
 *
 * <p>
 * Here, in the invocation <code>f(xs)</code> the array <code>xs</code> cannot
 * be moved since it is live afterwards. Instead, we must clone <code>xs</code>
 * at this point. However, the subsequent use of <code>xs</code> in the
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
 * Clearly, there is not need to clone <code>xs</code> when it is used in the
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
	public void visitAssign(Stmt.Assign stmt, Boolean consumed) {
		visitLVals(stmt.getLeftHandSide(), true);
		visitExpressions(stmt.getRightHandSide(), true);
	}

	@Override
	public void visitReturn(Stmt.Return stmt, Boolean consumed) {
		visitExpressions(stmt.getReturns(), true);
	}

	@Override
	public void visitVariable(Decl.Variable stmt, Boolean consumed) {
		if (stmt.hasInitialiser()) {
			visitExpression(stmt.getInitialiser(), true);
		}
	}


	@Override
	public void visitVariableAccess(Expr.VariableAccess expr, Boolean consumed) {
		if (!consumed) {
			// In this case, we have identified a variable access which is
			// not consumed and therefore can be implemented as a move.

			// FIXME: implement change to variable move
		}
	}

	@Override
	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, Boolean consumed) {
		// FIXME: unsure what to do here?
	}

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
	public void visitUniversalQuantifier(Expr.UniversalQuantifier expr, Boolean consumed) {
		visitExpression(expr.getOperand(), false);
	}

	@Override
	public void visitExistentialQuantifier(Expr.ExistentialQuantifier expr, Boolean consumed) {
		visitExpression(expr.getOperand(), false);
	}

	// FIXME: this needs considerable thought to make it work nicely.
}
