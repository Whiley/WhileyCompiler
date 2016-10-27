// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.
package wyc.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wybs.util.StdProject;
import wyc.lang.WhileyFile;
import wycc.util.AbstractCommand;
import wycc.util.ArrayUtils;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.JarFileRoot;
import wyfs.util.VirtualRoot;
import wyil.lang.WyilFile;

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
	 * The whiley path identifies additional items (i.e. libraries or
	 * directories) which the compiler uses to resolve symbols (e.g. module
	 * names, functions, etc).
	 */
	protected ArrayList<Path.Root> whileypath = new ArrayList<Path.Root>();

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

	public void setWhileypath(String paths) throws IOException {
		whileypath.clear();
		// TODO: this should be pushed into AbstractConfigurable
		String[] roots = paths.split(":");
		for (String root : roots) {
			try {
				if (root.endsWith(".jar")) {
					whileypath.add(new JarFileRoot(root, registry));
				} else {
					whileypath.add(new DirectoryRoot(root, registry));
				}
			} catch (IOException e) {
				logger.logTimedMessage("Warning: " + root + " is not a valid package root", 0, 0);
			}
		}
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
	 * Construct a new temporary project. This project is temporary because it
	 * only exists for the life of an execution of this command.
	 *
	 * @return
	 * @throws IOException
	 */
	protected StdProject initialiseProject() throws IOException {
		// Finalise configuration
		finaliseConfiguration();
		// Add roots and construct project
		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();

		roots.add(whileydir);
		roots.add(wyildir);
		roots.add(wyaldir);
		roots.add(wycsdir);
		roots.addAll(whileypath);
		//
		addBootpath(roots);

		return new StdProject(roots);
	}

	/**
	 * Finalise the given configuration to ensure it is an consistent state.
	 * This means, in particular, that roots which have not been defined by the
	 * user are created as necessary.
	 */
	private void finaliseConfiguration() throws IOException {
		whileydir = getDirectoryRoot(whileydir,new DirectoryRoot(".",registry));
		wyildir = getDirectoryRoot(wyildir,whileydir);
		wyaldir = getAbstractRoot(wyaldir);
		wycsdir = getAbstractRoot(wycsdir);
	}

	/**
	 * Initialise the bootpath for use with the compiler. The bootpath basically
	 * identifies the location of the standard library for automatic inclusion
	 * into the whileypath.
	 *
	 * @param roots
	 * @throws IOException
	 */
	private void addBootpath(List<Path.Root>roots) throws IOException {
		// Configure boot path
		String bootpath = System.getProperty("wdk.bootpath");
		if (bootpath != null) {
			roots.add(new JarFileRoot(bootpath, registry));
		}
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

	public List getModifiedSourceFiles() throws IOException {
		if (whileydir == null) {
			// Note, whileyDir can be null if e.g. compiling wyil -> wyjc
			return new ArrayList();
		} else {
			Content.Filter<WhileyFile> whileyIncludes = Content.filter("**", WhileyFile.ContentType);
			return getModifiedSourceFiles(whileydir, whileyIncludes, wyildir,
					WyilFile.ContentType);
		}
	}

	/**
	 * Generate the list of source files which need to be recompiled. By
	 * default, this is done by comparing modification times of each whiley file
	 * against its corresponding wyil file. Wyil files which are out-of-date are
	 * scheduled to be recompiled.
	 *
	 * @return
	 * @throws IOException
	 */
	public static <T, S> List<Path.Entry<T>> getModifiedSourceFiles(Path.Root sourceDir,
			Content.Filter<T> sourceIncludes, Path.Root binaryDir, Content.Type<S> binaryContentType)
					throws IOException {
		// Now, touch all source files which have modification date after
		// their corresponding binary.
		ArrayList<Path.Entry<T>> sources = new ArrayList<Path.Entry<T>>();

		for (Path.Entry<T> source : sourceDir.get(sourceIncludes)) {
			// currently, I'm assuming everything is modified!
			Path.Entry<S> binary = binaryDir.get(source.id(), binaryContentType);
			// first, check whether wycs file out-of-date with source file
			if (binary == null || binary.lastModified() < source.lastModified()) {
				sources.add(source);
			}
		}

		return sources;
	}
}
