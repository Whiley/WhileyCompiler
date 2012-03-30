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

package wyjc.testing.tests;

import org.junit.*;

import wyjc.testing.TestHarness;

public class StaticInvalidTests extends TestHarness {
 public StaticInvalidTests() {
  super("tests/base/invalid","tests/base/invalid","sysout");
 }

 @Test public void Assert_CompileFail_1_StaticTest() { contextFailTest("Assert_CompileFail_1"); }
 @Test public void Assign_CompileFail_1_StaticTest() { contextFailTest("Assign_CompileFail_1"); }
 @Test public void Assign_CompileFail_2_StaticTest() { contextFailTest("Assign_CompileFail_2"); }
 @Test public void Assign_CompileFail_3_StaticTest() { contextFailTest("Assign_CompileFail_3"); }
 @Test public void Assign_CompileFail_4_StaticTest() { contextFailTest("Assign_CompileFail_4"); }
 @Test public void Assign_CompileFail_5_StaticTest() { contextFailTest("Assign_CompileFail_5"); }
 @Test public void Assign_CompileFail_6_StaticTest() { contextFailTest("Assign_CompileFail_6"); }
 @Test public void Assign_CompileFail_7_StaticTest() { contextFailTest("Assign_CompileFail_7"); }
 @Test public void Byte_CompileFail_1_StaticTest() { contextFailTest("Byte_Invalid_1"); }
 @Test public void Cast_CompileFail_1_StaticTest() { contextFailTest("Cast_Invalid_1"); }
 @Test public void Cast_CompileFail_2_StaticTest() { contextFailTest("Cast_Invalid_2"); }
 @Test public void Cast_CompileFail_3_StaticTest() { contextFailTest("Cast_Invalid_3"); }
 @Test public void Cast_CompileFail_4_StaticTest() { contextFailTest("Cast_Invalid_4"); } 
 @Test public void Coercion_CompileFail_1_StaticTest() { contextFailTest("Coercion_Invalid_1"); }
 @Test public void Coercion_CompileFail_2_StaticTest() { contextFailTest("Coercion_Invalid_2"); }
 @Test public void Constant_Invaid_1_StaticTest() { contextFailTest("Constant_Invalid_1"); }
 @Test public void Constant_Invaid_2_StaticTest() { contextFailTest("Constant_Invalid_2"); }
 @Test public void Char_CompileFail_1_StaticTest() { contextFailTest("Char_Invalid_1"); }
 @Test public void DefiniteAssign_CompileFail_1_RuntimeFailTest() { contextFailTest("DefiniteAssign_CompileFail_1"); }
 @Test public void DefiniteAssign_CompileFail_2_RuntimeFailTest() { contextFailTest("DefiniteAssign_CompileFail_2"); }
 @Test public void DefiniteAssign_CompileFail_3_RuntimeFailTest() { contextFailTest("DefiniteAssign_CompileFail_3"); }
 @Test public void DefiniteAssign_CompileFail_4_StaticTest() { contextFailTest("DefiniteAssign_CompileFail_4"); }
 @Ignore("Known Issue") @Test public void Export_CompileFail_1_RuntimeFailTest() { contextFailTest("Export_Invalid_1"); }
 @Test public void For_CompileFail_1_StaticTest() { contextFailTest("For_CompileFail_1"); }
 @Test public void For_CompileFail_2_StaticTest() { contextFailTest("For_Invalid_2"); }
 @Test public void For_CompileFail_3_StaticTest() { contextFailTest("For_Invalid_3"); }
 @Test public void For_CompileFail_4_StaticTest() { contextFailTest("For_Invalid_4"); }
 @Test public void Function_CompileFail_1_StaticTest() { contextFailTest("Function_CompileFail_1"); }
 @Ignore("Known Issue")
 @Test public void Function_CompileFail_2_StaticTest() { contextFailTest("Function_CompileFail_2"); }
 @Test public void Function_CompileFail_3_StaticTest() { contextFailTest("Function_CompileFail_3"); }
 @Test public void Function_CompileFail_4_StaticTest() { contextFailTest("Function_CompileFail_4"); }
 @Test public void Function_Invalid_5_StaticTest() { contextFailTest("Function_Invalid_5"); }
 @Ignore("Known Issue") @Test public void Function_Invalid_6_StaticTest() { contextFailTest("Function_Invalid_6"); }
 @Test public void Function_Invalid_7_StaticTest() { contextFailTest("Function_Invalid_7"); }
 @Test public void Function_Invalid_8_StaticTest() { contextFailTest("Function_Invalid_8"); }
 @Test public void FunctionRef_CompileFail_1_StaticTest() { contextFailTest("FunctionRef_Invalid_1"); }
 @Test public void FunctionRef_CompileFail_2_StaticTest() { contextFailTest("FunctionRef_Invalid_2"); }
 @Test public void FunctionRef_CompileFail_3_StaticTest() { contextFailTest("FunctionRef_Invalid_3"); }
 @Test public void FunctionRef_CompileFail_4_StaticTest() { contextFailTest("FunctionRef_Invalid_4"); }
 @Test public void FunctionRef_CompileFail_5_StaticTest() { contextFailTest("FunctionRef_Invalid_5"); }
 @Test public void FunctionRef_CompileFail_6_StaticTest() { contextFailTest("FunctionRef_Invalid_6"); }
 @Test public void FunctionRef_CompileFail_7_StaticTest() { contextFailTest("FunctionRef_Invalid_7"); }
 @Test public void Import_CompileFail_1_RuntimeTest() { contextFailTest("Import_Invalid_1"); }
 @Test public void Intersection_CompileFail_1_RuntimeTest() { contextFailTest("Intersection_Invalid_1"); }
 @Test public void Intersection_CompileFail_2_RuntimeTest() { contextFailTest("Intersection_Invalid_2"); }
 @Test public void If_CompileFail_1_StaticTest() { contextFailTest("If_CompileFail_1"); }
 @Test public void If_CompileFail_2_StaticTest() { contextFailTest("If_CompileFail_2"); }
 @Test public void If_CompileFail_3_StaticTest() { contextFailTest("If_CompileFail_3"); }
 @Test public void If_CompileFail_4_StaticTest() { contextFailTest("If_CompileFail_4"); }  
 @Test public void ListAccess_CompileFail_1_StaticTest() { contextFailTest("ListAccess_CompileFail_1"); }
 @Test public void ListAccess_CompileFail_3_StaticTest() { contextFailTest("ListAccess_CompileFail_3"); }
 @Test public void ListAppend_Invalid_1_StaticTest() { contextFailTest("ListAppend_Invalid_1"); }
 @Test public void ListAppend_Invalid_2_StaticTest() { contextFailTest("ListAppend_Invalid_2"); }
 @Test public void ListAssign_CompileFail_1_StaticTest() { contextFailTest("ListAssign_CompileFail_1"); }
 @Test public void ListConversion_CompileFail_1_StaticTest() { contextFailTest("ListConversion_CompileFail_1"); }
 @Test public void ListDefine_CompileFail_1_StaticTest() { contextFailTest("ListDefine_CompileFail_1"); }
 @Test public void ListElemOf_CompileFail_1_StaticTest() { contextFailTest("ListElemOf_CompileFail_1"); }
 @Test public void ListEmpty_CompileFail_1_StaticTest() { contextFailTest("ListEmpty_CompileFail_1"); }
 @Test public void ListSublist_CompileFail_1_StaticTest() { contextFailTest("ListSublist_CompileFail_1"); }
 @Test public void ListSublist_CompileFail_3_StaticTest() { contextFailTest("ListSublist_CompileFail_3"); }
 @Test public void List_CompileFail_1_StaticTest() { contextFailTest("List_CompileFail_1"); }
 @Test public void List_CompileFail_2_StaticTest() { contextFailTest("List_CompileFail_2"); }
 @Test public void List_CompileFail_3_StaticTest() { contextFailTest("List_CompileFail_3"); }
 @Test public void List_CompileFail_4_StaticTest() { contextFailTest("List_CompileFail_4"); }
 @Test public void List_CompileFail_5_StaticTest() { contextFailTest("List_CompileFail_5"); }
 @Test public void List_CompileFail_6_StaticTest() { contextFailTest("List_CompileFail_6"); }
 @Test public void MethodCall_CompileFail_1_StaticTest() { contextFailTest("MethodCall_CompileFail_1"); }
 @Test public void MethodCall_CompileFail_2_StaticTest() { contextFailTest("MethodCall_CompileFail_2"); }
 @Test public void MethodCall_CompileFail_3_StaticTest() { contextFailTest("MethodCall_CompileFail_3"); }
 @Test public void MethodCall_CompileFail_4_StaticTest() { contextFailTest("MethodCall_CompileFail_4"); }
 @Test public void MethodCall_CompileFail_5_StaticTest() { contextFailTest("MethodCall_CompileFail_5"); }
 @Test public void MethodCall_CompileFail_6_StaticTest() { contextFailTest("MethodCall_CompileFail_6"); }
 @Test public void MethodCall_CompileFail_7_StaticTest() { contextFailTest("MethodCall_CompileFail_7"); }
 @Test public void MethodCall_CompileFail_8_StaticTest() { contextFailTest("MethodCall_CompileFail_8"); }
 @Test public void MethodRef_Invalid_1_StaticTest() { contextFailTest("MethodRef_Invalid_1"); }
 @Test public void MethodRef_Invalid_2_StaticTest() { contextFailTest("MethodRef_Invalid_2"); }
 @Test public void MethodRef_Invalid_3_StaticTest() { contextFailTest("MethodRef_Invalid_3"); }
 @Ignore("Known Issue") @Test public void Native_CompileFail_1_RuntimeFailTest() { contextFailTest("Native_Invalid_1"); }
 @Test public void NegationType_Invalid_1_RuntimeTest() { contextFailTest("NegationType_Invalid_1"); }
 @Test public void NegationType_Invalid_2_RuntimeTest() { contextFailTest("NegationType_Invalid_2"); }
 @Test public void NegationType_Invalid_3_RuntimeTest() { contextFailTest("NegationType_Invalid_3"); }
 @Test public void OpenRecord_CompileFail_1_StaticTest() { contextFailTest("OpenRecord_Invalid_1"); }
 @Test public void OpenRecord_CompileFail_2_StaticTest() { contextFailTest("OpenRecord_Invalid_2"); }
 @Test public void OpenRecord_CompileFail_3_StaticTest() { contextFailTest("OpenRecord_Invalid_3"); }
 @Test public void OpenRecord_CompileFail_4_StaticTest() { contextFailTest("OpenRecord_Invalid_4"); }
 @Test public void OpenRecord_CompileFail_5_StaticTest() { contextFailTest("OpenRecord_Invalid_5"); }
 @Test public void OpenRecord_CompileFail_6_StaticTest() { contextFailTest("OpenRecord_Invalid_6"); }
 @Test public void OpenRecord_CompileFail_7_StaticTest() { contextFailTest("OpenRecord_Invalid_7"); }
 @Test public void Process_Invalid_1_StaticTest() { contextFailTest("Process_Invalid_1"); }
 @Ignore("Known Issue") @Test public void Process_Invalid_2_StaticTest() { contextFailTest("Process_Invalid_2"); }
 @Test public void Process_Invalid_3_StaticTest() { contextFailTest("Process_Invalid_3"); }
 @Test public void ProcessAccess_Invalid_1_StaticTest() { contextFailTest("ProcessAccess_CompileFail_1"); }
 @Test public void ProcessAccess_Invalid_2_StaticTest() { contextFailTest("ProcessAccess_CompileFail_2"); }
 @Test public void ProcessAccess_Invalid_3_StaticTest() { contextFailTest("ProcessAccess_Invalid_3"); }
 @Test public void Parameter_Invalid_1_StaticTest() { contextFailTest("Parameter_CompileFail_1"); }
 @Test public void Parameter_Invalid_2_StaticTest() { contextFailTest("Parameter_CompileFail_2"); }
 @Test public void RealAdd_CompileFail_1_StaticTest() { contextFailTest("RealAdd_CompileFail_1"); }
 @Test public void RealDiv_CompileFail_1_StaticTest() { contextFailTest("RealDiv_CompileFail_1"); }
 @Test public void Record_CompileFail_1_StaticTest() { contextFailTest("Record_Invalid_1"); }
 @Test public void Record_CompileFail_2_StaticTest() { contextFailTest("Record_Invalid_2"); }
 @Test public void RecursiveType_Invalid_4_StaticTest() { contextFailTest("RecursiveType_Invalid_4"); }
 @Test public void RecursiveType_Invalid_5_StaticTest() { contextFailTest("RecursiveType_Invalid_5"); }
 @Test public void RecursiveType_Invalid_6_StaticTest() { contextFailTest("RecursiveType_Invalid_6"); }
 @Test public void Remainder_Invalid_1_StaticTest() { contextFailTest("Remainder_Invalid_1"); }
 @Test public void Remainder_Invalid_2_StaticTest() { contextFailTest("Remainder_Invalid_2"); }
 @Test public void Remainder_Invalid_3_StaticTest() { contextFailTest("Remainder_Invalid_3"); }
 @Test public void Return_CompileFail_1_StaticTest() { contextFailTest("Return_CompileFail_1"); }
 @Test public void Return_CompileFail_10_StaticTest() { contextFailTest("Return_CompileFail_10"); }
 @Test public void Return_CompileFail_11_StaticTest() { contextFailTest("Return_CompileFail_11"); }
 @Test public void Return_CompileFail_2_StaticTest() { contextFailTest("Return_CompileFail_2"); }
 @Test public void Return_CompileFail_3_StaticTest() { contextFailTest("Return_CompileFail_3"); }
 @Test public void Return_CompileFail_4_StaticTest() { contextFailTest("Return_CompileFail_4"); }
 @Test public void Return_CompileFail_5_StaticTest() { contextFailTest("Return_CompileFail_5"); }
 @Test public void Return_CompileFail_6_StaticTest() { contextFailTest("Return_CompileFail_6"); }
 @Test public void Return_CompileFail_7_StaticTest() { contextFailTest("Return_CompileFail_7"); }
 @Test public void Return_CompileFail_8_StaticTest() { contextFailTest("Return_CompileFail_8"); }
 @Test public void Return_CompileFail_9_StaticTest() { contextFailTest("Return_CompileFail_9"); }
 @Test public void SetComprehension_CompileFail_1_StaticTest() { contextFailTest("SetComprehension_CompileFail_1"); }
 @Test public void SetComprehension_CompileFail_2_StaticTest() { contextFailTest("SetComprehension_CompileFail_2"); }
 @Test public void SetComprehension_CompileFail_3_StaticTest() { contextFailTest("SetComprehension_CompileFail_3"); }
 @Test public void SetComprehension_CompileFail_4_StaticTest() { contextFailTest("SetComprehension_CompileFail_4"); }
 @Test public void SetComprehension_CompileFail_5_StaticTest() { contextFailTest("SetComprehension_CompileFail_5"); }
 @Test public void SetConversion_CompileFail_1_StaticTest() { contextFailTest("SetConversion_CompileFail_1"); }
 @Test public void SetDefine_CompileFail_1_StaticTest() { contextFailTest("SetDefine_CompileFail_1"); }
 @Test public void SetDefine_CompileFail_2_StaticTest() { contextFailTest("SetDefine_CompileFail_2"); }
 @Test public void SetElemOf_CompileFail_1_StaticTest() { contextFailTest("SetElemOf_CompileFail_1"); }
 @Test public void SetEmpty_CompileFail_1_StaticTest() { contextFailTest("SetEmpty_CompileFail_1"); }
 @Test public void SetIntersect_CompileFail_1_StaticTest() { contextFailTest("SetIntersect_CompileFail_1"); }
 @Test public void SetIntersect_CompileFail_2_StaticTest() { contextFailTest("SetIntersect_CompileFail_2"); }
 @Test public void SetUnion_CompileFail_1_StaticTest() { contextFailTest("SetUnion_CompileFail_1"); }
 @Test public void SetUnion_CompileFail_2_StaticTest() { contextFailTest("SetUnion_CompileFail_2"); }
 @Test public void String_CompileFail_1_StaticTest() { contextFailTest("String_Invalid_1"); }
 @Test public void String_CompileFail_2_StaticTest() { contextFailTest("String_Invalid_2"); }
 @Test public void Switch_CompileFail_1_StaticTest() { contextFailTest("Switch_CompileFail_1"); } 
 @Test public void Switch_CompileFail_2_StaticTest() { contextFailTest("Switch_CompileFail_2"); } 
 @Test public void Switch_CompileFail_3_StaticTest() { contextFailTest("Switch_CompileFail_3"); }
 @Test public void Switch_CompileFail_4_StaticTest() { contextFailTest("Switch_CompileFail_4"); }
 @Test public void Switch_CompileFail_5_StaticTest() { contextFailTest("Switch_CompileFail_5"); }
 @Test public void Switch_CompileFail_6_StaticTest() { contextFailTest("Switch_Invalid_6"); }
 @Test public void Switch_CompileFail_7_StaticTest() { contextFailTest("Switch_Invalid_7"); }
 @Test public void Throws_Invalid_1_StaticTest() { contextFailTest("Throws_Invalid_1"); }
 @Test public void Throws_Invalid_2_StaticTest() { contextFailTest("Throws_Invalid_2"); }
 @Test public void TryCatch_Invalid_1_StaticTest() { contextFailTest("TryCatch_Invalid_1"); }
 @Test public void TryCatch_Invalid_2_StaticTest() { contextFailTest("TryCatch_Invalid_2"); }
 @Test public void TryCatch_Invalid_3_StaticTest() { contextFailTest("TryCatch_Invalid_3"); }
 @Test public void TryCatch_Invalid_4_StaticTest() { contextFailTest("TryCatch_Invalid_4"); }
 @Test public void TryCatch_Invalid_5_StaticTest() { contextFailTest("TryCatch_Invalid_5"); }
 @Test public void TupleDefine_CompileFail_1_StaticTest() { contextFailTest("TupleDefine_CompileFail_1"); }
 @Test public void Tuple_Invalid_2_StaticTest() { contextFailTest("Tuple_Invalid_2"); }
 @Test public void TypeEquals_Invalid_1_StaticTest() { contextFailTest("TypeEquals_Invalid_1"); }
 @Test public void TypeEquals_Invalid_2_StaticTest() { contextFailTest("TypeEquals_Invalid_2"); }
 @Test public void TypeEquals_Invalid_3_StaticTest() { contextFailTest("TypeEquals_Invalid_3"); }
 @Test public void TypeEquals_Invalid_4_StaticTest() { contextFailTest("TypeEquals_Invalid_4"); }
 @Test public void UnionType_CompileFail_1_StaticTest() { contextFailTest("UnionType_CompileFail_1"); }
 @Test public void UnionType_CompileFail_2_StaticTest() { contextFailTest("UnionType_CompileFail_2"); }
 @Test public void UnionType_CompileFail_3_StaticTest() { contextFailTest("UnionType_CompileFail_3"); }
 @Test public void UnionType_CompileFail_4_StaticTest() { contextFailTest("UnionType_CompileFail_4"); }
 @Test public void UnionType_CompileFail_5_StaticTest() { contextFailTest("UnionType_CompileFail_5"); }
 @Test public void UnionType_CompileFail_6_StaticTest() { contextFailTest("UnionType_CompileFail_6"); }
 @Test public void UnionType_CompileFail_7_StaticTest() { contextFailTest("UnionType_CompileFail_7"); }
 @Test public void VarDecl_CompileFail_4_StaticTest() { contextFailTest("VarDecl_CompileFail_4"); }
 @Test public void Void_CompileFail_1_StaticTest() { contextFailTest("Void_CompileFail_1"); }
 @Test public void Void_CompileFail_2_StaticTest() { contextFailTest("Void_CompileFail_2"); }
 @Test public void Void_CompileFail_3_StaticTest() { contextFailTest("Void_CompileFail_3"); }
 @Test public void While_CompileFail_1_RuntimeFailTest() { contextFailTest("While_CompileFail_1"); }
 @Test public void While_CompileFail_2_RuntimeFailTest() { contextFailTest("While_CompileFail_2"); }
 @Test public void While_CompileFail_3_RuntimeFailTest() { contextFailTest("While_CompileFail_3"); }
 @Test public void While_CompileFail_4_RuntimeFailTest() { contextFailTest("While_CompileFail_4"); } 
 @Test public void While_CompileFail_7_RuntimeFailTest() { contextFailTest("While_CompileFail_7"); }
 @Test public void XOR_CompileFail_1_StaticTest() { contextFailTest("XOR_CompileFail_1"); }
}
