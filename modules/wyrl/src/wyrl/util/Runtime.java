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

package wyrl.util;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;

import wyautl.core.*;
import wyautl.io.BinaryAutomataReader;
import wyautl.io.PrettyAutomataWriter;
import wyfs.io.BinaryInputStream;
import wyrl.core.Pattern;
import wyrl.core.Type;
import wyrl.core.Types;
import wyrl.io.JavaIdentifierInputStream;

public class Runtime {

	/**
	 * A simple method to help debugging automaton rewrites.
	 *
	 * @param automaton
	 *            The automaton to be printed.
	 * @param schema
	 *            The schema for the automaton being printed.
	 */
	public static void debug(Automaton automaton, Schema schema, String... indents) {
		try {
			PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,
					schema,indents);
			writer.write(automaton);
			writer.flush();
			System.out.println();
		} catch (IOException e) {
			System.err.println("I/O error printing automaton");
		}
	}

	/**
	 * A simple method to help debugging automaton rewrites.
	 *
	 * @param root
	 *            The root node to print from.
	 * @param automaton
	 *            The automaton to be printed.
	 * @param schema
	 *            The schema for the automaton being printed.
	 */
	public static void debug(int root, Automaton automaton, Schema schema) {
		try {
			PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,
					schema);
			writer.write(root,automaton);
			writer.flush();
			System.out.println();
		} catch (IOException e) {
			System.err.println("I/O error printing automaton");
		}

	}

	/**
	 * A simple method to help debugging automaton rewrites.
	 *
	 * @param root
	 *            The root node to print from.
	 * @param automaton
	 *            The automaton to be printed.
	 * @param schema
	 *            The schema for the automaton being printed.
	 */
	public static void debug(Automaton.State root, Automaton automaton, Schema schema) {
		int index = automaton.add(root);
		debug(index,automaton,schema);
	}

	/**
	 * Construct an <code>Automaton.List</code> representing the consecutive
	 * list of numbers between <code>start</code> and <code>end</code>
	 * (exclusive).
	 *
	 * @param automaton
	 *            --- automaton into which to create this List.
	 * @param start
	 *            --- starting index
	 * @param end
	 *            --- final index
	 * @return
	 */
	public static Automaton.List rangeOf(Automaton automaton,
			Automaton.Int _start, Automaton.Int _end) {
		// FIXME: there is a bug here for big integer values.
		int start = _start.intValue();
		int end = _end.intValue();
		int[] children = new int[end - start];
		for (int i = 0; i < children.length; ++i, ++start) {
			children[i] = automaton.add(new Automaton.Int(start));
		}
		return new Automaton.List(children);
	}

	/**
	 * Construct a type from a string encoding of it. The string must be a
	 * binary encoding of the underlying automaton which is itself encoded as a
	 * Java string using the <code>JavaIdentifierOutputStream</code>.
	 *
	 * @param b
	 * @return
	 */
	public static Type Type(String str) {
		try {
			JavaIdentifierInputStream jin = new JavaIdentifierInputStream(str);
			BinaryInputStream bin = new BinaryInputStream(jin);
			BinaryAutomataReader reader = new BinaryAutomataReader(bin,
					Types.SCHEMA);
			Automaton automaton = reader.read();
			reader.close();

			return Type.construct(automaton);
		} catch (IOException e) {
			throw new RuntimeException("runtime failure constructing type", e);
		}
	}

	/**
	 * Determine whether a given automaton is <i>accepted</i> by (i.e. contained
	 * in) an given type. For example, consider this very simple type:
	 *
	 * <pre>
	 * term True
	 * term False
	 * define Bool as True | False
	 * </pre>
	 *
	 * We can then ask the question as to whether or not the type
	 * <code>Bool</code> accepts the automaton which describes <code>True</code>
	 * . This function is used during rewriting to determine whether or not a
	 * given pattern leaf matches, and also for implementing the <code>is</code>
	 * operator
	 *
	 * @param type
	 *            --- The type being to check for containment.
	 * @param automaton
	 *            --- The automaton being checked for inclusion.
	 * @param root
	 *            --- Automaton root to start from.
	 * @param schema
	 *            -- The schema for the actual automaton which is used to map
	 *            term names to their kinds.
	 * @return
	 */
	public static boolean accepts(Type type, Automaton automaton, int root,
			Schema schema) {

		// FIXME: this doesn't yet handle cyclic automata

		Automaton type_automaton = type.automaton();
		return accepts(type_automaton, type_automaton.getRoot(0), automaton,
				root, schema);
	}

	/**
	 * Determine whether a given automaton is <i>accepted</i> by (i.e. contained
	 * in) an given type. For example, consider this very simple type:
	 *
	 * <pre>
	 * term True
	 * term False
	 * define Bool as True | False
	 * </pre>
	 *
	 * We can then ask the question as to whether or not the type
	 * <code>Bool</code> accepts the automaton which describes <code>True</code>
	 * . This function is used during rewriting to determine whether or not a
	 * given pattern leaf matches, and also for implementing the <code>is</code>
	 * operator.
	 *
	 * @param type
	 *            --- The type being to check for containment.
	 * @param actual
	 *            --- The automaton being checked for inclusion.
	 * @param aState
	 *            --- The state in the actual automaton being tested for
	 *            acceptance.
	 * @param schema
	 *            -- The schema for the actual automaton which is used to map
	 *            term names to their kinds.
	 * @return
	 */
	public static boolean accepts(Type type, Automaton actual,
			Automaton.State aState, Schema schema) {

		// FIXME: this doesn't yet handle cyclic automata

		Automaton type_automaton = type.automaton();
		return accepts(type_automaton, type_automaton.getRoot(0), actual,
				aState, schema);
	}

	/**
	 * <p>
	 * Determine whether a state in the type automaton accepts a state in the
	 * actual automaton. Here, we starting from a reference into the actual
	 * automaton. Therefore, the state in the type automaton must be a reference
	 * to reflect this. Assuming it is, this function recursively dispatches to
	 * a helper function which then examines the actual kind of state involved.
	 * </p>
	 *
	 * @param type
	 *            --- The type automaton which provides a schema describing a
	 *            given type.
	 * @param tIndex
	 *            --- Index of state in the type automaton being which is being
	 *            checked to see whether it accepts the actual state.
	 * @param actual
	 *            --- The actual automaton whose states (at least some) are
	 *            testing whether or not is accepted.
	 * @param aIndex
	 *            --- The state in the actual automaton being tested for
	 *            acceptance.
	 * @param schema
	 *            -- The schema for the actual automaton which is used to map
	 *            term names to their kinds.
	 * @return
	 */
	private static boolean accepts(Automaton type, int tIndex,
			Automaton actual, int aIndex, Schema schema) {
		Automaton.Term tState = (Automaton.Term) type.get(tIndex);
		Automaton.State aState = actual.get(aIndex);
		if (tState.kind == Types.K_Ref) {
			Automaton.Term tTerm = (Automaton.Term) tState;
			return accepts(type, tTerm.contents, actual, aState, schema);
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * Determine whether a state in the type automaton accepts a state in the
	 * actual automaton. Here, we have the top-level case where an arbitrary
	 * type is being checked against. The function dispatches to helper
	 * functions based on the kind of type we have, assuming both states have
	 * the same kind. Otherwise, it returns false indicating there is no
	 * acceptance.
	 * </p>
	 *
	 * @param type
	 *            --- The type automaton which provides a schema describing a
	 *            given type.
	 * @param tIndex
	 *            --- Index of state in the type automaton being which is being
	 *            checked to see whether it accepts the actual state. This index
	 *            is known to reference the root of a type (which is always a
	 *            term).
	 * @param actual
	 *            --- The actual automaton whose states (at least some) are
	 *            testing whether or not is accepted.
	 * @param aState
	 *            --- The state in the actual automaton being tested for
	 *            acceptance.
	 * @param schema
	 *            -- The schema for the actual automaton which is used to map
	 *            term names to their kinds.
	 * @return
	 */
	private static boolean accepts(Automaton type, int tIndex,
			Automaton automaton, Automaton.State aState, Schema schema) {
		Automaton.Term tState = (Automaton.Term) type.get(tIndex);

		switch (tState.kind) {
		case Types.K_Void:
			return false;
		case Types.K_Any:
			return true;
		case Types.K_Bool:
			return aState instanceof Automaton.Bool;
		case Types.K_Int:
			return aState instanceof Automaton.Int;
		case Types.K_Real:
			return aState instanceof Automaton.Real;
		case Types.K_String:
			return aState instanceof Automaton.Strung;
		case Types.K_Term:
			if (aState instanceof Automaton.Term) {
				Automaton.Term aTerm = (Automaton.Term) aState;
				return accepts(type, tState, automaton, aTerm, schema);
			}
			return false;
		case Types.K_Nominal:
			return acceptsNominal(type, (Automaton.Term) tState, automaton,
					aState, schema);
		case Types.K_Set:
			if (aState instanceof Automaton.Set) {
				Automaton.Set aSet = (Automaton.Set) aState;
				return acceptsSetOrBag(type, tState, automaton, aSet, schema);
			}
			return false;
		case Types.K_Bag:
			if (aState instanceof Automaton.Bag) {
				Automaton.Bag aBag = (Automaton.Bag) aState;
				return acceptsSetOrBag(type, tState, automaton, aBag, schema);
			}
			return false;
		case Types.K_List:
			if (aState instanceof Automaton.List) {
				Automaton.List aList = (Automaton.List) aState;
				return acceptsList(type, tState, automaton, aList, schema);
			}
			return false;
		case Types.K_Or:
			return acceptsOr(type, tState, automaton, aState, schema);
		case Types.K_And:
			return acceptsAnd(type, tState, automaton, aState, schema);
		}

		// This should be dead-code since all possible cases are covered above.
		throw new IllegalArgumentException("unknowm type kind encountered ("
				+ tState.kind + ")");
	}

	/**
	 * <p>
	 * Determine whether a state in the type automaton accepts a state in the
	 * actual automaton. Here, both states are known to represent terms. For the
	 * type automaton, we have a template describing a term, whilst for the
	 * actual automaton we have an actual term. The question is whether or not
	 * the actual state matches the given template.
	 * </p>
	 * <p>
	 * The template for a term is given by
	 * <code>Term[string,Type...]</code>, where the string identifies the
	 * term's name and the remainder represents 0 or 1 substates.
	 * </p>
	 *
	 * @param type
	 *            --- The type automaton which provides a schema describing a
	 *            given type.
	 * @param tState
	 *            --- The state in the type automaton being which is being
	 *            checked to see whether it accepts the actual state.
	 * @param actual
	 *            --- The actual automaton whose states (at least some) are
	 *            testing whether or not is accepted.
	 * @param aTerm
	 *            --- The state in the actual automaton being tested for
	 *            acceptance. This is known to represent a term.
	 * @param schema
	 *            -- The schema for the actual automaton which is used to map
	 *            term names to their kinds.
	 * @return
	 */
	private static boolean accepts(Automaton type, Automaton.Term tState,
			Automaton actual, Automaton.Term aTerm, Schema schema) {
		Automaton.List list = (Automaton.List) type.get(tState.contents);
		String expectedName = ((Automaton.Strung) type.get(list.get(0))).value;
		String actualName = schema.get(aTerm.kind).name;
		if (!expectedName.equals(actualName)) {
			return false;
		} else if (list.size() == 1) {
			return aTerm.contents == Automaton.K_VOID;
		} else {
			return accepts(type, list.get(1), actual, aTerm.contents, schema);
		}
	}

	/**
	 * <p>
	 * Determine whether a state in the type automaton accepts a state in the
	 * actual automaton. Here, both states are known to represent sets or bags.
	 * For the type automaton, we have a template describing a set or bag type,
	 * whilst for the actual automaton we have an actual set or bag state. The
	 * question is whether or not the actual state matches the given template.
	 * </p>
	 * <p>
	 * The template for a set or bag is given by
	 * <code>Set[Type,{|Type...|}]</code>, where the first element represents
	 * the unbounded components (if void, then set is bounded) and the second
	 * element represents the expected matching subcomponents.
	 * </p>
	 *
	 * @param type
	 *            --- The type automaton which provides a schema describing a
	 *            given type.
	 * @param tState
	 *            --- The state in the type automaton being which is being
	 *            checked to see whether it accepts the actual state.
	 * @param actual
	 *            --- The actual automaton whose states (at least some) are
	 *            testing whether or not is accepted.
	 * @param aTerm
	 *            --- The state in the actual automaton being tested for
	 *            acceptance. This is known to represent a term.
	 * @param schema
	 *            -- The schema for the actual automaton which is used to map
	 *            term names to their kinds.
	 * @return
	 */
	private static boolean acceptsSetOrBag(Automaton type,
			Automaton.Term tState, Automaton automaton,
			Automaton.Collection aSetOrBag, Schema schema) {

		Automaton.List list = (Automaton.List) type.get(tState.contents);
		Automaton.Collection collection = (Automaton.Collection) type.get(list
				.get(1));
		int unboundedIndex = list.get(0);
		Automaton.Term unbounded = (Automaton.Term) type.get(unboundedIndex);
		boolean isUnbounded = unbounded.kind != Types.K_Void;

		// The minimum expected size of the collection. In the case of a bounded
		// collection, this is exactly the size of the collection. For an
		// unbounded collection, it is one less since the last element
		// represents zero or more elements.
		int minSize = collection.size();

		if (aSetOrBag.size() < minSize
				|| (!isUnbounded && minSize != aSetOrBag.size())) {
			// collection is not big enough.
			return false;
		}

		// Now, attempt to match all the requested items. Each match is noted
		// in matched in order to prevent double matching of the same item.
		BitSet matched = new BitSet();

		// FIXME: is there a bug here because of the ordering I go through the
		// loop means we don't try all combinations?

		for (int i = 0; i != minSize; ++i) {
			int typeItem = collection.get(i);
			boolean found = false;
			for (int j = 0; j != aSetOrBag.size(); ++j) {
				if (matched.get(j)) {
					continue;
				}
				int aItem = aSetOrBag.get(j);
				if (accepts(type, typeItem, automaton, aItem, schema)) {
					matched.set(i, true);
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}

		// Finally, for an unbounded match we need to match all other items
		// against the remainder.
		if (isUnbounded) {
			for (int j = 0; j != aSetOrBag.size(); ++j) {
				if (matched.get(j)) {
					continue;
				}
				int aItem = aSetOrBag.get(j);
				if (!accepts(type, unboundedIndex, automaton, aItem, schema)) {
					return false;
				}
			}
		}

		// If we get here, we're done.

		return true;
	}

	/**
	 * <p>
	 * Determine whether a state in the type automaton accepts a state in the
	 * actual automaton. Here, both states are known to represent lists. For the
	 * type automaton, we have a template describing a list type, whilst for the
	 * actual automaton we have an actual list state. The question is whether or
	 * not the actual state matches the given template.
	 * </p>
	 * <p>
	 * The template for list is given by <code>List[Type,[Type...]]</code>,
	 * where the first element represents the unbounded components (if void,
	 * then list is bounded) and the second element represents the expected
	 * matching subcomponents.
	 * </p>
	 *
	 * @param type
	 *            --- The type automaton which provides a schema describing a
	 *            given type.
	 * @param tState
	 *            --- The state in the type automaton being which is being
	 *            checked to see whether it accepts the actual state.
	 * @param actual
	 *            --- The actual automaton whose states (at least some) are
	 *            testing whether or not is accepted.
	 * @param aTerm
	 *            --- The state in the actual automaton being tested for
	 *            acceptance. This is known to represent a term.
	 * @param schema
	 *            -- The schema for the actual automaton which is used to map
	 *            term names to their kinds.
	 * @return
	 */
	private static boolean acceptsList(Automaton type, Automaton.Term tState,
			Automaton automaton, Automaton.List aList, Schema schema) {
		Automaton.List list = (Automaton.List) type.get(tState.contents);
		Automaton.Collection collection = (Automaton.Collection) type.get(list
				.get(1));
		int unboundedIndex = list.get(0);
		Automaton.Term unbounded = (Automaton.Term) type.get(unboundedIndex);
		boolean isUnbounded = unbounded.kind != Types.K_Void;

		// The minimum expected size of the collection. In the case of a bounded
		// collection, this is exactly the size of the collection. For an
		// unbounded collection, it is one less since the last element
		// represents zero or more elements.
		int minSize = collection.size();

		if (aList.size() < minSize || (!isUnbounded && minSize != aList.size())) {
			// collection is not big enough.
			return false;
		}

		// Now, attempt to match all the requested items. Each match is loaded
		// into matches in order to prevent double matching of the same item.
		for (int i = 0; i != minSize; ++i) {
			int tItem = collection.get(i);
			int aItem = aList.get(i);
			if (!accepts(type, tItem, automaton, aItem, schema)) {
				return false;
			}
		}

		// Finally, for an unbounded match we need to match all other items
		// against the remainder.
		if (isUnbounded) {
			for (int j = minSize; j != aList.size(); ++j) {
				int aItem = aList.get(j);
				if (!accepts(type, unboundedIndex, automaton, aItem, schema)) {
					return false;
				}
			}

		}

		// If we get here, we're done.

		return true;
	}

	/**
	 * <p>
	 * Determine whether a state in the type automaton accepts a state in the
	 * actual automaton. Here, the type state is known to represent a template
	 * describing a nominal type. In this case, acceptance is simply determined
	 * by acceptance of the nominal child on the same state.
	 * </p>
	 * <p>
	 * The template for list is given by <code>Nominal[string,Type]</code>,
	 * where the first element represents the nominal name and the second
	 * element gives its body.
	 * </p>
	 *
	 * @param type
	 *            --- The type automaton which provides a schema describing a
	 *            given type.
	 * @param tState
	 *            --- The state in the type automaton being which is being
	 *            checked to see whether it accepts the actual state.
	 * @param actual
	 *            --- The actual automaton whose states (at least some) are
	 *            testing whether or not is accepted.
	 * @param aTerm
	 *            --- The state in the actual automaton being tested for
	 *            acceptance. This is known to represent a term.
	 * @param schema
	 *            -- The schema for the actual automaton which is used to map
	 *            term names to their kinds.
	 * @return
	 */
	private static boolean acceptsNominal(Automaton type,
			Automaton.Term tState, Automaton automaton, Automaton.State aState,
			Schema schema) {
		Automaton.List l = (Automaton.List) type.get(tState.contents);
		return accepts(type, l.get(1), automaton, aState, schema);
	}

	private static boolean acceptsAnd(Automaton type, Automaton.Term tState,
			Automaton automaton, Automaton.State aState, Schema schema) {
		Automaton.Set set = (Automaton.Set) type.get(tState.contents);
		for (int i = 0; i != set.size(); ++i) {
			int element = set.get(i);
			if (!accepts(type, element, automaton, aState, schema)) {
				return false;
			}
		}
		return true;
	}

	private static boolean acceptsOr(Automaton type, Automaton.Term tState,
			Automaton automaton, Automaton.State aState, Schema schema) {

		Automaton.Set set = (Automaton.Set) type.get(tState.contents);
		for (int i = 0; i != set.size(); ++i) {
			int element = set.get(i);
			if (accepts(type, element, automaton, aState, schema)) {
				return true;
			}
		}
		return false;
	}
}
