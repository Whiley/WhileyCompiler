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
import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wyc.command.Compile;
import wyc.util.TestUtils;
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
public class AllValidVerificationTest {
	@Rule
	public Timeout globalTimeout = new Timeout(15, TimeUnit.SECONDS);

	/**
	 * The directory containing the source files for each test case. Every test
	 * corresponds to a file in this directory.
	 */
	public final static String WHILEY_SRC_DIR = "tests/valid".replace('/', File.separatorChar);

	/**
	 * Ignored tests and a reason why we ignore them.
	 */
	public final static Map<String, String> IGNORED = new HashMap<>();

	static {
		// =================================================
		// Whiley Compiler Problems
		// =================================================
		// Bring over all the currently failing tests for the compiler. There's
		// absolutely no point trying to see whether these work or not, since we
		// already know they will not.
		IGNORED.putAll(AllValidTest.IGNORED);
		// Null Checks as Type Tests in VCG (closed)
		IGNORED.put("IfElse_Valid_4", "#712");
		IGNORED.put("Complex_Valid_4", "#712");
		IGNORED.put("ListAccess_Valid_6", "712");
		// Verification Condition Generation and Dereference Assignment
		IGNORED.put("Process_Valid_1", "#743");
		IGNORED.put("Process_Valid_9", "#743");
		IGNORED.put("Process_Valid_10", "#743");
		IGNORED.put("Process_Valid_11", "#743");
		IGNORED.put("Reference_Valid_2", "#743");
		IGNORED.put("Reference_Valid_3", "#743");
		IGNORED.put("Reference_Valid_6", "#743");
		// Flow Typing and VerificationConditionGeneration
		IGNORED.put("RecursiveType_Valid_3", "#781");
		// WyTP Variable Ordering Effect
		IGNORED.put("ConstrainedList_Valid_26", "#782");
		// ===============================================================
		// Whiley Theorem Prover faults
		// ===============================================================
		// Issue 2 "Verification of Remainder Operator"
		IGNORED.put("ConstrainedInt_Valid_22", "WyTP#2");
		// Issue 12 "Support for Non-linear Arthmetic"
		IGNORED.put("IntMul_Valid_2", "WyTP#12");
		IGNORED.put("While_Valid_27", "WyTP#12");
		IGNORED.put("While_Valid_32", "WyTP#12");
		// Issue 29 "Triggerless Quantifier Instantiation"
		IGNORED.put("Assert_Valid_1", "#29");
		IGNORED.put("ConstrainedList_Valid_14", "WyTP#29");
		IGNORED.put("ConstrainedList_Valid_18", "WyTP#29");
		IGNORED.put("Quantifiers_Valid_2", "WyTP#29");
		// Issue 36 "Support for Division Operator Feature"
		IGNORED.put("Cast_Valid_5", "WyTP#36");
		IGNORED.put("IntOp_Valid_1", "WyTP#36");
		IGNORED.put("IntDiv_Valid_3", "WyTP#36");
		IGNORED.put("Lambda_Valid_3", "WyTP#36");
		IGNORED.put("Lambda_Valid_4", "WyTP#36");
		// Issue 41 "Case Split within Quantifier"
		IGNORED.put("Property_Valid_4", "WyTP#41");
		IGNORED.put("Subtype_Valid_5", "WyTP#41");
		IGNORED.put("RecursiveType_Valid_19", "WyTP#41");
		// Issue 76 "Casting Record Types"
		IGNORED.put("Coercion_Valid_9", "WyTP#76");
		IGNORED.put("RecordCoercion_Valid_1", "WyTP#76");
		// Issue 80 "(Non-)Empty Type"
		IGNORED.put("OpenRecord_Valid_4", "WyTP#80");
		IGNORED.put("OpenRecord_Valid_9", "WyTP#80");
		// Issue 85 "NegativeArraySizeException in CoerciveSubtypeOperator"
		IGNORED.put("ConstrainedRecord_Valid_9", "WyTP#85");
		IGNORED.put("TypeEquals_Valid_54", "WyTP#85");
		// Issue 89 "Unknown Assertion Failure"
		IGNORED.put("While_Valid_37", "WyTP#89");
		// Issue 102 "Support Reference Lifetimes"
		IGNORED.put("Lifetime_Valid_8", "WyTP#102");
		// Issue 104 "Incompleteness in CoerciveSubtypeOperator"
		IGNORED.put("RecursiveType_Valid_7", "WyTP#104");
		IGNORED.put("While_Valid_15", "WyTP#104");
		IGNORED.put("While_Valid_20", "WyTP#104");
		// Issue 107 "Limitation with ReadableRecordExtractor"
		IGNORED.put("TypeEquals_Valid_30", "WyTP#107");
		IGNORED.put("TypeEquals_Valid_25", "WyTP#107");
		// Issue 111 "Infinite Recursive Expansion"
		IGNORED.put("RecursiveType_Valid_28", "WyTP#111");
		IGNORED.put("RecursiveType_Valid_29", "WyTP#111");
		IGNORED.put("Complex_Valid_3", "WyTP#111");
		IGNORED.put("Complex_Valid_8", "WyTP#111");
		IGNORED.put("RecursiveType_Valid_2", "WyTP#111");
		// Issue 112 "More Performance Problems with Type Checking"
		IGNORED.put("Complex_Valid_2", "WyTP#112");
		IGNORED.put("BoolList_Valid_3", "WyTP#112");
		IGNORED.put("While_Valid_2", "WyTP#112");
		IGNORED.put("While_Valid_26", "WyTP#112");
		// Issue 114 "Limitation with TypeTestClosure"
		IGNORED.put("RecursiveType_Valid_4", "WyTP#114");
		//
		IGNORED.put("Ensures_Valid_6", "WyTP#133");
		IGNORED.put("RecursiveType_Valid_22", "WyTP#133");
		IGNORED.put("While_Valid_24", "WyTP#133");
		IGNORED.put("While_Valid_34", "#133");
		IGNORED.put("While_Valid_35", "WyTP#133");
		//
		IGNORED.put("String_Valid_6", "??");
		IGNORED.put("RecursiveType_Valid_12", "??");
		// Performance problems?
		IGNORED.put("Complex_Valid_10", "??");
	}

	/**
	 * The directory where compiler libraries are stored. This is necessary
	 * since it will contain the Whiley Runtime.
	 */
	public final static String WYC_LIB_DIR = "lib/".replace('/', File.separatorChar);

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
	 * @throws IOException
	 */
	protected void runTest(String name) throws IOException {
		File whileySrcDir = new File(WHILEY_SRC_DIR);
		// this will need to turn on verification at some point.
		name = WHILEY_SRC_DIR + File.separatorChar + name + ".whiley";

		Pair<Compile.Result,String> p = TestUtils.compile(
				whileySrcDir,      // location of source directory
				true,                // enable verification
				name);               // name of test to compile

		Compile.Result r = p.first();
		System.out.print(p.second());

		if (r != Compile.Result.SUCCESS) {
			fail("Test failed to compile!");
		} else if (r == Compile.Result.INTERNAL_FAILURE) {
			fail("Test caused internal failure!");
		}
	}

	// ======================================================================
	// Tests
	// ======================================================================

	// Parameter to test case is the name of the current test.
	// It will be passed to the constructor by JUnit.
	private final String testName;
	public AllValidVerificationTest(String testName) {
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
