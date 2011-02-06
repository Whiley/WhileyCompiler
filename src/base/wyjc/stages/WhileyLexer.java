// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjc.stages;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import wyil.jvm.rt.BigRational;
import wyil.util.SyntaxError;

public class WhileyLexer {	
	private String filename;
	private StringBuffer input;
	private int pos;
	
	public WhileyLexer(String filename) throws IOException {
		this(new InputStreamReader(new FileInputStream(filename),"UTF8"));
		this.filename = filename;
	}
	
	public WhileyLexer(InputStream instream) throws IOException {
		this(new InputStreamReader(instream,"UTF8"));		
	}
	
	public WhileyLexer(Reader reader) throws IOException {
		BufferedReader in = new BufferedReader(reader);
		
		StringBuffer text = new StringBuffer();
		String tmp;
		while ((tmp = in.readLine()) != null) {
			text.append(tmp);
			text.append("\n");
		}
		
		input = text;	
	}
	
	public List<Token> scan() {
		ArrayList<Token> tokens = new ArrayList<Token>();
		pos = 0;
		
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
			} else if(c == '\n') {
				tokens.add(new NewLine(pos++));
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
	
	public Token scanComment() {
		int start = pos;
		while(pos < input.length() && input.charAt(pos) != '\n') {
			pos++;
		}
		return new Comment(input.substring(start,pos),start);
	}
	
	public Token scanDigits() {		
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
				return new Int(r,input.substring(start,pos),start);
			}
			while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
				pos = pos + 1;
			}			
			BigRational r = new BigRational(input.substring(start, pos));
			return new Real(r,input.substring(start,pos),start);
		} else {
			BigInteger r = new BigInteger(input.substring(start, pos));
			return new Int(r,input.substring(start,pos),start);			
		}		
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
				default:
					syntaxError("unrecognised escape character",pos);
			}
		}
		if(input.charAt(pos) != '\'') {
			syntaxError("unexpected end-of-character",pos);
		}
		pos = pos + 1;
		return new Int(BigInteger.valueOf(c),input.substring(start,pos),start);
	}
	
	public Token scanString() {
		int start = pos;
		pos ++;
		while(pos < input.length()) {
			char c = input.charAt(pos);			
			if (c == '"') {				
				String v = input.substring(start,++pos);
				return new Strung(parseString(v),v, start);
			}
			pos = pos + 1;
		}
		syntaxError("unexpected end-of-string",pos-1);
		return null;
	}
	
	protected String parseString(String v) {				
		/*
         * Parsing a string requires several steps to be taken. First, we need
         * to strip quotes from the ends of the string.
         */
		v = v.substring(1, v.length() - 1);
		int start = pos - v.length();
		// Second, step through the string and replace escaped characters
		for (int i = 0; i < v.length(); i++) {
			if (v.charAt(i) == '\\') {
				if (v.length() <= i + 1) {
					syntaxError("unexpected end-of-string",start+i);
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
							String unicode = v.substring(i + 2, i + 6);
							replace = (char) Integer.parseInt(unicode, 16); // unicode
							break;
						default :
							syntaxError("unknown escape character",start+i);							
					}
					v = v.substring(0, i) + replace + v.substring(i + len);
				}
			}
		}
		return v;
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
			'*', '/', '!', '?', '=', '<', '>', ':', ';', '&', '|', '.','~',
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
				pos += 2;
				return new DotDot(pos-2);
			} else {
				return new Dot(pos++);
			}
		} else if(c == ',') {
			return new Comma(pos++);
		} else if(c == ':') {
			return new Colon(pos++);
		} else if(c == ';') {
			return new SemiColon(pos++);
		} else if(c == '(') {
			return new LeftBrace(pos++);
		} else if(c == ')') {
			return new RightBrace(pos++);
		} else if(c == '[') {
			return new LeftSquare(pos++);
		} else if(c == ']') {
			return new RightSquare(pos++);
		} else if(c == '{') {
			return new LeftCurly(pos++);
		} else if(c == '}') {
			return new RightCurly(pos++);
		} else if(c == '+') {
			return new Plus(pos++);
		} else if(c == '-') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '>') {
				pos += 2;
				return new Arrow("->",pos-2);
			} else {
				return new Minus(pos++);				
			}			
		} else if(c == '*') {
			return new Star(pos++);
		} else if(c == '&') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '&') {
				pos += 2;
				return new LogicalAnd("&&",pos-2);
			} else {
				return new AddressOf("&",pos++);
			}
		} else if(c == '|') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '|') {
				pos += 2;
				return new LogicalOr("||",pos-2);
			} else {
				return new Bar(pos++);
			}
		} else if(c == '/') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '/') {
				return scanComment();
			} else {
				return new RightSlash(pos++);
			}
		} else if(c == '!') {			
			if((pos+1) < input.length() && input.charAt(pos+1) == '=') {
				pos += 2;
				return new NotEquals("!=",pos-2);
			} else {
				return new Shreak(pos++);				
			}			
		} else if(c == '?') {						
			return new Question(pos++);							
		} else if(c == '=') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '=') {
				pos += 2;
				return new EqualsEquals(pos-2);
			} else {
				return new Equals(pos++);				
			}
		} else if(c == '<') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '=') {
				pos += 2;
				return new LessEquals("<=",pos-2);
			} else {
				return new LeftAngle(pos++);
			}
		} else if(c == '>') {
			if((pos+1) < input.length() && input.charAt(pos+1) == '=') {
				pos += 2;
				return new GreaterEquals(">=",pos - 2);
			} else {
				return new RightAngle(pos++);
			}
		} else if (c == '~' && (pos + 1) < input.length()
				&& input.charAt(pos + 1) == '=') {
			pos += 2;
			return new TypeEquals(pos - 2);
		} else if(c == UC_LESSEQUALS) {
			return new LessEquals(""+UC_LESSEQUALS,pos++);
		} else if(c == UC_GREATEREQUALS) {
			return new GreaterEquals(""+UC_GREATEREQUALS,pos++);
		} else if(c == UC_SETUNION) {
			return new Union(""+UC_SETUNION,pos++);
		} else if(c == UC_SETINTERSECTION) {
			return new Intersection(""+UC_SETINTERSECTION,pos++);
		} else if(c == UC_ELEMENTOF) {
			return new ElemOf(""+UC_ELEMENTOF,pos++);
		} else if(c == UC_SUBSET) {
			return new Subset(""+UC_SUBSET,pos++);
		} else if(c == UC_SUBSETEQ) {
			return new SubsetEquals(""+UC_SUBSETEQ,pos++);
		} else if(c == UC_SUPSET) {
			return new Supset(""+UC_SUPSET,pos++);
		} else if(c == UC_SUPSETEQ) {
			return new SupsetEquals(""+UC_SUPSETEQ,pos++);
		} else if(c == UC_EMPTYSET) {
			return new EmptySet(""+UC_EMPTYSET,pos++);
		} else if(c == UC_LOGICALOR) {
			return new LogicalOr(""+UC_LOGICALOR,pos++);
		} else if(c == UC_LOGICALAND) {
			return new LogicalAnd(""+UC_LOGICALAND,pos++);
		} 
				
		syntaxError("unknown operator encountered: " + c);
		return null;
	}
	
	public boolean isIdentifierStart(char c) {
		return Character.isJavaIdentifierStart(c);
	}
	
	public static final String[] keywords = {		
		"true",
		"false",
		"null",
		"int",
		"real",
		"bool",
		"process",
		"void",			
		"if",
		"while",
		"else",
		"where",
		"requires",
		"ensures",
		"as",
		"for",
		"assert",
		"debug",
		"print",
		"return",
		"define",		
		"function",
		"import",
		"package",
		"public",
		"extern",
		"spawn"
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
				return new Keyword(text,start);
			}
		}
		
		// now, check for text operators
		if(text.equals("in")) {
			return new ElemOf(text,start);
		} else if(text.equals("no")) {
			return new None(text,start);
		} else if(text.equals("some")) {
			return new Some(text,start);
		} 		
	
		// otherwise, must be identifier
		return new Identifier(text,start);
	}
	
	public Token scanTabs() {
		int start = pos;
		int ntabs = 0;
		while (pos < input.length() && input.charAt(pos) == '\t') {
			pos++;
			ntabs++;
		}
		return new Tabs(input.substring(start, pos), ntabs, start);	
	}
	
	public void skipWhitespace(List<Token> tokens) {		
		int start = pos;		
		while (pos < input.length() && input.charAt(pos) != '\n'
			&& input.charAt(pos) == ' ') {			
			pos++;		
		}
		int ts = (pos - start) / 4;
		if(ts > 0) {			
			tokens.add(new Tabs(input.substring(start,pos),ts,start));
		}
		while (pos < input.length() && input.charAt(pos) != '\n'
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
		
		public Token(String text, int pos) {
			this.text = text;
			this.start = pos;			
		}
			
		public int end() {
			return start + text.length() - 1;
		}
	}
	
	public static class Real extends Token {
		public final BigRational value;
		public Real(BigRational r, String text, int pos) { 
			super(text,pos);
			value = r;
		}
	}
	public static class Int extends Token {
		public final BigInteger value;
		public Int(BigInteger r, String text, int pos) { 
			super(text,pos);
			value = r;
		}
	}
	public static class Identifier extends Token {
		public Identifier(String text, int pos) { super(text,pos); }
	}
	public static class Strung extends Token {
		public final String string;
		public Strung(String string, String text, int pos) { 
			super(text,pos);
			this.string = string;
		}
	}	
	public static class Keyword extends Token {
		public Keyword(String text, int pos) { super(text,pos); }
	}
	public static class NewLine extends Token {
		public NewLine(int pos) { super("\n",pos); }
	}	
	public static class Tabs extends Token {
		public int ntabs;
		public Tabs(String text, int ntabs, int pos) { 
			super(text,pos);
			this.ntabs = ntabs; 
		}		
	}
	public static class Comment extends Token {
		public Comment(String text, int pos) { super(text,pos);	}
	}
	public static class Comma extends Token {
		public Comma(int pos) { super(",",pos);	}
	}
	public static class Colon extends Token {
		public Colon(int pos) { super(":",pos);	}
	}
	public static class SemiColon extends Token {
		public SemiColon(int pos) { super(";",pos);	}
	}
	public static class LeftBrace extends Token {
		public LeftBrace(int pos) { super("(",pos);	}
	}
	public static class RightBrace extends Token {
		public RightBrace(int pos) { super(")",pos);	}
	}
	public static class LeftSquare extends Token {
		public LeftSquare(int pos) { super("[",pos);	}
	}
	public static class RightSquare extends Token {
		public RightSquare(int pos) { super("]",pos);	}
	}
	public static class LeftAngle extends Token {
		public LeftAngle(int pos) { super("<",pos);	}
	}
	public static class RightAngle extends Token {
		public RightAngle(int pos) { super(">",pos);	}
	}
	public static class LeftCurly extends Token {
		public LeftCurly(int pos) { super("{",pos);	}
	}
	public static class RightCurly extends Token {
		public RightCurly(int pos) { super("}",pos);	}
	}
	public static class Plus extends Token {
		public Plus(int pos) { super("+",pos);	}
	}
	public static class Minus extends Token {
		public Minus(int pos) { super("-",pos);	}
	}
	public static class Star extends Token {
		public Star(int pos) { super("*",pos);	}
	}
	public static class LeftSlash extends Token {
		public LeftSlash(int pos) { super("\\",pos);	}
	}
	public static class RightSlash extends Token {
		public RightSlash(int pos) { super("/",pos);	}
	}
	public static class Shreak extends Token {
		public Shreak(int pos) { super("!",pos);	}
	}
	public static class Question extends Token {
		public Question(int pos) { super("?",pos);	}
	}
	public static class Dot extends Token {
		public Dot(int pos) { super(".",pos);	}
	}
	public static class DotDot extends Token {
		public DotDot(int pos) { super("..",pos);	}
	}
	public static class Bar extends Token {
		public Bar(int pos) { super("|",pos);	}
	}	
	public static class Equals extends Token {
		public Equals(int pos) { super("=",pos);	}
	}
	public static class EqualsEquals extends Token {
		public EqualsEquals(int pos) { super("==",pos);	}
	}
	public static class NotEquals extends Token {
		public NotEquals(String text, int pos) { super(text,pos);	}
	}
	public static class LessEquals extends Token {
		public LessEquals(String text, int pos) { super(text,pos);	}
	}
	public static class GreaterEquals extends Token {
		public GreaterEquals(String text, int pos) { super(text,pos);	}
	}
	public static class TypeEquals extends Token {
		public TypeEquals(int pos) { super("~=",pos);	}
	}
	public static class None extends Token {
		public None(String text, int pos) { super(text,pos);	}
	}
	public static class Some extends Token {
		public Some(String text, int pos) { super(text,pos);	}
	}
	public static class ElemOf extends Token {
		public ElemOf(String text, int pos) { super(text,pos);	}
	}
	public static class Union extends Token {
		public Union(String text, int pos) { super(text,pos);	}
	}
	public static class Intersection extends Token {
		public Intersection(String text, int pos) { super(text,pos);	}
	}
	public static class EmptySet extends Token {
		public EmptySet(String text, int pos) { super(text,pos);	}
	}
	public static class Subset extends Token {
		public Subset(String text, int pos) { super(text,pos);	}
	}
	public static class Supset extends Token {
		public Supset(String text, int pos) { super(text,pos);	}
	}
	public static class SubsetEquals extends Token {
		public SubsetEquals(String text, int pos) { super(text,pos);	}
	}
	public static class SupsetEquals extends Token {
		public SupsetEquals(String text, int pos) { super(text,pos);	}
	}
	public static class LogicalAnd extends Token {
		public LogicalAnd(String text, int pos) { super(text,pos);	}
	}
	public static class LogicalOr extends Token {
		public LogicalOr(String text, int pos) { super(text,pos);	}
	}
	public static class LogicalNot extends Token {
		public LogicalNot(String text, int pos) { super(text,pos);	}
	}
	public static class AddressOf extends Token {
		public AddressOf(String text, int pos) { super(text,pos);	}
	}
	public static class BitwiseOr extends Token {
		public BitwiseOr(String text, int pos) { super(text,pos);	}
	}
	public static class BitwiseNot extends Token {
		public BitwiseNot(String text, int pos) { super(text,pos);	}
	}
	public static class Arrow extends Token {
		public Arrow(String text, int pos) { super(text,pos);	}
	}
}
