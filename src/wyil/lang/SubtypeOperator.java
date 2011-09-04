package wyil.lang;

import static wyil.lang.Type.*;
import wyautl.lang.*;

public class SubtypeOperator implements Relation {
	protected final Automata from;
	protected final Automata to;
	private final BinaryMatrix subtypes; // from :> to
	private final BinaryMatrix suptypes; // to :> from
	
	public SubtypeOperator(Automata from, Automata to) {
		this.from = from;
		this.to = to;
		this.subtypes = new BinaryMatrix(from.size(),to.size(),true);
		this.suptypes = new BinaryMatrix(to.size(),from.size(),true);
	}
	
	public final Automata from() {
		return from;
	}
	
	public final Automata to() {
		return to;
	}
	
	public final boolean update(int fromIndex, int toIndex) {
		boolean oldSubtype = subtypes.get(fromIndex,toIndex);
		boolean oldSuptype = suptypes.get(toIndex,fromIndex);
		boolean newSubtype = isSubtype(fromIndex,toIndex,subtypes,suptypes);
		boolean newSuptype = isSubtype(toIndex,fromIndex,suptypes,subtypes);		
		subtypes.set(fromIndex,toIndex,newSubtype);
		suptypes.set(toIndex,fromIndex,newSubtype);
		return newSubtype != oldSubtype || newSuptype != oldSuptype;
	}
	
	public final boolean isRelated(int fromIndex, int toIndex) {
		return subtypes.get(fromIndex,toIndex);
	}
	
	// check if to is a subtype of from
	public boolean isSubtype(int fromIndex, int toIndex, BinaryMatrix subtypes,
			BinaryMatrix suptypes) {
		Automata.State fromState = from.states[fromIndex];
		Automata.State toState = to.states[toIndex];
		int fromKind = fromState.kind;
		int toKind = toState.kind;
		
		if(fromKind == toKind) {
			switch(fromKind) {
			// === Leaf States First ===
			case K_EXISTENTIAL:
				NameID nid1 = (NameID) fromState.data;
				NameID nid2 = (NameID) toState.data;				
				return nid1.equals(nid2);
			
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
					if (!subtypes.get(fromChildren[i], toChildren[i])) {
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
					if(!subtypes.get(fromChild,toChild)) {
						return false;
					}					
				}									
				return true;	
			}
			case K_UNION: {								
				int[] toChildren = (int[]) toState.children;		
				for(int j : toChildren) {				
					if(!subtypes.get(fromIndex,j)) { return false; }								
				}
				return true;								
			}			
			// === Heterogenous Compound States ===
			case K_FUNCTION:				
			case K_METHOD:
				// nary nodes
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				if(fromChildren.length != toChildren.length){
					return false;
				}
				int start = 0;
				if(fromKind == K_METHOD) {
					// Check (optional) receiver value first (which is contravariant)
					if (!suptypes.get(toChildren[0],fromChildren[0])) {
						return false;
					}
					start++;
				}
				// Check return value first (which is covariant)
				int fromChild = fromChildren[start];
				int toChild = toChildren[start];
				if(!subtypes.get(fromChild,toChild)) {
					return false;
				}
				// Now, check parameters (which are contra-variant)
				for(int i=start+1;i<fromChildren.length;++i) {
					if(!suptypes.get(toChildren[i],fromChildren[i])) {
						return false;
					}
				}
				return true;
			default:
				// other primitive types (e.g. void, any, null, int, etc)
				return true;
			}
		} else if(fromKind == K_ANY || toKind == K_VOID){
			return true;
		} else if(fromKind == K_UNION) {
			int[] fromChildren = fromState.children;		
			for(int i : fromChildren) {				
				if(subtypes.get(i,toIndex)) {
					return true;
				}								
			}
			return false;	
		} else if(toKind == K_UNION) {
			int[] toChildren = toState.children;		
			for(int j : toChildren) {				
				if(!subtypes.get(fromIndex,j)) {
					return false;
				}								
			}
			return true;	
		}
		
		return false;
	}	
}
