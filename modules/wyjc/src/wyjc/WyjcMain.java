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

package wyjc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import wyc.WycMain;
import wyc.util.WycBuildTask;
import wycc.util.OptArg;
import wyjc.util.WyjcBuildTask;

/**
 * The main class provides all of the necessary plumbing to process command-line
 * options, construct an appropriate pipeline and then instantiate the Whiley
 * Compiler to generate class files.
 *
 * @author David J. Pearce
 *
 */
public class WyjcMain extends WycMain {

	public static final OptArg[] EXTRA_OPTIONS = {
		new OptArg("classdir", "cd", OptArg.FILEDIR, "Specify where to place generated class files",
			new File("."))
	};

	public static OptArg[] DEFAULT_OPTIONS;

	static {
		// first append options
		OptArg[] options = new OptArg[WycMain.DEFAULT_OPTIONS.length
				+ EXTRA_OPTIONS.length];
		System.arraycopy(WycMain.DEFAULT_OPTIONS, 0, options, 0,
				WycMain.DEFAULT_OPTIONS.length);
		System.arraycopy(EXTRA_OPTIONS, 0, options,
				WycMain.DEFAULT_OPTIONS.length, EXTRA_OPTIONS.length);
		WyjcMain.DEFAULT_OPTIONS = options;
	}

	public WyjcMain(WyjcBuildTask builder, OptArg[] options) {
		super(builder, options);
	}

	@Override
	public void configure(Map<String, Object> values) throws IOException {
		super.configure(values);

		File classDir = (File) values.get("classdir");
		if (classDir != null) {
			((WyjcBuildTask) builder).setClassDir(classDir);
		}
	}

	public static void main(String[] args) {

		// now, run wyjc build task
		System.exit(new WyjcMain(new WyjcBuildTask(), DEFAULT_OPTIONS).run(args));
	}
}
