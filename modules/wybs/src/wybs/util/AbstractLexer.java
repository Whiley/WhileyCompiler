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

package wybs.util;

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
				throw new Error("unrecognised token encountered",pos);
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

	/**
	 * An abstract notion representing a single token in the token stream
	 * produced by lexing a given input stream.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Token {
		public final java.lang.String text;
		public final int start;

		public Token(java.lang.String text, int pos) {
			this.text = text;
			this.start = pos;
		}

		/**
		 * Get the last position in the original stream which contains a
		 * character from this token.
		 * 
		 * @return
		 */
		public int end() {
			return start + text.length() - 1;
		}
	}

	// ===================================================================
	// Standard Tokens
	// ===================================================================	

	/**
	 * Whitespace represents denotes the unused portions of the source file
	 * which lie between the significant tokens.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Whitespace extends Token {
		public Whitespace(java.lang.String text, int pos) {
			super(text, pos);
		}
	}
	
	/**
	 * An identifier is a token representing a sequence of (typically)
	 * alpha-numeric characters, which starts with an alphabetic character.
	 * Identifiers are commonly used for variable names, function names, etc.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Identifier extends Token {
		public Identifier(java.lang.String text, int pos) {
			super(text, pos);
		}
	}

	/**
	 * A keyword is similar to an identifier which has been marked out as having
	 * special significance. Keywords are commonly used in programming languages
	 * to denote important structures (e.g. for-loops, if-conditions, etc).
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Keyword extends Token {
		public Keyword(java.lang.String text, int pos) {
			super(text, pos);
		}
	}
	
	/**
	 * A comment represents a section of the source file which should be
	 * effectively ignored (at least, from the perspective of semantics).  
	 * 
	 * @author David J. Perarce
	 * 
	 */
	public static abstract class Comment extends Token {
		public Comment(java.lang.String text, int pos) {
			super(text, pos);
		}
	}

	/**
	 * A line comment represents a comment which spans to the end of the current
	 * line.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class LineComment extends Comment {
		public LineComment(java.lang.String text, int pos) {
			super(text, pos);
		}
	}

	/**
	 * A block comment represents a comment which potentially spans several
	 * lines of the source file.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class BlockComment extends Comment {
		public BlockComment(java.lang.String text, int pos) {
			super(text, pos);
		}
	}

	
	/**
	 * Represents a string which begins and ends with double quotes.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class String extends Comment {
		public String(java.lang.String text, int pos) {
			super(text, pos);
		}
	}
	
	/**
	 * Represents a single character which begins and ends with single quotes.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Char extends Comment {
		public final char character;

		public Char(char c, java.lang.String text, int pos) {
			super(text, pos);
			this.character = c;
		}
	}
	
	/**
	 * A number which consists of an integer of unbounded size, followed by an
	 * (optional) second integer of unbounded size separated by a decimal point.
	 * This token additionally records the base in which the numbers were
	 * represented (e.g. base 10, or base 16).
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Number extends Token {
		public final int base;
		public final BigInteger beforePoint;
		public final BigInteger afterPoint;

		/**
		 * Construct a token representing a decimal number represented in a
		 * given base.
		 * 
		 * @param base
		 *            --- Must be greater than 1. Examples include decimal (base
		 *            10), hexadecimal (base 16), Octogal (base 8) and Binary
		 *            (base 2).
		 * @param beforePoint
		 *            --- the integer component occurring before (i.e. to the
		 *            left of) the decimal point.
		 * @param afterPoint
		 *            --- the integer component occurring after (i.e. to the
		 *            right of) the decimal point. This will be <code>null</code>
		 *            if there was no decimal point.
		 * @param text
		 *            --- the actual text of the number.
		 * @param pos
		 *            --- the position.
		 */
		public Number(int base, BigInteger beforePoint, BigInteger afterPoint,
				java.lang.String text, int pos) {
			super(text, pos);
			this.beforePoint = beforePoint;
			this.afterPoint = afterPoint;
			this.base = base;
		}
	}
			
	/**
	 * Represents an operator symbol, which may consist of 1 or more characters.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Operator extends Token {
		public Operator(java.lang.String text, int pos) { super(text,pos);	}
	}
	
	public static final char UC_FORALL = '\u2200';
	public static final char UC_EXISTS = '\u2203';
	public static final char UC_EMPTYSET = '\u2205';
	public static final char UC_SUBSET = '\u2282';
	public static final char UC_SUBSETEQ = '\u2286';
	public static final char UC_SUPSET = '\u2283';
	public static final char UC_SUPSETEQ = '\u2287';
	public static final char UC_SETUNION = '\u222A';
	public static final char UC_SETINTERSECTION = '\u2229';
	public static final char UC_LESSEQUALS = '\u2264';
	public static final char UC_GREATEREQUALS = '\u2265';
	public static final char UC_ELEMENTOF = '\u2208';
	public static final char UC_LOGICALAND = '\u2227';
	public static final char UC_LOGICALOR = '\u2228';	

	public static final java.lang.String sUC_FORALL = "" + UC_FORALL;
	public static final java.lang.String sUC_EXISTS = "" + UC_EXISTS;
	public static final java.lang.String sUC_EMPTYSET = "" + UC_EMPTYSET;
	public static final java.lang.String sUC_SUBSET = "" + UC_SUBSET;
	public static final java.lang.String sUC_SUBSETEQ = "" + UC_SUBSETEQ;
	public static final java.lang.String sUC_SUPSET = "" + UC_SUPSET;
	public static final java.lang.String sUC_SUPSETEQ = "" + UC_SUPSETEQ;
	public static final java.lang.String sUC_SETUNION = "" + UC_SETUNION;
	public static final java.lang.String sUC_SETINTERSECTION = "" + UC_SETINTERSECTION;
	public static final java.lang.String sUC_LESSEQUALS = "" + UC_LESSEQUALS;
	public static final java.lang.String sUC_GREATEREQUALS = "" + UC_GREATEREQUALS;
	public static final java.lang.String sUC_ELEMENTOF = "" + UC_ELEMENTOF;
	public static final java.lang.String sUC_LOGICALAND = "" + UC_LOGICALAND;
	public static final java.lang.String sUC_LOGICALOR = "" + UC_LOGICALOR;	
	
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
			while (pos < input.length()
					&& Character.isWhitespace(input.charAt(pos))) {
				pos++;
			}
			if (start != pos) {
				return new Whitespace(input.substring(start, pos), start);
			} else {
				return null;
			}
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
					return new Operator(operator, start);
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
					return new Keyword(keyword, start);
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
			return new Identifier(text, start);
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
				if (c == '"') {				
					java.lang.String v = input.substring(start,++pos);
					return new String(scan(v, pos - v.length()),start);
				}
				pos = pos + 1;
			}
			throw new Error("unexpected end-of-string",pos-1);
		}
		
		private java.lang.String scan(java.lang.String v, int start) throws Error {
			/*
	         * Parsing a string requires several steps to be taken. First, we need
	         * to strip quotes from the ends of the string.
	         */
			v = v.substring(1, v.length() - 1);
			// Second, step through the string and replace escaped characters
			for (int i = 0; i < v.length(); i++) {
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
			return new Char(ans,input.substring(start,pos),start);
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
				if (pos < input.length() && Character.isDigit(pos)) {
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
			
			return new Number(10,beforePoint,afterPoint,input.substring(start,pos),start);					
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
			return new LineComment(input.substring(start, pos), start);
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
			return new BlockComment(input.substring(start, pos), start);
		}
	}
	
	// ===================================================================
	// Helper Classes / Methods
	// ===================================================================	
	
	private static boolean matchString(StringBuffer input, int pos,
			java.lang.String syntax) {
		for (int i = 0; i != syntax.length(); ++i) {
			if (syntax.charAt(i) != input.charAt(pos + i)) {
				return false;
			}
		}
		return true;
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
