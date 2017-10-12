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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wybs.util.StdProject;
import wyc.command.Decompile;
import wyc.command.Compile.Result;
import wyc.io.WhileyFilePrinter;
import wyc.lang.WhileyFile;
import wyc.util.AbstractProjectCommand;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyil.io.WyilFileReader;

/**
 * Responsible for implementing the command "<code>wy decompile ...</code>" which
 * invokes the decompiler to build only those files specified on the command line.
 *
 * @author David J. Pearce
 *
 */
public class Decompile extends AbstractProjectCommand<Decompile.Result> {
	/**
	 * Result kind for this command
	 *
	 */
	public enum Result {
		SUCCESS,
		ERRORS,
		INTERNAL_FAILURE
	}

	/**
	 * Indicate whether or not to print out verbose information. That is,
	 * include more details about the underlying bytecode structure.
	 */
	private boolean verbose;

	public Decompile(Content.Registry registry, Logger logger) {
		super(registry, logger);
	}

	// =======================================================================
	// Configuration
	// =======================================================================

	@Override
	public String getDescription() {
		return "Decompile one or more binary WyIL files";
	}

	public String describeVerbose() {
		return "Provide details about underlying bytecode structure";
	}

	public void setVerbose() {
		this.verbose = true;
	}

	@Override
	public String getName() {
		return "decompile";
	}

	// =======================================================================
	// Execute
	// =======================================================================

	@Override
	public Result execute(String... args) {
		// Create delta and santify check
		ArrayList<File> delta = new ArrayList<>();
		for (String arg : args) {
			delta.add(new File(arg));
		}

		// FIXME: somehow, needing to use physical files at this point is
		// rather cumbersome. It would be much better if the enclosing
		// framework could handle this aspect for us.
		for(File f : delta) {
			if(!f.exists()) {
				// FIXME: sort this out!
				System.out.println("decompile: file not found: " + f.getName());
				return Result.ERRORS;
			}
		}
		try {
			// Finalise the configuration before continuing.
			StdProject project = initialiseProject();
			// Determine source files to build
			List<Path.Entry<WhileyFile>> entries = wyildir.find(delta, WhileyFile.BinaryContentType);
			for (Path.Entry<WhileyFile> e : entries) {
				WhileyFile wf = new WyilFileReader(e).read();
				WhileyFilePrinter wyp = new WhileyFilePrinter(System.out);
				wyp.setVerbose(verbose);
				wyp.apply(wf);
			}
		} catch (IOException e) {
			// FIXME: need a better error reporting mechanism
			System.err.println("internal failure: " + e.getMessage());
			if (verbose) {
				e.printStackTrace();
			}
			return Result.INTERNAL_FAILURE;
		}
		return Result.SUCCESS;
	}

	// =======================================================================
	// Helpers
	// =======================================================================

}
