package wyc;

import wycc.lang.Command;
import wycc.lang.Module;
import wycc.util.Logger;
import wycs.core.WycsFile;
import wycs.syntax.WyalFile;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyil.lang.WyilFile;
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
		public void associate(Path.Entry e) {
			String suffix = e.suffix();

			if (suffix.equals("whiley")) {
				e.associate(WhileyFile.ContentType, null);
			} else if (suffix.equals("wyil")) {
				e.associate(WyilFile.ContentType, null);
			} else if (suffix.equals("wyal")) {
				e.associate(WyalFile.ContentType, null);
			} else if (suffix.equals("wycs")) {
				e.associate(WycsFile.ContentType, null);
			}
		}

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
		final Command[] commands = { new Compile(registry, logger), new Decompile(registry),
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
