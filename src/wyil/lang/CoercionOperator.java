package wyil.lang;

import static wyil.lang.Type.*;
import wyautl.lang.*;


public final class CoercionOperator extends SubtypeOperator {
	
	public CoercionOperator(Automata fromAutomata, Automata toAutomata) {
		super(fromAutomata,toAutomata);
	}
	
	public boolean isSubtype(int fromIndex, int toIndex, BinaryMatrix subtypes,
			BinaryMatrix suptypes) {
		Automata.State fromState = from.states[fromIndex];
		Automata.State toState = to.states[toIndex];

		if (fromState.kind == K_CHAR && toState.kind == K_INT) {
			// ints can flow into chars
			return true;
		} else if (fromState.kind == K_INT && toState.kind == K_CHAR) {
			// chars can flow into ints
			return true;
		} else if (fromState.kind == K_RATIONAL
				&& (toState.kind == K_INT || toState.kind == K_CHAR)) {
			// ints or chars can flow into rationals
			return true;
		} else if (fromState.kind == K_SET && toState.kind == K_LIST) {
			return subtypes.get(fromState.children[0], toState.children[0]);
		} else if (fromState.kind == K_DICTIONARY && toState.kind == K_LIST) {
			int[] fromChildren = fromState.children;
			return from.states[fromChildren[0]].kind == K_INT
					&& subtypes.get(fromChildren[1], toState.children[0]);
		} else if (fromState.kind == K_LIST && toState.kind == K_STRING) {
			// TO DO: this is a bug here for cases when the element type is e.g.
			// int|real
			int[] fromChildren = fromState.children;
			Automata.State childState = from.states[fromChildren[0]];
			return childState.kind == K_ANY || childState.kind == K_INT
					|| childState.kind == K_RATIONAL;
		} else {
			return super.isSubtype(fromIndex, toIndex, subtypes, suptypes);
		}
	}
}