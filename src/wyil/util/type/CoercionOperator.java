package wyil.util.type;

import static wyil.lang.Type.*;
import wyautl.lang.*;

/**
 * <p>
 * The coercion operator extends the basic subtype operator to consider
 * <i>implicit coercions</i> in the subtype computation. Thus, <code>T1</code>
 * is a <i>coercive subtype</i> of <code>T2</code> iff <code>T1 ~> T3</code> and
 * <code>T3 <: T2</code> (where <code>T1 ~> T3</code> is taken to mean
 * <code>T1</code> can be implicitly coerced into <code>T3</code>).
 * </p>
 * <p>
 * There are several places in the Whiley language where implicit coercions are
 * applied. For example, in the following:
 * 
 * <pre>
 * real f(int x):
 *     return x
 * </pre>
 * 
 * The above compiles correctly because, while <code>real :> int</code> does not
 * hold, an <code>int</code> can be implicitly coerced into a <code>real</code>.
 * </p>
 * <p>
 * <b>NOTE:</b> as for the subtype operator, both types must have been
 * normalised beforehand to guarantee correct results from this operator.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class CoercionOperator extends SubtypeOperator {
	
	public CoercionOperator(Automata fromAutomata, Automata toAutomata) {
		super(fromAutomata,toAutomata);
	}
	
	@Override
	public boolean isIntersectionInner(int fromIndex, boolean fromSign, int toIndex,
			boolean toSign) {		
		Automata.State fromState = from.states[fromIndex];
		Automata.State toState = to.states[toIndex];
		int fromKind = fromState.kind;
		int toKind = toState.kind;
		
		if (primitiveSubtype(fromKind,toKind)) {
			return fromSign == toSign || (fromSign && !toSign);
		} else if(fromKind == K_SET && toKind == K_LIST) {
			if (fromSign != toSign) {
				// nary nodes
				int fromChild = fromState.children[0];
				int toChild = toState.children[0];
				if (!isIntersection(fromChild, fromSign, toChild, toSign)) {
					return false;
				}
			}
			return true;
		} else if(fromKind == K_DICTIONARY && toKind == K_LIST) {
			if (!fromSign || !toSign) {
				// nary nodes
				int fromKey = fromState.children[0];
				int fromValue = fromState.children[1];
				int toChild = toState.children[0];
				Automata.State fromKeyState = from.states[fromKey];
				if (fromKeyState.kind != K_INT) {
					return fromSign != toSign;
				} else if (!isIntersection(fromValue, fromSign, toChild, toSign)) {
					return false;
				}
			}
			return true;
		} else if(fromKind == K_DICTIONARY && toKind == K_SET) {
			if (!fromSign || !toSign) {
				// TO DO: this is a bug here for cases when the element type is e.g. int|real
				int toChild = toState.children[0];				
				int toChildKind = to.states[toChild].kind;
				if (toChildKind != K_VOID) {					
					return fromSign != toSign;
				} 
			}
			return fromSign == toSign;
		} else if(fromKind == K_LIST && toKind == K_STRING) {			
			if (!fromSign || !toSign) {
				// TO DO: this is a bug here for cases when the element type is e.g. int|real
				int fromChild = fromState.children[0];				
				int fromChildKind = from.states[fromChild].kind;
				if (fromChildKind != K_INT
						&& fromChildKind != K_RATIONAL
						&& fromChildKind != K_ANY) {					
					return fromSign != toSign;
				} 
			}
			return fromSign == toSign;
		} else {
			// TODO: deal with lists and sets
			return super.isIntersectionInner(fromIndex, fromSign, toIndex, toSign);
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