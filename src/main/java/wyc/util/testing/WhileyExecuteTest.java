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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import wyc.Compiler;
import wycc.lang.Syntactic;
import wycc.util.*;
import wycc.util.AbstractCompilationUnit.Identifier;
import wycc.util.AbstractCompilationUnit.Name;
import wycc.util.testing.TestFile;
import wycc.util.testing.TestStage;
import wycc.util.testing.TestFile.Error;
import wyil.interpreter.Interpreter;
import wyil.interpreter.ConcreteSemantics.RValue;
import wyil.interpreter.Interpreter.CallStack;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.*;

public class WhileyExecuteTest implements TestStage {
	/**
	 * The maximum error that this stage can detect.
	 */
	public final static int MAX_RUNTIME_ERROR = 715;

	@Override
	public Result apply(Trie path, Path dir, Map<Trie, TextFile> state, TestFile tf) throws IOException {
		boolean ignored = tf.get(Boolean.class, "whiley.execute.ignore").orElse(false);
		// Test was expected to compile, so attempt to run the code.
		String unit = tf.get(String.class, "main.file").orElse("main");
		TestFile.Error[] actual = new TestFile.Error[0];
		try {
			execWyil(dir.toFile(), path, Trie.fromString(unit));
		} catch (Interpreter.RuntimeError e) {
			actual = new TestFile.Error[] { toError(state, e) };
		}
		return new TestStage.Result(ignored, actual);
	}

	@Override
	public Error[] filter(Error[] errors) {
		return Arrays.asList(errors).stream().filter(m -> isRuntimeError(m.getErrorNumber()))
				.toArray(TestFile.Error[]::new);
	}

	public static boolean isRuntimeError(int m) {
		return WhileyCompileTest.MAX_STATIC_ERROR < m && m <= MAX_RUNTIME_ERROR;
	}

	// ==============================================================================
	// Helpers
	// ==============================================================================

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
		execWyil(wyildir,id,id);
	}

	public static void execWyil(File wyildir, Trie id, Trie unit) throws IOException {
		WyilFile target = Compiler.readWyilFile(wyildir, id);
		// Empty signature
		Type.Method sig = new Type.Method(Type.Void, Type.Void);
		QualifiedName name = new QualifiedName(new Name(unit), new Identifier("test"));
		//
		try {
			// Try to run the given function or method
			Interpreter interpreter = new Interpreter(System.out, target);
			// Create the initial stack
			Interpreter.CallStack stack = interpreter.new CallStack();
			// Sanity check modifiers on test method
			Decl.Callable lambda = interpreter.getCallable(name, sig);
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