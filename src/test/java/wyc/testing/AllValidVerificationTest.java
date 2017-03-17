// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

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

import wyc.commands.Compile;
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
		//
		IGNORED.put("ConstrainedList_Valid_28", "#666");
		//
		IGNORED.put("IfElse_Valid_4", "#712");
		IGNORED.put("While_Valid_15", "#712");
		IGNORED.put("While_Valid_20", "#712");
		//
		IGNORED.put("RecursiveType_Valid_17", "#713");
		IGNORED.put("RecursiveType_Valid_18", "#713");
		IGNORED.put("Return_Valid_1", "#713");
		IGNORED.put("UnionType_Valid_2", "#713");
		//
		IGNORED.put("Record_Valid_3", "#714");

		// ===============================================================
		// Whiley Theorem Prover faults
		// ===============================================================

		// timeouts (because of constrained types)
		IGNORED.put("ConstrainedList_Valid_1", "timeout");
		IGNORED.put("ConstrainedList_Valid_19", "timeout");
		IGNORED.put("ConstrainedList_Valid_16", "timeout");
		IGNORED.put("Function_Valid_6", "timeout");
		// timeouts
		IGNORED.put("BoolList_Valid_3", "timeout");
		IGNORED.put("Complex_Valid_5", "timeout");
		IGNORED.put("Complex_Valid_8", "timeout");
		IGNORED.put("ConstrainedList_Valid_9", "timeout");
		IGNORED.put("ConstrainedList_Valid_17", "timeout");
		IGNORED.put("ConstrainedList_Valid_21", "timeout");
		IGNORED.put("ConstrainedList_Valid_22", "timeout");
		IGNORED.put("ConstrainedList_Valid_2", "timeout");
		IGNORED.put("ConstrainedList_Valid_6", "timeout");
		IGNORED.put("ConstrainedList_Valid_8", "timeout");
		IGNORED.put("ConstrainedList_Valid_23", "timeout");
		IGNORED.put("ConstrainedRecord_Valid_9", "timeout");
		IGNORED.put("Complex_Valid_1", "timeout");
		IGNORED.put("Ensures_Valid_3", "timeout");
		IGNORED.put("FunctionRef_Valid_4", "timeout");
		IGNORED.put("FunctionRef_Valid_11", "timeout");
		IGNORED.put("Function_Valid_18", "timeout");
		IGNORED.put("Lambda_Valid_3", "timeout");
		IGNORED.put("Lambda_Valid_4", "timeout");
		IGNORED.put("ListAssign_Valid_12", "timeout");
		IGNORED.put("ListAssign_Valid_3", "timeout");
		IGNORED.put("ListAssign_Valid_5", "timeout");
		IGNORED.put("ListAssign_Valid_8", "timeout");
		IGNORED.put("ListAccess_Valid_6", "timeout");
		IGNORED.put("Method_Valid_1", "timeout");
		IGNORED.put("OpenRecord_Valid_5", "timeout");
		IGNORED.put("OpenRecord_Valid_6", "timeout");
		IGNORED.put("Property_Valid_7", "timeout");
		IGNORED.put("RecordCoercion_Valid_1", "timeout");
		IGNORED.put("RecursiveType_Valid_4", "timeout");
		IGNORED.put("RecursiveType_Valid_11", "timeout");
		IGNORED.put("RecursiveType_Valid_14", "timeout");
		IGNORED.put("TypeEquals_Valid_3", "timeout");
		IGNORED.put("TypeEquals_Valid_25", "timeout");
		IGNORED.put("TypeEquals_Valid_54", "timeout");
		IGNORED.put("UnionType_Valid_11", "timeout");
		IGNORED.put("UnionType_Valid_22", "timeout");
		IGNORED.put("UnionType_Valid_23", "timeout");
		IGNORED.put("While_Valid_5", "timeout");
		IGNORED.put("While_Valid_11", "timeout");
		IGNORED.put("While_Valid_34", "timeout");
		IGNORED.put("While_Valid_37", "timeout");
		IGNORED.put("While_Valid_41", "timeout");
		IGNORED.put("While_Valid_42", "timeout");
		IGNORED.put("While_Valid_43", "timeout");
		IGNORED.put("While_Valid_45", "timeout");
		IGNORED.put("While_Valid_53", "timeout");
		IGNORED.put("While_Valid_54", "timeout");
		IGNORED.put("While_Valid_2", "timeout");
		IGNORED.put("While_Valid_16", "timeout");
		IGNORED.put("While_Valid_22", "timeout");
		IGNORED.put("While_Valid_26", "timeout");
		// Issue 2
		IGNORED.put("ConstrainedInt_Valid_22", "WyTP#2");
		// Issue 12
		IGNORED.put("While_Valid_27", "WyTP#12");
		IGNORED.put("While_Valid_32", "WyTP#12");
		// Issue 26
		IGNORED.put("Coercion_Valid_8", "WyTP#26");
		IGNORED.put("Coercion_Valid_9", "WyTP#26");
		IGNORED.put("Fail_Valid_3", "WyTP#26");
		IGNORED.put("FunctionRef_Valid_10", "WyTP#26");
		IGNORED.put("RecursiveType_Valid_1", "WyTP#26");
		IGNORED.put("RecursiveType_Valid_2", "WyTP#26");
		IGNORED.put("RecursiveType_Valid_7", "WyTP#26");
		IGNORED.put("RecursiveType_Valid_21", "WyTP#26");
		IGNORED.put("RecursiveType_Valid_23", "WyTP#26");
		IGNORED.put("RecursiveType_Valid_24", "WyTP#26");
		IGNORED.put("RecursiveType_Valid_26", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_17", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_2", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_21", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_24", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_28", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_29", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_30", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_31", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_32", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_34", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_35", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_39", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_42", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_43", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_44", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_52", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_56", "WyTP#26");
		IGNORED.put("TypeEquals_Valid_9", "WyTP#26");
		IGNORED.put("UnionType_Valid_11", "WyTP#26");
		IGNORED.put("UnionType_Valid_14", "WyTP#26");
		IGNORED.put("UnionType_Valid_17", "WyTP#26");
		// Issue 27
		IGNORED.put("Complex_Valid_2", "WyTP#27");
		// Issue 28
		IGNORED.put("Complex_Valid_4", "WyTP#28");
		// Issue 29
		IGNORED.put("ConstrainedList_Valid_18", "WyTP#29");
		// Issue 30
		IGNORED.put("UnionType_Valid_21", "WyTP#30");
		IGNORED.put("Ensures_Valid_7", "WyTP#30");
		IGNORED.put("TypeEquals_Valid_55", "WyTP#30");
		// Issue 31
		IGNORED.put("Fail_Valid_1", "WyTP#31");
		// Issue 32
		IGNORED.put("Function_Valid_13", "WyTP#32");
		// Issue 33
		IGNORED.put("OpenRecord_Valid_1", "WyTP#33");
		IGNORED.put("OpenRecord_Valid_2", "WyTP#33");
		IGNORED.put("OpenRecord_Valid_4", "WyTP#33");
		IGNORED.put("OpenRecord_Valid_9", "WyTP#33");
		IGNORED.put("OpenRecord_Valid_10", "WyTP#33");
		IGNORED.put("OpenRecord_Valid_11", "WyTP#33");
		// Issue 34
		IGNORED.put("ConstrainedRecord_Valid_8", "WyTP#34");
		IGNORED.put("RecursiveType_Valid_19", "WyTP#34");
		IGNORED.put("Subtype_Valid_5", "WyTP#34");
		IGNORED.put("TypeEquals_Valid_47", "WyTP#34");
		// Issue 35
		IGNORED.put("UnionType_Valid_7",  "WyTP#35");
		IGNORED.put("UnionType_Valid_8",  "WyTP#35");
		// Issue 36
		IGNORED.put("Cast_Valid_5", "WyTP#36");
		IGNORED.put("IntOp_Valid_1", "WyTP#36");
		IGNORED.put("IntDiv_Valid_3", "WyTP#36");
		// Issue 37
		IGNORED.put("Process_Valid_1", "WyTP#37");
		IGNORED.put("Process_Valid_9", "WyTP#37");
		IGNORED.put("Process_Valid_10", "WyTP#37");
		IGNORED.put("Reference_Valid_2", "WyTP#37");
		IGNORED.put("Reference_Valid_3", "WyTP#37");
		IGNORED.put("Reference_Valid_6", "WyTP#37");
		//
		IGNORED.put("ConstrainedNegation_Valid_1", "WyTP#39");
		//
		IGNORED.put("Property_Valid_4", "WyTP#41");
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
