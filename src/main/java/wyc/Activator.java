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

import wyc.task.CompileTask;
import wycli.cfg.Configuration;
import wycli.lang.Command;
import wycli.lang.Module;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.interpreter.ConcreteSemantics.RValue;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.QualifiedName;
import wyil.lang.WyilFile.Type;
import static wyil.lang.WyilFile.Name;
import wyil.interpreter.Interpreter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.lang.WhileyFile;

public class Activator implements Module.Activator {

	public static Trie PKGNAME_CONFIG_OPTION = Trie.fromString("package/name");
	public static Trie SOURCE_CONFIG_OPTION = Trie.fromString("build/whiley/source");
	public static Trie TARGET_CONFIG_OPTION = Trie.fromString("build/whiley/target");
	public static Trie VERIFY_CONFIG_OPTION = Trie.fromString("build/whiley/verify");
	public static Trie COUNTEREXAMPLE_CONFIG_OPTION = Trie.fromString("build/whiley/counterexamples");
	private static Value.UTF8 SOURCE_DEFAULT = new Value.UTF8("src".getBytes());
	private static Value.UTF8 TARGET_DEFAULT = new Value.UTF8("bin".getBytes());

	public static Command.Platform WHILEY_PLATFORM = new Command.Platform() {
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
		public Build.Task initialise(Configuration configuration, Command.Environment environment) throws IOException {
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
			// Construct build task

			// FIXME: this is clearly broken!

			return new CompileTask(target, Collections.EMPTY_LIST);
		}
	};

	// =======================================================================
	// Start
	// =======================================================================

	@Override
	public Module start(Module.Context context) {
		// Register platform
		context.register(Command.Platform.class, WHILEY_PLATFORM);
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
