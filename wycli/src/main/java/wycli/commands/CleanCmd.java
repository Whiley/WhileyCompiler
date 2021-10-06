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
package wycli.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jbfs.core.Build;
import jbfs.core.Content;
import jbfs.util.Trie;
import wycc.util.Logger;
import wycli.cfg.Configuration;
import wycli.cfg.Configuration.Schema;
import wycli.lang.Command;

/**
 * Command for cleaning all generated files from the local directory.
 *
 * @author David J. Pearce
 *
 */
public class CleanCmd implements Command {

	/**
	 * Descriptor for this command.
	 */
	public static final Command.Descriptor DESCRIPTOR = new Command.Descriptor() {

		@Override
		public String getName() {
			return "clean";
		}

		@Override
		public String getDescription() {
			return "Remove all generated (e.g. binary) files";
		}

		@Override
		public List<wycli.lang.Command.Option.Descriptor> getOptionDescriptors() {
			return Arrays.asList(Command.OPTION_FLAG("verbose", "generate verbose information", false));
		}

		@Override
		public Schema getConfigurationSchema() {
			return Configuration.EMPTY_SCHEMA;
		}

		@Override
		public List<Descriptor> getCommands() {
			return Collections.emptyList();
		}

		@Override
		public Command initialise(Environment environment) {
			return new CleanCmd(environment);
		}

	};

	/**
	 * The enclosing project for this build
	 */
	private final Command.Environment environment;

	/**
	 * Logger
	 */
	private Logger logger;

	public CleanCmd(Command.Environment environment) {
		this.environment = environment;
		this.logger = environment.getLogger();
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
		// Access workspace root
		Content.Root workspace = environment.getWorkspaceRoot();
		// Extract options
		boolean verbose = template.getOptions().get("verbose", Boolean.class);
		// Construct the build plan
		Build.Transaction plan = BuildCmd.getBuildPlan(path, environment);
		// Compare files in the build repository with those in the root.
		for (Build.Task t : plan) {
			if (verbose) {
				logger.logTimedMessage("removing  " + t.getPath() + ", " + t.getContentType(), 0, 0);
			}
			workspace.remove(t.getPath(), t.getContentType());
		}
		// Sync workspace to disk
		workspace.synchronise();
		//
		return true;
	}
}
