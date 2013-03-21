package wyc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wybs.lang.Content;
import wybs.lang.Logger;
import wybs.lang.Path;
import wybs.lang.Pipeline;
import wybs.util.DirectoryRoot;
import wybs.util.JarFileRoot;
import wybs.util.StandardProject;
import wybs.util.StandardBuildRule;
import wybs.util.Trie;
import wyil.transforms.*;
import wyil.builders.Wyil2WycsBuilder;
import wyil.checks.*;
import wyc.builder.WhileyBuilder;
import wyc.lang.WhileyFile;
import wycs.lang.WycsFile;
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
			} else if(suffix.equals("wycs")) {
				e.associate(WycsFile.ContentType, null);				
			} 
		}
		
		public String suffix(Content.Type<?> t) {
			if(t == WhileyFile.ContentType) {
				return "whiley";
			} else if(t == WyilFile.ContentType) {
				return "wyil";
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
					// add(new Template(WyilFilePrinter.class,
					// Collections.EMPTY_MAP));
					add(new Pipeline.Template(DefiniteAssignmentCheck.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(ModuleCheck.class, Collections.EMPTY_MAP));
					add(new Pipeline.Template(RuntimeAssertions.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(BackPropagation.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(LoopVariants.class, Collections.EMPTY_MAP));
					add(new Pipeline.Template(ConstantPropagation.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(CoercionCheck.class, Collections.EMPTY_MAP));
					add(new Pipeline.Template(DeadCodeElimination.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(LiveVariablesAnalysis.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(WyilFilePrinter.class,
							Collections.EMPTY_MAP));
				}
			});

	/**
	 * Register default transforms. This is necessary so they can be referred to
	 * from the command-line using abbreviated names, rather than their full
	 * names.
	 */
	static {
		Pipeline.register(BackPropagation.class);
		Pipeline.register(DefiniteAssignmentCheck.class);
		Pipeline.register(LoopVariants.class);
		Pipeline.register(ConstantPropagation.class);
		Pipeline.register(ModuleCheck.class);
		Pipeline.register(RuntimeAssertions.class);
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
	protected DirectoryRoot wyilDir;

	/**
	 * The wyil directory is the filesystem directory where all generated wycs
	 * files will be placed.
	 */
	protected DirectoryRoot wycsDir;
	
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
	 * <code>wyilBinaryIncludes</code> . By default, no files files reachable from
	 * <code>wyilDestDir</code> are excluded.
	 */
	protected Content.Filter<WyilFile> wyilExcludes = null;

	/**
	 * The pipeline modifiers which will be applied to the default pipeline.
	 */
	protected ArrayList<Pipeline.Modifier> pipelineModifiers;
	
	/**
	 * Indicates whether or the compiler should produce verbose information
	 * during compilation. This is generally used for diagnosing bugs in the
	 * compiler.
	 */
	protected boolean verbose = false;	
	
	// ==========================================================================
	// Constructors & Configuration
	// ========================================================================== 
	
	public WycBuildTask() {
		this.registry = new Registry();		
	}
	
	public WycBuildTask(Content.Registry registry) {
		this.registry = registry;		
	}
	
	public void setLogOut(PrintStream logout) {
		this.logout = logout;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	public void setPipelineModifiers(List<Pipeline.Modifier> modifiers) {		
		this.pipelineModifiers = new ArrayList<Pipeline.Modifier>(modifiers);
	}
		
	public void setWhileyDir(File whileydir) throws IOException {
		this.whileyDir = new DirectoryRoot(whileydir, whileyFileFilter, registry);
		if(wyilDir == null) {
			this.wyilDir = new DirectoryRoot(whileydir, wyilFileFilter, registry);
		}
		if(wycsDir == null) {
			this.wycsDir = new DirectoryRoot(whileydir, wycsFileFilter, registry);
		}
	}

    public void setWyilDir (File wyildir) throws IOException {	
        this.wyilDir = new DirectoryRoot(wyildir, wyilFileFilter, registry);
    }
    
    public void setWycsDir (File wycsdir) throws IOException {	
        this.wycsDir = new DirectoryRoot(wycsdir, wyilFileFilter, registry);
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
		List<Path.Entry<?>> entries = getSourceFiles(files);
		buildEntries(entries);    
	}

    /**
	 * Build all source files which have been modified.
	 * 
	 * @param _args
	 */
	public int buildAll() throws Exception {
		List<Path.Entry<?>> delta = getModifiedSourceFiles();
		buildEntries(delta);
		return delta.size();
	}
	
	protected void buildEntries(List<Path.Entry<?>> delta) throws Exception {	
		
		// ======================================================================
		// Initialise Project
		// ======================================================================

		StandardProject project = initialiseProject();  		

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
	protected StandardProject initialiseProject() throws IOException {
		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();
		
		if(whileyDir != null) {
			roots.add(whileyDir);
		}
		
		roots.add(wycsDir);
		roots.add(wyilDir);
		roots.addAll(whileypath);
		roots.addAll(bootpath);

		// second, construct the module loader
		return new StandardProject(roots);
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
	 * Add all build rules to the project. By default, this adds a standard
	 * build rule for compiling whiley files to wyil files using the
	 * <code>Whiley2WyilBuilder</code>.
	 * 
	 * @param project
	 */
	protected void addBuildRules(StandardProject project) {
		if(whileyDir != null) {
			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.
			Pipeline pipeline = initialisePipeline();    		

			if(pipelineModifiers != null) {
        		pipeline.apply(pipelineModifiers);
        	}
			
			// ========================================================
			// Whiley => Wyil Compilation Rule
			// ========================================================
			
			WhileyBuilder wyilBuilder = new WhileyBuilder(project,pipeline);

			if(verbose) {			
				wyilBuilder.setLogger(new Logger.Default(System.err));
			}

			StandardBuildRule rule = new StandardBuildRule(wyilBuilder);		

			rule.add(whileyDir, whileyIncludes, whileyExcludes, wyilDir,
					WhileyFile.ContentType, WyilFile.ContentType);

			project.add(rule);
			
			// ========================================================
			// Wyil => Wycs Compilation Rule
			// ========================================================

			Wyil2WycsBuilder wycsBuilder = new Wyil2WycsBuilder(project);

			if(verbose) {			
				wycsBuilder.setLogger(new Logger.Default(System.err));
			}

			rule = new StandardBuildRule(wycsBuilder);		

			Content.Filter<WyilFile> wyilIncludes = Content.filter("**",
					WyilFile.ContentType);
			rule.add(wyilDir, wyilIncludes, null, wycsDir,
					WyilFile.ContentType, WycsFile.ContentType);
			
			project.add(rule);			
		}
	}
		
	protected List<Path.Entry<?>> getSourceFiles(List<File> delta)
			throws IOException {
		// Now, touch all source files which have modification date after
		// their corresponding binary.
		ArrayList<Path.Entry<?>> sources = new ArrayList<Path.Entry<?>>();
		
		if(whileyDir != null) {			
			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.
			String whileyDirPath = whileyDir.location().getCanonicalPath();
			for (File file : delta) {
				String filePath = file.getCanonicalPath();
				if(filePath.startsWith(whileyDirPath)) {
					int end = whileyDirPath.length();
					if(end > 1) {
						end++;
					}					
					String module = filePath.substring(end).replace(File.separatorChar, '.');
					
					if(module.endsWith(".whiley")) {
						module = module.substring(0,module.length()-7);						
						Path.ID mid = Trie.fromString(module);
						Path.Entry<WhileyFile> entry = whileyDir.get(mid,WhileyFile.ContentType);
						if (entry != null) {							
							sources.add(entry);
						}
					}
					
				}
			}
		}
		
		return sources;
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
	protected List<Path.Entry<?>> getModifiedSourceFiles() throws IOException {
		// Now, touch all source files which have modification date after
		// their corresponding binary.
		ArrayList<Path.Entry<?>> sources = new ArrayList<Path.Entry<?>>();

		if (whileyDir != null) {
			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.
			for (Path.Entry<WhileyFile> source : whileyDir.get(whileyIncludes)) {
				Path.Entry<WyilFile> binary = wyilDir.get(source.id(),
						WyilFile.ContentType);

				// first, check whether wyil file out-of-date with source file
				if (binary == null
						|| binary.lastModified() < source.lastModified()) {
					sources.add(source);
				}
			}
		}
		return sources;
	}
	
	/**
	 * Flush all built files to disk.
	 */
	protected void flush() throws IOException {
		if(whileyDir != null) {
			// only flush wyilDir if it could contain wyil files which were
			// generated from whiley source files.
			wyilDir.flush();
		}
	}	
}
