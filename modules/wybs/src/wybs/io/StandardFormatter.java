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
		for(int i=0;i!=tokens.size();) {
			for(int j=0;j!=rules.length;++j) {
				Rule rule = rules[j];
				i += rule.apply(tokens,i);
			}
		}
	}
		
	/**
	 * An abstract rewrite rule which is used to format a token stream.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public abstract class Rule {
		
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
		public abstract int apply(List<Token> input, int index);
	}
	
	/**
	 * This inserts a newline after a given token, and indents the next line to
	 * the current indentation level.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public class BreakRule extends Rule {
		private String[] matches;
		
		public BreakRule(String... matches) {
			this.matches = matches;
		}
		
		public int apply(List<Token> input, int index) {
			Token token = input.get(index);
			for(int i=0;i!=matches.length;++i) {
				if(matches[i].equals(token.text)) {
					input.add(index,new Token.Whitespace("\n",??));
				}
			}
		}
	}
}
