package wyil.util.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import wyautl_old.lang.Automaton;
import wyautl_old.lang.Automaton.State;
import wyil.lang.Type;

/**
 * This class can be used to substitute lifetimes, i.e. instantiate lifetime
 * parameters with actual lifetime arguments.
 * 
 * It is not a trivial task, as you must take care to not capture lifetime
 * parameters within nested method types. This is especially difficult when
 * types are recursive.
 * 
 * The method type defined by
 * 
 * <pre>
 * type mymethod is method<a>(&*:mymethod)->(&a:mymethod)
 * </pre>
 * 
 * returns
 * 
 * <pre>
 * X<&a:Y<method<a>(&Y)->(X)>>
 * </pre>
 * 
 * Assume the argument for parameter "a" is "b". You cannot just replace "&a:"
 * with "&b:", because it would also affect the nested return type. Instead, we
 * need this type here:
 * 
 * <pre>
 * &b:X<method<a>(&X)->( Y<&a:Z<method<a>(&Z)->(Y)>> )>
 * </pre>
 *
 * It can be further simplified to
 * 
 * <pre>
 * &b:X<method<a>(&X)->(&a:X)>
 * </pre>
 * 
 * Note that the original type has two different references, but the substituted
 * and minimized type has three reference types with different lifetimes. They
 * cannot be mapped to the same automaton state. Substitution might yield an
 * automaton with more states!
 */
public class LifetimeSubstitution {

	/**
	 * The substitution to apply
	 */
	private final Map<String, String> substitution;

	/**
	 * The substituted type
	 */
	private final Type substituted;

	/**
	 * The states in the original automaton
	 */
	private final State[] originalStates;

	/**
	 * The states in our new automaton
	 */
	private final List<State> substitutedStates;

	/**
	 * For each old state we can have several new states. They differ in the set
	 * of lifetimes that had to be substituted.
	 */
	private final Map<Integer, List<SubstitutedState>> mapping;

	/**
	 * Apply the substitution.
	 *
	 * @param original
	 *            the original automaton
	 * @param substitution
	 *            the substitution to apply
	 */
	public LifetimeSubstitution(Type original, Map<String, String> substitution) {
		this.substitution = substitution;

		if (substitution.isEmpty()) {
			// easy!
			substituted = original;

			// we do not need those fields
			originalStates = null;
			substitutedStates = null;
			mapping = null;
		} else {
			// initialize all fields
			originalStates = Type.destruct(original).states;
			substitutedStates = new ArrayList<State>(originalStates.length);
			mapping = new HashMap<>(originalStates.length);

			// Start with the root state
			SubstitutedState newRootState = copy(0, Collections.<String>emptySet());
			if (newRootState.substitutedLiffetimes.isEmpty()) {
				// nothing got substituted!
				substituted = original;
			} else {
				// Construct the resulting type.
				substituted = Type.construct(new Automaton(substitutedStates));
			}
		}
	}

	/**
	 * Get the substituted type.
	 *
	 * @return the substituted type
	 */
	public Type getType() {
		return substituted;
	}

	/**
	 * Copy the given state into our new automaton while applying the
	 * substitution but ignoring the given lifetimes.
	 *
	 * @param index
	 *            the state index from the original automaton
	 * @param ignored
	 *            do not apply substitution for these lifetimes
	 * @return
	 */
	private SubstitutedState copy(int index, Set<String> ignored) {
		List<SubstitutedState> mapped = mapping.get(index);
		if (mapped == null) {
			mapped = new LinkedList<>();
			mapping.put(index, mapped);
		} else {
			outer: for (SubstitutedState entry : mapped) {
				if (ignored.containsAll(entry.ignoredLifetimes)) {
					// This entry might be suitable for us, but only if it did
					// not substitute any lifetime that should be ignored now.
					for (String substitutedLifetime : entry.substitutedLiffetimes) {
						if (ignored.contains(substitutedLifetime)) {
							// no, cannot re-use that one
							continue outer;
						}
					}
					return entry;
				}
			}
		}

		// OK, we need to copy that state.
		SubstitutedState substitutedState = new SubstitutedState(
				substitutedStates.size(), // next available index
				ignored,
				new HashSet<String>()
		);
		mapped.add(substitutedState);
		State state = new State(originalStates[index]);
		substitutedStates.add(state);

		switch (state.kind) {
		case Type.K_REFERENCE:
			// If it is a reference, then we might have to fix the lifetime
			String lifetime = (String) state.data;
			if (!ignored.contains(lifetime)) {
				String replacement = substitution.get(lifetime);
				if (replacement != null) {
					// OK, we need to replace
					substitutedState.substitutedLiffetimes.add(lifetime);
					state.data = replacement;
				}
			}
			break;

		case Type.K_FUNCTION:
		case Type.K_METHOD:
			// We need to apply the substitution to the context lifetimes
			Type.FunctionOrMethod.Data data = (Type.FunctionOrMethod.Data) state.data;
			if (!data.contextLifetimes.isEmpty()) {
				ListIterator<String> it = data.contextLifetimes.listIterator();
				boolean updated = false;
				while (it.hasNext()) {
					String lifetime2 = it.next();
					if (!ignored.contains(lifetime2)) {
						String replacement = substitution.get(lifetime2);
						if (replacement != null) {
							// OK, we need to replace
							substitutedState.substitutedLiffetimes.add(lifetime2);
							it.set(replacement);
							updated = true;
						}
					}
				}
				if (updated) {
					// Context lifetimes should be sorted
					Collections.sort(data.contextLifetimes);
				}
			}

			// We need to ignore lifetime parameters when recursing to the children children.
			if (!data.lifetimeParameters.isEmpty()) {
				ignored = new HashSet<String>(ignored);
				ignored.addAll(data.lifetimeParameters);
			}
			break;

		default:
		}

		if (state.children.length != 0) {
			// We cannot yet know which lifetimes will be substituted, but need
			// to specify something before we enter the children.
			// Overestimating does not harm, so we assume that all lifetimes
			// that should be substituted actually will be substituted
			// But we keep track of which lifetimes got substituted in the children.
			Set<String> childSubstituted = new HashSet<String>(substitutedState.substitutedLiffetimes);
			substitutedState.substitutedLiffetimes.addAll(substitution.keySet());
			substitutedState.substitutedLiffetimes.removeAll(ignored);

			// Now copy all children
			for (int i = 0; i < state.children.length; ++i) {
				SubstitutedState child = copy(state.children[i], ignored);
				if (child != substitutedState) {
					// remember the substituted lifetimes
					// but only if that child is not directly recursively
					// referring to us :)
					childSubstituted.addAll(child.substitutedLiffetimes);
				}

				// fix the index
				state.children[i] = child.newStateIndex;
			}

			// Update the substituted lifetimes
			substitutedState.substitutedLiffetimes.retainAll(childSubstituted);
		}

		return substitutedState;
	}

	private static class SubstitutedState {
		private final int newStateIndex;
		private final Set<String> ignoredLifetimes;
		private final Set<String> substitutedLiffetimes;

		private SubstitutedState(int newStateIndex, Set<String> ignoredLifetimes, Set<String> substitutedLiffetimes) {
			this.newStateIndex = newStateIndex;
			this.ignoredLifetimes = ignoredLifetimes;
			this.substitutedLiffetimes = substitutedLiffetimes;
		}
	}
}
