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
import wycc.util.AbstractCompilationUnit.Value;
import wyil.lang.WyilFile;
import wyqc.lang.QuickCheck;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jbfs.core.Build;
import jbfs.core.Content;
import jbfs.core.Build.Artifact;
import jbfs.core.Build.SnapShot;
import jbfs.util.Pair;
import jbfs.util.Trie;

import static wyqc.cmd.Check.*;
import static wyc.Activator.*;
import wyc.lang.WhileyFile;

public class Activator implements Plugin.Activator {

	public static Command.Platform CHECK_PLATFORM = new Command.Platform() {
		//
		@Override
		public String getName() {
			return "check";
		}

		@Override
		public Configuration.Schema getConfigurationSchema() {
			return Configuration.EMPTY_SCHEMA;
		}

		@Override
		public Build.Task initialise(Trie path, Command.Environment environment) throws IOException {
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
			// Construct initial context
			QuickCheck.Context context = QuickCheck.DEFAULT_CONTEXT.setIntegerRange(minInteger, maxInteger).setArrayLength(maxArrayLength)
					.setTypeDepth(maxTypeDepth).setAliasingWidth(maxAliasingWidth).setLambdaWidth(maxRotationWidth)
					.setIgnores(ignores).setSamplingRate(samplingRate).setSampleMin(limit)
					.setTimeout(timeout);
			Trie pkg = Trie.fromString(configuration.get(Value.UTF8.class, PACKAGE_NAME).unwrap());
			// Specify directory where generated WyIL files are dumped.
			Trie bindir = Trie.fromString(configuration.get(Value.UTF8.class, BUILD_WHILEY_TARGET).unwrap());
			final Trie target = bindir.append(pkg);
			//
			return new Build.Task() {

				@Override
				public Trie getPath() {
					return target;
				}

				@Override
				public Type<? extends Artifact> getContentType() {
					return WyilFile.ContentType;
				}

				@Override
				public List<? extends Artifact> getSourceArtifacts() {
					return Arrays.asList();
				}

				@Override
				public Pair<SnapShot, Boolean> apply(SnapShot snapshot) {
					//
					try {
						WyilFile binary = snapshot.get(WyilFile.ContentType,target);
						boolean OK = new QuickCheck(environment.getLogger()).check(environment, binary, context,
								Collections.EMPTY_LIST);
						// FIXME: something is not right here because QuickCheck updates the WyilFile
						// with error messages.
						return new Pair<>(snapshot, OK);
					} catch(IOException e) {
						// Why is this necessary? There should be no I/O in this method. That's the
						// whole point!
						throw new RuntimeException(e);
					}
				}

			};
		}
	};

	// =======================================================================
	// Start
	// =======================================================================

	@Override
	public Plugin start(Plugin.Context context) {
		// Register check command
		//context.register(Command.Descriptor.class, Check.DESCRIPTOR);
		// Register platform
		context.register(Command.Platform.class, CHECK_PLATFORM);
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
