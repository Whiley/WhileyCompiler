package wyil.util.type;

import static wyil.lang.Type.*;
import wyautl.lang.*;

/**
 * <p>
 * The explicit coercion operator extends the implicit coercion operator to
 * include coercions which must be specified with an explicit cast operation.
 * This is necessary because such coercions correspond to a loss of precision
 * and, hence, may fail at runtime. An example is the following
 * </p>
 * 
 * <pre>
 * char f(int x):
 *     return (char) x
 * </pre>
 * 
 * <p>
 * The above will only compile if the explicit <code>(char)</code> cast is
 * provided. This is required because a <code>char</code> corresponds only to a
 * subset of the possible integers (i.e. those codepoints defined by the Unicode
 * standard). The semantics of the Whiley language dictate that, should the
 * integer lie outside the range of permissible code points, then a runtime
 * fault is raised.
 * </p>
 * 
 * <b>NOTE:</b> as for the subtype operator, both types must have been
 * normalised beforehand to guarantee correct results from this operator. </p>
 * 
 * @author David J. Pearce
 * 
 */
public class ExplicitCoercionOperator extends ImplicitCoercionOperator {
	
	public ExplicitCoercionOperator(Automaton fromAutomata, Automaton toAutomata) {
		super(fromAutomata,toAutomata);
	}
	
	@Override
	public boolean isIntersectionInner(int fromIndex, boolean fromSign,
			int toIndex, boolean toSign) {
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		int fromKind = fromState.kind;
		int toKind = toState.kind;

		if (primitiveSubtype(fromKind, toKind)) {
			return fromSign == toSign || (fromSign && !toSign);
		} else {
			return super.isIntersectionInner(fromIndex, fromSign, toIndex,
					toSign);
		}
	}
	
	private static boolean primitiveSubtype(int fromKind, int toKind) {
		if (fromKind == K_CHAR && toKind == K_INT) {
			// ints can flow (explicitly) into chars
			return true;
		} 
		return false;
	}
}