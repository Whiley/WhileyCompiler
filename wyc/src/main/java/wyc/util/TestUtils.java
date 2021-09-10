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
import java.util.*;

import wycc.lang.Build;
import wycc.lang.Content;
import wycc.lang.SyntacticException;
import wycc.lang.SyntacticItem;
import wycc.util.AbstractCompilationUnit.Identifier;
import wycc.util.AbstractCompilationUnit.Name;
import wycc.util.ByteRepository;
import wycc.util.DirectoryRoot;
import wycc.util.Pair;
import wycc.util.Transactions;
import wycc.util.Trie;
import wyc.io.WhileyFileParser;
import wyc.lang.WhileyFile;
import wyc.task.CompileTask;
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
public class TestUtils {
	private final static boolean DEBUG = false;
	//
	public final static Map<String, String> VALID_IGNORED = new HashMap<>();

	static {
		// Problem Type Checking Union Type
		VALID_IGNORED.put("RecordSubtype_Valid_1", "#696");
		VALID_IGNORED.put("RecordSubtype_Valid_2", "#696");
		// Semantics of Runtime Type Tests
		VALID_IGNORED.put("TypeEquals_Valid_61", "936");
		VALID_IGNORED.put("TypeEquals_Valid_62", "936");
		// FlowTyping over Logical Conditions
		VALID_IGNORED.put("Complex_Valid_3", "936");
		VALID_IGNORED.put("RecursiveType_Valid_12", "936");
		VALID_IGNORED.put("RecursiveType_Valid_30", "936");
		// Subtype Operator for Casting
		VALID_IGNORED.put("Coercion_Valid_9", "938");
		VALID_IGNORED.put("RecordCoercion_Valid_1", "938");
		// Separate out branch always taken 952
		VALID_IGNORED.put("ListAccess_Valid_6", "955");
		VALID_IGNORED.put("While_Valid_34", "955");
		// Type Refinement via Assignment
		VALID_IGNORED.put("Template_Valid_35", "977");
		VALID_IGNORED.put("Template_Valid_36", "977");
		VALID_IGNORED.put("Coercion_Valid_16", "977");
		//
		VALID_IGNORED.put("Reference_Valid_12", "986");
		VALID_IGNORED.put("Reference_Valid_14", "986");
		VALID_IGNORED.put("Reference_Valid_17", "986");
		VALID_IGNORED.put("Reference_Valid_18", "986");
		// Interpreter handling of tagged unions
		VALID_IGNORED.put("FunctionRef_Valid_13", "???");
		// Binding Against Union Types
		VALID_IGNORED.put("Template_Valid_19", "994");
		// Flow typing and loop invariants
		VALID_IGNORED.put("TypeEquals_Valid_3", "1000");
		// Flow typing and selectors
		VALID_IGNORED.put("RecordAssign_Valid_11", "1002");
		// Problems with strict subtyping
		VALID_IGNORED.put("Contractive_Valid_2", "1003");
		VALID_IGNORED.put("OpenRecord_Valid_9", "1003");
		VALID_IGNORED.put("Template_Valid_59", "1003");
		VALID_IGNORED.put("TypeEquals_Valid_28", "1003");
		VALID_IGNORED.put("TypeEquals_Valid_35", "1003");
		VALID_IGNORED.put("TypeEquals_Valid_46", "1003");
		VALID_IGNORED.put("TypeEquals_Valid_58", "1003");
		VALID_IGNORED.put("TypeEquals_Valid_59", "1003");
		VALID_IGNORED.put("UnionType_Valid_6", "1003");
		// Limitations of Type Inference
		VALID_IGNORED.put("Template_Valid_61", "1004");
		VALID_IGNORED.put("Template_Valid_62", "1004");
		// Interpreter Infinite Loop.
		VALID_IGNORED.put("Infeasible_Function_2", "???");
		VALID_IGNORED.put("Infeasible_Function_3", "???");
		VALID_IGNORED.put("Infeasible_Function_4", "???");
		// Type Invariant Failure (QuickCheck#10)
		VALID_IGNORED.put("RecordAssign_Valid_17", "???");
		// This test is fine, but the interpreter reports an error (correctly). In the
		// future, I imagine the test format can allow for this.
		VALID_IGNORED.put("Unsafe_Valid_6", "???");
	}

	/**
	 * Default implementation of a content registry. This associates whiley and
	 * wyil files with their respective content types.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Registry implements Content.Registry {

		@Override
		public String suffix(Content.Type<?> t) {
			return t.getSuffix();
		}

		@Override
		public wycc.lang.Content.Type<?> contentType(String suffix) {
			switch(suffix) {
			case "whiley":
				return WhileyFile.ContentType;
			case "wyil":
				return WyilFile.ContentType;
			}
			return null;
		}
	}

	/**
	 * Parse a Whiley type from a string.
	 *
	 * @param from
	 * @return
	 */
	public static Type fromString(String from) {
		Trie id = Trie.fromString("main");
		WhileyFile sf = new WhileyFile(id,from.getBytes());
		WyilFile wf = new WyilFile(id, Arrays.asList(sf));
		WhileyFileParser parser = new WhileyFileParser(wf, sf);
		WhileyFileParser.EnclosingScope scope = parser.new EnclosingScope(Build.NULL_METER);
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
	 * A simple default registry which knows about whiley files and wyil files.
	 */
	private static final Content.Registry registry = new Registry();

	/**
	 * Run the Whiley Compiler with the given list of arguments.
	 *
	 * @param whileydir --- list of tests to compile.
	 * @return
	 * @throws IOException
	 */
	public static Pair<Boolean, String> compile(File whileydir, boolean verify, boolean counterexamples, String arg)
			throws IOException {
		String filename = arg + ".whiley";
		ByteArrayOutputStream syserr = new ByteArrayOutputStream();
		PrintStream psyserr = new PrintStream(syserr);
		// Determine the ID of the test being compiler
		Trie path = Trie.fromString(arg);
		//
		boolean result = true;
		// Construct the directory root
		DirectoryRoot root = new DirectoryRoot(registry, whileydir, f -> {
			return f.getName().equals(filename);
		});
		//
		try {
			// Extract source file
			WhileyFile source = root.get(WhileyFile.ContentType, path);
			// Construct build repository
			Build.Repository repository = new ByteRepository(registry, source);
			// Construct compile task
			CompileTask task = new CompileTask(path, source);
			// Apply Whiley Compiler to repository
			repository.apply(Transactions.create(task));
			// Read out binary file from build repository
			WyilFile target = repository.get(WyilFile.ContentType, path);
			// Write binary file to directory
			root.put(path, target);
			// Check whether result valid (or not)
			result = target.isValid();
			// Print out syntactic markers
			wycli.commands.BuildCmd.printSyntacticMarkers(psyserr, target, source);
		} catch (SyntacticException e) {
			// Print out the syntax error
			//e.outputSourceError(psyserr);
			result = false;
		} catch (Exception e) {
			// Print out the syntax error
			printStackTrace(psyserr, e);
			result = false;
		} finally {
			// Writeback any results
			root.synchronise();
		}
		//
		psyserr.flush();
		// Convert bytes produced into resulting string.
		byte[] errBytes = syserr.toByteArray();
		String output = new String(errBytes);
		return new Pair<>(result, output);
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
	public static boolean findSyntaxErrors(SyntacticItem item, BitSet visited) {
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

	/**
	 * Execute a given WyIL file using the default interpreter.
	 *
	 * @param wyildir
	 *            The root directory to look for the WyIL file.
	 * @param id
	 *            The name of the WyIL file
	 * @throws IOException
	 */
	public static void execWyil(File wyildir, Trie id) throws IOException {
		String filename = id.toString() + ".wyil";
		Content.Source root = new DirectoryRoot(registry, wyildir, f -> {
			return f.getName().equals(filename);
		});
		// Empty signature
		Type.Method sig = new Type.Method(Type.Void, Type.Void);
		QualifiedName name = new QualifiedName(new Name(id), new Identifier("test"));
		// Try to run the given function or method
		Interpreter interpreter = new Interpreter(System.out);
		// Create the initial stack
		Interpreter.CallStack stack = interpreter.new CallStack();
		//
		try {
			// Load the relevant WyIL module
			stack.load(root.get(WyilFile.ContentType, id));
			// Sanity check modifiers on test method
			Decl.Callable lambda = stack.getCallable(name, sig);
			// Sanity check target has correct modifiers.
			if (lambda.getModifiers().match(Modifier.Export.class) == null
					|| lambda.getModifiers().match(Modifier.Public.class) == null) {
				WhileyFile srcfile = root.get(WhileyFile.ContentType, id);
				new SyntacticException("test method must be exported and public", srcfile, lambda)
						.outputSourceError(System.out, false);
				throw new RuntimeException("test method must be exported and public");
			} else {
				//
				RValue returns = interpreter.execute(name, sig, stack);
				// Print out any return values produced
				// if (returns != null) {
				// System.out.println(returns);
				// }
			}
		} catch (Interpreter.RuntimeError e) {
			WhileyFile srcfile = root.get(WhileyFile.ContentType, id);
			// FIXME: this is a hack based on current available API.
			new SyntacticException(e.getMessage(), srcfile, e.getElement()).outputSourceError(System.out, false);
			throw e;
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
		try {
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
		} finally {
			outReader.close();
			refReader.close();
		}
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
