// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.commands;

import java.io.IOException;
import java.util.Collections;

import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.util.StdProject;
import wyc.util.AbstractProjectCommand;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.util.interpreter.Interpreter;

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
			Type.Method sig = (Type.Method) Type.Method(new Type[0], new Type[0]);
			NameID name = new NameID(id, args[1]);
			executeFunctionOrMethod(name, sig, project);
		} catch (IOException e) {
			// FIXME: this is broken
			throw new RuntimeException(e);
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
	private void executeFunctionOrMethod(NameID id, Type.FunctionOrMethod signature, Build.Project project)
			throws IOException {
		// Try to run the given function or method
		Constant[] returns = new Interpreter(project, System.out).execute(id, signature);
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
