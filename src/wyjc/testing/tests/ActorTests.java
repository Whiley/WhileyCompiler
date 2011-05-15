package wyjc.testing.tests;

import org.junit.Test;

import wyjc.testing.TestHarness;


public class ActorTests extends TestHarness {
	
	public ActorTests() {
		super("tests/actors", "tests/actors", "sysout");
	}
	
	@Test
	public void test() {
		runTest("test");
	}
	
}
