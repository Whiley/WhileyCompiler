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
package wycc.util.testing;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import wycc.util.TextFile;
import wycc.util.Trie;

/**
 * The test manager provides a generic framework for running the Whiley test
 * suite.
 *
 * @author David J. Pearce
 *
 */
public class TestManager {
	/**
	 * Identifies the location of the Whiley test files.
	 */
	private final Path srcDir;

	private final TestStage[] stages;
	/**
	 * Debug mode makes it easier to see what went wrong during a test run.
	 */
	private final boolean debug = false;

	public TestManager(Path testDir, TestStage... stages) {
		this.srcDir = testDir;
		this.stages = stages;
	}

	/**
	 * Run a given test.
	 *
	 * @param path
	 * @throws IOException
	 */
	public boolean run(Trie test) throws IOException {
		// Parse the target test file
		TestFile tf = readTestFile(srcDir, test);
		// Setup test directory
		Path testDir = setup(test);
		//
		try {
			// Iterate test frames
			int index = 0;
			HashMap<Trie, TextFile> state = new HashMap<>();
			for (TestFile.Frame f : tf) {
				// Apply frame to current state
				f.apply(state);
				// Determine frame directory
				Path frameDir = testDir.resolve("_" + index++);
				// Mirror state in frame directory
				mirror(state, frameDir);
				// Apply each stage generating errors as appropriate
				for (TestStage stage : stages) {
					// Apply stage producing a set of errors
					TestFile.Error[] actual = stage.apply(test, frameDir, state, tf);
					// Determine set of expected errors
					TestFile.Error[] expected = stage.filter(f.markers);
					// Check we got what we expected.
					boolean outcome = compareReportedErrors(expected, actual);
					//
					if (!outcome) {
						return false;
					}
				}

			}
		} finally {
			// Teat down test directory
			tearDown(testDir);
		}
		return true;
	}

	public static boolean compareReportedErrors(TestFile.Error[] expected, TestFile.Error[] actual) {
		boolean failed = false;
		// First, sort both error sets to make sure a fair comparison.
		Arrays.sort(expected);
		Arrays.sort(actual);
		// Implement a greedy diff
		int ei = 0;
		int ai = 0;
		while (ei < expected.length && ai < actual.length) {
			TestFile.Error eith = expected[ei];
			TestFile.Error aith = actual[ai];
			int c = eith.compareTo(aith);
			if (c == 0) {
				ei = ei + 1;
				ai = ai + 1;
			} else if (c < 0) {
				ei = ei + 1;
				failed = true;
				System.out.println(">>> " + eith);
			} else {
				ai = ai + 1;
				failed = true;
				System.out.println("<<< " + aith);
			}
		}
		// Sanity check whether anything was missing
		failed |= (ei < expected.length);
		failed |= (ai < actual.length);
		// Print expected
		for (; ei < expected.length; ei = ei + 1) {
			TestFile.Error eith = expected[ei];
			System.out.println(">>> " + eith);
		}
		// Print missing
		for (; ai < actual.length; ai = ai + 1) {
			TestFile.Error aith = actual[ai];
			System.out.println("<<< " + aith);
		}
		//
		return failed;
	}

	private Path setup(Trie path) throws IOException {
		// Determine the path of the testing directory.
		Path testDir = srcDir.resolve(path.toPath());
		// Make sure testing directory is removed!
		forceDelete(testDir);
		// Make all directories required for test
		testDir.toFile().mkdirs();
		//
		return testDir;
	}

	private void tearDown(Path testDir) throws IOException {
		// Finally clean up if we get here. Otherwise, leave files in place for
		// debugging.
		if(!debug) {
			forceDelete(testDir);
		}
	}

	/**
	 * Mirror the contents of a given test state in a given directory.
	 *
	 * @param state
	 * @param dir
	 * @throws IOException
	 */
	public static void mirror(Map<Trie, TextFile> state, Path dir) throws IOException {
		for (Map.Entry<Trie, TextFile> e : state.entrySet()) {
			Path p = dir.resolve(e.getKey().toPath());
			TextFile f = e.getValue();
			//
			p.getParent().toFile().mkdirs();
			try (FileOutputStream fout = new FileOutputStream(p.toFile())) {
				fout.write(f.getBytes(StandardCharsets.US_ASCII));
			}
		}
	}

	/**
	 * Force a directory and all its contents to be deleted.
	 *
	 * @param path
	 * @throws IOException
	 */
	public void forceDelete(Path path) throws IOException {
		if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
			try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
				for (Path entry : entries) {
					forceDelete(entry);
				}
			}
		}
		if (path.toFile().exists()) {
			Files.delete(path);
		}
	}

	/**
	 * Read a test file from a path.
	 *
	 * @param dir
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static TestFile readTestFile(Path dir, Trie path) throws IOException {
		// First, determine filename
		String filename = path.toNativeString() + ".test";
		// Second Read the file
		try (FileInputStream fin = new FileInputStream(new File(dir.toFile(), filename))) {
			return TestFile.parse(fin);
		}
	}
}
