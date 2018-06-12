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
package wyll.interpreter;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.lang.SyntacticElement;
import wyfs.lang.Path;
import wyll.core.WyllFile;
import wyll.interpreter.ConcreteSemantics.LValue;
import wyll.interpreter.ConcreteSemantics.RValue;

import static wyll.core.WyllFile.*;

/**
 * <p>
 * A simple interpreter for WyLL bytecodes. The purpose of this interpreter is
 * to provide a reference implementation for the semantics of WyLL bytecodes.
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
	 * Provides mechanism for resolving names.
	 */
	private final WyllFile wyllFile;

	/**
	 * The debug stream provides an I/O stream through which debug bytecodes can
	 * write their messages.
	 */
	private final PrintStream debug;

	public Interpreter(Build.Project project, PrintStream debug, WyllFile wyllFile) {
		this.project = project;
		this.debug = debug;
		this.wyllFile = wyllFile;
	}

	private enum Status {
		RETURN,
		BREAK,
		CONTINUE,
		NEXT
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
	public RValue execute(NameID nid, Type.Method sig, CallStack frame, RValue... args) {
		// First, find the enclosing WyilFile
		try {
			// FIXME: NameID needs to be deprecated
			Identifier name = new Identifier(nid.name());
			// NOTE: need to read WyilFile here as, otherwose, it forces a
			// rereading of the Whiley source file and a loss of all generation
			// information.
			Path.Entry<WyllFile> entry = project.get(nid.module(), WyllFile.ContentType);
			if (entry == null) {
				throw new IllegalArgumentException("no WyIL file found: " + nid.module());
			}
			// Second, find the given function or method
			WyllFile wyllFile = entry.read();
			Decl.Method fmp = wyllFile.getDeclaration(name, sig, Decl.Method.class);
			if (fmp == null) {
				throw new IllegalArgumentException("no function or method found: " + nid + ", " + sig);
			} else if (sig.getParameters().size() != args.length) {
				throw new IllegalArgumentException("incorrect number of arguments: " + nid + ", " + sig);
			}
			// Fourth, construct the stack frame for execution
			frame = frame.enter(fmp);
			extractParameters(frame,args,fmp);
			// check function or method body exists
			if (fmp.getBody() == null) {
				// FIXME: Add support for native functions or methods. That is,
				// allow native functions to be implemented and called from the
				// interpreter.
				throw new IllegalArgumentException("no method body found: " + nid + ", " + sig);
			}
			// Execute the method or function body
			executeBlock(fmp.getBody(), frame, new FunctionOrMethodScope(fmp));
			// Return the return value!
			return frame.getLocal(name);
			//
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void extractParameters(CallStack frame, RValue[] args, Decl.Method decl) {
		Tuple<Decl.Variable> parameters = decl.getParameters();
		for(int i=0;i!=parameters.size();++i) {
			Decl.Variable parameter = parameters.get(i);
			frame.putLocal(parameter.getName(), args[i]);
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
	private Status executeBlock(Stmt.Block block, CallStack frame, EnclosingScope scope) {
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			Status r = executeStatement(stmt, frame, scope);
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
	private Status executeStatement(Stmt stmt, CallStack frame, EnclosingScope scope) {
		try {
			switch (stmt.getOpcode()) {
			case WyllFile.STMT_assert:
				return executeAssert((Stmt.Assert) stmt, frame, scope);
			case WyllFile.STMT_assign:
				return executeAssign((Stmt.Assign) stmt, frame, scope);
			case WyllFile.STMT_break:
				return executeBreak((Stmt.Break) stmt, frame, scope);
			case WyllFile.STMT_block:
				return executeBlock((Stmt.Block) stmt, frame, scope);
			case WyllFile.STMT_continue:
				return executeContinue((Stmt.Continue) stmt, frame, scope);
//			case WyllFile.STMT_debug:
//				return executeDebug((Stmt.Debug) stmt, frame, scope);
			case WyllFile.STMT_dowhile:
				return executeDoWhile((Stmt.DoWhile) stmt, frame, scope);
			case WyllFile.STMT_fail:
				return executeFail((Stmt.Fail) stmt, frame, scope);
			case WyllFile.STMT_foreach:
				return executeForEach((Stmt.ForEach) stmt, frame, scope);
			case WyllFile.STMT_if:
			case WyllFile.STMT_ifelse:
				return executeIf((Stmt.IfElse) stmt, frame, scope);
			case WyllFile.EXPR_indirectinvoke:
				executeIndirectInvoke((Expr.IndirectInvoke) stmt, frame);
				return Status.NEXT;
			case WyllFile.EXPR_invoke:
				executeInvoke((Expr.Invoke) stmt, frame);
				return Status.NEXT;
			case WyllFile.STMT_while:
				return executeWhile((Stmt.While) stmt, frame, scope);
			case WyllFile.STMT_return:
				return executeReturn((Stmt.Return) stmt, frame, scope);
//			case WyllFile.STMT_skip:
//				return executeSkip((Stmt.Skip) stmt, frame, scope);
			case WyllFile.STMT_switch:
				return executeSwitch((Stmt.Switch) stmt, frame, scope);
			case WyllFile.DECL_variableinitialiser:
			case WyllFile.DECL_variable:
				return executeVariableDeclaration((Decl.Variable) stmt, frame);
			}
		}
		catch (ResolutionError e) {
			error(e.getMessage(), stmt);
			return null;
		}
		deadCode(stmt);
		return null; // deadcode
	}

	private Status executeAssign(Stmt.Assign stmt, CallStack frame, EnclosingScope scope) {
		// FIXME: handle multi-assignments properly
		RValue rhs = executeExpression(ANY_T,stmt.getRightHandSide(), frame);
		LValue lhs = constructLVal(stmt.getLeftHandSide(), frame);
		lhs.write(frame, rhs);
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
	private Status executeAssert(Stmt.Assert stmt, CallStack frame, EnclosingScope scope) {
		//
		RValue.Bool b = executeExpression(BOOL_T, stmt.getCondition(), frame);
		if (b == RValue.False) {
			// FIXME: need to do more here
			throw new AssertionError();
		}
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
	private Status executeBreak(Stmt.Break stmt, CallStack frame, EnclosingScope scope) {
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
	private Status executeContinue(Stmt.Continue stmt, CallStack frame, EnclosingScope scope) {
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
//	private Status executeDebug(Stmt.Debug stmt, CallStack frame, EnclosingScope scope) {
//		//
//		// FIXME: need to do something with this
//		RValue.Array arr = executeExpression(ARRAY_T, stmt.getOperand(), frame);
//		for (RValue item : arr.getElements()) {
//			RValue.Int i = (RValue.Int) item;
//			char c = (char) i.intRValue();
//			debug.print(c);
//		}
//		//
//		return Status.NEXT;
//	}

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
	private Status executeDoWhile(Stmt.DoWhile stmt, CallStack frame, EnclosingScope scope) {
		Status r = Status.NEXT;
		while (r == Status.NEXT || r == Status.CONTINUE) {
			r = executeBlock(stmt.getBody(), frame, scope);
			if (r == Status.NEXT) {
				RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame);
				if (!operand.boolValue()) {
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
	private Status executeFail(Stmt.Fail stmt, CallStack frame, EnclosingScope scope) {
		throw new AssertionError("Runtime fault occurred");
	}

	private Status executeForEach(Stmt.ForEach stmt, CallStack frame, EnclosingScope scope) {
		Decl.Variable var = stmt.getVariable();
		RValue.Int ONE = ConcreteSemantics.Int(BigInteger.ONE);
		RValue.Int start = executeExpression(INT_T,stmt.getStart(),frame);
		RValue.Int end = executeExpression(INT_T,stmt.getEnd(),frame);
		while(!start.equals(end)) {
			frame.putLocal(var.getName(), start);
			Status r = executeBlock(stmt.getBody(),frame,scope);
			if(r != Status.NEXT && r != Status.CONTINUE) {
				return r;
			}
			start = start.add(ONE);
		}
		//
		return Status.NEXT;
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
	private Status executeIf(Stmt.IfElse stmt, CallStack frame, EnclosingScope scope) {
		Tuple<Pair<Expr, Stmt.Block>> branches = stmt.getBranches();
		for (int i = 0; i != branches.size(); ++i) {
			Pair<Expr, Stmt.Block> branch = branches.get(i);
			RValue.Bool operand = executeExpression(BOOL_T, branch.getFirst(), frame);
			if (operand.boolValue()) {
				return executeBlock(branch.getSecond(), frame, scope);
			}
		}
		if (stmt.hasDefaultBranch()) {
			return executeBlock(stmt.getDefaultBranch(), frame, scope);
		} else {
			return Status.NEXT;
		}
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
	private Status executeWhile(Stmt.While stmt, CallStack frame, EnclosingScope scope) {
		Status r;
		do {
			RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame);
			if (!operand.boolValue()) {
				return Status.NEXT;
			}
			// Keep executing the loop body until we exit it somehow.
			r = executeBlock(stmt.getBody(), frame, scope);
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
	private Status executeReturn(Stmt.Return stmt, CallStack frame, EnclosingScope scope) {
		// We know that a return statement can only appear in either a function
		// or method declaration. It cannot appear, for example, in a type
		// declaration. Therefore, the enclosing declaration is a function or
		// method.
		Decl.Method context = scope.getEnclosingScope(FunctionOrMethodScope.class).getContext();
		RValue value = executeExpression(ANY_T,stmt.getReturn(), frame);
		frame.putLocal(context.getName(), value);
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
//	private Status executeSkip(Stmt.Skip stmt, CallStack frame, EnclosingScope scope) {
//		// skip !
//		return Status.NEXT;
//	}

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
	private Status executeSwitch(Stmt.Switch stmt, CallStack frame, EnclosingScope scope) {
		Tuple<Stmt.Case> cases = stmt.getCases();
		//
		RValue.Int value = executeExpression(INT_T, stmt.getCondition(), frame);
		//
		for (int i = 0; i != cases.size(); ++i) {
			Stmt.Case c = cases.get(i);
			Stmt.Block body = c.getBlock();
			if (c.isDefault()) {
				return executeBlock(body, frame, scope);
			} else {
				for (Value.Int v : c.getConditions()) {
					RValue rv = ConcreteSemantics.Int(v.get());
					if (v.equals(rv)) {
						return executeBlock(body, frame, scope);
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
	private Status executeVariableDeclaration(Decl.Variable stmt, CallStack frame) {
		// We only need to do something if this has an initialiser
		if(stmt.hasInitialiser()) {
			RValue value = executeExpression(ANY_T, stmt.getInitialiser(), frame);
			frame.putLocal(stmt.getName(),value);
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
	public <T extends RValue> T executeExpression(Class<T> expected, Expr expr, CallStack frame) {
		try {
			RValue val;
			switch (expr.getOpcode()) {
			case WyllFile.EXPR_nullconstant:
				val = executeNullConst((Expr.NullConstant) expr, frame);
				break;
			case WyllFile.EXPR_boolconstant:
				val = executeBoolConst((Expr.BoolConstant) expr, frame);
				break;
			case WyllFile.EXPR_intconstant:
				val = executeIntConst((Expr.IntConstant) expr, frame);
				break;
			case WyllFile.EXPR_recordinitialiser:
				val = executeRecordInitialiser((Expr.RecordInitialiser) expr, frame);
				break;
			case WyllFile.EXPR_recordaccess:
				val = executeRecordAccess((Expr.RecordAccess) expr, frame);
				break;
			case WyllFile.EXPR_indirectinvoke:
				val = executeIndirectInvoke((Expr.IndirectInvoke) expr, frame);
				break;
			case WyllFile.EXPR_invoke:
				val = executeInvoke((Expr.Invoke) expr, frame);
				break;
			case WyllFile.EXPR_variableaccess:
				val = executeVariableAccess((Expr.VariableAccess) expr, frame);
				break;
			case WyllFile.EXPR_staticvariable:
				val = executeStaticVariableAccess((Expr.StaticVariableAccess) expr, frame);
				break;
			case WyllFile.EXPR_logicalnot:
				val = executeLogicalNot((Expr.LogicalNot) expr, frame);
				break;
			case WyllFile.EXPR_logicaland:
				val = executeLogicalAnd((Expr.LogicalAnd) expr, frame);
				break;
			case WyllFile.EXPR_logicalor:
				val = executeLogicalOr((Expr.LogicalOr) expr, frame);
				break;
			case WyllFile.EXPR_equal:
				val = executeEqual((Expr.Equal) expr, frame);
				break;
			case WyllFile.EXPR_notequal:
				val = executeNotEqual((Expr.NotEqual) expr, frame);
				break;
			case WyllFile.EXPR_integernegation:
				val = executeIntegerNegation((Expr.IntegerNegation) expr, frame);
				break;
			case WyllFile.EXPR_integeraddition:
				val = executeIntegerAddition((Expr.IntegerAddition) expr, frame);
				break;
			case WyllFile.EXPR_integersubtraction:
				val = executeIntegerSubtraction((Expr.IntegerSubtraction) expr, frame);
				break;
			case WyllFile.EXPR_integermultiplication:
				val = executeIntegerMultiplication((Expr.IntegerMultiplication) expr, frame);
				break;
			case WyllFile.EXPR_integerdivision:
				val = executeIntegerDivision((Expr.IntegerDivision) expr, frame);
				break;
			case WyllFile.EXPR_integerremainder:
				val = executeIntegerRemainder((Expr.IntegerRemainder) expr, frame);
				break;
			case WyllFile.EXPR_integerlessthan:
				val = executeIntegerLessThan((Expr.IntegerLessThan) expr, frame);
				break;
			case WyllFile.EXPR_integerlessequal:
				val = executeIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr, frame);
				break;
			case WyllFile.EXPR_integergreaterthan:
				val = executeIntegerGreaterThan((Expr.IntegerGreaterThan) expr, frame);
				break;
			case WyllFile.EXPR_integergreaterequal:
				val = executeIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr, frame);
				break;
			case WyllFile.EXPR_bitwisenot:
				val = executeBitwiseNot((Expr.BitwiseComplement) expr, frame);
				break;
			case WyllFile.EXPR_bitwiseor:
				val = executeBitwiseOr((Expr.BitwiseOr) expr, frame);
				break;
			case WyllFile.EXPR_bitwisexor:
				val = executeBitwiseXor((Expr.BitwiseXor) expr, frame);
				break;
			case WyllFile.EXPR_bitwiseand:
				val = executeBitwiseAnd((Expr.BitwiseAnd) expr, frame);
				break;
			case WyllFile.EXPR_bitwiseshl:
				val = executeBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr, frame);
				break;
			case WyllFile.EXPR_bitwiseshr:
				val = executeBitwiseShiftRight((Expr.BitwiseShiftRight) expr, frame);
				break;
			case WyllFile.EXPR_arrayaccess:
				val = executeArrayAccess((Expr.ArrayAccess) expr, frame);
				break;
			case WyllFile.EXPR_arraygenerator:
				val = executeArrayGenerator((Expr.ArrayGenerator) expr, frame);
				break;
			case WyllFile.EXPR_arraylength:
				val = executeArrayLength((Expr.ArrayLength) expr, frame);
				break;
			case WyllFile.EXPR_arrayinitialiser:
				val = executeArrayInitialiser((Expr.ArrayInitialiser) expr, frame);
				break;
			case WyllFile.EXPR_new:
				val = executeNew((Expr.New) expr, frame);
				break;
			case WyllFile.EXPR_dereference:
				val = executeDereference((Expr.Dereference) expr, frame);
				break;
			case WyllFile.EXPR_lambdaaccess:
				val = executeLambdaAccess((Expr.LambdaAccess) expr, frame);
				break;
			default:
				return deadCode(expr);
			}
			return checkType(val, expr, expected);
		} catch (ResolutionError e) {
			error(e.getMessage(), expr);
			return null;
		}
	}

	/**
	 * Execute a null constant expression at a given point in the method body
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private RValue executeNullConst(Expr.NullConstant expr, CallStack frame) {
		return RValue.Null;
	}
	/**
	 * Execute a boolean constant expression at a given point in the function or
	 * method body
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private RValue executeBoolConst(Expr.BoolConstant expr, CallStack frame) {
		return ConcreteSemantics.Bool(expr.getValue().get());
	}

	/**
	 * Execute an integer constant expression at a given point in the function or
	 * method body
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private RValue executeIntConst(Expr.IntConstant expr, CallStack frame) {
		return ConcreteSemantics.Int(expr.getValue().get());
	}

	private RValue executeRecordAccess(Expr.RecordAccess expr, CallStack frame) {
		RValue.Record rec = executeExpression(RECORD_T, expr.getOperand(), frame);
		return rec.read(expr.getField());
	}

	private RValue executeRecordInitialiser(Expr.RecordInitialiser expr, CallStack frame) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		RValue.Field[] values = new RValue.Field[operands.size()];
		for (int i = 0; i != operands.size(); ++i) {
			Identifier field = fields.get(i);
			Expr operand = operands.get(i);
			RValue value = executeExpression(ANY_T, operand, frame);
			values[i] = ConcreteSemantics.Field(field, value);
		}
		return ConcreteSemantics.Record(values);
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
	private RValue executeVariableAccess(Expr.VariableAccess expr, CallStack frame) {
		return frame.getLocal(expr.getName());
	}

	private RValue executeStaticVariableAccess(Expr.StaticVariableAccess expr, CallStack frame) throws ResolutionError {
		// FIXME: yes, this is a hack
		Decl.StaticVariable decl = wyllFile.getDeclaration(expr.getName().getLast(), expr.getType(),
				Decl.StaticVariable.class);
		NameID nid = decl.getQualifiedName().toNameID();
		return frame.getStatic(nid);
	}

	public RValue executeIntegerNegation(Expr.IntegerNegation expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getOperand(), frame);
		return lhs.negate();
	}

	public RValue executeIntegerAddition(Expr.IntegerAddition expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.add(rhs);
	}

	public RValue executeIntegerSubtraction(Expr.IntegerSubtraction expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.subtract(rhs);
	}

	public RValue executeIntegerMultiplication(Expr.IntegerMultiplication expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.multiply(rhs);
	}

	public RValue executeIntegerDivision(Expr.IntegerDivision expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.divide(rhs);
	}

	public RValue executeIntegerRemainder(Expr.IntegerRemainder expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.remainder(rhs);
	}

	public RValue executeEqual(Expr.Equal expr, CallStack frame) {
		RValue lhs = executeExpression(ANY_T, expr.getFirstOperand(), frame);
		RValue rhs = executeExpression(ANY_T, expr.getSecondOperand(), frame);
		return lhs.equal(rhs);
	}

	public RValue executeNotEqual(Expr.NotEqual expr, CallStack frame) {
		RValue lhs = executeExpression(ANY_T, expr.getFirstOperand(), frame);
		RValue rhs = executeExpression(ANY_T, expr.getSecondOperand(), frame);
		return lhs.notEqual(rhs);
	}

	public RValue executeIntegerLessThan(Expr.IntegerLessThan expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.lessThan(rhs);
	}

	public RValue executeIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.lessThanOrEqual(rhs);
	}

	public RValue executeIntegerGreaterThan(Expr.IntegerGreaterThan expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return rhs.lessThan(lhs);
	}

	public RValue executeIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return rhs.lessThanOrEqual(lhs);
	}

	public RValue executeLogicalNot(Expr.LogicalNot expr, CallStack frame) {
		RValue.Bool lhs = executeExpression(BOOL_T, expr.getOperand(), frame);
		return lhs.not();
	}

	public RValue executeLogicalAnd(Expr.LogicalAnd expr, CallStack frame) {
		// This is a short-circuiting operator. Therefore, we fail as soon as one
		// argument fails.
		Tuple<Expr> operands = expr.getOperands();
		for(int i=0;i!=operands.size();++i) {
			RValue.Bool b = executeExpression(BOOL_T, operands.get(i), frame);
			if(b == RValue.False) {
				return b;
			}
		}
		return RValue.True;
	}

	public RValue executeLogicalOr(Expr.LogicalOr expr, CallStack frame) {
		// This is a short-circuiting operator. Therefore, we succeed as soon as one
		// argument succeeds.
		Tuple<Expr> operands = expr.getOperands();
		for(int i=0;i!=operands.size();++i) {
			RValue.Bool b = executeExpression(BOOL_T, operands.get(i), frame);
			if(b == RValue.True) {
				return b;
			}
		}
		return RValue.False;
	}

	public RValue executeBitwiseNot(Expr.BitwiseComplement expr, CallStack frame) {
		RValue.Byte lhs = executeExpression(BYTE_T, expr.getOperand(), frame);
		return lhs.invert();
	}

	public RValue executeBitwiseAnd(Expr.BitwiseAnd expr, CallStack frame) {
		Tuple<Expr> operands = expr.getOperands();
		RValue.Byte val = executeExpression(BYTE_T, operands.get(0), frame);
		for (int i = 1; i != operands.size(); ++i) {
			val = val.and(executeExpression(BYTE_T, operands.get(i), frame));
		}
		return val;
	}

	public RValue executeBitwiseOr(Expr.BitwiseOr expr, CallStack frame) {
		Tuple<Expr> operands = expr.getOperands();
		RValue.Byte val = executeExpression(BYTE_T, operands.get(0), frame);
		for (int i = 1; i != operands.size(); ++i) {
			val = val.or(executeExpression(BYTE_T, operands.get(i), frame));
		}
		return val;
	}

	public RValue executeBitwiseXor(Expr.BitwiseXor expr, CallStack frame) {
		Tuple<Expr> operands = expr.getOperands();
		RValue.Byte val = executeExpression(BYTE_T, operands.get(0), frame);
		for (int i = 1; i != operands.size(); ++i) {
			val = val.xor(executeExpression(BYTE_T, operands.get(i), frame));
		}
		return val;
	}

	public RValue executeBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, CallStack frame) {
		RValue.Byte lhs = executeExpression(BYTE_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.shl(rhs);
	}
	public RValue executeBitwiseShiftRight(Expr.BitwiseShiftRight expr, CallStack frame) {
		RValue.Byte lhs = executeExpression(BYTE_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.shr(rhs);
	}

	public RValue executeArrayLength(Expr.ArrayLength expr, CallStack frame) {
		RValue.Array array = executeExpression(ARRAY_T, expr.getOperand(), frame);
		return array.length();
	}

	public RValue executeArrayAccess(Expr.ArrayAccess expr, CallStack frame) {
		RValue.Array array = executeExpression(ARRAY_T, expr.getFirstOperand(), frame);
		RValue.Int index = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return array.read(index);
	}

	public RValue executeArrayGenerator(Expr.ArrayGenerator expr, CallStack frame) {
		RValue element = executeExpression(ANY_T, expr.getFirstOperand(), frame);
		RValue.Int count = executeExpression(INT_T, expr.getSecondOperand(), frame);
		int n = count.intValue();
		if (n < 0) {
			throw new AssertionError("negative array length");
		}
		RValue[] values = new RValue[n];
		for (int i = 0; i != n; ++i) {
			values[i] = element;
		}
		return ConcreteSemantics.Array(values);
	}

	public RValue executeArrayInitialiser(Expr.ArrayInitialiser expr, CallStack frame) {
		Tuple<Expr> operands = expr.getOperands();
		RValue[] elements = new RValue[operands.size()];
		for (int i = 0; i != elements.length; ++i) {
			elements[i] = executeExpression(ANY_T, operands.get(i), frame);
		}
		return ConcreteSemantics.Array(elements);
	}

	public RValue executeNew(Expr.New expr, CallStack frame) {
		RValue initialiser = executeExpression(ANY_T, expr.getOperand(), frame);
		RValue.Cell cell = ConcreteSemantics.Cell(initialiser);
		return ConcreteSemantics.Reference(cell);
	}

	public RValue executeDereference(Expr.Dereference expr, CallStack frame) {
		RValue.Reference ref = executeExpression(REF_T, expr.getOperand(), frame);
		return ref.deref().read();
	}

	public RValue executeLambdaAccess(Expr.LambdaAccess expr, CallStack frame) throws ResolutionError {
		// Locate the function or method body in order to execute it
		// FIXME: This is horrendous. Should be able to use descriptor here!!
		Decl.Method decl = resolveExactly(expr.getName(), expr.getSignature(), Decl.Method.class);
		// Clone frame to ensure it executes in this exact environment.
		return ConcreteSemantics.Lambda(decl, frame.clone(), decl.getBody());
	}

//	private RValue executeLambdaDeclaration(Decl.Lambda decl, CallStack frame) {
//		// FIXME: this needs a clone of the frame? Otherwise, it's just
//		// executing in the later environment.
//		return ConcreteSemantics.Lambda(decl, frame.clone(), decl.getBody());
//	}

	// =============================================================
	// Multiple expressions
	// =============================================================

	/**
	 * Execute one or more expressions. This is slightly more complex than for
	 * the single expression case because of the potential to encounter
	 * "positional operands". That is, severals which arise from executing the
	 * same expression.
	 *
	 * @param expressions
	 * @param frame
	 * @return
	 */
	private RValue[] executeExpressions(Tuple<Expr> expressions, CallStack frame) {
		RValue[] results = new RValue[expressions.size()];
		for(int i=0;i!=expressions.size();++i) {
			results[i] = executeExpression(ANY_T,expressions.get(i),frame);
		}
		return results;
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
	private RValue executeIndirectInvoke(Expr.IndirectInvoke expr, CallStack frame) {
		RValue.Lambda src = executeExpression(LAMBDA_T, expr.getSource(),frame);
		RValue[] arguments = executeExpressions(expr.getArguments(), frame);
		// Here we have to use the enclosing frame when the lambda was created.
		// The reason for this is that the lambda may try to access enclosing
		// variables in the scope it was created.
		frame = src.getFrame();
		extractParameters(frame,arguments,src.getContext());
		// Execute the method or function body
		Stmt body = src.getBody();
		if(body instanceof Stmt.Block) {
			executeBlock((Stmt.Block) body, frame, new FunctionOrMethodScope(src.getContext()));
			// Extra the return values
			return frame.getLocal(src.getContext().getName());
		} else {
			return executeExpression(ANY_T,(Expr) body, frame);
		}
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
	 * @throws ResolutionError
	 */
	private RValue executeInvoke(Expr.Invoke expr, CallStack frame) throws ResolutionError {
		// Resolve function or method being invoked to a concrete declaration
		Decl.Method decl = resolveExactly(expr.getName(), expr.getSignature(), Decl.Method.class);
		// Evaluate argument expressions
		RValue[] arguments = executeExpressions(expr.getOperands(), frame);
		// Invoke the function or method in question
		return execute(decl.getQualifiedName().toNameID(), decl.getType(), frame, arguments);
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
	private LValue constructLVal(Expr expr, CallStack frame) {
		switch (expr.getOpcode()) {
		case EXPR_arrayaccess: {
			Expr.ArrayAccess e = (Expr.ArrayAccess) expr;
			LValue src = constructLVal(e.getFirstOperand(), frame);
			RValue.Int index = executeExpression(INT_T, e.getSecondOperand(), frame);
			return new LValue.Array(src, index);
		}
		case EXPR_dereference: {
			Expr.Dereference e = (Expr.Dereference) expr;
			LValue src = constructLVal(e.getOperand(), frame);
			return new LValue.Dereference(src);
		}
		case EXPR_recordaccess: {
			Expr.RecordAccess e = (Expr.RecordAccess) expr;
			LValue src = constructLVal(e.getOperand(), frame);
			return new LValue.Record(src, e.getField());
		}
		case EXPR_variableaccess: {
			Expr.VariableAccess e = (Expr.VariableAccess) expr;
			return new LValue.Variable(e.getName());
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
	public void checkInvariants(CallStack frame, Tuple<Expr> invariants) {
		for (int i = 0; i != invariants.size(); ++i) {
			RValue.Bool b = executeExpression(BOOL_T, invariants.get(i), frame);
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
	public void checkInvariants(CallStack frame, Expr... invariants) {
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
	private <T> T deadCode(SyntacticElement element) {
		// FIXME: do more here
		throw new RuntimeException("internal failure --- dead code reached");
	}

	private static final Class<RValue> ANY_T = RValue.class;
	private static final Class<RValue.Bool> BOOL_T = RValue.Bool.class;
	private static final Class<RValue.Byte> BYTE_T = RValue.Byte.class;
	private static final Class<RValue.Int> INT_T = RValue.Int.class;
	private static final Class<RValue.Reference> REF_T = RValue.Reference.class;
	private static final Class<RValue.Array> ARRAY_T = RValue.Array.class;
	private static final Class<RValue.Record> RECORD_T = RValue.Record.class;
	private static final Class<RValue.Lambda> LAMBDA_T = RValue.Lambda.class;

	public final class CallStack {
		private final Set<Path.ID> modules;
		private final Decl.Method context;
		private final Map<Identifier, RValue> locals;
		private final Map<NameID, RValue> globals;

		public CallStack() {
			this.locals = new HashMap<>();
			this.globals = new HashMap<>();
			this.modules = new HashSet<>();
			this.context = null;
		}

		private CallStack(CallStack parent, Decl.Method context) {
			this.context = context;
			this.locals = new HashMap<>();
			this.globals = parent.globals;
			this.modules = parent.modules;
		}

		public RValue getLocal(Identifier name) {
			return locals.get(name);
		}

		public void putLocal(Identifier name, RValue value) {
			locals.put(name, value);
		}

		public RValue getStatic(NameID name) {
			RValue v = globals.get(name);
			if(v == null) {
				load(name.module());
				v  = globals.get(name);
			}
			return v;
		}

		public void putStatic(NameID name, RValue value) {
			load(name.module());
			globals.put(name, value);
		}

		public CallStack enter(Decl.Method context) {
			load(context.getQualifiedName().toNameID().module());
			return new CallStack(this, context);
		}

		@Override
		public CallStack clone() {
			CallStack frame = new CallStack(this, this.context);
			frame.locals.putAll(locals);
			return frame;
		}

		/**
		 * Load a given module and make sure that all static variables are
		 * properly initialised.
		 *
		 * @param mid
		 * @param frame
		 */
		private void load(Path.ID mid) {
			if (!modules.contains(mid)) {
				// NOTE: must add module before attempting to load it.
				// Otherwise, static initialisers it contains will force itself
				// to be loaded.
				modules.add(mid);
				try {
					WyllFile module = project.get(mid, WyllFile.ContentType).read();
					for (WyllFile.Decl d : module.getDeclarations()) {
						if (d instanceof Decl.StaticVariable) {
							Decl.StaticVariable decl = (Decl.StaticVariable) d;
							RValue value = executeExpression(ANY_T, decl.getInitialiser(), this);
							globals.put(new NameID(mid, decl.getName().toString()), value);
						}
					}
					//
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * An enclosing scope captures the nested of declarations, blocks and other
	 * staments (e.g. loops). It is used to store information associated with
	 * these things such they can be accessed further down the chain. It can
	 * also be used to propagate information up the chain (for example, the
	 * environments arising from a break or continue statement).
	 *
	 * @author David J. Pearce
	 *
	 */
	private abstract static class EnclosingScope {
		private final EnclosingScope parent;

		public EnclosingScope(EnclosingScope parent) {
			this.parent = parent;
		}

		/**
		 * Get the innermost enclosing block of a given kind. For example, when
		 * processing a return statement we may wish to get the enclosing
		 * function or method declaration such that we can type check the return
		 * types.
		 *
		 * @param kind
		 */
		public <T extends EnclosingScope> T getEnclosingScope(Class<T> kind) {
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
		private final Decl.Method context;;

		public FunctionOrMethodScope(Decl.Method context) {
			super(null);
			this.context = context;
		}

		public Decl.Method getContext() {
			return context;
		}
	}

	public <T extends Decl.Method> T resolveExactly(Name name, Type.Method signature, Class<T> kind)
			throws ResolutionError {
		return wyllFile.getDeclaration(name.getLast(),signature,kind);
	}
}
