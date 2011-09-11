package wyil.lang;

import java.util.ArrayList;

import wyautl.lang.*;

/**
 * <p>
 * The simplification rule implements a rewrite rule for the automata
 * representation of types in Whiley. The rewrite rule applies a number of
 * relatively straightforward simplifications, including:
 * </p>
 * <ul>
 * <li><code>T | any</code> => <code>any</code>.</li>
 * <li><code>T & any</code> => <code>T</code>.
 * <li><code>T | void</code> => <code>T</code>.</li>
 * <li><code>T & void</code> => <code>void</code>.
 * <li><code>(T_1 & T_2) & T_3</code> => <code>(T_1 & T_2) & T_3</code>.</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> this rules will not operate correctly unless the given type is
 * already in CNF. <b>NOTE:</b> applications of this rewrite rule may leave
 * </p>
 * <p>
 * states which are unreachable from the root. Therefore, the resulting automata
 * should be extracted after rewriting to eliminate any such states.
 * </p>
 * 
 * @author djp
 * 
 */
public final class IntersectionRule implements RewriteRule {
	
	public final boolean apply(int index, Automata automata) {
		Automata.State state = automata.states[index];
		switch(state.kind) {
		case Type.K_INTERSECTION:
			return applyIntersection(index,state,automata);
		}
		return false;
	}
	
	public boolean applyIntersection(int index, Automata.State state,
			Automata automata) {
		boolean changed = false;
		int[] children = state.children;		
		int kind = Type.K_ANY;
		for(int i=0;i!=children.length;++i) {			
			int iChild = children[i];
			// check whether this child is subsumed				
			Automata.State child = automata.states[iChild];
			if(kind == Type.K_ANY) {				
				kind = child.kind;
			} else if (kind != child.kind && child.kind != Type.K_ANY) {
				// FIXME: need to check records and existentials here
				// this indicates the intersection is equivalent to void
				automata.states[index] = new Automata.State(Type.K_VOID);
				return true;
			} else if(child.kind == Type.K_ANY) {	
				children = removeIndex(i,children);
				state.children = children;				
				changed=true;			
			}
		}	
		
		// TODO: need to propagate intersections through kinds.
		
		if(children.length == 0) {
			// this can happen in the case of a union which has only itself as a
			// child.
			automata.states[index] = new Automata.State(Type.K_VOID);
			changed = true;
		} else if(children.length == 1) {
			// bypass this node altogether
			int child = children[0];
			automata.states[index] = new Automata.State(automata.states[child]);
			changed = true;
		} 
		return changed;
	}	

	private static int[] removeIndex(int index, int[] children) {
		int[] nchildren = new int[children.length-1];
		for(int j=0;j!=children.length;++j) {
			if(j<index) {
				nchildren[j] = children[j]; 
			} else if(j>index) {
				nchildren[j-1] = children[j];
			}
		}
		return nchildren;
	}
}
