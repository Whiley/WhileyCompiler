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
 * @author djp
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
		} else if (primitiveSubtype(toKind,fromKind)) {			
			return fromSign == toSign || (toSign && !fromSign);
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