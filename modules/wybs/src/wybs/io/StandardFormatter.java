package wybs.io;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

/**
 * Provides a generic mechanism for formating source files. This strips out all
 * whitespace in a given token stream and inserts new whitespace according to a
 * predefined set of rules. Typically, inserting whitespace amounts to inserting
 * indentation of some sort.
 * 
 * @author David J. Pearce
 * 
 */
public class StandardFormatter {

	/**
	 * The array of rules to be applied when formatting the source file.
	 */
	private final Rule[] rules;
	
	/**
	 * The current indent level within the source file.
	 */
	private int indent;
	
	public StandardFormatter(Rule... rules) {
		this.rules = rules;
	}
	
	public void format(List<Token> tokens) {
		for(int i=0;i!=tokens.size();++i) {
			for(int j=0;j!=rules.length;++j) {
				Rule rule = rules[j];
				i += rule.apply(this,tokens,i);
			}
		}
	}
		
	/**
	 * An abstract rewrite rule which is used to format a token stream.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public abstract static class Rule {
		
		/**
		 * Apply this rewrite rule to the token stream at a given position,
		 * returning the amount to advance afterwards.
		 * 
		 * @param tokens
		 *            --- token stream being rewritten.
		 * @param index
		 *            --- current index in that stream.
		 * @return number of additional tokens inserted.
		 */
		public abstract int apply(StandardFormatter parent, List<Token> input, int index);
	}
	
	/**
	 * This inserts a newline after a given token, and indents the next line to
	 * the current indentation level.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class BreakRule extends Rule {
		private String[] matches;
		
		public BreakRule(String... matches) {
			this.matches = matches;
		}
		
		public int apply(StandardFormatter parent, List<Token> input, int index) {
			Token token = input.get(index);
			for (int i = 0; i != matches.length; ++i) {
				if (matches[i].equals(token.text)) {
					String indent = indent(parent.indent);
					input.add(index+1, new Token.Whitespace("\n" + indent, 0));
					return 1;
				}
			}
			return 0;
		}
	}
	
	/**
	 * This inserts a newline after a given token, increases the current
	 * indentation level, and then indents the next line to that level.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class IndentRule extends Rule {
		private String[] matches;
		
		public IndentRule(String... matches) {
			this.matches = matches;
		}
		
		public int apply(StandardFormatter parent, List<Token> input, int index) {
			Token token = input.get(index);
			for (int i = 0; i != matches.length; ++i) {
				if (matches[i].equals(token.text)) {
					parent.indent++;
					String indent = indent(parent.indent);
					input.add(index+1, new Token.Whitespace("\n" + indent, 0));
					return 1;
				}
			}
			return 0;
		}
	}
	
	/**
	 * This inserts a newline before a given token, decreases the current
	 * indentation level, and then indents the next line to that level.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class UndentRule extends Rule {
		private String[] matches;
		
		public UndentRule(String... matches) {
			this.matches = matches;
		}
		
		public int apply(StandardFormatter parent, List<Token> input, int index) {
			Token token = input.get(index);
			for (int i = 0; i != matches.length; ++i) {
				if (matches[i].equals(token.text)) {
					parent.indent--;
					String indent = indent(parent.indent);
					input.add(index, new Token.Whitespace("\n" + indent, 0));
					return 1;
				}
			}
			return 0;
		}
	}
	
	private static String indent(int indent) {
		String r = "";
		for(int i=0;i!=indent;++i) {
			r += "\t";
		}
		return r;
	}
	
}
