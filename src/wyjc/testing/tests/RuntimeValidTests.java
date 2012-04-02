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

public class RuntimeValidTests extends TestHarness {
 public RuntimeValidTests() {
  super("tests/base/valid","tests/base/valid","sysout");
 }

 @Test public void Access_Valid_1_RuntimeTest() { runTest("Access_Valid_1"); }
 @Test public void Access_Valid_2_RuntimeTest() { runTest("Access_Valid_2"); }
 @Test public void BoolAssign_Valid_1_RuntimeTest() { runTest("BoolAssign_Valid_1"); }
 @Test public void BoolAssign_Valid_2_RuntimeTest() { runTest("BoolAssign_Valid_2"); }
 @Test public void BoolAssign_Valid_3_RuntimeTest() { runTest("BoolAssign_Valid_3"); }
 @Test public void BoolAssign_Valid_4_RuntimeTest() { runTest("BoolAssign_Valid_4"); }
 @Test public void BoolFun_Valid_1_RuntimeTest() { runTest("BoolFun_Valid_1"); }
 @Test public void BoolIfElse_Valid_1_RuntimeTest() { runTest("BoolIfElse_Valid_1"); }
 @Test public void BoolIfElse_Valid_2_RuntimeTest() { runTest("BoolIfElse_Valid_2"); }
 @Test public void BoolList_Valid_1_RuntimeTest() { runTest("BoolList_Valid_1"); }
 @Test public void BoolRecord_Valid_1_RuntimeTest() { runTest("BoolRecord_Valid_1"); }
 @Test public void BoolRecord_Valid_2_RuntimeTest() { runTest("BoolRecord_Valid_2"); } 
 @Test public void BoolReturn_Valid_1_RuntimeTest() { runTest("BoolReturn_Valid_1"); }
 @Test public void Byte_Valid_1_RuntimeTest() { runTest("Byte_Valid_1"); }
 @Test public void Byte_Valid_2_RuntimeTest() { runTest("Byte_Valid_2"); }
 @Test public void Byte_Valid_3_RuntimeTest() { runTest("Byte_Valid_3"); }
 @Test public void Byte_Valid_4_RuntimeTest() { runTest("Byte_Valid_4"); }
 @Test public void Byte_Valid_5_RuntimeTest() { runTest("Byte_Valid_5"); }
 @Test public void Byte_Valid_6_RuntimeTest() { runTest("Byte_Valid_6"); }
 @Test public void Byte_Valid_7_RuntimeTest() { runTest("Byte_Valid_7"); }
 @Test public void Byte_Valid_8_RuntimeTest() { runTest("Byte_Valid_8"); }
 @Test public void Byte_Valid_9_RuntimeTest() { runTest("Byte_Valid_9"); }
 @Test public void Cast_Valid_1_RuntimeTest() { runTest("Cast_Valid_1"); }
 @Test public void Cast_Valid_2_RuntimeTest() { runTest("Cast_Valid_2"); }
 @Test public void Cast_Valid_3_RuntimeTest() { runTest("Cast_Valid_3"); }
 @Test public void Cast_Valid_4_RuntimeTest() { runTest("Cast_Valid_4"); }
 @Test public void Cast_Valid_5_RuntimeTest() { runTest("Cast_Valid_5"); }
 @Test public void Char_Valid_1_RuntimeTest() { runTest("Char_Valid_1"); }
 @Test public void Char_Valid_2_RuntimeTest() { runTest("Char_Valid_2"); }
 @Test public void Char_Valid_3_RuntimeTest() { runTest("Char_Valid_3"); }
 @Test public void Char_Valid_4_RuntimeTest() { runTest("Char_Valid_4"); }
 @Test public void Char_Valid_5_RuntimeTest() { runTest("Char_Valid_5"); }
 @Test public void Char_Valid_6_RuntimeTest() { runTest("Char_Valid_6"); }
 @Test public void Char_Valid_7_RuntimeTest() { runTest("Char_Valid_7"); }
 @Ignore("Known Issue") @Test public void Contractive_Valid_1_RuntimeTest() { runTest("Contractive_Valid_1"); }
 @Test public void Contractive_Valid_2_RuntimeTest() { runTest("Contractive_Valid_2"); }
 @Test public void Coercion_Valid_1_RuntimeTest() { runTest("Coercion_Valid_1"); }
 @Test public void Coercion_Valid_2_RuntimeTest() { runTest("Coercion_Valid_2"); }
 @Test public void Coercion_Valid_3_RuntimeTest() { runTest("Coercion_Valid_3"); }
 @Test public void Coercion_Valid_4_RuntimeTest() { runTest("Coercion_Valid_4"); }
 @Ignore("Known Issue") @Test public void Coercion_Valid_5_RuntimeTest() { runTest("Coercion_Valid_5"); }
 @Test public void Coercion_Valid_6_RuntimeTest() { runTest("Coercion_Valid_6"); }
 @Test public void Coercion_Valid_7_RuntimeTest() { runTest("Coercion_Valid_7"); }
 @Test public void Coercion_Valid_8_RuntimeTest() { runTest("Coercion_Valid_8"); }
 @Test public void Complex_Valid_1_RuntimeTest() { runTest("Complex_Valid_1"); }
 @Test public void Complex_Valid_2_RuntimeTest() { runTest("Complex_Valid_2"); }
 @Ignore("Known Issue") @Test public void Constant_Valid_1_RuntimeTest() { runTest("Constant_Valid_1"); }
 @Test public void Constant_Valid_2_RuntimeTest() { runTest("Constant_Valid_2"); }
 @Test public void ConstrainedInt_Valid_1_RuntimeTest() { runTest("ConstrainedInt_Valid_1"); }
 @Test public void ConstrainedInt_Valid_2_RuntimeTest() { runTest("ConstrainedInt_Valid_2"); }
 @Test public void ConstrainedInt_Valid_3_RuntimeTest() { runTest("ConstrainedInt_Valid_3"); }
 @Test public void ConstrainedInt_Valid_4_RuntimeTest() { runTest("ConstrainedInt_Valid_4"); }
 @Test public void ConstrainedInt_Valid_5_RuntimeTest() { runTest("ConstrainedInt_Valid_5"); }
 @Test public void ConstrainedInt_Valid_6_RuntimeTest() { runTest("ConstrainedInt_Valid_6"); }
 @Test public void ConstrainedInt_Valid_7_RuntimeTest() { runTest("ConstrainedInt_Valid_7"); }
 @Test public void ConstrainedInt_Valid_8_RuntimeTest() { runTest("ConstrainedInt_Valid_8"); }
 @Test public void ConstrainedInt_Valid_9_RuntimeTest() { runTest("ConstrainedInt_Valid_9"); }
 @Test public void ConstrainedInt_Valid_10_RuntimeTest() { runTest("ConstrainedInt_Valid_10"); }
 @Test public void ConstrainedInt_Valid_11_RuntimeTest() { runTest("ConstrainedInt_Valid_11"); }
 @Test public void ConstrainedList_Valid_1_RuntimeTest() { runTest("ConstrainedList_Valid_1"); }
 @Test public void ConstrainedList_Valid_2_RuntimeTest() { runTest("ConstrainedList_Valid_2"); }
 @Test public void ConstrainedList_Valid_3_RuntimeTest() { runTest("ConstrainedList_Valid_3"); }
 @Test public void ConstrainedList_Valid_4_RuntimeTest() { runTest("ConstrainedList_Valid_4"); }
 @Test public void ConstrainedList_Valid_5_RuntimeTest() { runTest("ConstrainedList_Valid_5"); }
 @Test public void ConstrainedRecord_Valid_1_RuntimeTest() { runTest("ConstrainedRecord_Valid_1"); }
 @Test public void ConstrainedRecord_Valid_2_RuntimeTest() { runTest("ConstrainedRecord_Valid_2"); }
 @Test public void ConstrainedRecord_Valid_3_RuntimeTest() { runTest("ConstrainedRecord_Valid_3"); }
 @Test public void ConstrainedRecord_Valid_4_RuntimeTest() { runTest("ConstrainedRecord_Valid_4"); }
 @Test public void ConstrainedRecord_Valid_5_RuntimeTest() { runTest("ConstrainedRecord_Valid_5"); }
 @Test public void ConstrainedSet_Valid_1_RuntimeTest() { runTest("ConstrainedSet_Valid_1"); }
 @Test public void ConstrainedSet_Valid_2_RuntimeTest() { runTest("ConstrainedSet_Valid_2"); }
 @Test public void ConstrainedSet_Valid_3_RuntimeTest() { runTest("ConstrainedSet_Valid_3"); }
 @Test public void ConstrainedSet_Valid_4_RuntimeTest() { runTest("ConstrainedSet_Valid_4"); }
 @Test public void DecimalAssignment_Valid_1_RuntimeTest() { runTest("DecimalAssignment_Valid_1"); }
 @Test public void Define_Valid_1_RuntimeTest() { runTest("Define_Valid_1"); }
 @Test public void Define_Valid_2_RuntimeTest() { runTest("Define_Valid_2"); }
 @Test public void Define_Valid_3_RuntimeTest() { runTest("Define_Valid_3"); }
 @Test public void Define_Valid_4_RuntimeTest() { runTest("Define_Valid_4"); }
 @Test public void Dictionary_Valid_1_RuntimeTest() { runTest("Dictionary_Valid_1"); }
 @Test public void Dictionary_Valid_2_RuntimeTest() { runTest("Dictionary_Valid_2"); }
 @Test public void Dictionary_Valid_3_RuntimeTest() { runTest("Dictionary_Valid_3"); }
 @Test public void Dictionary_Valid_4_RuntimeTest() { runTest("Dictionary_Valid_4"); }
 @Test public void Dictionary_Valid_5_RuntimeTest() { runTest("Dictionary_Valid_5"); }
 @Test public void Dictionary_Valid_6_RuntimeTest() { runTest("Dictionary_Valid_6"); }
 @Test public void Dictionary_Valid_7_RuntimeTest() { runTest("Dictionary_Valid_7"); }
 @Test public void Dictionary_Valid_8_RuntimeTest() { runTest("Dictionary_Valid_8"); }
 @Test public void Dictionary_Valid_9_RuntimeTest() { runTest("Dictionary_Valid_9"); }
 @Test public void Dictionary_Valid_10_RuntimeTest() { runTest("Dictionary_Valid_10"); }
 @Test public void Dictionary_Valid_11_RuntimeTest() { runTest("Dictionary_Valid_11"); }
 @Test public void Dictionary_Valid_12_RuntimeTest() { runTest("Dictionary_Valid_12"); }
 @Test public void Dictionary_Valid_13_RuntimeTest() { runTest("Dictionary_Valid_13"); }
 @Test public void Dictionary_Valid_14_RuntimeTest() { runTest("Dictionary_Valid_14"); }
 @Test public void Dictionary_Valid_15_RuntimeTest() { runTest("Dictionary_Valid_15"); }
 @Test public void Dictionary_Valid_16_RuntimeTest() { runTest("Dictionary_Valid_16"); }
 @Test public void DoWhile_Valid_1_RuntimeTest() { runTest("DoWhile_Valid_1"); }
 @Test public void DoWhile_Valid_2_RuntimeTest() { runTest("DoWhile_Valid_2"); }
 @Ignore("Future Work") @Test public void DoWhile_Valid_3_RuntimeTest() { runTest("DoWhile_Valid_3"); }
 @Test public void DoWhile_Valid_4_RuntimeTest() { runTest("DoWhile_Valid_4"); }
 @Test public void For_Valid_1_RuntimeTest() { runTest("For_Valid_1"); }
 @Test public void For_Valid_2_RuntimeTest() { runTest("For_Valid_2"); }
 @Test public void For_Valid_3_RuntimeTest() { runTest("For_Valid_3"); }
 @Test public void For_Valid_4_RuntimeTest() { runTest("For_Valid_4"); }
 @Ignore("Future Work") @Test public void For_Valid_5_RuntimeTest() { runTest("For_Valid_5"); }
 @Test public void For_Valid_6_RuntimeTest() { runTest("For_Valid_6"); }
 @Test public void For_Valid_7_RuntimeTest() { runTest("For_Valid_7"); }
 @Ignore("Future Work") @Test public void For_Valid_8_RuntimeTest() { runTest("For_Valid_8"); }
 @Test public void For_Valid_9_RuntimeTest() { runTest("For_Valid_9"); }
 @Test public void For_Valid_10_RuntimeTest() { runTest("For_Valid_10"); }
 @Test public void For_Valid_11_RuntimeTest() { runTest("For_Valid_11"); }
 @Test public void For_Valid_12_RuntimeTest() { runTest("For_Valid_12"); }
 @Test public void For_Valid_13_RuntimeTest() { runTest("For_Valid_13"); }
 @Test public void Function_Valid_1_RuntimeTest() { runTest("Function_Valid_1"); }
 @Test public void Function_Valid_2_RuntimeTest() { runTest("Function_Valid_2"); }
 @Test public void Function_Valid_3_RuntimeTest() { runTest("Function_Valid_3"); }
 @Test public void Function_Valid_4_RuntimeTest() { runTest("Function_Valid_4"); }
 @Test public void Function_Valid_5_RuntimeTest() { runTest("Function_Valid_5"); }
 @Test public void Function_Valid_6_RuntimeTest() { runTest("Function_Valid_6"); }
 @Test public void Function_Valid_7_RuntimeTest() { runTest("Function_Valid_7"); } 
 @Test public void Function_Valid_9_RuntimeTest() { runTest("Function_Valid_9"); }
 @Test public void Function_Valid_10_RuntimeTest() { runTest("Function_Valid_10"); } 
 @Test public void Function_Valid_12_RuntimeTest() { runTest("Function_Valid_12"); }
 @Test public void FunctionRef_Valid_1_RuntimeTest() { runTest("FunctionRef_Valid_1"); }
 @Ignore("Known Issue") @Test public void FunctionRef_Valid_2_RuntimeTest() { runTest("FunctionRef_Valid_2"); }
 @Ignore("Known Issue") @Test public void FunctionRef_Valid_3_RuntimeTest() { runTest("FunctionRef_Valid_3"); }
 @Test public void FunctionRef_Valid_4_RuntimeTest() { runTest("FunctionRef_Valid_4"); }
 @Test public void FunctionRef_Valid_5_RuntimeTest() { runTest("FunctionRef_Valid_5"); }
 @Ignore("Known Issue") @Test public void FunctionRef_Valid_6_RuntimeTest() { runTest("FunctionRef_Valid_6"); } 
 @Test public void FunctionRef_Valid_7_RuntimeTest() { runTest("FunctionRef_Valid_7"); }
 @Test public void FunctionRef_Valid_8_RuntimeTest() { runTest("FunctionRef_Valid_8"); }
 @Test public void FunctionRef_Valid_9_RuntimeTest() { runTest("FunctionRef_Valid_9"); }
 @Ignore("Future Work") @Test public void FunctionRef_Valid_10_RuntimeTest() { runTest("FunctionRef_Valid_10"); }
 @Test public void HexAssign_Valid_1_RuntimeTest() { runTest("HexAssign_Valid_1"); }
 @Test public void IfElse_Valid_1_RuntimeTest() { runTest("IfElse_Valid_1"); }
 @Test public void IfElse_Valid_2_RuntimeTest() { runTest("IfElse_Valid_2"); }
 @Test public void IfElse_Valid_3_RuntimeTest() { runTest("IfElse_Valid_3"); }
 @Test public void IfElse_Valid_4_RuntimeTest() { runTest("IfElse_Valid_4"); }
 @Test public void Import_Valid_1_RuntimeTest() { runTest("Import_Valid_1"); }
 @Test public void Import_Valid_2_RuntimeTest() { runTest("Import_Valid_2"); } 
 @Test public void Import_Valid_3_RuntimeTest() { runTest("Import_Valid_3"); }
 @Test public void Import_Valid_4_RuntimeTest() { runTest("Import_Valid_4"); }
 @Test public void Import_Valid_5_RuntimeTest() { runTest("Import_Valid_5"); }
 @Test public void Import_Valid_6_RuntimeTest() { runTest("Import_Valid_6"); }
 @Test public void Import_Valid_7_RuntimeTest() { runTest("Import_Valid_7"); }
 @Ignore("Future Work") @Test public void Interface_Valid_1_RuntimeTest() { runTest("Interface_Valid_1"); }
 @Ignore("Future Work") @Test public void Intersection_Valid_1_RuntimeTest() { runTest("Intersection_Valid_1"); }
 @Ignore("Future Work") @Test public void Intersection_Valid_2_RuntimeTest() { runTest("Intersection_Valid_2"); }
 @Test public void IntConst_Valid_1_RuntimeTest() { runTest("IntConst_Valid_1"); }
 @Test public void IntDefine_Valid_1_RuntimeTest() { runTest("IntDefine_Valid_1"); }
 @Test public void IntDiv_Valid_1_RuntimeTest() { runTest("IntDiv_Valid_1"); }
 @Test public void IntDiv_Valid_2_RuntimeTest() { runTest("IntDiv_Valid_2"); }
 @Test public void IntEquals_Valid_1_RuntimeTest() { runTest("IntEquals_Valid_1"); }
 @Test public void IntMul_Valid_1_RuntimeTest() { runTest("IntMul_Valid_1"); }
 @Test public void IntOp_Valid_1_RuntimeTest() { runTest("IntOp_Valid_1"); }
 @Test public void LengthOf_Valid_1_RuntimeTest() { runTest("LengthOf_Valid_1"); }
 @Test public void LengthOf_Valid_2_RuntimeTest() { runTest("LengthOf_Valid_2"); }
 @Test public void LengthOf_Valid_3_RuntimeTest() { runTest("LengthOf_Valid_3"); }
 @Test public void LengthOf_Valid_4_RuntimeTest() { runTest("LengthOf_Valid_4"); }
 @Test public void LengthOf_Valid_5_RuntimeTest() { runTest("LengthOf_Valid_5"); }
 @Test public void EffectiveList_Valid_1_RuntimeTest() { runTest("EffectiveList_Valid_1"); }
 @Test public void ListAccess_Valid_1_RuntimeTest() { runTest("ListAccess_Valid_1"); }
 @Test public void ListAccess_Valid_2_RuntimeTest() { runTest("ListAccess_Valid_2"); }
 @Test public void ListAccess_Valid_3_RuntimeTest() { runTest("ListAccess_Valid_3"); }
 @Test public void ListAccess_Valid_4_RuntimeTest() { runTest("ListAccess_Valid_4"); }
 @Test public void ListAccess_Valid_5_RuntimeTest() { runTest("ListAccess_Valid_5"); }
 @Test public void ListAccess_Valid_6_RuntimeTest() { runTest("ListAccess_Valid_6"); }
 @Test public void ListAppend_Valid_1_RuntimeTest() { runTest("ListAppend_Valid_1"); }
 @Test public void ListAppend_Valid_2_RuntimeTest() { runTest("ListAppend_Valid_2"); }
 @Test public void ListAppend_Valid_3_RuntimeTest() { runTest("ListAppend_Valid_3"); }
 @Test public void ListAppend_Valid_4_RuntimeTest() { runTest("ListAppend_Valid_4"); }
 @Test public void ListAppend_Valid_5_RuntimeTest() { runTest("ListAppend_Valid_5"); }
 @Test public void ListAppend_Valid_6_RuntimeTest() { runTest("ListAppend_Valid_6"); }
 @Test public void ListAppend_Valid_7_RuntimeTest() { runTest("ListAppend_Valid_7"); }
 @Test public void ListAppend_Valid_8_RuntimeTest() { runTest("ListAppend_Valid_8"); }
 @Test public void ListAppend_Valid_9_RuntimeTest() { runTest("ListAppend_Valid_9"); }
 @Test public void ListAssign_Valid_1_RuntimeTest() { runTest("ListAssign_Valid_1"); }
 @Test public void ListAssign_Valid_2_RuntimeTest() { runTest("ListAssign_Valid_2"); }
 @Test public void ListAssign_Valid_3_RuntimeTest() { runTest("ListAssign_Valid_3"); }
 @Test public void ListAssign_Valid_4_RuntimeTest() { runTest("ListAssign_Valid_4"); }
 @Test public void ListAssign_Valid_5_RuntimeTest() { runTest("ListAssign_Valid_5"); }
 @Test public void ListAssign_Valid_6_RuntimeTest() { runTest("ListAssign_Valid_6"); }
 @Test public void ListAssign_Valid_7_RuntimeTest() { runTest("ListAssign_Valid_7"); }
 @Test public void ListAssign_Valid_8_RuntimeTest() { runTest("ListAssign_Valid_8"); }
 @Test public void ListAssign_Valid_9_RuntimeTest() { runTest("ListAssign_Valid_9"); }
 @Test public void ListAssign_Valid_10_RuntimeTest() { runTest("ListAssign_Valid_10"); }
 @Test public void ListConversion_Valid_1_RuntimeTest() { runTest("ListConversion_Valid_1"); }
 @Test public void ListElemOf_Valid_1_RuntimeTest() { runTest("ListElemOf_Valid_1"); }
 @Test public void ListEmpty_Valid_1_RuntimeTest() { runTest("ListEmpty_Valid_1"); }
 @Test public void ListEquals_Valid_1_RuntimeTest() { runTest("ListEquals_Valid_1"); }
 @Test public void ListGenerator_Valid_1_RuntimeTest() { runTest("ListGenerator_Valid_1"); }
 @Test public void ListGenerator_Valid_2_RuntimeTest() { runTest("ListGenerator_Valid_2"); }
 @Test public void ListGenerator_Valid_3_RuntimeTest() { runTest("ListGenerator_Valid_3"); }
 @Test public void ListLength_Valid_1_RuntimeTest() { runTest("ListLength_Valid_1"); }
 @Test public void ListLength_Valid_2_RuntimeTest() { runTest("ListLength_Valid_2"); }
 @Test public void ListSublist_Valid_1_RuntimeTest() { runTest("ListSublist_Valid_1"); }
 @Test public void ListSublist_Valid_2_RuntimeTest() { runTest("ListSublist_Valid_2"); }
 @Test public void ListSublist_Valid_3_RuntimeTest() { runTest("ListSublist_Valid_3"); }
 
 @Test public void MethodCall_Valid_3_RuntimeTest() { runTest("MethodCall_Valid_3"); }
 
 @Test public void MethodCall_Valid_5_RuntimeTest() { runTest("MethodCall_Valid_5"); }
 @Test public void MethodCall_Valid_6_RuntimeTest() { runTest("MethodCall_Valid_6"); }
  
 @Test public void MessageSend_Valid_3_RuntimeTest() { runTest("MessageSend_Valid_3"); }
 @Test public void MessageSend_Valid_4_RuntimeTest() { runTest("MessageSend_Valid_4"); }
 @Test public void MessageSend_Valid_5_RuntimeTest() { runTest("MessageSend_Valid_5"); }
 @Test public void MessageSend_Valid_6_RuntimeTest() { runTest("MessageSend_Valid_6"); }
 @Test public void MessageSend_Valid_7_RuntimeTest() { runTest("MessageSend_Valid_7"); }
 
 @Ignore("Known Issue") @Test public void MethodCall_Valid_8_RuntimeTest() { runTest("MethodCall_Valid_8"); }
 @Test public void MethodRef_Valid_1_RuntimeTest() { runTest("MethodRef_Valid_1"); }
 @Test public void MethodRef_Valid_2_RuntimeTest() { runTest("MethodRef_Valid_2"); }
 @Test public void MessageRef_Valid_1_RuntimeTest() { runTest("MessageRef_Valid_1"); }
 @Test public void MessageRef_Valid_2_RuntimeTest() { runTest("MessageRef_Valid_2"); }
 @Test public void MultiLineComment_Valid_1_RuntimeTest() { runTest("MultiLineComment_Valid_1"); }
 @Test public void MultiLineComment_Valid_2_RuntimeTest() { runTest("MultiLineComment_Valid_2"); }
 @Test public void NegationType_Valid_1_RuntimeTest() { runTest("NegationType_Valid_1"); }
 @Test public void NegationType_Valid_2_RuntimeTest() { runTest("NegationType_Valid_2"); }
 @Ignore("Future Work") @Test public void NegationType_Valid_3_RuntimeTest() { runTest("NegationType_Valid_3"); }
 @Test public void NegationType_Valid_4_RuntimeTest() { runTest("NegationType_Valid_4"); }
 @Test public void OpenRecord_Valid_1_RuntimeTest() { runTest("OpenRecord_Valid_1"); }
 @Test public void OpenRecord_Valid_2_RuntimeTest() { runTest("OpenRecord_Valid_2"); }
 @Test public void OpenRecord_Valid_3_RuntimeTest() { runTest("OpenRecord_Valid_3"); }
 @Test public void OpenRecord_Valid_4_RuntimeTest() { runTest("OpenRecord_Valid_4"); }
 @Test public void OpenRecord_Valid_5_RuntimeTest() { runTest("OpenRecord_Valid_5"); }
 @Test public void OpenRecord_Valid_6_RuntimeTest() { runTest("OpenRecord_Valid_6"); }
 @Test public void OpenRecord_Valid_7_RuntimeTest() { runTest("OpenRecord_Valid_7"); }
 @Test public void OpenRecord_Valid_8_RuntimeTest() { runTest("OpenRecord_Valid_8"); }
 @Test public void OpenRecord_Valid_9_RuntimeTest() { runTest("OpenRecord_Valid_9"); }
 @Test public void OpenRecord_Valid_10_RuntimeTest() { runTest("OpenRecord_Valid_10"); }
 @Test public void Print_Valid_1_RuntimeTest() { runTest("Print_Valid_1"); }
 @Test public void ProcessAccess_Valid_1_RuntimeTest() { runTest("ProcessAccess_Valid_1"); }
 @Test public void ProcessAccess_Valid_2_RuntimeTest() { runTest("ProcessAccess_Valid_2"); }
 @Test public void Process_Valid_1_RuntimeTest() { runTest("Process_Valid_1"); }
 @Test public void Process_Valid_2_RuntimeTest() { runTest("Process_Valid_2"); }
 @Test public void Process_Valid_3_RuntimeTest() { runTest("Process_Valid_3"); }
 @Test public void Process_Valid_4_RuntimeTest() { runTest("Process_Valid_4"); }
 @Test public void Process_Valid_5_RuntimeTest() { runTest("Process_Valid_5"); }
 @Test public void Process_Valid_6_RuntimeTest() { runTest("Process_Valid_6"); }
 @Test public void Process_Valid_7_RuntimeTest() { runTest("Process_Valid_7"); }
 @Test public void Process_Valid_8_RuntimeTest() { runTest("Process_Valid_8"); }
 @Test public void Process_Valid_9_RuntimeTest() { runTest("Process_Valid_9"); }
 @Test public void Process_Valid_10_RuntimeTest() { runTest("Process_Valid_10"); }
 @Test public void Process_Valid_11_RuntimeTest() { runTest("Process_Valid_11"); }
 @Test public void Process_Valid_12_RuntimeTest() { runTest("Process_Valid_12"); }
 @Ignore("Known Issue") @Test public void Process_Valid_13_RuntimeTest() { runTest("Process_Valid_13"); }
 @Test public void Real_Valid_1_RuntimeTest() { runTest("Real_Valid_1"); }
 @Test public void RealConst_Valid_1_RuntimeTest() { runTest("RealConst_Valid_1"); }
 @Test public void RealDiv_Valid_1_RuntimeTest() { runTest("RealDiv_Valid_1"); }
 @Test public void RealDiv_Valid_2_RuntimeTest() { runTest("RealDiv_Valid_2"); }
 @Test public void RealDiv_Valid_3_RuntimeTest() { runTest("RealDiv_Valid_3"); }
 @Test public void RealDiv_Valid_4_RuntimeTest() { runTest("RealDiv_Valid_4"); }
 @Test public void RealNeg_Valid_1_RuntimeTest() { runTest("RealNeg_Valid_1"); }
 @Test public void RealSplit_Valid_1_RuntimeTest() { runTest("RealSplit_Valid_1"); }
 @Test public void RealSub_Valid_1_RuntimeTest() { runTest("RealSub_Valid_1"); }
 @Test public void RealSub_Valid_2_RuntimeTest() { runTest("RealSub_Valid_2"); }
 @Test public void RecordAccess_Valid_1_RuntimeTest() { runTest("RecordAccess_Valid_1"); }
 @Test public void RecordAccess_Valid_2_RuntimeTest() { runTest("RecordAccess_Valid_2"); }
 @Test public void RecordAssign_Valid_1_RuntimeTest() { runTest("RecordAssign_Valid_1"); }
 @Test public void RecordAssign_Valid_2_RuntimeTest() { runTest("RecordAssign_Valid_2"); }
 @Test public void RecordAssign_Valid_3_RuntimeTest() { runTest("RecordAssign_Valid_3"); }
 @Test public void RecordAssign_Valid_4_RuntimeTest() { runTest("RecordAssign_Valid_4"); }
 @Test public void RecordAssign_Valid_5_RuntimeTest() { runTest("RecordAssign_Valid_5"); }
 @Test public void RecordConversion_Valid_1_RuntimeTest() { runTest("RecordConversion_Valid_1"); }
 @Test public void RecordCoercion_Valid_1_RuntimeTest() { runTest("RecordCoercion_Valid_1"); }
 @Test public void RecordDefine_Valid_1_RuntimeTest() { runTest("RecordDefine_Valid_1"); }
 @Ignore("Known Issue") @Test public void RecordSubtype_Valid_1_RuntimeTest() { runTest("RecordSubtype_Valid_1"); }
 @Ignore("Known Issue") @Test public void RecordSubtype_Valid_2_RuntimeTest() { runTest("RecordSubtype_Valid_2"); }
 @Test public void RecursiveType_Valid_1_RuntimeTest() { runTest("RecursiveType_Valid_1"); }
 @Test public void RecursiveType_Valid_2_RuntimeTest() { runTest("RecursiveType_Valid_2"); }
 @Test public void RecursiveType_Valid_3_RuntimeTest() { runTest("RecursiveType_Valid_3"); }
 @Test public void RecursiveType_Valid_4_RuntimeTest() { runTest("RecursiveType_Valid_4"); }
 @Test public void RecursiveType_Valid_5_RuntimeTest() { runTest("RecursiveType_Valid_5"); }
 @Test public void RecursiveType_Valid_6_RuntimeTest() { runTest("RecursiveType_Valid_6"); }
 @Test public void RecursiveType_Valid_7_RuntimeTest() { runTest("RecursiveType_Valid_7"); }
 @Test public void RecursiveType_Valid_8_RuntimeTest() { runTest("RecursiveType_Valid_8"); }
 @Test public void RecursiveType_Valid_9_RuntimeTest() { runTest("RecursiveType_Valid_9"); }
 @Test public void RecursiveType_Valid_10_RuntimeTest() { runTest("RecursiveType_Valid_10"); }
 @Test public void RecursiveType_Valid_11_RuntimeTest() { runTest("RecursiveType_Valid_11"); }
 @Test public void RecursiveType_Valid_12_RuntimeTest() { runTest("RecursiveType_Valid_12"); }
 @Test public void RecursiveType_Valid_13_RuntimeTest() { runTest("RecursiveType_Valid_13"); }
 @Test public void RecursiveType_Valid_14_RuntimeTest() { runTest("RecursiveType_Valid_14"); }
 @Test public void RecursiveType_Valid_15_RuntimeTest() { runTest("RecursiveType_Valid_15"); }
 @Test public void RecursiveType_Valid_16_RuntimeTest() { runTest("RecursiveType_Valid_16"); } 
 @Test public void RecursiveType_Valid_17_RuntimeTest() { runTest("RecursiveType_Valid_17"); }
 @Test public void RecursiveType_Valid_18_RuntimeTest() { runTest("RecursiveType_Valid_18"); }
 @Ignore("Known Issue")
 @Test public void RecursiveType_Valid_19_RuntimeTest() { runTest("RecursiveType_Valid_19"); }
 @Test public void RecursiveType_Valid_20_RuntimeTest() { runTest("RecursiveType_Valid_20"); }
 @Test public void Remainder_Valid_1_RuntimeTest() { runTest("Remainder_Valid_1"); }
 @Test public void Resolution_Valid_1_RuntimeTest() { runTest("Resolution_Valid_1"); }
 @Test public void SetAssign_Valid_1_RuntimeTest() { runTest("SetAssign_Valid_1"); }
 @Test public void SetComprehension_Valid_1_RuntimeTest() { runTest("SetComprehension_Valid_1"); }
 @Test public void SetComprehension_Valid_2_RuntimeTest() { runTest("SetComprehension_Valid_2"); }
 @Test public void SetComprehension_Valid_3_RuntimeTest() { runTest("SetComprehension_Valid_3"); }
 @Test public void SetComprehension_Valid_4_RuntimeTest() { runTest("SetComprehension_Valid_4"); }
 @Test public void SetComprehension_Valid_5_RuntimeTest() { runTest("SetComprehension_Valid_5"); }
 @Test public void SetComprehension_Valid_6_RuntimeTest() { runTest("SetComprehension_Valid_6"); }
 @Test public void SetComprehension_Valid_7_RuntimeTest() { runTest("SetComprehension_Valid_7"); }
 @Test public void SetComprehension_Valid_8_RuntimeTest() { runTest("SetComprehension_Valid_8"); }
 @Test public void SetComprehension_Valid_9_RuntimeTest() { runTest("SetComprehension_Valid_9"); }
 @Test public void SetComprehension_Valid_10_RuntimeTest() { runTest("SetComprehension_Valid_10"); }
 @Test public void SetConversion_Valid_1_RuntimeTest() { runTest("SetConversion_Valid_1"); }
 @Test public void SetDefine_Valid_1_RuntimeTest() { runTest("SetDefine_Valid_1"); }
 @Test public void SetDifference_Valid_1_RuntimeTest() { runTest("SetDifference_Valid_1"); }
 @Test public void SetElemOf_Valid_1_RuntimeTest() { runTest("SetElemOf_Valid_1"); }
 @Test public void SetEmpty_Valid_1_RuntimeTest() { runTest("SetEmpty_Valid_1"); }
 @Test public void SetGenerator_Valid_1_RuntimeTest() { runTest("SetGenerator_Valid_1"); }
 @Test public void SetIntersection_Valid_1_RuntimeTest() { runTest("SetIntersection_Valid_1"); }
 @Test public void SetIntersection_Valid_2_RuntimeTest() { runTest("SetIntersection_Valid_2"); }
 @Test public void SetIntersection_Valid_3_RuntimeTest() { runTest("SetIntersection_Valid_3"); }
 @Test public void SetIntersection_Valid_4_RuntimeTest() { runTest("SetIntersection_Valid_4"); }
 @Test public void SetIntersect_Valid_1_RuntimeTest() { runTest("SetIntersect_Valid_1"); }
 @Test public void SetIntersect_Valid_2_RuntimeTest() { runTest("SetIntersect_Valid_2"); }
 @Test public void SetLength_Valid_1_RuntimeTest() { runTest("SetLength_Valid_1"); }
 @Test public void SetSubset_Valid_1_RuntimeTest() { runTest("SetSubset_Valid_1"); } 
 @Test public void SetSubset_Valid_3_RuntimeTest() { runTest("SetSubset_Valid_3"); }
 @Test public void SetSubset_Valid_4_RuntimeTest() { runTest("SetSubset_Valid_4"); }
 @Test public void SetSubset_Valid_5_RuntimeTest() { runTest("SetSubset_Valid_5"); }
 @Test public void SetSubset_Valid_6_RuntimeTest() { runTest("SetSubset_Valid_6"); } 
 @Test public void SetUnion_Valid_1_RuntimeTest() { runTest("SetUnion_Valid_1"); }
 @Test public void SetUnion_Valid_2_RuntimeTest() { runTest("SetUnion_Valid_2"); }
 @Test public void SetUnion_Valid_3_RuntimeTest() { runTest("SetUnion_Valid_3"); }
 @Test public void SetUnion_Valid_4_RuntimeTest() { runTest("SetUnion_Valid_4"); }
 @Test public void SetUnion_Valid_5_RuntimeTest() { runTest("SetUnion_Valid_5"); }
 @Test public void SetUnion_Valid_6_RuntimeTest() { runTest("SetUnion_Valid_6"); }
 @Test public void SetUnion_Valid_7_RuntimeTest() { runTest("SetUnion_Valid_7"); }
 @Test public void SetUnion_Valid_8_RuntimeTest() { runTest("SetUnion_Valid_8"); }
 @Test public void SetUnion_Valid_9_RuntimeTest() { runTest("SetUnion_Valid_9"); }
 @Test public void SingleLineComment_Valid_1_RuntimeTest() { runTest("SingleLineComment_Valid_1"); }
 @Test public void Subtype_Valid_3_RuntimeTest() { runTest("Subtype_Valid_3"); }
 @Test public void Subtype_Valid_4_RuntimeTest() { runTest("Subtype_Valid_4"); }
 @Test public void Subtype_Valid_5_RuntimeTest() { runTest("Subtype_Valid_5"); }
 @Test public void Subtype_Valid_6_RuntimeTest() { runTest("Subtype_Valid_6"); }
 @Test public void Subtype_Valid_7_RuntimeTest() { runTest("Subtype_Valid_7"); }
 @Test public void Subtype_Valid_8_RuntimeTest() { runTest("Subtype_Valid_8"); }
 @Test public void Subtype_Valid_9_RuntimeTest() { runTest("Subtype_Valid_9"); }
 @Test public void String_Valid_1_RuntimeTest() { runTest("String_Valid_1"); }
 @Test public void String_Valid_2_RuntimeTest() { runTest("String_Valid_2"); }
 @Test public void String_Valid_3_RuntimeTest() { runTest("String_Valid_3"); }
 @Test public void String_Valid_4_RuntimeTest() { runTest("String_Valid_4"); }
 @Test public void String_Valid_5_RuntimeTest() { runTest("String_Valid_5"); }
 @Test public void String_Valid_6_RuntimeTest() { runTest("String_Valid_6"); }
 @Test public void String_Valid_7_RuntimeTest() { runTest("String_Valid_7"); }
 @Test public void Switch_Valid_1_RuntimeTest() { runTest("Switch_Valid_1"); }
 @Test public void Switch_Valid_2_RuntimeTest() { runTest("Switch_Valid_2"); }
 @Test public void Switch_Valid_3_RuntimeTest() { runTest("Switch_Valid_3"); }
 @Test public void Switch_Valid_4_RuntimeTest() { runTest("Switch_Valid_4"); }
 @Test public void Switch_Valid_5_RuntimeTest() { runTest("Switch_Valid_5"); }
 @Test public void Switch_Valid_6_RuntimeTest() { runTest("Switch_Valid_6"); }
 @Test public void Switch_Valid_7_RuntimeTest() { runTest("Switch_Valid_7"); }
 @Test public void Switch_Valid_8_RuntimeTest() { runTest("Switch_Valid_8"); }
 @Test public void Switch_Valid_9_RuntimeTest() { runTest("Switch_Valid_9"); }
 @Test public void Syntax_Valid_1_RuntimeTest() { runTest("Syntax_Valid_1"); }
 @Test public void TryCatch_Valid_1_RuntimeTest() { runTest("TryCatch_Valid_1"); }
 @Test public void TryCatch_Valid_2_RuntimeTest() { runTest("TryCatch_Valid_2"); }
 @Test public void TryCatch_Valid_3_RuntimeTest() { runTest("TryCatch_Valid_3"); }
 @Test public void TryCatch_Valid_4_RuntimeTest() { runTest("TryCatch_Valid_4"); }
 @Test public void TupleType_Valid_1_RuntimeTest() { runTest("TupleType_Valid_1"); }
 @Test public void TupleType_Valid_2_RuntimeTest() { runTest("TupleType_Valid_2"); }
 @Test public void TupleType_Valid_3_RuntimeTest() { runTest("TupleType_Valid_3"); }
 @Test public void TupleType_Valid_4_RuntimeTest() { runTest("TupleType_Valid_4"); }
 @Test public void TupleType_Valid_5_RuntimeTest() { runTest("TupleType_Valid_5"); }
 @Test public void TupleType_Valid_6_RuntimeTest() { runTest("TupleType_Valid_6"); }
 @Test public void TupleType_Valid_7_RuntimeTest() { runTest("TupleType_Valid_7"); }
 @Test public void TupleType_Valid_8_RuntimeTest() { runTest("TupleType_Valid_8"); }
 @Test public void TypeEquals_Valid_1_RuntimeTest() { runTest("TypeEquals_Valid_1"); }
 @Test public void TypeEquals_Valid_2_RuntimeTest() { runTest("TypeEquals_Valid_2"); }
 @Test public void TypeEquals_Valid_3_RuntimeTest() { runTest("TypeEquals_Valid_3"); }
 @Test public void TypeEquals_Valid_4_RuntimeTest() { runTest("TypeEquals_Valid_4"); }
 @Test public void TypeEquals_Valid_5_RuntimeTest() { runTest("TypeEquals_Valid_5"); }
 @Ignore("Future Work") 
 @Test public void TypeEquals_Valid_6_RuntimeTest() { runTest("TypeEquals_Valid_6"); }
 @Ignore("Future Work")
 @Test public void TypeEquals_Valid_7_RuntimeTest() { runTest("TypeEquals_Valid_7"); }
 @Test public void TypeEquals_Valid_8_RuntimeTest() { runTest("TypeEquals_Valid_8"); }
 @Test public void TypeEquals_Valid_9_RuntimeTest() { runTest("TypeEquals_Valid_9"); }
 @Test public void TypeEquals_Valid_10_RuntimeTest() { runTest("TypeEquals_Valid_10"); }
 @Test public void TypeEquals_Valid_11_RuntimeTest() { runTest("TypeEquals_Valid_11"); }
 @Test public void TypeEquals_Valid_12_RuntimeTest() { runTest("TypeEquals_Valid_12"); }
 @Test public void TypeEquals_Valid_13_RuntimeTest() { runTest("TypeEquals_Valid_13"); }
 @Test public void TypeEquals_Valid_14_RuntimeTest() { runTest("TypeEquals_Valid_14"); }
 @Ignore("Future Work")
 @Test public void TypeEquals_Valid_15_RuntimeTest() { runTest("TypeEquals_Valid_15"); }
 @Test public void TypeEquals_Valid_16_RuntimeTest() { runTest("TypeEquals_Valid_16"); }
 @Test public void TypeEquals_Valid_17_RuntimeTest() { runTest("TypeEquals_Valid_17"); }
 @Test public void TypeEquals_Valid_18_RuntimeTest() { runTest("TypeEquals_Valid_18"); }
 @Test public void TypeEquals_Valid_19_RuntimeTest() { runTest("TypeEquals_Valid_19"); }
 @Test public void TypeEquals_Valid_20_RuntimeTest() { runTest("TypeEquals_Valid_20"); }
 @Test public void TypeEquals_Valid_21_RuntimeTest() { runTest("TypeEquals_Valid_21"); }
 @Test public void TypeEquals_Valid_22_RuntimeTest() { runTest("TypeEquals_Valid_22"); }
 @Test public void TypeEquals_Valid_23_RuntimeTest() { runTest("TypeEquals_Valid_23"); }
 @Test public void TypeEquals_Valid_24_RuntimeTest() { runTest("TypeEquals_Valid_24"); }
 @Test public void TypeEquals_Valid_25_RuntimeTest() { runTest("TypeEquals_Valid_25"); }
 @Test public void TypeEquals_Valid_26_RuntimeTest() { runTest("TypeEquals_Valid_26"); }
 @Test public void TypeEquals_Valid_27_RuntimeTest() { runTest("TypeEquals_Valid_27"); }
 @Test public void TypeEquals_Valid_28_RuntimeTest() { runTest("TypeEquals_Valid_28"); } 
 @Test public void TypeEquals_Valid_29_RuntimeTest() { runTest("TypeEquals_Valid_29"); }
 @Test public void TypeEquals_Valid_30_RuntimeTest() { runTest("TypeEquals_Valid_30"); }
 @Test public void TypeEquals_Valid_31_RuntimeTest() { runTest("TypeEquals_Valid_31"); }
 @Test public void TypeEquals_Valid_32_RuntimeTest() { runTest("TypeEquals_Valid_32"); }
 @Test public void TypeEquals_Valid_33_RuntimeTest() { runTest("TypeEquals_Valid_33"); }
 @Test public void TypeEquals_Valid_34_RuntimeTest() { runTest("TypeEquals_Valid_34"); }
 @Test public void TypeEquals_Valid_35_RuntimeTest() { runTest("TypeEquals_Valid_35"); }
 @Test public void TypeEquals_Valid_36_RuntimeTest() { runTest("TypeEquals_Valid_36"); }
 @Test public void TypeEquals_Valid_37_RuntimeTest() { runTest("TypeEquals_Valid_37"); }
 @Test public void TypeEquals_Valid_38_RuntimeTest() { runTest("TypeEquals_Valid_38"); }
 @Ignore("Known Issue") @Test public void Update_Valid_1_RuntimeTest() { runTest("Update_Valid_1"); }
 @Ignore("Known Issue") @Test public void Update_Valid_2_RuntimeTest() { runTest("Update_Valid_2"); }
 @Test public void UnionType_Valid_1_RuntimeTest() { runTest("UnionType_Valid_1"); }
 @Test public void UnionType_Valid_2_RuntimeTest() { runTest("UnionType_Valid_2"); }
 @Test public void UnionType_Valid_4_RuntimeTest() { runTest("UnionType_Valid_4"); }
 @Test public void UnionType_Valid_5_RuntimeTest() { runTest("UnionType_Valid_5"); }
 @Test public void UnionType_Valid_6_RuntimeTest() { runTest("UnionType_Valid_6"); }
 @Test public void UnionType_Valid_7_RuntimeTest() { runTest("UnionType_Valid_7"); }
 @Test public void UnionType_Valid_8_RuntimeTest() { runTest("UnionType_Valid_8"); }
 @Test public void UnionType_Valid_9_RuntimeTest() { runTest("UnionType_Valid_9"); }
 @Test public void UnionType_Valid_10_RuntimeTest() { runTest("UnionType_Valid_10"); }
 @Test public void UnionType_Valid_11_RuntimeTest() { runTest("UnionType_Valid_11"); }
 @Test public void UnionType_Valid_12_RuntimeTest() { runTest("UnionType_Valid_12"); }
 @Test public void UnionType_Valid_13_RuntimeTest() { runTest("UnionType_Valid_13"); }
 @Test public void UnionType_Valid_14_RuntimeTest() { runTest("UnionType_Valid_14"); }
 @Test public void UnionType_Valid_15_RuntimeTest() { runTest("UnionType_Valid_15"); }
 @Test public void UnionType_Valid_16_RuntimeTest() { runTest("UnionType_Valid_16"); }
 @Test public void UnionType_Valid_17_RuntimeTest() { runTest("UnionType_Valid_17"); }
 @Test public void UnionType_Valid_18_RuntimeTest() { runTest("UnionType_Valid_18"); }
 @Test public void VarDecl_Valid_1_RuntimeTest() { runTest("VarDecl_Valid_1"); }
 @Test public void VarDecl_Valid_2_RuntimeTest() { runTest("VarDecl_Valid_2"); }
 @Test public void VarDecl_Valid_3_RuntimeTest() { runTest("VarDecl_Valid_3"); }
 @Test public void While_Valid_1_RuntimeTest() { runTest("While_Valid_1"); }
 @Test public void While_Valid_2_RuntimeTest() { runTest("While_Valid_2"); }
 @Test public void While_Valid_3_RuntimeTest() { runTest("While_Valid_3"); }
 @Test public void While_Valid_4_RuntimeTest() { runTest("While_Valid_4"); }
 @Test public void While_Valid_5_RuntimeTest() { runTest("While_Valid_5"); }
 @Test public void While_Valid_6_RuntimeTest() { runTest("While_Valid_6"); } 
 @Test public void While_Valid_7_RuntimeTest() { runTest("While_Valid_7"); }
 @Test public void While_Valid_8_RuntimeTest() { runTest("While_Valid_8"); }
 @Test public void While_Valid_9_RuntimeTest() { runTest("While_Valid_9"); }
 @Test public void While_Valid_10_RuntimeTest() { runTest("While_Valid_10"); }
}
