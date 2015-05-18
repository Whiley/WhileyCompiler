package wyil.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import wyautl.util.BigRational;
import wybs.lang.Build;
import wycc.lang.NameID;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyil.lang.*;
import wyil.lang.CodeBlock.Index;

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
	 * The debug stream provides an I/O stream through which debug bytecodes can
	 * write their messages.
	 */
	private final PrintStream debug;

	public Interpreter(Build.Project project, PrintStream debug) {
		this.project = project;
		this.debug = debug;
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
	public Constant execute(NameID nid, Type.FunctionOrMethod sig, Constant... args) {
		// First, find the enclosing WyilFile
		try {
			Path.Entry<WyilFile> entry = project.get(nid.module(),
					WyilFile.ContentType);
			if (entry == null) {
				throw new IllegalArgumentException("no WyIL file found: "
						+ nid.module());
			}
			// Second, find the given function or method
			WyilFile wyilFile = entry.read();
			WyilFile.FunctionOrMethod fm = wyilFile.functionOrMethod(
					nid.name(), sig);
			if (fm == null) {
				throw new IllegalArgumentException(
						"no function or method found: " + nid + ", " + sig);
			} else if (sig.params().size() != args.length) {
				throw new IllegalArgumentException(
						"incorrect number of arguments: " + nid + ", " + sig);
			}
			// Third, get and check the function or method body
			AttributedCodeBlock body = fm.body();
			if (body == null) {
				// FIXME: add support for native functions or methods
				throw new IllegalArgumentException(
						"no function or method body found: " + nid + ", " + sig);
			}
			// Fourth, construct the stack frame for execution
			Constant[] frame = new Constant[body.numSlots()];
			ArrayList<Type> sig_params = sig.params();
			for (int i = 0; i != sig_params.size(); ++i) {
				frame[i] = args[i];
			}
			// Finally, let's do it!
			Map<String,CodeBlock.Index> labels = CodeUtils.buildLabelMap(body);
			return (Constant) executeAllWithin(null, frame, body, labels);
		} catch(IOException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	/**
	 * Execute a given block of bytecodes starting from the beginning
	 *
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object executeAllWithin(CodeBlock.Index parent, Constant[] frame,
			AttributedCodeBlock block, Map<String, CodeBlock.Index> labels) {
		CodeBlock.Index pc = parent == null ? new CodeBlock.Index(null, 0)
				: parent.firstWithin();

		do {
			Object r = execute(pc, frame, block, labels);
			// Now, see whether we are continuing or not
			if (r instanceof CodeBlock.Index) {
				pc = (CodeBlock.Index) r;
			} else {
				return r;
			}
		} while(pc.isWithin(parent) && block.contains(pc));

		if(!pc.isWithin(parent)) {
			// non-local exit
			return pc;
		} else {
			return null;
		}
	}

	/**
	 * Execute an Assign bytecode instruction at a given point in the function
	 * or method body
	 *
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @param labels
	 *            --- Mapping of labels to their block indices
	 * @return
	 */
	private Object execute(CodeBlock.Index pc, Constant[] frame,
			AttributedCodeBlock block, Map<String,CodeBlock.Index> labels) {
		Code bytecode = block.get(pc);
		// FIXME: turn this into a switch statement?
		if(bytecode instanceof Codes.Invariant) {
			return execute((Codes.Invariant) bytecode, pc,frame,block,labels);
		} else if(bytecode instanceof Codes.AssertOrAssume) {
			return execute((Codes.AssertOrAssume) bytecode, pc,frame,block,labels);
		} else if(bytecode instanceof Codes.Assign) {
			return execute((Codes.Assign) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.BinaryOperator) {
			return execute((Codes.BinaryOperator) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Const) {
			return execute((Codes.Const) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Convert) {
			return execute((Codes.Convert) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Debug) {
			return execute((Codes.Debug) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Dereference) {
			return execute((Codes.Dereference) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Fail) {
			return execute((Codes.Fail) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.FieldLoad) {
			return execute((Codes.FieldLoad) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.ForAll) {
			return execute((Codes.ForAll) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Goto) {
			return execute((Codes.Goto) bytecode, pc,frame,block,labels);
		} else if(bytecode instanceof Codes.If) {
			return execute((Codes.If) bytecode, pc,frame,block,labels);
		} else if(bytecode instanceof Codes.IfIs) {
			return execute((Codes.IfIs) bytecode, pc,frame,block,labels);
		} else if(bytecode instanceof Codes.IndexOf) {
			return execute((Codes.IndexOf) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.IndirectInvoke) {
			return execute((Codes.IndirectInvoke) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Invert) {
			return execute((Codes.Invert) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Invoke) {
			return execute((Codes.Invoke) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Label) {
			// essentially do nout
			return pc.next();
		} else if(bytecode instanceof Codes.Lambda) {
			return execute((Codes.Lambda) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.LengthOf) {
			return execute((Codes.LengthOf) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.ListOperator) {
			return execute((Codes.ListOperator) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Loop) {
			return execute((Codes.Loop) bytecode, pc,frame,block,labels);
		} else if(bytecode instanceof Codes.Move) {
			return execute((Codes.Move) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.NewList) {
			return execute((Codes.NewList) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.NewMap) {
			return execute((Codes.NewMap) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.NewObject) {
			return execute((Codes.NewObject) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.NewRecord) {
			return execute((Codes.NewRecord) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.NewSet) {
			return execute((Codes.NewSet) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.NewTuple) {
			return execute((Codes.NewTuple) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Nop) {
			return execute((Codes.Nop) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Quantify) {
			return execute((Codes.Quantify) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Return) {
			return execute((Codes.Return) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.SetOperator) {
			return execute((Codes.SetOperator) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.SubList) {
			return execute((Codes.SubList) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Switch) {
			return execute((Codes.Switch) bytecode, pc,frame,block,labels);
		} else if(bytecode instanceof Codes.TupleLoad) {
			return execute((Codes.TupleLoad) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.UnaryOperator) {
			return execute((Codes.UnaryOperator) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Update) {
			return execute((Codes.Update) bytecode, pc,frame,block);
		} else if(bytecode instanceof Codes.Void) {
			return execute((Codes.Void) bytecode, pc,frame,block);
		} else {
			throw new IllegalArgumentException("Unknown bytecode encountered: " + bytecode);
		}
	}

	private Object execute(Codes.AssertOrAssume bytecode, Index pc,
			Constant[] frame, AttributedCodeBlock block,
			Map<String, CodeBlock.Index> labels) {
		//
		Object r = executeAllWithin(pc, frame, block, labels);
		//
		if(r == null) {
			// Body of assert fell through to next
			return pc.next();
		} else {
			return r;
		}
	}

	/**
	 * Execute an Assign bytecode instruction at a given point in the function
	 * or method body
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.Assign bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		frame[bytecode.target()] = frame[bytecode.operand(0)];
		return pc.next();
	}

	/**
	 * Execute a binary operator instruction at a given point in the function or
	 * method body.  This will check operands match their expected types.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.BinaryOperator bytecode, Index pc,
			Constant[] frame, AttributedCodeBlock block) {
		Constant op1 = frame[bytecode.operand(0)];
		Constant op2 = frame[bytecode.operand(1)];

		// Check operands have appropriate and identical types
		checkType(op1,pc,block,Constant.Integer.class,Constant.Decimal.class);
		checkType(op2,pc,block,op1.getClass());

		// Compute result
		Constant result;
		if (op1 instanceof Constant.Integer && op2 instanceof Constant.Integer) {
			result = execute(bytecode.kind, (Constant.Integer) op1,
					(Constant.Integer) op2);
		} else if (op1 instanceof Constant.Decimal
				&& op2 instanceof Constant.Decimal) {
			result = execute(bytecode.kind, (Constant.Decimal) op1,
					(Constant.Decimal) op2);
		} else {
			return deadCode(pc,block); // dead because of above checks
		}

		// Write result to target
		frame[bytecode.target()] = result;
		return pc.next();
	}

	/**
	 * Execute an integer binary operator
	 *
	 * @param kind --- operator kind
	 * @param i1 --- left operand
	 * @param i2 --- right operand
	 * @return
	 */
	private Constant execute(Codes.BinaryOperatorKind kind, Constant.Integer i1, Constant.Integer i2) {
		switch(kind) {
		case ADD:
			return i1.add(i2);
		case SUB:
			return i1.subtract(i2);
		case MUL:
			return i1.multiply(i2);
		case DIV:
			return i1.divide(i2);
		case REM:
			return i1.remainder(i2);
		}
		throw new RuntimeException("Invalid binary integer operator");
	}

	/**
	 * Execute a rational binary operator
	 *
	 * @param kind --- operator kind
	 * @param i1 --- left operand
	 * @param i2 --- right operand
	 * @return
	 */
	private Constant execute(Codes.BinaryOperatorKind kind, Constant.Decimal r1,
			Constant.Decimal r2) {
		switch (kind) {
		case ADD:
			return r1.add(r2);
		case SUB:
			return r1.subtract(r2);
		case MUL:
			return r1.multiply(r2);
		case DIV:
			return r1.divide(r2);
		}
		throw new RuntimeException("Invalid binary integer operator");
	}

	/**
	 * Execute a Const bytecode instruction at a given point in the function
	 * or method body
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.Const bytecode, CodeBlock.Index pc,
			Constant[] frame, AttributedCodeBlock block) {
		frame[bytecode.target()] = bytecode.constant;
		return pc.next();
	}

	private Object execute(Codes.Convert bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Implement me!");
	}

	/**
	 * Execute a Debug bytecode instruction at a given point in the function or
	 * method body. This will write the provided string out to the debug stream.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.Debug bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		//
		Constant.List list = (Constant.List) frame[bytecode.operand];
		for(Constant item : list.values) {
			BigInteger b = ((Constant.Integer)item).value;
			char c = (char) b.intValue();
			debug.print(c);
		}
		//
		return pc.next();
	}

	private Object execute(Codes.Dereference bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Implement me!");
	}

	/**
	 * Execute a fail bytecode instruction at a given point in the function or
	 * method body. This will generate a runtime fault.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.Fail bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		return error("Runtime fault occurred",pc,block);
	}

	private Object execute(Codes.FieldLoad bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		Constant.Record rec = (Constant.Record) frame[bytecode.operand(0)];
		frame[bytecode.target()] = rec.values.get(bytecode.field);
		return pc.next();
	}

	private Object execute(Codes.ForAll bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Implement me!");
	}

	private Object execute(Codes.Goto bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block, Map<String, CodeBlock.Index> labels) {
		return labels.get(bytecode.target);
	}

	private Object execute(Codes.If bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block, Map<String, CodeBlock.Index> labels) {
		Constant op1 = frame[bytecode.leftOperand];
		Constant op2 = frame[bytecode.rightOperand];
		boolean result;
		switch(bytecode.op) {
			// Handle cases which apply to all values
		case EQ:
			result = op1.equals(op2);
			break;
		case NEQ:
			result = !op1.equals(op2);
			break;
			// Handle arithmetic cases
		case LT:
			result = lessThan(op1,op2,true, pc, block);
			break;
		case LTEQ:
			result = lessThan(op1,op2,false, pc, block);
			break;
		case GT:
			result = lessThan(op2,op1,true, pc, block);
			break;
		case GTEQ:
			result = lessThan(op2,op1,false, pc, block);
			break;
		default:
		// TODO: handle other cases
			throw new RuntimeException("Implement me!");
		}

		if(result) {
			// branch taken, so jump to destination label
			return labels.get(bytecode.target);
		} else {
			// branch not taken, so fall through to next bytecode.
			return pc.next();
		}
		// TODO Auto-generated method stub

	}

	private boolean lessThan(Constant lhs, Constant rhs, boolean isStrict, Index pc, AttributedCodeBlock block) {
		checkType(lhs,pc,block,Constant.Integer.class,Constant.Decimal.class);
		checkType(rhs,pc,block,lhs.getClass());
		int result;
		if(lhs instanceof Constant.Integer) {
			Constant.Integer lhs_i = (Constant.Integer) lhs;
			Constant.Integer rhs_i = (Constant.Integer) rhs;
			result = lhs_i.compareTo(rhs_i);
		} else {
			Constant.Decimal lhs_i = (Constant.Decimal) lhs;
			Constant.Decimal rhs_i = (Constant.Decimal) rhs;
			result = lhs_i.compareTo(rhs_i);
		}
		// In the strict case, the lhs must be strictly below the rhs. In the
		// non-strict case, they can be equal.
		if(isStrict) {
			return result < 0;
		} else {
			return result <= 0;
		}
	}

	private Object execute(Codes.IfIs bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block, Map<String, CodeBlock.Index> labels) {
		Constant op = frame[bytecode.operand];
		if (Type.isSubtype(bytecode.rightOperand, op.type())) {
			// Yes, the type test holds
			return labels.get(bytecode.target);
		} else {
			// No, it doesn't so fall through to next instruction
			return pc.next();
		}
	}

	/**
	 * Execute an IndexOf bytecode instruction at a given point in the function
	 * or method body. This checks the first operand is a list value, and the
	 * second operand is an integer value. It also checks that the integer index
	 * is within bounds and, if not, raises a runtime fault.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.IndexOf bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		Constant operand_0 = frame[bytecode.operand(0)];
		Constant operand_1 = frame[bytecode.operand(1)];
		// Check we have a list and an integer index
		checkType(operand_0,pc,block,Constant.List.class);
		checkType(operand_1,pc,block,Constant.Integer.class);
		// Yes, now check that this is in bounds
		Constant.List list = (Constant.List) operand_0;
		Constant.Integer index = (Constant.Integer) operand_1;
		int i = index.value.intValue();
		if(i < 0 || i >= list.values.size()) {
			error("index-out-of-bounds",pc,block);
		}
		// Ok, get the element at that index
		frame[bytecode.target()] = list.values.get(index.value.intValue());
		// Done
		return pc.next();
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
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.IndirectInvoke bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		Constant operand = frame[bytecode.operand(0)];
		// Check that we have a function reference
		checkType(operand,pc,block,Constant.Lambda.class);
		// Yes we do; now construct the arguments
		Constant.Lambda func = (Constant.Lambda) operand;
		int[] operands = bytecode.operands();
		Constant[] arguments = new Constant[operands.length-1];
		for (int i = 0; i != arguments.length; ++i) {
			arguments[i] = frame[operands[i-1]];
		}
		// Make the actual call
		Constant result = execute(func.name, func.type(), arguments);
		// Check whether a return value was expected or not
		if (bytecode.target() != Codes.NULL_REG) {
			frame[bytecode.target()] = result;
		}
		// Done
		return pc.next();
	}

	private Object execute(Codes.Invariant bytecode, Index pc,
			Constant[] frame, AttributedCodeBlock block,
			Map<String, CodeBlock.Index> labels) {
		// FIXME: currently implemented as a NOP because of #480
		return pc.next();
	}

	/**
	 * Execute an Invert bytecode instruction at a given point in the function
	 * or method body. This checks the operand is a byte value.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.Invert bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		Constant operand = frame[bytecode.operand(0)];
		// Check that we have a byte operand
		checkType(operand,pc,block,Constant.Byte.class);
		// Write back the inverted value
		Constant.Byte b = (Constant.Byte) operand;
		frame[bytecode.target()] = Constant.V_BYTE((byte)~b.value);
		// Done
		return pc.next();
	}

	/**
	 * Execute an Invoke bytecode instruction at a given point in the function
	 * or method body. This generates a recursive call to execute the given
	 * function. If the function does not exist, or is provided with the wrong
	 * number of arguments, then a runtime fault will occur.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.Invoke bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		int[] operands = bytecode.operands();
		Constant[] arguments = new Constant[operands.length];
		for (int i = 0; i != arguments.length; ++i) {
			arguments[i] = frame[operands[i]];
		}
		Constant result = execute(bytecode.name, bytecode.type(), arguments);
		if (bytecode.target() != Codes.NULL_REG) {
			frame[bytecode.target()] = result;
		}
		return pc.next();
	}

	private Object execute(Codes.Lambda bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		// FIXME: need to do something with the operands here.
		frame[bytecode.target()] = Constant.V_LAMBDA(bytecode.name,
				bytecode.type());
		//
		return pc.next();
	}

	/**
	 * Execute a LengthOf bytecode instruction at a given point in the function
	 * or method body. This simply returns the length of the list at the given
	 * position.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.LengthOf bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		Constant _source = frame[bytecode.operand(0)];
		checkType(_source,pc,block,Constant.List.class,Constant.Map.class,Constant.Set.class);
		BigInteger length;
		if(_source instanceof Constant.List) {
			Constant.List list = (Constant.List) _source;
			length = BigInteger.valueOf(list.values.size());
		} else if(_source instanceof Constant.Map) {
			Constant.Map list = (Constant.Map) _source;
			length = BigInteger.valueOf(list.values.size());
		} else {
			Constant.Set list = (Constant.Set) _source;
			length = BigInteger.valueOf(list.values.size());
		}
		frame[bytecode.target()] = Constant.V_INTEGER(length);
		return pc.next();
	}

	/**
	 * Execute the list append bytecode instruction at a given point in the
	 * function or method body. This simply assigns the appended list to the
	 * target register.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.ListOperator bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		Constant lhs = frame[bytecode.operand(0)];
		Constant rhs = frame[bytecode.operand(1)];
		// Check that we have a function reference
		checkType(lhs,pc,block,Constant.List.class);
		checkType(rhs,pc,block,Constant.List.class);
		// Now, perform the append
		Constant.List l1 = (Constant.List) lhs;
		Constant.List l2 = (Constant.List) rhs;
		ArrayList<Constant> values = new ArrayList<Constant>();
		values.addAll(l1.values);
		values.addAll(l2.values);
		frame[bytecode.target()] = Constant.V_LIST(values);
		return pc.next();
	}

	private Object execute(Codes.Loop bytecode, Index parent, Constant[] frame,
			AttributedCodeBlock block, Map<String, CodeBlock.Index> labels) {
		Object r;
		do {
			// Keep executing the loop body until we exit it somehow.
			r = executeAllWithin(parent, frame, block, labels);
		} while (r == null);

		// If we get here, then we have exited the loop body without falling
		// through to the next bytecode.
		return r;
	}

	/**
	 * Execute a move bytecode instruction at a given point in the function or
	 * method body. This moves the operand value into the target register, and
	 * voids the operand.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.Move bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		frame[bytecode.target()] = frame[bytecode.operand(0)];
		frame[bytecode.operand(0)] = null;
		return pc.next();
	}

	/**
	 * Execute a Record bytecode instruction at a given point in the function or
	 * method body.  This constructs a new list.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.NewList bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		ArrayList<Constant> values = new ArrayList<Constant>();
		for (int operand : bytecode.operands()) {
			values.add((Constant) frame[operand]);
		}
		frame[bytecode.target()] = Constant.V_LIST(values);
		return pc.next();
	}

	/**
	 * Execute a Map constructor bytecode instruction at a given point in the
	 * function or method body. This constructs a new map.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.NewMap bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		HashSet<Pair<Constant, Constant>> values = new HashSet<Pair<Constant, Constant>>();
		int[] operands = bytecode.operands();
		for (int i = 0; i != operands.length; i = i + 2) {
			Constant key = frame[operands[i]];
			Constant value = frame[operands[i + 1]];
			values.add(new Pair<Constant, Constant>(key, value));
		}
		frame[bytecode.target()] = Constant.V_MAP(values);
		return pc.next();
	}

	private Object execute(Codes.NewObject bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Implement me!");
	}

	/**
	 * Execute a Record bytecode instruction at a given point in the function or
	 * method body.  This constructs a new record.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.NewRecord bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		HashMap<String, Constant> values = new HashMap<String, Constant>();
		ArrayList<String> fields = new ArrayList<String>(bytecode.type()
				.fields().keySet());
		Collections.sort(fields);
		int[] operands = bytecode.operands();
		for (int i = 0; i != operands.length; ++i) {
			values.put(fields.get(i), (Constant) frame[operands[i]]);
		}
		frame[bytecode.target()] = Constant.V_RECORD(values);
		return pc.next();
	}

	private Object execute(Codes.NewSet bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		HashSet<Constant> values = new HashSet<Constant>();
		int[] operands = bytecode.operands();
		for (int i = 0; i != operands.length; i = i + 1) {
			values.add(frame[operands[i]]);
		}
		frame[bytecode.target()] = Constant.V_SET(values);
		return pc.next();
	}

	/**
	 * Execute a Tuple bytecode instruction at a given point in the function or
	 * method body.  This constructs a new tuple.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.NewTuple bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		ArrayList<Constant> values = new ArrayList<Constant>();
		for (int operand : bytecode.operands()) {
			values.add((Constant) frame[operand]);
		}
		frame[bytecode.target()] = Constant.V_TUPLE(values);
		return pc.next();
	}

	/**
	 * Execute a Nop bytecode instruction at a given point in the function or
	 * method body.  This basically doesn't do anything!
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.Nop bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		return pc.next();
	}

	/**
	 * Execute a Return bytecode instruction at a given point in the function or
	 * method body
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param pc
	 *            --- The position of the instruction to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param block
	 *            --- The root block of bytecode instructions
	 * @return
	 */
	private Object execute(Codes.Return bytecode, CodeBlock.Index pc,
			Constant[] frame, AttributedCodeBlock block) {
		if (bytecode.operand == Codes.NULL_REG) {
			// This is a void return
			return null;
		} else {
			return frame[bytecode.operand];
		}
	}

	private Object execute(Codes.SetOperator bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Implement me!");
	}

	private Object execute(Codes.SubList bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		Constant _source = frame[bytecode.operand(0)];
		Constant _fromIndex = frame[bytecode.operand(1)];
		Constant _toIndex = frame[bytecode.operand(2)];
		// Check that we have a function reference
		checkType(_source,pc,block,Constant.List.class);
		checkType(_fromIndex,pc,block,Constant.Integer.class);
		checkType(_toIndex,pc,block,Constant.Integer.class);
		// Now, perform the append
		Constant.List source = (Constant.List) _source;
		Constant.Integer fromIndex = (Constant.Integer) _fromIndex;
		Constant.Integer toIndex = (Constant.Integer) _toIndex;

		frame[bytecode.target()] = Constant.V_LIST(source.values.subList(
				fromIndex.value.intValue(), toIndex.value.intValue()));

		return pc.next();
	}

	private Object execute(Codes.Switch bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block, Map<String,CodeBlock.Index> labels) {
		//
		Constant operand = frame[bytecode.operand];
		for(Pair<Constant,String> branch : bytecode.branches) {
			Constant caseOperand = branch.first();
			if(caseOperand == null || caseOperand.equals(operand)) {
				return labels.get(branch.second());
			}
		}
		return pc.next();
	}

	private Object execute(Codes.TupleLoad bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		//
		Constant _source = frame[bytecode.operand(0)];
		// Check that we have a tuple
		checkType(_source,pc,block,Constant.Tuple.class);
		Constant.Tuple source = (Constant.Tuple) _source;
		// Assign tuple element to target register
		frame[bytecode.target()] = source.values.get(bytecode.index);
		// Fall through to next bytecode
		return pc.next();
	}

	private Object execute(Codes.UnaryOperator bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		Constant _operand = frame[bytecode.operand(0)];
		Constant result;
		//
		switch(bytecode.kind) {
		case NEG:
			checkType(_operand,pc,block,Constant.Integer.class,Constant.Decimal.class);
			if(_operand instanceof Constant.Integer) {
				Constant.Integer operand = (Constant.Integer) _operand;
				result = operand.negate();
			} else {
				Constant.Decimal operand = (Constant.Decimal) _operand;
				result = operand.negate();
			}
			break;
		case DENOMINATOR: {
			checkType(_operand,pc,block,Constant.Decimal.class);
			Constant.Decimal operand = (Constant.Decimal) _operand;
			result = Constant.V_INTEGER(new BigRational(operand.value).denominator());
			break;
		}
		case NUMERATOR: {
			checkType(_operand,pc,block,Constant.Decimal.class);
			Constant.Decimal operand = (Constant.Decimal) _operand;
			result = Constant.V_INTEGER(new BigRational(operand.value).numerator());
			break;
		}
		default:
			return deadCode(pc,block);
		}
		// Assign result to target register
		frame[bytecode.target()] = result;
		// Fall through to next bytecode
		return pc.next();

	}

	private Object execute(Codes.Update bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Implement me!");
	}

	private Object execute(Codes.Void bytecode, Index pc, Constant[] frame,
			AttributedCodeBlock block) {
		// This bytecode just voids out all of its operands.
		for(int operand : bytecode.operands()) {
			frame[operand] = null;
		}
		return pc.next();
	}

	/**
	 * Check that a given operand value matches an expected type.
	 *
	 * @param operand --- bytecode operand to be checked
	 * @param pc --- Current position within the code block
	 * @param block --- Enclosing code block
	 * @param types --- Types to be checked against
	 */
	private void checkType(Constant operand, CodeBlock.Index pc,
			AttributedCodeBlock block, Class<? extends Constant>... types) {
		// Got through each type in turn checking for a match
		for(int i=0;i!=types.length;++i) {
			if(types[i].isInstance(operand)) {
				// Matched!
				return;
			}
		}
		// No match, therefore through an error
		error("invalid operand",pc,block);
	}

	/**
	 * This method is provided as a generic mechanism for reporting runtime
	 * errors within the interpreter.
	 *
	 * @param msg
	 *            --- Message to be printed when error arises.
	 * @param pc
	 *            --- Current position within the code block
	 * @param block
	 *            --- Enclosing code block
	 */
	private Object error(String msg, CodeBlock.Index pc, AttributedCodeBlock block) {
		// FIXME: do more here
		throw new RuntimeException(msg);
	}

	/**
	 * This method is provided to properly handled positions which should be
	 * dead code.
	 *
	 * @param pc
	 *            --- Current position within the code block
	 * @param block
	 *            --- Enclosing code block
	 */
	private Object deadCode(CodeBlock.Index pc, AttributedCodeBlock block) {
		// FIXME: do more here
		throw new RuntimeException("internal failure --- dead code reached");
	}
}
