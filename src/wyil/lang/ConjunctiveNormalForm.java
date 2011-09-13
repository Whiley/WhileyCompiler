package wyil.lang;

import java.util.ArrayList;

import wyautl.lang.*;

/**
 * <p>
 * This simplification rule converts a type into <i>conjunctive normal form</i>.
 * This is achieved by repeated application of the following rewrites:
 * </p>
 * <ol>
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
 * <li><code>T_1 & T_2</code> where <code>T_1 n T_2 = 0</code> => <code>void</code>.
 * 
 * </ol>
 * <p>
 * <b>NOTE:</b> applications of this rewrite rule may leave states which are
 * unreachable from the root. Therefore, the resulting automata should be
 * extracted after rewriting to eliminate any such states.
 * </p>
 * 
 * @author djp
 * 
 */
public final class ConjunctiveNormalForm implements RewriteRule {
	private IntersectionOperator subtypes;
	
	public ConjunctiveNormalForm(Automata automata) {
		updateSubtypes(automata);
	}
	
	public final boolean apply(int index, Automata automata) {
		Automata.State state = automata.states[index];
		boolean changed=false;
		switch (state.kind) {
			case Type.K_UNION :
				changed = applyUnion(index, state, automata);
				break;
			case Type.K_INTERSECTION :
				changed = applyIntersection(index, state, automata);
				break;
			case Type.K_NEGATION :
				changed = applyNot(index, state, automata);
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
			updateSubtypes(automata); 
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
	
	public boolean applyNot(int index, Automata.State state, Automata automata) {
		int childIndex = state.children[0];
		Automata.State child = automata.states[childIndex];
		switch (child.kind) {
			case Type.K_ANY :
				automata.states[index] = new Automata.State(Type.K_VOID);
				return true;
			case Type.K_VOID :
				automata.states[index] = new Automata.State(Type.K_ANY);
				return true;
			case Type.K_NEGATION :
				// bypass this node altogether
				int childChildIndex = child.children[0];
				Automata.State childChild = automata.states[childChildIndex];
				automata.states[index] = new Automata.State(childChild);
				return true;
			case Type.K_UNION :
			case Type.K_INTERSECTION : {
				int[] child_children = child.children;
				int[] nchildren = new int[child_children.length];
				Automata.State[] nstates = new Automata.State[child_children.length];
				for (int i = 0; i < child_children.length; ++i) {
					int[] children = new int[1];
					children[0] = child_children[i];
					nchildren[i] = i + automata.size();
					nstates[i] = new Automata.State(Type.K_NEGATION, children);
				}
				Automatas.inplaceAppendAll(automata, nstates);
				state = automata.states[index];
				int nkind = child.kind == Type.K_UNION
						? Type.K_INTERSECTION
						: Type.K_UNION;
				state.kind = nkind;
				state.children = nchildren;
				state.deterministic = false;

				return true;
			}
			default :
				return false;
		}
	}

	/**
	 * This method is responsible for the following rewrite rules:
	 * <ol>
	 * <li><code>T & void</code> => <code>void</code>.</li>
	 * <li><code>T & any</code> => <code>T</code>.</li>
	 * <li><code>X<T & X></code> => <code>void</code>.</li>
	 * <li><code>(T_1 & T_2) & T_3</code> => <code>(T_1 & T_2 & T_3)</code>.</li>
	 * <li><code>T_1 & (T_2|T_3)</code> =>
	 * <code>(T_1 & T_2) | (T_1 & T_3)</code>.</li>
	 * <li><code>[T_1] & [T_2] & T_3</code> => <code>[T_1 & T_2] & T_3)</code>.</li>
	 * <li><code>![T_1] & ![T_2] & T_3</code> =>
	 * <code>![T_1 | T_2] & T_3)</code>.</li>
	 * <li><code>[T_1] & {T_2}</code> => <code>void</code>.</li>
	 * </ol>
	 * 
	 * @param index
	 *            --- index of state being worked on.
	 * @param state
	 *            --- state being worked on.
	 * @param automata
	 *            --- automata containing state being worked on.
	 * @return
	 */
	public boolean applyIntersection(int index, Automata.State state,
			Automata automata) {
		return applyIntersection_1(index, state, automata)
				|| applyIntersection_2(index, state, automata)
				|| applyIntersection_3(index, state, automata);
	}

	/**
	 * This method applies the following rewrite rules:
	 * <ol>
	 * <li><code>T & void</code> => <code>void</code>.</li>
	 * <li><code>T & any</code> => <code>T</code>.</li>
	 * <li><code>X<T & X></code> => <code>void</code>.</li>
	 * <li><code>(T_1 & T_2) & T_3</code> => <code>(T_1 & T_2 & T_3)</code>.</li>
	 * <li><code>T_1 & (T_2|T_3)</code> =>
	 * <code>(T_1 & T_2) | (T_1 & T_3)</code>.</li>
	 * </ol>
	 * 
	 * @param index
	 *            --- index of state being worked on.
	 * @param state
	 *            --- state being worked on.
	 * @param automata
	 *            --- automata containing state being worked on.
	 * @return
	 */
	private static boolean applyIntersection_1(int index, Automata.State state,
			Automata automata) {
		int[] children = state.children;
		boolean changed = false;
		for (int i = 0; i < children.length; ++i) {
			int iChild = children[i];
			if (iChild == index) {
				// X<T1 & X> => void
				automata.states[index] = new Automata.State(Type.K_VOID);
				return true;
			} else {
				Automata.State child = automata.states[iChild];
				switch (child.kind) {
					case Type.K_VOID :
						// T1 & void => void
						automata.states[index] = new Automata.State(Type.K_VOID);
						return true;
					case Type.K_ANY :
						// T1 & any => T1
						children = removeIndex(i, children);
						state.children = children;
						changed = true;
					case Type.K_INTERSECTION :
						// T1 & (T2 & T3) => T1 & T2 & T3
						return flattenChildren(index, state, automata);
					case Type.K_UNION : {
						// T1 & (T2 | T3) => (T1 & T2) | (T1 & T3)
						int[] child_children = child.children;
						int[] nchildren = new int[child_children.length];
						Automata.State[] nstates = new Automata.State[child_children.length];
						for (int j = 0; j < child_children.length; ++j) {
							int jChildIndex = child_children[j];
							int[] kchildren = new int[children.length];
							nchildren[j] = automata.size() + j;
							for (int k = 0; k < children.length; ++k) {
								if (k != i) {
									kchildren[k] = children[k];
								} else {
									kchildren[i] = jChildIndex;
								}
							}
							nstates[j] = new Automata.State(
									Type.K_INTERSECTION, kchildren);
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
		if (children.length == 1) {
			// bypass this node altogether
			int child = children[0];
			automata.states[index] = new Automata.State(automata.states[child]);
			changed = true;
		}
		return changed;
	}

	/**
	 * This method applies the following rewrite rules:
	 * <ol>
	 * <li><code>[T_1] & [T_2] & T_3</code> => <code>[T_1 & T_2] & T_3)</code>.</li>
	 * <li><code>![T_1] & ![T_2] & T_3</code> =>
	 * <code>![T_1 | T_2] & T_3)</code>.</li>
	 * <li><code>[T_1] & {T_2}</code> => <code>void</code>.</li>
	 * </ol>
	 * 
	 * @param index
	 *            --- index of state being worked on.
	 * @param state
	 *            --- state being worked on.
	 * @param automata
	 *            --- automata containing state being worked on.
	 * @return
	 */
	private static boolean applyIntersection_2(int index, Automata.State state,
			Automata automata) {
		int[] children = state.children;
		int pivot = splitPositiveNegativeChildren(state, automata);
		boolean changed = false;
		
		if (pivot > 1) {
			// collect up positive children
			int kind = -1;
			int nchildren = 0;
			Object data = null;
			for (int i = 0; i != pivot; ++i) {
				Automata.State child = automata.states[children[i]];
				if (i == 0) {
					// first time around
					kind = child.kind;
					nchildren = child.children.length;
					data = child.data;
				} else if (kind != child.kind
						|| nchildren != child.children.length
						|| (data == null && child.data != null)
						|| (data != null && !data.equals(child.data))) {
					// [T_1] & {T_2} & T_3 => void
					automata.states[index] = new Automata.State(Type.K_VOID);
					return true;
				}
			}

			// INVARIANT: all children have same kind, same number of their
			// children and same supplementary data.

			switch (kind) {
				case Type.K_PROCESS :
					throw new RuntimeException(
							"Need to deal with process and function types");
				case Type.K_SET :
				case Type.K_LIST :
				case Type.K_DICTIONARY :
				case Type.K_TUPLE :
				case Type.K_RECORD :
					// [T_1] & [T_2] & T3 => [T_1 & T_2] & T3
					collectPositiveChildren(kind, nchildren, data, pivot,
							state, automata);
					pivot = 1;
					children = state.children;
					changed = true;
			}			
		}
		
		// TODO: collect negative children [this is the harder case]
		
		if (children.length == 1) {
			// bypass this node altogether
			int child = children[0];
			automata.states[index] = new Automata.State(automata.states[child]);
			changed = true;
		}
		
		return changed;
	}

	/**
	 * This method applies the following rewrite rules:
	 * <ol>
	 * <li><code>T_1 & T_2</code> where <code>T_1 :> T_2</code> => <code>T_2</code>.
	 * <li><code>T_1 & T_2</code> where <code>T_1 n T_2 = 0</code> => <code>void</code>.
	 * </ol>
	 * 
	 * @param index
	 *            --- index of state being worked on.
	 * @param state
	 *            --- state being worked on.
	 * @param automata
	 *            --- automata containing state being worked on.
	 * @return
	 */
	private boolean applyIntersection_3(int index, Automata.State state,
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
				} else if(primitiveInverse(iChild,jChild,automata)) {
					automata.states[index] = new Automata.State(Type.K_VOID);
					return true;
				}
//				else if (!subtypes.isIntersection(iChild, jChild)) {
//					// no intersection is possible!
//					automata.states[index] = new Automata.State(Type.K_VOID);
//					return true;
//				}			
			}
			if(subsumed) {					
				changed = true;
				children = removeIndex(i--,children);
				state.children = children;
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
	
	private static boolean primitiveInverse(int fromIndex, int toIndex, Automata automata) {
		Automata.State from = automata.states[fromIndex];
		Automata.State to = automata.states[toIndex];
		if(from.kind == Type.K_NEGATION && isPrimitive(to.kind)) {
			return automata.states[from.children[0]].kind == to.kind;
		} else if(isPrimitive(from.kind) && to.kind == Type.K_NEGATION) {
			return automata.states[to.children[0]].kind == from.kind;
		}
		return false;
	}
	
	private static boolean isPrimitive(int kind) {
		return kind <= Type.K_STRING;
	}
	
	/**
	 * This method is responsible for the following rewrite rules:
	 * <ol>
	 * <li><code>T | void</code> => <code>T</code>.</li>
	 * <li><code>T | any</code> => <code>any</code>.</li>
	 * <li><code>X<T | X></code> => <code>T</code>.</li>
	 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
	 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> => <code>T_1</code>.
	 * </ol>
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
	 * <ol>
	 * <li><code>T | void</code> => <code>T</code>.</li>
	 * <li><code>T | any</code> => <code>any</code>.</li>
	 * <li><code>X<T | X></code> => <code>T</code>.</li>
	 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
	 * </ol>
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
	 * <ol>
	 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> => <code>T_1</code>.
	 * </ol>
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
				if (i != j && subtypes.isSubtype(jChild, iChild)
						&& (!subtypes.isSubtype(iChild, jChild) || i > j)) {
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

	/**
	 * PRECONDITION: Every child of the given state has the given kind, exactly
	 * the given number of children and the given data.
	 * 
	 * @param kind
	 *            --- every child of state has this kind.
	 * @param numChildren
	 *            --- every child of state has exactly this many children.
	 * @param data
	 *            --- every child of state has this supplementary data.
	 * @param numPositiveChildren
	 *            --- the number of positive children in the given state.
	 * @param state
	 *            --- the state being collected (aka the outermost state).
	 * @param automata
	 *            --- automata contained the state being collected.
	 * @return
	 */
	private static void collectPositiveChildren(int kind, int numChildren,
			Object data, int numPositiveChildren, Automata.State state, Automata automata) {
		Automata.State[] nstates = new Automata.State[numChildren + 1];
		int[] state_children = state.children;		
		int[] nchildren = new int[numChildren];		
		
		int start = automata.size();
		int next = start+1;
		for(int i=0;i!=numChildren;++i) {			
			nchildren[i] = next++;
			int[] childChildren = new int[numPositiveChildren];
			for(int j=0;j!=numPositiveChildren;++j) {
				Automata.State child = automata.states[state_children[j]];
				childChildren[j] = child.children[i];
			}
			nstates[i+1] = new Automata.State(Type.K_INTERSECTION,childChildren,false);
		}
		
		// now, add the newly created states to the automata.		
		nstates[0] = new Automata.State(kind,nchildren,true,data);
		Automatas.inplaceAppendAll(automata,nstates);
		
		// finally, update the outermost state,
		int[] nstate_children = new int[1+(state_children.length-numPositiveChildren)];
		nstate_children[0] = start;
		System.arraycopy(state_children, numPositiveChildren, nstate_children, 1, nstate_children.length-1);
		state.children = nstate_children;
	}
	
	private void updateSubtypes(Automata automata) {
		// this is horrendously inefficient
		subtypes = new IntersectionOperator(automata,automata);
		Automatas.computeFixpoint(subtypes);
	}
}
