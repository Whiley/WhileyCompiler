// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import wyc.util.AbstractProjectCommand;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyil.io.WyilFilePrinter;
import wyil.io.WyilFileReader;
import wyil.lang.WyilFile;

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
		// decompile files
		try {
			for (File f : delta) {
				FileInputStream fin = new FileInputStream(f);
				WyilFile wf = new WyilFileReader(fin).read();
				WyilFilePrinter wyp = new WyilFilePrinter(System.out);
				wyp.setVerbose(verbose);
				wyp.apply(wf);
			}
		} catch (IOException e) {
			// FIXME: this is no solution
			throw new RuntimeException(e);
		}
		return Result.SUCCESS;
	}

	// =======================================================================
	// Helpers
	// =======================================================================

}
