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

import org.junit.*;

import wyc.WycMain;
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
public class AllValidVerificationTests {

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
	 */
	protected void runTest(String name) {
		// this will need to turn on verification at some point.
		name = WHILEY_SRC_DIR + File.separatorChar + name + ".whiley";

		Pair<Integer,String> p = TestUtils.compile(
				"-wd", WHILEY_SRC_DIR,      // location of source directory
				"-wp", WYRT_PATH,           // add wyrt to whileypath
				"-verify",                  // enable verification
				name);                      // name of test to compile

		int r = p.first();
		System.out.print(p.second());

		if (r != WycMain.SUCCESS) {
			fail("Test failed to compile!");
		} else if (r == WycMain.INTERNAL_FAILURE) {
			fail("Test caused internal failure!");
		}

		// TODO: we should actually execute the compiled file here using the
		// WyIL Interpreter (when that exists).
	}

	//
	// Tests
	//

	@Test
	public void Access_Valid_1() {
		runTest("Access_Valid_1");
	}

	@Test
	public void Access_Valid_2() {
		runTest("Access_Valid_2");
	}

	@Ignore("#308") @Test
	public void Assume_Valid_1() {
		runTest("Assume_Valid_1");
	}

	@Test
	public void Assume_Valid_2() {
		runTest("Assume_Valid_2");
	}

	@Test
	public void BoolAssign_Valid_1() {
		runTest("BoolAssign_Valid_1");
	}

	@Test
	public void BoolAssign_Valid_2() {
		runTest("BoolAssign_Valid_2");
	}

	@Test
	public void BoolAssign_Valid_3() {
		runTest("BoolAssign_Valid_3");
	}

	@Test
	public void BoolAssign_Valid_4() {
		runTest("BoolAssign_Valid_4");
	}

	@Test
	public void BoolAssign_Valid_5() {
		runTest("BoolAssign_Valid_5");
	}

	@Test
	public void BoolAssign_Valid_6() {
		runTest("BoolAssign_Valid_6");
	}

	@Test
	public void BoolFun_Valid_1() {
		runTest("BoolFun_Valid_1");
	}

	@Test
	public void BoolIfElse_Valid_1() {
		runTest("BoolIfElse_Valid_1");
	}

	@Test
	public void BoolIfElse_Valid_2() {
		runTest("BoolIfElse_Valid_2");
	}

	@Test
	public void BoolList_Valid_1() {
		runTest("BoolList_Valid_1");
	}

	@Ignore("#229") @Test
	public void BoolList_Valid_2() {
		runTest("BoolList_Valid_2");
	}

	@Ignore("#229") @Test
	public void BoolList_Valid_3() {
		runTest("BoolList_Valid_3");
	}

	@Test
	public void BoolRecord_Valid_1() {
		runTest("BoolRecord_Valid_1");
	}

	@Test
	public void BoolRecord_Valid_2() {
		runTest("BoolRecord_Valid_2");
	}

	@Test
	public void BoolRequires_Valid_1() {
		runTest("BoolRequires_Valid_1");
	}

	@Test
	public void BoolReturn_Valid_1() {
		runTest("BoolReturn_Valid_1");
	}

	@Test
	public void Byte_Valid_1() {
		runTest("Byte_Valid_1");
	}

	@Ignore("#336") @Test
	public void Byte_Valid_2() {
		runTest("Byte_Valid_2");
	}

	@Ignore("#336") @Test
	public void Byte_Valid_3() {
		runTest("Byte_Valid_3");
	}

	@Ignore("#336") @Test
	public void Byte_Valid_4() {
		runTest("Byte_Valid_4");
	}

	@Ignore("#336") @Test
	public void Byte_Valid_5() {
		runTest("Byte_Valid_5");
	}

	@Ignore("#336") @Test
	public void Byte_Valid_6() {
		runTest("Byte_Valid_6");
	}

	@Test
	public void Byte_Valid_7() {
		runTest("Byte_Valid_7");
	}

	@Ignore("#336") @Test
	public void Byte_Valid_8() {
		runTest("Byte_Valid_8");
	}

	@Ignore("#336") @Test
	public void Byte_Valid_9() {
		runTest("Byte_Valid_9");
	}

	@Test
	public void Cast_Valid_1() {
		runTest("Cast_Valid_1");
	}

	@Test
	public void Cast_Valid_2() {
		runTest("Cast_Valid_2");
	}

	@Test
	public void Cast_Valid_3() {
		runTest("Cast_Valid_3");
	}

	@Test
	public void Cast_Valid_4() {
		runTest("Cast_Valid_4");
	}

	@Test
	public void Cast_Valid_5() {
		runTest("Cast_Valid_5");
	}

	@Ignore("#366") @Test
	public void Cast_Valid_6() {
		runTest("Cast_Valid_6");
	}

	@Test
	public void Coercion_Valid_1() {
		runTest("Coercion_Valid_1");
	}

	@Test
	public void Coercion_Valid_2() {
		runTest("Coercion_Valid_2");
	}

	@Test
	public void Coercion_Valid_3() {
		runTest("Coercion_Valid_3");
	}

	@Test
	public void Coercion_Valid_7() {
		runTest("Coercion_Valid_7");
	}

	@Ignore("#298") @Test
	public void Coercion_Valid_8() {
		runTest("Coercion_Valid_8");
	}

	@Test
	public void Coercion_Valid_9() {
		runTest("Coercion_Valid_9");
	}

	@Ignore("Issue ???") @Test
	public void Complex_Valid_1() {
		runTest("Complex_Valid_1");
	}

	@Ignore("#298") @Test
	public void Complex_Valid_2() {
		runTest("Complex_Valid_2");
	}

	@Ignore("#339") @Test
	public void Complex_Valid_3() {
		runTest("Complex_Valid_3");
	}

	@Ignore("#298") @Test
	public void Complex_Valid_4() {
		runTest("Complex_Valid_4");
	}

	@Ignore("Issue ???") @Test
	public void Complex_Valid_5() {
		runTest("Complex_Valid_5");
	}

	@Test
	public void Complex_Valid_6() {
		runTest("Complex_Valid_6");
	}

	@Ignore("timeout") @Test
	public void Complex_Valid_7() {
		runTest("Complex_Valid_7");
	}

	@Ignore("???") @Test
	public void Complex_Valid_8() {
		runTest("Complex_Valid_8");
	}

	@Test
	public void Constant_Valid_3() {
		runTest("Constant_Valid_3");
	}

	@Ignore("#340") @Test
	public void ConstrainedDictionary_Valid_1() {
		runTest("ConstrainedDictionary_Valid_1");
	}

	@Test
	public void ConstrainedInt_Valid_1() {
		runTest("ConstrainedInt_Valid_1");
	}

	@Ignore("Issue ???	") @Test
	public void ConstrainedInt_Valid_10() {
		runTest("ConstrainedInt_Valid_10");
	}

	@Test
	public void ConstrainedInt_Valid_12() {
		runTest("ConstrainedInt_Valid_12");
	}

	@Test
	public void ConstrainedInt_Valid_13() {
		runTest("ConstrainedInt_Valid_13");
	}

	@Test
	public void ConstrainedInt_Valid_15() {
		runTest("ConstrainedInt_Valid_15");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedInt_Valid_16() {
		runTest("ConstrainedInt_Valid_16");
	}

	@Test
	public void ConstrainedInt_Valid_17() {
		runTest("ConstrainedInt_Valid_17");
	}

	@Test
	public void ConstrainedInt_Valid_18() {
		runTest("ConstrainedInt_Valid_18");
	}

	@Test
	public void ConstrainedInt_Valid_19() {
		runTest("ConstrainedInt_Valid_19");
	}

	@Test
	public void ConstrainedInt_Valid_2() {
		runTest("ConstrainedInt_Valid_2");
	}

	@Test
	public void ConstrainedInt_Valid_20() {
		runTest("ConstrainedInt_Valid_20");
	}

	@Test
	public void ConstrainedInt_Valid_21() {
		runTest("ConstrainedInt_Valid_21");
	}

	@Ignore("#337") @Test
	public void ConstrainedInt_Valid_22() {
		runTest("ConstrainedInt_Valid_22");
	}

	@Test
	public void ConstrainedInt_Valid_23() {
		runTest("ConstrainedInt_Valid_23");
	}

	@Test
	public void ConstrainedInt_Valid_24() {
		runTest("ConstrainedInt_Valid_24");
	}

	@Test
	public void ConstrainedInt_Valid_3() {
		runTest("ConstrainedInt_Valid_3");
	}

	@Test
	public void ConstrainedInt_Valid_4() {
		runTest("ConstrainedInt_Valid_4");
	}

	@Test
	public void ConstrainedInt_Valid_5() {
		runTest("ConstrainedInt_Valid_5");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedInt_Valid_6() {
		runTest("ConstrainedInt_Valid_6");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedInt_Valid_7() {
		runTest("ConstrainedInt_Valid_7");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedInt_Valid_8() {
		runTest("ConstrainedInt_Valid_8");
	}

	@Test
	public void ConstrainedIntersection_Valid_1() {
		runTest("ConstrainedIntersection_Valid_1");
	}

	@Test
	public void ConstrainedList_Valid_1() {
		runTest("ConstrainedList_Valid_1");
	}

	@Test
	public void ConstrainedList_Valid_11() {
		runTest("ConstrainedList_Valid_11");
	}

	@Test
	public void ConstrainedList_Valid_12() {
		runTest("ConstrainedList_Valid_12");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedList_Valid_14() {
		runTest("ConstrainedList_Valid_14");
	}

	@Test
	public void ConstrainedList_Valid_15() {
		runTest("ConstrainedList_Valid_15");
	}

	@Test
	public void ConstrainedList_Valid_16() {
		runTest("ConstrainedList_Valid_16");
	}

	@Test
	public void ConstrainedList_Valid_17() {
		runTest("ConstrainedList_Valid_17");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedList_Valid_18() {
		runTest("ConstrainedList_Valid_18");
	}

	@Test
	public void ConstrainedList_Valid_19() {
		runTest("ConstrainedList_Valid_19");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedList_Valid_2() {
		runTest("ConstrainedList_Valid_2");
	}

	@Test
	public void ConstrainedList_Valid_20() {
		runTest("ConstrainedList_Valid_20");
	}

	@Ignore("#308") @Test
	public void ConstrainedList_Valid_21() {
		runTest("ConstrainedList_Valid_21");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedList_Valid_22() {
		runTest("ConstrainedList_Valid_22");
	}

	@Ignore("#229") @Test
	public void ConstrainedList_Valid_23() {
		runTest("ConstrainedList_Valid_23");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedList_Valid_24() {
		runTest("ConstrainedList_Valid_24");
	}

	@Ignore("???") @Test
	public void ConstrainedList_Valid_25() {
		runTest("ConstrainedList_Valid_25");
	}

	@Test
	public void ConstrainedList_Valid_26() {
		runTest("ConstrainedList_Valid_26");
	}

	@Test
	public void ConstrainedList_Valid_27() {
		runTest("ConstrainedList_Valid_27");
	}

	@Ignore("#231") @Test
	public void ConstrainedList_Valid_3() {
		runTest("ConstrainedList_Valid_3");
	}

	@Test
	public void ConstrainedList_Valid_4() {
		runTest("ConstrainedList_Valid_4");
	}

	@Test
	public void ConstrainedList_Valid_5() {
		runTest("ConstrainedList_Valid_5");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedList_Valid_6() {
		runTest("ConstrainedList_Valid_6");
	}

	@Test
	public void ConstrainedList_Valid_7() {
		runTest("ConstrainedList_Valid_7");
	}

	@Ignore("#233") @Test
	public void ConstrainedList_Valid_8() {
		runTest("ConstrainedList_Valid_8");
	}

	@Test
	public void ConstrainedList_Valid_9() {
		runTest("ConstrainedList_Valid_9");
	}

	@Ignore("#342") @Test
	public void ConstrainedNegation_Valid_1() {
		runTest("ConstrainedNegation_Valid_1");
	}

	@Ignore("#342") @Test
	public void ConstrainedNegation_Valid_2() {
		runTest("ConstrainedNegation_Valid_2");
	}

	@Test
	public void ConstrainedRecord_Valid_1() {
		runTest("ConstrainedRecord_Valid_1");
	}

	@Test
	public void ConstrainedRecord_Valid_2() {
		runTest("ConstrainedRecord_Valid_2");
	}

	@Test
	public void ConstrainedRecord_Valid_3() {
		runTest("ConstrainedRecord_Valid_3");
	}

	@Test
	public void ConstrainedRecord_Valid_4() {
		runTest("ConstrainedRecord_Valid_4");
	}

	@Test
	public void ConstrainedRecord_Valid_5() {
		runTest("ConstrainedRecord_Valid_5");
	}

	@Test
	public void ConstrainedRecord_Valid_6() {
		runTest("ConstrainedRecord_Valid_6");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedRecord_Valid_8() {
		runTest("ConstrainedRecord_Valid_8");
	}

	@Ignore("Issue ???") @Test
	public void ConstrainedRecord_Valid_9() {
		runTest("ConstrainedRecord_Valid_9");
	}

	@Test
	public void ConstrainedRecord_Valid_10() {
		runTest("ConstrainedRecord_Valid_10");
	}

	@Test
	public void ConstrainedReference_Valid_1() {
		runTest("ConstrainedReference_Valid_1");
	}

	@Test
	public void ConstrainedTuple_Valid_1() {
		runTest("ConstrainedTuple_Valid_1");
	}

	@Test
	public void ConstrainedTuple_Valid_2() {
		runTest("ConstrainedTuple_Valid_2");
	}

	@Test
	public void ConstrainedUnion_Valid_1() {
		runTest("ConstrainedUnion_Valid_1");
	}

	@Ignore("Unknown Problem") @Test
	public void Contractive_Valid_1() {
		runTest("Contractive_Valid_1");
	}

	@Test
	public void Contractive_Valid_2() {
		runTest("Contractive_Valid_2");
	}

	@Test
	public void DecimalAssignment_Valid_1() {
		runTest("DecimalAssignment_Valid_1");
	}

	@Test
	public void Define_Valid_1() {
		runTest("Define_Valid_1");
	}

	@Test
	public void Define_Valid_2() {
		runTest("Define_Valid_2");
	}

	@Test
	public void Define_Valid_3() {
		runTest("Define_Valid_3");
	}

	@Test
	public void Define_Valid_4() {
		runTest("Define_Valid_4");
	}

	@Ignore("Issue #343") @Test
	public void DoWhile_Valid_1() {
		runTest("DoWhile_Valid_1");
	}

	@Test
	public void DoWhile_Valid_2() {
		runTest("DoWhile_Valid_2");
	}

	@Test
	public void DoWhile_Valid_3() {
		runTest("DoWhile_Valid_3");
	}

	@Ignore("#298") @Test
	public void DoWhile_Valid_4() {
		runTest("DoWhile_Valid_4");
	}

	@Ignore("#343") @Test
	public void DoWhile_Valid_5() {
		runTest("DoWhile_Valid_5");
	}

	@Ignore("timeout?") @Test
	public void DoWhile_Valid_6() {
		runTest("DoWhile_Valid_6");
	}

	@Test
	public void EffectiveList_Valid_1() {
		runTest("EffectiveList_Valid_1");
	}

	@Test
	public void Ensures_Valid_1() {
		runTest("Ensures_Valid_1");
	}

	@Test
	public void Ensures_Valid_2() {
		runTest("Ensures_Valid_2");
	}


	@Ignore("Issue ???") @Test
	public void Ensures_Valid_3() {
		runTest("Ensures_Valid_3");
	}

	@Test
	public void Ensures_Valid_4() {
		runTest("Ensures_Valid_4");
	}

	@Test
	public void Ensures_Valid_5() {
		runTest("Ensures_Valid_5");
	}

	@Test
	public void Ensures_Valid_6() {
		runTest("Ensures_Valid_6");
	}


	@Ignore("Issue ???") @Test
	public void Ensures_Valid_7() {
		runTest("Ensures_Valid_7");
	}

	@Test
	public void Ensures_Valid_8() {
		runTest("Ensures_Valid_8");
	}

	@Ignore("Issue ???") @Test
	public void FunctionRef_Valid_1() {
		runTest("FunctionRef_Valid_1");
	}

	@Ignore("Issue ???") @Test
	public void FunctionRef_Valid_2() {
		runTest("FunctionRef_Valid_2");
	}


	@Ignore("Issue ???") @Test
	public void FunctionRef_Valid_3() {
		runTest("FunctionRef_Valid_3");
	}

	@Ignore("Issue ???") @Test
	public void FunctionRef_Valid_4() {
		runTest("FunctionRef_Valid_4");
	}

	@Ignore("Issue ???") @Test
	public void FunctionRef_Valid_5() {
		runTest("FunctionRef_Valid_5");
	}

	@Ignore("Issue ???") @Test
	public void FunctionRef_Valid_6() {
		runTest("FunctionRef_Valid_6");
	}

	@Test
	public void FunctionRef_Valid_7() {
		runTest("FunctionRef_Valid_7");
	}

	@Ignore("Issue ???") @Test
	public void FunctionRef_Valid_8() {
		runTest("FunctionRef_Valid_8");
	}

	@Ignore("Unknown Issue") @Test
	public void FunctionRef_Valid_9() {
		runTest("FunctionRef_Valid_9");
	}

	@Test
	public void Function_Valid_1() {
		runTest("Function_Valid_1");
	}

	@Test
	public void Function_Valid_12() {
		runTest("Function_Valid_12");
	}

	@Test
	public void Function_Valid_13() {
		runTest("Function_Valid_13");
	}

	@Test
	public void Function_Valid_14() {
		runTest("Function_Valid_14");
	}

	@Test
	public void Function_Valid_16() {
		runTest("Function_Valid_16");
	}

	@Test
	public void Function_Valid_17() {
		runTest("Function_Valid_17");
	}

	@Test
	public void Function_Valid_18() {
		runTest("Function_Valid_18");
	}

	@Ignore("#290") @Test
	public void Function_Valid_19() {
		runTest("Function_Valid_19");
	}

	@Test
	public void Function_Valid_2() {
		runTest("Function_Valid_2");
	}

	@Test
	public void Function_Valid_20() {
		runTest("Function_Valid_20");
	}

	@Test
	public void Function_Valid_21() {
		runTest("Function_Valid_21");
	}

	@Test
	public void Function_Valid_3() {
		runTest("Function_Valid_3");
	}

	@Test
	public void Function_Valid_4() {
		runTest("Function_Valid_4");
	}

	@Test
	public void Function_Valid_5() {
		runTest("Function_Valid_5");
	}


	@Ignore("#234") @Test
	public void Function_Valid_6() {
		runTest("Function_Valid_6");
	}

	@Test
	public void Function_Valid_7() {
		runTest("Function_Valid_7");
	}


	@Ignore("#234") @Test
	public void Function_Valid_8() {
		runTest("Function_Valid_8");
	}

	@Test
	public void Function_Valid_9() {
		runTest("Function_Valid_9");
	}

	@Test
	public void HexAssign_Valid_1() {
		runTest("HexAssign_Valid_1");
	}

	@Test
	public void IfElse_Valid_1() {
		runTest("IfElse_Valid_1");
	}

	@Test
	public void IfElse_Valid_2() {
		runTest("IfElse_Valid_2");
	}

	@Test
	public void IfElse_Valid_3() {
		runTest("IfElse_Valid_3");
	}

	@Ignore("#298") @Test
	public void IfElse_Valid_4() {
		runTest("IfElse_Valid_4");
	}

	@Test
	public void IfElse_Valid_5() {
		runTest("IfElse_Valid_5");
	}

	@Test
	public void Import_Valid_1() {
		runTest("Import_Valid_1");
	}

	@Test
	public void Import_Valid_2() {
		runTest("Import_Valid_2");
	}

	@Test
	public void Import_Valid_3() {
		runTest("Import_Valid_3");
	}

	@Ignore("#492") @Test
	public void Import_Valid_4() {
		runTest("Import_Valid_4");
	}

	@Ignore("#492") @Test
	public void Import_Valid_5() {
		runTest("Import_Valid_5");
	}

	@Test
	public void Import_Valid_6() {
		runTest("Import_Valid_6");
	}

	@Test
	public void IntConst_Valid_1() {
		runTest("IntConst_Valid_1");
	}

	@Test
	public void IntDefine_Valid_1() {
		runTest("IntDefine_Valid_1");
	}

	@Test
	public void IntDefine_Valid_2() {
		runTest("IntDefine_Valid_2");
	}

	@Test
	public void IntDiv_Valid_1() {
		runTest("IntDiv_Valid_1");
	}

	@Ignore("Unknown Issue") @Test
	public void IntDiv_Valid_3() {
		runTest("IntDiv_Valid_3");
	}

	@Test
	public void IntDiv_Valid_4() {
		runTest("IntDiv_Valid_4");
	}

	@Test
	public void IntDiv_Valid_5() {
		runTest("IntDiv_Valid_5");
	}

	@Test
	public void IntEquals_Valid_1() {
		runTest("IntEquals_Valid_1");
	}

	@Test
	public void IntMul_Valid_1() {
		runTest("IntMul_Valid_1");
	}
	@Test
	public void IntMul_Valid_2() {
		runTest("IntMul_Valid_2");
	}

	@Test
	public void IntOp_Valid_1() {
		runTest("IntOp_Valid_1");
	}

	@Ignore("Known Issue") @Test
	public void Intersection_Valid_1() {
		runTest("Intersection_Valid_1");
	}

	@Ignore("Known Issue") @Test
	public void Intersection_Valid_2() {
		runTest("Intersection_Valid_2");
	}

	@Ignore("#344") @Test
	public void Lambda_Valid_1() {
		runTest("Lambda_Valid_1");
	}

	@Ignore("#344") @Test
	public void Lambda_Valid_2() {
		runTest("Lambda_Valid_2");
	}

	@Ignore("#344") @Test
	public void Lambda_Valid_3() {
		runTest("Lambda_Valid_3");
	}

	@Ignore("#344") @Test
	public void Lambda_Valid_4() {
		runTest("Lambda_Valid_4");
	}

	@Ignore("#344") @Test
	public void Lambda_Valid_5() {
		runTest("Lambda_Valid_5");
	}

	@Ignore("#344") @Test
	public void Lambda_Valid_6() {
		runTest("Lambda_Valid_6");
	}

	@Ignore("#344") @Test
	public void Lambda_Valid_7() {
		runTest("Lambda_Valid_7");
	}

	@Ignore("#344") @Test
	public void Lambda_Valid_8() {
		runTest("Lambda_Valid_8");
	}

	@Test
	public void LengthOf_Valid_1() {
		runTest("LengthOf_Valid_1");
	}

	@Test
	public void LengthOf_Valid_4() {
		runTest("LengthOf_Valid_4");
	}

	@Test
	public void LengthOf_Valid_5() {
		runTest("LengthOf_Valid_5");
	}

	@Test
	public void ListAccess_Valid_1() {
		runTest("ListAccess_Valid_1");
	}

	@Test
	public void ListAccess_Valid_3() {
		runTest("ListAccess_Valid_3");
	}

	@Test
	public void ListAccess_Valid_5() {
		runTest("ListAccess_Valid_5");
	}

	@Ignore("Known Issue") @Test
	public void ListAccess_Valid_6() {
		runTest("ListAccess_Valid_6");
	}

	@Ignore("Known Issue") @Test
	public void ListAccess_Valid_7() {
		runTest("ListAccess_Valid_7");
	}

	@Test
	public void ListAccess_Valid_8() {
		runTest("ListAccess_Valid_8");
	}

	@Test
	public void ListAccess_Valid_9() {
		runTest("ListAccess_Valid_9");
	}

	@Test
	public void ListAppend_Valid_1() {
		runTest("ListAppend_Valid_1");
	}


	@Ignore("#231") @Test
	public void ListAppend_Valid_10() {
		runTest("ListAppend_Valid_10");
	}

	@Test
	public void ListAppend_Valid_11() {
		runTest("ListAppend_Valid_11");
	}


	@Ignore("#231") @Test
	public void ListAppend_Valid_12() {
		runTest("ListAppend_Valid_12");
	}

	@Test
	public void ListAppend_Valid_13() {
		runTest("ListAppend_Valid_13");
	}

	@Test
	public void ListAppend_Valid_14() {
		runTest("ListAppend_Valid_14");
	}

	@Test
	public void ListAppend_Valid_2() {
		runTest("ListAppend_Valid_2");
	}

	@Ignore("#231") @Test
	public void ListAppend_Valid_3() {
		runTest("ListAppend_Valid_3");
	}

	@Test
	public void ListAppend_Valid_4() {
		runTest("ListAppend_Valid_4");
	}

	@Ignore("#231") @Test
	public void ListAppend_Valid_5() {
		runTest("ListAppend_Valid_5");
	}

	@Test
	public void ListAppend_Valid_6() {
		runTest("ListAppend_Valid_6");
	}

	@Test
	public void ListAppend_Valid_7() {
		runTest("ListAppend_Valid_7");
	}

	@Test
	public void ListAppend_Valid_8() {
		runTest("ListAppend_Valid_8");
	}

	@Test
	public void ListAppend_Valid_9() {
		runTest("ListAppend_Valid_9");
	}


	@Ignore("#233") @Test
	public void ListAssign_Valid_1() {
		runTest("ListAssign_Valid_1");
	}

	@Test
	public void ListAssign_Valid_11() {
		runTest("ListAssign_Valid_11");
	}

	@Ignore("timeout") @Test
	public void ListAssign_Valid_2() {
		runTest("ListAssign_Valid_2");
	}

	@Test
	public void ListAssign_Valid_3() {
		runTest("ListAssign_Valid_3");
	}

	@Ignore("#233") @Test
	public void ListAssign_Valid_4() {
		runTest("ListAssign_Valid_4");
	}

	@Ignore("Issue ???") @Test
	public void ListAssign_Valid_5() {
		runTest("ListAssign_Valid_5");
	}

	@Ignore("#233") @Test
	public void ListAssign_Valid_6() {
		runTest("ListAssign_Valid_6");
	}

	@Ignore("#233") @Test
	public void ListAssign_Valid_7() {
		runTest("ListAssign_Valid_7");
	}

	@Test
	public void ListAssign_Valid_8() {
		runTest("ListAssign_Valid_8");
	}

	@Test
	public void ListAssign_Valid_9() {
		runTest("ListAssign_Valid_9");
	}

	@Test
	public void ListConversion_Valid_1() {
		runTest("ListConversion_Valid_1");
	}

	@Test
	public void ListElemOf_Valid_1() {
		runTest("ListElemOf_Valid_1");
	}

	@Test
	public void ListEmpty_Valid_1() {
		runTest("ListEmpty_Valid_1");
	}

	@Test
	public void ListEquals_Valid_1() {
		runTest("ListEquals_Valid_1");
	}

	@Test
	public void ListGenerator_Valid_1() {
		runTest("ListGenerator_Valid_1");
	}

	@Ignore("Unknown Issue") @Test
	public void ListGenerator_Valid_2() {
		runTest("ListGenerator_Valid_2");
	}

	@Test
	public void ListGenerator_Valid_3() {
		runTest("ListGenerator_Valid_3");
	}

	@Test
	public void ListGenerator_Valid_5() {
		runTest("ListGenerator_Valid_5");
	}

	@Test
	public void ListLength_Valid_1() {
		runTest("ListLength_Valid_1");
	}

	@Test
	public void ListLength_Valid_2() {
		runTest("ListLength_Valid_2");
	}

	@Test
	public void ListLength_Valid_3() {
		runTest("ListLength_Valid_3");
	}

	@Test
	public void ListRange_Valid_1() {
		runTest("ListRange_Valid_1");
	}

	@Ignore("#232") @Test
	public void ListSublist_Valid_1() {
		runTest("ListSublist_Valid_1");
	}

	@Test
	public void ListSublist_Valid_2() {
		runTest("ListSublist_Valid_2");
	}

	@Ignore("#232") @Test
	public void ListSublist_Valid_3() {
		runTest("ListSublist_Valid_3");
	}

	@Test
	public void ListSublist_Valid_4() {
		runTest("ListSublist_Valid_4");
	}

	@Test
	public void ListSublist_Valid_5() {
		runTest("ListSublist_Valid_5");
	}


	@Ignore("#344") @Test
	public void MessageRef_Valid_1() {
		runTest("MessageRef_Valid_1");
	}


	@Ignore("#344") @Test
	public void MessageRef_Valid_2() {
		runTest("MessageRef_Valid_2");
	}

	@Test
	public void MessageSend_Valid_1() {
		runTest("MessageSend_Valid_1");
	}

	@Test
	public void MessageSend_Valid_2() {
		runTest("MessageSend_Valid_2");
	}

	@Test
	public void MessageSend_Valid_3() {
		runTest("MessageSend_Valid_3");
	}

	@Test
	public void MessageSend_Valid_4() {
		runTest("MessageSend_Valid_4");
	}

	@Test
	public void MessageSend_Valid_5() {
		runTest("MessageSend_Valid_5");
	}

	@Test
	public void MethodCall_Valid_1() {
		runTest("MethodCall_Valid_1");
	}

	@Test
	public void MethodCall_Valid_2() {
		runTest("MethodCall_Valid_2");
	}

	@Test
	public void MethodCall_Valid_3() {
		runTest("MethodCall_Valid_3");
	}

	@Ignore("#344") @Test
	public void MethodCall_Valid_4() {
		runTest("MethodCall_Valid_4");
	}

	@Ignore("#344") @Test
	public void MethodRef_Valid_1() {
		runTest("MethodRef_Valid_1");
	}

	@Ignore("#344") @Test
	public void MethodRef_Valid_2() {
		runTest("MethodRef_Valid_2");
	}

	@Ignore("Issue ???") @Test
	public void Method_Valid_1() {
		runTest("Method_Valid_1");
	}

	@Test
	public void MultiLineComment_Valid_1() {
		runTest("MultiLineComment_Valid_1");
	}

	@Test
	public void MultiLineComment_Valid_2() {
		runTest("MultiLineComment_Valid_2");
	}

	@Test
	public void NegationType_Valid_1() {
		runTest("NegationType_Valid_1");
	}

	@Test
	public void NegationType_Valid_2() {
		runTest("NegationType_Valid_2");
	}

	@Ignore("Known Issue") @Test
	public void NegationType_Valid_3() {
		runTest("NegationType_Valid_3");
	}

	@Test
	public void NegationType_Valid_4() {
		runTest("NegationType_Valid_4");
	}

	@Test
	public void OpenRecord_Valid_1() {
		runTest("OpenRecord_Valid_1");
	}

	@Test
	public void OpenRecord_Valid_10() {
		runTest("OpenRecord_Valid_10");
	}

	@Test
	public void OpenRecord_Valid_2() {
		runTest("OpenRecord_Valid_2");
	}

	@Ignore("Known Issue") @Test
	public void OpenRecord_Valid_3() {
		runTest("OpenRecord_Valid_3");
	}

	@Test
	public void OpenRecord_Valid_4() {
		runTest("OpenRecord_Valid_4");
	}

	@Test
	public void OpenRecord_Valid_5() {
		runTest("OpenRecord_Valid_5");
	}

	@Test
	public void OpenRecord_Valid_6() {
		runTest("OpenRecord_Valid_6");
	}

	@Test
	public void OpenRecord_Valid_7() {
		runTest("OpenRecord_Valid_7");
	}

	@Test
	public void OpenRecord_Valid_8() {
		runTest("OpenRecord_Valid_8");
	}

	@Test
	public void OpenRecord_Valid_9() {
		runTest("OpenRecord_Valid_9");
	}

	@Test
	public void ProcessAccess_Valid_1() {
		runTest("ProcessAccess_Valid_1");
	}

	@Test
	public void ProcessAccess_Valid_2() {
		runTest("ProcessAccess_Valid_2");
	}

	@Ignore("#291") @Test
	public void Process_Valid_1() {
		runTest("Process_Valid_1");
	}

	@Ignore("#291") @Test
	public void Process_Valid_10() {
		runTest("Process_Valid_10");
	}

	@Ignore("#291") @Test
	public void Process_Valid_11() {
		runTest("Process_Valid_11");
	}

	@Ignore("#291") @Test
	public void Process_Valid_12() {
		runTest("Process_Valid_12");
	}

	@Test
	public void Process_Valid_2() {
		runTest("Process_Valid_2");
	}

	@Test
	public void Process_Valid_3() {
		runTest("Process_Valid_3");
	}

	@Test
	public void Process_Valid_4() {
		runTest("Process_Valid_4");
	}

	@Test
	public void Process_Valid_5() {
		runTest("Process_Valid_5");
	}

	@Ignore("Issue ???") @Test
	public void Process_Valid_6() {
		runTest("Process_Valid_6");
	}

	@Test
	public void Process_Valid_7() {
		runTest("Process_Valid_7");
	}

	@Test
	public void Process_Valid_8() {
		runTest("Process_Valid_8");
	}


	@Ignore("#231") @Test
	public void Process_Valid_9() {
		runTest("Process_Valid_9");
	}

	@Test
	public void Quantifiers_Valid_1() {
		runTest("Quantifiers_Valid_1");
	}

	@Ignore("#308") @Test
	public void Range_Valid_1() {
		runTest("Range_Valid_1");
	}

	@Test
	public void RealConst_Valid_1() {
		runTest("RealConst_Valid_1");
	}

	@Test
	public void RealDiv_Valid_1() {
		runTest("RealDiv_Valid_1");
	}

	@Test
	public void RealDiv_Valid_2() {
		runTest("RealDiv_Valid_2");
	}

	@Test
	public void RealDiv_Valid_3() {
		runTest("RealDiv_Valid_3");
	}

	@Test
	public void RealDiv_Valid_4() {
		runTest("RealDiv_Valid_4");
	}

	@Ignore("#183") @Test
	public void RealDiv_Valid_5() {
		runTest("RealDiv_Valid_5");
	}

	@Test
	public void RealDiv_Valid_6() {
		runTest("RealDiv_Valid_6");
	}

	@Test
	public void RealDiv_Valid_7() {
		runTest("RealDiv_Valid_7");
	}

	@Test
	public void RealNeg_Valid_1() {
		runTest("RealNeg_Valid_1");
	}

	@Test
	public void RealNeg_Valid_2() {
		runTest("RealNeg_Valid_2");
	}

	@Test
	public void RealSplit_Valid_1() {
		runTest("RealSplit_Valid_1");
	}

	@Test
	public void RealSub_Valid_1() {
		runTest("RealSub_Valid_1");
	}

	@Test
	public void RealSub_Valid_2() {
		runTest("RealSub_Valid_2");
	}

	@Test
	public void RealSub_Valid_3() {
		runTest("RealSub_Valid_3");
	}

	@Test
	public void Real_Valid_1() {
		runTest("Real_Valid_1");
	}

	@Test
	public void RecordAccess_Valid_1() {
		runTest("RecordAccess_Valid_1");
	}

	@Test
	public void RecordAccess_Valid_2() {
		runTest("RecordAccess_Valid_2");
	}

	@Test
	public void RecordAssign_Valid_1() {
		runTest("RecordAssign_Valid_1");
	}

	@Test
	public void RecordAssign_Valid_10() {
		runTest("RecordAssign_Valid_10");
	}

	@Test
	public void RecordAssign_Valid_2() {
		runTest("RecordAssign_Valid_2");
	}

	@Test
	public void RecordAssign_Valid_3() {
		runTest("RecordAssign_Valid_3");
	}

	@Test
	public void RecordAssign_Valid_4() {
		runTest("RecordAssign_Valid_4");
	}

	@Test
	public void RecordAssign_Valid_5() {
		runTest("RecordAssign_Valid_5");
	}

	@Test
	public void RecordAssign_Valid_6() {
		runTest("RecordAssign_Valid_6");
	}

	@Test
	public void RecordAssign_Valid_7() {
		runTest("RecordAssign_Valid_7");
	}

	@Test
	public void RecordAssign_Valid_8() {
		runTest("RecordAssign_Valid_8");
	}

	@Test
	public void RecordAssign_Valid_9() {
		runTest("RecordAssign_Valid_9");
	}

	@Test
	public void RecordCoercion_Valid_1() {
		runTest("RecordCoercion_Valid_1");
	}

	@Test
	public void RecordConversion_Valid_1() {
		runTest("RecordConversion_Valid_1");
	}

	@Test
	public void RecordDefine_Valid_1() {
		runTest("RecordDefine_Valid_1");
	}

	@Test
	public void RecordDefine_Valid_2() {
		runTest("RecordDefine_Valid_2");
	}

	@Ignore("Known Issue") @Test
	public void RecordSubtype_Valid_1() {
		runTest("RecordSubtype_Valid_1");
	}

	@Ignore("Known Issue") @Test
	public void RecordSubtype_Valid_2() {
		runTest("RecordSubtype_Valid_2");
	}

	@Test
	public void RecursiveType_Valid_1() {
		runTest("RecursiveType_Valid_1");
	}

	@Test
	public void RecursiveType_Valid_10() {
		runTest("RecursiveType_Valid_10");
	}

	@Test
	public void RecursiveType_Valid_11() {
		runTest("RecursiveType_Valid_11");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_12() {
		runTest("RecursiveType_Valid_12");
	}

	@Test
	public void RecursiveType_Valid_13() {
		runTest("RecursiveType_Valid_13");
	}

	@Test
	public void RecursiveType_Valid_14() {
		runTest("RecursiveType_Valid_14");
	}

	@Test
	public void RecursiveType_Valid_15() {
		runTest("RecursiveType_Valid_15");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_16() {
		runTest("RecursiveType_Valid_16");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_17() {
		runTest("RecursiveType_Valid_17");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_18() {
		runTest("RecursiveType_Valid_18");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_19() {
		runTest("RecursiveType_Valid_19");
	}


	@Ignore("#298") @Test
	public void RecursiveType_Valid_2() {
		runTest("RecursiveType_Valid_2");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_20() {
		runTest("RecursiveType_Valid_20");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_21() {
		runTest("RecursiveType_Valid_21");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_22() {
		runTest("RecursiveType_Valid_22");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_23() {
		runTest("RecursiveType_Valid_23");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_24() {
		runTest("RecursiveType_Valid_24");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_25() {
		runTest("RecursiveType_Valid_25");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_26() {
		runTest("RecursiveType_Valid_26");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_27() {
		runTest("RecursiveType_Valid_27");
	}

	@Ignore("#396") @Test
	public void RecursiveType_Valid_29() {
		runTest("RecursiveType_Valid_29");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_3() {
		runTest("RecursiveType_Valid_3");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_30() {
		runTest("RecursiveType_Valid_30");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_31() {
		runTest("RecursiveType_Valid_31");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_4() {
		runTest("RecursiveType_Valid_4");
	}

	@Ignore("#18") @Test
	public void RecursiveType_Valid_5() {
		runTest("RecursiveType_Valid_5");
	}

	@Test
	public void RecursiveType_Valid_6() {
		runTest("RecursiveType_Valid_6");
	}

	@Ignore("#298") @Test
	public void RecursiveType_Valid_7() {
		runTest("RecursiveType_Valid_7");
	}

	@Test
	public void RecursiveType_Valid_8() {
		runTest("RecursiveType_Valid_8");
	}

	@Test
	public void RecursiveType_Valid_9() {
		runTest("RecursiveType_Valid_9");
	}

	@Test
	public void Reference_Valid_1() {
		runTest("Reference_Valid_1");
	}

	@Ignore("Unclassified") @Test
	public void Reference_Valid_2() {
		runTest("Reference_Valid_2");
	}

	@Ignore("Unclassified") @Test
	public void Reference_Valid_3() {
		runTest("Reference_Valid_3");
	}

	@Test
	public void Reference_Valid_4() {
		runTest("Reference_Valid_4");
	}

	@Test
	public void Reference_Valid_5() {
		runTest("Reference_Valid_5");
	}

	@Ignore("#337") @Test
	public void Remainder_Valid_1() {
		runTest("Remainder_Valid_1");
	}

	@Test
	public void Requires_Valid_1() {
		runTest("Requires_Valid_1");
	}

	@Test
	public void Resolution_Valid_1() {
		runTest("Resolution_Valid_1");
	}

	@Test
	public void SingleLineComment_Valid_1() {
		runTest("SingleLineComment_Valid_1");
	}

	@Test
	public void Skip_Valid_1() {
		runTest("Skip_Valid_1");
	}

	@Test
	public void String_Valid_1() {
		runTest("String_Valid_1");
	}


	@Test
	public void String_Valid_2() {
		runTest("String_Valid_2");
	}

	@Test
	public void String_Valid_3() {
		runTest("String_Valid_3");
	}


	@Test
	public void String_Valid_4() {
		runTest("String_Valid_4");
	}

	@Test
	public void String_Valid_5() {
		runTest("String_Valid_5");
	}

	@Test
	public void String_Valid_6() {
		runTest("String_Valid_6");
	}

	@Test
	public void String_Valid_7() {
		runTest("String_Valid_7");
	}

	@Test
	public void String_Valid_8() {
		runTest("String_Valid_8");
	}

	@Test
	public void Subtype_Valid_1() {
		runTest("Subtype_Valid_1");
	}

	@Test
	public void Subtype_Valid_10() {
		runTest("Subtype_Valid_10");
	}

	@Test
	public void Subtype_Valid_11() {
		runTest("Subtype_Valid_11");
	}

	@Test
	public void Subtype_Valid_12() {
		runTest("Subtype_Valid_12");
	}

	@Test
	public void Subtype_Valid_13() {
		runTest("Subtype_Valid_13");
	}

	@Test
	public void Subtype_Valid_14() {
		runTest("Subtype_Valid_14");
	}

	@Test
	public void Subtype_Valid_2() {
		runTest("Subtype_Valid_2");
	}

	@Test
	public void Subtype_Valid_3() {
		runTest("Subtype_Valid_3");
	}

	@Test
	public void Subtype_Valid_4() {
		runTest("Subtype_Valid_4");
	}

	@Test
	public void Subtype_Valid_5() {
		runTest("Subtype_Valid_5");
	}

	@Test
	public void Subtype_Valid_6() {
		runTest("Subtype_Valid_6");
	}

	@Test
	public void Subtype_Valid_7() {
		runTest("Subtype_Valid_7");
	}

	@Test
	public void Subtype_Valid_8() {
		runTest("Subtype_Valid_8");
	}

	@Test
	public void Subtype_Valid_9() {
		runTest("Subtype_Valid_9");
	}

	@Test
	public void Switch_Valid_1() {
		runTest("Switch_Valid_1");
	}

	@Test
	public void Switch_Valid_10() {
		runTest("Switch_Valid_10");
	}

	@Test
	public void Switch_Valid_11() {
		runTest("Switch_Valid_11");
	}

	@Test
	public void Switch_Valid_12() {
		runTest("Switch_Valid_12");
	}

	@Test
	public void Switch_Valid_13() {
		runTest("Switch_Valid_13");
	}

	@Test
	public void Switch_Valid_2() {
		runTest("Switch_Valid_2");
	}

	@Test
	public void Switch_Valid_3() {
		runTest("Switch_Valid_3");
	}

	@Test
	public void Switch_Valid_4() {
		runTest("Switch_Valid_4");
	}

	@Test
	public void Switch_Valid_5() {
		runTest("Switch_Valid_5");
	}

	@Test
	public void Switch_Valid_6() {
		runTest("Switch_Valid_6");
	}

	@Test
	public void Switch_Valid_7() {
		runTest("Switch_Valid_7");
	}

	@Test
	public void Switch_Valid_8() {
		runTest("Switch_Valid_8");
	}

	@Test
	public void Switch_Valid_9() {
		runTest("Switch_Valid_9");
	}

	@Test
	public void Syntax_Valid_1() {
		runTest("Syntax_Valid_1");
	}

	@Test
	public void TupleType_Valid_1() {
		runTest("TupleType_Valid_1");
	}

	@Test
	public void TupleType_Valid_2() {
		runTest("TupleType_Valid_2");
	}

	@Test
	public void TupleType_Valid_3() {
		runTest("TupleType_Valid_3");
	}

	@Test
	public void TupleType_Valid_4() {
		runTest("TupleType_Valid_4");
	}

	@Test
	public void TupleType_Valid_5() {
		runTest("TupleType_Valid_5");
	}

	@Test
	public void TupleType_Valid_6() {
		runTest("TupleType_Valid_6");
	}

	@Test
	public void TupleType_Valid_7() {
		runTest("TupleType_Valid_7");
	}

	@Ignore("timeout") @Test
	public void TupleType_Valid_8() {
		runTest("TupleType_Valid_8");
	}

	@Test
	public void TypeEquals_Valid_1() {
		runTest("TypeEquals_Valid_1");
	}

	@Test
	public void TypeEquals_Valid_10() {
		runTest("TypeEquals_Valid_10");
	}

	@Test
	public void TypeEquals_Valid_11() {
		runTest("TypeEquals_Valid_11");
	}

	@Test
	public void TypeEquals_Valid_12() {
		runTest("TypeEquals_Valid_12");
	}

	@Test
	public void TypeEquals_Valid_14() {
		runTest("TypeEquals_Valid_14");
	}

	@Test
	public void TypeEquals_Valid_15() {
		runTest("TypeEquals_Valid_15");
	}

	@Ignore("#298") @Test
	public void TypeEquals_Valid_16() {
		runTest("TypeEquals_Valid_16");
	}

	@Test
	public void TypeEquals_Valid_17() {
		runTest("TypeEquals_Valid_17");
	}

	@Test
	public void TypeEquals_Valid_18() {
		runTest("TypeEquals_Valid_18");
	}

	@Test
	public void TypeEquals_Valid_19() {
		runTest("TypeEquals_Valid_19");
	}

	@Ignore("#298") @Test
	public void TypeEquals_Valid_2() {
		runTest("TypeEquals_Valid_2");
	}

	@Test
	public void TypeEquals_Valid_20() {
		runTest("TypeEquals_Valid_20");
	}

	@Test
	public void TypeEquals_Valid_21() {
		runTest("TypeEquals_Valid_21");
	}

	@Ignore("Issue ???") @Test
	public void TypeEquals_Valid_23() {
		runTest("TypeEquals_Valid_23");
	}

	@Test
	public void TypeEquals_Valid_24() {
		runTest("TypeEquals_Valid_24");
	}

	@Ignore("#298") @Test
	public void TypeEquals_Valid_25() {
		runTest("TypeEquals_Valid_25");
	}

	@Test
	public void TypeEquals_Valid_27() {
		runTest("TypeEquals_Valid_27");
	}

	@Test
	public void TypeEquals_Valid_28() {
		runTest("TypeEquals_Valid_28");
	}

	@Test
	public void TypeEquals_Valid_29() {
		runTest("TypeEquals_Valid_29");
	}

	@Ignore("Issue ???") @Test
	public void TypeEquals_Valid_3() {
		runTest("TypeEquals_Valid_3");
	}

	@Ignore("#298") @Test
	public void TypeEquals_Valid_30() {
		runTest("TypeEquals_Valid_30");
	}

	@Test
	public void TypeEquals_Valid_31() {
		runTest("TypeEquals_Valid_31");
	}

	@Test
	public void TypeEquals_Valid_32() {
		runTest("TypeEquals_Valid_32");
	}

	@Test
	public void TypeEquals_Valid_33() {
		runTest("TypeEquals_Valid_33");
	}

	@Test
	public void TypeEquals_Valid_34() {
		runTest("TypeEquals_Valid_34");
	}

	@Test
	public void TypeEquals_Valid_35() {
		runTest("TypeEquals_Valid_35");
	}

	@Ignore("Known Issue") @Test
	public void TypeEquals_Valid_36() {
		runTest("TypeEquals_Valid_36");
	}

	@Ignore("Known Issue") @Test
	public void TypeEquals_Valid_37() {
		runTest("TypeEquals_Valid_37");
	}

	@Ignore("Known Issue") @Test
	public void TypeEquals_Valid_38() {
		runTest("TypeEquals_Valid_38");
	}

	@Test
	public void TypeEquals_Valid_39() {
		runTest("TypeEquals_Valid_39");
	}

	@Test
	public void TypeEquals_Valid_40() {
		runTest("TypeEquals_Valid_40");
	}

	@Ignore("Known Issue") @Test
	public void TypeEquals_Valid_41() {
		runTest("TypeEquals_Valid_41");
	}

	@Test
	public void TypeEquals_Valid_42() {
		runTest("TypeEquals_Valid_42");
	}

	@Test
	public void TypeEquals_Valid_43() {
		runTest("TypeEquals_Valid_43");
	}

	@Test
	public void TypeEquals_Valid_44() {
		runTest("TypeEquals_Valid_44");
	}

	@Test
	public void TypeEquals_Valid_45() {
		runTest("TypeEquals_Valid_45");
	}

	@Test
	public void TypeEquals_Valid_46() {
		runTest("TypeEquals_Valid_46");
	}

	@Test
	public void TypeEquals_Valid_47() {
		runTest("TypeEquals_Valid_47");
	}

	@Test
	public void TypeEquals_Valid_5() {
		runTest("TypeEquals_Valid_5");
	}

	@Test
	public void TypeEquals_Valid_6() {
		runTest("TypeEquals_Valid_6");
	}

	@Test
	public void TypeEquals_Valid_7() {
		runTest("TypeEquals_Valid_7");
	}

	@Test
	public void TypeEquals_Valid_8() {
		runTest("TypeEquals_Valid_8");
	}

	@Test
	public void TypeEquals_Valid_9() {
		runTest("TypeEquals_Valid_9");
	}

	@Test
	public void UnionType_Valid_1() {
		runTest("UnionType_Valid_1");
	}

	@Test
	public void UnionType_Valid_10() {
		runTest("UnionType_Valid_10");
	}

	@Test
	public void UnionType_Valid_11() {
		runTest("UnionType_Valid_11");
	}

	@Test
	public void UnionType_Valid_12() {
		runTest("UnionType_Valid_12");
	}

	@Test
	public void UnionType_Valid_13() {
		runTest("UnionType_Valid_13");
	}

	@Ignore("#298") @Test
	public void UnionType_Valid_14() {
		runTest("UnionType_Valid_14");
	}

	@Test
	public void UnionType_Valid_15() {
		runTest("UnionType_Valid_15");
	}

	@Test
	public void UnionType_Valid_16() {
		runTest("UnionType_Valid_16");
	}

	@Test
	public void UnionType_Valid_17() {
		runTest("UnionType_Valid_17");
	}

	@Ignore("Issue ???") @Test
	public void UnionType_Valid_18() {
		runTest("UnionType_Valid_18");
	}

	@Test
	public void UnionType_Valid_19() {
		runTest("UnionType_Valid_19");
	}

	@Test
	public void UnionType_Valid_2() {
		runTest("UnionType_Valid_2");
	}

	@Ignore("Issue ???") @Test
	public void UnionType_Valid_20() {
		runTest("UnionType_Valid_20");
	}

	@Ignore("Issue ???") @Test
	public void UnionType_Valid_21() {
		runTest("UnionType_Valid_21");
	}

	@Ignore("Issue ???") @Test
	public void UnionType_Valid_22() {
		runTest("UnionType_Valid_22");
	}

	@Test
	public void UnionType_Valid_23() {
		runTest("UnionType_Valid_23");
	}

	@Test
	public void UnionType_Valid_3() {
		runTest("UnionType_Valid_3");
	}

	@Test
	public void UnionType_Valid_4() {
		runTest("UnionType_Valid_4");
	}

	@Test
	public void UnionType_Valid_5() {
		runTest("UnionType_Valid_5");
	}

	@Test
	public void UnionType_Valid_6() {
		runTest("UnionType_Valid_6");
	}

	@Test
	public void UnionType_Valid_7() {
		runTest("UnionType_Valid_7");
	}

	@Test
	public void UnionType_Valid_8() {
		runTest("UnionType_Valid_8");
	}

	@Test
	public void UnionType_Valid_9() {
		runTest("UnionType_Valid_9");
	}

	@Ignore("Known Issue") @Test
	public void Update_Valid_1() {
		runTest("Update_Valid_1");
	}

	@Ignore("Known Issue") @Test
	public void Update_Valid_2() {
		runTest("Update_Valid_2");
	}

	@Test
	public void VarDecl_Valid_1() {
		runTest("VarDecl_Valid_1");
	}

	@Test
	public void VarDecl_Valid_2() {
		runTest("VarDecl_Valid_2");
	}

	@Test
	public void VarDecl_Valid_3() {
		runTest("VarDecl_Valid_3");
	}

	@Test
	public void VarDecl_Valid_4() {
		runTest("VarDecl_Valid_4");
	}

	@Test
	public void While_Valid_1() {
		runTest("While_Valid_1");
	}

	@Test
	public void While_Valid_10() {
		runTest("While_Valid_10");
	}

	@Ignore("#379") @Test
	public void While_Valid_11() {
		runTest("While_Valid_11");
	}

	@Ignore("#379") @Test
	public void While_Valid_12() {
		runTest("While_Valid_12");
	}

	@Test
	public void While_Valid_14() {
		runTest("While_Valid_14");
	}

	@Ignore("#298") @Test
	public void While_Valid_15() {
		runTest("While_Valid_15");
	}

	@Ignore("#229") @Test
	public void While_Valid_16() {
		runTest("While_Valid_16");
	}

	@Test
	public void While_Valid_17() {
		runTest("While_Valid_17");
	}

	@Test
	public void While_Valid_18() {
		runTest("While_Valid_18");
	}

	@Test
	public void While_Valid_19() {
		runTest("While_Valid_19");
	}

	@Ignore("#229") @Test
	public void While_Valid_2() {
		runTest("While_Valid_2");
	}

	@Ignore("#298") @Test
	public void While_Valid_20() {
		runTest("While_Valid_20");
	}

	@Ignore("#231") @Test
	public void While_Valid_21() {
		runTest("While_Valid_21");
	}

	@Ignore("Issue ???") @Test
	public void While_Valid_22() {
		runTest("While_Valid_22");
	}

	@Ignore("#229") @Test
	public void While_Valid_23() {
		runTest("While_Valid_23");
	}

	@Test
	public void While_Valid_24() {
		runTest("While_Valid_24");
	}

	@Test
	public void While_Valid_25() {
		runTest("While_Valid_25");
	}

	@Ignore("#384") @Test
	public void While_Valid_26() {
		runTest("While_Valid_26");
	}

	@Ignore("Issue 378") @Test
	public void While_Valid_27() {
		runTest("While_Valid_27");
	}

	@Test
	public void While_Valid_28() {
		runTest("While_Valid_28");
	}

	@Test
	public void While_Valid_29() {
		runTest("While_Valid_29");
	}

	@Test
	public void While_Valid_30() {
		runTest("While_Valid_30");
	}

	@Test
	public void While_Valid_31() {
		runTest("While_Valid_31");
	}

	@Ignore("378") @Test
	public void While_Valid_32() {
		runTest("While_Valid_32");
	}

	@Test
	public void While_Valid_33() {
		runTest("While_Valid_33");
	}

	@Ignore("#348") @Test
	public void While_Valid_34() {
		runTest("While_Valid_34");
	}

	@Test
	public void While_Valid_35() {
		runTest("While_Valid_35");
	}

	@Test
	public void While_Valid_36() {
		runTest("While_Valid_36");
	}

	@Ignore("#379") @Test
	public void While_Valid_37() {
		runTest("While_Valid_37");
	}

	@Ignore("#379") @Test
	public void While_Valid_38() {
		runTest("While_Valid_38");
	}

	@Ignore("#393") @Test
	public void While_Valid_39() {
		runTest("While_Valid_39");
	}

	@Ignore("#379") @Test
	public void While_Valid_40() {
		runTest("While_Valid_40");
	}

	@Ignore("#379") @Test
	public void While_Valid_41() {
		runTest("While_Valid_41");
	}

	@Ignore("#379") @Test
	public void While_Valid_42() {
		runTest("While_Valid_42");
	}

	@Ignore("#379") @Test
	public void While_Valid_43() {
		runTest("While_Valid_43");
	}

	@Ignore("#231") @Test
	public void While_Valid_44() {
		runTest("While_Valid_44");
	}

	@Test
	public void While_Valid_45() {
		runTest("While_Valid_45");
	}

	@Test
	public void While_Valid_47() {
		runTest("While_Valid_47");
	}

	@Test
	public void While_Valid_46() {
		runTest("While_Valid_46");
	}

	@Test
	public void While_Valid_3() {
		runTest("While_Valid_3");
	}

	@Ignore("#379") @Test
	public void While_Valid_5() {
		runTest("While_Valid_5");
	}

	@Test
	public void While_Valid_7() {
		runTest("While_Valid_7");
	}

	@Test
	public void While_Valid_9() {
		runTest("While_Valid_9");
	}
}
