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
package wyc.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Split a source file into a list of tokens. These tokens can then be fed into
 * the parser in order to generate an Abstract Syntax Tree (AST).
 *
 * @author David J. Pearce
 *
 */
public class WhileyFileLexer {
	private StringBuilder input;
	private int pos;

	public WhileyFileLexer(InputStream input) throws IOException {
		Reader reader = new InputStreamReader(input);
		BufferedReader in = new BufferedReader(reader);
		try {
			StringBuilder text = new StringBuilder();
			int len = 0;
			char[] buf = new char[1024];
			while ((len = in.read(buf)) != -1) {
				text.append(buf, 0, len);
			}
			this.input = text;
		} finally {
			in.close();
		}
	}

	public WhileyFileLexer(String input) {
		this.input = new StringBuilder(input);
	}

	/**
	 * Scan all characters from the input stream and generate a corresponding
	 * list of tokens, whilst discarding all whitespace and comments.
	 *
	 * @return
	 */
	public List<Token> scan() {
		ArrayList<Token> tokens = new ArrayList<>();
		pos = 0;

		while (pos < input.length()) {
			char c = input.charAt(pos);

			if (isDigit(c)) {
				tokens.add(scanNumericLiteral());
			} else if (c == '"') {
				tokens.add(scanStringLiteral());
			} else if (c == '\'') {
				tokens.add(scanCharacterLiteral());
			} else if (isOperatorStart(c)) {
				tokens.add(scanOperator());
			} else if (isLetter(c) || c == '_') {
				tokens.add(scanIdentifier());
			} else if (Character.isWhitespace(c)) {
				scanWhiteSpace(tokens);
			} else {
				tokens.add(new Token(Token.Kind.Unknown, "" + c, pos++));
			}
		}

		return tokens;
	}

	/**
	 * Scan a numeric constant. That is a sequence of digits which constitutes an
	 * integer literal (e.g. 12 or 1_000), a binary literal (e.g. 0b1001_0011) or a
	 * hex literal (e.g. 0xff).
	 *
	 * @return
	 */
	public Token scanNumericLiteral() {
		int next = pos + 1;
		// Decide whether it's an integer, binary or hexadecimal literal
		if (next < input.length() && input.charAt(pos) == '0' && input.charAt(next) == 'x') {
			// Hexadecimal literal
			return scanHexLiteral();
		} else if (next < input.length() && input.charAt(pos) == '0' && input.charAt(next) == 'b') {
			// Binary literal
			return scanBinaryLiteral();
		} else {
			return scanIntegerLiteral();
		}
	}

	public Token scanIntegerLiteral() {
		int start = pos;
		while (pos < input.length() && (input.charAt(pos) == '_' || isDigit(input.charAt(pos)))) {
			pos = pos + 1;
		}
		// Extract literal characters
		String literal = input.substring(start, pos);
		// Done
		return new Token(Token.Kind.IntegerLiteral, literal, start);
	}

	public Token scanHexLiteral() {
		int start = pos;
		pos = pos + 2; // skip "0x"
		while (pos < input.length() && (input.charAt(pos) == '_' || isLetterOrDigit(input.charAt(pos)))) {
			pos = pos + 1;
		}
		// Extract literal characters
		String literal = input.substring(start, pos);
		// Done
		return new Token(Token.Kind.HexLiteral, literal, start);
	}

	public Token scanBinaryLiteral() {
		int start = pos;
		pos = pos + 2; // skip "0b"
		while (pos < input.length() && (input.charAt(pos) == '_' || isDigit(input.charAt(pos)))) {
			pos = pos + 1;
		}
		// Extract literal characters
		String literal = input.substring(start, pos);
		// Done
		return new Token(Token.Kind.BinaryLiteral, literal, start);
	}

	/**
	 * Scan a character literal, such as e.g. 'c'. Observe that care must be
	 * taken to properly handle escape codes. For example, '\n' is a single
	 * character constant which is made up from two characters in the input
	 * string.
	 *
	 * @return
	 */
	public Token scanCharacterLiteral() {
		int start = pos++;
		// Check for escape character
		if (pos < input.length() && input.charAt(pos) == '\\') {
			pos = pos + 1;
		}
		// Parse character
		pos = pos + 1;
		// Check for trailing quote
		if (pos >= input.length()) {
			// Missing
			return new Token(Token.Kind.Unknown, input.substring(start), input.length());
		} else if (input.charAt(pos) != '\'') {
			// Missing
			return new Token(Token.Kind.Unknown, input.substring(start, pos), pos);
		} else {
			// Present
			pos = pos + 1;
			return new Token(Token.Kind.CharLiteral, input.substring(start, pos),
					start);
		}
	}

	public Token scanStringLiteral() {
		int start = pos;
		boolean escaped = false;
		pos++;
		while (pos < input.length()) {
			char c = input.charAt(pos);
			if (c == '"' && !escaped) {
				String v = input.substring(start, ++pos);
				return new Token(Token.Kind.StringLiteral, v, start);
			} else if(c == '\\' && !escaped) {
				escaped = true;
			} else {
				escaped = false;
			}
			pos = pos + 1;
		}
		return new Token(Token.Kind.Unknown, input.substring(start, pos), pos - 1);
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

	static final char[] opStarts = { ',', '(', ')', '[', ']', '{', '}', '+',
			'-', '*', '/', '%', '^', '!', '?', '=', '<', '>', ':', ';', '&', '|',
			'.', '~',
			// Unicode operators
			UC_FORALL,
			UC_EXISTS,
			UC_EMPTYSET,
			UC_SUBSET,
			UC_SUBSETEQ,
			UC_SUPSET,
			UC_SUPSETEQ,
			UC_SETUNION,
			UC_SETINTERSECTION,
			UC_LESSEQUALS,
			UC_GREATEREQUALS,
			UC_ELEMENTOF
	};

	public boolean isOperatorStart(char c) {
		for (char o : opStarts) {
			if (c == o) {
				return true;
			}
		}
		return false;
	}

	public Token scanOperator() {
		char c = input.charAt(pos);

		switch (c) {
		case '.':
			if ((pos + 1) < input.length() && input.charAt(pos + 1) == '.') {
				pos = pos + 2;
				if (pos < input.length() && input.charAt(pos) == '.') {
					return new Token(Token.Kind.DotDotDot, "..", pos++);
				} else {
					return new Token(Token.Kind.DotDot, "..", pos);
				}
			} else {
				return new Token(Token.Kind.Dot, ".", pos++);
			}
		case ',':
			return new Token(Token.Kind.Comma, ",", pos++);
		case ';':
			return new Token(Token.Kind.SemiColon, ";", pos++);
		case ':':
			if (pos + 1 < input.length() && input.charAt(pos + 1) == ':') {
				pos += 2;
				return new Token(Token.Kind.ColonColon, "::", pos - 2);
			} else {
				return new Token(Token.Kind.Colon, ":", pos++);
			}
		case '|':
			if (pos + 1 < input.length() && input.charAt(pos + 1) == '|') {
				pos += 2;
				return new Token(Token.Kind.LogicalOr, "||", pos - 2);
			} else {
				return new Token(Token.Kind.VerticalBar, "|", pos++);
			}
		case '(':
			return new Token(Token.Kind.LeftBrace, "(", pos++);
		case ')':
			return new Token(Token.Kind.RightBrace, ")", pos++);
		case '[':
			return new Token(Token.Kind.LeftSquare, "[", pos++);
		case ']':
			return new Token(Token.Kind.RightSquare, "]", pos++);
		case '{':
			return new Token(Token.Kind.LeftCurly, "{", pos++);
		case '}':
			return new Token(Token.Kind.RightCurly, "}", pos++);
		case '+':
			if ((pos + 1) < input.length() && input.charAt(pos + 1) == '+') {
				pos = pos + 2;
				return new Token(Token.Kind.PlusPlus, "++", pos);
			} else {
				return new Token(Token.Kind.Plus, "+", pos++);
			}
		case '-':
			if (pos + 1 < input.length() && input.charAt(pos + 1) == '>') {
				pos += 2;
				return new Token(Token.Kind.MinusGreater, "->", pos - 2);
			} else {
				return new Token(Token.Kind.Minus, "-", pos++);
			}
		case '*':
			return new Token(Token.Kind.Star, "*", pos++);
		case '&':
			if (pos + 1 < input.length() && input.charAt(pos + 1) == '&') {
				pos += 2;
				return new Token(Token.Kind.LogicalAnd, "&&", pos - 2);
			} else {
				return new Token(Token.Kind.Ampersand, "&", pos++);
			}
		case '/':
			if((pos+1) < input.length() && input.charAt(pos+1) == '/') {
				return scanLineComment();
			} else if((pos+1) < input.length() && input.charAt(pos+1) == '*') {
				return scanBlockComment();
			} else {
				return new Token(Token.Kind.RightSlash, "/", pos++);
			}
		case '%':
			return new Token(Token.Kind.Percent, "%", pos++);
		case '^':
			return new Token(Token.Kind.Caret, "^", pos++);
		case '~':
			return new Token(Token.Kind.Tilde, "~", pos++);
		case '?':
			return new Token(Token.Kind.QuestionMark, "?", pos++);
		case '!':
			if ((pos + 1) < input.length() && input.charAt(pos + 1) == '=') {
				pos += 2;
				return new Token(Token.Kind.NotEquals, "!=", pos - 2);
			} else {
				return new Token(Token.Kind.Shreak, "!", pos++);
			}
		case '=':
			if ((pos + 1) < input.length() && input.charAt(pos + 1) == '=') {
				pos += 2;
				if (pos < input.length() && input.charAt(pos) == '>') {
					pos++;
					return new Token(Token.Kind.LogicalImplication, "==>", pos - 3);
				} else {
					return new Token(Token.Kind.EqualsEquals, "==", pos - 2);
				}
			} else if ((pos + 1) < input.length() && input.charAt(pos + 1) == '>') {
				pos += 2;
				return new Token(Token.Kind.EqualsGreater, "=>", pos - 2);
			} else {
				return new Token(Token.Kind.Equals, "=", pos++);
			}
		case '<':
			if ((pos + 1) < input.length() && input.charAt(pos + 1) == '=') {
				pos += 2;
				if ((pos+1) < input.length() && input.charAt(pos) == '=' && input.charAt(pos+1) == '>') {
					pos += 2;
					return new Token(Token.Kind.LogicalIff, "<==>", pos - 4);
				} else {
					return new Token(Token.Kind.LessEquals, "<=", pos - 2);
				}
			} else if ((pos + 1) < input.length() && input.charAt(pos + 1) == '<') {
				pos += 2;
				return new Token(Token.Kind.LeftAngleLeftAngle, "<<", pos - 2);
			} else{
				return new Token(Token.Kind.LeftAngle, "<", pos++);
			}
		case '>':
			if ((pos + 1) < input.length() && input.charAt(pos + 1) == '=') {
				pos += 2;
				return new Token(Token.Kind.GreaterEquals, ">=", pos - 2);
			} else if ((pos + 1) < input.length() && input.charAt(pos + 1) == '>') {
				pos += 2;
				return new Token(Token.Kind.RightAngleRightAngle, ">>", pos - 2);
			} else {
				return new Token(Token.Kind.RightAngle, ">", pos++);
			}
		// =================================================================
		//
		// =================================================================
		case UC_LESSEQUALS:
			return new Token(Token.Kind.LessEquals, "" + c, pos++);
		case UC_GREATEREQUALS:
			return new Token(Token.Kind.GreaterEquals, "" + c, pos++);
		case UC_SETUNION:
			return new Token(Token.Kind.SetUnion, "" + c, pos++);
		case UC_SETINTERSECTION:
			return new Token(Token.Kind.SetIntersection, "" + c, pos++);
		case UC_ELEMENTOF:
			return new Token(Token.Kind.ElementOf, "" + c, pos++);
		case UC_SUBSET:
			return new Token(Token.Kind.Subset, "" + c, pos++);
		case UC_SUBSETEQ:
			return new Token(Token.Kind.SubsetEquals, "" + c, pos++);
		case UC_SUPSET:
			return new Token(Token.Kind.Superset, "" + c, pos++);
		case UC_SUPSETEQ:
			return new Token(Token.Kind.SupersetEquals, "" + c, pos++);
		case UC_EMPTYSET:
			return new Token(Token.Kind.EmptySet, "" + c, pos++);
		case UC_LOGICALOR:
			return new Token(Token.Kind.LogicalOr, "" + c, pos++);
		case UC_LOGICALAND:
			return new Token(Token.Kind.LogicalAnd, "" + c, pos++);
		}
		return new Token(Token.Kind.Unknown, "" + c, pos++);
	}

	public Token scanIdentifier() {
		int start = pos;
		while (pos < input.length()
				&& (input.charAt(pos) == '_' || isLetterOrDigit(input
						.charAt(pos)))) {
			pos++;
		}
		String text = input.substring(start, pos);

		// now, check for keywords
		Token.Kind kind = keywords.get(text);
		if (kind == null) {
			// not a keyword, so just a regular identifier.
			kind = Token.Kind.Identifier;
		}
		return new Token(kind, text, start);
	}

	public void scanWhiteSpace(List<Token> tokens) {
		while (pos < input.length()
				&& Character.isWhitespace(input.charAt(pos))) {
			if (input.charAt(pos) == ' ' || input.charAt(pos) == '\t') {
				tokens.add(scanIndent());
			} else if (input.charAt(pos) == '\n') {
				tokens.add(new Token(Token.Kind.NewLine, input.substring(pos,
						pos + 1), pos));
				pos = pos + 1;
			} else if (input.charAt(pos) == '\r' && (pos + 1) < input.length()
					&& input.charAt(pos + 1) == '\n') {
				tokens.add(new Token(Token.Kind.NewLine, input.substring(pos,
						pos + 2), pos));
				pos = pos + 2;
			} else {
				tokens.add(new Token(Token.Kind.Unknown, "" + input.charAt(pos), pos++));
			}
		}
	}

	/**
	 * Scan one or more spaces or tab characters, combining them to form an
	 * "indent".
	 *
	 * @return
	 */
	public Token scanIndent() {
		int start = pos;
		while (pos < input.length()
				&& (input.charAt(pos) == ' ' || input.charAt(pos) == '\t')) {
			pos++;
		}
		return new Token(Token.Kind.Indent, input.substring(start, pos), start);
	}

	public Token scanLineComment() {
		int start = pos;
		while (pos < input.length() && input.charAt(pos) != '\n') {
			pos++;
		}
		return new Token(Token.Kind.LineComment, input.substring(start, pos),
				start);
	}

	public Token scanBlockComment() {
		int start = pos;
		while((pos+1) < input.length() && (input.charAt(pos) != '*' || input.charAt(pos+1) != '/')) {
			pos++;
		}
		pos++;
		pos++;
		return new Token(Token.Kind.BlockComment,input.substring(start,pos),start);
	}

	/**
	 * Skip over any whitespace at the current index position in the input
	 * string.
	 *
	 * @param tokens
	 */
	public void skipWhitespace(List<Token> tokens) {
		while (pos < input.length()
				&& (input.charAt(pos) == '\n' || input.charAt(pos) == '\t')) {
			pos++;
		}
	}

	/**
	 * This is used in place of Character.isDigit(), since the latter also supports
	 * unicode digits.
	 *
	 * @param c
	 * @return
	 */
	private boolean isDigit(char c) {
		return '0' <= c && c <= '9';
	}

	/**
	 * This is used in place of Character.isLetter(), since the latter also supports
	 * unicode letters.
	 *
	 * @param c
	 * @return
	 */
	private boolean isLetter(char c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ;
	}

	/**
	 * This is used in place of Character.isLetterOrDigit(), since the latter also
	 * supports unicode digits and letters.
	 *
	 * @param c
	 * @return
	 */
	private boolean isLetterOrDigit(char c) {
		return isLetter(c) || isDigit(c);
	}

	/**
	 * A map from identifier strings to the corresponding token kind.
	 */
	public static final HashMap<String, Token.Kind> keywords = new HashMap<String, Token.Kind>() {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		{
			// types
			//put("void", Token.Kind.Void);
			put("null", Token.Kind.Null);
			put("bool", Token.Kind.Bool);
			put("byte", Token.Kind.Byte);
			put("int", Token.Kind.Int);
			put("real", Token.Kind.Real);
			// constants
			put("true", Token.Kind.True);
			put("false", Token.Kind.False);
			// statements
			put("assert", Token.Kind.Assert);
			put("assume", Token.Kind.Assume);
			put("break", Token.Kind.Break);
			put("case", Token.Kind.Case);
			put("catch", Token.Kind.Catch);
			put("continue", Token.Kind.Continue);
			put("debug", Token.Kind.Debug);
			put("default", Token.Kind.Default);
			put("do", Token.Kind.Do);
			put("else", Token.Kind.Else);
			put("ensures", Token.Kind.Ensures);
			put("fail", Token.Kind.Fail);
			put("for", Token.Kind.For);
			put("if", Token.Kind.If);
			put("new", Token.Kind.New);
			put("return", Token.Kind.Return);
			put("requires", Token.Kind.Requires);
			put("skip", Token.Kind.Skip);
			put("switch", Token.Kind.Switch);
			put("throw", Token.Kind.Throw);
			put("throws", Token.Kind.Throws);
			put("try", Token.Kind.Try);
			put("while", Token.Kind.While);
			// expressions
			put("all", Token.Kind.All);
			put("no", Token.Kind.No);
			put("some", Token.Kind.Some);
			put("is", Token.Kind.Is);
			put("in", Token.Kind.In);
			put("where", Token.Kind.Where);
			// declarations
			put("import", Token.Kind.Import);
			put("function", Token.Kind.Function);
			put("method", Token.Kind.Method);
			put("property", Token.Kind.Property);
			// modifiers
			put("public", Token.Kind.Public);
			put("private", Token.Kind.Private);
			put("native", Token.Kind.Native);
			put("export", Token.Kind.Export);
			put("package", Token.Kind.Package);
			put("final", Token.Kind.Final);
			// lifetimes
			put("this", Token.Kind.This);
		}
	};

	/**
	 * The base class for all tokens.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Token {

		public enum Kind {
			Unknown,
			Identifier,
			// Constants
			True("true"),
			False("false"),
			BinaryLiteral,
			IntegerLiteral,
			HexLiteral,
			CharLiteral,
			StringLiteral,
			// Types
			Null("null"),
			//Void("void"),
			Bool("bool"),
			Byte("byte"),
			Int("int"),
			Real("real"),
			Char("char"),
			String("string"),
			// Statements
			Assert("assert"),
			Assume("assume"),
			Break("break"),
			Case("case"),
			Catch("catch"),
			Continue("continue"),
			Debug("debug"),
			Default("default"),
			Do("do"),
			Else("else"),
			Ensures("ensures"),
			For("for"),
			Fail("for"),
			If("if"),
			New("new"),
			Return("return"),
			Requires("requires"),
			Skip("skip"),
			Switch("switch"),
			Throw("throw"),
			Throws("throws"),
			Try("try"),
			While("while"),
			// Declarations
			Package("package"),
			Import("import"),
			Public("public"),
			Private("private"),
			Native("native"),
			Export("export"),
			Final("final"),
			Function("function"),
			Method("method"),
			Property("property"),
			// Lifetimes
			This("this"),
			// Expressions
			All("all"),
			No("no"),
			Some("some"),
			Is("is"),
			In("in"),
			Where("where"),
			Comma(","),
			SemiColon(";"),
			Colon(":"),
			ColonColon("::"),
			Ampersand("&"),
			VerticalBar("|"),
			LeftBrace("("),
			RightBrace(")"),
			LeftSquare("["),
			RightSquare("]"),
			LeftAngleLeftAngle("<<"),
			LeftAngle("<"),
			RightAngleRightAngle(">>"),
			RightAngle(">"),
			LeftCurly("{"),
			RightCurly("}"),
			PlusPlus("++"),
			Plus("+"),
			Minus("-"),
			Star("*"),
			LeftSlash("\\"),
			RightSlash("//"),
			Percent("%"),
			Shreak("!"),
			Caret("^"),
			Tilde("~"),
			QuestionMark("?"),
			Dot("."),
			DotDot(".."),
			DotDotDot("..."),
			Equals("="),
			EqualsEquals("=="),
			NotEquals("!="),
			LessEquals("<="),
			GreaterEquals(">="),
			EqualsGreater("=>"),
			MinusGreater("->"),
			LogicalAnd("&&"),
			LogicalOr("||"),
			LogicalImplication("==>"),
			LogicalIff("<==>"),
			SetUnion("" + UC_SETUNION),
			SetIntersection("" + UC_SETINTERSECTION),
			ElementOf("" + UC_ELEMENTOF),
			EmptySet("" + UC_EMPTYSET),
			Subset("" + UC_SUBSET),
			SubsetEquals("" + UC_SUBSETEQ),
			Superset("" + UC_SUPSETEQ),
			SupersetEquals("" + UC_SUPSETEQ),
			// Other
			NewLine, Indent, LineComment, BlockComment;

			private final String displayString;

			private Kind() {
				this.displayString = null; // Use default toString
			}

			private Kind(String displayString) {
				this.displayString = displayString;
			}

			@Override
			public String toString() {
				// Use displayString if present, otherwise default toString
				return (displayString != null) ? displayString : super.toString();
			}
		}

		public final Kind kind;
		public final String text;
		public final int start;

		public Token(Kind kind, String text, int pos) {
			this.kind = kind;
			this.text = text;
			this.start = pos;
		}

		public int end() {
			return start + text.length() - 1;
		}
	}
}
