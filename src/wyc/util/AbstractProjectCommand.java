package wyc.util;

import java.io.IOException;
import java.util.ArrayList;

import wybs.util.StdProject;
import wycc.util.AbstractCommand;
import wycc.util.ArrayUtils;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.VirtualRoot;

/**
 * Provides an abstract command from which other commands for controlling the
 * Whiley compiler can be derived. Specifically, this class handles all the
 * issues related to managing the various project roots, etc.
 * 
 * @author David J. Pearce
 *
 */
public abstract class AbstractProjectCommand<T> extends AbstractCommand<T> {

	/**
	 * The master project content type registry. This is needed for the build
	 * system to determine the content type of files it finds on the file
	 * system.
	 */
	public final Content.Registry registry;

	/**
	 * The location in which whiley source files a located, or null if not
	 * specified.  The default value is the current directory.
	 */
	protected DirectoryRoot whileydir;

	/**
	 * The location in which wyil binary files are stored, or null if not
	 * specified.
	 */
	protected DirectoryRoot wyildir;

	/**
	 * The location in which wyal source files are stored, or null if not
	 * specified.
	 */
	protected Path.Root wyaldir;

	/**
	 * The location in which wycs binary files are stored, or null if not
	 * specified.
	 */
	protected Path.Root wycsdir;

	/**
	 * The logger used for logging system events
	 */
	protected Logger logger;

	/**
	 * Construct a new instance of this command.
	 * 
	 * @param registry
	 *            The content registry being used to match files to content
	 *            types.
	 * @throws IOException 
	 */
	public AbstractProjectCommand(Content.Registry registry, Logger logger, String... options) {
		super(ArrayUtils.append(options,"whileypath","whileydir","wyildir","wyaldir"));
		this.registry = registry;
		this.logger = logger;
	}
	
	// =======================================================================
	// Configuration
	// =======================================================================

	public String describeWhileypath() {
		return "Specify where to find compiled Whiley (WyIL) files";
	}

	public void setWhileydir(String dir) throws IOException {
		whileydir = new DirectoryRoot(dir,registry);
	}

	public String describeWhileydir() {
		return "Specify where to find Whiley source files";
	}

	public String describeWyildir() {
		return "Specify where to find place compiled Whiley (WyIL) files";
	}

	public void setWyildir(String dir) throws IOException {
		this.wyildir = new DirectoryRoot(dir, registry);
	}
	
	public String describeWyaldir() {
		return "Specify where to find place generated verification (WyAL) files";
	}
	
	public void setWyaldir(String dir) throws IOException {
		this.wyildir = new DirectoryRoot(dir, registry);
	}
	
	// =======================================================================
	// Configuration
	// =======================================================================

	/**
	 * Finalise the given configuration to ensure it is an consistent state.
	 * This means, in particular, that roots which have not been defined by the
	 * user are created as necessary.
	 */
	protected void finaliseConfiguration() throws IOException {
		whileydir = getDirectoryRoot(whileydir,new DirectoryRoot(".",registry));
		wyildir = getDirectoryRoot(wyildir,whileydir);
		wyaldir = getAbstractRoot(wyaldir);
		wycsdir = getAbstractRoot(wycsdir);
	}

	/**
	 * Construct a new temporary project. This project is temporary because it
	 * only exists for the life of an execution of this command.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected StdProject initialiseProject() throws IOException {
		// Add roots and construct project
		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();
		roots.add(whileydir);
		roots.add(wyildir);
		roots.add(wyaldir);
		roots.add(wycsdir);

		return new StdProject(roots);
	}
	

	/**
	 * Construct a root which must correspond to a physical directory.
	 * 
	 * @throws IOException
	 * 
	 */
	private DirectoryRoot getDirectoryRoot(DirectoryRoot dir, DirectoryRoot defaulT) throws IOException {
		if(dir != null) {
			return dir;
		} else {
			return defaulT;
		}
	}

	/**
	 * Construct a root which is either virtual or corresponds to a physical
	 * directory.
	 * 
	 * @throws IOException
	 * 
	 */
	private Path.Root getAbstractRoot(Path.Root dir) throws IOException {
		if(dir != null) {
			return dir;
		} else {
			return new VirtualRoot(registry);
		}
	}
}
