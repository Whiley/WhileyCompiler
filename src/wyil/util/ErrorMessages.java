package wyil.util;

/**
 * <p>
 * Contains global constants mapping syntax errors to their error messages. The
 * purpose of this class is bring all syntax error messages into one place, such
 * that:
 * </p>
 * <ol>
 * <li>It's easy to change error messages, and be sure the changes are applied
 * universally.</li>
 * <li>It's easy to reflect on the set of all error messages, and perhaps find
 * ways to improve them.</li>
 * </ol>
 * <p>
 * In short, error message reporting is done poorly by modern compilers and it
 * would be nice to do a better job!
 * </p>
 * 
 * @author djp
 * 
 */
public class ErrorMessages {

	public static final int UNKNOWN_WYIL_CODE = 0;
	public static final int INVALID_CONSTANT_EXPRESSION = 1;
	public static final int INVALID_BOOLEAN_EXPRESSION = 2;
	public static final int INVALID_NUMERIC_EXPRESSION = 3;
	public static final int INVALID_BINARY_EXPRESSION = 4;
	public static final int INVALID_LIST_EXPRESSION = 5;
	public static final int INVALID_SET_EXPRESSION = 6;
	public static final int SUBTYPE_ERROR = 7;
	
		private final static String[] messages = {
			"unknown wyil bytecode encountered ($1)",
			"invalid constant expression",
			"invalid boolean expression",
			"invalid numeric expression",
			"invalid binary expression",
			"invalid list expression",
			"invalid set expression",
			"expected type $1, found $2"
	};
	
	/**
	 * Return the error message for a given error kind. Supplementary data may
	 * be required in some cases, and failure to provide the right data causes
	 * an exception of some kind.
	 * 
	 * @param kind
	 * @param data
	 * @return
	 */
	public static String errorMessage(int kind, Object... data) {
		if(kind < 0 || kind >= messages.length) {
			throw new IllegalArgumentException("Invalid error kind");
		}
		String r = messages[kind];
		for (int i = 0; i != data.length; ++i) {
			r = r.replaceAll("$" + i, data.toString());
		}
		return r;		
	}
}
