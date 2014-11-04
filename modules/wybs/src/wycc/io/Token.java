package wycc.io;

import java.math.BigInteger;

/**
 * An abstract notion representing a single token in the token stream produced
 * by lexing a given input stream. A number of standard token kinds are also
 * provided.
 *
 * @author David J. Pearce
 *
 */
public abstract class Token {
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

	public java.lang.String toString() {
		return "token:" + start + ": \"" + text + "\"";
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
	public abstract static class Whitespace extends Token {
		public Whitespace(java.lang.String text, int pos) {
			super(text, pos);
		}
	}

	/**
	 * Denotes a new line in the source file.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class NewLine extends Whitespace {
		public NewLine(java.lang.String text, int pos) {
			super(text, pos);
		}
	}

	/**
	 * Denotes a sequence of one or more space characters
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Spaces extends Whitespace {
		public Spaces(java.lang.String text, int pos) {
			super(text, pos);
		}
	}

	/**
	 * Denotes a sequence of one or more tab characters
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Tabs extends Whitespace {
		public Tabs(java.lang.String text, int pos) {
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
	public static class String extends Token {
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
	public static class Char extends Token {
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

}