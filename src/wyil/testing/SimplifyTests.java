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
		checkSimplification("X<[X]>");
	}
	@Test public void test_10() {
		checkSimplification("[void]");
	}
	@Test public void test_12() {
		checkSimplification("[any]");
	}
	@Test public void test_14() {
		checkSimplification("[null]");
	}
	@Test public void test_16() {
		checkSimplification("[int]");
	}
	@Test public void test_18() {
		checkSimplification("[X<[X]>]");
	}
	@Test public void test_20() {
		checkSimplification("X<X|void>");
	}
	@Test public void test_22() {
		checkSimplification("X<X|any>");
	}
	@Test public void test_24() {
		checkSimplification("X<X|null>");
	}
	@Test public void test_26() {
		checkSimplification("X<X|int>");
	}
	@Test public void test_28() {
		checkSimplification("X<X|Y<[Y]>>");
	}
	@Test public void test_30() {
		checkSimplification("[[void]]");
	}
	@Test public void test_32() {
		checkSimplification("[[any]]");
	}
	@Test public void test_34() {
		checkSimplification("[[null]]");
	}
	@Test public void test_36() {
		checkSimplification("[[int]]");
	}
	@Test public void test_38() {
		checkSimplification("[[X<[X]>]]");
	}
	@Test public void test_40() {
		checkSimplification("X<[[[X]]]>");
	}
	@Test public void test_42() {
		checkSimplification("X<[[Y<X|Y>]]>");
	}
	@Test public void test_44() {
		checkSimplification("[X<X|void>]");
	}
	@Test public void test_46() {
		checkSimplification("[X<X|any>]");
	}
	@Test public void test_48() {
		checkSimplification("[X<X|null>]");
	}
	@Test public void test_50() {
		checkSimplification("[X<X|int>]");
	}
	@Test public void test_52() {
		checkSimplification("[X<X|Y<[Y]>>]");
	}
	@Test public void test_54() {
		checkSimplification("X<[Y<Y|[X]>]>");
	}
	@Test public void test_56() {
		checkSimplification("X<[Y<Y|(Z<X|Z>)>]>");
	}
	@Test public void test_58() {
		checkSimplification("void|void");
	}
	@Test public void test_60() {
		checkSimplification("void|any");
	}
	@Test public void test_62() {
		checkSimplification("void|null");
	}
	@Test public void test_64() {
		checkSimplification("void|int");
	}
	@Test public void test_66() {
		checkSimplification("void|X<[X]>");
	}
	@Test public void test_68() {
		checkSimplification("void|[void]");
	}
	@Test public void test_70() {
		checkSimplification("void|(X<void|X>)");
	}
	@Test public void test_72() {
		checkSimplification("any|void");
	}
	@Test public void test_74() {
		checkSimplification("any|any");
	}
	@Test public void test_76() {
		checkSimplification("any|null");
	}
	@Test public void test_78() {
		checkSimplification("any|int");
	}
	@Test public void test_80() {
		checkSimplification("any|X<[X]>");
	}
	@Test public void test_82() {
		checkSimplification("any|[any]");
	}
	@Test public void test_84() {
		checkSimplification("any|(X<any|X>)");
	}
	@Test public void test_86() {
		checkSimplification("null|void");
	}
	@Test public void test_88() {
		checkSimplification("null|any");
	}
	@Test public void test_90() {
		checkSimplification("null|null");
	}
	@Test public void test_92() {
		checkSimplification("null|int");
	}
	@Test public void test_94() {
		checkSimplification("null|X<[X]>");
	}
	@Test public void test_96() {
		checkSimplification("null|[null]");
	}
	@Test public void test_98() {
		checkSimplification("null|(X<null|X>)");
	}
	@Test public void test_100() {
		checkSimplification("int|void");
	}
	@Test public void test_102() {
		checkSimplification("int|any");
	}
	@Test public void test_104() {
		checkSimplification("int|null");
	}
	@Test public void test_106() {
		checkSimplification("int|int");
	}
	@Test public void test_108() {
		checkSimplification("int|X<[X]>");
	}
	@Test public void test_110() {
		checkSimplification("int|[int]");
	}
	@Test public void test_112() {
		checkSimplification("int|(X<int|X>)");
	}
	@Test public void test_114() {
		checkSimplification("[void]|void");
	}
	@Test public void test_116() {
		checkSimplification("X<[X]>|void");
	}
	@Test public void test_118() {
		checkSimplification("X<X|[void]>");
	}
	@Test public void test_120() {
		checkSimplification("[any]|any");
	}
	@Test public void test_122() {
		checkSimplification("X<[X]>|any");
	}
	@Test public void test_124() {
		checkSimplification("X<X|[any]>");
	}
	@Test public void test_126() {
		checkSimplification("[null]|null");
	}
	@Test public void test_128() {
		checkSimplification("X<[X]>|null");
	}
	@Test public void test_130() {
		checkSimplification("X<X|[null]>");
	}
	@Test public void test_132() {
		checkSimplification("[int]|int");
	}
	@Test public void test_134() {
		checkSimplification("X<[X]>|int");
	}
	@Test public void test_136() {
		checkSimplification("X<X|[int]>");
	}
	@Test public void test_138() {
		checkSimplification("[X<[X]>]|X<[X]>");
	}
	@Test public void test_140() {
		checkSimplification("X<[X]>|Y<[Y]>");
	}
	@Test public void test_142() {
		checkSimplification("X<[X]>|[X<[X]>]");
	}
	@Test public void test_144() {
		checkSimplification("X<X|[Y<[Y]>]>");
	}
	@Test public void test_146() {
		checkSimplification("X<X|[[X]]>");
	}
	@Test public void test_148() {
		checkSimplification("X<[X]>|(Y<X<[X]>|Y>)");
	}
	@Test public void test_150() {
		checkSimplification("X<X|[Y<X|Y>]>");
	}
	@Test public void test_152() {
		checkSimplification("(X<X|void>)|void");
	}
	@Test public void test_154() {
		checkSimplification("X<X|(Y<Y|void>)>");
	}
	@Test public void test_156() {
		checkSimplification("(X<X|any>)|any");
	}
	@Test public void test_158() {
		checkSimplification("X<X|(Y<Y|any>)>");
	}
	@Test public void test_160() {
		checkSimplification("(X<X|null>)|null");
	}
	@Test public void test_162() {
		checkSimplification("X<X|(Y<Y|null>)>");
	}
	@Test public void test_164() {
		checkSimplification("(X<X|int>)|int");
	}
	@Test public void test_166() {
		checkSimplification("X<X|(Y<Y|int>)>");
	}
	@Test public void test_168() {
		checkSimplification("(X<X|Y<[Y]>>)|Y<[Y]>");
	}
	@Test public void test_170() {
		checkSimplification("X<X|(Y<Y|Z<[Z]>>)>");
	}
	@Test public void test_172() {
		checkSimplification("X<X|(Y<Y|[X]>)>");
	}
	@Test public void test_174() {
		checkSimplification("X<X|(Y<Y|(Z<X|Z>)>)>");
	}

	private void checkSimplification(String from) {
		Type type = Type.fromString(from);
		Type simplified = Type.minimise(type);
		assertTrue(Type.isSubtype(type,simplified));
		assertTrue(Type.isSubtype(simplified,type));
	}
}
