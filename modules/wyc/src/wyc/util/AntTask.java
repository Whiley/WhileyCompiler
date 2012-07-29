// Copyright (c) 2012, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyc.util;

import java.io.*;
import java.util.*;

import wybs.lang.*;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.*;
import wyc.builder.Whiley2WyilBuilder;
import wyc.lang.WhileyFile;
import wyil.Pipeline;
import wyil.lang.WyilFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

/**
 * This class implements an baseline ant task for compiling whiley files via ant
 * and an appropriate build.xml file. The following illustrates how this task
 * can be used in a build.xml file:
 * 
 * <pre>
 * <taskdef name="wyc" classname="wyc.util.AntTask" classpath="lib/wyc.jar"/>
 * <wyc whileydir="stdlib" includes="whiley\/**\/*.whiley" excludes="whiley/io/**"/>
 * </pre>
 * 
 * <p>
 * The first line defines the new task, and requires <code>wyc.jar</code> (which
 * contains this class) to be on the classpath; The second invokes the task to
 * compile all files rooted in the <code>stdlib/</code> directory which are in
 * the <code>whiley/</code> package, excluding those in <code>whiley/io</code>.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> this class is intended to be overriden by the compiler backends
 * which compile Wyil bytecodes to different architectures (e.g. JVM, C,
 * JavaScript, etc).
 * </p>
 * 
 * 
 * @author David J. Pearce
 * 
 */
public class AntTask extends MatchingTask {
	
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
	 * The purpose of the source file filter is simply to ensure only whiley
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter whileyFileFilter = new FileFilter() {
		public boolean accept(File f) {
			String name = f.getName();
			return name.endsWith(".whiley") || f.isDirectory();
		}
	};

	/**
	 * The purpose of the binary file filter is simply to ensure only wyil
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter wyilFileFilter = new FileFilter() {
		public boolean accept(File f) {
			String name = f.getName();
			return name.endsWith(".wyil") || f.isDirectory();			
		}
	};
	
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
	 * Indicates whether or the compiler should produce verbose information
	 * during compilation. This is generally used for diagnosing bugs in the
	 * compiler.
	 */
	protected boolean verbose = false;
	
	public AntTask() {
		this.registry = new Registry();
	}
	
	public AntTask(Content.Registry registry) {
		this.registry = registry;
	}
	
	public void setWhileydir(File whileydir) throws IOException {
		this.whileyDir = new DirectoryRoot(whileydir, whileyFileFilter, registry);
		if(wyilDir == null) {
			this.wyilDir = new DirectoryRoot(whileydir, wyilFileFilter, registry);
		}
	}

    public void setWyildir (File wyildir) throws IOException {	
        this.wyilDir = new DirectoryRoot(wyildir, wyilFileFilter, registry);
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
    
    public void setWhileyPath (org.apache.tools.ant.types.Path path) throws IOException {
    	whileypath.clear(); // just to be sure
    	for(String file : path.list()) {
    		if(file.endsWith(".jar")) {
    			whileypath.add(new JarFileRoot(file,registry));
    		} else {
    			whileypath.add(new DirectoryRoot(file,registry));
    		}
    	}
    }
    
    public void setBootPath (org.apache.tools.ant.types.Path path) throws IOException {
    	bootpath.clear(); // just to be sure
    	for(String file : path.list()) {
    		if(file.endsWith(".jar")) {
    			bootpath.add(new JarFileRoot(file,registry));
    		} else {
    			bootpath.add(new DirectoryRoot(file,registry));
    		}
    	}
    }
    
    public void setVerbose(boolean b) {
    	verbose=b;
    }
    
    public void execute() throws BuildException {
        if (whileyDir == null) {
            throw new BuildException("whileydir must be specified");
        }
       
        if(!compile()) {
        	throw new BuildException("compilation errors");
        }        	        
    }
    	
    protected boolean compile() {
    	try {
    		// =====================================================================================
    		// Initialise Project
    		// =====================================================================================

    		SimpleProject project = initialiseProject();  		

    		// =====================================================================================
			// Initialise Build Rules
			// =====================================================================================
    		
    		addBuildRules(project);
    		
    		// =====================================================================================
			// Misc
			// =====================================================================================		

    		List<Path.Entry<?>> sources = getSourceFileDelta();
			
			log("Compiling " + sources.size() + " source file(s)");
			
    		// finally, compile away!
    		project.build(sources);
    		
    		flush(); // force all built components to disk
    		
    		return true;
    	} catch (InternalFailure e) {
    		e.outputSourceError(System.err);
    		if (verbose) {
    			e.printStackTrace(System.err);
    		}
    		return false;
    	} catch (SyntaxError e) {
    		e.outputSourceError(System.err);
    		if (verbose) {
    			e.printStackTrace(System.err);
    		}
    		return false;
    	} catch (Throwable e) {
    		System.err.println("internal failure: " + e.getMessage());
    		if (verbose) {
    			e.printStackTrace(System.err);
    		}
    		return false;
    	}    	
    }
    
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

		wyc.Main.initialiseBootPath(bootpath);
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
	
	/**
	 * Generate the list of source files which need to be recompiled. By
	 * default, this is done by comparing modification times of each whiley file
	 * against its corresponding wyil file. Wyil files which are out-of-date are
	 * scheduled to be recompiled.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected List<Path.Entry<?>> getSourceFileDelta() throws IOException {
		// Now, touch all source files which have modification date after
		// their corresponding binary.
		ArrayList<Path.Entry<?>> sources = new ArrayList<Path.Entry<?>>();
		
		if(whileyDir != null) {
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
