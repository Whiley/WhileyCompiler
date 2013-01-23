// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute
// it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public
// License along with the Whiley-to-Java Compiler. If not, see
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce.

package wyc.testing.tests;

import org.junit.*;

import wyc.testing.TestHarness;

public class ExtendedValidTests extends TestHarness {
 public ExtendedValidTests() {
	 super("../../tests/ext/valid","../../tests/ext/valid","sysout");	
 }

 @Test public void Assume_Valid_1_RuntimeTest() { verifyPassTest("Assume_Valid_1"); }
 @Test public void BoolAssign_Valid_3_RuntimeTest() { verifyPassTest("BoolAssign_Valid_3"); }
 @Test public void BoolAssign_Valid_4_RuntimeTest() { verifyPassTest("BoolAssign_Valid_4"); }
 @Test public void BoolRequires_Valid_1_RuntimeTest() { verifyPassTest("BoolRequires_Valid_1"); }
 @Ignore("Known Issue") @Test public void Complex_Valid_3_RuntimeTest() { verifyPassTest("Complex_Valid_3"); }
 @Ignore("Issue #233") @Test public void Complex_Valid_4_RuntimeTest() { verifyPassTest("Complex_Valid_4"); }
 @Ignore("Requires Maps") @Test public void ConstrainedDictionary_Valid_1_RuntimeTest() { verifyPassTest("ConstrainedDictionary_Valid_1"); }
 @Test public void ConstrainedInt_Valid_1_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_1"); }
 @Test public void ConstrainedInt_Valid_10_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_10"); }
 @Test public void ConstrainedInt_Valid_11_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_11"); }
 @Test public void ConstrainedInt_Valid_12_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_12"); }
 @Ignore("Requires Modulus") @Test public void ConstrainedInt_Valid_13_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_13"); }
 @Test public void ConstrainedInt_Valid_3_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_3"); }
 @Test public void ConstrainedInt_Valid_4_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_4"); }
 @Test public void ConstrainedInt_Valid_5_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_5"); }
 @Test public void ConstrainedInt_Valid_6_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_6"); }
 @Test public void ConstrainedInt_Valid_7_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_7"); }
 @Test public void ConstrainedInt_Valid_8_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_8"); }
 @Test public void ConstrainedInt_Valid_9_RuntimeTest() { verifyPassTest("ConstrainedInt_Valid_9"); }
 @Test public void ConstrainedList_Valid_1_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_1"); }
 @Test public void ConstrainedList_Valid_2_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_2"); }
 @Test public void ConstrainedList_Valid_3_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_3"); }
 @Test public void ConstrainedList_Valid_4_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_4"); }
 @Ignore("Issue #210") @Test public void ConstrainedList_Valid_5_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_5"); }
 @Ignore("Issue #210") @Test public void ConstrainedList_Valid_6_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_6"); }
 @Test public void ConstrainedList_Valid_7_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_7"); }
 @Ignore("Issue #209") @Test public void ConstrainedList_Valid_8_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_8"); }
 @Test public void ConstrainedList_Valid_9_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_9"); }
 @Test public void ConstrainedList_Valid_10_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_10"); }
 @Ignore("Issue #229") @Test public void ConstrainedList_Valid_11_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_11"); }
 @Ignore("Issue #228") @Test public void ConstrainedList_Valid_12_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_12"); }
 @Ignore("Issue #230") @Test public void ConstrainedList_Valid_13_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_13"); }
 @Ignore("Issue #229") @Test public void ConstrainedList_Valid_14_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_14"); }
 @Ignore("Issue #230") @Test public void ConstrainedList_Valid_15_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_15"); }
 @Ignore("Issue #229") @Test public void ConstrainedList_Valid_16_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_16"); }
 @Ignore("Issue #210") @Test public void ConstrainedList_Valid_17_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_17"); }
 @Ignore("Issue #229") @Test public void ConstrainedList_Valid_18_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_18"); }
 @Ignore("Issue #231") @Test public void ConstrainedList_Valid_19_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_19"); }
 @Ignore("Unclassified") @Test public void ConstrainedList_Valid_20_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_20"); }
 @Ignore("Issue #225") @Test public void ConstrainedList_Valid_21_RuntimeTest() { verifyPassTest("ConstrainedList_Valid_21"); }
 @Ignore("Future Work") @Test public void ConstrainedNegation_Valid_1_RuntimeTest() { verifyPassTest("ConstrainedNegation_Valid_1"); }
 @Test public void ConstrainedRecord_Valid_4_RuntimeTest() { verifyPassTest("ConstrainedRecord_Valid_4"); }
 @Test public void ConstrainedRecord_Valid_5_RuntimeTest() { verifyPassTest("ConstrainedRecord_Valid_5"); }
 @Test public void ConstrainedRecord_Valid_6_RuntimeTest() { verifyPassTest("ConstrainedRecord_Valid_6"); }
 @Ignore("Unclassified") @Test public void ConstrainedRecord_Valid_7_RuntimeTest() { verifyPassTest("ConstrainedRecord_Valid_7"); }
 @Test public void ConstrainedSet_Valid_1_RuntimeTest() { verifyPassTest("ConstrainedSet_Valid_1"); }
 @Test public void ConstrainedSet_Valid_2_RuntimeTest() { verifyPassTest("ConstrainedSet_Valid_2"); }
 @Test public void ConstrainedSet_Valid_3_RuntimeTest() { verifyPassTest("ConstrainedSet_Valid_3"); }
 @Test public void ConstrainedSet_Valid_4_RuntimeTest() { verifyPassTest("ConstrainedSet_Valid_4"); }
 @Test public void ConstrainedTuple_Valid_1_RuntimeTest() { verifyPassTest("ConstrainedTuple_Valid_1"); }
 @Test public void Ensures_Valid_1_RuntimeTest() { verifyPassTest("Ensures_Valid_1"); }
 @Test public void Ensures_Valid_2_RuntimeTest() { verifyPassTest("Ensures_Valid_2"); }
 @Ignore("Issue #234") @Test public void Ensures_Valid_3_RuntimeTest() { verifyPassTest("Ensures_Valid_3"); }
 @Test public void Ensures_Valid_4_RuntimeTest() { verifyPassTest("Ensures_Valid_4"); }
 @Test public void Ensures_Valid_5_RuntimeTest() { verifyPassTest("Ensures_Valid_5"); }
 @Test public void For_Valid_2_RuntimeTest() { verifyPassTest("For_Valid_2"); }
 @Test public void For_Valid_3_RuntimeTest() { verifyPassTest("For_Valid_3"); }
 @Test public void For_Valid_4_RuntimeTest() { verifyPassTest("For_Valid_4"); }
 @Test public void For_Valid_5_RuntimeTest() { verifyPassTest("For_Valid_5"); }
 @Ignore("Requires Overloading") @Test public void Function_Valid_11_RuntimeTest() { verifyPassTest("Function_Valid_11"); }
 @Test public void Function_Valid_12_RuntimeTest() { verifyPassTest("Function_Valid_12"); }
 @Test public void Function_Valid_14_RuntimeTest() { verifyPassTest("Function_Valid_14"); }
 @Ignore("Issue #230") @Test public void Function_Valid_15_RuntimeTest() { verifyPassTest("Function_Valid_15"); }
 @Test public void Function_Valid_2_RuntimeTest() { verifyPassTest("Function_Valid_2"); }
 @Test public void Function_Valid_3_RuntimeTest() { verifyPassTest("Function_Valid_3"); }
 @Test public void Function_Valid_4_RuntimeTest() { verifyPassTest("Function_Valid_4"); }
 @Ignore("Issue #234") @Test public void Function_Valid_5_RuntimeTest() { verifyPassTest("Function_Valid_5"); }
 @Ignore("Issue #234") @Test public void Function_Valid_6_RuntimeTest() { verifyPassTest("Function_Valid_6"); }
 @Ignore("Requires Overloading") @Test public void Function_Valid_8_RuntimeTest() { verifyPassTest("Function_Valid_8"); }
 @Test public void Function_Valid_9_RuntimeTest() { verifyPassTest("Function_Valid_9"); } 
 @Test public void IntDefine_Valid_1_RuntimeTest() { verifyPassTest("IntDefine_Valid_1"); }
 @Test public void IntDiv_Valid_1_RuntimeTest() { verifyPassTest("IntDiv_Valid_1"); }
 @Ignore("Requires Lambdas") @Test public void Lambda_Valid_1_RuntimeTest() { verifyPassTest("Lambda_Valid_1"); }
 @Test public void ListAccess_Valid_1_RuntimeTest() { verifyPassTest("ListAccess_Valid_1"); }
 @Test public void ListAccess_Valid_2_RuntimeTest() { verifyPassTest("ListAccess_Valid_2"); }
 @Test public void ListAppend_Valid_5_RuntimeTest() { verifyPassTest("ListAppend_Valid_5"); }
 @Ignore("Issue #231") @Test public void ListAppend_Valid_6_RuntimeTest() { verifyPassTest("ListAppend_Valid_6"); }
 @Test public void ListAppend_Valid_7_RuntimeTest() { verifyPassTest("ListAppend_Valid_7"); }
 @Ignore("Issue #231") @Test public void ListAppend_Valid_8_RuntimeTest() { verifyPassTest("ListAppend_Valid_8"); }
 @Ignore("Issue #231") @Test public void ListAppend_Valid_9_RuntimeTest() { verifyPassTest("ListAppend_Valid_9"); }
 @Test public void ListAssign_Valid_5_RuntimeTest() { verifyPassTest("ListAssign_Valid_5"); }
 @Test public void ListAssign_Valid_6_RuntimeTest() { verifyPassTest("ListAssign_Valid_6"); }
 @Test public void ListGenerator_Valid_1_RuntimeTest() { verifyPassTest("ListGenerator_Valid_1"); }
 @Test public void ListGenerator_Valid_2_RuntimeTest() { verifyPassTest("ListGenerator_Valid_2"); }
 @Test public void ListLength_Valid_1_RuntimeTest() { verifyPassTest("ListLength_Valid_1"); }
 @Ignore("Issue #232") @Test public void ListSublist_Valid_1_RuntimeTest() { verifyPassTest("ListSublist_Valid_1"); }
 @Ignore("Issue #233") @Test public void Method_Valid_1_RuntimeTest() { verifyPassTest("Method_Valid_1"); }
 @Test public void Process_Valid_2_RuntimeTest() { verifyPassTest("Process_Valid_2"); }
 @Test public void Quantifiers_Valid_1_RuntimeTest() { verifyPassTest("Quantifiers_Valid_1"); }
 @Ignore("Issue #229") @Test public void Range_Valid_1_RuntimeTest() { verifyPassTest("Range_Valid_1"); }
 @Test public void RealDiv_Valid_1_RuntimeTest() { verifyPassTest("RealDiv_Valid_1"); }
 @Test public void RealDiv_Valid_2_RuntimeTest() { verifyPassTest("RealDiv_Valid_2"); }
 @Ignore("Known Issue") @Test public void RealDiv_Valid_3_RuntimeTest() { verifyPassTest("RealDiv_Valid_3"); }
 @Test public void RealNeg_Valid_1_RuntimeTest() { verifyPassTest("RealNeg_Valid_1"); }
 @Test public void RealSub_Valid_1_RuntimeTest() { verifyPassTest("RealSub_Valid_1"); }
 @Test public void RecordAssign_Valid_1_RuntimeTest() { verifyPassTest("RecordAssign_Valid_1"); }
 @Test public void RecordAssign_Valid_2_RuntimeTest() { verifyPassTest("RecordAssign_Valid_2"); }
 @Test public void RecordAssign_Valid_4_RuntimeTest() { verifyPassTest("RecordAssign_Valid_4"); }
 @Test public void RecordAssign_Valid_5_RuntimeTest() { verifyPassTest("RecordAssign_Valid_5"); }
 @Test public void RecordDefine_Valid_1_RuntimeTest() { verifyPassTest("RecordDefine_Valid_1"); }
 @Test public void RecursiveType_Valid_3_RuntimeTest() { verifyPassTest("RecursiveType_Valid_3"); }
 @Test public void RecursiveType_Valid_5_RuntimeTest() { verifyPassTest("RecursiveType_Valid_5"); }
 @Test public void RecursiveType_Valid_6_RuntimeTest() { verifyPassTest("RecursiveType_Valid_6"); }
 @Test public void RecursiveType_Valid_8_RuntimeTest() { verifyPassTest("RecursiveType_Valid_8"); }
 @Ignore("Requires Type Test") @Test public void RecursiveType_Valid_9_RuntimeTest() { verifyPassTest("RecursiveType_Valid_9"); }
 @Test public void Requires_Valid_1_RuntimeTest() { verifyPassTest("Requires_Valid_1"); }
 @Test public void SetAssign_Valid_1_RuntimeTest() { verifyPassTest("SetAssign_Valid_1"); }
 @Ignore("Issue #234") @Test public void SetComprehension_Valid_6_RuntimeTest() { verifyPassTest("SetComprehension_Valid_6"); }
 @Test public void SetComprehension_Valid_7_RuntimeTest() { verifyPassTest("SetComprehension_Valid_7"); }
 @Test public void SetDefine_Valid_1_RuntimeTest() { verifyPassTest("SetDefine_Valid_1"); }
 @Ignore("Known Issue") @Test public void SetIntersection_Valid_1_RuntimeTest() { verifyPassTest("SetIntersection_Valid_1"); }
 @Ignore("Known Issue") @Test public void SetIntersection_Valid_2_RuntimeTest() { verifyPassTest("SetIntersection_Valid_2"); }
 @Ignore("Known Issue") @Test public void SetIntersection_Valid_3_RuntimeTest() { verifyPassTest("SetIntersection_Valid_3"); }
 @Test public void SetSubset_Valid_1_RuntimeTest() { verifyPassTest("SetSubset_Valid_1"); }
 @Test public void SetSubset_Valid_2_RuntimeTest() { verifyPassTest("SetSubset_Valid_2"); }
 @Test public void SetSubset_Valid_3_RuntimeTest() { verifyPassTest("SetSubset_Valid_3"); }
 @Test public void SetSubset_Valid_4_RuntimeTest() { verifyPassTest("SetSubset_Valid_4"); }
 @Test public void SetSubset_Valid_5_RuntimeTest() { verifyPassTest("SetSubset_Valid_5"); }
 @Test public void SetSubset_Valid_6_RuntimeTest() { verifyPassTest("SetSubset_Valid_6"); }
 @Test public void SetSubset_Valid_7_RuntimeTest() { verifyPassTest("SetSubset_Valid_7"); }
 @Ignore("Issue #235") @Test public void SetUnion_Valid_5_RuntimeTest() { verifyPassTest("SetUnion_Valid_5"); }
 @Test public void SetUnion_Valid_6_RuntimeTest() { verifyPassTest("SetUnion_Valid_6"); }
 @Test public void String_Valid_1_RuntimeTest() { verifyPassTest("String_Valid_1"); }
 @Ignore("Issue #232") @Test public void SubList_Valid_1_RuntimeTest() { verifyPassTest("SubList_Valid_1"); }
 @Test public void Subtype_Valid_3_RuntimeTest() { verifyPassTest("Subtype_Valid_3"); }
 @Test public void Subtype_Valid_4_RuntimeTest() { verifyPassTest("Subtype_Valid_4"); }
 @Test public void Subtype_Valid_5_RuntimeTest() { verifyPassTest("Subtype_Valid_5"); }
 @Test public void Subtype_Valid_6_RuntimeTest() { verifyPassTest("Subtype_Valid_6"); }
 @Test public void Subtype_Valid_7_RuntimeTest() { verifyPassTest("Subtype_Valid_7"); }
 @Test public void Subtype_Valid_8_RuntimeTest() { verifyPassTest("Subtype_Valid_8"); }
 @Test public void Subtype_Valid_9_RuntimeTest() { verifyPassTest("Subtype_Valid_9"); }
 @Ignore("Issue #222") @Test public void Switch_Valid_1_RuntimeTest() { verifyPassTest("Switch_Valid_1"); }
 @Ignore("Issue #222") @Test public void Switch_Valid_2_RuntimeTest() { verifyPassTest("Switch_Valid_2"); }
 @Ignore("Requires Type Test") @Test public void TypeEquals_Valid_1_RuntimeTest() { verifyPassTest("TypeEquals_Valid_1"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_10_RuntimeTest() { verifyPassTest("TypeEquals_Valid_10"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_13_RuntimeTest() { verifyPassTest("TypeEquals_Valid_13"); }
 @Test public void TypeEquals_Valid_5_RuntimeTest() { verifyPassTest("TypeEquals_Valid_5"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_6_RuntimeTest() { verifyPassTest("TypeEquals_Valid_6"); }
 @Ignore("Requires Type Test") @Test public void TypeEquals_Valid_8_RuntimeTest() { verifyPassTest("TypeEquals_Valid_8"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_9_RuntimeTest() { verifyPassTest("TypeEquals_Valid_9"); }
 @Test public void TypeEquals_Valid_14_RuntimeTest() { verifyPassTest("TypeEquals_Valid_14"); }
 @Ignore("Requires Type Test") @Test public void TypeEquals_Valid_15_RuntimeTest() { verifyPassTest("TypeEquals_Valid_15"); }
 @Test public void UnionType_Valid_12_RuntimeTest() { verifyPassTest("UnionType_Valid_12"); }
 @Test public void UnionType_Valid_4_RuntimeTest() { verifyPassTest("UnionType_Valid_4"); }
 @Ignore("Requires Type Test") @Test public void UnionType_Valid_5_RuntimeTest() { verifyPassTest("UnionType_Valid_5"); }
 @Test public void UnionType_Valid_6_RuntimeTest() { verifyPassTest("UnionType_Valid_6"); }
 @Ignore("Requires Type Test") @Test public void UnionType_Valid_7_RuntimeTest() { verifyPassTest("UnionType_Valid_7"); }
 @Ignore("Requires Type Test") @Test public void UnionType_Valid_8_RuntimeTest() { verifyPassTest("UnionType_Valid_8"); }
 @Test public void VarDecl_Valid_2_RuntimeTest() { verifyPassTest("VarDecl_Valid_2"); }
 @Test public void While_Valid_2_RuntimeTest() { verifyPassTest("While_Valid_2"); }
 @Ignore("Issue #231") @Test public void While_Valid_3_RuntimeTest() { verifyPassTest("While_Valid_3"); }
 @Test public void While_Valid_4_RuntimeTest() { verifyPassTest("While_Valid_4"); }
 @Test public void While_Valid_5_RuntimeTest() { verifyPassTest("While_Valid_5"); }
 @Ignore("Issue #231") @Test public void While_Valid_6_RuntimeTest() { verifyPassTest("While_Valid_6"); }
 @Test public void While_Valid_7_RuntimeTest() { verifyPassTest("While_Valid_7"); }
 @Ignore("Issue #225") @Test public void While_Valid_8_RuntimeTest() { verifyPassTest("While_Valid_8"); }
 @Test public void While_Valid_9_RuntimeTest() { verifyPassTest("While_Valid_9"); }
}
