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
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

import wyal.lang.WyalFile;
import wybs.lang.Build;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntacticException;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.SequentialBuildProject;
import wyc.io.WhileyFileLexer;
import wyc.io.WhileyFileParser;
import wyc.lang.WhileyFile;
import wyc.task.CompileTask;
import wycc.util.Logger;
import wycc.util.Pair;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.Trie;
import wyil.interpreter.ConcreteSemantics.RValue;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.QualifiedName;
import wyil.lang.WyilFile.Type;
import wyil.interpreter.Interpreter;

/**
 * Miscellaneous utilities related to the test harness. These are located here
 * in order that other plugins may use them.
 *
 * @author David J. Pearce
 *
 */
public class TestUtils {

	/**
	 * Default implementation of a content registry. This associates whiley and
	 * wyil files with their respective content types.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Registry implements Content.Registry {
		@Override
		public void associate(Path.Entry e) {
			String suffix = e.suffix();

			if (suffix.equals("whiley")) {
				e.associate(WhileyFile.ContentType, null);
			} else if (suffix.equals("wyil")) {
				e.associate(WyilFile.ContentType, null);
			} else if (suffix.equals("wyal")) {
				e.associate(WyalFile.ContentType, null);
			}
		}

		@Override
		public String suffix(Content.Type<?> t) {
			return t.getSuffix();
		}
	}

	/**
	 * Parse a Whiley type from a string.
	 *
	 * @param from
	 * @return
	 */
	public static Type fromString(String from) {
		List<WhileyFileLexer.Token> tokens = new WhileyFileLexer(from).scan();
		WyilFile wf = new WyilFile(null);
		WhileyFileParser parser = new WhileyFileParser(wf, new WhileyFile(tokens));
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
	 * Identifies which whiley source files should be considered for compilation. By
	 * default, all files reachable from srcdir are considered.
	 */
	private static Content.Filter<WhileyFile> whileyIncludes = Content.filter("**", WhileyFile.ContentType);
	/**
	 * Identifies which WyIL source files should be considered for verification. By
	 * default, all files reachable from srcdir are considered.
	 */
	private static Content.Filter<WyilFile> wyilIncludes = Content.filter("**", WyilFile.ContentType);
	/**
	 * Identifies which WyAL source files should be considered for verification. By
	 * default, all files reachable from srcdir are considered.
	 */
	private static Content.Filter<WyalFile> wyalIncludes = Content.filter("**", WyalFile.ContentType);
	/**
	 * A simple default registry which knows about whiley files and wyil files.
	 */
	private static final Content.Registry registry = new Registry();

	/**
	 * Run the Whiley Compiler with the given list of arguments.
	 *
	 * @param args --- list of tests to compile.
	 * @return
	 * @throws IOException
	 */
	public static Pair<Boolean, String> compile(File whileydir, boolean verify, boolean counterexamples, String arg)
			throws IOException {
		ByteArrayOutputStream syserr = new ByteArrayOutputStream();
		ByteArrayOutputStream sysout = new ByteArrayOutputStream();
		PrintStream psyserr = new PrintStream(syserr);
		//
		boolean result = true;
		//
		try {
			// Construct the project
			DirectoryRoot root = new DirectoryRoot(whileydir, registry);
			SequentialBuildProject project = new SequentialBuildProject(root);
			// Identify source files
			Pair<Path.Entry<WhileyFile>,Path.Entry<WyilFile>> p = findSourceFiles(root,arg);
			List<Path.Entry<WhileyFile>> sources = Arrays.asList(p.first());
			Path.Entry<WyilFile> target = p.second();
			// Add build rule
			project.add(new Build.Rule() {
				@Override
				public void apply(Collection<Build.Task> tasks) throws IOException {
					// Construct a new build task
					CompileTask task = new CompileTask(project, root, target, sources).setVerification(verify)
							.setCounterExamples(counterexamples);
					// Submit the task for execution
					tasks.add(task);
				}
			});
			// Actually force the project to build
			project.build(ForkJoinPool.commonPool());
			// Flush any created resources (e.g. wyil files)
			root.flush();
			// Check whether any syntax error produced
			result = !findSyntaxErrors(target.read().getRootItem(), new BitSet());
			// FIXME: this seems quite broken.
			wycc.commands.Build.printSyntacticMarkers(psyserr, (List) sources, (Path.Entry) target);
		} catch (SyntacticException e) {
			// Print out the syntax error
			e.outputSourceError(psyserr,false);
			result = false;
		} catch (Exception e) {
			// Print out the syntax error
			e.printStackTrace(psyserr);
			result = false;
		}
		//
		psyserr.flush();
		// Convert bytes produced into resulting string.
		byte[] errBytes = syserr.toByteArray();
		byte[] outBytes = sysout.toByteArray();
		String output = new String(errBytes) + new String(outBytes);
		return new Pair<>(result, output);
	}

	/**
	 * Following method is something of a kludge as no easy way at the moment to
	 * tell when a build has failed.
	 *
	 * @param item
	 * @param items
	 * @param visited
	 * @return
	 */
	public static boolean findSyntaxErrors(SyntacticItem item, BitSet visited) {
		int index = item.getIndex();
		// Check whether already visited this item
		if(!visited.get(index)) {
			visited.set(index);
			// Check whether this item has a marker associated with it.
			if (item instanceof WyilFile.SyntaxError) {
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

	/**
	 * For each test, identify the corresponding Whiley file entry in the source
	 * root.
	 *
	 * @param root
	 * @param args
	 * @return
	 * @throws IOException
	 */
	public static Pair<Path.Entry<WhileyFile>, Path.Entry<WyilFile>> findSourceFiles(Path.Root root, String arg)
			throws IOException {
		Path.ID id = Trie.fromString(arg);
		Path.Entry<WhileyFile> source = root.get(id, WhileyFile.ContentType);
		if (source == null) {
			throw new IllegalArgumentException("file not found: " + arg);
		}
		// Construct target
		Path.Entry<WyilFile> target = root.get(id, WyilFile.ContentType);
		// Doesn't exist, so create with default value
		target = root.create(id, WyilFile.ContentType);
		WyilFile wf = new WyilFile(target);
		target.write(wf);
		// Create initially empty WyIL module.
		wf.setRootItem(new WyilFile.Decl.Module(new Name(id), new Tuple<>(), new Tuple<>(), new Tuple<>()));
		// Done
		return new Pair<>(source, target);
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
	public static void execWyil(File wyildir, Path.ID id) throws IOException {
		Path.Root root = new DirectoryRoot(wyildir, registry);
		// Empty signature
		Type.Method sig = new Type.Method(new Tuple<>(new Type[0]), new Tuple<>(), new Tuple<>(), new Tuple<>());
		QualifiedName name = new QualifiedName(new Name(id), new Identifier("test"));
		// Try to run the given function or method
		Interpreter interpreter = new Interpreter(System.out);
		// Create the initial stack
		Interpreter.CallStack stack = interpreter.new CallStack();
		// Load the relevant WyIL module
		stack.load(root.get(id, WyilFile.ContentType).read());
		//
		RValue[] returns = interpreter.execute(name, sig, stack);
		// Print out any return values produced
		if (returns != null) {
			for (int i = 0; i != returns.length; ++i) {
				if (i != 0) {
					System.out.println(", ");
				}
				System.out.println(returns[i]);
			}
		}
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
