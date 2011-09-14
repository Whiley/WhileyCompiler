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

import wyjc.Main;

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
	private File srcdir;
	private boolean noVerificationChecks = false;
	private boolean verbose = false;
	
	public void setNvc(boolean b) {		
		noVerificationChecks=b;
	}
		
    public void setSrcdir (File srcdir) {
        this.srcdir = srcdir;
    }

    public void setVerbose(boolean b) {
    	verbose=b;
    }
    
    public void execute() throws BuildException {
        if (srcdir == null) {
            throw new BuildException("srcdir must be specified");
        }
        log("dir = " + srcdir, Project.MSG_DEBUG);

        DirectoryScanner ds = getDirectoryScanner(srcdir);
        String[] files = ds.getIncludedFiles();        
        
        // Now, construct the parameters list for whiley.Main.run
        int nparams = files.length;
        if(noVerificationChecks) { nparams ++; }        
        ArrayList<String> params = new ArrayList<String>();        
        if(noVerificationChecks) { params.add("-Nvc"); }        
        if(verbose) { params.add("-verbose"); }
        int nfiles = 0;
        for(String f : files) {
        	String fname = srcdir.getPath() + File.separatorChar + f;
        	File srcfile = new File(fname);
        	File classfile = new File(fname.replace(".whiley",".class"));
        	if(srcfile.lastModified() > classfile.lastModified()) {
        		params.add(fname);
        		nfiles++;
        	}
        }
        
        if(nfiles > 0) {
        	log("Compiling " + nfiles + " source file(s)");
        	// Finally, run the whiley compiler        	
			int exitCode = new Main().run(params.toArray(new String[params.size()]));        

        	if(exitCode != 0) {
        		throw new BuildException("compilation errors");
        	}
        }
        srcdir = null; // release file
    }
}
