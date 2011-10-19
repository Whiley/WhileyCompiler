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
	
	public static final int CYCLIC_CONSTANT_DECLARATION = 0;
	public static final int INVALID_CONSTANT_EXPRESSION = 1;
	public static final int INVALID_BOOLEAN_EXPRESSION = 2;
	public static final int INVALID_NUMERIC_EXPRESSION = 3;
	public static final int INVALID_BINARY_EXPRESSION = 4;	
	public static final int INVALID_LIST_EXPRESSION = 5;
	public static final int INVALID_SET_EXPRESSION = 6;
	public static final int INVALID_SET_OR_LIST_EXPRESSION = 7;
	public static final int INVALID_DICTIONARY_EXPRESSION = 8;
	public static final int INVALID_LVAL_EXPRESSION = 9;
	public static final int INVALID_DESTRUCTURE_EXPRESSION = 10;
	public static final int INVALID_TUPLE_LVAL = 12;
	public static final int INVALID_MODULE_ACCESS = 13;
	public static final int INVALID_PACKAGE_ACCESS = 14;	
	public static final int BREAK_OUTSIDE_SWITCH_OR_LOOP = 14;
	public static final int RESOLUTION_ERROR = 15;
	public static final int AMBIGUOUS_VARIABLE = 16;
	public static final int UNKNOWN_VARIABLE = 17;
	public static final int UNKNOWN_FUNCTION_OR_METHOD = 18;
	public static final int VARIABLE_POSSIBLY_UNITIALISED = 19;
	public static final int VARIABLE_ALREADY_DEFINED = 20;
	public static final int DUPLICATE_DEFAULT_LABEL = 21;
	public static final int DUPLICATE_CASE_LABEL = 22;
	public static final int UNREACHABLE_CODE = 23;
	public static final int RECEIVER_NOT_PROCESS = 24;	
	public static final int SUBTYPE_ERROR = 25;
	public static final int RECORD_TYPE_REQUIRED = 26;
	public static final int RECORD_MISSING_FIELD = 27;
	public static final int RETURN_FROM_VOID = 28;
	public static final int MISSING_RETURN_VALUE = 28;
	
		private final static String[] messages = {			
			"cyclic constant expression",
			"invalid constant expression",
			"invalid boolean expression",
			"invalid numeric expression",
			"invalid binary expression",			
			"invalid list expression",
			"invalid set expression",
			"invalid set or list expression",
			"invalid dictionary expression",
			"invalid assignment expression",
			"invalid destructure expression",
			"invalid tuple lval",
			"invalid module access",
			"invalid package access",
			"break outside switch or loop",
			"$1", // resolution error
			"ambiguous variable",
			"unknown variable",
			"unknown function or method",
			"variable may be uninitialised",
			"variable $1 already defined",
			"duplicate default label",
			"duplicate case label",
			"unreachable code",
			"method receiver must have process type",
			"expected type $1, found $2",
			"record required, got: $1",
			"record has no field named $1",
			"cannot return value from method with void return type",
			"missing return value"
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
