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

package wyc.testing;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.*;

import wybs.lang.Build;
import wybs.util.StdProject;
import wyc.WycMain;
import wyc.util.WycBuildTask;
import wycc.util.Pair;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.Trie;
import wyil.Main.Registry;

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
public class AllValidTests {

	/**
	 * The directory containing the source files for each test case. Every test
	 * corresponds to a file in this directory.
	 */
	public final static String WHILEY_SRC_DIR = "../../tests/valid".replace('/', File.separatorChar);

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
	protected void runTest(String testName) throws IOException {
		// this will need to turn on verification at some point.
		String whileyFilename = WHILEY_SRC_DIR + File.separatorChar + testName
				+ ".whiley";

		Pair<Integer,String> p = TestUtils.compile(
				"-wd", WHILEY_SRC_DIR,      // location of source directory
				"-wp", WYRT_PATH,           // add wyrt to whileypath
				//"-verify",                  // enable verification
				whileyFilename);                      // name of test to compile

		int r = p.first();

		System.out.print(p.second());

		if (r != WycMain.SUCCESS) {
			fail("Test failed to compile!");
		} else if (r == WycMain.INTERNAL_FAILURE) {
			fail("Test caused internal failure!");
		}

		// Execute the compile WyIL file
		TestUtils.execWyil(WHILEY_SRC_DIR, Trie.fromString(testName));
	}

	// ======================================================================
	// Tests
	// ======================================================================

	@Test
	public void Access_Valid_1() throws IOException {
		runTest("Access_Valid_1");
	}

	@Test
	public void Access_Valid_2() throws IOException {
		runTest("Access_Valid_2");
	}

	@Test
	public void Assume_Valid_1() throws IOException {
		runTest("Assume_Valid_1");
	}

	@Test
	public void Assume_Valid_2() throws IOException {
		runTest("Assume_Valid_2");
	}

	@Test
	public void BoolAssign_Valid_1() throws IOException {
		runTest("BoolAssign_Valid_1");
	}

	@Test
	public void BoolAssign_Valid_2() throws IOException {
		runTest("BoolAssign_Valid_2");
	}

	@Test
	public void BoolAssign_Valid_3() throws IOException {
		runTest("BoolAssign_Valid_3");
	}

	@Test
	public void BoolAssign_Valid_4() throws IOException {
		runTest("BoolAssign_Valid_4");
	}

	@Test
	public void BoolAssign_Valid_5() throws IOException {
		runTest("BoolAssign_Valid_5");
	}

	@Test
	public void BoolAssign_Valid_6() throws IOException {
		runTest("BoolAssign_Valid_6");
	}

	@Test
	public void BoolFun_Valid_1() throws IOException {
		runTest("BoolFun_Valid_1");
	}

	@Test
	public void BoolIfElse_Valid_1() throws IOException {
		runTest("BoolIfElse_Valid_1");
	}

	@Test
	public void BoolIfElse_Valid_2() throws IOException {
		runTest("BoolIfElse_Valid_2");
	}

	@Test
	public void BoolList_Valid_1() throws IOException {
		runTest("BoolList_Valid_1");
	}

	@Test
	public void BoolList_Valid_2() throws IOException {
		runTest("BoolList_Valid_2");
	}

	@Test
	public void BoolList_Valid_3() throws IOException {
		runTest("BoolList_Valid_3");
	}

	@Test
	public void BoolRecord_Valid_1() throws IOException {
		runTest("BoolRecord_Valid_1");
	}

	@Test
	public void BoolRecord_Valid_2() throws IOException {
		runTest("BoolRecord_Valid_2");
	}

	@Test
	public void BoolRequires_Valid_1() throws IOException {
		runTest("BoolRequires_Valid_1");
	}

	@Test
	public void BoolReturn_Valid_1() throws IOException {
		runTest("BoolReturn_Valid_1");
	}

	@Test
	public void Byte_Valid_1() throws IOException {
		runTest("Byte_Valid_1");
	}

	@Test
	public void Byte_Valid_2() throws IOException {
		runTest("Byte_Valid_2");
	}

	@Test
	public void Byte_Valid_3() throws IOException {
		runTest("Byte_Valid_3");
	}

	@Test
	public void Byte_Valid_4() throws IOException {
		runTest("Byte_Valid_4");
	}

	@Test
	public void Byte_Valid_5() throws IOException {
		runTest("Byte_Valid_5");
	}

	@Test
	public void Byte_Valid_6() throws IOException {
		runTest("Byte_Valid_6");
	}

	@Test
	public void Byte_Valid_7() throws IOException {
		runTest("Byte_Valid_7");
	}

	@Test
	public void Byte_Valid_8() throws IOException {
		runTest("Byte_Valid_8");
	}

	@Test
	public void Byte_Valid_9() throws IOException {
		runTest("Byte_Valid_9");
	}

	@Test
	public void Cast_Valid_1() throws IOException {
		runTest("Cast_Valid_1");
	}

	@Test
	public void Cast_Valid_2() throws IOException {
		runTest("Cast_Valid_2");
	}

	@Test
	public void Cast_Valid_3() throws IOException {
		runTest("Cast_Valid_3");
	}

	@Test
	public void Cast_Valid_4() throws IOException {
		runTest("Cast_Valid_4");
	}

	@Test
	public void Cast_Valid_5() throws IOException {
		runTest("Cast_Valid_5");
	}

	@Test
	public void Cast_Valid_6() throws IOException {
		runTest("Cast_Valid_6");
	}

	@Test
	public void Coercion_Valid_1() throws IOException {
		runTest("Coercion_Valid_1");
	}

	@Test
	public void Coercion_Valid_2() throws IOException {
		runTest("Coercion_Valid_2");
	}

	@Test
	public void Coercion_Valid_3() throws IOException {
		runTest("Coercion_Valid_3");
	}

	@Test
	public void Coercion_Valid_7() throws IOException {
		runTest("Coercion_Valid_7");
	}

	@Ignore("#406") @Test
	public void Coercion_Valid_8() throws IOException {
		runTest("Coercion_Valid_8");
	}

	@Test
	public void Coercion_Valid_9() throws IOException {
		runTest("Coercion_Valid_9");
	}

	@Test
	public void Complex_Valid_1() throws IOException {
		runTest("Complex_Valid_1");
	}

	@Test
	public void Complex_Valid_2() throws IOException {
		runTest("Complex_Valid_2");
	}

	@Ignore("Issue ???") @Test
	public void Complex_Valid_3() throws IOException {
		runTest("Complex_Valid_3");
	}

	@Test
	public void Complex_Valid_4() throws IOException {
		runTest("Complex_Valid_4");
	}

	@Test
	public void Complex_Valid_5() throws IOException {
		runTest("Complex_Valid_5");
	}

	@Test
	public void Complex_Valid_6() throws IOException {
		runTest("Complex_Valid_6");
	}

	@Test
	public void Complex_Valid_7() throws IOException {
		runTest("Complex_Valid_7");
	}

	@Test
	public void Complex_Valid_8() throws IOException {
		runTest("Complex_Valid_8");
	}

	@Test
	public void Constant_Valid_3() throws IOException {
		runTest("Constant_Valid_3");
	}

	@Test
	public void ConstrainedInt_Valid_1() throws IOException {
		runTest("ConstrainedInt_Valid_1");
	}

	@Test
	public void ConstrainedInt_Valid_10() throws IOException {
		runTest("ConstrainedInt_Valid_10");
	}

	@Test
	public void ConstrainedInt_Valid_12() throws IOException {
		runTest("ConstrainedInt_Valid_12");
	}

	@Test
	public void ConstrainedInt_Valid_13() throws IOException {
		runTest("ConstrainedInt_Valid_13");
	}

	@Test
	public void ConstrainedInt_Valid_15() throws IOException {
		runTest("ConstrainedInt_Valid_15");
	}

	@Test
	public void ConstrainedInt_Valid_16() throws IOException {
		runTest("ConstrainedInt_Valid_16");
	}

	@Test
	public void ConstrainedInt_Valid_17() throws IOException {
		runTest("ConstrainedInt_Valid_17");
	}

	@Test
	public void ConstrainedInt_Valid_18() throws IOException {
		runTest("ConstrainedInt_Valid_18");
	}

	@Test
	public void ConstrainedInt_Valid_19() throws IOException {
		runTest("ConstrainedInt_Valid_19");
	}

	@Test
	public void ConstrainedInt_Valid_2() throws IOException {
		runTest("ConstrainedInt_Valid_2");
	}

	@Test
	public void ConstrainedInt_Valid_20() throws IOException {
		runTest("ConstrainedInt_Valid_20");
	}

	@Test
	public void ConstrainedInt_Valid_21() throws IOException {
		runTest("ConstrainedInt_Valid_21");
	}

	@Test
	public void ConstrainedInt_Valid_22() throws IOException {
		runTest("ConstrainedInt_Valid_22");
	}

	@Test
	public void ConstrainedInt_Valid_23() throws IOException {
		runTest("ConstrainedInt_Valid_23");
	}

	@Test
	public void ConstrainedInt_Valid_24() throws IOException {
		runTest("ConstrainedInt_Valid_24");
	}

	@Test
	public void ConstrainedInt_Valid_3() throws IOException {
		runTest("ConstrainedInt_Valid_3");
	}

	@Test
	public void ConstrainedInt_Valid_4() throws IOException {
		runTest("ConstrainedInt_Valid_4");
	}

	@Test
	public void ConstrainedInt_Valid_5() throws IOException {
		runTest("ConstrainedInt_Valid_5");
	}

	@Test
	public void ConstrainedInt_Valid_6() throws IOException {
		runTest("ConstrainedInt_Valid_6");
	}

	@Test
	public void ConstrainedInt_Valid_8() throws IOException {
		runTest("ConstrainedInt_Valid_8");
	}

	@Ignore("unknown") @Test
	public void ConstrainedIntersection_Valid_1() throws IOException {
		runTest("ConstrainedIntersection_Valid_1");
	}

	@Test
	public void ConstrainedList_Valid_1() throws IOException {
		runTest("ConstrainedList_Valid_1");
	}

	@Test
	public void ConstrainedList_Valid_11() throws IOException {
		runTest("ConstrainedList_Valid_11");
	}

	@Test
	public void ConstrainedList_Valid_12() throws IOException {
		runTest("ConstrainedList_Valid_12");
	}

	@Test
	public void ConstrainedList_Valid_14() throws IOException {
		runTest("ConstrainedList_Valid_14");
	}

	@Test
	public void ConstrainedList_Valid_15() throws IOException {
		runTest("ConstrainedList_Valid_15");
	}

	@Test
	public void ConstrainedList_Valid_16() throws IOException {
		runTest("ConstrainedList_Valid_16");
	}

	@Test
	public void ConstrainedList_Valid_17() throws IOException {
		runTest("ConstrainedList_Valid_17");
	}

	@Test
	public void ConstrainedList_Valid_18() throws IOException {
		runTest("ConstrainedList_Valid_18");
	}

	@Test
	public void ConstrainedList_Valid_19() throws IOException {
		runTest("ConstrainedList_Valid_19");
	}

	@Test
	public void ConstrainedList_Valid_2() throws IOException {
		runTest("ConstrainedList_Valid_2");
	}

	@Test
	public void ConstrainedList_Valid_20() throws IOException {
		runTest("ConstrainedList_Valid_20");
	}

	@Test
	public void ConstrainedList_Valid_21() throws IOException {
		runTest("ConstrainedList_Valid_21");
	}

	@Test
	public void ConstrainedList_Valid_22() throws IOException {
		runTest("ConstrainedList_Valid_22");
	}

	@Test
	public void ConstrainedList_Valid_23() throws IOException {
		runTest("ConstrainedList_Valid_23");
	}

	@Test
	public void ConstrainedList_Valid_25() throws IOException {
		runTest("ConstrainedList_Valid_25");
	}

	@Test
	public void ConstrainedList_Valid_26() throws IOException {
		runTest("ConstrainedList_Valid_26");
	}

	@Test
	public void ConstrainedList_Valid_27() throws IOException {
		runTest("ConstrainedList_Valid_27");
	}

	@Test
	public void ConstrainedList_Valid_3() throws IOException {
		runTest("ConstrainedList_Valid_3");
	}

	@Test
	public void ConstrainedList_Valid_4() throws IOException {
		runTest("ConstrainedList_Valid_4");
	}

	@Test
	public void ConstrainedList_Valid_5() throws IOException {
		runTest("ConstrainedList_Valid_5");
	}

	@Test
	public void ConstrainedList_Valid_6() throws IOException {
		runTest("ConstrainedList_Valid_6");
	}

	@Test
	public void ConstrainedList_Valid_7() throws IOException {
		runTest("ConstrainedList_Valid_7");
	}

	@Test
	public void ConstrainedList_Valid_8() throws IOException {
		runTest("ConstrainedList_Valid_8");
	}

	@Test
	public void ConstrainedList_Valid_9() throws IOException {
		runTest("ConstrainedList_Valid_9");
	}

	@Ignore("#342") @Test
	public void ConstrainedNegation_Valid_1() throws IOException {
		runTest("ConstrainedNegation_Valid_1");
	}

	@Ignore("#342") @Test
	public void ConstrainedNegation_Valid_2() throws IOException {
		runTest("ConstrainedNegation_Valid_2");
	}

	@Test
	public void ConstrainedRecord_Valid_1() throws IOException {
		runTest("ConstrainedRecord_Valid_1");
	}

	@Test
	public void ConstrainedRecord_Valid_2() throws IOException {
		runTest("ConstrainedRecord_Valid_2");
	}

	@Test
	public void ConstrainedRecord_Valid_3() throws IOException {
		runTest("ConstrainedRecord_Valid_3");
	}

	@Test
	public void ConstrainedRecord_Valid_4() throws IOException {
		runTest("ConstrainedRecord_Valid_4");
	}

	@Test
	public void ConstrainedRecord_Valid_5() throws IOException {
		runTest("ConstrainedRecord_Valid_5");
	}

	@Test
	public void ConstrainedRecord_Valid_6() throws IOException {
		runTest("ConstrainedRecord_Valid_6");
	}

	@Test
	public void ConstrainedRecord_Valid_8() throws IOException {
		runTest("ConstrainedRecord_Valid_8");
	}

	@Test
	public void ConstrainedRecord_Valid_9() throws IOException {
		runTest("ConstrainedRecord_Valid_9");
	}

	@Test
	public void ConstrainedRecord_Valid_10() throws IOException {
		runTest("ConstrainedRecord_Valid_10");
	}

	@Test
	public void ConstrainedReference_Valid_1() throws IOException {
		runTest("ConstrainedReference_Valid_1");
	}

	@Test
	public void ConstrainedUnion_Valid_1() throws IOException {
		runTest("ConstrainedUnion_Valid_1");
	}

	@Test
	public void ConstrainedTuple_Valid_1() throws IOException {
		runTest("ConstrainedTuple_Valid_1");
	}

	@Test
	public void ConstrainedTuple_Valid_2() throws IOException {
		runTest("ConstrainedTuple_Valid_2");
	}

	@Ignore("Issue ???") @Test
	public void Contractive_Valid_1() throws IOException {
		runTest("Contractive_Valid_1");
	}

	@Test
	public void Contractive_Valid_2() throws IOException {
		runTest("Contractive_Valid_2");
	}

	@Test
	public void DecimalAssignment_Valid_1() throws IOException {
		runTest("DecimalAssignment_Valid_1");
	}

	@Test
	public void Define_Valid_1() throws IOException {
		runTest("Define_Valid_1");
	}

	@Test
	public void Define_Valid_2() throws IOException {
		runTest("Define_Valid_2");
	}

	@Test
	public void Define_Valid_3() throws IOException {
		runTest("Define_Valid_3");
	}

	@Test
	public void Define_Valid_4() throws IOException {
		runTest("Define_Valid_4");
	}

	@Test
	public void DoWhile_Valid_1() throws IOException {
		runTest("DoWhile_Valid_1");
	}

	@Test
	public void DoWhile_Valid_2() throws IOException {
		runTest("DoWhile_Valid_2");
	}

	@Test
	public void DoWhile_Valid_3() throws IOException {
		runTest("DoWhile_Valid_3");
	}

	@Ignore("unknown") @Test
	public void DoWhile_Valid_4() throws IOException {
		runTest("DoWhile_Valid_4");
	}

	@Test
	public void DoWhile_Valid_5() throws IOException {
		runTest("DoWhile_Valid_5");
	}

	@Test
	public void DoWhile_Valid_6() throws IOException {
		runTest("DoWhile_Valid_6");
	}

	@Test
	public void EffectiveList_Valid_1() throws IOException {
		runTest("EffectiveList_Valid_1");
	}

	@Test
	public void Ensures_Valid_1() throws IOException {
		runTest("Ensures_Valid_1");
	}

	@Test
	public void Ensures_Valid_2() throws IOException {
		runTest("Ensures_Valid_2");
	}

	@Test
	public void Ensures_Valid_3() throws IOException {
		runTest("Ensures_Valid_3");
	}

	@Test
	public void Ensures_Valid_4() throws IOException {
		runTest("Ensures_Valid_4");
	}

	@Test
	public void Ensures_Valid_5() throws IOException {
		runTest("Ensures_Valid_5");
	}

	@Test
	public void Ensures_Valid_6() throws IOException {
		runTest("Ensures_Valid_6");
	}

	@Test
	public void Ensures_Valid_7() throws IOException {
		runTest("Ensures_Valid_7");
	}

	@Test
	public void Ensures_Valid_8() throws IOException {
		runTest("Ensures_Valid_8");
	}
	
	@Test
	public void FunctionRef_Valid_1() throws IOException {
		runTest("FunctionRef_Valid_1");
	}

	@Ignore("Issue ???") @Test
	public void FunctionRef_Valid_2() throws IOException {
		runTest("FunctionRef_Valid_2");
	}

	@Test
	public void FunctionRef_Valid_3() throws IOException {
		runTest("FunctionRef_Valid_3");
	}

	@Test
	public void FunctionRef_Valid_4() throws IOException {
		runTest("FunctionRef_Valid_4");
	}

	@Test
	public void FunctionRef_Valid_5() throws IOException {
		runTest("FunctionRef_Valid_5");
	}

	@Test
	public void FunctionRef_Valid_6() throws IOException {
		runTest("FunctionRef_Valid_6");
	}

	@Test
	public void FunctionRef_Valid_7() throws IOException {
		runTest("FunctionRef_Valid_7");
	}

	@Test
	public void FunctionRef_Valid_8() throws IOException {
		runTest("FunctionRef_Valid_8");
	}

	@Test
	public void FunctionRef_Valid_9() throws IOException {
		runTest("FunctionRef_Valid_9");
	}

	@Test
	public void Function_Valid_1() throws IOException {
		runTest("Function_Valid_1");
	}

	@Test
	public void Function_Valid_12() throws IOException {
		runTest("Function_Valid_12");
	}

	@Test
	public void Function_Valid_13() throws IOException {
		runTest("Function_Valid_13");
	}

	@Test
	public void Function_Valid_14() throws IOException {
		runTest("Function_Valid_14");
	}

	@Test
	public void Function_Valid_16() throws IOException {
		runTest("Function_Valid_16");
	}

	@Test
	public void Function_Valid_17() throws IOException {
		runTest("Function_Valid_17");
	}

	@Test
	public void Function_Valid_18() throws IOException {
		runTest("Function_Valid_18");
	}

	@Test
	public void Function_Valid_2() throws IOException {
		runTest("Function_Valid_2");
	}

	@Test
	public void Function_Valid_20() throws IOException {
		runTest("Function_Valid_20");
	}

	@Test
	public void Function_Valid_21() throws IOException {
		runTest("Function_Valid_21");
	}

	@Test
	public void Function_Valid_3() throws IOException {
		runTest("Function_Valid_3");
	}

	@Test
	public void Function_Valid_4() throws IOException {
		runTest("Function_Valid_4");
	}

	@Test
	public void Function_Valid_5() throws IOException {
		runTest("Function_Valid_5");
	}

	@Test
	public void Function_Valid_6() throws IOException {
		runTest("Function_Valid_6");
	}

	@Test
	public void Function_Valid_7() throws IOException {
		runTest("Function_Valid_7");
	}

	@Test
	public void Function_Valid_8() throws IOException {
		runTest("Function_Valid_8");
	}

	@Test
	public void Function_Valid_9() throws IOException {
		runTest("Function_Valid_9");
	}

	@Test
	public void HexAssign_Valid_1() throws IOException {
		runTest("HexAssign_Valid_1");
	}

	@Test
	public void IfElse_Valid_1() throws IOException {
		runTest("IfElse_Valid_1");
	}

	@Test
	public void IfElse_Valid_2() throws IOException {
		runTest("IfElse_Valid_2");
	}

	@Test
	public void IfElse_Valid_3() throws IOException {
		runTest("IfElse_Valid_3");
	}

	@Test
	public void IfElse_Valid_4() throws IOException {
		runTest("IfElse_Valid_4");
	}

	@Test
	public void IfElse_Valid_5() throws IOException {
		runTest("IfElse_Valid_5");
	}

	@Test
	public void Import_Valid_1() throws IOException {
		runTest("Import_Valid_1");
	}

	@Test
	public void Import_Valid_2() throws IOException {
		runTest("Import_Valid_2");
	}

	@Test
	public void Import_Valid_3() throws IOException {
		runTest("Import_Valid_3");
	}

	@Ignore("#492") @Test
	public void Import_Valid_4() throws IOException {
		runTest("Import_Valid_4");
	}

	@Ignore("#492") @Test
	public void Import_Valid_5() throws IOException {
		runTest("Import_Valid_5");
	}

	@Test
	public void Import_Valid_6() throws IOException {
		runTest("Import_Valid_6");
	}

	@Test
	public void IntConst_Valid_1() throws IOException {
		runTest("IntConst_Valid_1");
	}

	@Test
	public void IntDefine_Valid_1() throws IOException {
		runTest("IntDefine_Valid_1");
	}

	@Test
	public void IntDefine_Valid_2() throws IOException {
		runTest("IntDefine_Valid_2");
	}

	@Test
	public void IntDiv_Valid_1() throws IOException {
		runTest("IntDiv_Valid_1");
	}

	@Test
	public void IntDiv_Valid_3() throws IOException {
		runTest("IntDiv_Valid_3");
	}

	@Test
	public void IntDiv_Valid_4() throws IOException {
		runTest("IntDiv_Valid_4");
	}

	@Test
	public void IntDiv_Valid_5() throws IOException {
		runTest("IntDiv_Valid_5");
	}

	@Test
	public void IntEquals_Valid_1() throws IOException {
		runTest("IntEquals_Valid_1");
	}

	@Test
	public void IntMul_Valid_1() throws IOException {
		runTest("IntMul_Valid_1");
	}
	@Test
	public void IntMul_Valid_2() throws IOException {
		runTest("IntMul_Valid_2");
	}

	@Test
	public void IntOp_Valid_1() throws IOException {
		runTest("IntOp_Valid_1");
	}

	@Ignore("Issue ???") @Test
	public void Intersection_Valid_1() throws IOException {
		runTest("Intersection_Valid_1");
	}

	@Ignore("Issue ???") @Test
	public void Intersection_Valid_2() throws IOException {
		runTest("Intersection_Valid_2");
	}

	@Test
	public void Lambda_Valid_1() throws IOException {
		runTest("Lambda_Valid_1");
	}

	@Test
	public void Lambda_Valid_2() throws IOException {
		runTest("Lambda_Valid_2");
	}

	@Test
	public void Lambda_Valid_3() throws IOException {
		runTest("Lambda_Valid_3");
	}

	@Test
	public void Lambda_Valid_4() throws IOException {
		runTest("Lambda_Valid_4");
	}

	@Test
	public void Lambda_Valid_5() throws IOException {
		runTest("Lambda_Valid_5");
	}

	@Test
	public void Lambda_Valid_6() throws IOException {
		runTest("Lambda_Valid_6");
	}

	@Test
	public void Lambda_Valid_7() throws IOException {
		runTest("Lambda_Valid_7");
	}

	@Test
	public void Lambda_Valid_8() throws IOException {
		runTest("Lambda_Valid_8");
	}

	@Test
	public void Lambda_Valid_9() throws IOException {
		runTest("Lambda_Valid_9");
	}

	@Test
	public void LengthOf_Valid_1() throws IOException {
		runTest("LengthOf_Valid_1");
	}

	@Test
	public void LengthOf_Valid_4() throws IOException {
		runTest("LengthOf_Valid_4");
	}

	@Test
	public void LengthOf_Valid_5() throws IOException {
		runTest("LengthOf_Valid_5");
	}

	@Test
	public void ListAccess_Valid_1() throws IOException {
		runTest("ListAccess_Valid_1");
	}

	@Test
	public void ListAccess_Valid_3() throws IOException {
		runTest("ListAccess_Valid_3");
	}

	@Test
	public void ListAccess_Valid_5() throws IOException {
		runTest("ListAccess_Valid_5");
	}

	@Ignore("Issue ???") @Test
	public void ListAccess_Valid_6() throws IOException {
		runTest("ListAccess_Valid_6");
	}

	@Ignore("Issue ???") @Test
	public void ListAccess_Valid_7() throws IOException {
		runTest("ListAccess_Valid_7");
	}

	@Test
	public void ListAccess_Valid_8() throws IOException {
		runTest("ListAccess_Valid_8");
	}

	@Test
	public void ListAccess_Valid_9() throws IOException {
		runTest("ListAccess_Valid_9");
	}

	@Test
	public void ListAppend_Valid_1() throws IOException {
		runTest("ListAppend_Valid_1");
	}

	@Test
	public void ListAppend_Valid_11() throws IOException {
		runTest("ListAppend_Valid_11");
	}

	@Test
	public void ListAppend_Valid_13() throws IOException {
		runTest("ListAppend_Valid_13");
	}

	@Test
	public void ListAppend_Valid_14() throws IOException {
		runTest("ListAppend_Valid_14");
	}

	@Test
	public void ListAppend_Valid_2() throws IOException {
		runTest("ListAppend_Valid_2");
	}

	@Test
	public void ListAppend_Valid_3() throws IOException {
		runTest("ListAppend_Valid_3");
	}

	@Test
	public void ListAppend_Valid_4() throws IOException {
		runTest("ListAppend_Valid_4");
	}

	@Test
	public void ListAppend_Valid_5() throws IOException {
		runTest("ListAppend_Valid_5");
	}

	@Test
	public void ListAppend_Valid_6() throws IOException {
		runTest("ListAppend_Valid_6");
	}

	@Test
	public void ListAppend_Valid_7() throws IOException {
		runTest("ListAppend_Valid_7");
	}

	@Test
	public void ListAppend_Valid_8() throws IOException {
		runTest("ListAppend_Valid_8");
	}

	@Test
	public void ListAppend_Valid_9() throws IOException {
		runTest("ListAppend_Valid_9");
	}

	@Test
	public void ListAssign_Valid_1() throws IOException {
		runTest("ListAssign_Valid_1");
	}

	@Test
	public void ListAssign_Valid_11() throws IOException {
		runTest("ListAssign_Valid_11");
	}

	@Test
	public void ListAssign_Valid_2() throws IOException {
		runTest("ListAssign_Valid_2");
	}

	@Test
	public void ListAssign_Valid_3() throws IOException {
		runTest("ListAssign_Valid_3");
	}

	@Test
	public void ListAssign_Valid_4() throws IOException {
		runTest("ListAssign_Valid_4");
	}

	@Test
	public void ListAssign_Valid_5() throws IOException {
		runTest("ListAssign_Valid_5");
	}

	@Test
	public void ListAssign_Valid_6() throws IOException {
		runTest("ListAssign_Valid_6");
	}

	@Test
	public void ListAssign_Valid_7() throws IOException {
		runTest("ListAssign_Valid_7");
	}

	@Test
	public void ListAssign_Valid_8() throws IOException {
		runTest("ListAssign_Valid_8");
	}

	@Test
	public void ListAssign_Valid_9() throws IOException {
		runTest("ListAssign_Valid_9");
	}

	@Test
	public void ListConversion_Valid_1() throws IOException {
		runTest("ListConversion_Valid_1");
	}

	@Test
	public void ListElemOf_Valid_1() throws IOException {
		runTest("ListElemOf_Valid_1");
	}

	@Test
	public void ListEmpty_Valid_1() throws IOException {
		runTest("ListEmpty_Valid_1");
	}

	@Test
	public void ListEquals_Valid_1() throws IOException {
		runTest("ListEquals_Valid_1");
	}

	@Test
	public void ListGenerator_Valid_1() throws IOException {
		runTest("ListGenerator_Valid_1");
	}

	@Test
	public void ListGenerator_Valid_2() throws IOException {
		runTest("ListGenerator_Valid_2");
	}

	@Test
	public void ListGenerator_Valid_3() throws IOException {
		runTest("ListGenerator_Valid_3");
	}

	@Test
	public void ListGenerator_Valid_5() throws IOException {
		runTest("ListGenerator_Valid_5");
	}

	@Test
	public void ListLength_Valid_1() throws IOException {
		runTest("ListLength_Valid_1");
	}

	@Test
	public void ListLength_Valid_2() throws IOException {
		runTest("ListLength_Valid_2");
	}

	@Test
	public void ListLength_Valid_3() throws IOException {
		runTest("ListLength_Valid_3");
	}

	@Test
	public void ListRange_Valid_1() throws IOException {
		runTest("ListRange_Valid_1");
	}

	@Test
	public void ListSublist_Valid_1() throws IOException {
		runTest("ListSublist_Valid_1");
	}

	@Test
	public void ListSublist_Valid_2() throws IOException {
		runTest("ListSublist_Valid_2");
	}

	@Test
	public void ListSublist_Valid_3() throws IOException {
		runTest("ListSublist_Valid_3");
	}

	@Test
	public void ListSublist_Valid_4() throws IOException {
		runTest("ListSublist_Valid_4");
	}

	@Test
	public void ListSublist_Valid_5() throws IOException {
		runTest("ListSublist_Valid_5");
	}

	@Test
	public void MessageRef_Valid_1() throws IOException {
		runTest("MessageRef_Valid_1");
	}

	@Test
	public void MessageRef_Valid_2() throws IOException {
		runTest("MessageRef_Valid_2");
	}

	@Test
	public void MessageSend_Valid_1() throws IOException {
		runTest("MessageSend_Valid_1");
	}

	@Test
	public void MessageSend_Valid_2() throws IOException {
		runTest("MessageSend_Valid_2");
	}

	@Test
	public void MessageSend_Valid_3() throws IOException {
		runTest("MessageSend_Valid_3");
	}

	@Test
	public void MessageSend_Valid_4() throws IOException {
		runTest("MessageSend_Valid_4");
	}

	@Test
	public void MessageSend_Valid_5() throws IOException {
		runTest("MessageSend_Valid_5");
	}

	@Test
	public void MethodCall_Valid_1() throws IOException {
		runTest("MethodCall_Valid_1");
	}

	@Test
	public void MethodCall_Valid_2() throws IOException {
		runTest("MethodCall_Valid_2");
	}

	@Test
	public void MethodCall_Valid_3() throws IOException {
		runTest("MethodCall_Valid_3");
	}

	@Test
	public void MethodCall_Valid_4() throws IOException {
		runTest("MethodCall_Valid_4");
	}

	@Test
	public void MethodRef_Valid_1() throws IOException {
		runTest("MethodRef_Valid_1");
	}

	@Test
	public void MethodRef_Valid_2() throws IOException {
		runTest("MethodRef_Valid_2");
	}

	@Test
	public void Method_Valid_1() throws IOException {
		runTest("Method_Valid_1");
	}

	@Test
	public void MultiLineComment_Valid_1() throws IOException {
		runTest("MultiLineComment_Valid_1");
	}

	@Test
	public void MultiLineComment_Valid_2() throws IOException {
		runTest("MultiLineComment_Valid_2");
	}

	@Test
	public void NegationType_Valid_1() throws IOException {
		runTest("NegationType_Valid_1");
	}

	@Test
	public void NegationType_Valid_2() throws IOException {
		runTest("NegationType_Valid_2");
	}

	@Ignore("Issue ???") @Test
	public void NegationType_Valid_3() throws IOException {
		runTest("NegationType_Valid_3");
	}

	@Test
	public void NegationType_Valid_4() throws IOException {
		runTest("NegationType_Valid_4");
	}

	@Test
	public void OpenRecord_Valid_1() throws IOException {
		runTest("OpenRecord_Valid_1");
	}

	@Test
	public void OpenRecord_Valid_10() throws IOException {
		runTest("OpenRecord_Valid_10");
	}

	@Test
	public void OpenRecord_Valid_2() throws IOException {
		runTest("OpenRecord_Valid_2");
	}

	@Test
	public void OpenRecord_Valid_3() throws IOException {
		runTest("OpenRecord_Valid_3");
	}

	@Test
	public void OpenRecord_Valid_4() throws IOException {
		runTest("OpenRecord_Valid_4");
	}

	@Test
	public void OpenRecord_Valid_5() throws IOException {
		runTest("OpenRecord_Valid_5");
	}

	@Test
	public void OpenRecord_Valid_6() throws IOException {
		runTest("OpenRecord_Valid_6");
	}

	@Test
	public void OpenRecord_Valid_7() throws IOException {
		runTest("OpenRecord_Valid_7");
	}

	@Test
	public void OpenRecord_Valid_8() throws IOException {
		runTest("OpenRecord_Valid_8");
	}

	@Test
	public void OpenRecord_Valid_9() throws IOException {
		runTest("OpenRecord_Valid_9");
	}

	@Test
	public void ProcessAccess_Valid_1() throws IOException {
		runTest("ProcessAccess_Valid_1");
	}

	@Test
	public void ProcessAccess_Valid_2() throws IOException {
		runTest("ProcessAccess_Valid_2");
	}

	@Test
	public void Process_Valid_1() throws IOException {
		runTest("Process_Valid_1");
	}

	@Test
	public void Process_Valid_10() throws IOException {
		runTest("Process_Valid_10");
	}

	@Test
	public void Process_Valid_11() throws IOException {
		runTest("Process_Valid_11");
	}

	@Test
	public void Process_Valid_12() throws IOException {
		runTest("Process_Valid_12");
	}

	@Test
	public void Process_Valid_2() throws IOException {
		runTest("Process_Valid_2");
	}

	@Test
	public void Process_Valid_3() throws IOException {
		runTest("Process_Valid_3");
	}

	@Test
	public void Process_Valid_4() throws IOException {
		runTest("Process_Valid_4");
	}

	@Test
	public void Process_Valid_5() throws IOException {
		runTest("Process_Valid_5");
	}

	@Test
	public void Process_Valid_6() throws IOException {
		runTest("Process_Valid_6");
	}

	@Test
	public void Process_Valid_7() throws IOException {
		runTest("Process_Valid_7");
	}

	@Test
	public void Process_Valid_8() throws IOException {
		runTest("Process_Valid_8");
	}

	@Test
	public void Process_Valid_9() throws IOException {
		runTest("Process_Valid_9");
	}

	@Test
	public void Quantifiers_Valid_1() throws IOException {
		runTest("Quantifiers_Valid_1");
	}

	@Test
	public void Range_Valid_1() throws IOException {
		runTest("Range_Valid_1");
	}

	@Test
	public void RealConst_Valid_1() throws IOException {
		runTest("RealConst_Valid_1");
	}

	@Test
	public void RealDiv_Valid_1() throws IOException {
		runTest("RealDiv_Valid_1");
	}

	@Test
	public void RealDiv_Valid_2() throws IOException {
		runTest("RealDiv_Valid_2");
	}

	@Test
	public void RealDiv_Valid_3() throws IOException {
		runTest("RealDiv_Valid_3");
	}

	@Test
	public void RealDiv_Valid_4() throws IOException {
		runTest("RealDiv_Valid_4");
	}

	@Test
	public void RealDiv_Valid_5() throws IOException {
		runTest("RealDiv_Valid_5");
	}

	@Test
	public void RealDiv_Valid_6() throws IOException {
		runTest("RealDiv_Valid_6");
	}

	@Test
	public void RealDiv_Valid_7() throws IOException {
		runTest("RealDiv_Valid_7");
	}

	@Test
	public void RealNeg_Valid_1() throws IOException {
		runTest("RealNeg_Valid_1");
	}

	@Test
	public void RealNeg_Valid_2() throws IOException {
		runTest("RealNeg_Valid_2");
	}

	@Test
	public void RealSplit_Valid_1() throws IOException {
		runTest("RealSplit_Valid_1");
	}

	@Test
	public void RealSub_Valid_1() throws IOException {
		runTest("RealSub_Valid_1");
	}

	@Test
	public void RealSub_Valid_2() throws IOException {
		runTest("RealSub_Valid_2");
	}

	@Test
	public void RealSub_Valid_3() throws IOException {
		runTest("RealSub_Valid_3");
	}

	@Test
	public void Real_Valid_1() throws IOException {
		runTest("Real_Valid_1");
	}

	@Test
	public void RecordAccess_Valid_1() throws IOException {
		runTest("RecordAccess_Valid_1");
	}

	@Test
	public void RecordAccess_Valid_2() throws IOException {
		runTest("RecordAccess_Valid_2");
	}

	@Test
	public void RecordAssign_Valid_1() throws IOException {
		runTest("RecordAssign_Valid_1");
	}

	@Test
	public void RecordAssign_Valid_10() throws IOException {
		runTest("RecordAssign_Valid_10");
	}

	@Test
	public void RecordAssign_Valid_2() throws IOException {
		runTest("RecordAssign_Valid_2");
	}

	@Test
	public void RecordAssign_Valid_3() throws IOException {
		runTest("RecordAssign_Valid_3");
	}

	@Test
	public void RecordAssign_Valid_4() throws IOException {
		runTest("RecordAssign_Valid_4");
	}

	@Test
	public void RecordAssign_Valid_5() throws IOException {
		runTest("RecordAssign_Valid_5");
	}

	@Test
	public void RecordAssign_Valid_6() throws IOException {
		runTest("RecordAssign_Valid_6");
	}

	@Test
	public void RecordAssign_Valid_7() throws IOException {
		runTest("RecordAssign_Valid_7");
	}

	@Test
	public void RecordAssign_Valid_8() throws IOException {
		runTest("RecordAssign_Valid_8");
	}

	@Test
	public void RecordAssign_Valid_9() throws IOException {
		runTest("RecordAssign_Valid_9");
	}

	@Test
	public void RecordCoercion_Valid_1() throws IOException {
		runTest("RecordCoercion_Valid_1");
	}

	@Test
	public void RecordConversion_Valid_1() throws IOException {
		runTest("RecordConversion_Valid_1");
	}

	@Test
	public void RecordDefine_Valid_1() throws IOException {
		runTest("RecordDefine_Valid_1");
	}

	@Test
	public void RecordDefine_Valid_2() throws IOException {
		runTest("RecordDefine_Valid_2");
	}

	@Ignore("Issue ???") @Test
	public void RecordSubtype_Valid_1() throws IOException {
		runTest("RecordSubtype_Valid_1");
	}

	@Ignore("Issue ???") @Test
	public void RecordSubtype_Valid_2() throws IOException {
		runTest("RecordSubtype_Valid_2");
	}

	@Test
	public void RecursiveType_Valid_1() throws IOException {
		runTest("RecursiveType_Valid_1");
	}

	@Test
	public void RecursiveType_Valid_10() throws IOException {
		runTest("RecursiveType_Valid_10");
	}

	@Test
	public void RecursiveType_Valid_11() throws IOException {
		runTest("RecursiveType_Valid_11");
	}

	@Test
	public void RecursiveType_Valid_12() throws IOException {
		runTest("RecursiveType_Valid_12");
	}

	@Test
	public void RecursiveType_Valid_13() throws IOException {
		runTest("RecursiveType_Valid_13");
	}

	@Test
	public void RecursiveType_Valid_14() throws IOException {
		runTest("RecursiveType_Valid_14");
	}

	@Test
	public void RecursiveType_Valid_15() throws IOException {
		runTest("RecursiveType_Valid_15");
	}

	@Test
	public void RecursiveType_Valid_16() throws IOException {
		runTest("RecursiveType_Valid_16");
	}

	@Test
	public void RecursiveType_Valid_17() throws IOException {
		runTest("RecursiveType_Valid_17");
	}

	@Test
	public void RecursiveType_Valid_18() throws IOException {
		runTest("RecursiveType_Valid_18");
	}

	@Test
	public void RecursiveType_Valid_19() throws IOException {
		runTest("RecursiveType_Valid_19");
	}

	@Test
	public void RecursiveType_Valid_2() throws IOException {
		runTest("RecursiveType_Valid_2");
	}

	@Test
	public void RecursiveType_Valid_20() throws IOException {
		runTest("RecursiveType_Valid_20");
	}

	@Test
	public void RecursiveType_Valid_21() throws IOException {
		runTest("RecursiveType_Valid_21");
	}

	@Test
	public void RecursiveType_Valid_22() throws IOException {
		runTest("RecursiveType_Valid_22");
	}

	@Test
	public void RecursiveType_Valid_23() throws IOException {
		runTest("RecursiveType_Valid_23");
	}

	@Test
	public void RecursiveType_Valid_24() throws IOException {
		runTest("RecursiveType_Valid_24");
	}

	@Test
	public void RecursiveType_Valid_25() throws IOException {
		runTest("RecursiveType_Valid_25");
	}

	@Test
	public void RecursiveType_Valid_26() throws IOException {
		runTest("RecursiveType_Valid_26");
	}

	@Test
	public void RecursiveType_Valid_27() throws IOException {
		runTest("RecursiveType_Valid_27");
	}

	@Ignore("#364") @Test
	public void RecursiveType_Valid_28() throws IOException {
		runTest("RecursiveType_Valid_28");
	}

	@Ignore("#406") @Test
	public void RecursiveType_Valid_3() throws IOException {
		runTest("RecursiveType_Valid_3");
	}

	@Test
	public void RecursiveType_Valid_30() throws IOException {
		runTest("RecursiveType_Valid_30");
	}

	@Ignore("#406") @Test
	public void RecursiveType_Valid_4() throws IOException {
		runTest("RecursiveType_Valid_4");
	}

	@Ignore("#18") @Test
	public void RecursiveType_Valid_5() throws IOException {
		runTest("RecursiveType_Valid_5");
	}

	@Test
	public void RecursiveType_Valid_6() throws IOException {
		runTest("RecursiveType_Valid_6");
	}

	@Test
	public void RecursiveType_Valid_7() throws IOException {
		runTest("RecursiveType_Valid_7");
	}

	@Test
	public void RecursiveType_Valid_8() throws IOException {
		runTest("RecursiveType_Valid_8");
	}

	@Test
	public void RecursiveType_Valid_9() throws IOException {
		runTest("RecursiveType_Valid_9");
	}

	@Test
	public void Reference_Valid_1() throws IOException {
		runTest("Reference_Valid_1");
	}

	@Test
	public void Reference_Valid_2() throws IOException {
		runTest("Reference_Valid_2");
	}

	@Test
	public void Reference_Valid_3() throws IOException {
		runTest("Reference_Valid_3");
	}

	@Test
	public void Reference_Valid_4() throws IOException {
		runTest("Reference_Valid_4");
	}

	@Test
	public void Reference_Valid_5() throws IOException {
		runTest("Reference_Valid_5");
	}

	@Test
	public void Remainder_Valid_1() throws IOException {
		runTest("Remainder_Valid_1");
	}

	@Test
	public void Requires_Valid_1() throws IOException {
		runTest("Requires_Valid_1");
	}

	@Test
	public void Resolution_Valid_1() throws IOException {
		runTest("Resolution_Valid_1");
	}

	@Test
	public void SingleLineComment_Valid_1() throws IOException {
		runTest("SingleLineComment_Valid_1");
	}

	@Test
	public void Skip_Valid_1() throws IOException {
		runTest("Skip_Valid_1");
	}

	@Test
	public void String_Valid_1() throws IOException {
		runTest("String_Valid_1");
	}

	@Test
	public void String_Valid_2() throws IOException {
		runTest("String_Valid_2");
	}

	@Test
	public void String_Valid_3() throws IOException {
		runTest("String_Valid_3");
	}

	@Test
	public void String_Valid_4() throws IOException {
		runTest("String_Valid_4");
	}

	@Test
	public void String_Valid_5() throws IOException {
		runTest("String_Valid_5");
	}

	@Test
	public void String_Valid_6() throws IOException {
		runTest("String_Valid_6");
	}

	@Test
	public void String_Valid_7() throws IOException {
		runTest("String_Valid_7");
	}

	@Test
	public void String_Valid_8() throws IOException {
		runTest("String_Valid_8");
	}

	@Test
	public void Subtype_Valid_1() throws IOException {
		runTest("Subtype_Valid_1");
	}

	@Test
	public void Subtype_Valid_10() throws IOException {
		runTest("Subtype_Valid_10");
	}

	@Test
	public void Subtype_Valid_11() throws IOException {
		runTest("Subtype_Valid_11");
	}

	@Test
	public void Subtype_Valid_12() throws IOException {
		runTest("Subtype_Valid_12");
	}

	@Test
	public void Subtype_Valid_13() throws IOException {
		runTest("Subtype_Valid_13");
	}

	@Test
	public void Subtype_Valid_14() throws IOException {
		runTest("Subtype_Valid_14");
	}

	@Test
	public void Subtype_Valid_2() throws IOException {
		runTest("Subtype_Valid_2");
	}

	@Test
	public void Subtype_Valid_3() throws IOException {
		runTest("Subtype_Valid_3");
	}

	@Test
	public void Subtype_Valid_4() throws IOException {
		runTest("Subtype_Valid_4");
	}

	@Test
	public void Subtype_Valid_5() throws IOException {
		runTest("Subtype_Valid_5");
	}

	@Test
	public void Subtype_Valid_6() throws IOException {
		runTest("Subtype_Valid_6");
	}

	@Test
	public void Subtype_Valid_7() throws IOException {
		runTest("Subtype_Valid_7");
	}

	@Test
	public void Subtype_Valid_8() throws IOException {
		runTest("Subtype_Valid_8");
	}

	@Test
	public void Subtype_Valid_9() throws IOException {
		runTest("Subtype_Valid_9");
	}

	@Test
	public void Switch_Valid_1() throws IOException {
		runTest("Switch_Valid_1");
	}

	@Test
	public void Switch_Valid_10() throws IOException {
		runTest("Switch_Valid_10");
	}

	@Test
	public void Switch_Valid_11() throws IOException {
		runTest("Switch_Valid_11");
	}

	@Test
	public void Switch_Valid_12() throws IOException {
		runTest("Switch_Valid_12");
	}

	@Test
	public void Switch_Valid_13() throws IOException {
		runTest("Switch_Valid_13");
	}

	@Test
	public void Switch_Valid_2() throws IOException {
		runTest("Switch_Valid_2");
	}

	@Test
	public void Switch_Valid_3() throws IOException {
		runTest("Switch_Valid_3");
	}

	@Test
	public void Switch_Valid_4() throws IOException {
		runTest("Switch_Valid_4");
	}

	@Test
	public void Switch_Valid_5() throws IOException {
		runTest("Switch_Valid_5");
	}

	@Test
	public void Switch_Valid_6() throws IOException {
		runTest("Switch_Valid_6");
	}

	@Test
	public void Switch_Valid_7() throws IOException {
		runTest("Switch_Valid_7");
	}

	@Test
	public void Switch_Valid_8() throws IOException {
		runTest("Switch_Valid_8");
	}

	@Test
	public void Switch_Valid_9() throws IOException {
		runTest("Switch_Valid_9");
	}

	@Test
	public void Syntax_Valid_1() throws IOException {
		runTest("Syntax_Valid_1");
	}

	@Test
	public void TupleType_Valid_1() throws IOException {
		runTest("TupleType_Valid_1");
	}

	@Test
	public void TupleType_Valid_2() throws IOException {
		runTest("TupleType_Valid_2");
	}

	@Test
	public void TupleType_Valid_3() throws IOException {
		runTest("TupleType_Valid_3");
	}

	@Test
	public void TupleType_Valid_4() throws IOException {
		runTest("TupleType_Valid_4");
	}

	@Test
	public void TupleType_Valid_5() throws IOException {
		runTest("TupleType_Valid_5");
	}

	@Test
	public void TupleType_Valid_6() throws IOException {
		runTest("TupleType_Valid_6");
	}

	@Test
	public void TupleType_Valid_7() throws IOException {
		runTest("TupleType_Valid_7");
	}

	@Test
	public void TupleType_Valid_8() throws IOException {
		runTest("TupleType_Valid_8");
	}

	@Test
	public void TypeEquals_Valid_1() throws IOException {
		runTest("TypeEquals_Valid_1");
	}

	@Test
	public void TypeEquals_Valid_10() throws IOException {
		runTest("TypeEquals_Valid_10");
	}

	@Test
	public void TypeEquals_Valid_11() throws IOException {
		runTest("TypeEquals_Valid_11");
	}

	@Test
	public void TypeEquals_Valid_12() throws IOException {
		runTest("TypeEquals_Valid_12");
	}

	@Test
	public void TypeEquals_Valid_14() throws IOException {
		runTest("TypeEquals_Valid_14");
	}

	@Test
	public void TypeEquals_Valid_15() throws IOException {
		runTest("TypeEquals_Valid_15");
	}

	@Test
	public void TypeEquals_Valid_16() throws IOException {
		runTest("TypeEquals_Valid_16");
	}

	@Test
	public void TypeEquals_Valid_17() throws IOException {
		runTest("TypeEquals_Valid_17");
	}

	@Test
	public void TypeEquals_Valid_18() throws IOException {
		runTest("TypeEquals_Valid_18");
	}

	@Test
	public void TypeEquals_Valid_19() throws IOException {
		runTest("TypeEquals_Valid_19");
	}

	@Test
	public void TypeEquals_Valid_2() throws IOException {
		runTest("TypeEquals_Valid_2");
	}

	@Test
	public void TypeEquals_Valid_20() throws IOException {
		runTest("TypeEquals_Valid_20");
	}

	@Test
	public void TypeEquals_Valid_21() throws IOException {
		runTest("TypeEquals_Valid_21");
	}

	@Ignore("Issue ???") @Test
	public void TypeEquals_Valid_23() throws IOException {
		runTest("TypeEquals_Valid_23");
	}

	@Test
	public void TypeEquals_Valid_24() throws IOException {
		runTest("TypeEquals_Valid_24");
	}

	@Test
	public void TypeEquals_Valid_25() throws IOException {
		runTest("TypeEquals_Valid_25");
	}

	@Test
	public void TypeEquals_Valid_27() throws IOException {
		runTest("TypeEquals_Valid_27");
	}

	@Test
	public void TypeEquals_Valid_28() throws IOException {
		runTest("TypeEquals_Valid_28");
	}

	@Test
	public void TypeEquals_Valid_29() throws IOException {
		runTest("TypeEquals_Valid_29");
	}

	@Test
	public void TypeEquals_Valid_3() throws IOException {
		runTest("TypeEquals_Valid_3");
	}

	@Test
	public void TypeEquals_Valid_30() throws IOException {
		runTest("TypeEquals_Valid_30");
	}

	@Test
	public void TypeEquals_Valid_31() throws IOException {
		runTest("TypeEquals_Valid_31");
	}

	@Test
	public void TypeEquals_Valid_32() throws IOException {
		runTest("TypeEquals_Valid_32");
	}

	@Test
	public void TypeEquals_Valid_33() throws IOException {
		runTest("TypeEquals_Valid_33");
	}

	@Test
	public void TypeEquals_Valid_34() throws IOException {
		runTest("TypeEquals_Valid_34");
	}

	@Test
	public void TypeEquals_Valid_35() throws IOException {
		runTest("TypeEquals_Valid_35");
	}

	@Ignore("Issue ???") @Test
	public void TypeEquals_Valid_36() throws IOException {
		runTest("TypeEquals_Valid_36");
	}

	@Ignore("Issue ???") @Test
	public void TypeEquals_Valid_37() throws IOException {
		runTest("TypeEquals_Valid_37");
	}

	@Ignore("Issue ???") @Test
	public void TypeEquals_Valid_38() throws IOException {
		runTest("TypeEquals_Valid_38");
	}

	@Test
	public void TypeEquals_Valid_39() throws IOException {
		runTest("TypeEquals_Valid_39");
	}

	@Test
	public void TypeEquals_Valid_40() throws IOException {
		runTest("TypeEquals_Valid_40");
	}

	@Ignore("Issue ???") @Test
	public void TypeEquals_Valid_41() throws IOException {
		runTest("TypeEquals_Valid_41");
	}

	@Test
	public void TypeEquals_Valid_42() throws IOException {
		runTest("TypeEquals_Valid_42");
	}

	@Test
	public void TypeEquals_Valid_43() throws IOException {
		runTest("TypeEquals_Valid_43");
	}

	@Test
	public void TypeEquals_Valid_44() throws IOException {
		runTest("TypeEquals_Valid_44");
	}

	@Test
	public void TypeEquals_Valid_45() throws IOException {
		runTest("TypeEquals_Valid_45");
	}

	@Test
	public void TypeEquals_Valid_46() throws IOException {
		runTest("TypeEquals_Valid_46");
	}

	@Test
	public void TypeEquals_Valid_47() throws IOException {
		runTest("TypeEquals_Valid_47");
	}

	@Test
	public void TypeEquals_Valid_5() throws IOException {
		runTest("TypeEquals_Valid_5");
	}

	@Test
	public void TypeEquals_Valid_6() throws IOException {
		runTest("TypeEquals_Valid_6");
	}

	@Test
	public void TypeEquals_Valid_7() throws IOException {
		runTest("TypeEquals_Valid_7");
	}

	@Test
	public void TypeEquals_Valid_8() throws IOException {
		runTest("TypeEquals_Valid_8");
	}

	@Test
	public void TypeEquals_Valid_9() throws IOException {
		runTest("TypeEquals_Valid_9");
	}

	@Test
	public void UnionType_Valid_1() throws IOException {
		runTest("UnionType_Valid_1");
	}

	@Test
	public void UnionType_Valid_10() throws IOException {
		runTest("UnionType_Valid_10");
	}

	@Test
	public void UnionType_Valid_11() throws IOException {
		runTest("UnionType_Valid_11");
	}

	@Test
	public void UnionType_Valid_12() throws IOException {
		runTest("UnionType_Valid_12");
	}

	@Test
	public void UnionType_Valid_13() throws IOException {
		runTest("UnionType_Valid_13");
	}

	@Test
	public void UnionType_Valid_14() throws IOException {
		runTest("UnionType_Valid_14");
	}

	@Test
	public void UnionType_Valid_15() throws IOException {
		runTest("UnionType_Valid_15");
	}

	@Test
	public void UnionType_Valid_16() throws IOException {
		runTest("UnionType_Valid_16");
	}

	@Test
	public void UnionType_Valid_17() throws IOException {
		runTest("UnionType_Valid_17");
	}

	@Test
	public void UnionType_Valid_18() throws IOException {
		runTest("UnionType_Valid_18");
	}

	@Test
	public void UnionType_Valid_19() throws IOException {
		runTest("UnionType_Valid_19");
	}

	@Test
	public void UnionType_Valid_2() throws IOException {
		runTest("UnionType_Valid_2");
	}

	@Test
	public void UnionType_Valid_20() throws IOException {
		runTest("UnionType_Valid_20");
	}

	@Test
	public void UnionType_Valid_21() throws IOException {
		runTest("UnionType_Valid_21");
	}

	@Test
	public void UnionType_Valid_22() throws IOException {
		runTest("UnionType_Valid_22");
	}

	@Test
	public void UnionType_Valid_23() throws IOException {
		runTest("UnionType_Valid_23");
	}

	@Test
	public void UnionType_Valid_3() throws IOException {
		runTest("UnionType_Valid_3");
	}

	@Test
	public void UnionType_Valid_4() throws IOException {
		runTest("UnionType_Valid_4");
	}

	@Test
	public void UnionType_Valid_5() throws IOException {
		runTest("UnionType_Valid_5");
	}

	@Test
	public void UnionType_Valid_6() throws IOException {
		runTest("UnionType_Valid_6");
	}

	@Test
	public void UnionType_Valid_7() throws IOException {
		runTest("UnionType_Valid_7");
	}

	@Test
	public void UnionType_Valid_8() throws IOException {
		runTest("UnionType_Valid_8");
	}

	@Test
	public void UnionType_Valid_9() throws IOException {
		runTest("UnionType_Valid_9");
	}

	@Test
	public void Update_Valid_2() throws IOException {
		runTest("Update_Valid_2");
	}

	@Test
	public void VarDecl_Valid_1() throws IOException {
		runTest("VarDecl_Valid_1");
	}

	@Test
	public void VarDecl_Valid_2() throws IOException {
		runTest("VarDecl_Valid_2");
	}

	@Test
	public void VarDecl_Valid_3() throws IOException {
		runTest("VarDecl_Valid_3");
	}

	@Test
	public void VarDecl_Valid_4() throws IOException {
		runTest("VarDecl_Valid_4");
	}

	@Test
	public void While_Valid_1() throws IOException {
		runTest("While_Valid_1");
	}

	@Test
	public void While_Valid_10() throws IOException {
		runTest("While_Valid_10");
	}

	@Test
	public void While_Valid_11() throws IOException {
		runTest("While_Valid_11");
	}

	@Test
	public void While_Valid_12() throws IOException {
		runTest("While_Valid_12");
	}

	@Test
	public void While_Valid_14() throws IOException {
		runTest("While_Valid_14");
	}

	@Ignore("unknown") @Test
	public void While_Valid_15() throws IOException {
		runTest("While_Valid_15");
	}

	@Test
	public void While_Valid_16() throws IOException {
		runTest("While_Valid_16");
	}

	@Test
	public void While_Valid_17() throws IOException {
		runTest("While_Valid_17");
	}

	@Test
	public void While_Valid_18() throws IOException {
		runTest("While_Valid_18");
	}

	@Test
	public void While_Valid_19() throws IOException {
		runTest("While_Valid_19");
	}

	@Test
	public void While_Valid_2() throws IOException {
		runTest("While_Valid_2");
	}

	@Test
	public void While_Valid_20() throws IOException {
		runTest("While_Valid_20");
	}

	@Test
	public void While_Valid_21() throws IOException {
		runTest("While_Valid_21");
	}

	@Test
	public void While_Valid_22() throws IOException {
		runTest("While_Valid_22");
	}

	@Test
	public void While_Valid_23() throws IOException {
		runTest("While_Valid_23");
	}

	@Test
	public void While_Valid_24() throws IOException {
		runTest("While_Valid_24");
	}

	@Test
	public void While_Valid_25() throws IOException {
		runTest("While_Valid_25");
	}

	@Test
	public void While_Valid_26() throws IOException {
		runTest("While_Valid_26");
	}

	@Test
	public void While_Valid_27() throws IOException {
		runTest("While_Valid_27");
	}

	@Test
	public void While_Valid_28() throws IOException {
		runTest("While_Valid_28");
	}

	@Test
	public void While_Valid_29() throws IOException {
		runTest("While_Valid_29");
	}

	@Test
	public void While_Valid_30() throws IOException {
		runTest("While_Valid_30");
	}

	@Test
	public void While_Valid_31() throws IOException {
		runTest("While_Valid_31");
	}

	@Test
	public void While_Valid_32() throws IOException {
		runTest("While_Valid_32");
	}

	@Test
	public void While_Valid_33() throws IOException {
		runTest("While_Valid_33");
	}

	@Test
	public void While_Valid_34() throws IOException {
		runTest("While_Valid_34");
	}

	@Test
	public void While_Valid_35() throws IOException {
		runTest("While_Valid_35");
	}

	@Test
	public void While_Valid_36() throws IOException {
		runTest("While_Valid_36");
	}

	@Test
	public void While_Valid_37() throws IOException {
		runTest("While_Valid_37");
	}

	@Test
	public void While_Valid_38() throws IOException {
		runTest("While_Valid_38");
	}

	@Test
	public void While_Valid_39() throws IOException {
		runTest("While_Valid_39");
	}

	@Test
	public void While_Valid_40() throws IOException {
		runTest("While_Valid_40");
	}

	@Test
	public void While_Valid_41() throws IOException {
		runTest("While_Valid_41");
	}

	@Test
	public void While_Valid_42() throws IOException {
		runTest("While_Valid_42");
	}

	@Test
	public void While_Valid_43() throws IOException {
		runTest("While_Valid_43");
	}

	@Test
	public void While_Valid_44() throws IOException {
		runTest("While_Valid_44");
	}

	@Test
	public void While_Valid_45() throws IOException {
		runTest("While_Valid_45");
	}

	@Test
	public void While_Valid_46() throws IOException {
		runTest("While_Valid_46");
	}

	@Test
	public void While_Valid_3() throws IOException {
		runTest("While_Valid_3");
	}

	public void While_Valid_5() throws IOException {
		runTest("While_Valid_5");
	}

	public void While_Valid_7() throws IOException {
		runTest("While_Valid_7");
	}

	@Test
	public void While_Valid_9() throws IOException {
		runTest("While_Valid_9");
	}
}
