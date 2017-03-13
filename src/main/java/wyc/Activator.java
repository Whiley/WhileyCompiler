// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc;

import wycc.lang.Command;
import wycc.lang.Module;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyil.lang.WyilFile;
import wyal.lang.WyalFile;
import wyc.commands.*;
import wyc.lang.WhileyFile;

public class Activator implements Module.Activator {
	/**
	 * Default implementation of a content registry. This associates whiley and
	 * wyil files with their respective content types.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Registry implements Content.Registry {
		@Override
		public void associate(Path.Entry e) {
			String suffix = e.suffix();

			if (suffix.equals("whiley")) {
				e.associate(WhileyFile.ContentType, null);
			} else if (suffix.equals("wyil")) {
				e.associate(WyilFile.ContentType, null);
			} else if (suffix.equals("wyal")) {
				e.associate(WyalFile.ContentType, null);
			}
		}

		@Override
		public String suffix(Content.Type<?> t) {
			return t.getSuffix();
		}
	}

	/**
	 * The master project content type registry. This is needed for the build
	 * system to determine the content type of files it finds on the file
	 * system.
	 */
	public final Content.Registry registry = new Registry();


	// =======================================================================
	// Start
	// =======================================================================

	@Override
	public Module start(Module.Context context) {
		// FIXME: logger is a hack!
		final Logger logger = new Logger.Default(System.err);
		// List of commands to use
		final Command[] commands = {
				new Compile(registry, logger),
				new Decompile(registry, logger),
				new Run(registry, logger) };
		// Register all commands
		for (Command c : commands) {
			context.register(wycc.lang.Command.class, c);
		}
		// Done
		return new Module() {
			// what goes here?
		};
	}

	// =======================================================================
	// Stop
	// =======================================================================

	@Override
	public void stop(Module module, Module.Context context) {
		// could do more here?
	}
}
