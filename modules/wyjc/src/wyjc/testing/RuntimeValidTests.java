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

package wyjc.testing;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import wybs.lang.Build;
import wybs.util.StdProject;
import wyc.WycMain;
import wyc.testing.TestUtils;
import wyc.util.WycBuildTask;
import wycc.util.Pair;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.Trie;
import wyil.Main.Registry;
import wyjc.WyjcMain;
import wyjc.util.WyjcBuildTask;

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
public class RuntimeValidTests {
	
	/**
	 * Ignored tests and a reason why we ignore them.
	 */
	public final static Map<String, String> IGNORED = new HashMap<String, String>();

	static {
		IGNORED.put("Complex_Valid_3", "Issue ???");
		IGNORED.put("Complex_Valid_4", "#663");
		IGNORED.put("ConstrainedIntersection_Valid_1", "unknown");
		IGNORED.put("ConstrainedNegation_Valid_1", "#342");
		IGNORED.put("ConstrainedNegation_Valid_2", "#342");
		IGNORED.put("FunctionRef_Valid_10", "#663");
		IGNORED.put("FunctionRef_Valid_11", "#663");
		IGNORED.put("FunctionRef_Valid_13", "#555");
		IGNORED.put("IfElse_Valid_4", "#663");
		IGNORED.put("Import_Valid_4", "#492");
		IGNORED.put("Import_Valid_5", "#492");
		IGNORED.put("Intersection_Valid_2", "Issue ???");
		IGNORED.put("ListAccess_Valid_6", "Issue ???");
		IGNORED.put("OpenRecord_Valid_5", "#663");
		IGNORED.put("OpenRecord_Valid_6", "#664");
		IGNORED.put("OpenRecord_Valid_11", "#585");
		IGNORED.put("RecordSubtype_Valid_1", "Issue ???");
		IGNORED.put("RecordSubtype_Valid_2", "Issue ???");
		IGNORED.put("RecursiveType_Valid_2", "#663");		
		IGNORED.put("RecursiveType_Valid_12", "#339");
		IGNORED.put("RecursiveType_Valid_21", "#663");
		IGNORED.put("RecursiveType_Valid_22", "#339");
		IGNORED.put("RecursiveType_Valid_26", "unknown");
		IGNORED.put("RecursiveType_Valid_28", "#364");
		IGNORED.put("RecursiveType_Valid_3", "#406");
		IGNORED.put("RecursiveType_Valid_4", "#406");
		IGNORED.put("RecursiveType_Valid_5", "#18");
		IGNORED.put("TypeEquals_Valid_23", "Issue ???");
		IGNORED.put("TypeEquals_Valid_36", "Issue ???");
		IGNORED.put("TypeEquals_Valid_37", "Issue ???");
		IGNORED.put("TypeEquals_Valid_38", "Issue ???");
		IGNORED.put("TypeEquals_Valid_41", "Issue ???");
		IGNORED.put("While_Valid_15", "unknown");
		IGNORED.put("While_Valid_20", "unknown");
		
		// Fails and was not listed as test case before parameterizing
		IGNORED.put("Function_Valid_11", "unknown");
		IGNORED.put("Function_Valid_15", "unknown");
	
		// Fails for reasons unknown
		IGNORED.put("ConstrainedReference_Valid_1", "unknown");
		IGNORED.put("FunctionRef_Valid_12", "unknown");
		IGNORED.put("Lifetime_Lambda_Valid_4", "unknown");
	}

	/**
	 * The directory containing the source files for each test case. Every test
	 * corresponds to a file in this directory.
	 */
	public final static String WHILEY_SRC_DIR = "../../tests/valid".replace('/', File.separatorChar);

	/**
	 * The path to the Whiley-2-Java class files. These are needed so we can
	 * access the JVM compiled version of the Whiley standard library, along
	 * with additional runtime support classes.
	 */
 	private static final String WYJC_CLASS_DIR="../../modules/wyjc/src/".replace('/', File.separatorChar);

	private static final String WYBS_CLASS_DIR="../../modules/wybs/src/".replace('/', File.separatorChar);
	
 	private static final String WYRL_CLASS_DIR="../../lib/wyrl-v0.4.4.jar".replace('/', File.separatorChar);

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

 		int r = compile(
 				"-wd", WHILEY_SRC_DIR,      // location of source directory
 				"-cd", WHILEY_SRC_DIR,      // location where to place class files
 				"-wp", WYRT_PATH,           // add wyrt to whileypath
 				filename);             // name of test to compile

 		if (r != WycMain.SUCCESS) {
 			fail("Test failed to compile!");
 		} else if (r == WycMain.INTERNAL_FAILURE) {
 			fail("Test caused internal failure!");
 		}

		String CLASSPATH = CLASSPATH(WHILEY_SRC_DIR, WYJC_CLASS_DIR,
				WYRL_CLASS_DIR, WYBS_CLASS_DIR);

 		// Second, execute the generated Java Program.
 		String output = TestUtils.execClass(CLASSPATH,WHILEY_SRC_DIR,"wyjc.testing.RuntimeValidTests",name);
 		if(!output.equals("")) {
 			System.out.println(output);
 			fail("unexpected output!");
 		}
 	}

 	/**
	 * This is the entry point for each test. The argument provided is the name
	 * of the test class. The special method "test" is then invoked on this
	 * class with no arguments provided. The method should execute without
	 * producing output (success), or report some kind of runtime fault
	 * (failure).
	 * 
	 * @param args
	 */
 	public static void main(String[] args) {
 		String testClassName = args[0]; 		
 		try {
 			Class testClass = Class.forName(testClassName);
 			Method testMethod = testClass.getMethod("test");
 			testMethod.invoke(null);
 		} catch(ClassNotFoundException e) {
 			e.printStackTrace(System.out);
 		} catch(NoSuchMethodException e) {
 			e.printStackTrace(System.out);
 		} catch(InvocationTargetException e) {
 			e.printStackTrace(System.out);
 		} catch (IllegalAccessException e) {
			e.printStackTrace(System.out);
		} 
 	}
 	
 	/**
	 * Run the Whiley Compiler with the given list of arguments.
	 *
	 * @param args
	 *            --- list of command-line arguments to provide to the Whiley
	 *            Compiler.
	 * @return
	 */
	public static int compile(String... args) {
		return new WyjcMain(new WyjcBuildTask(), WyjcMain.DEFAULT_OPTIONS)
				.run(args);
	}

	/**
	 * Construct a classpath string from a sequence of path components. Each
	 * classpath component is separated by ':' (note that
	 * <code>TestUtils.exec</code> takes care of platform dependent path
	 * separators).
	 *
	 * @param components
	 * @return
	 */
	public String CLASSPATH(String... components) {
		String r = "";
		boolean firstTime = true;
		for (String c : components) {
			if (!firstTime) {
				r = r + ":";
			}
			firstTime = false;
			r = r + c;
		}
		return r;
	}
	
	// ======================================================================
	// Tests
	// ======================================================================

	// Parameter to test case is the name of the current test.
	// It will be passed to the constructor by JUnit.
	private final String testName;
	public RuntimeValidTests(String testName) {
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
		runTest(this.testName);
	}
}
