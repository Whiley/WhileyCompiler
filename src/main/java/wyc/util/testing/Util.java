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
package wyc.util.testing;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import wycc.util.Trie;
import wycc.lang.Syntactic;
import wycc.util.AbstractCompilationUnit.Identifier;
import wycc.util.AbstractCompilationUnit.Name;
import wyc.Compiler;
import wyc.io.WhileyFileParser;
import wyc.lang.WhileyFile;
import wyil.interpreter.ConcreteSemantics.RValue;
import wyil.interpreter.Interpreter;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.*;

/**
 * Miscellaneous utilities related to the test harness. These are located here
 * in order that other plugins may use them.
 *
 * @author David J. Pearce
 *
 */
public class Util {

	/**
	 * Parse a Whiley type from a string.
	 *
	 * @param from
	 * @return
	 */
	public static Type fromString(String from) {
		Trie id = Trie.fromString("main");
		WhileyFile sf = new WhileyFile(id,from.getBytes());
		WyilFile wf = new WyilFile(Arrays.asList(sf));
		WhileyFileParser parser = new WhileyFileParser(wf, sf);
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
			testcases.add(new Object[]{testName});
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
	 * Scan a directory to get the names of all the whiley test files
	 * in that directory. The list of file names can be used as input
	 * parameters to a JUnit test.
	 *
	 * @param srcDir The path of the directory to scan.
	 */
	public static Collection<Object[]> findTestFiles(String srcDir) {
		final String suffix = ".test";
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
			testcases.add(new Object[]{testName});
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
	 * Print a complete stack trace. This differs from Throwable.printStackTrace()
	 * in that it always prints all of the trace.
	 *
	 * @param out
	 * @param err
	 */
	public static void printStackTrace(PrintStream out, Throwable err) {
		out.println(err.getClass().getName() + ": " + err.getMessage());
		for (StackTraceElement ste : err.getStackTrace()) {
			out.println("\tat " + ste.toString());
		}
		if (err.getCause() != null) {
			out.print("Caused by: ");
			printStackTrace(out, err.getCause());
		}
	}

	/**
	 * Following method is something of a kludge as no easy way at the moment to
	 * tell when a build has failed.
	 *
	 * @param item
	 * @param visited
	 * @return
	 */
	public static boolean findSyntaxErrors(Syntactic.Item item, BitSet visited) {
		int index = item.getIndex();
		// Check whether already visited this item
		if(!visited.get(index)) {
			visited.set(index);
			// Check whether this item has a marker associated with it.
			if (item instanceof WyilFile.Attr.SyntaxError) {
				// At least one marked associated with item.
				return true;
			}
			// Recursive children looking for other syntactic markers
			for (int i = 0; i != item.size(); ++i) {
				if(findSyntaxErrors(item.get(i), visited)) {
					return true;
				}
			}
		}
		//
		return false;
	}
}
