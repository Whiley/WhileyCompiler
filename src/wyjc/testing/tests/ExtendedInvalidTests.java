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

public class ExtendedInvalidTests extends TestHarness {
 public ExtendedInvalidTests() {
  super("tests/ext/invalid","tests/ext/invalid","sysout");
 }

 @Test public void ConstrainedDictionary_Invalid_1_StaticTest() { verifyFailTest("ConstrainedDictionary_Invalid_1"); }
 @Test public void ConstrainedInt_Invalid_1_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_1"); }
 @Test public void ConstrainedInt_Invalid_10_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_10"); }
 @Test public void ConstrainedInt_Invalid_11_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_11"); }
 @Test public void ConstrainedInt_Invalid_2_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_2"); }
 @Test public void ConstrainedInt_Invalid_3_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_3"); }
 @Test public void ConstrainedInt_Invalid_4_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_4"); }
 @Test public void ConstrainedInt_Invalid_5_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_5"); }
 @Test public void ConstrainedInt_Invalid_6_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_6"); }
 @Test public void ConstrainedInt_Invalid_7_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_7"); }
 @Ignore("Known Issue") @Test public void ConstrainedInt_Invalid_8_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_8"); }
 @Test public void ConstrainedInt_Invalid_9_StaticFailTest() { verifyFailTest("ConstrainedInt_Invalid_9"); }
 @Test public void ConstrainedList_Invalid_1_StaticFailTest() { verifyFailTest("ConstrainedList_Invalid_1"); }
 @Test public void ConstrainedList_Invalid_2_StaticFailTest() { verifyFailTest("ConstrainedList_Invalid_2"); }
 @Test public void ConstrainedList_Invalid_3_StaticFailTest() { verifyFailTest("ConstrainedList_Invalid_3"); }
 @Test public void ConstrainedSet_Invalid_1_StaticFailTest() { verifyFailTest("ConstrainedSet_Invalid_1"); }
 @Test public void ConstrainedSet_Invalid_2_StaticFailTest() { verifyFailTest("ConstrainedSet_Invalid_2"); }
 @Test public void ConstrainedSet_Invalid_3_StaticFailTest() { verifyFailTest("ConstrainedSet_Invalid_3"); }
 @Test public void ConstrainedTuple_Invalid_1_StaticFailTest() { verifyFailTest("ConstrainedTuple_Invalid_1"); }
 @Test public void Ensures_CompileFail_1_StaticFailTest() { verifyFailTest("Ensures_CompileFail_1"); }
 @Test public void Ensures_CompileFail_3_StaticFailTest() { verifyFailTest("Ensures_CompileFail_3"); }
 @Test public void Ensures_Invalid_1_StaticFailTest() { verifyFailTest("Ensures_Invalid_1"); }
 @Test public void For_Invalid_1_StaticFailTest() { verifyFailTest("For_Invalid_1"); }
 @Test public void For_Invalid_2_StaticFailTest() { verifyFailTest("For_Invalid_2"); }
 @Test public void For_Invalid_3_StaticFailTest() { verifyFailTest("For_Invalid_3"); }
 @Test public void For_Invalid_4_StaticFailTest() { verifyFailTest("For_Invalid_4"); }
 @Ignore("Known Issue") @Test public void Function_CompileFail_5_StaticFailTest() { verifyFailTest("Function_CompileFail_5"); }
 @Ignore("Known Issue") @Test public void Function_CompileFail_6_StaticFailTest() { verifyFailTest("Function_CompileFail_6"); }
 @Test public void IntDiv_Invalid_1_StaticFailTest() { verifyFailTest("IntDiv_Invalid_1"); }
 @Test public void ListAccess_CompileFail_2_StaticFailTest() { verifyFailTest("ListAccess_CompileFail_2"); }
 @Test public void ListAccess_Invalid_1_StaticFailTest() { verifyFailTest("ListAccess_Invalid_1"); }
 @Test public void ListAccess_Invalid_2_StaticFailTest() { verifyFailTest("ListAccess_Invalid_2"); }
 @Test public void ListAppend_Invalid_3_StaticFailTest() { verifyFailTest("ListAppend_Invalid_3"); }
 @Test public void ListAppend_Invalid_4_StaticFailTest() { verifyFailTest("ListAppend_Invalid_4"); }
 @Test public void ListAppend_Invalid_5_StaticFailTest() { verifyFailTest("ListAppend_Invalid_5"); }
 @Test public void ListAssign_Invalid_1_StaticFailTest() { verifyFailTest("ListAssign_Invalid_1"); }
 @Test public void ListAssign_Invalid_2_StaticFailTest() { verifyFailTest("ListAssign_Invalid_2"); }
 @Test public void ListElemOf_Invalid_1_StaticFailTest() { verifyFailTest("ListElemOf_Invalid_1"); }
 @Test public void ListEmpty_Invalid_1_StaticFailTest() { verifyFailTest("ListEmpty_Invalid_1"); }
 @Test public void ListEquals_CompileFail_1_StaticFailTest() { verifyFailTest("ListEquals_CompileFail_1"); }
 @Test public void ListLength_Invalid_1_StaticFailTest() { verifyFailTest("ListLength_Invalid_1"); }
 @Test public void ListLength_Invalid_2_StaticFailTest() { verifyFailTest("ListLength_Invalid_2"); }
 @Test public void ListLength_Invalid_3_StaticFailTest() { verifyFailTest("ListLength_Invalid_3"); }
 @Test public void ListSublist_CompileFail_2_StaticFailTest() { verifyFailTest("ListSublist_CompileFail_2"); } 
 @Ignore("Known Issue") @Test public void Process_Invalid_2_StaticFailTest() { verifyFailTest("Process_Invalid_2"); }
 @Test public void Quantifiers_CompileFail_1_StaticFailTest() { verifyFailTest("Quantifiers_CompileFail_1"); }
 @Test public void Quantifiers_CompileFail_2_StaticFailTest() { verifyFailTest("Quantifiers_CompileFail_2"); }
 @Test public void Quantifiers_CompileFail_3_StaticFailTest() { verifyFailTest("Quantifiers_CompileFail_3"); }
 @Test public void Quantifiers_CompileFail_4_StaticFailTest() { verifyFailTest("Quantifiers_CompileFail_4"); }
 @Test public void Quantifiers_Invalid_1_StaticFailTest() { verifyFailTest("Quantifiers_Invalid_1"); }
 @Test public void Quantifiers_Invalid_2_StaticFailTest() { verifyFailTest("Quantifiers_Invalid_2"); }
 @Test public void Quantifiers_Invalid_3_StaticFailTest() { verifyFailTest("Quantifiers_Invalid_3"); }
 @Test public void Quantifiers_Invalid_4_StaticFailTest() { verifyFailTest("Quantifiers_Invalid_4"); }
 @Test public void RealConvert_CompileFail_1_StaticFailTest() { verifyFailTest("RealConvert_CompileFail_1"); }
 @Test public void RealConvert_CompileFail_2_StaticFailTest() { verifyFailTest("RealConvert_CompileFail_2"); }
 @Test public void RealDiv_Invalid_1_StaticFailTest() { verifyFailTest("RealDiv_Invalid_1"); }
 @Test public void RealMul_Invalid_1_StaticFailTest() { verifyFailTest("RealMul_Invalid_1"); }
 @Test public void RecursiveType_Invalid_10_StaticFailTest() { verifyFailTest("RecursiveType_Invalid_10"); }
 @Test public void RecursiveType_Invalid_3_StaticFailTest() { verifyFailTest("RecursiveType_Invalid_3"); }
 @Test public void RecursiveType_Invalid_5_StaticFailTest() { verifyFailTest("RecursiveType_Invalid_5"); }
 @Test public void RecursiveType_Invalid_6_StaticFailTest() { verifyFailTest("RecursiveType_Invalid_6"); }
 @Test public void RecursiveType_Invalid_7_StaticFailTest() { verifyFailTest("RecursiveType_Invalid_7"); }
 @Test public void RecursiveType_Invalid_8_StaticFailTest() { verifyFailTest("RecursiveType_Invalid_8"); }
 @Test public void RecursiveType_Invalid_9_StaticFailTest() { verifyFailTest("RecursiveType_Invalid_9"); }
 @Test public void Requires_Invalid_1_StaticFailTest() { verifyFailTest("Requires_Invalid_1"); }
 @Test public void SetAssign_Invalid_1_StaticFailTest() { verifyFailTest("SetAssign_Invalid_1"); }
 @Test public void SetComprehension_Invalid_1_StaticFailTest() { verifyFailTest("SetComprehension_Invalid_1"); }
 @Test public void SetElemOf_Invalid_1_StaticFailTest() { verifyFailTest("SetElemOf_Invalid_1"); }
 @Test public void SetEmpty_Invalid_1_StaticFailTest() { verifyFailTest("SetEmpty_Invalid_1"); }
 @Test public void SetIntersection_Invalid_1_StaticFailTest() { verifyFailTest("SetIntersection_Invalid_1"); }
 @Test public void SetIntersection_Invalid_2_StaticFailTest() { verifyFailTest("SetIntersection_Invalid_2"); }
 @Test public void SetSubset_CompileFail_1_StaticFailTest() { verifyFailTest("SetSubset_CompileFail_1"); }
 @Test public void SetSubset_CompileFail_2_StaticFailTest() { verifyFailTest("SetSubset_CompileFail_2"); }
 @Test public void SetSubset_CompileFail_3_StaticFailTest() { verifyFailTest("SetSubset_CompileFail_3"); }
 @Test public void SetSubset_CompileFail_4_StaticFailTest() { verifyFailTest("SetSubset_CompileFail_4"); }
 @Test public void SetSubset_Invalid_1_StaticFailTest() { verifyFailTest("SetSubset_Invalid_1"); }
 @Test public void SetSubset_Invalid_2_StaticFailTest() { verifyFailTest("SetSubset_Invalid_2"); }
 @Test public void SetSubset_Invalid_3_StaticFailTest() { verifyFailTest("SetSubset_Invalid_3"); }
 @Test public void SetSubset_Invalid_4_StaticFailTest() { verifyFailTest("SetSubset_Invalid_4"); }
 @Test public void SetSubset_Invalid_5_StaticFailTest() { verifyFailTest("SetSubset_Invalid_5"); }
 @Test public void SetSubset_Invalid_6_StaticFailTest() { verifyFailTest("SetSubset_Invalid_6"); }
 @Test public void SetUnion_Invalid_1_StaticFailTest() { verifyFailTest("SetUnion_Invalid_1"); }
 @Test public void SetUnion_Invalid_2_StaticFailTest() { verifyFailTest("SetUnion_Invalid_2"); }
 @Test public void Subtype_CompileFail_1_StaticFailTest() { verifyFailTest("Subtype_CompileFail_1"); }
 @Test public void Subtype_CompileFail_2_StaticFailTest() { verifyFailTest("Subtype_CompileFail_2"); }
 @Test public void Subtype_CompileFail_3_StaticFailTest() { verifyFailTest("Subtype_CompileFail_3"); }
 @Test public void Subtype_CompileFail_4_StaticFailTest() { verifyFailTest("Subtype_CompileFail_4"); }
 @Test public void Subtype_CompileFail_5_StaticFailTest() { verifyFailTest("Subtype_CompileFail_5"); }
 @Test public void Subtype_CompileFail_6_StaticFailTest() { verifyFailTest("Subtype_CompileFail_6"); }
 @Test public void Subtype_CompileFail_7_StaticFailTest() { verifyFailTest("Subtype_CompileFail_7"); }
 @Test public void Subtype_CompileFail_8_StaticFailTest() { verifyFailTest("Subtype_CompileFail_8"); }
 @Test public void Subtype_CompileFail_9_StaticFailTest() { verifyFailTest("Subtype_CompileFail_9"); }
 @Test public void Tuple_Invalid_1_StaticFailTest() { verifyFailTest("Tuple_Invalid_1"); }
 @Test public void Tuple_Invalid_2_StaticFailTest() { verifyFailTest("Tuple_Invalid_2"); }
 @Test public void Tuple_Invalid_3_StaticFailTest() { verifyFailTest("Tuple_Invalid_3"); }
 @Test public void Tuple_Invalid_4_StaticFailTest() { verifyFailTest("Tuple_Invalid_4"); }
 @Test public void TupleAssign_Invalid_1_StaticFailTest() { verifyFailTest("TupleAssign_Invalid_1"); }
 @Test public void TupleAssign_Invalid_2_StaticFailTest() { verifyFailTest("TupleAssign_Invalid_2"); }
 @Test public void TupleAssign_Invalid_3_StaticFailTest() { verifyFailTest("TupleAssign_Invalid_3"); }
 @Test public void TupleDefine_CompileFail_2_StaticFailTest() { verifyFailTest("TupleDefine_CompileFail_2"); } 
 @Test public void TypeEquals_Invalid_3_StaticFailTest() { verifyFailTest("TypeEquals_Invalid_3"); }
 @Test public void TypeEquals_Invalid_4_StaticFailTest() { verifyFailTest("TypeEquals_Invalid_4"); }
 @Test public void UnionType_CompileFail_8_StaticFailTest() { verifyFailTest("UnionType_CompileFail_8"); }
 @Test public void UnionType_Invalid_1_StaticFailTest() { verifyFailTest("UnionType_Invalid_1"); }
 @Test public void UnionType_Invalid_2_StaticFailTest() { verifyFailTest("UnionType_Invalid_2"); }
 @Test public void UnionType_Invalid_3_StaticFailTest() { verifyFailTest("UnionType_Invalid_3"); } 
 @Test public void VarDecl_Invalid_1_StaticFailTest() { verifyFailTest("VarDecl_Invalid_1"); }  
 @Test public void While_CompileFail_6_StaticFailTest() { verifyFailTest("While_CompileFail_6"); }
 @Test public void While_Invalid_2_StaticFailTest() { verifyFailTest("While_Invalid_2"); }
 @Test public void While_Invalid_3_StaticFailTest() { verifyFailTest("While_Invalid_3"); }
 @Test public void While_Invalid_4_StaticFailTest() { verifyFailTest("While_Invalid_4"); }
 @Test public void While_Invalid_5_StaticFailTest() { verifyFailTest("While_Invalid_5"); }
 @Test public void While_Invalid_6_StaticFailTest() { verifyFailTest("While_Invalid_6"); }
}
