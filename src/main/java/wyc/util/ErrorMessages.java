// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyc.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Template;
import wyil.lang.WyilFile.Type;

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
 * @author David J. Pearce
 *
 */
public class ErrorMessages {

	private interface Message {
		public String getMessage(Tuple<SyntacticItem> context);
	}

	/**
	 * A static message is a single message which, once created, never changes.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static final class StaticMessage implements Message {
		private final String message;

		public StaticMessage(String message) {
			this.message = message;
		}

		@Override
		public String getMessage(Tuple<SyntacticItem> context) {
			return message;
		}
	}

	/**
	 * A multi-part message is a dynamic message which is created based upon the
	 * context in which it exists.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static final class MultiPartMessage implements Message {
		private final String[] parts;

		public MultiPartMessage(String... parts) {
			this.parts = parts;
		}

		@Override
		public String getMessage(Tuple<SyntacticItem> context) {
			String output = "";
			for(int i=0;i!=parts.length;++i) {
				output += parts[i];
				if(i < context.size()) {
					output += context.get(i).toString();
				}
			}
			return output;
		}
	}

	// ========================================================================
	// Name Resolution
	// ========================================================================
	public static final Message AMBIGUOUS_RESOLUTION = new Message() {
		@Override
		public String getMessage(Tuple<SyntacticItem> context) {
			String msg = "unable to resolve name (is ambiguous)";
			return msg + foundCandidatesString((Tuple) context);
		}
	};
	// ========================================================================
	// Name Resolution
	// ========================================================================
	public static final StaticMessage RESOLUTION_ERROR = new StaticMessage("unable to resolve name");
	public static final StaticMessage MISSING_TEMPLATE_PARAMETERS = new StaticMessage("insufficient template parameters");
	public static final StaticMessage TOOMANY_TEMPLATE_PARAMETERS = new StaticMessage("too many template parameters");
	// ========================================================================
	// Type Checking
	// ========================================================================
	public static final MultiPartMessage SUBTYPE_ERROR = new MultiPartMessage("expected type ",", found ");
	public static final StaticMessage EMPTY_TYPE  = new StaticMessage("empty type encountered");
	public static final StaticMessage EXPECTED_ARRAY  = new StaticMessage("expected array type");
	public static final StaticMessage EXPECTED_RECORD  = new StaticMessage("expected record type");
	public static final StaticMessage EXPECTED_REFERENCE  = new StaticMessage("expected reference type");
	public static final StaticMessage EXPECTED_LAMBDA  = new StaticMessage("expected lambda");
	public static final StaticMessage INVALID_FIELD  = new StaticMessage("invalid field access");
	public static final StaticMessage MISSING_RETURN_STATEMENT = new StaticMessage("missing return statement");
	public static final StaticMessage UNREACHABLE_CODE = new StaticMessage("unreachable code encountered (i.e. execution can never reach this statement)");
	public static final StaticMessage BRANCH_ALWAYS_TAKEN = new StaticMessage("branch always taken");
	public static final MultiPartMessage INCOMPARABLE_OPERANDS = new MultiPartMessage("incomparable operands: "," and ");
    public static final StaticMessage INSUFFICIENT_ARGUMENTS = new StaticMessage("insufficient arguments for function or method invocation");
	public static final StaticMessage TOO_MANY_RETURNS = new StaticMessage("too many return values");
	public static final StaticMessage INSUFFICIENT_RETURNS = new StaticMessage("insufficient return values");
	public static final StaticMessage INVALID_LVAL_EXPRESSION = new StaticMessage("invalid assignment expression");

	// ========================================================================
	// Definite (Un)Assignment
	// ========================================================================
	public static final StaticMessage VARIABLE_POSSIBLY_UNITIALISED = new StaticMessage("variable may be uninitialised");
	public static final StaticMessage PARAMETER_REASSIGNED = new StaticMessage("cannot assign parameter");
	public static final StaticMessage FINAL_VARIABLE_REASSIGNED = new StaticMessage("cannot reassign final variable");

	// ========================================================================
	// Static Variable Check
	// ========================================================================
	public static final StaticMessage CYCLIC_STATIC_INITIALISER = new StaticMessage("cyclic static initialiser");

	// ========================================================================
	// Functional Check
	// ========================================================================
	public static final StaticMessage ALLOCATION_NOT_PERMITTED = new StaticMessage("object allocation not permitted");
	public static final StaticMessage REFERENCE_NOT_PERMITTED = new StaticMessage("reference not permitted");
	public static final StaticMessage METHODCALL_NOT_PERMITTED = new StaticMessage("method invocation not permitted");
	public static final StaticMessage REFERENCE_ACCESS_NOT_PERMITTED = new StaticMessage("dereference not permitted");

	// ========================================================================
	// Ambiguous Coercion Check
	// ========================================================================
	public static final MultiPartMessage AMBIGUOUS_COERCION = new MultiPartMessage("ambiguous coercion required ("," to ",")");

	// ========================================================================
	// Verification
	// ========================================================================
	public static final StaticMessage PRECONDITION_NOT_SATISFIED = new StaticMessage("precondition not satisfied");
	public static final StaticMessage POSTCONDITION_NOT_SATISFIED = new StaticMessage("postcondition not satisfied");
	public static final StaticMessage TYPEINVARIANT_NOT_SATISFIED = new StaticMessage("type invariant not satisfied");
	public static final StaticMessage LOOPINVARIANT_NOT_ESTABLISHED = new StaticMessage("loop invariant not established");
	public static final StaticMessage LOOPINVARIANT_NOT_RESTORED = new StaticMessage("loop invariant not restored");
	public static final StaticMessage PRECONDITION_MAYBE_NOT_SATISFIED = new StaticMessage(
			"precondition may not be satisfied");
	public static final StaticMessage POSTCONDITION_MAYBE_NOT_SATISFIED = new StaticMessage(
			"postcondition may not be satisfied");
	public static final StaticMessage TYPEINVARIANT_MAYBE_NOT_SATISFIED = new StaticMessage(
			"type invariant may not be satisfied");
	public static final StaticMessage LOOPINVARIANT_MAYBE_NOT_ESTABLISHED = new StaticMessage(
			"loop invariant may not be established");
	public static final StaticMessage LOOPINVARIANT_MAYBE_NOT_RESTORED = new StaticMessage(
			"loop invariant may not be restored");
	public static final StaticMessage ASSERTION_FAILED = new StaticMessage("assertion failed");
	public static final StaticMessage ASSUMPTION_FAILED = new StaticMessage("assumption failed");
	public static final StaticMessage INDEX_BELOW_BOUNDS = new StaticMessage("index out-of-bounds (negative)");
	public static final StaticMessage INDEX_ABOVE_BOUNDS = new StaticMessage("index out-of-bounds (not less than length)");
	public static final StaticMessage NEGATIVE_LENGTH = new StaticMessage("negative array length");
	public static final StaticMessage NEGATIVE_RANGE = new StaticMessage("negative array range");
	public static final StaticMessage DIVISION_BY_ZERO = new StaticMessage("division by zero");
	public static final StaticMessage RUNTIME_FAULT = new StaticMessage("runtime fault encountered");

	// ========================================================================
	// Misc
	// ========================================================================
	public static final StaticMessage INVALID_CONSTANT_EXPRESSION = new StaticMessage("invalid constant expression");
	public static final StaticMessage INVALID_BOOLEAN_EXPRESSION = new StaticMessage("invalid boolean expression");
	public static final StaticMessage INVALID_NUMERIC_EXPRESSION = new StaticMessage("invalid numeric expression");
	public static final StaticMessage INVALID_UNARY_EXPRESSION = new StaticMessage("invalid unary expression");
	public static final StaticMessage INVALID_BINARY_EXPRESSION = new StaticMessage("invalid binary expression");
	public static final StaticMessage INVALID_ARRAY_EXPRESSION  = new StaticMessage("invalid array expression");

//	public static final StaticMessage INVALID_TUPLE_LVAL = new StaticMessage("invalid tuple lval");
//	public static final StaticMessage INVALID_FILE_ACCESS = new StaticMessage("invalid file access");
//	public static final StaticMessage INVALID_PACKAGE_ACCESS = new StaticMessage("invalid package access");
//	public static final StaticMessage BREAK_OUTSIDE_SWITCH_OR_LOOP = new StaticMessage("break outside switch or loop");
//	public static final StaticMessage CONTINUE_OUTSIDE_LOOP = new StaticMessage("continue outside loop");

//	public static final StaticMessage UNKNOWN_VARIABLE = new StaticMessage("unknown variable");
//	public static final StaticMessage UNKNOWN_FUNCTION_OR_METHOD = new StaticMessage("unknown function or method");

//	public static final MultiPartMessage VARIABLE_ALREADY_DEFINED = new MultiPartMessage("variable ", " already defined");
//	public static final StaticMessage DUPLICATE_DEFAULT_LABEL = new StaticMessage("duplicate default label");
//	public static final StaticMessage DUPLICATE_CASE_LABEL = new StaticMessage("duplicate case label");
//	public static final StaticMessage DEAD_CODE = new StaticMessage("dead-code encountered (i.e. this statement has no effect)");

//	public static final MultiPartMessage RECORD_TYPE_REQUIRED = new MultiPartMessage("record required, got: ");
//	public static final MultiPartMessage REFERENCE_TYPE_REQUIRED = new MultiPartMessage("reference required, got: ");
//	public static final MultiPartMessage FUNCTION_OR_METHOD_TYPE_REQUIRED = new MultiPartMessage("function or method required, got: ");
//	public static final MultiPartMessage RECORD_MISSING_FIELD = new MultiPartMessage("record has no field named ");
//	public static final StaticMessage RETURN_FROM_VOID = new StaticMessage("cannot return value from method with void return type");
//	public static final StaticMessage MISSING_RETURN_VALUE = new StaticMessage("missing return value");

	private static final Message[][] ERROR_MESSAGES = {
		null, // 00
		null, // 01
		null, // 02
		null, // 03
		{
			SUBTYPE_ERROR,     // 400
			EMPTY_TYPE,        // 401
			EXPECTED_ARRAY,    // 402
			EXPECTED_RECORD,   // 403
			EXPECTED_REFERENCE,// 404
			EXPECTED_LAMBDA,   //405
			INVALID_FIELD,     // 406
			RESOLUTION_ERROR,   // 407
			AMBIGUOUS_COERCION,   // 408
			MISSING_TEMPLATE_PARAMETERS,   // 409
			TOOMANY_TEMPLATE_PARAMETERS,   // 409
		},
		{
			MISSING_RETURN_STATEMENT, // 500;
			null,
			null,
			null,
			UNREACHABLE_CODE,        // 504;
			null,
			BRANCH_ALWAYS_TAKEN,     // 506;
			TOO_MANY_RETURNS,        // 507;
			INSUFFICIENT_RETURNS,    // 508;
			CYCLIC_STATIC_INITIALISER, // 509;
		},
		{
			// Expressions
			null,
			VARIABLE_POSSIBLY_UNITIALISED, // 601
			INCOMPARABLE_OPERANDS,  // 602;
			INSUFFICIENT_ARGUMENTS, // 603;
			AMBIGUOUS_RESOLUTION,   // 604;
			PARAMETER_REASSIGNED,   // 605;
			FINAL_VARIABLE_REASSIGNED,   // 606;
			ALLOCATION_NOT_PERMITTED,   // 607;
			METHODCALL_NOT_PERMITTED,   // 608;
			REFERENCE_ACCESS_NOT_PERMITTED,   // 609;
			INVALID_LVAL_EXPRESSION,   // 610;
		},
		{
			// Verification
			PRECONDITION_NOT_SATISFIED, // 700
			POSTCONDITION_NOT_SATISFIED, // 701
			TYPEINVARIANT_NOT_SATISFIED, // 702;
			LOOPINVARIANT_NOT_ESTABLISHED, // 703;
			LOOPINVARIANT_NOT_RESTORED, // 704;
			ASSERTION_FAILED, // 705
			ASSUMPTION_FAILED, // 706
			INDEX_BELOW_BOUNDS, // 707;
			INDEX_ABOVE_BOUNDS, // 708;
			NEGATIVE_LENGTH, // 709;
			NEGATIVE_RANGE, // 710;
			DIVISION_BY_ZERO, // 711;
			RUNTIME_FAULT, // 712
			null, // 713
			null, // 714
			null, // 715
			PRECONDITION_MAYBE_NOT_SATISFIED, // 716
			POSTCONDITION_MAYBE_NOT_SATISFIED, // 717
			TYPEINVARIANT_MAYBE_NOT_SATISFIED, // 718;
			LOOPINVARIANT_MAYBE_NOT_ESTABLISHED, // 719;
			LOOPINVARIANT_MAYBE_NOT_RESTORED, // 720;
		}
	};

	//
	public static String getErrorMessage(int code, Tuple<SyntacticItem> context) {
		int base = code / 100;
		int offset = code % 100;
		Message m = ERROR_MESSAGES[base][offset];
		//
		return m.getMessage(context);
	}


	/**
	 * Report an error message. This may additionally sanity check the supplied
	 * context.
	 * @param e
	 * @param code
	 * @param context
	 */
	public static void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		WyilFile wf = (WyilFile) e.getHeap();
		// Allocate syntax error in the heap));
		SyntacticItem.Marker m = wf.allocate(new WyilFile.SyntaxError(code, e, new Tuple<>(context)));
		// Record marker to ensure it gets written to disk
		wf.getModule().addAttribute(m);
	}

	// =============================================================================
	// Helpers
	// =============================================================================

	private static String foundCandidatesString(Tuple<Decl.Callable> candidates) {
		ArrayList<String> candidateStrings = new ArrayList<>();
		for (Decl.Callable c : candidates) {
			candidateStrings.add(candidateString(c, null));
		}
		Collections.sort(candidateStrings); // make error message deterministic!
		StringBuilder msg = new StringBuilder();
		for (String s : candidateStrings) {
			msg.append("\n\tfound ");
			msg.append(s);
		}
		return msg.toString();
	}

	private static String candidateString(Decl.Callable decl, Map<Identifier, SyntacticItem> binding) {
		String r;
		if (decl instanceof Decl.Method) {
			r = "method ";
		} else if (decl instanceof Decl.Function) {
			r = "function ";
		} else {
			r = "property ";
		}
		Type.Callable type = decl.getType();
		return r + decl.getQualifiedName() + bindingString(decl,binding) + type.getParameters() + "->" + type.getReturns();
	}

//	private String foundBindingsString(Collection<? extends Binding> candidates) {
//		ArrayList<String> candidateStrings = new ArrayList<>();
//		for (Binding b : candidates) {
//			Decl.Callable c = b.getCandidiateDeclaration();
//			candidateStrings.add(candidateString(c,b.getBinding()));
//		}
//		Collections.sort(candidateStrings); // make error message deterministic!
//		StringBuilder msg = new StringBuilder();
//		for (String s : candidateStrings) {
//			msg.append("\n\tfound ");
//			msg.append(s);
//		}
//		return msg.toString();
//	}

	private static String bindingString(Decl.Callable decl, Map<Identifier, SyntacticItem> binding) {
		if (binding != null && decl instanceof Decl.Method) {
			Decl.Method method = (Decl.Method) decl;
			String r = "<";

			Tuple<Template.Variable> template = method.getTemplate();
			for (int i = 0; i != template.size(); ++i) {
				Identifier lifetime = template.get(i).getName();
				if (i != 0) {
					r += ",";
				}
				r = r + lifetime + "=" + binding.get(lifetime);
			}
			return r + ">";
		} else {
			return "";
		}
	}

}
