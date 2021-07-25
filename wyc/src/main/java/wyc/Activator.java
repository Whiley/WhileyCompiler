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
import wycli.lang.Package;
import wyil.lang.WyilFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import wycc.lang.*;
import wycc.util.AbstractCompilationUnit.Value;
import wyc.lang.WhileyFile;

public class Activator implements Plugin.Activator {
	public static Path PACKAGE_NAME = Path.fromString("package/name");
	public static Path BUILD_WHILEY_SOURCE = Path.fromString("build/whiley/source");
	public static Path BUILD_WHILEY_TARGET = Path.fromString("build/whiley/target");
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
					Configuration.UNBOUND_STRING(BUILD_WHILEY_SOURCE, "Specify location for whiley source files", SOURCE_DEFAULT),
					Configuration.UNBOUND_STRING(BUILD_WHILEY_TARGET, "Specify location for generated wyil files", TARGET_DEFAULT));
		}

		@Override
		public Build.Task initialise(Path path, Command.Environment environment) throws IOException {
			// Determine local configuration
			Configuration config = environment.get(path);
			Build.SnapShot snapshot = environment.getRepository().last();
			Package.Resolver resolver = environment.getPackageResolver();
			//
			Path pkg = Path.fromString(config.get(Value.UTF8.class, PACKAGE_NAME).unwrap());
			//
			Filter source = Filter.fromString(config.get(Value.UTF8.class, BUILD_WHILEY_SOURCE).unwrap());
			// Specify directory where generated WyIL files are dumped.
			Path target = Path.fromString(config.get(Value.UTF8.class, BUILD_WHILEY_TARGET).unwrap());
			// Construct build task
			Filter includes = source.append(Filter.EVERYTHING);
			// Identify all Whiley source files
			List<WhileyFile> sources = snapshot.getAll(WhileyFile.ContentType, includes);
			// Resolve all packages declared in configuration
			List<Content.Source> pkgs = resolver.resolve(config);
			// Done
			return new CompileTask(target.append(pkg), sources, pkgs);
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
