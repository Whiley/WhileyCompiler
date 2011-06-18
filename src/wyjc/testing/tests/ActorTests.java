package wyjc.testing.tests;

import org.junit.Test;

import wyjc.testing.TestHarness;

public class ActorTests extends TestHarness {

	public ActorTests() {
		super("tests/actors/valid", "tests/actors/valid", "sysout");
	}

	@Test
	public void Parameter() {
		runTest("Parameter");
	}

	@Test
	public void Print_Async() {
		runTest("Print_Async");
	}

	@Test
	public void Print_Sync() {
		runTest("Print_Sync");
	}

	@Test
	public void Self_Method() {
		runTest("Self_Method1");
	}

	@Test
	public void Self_Method2() {
		runTest("Self_Method2");
	}

	@Test
	public void Variable() {
		runTest("Variable1");
	}

	@Test
	public void Variable2() {
		runTest("Variable2");
	}

}
