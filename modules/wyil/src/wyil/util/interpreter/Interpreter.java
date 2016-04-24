package wyil.util.interpreter;

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
import wyil.util.TypeExpander;

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
			BytecodeForest code = fm.code();
			if (fm.body() == null) {
				// FIXME: add support for native functions or methods
				throw new IllegalArgumentException("no function or method body found: " + nid + ", " + sig);
			}
			// Fourth, construct the stack frame for execution
			ArrayList<Type> sig_params = sig.params();
			Constant[] frame = new Constant[code.numRegisters()];
			for (int i = 0; i != sig_params.size(); ++i) {
				frame[i] = args[i];
			}
			// Finally, let's do it!
			BytecodeForest.Index pc = new BytecodeForest.Index(fm.body(), 0);
			return (Constant[]) executeAllWithin(frame, new Context(pc, code));
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
		BytecodeForest forest = context.forest;
		BytecodeForest.Index pc = context.pc;
		int block = pc.block();
		BytecodeForest.Block codes = forest.get(pc.block());

		while (pc.block() == block && pc.offset() < codes.size()) {
			Object r = execute(frame, new Context(pc, context.forest));
			// Now, see whether we are continuing or not
			if (r instanceof BytecodeForest.Index) {
				pc = (BytecodeForest.Index) r;
			} else {
				return r;
			}
		}
		if (pc.block() != block) {
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
		Bytecode bytecode = context.forest.get(context.pc).code();
		// FIXME: turn this into a switch statement?
		if (bytecode instanceof Bytecode.Invariant) {
			return execute((Bytecode.Invariant) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.AssertOrAssume) {
			return execute((Bytecode.AssertOrAssume) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Operator) {
			return execute((Bytecode.Operator) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Const) {
			return execute((Bytecode.Const) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Convert) {
			return execute((Bytecode.Convert) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Debug) {
			return execute((Bytecode.Debug) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Fail) {
			return execute((Bytecode.Fail) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.FieldLoad) {
			return execute((Bytecode.FieldLoad) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Goto) {
			return execute((Bytecode.Goto) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.If) {
			return execute((Bytecode.If) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.IfIs) {
			return execute((Bytecode.IfIs) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.IndirectInvoke) {
			return execute((Bytecode.IndirectInvoke) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Invoke) {
			return execute((Bytecode.Invoke) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Label) {
			// essentially do nout
			return context.pc.next();
		} else if (bytecode instanceof Bytecode.Lambda) {
			return execute((Bytecode.Lambda) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Quantify) {
			return execute((Bytecode.Quantify) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Loop) {
			return execute((Bytecode.Loop) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Return) {
			return execute((Bytecode.Return) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Switch) {
			return execute((Bytecode.Switch) bytecode, frame, context);
		} else if (bytecode instanceof Bytecode.Update) {
			return execute((Bytecode.Update) bytecode, frame, context);
		} else {
			throw new IllegalArgumentException("Unknown bytecode encountered: " + bytecode);
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
	private Object execute(Bytecode.AssertOrAssume bytecode, Constant[] frame, Context context) {
		//
		BytecodeForest.Index pc = new BytecodeForest.Index(bytecode.block(), 0);
		Object r = executeAllWithin(frame, new Context(pc, context.forest));
		//
		if (r == null) {
			// Body of assert fell through to next
			return context.pc.next();
		} else {
			return r;
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
	private Object execute(Bytecode.Operator bytecode, Constant[] frame, Context context) {
		int[] operands = bytecode.operands();
		Constant[] values = new Constant[operands.length];
		// Read all operands
		for(int i=0;i!=operands.length;++i) {
			values[i] = frame[operands[i]];
		}		
		// Compute result
		Constant result = operators[bytecode.opcode()].apply(values, context);		
		// Write result to target
		frame[bytecode.target(0)] = result;
		// Continue on to next instruction
		return context.pc.next();
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
	private Object execute(Bytecode.Const bytecode, Constant[] frame, Context context) {
		frame[bytecode.target()] = bytecode.constant();
		return context.pc.next();
	}

	private Object execute(Bytecode.Convert bytecode, Constant[] frame, Context context) {
		try {
			Constant operand = frame[bytecode.operand(0)];
			Type target = expander.getUnderlyingType(bytecode.result());
			frame[bytecode.target(0)] = convert(operand, target, context);
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
	private Object execute(Bytecode.Debug bytecode, Constant[] frame, Context context) {
		//
		Constant.Array list = (Constant.Array) frame[bytecode.operand(0)];
		for (Constant item : list.values()) {
			BigInteger b = ((Constant.Integer) item).value();
			char c = (char) b.intValue();
			debug.print(c);
		}
		//
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
	private Object execute(Bytecode.Fail bytecode, Constant[] frame, Context context) {
		throw new Error("Runtime fault occurred");
	}

	private Object execute(Bytecode.FieldLoad bytecode, Constant[] frame, Context context) {
		Constant.Record rec = (Constant.Record) frame[bytecode.operand(0)];
		frame[bytecode.target(0)] = rec.values().get(bytecode.fieldName());
		return context.pc.next();
	}

	private Object execute(Bytecode.Quantify bytecode, Constant[] frame, Context context) {
		Constant startOperand = frame[bytecode.startOperand()];
		Constant endOperand = frame[bytecode.endOperand()];
		checkType(startOperand, context, Constant.Integer.class);
		checkType(endOperand, context, Constant.Integer.class);
		Constant.Integer so = (Constant.Integer) startOperand;
		Constant.Integer eo = (Constant.Integer) endOperand;
		int start = so.value().intValue();
		int end = eo.value().intValue();
		for (int i = start; i < end; ++i) {
			// Assign the index variable
			frame[bytecode.indexOperand()] = new Constant.Integer(BigInteger.valueOf(i));
			// Execute loop body for one iteration
			BytecodeForest.Index pc = new BytecodeForest.Index(bytecode.block(), 0);
			Object r = executeAllWithin(frame, new Context(pc, context.forest));
			// Now, check whether we fell through to the end or not. If not,
			// then we must have exited somehow so return to signal that.
			if (r != null) {
				return r;
			}
		}

		return context.pc.next();
	}

	private Object execute(Bytecode.Goto bytecode, Constant[] frame, Context context) {
		return context.getLabel(bytecode.destination());
	}

	private Object execute(Bytecode.If bytecode, Constant[] frame, Context context) {
		Constant.Bool operand = checkType(frame[bytecode.operand(0)],context,Constant.Bool.class);
		
		if (operand.value()) {
			// branch taken, so jump to destination label
			return context.getLabel(bytecode.destination());
		} else {
			// branch not taken, so fall through to next bytecode.
			return context.pc.next();
		}
	}

	private Object execute(Bytecode.IfIs bytecode, Constant[] frame, Context context) {
		Type typeOperand = bytecode.type(1);
		Constant op = frame[bytecode.operand(0)];
		if (isMemberOfType(op, typeOperand, context)) {
			return context.getLabel(bytecode.destination());
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
			if (value instanceof Constant.Lambda) {
				Constant.Lambda l = (Constant.Lambda) value;
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
				if (invariant.numBlocks() > 0) {
					Constant[] frame = new Constant[invariant.numRegisters()];
					frame[0] = value;
					BytecodeForest.Index pc = new BytecodeForest.Index(invariant.getRoot(0), 0);
					executeAllWithin(frame, new Context(pc, invariant));
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
	private Object execute(Bytecode.IndirectInvoke bytecode, Constant[] frame, Context context) {
		Constant operand = frame[bytecode.operand(0)];
		// Check that we have a function reference
		checkType(operand, context, Constant.Lambda.class);
		// Yes we do; now construct the arguments. This requires merging the
		// constant arguments provided in the lambda itself along with those
		// operands provided for the "holes".
		Constant.Lambda func = (Constant.Lambda) operand;
		List<Constant> func_arguments = func.arguments();
		int[] operands = bytecode.operands();
		Constant[] arguments = new Constant[func_arguments.size() + (operands.length - 1)];
		{
			int i = 0;
			for (int j = 1; j != operands.length; ++j) {
				arguments[i++] = frame[operands[j]];
			}
			for (int j = 0; j != func_arguments.size(); ++j) {
				arguments[i++] = func.arguments().get(j);
			}
		}
		// Make the actual call
		Constant[] results = execute(func.name(), func.type(), arguments);
		// Check whether a return value was expected or not
		int[] targets = bytecode.targets();
		List<Type> returns = bytecode.type(0).returns();
		for (int i = 0; i != targets.length; ++i) {
			// Coerce the result (may not be actually necessary))
			frame[targets[i]] = convert(results[i], returns.get(i), context);
		}
		// Done
		return context.pc.next();
	}

	private Object execute(Bytecode.Invariant bytecode, Constant[] frame, Context context) {
		// FIXME: currently implemented as a NOP because of #480
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
	private Object execute(Bytecode.Invoke bytecode, Constant[] frame, Context context) {
		int[] operands = bytecode.operands();
		Constant[] arguments = new Constant[operands.length];
		for (int i = 0; i != arguments.length; ++i) {
			arguments[i] = frame[operands[i]];
		}
		Constant[] results = execute(bytecode.name(), bytecode.type(0), arguments);
		int[] targets = bytecode.targets();
		for (int i = 0; i != targets.length; ++i) {
			frame[targets[i]] = results[i];
		}
		return context.pc.next();
	}

	private Object execute(Bytecode.Lambda bytecode, Constant[] frame, Context context) {

		int[] operands = bytecode.operands();
		Constant[] arguments = new Constant[operands.length];

		for (int i = 0; i != arguments.length; ++i) {
			int reg = operands[i];
			arguments[i] = frame[reg];
		}
		// FIXME: need to do something with the operands here.
		frame[bytecode.target(0)] = new Constant.Lambda(bytecode.name(), bytecode.type(0), arguments);
		//
		return context.pc.next();
	}

	private Object execute(Bytecode.Loop bytecode, Constant[] frame, Context context) {
		Object r;
		do {
			// Keep executing the loop body until we exit it somehow.
			BytecodeForest.Index pc = new BytecodeForest.Index(bytecode.block(), 0);
			r = executeAllWithin(frame, new Context(pc, context.forest));
		} while (r == null);

		// If we get here, then we have exited the loop body without falling
		// through to the next bytecode.
		return r;
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
	private Object execute(Bytecode.Return bytecode, Constant[] frame, Context context) {
		int[] operands = bytecode.operands();
		Constant[] returns = new Constant[operands.length];
		for (int i = 0; i != operands.length; ++i) {
			returns[i] = frame[operands[i]];
		}
		return returns;
	}

	private Object execute(Bytecode.Switch bytecode, Constant[] frame, Context context) {
		//
		Constant operand = frame[bytecode.operand(0)];
		for (Pair<Constant, String> branch : bytecode.branches()) {
			Constant caseOperand = branch.first();
			if (caseOperand.equals(operand)) {
				return context.getLabel(branch.second());
			}
		}
		return context.getLabel(bytecode.defaultTarget());
	}

	private Object execute(Bytecode.Update bytecode, Constant[] frame, Context context) {
		Constant rhs = frame[bytecode.result()];
		Constant lhs = frame[bytecode.target(0)];
		frame[bytecode.target(0)] = update(lhs, bytecode.iterator(), rhs, frame, context);
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
	private Constant update(Constant lhs, Iterator<Bytecode.LVal> descriptor, Constant rhs, Constant[] frame,
			Context context) {
		if (descriptor.hasNext()) {
			Bytecode.LVal lval = descriptor.next();
			// Check what shape the left-hand side is
			if (lval instanceof Bytecode.ArrayLVal) {
				// List
				Bytecode.ArrayLVal lv = (Bytecode.ArrayLVal) lval;
				Constant operand = frame[lv.indexOperand];
				checkType(operand, context, Constant.Integer.class);
				checkType(lhs, context, Constant.Array.class);
				Constant.Array list = (Constant.Array) lhs;
				int index = ((Constant.Integer) operand).value().intValue();
				ArrayList<Constant> values = new ArrayList<Constant>(list.values());
				rhs = update(values.get(index), descriptor, rhs, frame, context);
				values.set(index, rhs);
				return new Constant.Array(values);
			} else if (lval instanceof Bytecode.RecordLVal) {
				// Record
				Bytecode.RecordLVal lv = (Bytecode.RecordLVal) lval;
				checkType(lhs, context, Constant.Record.class);
				Constant.Record record = (Constant.Record) lhs;
				HashMap<String, Constant> values = new HashMap<String, Constant>(record.values());
				rhs = update(values.get(lv.field), descriptor, rhs, frame, context);
				values.put(lv.field, rhs);
				return new Constant.Record(values);
			} else {
				// Reference
				Bytecode.ReferenceLVal lv = (Bytecode.ReferenceLVal) lval;
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

	/**
	 * A class for grouping two related items together. In essence, the context
	 * represents a position within a given bytecode block. This is useful for
	 * better error reporting.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Context {
		public final BytecodeForest.Index pc;
		public final BytecodeForest forest;
		private Map<String, BytecodeForest.Index> labels;

		public Context(BytecodeForest.Index pc, BytecodeForest block) {
			this.pc = pc;
			this.forest = block;
		}

		public BytecodeForest.Index getLabel(String label) {
			if (labels == null) {
				labels = Bytecode.buildLabelMap(forest);
			}
			return labels.get(label);
		}
		
		public Bytecode getBytecode() {
			return forest.get(pc).first();
		}
	}

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
	 * An internal function is simply a named internal function. This reads a
	 * bunch of operands and returns a set of results.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static interface InternalFunction {
		public Constant apply(Constant[] operands, Context context);
	}
}
