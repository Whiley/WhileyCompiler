package wyc.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import wycc.util.AbstractCommand;
import wyfs.lang.Content;
import wyil.io.WyilFilePrinter;
import wyil.io.WyilFileReader;
import wyil.lang.WyilFile;

public class Decompile extends AbstractCommand<Decompile.Result> {
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
	 * The master project content type registry. This is needed for the build
	 * system to determine the content type of files it finds on the file
	 * system.
	 */
	private final Content.Registry registry;
	
	/**
	 * Indicate whether or not to print out verbose information. That is,
	 * include more details about the underlying bytecode structure.
	 */
	private boolean verbose;
	
	public Decompile(Content.Registry registry) {
		super("verbose");
		this.registry = registry;
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

	// =======================================================================
	// Execute
	// =======================================================================

	@Override
	public Result execute(String... args) {
		// Create delta and santify check
		ArrayList<File> delta = new ArrayList<File>();
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
