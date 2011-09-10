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
		checkIsSubtype("any","X<[X]>");
	}
	@Test public void test_5() {
		checkIsSubtype("any","[void]");
	}
	@Test public void test_6() {
		checkIsSubtype("any","[any]");
	}
	@Test public void test_7() {
		checkIsSubtype("any","[null]");
	}
	@Test public void test_8() {
		checkIsSubtype("any","[int]");
	}
	@Test public void test_9() {
		checkIsSubtype("any","[X<[X]>]");
	}
	@Test public void test_10() {
		checkIsSubtype("any","X<X|void>");
	}
	@Test public void test_11() {
		checkIsSubtype("any","X<X|any>");
	}
	@Test public void test_12() {
		checkIsSubtype("any","X<X|null>");
	}
	@Test public void test_13() {
		checkIsSubtype("any","X<X|int>");
	}
	@Test public void test_14() {
		checkIsSubtype("any","X<X|Y<[Y]>>");
	}
	@Test public void test_15() {
		checkIsSubtype("any","[[void]]");
	}
	@Test public void test_16() {
		checkIsSubtype("any","[[any]]");
	}
	@Test public void test_17() {
		checkIsSubtype("any","[[null]]");
	}
	@Test public void test_18() {
		checkIsSubtype("any","[[int]]");
	}
	@Test public void test_19() {
		checkIsSubtype("any","[[X<[X]>]]");
	}
	@Test public void test_20() {
		checkIsSubtype("any","X<[[[X]]]>");
	}
	@Test public void test_21() {
		checkIsSubtype("any","X<[[Y<X|Y>]]>");
	}
	@Test public void test_22() {
		checkIsSubtype("any","[X<X|void>]");
	}
	@Test public void test_23() {
		checkIsSubtype("any","[X<X|any>]");
	}
	@Test public void test_24() {
		checkIsSubtype("any","[X<X|null>]");
	}
	@Test public void test_25() {
		checkIsSubtype("any","[X<X|int>]");
	}
	@Test public void test_26() {
		checkIsSubtype("any","[X<X|Y<[Y]>>]");
	}
	@Test public void test_27() {
		checkIsSubtype("any","X<[Y<Y|[X]>]>");
	}
	@Test public void test_28() {
		checkIsSubtype("any","X<[Y<Y|(Z<X|Z>)>]>");
	}
	@Test public void test_29() {
		checkIsSubtype("any","void|void");
	}
	@Test public void test_30() {
		checkIsSubtype("any","void|any");
	}
	@Test public void test_31() {
		checkIsSubtype("any","void|null");
	}
	@Test public void test_32() {
		checkIsSubtype("any","void|int");
	}
	@Test public void test_33() {
		checkIsSubtype("any","void|X<[X]>");
	}
	@Test public void test_34() {
		checkIsSubtype("any","void|[void]");
	}
	@Test public void test_35() {
		checkIsSubtype("any","void|(X<void|X>)");
	}
	@Test public void test_36() {
		checkIsSubtype("any","any|void");
	}
	@Test public void test_37() {
		checkIsSubtype("any","any|any");
	}
	@Test public void test_38() {
		checkIsSubtype("any","any|null");
	}
	@Test public void test_39() {
		checkIsSubtype("any","any|int");
	}
	@Test public void test_40() {
		checkIsSubtype("any","any|X<[X]>");
	}
	@Test public void test_41() {
		checkIsSubtype("any","any|[any]");
	}
	@Test public void test_42() {
		checkIsSubtype("any","any|(X<any|X>)");
	}
	@Test public void test_43() {
		checkIsSubtype("any","null|void");
	}
	@Test public void test_44() {
		checkIsSubtype("any","null|any");
	}
	@Test public void test_45() {
		checkIsSubtype("any","null|null");
	}
	@Test public void test_46() {
		checkIsSubtype("any","null|int");
	}
	@Test public void test_47() {
		checkIsSubtype("any","null|X<[X]>");
	}
	@Test public void test_48() {
		checkIsSubtype("any","null|[null]");
	}
	@Test public void test_49() {
		checkIsSubtype("any","null|(X<null|X>)");
	}
	@Test public void test_50() {
		checkIsSubtype("any","int|void");
	}
	@Test public void test_51() {
		checkIsSubtype("any","int|any");
	}
	@Test public void test_52() {
		checkIsSubtype("any","int|null");
	}
	@Test public void test_53() {
		checkIsSubtype("any","int|int");
	}
	@Test public void test_54() {
		checkIsSubtype("any","int|X<[X]>");
	}
	@Test public void test_55() {
		checkIsSubtype("any","int|[int]");
	}
	@Test public void test_56() {
		checkIsSubtype("any","int|(X<int|X>)");
	}
	@Test public void test_57() {
		checkIsSubtype("any","[void]|void");
	}
	@Test public void test_58() {
		checkIsSubtype("any","X<[X]>|void");
	}
	@Test public void test_59() {
		checkIsSubtype("any","X<X|[void]>");
	}
	@Test public void test_60() {
		checkIsSubtype("any","[any]|any");
	}
	@Test public void test_61() {
		checkIsSubtype("any","X<[X]>|any");
	}
	@Test public void test_62() {
		checkIsSubtype("any","X<X|[any]>");
	}
	@Test public void test_63() {
		checkIsSubtype("any","[null]|null");
	}
	@Test public void test_64() {
		checkIsSubtype("any","X<[X]>|null");
	}
	@Test public void test_65() {
		checkIsSubtype("any","X<X|[null]>");
	}
	@Test public void test_66() {
		checkIsSubtype("any","[int]|int");
	}
	@Test public void test_67() {
		checkIsSubtype("any","X<[X]>|int");
	}
	@Test public void test_68() {
		checkIsSubtype("any","X<X|[int]>");
	}
	@Test public void test_69() {
		checkIsSubtype("any","[X<[X]>]|X<[X]>");
	}
	@Test public void test_70() {
		checkIsSubtype("any","X<[X]>|Y<[Y]>");
	}
	@Test public void test_71() {
		checkIsSubtype("any","X<[X]>|[X<[X]>]");
	}
	@Test public void test_72() {
		checkIsSubtype("any","X<X|[Y<[Y]>]>");
	}
	@Test public void test_73() {
		checkIsSubtype("any","X<X|[[X]]>");
	}
	@Test public void test_74() {
		checkIsSubtype("any","X<[X]>|(Y<X<[X]>|Y>)");
	}
	@Test public void test_75() {
		checkIsSubtype("any","X<X|[Y<X|Y>]>");
	}
	@Test public void test_76() {
		checkIsSubtype("any","(X<X|void>)|void");
	}
	@Test public void test_77() {
		checkIsSubtype("any","X<X|(Y<Y|void>)>");
	}
	@Test public void test_78() {
		checkIsSubtype("any","(X<X|any>)|any");
	}
	@Test public void test_79() {
		checkIsSubtype("any","X<X|(Y<Y|any>)>");
	}
	@Test public void test_80() {
		checkIsSubtype("any","(X<X|null>)|null");
	}
	@Test public void test_81() {
		checkIsSubtype("any","X<X|(Y<Y|null>)>");
	}
	@Test public void test_82() {
		checkIsSubtype("any","(X<X|int>)|int");
	}
	@Test public void test_83() {
		checkIsSubtype("any","X<X|(Y<Y|int>)>");
	}
	@Test public void test_84() {
		checkIsSubtype("any","(X<X|Y<[Y]>>)|Y<[Y]>");
	}
	@Test public void test_85() {
		checkIsSubtype("any","X<X|(Y<Y|Z<[Z]>>)>");
	}
	@Test public void test_86() {
		checkIsSubtype("any","X<X|(Y<Y|[X]>)>");
	}
	@Test public void test_87() {
