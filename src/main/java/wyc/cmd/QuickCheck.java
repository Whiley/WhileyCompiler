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

import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.function.Predicate;

import jmodelgen.core.Domain;
import jmodelgen.util.Domains;
import wyil.interpreter.*;
import wybs.lang.Build;
import wybs.lang.SyntacticException;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.Activator;
import wyc.util.ErrorMessages;
import wycc.WyProject;
import wycc.cfg.Configuration;
import wycc.cfg.Configuration.Schema;
import wycc.lang.Command;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.interpreter.ConcreteSemantics.RValue;
import wyil.interpreter.Interpreter.CallStack;
import wyil.lang.WyilFile;
import static wyil.lang.WyilFile.*;
import wyil.lang.WyilFile.Decl;

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
			return Collections.EMPTY_LIST;
		}

		@Override
		public Schema getConfigurationSchema() {
			return Configuration.EMPTY_SCHEMA;
		}

		@Override
		public List<Descriptor> getCommands() {
			return Collections.EMPTY_LIST;
		}

		@Override
		public Command initialise(Command environment, Configuration configuration) {
			return new QuickCheck(((WyProject) environment).getBuildProject(), configuration, System.out, System.err);
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
	 * The configuration associated with this command.
	 */
	private final Configuration configuration;

	/**
	 * The enclosing project for this build
	 */
	private final Build.Project project;

	/**
	 * The interpreter instance used for executing code.
	 */
	private final Interpreter interpreter;

	/**
	 * Cache of previously computed values.
	 */
	private final HashMap<Decl,Domain<RValue>> cache;

	public QuickCheck(Build.Project project, Configuration configuration, OutputStream sysout, OutputStream syserr) {
		this.project = project;
		this.configuration = configuration;
		this.sysout = new PrintStream(sysout);
		this.syserr = new PrintStream(syserr);
		this.interpreter = new Interpreter(this.syserr);
		this.cache = new HashMap<>();
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
	public boolean execute(Template template) throws Exception {
		Trie pkg = Trie.fromString(configuration.get(Value.UTF8.class, Activator.PKGNAME_CONFIG_OPTION).unwrap());
		// Specify directory where generated WyIL files are dumped.
		Trie target = Trie.fromString(configuration.get(Value.UTF8.class, Activator.TARGET_CONFIG_OPTION).unwrap());
		//
		Path.Root binaryRoot = project.getRoot().createRelativeRoot(target);
		//
		if (binaryRoot.exists(pkg, WyilFile.ContentType)) {
			// Yes, it does so reuse it.
			Path.Entry<WyilFile> binary = binaryRoot.get(pkg, WyilFile.ContentType);
			// Perform the check
			check(binary.read());
			//
			return true;
		} else {
			return false;
		}
	}

	public void check(WyilFile wf) {
		for (Decl.Unit unit : wf.getModule().getUnits()) {
			check(wf,unit);
		}
	}

	private void check(WyilFile context, Decl.Unit unit) {
		for (Decl d : unit.getDeclarations()) {
			switch (d.getOpcode()) {
			case DECL_function:
			case DECL_method:
				check(context, (Decl.FunctionOrMethod) d);
				break;
			case DECL_type:
				check((Decl.Type) d);
				break;
			}
		}
	}

	private void check(WyilFile context, Decl.FunctionOrMethod fm) {
		System.out.println("Checking " + fm.getName() + " : " + fm.getType() + ".");
		// Get appropriate generators for each parameter
		Domain<RValue>[] generators = constructGenerators(fm.getParameters(), new Context());
		//
		List<RValue[]> inputs = execute(fm.getRequires(), fm.getParameters(), generators);
		//
		System.out.println("Generated " + inputs.size() + " inputs.");
		//
		for(RValue[] args : inputs) {
			// Invoke the method!!
			if (!execute(context, fm.getQualifiedName(), fm.getType(), args)) {
				// Failed, so exit early
				return;
			}
		}

	}

	private boolean execute(WyilFile context, QualifiedName name, Type.Callable signature, RValue... args) {
		// FIXME: approach for constructing stack frame is rather inefficient.

		// Construct a fresh call stack for this execution
		CallStack frame = interpreter.new CallStack();
		// Load all relevant modules
		frame.load(context);
		//
		try {
			interpreter.execute(name, signature, frame, args);
			//
			return true;
		} catch (Interpreter.RuntimeError e) {
			// Add appropriate syntax error to the syntactic item where the error arose.
			ErrorMessages.syntaxError(e.getElement(), e.getErrorCode());
			// Done
			return false;
		} catch(Exception e) {
			// FIXME: this is a temporary hack to help identify situations where the
			// interpreter is not throwing appropriate error messages.
			e.printStackTrace(System.out);
			return true;
		}
	}

	private void check(Decl.Type t) {
		check(t,new Context());
	}

	private void check(Decl.Type t, Context context) {
		// Get an appropriate generator for the underlying type
		Domain<RValue> generator = constructGenerator(t.getType(), context);
		// iterate through all values in the generator to see whether any pass the
		// invariant and, hence, are valid instances of this invariant.
		Domain<RValue> domain = execute(t.getInvariant(), t.getVariableDeclaration(), generator);
		//
		cache.put(t,domain);
	}

	private Domain<RValue> execute(Tuple<Expr> predicate, Decl.Variable variable, Domain<RValue> generator) {
		CallStack frame = interpreter.new CallStack();
		//
		ArrayList<RValue> results = new ArrayList<>();
		//
		for(int i=0;i!=generator.size();++i) {
			RValue input = generator.get(i);
			// Construct the stack frame
			frame.putLocal(variable.getName(), input);
			// execute invariant
			if(execute(predicate,frame)) {
				results.add(input);
			}
		}
		// FIXME: not very efficient?
		return Domains.Finite(results.toArray(new RValue[results.size()]));
	}

	private List<RValue[]> execute(Tuple<Expr> predicate, Tuple<Decl.Variable> variables, Domain<RValue>... generators) {
		if(variables.size() != generators.length) {
			throw new IllegalArgumentException("invalid number of generators");
		}
		DomainIterator iterator = new DomainIterator(generators);
		//
		CallStack frame = interpreter.new CallStack();
		//
		ArrayList<RValue[]> results = new ArrayList<>();
		//
		while(iterator.hasNext()) {
			RValue[] inputs = iterator.next();
			// Construct the stack frame
			for (int i = 0; i != inputs.length; ++i) {
				// update environment
				frame.putLocal(variables.get(i).getName(), inputs[i]);
			}
			// execute invariant
			if(execute(predicate,frame)) {
				results.add(inputs);
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

	private Domain<RValue>[] constructGenerators(Tuple<Decl.Variable> parameters, Context context) {
		Domain<RValue>[] generators = new Domain[parameters.size()];
		//
		for (int i = 0; i != parameters.size(); ++i) {
			generators[i] = constructGenerator(parameters.get(i).getType(), context);
		}
		return generators;
	}

	private Domain<RValue> constructGenerator(Type type, Context context) {
		switch(type.getOpcode()) {
		case TYPE_null:
			return constructGenerator((Type.Null) type, context);
		case TYPE_bool:
			return constructGenerator((Type.Bool) type, context);
		case TYPE_byte:
			return constructGenerator((Type.Byte) type, context);
		case TYPE_int:
			return constructGenerator((Type.Int) type, context);
		case TYPE_array:
			return constructGenerator((Type.Array) type, context);
		case TYPE_record:
			return constructGenerator((Type.Record) type, context);
		case TYPE_nominal:
			return constructGenerator((Type.Nominal) type, context);
		case TYPE_union:
			return constructGenerator((Type.Union) type, context);
		case TYPE_staticreference:
		case TYPE_reference:
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
		default:
			// FIXME: need to do something here.
			throw new RuntimeException("unknown type encountered (" + type + ")");
		}
	}

	private Domain<RValue> constructGenerator(Type.Null type, Context context) {
		return Domains.Finite(RValue.Null);
	}

	private Domain<RValue> constructGenerator(Type.Bool type, Context context) {
		return Domains.Finite(RValue.False, RValue.True);
	}

	private Domain<RValue> constructGenerator(Type.Int type, Context context) {
		return new DomainWrapper<Integer>(Domains.Int(context.minInt(), context.maxInt())) {
			@Override
			public RValue.Int get(long index) {
				return RValue.Int(BigInteger.valueOf(domain.get(index)));
			}
		};
	}

	private Domain<RValue> constructGenerator(Type.Byte type, Context context) {
		return new DomainWrapper<Integer>(Domains.Int(context.minInt(), context.maxInt())) {
			@Override
			public RValue.Byte get(long index) {
				int value = domain.get(index);
				return RValue.Byte((byte) value);
			}
		};
	}

	private Domain<RValue> constructGenerator(Type.Array type, Context context) {
		Domain<RValue> generator = constructGenerator(type.getElement(), context);
		//
		return new DomainWrapper<List<RValue>>(Domains.List(0,context.maxLength(), generator)) {
			@Override
			public RValue.Array get(long index) {
				List<RValue> list = domain.get(index);
				// FIXME: could be more efficient
				return RValue.Array(list.toArray(new RValue[list.size()]));
			}
		};
	}

	private Domain<RValue> constructGenerator(Type.Record type, Context context) {
		// FIXME: need to support open records!
		Tuple<Type.Field> fields = type.getFields();
		Domain<RValue>[] generators = new Domain[fields.size()];
		// Construct a generator for each field
		for(int i=0;i!=fields.size();++i) {
			generators[i] = constructGenerator(fields.get(i).getType(), context);
		}
		//
		return new DomainWrapper<RValue[]>(Domains.Product(generators)) {
			@Override
			public RValue.Record get(long index) {
				RValue[] vals = domain.get(index);
				Tuple<Type.Field> typeFields = type.getFields();
				RValue.Field[] fields = new RValue.Field[vals.length];
				//
				for (int i = 0; i != fields.length; ++i) {
					fields[i] = RValue.Field(typeFields.get(i).getName(), vals[i]);
				}
				// Done
				return RValue.Record(fields);
			}
		};
	}

	private Domain<RValue> constructGenerator(Type.Nominal type, Context context) {
		Decl.Type decl = type.getLink().getTarget();
		//
		if (context.depth(decl) == context.maxDepth()) {
			return Domains.Finite();
		} else if (!cache.containsKey(decl)) {
			context.enter(decl);
			check(decl, context);
			context.leave(decl);
		}
		//
		return cache.get(decl);
	}

	private Domain<RValue> constructGenerator(Type.Union type, Context context) {
		// Construct generators for each subtype
		Domain<RValue>[] generators = new Domain[type.size()];
		for(int i=0;i!=type.size();++i) {
			generators[i] = constructGenerator(type.get(i), context);
		}
		// Return the union of all domains
		return Domains.Union(generators);
	}

	private static abstract class DomainWrapper<T> implements Domain<RValue> {
		protected final Domain<T> domain;

		public DomainWrapper(Domain<T> domain) {
			this.domain = domain;
		}

		@Override
		public long size() {
			return domain.size();
		}

		@Override
		public Domain<RValue> slice(long start, long end) {
			throw new UnsupportedOperationException();
		}
	}

	private static final class DomainIterator implements Iterator<RValue[]> {
		private final long[] iterators;
		private final Domain<RValue>[] generators;

		public DomainIterator(Domain<RValue>...generators) {
			this.iterators = new long[generators.length + 1];
			this.generators = generators;
		}

		@Override
		public boolean hasNext() {
			return iterators[generators.length] == 0;
		}

		@Override
		public RValue[] next() {
			RValue[] result = new RValue[generators.length];
			// Extract the result values
			for (int i = 0; i != generators.length; ++i) {
				result[i] = generators[i].get(iterators[i]);
			}
			// Increment the iterators
			increment();
			//
			return result;
		}

		private void increment() {
			for (int i = 0; i != iterators.length; ++i) {
				long iterator = iterators[i] + 1;
				if (i >= generators.length || iterator < generators[i].size()) {
					iterators[i] = iterator;
					break;
				} else {
					iterators[i] = 0;
				}
			}
		}
	}

	/**
	 * Provides various mechanisms for controlling the construction of generators,
	 * such as limiting the maximum depth of recursive types.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Context {
		private HashMap<Decl,Integer> depths = new HashMap<>();

		public int maxDepth() {
			return 3;
		}

		public int minInt() {
			return -3;
		}

		public int maxInt() {
			return 3;
		}

		public int maxLength() {
			return 3;
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
}
