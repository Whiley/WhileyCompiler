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

package wyc.testing.tests;

import org.junit.*;

import wyc.testing.TestHarness;

public class StaticInvalidTests extends TestHarness {
 public StaticInvalidTests() {
	 super("../../tests/invalid","../../tests/invalid","sysout");
 }

 @Test public void Assert_Invalid_1() { contextFailTest("Assert_Invalid_1"); }
 @Test public void Assert_Invalid_2() { contextFailTest("Assert_Invalid_2"); }
 @Test public void Assign_Invalid_1() { contextFailTest("Assign_Invalid_1"); }
 @Test public void Assign_Invalid_2() { contextFailTest("Assign_Invalid_2"); }
 @Test public void Assign_Invalid_3() { contextFailTest("Assign_Invalid_3"); }
 @Test public void Assign_Invalid_4() { contextFailTest("Assign_Invalid_4"); }
 @Test public void Assign_Invalid_5() { contextFailTest("Assign_Invalid_5"); }
 @Test public void Assign_Invalid_6() { contextFailTest("Assign_Invalid_6"); }
 @Test public void Assign_Invalid_7() { contextFailTest("Assign_Invalid_7"); }
 @Test public void Byte_Invalid_1() { contextFailTest("Byte_Invalid_1"); }
 @Test public void Cast_Invalid_1() { contextFailTest("Cast_Invalid_1"); }
 @Test public void Cast_Invalid_2() { contextFailTest("Cast_Invalid_2"); }
 @Test public void Cast_Invalid_3() { contextFailTest("Cast_Invalid_3"); }
 @Test public void Cast_Invalid_4() { contextFailTest("Cast_Invalid_4"); }
 @Test public void Char_Invalid_1() { contextFailTest("Char_Invalid_1"); }
 @Test public void Coercion_Invalid_1() { contextFailTest("Coercion_Invalid_1"); }
 @Test public void Coercion_Invalid_2() { contextFailTest("Coercion_Invalid_2"); }
 @Test public void Constant_Invalid_1() { contextFailTest("Constant_Invalid_1"); }
 @Test public void Constant_Invalid_2() { contextFailTest("Constant_Invalid_2"); }
 @Test public void ConstrainedDictionary_Invalid_1() { contextFailTest("ConstrainedDictionary_Invalid_1"); }
 @Test public void ConstrainedInt_Invalid_1() { contextFailTest("ConstrainedInt_Invalid_1"); }
 @Test public void ConstrainedInt_Invalid_10() { contextFailTest("ConstrainedInt_Invalid_10"); }
 @Test public void ConstrainedInt_Invalid_11() { contextFailTest("ConstrainedInt_Invalid_11"); }
 @Test public void ConstrainedInt_Invalid_12() { contextFailTest("ConstrainedInt_Invalid_12"); }
 @Test public void ConstrainedInt_Invalid_2() { contextFailTest("ConstrainedInt_Invalid_2"); }
 @Test public void ConstrainedInt_Invalid_3() { contextFailTest("ConstrainedInt_Invalid_3"); }
 @Test public void ConstrainedInt_Invalid_4() { contextFailTest("ConstrainedInt_Invalid_4"); }
 @Test public void ConstrainedInt_Invalid_5() { contextFailTest("ConstrainedInt_Invalid_5"); }
 @Test public void ConstrainedInt_Invalid_6() { contextFailTest("ConstrainedInt_Invalid_6"); }
 @Test public void ConstrainedInt_Invalid_7() { contextFailTest("ConstrainedInt_Invalid_7"); }
 @Test public void ConstrainedInt_Invalid_8() { contextFailTest("ConstrainedInt_Invalid_8"); }
 @Test public void ConstrainedInt_Invalid_9() { contextFailTest("ConstrainedInt_Invalid_9"); }
 @Test public void ConstrainedList_Invalid_1() { contextFailTest("ConstrainedList_Invalid_1"); }
 @Test public void ConstrainedList_Invalid_2() { contextFailTest("ConstrainedList_Invalid_2"); }
 @Test public void ConstrainedList_Invalid_3() { contextFailTest("ConstrainedList_Invalid_3"); }
 @Test public void ConstrainedSet_Invalid_1() { contextFailTest("ConstrainedSet_Invalid_1"); }
 @Test public void ConstrainedSet_Invalid_2() { contextFailTest("ConstrainedSet_Invalid_2"); }
 @Test public void ConstrainedSet_Invalid_3() { contextFailTest("ConstrainedSet_Invalid_3"); }
 @Test public void ConstrainedTuple_Invalid_1() { contextFailTest("ConstrainedTuple_Invalid_1"); }
 @Test public void DefiniteAssign_Invalid_1() { contextFailTest("DefiniteAssign_Invalid_1"); }
 @Test public void DefiniteAssign_Invalid_2() { contextFailTest("DefiniteAssign_Invalid_2"); }
 @Test public void DefiniteAssign_Invalid_3() { contextFailTest("DefiniteAssign_Invalid_3"); }
 @Test public void DefiniteAssign_Invalid_4() { contextFailTest("DefiniteAssign_Invalid_4"); }
 @Test public void Ensures_Invalid_1() { contextFailTest("Ensures_Invalid_1"); }
 @Test public void Ensures_Invalid_2() { contextFailTest("Ensures_Invalid_2"); }
 @Test public void Ensures_Invalid_3() { contextFailTest("Ensures_Invalid_3"); }
 @Test public void Export_Invalid_1() { contextFailTest("Export_Invalid_1"); }
 @Test public void For_Invalid_1() { contextFailTest("For_Invalid_1"); }
 @Test public void For_Invalid_5() { contextFailTest("For_Invalid_5"); }
 @Test public void For_Invalid_6() { contextFailTest("For_Invalid_6"); }
 @Test public void For_Invalid_7() { contextFailTest("For_Invalid_7"); }
 @Test public void For_Invalid_8() { contextFailTest("For_Invalid_8"); }
 @Test public void FunctionRef_Invalid_1() { contextFailTest("FunctionRef_Invalid_1"); }
 @Test public void FunctionRef_Invalid_2() { contextFailTest("FunctionRef_Invalid_2"); }
 @Test public void FunctionRef_Invalid_3() { contextFailTest("FunctionRef_Invalid_3"); }
 @Test public void FunctionRef_Invalid_4() { contextFailTest("FunctionRef_Invalid_4"); }
 @Test public void FunctionRef_Invalid_5() { contextFailTest("FunctionRef_Invalid_5"); }
 @Test public void FunctionRef_Invalid_6() { contextFailTest("FunctionRef_Invalid_6"); }
 @Test public void FunctionRef_Invalid_7() { contextFailTest("FunctionRef_Invalid_7"); }
 @Test public void Function_Invalid_1() { contextFailTest("Function_Invalid_1"); }
 @Test public void Function_Invalid_10() { contextFailTest("Function_Invalid_10"); }
 @Test public void Function_Invalid_2() { contextFailTest("Function_Invalid_2"); }
 @Test public void Function_Invalid_3() { contextFailTest("Function_Invalid_3"); }
 @Test public void Function_Invalid_4() { contextFailTest("Function_Invalid_4"); }
 @Test public void Function_Invalid_9() { contextFailTest("Function_Invalid_9"); }
 @Test public void If_Invalid_1() { contextFailTest("If_Invalid_1"); }
 @Test public void If_Invalid_2() { contextFailTest("If_Invalid_2"); }
 @Test public void If_Invalid_3() { contextFailTest("If_Invalid_3"); }
 @Test public void If_Invalid_4() { contextFailTest("If_Invalid_4"); }
 @Test public void Import_Invalid_1() { contextFailTest("Import_Invalid_1"); }
 @Test public void IntDiv_Invalid_1() { contextFailTest("IntDiv_Invalid_1"); }
 @Test public void Intersection_Invalid_1() { contextFailTest("Intersection_Invalid_1"); }
 @Test public void Intersection_Invalid_2() { contextFailTest("Intersection_Invalid_2"); }
 @Test public void Lambda_Invalid_3() { contextFailTest("Lambda_Invalid_3"); }
 @Test public void ListAccess_Invalid_1() { contextFailTest("ListAccess_Invalid_1"); }
 @Test public void ListAccess_Invalid_2() { contextFailTest("ListAccess_Invalid_2"); }
 @Test public void ListAccess_Invalid_3() { contextFailTest("ListAccess_Invalid_3"); }
 @Test public void ListAccess_Invalid_4() { contextFailTest("ListAccess_Invalid_4"); }
 @Test public void ListAccess_Invalid_5() { contextFailTest("ListAccess_Invalid_5"); }
 @Test public void ListAppend_Invalid_1() { contextFailTest("ListAppend_Invalid_1"); }
 @Test public void ListAppend_Invalid_2() { contextFailTest("ListAppend_Invalid_2"); }
 @Test public void ListAppend_Invalid_3() { contextFailTest("ListAppend_Invalid_3"); }
 @Test public void ListAppend_Invalid_4() { contextFailTest("ListAppend_Invalid_4"); }
 @Test public void ListAppend_Invalid_5() { contextFailTest("ListAppend_Invalid_5"); }
 @Test public void ListAssign_Invalid_1() { contextFailTest("ListAssign_Invalid_1"); }
 @Test public void ListAssign_Invalid_2() { contextFailTest("ListAssign_Invalid_2"); }
 @Test public void ListAssign_Invalid_3() { contextFailTest("ListAssign_Invalid_3"); }
 @Test public void ListConversion_Invalid_1() { contextFailTest("ListConversion_Invalid_1"); }
 @Test public void ListElemOf_Invalid_1() { contextFailTest("ListElemOf_Invalid_1"); }
 @Test public void ListElemOf_Invalid_2() { contextFailTest("ListElemOf_Invalid_2"); }
 @Test public void ListEmpty_Invalid_1() { contextFailTest("ListEmpty_Invalid_1"); }
 @Test public void ListEmpty_Invalid_2() { contextFailTest("ListEmpty_Invalid_2"); }
 @Test public void ListEquals_Invalid_1() { contextFailTest("ListEquals_Invalid_1"); }
 @Test public void ListLength_Invalid_1() { contextFailTest("ListLength_Invalid_1"); }
 @Test public void ListLength_Invalid_2() { contextFailTest("ListLength_Invalid_2"); }
 @Test public void ListLength_Invalid_3() { contextFailTest("ListLength_Invalid_3"); }
 @Test public void ListSublist_Invalid_1() { contextFailTest("ListSublist_Invalid_1"); }
 @Test public void ListSublist_Invalid_2() { contextFailTest("ListSublist_Invalid_2"); }
 @Test public void ListSublist_Invalid_3() { contextFailTest("ListSublist_Invalid_3"); }
 @Test public void ListUpdate_Invalid_1() { contextFailTest("ListUpdate_Invalid_1"); }
 @Test public void List_Invalid_1() { contextFailTest("List_Invalid_1"); }
 @Test public void List_Invalid_2() { contextFailTest("List_Invalid_2"); }
 @Test public void List_Invalid_3() { contextFailTest("List_Invalid_3"); }
 @Test public void List_Invalid_4() { contextFailTest("List_Invalid_4"); }
 @Test public void List_Invalid_5() { contextFailTest("List_Invalid_5"); }
 @Test public void List_Invalid_6() { contextFailTest("List_Invalid_6"); }
 @Test public void MethodCall_Invalid_1() { contextFailTest("MethodCall_Invalid_1"); }
 @Test public void MethodCall_Invalid_2() { contextFailTest("MethodCall_Invalid_2"); }
 @Test public void MethodCall_Invalid_3() { contextFailTest("MethodCall_Invalid_3"); }
 @Test public void MethodCall_Invalid_4() { contextFailTest("MethodCall_Invalid_4"); }
 @Test public void MethodCall_Invalid_5() { contextFailTest("MethodCall_Invalid_5"); }
 @Test public void MethodCall_Invalid_6() { contextFailTest("MethodCall_Invalid_6"); }
 @Test public void MethodCall_Invalid_7() { contextFailTest("MethodCall_Invalid_7"); }
 @Test public void MethodCall_Invalid_8() { contextFailTest("MethodCall_Invalid_8"); }
 @Test public void MethodRef_Invalid_1() { contextFailTest("MethodRef_Invalid_1"); }
 @Test public void MethodRef_Invalid_2() { contextFailTest("MethodRef_Invalid_2"); }
 @Test public void MethodRef_Invalid_3() { contextFailTest("MethodRef_Invalid_3"); }
 @Test public void Native_Invalid_1() { contextFailTest("Native_Invalid_1"); }
 @Test public void NegationType_Invalid_1() { contextFailTest("NegationType_Invalid_1"); }
 @Test public void NegationType_Invalid_2() { contextFailTest("NegationType_Invalid_2"); }
 @Test public void NegationType_Invalid_3() { contextFailTest("NegationType_Invalid_3"); }
 @Test public void OpenRecord_Invalid_1() { contextFailTest("OpenRecord_Invalid_1"); }
 @Test public void OpenRecord_Invalid_2() { contextFailTest("OpenRecord_Invalid_2"); }
 @Test public void OpenRecord_Invalid_3() { contextFailTest("OpenRecord_Invalid_3"); }
 @Test public void OpenRecord_Invalid_4() { contextFailTest("OpenRecord_Invalid_4"); }
 @Test public void OpenRecord_Invalid_5() { contextFailTest("OpenRecord_Invalid_5"); }
 @Test public void OpenRecord_Invalid_6() { contextFailTest("OpenRecord_Invalid_6"); }
 @Test public void OpenRecord_Invalid_7() { contextFailTest("OpenRecord_Invalid_7"); }
 @Test public void Parameter_Invalid_1() { contextFailTest("Parameter_Invalid_1"); }
 @Test public void Parameter_Invalid_2() { contextFailTest("Parameter_Invalid_2"); }
 @Test public void ProcessAccess_Invalid_1() { contextFailTest("ProcessAccess_Invalid_1"); }
 @Test public void ProcessAccess_Invalid_2() { contextFailTest("ProcessAccess_Invalid_2"); }
 @Test public void ProcessAccess_Invalid_3() { contextFailTest("ProcessAccess_Invalid_3"); }
 @Test public void Process_Invalid_1() { contextFailTest("Process_Invalid_1"); }
 @Test public void Process_Invalid_2() { contextFailTest("Process_Invalid_2"); }
 @Test public void Process_Invalid_3() { contextFailTest("Process_Invalid_3"); }
 @Test public void Quantifiers_Invalid_1() { contextFailTest("Quantifiers_Invalid_1"); }
 @Test public void Quantifiers_Invalid_2() { contextFailTest("Quantifiers_Invalid_2"); }
 @Test public void Quantifiers_Invalid_3() { contextFailTest("Quantifiers_Invalid_3"); }
 @Test public void Quantifiers_Invalid_4() { contextFailTest("Quantifiers_Invalid_4"); }
 @Test public void Quantifiers_Invalid_5() { contextFailTest("Quantifiers_Invalid_5"); }
 @Test public void Quantifiers_Invalid_6() { contextFailTest("Quantifiers_Invalid_6"); }
 @Test public void Quantifiers_Invalid_7() { contextFailTest("Quantifiers_Invalid_7"); }
 @Test public void Quantifiers_Invalid_8() { contextFailTest("Quantifiers_Invalid_8"); }
 @Test public void RealAdd_Invalid_1() { contextFailTest("RealAdd_Invalid_1"); }
 @Test public void RealConvert_Invalid_1() { contextFailTest("RealConvert_Invalid_1"); }
 @Test public void RealConvert_Invalid_2() { contextFailTest("RealConvert_Invalid_2"); }
 @Test public void RealDiv_Invalid_1() { contextFailTest("RealDiv_Invalid_1"); }
 @Test public void RealDiv_Invalid_2() { contextFailTest("RealDiv_Invalid_2"); }
 @Test public void RealMul_Invalid_1() { contextFailTest("RealMul_Invalid_1"); }
 @Test public void Record_Invalid_1() { contextFailTest("Record_Invalid_1"); }
 @Test public void Record_Invalid_2() { contextFailTest("Record_Invalid_2"); }
 @Test public void RecursiveType_Invalid_1() { contextFailTest("RecursiveType_Invalid_1"); }
 @Test public void RecursiveType_Invalid_10() { contextFailTest("RecursiveType_Invalid_10"); }
 @Test public void RecursiveType_Invalid_2() { contextFailTest("RecursiveType_Invalid_2"); }
 @Test public void RecursiveType_Invalid_3() { contextFailTest("RecursiveType_Invalid_3"); }
 @Test public void RecursiveType_Invalid_4() { contextFailTest("RecursiveType_Invalid_4"); }
 @Test public void RecursiveType_Invalid_5() { contextFailTest("RecursiveType_Invalid_5"); }
 @Test public void RecursiveType_Invalid_7() { contextFailTest("RecursiveType_Invalid_7"); }
 @Test public void RecursiveType_Invalid_8() { contextFailTest("RecursiveType_Invalid_8"); }
 @Test public void RecursiveType_Invalid_9() { contextFailTest("RecursiveType_Invalid_9"); }
 @Test public void Remainder_Invalid_1() { contextFailTest("Remainder_Invalid_1"); }
 @Test public void Remainder_Invalid_2() { contextFailTest("Remainder_Invalid_2"); }
 @Test public void Remainder_Invalid_3() { contextFailTest("Remainder_Invalid_3"); }
 @Test public void Requires_Invalid_1() { contextFailTest("Requires_Invalid_1"); }
 @Test public void Return_Invalid_1() { contextFailTest("Return_Invalid_1"); }
 @Test public void Return_Invalid_10() { contextFailTest("Return_Invalid_10"); }
 @Test public void Return_Invalid_11() { contextFailTest("Return_Invalid_11"); }
 @Test public void Return_Invalid_2() { contextFailTest("Return_Invalid_2"); }
 @Test public void Return_Invalid_3() { contextFailTest("Return_Invalid_3"); }
 @Test public void Return_Invalid_4() { contextFailTest("Return_Invalid_4"); }
 @Test public void Return_Invalid_5() { contextFailTest("Return_Invalid_5"); }
 @Test public void Return_Invalid_6() { contextFailTest("Return_Invalid_6"); }
 @Test public void Return_Invalid_7() { contextFailTest("Return_Invalid_7"); }
 @Test public void Return_Invalid_8() { contextFailTest("Return_Invalid_8"); }
 @Test public void Return_Invalid_9() { contextFailTest("Return_Invalid_9"); }
 @Test public void SetAssign_Invalid_1() { contextFailTest("SetAssign_Invalid_1"); }
 @Test public void SetComprehension_Invalid_1() { contextFailTest("SetComprehension_Invalid_1"); }
 @Test public void SetComprehension_Invalid_2() { contextFailTest("SetComprehension_Invalid_2"); }
 @Test public void SetComprehension_Invalid_3() { contextFailTest("SetComprehension_Invalid_3"); }
 @Test public void SetComprehension_Invalid_4() { contextFailTest("SetComprehension_Invalid_4"); }
 @Test public void SetComprehension_Invalid_5() { contextFailTest("SetComprehension_Invalid_5"); }
 @Test public void SetComprehension_Invalid_6() { contextFailTest("SetComprehension_Invalid_6"); }
 @Test public void SetConversion_Invalid_1() { contextFailTest("SetConversion_Invalid_1"); }
 @Test public void SetDefine_Invalid_1() { contextFailTest("SetDefine_Invalid_1"); }
 @Test public void SetDefine_Invalid_2() { contextFailTest("SetDefine_Invalid_2"); }
 @Test public void SetElemOf_Invalid_1() { contextFailTest("SetElemOf_Invalid_1"); }
 @Test public void SetElemOf_Invalid_2() { contextFailTest("SetElemOf_Invalid_2"); }
 @Test public void SetEmpty_Invalid_1() { contextFailTest("SetEmpty_Invalid_1"); }
 @Test public void SetEmpty_Invalid_2() { contextFailTest("SetEmpty_Invalid_2"); }
 @Test public void SetIntersect_Invalid_1() { contextFailTest("SetIntersect_Invalid_1"); }
 @Test public void SetIntersect_Invalid_2() { contextFailTest("SetIntersect_Invalid_2"); }
 @Test public void SetIntersection_Invalid_1() { contextFailTest("SetIntersection_Invalid_1"); }
 @Test public void SetIntersection_Invalid_2() { contextFailTest("SetIntersection_Invalid_2"); }
 @Test public void SetSubset_Invalid_1() { contextFailTest("SetSubset_Invalid_1"); }
 @Test public void SetSubset_Invalid_10() { contextFailTest("SetSubset_Invalid_10"); }
 @Test public void SetSubset_Invalid_2() { contextFailTest("SetSubset_Invalid_2"); }
 @Test public void SetSubset_Invalid_3() { contextFailTest("SetSubset_Invalid_3"); }
 @Test public void SetSubset_Invalid_4() { contextFailTest("SetSubset_Invalid_4"); }
 @Test public void SetSubset_Invalid_5() { contextFailTest("SetSubset_Invalid_5"); }
 @Test public void SetSubset_Invalid_6() { contextFailTest("SetSubset_Invalid_6"); }
 @Test public void SetSubset_Invalid_7() { contextFailTest("SetSubset_Invalid_7"); }
 @Test public void SetSubset_Invalid_8() { contextFailTest("SetSubset_Invalid_8"); }
 @Test public void SetSubset_Invalid_9() { contextFailTest("SetSubset_Invalid_9"); }
 @Test public void SetUnion_Invalid_1() { contextFailTest("SetUnion_Invalid_1"); }
 @Test public void SetUnion_Invalid_2() { contextFailTest("SetUnion_Invalid_2"); }
 @Test public void SetUnion_Invalid_3() { contextFailTest("SetUnion_Invalid_3"); }
 @Test public void SetUnion_Invalid_4() { contextFailTest("SetUnion_Invalid_4"); }
 @Test public void String_Invalid_3() { contextFailTest("String_Invalid_3"); }
 @Test public void Subtype_Invalid_1() { contextFailTest("Subtype_Invalid_1"); }
 @Test public void Subtype_Invalid_2() { contextFailTest("Subtype_Invalid_2"); }
 @Test public void Subtype_Invalid_3() { contextFailTest("Subtype_Invalid_3"); }
 @Test public void Subtype_Invalid_4() { contextFailTest("Subtype_Invalid_4"); }
 @Test public void Subtype_Invalid_5() { contextFailTest("Subtype_Invalid_5"); }
 @Test public void Subtype_Invalid_6() { contextFailTest("Subtype_Invalid_6"); }
 @Test public void Subtype_Invalid_7() { contextFailTest("Subtype_Invalid_7"); }
 @Test public void Subtype_Invalid_8() { contextFailTest("Subtype_Invalid_8"); }
 @Test public void Subtype_Invalid_9() { contextFailTest("Subtype_Invalid_9"); }
 @Test public void Switch_Invalid_1() { contextFailTest("Switch_Invalid_1"); }
 @Test public void Switch_Invalid_2() { contextFailTest("Switch_Invalid_2"); }
 @Test public void Switch_Invalid_3() { contextFailTest("Switch_Invalid_3"); }
 @Test public void Switch_Invalid_4() { contextFailTest("Switch_Invalid_4"); }
 @Test public void Switch_Invalid_5() { contextFailTest("Switch_Invalid_5"); }
 @Test public void Switch_Invalid_6() { contextFailTest("Switch_Invalid_6"); }
 @Test public void Switch_Invalid_7() { contextFailTest("Switch_Invalid_7"); }
 @Test public void Throws_Invalid_1() { contextFailTest("Throws_Invalid_1"); }
 @Test public void Throws_Invalid_2() { contextFailTest("Throws_Invalid_2"); }
 @Test public void TryCatch_Invalid_1() { contextFailTest("TryCatch_Invalid_1"); }
 @Test public void TryCatch_Invalid_2() { contextFailTest("TryCatch_Invalid_2"); }
 @Test public void TryCatch_Invalid_3() { contextFailTest("TryCatch_Invalid_3"); }
 @Test public void TryCatch_Invalid_4() { contextFailTest("TryCatch_Invalid_4"); }
 @Test public void TryCatch_Invalid_5() { contextFailTest("TryCatch_Invalid_5"); }
 @Test public void TupleAssign_Invalid_1() { contextFailTest("TupleAssign_Invalid_1"); }
 @Test public void TupleAssign_Invalid_2() { contextFailTest("TupleAssign_Invalid_2"); }
 @Test public void TupleAssign_Invalid_3() { contextFailTest("TupleAssign_Invalid_3"); }
 @Test public void TupleDefine_Invalid_1() { contextFailTest("TupleDefine_Invalid_1"); }
 @Test public void TupleDefine_Invalid_2() { contextFailTest("TupleDefine_Invalid_2"); }
 @Test public void Tuple_Invalid_1() { contextFailTest("Tuple_Invalid_1"); }
 @Test public void Tuple_Invalid_3() { contextFailTest("Tuple_Invalid_3"); }
 @Test public void Tuple_Invalid_4() { contextFailTest("Tuple_Invalid_4"); }
 @Test public void Tuple_Invalid_5() { contextFailTest("Tuple_Invalid_5"); }
 @Test public void TypeEquals_Invalid_1() { contextFailTest("TypeEquals_Invalid_1"); }
 @Test public void TypeEquals_Invalid_2() { contextFailTest("TypeEquals_Invalid_2"); }
 @Test public void TypeEquals_Invalid_5() { contextFailTest("TypeEquals_Invalid_5"); }
 @Test public void TypeEquals_Invalid_6() { contextFailTest("TypeEquals_Invalid_6"); }
 @Test public void UnionType_Invalid_1() { contextFailTest("UnionType_Invalid_1"); }
 @Test public void UnionType_Invalid_10() { contextFailTest("UnionType_Invalid_10"); }
 @Test public void UnionType_Invalid_2() { contextFailTest("UnionType_Invalid_2"); }
 @Test public void UnionType_Invalid_3() { contextFailTest("UnionType_Invalid_3"); }
 @Test public void UnionType_Invalid_4() { contextFailTest("UnionType_Invalid_4"); }
 @Test public void UnionType_Invalid_5() { contextFailTest("UnionType_Invalid_5"); }
 @Test public void UnionType_Invalid_6() { contextFailTest("UnionType_Invalid_6"); }
 @Test public void UnionType_Invalid_7() { contextFailTest("UnionType_Invalid_7"); }
 @Test public void UnionType_Invalid_8() { contextFailTest("UnionType_Invalid_8"); }
 @Test public void UnionType_Invalid_9() { contextFailTest("UnionType_Invalid_9"); }
 @Test public void VarDecl_Invalid_1() { contextFailTest("VarDecl_Invalid_1"); }
 @Test public void VarDecl_Invalid_2() { contextFailTest("VarDecl_Invalid_2"); }
 @Test public void Void_Invalid_1() { contextFailTest("Void_Invalid_1"); }
 @Test public void Void_Invalid_2() { contextFailTest("Void_Invalid_2"); }
 @Test public void Void_Invalid_3() { contextFailTest("Void_Invalid_3"); }
 @Test public void While_Invalid_1() { contextFailTest("While_Invalid_1"); }
 @Test public void While_Invalid_10() { contextFailTest("While_Invalid_10"); }
 @Test public void While_Invalid_11() { contextFailTest("While_Invalid_11"); }
 @Test public void While_Invalid_12() { contextFailTest("While_Invalid_12"); }
 @Test public void While_Invalid_2() { contextFailTest("While_Invalid_2"); }
 @Test public void While_Invalid_3() { contextFailTest("While_Invalid_3"); }
 @Test public void While_Invalid_4() { contextFailTest("While_Invalid_4"); }
 @Test public void While_Invalid_5() { contextFailTest("While_Invalid_5"); }
 @Test public void While_Invalid_6() { contextFailTest("While_Invalid_6"); }
 @Test public void While_Invalid_7() { contextFailTest("While_Invalid_7"); }
 @Test public void While_Invalid_8() { contextFailTest("While_Invalid_8"); }
 @Test public void While_Invalid_9() { contextFailTest("While_Invalid_9"); }
 @Test public void XOR_Invalid_1() { contextFailTest("XOR_Invalid_1"); }
}
