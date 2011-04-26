// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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
