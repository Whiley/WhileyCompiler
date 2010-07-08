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

public class DefiniteRuntimeInvalidTests extends TestHarness {
 public DefiniteRuntimeInvalidTests() {
  super("tests/invalid/definite","tests/invalid/definite","sysout");
 }

 @Test public void ConstrainedInt_Invalid_1_RunTest() { runtimeFailTest("ConstrainedInt_Invalid_1"); }
 @Test public void ConstrainedInt_Invalid_10_RunTest() { runtimeFailTest("ConstrainedInt_Invalid_10"); }
 
 @Test public void ConstrainedInt_Invalid_2_RunTest() { runtimeFailTest("ConstrainedInt_Invalid_2"); }
 @Test public void ConstrainedInt_Invalid_3_RunTest() { runtimeFailTest("ConstrainedInt_Invalid_3"); }
 @Test public void ConstrainedInt_Invalid_4_RunTest() { runtimeFailTest("ConstrainedInt_Invalid_4"); }
 @Test public void ConstrainedInt_Invalid_5_RunTest() { runtimeFailTest("ConstrainedInt_Invalid_5"); }
 @Test public void ConstrainedInt_Invalid_6_RunTest() { runtimeFailTest("ConstrainedInt_Invalid_6"); }
 @Test public void ConstrainedInt_Invalid_7_RunTest() { runtimeFailTest("ConstrainedInt_Invalid_7"); }
 @Test public void ConstrainedInt_Invalid_8_RunTest() { runtimeFailTest("ConstrainedInt_Invalid_8"); }
 @Test public void ConstrainedInt_Invalid_9_RunTest() { runtimeFailTest("ConstrainedInt_Invalid_9"); }
 @Test public void ConstrainedList_Invalid_1_RunTest() { runtimeFailTest("ConstrainedList_Invalid_1"); }
 @Test public void ConstrainedList_Invalid_2_RunTest() { runtimeFailTest("ConstrainedList_Invalid_2"); }
 @Test public void ConstrainedList_Invalid_3_RunTest() { runtimeFailTest("ConstrainedList_Invalid_3"); }
 
 @Test public void ConstrainedSet_Invalid_2_RunTest() { runtimeFailTest("ConstrainedSet_Invalid_2"); }
 @Test public void ConstrainedSet_Invalid_3_RunTest() { runtimeFailTest("ConstrainedSet_Invalid_3"); }
 @Test public void ConstrainedTuple_Invalid_1_RunTest() { runtimeFailTest("ConstrainedTuple_Invalid_1"); }
 @Test public void Ensures_Invalid_1_RunTest() { runtimeFailTest("Ensures_Invalid_1"); }
 @Test public void IntDiv_Invalid_1_RunTest() { runtimeFailTest("IntDiv_Invalid_1"); }
 @Test public void ListAppend_Invalid_3_RunTest() { runtimeFailTest("ListAppend_Invalid_3"); }
 @Test public void ListAppend_Invalid_4_RunTest() { runtimeFailTest("ListAppend_Invalid_4"); }
 @Test public void ListAppend_Invalid_5_RunTest() { runtimeFailTest("ListAppend_Invalid_5"); }
 @Test public void ListAccess_Invalid_1_RunTest() { runtimeFailTest("ListAccess_Invalid_1"); }
 @Test public void ListAccess_Invalid_2_RunTest() { runtimeFailTest("ListAccess_Invalid_2"); }
 @Test public void ListAssign_Invalid_1_RunTest() { runtimeFailTest("ListAssign_Invalid_1"); }
 @Test public void ListAssign_Invalid_2_RunTest() { runtimeFailTest("ListAssign_Invalid_2"); }
 @Test public void ListElemOf_Invalid_1_RunTest() { runtimeFailTest("ListElemOf_Invalid_1"); } 
 @Test public void ListLength_Invalid_1_RunTest() { runtimeFailTest("ListLength_Invalid_1"); }
 @Test public void ListLength_Invalid_2_RunTest() { runtimeFailTest("ListLength_Invalid_2"); }
 @Ignore("Known Bug") @Test public void Process_Invalid_1_RunTest() { runtimeFailTest("Process_Invalid_1"); }
 @Ignore("Known Bug") @Test public void Process_Invalid_2_RunTest() { runtimeFailTest("Process_Invalid_2"); }
 @Test public void Quantifiers_Invalid_1_RunTest() { runtimeFailTest("Quantifiers_Invalid_1"); }
 @Test public void Quantifiers_Invalid_2_RunTest() { runtimeFailTest("Quantifiers_Invalid_2"); }
 @Test public void Quantifiers_Invalid_3_RunTest() { runtimeFailTest("Quantifiers_Invalid_3"); }
 @Test public void Quantifiers_Invalid_4_RunTest() { runtimeFailTest("Quantifiers_Invalid_4"); }
 @Test public void RealDiv_Invalid_1_RunTest() { runtimeFailTest("RealDiv_Invalid_1"); }
 @Test public void RealMul_Invalid_1_RunTest() { runtimeFailTest("RealMul_Invalid_1"); }
 @Test public void RecursiveType_Invalid_3_RunTest() { runtimeFailTest("RecursiveType_Invalid_3"); }
 @Test public void RecursiveType_Invalid_5_RunTest() { runtimeFailTest("RecursiveType_Invalid_5"); }
 @Test public void RecursiveType_Invalid_6_RunTest() { runtimeFailTest("RecursiveType_Invalid_6"); }
 @Test public void RecursiveType_Invalid_7_RunTest() { runtimeFailTest("RecursiveType_Invalid_7"); }
 @Test public void Requires_Invalid_1_RunTest() { runtimeFailTest("Requires_Invalid_1"); }
 @Test public void SetAssign_Invalid_1_RunTest() { runtimeFailTest("SetAssign_Invalid_1"); }
 @Test public void SetElemOf_Invalid_1_RunTest() { runtimeFailTest("SetElemOf_Invalid_1"); } 
 @Test public void SetIntersection_Invalid_1_RunTest() { runtimeFailTest("SetIntersection_Invalid_1"); }
 @Test public void SetIntersection_Invalid_2_RunTest() { runtimeFailTest("SetIntersection_Invalid_2"); }
 @Test public void SetSubset_Invalid_1_RunTest() { runtimeFailTest("SetSubset_Invalid_1"); }
 @Test public void SetSubset_Invalid_2_RunTest() { runtimeFailTest("SetSubset_Invalid_2"); }
 @Test public void SetSubset_Invalid_3_RunTest() { runtimeFailTest("SetSubset_Invalid_3"); }
 @Test public void SetSubset_Invalid_4_RunTest() { runtimeFailTest("SetSubset_Invalid_4"); }
 @Test public void SetSubset_Invalid_5_RunTest() { runtimeFailTest("SetSubset_Invalid_5"); }
 @Test public void SetSubset_Invalid_6_RunTest() { runtimeFailTest("SetSubset_Invalid_6"); }
 @Test public void SetUnion_Invalid_1_RunTest() { runtimeFailTest("SetUnion_Invalid_1"); }
 @Test public void SetUnion_Invalid_2_RunTest() { runtimeFailTest("SetUnion_Invalid_2"); }
 @Test public void TupleAssign_Invalid_1_RunTest() { runtimeFailTest("TupleAssign_Invalid_1"); }
 @Test public void TupleAssign_Invalid_2_RunTest() { runtimeFailTest("TupleAssign_Invalid_2"); }
 @Test public void TupleAssign_Invalid_3_RunTest() { runtimeFailTest("TupleAssign_Invalid_3"); }
 @Test public void TypeEquals_Invalid_3_RunTest() { runtimeFailTest("TypeEquals_Invalid_3"); }
 @Test public void UnionType_Invalid_1_RunTest() { runtimeFailTest("UnionType_Invalid_1"); }
 @Test public void UnionType_Invalid_2_RunTest() { runtimeFailTest("UnionType_Invalid_2"); }
 @Test public void VarDecl_Invalid_1_RunTest() { runtimeFailTest("VarDecl_Invalid_1"); } 
}
