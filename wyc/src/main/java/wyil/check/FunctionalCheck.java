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
package wyil.check;

import wycc.lang.SyntacticItem;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.util.AbstractConsumer;
import wyc.util.ErrorMessages;
import static wyil.lang.WyilFile.*;

import java.util.HashSet;

import jbfs.core.Build;

/**
 * <p>
 * Responsible for checking purity of statements and expressions in specific
 * context. The concept of a pure or referentially-transparent function is
 * relatively well understood. For example, Wikipedia lists two requirements:
 * </p>
 *
 * <ol>
 * <li><em>The function always evaluates the same result value given the same
 * argument value(s). The function result value cannot depend on any hidden
 * information or state that may change while program execution proceeds or
 * between different executions of the program, nor can it depend on any
 * external input from I/O devices (usually—see below).</em></li>
 * <li><em>Evaluation of the result does not cause any semantically observable side
 * effect or output, such as mutation of mutable objects or output to I/O
 * devices.</em></li>
 * </ol>
 *
 * <p>
 * The purpose of the functional check is to ensure that the rules regarding
 * pure functions and related concepts are properly adhered to. There are two
 * specific contexts of importance here:
 * </p>
 *
 * <ul>
 * <li><b>Function Context</b>. This corresponds to all statements and
 * expressions contained within a <code>function</code>. This is directly
 * comparable to the above statements regarding pure functions.</li>
 * <li><b>Functional Context</b>. This corresponds to an expression in a context
 * which should be functionally pure. This includes all <code>requires</code>,
 * <code>ensures</code>, <code>where</code> clause and the conditions for
 * <code>assert</code> or <code>assume</code> statements. In addition,
 * <i>constant expressions</i> are also considered functional contexts. There
 * are used, for example, for Constant expressions are used, for example, for
 * static variable initialisers.</li>
 * </ul>
 *
 * <p>
 * The rules enforced by this check are: <i>(1) No expression or statement in a
 * function context can dereference a variable, invoke a <code>method</code>,
 * allocate objects via <code>new</code> or access a static variable</i>; <i>(2)
 * No expression in a functional context can invoke a <code>method</code> or
 * allocate objects via <code>new</code></i>.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class FunctionalCheck extends AbstractConsumer<FunctionalCheck.Context> implements Compiler.Check {
	private boolean status = true;

	public FunctionalCheck(Build.Meter meter) {
		super(meter.fork(FunctionalCheck.class.getSimpleName()));
	}

	@Override
	public boolean check(WyilFile file) {
		visitModule(file, null);
		meter.done();
		return status;
	}

	public enum Context {
		PURE, FUNCTIONAL, IMPURE
	}

	@Override
	public void visitExternalUnit(Decl.Unit unit, Context data) {
		// NOTE: we override this to prevent unnecessarily traversing units
	}

	@Override
	public void visitType(Decl.Type decl, Context data) {
		visitVariable(decl.getVariableDeclaration(), data);
		visitExpressions(decl.getInvariant(), Context.FUNCTIONAL);
	}

	@Override
	public void visitProperty(Decl.Property decl, Context data) {
		visitExpressions(decl.getInvariant(), Context.FUNCTIONAL);
	}

	@Override
	public void visitFunction(Decl.Function decl, Context data) {
		visitVariables(decl.getParameters(), Context.PURE);
		visitVariables(decl.getReturns(), Context.PURE);
		visitExpressions(decl.getRequires(), Context.PURE);
		visitExpressions(decl.getEnsures(), Context.PURE);
		visitStatement(decl.getBody(), Context.PURE);
	}

	@Override
	public void visitMethod(Decl.Method decl, Context data) {
		// No need to visit variables here as no restrictions imposed.
		visitExpressions(decl.getRequires(), Context.FUNCTIONAL);
		visitExpressions(decl.getEnsures(), Context.FUNCTIONAL);
		visitStatement(decl.getBody(), Context.IMPURE);
	}

	@Override
	public void visitStaticVariable(Decl.StaticVariable decl, Context data) {
		// FIXME: should also prohibit invocation of pure functions in this context?
		visitExpression(decl.getInitialiser(), Context.PURE);
	}

	@Override
	public void visitAssert(Stmt.Assert stmt, Context context) {
		visitExpression(stmt.getCondition(), toFunctional(context));
	}

	@Override
	public void visitAssume(Stmt.Assume stmt, Context context) {
		visitExpression(stmt.getCondition(), toFunctional(context));
	}

	@Override
	public void visitDereference(Expr.Dereference expr, Context context) {
		if (context == Context.PURE) {
			syntaxError(expr, REFERENCE_ACCESS_NOT_PERMITTED);
		}
		super.visitDereference(expr, context);
	}

	@Override
	public void visitDoWhile(Stmt.DoWhile stmt, Context context) {
		visitStatement(stmt.getBody(), context);
		visitExpression(stmt.getCondition(), context);
		visitExpressions(stmt.getInvariant(), toFunctional(context));
	}

	@Override
	public void visitFor(Stmt.For stmt, Context context) {
		visitExpression(stmt.getVariable().getInitialiser(), context);
		visitStatement(stmt.getBody(), context);
		visitExpressions(stmt.getInvariant(), toFunctional(context));
	}

	@Override
	public void visitWhile(Stmt.While stmt, Context context) {
		visitStatement(stmt.getBody(), context);
		visitExpression(stmt.getCondition(), context);
		visitExpressions(stmt.getInvariant(), toFunctional(context));
	}

	@Override
	public void visitInvoke(Expr.Invoke expr, Context context) {
		// Check whether invoking an impure method in a pure context
		Decl.Link<Decl.Callable> name = expr.getLink();
		if (context != Context.IMPURE && name.isResolved() && name.getTarget() instanceof Decl.Method) {
			syntaxError(expr, METHODCALL_NOT_PERMITTED);
		}
		super.visitInvoke(expr, context);
	}

	@Override
	public void visitIndirectInvoke(Expr.IndirectInvoke expr, Context context) {
		// Check whether invoking an impure method in a pure context
		if (context != Context.IMPURE && isMethodType(expr.getSource().getType())) {
			syntaxError(expr, METHODCALL_NOT_PERMITTED);
		}
		super.visitIndirectInvoke(expr, context);
	}

	@Override
	public void visitExistentialQuantifier(Expr.ExistentialQuantifier expr, Context context) {
		for(Decl.StaticVariable v : expr.getParameters()) {
			visitExpression(v.getInitialiser(), context);
		}
		visitExpression(expr.getOperand(), context);
	}

	@Override
	public void visitUniversalQuantifier(Expr.UniversalQuantifier expr, Context context) {
		for(Decl.StaticVariable v : expr.getParameters()) {
			visitExpression(v.getInitialiser(), context);
		}
		visitExpression(expr.getOperand(), context);
	}

	@Override
	public void visitLambda(Decl.Lambda expr, Context context) {
		if(expr.getType() instanceof Type.Method) {
			super.visitLambda(expr, Context.IMPURE);
		} else {
			super.visitLambda(expr, Context.PURE);
		}
	}

	@Override
	public void visitNew(Expr.New expr, Context context) {
		if (context != Context.IMPURE) {
			syntaxError(expr, ALLOCATION_NOT_PERMITTED);
		}
		super.visitNew(expr, context);
	}

	@Override
	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, Context context) {
		// FIXME: we should prohibit static variable accesses in certain contexts.
		// However, at the moment, there is no way to indicate a final static variable
		// access. As such, prohibiting them would prevent the use of constants within
		// any functional context.
	}

	@Override
	public void visitType(Type type, Context context) {
		// NOTE: don't traverse types as this is unnecessary. Even in a pure context,
		// seemingly impure types (e.g. references and methods) can still be used
		// safely.
	}

	public boolean isMethodType(Type type) {
		if (type instanceof Type.Method) {
			return true;
		} else if (type instanceof Type.Nominal) {
			Type.Nominal n = (Type.Nominal) type;
			Decl.Link<Decl.Type> l = n.getLink();
			if(l.isResolved()) {
				return isMethodType(l.getTarget().getType());
			} else {
				// NOTE: this is handle error recovery for situations where name resolution
				// failed on the nominal type.
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Upgrade a given context to a functional or pure context. If the context
	 * already was PURE, then it remains as PURE (this is the highest level).
	 * Otherwise, it is FUNCTIONAL.
	 *
	 * @param context
	 * @return
	 */
	public Context toFunctional(Context context) {
		if (context == Context.PURE) {
			return context;
		} else {
			return Context.FUNCTIONAL;
		}
	}

	private void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context);
	}
}
