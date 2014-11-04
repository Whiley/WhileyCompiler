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

package wycs.testing;

import static org.junit.Assert.fail;

import java.io.*;

import wycs.WycsMain;
import wycs.util.WycsBuildTask;

public class TestHarness {
	private String sourcepath;    // path to source files
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

	/**
	 * Construct a test harness object.
	 *
	 * @param srcPath
	 *            The path to the source files to be tested
	 */
	public TestHarness(String srcPath) {
		this.sourcepath = srcPath.replace('/', File.separatorChar);
	}

	protected void verifyPassTest(String name) {
		// this will need to turn on verification at some point.
		name = sourcepath + File.separatorChar + name + ".wyal";

		try {
			if (compile("-bp", WYRT_PATH,
						"-wyaldir", sourcepath,
						"-wycsdir", sourcepath,
						name) != WycsMain.SUCCESS) {
				fail("Test failed to verify!");
			}
		} catch(IOException e) {
			fail("Test threw IOException");
		}
	}

	protected void verifyFailTest(String name) {
		// this will need to turn on verification at some point.
		name = sourcepath + File.separatorChar + name + ".wyal";

		try {
			if (compile("-bp", WYRT_PATH, "-wyaldir", sourcepath, "-wycsdir",
					sourcepath, name) != WycsMain.SYNTAX_ERROR) {
				fail("Test verified when it shouldn't have!");
			}
		} catch(IOException e) {
			fail("Test threw IOException");
		}
	}


	private static int compile(String... args) throws IOException {
		return new WycsMain(new WycsBuildTask(), WycsMain.DEFAULT_OPTIONS)
				.run(args);
	}
}
