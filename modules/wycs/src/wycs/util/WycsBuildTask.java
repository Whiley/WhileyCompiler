package wycs.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wybs.util.StdBuildRule;
import wybs.util.StdProject;
import wycc.lang.Pipeline;
import wycc.util.Logger;
import wycs.builders.Wyal2WycsBuilder;
import wycs.builders.Wycs2WyalBuilder;
import wycs.core.WycsFile;
import wycs.syntax.WyalFile;
import wycs.transforms.*;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.JarFileRoot;
import wyfs.util.VirtualRoot;

/**
 * <p>
 * Provides a general-purpose implementation for compiling Wycs source files
 * into binary files and verifying them. This is designed to make it easy to
 * interface with other frameworks (e.g. Ant). This class is designed to be
 * extended by clients which are providing some kind of compiler extension.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class WycsBuildTask {

	/**
	 * The purpose of the wyal file filter is simply to ensure only wyal files
	 * are loaded in a given directory root. It is not strictly necessary for
	 * correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter wyalFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().endsWith(".wyal") || f.isDirectory();
		}
	};

	/**
	 * The purpose of the wycs file filter is simply to ensure only wycs files
	 * are loaded in a given directory root. It is not strictly necessary for
	 * correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter wycsFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().endsWith(".wycs") || f.isDirectory();
		}
	};

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

			if (suffix.equals("wyal")) {
				e.associate(WyalFile.ContentType, null);
			} else if (suffix.equals("wycs")) {
				e.associate(WycsFile.ContentType, null);
			}
		}

		public String suffix(Content.Type<?> t) {
			if (t == WyalFile.ContentType) {
				return "wyal";
			} else if (t == WycsFile.ContentType) {
				return "wycs";
			} else {
				return "dat";
			}
		}
	}

	public static final List<Pipeline.Template> defaultPipeline = Collections
			.unmodifiableList(new ArrayList<Pipeline.Template>() {
				{
					add(new Pipeline.Template(MacroExpansion.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(VerificationCheck.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(SmtVerificationCheck.class,
							Collections.EMPTY_MAP));
				}
			});

	/**
	 * Register default transforms. This is necessary so they can be referred to
	 * from the command-line using abbreviated names, rather than their full
	 * names.
	 */
	static {
		Pipeline.register(TypePropagation.class);
		Pipeline.register(MacroExpansion.class);
		Pipeline.register(VerificationCheck.class);
        Pipeline.register(SmtVerificationCheck.class);
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

	/**
	 * The boot path contains the location of the wycs standard library.
	 */
	protected ArrayList<Path.Root> bootpath = new ArrayList<Path.Root>();

	/**
	 * The wycs path identifies additional items (i.e. libraries or
	 * directories) which the compiler uses to resolve symbols (e.g. module
	 * names, functions, etc).
	 */
	protected ArrayList<Path.Root> wycspath = new ArrayList<Path.Root>();

	/**
	 * The wyal source directory is the filesystem directory from which the
	 * compiler will look for (wyal) source files.
	 */
	protected DirectoryRoot wyalDir;

	/**
	 * The wycs directory is the filesystem directory where all generated wycs
	 * files will be placed.
	 */
	protected Path.Root wycsDir;

	/**
	 * The pipeline modifiers which will be applied to the default pipeline.
	 */
	protected ArrayList<Pipeline.Modifier> pipelineModifiers;

	/**
	 * Identifies which wyal source files should be considered for verification.
	 * By default, all files reachable from srcdir are considered.
	 */
	protected Content.Filter<WyalFile> wyalIncludes = Content.filter("**",
			WyalFile.ContentType);

	/**
	 * Identifies which wyal sources files should not be considered for
	 * compilation. This overrides any identified by <code>whileyIncludes</code>
	 * . By default, no files files reachable from srcdir are excluded.
	 */
	protected Content.Filter<WyalFile> wyalExcludes = null;

	/**
	 * Indicates whether or the compiler should produce verbose information
	 * during compilation. This is generally used for diagnosing bugs in the
	 * compiler.
	 */
	protected boolean verbose = false;

	/**
	 * Indicates whether or the compiler should produce debugging information
	 * during compilation. This is generally used for advanced diagnosis of bugs
	 * in the compiler.
	 */
	protected boolean debug = false;

	/**
	 * In decompilation mode, the build task will generate wyal files from wycs
	 * files (rather than the other way around).
	 */
	protected boolean decompile = false;

	// ==========================================================================
	// Constructors & Configuration
	// ==========================================================================

	public WycsBuildTask() {
		this.registry = new Registry();
		this.wycsDir = new VirtualRoot(registry);
	}

	public WycsBuildTask(Content.Registry registry) {
		this.registry = registry;
		this.wycsDir = new VirtualRoot(registry);
	}

	public void setLogOut(PrintStream logout) {
		this.logout = logout;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setDecompile(boolean decompile) {
		this.decompile = decompile;
	}

	public void setWyalDir(File wyalDir) throws IOException {
		this.wyalDir = new DirectoryRoot(wyalDir, wyalFileFilter, registry);
		if (wycsDir instanceof VirtualRoot) {
			// The point here is to ensure that when this build task is used in
			// a standalone fashion, that wycs files are actually written to
			// disk.
			this.wycsDir = new DirectoryRoot(wyalDir, wycsFileFilter, registry);
		}
	}

	public void setWycsDir(File wycsdir) throws IOException {
		this.wycsDir = new DirectoryRoot(wycsdir, wycsFileFilter, registry);
	}

	public void setWycsPath(List<File> roots) throws IOException {
		wycspath.clear();
		for (File root : roots) {
			try {
				if (root.getName().endsWith(".jar")) {
					wycspath.add(new JarFileRoot(root, registry));
				} else {
					wycspath.add(new DirectoryRoot(root, wycsFileFilter,
							registry));
				}
			} catch (IOException e) {
				if (verbose) {
					logout.println("Warning: " + root
							+ " is not a valid package root");
				}
			}
		}
	}

	public void setBootPath(List<File> roots) throws IOException {
		bootpath.clear();
		for (File root : roots) {
			try {
				if (root.getName().endsWith(".jar")) {
					bootpath.add(new JarFileRoot(root, registry));
				} else {
					bootpath.add(new DirectoryRoot(root, wyalFileFilter,
							registry));
				}
			} catch (IOException e) {
				if (verbose) {
					logout.println("Warning: " + root
							+ " is not a valid package root");
				}
			}
		}
	}

	public void setPipelineModifiers(List<Pipeline.Modifier> modifiers) {
		this.pipelineModifiers = new ArrayList<Pipeline.Modifier>(modifiers);
	}

	public void setIncludes(String includes) {
		String[] split = includes.split(",");
		Content.Filter<WyalFile> wyalFilter = null;

		for (String s : split) {
			if (s.endsWith(".wyal")) {
				String name = s.substring(0, s.length() - 5);
				Content.Filter<WyalFile> nf = Content.filter(name,
						WyalFile.ContentType);
				wyalFilter = wyalFilter == null ? nf : Content.or(nf,
						wyalFilter);
			}
		}

		if (wyalFilter != null) {
			this.wyalIncludes = wyalFilter;
		}
	}

	public void setExcludes(String excludes) {
		String[] split = excludes.split(",");
		Content.Filter<WyalFile> wyalFilter = null;
		for (String s : split) {
			if (s.endsWith(".wyal")) {
				String name = s.substring(0, s.length() - 5);
				Content.Filter<WyalFile> nf = Content.filter(name,
						WyalFile.ContentType);
				wyalFilter = wyalFilter == null ? nf : Content.or(nf,
						wyalFilter);
			}
		}

		this.wyalExcludes = wyalFilter;
	}

	// ==========================================================================
	// Build Methods
	// ==========================================================================

	/**
	 * Building the given source files.
	 *
	 * @param _args
	 */
	public void build(List<File> files) throws Exception {
		if (decompile) {
			if (wycsDir instanceof DirectoryRoot) {
				DirectoryRoot wd = (DirectoryRoot) wycsDir;
				buildEntries(getMatchingFiles(files, wd, WycsFile.ContentType));
			} else {
				System.out
						.println("WARNING: decompiling without properly specified wycsdir");
			}
		} else {
			buildEntries(getMatchingFiles(files, wyalDir, WyalFile.ContentType));
		}
	}

	/**
	 * Build all source files which have been modified.
	 *
	 * @param _args
	 */
	public int buildAll() throws Exception {
		if(decompile) {
			List<Path.Entry<WycsFile>> delta = getModifiedSourceFiles(wycsDir,
					Content.filter("**", WycsFile.ContentType), wyalDir,
					WyalFile.ContentType);
			buildEntries(delta);
			return delta.size();
		} else {
			List<Path.Entry<WyalFile>> delta = getModifiedSourceFiles(wyalDir,
					wyalIncludes, wycsDir, WycsFile.ContentType);
			buildEntries(delta);
			return delta.size();
		}
	}

	protected <T> void buildEntries(List<Path.Entry<T>> delta) throws Exception {

		// ======================================================================
		// Initialise Project
		// ======================================================================

		StdProject project = initialiseProject();

		// ======================================================================
		// Initialise Build Rules
		// ======================================================================

		if (decompile) {
			addDecompileBuildRules(project);
		} else {
			addCompileBuildRules(project);
		}

		// ======================================================================
		// Build!
		// ======================================================================

		project.build(delta);

		flush();
	}

	// ==========================================================================
	// Misc
	// ==========================================================================

	/**
	 *
	 * @return
	 * @throws IOException
	 */
	protected StdProject initialiseProject() throws IOException {
		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();

		if (wyalDir != null) {
			roots.add(wyalDir);
		}
		roots.add(wycsDir);
		roots.addAll(wycspath);
		roots.addAll(bootpath);

		// second, construct the module loader
		return new StdProject(roots);
	}

	/**
	 * Add all build rules to the project. By default, this adds a standard
	 * build rule for compiling whiley files to wyil files using the
	 * <code>Whiley2WyilBuilder</code>.
	 *
	 * @param project
	 */
	protected void addCompileBuildRules(StdProject project) {
		if (wyalDir != null) {
			Pipeline pipeline = initialisePipeline();

			if (pipelineModifiers != null) {
				pipeline.apply(pipelineModifiers);
			}

			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.
			Wyal2WycsBuilder builder = new Wyal2WycsBuilder(project, pipeline);

			if (verbose) {
				builder.setLogger(new Logger.Default(System.err));
			}
			builder.setDebug(debug);

			project.add(new StdBuildRule(builder, wyalDir, wyalIncludes,
					wyalExcludes, wycsDir));
		}
	}

	protected void addDecompileBuildRules(StdProject project) {
		if (wycsDir != null) {
			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.
			Wycs2WyalBuilder builder = new Wycs2WyalBuilder(project);

			if (verbose) {
				builder.setLogger(new Logger.Default(System.err));
			}
			builder.setDebug(debug);

			project.add(new StdBuildRule(builder, wycsDir, Content.filter("**",
					WycsFile.ContentType), null, wyalDir));
		}
	}

	/**
	 * Initialise the Wyil pipeline to be used for compiling Whiley files. The
	 * default implementation just returns <code>Pipeline.defaultPipeline</code>
	 * .
	 *
	 * @return
	 */
	protected Pipeline initialisePipeline() {
		return new Pipeline(defaultPipeline);
	}

	/**
	 * Generate the list of source files whose modification time is after that
	 * of their binary counterpart. This is useful for determining which source
	 * files are out-of-date and should be scheduled for recompiliation.
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
			// first, check whether wycs file out-of-date with source file
			if (binary == null || binary.lastModified() < source.lastModified()) {
				sources.add(source);
			}
		}

		return sources;
	}

	/**
	 * Generate the list of matching path entries corresponding to thegiven
	 * files on the filesystem. If one match cannot be found, an error is
	 * thrown.
	 *
	 * @return
	 * @throws IOException
	 */
	public static <T> List<Path.Entry<T>> getMatchingFiles(
			List<File> files, DirectoryRoot dir,
			Content.Type<T> contentType) throws IOException {
		List<Path.Entry<T>> matches = dir.find(files, contentType);
		for (int i = 0; i != matches.size(); ++i) {
			File file = files.get(i);
			Path.Entry<T> entry = matches.get(i);
			if (entry == null) {
				throw new RuntimeException("file not found: " + file);
			}
		}
		return matches;
	}

	/**
	 * Flush all built files to disk.
	 */
	protected void flush() throws IOException {
		// NOTE: in principle we want to flush the wyaldir, since in
		// decompilation mode this results in writing the decompiled file to
		// disk. However, for now I have disabled this.
		// wyalDir.flush();
		wycsDir.flush();
	}
}
