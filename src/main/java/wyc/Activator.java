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
import wycli.lang.Plugin;
import wyil.lang.WyilFile;

import java.io.IOException;
import java.util.Collections;

import wycc.lang.Build;
import wycc.lang.Content;
import wycc.lang.Path;
import wycc.util.AbstractCompilationUnit.Identifier;
import wycc.util.AbstractCompilationUnit.Tuple;
import wycc.util.AbstractCompilationUnit.Value;
import wyc.lang.WhileyFile;

public class Activator implements Plugin.Activator {

	public static Path PKGNAME_CONFIG_OPTION = Path.fromString("package/name");
	public static Path SOURCE_CONFIG_OPTION = Path.fromString("build/whiley/source");
	public static Path TARGET_CONFIG_OPTION = Path.fromString("build/whiley/target");
	public static Path VERIFY_CONFIG_OPTION = Path.fromString("build/whiley/verify");
	public static Path COUNTEREXAMPLE_CONFIG_OPTION = Path.fromString("build/whiley/counterexamples");
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
		public Build.Task initialise(Path path, Command.Environment environment) throws IOException {
			// Determine local configuration
			Configuration config = environment.get(path);
			//
			Path pkg = Path.fromString(config.get(Value.UTF8.class, PKGNAME_CONFIG_OPTION).unwrap());
			//
			Path source = Path.fromString(config.get(Value.UTF8.class, SOURCE_CONFIG_OPTION).unwrap());
			// Specify directory where generated WyIL files are dumped.
			Path target = Path.fromString(config.get(Value.UTF8.class, TARGET_CONFIG_OPTION).unwrap());
			// Determine whether verification enabled or not
			boolean verification = config.get(Value.Bool.class, VERIFY_CONFIG_OPTION).unwrap();
			// Determine whether to try and find counterexamples or not
			boolean counterexamples = config.get(Value.Bool.class, COUNTEREXAMPLE_CONFIG_OPTION).unwrap();
			// Construct build task

			System.out.println("INITIALISING WYC TASK");

			// FIXME: this is clearly broken!

			return new CompileTask(target.append(pkg), Collections.EMPTY_LIST);
		}
	};

	// =======================================================================
	// Start
	// =======================================================================

	@Override
	public Plugin start(Plugin.Context context) {
		// Register platform
		context.register(Command.Platform.class, WHILEY_PLATFORM);
		// List of content types
		context.register(Content.Type.class, WhileyFile.ContentType);
		context.register(Content.Type.class, WyilFile.ContentType);
		// Done
		return new Plugin() {
			// what goes here?
		};
	}

	// =======================================================================
	// Stop
	// =======================================================================

	@Override
	public void stop(Plugin module, Plugin.Context context) {
		// could do more here?
	}
}
