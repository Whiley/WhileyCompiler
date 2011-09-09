// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class SubtypeTests {
	@Test public void test_1() {
		checkIsSubtype("void","void");
	}
	@Test public void test_2() {
		checkNotSubtype("void","any");
	}
	@Test public void test_3() {
		checkNotSubtype("void","null");
	}
	@Test public void test_4() {
		checkNotSubtype("void","int");
	}
	@Test public void test_5() {
		checkNotSubtype("void","[void]");
	}
	@Test public void test_6() {
		checkNotSubtype("void","[any]");
	}
	@Test public void test_7() {
		checkNotSubtype("void","[null]");
	}
	@Test public void test_8() {
		checkNotSubtype("void","[int]");
	}
	@Test public void test_9() {
		checkIsSubtype("any","void");
	}
	@Test public void test_10() {
		checkIsSubtype("any","any");
	}
	@Test public void test_11() {
		checkIsSubtype("any","null");
	}
	@Test public void test_12() {
		checkIsSubtype("any","int");
	}
	@Test public void test_13() {
		checkIsSubtype("any","[void]");
	}
	@Test public void test_14() {
		checkIsSubtype("any","[any]");
	}
	@Test public void test_15() {
		checkIsSubtype("any","[null]");
	}
	@Test public void test_16() {
		checkIsSubtype("any","[int]");
	}
	@Test public void test_17() {
		checkNotSubtype("null","void");
	}
	@Test public void test_18() {
		checkNotSubtype("null","any");
	}
	@Test public void test_19() {
		checkIsSubtype("null","null");
	}
	@Test public void test_20() {
		checkNotSubtype("null","int");
	}
	@Test public void test_21() {
		checkNotSubtype("null","[void]");
	}
	@Test public void test_22() {
		checkNotSubtype("null","[any]");
	}
	@Test public void test_23() {
		checkNotSubtype("null","[null]");
	}
	@Test public void test_24() {
		checkNotSubtype("null","[int]");
	}
	@Test public void test_25() {
		checkNotSubtype("int","void");
	}
	@Test public void test_26() {
		checkNotSubtype("int","any");
	}
	@Test public void test_27() {
		checkNotSubtype("int","null");
	}
	@Test public void test_28() {
		checkIsSubtype("int","int");
	}
	@Test public void test_29() {
		checkNotSubtype("int","[void]");
	}
	@Test public void test_30() {
		checkNotSubtype("int","[any]");
	}
	@Test public void test_31() {
		checkNotSubtype("int","[null]");
	}
	@Test public void test_32() {
		checkNotSubtype("int","[int]");
	}
	@Test public void test_33() {
		checkNotSubtype("[void]","void");
	}
	@Test public void test_34() {
		checkNotSubtype("[void]","any");
	}
	@Test public void test_35() {
		checkNotSubtype("[void]","null");
	}
	@Test public void test_36() {
		checkNotSubtype("[void]","int");
	}
	@Test public void test_37() {
		checkIsSubtype("[void]","[void]");
	}
	@Test public void test_38() {
		checkNotSubtype("[void]","[any]");
	}
	@Test public void test_39() {
		checkNotSubtype("[void]","[null]");
	}
	@Test public void test_40() {
		checkNotSubtype("[void]","[int]");
	}
	@Test public void test_41() {
		checkNotSubtype("[any]","void");
	}
	@Test public void test_42() {
		checkNotSubtype("[any]","any");
	}
	@Test public void test_43() {
		checkNotSubtype("[any]","null");
	}
	@Test public void test_44() {
		checkNotSubtype("[any]","int");
	}
	@Test public void test_45() {
		checkIsSubtype("[any]","[void]");
	}
	@Test public void test_46() {
		checkIsSubtype("[any]","[any]");
	}
	@Test public void test_47() {
		checkIsSubtype("[any]","[null]");
	}
	@Test public void test_48() {
		checkIsSubtype("[any]","[int]");
	}
	@Test public void test_49() {
		checkNotSubtype("[null]","void");
	}
	@Test public void test_50() {
		checkNotSubtype("[null]","any");
	}
	@Test public void test_51() {
		checkNotSubtype("[null]","null");
	}
	@Test public void test_52() {
		checkNotSubtype("[null]","int");
	}
	@Test public void test_53() {
		checkNotSubtype("[null]","[void]");
	}
	@Test public void test_54() {
		checkNotSubtype("[null]","[any]");
	}
	@Test public void test_55() {
		checkIsSubtype("[null]","[null]");
	}
	@Test public void test_56() {
		checkNotSubtype("[null]","[int]");
	}
	@Test public void test_57() {
		checkNotSubtype("[int]","void");
	}
	@Test public void test_58() {
		checkNotSubtype("[int]","any");
	}
	@Test public void test_59() {
		checkNotSubtype("[int]","null");
	}
	@Test public void test_60() {
		checkNotSubtype("[int]","int");
	}
	@Test public void test_61() {
		checkNotSubtype("[int]","[void]");
	}
	@Test public void test_62() {
		checkNotSubtype("[int]","[any]");
	}
	@Test public void test_63() {
		checkNotSubtype("[int]","[null]");
	}
	@Test public void test_64() {
		checkIsSubtype("[int]","[int]");
	}

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
