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

import wybs.lang.SyntaxError;

/**
 * Split a source file into a list of tokens. These tokens can then be fed into
 * the parser in order to generate an Abstract Syntax Tree (AST).
 * 
 * @author David J. Pearce
 * 
 */
public class NewWhileyFileLexer {

	private String filename;
	private StringBuffer input;
	private int pos;

	public NewWhileyFileLexer(String filename) throws IOException {
		this(new InputStreamReader(new FileInputStream(filename), "UTF8"));
		this.filename = filename;
	}

	public NewWhileyFileLexer(InputStream instream) throws IOException {
		this(new InputStreamReader(instream, "UTF8"));
	}

	public NewWhileyFileLexer(Reader reader) throws IOException {
		BufferedReader in = new BufferedReader(reader);

		StringBuffer text = new StringBuffer();
		String tmp;
		while ((tmp = in.readLine()) != null) {
			text.append(tmp);
			text.append("\n");
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
			} else if (Character.isJavaIdentifierStart(c)) {
				tokens.add(scanIdentifier());
			} else if (Character.isWhitespace(c)) {
				scanWhiteSpace(tokens);
			} else {
				syntaxError("syntax error");
			}
		}

		return tokens;
	}

	/**
	 * Scan a numeric constant. That is a sequence of digits which gives either
	 * an integer constant, or a real constant (if it includes a dot).
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
			case 't':
				c = '\t';
				break;
			case 'n':
				c = '\n';
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
		pos++;
		while (pos < input.length()) {
			char c = input.charAt(pos);
			if (c == '"') {
				String v = input.substring(start, ++pos);
				return new Token(Token.Kind.StringValue, v, start);
			}
			pos = pos + 1;
		}
		syntaxError("unexpected end-of-string", pos - 1);
		return null;
	}

	static final char[] opStarts = { ',', '(', ')', '[', ']', '{', '}', '+',
			'-', '*', '/', '%', '!', '?', '=', '<', '>', ':', ';', '&', '|',
			'.', '~' };

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
			return new Token(Token.Kind.RightSlash, "/", pos++);
		case '%':
			return new Token(Token.Kind.Percent, "%", pos++);
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
				return new Token(Token.Kind.EqualsEquals, "==", pos - 2);
			} else if ((pos + 1) < input.length() && input.charAt(pos + 1) == '>') {
				pos += 2;
				return new Token(Token.Kind.EqualsGreater, "=>", pos - 2);
			} else {
				return new Token(Token.Kind.Equals, "=", pos++);
			}
		case '<':
			if ((pos + 1) < input.length() && input.charAt(pos + 1) == '=') {
				pos += 2;
				return new Token(Token.Kind.LessEquals, "<=", pos - 2);
			} else {
				return new Token(Token.Kind.LeftAngle, "<", pos++);
			}
		case '>':
			if ((pos + 1) < input.length() && input.charAt(pos + 1) == '=') {
				pos += 2;
				return new Token(Token.Kind.GreaterEquals, ">=", pos - 2);
			} else {
				return new Token(Token.Kind.RightAngle, ">", pos++);
			}
		}

		syntaxError("unknown operator encountered: " + c);
		return null;
	}

	public Token scanIdentifier() {
		int start = pos;
		while (pos < input.length()
				&& Character.isJavaIdentifierPart(input.charAt(pos))) {
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
						+ input.charAt(pos));
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
		throw new SyntaxError(msg, filename, index, index);
	}

	/**
	 * Raise a syntax error with a given message at the current index.
	 * 
	 * @param msg
	 * @param index
	 */
	private void syntaxError(String msg) {
		throw new SyntaxError(msg, filename, pos, pos);
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
			put("char", Token.Kind.Char);
			put("int", Token.Kind.Int);
			put("real", Token.Kind.Real);
			put("string", Token.Kind.String);
			// constants
			put("true", Token.Kind.True);
			put("false", Token.Kind.False);
			// statements
			put("break", Token.Kind.Break);
			put("case", Token.Kind.Case);
			put("catch", Token.Kind.Catch);
			put("continue", Token.Kind.Continue);
			put("debug", Token.Kind.Debug);
			put("do", Token.Kind.Do);
			put("else", Token.Kind.Else);
			put("for", Token.Kind.For);
			put("if", Token.Kind.If);
			put("return", Token.Kind.Return);
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
			put("constant", Token.Kind.Constant);
			put("from", Token.Kind.From);
			put("function", Token.Kind.Function);
			put("import", Token.Kind.Import);
			put("public", Token.Kind.Public);
			put("protected", Token.Kind.Protected);
			put("private", Token.Kind.Private);
			put("native", Token.Kind.Native);
			put("export", Token.Kind.Export);
			put("method", Token.Kind.Method);
			put("package", Token.Kind.Package);
			put("type", Token.Kind.Type);
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
			True {
				public String toString() {
					return "true";
				}
			},
			False {
				public String toString() {
					return "true";
				}
			},
			RealValue, 
			IntValue, 
			CharValue, 
			StringValue,
			// Types
			Null {
				public String toString() {
					return "null";
				}
			},
			Void {
				public String toString() {
					return "void";
				}
			},
			Any {
				public String toString() {
					return "any";
				}
			},
			Bool {
				public String toString() {
					return "bool";
				}
			},
			Byte {
				public String toString() {
					return "byte";
				}
			},
			Int {
				public String toString() {
					return "int";
				}
			},
			Real {
				public String toString() {
					return "real";
				}
			},
			Char {
				public String toString() {
					return "char";
				}
			},
			String {
				public String toString() {
					return "string";
				}
			},
			// Statements
			Break {
				public String toString() {
					return "break";
				}
			},
			Case {
				public String toString() {
					return "case";
				}
			},
			Catch {
				public String toString() {
					return "catch";
				}
			},
			Continue {
				public String toString() {
					return "continue";
				}
			},								
			Debug {
				public String toString() {
					return "debug";
				}
			},
			Do {
				public String toString() {
					return "do";
				}
			},
			Else {
				public String toString() {
					return "else";
				}
			},
			For {
				public String toString() {
					return "for";
				}
			},
			If {
				public String toString() {
					return "if";
				}
			},								
			Return {
				public String toString() {
					return "return";
				}
			},
			Switch {
				public String toString() {
					return "switch";
				}
			},
			Throw {
				public String toString() {
					return "throw";
				}
			},
			Throws {
				public String toString() {
					return "throws";
				}
			},
			Try {
				public String toString() {
					return "try";
				}
			},
			While {
				public String toString() {
					return "while";
				}
			},
			// Declarations
			Package {
				public String toString() {
					return "package";
				}
			},
			Import {
				public String toString() {
					return "import";
				}
			},
			From {
				public String toString() {
					return "from";
				}
			},
			Public {
				public String toString() {
					return "public";
				}
			},
			Private {
				public String toString() {
					return "private";
				}
			},
			Protected {
				public String toString() {
					return "protected";
				}
			},
			Native {
				public String toString() {
					return "native";
				}
			},
			Export {
				public String toString() {
					return "export";
				}
			},
			Constant {
				public String toString() {
					return "constant";
				}
			},
			Type {
				public String toString() {
					return "type";
				}
			},
			Function {
				public String toString() {
					return "function";
				}
			},
			Method {
				public String toString() {
					return "method";
				}
			},
			// Expressions
			All {
				public String toString() {
					return "all";
				}
			},
			No {
				public String toString() {
					return "no";
				}
			},
			Some {
				public String toString() {
					return "some";
				}
			},
			Is {
				public String toString() {
					return "is";
				}
			},
			In {
				public String toString() {
					return "in";
				}
			},
			Where {
				public String toString() {
					return "where";
				}
			},
			Comma {
				public String toString() {
					return ",";
				}
			},
			SemiColon {
				public String toString() {
					return ";";
				}
			},
			Colon {
				public String toString() {
					return ":";
				}
			},
			Ampersand {
				public String toString() {
					return "&";
				}
			},
			VerticalBar {
				public String toString() {
					return "|";
				}
			},
			LeftBrace {
				public String toString() {
					return "(";
				}
			},
			RightBrace {
				public String toString() {
					return ")";
				}
			},
			LeftSquare {
				public String toString() {
					return "[";
				}
			},
			RightSquare {
				public String toString() {
					return "]";
				}
			},
			LeftAngle {
				public String toString() {
					return "<";
				}
			},
			RightAngle {
				public String toString() {
					return ">";
				}
			},
			LeftCurly {
				public String toString() {
					return "{";
				}
			},
			RightCurly {
				public String toString() {
					return "}";
				}
			},
			PlusPlus {
				public String toString() {
					return "++";
				}
			},
			Plus {
				public String toString() {
					return "+";
				}
			},
			Minus {
				public String toString() {
					return "-";
				}
			},
			Star {
				public String toString() {
					return "*";
				}
			},
			LeftSlash {
				public String toString() {
					return "\\";
				}
			},
			RightSlash {
				public String toString() {
					return "//";
				}
			},
			Percent {
				public String toString() {
					return "%";
				}
			},
			Shreak {
				public String toString() {
					return "!";
				}
			},
			Dot {
				public String toString() {
					return ".";
				}
			},
			DotDot {
				public String toString() {
					return "..";
				}
			},
			DotDotDot {
				public String toString() {
					return "...";
				}
			},
			Equals {
				public String toString() {
					return "=";
				}
			},
			EqualsEquals {
				public String toString() {
					return "==";
				}
			},
			NotEquals {
				public String toString() {
					return "!=";
				}
			},
			LessEquals {
				public String toString() {
					return "<=";
				}
			},
			GreaterEquals {
				public String toString() {
					return ">=";
				}
			},
			EqualsGreater {
				public String toString() {
					return "=>";
				}
			},
			MinusGreater {
				public String toString() {
					return "->";
				}
			},
			LogicalAnd {
				public String toString() {
					return "&&";
				}
			},
			LogicalOr {
				public String toString() {
					return "||";
				}
			},
			LogicalImplication {
				public String toString() {
					return "==>";
				}
			},
			// Other
			NewLine, Indent
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
