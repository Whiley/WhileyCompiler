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
package wyqc;

import wycli.cfg.Configuration;
import wycli.lang.Command;
import wycli.lang.Plugin;
import wycc.lang.Content;
import wycc.lang.Path;
import wyil.lang.WyilFile;
import wyqc.cmd.Check;

import java.io.IOException;

import wycc.lang.Build;
import wyc.lang.WhileyFile;

public class Activator implements Plugin.Activator {

//	public static Command.Platform CHECK_PLATFORM = new Command.Platform() {
//		//
//		@Override
//		public String getName() {
//			return "check";
//		}
//
//		@Override
//		public Configuration.Schema getConfigurationSchema() {
//			return Configuration.EMPTY_SCHEMA;
//		}
//
//		@Override
//		public Build.Task initialise(Path path, Command.Environment environment) throws IOException {
//			throw new IllegalArgumentException();
//		}
//	};

	// =======================================================================
	// Start
	// =======================================================================

	@Override
	public Plugin start(Plugin.Context context) {
		// Register check command
		context.register(Command.Descriptor.class, Check.DESCRIPTOR);
		// Register platform
//		context.register(Command.Platform.class, CHECK_PLATFORM);
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
