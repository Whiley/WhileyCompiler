package wyil.util;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.*;

import wyautl.util.BigRational;
import wybs.lang.Build;
import wycc.lang.NameID;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wyfs.lang.Path;
import wyil.lang.*;

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
	 * The debug stream provides an I/O stream through which debug bytecodes can
	 * write their messages.
	 */
	private final PrintStream debug;

	public Interpreter(Build.Project project, PrintStream debug) {
		this.project = project;
		this.debug = debug;
		this.expander = new TypeExpander(project);
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
	public Constant execute(NameID nid, Type.FunctionOrMethod sig,
			Constant... args) {
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
			ArrayList<Type> sig_params = sig.params();
			Constant[] frame = new Constant[Math.max(sig_params.size(),
					body.numSlots())];
			for (int i = 0; i != sig_params.size(); ++i) {
				frame[i] = args[i];
			}
			// Finally, let's do it!
			return (Constant) executeAllWithin(frame, new Context(null, body));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
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
	private Object executeAllWithin(Constant[] frame, Context context) {
		AttributedCodeBlock block = context.block;
		CodeBlock.Index parent = context.pc;
		CodeBlock.Index pc = parent == null ? new CodeBlock.Index(null, 0)
				: parent.firstWithin();

		do {
			Object r = execute(frame, new Context(pc, block));
			// Now, see whether we are continuing or not
			if (r instanceof CodeBlock.Index) {
				pc = (CodeBlock.Index) r;
			} else {
				return r;
			}
		} while (pc.isWithin(parent) && block.contains(pc));
		if (!pc.isWithin(parent)) {
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
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Constant[] frame, Context context) {
		Code bytecode = context.block.get(context.pc);
		// FIXME: turn this into a switch statement?
		if (bytecode instanceof Codes.Invariant) {
			return execute((Codes.Invariant) bytecode, frame, context);
		} else if (bytecode instanceof Codes.AssertOrAssume) {
			return execute((Codes.AssertOrAssume) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Assign) {
			return execute((Codes.Assign) bytecode, frame, context);
		} else if (bytecode instanceof Codes.BinaryOperator) {
			return execute((Codes.BinaryOperator) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Const) {
			return execute((Codes.Const) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Convert) {
			return execute((Codes.Convert) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Debug) {
			return execute((Codes.Debug) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Dereference) {
			return execute((Codes.Dereference) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Fail) {
			return execute((Codes.Fail) bytecode, frame, context);
		} else if (bytecode instanceof Codes.FieldLoad) {
			return execute((Codes.FieldLoad) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Goto) {
			return execute((Codes.Goto) bytecode, frame, context);
		} else if (bytecode instanceof Codes.If) {
			return execute((Codes.If) bytecode, frame, context);
		} else if (bytecode instanceof Codes.IfIs) {
			return execute((Codes.IfIs) bytecode, frame, context);
		} else if (bytecode instanceof Codes.IndexOf) {
			return execute((Codes.IndexOf) bytecode, frame, context);
		} else if (bytecode instanceof Codes.IndirectInvoke) {
			return execute((Codes.IndirectInvoke) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Invert) {
			return execute((Codes.Invert) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Invoke) {
			return execute((Codes.Invoke) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Label) {
			// essentially do nout
			return context.pc.next();
		} else if (bytecode instanceof Codes.Lambda) {
			return execute((Codes.Lambda) bytecode, frame, context);
		} else if (bytecode instanceof Codes.LengthOf) {
			return execute((Codes.LengthOf) bytecode, frame, context);
		} else if (bytecode instanceof Codes.ListOperator) {
			return execute((Codes.ListOperator) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Loop) {
			return execute((Codes.Loop) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Move) {
			return execute((Codes.Move) bytecode, frame, context);
		} else if (bytecode instanceof Codes.NewList) {
			return execute((Codes.NewList) bytecode, frame, context);
		} else if (bytecode instanceof Codes.NewObject) {
			return execute((Codes.NewObject) bytecode, frame, context);
		} else if (bytecode instanceof Codes.NewRecord) {
			return execute((Codes.NewRecord) bytecode, frame, context);
		} else if (bytecode instanceof Codes.NewTuple) {
			return execute((Codes.NewTuple) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Nop) {
			return execute((Codes.Nop) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Quantify) {
			return execute((Codes.Quantify) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Return) {
			return execute((Codes.Return) bytecode, frame, context);
		} else if (bytecode instanceof Codes.SubList) {
			return execute((Codes.SubList) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Switch) {
			return execute((Codes.Switch) bytecode, frame, context);
		} else if (bytecode instanceof Codes.TupleLoad) {
			return execute((Codes.TupleLoad) bytecode, frame, context);
		} else if (bytecode instanceof Codes.UnaryOperator) {
			return execute((Codes.UnaryOperator) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Update) {
			return execute((Codes.Update) bytecode, frame, context);
		} else if (bytecode instanceof Codes.Void) {
			return execute((Codes.Void) bytecode, frame, context);
		} else {
			throw new IllegalArgumentException("Unknown bytecode encountered: "
					+ bytecode);
		}
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
	private Object execute(Codes.AssertOrAssume bytecode, Constant[] frame,
			Context context) {
		//
		Object r = executeAllWithin(frame, context);
		//
		if (r == null) {
			// Body of assert fell through to next
			return context.pc.next();
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
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Codes.Assign bytecode, Constant[] frame,
			Context context) {
		frame[bytecode.target()] = frame[bytecode.operand(0)];
		return context.pc.next();
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
	private Object execute(Codes.BinaryOperator bytecode, Constant[] frame,
			Context context) {
		Constant op1 = frame[bytecode.operand(0)];
		Constant op2 = frame[bytecode.operand(1)];

		// Compute result
		Constant result;
		if (op1 instanceof Constant.Integer) {
			checkType(op2, context, Constant.Integer.class);
			result = execute(bytecode.kind, (Constant.Integer) op1,
					(Constant.Integer) op2, context);
		} else if (op1 instanceof Constant.Rational) {
			checkType(op2, context, Constant.Rational.class);
			result = execute(bytecode.kind, (Constant.Rational) op1,
					(Constant.Rational) op2, context);
		} else if (op1 instanceof Constant.Byte) {
			checkType(op2, context, Constant.Byte.class, Constant.Integer.class);
			result = execute(bytecode.kind, (Constant.Byte) op1, op2, context);
		} else {
			return deadCode(context); // dead because of above checks
		}

		// Write result to target
		frame[bytecode.target()] = result;
		return context.pc.next();
	}

	/**
	 * Execute an integer binary operator
	 *
	 * @param kind
	 *            --- operator kind
	 * @param i1
	 *            --- left operand
	 * @param i2
	 *            --- right operand
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant execute(Codes.BinaryOperatorKind kind,
			Constant.Integer i1, Constant.Integer i2, Context context) {
		switch (kind) {
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
		case RANGE:
			ArrayList<Constant> values = new ArrayList<Constant>();
			int start = i1.value.intValue();
			int end = i2.value.intValue();
			for (int i = start; i < end; ++i) {
				values.add(Constant.V_INTEGER(BigInteger.valueOf(i)));
			}
			return Constant.V_LIST(values);
		}
		deadCode(context);
		return null;
	}

	/**
	 * Execute a rational binary operator
	 *
	 * @param kind
	 *            --- operator kind
	 * @param i1
	 *            --- left operand
	 * @param i2
	 *            --- right operand
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant execute(Codes.BinaryOperatorKind kind,
			Constant.Rational r1, Constant.Rational r2, Context context) {
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
		deadCode(context);
		return null;
	}

	/**
	 * Execute an bitwise binary operator
	 *
	 * @param kind
	 *            --- operator kind
	 * @param i1
	 *            --- left operand
	 * @param i2
	 *            --- right operand
	 * @param context
	 *            --- Context in which bytecodes are executed            
	 * @return
	 */
	private Constant execute(Codes.BinaryOperatorKind kind, Constant.Byte i1,
			Constant i2, Context context) {
		int result;
		switch (kind) {
		case BITWISEAND:
			checkType(i2, context, Constant.Byte.class);
			result = i1.value & ((Constant.Byte) i2).value;
			break;
		case BITWISEOR:
			checkType(i2, context, Constant.Byte.class);
			result = i1.value | ((Constant.Byte) i2).value;
			break;
		case BITWISEXOR:
			checkType(i2, context, Constant.Byte.class);
			result = i1.value ^ ((Constant.Byte) i2).value;
			break;
		case LEFTSHIFT:
			checkType(i2, context, Constant.Integer.class);
			result = i1.value << ((Constant.Integer) i2).value.intValue();
			break;
		case RIGHTSHIFT:
			checkType(i2, context, Constant.Integer.class);
			result = i1.value >> ((Constant.Integer) i2).value.intValue();
			break;
		default:
			deadCode(context);
			return null;
		}
		return Constant.V_BYTE((byte) result);
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
	private Object execute(Codes.Const bytecode, Constant[] frame,
			Context context) {
		Constant c = cleanse(bytecode.constant, context);
		frame[bytecode.target()] = c;
		return context.pc.next();
	}

	private Object execute(Codes.Convert bytecode, Constant[] frame,
			Context context) {
		try {
			Constant operand = frame[bytecode.operand(0)];
			Type target = expander.getUnderlyingType(bytecode.result);
			frame[bytecode.target()] = convert(operand, target, context);
			return context.pc.next();
		} catch (IOException e) {
			return error(e.getMessage(), context);
		} catch (ResolveError e) {
			return error(e.getMessage(), context);
		}
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
		} else if (to instanceof Type.Real) {
			return convert(value, (Type.Real) to, context);
		} else if (to instanceof Type.Record) {
			return convert(value, (Type.Record) to, context);
		} else if (to instanceof Type.List) {
			return convert(value, (Type.List) to, context);
		} else if (to instanceof Type.Tuple) {
			return convert(value, (Type.Tuple) to, context);
		} else if (to instanceof Type.Union) {
			return convert(value, (Type.Union) to, context);
		} else if (to instanceof Type.FunctionOrMethod) {
			return convert(value, (Type.FunctionOrMethod) to, context);
		}
		deadCode(context);
		return null;
	}

	/**
	 * Convert a value into an real type. The value must be of integer type for
	 * this to make sense.
	 *
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type.Real to, Context context) {
		checkType(value, context, Constant.Integer.class);
		// int -> real
		Constant.Integer iv = (Constant.Integer) value;
		return Constant.V_RATIONAL(new BigRational(iv.value));
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
		HashSet<String> rv_fields = new HashSet<String>(rv.values.keySet());
		// Check fields in value are subset of those in target type
		if (!rv_fields.containsAll(to.keys())) {
			error("cannot convert between records with differing fields",
					context);
			return null; // deadcode
		} else {
			HashMap<String, Constant> nValues = new HashMap<String, Constant>();
			for (String field : to.keys()) {
				Constant nValue = convert(rv.values.get(field),
						to.field(field), context);
				nValues.put(field, nValue);
			}
			return Constant.V_RECORD(nValues);
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
	private Constant convert(Constant value, Type.List to, Context context) {
		checkType(value, context, Constant.List.class);
		Constant.List lv = (Constant.List) value;
		ArrayList<Constant> values = new ArrayList<Constant>(lv.values);
		for (int i = 0; i != values.size(); ++i) {
			values.set(i, convert(values.get(i), to.element(), context));
		}
		return Constant.V_LIST(values);
	}
	
	/**
	 * Convert a value into a tuple type. The value must be of tuple type for
	 * this to make sense.
	 *
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type.Tuple to, Context context) {
		checkType(value, context, Constant.Tuple.class);
		Constant.Tuple lv = (Constant.Tuple) value;
		List<Type> to_elements = to.elements();
		ArrayList<Constant> lv_values = lv.values;
		// Check we have matching fields
		if (lv_values.size() != to_elements.size()) {
			error("cannot convert between tuples with differing sizes", context);
			return null; // deadcode
		} else {
			ArrayList<Constant> nValues = new ArrayList<Constant>();
			for (int i = 0; i != lv_values.size(); ++i) {
				Constant nValue = convert(lv_values.get(i), to_elements.get(i),
						context);
				nValues.add(nValue);
			}
			return Constant.V_TUPLE(nValues);
		}
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
	private Object execute(Codes.Debug bytecode, Constant[] frame,
			Context context) {
		//
		Constant.List list = (Constant.List) frame[bytecode.operand];
		for (Constant item : list.values) {
			BigInteger b = ((Constant.Integer) item).value;
			char c = (char) b.intValue();
			debug.print(c);
		}
		//
		return context.pc.next();
	}

	private Object execute(Codes.Dereference bytecode, Constant[] frame,
			Context context) {
		Constant operand = frame[bytecode.operand(0)];
		checkType(operand, context, ConstantObject.class);
		ConstantObject ref = (ConstantObject) operand;
		frame[bytecode.target()] = ref.read();
		return context.pc.next();
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
	private Object execute(Codes.Fail bytecode, Constant[] frame,
			Context context) {
		throw new Error("Runtime fault occurred");
	}

	private Object execute(Codes.FieldLoad bytecode, Constant[] frame,
			Context context) {
		Constant.Record rec = (Constant.Record) frame[bytecode.operand(0)];
		frame[bytecode.target()] = rec.values.get(bytecode.field);
		return context.pc.next();
	}

	private Object execute(Codes.Goto bytecode, Constant[] frame,
			Context context) {
		return context.getLabel(bytecode.target);
	}

	private Object execute(Codes.If bytecode, Constant[] frame, Context context) {
		Constant op1 = frame[bytecode.leftOperand];
		Constant op2 = frame[bytecode.rightOperand];
		boolean result;
		switch (bytecode.op) {
		// Handle cases which apply to all values
		case EQ:
			result = op1.equals(op2);
			break;
		case NEQ:
			result = !op1.equals(op2);
			break;
		// Handle arithmetic cases
		case LT:
			result = lessThan(op1, op2, true, context);
			break;
		case LTEQ:
			result = lessThan(op1, op2, false, context);
			break;
		case GT:
			result = lessThan(op2, op1, true, context);
			break;
		case GTEQ:
			result = lessThan(op2, op1, false, context);
			break;
		case IN:
			result = elementOf(op1, op2, context);
			break;		
		default:
			return deadCode(context);
		}

		if (result) {
			// branch taken, so jump to destination label
			return context.getLabel(bytecode.target);
		} else {
			// branch not taken, so fall through to next bytecode.
			return context.pc.next();
		}
	}

	private boolean elementOf(Constant lhs, Constant rhs, Context context) {
		checkType(rhs, context,Constant.List.class);
		Constant.List list = (Constant.List) rhs;		
		return list.values.contains(lhs);
	}

	private boolean lessThan(Constant lhs, Constant rhs, boolean isStrict,
			Context context) {
		checkType(lhs, context, Constant.Integer.class, Constant.Rational.class);
		checkType(rhs, context, lhs.getClass());
		int result;
		if (lhs instanceof Constant.Integer) {
			Constant.Integer lhs_i = (Constant.Integer) lhs;
			Constant.Integer rhs_i = (Constant.Integer) rhs;
			result = lhs_i.compareTo(rhs_i);
		} else {
			Constant.Rational lhs_i = (Constant.Rational) lhs;
			Constant.Rational rhs_i = (Constant.Rational) rhs;
			result = lhs_i.compareTo(rhs_i);
		}
		// In the strict case, the lhs must be strictly below the rhs. In the
		// non-strict case, they can be equal.
		if (isStrict) {
			return result < 0;
		} else {
			return result <= 0;
		}
	}

	private Object execute(Codes.IfIs bytecode, Constant[] frame,
			Context context) {
		Type typeOperand = bytecode.rightOperand;
		Constant op = frame[bytecode.operand];
		if (isMemberOfType(op, typeOperand, context)) {
			return context.getLabel(bytecode.target);
		}
		// No, it doesn't so fall through to next instruction
		return context.pc.next();

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
		} else if (type instanceof Type.Real) {
			return value instanceof Constant.Rational;
		} else if (type instanceof Type.Reference) {
			if (value instanceof ConstantObject) {
				ConstantObject obj = (ConstantObject) value;
				Type.Reference rt = (Type.Reference) type;
				return isMemberOfType(obj.value, rt.element(), context);
			}
			return false;
		} else if (type instanceof Type.List) {
			if (value instanceof Constant.List) {
				Constant.List t = (Constant.List) value;
				Type element = ((Type.List) type).element();
				boolean r = true;
				for (Constant val : t.values) {
					r &= isMemberOfType(val, element, context);
				}
				return r;
			}
			return false;
		} else if (type instanceof Type.Tuple) {
			if (value instanceof Constant.Tuple) {
				Constant.Tuple t = (Constant.Tuple) value;
				Type.Tuple tt = (Type.Tuple) type;
				List<Constant> t_values = t.values;
				List<Type> tt_elements = tt.elements();
				if (t_values.size() != tt_elements.size()) {
					return false;
				} else {
					boolean r = true;
					for (int i = 0; i != t_values.size(); ++i) {
						r &= isMemberOfType(t_values.get(i),
								tt_elements.get(i), context);
					}
					return r;
				}
			}
			return false;
		} else if (type instanceof Type.Record) {
			if (value instanceof Constant.Record) {
				Type.Record rt = (Type.Record) type;
				Constant.Record t = (Constant.Record) value;
				Set<String> fields = t.values.keySet();
				if (!fields.containsAll(rt.keys())
						|| (!rt.keys().containsAll(fields) && !rt.isOpen())) {
					// In this case, the set of fields does not match properly
					return false;
				}
				boolean r = true;
				for (String field : fields) {
					r &= isMemberOfType(t.values.get(field), rt.field(field),
							context);
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
		} else if (type instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.name();
			try {
				Path.Entry<WyilFile> entry = project.get(nid.module(),
						WyilFile.ContentType);
				if (entry == null) {
					throw new IllegalArgumentException("no WyIL file found: "
							+ nid.module());
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
				AttributedCodeBlock invariant = td.invariant();
				if (invariant != null) {
					Constant[] frame = new Constant[invariant.numSlots()];
					frame[0] = value;
					executeAllWithin(frame, new Context(null, invariant));
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

	/**
	 * Execute an IndexOf bytecode instruction at a given point in the function
	 * or method body. This checks the first operand is a list value, and the
	 * second operand is an integer value. It also checks that the integer index
	 * is within bounds and, if not, raises a runtime fault.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Codes.IndexOf bytecode, Constant[] frame,
			Context context) {
		Constant operand_0 = frame[bytecode.operand(0)];
		Constant operand_1 = frame[bytecode.operand(1)];
		// Check we have a list and an integer index
		checkType(operand_0, context, Constant.List.class);
		checkType(operand_1, context, Constant.Integer.class);
		// Yes, now check that this is in bounds
		Constant.List list = (Constant.List) operand_0;
		Constant.Integer index = (Constant.Integer) operand_1;
		int i = index.value.intValue();
		if (i < 0 || i >= list.values.size()) {
			error("index-out-of-bounds", context);
		}
		// Ok, get the element at that index
		frame[bytecode.target()] = list.values.get(index.value.intValue());
		// Done		
		return context.pc.next();
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
	private Object execute(Codes.IndirectInvoke bytecode, Constant[] frame,
			Context context) {
		Constant operand = frame[bytecode.operand(0)];
		// Check that we have a function reference
		checkType(operand, context, Constant.Lambda.class);
		// Yes we do; now construct the arguments. This requires merging the
		// constant arguments provided in the lambda itself along with those
		// operands provided for the "holes".
		Constant.Lambda func = (Constant.Lambda) operand;
		int[] operands = bytecode.operands();
		Constant[] arguments = new Constant[Math.max(func.arguments.size(),
				operands.length - 1)];
		int operandIndex = 1;
		for (int i = 0; i != arguments.length; ++i) {
			if (i >= func.arguments.size() || func.arguments.get(i) == null) {
				arguments[i] = frame[operands[operandIndex]];
				operandIndex = operandIndex + 1;
			} else {
				arguments[i] = func.arguments.get(i);
			}
		}
		// Make the actual call
		Constant result = execute(func.name, func.type(), arguments);
		// Coerce the result (may not be actually necessary)
		result = convert(result,bytecode.type().ret(),context);
		// Check whether a return value was expected or not
		if (bytecode.target() != Codes.NULL_REG) {
			frame[bytecode.target()] = result;
		}
		// Done
		return context.pc.next();
	}

	private Object execute(Codes.Invariant bytecode, Constant[] frame,
			Context context) {
		// FIXME: currently implemented as a NOP because of #480
		return context.pc.next();
	}

	/**
	 * Execute an Invert bytecode instruction at a given point in the function
	 * or method body. This checks the operand is a byte value.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Codes.Invert bytecode, Constant[] frame,
			Context context) {
		Constant operand = frame[bytecode.operand(0)];
		// Check that we have a byte operand
		checkType(operand, context, Constant.Byte.class);
		// Write back the inverted value
		Constant.Byte b = (Constant.Byte) operand;
		frame[bytecode.target()] = Constant.V_BYTE((byte) ~b.value);
		// Done
		return context.pc.next();
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
	private Object execute(Codes.Invoke bytecode, Constant[] frame,
			Context context) {
		int[] operands = bytecode.operands();
		Constant[] arguments = new Constant[operands.length];
		for (int i = 0; i != arguments.length; ++i) {
			arguments[i] = frame[operands[i]];
		}
		Constant result = execute(bytecode.name, bytecode.type(), arguments);
		if (bytecode.target() != Codes.NULL_REG) {
			frame[bytecode.target()] = result;
		}
		return context.pc.next();
	}

	private Object execute(Codes.Lambda bytecode, Constant[] frame,
			Context context) {

		int[] operands = bytecode.operands();
		Constant[] arguments = new Constant[operands.length];
		for (int i = 0; i != arguments.length; ++i) {
			int reg = operands[i];
			if (reg != Codes.NULL_REG) {
				arguments[i] = frame[reg];
			}
		}
		// FIXME: need to do something with the operands here.
		frame[bytecode.target()] = Constant.V_LAMBDA(bytecode.name,
				bytecode.type(), arguments);
		//
		return context.pc.next();
	}

	/**
	 * Execute a LengthOf bytecode instruction at a given point in the function
	 * or method body. This simply returns the length of the list at the given
	 * position.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Codes.LengthOf bytecode, Constant[] frame,
			Context context) {
		Constant _source = frame[bytecode.operand(0)];
		checkType(_source, context, Constant.List.class);
		Constant.List list = (Constant.List) _source;
		BigInteger length = BigInteger.valueOf(list.values.size());		
		frame[bytecode.target()] = Constant.V_INTEGER(length);
		return context.pc.next();
	}

	/**
	 * Execute the list append bytecode instruction at a given point in the
	 * function or method body. This simply assigns the appended list to the
	 * target register.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Codes.ListOperator bytecode, Constant[] frame,
			Context context) {
		Constant lhs = frame[bytecode.operand(0)];
		Constant rhs = frame[bytecode.operand(1)];
		// Check that we have a function reference
		checkType(lhs, context, Constant.List.class);
		checkType(rhs, context, Constant.List.class);
		// Now, perform the append
		Constant.List l1 = (Constant.List) lhs;
		Constant.List l2 = (Constant.List) rhs;
		ArrayList<Constant> values = new ArrayList<Constant>();
		values.addAll(l1.values);
		values.addAll(l2.values);
		frame[bytecode.target()] = Constant.V_LIST(values);
		return context.pc.next();
	}

	private Object execute(Codes.Loop bytecode, Constant[] frame,
			Context context) {
		Object r;
		do {
			// Keep executing the loop body until we exit it somehow.
			r = executeAllWithin(frame, context);
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
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Codes.Move bytecode, Constant[] frame,
			Context context) {
		frame[bytecode.target()] = frame[bytecode.operand(0)];
		frame[bytecode.operand(0)] = null;
		return context.pc.next();
	}

	/**
	 * Execute a Record bytecode instruction at a given point in the function or
	 * method body. This constructs a new list.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Codes.NewList bytecode, Constant[] frame,
			Context context) {
		ArrayList<Constant> values = new ArrayList<Constant>();
		for (int operand : bytecode.operands()) {
			values.add((Constant) frame[operand]);
		}
		frame[bytecode.target()] = Constant.V_LIST(values);
		return context.pc.next();
	}

	private Object execute(Codes.NewObject bytecode, Constant[] frame,
			Context context) {
		Constant operand = frame[bytecode.operand(0)];
		ConstantObject o = new ConstantObject(operand);
		frame[bytecode.target()] = o;
		return context.pc.next();
	}

	/**
	 * Execute a Record bytecode instruction at a given point in the function or
	 * method body. This constructs a new record.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Codes.NewRecord bytecode, Constant[] frame,
			Context context) {
		HashMap<String, Constant> values = new HashMap<String, Constant>();
		ArrayList<String> fields = new ArrayList<String>(bytecode.type()
				.fields().keySet());
		Collections.sort(fields);
		int[] operands = bytecode.operands();
		for (int i = 0; i != operands.length; ++i) {
			values.put(fields.get(i), (Constant) frame[operands[i]]);
		}
		frame[bytecode.target()] = Constant.V_RECORD(values);
		return context.pc.next();
	}

	/**
	 * Execute a Tuple bytecode instruction at a given point in the function or
	 * method body. This constructs a new tuple.
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Codes.NewTuple bytecode, Constant[] frame,
			Context context) {
		ArrayList<Constant> values = new ArrayList<Constant>();
		for (int operand : bytecode.operands()) {
			values.add((Constant) frame[operand]);
		}
		frame[bytecode.target()] = Constant.V_TUPLE(values);
		return context.pc.next();
	}

	/**
	 * Execute a Nop bytecode instruction at a given point in the function or
	 * method body. This basically doesn't do anything!
	 *
	 * @param bytecode
	 *            --- The bytecode to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Object execute(Codes.Nop bytecode, Constant[] frame, Context context) {
		return context.pc.next();
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
	private Object execute(Codes.Return bytecode, Constant[] frame,
			Context context) {
		if (bytecode.operand == Codes.NULL_REG) {
			// This is a void return
			return null;
		} else {
			return frame[bytecode.operand];
		}
	}

	private Object execute(Codes.SubList bytecode, Constant[] frame,
			Context context) {
		Constant _source = frame[bytecode.operand(0)];
		Constant _fromIndex = frame[bytecode.operand(1)];
		Constant _toIndex = frame[bytecode.operand(2)];
		// Check that we have a function reference
		checkType(_source, context, Constant.List.class);
		checkType(_fromIndex, context, Constant.Integer.class);
		checkType(_toIndex, context, Constant.Integer.class);
		// Now, perform the append
		Constant.List source = (Constant.List) _source;
		Constant.Integer fromIndex = (Constant.Integer) _fromIndex;
		Constant.Integer toIndex = (Constant.Integer) _toIndex;

		frame[bytecode.target()] = Constant.V_LIST(source.values.subList(
				fromIndex.value.intValue(), toIndex.value.intValue()));

		return context.pc.next();
	}

	private Object execute(Codes.Switch bytecode, Constant[] frame,
			Context context) {
		//
		Constant operand = frame[bytecode.operand];
		for (Pair<Constant, String> branch : bytecode.branches) {
			Constant caseOperand = cleanse(branch.first(), context);
			if (caseOperand.equals(operand)) {
				return context.getLabel(branch.second());
			}
		}
		return context.getLabel(bytecode.defaultTarget);
	}

	private Object execute(Codes.TupleLoad bytecode, Constant[] frame,
			Context context) {
		//
		Constant _source = frame[bytecode.operand(0)];
		// Check that we have a tuple
		checkType(_source, context, Constant.Tuple.class);
		Constant.Tuple source = (Constant.Tuple) _source;
		// Assign tuple element to target register
		frame[bytecode.target()] = source.values.get(bytecode.index);
		// Fall through to next bytecode
		return context.pc.next();
	}

	private Object execute(Codes.UnaryOperator bytecode, Constant[] frame,
			Context context) {
		Constant _operand = frame[bytecode.operand(0)];
		Constant result;
		//
		switch (bytecode.kind) {
		case NEG:
			checkType(_operand, context, Constant.Integer.class,
					Constant.Rational.class);
			if (_operand instanceof Constant.Integer) {
				Constant.Integer operand = (Constant.Integer) _operand;
				result = operand.negate();
			} else {
				Constant.Rational operand = (Constant.Rational) _operand;
				result = operand.negate();
			}
			break;
		case DENOMINATOR: {
			checkType(_operand, context, Constant.Rational.class);
			Constant.Rational operand = (Constant.Rational) _operand;
			result = Constant.V_INTEGER(operand.value.denominator());
			break;
		}
		case NUMERATOR: {
			checkType(_operand, context, Constant.Rational.class);
			Constant.Rational operand = (Constant.Rational) _operand;
			result = Constant.V_INTEGER(operand.value.numerator());
			break;
		}
		default:
			return deadCode(context);
		}
		// Assign result to target register
		frame[bytecode.target()] = result;
		// Fall through to next bytecode
		return context.pc.next();

	}

	private Object execute(Codes.Update bytecode, Constant[] frame,
			Context context) {
		Constant rhs = frame[bytecode.result()];
		Constant lhs = frame[bytecode.target()];
		frame[bytecode.target()] = update(lhs, bytecode.iterator(), rhs, frame,
				context);
		return context.pc.next();
	}

	/**
	 * Manages the process of updating an LVal. Here, the left-hand side (lhs)
	 * value is passed in along with a descriptor describing the shape of the
	 * lhs. The right-hand side value is that being assigned by the bytecode to
	 * a component of the left-hand side.
	 *
	 * @param lhs
	 *            The left-hand side component being updated
	 * @param descriptor
	 *            A descriptor describing the shape of the left-hand side
	 *            component.
	 * @param rhs
	 *            The right-hand side value being assigned to the inner-most
	 *            component of the left-hand side.
	 * @param frame
	 *            The current stack frame
	 * @param context
	 *            Context in which bytecodes are executed
	 *
	 * @return The left-hand side updated with the new value assigned
	 */
	private Constant update(Constant lhs, Iterator<Codes.LVal> descriptor,
			Constant rhs, Constant[] frame, Context context) {
		if (descriptor.hasNext()) {
			Codes.LVal lval = descriptor.next();
			// Check what shape the left-hand side is
			if (lval instanceof Codes.ListLVal) {
				// List
				Codes.ListLVal lv = (Codes.ListLVal) lval;
				Constant operand = frame[lv.indexOperand];
				checkType(operand, context, Constant.Integer.class);
				checkType(lhs, context, Constant.List.class);
				Constant.List list = (Constant.List) lhs;
				int index = ((Constant.Integer) operand).value.intValue();
				ArrayList<Constant> values = new ArrayList<Constant>(
						list.values);
				rhs = update(values.get(index), descriptor, rhs, frame, context);
				values.set(index, rhs);
				return Constant.V_LIST(values);
			} else if (lval instanceof Codes.RecordLVal) {
				// Record
				Codes.RecordLVal lv = (Codes.RecordLVal) lval;
				checkType(lhs, context, Constant.Record.class);
				Constant.Record record = (Constant.Record) lhs;
				HashMap<String, Constant> values = new HashMap<String, Constant>(
						record.values);
				rhs = update(values.get(lv.field), descriptor, rhs, frame,
						context);
				values.put(lv.field, rhs);
				return Constant.V_RECORD(values);
			} else {
				// Reference
				Codes.ReferenceLVal lv = (Codes.ReferenceLVal) lval;
				checkType(lhs, context, ConstantObject.class);
				ConstantObject object = (ConstantObject) lhs;
				rhs = update(object.read(), descriptor, rhs, frame, context);
				object.write(rhs);
				return object;
			}
		} else {
			// Base case --- we've reached the inner most component. Therefore,
			// we effectively replace the lhs with the rhs.
			return rhs;
		}
	}

	private Object execute(Codes.Void bytecode, Constant[] frame,
			Context context) {
		// This bytecode just voids out all of its operands.
		for (int operand : bytecode.operands()) {
			frame[operand] = null;
		}
		return context.pc.next();
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
	private void checkType(Constant operand, Context context,
			Class<? extends Constant>... types) {
		// Got through each type in turn checking for a match
		for (int i = 0; i != types.length; ++i) {
			if (types[i].isInstance(operand)) {
				// Matched!
				return;
			}
		}
		// No match, therefore through an error
		error("invalid operand", context);
	}

	/**
	 * Cleanse all instances of Constant.Decimal from the given constant and
	 * replace them with Constant.Rational.
	 *
	 * @param constant
	 * @return
	 */
	private Constant cleanse(Constant constant, Context context) {
		// See #494 for more on why this method exists, and whether or not we
		// can get rid of it.
		if (constant instanceof Constant.Null
				|| constant instanceof Constant.Byte
				|| constant instanceof Constant.Bool
				|| constant instanceof Constant.Integer
				|| constant instanceof Constant.Lambda
				|| constant instanceof Constant.Type) {
			return constant;
		} else if (constant instanceof Constant.Decimal) {
			Constant.Decimal dec = (Constant.Decimal) constant;
			return Constant.V_RATIONAL(new BigRational(dec.value));
		} else if (constant instanceof Constant.Tuple) {
			Constant.Tuple ct = (Constant.Tuple) constant;
			ArrayList<Constant> values = new ArrayList<Constant>(ct.values);
			for (int i = 0; i != values.size(); ++i) {
				values.set(i, cleanse(values.get(i), context));
			}
			return Constant.V_TUPLE(values);
		} else if (constant instanceof Constant.List) {
			Constant.List ct = (Constant.List) constant;
			ArrayList<Constant> values = new ArrayList<Constant>(ct.values);
			for (int i = 0; i != values.size(); ++i) {
				values.set(i, cleanse(values.get(i), context));
			}
			return Constant.V_LIST(values);
		} else if (constant instanceof Constant.Record) {
			Constant.Record mt = (Constant.Record) constant;
			HashMap<String, Constant> fields = mt.values;
			HashMap<String, Constant> nFields = new HashMap<String, Constant>();
			for (Map.Entry<String, Constant> e : fields.entrySet()) {
				Constant value = cleanse(e.getValue(), context);
				nFields.put(e.getKey(), value);
			}
			return Constant.V_RECORD(nFields);
		} else {
			deadCode(context);
			return null;
		}
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
	private Object error(String msg, Context context) {
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

	/**
	 * A class for grouping two related items together. In essence, the context
	 * represents a position within a given bytecode block. This is useful for
	 * better error reporting.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Context {
		public final CodeBlock.Index pc;
		public final AttributedCodeBlock block;
		private Map<String, CodeBlock.Index> labels;

		public Context(CodeBlock.Index pc, AttributedCodeBlock block) {
			this.pc = pc;
			this.block = block;
		}

		public CodeBlock.Index getLabel(String label) {
			if (labels == null) {
				labels = CodeUtils.buildLabelMap(block);
			}
			return labels.get(label);
		}
	}

	/**
	 * Represents an object allocated on the heap.
	 *
	 * @author David J. Pearce
	 *
	 */
	private class ConstantObject extends Constant {
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
			throw new UnsupportedOperationException(
					"ConstantObject.compare() cannot be implemented");
		}

		@Override
		public wyil.lang.Type type() {
			return wyil.lang.Type.Reference(value.type());
		}

	}
}
