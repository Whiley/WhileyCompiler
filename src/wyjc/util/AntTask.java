// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
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

package wyjc.util;

import java.io.*;
import java.util.*;

import wybs.lang.*;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.*;
import wyc.builder.Whiley2WyilBuilder;
import wyc.lang.WhileyFile;
import wyil.Pipeline;
import wyil.lang.WyilFile;
import wyjc.Wyil2JavaBuilder;
import wyjvm.lang.ClassFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

/**
 * This class implements an ant task for compiling whiley files via ant and an
 * appropriate build.xml file. The following illustrates how this task can be
 * used in a build.xml file:
 * 
 * <pre>
 * <taskdef name="wyjc" classname="wyjc.util.AntTask" classpath="lib/whiley.jar"/>
 * <wyjc srcdir="stdlib" includes="whiley\/**\/*.whiley" excludes="whiley/io/**" nvc="true"/>
 * </pre>
 * 
 * Here, the first line defines the new task, and requires whiley.jar (which
 * contains this class) to be on the classpath; The second invokes the task to
 * compile all files rooted in the stdlib/ directory which in the whiley/
 * package, excluding those in whiley/io. The nvc="true" switch indicates no
 * verification checking should be performed; a similar switch, nrc="true", can
 * be used to turn off generation of runtime checks as well.
 * 
 * @author David J. Pearce
 * 
 */
public class AntTask extends MatchingTask {
	
	/**
	 * The master project content type registry.
	 */
	public static final Content.Registry registry = new Content.Registry() {
	
		public void associate(Path.Entry e) {
			String suffix = e.suffix();
			
			if(suffix.equals("whiley")) {
				e.associate(WhileyFile.ContentType, null);
			} else if(suffix.equals("wyil")) {
				e.associate(WyilFile.ContentType, null);				
			} else if(suffix.equals("class")) {				
				e.associate(ClassFile.ContentType, null);				
			} 
		}
		
		public String suffix(Content.Type<?> t) {
			if(t == WhileyFile.ContentType) {
				return "whiley";
			} else if(t == WyilFile.ContentType) {
				return "wyil";
			} else if(t == ClassFile.ContentType) {
				return "class";
			} else {
				return "dat";
			}
		}
	};
	
	/**
	 * The purpose of the source file filter is simply to ensure only source
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter sourceFileFilter = new FileFilter() {
		public boolean accept(File f) {
			String name = f.getName();
			return name.endsWith(".whiley") || f.isDirectory();
		}
	};

	/**
	 * The purpose of the binary file filter is simply to ensure only binary
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter binaryFileFilter = new FileFilter() {
		public boolean accept(File f) {
			String name = f.getName();
			return name.endsWith(".wyil") || name.endsWith(".class") || f.isDirectory();			
		}
	};
	
	ArrayList<Path.Root> bootpath = new ArrayList<Path.Root>();
	ArrayList<Path.Root> whileypath = new ArrayList<Path.Root>();
	private File srcdir;
	private File destdir;
	private Content.Filter<WhileyFile> whileyIncludes = Content.filter("**", WhileyFile.ContentType);
	private Content.Filter<WhileyFile> whileyExcludes = null;
	private Content.Filter<WyilFile> wyilIncludes = Content.filter("**", WyilFile.ContentType);
	private Content.Filter<WyilFile> wyilExcludes = null;
	private boolean verbose = false;
		
    public void setSrcdir (File srcdir) {
        this.srcdir = srcdir;
    }

    public void setDestdir (File destdir) {
        this.destdir = destdir;
    }
    
    public void setIncludes(String includes) {
    	String[] split = includes.split(",");
    	Content.Filter<WhileyFile> whileyFilter = null;
    	Content.Filter<WyilFile> wyilFilter = null;
    	
    	for(String s : split) {
    		if(s.endsWith(".whiley")) {
    			String name = s.substring(0,s.length()-7);
    			Content.Filter<WhileyFile> f1 = Content.filter(name,WhileyFile.ContentType);
    			Content.Filter<WyilFile> f2 = Content.filter(name,WyilFile.ContentType);
    			whileyFilter = whileyFilter == null ? f1 : Content.or(f1, whileyFilter); 
    			wyilFilter = wyilFilter == null ? f2 : Content.or(f2, wyilFilter);
    		} else if(s.endsWith(".wyil")) {    			
    			String name = s.substring(0,s.length()-5);
    			Content.Filter<WyilFile> f2 = Content.filter(name,WyilFile.ContentType);    			
    			wyilFilter = wyilFilter == null ? f2 : Content.or(f2, wyilFilter);
    		}
    	}
    	
		// FIXME: it's slightly annoying that the filter could be null here. I
		// think this indicates a limitation with the content type system I've
		// designed.
    	
    	this.whileyIncludes = whileyFilter;
    	this.wyilIncludes = wyilFilter;
    }
    
    public void setExcludes(String excludes) {
    	String[] split = excludes.split(",");
    	Content.Filter<WhileyFile> whileyFilter = null;
    	Content.Filter<WyilFile> wyilFilter = null;
    	for(String s : split) {
    		if(s.endsWith(".whiley")) {
    			String name = s.substring(0,s.length()-7);
    			Content.Filter<WhileyFile> f1 = Content.filter(name,WhileyFile.ContentType);
    			Content.Filter<WyilFile> f2 = Content.filter(name,WyilFile.ContentType);
    			whileyFilter = whileyFilter == null ? f1 : Content.or(f1, whileyFilter); 
    			wyilFilter = wyilFilter == null ? f2 : Content.or(f2, wyilFilter);
    		} else if(s.endsWith(".wyil")) {    			
    			String name = s.substring(0,s.length()-5);    			
    			Content.Filter<WyilFile> f2 = Content.filter(name,WyilFile.ContentType);    			
    			wyilFilter = wyilFilter == null ? f2 : Content.or(f2, wyilFilter);
    		}
    	}
    	
		// FIXME: it's slightly annoying that the filter could be null here. I
		// think this indicates a limitation with the content type system I've
		// designed.
    	
    	this.whileyExcludes = whileyFilter;
    	this.wyilExcludes = wyilFilter;
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
        if (srcdir == null) {
            throw new BuildException("srcdir must be specified");
        }
        log("dir = " + srcdir, org.apache.tools.ant.Project.MSG_DEBUG);

       
        if(!compile()) {
        	throw new BuildException("compilation errors");
        }        	
                
        srcdir = null; // release file
    }
    	
    protected boolean compile() {
    	try {
    		// =====================================================================================
    		// Initialise Roots
    		// =====================================================================================

    		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();
        	DirectoryRoot source = new DirectoryRoot(srcdir,sourceFileFilter,registry); 
    		roots.add(source);    
        		        	
        	DirectoryRoot target;
        	if(destdir != null) {
        		target = new DirectoryRoot(destdir,binaryFileFilter,registry);        	
        		roots.add(target);
        	} else {
        		target = new DirectoryRoot(srcdir,binaryFileFilter,registry);        	
        		roots.add(target);
        	}
        	    	       	
        	wyjc.Main.initialiseBootPath(bootpath);      	
        	roots.addAll(whileypath);
        	roots.addAll(bootpath);
    		        	
    		// second, construct the module loader    		
    		SimpleProject project = new SimpleProject(roots);    		
    		
    		// third, initialise the pipeline    		    	
    		Pipeline pipeline = new Pipeline(Pipeline.defaultPipeline);
 
			// =====================================================================================
			// Whiley to Wyil Build Rule
			// =====================================================================================			
    		Whiley2WyilBuilder builder = new Whiley2WyilBuilder(project,pipeline);
    		
    		if(verbose) {			
    			builder.setLogger(new Logger.Default(System.err));
    		}
    		
			StandardBuildRule rule = new StandardBuildRule(builder);			
			rule.add(source, whileyIncludes, whileyExcludes, target,
					WhileyFile.ContentType, WyilFile.ContentType);			
			project.add(rule);
    		
			// =====================================================================================
			// Wyil-to-Java Build Rule
			// =====================================================================================
			Wyil2JavaBuilder jbuilder = new Wyil2JavaBuilder();
    		
    		if(verbose) {			
    			jbuilder.setLogger(new Logger.Default(System.err));
    		}
    		
			rule = new StandardBuildRule(jbuilder);			
			rule.add(target, wyilIncludes, wyilExcludes, target,
					WyilFile.ContentType, ClassFile.ContentType);			
			project.add(rule);
    		
			// =====================================================================================
			// Misc
			// =====================================================================================		

			// Now, touch all source files which have modification date after
			// their corresponding binary.	
			ArrayList<Path.Entry<?>> sources = new ArrayList<Path.Entry<?>>();
			for (Path.Entry<WhileyFile> sourceFile : source.get(whileyIncludes)) {
				Path.Entry<WyilFile> wyilBinary;

				wyilBinary = target.get(sourceFile.id(), WyilFile.ContentType);

				// first, check whether wyil file out-of-date with source file
				if (wyilBinary == null || wyilBinary.lastModified() < sourceFile.lastModified()) {
					sources.add(sourceFile);
				} else {
					// second, check whether class file out of date with wyil file.
					Path.Entry<ClassFile> classBinary = target.get(
							sourceFile.id(), ClassFile.ContentType);
					if (classBinary == null || classBinary.lastModified() < wyilBinary.lastModified()) {
						sources.add(sourceFile);
					}
				}
			}
			
			log("Compiling " + sources.size() + " source file(s)");
			
    		// finally, compile away!
    		project.build(sources);
    		project.flush(); // force all built components to disk
    		
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
}
