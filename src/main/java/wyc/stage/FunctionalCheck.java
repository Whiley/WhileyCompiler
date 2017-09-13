// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.stage;

import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.task.CompileTask;
import wyc.type.TypeSystem;
import wyc.util.SingleParameterVisitor;

import static wyc.lang.WhileyFile.*;
import static wyc.util.ErrorMessages.*;

/**
 * <p>
 * Performs a number of simplistic visits that a module is syntactically
 * correct. This includes the following
 * </p>
 * <ul>
 * <li><b>Functions cannot have side-effects</b>. This includes sending messages
 * to actors, calling headless methods and spawning processes.</li>
 * <li><b>Functions/Methods cannot throw exceptions unless they are
 * declared</b>. Thus, if a method or function throws an exception an
 * appropriate throws clause is required.
 * <li><b>Every catch handler must catch something</b>. It is a syntax error if
 * a catch handler exists which can never catch anything (i.e. it is dead-code).
 * </li>
 * </ul>
 *
 * @author David J. Pearce
 *
 */
public class FunctionalCheck extends SingleParameterVisitor<FunctionalCheck.Context> {
	public TypeSystem types;

	public FunctionalCheck(CompileTask builder) {
		this.types = builder.getTypeSystem();
	}

	public void check(WhileyFile file) {
		visitWhileyFile(file, null);
	}

	public enum Context {
		PURE, IMPURE
	}


	@Override
	public void visitType(Decl.Type decl, Context data) {
		visitVariable(decl.getVariableDeclaration(), data);
		visitExpressions(decl.getInvariant(), Context.PURE);
	}

	@Override
	public void visitProperty(Decl.Property decl, Context data) {
		visitExpressions(decl.getInvariant(), Context.PURE);
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
		visitVariables(decl.getParameters(), Context.IMPURE);
		visitVariables(decl.getReturns(), Context.IMPURE);
		visitExpressions(decl.getRequires(), Context.PURE);
		visitExpressions(decl.getEnsures(), Context.PURE);
		visitStatement(decl.getBody(), Context.IMPURE);
	}

	@Override
	public void visitAssert(Stmt.Assert stmt, Context context) {
		visitExpression(stmt.getCondition(), Context.PURE);
	}

	@Override
	public void visitAssume(Stmt.Assume stmt, Context context) {
		visitExpression(stmt.getCondition(), Context.PURE);
	}

	@Override
	public void visitDereference(Expr.Dereference expr, Context context) {
		if (context != Context.IMPURE) {
			invalidObjectAllocation(expr, context);
		}
		super.visitDereference(expr, context);
	}

	@Override
	public void visitDoWhile(Stmt.DoWhile stmt, Context context) {
		visitStatement(stmt.getBody(), context);
		visitExpression(stmt.getCondition(), context);
		visitExpressions(stmt.getInvariant(), Context.PURE);
	}

	@Override
	public void visitWhile(Stmt.While stmt, Context context) {
		visitStatement(stmt.getBody(), context);
		visitExpression(stmt.getCondition(), context);
		visitExpressions(stmt.getInvariant(), Context.PURE);
	}

	@Override
	public void visitInvoke(Expr.Invoke expr, Context context) {
		// Check whether invoking an impure method in a pure context
		if (context != Context.IMPURE && expr.getSignature() instanceof Type.Method) {
			invalidMethodCall(expr, context);
		}
		super.visitInvoke(expr,context);
	}

	public void visitIndirectFunctionOrMethodCall(Expr.IndirectInvoke expr, Context context) {
		// Check whether invoking an impure method in a pure context
		if (context != Context.IMPURE && expr.getSource().getType() instanceof Type.Method) {
			invalidMethodCall(expr, context);
		}
		super.visitIndirectInvoke(expr,context);
	}

	@Override
	public void visitNew(Expr.New expression, Context context) {
		if(context != Context.IMPURE) {
			invalidObjectAllocation(expression,context);
		}
		super.visitNew(expression, context);
	}


	@Override
	public void visitType(Type type, Context context) {
		// NOTE: don't traverse types as this is unnecessary. Even in a pure context,
		// seemingly impure types (e.g. references and methods) can still be used
		// safely.
	}

	public void invalidObjectAllocation(SyntacticItem expression, Context context) {
		String msg = errorMessage(ALLOCATION_NOT_PERMITTED);
		WhileyFile file = ((WhileyFile) expression.getHeap());
		throw new SyntaxError(msg, file.getEntry(), expression);
	}

	public void invalidMethodCall(SyntacticItem expression, Context context) {
		String msg = errorMessage(METHODCALL_NOT_PERMITTED);
		WhileyFile file = ((WhileyFile) expression.getHeap());
		throw new SyntaxError(msg, file.getEntry(), expression);
	}

	public void invalidReferenceAccess(SyntacticItem expression, Context context) {
		String msg= errorMessage(REFERENCE_ACCESS_NOT_PERMITTED);
		WhileyFile file = ((WhileyFile) expression.getHeap());
		throw new SyntaxError(msg, file.getEntry(), expression);
	}
}
