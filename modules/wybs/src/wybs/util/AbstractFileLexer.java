package wybs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CharsetDecoder;

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
 * 
 * @author David J. Pearce
 * 
 */
public class AbstractFileLexer {
	/**
	 * The input stream.
	 */
	private StringBuffer input;

	/**
	 * Current position in the input stream.
	 */
	private int pos;

	/**
	 * Construct from an input stream using UTF-8 as the default character
	 * encoding.
	 * 
	 * @param instream
	 * @throws IOException
	 */
	public AbstractFileLexer(InputStream instream) throws IOException {
		this(new InputStreamReader(instream, "UTF-8"));
	}

	/**
	 * Construct from an input stream using a given character set decoder.
	 * 
	 * @param instream
	 * @throws IOException
	 */
	public AbstractFileLexer(InputStream instream, CharsetDecoder decoder)
			throws IOException {
		this(new InputStreamReader(instream, decoder));
	}

	/**
	 * Construct from a reader (which already has some notion of character
	 * enconding included).
	 * 
	 * @param reader
	 * @throws IOException
	 */
	public AbstractFileLexer(Reader reader) throws IOException {
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
	 * A lexer rule is responsible for matching a given character sequence and
	 * turning it into a token.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Rule {
		/**
		 * Determines the maximum amount of lookahead required by this rule.
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
		public abstract Token match(StringBuffer buffer, int start);
	}

	/**
	 * An abstract notion representing a single token in the token stream
	 * produced by lexing a given input stream.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Token {
		public final String text;
		public final int start;

		public Token(String text, int pos) {
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

	
	/**
	 * Whitespace represents denotes the unused portions of the source file
	 * which lie between the significant tokens.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Whitespace extends Token {
		public Whitespace(String text, int pos) {
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
		public Identifier(String text, int pos) {
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
		public Keyword(String text, int pos) {
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
		public Comment(String text, int pos) {
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
		public LineComment(String text, int pos) {
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
		public BlockComment(String text, int pos) {
			super(text, pos);
		}
	}

	/**
	 * Represents the symbol: ','
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Comma extends Token {
		public Comma(int pos) { super(",",pos);	}
	}

	/**
	 * Represents the symbol: ':'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Colon extends Token {
		public Colon(int pos) { super(":",pos);	}
	}
	
	/**
	 * Represents the symbol: ';'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class SemiColon extends Token {
		public SemiColon(int pos) { super(";",pos);	}
	}
	
	/**
	 * Represents the symbol: '('
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class LeftBrace extends Token {
		public LeftBrace(int pos) { super("(",pos);	}
	}
	
	/**
	 * Represents the symbol: ')'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class RightBrace extends Token {
		public RightBrace(int pos) { super(")",pos);	}
	}
	
	/**
	 * Represents the symbol: '['
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class LeftSquare extends Token {
		public LeftSquare(int pos) { super("[",pos);	}
	}
	
	/**
	 * Represents the symbol: ']'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class RightSquare extends Token {
		public RightSquare(int pos) { super("]",pos);	}
	}
	
	/**
	 * Represents the symbol: '<'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class LeftAngle extends Token {
		public LeftAngle(int pos) { super("<",pos);	}
	}
	
	/**
	 * Represents the symbol: '>'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class RightAngle extends Token {
		public RightAngle(int pos) { super(">",pos);	}
	}
	
	/**
	 * Represents the symbol: '{'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class LeftCurly extends Token {
		public LeftCurly(int pos) { super("{",pos);	}
	}
	
	/**
	 * Represents the symbol: '}'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class RightCurly extends Token {
		public RightCurly(int pos) { super("}",pos);	}
	}
	
	/**
	 * Represents the symbol: '+'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Plus extends Token {
		public Plus(int pos) { super("+",pos);	}
	}
	
	/**
	 * Represents the symbol: '++'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class PlusPlus extends Token {
		public PlusPlus(int pos) { super("++",pos);	}
	}
	
	/**
	 * Represents the symbol: '--'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Minus extends Token {
		public Minus(int pos) { super("-",pos);	}
	}
	
	/**
	 * Represents the symbol: '*'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Star extends Token {
		public Star(int pos) { super("*",pos);	}
	}
	
	/**
	 * Represents the symbol: '#'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Hash extends Token {
		public Hash(int pos) { super("#",pos);	}
	}
	
	/**
	 * Represents the symbol: '\'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class LeftSlash extends Token {
		public LeftSlash(int pos) { super("\\",pos);	}
	}
	
	/**
	 * Represents the symbol: '/'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class RightSlash extends Token {
		public RightSlash(int pos) { super("/",pos);	}
	}
	
	/**
	 * Represents the symbol: '!'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Shreak extends Token {
		public Shreak(int pos) { super("!",pos);	}
	}
	
	/**
	 * Represents the symbol: '?'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Question extends Token {
		public Question(int pos) { super("?",pos);	}
	}
	
	/**
	 * Represents the symbol: '%'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Percent extends Token {
		public Percent(int pos) { super("%",pos);	}
	}
	
	/**
	 * Represents the symbol: '&'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Ampersand extends Token {
		public Ampersand(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the symbol: '&&'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class AmpersandAmpersand extends Token {
		public AmpersandAmpersand(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the symbol: '^'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Hat extends Token {
		public Hat(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the symbol: '->'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class RightArrow extends Token {
		public RightArrow(String text, int pos) { super(text,pos);	}
	}	
	
	/**
	 * Represents the symbol: '==>'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class LongRightDoubleArrow extends Token {
		public LongRightDoubleArrow(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the symbol: '<==>'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class LongLeftRightDoubleArrow extends Token {
		public LongLeftRightDoubleArrow(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the symbol: '.'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Dot extends Token {
		public Dot(int pos) { super(".",pos);	}
	}
	
	/**
	 * Represents the symbol: '..'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class DotDot extends Token {
		public DotDot(int pos) { super("..",pos);	}
	}
	
	/**
	 * Represents the symbol: '...'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class DotDotDot extends Token {
		public DotDotDot(int pos) { super("...",pos);	}
	}
	
	/**
	 * Represents the symbol: '|'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Bar extends Token {
		public Bar(int pos) { super("|",pos);	}
	}	
	
	/**
	 * Represents the symbol: '||'
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class BarBar extends Token {
		public BarBar(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the symbol: '='
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Equals extends Token {
		public Equals(int pos) { super("=",pos);	}
	}
	
	/**
	 * Represents the symbol: '=='
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class EqualsEquals extends Token {
		public EqualsEquals(int pos) { super("==",pos);	}
	}
	
	/**
	 * Represents the symbol: '!='
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class NotEquals extends Token {
		public NotEquals(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the symbol: '<='
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class LessEquals extends Token {
		public LessEquals(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the symbol: '>='
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class GreaterEquals extends Token {
		public GreaterEquals(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the unicode symbol: ''
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class ForAll extends Token {
		public ForAll(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the unicode symbol: ''
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Exists extends Token {
		public Exists(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the unicode symbol: ''
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class ElemOf extends Token {
		public ElemOf(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the unicode symbol: ''
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Union extends Token {
		public Union(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the unicode symbol: ''
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Intersection extends Token {
		public Intersection(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the unicode symbol: ''
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class EmptySet extends Token {
		public EmptySet(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the unicode symbol: ''
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Subset extends Token {
		public Subset(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the unicode symbol: ''
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Supset extends Token {
		public Supset(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the unicode symbol: ''
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class SubsetEquals extends Token {
		public SubsetEquals(String text, int pos) { super(text,pos);	}
	}
	
	/**
	 * Represents the unicode symbol: ''
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class SupsetEquals extends Token {
		public SupsetEquals(String text, int pos) { super(text,pos);	}
	}
}
