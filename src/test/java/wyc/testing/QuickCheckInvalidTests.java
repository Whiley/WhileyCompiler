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

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wybs.lang.Build;
import wybs.lang.SyntacticException;
import wybs.util.SequentialBuildProject;
import wyc.cmd.QuickCheck;
import wyc.lang.WhileyFile;
import wyc.task.CompileTask;
import wyc.util.TestUtils;
import wycc.util.Pair;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
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
//		IGNORED.put("Type_Invalid_10", "823");
		// Temporary Removal of Intersections
//		IGNORED.put("Intersection_Invalid_1", "#843");
//		IGNORED.put("Intersection_Invalid_2", "#843");
//		IGNORED.put("NegationType_Invalid_2", "#843");
//		IGNORED.put("Type_Invalid_6", "#843");
//		IGNORED.put("Type_Invalid_9", "#843");
		//
//		IGNORED.put("StaticVar_Invalid_10", "830");
//		IGNORED.put("Type_Invalid_12", "830");
		// Problems with counterexample generation?
//		IGNORED.put("DoWhile_Invalid_6", "??");
//		IGNORED.put("DoWhile_Invalid_8", "??");
//		IGNORED.put("While_Invalid_18", "??");
//		IGNORED.put("While_Invalid_20", "??");
//		IGNORED.put("While_Invalid_21", "??");
//		IGNORED.put("While_Invalid_22", "??");
//		IGNORED.put("While_Invalid_23", "??");
//		IGNORED.put("TupleAssign_Invalid_3", "??");
//		IGNORED.put("TypeEquals_Invalid_5", "??");
		// #885 --- Contractive Types and isVoid()
//		IGNORED.put("Type_Invalid_5", "885");
//		IGNORED.put("Type_Invalid_7", "885");
//		IGNORED.put("Type_Invalid_8", "885");
		// ===============================================================
		// Whiley Theorem Prover faults
		// ===============================================================
//		IGNORED.put("RecursiveType_Invalid_9", "unclassified");
//		IGNORED.put("RecursiveType_Invalid_2", "WyTP#26");

		// ===============================================================
		// Irrelevant Tests
		// ===============================================================

		// Parser Errors
		IGNORED.put("AddressExpression_Invalid_2", "unknown type encountered");
		IGNORED.put("Continue_Invalid_1", "continue outside loop");
		IGNORED.put("EndOfFile_Invalid_1", "unexpected end-of-file");
		IGNORED.put("FunctionRef_Invalid_1", "expecting \":\" here");
		IGNORED.put("FunctionRef_Invalid_2", "expecting \":\" here");
		IGNORED.put("FunctionRef_Invalid_3", "expecting \":\" here");
		IGNORED.put("Intersection_Invalid_1", "expecting \"Identifier\" here");
		IGNORED.put("Intersection_Invalid_2", "expected end-of-line");
		IGNORED.put("Lifetime_Invalid_3", "use of undeclared lifetime");
		IGNORED.put("Lifetime_Lambda_Invalid_4", "use of undeclared lifetime");
		IGNORED.put("NegationType_Invalid_2", "expecting \"Identifier\" here");
		IGNORED.put("OpenRecord_Invalid_1", "expecting \":\" here");
		IGNORED.put("Parameter_Invalid_1", "name already declared");
		IGNORED.put("Parameter_Invalid_2", "unknown token encountered");
		IGNORED.put("Parsing_Invalid_10", "break outside switch or loop");
		IGNORED.put("Parsing_Invalid_11", "continue outside loop");
		IGNORED.put("Parsing_Invalid_12", "duplicate case label");
		IGNORED.put("Parsing_Invalid_16", "duplicate record key");
		IGNORED.put("Parsing_Invalid_4", "duplicate record key");
		IGNORED.put("Parsing_Invalid_5", "name already declared");
		IGNORED.put("Parsing_Invalid_6", "name already declared");
		IGNORED.put("Property_Invalid_5", "expecting \":\" here");
		IGNORED.put("Property_Invalid_6", "expecting \":\" here");
		IGNORED.put("Property_Invalid_7", "expecting \":\" here");
		IGNORED.put("StaticVar_Invalid_10", "expecting \")\" here");
		IGNORED.put("Switch_Invalid_1", "duplicate case label");
		IGNORED.put("Switch_Invalid_2", "unreachable code");
		IGNORED.put("Switch_Invalid_3", "duplicate default label");
		IGNORED.put("Switch_Invalid_4", "duplicate case label");
		IGNORED.put("Switch_Invalid_5", "break outside switch or loop");
		IGNORED.put("Switch_Invalid_7", "unrecognised term");
		IGNORED.put("Template_Invalid_1", "missing type variable(s)");
		IGNORED.put("Template_Invalid_2", "expecting \"Identifier\" here");
		IGNORED.put("Template_Invalid_22", "use of undeclared lifetime");
		IGNORED.put("Template_Invalid_26", "use of undeclared lifetime");
		IGNORED.put("Template_Invalid_3", "name already declared");
		IGNORED.put("Template_Invalid_4", "expecting \"=\" here");
		IGNORED.put("Type_Invalid_12", "expecting \")\" here");
		IGNORED.put("Type_Invalid_6", "expected end-of-line");
		IGNORED.put("Type_Invalid_9", "expected end-of-line");
		// Other Errors
		IGNORED.put("AddressExpression_Invalid_1", "not relevant");
		IGNORED.put("ArrayAccess_Invalid_1", "not relevant");
		IGNORED.put("ArrayAccess_Invalid_3", "not relevant");
		IGNORED.put("ArrayAssign_Invalid_1", "not relevant");
		IGNORED.put("ArrayConversion_Invalid_1", "not relevant");
		IGNORED.put("ArrayEmpty_Invalid_1", "not relevant");
		IGNORED.put("ArrayGenerator_Invalid_3", "not relevant");
		IGNORED.put("ArrayGenerator_Invalid_4", "not relevant");
		IGNORED.put("Array_Invalid_1", "not relevant");
		IGNORED.put("Array_Invalid_2", "not relevant");
		IGNORED.put("Array_Invalid_3", "not relevant");
		IGNORED.put("Array_Invalid_4", "not relevant");
		IGNORED.put("Array_Invalid_5", "not relevant");
		IGNORED.put("Array_Invalid_6", "not relevant");
		IGNORED.put("Array_Invalid_7", "not relevant");
		IGNORED.put("Assert_Invalid_1", "not relevant");
		IGNORED.put("Assert_Invalid_2", "not relevant");
		IGNORED.put("Assign_Invalid_1", "not relevant");
		IGNORED.put("Assign_Invalid_2", "not relevant");
		IGNORED.put("Assign_Invalid_3", "not relevant");
		IGNORED.put("Assign_Invalid_4", "not relevant");
		IGNORED.put("Assign_Invalid_5", "not relevant");
		IGNORED.put("Assign_Invalid_6", "not relevant");
		IGNORED.put("Assign_Invalid_7", "not relevant");
		IGNORED.put("Assign_Invalid_8", "not relevant");
		IGNORED.put("Assign_Invalid_9", "not relevant");
		IGNORED.put("Byte_Invalid_9", "not relevant");
		IGNORED.put("Byte_Invalid_1", "not relevant");
		IGNORED.put("Coercion_Invalid_1", "not relevant");
		IGNORED.put("Coercion_Invalid_10", "not relevant");
		IGNORED.put("Coercion_Invalid_11", "not relevant");
		IGNORED.put("Coercion_Invalid_2", "not relevant");
		IGNORED.put("Coercion_Invalid_3", "not relevant");
		IGNORED.put("Coercion_Invalid_4", "not relevant");
		IGNORED.put("Coercion_Invalid_5", "not relevant");
		IGNORED.put("Coercion_Invalid_6", "not relevant");
		IGNORED.put("Coercion_Invalid_7", "not relevant");
		IGNORED.put("Coercion_Invalid_8", "not relevant");
		IGNORED.put("Coercion_Invalid_9", "not relevant");
		IGNORED.put("Constant_Invalid_1", "not relevant");
		IGNORED.put("Constant_Invalid_2", "not relevant");
		IGNORED.put("ConstrainedInt_Invalid_11", "not relevant");
		IGNORED.put("Continue_Invalid_2", "not relevant");
		IGNORED.put("DefiniteAssign_Invalid_1", "not relevant");
		IGNORED.put("DefiniteAssign_Invalid_2", "not relevant");
		IGNORED.put("DefiniteAssign_Invalid_3", "not relevant");
		IGNORED.put("DefiniteAssign_Invalid_4", "not relevant");
		IGNORED.put("DoWhile_Invalid_5", "not relevant");
		IGNORED.put("Ensures_Invalid_2", "not relevant");
		IGNORED.put("Fail_Invalid_2", "not relevant");
		IGNORED.put("Final_Invalid_1", "not relevant");
		IGNORED.put("Final_Invalid_10", "not relevant");
		IGNORED.put("Final_Invalid_11", "not relevant");
		IGNORED.put("Final_Invalid_12", "not relevant");
		IGNORED.put("Final_Invalid_13", "not relevant");
		IGNORED.put("Final_Invalid_14", "not relevant");
		IGNORED.put("Final_Invalid_15", "not relevant");
		IGNORED.put("Final_Invalid_2", "not relevant");
		IGNORED.put("Final_Invalid_3", "not relevant");
		IGNORED.put("Final_Invalid_4", "not relevant");
		IGNORED.put("Final_Invalid_5", "not relevant");
		IGNORED.put("Final_Invalid_6", "not relevant");
		IGNORED.put("Final_Invalid_7", "not relevant");
		IGNORED.put("Final_Invalid_8", "not relevant");
		IGNORED.put("Final_Invalid_9", "not relevant");
		IGNORED.put("FunctionRef_Invalid_4", "not relevant");
		IGNORED.put("FunctionRef_Invalid_5", "not relevant");
		IGNORED.put("FunctionRef_Invalid_6", "not relevant");
		IGNORED.put("FunctionRef_Invalid_7", "not relevant");
		IGNORED.put("Function_Invalid_1", "not relevant");
		IGNORED.put("Function_Invalid_10", "not relevant");
		IGNORED.put("Function_Invalid_11", "not relevant");
		IGNORED.put("Function_Invalid_12", "not relevant");
		IGNORED.put("Function_Invalid_13", "not relevant");
		IGNORED.put("Function_Invalid_3", "not relevant");
		IGNORED.put("Function_Invalid_4", "not relevant");
		IGNORED.put("If_Invalid_1", "not relevant");
		IGNORED.put("If_Invalid_2", "not relevant");
		IGNORED.put("If_Invalid_4", "not relevant");
		IGNORED.put("If_Invalid_5", "not relevant");
		IGNORED.put("Import_Invalid_1", "not relevant");
		IGNORED.put("Lifetime_Invalid_1", "not relevant");
		IGNORED.put("Lifetime_Invalid_2", "not relevant");
		IGNORED.put("Lifetime_Invalid_4", "not relevant");
		IGNORED.put("Lifetime_Invalid_5", "not relevant");
		IGNORED.put("Lifetime_Invalid_6", "not relevant");
		IGNORED.put("Lifetime_Invalid_7", "not relevant");
		IGNORED.put("Lifetime_Invalid_8", "not relevant");
		IGNORED.put("Lifetime_Invalid_9", "not relevant");
		IGNORED.put("Lifetime_Lambda_Invalid_1", "not relevant");
		IGNORED.put("Lifetime_Lambda_Invalid_2", "not relevant");
		IGNORED.put("MethodCall_Invalid_1", "not relevant");
		IGNORED.put("MethodCall_Invalid_2", "not relevant");
		IGNORED.put("MethodCall_Invalid_3", "not relevant");
		IGNORED.put("MethodCall_Invalid_4", "not relevant");
		IGNORED.put("MethodCall_Invalid_5", "not relevant");
		IGNORED.put("MethodCall_Invalid_6", "not relevant");
		IGNORED.put("MethodCall_Invalid_7", "not relevant");
		IGNORED.put("MethodCall_Invalid_8", "not relevant");
		IGNORED.put("MethodRef_Invalid_1", "not relevant");
		IGNORED.put("MethodRef_Invalid_2", "not relevant");
		IGNORED.put("MethodRef_Invalid_3", "not relevant");
		IGNORED.put("Method_Invalid_9", "not relevant");
		IGNORED.put("MultiAssign_Invalid_1", "not relevant");
		IGNORED.put("NegationType_Invalid_1", "not relevant");
		IGNORED.put("NegationType_Invalid_3", "not relevant");
		IGNORED.put("NegationType_Invalid_4", "not relevant");
		IGNORED.put("OpenRecord_Invalid_2", "not relevant");
		IGNORED.put("OpenRecord_Invalid_3", "not relevant");
		IGNORED.put("OpenRecord_Invalid_4", "not relevant");
		IGNORED.put("OpenRecord_Invalid_5", "not relevant");
		IGNORED.put("OpenRecord_Invalid_6", "not relevant");
		IGNORED.put("OpenRecord_Invalid_7", "not relevant");
		IGNORED.put("Parsing_Invalid_21", "not relevant");
		IGNORED.put("Parsing_Invalid_23", "not relevant");
		IGNORED.put("Parsing_Invalid_24", "not relevant");
		IGNORED.put("Parsing_Invalid_25", "not relevant");
		IGNORED.put("Parsing_Invalid_26", "not relevant");
		IGNORED.put("Parsing_Invalid_29", "not relevant");
		IGNORED.put("Parsing_Invalid_30", "not relevant");
		IGNORED.put("Parsing_Invalid_31", "not relevant");
		IGNORED.put("Parsing_Invalid_8", "not relevant");
		IGNORED.put("ProcessAccess_Invalid_1", "not relevant");
		IGNORED.put("ProcessAccess_Invalid_2", "not relevant");
		IGNORED.put("ProcessAccess_Invalid_3", "not relevant");
		IGNORED.put("Process_Invalid_1", "not relevant");
		IGNORED.put("Property_Invalid_1", "not relevant");
		IGNORED.put("Record_Invalid_1", "not relevant");
		IGNORED.put("Record_Invalid_2", "not relevant");
		IGNORED.put("Record_Invalid_3", "not relevant");
		IGNORED.put("Record_Invalid_4", "not relevant");
		IGNORED.put("Record_Invalid_5", "not relevant");
		IGNORED.put("Record_Invalid_6", "not relevant");
		IGNORED.put("Record_Invalid_7", "not relevant");
		IGNORED.put("RecursiveType_Invalid_11", "not relevant");
		IGNORED.put("RecursiveType_Invalid_3", "not relevant");
		IGNORED.put("RecursiveType_Invalid_5", "not relevant");
		IGNORED.put("Reference_Invalid_1", "not relevant");
		IGNORED.put("Reference_Subtype_Variant_Invalid_1", "not relevant");
		IGNORED.put("Remainder_Invalid_2", "not relevant");
		IGNORED.put("Remainder_Invalid_3", "not relevant");
		IGNORED.put("Return_Invalid_1", "not relevant");
		IGNORED.put("Return_Invalid_10", "not relevant");
		IGNORED.put("Return_Invalid_11", "not relevant");
		IGNORED.put("Return_Invalid_12", "not relevant");
		IGNORED.put("Return_Invalid_2", "not relevant");
		IGNORED.put("Return_Invalid_3", "not relevant");
		IGNORED.put("Return_Invalid_4", "not relevant");
		IGNORED.put("Return_Invalid_5", "not relevant");
		IGNORED.put("Return_Invalid_6", "not relevant");
		IGNORED.put("Return_Invalid_7", "not relevant");
		IGNORED.put("Return_Invalid_8", "not relevant");
		IGNORED.put("Return_Invalid_9", "not relevant");
		IGNORED.put("StaticVar_Invalid_1", "not relevant");
		IGNORED.put("StaticVar_Invalid_2", "not relevant");
		IGNORED.put("StaticVar_Invalid_3", "not relevant");
		IGNORED.put("StaticVar_Invalid_4", "not relevant");
		IGNORED.put("StaticVar_Invalid_6", "not relevant");
		IGNORED.put("StaticVar_Invalid_7", "not relevant");
		IGNORED.put("StaticVar_Invalid_8", "not relevant");
		IGNORED.put("StaticVar_Invalid_9", "not relevant");
		IGNORED.put("Switch_Invalid_6", "not relevant");
		IGNORED.put("Template_Invalid_10", "not relevant");
		IGNORED.put("Template_Invalid_11", "not relevant");
		IGNORED.put("Template_Invalid_12", "not relevant");
		IGNORED.put("Template_Invalid_13", "not relevant");
		IGNORED.put("Template_Invalid_14", "not relevant");
		IGNORED.put("Template_Invalid_15", "not relevant");
		IGNORED.put("Template_Invalid_16", "not relevant");
		IGNORED.put("Template_Invalid_17", "not relevant");
		IGNORED.put("Template_Invalid_19", "not relevant");
		IGNORED.put("Template_Invalid_20", "not relevant");
		IGNORED.put("Template_Invalid_21", "not relevant");
		IGNORED.put("Template_Invalid_23", "not relevant");
		IGNORED.put("Template_Invalid_24", "not relevant");
		IGNORED.put("Template_Invalid_25", "not relevant");
		IGNORED.put("Template_Invalid_27", "not relevant");
		IGNORED.put("Template_Invalid_28", "not relevant");
		IGNORED.put("Template_Invalid_5", "not relevant");
		IGNORED.put("Template_Invalid_6", "not relevant");
		IGNORED.put("Template_Invalid_7", "not relevant");
		IGNORED.put("Template_Invalid_8", "not relevant");
		IGNORED.put("Template_Invalid_9", "not relevant");
		IGNORED.put("TupleDefine_Invalid_1", "not relevant");
		IGNORED.put("Tuple_Invalid_6", "not relevant");
		IGNORED.put("Tuple_Invalid_7", "not relevant");
		IGNORED.put("TypeEquals_Invalid_1", "not relevant");
		IGNORED.put("TypeEquals_Invalid_10", "not relevant");
		IGNORED.put("TypeEquals_Invalid_11", "not relevant");
		IGNORED.put("TypeEquals_Invalid_13", "not relevant");
		IGNORED.put("TypeEquals_Invalid_2", "not relevant");
		IGNORED.put("TypeEquals_Invalid_7", "not relevant");
		IGNORED.put("TypeEquals_Invalid_8", "not relevant");
		IGNORED.put("TypeEquals_Invalid_9", "not relevant");
		IGNORED.put("Type_Invalid_1", "not relevant");
		IGNORED.put("Type_Invalid_15", "not relevant");
		IGNORED.put("Type_Invalid_2", "not relevant");
		IGNORED.put("Type_Invalid_3", "not relevant");
		IGNORED.put("Type_Invalid_4", "not relevant");
		IGNORED.put("UnionType_Invalid_1", "not relevant");
		IGNORED.put("UnionType_Invalid_11", "not relevant");
		IGNORED.put("UnionType_Invalid_12", "not relevant");
		IGNORED.put("UnionType_Invalid_13", "not relevant");
		IGNORED.put("UnionType_Invalid_14", "not relevant");
		IGNORED.put("UnionType_Invalid_15", "not relevant");
		IGNORED.put("UnionType_Invalid_2", "not relevant");
		IGNORED.put("UnionType_Invalid_3", "not relevant");
		IGNORED.put("UnionType_Invalid_4", "not relevant");
		IGNORED.put("UnionType_Invalid_5", "not relevant");
		IGNORED.put("UnionType_Invalid_6", "not relevant");
		IGNORED.put("VarDecl_Invalid_1", "not relevant");
		IGNORED.put("VarDecl_Invalid_3", "not relevant");
		IGNORED.put("While_Invalid_1", "not relevant");
		IGNORED.put("While_Invalid_14", "not relevant");
		IGNORED.put("While_Invalid_16", "not relevant");
		IGNORED.put("While_Invalid_2", "not relevant");
		IGNORED.put("While_Invalid_3", "not relevant");
		IGNORED.put("While_Invalid_4", "not relevant");
		IGNORED.put("While_Invalid_5", "not relevant");
		IGNORED.put("While_Invalid_6", "not relevant");
		IGNORED.put("While_Invalid_9", "not relevant");
		IGNORED.put("XOR_Invalid_1", "not relevant");
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
			//TestUtils.compare(output,sampleOutputFile);
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
		boolean relevant = true;
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
				relevant = false;
				System.out.println("IGNORED.put(\"" + arg + "\", \"not relevant\");");
				throw new RuntimeException("not relevant");
			} else {
				// NOTE: this indicates everything passed so far. The question then is whether
				// or not QuickCheck can detect a problem.
//				QuickCheck.Context context = QuickCheck.DEFAULT_CONTEXT;
				QuickCheck.Context smallContext = new QuickCheck.Context(-1,1,1,1,1,1,true);
				QuickCheck.Context mediumContext = new QuickCheck.Context(-2,2,2,2,2,2,true);
				QuickCheck.Context largeContext = new QuickCheck.Context(-3,3,3,3,3,3,true);
				new QuickCheck(project, null, System.out, System.err).check(wyilTarget.read(), largeContext);
				// Recheck whether any syntax errors produced
				result = !TestUtils.findSyntaxErrors(wyilTarget.read().getRootItem(), new BitSet());
			}
			// Print out any error messages
			wycc.commands.Build.printSyntacticMarkers(psyserr, (List) sources, (Path.Entry) wyilTarget);
			// Flush any created resources (e.g. wyil files)
			project.getRoot().flush();
		} catch (SyntacticException e) {
			// Print out the syntax error
			e.printStackTrace();
			System.out.println("IGNORED.put(\"" + arg + "\", \"" + e.getMessage().replace("\"", "\\\"") + "\");");
			e.outputSourceError(new PrintStream(syserr),false);
			result = false;
			throw new RuntimeException();
		} catch (Throwable e) {
			if(e instanceof RuntimeException && !relevant) {
				throw (RuntimeException) e;
			}
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
