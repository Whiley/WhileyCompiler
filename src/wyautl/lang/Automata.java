package wyautl.lang;

import java.util.*;

/**
 * <p>
 * An Automata represents a finite-state automata. This is a machine for
 * accepting matching inputs of a given language. An automata is a directed
 * graph whose nodes and edges are referred to as <i>states</i> and
 * <i>transitions</i>. Each state has a "kind" which determines how the state
 * behaves on given inputs. For example, a state with "OR" kind might accept an
 * input if either of its children does; in contrast, and state of "AND" kind
 * might accept an input only if all its children does.
 * </p>
 * 
 * <p>
 * The organisation of children is done according to two approaches:
 * <i>sequential</i> and <i>non-sequential</i>. In the sequential approach, the
 * ordering of children is important; in the non-sequential approach, the
 * ordering of children is not important. Note, the latter corresponds to a
 * multi-set rather than an ordinary set. To indicate a kind is non-sequential,
 * its value should include the <code>NONSEQUENTIAL</code> constant (as a flag).
 * </p>
 * 
 * <p>
 * Aside from having a particular kind, each state may also have supplementary
 * material. This can be used, for example, to effectively provide labelled
 * transitions. Another use of this might be to store a given string which must
 * be matched.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> In the internal representation of automata, leaf states may be
 * not be represented as actual nodes. This will occur if the leaf node does not
 * include any supplementary data, and is primarily for space and performance
 * optimisation. In such case, the node is represented as a child node using a
 * negative index.
 * </p>
 * 
 * @author djp
 * 
 */
public final class Automata {	
	public final State[] states;	
	public static final int NONSEQUENTIAL = (1 << 31); 
	
	public Automata(State... states) {
		this.states = states;
	}

	public int size() {
		return states.length;
	}
	
	/**
	 * Determine the hashCode of a type.
	 */
	public int hashCode() {
		int r = 0;
		for(State c : states) {
			r = r + c.hashCode();
		}
		return r;
	}
	
	/**
	 * This method compares two compound types to test whether they are
	 * <i>identical</i>. Observe that it does not perform an
	 * <i>isomorphism</i> test. Thus, two distinct types which are
	 * structurally isomorphic will <b>not</b> be considered equal under
	 * this method. <b>NOTE:</b> to test whether two types are structurally
	 * isomorphic, using the <code>isomorphic(t1,t2)</code> method.
	 */
	public boolean equals(Object o) {
		if(o instanceof Automata) {
			State[] cs = ((Automata) o).states;
			if(cs.length != states.length) {
				return false;
			}
			for(int i=0;i!=cs.length;++i) {
				if(!states[i].equals(cs[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public String toString() {
		String r = "";
		for(int i=0;i!=states.length;++i) {
			State n = states[i];
			int[] children = n.children;
			r = r + "#" + i + " : ";
			boolean firstTime=true;
			for(int j=0;j!=children.length;++j) {
				if(!firstTime) {
					r = r + ", ";
				}
				firstTime=false;
				r = r + j;
			}
			if(n.data != null) {
				r = r + " : " + n.data;
			}
			r = r + "\n";
		}
		return r;
	}

	/**
	 * Represents a state in an automata. Each state has a kind, along with zero
	 * or more children and an (optional) supplementary data item.
	 * 
	 * @author djp
	 * 
	 */
	public static final class State {
		public final int kind;
		public final int[] children;
		public final Object data;

		/**
		 * Construct a state with no children and no supplementary data.
		 * 
		 * @param kind
		 *            --- State kind (must be positive integer).
		 */
		public State(int kind) {
			this(kind,NOCHILDREN,null);
		}
		
		/**
		 * Construct a state with no supplementary data.
		 * 
		 * @param kind
		 *            --- State kind (must be positive integer).
		 * @param children
		 *            --- Array of child indices.
		 */
		public State(int kind, int[] children) {
			this(kind,children,null);
		}

		/**
		 * Construct a state with children and supplementary data.
		 * 
		 * @param kind
		 *            --- State kind (must be positive integer).
		 * @param children
		 *            --- Array of child indices.
		 * @param data
		 *            --- Supplementary data store with state.
		 */
		public State(int kind, int[] children, Object data) {
			this.kind = kind;
			this.children = children;
			this.data = data;
		}
		
		public boolean equals(final Object o) {
			if (o instanceof State) {
				State c = (State) o;
				// in the following, we only need to check data != null for this
				// node as both nodes have the same kind and, hence, this.data
				// != null implies c.data != null.
				return kind == c.kind && Arrays.equals(children, c.children)
						&& (data == null || data.equals(c.data));
			}
			return false;
		}

		public int hashCode() {
			int r = Arrays.hashCode(children) + kind;
			if (data == null) {
				return r + data.hashCode();
			} else {
				return r;
			}
		}
	}

	/**
	 * The following constant is used simply to prevent unnecessary memory
	 * allocations.
	 */
	private static final int[] NOCHILDREN = new int[0];

}
