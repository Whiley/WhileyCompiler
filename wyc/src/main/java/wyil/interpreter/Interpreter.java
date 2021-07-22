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
package wyil.interpreter;

import static wycc.util.AbstractCompilationUnit.ITEM_bool;
import static wycc.util.AbstractCompilationUnit.ITEM_byte;
import static wycc.util.AbstractCompilationUnit.ITEM_int;
import static wycc.util.AbstractCompilationUnit.ITEM_null;
import static wycc.util.AbstractCompilationUnit.ITEM_utf8;
import static wyil.lang.WyilFile.*;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import wycc.lang.*;
import wycc.util.AbstractCompilationUnit.Identifier;
import wycc.util.AbstractCompilationUnit.Tuple;
import wycc.util.AbstractCompilationUnit.Value;
import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyil.interpreter.ConcreteSemantics.RValue;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.LVal;
import wyil.lang.WyilFile.QualifiedName;
import wyil.lang.WyilFile.Attr.StackFrame;
import wyil.lang.WyilFile.Stmt;
import wyil.lang.WyilFile.Type;

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
	 * Determines the underlying semantics used for this interpreter.
	 */
	private final ConcreteSemantics semantics;

	/**
	 * The debug stream provides an I/O stream through which debug bytecodes can
	 * write their messages.
	 */
	private final PrintStream debug;

	public Interpreter(PrintStream debug, WyilFile... modules) {
		this.debug = debug;
		this.semantics = new ConcreteSemantics();
	}

	private enum Status {
		RETURN, BREAK, CONTINUE, NEXT
	}

	/**
	 * Execute a function or method identified by a name and type signature with the
	 * given arguments, producing a return value or null (if none). If the function
	 * or method cannot be found, or the number of arguments is incorrect then an
	 * exception is thrown.
	 *
	 * @param nid       The fully qualified identifier of the function or method
	 * @param signature The exact type signature identifying the method.
	 * @param args      The supplied arguments
	 * @return
	 */
	public RValue execute(QualifiedName name, Type.Callable signature, CallStack frame, RValue... args) {
		return execute(name, signature, frame, args, null);
	}

	/**
	 * Execute a function or method identified by a name and type signature with the
	 * given arguments, producing a return value or null (if none). If the function
	 * or method cannot be found, or the number of arguments is incorrect then an
	 * exception is thrown.
	 *
	 * @param nid       The fully qualified identifier of the function or method
	 * @param signature The exact type signature identifying the method.
	 * @param args      The supplied arguments
	 * @param context   Identifies the position where this was invoked from to use
	 *                  when reporting precondition violations.
	 * @return
	 */
	public RValue execute(QualifiedName name, Type.Callable signature, CallStack frame, RValue[] args,
			SyntacticItem context) {
		Decl.Callable lambda = frame.getCallable(name, signature);
		if (lambda == null) {
			throw new IllegalArgumentException("no function or method found: " + name + ", " + signature);
		} else if (lambda.getParameters().size() != args.length) {
			throw new IllegalArgumentException(
					"incorrect number of arguments: " + lambda.getName() + ", " + lambda.getType());
		}
		// Enter a new frame for executing this callable item
		frame = frame.enter(lambda);
		// Execute the lambda we've extracted
		return execute(lambda, frame, args, context);
	}

	/**
	 * Execute a given callable entity, which could be a function, method, property
	 * or lambda.
	 *
	 * @param lambda
	 * @param frame
	 * @param args    The supplied arguments
	 * @param context Identifies the position where this was invoked from to use
	 *                when reporting precondition violations.
	 * @return
	 */
	public RValue execute(Decl.Callable lambda, CallStack frame, RValue[] args, SyntacticItem context) {
		// Fourth, construct the stack frame for execution
		extractParameters(frame, args, lambda);
		// Check the precondition
		if (lambda instanceof Decl.FunctionOrMethod) {
			Decl.FunctionOrMethod fm = (Decl.FunctionOrMethod) lambda;
			// Check preconditions hold
			checkPrecondition(WyilFile.RUNTIME_PRECONDITION_FAILURE, frame, fm.getRequires(), context);
			// check function or method body exists
			if (fm.getBody().size() == 0) {
				// FIXME: Add support for native functions or methods. That is,
				// allow native functions to be implemented and called from the
				// interpreter.
				throw new IllegalArgumentException(
						"no function or method body found: " + lambda.getQualifiedName() + " : " + lambda.getType());
			}
			// Execute the method or function body
			executeBlock(fm.getBody(), frame, new FunctionOrMethodScope(fm, args));
			// Extra the return values
			return packReturns(frame, lambda);
		} else if (lambda instanceof Decl.Lambda) {
			Decl.Lambda l = (Decl.Lambda) lambda;
			return executeExpression(ANY_T, l.getBody(), frame);
		} else {
			Decl.Property p = (Decl.Property) lambda;
			Tuple<Expr> invariant = p.getInvariant();
			// Evaluate clauses of property, and terminate early as soon as one doesn't
			// hold.
			for(int i=0;i!=invariant.size();++i) {
				RValue.Bool retval = executeExpression(BOOL_T, invariant.get(i), frame);
				if(!retval.boolValue()) {
					// Short circuit
					return retval;
				}
			}
			//
			return RValue.True;
		}
	}

	private void extractParameters(CallStack frame, RValue[] args, Decl.Callable decl) {
		Tuple<Decl.Variable> parameters = decl.getParameters();
		for (int i = 0; i != parameters.size(); ++i) {
			Decl.Variable parameter = parameters.get(i);
			frame.putLocal(parameter.getName(), args[i]);
		}
	}

	/**
	 * Given an execution frame, extract the return values from a given function or
	 * method. The parameters of the function or method are located first in the
	 * frame, followed by the return values.
	 *
	 * @param frame
	 * @param type
	 * @return
	 */
	private RValue packReturns(CallStack frame, Decl.Callable decl) {
		if (decl instanceof Decl.Property) {
			return RValue.True;
		} else {
			Tuple<Decl.Variable> returns = decl.getReturns();
			if(returns.size() == 1) {
				Decl.Variable v = returns.get(0);
				return frame.getLocal(v.getName());
			} else {
				RValue[] items = new RValue[returns.size()];
				for(int i=0;i!=items.length;++i) {
					Decl.Variable v = returns.get(i);
					items[i] = frame.getLocal(v.getName());
				}
				return RValue.Tuple(items);
			}
		}
	}

	/**
	 * Execute a given block of statements starting from the beginning. Control may
	 * terminate prematurely in a number of situations. For example, when a return
	 * or break statement is encountered.
	 *
	 * @param block --- Statement block to execute
	 * @param frame --- The current stack frame
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
	 * @param stmt  --- The statement to be executed
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeStatement(Stmt stmt, CallStack frame, EnclosingScope scope) {
		checkForTimeout(frame);
		//
		switch (stmt.getOpcode()) {
		case STMT_assert:
			return executeAssert((Stmt.Assert) stmt, frame, scope);
		case STMT_assume:
			return executeAssume((Stmt.Assume) stmt, frame, scope);
		case STMT_assign:
			return executeAssign((Stmt.Assign) stmt, frame, scope);
		case STMT_break:
			return executeBreak((Stmt.Break) stmt, frame, scope);
		case STMT_continue:
			return executeContinue((Stmt.Continue) stmt, frame, scope);
		case STMT_debug:
			return executeDebug((Stmt.Debug) stmt, frame, scope);
		case STMT_dowhile:
			return executeDoWhile((Stmt.DoWhile) stmt, frame, scope);
		case STMT_fail:
			return executeFail((Stmt.Fail) stmt, frame, scope);
		case STMT_for:
			return executeFor((Stmt.For) stmt, frame, scope);
		case STMT_if:
		case STMT_ifelse:
			return executeIf((Stmt.IfElse) stmt, frame, scope);
		case STMT_initialiser:
		case STMT_initialiservoid:
			return executeInitialiser((Stmt.Initialiser) stmt, frame, scope);
		case EXPR_indirectinvoke:
			executeIndirectInvoke((Expr.IndirectInvoke) stmt, frame);
			return Status.NEXT;
		case EXPR_invoke:
			executeInvoke((Expr.Invoke) stmt, frame);
			return Status.NEXT;
		case STMT_namedblock:
			return executeNamedBlock((Stmt.NamedBlock) stmt, frame, scope);
		case STMT_while:
			return executeWhile((Stmt.While) stmt, frame, scope);
		case STMT_return:
		case STMT_returnvoid:
			return executeReturn((Stmt.Return) stmt, frame, scope);
		case STMT_skip:
			return executeSkip((Stmt.Skip) stmt, frame, scope);
		case STMT_switch:
			return executeSwitch((Stmt.Switch) stmt, frame, scope);
		case DECL_variable:
			return executeVariableDeclaration((Decl.Variable) stmt, frame);
		}
		deadCode(stmt);
		return null; // deadcode
	}

	private Status executeAssign(Stmt.Assign stmt, CallStack frame, EnclosingScope scope) {
		Tuple<WyilFile.LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		RValue[] rvals = executeExpressions(stmt.getRightHandSide(), frame);
		//
		for (int i = 0; i != rhs.size(); ++i) {
			Expr r = rhs.get(i);
			// Determine width of expression
			executeAssignLVal(lhs.get(i), rvals[i], frame, scope, r);
		}
		return Status.NEXT;
	}

	private void executeAssignLVal(LVal lval, RValue rval, CallStack frame, EnclosingScope scope,
			SyntacticItem context) {
		switch (lval.getOpcode()) {
		case EXPR_arrayborrow:
		case EXPR_arrayaccess: {
			executeAssignArray((Expr.ArrayAccess) lval, rval, frame, scope, context);
			break;
		}
		case EXPR_dereference: {
			executeAssignDereference((Expr.Dereference) lval, rval, frame, scope, context);
			break;
		}
		case EXPR_fielddereference: {
			executeAssignFieldDereference((Expr.FieldDereference) lval, rval, frame, scope, context);
			break;
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			executeAssignRecord((Expr.RecordAccess) lval, rval, frame, scope, context);
			break;
		}
		case EXPR_variablemove:
		case EXPR_variablecopy: {
			executeAssignVariable((Expr.VariableAccess) lval, rval, frame, scope, context);
			break;
		}
		case EXPR_tupleinitialiser: {
			executeAssignTuple((Expr.TupleInitialiser) lval, rval, frame, scope, context);
			break;
		}
		default:
			deadCode(lval);
		}
	}

	private void executeAssignArray(Expr.ArrayAccess lval, RValue rval, CallStack frame, EnclosingScope scope,
			SyntacticItem context) {
		RValue.Array array = executeExpression(ARRAY_T, lval.getFirstOperand(), frame);
		RValue.Int index = executeExpression(INT_T, lval.getSecondOperand(), frame);
		// Sanity check access
		checkArrayBounds(array, index, frame, lval.getSecondOperand());
		// Update the array
		array = array.write(index, rval);
		// Write the results back
		executeAssignLVal((LVal) lval.getFirstOperand(), array, frame, scope, context);
	}

	private void executeAssignDereference(Expr.Dereference lval, RValue rval, CallStack frame, EnclosingScope scope,
			SyntacticItem context) {
		RValue.Reference ref = executeExpression(REF_T, lval.getOperand(), frame);
		// FIXME: need to check type invariant here??
		RValue.Cell cell = ref.deref();
		cell.write(rval);
	}

	private void executeAssignFieldDereference(Expr.FieldDereference lval, RValue rval, CallStack frame, EnclosingScope scope,
			SyntacticItem context) {
		RValue.Reference ref = executeExpression(REF_T, lval.getOperand(), frame);
		// Extract target cell
		RValue.Cell cell = ref.deref();
		// FIXME: need to check type invariant here??
		RValue.Record record = checkType(cell.read(), lval.getOperand(), RECORD_T);
		// Update target
		cell.write(record.write(lval.getField(), rval));
	}

	private void executeAssignRecord(Expr.RecordAccess lval, RValue rval, CallStack frame, EnclosingScope scope,
			SyntacticItem context) {
		RValue.Record record = executeExpression(RECORD_T, lval.getOperand(), frame);
		// Write rval to field
		record = record.write(lval.getField(), rval);
		//
		executeAssignLVal((LVal) lval.getOperand(), record, frame, scope, context);
	}

	private void executeAssignVariable(Expr.VariableAccess lval, RValue rval, CallStack frame, EnclosingScope scope,
			SyntacticItem context) {
		// Check type invariants for lval being assigned
		checkTypeInvariants(lval.getVariableDeclaration().getType(), rval, frame, context);
		//
		frame.putLocal(lval.getVariableDeclaration().getName(), rval);
	}

	private void executeAssignTuple(Expr.TupleInitialiser lval, RValue rval, CallStack frame, EnclosingScope scope,
			SyntacticItem context) {
		Tuple<Expr> operands = lval.getOperands();
		// Check we have a tuple as expected!
		RValue.Tuple tuple = checkType(rval, lval, TUPLE_T);
		//
		if(tuple.size() != operands.size()) {
			deadCode(lval);
		}
		//
		for(int i=0;i!=operands.size();++i) {
			executeAssignLVal((LVal) operands.get(i), tuple.get(i), frame, scope, context);
		}

	}

	/**
	 * Execute an assert or assume statement. In both cases, if the condition
	 * evaluates to false an exception is thrown.
	 *
	 * @param stmt  --- Assert statement.
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeAssert(Stmt.Assert stmt, CallStack frame, EnclosingScope scope) {
		//
		checkInvariants(WyilFile.RUNTIME_ASSERTION_FAILURE, frame, stmt.getCondition());
		return Status.NEXT;
	}

	/**
	 * Execute an assert or assume statement. In both cases, if the condition
	 * evaluates to false an exception is thrown.
	 *
	 * @param stmt  --- Assert statement.
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeAssume(Stmt.Assume stmt, CallStack frame, EnclosingScope scope) {
		//
		checkInvariants(WyilFile.RUNTIME_ASSUMPTION_FAILURE, frame, stmt.getCondition());
		return Status.NEXT;
	}

	/**
	 * Execute a break statement. This transfers to control out of the nearest
	 * enclosing loop.
	 *
	 * @param stmt  --- Break statement.
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeBreak(Stmt.Break stmt, CallStack frame, EnclosingScope scope) {
		// TODO: the break bytecode supports a non-nearest exit and eventually
		// this should be supported.
		return Status.BREAK;
	}

	/**
	 * Execute a continue statement. This transfers to control back to the start the
	 * nearest enclosing loop.
	 *
	 * @param stmt  --- Break statement.
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeContinue(Stmt.Continue stmt, CallStack frame, EnclosingScope scope) {
		return Status.CONTINUE;
	}

	/**
	 * Execute a Debug statement at a given point in the function or method body.
	 * This will write the provided string out to the debug stream.
	 *
	 * @param stmt  --- Debug statement to executed
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeDebug(Stmt.Debug stmt, CallStack frame, EnclosingScope scope) {
		//
		RValue.Array arr = executeExpression(ARRAY_T, stmt.getOperand(), frame);
		//
		for (RValue item : arr.getElements()) {
			RValue.Int i = (RValue.Int) item;
			char c = (char) i.intValue();
			debug.print(c);
		}
		//
		return Status.NEXT;
	}

	/**
	 * Execute a DoWhile statement at a given point in the function or method body.
	 * This will loop over the body zero or more times.
	 *
	 * @param stmt  --- Loop statement to executed
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeDoWhile(Stmt.DoWhile stmt, CallStack frame, EnclosingScope scope) {
		int errcode = WyilFile.RUNTIME_LOOPINVARIANT_ESTABLISH_FAILURE;
		Status r = Status.NEXT;
		while (r == Status.NEXT || r == Status.CONTINUE) {
			r = executeBlock(stmt.getBody(), frame, scope);
			if (r == Status.NEXT) {
				// NOTE: only check loop invariant if normal execution, since breaks are handled
				// differently.
				checkInvariants(errcode, frame, stmt.getInvariant(), null);
				errcode = WyilFile.RUNTIME_LOOPINVARIANT_RESTORED_FAILURE;
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
	 * @param stmt  --- The fail statement to execute
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeFail(Stmt.Fail stmt, CallStack frame, EnclosingScope scope) {
		throw new RuntimeError(WyilFile.RUNTIME_FAULT, frame, stmt);
	}

	/**
	 * Execute a for statement at a given point in the function or method body. This
	 * will loop over the body zero or more times.
	 *
	 *
	 * @param stmt  --- The for statement to execute
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeFor(Stmt.For stmt, CallStack frame, EnclosingScope scope) {
		Decl.StaticVariable var = stmt.getVariable();
		int errcode = WyilFile.RUNTIME_LOOPINVARIANT_ESTABLISH_FAILURE;
		Status r = Status.NEXT;
		// Evaluate index array
		RValue[] indices = executeExpression(ARRAY_T, var.getInitialiser(), frame).getElements();
		// Execute loop
		for(int i=0;i!=indices.length;++i) {
			// Assign index variable
			frame.putLocal(var.getName(), indices[i]);
			// Check loop invariants
			checkInvariants(errcode, frame, stmt.getInvariant(), null);
			// Keep executing the loop body until we exit it somehow.
			r = executeBlock(stmt.getBody(), frame, scope);
			if(r == Status.BREAK || r == Status.RETURN) {
				break;
			} else if(r == Status.CONTINUE) {
				r = Status.NEXT;
			}
			errcode = WyilFile.RUNTIME_LOOPINVARIANT_RESTORED_FAILURE;
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
	 * Execute an if statement at a given point in the function or method body. This
	 * will proceed done either the true or false branch.
	 *
	 * @param stmt  --- The if statement to execute
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeIf(Stmt.IfElse stmt, CallStack frame, EnclosingScope scope) {
		RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame);
		if (operand == RValue.True) {
			// branch taken, so execute true branch
			return executeBlock(stmt.getTrueBranch(), frame, scope);
		} else if (stmt.hasFalseBranch()) {
			// branch not taken, so execute false branch
			return executeBlock(stmt.getFalseBranch(), frame, scope);
		} else {
			return Status.NEXT;
		}
	}

	private Status executeInitialiser(Stmt.Initialiser stmt, CallStack frame, EnclosingScope scope) {
		Tuple<Decl.Variable> variables = stmt.getVariables();
		//
		if(!stmt.hasInitialiser()) {
			// Do nothing as no initialiser!
		} else if(variables.size() == 1) {
			// Easy case --- unit assignment
			RValue value = executeExpression(ANY_T, stmt.getInitialiser(), frame);
			// Check type invariants are established
			checkTypeInvariants(stmt.getType(), value, frame, stmt.getInitialiser());
			// Assign variable
			frame.putLocal(variables.get(0).getName(), value);
		} else {
			// Construct lhs type
			Type type = Type.Tuple.create(variables.map(v -> v.getType()));
			// Harder case --- tuple assignment
			RValue.Tuple value = executeExpression(TUPLE_T, stmt.getInitialiser(), frame);
			// Check type invariants are established
			checkTypeInvariants(type,value,frame,stmt.getInitialiser());
			// Assign individual components
			for(int i=0;i!=variables.size();++i) {
				frame.putLocal(variables.get(i).getName(), value.get(i));
			}
		}
		// Done
		return Status.NEXT;
	}

	/**
	 * Execute a named block which is simply a block of statements.
	 *
	 * @param stmt  --- Block statement to executed
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeNamedBlock(Stmt.NamedBlock stmt, CallStack frame, EnclosingScope scope) {
		return executeBlock(stmt.getBlock(), frame, scope);
	}

	/**
	 * Execute a While statement at a given point in the function or method body.
	 * This will loop over the body zero or more times.
	 *
	 * @param stmt  --- Loop statement to executed
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeWhile(Stmt.While stmt, CallStack frame, EnclosingScope scope) {
		int errcode = WyilFile.RUNTIME_LOOPINVARIANT_ESTABLISH_FAILURE;
		Status r;
		int count = 0;
		do {
			checkInvariants(errcode, frame, stmt.getInvariant(), null);
			RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame);
			if (operand == RValue.False) {
				return Status.NEXT;
			}
			// Keep executing the loop body until we exit it somehow.
			r = executeBlock(stmt.getBody(), frame, scope);
			//
			errcode = WyilFile.RUNTIME_LOOPINVARIANT_RESTORED_FAILURE;
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
	 * Execute a Return statement at a given point in the function or method body
	 *
	 * @param stmt  --- The return statement to execute
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeReturn(Stmt.Return stmt, CallStack frame, EnclosingScope scope) {
		// We know that a return statement can only appear in either a function
		// or method declaration. It cannot appear, for example, in a type
		// declaration. Therefore, the enclosing declaration is a function or
		// method.
		FunctionOrMethodScope enclosingScope = scope.getEnclosingScope(FunctionOrMethodScope.class);
		// Extract relevant information
		Decl.FunctionOrMethod context = enclosingScope.getContext();
		Tuple<Decl.Variable> returns = context.getReturns();
		Type.Callable type = context.getType();
		if(stmt.hasReturn()) {
			// Execute return expressions
			RValue value = executeExpression(ANY_T, stmt.getReturn(), frame);
			// Check type invariants
			checkTypeInvariants(type.getReturn(), value, frame, stmt.getReturn());
			// Configure return values
			if(returns.size() > 1) {
				RValue.Tuple t = (RValue.Tuple) value;
				for(int i=0;i!=returns.size();++i) {
					Decl.Variable r = returns.get(i);
					frame.putLocal(r.getName(), t.get(i));
				}
			} else {
				Decl.Variable r = returns.get(0);
				frame.putLocal(r.getName(), value);
			}
			// Restore original parameter values
			extractParameters(frame, enclosingScope.getArguments(), context);
			// Check the postcondition holds
			checkInvariants(WyilFile.RUNTIME_POSTCONDITION_FAILURE, frame, context.getEnsures(), stmt);
		}
		//
		return Status.RETURN;
	}

	/**
	 * Execute a skip statement at a given point in the function or method body
	 *
	 * @param stmt  --- The skip statement to execute
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeSkip(Stmt.Skip stmt, CallStack frame, EnclosingScope scope) {
		// skip !
		return Status.NEXT;
	}

	/**
	 * Execute a Switch statement at a given point in the function or method body
	 *
	 * @param stmt  --- The swithc statement to execute
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeSwitch(Stmt.Switch stmt, CallStack frame, EnclosingScope scope) {
		Tuple<Stmt.Case> cases = stmt.getCases();
		//
		Object value = executeExpression(ANY_T, stmt.getCondition(), frame);
		for (int i = 0; i != cases.size(); ++i) {
			Stmt.Case c = cases.get(i);
			Stmt.Block body = c.getBlock();
			if (c.isDefault()) {
				return executeBlock(body, frame, scope);
			} else {
				// FIXME: this is a temporary hack until a proper notion of
				// ConstantExpr is introduced.
				RValue[] values = executeExpressions(c.getConditions(), frame);
				for (RValue v : values) {
					if (v.equals(value)) {
						return executeBlock(body, frame, scope);
					}
				}
			}
		}
		return Status.NEXT;
	}

	/**
	 * Execute a variable declaration statement at a given point in the function or
	 * method body
	 *
	 * @param stmt  --- The statement to execute
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeVariableDeclaration(Decl.Variable stmt, CallStack frame) {
		// We only need to do something if this has an initialiser
//		if (stmt.hasInitialiser()) {
//			RValue value = executeExpression(ANY_T, stmt.getInitialiser(), frame);
//			// Check type invariants are established
//			checkTypeInvariants(stmt.getType(), value, frame, stmt.getInitialiser());
//			//
//			frame.putLocal(stmt.getName(), value);
//		}
		return Status.NEXT;
	}

	// =============================================================
	// Single expressions
	// =============================================================

	/**
	 * Execute a single expression which is expected to return a single result of an
	 * expected type. If a result of an incorrect type is returned, then an
	 * exception is raised.
	 *
	 * @param expected The expected type of the result
	 * @param expr     The expression to be executed
	 * @param frame    The frame in which the expression is executing
	 * @return
	 */
	public <T extends RValue> T executeExpression(Class<T> expected, Expr expr, CallStack frame) {
		checkForTimeout(frame);
		//
		RValue val;
		switch (expr.getOpcode()) {
		case EXPR_constant:
			val = executeConst((Expr.Constant) expr, frame);
			break;
		case EXPR_cast:
			val = executeConvert((Expr.Cast) expr, frame);
			break;
		case EXPR_recordinitialiser:
			val = executeRecordInitialiser((Expr.RecordInitialiser) expr, frame);
			break;
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			val = executeRecordAccess((Expr.RecordAccess) expr, frame);
			break;
		case EXPR_indirectinvoke:
			val = executeIndirectInvoke((Expr.IndirectInvoke) expr, frame);
			break;
		case EXPR_invoke:
			val = executeInvoke((Expr.Invoke) expr, frame);
			break;
		case EXPR_variablemove:
		case EXPR_variablecopy:
			val = executeVariableAccess((Expr.VariableAccess) expr, frame);
			break;
		case EXPR_staticvariable:
			val = executeStaticVariableAccess((Expr.StaticVariableAccess) expr, frame);
			break;
		case EXPR_is:
			val = executeIs((Expr.Is) expr, frame);
			break;
		case EXPR_logicalnot:
			val = executeLogicalNot((Expr.LogicalNot) expr, frame);
			break;
		case EXPR_logicaland:
			val = executeLogicalAnd((Expr.LogicalAnd) expr, frame);
			break;
		case EXPR_logicalor:
			val = executeLogicalOr((Expr.LogicalOr) expr, frame);
			break;
		case EXPR_logicalimplication:
			val = executeLogicalImplication((Expr.LogicalImplication) expr, frame);
			break;
		case EXPR_logicaliff:
			val = executeLogicalIff((Expr.LogicalIff) expr, frame);
			break;
		case EXPR_logicalexistential:
		case EXPR_logicaluniversal:
			val = executeQuantifier((Expr.Quantifier) expr, frame);
			break;
		case EXPR_equal:
			val = executeEqual((Expr.Equal) expr, frame);
			break;
		case EXPR_notequal:
			val = executeNotEqual((Expr.NotEqual) expr, frame);
			break;
		case EXPR_integernegation:
			val = executeIntegerNegation((Expr.IntegerNegation) expr, frame);
			break;
		case EXPR_integeraddition:
			val = executeIntegerAddition((Expr.IntegerAddition) expr, frame);
			break;
		case EXPR_integersubtraction:
			val = executeIntegerSubtraction((Expr.IntegerSubtraction) expr, frame);
			break;
		case EXPR_integermultiplication:
			val = executeIntegerMultiplication((Expr.IntegerMultiplication) expr, frame);
			break;
		case EXPR_integerdivision:
			val = executeIntegerDivision((Expr.IntegerDivision) expr, frame);
			break;
		case EXPR_integerremainder:
			val = executeIntegerRemainder((Expr.IntegerRemainder) expr, frame);
			break;
		case EXPR_integerlessthan:
			val = executeIntegerLessThan((Expr.IntegerLessThan) expr, frame);
			break;
		case EXPR_integerlessequal:
			val = executeIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr, frame);
			break;
		case EXPR_integergreaterthan:
			val = executeIntegerGreaterThan((Expr.IntegerGreaterThan) expr, frame);
			break;
		case EXPR_integergreaterequal:
			val = executeIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr, frame);
			break;
		case EXPR_bitwisenot:
			val = executeBitwiseNot((Expr.BitwiseComplement) expr, frame);
			break;
		case EXPR_bitwiseor:
			val = executeBitwiseOr((Expr.BitwiseOr) expr, frame);
			break;
		case EXPR_bitwisexor:
			val = executeBitwiseXor((Expr.BitwiseXor) expr, frame);
			break;
		case EXPR_bitwiseand:
			val = executeBitwiseAnd((Expr.BitwiseAnd) expr, frame);
			break;
		case EXPR_bitwiseshl:
			val = executeBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr, frame);
			break;
		case EXPR_bitwiseshr:
			val = executeBitwiseShiftRight((Expr.BitwiseShiftRight) expr, frame);
			break;
		case EXPR_arrayborrow:
		case EXPR_arrayaccess:
			val = executeArrayAccess((Expr.ArrayAccess) expr, frame);
			break;
		case EXPR_arraygenerator:
			val = executeArrayGenerator((Expr.ArrayGenerator) expr, frame);
			break;
		case EXPR_arraylength:
			val = executeArrayLength((Expr.ArrayLength) expr, frame);
			break;
		case EXPR_arrayinitialiser:
			val = executeArrayInitialiser((Expr.ArrayInitialiser) expr, frame);
			break;
		case EXPR_arrayrange:
			val = executeArrayRange((Expr.ArrayRange) expr, frame);
			break;
		case EXPR_new:
			val = executeNew((Expr.New) expr, frame);
			break;
		case EXPR_dereference:
			val = executeDereference((Expr.Dereference) expr, frame);
			break;
		case EXPR_fielddereference:
			val = executeFieldDereference((Expr.FieldDereference) expr, frame);
			break;
		case EXPR_lambdaaccess:
			val = executeLambdaAccess((Expr.LambdaAccess) expr, frame);
			break;
		case DECL_lambda:
			val = executeLambdaDeclaration((Decl.Lambda) expr, frame);
			break;
		case EXPR_tupleinitialiser:
			val = executeTupleInitialiser((Expr.TupleInitialiser) expr, frame);
			break;
		default:
			return deadCode(expr);
		}
		return checkType(val, expr, expected);
	}

	/**
	 * Execute a Constant expression at a given point in the function or method body
	 *
	 * @param expr  --- The expression to execute
	 * @param frame --- The current stack frame
	 * @return
	 */
	private RValue executeConst(Expr.Constant expr, CallStack frame) {
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
		case ITEM_byte: {
			Value.Byte b = (Value.Byte) v;
			return semantics.Byte(b.get());
		}
		case ITEM_int: {
			Value.Int i = (Value.Int) v;
			return semantics.Int(i.get());
		}
		case ITEM_utf8: {
			Value.UTF8 s = (Value.UTF8) v;
			byte[] bytes = s.get();
			RValue[] elements = new RValue[bytes.length];
			for (int i = 0; i != elements.length; ++i) {
				// FIXME: something tells me this is wrong for signed byte
				// values?
				elements[i] = semantics.Int(BigInteger.valueOf(bytes[i]));
			}
			return semantics.Array(elements);
		}
		default:
			throw new RuntimeException("unknown value encountered (" + expr + ")");
		}
	}

	/**
	 * Execute a type conversion at a given point in the function or method body
	 *
	 * @param expr  --- The expression to execute
	 * @param frame --- The current stack frame
	 * @return
	 */
	private RValue executeConvert(Expr.Cast expr, CallStack frame) {
		RValue operand = executeExpression(ANY_T, expr.getOperand(), frame);
		return operand.convert(expr.getType());
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
			values[i] = semantics.Field(field, value);
		}
		return semantics.Record(values);
	}

	private RValue executeTupleInitialiser(Expr.TupleInitialiser expr, CallStack frame) {
		Tuple<Expr> operands = expr.getOperands();
		RValue[] values = new RValue[operands.size()];
		for (int i = 0; i != values.length; ++i) {
			values[i] = executeExpression(ANY_T, operands.get(i), frame);
		}
		return semantics.Tuple(values);
	}

	private RValue executeQuantifier(Expr.Quantifier expr, CallStack frame) {
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
	private boolean executeQuantifier(int index, Expr.Quantifier expr, CallStack frame) {
		Tuple<Decl.StaticVariable> vars = expr.getParameters();
		if (index == vars.size()) {
			// This is the base case where we evaluate the condition itself.
			RValue.Bool r = executeExpression(BOOL_T, expr.getOperand(), frame);
			boolean q = (expr instanceof Expr.UniversalQuantifier);
			// If this evaluates to true, then we will continue executing the
			// quantifier.
			return r.boolValue() == q;
		} else {
			Decl.StaticVariable var = vars.get(index);
			RValue.Array range = executeExpression(ARRAY_T, var.getInitialiser(), frame);
			RValue[] elements = range.getElements();
			for (int i = 0; i != elements.length; ++i) {
				frame.putLocal(var.getName(), elements[i]);
				boolean r = executeQuantifier(index + 1, expr, frame);
				if (!r) {
					// early termination
					return r;
				}
			}
			return true;
		}
	}

	/**
	 * Execute a variable access expression at a given point in the function or
	 * method body. This simply loads the value of the given variable from the
	 * frame.
	 *
	 * @param expr  --- The expression to execute
	 * @param frame --- The current stack frame
	 * @return
	 */
	private RValue executeVariableAccess(Expr.VariableAccess expr, CallStack frame) {
		Decl.Variable decl = expr.getVariableDeclaration();
		return frame.getLocal(decl.getName());
	}

	private RValue executeStaticVariableAccess(Expr.StaticVariableAccess expr, CallStack frame) {
		Decl.StaticVariable decl = expr.getLink().getTarget();
		RValue v = frame.getStatic(decl.getQualifiedName());
		if (v == null) {
			// NOTE: it's possible to get here without the static variable having been
			// initialised in the special case that we have just loaded a module.
			frame = frame.enter(decl);
			v = executeExpression(ANY_T, decl.getInitialiser(), frame);
			frame.putStatic(decl.getQualifiedName(), v);
		}
		return v;
	}

	private RValue executeIs(Expr.Is expr, CallStack frame) {
		RValue lhs = executeExpression(ANY_T, expr.getOperand(), frame);
		return lhs.is(expr.getTestType(), frame);
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
		checkDivisionByZero(rhs, frame, expr.getSecondOperand());
		return lhs.divide(rhs);
	}

	public RValue executeIntegerRemainder(Expr.IntegerRemainder expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		checkDivisionByZero(rhs, frame, expr.getSecondOperand());
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
		for (int i = 0; i != operands.size(); ++i) {
			RValue.Bool b = executeExpression(BOOL_T, operands.get(i), frame);
			if (b == RValue.False) {
				return b;
			}
		}
		return RValue.True;
	}

	public RValue executeLogicalOr(Expr.LogicalOr expr, CallStack frame) {
		// This is a short-circuiting operator. Therefore, we succeed as soon as one
		// argument succeeds.
		Tuple<Expr> operands = expr.getOperands();
		for (int i = 0; i != operands.size(); ++i) {
			RValue.Bool b = executeExpression(BOOL_T, operands.get(i), frame);
			if (b == RValue.True) {
				return b;
			}
		}
		return RValue.False;
	}

	public RValue executeLogicalImplication(Expr.LogicalImplication expr, CallStack frame) {
		RValue.Bool lhs = executeExpression(BOOL_T, expr.getFirstOperand(), frame);
		if (lhs == RValue.False) {
			return RValue.True;
		} else {
			RValue.Bool rhs = executeExpression(BOOL_T, expr.getSecondOperand(), frame);
			return lhs.equal(rhs);
		}
	}

	public RValue executeLogicalIff(Expr.LogicalIff expr, CallStack frame) {
		RValue.Bool lhs = executeExpression(BOOL_T, expr.getFirstOperand(), frame);
		RValue.Bool rhs = executeExpression(BOOL_T, expr.getSecondOperand(), frame);
		return lhs.equal(rhs);
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
		// Sanity check access
		checkArrayBounds(array, index, frame, expr.getSecondOperand());
		// Perform the read
		return array.read(index);
	}

	public RValue executeArrayGenerator(Expr.ArrayGenerator expr, CallStack frame) {
		RValue element = executeExpression(ANY_T, expr.getFirstOperand(), frame);
		RValue.Int count = executeExpression(INT_T, expr.getSecondOperand(), frame);
		int n = count.intValue();
		if (n < 0) {
			throw new RuntimeError(WyilFile.RUNTIME_NEGATIVE_LENGTH_FAILURE, frame, expr.getSecondOperand());
		}
		RValue[] values = new RValue[n];
		for (int i = 0; i != n; ++i) {
			values[i] = element;
		}
		return semantics.Array(values);
	}

	public RValue executeArrayInitialiser(Expr.ArrayInitialiser expr, CallStack frame) {
		Tuple<Expr> operands = expr.getOperands();
		RValue[] elements = new RValue[operands.size()];
		for (int i = 0; i != elements.length; ++i) {
			elements[i] = executeExpression(ANY_T, operands.get(i), frame);
		}
		return semantics.Array(elements);
	}

	public RValue executeArrayRange(Expr.ArrayRange expr, CallStack frame) {
		int start = executeExpression(INT_T, expr.getFirstOperand(), frame).intValue();
		int end = executeExpression(INT_T, expr.getSecondOperand(), frame).intValue();
		if (start < 0 || end < start) {
			throw new RuntimeError(WyilFile.RUNTIME_NEGATIVE_RANGE_FAILURE, frame, expr.getSecondOperand());
		}
		RValue[] elements = new RValue[end - start];
		for (int i = start; i < end; ++i) {
			elements[i - start] = semantics.Int(BigInteger.valueOf(i));
		}
		return semantics.Array(elements);
	}

	public RValue executeNew(Expr.New expr, CallStack frame) {
		RValue initialiser = executeExpression(ANY_T, expr.getOperand(), frame);
		RValue.Cell cell = semantics.Cell(initialiser);
		return semantics.Reference(cell);
	}

	public RValue executeDereference(Expr.Dereference expr, CallStack frame) {
		RValue.Reference ref = executeExpression(REF_T, expr.getOperand(), frame);
		return ref.deref().read();
	}

	public RValue executeFieldDereference(Expr.FieldDereference expr, CallStack frame) {
		RValue.Reference ref = executeExpression(REF_T, expr.getOperand(), frame);
		// Extract target
		RValue.Cell cell = ref.deref();
		// Extract record value
		RValue.Record rec = checkType(cell.read(),expr,RECORD_T);
		// Read the given field
		return rec.read(expr.getField());
	}

	public RValue executeLambdaAccess(Expr.LambdaAccess expr, CallStack frame) {
		// Locate the function or method body in order to execute it
		Decl.Callable decl = expr.getLink().getTarget();
		//
		if (decl instanceof Decl.FunctionOrMethod) {
			Decl.FunctionOrMethod fm = (Decl.FunctionOrMethod) decl;
			// Clone frame to ensure it executes in this exact environment.
			return semantics.Lambda(decl, frame.clone());
		} else {
			throw new SyntacticException("cannot take address of property", null, expr);
		}
	}

	private RValue executeLambdaDeclaration(Decl.Lambda decl, CallStack frame) {
		// FIXME: this needs a clone of the frame? Otherwise, it's just
		// executing in the later environment.
		return semantics.Lambda(decl, frame.clone());
	}

	// =============================================================
	// Multiple expressions
	// =============================================================

	/**
	 * Execute one or more expressions. This is slightly more complex than for the
	 * single expression case because of the potential to encounter "positional
	 * operands". That is, severals which arise from executing the same expression.
	 *
	 * @param expressions
	 * @param frame
	 * @return
	 */
	private RValue[] executeExpressions(Tuple<Expr> expressions, CallStack frame) {
		RValue[] results = new RValue[expressions.size()];
		for (int i = 0; i != expressions.size(); ++i) {
			results[i] = executeExpression(ANY_T,expressions.get(i), frame);
		}
		return results;
	}

	/**
	 * Execute an IndirectInvoke bytecode instruction at a given point in the
	 * function or method body. This first checks the operand is a function
	 * reference, and then generates a recursive call to execute the given function.
	 * If the function does not exist, or is provided with the wrong number of
	 * arguments, then a runtime fault will occur.
	 *
	 * @param expr    --- The expression to execute
	 * @param frame   --- The current stack frame
	 * @param context --- Context in which bytecodes are executed
	 * @return
	 */
	private RValue executeIndirectInvoke(Expr.IndirectInvoke expr, CallStack frame) {
		RValue.Lambda src = executeExpression(LAMBDA_T, expr.getSource(), frame);
		RValue[] arguments = executeExpressions(expr.getArguments(), frame);
		// Extract concrete type
		Type.Callable type = src.getType();
		// Check parameter type invariants
		checkTypeInvariants(type.getParameter(), arguments, expr.getArguments(), frame);
		// Here we supply the enclosing frame when the lambda was created.
		// The reason for this is that the lambda may try to access enclosing
		// variables in the scope it was created.
		return src.execute(this,arguments,expr);
	}

	/**
	 * Execute an Invoke bytecode instruction at a given point in the function or
	 * method body. This generates a recursive call to execute the given function.
	 * If the function does not exist, or is provided with the wrong number of
	 * arguments, then a runtime fault will occur.
	 *
	 * @param expr  --- The expression to execute
	 * @param frame --- The current stack frame
	 * @return
	 * @throws ResolutionError
	 */
	private RValue executeInvoke(Expr.Invoke expr, CallStack frame) {
		// Resolve function or method being invoked to a concrete declaration
		Decl.Callable decl = expr.getLink().getTarget();
		// Evaluate argument expressions
		RValue[] arguments = executeExpressions(expr.getOperands(), frame);
		// Extract the concrete type
		Type.Callable type = expr.getBinding().getConcreteType();
		// Check type invariants
		checkTypeInvariants(type.getParameter(), arguments, expr.getOperands(), frame);
		// Invoke the function or method in question
		// FIXME: could potentially optimise this by calling execute with decl directly.
		// This currently fails for external symbols which are represented as
		// prototypes.
		return execute(decl.getQualifiedName(), decl.getType(), frame, arguments, expr);
	}

	// =============================================================
	// Constants
	// =============================================================

	public void checkArrayBounds(RValue.Array array, RValue.Int index, CallStack frame, SyntacticItem context) {
		int len = array.length().intValue();
		int idx = index.intValue();
		if (idx < 0) {
			throw new RuntimeError(WyilFile.RUNTIME_BELOWBOUNDS_INDEX_FAILURE, frame, context);
		} else if (idx >= len) {
			throw new RuntimeError(WyilFile.RUNTIME_ABOVEBOUNDS_INDEX_FAILURE, frame, context);
		}
	}

	public void checkDivisionByZero(RValue.Int value, CallStack frame, SyntacticItem context) {
		if (value.intValue() == 0) {
			throw new RuntimeError(WyilFile.RUNTIME_DIVIDEBYZERO_FAILURE, frame, context);
		}
	}

	public void checkTypeInvariants(Type variables, RValue[] values, Tuple<Expr> rvals, CallStack frame) {
		// This is a bit tricky because we have to account for multi-expressions which,
		// as always, are annoying.
		int index = 0;
		for (int i = 0; i != rvals.size(); ++i) {
			Expr rval = rvals.get(i);
			checkTypeInvariants(variables.dimension(index), values[index++], frame, rval);
		}
	}

	public void checkTypeInvariants(Type type, RValue value, CallStack frame, SyntacticItem context) {
		if (value.is(type, frame).boolValue() == false) {
			throw new RuntimeError(WyilFile.RUNTIME_TYPEINVARIANT_FAILURE, frame, context);
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
	public void checkInvariants(int code, CallStack frame, Tuple<Expr> invariants, SyntacticItem context) {
		for (int i = 0; i != invariants.size(); ++i) {
			Expr invariant = invariants.get(i);
			// Execute invariant
			RValue.Bool b = executeExpression(BOOL_T, invariant, frame);
			// Check whether it holds or not
			if (b == RValue.False) {
				if(context == null) {
					throw new RuntimeError(code, frame, invariant);
				} else {
					throw new RuntimeError(code, frame, context, invariant);
				}
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
	public void checkPrecondition(int code, CallStack frame, Tuple<Expr> invariants, SyntacticItem context) {
		for (int i = 0; i != invariants.size(); ++i) {
			Expr invariant = invariants.get(i);
			// Execute invariant
			RValue.Bool b = executeExpression(BOOL_T, invariant, frame);
			// Check whether it holds or not
			if (b == RValue.False) {
				if (context != null) {
					throw new RuntimeError(code, frame, context, invariant);
				} else {
					throw new RuntimeError(code, frame, invariant);
				}
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
	public void checkInvariants(int code, CallStack frame, Expr... invariants) {
		for (int i = 0; i != invariants.length; ++i) {
			Expr invariant = invariants[i];
			// Execute invariant
			RValue.Bool b = executeExpression(BOOL_T, invariant, frame);
			// Check whether it holds or not
			if (b == RValue.False) {
				throw new RuntimeError(code, frame, invariant);
			}
		}
	}

	/**
	 * Check that a given operand value matches an expected type.
	 *
	 * @param operand --- bytecode operand to be checked
	 * @param context --- Context in which bytecodes are executed
	 * @param types   --- Types to be checked against
	 */
	@SafeVarargs
	public static <T extends RValue> T checkType(RValue operand, SyntacticItem context, Class<T>... types) {
		// Got through each type in turn checking for a match
		for (int i = 0; i != types.length; ++i) {
			if (types[i].isInstance(operand)) {
				// Matched!
				return (T) operand;
			}
		}
		// No match, therefore throw an error
		if (operand == null) {
			throw new SyntacticException("null operand", null, context);
		} else {
			throw new SyntacticException(
					"operand returned " + operand.getClass().getName() + ", expecting one of " + Arrays.toString(types),
					null, context);
		}
	}

	private void checkForTimeout(CallStack frame) {
		long time = System.currentTimeMillis();
		if(time > frame.getTimeout()) {
			throw new TimeoutException("timeout!");
		}
	}

	/**
	 * This method is provided to properly handled positions which should be dead
	 * code.
	 *
	 * @param context --- Context in which bytecodes are executed
	 */
	private <T> T deadCode(SyntacticItem element) {
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
	private static final Class<RValue.Tuple> TUPLE_T = RValue.Tuple.class;

	public final static class RuntimeError extends SyntacticException {
		private final int code;
		private final CallStack frame;

		public RuntimeError(int code, CallStack frame, SyntacticItem element, SyntacticItem... context) {
			super(ErrorMessages.getErrorMessage(code, new Tuple<>(context)), extractEntry(element), element);
			this.code = code;
			this.frame = frame;
		}

		public int getErrorCode() {
			return code;
		}

		public CallStack getFrame() {
			return frame;
		}

		private static Build.Artifact extractEntry(SyntacticItem item) {
			// FIXME: this feels like a hack
			SyntacticHeap h = item.getHeap();
			if (h instanceof WyilFile) {
				return ((WyilFile) h);
			} else {
				return null;
			}
		}
	}

	public final static class TimeoutException extends RuntimeException {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public TimeoutException(String msg) {
			super(msg);
		}
	}

	public final class CallStack {
		private CallStack parent;
		private long timeout;
		private final HashMap<QualifiedName, Map<String, Decl.Callable>> callables;
		private final HashMap<QualifiedName, RValue> statics;
		private final HashMap<Identifier, RValue> locals;
		private final Decl.Named context;

		public CallStack() {
			this.parent = null;
			this.timeout = Long.MAX_VALUE;
			this.callables = new HashMap<>();
			this.statics = new HashMap<>();
			this.locals = new HashMap<>();
			this.context = null;
		}

		private CallStack(CallStack parent, Decl.Named context) {
			this.parent = parent;
			this.timeout = parent.timeout;
			this.context = context;
			this.locals = new HashMap<>();
			this.statics = parent.statics;
			this.callables = parent.callables;
		}

		public long getTimeout() {
			return timeout;
		}

		public RValue getLocal(Identifier name) {
			return locals.get(name);
		}

		public void putLocal(Identifier name, RValue value) {
			locals.put(name, value);
		}

		public Map<Identifier,RValue> getLocals() {
			return locals;
		}

		public Map<QualifiedName,RValue> getStatics() {
			return statics;
		}

		public RValue getStatic(QualifiedName name) {
			return statics.get(name);
		}

		public void putStatic(QualifiedName name, RValue value) {
			statics.put(name, value);
		}

		public Decl.Callable getCallable(QualifiedName name, Type.Callable signature) {
			// NOTE: must use toCanonicalString() here in order to guarantee that we get the
			// same string as at the declaration site.
			return callables.get(name).get(signature.toCanonicalString());
		}

		public CallStack enter(Decl.Named<?> context) {
			return new CallStack(this, context);
		}

		public <T extends RValue> T execute(Class<T> expected, Expr expr, CallStack frame) {
			return Interpreter.this.executeExpression(expected, expr, frame);
		}

		public CallStack setTimeout(long timeout) {
			if(timeout != Long.MAX_VALUE) {
				long start = System.currentTimeMillis();
				this.timeout = start + timeout;
			}
			return this;
		}

		@Override
		public CallStack clone() {
			CallStack frame = new CallStack(this, this.context);
			// Reset parent
			frame.parent = this.parent;
			frame.locals.putAll(locals);
			return frame;
		}

		/**
		 * Load one or more modules into this CallStack.
		 *
		 * @param modules
		 * @return
		 */
		public CallStack load(WyilFile... modules) {
			for (int i = 0; i != modules.length; ++i) {
				load(modules[i]);
			}
			return this;
		}

		public StackFrame[] toStackFrame() {
			if(context == null) {
				return new StackFrame[0];
			} else {
				Value[] arguments;
				//
				switch(context.getOpcode()) {
				case DECL_function:
				case DECL_method:
				case DECL_property: {
					Decl.Callable fm = (Decl.Callable) context;
					Tuple<Decl.Variable> parameters = fm.getParameters();
					arguments = new Value[parameters.size()];
					for(int i=0;i!=arguments.length;++i) {
						Decl.Variable parameter = parameters.get(i);
						// FIXME: why is this needed?
						if(getLocal(parameter.getName()) != null) {
							arguments[i] = getLocal(parameter.getName()).toValue();
						} else {
							arguments[i] = new Value.Null();
						}
					}
					break;
				}
				case DECL_rectype:
				case DECL_type: {
					Decl.Type t = (Decl.Type) context;
					Decl.Variable parameter = t.getVariableDeclaration();
					arguments = new Value[1];
					arguments[0] = getLocal(parameter.getName()).toValue();
					break;
				}
				case DECL_staticvar: {
					arguments = new Value[0];
					break;
				}
				default:
					throw new IllegalArgumentException("unknown context: " + context.getQualifiedName());
				}
				// Extract parent stack frame (if applicable)
				StackFrame[] sf = parent != null ? parent.toStackFrame() : new StackFrame[0];
				// Append new item
				return ArrayUtils.append(new StackFrame(context, new Tuple<>(arguments)),sf);
			}
		}

		/**
		 * Load a given module and make sure that all static variables are properly
		 * initialised.
		 *
		 * @param mid
		 * @param frame
		 */
		private void load(WyilFile module) {
			// Load all invokable items
			for (Decl.Unit unit : module.getModule().getUnits()) {
				for (Decl d : unit.getDeclarations()) {
					switch (d.getOpcode()) {
					case DECL_function:
					case DECL_method:
					case DECL_property:
						Decl.Callable decl = (Decl.Callable) d;
						Map<String, Decl.Callable> map = callables.get(decl.getQualifiedName());
						if (map == null) {
							map = new HashMap<>();
							callables.put(decl.getQualifiedName(), map);
						}
						// NOTE: must use canonical string here to ensure unique signature for lookup.
						map.put(decl.getType().toCanonicalString(), decl);
						break;
					}
				}
			}
			// Execute all static variable initialisers
			for (Decl.Unit unit : module.getModule().getUnits()) {
				for (Decl d : unit.getDeclarations()) {
					switch (d.getOpcode()) {
					case DECL_staticvar: {
						Decl.StaticVariable decl = (Decl.StaticVariable) d;
						if (!statics.containsKey(decl.getQualifiedName())) {
							// Static variable has not been initialised yet, therefore force its
							// initialisation.
							RValue value = executeExpression(ANY_T, decl.getInitialiser(), this);
							// Check type invariants.
							checkTypeInvariants(decl.getType(), value, this, decl);
							// Done.
							statics.put(decl.getQualifiedName(), value);
						}
						break;
					}
					}
				}
			}
		}
	}

	/**
	 * An enclosing scope captures the nested of declarations, blocks and other
	 * staments (e.g. loops). It is used to store information associated with these
	 * things such they can be accessed further down the chain. It can also be used
	 * to propagate information up the chain (for example, the environments arising
	 * from a break or continue statement).
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
		 * processing a return statement we may wish to get the enclosing function or
		 * method declaration such that we can type check the return types.
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
		/**
		 * The declaration being invoked
		 */
		private final Decl.FunctionOrMethod context;
		/**
		 * Arguments provided to the invocation.  These are needed for evaluating the post-condition at the point of a return.
		 * @param context
		 */
		private final RValue[] args;

		public FunctionOrMethodScope(Decl.FunctionOrMethod context, RValue[] args) {
			super(null);
			this.context = context;
			this.args = args;
		}

		public Decl.FunctionOrMethod getContext() {
			return context;
		}

		public RValue[] getArguments() {
			return args;
		}
	}
}
