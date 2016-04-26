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
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wyc.WycMain;
import wycc.util.Pair;

/**
 * Run through all valid test cases with verification enabled. Since every test
 * file is valid, a successful test occurs when the compiler succeeds and, when
 * executed, the compiled file produces the expected output. Note that an
 * internal failure does not count as a valid pass, and indicates the test
 * exposed some kind of compiler bug.
 *
 * @author David J. Pearce
 *
 */
@RunWith(Parameterized.class)
public class AllValidVerificationTests {

	/**
	 * The directory containing the source files for each test case. Every test
	 * corresponds to a file in this directory.
	 */
	public final static String WHILEY_SRC_DIR = "../../tests/valid".replace('/', File.separatorChar);

	/**
	 * Ignored tests and a reason why we ignore them.
	 */
	public final static Map<String, String> IGNORED = new HashMap<String, String>();

	static {
		IGNORED.put("BoolList_Valid_3", "#229");
		IGNORED.put("BoolRecord_Valid_1", "#522");
		IGNORED.put("Cast_Valid_5", "#468");
		IGNORED.put("Coercion_Valid_8", "#298");
		IGNORED.put("Complex_Valid_1", "Issue ???");
		IGNORED.put("Complex_Valid_2", "#298");
		IGNORED.put("Complex_Valid_3", "#339");
		IGNORED.put("Complex_Valid_4", "#298");
		IGNORED.put("Complex_Valid_5", "Issue ???");
		IGNORED.put("Complex_Valid_6", "#468");
		IGNORED.put("Complex_Valid_7", "timeout");
		IGNORED.put("Complex_Valid_8", "???");
		IGNORED.put("ConstrainedInt_Valid_22", "#337");
		IGNORED.put("ConstrainedInt_Valid_8", "Issue ???");
		IGNORED.put("ConstrainedIntersection_Valid_1", "unknown");
		IGNORED.put("ConstrainedList_Valid_1", "#468");
		IGNORED.put("ConstrainedList_Valid_16", "#468");
		IGNORED.put("ConstrainedList_Valid_17", "#508");
		IGNORED.put("ConstrainedList_Valid_18", "Issue ???");
		IGNORED.put("ConstrainedList_Valid_19", "#468");
		IGNORED.put("ConstrainedList_Valid_2", "Issue ???");
		IGNORED.put("ConstrainedList_Valid_20", "#468");
		IGNORED.put("ConstrainedList_Valid_21", "#308");
		IGNORED.put("ConstrainedList_Valid_22", "Issue ???");
		IGNORED.put("ConstrainedList_Valid_23", "#229");
		IGNORED.put("ConstrainedList_Valid_26", "#290");
		IGNORED.put("ConstrainedList_Valid_28", "TOO LONG");
		IGNORED.put("ConstrainedList_Valid_4", "#468");
		IGNORED.put("ConstrainedList_Valid_6", "Issue ???");
		IGNORED.put("ConstrainedList_Valid_7", "#290");
		IGNORED.put("ConstrainedList_Valid_8", "#233");
		IGNORED.put("ConstrainedNegation_Valid_1", "#342");
		IGNORED.put("ConstrainedNegation_Valid_2", "#342");
		IGNORED.put("ConstrainedRecord_Valid_8", "Issue ???");
		IGNORED.put("ConstrainedRecord_Valid_9", "Issue ???");
		IGNORED.put("ConstrainedRecord_Valid_10", "#514");
		IGNORED.put("ConstrainedReference_Valid_1", "#468");
		IGNORED.put("Contractive_Valid_1", "Unknown Problem");
		IGNORED.put("DoWhile_Valid_4", "#298");
		IGNORED.put("Ensures_Valid_3", "Issue ???");
		IGNORED.put("Ensures_Valid_6", "timeout");
		IGNORED.put("FunctionRef_Valid_2", "Issue ???");
		IGNORED.put("FunctionRef_Valid_4", "Issue ???");
		IGNORED.put("FunctionRef_Valid_10", "#298");
		IGNORED.put("FunctionRef_Valid_11", "#298");
		IGNORED.put("FunctionRef_Valid_13", "#555");
		IGNORED.put("IfElse_Valid_4", "#298");
		IGNORED.put("Import_Valid_4", "#492");
		IGNORED.put("Import_Valid_5", "#492");
		IGNORED.put("IntDiv_Valid_3", "Unknown Issue");
		IGNORED.put("Intersection_Valid_1", "Known Issue");
		IGNORED.put("Intersection_Valid_2", "Known Issue");
		IGNORED.put("Lambda_Valid_3", "#344");
		IGNORED.put("Lambda_Valid_4", "#344");
		IGNORED.put("Lambda_Valid_7", "#344");
		IGNORED.put("Lifetime_Lambda_Valid_4", "#298");
		IGNORED.put("ListAccess_Valid_6", "Known Issue");
		IGNORED.put("ListAssign_Valid_1", "#233");
		IGNORED.put("ListAssign_Valid_6", "#233");
		IGNORED.put("ListGenerator_Valid_12", "Unknown");
		IGNORED.put("Method_Valid_1", "Issue ???");
		IGNORED.put("NegationType_Valid_3", "Known Issue");
		IGNORED.put("OpenRecord_Valid_11", "#585");
		IGNORED.put("Process_Valid_1", "#291");
		IGNORED.put("Process_Valid_10", "#291");
		IGNORED.put("Process_Valid_9", "#231");
		IGNORED.put("RecordAssign_Valid_2", "#524");
		IGNORED.put("RecordCoercion_Valid_1", "#564");
		IGNORED.put("RecordSubtype_Valid_1", "Known Issue");
		IGNORED.put("RecordSubtype_Valid_2", "Known Issue");
		IGNORED.put("RecursiveType_Valid_11", "#298");
		IGNORED.put("RecursiveType_Valid_12", "#298");
		IGNORED.put("RecursiveType_Valid_14", "#298");
		IGNORED.put("RecursiveType_Valid_16", "#298");
		IGNORED.put("RecursiveType_Valid_17", "#298");
		IGNORED.put("RecursiveType_Valid_18", "#298");
		IGNORED.put("RecursiveType_Valid_19", "#298");
		IGNORED.put("RecursiveType_Valid_2", "#298");
		IGNORED.put("RecursiveType_Valid_20", "#298");
		IGNORED.put("RecursiveType_Valid_21", "#298");
		IGNORED.put("RecursiveType_Valid_22", "#298");
		IGNORED.put("RecursiveType_Valid_23", "#298");
		IGNORED.put("RecursiveType_Valid_24", "#298");
		IGNORED.put("RecursiveType_Valid_25", "#298");
		IGNORED.put("RecursiveType_Valid_26", "#298");
		IGNORED.put("RecursiveType_Valid_27", "#298");
		IGNORED.put("RecursiveType_Valid_29", "#396");
		IGNORED.put("RecursiveType_Valid_3", "#298");
		IGNORED.put("RecursiveType_Valid_30", "#298");
		IGNORED.put("RecursiveType_Valid_31", "#298");
		IGNORED.put("RecursiveType_Valid_4", "#298");
		IGNORED.put("RecursiveType_Valid_5", "#18");
		IGNORED.put("RecursiveType_Valid_7", "#298");
		IGNORED.put("Reference_Valid_2", "Unclassified");
		IGNORED.put("Reference_Valid_3", "Unclassified");
		IGNORED.put("Reference_Valid_6", "#553");
		IGNORED.put("Return_Valid_1", "#565");
		IGNORED.put("Subtype_Valid_1", "#522");
		IGNORED.put("Subtype_Valid_10", "#522");
		IGNORED.put("Subtype_Valid_11", "#522");
		IGNORED.put("Subtype_Valid_12", "#522");
		IGNORED.put("Subtype_Valid_13", "#522");
		IGNORED.put("Subtype_Valid_14", "#522");
		IGNORED.put("Subtype_Valid_7", "#522");
		IGNORED.put("TypeEquals_Valid_2", "#298");
		IGNORED.put("TypeEquals_Valid_23", "Issue ???");
		IGNORED.put("TypeEquals_Valid_25", "#298");
		IGNORED.put("TypeEquals_Valid_3", "Issue ???");
		IGNORED.put("TypeEquals_Valid_30", "#298");
		IGNORED.put("TypeEquals_Valid_36", "Known Issue");
		IGNORED.put("TypeEquals_Valid_37", "Known Issue");
		IGNORED.put("TypeEquals_Valid_38", "Known Issue");
		IGNORED.put("TypeEquals_Valid_41", "Known Issue");
		IGNORED.put("TypeEquals_Valid_45", "#528");
		IGNORED.put("UnionType_Valid_14", "#298");
		IGNORED.put("UnionType_Valid_2", "#468");
		IGNORED.put("UnionType_Valid_22", "Issue ???");
		IGNORED.put("While_Valid_11", "#379");
		IGNORED.put("While_Valid_15", "#298");
		IGNORED.put("While_Valid_16", "#229");
		IGNORED.put("While_Valid_2", "#229");
		IGNORED.put("While_Valid_22", "Issue ???");
		IGNORED.put("While_Valid_23", "#229");
		IGNORED.put("While_Valid_26", "#384");
		IGNORED.put("While_Valid_27", "Issue 378");
		IGNORED.put("While_Valid_34", "#348");
		IGNORED.put("While_Valid_37", "#379");
		IGNORED.put("While_Valid_42", "#379");
		IGNORED.put("While_Valid_3", "466");
		IGNORED.put("While_Valid_5", "#231");

		// Fails and was not listed as test case before parameterizing
		IGNORED.put("RecursiveType_Valid_28", "unknown");
		IGNORED.put("While_Valid_48", "unknown");
		IGNORED.put("DoWhile_Valid_7", "unknown");
		IGNORED.put("Function_Valid_11", "unknown");
		IGNORED.put("Function_Valid_15", "unknown");
		IGNORED.put("Lambda_Valid_9", "unknown");
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

	//
	// Test Harness
	//

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
		name = WHILEY_SRC_DIR + File.separatorChar + name + ".whiley";

		Pair<Integer,String> p = TestUtils.compile(
				"-wd", WHILEY_SRC_DIR,      // location of source directory
				"-wp", WYRT_PATH,           // add wyrt to whileypath
				"-verify",                  // enable verification
				name);                      // name of test to compile

		int r = p.first();
		System.out.print(p.second());

		if (r != WycMain.SUCCESS) {
			fail("Test failed to compile!");
		} else if (r == WycMain.INTERNAL_FAILURE) {
			fail("Test caused internal failure!");
		}

		// TODO: we should actually execute the compiled file here using the
		// WyIL Interpreter (when that exists).
	}

	// ======================================================================
	// Tests
	// ======================================================================

	// Parameter to test case is the name of the current test.
	// It will be passed to the constructor by JUnit.
	private final String testName;
	public AllValidVerificationTests(String testName) {
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
	public void validVerification() throws IOException {
		if (new File("../../running_on_travis").exists()) {
			System.out.println(".");
		}
		runTest(this.testName);
	}
}
