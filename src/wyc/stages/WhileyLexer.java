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

package wyc.stages;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.SyntaxError;
import wyjc.runtime.BigRational;

/**
 * Split a source file into a list of tokens. These tokens can then be fed into
 * the parser in order to generate an Abstract Syntax Tree (AST).
 * 
 * @author David J. Pearce
 * 
 */
public class WhileyLexer {	
	private String filename;
	private String input;
	private int pos;
	private int line;
	
	public WhileyLexer(String filename) throws IOException {
		this(new InputStreamReader(new FileInputStream(filename),"UTF8"));
		this.filename = filename;
	}
	
	public WhileyLexer(InputStream instream) throws IOException {
		this(new InputStreamReader(instream,"UTF8"));		
	}
	
	public WhileyLexer(Reader reader) throws IOException {	
		StringBuilder tmp = new StringBuilder();	    
	    int len = 0;
	    char[] buf = new char[1024]; 
	    while((len = reader.read(buf)) != -1) {
	    	tmp.append(buf,0,len);	    	
	    }
		
		input = tmp.toString();	
	}
	
	public List<Token> scan() {
		ArrayList<Token> tokens = new ArrayList<Token>();
		pos = 0;
		line = 1;
		
		while(pos < input.length()) {
			char c = input.charAt(pos);
			
			if(Character.isDigit(c)) {
				tokens.add(scanDigits());
			} else if(c == '"') {
				tokens.add(scanString());
			} else if(c == '\'') {
				tokens.add(scanChar());
			} else if(isOperatorStart(c)) {
				tokens.add(scanOperator());
			} else if(isIdentifierStart(c)) {
				tokens.add(scanIdentifier());
			} else if (c == '\r' && (pos + 1) < input.length()
					&& input.charAt(pos + 1) == '\n') {
				tokens.add(new NewLine("\r\n",pos,line++));
				pos+=2;
			} else if(c == '\n') {				
				tokens.add(new NewLine("\n",pos++,line++));				
			} else if(c == '\t') {
				tokens.add(scanTabs());
			} else if(Character.isWhitespace(c)) {				
				skipWhitespace(tokens);
			} else {
				syntaxError("syntax error");
			}
		}
		
		return tokens;
	}
	
	public Token scanLineComment() {
		int start = pos;
		while(pos < input.length() && input.charAt(pos) != '\n') {
			pos++;
		}
		return new LineComment(input.substring(start,pos),start,line);
	}
	
	public Token scanBlockComment() {
		int start = pos;
		while((pos+1) < input.length() && (input.charAt(pos) != '*' || input.charAt(pos+1) != '/')) {
			pos++;
		}
		pos++;
		pos++;
		return new BlockComment(input.substring(start,pos),start,line);
	}
	
	public Token scanDigits() {		
		if (pos < (input.length() + 1) && input.charAt(pos) == '0'
				&& input.charAt(pos+1) == 'x') {			
			return scanHexDigits();
		}

		int start = pos;
		while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
			pos = pos + 1;
		}		
		if(pos < input.length() && input.charAt(pos) == '.') {
			pos = pos + 1;			
			if(pos < input.length() && input.charAt(pos) == '.') {
				// this is case for range e.g. 0..1
				pos = pos - 1;
				BigInteger r = new BigInteger(input.substring(start, pos));
				return new Int(r,input.substring(start,pos),start,line);
			}
			while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
				pos = pos + 1;
			}			
			BigRational r = new BigRational(input.substring(start, pos));
			return new Real(r,input.substring(start,pos),start,line);
		} else if(pos < input.length() && input.charAt(pos) == 'b') {
			// indicates a binary literal
			if((pos - start) > 8) {
				syntaxError("invalid binary literal (too long)");
			}
			int val = 0;
			for(int i=start;i!=pos;++i) {
				val = val << 1;
				char c = input.charAt(i);
				if(c == '1') {
					val = val | 1;
				} else if(c == '0') {
					
				} else {
					syntaxError("invalid binary literal (invalid characters)");
				}				
			}
			pos = pos + 1;
			return new Byte((byte)val,input.substring(start,pos),start,line);
		} else {
			BigInteger r = new BigInteger(input.substring(start, pos));
			return new Int(r,input.substring(start,pos),start,line);			
		}		
	}
	
	public Token scanHexDigits() {		
		int start = pos;
		pos += 2; // skip "0x"
		while (pos < input.length()
				&& isHexDigit(input.charAt(pos))) {			
			pos = pos + 1;
		}		
		BigInteger r = BigInteger.ZERO;
		BigInteger base = BigInteger.ONE;
		BigInteger sixteen = BigInteger.valueOf(16);
		int first = start+2;
		int tmp = pos;		
		while(tmp > first) {			
			BigInteger d = BigInteger.valueOf(hexDigit(input.charAt(--tmp)));			
			r = r.add(d.multiply(base));
			base = base.multiply(sixteen);
		}
		
		return new Int(r,input.substring(start,pos),start,line);						
	}
	
	public int hexDigit(char c) {
		if('0' <= c && c <= '9') { 
			return c - '0';
		} else if('a' <= c && c <= 'f') {
			return 10 + (c - 'a');
		} else {
			return 10 + (c - 'A');
		}
	}
	
	public boolean isHexDigit(char c) {
		return ('0' <= c && c <= '9') || ('a' <= c && c <= 'f')
				|| ('A' <= c && c <= 'F');
	}
	
	public Token scanChar() {
		int start = pos;
		pos++;
		char c = input.charAt(pos++);				
		if(c == '\\') {
			// escape code
			switch(input.charAt(pos++)) {
				case 't':
					c = '\t';
					break;
				case 'n':
					c = '\n';
					break;
				case 'r':
					c = '\r';
					break;
				default:
					syntaxError("unrecognised escape character",pos);
			}
		}
		if(input.charAt(pos) != '\'') {
			syntaxError("unexpected end-of-character",pos);
		}
		pos = pos + 1;
		return new Char(c,input.substring(start,pos),start,line);
	}
	
	public Token scanString() {
		int start = pos;
		pos ++;
		StringBuffer buf = new StringBuffer();
		
		while(pos < input.length()) {
			char c = input.charAt(pos);
			if(c == '\\') {				
				if(++pos == input.length()) {
					syntaxError("unexpected end-of-file",pos);
				}
				switch (input.charAt(pos)) {
					case 'b' :
						buf.append('\b');
						break;
					case 't' :
						buf.append('\t');
						break;
					case 'n' :
						buf.append('\n');
						break;
					case 'f' :
						buf.append('\f');
						break;
					case 'r' :
						buf.append('\r');
						break;
					case '"' :
						buf.append('\"');
						break;
					case '\'' :
						buf.append('\'');
						break;
					case '\\' :
						buf.append('\\');
						break;
					case 'u' :
						// unicode escapes are six digits long, including "slash u"
						String unicode = input.substring(pos+1,pos+5);
						buf.append((char) Integer.parseInt(unicode, 16)); // unicode
						break;
					default :
						syntaxError("unknown escape character",pos);							
				}
			} else if (c == '"') {				
				String v = input.substring(start,++pos);
				return new Strung(buf.toString(),v, start,line);
			} else {			
				buf.append(c);
			}
			pos = pos + 1;
		}
		syntaxError("unexpected end-of-string",pos-1);
		return null;
	}	

	static final char UC_FORALL = '\u2200';
	static final char UC_EXISTS = '\u2203';
	static final char UC_EMPTYSET = '\u2205';
	static final char UC_SUBSET = '\u2282';
	static final char UC_SUBSETEQ = '\u2286';
	static final char UC_SUPSET = '\u2283';
	static final char UC_SUPSETEQ = '\u2287';
	static final char UC_SETUNION = '\u222A';
	static final char UC_SETINTERSECTION = '\u2229';
	static final char UC_LESSEQUALS = '\u2264';
	static final char UC_GREATEREQUALS = '\u2265';
	static final char UC_ELEMENTOF = '\u2208';
	static final char UC_LOGICALAND = '\u2227';
	static final char UC_LOGICALOR = '\u2228';
	
	static final char[] opStarts = { ',', '(', ')', '[', ']', '{', '}', '+', '-',
			'*', '/', '%', '!', '?', '=', '<', '>', ':', ';', '&', '|', '^', '.','~',
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
		for(char o : opStarts) {
			if(c == o) {
				return true;
			}
		}
		return false;
	}
	
	public Token scanOperator() {		
		char c = input.charAt(pos);
		
		if(c == '.') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '.') {
				if((pos+2) < input.length() && input.charAt(pos+2) == '.') {
					pos += 3;
					return new DotDotDot(pos-3,line);
				} else {
					pos += 2;
					return new DotDot(pos-2,line);
				}
			} else {
				return new Dot(pos++,line);
			}
		} else if(c == ',') {
			return new Comma(pos++,line);
		} else if(c == ':') {
			if((pos+1) < input.length() && input.charAt(pos+1) == ':') {
				pos += 2;
				return new ColonColon(pos-2,line);
			} else {
				return new Colon(pos++,line);				
			}			
		} else if(c == ';') {
			return new SemiColon(pos++,line);
		} else if(c == '(') {
			return new LeftBrace(pos++,line);
		} else if(c == ')') {
			return new RightBrace(pos++,line);
		} else if(c == '[') {
			return new LeftSquare(pos++,line);
		} else if(c == ']') {
			return new RightSquare(pos++,line);
		} else if(c == '{') {
			return new LeftCurly(pos++,line);
		} else if(c == '}') {
			return new RightCurly(pos++,line);
		} else if(c == '+') {
			return new Plus(pos++,line);
		} else if(c == '-') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '>') {
				pos += 2;
				return new RightArrow("->",pos-2,line);
			} else {
				return new Minus(pos++,line);				
			}			
		} else if(c == '*') {
			return new Star(pos++,line);
		} else if(c == '&') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '&') {
				pos += 2;
				return new LogicalAnd("&&",pos-2,line);
			} else {
				return new Ampersand("&",pos++,line);
			}
		} else if(c == '|') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '|') {
				pos += 2;
				return new LogicalOr("||",pos-2,line);
			} else {
				return new Bar(pos++,line);
			}
		} else if(c == '/') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '/') {
				return scanLineComment();
			} else if((pos+1) < input.length() && input.charAt(pos+1) == '*') {
				return scanBlockComment();
			} {
				return new RightSlash(pos++,line);
			}
		} else if(c == '%') {
			return new Percent(pos++,line);			
		} else if(c == '^') {
			return new Caret(pos++,line);			
		} else if(c == '~') {
			return new Tilde(pos++,line);			
		} else if(c == '!') {			
			if((pos+1) < input.length() && input.charAt(pos+1) == '=') {
				pos += 2;
				return new NotEquals("!=",pos-2,line);
			} else {
				return new Shreak(pos++,line);				
			}			
		} else if(c == '?') {						
			return new Question(pos++,line);							
		} else if(c == '=') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '=') {
				pos += 2;
				return new EqualsEquals(pos-2,line);
			} else if((pos+1) < input.length() && input.charAt(pos+1) == '>') {
				pos += 2;
				return new StrongRightArrow("=>",pos-2,line);
			} else {
				return new Equals(pos++,line);				
			}
		} else if(c == '<') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '=') {
				pos += 2;
				return new LessEquals("<=",pos-2,line);
			} else if((pos+1) < input.length() && input.charAt(pos+1) == '<') {
				pos += 2;
				return new LeftLeftAngle(pos-2,line);
			} else {
				return new LeftAngle(pos++,line);
			}
		} else if(c == '>') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '=') {
				pos += 2;
				return new GreaterEquals(">=",pos - 2,line);
			} else if((pos+1) < input.length() && input.charAt(pos+1) == '>') {
				pos += 2;
				return new RightRightAngle(pos - 2,line);
			} else {
				return new RightAngle(pos++,line);
			}
		} else if(c == UC_LESSEQUALS) {
			return new LessEquals(""+UC_LESSEQUALS,pos++,line);
		} else if(c == UC_GREATEREQUALS) {
			return new GreaterEquals(""+UC_GREATEREQUALS,pos++,line);
		} else if(c == UC_SETUNION) {
			return new Union(""+UC_SETUNION,pos++,line);
		} else if(c == UC_SETINTERSECTION) {
			return new Intersection(""+UC_SETINTERSECTION,pos++,line);
		} else if(c == UC_ELEMENTOF) {
			return new ElemOf(""+UC_ELEMENTOF,pos++,line);
		} else if(c == UC_SUBSET) {
			return new Subset(""+UC_SUBSET,pos++,line);
		} else if(c == UC_SUBSETEQ) {
			return new SubsetEquals(""+UC_SUBSETEQ,pos++,line);
		} else if(c == UC_SUPSET) {
			return new Supset(""+UC_SUPSET,pos++,line);
		} else if(c == UC_SUPSETEQ) {
			return new SupsetEquals(""+UC_SUPSETEQ,pos++,line);
		} else if(c == UC_EMPTYSET) {
			return new EmptySet(""+UC_EMPTYSET,pos++,line);
		} else if(c == UC_LOGICALOR) {
			return new LogicalOr(""+UC_LOGICALOR,pos++,line);
		} else if(c == UC_LOGICALAND) {
			return new LogicalAnd(""+UC_LOGICALAND,pos++,line);
		} 
				
		syntaxError("unknown operator encountered: " + c);
		return null;
	}
	
	public boolean isIdentifierStart(char c) {
		return Character.isJavaIdentifierStart(c);
	}
	
	public static final String[] keywords = {		
		"skip",
		"true",
		"false",
		"null",
		"any",
		"byte",
		"char",
		"catch",
		"int",
		"real",
		"string",
		"bool",
		"ref",
		"void",			
		"if",		
		"switch",
		"break",
		"case",
		"default",
		"throw",
		"throws",
		"do",
		"while",
		"else",
		"where",
		"requires",
		"ensures",
		"as",
		"for",
		"assert",
		"debug",		
		"return",
		"define",		
		"function",
		"import",
		"package",
		"public",
		"native",
		"export",
		"extern",
		"new",
		"try"
	};
	
	public Token scanIdentifier() {
		int start = pos;		
		while (pos < input.length() &&
				Character.isJavaIdentifierPart(input.charAt(pos))) {
			pos++;								
		}		
		String text = input.substring(start,pos);
		
		// now, check for keywords
		for(String keyword : keywords) {
			if(keyword.equals(text)) {
				return new Keyword(text,start,line);
			}
		}
		
		// now, check for text operators
		if(text.equals("in")) {
			return new ElemOf(text,start,line);
		} else if(text.equals("no")) {
			return new None(text,start,line);
		} else if(text.equals("some")) {
			return new Some(text,start,line);
		} else if(text.equals("is")) {			
			return new InstanceOf(start,line);			 
		}
	
		// otherwise, must be identifier
		return new Identifier(text,start,line);
	}
	
	public Token scanTabs() {
		int start = pos;
		int ntabs = 0;
		while (pos < input.length() && input.charAt(pos) == '\t') {
			pos++;
			ntabs++;
		}
		return new Tabs(input.substring(start, pos), ntabs, start,line);	
	}
	
	public void skipWhitespace(List<Token> tokens) {		
		int start = pos;		
		while (pos < input.length() && input.charAt(pos) == ' ') {			
			pos++;		
		}
		int ts = (pos - start) / 4;
		if(ts > 0) {			
			tokens.add(new Tabs(input.substring(start,pos),ts,start,line));
		}
		while (pos < input.length() && input.charAt(pos) != '\n'
				&& input.charAt(pos) != '\r'
				&& Character.isWhitespace(input.charAt(pos))) {
			pos++;
		}		
	}
	
	private void syntaxError(String msg, int index) {
		throw new SyntaxError(msg, filename, index, index);
	}
	
	private void syntaxError(String msg) {
		throw new SyntaxError(msg, filename, pos, pos);
	}
	
	public static abstract class Token {
		public final String text;
		public final int start;
		public final int line;		
		
		public Token(String text, int pos, int line) {
			this.text = text;
			this.start = pos;
			this.line = line;
		}
			
		public int end() {
			return start + text.length() - 1;
		}
	}
	
	public static class Real extends Token {
		public final BigRational value;
		public Real(BigRational r, String text, int pos, int line) { 
			super(text,pos,line);
			value = r;
		}
	}
	public static class Byte extends Token {
		public final byte value;
		public Byte(byte r, String text, int pos, int line) { 
			super(text,pos,line);
			value = r;
		}
	}
	public static class Char extends Token {
		public final char value;
		public Char(char r, String text, int pos, int line) { 
			super(text,pos,line);
			value = r;
		}
	}
	public static class Int extends Token {
		public final BigInteger value;
		public Int(BigInteger r, String text, int pos, int line) { 
			super(text,pos,line);
			value = r;
		}
	}
	public static class Identifier extends Token {
		public Identifier(String text, int pos, int line) { super(text,pos,line); }
	}
	public static class Strung extends Token {
		public final String string;
		public Strung(String string, String text, int pos, int line) { 
			super(text,pos,line);
			this.string = string;
		}
	}	
	public static class Keyword extends Token {
		public Keyword(String text, int pos, int line) { super(text,pos,line); }
	}
	public static class NewLine extends Token {
		public NewLine(String text, int pos, int line) { super(text,pos,line); }
	}	
	public static class Tabs extends Token {
		public int ntabs;
		public Tabs(String text, int ntabs, int pos, int line) { 
			super(text,pos,line);
			this.ntabs = ntabs; 
		}		
	}	
	public static class LineComment extends Token {
		public LineComment(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class BlockComment extends Token {
		public BlockComment(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class Caret extends Token {
		public Caret(int pos, int line) { super("^",pos,line);	}
	}
	public static class Comma extends Token {
		public Comma(int pos, int line) { super(",",pos,line);	}
	}
	public static class Colon extends Token {
		public Colon(int pos, int line) { super(":",pos,line);	}
	}
	public static class ColonColon extends Token {
		public ColonColon(int pos, int line) { super("::",pos,line);	}
	}
	public static class SemiColon extends Token {
		public SemiColon(int pos, int line) { super(";",pos,line);	}
	}
	public static class LeftBrace extends Token {
		public LeftBrace(int pos, int line) { super("(",pos,line);	}
	}
	public static class RightBrace extends Token {
		public RightBrace(int pos, int line) { super(")",pos,line);	}
	}
	public static class LeftSquare extends Token {
		public LeftSquare(int pos, int line) { super("[",pos,line);	}
	}
	public static class RightSquare extends Token {
		public RightSquare(int pos, int line) { super("]",pos,line);	}
	}
	public static class LeftAngle extends Token {
		public LeftAngle(int pos, int line) { super("<",pos,line);	}
	}
	public static class LeftLeftAngle extends Token {
		public LeftLeftAngle(int pos, int line) { super("<<",pos,line);	}
	}
	public static class RightAngle extends Token {
		public RightAngle(int pos, int line) { super(">",pos,line);	}
	}
	public static class RightRightAngle extends Token {
		public RightRightAngle(int pos, int line) { super(">>",pos,line);	}
	}
	public static class LeftCurly extends Token {
		public LeftCurly(int pos, int line) { super("{",pos,line);	}
	}
	public static class RightCurly extends Token {
		public RightCurly(int pos, int line) { super("}",pos,line);	}
	}
	public static class Plus extends Token {
		public Plus(int pos, int line) { super("+",pos,line);	}
	}
	public static class Minus extends Token {
		public Minus(int pos, int line) { super("-",pos,line);	}
	}
	public static class Star extends Token {
		public Star(int pos, int line) { super("*",pos,line);	}
	}
	public static class Percent extends Token {
		public Percent(int pos, int line) { super("%",pos,line);	}
	}
	public static class LeftSlash extends Token {
		public LeftSlash(int pos, int line) { super("\\",pos,line);	}
	}
	public static class RightSlash extends Token {
		public RightSlash(int pos, int line) { super("/",pos,line);	}
	}
	public static class Tilde extends Token {
		public Tilde(int pos, int line) { super("~",pos,line);	}
	}
	public static class Shreak extends Token {
		public Shreak(int pos, int line) { super("!",pos,line);	}
	}
	public static class Question extends Token {
		public Question(int pos, int line) { super("?",pos,line);	}
	}
	public static class Dot extends Token {
		public Dot(int pos, int line) { super(".",pos,line);	}
	}
	public static class DotDot extends Token {
		public DotDot(int pos, int line) { super("..",pos,line);	}
	}
	public static class DotDotDot extends Token {
		public DotDotDot(int pos, int line) { super("...",pos,line);	}
	}
	public static class Bar extends Token {
		public Bar(int pos, int line) { super("|",pos,line);	}
	}	
	public static class Equals extends Token {
		public Equals(int pos, int line) { super("=",pos,line);	}
	}
	public static class EqualsEquals extends Token {
		public EqualsEquals(int pos, int line) { super("==",pos,line);	}
	}
	public static class NotEquals extends Token {
		public NotEquals(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class LessEquals extends Token {
		public LessEquals(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class GreaterEquals extends Token {
		public GreaterEquals(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class InstanceOf extends Token {
		public InstanceOf(int pos, int line) { super("is",pos,line);	}
	}
	public static class None extends Token {
		public None(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class Some extends Token {
		public Some(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class ElemOf extends Token {
		public ElemOf(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class Union extends Token {
		public Union(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class Intersection extends Token {
		public Intersection(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class EmptySet extends Token {
		public EmptySet(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class Subset extends Token {
		public Subset(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class Supset extends Token {
		public Supset(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class SubsetEquals extends Token {
		public SubsetEquals(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class SupsetEquals extends Token {
		public SupsetEquals(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class LogicalAnd extends Token {
		public LogicalAnd(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class LogicalOr extends Token {
		public LogicalOr(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class LogicalNot extends Token {
		public LogicalNot(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class Ampersand extends Token {
		public Ampersand(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class BitwiseOr extends Token {
		public BitwiseOr(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class BitwiseNot extends Token {
		public BitwiseNot(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class LeftRightArrow extends Token {
		public LeftRightArrow(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class LeftArrow extends Token {
		public LeftArrow(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class RightArrow extends Token {
		public RightArrow(String text, int pos, int line) { super(text,pos,line);	}
	}
	public static class StrongRightArrow extends Token {
		public StrongRightArrow(String text, int pos, int line) { super(text,pos,line);	}
	}
}
