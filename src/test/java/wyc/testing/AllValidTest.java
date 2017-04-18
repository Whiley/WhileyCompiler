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
import wyfs.util.Trie;

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
public class AllValidTest {

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
		IGNORED.put("RecursiveType_Valid_12", "#339");
		IGNORED.put("RecursiveType_Valid_22", "#339");
		//
		IGNORED.put("Function_Valid_15", "#566");
		IGNORED.put("TypeEquals_Valid_23", "#566");
		IGNORED.put("TypeEquals_Valid_41", "#566");
		//
		IGNORED.put("Lifetime_Lambda_Valid_4", "#645");
		//
		IGNORED.put("RecordSubtype_Valid_1", "#696");
		IGNORED.put("RecordSubtype_Valid_2", "#696");
		IGNORED.put("RecursiveType_Valid_3", "#696");
		IGNORED.put("TypeEquals_Valid_36", "#696");
		IGNORED.put("TypeEquals_Valid_37", "#696");
		IGNORED.put("TypeEquals_Valid_38", "#696");
		//
		IGNORED.put("Function_Valid_11", "#702");
		//
		IGNORED.put("Complex_Valid_3", "#339");
		IGNORED.put("DoWhile_Valid_4", "#339");
		IGNORED.put("RecursiveType_Valid_28", "#339");
	}

	/**
	 * The directory where compiler libraries are stored. This is necessary
	 * since it will contain the Whiley Runtime.
	 */
	public final static String WYC_LIB_DIR = "../../lib/".replace('/', File.separatorChar);

	static {

		// The purpose of this is to figure out what the proper name for the
		// wyrt file is. Since there can be multiple versions of this file,
		// we're not sure which one to pick.

		File file = new File(WYC_LIB_DIR);
//		for(String f : file.list()) {
//			if(f.startsWith("wyrt-v")) {
//				WYRT_PATH = WYC_LIB_DIR + f;
//			}
//		}
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
	protected void runTest(String testName) throws IOException {
		File whileySrcDir = new File(WHILEY_SRC_DIR);
		// this will need to turn on verification at some point.
		String whileyFilename = WHILEY_SRC_DIR + File.separatorChar + testName
				+ ".whiley";

		Pair<Compile.Result,String> p = TestUtils.compile(
				whileySrcDir,      // location of source directory
				false,               // no verification
				whileyFilename);     // name of test to compile

		Compile.Result r = p.first();

		System.out.print(p.second());

		if (r != Compile.Result.SUCCESS) {
			fail("Test failed to compile!");
		} else if (r == Compile.Result.INTERNAL_FAILURE) {
			fail("Test caused internal failure!");
		}

		// Execute the compile WyIL file
		TestUtils.execWyil(whileySrcDir, Trie.fromString(testName));
	}

	// ======================================================================
	// Tests
	// ======================================================================

	// Parameter to test case is the name of the current test.
	// It will be passed to the constructor by JUnit.
	private final String testName;
	public AllValidTest(String testName) {
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
	public void valid() throws IOException {
		if (new File("../../running_on_travis").exists()) {
			System.out.println(".");
		}
		runTest(this.testName);
	}
}
