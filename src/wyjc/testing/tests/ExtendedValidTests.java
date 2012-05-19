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

 @Test public void BoolAssign_Valid_3_RuntimeTest() { verifyRunTest("BoolAssign_Valid_3"); }
 @Test public void BoolAssign_Valid_4_RuntimeTest() { verifyRunTest("BoolAssign_Valid_4"); }
 @Test public void BoolRequires_Valid_1_RuntimeTest() { verifyRunTest("BoolRequires_Valid_1"); }
 @Test public void ConstrainedDictionary_Valid_1_RuntimeTest() { verifyRunTest("ConstrainedDictionary_Valid_1"); }
 @Test public void ConstrainedInt_Valid_1_RuntimeTest() { verifyRunTest("ConstrainedInt_Valid_1"); }
 @Test public void ConstrainedInt_Valid_10_RuntimeTest() { verifyRunTest("ConstrainedInt_Valid_10"); }
 @Test public void ConstrainedInt_Valid_11_RuntimeTest() { verifyRunTest("ConstrainedInt_Valid_11"); }
 @Test public void ConstrainedInt_Valid_3_RuntimeTest() { verifyRunTest("ConstrainedInt_Valid_3"); }
 @Test public void ConstrainedInt_Valid_4_RuntimeTest() { verifyRunTest("ConstrainedInt_Valid_4"); }
 @Test public void ConstrainedInt_Valid_5_RuntimeTest() { verifyRunTest("ConstrainedInt_Valid_5"); }
 @Test public void ConstrainedInt_Valid_6_RuntimeTest() { verifyRunTest("ConstrainedInt_Valid_6"); }
 @Test public void ConstrainedInt_Valid_7_RuntimeTest() { verifyRunTest("ConstrainedInt_Valid_7"); }
 @Test public void ConstrainedInt_Valid_8_RuntimeTest() { verifyRunTest("ConstrainedInt_Valid_8"); }
 @Test public void ConstrainedInt_Valid_9_RuntimeTest() { verifyRunTest("ConstrainedInt_Valid_9"); }
 @Test public void ConstrainedList_Valid_1_RuntimeTest() { verifyRunTest("ConstrainedList_Valid_1"); }
 @Test public void ConstrainedList_Valid_2_RuntimeTest() { verifyRunTest("ConstrainedList_Valid_2"); }
 @Test public void ConstrainedList_Valid_3_RuntimeTest() { verifyRunTest("ConstrainedList_Valid_3"); }
 @Test public void ConstrainedList_Valid_4_RuntimeTest() { verifyRunTest("ConstrainedList_Valid_4"); }
 @Ignore("Future Work") @Test public void ConstrainedNegation_Valid_1_RuntimeTest() { verifyRunTest("ConstrainedNegation_Valid_1"); }
 @Test public void ConstrainedRecord_Valid_4_RuntimeTest() { verifyRunTest("ConstrainedRecord_Valid_4"); }
 @Test public void ConstrainedRecord_Valid_5_RuntimeTest() { verifyRunTest("ConstrainedRecord_Valid_5"); }
 @Test public void ConstrainedSet_Valid_1_RuntimeTest() { verifyRunTest("ConstrainedSet_Valid_1"); }
 @Test public void ConstrainedSet_Valid_2_RuntimeTest() { verifyRunTest("ConstrainedSet_Valid_2"); }
 @Test public void ConstrainedSet_Valid_3_RuntimeTest() { verifyRunTest("ConstrainedSet_Valid_3"); }
 @Test public void ConstrainedSet_Valid_4_RuntimeTest() { verifyRunTest("ConstrainedSet_Valid_4"); }
 @Test public void Ensures_Valid_1_RuntimeTest() { verifyRunTest("Ensures_Valid_1"); }
 @Test public void Ensures_Valid_2_RuntimeTest() { verifyRunTest("Ensures_Valid_2"); }
 @Test public void Ensures_Valid_3_RuntimeTest() { verifyRunTest("Ensures_Valid_3"); }
 @Test public void Ensures_Valid_4_RuntimeTest() { verifyRunTest("Ensures_Valid_4"); }
 @Test public void Ensures_Valid_5_RuntimeTest() { verifyRunTest("Ensures_Valid_5"); }
 @Test public void For_Valid_2_RuntimeTest() { verifyRunTest("For_Valid_2"); }
 @Test public void For_Valid_3_RuntimeTest() { verifyRunTest("For_Valid_3"); }
 @Ignore("Known Issue") @Test public void Function_Valid_11_RuntimeTest() { verifyRunTest("Function_Valid_11"); }
 @Test public void Function_Valid_12_RuntimeTest() { verifyRunTest("Function_Valid_12"); }
 @Test public void Function_Valid_2_RuntimeTest() { verifyRunTest("Function_Valid_2"); }
 @Test public void Function_Valid_3_RuntimeTest() { verifyRunTest("Function_Valid_3"); }
 @Test public void Function_Valid_4_RuntimeTest() { verifyRunTest("Function_Valid_4"); }
 @Test public void Function_Valid_5_RuntimeTest() { verifyRunTest("Function_Valid_5"); }
 @Test public void Function_Valid_6_RuntimeTest() { verifyRunTest("Function_Valid_6"); }
 @Ignore("Known Issue") @Test public void Function_Valid_8_RuntimeTest() { verifyRunTest("Function_Valid_8"); }
 @Test public void IntDefine_Valid_1_RuntimeTest() { verifyRunTest("IntDefine_Valid_1"); }
 @Test public void IntDiv_Valid_1_RuntimeTest() { verifyRunTest("IntDiv_Valid_1"); }
 @Test public void ListAccess_Valid_1_RuntimeTest() { verifyRunTest("ListAccess_Valid_1"); }
 @Test public void ListAccess_Valid_2_RuntimeTest() { verifyRunTest("ListAccess_Valid_2"); }
 @Test public void ListAppend_Valid_5_RuntimeTest() { verifyRunTest("ListAppend_Valid_5"); }
 @Test public void ListAppend_Valid_6_RuntimeTest() { verifyRunTest("ListAppend_Valid_6"); }
 @Test public void ListAssign_Valid_5_RuntimeTest() { verifyRunTest("ListAssign_Valid_5"); }
 @Test public void ListAssign_Valid_6_RuntimeTest() { verifyRunTest("ListAssign_Valid_6"); }
 @Test public void ListGenerator_Valid_1_RuntimeTest() { verifyRunTest("ListGenerator_Valid_1"); }
 @Test public void ListGenerator_Valid_2_RuntimeTest() { verifyRunTest("ListGenerator_Valid_2"); }
 @Test public void ListSublist_Valid_1_RuntimeTest() { verifyRunTest("ListSublist_Valid_1"); }
 @Test public void Process_Valid_2_RuntimeTest() { verifyRunTest("Process_Valid_2"); }
 @Test public void Quantifiers_Valid_1_RuntimeTest() { verifyRunTest("Quantifiers_Valid_1"); }
 @Test public void RealDiv_Valid_1_RuntimeTest() { verifyRunTest("RealDiv_Valid_1"); }
 @Test public void RealDiv_Valid_2_RuntimeTest() { verifyRunTest("RealDiv_Valid_2"); }
 @Test public void RealDiv_Valid_3_RuntimeTest() { verifyRunTest("RealDiv_Valid_3"); }
 @Test public void RealNeg_Valid_1_RuntimeTest() { verifyRunTest("RealNeg_Valid_1"); }
 @Test public void RealSub_Valid_1_RuntimeTest() { verifyRunTest("RealSub_Valid_1"); }
 @Test public void RecordAssign_Valid_1_RuntimeTest() { verifyRunTest("RecordAssign_Valid_1"); }
 @Test public void RecordAssign_Valid_2_RuntimeTest() { verifyRunTest("RecordAssign_Valid_2"); }
 @Test public void RecordAssign_Valid_4_RuntimeTest() { verifyRunTest("RecordAssign_Valid_4"); }
 @Test public void RecordAssign_Valid_5_RuntimeTest() { verifyRunTest("RecordAssign_Valid_5"); }
 @Test public void RecordDefine_Valid_1_RuntimeTest() { verifyRunTest("RecordDefine_Valid_1"); }
 @Test public void RecursiveType_Valid_3_RuntimeTest() { verifyRunTest("RecursiveType_Valid_3"); }
 @Test public void RecursiveType_Valid_5_RuntimeTest() { verifyRunTest("RecursiveType_Valid_5"); }
 @Test public void RecursiveType_Valid_6_RuntimeTest() { verifyRunTest("RecursiveType_Valid_6"); }
 @Test public void RecursiveType_Valid_8_RuntimeTest() { verifyRunTest("RecursiveType_Valid_8"); }
 @Test public void RecursiveType_Valid_9_RuntimeTest() { verifyRunTest("RecursiveType_Valid_9"); }
 @Test public void Requires_Valid_1_RuntimeTest() { verifyRunTest("Requires_Valid_1"); }
 @Test public void SetAssign_Valid_1_RuntimeTest() { verifyRunTest("SetAssign_Valid_1"); }
 @Test public void SetComprehension_Valid_6_RuntimeTest() { verifyRunTest("SetComprehension_Valid_6"); }
 @Test public void SetComprehension_Valid_7_RuntimeTest() { verifyRunTest("SetComprehension_Valid_7"); }
 @Test public void SetDefine_Valid_1_RuntimeTest() { verifyRunTest("SetDefine_Valid_1"); }
 @Test public void SetIntersection_Valid_1_RuntimeTest() { verifyRunTest("SetIntersection_Valid_1"); }
 @Test public void SetIntersection_Valid_2_RuntimeTest() { verifyRunTest("SetIntersection_Valid_2"); }
 @Test public void SetIntersection_Valid_3_RuntimeTest() { verifyRunTest("SetIntersection_Valid_3"); }
 @Test public void SetSubset_Valid_1_RuntimeTest() { verifyRunTest("SetSubset_Valid_1"); }
 @Test public void SetSubset_Valid_2_RuntimeTest() { verifyRunTest("SetSubset_Valid_2"); }
 @Test public void SetSubset_Valid_3_RuntimeTest() { verifyRunTest("SetSubset_Valid_3"); }
 @Test public void SetSubset_Valid_4_RuntimeTest() { verifyRunTest("SetSubset_Valid_4"); }
 @Test public void SetSubset_Valid_5_RuntimeTest() { verifyRunTest("SetSubset_Valid_5"); }
 @Test public void SetSubset_Valid_6_RuntimeTest() { verifyRunTest("SetSubset_Valid_6"); }
 @Test public void SetSubset_Valid_7_RuntimeTest() { verifyRunTest("SetSubset_Valid_7"); }
 @Test public void SetUnion_Valid_5_RuntimeTest() { verifyRunTest("SetUnion_Valid_5"); }
 @Test public void SetUnion_Valid_6_RuntimeTest() { verifyRunTest("SetUnion_Valid_6"); }
 @Test public void Subtype_Valid_3_RuntimeTest() { verifyRunTest("Subtype_Valid_3"); }
 @Test public void Subtype_Valid_4_RuntimeTest() { verifyRunTest("Subtype_Valid_4"); }
 @Test public void Subtype_Valid_5_RuntimeTest() { verifyRunTest("Subtype_Valid_5"); }
 @Test public void Subtype_Valid_6_RuntimeTest() { verifyRunTest("Subtype_Valid_6"); }
 @Test public void Subtype_Valid_7_RuntimeTest() { verifyRunTest("Subtype_Valid_7"); }
 @Test public void Subtype_Valid_8_RuntimeTest() { verifyRunTest("Subtype_Valid_8"); }
 @Test public void Subtype_Valid_9_RuntimeTest() { verifyRunTest("Subtype_Valid_9"); }
 @Test public void TypeEquals_Valid_1_RuntimeTest() { verifyRunTest("TypeEquals_Valid_1"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_10_RuntimeTest() { verifyRunTest("TypeEquals_Valid_10"); }
 @Test public void TypeEquals_Valid_13_RuntimeTest() { verifyRunTest("TypeEquals_Valid_13"); }
 @Test public void TypeEquals_Valid_5_RuntimeTest() { verifyRunTest("TypeEquals_Valid_5"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_6_RuntimeTest() { verifyRunTest("TypeEquals_Valid_6"); }
 @Test public void TypeEquals_Valid_8_RuntimeTest() { verifyRunTest("TypeEquals_Valid_8"); }
 @Ignore("Known Issue") @Test public void TypeEquals_Valid_9_RuntimeTest() { verifyRunTest("TypeEquals_Valid_9"); }
 @Test public void TypeEquals_Valid_14_RuntimeTest() { verifyRunTest("TypeEquals_Valid_14"); }
 @Test public void UnionType_Valid_12_RuntimeTest() { verifyRunTest("UnionType_Valid_12"); }
 @Test public void UnionType_Valid_4_RuntimeTest() { verifyRunTest("UnionType_Valid_4"); }
 @Test public void UnionType_Valid_5_RuntimeTest() { verifyRunTest("UnionType_Valid_5"); }
 @Test public void UnionType_Valid_6_RuntimeTest() { verifyRunTest("UnionType_Valid_6"); }
 @Test public void UnionType_Valid_7_RuntimeTest() { verifyRunTest("UnionType_Valid_7"); }
 @Test public void UnionType_Valid_8_RuntimeTest() { verifyRunTest("UnionType_Valid_8"); }
 @Test public void VarDecl_Valid_2_RuntimeTest() { verifyRunTest("VarDecl_Valid_2"); }
 @Test public void While_Valid_2_RuntimeTest() { verifyRunTest("While_Valid_2"); }
 @Test public void While_Valid_3_RuntimeTest() { verifyRunTest("While_Valid_3"); }
 @Test public void While_Valid_4_RuntimeTest() { verifyRunTest("While_Valid_4"); }
 @Test public void While_Valid_5_RuntimeTest() { verifyRunTest("While_Valid_5"); }
 @Test public void While_Valid_6_RuntimeTest() { verifyRunTest("While_Valid_6"); }
}
