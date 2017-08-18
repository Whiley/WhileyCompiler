// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.lang;

import java.io.*;
import java.util.*;

import wybs.lang.CompilationUnit;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit;
import wybs.util.AbstractSyntacticItem;
import wyc.io.WhileyFileLexer;
import wyc.io.WhileyFileParser;
import wyc.util.AbstractWhileyFile;
import wycc.util.ArrayUtils;
import wyfs.lang.Content;
import wyfs.lang.Path;

/**
 * Provides the in-memory representation of a Whiley source file (a.k.a. an
 * "Abstract Syntax Tree"). This is implemented as a "heap" of syntactic items.
 * For example, consider the following simple Whiley source file:
 *
 * <pre>
 * function id(int x) -> (int y):
 *     return x
 * </pre>
 *
 * This is represented internally using a heap of syntactic items which might
 * look something like this:
 *
 * <pre>
 * [00] DECL_function(#0,#2,#6,#8)
 * [01] ITEM_utf8("id")
 * [02] ITEM_tuple(#3)
 * [03] DECL_variable(#4,#5)
 * [04] ITEM_utf8("x")
 * [05] TYPE_int
 * [06] ITEM_tuple(#7)
 * [07] DECL_variable(#8,#9)
 * [08] ITEM_utf8("y")
 * [09] TYPE_int
 * [10] STMT_block(#11)
 * [11] STMT_return(#12)
 * [12] EXPR_variable(#03)
 * </pre>
 *
 * Each of this syntactic items will additionally be associated with one or more
 * attributes (e.g. encoding line number information, etc).
 *
 * @author David J. Pearce
 *
 */
public class WhileyFile extends AbstractWhileyFile<WhileyFile> {

	// =========================================================================
	// Content Type
	// =========================================================================

	public static final Content.Type<WhileyFile> ContentType = new Content.Type<WhileyFile>() {

		/**
		 * This method simply parses a whiley file into an abstract syntax tree.
		 * It makes little effort to check whether or not the file is
		 * syntactically correct. In particular, it does not determine the
		 * correct type of all declarations, expressions, etc.
		 *
		 * @param file
		 * @return
		 * @throws IOException
		 */
		@Override
		public WhileyFile read(Path.Entry<WhileyFile> e, InputStream inputstream) throws IOException {
			WhileyFileLexer wlexer = new WhileyFileLexer(e);
			WhileyFileParser wfr = new WhileyFileParser(new WhileyFile(e), wlexer.scan());
			return wfr.read();
		}

		@Override
		public void write(OutputStream output, WhileyFile value) {
			// for now
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return "Content-Type: whiley";
		}

		@Override
		public String getSuffix() {
			return "whiley";
		}
	};

	// =========================================================================
	// Constructors
	// =========================================================================

	public WhileyFile(Path.Entry<WhileyFile> entry) {
		super(entry);
	}

	public WhileyFile(Path.Entry<WhileyFile> entry, CompilationUnit other) {
		super(entry,other);
	}
}
