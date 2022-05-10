// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyc.testing;

import static org.junit.Assert.fail;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wycc.lang.Syntactic;
import wycc.util.*;
import wycc.util.testing.*;
import wycc.util.testing.TestFile.Error;
import wycc.util.testing.TestManager.Result;
import wyil.interpreter.Interpreter;
import wyil.lang.WyilFile.Attr.SyntaxError;
import wyc.lang.WhileyFile;
import wyc.util.ErrorMessages;
import wyc.util.ErrorMessages.Message;
import wyc.util.testing.*;

/**
 * Run through all valid test cases with verification enabled. Since every test
 * file is valid, a successful test occurs when the compiler succeeds and, when
 * executed, the compiled file produces the expected output. Note that an
 * internal failure does not count as a valid pass, and indicates the test
 * exposed some kind of compiler bug.
 *
 * @author David J. Pearce
 *
 */
@RunWith(Parameterized.class)
public class WhileyCompilerTests {
	/**
	 * The directory containing the source files for each test case. Every test
	 * corresponds to a file in this directory.
	 */
	public final static String WHILEY_SRC_DIR = "tests";

	public final static TestManager manager = new TestManager(Path.of(WHILEY_SRC_DIR), new CompileTest(), new ExecuteTest());

	// ======================================================================
	// Test Harness
	// ======================================================================

	protected void runTest(Trie path) throws IOException {
		TestManager.Result r = manager.run(path);
		//
		if(r == Result.IGNORED) {
			Assume.assumeTrue("Test " + path + " skipped", false);
		} else if(r == Result.FAILURE) {
			fail("test failure for reasons unknown");
		} else if(r == Result.INVALIDIGNORED) {
			fail("test should not be marked as ignored");
		}
	}

	// ======================================================================
	// Tests
	// ======================================================================

	// Parameter to test case is the name of the current test.
	// It will be passed to the constructor by JUnit.
	private final Trie testName;

	public WhileyCompilerTests(String testName) {
		this.testName = Trie.fromString(testName);
	}

	// Here we enumerate all available test cases.
	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Util.findTestFiles(WHILEY_SRC_DIR);
	}

	@Test
	public void valid() throws IOException {
		runTest(this.testName);
	}
}
