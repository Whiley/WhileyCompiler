package wyautl.lang;

import java.util.Arrays;

/**
 * <p>
 * The default interpretation provides a standard (i.e. simplistic)
 * interpretation of automatas. It is useful for testing simple kinds of
 * automata.
 * </p>
 * <p>
 * In the default interpretation, a value is a tree where each node has a kind
 * and zero or more children. A value is accepted by a (deterministic) state if
 * it has the same kind, and every child value is accepted by the corresponding
 * child state. For non-deterministic states, we require that every child value
 * is accepted by some child state.
 * </p>
 * <p>
 * <b>NOTE:</b> in the default interpretation, supplementary data is ignored.
 * </p>
 * 
 * @author djp
 * 
 */
public class DefaultInterpretation implements Interpretation<DefaultInterpretation.Value> {
			
	public static final class Value {
		public final int kind;
		public final Value[] children;

		public Value(int kind, Value... children) {
			this.kind = kind;
			this.children = children;			
		}

		public boolean equals(Object o) {
			if (o instanceof Value) {
				Value dv = (Value) o;
				return kind == dv.kind && Arrays.equals(children, dv.children);
			}
			return false;
		}

		public int hashCode() {
			return kind + Arrays.hashCode(children);
		}

		public String toString() {
			String middle = "";
			boolean firstTime=true;
			for(Value child : children) {
				if(!firstTime) {
					middle = middle + ",";
				}
				firstTime=false;
				middle = middle + child;
			}
			return kind + "(" + middle + ")";
		}
	}
	
	/**
	 * Construct a value from an automata. Will throw an
	 * IllegalArgumentException if the value is not concrete.
	 * 
	 * @param automata
	 * @return
	 */
	public static Value construct(Automata automata) {
		if(!Automatas.isConcrete(automata)) {
			throw new IllegalArgumentException("Cannot construct value from non-concrete automata");
		}
		return construct(0,automata);
	}
	
	private static Value construct(int index, Automata automata) {
		Automata.State state = automata.states[index];
		Value[] children = new Value[state.children.length];
		int i = 0;
		for(int c : state.children) {			
			children[i++] = construct(c,automata);
		}
		return new Value(state.kind,children);
	}
		
	public boolean accepts(Automata automata, Value value) {
		return accepts(0,automata,value);
	}
	
	public boolean accepts(int index, Automata automata, Value value) {		
		Automata.State state = automata.states[index];
		if(state.kind == value.kind) {		
			if(state.deterministic) {
				int[] schildren = state.children;
				Value[] vchildren = value.children;
				if(schildren.length != vchildren.length) {
					return false;
				}
				int length = schildren.length;
				for(int i=0;i!=length;++i) {
					int schild = schildren[i];					
					Value vchild = vchildren[i];
					if(!accepts(schild,automata,vchild)) {
						return false;
					}
				}
				return true;
			} else {
				// non-deterministic case
				int[] schildren = state.children;
				Value[] vchildren = value.children;
				
				if (vchildren.length == 0 && schildren.length > 0) {
					return false;
				}
				
				for(int i=0;i!=vchildren.length;++i) {
					Value vchild = vchildren[i];
					boolean matched = false;
					for(int j=0;j!=schildren.length;++j) {
						int schild = schildren[j];							
						if(accepts(schild,automata,vchild)) {
							matched = true;
							break;
						}
					}
					if(!matched) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
}
