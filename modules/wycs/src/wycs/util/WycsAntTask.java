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

package wycs.util;

import java.io.*;
import java.util.*;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

/**
 * This class implements an baseline ant task for compiling wyal files via ant
 * and an appropriate build.xml file. The following illustrates how this task
 * can be used in a build.xml file:
 *
 * <pre>
 *   <taskdef name=\"wycs\" classname=\"wycs.util.WycsAntTask\" classpath=\"src/\"/>
 *   <wycs wyaldir=\"stdlib/\" includes=\"*\/**.wycs\"/>
 * </pre>
 *
 * @author David J. Pearce
 *
 */
public class WycsAntTask extends MatchingTask {

	protected final WycsBuildTask builder;

	public WycsAntTask() {
		this.builder = new WycsBuildTask();
	}

	public WycsAntTask(WycsBuildTask builder) {
		this.builder = builder;
	}

	public void setWyaldir(File dir) throws IOException {
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

    public void setWycsPath (org.apache.tools.ant.types.Path path) throws IOException {
    	ArrayList<File> whileypath = new ArrayList<File>();
    	for(String file : path.list()) {
    		whileypath.add(new File(file));
    	}
    	builder.setWycsPath(whileypath);
    }

    public void setBootPath (org.apache.tools.ant.types.Path path) throws IOException {
    	ArrayList<File> bootpath = new ArrayList<File>();
    	for(String file : path.list()) {
    		bootpath.add(new File(file));
    	}
    	builder.setWycsPath(bootpath);
    }

    public void setVerbose(boolean b) {
    	builder.setVerbose(b);
    }

    public void execute() throws BuildException {
    	try {
    		int count = builder.buildAll();
    		log("Compiled " + count + " source file(s)");
    	} catch(Exception e) {
    		throw new BuildException(e);
    	}
    }
}
