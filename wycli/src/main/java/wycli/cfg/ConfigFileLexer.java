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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import wycc.lang.SyntacticException;

/**
 * Split a configuration file into a list of tokens. These tokens can then be fed into
 * the parser in order to generate an Abstract Syntax Tree (AST).
 *
 * @author David J. Pearce
 *
 */
public class ConfigFileLexer {
	private StringBuilder input;
	private int pos;

	public ConfigFileLexer(InputStream input) throws IOException {
		Reader reader = new InputStreamReader(input);
		BufferedReader in = new BufferedReader(reader);

        StringBuilder text = new StringBuilder();
		int len = 0;
		char[] buf = new char[1024];
		while ((len = in.read(buf)) != -1) {
			text.append(buf, 0, len);
		}
        this.input = text;
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

			if (Character.isDigit(c) || c == '-') {
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
		// Check for negative values
		if(input.charAt(pos) == '-') {
			pos = pos + 1;
		}
		// Parse the remainder
		while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
			pos = pos + 1;
		}
		// Done
		return new Token(Token.Kind.IntValue, input.substring(start, pos),
				start);
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

	public Token scanIdentifier() {
		int start = pos;
		while (pos < input.length() && (input.charAt(pos) == '_' || Character.isLetterOrDigit(input.charAt(pos)))) {
			pos++;
		}
		String text = input.substring(start, pos);
		Token.Kind kind = Token.Kind.Identifier;
		if (text.equals("false")) {
			kind = Token.Kind.False;
		} else if (text.equals("true")) {
			kind = Token.Kind.True;
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
		return new Token(Token.Kind.LineComment, input.substring(start, pos), start);
	}

	/**
	 * Skip over any whitespace at the current index position in the input
	 * string.
	 *
	 * @param tokens
	 */
	public void skipWhitespace(List<Token> tokens) {
		while (pos < input.length() && (input.charAt(pos) == '\n' || input.charAt(pos) == '\t')) {
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
		throw new SyntacticException(msg, null, new ConfigFile.Attribute.Span(null, index, index));
	}

	static final char[] opStarts = { '[', ']', '=', '.', ',' };

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
		case '[':
			return new Token(Token.Kind.LeftSquare, "[", pos++);
		case ']':
			return new Token(Token.Kind.RightSquare, "]", pos++);
		case '=':
			return new Token(Token.Kind.Equals, "=", pos++);
		case '.':
			return new Token(Token.Kind.Dot, ".", pos++);
		case ',':
			return new Token(Token.Kind.Comma, ",", pos++);
		// =================================================================
		//
		// =================================================================
		}

		syntaxError("unknown operator encountered: " + c, pos);
		return null;
	}
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
			IntValue,
			CharValue,
			StringValue,
			// Operators
			Dot("."),
			Comma(","),
			LeftSquare("["),
			RightSquare("]"),
			Equals,
			// Other
			NewLine, Indent, LineComment;

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
