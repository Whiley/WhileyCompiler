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

public class DefiniteRuntimeValidTests extends TestHarness {
 public DefiniteRuntimeValidTests() {
  super("tests/valid/definite","tests/valid/definite","sysout");
 }

 @Test public void BoolAssign_Valid_1_RunTest() { runTest("BoolAssign_Valid_1"); }
 @Test public void BoolAssign_Valid_2_RunTest() { runTest("BoolAssign_Valid_2"); }
 @Test public void BoolAssign_Valid_3_RunTest() { runTest("BoolAssign_Valid_3"); }
 @Test public void BoolAssign_Valid_4_RunTest() { runTest("BoolAssign_Valid_4"); }
 @Test public void BoolFun_Valid_1_RunTest() { runTest("BoolFun_Valid_1"); }
 @Test public void BoolIfElse_Valid_1_RunTest() { runTest("BoolIfElse_Valid_1"); }
 @Test public void BoolList_Valid_1_RunTest() { runTest("BoolList_Valid_1"); }
 @Test public void BoolRequires_Valid_1_RunTest() { runTest("BoolRequires_Valid_1"); }
 @Test public void BoolReturn_Valid_1_RunTest() { runTest("BoolReturn_Valid_1"); }
 @Test public void BoolTuple_Valid_1_RunTest() { runTest("BoolTuple_Valid_1"); }
 @Test public void BoolTuple_Valid_2_RunTest() { runTest("BoolTuple_Valid_2"); }
 @Test public void ConstrainedInt_Valid_1_RunTest() { runTest("ConstrainedInt_Valid_1"); }
 @Test public void ConstrainedInt_Valid_10_RunTest() { runTest("ConstrainedInt_Valid_10"); }
 @Test public void ConstrainedInt_Valid_11_RunTest() { runTest("ConstrainedInt_Valid_11"); }
 @Test public void ConstrainedInt_Valid_2_RunTest() { runTest("ConstrainedInt_Valid_2"); }
 @Test public void ConstrainedInt_Valid_3_RunTest() { runTest("ConstrainedInt_Valid_3"); }
 @Test public void ConstrainedInt_Valid_4_RunTest() { runTest("ConstrainedInt_Valid_4"); }
 @Test public void ConstrainedInt_Valid_5_RunTest() { runTest("ConstrainedInt_Valid_5"); }
 @Test public void ConstrainedInt_Valid_6_RunTest() { runTest("ConstrainedInt_Valid_6"); }
 @Test public void ConstrainedInt_Valid_7_RunTest() { runTest("ConstrainedInt_Valid_7"); }
 @Test public void ConstrainedInt_Valid_8_RunTest() { runTest("ConstrainedInt_Valid_8"); }
 @Test public void ConstrainedInt_Valid_9_RunTest() { runTest("ConstrainedInt_Valid_9"); }
 @Test public void ConstrainedList_Valid_1_RunTest() { runTest("ConstrainedList_Valid_1"); }
 @Test public void ConstrainedList_Valid_2_RunTest() { runTest("ConstrainedList_Valid_2"); }
 @Test public void ConstrainedList_Valid_3_RunTest() { runTest("ConstrainedList_Valid_3"); }
 @Test public void ConstrainedList_Valid_4_RunTest() { runTest("ConstrainedList_Valid_4"); }
 @Test public void ConstrainedSet_Valid_1_RunTest() { runTest("ConstrainedSet_Valid_1"); }
 @Test public void ConstrainedSet_Valid_2_RunTest() { runTest("ConstrainedSet_Valid_2"); }
 @Test public void ConstrainedSet_Valid_3_RunTest() { runTest("ConstrainedSet_Valid_3"); }
 @Test public void ConstrainedSet_Valid_4_RunTest() { runTest("ConstrainedSet_Valid_4"); }
 @Test public void ConstrainedTuple_Valid_1_RunTest() { runTest("ConstrainedTuple_Valid_1"); }
 @Test public void ConstrainedTuple_Valid_2_RunTest() { runTest("ConstrainedTuple_Valid_2"); }
 @Test public void ConstrainedTuple_Valid_3_RunTest() { runTest("ConstrainedTuple_Valid_3"); }
 @Test public void ConstrainedTuple_Valid_4_RunTest() { runTest("ConstrainedTuple_Valid_4"); }
 @Test public void ConstrainedTuple_Valid_5_RunTest() { runTest("ConstrainedTuple_Valid_5"); }
 @Test public void Define_Valid_1_RunTest() { runTest("Define_Valid_1"); }
 @Test public void Define_Valid_2_RunTest() { runTest("Define_Valid_2"); }
 @Test public void Define_Valid_3_RunTest() { runTest("Define_Valid_3"); }
 @Test public void Define_Valid_4_RunTest() { runTest("Define_Valid_4"); }
 @Test public void Ensures_Valid_1_RunTest() { runTest("Ensures_Valid_1"); }
 @Test public void Ensures_Valid_2_RunTest() { runTest("Ensures_Valid_2"); }
 @Test public void Ensures_Valid_3_RunTest() { runTest("Ensures_Valid_3"); }
 @Test public void Ensures_Valid_4_RunTest() { runTest("Ensures_Valid_4"); }
 @Test public void Ensures_Valid_5_RunTest() { runTest("Ensures_Valid_5"); }
 @Test public void Function_Valid_1_RunTest() { runTest("Function_Valid_1"); }
 @Test public void Function_Valid_10_RunTest() { runTest("Function_Valid_10"); }
 @Test public void Function_Valid_11_RunTest() { runTest("Function_Valid_11"); }
 @Test public void Function_Valid_2_RunTest() { runTest("Function_Valid_2"); }
 @Test public void Function_Valid_3_RunTest() { runTest("Function_Valid_3"); }
 @Test public void Function_Valid_4_RunTest() { runTest("Function_Valid_4"); }
 @Test public void Function_Valid_5_RunTest() { runTest("Function_Valid_5"); }
 @Test public void Function_Valid_6_RunTest() { runTest("Function_Valid_6"); }
 @Test public void Function_Valid_7_RunTest() { runTest("Function_Valid_7"); }
 @Test public void Function_Valid_8_RunTest() { runTest("Function_Valid_8"); }
 @Test public void Function_Valid_9_RunTest() { runTest("Function_Valid_9"); }
 @Test public void IfElse_Valid_1_RunTest() { runTest("IfElse_Valid_1"); }
 @Test public void IfElse_Valid_2_RunTest() { runTest("IfElse_Valid_2"); }
 @Test public void IfElse_Valid_3_RunTest() { runTest("IfElse_Valid_3"); }
 @Test public void IntConst_Valid_1_RunTest() { runTest("IntConst_Valid_1"); }
 @Test public void IntDefine_Valid_1_RunTest() { runTest("IntDefine_Valid_1"); }
 @Test public void IntDiv_Valid_1_RunTest() { runTest("IntDiv_Valid_1"); }
 @Test public void IntEquals_Valid_1_RunTest() { runTest("IntEquals_Valid_1"); }
 @Test public void IntOp_Valid_1_RunTest() { runTest("IntOp_Valid_1"); }
 @Ignore("Known Bug") @Test public void IntersectionType_Valid_1_RunTest() { runTest("IntersectionType_Valid_1"); }
 @Ignore("Known Bug") @Test public void IntersectionType_Valid_2_RunTest() { runTest("IntersectionType_Valid_2"); }
 @Test public void ListAccess_Valid_1_RunTest() { runTest("ListAccess_Valid_1"); }
 @Test public void ListAccess_Valid_2_RunTest() { runTest("ListAccess_Valid_2"); }
 @Test public void ListAccess_Valid_3_RunTest() { runTest("ListAccess_Valid_3"); }
 @Test public void ListAssign_Valid_1_RunTest() { runTest("ListAssign_Valid_1"); }
 @Test public void ListAssign_Valid_2_RunTest() { runTest("ListAssign_Valid_2"); }
 @Test public void ListAssign_Valid_3_RunTest() { runTest("ListAssign_Valid_3"); }
 @Test public void ListAssign_Valid_4_RunTest() { runTest("ListAssign_Valid_4"); }
 @Test public void ListAssign_Valid_5_RunTest() { runTest("ListAssign_Valid_5"); }
 @Test public void ListAssign_Valid_6_RunTest() { runTest("ListAssign_Valid_6"); }
 @Test public void ListAppend_Valid_1_RunTest() { runTest("ListAppend_Valid_1"); }
 @Test public void ListAppend_Valid_2_RunTest() { runTest("ListAppend_Valid_2"); }
 @Test public void ListAppend_Valid_3_RunTest() { runTest("ListAppend_Valid_3"); }
 @Test public void ListAppend_Valid_4_RunTest() { runTest("ListAppend_Valid_4"); }
 @Test public void ListAppend_Valid_5_RunTest() { runTest("ListAppend_Valid_5"); }
 @Test public void ListAppend_Valid_6_RunTest() { runTest("ListAppend_Valid_6"); }
 @Test public void ListConversion_Valid_1_RunTest() { runTest("ListConversion_Valid_1"); }
 @Test public void ListElemOf_Valid_1_RunTest() { runTest("ListElemOf_Valid_1"); }
 @Test public void ListEmpty_Valid_1_RunTest() { runTest("ListEmpty_Valid_1"); }
 @Test public void ListEquals_Valid_1_RunTest() { runTest("ListEquals_Valid_1"); }
 @Test public void ListGenerator_Valid_1_RunTest() { runTest("ListGenerator_Valid_1"); }
 @Test public void ListGenerator_Valid_2_RunTest() { runTest("ListGenerator_Valid_2"); }
 @Test public void ListGenerator_Valid_3_RunTest() { runTest("ListGenerator_Valid_3"); }
 @Test public void ListLength_Valid_1_RunTest() { runTest("ListLength_Valid_1"); }
 @Test public void ListLength_Valid_2_RunTest() { runTest("ListLength_Valid_2"); }
 @Test public void ListSublist_Valid_1_RunTest() { runTest("ListSublist_Valid_1"); }
 @Test public void ListSublist_Valid_2_RunTest() { runTest("ListSublist_Valid_2"); }
 @Test public void MethodCall_Valid_3_RunTest() { runTest("MethodCall_Valid_3"); }
 @Test public void MethodCall_Valid_4_RunTest() { runTest("MethodCall_Valid_4"); }
 @Test public void MethodCall_Valid_5_RunTest() { runTest("MethodCall_Valid_5"); }
 @Test public void MethodCall_Valid_6_RunTest() { runTest("MethodCall_Valid_6"); }
 @Test public void Print_Valid_1_RunTest() { runTest("Print_Valid_1"); }
 @Test public void ProcessAccess_Valid_1_RunTest() { runTest("ProcessAccess_Valid_1"); }
 @Test public void ProcessAccess_Valid_2_RunTest() { runTest("ProcessAccess_Valid_2"); }
 @Test public void Process_Valid_1_RunTest() { runTest("Process_Valid_1"); }
 @Test public void Process_Valid_2_RunTest() { runTest("Process_Valid_2"); }
 @Test public void Quantifiers_Valid_1_RunTest() { runTest("Quantifiers_Valid_1"); }
 @Test public void RealConst_Valid_1_RunTest() { runTest("RealConst_Valid_1"); }
 @Test public void RealDiv_Valid_1_RunTest() { runTest("RealDiv_Valid_1"); }
 @Test public void RealDiv_Valid_2_RunTest() { runTest("RealDiv_Valid_2"); }
 @Test public void RealDiv_Valid_3_RunTest() { runTest("RealDiv_Valid_3"); }
 @Test public void RealNeg_Valid_1_RunTest() { runTest("RealNeg_Valid_1"); }
 @Test public void RealSub_Valid_1_RunTest() { runTest("RealSub_Valid_1"); }
 @Test public void RecursiveType_Valid_1_RunTest() { runTest("RecursiveType_Valid_1"); }
 @Test public void RecursiveType_Valid_2_RunTest() { runTest("RecursiveType_Valid_2"); }
 @Test public void RecursiveType_Valid_3_RunTest() { runTest("RecursiveType_Valid_3"); }
 @Test public void RecursiveType_Valid_4_RunTest() { runTest("RecursiveType_Valid_4"); }
 @Test public void RecursiveType_Valid_5_RunTest() { runTest("RecursiveType_Valid_5"); }
 @Test public void RecursiveType_Valid_6_RunTest() { runTest("RecursiveType_Valid_6"); }
 @Test public void RecursiveType_Valid_7_RunTest() { runTest("RecursiveType_Valid_7"); }
 @Test public void RecursiveType_Valid_8_RunTest() { runTest("RecursiveType_Valid_8"); }
 @Test public void RecursiveType_Valid_9_RunTest() { runTest("RecursiveType_Valid_9"); }
 @Test public void Requires_Valid_1_RunTest() { runTest("Requires_Valid_1"); }
 @Test public void Resolution_Valid_1_RunTest() { runTest("Resolution_Valid_1"); }
 @Test public void SetAssign_Valid_1_RunTest() { runTest("SetAssign_Valid_1"); }
 @Test public void SetComprehension_Valid_1_RunTest() { runTest("SetComprehension_Valid_1"); }
 @Test public void SetComprehension_Valid_2_RunTest() { runTest("SetComprehension_Valid_2"); }
 @Test public void SetComprehension_Valid_3_RunTest() { runTest("SetComprehension_Valid_3"); }
 @Test public void SetComprehension_Valid_4_RunTest() { runTest("SetComprehension_Valid_4"); }
 @Test public void SetComprehension_Valid_5_RunTest() { runTest("SetComprehension_Valid_5"); }
 @Test public void SetComprehension_Valid_6_RunTest() { runTest("SetComprehension_Valid_6"); }
 @Test public void SetComprehension_Valid_7_RunTest() { runTest("SetComprehension_Valid_7"); }
 @Test public void SetConversion_Valid_1_RunTest() { runTest("SetConversion_Valid_1"); }
 @Test public void SetDefine_Valid_1_RunTest() { runTest("SetDefine_Valid_1"); }
 @Test public void SetElemOf_Valid_1_RunTest() { runTest("SetElemOf_Valid_1"); }
 @Test public void SetEmpty_Valid_1_RunTest() { runTest("SetEmpty_Valid_1"); }
 @Test public void SetGenerator_Valid_1_RunTest() { runTest("SetGenerator_Valid_1"); }
 @Test public void SetIntersect_Valid_1_RunTest() { runTest("SetIntersect_Valid_1"); }
 @Test public void SetIntersect_Valid_2_RunTest() { runTest("SetIntersect_Valid_2"); }
 @Test public void SetIntersection_Valid_1_RunTest() { runTest("SetIntersection_Valid_1"); }
 @Test public void SetIntersection_Valid_2_RunTest() { runTest("SetIntersection_Valid_2"); }
 @Test public void SetIntersection_Valid_3_RunTest() { runTest("SetIntersection_Valid_3"); }
 @Test public void SetLength_Valid_1_RunTest() { runTest("SetLength_Valid_1"); }
 @Test public void SetSubset_Valid_1_RunTest() { runTest("SetSubset_Valid_1"); }
 @Test public void SetSubset_Valid_2_RunTest() { runTest("SetSubset_Valid_2"); }
 @Test public void SetSubset_Valid_3_RunTest() { runTest("SetSubset_Valid_3"); }
 @Test public void SetSubset_Valid_4_RunTest() { runTest("SetSubset_Valid_4"); }
 @Test public void SetSubset_Valid_5_RunTest() { runTest("SetSubset_Valid_5"); }
 @Test public void SetSubset_Valid_6_RunTest() { runTest("SetSubset_Valid_6"); }
 @Test public void SetSubset_Valid_7_RunTest() { runTest("SetSubset_Valid_7"); }
 @Test public void SetUnion_Valid_1_RunTest() { runTest("SetUnion_Valid_1"); }
 @Test public void SetUnion_Valid_2_RunTest() { runTest("SetUnion_Valid_2"); }
 @Test public void SetUnion_Valid_3_RunTest() { runTest("SetUnion_Valid_3"); }
 @Test public void SetUnion_Valid_4_RunTest() { runTest("SetUnion_Valid_4"); }
 @Test public void SetUnion_Valid_5_RunTest() { runTest("SetUnion_Valid_5"); }
 @Test public void SetUnion_Valid_6_RunTest() { runTest("SetUnion_Valid_6"); }
 @Test public void Subtype_Valid_3_RunTest() { runTest("Subtype_Valid_3"); }
 @Test public void Subtype_Valid_4_RunTest() { runTest("Subtype_Valid_4"); }
 @Test public void Subtype_Valid_5_RunTest() { runTest("Subtype_Valid_5"); }
 @Test public void Subtype_Valid_6_RunTest() { runTest("Subtype_Valid_6"); }
 @Test public void Subtype_Valid_7_RunTest() { runTest("Subtype_Valid_7"); }
 @Test public void Subtype_Valid_8_RunTest() { runTest("Subtype_Valid_8"); }
 @Test public void Subtype_Valid_9_RunTest() { runTest("Subtype_Valid_9"); }
 @Test public void TupleAccess_Valid_1_RunTest() { runTest("TupleAccess_Valid_1"); }
 @Test public void TupleAssign_Valid_1_RunTest() { runTest("TupleAssign_Valid_1"); }
 @Test public void TupleAssign_Valid_2_RunTest() { runTest("TupleAssign_Valid_2"); }
 @Test public void TupleAssign_Valid_3_RunTest() { runTest("TupleAssign_Valid_3"); }
 @Test public void TupleAssign_Valid_4_RunTest() { runTest("TupleAssign_Valid_4"); }
 @Test public void TupleAssign_Valid_5_RunTest() { runTest("TupleAssign_Valid_5","-wyil"); }
 @Test public void TupleConversion_Valid_1_RunTest() { runTest("TupleConversion_Valid_1"); }
 @Test public void TupleDefine_Valid_1_RunTest() { runTest("TupleDefine_Valid_1"); }
 @Test public void TypeEquals_Valid_1_RunTest() { runTest("TypeEquals_Valid_1"); }
 @Test public void TypeEquals_Valid_2_RunTest() { runTest("TypeEquals_Valid_2"); }
 @Test public void TypeEquals_Valid_3_RunTest() { runTest("TypeEquals_Valid_3"); }
 @Test public void TypeEquals_Valid_4_RunTest() { runTest("TypeEquals_Valid_4"); }
 @Test public void TypeEquals_Valid_5_RunTest() { runTest("TypeEquals_Valid_5"); }
 @Test public void TypeEquals_Valid_6_RunTest() { runTest("TypeEquals_Valid_6"); }
 @Test public void TypeEquals_Valid_7_RunTest() { runTest("TypeEquals_Valid_7"); }
 @Test public void TypeEquals_Valid_8_RunTest() { runTest("TypeEquals_Valid_8"); }
 @Test public void TypeEquals_Valid_9_RunTest() { runTest("TypeEquals_Valid_9"); }
 @Test public void TypeEquals_Valid_10_RunTest() { runTest("TypeEquals_Valid_10"); }
 @Test public void TypeEquals_Valid_11_RunTest() { runTest("TypeEquals_Valid_11"); }
 @Test public void TypeEquals_Valid_12_RunTest() { runTest("TypeEquals_Valid_12"); }
 @Test public void UnionType_Valid_1_RunTest() { runTest("UnionType_Valid_1"); }
 @Test public void UnionType_Valid_10_RunTest() { runTest("UnionType_Valid_10"); }
 @Test public void UnionType_Valid_11_RunTest() { runTest("UnionType_Valid_11"); }
 @Test public void UnionType_Valid_12_RunTest() { runTest("UnionType_Valid_12"); }
 @Test public void UnionType_Valid_13_RunTest() { runTest("UnionType_Valid_13"); }
 @Test public void UnionType_Valid_2_RunTest() { runTest("UnionType_Valid_2"); }
 @Ignore("Known Bug") @Test public void UnionType_Valid_3_RunTest() { runTest("UnionType_Valid_3"); }
 @Test public void UnionType_Valid_4_RunTest() { runTest("UnionType_Valid_4"); }
 @Test public void UnionType_Valid_5_RunTest() { runTest("UnionType_Valid_5"); }
 @Test public void UnionType_Valid_6_RunTest() { runTest("UnionType_Valid_6"); }
 @Test public void UnionType_Valid_7_RunTest() { runTest("UnionType_Valid_7"); }
 @Test public void UnionType_Valid_8_RunTest() { runTest("UnionType_Valid_8"); }
 @Test public void UnionType_Valid_9_RunTest() { runTest("UnionType_Valid_9"); }
 @Test public void VarDecl_Valid_1_RunTest() { runTest("VarDecl_Valid_1"); }
 @Test public void VarDecl_Valid_2_RunTest() { runTest("VarDecl_Valid_2"); }
}
