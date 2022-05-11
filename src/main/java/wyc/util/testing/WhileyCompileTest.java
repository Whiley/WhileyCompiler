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

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import wycc.lang.Syntactic;
import wycc.util.*;
import wycc.util.testing.TestFile;
import wycc.util.testing.TestStage;
import wycc.util.testing.TestFile.Error;
import wycc.util.testing.TestStage.Result;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Attr.SyntaxError;

public class WhileyCompileTest implements TestStage {
	/**
	 * The maximum error this stage can detect.
	 */
	public final static int MAX_STATIC_ERROR = 699;

	@Override
	public Result apply(Trie path, Path dir, Map<Trie,TextFile> state, TestFile tf) throws IOException {
		boolean strict = tf.get(Boolean.class, "whiley.compile.strict").orElse(false);
		boolean ignored = tf.get(Boolean.class, "whiley.compile.ignore").orElse(false);
		// Construct a suitable mailbox
		MailBox.Buffered<SyntaxError> handler = new MailBox.Buffered<>();
		try {
			// Configure compiler
			wyc.Compiler wyc = new wyc.Compiler().setWhileyDir(dir.toFile()).setWyilDir(dir.toFile()).setTarget(path)
					.setErrorHandler(handler).setStrict(strict);
			// Add source files
			for (Trie sf : state.keySet()) {
				sf = Trie.fromString(sf.toString().replace(".whiley", ""));
				wyc.addSource(sf);
			}
			// Check whether build succeeded
			boolean compiled = wyc.run();
			// Done
			TestFile.Error[] markers = handler.stream().map(se -> toError(state, se)).toArray(TestFile.Error[]::new);
			//
			return new TestStage.Result(ignored, markers);
		} catch(Syntactic.Exception e) {
			e.printStackTrace();
			TestFile.Error err = toError(state,e);
			return new TestStage.Result(ignored,new TestFile.Error[] {err});
		}
	}

	@Override
	public Error[] filter(Error[] errors) {
		return Arrays.asList(errors).stream().filter(m -> m.getErrorNumber() <= MAX_STATIC_ERROR).toArray(TestFile.Error[]::new);
	}

	public static TestFile.Error toError(Map<Trie, TextFile> files, SyntaxError err) {
		return toError(err.getErrorCode(), files, err.getTarget());
	}

	public static TestFile.Error toError(Map<Trie, TextFile> files, Syntactic.Exception ex) {
		return toError(WyilFile.INTERNAL_FAILURE, files, ex.getElement());
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
	public static TestFile.Error toError(int errno, Map<Trie, TextFile> files, Syntactic.Item item) {
		Trie filename = Trie.fromString(getSource(item).toString() + ".whiley");
		// Determine the source-file span for the given syntactic marker.
		Syntactic.Span span = item.getAncestor(AbstractCompilationUnit.Attribute.Span.class);
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

	private static Trie getSource(Syntactic.Item e) {
		Decl.Unit unit = e.getAncestor(Decl.Unit.class);
		String nameStr = unit.getName().toString().replace("::", "/");
		return Trie.fromString(nameStr);
	}
}
