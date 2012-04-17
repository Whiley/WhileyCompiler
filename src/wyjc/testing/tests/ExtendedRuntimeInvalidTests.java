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

public class ExtendedRuntimeInvalidTests extends TestHarness {
 public ExtendedRuntimeInvalidTests() {
  super("tests/ext/invalid","tests/ext/invalid","sysout");
 }

 @Test public void ConstrainedDictionary_Invalid_1_RuntimeTest() { runtimeFailTest("ConstrainedDictionary_Invalid_1"); }
 @Test public void ConstrainedInt_Invalid_1_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_1"); }
 @Test public void ConstrainedInt_Invalid_10_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_10"); }
 @Test public void ConstrainedInt_Invalid_11_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_11"); }
 @Test public void ConstrainedInt_Invalid_2_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_2"); }
 @Test public void ConstrainedInt_Invalid_3_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_3"); }
 @Test public void ConstrainedInt_Invalid_4_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_4"); }
 @Test public void ConstrainedInt_Invalid_5_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_5"); }
 @Test public void ConstrainedInt_Invalid_6_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_6"); }
 @Test public void ConstrainedInt_Invalid_7_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_7"); }
 @Ignore("Known Issue") @Test public void ConstrainedInt_Invalid_8_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_8"); }
 @Test public void ConstrainedInt_Invalid_9_RuntimeFailTest() { runtimeFailTest("ConstrainedInt_Invalid_9"); }
 @Test public void ConstrainedList_Invalid_1_RuntimeFailTest() { runtimeFailTest("ConstrainedList_Invalid_1"); }
 @Test public void ConstrainedList_Invalid_2_RuntimeFailTest() { runtimeFailTest("ConstrainedList_Invalid_2"); }
 @Test public void ConstrainedList_Invalid_3_RuntimeFailTest() { runtimeFailTest("ConstrainedList_Invalid_3"); }
 @Test public void ConstrainedSet_Invalid_1_RuntimeFailTest() { runtimeFailTest("ConstrainedSet_Invalid_1"); }
 @Test public void ConstrainedSet_Invalid_2_RuntimeFailTest() { runtimeFailTest("ConstrainedSet_Invalid_2"); }
 @Test public void ConstrainedSet_Invalid_3_RuntimeFailTest() { runtimeFailTest("ConstrainedSet_Invalid_3"); }
 @Test public void ConstrainedTuple_Invalid_1_RuntimeFailTest() { runtimeFailTest("ConstrainedTuple_Invalid_1"); }
 @Test public void Ensures_CompileFail_1_RuntimeFailTest() { runtimeFailTest("Ensures_CompileFail_1"); }
 @Test public void Ensures_CompileFail_3_RuntimeFailTest() { runtimeFailTest("Ensures_CompileFail_3"); }
 @Test public void Ensures_Invalid_1_RuntimeFailTest() { runtimeFailTest("Ensures_Invalid_1"); }
 @Test public void For_Invalid_1_RuntimeFailTest() { runtimeFailTest("For_Invalid_1"); }
 @Test public void For_Invalid_2_RuntimeFailTest() { runtimeFailTest("For_Invalid_2"); }
 @Test public void For_Invalid_3_RuntimeFailTest() { runtimeFailTest("For_Invalid_3"); }
 @Test public void For_Invalid_4_RuntimeFailTest() { runtimeFailTest("For_Invalid_4"); }
 @Ignore("Known Issue") @Test public void Function_CompileFail_5_RuntimeFailTest() { runtimeFailTest("Function_CompileFail_5"); }
 @Ignore("Known Issue") @Test public void Function_CompileFail_6_RuntimeFailTest() { runtimeFailTest("Function_CompileFail_6"); }
 @Test public void IntDiv_Invalid_1_RuntimeFailTest() { runtimeFailTest("IntDiv_Invalid_1"); }
 @Test public void ListAccess_CompileFail_2_RuntimeFailTest() { runtimeFailTest("ListAccess_CompileFail_2"); }
 @Test public void ListAccess_Invalid_1_RuntimeFailTest() { runtimeFailTest("ListAccess_Invalid_1"); }
 @Test public void ListAccess_Invalid_2_RuntimeFailTest() { runtimeFailTest("ListAccess_Invalid_2"); }
 @Test public void ListAppend_Invalid_3_RuntimeFailTest() { runtimeFailTest("ListAppend_Invalid_3"); }
 @Test public void ListAppend_Invalid_4_RuntimeFailTest() { runtimeFailTest("ListAppend_Invalid_4"); }
 @Test public void ListAppend_Invalid_5_RuntimeFailTest() { runtimeFailTest("ListAppend_Invalid_5"); }
 @Test public void ListAssign_Invalid_1_RuntimeFailTest() { runtimeFailTest("ListAssign_Invalid_1"); }
 @Test public void ListAssign_Invalid_2_RuntimeFailTest() { runtimeFailTest("ListAssign_Invalid_2"); }
 @Test public void ListElemOf_Invalid_1_RuntimeFailTest() { runtimeFailTest("ListElemOf_Invalid_1"); }
 @Test public void ListEmpty_Invalid_1_RuntimeFailTest() { runtimeFailTest("ListEmpty_Invalid_1"); }
 @Test public void ListEquals_CompileFail_1_RuntimeFailTest() { runtimeFailTest("ListEquals_CompileFail_1"); }
 @Test public void ListLength_Invalid_1_RuntimeFailTest() { runtimeFailTest("ListLength_Invalid_1"); }
 @Test public void ListLength_Invalid_2_RuntimeFailTest() { runtimeFailTest("ListLength_Invalid_2"); }
 @Test public void ListLength_Invalid_3_RuntimeFailTest() { runtimeFailTest("ListLength_Invalid_3"); }
 @Test public void ListSublist_CompileFail_2_RuntimeFailTest() { runtimeFailTest("ListSublist_CompileFail_2"); } 
 @Ignore("Known Issue") @Test public void Process_Invalid_2_RuntimeFailTest() { runtimeFailTest("Process_Invalid_2"); }
 @Test public void Quantifiers_CompileFail_1_RuntimeFailTest() { runtimeFailTest("Quantifiers_CompileFail_1"); }
 @Test public void Quantifiers_CompileFail_2_RuntimeFailTest() { runtimeFailTest("Quantifiers_CompileFail_2"); }
 @Test public void Quantifiers_CompileFail_3_RuntimeFailTest() { runtimeFailTest("Quantifiers_CompileFail_3"); }
 @Test public void Quantifiers_CompileFail_4_RuntimeFailTest() { runtimeFailTest("Quantifiers_CompileFail_4"); }
 @Test public void Quantifiers_Invalid_1_RuntimeFailTest() { runtimeFailTest("Quantifiers_Invalid_1"); }
 @Test public void Quantifiers_Invalid_2_RuntimeFailTest() { runtimeFailTest("Quantifiers_Invalid_2"); }
 @Test public void Quantifiers_Invalid_3_RuntimeFailTest() { runtimeFailTest("Quantifiers_Invalid_3"); }
 @Test public void Quantifiers_Invalid_4_RuntimeFailTest() { runtimeFailTest("Quantifiers_Invalid_4"); }
 @Test public void RealConvert_CompileFail_1_RuntimeFailTest() { runtimeFailTest("RealConvert_CompileFail_1"); }
 @Test public void RealConvert_CompileFail_2_RuntimeFailTest() { runtimeFailTest("RealConvert_CompileFail_2"); }
 @Test public void RealDiv_Invalid_1_RuntimeFailTest() { runtimeFailTest("RealDiv_Invalid_1"); }
 @Test public void RealMul_Invalid_1_RuntimeFailTest() { runtimeFailTest("RealMul_Invalid_1"); }
 @Test public void RecursiveType_Invalid_10_RuntimeFailTest() { runtimeFailTest("RecursiveType_Invalid_10"); }
 @Test public void RecursiveType_Invalid_3_RuntimeFailTest() { runtimeFailTest("RecursiveType_Invalid_3"); }
 @Test public void RecursiveType_Invalid_5_RuntimeFailTest() { runtimeFailTest("RecursiveType_Invalid_5"); }
 @Test public void RecursiveType_Invalid_6_RuntimeFailTest() { runtimeFailTest("RecursiveType_Invalid_6"); }
 @Test public void RecursiveType_Invalid_7_RuntimeFailTest() { runtimeFailTest("RecursiveType_Invalid_7"); }
 @Test public void RecursiveType_Invalid_8_RuntimeFailTest() { runtimeFailTest("RecursiveType_Invalid_8"); }
 @Test public void RecursiveType_Invalid_9_RuntimeFailTest() { runtimeFailTest("RecursiveType_Invalid_9"); }
 @Test public void Requires_Invalid_1_RuntimeFailTest() { runtimeFailTest("Requires_Invalid_1"); }
 @Test public void SetAssign_Invalid_1_RuntimeFailTest() { runtimeFailTest("SetAssign_Invalid_1"); }
 @Test public void SetComprehension_Invalid_1_RuntimeFailTest() { runtimeFailTest("SetComprehension_Invalid_1"); }
 @Test public void SetElemOf_Invalid_1_RuntimeFailTest() { runtimeFailTest("SetElemOf_Invalid_1"); }
 @Test public void SetEmpty_Invalid_1_RuntimeFailTest() { runtimeFailTest("SetEmpty_Invalid_1"); }
 @Test public void SetIntersection_Invalid_1_RuntimeFailTest() { runtimeFailTest("SetIntersection_Invalid_1"); }
 @Test public void SetIntersection_Invalid_2_RuntimeFailTest() { runtimeFailTest("SetIntersection_Invalid_2"); }
 @Test public void SetSubset_CompileFail_1_RuntimeFailTest() { runtimeFailTest("SetSubset_CompileFail_1"); }
 @Test public void SetSubset_CompileFail_2_RuntimeFailTest() { runtimeFailTest("SetSubset_CompileFail_2"); }
 @Test public void SetSubset_CompileFail_3_RuntimeFailTest() { runtimeFailTest("SetSubset_CompileFail_3"); }
 @Test public void SetSubset_CompileFail_4_RuntimeFailTest() { runtimeFailTest("SetSubset_CompileFail_4"); }
 @Test public void SetSubset_Invalid_1_RuntimeFailTest() { runtimeFailTest("SetSubset_Invalid_1"); }
 @Test public void SetSubset_Invalid_2_RuntimeFailTest() { runtimeFailTest("SetSubset_Invalid_2"); }
 @Test public void SetSubset_Invalid_3_RuntimeFailTest() { runtimeFailTest("SetSubset_Invalid_3"); }
 @Test public void SetSubset_Invalid_4_RuntimeFailTest() { runtimeFailTest("SetSubset_Invalid_4"); }
 @Test public void SetSubset_Invalid_5_RuntimeFailTest() { runtimeFailTest("SetSubset_Invalid_5"); }
 @Test public void SetSubset_Invalid_6_RuntimeFailTest() { runtimeFailTest("SetSubset_Invalid_6"); }
 @Test public void SetUnion_Invalid_1_RuntimeFailTest() { runtimeFailTest("SetUnion_Invalid_1"); }
 @Test public void SetUnion_Invalid_2_RuntimeFailTest() { runtimeFailTest("SetUnion_Invalid_2"); }
 @Test public void Subtype_CompileFail_1_RuntimeFailTest() { runtimeFailTest("Subtype_CompileFail_1"); }
 @Test public void Subtype_CompileFail_2_RuntimeFailTest() { runtimeFailTest("Subtype_CompileFail_2"); }
 @Test public void Subtype_CompileFail_3_RuntimeFailTest() { runtimeFailTest("Subtype_CompileFail_3"); }
 @Test public void Subtype_CompileFail_4_RuntimeFailTest() { runtimeFailTest("Subtype_CompileFail_4"); }
 @Test public void Subtype_CompileFail_5_RuntimeFailTest() { runtimeFailTest("Subtype_CompileFail_5"); }
 @Test public void Subtype_CompileFail_6_RuntimeFailTest() { runtimeFailTest("Subtype_CompileFail_6"); }
 @Test public void Subtype_CompileFail_7_RuntimeFailTest() { runtimeFailTest("Subtype_CompileFail_7"); }
 @Test public void Subtype_CompileFail_8_RuntimeFailTest() { runtimeFailTest("Subtype_CompileFail_8"); }
 @Test public void Subtype_CompileFail_9_RuntimeFailTest() { runtimeFailTest("Subtype_CompileFail_9"); }
 @Test public void Tuple_Invalid_1_RuntimeFailTest() { runtimeFailTest("Tuple_Invalid_1"); }
 @Test public void Tuple_Invalid_2_RuntimeFailTest() { runtimeFailTest("Tuple_Invalid_2"); }
 @Test public void Tuple_Invalid_3_RuntimeFailTest() { runtimeFailTest("Tuple_Invalid_3"); }
 @Test public void Tuple_Invalid_4_RuntimeFailTest() { runtimeFailTest("Tuple_Invalid_4"); }
 @Test public void TupleAssign_Invalid_1_RuntimeFailTest() { runtimeFailTest("TupleAssign_Invalid_1"); }
 @Test public void TupleAssign_Invalid_2_RuntimeFailTest() { runtimeFailTest("TupleAssign_Invalid_2"); }
 @Test public void TupleAssign_Invalid_3_RuntimeFailTest() { runtimeFailTest("TupleAssign_Invalid_3"); }
 @Test public void TupleDefine_CompileFail_2_RuntimeFailTest() { runtimeFailTest("TupleDefine_CompileFail_2"); } 
 @Test public void TypeEquals_Invalid_3_RuntimeFailTest() { runtimeFailTest("TypeEquals_Invalid_3"); }
 @Test public void TypeEquals_Invalid_4_RuntimeFailTest() { runtimeFailTest("TypeEquals_Invalid_4"); }
 @Test public void UnionType_CompileFail_8_RuntimeFailTest() { runtimeFailTest("UnionType_CompileFail_8"); }
 @Test public void UnionType_Invalid_1_RuntimeFailTest() { runtimeFailTest("UnionType_Invalid_1"); }
 @Test public void UnionType_Invalid_2_RuntimeFailTest() { runtimeFailTest("UnionType_Invalid_2"); }
 @Test public void UnionType_Invalid_3_RuntimeFailTest() { runtimeFailTest("UnionType_Invalid_3"); } 
 @Test public void VarDecl_Invalid_1_RuntimeFailTest() { runtimeFailTest("VarDecl_Invalid_1"); }  
 @Test public void While_CompileFail_6_RuntimeFailTest() { runtimeFailTest("While_CompileFail_6"); }
 @Test public void While_Invalid_2_RuntimeFailTest() { runtimeFailTest("While_Invalid_2"); }
 @Test public void While_Invalid_3_RuntimeFailTest() { runtimeFailTest("While_Invalid_3"); }
 @Test public void While_Invalid_4_RuntimeFailTest() { runtimeFailTest("While_Invalid_4"); }
 @Test public void While_Invalid_5_RuntimeFailTest() { runtimeFailTest("While_Invalid_5"); }
 @Test public void While_Invalid_6_RuntimeFailTest() { runtimeFailTest("While_Invalid_6"); }
}
