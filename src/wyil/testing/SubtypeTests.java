// This file was automatically generated.
package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class SubtypeTests {
	@Test public void test_1() {
		checkIsSubtype("any","any");
	}
	@Test public void test_2() {
		checkIsSubtype("any","null");
	}
	@Test public void test_3() {
		checkIsSubtype("any","int");
	}
	@Test public void test_4() {
		checkIsSubtype("any","[void]");
	}
	@Test public void test_5() {
		checkIsSubtype("any","[any]");
	}
	@Test public void test_6() {
		checkIsSubtype("any","[null]");
	}
	@Test public void test_7() {
		checkIsSubtype("any","[int]");
	}
	@Test public void test_8() {
		checkNotSubtype("null","any");
	}
	@Test public void test_9() {
		checkIsSubtype("null","null");
	}
	@Test public void test_10() {
		checkNotSubtype("null","int");
	}
	@Test public void test_11() {
		checkNotSubtype("null","[void]");
	}
	@Test public void test_12() {
		checkNotSubtype("null","[any]");
	}
	@Test public void test_13() {
		checkNotSubtype("null","[null]");
	}
	@Test public void test_14() {
		checkNotSubtype("null","[int]");
	}
	@Test public void test_15() {
		checkNotSubtype("int","any");
	}
	@Test public void test_16() {
		checkNotSubtype("int","null");
	}
	@Test public void test_17() {
		checkIsSubtype("int","int");
	}
	@Test public void test_18() {
		checkNotSubtype("int","[void]");
	}
	@Test public void test_19() {
		checkNotSubtype("int","[any]");
	}
	@Test public void test_20() {
		checkNotSubtype("int","[null]");
	}
	@Test public void test_21() {
		checkNotSubtype("int","[int]");
	}
	@Test public void test_22() {
		checkNotSubtype("[void]","any");
	}
	@Test public void test_23() {
		checkNotSubtype("[void]","null");
	}
	@Test public void test_24() {
		checkNotSubtype("[void]","int");
	}
	@Test public void test_25() {
		checkIsSubtype("[void]","[void]");
	}
	@Test public void test_26() {
		checkNotSubtype("[void]","[any]");
	}
	@Test public void test_27() {
		checkNotSubtype("[void]","[null]");
	}
	@Test public void test_28() {
		checkNotSubtype("[void]","[int]");
	}
	@Test public void test_29() {
		checkNotSubtype("[any]","any");
	}
	@Test public void test_30() {
		checkNotSubtype("[any]","null");
	}
	@Test public void test_31() {
		checkNotSubtype("[any]","int");
	}
	@Test public void test_32() {
		checkIsSubtype("[any]","[void]");
	}
	@Test public void test_33() {
		checkIsSubtype("[any]","[any]");
	}
	@Test public void test_34() {
		checkIsSubtype("[any]","[null]");
	}
	@Test public void test_35() {
		checkIsSubtype("[any]","[int]");
	}
	@Test public void test_36() {
		checkNotSubtype("[null]","any");
	}
	@Test public void test_37() {
		checkNotSubtype("[null]","null");
	}
	@Test public void test_38() {
		checkNotSubtype("[null]","int");
	}
	@Test public void test_39() {
		checkIsSubtype("[null]","[void]");
	}
	@Test public void test_40() {
		checkNotSubtype("[null]","[any]");
	}
	@Test public void test_41() {
		checkIsSubtype("[null]","[null]");
	}
	@Test public void test_42() {
		checkNotSubtype("[null]","[int]");
	}
	@Test public void test_43() {
		checkNotSubtype("[int]","any");
	}
	@Test public void test_44() {
		checkNotSubtype("[int]","null");
	}
	@Test public void test_45() {
		checkNotSubtype("[int]","int");
	}
	@Test public void test_46() {
		checkIsSubtype("[int]","[void]");
	}
	@Test public void test_47() {
		checkNotSubtype("[int]","[any]");
	}
	@Test public void test_48() {
		checkNotSubtype("[int]","[null]");
	}
	@Test public void test_49() {
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
