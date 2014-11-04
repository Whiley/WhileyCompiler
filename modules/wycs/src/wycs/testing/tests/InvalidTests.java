package wycs.testing.tests;

import org.junit.Ignore;
import org.junit.Test;

import wycs.testing.TestHarness;

public class InvalidTests extends TestHarness {
	public InvalidTests() {
		 super("tests/invalid");
	 }

	@Test public void Test_Invalid_001() { verifyFailTest("test_001"); }
	@Test public void Test_Invalid_002() { verifyFailTest("test_002"); }
	@Test public void Test_Invalid_003() { verifyFailTest("test_003"); }
	@Test public void Test_Invalid_004() { verifyFailTest("test_004"); }
	@Test public void Test_Invalid_005() { verifyFailTest("test_005"); }
	@Test public void Test_Invalid_006() { verifyFailTest("test_006"); }
	@Test public void Test_Invalid_007() { verifyFailTest("test_007"); }
	@Test public void Test_Invalid_008() { verifyFailTest("test_008"); }
	@Test public void Test_Invalid_009() { verifyFailTest("test_009"); }
	@Test public void Test_Invalid_010() { verifyFailTest("test_010"); }
	@Test public void Test_Invalid_011() { verifyFailTest("test_011"); }
	@Ignore("Known Issue")
	@Test public void Test_Invalid_012() { verifyFailTest("test_012"); }
	@Test public void Test_Invalid_013() { verifyFailTest("test_013"); }
	@Test public void Test_Invalid_014() { verifyFailTest("test_014"); }
	@Test public void Test_Invalid_015() { verifyFailTest("test_015"); }
	@Test public void Test_Invalid_016() { verifyFailTest("test_016"); }
	@Test public void Test_Invalid_017() { verifyFailTest("test_017"); }
	@Test public void Test_Invalid_018() { verifyFailTest("test_018"); }
	@Test public void Test_Invalid_019() { verifyFailTest("test_019"); }
	@Test public void Test_Invalid_020() { verifyFailTest("test_020"); }

	@Test public void Test_Invalid_050() { verifyFailTest("test_050"); }
	@Test public void Test_Invalid_051() { verifyFailTest("test_051"); }
	@Test public void Test_Invalid_052() { verifyFailTest("test_052"); }
	@Test public void Test_Invalid_053() { verifyFailTest("test_053"); }
	@Test public void Test_Invalid_054() { verifyFailTest("test_054"); }
	@Test public void Test_Invalid_055() { verifyFailTest("test_055"); }
	@Test public void Test_Invalid_056() { verifyFailTest("test_056"); }
	@Test public void Test_Invalid_057() { verifyFailTest("test_057"); }
	@Test public void Test_Invalid_058() { verifyFailTest("test_058"); }
	@Test public void Test_Invalid_059() { verifyFailTest("test_059"); }
	@Test public void Test_Invalid_060() { verifyFailTest("test_060"); }
	@Test public void Test_Invalid_061() { verifyFailTest("test_061"); }
	@Test public void Test_Invalid_062() { verifyFailTest("test_062"); }
	@Test public void Test_Invalid_063() { verifyFailTest("test_063"); }
	@Test public void Test_Invalid_064() { verifyFailTest("test_064"); }
	@Test public void Test_Invalid_065() { verifyFailTest("test_065"); }
	@Test public void Test_Invalid_066() { verifyFailTest("test_066"); }

	@Test public void Test_Invalid_100() { verifyFailTest("test_100"); }
	@Test public void Test_Invalid_101() { verifyFailTest("test_101"); }
	@Test public void Test_Invalid_102() { verifyFailTest("test_102"); }
	@Test public void Test_Invalid_103() { verifyFailTest("test_103"); }
	@Test public void Test_Invalid_104() { verifyFailTest("test_104"); }
}
