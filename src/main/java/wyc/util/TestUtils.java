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
package wyc.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import wyc.command.Compile;
import wyc.command.Run;
import wyc.io.WhileyFileLexer;
import wyc.io.WhileyFileParser;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Type;
import wycc.util.Logger;
import wycc.util.Pair;
import wyfs.lang.Content;
import wyfs.lang.Path;

/**
 * Miscellaneous utilities related to the test harness. These are located here
 * in order that other plugins may use them.
 *
 * @author David J. Pearce
 *
 */
public class TestUtils {

	/**
	 * Parse a Whiley type from a string.
	 *
	 * @param from
	 * @return
	 */
	public static Type fromString(String from) {
		List<WhileyFileLexer.Token> tokens = new WhileyFileLexer(from).scan();
		WhileyFile wf = new WhileyFile(null);
		WhileyFileParser parser = new WhileyFileParser(wf, tokens);
		WhileyFileParser.EnclosingScope scope = parser.new EnclosingScope();
		return parser.parseType(scope);
	}

	/**
	 * Scan a directory to get the names of all the whiley source files
	 * in that directory. The list of file names can be used as input
	 * parameters to a JUnit test.
	 *
	 * If the system property <code>test.name.contains</code> is set,
	 * then the list of files returned will be filtered. Only file
	 * names that contain the property will be returned. This makes it
	 * possible to run a subset of tests when testing interactively
	 * from the command line.
	 *
	 * @param srcDir The path of the directory to scan.
	 */
	public static Collection<Object[]> findTestNames(String srcDir) {
		final String suffix = ".whiley";
		String containsFilter = System.getProperty("test.name.contains");

		ArrayList<Object[]> testcases = new ArrayList<>();
		for (File f : new File(srcDir).listFiles()) {
			// Check it's a file
			if (!f.isFile()) {
				continue;
			}
			String name = f.getName();
			// Check it's a whiley source file
			if (!name.endsWith(suffix)) {
				continue;
			}
			// Get rid of ".whiley" extension
			String testName = name.substring(0, name.length() - suffix.length());
			// If there's a filter, check the name matches
			if (containsFilter != null && !testName.contains(containsFilter)) {
				continue;
			}
			testcases.add(new Object[] { testName });
		}
		// Sort the result by filename
		Collections.sort(testcases, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					return ((String) o1[0]).compareTo((String) o2[0]);
				}
		});
		return testcases;
	}

	/**
	 * Run the Whiley Compiler with the given list of arguments.
	 *
	 * @param args
	 *            --- list of command-line arguments to provide to the Whiley
	 *            Compiler.
	 * @return
	 * @throws IOException
	 */
	public static Pair<Compile.Result,String> compile(File whileydir, boolean verify, String... args) throws IOException {
		ByteArrayOutputStream syserr = new ByteArrayOutputStream();
		ByteArrayOutputStream sysout = new ByteArrayOutputStream();
		Content.Registry registry = new wyc.Activator.Registry();
		Compile cmd = new Compile(registry,Logger.NULL,sysout,syserr);
		cmd.setWhileydir(whileydir);
		cmd.setWyaldir(whileydir); //
		cmd.setVerify(verify);
//		cmd.setVerbose(true);
		Compile.Result result = cmd.execute(args);
		byte[] errBytes = syserr.toByteArray();
		byte[] outBytes = sysout.toByteArray();
		String output = new String(errBytes) + new String(outBytes);
		return new Pair<>(result,output);
	}

	/**
	 * Execute a given WyIL file using the default interpreter.
	 *
	 * @param wyilDir
	 *            The root directory to look for the WyIL file.
	 * @param id
	 *            The name of the WyIL file
	 * @throws IOException
	 */
	public static void execWyil(File wyilDir, Path.ID id) throws IOException {
		Content.Registry registry = new wyc.Activator.Registry();
		Run cmd = new Run(registry,Logger.NULL);
		cmd.setWyildir(wyilDir);
		cmd.execute(id.toString(),"test");
	}

	/**
	 * Compare the output of executing java on the test case with a reference
	 * file. If the output differs from the reference output, then the offending
	 * line is written to the stdout and an exception is thrown.
	 *
	 * @param output
	 *            This provides the output from executing java on the test case.
	 * @param referenceFile
	 *            The full path to the reference file. This should use the
	 *            appropriate separator char for the host operating system.
	 * @throws IOException
	 */
	public static boolean compare(String output, String referenceFile) throws IOException {
		BufferedReader outReader = new BufferedReader(new StringReader(output));
		BufferedReader refReader = new BufferedReader(new FileReader(new File(referenceFile)));

		boolean match = true;
		while (true) {
			String l1 = refReader.readLine();
			String l2 = outReader.readLine();
			if (l1 != null && l2 != null) {
				if (!l1.equals(l2)) {
					System.err.println(" < " + l1);
					System.err.println(" > " + l2);
					match = false;
				}
			} else if (l1 != null) {
				System.err.println(" < " + l1);
				match = false;
			} else if (l2 != null) {
				System.err.println(" > " + l2);
				match = false;
			} else {
				break;
			}
		}
		if (!match) {
			System.err.println();
			return false;
		}
		return true;
	}

	/**
	 * Grab everything produced by a given input stream until the End-Of-File
	 * (EOF) is reached. This is implemented as a separate thread to ensure that
	 * reading from other streams can happen concurrently. For example, we can
	 * read concurrently from <code>stdin</code> and <code>stderr</code> for
	 * some process without blocking that process.
	 *
	 * @author David J. Pearce
	 *
	 */
	static public class StreamGrabber extends Thread {
		private InputStream input;
		private StringBuffer buffer;

		public StreamGrabber(InputStream input, StringBuffer buffer) {
			this.input = input;
			this.buffer = buffer;
			start();
		}

		@Override
		public void run() {
			try {
				int nextChar;
				// keep reading!!
				while ((nextChar = input.read()) != -1) {
					buffer.append((char) nextChar);
				}
			} catch (IOException ioe) {
			}
		}
	}
}
