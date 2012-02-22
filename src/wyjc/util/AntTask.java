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

import wyc.Compiler;
import wyc.Pipeline;
import wyc.lang.WhileyProject;
import wyc.util.path.BinaryDirectoryRoot;
import wyc.util.path.JarFileRoot;
import wyc.util.path.Path;
import wyc.util.path.SourceDirectoryRoot;
import wyil.ModuleLoader;
import wyil.Transform;
import wyil.util.SyntaxError;
import wyil.util.SyntaxError.InternalFailure;
import wyjc.Main;
import wyjc.io.ClassFileLoader;
import wyjc.transforms.ClassWriter;

import org.apache.tools.ant.*;
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
	ArrayList<Path.Root> bootpath = new ArrayList<Path.Root>();
	ArrayList<Path.Root> whileypath = new ArrayList<Path.Root>();
	private File srcdir;
	private File destdir;
	private boolean verbose = false;
		
    public void setSrcdir (File srcdir) {
        this.srcdir = srcdir;
    }

    public void setDestdir (File destdir) {
        this.destdir = destdir;
    }
    
    public void setWhileyPath (org.apache.tools.ant.types.Path path) throws IOException {
    	whileypath.clear(); // just to be sure
    	for(String file : path.list()) {
    		if(file.endsWith(".jar")) {
    			whileypath.add(new JarFileRoot(file));
    		} else {
    			whileypath.add(new BinaryDirectoryRoot(file));
    		}
    	}
    }
    
    public void setBootPath (org.apache.tools.ant.types.Path path) throws IOException {
    	bootpath.clear(); // just to be sure
    	for(String file : path.list()) {
    		if(file.endsWith(".jar")) {
    			bootpath.add(new JarFileRoot(file));
    		} else {
    			bootpath.add(new BinaryDirectoryRoot(file));
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
        log("dir = " + srcdir, Project.MSG_DEBUG);

       
        ArrayList<File> srcfiles = findSourceFiles();
        if(!srcfiles.isEmpty()) {
        	log("Compiling " + srcfiles.size() + " source file(s)"); 	
	                	        	
        	if(!compile(srcfiles)) {
        		throw new BuildException("compilation errors");
        	}        	
        }
        
        srcdir = null; // release file
    }
    
	protected ArrayList<File> findSourceFiles() {
		DirectoryScanner ds = getDirectoryScanner(srcdir);
		ArrayList<File> srcfiles = new ArrayList<File>();
		
		for (String f : ds.getIncludedFiles()) {			
			File srcfile = new File(srcdir.getPath() + File.separatorChar + f);
			File bindir;
			if (destdir != null) {
				bindir = destdir;
			} else {
				bindir = srcdir;
			}
			File binfile = new File(bindir.getPath() + File.separatorChar
					+ f.replace(".whiley", ".class"));
			if (srcfile.lastModified() > binfile.lastModified()) {
				srcfiles.add(srcfile);
			}
		}

		return srcfiles;
	}
    
    protected boolean compile(ArrayList<File> srcfiles) {
    	try {
    		// first, initialise sourcepath and whileypath
    		List<Path.Root> sourcepath = initialiseSourcePath();
    		List<Path.Root> whileypath = initialiseWhileyPath();

    		// second, construct the module loader    		
    		WhileyProject project = new WhileyProject(sourcepath,whileypath);
    		project.setModuleReader("class",  new ClassFileLoader());
    		
    		// third, initialise the pipeline
    		ArrayList<Pipeline.Template> templates = new ArrayList<Pipeline.Template>(Pipeline.defaultPipeline);
    		templates.add(new Pipeline.Template(ClassWriter.class,Collections.EMPTY_MAP));
    		Pipeline pipeline = new Pipeline(templates, project);
    		if(destdir != null) {
    			pipeline.setOption(ClassWriter.class, "outputDirectory",
					destdir.getPath());
    		}
    		List<Transform> stages = pipeline.instantiate();
    		
    		// fourth initialise the compiler
    		Compiler compiler = new Compiler(project,stages);		
    		project.setLogger(compiler);		

    		if(verbose) {			
    			compiler.setLogOut(System.err);
    		}

    		// finally, compile away!
    		compiler.compile(srcfiles);
    		
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
    
    protected List<Path.Root> initialiseSourcePath() throws IOException {
    	ArrayList<Path.Root> sourcepath = new ArrayList<Path.Root>();
    	BinaryDirectoryRoot bindir = null;
    	if(destdir != null) {
    		bindir = new BinaryDirectoryRoot(destdir);
    	}
    	sourcepath.add(new SourceDirectoryRoot(srcdir,bindir));    	
    	return sourcepath;
    }
    
    protected List<Path.Root> initialiseWhileyPath() {    	
    	wyjc.Main.initialiseBootPath(bootpath);
    	whileypath.addAll(bootpath);
    	return whileypath;
    }
}
