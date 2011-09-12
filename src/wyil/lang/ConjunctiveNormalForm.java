package wyil.lang;

import java.util.ArrayList;

import wyautl.lang.*;

/**
 * <p>
 * This simplification rule converts a type into <i>conjunctive normal form</i>.
 * This is achieved by repeated application of the following rewrites:
 * </p>
 * <ul>
 * <li><code>T | void</code> => <code>T</code>.</li>
 * <li><code>T | any</code> => <code>any</code>.</li>
 * <li><code>T & void</code> => <code>void</code>.</li>
 * <li><code>!!T</code> => <code>T</code>.</li>
 * <li><code>!any</code> => <code>void</code>.</li>
 * <li><code>!void</code> => <code>any</code>.</li>
 * <li><code>!(T_1 | T_2)</code> => <code>!T_1 & !T_2</code>.</li>
 * <li><code>!(T_1 & T_2)</code> => <code>!T_1 | !T_2</code>.</li>
 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
 * <li><code>(T_1 & T_2) & T_3</code> => <code>(T_1 & T_2 & T_3)</code>.</li>
 * <li><code>T_1 & (T_2|T_3)</code> => <code>(T_1 & T_2) | (T_1 & T_3)</code>.</li>
 * </ul>
 * <p>
 * <b>NOTE:</b> applications of this rewrite rule may leave states which are
 * unreachable from the root. Therefore, the resulting automata should be
 * extracted after rewriting to eliminate any such states.
 * </p>
 * @author djp
 * 
 */
public final class ConjunctiveNormalForm implements RewriteRule {
	
	public final boolean apply(int index, Automata automata) {
		Automata.State state = automata.states[index];
		switch(state.kind) {
		case Type.K_UNION:
			return applyUnion(index,state,automata);
		case Type.K_INTERSECTION:
			return applyIntersection(index,state,automata);
		case Type.K_NEGATION:
			return applyNot(index,state,automata);
		}
		return false;
	}
	
	public boolean applyNot(int index, Automata.State state,
			Automata automata) {				
		int childIndex = state.children[0];
		Automata.State child = automata.states[childIndex];
		switch(child.kind) {
			case Type.K_ANY:
				automata.states[index] = new Automata.State(Type.K_VOID);
				return true;
			case Type.K_VOID:
				automata.states[index] = new Automata.State(Type.K_ANY);
				return true;
			case Type.K_NEGATION:
				// bypass this node altogether
				int childChildIndex = child.children[0];
				Automata.State childChild = automata.states[childChildIndex];
				automata.states[index] = new Automata.State(childChild);
				return true;
			case Type.K_UNION:
			case Type.K_INTERSECTION: {						
				int[] child_children = child.children;
				int[] nchildren = new int[child_children.length];
				Automata.State[] nstates = new Automata.State[child_children.length];				
				for(int i=0;i!=child_children.length;++i) {					
					int[] children = new int[1];
					children[0] = child_children[i];
					nchildren[i] = i + automata.size();
					nstates[i] = new Automata.State(Type.K_NEGATION,children);
				}
				Automatas.inplaceAppendAll(automata, nstates);
				state = automata.states[index];
				int nkind = child.kind == Type.K_UNION ? Type.K_INTERSECTION : Type.K_UNION;
				state.kind = nkind;				
				state.children = nchildren;
				state.deterministic = false;
				
				return true;
			}
			default:
				return false;
		}
	}
	
	public boolean applyIntersection(int index, Automata.State state,
			Automata automata) {				
		int[] children = state.children;		
		boolean changed = false;
		for(int i=0;i!=children.length;++i) {			
			int iChild = children[i];
			if(iChild == index) {
				// contractive case				
				children = removeIndex(i,children);
				state.children = children;				
				changed=true;
			} else {
				Automata.State child = automata.states[iChild];
				switch(child.kind) {
				case Type.K_VOID:								
					automata.states[index] = new Automata.State(Type.K_VOID);
					return true;				
				case Type.K_INTERSECTION:
					return flattenChildren(index,state,automata);
				case Type.K_UNION: {
					int[] child_children = child.children;
					int[] nchildren = new int[child_children.length];
					Automata.State[] nstates = new Automata.State[child_children.length];
					for(int j=0;j!=child_children.length;++j) {
						int jChildIndex = child_children[j];
						int[] kchildren = new int[children.length];
						nchildren[j] = automata.size()+j;
						for(int k=0;k!=children.length;++k) {
							if(k != i) {
								kchildren[k] = children[k];
							} else {
								kchildren[i] = jChildIndex;
							}
						}
						nstates[j] = new Automata.State(Type.K_INTERSECTION,kchildren);					
					}
					Automatas.inplaceAppendAll(automata, nstates);
					state = automata.states[index];
					state.kind = Type.K_UNION;				
					state.children = nchildren;				

					return true;
				}
				}
			}
		}
		if(children.length == 0) {
			// this can happen in the case of an intersection which has only
			// itself as a child.
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
		
	public boolean applyUnion(int index, Automata.State state,
			Automata automata) {		
		int[] children = state.children;		
		boolean changed = false;
		for(int i=0;i!=children.length;++i) {			
			int iChild = children[i];
			if(iChild == index) {
				// contractive case
				children = removeIndex(i,children);
				state.children = children;				
				changed=true;
			} else {
				Automata.State child = automata.states[iChild];
				switch(child.kind) {
				case Type.K_ANY:								
					automata.states[index] = new Automata.State(Type.K_ANY);
					return true;			
				case Type.K_VOID: {
					children = removeIndex(i,children);
					state.children = children;				
					changed=true;
				}				
				case Type.K_UNION:
					return flattenChildren(index,state,automata);
				}
			}
		}
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
	
	/**
	 * This rule flattens children which have the same kind as the given state.
	 * 
	 * @param index
	 * @param state
	 * @param automata
	 * @return
	 */
	public boolean flattenChildren(int index,
			Automata.State state, Automata automata) {
		ArrayList<Integer> nchildren = new ArrayList<Integer>();
		int[] children = state.children;
		final int kind = state.kind;		
		
		for (int i = 0; i != children.length; ++i) {
			int iChild = children[i];
			Automata.State child = automata.states[iChild];
			if(child.kind == kind) {
				for (int c : child.children) {
					nchildren.add(c);
				}
			} else {				
				nchildren.add(iChild);
			}
		}

		children = new int[nchildren.size()];
		for (int i = 0; i != children.length; ++i) {
			children[i] = nchildren.get(i);
		}
		
		automata.states[index] = new Automata.State(kind,
				children, false);

		return true;
	}	
}
