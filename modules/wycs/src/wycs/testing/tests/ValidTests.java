package wycs.testing.tests;

import org.junit.Ignore;
import org.junit.Test;

import wycs.testing.TestHarness;

public class ValidTests extends TestHarness {
	public ValidTests() {
		 super("tests/valid");
	 }
	
	@Test public void Test_Arith_1() { verifyPassTest("test_arith_01"); }
	@Test public void Test_Arith_2() { verifyPassTest("test_arith_02"); }
	@Test public void Test_Arith_3() { verifyPassTest("test_arith_03"); }
	@Test public void Test_Arith_4() { verifyPassTest("test_arith_04"); }	
	@Test public void Test_Arith_5() { verifyPassTest("test_arith_05"); }	
	@Test public void Test_Arith_6() { verifyPassTest("test_arith_06"); }	
	@Ignore("Known Issue") @Test public void Test_Arith_7() { verifyPassTest("test_arith_07"); }
	@Test public void Test_Arith_8() { verifyPassTest("test_arith_08"); }
	@Test public void Test_Arith_9() { verifyPassTest("test_arith_09"); }
	@Test public void Test_Arith_10() { verifyPassTest("test_arith_10"); }
	@Test public void Test_Arith_11() { verifyPassTest("test_arith_11"); }
	@Ignore("Known Issue") @Test public void Test_Arith_12() { verifyPassTest("test_arith_12"); }
	@Test public void Test_Arith_13() { verifyPassTest("test_arith_13"); }
	@Test public void Test_Arith_14() { verifyPassTest("test_arith_14"); }
	@Ignore("Known Issue") @Test public void Test_Arith_15() { verifyPassTest("test_arith_15"); }
	@Test public void Test_Arith_16() { verifyPassTest("test_arith_16"); }
	@Test public void Test_Arith_17() { verifyPassTest("test_arith_17"); }
	@Ignore("Known Issue") @Test public void Test_Arith_18() { verifyPassTest("test_arith_18"); }
	@Test public void Test_Arith_19() { verifyPassTest("test_arith_19"); }
	@Test public void Test_Arith_20() { verifyPassTest("test_arith_20"); }
	@Test public void Test_Arith_21() { verifyPassTest("test_arith_21"); }
	@Ignore("Set <=> Length") @Test public void Test_Arith_22() { verifyPassTest("test_arith_22"); }	
	@Test public void Test_Arith_23() { verifyPassTest("test_arith_23"); }
	@Test public void Test_Arith_24() { verifyPassTest("test_arith_24"); }
	@Test public void Test_Arith_25() { verifyPassTest("test_arith_25"); }
	@Test public void Test_Arith_26() { verifyPassTest("test_arith_26"); }
	@Test public void Test_Arith_27() { verifyPassTest("test_arith_27"); }
	@Ignore("Known Issue") @Test public void Test_Arith_28() { verifyPassTest("test_arith_28"); }
	
	@Test public void Test_Macro_1() { verifyPassTest("test_macro_01"); }	
	@Test public void Test_Macro_2() { verifyPassTest("test_macro_02"); }
	
	@Test public void Test_Bool_1() { verifyPassTest("test_bool_01"); }
	
	@Test public void Test_Fun_1() { verifyPassTest("test_fun_01"); }
	
	@Test public void Test_Set_1() { verifyPassTest("test_set_01"); }	
	@Test public void Test_Set_2() { verifyPassTest("test_set_02"); }
	@Ignore("Set <=> Length") @Test public void Test_Set_3() { verifyPassTest("test_set_03"); }
	@Test public void Test_Set_4() { verifyPassTest("test_set_04"); }
	@Ignore("SubsetEq <=> Length") @Test public void Test_Set_5() { verifyPassTest("test_set_05"); }	
	@Test public void Test_Set_6() { verifyPassTest("test_set_06"); }	
	@Ignore("SubsetEq <=> Length") @Test public void Test_Set_7() { verifyPassTest("test_set_07"); }	
	@Test public void Test_Set_8() { verifyPassTest("test_set_08"); }
	@Test public void Test_Set_9() { verifyPassTest("test_set_09"); }
	@Test public void Test_Set_10() { verifyPassTest("test_set_10"); }
	
	@Test public void Test_List_1() { verifyPassTest("test_list_01"); }
	@Test public void Test_List_2() { verifyPassTest("test_list_02"); }
	@Test public void Test_List_3() { verifyPassTest("test_list_03"); }
	
	@Ignore("SubsetEq <=> Length") @Test public void Test_Valid_058() { verifyPassTest("test_058"); }
	@Test public void Test_Valid_059() { verifyPassTest("test_059"); }
	@Test public void Test_Valid_060() { verifyPassTest("test_060"); }
	@Test public void Test_Valid_061() { verifyPassTest("test_061"); }
	@Ignore("SubsetEq <=> Length") @Test public void Test_Valid_062() { verifyPassTest("test_062"); }
	@Test public void Test_Valid_063() { verifyPassTest("test_063"); }
	@Test public void Test_Valid_064() { verifyPassTest("test_064"); }
	@Test public void Test_Valid_065() { verifyPassTest("test_065"); }
	@Ignore("Set <=> Length") @Test public void Test_Valid_066() { verifyPassTest("test_066"); }
	@Ignore("Nat <=> Length") @Test public void Test_Valid_067() { verifyPassTest("test_067"); }
	@Test public void Test_Valid_068() { verifyPassTest("test_068"); }	
	@Ignore("SubsetEq <=> Length") @Test public void Test_Valid_069() { verifyPassTest("test_069"); }
	@Ignore("SubsetEq <=> Length") @Test public void Test_Valid_070() { verifyPassTest("test_070"); }
	@Ignore("SubsetEq to Equals") @Test public void Test_Valid_071() { verifyPassTest("test_071"); }
	@Test public void Test_Valid_072() { verifyPassTest("test_072"); }
	@Test public void Test_Valid_073() { verifyPassTest("test_073"); }
	@Ignore("ListLength") @Test public void Test_Valid_074() { verifyPassTest("test_074"); }
	@Test public void Test_Valid_075() { verifyPassTest("test_075"); }
	@Ignore("ListAppend") @Test public void Test_Valid_076() { verifyPassTest("test_076"); }
	@Ignore("ListAppend") @Test public void Test_Valid_077() { verifyPassTest("test_077"); }
	@Test public void Test_Valid_078() { verifyPassTest("test_078"); }
	@Test public void Test_Valid_079() { verifyPassTest("test_079"); }
	
	@Test public void Test_Valid_100() { verifyPassTest("test_100"); }
	@Ignore("MaxSteps") @Test public void Test_Valid_101() { verifyPassTest("test_101"); }
	@Test public void Test_Valid_102() { verifyPassTest("test_102"); }
	@Ignore("Known Issue") @Test public void Test_Valid_103() { verifyPassTest("test_103"); }
	@Ignore("Infinite Loop?") @Test public void Test_Valid_104() { verifyPassTest("test_104"); }
	@Test public void Test_Valid_105() { verifyPassTest("test_105"); }
	@Ignore("Known Issue") @Test public void Test_Valid_106() { verifyPassTest("test_106"); }
	@Ignore("Known Issue") @Test public void Test_Valid_107() { verifyPassTest("test_107"); }
	@Test public void Test_Valid_108() { verifyPassTest("test_108"); }
	@Ignore("Infinite Loop?") @Test public void Test_Valid_109() { verifyPassTest("test_109"); }
	@Ignore("Known Issue") @Test public void Test_Valid_110() { verifyPassTest("test_110"); }
	@Ignore("Known Issue") @Test public void Test_Valid_111() { verifyPassTest("test_111"); }
	@Ignore("Known Issue") @Test public void Test_Valid_112() { verifyPassTest("test_112"); }
	@Ignore("Known Issue") @Test public void Test_Valid_113() { verifyPassTest("test_113"); }
	@Test public void Test_Valid_114() { verifyPassTest("test_114"); }
	@Ignore("Known Issue") @Test public void Test_Valid_115() { verifyPassTest("test_115"); }
	@Ignore("Known Issue") @Test public void Test_Valid_116() { verifyPassTest("test_116"); }
	@Ignore("Known Issue") @Test public void Test_Valid_117() { verifyPassTest("test_117"); }
	@Ignore("Known Issue") @Test public void Test_Valid_118() { verifyPassTest("test_118"); }
	@Ignore("Known Issue") @Test public void Test_Valid_119() { verifyPassTest("test_119"); }
	@Ignore("Known Issue") @Test public void Test_Valid_120() { verifyPassTest("test_120"); }
}
