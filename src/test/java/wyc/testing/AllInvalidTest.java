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
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wyc.util.TestUtils;
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
		IGNORED.put("Parsing_Invalid_1", "608");
		IGNORED.put("Parsing_Invalid_2", "608");
		// Normalisation for Method Subtyping
		IGNORED.put("Lifetime_Lambda_Invalid_3", "#794");
		// Support Captured Lifetime Parameters
		IGNORED.put("Lifetime_Lambda_Invalid_5", "#795");
		IGNORED.put("Lifetime_Lambda_Invalid_6", "#765");
		// Access Static Variable from Type Invariant
		IGNORED.put("Type_Invalid_11", "793");
		// Problems with counterexample generation?
		IGNORED.put("DoWhile_Invalid_6", "??");
		IGNORED.put("DoWhile_Invalid_8", "??");
		IGNORED.put("While_Invalid_18", "??");
		IGNORED.put("While_Invalid_20", "??");
		IGNORED.put("While_Invalid_21", "??");
		IGNORED.put("While_Invalid_22", "??");
		IGNORED.put("While_Invalid_23", "??");
		IGNORED.put("TupleAssign_Invalid_3", "??");
		IGNORED.put("TypeEquals_Invalid_5", "??");
//		// #885 --- Contractive Types and isVoid()
		IGNORED.put("Type_Invalid_5", "885");
		IGNORED.put("Type_Invalid_8", "??");
		IGNORED.put("Reference_Invalid_2", "unclassified");
		IGNORED.put("Type_Invalid_14", "??");
		IGNORED.put("Type_Invalid_15", "??");
		IGNORED.put("While_Invalid_24", "??");
		IGNORED.put("While_Invalid_25", "#956");
		IGNORED.put("For_Invalid_9", "#982");
		// Lifetime infernece
		IGNORED.put("Lifetime_Invalid_5", "???");
		IGNORED.put("Lifetime_Invalid_6", "???");
		IGNORED.put("Lifetime_Invalid_7", "???");
		IGNORED.put("Lifetime_Invalid_8", "???");
		IGNORED.put("Lifetime_Invalid_9", "???");
		IGNORED.put("Lifetime_Lambda_Invalid_1", "???");
		IGNORED.put("Lifetime_Lambda_Invalid_2", "???");
		// ===============================================================
		// Whiley Theorem Prover faults
		// ===============================================================
		IGNORED.put("RecursiveType_Invalid_9", "unclassified");
		IGNORED.put("RecursiveType_Invalid_2", "WyTP#26");
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
	 * @throws IOException
	 */
	protected void runTest(String name) throws IOException {
		File whileySrcDir = new File(WHILEY_SRC_DIR);

		Pair<Boolean,String> p = TestUtils.compile(
				whileySrcDir,      // location of source directory
				true,              // enable verification
				true,              // enable counterexample generation
				name);             // name of test to compile

		boolean r = p.first();
		String output = p.second();
		// Now, let's check the expected output against the file which
		// contains the sample output for this test
		String sampleOutputFile = WHILEY_SRC_DIR + File.separatorChar + name
				+ ".sysout";

		//			Following used when sample output changed.
//					try {
//						FileWriter fw = new FileWriter(sampleOutputFile);
//						fw.write(output);
//						fw.close();
//					} catch(Exception e) {}

		// Third, compare the output!
		if(!TestUtils.compare(output,sampleOutputFile)) {
			fail("Output does not match reference");
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
