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

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import wycc.util.*;
import wycc.util.testing.TestFile;
import wyc.lang.WhileyFile;
import wyc.util.TestUtils;


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

	// ======================================================================
	// Test Harness
	// ======================================================================

	/**
	 * Compile a syntactically invalid test case with verification enabled. The
	 * expectation is that compilation should fail with an error and, hence, the
	 * test fails if compilation does not.
	 *
	 * @param testName
	 *            Name of the test to run. This must correspond to a whiley
	 *            source file in the <code>WHILEY_SRC_DIR</code> directory.
	 */
	protected void runTest(Trie path) throws IOException {
		Path srcDir = Path.of(WHILEY_SRC_DIR);
		TestFile tf = readTestFile(srcDir.toFile(), path);
		Path testDir = srcDir.resolve(path.toPath());
		forceDelete(testDir);
		testDir.toFile().mkdirs();
		int index = 0;
		HashMap<Trie, TextFile> state = new HashMap<>();
		for (TestFile.Frame f : tf) {
			// Construct frame directory
			Path frameDir = testDir.resolve("_" + index);
			index++;
			// Mirror state in frame directory
			f.apply(state);
			mirror(state, frameDir);
			// Configure compiler
			wyc.Compiler wyc = new wyc.Compiler().setWhileyDir(frameDir.toFile()).setWyilDir(frameDir.toFile())
					.setTarget(path).setBrief(true);
			// Add source files
			for (Trie sf : state.keySet()) {
				sf = Trie.fromString(sf.toString().replace(".whiley", ""));
				wyc.addSource(sf);
			}
			// Check whether build succeeded
			boolean compiled = wyc.run();
			// Check whether build should have succeeded
			boolean shouldCompile = (f.markers.length == 0);
			// Interpreter what happened
			if (compiled && shouldCompile) {
				// Test was expected to compile, so attempt to run the code.
				String unit = tf.get(String.class, "main.file").orElse("main");
				TestUtils.execWyil(frameDir.toFile(), path, Trie.fromString(unit));
			} else if (compiled) {
				fail("Test should not have compiled!");
			} else if (!compiled && !shouldCompile) {
				throw new IllegalArgumentException("NEED TO CHECK MARKERS");
			} else {
				fail("Test should have compiled!");
			}
		}
	}

	public static void mirror(Map<Trie,TextFile> state, Path dir) throws IOException {
		for (Map.Entry<Trie, TextFile> e : state.entrySet()) {
			Path p = dir.resolve(e.getKey().toPath());
			TextFile f = e.getValue();
			//
			p.getParent().toFile().mkdirs();
			try(FileOutputStream fout = new FileOutputStream(p.toFile())) {
				fout.write(f.getBytes(StandardCharsets.US_ASCII));
			}
		}
	}

	public void forceDelete(Path path) throws IOException {
		if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
				for (Path entry : entries) {
					forceDelete(entry);
				}
			}
		}
		Files.delete(path);
	}

	public static TestFile readTestFile(File dir, Trie path) throws IOException {
		// First, determine filename
		String filename = path.toNativeString() + ".test";
		// Second Read the file
		try (FileInputStream fin = new FileInputStream(new File(dir, filename))) {
			return TestFile.parse(fin);
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
		return TestUtils.findTestFiles(WHILEY_SRC_DIR);
	}

	@Test
	public void valid() throws IOException {
		runTest(this.testName);
	}
}
