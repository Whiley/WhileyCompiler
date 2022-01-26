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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.*;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wycc.util.Pair;
import wycc.util.Trie;
import wyc.util.TestUtils;


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
		IGNORED.put("Reference_Invalid_5", "??");
		//
		IGNORED.put("For_Invalid_11", "??");
		IGNORED.put("Subtype_Invalid_11", "??");
		IGNORED.put("Subtype_Invalid_12", "??");
		IGNORED.put("Subtype_Invalid_13", "??");
		IGNORED.put("RecursiveType_Invalid_9", "unclassified");
		IGNORED.put("RecursiveType_Invalid_2", "WyTP#26");
		// Ignored because removed theorem prover
		IGNORED.put("ArrayAccess_Invalid_2", "???");
		IGNORED.put("ArrayAccess_Invalid_4", "???");
		IGNORED.put("ArrayAccess_Invalid_5", "???");
		IGNORED.put("ArrayAssign_Invalid_2", "???");
		IGNORED.put("ArrayAssign_Invalid_3", "???");
		IGNORED.put("ArrayEmpty_Invalid_2", "???");
		IGNORED.put("ArrayEquals_Invalid_1", "???");
		IGNORED.put("ArrayGenerator_Invalid_1", "???");
		IGNORED.put("ArrayGenerator_Invalid_2", "???");
		IGNORED.put("ArrayLength_Invalid_1", "???");
		IGNORED.put("ArrayLength_Invalid_2", "???");
		IGNORED.put("ArrayLength_Invalid_3", "???");
		IGNORED.put("ArrayUpdate_Invalid_1", "???");
		IGNORED.put("Array_Invalid_10", "???");
		IGNORED.put("Array_Invalid_11", "???");
		IGNORED.put("Array_Invalid_12", "???");
		IGNORED.put("Array_Invalid_13", "???");
		IGNORED.put("Array_Invalid_14", "???");
		IGNORED.put("Array_Invalid_15", "???");
		IGNORED.put("Array_Invalid_16", "???");
		IGNORED.put("Array_Invalid_17", "???");
		IGNORED.put("Array_Invalid_18", "???");
		IGNORED.put("Array_Invalid_19", "???");
		IGNORED.put("Array_Invalid_20", "???");
		IGNORED.put("Array_Invalid_21", "???");
		IGNORED.put("Array_Invalid_22", "???");
		IGNORED.put("Array_Invalid_23", "???");
		IGNORED.put("Array_Invalid_24", "???");
		IGNORED.put("Array_Invalid_8", "???");
		IGNORED.put("Array_Invalid_9", "???");
		IGNORED.put("Assert_Invalid_3", "???");
		IGNORED.put("Byte_Invalid_2", "???");
		IGNORED.put("ConstrainedArray_Invalid_1", "???");
		IGNORED.put("ConstrainedArray_Invalid_2", "???");
		IGNORED.put("ConstrainedArray_Invalid_3", "???");
		IGNORED.put("ConstrainedInt_Invalid_1", "???");
		IGNORED.put("ConstrainedInt_Invalid_12", "???");
		IGNORED.put("ConstrainedInt_Invalid_2", "???");
		IGNORED.put("ConstrainedInt_Invalid_3", "???");
		IGNORED.put("ConstrainedInt_Invalid_4", "???");
		IGNORED.put("ConstrainedInt_Invalid_5", "???");
		IGNORED.put("ConstrainedInt_Invalid_6", "???");
		IGNORED.put("ConstrainedInt_Invalid_7", "???");
		IGNORED.put("ConstrainedInt_Invalid_8", "???");
		IGNORED.put("ConstrainedInt_Invalid_9", "???");
		IGNORED.put("ConstrainedRecord_Invalid_1", "???");
		IGNORED.put("DoWhile_Invalid_1", "???");
		IGNORED.put("DoWhile_Invalid_2", "???");
		IGNORED.put("DoWhile_Invalid_3", "???");
		IGNORED.put("DoWhile_Invalid_4", "???");
		IGNORED.put("DoWhile_Invalid_7", "???");
		IGNORED.put("EndOfFile_Invalid_1", "???");
		IGNORED.put("Ensures_Invalid_1", "???");
		IGNORED.put("Ensures_Invalid_3", "???");
		IGNORED.put("Ensures_Invalid_4", "???");
		IGNORED.put("Fail_Invalid_1", "???");
		IGNORED.put("Fail_Invalid_3", "???");
		IGNORED.put("For_Invalid_7", "???");
		IGNORED.put("For_Invalid_8", "???");
		IGNORED.put("If_Invalid_5", "???");
		IGNORED.put("Infeasible_Constant_1", "???");
		IGNORED.put("Infeasible_Function_1", "???");
		IGNORED.put("Infeasible_Function_5", "???");
		IGNORED.put("Infeasible_Function_6", "???");
		IGNORED.put("IntDiv_Invalid_1", "???");
		IGNORED.put("Lambda_Invalid_3", "???");
		IGNORED.put("Parsing_Invalid_32", "???");
		IGNORED.put("Old_Invalid_7", "???");
		IGNORED.put("Old_Invalid_8", "???");
		IGNORED.put("Old_Invalid_13", "???");
		IGNORED.put("Old_Invalid_14", "???");
		IGNORED.put("Old_Invalid_15", "???");
		IGNORED.put("Old_Invalid_16", "???");
		IGNORED.put("Property_Invalid_2", "???");
		IGNORED.put("Property_Invalid_3", "???");
		IGNORED.put("Property_Invalid_4", "???");
		IGNORED.put("Property_Invalid_8", "???");
		IGNORED.put("Quantifiers_Invalid_1", "???");
		IGNORED.put("Quantifiers_Invalid_2", "???");
		IGNORED.put("Quantifiers_Invalid_3", "???");
		IGNORED.put("Quantifiers_Invalid_4", "???");
		IGNORED.put("Quantifiers_Invalid_5", "???");
		IGNORED.put("Quantifiers_Invalid_6", "???");
		IGNORED.put("Quantifiers_Invalid_7", "???");
		IGNORED.put("Quantifiers_Invalid_8", "???");
		IGNORED.put("RecursiveType_Invalid_1", "???");
		IGNORED.put("RecursiveType_Invalid_10", "???");
		IGNORED.put("RecursiveType_Invalid_4", "???");
		IGNORED.put("RecursiveType_Invalid_7", "???");
		IGNORED.put("RecursiveType_Invalid_8", "???");
		IGNORED.put("Reference_Invalid_6", "???");
		IGNORED.put("Reference_Invalid_7", "???");
		IGNORED.put("Remainder_Invalid_1", "???");
		IGNORED.put("Requires_Invalid_1", "???");
		IGNORED.put("StaticVar_Invalid_5", "???");
		IGNORED.put("StaticVar_Invalid_13", "???");
		IGNORED.put("StaticVar_Invalid_14", "???");
		IGNORED.put("Subtype_Invalid_1", "???");
		IGNORED.put("Subtype_Invalid_10", "???");
		IGNORED.put("Subtype_Invalid_2", "???");
		IGNORED.put("Subtype_Invalid_3", "???");
		IGNORED.put("Subtype_Invalid_4", "???");
		IGNORED.put("Subtype_Invalid_5", "???");
		IGNORED.put("Subtype_Invalid_6", "???");
		IGNORED.put("Subtype_Invalid_7", "???");
		IGNORED.put("Subtype_Invalid_8", "???");
		IGNORED.put("Subtype_Invalid_9", "???");
		IGNORED.put("Template_Invalid_18", "???");
		IGNORED.put("TupleAssign_Invalid_1", "???");
		IGNORED.put("TupleAssign_Invalid_2", "???");
		IGNORED.put("TupleDefine_Invalid_2", "???");
		IGNORED.put("Tuple_Invalid_3", "???");
		IGNORED.put("Tuple_Invalid_4", "???");
		IGNORED.put("Tuple_Invalid_5", "???");
		IGNORED.put("Tuple_Invalid_8", "???");
		IGNORED.put("TypeEquals_Invalid_12", "???");
		IGNORED.put("TypeEquals_Invalid_6", "???");
		IGNORED.put("UnionType_Invalid_10", "???");
		IGNORED.put("UnionType_Invalid_7", "???");
		IGNORED.put("UnionType_Invalid_8", "???");
		IGNORED.put("UnionType_Invalid_9", "???");
		IGNORED.put("VarDecl_Invalid_2", "???");
		IGNORED.put("VarDecl_Invalid_4", "???");
		IGNORED.put("While_Invalid_10", "???");
		IGNORED.put("While_Invalid_11", "???");
		IGNORED.put("While_Invalid_12", "???");
		IGNORED.put("While_Invalid_13", "???");
		IGNORED.put("While_Invalid_15", "???");
		IGNORED.put("While_Invalid_17", "???");
		IGNORED.put("While_Invalid_19", "???");
		IGNORED.put("While_Invalid_5", "???");
		IGNORED.put("While_Invalid_7", "???");
		IGNORED.put("While_Invalid_8", "???");
		IGNORED.put("While_Invalid_9", "???");

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
	protected void runTest(Trie path) throws IOException {
		File whileySrcDir = new File(WHILEY_SRC_DIR);
		ByteArrayOutputStream syserr = new ByteArrayOutputStream();
		PrintStream psyserr = new PrintStream(syserr);
		//
		boolean r = new wyc.Compiler().setOutput(psyserr).setWhileyDir(whileySrcDir).setWyilDir(whileySrcDir)
				.setTarget(path).addSource(path).setVerification(true).setCounterExamples(true).setStrict(true).run();
		if (r) {
			fail("Test should not have compiled!");
		} else {
			// Now, let's check the expected output against the file which
			// contains the sample output for this test
			String sampleOutputFile = WHILEY_SRC_DIR + File.separatorChar + path + ".sysout";
			psyserr.flush();
			// Convert bytes produced into resulting string.
			byte[] errBytes = syserr.toByteArray();
			String output = new String(errBytes);
			// Translate any Windows file separators to Unix.
			output = output.replaceAll("\\\\","/");
			// Build failed, so check output
			if(!compare(output,sampleOutputFile)) {
				fail("Output does not match reference");
			}
		}
	}

	/**
	 * Compare the output of executing java on the test case with a reference
	 * file. If the output differs from the reference output, then the offending
	 * line is written to the stdout and an exception is thrown.
	 *
	 * @param output
	 *            This provides the output from executing java on the test case.
	 * @param referenceFile
	 *            The full path to the reference file. This should use the
	 *            appropriate separator char for the host operating system.
	 * @throws IOException
	 */
	public static boolean compare(String output, String referenceFile) throws IOException {
		BufferedReader outReader = new BufferedReader(new StringReader(output));
		BufferedReader refReader = new BufferedReader(new FileReader(new File(referenceFile)));
		try {
			boolean match = true;
			while (true) {
				String l1 = refReader.readLine();
				String l2 = outReader.readLine();
				if (l1 != null && l2 != null) {
					if (!l1.equals(l2)) {
						System.err.println(" < " + l1);
						System.err.println(" > " + l2);
						match = false;
					}
				} else if (l1 != null) {
					System.err.println(" < " + l1);
					match = false;
				} else if (l2 != null) {
					System.err.println(" > " + l2);
					match = false;
				} else {
					break;
				}
			}
			if (!match) {
				System.err.println();
				return false;
			}
			return true;
		} finally {
			outReader.close();
			refReader.close();
		}
	}

	// ======================================================================
	// Tests
	// ======================================================================

	// Parameter to test case is the name of the current test.
	// It will be passed to the constructor by JUnit.
	private final Trie testName;
	public AllInvalidTest(String testName) {
		this.testName = Trie.fromString(testName);
	}

	// Here we enumerate all available test cases.
	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return TestUtils.findTestNames(WHILEY_SRC_DIR);
	}

	// Skip ignored tests
	@Before
	public void beforeMethod() {
		String ignored = IGNORED.get(this.testName.toString());
		Assume.assumeTrue("Test " + this.testName + " skipped: " + ignored, ignored == null);
	}

	@Test
	public void invalid() throws IOException {
		runTest(this.testName);
	}
}
