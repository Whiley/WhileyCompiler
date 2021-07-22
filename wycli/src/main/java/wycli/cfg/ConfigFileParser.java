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
package wycli.cfg;

import static wycli.cfg.ConfigFileLexer.Token.Kind.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import wycc.lang.Path;
import wycc.util.AbstractCompilationUnit.Identifier;
import wycli.cfg.ConfigFile.*;
import wycli.cfg.ConfigFileLexer.Token;
import wycc.lang.SyntacticItem;
import wycc.lang.SyntacticException;
import wycc.util.AbstractCompilationUnit.*;

/**
 * Convert a list of tokens into an Abstract Syntax Tree (AST) representing the
 * original configuration file in question.
 *
 * @author David J. Pearce
 *
 */
public class ConfigFileParser {
	private final ConfigFile file;
	private final ArrayList<Token> tokens;
	private int index;

	public ConfigFileParser(Path id, List<Token> tokens) {
		this.tokens = new ArrayList<>(tokens);
		this.file = new ConfigFile(id);
	}

	/**
	 * Read a <code>ConfigFile</code> from the token stream. If the stream is
	 * invalid in some way (e.g. contains a syntax error, etc) then a
	 * <code>SyntaxError</code> is thrown.
	 *
	 * @return
	 */
	public ConfigFile read() {
		List<Declaration> declarations = new ArrayList<>();
		skipWhiteSpace();
		while (index < tokens.size()) {
			Token lookahead = tokens.get(index);
			if (lookahead.kind == LeftSquare) {
				declarations.add(parseSection());
			} else {
				declarations.add(parseKeyValuePair());
			}
			skipWhiteSpace();
		}
		// FIXME: why do we need this?
		file.setDeclarations(new Tuple<>(declarations));
		return file;
	}

	private Table parseSection() {
		int start = index;
		List<KeyValuePair> declarations = new ArrayList<>();
		match(LeftSquare);
		List<Identifier> name = parseSectionName();
		match(RightSquare);
		skipWhiteSpace();
		while (index < tokens.size()) {
			Token lookahead = tokens.get(index);
			if (lookahead.kind == LeftSquare) {
				break;
			} else {
				declarations.add(parseKeyValuePair());
			}
			skipWhiteSpace();
		}
		// Construct the new section
		Table section = new Table(new Tuple<>(name), new Tuple<>(declarations));
		//
		return annotateSourceLocation(section, start);
	}

	private List<Identifier> parseSectionName() {
		ArrayList<Identifier> identifiers = new ArrayList<>();
		identifiers.add(parseIdentifier());
		while (index < tokens.size() && tokens.get(index).kind == Dot) {
			match(Token.Kind.Dot);
			identifiers.add(parseIdentifier());
		}
		return identifiers;
	}

	private KeyValuePair parseKeyValuePair() {
		int start = index;
		Identifier key = parseIdentifier();
		match(Token.Kind.Equals);
		Value value = parseValue();
		KeyValuePair r = annotateSourceLocation(new KeyValuePair(key, value), start);
		skipWhiteSpace();
		return r;
	}

	private Value parseValue() {
		checkNotEof();
		int start = index;
		Value value;
		Token token = tokens.get(index);
		match(token.kind);
		switch (token.kind) {
		case False:
			value = new Value.Bool(false);
			break;
		case True:
			value = new Value.Bool(true);
			break;
		case IntValue:
			value = new Value.Int(new BigInteger(token.text));
			break;
		case StringValue:
			// FIXME: this is probably broken at the extremes
			value = new Value.UTF8(parseString(token.text).getBytes());
			break;
		case LeftSquare:
			index = start;
			value = parseArrayValue();
			break;
		default:
			syntaxError("unknown token", token);
			return null; // deadcode
		}
		return annotateSourceLocation(value, start);
	}

	private Value.Array parseArrayValue() {
		checkNotEof();
		int start = index;
		match(LeftSquare);
		ArrayList<Value> values = new ArrayList<>();
		Value last = null;
		while (eventuallyMatch(RightSquare) == null) {
			if(last != null) {
				match(Comma);
			}
			Value v = parseValue();
			if(last == null) {
				last = v;
			} else if(last.getClass() != v.getClass()){
				throw new SyntacticException("array elements require same type", file, v);
			}
			values.add(v);
		}
		return new Value.Array(values);
	}

	private Identifier parseIdentifier() {
		int start = skipWhiteSpace(index);
		Token token = match(Identifier);
		Identifier id = new Identifier(token.text);
		return annotateSourceLocation(id, start);
	}

	private <T extends SyntacticItem> T annotateSourceLocation(T item, int start) {
		return annotateSourceLocation(item, start, index - 1);
	}

	private <T extends SyntacticItem> T annotateSourceLocation(T item, int start, int end) {
		// Allocate item to enclosing WhileyFile. This is necessary so that the
		// annotations can then be correctly allocated as well.
		item = file.allocate(item);
		// Determine the first and last token representing this span.
		Token first = tokens.get(start);
		Token last = tokens.get(end);
		file.allocate(new Attribute.Span(item, first.start, last.end()));
		return item;
	}

	/**
	 * Match a given token kind, whilst moving passed any whitespace encountered
	 * inbetween. In the case that meet the end of the stream, or we don't match
	 * the expected token, then an error is thrown.
	 *
	 * @param kind
	 * @return
	 */
	private Token match(Token.Kind kind) {
		checkNotEof();
		Token token = tokens.get(index++);
		if (token.kind != kind) {
			syntaxError("expecting \"" + kind + "\" here", token);
		}
		return token;
	}

	/**
	 * Match a given sequence of tokens, whilst moving passed any whitespace
	 * encountered inbetween. In the case that meet the end of the stream, or we
	 * don't match the expected tokens in the expected order, then an error is
	 * thrown.
	 *
	 * @param operator
	 * @return
	 */
	private Token[] match(Token.Kind... kinds) {
		Token[] result = new Token[kinds.length];
		for (int i = 0; i != result.length; ++i) {
			checkNotEof();
			Token token = tokens.get(index++);
			if (token.kind == kinds[i]) {
				result[i] = token;
			} else {
				syntaxError("Expected \"" + kinds[i] + "\" here", token);
			}
		}
		return result;
	}

	/**
	 * Attempt to match a given kind of token with the view that it must
	 * *eventually* be matched. This differs from <code>tryAndMatch()</code>
	 * because it calls <code>checkNotEof()</code>. Thus, it is guaranteed to
	 * skip any whitespace encountered in between. This is safe because we know
	 * there is a terminating token still to come.
	 *
	 * @param kind
	 * @return
	 */
	private Token eventuallyMatch(Token.Kind kind) {
		checkNotEof();
		Token token = tokens.get(index);
		if (token.kind != kind) {
			return null;
		} else {
			index = index + 1;
			return token;
		}
	}

	/**
	 * Attempt to match a given token(s), whilst ignoring any whitespace in
	 * between. Note that, in the case it fails to match, then the index will be
	 * unchanged. This latter point is important, otherwise we could
	 * accidentally gobble up some important indentation. If more than one kind
	 * is provided then this will try to match any of them.
	 *
	 * @param terminated
	 *            Indicates whether or not this function should be concerned
	 *            with new lines. The terminated flag indicates whether or not
	 *            the current construct being parsed is known to be terminated.
	 *            If so, then we don't need to worry about newlines and can
	 *            greedily consume them (i.e. since we'll eventually run into
	 *            the terminating symbol).
	 * @param kinds
	 *
	 * @return
	 */
	private Token tryAndMatch(boolean terminated, Token.Kind... kinds) {
		// If the construct being parsed is know to be terminated, then we can
		// skip all whitespace. Otherwise, we can't skip newlines as these are
		// significant.
		int next = terminated ? skipWhiteSpace(index) : skipLineSpace(index);

		if (next < tokens.size()) {
			Token t = tokens.get(next);
			for (int i = 0; i != kinds.length; ++i) {
				if (t.kind == kinds[i]) {
					index = next + 1;
					return t;
				}
			}
		}
		return null;
	}

	/**
	 * Attempt to match a given sequence of tokens in the given order, whilst
	 * ignoring any whitespace in between. Note that, in any case, the index
	 * will be unchanged!
	 *
	 * @param terminated
	 *            Indicates whether or not this function should be concerned
	 *            with new lines. The terminated flag indicates whether or not
	 *            the current construct being parsed is known to be terminated.
	 *            If so, then we don't need to worry about newlines and can
	 *            greedily consume them (i.e. since we'll eventually run into
	 *            the terminating symbol).
	 * @param kinds
	 *
	 * @return whether the sequence matches
	 */
	private boolean lookaheadSequence(boolean terminated, Token.Kind... kinds) {
		int next = index;
		for (Token.Kind k : kinds) {
			next = terminated ? skipWhiteSpace(next) : skipLineSpace(next);
			if (next >= tokens.size() || tokens.get(next++).kind != k) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether the current index is, after skipping all line spaces, at
	 * the end of a line. This method does not change the state!
	 *
	 * @return whether index is at end of line
	 */
	private boolean isAtEOL() {
		int next = skipLineSpace(index);
		return next >= tokens.size() || tokens.get(next).kind == NewLine;
	}

	/**
	 * Attempt to match a given token on the *same* line, whilst ignoring any
	 * whitespace in between. Note that, in the case it fails to match, then the
	 * index will be unchanged. This latter point is important, otherwise we
	 * could accidentally gobble up some important indentation.
	 *
	 * @param kind
	 * @return
	 */
	private Token tryAndMatchOnLine(Token.Kind kind) {
		int next = skipLineSpace(index);
		if (next < tokens.size()) {
			Token t = tokens.get(next);
			if (t.kind == kind) {
				index = next + 1;
				return t;
			}
		}
		return null;
	}

	/**
	 * Match a the end of a line. This is required to signal, for example, the
	 * end of the current statement.
	 */
	private void matchEndLine() {
		// First, parse all whitespace characters except for new lines
		index = skipLineSpace(index);

		// Second, check whether we've reached the end-of-file (as signaled by
		// running out of tokens), or we've encountered some token which not a
		// newline.
		if (index >= tokens.size()) {
			return; // EOF
		} else if (tokens.get(index).kind != NewLine) {
			syntaxError("expected end-of-line", tokens.get(index));
		} else {
			index = index + 1;
		}
	}

	/**
	 * Check that the End-Of-File has not been reached. This method should be
	 * called from contexts where we are expecting something to follow.
	 */
	private void checkNotEof() {
		skipWhiteSpace();
		if (index >= tokens.size()) {
			if (index > 0) {
				syntaxError("unexpected end-of-file", tokens.get(index - 1));
			} else {
				// I believe this is actually dead-code, since checkNotEof()
				// won't be called before at least one token is matched.
				throw new SyntacticException("unexpected end-of-file", file, null);
			}
		}
	}

	/**
	 * Skip over any whitespace characters.
	 */
	private void skipWhiteSpace() {
		index = skipWhiteSpace(index);
	}

	/**
	 * Skip over any whitespace characters, starting from a given index and
	 * returning the first index passed any whitespace encountered.
	 */
	private int skipWhiteSpace(int index) {
		while (index < tokens.size() && isWhiteSpace(tokens.get(index))) {
			index++;
		}
		return index;
	}

	/**
	 * Skip over any whitespace characters that are permitted on a given line
	 * (i.e. all except newlines), starting from a given index and returning the
	 * first index passed any whitespace encountered.
	 */
	private int skipLineSpace(int index) {
		while (index < tokens.size() && isLineSpace(tokens.get(index))) {
			index++;
		}
		return index;
	}

	/**
	 * Skip over any empty lines. That is lines which contain only whitespace
	 * and comments.
	 */
	private void skipEmptyLines() {
		int tmp = index;
		do {
			tmp = skipLineSpace(tmp);
			if (tmp < tokens.size() && tokens.get(tmp).kind != Token.Kind.NewLine) {
				return; // done
			} else if (tmp >= tokens.size()) {
				index = tmp;
				return; // end-of-file reached
			}
			// otherwise, skip newline and continue
			tmp = tmp + 1;
			index = tmp;
		} while (true);
		// deadcode
	}

	/**
	 * Define what is considered to be whitespace.
	 *
	 * @param token
	 * @return
	 */
	private boolean isWhiteSpace(Token token) {
		return token.kind == Token.Kind.NewLine || isLineSpace(token);
	}

	/**
	 * Define what is considered to be linespace.
	 *
	 * @param token
	 * @return
	 */
	private boolean isLineSpace(Token token) {
		return token.kind == Token.Kind.Indent || token.kind == Token.Kind.LineComment;
	}

	/**
	 * Parse a character from a string of the form 'c' or '\c'.
	 *
	 * @param input
	 * @return
	 */
	private BigInteger parseCharacter(String input) {
		int pos = 1;
		char c = input.charAt(pos++);
		if (c == '\\') {
			// escape code
			switch (input.charAt(pos++)) {
			case 'b':
				c = '\b';
				break;
			case 't':
				c = '\t';
				break;
			case 'n':
				c = '\n';
				break;
			case 'f':
				c = '\f';
				break;
			case 'r':
				c = '\r';
				break;
			case '"':
				c = '\"';
				break;
			case '\'':
				c = '\'';
				break;
			case '\\':
				c = '\\';
				break;
			default:
				throw new RuntimeException("unrecognised escape character");
			}
		}
		return BigInteger.valueOf(c);
	}

	/**
	 * Parse a string constant whilst interpreting all escape characters.
	 *
	 * @param v
	 * @return
	 */
	protected String parseString(String v) {
		/*
		 * Parsing a string requires several steps to be taken. First, we need
		 * to strip quotes from the ends of the string.
		 */
		v = v.substring(1, v.length() - 1);

		StringBuffer result = new StringBuffer();
		// Second, step through the string and replace escaped characters
		for (int i = 0; i < v.length(); i++) {
			if (v.charAt(i) == '\\') {
				if (v.length() <= i + 1) {
					throw new RuntimeException("unexpected end-of-string");
				} else {
					char replace = 0;
					int len = 2;
					switch (v.charAt(i + 1)) {
					case 'b':
						replace = '\b';
						break;
					case 't':
						replace = '\t';
						break;
					case 'n':
						replace = '\n';
						break;
					case 'f':
						replace = '\f';
						break;
					case 'r':
						replace = '\r';
						break;
					case '"':
						replace = '\"';
						break;
					case '\'':
						replace = '\'';
						break;
					case '\\':
						replace = '\\';
						break;
					case 'u':
						len = 6; // unicode escapes are six digits long,
						// including "slash u"
						String unicode = v.substring(i + 2, i + 6);
						replace = (char) Integer.parseInt(unicode, 16); // unicode
						i = i + 5;
						break;
					default:
						throw new RuntimeException("unknown escape character");
					}
					result = result.append(replace);
					i = i + 1;
				}
			} else {
				result = result.append(v.charAt(i));
			}
		}
		return result.toString();
	}

	/**
	 * Parse a token representing a byte value. Every such token is a sequence
	 * of one or more binary digits ('0' or '1') followed by 'b'. For example,
	 * "00110b" is parsed as the byte value 6.
	 *
	 * @param input
	 *            The token representing the byte value.
	 * @return
	 */
	private byte parseByte(Token input) {
		String text = input.text;
		if (text.length() > 9) {
			syntaxError("invalid binary literal (too long)", input);
		}
		int val = 0;
		for (int i = 0; i != text.length() - 1; ++i) {
			val = val << 1;
			char c = text.charAt(i);
			if (c == '1') {
				val = val | 1;
			} else if (c == '0') {

			} else {
				syntaxError("invalid binary literal (invalid characters)", input);
			}
		}
		return (byte) val;
	}

	private void syntaxError(String msg, Token t) {
		throw new SyntacticException(msg, file, new ConfigFile.Attribute.Span(null, t.start, t.end()));
	}

	/**
	 * Represents a given amount of indentation. Specifically, a count of tabs
	 * and spaces. Observe that the order in which tabs / spaces occurred is not
	 * retained.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class Indent extends Token {
		private final int countOfSpaces;
		private final int countOfTabs;

		public Indent(String text, int pos) {
			super(Token.Kind.Indent, text, pos);
			// Count the number of spaces and tabs
			int nSpaces = 0;
			int nTabs = 0;
			for (int i = 0; i != text.length(); ++i) {
				char c = text.charAt(i);
				switch (c) {
				case ' ':
					nSpaces++;
					break;
				case '\t':
					nTabs++;
					break;
				default:
					throw new IllegalArgumentException("Space or tab character expected");
				}
			}
			countOfSpaces = nSpaces;
			countOfTabs = nTabs;
		}

		/**
		 * Test whether this indentation is considered "less than or equivalent"
		 * to another indentation. For example, an indentation of 2 spaces is
		 * considered less than an indentation of 3 spaces, etc.
		 *
		 * @param other
		 *            The indent to compare against.
		 * @return
		 */
		public boolean lessThanEq(Indent other) {
			return countOfSpaces <= other.countOfSpaces && countOfTabs <= other.countOfTabs;
		}

		/**
		 * Test whether this indentation is considered "equivalent" to another
		 * indentation. For example, an indentation of 3 spaces followed by 1
		 * tab is considered equivalent to an indentation of 1 tab followed by 3
		 * spaces, etc.
		 *
		 * @param other
		 *            The indent to compare against.
		 * @return
		 */
		public boolean equivalent(Indent other) {
			return countOfSpaces == other.countOfSpaces && countOfTabs == other.countOfTabs;
		}
	}

	/**
	 * An abstract indentation which represents the indentation of top-level
	 * declarations, such as function declarations. This is used to simplify the
	 * code for parsing indentation.
	 */
	private static final Indent ROOT_INDENT = new Indent("", 0);
}
