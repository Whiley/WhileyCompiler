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
package wyll.testing;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.util.StdProject;
import wyc.command.Compile;
import wyc.lang.WhileyFile;
import wyc.util.TestUtils;
import wycc.util.Logger;
import wycc.util.Pair;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.lang.Content.Type;
import wyfs.lang.Path.Entry;
import wyfs.util.DirectoryRoot;
import wyfs.util.Trie;
import wyll.command.LowLevelCompile;
import wyll.core.WyllFile;
import wyll.interpreter.Interpreter;
import wyll.io.WyllFileReader;

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
public class RuntimeValidTest {

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
		// Problem Type Checking Union Type
		IGNORED.put("RecordSubtype_Valid_1", "#696");
		IGNORED.put("RecordSubtype_Valid_2", "#696");
		// Function Overloading for Nominal Types
		IGNORED.put("Function_Valid_11", "#702");
		IGNORED.put("Function_Valid_15", "#702");
		//  Normalisation for Method Subtyping
		IGNORED.put("Lifetime_Lambda_Valid_2", "#794");
		IGNORED.put("Lifetime_Lambda_Valid_5", "#794");
		IGNORED.put("Lifetime_Lambda_Valid_6", "#794");
		// Support Captured Lifetime Parameters
		IGNORED.put("Lifetime_Lambda_Valid_7", "#795");
		// Type Tests with Invariants
		IGNORED.put("TypeEquals_Valid_23", "#787");
		IGNORED.put("TypeEquals_Valid_25", "#787");
		IGNORED.put("TypeEquals_Valid_30", "#787");
		IGNORED.put("TypeEquals_Valid_41", "#787");
		// Remove Any and Negation Types
		IGNORED.put("ConstrainedReference_Valid_1", "#827");
		// Temporary Removal of Intersections
		IGNORED.put("Intersection_Valid_1", "#843");
		IGNORED.put("Intersection_Valid_2", "#843");
		IGNORED.put("NegationType_Valid_3", "#843");
		// Problems with relaxed versus strict subtype operator
		IGNORED.put("Function_Valid_22", "#845");
		// Unclassified
		IGNORED.put("Lifetime_Valid_8", "???");
		// Readable Reference Types
		IGNORED.put("UnionType_Valid_26", "#849");
		// Rethinking Runtime Type Test Operator ?
		IGNORED.put("RecordAssign_Valid_11", "#850");
		// Ambiguous coercions
		IGNORED.put("TypeEquals_Valid_33", "#837");
		IGNORED.put("TypeEquals_Valid_35", "#837");
		IGNORED.put("Coercion_Valid_10", "#837");
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

		Pair<Compile.Result,String> p = compileWhiley2Wyll(
				WHILEY_SRC_DIR,      // location of source directory
				whileyFilename);     // name of test to compile

		Compile.Result r = p.first();

		System.out.print(p.second());

		if (r != Compile.Result.SUCCESS) {
			fail("Test failed to compile!");
		} else if (r == Compile.Result.INTERNAL_FAILURE) {
			fail("Test caused internal failure!");
		}

		// Execute the compile WyIL file
		execWyll(WHILEY_SRC_DIR, Trie.fromString(testName));
	}


 	/**
	 * Run the Whiley Compiler with the given list of arguments to produce a
	 * JavaScript source file. This will then need to be separately compiled.
	 *
	 * @param args
	 *            --- list of command-line arguments to provide to the Whiley
	 *            Compiler.
	 * @return
	 * @throws IOException
	 */
	public static Pair<Compile.Result,String> compileWhiley2Wyll(String whileydir, String... args) throws IOException {
		ByteArrayOutputStream syserr = new ByteArrayOutputStream();
		ByteArrayOutputStream sysout = new ByteArrayOutputStream();
		Content.Registry registry = new wyc.Activator.Registry();
		LowLevelCompile cmd = new LowLevelCompile(registry,Logger.NULL,sysout,syserr);
		cmd.setWhileydir(new File(whileydir));
		cmd.setVerbose();
		Compile.Result result = cmd.execute(args);
		byte[] errBytes = syserr.toByteArray();
		byte[] outBytes = sysout.toByteArray();
		String output = new String(errBytes) + new String(outBytes);
		return new Pair<>(result,output);
	}
	/**
	 * Execute a given JavaScript file stored on disk using the built-in
	 * "Nashorn" interpreter.
	 *
	 * @param filename
	 *            The fully qualified name of the JavaScript file to be
	 *            executed.
	 * @throws ScriptException
	 * @throws IOException
	 */
	private void execWyll(String wylldir, Path.ID mid) throws IOException {
		Build.Project project = initialiseProject(wylldir);
		Path.Entry<WyllFile> wlf = project.get(mid, WyllFile.ContentType);
		WyllFile.Type.Method sig = new WyllFile.Type.Method(new WyllFile.Tuple<>(), WyllFile.Type.Void);
		NameID nid = new NameID(mid, "test");
		Interpreter interpreter = new Interpreter(project, System.err, wlf.read());
		Interpreter.CallStack frame = interpreter.new CallStack();
		interpreter.execute(nid, sig, frame);
	}

	private Build.Project initialiseProject(String wylldir) throws IOException {
		// Add roots and construct project
		Content.Registry registry = new Content.Registry() {

			@Override
			public String suffix(Type<?> t) {
				return t.getSuffix();
			}

			@Override
			public void associate(Entry e) {
				String suffix = e.suffix();
				if (suffix.equals("wyll")) {
					e.associate(WyllFile.ContentType, null);
				}
			}
		};
		DirectoryRoot root = new DirectoryRoot(wylldir,registry);
		ArrayList<Path.Root> roots = new ArrayList<>();
		roots.add(root);
		return new StdProject(roots);
	}

	// ======================================================================
	// Tests
	// ======================================================================

	// Parameter to test case is the name of the current test.
	// It will be passed to the constructor by JUnit.
	private final String testName;
	public RuntimeValidTest(String testName) {
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
