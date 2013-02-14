package wycs.testing.tests;

import org.junit.Test;

import wycs.testing.TestHarness;

public class ValidTests extends TestHarness {
	public ValidTests() {
		 super("tests/valid");
	 }
	
	@Test public void Test_Valid_001() { verifyPassTest("test_001"); }
	@Test public void Test_Valid_002() { verifyPassTest("test_002"); }
	@Test public void Test_Valid_003() { verifyPassTest("test_003"); }
	@Test public void Test_Valid_004() { verifyPassTest("test_004"); }	
	@Test public void Test_Valid_005() { verifyPassTest("test_005"); }	
	@Test public void Test_Valid_006() { verifyPassTest("test_006"); }	
	@Test public void Test_Valid_007() { verifyPassTest("test_007"); }
	@Test public void Test_Valid_008() { verifyPassTest("test_008"); }
	@Test public void Test_Valid_009() { verifyPassTest("test_009"); }
	@Test public void Test_Valid_010() { verifyPassTest("test_010"); }
}
