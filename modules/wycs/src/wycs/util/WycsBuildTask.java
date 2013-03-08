package wycs.util;

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
import wycs.WycsBuilder;
import wycs.lang.WycsFile;
import wycs.transforms.*;

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
	 * The purpose of the wycs file filter is simply to ensure only wycs
	 * or wyil files are loaded in a given directory root. It is not strictly
	 * necessary for correct operation, although hopefully it offers some
	 * performance benefits.
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
			
			if(suffix.equals("wycs")) {
				e.associate(WycsFile.ContentType, null);				
			} 
		}
		
		public String suffix(Content.Type<?> t) {
			if(t == WycsFile.ContentType) {
				return "wycs";
			} else {
				return "dat";
			}
		}
	}
	
	public static final List<Pipeline.Template> defaultPipeline = Collections
			.unmodifiableList(new ArrayList<Pipeline.Template>() {
				{
					add(new Pipeline.Template(TypePropagation.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(ConstraintInline.class,
							Collections.EMPTY_MAP));
					add(new Pipeline.Template(VerificationCheck.class,
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
		Pipeline.register(ConstraintInline.class);
		Pipeline.register(VerificationCheck.class);
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
	 * The whiley path identifies additional items (i.e. libraries or
	 * directories) which the compiler uses to resolve symbols (e.g. module
	 * names, functions, etc).
	 */
	protected ArrayList<Path.Root> wycspath = new ArrayList<Path.Root>();
	
	/**
	 * The whiley source directory is the filesystem directory from which the
	 * compiler will look for (wycs) source files.
	 */
	protected DirectoryRoot wycsDir;
		
	/**
	 * The pipeline modifiers which will be applied to the default pipeline.
	 */
	protected ArrayList<Pipeline.Modifier> pipelineModifiers;
	
	/**
	 * Identifies which wycs source files should be considered for verification.
	 * By default, all files reachable from srcdir are considered.
	 */
	protected Content.Filter<WycsFile> wycsIncludes = Content.filter("**", WycsFile.ContentType);
	
	/**
	 * Identifies which wycs sources files should not be considered for
	 * compilation. This overrides any identified by <code>whileyIncludes</code>
	 * . By default, no files files reachable from srcdir are excluded.
	 */
	protected Content.Filter<WycsFile> wycsExcludes = null;
				
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

	// ==========================================================================
	// Constructors & Configuration
	// ========================================================================== 
	
	public WycsBuildTask() {
		this.registry = new Registry();		
	}
	
	public WycsBuildTask(Content.Registry registry) {
		this.registry = registry;		
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
					wycspath.add(new DirectoryRoot(root, wycsFileFilter, registry));
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
					bootpath.add(new DirectoryRoot(root, wycsFileFilter, registry));
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
    	Content.Filter<WycsFile> wycsFilter = null;
    	
		for (String s : split) {
			if (s.endsWith(".wycs")) {
				String name = s.substring(0, s.length() - 7);
				Content.Filter<WycsFile> nf = Content.filter(name,
						WycsFile.ContentType);
				wycsFilter = wycsFilter == null ? nf : Content.or(nf,
						wycsFilter);
			}
		}
    	
		if(wycsFilter != null) {
			this.wycsIncludes = wycsFilter;
		}
    }
    
    public void setExcludes(String excludes) {
    	String[] split = excludes.split(",");
    	Content.Filter<WycsFile> wycsFilter = null;
    	for(String s : split) {
    		if(s.endsWith(".wycs")) {
    			String name = s.substring(0,s.length()-7);
    			Content.Filter<WycsFile> nf = Content.filter(name,WycsFile.ContentType);
    			wycsFilter = wycsFilter == null ? nf : Content.or(nf, wycsFilter);     			
    		} 
    	}
    	
    	this.wycsExcludes = wycsFilter;
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
		
		if(wycsDir != null) {
			roots.add(wycsDir);
		}
				
		roots.addAll(wycspath);
		roots.addAll(bootpath);

		// second, construct the module loader
		return new StandardProject(roots);
	}
		
	/**
	 * Add all build rules to the project. By default, this adds a standard
	 * build rule for compiling whiley files to wyil files using the
	 * <code>Whiley2WyilBuilder</code>.
	 * 
	 * @param project
	 */
	protected void addBuildRules(StandardProject project) {
		if(wycsDir != null) {
			Pipeline pipeline = initialisePipeline();    		

			if(pipelineModifiers != null) {
        		pipeline.apply(pipelineModifiers);
        	}
			
			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.
			WycsBuilder builder = new WycsBuilder(project,pipeline);

			if(verbose) {			
				builder.setLogger(new Logger.Default(System.err));
			}
			builder.setDebug(debug);

			StandardBuildRule rule = new StandardBuildRule(builder);		

			// FIXME: really we should have a binary content type for WycsFiles.
			rule.add(wycsDir, wycsIncludes, wycsExcludes, wycsDir,
					WycsFile.ContentType, WycsFile.ContentType);
			
			project.add(rule);
		}
	}
		
	protected List<Path.Entry<?>> getSourceFiles(List<File> delta)
			throws IOException {
		// Now, touch all source files which have modification date after
		// their corresponding binary.
		ArrayList<Path.Entry<?>> sources = new ArrayList<Path.Entry<?>>();
		
		if(wycsDir != null) {			
			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.
			String wycsDirPath = wycsDir.location().getCanonicalPath();
			for (File file : delta) {
				String filePath = file.getCanonicalPath();
				if(filePath.startsWith(wycsDirPath)) {
					int end = wycsDirPath.length();
					if(end > 1) {
						end++;
					}					
					String module = filePath.substring(end).replace(File.separatorChar, '.');
					
					if(module.endsWith(".wycs")) {
						module = module.substring(0,module.length()-5);						
						Path.ID mid = Trie.fromString(module);
						Path.Entry<WycsFile> entry = wycsDir.get(mid,WycsFile.ContentType);
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

		if (wycsDir != null) {
			// whileydir can be null if a subclass of this task doesn't
			// necessarily require it.
			for (Path.Entry<WycsFile> source : wycsDir.get(wycsIncludes)) {
				// currently, I'm assuming everything is modified!
//				Path.Entry<WyilFile> binary = wyilDir.get(source.id(),
//						WyilFile.ContentType);
//
//				// first, check whether wyil file out-of-date with source file
//				if (binary == null
//						|| binary.lastModified() < source.lastModified()) {
					sources.add(source);
//				}
			}
		}
		return sources;
	}
	
	/**
	 * Flush all built files to disk.
	 */
	protected void flush() throws IOException {
		if(wycsDir != null) {
			// only flush wyilDir if it could contain wyil files which were
			// generated from whiley source files.
			wycsDir.flush();
		}
	}	
}
