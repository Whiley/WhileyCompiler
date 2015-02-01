package wyc.util;

import java.io.*;
import java.util.*;

import wybs.lang.*;
import wybs.util.*;
import wyfs.lang.Content;
import wyfs.lang.Content.Filter;
import wyfs.lang.Content.Type;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyfs.lang.Path.ID;
import wyfs.util.DirectoryRoot;
import wyfs.util.JarFileRoot;
import wyfs.util.VirtualRoot;
import wyil.transforms.*;
import wyil.builders.Wyil2WyalBuilder;
import wyil.checks.*;
import wyc.builder.WhileyBuilder;
import wyc.lang.WhileyFile;
import wycc.lang.Pipeline;
import wycc.util.Logger;
import wycc.util.Pair;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.WycsFile;
import wycs.syntax.WyalFile;
import wycs.transforms.SmtVerificationCheck;
import wycs.transforms.VerificationCheck;
import wycs.util.WycsBuildTask;
import wyil.io.WyilFilePrinter;
import wyil.lang.WyilFile;

/**
 * <p>
 * Provides a general-purpose implementation for compiling Whiley source files
 * into Wyil binary files. This is designed to make it easy to write compiler
 * variants, such as for supporting different back-ends (e.g. Java or
 * JavaScript) or for interfacing with other frameworks (e.g. Ant). This class
 * is designed to be extended by clients which are providing some kind of
 * compiler extension.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class WycBuildTask {

	/**
	 * The purpose of the source file filter is simply to ensure only source
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter whileyFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().endsWith(".whiley") || f.isDirectory();
		}
	};

	/**
	 * The purpose of the binary file filter is simply to ensure only binary
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter wyilFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().endsWith(".wyil") || f.isDirectory();
		}
	};

	/**
	 * The purpose of the wyal file filter is simply to ensure only binary
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter wyalFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().endsWith(".wyal") || f.isDirectory();
		}
	};

	/**
	 * The purpose of the wycs file filter is simply to ensure only binary
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter wycsFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().endsWith(".wycs") || f.isDirectory();
		}
	};

	/**
	 * The purpose of the wyil or wycs file filter is simply to ensure only wycs
	 * or wyil files are loaded in a given directory root. It is not strictly
	 * necessary for correct operation, although hopefully it offers some
	 * performance benefits.
	 */
	public static final FileFilter wyilOrWycsFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().endsWith(".wyil") || f.getName().endsWith(".wycs") || f.isDirectory();
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
			if(t == WhileyFile.ContentType) {
				return "whiley";
			} else if(t == WyilFile.ContentType) {
				return "wyil";
			} else if(t == WyalFile.ContentType) {
				return "wyal";
			} else if(t == WycsFile.ContentType) {
				return "wycs";
			} else {
				return "dat";
			}
		}
	}

	public static final List<Pipeline.Template> defaultPipeline = Collections
			.unmodifiableList(new ArrayList<Pipeline.Template>() {
				{
					// add(new Pipeline.Template(WyilFilePrinter.class,
					// Collections.EMPTY_MAP));
					add(new Pipeline.Template(DefiniteAssignmentCheck.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(ModuleCheck.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(LoopVariants.class,
							Collections.EMPTY_MAP));
//					add(new Pipeline.Template(ConstantPropagation.class,
//							Collections.EMPTY_MAP));
					add(new Pipeline.Template(CoercionCheck.class,
							Collections.EMPTY_MAP));
//					add(new Pipeline.Template(DeadCodeElimination.class,
//							Collections.EMPTY_MAP));
//					add(new Pipeline.Template(LiveVariablesAnalysis.class,
//							Collections.EMPTY_MAP));
					// add(new Pipeline.Template(WyilFilePrinter.class,
					// Collections.EMPTY_MAP));
				}
			});

	/**
	 * Register default transforms. This is necessary so they can be referred to
	 * from the command-line using abbreviated names, rather than their full
	 * names.
	 */
	static {
		Pipeline.register(DefiniteAssignmentCheck.class);
		Pipeline.register(LoopVariants.class);
		Pipeline.register(ConstantPropagation.class);
		Pipeline.register(ModuleCheck.class);
		Pipeline.register(CoercionCheck.class);
		Pipeline.register(WyilFilePrinter.class);
		Pipeline.register(DeadCodeElimination.class);
		Pipeline.register(LiveVariablesAnalysis.class);
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
	 * The boot path contains the location of the whiley runtime (wyrt) library.
	 */
	protected ArrayList<Path.Root> bootpath = new ArrayList<Path.Root>();

	/**
	 * The whiley path identifies additional items (i.e. libraries or
	 * directories) which the compiler uses to resolve symbols (e.g. module
	 * names, functions, etc).
	 */
	protected ArrayList<Path.Root> whileypath = new ArrayList<Path.Root>();

	/**
	 * The whiley source directory is the filesystem directory from which the
	 * compiler will look for (whiley) source files.
	 */
	protected DirectoryRoot whileyDir;

	/**
	 * The wyil directory is the filesystem directory where all generated wyil
	 * files will be placed.
	 */
	protected Path.Root wyilDir;

	/**
	 * The wyal directory is the filesystem directory where all generated wyal
	 * files will be placed.
	 */
	protected Path.Root wyalDir;

	/**
	 * The wycs directory is the filesystem directory where all generated wycs
	 * files will be placed.
	 */
	protected Path.Root wycsDir;

	/**
	 * Identifies which whiley source files should be considered for
	 * compilation. By default, all files reachable from srcdir are considered.
	 */
	protected Content.Filter<WhileyFile> whileyIncludes = Content.filter("**", WhileyFile.ContentType);

	/**
	 * Identifies which whiley sources files should not be considered for
	 * compilation. This overrides any identified by <code>whileyIncludes</code>
	 * . By default, no files files reachable from srcdir are excluded.
	 */
	protected Content.Filter<WhileyFile> whileyExcludes = null;

	/**
	 * Identifies which wyil files generated from whiley source files which
	 * should be considered for compilation. By default, all files reachable
	 * from <code>whileyDestDir</code> are considered.
	 */
	protected Content.Filter<WyilFile> wyilIncludes = Content.filter("**", WyilFile.ContentType);

	/**
	 * Identifies which wyil files generated from whiley source files should not
	 * be considered for compilation. This overrides any identified by
	 * <code>wyilIncludes</code>.
	 */
	protected Content.Filter<WyilFile> wyilExcludes = null;

	/**
	 * Identifies which wyal files generated from whiley source files which
	 * should be considered for compilation. By default, all files reachable
	 * from <code>whileytDir</code> are considered.
	 */
	protected Content.Filter<WyalFile> wyalIncludes = Content.filter("**", WyalFile.ContentType);

	/**
	 * Identifies which wyal files generated from whiley source files should not
	 * be considered for compilation.  This overrides any identified by
	 * <code>wyalIncludes</code>.
	 */
	protected Content.Filter<WyalFile> wyalExcludes = null;

	/**
	 * The pipeline modifiers which will be applied to the default pipeline.
	 */
	protected ArrayList<Pipeline.Modifier> pipelineModifiers;

	/**
	 * Indicates whether or not the compiler should produce verbose information
	 * during compilation. This is generally used for diagnosing bugs in the
	 * compiler.
	 */
	protected boolean verbose = false;

	/**
	 * Indicates whether or not the compiler should enable detailed verification
	 * checking of pre- and post-conditions.
	 */
	protected boolean verification = false;

	/**
	 * Indicates whether or not the compiler should generate the intermediate
	 * verification conditions. If verification is true, then this is done
	 * automatically. Otherwise, you can force it with this flag without
	 * actually performing verification.
	 */
	protected boolean verificationConditions = false;
	
	/**
	 * Indicates whether or not the compiler should enable detailed verification
	 * checking of pre- and post-conditions using an external SMT solver.
	 */
	protected boolean smtVerification = false;


	// ==========================================================================
	// Constructors & Configuration
	// ==========================================================================

	public WycBuildTask() {
		this.registry = new Registry();
		this.wyilDir = new VirtualRoot(registry);
		this.wyalDir = new VirtualRoot(registry);
		this.wycsDir = new VirtualRoot(registry);
	}

	public WycBuildTask(Content.Registry registry) {
		this.registry = registry;
		this.wyilDir = new VirtualRoot(registry);
		this.wyalDir = new VirtualRoot(registry);
		this.wycsDir = new VirtualRoot(registry);
	}

	public void setLogOut(PrintStream logout) {
		this.logout = logout;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setVerification(boolean verification) {
		this.verification = verification;
	}

	public void setVerificationConditions(boolean flag) {
		this.verificationConditions = flag;
	}
	
	public void setSmtVerification(boolean verification) {
		this.smtVerification = verification;
	}

	public boolean getVerification() {
		return verification;
	}

	public boolean getVerificationConditions() {
		return verificationConditions;
	}
	
	public void setPipelineModifiers(List<Pipeline.Modifier> modifiers) {
		this.pipelineModifiers = new ArrayList<Pipeline.Modifier>(modifiers);
	}

	public void setWhileyDir(File whileydir) throws IOException {
		this.whileyDir = new DirectoryRoot(whileydir, whileyFileFilter, registry);
		if(wyilDir instanceof VirtualRoot) {
			// The point here is to ensure that when this build task is used in
			// a standalone fashion, that wyil files are actually written to
			// disk.
			this.wyilDir = new DirectoryRoot(whileydir, wyilFileFilter, registry);
		}
	}

    public void setWyilDir (File wyildir) throws IOException {
        this.wyilDir = new DirectoryRoot(wyildir, wyilFileFilter, registry);
    }

    public void setWyalDir (File wyaldir) throws IOException {
        this.wyalDir = new DirectoryRoot(wyaldir, wyalFileFilter, registry);
    }

    public void setWycsDir (File wycsdir) throws IOException {
        this.wycsDir = new DirectoryRoot(wycsdir, wycsFileFilter, registry);
    }

    public void setWhileyPath(List<File> roots) throws IOException {
		whileypath.clear();
		for (File root : roots) {
			try {
				if (root.getName().endsWith(".jar")) {
					whileypath.add(new JarFileRoot(root, registry));
				} else {
					whileypath.add(new DirectoryRoot(root, wyilFileFilter, registry));
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
					bootpath.add(new DirectoryRoot(root, wyilOrWycsFileFilter, registry));
				}
			} catch (IOException e) {
				if (verbose) {
					logout.println("Warning: " + root
							+ " is not a valid package root");
				}
			}
		}
	}

    public void setIncludes(String includes) {
    	String[] split = includes.split(",");
    	Content.Filter<WhileyFile> whileyFilter = null;
    	Content.Filter<WyilFile> wyilFilter = null;

		for (String s : split) {
			if (s.endsWith(".whiley")) {
				String name = s.substring(0, s.length() - 7);
				Content.Filter<WhileyFile> nf1 = Content.filter(name,
						WhileyFile.ContentType);
				whileyFilter = whileyFilter == null ? nf1 : Content.or(nf1,
						whileyFilter);
				// in this case, we are explicitly including some whiley source
				// files. This implicitly means the corresponding wyil files are
				// included.
				Content.Filter<WyilFile> nf2 = Content.filter(name,
						WyilFile.ContentType);
				wyilFilter = wyilFilter == null ? nf2 : Content.or(nf2,
						wyilFilter);
			} else if (s.endsWith(".wyil")) {
				// in this case, we are explicitly including some wyil files.
				String name = s.substring(0, s.length() - 5);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilFilter = wyilFilter == null ? nf : Content.or(nf,
						wyilFilter);
			}
		}

		if(whileyFilter != null) {
			this.whileyIncludes = whileyFilter;
		}
		if(wyilFilter != null) {
			this.wyilIncludes = wyilFilter;
		}
    }

    public void setExcludes(String excludes) {
    	String[] split = excludes.split(",");
    	Content.Filter<WhileyFile> whileyFilter = null;
		Content.Filter<WyilFile> wyilFilter = null;

    	for(String s : split) {
    		if(s.endsWith(".whiley")) {
    			String name = s.substring(0,s.length()-7);
    			Content.Filter<WhileyFile> nf1 = Content.filter(name,WhileyFile.ContentType);
    			whileyFilter = whileyFilter == null ? nf1 : Content.or(nf1, whileyFilter);
    			Content.Filter<WyilFile> nf2 = Content.filter(name,WyilFile.ContentType);
				wyilFilter = wyilFilter == null ? nf2 : Content.or(
						nf2, wyilFilter);
    		} else if (s.endsWith(".wyil")) {
				String name = s.substring(0, s.length() - 5);
				Content.Filter<WyilFile> nf = Content.filter(name,
						WyilFile.ContentType);
				wyilFilter = wyilFilter == null ? nf : Content.or(
						nf, wyilFilter);
			}
    	}

    	this.whileyExcludes = whileyFilter;
    	this.wyilExcludes = wyilFilter;
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
		List<Path.Entry<WhileyFile>> entries = whileyDir.find(files,
				WhileyFile.ContentType);
		int j = 0;
		for (int i = 0; j < files.size(); ++i, ++j) {
			if (entries.get(i) == null) {
				logout.println("WARNING: ignoring unknown file "
						+ files.get(j).getName());
				entries.remove(i--);
			}
		}
		buildEntries(entries);
	}

    /**
	 * Build all source files which have been modified.
	 *
	 * @param _args
	 */
	public int buildAll() throws Exception {
		List delta = getModifiedSourceFiles();
		buildEntries(delta);
		return delta.size();
	}

	protected <T> void buildEntries(List<Path.Entry<T>> delta) throws Exception {

		// ======================================================================
		// Initialise Project
		// ======================================================================

		StdProject project = initialiseProject();

		// ======================================================================
		// Initialise Build Rules
		// ======================================================================

		addBuildRules(project);

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

		if(whileyDir != null) {
			roots.add(whileyDir);
		}

		roots.add(wyilDir);
		roots.add(wyalDir);
		roots.add(wycsDir);
		roots.addAll(whileypath);
		roots.addAll(bootpath);

		// second, construct the module loader
		return new StdProject(roots);
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

	protected List getModifiedSourceFiles() throws IOException {
		if (whileyDir == null) {
			// Note, whileyDir can be null if e.g. compiling wyil -> wyjc
			return new ArrayList();
		} else {
			return getModifiedSourceFiles(whileyDir, whileyIncludes, wyilDir,
					WyilFile.ContentType);
		}
	}

	/**
	 * Add all build rules to the project. By default, this adds a standard
	 * build rule for compiling whiley files to wyil files using the
	 * <code>Whiley2WyilBuilder</code>.
	 *
	 * @param project
	 */
	protected void addBuildRules(StdProject project) {
		if(whileyDir != null) {
			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.
			Pipeline wyilPipeline = initialisePipeline();

			if(pipelineModifiers != null) {
        		wyilPipeline.apply(pipelineModifiers);
        	}

			// ========================================================
			// Whiley => Wyil Compilation Rule
			// ========================================================

			WhileyBuilder wyilBuilder = new WhileyBuilder(project,wyilPipeline);

			if(verbose) {
				wyilBuilder.setLogger(new Logger.Default(System.err));
			}

			project.add(new StdBuildRule(wyilBuilder, whileyDir,
					whileyIncludes, whileyExcludes, wyilDir));

			// ========================================================
			// Wyil => Wycs Compilation Rule
			// ========================================================

			if(verification || smtVerification || verificationConditions) {

				// First, handle the conversion of wyil to wyal

				Wyil2WyalBuilder wyalBuilder = new Wyil2WyalBuilder(project);

				if(verbose) {
					wyalBuilder.setLogger(new Logger.Default(System.err));
				}

				project.add(new StdBuildRule(wyalBuilder, wyilDir,
						wyilIncludes, wyilExcludes, wyalDir));

				// Second, handle the conversion of wyal to wycs				
				Pipeline<WycsFile> wycsPipeline = new Pipeline(WycsBuildTask.defaultPipeline);

				wycsPipeline.setOption(VerificationCheck.class,"enable",verification);
				wycsPipeline.setOption(SmtVerificationCheck.class,"enable",smtVerification);
				Wyal2WycsBuilder wycsBuilder = new Wyal2WycsBuilder(project,wycsPipeline);

				if(verbose) {
					wycsBuilder.setLogger(new Logger.Default(System.err));
				}

				project.add(new StdBuildRule(wycsBuilder, wyalDir,
						wyalIncludes, wyalExcludes, wycsDir));				
			}
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
	 * Flush all built files to disk.
	 */
	protected void flush() throws IOException {
		wyilDir.flush();
		wyalDir.flush();
		wycsDir.flush();
	}		
}
