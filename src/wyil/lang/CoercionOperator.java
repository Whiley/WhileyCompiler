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

		if (primitiveIntersection(fromState.kind,fromSign,toState.kind,toSign)) {
			return true;
			//return fromSign == toSign || (fromSign && !toSign);
		} else {
			return super.isIntersection(fromIndex, fromSign, toIndex, toSign);
		}
	}
	
	private static boolean primitiveIntersection(int fromKind,
			boolean fromSign, int toKind, boolean toSign) {
		boolean fromTo = primitiveSubtype(fromKind,toKind);
		boolean toFrom = primitiveSubtype(toKind,fromKind);
		
		if(fromSign == toSign) {
			return fromTo || toFrom; 
		} else if(fromSign) {
			return fromTo;
		} else {
			return toFrom;
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