// Copyright (c) 2013, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wycc.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Provides a generic mechanism for turning an input file or string into a list
 * of tokens. Every token records the text that constituted it, as well as its
 * start and end position in the stream.
 * </p>
 * <p>
 * There are several use cases for this class. The most important use case is
 * obviously lexing a source file so that it can be fed into a parser and then
 * into the remainder of a compiler. However, other use cases include pretty
 * printers which read a stream and format it according to certain rules for
 * indentation, etc.
 * </p>
 * <p>
 * This class also provides several standard tokens and rules which are common
 * across the various languages used within the Whiley compiler system.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class AbstractLexer {
	/**
	 * The input stream.
	 */
	private final StringBuffer input;

	/**
	 * Lexing rules which determine how the input stream is broken down into
	 * tokens.
	 */
	private final Rule[] rules;

	/**
	 * Construct from an input stream using UTF-8 as the default character
	 * encoding.
	 *
	 * @param instream
	 * @throws IOException
	 */
	public AbstractLexer(Rule[] rules, InputStream instream) throws IOException {
		this(rules, new InputStreamReader(instream, "UTF-8"));
	}

	/**
	 * Construct from an input stream using a given character set decoder.
	 *
	 * @param instream
	 * @throws IOException
	 */
	public AbstractLexer(Rule[] rules, InputStream instream, CharsetDecoder decoder)
			throws IOException {
		this(rules, new InputStreamReader(instream, decoder));
	}

	/**
	 * Construct from a reader (which already has some notion of character
	 * enconding included).
	 *
	 * @param reader
	 * @throws IOException
	 */
	public AbstractLexer(Rule[] rules, Reader reader) throws IOException {
		BufferedReader in = new BufferedReader(reader);

		StringBuffer text = new StringBuffer();
		java.lang.String tmp;
		while ((tmp = in.readLine()) != null) {
			text.append(tmp);
			text.append("\n");
		}

		this.input = text;
		this.rules = rules;
	}

	/**
	 * Scan the given input stream and produce a list of tokens, or an error.
	 *
	 * @return
	 */
	public List<Token> scan() throws Error {
		ArrayList<Token> tokens = new ArrayList<Token>();
		int pos = 0;
		while (pos < input.length()) {
			int start = pos;
			for (int i = 0; i != rules.length; ++i) {
				Rule rule = rules[i];
				int left = input.length() - pos;
				if (left >= rule.lookahead()) {
					Token t = rule.match(input, pos);
					if (t != null) {
						tokens.add(t);
						pos = pos + t.text.length();
						break; // restart rule application loop
					}
				}
			}
			if(pos == start) {
				throw new Error("unrecognised token encountered (" + input.charAt(pos) + ")",pos);
			}
		}
		return tokens;
	}

	/**
	 * A lexer rule is responsible for matching a given character sequence and
	 * turning it into a token.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class Rule {
		/**
		 * Determines the maximum amount of lookahead required by this rule. The
		 * system will guarantee there is enough lookahead space in the input
		 * before calling the rule.
		 *
		 * @return
		 */
		public abstract int lookahead();

		/**
		 * Attempt to match this rule at a given position in the input stream.
		 * Observe that upon a successful match (i.e. when the returned value is
		 * not <code>null</code>) the stream will be advanced to
		 * <code>Token.end + 1</code>.
		 *
		 * @param buffer
		 * @param start
		 * @return
		 */
		public abstract Token match(StringBuffer buffer, int start) throws Error;
	}

	// ===================================================================
	// Standard Rules
	// ===================================================================

	/**
	 * Standard rule for parsing Whitespace.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class WhitespaceRule extends Rule {

		@Override
		public int lookahead() {
			return 1;
		}

		@Override
		public Token match(StringBuffer input, int pos) {
			int start = pos;

			if(pos < input.length()) {

				// First, look for new lines
				if(input.charAt(pos) == '\n') {
					pos++;
					return new Token.NewLine("\n",start);
				} else if((pos+1) < input.length() && input.charAt(pos) == '\r' && input.charAt(pos+1) == '\n') {
					return new Token.NewLine("\r\n",start);
				}

				// Second, look for spaces
				if(input.charAt(pos) == ' ') {
					while (pos < input.length()
							&& input.charAt(pos) == ' ') {
						pos++;
					}
					return new Token.Spaces(input.substring(start, pos), start);
				}

				// Third, look for tabs
				if(input.charAt(pos) == '\t') {
					while (pos < input.length()
							&& input.charAt(pos) == '\t') {
						pos++;
					}
					return new Token.Tabs(input.substring(start, pos), start);
				}
			}

			return null;
		}
	}

	/**
	 * Standard rule for parsing Operators.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class OperatorRule extends Rule {
		private final java.lang.String[] operators;
		private final int minLookahead;

		public OperatorRule(java.lang.String[] operators) {
			this.operators = operators;
			int min = Integer.MAX_VALUE;
			for(int i=0;i!=operators.length;++i) {
				min = Math.min(operators[i].length(),min);
			}
			this.minLookahead = min;
		}

		@Override
		public int lookahead() {
			return minLookahead;
		}

		@Override
		public Token match(StringBuffer input, int pos) {
			int start = pos;
			int nRemaining = input.length() - pos;
			for (int i = 0; i != operators.length; ++i) {
				java.lang.String operator = operators[i];
				if (operator.length() <= nRemaining
						&& matchString(input, pos, operator)) {
					return new Token.Operator(operator, start);
				}
			}
			return null;
		}
	}

	/**
	 * Standard rule for parsing keywords.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class KeywordRule extends Rule {
		private final java.lang.String[] keywords;
		private final int minLookahead;

		public KeywordRule(java.lang.String[] keywords) {
			this.keywords = keywords;
			int min = Integer.MAX_VALUE;
			for(int i=0;i!=keywords.length;++i) {
				min = Math.min(keywords[i].length(),min);
			}
			this.minLookahead = min;
		}

		@Override
		public int lookahead() {
			return minLookahead;
		}

		@Override
		public Token match(StringBuffer input, int pos) {
			int start = pos;
			while (pos < input.length()
					&& Character.isLetter(input.charAt(pos))) {
				pos = pos + 1;
			}
			java.lang.String word = input.substring(start, pos);
			for (int i = 0; i != keywords.length; ++i) {
				java.lang.String keyword = keywords[i];
				if (keyword.equals(word)) {
					return new Token.Keyword(keyword, start);
				}
			}
			return null;
		}
	}

	/**
	 * A standard rule for parsing identifiers. Identifiers may not start with a
	 * numeric character, or an operator. But, they may start with e.g. '$', or
	 * '_' and, obviously, any alpabetic character.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class IdentifierRule extends Rule {

		@Override
		public int lookahead() {
			return 1;
		}

		@Override
		public Token match(StringBuffer input, int pos) {
			int start = pos;
			if (!Character.isJavaIdentifierStart(input.charAt(pos))) {
				return null;
			}
			pos = pos + 1;
			while (pos < input.length()
					&& Character.isJavaIdentifierPart(input.charAt(pos))) {
				pos++;
			}
			java.lang.String text = input.substring(start, pos);
			return new Token.Identifier(text, start);
		}
	}

	/**
	 * A standard rule for parsing strings which begin with quote marks. For
	 * example, <code>"Hello World"</code>. This rule correctly handles escape
	 * sequences, such as "\n", "\t" and "\\", etc.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class StringRule extends Rule {

		@Override
		public int lookahead() {
			return 1;
		}

		@Override
		public Token match(StringBuffer input, int pos) throws Error {
			if(input.charAt(pos) != '\"') { return null; }
			int start = pos;
			boolean flag = false;
			pos ++;
			while(pos < input.length()) {
				char c = input.charAt(pos);
				if (flag) {
					flag = false;
					continue;
				}
				if (c == '\\') {
					flag = true;
					continue;
				}
				if (c == '\"') {
					java.lang.String v = input.substring(start,++pos);
					return new Token.String(scan(v, pos - v.length()),start);
				}
				pos = pos + 1;
			}
			throw new Error("unexpected end-of-string",pos-1);
		}

		private java.lang.String scan(java.lang.String v, int start) throws Error {
			// Second, step through the string and replace escaped characters
			int end = v.length()-1;
			for (int i = 1; i < end; i++) {
				if (v.charAt(i) == '\\') {
					if (v.length() <= i + 1) {
						throw new Error("unexpected end-of-string",start+i);
					} else {
						char replace = 0;
						int len = 2;
						switch (v.charAt(i + 1)) {
							case 'b' :
								replace = '\b';
								break;
							case 't' :
								replace = '\t';
								break;
							case 'n' :
								replace = '\n';
								break;
							case 'f' :
								replace = '\f';
								break;
							case 'r' :
								replace = '\r';
								break;
							case '"' :
								replace = '\"';
								break;
							case '\'' :
								replace = '\'';
								break;
							case '\\' :
								replace = '\\';
								break;
							case 'u' :
								len = 6; // unicode escapes are six digits long,
								// including "slash u"
								java.lang.String unicode = v.substring(i + 2, i + 6);
								replace = (char) Integer.parseInt(unicode, 16); // unicode
								break;
							default :
								throw new Error("unknown escape character",start+i);
						}
						v = v.substring(0, i) + replace + v.substring(i + len);
					}
				}
			}
			return v;
		}

	}

	/**
	 * A standard rule for parsing characters which begin with single quote
	 * marks. For example, <code>'H'</code>. This rule correctly handles escape
	 * sequences, such as '\n', '\t' and '\\', etc.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class CharRule extends Rule {

		@Override
		public int lookahead() {
			return 1;
		}

		@Override
		public Token match(StringBuffer input, int pos) throws Error {
			char ans = ' ';		// set to keep javac out of trouble.
			int start = pos;
			boolean addflag = false;
			boolean escflag = false;
			boolean gotflag = false;
			boolean ovflag = false;
			pos ++;
			while(pos < input.length()) {
				char c = input.charAt(pos);
				if (addflag) {
					addflag = false;
					ans = c;
					continue;
				}
				if (c == '\\') {
					gotflag = true;
					escflag = true;
					continue;
				}
				if (c == '\'') {
					break;
				}
				ans = c;
				ovflag = gotflag;
				gotflag = true;
				pos = pos + 1;
			}
			if (!( pos < input.length())) {
				throw new Error("unexpected end-of-character", pos-1);
			}
			if (!gotflag) {
				throw new Error("empty character", pos-1);
			}
			if (ovflag) {
				throw new Error("character overflow", pos-1);
			}
			if (escflag) {
				// escape code
				switch(ans) {
				case 't':
					ans = '\t';
					break;
				case 'n':
					ans = '\n';
					break;
				default:
					throw new Error("unrecognised escape character",pos-1);
				}
			}
			return new Token.Char(ans,input.substring(start,pos),start);
		}

	}

	/**
	 * A standard rule for parsing numbers represented in decimal (i.e. base
	 * 10).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class DecimalRule extends Rule {

		@Override
		public int lookahead() {
			return 1;
		}

		@Override
		public Token match(StringBuffer input, int pos) throws Error {
			int start = pos;
			if(!Character.isDigit(input.charAt(pos))) {
				return null;
			}
			while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
				pos = pos + 1;
			}

			BigInteger beforePoint = new BigInteger(input.substring(start, pos));
			BigInteger afterPoint = null;

			if (pos < input.length() && input.charAt(pos) == '.') {
				pos = pos + 1;
				int dotStart = pos;
				if (pos < input.length() && Character.isDigit(input.charAt(pos))) {
					while (pos < input.length()
							&& Character.isDigit(input.charAt(pos))) {
						pos = pos + 1;
					}
					afterPoint = new BigInteger(input.substring(dotStart, pos));
				} else {
					// this is case for range e.g. 0..1
					pos = pos - 1;
				}
			}
			return new Token.Number(10,beforePoint,afterPoint,input.substring(start,pos),start);
		}

	}

	/**
	 * Standard rule for parsing line comments with user-defineable syntax.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class LineCommentRule extends Rule {
		private java.lang.String syntax;
		public LineCommentRule(java.lang.String syntax) {
			this.syntax = syntax;
		}

		@Override
		public int lookahead() {
			return syntax.length();
		}

		@Override
		public Token match(StringBuffer input, int pos) throws Error {
			// first, check whether this rule applies or not.
			if(!matchString(input,pos,syntax)) {
				return null;
			}
			// second scan until the end-of-line is reached.
			int start = pos;
			while (pos < input.length() && input.charAt(pos) != '\n') {
				pos++;
			}
			return new Token.LineComment(input.substring(start, pos), start);
		}
	}

	/**
	 * Standard rule for parsing block comments with user-defineable start and
	 * end syntax.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class BlockCommentRule extends Rule {
		private java.lang.String startSyntax;
		private java.lang.String endSyntax;

		public BlockCommentRule(java.lang.String startSyntax, java.lang.String endSyntax) {
			this.startSyntax = startSyntax;
			this.endSyntax = endSyntax;
		}

		@Override
		public int lookahead() {
			return startSyntax.length();
		}

		@Override
		public Token match(StringBuffer input, int pos) throws Error {
			// first, check whether this rule applies or not.
			if (!matchString(input, pos, startSyntax)) {
				return null;
			}
			// second, parse the block comment!
			int start = pos;
			while ((pos + 1) < input.length()
					&& !matchString(input, pos, endSyntax)) {
				pos++;
			}
			pos += endSyntax.length();
			return new Token.BlockComment(input.substring(start, pos), start);
		}
	}

	// ===================================================================
	// Helper Classes / Methods
	// ===================================================================

	private static boolean matchString(StringBuffer input, int pos,
			java.lang.String syntax) {
		int diff = input.length() - pos;
		if(syntax.length() > diff) {
			return false;
		} else {
			for (int i = 0; i != syntax.length(); ++i) {
				if (syntax.charAt(i) != input.charAt(pos + i)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Used to report lexing errors.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Error extends Exception {
		private final int position;

		public Error(java.lang.String msg, int position) {
			super(msg);
			this.position = position;
		}

		public int getPosition() {
			return position;
		}
	}
}
