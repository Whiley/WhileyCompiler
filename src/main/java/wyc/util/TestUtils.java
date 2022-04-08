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
public class TestUtils {
	private final static boolean DEBUG = false;
	/**
	 * List of tests marked as ignored.
	 */
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
		VALID_IGNORED.put("Old_Valid_5", "986");
		VALID_IGNORED.put("Old_Valid_6", "986");
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
		// Runtime checking Old Static Variable Accesses
		VALID_IGNORED.put("StaticVar_Valid_15","#1122");
		// Lambda Typing Bug
//		VALID_IGNORED.put("Lambda_Valid_32","#1132");
//		VALID_IGNORED.put("Lambda_Valid_34","#1132");
		// Interpreter Infinite Loop.
		VALID_IGNORED.put("Infeasible_Function_2", "???");
		VALID_IGNORED.put("Infeasible_Function_3", "???");
		VALID_IGNORED.put("Infeasible_Function_4", "???");
		// Type Invariant Failure (QuickCheck#10)
		VALID_IGNORED.put("RecordAssign_Valid_17", "???");
		// This test is fine, but the interpreter reports an error (correctly). In the
		// future, I imagine the test format can allow for this.
		VALID_IGNORED.put("Unsafe_Valid_6", "???");
		// Flow Typing Over Type Invariants
		VALID_IGNORED.put("UnionType_Valid_28", "1095");
		// other
		VALID_IGNORED.put("Old_Valid_19", "???");
		VALID_IGNORED.put("Old_Valid_21", "???");
		VALID_IGNORED.put("Old_Valid_22", "???");
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
		WyilFile target = Compiler.readWyilFile(wyildir, id);
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
			stack.load(target);
			// Sanity check modifiers on test method
			Decl.Callable lambda = stack.getCallable(name, sig);
			// Sanity check target has correct modifiers.
			if (lambda.getModifiers().match(Modifier.Export.class) == null
					|| lambda.getModifiers().match(Modifier.Public.class) == null) {
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
			throw e;
		}
	}
}
