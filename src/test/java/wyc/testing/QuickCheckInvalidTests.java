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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wybs.lang.Build;
import wybs.lang.SyntacticException;
import wybs.util.AbstractCompilationUnit;
import wybs.util.SequentialBuildProject;
import wyc.cmd.QuickCheck;
import wyc.lang.WhileyFile;
import wyc.task.CompileTask;
import wyc.util.TestUtils;
import wycc.WyMain;
import wycc.cfg.Configuration;
import wycc.cfg.HashMapConfiguration;
import wycc.util.Pair;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.Trie;
import wyil.lang.WyilFile;

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
public class QuickCheckInvalidTests {

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
		// Temporary Removal of Intersections
		IGNORED.put("Intersection_Invalid_1", "#843");
		IGNORED.put("Intersection_Invalid_2", "#843");
		IGNORED.put("NegationType_Invalid_2", "#843");
		IGNORED.put("Type_Invalid_6", "#843");
		IGNORED.put("Type_Invalid_9", "#843");
		//
		IGNORED.put("StaticVar_Invalid_10", "830");
		IGNORED.put("Type_Invalid_12", "830");
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
		// #885 --- Contractive Types and isVoid()
		IGNORED.put("Type_Invalid_5", "885");
		IGNORED.put("Type_Invalid_7", "885");
		IGNORED.put("Type_Invalid_8", "885");
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

		Pair<Boolean, String> p = compile(whileySrcDir, // location of source directory
				name); // name of test to compile

		boolean r = p.first();
		String output = p.second();
		// Now, let's check the expected output against the file which
		// contains the sample output for this test
		String sampleOutputFile = WHILEY_SRC_DIR + File.separatorChar + name
				+ ".sysout";

		// Third, compare the output!
//		if(!TestUtils.compare(output,sampleOutputFile)) {
//			fail("Output does not match reference");
//		} else
		if(r) {
			TestUtils.compare(output,sampleOutputFile);
			fail("QuickCheck failed to identify problem");
		}
	}

	/**
 	 * A simple default registry which knows about whiley files and wyil files.
 	 */
 	private static final Content.Registry registry = new TestUtils.Registry();

	public static void buildProject(Build.Project project) throws Throwable {
		try {
			// Refresh project
			project.refresh();
			// Actually force the project to build
			project.build(ForkJoinPool.commonPool()).get();
		} catch (ExecutionException e) {
			// FIXME: this is a complete kludge to handle the workaround for
			// VerificationCheck. This currently uses the old theorem prover which forceably
			// throws exceptions (yuk)
			Throwable cause = e;
			while (cause.getCause() != null) {
				cause = cause.getCause();
			}
			throw cause;
		}
	}

	public static Pair<Boolean,String> compile(File whileydir, String arg) throws IOException {
		ByteArrayOutputStream syserr = new ByteArrayOutputStream();
		ByteArrayOutputStream sysout = new ByteArrayOutputStream();
		PrintStream psyserr = new PrintStream(syserr);
		//
		boolean result = true;
		//
		try {
			// Construct the project
			DirectoryRoot root = new DirectoryRoot(whileydir, registry);
			SequentialBuildProject project = new SequentialBuildProject(root);
			// Identify source files and target files
			Pair<Path.Entry<WhileyFile>,Path.Entry<WyilFile>> p = TestUtils.findSourceFiles(root,arg);
			List<Path.Entry<WhileyFile>> sources = Arrays.asList(p.first());
			Path.Entry<WyilFile> wyilTarget = p.second();
			// Add Whiley => WyIL build rule
			project.add(new Build.Rule() {
				@Override
				public void apply(Collection<Build.Task> tasks) throws IOException {
					// Construct a new build task
					CompileTask task = new CompileTask(project, root, wyilTarget, sources);
					// Submit the task for execution
					tasks.add(task);
				}
			});
			buildProject(project);
			// Flush any created resources (e.g. wyil files)
			root.flush();
			// Check whether any syntax error produced
			result = !TestUtils.findSyntaxErrors(wyilTarget.read().getRootItem(), new BitSet());
			// Run quick check
			if(!result) {
				// NOTE: getting here indicates some kind of a regular kind of error was
				// detected (e.g. type error, problem of definite assignment, etc). In
				// other words, this was not a verification error.
			} else {
				// NOTE: this indicates everything passed so far. The question then is whether
				// or not QuickCheck can detect a problem.
				new QuickCheck(project, null, System.out, System.err).check(wyilTarget.read(), QuickCheck.DEFAULT_CONTEXT);
				// Recheck whether any syntax errors produced
				result = !TestUtils.findSyntaxErrors(wyilTarget.read().getRootItem(), new BitSet());
			}
			// Print out any error messages
			wycc.commands.Build.printSyntacticMarkers(psyserr, (List) sources, (Path.Entry) wyilTarget);
			// Flush any created resources (e.g. wyil files)
			project.getRoot().flush();
		} catch (SyntacticException e) {
			// Print out the syntax error
			e.outputSourceError(new PrintStream(syserr),false);
			result = false;
		} catch (Throwable e) {
			// Internal failure
			e.printStackTrace(new PrintStream(syserr));
		}
		// Convert bytes produced into resulting string.
		byte[] errBytes = syserr.toByteArray();
		byte[] outBytes = sysout.toByteArray();
		String output = new String(errBytes) + new String(outBytes);
		return new Pair<>(result, output);
	}

	// ======================================================================
	// Tests
	// ======================================================================

	// Parameter to test case is the name of the current test.
	// It will be passed to the constructor by JUnit.
	private final String testName;
	public QuickCheckInvalidTests(String testName) {
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
