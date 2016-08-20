package wyc.commands;

import wycc.util.AbstractCommand;
import wyfs.lang.Content;

public class Decompile extends AbstractCommand {

	/**
	 * The master project content type registry. This is needed for the build
	 * system to determine the content type of files it finds on the file
	 * system.
	 */
	public final Content.Registry registry;
	
	public Decompile(Content.Registry registry) {
		this.registry = registry;
	}
	
	// =======================================================================
	// Configuration
	// =======================================================================

	@Override
	public String getDescription() {
		return "Decompile one or more binary WyIL files";
	}

	// =======================================================================
	// Execute
	// =======================================================================

	@Override
	public void execute(String... args) {
		// TODO Auto-generated method stub
		
	}

	// =======================================================================
	// Helpers
	// =======================================================================

}
