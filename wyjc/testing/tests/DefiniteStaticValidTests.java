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

public class DefiniteStaticValidTests extends TestHarness {
 public DefiniteStaticValidTests() {
  super("tests/valid/definite","tests/valid/definite","sysout");
 }

 @Test public void BoolAssign_Valid_1_StaticTest() { verificationRunTest("BoolAssign_Valid_1"); }
 @Test public void BoolAssign_Valid_2_StaticTest() { verificationRunTest("BoolAssign_Valid_2"); }
 @Test public void BoolAssign_Valid_3_StaticTest() { verificationRunTest("BoolAssign_Valid_3"); }
 @Test public void BoolAssign_Valid_4_StaticTest() { verificationRunTest("BoolAssign_Valid_4"); }
 @Test public void BoolFun_Valid_1_StaticTest() { verificationRunTest("BoolFun_Valid_1"); }
 @Test public void BoolIfElse_Valid_1_StaticTest() { verificationRunTest("BoolIfElse_Valid_1"); }
 @Test public void BoolList_Valid_1_StaticTest() { verificationRunTest("BoolList_Valid_1"); }
 @Test public void BoolRequires_Valid_1_StaticTest() { verificationRunTest("BoolRequires_Valid_1"); }
 @Test public void BoolReturn_Valid_1_StaticTest() { verificationRunTest("BoolReturn_Valid_1"); }
 @Test public void BoolRecord_Valid_1_StaticTest() { verificationRunTest("BoolRecord_Valid_1"); }
 @Test public void BoolRecord_Valid_2_StaticTest() { verificationRunTest("BoolRecord_Valid_2"); }
 @Test public void ConstrainedInt_Valid_1_StaticTest() { verificationRunTest("ConstrainedInt_Valid_1"); }
 @Test public void ConstrainedInt_Valid_10_StaticTest() { verificationRunTest("ConstrainedInt_Valid_10"); }
 @Test public void ConstrainedInt_Valid_11_StaticTest() { verificationRunTest("ConstrainedInt_Valid_11"); }
 @Test public void ConstrainedInt_Valid_2_StaticTest() { verificationRunTest("ConstrainedInt_Valid_2"); }
 @Test public void ConstrainedInt_Valid_3_StaticTest() { verificationRunTest("ConstrainedInt_Valid_3"); }
 @Test public void ConstrainedInt_Valid_4_StaticTest() { verificationRunTest("ConstrainedInt_Valid_4"); }
 @Test public void ConstrainedInt_Valid_5_StaticTest() { verificationRunTest("ConstrainedInt_Valid_5"); }
 @Test public void ConstrainedInt_Valid_6_StaticTest() { verificationRunTest("ConstrainedInt_Valid_6"); }
 @Test public void ConstrainedInt_Valid_7_StaticTest() { verificationRunTest("ConstrainedInt_Valid_7"); }
 @Test public void ConstrainedInt_Valid_8_StaticTest() { verificationRunTest("ConstrainedInt_Valid_8"); }
 @Test public void ConstrainedInt_Valid_9_StaticTest() { verificationRunTest("ConstrainedInt_Valid_9"); }
 @Test public void ConstrainedList_Valid_1_StaticTest() { verificationRunTest("ConstrainedList_Valid_1"); }
 @Test public void ConstrainedList_Valid_2_StaticTest() { verificationRunTest("ConstrainedList_Valid_2"); }
 @Test public void ConstrainedList_Valid_3_StaticTest() { verificationRunTest("ConstrainedList_Valid_3"); }
 @Test public void ConstrainedList_Valid_4_StaticTest() { verificationRunTest("ConstrainedList_Valid_4"); }
 @Test public void ConstrainedSet_Valid_1_StaticTest() { verificationRunTest("ConstrainedSet_Valid_1"); }
 @Test public void ConstrainedSet_Valid_2_StaticTest() { verificationRunTest("ConstrainedSet_Valid_2"); }
 @Test public void ConstrainedSet_Valid_3_StaticTest() { verificationRunTest("ConstrainedSet_Valid_3"); }
 @Test public void ConstrainedSet_Valid_4_StaticTest() { verificationRunTest("ConstrainedSet_Valid_4"); }
 @Test public void ConstrainedRecord_Valid_1_StaticTest() { verificationRunTest("ConstrainedRecord_Valid_1"); }
 @Test public void ConstrainedRecord_Valid_2_StaticTest() { verificationRunTest("ConstrainedRecord_Valid_2"); }
 @Test public void ConstrainedRecord_Valid_3_StaticTest() { verificationRunTest("ConstrainedRecord_Valid_3"); }
 @Test public void ConstrainedRecord_Valid_4_StaticTest() { verificationRunTest("ConstrainedRecord_Valid_4"); }
 @Test public void ConstrainedRecord_Valid_5_StaticTest() { verificationRunTest("ConstrainedRecord_Valid_5"); }
 @Test public void Define_Valid_1_StaticTest() { verificationRunTest("Define_Valid_1"); }
 @Test public void Define_Valid_2_StaticTest() { verificationRunTest("Define_Valid_2"); }
 @Test public void Define_Valid_3_StaticTest() { verificationRunTest("Define_Valid_3"); }
 @Test public void Define_Valid_4_StaticTest() { verificationRunTest("Define_Valid_4"); }
 @Test public void Ensures_Valid_1_StaticTest() { verificationRunTest("Ensures_Valid_1"); }
 @Test public void Ensures_Valid_2_StaticTest() { verificationRunTest("Ensures_Valid_2"); }
 @Test public void Ensures_Valid_3_StaticTest() { verificationRunTest("Ensures_Valid_3"); }
 @Test public void Ensures_Valid_4_StaticTest() { verificationRunTest("Ensures_Valid_4"); }
 @Test public void Ensures_Valid_5_StaticTest() { verificationRunTest("Ensures_Valid_5"); }
 @Test public void For_Valid_1_StaticTest() { verificationRunTest("For_Valid_1"); }
 @Test public void For_Valid_2_StaticTest() { verificationRunTest("For_Valid_2"); }
 @Test public void For_Valid_3_StaticTest() { verificationRunTest("For_Valid_3"); }
 @Test public void Function_Valid_1_StaticTest() { verificationRunTest("Function_Valid_1"); }
 @Test public void Function_Valid_10_StaticTest() { verificationRunTest("Function_Valid_10"); }
 @Test public void Function_Valid_11_StaticTest() { verificationRunTest("Function_Valid_11"); }
 @Test public void Function_Valid_12_StaticTest() { verificationRunTest("Function_Valid_12"); }
 @Test public void Function_Valid_13_StaticTest() { verificationRunTest("Function_Valid_13"); }
 @Test public void Function_Valid_2_StaticTest() { verificationRunTest("Function_Valid_2"); }
 @Test public void Function_Valid_3_StaticTest() { verificationRunTest("Function_Valid_3"); }
 @Test public void Function_Valid_4_StaticTest() { verificationRunTest("Function_Valid_4"); }
 @Test public void Function_Valid_5_StaticTest() { verificationRunTest("Function_Valid_5"); }
 @Test public void Function_Valid_6_StaticTest() { verificationRunTest("Function_Valid_6"); }
 @Test public void Function_Valid_7_StaticTest() { verificationRunTest("Function_Valid_7"); }
 @Test public void Function_Valid_8_StaticTest() { verificationRunTest("Function_Valid_8"); }
 @Test public void Function_Valid_9_StaticTest() { verificationRunTest("Function_Valid_9"); }
 @Test public void IfElse_Valid_1_StaticTest() { verificationRunTest("IfElse_Valid_1"); }
 @Test public void IfElse_Valid_2_StaticTest() { verificationRunTest("IfElse_Valid_2"); }
 @Test public void IfElse_Valid_3_StaticTest() { verificationRunTest("IfElse_Valid_3"); }
 @Test public void IntConst_Valid_1_StaticTest() { verificationRunTest("IntConst_Valid_1"); }
 @Test public void IntDefine_Valid_1_StaticTest() { verificationRunTest("IntDefine_Valid_1"); }
 @Test public void IntDiv_Valid_1_StaticTest() { verificationRunTest("IntDiv_Valid_1"); }
 @Test public void IntMul_Valid_1_RunTest() { verificationRunTest("IntMul_Valid_1"); }
 @Test public void IntEquals_Valid_1_StaticTest() { verificationRunTest("IntEquals_Valid_1"); }
 @Test public void IntOp_Valid_1_StaticTest() { verificationRunTest("IntOp_Valid_1"); }
 @Ignore("Known Bug") @Test public void IntersectionType_Valid_1_StaticTest() { verificationRunTest("IntersectionType_Valid_1"); }
 @Ignore("Known Bug") @Test public void IntersectionType_Valid_2_StaticTest() { verificationRunTest("IntersectionType_Valid_2"); }
 @Test public void ListAccess_Valid_1_StaticTest() { verificationRunTest("ListAccess_Valid_1"); }
 @Test public void ListAccess_Valid_2_StaticTest() { verificationRunTest("ListAccess_Valid_2"); }
 @Test public void ListAccess_Valid_3_StaticTest() { verificationRunTest("ListAccess_Valid_3"); }
 @Test public void ListAssign_Valid_1_StaticTest() { verificationRunTest("ListAssign_Valid_1"); }
 @Test public void ListAssign_Valid_2_StaticTest() { verificationRunTest("ListAssign_Valid_2"); }
 @Test public void ListAssign_Valid_3_StaticTest() { verificationRunTest("ListAssign_Valid_3"); }
 @Test public void ListAssign_Valid_4_StaticTest() { verificationRunTest("ListAssign_Valid_4"); }
 @Test public void ListAssign_Valid_5_StaticTest() { verificationRunTest("ListAssign_Valid_5"); }
 @Test public void ListAssign_Valid_6_StaticTest() { verificationRunTest("ListAssign_Valid_6"); }
 @Test public void ListAppend_Valid_1_StaticTest() { verificationRunTest("ListAppend_Valid_1"); }
 @Test public void ListAppend_Valid_2_StaticTest() { verificationRunTest("ListAppend_Valid_2"); }
 @Test public void ListAppend_Valid_3_StaticTest() { verificationRunTest("ListAppend_Valid_3"); }
 @Test public void ListAppend_Valid_4_StaticTest() { verificationRunTest("ListAppend_Valid_4"); }
 @Test public void ListAppend_Valid_5_StaticTest() { verificationRunTest("ListAppend_Valid_5"); }
 @Test public void ListAppend_Valid_6_StaticTest() { verificationRunTest("ListAppend_Valid_6"); }
 @Test public void ListConversion_Valid_1_StaticTest() { verificationRunTest("ListConversion_Valid_1"); }
 @Test public void ListElemOf_Valid_1_StaticTest() { verificationRunTest("ListElemOf_Valid_1"); }
 @Test public void ListEmpty_Valid_1_StaticTest() { verificationRunTest("ListEmpty_Valid_1"); }
 @Test public void ListEquals_Valid_1_StaticTest() { verificationRunTest("ListEquals_Valid_1"); }
 @Test public void ListGenerator_Valid_1_StaticTest() { verificationRunTest("ListGenerator_Valid_1"); }
 @Test public void ListGenerator_Valid_2_StaticTest() { verificationRunTest("ListGenerator_Valid_2"); }
 @Test public void ListGenerator_Valid_3_StaticTest() { verificationRunTest("ListGenerator_Valid_3"); }
 @Test public void ListLength_Valid_1_StaticTest() { verificationRunTest("ListLength_Valid_1"); }
 @Test public void ListLength_Valid_2_StaticTest() { verificationRunTest("ListLength_Valid_2"); }
 @Test public void ListSublist_Valid_1_StaticTest() { verificationRunTest("ListSublist_Valid_1"); }
 @Test public void ListSublist_Valid_2_StaticTest() { verificationRunTest("ListSublist_Valid_2"); }
 @Test public void MethodCall_Valid_3_StaticTest() { verificationRunTest("MethodCall_Valid_3"); }
 @Test public void MethodCall_Valid_4_StaticTest() { verificationRunTest("MethodCall_Valid_4"); }
 @Test public void MethodCall_Valid_5_StaticTest() { verificationRunTest("MethodCall_Valid_5"); }
 @Test public void MethodCall_Valid_6_StaticTest() { verificationRunTest("MethodCall_Valid_6"); }
 @Test public void Print_Valid_1_StaticTest() { verificationRunTest("Print_Valid_1"); }
 @Test public void ProcessAccess_Valid_1_StaticTest() { verificationRunTest("ProcessAccess_Valid_1"); }
 @Test public void ProcessAccess_Valid_2_StaticTest() { verificationRunTest("ProcessAccess_Valid_2"); }
 @Test public void Process_Valid_1_StaticTest() { verificationRunTest("Process_Valid_1"); }
 @Test public void Process_Valid_2_StaticTest() { verificationRunTest("Process_Valid_2"); }
 @Test public void Process_Valid_3_StaticTest() { verificationRunTest("Process_Valid_3"); }
 @Test public void Quantifiers_Valid_1_StaticTest() { verificationRunTest("Quantifiers_Valid_1"); }
 @Test public void RealConst_Valid_1_StaticTest() { verificationRunTest("RealConst_Valid_1"); }
 @Test public void RealDiv_Valid_1_StaticTest() { verificationRunTest("RealDiv_Valid_1"); }
 @Test public void RealDiv_Valid_2_StaticTest() { verificationRunTest("RealDiv_Valid_2"); }
 @Test public void RealDiv_Valid_3_StaticTest() { verificationRunTest("RealDiv_Valid_3"); }
 @Test public void RealNeg_Valid_1_StaticTest() { verificationRunTest("RealNeg_Valid_1"); }
 @Test public void RealSub_Valid_1_StaticTest() { verificationRunTest("RealSub_Valid_1"); }
 @Test public void RecursiveType_Valid_1_StaticTest() { verificationRunTest("RecursiveType_Valid_1"); }
 @Test public void RecursiveType_Valid_2_StaticTest() { verificationRunTest("RecursiveType_Valid_2"); }
 @Test public void RecursiveType_Valid_3_StaticTest() { verificationRunTest("RecursiveType_Valid_3"); } 
 @Test public void RecursiveType_Valid_4_StaticTest() { verificationRunTest("RecursiveType_Valid_4"); }
 @Test public void RecursiveType_Valid_5_StaticTest() { verificationRunTest("RecursiveType_Valid_5"); }
 @Test public void RecursiveType_Valid_6_StaticTest() { verificationRunTest("RecursiveType_Valid_6"); }
 @Test public void RecursiveType_Valid_7_StaticTest() { verificationRunTest("RecursiveType_Valid_7"); }
 @Test public void RecursiveType_Valid_8_StaticTest() { verificationRunTest("RecursiveType_Valid_8"); }
 @Test public void RecursiveType_Valid_9_StaticTest() { verificationRunTest("RecursiveType_Valid_9"); }
 @Test public void RecursiveType_Valid_10_StaticTest() { verificationRunTest("RecursiveType_Valid_10"); }
 @Test public void Requires_Valid_1_StaticTest() { verificationRunTest("Requires_Valid_1"); }
 @Test public void Resolution_Valid_1_StaticTest() { verificationRunTest("Resolution_Valid_1"); }
 @Test public void SetAssign_Valid_1_StaticTest() { verificationRunTest("SetAssign_Valid_1"); }
 @Test public void SetComprehension_Valid_1_StaticTest() { verificationRunTest("SetComprehension_Valid_1"); }
 @Test public void SetComprehension_Valid_2_StaticTest() { verificationRunTest("SetComprehension_Valid_2"); }
 @Test public void SetComprehension_Valid_3_StaticTest() { verificationRunTest("SetComprehension_Valid_3"); }
 @Test public void SetComprehension_Valid_4_StaticTest() { verificationRunTest("SetComprehension_Valid_4"); }
 @Test public void SetComprehension_Valid_5_StaticTest() { verificationRunTest("SetComprehension_Valid_5"); }
 @Test public void SetComprehension_Valid_6_StaticTest() { verificationRunTest("SetComprehension_Valid_6"); }
 @Test public void SetComprehension_Valid_7_StaticTest() { verificationRunTest("SetComprehension_Valid_7"); }
 @Test public void SetConversion_Valid_1_StaticTest() { verificationRunTest("SetConversion_Valid_1"); }
 @Test public void SetDefine_Valid_1_StaticTest() { verificationRunTest("SetDefine_Valid_1"); }
 @Test public void SetElemOf_Valid_1_StaticTest() { verificationRunTest("SetElemOf_Valid_1"); }
 @Test public void SetEmpty_Valid_1_StaticTest() { verificationRunTest("SetEmpty_Valid_1"); }
 @Test public void SetGenerator_Valid_1_StaticTest() { verificationRunTest("SetGenerator_Valid_1"); }
 @Test public void SetIntersect_Valid_1_StaticTest() { verificationRunTest("SetIntersect_Valid_1"); }
 @Test public void SetIntersect_Valid_2_StaticTest() { verificationRunTest("SetIntersect_Valid_2"); }
 @Test public void SetIntersection_Valid_1_StaticTest() { verificationRunTest("SetIntersection_Valid_1"); }
 @Test public void SetIntersection_Valid_2_StaticTest() { verificationRunTest("SetIntersection_Valid_2"); }
 @Test public void SetIntersection_Valid_3_StaticTest() { verificationRunTest("SetIntersection_Valid_3"); }
 @Test public void SetLength_Valid_1_StaticTest() { verificationRunTest("SetLength_Valid_1"); }
 @Test public void SetSubset_Valid_1_StaticTest() { verificationRunTest("SetSubset_Valid_1"); }
 @Test public void SetSubset_Valid_2_StaticTest() { verificationRunTest("SetSubset_Valid_2"); }
 @Test public void SetSubset_Valid_3_StaticTest() { verificationRunTest("SetSubset_Valid_3"); }
 @Test public void SetSubset_Valid_4_StaticTest() { verificationRunTest("SetSubset_Valid_4"); }
 @Test public void SetSubset_Valid_5_StaticTest() { verificationRunTest("SetSubset_Valid_5"); }
 @Test public void SetSubset_Valid_6_StaticTest() { verificationRunTest("SetSubset_Valid_6"); }
 @Test public void SetSubset_Valid_7_StaticTest() { verificationRunTest("SetSubset_Valid_7"); }
 @Test public void SetUnion_Valid_1_StaticTest() { verificationRunTest("SetUnion_Valid_1"); }
 @Test public void SetUnion_Valid_2_StaticTest() { verificationRunTest("SetUnion_Valid_2"); }
 @Test public void SetUnion_Valid_3_StaticTest() { verificationRunTest("SetUnion_Valid_3"); }
 @Test public void SetUnion_Valid_4_StaticTest() { verificationRunTest("SetUnion_Valid_4"); }
 @Test public void SetUnion_Valid_5_StaticTest() { verificationRunTest("SetUnion_Valid_5"); }
 @Test public void SetUnion_Valid_6_StaticTest() { verificationRunTest("SetUnion_Valid_6"); }
 @Test public void Subtype_Valid_3_StaticTest() { verificationRunTest("Subtype_Valid_3"); }
 @Test public void Subtype_Valid_4_StaticTest() { verificationRunTest("Subtype_Valid_4"); }
 @Test public void Subtype_Valid_5_StaticTest() { verificationRunTest("Subtype_Valid_5"); }
 @Test public void Subtype_Valid_6_StaticTest() { verificationRunTest("Subtype_Valid_6"); }
 @Test public void Subtype_Valid_7_StaticTest() { verificationRunTest("Subtype_Valid_7"); }
 @Test public void Subtype_Valid_8_StaticTest() { verificationRunTest("Subtype_Valid_8"); }
 @Test public void Subtype_Valid_9_StaticTest() { verificationRunTest("Subtype_Valid_9"); }
 @Test public void RecordAccess_Valid_1_StaticTest() { verificationRunTest("RecordAccess_Valid_1"); }
 @Test public void RecordAssign_Valid_1_StaticTest() { verificationRunTest("RecordAssign_Valid_1"); }
 @Test public void RecordAssign_Valid_2_StaticTest() { verificationRunTest("RecordAssign_Valid_2"); }
 @Test public void RecordAssign_Valid_3_StaticTest() { verificationRunTest("RecordAssign_Valid_3"); }
 @Test public void RecordAssign_Valid_4_StaticTest() { verificationRunTest("RecordAssign_Valid_4"); }
 @Test public void RecordAssign_Valid_5_StaticTest() { verificationRunTest("RecordAssign_Valid_5"); }
 @Test public void RecordConversion_Valid_1_StaticTest() { verificationRunTest("RecordConversion_Valid_1"); }
 @Test public void RecordDefine_Valid_1_StaticTest() { verificationRunTest("RecordDefine_Valid_1"); }
 @Test public void TupleType_Valid_1_StaticTest() { verificationRunTest("TupleType_Valid_1"); }
 @Test public void TupleType_Valid_2_StaticTest() { verificationRunTest("TupleType_Valid_2"); }
 @Test public void TupleType_Valid_3_StaticTest() { verificationRunTest("TupleType_Valid_3"); }
 @Test public void TypeEquals_Valid_1_StaticTest() { verificationRunTest("TypeEquals_Valid_1"); }
 @Test public void TypeEquals_Valid_2_StaticTest() { verificationRunTest("TypeEquals_Valid_2"); }
 @Test public void TypeEquals_Valid_3_StaticTest() { verificationRunTest("TypeEquals_Valid_3"); }
 @Test public void TypeEquals_Valid_4_StaticTest() { verificationRunTest("TypeEquals_Valid_4"); }
 @Test public void TypeEquals_Valid_5_StaticTest() { verificationRunTest("TypeEquals_Valid_5"); }
 @Test public void TypeEquals_Valid_6_StaticTest() { verificationRunTest("TypeEquals_Valid_6"); }
 @Test public void TypeEquals_Valid_7_StaticTest() { verificationRunTest("TypeEquals_Valid_7"); }
 @Test public void TypeEquals_Valid_8_StaticTest() { verificationRunTest("TypeEquals_Valid_8"); }
 @Test public void TypeEquals_Valid_9_StaticTest() { verificationRunTest("TypeEquals_Valid_9"); }
 @Test public void TypeEquals_Valid_10_StaticTest() { verificationRunTest("TypeEquals_Valid_10"); }
 @Test public void TypeEquals_Valid_11_StaticTest() { verificationRunTest("TypeEquals_Valid_11"); }
 @Test public void TypeEquals_Valid_12_StaticTest() { verificationRunTest("TypeEquals_Valid_12"); }
 @Test public void TypeEquals_Valid_13_StaticTest() { verificationRunTest("TypeEquals_Valid_13"); }
 @Test public void TypeEquals_Valid_14_StaticTest() { verificationRunTest("TypeEquals_Valid_14"); }
 @Test public void TypeEquals_Valid_15_StaticTest() { verificationRunTest("TypeEquals_Valid_15"); }
 @Test public void TypeEquals_Valid_16_StaticTest() { verificationRunTest("TypeEquals_Valid_16"); }
 @Test public void UnionType_Valid_1_StaticTest() { verificationRunTest("UnionType_Valid_1"); }
 @Test public void UnionType_Valid_10_StaticTest() { verificationRunTest("UnionType_Valid_10"); }
 @Test public void UnionType_Valid_11_StaticTest() { verificationRunTest("UnionType_Valid_11"); }
 @Test public void UnionType_Valid_12_StaticTest() { verificationRunTest("UnionType_Valid_12"); }
 @Test public void UnionType_Valid_13_StaticTest() { verificationRunTest("UnionType_Valid_13"); }
 @Test public void UnionType_Valid_14_StaticTest() { verificationRunTest("UnionType_Valid_14"); }
 @Test public void UnionType_Valid_15_StaticTest() { verificationRunTest("UnionType_Valid_15"); }
 @Test public void UnionType_Valid_16_StaticTest() { verificationRunTest("UnionType_Valid_16"); }
 @Test public void UnionType_Valid_2_StaticTest() { verificationRunTest("UnionType_Valid_2"); }
 @Ignore("Known Bug") @Test public void UnionType_Valid_3_StaticTest() { verificationRunTest("UnionType_Valid_3"); }
 @Test public void UnionType_Valid_4_StaticTest() { verificationRunTest("UnionType_Valid_4"); }
 @Test public void UnionType_Valid_5_StaticTest() { verificationRunTest("UnionType_Valid_5"); }
 @Test public void UnionType_Valid_6_StaticTest() { verificationRunTest("UnionType_Valid_6"); }
 @Test public void UnionType_Valid_7_StaticTest() { verificationRunTest("UnionType_Valid_7"); }
 @Test public void UnionType_Valid_8_StaticTest() { verificationRunTest("UnionType_Valid_8"); }
 @Test public void UnionType_Valid_9_StaticTest() { verificationRunTest("UnionType_Valid_9"); }
 @Test public void VarDecl_Valid_1_StaticTest() { verificationRunTest("VarDecl_Valid_1"); }
 @Test public void VarDecl_Valid_2_StaticTest() { verificationRunTest("VarDecl_Valid_2"); }
 @Test public void While_Valid_1_StaticTest() { verificationRunTest("While_Valid_1"); }
 @Test public void While_Valid_2_StaticTest() { verificationRunTest("While_Valid_2"); }
 @Test public void While_Valid_3_StaticTest() { verificationRunTest("While_Valid_3"); }
 @Test public void While_Valid_4_StaticTest() { verificationRunTest("While_Valid_4"); }
 @Test public void While_Valid_5_StaticTest() { verificationRunTest("While_Valid_5"); }
 @Test public void While_Valid_6_StaticTest() { verificationRunTest("While_Valid_6"); }
}
