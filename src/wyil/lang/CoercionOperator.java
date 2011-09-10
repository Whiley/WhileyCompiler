package wyil.lang;

import static wyil.lang.Type.*;
import wyautl.lang.*;


public final class CoercionOperator extends IntersectionOperator {
	
	public CoercionOperator(Automata fromAutomata, Automata toAutomata) {
		super(fromAutomata,toAutomata);
	}
	
	public boolean isIntersection(int fromIndex, boolean fromSign, int toIndex,
			boolean toSign) {
		Automata.State fromState = from.states[fromIndex];
		Automata.State toState = to.states[toIndex];
		int fromKind = fromState.kind;
		int toKind = toState.kind;
		
		if (primitiveSubtype(fromKind,toKind)) {
			return fromSign == toSign || (fromSign && !toSign);
		} else if (primitiveSubtype(toKind,fromKind)) {
			return fromSign == toSign || (toSign && !fromSign);
		} else {
			// TODO: deal with lists and sets
			return super.isIntersection(fromIndex, fromSign, toIndex, toSign);
		}
	}
	
	private static boolean primitiveSubtype(int fromKind, int toKind) {
		if (fromKind == K_CHAR && toKind == K_INT) {
			// ints can flow into chars
			return true;
		} else if (fromKind == K_INT && toKind == K_CHAR) {
			// chars can flow into ints
			return true;
		} else if (fromKind == K_RATIONAL
				&& (toKind == K_INT || toKind == K_CHAR)) {
			// ints or chars can flow into rationals
			return true;
		}
		return false;
	}
}