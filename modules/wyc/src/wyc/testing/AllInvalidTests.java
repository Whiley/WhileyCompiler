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
import java.io.FileWriter;
import java.io.IOException;

import org.junit.*;

import wyc.WycMain;
import wyc.util.WycBuildTask;
import wycc.util.Pair;

/**
 * Run through all invalid test cases with verification enabled. Since every
 * test file is invalid, a successful test occurs when the compiler produces a
 * syntax error for the file. Note that an internal failure does not count as a
 * valid pass, and indicates the test exposed some kind of compiler bug.
 *
 * @author David J. Pearce
 *
 */
public class AllInvalidTests {

	/**
	 * The directory containing the source files for each test case. Every test
	 * corresponds to a file in this directory.
	 */
	public final static String WHILEY_SRC_DIR = "../../tests/invalid".replace('/', File.separatorChar);

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

		Pair<Integer,String> p = TestUtils.compile(
				"-wd", WHILEY_SRC_DIR,      // location of source directory
				"-wp", WYRT_PATH,           // add wyrt to whileypath
				"-verify",                  // enable verification
				filename);                      // name of test to compile

		int r = p.first();
		String output = p.second();

		if (r == WycMain.SUCCESS) {
			// Clearly, the test should not compile.
			fail("Test compiled when it shouldn't have!");
		} else if (r == WycMain.INTERNAL_FAILURE) {
			// This indicates some other kind of internal failure.
			fail("Test caused internal failure!");
		} else {
			// Now, let's check the expected output against the file which
			// contains the sample output for this test
			String sampleOutputFile = WHILEY_SRC_DIR + File.separatorChar + name
					+ ".sysout";

//			Following used when sample output changed.
//			try {
//				FileWriter fw = new FileWriter(sampleOutputFile);
//				fw.write(output);
//				fw.close();
//			} catch(Exception e) {}

	 		// Third, compare the output!
	 		TestUtils.compare(output,sampleOutputFile);
		}
	}

	// ======================================================================
	// Tests
	// ======================================================================

	@Test
	public void Assert_Invalid_1() {
		runTest("Assert_Invalid_1");
	}

	@Test
	public void Assert_Invalid_2() {
		runTest("Assert_Invalid_2");
	}

	@Test
	public void Assign_Invalid_1() {
		runTest("Assign_Invalid_1");
	}

	@Test
	public void Assign_Invalid_2() {
		runTest("Assign_Invalid_2");
	}

	@Test
	public void Assign_Invalid_3() {
		runTest("Assign_Invalid_3");
	}

	@Test
	public void Assign_Invalid_4() {
		runTest("Assign_Invalid_4");
	}

	@Test
	public void Assign_Invalid_5() {
		runTest("Assign_Invalid_5");
	}

	@Test
	public void Assign_Invalid_6() {
		runTest("Assign_Invalid_6");
	}

	@Test
	public void Assign_Invalid_7() {
		runTest("Assign_Invalid_7");
	}

	@Test
	public void Assign_Invalid_8() {
		runTest("Assign_Invalid_8");
	}

	@Test
	public void Assign_Invalid_9() {
		runTest("Assign_Invalid_9");
	}

	@Test
	public void Byte_Invalid_1() {
		runTest("Byte_Invalid_1");
	}

	@Test
	public void Cast_Invalid_1() {
		runTest("Cast_Invalid_1");
	}

	@Test
	public void Cast_Invalid_2() {
		runTest("Cast_Invalid_2");
	}

	@Test
	public void Cast_Invalid_3() {
		runTest("Cast_Invalid_3");
	}

	@Test
	public void Cast_Invalid_4() {
		runTest("Cast_Invalid_4");
	}

	@Test
	public void Coercion_Invalid_1() {
		runTest("Coercion_Invalid_1");
	}

	@Test
	public void Coercion_Invalid_2() {
		runTest("Coercion_Invalid_2");
	}

	@Ignore("unclassified") @Test
	public void Constant_Invalid_1() {
		runTest("Constant_Invalid_1");
	}

	@Test
	public void Constant_Invalid_2() {
		runTest("Constant_Invalid_2");
	}

	@Ignore("unclassified") @Test
	public void ConstrainedDictionary_Invalid_1() {
		runTest("ConstrainedDictionary_Invalid_1");
	}

	@Test
	public void ConstrainedInt_Invalid_1() {
		runTest("ConstrainedInt_Invalid_1");
	}

	@Ignore("unclassified") @Test
	public void ConstrainedInt_Invalid_10() {
		runTest("ConstrainedInt_Invalid_10");
	}

	@Test
	public void ConstrainedInt_Invalid_11() {
		runTest("ConstrainedInt_Invalid_11");
	}

	@Test
	public void ConstrainedInt_Invalid_12() {
		runTest("ConstrainedInt_Invalid_12");
	}

	@Test
	public void ConstrainedInt_Invalid_2() {
		runTest("ConstrainedInt_Invalid_2");
	}

	@Test
	public void ConstrainedInt_Invalid_3() {
		runTest("ConstrainedInt_Invalid_3");
	}

	@Test
	public void ConstrainedInt_Invalid_4() {
		runTest("ConstrainedInt_Invalid_4");
	}

	@Test
	public void ConstrainedInt_Invalid_5() {
		runTest("ConstrainedInt_Invalid_5");
	}

	@Test
	public void ConstrainedInt_Invalid_6() {
		runTest("ConstrainedInt_Invalid_6");
	}

	@Test
	public void ConstrainedInt_Invalid_7() {
		runTest("ConstrainedInt_Invalid_7");
	}

	@Test
	public void ConstrainedInt_Invalid_8() {
		runTest("ConstrainedInt_Invalid_8");
	}

	@Ignore("Timeout") @Test
	public void ConstrainedInt_Invalid_9() {
		runTest("ConstrainedInt_Invalid_9");
	}

	@Test
	public void ConstrainedList_Invalid_1() {
		runTest("ConstrainedList_Invalid_1");
	}

	@Test
	public void ConstrainedList_Invalid_2() {
		runTest("ConstrainedList_Invalid_2");
	}

	@Ignore("unclassified") @Test
	public void ConstrainedList_Invalid_3() {
		runTest("ConstrainedList_Invalid_3");
	}

	@Test
	public void ConstrainedTuple_Invalid_1() {
		runTest("ConstrainedTuple_Invalid_1");
	}

	@Test
	public void Contractive_Invalid_1() {
		runTest("Contractive_Invalid_1");
	}

	@Test
	public void Contractive_Invalid_2() {
		runTest("Contractive_Invalid_2");
	}

	@Test
	public void DefiniteAssign_Invalid_1() {
		runTest("DefiniteAssign_Invalid_1");
	}

	@Test
	public void DefiniteAssign_Invalid_2() {
		runTest("DefiniteAssign_Invalid_2");
	}

	@Test
	public void DefiniteAssign_Invalid_3() {
		runTest("DefiniteAssign_Invalid_3");
	}

	@Test
	public void DefiniteAssign_Invalid_4() {
		runTest("DefiniteAssign_Invalid_4");
	}

	@Test
	public void EndOfFile_Invalid_1() {
		runTest("EndOfFile_Invalid_1");
	}

	@Test
	public void Ensures_Invalid_1() {
		runTest("Ensures_Invalid_1");
	}

	@Test
	public void Ensures_Invalid_2() {
		runTest("Ensures_Invalid_2");
	}

	@Test
	public void Ensures_Invalid_3() {
		runTest("Ensures_Invalid_3");
	}

	@Ignore("unclassified") @Test
	public void Export_Invalid_1() {
		runTest("Export_Invalid_1");
	}

	@Test
	public void For_Invalid_1() {
		runTest("For_Invalid_1");
	}

	@Test
	public void For_Invalid_5() {
		runTest("For_Invalid_5");
	}

	@Test
	public void For_Invalid_6() {
		runTest("For_Invalid_6");
	}

	@Test
	public void For_Invalid_7() {
		runTest("For_Invalid_7");
	}

	@Test
	public void For_Invalid_8() {
		runTest("For_Invalid_8");
	}

	@Ignore("#409") @Test
	public void For_Invalid_9() {
		runTest("For_Invalid_9");
	}


	@Test
	public void FunctionRef_Invalid_1() {
		runTest("FunctionRef_Invalid_1");
	}

	@Test
	public void FunctionRef_Invalid_2() {
		runTest("FunctionRef_Invalid_2");
	}

	@Test
	public void FunctionRef_Invalid_3() {
		runTest("FunctionRef_Invalid_3");
	}

	@Test
	public void FunctionRef_Invalid_4() {
		runTest("FunctionRef_Invalid_4");
	}

	@Test
	public void FunctionRef_Invalid_5() {
		runTest("FunctionRef_Invalid_5");
	}

	@Test
	public void FunctionRef_Invalid_6() {
		runTest("FunctionRef_Invalid_6");
	}

	@Test
	public void FunctionRef_Invalid_7() {
		runTest("FunctionRef_Invalid_7");
	}

	@Test
	public void Function_Invalid_1() {
		runTest("Function_Invalid_1");
	}

	@Test
	public void Function_Invalid_10() {
		runTest("Function_Invalid_10");
	}

	@Ignore("Internal Failure") @Test
	public void Function_Invalid_2() {
		runTest("Function_Invalid_2");
	}

	@Test
	public void Function_Invalid_3() {
		runTest("Function_Invalid_3");
	}

	@Test
	public void Function_Invalid_4() {
		runTest("Function_Invalid_4");
	}

	@Ignore("unclassified") @Test
	public void Function_Invalid_9() {
		runTest("Function_Invalid_9");
	}

	@Test
	public void If_Invalid_1() {
		runTest("If_Invalid_1");
	}

	@Test
	public void If_Invalid_2() {
		runTest("If_Invalid_2");
	}

	@Test
	public void If_Invalid_3() {
		runTest("If_Invalid_3");
	}

	@Test
	public void If_Invalid_4() {
		runTest("If_Invalid_4");
	}

	@Test
	public void If_Invalid_5() {
		runTest("If_Invalid_5");
	}

	@Test
	public void Import_Invalid_1() {
		runTest("Import_Invalid_1");
	}

	@Test
	public void IntDiv_Invalid_1() {
		runTest("IntDiv_Invalid_1");
	}

	@Ignore("unclassified") @Test
	public void Intersection_Invalid_1() {
		runTest("Intersection_Invalid_1");
	}

	@Ignore("unclassified") @Test
	public void Intersection_Invalid_2() {
		runTest("Intersection_Invalid_2");
	}

	@Ignore("unclassified") @Test
	public void Lambda_Invalid_3() {
		runTest("Lambda_Invalid_3");
	}

	@Test
	public void ListAccess_Invalid_1() {
		runTest("ListAccess_Invalid_1");
	}

	@Test
	public void ListAccess_Invalid_2() {
		runTest("ListAccess_Invalid_2");
	}

	@Test
	public void ListAccess_Invalid_3() {
		runTest("ListAccess_Invalid_3");
	}

	@Test
	public void ListAccess_Invalid_4() {
		runTest("ListAccess_Invalid_4");
	}

	@Test
	public void ListAccess_Invalid_5() {
		runTest("ListAccess_Invalid_5");
	}

	@Test
	public void ListAppend_Invalid_1() {
		runTest("ListAppend_Invalid_1");
	}

	@Test
	public void ListAppend_Invalid_2() {
		runTest("ListAppend_Invalid_2");
	}

	@Ignore("Unknown Issue") @Test
	public void ListAppend_Invalid_3() {
		runTest("ListAppend_Invalid_3");
	}

	@Ignore("Internal Failure") @Test
	public void ListAppend_Invalid_4() {
		runTest("ListAppend_Invalid_4");
	}

	@Ignore("Internal Failure") @Test
	public void ListAppend_Invalid_5() {
		runTest("ListAppend_Invalid_5");
	}

	@Test
	public void ListAssign_Invalid_1() {
		runTest("ListAssign_Invalid_1");
	}

	@Ignore("Infinite Loop?") @Test
	public void ListAssign_Invalid_2() {
		runTest("ListAssign_Invalid_2");
	}

	@Ignore("Infinite Loop?") @Test
	public void ListAssign_Invalid_3() {
		runTest("ListAssign_Invalid_3");
	}

	@Test
	public void ListConversion_Invalid_1() {
		runTest("ListConversion_Invalid_1");
	}

	@Test
	public void ListElemOf_Invalid_1() {
		runTest("ListElemOf_Invalid_1");
	}

	@Test
	public void ListElemOf_Invalid_2() {
		runTest("ListElemOf_Invalid_2");
	}

	@Test
	public void ListEmpty_Invalid_1() {
		runTest("ListEmpty_Invalid_1");
	}

	@Test
	public void ListEmpty_Invalid_2() {
		runTest("ListEmpty_Invalid_2");
	}

	@Test
	public void ListEquals_Invalid_1() {
		runTest("ListEquals_Invalid_1");
	}

	@Test
	public void ListLength_Invalid_1() {
		runTest("ListLength_Invalid_1");
	}

	@Ignore("Timeout") @Test
	public void ListLength_Invalid_2() {
		runTest("ListLength_Invalid_2");
	}

	@Test
	public void ListLength_Invalid_3() {
		runTest("ListLength_Invalid_3");
	}

	@Test
	public void ListSublist_Invalid_1() {
		runTest("ListSublist_Invalid_1");
	}

	@Ignore("unclassified") @Test
	public void ListSublist_Invalid_2() {
		runTest("ListSublist_Invalid_2");
	}

	@Test
	public void ListSublist_Invalid_3() {
		runTest("ListSublist_Invalid_3");
	}

	@Ignore("unclassified") @Test
	public void ListUpdate_Invalid_1() {
		runTest("ListUpdate_Invalid_1");
	}

	@Test
	public void List_Invalid_1() {
		runTest("List_Invalid_1");
	}

	@Test
	public void List_Invalid_2() {
		runTest("List_Invalid_2");
	}

	@Test
	public void List_Invalid_3() {
		runTest("List_Invalid_3");
	}

	@Test
	public void List_Invalid_4() {
		runTest("List_Invalid_4");
	}

	@Test
	public void List_Invalid_5() {
		runTest("List_Invalid_5");
	}

	@Test
	public void List_Invalid_6() {
		runTest("List_Invalid_6");
	}

	@Test
	public void List_Invalid_7() {
		runTest("List_Invalid_7");
	}


	@Test
	public void MethodCall_Invalid_1() {
		runTest("MethodCall_Invalid_1");
	}

	@Test
	public void MethodCall_Invalid_2() {
		runTest("MethodCall_Invalid_2");
	}

	@Test
	public void MethodCall_Invalid_3() {
		runTest("MethodCall_Invalid_3");
	}

	@Test
	public void MethodCall_Invalid_4() {
		runTest("MethodCall_Invalid_4");
	}

	@Test
	public void MethodCall_Invalid_5() {
		runTest("MethodCall_Invalid_5");
	}

	@Test
	public void MethodCall_Invalid_6() {
		runTest("MethodCall_Invalid_6");
	}

	@Test
	public void MethodCall_Invalid_7() {
		runTest("MethodCall_Invalid_7");
	}

	@Test
	public void MethodCall_Invalid_8() {
		runTest("MethodCall_Invalid_8");
	}

	@Ignore("unclassified") @Test
	public void MethodRef_Invalid_1() {
		runTest("MethodRef_Invalid_1");
	}

	@Ignore("Internal Failure") @Test
	public void MethodRef_Invalid_2() {
		runTest("MethodRef_Invalid_2");
	}

	@Ignore("unclassified") @Test
	public void MethodRef_Invalid_3() {
		runTest("MethodRef_Invalid_3");
	}

	@Ignore("unclassified") @Test
	public void Native_Invalid_1() {
		runTest("Native_Invalid_1");
	}

	@Test
	public void NegationType_Invalid_1() {
		runTest("NegationType_Invalid_1");
	}

	@Ignore("unclassified") @Test
	public void NegationType_Invalid_2() {
		runTest("NegationType_Invalid_2");
	}

	@Test
	public void NegationType_Invalid_3() {
		runTest("NegationType_Invalid_3");
	}

	@Test
	public void OpenRecord_Invalid_1() {
		runTest("OpenRecord_Invalid_1");
	}

	@Ignore("unclassified") @Test
	public void OpenRecord_Invalid_2() {
		runTest("OpenRecord_Invalid_2");
	}

	@Test
	public void OpenRecord_Invalid_3() {
		runTest("OpenRecord_Invalid_3");
	}

	@Test
	public void OpenRecord_Invalid_4() {
		runTest("OpenRecord_Invalid_4");
	}

	@Test
	public void OpenRecord_Invalid_5() {
		runTest("OpenRecord_Invalid_5");
	}

	@Test
	public void OpenRecord_Invalid_6() {
		runTest("OpenRecord_Invalid_6");
	}

	@Test
	public void OpenRecord_Invalid_7() {
		runTest("OpenRecord_Invalid_7");
	}

	@Test
	public void Parameter_Invalid_1() {
		runTest("Parameter_Invalid_1");
	}

	@Test
	public void Parameter_Invalid_2() {
		runTest("Parameter_Invalid_2");
	}

	@Test
	public void ProcessAccess_Invalid_1() {
		runTest("ProcessAccess_Invalid_1");
	}

	@Test
	public void ProcessAccess_Invalid_2() {
		runTest("ProcessAccess_Invalid_2");
	}

	@Test
	public void ProcessAccess_Invalid_3() {
		runTest("ProcessAccess_Invalid_3");
	}

	@Test
	public void Process_Invalid_1() {
		runTest("Process_Invalid_1");
	}

	@Test
	public void Process_Invalid_2() {
		runTest("Process_Invalid_2");
	}

	@Test
	public void Process_Invalid_3() {
		runTest("Process_Invalid_3");
	}

	@Test
	public void Quantifiers_Invalid_1() {
		runTest("Quantifiers_Invalid_1");
	}

	@Test
	public void Quantifiers_Invalid_2() {
		runTest("Quantifiers_Invalid_2");
	}

	@Test
	public void Quantifiers_Invalid_3() {
		runTest("Quantifiers_Invalid_3");
	}

	@Test
	public void Quantifiers_Invalid_4() {
		runTest("Quantifiers_Invalid_4");
	}

	@Test
	public void Quantifiers_Invalid_5() {
		runTest("Quantifiers_Invalid_5");
	}

	@Test
	public void Quantifiers_Invalid_6() {
		runTest("Quantifiers_Invalid_6");
	}

	@Test
	public void Quantifiers_Invalid_7() {
		runTest("Quantifiers_Invalid_7");
	}

	@Test
	public void Quantifiers_Invalid_8() {
		runTest("Quantifiers_Invalid_8");
	}

	@Test
	public void Rational_Invalid_1() {
		runTest("Rational_Invalid_1");
	}

	@Test
	public void Rational_Invalid_2() {
		runTest("Rational_Invalid_2");
	}

	@Test
	public void RealAdd_Invalid_1() {
		runTest("RealAdd_Invalid_1");
	}

	@Test
	public void RealConvert_Invalid_1() {
		runTest("RealConvert_Invalid_1");
	}

	@Test
	public void RealConvert_Invalid_2() {
		runTest("RealConvert_Invalid_2");
	}

	@Test
	public void RealDiv_Invalid_1() {
		runTest("RealDiv_Invalid_1");
	}

	@Test
	public void RealDiv_Invalid_2() {
		runTest("RealDiv_Invalid_2");
	}

	@Ignore("Internal Failure") @Test
	public void RealMul_Invalid_1() {
		runTest("RealMul_Invalid_1");
	}

	@Test
	public void Record_Invalid_1() {
		runTest("Record_Invalid_1");
	}

	@Test
	public void Record_Invalid_2() {
		runTest("Record_Invalid_2");
	}

	@Test
	public void Record_Invalid_3() {
		runTest("Record_Invalid_3");
	}

	@Ignore("unclassified") @Test
	public void RecursiveType_Invalid_1() {
		runTest("RecursiveType_Invalid_1");
	}

	@Ignore("unclassified") @Test
	public void RecursiveType_Invalid_10() {
		runTest("RecursiveType_Invalid_10");
	}

	@Ignore("unclassified") @Test
	public void RecursiveType_Invalid_2() {
		runTest("RecursiveType_Invalid_2");
	}

	@Test
	public void RecursiveType_Invalid_3() {
		runTest("RecursiveType_Invalid_3");
	}

	@Ignore("unclassified") @Test
	public void RecursiveType_Invalid_4() {
		runTest("RecursiveType_Invalid_4");
	}

	@Test
	public void RecursiveType_Invalid_5() {
		runTest("RecursiveType_Invalid_5");
	}

	@Ignore("unclassified") @Test
	public void RecursiveType_Invalid_7() {
		runTest("RecursiveType_Invalid_7");
	}

	@Ignore("unclassified") @Test
	public void RecursiveType_Invalid_8() {
		runTest("RecursiveType_Invalid_8");
	}

	@Ignore("unclassified") @Test
	public void RecursiveType_Invalid_9() {
		runTest("RecursiveType_Invalid_9");
	}

	@Test
	public void Remainder_Invalid_1() {
		runTest("Remainder_Invalid_1");
	}

	@Test
	public void Remainder_Invalid_2() {
		runTest("Remainder_Invalid_2");
	}

	@Test
	public void Remainder_Invalid_3() {
		runTest("Remainder_Invalid_3");
	}

	@Test
	public void Requires_Invalid_1() {
		runTest("Requires_Invalid_1");
	}

	@Test
	public void Return_Invalid_1() {
		runTest("Return_Invalid_1");
	}

	@Test
	public void Return_Invalid_10() {
		runTest("Return_Invalid_10");
	}

	@Test
	public void Return_Invalid_11() {
		runTest("Return_Invalid_11");
	}

	@Test
	public void Return_Invalid_2() {
		runTest("Return_Invalid_2");
	}

	@Test
	public void Return_Invalid_3() {
		runTest("Return_Invalid_3");
	}

	@Test
	public void Return_Invalid_4() {
		runTest("Return_Invalid_4");
	}

	@Test
	public void Return_Invalid_5() {
		runTest("Return_Invalid_5");
	}

	@Test
	public void Return_Invalid_6() {
		runTest("Return_Invalid_6");
	}

	@Test
	public void Return_Invalid_7() {
		runTest("Return_Invalid_7");
	}

	@Test
	public void Return_Invalid_8() {
		runTest("Return_Invalid_8");
	}

	@Test
	public void Return_Invalid_9() {
		runTest("Return_Invalid_9");
	}

	@Test
	public void Subtype_Invalid_1() {
		runTest("Subtype_Invalid_1");
	}

	@Test
	public void Subtype_Invalid_2() {
		runTest("Subtype_Invalid_2");
	}

	@Test
	public void Subtype_Invalid_3() {
		runTest("Subtype_Invalid_3");
	}

	@Test
	public void Subtype_Invalid_4() {
		runTest("Subtype_Invalid_4");
	}

	@Test
	public void Subtype_Invalid_5() {
		runTest("Subtype_Invalid_5");
	}

	@Test
	public void Subtype_Invalid_6() {
		runTest("Subtype_Invalid_6");
	}

	@Test
	public void Subtype_Invalid_7() {
		runTest("Subtype_Invalid_7");
	}

	@Ignore("Infinite Loop") @Test
	public void Subtype_Invalid_8() {
		runTest("Subtype_Invalid_8");
	}

	@Ignore("timeout") @Test
	public void Subtype_Invalid_9() {
		runTest("Subtype_Invalid_9");
	}

	@Test
	public void Switch_Invalid_1() {
		runTest("Switch_Invalid_1");
	}

	@Test
	public void Switch_Invalid_2() {
		runTest("Switch_Invalid_2");
	}

	@Test
	public void Switch_Invalid_3() {
		runTest("Switch_Invalid_3");
	}

	@Test
	public void Switch_Invalid_4() {
		runTest("Switch_Invalid_4");
	}

	@Test
	public void Switch_Invalid_5() {
		runTest("Switch_Invalid_5");
	}

	@Ignore("unclassified") @Test
	public void Switch_Invalid_6() {
		runTest("Switch_Invalid_6");
	}

	@Test
	public void Switch_Invalid_7() {
		runTest("Switch_Invalid_7");
	}

	@Test
	public void TupleAssign_Invalid_1() {
		runTest("TupleAssign_Invalid_1");
	}

	@Test
	public void TupleAssign_Invalid_2() {
		runTest("TupleAssign_Invalid_2");
	}

	@Test
	public void TupleAssign_Invalid_3() {
		runTest("TupleAssign_Invalid_3");
	}

	@Test
	public void TupleDefine_Invalid_1() {
		runTest("TupleDefine_Invalid_1");
	}

	@Test
	public void TupleDefine_Invalid_2() {
		runTest("TupleDefine_Invalid_2");
	}

	@Test
	public void Tuple_Invalid_1() {
		runTest("Tuple_Invalid_1");
	}

	@Test
	public void Tuple_Invalid_3() {
		runTest("Tuple_Invalid_3");
	}

	@Test
	public void Tuple_Invalid_4() {
		runTest("Tuple_Invalid_4");
	}

	@Test
	public void Tuple_Invalid_5() {
		runTest("Tuple_Invalid_5");
	}

	@Test
	public void Tuple_Invalid_6() {
		runTest("Tuple_Invalid_6");
	}

	@Test
	public void Tuple_Invalid_7() {
		runTest("Tuple_Invalid_7");
	}

	@Test
	public void TypeEquals_Invalid_1() {
		runTest("TypeEquals_Invalid_1");
	}

	@Test
	public void TypeEquals_Invalid_2() {
		runTest("TypeEquals_Invalid_2");
	}

	@Ignore("unclassified") @Test
	public void TypeEquals_Invalid_5() {
		runTest("TypeEquals_Invalid_5");
	}

	@Test
	public void TypeEquals_Invalid_6() {
		runTest("TypeEquals_Invalid_6");
	}

	@Test
	public void UnionType_Invalid_1() {
		runTest("UnionType_Invalid_1");
	}

	@Test
	public void UnionType_Invalid_10() {
		runTest("UnionType_Invalid_10");
	}

	@Test
	public void UnionType_Invalid_2() {
		runTest("UnionType_Invalid_2");
	}

	@Test
	public void UnionType_Invalid_3() {
		runTest("UnionType_Invalid_3");
	}

	@Test
	public void UnionType_Invalid_4() {
		runTest("UnionType_Invalid_4");
	}

	@Test
	public void UnionType_Invalid_5() {
		runTest("UnionType_Invalid_5");
	}

	@Test
	public void UnionType_Invalid_6() {
		runTest("UnionType_Invalid_6");
	}

	@Test
	public void UnionType_Invalid_7() {
		runTest("UnionType_Invalid_7");
	}

	@Ignore("unclassified") @Test
	public void UnionType_Invalid_8() {
		runTest("UnionType_Invalid_8");
	}

	@Ignore("#348") @Test
	public void UnionType_Invalid_9() {
		runTest("UnionType_Invalid_9");
	}

	@Test
	public void VarDecl_Invalid_1() {
		runTest("VarDecl_Invalid_1");
	}

	@Test
	public void VarDecl_Invalid_2() {
		runTest("VarDecl_Invalid_2");
	}

	@Test
	public void VarDecl_Invalid_3() {
		runTest("VarDecl_Invalid_3");
	}

	@Ignore("unclassified") @Test
	public void Void_Invalid_1() {
		runTest("Void_Invalid_1");
	}

	@Ignore("unclassified") @Test
	public void Void_Invalid_2() {
		runTest("Void_Invalid_2");
	}

	@Ignore("unclassified") @Test
	public void Void_Invalid_3() {
		runTest("Void_Invalid_3");
	}

	@Test
	public void While_Invalid_1() {
		runTest("While_Invalid_1");
	}

	@Test
	public void While_Invalid_10() {
		runTest("While_Invalid_10");
	}

	@Test
	public void While_Invalid_11() {
		runTest("While_Invalid_11");
	}

	@Test
	public void While_Invalid_12() {
		runTest("While_Invalid_12");
	}

	@Test
	public void While_Invalid_13() {
		runTest("While_Invalid_13");
	}

	@Test
	public void While_Invalid_2() {
		runTest("While_Invalid_2");
	}

	@Test
	public void While_Invalid_3() {
		runTest("While_Invalid_3");
	}

	@Test
	public void While_Invalid_4() {
		runTest("While_Invalid_4");
	}

	@Test
	public void While_Invalid_5() {
		runTest("While_Invalid_5");
	}

	@Test
	public void While_Invalid_6() {
		runTest("While_Invalid_6");
	}

	@Test
	public void While_Invalid_7() {
		runTest("While_Invalid_7");
	}

	@Test
	public void While_Invalid_8() {
		runTest("While_Invalid_8");
	}

	@Test
	public void While_Invalid_9() {
		runTest("While_Invalid_9");
	}

	@Test
	public void XOR_Invalid_1() {
		runTest("XOR_Invalid_1");
	}
}
