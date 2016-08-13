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

package wyil.util.type;

import static wyil.lang.Type.K_ANY;
import static wyil.lang.Type.K_UNION;
import static wyil.lang.Type.K_VOID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;

import wyautl_old.lang.*;
import wybs.lang.NameID;
import wyil.lang.Type;

/**
 * Contains various important algorithms for manipulating types. These were
 * split out from wyil.lang.Type, as that class is already quite large. The most
 * important algorithms contained in this class are:
 * <ul>
 * <li>
 * <p>
 * <b>Type Intersection</b>. Here, the intersection of two types is calculated.
 * This is necessary for determining the updated type after a runtime type test.
 * Essentially, we intersect the original type with the tested type to produce
 * something new.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Type Simplification</b>. Here, a number of simplification rules are
 * provided which reduce non-sensical forms into something sensible. For
 * example, <code>int|int</code> is a non-sensical form, and should be
 * simplified to just <code>int</code>.
 * </p>
 * </li>
 * </ul>
 * <b>NOTE:</b> the algorithms contained in this file are ugly and difficult to
 * follow. Do not touch them unless you absolutely know what you're doing!
 *
 * @author David J. Pearce
 *
 */
public final class TypeAlgorithms {

	/**
	 * The data comparator is used in the type canonicalisation process. It is
	 * used to compare the supplementary data of states in automata
	 * representing types. Supplementary data is used for record kinds and
	 */
	public static final Comparator<Automaton.State> DATA_COMPARATOR = new Comparator<Automaton.State>() {
		public int compare(Automaton.State s1, Automaton.State s2) {
			// PRE-CONDITION s1.kind == s2.kind
			if(s1.kind == Type.K_RECORD) {
				Type.Record.State fields1 = (Type.Record.State) s1.data;
				Type.Record.State fields2 = (Type.Record.State) s2.data;
				int fields1_size = fields1.size();
				int fields2_size = fields2.size();
				if (fields1_size < fields2_size
						|| (!fields1.isOpen && fields2.isOpen)) {
					return -1;
				} else if (fields1_size > fields2_size
						|| (fields1.isOpen && !fields2.isOpen)) {
					return 1;
				}
				// ASSERT: fields1_size == fields2_size
				for(int i=0;i!=fields1_size;++i) {
					String str1 = fields1.get(i);
					String str2 = fields2.get(i);
					int c = str1.compareTo(str2);
					if(c != 0) {
						return c;
					}
				}
				return 0;
			} else if(s1.kind == Type.K_NOMINAL){
				NameID nid1 = (NameID) s1.data;
				NameID nid2 = (NameID) s2.data;
				return nid1.toString().compareTo(nid2.toString());
			} else if(s1.kind == Type.K_LIST || s1.kind == Type.K_SET){
				Boolean nid1 = (Boolean) s1.data;
				Boolean nid2 = (Boolean) s2.data;
				return nid1.toString().compareTo(nid2.toString());
			} else if(s1.kind == Type.K_FUNCTION || s1.kind == Type.K_METHOD) {
				Type.FunctionOrMethod.Data s1Data = (Type.FunctionOrMethod.Data) s1.data;
				Type.FunctionOrMethod.Data s2Data = (Type.FunctionOrMethod.Data) s2.data;
				return s1Data.compareTo(s2Data);
			} else {
				String str1 = (String) s1.data;
				String str2 = (String) s2.data;
				return str1.compareTo(str2);
			}
		}
	};

	/**
	 * <p>
	 * Contractive types are types which cannot accept value because they have
	 * an <i>unterminated cycle</i>. An unterminated cycle has no leaf nodes
	 * terminating it. For example, <code>X<{X field}></code> is contractive,
	 * where as <code>X<{null|X field}></code> is not.
	 * </p>
	 *
	 * <p>
	 * This method returns true if the type is contractive, or contains a
	 * contractive subcomponent. For example, <code>null|X<{X field}></code> is
	 * considered contracted.
	 * </p>
	 *
	 * @param type --- type to test for contractivity.
	 * @return
	 */
	public static boolean isContractive(Automaton automaton) {
		BitSet contractives = new BitSet(automaton.size());
		// TODO: optimise away the need to initialise the contractives
		contractives.set(0,automaton.size(),true);
		// initially all nodes are considered contracive.
		return findContractives(automaton,contractives);
	}

	private static boolean findContractives(Automaton automaton, BitSet contractives) {
		boolean changed = true;
		boolean contractive = false;
		while(changed) {
			changed=false;
			contractive = false;
			for(int i=0;i!=automaton.size();++i) {
				boolean oldVal = contractives.get(i);
				boolean newVal = isContractive(i,contractives,automaton);
				if(oldVal && !newVal) {
					contractives.set(i,newVal);
					changed = true;
				}
				contractive |= newVal;
			}
		}
		return contractive;
	}

	private static boolean isContractive(int index, BitSet contractives,
			Automaton automaton) {
		Automaton.State state = automaton.states[index];
		int[] children = state.children;
		if(children.length == 0) {
			return false;
		}
		switch(state.kind) {
		case Type.K_SET:
		case Type.K_MAP:
		case Type.K_LIST:
		case Type.K_FUNCTION:
		case Type.K_METHOD:
			return false;
		}

		if(state.deterministic) {
			for(int child : children) {
				if(child == index || contractives.get(child)) {
					return true;
				}
			}
			return false;
		} else {
			boolean r = true;
			for(int child : children) {
				if(child == index) {
					return true;
				}
				r &= contractives.get(child);
			}
			return r;
		}
	}

	/**
	 * <p>
	 * This simplification rule removes spurious components by repeated
	 * application of various rewrite rules. The following provides
	 * representatives of the main rules considered.
	 * </p>
	 * <ul>
	 * <li><code>{void f}</code> => <code>void</code>.</li>
	 * <li><code>T | void</code> => <code>T</code>.</li>
	 * <li><code>T | any</code> => <code>any</code>.</li>
	 * <li><code>X<T | X></code> => <code>T</code>.</li>
	 * <li><code>!!T</code> => <code>T</code>.</li>
	 * <li><code>!any</code> => <code>void</code>.</li>
	 * <li><code>!void</code> => <code>any</code>.</li>
	 * <li><code>!(T_1 | T_2)</code> => <code>!T_1 & !T_2</code>.</li>
	 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
	 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> =>
	 * <code>T_1</code>.
	 * <code>void</code>.
	 * </ul>
	 * <p>
	 * <b>NOTE:</b> applications of this rewrite rule may leave states which are
	 * unreachable from the root. Therefore, the resulting automaton should be
	 * extracted after rewriting to eliminate such states.
	 * </p>
	 *
	 */
	/**
	 * Type inhabitation labels used by the simplification algorithm.
	 */
	private static enum Inhabitation {
		/**
		 * Indicates that the simplification algorithm has not yet labeled a state
		 * with a value.
		 */
		UNLABELED,
		/**
		 * Type is uninhabited. Equivalent to void.
		 */
		NONE,
		/**
		 * May have some values inhabiting the type. Since we don't precisely
		 * track inhabitation, a type that has this label does not necessarily
		 * have any values, but we may be unable to prove it is uninhabited so we
		 * may choose to assume that it is inhabited to be safe.
		 */
		SOME,
		/**
		 * Type is inhabited by all values. Equivalent to any.
		 */
		ALL
	}

	/**
	 * Get the inhabitation of a state during simplification. Some states have a
	 * fixed, known inhabitation. Others are either UNLABELED or SOME, depending
	 * on the value of the corresponding bit in the set of flags.
	 *
	 * @param index
	 *            --- index of state being worked on.
	 * @param automaton
	 *            --- automaton containing state being worked on.
	 * @param inhabitationFlags
	 *            --- flags tracking inhabitation for some of the states
	 * @return The inhabitation of the state.
	 */
	private static Inhabitation getStateInhabitation(int index, Automaton automaton, BitSet inhabitationFlags) {
		Automaton.State state = automaton.states[index];
		switch (state.kind) {
		// Some states have known, fixed inhabitation.
		case Type.K_VOID:
			return Inhabitation.NONE;
		case Type.K_ANY:
			return Inhabitation.ALL;
		case Type.K_NULL:
		case Type.K_BOOL:
		case Type.K_BYTE:
		case Type.K_CHAR:
		case Type.K_INT:
		case Type.K_RATIONAL:
		case Type.K_STRING:
		case Type.K_FUNCTION:
		case Type.K_NOMINAL:
		case Type.K_META:
			return Inhabitation.SOME;
		// Other states have their inhabitation labels stored as a flag.
		case Type.K_NEGATION:
		case Type.K_UNION :
		case Type.K_LIST:
		case Type.K_REFERENCE:
		case Type.K_SET:
		case Type.K_RECORD:
		case Type.K_TUPLE:
		case Type.K_METHOD:
			if (inhabitationFlags.get(index)) {
				return Inhabitation.SOME;
			} else {
				return Inhabitation.UNLABELED;
			}
		default:
			throw new IllegalArgumentException("Unknown kind: " + state.kind);
		}
	}

	/**
	 * Set the inhabitation of a state during simplification. This method might
	 * do several things:
	 *
	 * 1. If the inhabitation is set to its existing value, then nothing happens
	 *    and false is returned.
	 * 2. If the inhabitation is set to NONE or ALL then the state's kind will be
	 *    changed to K_VOID or K_ANY and true is returned.
	 * 3. If the inhabitation is set to SOME or UNLABELED, and the state's kind
	 *    supports it, then a flag will be set or clared in the inhabitationFlags
	 *    parameter. True will be returned.
	 * 4. If none of these actions are possible then a state is being set to
	 *    an unsupported inhabitation label (e.g. K_INT set to K_UNLABELED) and
	 *    an IllegalArgumentExceptionw will be thrown.
	 *
	 * @param index
	 *            --- index of state being worked on.
	 * @param automaton
	 *            --- automaton containing state being worked on.
	 * @param inhabitationFlags
	 *            --- flags tracking inhabitation for some of the states
	 * @param newValue
	 *            --- the new inhabitation value to set the state to
	 * @return Whether or not the value was changed.
	 */
	private static boolean setStateInhabitation(int index, Automaton automaton, BitSet inhabitationFlags, Inhabitation newValue) {
		Inhabitation existingValue = getStateInhabitation(index, automaton, inhabitationFlags);
		if (newValue == existingValue) {
			return false;
		}
		if (newValue == Inhabitation.NONE) {
			automaton.states[index] = new Automaton.State(Type.K_VOID);
		} else if (newValue == Inhabitation.ALL) {
			automaton.states[index] = new Automaton.State(Type.K_ANY);
		} else if (newValue == Inhabitation.SOME || newValue == Inhabitation.UNLABELED) {
			int existingKind = automaton.states[index].kind;
			switch (existingKind) {
			case Type.K_NEGATION:
			case Type.K_UNION :
			case Type.K_LIST:
			case Type.K_REFERENCE:
			case Type.K_SET:
			case Type.K_RECORD:
			case Type.K_TUPLE:
			case Type.K_FUNCTION:
			case Type.K_METHOD:
				inhabitationFlags.set(index, newValue == Inhabitation.SOME);
				break;
			default:
				throw new IllegalArgumentException("Cannot label state with kind " + existingKind + " with inhabitation label " + newValue);
			}
		} else {
			throw new IllegalArgumentException("Unknown inhabitation label " + newValue);
		}
		return true;
	}

	/**
	 * Analyse an automaton and try to produce a version that is simpler but
	 * still equivalent.
	 *
	 * @param automaton
	 *            --- automaton to simplify.
	 */
	public static void simplify(Automaton automaton) {
		// Use a BitSet to track the inhabitation label of some of the states in
		// the automaton. A bit is available for each state but it is ignored for
		// states whose kind has an implicit inhabitation label, e.g. K_VOID has
		// a label of NONE, K_ANY has a label of ALL, K_INT has a label of SOME,
		// etc, but K_RECORD needs a bit to track whether it is UNLABELED or has
		// a label of SOME.
		//
		// A clear bit indicates a state is UNLABELED, a set bit indicates it
		// has SOME inhabitants. This bit has no meaning for states that already
		// have implicit inhabitation information.
		BitSet lastInhabitationFlags = null;
		BitSet inhabitationFlags = null;

		while (true) {
			// Swap inhabitation flags.
			{
				BitSet temp = inhabitationFlags;
				lastInhabitationFlags = inhabitationFlags;
				lastInhabitationFlags = temp;
				if (inhabitationFlags == null) {
					inhabitationFlags = new BitSet(automaton.size());
				} else {
					inhabitationFlags.clear();
				}
			}

			// Don't assume any prior knowledge about inhabitation. Previous simplifications
			// may have assumed states had SOME inhabitants but these could be NONE after
			// further simplification, i.e. because !!SOME => SOME using our rules, but this
			// is only because we're imprecise in propagating inhabitation through negation.
			inhabitationFlags.clear();

			// Perform an initial simplification on the automaton.
			boolean simplified = simplifyInner(automaton, inhabitationFlags);
			// Break if not simplified or inhabitaitonFlags non-null and equal
			if (!simplified && lastInhabitationFlags != null && lastInhabitationFlags.equals(inhabitationFlags)) {
				// No simplification and inhabitation flags unchanged: breaking out of simplification loop
				break;
			}
			// Found simplification and/or inhabitation flags changes: continuing with simplification loop

			// Now that simplification has occurred once, all possibly inhabited states should
			// have a label. Anything unlabeled can be considered uninhabited.
			boolean labelingChanged = markUnlabeledAsUninhabited(automaton, inhabitationFlags);
			if (!labelingChanged && lastInhabitationFlags != null) {
				// No uninhabited labeling changes: breaking out of simplification loop
				break;
			}
			// Found labeling changes: continuing with simplification loop
		}
	}

	/**
	 * Simplifies each state in an automaton until a fixpoint is reached.
	 *
	 * @param automaton
	 *            --- automaton being simplified.
	 * @param inhabitationFlags
	 *            --- flags tracking inhabitation for some of the states
	 */
	private static boolean simplifyInner(Automaton automaton, BitSet inhabitationFlags) {
		// Flag indicating whether any simplifications have been performed by this
		// method.
		boolean anySimplificationPerformed = false;
		// Flag indicating whether or not to loop again. This will be false when
		// no simplifications are made to any state.
		boolean loopAgain = true;
		while (loopAgain) {
			loopAgain = false;
			// Try to simplify each state and track whether or not any simplifications
			// have occurred.
			for(int i=0;i!=automaton.size();++i) {
				boolean stateSimplified = simplifyState(i,automaton, inhabitationFlags);
				loopAgain |= stateSimplified;
				anySimplificationPerformed |= stateSimplified;
			}
		}
		return anySimplificationPerformed;
	}

	/**
	 * This method is called after an initial simplification pass. Any states
	 * that have an UNLABELED inhabitation must not be inhabited, so can be set
	 * to NONE.
	 *
	 * @param automaton
	 *            --- automaton being simplified.
	 * @param inhabitationFlags
	 *            --- flags tracking inhabitation for some of the states
	 * @return True if the labels were changed by this method, false otherwise.
	 */
	private static boolean markUnlabeledAsUninhabited(Automaton automaton, BitSet inhabitationFlags) {
		boolean labelingChanged = false;
		for(int i=0;i!=automaton.size();++i) {
			if (getStateInhabitation(i, automaton, inhabitationFlags) == Inhabitation.UNLABELED) {
				// Mark unlabeled state as uninhabited
				setStateInhabitation(i, automaton, inhabitationFlags, Inhabitation.NONE);
				labelingChanged = true;
			}
		}
		return labelingChanged;
	}

	// FIXME: This method is not called from anywhere.
	private static boolean simplifyContractives(Automaton automaton) {
		BitSet contractives = new BitSet(automaton.size());
		// initially all nodes are considered contractive.
		// TODO: optimise away the need to initialise the contractives
		contractives.set(0,automaton.size(),true);
		boolean changed = findContractives(automaton, contractives);

		for (int i = contractives.nextSetBit(0); i >= 0; i = contractives
				.nextSetBit(i + 1)) {
			automaton.states[i] = new Automaton.State(Type.K_VOID);
		}

		return changed;
	}

	/**
	 * Attempts to simplify a single state in automaton.
	 *
	 * @param index
	 *            --- the index of the state being worked on.
	 * @param automaton
	 *            --- automaton being simplified.
	 * @param inhabitationFlags
	 *            --- flags tracking inhabitation for some of the states
	 * @return True if this method modified the state, false if the state is
	 *         still the same.
	 */
	private static boolean simplifyState(int index, Automaton automaton, BitSet inhabitationFlags) {
		Automaton.State state = automaton.states[index];
		switch (state.kind) {
		case Type.K_VOID:
		case Type.K_ANY:
		case Type.K_NULL:
		case Type.K_BOOL:
		case Type.K_BYTE:
		case Type.K_CHAR:
		case Type.K_INT:
		case Type.K_RATIONAL:
		case Type.K_STRING:
		case Type.K_NOMINAL:
		case Type.K_FUNCTION:
		case Type.K_META:
			return false;
		case Type.K_NEGATION:
			return simplifyNegation(index, state, automaton, inhabitationFlags);
		case Type.K_UNION :
			return simplifyUnion(index, state, automaton, inhabitationFlags);
		case Type.K_REFERENCE:
			return simplifyReference(index, state, automaton, inhabitationFlags);
		case Type.K_LIST:
		case Type.K_SET:
			// for list and set types, we want to simplify the following cases:
			// [void+] => void
			// {void+} => void
			boolean nonEmpty = (Boolean) state.data;
			if(!nonEmpty) {
				// type of form [T], so no further simplication needed
				// All non-empty lists and sets contain the empty list or set
				return setStateInhabitation(index, automaton, inhabitationFlags, Inhabitation.SOME);
			}
			// list/set type of form [T+] so fall through to simplify like other compounds.
		case Type.K_RECORD:
		case Type.K_TUPLE:
		case Type.K_METHOD:
			return simplifyCompound(index, state, automaton, inhabitationFlags);
		default:
			throw new IllegalArgumentException("Can't simplify state with kind: " + state.kind);
		}
	}

	private static boolean simplifyNegation(int index, Automaton.State state, Automaton automaton, BitSet inhabitationFlags) {
		// Rewrite !!X => X
		Automaton.State child = automaton.states[state.children[0]];
		if(child.kind == Type.K_NEGATION) {
			// bypass node
			int childchildIndex = child.children[0];
			Inhabitation childchildInhabitation = getStateInhabitation(childchildIndex, automaton, inhabitationFlags);
			Automaton.State childchild = automaton.states[childchildIndex];
			// Replace double negation of X with just X
			automaton.states[index] = new Automaton.State(childchild);
			setStateInhabitation(index, automaton, inhabitationFlags, childchildInhabitation);
			return true;
		}

		// Set inhabitation label based on child's label
		Inhabitation childInhabitation = getStateInhabitation(state.children[0], automaton, inhabitationFlags);
		Inhabitation inhabitation;
		if (childInhabitation == Inhabitation.ALL) {
			inhabitation = Inhabitation.NONE; // Negation of ALL is NONE
		} else if (childInhabitation == Inhabitation.NONE) {
			inhabitation = Inhabitation.ALL; // Negation of ALL is SOME
		} else {
			// Approximate negation of SOME is SOME, leave UNLABELED if child is UNLABELED
			inhabitation = childInhabitation;
		}
		return setStateInhabitation(index, automaton, inhabitationFlags, inhabitation);
	}

	private static boolean simplifyReference(int index, Automaton.State state, Automaton automaton, BitSet inhabitationFlags) {
		// Look at child inhabitation to work out reference inhabitation
		Inhabitation childInhabitation = getStateInhabitation(state.children[0], automaton, inhabitationFlags);
		if (childInhabitation == Inhabitation.ALL || childInhabitation == Inhabitation.SOME) {
			// Use SOME for the reference even if the child is ALL. This is necessary to prevent
			// the constant Reference.T_REF_ANY from be normalised directly into any, losing its
			// reference-ness. We could possibly use ALL if we added a way to construct a reference
			// without normalising it completely.
			return setStateInhabitation(index, automaton, inhabitationFlags, Inhabitation.SOME);
		} else if (childInhabitation == Inhabitation.NONE) {
			return setStateInhabitation(index, automaton, inhabitationFlags, Inhabitation.NONE);
		} else {
			return false;
		}
	}

	private static boolean simplifyCompound(int index, Automaton.State state, Automaton automaton, BitSet inhabitationFlags) {
		int kind = state.kind;
		int[] children = state.children;

		// Skip some children if the compound is a function
		int numChildrenToCheck = children.length;
		if (state.kind == Type.K_FUNCTION) {
		  // Only check function parameters for now
		  // TODO: Work out how to handle function return types properly
			numChildrenToCheck = (Integer) state.data;
		}

		// Scan compound's children
		boolean allChildrenInhabited = true;
		for(int i=0;i<numChildrenToCheck;++i) {			
			Automaton.State child = automaton.states[children[i]];
			Inhabitation childInhabitation = getStateInhabitation(children[i], automaton, inhabitationFlags);
			if (childInhabitation == Inhabitation.NONE) {
				return setStateInhabitation(index, automaton, inhabitationFlags, Inhabitation.NONE);
			} else if (childInhabitation == Inhabitation.UNLABELED) {
				// If a child is labeled then we do not know if all the children are inhabited
				allChildrenInhabited = false;
			} // else ALL or SOME: leave allChildrenInhabited as it is
		}

		// Consider compound inhabited if all children are inhabited
		if (allChildrenInhabited) {
			return setStateInhabitation(index, automaton, inhabitationFlags, Inhabitation.SOME);
		}

		return false;
	}

	/**
	 * This method is responsible for the following rewrite rules:
	 * <ul>
	 * <li><code>T | void</code> => <code>T</code>.</li>
	 * <li><code>T | any</code> => <code>any</code>.</li>
	 * <li><code>X<T | X></code> => <code>T</code>.</li>
	 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
	 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> => <code>T_1</code>.
	 * </ul>
	 *
	 * @param index
	 *            --- index of state being worked on.
	 * @param state
	 *            --- state being worked on.
	 * @param automaton
	 *            --- automaton containing state being worked on.
	 * @return
	 */
	private static boolean simplifyUnion(int index, Automaton.State state,
			Automaton automaton, BitSet inhabitationFlags) {
		return simplifyUnion_1(index, state, automaton, inhabitationFlags)
				|| simplifyUnion_2(index, state, automaton);
	}

	/**
	 * This method applies the following rewrite rules:
	 * <ul>
	 * <li><code>T | void</code> => <code>T</code>.</li>
	 * <li><code>T | any</code> => <code>any</code>.</li>
	 * <li><code>X<T | X></code> => <code>T</code>.</li>
	 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
	 * </ul>
	 *
	 * @param index
	 *            --- index of state being worked on.
	 * @param state
	 *            --- state being worked on.
	 * @param automaton
	 *            --- automaton containing state being worked on.
	 * @return
	 */
	private static boolean simplifyUnion_1(int index, Automaton.State state,
			Automaton automaton, BitSet inhabitationFlags) {
		int[] children = state.children;

		boolean changed = false;

		// Scan over all children, flattening nested unions, removing uninhabited
		// children, checking if all children are inhabited, etc.
		boolean anyChildrenInhabited = false;
		for (int i = 0; i < children.length; ++i) {
			int iChild = children[i];
			if (iChild == index) {
				// Contractive case: union contains itself. We can remove this child.
				state.children = removeIndex(i, children);
				changed = true;
			} else {
				Automaton.State child = automaton.states[iChild];
				// If a child of a union is a union, flatten them together.
				if (child.kind == Type.K_UNION) {
					// TODO: Optimise by inserting in-place and continuing loop?
					flattenChildren(index, state, automaton);
					return true;
				}
				// Now check inhabitation of children
				switch (getStateInhabitation(iChild, automaton, inhabitationFlags)) {
					case ALL:
						// If a child is ALL then we can replace the union with ALL
						return setStateInhabitation(index, automaton, inhabitationFlags, Inhabitation.ALL);
					case NONE:
						// If a child is NONE then it can be removed from the union
						children = removeIndex(i, children);
						state.children = children;
						changed = true;
						continue;
					case SOME:
						// If a child is SOME then that means the union can be SOME
						anyChildrenInhabited = true;
					// Unlabeled children are ignored
				}
			}
		}

		// Perform more simplifications now that all children have been examined
		if (children.length == 0) {
			// Union with no children is void. This can happen in the case of a
			// union which has only itself as a child.
			automaton.states[index] = new Automaton.State(Type.K_VOID);
			changed = true;
		} else if (children.length == 1) {
			// Union with only 1 child is equivalent to that child: bypass the union
			// altogether.
			int child = children[0];
			automaton.states[index] = new Automaton.State(automaton.states[child]);
			setStateInhabitation(index, automaton, inhabitationFlags, getStateInhabitation(child, automaton, inhabitationFlags));
			changed = true;
		} else if (anyChildrenInhabited) {
			// Union with at least one SOME child is SOME itself.
			changed |= setStateInhabitation(index, automaton, inhabitationFlags, Inhabitation.SOME);
		}

		return changed;
	}

	/**
	 * This method applies the following rewrite rules:
	 * <ul>
	 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> => <code>T_1</code>.
	 * </ul>
	 *
	 * @param index
	 *            --- index of state being worked on.
	 * @param state
	 *            --- state being worked on.
	 * @param automaton
	 *            --- automaton containing state being worked on.
	 * @return
	 */
	private static boolean simplifyUnion_2(int index, Automaton.State state,
			Automaton automaton) {
		boolean changed = false;
		int[] children = state.children;

		for (int i = 0; i < children.length; ++i) {
			int iChild = children[i];
			// check whether this child is subsumed
			boolean subsumed = false;
			for (int j = 0; j < children.length; ++j) {
				int jChild = children[j];
				if (i != j && isSubtype(jChild, iChild, automaton)
						&& (!isSubtype(iChild, jChild, automaton) || i > j)) {
					// Found a child that's a subtype of another child; it can be
					// subsumed into that other child.
					subsumed = true;
				}
			}
			if (subsumed) {
				// Remove the subsumed child.
				children = removeIndex(i--, children);
				state.children = children;
				changed = true;
			}
		}

		if (children.length == 1) {
			// bypass this node altogether
			int child = children[0];
			automaton.states[index] = new Automaton.State(automaton.states[child]);
			changed = true;
		}

		return changed;
	}

	private static boolean isSubtype(int fromIndex, int toIndex,
			Automaton automaton) {
		SubtypeOperator op = new SubtypeOperator(automaton,automaton,LifetimeRelation.EMPTY);
		return op.isSubtype(fromIndex, toIndex);
	}

	private final static class IntersectionPoint {
		public final int fromIndex;
		public final boolean fromSign;
		public final int toIndex;
		public final boolean toSign;

		public IntersectionPoint(int fromIndex, boolean fromSign, int toIndex, boolean toSign) {
			this.fromIndex = fromIndex;
			this.fromSign = fromSign;
			this.toIndex = toIndex;
			this.toSign = toSign;
		}

		public boolean equals(Object o) {
			if(o instanceof IntersectionPoint) {
				IntersectionPoint ip = (IntersectionPoint) o;
				return fromIndex == ip.fromIndex && fromSign == ip.fromSign
						&& toIndex == ip.toIndex && toSign == ip.toSign;
			}
			return false;
		}

		@Override
		public String toString() {
			return "(" + (fromSign ? '+' : '-') + fromIndex + "&" + (toSign ? '+' : '-') + fromIndex + ")";
		}

		public int hashCode() {
			return fromIndex + toIndex;
		}
	}

	/**
	 * Compute the <i>intersection</i> of two types. The resulting type will
	 * only accept values which are accepted by both types being intersected. In
	 * many cases, the only valid intersection will be <code>void</code>. For
	 * example:
	 *
	 * <pre>
	 * int & real => void
	 * int & [int] => void
	 * !int & int|[int] => [int]
	 * int|[any] & any|[int] => int|[int]
	 * </pre>
	 *
	 * Type intersection can be thought of as <i>set intersection</i>. In this
	 * way, we're effectively taking the sets of values represented by the two
	 * types and intersecting them. In particular, the resulting type should
	 * correspond exactly to that intersection.
	 *
	 * @param t1
	 *            --- first type to be intersected
	 * @param t2
	 *            --- second type to be intersected
	 * @return
	 */
	public static Type intersect(Type t1, Type t2) {
		Automaton a1 = Type.destruct(t1);
		Automaton a2 = Type.destruct(t2);
		return Type.construct(intersect(true,a1,true,a2));
	}

	private static Automaton intersect(boolean fromSign, Automaton from, boolean toSign, Automaton to) {
		HashMap<IntersectionPoint,Integer> allocations = new HashMap();
		ArrayList<Automaton.State> nstates = new ArrayList();
		intersect(0,fromSign,from,0,toSign,to,allocations,nstates);
		return new Automaton(nstates.toArray(new Automaton.State[nstates.size()]));
	}

	private static int intersect(int fromIndex, boolean fromSign,
			Automaton from, int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		// first, check whether we have determined this state already
		IntersectionPoint ip = new IntersectionPoint(fromIndex,fromSign,toIndex,toSign);
		Integer allocation = allocations.get(ip);
		if(allocation != null) { return allocation;}

		// looks like we haven't, so proceed to determine the new state.
		int myIndex = states.size();
		allocations.put(ip,myIndex);

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		// now, dispatch for the appropriate case.
		if(fromState.kind == toState.kind) {
			return intersectSameKind(fromIndex, fromSign, from, toIndex,
					toSign, to, allocations, states);
		} else {
			return intersectDifferentKind(fromIndex, fromSign, from, toIndex,
					toSign, to, allocations, states);
		}
	}

	/**
	 * The following method intersects two nodes which have different kinds. A
	 * precondition is that space has already been allocated in states for the
	 * resulting node.
	 *
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param fromSign
	 *            --- index of state in from position
	 * @param from
	 *            --- automaton in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param toSign
	 *            --- index of state in to position
	 * @param to
	 *            --- automaton in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automaton being
	 *            constructed/
	 * @return
	 */
	private static int intersectDifferentKind(int fromIndex, boolean fromSign,
			Automaton from, int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		int myIndex = states.size();
		states.add(null); // allocate space for me
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		Automaton.State myState = null;

		// using invert helps reduce the number of cases to consider.
		int fromKind = invert(fromState.kind,fromSign);
		int toKind = invert(toState.kind,toSign);

		// TODO: tidy this mess up
		if(fromKind == Type.K_VOID || toKind == Type.K_VOID) {
			myState = new Automaton.State(Type.K_VOID);
		} else if(fromKind == Type.K_UNION) {

			// (T1 | T2) & T3 => (T1&T3) | (T2&T3)
			int[] fromChildren = fromState.children;
			int[] myChildren = new int[fromChildren.length];
			for(int i=0;i!=fromChildren.length;++i) {
				int fromChild = fromChildren[i];
				myChildren[i] = intersect(fromChild,fromSign,from,toIndex,toSign,to,allocations,states);
			}
			myState = new Automaton.State(Type.K_UNION,false,myChildren);
		} else if(toKind == Type.K_UNION) {
			int[] toChildren = toState.children;
			int[] myChildren = new int[toChildren.length];
			for(int i=0;i!=toChildren.length;++i) {
				int toChild = toChildren[i];
				myChildren[i] = intersect(fromIndex, fromSign, from,
						toChild, toSign, to, allocations, states);
			}
			myState = new Automaton.State(Type.K_UNION,false,myChildren);
		} else if (fromKind == K_INTERSECTION) {
			// !(T1 | T2) & T3 => (!T1&T3) & (!T2&T3)
			// => !(!(!T1&T3)|!(!T2&T3))
			int[] fromChildren = fromState.children;
			int[] myChildren = new int[fromChildren.length];
			for (int i = 0; i != fromChildren.length; ++i) {
				int fromChild = fromChildren[i];
				int tmpChild = intersect(fromChild, fromSign, from, toIndex,
						toSign, to, allocations, states);
				myChildren[i] = states.size();
				states.add(new Automaton.State(Type.K_NEGATION, true, tmpChild));
			}
			states.add(new Automaton.State(Type.K_UNION, false, myChildren));
			myState = new Automaton.State(Type.K_NEGATION, true,
					states.size() - 1);
		} else if (toKind == K_INTERSECTION) {
			int[] toChildren = toState.children;
			int[] myChildren = new int[toChildren.length];
			for (int i = 0; i != toChildren.length; ++i) {
				int toChild = toChildren[i];
				int tmpChild = intersect(fromIndex, fromSign, from, toChild,
						toSign, to, allocations, states);
				myChildren[i] = states.size();
				states.add(new Automaton.State(Type.K_NEGATION, true, tmpChild));
			}
			states.add(new Automaton.State(Type.K_UNION, false, myChildren));
			myState = new Automaton.State(Type.K_NEGATION, true,
					states.size() - 1);
		} else if(fromKind == Type.K_NEGATION) {
			states.remove(states.size()-1);
			int fromChild = fromState.children[0];
			return intersect(fromChild,!fromSign,from,toIndex,toSign,to,allocations,states);
		} else if(toKind == Type.K_NEGATION) {
			states.remove(states.size()-1);
			int toChild = toState.children[0];
			return intersect(fromIndex,fromSign,from,toChild,!toSign,to,allocations,states);
		} else if(fromKind == Type.K_ANY) {
			states.remove(states.size()-1);
			if(!toSign) {
				states.add(new Automaton.State(Type.K_NEGATION,states.size()+1));
			}
			Automata.extractOnto(toIndex,to,states);
			return myIndex;
		} else if(toKind == Type.K_ANY) {
			states.remove(states.size()-1);
			if(!fromSign) {
				states.add(new Automaton.State(Type.K_NEGATION,states.size()+1));
			}
			Automata.extractOnto(fromIndex,from,states);
			return myIndex;
		} else if(fromSign && toSign) {
			myState = new Automaton.State(Type.K_VOID);
		} else if(fromSign) {
			states.remove(states.size()-1);
			Automata.extractOnto(fromIndex,from,states);
			return myIndex;
		} else if(toSign) {
			states.remove(states.size()-1);
			Automata.extractOnto(toIndex,to,states);
			return myIndex;
		} else {
			int childIndex = states.size();
			states.add(null);
			int nFromChild = states.size();
			Automata.extractOnto(fromIndex,from,states);
			int nToChild = states.size();
			Automata.extractOnto(toIndex,to,states);
			states.set(childIndex,new Automaton.State(Type.K_UNION, nFromChild,
					nToChild));
			myState = new Automaton.State(Type.K_NEGATION, childIndex);
		}

		states.set(myIndex, myState);

		return myIndex;
	}

	/**
	 * The following method intersects two nodes which have identical kind.
	 *
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param fromSign
	 *            --- sign of state in from position
	 * @param from
	 *            --- automaton in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param toSign
	 *            --- sign of state in to position
	 * @param to
	 *            --- automaton in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automaton being
	 *            constructed/
	 * @return
	 */
	private static int intersectSameKind(int fromIndex, boolean fromSign, Automaton from,
			int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
		Automaton.State fromState = from.states[fromIndex];

		switch(fromState.kind) {
			case Type.K_VOID:
				return intersectVoid(fromIndex,fromSign,from,toIndex,toSign,to,allocations,states);
			case Type.K_ANY:
				return intersectVoid(fromIndex,!fromSign,from,toIndex,!toSign,to,allocations,states);
			case Type.K_RECORD:
				return intersectRecords(fromIndex,fromSign,from,toIndex,toSign,to,allocations,states);
			case Type.K_NOMINAL:
				return intersectNominals(fromIndex,fromSign,from,toIndex,toSign,to,allocations,states);
			case Type.K_TUPLE:
				return intersectTuples(fromIndex,fromSign,from,toIndex,toSign,to,allocations,states);
			case Type.K_LIST:
			case Type.K_SET:
				return intersectSetsOrLists(fromIndex,fromSign,from,toIndex,toSign,to,allocations,states);
			case Type.K_REFERENCE:
				return intersectCompounds(fromIndex,fromSign,from,toIndex,toSign,to,fromState.data,allocations,states);
			case Type.K_MAP:
				return intersectCompounds(fromIndex,fromSign,from,toIndex,toSign,to,null,allocations,states);
			case Type.K_NEGATION:
				return intersectNegations(fromIndex,fromSign,from,toIndex,toSign,to,allocations,states);
			case Type.K_UNION:
				return intersectUnions(fromIndex,fromSign,from,toIndex,toSign,to,allocations,states);
			case Type.K_FUNCTION:
			case Type.K_METHOD:
				return intersectFunctionsOrMethods(fromIndex,fromSign,from,toIndex,toSign,to,allocations,states);
			default: {
				return intersectPrimitives(fromIndex,fromSign,from,toIndex,toSign,to,allocations,states);
			}
		}
	}

	// ==================================================================================
	// Primitives
	// ==================================================================================

	private static int intersectVoid(int fromIndex, boolean fromSign, Automaton from,
			int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		Automaton.State myState;

		if(!fromSign && !toSign) {
			myState = new Automaton.State(Type.K_ANY);
		} else {
			// void & void => void
			// void & !void => void
			myState = new Automaton.State(Type.K_VOID);
		}

		states.add(myState);
		return states.size()-1;
	}

	private static int intersectPrimitives(int fromIndex, boolean fromSign, Automaton from,
			int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State myState;

		if(fromSign && toSign) {
			// e.g. INT & INT => INT
			myState = new Automaton.State(fromState.kind);
		} else if(fromSign || toSign) {
			// e.g. !INT & INT => INT
			myState = new Automaton.State(Type.K_VOID);
		} else {
			// e.g. !INT & !INT => !INT
			int myIndex = states.size();
			states.add(new Automaton.State(Type.K_NEGATION,myIndex+1));
			states.add(new Automaton.State(fromState.kind));
			return myIndex;
		}

		states.add(myState);
		return states.size()-1;
	}

	// ==================================================================================
	// Negations
	// ==================================================================================

	private static int intersectNegations(int fromIndex, boolean fromSign, Automaton from,
			int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		int fromChild = fromState.children[0];
		int toChild = toState.children[0];

		return intersect(fromChild,!fromSign,from,toChild,!toSign,to,allocations,states);
	}

	// ==================================================================================
	// Nominals
	// ==================================================================================

	private static int intersectNominals(int fromIndex, boolean fromSign, Automaton from,
			int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		if(!fromState.data.equals(toState.data)) {
			int myIndex = states.size();
			if(fromSign && toSign) {
				states.add(new Automaton.State(Type.K_VOID));
				return myIndex;
			} else if(fromSign || toSign) {
				Automata.extractOnto(fromIndex,from,states);
				return myIndex;
			}
		}

		return intersectCompounds(fromIndex, fromSign, from, toIndex, toSign,
				to, fromState.data, allocations, states);
	}

	// ==================================================================================
	// Tuples
	// ==================================================================================

	private static int intersectTuples(int fromIndex, boolean fromSign,
			Automaton from, int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		if(fromState.children.length != toState.children.length) {
			if(fromSign && toSign) {
				int myIndex = states.size();
				states.add(new Automaton.State(Type.K_VOID));
				return myIndex;
			} else if(fromSign || toSign) {
				int myIndex = states.size();
				Automata.extractOnto(fromIndex,from,states);
				return myIndex;
			}
		}

		return intersectCompounds(fromIndex,fromSign,from,toIndex,toSign,to,null,allocations,states);
	}

	// ==================================================================================
	// Unions
	// ==================================================================================

	private static int intersectUnions(int fromIndex, boolean fromSign, Automaton from,
			int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states){
		if (!fromSign && !toSign) {
			return intersectUnionsNegNeg(fromIndex, from, toIndex, to,allocations,states);
		} else {
			return intersectUnionsPosNeg(fromIndex, fromSign, from, toIndex, toSign, to, allocations,states);
		}
	}

	private static int intersectUnionsPosNeg(int fromIndex, boolean fromSign,
			Automaton from, int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
		// (T1|T2) & !(T3|T4) => (T1&!(T3|T4)) | (T2&!(T3|T4))

		Automaton.State fromState = from.states[fromIndex];
		int myIndex = states.size();
		states.add(null); // reserve space for me

		int[] fromChildren = fromState.children;
		int[] newChildren = new int[fromChildren.length];
		for (int i = 0; i != fromChildren.length; ++i) {
			int fromChild = fromChildren[i];
			newChildren[i] = intersect(fromChild, fromSign, from, toIndex,
					toSign, to, allocations, states);
		}

		Automaton.State myState = new Automaton.State(Type.K_UNION, false,
				newChildren);
		states.set(myIndex,myState);
		return myIndex;
	}

	private static int intersectUnionsNegNeg(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states){
		// !(T1|T2) & !(T3|T4) => !T1 & !T2 & !T3 & !T4 => !(T1|T2|T3|T4)
		int myIndex = states.size();
		states.add(null); // reserve space for me

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[fromIndex];

		int[] fromChildren = fromState.children;
		int[] toChildren = toState.children;
		int[] newChildren = new int[fromChildren.length+toChildren.length];
		int childIndex = states.size();
		states.add(new Automaton.State(Type.K_UNION, false, newChildren));
		for (int i = 0; i != fromChildren.length; ++i) {
			int fromChild = fromChildren[i];
			newChildren[i] = states.size();
			Automata.extractOnto(fromChild,from,states);
		}
		for (int i = 0; i != toChildren.length; ++i) {
			int toChild = toChildren[i];
			newChildren[i+fromChildren.length] = states.size();
			Automata.extractOnto(toChild,to,states);
		}

		Automaton.State myState = new Automaton.State(Type.K_NEGATION, true, childIndex);
		states.set(myIndex,myState);
		return myIndex;
	}

	// ==================================================================================
	// Sets and Lists
	// ==================================================================================

	private static int intersectSetsOrLists(int fromIndex, boolean fromSign, Automaton from,
			int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {


		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		// pos-pos
		// [T1]  & [T2]  => [T1&T2]
		// [T1+] & [T2]  => [T1&T2+]
		// [T1]  & [T2+] => [T1&T2+]
		// [T1+] & [T2+] => [T1&T2+]

		// pos-neg
		// [T1] & ![T2] => [T1&!T2+]
		// [T1+] & ![T2] => [T1&!T2+]
		// [T1] & ![T2+] => [T1&!T2]
		// [T1+] & ![T2+] => [T1&!T2+]

		boolean fromNonEmpty = (Boolean) fromState.data;
		boolean toNonEmpty = (Boolean) toState.data;
		Object myData = null;

		if(fromSign && toSign) {
			myData = fromNonEmpty | toNonEmpty;
		} else if(fromSign) {
			myData = fromNonEmpty | !toNonEmpty;
		} else if(toSign) {
			myData = !fromNonEmpty | toNonEmpty;
		}

		return intersectCompounds(fromIndex,fromSign,from,toIndex,toSign,to,myData,allocations,states);
	}

	// ==================================================================================
	// Dictionaries, Processes, Records (closed), Sets and Lists
	// ==================================================================================

	private static int intersectCompounds(int fromIndex, boolean fromSign,
			Automaton from, int toIndex, boolean toSign, Automaton to,
			Object myData,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
		if (fromSign == toSign) {
			if (fromSign) {
				return intersectCompoundsPosPos(fromIndex, from, toIndex, to,
						myData,allocations, states);
			} else {
				return intersectCompoundsNegNeg(fromIndex, from, toIndex, to,
						allocations, states);
			}
		} else if (fromSign) {
			return intersectCompoundsPosNeg(fromIndex, from, toIndex, to,
					myData,allocations, states);
		} else {
			return intersectCompoundsPosNeg(toIndex, to, fromIndex, from,
					myData,allocations, states);
		}
	}

	private static int intersectCompoundsPosPos(int fromIndex, Automaton from,
			int toIndex, Automaton to,Object myData,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		int myIndex = states.size();
		states.add(null); // reserve space for me

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		int[] myChildren = contiguousZipIntersection(fromState.children, from,
				toState.children, to, allocations, states);

		Automaton.State myState = new Automaton.State(fromState.kind, myData,
				true, myChildren);

		states.set(myIndex,myState);
		return myIndex;
	}

	private static int intersectCompoundsPosNeg(int fromIndex, Automaton from,
			int toIndex, Automaton to,Object myData,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		int myIndex = states.size();
		states.add(null); // reserve space for me

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		int[] myChildren = contiguousDistributeIntersection(fromState, true, from,
				toState, false, to, myData, allocations, states);

		Automaton.State myState = new Automaton.State(Type.K_UNION, null,
				false, myChildren);

		states.set(myIndex,myState);
		return myIndex;
	}

	private static int intersectCompoundsNegNeg(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		int myIndex = states.size();
		states.add(null); // reserve space for me

		// e.g. ![int] & ![real] => !([int]|[real])
		int fromChild = states.size();
		Automata.extractOnto(fromIndex,from,states);
		int toChild = states.size();
		Automata.extractOnto(toIndex,to,states);
		states.add(new Automaton.State(Type.K_UNION,false,fromChild,toChild));
		Automaton.State myState = new Automaton.State(Type.K_NEGATION,states.size()-1);

		states.set(myIndex,myState);
		return myIndex;
	}

	// ==================================================================================
	// Functions and Methods
	// ==================================================================================
	private static int intersectFunctionsOrMethods(int fromIndex, boolean fromSign,
			Automaton from, int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		if(fromState.children.length != toState.children.length) {
			if(fromSign && toSign) {
				int myIndex = states.size();
				states.add(new Automaton.State(Type.K_VOID));
				return myIndex;
			} else if(fromSign || toSign) {
				int myIndex = states.size();
				Automata.extractOnto(fromIndex,from,states);
				return myIndex;
			}
		}

		// it's fair to say that something doesn't feel right about this?

		if(fromSign == toSign) {
			if(fromSign) {
				return intersectFunctionOrMethodsHelper(fromIndex,from,toIndex,toSign,to,allocations,states);
			} else {
				return intersectCompounds(fromIndex,fromSign,from,toIndex,toSign,to,null,allocations,states);
			}
		} else if(fromSign) {
			return intersectFunctionOrMethodsHelper(fromIndex,from,toIndex,false,to,allocations,states);
		} else {
			return intersectFunctionOrMethodsHelper(toIndex,to,fromIndex,false,from,allocations,states);
		}
	}

	private static int intersectFunctionOrMethodsHelper(int fromIndex,
			Automaton from, int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		// int(int) & any(real) => int(int|real)

		int myIndex = states.size();
		states.add(null); // reserve space for me

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		int[] fromChildren = fromState.children;
		int[] toChildren = toState.children;
		int[] myChildren = new int[fromChildren.length];

		// return value is co-variant (i.e. normal, like e.g. list elements)
		myChildren[0] = intersect(fromChildren[0],
				true, from, toChildren[0], toSign, to,
				allocations, states);

		// throws clause is co-variant (i.e. normal, like e.g. list elements)
		myChildren[1] = intersect(fromChildren[1],
				true, from, toChildren[1], toSign, to,
				allocations, states);

		if(toSign) {
			// parameter values are harder, since they are contra-variant.
			for(int i=2;i<fromChildren.length;++i) {
				int fromChild = fromChildren[i];
				int toChild = toChildren[i];
				int[] childChildren = new int[2];
				myChildren[i] = states.size();
				states.add(new Automaton.State(Type.K_UNION,null,false,childChildren));
				childChildren[0] = states.size();
				Automata.extractOnto(fromChild,from,states);
				childChildren[1] = states.size();
				Automata.extractOnto(toChild,to,states);
			}
		} else {
			// parameter values are harder, since they are contra-variant.
			for(int i=2;i<fromChildren.length;++i) {
				int fromChild = fromChildren[i];
				int toChild = toChildren[i];
				int[] childChildren = new int[2];
				myChildren[i] = states.size();
				states.add(new Automaton.State(Type.K_UNION,null,false,childChildren));
				childChildren[0] = states.size();
				Automata.extractOnto(fromChild,from,states);
				childChildren[1] = states.size();
				states.add(new Automaton.State(Type.K_NEGATION,null,false,states.size()+1));
				Automata.extractOnto(toChild,to,states);
			}
		}

		Automaton.State myState = new Automaton.State(fromState.kind,
				fromState.data, true, myChildren);

		states.set(myIndex,myState);
		return myIndex;
	}

	// ==================================================================================
	// Records
	// ==================================================================================

	private static int intersectRecords(int fromIndex, boolean fromSign, Automaton from,
			int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
		if(fromSign == toSign) {
			if(fromSign) {
				return intersectRecordsPosPos(fromIndex,from,toIndex,to,allocations,states);
			} else {
				return intersectCompoundsNegNeg(fromIndex,from,toIndex,to,allocations,states);
			}
		} else if(fromSign) {
			return intersectRecordsPosNeg(fromIndex,from,toIndex,to,allocations,states);
		} else {
			return intersectRecordsPosNeg(toIndex,to,fromIndex,from,allocations,states);
		}
	}

	private static int intersectRecordsPosPos(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		Type.Record.State fromData = (Type.Record.State) fromState.data;
		Type.Record.State toData = (Type.Record.State) toState.data;

		Type.Record.State myData;
		int myIndex = states.size();
		states.add(null); // reserve space
		int[] myChildren;

		if(fromData.isOpen == toData.isOpen) {
			if(fromData.isOpen) {
				// open open
				myData = new Type.Record.State(true);
				setUnion(fromData,toData,myData);
				myChildren = nonContiguousZipIntersection(myData,fromState,from,toState,to,allocations,states);
			} else {
				// closed closed
				if(!fromData.equals(toData)) {
					// e.g. {int f,...} & {int g}
					states.set(myIndex,new Automaton.State(Type.K_VOID));
					return myIndex;
				} else {
					myData = fromData;
					myChildren = contiguousZipIntersection(fromState.children, from,
						toState.children, to, allocations, states);
				}
			}
		} else if(fromData.isOpen) {
			// open closed
			if(!isSubset(fromData,toData)) {
				// e.g. {int f,...} & {int g}
				states.set(myIndex,new Automaton.State(Type.K_VOID));
				return myIndex;
			} else {
				myData = toData;
				myChildren = nonContiguousZipIntersection(toData,fromState,from,toState,to,allocations,states);
			}
		} else { // assert toData.isOpen
			if(!isSubset(toData,fromData)) {
				// e.g. {int f} & {int g,...}
				states.set(myIndex,new Automaton.State(Type.K_VOID));
				return myIndex;
			} else {
				myData = fromData;
				myChildren = nonContiguousZipIntersection(fromData,fromState,from,toState,to,allocations,states);
			}
		}

		Automaton.State myState = new Automaton.State(fromState.kind, myData,
				true, myChildren);

		states.set(myIndex,myState);

		return myIndex;
	}

	private static int intersectRecordsPosNeg(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		Type.Record.State fromData = (Type.Record.State) fromState.data;
		Type.Record.State toData = (Type.Record.State) toState.data;
		if (fromData.isOpen && toData.isOpen) {
			return intersectPosNegOpenOpen(fromIndex,from,toIndex,to,allocations,states);
		} else if(toData.isOpen) {
			return intersectPosNegClosedOpen(fromIndex,from,toIndex,to,allocations,states);
		} else if(fromData.isOpen) {
			return intersectNegPosClosedOpen(toIndex,to,fromIndex,from,allocations,states);
		} else {
			// fall through to general case for compounds
			if (!fromData.equals(toData)) {
				// e.g. {int f} & !{int g} => {int f}
				int myIndex = states.size();
				Automata.extractOnto(fromIndex,from,states);
				return myIndex;
			} else {
				return intersectCompoundsPosNeg(fromIndex,from,toIndex,to,fromState.data,allocations,states);
			}
		}
	}

	/**
	 * Intersect two automaton representing open records, where the first is
	 * positive and the second negative. For example:
	 *
	 * <pre>
	 * {T1 f, T2 g, ...} & !{T3 g, T4 h, ...} => {T1 f, T2&!T3 g, ...} | {T1 f, T2 g, void h, ...}
	 * </pre>
	 *
	 * This is a fairly ticky case!
	 *
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automaton in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automaton in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automaton being
	 *            constructed.
	 * @return
	 */
	private static int intersectPosNegOpenOpen(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		int myIndex = states.size();
		states.add(null); // reserve space for me

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		Type.Record.State fromData = (Type.Record.State) fromState.data;
		Type.Record.State toData = (Type.Record.State) toState.data;


//		if(isSubset(toData,fromData)) {
//			// no possible intersection so back out
//			states.set(myIndex,new Automaton.State(Type.K_VOID));
//			return myIndex; // tad ugly perhaps
//		}

		// finally, distribute over those fields present in the open record
		// (which are a subset of those in the closed record).

		int[] myChildren = nonContiguousDistributeIntersection(fromState, true,
				from, toState, false, to, fromData, allocations, states);

		Automaton.State myState = new Automaton.State(Type.K_UNION, null, false, myChildren);

		states.set(myIndex,myState);
		return myIndex;
	}

	/**
	 * Intersect two automaton representing records, where the first is positive
	 * and closed and the second negative and open. For example:
	 *
	 * <pre>
	 * {T1 f, T2 g} & !{T3 g, T4 h, ...} => {T1 f, T2 g}
	 * {T1 f, T2 g} & !{T3 f, T4 g, ...} => {T1 & !T3 f, T2 g} | {T1 f, T2 & !T4 g}
	 * {T1 f, T2 g, T3 h} & !{T4 f, T5 g, ...} => {T1 & !T4 f, T2 g, T3 h} | {T1 f, T2 & !T5 g, T3 h}
	 * </pre>
	 *
	 * In this case, the result must include exactly those fields in the closed
	 * record. If the open record contains a field not present
	 * in the closed record then it does not intersect with the closed record
	 * and we're fine. Finally, in the case they have exactly the same fields
	 * then we have to distribute.
	 *
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automaton in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automaton in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automaton being
	 *            constructed.
	 * @return
	 */
	private static int intersectPosNegClosedOpen(
			int fromIndex, Automaton from, int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		int myIndex = states.size();
		states.add(null); // reserve space for me

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		Type.Record.State fromData = (Type.Record.State) fromState.data;
		Type.Record.State toData = (Type.Record.State) toState.data;

		// check whether the open record contains a field not present in
		// the closed record. If so, then there is no intersection between them
		// and we can just return the closed record.

		if(!isSubset(toData,fromData)) {
			// no possible intersection so back out
			myIndex = states.size();
			Automata.extractOnto(toIndex,to,states);
			return myIndex; // tad ugly perhaps
		}

		// finally, distribute over those fields present in the open record
		// (which are a subset of those in the closed record).

		int[] myChildren = nonContiguousDistributeIntersection(fromState, true,
				from, toState, false, to, fromData, allocations, states);

		Automaton.State myState = new Automaton.State(Type.K_UNION, null, false, myChildren);

		states.set(myIndex,myState);
		return myIndex;
	}

	/**
	 * Intersect two automaton representing records, where the first is negative
	 * and closed and the second positive and open. For example:
	 *
	 * <pre>
	 * !{int f} & {int f,...} => {int f, ...+}
	 * {T1 f, T2 g, ...} & !{T3 g, T4 h} => {T1 f, T2 g}
	 * {T1 f, T2 g, ...} & !{T3 f, T4 g} => {T1&!T3 f, T2 g,...} | {T1 f, T2&!T4 g,...}
	 * {T1 f, T2 g, ...} & !{T3 f, T4 g, T5 h} =>  {T1&!T3 f, T2 g, ...} |
	 *                      {T1 f, T2&!T4 g, ...} | {T1 f, T2 g, void h, ...} |
	 *                      {T1 f, T2 g, !T5 h, ...}
	 * </pre>
	 *
	 * Here, <code>{T1 f, T2 g, void h, ...}</code> represents a record which
	 * doesn't have a field h. This use of void only really makes sense in the
	 * context of open records.
	 *
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automaton in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automaton in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automaton being
	 *            constructed.
	 * @return
	 */
	private static int intersectNegPosClosedOpen(
			int fromIndex, Automaton from, int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		int myIndex = states.size();
		states.add(null); // reserve space for me

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];

		Type.Record.State fromData = (Type.Record.State) fromState.data;
		Type.Record.State toData = (Type.Record.State) toState.data;

		// check whether the open record contains a field not present in
		// the closed record. If so, then there is no intersection between them
		// and we can just return the closed record.

		if(!isSubset(toData,fromData)) {
			// no possible intersection so back out
			myIndex = states.size();
			Automata.extractOnto(toIndex,to,states);
			return myIndex; // tad ugly perhaps
		}

		// finally, distribute over those fields present in the open record
		// (which are a subset of those in the closed record).
		Type.Record.State myData = new Type.Record.State(false,fromData);

		int[] myChildren = nonContiguousDistributeIntersection(fromState,
				false, from, toState, true, to, myData, allocations,
				states);

		myChildren = Arrays.copyOf(myChildren, myChildren.length+1);
		myChildren[myChildren.length-1] = states.size();
		Automata.extractOnto(toIndex,to,states);
		// FIXME: at this point, the extract type must be made larger

		Automaton.State myState = new Automaton.State(Type.K_UNION, null, false, myChildren);
		states.set(myIndex,myState);
		return myIndex;
	}

	/**
	 * Check whether one sorted lists is a subset or another.
	 *
	 * @param subset --- list to check as subset
	 * @param superset --- list to check as superset
	 * @return
	 */
	private static boolean isSubset(ArrayList<String> subset,
			ArrayList<String> superset) {
		int subsetSize = subset.size();
		int supersetSize = superset.size();

		if(subsetSize > supersetSize) {
			return false;
		}

		int fi=0,ti=0;
		while(fi < subsetSize && ti < supersetSize) {
			String fn = subset.get(fi);
			String tn = superset.get(ti);
			int c = fn.compareTo(tn);
			if(c > 0) {
				++ti;
				continue;
			} else if(c == 0){
				++ti;
				++fi;
				continue;
			}
			return false;
		}
		return fi == subsetSize;
	}

	/**
	 * Compute the union of two sorted ArrayLists. The resulting list is sorted,
	 * and does not contain any duplicate labels.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private static void setUnion(ArrayList<String> lhs, ArrayList<String> rhs,
			ArrayList<String> result) {
		int li = 0;
		int ri = 0;
		int lhsSize = lhs.size();
		int rhsSize = rhs.size();
		while(li < lhsSize && ri < rhsSize) {
			String ln = lhs.get(li);
			String rn = rhs.get(ri);
			int c = ln.compareTo(rn);
			if(c < 0) {
				result.add(ln);
				li++;
			} else if(c == 0) {
				result.add(ln);
				li++;
				ri++;
			} else {
				result.add(rn);
				ri++;
			}
		}
		while(li < lhsSize) {
			result.add(lhs.get(li++));
		}
		while(ri < rhsSize) {
			result.add(rhs.get(ri++));
		}
	}

	/**
	 * Intersect the <code>fromChildren</code> with the <code>toChildren</code>
	 * in a one-one fashion. For example,
	 * <code>[T1,T2] & [T3,T4] => [T1&T3,T2&T4]</code>.
	 *
	 * @param fromChildren
	 * @param from
	 * @param toChildren
	 * @param to
	 * @param allocations
	 * @param states
	 * @return
	 */
	private static int[] contiguousZipIntersection(int[] fromChildren,
			Automaton from, int[] toChildren, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		int fromChildrenLength = fromChildren.length;
		int[] myChildren = new int[fromChildrenLength];
		for(int i=0;i!=fromChildrenLength;++i) {
			int fromChild = fromChildren[i];
			int toChild = toChildren[i];
			myChildren[i] = intersect(fromChild, true, from,
					toChild, true, to, allocations, states);
		}
		return myChildren;
	}

	/**
	 * Similar to a zip intersection, except that the element labels are
	 * non-contiguous.  For example:
	 *
	 * <pre>
	 * [T1 f,T2 h] & [T3 g,T4 h] => [T1 f,T3 g,T2&T4 h]
	 * </pre>
	 *
	 * @param fromChildren
	 * @param from
	 * @param toChildren
	 * @param to
	 * @param allocations
	 * @param states
	 * @return
	 */
	private static int[] nonContiguousZipIntersection(
			ArrayList<String> myLabels,
			Automaton.State fromState, Automaton from, Automaton.State toState,
			Automaton to, HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		ArrayList<String> fromLabels = (ArrayList) fromState.data;
		ArrayList<String> toLabels = (ArrayList) toState.data;

		int[] fromChildren = fromState.children;
		int[] toChildren = toState.children;
		int[] myChildren = new int[myLabels.size()];
		int fi = 0, ti = 0, mi = 0;
		while (fi < fromLabels.size() && ti < toLabels.size()) {
			int fromChild = fromChildren[fi];
			int toChild = toChildren[ti];
			String fn = fromLabels.get(fi);
			String tn = toLabels.get(ti);
			int c = fn.compareTo(tn);
			if (c == 0) {
				c = intersect(fromChild, true, from, toChild,true, to, allocations, states);
				fi++;ti++;
			} else if (c < 0) {
				c = states.size();
				Automata.extractOnto(fromChild,from,states);
				fi++;
			} else {
				c = states.size();
				Automata.extractOnto(toChild,to,states);
				ti++;
			}
			myChildren[mi++] = c;
		}
		while (fi < fromLabels.size()) {
			int fromChild = fromChildren[fi++];
			myChildren[mi++] = states.size();
			Automata.extractOnto(fromChild,from,states);
		}
		while (ti < toLabels.size()) {
			int toChild = toChildren[ti++];
			myChildren[mi++] = states.size();
			Automata.extractOnto(toChild,to,states);
		}

		return myChildren;
	}

	/**
	 * Distribute the intersections of <code>fromState.Children</code> over
	 * <code>toState.Children</code>. For example,
	 * <code>[T1,T2] & [T3,T4] => [T1&T3,T2] | [T1,T2&T4]</code>.
	 *
	 * @param fromChildren
	 * @param from
	 * @param toChildren
	 * @param to
	 * @param allocations
	 * @param states
	 * @return
	 */
	private static int[] contiguousDistributeIntersection(Automaton.State fromState,
			boolean fromSign, Automaton from, Automaton.State toState,
			boolean toSign, Automaton to, Object myData,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		int[] fromChildren = fromState.children;
		int[] toChildren = toState.children;

		// First, allocate every fromChildren since each one will be used in one
		// of the generated components.
		int fromChildrenLength = fromChildren.length;
		int[] tmpChildren = extractChildren(fromChildren,from,states);

		// Second, generate the new components (one for each element in
		// fromChildren).
		int[] myChildren = new int[fromChildrenLength];
		for(int i=0;i!=fromChildrenLength;++i) {
			int[] myChildChildren = new int[fromChildren.length];
			System.arraycopy(tmpChildren, 0, myChildChildren, 0, fromChildrenLength);
			myChildChildren[i] = intersect(fromChildren[i], fromSign, from, toChildren[i],
					toSign, to, allocations, states);
			myChildren[i] = states.size();
			states.add(new Automaton.State(fromState.kind, myData, true,
					myChildChildren));
		}

		return myChildren;
	}

	/**
	 * Similar to a contiguous distribute intersection, except that the element
	 * labels are non-contiguous. For example:
	 *
	 * <pre>
	 * [T1 f, T2 g] & [T3 g] => [void f,T2 g]|[T1 f,T2&T3 g]
	 * [T1 f, T2 g] & [T3 g, T4 h] => [void f,T2 g]|[T1 f,T2&T3 g]
	 * </pre>
	 *
	 * <b>NOTE</b> current precondition is that toState.data is a subset of
	 * fromState.data
	 *
	 * @param fromChildren
	 * @param from
	 * @param toChildren
	 * @param to
	 * @param allocations
	 * @param states
	 * @return
	 */
	private static int[] nonContiguousDistributeIntersection(Automaton.State fromState,
			boolean fromSign, Automaton from, Automaton.State toState,
			boolean toSign, Automaton to, Object myData,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		ArrayList<String> fromLabels = (ArrayList) fromState.data;
		ArrayList<String> toLabels = (ArrayList) toState.data;

		int[] fromChildren = fromState.children;
		int[] toChildren = toState.children;
		int[] tmpChildren = extractChildren(fromChildren,from,states);


		// Second, generate the new components (one for each element in
		// fromChildren).
		int[] myChildren = new int[fromChildren.length];
		for(int fi=0,ti=0;fi!=fromChildren.length;++fi,++ti) {
			int[] myChildChildren = new int[fromChildren.length];
			System.arraycopy(tmpChildren, 0, myChildChildren, 0, fromChildren.length);

			if(ti < toLabels.size()) {
				String fn = fromLabels.get(fi);
				String tn = toLabels.get(ti);
				int c = fn.compareTo(tn);
				if(c == 0) {
					myChildChildren[fi] = intersect(fromChildren[fi], fromSign, from, toChildren[fi],
							toSign, to, allocations, states);
					ti++;
				} else if(c < 0) {
					myChildChildren[fi] = states.size();
					states.add(new Automaton.State(Type.K_VOID));
				} // precondition protects against other case
			} else {
				myChildChildren[fi] = states.size();
				states.add(new Automaton.State(Type.K_VOID));
			}
			myChildren[fi] = states.size();
			states.add(new Automaton.State(fromState.kind, myData, true,
					myChildChildren));
		}

		return myChildren;
	}

	/**
	 * Extract a given array of states onto the given list of states, returning
	 * an array of indices to the copied states.
	 *
	 * @param children
	 *            --- states to be extracted
	 * @param automaton
	 *            --- automata from which to extract states.
	 * @param states
	 *            --- heap onto which to place the new states.
	 * @return
	 */
	private static int[] extractChildren(int[] children, Automaton automaton,
			ArrayList<Automaton.State> states) {
		int childrenLength = children.length;
		int[] nchildren = new int[childrenLength];
		for(int i=0;i!=childrenLength;++i) {
			nchildren[i] = states.size();
			Automata.extractOnto(children[i],automaton,states);
		}
		return nchildren;
	}

	private static int invert(int kind, boolean sign) {
		if(sign) {
			return kind;
		}
		switch(kind) {
			case K_ANY:
				return K_VOID;
			case K_VOID:
				return K_ANY;
			case K_UNION:
				return K_INTERSECTION;
			default:
				return kind;
		}
	}

	/**
	 * The following constant is not actually a valid kind; however, it's
	 * helpful to think of it as one.
	 */
	private static final int K_INTERSECTION = -1;

	private static int[] removeIndex(int index, int[] children) {
		int[] nchildren = new int[children.length - 1];
		for (int j = 0; j < children.length; ++j) {
			if (j < index) {
				nchildren[j] = children[j];
			} else if (j > index) {
				nchildren[j - 1] = children[j];
			}
		}
		return nchildren;
	}

	/**
	 * This rule flattens children which have the same kind as the given state.
	 *
	 * @param index
	 * @param state
	 * @param automaton
	 * @return
	 */
	private static boolean flattenChildren(int index, Automaton.State state,
			Automaton automaton) {
		ArrayList<Integer> nchildren = new ArrayList<Integer>();
		int[] children = state.children;
		final int kind = state.kind;

		for (int i = 0; i < children.length; ++i) {
			int iChild = children[i];
			Automaton.State child = automaton.states[iChild];
			if (child.kind == kind) {
				for (int c : child.children) {
					nchildren.add(c);
				}
			} else {
				nchildren.add(iChild);
			}
		}

		children = new int[nchildren.size()];
		for (int i = 0; i < children.length; ++i) {
			children[i] = nchildren.get(i);
		}

		automaton.states[index] = new Automaton.State(kind, false, children);

		return true;
	}
}
