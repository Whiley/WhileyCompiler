package wyopcl.testing.interpreter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

public class AllInvalidTestCase {
	@Rule
	public TestRule timeout = new Timeout(5000);
	private BaseTestUtil util;

	@Before
	public void setUp() throws Exception {
		util = new BaseTestUtil();
	}

	@After
	public void tearDown() throws Exception {
		util.terminate();
		util = null;
	}
	
	@Test	
	public void test_Assert_Invalid_1() {		
		util.exec("Assert_Invalid_1");
	}
		
	@Test	
	public void test_Assert_Invalid_2() {		
		util.exec("Assert_Invalid_2");
	}
	@Test
	public void test_Assign_Invalid_1() {
		util.exec("Assign_Invalid_1");
	}
	
	@Test
	public void test_Assign_Invalid_2() {
		util.exec("Assign_Invalid_2");
	}
	
	@Test
	public void test_Assign_Invalid_3() {
		util.exec("Assign_Invalid_3");
	}
	
	@Test
	public void test_Assign_Invalid_4() {
		util.exec("Assign_Invalid_4");
	}
	@Test
	public void test_Assign_Invalid_5() {
		util.exec("Assign_Invalid_5");
	}
	
	@Test
	public void test_Assign_Invalid_6() {
		util.exec("Assign_Invalid_6");
	}
	
	@Test
	public void test_Assign_Invalid_7() {
		util.exec("Assign_Invalid_7");
	}
	
	@Test
	public void test_Assign_Invalid_8() {
		util.exec("Assign_Invalid_8");
	}
	
	@Test
	public void test_Assign_Invalid_9() {
		util.exec("Assign_Invalid_9");
	}
	
	@Test
	public void test_Byte_Invalid_1() {
		util.exec("Byte_Invalid_1");
	}
	

	@Test
	public void test_Cast_Invalid_1() {
		util.exec("Cast_Invalid_1");
	}
	
	@Test
	public void test_Cast_Invalid_2() {
		util.exec("Cast_Invalid_2");
	}
	
	@Test
	public void test_Cast_Invalid_3() {
		util.exec("Cast_Invalid_3");
	}
	
	@Test
	public void test_Cast_Invalid_4() {
		util.exec("Cast_Invalid_4");
	}
	@Test
	public void test_Char_Invalid_1() {
		util.exec("Char_Invalid_1");
	}


	@Test
	public void test_Coercion_InInvalid_1() {
		util.exec("Coercion_Invalid_1");
	}

	@Test
	public void test_Coercion_Invalid_2() {
		util.exec("Coercion_Invalid_2");
	}
	
	@Test
	@Ignore("unclassified")
	public void test_Constant_Invalid_1() {
		util.exec("Constant_Invalid_1");
	}

	@Test
	public void test_Constant_Invalid_2() {
		util.exec("Constant_Invalid_2");
	}

	@Ignore("unclassified")
	@Test
	public void test_ConstrainedDictionary_Invalid_1() {
		util.exec("ConstrainedDictionary_Invalid_1");
	}

	@Test
	public void test_ConstrainedInt_Invalid_1() {
		util.exec("ConstrainedInt_Invalid_1");
	}

	@Ignore("unclassified")
	@Test
	public void test_ConstrainedInt_Invalid_10() {
		util.exec("ConstrainedInt_Invalid_10");
	}
	
	@Ignore("Missing 'ConstrainedInt_Invalid_12.sysout'")
	@Test
	public void test_ConstrainedInt_Invalid_12() {
		util.exec("ConstrainedInt_Invalid_12");
	}

	
	@Test
	public void test_ConstrainedInt_Invalid_2() {
		util.exec("ConstrainedInt_Invalid_2");
	}	

	@Test
	public void test_ConstrainedInt_Invalid_3() {
		util.exec("ConstrainedInt_Invalid_3");
	}
	
	@Test
	public void test_ConstrainedInt_Invalid_4() {
		util.exec("ConstrainedInt_Invalid_4");
	}

	@Test
	public void test_ConstrainedInt_Invalid_5() {
		util.exec("ConstrainedInt_Invalid_5");
	}
	
	@Test
	public void test_ConstrainedInt_Invalid_6() {
		util.exec("ConstrainedInt_Invalid_6");
	}

	@Test
	public void test_ConstrainedInt_Invalid_8() {
		util.exec("ConstrainedInt_Invalid_8");
	}
	
	@Test
	@Ignore("Timeout")
	public void test_ConstrainedInt_Invalid_9() {
		util.exec("ConstrainedInt_Invalid_9");
	}

	@Test
	public void test_ConstrainedList_Invalid_1() {
		util.exec("ConstrainedList_Invalid_1");
	}
	
	@Test
	public void test_ConstrainedList_Invalid_2() {
		util.exec("ConstrainedList_Invalid_2");
	}

	@Ignore("unclassified")
	@Test
	public void test_ConstrainedList_Invalid_3() {
		util.exec("ConstrainedList_Invalid_3");
	}

	
	@Test
	public void test_ConstrainedSet_Invalid_1() {
		util.exec("ConstrainedSet_Invalid_1");
	}

	@Test
	public void test_ConstrainedSet_Invalid_2() {
		util.exec("ConstrainedSet_Invalid_2");
	}

	@Test
	public void test_ConstrainedSet_Invalid_3() {
		util.exec("ConstrainedSet_Invalid_3");
	}
	
	@Test
	public void test_ConstrainedTuple_Invalid_1() {
		util.exec("ConstrainedTuple_Invalid_1");
	}

	@Test
	public void test_Contractive_Invalid_1() {
		util.exec("Contractive_Invalid_1");
	}

	@Test
	public void test_Contractive_Invalid_2() {
		util.exec("Contractive_Invalid_2");
	}
	
	@Ignore("Missing DefineAssign_Invalid_1.sysout")
	@Test
	public void test_DefineAssign_Invalid_1() {
		util.exec("DefineAssign_Invalid_1");
	}
	@Ignore("Missing DefineAssign_Invalid_2.sysout")
	@Test
	public void test_DefineAssign_Invalid_2() {
		util.exec("DefineAssign_Invalid_2");
	}
	@Ignore("Missing DefineAssign_Invalid_3.sysout")
	@Test
	public void test_DefineAssign_Invalid_3() {
		util.exec("DefineAssign_Invalid_3");
	}
	@Ignore("Missing DefineAssign_Invalid_4.sysout")
	@Test
	public void test_DefineAssign_Invalid_4() {
		util.exec("DefineAssign_Invalid_4");
	}
	
	@Test
	public void test_EndOfFile_Invalid_1() {
		util.exec("EndOfFile_Invalid_1");
	}
	
	@Test
	public void test_Ensures_Invalid_1() {
		util.exec("Ensures_Invalid_1");
	}

	@Test
	public void test_Ensures_Invalid_2() {
		util.exec("Ensures_Invalid_2");
	}
	
	@Test
	public void test_Ensures_Invalid_3() {
		util.exec("Ensures_Invalid_3");
	}

	@Test
	@Ignore("unclassified")
	public void test_Export_Invalid_1() {
		util.exec("Export_Invalid_1");
	}
	
	@Test
	public void test_For_Invalid_1() {
		util.exec("For_Invalid_1");
	}

	
	@Test
	public void test_For_Invalid_5() {
		util.exec("For_Invalid_5");
	}

	@Test
	public void test_For_Invalid_6() {
		util.exec("For_Invalid_6");
	}
	
	@Test
	public void test_For_Invalid_7() {
		util.exec("For_Invalid_7");
	}

	@Test
	public void test_For_Invalid_8() {
		util.exec("For_Invalid_8");
	}

	@Ignore("Issue #409")
	@Test
	public void test_For_Invalid_9() {
		util.exec("For_Invalid_9");
	}

	
	@Test
	public void test_FunctionRef_Invalid_1() {
		util.exec("FunctionRef_Invalid_1");
	}

	@Test
	public void test_FunctionRef_Invalid_2() {
		util.exec("FunctionRef_Invalid_2");
	}

	@Test
	public void test_FunctionRef_Invalid_3() {
		util.exec("FunctionRef_Invalid_3");
	}

	@Test
	public void test_FunctionRef_Invalid_4() {
		util.exec("FunctionRef_Invalid_4");
	}

	@Test
	public void test_FunctionRef_Invalid_5() {
		util.exec("FunctionRef_Invalid_5");
	}

	@Test
	public void test_FunctionRef_Invalid_6() {
		util.exec("FunctionRef_Invalid_6");
	}

	@Test
	public void test_FunctionRef_Invalid_7() {
		util.exec("FunctionRef_Invalid_7");
	}
	@Ignore("Missing FunctionRef_Invalid_9.sysout")
	@Test
	public void test_FunctionRef_Invalid_9() {
		util.exec("FunctionRef_Invalid_9");
	}

	@Test
	public void test_Function_Invalid_1() {
		util.exec("Function_Invalid_1");
	}

	@Test
	public void test_Function_Invalid_10() {
		util.exec("Function_Invalid_10");
	}
	
	@Ignore("Internal Failure")
	@Test
	public void test_Function_Invalid_2() {
		util.exec("Function_Invalid_2");
	}

	@Test
	public void test_Function_Invalid_3() {
		util.exec("Function_Invalid_3");
	}

	@Test
	public void test_Function_Invalid_4() {
		util.exec("Function_Invalid_4");
	}
	@Ignore("unclassified")
	@Test
	public void test_Function_Invalid_9() {
		util.exec("Function_Invalid_9");
	}
	
	@Test
	public void test_If_Invalid_1() {
		util.exec("If_Invalid_1");
	}

	@Test
	public void test_If_Invalid_2() {
		util.exec("If_Invalid_2");
	}

	@Test
	public void test_If_Invalid_3() {
		util.exec("If_Invalid_3");
	}

	@Test
	public void test_If_Invalid_4() {
		util.exec("If_Invalid_4");
	}
	
	@Test
	public void test_If_Invalid_5() {
		util.exec("If_Invalid_5");
	}

	@Test
	public void test_Import_Invalid_1() {
		util.exec("Import_Invalid_1");
	}
	@Ignore("unclassified")
	@Test
	public void test_Intersection_Invalid_1() {
		util.exec("Intersection_Invalid_1");
	}
	
	@Ignore("unclassified")
	@Test
	public void test_Intersection_Invalid_2() {
		util.exec("Intersection_Invalid_2");
	}
	
	@Test
	public void test_IntDiv_Invalid_1() {
		util.exec("IntDiv_Invalid_1");
	}
	
	@Ignore("unclassified")
	@Test
	public void test_Lambda_Invalid_3() {
		util.exec("Lambda_Invalid_3");
	}

	@Test
	public void test_ListAccess_Invalid_1() {
		util.exec("ListAccess_Invalid_1");
	}

	@Test
	public void test_ListAccess_Invalid_3() {
		util.exec("ListAccess_Invalid_3");
	}
	
	@Test
	public void test_ListAccess_Invalid_5() {
		util.exec("ListAccess_Invalid_5");
	}
	
	@Test
	public void test_ListAppend_Invalid_1() {
		util.exec("ListAppend_Invalid_1");
	}
	
	@Test
	public void test_ListAppend_Invalid_2() {
		util.exec("ListAppend_Invalid_2");
	}

	@Ignore("Unknown Issue")
	@Test
	public void test_ListAppend_Invalid_3() {
		util.exec("ListAppend_Invalid_3");
	}

	@Ignore("Internal Failure")
	@Test
	public void test_ListAppend_Invalid_4() {
		util.exec("ListAppend_Invalid_4");
	}

	@Ignore("Internal Failure")
	@Test
	public void test_ListAppend_Invalid_5() {
		util.exec("ListAppend_Invalid_5");
	}
	

	@Test
	public void test_ListAssign_Invalid_1() {
		util.exec("ListAssign_Invalid_1");
	}

	@Ignore("Infinite Loop?")
	@Test
	public void test_ListAssign_Invalid_2() {
		util.exec("ListAssign_Invalid_2");
	}

	@Ignore("Infinite Loop?")
	@Test
	public void test_ListAssign_Invalid_3() {
		util.exec("ListAssign_Invalid_3");
	}
	

	@Test
	public void test_ListConversion_Invalid_1() {
		util.exec("ListConversion_Invalid_1");
	}

	@Test
	public void test_ListElemOf_Invalid_1() {
		util.exec("ListElemOf_Invalid_1");
	}
	
	@Test
	public void test_ListElemOf_Invalid_2() {
		util.exec("ListElemOf_Invalid_2");
	}

	@Test
	public void test_ListEmpty_Invalid_1() {
		util.exec("ListEmpty_Invalid_1");
	}
	
	@Test
	public void test_ListEmpty_Invalid_2() {
		util.exec("ListEmpty_Invalid_2");
	}

	@Test
	public void test_ListEquals_Invalid_1() {
		util.exec("ListEquals_Invalid_1");
	}	

	@Test
	public void test_ListLength_Invalid_1() {
		util.exec("ListLength_Invalid_1");
	}

	@Test
	public void test_ListLength_Invalid_2() {
		util.exec("ListLength_Invalid_2");
	}

	@Test
	public void test_ListLength_Invalid_3() {
		util.exec("ListLength_Invalid_3");
	}

	@Test
	public void test_ListSublist_Invalid_1() {
		util.exec("ListSublist_Invalid_1");
	}

	@Ignore("unclassified")
	@Test
	public void test_ListSublist_Invalid_2() {
		util.exec("ListSublist_Invalid_2");
	}

	@Test
	public void test_ListSublist_Invalid_3() {
		util.exec("ListSublist_Invalid_3");
	}

	@Ignore("unclassified")
	@Test
	public void test_ListUpdate_Invalid_1() {
		util.exec("ListUpdate_Invalid_1");
	}

	@Test
	public void test_List_Invalid_1() {
		util.exec("List_Invalid_1");
	}
	
	@Test
	public void test_List_Invalid_2() {
		util.exec("List_Invalid_2");
	}
	
	@Test
	public void test_List_Invalid_3() {
		util.exec("List_Invalid_3");
	}
	
	@Test
	public void test_List_Invalid_4() {
		util.exec("List_Invalid_4");
	}
	
	
	@Test
	public void test_List_Invalid_5() {
		util.exec("List_Invalid_5");
	}
	
	@Test
	public void test_List_Invalid_6() {
		util.exec("List_Invalid_6");
	}
	
	@Test
	public void test_List_Invalid_7() {
		util.exec("List_Invalid_7");
	}
	
	@Test
	public void test_MethodCall_Invalid_1() {
		util.exec("MethodCall_Invalid_1");
	}

	@Test
	public void test_MethodCall_Invalid_2() {
		util.exec("MethodCall_Invalid_2");
	}

	@Test
	public void test_MethodCall_Invalid_3() {
		util.exec("MethodCall_Invalid_3");
	}

	@Test
	public void test_MethodCall_Invalid_4() {
		util.exec("MethodCall_Invalid_4");
	}
	
	@Test
	public void test_MethodCall_Invalid_5() {
		util.exec("MethodCall_Invalid_5");
	}

	@Test
	public void test_MethodCall_Invalid_6() {
		util.exec("MethodCall_Invalid_6");
	}

	@Test
	public void test_MethodCall_Invalid_7() {
		util.exec("MethodCall_Invalid_7");
	}

	@Test
	public void test_MethodCall_Invalid_8() {
		util.exec("MethodCall_Invalid_8");
	}
	@Ignore("unclassified")
	@Test
	public void test_MethodRef_Invalid_1() {
		util.exec("MethodRef_Invalid_1");
	}
	@Ignore("Internal Failure") 
	@Test
	public void test_MethodRef_Invalid_2() {
		util.exec("MethodRef_Invalid_2");
	}
	@Ignore("unclassified")
	@Test
	public void test_MethodRef_Invalid_3() {
		util.exec("MethodRef_Invalid_3");
	}

	@Ignore("unclassified")
	@Test
	public void test_Native_Invalid_1() {
		util.exec("Native_Invalid_1");
	}	
	
	@Test
	public void test_NegationType_Invalid_1() {
		util.exec("NegationType_Invalid_1");
	}
	@Ignore("unclassified")
	@Test
	public void test_NegationType_Invalid_2() {
		util.exec("NegationType_Invalid_2");
	}

	@Test
	public void test_NegationType_Invalid_3() {
		util.exec("NegationType_Invalid_3");
	}
	
	@Test
	public void test_OpenRecord_Invalid_1() {
		util.exec("OpenRecord_Invalid_1");
	}
	
	@Ignore("unclassified")
	@Test
	public void test_OpenRecord_Invalid_2() {
		util.exec("OpenRecord_Invalid_2");
	}

	@Test
	public void test_OpenRecord_Invalid_3() {
		util.exec("OpenRecord_Invalid_3");
	}

	@Test
	public void test_OpenRecord_Invalid_4() {
		util.exec("OpenRecord_Invalid_4");
	}

	@Test
	public void test_OpenRecord_Invalid_5() {
		util.exec("OpenRecord_Invalid_5");
	}

	@Test
	public void test_OpenRecord_Invalid_6() {
		util.exec("OpenRecord_Invalid_6");
	}

	@Test
	public void test_OpenRecord_Invalid_7() {
		util.exec("OpenRecord_Invalid_7");
	}
	
	@Test
	public void test_Parameter_Invalid_1() {
		util.exec("Parameter_Invalid_1");
	}
	
	@Test
	public void test_Parameter_Invalid_2() {
		util.exec("Parameter_Invalid_2");
	}
	
	@Test
	public void test_ProcessAccess_Invalid_1() {
		util.exec("ProcessAccess_Invalid_1");
	}

	@Test
	public void test_ProcessAccess_Invalid_2() {
		util.exec("ProcessAccess_Invalid_2");
	}

	@Test
	public void test_ProcessAccess_Invalid_3() {
		util.exec("ProcessAccess_Invalid_3");
	}

	
	@Test
	public void test_Process_Invalid_1() {
		util.exec("Process_Invalid_1");
	}
	
	@Test
	public void test_Process_Invalid_2() {
		util.exec("Process_Invalid_2");
	}

	@Test
	public void test_Process_Invalid_3() {
		util.exec("Process_Invalid_3");
	}
	
	@Test
	public void test_Quantifiers_Invalid_1() {
		util.exec("Quantifiers_Invalid_1");
	}
	
	@Test
	public void test_Quantifiers_Invalid_2() {
		util.exec("Quantifiers_Invalid_2");
	}
	
	@Test
	public void test_Quantifiers_Invalid_3() {
		util.exec("Quantifiers_Invalid_3");
	}
	
	@Test
	public void test_Quantifiers_Invalid_4() {
		util.exec("Quantifiers_Invalid_4");
	}
	
	@Test
	public void test_Quantifiers_Invalid_5() {
		util.exec("Quantifiers_Invalid_5");
	}

	@Test
	public void test_Quantifiers_Invalid_6() {
		util.exec("Quantifiers_Invalid_6");
	}
	
	@Test
	public void test_Quantifiers_Invalid_7() {
		util.exec("Quantifiers_Invalid_7");
	}
	
	@Test
	public void test_Quantifiers_Invalid_8() {
		util.exec("Quantifiers_Invalid_8");
	}
	
	@Test
	public void test_Rational_Invalid_1() {
		util.exec("Rational_Invalid_1");
	}
	
	@Test
	public void test_Rational_Invalid_2() {
		util.exec("Rational_Invalid_2");
	}
	
	@Test
	public void test_RealAdd_Invalid_1() {
		util.exec("RealAdd_Invalid_1");
	}

	@Test
	public void test_RealConvert_Invalid_1() {
		util.exec("RealConvert_Invalid_1");
	}

	@Test
	public void test_RealConvert_Invalid_2() {
		util.exec("RealConvert_Invalid_2");
	}
	
	@Test
	public void test_RealDiv_Invalid_1() {
		util.exec("RealDiv_Invalid_1");
	}

	@Test
	public void test_RealDiv_Invalid_2() {
		util.exec("RealDiv_Invalid_2");
	}	
	@Ignore("Internal Failure")
	@Test
	public void test_RealMul_Invalid_1() {
		util.exec("RealMul_Invalid_1");
	}
	
	@Test
	public void test_Record_Invalid_1() {
		util.exec("Record_Invalid_1");
	}

	@Test
	public void test_Record_Invalid_2() {
		util.exec("Record_Invalid_2");
	}

	@Test
	public void test_Record_Invalid_3() {
		util.exec("Record_Invalid_3");
	}

	@Ignore("unclassified")
	@Test
	public void test_RecursiveType_Invalid_1() {
		util.exec("RecursiveType_Invalid_1");
	}
	@Ignore("unclassified")
	@Test
	public void test_RecursiveType_Invalid_10() {
		util.exec("RecursiveType_Invalid_10");
	}
	@Ignore("unclassified")
	@Test
	public void test_RecursiveType_Invalid_2() {
		util.exec("RecursiveType_Invalid_2");
	}

	@Test
	public void test_RecursiveType_Invalid_3() {
		util.exec("RecursiveType_Invalid_3");
	}
	@Ignore("unclassified")
	@Test
	public void test_RecursiveType_Invalid_4() {
		util.exec("RecursiveType_Invalid_4");
	}

	@Test
	public void test_RecursiveType_Invalid_5() {
		util.exec("RecursiveType_Invalid_5");
	}
	
	@Ignore("unclassified")
	@Test
	public void test_RecursiveType_Invalid_7() {
		util.exec("RecursiveType_Invalid_7");
	}
	@Ignore("unclassified")
	@Test
	public void test_RecursiveType_Invalid_8() {
		util.exec("RecursiveType_Invalid_8");
	}
	@Ignore("unclassified")
	@Test
	public void test_RecursiveType_Invalid_9() {
		util.exec("RecursiveType_Invalid_9");
	}
	
	@Test
	public void test_Remainder_Invalid_1() {
		util.exec("Remainder_Invalid_1");
	}
	
	@Test
	public void test_Remainder_Invalid_2() {
		util.exec("Remainder_Invalid_2");
	}
	
	@Test
	public void test_Remainder_Invalid_3() {
		util.exec("Remainder_Invalid_3");
	}

	@Test
	public void test_Requires_Invalid_1() {
		util.exec("Requires_Invalid_1");
	}
	
	@Test
	public void test_Return_Invalid_1() {
		util.exec("Return_Invalid_1");
	}
	@Ignore("Missing Return_Invalid_10.sysout")
	@Test
	public void test_Return_Invalid_10() {
		util.exec("Return_Invalid_10");
	}
	@Ignore("Missing Return_Invalid_11.sysout")
	@Test
	public void test_Return_Invalid_11() {
		util.exec("Return_Invalid_11");
	}
	
	@Test
	public void test_Return_Invalid_2() {
		util.exec("Return_Invalid_2");
	}
	
	
	@Test
	public void test_Return_Invalid_3() {
		util.exec("Return_Invalid_3");
	}
	
	@Test
	public void test_Return_Invalid_4() {
		util.exec("Return_Invalid_4");
	}
	
	@Test
	public void test_Return_Invalid_5() {
		util.exec("Return_Invalid_5");
	}
	
	@Test
	public void test_Return_Invalid_6() {
		util.exec("Return_Invalid_6");
	}

	@Test
	public void test_Return_Invalid_7() {
		util.exec("Return_Invalid_7");
	}
	
	@Test
	public void test_Return_Invalid_8() {
		util.exec("Return_Invalid_8");
	}
	
	@Test
	public void test_Return_Invalid_9() {
		util.exec("Return_Invalid_9");
	}
	
	@Test
	public void test_SetAssign_Invalid_1() {
		util.exec("SetAssign_Invalid_1");
	}
	
	@Test
	public void test_SetComprehension_Invalid_1() {
		util.exec("SetComprehension_Invalid_1");
	}	

	@Test
	public void test_SetComprehension_Invalid_2() {
		util.exec("SetComprehension_Invalid_2");
	}

	@Test
	public void test_SetComprehension_Invalid_3() {
		util.exec("SetComprehension_Invalid_3");
	}

	@Test
	public void test_SetComprehension_Invalid_4() {
		util.exec("SetComprehension_Invalid_4");
	}

	@Test
	public void test_SetComprehension_Invalid_5() {
		util.exec("SetComprehension_Invalid_5");
	}
	
	@Test	
	public void test_SetComprehension_Invalid_6() {
		util.exec("SetComprehension_Invalid_6");
	}	

	@Test
	public void test_SetConversion_Invalid_1() {
		util.exec("SetConversion_Invalid_1");
	}
	

	@Test
	public void test_SetDefine_Invalid_1() {
		util.exec("SetDefine_Invalid_1");
	}

	@Test
	public void test_SetDefine_Invalid_2() {
		util.exec("SetDefine_Invalid_2");
	}

	@Test
	public void test_SetElemOf_Invalid_1() {
		util.exec("SetElemOf_Invalid_1");
	}

	@Test
	public void test_SetEmpty_Invalid_1() {
		util.exec("SetEmpty_Invalid_1");
	}
	
	@Test
	public void test_SetEmpty_Invalid_2() {
		util.exec("SetEmpty_Invalid_2");
	}

	@Test
	public void test_SetIntersect_Invalid_1() {
		util.exec("SetIntersect_Invalid_1");
	}
	@Ignore("Missing SetIntersect_Invalid_2.sysout")
	@Test
	public void test_SetIntersect_Invalid_2() {
		util.exec("SetIntersect_Invalid_2");
	}
	@Ignore("Missing SetIntersection_Invalid_1.sysout")
	@Test
	public void test_SetIntersection_Invalid_1() {
		util.exec("SetIntersection_Invalid_1");
	}

	@Test
	public void test_SetIntersection_Invalid_2() {
		util.exec("SetIntersection_Invalid_2");
	}
	
	@Test
	public void test_SetSubset_Invalid_1() {
		util.exec("SetSubset_Invalid_1");
	}
	@Ignore("Missing SetSubset_Invalid_10.sysout")
	@Test
	public void test_SetSubset_Invalid_10() {
		util.exec("SetSubset_Invalid_10");
	}
	@Ignore("Missing SetSubset_Invalid_11.sysout")
	@Test
	public void test_SetSubset_Invalid_11() {
		util.exec("SetSubset_Invalid_11");
	}
	@Ignore("Missing SetSubset_Invalid_2.sysout")
	@Test
	public void test_SetSubset_Invalid_2() {
		util.exec("SetSubset_Invalid_2");
	}

	@Test
	public void test_SetSubset_Invalid_3() {
		util.exec("SetSubset_Invalid_3");
	}
	@Ignore("Missing SetSubset_Invalid_4.sysout")
	@Test
	public void test_SetSubset_Invalid_4() {
		util.exec("SetSubset_Invalid_4");
	}

	@Test
	public void test_SetSubset_Invalid_5() {
		util.exec("SetSubset_Invalid_5");
	}

	@Test
	public void test_SetSubset_Invalid_6() {
		util.exec("SetSubset_Invalid_6");
	}

	@Test
	public void test_SetSubset_Invalid_7() {
		util.exec("SetSubset_Invalid_7");
	}

	@Test
	public void test_SetSubset_Invalid_8() {
		util.exec("SetSubset_Invalid_8");
	}

	@Test
	public void test_SetSubset_Invalid_9() {
		util.exec("SetSubset_Invalid_9");
	}

	@Test
	public void test_SetUnion_Invalid_1() {
		util.exec("SetUnion_Invalid_1");
	}	

	@Test
	public void test_SetUnion_Invalid_2() {
		util.exec("SetUnion_Invalid_2");
	}

	@Test
	public void test_SetUnion_Invalid_3() {
		util.exec("SetUnion_Invalid_3");
	}

	@Test
	public void test_SetUnion_Invalid_4() {
		util.exec("SetUnion_Invalid_4");
	}
	
	@Test
	public void test_SingleLineComment_Valid_1() {
		util.exec("SingleLineComment_Valid_1");
	}
	
	@Test
	public void test_String_Invalid_3() {
		util.exec("String_Invalid_3");
	}
	
	@Test
	public void test_Subtype_Invalid_1() {
		util.exec("Subtype_Invalid_1");
	}	

	@Test
	public void test_Subtype_Invalid_2() {
		util.exec("Subtype_Invalid_2");
	}

	@Test
	public void test_Subtype_Invalid_3() {
		util.exec("Subtype_Invalid_3");
	}

	@Test
	public void test_Subtype_Invalid_4() {
		util.exec("Subtype_Invalid_4");
	}

	@Test
	public void test_Subtype_Invalid_5() {
		util.exec("Subtype_Invalid_5");
	}

	@Test
	public void test_Subtype_Invalid_6() {
		util.exec("Subtype_Invalid_6");
	}

	@Test
	public void test_Subtype_Invalid_7() {
		util.exec("Subtype_Invalid_7");
	}

	@Test
	public void test_Subtype_Invalid_8() {
		util.exec("Subtype_Invalid_8");
	}

	@Test
	public void test_Subtype_Invalid_9() {
		util.exec("Subtype_Invalid_9");
	}

	@Test
	public void test_Switch_Invalid_1() {
		util.exec("Switch_Invalid_1");
	}

	@Test
	public void test_Switch_Invalid_2() {
		util.exec("Switch_Invalid_2");
	}

	@Test
	public void test_Switch_Invalid_3() {
		util.exec("Switch_Invalid_3");
	}

	@Test
	public void test_Switch_Invalid_4() {
		util.exec("Switch_Invalid_4");
	}

	@Test
	public void test_Switch_Invalid_5() {
		util.exec("Switch_Invalid_5");
	}
	@Ignore("Missing Switch_Invalid_6.sysout")
	@Test
	public void test_Switch_Invalid_6() {
		util.exec("Switch_Invalid_6");
	}

	@Test
	public void test_Switch_Invalid_7() {
		util.exec("Switch_Invalid_7");
	}
	
	@Test
	public void test_Throws_Invalid_1() {
		util.exec("Throws_Invalid_1");
	}

	@Test
	public void test_Throws_Invalid_2() {
		util.exec("Throws_Invalid_2");
	}
	@Test
	public void test_TryCatch_Invalid_1() {
		util.exec("TryCatch_Invalid_1");
	}

	@Test
	public void test_TryCatch_Invalid_2() {
		util.exec("TryCatch_Invalid_2");
	}

	@Test
	public void test_TryCatch_Invalid_3() {
		util.exec("TryCatch_Invalid_3");
	}

	@Test
	public void test_TryCatch_Invalid_4() {
		util.exec("TryCatch_Invalid_4");
	}

	@Test
	public void test_TryCatch_Invalid_5() {
		util.exec("TryCatch_Invalid_5");
	}

	@Ignore("Missing TupleAssgn_Invalid_1")
	@Test
	public void test_TupleAssgn_Invalid_1() {
		util.exec("TupleAssgn_Invalid_1");
	}
	@Ignore("Missing TupleAssgn_Invalid_2")
	@Test
	public void test_TupleAssgn_Invalid_2() {
		util.exec("TupleAssgn_Invalid_2");
	}
	@Ignore("Missing TupleAssgn_Invalid_3")
	@Test
	public void test_TupleAssgn_Invalid_3() {
		util.exec("TupleAssgn_Invalid_3");
	}
	
	@Test
	public void test_TupleDefine_Invalid_1() {
		util.exec("TupleDefine_Invalid_1");
	}

	@Test
	public void test_TupleDefine_Invalid_2() {
		util.exec("TupleDefine_Invalid_2");
	}

	@Test
	public void test_Tuple_Invalid_1() {
		util.exec("Tuple_Invalid_1");
	}
	@Ignore("Missing Tuple_Invalid_2")
	@Test
	public void test_Tuple_Invalid_2() {
		util.exec("Tuple_Invalid_2");
	}

	@Test
	public void test_Tuple_Invalid_3() {
		util.exec("Tuple_Invalid_3");
	}

	@Test
	public void test_Tuple_Invalid_4() {
		util.exec("Tuple_Invalid_4");
	}

	@Test
	public void test_Tuple_Invalid_5() {
		util.exec("Tuple_Invalid_5");
	}

	@Test
	public void test_Tuple_Invalid_6() {
		util.exec("Tuple_Invalid_6");
	}

	@Test
	public void test_Tuple_Invalid_7() {
		util.exec("Tuple_Invalid_7");
	}
	@Ignore("Missing Tuple_Invalid_8")
	@Test
	public void test_Tuple_Invalid_8() {
		util.exec("Tuple_Invalid_8");
	}
	
	@Test
	public void test_TypeEquals_Invalid_1() {
		util.exec("TypeEquals_Invalid_1");
	}	
	
	@Test
	public void test_TypeEquals_Invalid_2() {
		util.exec("TypeEquals_Invalid_2");
	}
	
	@Ignore("Missing TypeEquals_Invalid_5.sysout")
	@Test
	public void test_TypeEquals_Invalid_5() {
		util.exec("TypeEquals_Invalid_5");
	}

	@Test
	public void test_TypeEquals_Invalid_6() {
		util.exec("TypeEquals_Invalid_6");
	}	
	
	@Test
	public void test_UnionType_Invalid_1() {
		util.exec("UnionType_Invalid_1");
	}
	@Ignore("Missing UnionType_Invalid_10")
	@Test
	public void test_UnionType_Invalid_10() {
		util.exec("UnionType_Invalid_10");
	}	

	@Test
	public void test_UnionType_Invalid_2() {
		util.exec("UnionType_Invalid_2");
	}	

	@Test
	public void test_UnionType_Invalid_3() {
		util.exec("UnionType_Invalid_3");
	}

	@Test
	public void test_UnionType_Invalid_4() {
		util.exec("UnionType_Invalid_4");
	}

	@Test
	public void test_UnionType_Invalid_5() {
		util.exec("UnionType_Invalid_5");
	}

	@Test
	public void test_UnionType_Invalid_6() {
		util.exec("UnionType_Invalid_6");
	}

	@Test
	public void test_UnionType_Invalid_7() {
		util.exec("UnionType_Invalid_7");
	}
	@Ignore("Missing UnionType_Invalid_8")
	@Test
	public void test_UnionType_Invalid_8() {
		util.exec("UnionType_Invalid_8");
	}
	@Ignore("Missing UnionType_Invalid_9")
	@Test
	public void test_UnionType_Invalid_9() {
		util.exec("UnionType_Invalid_9");
	}
	
	@Test
	public void test_VarDecl_Invalid_1() {
		util.exec("VarDecl_Invalid_1");
	}

	@Test
	public void test_VarDecl_Invalid_2() {
		util.exec("VarDecl_Invalid_2");
	}

	@Test
	public void test_VarDecl_Invalid_3() {
		util.exec("VarDecl_Invalid_3");
	}	

	
	@Ignore("Missing Void_Invalid_1.sysout")
	@Test
	public void test_Void_Invalid_1() {
		util.exec("Void_Invalid_1");
	}
	@Ignore("Missing Void_Invalid_2.sysout")
	@Test
	public void test_Void_Invalid_2() {
		util.exec("Void_Invalid_2");
	}
	@Ignore("Missing Void_Invalid_3.sysout")
	@Test
	public void test_Void_Invalid_3() {
		util.exec("Void_Invalid_3");
	}
	
	@Test
	public void test_While_Invalid_1() {
		util.exec("While_Invalid_1");
	}
	@Ignore("Missing While_Invalid_10.sysout")
	@Test
	public void test_While_Invalid_10() {
		util.exec("While_Invalid_10");
	}
	@Ignore("Missing While_Invalid_11.sysout")
	@Test
	public void test_While_Invalid_11() {
		util.exec("While_Invalid_11");
	}
	@Ignore("Missing While_Invalid_12.sysout")
	@Test
	public void test_While_Invalid_12() {
		util.exec("While_Invalid_12");
	}
	@Ignore("Missing While_Invalid_13.sysout")
	@Test
	public void test_While_Invalid_13() {
		util.exec("While_Invalid_13");
	}	

	@Test
	public void test_While_Invalid_2() {
		util.exec("While_Invalid_2");
	}
	
	@Test
	public void test_While_Invalid_3() {
		util.exec("While_Invalid_3");
	}
	
	@Test
	public void test_While_Invalid_4() {
		util.exec("While_Invalid_4");
	}
	@Test
	public void test_While_Invalid_5() {
		util.exec("While_Invalid_5");
	}
	@Test
	public void test_While_Invalid_6() {
		util.exec("While_Invalid_6");
	}
	@Test
	public void test_While_Invalid_7() {
		util.exec("While_Invalid_7");
	}

	@Test
	public void test_While_Invalid_8() {
		util.exec("While_Invalid_8");
	}
	
	@Test
	public void test_While_Invalid_9() {
		util.exec("While_Invalid_9");
	}


}
