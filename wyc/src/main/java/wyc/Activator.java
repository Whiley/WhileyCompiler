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
import java.util.List;

import jbfs.core.Build;
import jbfs.core.Content;
import jbfs.util.Trie;
import wycc.lang.*;
import wycc.util.AbstractCompilationUnit.Value;
import wyc.lang.WhileyFile;

public class Activator implements Plugin.Activator {
	public static Trie PACKAGE_NAME = Trie.fromString("package/name");
	public static Trie BUILD_WHILEY_SOURCE = Trie.fromString("build/whiley/source");
	public static Trie BUILD_WHILEY_TARGET = Trie.fromString("build/whiley/target");
	public static Trie BUILD_WHILEY_STRICT = Trie.fromString("build/whiley/strict");
	private static Value.UTF8 SOURCE_DEFAULT = new Value.UTF8("src".getBytes());
	private static Value.UTF8 TARGET_DEFAULT = new Value.UTF8("bin".getBytes());
	private static Value.Bool STRICT_DEFAULT = new Value.Bool(false);

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
					Configuration.UNBOUND_STRING(BUILD_WHILEY_TARGET, "Specify location for generated wyil files", TARGET_DEFAULT),
					Configuration.UNBOUND_BOOLEAN(BUILD_WHILEY_STRICT, "Specify strict treatment of unsafe code", STRICT_DEFAULT));
		}

		@Override
		public Build.Task initialise(Trie path, Command.Environment environment) throws IOException {
			// Determine local configuration
			Configuration config = environment.get(path);
			Build.SnapShot snapshot = environment.getRepository().last();
			Package.Resolver resolver = environment.getPackageResolver();
			//
			Trie pkg = Trie.fromString(config.get(Value.UTF8.class, PACKAGE_NAME).unwrap());
			//
			Trie source = Trie.fromString(config.get(Value.UTF8.class, BUILD_WHILEY_SOURCE).unwrap());
			// Specify directory where generated WyIL files are dumped.
			Trie target = Trie.fromString(config.get(Value.UTF8.class, BUILD_WHILEY_TARGET).unwrap());
			// Determine strictness
			boolean strict = config.get(Value.Bool.class, BUILD_WHILEY_STRICT).get();
			// Construct includes filter
			Content.Filter<WhileyFile> includes = Content.Filter(WhileyFile.ContentType,source.append(Trie.EVERYTHING));
			// Identify all Whiley source files
			List<WhileyFile> sources = snapshot.getAll(includes);
			// Resolve all packages declared in configuration
			List<Content.Source> pkgs = resolver.resolve(config);
			// Done
			return new CompileTask(target.append(pkg), sources, pkgs).setStrict(strict);
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
