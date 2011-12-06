// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil.util.type;

import static wyil.lang.Type.K_ANY;
import static wyil.lang.Type.K_UNION;
import static wyil.lang.Type.K_VOID;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;

import wyautl.lang.*;

import wyil.lang.NameID;
import wyil.lang.Type;


public final class TypeAlgorithms {

	/**
	 * The data comparator is used in the type canonicalisation process. It is
	 * used to compare the supplementary data of states in automatas
	 * representing types. Supplementary data is used for record kinds and 
	 */
	public static final Comparator<Automaton.State> DATA_COMPARATOR = new Comparator<Automaton.State>() {
		public int compare(Automaton.State s1, Automaton.State s2) {
			// PRE-CONDITION s1.kind == s2.kind
			if(s1.kind == Type.K_RECORD) {
				Type.Record.State fields1 = (Type.Record.State) s1.data;
				Type.Record.State fields2 = (Type.Record.State) s2.data;
				int fields1_size = fields1.size();
				int fields2_size = fields2.size();
				if (fields1_size < fields2_size
						|| (!fields1.isOpen && fields2.isOpen)) {
					return -1;
				} else if (fields1_size > fields2_size
						|| (fields1.isOpen && !fields2.isOpen)) {
					return 1;
				}
				// ASSERT: fields1_size == fields2_size
				for(int i=0;i!=fields1_size;++i) {
					String str1 = fields1.get(i);
					String str2 = fields2.get(i);
					int c = str1.compareTo(str2);
					if(c != 0) {
						return c;
					}
				}
				return 0;
			} else if(s1.kind == Type.K_NOMINAL){
				NameID nid1 = (NameID) s1.data;
				NameID nid2 = (NameID) s2.data;
				return nid1.toString().compareTo(nid2.toString());
			} else if(s1.kind == Type.K_LIST || s1.kind == Type.K_SET){
				Boolean nid1 = (Boolean) s1.data;
				Boolean nid2 = (Boolean) s2.data;				
				return nid1.toString().compareTo(nid2.toString());
			} else {
				String str1 = (String) s1.data;
				String str2 = (String) s2.data;
				return str1.compareTo(str2);
			}
		}
	};
	
	/**
	 * <p>
	 * Contractive types are types which cannot accept value because they have
	 * an <i>unterminated cycle</i>. An unterminated cycle has no leaf nodes
	 * terminating it. For example, <code>X<{X field}></code> is contractive,
	 * where as <code>X<{null|X field}></code> is not.
	 * </p>
	 * 
	 * <p>
	 * This method returns true if the type is contractive, or contains a
	 * contractive subcomponent. For example, <code>null|X<{X field}></code> is
	 * considered contracted.
	 * </p>
	 * 
	 * @param type --- type to test for contractivity.
	 * @return
	 */
	public static boolean isContractive(Automaton automata) {		
		BitSet contractives = new BitSet(automata.size());
		// TODO: optimise away the need to initialise the contractives
		contractives.set(0,automata.size(),true);
		// initially all nodes are considered contracive.
		return findContractives(automata,contractives);
	}
	
	private static boolean findContractives(Automaton automata, BitSet contractives) {		
		boolean changed = true;
		boolean contractive = false;
		while(changed) {
			changed=false;
			contractive = false;
			for(int i=0;i!=automata.size();++i) {
				boolean oldVal = contractives.get(i);
				boolean newVal = isContractive(i,contractives,automata);
				if(oldVal && !newVal) {
					contractives.set(i,newVal);
					changed = true;
				}
				contractive |= newVal;
			}
		}
		return contractive;
	}
	
	private static boolean isContractive(int index, BitSet contractives,
			Automaton automata) {
		Automaton.State state = automata.states[index];
		int[] children = state.children;
		if(children.length == 0) {
			return false;
		}
		if(state.deterministic) {
			for(int child : children) {
				if(child == index || contractives.get(child)) {
					return true;
				}
			}
			return false;
		} else {			
			boolean r = true;
			for(int child : children) {				
				if(child == index) { 
					return true;
				}
				r &= contractives.get(child);									
			}
			return r;
		}
	}
	
	/**
	 * <p>
	 * This simplification rule removes spurious components by repeated
	 * application of various rewrite rules. The following provides
	 * representatives of the main rules considered.
	 * </p>
	 * <ul>
	 * <li><code>{void f}</code> => <code>void</code>.</li>
	 * <li><code>T | void</code> => <code>T</code>.</li>
	 * <li><code>T | any</code> => <code>any</code>.</li>
	 * <li><code>X<T | X></code> => <code>T</code>.</li>
	 * <li><code>!!T</code> => <code>T</code>.</li>
	 * <li><code>!any</code> => <code>void</code>.</li>
	 * <li><code>!void</code> => <code>any</code>.</li>
	 * <li><code>!(T_1 | T_2)</code> => <code>!T_1 & !T_2</code>.</li>
	 * <li><code>(T_1 | T_2) | T_3</code> => <code>(T_1 | T_2 | T_3)</code>.</li>
	 * <li><code>T_1 | T_2</code> where <code>T_1 :> T_2</code> =>
	 * <code>T_1</code>.
	 * <code>void</code>.
	 * </ul>
	 * <p>
	 * <b>NOTE:</b> applications of this rewrite rule may leave states which are
	 * unreachable from the root. Therefore, the resulting automata should be
	 * extracted after rewriting to eliminate such states.
	 * </p>
	 * 
	 */
	public static void simplify(Automaton automata) {		
		boolean changed = true;
		while(changed) {				
			changed = false;			
			changed |= simplifyContractives(automata);						
			for(int i=0;i!=automata.size();++i) {				
				changed |= simplify(i,automata);				
			}			
		}	
	}	
	
	private static boolean simplifyContractives(Automaton automata) {
		BitSet contractives = new BitSet(automata.size());
		// initially all nodes are considered contractive.
		// TODO: optimise away the need to initialise the contractives
		contractives.set(0,automata.size(),true);
		boolean changed = findContractives(automata, contractives);

		for (int i = contractives.nextSetBit(0); i >= 0; i = contractives
				.nextSetBit(i + 1)) {
			automata.states[i] = new Automaton.State(Type.K_VOID);
		}

		return changed;
	}
	
	private static boolean simplify(int index, Automaton automata) {		
		Automaton.State state = automata.states[index];
		boolean changed=false;
		switch (state.kind) {
		case Type.K_NEGATION:			
			changed = simplifyNegation(index, state, automata);
			break;
		case Type.K_UNION :			
			changed = simplifyUnion(index, state, automata);			
			break;		
		case Type.K_LIST:
		case Type.K_SET:
			// for list and set types, we want to simplify the following cases:
			// [void+] => void
			// {void+} => void
			boolean nonEmpty = (Boolean) state.data;
			if(!nonEmpty) {
				// type of form [T], so ignore
				break;
			}
			// type of form [T+] so simplify like other compounds.
		case Type.K_RECORD:
		case Type.K_TUPLE:
		case Type.K_FUNCTION:
		case Type.K_METHOD:
		case Type.K_HEADLESS:			
			changed = simplifyCompound(index, state, automata);			
			break;
		}				
		return changed;
	}
	
	private static boolean simplifyNegation(int index, Automaton.State state, Automaton automata) {
		Automaton.State child = automata.states[state.children[0]];
		if(child.kind == Type.K_NEGATION) {
			// bypass node
			Automaton.State childchild = automata.states[child.children[0]];
			automata.states[index] = new Automaton.State(childchild);
			return true;
		}
		return false;
	}
	
	private static boolean simplifyCompound(int index, Automaton.State state, Automaton automata) {
		int kind = state.kind;
		int[] children = state.children;
		for(int i=0;i<children.length;++i) {
			if ((i == 0 || i == 1) && (kind == Type.K_HEADLESS || kind == Type.K_FUNCTION)) {
				// headless method and function return or throws type allowed to be void
				continue;
			} else if((i == 1 || i == 2) && kind == Type.K_METHOD) {
				// method return or throws type allowed to be void
				continue;
			}
			Automaton.State child = automata.states[children[i]];
			if(child.kind == Type.K_VOID) {
				automata.states[index] = new Automaton.State(Type.K_VOID);
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
	private static boolean simplifyUnion(int index, Automaton.State state,
			Automaton automata) {
		return simplifyUnion_1(index, state, automata)
				|| simplifyUnion_2(index, state, automata);
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
	private static boolean simplifyUnion_1(int index, Automaton.State state,
			Automaton automata) {
		int[] children = state.children;
		boolean changed = false;
		for (int i = 0; i < children.length; ++i) {
			int iChild = children[i];
			if (iChild == index) {
				// contractive case
				state.children = removeIndex(i, children);
				changed = true;
			} else {
				Automaton.State child = automata.states[iChild];
				switch (child.kind) {
					case Type.K_ANY :
						automata.states[index] = new Automaton.State(Type.K_ANY);
						return true;
					case Type.K_VOID : {
						children = removeIndex(i, children);
						state.children = children;
						changed = true;
						break;
					}
					case Type.K_UNION :
						return flattenChildren(index, state, automata);
				}
			}
		}
		if (children.length == 0) {
			// this can happen in the case of a union which has only itself as a
			// child.
			automata.states[index] = new Automaton.State(Type.K_VOID);
			changed = true;
		} else if (children.length == 1) {
			// bypass this node altogether
			int child = children[0];
			automata.states[index] = new Automaton.State(automata.states[child]);
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
	private static boolean simplifyUnion_2(int index, Automaton.State state,
			Automaton automata) {
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
			automata.states[index] = new Automaton.State(automata.states[child]);
			changed = true;
		}
		
		return changed;
	}
	
	private static boolean isSubtype(int fromIndex, int toIndex,
			Automaton automata) {
		SubtypeOperator op = new SubtypeOperator(automata,automata);
		return op.isSubtype(fromIndex, toIndex);
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
	
	/**
	 * Compute the intersection of two types. The resulting type will only
	 * accept values which are accepted by both types being intersected.. In
	 * many cases, the only valid intersection will be <code>void</code>.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static Type intersect(Type t1, Type t2) {
		Automaton a1 = Type.destruct(t1);
		Automaton a2 = Type.destruct(t2);
		return Type.construct(intersect(true,a1,true,a2));
	}
	
	private static Automaton intersect(boolean fromSign, Automaton from, boolean toSign, Automaton to) {
		HashMap<IntersectionPoint,Integer> allocations = new HashMap();		
		ArrayList<Automaton.State> nstates = new ArrayList();
		intersect(0,fromSign,from,0,toSign,to,allocations,nstates);		
		return new Automaton(nstates.toArray(new Automaton.State[nstates.size()]));		
	}
	
	private static int intersect(int fromIndex, boolean fromSign,
			Automaton from, int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {				
		
		// first, check whether we have determined this state already
		IntersectionPoint ip = new IntersectionPoint(fromIndex,fromSign,toIndex,toSign);
		Integer allocation = allocations.get(ip);
		if(allocation != null) { return allocation;}
		
		// looks like we haven't, so proceed to determine the new state.
		int myIndex = states.size();
		allocations.put(ip,myIndex);
		states.add(null); // allocate space for me
		
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];		
				
		// now, dispatch for the appropriate case.
		if(fromState.kind == toState.kind) {
			if(fromSign && toSign) {
				return intersectSameKindPosPos(fromIndex, from, toIndex,
					to, allocations, states);
			} else if(fromSign) {
				return intersectSameKindPosNeg(fromIndex, from, toIndex,
						to, allocations, states);
			} else if(toSign) {
				return intersectSameKindNegPos(fromIndex, from, toIndex,
						to, allocations, states);
			} else {
				return intersectSameKindNegNeg(fromIndex, from, toIndex,
						to, allocations, states);
			}
		} else {
			return intersectDifferentKind(fromIndex, fromSign, from, toIndex,
					toSign, to, allocations, states);
		}
	}

	/**
	 * The following method intersects two nodes which have different kinds. A
	 * precondition is that space has already been allocated in states for the
	 * resulting node.
	 * 
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param fromSign
	 *            --- index of state in from position
	 * @param from
	 *            --- automata in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param toSign
	 *            --- index of state in to position
	 * @param to
	 *            --- automata in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automata being
	 *            constructed/
	 * @return
	 */
	private static int intersectDifferentKind(int fromIndex, boolean fromSign,
			Automaton from, int toIndex, boolean toSign, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
				
		int myIndex = states.size()-1;
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];		
		Automaton.State myState = null;
		
		// using invert helps reduce the number of cases to consider.
		int fromKind = invert(fromState.kind,fromSign);
		int toKind = invert(toState.kind,toSign);
				
		// TODO: tidy this mess up
		if(fromKind == Type.K_VOID || toKind == Type.K_VOID) {
			myState = new Automaton.State(Type.K_VOID);
		} else if(fromKind == Type.K_UNION) {						
			
			// (T1 | T2) & T3 => (T1&T3) | (T2&T3)
			int[] fromChildren = fromState.children;
			int[] myChildren = new int[fromChildren.length];
			for(int i=0;i!=fromChildren.length;++i) {
				int fromChild = fromChildren[i];
				myChildren[i] = intersect(fromChild,fromSign,from,toIndex,toSign,to,allocations,states);
			}					
			myState = new Automaton.State(Type.K_UNION,false,myChildren);			
		} else if(toKind == Type.K_UNION) {			
			int[] toChildren = toState.children;
			int[] myChildren = new int[toChildren.length];
			for(int i=0;i!=toChildren.length;++i) {
				int toChild = toChildren[i];
				myChildren[i] = intersect(fromIndex, fromSign, from,
						toChild, toSign, to, allocations, states);
			}			
			myState = new Automaton.State(Type.K_UNION,false,myChildren);					
		} else if (fromKind == K_INTERSECTION) {
			// !(T1 | T2) & T3 => (!T1&T3) & (!T2&T3)
			// => !(!(!T1&T3)|!(!T2&T3))
			int[] fromChildren = fromState.children;
			int[] myChildren = new int[fromChildren.length];
			for (int i = 0; i != fromChildren.length; ++i) {
				int fromChild = fromChildren[i];
				int tmpChild = intersect(fromChild, fromSign, from, toIndex,
						toSign, to, allocations, states);
				myChildren[i] = states.size();
				states.add(new Automaton.State(Type.K_NEGATION, true, tmpChild));
			}
			states.add(new Automaton.State(Type.K_UNION, false, myChildren));
			myState = new Automaton.State(Type.K_NEGATION, true,
					states.size() - 1);
		} else if (toKind == K_INTERSECTION) {
			int[] toChildren = toState.children;
			int[] myChildren = new int[toChildren.length];
			for (int i = 0; i != toChildren.length; ++i) {
				int toChild = toChildren[i];
				int tmpChild = intersect(fromIndex, fromSign, from, toChild,
						toSign, to, allocations, states);
				myChildren[i] = states.size();
				states.add(new Automaton.State(Type.K_NEGATION, true, tmpChild));
			}
			states.add(new Automaton.State(Type.K_UNION, false, myChildren));
			myState = new Automaton.State(Type.K_NEGATION, true,
					states.size() - 1);
		} else if(fromKind == Type.K_NEGATION) {
			states.remove(states.size()-1);
			int fromChild = fromState.children[0];
			return intersect(fromChild,!fromSign,from,toIndex,toSign,to,allocations,states);
		} else if(toKind == Type.K_NEGATION) {			
			states.remove(states.size()-1);
			int toChild = toState.children[0];
			return intersect(fromIndex,fromSign,from,toChild,!toSign,to,allocations,states);
		} else if(fromKind == Type.K_ANY) {				
			states.remove(states.size()-1);
			if(!toSign) {
				states.add(new Automaton.State(Type.K_NEGATION,states.size()+1));
			}
			Automata.extractOnto(toIndex,to,states);
			return myIndex;
		} else if(toKind == Type.K_ANY) {
			states.remove(states.size()-1);
			if(!fromSign) {
				states.add(new Automaton.State(Type.K_NEGATION,states.size()+1));
			}
			Automata.extractOnto(fromIndex,from,states);
			return myIndex;
		} else if(fromSign && toSign) {				
			myState = new Automaton.State(Type.K_VOID);
		} else if(fromSign) {
			states.remove(states.size()-1);
			Automata.extractOnto(fromIndex,from,states);
			return myIndex;
		} else if(toSign) {
			states.remove(states.size()-1);
			Automata.extractOnto(toIndex,to,states);
			return myIndex;
		} else {
			int childIndex = states.size();			
			states.add(null);		
			int nFromChild = states.size();			
			Automata.extractOnto(fromIndex,from,states);
			int nToChild = states.size();
			Automata.extractOnto(toIndex,to,states);
			states.set(childIndex,new Automaton.State(Type.K_UNION, nFromChild,
					nToChild));
			myState = new Automaton.State(Type.K_NEGATION, childIndex);
		}
		
		states.set(myIndex, myState);
		
		return myIndex;
	}
	
	/**
	 * The following method intersects two nodes with positive sign which have
	 * identical kind. A precondition is that space has already been allocated
	 * in states for the resulting node.
	 * 
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automata in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automata in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automata being
	 *            constructed/
	 * @return
	 */
	private static int intersectSameKindPosPos(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
		int myIndex = states.size()-1;
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];		
		Automaton.State myState;
		
		switch(fromState.kind) {
			case Type.K_VOID: {
				// void & void => void
				myState = new Automaton.State(Type.K_VOID);				
				break;
			}
			case Type.K_ANY: {
				// any & any => any
				myState = new Automaton.State(Type.K_ANY);
				break;
			}				
			case Type.K_RECORD: {
				Type.Record.State fromData = (Type.Record.State) fromState.data;
				Type.Record.State toData = (Type.Record.State) toState.data;
				if (fromData.isOpen && toData.isOpen) {					
					myState = intersectPosPosOpenOpen(fromIndex,from,toIndex,to,allocations,states);
					break;
				} else if(toData.isOpen) {
					myState = intersectPosPosClosedOpen(fromIndex,from,toIndex,to,allocations,states);
					break;
				} else if(fromData.isOpen) {
					myState = intersectPosPosClosedOpen(toIndex,to,fromIndex,from,allocations,states);
					break;
				} else {
					// fall through to general case for compounds
				}
			}
			case Type.K_LABEL:
			case Type.K_NOMINAL: {
				if(!fromState.data.equals(toState.data)) {					
					// e.g. {int f} & {int g} => void
					myState = new Automaton.State(Type.K_VOID);
					break;
				}
				// now fall through as for the other compound types.
			}
			case Type.K_TUPLE:
				if(fromState.children.length != toState.children.length) {					
					// e.g. (int,int) & (int) => void
					myState = new Automaton.State(Type.K_VOID);
					break;
				}					
			case Type.K_PROCESS: 
			case Type.K_LIST:
			case Type.K_SET:
			case Type.K_DICTIONARY: {								
				// e.g. [T1] & [T2] => [T1&T2]			
				Object myData;
				if (fromState.kind == Type.K_LIST || fromState.kind == Type.K_SET) {
					// [T1]  & [T2]  => [T1&T2]
					// [T1+] & [T2]  => [T1&T2+]
					// [T1]  & [T2+] => [T1&T2]
					// [T1+] & [T2+] => [T1&T2+]
					boolean fromNonEmpty = (Boolean) fromState.data;
					boolean toNonEmpty = (Boolean) toState.data;
					myData = fromNonEmpty | toNonEmpty;
				} else {
					myData = fromState.data;
				}
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				int[] myChildren = new int[fromChildren.length];
				for(int i=0;i!=fromChildren.length;++i) {
					int fromChild = fromChildren[i];
					int toChild = toChildren[i];
					myChildren[i] = intersect(fromChild, true, from,
							toChild, true, to, allocations, states);
				}				
				myState = new Automaton.State(fromState.kind, myData,
						true, myChildren);
				break;
			}							
			case Type.K_NEGATION: {
				// !T1 & !T2 => !T1 & !T2 (!)
				states.remove(states.size()-1);
				int fromChild = fromState.children[0];
				int toChild = toState.children[0];				
				return intersect(fromChild,false,from,toChild,false,to,allocations,states);
			}				
			case Type.K_UNION: {
				int[] fromChildren = fromState.children;
				int[] newChildren = new int[fromChildren.length];
				for (int i = 0; i != fromChildren.length; ++i) {
					int fromChild = fromChildren[i];
					newChildren[i] = intersect(fromChild, true, from,
							toIndex, true, to, allocations, states);
				}
				myState = new Automaton.State(Type.K_UNION, false, newChildren);
				break;
			}
			case Type.K_FUNCTION:
			case Type.K_HEADLESS:
			case Type.K_METHOD: {
				// int(int) & any(real) => int(int|real)				
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				int[] myChildren = new int[fromChildren.length];
				int returnIndex = 0; // index of return value in children
				
				if(fromState.kind == Type.K_METHOD) {
					// receiver is invariant
					myChildren[0] = intersect(fromChildren[0],
							true, from, toChildren[0], true, to,
							allocations, states);
					returnIndex = 1;
				}
				
				// return value is co-variant (i.e. normal, like e.g. list elements)
				myChildren[returnIndex] = intersect(fromChildren[returnIndex],
						true, from, toChildren[returnIndex], true, to,
						allocations, states);
				
				// throws clause is co-variant (i.e. normal, like e.g. list elements)
				myChildren[returnIndex+1] = intersect(fromChildren[returnIndex+1],
						true, from, toChildren[returnIndex+1], true, to,
						allocations, states);
				
				// parameter values are harder, since they are contra-variant.
				for(int i=returnIndex+2;i<fromChildren.length;++i) {
					int fromChild = fromChildren[i];
					int toChild = toChildren[i];
					int[] childChildren = new int[2];
					myChildren[i] = states.size();					
					states.add(new Automaton.State(Type.K_UNION,null,false,childChildren));					
					childChildren[0] = states.size();
					Automata.extractOnto(fromChild,from,states);
					childChildren[1] = states.size();
					Automata.extractOnto(toChild,to,states);
				}				
				myState = new Automaton.State(fromState.kind, fromState.data,
						true, myChildren);
				break;
			}
			default: {
				// K_BYTE, K_CHAR, K_INT, K_RATIONAL
				// K_STRING, K_NULL			
				// e.g. INT & INT => INT
				myState = new Automaton.State(fromState.kind);
				break;
			}		
		}
		
		states.set(myIndex, myState);
		
		return myIndex;
	}
	
	/**
	 * Intersect two automata representing positive, open records. For example:
	 * 
	 * <pre>
	 * {T1 f, T2 g, ...} & {T3 g, T4 h, ...} => {T1 f, (T2&T3) g, T4 h, ...}
	 * </pre>
	 * 
	 * As you can see from above, we include all fields from both types. For
	 * fields common to both we intersect their types.
	 * 
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automata in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automata in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automata being
	 *            constructed.
	 * @return
	 */
	private static Automaton.State intersectPosPosOpenOpen(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		Type.Record.State fromData = (Type.Record.State) fromState.data;
		Type.Record.State toData = (Type.Record.State) toState.data;
		Type.Record.State myData = new Type.Record.State(true);		
		
		int fi = 0, ti = 0;
		int[] fromChildren = fromState.children;
		int[] toChildren = toState.children;
		ArrayList<Integer> myChildren = new ArrayList<Integer>();
		while (fi < fromData.size() && ti < toData.size()) {			
			int fromChild = fromChildren[fi];
			int toChild = toChildren[ti];
			String fn = fromData.get(fi);
			String tn = toData.get(ti);						
			int c = fn.compareTo(tn);
			if (c == 0) {
				myData.add(fn);
				myChildren.add(intersect(fromChild, true, from, toChild,
						true, to, allocations, states));
				fi++;ti++;
			} else if (c < 0) {
				myData.add(fn);
				myChildren.add(states.size());
				Automata.extractOnto(fromChild,from,states);
				fi++;
			} else {
				myData.add(tn);
				myChildren.add(states.size());
				Automata.extractOnto(toChild,to,states);
				ti++;
			}
		}	
		while (fi < fromData.size()) {
			String fn = fromData.get(fi);
			myData.add(fn);
			int fromChild = fromChildren[fi];
			myChildren.add(states.size());
			Automata.extractOnto(fromChild,from,states);
			fi = fi + 1;
		}
		while (ti < toData.size()) {
			String tn = toData.get(ti);
			myData.add(tn);
			int toChild = toChildren[ti];
			myChildren.add(states.size());
			Automata.extractOnto(toChild,to,states);
			ti = ti + 1;
		}
		// finally, create the new type
		int[] myChildrenArray = new int[myChildren.size()];
		for(int i=0;i!=myChildrenArray.length;++i) {
			 myChildrenArray[i] = myChildren.get(i);
		}
		return new Automaton.State(fromState.kind, myData, true,							
				myChildrenArray);
	}

	/**
	 * Intersect two automata representing positive records, where the first is
	 * closed and the second open. For example:
	 * 
	 * <pre>
	 * {T1 f, T2 g} & {T3 g, T4 h, ...} => void
	 * {T1 f, T2 g} & {T3 g, ...} => {T1 f, (T2&T3) g}
	 * </pre>
	 * 
	 * In this case, the result must include exactly those fields in the closed
	 * record. If a required field in the open record is not present in the
	 * closed record, then there is no intersection.
	 * 
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automata in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automata in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automata being
	 *            constructed.
	 * @return
	 */
	private static Automaton.State intersectPosPosClosedOpen(
			int fromIndex, Automaton from, int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
		
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		Type.Record.State fromData = (Type.Record.State) fromState.data;
		Type.Record.State toData = (Type.Record.State) toState.data;

		int oldSize = states.size(); // in case we need to back out
		int[] myChildren = new int[fromData.size()];
		int[] fromChildren = fromState.children;
		int[] toChildren = toState.children;
		for (int fi = 0, ti = 0; fi != myChildren.length; ++fi) {
			String fn = fromData.get(fi);
			int fromChild = fromChildren[fi];
			if (ti < toData.size()) {
				String tn = toData.get(ti);
				int c = fn.compareTo(tn);
				if (c == 0) {
					int toChild = toChildren[ti++];
					myChildren[fi] = intersect(fromChild, true, from, toChild,
							true, to, allocations, states);
					continue;
				} else if (c > 0) {
					// e.g. {int f} & {int g,...}
					while (states.size() != oldSize) {
						states.remove(states.size() - 1);
					}
					return new Automaton.State(Type.K_VOID);
				}
			}
			myChildren[fi] = states.size();
			Automata.extractOnto(fromChild, from, states);
		}		
		return new Automaton.State(fromState.kind, fromData, true, myChildren);
	}
	
	/**
	 * The following method intersects two nodes with (resp. positive and
	 * negative sign) which have identical kind. A precondition is that space
	 * has already been allocated in states for the resulting node.
	 * 
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automata in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automata in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automata being
	 *            constructed/
	 * @return
	 */
	private static int intersectSameKindPosNeg(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
		int myIndex = states.size()-1;
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];		
		Automaton.State myState;
		
		switch(fromState.kind) {
			case Type.K_ANY: 
			case Type.K_VOID: {
				// void & !void => void
				// any & !any => void
				myState = new Automaton.State(Type.K_VOID);				
				break;
			}
			case Type.K_RECORD: {
				Type.Record.State fromData = (Type.Record.State) fromState.data;
				Type.Record.State toData = (Type.Record.State) toState.data;
				if (fromData.isOpen && toData.isOpen) {					
					myState = intersectPosNegOpenOpen(fromIndex,from,toIndex,to,allocations,states);
					break;
				} else if(toData.isOpen) {
					myState = intersectPosNegClosedOpen(fromIndex,from,toIndex,to,allocations,states);
					break;
				} else if(fromData.isOpen) {
					myState = intersectNegPosClosedOpen(toIndex,to,fromIndex,from,allocations,states);
					break;
				} else {
					// fall through to general case for compounds
				}
			}				
			case Type.K_LABEL:
			case Type.K_NOMINAL: {
				if(!fromState.data.equals(toState.data)) {					
					// e.g. {int f} & !{int g} => {int f}
					states.remove(states.size()-1);
					Automata.extractOnto(fromIndex,from,states);
					return myIndex;
				}
				// now fall through as for the other compound types.
			}
			case Type.K_TUPLE:
				if(fromState.children.length != toState.children.length) {					
					// e.g. (int,int) & !(int) => (int,int)
					states.remove(states.size()-1);
					Automata.extractOnto(fromIndex,from,states);
					return myIndex;
				}
			case Type.K_PROCESS: 
			case Type.K_LIST:
			case Type.K_SET:
			case Type.K_DICTIONARY: {				
				// (T1,T2) & !(T3,T4) => (T1 & !T3, T2) | (T1, T2 & !T4) 
				
				Object myData;
				if (fromState.kind == Type.K_LIST || fromState.kind == Type.K_SET) {
					// [T1]  & ![T2]  => [T1&!T2+]
					// [T1+] & ![T2]  => [T1&!T2+]
					// [T1]  & ![T2+] => [T1&!T2]
					// [T1+] & ![T2+] => [T1&!T2+]
					boolean fromNonEmpty = (Boolean) fromState.data;
					boolean toNonEmpty = (Boolean) toState.data;
					myData = fromNonEmpty || !toNonEmpty;
				} else {
					myData = fromState.data;
				}
				
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				int[] tmpChildren = new int[fromChildren.length];
				for(int i=0;i!=fromChildren.length;++i) {
					tmpChildren[i] = states.size();
					Automata.extractOnto(fromChildren[i],from,states);
				}				
				int[] myChildren = new int[fromChildren.length];
				for(int i=0;i!=fromChildren.length;++i) {
					int[] myChildChildren = new int[fromChildren.length];
					for(int j=0;j!=fromChildren.length;++j) {
						if(i == j) {
							int fromChild = fromChildren[i];
							int toChild = toChildren[i];
							myChildChildren[i] = intersect(fromChild, true, from,
									toChild, false, to, allocations, states);
						} else {
							myChildChildren[j] = tmpChildren[j];
						}
					}
					myChildren[i] = states.size();
					states.add(new Automaton.State(fromState.kind, myData,
							true, myChildChildren));
				}				
				myState = new Automaton.State(Type.K_UNION, null,
						false, myChildren);
				break;
			}
			case Type.K_NEGATION: {
				// !T1 & !!T2 => !T1 & T2 (!)
				states.remove(states.size()-1);
				int fromChild = fromState.children[0];
				int toChild = toState.children[0];
				return intersect(fromChild,false,from,toChild,true,to,allocations,states);
			}				
			case Type.K_UNION: {
				// (T1|T2) & !(T3|T4) => (T1&!(T3|T4)) | (T2&!(T3|T4))				
				int[] fromChildren = fromState.children;
				int[] newChildren = new int[fromChildren.length];
				for (int i = 0; i != fromChildren.length; ++i) {
					int fromChild = fromChildren[i];
					newChildren[i] = intersect(fromChild, true, from,
							toIndex, false, to, allocations, states);
				}
				myState = new Automaton.State(Type.K_UNION, false, newChildren);
				break;
			}
			case Type.K_FUNCTION:
			case Type.K_HEADLESS:
			case Type.K_METHOD:
				// int(int) & !(any(real)) => int&!any(int|!real)				
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				int[] myChildren = new int[fromChildren.length];
				int returnIndex = 0; // index of return value in children
				
				if(fromState.kind == Type.K_METHOD) {
					// receiver is invariant
					myChildren[0] = intersect(fromChildren[0],
							true, from, toChildren[0], false, to,
							allocations, states);
					returnIndex = 1;
				}
				
				// return value is co-variant (i.e. normal, like e.g. list elements)
				myChildren[returnIndex] = intersect(fromChildren[returnIndex],
						true, from, toChildren[returnIndex], false, to,
						allocations, states);

				// throws clause is co-variant (i.e. normal, like e.g. list elements)
				myChildren[returnIndex+1] = intersect(fromChildren[returnIndex+1],
						true, from, toChildren[returnIndex+1], false, to,
						allocations, states);
				
				// parameter values are harder, since they are contra-variant.
				for(int i=returnIndex+2;i<fromChildren.length;++i) {
					int fromChild = fromChildren[i];
					int toChild = toChildren[i];
					int[] childChildren = new int[2];
					myChildren[i] = states.size();					
					states.add(new Automaton.State(Type.K_UNION,null,false,childChildren));					
					childChildren[0] = states.size();
					Automata.extractOnto(fromChild,from,states);
					childChildren[1] = states.size();
					states.add(new Automaton.State(Type.K_NEGATION,null,false,states.size()+1));
					Automata.extractOnto(toChild,to,states);
				}				
				myState = new Automaton.State(fromState.kind, fromState.data,
						true, myChildren);
			default: {
				// e.g. !INT & INT => INT
				myState = new Automaton.State(Type.K_VOID);				
				break;
			}		
		}
		
		states.set(myIndex, myState);
		
		return myIndex;
	}

	/**
	 * Intersect two automata representing open records, where the first is
	 * positive and the second negative. For example:
	 * 
	 * <pre>
	 * {T1 f, T2 g, ...} & !{T3 g, T4 h, ...} => {T1 f, T2&!T3 g, ...} | {T1 f, T2 g, void h, ...}
	 * </pre>
	 * 
	 * This is a fairly ticky case!
	 * 
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automata in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automata in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automata being
	 *            constructed.
	 * @return
	 */
	private static Automaton.State intersectPosNegOpenOpen(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
		// FIXME: need to do better here
		int myIndex = states.size()-1;
		states.remove(myIndex);
		Automata.extractOnto(fromIndex,from,states);
		return states.get(myIndex); 
	}

	/**
	 * Intersect two automata representing records, where the first is positive
	 * and closed and the second negative and open. For example:
	 * 
	 * <pre>
	 * {T1 f, T2 g} & !{T3 g, T4 h, ...} => {T1 f, T2 g}
	 * {T1 f, T2 g} & !{T3 f, T4 g, ...} => {T1 & !T3 f, T2 g} | {T1 f, T2 & !T4 g}
	 * {T1 f, T2 g, T3 h} & !{T4 f, T5 g, ...} => {T1 & !T4 f, T2 g, T3 h} | {T1 f, T2 & !T5 g, T3 h}
	 * </pre>
	 * 
	 * In this case, the result must include exactly those fields in the closed
	 * record. If the open record contains a field not present
	 * in the closed record then it does not intersect with the closed record
	 * and we're fine. Finally, in the case they have exactly the same fields
	 * then we have to distribute.
	 * 
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automata in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automata in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automata being
	 *            constructed.
	 * @return
	 */
	private static Automaton.State intersectPosNegClosedOpen(
			int fromIndex, Automaton from, int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {

		int oldSize = states.size(); // in case we need to back out
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];		
		
		int[] fromChildren = fromState.children;
		int[] toChildren = toState.children;

		// first, check whether the open record has more fields than the closed.
		// If so, then there is no intersection between them and we can just
		// return the closed record.
		
		if(fromChildren.length < toChildren.length) {
			// no possible intersection so back out
			states.remove(states.size()-1);
			Automata.extractOnto(fromIndex,from,states);
			return states.get(oldSize); // tad ugly perhaps			
		}
		
		Type.Record.State fromData = (Type.Record.State) fromState.data;
		Type.Record.State toData = (Type.Record.State) toState.data;
		
		// second, check whether the open record contains a field not present in
		// the closed record. If so, then there is no intersection between them
		// and we can just return the closed record.
		
		int fi=0,ti=0;
		while(ti!=toChildren.length) {
			if(fi < fromChildren.length) {
				String fn = fromData.get(fi);
				String tn = toData.get(ti);
				int c = fn.compareTo(tn);
				if(c < 0) {
					++fi;
					continue;
				} else if(c == 0){
					++ti;
					++fi;
					continue;
				}
			} 			
			// no possible intersection so back out
			states.remove(states.size()-1);
			Automata.extractOnto(fromIndex,from,states);
			return states.get(oldSize); // tad ugly perhaps			 
		}
		
		// finally, distribute over those fields present in the open record
		// (which are a subset of those in the closed record).
		
		int[] tmpChildren = new int[fromChildren.length];
		for(int i=0;i!=fromChildren.length;++i) {
			tmpChildren[i] = states.size();
			Automata.extractOnto(fromChildren[i],from,states);
		}				
		int[] myChildren = new int[toChildren.length];
		for(ti=0;ti!=toChildren.length;++ti) {
			int[] myChildChildren = new int[fromChildren.length];
			String tn = toData.get(ti);
			for(fi=0;fi!=fromChildren.length;++fi) {
				String fn = fromData.get(fi);
				if(fn.equals(tn)) {
					int fromChild = fromChildren[fi];
					int toChild = toChildren[ti];
					myChildChildren[fi] = intersect(fromChild, true, from,
							toChild, false, to, allocations, states);
				} else {
					myChildChildren[fi] = tmpChildren[fi];
				}
			}
			myChildren[ti] = states.size();
			states.add(new Automaton.State(fromState.kind, fromData, true,
					myChildChildren));
		}				
		return new Automaton.State(Type.K_UNION, null, false, myChildren);
	}

	/**
	 * The following method intersects two nodes with (resp. negative and
	 * positive sign) which have identical kind. A precondition is that space
	 * has already been allocated in states for the resulting node.
	 * 
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automata in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automata in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automata being
	 *            constructed/
	 * @return
	 */
	private static int intersectSameKindNegPos(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
	
		int myIndex = states.size()-1;
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];		
		Automaton.State myState;
		
		switch(fromState.kind) {
			case Type.K_ANY: 
			case Type.K_VOID: {
				// !void & void => void
				// !any & any => void
				myState = new Automaton.State(Type.K_VOID);				
				break;
			}
				
			case Type.K_RECORD: {
				Type.Record.State fromData = (Type.Record.State) fromState.data;
				Type.Record.State toData = (Type.Record.State) toState.data;
				if (fromData.isOpen && toData.isOpen) {					
					myState = intersectPosNegOpenOpen(toIndex,to,fromIndex,from,allocations,states);
					break;
				} else if(fromData.isOpen) {
					myState = intersectPosNegClosedOpen(toIndex,to,fromIndex,from,allocations,states);
					break;
				} else if(toData.isOpen) {
					myState = intersectNegPosClosedOpen(fromIndex,from,toIndex,to,allocations,states);;
					break;
				} else {
					// fall through to general case for compounds
				}
			}	
			case Type.K_LABEL:
			case Type.K_NOMINAL: {
				if(!fromState.data.equals(toState.data)) {					
					// e.g. !{int f} & {int g} => {int g}
					states.remove(states.size()-1);
					Automata.extractOnto(toIndex,to,states);
					return myIndex;
				}
				// now fall through as for the other compound types.
			}
			case Type.K_TUPLE:
				if(fromState.children.length != toState.children.length) {					
					// e.g. !(int,int) & (int) => (int)
					states.remove(states.size()-1);
					Automata.extractOnto(toIndex,to,states);
					return myIndex;
				}
			case Type.K_PROCESS: 
			case Type.K_LIST:
			case Type.K_SET:
			case Type.K_DICTIONARY: {
				// !(T1,T2) & (T3,T4) => (!T1 & T3, T2) | (T3, !T2 & T4) 
				
				Object myData;
				if (fromState.kind == Type.K_LIST || fromState.kind == Type.K_SET) {
					// ![T1]  & [T2]  => [!T1&T2+]
					// ![T1+] & [T2]  => [!T1&T2]
					// ![T1]  & [T2+] => [!T1&T2+]
					// ![T1+] & [T2+] => [!T1&T2+]
					boolean fromNonEmpty = (Boolean) fromState.data;
					boolean toNonEmpty = (Boolean) toState.data;
					myData = !fromNonEmpty || toNonEmpty;
				} else {
					myData = fromState.data;
				}
				
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				int[] tmpChildren = new int[fromChildren.length];
				for(int i=0;i!=toChildren.length;++i) {
					tmpChildren[i] = states.size();
					Automata.extractOnto(toChildren[i],to,states);
				}				
				int[] myChildren = new int[fromChildren.length];
				for(int i=0;i!=fromChildren.length;++i) {
					int[] myChildChildren = new int[fromChildren.length];
					for(int j=0;j!=fromChildren.length;++j) {
						if(i == j) {
							int fromChild = fromChildren[i];
							int toChild = toChildren[i];
							myChildChildren[i] = intersect(fromChild, false, from,
									toChild, true, to, allocations, states);
						} else {
							myChildChildren[j] = tmpChildren[j];
						}
					}
					myChildren[i] = states.size();
					states.add(new Automaton.State(fromState.kind, myData,
							true, myChildChildren));
				}				
				myState = new Automaton.State(Type.K_UNION, null,
						false, myChildren);
				break;				
			}							
			case Type.K_NEGATION: {
				// !!T1 & !T2 => T1 & !T2 (!)
				states.remove(states.size()-1);
				int fromChild = fromState.children[0];
				int toChild = toState.children[0];
				return intersect(fromChild,true,from,toChild,false,to,allocations,states);
			}				
			case Type.K_UNION: {
				// !(T1|T2) & (T3|T4) => !(T1|T2)&T3 | !(T1|T2)&T3
				int[] toChildren = toState.children;
				int[] newChildren = new int[toChildren.length];
				for (int i = 0; i != toChildren.length; ++i) {
					int toChild = toChildren[i];
					newChildren[i] = intersect(fromIndex, false, from,
							toChild, true, to, allocations, states);
				}
				myState = new Automaton.State(Type.K_UNION, false, newChildren);
				break;
			}				
			case Type.K_FUNCTION:
			case Type.K_HEADLESS:
			case Type.K_METHOD:
				// !(int(int)) & any(real) => !int&any(!int|real)				
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				int[] myChildren = new int[fromChildren.length];
				int returnIndex = 0; // index of return value in children
				
				if(fromState.kind == Type.K_METHOD) {
					// receiver is invariant
					myChildren[0] = intersect(fromChildren[0],
							false, from, toChildren[0], true, to,
							allocations, states);
					returnIndex = 1;
				}
				
				// return value is co-variant (i.e. normal, like e.g. list elements)
				myChildren[returnIndex] = intersect(fromChildren[returnIndex],
						true, from, toChildren[returnIndex], false, to,
						allocations, states);
				
				// throw clause is co-variant (i.e. normal, like e.g. list elements)
				myChildren[returnIndex+1] = intersect(fromChildren[returnIndex+1],
						true, from, toChildren[returnIndex+1], false, to,
						allocations, states);
				
				// parameter values are harder, since they are contra-variant.
				for(int i=returnIndex+2;i<fromChildren.length;++i) {
					int fromChild = fromChildren[i];
					int toChild = toChildren[i];
					int[] childChildren = new int[2];
					myChildren[i] = states.size();					
					states.add(new Automaton.State(Type.K_UNION,null,false,childChildren));					
					childChildren[0] = states.size();
					states.add(new Automaton.State(Type.K_NEGATION,null,false,states.size()+1));
					Automata.extractOnto(fromChild,from,states);
					childChildren[1] = states.size();					
					Automata.extractOnto(toChild,to,states);
				}				
				myState = new Automaton.State(fromState.kind, fromState.data,
						true, myChildren);
			default: {
				// e.g. INT & !INT => INT
				myState = new Automaton.State(Type.K_VOID);				
				break;
			}		
		}
		
		states.set(myIndex, myState);
		
		return myIndex;
	}

	/**
	 * Intersect two automata representing records, where the first is negative
	 * and closed and the second positive and open. For example:
	 * 
	 * <pre>
	 * !{int f} & {int f,...} => {int f, ...+}
	 * {T1 f, T2 g, ...} & !{T3 g, T4 h} => {T1 f, T2 g}   
	 * {T1 f, T2 g, ...} & !{T3 f, T4 g} => {T1&!T3 f, T2 g,...} | {T1 f, T2&!T4 g,...} 
	 * {T1 f, T2 g, ...} & !{T3 f, T4 g, T5 h} => {T1&!T3 f, T2 g, ...} | 
	 *                      {T1 f, T2&!T4 g, ...} | {T1 f, T2 g, void h, ...} | 
	 *                      {T1 f, T2 g, !T5 h, ...}
	 * </pre>
	 * 
	 * Here, <code>{T1 f, T2 g, void h, ...}</code> represents a record which
	 * doesn't have a field h. This use of void only really makes sense in the
	 * context of open records.
	 * 
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automata in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automata in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automata being
	 *            constructed.
	 * @return
	 */
	private static Automaton.State intersectNegPosClosedOpen(
			int fromIndex, Automaton from, int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {		

		int oldSize = states.size(); // in case we need to back out
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];		
		
		int[] fromChildren = fromState.children;
		int[] toChildren = toState.children;

		// first, check whether the open record has more fields than the closed.
		// If so, then there is no intersection between them and we can just
		// return the closed record.

		if(fromChildren.length < toChildren.length) {
			// no possible intersection so back out
			states.remove(states.size()-1);
			Automata.extractOnto(toIndex,to,states);
			return states.get(oldSize); // tad ugly perhaps			
		}
		
		Type.Record.State fromData = (Type.Record.State) fromState.data;
		Type.Record.State toData = (Type.Record.State) toState.data;
		
		// second, check whether the open record contains a field not present in
		// the closed record. If so, then there is no intersection between them
		// and we can just return the closed record.
		
		int fi=0,ti=0;
		while(ti!=toChildren.length) {
			if(fi < fromChildren.length) {
				String fn = fromData.get(fi);
				String tn = toData.get(ti);
				int c = fn.compareTo(tn);
				if(c < 0) {
					++fi;
					continue;
				} else if(c == 0){
					++ti;
					++fi;
					continue;
				}
			} 			
			// no possible intersection so back out
			states.remove(states.size()-1);
			Automata.extractOnto(toIndex,to,states);
			return states.get(oldSize); // tad ugly perhaps			 
		}
		
		// finally, distribute over those fields present in the open record
		// (which are a subset of those in the closed record).
		
		int[] tmpChildren = new int[toChildren.length];
		for(int i=0;i!=toChildren.length;++i) {
			tmpChildren[i] = states.size();
			Automata.extractOnto(toChildren[i],to,states);
		}				
		int[] myChildren = new int[fromChildren.length];
		for(fi=0;fi!=fromChildren.length;++fi) {
			int[] myChildChildren = new int[fromChildren.length];			
			ti=0;
			for(int fj=0;fj!=fromChildren.length;++fj) {
				String fn = fromData.get(fj);
				String tn;
				if (ti < toData.size() && (tn = toData.get(ti)).equals(fn)) {
					if(fi == fj) {
						int fromChild = fromChildren[fi];
						int toChild = toChildren[ti];
						myChildChildren[fj] = intersect(fromChild, false, from,
								toChild, true, to, allocations, states);
					} else {
						myChildChildren[fj] = tmpChildren[ti];
					}
					ti++;
				} else {
					states.add(new Automaton.State(Type.K_VOID));
					myChildChildren[fj] = states.size()-1; 
				}
			}
			myChildren[fi] = states.size();
			states.add(new Automaton.State(toState.kind, toData, true,
					myChildChildren));
		}				
		return new Automaton.State(Type.K_UNION, null, false, myChildren);
	}
	
	/**
	 * The following method intersects two nodes with (resp) negative and
	 * negative sign which have identical kind. A precondition is that space has
	 * already been allocated in states for the resulting node.
	 * 
	 * @param fromIndex
	 *            --- index of state in from position
	 * @param from
	 *            --- automata in the from position (i.e. containing state at
	 *            fromIndex).
	 * @param toIndex
	 *            --- index of state in to position
	 * @param to
	 *            --- automata in the to position (i.e. containing state at
	 *            toIndex).
	 * @param allocations
	 *            --- mapping of intersection points to their index in states
	 * @param states
	 *            --- list of states which constitute the new automata being
	 *            constructed/
	 * @return
	 */
	private static int intersectSameKindNegNeg(int fromIndex, Automaton from,
			int toIndex, Automaton to,
			HashMap<IntersectionPoint, Integer> allocations,
			ArrayList<Automaton.State> states) {
		
		int myIndex = states.size()-1;
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];		
		Automaton.State myState;
		
		switch(fromState.kind) {
			case Type.K_VOID: {
				// !void & !void => any
				myState = new Automaton.State(Type.K_ANY);				
				break;
			}
			case Type.K_ANY: {
				// !any & !any -> void				
				myState = new Automaton.State(Type.K_VOID);				
				break;
			}				
			case Type.K_LABEL:
			case Type.K_NOMINAL:
			case Type.K_PROCESS:
			case Type.K_LIST:
			case Type.K_SET:
			case Type.K_DICTIONARY:
			case Type.K_TUPLE: 
			case Type.K_RECORD: 
			case Type.K_FUNCTION:
			case Type.K_HEADLESS:
			case Type.K_METHOD: {				
				// e.g. ![int] & ![real] => !([int]|[real])
				int childIndex = states.size();						
				states.add(null);
				int fromChild = states.size();
				Automata.extractOnto(fromIndex,from,states);
				int toChild = states.size();
				Automata.extractOnto(toIndex,to,states);
				states.set(childIndex, new Automaton.State(Type.K_UNION,false,fromChild,toChild));
				myState = new Automaton.State(Type.K_NEGATION,childIndex);
				break;
			}				
			case Type.K_NEGATION: {
				// !!T1 & !!T2 => T1 & T2 (!)
				states.remove(states.size()-1);
				int fromChild = fromState.children[0];
				int toChild = toState.children[0];
				return intersect(fromChild,true,from,toChild,true,to,allocations,states);			
			}				
			case Type.K_UNION: {
				// !(T1|T2) & !(T3|T4) => !T1 & !T2 & !T3 & !T4 => !(T1|T2|T3|T4)
				int[] fromChildren = fromState.children;
				int[] toChildren = toState.children;
				int[] newChildren = new int[fromChildren.length+toChildren.length];
				int childIndex = states.size();
				states.add(new Automaton.State(Type.K_UNION, false, newChildren));
				for (int i = 0; i != fromChildren.length; ++i) {
					int fromChild = fromChildren[i];
					newChildren[i] = states.size();
					Automata.extractOnto(fromChild,from,states);					
				}
				for (int i = 0; i != toChildren.length; ++i) {
					int toChild = toChildren[i];
					newChildren[i+fromChildren.length] = states.size();
					Automata.extractOnto(toChild,to,states);					
				}
				myState = new Automaton.State(Type.K_NEGATION, true, childIndex);
				break;
			}				
			default: {
				// e.g. !INT & !INT => !INT
				int childIndex = states.size();
				states.add(new Automaton.State(fromState.kind));
				myState = new Automaton.State(Type.K_NEGATION,childIndex);			
				break;			
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
			case K_UNION:
				return K_INTERSECTION;			
			default:
				return kind;
		}		
	}

	/**
	 * The following constant is not actually a valid kind; however, it's
	 * helpful to think of it as one.
	 */
	private static final int K_INTERSECTION = -1;
	
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
	private static boolean flattenChildren(int index, Automaton.State state,
			Automaton automata) {
		ArrayList<Integer> nchildren = new ArrayList<Integer>();
		int[] children = state.children;
		final int kind = state.kind;

		for (int i = 0; i < children.length; ++i) {
			int iChild = children[i];
			Automaton.State child = automata.states[iChild];
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

		automata.states[index] = new Automaton.State(kind, false, children);

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
	private static int splitPositiveNegativeChildren(Automaton.State state,
			Automaton automata) {
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
			Automaton automata) {
		while (index < children.length
				&& automata.states[children[index]].kind != Type.K_NEGATION) {
			index = index + 1;
		}
		return index;
	}

	private static int retreatNegative(int index, int[] children,
			Automaton automata) {
		while (index > 0
				&& automata.states[children[index]].kind == Type.K_NEGATION) {
			index = index - 1;
		}
		return index;
	}	
}
