package wyil.lang;

import java.util.ArrayList;
import java.util.Arrays;

import wyautl.lang.*;

/**
 * <p>
 * This simplification rule implements a rewrite for the automata
 * representation of types in Whiley. The rewrite rule applies a number of
 * relatively straightforward simplifications, including:
 * </p>
 * <ul>
 * <li><code>T & any</code> => <code>T</code>.
 * <li><code>[T_1] & [T_2]</code> => <code>[T_1 & T_2]</code>.
 * <li><code>{T_1} & {T_2}</code> => <code>{T_1 & T_2}</code>.
 * <li><code>process {T_1} & process {T_2}</code> => <code>process {T_1 & T_2}</code>.
 * <li><code>{T_1->T_2} & {T_3->T_4}</code> => <code>{(T_1 & T_3)->(T_2&T_4)}</code>.
 * <li><code>(T_1,T_2) & (T_3,T_4)</code> => <code>(T_1 & T_3,T_2 & T_4)</code>.
 * <li><code>{T_1 f} & {T_2 f)</code> => <code>{T_1 & T_3 f}</code>.
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
public final class IntersectionRewrites implements RewriteRule {
	
	public final boolean apply(int index, Automata automata) {
		Automata.State state = automata.states[index];
		switch(state.kind) {
			case Type.K_PROCESS:
			case Type.K_DICTIONARY:		
			case Type.K_TUPLE:
			case Type.K_RECORD:		
			case Type.K_FUNCTION:
			case Type.K_METHOD:
			case Type.K_HEADLESS:
				return applyOther(index,state,automata);				
		case Type.K_INTERSECTION:
			return applyIntersection(index,state,automata);
		}
		return false;
	}
	
	public boolean applyOther(int index, Automata.State state,
			Automata automata) {		
					
		for(int childIndex : state.children) {
			Automata.State child = automata.states[childIndex];
			if(child.kind == Type.K_VOID) {
				automata.states[index] = new Automata.State(Type.K_VOID);
				return true;
			}
		}	
		
		return false;
	}	
	
	public boolean applyIntersection(int index, Automata.State state,
			Automata automata) {
		boolean changed = false;
		int[] children = state.children;		
		Object data = null;
		
		int kind = Type.K_ANY;
		int numChildren = 0;
		for(int i=0;i<children.length;++i) {			
			int iChild = children[i];
			// check whether this child is subsumed				
			Automata.State child = automata.states[iChild];
			if(kind == Type.K_ANY && child.kind != Type.K_ANY) {				
				kind = child.kind;
				data = child.data;
				numChildren = child.children.length;
			} else if (kind != child.kind && child.kind != Type.K_ANY) {				
				automata.states[index] = new Automata.State(Type.K_VOID);
				return true;
			} else if(child.kind == Type.K_ANY) {				
				children = removeIndex(i--,children);
				state.children = children;				
				changed=true;			
			} else if (kind == child.kind) {				
				if (child.children.length == numChildren
						&& (data == child.data || (data != null && data
								.equals(child.data)))) {
					// this is ok
				} else {										
					automata.states[index] = new Automata.State(Type.K_VOID);
					return true;
				}
			} 
		}	
		
		if(children.length == 0) {			
			automata.states[index] = new Automata.State(Type.K_ANY);
			return true;			
		} else if(children.length == 1) {
			// bypass this node altogether
			int child = children[0];
			automata.states[index] = new Automata.State(automata.states[child]);
			return true;
		} 
		switch(kind) {
			case Type.K_FUNCTION:
			case Type.K_HEADLESS:
			case Type.K_METHOD:
				throw new RuntimeException("Need to deal with intersection of function types");
			case Type.K_PROCESS:
			case Type.K_SET:
			case Type.K_LIST:
			case Type.K_TUPLE:
			case Type.K_RECORD:									
				int[] nchildren = new int[numChildren];
				Automata.State[] nstates = new Automata.State[nchildren.length];				
				for (int i = 0; i != numChildren; ++i) {
					nchildren[i] = i + automata.size();
					int[] nChildChildren = new int[children.length];
					for (int j = 0; j != children.length; ++j) {
						int childIndex = children[j];
						Automata.State child = automata.states[childIndex];
						nChildChildren[j] = child.children[i];
					}
					nstates[i] = new Automata.State(Type.K_INTERSECTION,
							nChildChildren,false);
				}
				Automatas.inplaceAppendAll(automata, nstates);
				state = automata.states[index];				
				state.kind = kind;				
				state.children = nchildren;
				state.deterministic = true;
				changed=true;
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
