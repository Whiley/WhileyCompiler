package wyil.lang;

import java.util.ArrayList;

import wyautl.lang.*;

/**
 * <p>
 * This simplification rule implements a rewrite for removing subsumed nodes
 * from the graph. More specifically, the following rewrites are applied:
 * </p>
 * <ul>
 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> => <code>T_1</code>.
 * <li><code>T_1 & T_2</code> where <code>T_1 :> T_2</code> => <code>T_2</code>.
 * </ul>
 * <p>
 * <b>NOTE:</b> applications of this rewrite rule may leave states which are
 * unreachable from the root. Therefore, the resulting automata should be
 * extracted after rewriting to eliminate any such states.
 * </p>
 * 
 * @author djp
 * 
 */
public final class SubsumptionRewrites implements RewriteRule {
	public IntersectionOperator subtypes;
	
	public SubsumptionRewrites(Automata automata) {
			this.subtypes = new IntersectionOperator(automata,automata);
			Automatas.computeFixpoint(subtypes);
	}
	
	public final boolean apply(int index, Automata automata) {
		Automata.State state = automata.states[index];
		switch(state.kind) {
		case Type.K_UNION:
			return applyUnion(index,state,automata);
		case Type.K_INTERSECTION:
			return applyIntersection(index,state,automata);
		}
		return false;
	}
	
	/**
	 * This rule removes subsumed children from an intersection.
	 * 
	 * @param index
	 * @param state
	 * @param automata
	 * @return
	 */
	public boolean applyIntersection(int index, Automata.State state,
			Automata automata) {
		boolean changed = false;
		int[] children = state.children;		
		
		for(int i=0;i!=children.length;++i) {			
			int iChild = children[i];				
			// check whether this child is subsumed
			boolean subsumed = false;
			for (int j = 0; j < children.length; ++j) {
				if (i == j) {
					continue;
				}
				int jChild = children[j];
				boolean irj = subtypes.isSubtype(iChild, jChild);
				boolean jri = subtypes.isSubtype(jChild, iChild);
				if (irj && (!jri || i > j)) {
					subsumed = true;
				} else if (subtypes.isIntersection(iChild, jChild)) {
					// no intersection is possible!
					automata.states[index] = new Automata.State(Type.K_VOID);
					return true;
				}
			}
			if(subsumed) {					
				changed = true;
				children = removeIndex(i--,children);
				state.children = children;
			} 							
		}	
		
		if(children.length == 1) {
			// bypass this node altogether
			int child = children[0];
			automata.states[index] = new Automata.State(automata.states[child]);			
		} 
		
		return changed;
	}	
	
	public boolean applyUnion(int index, Automata.State state,
			Automata automata) {
		boolean changed = false;
		int[] children = state.children;				
		
		for(int i=0;i!=children.length;++i) {			
			int iChild = children[i];										
			// check whether this child is subsumed
			boolean subsumed = false;
			for (int j = 0; j < children.length; ++j) {
				int jChild = children[j];
				if (i != j && subtypes.isSubtype(jChild, iChild)
						&& (!subtypes.isSubtype(iChild, jChild) || i > j)) {
					subsumed = true;
				}
			}
			if(subsumed) {
				children = removeIndex(i--,children);
				state.children = children;
				changed=true;
			} 
		}
		
		if(children.length == 1) {
			// bypass this node altogether
			int child = children[0];
			automata.states[index] = new Automata.State(automata.states[child]);			
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
