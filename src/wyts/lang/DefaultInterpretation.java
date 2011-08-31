package wyts.lang;

import java.util.Arrays;

/**
 * <p>
 * The default interpretation provides a standard (i.e. simplistic)
 * interpretation of automatas. It is useful for testing simple kinds of
 * automata.
 * </p>
 * <p>
 * In the default interpretation, a value is a node with a kind, and zero or
 * more children. A value is accepted by a (sequential) state if it has the same
 * kind, and every child value is accepted by the corresponding child state. For
 * non-sequential states, we require that every child value is accepted by some
 * child state.
 * </p>
 * <p>
 * <b>NOTE:</b> in the default interpretation, supplementary data is ignored.
 * </p>
 * 
 * @author djp
 * 
 */
public final class DefaultInterpretation implements Interpretation<DefaultInterpretation.Value> {
			
	public static final class Value {
		public final int kind;
		private final Value[] children;

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
			return kind + "(" + Arrays.toString(children) + ")";
		}
	}
	
	public boolean accepts(Automata automata, Value value) {
		return accepts(0,automata,value);
	}
	
	public boolean accepts(int index, Automata automata, Value value) {
		Automata.State state = automata.states[index];
		if(state.kind == value.kind) {
			int kind = state.kind;
			if((kind & Automata.NONSEQUENTIAL)==0) {
				// sequential case
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
				// non-sequential case
				int[] schildren = state.children;
				Value[] vchildren = value.children;
								
				for(int i=0;i!=vchildren.length;++i) {
					Value vchild = vchildren[i];
					boolean matched = false;
					for(int j=0;j!=schildren.length;++i) {
						int schild = schildren[i];					
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
