package wyil.testing;
import org.junit.*;
import static org.junit.Assert.*;
import wyil.lang.Type;

public class SimplifyTests {
	@Test public void test_2() {
		checkSimplification("any");
	}
	@Test public void test_4() {
		checkSimplification("null");
	}
	@Test public void test_6() {
		checkSimplification("int");
	}
	@Test public void test_8() {
		checkSimplification("[void]");
	}
	@Test public void test_10() {
		checkSimplification("[any]");
	}
	@Test public void test_12() {
		checkSimplification("[null]");
	}
	@Test public void test_14() {
		checkSimplification("[int]");
	}
	@Test public void test_16() {
		checkSimplification("!void");
	}
	@Test public void test_18() {
		checkSimplification("!any");
	}
	@Test public void test_20() {
		checkSimplification("!null");
	}
	@Test public void test_22() {
		checkSimplification("!int");
	}
	@Test public void test_24() {
		checkSimplification("[[void]]");
	}
	@Test public void test_26() {
		checkSimplification("[[any]]");
	}
	@Test public void test_28() {
		checkSimplification("[[null]]");
	}
	@Test public void test_30() {
		checkSimplification("[[int]]");
	}
	@Test public void test_32() {
		checkSimplification("[!void]");
	}
	@Test public void test_34() {
		checkSimplification("[!any]");
	}
	@Test public void test_36() {
		checkSimplification("[!null]");
	}
	@Test public void test_38() {
		checkSimplification("[!int]");
	}
	@Test public void test_40() {
		checkSimplification("void&void");
	}
	@Test public void test_42() {
		checkSimplification("void&any");
	}
	@Test public void test_44() {
		checkSimplification("void&null");
	}
	@Test public void test_46() {
		checkSimplification("void&int");
	}
	@Test public void test_48() {
		checkSimplification("any&void");
	}
	@Test public void test_50() {
		checkSimplification("any&any");
	}
	@Test public void test_52() {
		checkSimplification("any&null");
	}
	@Test public void test_54() {
		checkSimplification("any&int");
	}
	@Test public void test_56() {
		checkSimplification("null&void");
	}
	@Test public void test_58() {
		checkSimplification("null&any");
	}
	@Test public void test_60() {
		checkSimplification("null&null");
	}
	@Test public void test_62() {
		checkSimplification("null&int");
	}
	@Test public void test_64() {
		checkSimplification("int&void");
	}
	@Test public void test_66() {
		checkSimplification("int&any");
	}
	@Test public void test_68() {
		checkSimplification("int&null");
	}
	@Test public void test_70() {
		checkSimplification("int&int");
	}
	@Test public void test_72() {
		checkSimplification("[void]&void");
	}
	@Test public void test_74() {
		checkSimplification("[any]&any");
	}
	@Test public void test_76() {
		checkSimplification("[null]&null");
	}
	@Test public void test_78() {
		checkSimplification("[int]&int");
	}
	@Test public void test_80() {
		checkSimplification("!void&void");
	}
	@Test public void test_82() {
		checkSimplification("!any&any");
	}
	@Test public void test_84() {
		checkSimplification("!null&null");
	}
	@Test public void test_86() {
		checkSimplification("!int&int");
	}
	@Test public void test_88() {
		checkSimplification("![void]");
	}
	@Test public void test_90() {
		checkSimplification("![any]");
	}
	@Test public void test_92() {
		checkSimplification("![null]");
	}
	@Test public void test_94() {
		checkSimplification("![int]");
	}
	@Test public void test_96() {
		checkSimplification("!!void");
	}
	@Test public void test_98() {
		checkSimplification("!!any");
	}
	@Test public void test_100() {
		checkSimplification("!!null");
	}
	@Test public void test_102() {
		checkSimplification("!!int");
	}

	private void checkSimplification(String from) {
		Type type = Type.fromString(from);
		Type simplified = Type.normalise(type);
		assertTrue(Type.isSubtype(type,simplified));
		assertTrue(Type.isSubtype(simplified,type));
	}
}
