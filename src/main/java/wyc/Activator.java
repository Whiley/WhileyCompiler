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
package wyc;

import wycc.cfg.Configuration;
import wycc.lang.Command;
import wycc.lang.Module;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyfs.util.Trie;
import wyil.interpreter.ConcreteSemantics.RValue;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.QualifiedName;
import wyil.lang.WyilFile.Type;
import static wyil.lang.WyilFile.Name;
import wyil.interpreter.Interpreter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import wybs.lang.Build;
import wybs.util.AbstractBuildRule;
import wybs.util.Logger;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.cmd.QuickCheck;
import wyc.lang.WhileyFile;
import wyc.task.CompileTask;

public class Activator implements Module.Activator {

	public static Trie PKGNAME_CONFIG_OPTION = Trie.fromString("package/name");
	public static Trie SOURCE_CONFIG_OPTION = Trie.fromString("build/whiley/source");
	public static Trie TARGET_CONFIG_OPTION = Trie.fromString("build/whiley/target");
	public static Trie VERIFY_CONFIG_OPTION = Trie.fromString("build/whiley/verify");
	public static Trie COUNTEREXAMPLE_CONFIG_OPTION = Trie.fromString("build/whiley/counterexamples");
	private static Value.UTF8 SOURCE_DEFAULT = new Value.UTF8("src".getBytes());
	private static Value.UTF8 TARGET_DEFAULT = new Value.UTF8("bin".getBytes());

	public static Build.Platform WHILEY_PLATFORM = new Build.Platform() {
		//
		@Override
		public String getName() {
			return "whiley";
		}

		@Override
		public Configuration.Schema getConfigurationSchema() {
			return Configuration.fromArray(
					Configuration.UNBOUND_STRING(SOURCE_CONFIG_OPTION, "Specify location for whiley source files", SOURCE_DEFAULT),
					Configuration.UNBOUND_STRING(TARGET_CONFIG_OPTION, "Specify location for generated wyil files", TARGET_DEFAULT),
					Configuration.UNBOUND_BOOLEAN(VERIFY_CONFIG_OPTION, "Enable verification of whiley files", new Value.Bool(false)),
					Configuration.UNBOUND_BOOLEAN(COUNTEREXAMPLE_CONFIG_OPTION, "Enable counterexample generation during verification", new Value.Bool(false)));
		}

		@Override
		public void initialise(Configuration configuration, Build.Project project) throws IOException {
			Trie pkg = Trie.fromString(configuration.get(Value.UTF8.class, PKGNAME_CONFIG_OPTION).unwrap());
			//
			Trie source = Trie.fromString(configuration.get(Value.UTF8.class, SOURCE_CONFIG_OPTION).unwrap());
			// Specify directory where generated WyIL files are dumped.
			Trie target = Trie.fromString(configuration.get(Value.UTF8.class, TARGET_CONFIG_OPTION).unwrap());
			// Specify set of files included
			Content.Filter<WhileyFile> includes = Content.filter("**", WhileyFile.ContentType);
			// Determine whether verification enabled or not
			boolean verification = configuration.get(Value.Bool.class, VERIFY_CONFIG_OPTION).unwrap();
			// Determine whether to try and find counterexamples or not
			boolean counterexamples = configuration.get(Value.Bool.class, COUNTEREXAMPLE_CONFIG_OPTION).unwrap();
			// Construct the source root
			Path.Root sourceRoot = project.getRoot().createRelativeRoot(source);
			// Construct the binary root
			Path.Root binaryRoot = project.getRoot().createRelativeRoot(target);
			// Initialise the target file being built
			Path.Entry<WyilFile> binary = initialiseBinaryTarget(binaryRoot,pkg);
			// Add build rule to project.
			project.getRules().add(new AbstractBuildRule<WhileyFile, WyilFile>(sourceRoot, includes, null) {
				@Override
				protected void apply(List<Path.Entry<WhileyFile>> matches, Collection<Build.Task> tasks)
						throws IOException {
					// Construct a new build task
					CompileTask task = new CompileTask(project, sourceRoot, binary, matches)
							.setVerification(verification).setCounterExamples(counterexamples);
					// Submit the task for execution
					tasks.add(task);
				}
			});
		}

		@Override
		public Content.Type<?> getSourceType() {
			return WhileyFile.ContentType;
		}

		@Override
		public Content.Type<?> getTargetType() {
			return WyilFile.ContentType;
		}

		@Override
		public void execute(Build.Project project, Path.ID id, String method, Value... args) throws IOException {
			// Construct method's qualified name and signature
			Type.Method sig = new Type.Method(Type.Void, Type.Void);
			QualifiedName name = new QualifiedName(new Name(id), new Identifier(method));
			// Try to run the given function or method
			Interpreter interpreter = new Interpreter(System.out);
			// Create the initial stack
			Interpreter.CallStack stack = initialise(project,interpreter);;
			// Execute the requested function
			RValue returns = interpreter.execute(name, sig, stack);
			// Print out any return values produced
			if (returns != null) {
				System.out.println(returns);
			}
		}

		private Interpreter.CallStack initialise(Build.Project project, Interpreter interpreter) throws IOException {
			throw new UnsupportedOperationException("FIX ME");
			// Determine target root where compiled WyIL files live
//			Path.Root bin = getTargetRoot(project.getRoot());
//			Path.Entry<WyilFile> binary = bin.get(pkg, WyilFile.ContentType);
//			//
//			Interpreter.CallStack stack = interpreter.new CallStack();
//			// Load the relevant WyIL module
//			stack.load(binary.read());
//			// Load all package dependencies
//			for(Build.Package p : project.getPackages()) {
//				// FIXME: is this the right way to determine the binary file from a given
//				// package?
//				List<Path.Entry<WyilFile>> entries = p.getRoot().get(Content.filter("**/*", WyilFile.ContentType));
//				//
//				for(Path.Entry<WyilFile> e : entries) {
//					stack.load(e.read());
//				}
//			}
//			//
//			return stack;
		}

		private Path.Entry<WyilFile> initialiseBinaryTarget(Path.Root binroot, Path.ID id) throws IOException {
			if(binroot.exists(id, WyilFile.ContentType)) {
				// Yes, it does so reuse it.
				return binroot.get(id, WyilFile.ContentType);
			} else {
				// No, it doesn't so create and initialise it with an empty WyilFile
				Path.Entry<WyilFile> e = binroot.create(id, WyilFile.ContentType);
				WyilFile wf = new WyilFile(e);
				wf.setRootItem(new WyilFile.Decl.Module(new Name(id), new Tuple<>(), new Tuple<>(), new Tuple<>()));
				e.write(wf);
				return e;
			}
		}
	};

	// =======================================================================
	// Start
	// =======================================================================

	@Override
	public Module start(Module.Context context) {
		// Register platform
		context.register(Build.Platform.class, WHILEY_PLATFORM);
		// List of commands to register
		context.register(Command.Descriptor.class, QuickCheck.DESCRIPTOR);
		// List of content types
		context.register(Content.Type.class, WhileyFile.ContentType);
		context.register(Content.Type.class, WyilFile.ContentType);
		// Done
		return new Module() {
			// what goes here?
		};
	}

	// =======================================================================
	// Stop
	// =======================================================================

	@Override
	public void stop(Module module, Module.Context context) {
		// could do more here?
	}
}
