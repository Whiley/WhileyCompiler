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
package wyc.cmd;

import static wyil.lang.WyilFile.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import jmodelgen.core.Domain;
import jmodelgen.core.Domains;
import jmodelgen.util.AbstractSmallDomain;
import wybs.lang.*;
import wybs.util.AbstractCompilationUnit;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.Activator;
import wyc.util.ErrorMessages;
import wycc.cfg.Configuration;
import wycc.cfg.Configuration.Schema;
import wycc.lang.Command;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.interpreter.ConcreteSemantics.RValue;
import wyil.interpreter.Interpreter;
import wyil.interpreter.Interpreter.CallStack;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Decl.FunctionOrMethod;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.QualifiedName;
import wyil.lang.WyilFile.Attr.StackFrame;
import wyil.lang.WyilFile.Attr.SyntaxError;
import wyil.lang.WyilFile.Type;
import wyil.lang.WyilFile.Type.Callable;

/**
 * <p>
 * Implements a command for randomised testing of functions and methods based on
 * their specifications. This is roughly in line with the "QuickCheck" series of
 * tools, though customised for Whiley. As an example, consider the following
 * function:
 * </p>
 *
 * <pre>
 * function max(int[] items) -> (int max)
 * requires |items| > 0
 * ensures ...:
 *     ...
 * </pre>
 *
 * <p>
 * This function is intended for computing the max of a (non-empty) integer
 * array. The quick check functionality will generate a number of random inputs
 * for this function. In this case, it will generate random arrays upto a given
 * size (say, maximum of three elements). Example inputs generated would be then
 * <code>[]</code>, <code>[1]</code>, <code>[1,2]</code>,<code>[1,2,3]</code>
 * and many more. In fact, the decision as to what integer values to generate is
 * determined by the underling range of integers being considered.
 * </p>
 * <p>
 * Having generated one or more candidate inputs, these will then be tested
 * against the precondition to check whether they are "meaningless" or not. That
 * is, whether or not they are valid inputs for this particular function. Those
 * which fail the precondition (including all invariants on parameter types) are
 * rejected. Those remaining are then passed into the function, and any faults
 * arising are noted along with any inputs for which the generated output(s)
 * fail the post-condition.
 * </p>
 *
 *
 *
 * @author David J. Pearce
 *
 */
public class QuickCheck implements Command {
	public static final Context DEFAULT_CONTEXT = new Context(-3, 3, 3, 3, 2, 2, new String[0], BigDecimal.ONE, 1000,
			10_000_000, Long.MAX_VALUE);
	// Configuration Options
	public static Trie MIN_CONFIG_OPTION = Trie.fromString("check/min");
	public static Trie MAX_CONFIG_OPTION = Trie.fromString("check/max");
	public static Trie LENGTH_CONFIG_OPTION = Trie.fromString("check/length");
	public static Trie DEPTH_CONFIG_OPTION = Trie.fromString("check/depth");
	public static Trie WIDTH_CONFIG_OPTION = Trie.fromString("check/width");
	public static Trie ROTATION_CONFIG_OPTION = Trie.fromString("check/rotation");
	public static Trie SAMPLING_CONFIG_OPTION = Trie.fromString("check/sample");
	public static Trie LIMIT_CONFIG_OPTION = Trie.fromString("check/limit");
	public static Trie TIMEOUT_CONFIG_OPTION = Trie.fromString("check/timeout");
	public static Trie IGNORES_CONFIG_OPTION = Trie.fromString("check/ignores");
	// Configuration Defaults
	public static Value.Int MIN_DEFAULT = new Value.Int(DEFAULT_CONTEXT.getIntegerMinimum());
	public static Value.Int MAX_DEFAULT = new Value.Int(DEFAULT_CONTEXT.getIntegerMaximum());
	public static Value.Int LENGTH_DEFAULT = new Value.Int(DEFAULT_CONTEXT.getMaxArrayLength());
	public static Value.Int DEPTH_DEFAULT = new Value.Int(DEFAULT_CONTEXT.getRecursiveTypeDepth());
	public static Value.Int WIDTH_DEFAULT = new Value.Int(DEFAULT_CONTEXT.getAliasingWidth());
	public static Value.Int ROTATION_DEFAULT = new Value.Int(DEFAULT_CONTEXT.getLambdaWidth());
	public static Value.Decimal SAMPLING_DEFAULT = new Value.Decimal(DEFAULT_CONTEXT.getSamplingRate());
	public static Value.Int LIMIT_DEFAULT = new Value.Int(DEFAULT_CONTEXT.getSampleMin());
	public static Value.Int TIMEOUT_DEFAULT = new Value.Int(DEFAULT_CONTEXT.getTimeout());
	public static Value.Array IGNORES_DEFAULT = new Value.Array();
	/**
	 * The descriptor for this command.
	 */
	public static final Command.Descriptor DESCRIPTOR = new Command.Descriptor() {
		@Override
		public String getName() {
			return "check";
		}

		@Override
		public String getDescription() {
			return "Perform randomised testing of functions and methods";
		}

		@Override
		public List<Option.Descriptor> getOptionDescriptors() {
			return Arrays.asList(
					Command.OPTION_BOUNDED_DOUBLE("sampling",
							"Specify sample size on test inputs to try for each function or method", 0.0D, 10.0D),
					Command.OPTION_FLAG("logsampling",
							"Specify log sample size  test inputs to try for each function or method"),
					Command.OPTION_NONNEGATIVE_INTEGER("limit",
							"Specify limit above which sampling takes place"),
					Command.OPTION_NONNEGATIVE_INTEGER("min",
						"Specify minimum integer value which can be generated"),
					Command.OPTION_NONNEGATIVE_INTEGER("max",
						"Specify maximum integer value which can be generated"),
					Command.OPTION_NONNEGATIVE_INTEGER("length",
						"Specify maximum length of a generated array"),
					Command.OPTION_NONNEGATIVE_INTEGER("depth",
						"Specify maximum depth of a recurisive type"),
					Command.OPTION_NONNEGATIVE_INTEGER("timeout",
							"Specify timeout (in seconds) to spend on each function or method")
					);
		}

		@Override
		public Schema getConfigurationSchema() {
			return Configuration.fromArray(
					Configuration.UNBOUND_INTEGER(MIN_CONFIG_OPTION, "Specify minimum integer value which can be generated",
							MIN_DEFAULT),
					Configuration.UNBOUND_INTEGER(MAX_CONFIG_OPTION, "Specify maximum integer value which can be generated",
							MAX_DEFAULT),
					Configuration.UNBOUND_INTEGER(LENGTH_CONFIG_OPTION, "Specify maximum length of a generated array", LENGTH_DEFAULT),
					Configuration.UNBOUND_INTEGER(DEPTH_CONFIG_OPTION, "Specify maximum depth of a recurisive type",
							DEPTH_DEFAULT),
					Configuration.UNBOUND_INTEGER(WIDTH_CONFIG_OPTION, "Specify aliasing width to use for reference types",
							WIDTH_DEFAULT),
					Configuration.UNBOUND_INTEGER(ROTATION_CONFIG_OPTION, "Specify rotation to use for synthesized lambdas",
							ROTATION_DEFAULT),
					Configuration.BOUND_DECIMAL(SAMPLING_CONFIG_OPTION, "Specify sample rate of test inputs to try for each function or method",
							SAMPLING_DEFAULT,0.0D,1.0D),
					Configuration.UNBOUND_INTEGER(LIMIT_CONFIG_OPTION, "Specify limit above which sampling takes place",
							LIMIT_DEFAULT),
					Configuration.UNBOUND_INTEGER(TIMEOUT_CONFIG_OPTION, "Specify timeout (in seconds) to spend on each function or method",
							TIMEOUT_DEFAULT),
					Configuration.UNBOUND_STRING_ARRAY(IGNORES_CONFIG_OPTION,
							"Specify items (e.g. functions or methods) which should be ignored", IGNORES_DEFAULT));
		}

		@Override
		public List<Descriptor> getCommands() {
			return Collections.EMPTY_LIST;
		}

		@Override
		public Command initialise(Command.Environment environment) {
			return new QuickCheck(environment, System.out, System.err);
		}

	};

	/**
	 * Provides a generic place to which normal output should be directed. This
	 * should eventually be replaced.
	 */
	private final PrintStream sysout;

	/**
	 * Provides a generic place to which error output should be directed. This
	 * should eventually be replaced.
	 */
	private final PrintStream syserr;

	/**
	 * The environment in which this command is executing
	 */
	private final Command.Environment environment;

	/**
	 * The interpreter instance used for executing code.
	 */
	private ExtendedInterpreter interpreter;

	/**
	 * Cache of previously computed values. This is useful for reducing memory
	 * requirements. Furthermore, it is necessary to ensure that aliasing bugs are
	 * identified.
	 */
	private final HashMap<Type, Domain.Big<RValue>> cache;

	/**
	 * Provides the output chanel for information about the quick check process.
	 */
	private StructuredLogger<LogEntry> logger;

	public QuickCheck(Command.Environment environment, OutputStream sysout, OutputStream syserr) {
		this.environment = environment;
		this.sysout = new PrintStream(sysout);
		this.syserr = new PrintStream(syserr);
		this.cache = new HashMap<>();
		// Default logger just reports up to project logger
		this.logger = new StructuredLogger<LogEntry>() {

			@Override
			public void logTimedMessage(LogEntry result, long time, long memory) {
				environment.getLogger().logTimedMessage(result.toString(), time, memory);
			}

			@Override
			public void logTimedMessage(String msg, long time, long memory) {
				environment.getLogger().logTimedMessage(msg, time, memory);
			}

		};
	}

	@Override
	public Descriptor getDescriptor() {
		return DESCRIPTOR;
	}

	@Override
	public void initialise() {

	}

	@Override
	public void finalise() {

	}

	public void setLogger(StructuredLogger<LogEntry> logger) {
		this.logger = logger;
	}

	@Override
	public boolean execute(Command.Project project, Template template) throws Exception {
		// Extract configuration options
		int minInteger = project.get(Value.Int.class,MIN_CONFIG_OPTION).unwrap().intValue();
		int maxInteger = project.get(Value.Int.class,MAX_CONFIG_OPTION).unwrap().intValue();
		int maxArrayLength = project.get(Value.Int.class,LENGTH_CONFIG_OPTION).unwrap().intValue();
		int maxTypeDepth = project.get(Value.Int.class,DEPTH_CONFIG_OPTION).unwrap().intValue();
		int maxAliasingWidth = project.get(Value.Int.class,WIDTH_CONFIG_OPTION).unwrap().intValue();
		int maxRotationWidth = project.get(Value.Int.class,ROTATION_CONFIG_OPTION).unwrap().intValue();
		int limit = project.get(Value.Int.class,LIMIT_CONFIG_OPTION).unwrap().intValue();
		double samplingRate = project.get(Value.Decimal.class,SAMPLING_CONFIG_OPTION).unwrap().doubleValue();
		long timeout = project.get(Value.Int.class,TIMEOUT_CONFIG_OPTION).unwrap().longValue();
		String[] ignores = toStringArray(project.get(Value.Array.class,IGNORES_CONFIG_OPTION));
		Trie pkg = Trie.fromString(project.get(Value.UTF8.class, Activator.PKGNAME_CONFIG_OPTION).unwrap());
		// Extract command-line options
		Command.Options options = template.getOptions();
		//
		if(options.has("sampling")) {
			samplingRate = options.get("sampling", Double.class);
		}
		if(options.has("limit")) {
			limit = options.get("limit", Integer.class);
		}
		if(options.has("min")) {
			minInteger = options.get("min", Integer.class);
		}
		if(options.has("max")) {
			maxInteger = options.get("max", Integer.class);
		}
		if(options.has("length")) {
			maxArrayLength = options.get("length", Integer.class);
		}
		if(options.has("depth")) {
			maxTypeDepth = options.get("depth", Integer.class);
		}
		if(options.has("timeout")) {
			timeout = options.get("timeout", Integer.class);
		}
		// Specify directory where generated WyIL files are dumped.
		Trie target = Trie.fromString(project.get(Value.UTF8.class, Activator.TARGET_CONFIG_OPTION).unwrap());
		//
		Path.Root binaryRoot = project.getRoot().createRelativeRoot(target);
		//
		if (binaryRoot.exists(pkg, WyilFile.ContentType)) {
			// Yes, it does so reuse it.
			Path.Entry<WyilFile> binary = binaryRoot.get(pkg, WyilFile.ContentType);
			// Read the target wyilfile
			WyilFile wf = binary.read();
			// Construct initial context
			Context context = DEFAULT_CONTEXT.setIntegerRange(minInteger, maxInteger).setArrayLength(maxArrayLength)
					.setTypeDepth(maxTypeDepth).setAliasingWidth(maxAliasingWidth).setLambdaWidth(maxRotationWidth)
					.setIgnores(ignores).setSamplingRate(samplingRate).setSampleMin(limit)
					.setTimeout(timeout);
			logger.logTimedMessage(new Summary(context), 0,0);
			// Perform the check
			boolean OK = check(project, wf, context, template.getArguments());
			//
			if(!OK) {
				// FIXME: this does not seem like a good solution :|
				List<wybs.lang.Build.Task> tasks = project.getTasks();
				// Look for error messages
				for (wybs.lang.Build.Task task : tasks) {
					printSyntacticMarkers(syserr, task.getSources(), task.getTarget());
				}
			}
			// Print out any
			return OK;
		} else {
			return false;
		}
	}

	/**
	 * Print out syntactic markers for all entries in the build graph. This requires
	 * going through all entries, extracting the markers and then printing them.
	 *
	 * <b>NOTE:</b> this method is a copy of
	 * wycc.commands.Build.printSyntacticMarkers which is extended to support
	 * printing of stack frames. However, this is really a temporary solution which
	 * should be replaced in the future with something more generic.
	 *
	 * @param executor
	 * @throws IOException
	 */
	public static void printSyntacticMarkers(PrintStream output, Collection<Path.Entry<?>> sources, Path.Entry<?> target) throws IOException {
		// Extract all syntactic markers from entries in the build graph
		List<SyntacticItem.Marker> items = wycc.commands.Build.extractSyntacticMarkers(target);
		// For each marker, print out error messages appropriately
		for (int i = 0; i != items.size(); ++i) {
			SyntacticItem.Marker marker = items.get(i);
			// Print separator
			for(int k=0;k!=80;++k) {
				output.print("=");
			}
			output.println();
			// Log the error message
			wycc.commands.Build.printSyntacticMarkers(output, sources, marker);
			// FIXME: this whole thing is a complete hack
			if(marker instanceof SyntaxError) {
				SyntaxError err = (SyntaxError) marker;
				Tuple<SyntacticItem> context = err.getContext();
				boolean firstTime=true;
				for(int j=0;j!=context.size();++j) {
					SyntacticItem jth = context.get(j);
					if(jth instanceof StackFrame) {
						StackFrame sf = (StackFrame) jth;
						if(firstTime) {
							output.println("Stack Trace:");
						}
						output.println("--> " + sf.getContext().getQualifiedName().toString() + sf.getArguments());
						firstTime=false;
					}
				}
			}
		}
	}

	public boolean check(Build.Project project, WyilFile parent, Context context, List<String> targets) throws IOException {
		// Initialise Interpreter
		this.interpreter = new ExtendedInterpreter(this.syserr, context);
		// Construct extended context
		ExtendedContext eContext = interpreter.getExtendedContext();
		// Initialise by context
		if(eContext.initialise(project,parent)) {
			//
			return check(parent, eContext, targets);
		} else {
			return false;
		}
	}

	public boolean check(WyilFile parent, ExtendedContext context, List<String> targets) throws IOException {
		boolean OK = true;
		if(targets.isEmpty()) {
			for (Decl.Unit unit : parent.getModule().getUnits()) {
				OK &= check(parent,unit, context);
			}
		} else {
			for (Decl.Named d : toNamedDeclarations(parent,targets)) {
				OK &= check(d, parent, context);
			}
		}
		return OK;
	}

	private boolean check(WyilFile parent, Decl.Unit unit, ExtendedContext context) throws IOException {
		boolean OK = true;
		//
		for (Decl d : unit.getDeclarations()) {
			if (d instanceof Decl.Named) {
				OK &= check((Decl.Named) d, parent, context);
			}
		}

		return OK;
	}

	private boolean check(Decl.Named d, WyilFile parent, ExtendedContext context) throws IOException {
		// Check whether declaration should be checked or not
		if(context.isIgnored(d)) {
			// No, this declaration was explicitly marked as ignored.
			logger.logTimedMessage(new Skipped(d), 0,0);
			return true;
		} else {
			// Yes, this declaration should be checked.
			Runtime runtime = Runtime.getRuntime();
			long time = System.currentTimeMillis();
			long memory = runtime.freeMemory();
			try {
				switch (d.getOpcode()) {
				case DECL_method:
				case DECL_function:
					return check((Decl.FunctionOrMethod) d, parent, context);
				case DECL_rectype:
				case DECL_type:
					return check((Decl.Type) d, context);
				}
				return true;
			} catch (Interpreter.TimeoutException e) {
				// Done
				time = System.currentTimeMillis() - time;
				memory = memory - runtime.freeMemory();
				logger.logTimedMessage(new Timeout(d,context.getTimeout()), time, memory);
				return false;
			} catch(Throwable t) {
				t.printStackTrace();
				time = System.currentTimeMillis() - time;
				memory = memory - runtime.freeMemory();
				logger.logTimedMessage(new InternalFailure(d,t), time, memory);
				return false;
			}
		}
	}

	/**
	 * Apply quick check to a given function or method. First, we generate inputs
	 * which meet the preconditions of the function or method in question. Second,
	 * we execute the function in question over the given inputs and observe any
	 * runtime faults arising.
	 *
	 * @param fm
	 * @param parent
	 * @param context
	 * @return
	 * @throws IOException
	 */
	private boolean check(Decl.FunctionOrMethod fm, WyilFile parent, ExtendedContext context) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long time = System.currentTimeMillis();
		long memory = runtime.freeMemory();
		// Set default result
		boolean result = true;
		// Get appropriate generators for each parameter
		Domain.Big<RValue>[] generators = constructGenerators(fm.getParameters(), context);
		//
		List<RValue[]> inputs = generateValidInputs(fm.getRequires(), fm.getParameters(), context, generators);
		//
		double total = calculateTotalInputs(generators);
		long split = System.currentTimeMillis()-time;
		//
		CallStack frame = context.getFrame().clone();
		//
		for(RValue[] args : inputs) {
			// Invoke the method!!
			if (!execute(parent, fm.getQualifiedName(), fm.getType(), frame.clone(), args)) {
				// Failed, so exit early
				result=false;
				break;
			}
		}
		time = System.currentTimeMillis() - time;
		memory = memory - runtime.freeMemory();
		//
		logger.logTimedMessage(new Result(fm, result, inputs.size(), total, split), time, memory);
		//
		return result;
	}

	private boolean execute(WyilFile context, QualifiedName name, Type.Callable signature, CallStack frame, RValue... args) throws IOException {
		// FIXME: approach for constructing stack frame inefficient!
		//
		try {
			interpreter.execute(name, signature, frame, args);
			//
			return true;
		} catch (Interpreter.RuntimeError e) {
			// FIXME: there is a known problem here because the stack frame will produce the
			// current values for the parameters, not their actual values on entry. The
			// easiest way to fix this is to prevent assignments to parameters!!
			//
			// Extract stack frame information
			StackFrame[] errorframe = e.getFrame().toStackFrame();
			// Add appropriate syntax error to the syntactic item where the error arose.
			ErrorMessages.syntaxError(e.getElement(), e.getErrorCode(), errorframe);
			// Done
			return false;
		}
	}

	private boolean check(Decl.Type t, ExtendedContext context) {
		Runtime runtime = Runtime.getRuntime();
		long time = System.currentTimeMillis();
		long memory = runtime.freeMemory();
		//
		CallStack frame = context.getFrame().enter(t);
		// Get an appropriate generator for the underlying type
		Domain.Big<RValue> generator = constructGenerator(t.getType(), context);
		// Record split time
		long split = System.currentTimeMillis() - time;
		// iterate through all values in the generator to see whether any pass the
		// invariant and, hence, are valid instances of this invariant.
		Domain.Small<RValue> domain = generateValidInputs(t.getInvariant(), t.getVariableDeclaration(), generator, context, frame);
 		//
		time = System.currentTimeMillis() - time;
		memory = memory - runtime.freeMemory();
		//
		logger.logTimedMessage(new Result(t, true, domain.bigSize(), generator.bigSize(), split), time, memory);
		//
		return domain.size() > 0;
	}

	/**
	 * Generate the valid inputs for a given set of predicates. This is called to
	 * determine the inputs for a given function or method. Inputs which cause
	 * faults (e.g. out-of-bounds errors) are discarded as these are of no interest.
	 * In other words, valid inputs must be defined.
	 *
	 * @param predicate
	 * @param variable
	 * @param domain
	 * @param frame
	 * @return
	 */
	private Domain.Small<RValue> generateValidInputs(Tuple<Expr> predicate, Decl.Variable variable, Domain.Big<RValue> domain, ExtendedContext context, CallStack frame) {
		ArrayList<RValue> results = new ArrayList<>();
		// Sample domain (if requested)
		domain = applySamplingAsNecessary(domain,context);
		//
		for(RValue input : domain) {
			try {
				// Construct the stack frame
				frame.putLocal(variable.getName(), input);
				// execute invariant
				if(execute(predicate,frame)) {
					results.add(input);
				}
			} catch(Interpreter.RuntimeError e) {

			}
		}
		// FIXME: not very efficient?
		return Domains.Finite(results.toArray(new RValue[results.size()]));
	}

	/**
	 * Generate the valid inputs for a given set of predicates. This is called to
	 * determine the inputs for a given function or method. Inputs which cause
	 * faults (e.g. out-of-bounds errors) are discarded as these are of no interest.
	 * In other words, valid inputs must be defined.
	 *
	 * @param predicate
	 * @param variables
	 * @param context
	 * @param generators
	 * @return
	 */
	private List<RValue[]> generateValidInputs(Tuple<Expr> predicate, Tuple<Decl.Variable> variables,
			ExtendedContext context, Domain.Big<RValue>... generators) {
		//
		ArrayList<RValue[]> results = new ArrayList<>();
		// Sanity check inputs
		if(variables.size() != generators.length) {
			throw new IllegalArgumentException("invalid number of generators");
		} else if(generators.length == 0) {
			// A special case arises when there are no generators. In this case, we generate
			// an empty input sequence to force execution. Without this, functions or
			// methods without parameters are never tested.
			results.add(new RValue[0]);
			return results;
		}
		Domain.Big<RValue[]> domain = Domains.Product(generators);
		// Sample domain (if requested)
		domain = applySamplingAsNecessary(domain,context);
		//
		CallStack frame = context.getFrame();
		//
		for (RValue[] inputs : domain) {
			try {
				// Construct the stack frame
				for (int j = 0; j != inputs.length; ++j) {
					// update environment
					frame.putLocal(variables.get(j).getName(), inputs[j]);
				}
				// execute invariant
				if (execute(predicate, frame)) {
					results.add(inputs);
				}
			} catch (Interpreter.RuntimeError e) {
			}
		}
		//
		return results;
	}

	/**
	 * Execute a sequence of boolean expressions in a given context. If one fails,
	 * then execution is terminated accordingly.
	 *
	 * @param predicate
	 * @param frame
	 * @return
	 */
	private boolean execute(Tuple<Expr> predicate, CallStack frame) {
		for (int i = 0; i != predicate.size(); ++i) {
			if (!execute(predicate.get(i), frame)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Execute a given boolean expression in a given context.
	 *
	 * @param predicate
	 * @param frame
	 * @return
	 */
	private boolean execute(Expr predicate, CallStack frame) {
		RValue.Bool b = interpreter.executeExpression(RValue.Bool.class,predicate,frame);
		return b.boolValue();
	}

	private Domain.Big<RValue>[] constructGenerators(Tuple<Decl.Variable> parameters, ExtendedContext context) {
		Domain.Big<RValue>[] generators = new Domain.Big[parameters.size()];
		//
		for (int i = 0; i != parameters.size(); ++i) {
			generators[i] = constructGenerator(parameters.get(i).getType(), context);
		}
		return generators;
	}

	/**
	 * Construct a generator (i.e. domain) for a given type. This is responsible for
	 * enumerating elements of that type within some finite bounds. Observe that
	 * this caches the constructed domains for two reasons: firstly, to save memory;
	 * secondly, to ensure possible aliasing between reference types.
	 *
	 * @param type
	 * @param context
	 * @return
	 */
	private Domain.Big<RValue> constructGenerator(Type type, ExtendedContext context) {
		Domain.Big<RValue> result = cache.get(type);
		if (result == null) {
			switch (type.getOpcode()) {
			case TYPE_null:
				result = constructGenerator((Type.Null) type, context);
				break;
			case TYPE_bool:
				result = constructGenerator((Type.Bool) type, context);
				break;
			case TYPE_byte:
				result = constructGenerator((Type.Byte) type, context);
				break;
			case TYPE_int:
				result = constructGenerator((Type.Int) type, context);
				break;
			case TYPE_array:
				result = constructGenerator((Type.Array) type, context);
				break;
			case TYPE_record:
				result = constructGenerator((Type.Record) type, context);
				break;
			case TYPE_nominal:
				result = constructGenerator((Type.Nominal) type, context);
				break;
			case TYPE_union:
				result = constructGenerator((Type.Union) type, context);
				break;
			case TYPE_variable:
				result = constructGenerator((Type.Variable) type, context);
				break;
			case TYPE_staticreference:
			case TYPE_reference:
				result = constructGenerator((Type.Reference) type, context);
				break;
			case TYPE_function:
				result = constructGenerator((Type.Function) type, context);
				break;
			case TYPE_method:
				result = constructGenerator((Type.Method) type, context);
				break;
			case TYPE_property:
			default:
				// NOTE: this should be dead code.
				throw new RuntimeException("unknown type encountered (" + type + ")");
			}
			// Store the computed result in the cache. This is important to reduce memory
			// footprints. In some cases, this reduction can be quite significant.
			cache.put(type,result);
		}
		return result;
	}

	private Domain.Small<RValue> constructGenerator(Type.Null type, ExtendedContext context) {
		return Domains.Finite(RValue.Null);
	}

	private Domain.Small<RValue> constructGenerator(Type.Bool type, ExtendedContext context) {
		return Domains.Finite(RValue.False, RValue.True);
	}

	private Domain.Small<RValue> constructGenerator(Type.Int type, ExtendedContext context) {
		return new AbstractSmallDomain.Adaptor<RValue, Integer>(
				Domains.Int(context.getIntegerMinimum(), context.getIntegerMaximum())) {
			@Override
			public RValue.Int get(Integer i) {
				return RValue.Int(BigInteger.valueOf(i));
			}
		};
	}

	private Domain.Small<RValue> constructGenerator(Type.Byte type, ExtendedContext context) {
		return new AbstractSmallDomain.Adaptor<RValue,Integer>(Domains.Int(context.getIntegerMinimum(), context.getIntegerMaximum())) {
			@Override
			public RValue.Byte get(Integer value) {
				return RValue.Byte((byte) (int) value);
			}
		};
	}

	private Domain.Big<RValue> constructGenerator(Type.Array type, ExtendedContext context) {
		// Construct domain for elements
		Domain.Big<RValue> element = constructGenerator(type.getElement(), context);
		// Construct domain for between 0 and max elements
		Domain.Big<RValue[]> array = Domains.Array(0, context.getMaxArrayLength(), element);
		// Adapt array to RValue.Array
		return Domains.Adaptor(array, (vs) -> RValue.Array(vs));
	}

	private Domain.Big<RValue> constructGenerator(Type.Record type, ExtendedContext context) {
		// FIXME: need to support open records!
		Tuple<Type.Field> fields = type.getFields();
		Domain.Big<RValue>[] generators = new Domain.Big[fields.size()];
		// Construct a generator for each field
		for(int i=0;i!=fields.size();++i) {
			generators[i] = constructGenerator(fields.get(i).getType(), context);
		}
		//
		return Domains.Adaptor(Domains.Product(generators), (vals) -> {
			Tuple<Type.Field> typeFields = type.getFields();
			RValue.Field[] rfields = new RValue.Field[vals.length];
			//
			for (int i = 0; i != rfields.length; ++i) {
				rfields[i] = RValue.Field(typeFields.get(i).getName(), vals[i]);
			}
			// Done
			return RValue.Record(rfields);
		});
	}

	private Domain.Big<RValue> constructGenerator(Type.Nominal type, ExtendedContext context) {
		Decl.Type decl = type.getLink().getTarget();
		int depth = context.depth(decl);
		//
		if (depth == context.getRecursiveTypeDepth()) {
			// NOTE: we've reached the maximum depth to explore for recursive types.
			// Therefore, we simply return the empty domain.
			return Domains.EMPTY;
		} else {
			// =============================================================================
			// FIXME: we're not using any form of cache here which could potentially improve
			// performance dramatically. A key aspect is that this must be for the concrete
			// type, to ensure that type parameters are included. Furthermore, handling of
			// recursive types needs to be done with care.
			// =============================================================================
			context.enter(decl);
			// Get an appropriate generator for the underlying type/
			Domain.Big<RValue> generator = constructGenerator(type.getConcreteType(), context);
			//
			CallStack frame = context.getFrame().enter(decl);
			// iterate through all values in the generator to see whether any pass the
			// invariant and, hence, are valid instances of this invariant.
			Domain.Big<RValue> domain = generateValidInputs(decl.getInvariant(), decl.getVariableDeclaration(), generator, context, frame);
			//
			context.leave(decl);
			//
			return domain;
		}
	}

	private Domain.Big<RValue> constructGenerator(Type.Union type, ExtendedContext context) {
		// Construct generators for each subtype
		Domain.Big<RValue>[] generators = new Domain.Big[type.size()];
		for(int i=0;i!=type.size();++i) {
			generators[i] = constructGenerator(type.get(i), context);
		}
		// Return the union of all domains
		return Domains.Union(generators);
	}

	/**
	 * Construct a generator for a reference type. This is a bit challenging because
	 * we want to ensure that the *same* references are always returned from this
	 * domain. This sets up the possibility of aliasing.
	 *
	 * @param type
	 * @param context
	 * @return
	 */
	private Domain.Big<RValue> constructGenerator(Type.Reference type, ExtendedContext context) {
		int width = context.getAliasingWidth();
		// NOTE: this is not done lazily which potentially could be problematic if
		// sampling was used.
		Domain.Big<RValue> element = constructGenerator(type.getElement(), context);
		// Construct a unique object for each possible element value.
		RValue.Reference[] refs = new RValue.Reference[element.bigSize().intValue() * width];
		// Construct our set of references.
		int i = 0;
		for (RValue e : element) {
			for (int j = 0; j != width; ++j) {
				// Construct multiple references to a cell with the same value. This has the
				// effect of ensuring the possibility of references to different cells which
				// hold the same value.
				refs[(i * width) + j] = RValue.Reference(RValue.Cell(e));
			}
			i = i + 1;
		}
		//
		return Domains.Finite(refs);
	}

	/**
	 * <p>
	 * Construct a generator for a function type. This is tricky because it relies
	 * on synthesising a function somehow. In essence, we need to generate mappings
	 * from input values to output values. This is very expensive in general. There
	 * are some strategies that can help. For example, we can explore all known
	 * functions and look for matches.
	 * </p>
	 * <p>
	 * To synthesize a function, inputs are mapped to outputs in a linear fashion.
	 * That is, the first possible input is mapped to the first possible output, and
	 * so on. A key factor is that we need to ensure the same outputs are returned
	 * for the same inputs.
	 * </p>
	 *
	 * @param type
	 * @param context
	 * @return
	 */
	private Domain.Big<RValue> constructGenerator(Type.Function type, ExtendedContext context) {
		Domain.Big<RValue> output = constructGenerator(type.getReturn(),context);
		RValue[] lambdas = new RValue[context.getLambdaWidth()];
		for(int i=0;i!=context.getLambdaWidth();++i) {
			// Apply the rotation (for i > 1)
			Domain.Big<RValue> tmp = i == 0 ? output : Rotate(output,i);
			// Construct the (deterministic) lambda
			lambdas[i] = new SynthesizedLambda(type,tmp);
		}
		return Domains.Finite(lambdas);
	}

	/**
	 * Construct a generator for a method type.
	 *
	 * @param type
	 * @param context
	 * @return
	 */
	private Domain.Big<RValue> constructGenerator(Type.Method type, ExtendedContext context) {
		Domain.Big<RValue> output = constructGenerator(type.getReturn(),context);
		RValue[] lambdas = new RValue[context.getLambdaWidth()];
		// FIXME: should make this non-deterministic!!
		for(int i=0;i!=context.getLambdaWidth();++i) {
			// Apply the rotation (for i > 1)
			Domain.Big<RValue> tmp = i == 0 ? output : Rotate(output,i);
			// Construct the (deterministic) lambda
			lambdas[i] = new SynthesizedLambda(type,tmp);
		}
		return Domains.Finite(lambdas);

	}

	/**
	 * construct a generator from a tuple of types.
	 *
	 * @param types
	 * @param context
	 * @return
	 */
	private Domain.Big<RValue[]> constructGenerator(Tuple<Type> types, ExtendedContext context) {
		Domain.Big<RValue>[] generators = new Domain.Big[types.size()];
		// Construct a generator for each type
		for(int i=0;i!=types.size();++i) {
			generators[i] = constructGenerator(types.get(i),context);
		}
		// Construct the product of generators
		return Domains.Product(generators);
	}

	private Domain.Big<RValue> constructGenerator(Type.Variable type, ExtendedContext context) {
		return Domains.Adaptor(Domains.Int(context.getIntegerMinimum(), context.getIntegerMaximum()),
				(i) -> RValue.Int(BigInteger.valueOf(i)));
	}

	private final static <T> Domain.Big<T> Rotate(Domain.Big<T> domain, int rotation) {
		if (domain instanceof Domain.Small) {
			Domain.Small<T> small = (Domain.Small) domain;
			final long size = small.size();

			return new AbstractSmallDomain<T>() {

				@Override
				public long size() {
					return size;
				}

				@Override
				public T get(long index) {
					// apply rotation
					index = (index + rotation) % size;
					//
					return small.get(index);
				}
			};
		} else {
			throw new UnsupportedOperationException("implement me");
		}
	}

	private final class SynthesizedLambda extends RValue.Lambda {
		private final Type.Callable type;
		private final ArrayList<RValue[]> inputs;
		private final Domain.Big<RValue> outputs;
		private final long size;

		public SynthesizedLambda(Type.Callable type, Domain.Big<RValue> outputs) {
			this.type = type;
			this.inputs = new ArrayList<>();
			this.outputs = outputs;
			this.size = outputs.bigSize().longValue();
		}

		@Override
		public RValue execute(Interpreter interpreter, RValue[] arguments, SyntacticItem context) {
			// Check through previous memoizations
			for(int i=0;i!=inputs.size();++i) {
				if(Arrays.equals(arguments, inputs.get(i))) {
					return outputs.get(BigInteger.valueOf(i % size));
				}
			}
			// Not matched so create a new one
			int r = inputs.size();
			inputs.add(arguments);
			return outputs.get(BigInteger.valueOf(r % size));
		}

		@Override
		public Callable getType() {
			return type;
		}

		@Override
		public Value toValue() {
			throw new UnsupportedOperationException();
		}
	}

	private static <T> Domain.Big<T> applySamplingAsNecessary(Domain.Big<T> domain, Context context) {
		if (!context.getSamplingRate().equals(BigDecimal.ONE)) {
			// Apply sampling
			BigInteger size = domain.bigSize();
			int k = context.getSampleSize(size);
			// NOTE: use approximate algorithm here as, otherwise, we get stuck generating
			// the sample.
			domain = Domains.FastApproximateSample(domain, k);
		}
		return domain;
	}

	private static double calculateTotalInputs(Domain.Big<?>... domains) {
		double max = 1.0D;
		for (int i = 0; i != domains.length; ++i) {
			max = max * domains[i].bigSize().doubleValue();
		}
		//
		return max;
	}

	/**
	 * Format the name and signature of a named declaration in a nice fashion for
	 * writing to the log file.
	 *
	 * @param d
	 * @return
	 */
	private static String toNameString(Decl.Named<?> d) {
		String kind;
		String rest;
		if(d instanceof Decl.Function) {
			Type.Callable t = ((Decl.Function)d).getType();
			kind = "function";
			rest = d.getQualifiedName().toString() + t.getParameter() + "->" + t.getReturn();
		} else if(d instanceof Decl.Method) {
			Decl.Method m = (Decl.Method) d;
			Type.Method t = m.getType();
			// FIXME: this needs to be improved!
			kind = "method";
			rest = m.getQualifiedName() + toMethodParametersString(t.getLifetimeParameters(),m.getTemplate()) + t.getParameter() + "->" + t.getReturn();

		} else if(d instanceof Decl.Type) {
			kind = "type";
			rest = ((Decl.Type)d).getQualifiedName().toString();
		} else {
			throw new RuntimeException("unknown declaration encountered: " + d);
		}
		// Remove all whitespace
		rest = rest.replace(" ", "_");
		//
		return kind + " " + rest;
	}

	private static String toNameString(Decl.Named<?> d, Tuple<Type> parameters) {
		String name = toNameString(d);
		if(parameters.size() > 0) {
			return name + "<" + parameters.toBareString() + ">";
		} else {
			return name;
		}
	}

	private static String toMethodParametersString(Tuple<? extends SyntacticItem> lifetimes, Tuple<? extends SyntacticItem> variables) {
		if(lifetimes.size() == 0 && variables.size() == 0) {
			return "";
		} else if(lifetimes.size() > 0) {
			return "<" + lifetimes.toBareString() + ">";
		} else  if(variables.size() > 0) {
			return "<" + variables.toBareString() + ">";
		} else {
			return "<" + lifetimes.toBareString() + ", " + variables.toBareString() + ">";
		}
	}

	/**
	 * Convert a bunch of compilation unit names into the corresponding
	 * declarations.
	 *
	 * @param parent
	 * @param names
	 * @return
	 */
	private List<Decl.Named> toNamedDeclarations(WyilFile parent, List<String> names) {
		HashSet<String> visited = new HashSet<>(names);
		ArrayList<Decl.Named> decls = new ArrayList<>();
		Decl.Module module = parent.getModule();
		// Add all declarations listed
		for (Decl.Unit unit : module.getUnits()) {
			CompilationUnit.Name un = unit.getName();
			for (Decl d : unit.getDeclarations()) {
				if (d instanceof Decl.Named) {
					Decl.Named n = (Decl.Named) d;
					String qn = n.getQualifiedName().toString();
					if (visited.contains(qn)) {
						decls.add(n);
					}
				}
			}
		}
		// Sanity check we got them all
		for(Decl.Named n : decls) {
			visited.remove(n.getQualifiedName().toString());
		}
		//
		for(String name : visited) {
			logger.logTimedMessage("declaration not found: " + name, 0, 0);
		}
		return decls;
	}

	/**
	 * Convert a value array into a string array.
	 *
	 * @param array
	 * @return
	 */
	public static String[] toStringArray(Value.Array array) {
		String[] items = new String[array.size()];
		for(int i=0;i!=array.size();++i) {
			items[i] = array.get(i).toString();
		}
		return items;
	}

	/**
	 * Provides various mechanisms for controlling the construction of generators,
	 * such as limiting the maximum depth of recursive types.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Context {
		private int min;
		private int max;
		private int length;
		private int depth;
		private int width;
		private int rotation;
		private BigDecimal samplingRate;
		private int sampleMin;
		private int sampleMax;
		private String[] ignores;
		private long timeout;

		private Context(int minInt, int maxInt, int maxLen, int maxDepth, int width, int rotation, String[] ignores,
				BigDecimal samplingRate, int sampleMin, int sampleMax, long timeout) {
			this.min = minInt;
			this.max = maxInt;
			this.length = maxLen;
			this.depth = maxDepth;
			this.width = width;
			this.rotation = rotation;
			this.ignores = ignores;
			this.samplingRate = samplingRate;
			this.sampleMin = sampleMin;
			this.sampleMax = sampleMax;
			this.timeout = timeout;
		}

		private Context(Context context) {
			this.min = context.min;
			this.max = context.max;
			this.length = context.length;
			this.depth = context.depth;
			this.width = context.width;
			this.rotation = context.rotation;
			this.ignores = context.ignores;
			this.samplingRate = context.samplingRate;
			this.sampleMin = context.sampleMin;
			this.sampleMax = context.sampleMax;
			this.timeout = context.timeout;
		}

		public int getIntegerMinimum() {
			return min;
		}

		public int getIntegerMaximum() {
			return max;
		}

		public Context setIntegerRange(int min, int max) {
			Context context = new Context(this);
			context.min = min;
			context.max = max;
			return context;
		}

		public int getMaxArrayLength() {
			return length;
		}

		public Context setArrayLength(int length) {
			Context context = new Context(this);
			context.length = length;
			return context;
		}

		public int getRecursiveTypeDepth() {
			return depth;
		}

		public Context setTypeDepth(int depth) {
			Context context = new Context(this);
			context.depth = depth;
			return context;
		}

		public int getAliasingWidth() {
			return width;
		}

		public Context setAliasingWidth(int width) {
			Context context = new Context(this);
			context.width = width;
			return context;
		}

		public int getLambdaWidth() {
			return rotation;
		}

		public Context setLambdaWidth(int width) {
			Context context = new Context(this);
			context.rotation = width;
			return context;
		}

		public String[] getIgnores() {
			return ignores;
		}

		public boolean isIgnored(Decl.Named decl) {
			String s = decl.getQualifiedName().toString();
			for(int i=0;i!=ignores.length;++i) {
				if(s.endsWith(ignores[i])) {
					return true;
				}
			}
			return false;
		}

		public Context setIgnores(String... ignores) {
			Context context = new Context(this);
			context.ignores = ignores;
			return context;
		}

		public BigDecimal getSamplingRate() {
			return samplingRate;
		}

		public Context setSamplingRate(double rate) {
			Context context = new Context(this);
			context.samplingRate = new BigDecimal(rate);
			return context;
		}

		public Context setSampleMin(int limit) {
			Context context = new Context(this);
			context.sampleMin = limit;
			return context;
		}

		public Context setSampleMax(int limit) {
			Context context = new Context(this);
			context.sampleMax = limit;
			return context;
		}

		public long getSampleMin() {
			return sampleMin;
		}

		public int getSampleMax() {
			return sampleMax;
		}

		public int getSampleSize(BigInteger size) {
			BigInteger k = samplingRate.multiply(new BigDecimal(size)).toBigInteger();
			// Ensure *at least* some number of samples
			k = k.max(BigInteger.valueOf(Math.min(size.longValue(), sampleMin)));
			// Ensure *at most* some number of samples
			k = k.min(BigInteger.valueOf(sampleMax));
			// Done
			return k.intValue();
		}

		public long getTimeout() {
			return timeout;
		}

		public Context setTimeout(long timeout) {
			Context context = new Context(this);
			context.timeout = timeout;
			return context;
		}
	}

	private static class ExtendedContext extends Context {
		// Stores the based frame
		private HashMap<Decl,Integer> depths = new HashMap<>();
		private final CallStack frame;
		private final long timeoutMillis;

		public ExtendedContext(CallStack frame, Context context) {
			super(context);
			this.frame = frame;
			//
			long timeout = context.getTimeout();
			//
			if(timeout != Long.MAX_VALUE) {
				timeoutMillis = timeout * 1000;
			} else {
				timeoutMillis = Long.MAX_VALUE;
			}
			//
		}

		public CallStack getFrame() {
			return frame.setTimeout(timeoutMillis);
		}

		public long getTimeoutMillis() {
			return timeoutMillis;
		}

		/**
		 * Initialise a base stack frame from for this project, such that it can be used
		 * to execute functions and methods within the project. This includes all
		 * modules which this project depends upon.
		 */
		public boolean initialise(Build.Project project, WyilFile context) throws IOException {
			try {
				// Load all relevant modules
				frame.load(context);
			} catch (Interpreter.RuntimeError e) {
				// Add appropriate syntax error to the syntactic item where the error arose.
				ErrorMessages.syntaxError(e.getElement(), e.getErrorCode());
				// Done
				return false;
			}
			// Load all dependencies
			for(Build.Package p : project.getPackages()) {
				// FIXME: is this the right way to determine the binary file from a given
				// package?
				List<Path.Entry<WyilFile>> entries = p.getRoot().get(Content.filter("**/*", WyilFile.ContentType));
				//
				for(Path.Entry<WyilFile> e : entries) {
					frame.load(e.read());
				}
			}
			return true;
		}


		public int depth(Decl decl) {
			Integer depth = depths.get(decl);
			if(depth == null) {
				return 0;
			} else {
				return depth;
			}
		}

		public void enter(Decl decl) {
			Integer depth = depths.get(decl);
			int d = depth == null ? 0 : depth;
			depths.put(decl, d+1);
		}

		public void leave(Decl decl) {
			Integer depth = depths.get(decl);
			int d = depth == null ? 0 : depth;
			depths.put(decl, d-1);
		}
	}

	private class ExtendedInterpreter extends Interpreter {
		private final ExtendedContext context;

		public ExtendedInterpreter(PrintStream debug, Context context) {
			super(debug);
			this.context = new ExtendedContext(new CallStack(),context);
		}

		public ExtendedContext getExtendedContext() {
			return context;
		}

		@Override
		public RValue execute(Decl.Callable lambda, CallStack frame, RValue[] args, SyntacticItem item) {
			if (lambda instanceof Decl.Method && lambda.getBody().size() == 0) {
				Domain.Big<RValue> returns = constructGenerator(lambda.getType().getReturn(), context);
				// FIXME: could return randomly here
				return returns.iterator().next();
			} else {
				return super.execute(lambda, frame, args, item);
			}
		}
	}

	public static interface StructuredLogger<T> {
		public void logTimedMessage(T result, long time, long memory);
		public void logTimedMessage(String msg, long time, long memory);
	}

	public static interface LogEntry {

	}

	/**
	 * A QuickCheck result is generate for each
	 * @author David J. Pearce
	 *
	 */
	public static abstract class AbstractLogEntry implements LogEntry {
		protected final Decl.Named item;

		public AbstractLogEntry(Decl.Named item) {
			this.item = item;
		}

		public Decl.Named getItem() {
			return item;
		}
	}

	/**
	 * Indicates that the given item was skipped.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Skipped extends AbstractLogEntry {
		public Skipped(Decl.Named item) {
			super(item);
		}

		@Override
		public String toString() {
			return "Skipped " + toNameString(item);
		}
	}

	/**
	 * Indicates that the given item caused some kind of internal failure (e.g. a
	 * stack overflow, etc).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class InternalFailure extends AbstractLogEntry {
		private final Throwable ex;

		public InternalFailure(Decl.Named item, Throwable ex) {
			super(item);
			this.ex = ex;
		}

		@Override
		public String toString() {
			return "Failure(" + ex.getClass().getSimpleName() + ", " + ex.getMessage() + ") "+ toNameString(item);
		}

		public Throwable getThrowable() {
			return ex;
		}
	}

	public static class Timeout extends AbstractLogEntry {
		private final long timeout;

		public Timeout(Decl.Named item, long timeout) {
			super(item);
			this.timeout = timeout;
		}

		@Override
		public String toString() {
			return "Timeout(" + timeout + "s) " + toNameString(item);
		}
	}

	/**
	 * Indicates that a QuickCheck result was obtained for a given named declaration
	 * (e.g. a function or type declaration).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Result extends AbstractLogEntry {
		/**
		 * Indicates whether check successful or not. That is, whether or not some kind
		 * of violation was detected.
		 */
		private boolean success;
		/**
		 * Indicates the total number of inputs generated.
		 */
		private double total;
		/**
		 * Indicates the total number of inputs checked.
		 */
		private double checked;

		/**
		 * Indicates the time taken to generate the inputs.
		 */
		private long split;

		public Result(Decl.Named n, boolean success, BigInteger checked, BigInteger total, long split) {
			super(n);
			this.success = success;
			this.checked = checked.doubleValue();
			this.total = total.doubleValue();
			this.split = split;
		}

		public Result(Decl.Named n, boolean success, double checked, double total, long split) {
			super(n);
			this.success = success;
			this.checked = checked;
			this.total = total;
			this.split = split;
		}

		/**
		 * Check whether any problems were encountered during the run.
		 *
		 * @return
		 */
		public boolean isChecked() {
			return success;
		}

		/**
		 * Get total space of inputs determined for this run.
		 *
		 * @return
		 */
		public double getTotalInputs() {
			return total;
		}

		/**
		 * Get number of inputs actually used for checking. This will be lower that the
		 * total in the cases where some inputs don't meet preconditions or type
		 * invariants, etc.
		 *
		 * @return
		 */
		public double getCheckedInputs() {
			return checked;
		}
		@Override
		public String toString() {
			String label = success ? "Checked " : "Failed ";
			double percent = total == 0.0D ? 0 : (checked * 100) / total;
			return label + toNameString(item) + " (" + checked + "/" + total + "=" + percent +"%, " + split + "ms)";
		}
	}

	/**
	 * Provides summary information on the QuickCheck configuration being used.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Summary implements LogEntry {
		private final Context context;
		public Summary(Context context) {
			this.context = context;
		}
		@Override
		public String toString() {
			return "Check configuration has ints (" + context.getIntegerMinimum() + ".." + context.getIntegerMaximum()
					+ "), array lengths (max " + context.getMaxArrayLength() + "), type depths (max "
					+ context.getRecursiveTypeDepth() + "), sampling (" + context.getSamplingRate() + ", limits "
					+ context.getSampleMin() + ".." + context.getSampleMax() + ")";
		}
	}
}
