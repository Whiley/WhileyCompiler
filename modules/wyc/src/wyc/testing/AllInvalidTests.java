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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wyc.WycMain;
import wyc.util.WycBuildTask;
import wycc.util.Pair;

/**
 * Run through all invalid test cases with verification enabled. Since every
 * test file is invalid, a successful test occurs when the compiler produces a
 * syntax error for the file. Note that an internal failure does not count as a
 * valid pass, and indicates the test exposed some kind of compiler bug.
 *
 * @author David J. Pearce
 *
 */
@RunWith(Parameterized.class)
public class AllInvalidTests {

	/**
	 * The directory containing the source files for each test case. Every test
	 * corresponds to a file in this directory.
	 */
	public final static String WHILEY_SRC_DIR = "../../tests/invalid".replace('/', File.separatorChar);

	/**
	 * Ignored tests and a reason why we ignore them.
	 */
	public final static Map<String, String> IGNORED = new HashMap<String, String>();

	static {
		IGNORED.put("Constant_Invalid_1", "unclassified");
		IGNORED.put("ConstrainedDictionary_Invalid_1", "unclassified");
		IGNORED.put("ConstrainedInt_Invalid_1", "unclassified");
		IGNORED.put("ConstrainedInt_Invalid_10", "unclassified");
		IGNORED.put("ConstrainedInt_Invalid_11", "unclassified");
		IGNORED.put("ConstrainedInt_Invalid_12", "unclassified");
		IGNORED.put("ConstrainedInt_Invalid_7", "timeout");
		IGNORED.put("ConstrainedInt_Invalid_8", "timeout");
		IGNORED.put("ConstrainedInt_Invalid_9", "Timeout");
		IGNORED.put("ConstrainedList_Invalid_2", "unclassified");
		IGNORED.put("ConstrainedList_Invalid_3", "unclassified");
		IGNORED.put("Contractive_Invalid_1", "#425");
		IGNORED.put("Contractive_Invalid_2", "unclassified");
		IGNORED.put("EndOfFile_Invalid_1", "unclassified");
		IGNORED.put("Export_Invalid_1", "unclassified");
		IGNORED.put("Function_Invalid_10", "unclassified");
		IGNORED.put("Function_Invalid_2", "unclassified");
		IGNORED.put("Function_Invalid_9", "unclassified");
		IGNORED.put("Intersection_Invalid_1", "unclassified");
		IGNORED.put("Intersection_Invalid_2", "unclassified");
		IGNORED.put("Lambda_Invalid_3", "unclassified");
		IGNORED.put("ListAssign_Invalid_2", "Infinite Loop?");
		IGNORED.put("ListAssign_Invalid_3", "Infinite Loop?");
		IGNORED.put("ListEquals_Invalid_1", "unclassified");
		IGNORED.put("ListLength_Invalid_2", "Timeout");
		IGNORED.put("ListUpdate_Invalid_1", "unclassified");
		IGNORED.put("MethodRef_Invalid_1", "unclassified");
		IGNORED.put("MethodRef_Invalid_2", "unclassified");
		IGNORED.put("MethodRef_Invalid_3", "unclassified");
		IGNORED.put("Native_Invalid_1", "unclassified");
		IGNORED.put("NegationType_Invalid_2", "unclassified");
		IGNORED.put("OpenRecord_Invalid_1", "wrong output");
		IGNORED.put("OpenRecord_Invalid_2", "unclassified");
		IGNORED.put("RecursiveType_Invalid_1", "unclassified");
		IGNORED.put("RecursiveType_Invalid_10", "unclassified");
		IGNORED.put("RecursiveType_Invalid_2", "unclassified");
		IGNORED.put("RecursiveType_Invalid_4", "unclassified");
		IGNORED.put("RecursiveType_Invalid_7", "unclassified");
		IGNORED.put("RecursiveType_Invalid_8", "unclassified");
		IGNORED.put("RecursiveType_Invalid_9", "unclassified");
		IGNORED.put("ReferenceOpenRecord_Invalid_1", "#585");
		IGNORED.put("Subtype_Invalid_6", "unclassified");
		IGNORED.put("Subtype_Invalid_8", "unclassified");
		IGNORED.put("Subtype_Invalid_9", "unclassified");
		IGNORED.put("Switch_Invalid_6", "unclassified");
		IGNORED.put("TupleAssign_Invalid_1", "unclassified");
		IGNORED.put("TupleAssign_Invalid_2", "unclassified");
		IGNORED.put("TupleAssign_Invalid_3", "unclassified");
		IGNORED.put("TupleDefine_Invalid_2", "unclassified");
		IGNORED.put("Tuple_Invalid_3", "unclassified");
		IGNORED.put("Tuple_Invalid_4", "unclassified");
		IGNORED.put("Tuple_Invalid_5", "unclassified");
		IGNORED.put("Tuple_Invalid_6", "unclassified");
		IGNORED.put("Tuple_Invalid_7", "unclassified");
		IGNORED.put("TypeEquals_Invalid_5", "unclassified");
		IGNORED.put("UnionType_Invalid_10", "#469");
		IGNORED.put("UnionType_Invalid_7", "unclassified");
		IGNORED.put("UnionType_Invalid_8", "unclassified");
		IGNORED.put("UnionType_Invalid_9", "#348");
		IGNORED.put("Void_Invalid_1", "unclassified");
		IGNORED.put("Void_Invalid_2", "unclassified");
		IGNORED.put("Void_Invalid_3", "unclassified");
		IGNORED.put("While_Invalid_12", "unclassified");
	}	

	/**
	 * The directory where compiler libraries are stored. This is necessary
	 * since it will contain the Whiley Runtime.
	 */
	public final static String WYC_LIB_DIR = "../../lib/".replace('/', File.separatorChar);

	/**
	 * The path to the Whiley RunTime (WyRT) library. This contains the Whiley
	 * standard library, which includes various helper functions, etc.
	 */
	private static String WYRT_PATH;

	static {

		// The purpose of this is to figure out what the proper name for the
		// wyrt file is. Since there can be multiple versions of this file,
		// we're not sure which one to pick.

		File file = new File(WYC_LIB_DIR);
		for(String f : file.list()) {
			if(f.startsWith("wyrt-v")) {
				WYRT_PATH = WYC_LIB_DIR + f;
			}
		}
	}

	// ======================================================================
	// Test Harness
	// ======================================================================

	/**
	 * Compile a syntactically invalid test case with verification enabled. The
	 * expectation is that compilation should fail with an error and, hence, the
	 * test fails if compilation does not.
	 *
	 * @param name
	 *            Name of the test to run. This must correspond to a whiley
	 *            source file in the <code>WHILEY_SRC_DIR</code> directory.
	 */
	protected void runTest(String name) {
		// this will need to turn on verification at some point.
		String filename = WHILEY_SRC_DIR + File.separatorChar + name + ".whiley";

		Pair<Integer,String> p = TestUtils.compile(
				"-wd", WHILEY_SRC_DIR,      // location of source directory
				"-wp", WYRT_PATH,           // add wyrt to whileypath
				"-verify",                  // enable verification
				filename);                      // name of test to compile

		int r = p.first();
		String output = p.second();

		if (r == WycMain.SUCCESS) {
			// Clearly, the test should not compile.
			fail("Test compiled when it shouldn't have!");
		} else if (r == WycMain.INTERNAL_FAILURE) {
			// This indicates some other kind of internal failure.
			fail("Test caused internal failure!");
		} else {
			// Now, let's check the expected output against the file which
			// contains the sample output for this test
			String sampleOutputFile = WHILEY_SRC_DIR + File.separatorChar + name
					+ ".sysout";

//			Following used when sample output changed.
//			try {
//				FileWriter fw = new FileWriter(sampleOutputFile);
//				fw.write(output);
//				fw.close();
//			} catch(Exception e) {}

	 		// Third, compare the output!
	 		TestUtils.compare(output,sampleOutputFile);
		}
	}

	// ======================================================================
	// Tests
	// ======================================================================

	// Parameter to test case is the name of the current test.
	// It will be passed to the constructor by JUnit.
	private final String testName;
	public AllInvalidTests(String testName) {
		this.testName = testName;
	}

	// Here we enumerate all available test cases.
	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return TestUtils.findTestNames(WHILEY_SRC_DIR);
	}

	// Skip ignored tests
	@Before
	public void beforeMethod() {
		String ignored = IGNORED.get(this.testName);
		Assume.assumeTrue("Test " + this.testName + " skipped: " + ignored, ignored == null);
	}

	@Test
	public void invalid() throws IOException {
		runTest(this.testName);
	}
}