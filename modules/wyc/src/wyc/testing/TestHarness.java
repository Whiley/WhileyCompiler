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

package wyc.testing;

import static org.junit.Assert.fail;

import java.io.*;

import wyc.WycMain;
import wyc.util.WycBuildTask;

public class TestHarness {
	private static final String WYJC_PATH="../../../modules/wyjc/src/";
	private static final String WYIL_PATH="../../../modules/wyil/src/";
	private static String WYRT_PATH;

	static {

		// The purpose of this is to figure out what the proper name for the
		// wyrt file is.

		File file = new File("../../lib/");
		for(String f : file.list()) {
			if(f.startsWith("wyrt-v")) {
				WYRT_PATH="../../lib/" + f;
			}
		}
	}
	private String sourcepath;    // path to source files	
	private String outputPath; // path to output files
	private String outputExtension; // the extension of output files
	
	/**
	 * Construct a test harness object.
	 * 
	 * @param srcPath
	 *            The path to the source files to be tested
	 * @param outputPath
	 *            The path to the sample output files to compare against.
	 * @param outputExtension
	 *            The extension of output files
	 * @param verification
	 *            if true, the verifier is used.
	 */
	public TestHarness(String srcPath, String outputPath,
			String outputExtension) {
		this.sourcepath = srcPath.replace('/', File.separatorChar);
		this.outputPath = outputPath.replace('/', File.separatorChar);
		this.outputExtension = outputExtension;		
	}
	
	/**
	 * Compile a syntactically invalid test case. The expectation is that
	 * compilation should fail with an error and, hence, the test fails if
	 * compilation does not.
	 * 
	 * @param name
	 *            Name of the test to run. This must correspond to an executable
	 *            Java file in the srcPath of the same name.
	 */
	protected void contextFailTest(String name) {
		name = sourcepath + File.separatorChar + name + ".whiley";

		int r = compile("-wd", sourcepath, "-wp", WYRT_PATH, name);

		if (r == WycMain.SUCCESS) {
			fail("Test compiled when it shouldn't have!");
		} else if (r == WycMain.INTERNAL_FAILURE) {
			fail("Test caused internal failure!");
		}
	}
	
	/**
	 * Compile a syntactically invalid test case with verification enabled. The
	 * expectation is that compilation should fail with an error and, hence, the
	 * test fails if compilation does not. This differs from the contextFailTest
	 * in that the test cases are expected to fail only in the verifier, and not
	 * the ordinary course of things.
	 * 
	 * @param name
	 *            Name of the test to run. This must correspond to an executable
	 *            Java file in the srcPath of the same name.
	 */
	protected void verifyFailTest(String name) {
		// this will need to turn on verification at some point.
		name = sourcepath + File.separatorChar + name + ".whiley";

		int r = compile("-wd", sourcepath, "-wp", WYRT_PATH, "-X",
				"verification:enable=true", name);

		if (r == WycMain.SUCCESS) {
			fail("Test compiled when it shouldn't have!");
		} else if (r == WycMain.INTERNAL_FAILURE) {
			fail("Test caused internal failure!");
		}
	}
	
	private static int compile(String... args) {
		return new WycMain(new WycBuildTask(), WycMain.DEFAULT_OPTIONS).run(args);
	}	
}
