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

package wyopcl;

import java.io.*;
import wyc.util.WycAntTask;

/**
 * An AntTask for compiling JVM Class files from Whiley source files or Wyil
 * binary files. The following illustrates how this task can be used in a
 * <code>build.xml</code> file:
 * 
 * <pre>
 *  <taskdef name="wyjc" classname="wyjc.util.AntTask" classpath="src/"/>
 * <wyjc verbose="true" wyildir="stdlib" classdir="src\/" includes="whiley\/**\/*.wyil"/>
 * </pre>
 * 
 * <p>
 * The first line defines the new task, and requires the <code>src/</code>
 * directory (which contains this class) to be on the classpath; The second
 * invokes the task to compile all wyil files rooted in the <code>stdlib/</code>
 * directory which are in the <code>whiley/</code> package.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class WyopclAntTask extends WycAntTask {
	private final WyopclBuildTask myBuilder;	
	
	public WyopclAntTask() {
		super(new WyopclBuildTask());
		this.myBuilder = (WyopclBuildTask) builder;
	}		
}
