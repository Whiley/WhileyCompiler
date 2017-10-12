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
package wyc.command;

import java.io.IOException;
import java.util.Collections;

import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.util.StdProject;
import wyc.command.Run;
import wyc.command.Decompile.Result;
import wyc.util.AbstractProjectCommand;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.interpreter.Interpreter;
import wyil.interpreter.ConcreteSemantics.RValue;

import static wyc.lang.WhileyFile.*;

/**
 * Responsible for implementing the command "<code>wy run ...</code>" which
 * loads the appropriate <code>wyil</code> file and executes a given method
 * using the <code>Interpreter</code>.
 *
 * @see @link wyil.interpreter.Interpreter
 *
 * @author David J. Pearce
 *
 */
public class Run extends AbstractProjectCommand<Run.Result> {
	/**
	 * Result kind for this command
	 *
	 */
	public enum Result {
		SUCCESS,
		ERRORS,
		INTERNAL_FAILURE
	}

	public Run(Content.Registry registry, Logger logger) {
		super(registry, logger);
	}

	// =======================================================================
	// Configuration
	// =======================================================================

	@Override
	public String getDescription() {
		return "Execute a given method from a WyIL";
	}

	@Override
	public String getName() {
		return "run";
	}

	// =======================================================================
	// Execute
	// =======================================================================

	@Override
	public Result execute(String... args) {
		if (args.length < 2) {
			// FIXME: this is broken
			System.out.println("usage: run <wyilfile> <method>");
			return Result.ERRORS;
		}
		try {
			StdProject project = initialiseProject();
			Path.ID id = Trie.fromString(args[0]);
			Type.Method sig = new Type.Method(new Tuple<>(new Type[0]), new Tuple<>(), new Tuple<>(), new Tuple<>());
			NameID name = new NameID(id, args[1]);
			executeFunctionOrMethod(name, sig, project);
		} catch (IOException e) {
			// FIXME: need a better error reporting mechanism
			System.err.println("internal failure: " + e.getMessage());
			e.printStackTrace();
			return Result.INTERNAL_FAILURE;
		}
		return Result.SUCCESS;
	}

	// =======================================================================
	// Helpers
	// =======================================================================

	/**
	 * Execute a given function or method in a wyil file.
	 *
	 * @param id
	 * @param signature
	 * @param project
	 * @throws IOException
	 */
	private void executeFunctionOrMethod(NameID id, Type.Callable signature, Build.Project project)
			throws IOException {
		// Try to run the given function or method
		Interpreter interpreter = new Interpreter(project, System.out);
		RValue[] returns = interpreter.execute(id, signature, interpreter.new CallStack());
		// Print out any return values produced
		if (returns != null) {
			for (int i = 0; i != returns.length; ++i) {
				if (i != 0) {
					System.out.println(", ");
				}
				System.out.println(returns[i]);
			}
		}
	}
}
