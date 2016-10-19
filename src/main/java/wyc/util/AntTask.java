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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

import wybs.util.StdProject;
import wyc.Activator;
import wyc.builder.CompileTask;
import wyc.commands.Compile;
import wyc.lang.WhileyFile;
import wycc.util.ArrayUtils;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.VirtualRoot;

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

	private Compile command;

	/**
	 * Construct a new instance of this command.
	 *
	 * @param registry
	 *            The content registry being used to match files to content
	 *            types.
	 * @throws IOException
	 */
	public AntTask() {
		Content.Registry registry = new Activator.Registry();
		this.command = new Compile(registry, Logger.NULL);

	}

	// =======================================================================
	// Configuration
	// =======================================================================

	public void setWhileypath(String dirs) throws IOException {
		command.setWhileypath(dirs);
	}

	public void setWhileydir(String dir) throws IOException {
		command.setWhileydir(dir);
	}

	public void setWyildir(String dir) throws IOException {
		command.setWyildir(dir);
	}

	public void setWyaldir(String dir) throws IOException {
		command.setWyaldir(dir);
	}

	public void setVerify() {
		command.setVerify();
	}

	public void setVerbose() {
		command.setVerbose();
	}

	// =======================================================================
	// Execute
	// =======================================================================

	@Override
	public void execute() throws BuildException {
		try {
			List<Path.Entry<WhileyFile>> files = command.getModifiedSourceFiles();
			Compile.Result r = command.execute(files);
			if (r == Compile.Result.SUCCESS) {
				log("Compiled " + files.size() + " source file(s)");
			}
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}
}
