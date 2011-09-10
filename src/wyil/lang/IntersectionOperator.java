package wyil.lang;

import static wyil.lang.Type.*;
import wyautl.lang.*;

public class IntersectionOperator implements Relation {
	private final Automata from;
	private final Automata to;
	private final BinaryMatrix intersections;
	
	public IntersectionOperator(Automata from, Automata to) {
		this.from = from;
		this.to = to;
		// matrix is twice the size to accommodate positive and negative signs 
		this.intersections = new BinaryMatrix(from.size()*2,to.size()*2,true);
	}
	
	public final Automata from() {
		return from;
	}
	
	public final Automata to() {
		return to;
	}
	
	public final boolean update(int fromIndex, int toIndex) {
		boolean oldTrueTrue = intersection(fromIndex,true,toIndex,true);
		boolean oldFalseTrue = intersection(fromIndex,false,toIndex,true);
		boolean oldTrueFalse = intersection(fromIndex,true,toIndex,false);
		boolean oldFalseFalse = intersection(fromIndex,false,toIndex,false);
		
		boolean newTrueTrue = isIntersection(fromIndex,true,toIndex,true);
		boolean newFalseTrue = isIntersection(fromIndex,false,toIndex,true);
		boolean newTrueFalse = isIntersection(fromIndex,true,toIndex,false);
		boolean newFalseFalse = isIntersection(fromIndex,false,toIndex,false);
		
		setIntersection(fromIndex,true,toIndex,true,newTrueTrue);
		setIntersection(fromIndex,false,toIndex,true,newFalseTrue);
		setIntersection(fromIndex,true,toIndex,false,newTrueFalse);
		setIntersection(fromIndex,false,toIndex,false,newFalseFalse);
		
		return oldTrueTrue != newTrueTrue || oldFalseTrue != newFalseTrue
				|| oldTrueFalse != newTrueFalse
				|| oldFalseFalse != newFalseFalse; 
	}
	
	public final boolean isRelated(int fromIndex, int toIndex) {
		return intersection(fromIndex,true,toIndex,true);
	}
	
	/**
	 * Test whether <code>from</code> :> <code>to</code>
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public final boolean isSubtype(int fromIndex, int toIndex) {
		return !intersection(fromIndex,false,toIndex,true);
	}
	
	/**
	 * Test whether <code>from</code> <: <code>to</code>
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public final boolean isSupertype(int fromIndex, int toIndex) {
		return !intersection(fromIndex,true,toIndex,false);
	}

	/**
	 * Determine whether there is a non-empty intersection between the state
	 * rooted at <code>fromIndex</code> and that rooted at <code>toIndex</code>.
	 * The signs indicate whether or not the state should be taken as its
	 * <i>inverse</i>.
	 * 
	 * @param fromIndex
	 *            --- index of from state
	 * @param fromSign
	 *            --- sign of from state (true = normal, false = inverted).
	 * @param toIndex
	 *            --- index of to state
	 * @param toSign
	 *            --- sign of from state (true = normal, false = inverted).
	 * @return --- true if such an intersection exists, false otherwise.
	 */
	public boolean isIntersection(int fromIndex, boolean fromSign, int toIndex,
			boolean toSign) {
		
		Automata.State fromState = from.states[fromIndex];
		Automata.State toState = to.states[toIndex];
		int fromKind = fromState.kind;
		int toKind = toState.kind;
		
		if(fromKind == toKind) {
			switch(fromKind) {
			case K_VOID:
				return !fromSign && !toSign;
			case K_ANY:
				return fromSign && toSign;
			// === Leaf States First ===
			case K_EXISTENTIAL:
				NameID nid1 = (NameID) fromState.data;
				NameID nid2 = (NameID) toState.data;				
				return fromSign == toSign && nid1.equals(nid2);			
			// === Homogenous Compound States ===
			case K_SET:
			case K_LIST:
			case K_PROCESS:
			case K_DICTIONARY:
			case K_TUPLE:  {
				// nary nodes
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				if (fromChildren.length != toChildren.length) {
					return false;
				}
				for (int i = 0; i < fromChildren.length; ++i) {
					if (!intersection(fromChildren[i], fromSign, toChildren[i], toSign)) {
						return false;
					}
				}
				return true;
			}
			case K_RECORD: {
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				if (fromChildren.length != toChildren.length) {
					return false;
				}				
				String[] fromFields = (String[]) fromState.data;
				String[] toFields = (String[]) toState.data;				
				
				for (int i = 0; i != fromFields.length; ++i) {
					String e1 = fromFields[i];
					String e2 = toFields[i];
					if(!e1.equals(e2)) { return false; }
					int fromChild = fromChildren[i];
					int toChild = toChildren[i];
					if(!intersection(fromChild,fromSign,toChild,toSign)) {
						return false;
					}					
				}									
				return true;	
			}
			case K_NOT: 
			case K_UNION : 
			case K_INTERSECTION:
					// let these cases fall through to if-statements after
					// switch.
				break;
			// === Heterogenous Compound States ===
			case K_FUNCTION:
			case K_HEADLESS:
			case K_METHOD:
				// nary nodes
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				if(fromChildren.length != toChildren.length){
					return false;
				}
				int start = 0;
				if(fromKind == K_METHOD) {
					// Check (optional) receiver value first
					// FIXME: receiver should be INVARIANT
					if (intersection(fromChildren[0], !fromSign,
							toChildren[0], !toSign)) {
						return false;
					}
					start++;
				}
				// Check return value first 
				int fromChild = fromChildren[start];
				int toChild = toChildren[start];
				if(!intersection(fromChild,fromSign,toChild,toSign)) {
					return false;
				}
				// Now, check parameters 
				for(int i=start+1;i<fromChildren.length;++i) {
						if (intersection(fromChildren[i], !fromSign,
								toChildren[i], !toSign)) {
						return false;
					}
				}
				return true;
			default:
				return fromSign == toSign;
			}
		} 
		
		if(fromKind == K_NOT) {
			int fromChild = fromState.children[0];
			return intersection(fromChild,!fromSign,toIndex,toSign);
		} else if(toKind == K_NOT) {
			int toChild = toState.children[0];
			return intersection(fromIndex,fromSign,toChild,!toSign);			
		}
		
		// using invert helps reduce the number of cases to consider.
		fromKind = invert(fromKind,fromSign);
		toKind = invert(toKind,toSign);		
				
		if(fromKind == K_VOID || toKind == K_VOID){
			return false;
		} else if(fromKind == K_ANY || toKind == K_ANY){
			return true;
		} else if(fromKind == K_UNION) {
			int[] fromChildren = fromState.children;		
			for(int i : fromChildren) {				
				if(intersection(i,true,toIndex,toSign)) {
					return true;
				}								
			}
			return false;	
		} else if(toKind == K_UNION) {
			int[] toChildren = toState.children;		
			for(int j : toChildren) {
				if(intersection(fromIndex,fromSign,j,true)) {
					return true;
				}											
			}
			return false;	
		} else if(fromKind == K_INTERSECTION) {
			int[] fromChildren = fromState.children;
			for (int i : fromChildren) {
				if(!intersection(i,true,toIndex,toSign)) {
					return false;
				}				
			}
			return true;	
		} else if(toKind == K_INTERSECTION) {
			int[] toChildren = toState.children;
			for (int j : toChildren) {
				if(intersection(fromIndex,fromSign,j,true)) {
					return false;
				}
			}
			return true;	
		}  
		
		return !fromSign || !toSign;		
	}
	
	private int invert(int kind, boolean sign) {
		if(sign) {
			return kind;
		}
		switch(kind) {
			case K_ANY:
				return K_VOID;
			case K_VOID:
				return K_ANY;
			case K_UNION:
				return K_INTERSECTION;
			case K_INTERSECTION:
				return K_UNION;
			default:
				return kind;
		}		
	}
	
	private boolean intersection(int fromIndex, boolean fromSign, int toIndex, boolean toSign) {
		if(fromSign) {
			fromIndex += from.size();
		}
		if(toSign) {
			toIndex += to.size();
		}
		
		return intersections.get(fromIndex,toIndex);
	}
		
	private void setIntersection(int fromIndex, boolean fromSign,
			int toIndex, boolean toSign, boolean value) {
		if(fromSign) {
			fromIndex += from.size();
		}
		if(toSign) {
			toIndex += to.size();
		}
		
		intersections.set(fromIndex,toIndex,value);
	}
}
