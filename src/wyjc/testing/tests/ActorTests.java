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
	public void Print_Async1() {
		runTest("Print_Async1");
	}
	
	@Test
	public void Print_Async2() {
		runTest("Print_Async2");
	}

	@Test
	public void Print_Sync1() {
		runTest("Print_Sync1");
	}
	
	@Test
	public void Print_Sync2() {
		runTest("Print_Sync2");
	}

	@Test
	public void Self_Method1() {
		runTest("Self_Method1");
	}

	@Test
	public void Self_Method2() {
		 runTest("Self_Method2");
	}

	@Test
	public void Variable1() {
		 runTest("Variable1");
	}

	@Test
	public void Variable2() {
		runTest("Variable2");
	}

}
