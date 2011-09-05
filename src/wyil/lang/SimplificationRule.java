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
 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> => <code>T_1</code>.
 * <li><code>T_1 & T_2</code> where <code>T_1 :> T_2</code> => <code>T_2</code>.
 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2) | T_3</code>.
 * <li><code>(T_1 & T_2) & T_3</code> => <code>(T_1 & T_2) & T_3</code>.</li>
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
public final class SimplificationRule implements RewriteRule {
	public final SubtypeOperator subtypes;
	
	public SimplificationRule(SubtypeOperator subtypes) {
		this.subtypes = subtypes;
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
	
	public boolean applyIntersection(int index, Automata.State state,
			Automata automata) {
		boolean changed = false;
		int[] children = state.children;		
		ArrayList<Integer> nchildren = new ArrayList<Integer>();
		
		for(int i=0;i!=children.length;++i) {			
			int iChild = children[i];
			Automata.State child = automata.states[iChild];
			switch(child.kind) {
			case Type.K_VOID:								
				automata.states[index] = new Automata.State(Type.K_VOID);
				return true;			
			case Type.K_INTERSECTION:
				for(int c : child.children) { 
					nchildren.add(c);
				}
				changed=true;
				break;
			default:
				// check for contractive case
				if(iChild != index) {					
					// check whether this child is subsumed
					boolean subsumed = false;
					for (int j = 0; j < children.length; ++j) {
						int jChild = children[j];
						if (i != j
								&& subtypes.isRelated(iChild, jChild)
								&& (!subtypes.isRelated(jChild, iChild) || i > j)) {
							subsumed = true;
						}
					}
					if(!subsumed) {
						nchildren.add(iChild);
					} else {
						changed = true;
					}
				} else {
					changed = true;
				}
			}						
		}	
		if(nchildren.size() == 0) {
			// this can happen in the case of an intersection which has only itself as a
			// child.
			automata.states[index] = new Automata.State(Type.K_VOID);
		} else if(nchildren.size() == 1) {
			// bypass this node altogether
			int child = nchildren.get(0);
			automata.states[index] = new Automata.State(automata.states[child]);			
		} else if(changed) {			
			children = new int[nchildren.size()];
			for(int i=0;i!=children.length;++i) {
				children[i] = nchildren.get(i);
			}
			automata.states[index] = new Automata.State(Type.K_INTERSECTION, children,
					false);
		}
		return changed;
	}	
	
	public boolean applyUnion(int index, Automata.State state,
			Automata automata) {
		boolean changed = false;
		int[] children = state.children;		
		ArrayList<Integer> nchildren = new ArrayList<Integer>();
		
		for(int i=0;i!=children.length;++i) {			
			int iChild = children[i];
			Automata.State child = automata.states[iChild];
			switch(child.kind) {
			case Type.K_ANY:								
				automata.states[index] = new Automata.State(Type.K_ANY);
				return true;			
			case Type.K_UNION:
				for(int c : child.children) { 
					nchildren.add(c);
				}
				changed=true;
				break;
			default:
				// check for contractive case
				if(iChild != index) {					
					// check whether this child is subsumed
					boolean subsumed = false;
						for (int j = 0; j < children.length; ++j) {
							int jChild = children[j];
							if (i != j
									&& subtypes.isRelated(jChild, iChild)
									&& (!subtypes.isRelated(iChild, jChild) || i > j)) {
								subsumed = true;
							}
						}
					if(!subsumed) {
						nchildren.add(iChild);
					} else {
						changed = true;
					}
				} else {
					changed = true;
				}
			}						
		}
		
		if(nchildren.size() == 0) {
			// this can happen in the case of a union which has only itself as a
			// child.
			automata.states[index] = new Automata.State(Type.K_VOID);
		} else if(nchildren.size() == 1) {
			// bypass this node altogether
			int child = nchildren.get(0);
			automata.states[index] = new Automata.State(automata.states[child]);			
		} else if(changed) {			
			children = new int[nchildren.size()];
			for(int i=0;i!=children.length;++i) {
				children[i] = nchildren.get(i);
			}
			automata.states[index] = new Automata.State(Type.K_UNION, children,
					false);
		}
		return changed;
	}	
}
