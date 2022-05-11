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
import wycc.util.*;
import wycc.util.testing.TestFile.Error;

/**
 * The test manager provides a generic framework for running the Whiley test
 * suite.
 *
 * @author David J. Pearce
 *
 */
public class TestManager {
	public enum Result {
		SUCCESS, FAILURE, IGNORED, INVALIDIGNORED
	}
	/**
	 * Identifies the location of the Whiley test files.
	 */
	private final Path srcDir;

	private final TestStage[] stages;
	/**
	 * Debug mode makes it easier to see what went wrong during a test run.
	 */
	private boolean debug = false;

	public TestManager(Path testDir, TestStage... stages) {
		this.srcDir = testDir;
		this.stages = stages;
	}

	public TestManager setDebug(boolean flag) {
		this.debug = flag;
		return this;
	}

	/**
	 * Run a given test.
	 *
	 * @param path
	 * @throws IOException
	 */
	public Result run(Trie test) throws IOException {
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
					TestStage.Result result = stage.apply(test, frameDir, state, tf);
					// Extract actual errors
					Error[] actual = result.markers;
					// Determine set of expected errors
					Error[] expected = stage.filter(f.markers);
					// Check we got what we expected.
					Diff diff = compareReportedErrors(expected, actual);
					//
					if(diff.isEmpty() && result.ignored) {
						// In this case, the stage appears to have run correctly but is ignored. This
						// suggests it doesn't need to be ignored any more.
						return Result.INVALIDIGNORED;
					} else if(diff.isEmpty()) {
						// Stage completed successfully
						if(actual.length > 0) {
							// Errors have been produced, so we cannot continue with this frame.
							break;
						}
					} else if(result.ignored) {
						// In this case, the test has failed and it was correctly ignored. Therefore, it
						// should continue to be ignored.
						return Result.IGNORED;
					} else {
						// In this case, the test has failed so something is up.
						for(Error e : diff.missingExpected) {
							System.out.println("expected error: " + e);
						}
						for(Error e : diff.missingActual) {
							System.out.println("unexpected error: " + e);
						}
						return Result.FAILURE;
					}
				}

			}
		} finally {
			// Teat down test directory
			tearDown(testDir);
		}
		return Result.SUCCESS;
	}

	/**
	 * Compare the set of expected errors against the set of actual errors, noting
	 * any which are missing. This returns a pair containing those expected errors
	 * which were not matched, and those actual errors which were not matched.
	 *
	 * @param expected
	 * @param actual
	 * @return
	 */
	public static Diff compareReportedErrors(Error[] expected, Error[] actual) {
		ArrayList<Error> missingExpected = new ArrayList<>();
		ArrayList<Error> missingActual = new ArrayList<>();
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
				missingExpected.add(eith);
			} else {
				ai = ai + 1;
				missingActual.add(aith);
			}
		}
		// Print expected
		for (; ei < expected.length; ei = ei + 1) {
			TestFile.Error eith = expected[ei];
			missingExpected.add(eith);
		}
		// Print missing
		for (; ai < actual.length; ai = ai + 1) {
			TestFile.Error aith = actual[ai];
			missingActual.add(aith);
		}
		//

		return new Diff(missingExpected,missingActual);
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

	private static class Diff {
		public final Error[] missingExpected;
		public final Error[] missingActual;

		public Diff(List<Error> missingExpected, List<Error> missingActual) {
			this.missingExpected = missingExpected.toArray(new Error[missingExpected.size()]);
			this.missingActual = missingActual.toArray(new Error[missingActual.size()]);
		}

		public boolean isEmpty() {
			return missingExpected.length == 0 && missingActual.length == 0;
		}
	}
}
