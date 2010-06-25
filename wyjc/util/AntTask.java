// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

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
 * @author djp
 * 
 */
public class AntTask extends MatchingTask {
	private File srcdir;
	private boolean noVerificationChecks = false;
	private boolean noRuntimeChecks = false;

	public void setNvc(boolean b) {		
		noVerificationChecks=b;
	}
	
	public void setNrc(boolean b) {		
		noRuntimeChecks=b;
	}
	
    public void setSrcdir (File srcdir) {
        this.srcdir = srcdir;
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
        if(noRuntimeChecks) { nparams ++; }
        ArrayList<String> params = new ArrayList<String>();        
        if(noVerificationChecks) { params.add("-nvc"); }
        if(noRuntimeChecks) { params.add("-nrc"); }
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
			int exitCode = Main.run(params.toArray(new String[params.size()]));        

        	if(exitCode != 0) {
        		throw new BuildException("compilation errors");
        	}
        }
        srcdir = null; // release file
    }
}
