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
import wycc.util.testing.TestFile;
import wyil.interpreter.Interpreter;
import wyil.lang.WyilFile.Attr.SyntaxError;
import wyc.lang.WhileyFile;
import wyc.util.ErrorMessages;
import wyc.util.ErrorMessages.Message;
import wyc.util.testing.ExecuteTest;
import wyc.util.testing.Util;

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
	 * @param testName Name of the test to run. This must correspond to a whiley
	 *                 source file in the <code>WHILEY_SRC_DIR</code> directory.
	 */
	protected void runTest(Trie path) throws IOException {
		Path srcDir = Path.of(WHILEY_SRC_DIR);
		TestFile tf = readTestFile(srcDir.toFile(), path);
		// Check whether this test should be ignored or not.
		boolean ignored = tf.get(Boolean.class, "build.whiley.ignore").orElse(false);
		boolean strict = tf.get(Boolean.class, "build.whiley.strict").orElse(false);
		// NOTE: if we get here, then ignored == false
		Path testDir = srcDir.resolve(path.toPath());
		forceDelete(testDir);
		testDir.toFile().mkdirs();
		int index = 0;
		HashMap<Trie, TextFile> state = new HashMap<>();
		try {
			for (TestFile.Frame f : tf) {
				// Filter available markers (ignoring those requiring verification)
				TestFile.Error[] markers = filterStaticMarkers(f.markers);
				MailBox.Buffered<SyntaxError> handler = new MailBox.Buffered<>();
				// Construct frame directory
				Path frameDir = testDir.resolve("_" + index);
				index++;
				// Mirror state in frame directory
				f.apply(state);
				mirror(state, frameDir);
				// Configure compiler
				wyc.Compiler wyc = new wyc.Compiler().setWhileyDir(frameDir.toFile()).setWyilDir(frameDir.toFile())
						.setTarget(path).setErrorHandler(handler).setStrict(strict);
				// Add source files
				for (Trie sf : state.keySet()) {
					sf = Trie.fromString(sf.toString().replace(".whiley", ""));
					wyc.addSource(sf);
				}
				// Check whether build succeeded
				boolean compiled = wyc.run();
				// Check whether build should have succeeded
				boolean shouldCompile = (markers.length == 0);
				// Interpreter what happened
				if (compiled && shouldCompile) {
					TestFile.Error[] runtime_markers = filterRuntimeMarkers(f.markers);
					TestFile.Error[] actual = new TestFile.Error[0];
					// Test was expected to compile, so attempt to run the code.
					String unit = tf.get(String.class, "main.file").orElse("main");
					try {
						ExecuteTest.execWyil(frameDir.toFile(), path, Trie.fromString(unit));
					} catch (Interpreter.RuntimeError e) {
						actual = new TestFile.Error[1];
						actual[0] = toError(state, e);
					}
					compareReportedErrors(actual, runtime_markers, ignored);
				} else if (compiled) {
					// Yes, test file indicates it should be ignored (for whatever reason).
					Assume.assumeTrue("Test " + path + " skipped", !ignored);
					fail("Test should not have compiled!");
				} else if (!compiled && !shouldCompile) {
					TestFile.Error[] actual = handler.stream().map(se -> toError(state, se))
							.toArray(TestFile.Error[]::new);
					compareReportedErrors(actual, markers, ignored);
				} else {
					Assume.assumeTrue("Test " + path + " skipped", !ignored);
					// Report errors
					for (SyntaxError syserr : handler) {
						TestFile.Error err = toError(state, syserr);
						TextFile sf = state.get(err.getFilename());
						System.out.println("E" + syserr.getErrorCode() + ": " + syserr.getMessage());
						printLineHighlight(System.out, err.getLocation(), sf);
					}
					// Yes, test file indicates it should be ignored (for whatever reason).
					fail("Test should have compiled!");
				}
			}
		} catch (Throwable e) {
			Assume.assumeTrue("Test " + path + " skipped", !ignored);
			throw e;
		} finally {
			// Finally clean up if we get here. Otherwise, leave files in place for
			// debugging.
			forceDelete(testDir);
		}
	}

	private static void printLineHighlight(PrintStream output, TestFile.Coordinate loc, TextFile sourceFile) {
		// Extract line text
		String text = sourceFile.getLine(loc.line);
		// Determine start and end of span
		int start = loc.range.start;
		int end = Math.min(text.length() - 1, loc.range.end);
		// Print out line text
		output.println(text);
		// First, mirror indentation
		String str = "";
		for (int i = 0; i < start; ++i) {
			if (text.charAt(i) == '\t') {
				str += "\t";
			} else {
				str += " ";
			}
		}
		// Second, place highlights
		for (int i = start; i <= end; ++i) {
			str += "^";
		}
		output.println(str);
	}

	/**
	 * Filter out markers which cannot be discovered using static checks in WyC. For
	 * example, markers which can only be detected with more sophisticated
	 * verification techniques.
	 *
	 * @param markers
	 * @return
	 */
	private static TestFile.Error[] filterStaticMarkers(TestFile.Error[] markers) {
		return Arrays.asList(markers).stream().filter(m -> m.getErrorNumber() < 700).toArray(TestFile.Error[]::new);
	}

	/**
	 * Filter out markers which do not arise at runtime.
	 *
	 * @param markers
	 * @return
	 */
	private static TestFile.Error[] filterRuntimeMarkers(TestFile.Error[] markers) {
		return Arrays.asList(markers).stream().filter(m -> m.getErrorNumber() < 716).toArray(TestFile.Error[]::new);
	}

	/**
	 * Convert a syntax error into a testfile error, so that we can perform a simple
	 * and direct comparison. This is not completely straightforward because syntax
	 * errors are in terms of "spans", whilst testfile errors are in terms of
	 * "coordinates" and we have to convert between them.
	 *
	 * @param files
	 * @param err
	 * @return
	 */
	public static TestFile.Error toError(Map<Trie, TextFile> files, SyntaxError err) {
		int errno = err.getErrorCode();
		// Identify enclosing source file
		Trie filename = Trie.fromString(err.getSource().toString() + ".whiley");
		// Determine the source-file span for the given syntactic marker.
		Syntactic.Span span = err.getTarget().getAncestor(AbstractCompilationUnit.Attribute.Span.class);
		// Extract source file
		TextFile sf = files.get(filename);
		// Extract enclosing line
		TextFile.Line l = sf.getEnclosingLine(span.getStart());
		int line;
		TestFile.Range range;
		//
		if (l != null) {
			// Convert space into coordinate
			int start = span.getStart() - l.getOffset();
			int end = span.getEnd() - l.getOffset();
			//
			line = l.getNumber();
			range = new TestFile.Range(start, end);
		} else {
			line = 1;
			range = new TestFile.Range(0, 1);
		}
		TestFile.Coordinate location = new TestFile.Coordinate(line, range);
		// Done
		return new TestFile.Error(errno, filename, location);
	}

	public static TestFile.Error toError(Map<Trie, TextFile> files, Interpreter.RuntimeError err) {
		int errno = err.getErrorCode();
		// Identify enclosing source file
		Trie filename = Trie.fromString(err.getSource().toString() + ".whiley");
		// Determine the source-file span for the given syntactic marker.
		Syntactic.Span span = err.getElement().getAncestor(AbstractCompilationUnit.Attribute.Span.class);
		// Extract source file
		TextFile sf = files.get(filename);
		// Extract enclosing line
		TextFile.Line l = sf.getEnclosingLine(span.getStart());
		// Convert space into coordinate
		int start = span.getStart() - l.getOffset();
		int end = span.getEnd() - l.getOffset();
		//
		TestFile.Range range = new TestFile.Range(start, end);
		TestFile.Coordinate location = new TestFile.Coordinate(l.getNumber(), range);
		// Done
		return new TestFile.Error(errno, filename, location);
	}

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

	public static TestFile readTestFile(File dir, Trie path) throws IOException {
		// First, determine filename
		String filename = path.toNativeString() + ".test";
		// Second Read the file
		try (FileInputStream fin = new FileInputStream(new File(dir, filename))) {
			return TestFile.parse(fin);
		}
	}

	public static void compareReportedErrors(TestFile.Error[] expected, TestFile.Error[] actual, boolean ignored) {
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
		if (failed) {
			// Yes, test file indicates it should be ignored (for whatever reason).
			Assume.assumeTrue(!ignored);
			fail("incorrect errors reported");
		} else if(ignored) {
			fail("test need not be ignored!");
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
