// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
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

package wyc.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wybs.lang.Attribute;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wyc.lang.WhileyFile;
import wyfs.lang.Path;

/**
 * Split a source file into a list of tokens. These tokens can then be fed into
 * the parser in order to generate an Abstract Syntax Tree (AST).
 *
 * @author David J. Pearce
 *
 */
public class WhileyFileLexer {
	private final Path.Entry<WhileyFile> entry;
	private StringBuilder input;
	private int pos;

	public WhileyFileLexer(Path.Entry<WhileyFile> entry) throws IOException {
		this.entry = entry;
		Reader reader = new InputStreamReader(entry.inputStream());
		BufferedReader in = new BufferedReader(reader);

        StringBuilder text = new StringBuilder();
		int len = 0;
		char[] buf = new char[1024];
		while ((len = in.read(buf)) != -1) {
			text.append(buf, 0, len);
		}
        input = text;
	}

	/**
	 * Scan all characters from the input stream and generate a corresponding
	 * list of tokens, whilst discarding all whitespace and comments.
	 *
	 * @return
	 */
	public List<Token> scan() {
		ArrayList<Token> tokens = new ArrayList<Token>();
		pos = 0;

		while (pos < input.length()) {
			char c = input.charAt(pos);

			if (Character.isDigit(c)) {
				tokens.add(scanNumericConstant());
			} else if (c == '"') {
				tokens.add(scanStringConstant());
			} else if (c == '\'') {
				tokens.add(scanCharacterConstant());
			} else if (isOperatorStart(c)) {
				tokens.add(scanOperator());
			} else if (Character.isLetter(c) || c == '_') {
				tokens.add(scanIdentifier());
			} else if (Character.isWhitespace(c)) {
				scanWhiteSpace(tokens);
			} else {
				syntaxError("unknown token encountered",pos);
			}
		}

		return tokens;
	}

	/**
	 * Scan a numeric constant. That is a sequence of digits which gives either
	 * an integer constant, or a real constant (if it includes a dot) or a byte
	 * (if it ends in a 'b').
	 *
	 * @return
	 */
	public Token scanNumericConstant() {
		int start = pos;
		while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
			pos = pos + 1;
		}
		if (pos < input.length() && input.charAt(pos) == '.') {
			pos = pos + 1;
			if (pos < input.length() && input.charAt(pos) == '.') {
				// this is case for range e.g. 0..1
				pos = pos - 1;
				return new Token(Token.Kind.IntValue, input.substring(start,
						pos), start);
			}
			while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
				pos = pos + 1;
			}
			return new Token(Token.Kind.RealValue, input.substring(start, pos),
					start);
		} else if(pos < input.length() && input.charAt(pos) == 'b') {
			pos = pos + 1;
			return new Token(Token.Kind.ByteValue, input.substring(start, pos),
					start);
		} else {
			return new Token(Token.Kind.IntValue, input.substring(start, pos),
					start);
		}
	}

	/**
	 * Scan a character constant, such as e.g. 'c'. Observe that care must be
	 * taken to properly handle escape codes. For example, '\n' is a single
	 * character constant which is made up from two characters in the input
	 * string.
	 *
	 * @return
	 */
	public Token scanCharacterConstant() {
		int start = pos;
		pos++;
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
				syntaxError("unrecognised escape character", pos);
			}
		}
		if (input.charAt(pos) != '\'') {
			syntaxError("unexpected end-of-character", pos);
		}
		pos = pos + 1;
		return new Token(Token.Kind.CharValue, input.substring(start, pos),
				start);
	}

	public Token scanStringConstant() {
		int start = pos;
		boolean escaped = false;
		pos++;
		while (pos < input.length()) {
			char c = input.charAt(pos);
			if (c == '"' && !escaped) {
				String v = input.substring(start, ++pos);
				return new Token(Token.Kind.StringValue, v, start);
			} else if(c == '\\' && !escaped) {
				escaped = true;
			} else {
				escaped = false;
			}
			pos = pos + 1;
		}
		syntaxError("unexpected end-of-string", pos - 1);
		return null;
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
			return new Token(Token.Kind.Colon, ":", pos++);
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

		syntaxError("unknown operator encountered: " + c, pos);
		return null;
	}

	public Token scanIdentifier() {
		int start = pos;
		while (pos < input.length()
				&& (input.charAt(pos) == '_' || Character.isLetterOrDigit(input
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
				syntaxError("unknown whitespace character encounterd: \""
						+ input.charAt(pos), pos);
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
	 * Raise a syntax error with a given message at given index.
	 *
	 * @param msg
	 *            --- message to raise.
	 * @param index
	 *            --- index position to associate the error with.
	 */
	private void syntaxError(String msg, int index) {
		// FIXME: this is clearly not a sensible approach
		SyntacticElement unknown = new SyntacticElement.Impl() {};
		unknown.attributes().add(new Attribute.Source(index, index, -1));
		throw new SyntaxError(msg, entry, unknown);

	}

	/**
	 * A map from identifier strings to the corresponding token kind.
	 */
	public static final HashMap<String, Token.Kind> keywords = new HashMap<String, Token.Kind>() {
		{
			// types
			put("void", Token.Kind.Void);
			put("any", Token.Kind.Any);
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
			put("function", Token.Kind.Function);
			put("import", Token.Kind.Import);
			put("public", Token.Kind.Public);
			put("private", Token.Kind.Private);
			put("native", Token.Kind.Native);
			put("export", Token.Kind.Export);
			put("method", Token.Kind.Method);
			put("package", Token.Kind.Package);
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
			Identifier,
			// Constants
			True("true"),
			False("false"),
			ByteValue,
			RealValue,
			IntValue,
			CharValue,
			StringValue,
			// Types
			Null("null"),
			Void("void"),
			Any("any"),
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
			Function("function"),
			Method("method"),
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
