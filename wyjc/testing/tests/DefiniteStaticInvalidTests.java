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

public class DefiniteStaticInvalidTests extends TestHarness {
 public DefiniteStaticInvalidTests() {
  super("tests/invalid/definite","tests/invalid/definite","sysout");
 }

 @Test public void ConstrainedInt_Invalid_1_StaticTest() { verificationFailTest("ConstrainedInt_Invalid_1"); }
 @Test public void ConstrainedInt_Invalid_10_StaticTest() { verificationFailTest("ConstrainedInt_Invalid_10"); } 
 @Test public void ConstrainedInt_Invalid_2_StaticTest() { verificationFailTest("ConstrainedInt_Invalid_2"); }
 @Test public void ConstrainedInt_Invalid_3_StaticTest() { verificationFailTest("ConstrainedInt_Invalid_3"); }
 @Test public void ConstrainedInt_Invalid_4_StaticTest() { verificationFailTest("ConstrainedInt_Invalid_4"); }
 @Test public void ConstrainedInt_Invalid_5_StaticTest() { verificationFailTest("ConstrainedInt_Invalid_5"); }
 @Test public void ConstrainedInt_Invalid_6_StaticTest() { verificationFailTest("ConstrainedInt_Invalid_6"); }
 @Test public void ConstrainedInt_Invalid_7_StaticTest() { verificationFailTest("ConstrainedInt_Invalid_7"); }
 // following fails because we must detect ambiguity
 @Ignore("Known Bug") @Test public void ConstrainedInt_Invalid_8_StaticTest() { verificationFailTest("ConstrainedInt_Invalid_8"); }
 @Test public void ConstrainedInt_Invalid_9_StaticTest() { verificationFailTest("ConstrainedInt_Invalid_9"); }
 @Test public void ConstrainedInt_Invalid_11_RunTest() { verificationFailTest("ConstrainedInt_Invalid_11"); }
 @Test public void ConstrainedList_Invalid_1_StaticTest() { verificationFailTest("ConstrainedList_Invalid_1"); }
 @Test public void ConstrainedList_Invalid_2_StaticTest() { verificationFailTest("ConstrainedList_Invalid_2"); } 
 @Test public void ConstrainedSet_Invalid_1_RunTest() { verificationFailTest("ConstrainedSet_Invalid_1"); }
 @Test public void ConstrainedSet_Invalid_2_StaticTest() { verificationFailTest("ConstrainedSet_Invalid_2"); }
 @Test public void ConstrainedSet_Invalid_3_StaticTest() { verificationFailTest("ConstrainedSet_Invalid_3"); }
 @Test public void ConstrainedTuple_Invalid_1_StaticTest() { verificationFailTest("ConstrainedTuple_Invalid_1"); }
 @Test public void Ensures_Invalid_1_StaticTest() { verificationFailTest("Ensures_Invalid_1"); }
 @Test public void IntDiv_Invalid_1_StaticTest() { verificationFailTest("IntDiv_Invalid_1"); }
 @Test public void ListAppend_Invalid_3_verificationTest() { verificationFailTest("ListAppend_Invalid_3"); }
 @Test public void ListAppend_Invalid_4_verificationTest() { verificationFailTest("ListAppend_Invalid_4"); }
 @Test public void ListAppend_Invalid_5_verificationTest() { verificationFailTest("ListAppend_Invalid_5"); } 
 @Test public void ListAccess_Invalid_1_StaticTest() { verificationFailTest("ListAccess_Invalid_1"); }
 @Test public void ListAccess_Invalid_2_StaticTest() { verificationFailTest("ListAccess_Invalid_2"); }
 @Test public void ListAssign_Invalid_1_StaticTest() { verificationFailTest("ListAssign_Invalid_1"); }
 @Test public void ListAssign_Invalid_2_StaticTest() { verificationFailTest("ListAssign_Invalid_2"); }
 @Test public void ListElemOf_Invalid_1_StaticTest() { verificationFailTest("ListElemOf_Invalid_1"); } 
 @Test public void ListLength_Invalid_1_StaticTest() { verificationFailTest("ListLength_Invalid_1"); }
 @Test public void ListLength_Invalid_2_StaticTest() { verificationFailTest("ListLength_Invalid_2"); }
 @Test public void ListSublist_CompileFail_2_StaticTest() { verificationFailTest("ListSublist_CompileFail_2"); }
 @Test public void Process_Invalid_1_StaticTest() { verificationFailTest("Process_Invalid_1"); }
 // Process state updates are completely broken
 @Ignore("Known Bug") @Test public void Process_Invalid_2_StaticTest() { verificationFailTest("Process_Invalid_2"); }
 @Test public void Quantifiers_Invalid_1_StaticTest() { verificationFailTest("Quantifiers_Invalid_1"); }
 @Test public void Quantifiers_Invalid_2_StaticTest() { verificationFailTest("Quantifiers_Invalid_2"); }
 @Test public void Quantifiers_Invalid_3_StaticTest() { verificationFailTest("Quantifiers_Invalid_3"); }
 @Test public void Quantifiers_Invalid_4_StaticTest() { verificationFailTest("Quantifiers_Invalid_4"); }
 @Test public void RealDiv_Invalid_1_StaticTest() { verificationFailTest("RealDiv_Invalid_1"); }
 @Test public void RealMul_Invalid_1_StaticTest() { verificationFailTest("RealMul_Invalid_1"); }
 @Test public void RecursiveType_Invalid_3_StaticTest() { verificationFailTest("RecursiveType_Invalid_3"); }
 @Test public void RecursiveType_Invalid_5_StaticTest() { verificationFailTest("RecursiveType_Invalid_5"); }
 @Test public void RecursiveType_Invalid_6_StaticTest() { verificationFailTest("RecursiveType_Invalid_6"); }
 @Test public void RecursiveType_Invalid_7_StaticTest() { verificationFailTest("RecursiveType_Invalid_7"); }
 @Test public void Requires_Invalid_1_StaticTest() { verificationFailTest("Requires_Invalid_1"); }
 @Test public void SetAssign_Invalid_1_StaticTest() { verificationFailTest("SetAssign_Invalid_1"); }
 @Test public void SetElemOf_Invalid_1_StaticTest() { verificationFailTest("SetElemOf_Invalid_1"); } 
 @Test public void SetIntersection_Invalid_1_StaticTest() { verificationFailTest("SetIntersection_Invalid_1"); }
 @Test public void SetIntersection_Invalid_2_StaticTest() { verificationFailTest("SetIntersection_Invalid_2"); }
 @Test public void SetSubset_Invalid_1_StaticTest() { verificationFailTest("SetSubset_Invalid_1"); }
 @Test public void SetSubset_Invalid_2_StaticTest() { verificationFailTest("SetSubset_Invalid_2"); }
 @Test public void SetSubset_Invalid_3_StaticTest() { verificationFailTest("SetSubset_Invalid_3"); }
 @Test public void SetSubset_Invalid_4_StaticTest() { verificationFailTest("SetSubset_Invalid_4"); }
 @Test public void SetSubset_Invalid_5_StaticTest() { verificationFailTest("SetSubset_Invalid_5"); }
 @Test public void SetSubset_Invalid_6_StaticTest() { verificationFailTest("SetSubset_Invalid_6"); }
 @Test public void SetUnion_Invalid_1_StaticTest() { verificationFailTest("SetUnion_Invalid_1"); }
 @Test public void SetUnion_Invalid_2_StaticTest() { verificationFailTest("SetUnion_Invalid_2"); }
 @Test public void Subtype_CompileFail_1_StaticTest() { verificationFailTest("Subtype_CompileFail_1"); }
 @Test public void Subtype_CompileFail_2_StaticTest() { verificationFailTest("Subtype_CompileFail_2"); }
 @Test public void Subtype_CompileFail_3_StaticTest() { verificationFailTest("Subtype_CompileFail_3"); } 
 @Test public void Subtype_CompileFail_5_StaticTest() { verificationFailTest("Subtype_CompileFail_5"); } 
 @Test public void Subtype_CompileFail_6_StaticTest() { verificationFailTest("Subtype_CompileFail_6"); }
 @Test public void Subtype_CompileFail_7_StaticTest() { verificationFailTest("Subtype_CompileFail_7"); }
 @Test public void Subtype_CompileFail_8_StaticTest() { verificationFailTest("Subtype_CompileFail_8"); }
 @Test public void Subtype_CompileFail_9_StaticTest() { verificationFailTest("Subtype_CompileFail_9"); } 
 @Test public void TupleAssign_Invalid_1_StaticTest() { verificationFailTest("TupleAssign_Invalid_1"); }
 @Test public void TupleAssign_Invalid_2_StaticTest() { verificationFailTest("TupleAssign_Invalid_2"); }
 @Test public void TupleAssign_Invalid_3_StaticTest() { verificationFailTest("TupleAssign_Invalid_3"); }
 @Test public void UnionType_Invalid_1_StaticTest() { verificationFailTest("UnionType_Invalid_1"); }
 @Test public void VarDecl_Invalid_1_StaticTest() { verificationFailTest("VarDecl_Invalid_1"); } 
}
