package wyc.util;

import java.io.*;
import java.util.*;

import wycommon.util.Logger;
import wycommon.util.Pair;
import wybs.lang.*;
import wybs.util.*;
import wyfs.lang.Content;
import wyfs.lang.Content.Filter;
import wyfs.lang.Content.Type;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.JarFileRoot;
import wyfs.util.VirtualRoot;
import wyc.builder.Compiler;
import wyc.builder.Decompiler;
import wyc.lang.WhileyFile;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.WycsFile;
import wycs.syntax.WyalFile;
import wyil.builders.Wyil2WyalBuilder;
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
	 * Indicates whether or not to compile or decompile.
	 */
	protected boolean compile = true;
	
	/**
	 * Indicates whether or not the compiler should generate the intermediate
	 * verification conditions. If verification is true, then this is done
	 * automatically. Otherwise, you can force it with this flag without
	 * actually performing verification.
	 */
	protected boolean verificationConditions = false;

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

	public void setCompile(boolean compile) {
		this.compile = compile;
	}

	public boolean getVerification() {
		return verification;
	}

	public boolean getVerificationConditions() {
		return verificationConditions;
	}
	
	public void setWhileyDir(File whileydir) throws IOException {
		this.whileyDir = new DirectoryRoot(whileydir, registry);
		if(wyilDir instanceof VirtualRoot) {
			// The point here is to ensure that when this build task is used in
			// a standalone fashion, that wyil files are actually written to
			// disk.
			this.wyilDir = new DirectoryRoot(whileydir, registry);
		}
	}

    public void setWyilDir (File dir) throws IOException {
        this.wyilDir = new DirectoryRoot(dir, registry);
    }

    public void setWyalDir (File dir) throws IOException {
        this.wyalDir = new DirectoryRoot(dir, registry);
    }
    
    public void setWycsDir (File dir) throws IOException {
        this.wycsDir = new DirectoryRoot(dir, registry);
    }
    
    public void setWhileyPath(List<File> roots) throws IOException {
		whileypath.clear();
		for (File root : roots) {
			try {
				if (root.getName().endsWith(".jar")) {
					whileypath.add(new JarFileRoot(root, registry));
				} else {
					whileypath.add(new DirectoryRoot(root, registry));
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
					bootpath.add(new DirectoryRoot(root, registry));
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
		if (compile) {
			List<Path.Entry<WhileyFile>> entries = whileyDir.find(files, WhileyFile.ContentType);
			stripUnknownEntries(files, entries);
			buildEntries(entries);
		} else if (wyilDir instanceof DirectoryRoot) {
			DirectoryRoot wyilRoot = (DirectoryRoot) wyilDir;
			List<Path.Entry<WyilFile>> entries = wyilRoot.find(files, WyilFile.ContentType);
			stripUnknownEntries(files, entries);
			buildEntries(entries);
		}
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

	private <T> void stripUnknownEntries(List<File> files, List<Path.Entry<T>> entries) throws Exception {
		int j = 0;
		for (int i = 0; j < files.size(); ++i, ++j) {
			if (entries.get(i) == null) {
				logout.println("WARNING: ignoring unknown file " + files.get(j).getName());
				entries.remove(i--);
			}
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

		if(compile) {
			addCompileRules(project);
		} else {
			addDecompileRules(project);
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
	protected void addCompileRules(StdProject project) {
		if(whileyDir != null) {
			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.

			// ========================================================
			// Whiley => Wyil Compilation Rule
			// ========================================================

			Compiler wyilBuilder = new Compiler(project);

			if(verbose) {
				wyilBuilder.setLogger(new Logger.Default(System.err));
			}

			project.add(new StdBuildRule(wyilBuilder, whileyDir,
					whileyIncludes, whileyExcludes, wyilDir));
			//
			// ========================================================
			// Wyil => Wycs Compilation Rule
			// ========================================================

			if (verification || verificationConditions) {

				// First, handle the conversion of wyil to wyal

				Wyil2WyalBuilder wyalBuilder = new Wyil2WyalBuilder(project);

				if (verbose) {
					wyalBuilder.setLogger(new Logger.Default(System.err));
				}

				project.add(new StdBuildRule(wyalBuilder, wyilDir, wyilIncludes, wyilExcludes, wyalDir));

				// Second, handle the conversion of wyal to wycs
				Wyal2WycsBuilder wycsBuilder = new Wyal2WycsBuilder(project);

				if (verbose) {
					wycsBuilder.setLogger(new Logger.Default(System.err));
				}

				project.add(new StdBuildRule(wycsBuilder, wyalDir, wyalIncludes, wyalExcludes, wycsDir));
			}
		}
	}

	protected void addDecompileRules(StdProject project) {
		Decompiler dcTask = new Decompiler(project);

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
		wyilDir.flush();
	}		
}
