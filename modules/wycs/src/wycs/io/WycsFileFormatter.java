package wycs.io;

import wybs.io.StandardFormatter;

/**
 * Provides a standard formatter for WycsFiles.
 * 
 * @author David J. Pearce
 * 
 */
public class WycsFileFormatter extends StandardFormatter {
	
	public WycsFileFormatter() {
		super(rules);
	}	
	
	private final static Rule[] rules = {
		new StandardFormatter.BreakRule("&&","||"),
		new StandardFormatter.IndentRule(":"),
		new StandardFormatter.UndentRule("]")
	};
}
