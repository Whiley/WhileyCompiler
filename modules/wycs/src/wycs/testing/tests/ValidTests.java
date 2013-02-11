package wycs.testing.tests;

import org.junit.Test;

import wycs.testing.TestHarness;

public class ValidTests extends TestHarness {
	public ValidTests() {
		 super("tests/valid");
	 }
	
	@Test public void Test_Valid_001() { verifyPassTest("test_001"); }
}
