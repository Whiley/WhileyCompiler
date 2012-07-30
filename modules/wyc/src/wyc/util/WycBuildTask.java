package wyc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import wybs.lang.Content;
import wybs.lang.Logger;
import wybs.lang.Path;
import wybs.util.DirectoryRoot;
import wybs.util.JarFileRoot;
import wybs.util.SimpleProject;
import wybs.util.StandardBuildRule;
import wybs.util.Trie;
import wyc.builder.Whiley2WyilBuilder;
import wyc.lang.WhileyFile;
import wyil.Pipeline;
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
			} 
		}
		
		public String suffix(Content.Type<?> t) {
			if(t == WhileyFile.ContentType) {
				return "whiley";
			} else if(t == WyilFile.ContentType) {
				return "wyil";
			} else {
				return "dat";
			}
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
	protected DirectoryRoot wyilDir;
	
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
	}

    public void setWyilDir (File wyildir) throws IOException {	
        this.wyilDir = new DirectoryRoot(wyildir, wyilFileFilter, registry);
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
					bootpath.add(new DirectoryRoot(root, wyilFileFilter, registry));
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
    	
		for (String s : split) {
			if (s.endsWith(".whiley")) {
				String name = s.substring(0, s.length() - 7);
				Content.Filter<WhileyFile> nf = Content.filter(name,
						WhileyFile.ContentType);
				whileyFilter = whileyFilter == null ? nf : Content.or(nf,
						whileyFilter);
			}
		}
    	
		if(whileyFilter != null) {
			this.whileyIncludes = whileyFilter;
		}
    }
    
    public void setExcludes(String excludes) {
    	String[] split = excludes.split(",");
    	Content.Filter<WhileyFile> whileyFilter = null;
    	for(String s : split) {
    		if(s.endsWith(".whiley")) {
    			String name = s.substring(0,s.length()-7);
    			Content.Filter<WhileyFile> nf = Content.filter(name,WhileyFile.ContentType);
    			whileyFilter = whileyFilter == null ? nf : Content.or(nf, whileyFilter);     			
    		} 
    	}
    	
    	this.whileyExcludes = whileyFilter;
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
	public void buildAll() throws Exception {
		buildEntries(getModifiedSourceFiles());
	}
	
	protected void buildEntries(List<Path.Entry<?>> delta) throws Exception {	
		
		// ======================================================================
		// Initialise Project
		// ======================================================================

		SimpleProject project = initialiseProject();  		

		// ======================================================================
		// Initialise Build Rules
		// ======================================================================

		addBuildRules(project);

		// ======================================================================
		// Build!
		// ======================================================================		

		project.build(delta);
		
		flush();
		
		logout.println("Compiled " + delta.size() + " source file(s)");
	}
	
	// ==========================================================================
	// Misc
	// ==========================================================================

	 /**
     * 
     * @return
     * @throws IOException
     */
	protected SimpleProject initialiseProject() throws IOException {
		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();
		
		if(whileyDir != null) {
			roots.add(whileyDir);
		}
		
		roots.add(wyilDir);
		roots.addAll(whileypath);
		roots.addAll(bootpath);

		// second, construct the module loader
		return new SimpleProject(roots);
	}
	
	/**
	 * Initialise the Wyil pipeline to be used for compiling Whiley files. The
	 * default implementation just returns <code>Pipeline.defaultPipeline</code>
	 * .
	 * 
	 * @return
	 */
	protected Pipeline initialisePipeline() {
		return new Pipeline(Pipeline.defaultPipeline);
	}
	
	/**
	 * Add all build rules to the project. By default, this adds a standard
	 * build rule for compiling whiley files to wyil files using the
	 * <code>Whiley2WyilBuilder</code>.
	 * 
	 * @param project
	 */
	protected void addBuildRules(SimpleProject project) {
		if(whileyDir != null) {
			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.
			Pipeline pipeline = initialisePipeline();    		

			if(pipelineModifiers != null) {
        		pipeline.apply(pipelineModifiers);
        	}
			
			Whiley2WyilBuilder builder = new Whiley2WyilBuilder(project,pipeline);

			if(verbose) {			
				builder.setLogger(new Logger.Default(System.err));
			}

			StandardBuildRule rule = new StandardBuildRule(builder);		

			rule.add(whileyDir, whileyIncludes, whileyExcludes, wyilDir,
					WhileyFile.ContentType, WyilFile.ContentType);

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
					module = module.substring(0,module.length()-7);						
					Path.ID mid = Trie.fromString(module);						
					Path.Entry<WhileyFile> e = whileyDir.get(mid,WhileyFile.ContentType);
					if (e != null) {							
						sources.add(e);
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
