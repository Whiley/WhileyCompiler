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

package wyjc.testing.tests;

import org.junit.*;

import wyjc.testing.TestHarness;

public class ExtendedValidTests extends TestHarness {
 public ExtendedValidTests() {
  super("tests/ext/valid","tests/ext/valid","sysout");
 }

 @Test public void BoolAssign_Valid_3_RuntimeTest() { runTest("BoolAssign_Valid_3"); }
 @Test public void BoolAssign_Valid_4_RuntimeTest() { runTest("BoolAssign_Valid_4"); }
 @Test public void BoolRequires_Valid_1_RuntimeTest() { runTest("BoolRequires_Valid_1"); }
 @Test public void ConstrainedDictionary_Valid_1_RuntimeTest() { runTest("ConstrainedDictionary_Valid_1"); }
 @Test public void ConstrainedInt_Valid_1_RuntimeTest() { runTest("ConstrainedInt_Valid_1"); }
 @Test public void ConstrainedInt_Valid_10_RuntimeTest() { runTest("ConstrainedInt_Valid_10"); }
 @Test public void ConstrainedInt_Valid_11_RuntimeTest() { runTest("ConstrainedInt_Valid_11"); }
 @Test public void ConstrainedInt_Valid_3_RuntimeTest() { runTest("ConstrainedInt_Valid_3"); }
 @Test public void ConstrainedInt_Valid_4_RuntimeTest() { runTest("ConstrainedInt_Valid_4"); }
 @Test public void ConstrainedInt_Valid_5_RuntimeTest() { runTest("ConstrainedInt_Valid_5"); }
 @Test public void ConstrainedInt_Valid_6_RuntimeTest() { runTest("ConstrainedInt_Valid_6"); }
 @Test public void ConstrainedInt_Valid_7_RuntimeTest() { runTest("ConstrainedInt_Valid_7"); }
 @Test public void ConstrainedInt_Valid_8_RuntimeTest() { runTest("ConstrainedInt_Valid_8"); }
 @Ignore("Known Issue") @Test public void ConstrainedInt_Valid_9_RuntimeTest() { runTest("ConstrainedInt_Valid_9"); }
 @Ignore("Known Issue") @Test public void ConstrainedList_Valid_1_RuntimeTest() { runTest("ConstrainedList_Valid_1"); }
 @Ignore("Known Issue") @Test public void ConstrainedList_Valid_2_RuntimeTest() { runTest("ConstrainedList_Valid_2"); }
 @Test public void ConstrainedList_Valid_3_RuntimeTest() { runTest("ConstrainedList_Valid_3"); }
 @Ignore("Known Issue") @Test public void ConstrainedList_Valid_4_RuntimeTest() { runTest("ConstrainedList_Valid_4"); }
 @Ignore("Future Work") @Test public void ConstrainedNegation_Valid_1_RuntimeTest() { runTest("ConstrainedNegation_Valid_1"); }
 @Test public void ConstrainedRecord_Valid_4_RuntimeTest() { runTest("ConstrainedRecord_Valid_4"); }
 @Test public void ConstrainedRecord_Valid_5_RuntimeTest() { runTest("ConstrainedRecord_Valid_5"); }
 @Ignore("Known Issue") @Test public void ConstrainedSet_Valid_1_RuntimeTest() { runTest("ConstrainedSet_Valid_1"); }
 @Ignore("Known Issue") @Test public void ConstrainedSet_Valid_2_RuntimeTest() { runTest("ConstrainedSet_Valid_2"); }
 @Test public void ConstrainedSet_Valid_3_RuntimeTest() { runTest("ConstrainedSet_Valid_3"); }
 @Test public void ConstrainedSet_Valid_4_RuntimeTest() { runTest("ConstrainedSet_Valid_4"); }
 @Test public void ConstrainedTuple_Valid_1_RuntimeTest() { verifyRunTest("ConstrainedTuple_Valid_1"); }
 @Test public void Ensures_Valid_1_RuntimeTest() { runTest("Ensures_Valid_1"); }
 @Test public void Ensures_Valid_2_RuntimeTest() { runTest("Ensures_Valid_2"); }
 @Test public void Ensures_Valid_3_RuntimeTest() { runTest("Ensures_Valid_3"); }
 @Test public void Ensures_Valid_4_RuntimeTest() { runTest("Ensures_Valid_4"); }
 @Test public void Ensures_Valid_5_RuntimeTest() { runTest("Ensures_Valid_5"); }
 @Test public void For_Valid_2_RuntimeTest() { runTest("For_Valid_2"); }
 @Ignore("Known Issue") @Test public void For_Valid_3_RuntimeTest() { runTest("For_Valid_3"); }
 @Ignore("Known Issue") @Test public void Function_Valid_11_RuntimeTest() { runTest("Function_Valid_11"); }
 @Test public void Function_Valid_12_RuntimeTest() { runTest("Function_Valid_12"); }
 @Test public void Function_Valid_2_RuntimeTest() { runTest("Function_Valid_2"); }
 @Test public void Function_Valid_3_RuntimeTest() { runTest("Function_Valid_3"); }
 @Test public void Function_Valid_4_RuntimeTest() { runTest("Function_Valid_4"); }
 @Test public void Function_Valid_5_RuntimeTest() { runTest("Function_Valid_5"); }
 @Test public void Function_Valid_6_RuntimeTest() { runTest("Function_Valid_6"); }
 @Ignore("Known Issue") @Test public void Function_Valid_8_RuntimeTest() { runTest("Function_Valid_8"); }
 @Test public void IntDefine_Valid_1_RuntimeTest() { runTest("IntDefine_Valid_1"); }
 @Test public void IntDiv_Valid_1_RuntimeTest() { runTest("IntDiv_Valid_1"); }
 @Test public void ListAccess_Valid_1_RuntimeTest() { runTest("ListAccess_Valid_1"); }
 @Test public void ListAccess_Valid_2_RuntimeTest() { runTest("ListAccess_Valid_2"); }
 @Test public void ListAppend_Valid_5_RuntimeTest() { runTest("ListAppend_Valid_5"); }
 @Test public void ListAppend_Valid_6_RuntimeTest() { runTest("ListAppend_Valid_6"); }
 @Test public void ListAssign_Valid_5_RuntimeTest() { runTest("ListAssign_Valid_5"); }
 @Test public void ListAssign_Valid_6_RuntimeTest() { runTest("ListAssign_Valid_6"); }
 @Test public void ListGenerator_Valid_1_RuntimeTest() { runTest("ListGenerator_Valid_1"); }
 @Test public void ListGenerator_Valid_2_RuntimeTest() { runTest("ListGenerator_Valid_2"); }
 @Test public void ListSublist_Valid_1_RuntimeTest() { runTest("ListSublist_Valid_1"); }
 @Test public void Process_Valid_2_RuntimeTest() { runTest("Process_Valid_2"); }
 @Test public void Quantifiers_Valid_1_RuntimeTest() { runTest("Quantifiers_Valid_1"); }
 @Test public void RealDiv_Valid_1_RuntimeTest() { runTest("RealDiv_Valid_1"); }
 @Test public void RealDiv_Valid_2_RuntimeTest() { runTest("RealDiv_Valid_2"); }
 @Test public void RealDiv_Valid_3_RuntimeTest() { runTest("RealDiv_Valid_3"); }
 @Test public void RealNeg_Valid_1_RuntimeTest() { runTest("RealNeg_Valid_1"); }
 @Test public void RealSub_Valid_1_RuntimeTest() { runTest("RealSub_Valid_1"); }
 @Test public void RecordAssign_Valid_1_RuntimeTest() { runTest("RecordAssign_Valid_1"); }
 @Test public void RecordAssign_Valid_2_RuntimeTest() { runTest("RecordAssign_Valid_2"); }
 @Ignore("Known Issue") @Test public void RecordAssign_Valid_4_RuntimeTest() { runTest("RecordAssign_Valid_4"); }
 @Ignore("Known Issue") @Test public void RecordAssign_Valid_5_RuntimeTest() { runTest("RecordAssign_Valid_5"); }
 @Test public void RecordDefine_Valid_1_RuntimeTest() { runTest("RecordDefine_Valid_1"); }
 @Test public void RecursiveType_Valid_3_RuntimeTest() { runTest("RecursiveType_Valid_3"); }
 @Test public void RecursiveType_Valid_5_RuntimeTest() { runTest("RecursiveType_Valid_5"); }
 @Test public void RecursiveType_Valid_6_RuntimeTest() { runTest("RecursiveType_Valid_6"); }
 @Test public void RecursiveType_Valid_8_RuntimeTest() { runTest("RecursiveType_Valid_8"); }
 @Test public void RecursiveType_Valid_9_RuntimeTest() { runTest("RecursiveType_Valid_9"); }
 @Ignore("Known Issue") @Test public void Requires_Valid_1_RuntimeTest() { runTest("Requires_Valid_1"); }
 @Test public void SetAssign_Valid_1_RuntimeTest() { runTest("SetAssign_Valid_1"); }
 @Test public void SetComprehension_Valid_6_RuntimeTest() { runTest("SetComprehension_Valid_6"); }
 @Test public void SetComprehension_Valid_7_RuntimeTest() { runTest("SetComprehension_Valid_7"); }
 @Test public void SetDefine_Valid_1_RuntimeTest() { runTest("SetDefine_Valid_1"); }
 @Test public void SetIntersection_Valid_1_RuntimeTest() { runTest("SetIntersection_Valid_1"); }
 @Test public void SetIntersection_Valid_2_RuntimeTest() { runTest("SetIntersection_Valid_2"); }
 @Test public void SetIntersection_Valid_3_RuntimeTest() { runTest("SetIntersection_Valid_3"); }
 @Test public void SetSubset_Valid_1_RuntimeTest() { runTest("SetSubset_Valid_1"); }
 @Test public void SetSubset_Valid_2_RuntimeTest() { runTest("SetSubset_Valid_2"); }
 @Test public void SetSubset_Valid_3_RuntimeTest() { runTest("SetSubset_Valid_3"); }
 @Test public void SetSubset_Valid_4_RuntimeTest() { runTest("SetSubset_Valid_4"); }
 @Test public void SetSubset_Valid_5_RuntimeTest() { runTest("SetSubset_Valid_5"); }
 @Test public void SetSubset_Valid_6_RuntimeTest() { runTest("SetSubset_Valid_6"); }
 @Test public void SetSubset_Valid_7_RuntimeTest() { runTest("SetSubset_Valid_7"); }
 @Test public void SetUnion_Valid_5_RuntimeTest() { runTest("SetUnion_Valid_5"); }
 @Test public void SetUnion_Valid_6_RuntimeTest() { runTest("SetUnion_Valid_6"); }
 @Test public void Subtype_Valid_3_RuntimeTest() { runTest("Subtype_Valid_3"); }
 @Test public void Subtype_Valid_4_RuntimeTest() { runTest("Subtype_Valid_4"); }
 @Test public void Subtype_Valid_5_RuntimeTest() { runTest("Subtype_Valid_5"); }
 @Test public void Subtype_Valid_6_RuntimeTest() { runTest("Subtype_Valid_6"); }
 @Test public void Subtype_Valid_7_RuntimeTest() { runTest("Subtype_Valid_7"); }
 @Test public void Subtype_Valid_8_RuntimeTest() { runTest("Subtype_Valid_8"); }
 @Test public void Subtype_Valid_9_RuntimeTest() { runTest("Subtype_Valid_9"); }
 @Test public void TypeEquals_Valid_1_RuntimeTest() { runTest("TypeEquals_Valid_1"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_10_RuntimeTest() { runTest("TypeEquals_Valid_10"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_13_RuntimeTest() { runTest("TypeEquals_Valid_13"); }
 @Test public void TypeEquals_Valid_5_RuntimeTest() { runTest("TypeEquals_Valid_5"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_6_RuntimeTest() { runTest("TypeEquals_Valid_6"); }
 @Test public void TypeEquals_Valid_8_RuntimeTest() { runTest("TypeEquals_Valid_8"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_9_RuntimeTest() { runTest("TypeEquals_Valid_9"); }
 @Test public void TypeEquals_Valid_14_RuntimeTest() { runTest("TypeEquals_Valid_14"); }
 @Test public void UnionType_Valid_12_RuntimeTest() { runTest("UnionType_Valid_12"); }
 @Test public void UnionType_Valid_4_RuntimeTest() { runTest("UnionType_Valid_4"); }
 @Test public void UnionType_Valid_5_RuntimeTest() { runTest("UnionType_Valid_5"); }
 @Test public void UnionType_Valid_6_RuntimeTest() { runTest("UnionType_Valid_6"); }
 @Test public void UnionType_Valid_7_RuntimeTest() { runTest("UnionType_Valid_7"); }
 @Test public void UnionType_Valid_8_RuntimeTest() { runTest("UnionType_Valid_8"); }
 @Test public void VarDecl_Valid_2_RuntimeTest() { runTest("VarDecl_Valid_2"); }
 @Test public void While_Valid_2_RuntimeTest() { runTest("While_Valid_2"); }
 @Test public void While_Valid_3_RuntimeTest() { runTest("While_Valid_3"); }
 @Test public void While_Valid_4_RuntimeTest() { runTest("While_Valid_4"); }
 @Test public void While_Valid_5_RuntimeTest() { runTest("While_Valid_5"); }
 @Ignore("Known Issue") @Test public void While_Valid_6_RuntimeTest() { runTest("While_Valid_6"); }
}
