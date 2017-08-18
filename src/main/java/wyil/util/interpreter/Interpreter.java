// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.util.interpreter;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.lang.SyntacticElement;
import wybs.util.ResolveError;
import wyfs.lang.Path;
import wyil.lang.*;
import static wyil.lang.WyilFile.*;
import wyil.util.TypeSystem;

/**
 * <p>
 * A simple interpreter for WyIL bytecodes. The purpose of this interpreter is
 * to provide a reference implementation for the semantics of WyIL bytecodes.
 * </p>
 * <p>
 * The interpreter is not intended to be time or space efficient. It also assume
 * the underlying WyIL bytecodes are well formed and does not attempt to check
 * them. Thus, malformed bytecodes can reuslt in the interpreter executing in an
 * unpredictable fashion.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class Interpreter {
	/**
	 * The build project provides access to compiled WyIL files.
	 */
	private final Build.Project project;

	/**
	 * Provides mechanism for operating on types. For example, expanding them
	 * and performing subtype tests, etc.
	 */
	private final TypeSystem typeSystem;

	/**
	 * Implementations for the internal operators
	 */
	private final InternalFunction[] operators;

	/**
	 * The debug stream provides an I/O stream through which debug bytecodes can
	 * write their messages.
	 */
	private final PrintStream debug;

	public Interpreter(Build.Project project, PrintStream debug) {
		this.project = project;
		this.debug = debug;
		this.typeSystem = new TypeSystem(project);
		this.operators = StandardFunctions.standardFunctions;
	}

	private enum Status {
		RETURN,
		BREAK,
		CONTINUE,
		NEXT
	}

	public TypeSystem getTypeSystem() {
		return typeSystem;
	}

	/**
	 * Execute a function or method identified by a name and type signature with
	 * the given arguments, producing a return value or null (if none). If the
	 * function or method cannot be found, or the number of arguments is
	 * incorrect then an exception is thrown.
	 *
	 * @param nid
	 *            The fully qualified identifier of the function or method
	 * @param sig
	 *            The exact type signature identifying the method.
	 * @param args
	 *            The supplied arguments
	 * @return
	 */
	public RValue[] execute(NameID nid, Type.Callable sig, RValue... args) {
		// First, find the enclosing WyilFile
		try {
			// FIXME: NameID needs to be deprecated
			Identifier name = new Identifier(nid.name());
			Path.Entry<WyilFile> entry = project.get(nid.module(), WyilFile.ContentType);
			if (entry == null) {
				throw new IllegalArgumentException("no WyIL file found: " + nid.module());
			}
			// Second, find the given function or method
			WyilFile wyilFile = entry.read();
			Declaration.Callable fmp = wyilFile.getDeclaration(name, sig,
					Declaration.Callable.class);
			if (fmp == null) {
				throw new IllegalArgumentException("no function or method found: " + nid + ", " + sig);
			} else if (sig.getParameters().size() != args.length) {
				throw new IllegalArgumentException("incorrect number of arguments: " + nid + ", " + sig);
			}
			// Fourth, construct the stack frame for execution
			Map<Identifier, RValue> frame = new HashMap<>();
			extractParameters(frame,args,fmp);
			// Check the precondition
			if(fmp instanceof Declaration.FunctionOrMethod) {
				Declaration.FunctionOrMethod fm = (Declaration.FunctionOrMethod) fmp;
				checkInvariants(frame,fm.getRequires());
				// check function or method body exists
				if (fm.getBody() == null) {
					// FIXME: Add support for native functions or methods. That is,
					// allow native functions to be implemented and called from the
					// interpreter.
					throw new IllegalArgumentException("no function or method body found: " + nid + ", " + sig);
				}
				// Execute the method or function body
				executeBlock(fm.getBody(), frame);
				// Extra the return values
				RValue[] returns = packReturns(frame,fmp);
				// Check the postcondition holds
				checkInvariants(frame, fm.getEnsures());
				return returns;
			} else {
				// Properties always return true (provided their preconditions hold)
				return new RValue[]{RValue.True};
			}
			//
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void extractParameters(Map<Identifier, RValue> frame, RValue[] args, Declaration.Callable decl) {
		Tuple<Declaration.Variable> parameters = decl.getParameters();
		for(int i=0;i!=parameters.size();++i) {
			Declaration.Variable parameter = parameters.getOperand(i);
			frame.put(parameter.getName(), args[i]);
		}
	}

	/**
	 * Given an execution frame, extract the return values from a given function
	 * or method. The parameters of the function or method are located first in
	 * the frame, followed by the return values.
	 *
	 * @param frame
	 * @param type
	 * @return
	 */
	private RValue[] packReturns(Map<Identifier, RValue> frame, Declaration.Callable decl) {
		if (decl.getSignature() instanceof Type.Property) {
			return new RValue[] { RValue.True };
		} else {
			Tuple<Declaration.Variable> returns = decl.getReturns();
			RValue[] values = new RValue[returns.size()];
			for (int i = 0; i != values.length; ++i) {
				values[i] = frame.get(returns.getOperand(i).getName());
			}
			return values;
		}
	}

	/**
	 * Execute a given block of statements starting from the beginning. Control
	 * may terminate prematurely in a number of situations. For example, when a
	 * return or break statement is encountered.
	 *
	 * @param block
	 *            --- Statement block to execute
	 * @param frame
	 *            --- The current stack frame
	 *
	 * @return
	 */
	private Status executeBlock(Stmt.Block block, Map<Identifier, RValue> frame) {
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.getOperand(i);
			Status r = executeStatement(stmt, frame);
			// Now, see whether we are continuing or not
			if (r != Status.NEXT) {
				return r;
			}
		}
		return Status.NEXT;
	}

	/**
	 * Execute a statement at a given point in the function or method body
	 *
	 * @param stmt
	 *            --- The statement to be executed
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeStatement(Stmt stmt, Map<Identifier, RValue> frame) {
		switch (stmt.getOpcode()) {
		case WyilFile.STMT_assert:
			return executeAssert((Stmt.Assert) stmt, frame);
		case WyilFile.STMT_assume:
			return executeAssume((Stmt.Assume) stmt, frame);
		case WyilFile.STMT_assign:
			return executeAssign((Stmt.Assign) stmt, frame);
		case WyilFile.STMT_break:
			return executeBreak((Stmt.Break) stmt, frame);
		case WyilFile.STMT_continue:
			return executeContinue((Stmt.Continue) stmt, frame);
		case WyilFile.STMT_debug:
			return executeDebug((Stmt.Debug) stmt, frame);
		case WyilFile.STMT_dowhile:
			return executeDoWhile((Stmt.DoWhile) stmt, frame);
		case WyilFile.STMT_fail:
			return executeFail((Stmt.Fail) stmt, frame);
		case WyilFile.STMT_if:
		case WyilFile.STMT_ifelse:
			return executeIf((Stmt.IfElse) stmt, frame);
		case WyilFile.EXPR_indirectinvoke:
			executeIndirectInvoke((Expr.IndirectInvoke) stmt, frame);
			return Status.NEXT;
		case WyilFile.EXPR_invoke:
			executeInvoke((Expr.Invoke) stmt, frame);
			return Status.NEXT;
		case WyilFile.STMT_namedblock:
			return executeNamedBlock((Stmt.NamedBlock) stmt, frame);
		case WyilFile.STMT_while:
			return executeWhile((Stmt.While) stmt, frame);
		case WyilFile.STMT_return:
			return executeReturn((Stmt.Return) stmt, frame);
		case WyilFile.STMT_skip:
			return executeSkip((Stmt.Skip) stmt, frame);
		case WyilFile.STMT_switch:
			return executeSwitch((Stmt.Switch) stmt, frame);
		case WyilFile.DECL_variableinitialiser:
		case WyilFile.DECL_variable:
			return executeVariableDeclaration((Declaration.Variable) stmt, frame);
		}

		deadCode(stmt);
		return null; // deadcode
	}

	private Status executeAssign(Stmt.Assign stmt, Map<Identifier, RValue> frame) {
		// FIXME: handle multi-assignments properly
		Tuple<WyilFile.LVal> lhs = stmt.getLeftHandSide();
		RValue[] rhs = executeExpressions(stmt.getRightHandSide().getOperands(), frame);
		for (int i = 0; i != lhs.size(); ++i) {
			LValue lval = constructLVal(lhs.getOperand(i), frame);
			lval.write(frame, rhs[i]);
		}
		return Status.NEXT;
	}

	/**
	 * Execute an assert or assume statement. In both cases, if the condition
	 * evaluates to false an exception is thrown.
	 *
	 * @param stmt
	 *            --- Assert statement.
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeAssert(Stmt.Assert stmt, Map<Identifier, RValue> frame) {
		//
		checkInvariants(frame,stmt.getCondition());
		return Status.NEXT;
	}

	/**
	 * Execute an assert or assume statement. In both cases, if the condition
	 * evaluates to false an exception is thrown.
	 *
	 * @param stmt
	 *            --- Assert statement.
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeAssume(Stmt.Assume stmt, Map<Identifier, RValue> frame) {
		//
		checkInvariants(frame,stmt.getCondition());
		return Status.NEXT;
	}

	/**
	 * Execute a break statement. This transfers to control out of the nearest
	 * enclosing loop.
	 *
	 * @param stmt
	 *            --- Break statement.
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeBreak(Stmt.Break stmt, Map<Identifier, RValue> frame) {
		// TODO: the break bytecode supports a non-nearest exit and eventually
		// this should be supported.
		return Status.BREAK;
	}

	/**
	 * Execute a continue statement. This transfers to control back to the start
	 * the nearest enclosing loop.
	 *
	 * @param stmt
	 *            --- Break statement.
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeContinue(Stmt.Continue stmt, Map<Identifier, RValue> frame) {
		// TODO: the continue bytecode supports a non-nearest exit and eventually
		// this should be supported.
		return Status.CONTINUE;
	}

	/**
	 * Execute a Debug statement at a given point in the function or method
	 * body. This will write the provided string out to the debug stream.
	 *
	 * @param stmt
	 *            --- Debug statement to executed
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeDebug(Stmt.Debug stmt, Map<Identifier, RValue> frame) {
		//
		// FIXME: need to do something with this
//		Value.Array arr = executeExpression(ARRAY_T, stmt.getCondition(), frame);
//		for (Constant item : arr.values()) {
//			BigInteger b = ((Constant.Integer) item).value();
//			char c = (char) b.intValue();
//			debug.print(c);
//		}
		//
		return Status.NEXT;
	}

	/**
	 * Execute a DoWhile statement at a given point in the function or method
	 * body. This will loop over the body zero or more times.
	 *
	 * @param stmt
	 *            --- Loop statement to executed
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeDoWhile(Stmt.DoWhile stmt, Map<Identifier, RValue> frame) {
		Status r = Status.NEXT;
		while (r == Status.NEXT || r == Status.CONTINUE) {
			r = executeBlock(stmt.getBody(), frame);
			if (r == Status.NEXT) {
				RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame);
				if (operand == RValue.False) {
					return Status.NEXT;
				}
			}
		}

		// If we get here, then we have exited the loop body without falling
		// through to the next bytecode.
		if (r == Status.BREAK) {
			return Status.NEXT;
		} else {
			return r;
		}
	}

	/**
	 * Execute a fail statement at a given point in the function or method body.
	 * This will generate a runtime fault.
	 *
	 * @param stmt
	 *            --- The fail statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeFail(Stmt.Fail stmt, Map<Identifier, RValue> frame) {
		throw new AssertionError("Runtime fault occurred");
	}

	/**
	 * Execute an if statement at a given point in the function or method body.
	 * This will proceed done either the true or false branch.
	 *
	 * @param stmt
	 *            --- The if statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeIf(Stmt.IfElse stmt, Map<Identifier, RValue> frame) {
		RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame);
		if (operand == RValue.True) {
			// branch taken, so execute true branch
			return executeBlock(stmt.getTrueBranch(), frame);
		} else if (stmt.hasFalseBranch()) {
			// branch not taken, so execute false branch
			return executeBlock(stmt.getFalseBranch(), frame);
		} else {
			return Status.NEXT;
		}
	}

	/**
	 * Execute a named block which is simply a block of statements.
	 *
	 * @param stmt
	 *            --- Block statement to executed
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeNamedBlock(Stmt.NamedBlock stmt, Map<Identifier, RValue> frame) {
		return executeBlock(stmt.getBlock(),frame);
	}

	/**
	 * Execute a While statement at a given point in the function or method
	 * body. This will loop over the body zero or more times.
	 *
	 * @param stmt
	 *            --- Loop statement to executed
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeWhile(Stmt.While stmt, Map<Identifier, RValue> frame) {
		Status r;
		do {
			RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame);
			if (operand == RValue.False) {
				return Status.NEXT;
			}
			// Keep executing the loop body until we exit it somehow.
			r = executeBlock(stmt.getBody(), frame);
		} while (r == Status.NEXT || r == Status.CONTINUE);
		// If we get here, then we have exited the loop body without falling
		// through to the next bytecode.
		if (r == Status.BREAK) {
			return Status.NEXT;
		} else {
			return r;
		}
	}

	/**
	 * Execute a Return statement at a given point in the function or method
	 * body
	 *
	 * @param stmt
	 *            --- The return statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeReturn(Stmt.Return stmt, Map<Identifier, RValue> frame) {
		// We know that a return statement can only appear in either a function
		// or method declaration. It cannot appear, for example, in a type
		// declaration. Therefore, the enclosing declaration is a function or
		// method.
		Declaration.Callable fmp = null; // FIXME
		Tuple<Declaration.Variable> returns = fmp.getReturns();
		RValue[] values = executeExpressions(stmt.getOperands(), frame);
		for (int i = 0; i != returns.size(); ++i) {
			frame.put(returns.getOperand(i).getName(), values[i]);
		}
		return Status.RETURN;
	}

	/**
	 * Execute a skip statement at a given point in the function or method
	 * body
	 *
	 * @param stmt
	 *            --- The skip statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeSkip(Stmt.Skip stmt, Map<Identifier, RValue> frame) {
		// skip !
		return Status.NEXT;
	}

	/**
	 * Execute a Switch statement at a given point in the function or method
	 * body
	 *
	 * @param stmt
	 *            --- The swithc statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeSwitch(Stmt.Switch stmt, Map<Identifier, RValue> frame) {
		Tuple<Stmt.Case> cases = stmt.getCases();
		//
		Object value = executeExpression(ANY_T, stmt.getCondition(), frame);
		for (int i = 0; i != cases.size(); ++i) {
			Stmt.Case c = cases.getOperand(i);
			Stmt.Block body = c.getBlock();
			if (c.isDefault()) {
				return executeBlock(body, frame);
			} else {
				// FIXME: this is a temporary hack until a proper notion of
				// ConstantExpr is introduced.
				RValue[] values = executeExpressions(c.getConditions().getOperands(), frame);
				for (RValue v : values) {
					if (v.equals(value)) {
						return executeBlock(body, frame);
					}
				}
			}
		}
		return Status.NEXT;
	}

	/**
	 * Execute a variable declaration statement at a given point in the function or method
	 * body
	 *
	 * @param stmt
	 *            --- The statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeVariableDeclaration(Declaration.Variable stmt, Map<Identifier, RValue> frame) {
		// We only need to do something if this has an initialiser
		if(stmt.hasInitialiser()) {
			RValue value = executeExpression(ANY_T, stmt.getInitialiser(), frame);
			frame.put(stmt.getName(),value);
		}

		return Status.NEXT;
	}

	// =============================================================
	// Single expressions
	// =============================================================

	/**
	 * Execute a single expression which is expected to return a single result
	 * of an expected type. If a result of an incorrect type is returned, then
	 * an exception is raised.
	 *
	 * @param expected
	 *            The expected type of the result
	 * @param expr
	 *            The expression to be executed
	 * @param frame
	 *            The frame in which the expression is executing
	 * @return
	 */
	private <T extends RValue> T executeExpression(Class<T> expected, Expr expr, Map<Identifier, RValue> frame) {
		try {
			RValue val;
			switch (expr.getOpcode()) {
			case WyilFile.EXPR_const:
				val = executeConst((Expr.Constant) expr, frame);
				break;
			case WyilFile.EXPR_cast:
				val = executeConvert((Expr.Cast) expr, frame);
				break;
			case WyilFile.EXPR_recfield:
				val = executeFieldLoad((Expr.RecordAccess) expr, frame);
				break;
			case WyilFile.EXPR_indirectinvoke:
				val = executeIndirectInvoke((Expr.IndirectInvoke) expr, frame)[0];
				break;
			case WyilFile.EXPR_invoke:
				val = executeInvoke((Expr.Invoke) expr, frame)[0];
				break;
			case WyilFile.EXPR_lambdainit:
				val = executeLambda((Expr.LambdaInitialiser) expr, frame);
				break;
			case WyilFile.EXPR_exists:
			case WyilFile.EXPR_forall:
				val = executeQuantifier((Expr.Quantifier) expr, frame);
				break;
			case WyilFile.EXPR_var:
				val = executeVariableAccess((Expr.VariableAccess) expr, frame);
				break;
			default:
				val = executeOperator((Expr.Operator) expr, frame);
			}
			return checkType(val, expr, expected);
		} catch (ResolveError err) {
			error(err.getMessage(), expr);
			return null;
		}
	}


	/**
	 * Execute a Constant expression at a given point in the function or
	 * method body
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private RValue executeConst(Expr.Constant expr, Map<Identifier, RValue> frame) {
		Value v = expr.getValue();
		switch (v.getOpcode()) {
		case ITEM_null:
			return RValue.Null;
		case ITEM_bool: {
			Value.Bool b = (Value.Bool) v;
			if (b.get()) {
				return RValue.True;
			} else {
				return RValue.False;
			}
		}
		case ITEM_int: {
			Value.Int i = (Value.Int) v;
			return RValue.Int(i.get());
		}
		default:
			throw new RuntimeException("unknown value encountered (" + expr + ")");
		}
	}

	/**
	 * Execute a type conversion at a given point in the function or method body
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private RValue executeConvert(Expr.Cast expr, Map<Identifier, RValue> frame) {
		RValue operand = executeExpression(ANY_T, expr.getCastedExpr(), frame);
		return operand.convert(expr.getCastType());
	}

	/**
	 * Execute a binary operator at a given point in the function or method
	 * body. This will check operands match their expected types.
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 * @throws ResolveError
	 *             If a named type within this expression cannot be resolved
	 *             within the enclosing project.
	 */
	private RValue executeOperator(Expr.Operator expr, Map<Identifier, RValue> frame) throws ResolveError {
		switch (expr.getOpcode()) {
		case WyilFile.EXPR_and: {
			// This is a short-circuiting operator
			RValue.Bool lhs = executeExpression(BOOL_T, expr.getOperand(0), frame);
			if(lhs == RValue.False) {
				return lhs;
			} else {
				return executeExpression(BOOL_T, expr.getOperand(1), frame);
			}
		}
		case WyilFile.EXPR_or: {
			// This is a short-circuiting operator
			RValue.Bool lhs = executeExpression(BOOL_T, expr.getOperand(0), frame);
			if(lhs == RValue.True) {
				return lhs;
			} else {
				return executeExpression(BOOL_T, expr.getOperand(1), frame);
			}
		}
		default: {
			// This is the default case where can treat the operator as an
			// external function and just call it with the evaluated operands.
			Expr[] operands = expr.getOperands();
			RValue[] values = new RValue[operands.length];
			// Read all operands
			for (int i = 0; i != operands.length; ++i) {
				values[i] = executeExpression(ANY_T, operands[i], frame);
			}
			// Compute result
			return operators[expr.getOpcode()].apply(values, this, expr);
		}
		}
	}

	private RValue executeFieldLoad(Expr.RecordAccess expr, Map<Identifier, RValue> frame) {
		RValue.Record rec = executeExpression(RECORD_T, expr.getSource(), frame);
		return rec.read(expr.getField());
	}

	private RValue executeQuantifier(Expr.Quantifier expr, Map<Identifier, RValue> frame) {
		boolean r = executeQuantifier(0, expr, frame);
		boolean q = (expr instanceof Expr.UniversalQuantifier);
		return r == q ? RValue.True : RValue.False;
	}

	/**
	 * Execute one range of the quantifier, or the body if no ranges remain.
	 *
	 * @param index
	 * @param expr
	 * @param frame
	 * @param context
	 * @return
	 */
	private boolean executeQuantifier(int index, Expr.Quantifier expr, Map<Identifier, RValue> frame) {
		Tuple<Declaration.Variable> vars = expr.getParameters();
		if (index == vars.size()) {
			// This is the base case where we evaluate the condition itself.
			RValue.Bool r = executeExpression(BOOL_T, expr.getBody(), frame);
			boolean q = (expr instanceof Expr.UniversalQuantifier);
			// If this evaluates to true, then we will continue executing the
			// quantifier.
			return r.boolValue() == q;
		} else {
			Declaration.Variable var = vars.getOperand(index);
			RValue.Array range = executeExpression(ARRAY_T, var.getInitialiser(), frame);
			RValue[] elements = range.getElements();
			for (int i = 0; i != elements.length; ++i) {
				frame.put(var.getName(), elements[i]);
				boolean r = executeQuantifier(index + 1, expr, frame);
				if (!r) {
					// early termination
					return r;
				}
			}
			return true;
		}
	}

	private RValue executeLambda(Expr.LambdaInitialiser expr, Map<Identifier, RValue> frame) {
		// Clone the frame at this point, in order that changes seen after this
		// bytecode is executed are not propagated into the lambda itself.
//		frame = Arrays.copyOf(frame, frame.length);
//		return new ConstantLambda(expr, frame);
		throw new RuntimeException("Need to implement lambdas!!");
	}

	/**
	 * Execute a variable access expression at a given point in the function or
	 * method body. This simply loads the value of the given variable from the
	 * frame.
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private RValue executeVariableAccess(Expr.VariableAccess expr, Map<Identifier, RValue> frame) {
		Declaration.Variable decl = expr.getVariableDeclaration();
		return frame.get(decl.getName());
	}

	// =============================================================
	// Multiple expressions
	// =============================================================

	/**
	 * Execute one or more expressions. This is slightly more complex than for
	 * the single expression case because of the potential to encounter
	 * "positional operands". That is, severals which arise from executing the
	 * same expression.
	 *
	 * @param operands
	 * @param frame
	 * @return
	 */
	private RValue[] executeExpressions(Expr[] operands, Map<Identifier, RValue> frame) {
		RValue[][] results = new RValue[operands.length][];
		int count = 0;
		for(int i=0;i!=operands.length;++i) {
			results[i] = executeMultiReturnExpression(operands[i],frame);
			count += results[i].length;
		}
		RValue[] rs = new RValue[count];
		int j = 0;
		for(int i=0;i!=operands.length;++i) {
			Object[] r = results[i];
			System.arraycopy(r, 0, rs, j, r.length);
			j += r.length;
		}
		return rs;
	}

	/**
	 * Execute an expression which has the potential to return more than one
	 * result. Thus the return type must accommodate this by allowing zero or
	 * more returned values.
	 *
	 * @param expr
	 * @param frame
	 * @return
	 */
	private RValue[] executeMultiReturnExpression(Expr expr, Map<Identifier, RValue> frame) {
		switch (expr.getOpcode()) {
		case WyilFile.EXPR_indirectinvoke:
			return executeIndirectInvoke((Expr.IndirectInvoke) expr, frame);
		case WyilFile.EXPR_invoke:
			return executeInvoke((Expr.Invoke) expr, frame);
		case WyilFile.EXPR_const:
		case WyilFile.EXPR_cast:
		case WyilFile.EXPR_recfield:
		case WyilFile.EXPR_lambdainit:
		case WyilFile.EXPR_exists:
		case WyilFile.EXPR_forall:
		default:
			RValue val = executeExpression(ANY_T, expr, frame);
			return new RValue[] { val };
		}
	}

	/**
	 * Execute an IndirectInvoke bytecode instruction at a given point in the
	 * function or method body. This first checks the operand is a function
	 * reference, and then generates a recursive call to execute the given
	 * function. If the function does not exist, or is provided with the wrong
	 * number of arguments, then a runtime fault will occur.
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private RValue[] executeIndirectInvoke(Expr.IndirectInvoke expr, Map<Identifier, RValue> frame) {

		// FIXME: This is implementation is *ugly* --- can we do better than
		// this? One approach is to register an anonymous function so that we
		// can reuse executeAllWithin in both bases. This is hard to setup
		// though.
		Object operand = executeExpression(ANY_T, expr.getSource(),frame);
		// Check that we have a function reference
//		if(operand instanceof Constant.FunctionOrMethod) {
//			Constant.FunctionOrMethod cl = checkType(operand, src, Constant.FunctionOrMethod.class);
//			Constant[] arguments = executeExpressions(expr.getOperandGroup(ARGUMENTS),frame);
//			return execute(cl.name(),cl.type(),arguments);
//		} else {
//			ConstantLambda cl = checkType(operand, src, ConstantLambda.class);
//			// Yes we do; now construct the arguments. This requires merging the
//			// constant arguments provided in the lambda itself along with those
//			// operands provided for the "holes".
//			Constant[] lambdaFrame = Arrays.copyOf(cl.frame, cl.frame.length);
//			int[] parameters = cl.lambda.getBytecode().getOperandGroup(PARAMETERS);
//			Constant[] arguments = executeExpressions(expr.getOperandGroup(ARGUMENTS),frame);
//			for(int i=0;i!=parameters.length;++i) {
//				lambdaFrame[parameters[i]] = arguments[i];
//			}
//			// Make the actual call. This may return multiple values since it is
//			// a function/method invocation.
//			return executeMultiReturnExpression(cl.lambda.getOperand(BODY), lambdaFrame);
//		}
		throw new RuntimeException("Need support for lambdas");
	}

	/**
	 * Execute an Invoke bytecode instruction at a given point in the function
	 * or method body. This generates a recursive call to execute the given
	 * function. If the function does not exist, or is provided with the wrong
	 * number of arguments, then a runtime fault will occur.
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private RValue[] executeInvoke(Expr.Invoke expr, Map<Identifier, RValue> frame) {
		RValue[] arguments = executeExpressions(expr.getArguments().getOperands(), frame);
		return execute(expr.getName().toNameID(), expr.getSignatureType(), arguments);
	}

	// =============================================================
	// Constants
	// =============================================================

	/**
	 * This method constructs a "mutable" representation of the lval. This is a
	 * bit strange, but is necessary because values in the frame are currently
	 * immutable.
	 *
	 * @param operand
	 * @param frame
	 * @param context
	 * @return
	 */
	private LValue constructLVal(Expr expr, Map<Identifier, RValue> frame) {
		switch (expr.getOpcode()) {
		case EXPR_arridx: {
			Expr.ArrayAccess e = (Expr.ArrayAccess) expr;
			LValue src = constructLVal(e.getSource(), frame);
			RValue.Int index = executeExpression(INT_T, e.getSubscript(), frame);
			return new LValue.Array(src, index);
		}
		case EXPR_deref: {
			Expr.Dereference e = (Expr.Dereference) expr;
			LValue src = constructLVal(e.getOperand(), frame);
			return new LValue.Dereference(src);
		}
		case EXPR_recfield: {
			Expr.RecordAccess e = (Expr.RecordAccess) expr;
			LValue src = constructLVal(e.getSource(), frame);
			return new LValue.Record(src, e.getField());
		}
		case EXPR_var: {
			Expr.VariableAccess e = (Expr.VariableAccess) expr;
			Declaration.Variable decl = e.getVariableDeclaration();
			return new LValue.Variable(decl.getName());
		}
		}
		deadCode(expr);
		return null; // deadcode
	}

	/**
	 * Evaluate zero or more conditional expressions, and check whether any is
	 * false. If so, raise an exception indicating a runtime fault.
	 *
	 * @param frame
	 * @param context
	 * @param invariants
	 */
	public void checkInvariants(Map<Identifier, RValue> frame, Tuple<Expr> invariants) {
		for (int i = 0; i != invariants.size(); ++i) {
			RValue.Bool b = executeExpression(BOOL_T, invariants.getOperand(i), frame);
			if (b == RValue.False) {
				// FIXME: need to do more here
				throw new AssertionError();
			}
		}
	}

	/**
	 * Evaluate zero or more conditional expressions, and check whether any is
	 * false. If so, raise an exception indicating a runtime fault.
	 *
	 * @param frame
	 * @param context
	 * @param invariants
	 */
	public void checkInvariants(Map<Identifier, RValue> frame, Expr... invariants) {
		for (int i = 0; i != invariants.length; ++i) {
			RValue.Bool b = executeExpression(BOOL_T, invariants[i], frame);
			if (b == RValue.False) {
				// FIXME: need to do more here
				throw new AssertionError();
			}
		}
	}

	/**
	 * Check that a given operand value matches an expected type.
	 *
	 * @param operand
	 *            --- bytecode operand to be checked
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @param types
	 *            --- Types to be checked against
	 */
	@SafeVarargs
	public static <T extends RValue> T checkType(RValue operand, SyntacticElement context, Class<T>... types) {
		// Got through each type in turn checking for a match
		for (int i = 0; i != types.length; ++i) {
			if (types[i].isInstance(operand)) {
				// Matched!
				return (T) operand;
			}
		}
		// No match, therefore through an error
		if (operand == null) {
			error("null operand", context);
		} else {
			error("operand returned " + operand.getClass().getName() + ", expecting one of " + Arrays.toString(types),
					context);
		}
		return null;
	}

	/**
	 * This method is provided as a generic mechanism for reporting runtime
	 * errors within the interpreter.
	 *
	 * @param msg
	 *            --- Message to be printed when error arises.
	 * @param context
	 *            --- Context in which bytecodes are executed
	 */
	public static Object error(String msg, SyntacticElement context) {
		// FIXME: do more here
		throw new RuntimeException(msg);
	}

	/**
	 * This method is provided to properly handled positions which should be
	 * dead code.
	 *
	 * @param context
	 *            --- Context in which bytecodes are executed
	 */
	private Object deadCode(SyntacticElement element) {
		// FIXME: do more here
		throw new RuntimeException("internal failure --- dead code reached");
	}

	private static final Class<RValue> ANY_T = RValue.class;
	private static final Class<RValue.Bool> BOOL_T = RValue.Bool.class;
	private static final Class<RValue.Int> INT_T = RValue.Int.class;
	private static final Class<RValue.Array> ARRAY_T = RValue.Array.class;
	private static final Class<RValue.Record> RECORD_T = RValue.Record.class;

	/**
	 * An internal function is simply a named internal function. This reads a
	 * bunch of operands and returns a set of results.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static interface InternalFunction {
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) throws ResolveError;
	}
}
