// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class SubtypeTests {
	@Test public void test_1() { checkIsSubtype("any","any"); }
	@Test public void test_2() { checkIsSubtype("any","null"); }
	@Test public void test_3() { checkIsSubtype("any","void(void)"); }
	@Test public void test_4() { checkIsSubtype("any","void(any)"); }
	@Test public void test_5() { checkIsSubtype("any","void(null)"); }
	@Test public void test_6() { checkIsSubtype("any","any(void)"); }
	@Test public void test_7() { checkIsSubtype("any","any(any)"); }
	@Test public void test_8() { checkIsSubtype("any","any(null)"); }
	@Test public void test_9() { checkIsSubtype("any","null(void)"); }
	@Test public void test_10() { checkIsSubtype("any","null(any)"); }
	@Test public void test_11() { checkIsSubtype("any","null(null)"); }
	@Test public void test_12() { checkNotSubtype("null","any"); }
	@Test public void test_13() { checkIsSubtype("null","null"); }
	@Test public void test_14() { checkIsSubtype("null","void(void)"); }
	@Test public void test_15() { checkIsSubtype("null","void(any)"); }
	@Test public void test_16() { checkIsSubtype("null","void(null)"); }
	@Test public void test_17() { checkIsSubtype("null","any(void)"); }
	@Test public void test_18() { checkNotSubtype("null","any(any)"); }
	@Test public void test_19() { checkNotSubtype("null","any(null)"); }
	@Test public void test_20() { checkIsSubtype("null","null(void)"); }
	@Test public void test_21() { checkNotSubtype("null","null(any)"); }
	@Test public void test_22() { checkNotSubtype("null","null(null)"); }
	@Test public void test_23() { checkNotSubtype("void(void)","any"); }
	@Test public void test_24() { checkNotSubtype("void(void)","null"); }
	@Test public void test_25() { checkIsSubtype("void(void)","void(void)"); }
	@Test public void test_26() { checkIsSubtype("void(void)","void(any)"); }
	@Test public void test_27() { checkIsSubtype("void(void)","void(null)"); }
	@Test public void test_28() { checkIsSubtype("void(void)","any(void)"); }
	@Test public void test_29() { checkNotSubtype("void(void)","any(any)"); }
	@Test public void test_30() { checkNotSubtype("void(void)","any(null)"); }
	@Test public void test_31() { checkIsSubtype("void(void)","null(void)"); }
	@Test public void test_32() { checkNotSubtype("void(void)","null(any)"); }
	@Test public void test_33() { checkNotSubtype("void(void)","null(null)"); }
	@Test public void test_34() { checkNotSubtype("void(any)","any"); }
	@Test public void test_35() { checkNotSubtype("void(any)","null"); }
	@Test public void test_36() { checkIsSubtype("void(any)","void(void)"); }
	@Test public void test_37() { checkIsSubtype("void(any)","void(any)"); }
	@Test public void test_38() { checkIsSubtype("void(any)","void(null)"); }
	@Test public void test_39() { checkIsSubtype("void(any)","any(void)"); }
	@Test public void test_40() { checkNotSubtype("void(any)","any(any)"); }
	@Test public void test_41() { checkNotSubtype("void(any)","any(null)"); }
	@Test public void test_42() { checkIsSubtype("void(any)","null(void)"); }
	@Test public void test_43() { checkNotSubtype("void(any)","null(any)"); }
	@Test public void test_44() { checkNotSubtype("void(any)","null(null)"); }
	@Test public void test_45() { checkNotSubtype("void(null)","any"); }
	@Test public void test_46() { checkNotSubtype("void(null)","null"); }
	@Test public void test_47() { checkIsSubtype("void(null)","void(void)"); }
	@Test public void test_48() { checkIsSubtype("void(null)","void(any)"); }
	@Test public void test_49() { checkIsSubtype("void(null)","void(null)"); }
	@Test public void test_50() { checkIsSubtype("void(null)","any(void)"); }
	@Test public void test_51() { checkNotSubtype("void(null)","any(any)"); }
	@Test public void test_52() { checkNotSubtype("void(null)","any(null)"); }
	@Test public void test_53() { checkIsSubtype("void(null)","null(void)"); }
	@Test public void test_54() { checkNotSubtype("void(null)","null(any)"); }
	@Test public void test_55() { checkNotSubtype("void(null)","null(null)"); }
	@Test public void test_56() { checkNotSubtype("any(void)","any"); }
	@Test public void test_57() { checkNotSubtype("any(void)","null"); }
	@Test public void test_58() { checkIsSubtype("any(void)","void(void)"); }
	@Test public void test_59() { checkIsSubtype("any(void)","void(any)"); }
	@Test public void test_60() { checkIsSubtype("any(void)","void(null)"); }
	@Test public void test_61() { checkIsSubtype("any(void)","any(void)"); }
	@Test public void test_62() { checkNotSubtype("any(void)","any(any)"); }
	@Test public void test_63() { checkNotSubtype("any(void)","any(null)"); }
	@Test public void test_64() { checkIsSubtype("any(void)","null(void)"); }
	@Test public void test_65() { checkNotSubtype("any(void)","null(any)"); }
	@Test public void test_66() { checkNotSubtype("any(void)","null(null)"); }
	@Test public void test_67() { checkNotSubtype("any(any)","any"); }
	@Test public void test_68() { checkNotSubtype("any(any)","null"); }
	@Test public void test_69() { checkIsSubtype("any(any)","void(void)"); }
	@Test public void test_70() { checkIsSubtype("any(any)","void(any)"); }
	@Test public void test_71() { checkIsSubtype("any(any)","void(null)"); }
	@Test public void test_72() { checkIsSubtype("any(any)","any(void)"); }
	@Test public void test_73() { checkIsSubtype("any(any)","any(any)"); }
	@Test public void test_74() { checkIsSubtype("any(any)","any(null)"); }
	@Test public void test_75() { checkIsSubtype("any(any)","null(void)"); }
	@Test public void test_76() { checkIsSubtype("any(any)","null(any)"); }
	@Test public void test_77() { checkIsSubtype("any(any)","null(null)"); }
	@Test public void test_78() { checkNotSubtype("any(null)","any"); }
	@Test public void test_79() { checkNotSubtype("any(null)","null"); }
	@Test public void test_80() { checkIsSubtype("any(null)","void(void)"); }
	@Test public void test_81() { checkIsSubtype("any(null)","void(any)"); }
	@Test public void test_82() { checkIsSubtype("any(null)","void(null)"); }
	@Test public void test_83() { checkIsSubtype("any(null)","any(void)"); }
	@Test public void test_84() { checkNotSubtype("any(null)","any(any)"); }
	@Test public void test_85() { checkIsSubtype("any(null)","any(null)"); }
	@Test public void test_86() { checkIsSubtype("any(null)","null(void)"); }
	@Test public void test_87() { checkNotSubtype("any(null)","null(any)"); }
	@Test public void test_88() { checkIsSubtype("any(null)","null(null)"); }
	@Test public void test_89() { checkNotSubtype("null(void)","any"); }
	@Test public void test_90() { checkNotSubtype("null(void)","null"); }
	@Test public void test_91() { checkIsSubtype("null(void)","void(void)"); }
	@Test public void test_92() { checkIsSubtype("null(void)","void(any)"); }
	@Test public void test_93() { checkIsSubtype("null(void)","void(null)"); }
	@Test public void test_94() { checkIsSubtype("null(void)","any(void)"); }
	@Test public void test_95() { checkNotSubtype("null(void)","any(any)"); }
	@Test public void test_96() { checkNotSubtype("null(void)","any(null)"); }
	@Test public void test_97() { checkIsSubtype("null(void)","null(void)"); }
	@Test public void test_98() { checkNotSubtype("null(void)","null(any)"); }
	@Test public void test_99() { checkNotSubtype("null(void)","null(null)"); }
	@Test public void test_100() { checkNotSubtype("null(any)","any"); }
	@Test public void test_101() { checkNotSubtype("null(any)","null"); }
	@Test public void test_102() { checkIsSubtype("null(any)","void(void)"); }
	@Test public void test_103() { checkIsSubtype("null(any)","void(any)"); }
	@Test public void test_104() { checkIsSubtype("null(any)","void(null)"); }
	@Test public void test_105() { checkIsSubtype("null(any)","any(void)"); }
	@Test public void test_106() { checkNotSubtype("null(any)","any(any)"); }
	@Test public void test_107() { checkNotSubtype("null(any)","any(null)"); }
	@Test public void test_108() { checkIsSubtype("null(any)","null(void)"); }
	@Test public void test_109() { checkIsSubtype("null(any)","null(any)"); }
	@Test public void test_110() { checkIsSubtype("null(any)","null(null)"); }
	@Test public void test_111() { checkNotSubtype("null(null)","any"); }
	@Test public void test_112() { checkNotSubtype("null(null)","null"); }
	@Test public void test_113() { checkIsSubtype("null(null)","void(void)"); }
	@Test public void test_114() { checkIsSubtype("null(null)","void(any)"); }
	@Test public void test_115() { checkIsSubtype("null(null)","void(null)"); }
	@Test public void test_116() { checkIsSubtype("null(null)","any(void)"); }
	@Test public void test_117() { checkNotSubtype("null(null)","any(any)"); }
	@Test public void test_118() { checkNotSubtype("null(null)","any(null)"); }
	@Test public void test_119() { checkIsSubtype("null(null)","null(void)"); }
	@Test public void test_120() { checkNotSubtype("null(null)","null(any)"); }
	@Test public void test_121() { checkIsSubtype("null(null)","null(null)"); }

	private void checkIsSubtype(String from, String to) {
		Type ft = Type.fromString(from);
		Type tt = Type.fromString(to);
		assertTrue(Type.isSubtype(ft,tt));
	}
	private void checkNotSubtype(String from, String to) {
		Type ft = Type.fromString(from);
		Type tt = Type.fromString(to);
		assertFalse(Type.isSubtype(ft,tt));
	}
}
