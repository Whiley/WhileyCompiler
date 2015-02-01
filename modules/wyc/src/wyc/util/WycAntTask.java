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
import wybs.util.*;
import wyc.builder.WhileyBuilder;
import wyc.lang.WhileyFile;
import wycc.lang.SyntaxError.InternalFailure;
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
public class WycAntTask extends MatchingTask {

	protected final WycBuildTask builder;

	public WycAntTask() {
		this.builder = new WycBuildTask();
	}

	public WycAntTask(WycBuildTask builder) {
		this.builder = builder;
	}

	public void setWhileydir(File dir) throws IOException {
		builder.setWhileyDir(dir);
	}

    public void setWyildir (File dir) throws IOException {
    	builder.setWyilDir(dir);
    }

    public void setWyaldir (File dir) throws IOException {
    	builder.setWyalDir(dir);
    }
    
    public void setWycsdir (File dir) throws IOException {
    	builder.setWycsDir(dir);
    }
    
    public void setIncludes(String includes) {
    	builder.setIncludes(includes);
    }

    public void setExcludes(String excludes) {
    	builder.setExcludes(excludes);
    }

    public void setWhileyPath (org.apache.tools.ant.types.Path path) throws IOException {
    	ArrayList<File> whileypath = new ArrayList<File>();
    	for(String file : path.list()) {
    		whileypath.add(new File(file));
    	}
    	builder.setWhileyPath(whileypath);
    }

    public void setBootPath (org.apache.tools.ant.types.Path path) throws IOException {
    	ArrayList<File> bootpath = new ArrayList<File>();
    	for(String file : path.list()) {
    		bootpath.add(new File(file));
    	}
    	builder.setBootPath(bootpath);
    }

    public void setVerbose(boolean b) {
    	builder.setVerbose(b);
    }

    public void setVerification(boolean b) {
    	builder.setVerification(b);
    }

    public void setVerificationConditions(boolean b) {
    	builder.setVerificationConditions(b);
    }
    
    public void execute() throws BuildException {
    	try {
    		int count = builder.buildAll();
    		if(builder.getVerification()) {
    			log("Compiled and Verified " + count + " source file(s)");
    		} else {
    			log("Compiled " + count + " source file(s)");
    		}
    	} catch(Exception e) {
    		throw new BuildException(e);
    	}
    }
}
