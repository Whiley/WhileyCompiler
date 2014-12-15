package wyopcl.testing.interpreter;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

public class AllValidTestCase {
	@Rule
	public TestRule timeout = new Timeout(25000);
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
	public void test_Access_Valid_1() {
		util.exec("Access_Valid_1");
	}

	@Test
	public void test_Access_Valid_2() {
		util.exec("Access_Valid_2");
	}
	
	@Test
	public void test_Assume_Valid_1() {
		util.exec("Assume_Valid_1");
	}

	@Test
	public void test_Assume_Valid_2() {
		util.exec("Assume_Valid_2");
	}
	
	@Test
	public void test_BoolAssign_Valid_1() {
		util.exec("BoolAssign_Valid_1");
	}

	@Test
	public void test_BoolAssign_Valid_2() {
		util.exec("BoolAssign_Valid_2");
	}

	@Test
	public void test_BoolAssign_Valid_3() {
		util.exec("BoolAssign_Valid_3");
	}

	@Test
	public void test_BoolAssign_Valid_4() {
		util.exec("BoolAssign_Valid_4");
	}

	@Test
	public void test_BoolAssign_Valid_5() {
		util.exec("BoolAssign_Valid_5");
	}

	@Test
	public void test_BoolAssign_Valid_6() {
		util.exec("BoolAssign_Valid_6");
	}

	@Test
	public void test_BoolFun_Valid_1() {
		util.exec("BoolFun_Valid_1");
	}

	@Test
	public void test_BoolIfElse_Valid_1() {
		util.exec("BoolIfElse_Valid_1");
	}

	@Test
	public void test_BoolIfElse_Valid_2() {
		util.exec("BoolIfElse_Valid_2");
	}

	@Test
	public void test_BoolList_Valid_1() {
		util.exec("BoolList_Valid_1");
	}

	@Test
	public void test_BoolList_Valid_2() {
		util.exec("BoolList_Valid_2");
	}

	@Test
	public void test_BoolList_Valid_3() {
		util.exec("BoolList_Valid_3");
	}

	@Test
	public void test_BoolRecord_Valid_1() {
		util.exec("BoolRecord_Valid_1");
	}

	@Test
	public void test_BoolRecord_Valid_2() {
		util.exec("BoolRecord_Valid_2");
	}

	@Test
	public void test_BoolRequires_Valid_1() {
		util.exec("BoolRequires_Valid_1");
	}

	@Test
	public void test_BoolReturn_Valid_1() {
		util.exec("BoolReturn_Valid_1");
	}
	
	@Test
	public void test_Byte_Valid_1() {
		util.exec("Byte_Valid_1");
	}

	@Test
	public void test_Byte_Valid_2() {
		util.exec("Byte_Valid_2");
	}

	@Test
	public void test_Byte_Valid_3() {
		util.exec("Byte_Valid_3");
	}

	@Test
	public void test_Byte_Valid_4() {
		util.exec("Byte_Valid_4");
	}

	@Test
	public void test_Byte_Valid_5() {
		util.exec("Byte_Valid_5");
	}

	@Test
	public void test_Byte_Valid_6() {
		util.exec("Byte_Valid_6");
	}

	@Test
	public void test_Byte_Valid_7() {
		util.exec("Byte_Valid_7");
	}

	@Test
	public void test_Byte_Valid_8() {
		util.exec("Byte_Valid_8");
	}

	@Test
	public void test_Byte_Valid_9() {
		util.exec("Byte_Valid_9");
	}
	
	@Test
	public void test_Cast_Valid_1() {
		util.exec("Cast_Valid_1");
	}

	@Test
	public void test_Cast_Valid_2() {
		util.exec("Cast_Valid_2");
	}

	@Test
	public void test_Cast_Valid_3() {
		util.exec("Cast_Valid_3");
	}

	@Test
	public void test_Cast_Valid_4() {
		util.exec("Cast_Valid_4");
	}

	@Test
	public void test_Cast_Valid_5() {
		util.exec("Cast_Valid_5");
	}

	@Test
	public void test_Cast_Valid_6() {
		util.exec("Cast_Valid_6");
	}

	@Test
	public void test_Char_Valid_1() {
		util.exec("Char_Valid_1");
	}

	@Test
	public void test_Char_Valid_2() {
		util.exec("Char_Valid_2");
	}

	@Test
	public void test_Char_Valid_3() {
		util.exec("Char_Valid_3");
	}

	@Test
	public void test_Char_Valid_4() {
		util.exec("Char_Valid_4");
	}

	@Test
	public void test_Char_Valid_5() {
		util.exec("Char_Valid_5");
	}

	@Test
	public void test_Char_Valid_6() {
		util.exec("Char_Valid_6");
	}

	@Test
	public void test_Char_Valid_7() {
		util.exec("Char_Valid_7");
	}
	
	@Test
	public void test_Coercion_Valid_1() {
		util.exec("Coercion_Valid_1");
	}

	@Test
	public void test_Coercion_Valid_2() {
		util.exec("Coercion_Valid_2");
	}

	@Test
	public void test_Coercion_Valid_3() {
		util.exec("Coercion_Valid_3");
	}

	@Test
	public void test_Coercion_Valid_4() {
		util.exec("Coercion_Valid_4");
	}

	@Test
	public void test_Coercion_Valid_6() {
		util.exec("Coercion_Valid_6");
	}

	@Test
	public void test_Coercion_Valid_7() {
		util.exec("Coercion_Valid_7");
	}

	/**
	.\Coercion_Valid_8.whiley:7: expected type real, found int
        return |x|
               ^^^
wycc.lang.SyntaxError: expected type real, found int
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.builder.FlowTypeChecker.checkIsSubtype(FlowTypeChecker.java:4060)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:799)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:327)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:766)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Test
	@Ignore("Issue #406\n"
			+ ".\\Coercion_Valid_8.whiley:7: expected type real, found int\r\n" + 
			"        return |x|\r\n" + 
			"               ^^^\r\n" + 
			"wycc.lang.SyntaxError: expected type real, found int\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)")
	public void test_Coercion_Valid_8() {
		util.exec("Coercion_Valid_8");
	}
	
	@Test
	public void test_Coercion_Valid_9() {
		util.exec("Coercion_Valid_9");
	}
	
	@Test
	public void test_Complex_Valid_1() {
		util.exec("Complex_Valid_1");
	}

	@Test
	public void test_Complex_Valid_2() {
		util.exec("Complex_Valid_2");
	}
	/**
	 * .\Complex_Valid_3.whiley:9: record required, got: X<null|{int item,X left,X right}>
    (tree != null && tree.left != null ==> tree.left.item < tree.item) &&
                                           ^^^^^^^^^^^^^^
wycc.lang.SyntaxError: record required, got: X<null|{int item,X left,X right}>
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2327)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1674)
	at wyc.builder.FlowTypeChecker.resolveLeafCondition(FlowTypeChecker.java:1458)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1356)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.resolveNonLeafCondition(FlowTypeChecker.java:1422)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1345)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.resolveNonLeafCondition(FlowTypeChecker.java:1398)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1345)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.resolveNonLeafCondition(FlowTypeChecker.java:1396)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1345)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1750)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1660)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:196)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:147)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Test
	@Ignore("Issue ???\n"
			+ ".\\Complex_Valid_3.whiley:9: record required, got: X<null|{int item,X left,X right}>\r\n" + 
			"    (tree != null && tree.left != null ==> tree.left.item < tree.item) &&\r\n" + 
			"                                           ^^^^^^^^^^^^^^\r\n" + 
			"wycc.lang.SyntaxError: record required, got: X<null|{int item,X left,X right}>\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)")
	public void test_Complex_Valid_3() {
		util.exec("Complex_Valid_3");
	}

	@Test
	public void test_Complex_Valid_4() {
		util.exec("Complex_Valid_4");
	}

	@Test
	public void test_Complex_Valid_5() {
		util.exec("Complex_Valid_5");
	}

	@Test
	public void test_Complex_Valid_6() {
		util.exec("Complex_Valid_6");
	}
	
	
	@Test
	public void test_Complex_Valid_7() {
		util.exec("Complex_Valid_7");
	}

	@Test
	public void test_Complex_Valid_8() {
		util.exec("Complex_Valid_8");
	}
	
	/**
	Parsed 1 source file(s). ....................................................... [60ms+1mb]
.\Constant_Valid_1.whiley:5: internal failure
constant CONSTANT is {V1 is TYPE, V2 is TYPE}
                            ^^^^
wycc.lang.SyntaxError$InternalFailure: internal failure
	at wycc.lang.SyntaxError.internalFailure(SyntaxError.java:312)
	at wyc.lang.WhileyFile.internalFailure(WhileyFile.java:635)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3190)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3078)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3023)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2382)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1712)
	at wyc.builder.FlowTypeChecker.resolveLeafCondition(FlowTypeChecker.java:1459)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1356)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1750)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1660)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2199)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1698)
	at wyc.builder.FlowTypeChecker.resolveAsConstant(FlowTypeChecker.java:3469)
	at wyc.builder.FlowTypeChecker.resolveAsConstant(FlowTypeChecker.java:3407)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3282)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3182)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3116)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3325)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3182)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3078)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3024)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2382)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1712)
	at wyc.builder.FlowTypeChecker.resolveLeafCondition(FlowTypeChecker.java:1459)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1356)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1750)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1660)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2199)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1698)
	at wyc.builder.FlowTypeChecker.resolveAsConstant(FlowTypeChecker.java:3469)
	at wyc.builder.FlowTypeChecker.resolveAsConstant(FlowTypeChecker.java:3407)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3282)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3182)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3116)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3325)
	at wyc.builder.FlowTypeC
	*/
	@Ignore("Issue #311\n"
			+ ".\\Constant_Valid_1.whiley:5: internal failure\r\n" + 
			"constant CONSTANT is {V1 is TYPE, V2 is TYPE}\r\n" + 
			"                            ^^^^\r\n" + 
			"wycc.lang.SyntaxError$InternalFailure: internal failure\r\n" + 
			"	at wycc.lang.SyntaxError.internalFailure(SyntaxError.java:312)\r\n" + 
			"	at wyc.lang.WhileyFile.internalFailure(WhileyFile.java:635)")
	@Test
	public void test_Constant_Valid_1() {
		util.exec("Constant_Valid_1");
	}

	/**
	 .\Constant_Valid_2.whiley:4: type not found: Constant_Valid_2:ZLIB
type CompressionMethod is {ZLIB}
                           ^^^^
wycc.lang.SyntaxError: type not found: Constant_Valid_2:ZLIB
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:253)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:625)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3185)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3123)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3078)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3024)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:179)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:147)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
Caused by: wycc.util.ResolveError: type not found: Constant_Valid_2:ZLIB
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3296)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3182)
	... 13 more

	 */
	@Ignore("Issue #311\n"
			+ ".\\Constant_Valid_2.whiley:4: type not found: Constant_Valid_2:ZLIB\r\n" + 
			"type CompressionMethod is {ZLIB}\r\n" + 
			"                           ^^^^\r\n" + 
			"wycc.lang.SyntaxError: type not found: Constant_Valid_2:ZLIB\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:253)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:625)")
	@Test
	public void test_Constant_Valid_2() {
		util.exec("Constant_Valid_2");
	}

	@Test
	public void test_Constant_Valid_3() {
		util.exec("Constant_Valid_3");
	}
	
	@Test
	public void test_ConstrainedDictionary_Valid_1() {
		util.exec("ConstrainedDictionary_Valid_1");
	}

	@Test
	public void test_ConstrainedInt_Valid_1() {
		util.exec("ConstrainedInt_Valid_1");
	}

	@Test
	public void test_ConstrainedInt_Valid_10() {
		util.exec("ConstrainedInt_Valid_10");
	}

	@Test
	public void test_ConstrainedInt_Valid_12() {
		util.exec("ConstrainedInt_Valid_12");
	}

	@Test
	public void test_ConstrainedInt_Valid_13() {
		util.exec("ConstrainedInt_Valid_13");
	}

	@Test
	public void test_ConstrainedInt_Valid_14() {
		util.exec("ConstrainedInt_Valid_14");
	}

	@Test
	public void test_ConstrainedInt_Valid_15() {
		util.exec("ConstrainedInt_Valid_15");
	}

	@Test
	public void test_ConstrainedInt_Valid_16() {
		util.exec("ConstrainedInt_Valid_16");
	}

	@Test
	public void test_ConstrainedInt_Valid_17() {
		util.exec("ConstrainedInt_Valid_17");
	}

	@Test
	public void test_ConstrainedInt_Valid_18() {
		util.exec("ConstrainedInt_Valid_18");
	}

	@Test
	public void test_ConstrainedInt_Valid_19() {
		util.exec("ConstrainedInt_Valid_19");
	}

	@Test
	public void test_ConstrainedInt_Valid_2() {
		util.exec("ConstrainedInt_Valid_2");
	}

	@Test
	public void test_ConstrainedInt_Valid_20() {
		util.exec("ConstrainedInt_Valid_20");
	}

	@Test
	public void test_ConstrainedInt_Valid_21() {
		util.exec("ConstrainedInt_Valid_21");
	}

	@Test
	public void test_ConstrainedInt_Valid_22() {
		util.exec("ConstrainedInt_Valid_22");
	}

	@Test
	public void test_ConstrainedInt_Valid_23() {
		util.exec("ConstrainedInt_Valid_23");
	}

	@Test
	public void test_ConstrainedInt_Valid_3() {
		util.exec("ConstrainedInt_Valid_3");
	}

	@Test
	public void test_ConstrainedInt_Valid_4() {
		util.exec("ConstrainedInt_Valid_4");
	}

	@Test
	public void test_ConstrainedInt_Valid_5() {
		util.exec("ConstrainedInt_Valid_5");
	}

	@Test
	public void test_ConstrainedInt_Valid_6() {
		util.exec("ConstrainedInt_Valid_6");
	}

	@Test
	public void test_ConstrainedInt_Valid_8() {
		util.exec("ConstrainedInt_Valid_8");
	}

	@Test
	public void test_ConstrainedList_Valid_1() {
		util.exec("ConstrainedList_Valid_1");
	}

	@Test
	public void test_ConstrainedList_Valid_11() {
		util.exec("ConstrainedList_Valid_11");
	}

	@Test
	public void test_ConstrainedList_Valid_12() {
		util.exec("ConstrainedList_Valid_12");
	}

	@Test
	public void test_ConstrainedList_Valid_14() {
		util.exec("ConstrainedList_Valid_14");
	}

	@Test
	public void test_ConstrainedList_Valid_15() {
		util.exec("ConstrainedList_Valid_15");
	}

	@Test
	public void test_ConstrainedList_Valid_16() {
		util.exec("ConstrainedList_Valid_16");
	}

	@Test
	public void test_ConstrainedList_Valid_17() {
		util.exec("ConstrainedList_Valid_17");
	}

	@Test
	public void test_ConstrainedList_Valid_18() {
		util.exec("ConstrainedList_Valid_18");
	}

	@Test
	public void test_ConstrainedList_Valid_19() {
		util.exec("ConstrainedList_Valid_19");
	}

	@Test
	public void test_ConstrainedList_Valid_2() {
		util.exec("ConstrainedList_Valid_2");
	}

	@Test
	public void test_ConstrainedList_Valid_20() {
		util.exec("ConstrainedList_Valid_20");
	}

	@Test
	public void test_ConstrainedList_Valid_21() {
		util.exec("ConstrainedList_Valid_21");
	}

	@Test
	public void test_ConstrainedList_Valid_22() {
		util.exec("ConstrainedList_Valid_22");
	}
	
	/**
	 * The expected result is different from executed one.
	 * Expected: :[[19, 43], [22, 50]]
	 * Actual: :[[19, [22], [43], 50]] 
	 */
	@Test
	public void test_ConstrainedList_Valid_23() {
		util.exec("ConstrainedList_Valid_23");
	}

	@Test
	public void test_ConstrainedList_Valid_24() {
		util.exec("ConstrainedList_Valid_24");
	}

	@Test
	public void test_ConstrainedList_Valid_25() {
		util.exec("ConstrainedList_Valid_25");
	}

	@Test
	public void test_ConstrainedList_Valid_26() {
		util.exec("ConstrainedList_Valid_26");
	}

	@Test
	public void test_ConstrainedList_Valid_3() {
		util.exec("ConstrainedList_Valid_3");
	}

	@Test
	public void test_ConstrainedList_Valid_4() {
		util.exec("ConstrainedList_Valid_4");
	}

	@Test
	public void test_ConstrainedList_Valid_5() {
		util.exec("ConstrainedList_Valid_5");
	}

	@Test
	public void test_ConstrainedList_Valid_6() {
		util.exec("ConstrainedList_Valid_6");
	}

	@Test
	public void test_ConstrainedList_Valid_7() {
		util.exec("ConstrainedList_Valid_7");
	}

	@Test
	public void test_ConstrainedList_Valid_8() {
		util.exec("ConstrainedList_Valid_8");
	}

	@Test
	public void test_ConstrainedList_Valid_9() {
		util.exec("ConstrainedList_Valid_9");
	}

	/**
	.\ConstrainedNegation_Valid_1.whiley:9: incomparable operands: !int and int
    if x is int:
       ^^^^^^^^
wycc.lang.SyntaxError: incomparable operands: !int and int
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)
	at wyc.builder.FlowTypeChecker.resolveLeafCondition(FlowTypeChecker.java:1510)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1356)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:755)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Test
	@Ignore("Issue ???\n"
			+ ".\\ConstrainedNegation_Valid_1.whiley:9: incomparable operands: !int and int\r\n" + 
			"    if x is int:\r\n" + 
			"       ^^^^^^^^\r\n" + 
			"wycc.lang.SyntaxError: incomparable operands: !int and int\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)"
			)
	public void test_ConstrainedNegation_Valid_1() {
		util.exec("ConstrainedNegation_Valid_1");
	}

	@Test
	public void test_ConstrainedRecord_Valid_1() {
		util.exec("ConstrainedRecord_Valid_1");
	}

	@Test
	public void test_ConstrainedRecord_Valid_2() {
		util.exec("ConstrainedRecord_Valid_2");
	}

	@Test
	public void test_ConstrainedRecord_Valid_3() {
		util.exec("ConstrainedRecord_Valid_3");
	}

	@Test
	public void test_ConstrainedRecord_Valid_4() {
		util.exec("ConstrainedRecord_Valid_4");
	}

	@Test
	public void test_ConstrainedRecord_Valid_5() {
		util.exec("ConstrainedRecord_Valid_5");
	}

	@Test
	public void test_ConstrainedRecord_Valid_6() {
		util.exec("ConstrainedRecord_Valid_6");
	}

	@Test
	public void test_ConstrainedRecord_Valid_8() {
		util.exec("ConstrainedRecord_Valid_8");
	}

	@Test
	public void test_ConstrainedRecord_Valid_9() {
		util.exec("ConstrainedRecord_Valid_9");
	}

	@Test
	public void test_ConstrainedSet_Valid_1() {
		util.exec("ConstrainedSet_Valid_1");
	}

	@Test
	public void test_ConstrainedSet_Valid_2() {
		util.exec("ConstrainedSet_Valid_2");
	}

	@Test
	public void test_ConstrainedSet_Valid_3() {
		util.exec("ConstrainedSet_Valid_3");
	}

	@Test
	public void test_ConstrainedSet_Valid_4() {
		util.exec("ConstrainedSet_Valid_4");
	}

	@Test
	public void test_ConstrainedSet_Valid_5() {
		util.exec("ConstrainedSet_Valid_5");
	}

	@Test
	public void test_ConstrainedSet_Valid_6() {
		util.exec("ConstrainedSet_Valid_6");
	}

	@Test
	public void test_ConstrainedSet_Valid_7() {
		util.exec("ConstrainedSet_Valid_7");
	}

	@Test
	public void test_ConstrainedSet_Valid_8() {
		util.exec("ConstrainedSet_Valid_8");
	}

	@Test
	public void test_ConstrainedTuple_Valid_1() {
		util.exec("ConstrainedTuple_Valid_1");
	}
	
	/**
	.\Contractive_Valid_1.whiley:5: contractive type encountered
function f(Contractive x) => Contractive:
           ^^^^^^^^^^^
wycc.lang.SyntaxError: contractive type encountered
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:2972)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:256)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Ignore("Issue ???\n"
			+ ".\\Contractive_Valid_1.whiley:5: contractive type encountered\r\n" + 
			"function f(Contractive x) => Contractive:\r\n" + 
			"           ^^^^^^^^^^^\r\n" + 
			"wycc.lang.SyntaxError: contractive type encountered\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)")
	@Test
	public void test_Contractive_Valid_1() {
		util.exec("Contractive_Valid_1");
	}

	@Test
	public void test_Contractive_Valid_2() {
		util.exec("Contractive_Valid_2");
	}
	
	@Test
	public void test_DecimalAssignment_Valid_1() {
		util.exec("DecimalAssignment_Valid_1");
	}
	
	@Test
	public void test_Define_Valid_1() {
		util.exec("Define_Valid_1");
	}

	@Test
	public void test_Define_Valid_2() {
		util.exec("Define_Valid_2");
	}

	@Test
	public void test_Define_Valid_3() {
		util.exec("Define_Valid_3");
	}

	@Test
	public void test_Define_Valid_4() {
		util.exec("Define_Valid_4");
	}

	@Test
	public void test_Dictionary_Valid_1() {
		util.exec("Dictionary_Valid_1");
	}

	@Test
	public void test_Dictionary_Valid_10() {
		util.exec("Dictionary_Valid_10");
	}

	@Test
	public void test_Dictionary_Valid_11() {
		util.exec("Dictionary_Valid_11");
	}

	@Test
	public void test_Dictionary_Valid_12() {
		util.exec("Dictionary_Valid_12");
	}

	@Test
	public void test_Dictionary_Valid_13() {
		util.exec("Dictionary_Valid_13");
	}

	@Test
	public void test_Dictionary_Valid_14() {
		util.exec("Dictionary_Valid_14");
	}

	@Test
	public void test_Dictionary_Valid_15() {
		util.exec("Dictionary_Valid_15");
	}

	@Test
	public void test_Dictionary_Valid_16() {
		util.exec("Dictionary_Valid_16");
	}

	@Test
	public void test_Dictionary_Valid_2() {
		util.exec("Dictionary_Valid_2");
	}

	@Test
	public void test_Dictionary_Valid_3() {
		util.exec("Dictionary_Valid_3");
	}

	@Test
	public void test_Dictionary_Valid_4() {
		util.exec("Dictionary_Valid_4");
	}

	@Test
	public void test_Dictionary_Valid_5() {
		util.exec("Dictionary_Valid_5");
	}

	@Test
	public void test_Dictionary_Valid_6() {
		util.exec("Dictionary_Valid_6");
	}

	@Test
	public void test_Dictionary_Valid_7() {
		util.exec("Dictionary_Valid_7");
	}

	@Test
	public void test_Dictionary_Valid_8() {
		util.exec("Dictionary_Valid_8");
	}

	@Test
	public void test_Dictionary_Valid_9() {
		util.exec("Dictionary_Valid_9");
	}
	
	@Test
	public void test_DoWhile_Valid_1() {
		util.exec("DoWhile_Valid_1");
	}

	@Test
	public void test_DoWhile_Valid_2() {
		util.exec("DoWhile_Valid_2");
	}

	@Test
	public void test_DoWhile_Valid_3() {
		util.exec("DoWhile_Valid_3");
	}
	
	@Test	
	public void test_DoWhile_Valid_4() {
		util.exec("DoWhile_Valid_4");
	}

	@Test
	public void test_DoWhile_Valid_5() {
		util.exec("DoWhile_Valid_5");
	}

	@Test
	public void test_EffectiveList_Valid_1() {
		util.exec("EffectiveList_Valid_1");
	}
	
	@Test
	public void test_Ensures_Valid_1() {
		util.exec("Ensures_Valid_1");
	}

	@Test
	public void test_Ensures_Valid_2() {
		util.exec("Ensures_Valid_2");
	}

	@Test
	public void test_Ensures_Valid_3() {
		util.exec("Ensures_Valid_3");
	}

	@Test
	public void test_Ensures_Valid_4() {
		util.exec("Ensures_Valid_4");
	}

	@Test
	public void test_Ensures_Valid_5() {
		util.exec("Ensures_Valid_5");
	}

	@Test
	public void test_Ensures_Valid_6() {
		util.exec("Ensures_Valid_6");
	}

	@Test
	public void test_Ensures_Valid_7() {
		util.exec("Ensures_Valid_7");
	}
	
	@Test
	public void test_For_Valid_1() {
		util.exec("For_Valid_1");
	}

	@Test
	public void test_For_Valid_10() {
		util.exec("For_Valid_10");
	}

	@Test
	public void test_For_Valid_11() {
		util.exec("For_Valid_11");
	}

	@Test
	public void test_For_Valid_12() {
		util.exec("For_Valid_12");
	}

	@Test
	public void test_For_Valid_13() {
		util.exec("For_Valid_13");
	}

	@Test
	public void test_For_Valid_14() {
		util.exec("For_Valid_14");
	}

	@Test
	public void test_For_Valid_15() {
		util.exec("For_Valid_15");
	}

	@Test
	public void test_For_Valid_16() {
		util.exec("For_Valid_16");
	}

	@Test
	public void test_For_Valid_17() {
		util.exec("For_Valid_17");
	}

	@Test
	public void test_For_Valid_18() {
		util.exec("For_Valid_18");
	}

	@Test
	public void test_For_Valid_2() {
		util.exec("For_Valid_2");
	}

	@Test
	public void test_For_Valid_3() {
		util.exec("For_Valid_3");
	}

	@Test
	public void test_For_Valid_4() {
		util.exec("For_Valid_4");
	}

	/**
	 * .\For_Valid_5.whiley:4: variable may be uninitialised for i, r in xs:
	 * ^^^^^^^^^^^^^^^ wycc.lang.SyntaxError: variable may be uninitialised at
	 * wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:675) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:333) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135) at
	 * wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181) at
	 * wybs.util.StdBuildRule.apply(StdBuildRule.java:109) at
	 * wybs.util.StdProject.build(StdProject.java:256) at
	 * wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503) at
	 * wyc.util.WycBuildTask.build(WycBuildTask.java:471) at
	 * wyopcl.WyopclMain.run(WyopclMain.java:119) at
	 * wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Ignore("Issue ???\n"
			+ ".\\For_Valid_5.whiley:4: variable may be uninitialised for i, r in xs:\r\n" + 
			"	  ^^^^^^^^^^^^^^^ wycc.lang.SyntaxError: variable may be uninitialised at\r\n" + 
			"	  wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238) at")
	@Test
	public void test_For_Valid_5() {
		util.exec("For_Valid_5");
	}
/**
 * 
 [.\For_Valid_6.whiley] failed on definite assignment check (internal failure, internal failure)  [0ms]
.\For_Valid_6.whiley:9: internal failure, internal failure
    for x in xs:
    ^^^^^^^^^^^^
wycc.lang.SyntaxError$InternalFailure: internal failure, internal failure
	at wycc.lang.SyntaxError.internalFailure(SyntaxError.java:312)
	at wyil.util.dfa.ForwardFlowAnalysis.propagate(ForwardFlowAnalysis.java:208)
	at wyil.util.dfa.ForwardFlowAnalysis.propagate(ForwardFlowAnalysis.java:94)
	at wyil.util.dfa.ForwardFlowAnalysis.propagate(ForwardFlowAnalysis.java:83)
	at wyil.util.dfa.ForwardFlowAnalysis.apply(ForwardFlowAnalysis.java:64)
	at wyil.checks.DefiniteAssignmentCheck.apply(DefiniteAssignmentCheck.java:1)
	at wyc.builder.WhileyBuilder.process(WhileyBuilder.java:360)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:224)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
Caused by: java.lang.NullPointerException
	at wyil.checks.DefiniteAssignmentCheck.join(DefiniteAssignmentCheck.java:164)
	at wyil.checks.DefiniteAssignmentCheck.propagate(DefiniteAssignmentCheck.java:157)
	at wyil.checks.DefiniteAssignmentCheck.propagate(DefiniteAssignmentCheck.java:1)
	at wyil.util.dfa.ForwardFlowAnalysis.propagate(ForwardFlowAnalysis.java:138)
	... 12 more
 */
	@Ignore("Issue ???"
			+ ".\\For_Valid_6.whiley:9: internal failure, internal failure\r\n" + 
			"    for x in xs:\r\n" + 
			"    ^^^^^^^^^^^^\r\n" + 
			"wycc.lang.SyntaxError$InternalFailure: internal failure, internal failure\r\n" + 
			"	at wycc.lang.SyntaxError.internalFailure(SyntaxError.java:312)")
	@Test
	public void test_For_Valid_6() {
		util.exec("For_Valid_6");
	}

	@Test
	public void test_For_Valid_7() {
		util.exec("For_Valid_7");
	}

	@Test
	public void test_For_Valid_8() {
		util.exec("For_Valid_8");
	}

	@Test
	public void test_For_Valid_9() {
		util.exec("For_Valid_9");
	}
	@Test
	public void test_FunctionRef_Valid_1() {
		util.exec("FunctionRef_Valid_1");
	}

	/**
	 * Parsed 1 source file(s).
	 * ....................................................... [62ms+1mb]
	 * .\FunctionRef_Valid_2.whiley:4: incomparable operands: real and int
	 * return x + 1 ^^^^^ wycc.lang.SyntaxError: incomparable operands: real and
	 * int at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1889) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1660) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:797) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:327) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145) at
	 * wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135) at
	 * wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181) at
	 * wybs.util.StdBuildRule.apply(StdBuildRule.java:109) at
	 * wybs.util.StdProject.build(StdProject.java:256) at
	 * wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503) at
	 * wyc.util.WycBuildTask.build(WycBuildTask.java:471) at
	 * wyopcl.WyopclMain.run(WyopclMain.java:119) at
	 * wyopcl.WyopclMain.main(WyopclMain.java:148)
	 **/
	@Ignore("Issue ???\n"
			+ ".\\FunctionRef_Valid_2.whiley:4: incomparable operands: real and int\r\n" + 
			"	  return x + 1 ^^^^^ wycc.lang.SyntaxError: incomparable operands: real and\r\n" + 
			"	  int at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238) at")
	@Test
	public void test_FunctionRef_Valid_2() {
		util.exec("FunctionRef_Valid_2");
	}

	@Test
	public void test_FunctionRef_Valid_3() {
		util.exec("FunctionRef_Valid_3");
	}

	@Test
	public void test_FunctionRef_Valid_4() {
		util.exec("FunctionRef_Valid_4");
	}

	@Test
	public void test_FunctionRef_Valid_5() {
		util.exec("FunctionRef_Valid_5");
	}

	@Test
	public void test_FunctionRef_Valid_6() {
		util.exec("FunctionRef_Valid_6");
	}

	@Test
	public void test_FunctionRef_Valid_7() {
		util.exec("FunctionRef_Valid_7");
	}

	@Test
	public void test_FunctionRef_Valid_8() {
		util.exec("FunctionRef_Valid_8");
	}

	@Test
	public void test_FunctionRef_Valid_9() {
		util.exec("FunctionRef_Valid_9");
	}

	@Test
	public void test_Function_Valid_1() {
		util.exec("Function_Valid_1");
	}

	@Test
	public void test_Function_Valid_10() {
		util.exec("Function_Valid_10");
	}

	@Test
	public void test_Function_Valid_12() {
		util.exec("Function_Valid_12");
	}

	@Test
	public void test_Function_Valid_13() {
		util.exec("Function_Valid_13");
	}

	@Test
	public void test_Function_Valid_14() {
		util.exec("Function_Valid_14");
	}

	@Test
	public void test_Function_Valid_16() {
		util.exec("Function_Valid_16");
	}

	@Test
	public void test_Function_Valid_17() {
		util.exec("Function_Valid_17");
	}

	@Test
	public void test_Function_Valid_18() {
		util.exec("Function_Valid_18");
	}

	@Test
	public void test_Function_Valid_19() {
		util.exec("Function_Valid_19");
	}

	@Test
	public void test_Function_Valid_2() {
		util.exec("Function_Valid_2");
	}

	@Test
	public void test_Function_Valid_20() {
		util.exec("Function_Valid_20");
	}

	@Test
	public void test_Function_Valid_21() {
		util.exec("Function_Valid_21");
	}

	@Test
	public void test_Function_Valid_3() {
		util.exec("Function_Valid_3");
	}

	@Test
	public void test_Function_Valid_4() {
		util.exec("Function_Valid_4");
	}

	@Test
	public void test_Function_Valid_5() {
		util.exec("Function_Valid_5");
	}

	@Test
	public void test_Function_Valid_6() {
		util.exec("Function_Valid_6");
	}

	@Test
	public void test_Function_Valid_7() {
		util.exec("Function_Valid_7");
	}

	@Test
	public void test_Function_Valid_8() {
		util.exec("Function_Valid_8");
	}

	@Test
	public void test_Function_Valid_9() {
		util.exec("Function_Valid_9");
	}

	@Test
	public void test_HexAssign_Valid_1() {
		util.exec("HexAssign_Valid_1");
	}

	@Test
	public void test_IfElse_Valid_1() {
		util.exec("IfElse_Valid_1");
	}

	@Test
	public void test_IfElse_Valid_2() {
		util.exec("IfElse_Valid_2");
	}

	@Test
	public void test_IfElse_Valid_3() {
		util.exec("IfElse_Valid_3");
	}

	@Test
	public void test_IfElse_Valid_4() {
		util.exec("IfElse_Valid_4");
	}

	@Test
	public void test_IfElse_Valid_5() {
		util.exec("IfElse_Valid_5");
	}
	
	@Test
	public void test_Import_Valid_1() {
		util.exec("Import_Valid_1");
	}

	@Test
	public void test_Import_Valid_2() {
		util.exec("Import_Valid_2");
	}

	@Test
	public void test_Import_Valid_3() {
		util.exec("Import_Valid_3");
	}

	@Test
	public void test_Import_Valid_4() {
		util.exec("Import_Valid_4");
	}

	@Test
	public void test_Import_Valid_5() {
		util.exec("Import_Valid_5");
	}

	@Test
	public void test_Import_Valid_6() {
		util.exec("Import_Valid_6");
	}

	@Test
	public void test_Import_Valid_7() {
		util.exec("Import_Valid_7");
	}

	/**
	 * .\Intersection_Valid_1.whiley:3: internal failure, intersection types not supported yet
type EmptyList is [int] & [real]
                 ^^^^^^^^^^^^^^^
wycc.lang.SyntaxError$InternalFailure: internal failure, intersection types not supported yet
	at wycc.lang.SyntaxError.internalFailure(SyntaxError.java:297)
	at wyc.lang.WhileyFile.internalFailure(WhileyFile.java:630)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3210)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3078)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3023)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:179)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:147)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Ignore("Issue ???\n"
			+ ".\\Intersection_Valid_1.whiley:3: internal failure, intersection types not supported yet\r\n" + 
			"type EmptyList is [int] & [real]\r\n" + 
			"                 ^^^^^^^^^^^^^^^\r\n" + 
			"wycc.lang.SyntaxError$InternalFailure: internal failure, intersection types not supported yet\r\n" + 
			"	at wycc.lang.SyntaxError.internalFailure(SyntaxError.java:297)\r\n" + 
			"	at wyc.lang.WhileyFile.internalFailure(WhileyFile.java:630)")
	@Test
	public void test_Intersection_Valid_1() {
		util.exec("Intersection_Valid_1");
	}
	/**
	 * .\Intersection_Valid_2.whiley:7: internal failure, intersection types not supported yet
type InterList is UnitList & LinkedList
                 ^^^^^^^^^^^^^^^^^^^^^^
wycc.lang.SyntaxError$InternalFailure: internal failure, intersection types not supported yet
	at wycc.lang.SyntaxError.internalFailure(SyntaxError.java:297)
	at wyc.lang.WhileyFile.internalFailure(WhileyFile.java:630)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3210)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3078)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3023)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:179)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:147)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Ignore("Issue ???\n"
			+ " .\\Intersection_Valid_2.whiley:7: internal failure, intersection types not supported yet\r\n" + 
			"type InterList is UnitList & LinkedList\r\n" + 
			"                 ^^^^^^^^^^^^^^^^^^^^^^\r\n" + 
			"wycc.lang.SyntaxError$InternalFailure: internal failure, intersection types not supported yet\r\n" + 
			"	at wycc.lang.SyntaxError.internalFailure(SyntaxError.java:297)\r\n" + 
			"	at wyc.lang.WhileyFile.internalFailure(WhileyFile.java:630)")
	@Test
	public void test_Intersection_Valid_2() {
		util.exec("Intersection_Valid_2");
	}
	
	@Test
	public void test_IntConst_Valid_1() {
		util.exec("IntConst_Valid_1");
	}

	@Test
	public void test_IntDefine_Valid_1() {
		util.exec("IntDefine_Valid_1");
	}

	@Test
	public void test_IntDefine_Valid_2() {
		util.exec("IntDefine_Valid_2");
	}

	@Test
	public void test_IntDiv_Valid_1() {
		util.exec("IntDiv_Valid_1");
	}

	@Test
	public void test_IntDiv_Valid_3() {
		util.exec("IntDiv_Valid_3");
	}

	@Test
	public void test_IntDiv_Valid_4() {
		util.exec("IntDiv_Valid_4");
	}

	@Test
	public void test_IntDiv_Valid_5() {
		util.exec("IntDiv_Valid_5");
	}

	@Test
	public void test_IntEquals_Valid_1() {
		util.exec("IntEquals_Valid_1");
	}

	@Test
	public void test_IntMul_Valid_1() {
		util.exec("IntMul_Valid_1");
	}

	@Test
	public void test_IntMul_Valid_2() {
		util.exec("IntMul_Valid_2");
	}

	@Test
	public void test_IntOp_Valid_1() {
		util.exec("IntOp_Valid_1");
	}
	
	@Test
	public void test_Lambda_Valid_1() {
		util.exec("Lambda_Valid_1");
	}

	@Test
	public void test_Lambda_Valid_2() {
		util.exec("Lambda_Valid_2");
	}

	@Test
	public void test_Lambda_Valid_3() {
		util.exec("Lambda_Valid_3");
	}

	@Test
	public void test_Lambda_Valid_4() {
		util.exec("Lambda_Valid_4");
	}

	@Test
	public void test_Lambda_Valid_5() {
		util.exec("Lambda_Valid_5");
	}

	@Test
	public void test_Lambda_Valid_6() {
		util.exec("Lambda_Valid_6");
	}

	@Test
	public void test_Lambda_Valid_7() {
		util.exec("Lambda_Valid_7");
	}

	@Test
	public void test_Lambda_Valid_8() {
		util.exec("Lambda_Valid_8");
	}
	
	@Test
	public void test_Lambda_Valid_9() {
		util.exec("Lambda_Valid_9");
	}

	@Test
	public void test_LengthOf_Valid_1() {
		util.exec("LengthOf_Valid_1");
	}

	@Test
	public void test_LengthOf_Valid_2() {
		util.exec("LengthOf_Valid_2");
	}

	@Test
	public void test_LengthOf_Valid_3() {
		util.exec("LengthOf_Valid_3");
	}

	@Test
	public void test_LengthOf_Valid_4() {
		util.exec("LengthOf_Valid_4");
	}

	@Test
	public void test_LengthOf_Valid_5() {
		util.exec("LengthOf_Valid_5");
	}
	
	@Test
	public void test_ListAccess_Valid_1() {
		util.exec("ListAccess_Valid_1");
	}

	@Test
	public void test_ListAccess_Valid_3() {
		util.exec("ListAccess_Valid_3");
	}

	@Test
	public void test_ListAccess_Valid_5() {
		util.exec("ListAccess_Valid_5");
	}
	/**
	 * .\ListAccess_Valid_6.whiley:9: invalid set or list expression
            r = r ++ [r[0]]
                      ^
wycc.lang.SyntaxError: invalid set or list expression
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2149)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1687)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2216)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1696)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1754)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1660)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:463)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:325)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:767)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.computeFixedPoint(FlowTypeChecker.java:2471)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:692)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:333)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Ignore("Issue ???\n"
			+ ".\\ListAccess_Valid_6.whiley:9: invalid set or list expression\r\n" + 
			"            r = r ++ [r[0]]\r\n" + 
			"                      ^\r\n" + 
			"wycc.lang.SyntaxError: invalid set or list expression\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)")
	@Test
	public void test_ListAccess_Valid_6() {
		util.exec("ListAccess_Valid_6");
	}
	/**
	 * .\ListAccess_Valid_7.whiley:6: incomparable operands: [void] and null
        if r == null:
           ^^^^^^^^^
wycc.lang.SyntaxError: incomparable operands: [void] and null
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)
	at wyc.builder.FlowTypeChecker.resolveLeafCondition(FlowTypeChecker.java:1606)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1356)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:755)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.computeFixedPoint(FlowTypeChecker.java:2471)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:692)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:333)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)

	 */
	@Ignore("Issue ???\n"
			+ ".\\ListAccess_Valid_7.whiley:6: incomparable operands: [void] and null\r\n" + 
			"        if r == null:\r\n" + 
			"           ^^^^^^^^^\r\n" + 
			"wycc.lang.SyntaxError: incomparable operands: [void] and null\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)")
	@Test
	public void test_ListAccess_Valid_7() {
		util.exec("ListAccess_Valid_7");
	}

	@Test
	public void test_ListAccess_Valid_8() {
		util.exec("ListAccess_Valid_8");
	}

	@Test
	public void test_ListAppend_Valid_1() {
		util.exec("ListAppend_Valid_1");
	}

	@Test
	public void test_ListAppend_Valid_11() {
		util.exec("ListAppend_Valid_11");
	}

	@Test
	public void test_ListAppend_Valid_13() {
		util.exec("ListAppend_Valid_13");
	}

	@Test
	public void test_ListAppend_Valid_14() {
		util.exec("ListAppend_Valid_14");
	}

	@Test
	public void test_ListAppend_Valid_2() {
		util.exec("ListAppend_Valid_2");
	}

	@Test
	public void test_ListAppend_Valid_3() {
		util.exec("ListAppend_Valid_3");
	}

	@Test
	public void test_ListAppend_Valid_4() {
		util.exec("ListAppend_Valid_4");
	}

	@Test
	public void test_ListAppend_Valid_5() {
		util.exec("ListAppend_Valid_5");
	}

	@Test
	public void test_ListAppend_Valid_6() {
		util.exec("ListAppend_Valid_6");
	}

	@Test
	public void test_ListAppend_Valid_7() {
		util.exec("ListAppend_Valid_7");
	}

	@Test
	public void test_ListAppend_Valid_8() {
		util.exec("ListAppend_Valid_8");
	}

	@Test
	public void test_ListAppend_Valid_9() {
		util.exec("ListAppend_Valid_9");
	}

	@Test
	public void test_ListAssign_Valid_1() {
		util.exec("ListAssign_Valid_1");
	}

	@Test
	public void test_ListAssign_Valid_11() {
		util.exec("ListAssign_Valid_11");
	}

	@Test
	public void test_ListAssign_Valid_2() {
		util.exec("ListAssign_Valid_2");
	}

	@Test
	public void test_ListAssign_Valid_3() {
		util.exec("ListAssign_Valid_3");
	}

	@Test
	public void test_ListAssign_Valid_4() {
		util.exec("ListAssign_Valid_4");
	}

	@Test
	public void test_ListAssign_Valid_5() {
		util.exec("ListAssign_Valid_5");
	}

	@Test
	public void test_ListAssign_Valid_6() {
		util.exec("ListAssign_Valid_6");
	}

	@Test
	public void test_ListAssign_Valid_7() {
		util.exec("ListAssign_Valid_7");
	}

	@Test
	public void test_ListAssign_Valid_8() {
		util.exec("ListAssign_Valid_8");
	}

	@Test
	public void test_ListAssign_Valid_9() {
		util.exec("ListAssign_Valid_9");
	}

	@Test
	public void test_ListConversion_Valid_1() {
		util.exec("ListConversion_Valid_1");
	}

	@Test
	public void test_ListElemOf_Valid_1() {
		util.exec("ListElemOf_Valid_1");
	}

	@Test
	public void test_ListEmpty_Valid_1() {
		util.exec("ListEmpty_Valid_1");
	}

	@Test
	public void test_ListEquals_Valid_1() {
		util.exec("ListEquals_Valid_1");
	}

	@Test
	public void test_ListGenerator_Valid_1() {
		util.exec("ListGenerator_Valid_1");
	}

	@Test
	public void test_ListGenerator_Valid_2() {
		util.exec("ListGenerator_Valid_2");
	}

	@Test
	public void test_ListGenerator_Valid_3() {
		util.exec("ListGenerator_Valid_3");
	}

	@Test
	public void test_ListGenerator_Valid_5() {
		util.exec("ListGenerator_Valid_5");
	}

	@Test
	public void test_ListLength_Valid_1() {
		util.exec("ListLength_Valid_1");
	}

	@Test
	public void test_ListLength_Valid_2() {
		util.exec("ListLength_Valid_2");
	}

	@Test
	public void test_ListLength_Valid_3() {
		util.exec("ListLength_Valid_3");
	}

	@Test
	public void test_ListRange_Valid_1() {
		util.exec("ListRange_Valid_1");
	}

	@Test
	public void test_ListSublist_Valid_1() {
		util.exec("ListSublist_Valid_1");
	}

	@Test
	public void test_ListSublist_Valid_2() {
		util.exec("ListSublist_Valid_2");
	}

	@Test
	public void test_ListSublist_Valid_3() {
		util.exec("ListSublist_Valid_3");
	}

	@Test
	public void test_ListSublist_Valid_4() {
		util.exec("ListSublist_Valid_4");
	}

	@Test
	public void test_ListSublist_Valid_5() {
		util.exec("ListSublist_Valid_5");
	}
	
	@Test
	public void test_MessageRef_Valid_1() {
		util.exec("MessageRef_Valid_1");
	}

	/**
	 * The expected result is different from executed one.
	 * Expected: 128
	 * Actual: 124 
	 */
	@Test
	public void test_MessageRef_Valid_2() {
		util.exec("MessageRef_Valid_2");
	}

	@Test
	public void test_MessageSend_Valid_1() {
		util.exec("MessageSend_Valid_1");
	}

	@Test
	public void test_MessageSend_Valid_2() {
		util.exec("MessageSend_Valid_2");
	}

	@Test
	public void test_MessageSend_Valid_3() {
		util.exec("MessageSend_Valid_3");
	}

	@Test
	public void test_MessageSend_Valid_4() {
		util.exec("MessageSend_Valid_4");
	}

	@Test
	public void test_MessageSend_Valid_5() {
		util.exec("MessageSend_Valid_5");
	}

	@Test
	public void test_MethodCall_Valid_1() {
		util.exec("MethodCall_Valid_1");
	}

	@Test
	public void test_MethodCall_Valid_2() {
		util.exec("MethodCall_Valid_2");
	}

	@Test
	public void test_MethodCall_Valid_3() {
		util.exec("MethodCall_Valid_3");
	}

	/**
	 * The expected result is different from the actual one.
	 * Expected:
	   123
	 * Actual:
	   SUM: 228
	   SUM: 228
	 * 
	 */
	@Test
	public void test_MethodCall_Valid_4() {
		util.exec("MethodCall_Valid_4");
	}

	@Test
	public void test_MethodRef_Valid_1() {
		util.exec("MethodRef_Valid_1");
	}

	@Test
	public void test_MethodRef_Valid_2() {
		util.exec("MethodRef_Valid_2");
	}

	@Test
	public void test_Method_Valid_1() {
		util.exec("Method_Valid_1");
	}

	@Test
	public void test_MultiLineComment_Valid_1() {
		util.exec("MultiLineComment_Valid_1");
	}

	@Test
	public void test_MultiLineComment_Valid_2() {
		util.exec("MultiLineComment_Valid_2");
	}
	
	@Test
	public void test_NegationType_Valid_1() {
		util.exec("NegationType_Valid_1");
	}

	@Test
	public void test_NegationType_Valid_2() {
		util.exec("NegationType_Valid_2");
	}
	/**
	 * .\NegationType_Valid_3.whiley:3: internal failure, intersection types not supported yet
function f(string x) => !null & !int:
                         ^^^^^^^^^^^
wycc.lang.SyntaxError$InternalFailure: internal failure, intersection types not supported yet
	at wycc.lang.SyntaxError.internalFailure(SyntaxError.java:297)
	at wyc.lang.WhileyFile.internalFailure(WhileyFile.java:630)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3210)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3197)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3078)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:3023)
	at wyc.builder.FlowTypeChecker.resolveAsType(FlowTypeChecker.java:2975)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:256)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)

	 */
	@Ignore("Issue ???\n"
			+ " .\\NegationType_Valid_3.whiley:3: internal failure, intersection types not supported yet\r\n" + 
			"function f(string x) => !null & !int:\r\n" + 
			"                         ^^^^^^^^^^^\r\n" + 
			"wycc.lang.SyntaxError$InternalFailure: internal failure, intersection types not supported yet\r\n" + 
			"	at wycc.lang.SyntaxError.internalFailure(SyntaxError.java:297)\r\n" + 
			"	at wyc.lang.WhileyFile.internalFailure(WhileyFile.java:630)")
	@Test
	public void test_NegationType_Valid_3() {
		util.exec("NegationType_Valid_3");
	}

	@Test
	public void test_NegationType_Valid_4() {
		util.exec("NegationType_Valid_4");
	}

	@Test
	public void test_OpenRecord_Valid_1() {
		util.exec("OpenRecord_Valid_1");
	}

	@Test
	public void test_OpenRecord_Valid_10() {
		util.exec("OpenRecord_Valid_10");
	}

	@Test
	public void test_OpenRecord_Valid_2() {
		util.exec("OpenRecord_Valid_2");
	}

	@Test
	@Ignore("Issue ???")
	public void test_OpenRecord_Valid_3() {
		util.exec("OpenRecord_Valid_3");
	}

	@Test
	public void test_OpenRecord_Valid_4() {
		util.exec("OpenRecord_Valid_4");
	}

	@Test
	public void test_OpenRecord_Valid_5() {
		util.exec("OpenRecord_Valid_5");
	}

	@Test
	public void test_OpenRecord_Valid_6() {
		util.exec("OpenRecord_Valid_6");
	}

	@Test
	public void test_OpenRecord_Valid_7() {
		util.exec("OpenRecord_Valid_7");
	}

	@Test
	public void test_OpenRecord_Valid_8() {
		util.exec("OpenRecord_Valid_8");
	}

	@Test
	public void test_OpenRecord_Valid_9() {
		util.exec("OpenRecord_Valid_9");
	}
	
	@Test
	public void test_Print_Valid_1() {
		util.exec("Print_Valid_1");
	}
	
	@Test
	public void test_ProcessAccess_Valid_1() {
		util.exec("ProcessAccess_Valid_1");
	}

	@Test
	public void test_ProcessAccess_Valid_2() {
		util.exec("ProcessAccess_Valid_2");
	}

	@Test
	public void test_Process_Valid_1() {
		util.exec("Process_Valid_1");
	}

	@Test
	public void test_Process_Valid_10() {
		util.exec("Process_Valid_10");
	}

	@Test
	public void test_Process_Valid_11() {
		util.exec("Process_Valid_11");
	}

	@Test
	public void test_Process_Valid_12() {
		util.exec("Process_Valid_12");
	}

	@Test
	public void test_Process_Valid_13() {
		util.exec("Process_Valid_13");
	}

	@Test
	public void test_Process_Valid_14() {
		util.exec("Process_Valid_14");
	}

	@Test
	public void test_Process_Valid_2() {
		util.exec("Process_Valid_2");
	}

	@Test
	public void test_Process_Valid_3() {
		util.exec("Process_Valid_3");
	}

	@Test
	public void test_Process_Valid_4() {
		util.exec("Process_Valid_4");
	}

	@Test
	public void test_Process_Valid_5() {
		util.exec("Process_Valid_5");
	}

	@Test
	public void test_Process_Valid_6() {
		util.exec("Process_Valid_6");
	}

	@Test
	public void test_Process_Valid_7() {
		util.exec("Process_Valid_7");
	}

	@Test
	public void test_Process_Valid_8() {
		util.exec("Process_Valid_8");
	}

	@Test
	public void test_Process_Valid_9() {
		util.exec("Process_Valid_9");
	}

	@Test
	public void test_Quantifiers_Valid_1() {
		util.exec("Quantifiers_Valid_1");
	}
	
	@Test
	public void test_Range_Valid_1() {
		util.exec("Range_Valid_1");
	}

	@Test
	public void test_RealConst_Valid_1() {
		util.exec("RealConst_Valid_1");
	}

	@Test
	public void test_RealDiv_Valid_1() {
		util.exec("RealDiv_Valid_1");
	}

	@Test
	public void test_RealDiv_Valid_2() {
		util.exec("RealDiv_Valid_2");
	}

	@Test
	public void test_RealDiv_Valid_3() {
		util.exec("RealDiv_Valid_3");
	}

	@Test
	public void test_RealDiv_Valid_4() {
		util.exec("RealDiv_Valid_4");
	}

	@Test
	public void test_RealDiv_Valid_5() {
		util.exec("RealDiv_Valid_5");
	}

	@Test
	public void test_RealDiv_Valid_6() {
		util.exec("RealDiv_Valid_6");
	}

	@Test
	public void test_RealDiv_Valid_7() {
		util.exec("RealDiv_Valid_7");
	}

	@Test
	public void test_RealNeg_Valid_1() {
		util.exec("RealNeg_Valid_1");
	}

	@Test
	public void test_RealNeg_Valid_2() {
		util.exec("RealNeg_Valid_2");
	}

	@Test
	public void test_RealSplit_Valid_1() {
		util.exec("RealSplit_Valid_1");
	}

	@Test
	public void test_RealSub_Valid_1() {
		util.exec("RealSub_Valid_1");
	}

	@Test
	public void test_RealSub_Valid_2() {
		util.exec("RealSub_Valid_2");
	}

	@Test
	public void test_RealSub_Valid_3() {
		util.exec("RealSub_Valid_3");
	}

	@Test
	public void test_Real_Valid_1() {
		util.exec("Real_Valid_1");
	}

	@Test
	public void test_RecordAccess_Valid_1() {
		util.exec("RecordAccess_Valid_1");
	}

	@Test
	public void test_RecordAccess_Valid_2() {
		util.exec("RecordAccess_Valid_2");
	}

	@Test
	public void test_RecordAssign_Valid_1() {
		util.exec("RecordAssign_Valid_1");
	}

	@Test
	public void test_RecordAssign_Valid_10() {
		util.exec("RecordAssign_Valid_10");
	}

	@Test
	public void test_RecordAssign_Valid_2() {
		util.exec("RecordAssign_Valid_2");
	}

	@Test
	public void test_RecordAssign_Valid_3() {
		util.exec("RecordAssign_Valid_3");
	}

	@Test
	public void test_RecordAssign_Valid_4() {
		util.exec("RecordAssign_Valid_4");
	}

	@Test
	public void test_RecordAssign_Valid_5() {
		util.exec("RecordAssign_Valid_5");
	}

	@Test
	public void test_RecordAssign_Valid_6() {
		util.exec("RecordAssign_Valid_6");
	}

	@Test
	public void test_RecordAssign_Valid_7() {
		util.exec("RecordAssign_Valid_7");
	}

	@Test
	public void test_RecordAssign_Valid_8() {
		util.exec("RecordAssign_Valid_8");
	}

	@Test
	public void test_RecordAssign_Valid_9() {
		util.exec("RecordAssign_Valid_9");
	}

	@Test
	public void test_RecordCoercion_Valid_1() {
		util.exec("RecordCoercion_Valid_1");
	}

	@Test
	public void test_RecordConversion_Valid_1() {
		util.exec("RecordConversion_Valid_1");
	}

	@Test
	public void test_RecordDefine_Valid_1() {
		util.exec("RecordDefine_Valid_1");
	}

	@Test
	public void test_RecordDefine_Valid_2() {
		util.exec("RecordDefine_Valid_2");
	}
	/**
	 * .\RecordSubtype_Valid_1.whiley:12: expected type {null x}|{int x}, found {null|int x}
    return x
           ^
wycc.lang.SyntaxError: expected type {null x}|{int x}, found {null|int x}
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.builder.FlowTypeChecker.checkIsSubtype(FlowTypeChecker.java:4060)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:799)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:327)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)

	 */
	@Ignore("Issue ???\n"
			+ "\\RecordSubtype_Valid_1.whiley:12: expected type {null x}|{int x}, found {null|int x}\r\n" + 
			"    return x\r\n" + 
			"           ^\r\n" + 
			"wycc.lang.SyntaxError: expected type {null x}|{int x}, found {null|int x}\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.builder.FlowTypeChecker.checkIsSubtype(FlowTypeChecker.java:4060)")
	@Test
	public void test_RecordSubtype_Valid_1() {
		util.exec("RecordSubtype_Valid_1");
	}
	/**
	 * .\RecordSubtype_Valid_2.whiley:12: expected type {null data,X<null|{null|int data,X next}> next}|{int data,X<null|{null|int data,X next}> next}, found X<{null|int data,X|null next}>
    return r
           ^
wycc.lang.SyntaxError: expected type {null data,X<null|{null|int data,X next}> next}|{int data,X<null|{null|int data,X next}> next}, found X<{null|int data,X|null next}>
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.builder.FlowTypeChecker.checkIsSubtype(FlowTypeChecker.java:4060)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:799)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:327)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Ignore("Issue ???\n"
			+ " .\\RecordSubtype_Valid_2.whiley:12: expected type {null data,X<null|{null|int data,X next}> next}|{int data,X<null|{null|int data,X next}> next}, found X<{null|int data,X|null next}>\r\n" + 
			"    return r\r\n" + 
			"           ^\r\n" + 
			"wycc.lang.SyntaxError: expected type {null data,X<null|{null|int data,X next}> next}|{int data,X<null|{null|int data,X next}> next}, found X<{null|int data,X|null next}>\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)")
	@Test
	public void test_RecordSubtype_Valid_2() {
		util.exec("RecordSubtype_Valid_2");
	}

	@Test
	public void test_RecursiveType_Valid_1() {
		util.exec("RecursiveType_Valid_1");
	}

	@Test
	public void test_RecursiveType_Valid_10() {
		util.exec("RecursiveType_Valid_10");
	}

	@Test
	public void test_RecursiveType_Valid_11() {
		util.exec("RecursiveType_Valid_11");
	}

	@Test
	public void test_RecursiveType_Valid_12() {
		util.exec("RecursiveType_Valid_12");
	}

	@Test
	public void test_RecursiveType_Valid_13() {
		util.exec("RecursiveType_Valid_13");
	}

	@Test
	public void test_RecursiveType_Valid_14() {
		util.exec("RecursiveType_Valid_14");
	}

	@Test
	public void test_RecursiveType_Valid_15() {
		util.exec("RecursiveType_Valid_15");
	}

	@Test
	public void test_RecursiveType_Valid_16() {
		util.exec("RecursiveType_Valid_16");
	}

	@Test
	public void test_RecursiveType_Valid_17() {
		util.exec("RecursiveType_Valid_17");
	}

	@Test
	public void test_RecursiveType_Valid_18() {
		util.exec("RecursiveType_Valid_18");
	}

	@Test
	public void test_RecursiveType_Valid_19() {
		util.exec("RecursiveType_Valid_19");
	}

	@Test
	public void test_RecursiveType_Valid_2() {
		util.exec("RecursiveType_Valid_2");
	}

	@Test
	public void test_RecursiveType_Valid_20() {
		util.exec("RecursiveType_Valid_20");
	}

	@Test
	public void test_RecursiveType_Valid_21() {
		util.exec("RecursiveType_Valid_21");
	}

	
	@Test
	public void test_RecursiveType_Valid_22() {
		util.exec("RecursiveType_Valid_22");
	}

	@Test
	public void test_RecursiveType_Valid_23() {
		util.exec("RecursiveType_Valid_23");
	}

	@Test
	public void test_RecursiveType_Valid_24() {
		util.exec("RecursiveType_Valid_24");
	}

	@Test
	public void test_RecursiveType_Valid_25() {
		util.exec("RecursiveType_Valid_25");
	}

	@Test
	public void test_RecursiveType_Valid_26() {
		util.exec("RecursiveType_Valid_26");
	}

	@Test
	public void test_RecursiveType_Valid_27() {
		util.exec("RecursiveType_Valid_27");
	}

	/**
	 .\RecursiveType_Valid_28.whiley:27: unable to resolve name (no match for get(X<null|{int data,X next}>,int)
	found: RecursiveType_Valid_28:get : function(RecursiveType_Valid_28:Link,int) => int)
        return get(ls.next,i+1)
               ^^^^^^^^^^^^^^^^
wycc.lang.SyntaxError: unable to resolve name (no match for get(X<null|{int data,X next}>,int)
	found: RecursiveType_Valid_28:get : function(RecursiveType_Valid_28:Link,int) => int)
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:253)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:625)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1715)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:797)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:327)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:767)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
Caused by: wycc.util.ResolveError: no match for get(X<null|{int data,X next}>,int)
	found: RecursiveType_Valid_28:get : function(RecursiveType_Valid_28:Link,int) => int
	at wyc.builder.FlowTypeChecker.selectCandidateFunctionOrMethod(FlowTypeChecker.java:2681)
	at wyc.builder.FlowTypeChecker.resolveAsFunctionOrMethod(FlowTypeChecker.java:2509)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2126)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1681)
	... 16 more
	 */
	@Test
	@Ignore("Issue #364\n"
			+ " .\\RecursiveType_Valid_28.whiley:27: unable to resolve name (no match for get(X<null|{int data,X next}>,int)\r\n" + 
			"	found: RecursiveType_Valid_28:get : function(RecursiveType_Valid_28:Link,int) => int)\r\n" + 
			"        return get(ls.next,i+1)\r\n" + 
			"               ^^^^^^^^^^^^^^^^\r\n" + 
			"wycc.lang.SyntaxError: unable to resolve name (no match for get(X<null|{int data,X next}>,int)\r\n" + 
			"	found: RecursiveType_Valid_28:get : function(RecursiveType_Valid_28:Link,int) => int)\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:253)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:625)")
	public void test_RecursiveType_Valid_28() {
		util.exec("RecursiveType_Valid_28");
	}
	
	public void test_RecursiveType_Valid_29() {
		util.exec("RecursiveType_Valid_29");
	}
	

	/**
	 .\RecursiveType_Valid_3.whiley:19: record required, got: X<[Y<X|{Y index,Z<{Z index,Z src}|int|real|[Z]> src}|{Z<{Z index,Z src}|int|real|[Z]> index,Y src}>+]>|{Z<{Z index,Z src}|int|real|[Z]> index,Z<{Z index,Z src}|int|real|[Z]> src}
            Value src = evaluate(e.src)
                                 ^^^^^
wycc.lang.SyntaxError: record required, got: X<[Y<X|{Y index,Z<{Z index,Z src}|int|real|[Z]> src}|{Z<{Z index,Z src}|int|real|[Z]> index,Y src}>+]>|{Z<{Z index,Z src}|int|real|[Z]> index,Z<{Z index,Z src}|int|real|[Z]> src}
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2327)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1674)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2108)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1681)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:428)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:323)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:767)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:767)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 
	  */
	@Test
	@Ignore("Issue #406\n"
			+ ".\\RecursiveType_Valid_3.whiley:19: record required, got: X<[Y<X|{Y index,Z<{Z index,Z src}|int|real|[Z]> src}|{Z<{Z index,Z src}|int|real|[Z]> index,Y src}>+]>|{Z<{Z index,Z src}|int|real|[Z]> index,Z<{Z index,Z src}|int|real|[Z]> src}\r\n" + 
			"            Value src = evaluate(e.src)\r\n" + 
			"                                 ^^^^^\r\n" + 
			"wycc.lang.SyntaxError: record required, got: X<[Y<X|{Y index,Z<{Z index,Z src}|int|real|[Z]> src}|{Z<{Z index,Z src}|int|real|[Z]> index,Y src}>+]>|{Z<{Z index,Z src}|int|real|[Z]> index,Z<{Z index,Z src}|int|real|[Z]> src}\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)")
	public void test_RecursiveType_Valid_3() {
		util.exec("RecursiveType_Valid_3");
	}

	public void test_RecursiveType_Valid_30() {
		util.exec("RecursiveType_Valid_30");
	}
	
	public void test_RecursiveType_Valid_31() {
		util.exec("RecursiveType_Valid_31");
	}
	
	/**
	 .\RecursiveType_Valid_4.whiley:23: record required, got: X<[Y<X|{Y index,Z<{Z index,Z src}|int|real|[Z]> src}|{Z<{Z index,Z src}|int|real|[Z]> index,Y src}>+]>|{Z<{Z index,Z src}|int|real|[Z]> index,Z<{Z index,Z src}|int|real|[Z]> src}
            null|Value src = evaluate(e.src)
                                      ^^^^^
wycc.lang.SyntaxError: record required, got: X<[Y<X|{Y index,Z<{Z index,Z src}|int|real|[Z]> src}|{Z<{Z index,Z src}|int|real|[Z]> index,Y src}>+]>|{Z<{Z index,Z src}|int|real|[Z]> index,Z<{Z index,Z src}|int|real|[Z]> src}
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2327)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1674)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2108)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1681)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:428)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:323)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:767)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:767)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Test
	@Ignore("Issue #406\n"
			+ ".\\RecursiveType_Valid_4.whiley:23: record required, got: X<[Y<X|{Y index,Z<{Z index,Z src}|int|real|[Z]> src}|{Z<{Z index,Z src}|int|real|[Z]> index,Y src}>+]>|{Z<{Z index,Z src}|int|real|[Z]> index,Z<{Z index,Z src}|int|real|[Z]> src}\r\n" + 
			"            null|Value src = evaluate(e.src)\r\n" + 
			"                                      ^^^^^\r\n" + 
			"wycc.lang.SyntaxError: record required, got: X<[Y<X|{Y index,Z<{Z index,Z src}|int|real|[Z]> src}|{Z<{Z index,Z src}|int|real|[Z]> index,Y src}>+]>|{Z<{Z index,Z src}|int|real|[Z]> index,Z<{Z index,Z src}|int|real|[Z]> src}\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)")
	public void test_RecursiveType_Valid_4() {
		util.exec("RecursiveType_Valid_4");
	}
	/**
	 * .\RecursiveType_Valid_5.whiley:6: expected type X<{[int] items,X|null next}>, found null
    Link start = null
                 ^^^^
wycc.lang.SyntaxError: expected type X<{[int] items,X|null next}>, found null
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.builder.FlowTypeChecker.checkIsSubtype(FlowTypeChecker.java:4070)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:429)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:323)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)

	 */
	@Ignore("Issue #18\n"
			+ " .\\RecursiveType_Valid_5.whiley:6: expected type X<{[int] items,X|null next}>, found null\r\n" + 
			"    Link start = null\r\n" + 
			"                 ^^^^\r\n" + 
			"wycc.lang.SyntaxError: expected type X<{[int] items,X|null next}>, found null\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.builder.FlowTypeChecker.checkIsSubtype(FlowTypeChecker.java:4070)")
	@Test
	public void test_RecursiveType_Valid_5() {
		util.exec("RecursiveType_Valid_5");
	}

	@Test
	public void test_RecursiveType_Valid_6() {
		util.exec("RecursiveType_Valid_6");
	}

	@Test
	public void test_RecursiveType_Valid_7() {
		util.exec("RecursiveType_Valid_7");
	}

	@Test
	public void test_RecursiveType_Valid_8() {
		util.exec("RecursiveType_Valid_8");
	}

	@Test
	public void test_RecursiveType_Valid_9() {
		util.exec("RecursiveType_Valid_9");
	}

	@Test
	public void test_Reference_Valid_1() {
		util.exec("Reference_Valid_1");
	}
	
	@Test
	public void test_Reference_Valid_2() {
		util.exec("Reference_Valid_2");
	}
	
	@Test
	public void test_Reference_Valid_3() {
		util.exec("Reference_Valid_3");
	}
	
	
	@Test
	public void test_Reference_Valid_4() {
		util.exec("Reference_Valid_4");
	}
	
	@Test
	public void test_Reference_Valid_5() {
		util.exec("Reference_Valid_5");
	}
	
	@Test
	public void test_Remainder_Valid_1() {
		util.exec("Remainder_Valid_1");
	}

	@Test
	public void test_Requires_Valid_1() {
		util.exec("Requires_Valid_1");
	}
	
	@Test
	public void test_Resolution_Valid_1() {
		util.exec("Resolution_Valid_1");
	}

	@Test
	public void test_SetAssign_Valid_1() {
		util.exec("SetAssign_Valid_1");
	}

	@Test
	public void test_SetAssign_Valid_2() {
		util.exec("SetAssign_Valid_2");
	}

	@Test
	public void test_SetAssign_Valid_3() {
		util.exec("SetAssign_Valid_3");
	}

	@Test
	public void test_SetComprehension_Valid_1() {
		util.exec("SetComprehension_Valid_1");
	}

	@Test
	public void test_SetComprehension_Valid_10() {
		util.exec("SetComprehension_Valid_10");
	}

	@Test
	public void test_SetComprehension_Valid_11() {
		util.exec("SetComprehension_Valid_11");
	}

	@Test
	public void test_SetComprehension_Valid_12() {
		util.exec("SetComprehension_Valid_12");
	}

	@Test
	public void test_SetComprehension_Valid_2() {
		util.exec("SetComprehension_Valid_2");
	}

	@Test
	public void test_SetComprehension_Valid_3() {
		util.exec("SetComprehension_Valid_3");
	}

	@Test
	public void test_SetComprehension_Valid_4() {
		util.exec("SetComprehension_Valid_4");
	}

	@Test
	public void test_SetComprehension_Valid_5() {
		util.exec("SetComprehension_Valid_5");
	}

	@Test
	public void test_SetComprehension_Valid_6() {
		util.exec("SetComprehension_Valid_6");
	}

	@Test
	public void test_SetComprehension_Valid_7() {
		util.exec("SetComprehension_Valid_7");
	}

	@Test
	public void test_SetComprehension_Valid_8() {
		util.exec("SetComprehension_Valid_8");
	}

	@Test
	public void test_SetComprehension_Valid_9() {
		util.exec("SetComprehension_Valid_9");
	}

	@Test
	public void test_SetConversion_Valid_1() {
		util.exec("SetConversion_Valid_1");
	}

	@Test
	public void test_SetDefine_Valid_1() {
		util.exec("SetDefine_Valid_1");
	}

	@Test
	public void test_SetDefine_Valid_2() {
		util.exec("SetDefine_Valid_2");
	}

	@Test
	public void test_SetDefine_Valid_3() {
		util.exec("SetDefine_Valid_3");
	}

	@Test
	public void test_SetDifference_Valid_1() {
		util.exec("SetDifference_Valid_1");
	}

	@Test
	public void test_SetElemOf_Valid_1() {
		util.exec("SetElemOf_Valid_1");
	}

	@Test
	public void test_SetEmpty_Valid_1() {
		util.exec("SetEmpty_Valid_1");
	}

	@Test
	public void test_SetEquals_Valid_1() {
		util.exec("SetEquals_Valid_1");
	}

	@Test
	public void test_SetGenerator_Valid_1() {
		util.exec("SetGenerator_Valid_1");
	}

	@Test
	public void test_SetIntersect_Valid_1() {
		util.exec("SetIntersect_Valid_1");
	}

	@Test
	public void test_SetIntersect_Valid_2() {
		util.exec("SetIntersect_Valid_2");
	}

	@Test
	public void test_SetIntersection_Valid_1() {
		util.exec("SetIntersection_Valid_1");
	}

	@Test
	public void test_SetIntersection_Valid_2() {
		util.exec("SetIntersection_Valid_2");
	}

	@Test
	public void test_SetIntersection_Valid_3() {
		util.exec("SetIntersection_Valid_3");
	}

	@Test
	public void test_SetIntersection_Valid_4() {
		util.exec("SetIntersection_Valid_4");
	}

	@Test
	public void test_SetIntersection_Valid_5() {
		util.exec("SetIntersection_Valid_5");
	}

	@Test
	public void test_SetIntersection_Valid_6() {
		util.exec("SetIntersection_Valid_6");
	}

	@Test
	public void test_SetIntersection_Valid_7() {
		util.exec("SetIntersection_Valid_7");
	}

	@Test
	public void test_SetLength_Valid_1() {
		util.exec("SetLength_Valid_1");
	}

	@Test
	public void test_SetNull_Valid_1() {
		util.exec("SetNull_Valid_1");
	}

	@Test
	public void test_SetSubset_Valid_1() {
		util.exec("SetSubset_Valid_1");
	}

	@Test
	public void test_SetSubset_Valid_10() {
		util.exec("SetSubset_Valid_10");
	}

	@Test
	public void test_SetSubset_Valid_11() {
		util.exec("SetSubset_Valid_11");
	}

	@Test
	public void test_SetSubset_Valid_12() {
		util.exec("SetSubset_Valid_12");
	}

	@Test
	public void test_SetSubset_Valid_2() {
		util.exec("SetSubset_Valid_2");
	}

	@Test
	public void test_SetSubset_Valid_3() {
		util.exec("SetSubset_Valid_3");
	}

	@Test
	public void test_SetSubset_Valid_4() {
		util.exec("SetSubset_Valid_4");
	}

	@Test
	public void test_SetSubset_Valid_5() {
		util.exec("SetSubset_Valid_5");
	}

	@Test
	public void test_SetSubset_Valid_6() {
		util.exec("SetSubset_Valid_6");
	}

	@Test
	public void test_SetSubset_Valid_7() {
		util.exec("SetSubset_Valid_7");
	}

	@Test
	public void test_SetSubset_Valid_8() {
		util.exec("SetSubset_Valid_8");
	}

	@Test
	public void test_SetSubset_Valid_9() {
		util.exec("SetSubset_Valid_9");
	}

	@Test
	public void test_SetUnion_Valid_1() {
		util.exec("SetUnion_Valid_1");
	}

	@Test
	public void test_SetUnion_Valid_10() {
		util.exec("SetUnion_Valid_10");
	}

	@Test
	public void test_SetUnion_Valid_11() {
		util.exec("SetUnion_Valid_11");
	}

	@Test
	public void test_SetUnion_Valid_2() {
		util.exec("SetUnion_Valid_2");
	}

	@Test
	public void test_SetUnion_Valid_3() {
		util.exec("SetUnion_Valid_3");
	}

	@Test
	public void test_SetUnion_Valid_4() {
		util.exec("SetUnion_Valid_4");
	}

	@Test
	public void test_SetUnion_Valid_5() {
		util.exec("SetUnion_Valid_5");
	}

	@Test
	public void test_SetUnion_Valid_6() {
		util.exec("SetUnion_Valid_6");
	}

	@Test
	public void test_SetUnion_Valid_7() {
		util.exec("SetUnion_Valid_7");
	}

	@Test
	public void test_SetUnion_Valid_8() {
		util.exec("SetUnion_Valid_8");
	}

	@Test
	public void test_SetUnion_Valid_9() {
		util.exec("SetUnion_Valid_9");
	}
	
	@Test
	public void test_Skip_Valid_1() {
		util.exec("Skip_Valid_1");
	}

	@Test
	public void test_String_Valid_1() {
		util.exec("String_Valid_1");
	}

	@Test
	public void test_String_Valid_2() {
		util.exec("String_Valid_2");
	}

	@Test
	public void test_String_Valid_3() {
		util.exec("String_Valid_3");
	}

	@Test
	public void test_String_Valid_4() {
		util.exec("String_Valid_4");
	}

	@Test
	public void test_String_Valid_5() {
		util.exec("String_Valid_5");
	}

	@Test
	public void test_String_Valid_6() {
		util.exec("String_Valid_6");
	}

	@Test
	public void test_String_Valid_7() {
		util.exec("String_Valid_7");
	}

	@Test
	public void test_String_Valid_8() {
		util.exec("String_Valid_8");
	}

	@Test
	public void test_Subtype_Valid_1() {
		util.exec("Subtype_Valid_1");
	}

	@Test
	public void test_Subtype_Valid_10() {
		util.exec("Subtype_Valid_10");
	}

	@Test
	public void test_Subtype_Valid_11() {
		util.exec("Subtype_Valid_11");
	}

	@Test
	public void test_Subtype_Valid_12() {
		util.exec("Subtype_Valid_12");
	}

	@Test
	public void test_Subtype_Valid_13() {
		util.exec("Subtype_Valid_13");
	}

	@Test
	public void test_Subtype_Valid_14() {
		util.exec("Subtype_Valid_14");
	}

	@Test
	public void test_Subtype_Valid_2() {
		util.exec("Subtype_Valid_2");
	}

	@Test
	public void test_Subtype_Valid_3() {
		util.exec("Subtype_Valid_3");
	}

	@Test
	public void test_Subtype_Valid_4() {
		util.exec("Subtype_Valid_4");
	}

	@Test
	public void test_Subtype_Valid_5() {
		util.exec("Subtype_Valid_5");
	}

	@Test
	public void test_Subtype_Valid_6() {
		util.exec("Subtype_Valid_6");
	}

	@Test
	public void test_Subtype_Valid_7() {
		util.exec("Subtype_Valid_7");
	}

	@Test
	public void test_Subtype_Valid_8() {
		util.exec("Subtype_Valid_8");
	}

	@Test
	public void test_Subtype_Valid_9() {
		util.exec("Subtype_Valid_9");
	}

	@Test
	public void test_Switch_Valid_1() {
		util.exec("Switch_Valid_1");
	}

	@Test
	public void test_Switch_Valid_10() {
		util.exec("Switch_Valid_10");
	}

	@Test
	public void test_Switch_Valid_11() {
		util.exec("Switch_Valid_11");
	}

	@Test
	public void test_Switch_Valid_12() {
		util.exec("Switch_Valid_12");
	}

	@Test
	public void test_Switch_Valid_13() {
		util.exec("Switch_Valid_13");
	}

	@Test
	public void test_Switch_Valid_2() {
		util.exec("Switch_Valid_2");
	}

	@Test
	public void test_Switch_Valid_3() {
		util.exec("Switch_Valid_3");
	}

	@Test
	public void test_Switch_Valid_4() {
		util.exec("Switch_Valid_4");
	}

	@Test
	public void test_Switch_Valid_5() {
		util.exec("Switch_Valid_5");
	}

	@Test
	public void test_Switch_Valid_6() {
		util.exec("Switch_Valid_6");
	}

	@Test
	public void test_Switch_Valid_7() {
		util.exec("Switch_Valid_7");
	}

	@Test
	public void test_Switch_Valid_8() {
		util.exec("Switch_Valid_8");
	}

	@Test
	public void test_Switch_Valid_9() {
		util.exec("Switch_Valid_9");
	}
	
	@Test
	public void test_Syntax_Valid_1() {
		util.exec("Syntax_Valid_1");
	}
	
	@Test
	public void test_TryCatch_Valid_1() {
		util.exec("TryCatch_Valid_1");
	}

	@Test
	public void test_TryCatch_Valid_2() {
		util.exec("TryCatch_Valid_2");
	}

	@Test
	public void test_TryCatch_Valid_3() {
		util.exec("TryCatch_Valid_3");
	}

	@Test
	public void test_TryCatch_Valid_4() {
		util.exec("TryCatch_Valid_4");
	}

	@Test
	public void test_TupleType_Valid_1() {
		util.exec("TupleType_Valid_1");
	}

	@Test
	public void test_TupleType_Valid_2() {
		util.exec("TupleType_Valid_2");
	}

	@Test
	public void test_TupleType_Valid_3() {
		util.exec("TupleType_Valid_3");
	}

	@Test
	public void test_TupleType_Valid_4() {
		util.exec("TupleType_Valid_4");
	}

	@Test
	public void test_TupleType_Valid_5() {
		util.exec("TupleType_Valid_5");
	}

	@Test
	public void test_TupleType_Valid_6() {
		util.exec("TupleType_Valid_6");
	}

	@Test
	public void test_TupleType_Valid_7() {
		util.exec("TupleType_Valid_7");
	}

	@Test
	public void test_TupleType_Valid_8() {
		util.exec("TupleType_Valid_8");
	}

	@Test
	public void test_TypeEquals_Valid_1() {
		util.exec("TypeEquals_Valid_1");
	}

	@Test
	public void test_TypeEquals_Valid_10() {
		util.exec("TypeEquals_Valid_10");
	}

	@Test
	public void test_TypeEquals_Valid_11() {
		util.exec("TypeEquals_Valid_11");
	}

	@Test
	public void test_TypeEquals_Valid_12() {
		util.exec("TypeEquals_Valid_12");
	}

	@Test
	public void test_TypeEquals_Valid_13() {
		util.exec("TypeEquals_Valid_13");
	}

	@Test
	public void test_TypeEquals_Valid_14() {
		util.exec("TypeEquals_Valid_14");
	}

	@Test
	public void test_TypeEquals_Valid_15() {
		util.exec("TypeEquals_Valid_15");
	}

	@Test
	public void test_TypeEquals_Valid_16() {
		util.exec("TypeEquals_Valid_16");
	}

	@Test
	public void test_TypeEquals_Valid_17() {
		util.exec("TypeEquals_Valid_17");
	}

	@Test
	public void test_TypeEquals_Valid_18() {
		util.exec("TypeEquals_Valid_18");
	}

	@Test
	public void test_TypeEquals_Valid_19() {
		util.exec("TypeEquals_Valid_19");
	}

	@Test
	public void test_TypeEquals_Valid_2() {
		util.exec("TypeEquals_Valid_2");
	}

	@Test
	public void test_TypeEquals_Valid_20() {
		util.exec("TypeEquals_Valid_20");
	}

	@Test
	public void test_TypeEquals_Valid_21() {
		util.exec("TypeEquals_Valid_21");
	}

	@Test
	public void test_TypeEquals_Valid_22() {
		util.exec("TypeEquals_Valid_22");
	}

	/**
	 * .\TypeEquals_Valid_23.whiley:11: expecting int or real or char, found TypeEquals_Valid_23:expr
        return e + 1
               ^
wycc.lang.SyntaxError: expecting int or real or char, found TypeEquals_Valid_23:expr
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)
	at wyc.builder.FlowTypeChecker.checkSuptypes(FlowTypeChecker.java:4133)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1883)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1660)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:797)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:327)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:767)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)

	 */
	@Ignore("Issue ???\n"
			+ ".\\TypeEquals_Valid_23.whiley:11: expecting int or real or char, found TypeEquals_Valid_23:expr\r\n" + 
			"        return e + 1\r\n" + 
			"               ^\r\n" + 
			"wycc.lang.SyntaxError: expecting int or real or char, found TypeEquals_Valid_23:expr\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)\r\n" + 
			"	at wyc.builder.FlowTypeChecker.checkSuptypes(FlowTypeChecker.java:4133)")
	@Test
	public void test_TypeEquals_Valid_23() {
		util.exec("TypeEquals_Valid_23");
	}

	@Test
	public void test_TypeEquals_Valid_24() {
		util.exec("TypeEquals_Valid_24");
	}

	@Test
	public void test_TypeEquals_Valid_25() {
		util.exec("TypeEquals_Valid_25");
	}

	@Test
	public void test_TypeEquals_Valid_26() {
		util.exec("TypeEquals_Valid_26");
	}

	@Test
	public void test_TypeEquals_Valid_27() {
		util.exec("TypeEquals_Valid_27");
	}

	@Test
	public void test_TypeEquals_Valid_28() {
		util.exec("TypeEquals_Valid_28");
	}

	@Test
	public void test_TypeEquals_Valid_29() {
		util.exec("TypeEquals_Valid_29");
	}
/**
 * .\TypeEquals_Valid_3.whiley:6: unknown variable
    r = []
    ^
wycc.lang.SyntaxError: unknown variable
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1021)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:462)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:325)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)

 */
	@Ignore("Issue ???\n"
			+ ".\\TypeEquals_Valid_3.whiley:6: unknown variable\r\n" + 
			"    r = []\r\n" + 
			"    ^\r\n" + 
			"wycc.lang.SyntaxError: unknown variable\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)")
	@Test
	public void test_TypeEquals_Valid_3() {
		util.exec("TypeEquals_Valid_3");
	}

	@Test
	public void test_TypeEquals_Valid_30() {
		util.exec("TypeEquals_Valid_30");
	}

	@Test
	public void test_TypeEquals_Valid_31() {
		util.exec("TypeEquals_Valid_31");
	}

	@Test
	public void test_TypeEquals_Valid_32() {
		util.exec("TypeEquals_Valid_32");
	}

	@Test
	public void test_TypeEquals_Valid_33() {
		util.exec("TypeEquals_Valid_33");
	}

	@Test
	public void test_TypeEquals_Valid_34() {
		util.exec("TypeEquals_Valid_34");
	}

	@Test
	public void test_TypeEquals_Valid_35() {
		util.exec("TypeEquals_Valid_35");
	}
/**
 * .\TypeEquals_Valid_36.whiley:10: found int|[int], expected string, set, list or dictionary.
    if (t.lhs is plist) && ((|t.lhs| > 0) && (t.lhs[0] == 0)):
                              ^^^^^
wycc.lang.SyntaxError: found int|[int], expected string, set, list or dictionary.
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2173)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1691)
	at wyc.builder.FlowTypeChecker.resolveLeafCondition(FlowTypeChecker.java:1458)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1356)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.resolveNonLeafCondition(FlowTypeChecker.java:1396)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1345)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.resolveNonLeafCondition(FlowTypeChecker.java:1398)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1345)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:755)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
 */
	@Ignore("Issue ???\n"
			+ ".\\TypeEquals_Valid_36.whiley:10: found int|[int], expected string, set, list or dictionary.\r\n" + 
			"    if (t.lhs is plist) && ((|t.lhs| > 0) && (t.lhs[0] == 0)):\r\n" + 
			"                              ^^^^^\r\n" + 
			"wycc.lang.SyntaxError: found int|[int], expected string, set, list or dictionary.\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)")
	@Test
	public void test_TypeEquals_Valid_36() {
		util.exec("TypeEquals_Valid_36");
	}
/**
 * .\TypeEquals_Valid_37.whiley:10: found int|[int], expected string, set, list or dictionary.
    if (t.lhs is plist) && ((|t.lhs| > 0) && (t.lhs[0] == 0)):
                              ^^^^^
wycc.lang.SyntaxError: found int|[int], expected string, set, list or dictionary.
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2173)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1691)
	at wyc.builder.FlowTypeChecker.resolveLeafCondition(FlowTypeChecker.java:1458)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1356)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.resolveNonLeafCondition(FlowTypeChecker.java:1396)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1345)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.resolveNonLeafCondition(FlowTypeChecker.java:1398)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1345)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:755)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)

 */
	@Ignore("Issue ???\n"
			+ ".\\TypeEquals_Valid_37.whiley:10: found int|[int], expected string, set, list or dictionary.\r\n" + 
			"    if (t.lhs is plist) && ((|t.lhs| > 0) && (t.lhs[0] == 0)):\r\n" + 
			"                              ^^^^^\r\n" + 
			"wycc.lang.SyntaxError: found int|[int], expected string, set, list or dictionary.\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)")
	@Test
	public void test_TypeEquals_Valid_37() {
		util.exec("TypeEquals_Valid_37");
	}

	/***
	 * .\TypeEquals_Valid_38.whiley:8: found int|[int], expected string, set, list or dictionary.
    if (t.lhs is [int]) && ((|t.lhs| > 0) && (t.lhs[0] == 0)):
                              ^^^^^
wycc.lang.SyntaxError: found int|[int], expected string, set, list or dictionary.
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:2173)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:1691)
	at wyc.builder.FlowTypeChecker.resolveLeafCondition(FlowTypeChecker.java:1458)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1356)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.resolveNonLeafCondition(FlowTypeChecker.java:1396)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1345)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.resolveNonLeafCondition(FlowTypeChecker.java:1398)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1345)
	at wyc.builder.FlowTypeChecker.propagateCondition(FlowTypeChecker.java:1263)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:755)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)
	 */
	@Ignore("Issue ???\n"
			+ ".\\TypeEquals_Valid_38.whiley:8: found int|[int], expected string, set, list or dictionary.\r\n" + 
			"    if (t.lhs is [int]) && ((|t.lhs| > 0) && (t.lhs[0] == 0)):\r\n" + 
			"                              ^^^^^\r\n" + 
			"wycc.lang.SyntaxError: found int|[int], expected string, set, list or dictionary.\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.lang.WhileyFile.syntaxError(WhileyFile.java:620)")
	@Test
	public void test_TypeEquals_Valid_38() {
		util.exec("TypeEquals_Valid_38");
	}

	@Test
	public void test_TypeEquals_Valid_39() {
		util.exec("TypeEquals_Valid_39");
	}

	@Test
	public void test_TypeEquals_Valid_40() {
		util.exec("TypeEquals_Valid_40");
	}
/**
 * .\TypeEquals_Valid_41.whiley:11: expected type int, found TypeEquals_Valid_41:expr
        return e
               ^
wycc.lang.SyntaxError: expected type int, found TypeEquals_Valid_41:expr
	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)
	at wyc.builder.FlowTypeChecker.checkIsSubtype(FlowTypeChecker.java:4060)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:799)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:327)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:767)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:329)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:298)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:264)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:145)
	at wyc.builder.FlowTypeChecker.propagate(FlowTypeChecker.java:135)
	at wyc.builder.WhileyBuilder.build(WhileyBuilder.java:181)
	at wybs.util.StdBuildRule.apply(StdBuildRule.java:109)
	at wybs.util.StdProject.build(StdProject.java:256)
	at wyc.util.WycBuildTask.buildEntries(WycBuildTask.java:503)
	at wyc.util.WycBuildTask.build(WycBuildTask.java:471)
	at wyopcl.WyopclMain.run(WyopclMain.java:119)
	at wyopcl.WyopclMain.main(WyopclMain.java:148)

 */
	@Ignore("Issue ???\n"
			+ ".\\TypeEquals_Valid_41.whiley:11: expected type int, found TypeEquals_Valid_41:expr\r\n" + 
			"        return e\r\n" + 
			"               ^\r\n" + 
			"wycc.lang.SyntaxError: expected type int, found TypeEquals_Valid_41:expr\r\n" + 
			"	at wycc.lang.SyntaxError.syntaxError(SyntaxError.java:238)\r\n" + 
			"	at wyc.builder.FlowTypeChecker.checkIsSubtype(FlowTypeChecker.java:4060)")
	@Test
	public void test_TypeEquals_Valid_41() {
		util.exec("TypeEquals_Valid_41");
	}

	@Test
	public void test_TypeEquals_Valid_42() {
		util.exec("TypeEquals_Valid_42");
	}

	@Test
	public void test_TypeEquals_Valid_43() {
		util.exec("TypeEquals_Valid_43");
	}

	@Test
	public void test_TypeEquals_Valid_44() {
		util.exec("TypeEquals_Valid_44");
	}

	@Test
	public void test_TypeEquals_Valid_45() {
		util.exec("TypeEquals_Valid_45");
	}

	@Test
	public void test_TypeEquals_Valid_46() {
		util.exec("TypeEquals_Valid_46");
	}

	@Test
	public void test_TypeEquals_Valid_47() {
		util.exec("TypeEquals_Valid_47");
	}

	@Test
	public void test_TypeEquals_Valid_5() {
		util.exec("TypeEquals_Valid_5");
	}

	@Test
	public void test_TypeEquals_Valid_6() {
		util.exec("TypeEquals_Valid_6");
	}

	@Test
	public void test_TypeEquals_Valid_7() {
		util.exec("TypeEquals_Valid_7");
	}

	@Test
	public void test_TypeEquals_Valid_8() {
		util.exec("TypeEquals_Valid_8");
	}

	@Test
	public void test_TypeEquals_Valid_9() {
		util.exec("TypeEquals_Valid_9");
	}
	
	@Test
	public void test_UnionType_Valid_1() {
		util.exec("UnionType_Valid_1");
	}

	@Test
	public void test_UnionType_Valid_10() {
		util.exec("UnionType_Valid_10");
	}

	@Test
	public void test_UnionType_Valid_11() {
		util.exec("UnionType_Valid_11");
	}

	@Test
	public void test_UnionType_Valid_12() {
		util.exec("UnionType_Valid_12");
	}

	@Test
	public void test_UnionType_Valid_13() {
		util.exec("UnionType_Valid_13");
	}

	@Test
	public void test_UnionType_Valid_14() {
		util.exec("UnionType_Valid_14");
	}

	@Test
	public void test_UnionType_Valid_15() {
		util.exec("UnionType_Valid_15");
	}

	@Test
	public void test_UnionType_Valid_16() {
		util.exec("UnionType_Valid_16");
	}

	@Test
	@Ignore("Issue ???")
	public void test_UnionType_Valid_17() {
		util.exec("UnionType_Valid_17");
	}

	@Test
	public void test_UnionType_Valid_18() {
		util.exec("UnionType_Valid_18");
	}

	@Test
	public void test_UnionType_Valid_19() {
		util.exec("UnionType_Valid_19");
	}

	@Test
	public void test_UnionType_Valid_2() {
		util.exec("UnionType_Valid_2");
	}

	@Test
	public void test_UnionType_Valid_20() {
		util.exec("UnionType_Valid_20");
	}

	@Test
	public void test_UnionType_Valid_21() {
		util.exec("UnionType_Valid_21");
	}

	@Test
	public void test_UnionType_Valid_22() {
		util.exec("UnionType_Valid_22");
	}

	@Test
	public void test_UnionType_Valid_23() {
		util.exec("UnionType_Valid_23");
	}

	@Test
	public void test_UnionType_Valid_3() {
		util.exec("UnionType_Valid_3");
	}

	@Test
	public void test_UnionType_Valid_4() {
		util.exec("UnionType_Valid_4");
	}

	@Test
	public void test_UnionType_Valid_5() {
		util.exec("UnionType_Valid_5");
	}

	@Test
	public void test_UnionType_Valid_6() {
		util.exec("UnionType_Valid_6");
	}

	@Test
	public void test_UnionType_Valid_7() {
		util.exec("UnionType_Valid_7");
	}

	@Test
	public void test_UnionType_Valid_8() {
		util.exec("UnionType_Valid_8");
	}

	@Test
	public void test_UnionType_Valid_9() {
		util.exec("UnionType_Valid_9");
	}

	@Ignore("Issue ???")
	@Test
	public void test_Update_Valid_1() {
		util.exec("Update_Valid_1");
	}
	@Ignore("Issue ???")
	@Test
	public void test_Update_Valid_2() {
		util.exec("Update_Valid_2");
	}
	
	@Test
	public void test_VarDecl_Valid_1() {
		util.exec("VarDecl_Valid_1");
	}

	@Test
	public void test_VarDecl_Valid_2() {
		util.exec("VarDecl_Valid_2");
	}

	@Test
	public void test_VarDecl_Valid_3() {
		util.exec("VarDecl_Valid_3");
	}

	@Test
	public void test_VarDecl_Valid_4() {
		util.exec("VarDecl_Valid_4");
	}

	@Test
	public void test_While_Valid_1() {
		util.exec("While_Valid_1");
	}

	@Test
	public void test_While_Valid_10() {
		util.exec("While_Valid_10");
	}

	@Test
	public void test_While_Valid_11() {
		util.exec("While_Valid_11");
	}

	@Test
	public void test_While_Valid_12() {
		util.exec("While_Valid_12");
	}

	@Test
	public void test_While_Valid_14() {
		util.exec("While_Valid_14");
	}

	@Test
	public void test_While_Valid_15() {
		util.exec("While_Valid_15");
	}

	@Test
	public void test_While_Valid_16() {
		util.exec("While_Valid_16");
	}

	@Test
	public void test_While_Valid_17() {
		util.exec("While_Valid_17");
	}

	@Test
	public void test_While_Valid_18() {
		util.exec("While_Valid_18");
	}

	@Test
	public void test_While_Valid_19() {
		util.exec("While_Valid_19");
	}

	@Test
	public void test_While_Valid_2() {
		util.exec("While_Valid_2");
	}

	@Test
	public void test_While_Valid_20() {
		util.exec("While_Valid_20");
	}

	@Test
	public void test_While_Valid_21() {
		util.exec("While_Valid_21");
	}

	@Test
	public void test_While_Valid_22() {
		util.exec("While_Valid_22");
	}

	@Test
	public void test_While_Valid_23() {
		util.exec("While_Valid_23");
	}

	@Test
	public void test_While_Valid_24() {
		util.exec("While_Valid_24");
	}

	@Test
	public void test_While_Valid_25() {
		util.exec("While_Valid_25");
	}
	
	@Test
	public void test_While_Valid_26() {
		util.exec("While_Valid_26");
	}

	@Test
	public void test_While_Valid_27() {
		util.exec("While_Valid_27");
	}

	@Test
	public void test_While_Valid_28() {
		util.exec("While_Valid_28");
	}

	@Test
	public void test_While_Valid_29() {
		util.exec("While_Valid_29");
	}

	@Test
	public void test_While_Valid_3() {
		util.exec("While_Valid_3");
	}
	
	@Test
	public void test_While_Valid_30() {
		util.exec("While_Valid_30");
	}

	@Test
	public void test_While_Valid_31() {
		util.exec("While_Valid_31");
	}

	@Test
	public void test_While_Valid_32() {
		util.exec("While_Valid_32");
	}

	@Test
	public void test_While_Valid_33() {
		util.exec("While_Valid_33");
	}

	@Test
	public void test_While_Valid_34() {
		util.exec("While_Valid_34");
	}

	@Test	
	public void test_While_Valid_35() {
		util.exec("While_Valid_35");
	}

	@Test	
	public void test_While_Valid_36() {
		util.exec("While_Valid_36");
	}

	@Test
	public void test_While_Valid_37() {
		util.exec("While_Valid_37");
	}

	@Test
	public void test_While_Valid_38() {
		util.exec("While_Valid_38");
	}
	
	@Test
	public void test_While_Valid_39() {
		util.exec("While_Valid_39");
	}
	
	@Test
	public void test_While_Valid_40() {
		util.exec("While_Valid_40");
	}
	
	@Test
	public void test_While_Valid_41() {
		util.exec("While_Valid_41");
	}
	
	@Test
	public void test_While_Valid_42() {
		util.exec("While_Valid_42");
	}
	
	@Test
	public void test_While_Valid_43() {
		util.exec("While_Valid_43");
	}

	public void test_While_Valid_5() {
		util.exec("While_Valid_5");
	}

	public void test_While_Valid_7() {
		util.exec("While_Valid_7");
	}

	@Test
	public void test_While_Valid_9() {
		util.exec("While_Valid_9");
	}

}