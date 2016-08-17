package wyc.util;

import java.io.*;
import java.util.*;

import wycc.util.Logger;
import wycc.util.Pair;
import wybs.lang.*;
import wybs.util.*;
import wyfs.lang.Content;
import wyfs.lang.Content.Filter;
import wyfs.lang.Content.Type;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.JarFileRoot;
import wyfs.util.VirtualRoot;
import wyc.builder.CompileTask;
import wyc.builder.DecompileTask;
import wyc.lang.WhileyFile;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.WycsFile;
import wycs.syntax.WyalFile;
import wyil.builders.Wyil2WyalBuilder;
import wyil.lang.WyilFile;

/**
 * <p>
 * The purpose of the build template is to provide a generic mechanism for
 * constructing an appropriate build project. A build project identifies the
 * necessary roots for source and binary files, as well as any additional
 * libraries. The build project also identifies the build rules used to action
 * the build itself.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class BuildTemplate {

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

			if(suffix.equals("whiley")) {
				e.associate(WhileyFile.ContentType, null);
			} else if(suffix.equals("wyil")) {
				e.associate(WyilFile.ContentType, null);
			} else if(suffix.equals("wyal")) {
				e.associate(WyalFile.ContentType, null);
			} else if(suffix.equals("wycs")) {
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
	public final Content.Registry registry;

	/**
	 * For logging information.
	 */
	protected PrintStream logout = System.err;

	// ==========================================================================
	// Constructors & Configuration
	// ==========================================================================

	public BuildTemplate() {
		this.registry = new Registry();
	}

    // ==========================================================================
	// Build Methods
	// ==========================================================================

    /**
	 * Building the given source files.
	 *
	 * @param _args
	 */
	public Build.Project instantiate(BuildConfiguration config) throws Exception {
		// Initialise Project
		StdProject project = initialiseProject(config);
		// Initialise Build Rules
		if (config.getAttribute("decompile", Boolean.class)) {
			addCompileRules(project, config);
			if (config.getAttribute("verify", Boolean.class)) {
				addVerifyRules(project, config);
			}
		} else {
			addDecompileRules(project, config);
		}
		// Done
		return project;
	}

	// ==========================================================================
	// Misc
	// ==========================================================================

	/**
     *
     * @return
     * @throws IOException
     */
	protected StdProject initialiseProject(BuildConfiguration config) throws IOException {
		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();
		// Setup the various folders
		roots.addAll(config.getFolders());
		// Setup the path
		roots.addAll(config.getPath());
		// Construct the project
		return new StdProject(roots);
	}

	/**
	 * Add necessary build rules to the project for compiling whiley files into
	 * wyil files.
	 *
	 * @param project
	 */
	private void addCompileRules(StdProject project, BuildConfiguration config) {
		// Folder configuration
		Path.Root whileyDir = config.getFolder(WhileyFile.ContentType);
		Path.Root wyilDir = config.getFolder(WhileyFile.ContentType);
		Content.Filter<WhileyFile> whileyIncludes = Content.filter("**", WhileyFile.ContentType);
		Content.Filter<WhileyFile> whileyExcludes = null;
		// Rule for compiling Whiley to WyIL
		CompileTask wyilBuilder = new CompileTask(project);		
		project.add(new StdBuildRule(wyilBuilder, whileyDir, whileyIncludes, whileyExcludes, wyilDir));
	}
	
	/**
	 * Add necessary build rules for generating and verifying verification
	 * conditions from a WyIL file
	 * 
	 * @param project
	 * @param config
	 */
	private void addVerifyRules(StdProject project, BuildConfiguration config) {
		// Folder configuration
		Path.Root wyalDir = config.getFolder(WyalFile.ContentType);
		Path.Root wyilDir = config.getFolder(WhileyFile.ContentType);
		Path.Root wycsDir = config.getFolder(WycsFile.ContentType);
		Content.Filter<WyilFile> wyilIncludes = Content.filter("**", WyilFile.ContentType);
		Content.Filter<WyilFile> wyilExcludes = null;
		Content.Filter<WyalFile> wyalIncludes = Content.filter("**", WyalFile.ContentType);
		Content.Filter<WyalFile> wyalExcludes = null;		
		// Rule for compiling WyIL to WyAL
		Wyil2WyalBuilder wyalBuilder = new Wyil2WyalBuilder(project);
		project.add(new StdBuildRule(wyalBuilder, wyilDir, wyilIncludes, wyilExcludes, wyalDir));
		// Rule for compiling WyAL to WyCS
		Wyal2WycsBuilder wycsBuilder = new Wyal2WycsBuilder(project);
		project.add(new StdBuildRule(wycsBuilder, wyalDir, wyalIncludes, wyalExcludes, wycsDir));
	}

	/**
	 * Add necessary build rules for decompiling a WyIL file
	 * 
	 * @param project
	 * @param config
	 */
	protected void addDecompileRules(StdProject project, BuildConfiguration config) {
		// Folder configuration
		Path.Root wyilDir = config.getFolder(WhileyFile.ContentType);
		Content.Filter<WyilFile> wyilIncludes = Content.filter("**", WyilFile.ContentType);
		Content.Filter<WyilFile> wyilExcludes = null;
		// Rule for decompiling wyil => wyil
		DecompileTask dcTask = new DecompileTask(project);
		project.add(new StdBuildRule(dcTask, wyilDir, wyilIncludes, wyilExcludes, wyilDir));
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
	public static <T,S> List<Path.Entry<T>> getModifiedSourceFiles(
			Path.Root sourceDir, Content.Filter<T> sourceIncludes,
			Path.Root binaryDir, Content.Type<S> binaryContentType) throws IOException {
		// Now, touch all source files which have modification date after
		// their corresponding binary.
		ArrayList<Path.Entry<T>> sources = new ArrayList<Path.Entry<T>>();

		for (Path.Entry<T> source : sourceDir.get(sourceIncludes)) {
			// currently, I'm assuming everything is modified!
			Path.Entry<S> binary = binaryDir.get(source.id(),
					binaryContentType);
			// first, check whether wyil file out-of-date with source file
			if (binary == null || binary.lastModified() < source.lastModified()) {
				sources.add(source);
			}
		}

		return sources;
	}

	/**
	 * Flush all built files to disk.
	 */
	protected void flush() throws IOException {
		// FIXME: may need to wyil folders to disk here
	}
}
