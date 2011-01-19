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

package wyjx.testing.tests;

import org.junit.*;
import wyjx.testing.TestHarness;

public class StaticInvalidTests extends TestHarness {
 public StaticInvalidTests() {
  super("tests/ext/invalid","tests/ext/invalid","sysout");
 }

 @Test public void ConstrainedInt_Invalid_1_StaticTest() { contextFailTest("ConstrainedInt_Invalid_1"); }
 @Test public void ConstrainedInt_Invalid_10_StaticTest() { contextFailTest("ConstrainedInt_Invalid_10"); }
 @Test public void ConstrainedInt_Invalid_11_StaticTest() { contextFailTest("ConstrainedInt_Invalid_11"); }
 @Test public void ConstrainedInt_Invalid_2_StaticTest() { contextFailTest("ConstrainedInt_Invalid_2"); }
 @Test public void ConstrainedInt_Invalid_3_StaticTest() { contextFailTest("ConstrainedInt_Invalid_3"); }
 @Test public void ConstrainedInt_Invalid_4_StaticTest() { contextFailTest("ConstrainedInt_Invalid_4"); }
 @Test public void ConstrainedInt_Invalid_5_StaticTest() { contextFailTest("ConstrainedInt_Invalid_5"); }
 @Test public void ConstrainedInt_Invalid_6_StaticTest() { contextFailTest("ConstrainedInt_Invalid_6"); }
 @Test public void ConstrainedInt_Invalid_7_StaticTest() { contextFailTest("ConstrainedInt_Invalid_7"); }
 @Test public void ConstrainedInt_Invalid_8_StaticTest() { contextFailTest("ConstrainedInt_Invalid_8"); }
 @Test public void ConstrainedInt_Invalid_9_StaticTest() { contextFailTest("ConstrainedInt_Invalid_9"); }
 @Test public void ConstrainedList_Invalid_1_StaticTest() { contextFailTest("ConstrainedList_Invalid_1"); }
 @Test public void ConstrainedList_Invalid_2_StaticTest() { contextFailTest("ConstrainedList_Invalid_2"); }
 @Test public void ConstrainedList_Invalid_3_StaticTest() { contextFailTest("ConstrainedList_Invalid_3"); }
 @Test public void ConstrainedSet_Invalid_1_StaticTest() { contextFailTest("ConstrainedSet_Invalid_1"); }
 @Test public void ConstrainedSet_Invalid_2_StaticTest() { contextFailTest("ConstrainedSet_Invalid_2"); }
 @Test public void ConstrainedSet_Invalid_3_StaticTest() { contextFailTest("ConstrainedSet_Invalid_3"); }
 @Test public void ConstrainedTuple_Invalid_1_StaticTest() { contextFailTest("ConstrainedTuple_Invalid_1"); }
 @Test public void DefiniteAssign_CompileFail_1_StaticTest() { contextFailTest("DefiniteAssign_CompileFail_1"); }
 @Test public void DefiniteAssign_CompileFail_2_StaticTest() { contextFailTest("DefiniteAssign_CompileFail_2"); }
 @Test public void DefiniteAssign_CompileFail_3_StaticTest() { contextFailTest("DefiniteAssign_CompileFail_3"); }
 @Test public void Ensures_CompileFail_1_StaticTest() { contextFailTest("Ensures_CompileFail_1"); }
 @Test public void Ensures_CompileFail_3_StaticTest() { contextFailTest("Ensures_CompileFail_3"); }
 @Test public void Ensures_Invalid_1_StaticTest() { contextFailTest("Ensures_Invalid_1"); }
 @Test public void For_Invalid_1_StaticTest() { contextFailTest("For_Invalid_1"); }
 @Test public void For_Invalid_2_StaticTest() { contextFailTest("For_Invalid_2"); }
 @Test public void For_Invalid_3_StaticTest() { contextFailTest("For_Invalid_3"); }
 @Test public void For_Invalid_4_StaticTest() { contextFailTest("For_Invalid_4"); }
 @Test public void Function_CompileFail_5_StaticTest() { contextFailTest("Function_CompileFail_5"); }
 @Test public void Function_CompileFail_6_StaticTest() { contextFailTest("Function_CompileFail_6"); }
 @Test public void IntDiv_Invalid_1_StaticTest() { contextFailTest("IntDiv_Invalid_1"); }
 @Test public void ListAccess_CompileFail_2_StaticTest() { contextFailTest("ListAccess_CompileFail_2"); }
 @Test public void ListAccess_Invalid_1_StaticTest() { contextFailTest("ListAccess_Invalid_1"); }
 @Test public void ListAccess_Invalid_2_StaticTest() { contextFailTest("ListAccess_Invalid_2"); }
 @Test public void ListAppend_Invalid_3_StaticTest() { contextFailTest("ListAppend_Invalid_3"); }
 @Test public void ListAppend_Invalid_4_StaticTest() { contextFailTest("ListAppend_Invalid_4"); }
 @Test public void ListAppend_Invalid_5_StaticTest() { contextFailTest("ListAppend_Invalid_5"); }
 @Test public void ListAssign_Invalid_1_StaticTest() { contextFailTest("ListAssign_Invalid_1"); }
 @Test public void ListAssign_Invalid_2_StaticTest() { contextFailTest("ListAssign_Invalid_2"); }
 @Test public void ListElemOf_Invalid_1_StaticTest() { contextFailTest("ListElemOf_Invalid_1"); }
 @Test public void ListEmpty_Invalid_1_StaticTest() { contextFailTest("ListEmpty_Invalid_1"); }
 @Test public void ListEquals_CompileFail_1_StaticTest() { contextFailTest("ListEquals_CompileFail_1"); }
 @Test public void ListLength_Invalid_1_StaticTest() { contextFailTest("ListLength_Invalid_1"); }
 @Test public void ListLength_Invalid_2_StaticTest() { contextFailTest("ListLength_Invalid_2"); }
 @Test public void ListLength_Invalid_3_StaticTest() { contextFailTest("ListLength_Invalid_3"); }
 @Test public void ListSublist_CompileFail_2_StaticTest() { contextFailTest("ListSublist_CompileFail_2"); }
 @Test public void Process_Invalid_1_StaticTest() { contextFailTest("Process_Invalid_1"); }
 @Test public void Process_Invalid_2_StaticTest() { contextFailTest("Process_Invalid_2"); }
 @Test public void Quantifiers_CompileFail_1_StaticTest() { contextFailTest("Quantifiers_CompileFail_1"); }
 @Test public void Quantifiers_CompileFail_2_StaticTest() { contextFailTest("Quantifiers_CompileFail_2"); }
 @Test public void Quantifiers_CompileFail_3_StaticTest() { contextFailTest("Quantifiers_CompileFail_3"); }
 @Test public void Quantifiers_CompileFail_4_StaticTest() { contextFailTest("Quantifiers_CompileFail_4"); }
 @Test public void Quantifiers_Invalid_1_StaticTest() { contextFailTest("Quantifiers_Invalid_1"); }
 @Test public void Quantifiers_Invalid_2_StaticTest() { contextFailTest("Quantifiers_Invalid_2"); }
 @Test public void Quantifiers_Invalid_3_StaticTest() { contextFailTest("Quantifiers_Invalid_3"); }
 @Test public void Quantifiers_Invalid_4_StaticTest() { contextFailTest("Quantifiers_Invalid_4"); }
 @Test public void RealConvert_CompileFail_1_StaticTest() { contextFailTest("RealConvert_CompileFail_1"); }
 @Test public void RealConvert_CompileFail_2_StaticTest() { contextFailTest("RealConvert_CompileFail_2"); }
 @Test public void RealDiv_Invalid_1_StaticTest() { contextFailTest("RealDiv_Invalid_1"); }
 @Test public void RealMul_Invalid_1_StaticTest() { contextFailTest("RealMul_Invalid_1"); }
 @Test public void RecursiveType_Invalid_10_StaticTest() { contextFailTest("RecursiveType_Invalid_10"); }
 @Test public void RecursiveType_Invalid_3_StaticTest() { contextFailTest("RecursiveType_Invalid_3"); }
 @Test public void RecursiveType_Invalid_5_StaticTest() { contextFailTest("RecursiveType_Invalid_5"); }
 @Test public void RecursiveType_Invalid_6_StaticTest() { contextFailTest("RecursiveType_Invalid_6"); }
 @Test public void RecursiveType_Invalid_7_StaticTest() { contextFailTest("RecursiveType_Invalid_7"); }
 @Test public void RecursiveType_Invalid_8_StaticTest() { contextFailTest("RecursiveType_Invalid_8"); }
 @Test public void RecursiveType_Invalid_9_StaticTest() { contextFailTest("RecursiveType_Invalid_9"); }
 @Test public void Requires_Invalid_1_StaticTest() { contextFailTest("Requires_Invalid_1"); }
 @Test public void SetAssign_Invalid_1_StaticTest() { contextFailTest("SetAssign_Invalid_1"); }
 @Test public void SetComprehension_Invalid_1_StaticTest() { contextFailTest("SetComprehension_Invalid_1"); }
 @Test public void SetElemOf_Invalid_1_StaticTest() { contextFailTest("SetElemOf_Invalid_1"); }
 @Test public void SetEmpty_Invalid_1_StaticTest() { contextFailTest("SetEmpty_Invalid_1"); }
 @Test public void SetIntersection_Invalid_1_StaticTest() { contextFailTest("SetIntersection_Invalid_1"); }
 @Test public void SetIntersection_Invalid_2_StaticTest() { contextFailTest("SetIntersection_Invalid_2"); }
 @Test public void SetSubset_CompileFail_1_StaticTest() { contextFailTest("SetSubset_CompileFail_1"); }
 @Test public void SetSubset_CompileFail_2_StaticTest() { contextFailTest("SetSubset_CompileFail_2"); }
 @Test public void SetSubset_CompileFail_3_StaticTest() { contextFailTest("SetSubset_CompileFail_3"); }
 @Test public void SetSubset_CompileFail_4_StaticTest() { contextFailTest("SetSubset_CompileFail_4"); }
 @Test public void SetSubset_Invalid_1_StaticTest() { contextFailTest("SetSubset_Invalid_1"); }
 @Test public void SetSubset_Invalid_2_StaticTest() { contextFailTest("SetSubset_Invalid_2"); }
 @Test public void SetSubset_Invalid_3_StaticTest() { contextFailTest("SetSubset_Invalid_3"); }
 @Test public void SetSubset_Invalid_4_StaticTest() { contextFailTest("SetSubset_Invalid_4"); }
 @Test public void SetSubset_Invalid_5_StaticTest() { contextFailTest("SetSubset_Invalid_5"); }
 @Test public void SetSubset_Invalid_6_StaticTest() { contextFailTest("SetSubset_Invalid_6"); }
 @Test public void SetUnion_Invalid_1_StaticTest() { contextFailTest("SetUnion_Invalid_1"); }
 @Test public void SetUnion_Invalid_2_StaticTest() { contextFailTest("SetUnion_Invalid_2"); }
 @Test public void Subtype_CompileFail_1_StaticTest() { contextFailTest("Subtype_CompileFail_1"); }
 @Test public void Subtype_CompileFail_2_StaticTest() { contextFailTest("Subtype_CompileFail_2"); }
 @Test public void Subtype_CompileFail_3_StaticTest() { contextFailTest("Subtype_CompileFail_3"); }
 @Test public void Subtype_CompileFail_4_StaticTest() { contextFailTest("Subtype_CompileFail_4"); }
 @Test public void Subtype_CompileFail_5_StaticTest() { contextFailTest("Subtype_CompileFail_5"); }
 @Test public void Subtype_CompileFail_6_StaticTest() { contextFailTest("Subtype_CompileFail_6"); }
 @Test public void Subtype_CompileFail_7_StaticTest() { contextFailTest("Subtype_CompileFail_7"); }
 @Test public void Subtype_CompileFail_8_StaticTest() { contextFailTest("Subtype_CompileFail_8"); }
 @Test public void Subtype_CompileFail_9_StaticTest() { contextFailTest("Subtype_CompileFail_9"); }
 @Test public void TupleAssign_Invalid_1_StaticTest() { contextFailTest("TupleAssign_Invalid_1"); }
 @Test public void TupleAssign_Invalid_2_StaticTest() { contextFailTest("TupleAssign_Invalid_2"); }
 @Test public void TupleAssign_Invalid_3_StaticTest() { contextFailTest("TupleAssign_Invalid_3"); }
 @Test public void TupleDefine_CompileFail_2_StaticTest() { contextFailTest("TupleDefine_CompileFail_2"); }
 @Test public void TupleDefine_CompileFail_3_StaticTest() { contextFailTest("TupleDefine_CompileFail_3"); }
 @Test public void TypeEquals_Invalid_3_StaticTest() { contextFailTest("TypeEquals_Invalid_3"); }
 @Test public void UnionType_CompileFail_7_StaticTest() { contextFailTest("UnionType_CompileFail_7"); }
 @Test public void UnionType_CompileFail_8_StaticTest() { contextFailTest("UnionType_CompileFail_8"); }
 @Test public void UnionType_Invalid_1_StaticTest() { contextFailTest("UnionType_Invalid_1"); }
 @Test public void UnionType_Invalid_2_StaticTest() { contextFailTest("UnionType_Invalid_2"); }
 @Test public void UnionType_Invalid_3_StaticTest() { contextFailTest("UnionType_Invalid_3"); }
 @Test public void VarDecl_CompileFail_3_StaticTest() { contextFailTest("VarDecl_CompileFail_3"); }
 @Test public void VarDecl_Invalid_1_StaticTest() { contextFailTest("VarDecl_Invalid_1"); }
 @Test public void While_CompileFail_1_StaticTest() { contextFailTest("While_CompileFail_1"); }
 @Test public void While_CompileFail_2_StaticTest() { contextFailTest("While_CompileFail_2"); }
 @Test public void While_CompileFail_3_StaticTest() { contextFailTest("While_CompileFail_3"); }
 @Test public void While_CompileFail_4_StaticTest() { contextFailTest("While_CompileFail_4"); }
 @Test public void While_CompileFail_6_StaticTest() { contextFailTest("While_CompileFail_6"); }
 @Test public void While_CompileFail_7_StaticTest() { contextFailTest("While_CompileFail_7"); }
 @Test public void While_Invalid_2_StaticTest() { contextFailTest("While_Invalid_2"); }
 @Test public void While_Invalid_3_StaticTest() { contextFailTest("While_Invalid_3"); }
 @Test public void While_Invalid_4_StaticTest() { contextFailTest("While_Invalid_4"); }
 @Test public void While_Invalid_5_StaticTest() { contextFailTest("While_Invalid_5"); }
 @Test public void While_Invalid_6_StaticTest() { contextFailTest("While_Invalid_6"); }
}
