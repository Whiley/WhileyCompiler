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
}
