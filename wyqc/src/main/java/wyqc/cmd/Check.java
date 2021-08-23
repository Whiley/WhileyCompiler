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
package wyqc.cmd;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

import wycc.lang.Build.Repository;
import wycc.util.AbstractCompilationUnit.Value;
import wycc.util.Trie;
import wyc.Activator;
import wycli.cfg.Configuration;
import wycli.cfg.Configuration.Schema;
import wycli.commands.BuildCmd;
import wycli.lang.Command;
import wyil.lang.WyilFile;
import static wyqc.lang.QuickCheck.DEFAULT_CONTEXT;
import wyqc.lang.QuickCheck;

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
public class Check implements Command {
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
			return Collections.emptyList();
		}

		@Override
		public Command initialise(Command.Environment environment) {
			return new Check(environment, System.out, System.err);
		}

	};

	/**
	 * Provides a generic place to which error output should be directed. This
	 * should eventually be replaced.
	 */
	private final PrintStream syserr;
	/**
	 * The enclosing project for this build
	 */
	private final Command.Environment environment;

	public Check(Command.Environment environment, OutputStream sysout, OutputStream syserr) {
		this.environment = environment;
		this.syserr = new PrintStream(syserr);
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

	@Override
	public boolean execute(Trie path, Template template) throws Exception {
		Repository repository = environment.getRepository();
		Configuration configuration = environment.get(path);
		// Extract configuration options
		int minInteger = configuration.get(Value.Int.class,MIN_CONFIG_OPTION).unwrap().intValue();
		int maxInteger = configuration.get(Value.Int.class,MAX_CONFIG_OPTION).unwrap().intValue();
		int maxArrayLength = configuration.get(Value.Int.class,LENGTH_CONFIG_OPTION).unwrap().intValue();
		int maxTypeDepth = configuration.get(Value.Int.class,DEPTH_CONFIG_OPTION).unwrap().intValue();
		int maxAliasingWidth = configuration.get(Value.Int.class,WIDTH_CONFIG_OPTION).unwrap().intValue();
		int maxRotationWidth = configuration.get(Value.Int.class,ROTATION_CONFIG_OPTION).unwrap().intValue();
		int limit = configuration.get(Value.Int.class,LIMIT_CONFIG_OPTION).unwrap().intValue();
		double samplingRate = configuration.get(Value.Decimal.class,SAMPLING_CONFIG_OPTION).unwrap().doubleValue();
		long timeout = configuration.get(Value.Int.class,TIMEOUT_CONFIG_OPTION).unwrap().longValue();
		String[] ignores = toStringArray(configuration.get(Value.Array.class,IGNORES_CONFIG_OPTION));
		Trie pkg = Trie.fromString(configuration.get(Value.UTF8.class, Activator.PACKAGE_NAME).unwrap());
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
		Trie target = Trie.fromString(configuration.get(Value.UTF8.class, Activator.BUILD_WHILEY_TARGET).unwrap());
		//
		WyilFile binary = repository.get(WyilFile.ContentType,target.append(pkg));

		if (binary != null) {
			// Construct initial context
			QuickCheck.Context context = QuickCheck.DEFAULT_CONTEXT.setIntegerRange(minInteger, maxInteger).setArrayLength(maxArrayLength)
					.setTypeDepth(maxTypeDepth).setAliasingWidth(maxAliasingWidth).setLambdaWidth(maxRotationWidth)
					.setIgnores(ignores).setSamplingRate(samplingRate).setSampleMin(limit)
					.setTimeout(timeout);
			// Perform the check
			boolean OK = new QuickCheck(environment.getLogger()).check(environment, binary, context, template.getArguments());
			if(!OK) {
				BuildCmd.printSyntacticMarkers(syserr, binary);
			}
//			//
//			if(!OK) {
//				// FIXME: this does not seem like a good solution :|
//
//				// Look for error messages
//
//				for (Build.Task task : tasks) {
//					printSyntacticMarkers(syserr, task.getSources(), task.getTarget());
//				}
//			}
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
	 * @throws IOException
	 */
//	public static void printSyntacticMarkers(PrintStream output, Collection<Trie.Entry<?>> sources, Trie.Entry<?> target) throws IOException {
//		// Extract all syntactic markers from entries in the build graph
//		List<SyntacticItem.Marker> items = wycli.commands.Build.extractSyntacticMarkers(target);
//		// For each marker, print out error messages appropriately
//		for (int i = 0; i != items.size(); ++i) {
//			SyntacticItem.Marker marker = items.get(i);
//			// Print separator
//			for(int k=0;k!=80;++k) {
//				output.print("=");
//			}
//			output.println();
//			// Log the error message
//			wycli.commands.Build.printSyntacticMarkers(output, sources, marker);
//			// FIXME: this whole thing is a complete hack
//			if(marker instanceof SyntaxError) {
//				SyntaxError err = (SyntaxError) marker;
//				Tuple<SyntacticItem> context = err.getContext();
//				boolean firstTime=true;
//				for(int j=0;j!=context.size();++j) {
//					SyntacticItem jth = context.get(j);
//					if(jth instanceof StackFrame) {
//						StackFrame sf = (StackFrame) jth;
//						if(firstTime) {
//							output.println("Stack Trace:");
//						}
//						output.println("--> " + sf.getContext().getQualifiedName().toString() + sf.getArguments());
//						firstTime=false;
//					}
//				}
//			}
//		}
//	}

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

}
