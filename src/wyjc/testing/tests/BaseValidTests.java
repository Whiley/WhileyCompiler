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

public class BaseValidTests extends TestHarness {
 public BaseValidTests() {
  super("tests/base/valid","tests/base/valid","sysout");
 }

 @Test public void BoolAssign_Valid_1_RuntimeTest() { runTest("BoolAssign_Valid_1"); }
 @Test public void BoolAssign_Valid_2_RuntimeTest() { runTest("BoolAssign_Valid_2"); }
 @Test public void BoolFun_Valid_1_RuntimeTest() { runTest("BoolFun_Valid_1"); }
 @Test public void BoolIfElse_Valid_1_RuntimeTest() { runTest("BoolIfElse_Valid_1"); }
 @Test public void BoolList_Valid_1_RuntimeTest() { runTest("BoolList_Valid_1"); }
 @Test public void BoolRecord_Valid_1_RuntimeTest() { runTest("BoolRecord_Valid_1"); }
 @Test public void BoolRecord_Valid_2_RuntimeTest() { runTest("BoolRecord_Valid_2"); }
 @Test public void BoolReturn_Valid_1_RuntimeTest() { runTest("BoolReturn_Valid_1"); }
 @Test public void ConstrainedInt_Valid_2_RuntimeTest() { runTest("ConstrainedInt_Valid_2"); }
 @Test public void ConstrainedInt_Valid_9_RuntimeTest() { runTest("ConstrainedInt_Valid_9"); }
 @Test public void ConstrainedList_Valid_5_RuntimeTest() { runTest("ConstrainedList_Valid_5"); }
 @Test public void ConstrainedRecord_Valid_1_RuntimeTest() { runTest("ConstrainedRecord_Valid_1"); }
 @Test public void ConstrainedRecord_Valid_2_RuntimeTest() { runTest("ConstrainedRecord_Valid_2"); }
 @Test public void ConstrainedRecord_Valid_3_RuntimeTest() { runTest("ConstrainedRecord_Valid_3"); }
 @Test public void Define_Valid_1_RuntimeTest() { runTest("Define_Valid_1"); }
 @Test public void Define_Valid_2_RuntimeTest() { runTest("Define_Valid_2"); }
 @Test public void Define_Valid_3_RuntimeTest() { runTest("Define_Valid_3"); }
 @Test public void Define_Valid_4_RuntimeTest() { runTest("Define_Valid_4"); }
 @Test public void For_Valid_1_RuntimeTest() { runTest("For_Valid_1"); }
 @Test public void Function_Valid_1_RuntimeTest() { runTest("Function_Valid_1"); }
 @Test public void Function_Valid_10_RuntimeTest() { runTest("Function_Valid_10"); }
 @Test public void Function_Valid_7_RuntimeTest() { runTest("Function_Valid_7"); }
 @Test public void Function_Valid_9_RuntimeTest() { runTest("Function_Valid_9"); }
 @Test public void IfElse_Valid_1_RuntimeTest() { runTest("IfElse_Valid_1"); }
 @Test public void IfElse_Valid_2_RuntimeTest() { runTest("IfElse_Valid_2"); }
 @Test public void IfElse_Valid_3_RuntimeTest() { runTest("IfElse_Valid_3"); }
 @Test public void IntConst_Valid_1_RuntimeTest() { runTest("IntConst_Valid_1"); }
 @Test public void IntEquals_Valid_1_RuntimeTest() { runTest("IntEquals_Valid_1"); }
 @Test public void IntMul_Valid_1_RuntimeTest() { runTest("IntMul_Valid_1"); }
 @Test public void IntOp_Valid_1_RuntimeTest() { runTest("IntOp_Valid_1"); }
 @Test public void ListAccess_Valid_3_RuntimeTest() { runTest("ListAccess_Valid_3"); }
 @Test public void ListAppend_Valid_1_RuntimeTest() { runTest("ListAppend_Valid_1"); }
 @Test public void ListAppend_Valid_2_RuntimeTest() { runTest("ListAppend_Valid_2"); }
 @Test public void ListAppend_Valid_3_RuntimeTest() { runTest("ListAppend_Valid_3"); }
 @Test public void ListAppend_Valid_4_RuntimeTest() { runTest("ListAppend_Valid_4"); }
 @Test public void ListAssign_Valid_1_RuntimeTest() { runTest("ListAssign_Valid_1"); }
 @Test public void ListAssign_Valid_2_RuntimeTest() { runTest("ListAssign_Valid_2"); }
 @Test public void ListAssign_Valid_3_RuntimeTest() { runTest("ListAssign_Valid_3"); }
 @Test public void ListAssign_Valid_4_RuntimeTest() { runTest("ListAssign_Valid_4"); }
 @Test public void ListConversion_Valid_1_RuntimeTest() { runTest("ListConversion_Valid_1"); }
 @Test public void ListElemOf_Valid_1_RuntimeTest() { runTest("ListElemOf_Valid_1"); }
 @Test public void ListEmpty_Valid_1_RuntimeTest() { runTest("ListEmpty_Valid_1"); }
 @Test public void ListEquals_Valid_1_RuntimeTest() { runTest("ListEquals_Valid_1"); }
 @Test public void ListGenerator_Valid_3_RuntimeTest() { runTest("ListGenerator_Valid_3"); }
 @Test public void ListLength_Valid_1_RuntimeTest() { runTest("ListLength_Valid_1"); }
 @Test public void ListLength_Valid_2_RuntimeTest() { runTest("ListLength_Valid_2"); }
 @Test public void ListSublist_Valid_2_RuntimeTest() { runTest("ListSublist_Valid_2"); }
 @Test public void MethodCall_Valid_3_RuntimeTest() { runTest("MethodCall_Valid_3"); }
 @Test public void MethodCall_Valid_4_RuntimeTest() { runTest("MethodCall_Valid_4"); }
 @Test public void MethodCall_Valid_5_RuntimeTest() { runTest("MethodCall_Valid_5"); }
 @Test public void MethodCall_Valid_6_RuntimeTest() { runTest("MethodCall_Valid_6"); }
 @Test public void Print_Valid_1_RuntimeTest() { runTest("Print_Valid_1"); }
 @Test public void ProcessAccess_Valid_1_RuntimeTest() { runTest("ProcessAccess_Valid_1"); }
 @Test public void ProcessAccess_Valid_2_RuntimeTest() { runTest("ProcessAccess_Valid_2"); }
 @Test public void Process_Valid_1_RuntimeTest() { runTest("Process_Valid_1"); }
 @Test public void Process_Valid_3_RuntimeTest() { runTest("Process_Valid_3"); }
 @Test public void Process_Valid_4_RuntimeTest() { runTest("Process_Valid_4"); }
 @Test public void Process_Valid_5_RuntimeTest() { runTest("Process_Valid_5"); }
 @Test public void Process_Valid_6_RuntimeTest() { runTest("Process_Valid_6"); }
 @Test public void Process_Valid_7_RuntimeTest() { runTest("Process_Valid_7"); }
 @Test public void Process_Valid_8_RuntimeTest() { runTest("Process_Valid_8"); }
 @Test public void RealConst_Valid_1_RuntimeTest() { runTest("RealConst_Valid_1"); }
 @Test public void RecordAccess_Valid_1_RuntimeTest() { runTest("RecordAccess_Valid_1"); }
 @Test public void RecordAssign_Valid_3_RuntimeTest() { runTest("RecordAssign_Valid_3"); }
 @Test public void RecordConversion_Valid_1_RuntimeTest() { runTest("RecordConversion_Valid_1"); }
 @Test public void RecursiveType_Valid_1_RuntimeTest() { runTest("RecursiveType_Valid_1"); }
 @Test public void RecursiveType_Valid_10_RuntimeTest() { runTest("RecursiveType_Valid_10"); }
 @Test public void RecursiveType_Valid_11_RuntimeTest() { runTest("RecursiveType_Valid_11"); }
 @Test public void RecursiveType_Valid_12_RuntimeTest() { runTest("RecursiveType_Valid_12"); }
 @Test public void RecursiveType_Valid_13_RuntimeTest() { runTest("RecursiveType_Valid_13"); }
 @Test public void RecursiveType_Valid_14_RuntimeTest() { runTest("RecursiveType_Valid_14"); }
 @Test public void RecursiveType_Valid_2_RuntimeTest() { runTest("RecursiveType_Valid_2"); }
 @Test public void RecursiveType_Valid_4_RuntimeTest() { runTest("RecursiveType_Valid_4"); }
 @Test public void RecursiveType_Valid_7_RuntimeTest() { runTest("RecursiveType_Valid_7"); }
 @Test public void Resolution_Valid_1_RuntimeTest() { runTest("Resolution_Valid_1"); }
 @Test public void SetComprehension_Valid_1_RuntimeTest() { runTest("SetComprehension_Valid_1"); }
 @Test public void SetComprehension_Valid_2_RuntimeTest() { runTest("SetComprehension_Valid_2"); }
 @Test public void SetComprehension_Valid_3_RuntimeTest() { runTest("SetComprehension_Valid_3"); }
 @Test public void SetComprehension_Valid_4_RuntimeTest() { runTest("SetComprehension_Valid_4"); }
 @Test public void SetComprehension_Valid_5_RuntimeTest() { runTest("SetComprehension_Valid_5"); }
 @Test public void SetConversion_Valid_1_RuntimeTest() { runTest("SetConversion_Valid_1"); }
 @Test public void SetElemOf_Valid_1_RuntimeTest() { runTest("SetElemOf_Valid_1"); }
 @Test public void SetEmpty_Valid_1_RuntimeTest() { runTest("SetEmpty_Valid_1"); }
 @Test public void SetGenerator_Valid_1_RuntimeTest() { runTest("SetGenerator_Valid_1"); }
 @Test public void SetIntersect_Valid_1_RuntimeTest() { runTest("SetIntersect_Valid_1"); }
 @Test public void SetIntersect_Valid_2_RuntimeTest() { runTest("SetIntersect_Valid_2"); }
 @Test public void SetLength_Valid_1_RuntimeTest() { runTest("SetLength_Valid_1"); }
 @Test public void SetUnion_Valid_1_RuntimeTest() { runTest("SetUnion_Valid_1"); }
 @Test public void SetUnion_Valid_2_RuntimeTest() { runTest("SetUnion_Valid_2"); }
 @Test public void SetUnion_Valid_3_RuntimeTest() { runTest("SetUnion_Valid_3"); }
 @Test public void SetUnion_Valid_4_RuntimeTest() { runTest("SetUnion_Valid_4"); }
 @Test public void TupleType_Valid_1_RuntimeTest() { runTest("TupleType_Valid_1"); }
 @Test public void TupleType_Valid_2_RuntimeTest() { runTest("TupleType_Valid_2"); }
 @Test public void TupleType_Valid_3_RuntimeTest() { runTest("TupleType_Valid_3"); }
 @Test public void TypeEquals_Valid_11_RuntimeTest() { runTest("TypeEquals_Valid_11"); }
 @Test public void TypeEquals_Valid_12_RuntimeTest() { runTest("TypeEquals_Valid_12"); }
 @Test public void TypeEquals_Valid_14_RuntimeTest() { runTest("TypeEquals_Valid_14"); }
 @Test public void TypeEquals_Valid_15_RuntimeTest() { runTest("TypeEquals_Valid_15"); }
 @Test public void TypeEquals_Valid_16_RuntimeTest() { runTest("TypeEquals_Valid_16"); }
 @Test public void TypeEquals_Valid_17_RuntimeTest() { runTest("TypeEquals_Valid_17"); }
 @Test public void TypeEquals_Valid_18_RuntimeTest() { runTest("TypeEquals_Valid_18"); }
 @Test public void TypeEquals_Valid_19_RuntimeTest() { runTest("TypeEquals_Valid_19"); }
 @Test public void TypeEquals_Valid_2_RuntimeTest() { runTest("TypeEquals_Valid_2"); }
 @Test public void TypeEquals_Valid_20_RuntimeTest() { runTest("TypeEquals_Valid_20"); }
 @Test public void TypeEquals_Valid_21_RuntimeTest() { runTest("TypeEquals_Valid_21"); }
 @Test public void TypeEquals_Valid_22_RuntimeTest() { runTest("TypeEquals_Valid_22"); }
 @Test public void TypeEquals_Valid_23_RuntimeTest() { runTest("TypeEquals_Valid_23"); }
 @Test public void TypeEquals_Valid_24_RuntimeTest() { runTest("TypeEquals_Valid_24"); }
 @Test public void TypeEquals_Valid_25_RuntimeTest() { runTest("TypeEquals_Valid_25"); }
 @Test public void TypeEquals_Valid_3_RuntimeTest() { runTest("TypeEquals_Valid_3"); }
 @Test public void TypeEquals_Valid_4_RuntimeTest() { runTest("TypeEquals_Valid_4"); }
 @Test public void TypeEquals_Valid_7_RuntimeTest() { runTest("TypeEquals_Valid_7"); }
 @Test public void UnionType_Valid_1_RuntimeTest() { runTest("UnionType_Valid_1"); }
 @Test public void UnionType_Valid_10_RuntimeTest() { runTest("UnionType_Valid_10"); }
 @Test public void UnionType_Valid_11_RuntimeTest() { runTest("UnionType_Valid_11"); }
 @Test public void UnionType_Valid_13_RuntimeTest() { runTest("UnionType_Valid_13"); }
 @Test public void UnionType_Valid_14_RuntimeTest() { runTest("UnionType_Valid_14"); }
 @Test public void UnionType_Valid_15_RuntimeTest() { runTest("UnionType_Valid_15"); }
 @Test public void UnionType_Valid_16_RuntimeTest() { runTest("UnionType_Valid_16"); }
 @Test public void UnionType_Valid_2_RuntimeTest() { runTest("UnionType_Valid_2"); } 
 @Test public void UnionType_Valid_8_RuntimeTest() { runTest("UnionType_Valid_8"); }
 @Test public void UnionType_Valid_9_RuntimeTest() { runTest("UnionType_Valid_9"); }
 @Test public void VarDecl_Valid_1_RuntimeTest() { runTest("VarDecl_Valid_1"); }
 @Test public void While_Valid_1_RuntimeTest() { runTest("While_Valid_1"); }
}
