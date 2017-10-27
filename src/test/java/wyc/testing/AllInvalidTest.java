// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyc.testing;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wyc.command.Compile;
import wyc.util.TestUtils;
import wycc.util.Pair;
import wyfs.util.Trie;

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
public class AllInvalidTest {

	/**
	 * The directory containing the source files for each test case. Every test
	 * corresponds to a file in this directory.
	 */
	public final static String WHILEY_SRC_DIR = "tests/invalid".replace('/', File.separatorChar);

	/**
	 * Ignored tests and a reason why we ignore them.
	 */
	public final static Map<String, String> IGNORED = new HashMap<>();

	static {
		IGNORED.put("Export_Invalid_1", "unclassified");
		IGNORED.put("Function_Invalid_2", "unclassified");
		IGNORED.put("Function_Invalid_9", "unclassified");
		IGNORED.put("Native_Invalid_1", "unclassified");
		//
		IGNORED.put("Parsing_Invalid_1", "608");
		IGNORED.put("Parsing_Invalid_2", "608");
		//
		IGNORED.put("Parsing_Invalid_15", "609");
		IGNORED.put("Parsing_Invalid_27", "609");
		IGNORED.put("Parsing_Invalid_28", "609");
		// Normalisation for Method Subtyping
		IGNORED.put("Lifetime_Lambda_Invalid_3", "#794");
		// Support Captured Lifetime Parameters
		IGNORED.put("Lifetime_Lambda_Invalid_5", "#795");
		IGNORED.put("Lifetime_Lambda_Invalid_6", "#765");
		// Access Static Variable from Type Invariant
		IGNORED.put("Type_Invalid_11", "793");
		// Infinite Array Types
		IGNORED.put("Type_Invalid_10", "823");
		// ===============================================================
		// Whiley Theorem Prover faults
		// ===============================================================
		IGNORED.put("RecursiveType_Invalid_9", "unclassified");
		IGNORED.put("RecursiveType_Invalid_2", "WyTP#26");
	}

	/**
	 * The directory where compiler libraries are stored. This is necessary
	 * since it will contain the Whiley Runtime.
	 */
	public final static String WYC_LIB_DIR = "../../lib/".replace('/', File.separatorChar);

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
	 * @throws IOException
	 */
	protected void runTest(String name) throws IOException {
		File whileySrcDir = new File(WHILEY_SRC_DIR);
		// this will need to turn on verification at some point.
		String filename = WHILEY_SRC_DIR + File.separatorChar + name + ".whiley";

		Pair<Compile.Result,String> p = TestUtils.compile(
				whileySrcDir,      // location of source directory
				true,                // enable verification
				filename);           // name of test to compile

		Compile.Result r = p.first();
		String output = p.second();

		if (r == Compile.Result.SUCCESS) {
			// This indicates the problem is some form of assertion error.
			// Therefore, execute the code whilst expecting an assertion failure
			//try {
				//TestUtils.execWyil(whileySrcDir, Trie.fromString(testName));
				fail("Test compiled when it shouldn't have!");
//			} catch(AssertionError e) {
//				// OK
//			}
		} else if (r == Compile.Result.INTERNAL_FAILURE) {
			// This indicates some other kind of internal failure.
			fail("Test caused internal failure!\n" + output);
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
			if(!TestUtils.compare(output,sampleOutputFile)) {
				fail("Output does not match reference");
			}
		}
	}

	// ======================================================================
	// Tests
	// ======================================================================

	// Parameter to test case is the name of the current test.
	// It will be passed to the constructor by JUnit.
	private final String testName;
	public AllInvalidTest(String testName) {
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
		if (new File("../../running_on_travis").exists()) {
			System.out.println(".");
		}
		runTest(this.testName);
	}
}
