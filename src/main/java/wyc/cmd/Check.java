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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import wybs.util.AbstractCompilationUnit.Value;
import wyc.Activator;
import wycc.WyProject;
import wycc.cfg.Configuration;
import wycc.cfg.Configuration.Schema;
import wycc.commands.Build;
import wycc.lang.Command;
import wycc.lang.Command.Descriptor;
import wycc.lang.Command.Option;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.WyilFile;
import static wyil.lang.WyilFile.*;
import wyil.lang.WyilFile.Decl;

public class Check implements Command {
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
			return new Check((WyProject) environment, configuration, System.out, System.err);
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
	private final WyProject project;

	public Check(WyProject project, Configuration configuration, OutputStream sysout, OutputStream syserr) {
		this.project = project;
		this.configuration = configuration;
		this.sysout = new PrintStream(sysout);
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
	public boolean execute(Template template) throws Exception {
		Trie pkg = Trie.fromString(configuration.get(Value.UTF8.class, Activator.PKGNAME_CONFIG_OPTION).unwrap());
		// Specify directory where generated WyIL files are dumped.
		Trie target = Trie.fromString(configuration.get(Value.UTF8.class, Activator.TARGET_CONFIG_OPTION).unwrap());
		//
		Path.Root binaryRoot = project.getBuildProject().getRoot().createRelativeRoot(target);
		//
		if(binaryRoot.exists(pkg, WyilFile.ContentType)) {
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

	private void check(WyilFile wf) {
		for(Decl.Unit unit : wf.getModule().getUnits()) {
			check(unit);
		}
	}

	private void check(Decl.Unit unit) {
		for(Decl d : unit.getDeclarations()) {
			switch(d.getOpcode()) {
			case DECL_function:
				check((Decl.FunctionOrMethod) d);
				break;
			}
		}
	}

	private void check(Decl.FunctionOrMethod fm) {
		System.out.println("FOUND: " + fm.getName());
	}
}
