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

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import jbuildgraph.util.ArrayUtils;
import jbuildgraph.util.Trie;
import jbuildstore.core.Content;
import jbuildstore.util.TextFile;
import jsynheap.lang.Syntactic;
import jsynheap.util.AbstractCompilationUnit;
import jsynheap.util.AbstractCompilationUnit.Identifier;
import jsynheap.util.AbstractCompilationUnit.Tuple;
import wyc.lang.WhileyFile;
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
		public String getMessage(Tuple<Syntactic.Item> context);
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
		public String getMessage(Tuple<Syntactic.Item> context) {
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
		public String getMessage(Tuple<Syntactic.Item> context) {
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

	private static final class TypingErrorMessage implements Message {
		private final String[] parts;

		public TypingErrorMessage(String... parts) {
			this.parts = parts;
		}

		@Override
		public String getMessage(Tuple<Syntactic.Item> context) {
			String output = "";
			for(int i=0;i!=parts.length;++i) {
				output += parts[i];
				if(i < context.size()) {
					output += toString(context.get(i));
				}
			}
			return output;
		}

		private String toString(Syntactic.Item context) {
			if(context instanceof Tuple<?>) {
				String r = "";
				Tuple<?> t = (Tuple) context;
				String[] items = new String[t.size()];
				for(int i=0;i!=t.size();++i) {
					items[i] = toString(t.get(i));
				}
				items = ArrayUtils.removeDuplicates(items);
				for(int i=0;i!=items.length;++i) {
					if(i != 0) {
						r += " or ";
					}
					r = r + items[i];
				}
				return r;
			} else {
				return context.toString();
			}
		}
	}


	/**
	 * A multi-part message is a dynamic message which is created based upon the
	 * context in which it exists.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static final class StackTraceMessage implements Message {
		private final String message;

		public StackTraceMessage(String message) {
			this.message = message;
		}

		@Override
		public String getMessage(Tuple<Syntactic.Item> context) {
			String output = message;
			for(int i=0;i!=context.size();++i) {
				output = output + "\n";
				output += "--> " + context.get(i).toString();
			}
			return output;
		}
	}

	// ========================================================================
	// Parsing
	// ========================================================================

	public static final Message EXPECTING_TOKEN = new MultiPartMessage("expecting \"", "\" here");
	public static final Message EXPECTED_LIFETIME = new StaticMessage("expecting lifetime identifier");
	public static final Message UNEXPECTED_EOF = new StaticMessage("unexpected end-of-file");
	public static final Message UNEXPECTED_BLOCK_END = new StaticMessage("unexpected end-of-block");
	public static final Message UNKNOWN_LIFETIME = new StaticMessage("use of undeclared lifetime");
	public static final Message UNKNOWN_TYPE = new StaticMessage("unknown type encountered");
	public static final Message UNKNOWN_LVAL = new StaticMessage("unexpected lval");
	public static final Message UNKNOWN_TERM = new StaticMessage("unrecognised term");
	public static final Message INVALID_UNICODE_LITERAL = new StaticMessage("invalid unicode string");
	public static final Message INVALID_BINARY_LITERAL = new StaticMessage("invalid binary literal");
	public static final Message INVALID_HEX_LITERAL = new StaticMessage("invalid hex literal (invalid characters)");
	public static final Message DUPLICATE_VISIBILITY_MODIFIER = new StaticMessage("visibility modifier already given");
	public static final Message DUPLICATE_TEMPLATE_VARIABLE = new StaticMessage("duplicate template variable");
	public static final Message DUPLICATE_CASE_LABEL = new StaticMessage("duplicate case label");
	public static final Message DUPLICATE_DEFAULT_LABEL = new StaticMessage("duplicate default label");
	public static final Message DUPLICATE_FIELD = new StaticMessage("duplicate record key");
	public static final Message DUPLICATE_DECLARATION = new StaticMessage("name already declared");
	public static final Message MISSING_TYPE_VARIABLE = new StaticMessage("missing type variable(s)");
	public static final Message BREAK_OUTSIDE_SWITCH_OR_LOOP = new StaticMessage("break outside switch or loop");
	public static final Message CONTINUE_OUTSIDE_LOOP = new StaticMessage("continue outside loop");
	public static final Message OLD_REQUIRES_TWOSTATES = new StaticMessage("old requires context with pre- and post-states");

	// ========================================================================
	// Name Resolution
	// ========================================================================
	public static final Message AMBIGUOUS_RESOLUTION = new Message() {
		@Override
		public String getMessage(Tuple<Syntactic.Item> context) {
			String msg = "unable to resolve name (is ambiguous)";
			return msg + foundCandidatesString((Tuple) context);
		}
	};
	// ========================================================================
	// Name Resolution
	// ========================================================================
	public static final Message RESOLUTION_ERROR = new StaticMessage("unable to resolve name");
	public static final Message EXPOSING_HIDDEN_DECLARATION = new StaticMessage("exposing private declaration");
	public static final Message MISSING_TEMPLATE_PARAMETERS = new StaticMessage("insufficient template parameters");
	public static final Message TOOMANY_TEMPLATE_PARAMETERS = new StaticMessage("too many template parameters");
	// ========================================================================
	// Type Checking
	// ========================================================================
	public static final Message SUBTYPE_ERROR = new TypingErrorMessage("expected ",", found ");
	public static final Message EMPTY_TYPE  = new StaticMessage("empty type encountered");
	public static final Message EXPECTED_ARRAY  = new StaticMessage("expected array");
	public static final Message EXPECTED_RECORD  = new StaticMessage("expected record");
	public static final Message EXPECTED_REFERENCE  = new StaticMessage("expected reference");
	public static final Message EXPECTED_LAMBDA  = new StaticMessage("expected lambda");
	public static final Message INVALID_FIELD  = new StaticMessage("invalid field access");
	public static final Message MISSING_RETURN_STATEMENT = new StaticMessage("missing return statement");
	public static final Message UNREACHABLE_CODE = new StaticMessage("unreachable code encountered (i.e. execution can never reach this statement)");
	public static final Message BRANCH_ALWAYS_TAKEN = new StaticMessage("branch always taken");
	public static final Message INCOMPARABLE_OPERANDS = new TypingErrorMessage("incomparable operands "," and ");
    public static final Message INSUFFICIENT_ARGUMENTS = new StaticMessage("insufficient arguments for function or method invocation");
	public static final Message TOO_MANY_RETURNS = new StaticMessage("too many return values");
	public static final Message INSUFFICIENT_RETURNS = new StaticMessage("insufficient return values");
	public static final Message INVALID_LVAL_EXPRESSION = new StaticMessage("invalid assignment expression");
	public static final Message DEREFERENCED_DYNAMICALLY_SIZED = new StaticMessage("cannot dereference dynamically sized type");
	public static final Message DEREFERENCED_UNKNOWN_TYPE = new StaticMessage("cannot dereference unknown type");

	// ========================================================================
	// Definite (Un)Assignment
	// ========================================================================
	public static final Message VARIABLE_POSSIBLY_UNITIALISED = new StaticMessage("variable may be uninitialised");
	public static final Message PARAMETER_REASSIGNED = new StaticMessage("cannot assign parameter");
	public static final Message FINAL_VARIABLE_REASSIGNED = new StaticMessage("cannot reassign final variable");

	// ========================================================================
	// Static Variable Check
	// ========================================================================
	public static final Message CYCLIC_STATIC_INITIALISER = new StaticMessage("cyclic static initialiser");

	// ========================================================================
	// Functional Check
	// ========================================================================
	public static final Message ALLOCATION_NOT_PERMITTED = new StaticMessage("object allocation not permitted");
	public static final Message REFERENCE_NOT_PERMITTED = new StaticMessage("reference not permitted");
	public static final Message METHODCALL_NOT_PERMITTED = new StaticMessage("method invocation not permitted");
	public static final Message UNSAFECALL_NOT_PERMITTED = new StaticMessage("unsafe invocation not permitted in safe context");
	public static final Message REFERENCE_ACCESS_NOT_PERMITTED = new StaticMessage("dereference not permitted");
	public static final Message VARIANTCALL_NOT_PERMITTED = new StaticMessage("variant invocation not permitted");

	// ========================================================================
	// Ambiguous Coercion Check
	// ========================================================================
	public static final Message AMBIGUOUS_COERCION = new TypingErrorMessage("ambiguous coercion from "," to ");

	// ========================================================================
	// Verification
	// ========================================================================
	public static final Message RUNTIME_PRECONDITION_FAILURE = new StackTraceMessage("precondition not satisfied");
	public static final Message RUNTIME_POSTCONDITION_FAILURE = new StackTraceMessage("postcondition not satisfied");
	public static final Message RUNTIME_TYPEINVARIANT_FAILURE = new StackTraceMessage("type invariant not satisfied");
	public static final Message RUNTIME_ESTABLISH_LOOPINVARIANT_FAILURE = new StackTraceMessage("loop invariant not established");
	public static final Message RUNTIME_RESTORE_LOOPINVARIANT_FAILURE = new StackTraceMessage("loop invariant not restored");
	public static final Message RUNTIME_ASSERTION_FAILURE = new StackTraceMessage("assertion failed");
	public static final Message RUNTIME_ASSUMPTION_FAILURE = new StackTraceMessage("assumption failed");
	public static final Message RUNTIME_BELOWBOUNDS_INDEX_FAILURE = new StackTraceMessage("index out of bounds (negative)");
	public static final Message RUNTIME_ABOVEBOUNDS_INDEX_ABOVE_FAILURE = new StackTraceMessage("index out of bounds (not less than length)");
	public static final Message RUNTIME_NEGATIVE_LENGTH_FAILURE = new StackTraceMessage("negative length");
	public static final Message RUNTIME_NEGATIVE_RANGE_FAILURE = new StackTraceMessage("negative array range");
	public static final Message RUNTIME_DIVIDEBYZERO_FAILURE = new StackTraceMessage("division by zero");
	public static final Message RUNTIME_FAULT = new StackTraceMessage("runtime fault");

	public static final Message STATIC_PRECONDITION_FAILURE = new MultiPartMessage(
			"precondition may not be satisfied ");
	public static final Message STATIC_POSTCONDITION_FAILURE = new MultiPartMessage(
			"postcondition may not be satisfied ");
	public static final Message STATIC_TYPEINVARIANT_FAILURE = new MultiPartMessage(
			"type invariant may not be satisfied ");
	public static final Message STATIC_ESTABLISH_LOOPINVARIANT_FAILURE = new MultiPartMessage(
			"loop invariant may not be established by first iteration ");
	public static final Message STATIC_ENTER_LOOPINVARIANT_FAILURE = new MultiPartMessage(
			"loop invariant may not hold on entry ");
	public static final Message STATIC_RESTORE_LOOPINVARIANT_FAILURE = new MultiPartMessage(
			"loop invariant may not be restored ");
	public static final Message STATIC_ASSERTION_FAILURE = new MultiPartMessage("assertion may not hold ");
	public static final Message STATIC_ASSUMPTION_FAILURE = new MultiPartMessage("assumption may not hold ");
	public static final Message STATIC_BELOWBOUNDS_INDEX_FAILURE = new MultiPartMessage(
			"possible index out of bounds (negative) ");
	public static final Message STATIC_ABOVEBOUNDS_INDEX_ABOVE_FAILURE = new MultiPartMessage(
			"possible index out of bounds (not less than length) ");
	public static final Message STATIC_NEGATIVE_LENGTH_FAILURE = new MultiPartMessage("negative length possible ");
	public static final Message STATIC_NEGATIVE_RANGE_FAILURE = new MultiPartMessage("possible negative array range ");
	public static final Message STATIC_DIVIDEBYZERO_FAILURE = new MultiPartMessage("possible division by zero ");
	public static final Message STATIC_FAULT = new MultiPartMessage("possible panic ");
	// ========================================================================
	// Misc
	// ========================================================================
	public static final Message INVALID_CONSTANT_EXPRESSION = new StaticMessage("invalid constant expression");
	public static final Message INVALID_BOOLEAN_EXPRESSION = new StaticMessage("invalid boolean expression");
	public static final Message INVALID_NUMERIC_EXPRESSION = new StaticMessage("invalid numeric expression");
	public static final Message INVALID_UNARY_EXPRESSION = new StaticMessage("invalid unary expression");
	public static final Message INVALID_BINARY_EXPRESSION = new StaticMessage("invalid binary expression");
	public static final Message INVALID_ARRAY_EXPRESSION  = new StaticMessage("invalid array expression");

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
		{
			EXPECTING_TOKEN, // 300
			EXPECTED_LIFETIME, // 301
			UNEXPECTED_EOF, // 302
			UNEXPECTED_BLOCK_END, // 303
			UNKNOWN_LIFETIME, // 304
			UNKNOWN_TYPE, // 305
			UNKNOWN_LVAL, // 306
			UNKNOWN_TERM, // 307
			INVALID_UNICODE_LITERAL, // 308
			INVALID_BINARY_LITERAL, // 309
			INVALID_HEX_LITERAL, // 310
			DUPLICATE_VISIBILITY_MODIFIER, // 311
			DUPLICATE_TEMPLATE_VARIABLE, // 312
			DUPLICATE_CASE_LABEL, // 313
			DUPLICATE_DEFAULT_LABEL, // 314
			DUPLICATE_FIELD, // 315
			DUPLICATE_DECLARATION, // 316
			MISSING_TYPE_VARIABLE, // 317
			BREAK_OUTSIDE_SWITCH_OR_LOOP, // 318
			CONTINUE_OUTSIDE_LOOP, // 319
			OLD_REQUIRES_TWOSTATES, // 320
		},
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
			TOOMANY_TEMPLATE_PARAMETERS,   // 410
			EXPOSING_HIDDEN_DECLARATION,   // 411
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
			DEREFERENCED_DYNAMICALLY_SIZED,   // 611;
			DEREFERENCED_UNKNOWN_TYPE,   // 612;
			UNSAFECALL_NOT_PERMITTED,   // 613
			VARIANTCALL_NOT_PERMITTED,   // 614
		},
		{
			// Verification
			RUNTIME_PRECONDITION_FAILURE, // 700
			RUNTIME_POSTCONDITION_FAILURE, // 701
			RUNTIME_TYPEINVARIANT_FAILURE, // 702;
			RUNTIME_ESTABLISH_LOOPINVARIANT_FAILURE, // 703;
			RUNTIME_RESTORE_LOOPINVARIANT_FAILURE, // 704;
			RUNTIME_ASSERTION_FAILURE, // 705
			RUNTIME_ASSUMPTION_FAILURE, // 706
			RUNTIME_BELOWBOUNDS_INDEX_FAILURE, // 707;
			RUNTIME_ABOVEBOUNDS_INDEX_ABOVE_FAILURE, // 708;
			RUNTIME_NEGATIVE_LENGTH_FAILURE, // 709;
			RUNTIME_NEGATIVE_RANGE_FAILURE, // 710;
			RUNTIME_DIVIDEBYZERO_FAILURE, // 711;
			RUNTIME_FAULT, // 712
			null, // 713
			null, // 714
			null, // 715
			STATIC_PRECONDITION_FAILURE, // 716
			STATIC_POSTCONDITION_FAILURE, // 717
			STATIC_TYPEINVARIANT_FAILURE, // 718;
			STATIC_ESTABLISH_LOOPINVARIANT_FAILURE, // 719;
			STATIC_ENTER_LOOPINVARIANT_FAILURE, // 720;
			STATIC_RESTORE_LOOPINVARIANT_FAILURE, // 721;
			STATIC_ASSERTION_FAILURE, // 722
			STATIC_ASSUMPTION_FAILURE, // 723
			STATIC_BELOWBOUNDS_INDEX_FAILURE, // 724
			STATIC_ABOVEBOUNDS_INDEX_ABOVE_FAILURE, // 725
			STATIC_NEGATIVE_LENGTH_FAILURE, // 726
			STATIC_NEGATIVE_RANGE_FAILURE, // 727
			STATIC_DIVIDEBYZERO_FAILURE, // 728
			STATIC_FAULT, // 729
		}
	};

	//
	public static String getErrorMessage(int code, Tuple<Syntactic.Item> context) {
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
	public static void syntaxError(Syntactic.Item e, int code, Syntactic.Item... context) {
		if(e == null) {
			throw new IllegalArgumentException("Syntactic item cannot be null");
		}
		WyilFile wf = (WyilFile) e.getHeap();
		// Allocate syntax error in the heap));
		Syntactic.Marker m = wf.allocate(new WyilFile.Attr.SyntaxError(code, e, new Tuple<>(context)));
		// Record marker to ensure it gets written to disk
		wf.getModule().addAttribute(m);
	}

	// =============================================================================
	// Print Markers
	// =============================================================================

	/**
	 * Print out all syntactic markers active within a given piece of content.
	 *
	 * @param executor
	 * @throws IOException
	 */
	public static void printSyntacticMarkers(PrintStream output, WyilFile target) throws IOException {
		// Extract all syntactic markers from entries in the build graph
		List<Syntactic.Marker> items = extractSyntacticMarkers(target);
		// For each marker, print out error messages appropriately
		for (int i = 0; i != items.size(); ++i) {
			// Log the error message
			printSyntacticMarkers(output, items.get(i), target.getSourceArtifacts());
		}
	}

	/**
	 * Print a given syntactic marker.
	 *
	 * @param output
	 * @param marker
	 * @param sources
	 */
	public static void printSyntacticMarkers(PrintStream output, Syntactic.Marker marker,
			List<WhileyFile> sources) {
		// Identify enclosing source file
		WhileyFile source = getSourceEntry(marker.getSource(), sources);
		String filename = marker.getSource().toString() + ".whiley";
		// Determine the source-file span for the given syntactic marker.
		Syntactic.Span span = marker.getTarget().getAncestor(AbstractCompilationUnit.Attribute.Span.class);
		// Read the enclosing line so we can print it
		TextFile.Line line = source.getEnclosingLine(span.getStart());
		// Sanity check we found it
		if (line != null) {
			// print the error message
			output.println(filename + ":" + line.getNumber() + ": " + marker.getMessage());
			// Finally print the line highlight
			printLineHighlight(output, span, line);
		} else {
			output.println(filename + ":?: " + marker.getMessage());
		}
	}

	public static List<Syntactic.Marker> extractSyntacticMarkers(Content... binaries) throws IOException {
		List<Syntactic.Marker> annotated = new ArrayList<>();
		//
		for (Content b : binaries) {
			// If the object in question can be decoded as a syntactic heap then we can look
			// for syntactic messages.
			if (b instanceof Syntactic.Heap) {
				annotated.addAll(extractSyntacticMarkers((Syntactic.Heap) b));
			}
		}
		//
		return annotated;
	}

	/**
	 * Traverse the various binaries which have been generated looking for error
	 * messages.
	 *
	 * @param binaries
	 * @return
	 * @throws IOException
	 */
	public static List<Syntactic.Marker> extractSyntacticMarkers(Syntactic.Heap h) throws IOException {
		List<Syntactic.Marker> annotated = new ArrayList<>();
		// FIXME: this just reports all syntactic markers
		annotated.addAll(h.findAll(Syntactic.Marker.class));
		//
		return annotated;
	}

	private static void printLineHighlight(PrintStream output, Syntactic.Span span, TextFile.Line enclosing) {
		// Extract line text
		String text = enclosing.getText();
		// Determine start and end of span
		int start = span.getStart() - enclosing.getOffset();
		int end = Math.min(text.length() - 1, span.getEnd() - enclosing.getOffset());
		// NOTE: in the following lines I don't print characters
		// individually. The reason for this is that it messes up the
		// ANT task output.
		output.println(text);
		// First, mirror indendation
		String str = "";
		for (int i = 0; i < start; ++i) {
			if (text.charAt(i) == '\t') {
				str += "\t";
			} else {
				str += " ";
			}
		}
		// Second, place highlights
		for (int i = start; i <= end; ++i) {
			str += "^";
		}
		output.println(str);
	}


	public static WhileyFile getSourceEntry(Syntactic.Item item) {
		Syntactic.Heap heap = item.getHeap();
		//
		if(heap instanceof WyilFile) {
			WyilFile a = (WyilFile) heap;
			Trie id = a.getPath();
			return getSourceEntry(id,a.getSourceArtifacts());
		} else {
			return null;
		}
	}

	public static WhileyFile getSourceEntry(Trie id, List<WhileyFile> sources) {
		//
		for (WhileyFile s : sources) {
			if (id.equals(s.getPath())) {
				return s;
			}
		}
		return null;
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

	private static String candidateString(Decl.Callable decl, Map<Identifier, Syntactic.Item> binding) {
		String r;
		if (decl instanceof Decl.Method) {
			r = "method ";
		} else if (decl instanceof Decl.Function) {
			r = "function ";
		} else {
			r = "property ";
		}
		Type.Callable type = decl.getType();
		// FIXME: temporary hack for now.
		if(type.getReturn().shape() > 0) {
			// non-void return
			return r + decl.getQualifiedName() + bindingString(decl,binding) + brace(type.getParameter()) + "->" + brace(type.getReturn());
		} else {
			// void return
			return r + decl.getQualifiedName() + bindingString(decl,binding) + brace(type.getParameter());
		}
	}

	private static String brace(Type t) {
		if(t.shape() < 2) {
			return "(" + t + ")";
		} else {
			return t.toString();
		}
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

	private static String bindingString(Decl.Callable decl, Map<Identifier, Syntactic.Item> binding) {
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
