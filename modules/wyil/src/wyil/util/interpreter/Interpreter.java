package wyil.util.interpreter;

import static wyil.util.interpreter.Interpreter.error;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.Policy.Parameters;
import java.util.*;

import wyautl.util.BigRational;
import wybs.lang.Build;
import wycc.lang.NameID;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wyfs.lang.Path;
import wyil.lang.*;
import wyil.util.TypeExpander;
import wyil.util.interpreter.Interpreter.ConstantObject;

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
	 * Responsible for expanding types to determine their underlying type and
	 * constraints.
	 */
	private final TypeExpander expander;
	
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
		this.expander = new TypeExpander(project);
		this.operators = StandardFunctions.standardFunctions;
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
	public Constant[] execute(NameID nid, Type.FunctionOrMethod sig, Constant... args) {
		// First, find the enclosing WyilFile
		try {
			Path.Entry<WyilFile> entry = project.get(nid.module(), WyilFile.ContentType);
			if (entry == null) {
				throw new IllegalArgumentException("no WyIL file found: " + nid.module());
			}
			// Second, find the given function or method
			WyilFile wyilFile = entry.read();
			WyilFile.FunctionOrMethod fm = wyilFile.functionOrMethod(nid.name(), sig);
			if (fm == null) {
				throw new IllegalArgumentException("no function or method found: " + nid + ", " + sig);
			} else if (sig.params().size() != args.length) {
				throw new IllegalArgumentException("incorrect number of arguments: " + nid + ", " + sig);
			}
			// Third, get and check the function or method body
			BytecodeForest forest = fm.code();
			if (fm.body() == null) {
				// FIXME: add support for native functions or methods
				throw new IllegalArgumentException("no function or method body found: " + nid + ", " + sig);
			}
			// Fourth, construct the stack frame for execution
			// FIXME: numLocations currently includes operands
			Constant[] frame = new Constant[forest.numLocations()];
			System.arraycopy(args, 0, frame, 0, sig.params().size());			
			// Setup the executing context
			Context context = new Context(fm,forest,fm.body(),0);
			// Check the precondition
			checkInvariants(frame,context,fm.preconditions());
			// Execute the method or function body
			executeAllWithin(frame, context);
			// Extra the return values
			Constant[] returns = extractReturns(frame,fm.type());
			//
			// Check the postcondition holds
			System.arraycopy(args,0,frame,0,args.length);
			checkInvariants(frame, context, fm.postconditions());
			// 				
			return returns;			
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
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
	private Constant[] extractReturns(Constant[] frame, Type.FunctionOrMethod type) {
		int paramsSize = type.params().size();
		int returnsSize = type.returns().size();
		Constant[] returns = new Constant[returnsSize];
		for (int i = 0, j = paramsSize; i != returnsSize; ++i, ++j) {
			returns[i] = frame[j];
		}
		return returns;
	}
	
	/**
	 * Execute a given block of bytecodes starting from the beginning
	 *
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecode instructions are executed
	 * @return
	 */
	private Status executeAllWithin(Constant[] frame, Context context) {
		while (context.hasNext()) {			
			Status r = execute(context.getStatement(), frame, context);
			// Now, see whether we are continuing or not
			if (r != Status.NEXT) {
				return r;
			}
			// Move context to the next bytecode
			context.nextStatement();
		}
		return Status.NEXT;
	}

	/**
	 * Execute a bytecode statement at a given point in the function
	 * or method body
	 *
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Status execute(Bytecode.Stmt bytecode, Constant[] frame, Context context) {
		switch (bytecode.opcode()) {
		case Bytecode.OPCODE_assert:
		case Bytecode.OPCODE_assume:
			return execute((Bytecode.AssertOrAssume) bytecode, frame, context);
		case Bytecode.OPCODE_assign:
			return execute((Bytecode.Assign) bytecode, frame, context);
		case Bytecode.OPCODE_break:
			return execute((Bytecode.Break) bytecode, frame, context);
		case Bytecode.OPCODE_continue:
			return execute((Bytecode.Continue) bytecode, frame, context);
		case Bytecode.OPCODE_debug:
			return execute((Bytecode.Debug) bytecode, frame, context);
		case Bytecode.OPCODE_dowhile:
			return execute((Bytecode.DoWhile) bytecode, frame, context);
		case Bytecode.OPCODE_fail:
			return execute((Bytecode.Fail) bytecode, frame, context);
		case Bytecode.OPCODE_if:
			return execute((Bytecode.If) bytecode, frame, context);
		case Bytecode.OPCODE_while:
			return execute((Bytecode.While) bytecode, frame, context);
		case Bytecode.OPCODE_return:
			return execute((Bytecode.Return) bytecode, frame, context);
		case Bytecode.OPCODE_switch:
			return execute((Bytecode.Switch) bytecode, frame, context);
		}

		deadCode(context);
		return null; // deadcode
	}


	private Status execute(Bytecode.Assign bytecode, Constant[] frame, Context context) {		
		// FIXME: handle multi-assignments properly
		int[] lhs = bytecode.leftHandSide();		
		Constant[] rhs = executeMulti(bytecode.rightHandSide(),frame,context);
		for(int i=0;i!=lhs.length;++i) {			
			// TODO: this is not a very efficient way of implement assignment.
			// To improve performance, it would help if values were mutable,
			// rather than immutable constants.
			LVal lval = constructLVal(lhs[i],frame,context);
			lval.write(frame, rhs[i]);			
		}
		return Status.NEXT;
	}

	/**
	 * Execute an assert or assume bytecode.
	 * 
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Status execute(Bytecode.AssertOrAssume bytecode, Constant[] frame, Context context) {
		//
		checkInvariants(frame,context,bytecode.operand());
		return Status.NEXT;
	}

	private Status execute(Bytecode.Break bytecode, Constant[] frame, Context context) {
		// TODO: the break bytecode supports a non-nearest exit and eventually
		// this should be supported.
		return Status.BREAK;
	}
	
	private Status execute(Bytecode.Continue bytecode, Constant[] frame, Context context) {
		// TODO: the continue bytecode supports a non-nearest exit and eventually
		// this should be supported.
		return Status.CONTINUE;
	}
	
	/**
	 * Execute a Debug bytecode instruction at a given point in the function or
	 * method body. This will write the provided string out to the debug stream.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Status execute(Bytecode.Debug bytecode, Constant[] frame, Context context) {
		//
		Constant.Array arr = executeSingle(ARRAY_T, bytecode.operand(0), frame, context);
		for (Constant item : arr.values()) {
			BigInteger b = ((Constant.Integer) item).value();
			char c = (char) b.intValue();
			debug.print(c);
		}
		//
		return Status.NEXT;
	}

	private Status execute(Bytecode.DoWhile bytecode, Constant[] frame, Context context) {
		Status r = Status.NEXT;
		while(r == Status.NEXT || r == Status.CONTINUE) {
			r = executeAllWithin(frame, context.subBlockContext(bytecode.body()));
			if(r == Status.NEXT) {
				Constant.Bool operand = executeSingle(BOOL_T, bytecode.operand(0), frame, context);
				if(!operand.value()) { return Status.NEXT; }
			}
		};
		// If we get here, then we have exited the loop body without falling
		// through to the next bytecode.
		if(r == Status.BREAK) {
			return Status.NEXT;
		} else {
			return r;
		}
	}
	
	/**
	 * Execute a fail bytecode instruction at a given point in the function or
	 * method body. This will generate a runtime fault.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Status execute(Bytecode.Fail bytecode, Constant[] frame, Context context) {
		throw new AssertionError("Runtime fault occurred");
	}

	private Status execute(Bytecode.If bytecode, Constant[] frame, Context context) {		
		Constant.Bool operand = executeSingle(BOOL_T, bytecode.operand(0), frame, context);
		if (operand.value()) {
			// branch taken, so execute true branch			
			return executeAllWithin(frame, context.subBlockContext(bytecode.trueBranch()));
		} else if (bytecode.hasFalseBranch()) {
			// branch not taken, so execute false branch			
			return executeAllWithin(frame, context.subBlockContext(bytecode.falseBranch()));
		} else {
			return Status.NEXT;
		}
	}

	private Status execute(Bytecode.While bytecode, Constant[] frame, Context context) {
		Status r;
		do {
			Constant.Bool operand = executeSingle(BOOL_T, bytecode.operand(0), frame, context);
			if(!operand.value()) { return Status.NEXT; }
			// Keep executing the loop body until we exit it somehow.
			r = executeAllWithin(frame, context.subBlockContext(bytecode.body()));
		} while (r == Status.NEXT || r == Status.CONTINUE);
		// If we get here, then we have exited the loop body without falling
		// through to the next bytecode.
		if(r == Status.BREAK) {
			return Status.NEXT;
		} else {
			return r;
		}
	}

	/**
	 * Execute a Return bytecode instruction at a given point in the function or
	 * method body
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Status execute(Bytecode.Return bytecode, Constant[] frame, Context context) {
		int[] operands = bytecode.operands();
		WyilFile.FunctionOrMethod fm = (WyilFile.FunctionOrMethod) context.getEnclosingDeclaration();
		Type.FunctionOrMethod type = fm.type();
		int paramsSize = type.params().size();		
		Constant[] values = executeMulti(operands,frame,context);
		for(int i=0,j=paramsSize;i!=values.length;++i,++j) {
			frame[j] = values[i];
		}
		return Status.RETURN;
	}

	private Status execute(Bytecode.Switch bytecode, Constant[] frame, Context context) {
		//
		Constant value = executeSingle(ANY_T, bytecode.operand(0), frame, context);
		for (Bytecode.Case c : bytecode.cases()) {
			if(c.isDefault()) {
				return executeAllWithin(frame,context.subBlockContext(c.block()));
			} else {
				for (Constant v : c.values()) {
					if (v.equals(value)) {
						return executeAllWithin(frame,context.subBlockContext(c.block()));
					}
				}
			}
		}	
		return Status.NEXT;
	}

	// =============================================================
	// Single expressions
	// =============================================================		
	
	private <T extends Constant> T executeSingle(Class<T> expected, int operand, Constant[] frame, Context context) {
		BytecodeForest.Location loc = (BytecodeForest.Location) context.getLocation(operand);
		Constant val; 
		if (loc instanceof BytecodeForest.Variable) {
			val = frame[operand];
		} else {
			Context.Operand opContext = context.subOperandContext(operand);
			BytecodeForest.Operand o = (BytecodeForest.Operand) loc;
			Bytecode.Expr bytecode = o.value();
			switch (bytecode.opcode()) {
			case Bytecode.OPCODE_const:
				val = executeSingle((Bytecode.Const) bytecode, frame, opContext);
				break;
			case Bytecode.OPCODE_convert:
				val = executeSingle((Bytecode.Convert) bytecode, frame, opContext);
				break;
			case Bytecode.OPCODE_fieldload:
				val = executeSingle((Bytecode.FieldLoad) bytecode, frame, opContext);
				break;
			case Bytecode.OPCODE_indirectinvoke:
				val = executeMulti((Bytecode.IndirectInvoke) bytecode, frame, opContext)[0];
				break;
			case Bytecode.OPCODE_invoke:
				val = executeMulti((Bytecode.Invoke) bytecode, frame, opContext)[0];
				break;
			case Bytecode.OPCODE_lambda:
				val = executeSingle((Bytecode.Lambda) bytecode, frame, opContext);
				break;
			case Bytecode.OPCODE_none:
			case Bytecode.OPCODE_some:
			case Bytecode.OPCODE_all:
				val = executeSingle((Bytecode.Quantifier) bytecode, frame, opContext);
				break;
			default:
				val = executeSingle((Bytecode.Operator) bytecode, frame, opContext);
			}
		}	
		return checkType(val,context,expected);
	}
	

	/**
	 * Execute a Const bytecode instruction at a given point in the function or
	 * method body
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant executeSingle(Bytecode.Const bytecode, Constant[] frame, Context.Operand context) {
		return bytecode.constant();
	}

	private Constant executeSingle(Bytecode.Convert bytecode, Constant[] frame, Context.Operand context) {
		try {
			Constant operand = executeSingle(ANY_T, bytecode.operand(),frame,context);
			Type target = expander.getUnderlyingType(bytecode.type());
			return convert(operand, target, context);
		} catch (IOException e) {
			error(e.getMessage(), context);
			return null;
		} catch (ResolveError e) {
			error(e.getMessage(), context);
			return null;
		}
	}

	/**
	 * Execute a binary operator instruction at a given point in the function or
	 * method body. This will check operands match their expected types.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant executeSingle(Bytecode.Operator bytecode, Constant[] frame, Context.Operand context) {		
		switch(bytecode.opcode()) {
		case Bytecode.OPCODE_logicaland: {
			// This is a short-circuiting operator
			Constant.Bool lhs = executeSingle(BOOL_T,bytecode.operand(0),frame,context);
			if(!lhs.value()) {
				// Short-circuit
				return Constant.False;
			}
			return executeSingle(BOOL_T, bytecode.operand(1),frame,context);
		}
		case Bytecode.OPCODE_logicalor: {
			// This is a short-circuiting operator
			Constant.Bool lhs = executeSingle(BOOL_T,bytecode.operand(0),frame,context);
			if(lhs.value()) {
				// Short-circuit
				return Constant.True;
			}
			return executeSingle(BOOL_T, bytecode.operand(1),frame,context);
		}
		default: {
			// This is the default case where can treat the operator as an
			// external function and just call it with the evaluated operands. 
			int[] operands = bytecode.operands();
			Constant[] values = new Constant[operands.length];
			// Read all operands
			for(int i=0;i!=operands.length;++i) {
				values[i] = executeSingle(ANY_T,operands[i],frame,context);
			}
			// Compute result
			return operators[bytecode.opcode()].apply(values, this, context);
		}
		}		
	}

	
	private Constant executeSingle(Bytecode.FieldLoad bytecode, Constant[] frame, Context.Operand context) {
		Constant.Record rec = executeSingle(RECORD_T,bytecode.operand(),frame,context);
		return rec.values().get(bytecode.fieldName());
	}

	private Constant executeSingle(Bytecode.Quantifier bytecode, Constant[] frame, Context.Operand context) {
		boolean r = executeQuantifier(0,bytecode,frame,context);
		// r ==> continued all the way through
		// ! ==> terminated early
		if (bytecode.opcode() == Bytecode.OPCODE_some) {
			return r ? Constant.False : Constant.True;
		} else {
			return r ? Constant.True : Constant.False;
		}
		
	}
	
	/**
	 * Execute one range of the quantifier, or the body if no ranges remain.
	 * 
	 * @param index
	 * @param bytecode
	 * @param frame
	 * @param context
	 * @return
	 */
	private boolean executeQuantifier(int index, Bytecode.Quantifier bytecode, Constant[] frame, Context.Operand context) {
		Bytecode.Range[] ranges = bytecode.ranges();
		if(index == ranges.length) {
			// This is the base case where we evaluate the condition itself.
			Constant.Bool r = executeSingle(BOOL_T,bytecode.body(),frame,context);
			int opcode = bytecode.opcode();
			if(r.value()) {
				switch(opcode) {
				case Bytecode.OPCODE_none:					
				case Bytecode.OPCODE_some:
					return false;
				}
			} else if(opcode == Bytecode.OPCODE_all){
				return false;
			}
			// This means that, for the given quantifier kind, we have to
			// continue as is. 
			return true;
		} else {
			Bytecode.Range range = ranges[index];
			Constant.Integer start = executeSingle(INT_T,range.startOperand(),frame,context);
			Constant.Integer end = executeSingle(INT_T,range.endOperand(),frame,context);
			int var = range.variable();
			long s = start.value().longValue();
			long e = end.value().longValue();
			for(long i=s;i<e;++i) {
				frame[var] = new Constant.Integer(BigInteger.valueOf(i));
				boolean r = executeQuantifier(index+1,bytecode,frame,context);
				if(!r) {
					// early termination
					return r;
				}
			}
			return true;
		}
	}
	
	private Constant executeSingle(Bytecode.Lambda bytecode, Constant[] frame, Context.Operand context) {
		frame = Arrays.copyOf(frame, frame.length);
		return new ConstantLambda(bytecode, context.forest, frame);
	}

	// =============================================================
	// Multi expressions
	// =============================================================
	
	private Constant[] executeMulti(int[] operands, Constant[] frame, Context context) {
		int numResults = 0;
		for(int i=0;i!=operands.length;++i) {
			numResults += context.getLocation(operands[i]).size();
		}
		Constant[] results = new Constant[numResults];		
		for(int i=0,pos=0;i!=operands.length;++i) {
			Constant[] returns = executeMulti(operands[i],frame,context);
			System.arraycopy(returns, 0, results, pos, returns.length);
			pos += returns.length;
		}
		return results;
	}
	
	private Constant[] executeMulti(int operand, Constant[] frame, Context context) {
		BytecodeForest.Location loc = (BytecodeForest.Location) context.getLocation(operand);
		if (loc instanceof BytecodeForest.Variable) {
			return new Constant[] { frame[operand] };
		} else {
			Context.Operand opContext = context.subOperandContext(operand);
			BytecodeForest.Operand o = (BytecodeForest.Operand) loc;
			Bytecode.Expr bytecode = o.value();
			switch (bytecode.opcode()) {			
			case Bytecode.OPCODE_indirectinvoke:
				return executeMulti((Bytecode.IndirectInvoke) bytecode, frame, opContext);
			case Bytecode.OPCODE_invoke:
				return executeMulti((Bytecode.Invoke) bytecode, frame, opContext);
			case Bytecode.OPCODE_const:				
			case Bytecode.OPCODE_convert:				
			case Bytecode.OPCODE_fieldload:
			case Bytecode.OPCODE_lambda:				
			case Bytecode.OPCODE_none:
			case Bytecode.OPCODE_some:
			case Bytecode.OPCODE_all:								
			default:
				Constant val = executeSingle(ANY_T, operand, frame, opContext);
				return new Constant[] { val };
			}
		}
	}
			
	/**
	 * Execute an IndirectInvoke bytecode instruction at a given point in the
	 * function or method body. This first checks the operand is a function
	 * reference, and then generates a recursive call to execute the given
	 * function. If the function does not exist, or is provided with the wrong
	 * number of arguments, then a runtime fault will occur.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant[] executeMulti(Bytecode.IndirectInvoke bytecode, Constant[] frame, Context.Operand context) {
		
		// FIXME: This is implementation is *ugly* --- can we do better than
		// this? One approach is to register an anonymous function so that we
		// can reuse executeAllWithin in both bases. This is hard to setup
		// though.
		
		Constant operand = executeSingle(ANY_T, bytecode.operand(0),frame,context);
		// Check that we have a function reference
		if(operand instanceof Constant.FunctionOrMethod) {
			Constant.FunctionOrMethod cl = checkType(operand, context, Constant.FunctionOrMethod.class);			
			Constant[] arguments = executeMulti(bytecode.arguments(),frame,context);			
			return execute(cl.name(),cl.type(),arguments);
		} else {
			ConstantLambda cl = checkType(operand, context, ConstantLambda.class);
			// Yes we do; now construct the arguments. This requires merging the
			// constant arguments provided in the lambda itself along with those
			// operands provided for the "holes".
			Context lambdaContext = new Context(context.getEnclosingDeclaration(),cl.forest);
			Constant[] lambdaFrame = Arrays.copyOf(cl.frame, cl.frame.length); 
			int[] parameters = cl.lambda.parameters();			
			Constant[] arguments = executeMulti(bytecode.arguments(),frame,context);
			for(int i=0;i!=parameters.length;++i) {			
				lambdaFrame[parameters[i]] = arguments[i];
			}
			// Make the actual call
			return executeMulti(cl.lambda.body(),lambdaFrame,lambdaContext);
		}
	}

	/**
	 * Execute an Invoke bytecode instruction at a given point in the function
	 * or method body. This generates a recursive call to execute the given
	 * function. If the function does not exist, or is provided with the wrong
	 * number of arguments, then a runtime fault will occur.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant[] executeMulti(Bytecode.Invoke bytecode, Constant[] frame, Context.Operand context) {
		int[] operands = bytecode.operands();
		Constant[] arguments = executeMulti(operands,frame,context); 		
		return execute(bytecode.name(), bytecode.type(), arguments);		
	}

	/**
	 * Convert a given value of one into a value of a different type. For
	 * example, converting a int value into a real value.
	 *
	 * @param from
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type to, Context context) {
		Type type = value.type();
		if (Type.isSubtype(to, type)) {
			// In this case, we don't need to do anything because the value is
			// already of the correct type.
			return value;
		} else if (type instanceof Type.Reference && to instanceof Type.Reference) {
			if (Type.isSubtype(((Type.Reference) to).element(), ((Type.Reference) type).element())) {
				// OK, it's just the lifetime that differs.
				return value;
			}
		} else if (to instanceof Type.Record) {
			return convert(value, (Type.Record) to, context);
		} else if (to instanceof Type.Array) {
			return convert(value, (Type.Array) to, context);
		} else if (to instanceof Type.Union) {
			return convert(value, (Type.Union) to, context);
		} else if (to instanceof Type.FunctionOrMethod) {
			return convert(value, (Type.FunctionOrMethod) to, context);
		}
		deadCode(context);
		return null;
	}

	/**
	 * Convert a value into a record type. The value must be of record type for
	 * this to make sense and must either have the same fields or, in the case
	 * of an open record, have a superset of fields.
	 *
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type.Record to, Context context) {
		checkType(value, context, Constant.Record.class);
		Constant.Record rv = (Constant.Record) value;
		HashSet<String> rv_fields = new HashSet<String>(rv.values().keySet());
		// Check fields in value are subset of those in target type
		if (!rv_fields.containsAll(to.keys())) {
			error("cannot convert between records with differing fields", context);
			return null; // deadcode
		} else {
			HashMap<String, Constant> nValues = new HashMap<String, Constant>();
			for (String field : to.keys()) {
				Constant nValue = convert(rv.values().get(field), to.field(field), context);
				nValues.put(field, nValue);
			}
			return new Constant.Record(nValues);
		}
	}

	/**
	 * Convert a value into a list type. The value must be of list type for this
	 * to make sense.
	 *
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type.Array to, Context context) {
		checkType(value, context, Constant.Array.class);
		Constant.Array lv = (Constant.Array) value;
		ArrayList<Constant> values = new ArrayList<Constant>(lv.values());
		for (int i = 0; i != values.size(); ++i) {
			values.set(i, convert(values.get(i), to.element(), context));
		}
		return new Constant.Array(values);
	}

	/**
	 * Convert a value into a union type. In this case, we must find an
	 * appropriate bound for the type in question. If no such type can be found,
	 * then this is ambiguous or otherwise invalid.
	 *
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type.Union to, Context context) {
		Type type = value.type();
		for (Type bound : to.bounds()) {
			if (Type.isExplicitCoerciveSubtype(bound, type)) {
				return convert(value, bound, context);
			}
		}
		deadCode(context);
		return null;
	}

	/**
	 * Convert a value into a function type. In this case, we actually do
	 * nothing for now.
	 *
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type.FunctionOrMethod to, Context context) {
		return value;
	}
	
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
	private LVal constructLVal(int operand, Constant[] frame, Context context) {
		BytecodeForest.Location loc = context.getLocation(operand);
		if(loc instanceof BytecodeForest.Variable) {
			return new VariableLVal(operand);
		} else {
			Bytecode.Expr lval = ((BytecodeForest.Operand) loc).value();
			if (lval instanceof Bytecode.Operator) {
				Bytecode.Operator op = (Bytecode.Operator) lval;
				switch (op.kind()) {
				case ARRAYINDEX: {
					LVal src = constructLVal(op.operand(0),frame,context);
					Constant.Integer index = executeSingle(INT_T,op.operand(1),frame,context);				
					int i = index.value().intValue();
					return new ArrayLVal(src,i);
				}
				case DEREFERENCE: {
					LVal src = constructLVal(op.operand(0),frame,context);
					return new DereferenceLVal(src);
				}
				}
			} else if(lval instanceof Bytecode.FieldLoad) {
				Bytecode.FieldLoad fl = (Bytecode.FieldLoad) lval;
				LVal src = constructLVal(fl.operand(),frame,context);
				return new RecordLVal(src,fl.fieldName());				
			}
		}
		
		deadCode(context);
		return null; // deadcode
	}
		
	private abstract class LVal {
		abstract public Constant read(Constant[] frame);
		abstract public void write(Constant[] frame,Constant rhs);
	}
	
	private class VariableLVal extends LVal {
		private final int index;
		
		public VariableLVal(int index) {
			this.index = index;
		}
		
		@Override
		public Constant read(Constant[] frame) {
			return frame[index];
		}

		@Override
		public void write(Constant[] frame, Constant rhs) {
			frame[index] = rhs;
		}
		
	}
	
	private class ArrayLVal extends LVal {
		private final LVal src;
		private final int index;
		
		public ArrayLVal(LVal src, int index) {
			this.src = src;
			this.index = index;
		}

		@Override
		public Constant read(Constant[] frame) {
			Constant.Array src = checkType(this.src.read(frame),null,Constant.Array.class);
			return src.values().get(index);		
		}

		@Override
		public void write(Constant[] frame,Constant rhs) {
			Constant.Array arr = checkType(this.src.read(frame),null,Constant.Array.class);			
			ArrayList<Constant> values = new ArrayList<Constant>(arr.values());				
			values.set(index, rhs);
			src.write(frame,new Constant.Array(values));
		}
	}
	
	private class RecordLVal extends LVal {
		private final LVal src;
		private final String field;
		
		public RecordLVal(LVal src, String field) {
			this.src = src;
			this.field = field;
		}
		
		@Override
		public Constant read(Constant[] frame) {
			Constant.Record src = checkType(this.src.read(frame),null,Constant.Record.class);
			return src.values().get(field);
		}
		@Override
		public void write(Constant[] frame, Constant rhs) {
			Constant.Record rec = checkType(this.src.read(frame),null,Constant.Record.class);
			HashMap<String, Constant> values = new HashMap<String, Constant>(rec.values());			
			values.put(field, rhs);
			src.write(frame,new Constant.Record(values));			
		}
	}
	
	private class DereferenceLVal extends LVal {
		private final LVal src;

		public DereferenceLVal(LVal src) {
			this.src = src;
		}
		
		@Override
		public Constant read(Constant[] frame) {
			ConstantObject objecy = checkType(src.read(frame),null,ConstantObject.class);
			return objecy.read();
		}
 		
		@Override
		public void write(Constant[] frame, Constant rhs) {
			ConstantObject object = checkType(src.read(frame),null,ConstantObject.class);
			object.write(rhs);
		}
	}
	
	/**
	 * Determine whether a given value is a member of a given type. In the case
	 * of a nominal type, then we must also check that any invariant(s) for that
	 * type hold true as well.
	 * 
	 * @param value
	 * @param type
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	public boolean isMemberOfType(Constant value, Type type, Context context) {
		if (type instanceof Type.Any) {
			return true;
		} else if (type instanceof Type.Void) {
			return false;
		} else if (type instanceof Type.Null) {
			return value instanceof Constant.Null;
		} else if (type instanceof Type.Bool) {
			return value instanceof Constant.Bool;
		} else if (type instanceof Type.Byte) {
			return value instanceof Constant.Byte;
		} else if (type instanceof Type.Int) {
			return value instanceof Constant.Integer;
		} else if (type instanceof Type.Reference) {
			if (value instanceof ConstantObject) {
				ConstantObject obj = (ConstantObject) value;
				Type.Reference rt = (Type.Reference) type;
				return isMemberOfType(obj.value, rt.element(), context);
			}
			return false;
		} else if (type instanceof Type.Array) {
			if (value instanceof Constant.Array) {
				Constant.Array t = (Constant.Array) value;
				Type element = ((Type.Array) type).element();
				boolean r = true;
				for (Constant val : t.values()) {
					r &= isMemberOfType(val, element, context);
				}
				return r;
			}
			return false;
		} else if (type instanceof Type.Record) {
			if (value instanceof Constant.Record) {
				Type.Record rt = (Type.Record) type;
				Constant.Record t = (Constant.Record) value;
				Set<String> fields = t.values().keySet();
				if (!fields.containsAll(rt.keys()) || (!rt.keys().containsAll(fields) && !rt.isOpen())) {
					// In this case, the set of fields does not match properly
					return false;
				}
				boolean r = true;
				for (String field : fields) {
					r &= isMemberOfType(t.values().get(field), rt.field(field), context);
				}
				return r;
			}
			return false;
		} else if (type instanceof Type.Union) {
			Type.Union t = (Type.Union) type;
			for (Type element : t.bounds()) {
				if (isMemberOfType(value, element, context)) {
					return true;
				}
			}
			return false;
		} else if (type instanceof Type.Negation) {
			Type.Negation t = (Type.Negation) type;
			return !isMemberOfType(value, t.element(), context);
		} else if (type instanceof Type.FunctionOrMethod) {
			if (value instanceof Constant.FunctionOrMethod) {
				Constant.FunctionOrMethod l = (Constant.FunctionOrMethod) value;
				if (Type.isSubtype(type, l.type())) {
					return true;
				}
			}
			return false;
		} else if (type instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.name();
			try {
				Path.Entry<WyilFile> entry = project.get(nid.module(), WyilFile.ContentType);
				if (entry == null) {
					throw new IllegalArgumentException("no WyIL file found: " + nid.module());
				}
				// Second, find the given function or method
				WyilFile wyilFile = entry.read();
				WyilFile.Type td = wyilFile.type(nid.name());
				if (td == null) {
					error("undefined nominal type encountered: " + nid, context);
				} else if (!isMemberOfType(value, td.type(), context)) {
					return false;
				}
				// Check any invariant associated with this type
				BytecodeForest invariant = td.invariant();				
				if(invariant.getRoots().length > 0) {
					Context typeContext = new Context(td,invariant);
					// FIXME: This is not the most efficient as the number of
					// locations is greater than the number of variables.					
					Constant[] frame = new Constant[invariant.numLocations()];
					frame[0] = value;
					checkInvariants(frame,typeContext,invariant.getRoots());
				}
				// Done
				return true;
			} catch (IOException e) {
				error(e.getMessage(), context);
			} catch (Error e) {
				// This signals that a runtime fault occurred in the body of the
				// type invariant.
				return false;
			}
		}

		deadCode(context);
		return false; // deadcode
	}
	
	public void checkInvariants(Constant[] frame, Context context, int... invariants) {
		for(int i=0;i!=invariants.length;++i) {
			Constant.Bool b = executeSingle(BOOL_T, invariants[i], frame, context);
			if(!b.value()) {
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
	public static <T extends Constant> T checkType(Constant operand, Context context, Class<T>... types) {
		// Got through each type in turn checking for a match
		for (int i = 0; i != types.length; ++i) {
			if (types[i].isInstance(operand)) {
				// Matched!
				return (T) operand;
			}
		}
		// No match, therefore through an error
		error("invalid operand", context);
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
	public static Object error(String msg, Context context) {
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
	private Object deadCode(Context context) {
		// FIXME: do more here
		throw new RuntimeException("internal failure --- dead code reached");
	}

	private static final Class<Constant> ANY_T = Constant.class;
	private static final Class<Constant.Bool> BOOL_T = Constant.Bool.class;
	private static final Class<Constant.Integer> INT_T = Constant.Integer.class;
	private static final Class<Constant.Array> ARRAY_T = Constant.Array.class;
	private static final Class<Constant.Record> RECORD_T = Constant.Record.class;
	
	/**
	 * Represents an object allocated on the heap.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class ConstantObject extends Constant {
		private Constant value;

		public ConstantObject(Constant value) {
			this.value = value;
		}

		public Constant read() {			
			return value;
		}

		public void write(Constant newValue) {			
			value = newValue;
		}

		public boolean equals(Object o) {
			return o == this;
		}

		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public int compareTo(Constant o) {
			// This method cannot be implmened because it does not make sense to
			// compare a reference with another reference.
			throw new UnsupportedOperationException("ConstantObject.compare() cannot be implemented");
		}

		@Override
		public wyil.lang.Type type() {
			// TODO: extend wyil.lang.Codes.NewObject with a lifetime and use it
			return wyil.lang.Type.Reference(value.type(), "*");
		}
	}

	/**
	 * Represents an object allocated on the heap.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class ConstantLambda extends Constant {
		private final Bytecode.Lambda lambda;
		private final BytecodeForest forest;
		private final Constant[] frame;		

		public ConstantLambda(Bytecode.Lambda lambda, BytecodeForest forest, Constant... frame) {
			this.lambda = lambda;
			this.forest = forest;
			this.frame = frame;
		}

		public boolean equals(Object o) {
			return o == this;
		}

		public int hashCode() {
			return Arrays.hashCode(frame);
		}

		@Override
		public int compareTo(Constant o) {
			// This method cannot be implmened because it does not make sense to
			// compare a reference with another reference.
			throw new UnsupportedOperationException("ConstantObject.compare() cannot be implemented");
		}

		@Override
		public wyil.lang.Type type() {
			return lambda.type();
		}
	}
	
	public static class Context {
		/**
		 * The enclosing declaration for this translation context 
		 */
		protected final WyilFile.Declaration enclosingDeclaration;
		/**
		 * The encosing bytecode forest
		 */
		protected final BytecodeForest forest;
		/**
		 * The enclosing block being executed
		 */
		private final BytecodeForest.Block block;
		/**
		 * Index of block we are executing with the forest
		 */
		private int blockID;
		/**
		 * Index of bytecode statement within block we are executing
		 */
		private int pc;
		
		public Context(WyilFile.Declaration enclosing, BytecodeForest forest) {
			this.enclosingDeclaration = enclosing;
			this.forest = forest;
			this.block = null;
			this.blockID = -1;
			this.pc = -1;
		}
		
		public Context(WyilFile.Declaration enclosing, BytecodeForest forest, int blockID, int pc) {
			this.enclosingDeclaration = enclosing;
			this.forest = forest;
			this.block = forest.get(blockID);
			this.blockID = blockID;
			this.pc = pc;
		}

		private Context(Context context) {
			this.enclosingDeclaration = context.enclosingDeclaration;
			this.forest = context.forest;
			this.block = context.block;
			this.blockID = context.blockID;
			this.pc = context.pc;
		}
		
		public WyilFile.Declaration getEnclosingDeclaration() {
			return enclosingDeclaration;
		}

		/**
		 * Create a new subcontext for executing a nested block at the given
		 * position.
		 * 
		 * @param blockID
		 * @return
		 */
		public Context subBlockContext(int blockID) {
			return new Context(enclosingDeclaration,forest,blockID,0);
		}
		public Context.Operand subOperandContext(int operand) {
			return new Context.Operand(operand,this);
		}
		
		public BytecodeForest.Index getIndex() {
			return new BytecodeForest.Index(blockID, pc);
		}
		
		public BytecodeForest.Location getLocation(int location) {
			return forest.getLocation(location);
		}
		
		public Bytecode.Stmt getStatement() {
			return block.get(pc).code();
		}
		
		public boolean hasNext() {
			return pc < block.size();
		}
		
		public void nextStatement() {
			pc = pc + 1; 
		}
		
		public static class Operand extends Context {
			/**
			 * Index of operand within the bytecode statement we are executing
			 */
			private final int operand;

			public Operand(int operand, Context context) {
				super(context);
				this.operand = operand;
			}
			
			public BytecodeForest.Operand getOperand() {
				return (BytecodeForest.Operand) forest.getLocation(operand);
			}					
		}
	}
	
	/**
	 * An internal function is simply a named internal function. This reads a
	 * bunch of operands and returns a set of results.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static interface InternalFunction {
		public Constant apply(Constant[] operands, Interpreter enclosing, Context.Operand context);
	}
}
