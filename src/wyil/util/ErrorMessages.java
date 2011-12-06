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
	public static final int INVALID_CONSTANT_AS_TYPE = 1;
	public static final int INVALID_FUNCTION_OR_METHOD_AS_TYPE = 2;
	public static final int INVALID_CONSTANT_EXPRESSION = 3;
	public static final int INVALID_BOOLEAN_EXPRESSION = 4;
	public static final int INVALID_NUMERIC_EXPRESSION = 5;
	public static final int INVALID_BINARY_EXPRESSION = 6;	
	public static final int INVALID_LIST_EXPRESSION = 7;
	public static final int INVALID_SET_EXPRESSION = 8;
	public static final int INVALID_SET_OR_LIST_EXPRESSION = 9;
	public static final int INVALID_DICTIONARY_EXPRESSION = 10;
	public static final int INVALID_LVAL_EXPRESSION = 11;
	public static final int INVALID_DESTRUCTURE_EXPRESSION = 12;
	public static final int INVALID_TUPLE_LVAL = 13;
	public static final int INVALID_MODULE_ACCESS = 14;
	public static final int INVALID_PACKAGE_ACCESS = 15;	
	public static final int BREAK_OUTSIDE_LOOP = 16;
	public static final int RESOLUTION_ERROR = 17;
	public static final int AMBIGUOUS_VARIABLE = 18;
	public static final int UNKNOWN_VARIABLE = 19;
	public static final int UNKNOWN_FUNCTION_OR_METHOD = 20;
	public static final int VARIABLE_POSSIBLY_UNITIALISED = 21;
	public static final int VARIABLE_ALREADY_DEFINED = 22;
	public static final int DUPLICATE_DEFAULT_LABEL = 23;
	public static final int DUPLICATE_CASE_LABEL = 24;
	public static final int DEAD_CODE = 25;
	public static final int UNREACHABLE_CODE = 26;
	public static final int RECEIVER_NOT_PROCESS = 27;
	public static final int PROCESS_NOT_PERMITTED_IN_FUNCTION = 28;
	public static final int SEND_NOT_PERMITTED_IN_FUNCTION = 29;
	public static final int METHODCALL_NOT_PERMITTED_IN_FUNCTION = 30;
	public static final int SPAWN_NOT_PERMITTED_IN_FUNCTION = 31;
	public static final int PROCESS_ACCESS_NOT_PERMITTED_IN_FUNCTION = 32;
	public static final int SUBTYPE_ERROR = 33;
	public static final int INCOMPARABLE_OPERANDS = 34;
	public static final int RECORD_TYPE_REQUIRED = 35;
	public static final int RECORD_MISSING_FIELD = 36;
	public static final int RETURN_FROM_VOID = 37;
	public static final int MISSING_RETURN_VALUE = 38;
	public static final int BRANCH_ALWAYS_TAKEN = 39;
	public static final int AMBIGUOUS_COERCION = 40;
	public static final int MUST_DECLARE_THROWN_EXCEPTION = 41;
	
		private final static String[] messages = {						
			"cyclic constant expression",
			"constant cannot be used as a type",
			"function or method cannot be used as a type",
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
			"$0", // resolution error
			"ambiguous variable",
			"unknown variable",
			"unknown function or method",
			"variable may be uninitialised",
			"variable $0 already defined",
			"duplicate default label",
			"duplicate case label",
			"dead-code encountered (i.e. this statement has no effect)",
			"unreachable code encountered (i.e. execution can never reach this statement)",
			"method receiver must have process type",
			"process not permitted in function",
			"message send not permitted in function",
			"method invocation not permitted in function",
			"process spawning not permitted in function",
			"process access not permitted in function",
			"expected type $0, found $1",
			"incomparable operands: $0 and $1",
			"record required, got: $0",
			"record has no field named $0",
			"cannot return value from method with void return type",
			"missing return value",
			"branch always taken",
			"ambiguous coercion (from $0 to $1)",
			"exception may be thrown which is not declared"
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
			String rep = data[i].toString();
			r = r.replaceAll("\\$" + i, rep);			
		}
		return r;		
	}
}
