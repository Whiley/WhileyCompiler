package wyil.util.type;

import static wyil.lang.Type.K_ANY;
import static wyil.lang.Type.K_FUNCTION;
import static wyil.lang.Type.K_HEADLESS;
import static wyil.lang.Type.K_METHOD;
import static wyil.lang.Type.K_RECORD;
import static wyil.lang.Type.K_UNION;
import static wyil.lang.Type.K_VOID;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

import wyautl.lang.*;
import wyautl.lang.Automata.State;
import wyil.lang.Type;

/**
 * <p>
 * This simplification rule converts a type into <i>conjunctive normal form</i>.
 * This is achieved by repeated application of various rewrite rules. The
 * following provides representatives of the main rules considered.
 * </p>
 * <ul>
 * <li><code>{void f}</code> => <code>void</code>.</li>
 * <li><code>T | void</code> => <code>T</code>.</li>
 * <li><code>T | any</code> => <code>any</code>.</li>
 * <li><code>T & void</code> => <code>void</code>.</li>
 * <li><code>T & any</code> => <code>T</code>.</li>
 * <li><code>X<T | X></code> => <code>T</code>.</li>
 * <li><code>X<T & X></code> => <code>void</code>.</li>
 * <li><code>!!T</code> => <code>T</code>.</li>
 * <li><code>!any</code> => <code>void</code>.</li>
 * <li><code>!void</code> => <code>any</code>.</li>
 * <li><code>!(T_1 | T_2)</code> => <code>!T_1 & !T_2</code>.</li>
 * <li><code>!(T_1 & T_2)</code> => <code>!T_1 | !T_2</code>.</li>
 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
 * <li><code>(T_1 & T_2) & T_3</code> => <code>(T_1 & T_2 & T_3)</code>.</li>
 * <li><code>T_1 & (T_2|T_3)</code> => <code>(T_1 & T_2) | (T_1 & T_3)</code>.</li>
 * <li><code>[T_1] & [T_2] & T_3</code> => <code>[T_1 & T_2] & T_3)</code>.</li>
 * <li><code>![T_1] & ![T_2] & T_3</code> => <code>![T_1 | T_2] & T_3)</code>.</li>
 * <li><code>[T_1] & {T_2}</code> => <code>void</code>.</li>
 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> => <code>T_1</code>.
 * <li><code>T_1 & T_2</code> where <code>T_1 :> T_2</code> => <code>T_2</code>.
 * <li><code>T_1 & T_2</code> where <code>T_1 n T_2 = 0</code> =>
 * <code>void</code>.
 * </ul>
 * <p>
 * <b>NOTE:</b> applications of this rewrite rule may leave states which are
 * unreachable from the root. Therefore, the resulting automata should be
 * extracted after rewriting to eliminate such states.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class TypeSimplifications implements RewriteRule {
	private SubtypeOperator subtypes;
	
	public final boolean apply(int index, Automata automata) {		
		Automata.State state = automata.states[index];
		boolean changed=false;
		switch (state.kind) {
			case Type.K_UNION :
				changed = applyUnion(index, state, automata);
				break;
			case Type.K_DICTIONARY:
			case Type.K_RECORD:
			case Type.K_TUPLE:
			case Type.K_FUNCTION:
			case Type.K_METHOD:
			case Type.K_HEADLESS:
				changed = applyCompound(index, state, automata);
				break;
		}
		if(changed) { 		
			// invalidate subtype cache
			subtypes = null;			
		}
		return changed;
	}

	public boolean applyCompound(int index, Automata.State state, Automata automata) {
		int kind = state.kind;
		int[] children = state.children;
		for(int i=0;i<children.length;++i) {
			if (i == 0 && (kind == Type.K_HEADLESS || kind == Type.K_FUNCTION)) {
				// headless method and function return type allowed to be void
				continue;				
			} else if(i == 1 && kind == Type.K_METHOD) {
				// method return type allowed to be void
				continue;
			}
			Automata.State child = automata.states[children[i]];
			if(child.kind == Type.K_VOID) {
				automata.states[index] = new Automata.State(Type.K_VOID);				
				return true;
			}			
		}
		return false;
	}
		
	/**
	 * This method is responsible for the following rewrite rules:
	 * <ul>
	 * <li><code>T | void</code> => <code>T</code>.</li>
	 * <li><code>T | any</code> => <code>any</code>.</li>
	 * <li><code>X<T | X></code> => <code>T</code>.</li>
	 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
	 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> => <code>T_1</code>.
	 * </ul>
	 * 
	 * @param index
	 *            --- index of state being worked on.
	 * @param state
	 *            --- state being worked on.
	 * @param automata
	 *            --- automata containing state being worked on.
	 * @return
	 */
	public boolean applyUnion(int index, Automata.State state,
			Automata automata) {
		return applyUnion_1(index, state, automata)
				|| applyUnion_2(index, state, automata);
	}
	
	/**
	 * This method applies the following rewrite rules:
	 * <ul>
	 * <li><code>T | void</code> => <code>T</code>.</li>
	 * <li><code>T | any</code> => <code>any</code>.</li>
	 * <li><code>X<T | X></code> => <code>T</code>.</li>
	 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
	 * </ul>
	 * 
	 * @param index
	 *            --- index of state being worked on.
	 * @param state
	 *            --- state being worked on.
	 * @param automata
	 *            --- automata containing state being worked on.
	 * @return
	 */
	private boolean applyUnion_1(int index, Automata.State state,
			Automata automata) {
		int[] children = state.children;
		boolean changed = false;
		for (int i = 0; i < children.length; ++i) {
			int iChild = children[i];
			if (iChild == index) {
				// contractive case
				children = removeIndex(i, children);
				state.children = children;
				changed = true;
			} else {
				Automata.State child = automata.states[iChild];
				switch (child.kind) {
					case Type.K_ANY :
						automata.states[index] = new Automata.State(Type.K_ANY);
						return true;
					case Type.K_VOID : {
						children = removeIndex(i, children);
						state.children = children;
						changed = true;
					}
					case Type.K_UNION :
						return flattenChildren(index, state, automata);
				}
			}
		}
		if (children.length == 0) {
			// this can happen in the case of a union which has only itself as a
			// child.
			automata.states[index] = new Automata.State(Type.K_VOID);
			changed = true;
		} else if (children.length == 1) {
			// bypass this node altogether
			int child = children[0];
			automata.states[index] = new Automata.State(automata.states[child]);
			changed = true;
		}
		return changed;
	}

	/**
	 * This method applies the following rewrite rules:
	 * <ul>
	 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> => <code>T_1</code>.
	 * </ul>
	 * 
	 * @param index
	 *            --- index of state being worked on.
	 * @param state
	 *            --- state being worked on.
	 * @param automata
	 *            --- automata containing state being worked on.
	 * @return
	 */
	private boolean applyUnion_2(int index, Automata.State state,
			Automata automata) {
		boolean changed = false;
		int[] children = state.children;

		for (int i = 0; i < children.length; ++i) {
			int iChild = children[i];
			// check whether this child is subsumed
			boolean subsumed = false;
			for (int j = 0; j < children.length; ++j) {
				int jChild = children[j];
				if (i != j && isSubtype(jChild, iChild, automata)
						&& (!isSubtype(iChild, jChild, automata) || i > j)) {
					subsumed = true;
				}
			}
			if (subsumed) {
				children = removeIndex(i--, children);
				state.children = children;
				changed = true;
			}
		}

		if (children.length == 1) {
			// bypass this node altogether
			int child = children[0];
			automata.states[index] = new Automata.State(automata.states[child]);
			changed = true;
		}
		
		return changed;
	}
	
	private final static class IntersectionPoint {
		public final int fromIndex;
		public final boolean fromSign;
		public final int toIndex;
		public final boolean toSign;
		
		public IntersectionPoint(int fromIndex, boolean fromSign, int toIndex, boolean toSign) {
			this.fromIndex = fromIndex;
			this.fromSign = fromSign;
			this.toIndex = toIndex;
			this.toSign = toSign;
		}
		
		public boolean equals(Object o) {
			if(o instanceof IntersectionPoint) {
				IntersectionPoint ip = (IntersectionPoint) o;
				return fromIndex == ip.fromIndex && fromSign == ip.fromSign
						&& toIndex == ip.toIndex && toSign == ip.toSign;
			}
			return false;
		}
		
		public int hashCode() {
			return fromIndex + toIndex;
		}
	}
	
	public static Type intersect(Type t1, Type t2) {
		Automata a1 = Type.destruct(t1);
		Automata a2 = Type.destruct(t2);
		HashMap<IntersectionPoint,Integer> allocations = new HashMap();		
		ArrayList<Automata.State> nstates = new ArrayList();
		intersect(0,true,a1,0,true,a2,allocations,nstates);
		System.out.println("GOT STATES: " + nstates);
		Automata automata = new Automata(nstates.toArray(new Automata.State[nstates.size()]));
		return Type.construct(automata);
	}
	
	private static int intersect(int fromIndex, boolean fromSign,
			Automata from, int toIndex, boolean toSign, Automata to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automata.State> states) {
		// first, check whether we have determined this state already
		IntersectionPoint ip = new IntersectionPoint(fromIndex,fromSign,toIndex,toSign);
		Integer allocation = allocations.get(ip);
		if(allocation != null) { return allocation;}
		
		// looks like we haven't, so proceed to determine the new state.
		int myIndex = states.size();
		allocations.put(ip,myIndex);
		states.add(null); // allocate space for me
		
		Automata.State fromState = from.states[fromIndex];
		Automata.State toState = to.states[toIndex];		
		Automata.State myState = null;
		
		if(fromState.kind == toState.kind) {
			switch(fromState.kind) {
			case Type.K_VOID: {
				if(!fromSign && !toSign) {
					myState = new Automata.State(Type.K_ANY);
				} else {
					myState = new Automata.State(Type.K_VOID);
				}
				break;
			}
			case Type.K_ANY: {
				if(fromSign && toSign) {
					myState = new Automata.State(Type.K_ANY);
				} else {
					myState = new Automata.State(Type.K_VOID);
				}
				break;
			}
				
			case Type.K_LABEL:
			case Type.K_EXISTENTIAL:
			
			case Type.K_LIST:
			case Type.K_SET: {
				int fromChild = fromState.children[0];
				int toChild = toState.children[0];
				// != below not ||. This is because lists and sets can intersect
				// on the empty list/set.
				if (!fromSign && !toSign) {
					// e.g. ![int] & ![real] => !([int],[real])
					int nFromChild = states.size();
					int nToChild = states.size() + 1;
					states.add(new Automata.State(fromState.kind));
					states.add(new Automata.State(toState.kind));
					myState = new Automata.State(Type.K_NEGATION, nFromChild,
							nToChild);
				} else {
					// e.g. [T1] & [T2] => [T1&T2]
					int childIndex = intersect(fromChild, fromSign, from,
							toChild, toSign, to, allocations, states);
					myState = new Automata.State(fromState.kind, childIndex);
				}
			}
			break;

			case Type.K_DICTIONARY:
			case Type.K_TUPLE:
			case Type.K_PROCESS:
				
			case Type.K_RECORD:
			case Type.K_NEGATION:
				
			case Type.K_UNION:
				
			case Type.K_FUNCTION:
			case Type.K_HEADLESS:
			case Type.K_METHOD:
				
			default: {
				// K_BYTE, K_CHAR, K_INT, K_RATIONAL
				// K_STRING, K_NULL
			
				if(fromSign && toSign) {
					// e.g. INT & INT => INT
					myState = new Automata.State(fromState.kind);
				} else if(fromSign != toSign) {
					// e.g. !INT & INT => INT
					myState = new Automata.State(Type.K_VOID);
				} else {
					// e.g. !INT & !INT => !INT
					int childIndex = states.size();
					states.add(new Automata.State(fromState.kind));
					myState = new Automata.State(Type.K_NEGATION,childIndex);
				}
				break;
			}
				
			}
		} else {
			// using invert helps reduce the number of cases to consider.
			int fromKind = invert(fromState.kind,fromSign);
			int toKind = invert(toState.kind,toSign);
			
			if(fromKind == K_VOID || toKind == K_VOID) {
				myState = new Automata.State(Type.K_VOID);
			} else if(fromKind == K_ANY) {				
				states.remove(states.size()-1);
				if(!toSign) {
					states.add(new Automata.State(Type.K_NEGATION));
				}
				Automatas.extractOnto(toIndex,to,states);
				return myIndex;
			} else if(toKind == K_ANY) {
				states.remove(states.size()-1);
				if(!fromSign) {
					states.add(new Automata.State(Type.K_NEGATION));
				}
				Automatas.extractOnto(fromIndex,from,states);
				return myIndex;
			} else if(fromSign && toSign) {				
				myState = new Automata.State(Type.K_VOID);
			} else if(fromSign) {
				states.remove(states.size()-1);
				Automatas.extractOnto(fromIndex,from,states);
				return myIndex;
			} else if(toSign) {
				states.remove(states.size()-1);
				Automatas.extractOnto(toIndex,to,states);
				return myIndex;
			} else {
				int nFromChild = states.size();
				int nToChild = states.size() + 1;
				states.add(new Automata.State(fromState.kind));
				states.add(new Automata.State(toState.kind));
				myState = new Automata.State(Type.K_NEGATION, nFromChild,
						nToChild);
			}
		}

		states.set(myIndex, myState);
		
		return myIndex;				
	}
	
	private static int invert(int kind, boolean sign) {
		if(sign) {
			return kind;
		}
		switch(kind) {
			case K_ANY:
				return K_VOID;
			case K_VOID:
				return K_ANY;
			default:
				return kind;
		}		
	}
	
	private static int[] removeIndex(int index, int[] children) {
		int[] nchildren = new int[children.length - 1];
		for (int j = 0; j < children.length; ++j) {
			if (j < index) {
				nchildren[j] = children[j];
			} else if (j > index) {
				nchildren[j - 1] = children[j];
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
	private static boolean flattenChildren(int index, Automata.State state,
			Automata automata) {
		ArrayList<Integer> nchildren = new ArrayList<Integer>();
		int[] children = state.children;
		final int kind = state.kind;

		for (int i = 0; i < children.length; ++i) {
			int iChild = children[i];
			Automata.State child = automata.states[iChild];
			if (child.kind == kind) {
				for (int c : child.children) {
					nchildren.add(c);
				}
			} else {
				nchildren.add(iChild);
			}
		}

		children = new int[nchildren.size()];
		for (int i = 0; i < children.length; ++i) {
			children[i] = nchildren.get(i);
		}

		automata.states[index] = new Automata.State(kind, children, false);

		return true;
	}

	/**
	 * The aim of this method is to split up the positive and negative children
	 * of a given state. A child is negative if it is a negated type; otherwise
	 * it is positive. This method orders the children such so all the positive
	 * ones come first, then the negative ones. The value returned indicates the
	 * lowest index of a negative child (i.e. the point where the negative
	 * children start). Thus, if the return value matches
	 * <code>state.children</code>, then all children are positive. Likewise, if
	 * the return value is <code>0</code>, then all children are negative.
	 * 
	 * @param state
	 *            --- automata state whose children are to be sorted.
	 * @return --- the start index of the negative children.
	 */
	private static int splitPositiveNegativeChildren(Automata.State state,
			Automata automata) {
		int[] children = state.children;
		int posIndex = advancePositive(0, children, automata);
		int negIndex = retreatNegative(children.length - 1, children, automata);

		while (posIndex < negIndex) {
			int tmp = children[posIndex];
			children[posIndex] = children[negIndex];
			children[negIndex] = tmp;
			posIndex = advancePositive(posIndex + 1, children, automata);
			negIndex = retreatNegative(negIndex - 1, children, automata);
		}

		return posIndex;
	}

	private static int advancePositive(int index, int[] children,
			Automata automata) {
		while (index < children.length
				&& automata.states[children[index]].kind != Type.K_NEGATION) {
			index = index + 1;
		}
		return index;
	}

	private static int retreatNegative(int index, int[] children,
			Automata automata) {
		while (index > 0
				&& automata.states[children[index]].kind == Type.K_NEGATION) {
			index = index - 1;
		}
		return index;
	}

	private boolean isSubtype(int fromIndex, int toIndex, Automata automata) {
		if(subtypes == null) {
			subtypes = new SubtypeOperator(automata,automata);
		}
		return subtypes.isSubtype(fromIndex, toIndex);
	}
}
