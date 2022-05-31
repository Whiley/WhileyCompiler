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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import wycc.util.ArrayUtils;
import wycc.util.Trie;
import wycc.lang.Syntactic;
import wycc.util.AbstractCompilationUnit.Identifier;
import wycc.util.AbstractCompilationUnit.Tuple;
import wycc.util.AbstractCompilationUnit.Value;
import wyc.util.ErrorMessages;
import wyil.interpreter.ConcreteSemantics.RValue;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.LVal;
import wyil.lang.WyilFile.QualifiedName;
import wyil.lang.WyilFile.Attr.StackFrame;
import wyil.lang.WyilFile.Stmt;
import wyil.lang.WyilFile.Type;
import wyil.util.AbstractVisitor;

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

	/**
	 * Set of loaded methods / functions which can be called.
	 */
	private final HashMap<QualifiedName, Map<String, Decl.Callable>> callables;

	/**
	 * Defines the heap
	 */
	private final Heap globalHeap;

	/**
	 * The maximum permissable stack depth.
	 */
	private final int maxStackDepth = 32;

	public Interpreter(PrintStream debug, Collection<WyilFile> modules) {
		this(debug, modules.toArray(new WyilFile[modules.size()]));
	}

	public Interpreter(PrintStream debug, WyilFile... modules) {
		this.debug = debug;
		this.semantics = new ConcreteSemantics();
		this.callables = new HashMap<>();
		this.globalHeap = new Heap();
		//
		for (WyilFile mod : modules) {
			load(mod);
		}
	}

	private enum Status {
		RETURN, BREAK, CONTINUE, NEXT
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
				case DECL_variant:
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
					if (globalHeap.read(decl.getQualifiedName()) == null) {
						// Static variable has not been initialised yet, therefore force its
						// initialisation.
						RValue value = executeExpression(ANY_T, decl.getInitialiser(), new CallStack(), globalHeap);
						// Check type invariants.
						checkTypeInvariants(decl.getType(), value, new CallStack(), globalHeap, decl);
						// Done.
						globalHeap.write(decl.getQualifiedName(), value);
					}
					break;
				}
				}
			}
		}
	}

	public Decl.Callable getCallable(QualifiedName name, Type.Callable signature) {
		// NOTE: must use toCanonicalString() here in order to guarantee that we get the
		// same string as at the declaration site.
		Map<String,Decl.Callable> candidates = callables.get(name);
		if(candidates == null) {
			return null;
		} else {
			return candidates.get(signature.toCanonicalString());
		}
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
		return execute(name, signature, frame, globalHeap, args, null);
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
	public RValue execute(QualifiedName name, Type.Callable signature, CallStack frame, Heap heap, RValue[] args,
			Syntactic.Item context) {
		Decl.Callable lambda = getCallable(name, signature);
		if (lambda == null) {
			throw new IllegalArgumentException("no function or method found: " + name + ", " + signature);
		} else if (lambda.getParameters().size() != args.length) {
			throw new IllegalArgumentException(
					"incorrect number of arguments: " + lambda.getName() + ", " + lambda.getType());
		} else if (frame.depth() >= maxStackDepth) {
			throw new RuntimeError(WyilFile.RUNTIME_FAULT, frame, context);
		}
		// Enter a new frame for executing this callable item
		frame = frame.enter(lambda);
		// Execute the lambda we've extracted
		return execute(lambda, frame, heap, args, context);
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
	public RValue execute(Decl.Callable lambda, CallStack frame, Heap heap, RValue[] args, Syntactic.Item context) {
		// Fourth, construct the stack frame for execution
		extractParameters(frame, args, lambda);
		// Check the precondition
		if (lambda instanceof Decl.FunctionOrMethod) {
			Decl.FunctionOrMethod fm = (Decl.FunctionOrMethod) lambda;
			// Check preconditions hold
			checkPrecondition(WyilFile.RUNTIME_PRECONDITION_FAILURE, frame, heap, fm.getRequires(), context);
			// check function or method body exists
			if (fm.getBody().size() == 0) {
				// FIXME: Add support for native functions or methods. That is,
				// allow native functions to be implemented and called from the
				// interpreter.
				throw new IllegalArgumentException(
						"no function or method body found: " + lambda.getQualifiedName() + " : " + lambda.getType());
			} else if (fm instanceof Decl.Method) {
				// Stash the prestate as it is
				frame.putLocal(OLD, heap.clone());
			}
			// Execute the method or function body
			executeBlock(fm.getBody(), frame, heap, new CallableScope(fm));
			// Restore original parameter values
			extractParameters(frame, args, fm);
			// Check the postcondition holds
			checkInvariants(WyilFile.RUNTIME_POSTCONDITION_FAILURE, frame, heap, fm.getEnsures(), context);
			// Extract the return values
			return packReturns(frame, lambda);
		} else if (lambda instanceof Decl.Lambda) {
			Decl.Lambda l = (Decl.Lambda) lambda;
			return executeExpression(ANY_T, l.getBody(), frame, heap);
		} else if (lambda instanceof Decl.Property) {
			Decl.Property p = (Decl.Property) lambda;
			Type type = p.getType().getReturn();
			// Execute the method or function body
			executeBlock(p.getBody(), frame, heap, new CallableScope(p));
			// Extract the return values
			return packReturns(frame, lambda);
		} else {
			Decl.Variant p = (Decl.Variant) lambda;
			Tuple<Expr> invariant = p.getInvariant();
			// Evaluate clauses of property, and terminate early as soon as one doesn't
			// hold.
			for (int i = 0; i != invariant.size(); ++i) {
				RValue.Bool retval = executeExpression(BOOL_T, invariant.get(i), frame, heap);
				if (!retval.boolValue()) {
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
		if (decl instanceof Decl.Variant) {
			return RValue.True;
		} else {
			Tuple<Decl.Variable> returns = decl.getReturns();
			if (returns.size() == 1) {
				Decl.Variable v = returns.get(0);
				return frame.getLocal(v.getName());
			} else {
				RValue[] items = new RValue[returns.size()];
				for (int i = 0; i != items.length; ++i) {
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
	private Status executeBlock(Stmt.Block block, CallStack frame, Heap heap, EnclosingScope scope) {
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			Status r = executeStatement(stmt, frame, heap, scope);
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
	private Status executeStatement(Stmt stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		checkForTimeout(frame);
		//
		try {
			//
			switch (stmt.getOpcode()) {
			case STMT_assert:
				return executeAssert((Stmt.Assert) stmt, frame, heap, scope);
			case STMT_assume:
				return executeAssume((Stmt.Assume) stmt, frame, heap, scope);
			case STMT_assign:
				return executeAssign((Stmt.Assign) stmt, frame, heap, scope);
			case STMT_break:
				return executeBreak((Stmt.Break) stmt, frame, heap, scope);
			case STMT_continue:
				return executeContinue((Stmt.Continue) stmt, frame, heap, scope);
			case STMT_debug:
				return executeDebug((Stmt.Debug) stmt, frame, heap, scope);
			case STMT_dowhile:
				return executeDoWhile((Stmt.DoWhile) stmt, frame, heap, scope);
			case STMT_fail:
				return executeFail((Stmt.Fail) stmt, frame, heap, scope);
			case STMT_for:
				return executeFor((Stmt.For) stmt, frame, heap, scope);
			case STMT_if:
			case STMT_ifelse:
				return executeIf((Stmt.IfElse) stmt, frame, heap, scope);
			case STMT_initialiser:
			case STMT_initialiservoid:
				return executeInitialiser((Stmt.Initialiser) stmt, frame, heap, scope);
			case EXPR_indirectinvoke:
				executeIndirectInvoke((Expr.IndirectInvoke) stmt, frame, heap);
				return Status.NEXT;
			case EXPR_invoke:
				executeInvoke((Expr.Invoke) stmt, frame, heap);
				return Status.NEXT;
			case STMT_namedblock:
				return executeNamedBlock((Stmt.NamedBlock) stmt, frame, heap, scope);
			case STMT_while:
				return executeWhile((Stmt.While) stmt, frame, heap, scope);
			case STMT_return:
			case STMT_returnvoid:
				return executeReturn((Stmt.Return) stmt, frame, heap, scope);
			case STMT_skip:
				return executeSkip((Stmt.Skip) stmt, frame, heap, scope);
			case STMT_switch:
				return executeSwitch((Stmt.Switch) stmt, frame, heap, scope);
			case DECL_variable:
				return executeVariableDeclaration((Decl.Variable) stmt, frame, heap);
			}
			deadCode(stmt);
			return null; // deadcode
		} catch (StackOverflowError e) {
			throw new RuntimeError(WyilFile.RUNTIME_FAULT, frame, stmt);
		}
	}

	private Status executeAssign(Stmt.Assign stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		Tuple<WyilFile.LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		RValue[] rvals = executeExpressions(stmt.getRightHandSide(), frame, heap);
		// Execute the assignment
		for (int i = 0; i != rhs.size(); ++i) {
			Expr r = rhs.get(i);
			// Determine width of expression
			executeAssignLVal(lhs.get(i), rvals[i], frame, heap, scope, r);
		}
		// Check type invariants. This has to happen after every variable has been
		// assigned as type invariants are allowed to be temporarily broken within
		// multiple assignments.
		for (int i = 0; i != lhs.size(); ++i) {
			Expr r = rhs.get(i);
			checkAssignedLVal(lhs.get(i), frame, heap, scope, r);
		}
		return Status.NEXT;
	}

	private void executeAssignLVal(LVal lval, RValue rval, CallStack frame, Heap heap, EnclosingScope scope,
			Syntactic.Item context) {
		switch (lval.getOpcode()) {
		case EXPR_arrayborrow:
		case EXPR_arrayaccess: {
			executeAssignArray((Expr.ArrayAccess) lval, rval, frame, heap, scope, context);
			break;
		}
		case EXPR_dereference: {
			executeAssignDereference((Expr.Dereference) lval, rval, frame, heap, scope, context);
			break;
		}
		case EXPR_fielddereference: {
			executeAssignFieldDereference((Expr.FieldDereference) lval, rval, frame, heap, scope, context);
			break;
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			executeAssignRecord((Expr.RecordAccess) lval, rval, frame, heap, scope, context);
			break;
		}
		case EXPR_staticvariable: {
			executeStaticAssignVariable((Expr.StaticVariableAccess) lval, rval, frame, heap, scope, context);
			break;
		}
		case EXPR_tupleinitialiser: {
			executeAssignTuple((Expr.TupleInitialiser) lval, rval, frame, heap, scope, context);
			break;
		}
		case EXPR_variablemove:
		case EXPR_variablecopy: {
			executeAssignVariable((Expr.VariableAccess) lval, rval, frame, heap, scope, context);
			break;
		}
		default:
			deadCode(lval);
		}
	}

	private void executeAssignArray(Expr.ArrayAccess lval, RValue rval, CallStack frame, Heap heap,
			EnclosingScope scope, Syntactic.Item context) {
		RValue.Array array = executeExpression(ARRAY_T, lval.getFirstOperand(), frame, heap);
		RValue.Int index = executeExpression(INT_T, lval.getSecondOperand(), frame, heap);
		// Sanity check access
		checkArrayBounds(array, index, frame, lval.getSecondOperand());
		// Update the array
		array = array.write(index, rval);
		// Write the results back
		executeAssignLVal((LVal) lval.getFirstOperand(), array, frame, heap, scope, context);
	}

	private void executeAssignDereference(Expr.Dereference lval, RValue rval, CallStack frame, Heap heap,
			EnclosingScope scope, Syntactic.Item context) {
		RValue.Reference ref = executeExpression(REF_T, lval.getOperand(), frame, heap);
		// FIXME: need to check type invariant here??
		int address = ref.deref();
		// Write to heap
		heap.write(address, rval);
	}

	private void executeAssignFieldDereference(Expr.FieldDereference lval, RValue rval, CallStack frame, Heap heap,
			EnclosingScope scope, Syntactic.Item context) {
		RValue.Reference ref = executeExpression(REF_T, lval.getOperand(), frame, heap);
		// Extract target cell
		int address = ref.deref();
		// FIXME: need to check type invariant here??
		RValue.Record record = checkType(heap.read(address), lval.getOperand(), RECORD_T);
		// Update target
		heap.write(address, record.write(lval.getField(), rval));
	}

	private void executeAssignRecord(Expr.RecordAccess lval, RValue rval, CallStack frame, Heap heap,
			EnclosingScope scope, Syntactic.Item context) {
		RValue.Record record = executeExpression(RECORD_T, lval.getOperand(), frame, heap);
		// Write rval to field
		record = record.write(lval.getField(), rval);
		//
		executeAssignLVal((LVal) lval.getOperand(), record, frame, heap, scope, context);
	}

	private void executeAssignVariable(Expr.VariableAccess lval, RValue rval, CallStack frame, Heap heap,
			EnclosingScope scope, Syntactic.Item context) {
		frame.putLocal(lval.getVariableDeclaration().getName(), rval);
	}

	private void executeStaticAssignVariable(Expr.StaticVariableAccess lval, RValue rval, CallStack frame, Heap heap,
			EnclosingScope scope, Syntactic.Item context) {
		Decl.StaticVariable decl = lval.getLink().getTarget();
		//
		heap.write(decl.getQualifiedName(), rval);
	}

	private void executeAssignTuple(Expr.TupleInitialiser lval, RValue rval, CallStack frame, Heap heap,
			EnclosingScope scope, Syntactic.Item context) {
		Tuple<Expr> operands = lval.getOperands();
		// Check we have a tuple as expected!
		RValue.Tuple tuple = checkType(rval, lval, TUPLE_T);
		//
		if (tuple.size() != operands.size()) {
			deadCode(lval);
		}
		//
		for (int i = 0; i != operands.size(); ++i) {
			executeAssignLVal((LVal) operands.get(i), tuple.get(i), frame, heap, scope, context);
		}

	}

	private void checkAssignedLVal(LVal lval, CallStack frame, Heap heap, EnclosingScope scope,
			Syntactic.Item context) {
		switch (lval.getOpcode()) {
		case EXPR_arrayborrow:
		case EXPR_arrayaccess: {
			Expr.ArrayAccess v = (Expr.ArrayAccess) lval;
			checkAssignedLVal((LVal) v.getFirstOperand(), frame, heap, scope, context);
			break;
		}
		case EXPR_dereference: {
			Expr.Dereference v = (Expr.Dereference) lval;
			checkAssignedLVal((LVal) v.getOperand(), frame, heap, scope, context);
			break;
		}
		case EXPR_fielddereference: {
			Expr.FieldDereference v = (Expr.FieldDereference) lval;
			checkAssignedLVal((LVal) v.getOperand(), frame, heap, scope, context);
			break;
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess v = (Expr.RecordAccess) lval;
			checkAssignedLVal((LVal) v.getOperand(), frame, heap, scope, context);
			break;
		}
		case EXPR_staticvariable: {
			Expr.StaticVariableAccess v = (Expr.StaticVariableAccess) lval;
			Decl.StaticVariable d = v.getLink().getTarget();
			checkTypeInvariants(d.getType(), heap.read(d.getQualifiedName()), frame, heap, context);
			break;
		}
		case EXPR_tupleinitialiser: {
			Expr.TupleInitialiser v = (Expr.TupleInitialiser) lval;
			Tuple<Expr> operands = v.getOperands();
			for (int i = 0; i != operands.size(); ++i) {
				checkAssignedLVal((LVal) operands.get(i), frame, heap, scope, context);
			}
			break;
		}
		case EXPR_variablemove:
		case EXPR_variablecopy: {
			Expr.VariableAccess v = (Expr.VariableAccess) lval;
			Decl.Variable d = v.getVariableDeclaration();
			checkTypeInvariants(d.getType(), frame.getLocal(d.getName()), frame, heap, context);
			break;
		}
		default:
			deadCode(lval);
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
	private Status executeAssert(Stmt.Assert stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		//
		checkInvariants(WyilFile.RUNTIME_ASSERTION_FAILURE, frame, heap, stmt.getCondition());
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
	private Status executeAssume(Stmt.Assume stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		//
		checkInvariants(WyilFile.RUNTIME_ASSUMPTION_FAILURE, frame, heap, stmt.getCondition());
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
	private Status executeBreak(Stmt.Break stmt, CallStack frame, Heap heap, EnclosingScope scope) {
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
	private Status executeContinue(Stmt.Continue stmt, CallStack frame, Heap heap, EnclosingScope scope) {
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
	private Status executeDebug(Stmt.Debug stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		//
		RValue.Array arr = executeExpression(ARRAY_T, stmt.getOperand(), frame, heap);
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
	private Status executeDoWhile(Stmt.DoWhile stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		int errcode = WyilFile.RUNTIME_LOOPINVARIANT_ESTABLISH_FAILURE;
		Status r = Status.NEXT;
		while (r == Status.NEXT || r == Status.CONTINUE) {
			r = executeBlock(stmt.getBody(), frame, heap, scope);
			if (r == Status.NEXT) {
				// NOTE: only check loop invariant if normal execution, since breaks are handled
				// differently.
				checkInvariants(errcode, frame, heap, stmt.getInvariant(), null);
				errcode = WyilFile.RUNTIME_LOOPINVARIANT_RESTORED_FAILURE;
				RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame, heap);
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
	private Status executeFail(Stmt.Fail stmt, CallStack frame, Heap heap, EnclosingScope scope) {
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
	private Status executeFor(Stmt.For stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		Decl.StaticVariable var = stmt.getVariable();
		int errcode = WyilFile.RUNTIME_LOOPINVARIANT_ESTABLISH_FAILURE;
		Status r = Status.NEXT;
		// Evaluate index array
		RValue[] indices = executeExpression(ARRAY_T, var.getInitialiser(), frame, heap).getElements();
		// Execute loop
		for (int i = 0; i != indices.length; ++i) {
			// Assign index variable
			frame.putLocal(var.getName(), indices[i]);
			// Check loop invariants
			checkInvariants(errcode, frame, heap, stmt.getInvariant(), null);
			// Keep executing the loop body until we exit it somehow.
			r = executeBlock(stmt.getBody(), frame, heap, scope);
			if (r == Status.BREAK || r == Status.RETURN) {
				break;
			} else if (r == Status.CONTINUE) {
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
	private Status executeIf(Stmt.IfElse stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame, heap);
		if (operand == RValue.True) {
			// branch taken, so execute true branch
			return executeBlock(stmt.getTrueBranch(), frame, heap, scope);
		} else if (stmt.hasFalseBranch()) {
			// branch not taken, so execute false branch
			return executeBlock(stmt.getFalseBranch(), frame, heap, scope);
		} else {
			return Status.NEXT;
		}
	}

	private Status executeInitialiser(Stmt.Initialiser stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		Tuple<Decl.Variable> variables = stmt.getVariables();
		//
		if (!stmt.hasInitialiser()) {
			// Do nothing as no initialiser!
		} else if (variables.size() == 1) {
			// Easy case --- unit assignment
			RValue value = executeExpression(ANY_T, stmt.getInitialiser(), frame, heap);
			// Check type invariants are established
			checkTypeInvariants(stmt.getType(), value, frame, heap, stmt.getInitialiser());
			// Assign variable
			frame.putLocal(variables.get(0).getName(), value);
		} else {
			// Construct lhs type
			Type type = Type.Tuple.create(variables.map(v -> v.getType()));
			// Harder case --- tuple assignment
			RValue.Tuple value = executeExpression(TUPLE_T, stmt.getInitialiser(), frame, heap);
			// Check type invariants are established
			checkTypeInvariants(type, value, frame, heap, stmt.getInitialiser());
			// Assign individual components
			for (int i = 0; i != variables.size(); ++i) {
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
	private Status executeNamedBlock(Stmt.NamedBlock stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		return executeBlock(stmt.getBlock(), frame, heap, scope);
	}

	/**
	 * Execute a While statement at a given point in the function or method body.
	 * This will loop over the body zero or more times.
	 *
	 * @param stmt  --- Loop statement to executed
	 * @param frame --- The current stack frame
	 * @return
	 */
	private Status executeWhile(Stmt.While stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		int errcode = WyilFile.RUNTIME_LOOPINVARIANT_ESTABLISH_FAILURE;
		Status r;
		int count = 0;
		do {
			checkInvariants(errcode, frame, heap, stmt.getInvariant(), null);
			RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame, heap);
			if (operand == RValue.False) {
				return Status.NEXT;
			}
			// Keep executing the loop body until we exit it somehow.
			r = executeBlock(stmt.getBody(), frame, heap, scope);
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
	private Status executeReturn(Stmt.Return stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		// We know that a return statement can only appear in either a function
		// or method declaration. It cannot appear, for example, in a type
		// declaration. Therefore, the enclosing declaration is a function or
		// method.
		CallableScope enclosingScope = scope.getEnclosingScope(CallableScope.class);
		// Extract relevant information
		Decl.Callable context = enclosingScope.getContext();
		Tuple<Decl.Variable> returns = context.getReturns();
		Type.Callable type = context.getType();
		if (stmt.hasReturn()) {
			// Execute return expressions
			RValue value = executeExpression(ANY_T, stmt.getReturn(), frame, heap);
			// Check type invariants
			checkTypeInvariants(type.getReturn(), value, frame, heap, stmt.getReturn());
			//
			if (returns.size() > 1) {
				RValue.Tuple t = (RValue.Tuple) value;
				for (int i = 0; i != returns.size(); ++i) {
					Decl.Variable r = returns.get(i);
					frame.putLocal(r.getName(), t.get(i));
				}
			} else {
				Decl.Variable r = returns.get(0);
				frame.putLocal(r.getName(), value);
			}
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
	private Status executeSkip(Stmt.Skip stmt, CallStack frame, Heap heap, EnclosingScope scope) {
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
	private Status executeSwitch(Stmt.Switch stmt, CallStack frame, Heap heap, EnclosingScope scope) {
		Tuple<Stmt.Case> cases = stmt.getCases();
		//
		Object value = executeExpression(ANY_T, stmt.getCondition(), frame, heap);
		for (int i = 0; i != cases.size(); ++i) {
			Stmt.Case c = cases.get(i);
			Stmt.Block body = c.getBlock();
			if (c.isDefault()) {
				return executeBlock(body, frame, heap, scope);
			} else {
				// FIXME: this is a temporary hack until a proper notion of
				// ConstantExpr is introduced.
				RValue[] values = executeExpressions(c.getConditions(), frame, heap);
				for (RValue v : values) {
					if (v.equals(value)) {
						return executeBlock(body, frame, heap, scope);
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
	private Status executeVariableDeclaration(Decl.Variable stmt, CallStack frame, Heap heap) {
		// We only need to do something if this has an initialiser
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
	public <T extends RValue> T executeExpression(Class<T> expected, Expr expr, CallStack frame, Heap heap) {
		checkForTimeout(frame);
		//
		RValue val;
		switch (expr.getOpcode()) {
		case EXPR_constant:
			val = executeConst((Expr.Constant) expr, frame, heap);
			break;
		case EXPR_cast:
			val = executeConvert((Expr.Cast) expr, frame, heap);
			break;
		case EXPR_recordinitialiser:
			val = executeRecordInitialiser((Expr.RecordInitialiser) expr, frame, heap);
			break;
		case EXPR_recordaccess:
		case EXPR_recordborrow:
			val = executeRecordAccess((Expr.RecordAccess) expr, frame, heap);
			break;
		case EXPR_recordupdate:
			val = executeRecordUpdate((Expr.RecordUpdate) expr, frame, heap);
			break;
		case EXPR_indirectinvoke:
			val = executeIndirectInvoke((Expr.IndirectInvoke) expr, frame, heap);
			break;
		case EXPR_invoke:
			val = executeInvoke((Expr.Invoke) expr, frame, heap);
			break;
		case EXPR_variablemove:
		case EXPR_variablecopy:
			val = executeVariableAccess((Expr.VariableAccess) expr, frame, heap);
			break;
		case EXPR_staticvariable:
			val = executeStaticVariableAccess((Expr.StaticVariableAccess) expr, frame, heap);
			break;
		case EXPR_is:
			val = executeIs((Expr.Is) expr, frame, heap);
			break;
		case EXPR_logicalnot:
			val = executeLogicalNot((Expr.LogicalNot) expr, frame, heap);
			break;
		case EXPR_logicaland:
			val = executeLogicalAnd((Expr.LogicalAnd) expr, frame, heap);
			break;
		case EXPR_logicalor:
			val = executeLogicalOr((Expr.LogicalOr) expr, frame, heap);
			break;
		case EXPR_logicalimplication:
			val = executeLogicalImplication((Expr.LogicalImplication) expr, frame, heap);
			break;
		case EXPR_logicaliff:
			val = executeLogicalIff((Expr.LogicalIff) expr, frame, heap);
			break;
		case EXPR_logicalexistential:
		case EXPR_logicaluniversal:
			val = executeQuantifier((Expr.Quantifier) expr, frame, heap);
			break;
		case EXPR_equal:
			val = executeEqual((Expr.Equal) expr, frame, heap);
			break;
		case EXPR_notequal:
			val = executeNotEqual((Expr.NotEqual) expr, frame, heap);
			break;
		case EXPR_integernegation:
			val = executeIntegerNegation((Expr.IntegerNegation) expr, frame, heap);
			break;
		case EXPR_integeraddition:
			val = executeIntegerAddition((Expr.IntegerAddition) expr, frame, heap);
			break;
		case EXPR_integersubtraction:
			val = executeIntegerSubtraction((Expr.IntegerSubtraction) expr, frame, heap);
			break;
		case EXPR_integermultiplication:
			val = executeIntegerMultiplication((Expr.IntegerMultiplication) expr, frame, heap);
			break;
		case EXPR_integerdivision:
			val = executeIntegerDivision((Expr.IntegerDivision) expr, frame, heap);
			break;
		case EXPR_integerremainder:
			val = executeIntegerRemainder((Expr.IntegerRemainder) expr, frame, heap);
			break;
		case EXPR_integerexponent:
			val = executeIntegerExponent((Expr.IntegerExponent) expr, frame, heap);
			break;
		case EXPR_integerlessthan:
			val = executeIntegerLessThan((Expr.IntegerLessThan) expr, frame, heap);
			break;
		case EXPR_integerlessequal:
			val = executeIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr, frame, heap);
			break;
		case EXPR_integergreaterthan:
			val = executeIntegerGreaterThan((Expr.IntegerGreaterThan) expr, frame, heap);
			break;
		case EXPR_integergreaterequal:
			val = executeIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr, frame, heap);
			break;
		case EXPR_bitwisenot:
			val = executeBitwiseNot((Expr.BitwiseComplement) expr, frame, heap);
			break;
		case EXPR_bitwiseor:
			val = executeBitwiseOr((Expr.BitwiseOr) expr, frame, heap);
			break;
		case EXPR_bitwisexor:
			val = executeBitwiseXor((Expr.BitwiseXor) expr, frame, heap);
			break;
		case EXPR_bitwiseand:
			val = executeBitwiseAnd((Expr.BitwiseAnd) expr, frame, heap);
			break;
		case EXPR_bitwiseshl:
			val = executeBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr, frame, heap);
			break;
		case EXPR_bitwiseshr:
			val = executeBitwiseShiftRight((Expr.BitwiseShiftRight) expr, frame, heap);
			break;
		case EXPR_arrayborrow:
		case EXPR_arrayaccess:
			val = executeArrayAccess((Expr.ArrayAccess) expr, frame, heap);
			break;
		case EXPR_arraygenerator:
			val = executeArrayGenerator((Expr.ArrayGenerator) expr, frame, heap);
			break;
		case EXPR_arraylength:
			val = executeArrayLength((Expr.ArrayLength) expr, frame, heap);
			break;
		case EXPR_arrayinitialiser:
			val = executeArrayInitialiser((Expr.ArrayInitialiser) expr, frame, heap);
			break;
		case EXPR_arrayrange:
			val = executeArrayRange((Expr.ArrayRange) expr, frame, heap);
			break;
		case EXPR_arrayupdate:
			val = executeArrayUpdate((Expr.ArrayUpdate) expr, frame, heap);
			break;
		case EXPR_new:
			val = executeNew((Expr.New) expr, frame, heap);
			break;
		case EXPR_old:
			val = executeOld((Expr.Old) expr, frame, heap);
			break;
		case EXPR_dereference:
			val = executeDereference((Expr.Dereference) expr, frame, heap);
			break;
		case EXPR_fielddereference:
			val = executeFieldDereference((Expr.FieldDereference) expr, frame, heap);
			break;
		case EXPR_lambdaaccess:
			val = executeLambdaAccess((Expr.LambdaAccess) expr, frame, heap);
			break;
		case DECL_lambda:
			val = executeLambdaDeclaration((Decl.Lambda) expr, frame, heap);
			break;
		case EXPR_tupleinitialiser:
			val = executeTupleInitialiser((Expr.TupleInitialiser) expr, frame, heap);
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
	private RValue executeConst(Expr.Constant expr, CallStack frame, Heap heap) {
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
	private RValue executeConvert(Expr.Cast expr, CallStack frame, Heap heap) {
		RValue operand = executeExpression(ANY_T, expr.getOperand(), frame, heap);
		return operand.convert(expr.getType());
	}

	private RValue executeRecordAccess(Expr.RecordAccess expr, CallStack frame, Heap heap) {
		RValue.Record rec = executeExpression(RECORD_T, expr.getOperand(), frame, heap);
		return rec.read(expr.getField());
	}

	private RValue executeRecordInitialiser(Expr.RecordInitialiser expr, CallStack frame, Heap heap) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		RValue.Field[] values = new RValue.Field[operands.size()];
		for (int i = 0; i != operands.size(); ++i) {
			Identifier field = fields.get(i);
			Expr operand = operands.get(i);
			RValue value = executeExpression(ANY_T, operand, frame, heap);
			values[i] = semantics.Field(field, value);
		}
		return semantics.Record(values);
	}

	public RValue executeRecordUpdate(Expr.RecordUpdate expr, CallStack frame, Heap heap) {
		RValue.Record rec = executeExpression(RECORD_T, expr.getFirstOperand(), frame, heap);
		// Evaluate new element
		RValue value = executeExpression(ANY_T, expr.getSecondOperand(), frame, heap);
		// Update the array
		return rec.write(expr.getField(), value);
	}

	private RValue executeTupleInitialiser(Expr.TupleInitialiser expr, CallStack frame, Heap heap) {
		Tuple<Expr> operands = expr.getOperands();
		RValue[] values = new RValue[operands.size()];
		for (int i = 0; i != values.length; ++i) {
			values[i] = executeExpression(ANY_T, operands.get(i), frame, heap);
		}
		return semantics.Tuple(values);
	}

	private RValue executeQuantifier(Expr.Quantifier expr, CallStack frame, Heap heap) {
		boolean r = executeQuantifier(0, expr, frame, heap);
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
	private boolean executeQuantifier(int index, Expr.Quantifier expr, CallStack frame, Heap heap) {
		Tuple<Decl.StaticVariable> vars = expr.getParameters();
		if (index == vars.size()) {
			// This is the base case where we evaluate the condition itself.
			RValue.Bool r = executeExpression(BOOL_T, expr.getOperand(), frame, heap);
			boolean q = (expr instanceof Expr.UniversalQuantifier);
			// If this evaluates to true, then we will continue executing the
			// quantifier.
			return r.boolValue() == q;
		} else {
			Decl.StaticVariable var = vars.get(index);
			RValue.Array range = executeExpression(ARRAY_T, var.getInitialiser(), frame, heap);
			RValue[] elements = range.getElements();
			for (int i = 0; i != elements.length; ++i) {
				frame.putLocal(var.getName(), elements[i]);
				boolean r = executeQuantifier(index + 1, expr, frame, heap);
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
	private RValue executeVariableAccess(Expr.VariableAccess expr, CallStack frame, Heap heap) {
		Decl.Variable decl = expr.getVariableDeclaration();
		return frame.getLocal(decl.getName());
	}

	private RValue executeStaticVariableAccess(Expr.StaticVariableAccess expr, CallStack frame, Heap heap) {
		Decl.StaticVariable decl = expr.getLink().getTarget();
		RValue v = heap.read(decl.getQualifiedName());
		if (v == null) {
			// NOTE: it's possible to get here without the static variable having been
			// initialised in the special case that we have just loaded a module.
			frame = frame.enter(decl);
			v = executeExpression(ANY_T, decl.getInitialiser(), frame, heap);
			heap.write(decl.getQualifiedName(), v);
		}
		return v;
	}

	private RValue executeIs(Expr.Is expr, CallStack frame, Heap heap) {
		RValue lhs = executeExpression(ANY_T, expr.getOperand(), frame, heap);
		return lhs.is(expr.getTestType(), frame, heap);
	}

	public RValue executeIntegerNegation(Expr.IntegerNegation expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getOperand(), frame, heap);
		return lhs.negate();
	}

	public RValue executeIntegerAddition(Expr.IntegerAddition expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		return lhs.add(rhs);
	}

	public RValue executeIntegerSubtraction(Expr.IntegerSubtraction expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		return lhs.subtract(rhs);
	}

	public RValue executeIntegerMultiplication(Expr.IntegerMultiplication expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		return lhs.multiply(rhs);
	}

	public RValue executeIntegerDivision(Expr.IntegerDivision expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		checkDivisionByZero(rhs, frame, expr.getSecondOperand());
		return lhs.divide(rhs);
	}

	public RValue executeIntegerRemainder(Expr.IntegerRemainder expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		checkDivisionByZero(rhs, frame, expr.getSecondOperand());
		return lhs.remainder(rhs);
	}

	public RValue executeIntegerExponent(Expr.IntegerExponent expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		checkNonNegative(rhs, frame, expr.getSecondOperand());
		return lhs.pow(rhs);
	}

	public RValue executeEqual(Expr.Equal expr, CallStack frame, Heap heap) {
		RValue lhs = executeExpression(ANY_T, expr.getFirstOperand(), frame, heap);
		RValue rhs = executeExpression(ANY_T, expr.getSecondOperand(), frame, heap);
		return lhs.equal(rhs);
	}

	public RValue executeNotEqual(Expr.NotEqual expr, CallStack frame, Heap heap) {
		RValue lhs = executeExpression(ANY_T, expr.getFirstOperand(), frame, heap);
		RValue rhs = executeExpression(ANY_T, expr.getSecondOperand(), frame, heap);
		return lhs.notEqual(rhs);
	}

	public RValue executeIntegerLessThan(Expr.IntegerLessThan expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		return lhs.lessThan(rhs);
	}

	public RValue executeIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		return lhs.lessThanOrEqual(rhs);
	}

	public RValue executeIntegerGreaterThan(Expr.IntegerGreaterThan expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		return rhs.lessThan(lhs);
	}

	public RValue executeIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, CallStack frame, Heap heap) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		return rhs.lessThanOrEqual(lhs);
	}

	public RValue executeLogicalNot(Expr.LogicalNot expr, CallStack frame, Heap heap) {
		RValue.Bool lhs = executeExpression(BOOL_T, expr.getOperand(), frame, heap);
		return lhs.not();
	}

	public RValue executeLogicalAnd(Expr.LogicalAnd expr, CallStack frame, Heap heap) {
		// This is a short-circuiting operator. Therefore, we fail as soon as one
		// argument fails.
		Tuple<Expr> operands = expr.getOperands();
		for (int i = 0; i != operands.size(); ++i) {
			RValue.Bool b = executeExpression(BOOL_T, operands.get(i), frame, heap);
			if (b == RValue.False) {
				return b;
			}
		}
		return RValue.True;
	}

	public RValue executeLogicalOr(Expr.LogicalOr expr, CallStack frame, Heap heap) {
		// This is a short-circuiting operator. Therefore, we succeed as soon as one
		// argument succeeds.
		Tuple<Expr> operands = expr.getOperands();
		for (int i = 0; i != operands.size(); ++i) {
			RValue.Bool b = executeExpression(BOOL_T, operands.get(i), frame, heap);
			if (b == RValue.True) {
				return b;
			}
		}
		return RValue.False;
	}

	public RValue executeLogicalImplication(Expr.LogicalImplication expr, CallStack frame, Heap heap) {
		RValue.Bool lhs = executeExpression(BOOL_T, expr.getFirstOperand(), frame, heap);
		if (lhs == RValue.False) {
			return RValue.True;
		} else {
			RValue.Bool rhs = executeExpression(BOOL_T, expr.getSecondOperand(), frame, heap);
			return lhs.equal(rhs);
		}
	}

	public RValue executeLogicalIff(Expr.LogicalIff expr, CallStack frame, Heap heap) {
		RValue.Bool lhs = executeExpression(BOOL_T, expr.getFirstOperand(), frame, heap);
		RValue.Bool rhs = executeExpression(BOOL_T, expr.getSecondOperand(), frame, heap);
		return lhs.equal(rhs);
	}

	public RValue executeBitwiseNot(Expr.BitwiseComplement expr, CallStack frame, Heap heap) {
		RValue.Byte lhs = executeExpression(BYTE_T, expr.getOperand(), frame, heap);
		return lhs.invert();
	}

	public RValue executeBitwiseAnd(Expr.BitwiseAnd expr, CallStack frame, Heap heap) {
		Tuple<Expr> operands = expr.getOperands();
		RValue.Byte val = executeExpression(BYTE_T, operands.get(0), frame, heap);
		for (int i = 1; i != operands.size(); ++i) {
			val = val.and(executeExpression(BYTE_T, operands.get(i), frame, heap));
		}
		return val;
	}

	public RValue executeBitwiseOr(Expr.BitwiseOr expr, CallStack frame, Heap heap) {
		Tuple<Expr> operands = expr.getOperands();
		RValue.Byte val = executeExpression(BYTE_T, operands.get(0), frame, heap);
		for (int i = 1; i != operands.size(); ++i) {
			val = val.or(executeExpression(BYTE_T, operands.get(i), frame, heap));
		}
		return val;
	}

	public RValue executeBitwiseXor(Expr.BitwiseXor expr, CallStack frame, Heap heap) {
		Tuple<Expr> operands = expr.getOperands();
		RValue.Byte val = executeExpression(BYTE_T, operands.get(0), frame, heap);
		for (int i = 1; i != operands.size(); ++i) {
			val = val.xor(executeExpression(BYTE_T, operands.get(i), frame, heap));
		}
		return val;
	}

	public RValue executeBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, CallStack frame, Heap heap) {
		RValue.Byte lhs = executeExpression(BYTE_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		return lhs.shl(rhs);
	}

	public RValue executeBitwiseShiftRight(Expr.BitwiseShiftRight expr, CallStack frame, Heap heap) {
		RValue.Byte lhs = executeExpression(BYTE_T, expr.getFirstOperand(), frame, heap);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		return lhs.shr(rhs);
	}

	public RValue executeArrayLength(Expr.ArrayLength expr, CallStack frame, Heap heap) {
		RValue.Array array = executeExpression(ARRAY_T, expr.getOperand(), frame, heap);
		return array.length();
	}

	public RValue executeArrayAccess(Expr.ArrayAccess expr, CallStack frame, Heap heap) {
		RValue.Array array = executeExpression(ARRAY_T, expr.getFirstOperand(), frame, heap);
		RValue.Int index = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		// Sanity check access
		checkArrayBounds(array, index, frame, expr.getSecondOperand());
		// Perform the read
		return array.read(index);
	}

	public RValue executeArrayGenerator(Expr.ArrayGenerator expr, CallStack frame, Heap heap) {
		RValue element = executeExpression(ANY_T, expr.getFirstOperand(), frame, heap);
		RValue.Int count = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
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

	public RValue executeArrayInitialiser(Expr.ArrayInitialiser expr, CallStack frame, Heap heap) {
		Tuple<Expr> operands = expr.getOperands();
		RValue[] elements = new RValue[operands.size()];
		for (int i = 0; i != elements.length; ++i) {
			elements[i] = executeExpression(ANY_T, operands.get(i), frame, heap);
		}
		return semantics.Array(elements);
	}

	public RValue executeArrayRange(Expr.ArrayRange expr, CallStack frame, Heap heap) {
		int start = executeExpression(INT_T, expr.getFirstOperand(), frame, heap).intValue();
		int end = executeExpression(INT_T, expr.getSecondOperand(), frame, heap).intValue();
		if (end < start) {
			throw new RuntimeError(WyilFile.RUNTIME_NEGATIVE_RANGE_FAILURE, frame, expr);
		}
		RValue[] elements = new RValue[end - start];
		for (int i = start; i < end; ++i) {
			elements[i - start] = semantics.Int(BigInteger.valueOf(i));
		}
		return semantics.Array(elements);
	}

	public RValue executeArrayUpdate(Expr.ArrayUpdate expr, CallStack frame, Heap heap) {
		RValue.Array array = executeExpression(ARRAY_T, expr.getFirstOperand(), frame, heap);
		RValue.Int index = executeExpression(INT_T, expr.getSecondOperand(), frame, heap);
		// Sanity check access
		checkArrayBounds(array, index, frame, expr.getSecondOperand());
		// Evaluate new element
		RValue value = executeExpression(ANY_T, expr.getThirdOperand(), frame, heap);
		// Update the array
		return array.write(index, value);
	}

	public RValue executeNew(Expr.New expr, CallStack frame, Heap heap) {
		// Evaluate operand
		RValue initialiser = executeExpression(ANY_T, expr.getOperand(), frame, heap);
		// Allocate operand into heap
		int address = heap.alloc(initialiser);
		// Done
		return semantics.Reference(address);
	}

	public RValue executeOld(Expr.Old expr, CallStack frame, Heap heap) {
		// Extract prestate (if present)
		Heap oldHeap = (Heap) frame.getLocal(OLD);
		//
		if (oldHeap == null) {
			throw new RuntimeException("internal failure --- invalid old heap");
		}
		//
		return executeExpression(ANY_T, expr.getOperand(), frame, oldHeap);
	}

	public RValue executeDereference(Expr.Dereference expr, CallStack frame, Heap heap) {
		// Evaluate operand
		RValue.Reference ref = executeExpression(REF_T, expr.getOperand(), frame, heap);
		//
		int address = ref.deref();
		// Sanity check
		if (address >= 0 && address < heap.size()) {
			return heap.read(address);
		} else {
			throw new RuntimeError(WyilFile.RUNTIME_FAULT, frame, expr.getOperand());
		}
	}

	public RValue executeFieldDereference(Expr.FieldDereference expr, CallStack frame, Heap heap) {
		// Evaluate operand
		RValue.Reference ref = executeExpression(REF_T, expr.getOperand(), frame, heap);
		// Extract target
		RValue val = heap.read(ref.deref());
		// Extract record value
		RValue.Record rec = checkType(val, expr, RECORD_T);
		// Read the given field
		return rec.read(expr.getField());
	}

	public RValue executeLambdaAccess(Expr.LambdaAccess expr, CallStack frame, Heap heap) {
		// Locate the function or method body in order to execute it
		Decl.Callable decl = expr.getLink().getTarget();
		//
		if (decl instanceof Decl.FunctionOrMethod) {
			Decl.FunctionOrMethod fm = (Decl.FunctionOrMethod) decl;
			// Clone frame to ensure it executes in this exact environment.
			return semantics.Lambda(decl, frame.clone());
		} else {
			throw new Syntactic.Exception("cannot take address of property", null, expr);
		}
	}

	private RValue executeLambdaDeclaration(Decl.Lambda decl, CallStack frame, Heap heap) {
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
	private RValue[] executeExpressions(Tuple<Expr> expressions, CallStack frame, Heap heap) {
		RValue[] results = new RValue[expressions.size()];
		for (int i = 0; i != expressions.size(); ++i) {
			results[i] = executeExpression(ANY_T, expressions.get(i), frame, heap);
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
	private RValue executeIndirectInvoke(Expr.IndirectInvoke expr, CallStack frame, Heap heap) {
		RValue.Lambda src = executeExpression(LAMBDA_T, expr.getSource(), frame, heap);
		RValue[] arguments = executeExpressions(expr.getArguments(), frame, heap);
		// Extract concrete type
		Type.Callable type = src.getType();
		// Check parameter type invariants
		checkTypeInvariants(type.getParameter(), arguments, expr.getArguments(), frame, heap);
		// Here we supply the enclosing frame when the lambda was created.
		// The reason for this is that the lambda may try to access enclosing
		// variables in the scope it was created.
		return src.execute(this, arguments, heap, expr);
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
	private RValue executeInvoke(Expr.Invoke expr, CallStack frame, Heap heap) {
		// Resolve function or method being invoked to a concrete declaration
		Decl.Callable decl = expr.getLink().getTarget();
		// Evaluate argument expressions
		RValue[] arguments = executeExpressions(expr.getOperands(), frame, heap);
		// Extract the concrete type
		Type.Callable type = expr.getBinding().getConcreteType();
		// Check type invariants
		checkTypeInvariants(type.getParameter(), arguments, expr.getOperands(), frame, heap);
		// Invoke the function or method in question
		// FIXME: could potentially optimise this by calling execute with decl directly.
		// This currently fails for external symbols which are represented as
		// prototypes.
		return execute(decl.getQualifiedName(), decl.getType(), frame, heap, arguments, expr);
	}

	// =============================================================
	// Constants
	// =============================================================

	public void checkArrayBounds(RValue.Array array, RValue.Int index, CallStack frame, Syntactic.Item context) {
		int len = array.length().intValue();
		int idx = index.intValue();
		if (idx < 0) {
			throw new RuntimeError(WyilFile.RUNTIME_BELOWBOUNDS_INDEX_FAILURE, frame, context);
		} else if (idx >= len) {
			throw new RuntimeError(WyilFile.RUNTIME_ABOVEBOUNDS_INDEX_FAILURE, frame, context);
		}
	}

	public void checkDivisionByZero(RValue.Int value, CallStack frame, Syntactic.Item context) {
		if (value.intValue() == 0) {
			throw new RuntimeError(WyilFile.RUNTIME_DIVIDEBYZERO_FAILURE, frame, context);
		}
	}

	public void checkNonNegative(RValue.Int value, CallStack frame, Syntactic.Item context) {
		if (value.intValue() < 0) {
			throw new RuntimeError(WyilFile.RUNTIME_NEGATIVE_EXPONENT_FAILURE, frame, context);
		}
	}

	public void checkTypeInvariants(Type variables, RValue[] values, Tuple<Expr> rvals, CallStack frame, Heap heap) {
		// This is a bit tricky because we have to account for multi-expressions which,
		// as always, are annoying.
		int index = 0;
		for (int i = 0; i != rvals.size(); ++i) {
			Expr rval = rvals.get(i);
			checkTypeInvariants(variables.dimension(index), values[index++], frame, heap, rval);
		}
	}

	public void checkTypeInvariants(Type type, RValue value, CallStack frame, Heap heap, Syntactic.Item context) {
		if (value.is(type, frame, heap).boolValue() == false) {
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
	public void checkInvariants(int code, CallStack frame, Heap heap, Tuple<Expr> invariants, Syntactic.Item context) {
		for (int i = 0; i != invariants.size(); ++i) {
			Expr invariant = invariants.get(i);
			// Execute invariant
			RValue.Bool b = executeExpression(BOOL_T, invariant, frame, heap);
			// Check whether it holds or not
			if (b == RValue.False) {
				if (context == null) {
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
	public void checkPrecondition(int code, CallStack frame, Heap heap, Tuple<Expr> invariants,
			Syntactic.Item context) {
		for (int i = 0; i != invariants.size(); ++i) {
			Expr invariant = invariants.get(i);
			// Execute invariant
			RValue.Bool b = executeExpression(BOOL_T, invariant, frame, heap);
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
	public void checkInvariants(int code, CallStack frame, Heap heap, Expr... invariants) {
		for (int i = 0; i != invariants.length; ++i) {
			Expr invariant = invariants[i];
			// Execute invariant
			RValue.Bool b = executeExpression(BOOL_T, invariant, frame, heap);
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
	public static <T extends RValue> T checkType(RValue operand, Syntactic.Item context, Class<T>... types) {
		// Got through each type in turn checking for a match
		for (int i = 0; i != types.length; ++i) {
			if (types[i].isInstance(operand)) {
				// Matched!
				return (T) operand;
			}
		}
		// No match, therefore throw an error
		if (operand == null) {
			throw new Syntactic.Exception("null operand", null, context);
		} else {
			throw new Syntactic.Exception(
					"operand returned " + operand.getClass().getName() + ", expecting one of " + Arrays.toString(types),
					null, context);
		}
	}

	private void checkForTimeout(CallStack frame) {
		long time = System.currentTimeMillis();
		if (time > frame.getTimeout()) {
			throw new TimeoutException("timeout!");
		}
	}

	/**
	 * This method is provided to properly handled positions which should be dead
	 * code.
	 *
	 * @param context --- Context in which bytecodes are executed
	 */
	private <T> T deadCode(Syntactic.Item element) {
		// FIXME: do more here
		throw new RuntimeException("internal failure --- dead code reached");
	}

	private static final Identifier OLD = new Identifier("old");
	private static final Class<RValue> ANY_T = RValue.class;
	private static final Class<RValue.Bool> BOOL_T = RValue.Bool.class;
	private static final Class<RValue.Byte> BYTE_T = RValue.Byte.class;
	private static final Class<RValue.Int> INT_T = RValue.Int.class;
	private static final Class<RValue.Reference> REF_T = RValue.Reference.class;
	private static final Class<RValue.Array> ARRAY_T = RValue.Array.class;
	private static final Class<RValue.Record> RECORD_T = RValue.Record.class;
	private static final Class<RValue.Lambda> LAMBDA_T = RValue.Lambda.class;
	private static final Class<RValue.Tuple> TUPLE_T = RValue.Tuple.class;

	public final static class RuntimeError extends Syntactic.Exception {
		private final int code;
		private final CallStack frame;

		public RuntimeError(int code, CallStack frame, Syntactic.Item element, Syntactic.Item... context) {
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

		private static Syntactic.Heap extractEntry(Syntactic.Item item) {
			// FIXME: this feels like a hack
			Syntactic.Heap h = item.getHeap();
			if (h instanceof WyilFile) {
				return (h);
			} else {
				return null;
			}
		}

		public Trie getSource() {
			Decl.Unit unit = getElement().getAncestor(Decl.Unit.class);
			String nameStr = unit.getName().toString().replace("::", "/");
			return Trie.fromString(nameStr);
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

	public final static class Heap extends ConcreteSemantics.RValue {
		private final HashMap<QualifiedName, RValue> statics;
		private final ArrayList<RValue> values;

		public Heap() {
			this.values = new ArrayList<>();
			this.statics = new HashMap<>();
		}

		public Heap(Map<QualifiedName, RValue> statics, Collection<RValue> values) {
			this.statics = new HashMap<>(statics);
			this.values = new ArrayList<>(values);
		}

		public int alloc(RValue value) {
			int address = values.size();
			values.add(value);
			return address;
		}

		public int size() {
			return values.size();
		}

		public void dealloc(int address) {
			values.set(address, null);
		}

		public RValue read(int address) {
			return values.get(address);
		}

		public RValue read(QualifiedName name) {
			return statics.get(name);
		}

		public RValue write(int address, RValue val) {
			return values.set(address, val);
		}

		public RValue write(QualifiedName name, RValue val) {
			return statics.put(name, val);
		}

		@Override
		public Heap clone() {
			return new Heap(statics, values);
		}

		@Override
		public Value toValue() {
			throw new UnsupportedOperationException();
		}
	}

	public final class CallStack {
		private CallStack parent;
		private long timeout;
		private final HashMap<Identifier, RValue> locals;
		private final Decl.Named context;

		public CallStack() {
			this.parent = null;
			this.timeout = Long.MAX_VALUE;
			this.locals = new HashMap<>();
			this.context = null;
		}

		private CallStack(CallStack parent, Decl.Named context) {
			this(parent, new HashMap<>(), context);
		}

		private CallStack(CallStack parent, HashMap<Identifier, RValue> locals, Decl.Named context) {
			this.parent = parent;
			this.timeout = parent.timeout;
			this.context = context;
			this.locals = locals;
		}

		public int depth() {
			if (parent == null) {
				return 1;
			} else {
				return parent.depth() + 1;
			}
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

		public Map<Identifier, RValue> getLocals() {
			return locals;
		}

		public CallStack enter(Decl.Named<?> context) {
			CallStack cs = new CallStack(this, context);
			// When a property is called, we cannot tell whether it requires a prestate or
			// not. Therefore, we copy over any existing prestate.
			if (context instanceof Decl.Variant) {
				cs.putLocal(OLD, locals.get(OLD));
			}
			return cs;
		}

		public <T extends RValue> T execute(Class<T> expected, Expr expr, CallStack frame, Heap heap) {
			return Interpreter.this.executeExpression(expected, expr, frame, heap);
		}

		public CallStack setTimeout(long timeout) {
			if (timeout != Long.MAX_VALUE) {
				long start = System.currentTimeMillis();
				this.timeout = start + timeout;
			}
			return this;
		}

		@Override
		public CallStack clone() {
			CallStack frame = new CallStack(this, new HashMap<>(locals), this.context);
			// Reset parent
			frame.parent = this.parent;
			return frame;
		}

		public StackFrame[] toStackFrame() {
			if (context == null) {
				return new StackFrame[0];
			} else {
				Value[] arguments;
				//
				switch (context.getOpcode()) {
				case DECL_function:
				case DECL_method:
				case DECL_property:
				case DECL_variant: {
					Decl.Callable fm = (Decl.Callable) context;
					Tuple<Decl.Variable> parameters = fm.getParameters();
					arguments = new Value[parameters.size()];
					for (int i = 0; i != arguments.length; ++i) {
						Decl.Variable parameter = parameters.get(i);
						// FIXME: why is this needed?
						if (getLocal(parameter.getName()) != null) {
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
				return ArrayUtils.append(new StackFrame(context, new Tuple<>(arguments)), sf);
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
	 * Represents the enclosing scope for a function, method or property
	 * declaration.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class CallableScope extends EnclosingScope {
		/**
		 * The declaration being invoked
		 */
		private final Decl.Callable context;

		public CallableScope(Decl.Callable context) {
			super(null);
			this.context = context;
		}

		public Decl.Callable getContext() {
			return context;
		}
	}
}
