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

import static wyil.lang.Type.*;

import java.util.*;

import wyautl_old.lang.*;
import wybs.lang.NameID;
import wyil.lang.Type;

/**
 * <p>
 * The subtype operator implements the algorithm for determining whether or not
 * one type is a <i>subtype</i> of another. For the most part, one can take
 * subtype to mean <i>subset</i> (this analogy breaks down with function types,
 * however). Following this analogy, <code>T1</code> is a subtype of
 * <code>T2</code> (denoted <code>T1 <: T2</code>) if the set of values
 * represented by <code>T1</code> is a subset of those represented by
 * <code>T2</code>.
 * </p>
 * <p>
 * The algorithm actually operates by computing the <i>intersection</i> relation
 * for two types (i.e. whether or not an intersection exists between their set
 * of values). Subtyping is closely related to intersection and, in fact, we
 * have that <code>T1 :> T2</code> iff <code>!(!T1 & T2)</code> (where
 * <code>&</code> is the intersection relation). The choice to compute
 * intersections, rather than subtypes, was for simplicity. Namely, it was
 * considered conceptually easier to think about intersections rather than
 * subtypes.
 * </p>
 * <p>
 * <b>NOTE:</b> for this algorithm to return correct results in all cases, both
 * types must have been normalised first.
 * </p>
 * <h3>References</h3>
 * <ul>
 * <li><p>David J. Pearce and James Noble. Structural and Flow-Sensitive Types for
 * Whiley. Technical Report, Victoria University of Wellington, 2010.</p></li>
 * <li><p>A. Frisch, G. Castagna, and V. Benzaken. Semantic subtyping. In
 * Proceedings of the <i>Symposium on Logic in Computer Science</i>, pages
 * 137--146. IEEE Computer Society Press, 2002.</p></li>
 * <li><p>Dexter Kozen, Jens Palsberg, and Michael I. Schwartzbach. Efficient
 * recursive subtyping. In <i>Proceedings of the ACM Conference on Principles of
 * Programming Languages</i>, pages 419--428, 1993.</p></li>
 * <li><p>Roberto M. Amadio and Luca Cardelli. Subtyping recursive types. <i>ACM
 * Transactions on Programming Languages and Systems</i>,
 * 15:575--631, 1993.</p></li>
 * </ul>
 *
 * @author David J. Pearce
 *
 */
public class SubtypeOperator {
	protected final Automaton from;
	protected final Automaton to;
	private final Map<String, String> fromLifetimeSubstitution;
	private final Map<String, String> toLifetimeSubstitution;
	private final LifetimeRelation lifetimeRelation;
	private final BitSet assumptions;

	public SubtypeOperator(Automaton from, Automaton to, LifetimeRelation lr) {
		this.from = from;
		this.to = to;
		this.lifetimeRelation = lr;
		this.fromLifetimeSubstitution = Collections.emptyMap();
		this.toLifetimeSubstitution = Collections.emptyMap();
		// matrix is twice the size to accommodate positive and negative signs
		this.assumptions = new BitSet((2*from.size()) * (to.size()*2));
		//System.out.println("FROM: " + from);
		//System.out.println("TO:   " + to);
	}

	private SubtypeOperator(SubtypeOperator sto, Map<String, String> fromLifetimeSubstitution,
			Map<String, String> toLifetimeSubstitution) {
		this.from = sto.from;
		this.to = sto.to;
		this.fromLifetimeSubstitution = new HashMap<String, String>(sto.fromLifetimeSubstitution);
		prependLifetimes(this.fromLifetimeSubstitution);
		this.fromLifetimeSubstitution.putAll(fromLifetimeSubstitution);
		this.toLifetimeSubstitution = new HashMap<String, String>(sto.toLifetimeSubstitution);
		prependLifetimes(this.toLifetimeSubstitution);
		this.toLifetimeSubstitution.putAll(toLifetimeSubstitution);
		this.lifetimeRelation = sto.lifetimeRelation;
		this.assumptions = sto.assumptions;
	}

	/**
	 * Test whether <code>from</code> :> <code>to</code>
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public final boolean isSubtype(int fromIndex, int toIndex) {
		return !isIntersection(fromIndex,false,toIndex,true);
	}

	/**
	 * Test whether <code>from</code> <: <code>to</code>
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 */
	public final boolean isSupertype(int fromIndex, int toIndex) {
		return !isIntersection(fromIndex,true,toIndex,false);
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
	protected boolean isIntersection(int fromIndex, boolean fromSign, int toIndex,
			boolean toSign) {

		//System.out.println("STARTING: " + fromIndex + "(" + fromSign + ") & " + toIndex + "(" + toSign + ")");

		// TODO: can further improve performance using caching

		int index = indexOf(fromIndex,fromSign,toIndex,toSign);
		if(assumptions.get(index)) {
			//System.out.println("ASSUMED:  " + fromIndex + "(" + fromSign + ") & " + toIndex + "(" + toSign + ")");
			return false;
		} else {
			assumptions.set(index,true);
		}

		boolean r = isIntersectionInner(fromIndex,fromSign,toIndex,toSign);

		assumptions.set(index,false);

		//System.out.println("RESULT:   " + fromIndex + "(" + fromSign + ") & " + toIndex + "(" + toSign + ") = " + r);

		return r;
	}

	protected boolean isIntersectionInner(int fromIndex, boolean fromSign, int toIndex,
			boolean toSign) {

		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		int fromKind = fromState.kind;
		int toKind = toState.kind;

		if(fromKind == toKind) {
			switch(fromKind) {
			case K_VOID:
				return !fromSign && !toSign;
			case K_ANY:
				return fromSign && toSign;
			// === Leaf States First ===
			case K_NOMINAL: {
				NameID nid1 = (NameID) fromState.data;
				NameID nid2 = (NameID) toState.data;
				if(fromSign || toSign) {
					if(nid1.equals(nid2)) {
						return fromSign && toSign;
					} else {
						return !fromSign || !toSign;
					}
				}
				return true;
			}
			// === Homogenous Compound States ===
			case K_SET:
			case K_LIST:
				// != below not ||. This is because lists and sets can intersect
				// on the empty list/set.
				if(fromSign != toSign) {
					// nary nodes
					int fromChild = fromState.children[0];
					int toChild = toState.children[0];
					if (!isIntersection(fromChild, fromSign, toChild, toSign)) {
						return false;
					}
				}
				return true;
			case K_REFERENCE:
				if(fromSign || toSign) {
					int fromChild = fromState.children[0];
					int toChild = toState.children[0];

					if (fromSign && toSign) {
						// "Is there any value in both &a:A and &b:B ?"
						//
						// This is equivalent to the question "Is there any value in both &A and &B?"
						// If there is a value in both &a:A and &b:B, then we can take that value
						// and change its lifetime to *. It will then be in both &A and &B.
						// If there is a value in both &A and &B, then it is also in both &a:A and &b:B
						// because the lifetime * outlives a and b.
						//
						// Finally, there is a value in both &A and &B iff the types A and B intersect.
						return isIntersection(fromChild, true, toChild, true);
					} else {
						String fromLifetime = applyFromLifetimeSubstitution((String) fromState.data);
						String toLifetime = applyToLifetimeSubstitution((String) toState.data);

						if (fromSign) {
							// Equivalent to the negation of "is &a:A (fromType) a subtype of &b:B (toType)?"
							return !(
								// &a:A is a subtype of &b:B iff all these are true:
								// a outlives b
								lifetimeRelation.outlives(fromLifetime, toLifetime)
								// A is a subtype of B
								&& !isIntersection(fromChild, true, toChild, false)
								// B is a subtype of A
								&& !isIntersection(fromChild, false, toChild, true)
							);
						} else {
							// Equivalent to the negation of "is &b:B (toType) a subtype of &a:A (fromType)?"
							return !(
								// &b:B is a subtype of &a:A iff all these are true:
								// b outlives a
								lifetimeRelation.outlives(toLifetime, fromLifetime)
								// B is a subtype of A
								&& !isIntersection(fromChild, false, toChild, true)
								// A is a subtype of B
								&& !isIntersection(fromChild, true, toChild, false)
							);
						}
					}
				}

				// An inverted reference type always contains the value "null".
				// Both are inverted, so they intersect with "null".
				return true;
			case K_MAP:
			case K_TUPLE:  {
				if(fromSign || toSign) {
					// nary nodes
					int[] fromChildren = fromState.children;
					int[] toChildren = toState.children;
					if (fromChildren.length != toChildren.length) {
						return !fromSign || !toSign;
					}
					boolean andChildren = true;
					boolean orChildren = false;
					for (int i = 0; i < fromChildren.length; ++i) {
						int fromChild = fromChildren[i];
						int toChild = toChildren[i];
						boolean v = isIntersection(fromChild, fromSign, toChild,
								toSign);
						andChildren &= v;
						orChildren |= v;
					}
					if(!fromSign || !toSign) {
						return orChildren;
					} else {
						return andChildren;
					}
				}
				return true;
			}
			case K_RECORD:
				return intersectRecords(fromIndex,fromSign,toIndex,toSign);
			case K_NEGATION:
			case K_UNION :
					// let these cases fall through to if-statements after
					// switch.
				break;
			// === Heterogenous Compound States ===
			case K_FUNCTION:
			case K_METHOD:
				if(fromSign || toSign) {
					// nary nodes
					int[] fromChildren = fromState.children;
					int[] toChildren = toState.children;
					Type.FunctionOrMethod.Data fromData = (Type.FunctionOrMethod.Data) fromState.data;
					Type.FunctionOrMethod.Data toData = (Type.FunctionOrMethod.Data) toState.data;
					int fromNumParams = fromData.numParams;
					int toNumParams = toData.numParams;
					List<String> fromLifetimeParameters = fromData.lifetimeParameters;
					List<String> toLifetimeParameters = toData.lifetimeParameters;

					if (fromSign && toSign) {
						// Two intersecting method types must have the same number
						// of parameters, returns and lifetime parameters.
						if (fromChildren.length != toChildren.length
								|| fromNumParams != toNumParams
								|| fromLifetimeParameters.size() != toLifetimeParameters.size()){
							return false;
						}
						// Context lifetimes do not matter, because we can always choose
						// a method without context lifetimes to find an intersecting method.
					} else {
						// Now we have one of the following:
						// fromSign == true: negation of "is fromType a subtype of toType?"
						// toSign  ==  true: negation of "is toType a subtype of fromType?"

						// Two method types can only be subtypes of each other if they have
						// the same number of parameters, returns and lifetime parameters.
						if (fromChildren.length != toChildren.length
								|| fromNumParams != toNumParams
								|| fromLifetimeParameters.size() != toLifetimeParameters.size()){
							return true; // true means "not subtype"
						}

						// The supertype must contain all context lifetimes of the subtype
						List<String> fromContextLifetimes = applyFromLifetimeSubstitution(fromData.contextLifetimes);
						List<String> toContextLifetimes = applyToLifetimeSubstitution(toData.contextLifetimes);
						if (fromSign) {
							if (!toContextLifetimes.containsAll(fromContextLifetimes)) {
								return true;
							}
						} else {
							if (!fromContextLifetimes.containsAll(toContextLifetimes)) {
								return true;
							}
						}
					}

					// Calculate the substitutions for lifetime parameters
					Map<String, String> fromLifetimeSubstitution = buildLifetimeSubstitution(fromLifetimeParameters);
					Map<String, String> toLifetimeSubstitution = buildLifetimeSubstitution(toLifetimeParameters);
					SubtypeOperator sto = this;
					if (!fromLifetimeSubstitution.isEmpty() || !toLifetimeSubstitution.isEmpty()) {
						sto = new SubtypeOperator(this, fromLifetimeSubstitution, toLifetimeSubstitution);
					}

					if (fromSign && toSign) {
						// Two intersecting method types must have intersecting return types.
						// Parameter types can always be chosen as "any".
						for (int i = fromNumParams; i < fromChildren.length; ++i) {
							if (!sto.isIntersection(fromChildren[i], true, toChildren[i], true)) {
								return false;
							}
						}
						return true;
					}

					// here we have the subtype test again...

					// Parameter types are contra-variant
					for (int i = 0; i < fromNumParams; ++i) {
						if (sto.isIntersection(fromChildren[i], !fromSign, toChildren[i], !toSign)) {
							return true;
						}
					}

					// Return types are co-variant
					for (int i = fromNumParams; i < fromChildren.length; ++i) {
						if (sto.isIntersection(fromChildren[i], fromSign, toChildren[i], toSign)) {
							return true;
						}
					}

					return false;
				}
				return true;
			default:
				return fromSign == toSign;
			}
		}

		if(fromKind == K_NEGATION) {
			int fromChild = fromState.children[0];
			return isIntersection(fromChild,!fromSign,toIndex,toSign);
		} else if(toKind == K_NEGATION) {
			int toChild = toState.children[0];
			return isIntersection(fromIndex,fromSign,toChild,!toSign);
		}

		// using invert helps reduce the number of cases to consider.
		fromKind = invert(fromKind,fromSign);
		toKind = invert(toKind,toSign);

		if(fromKind == K_VOID || toKind == K_VOID){
			return false;
		} else if(fromKind == K_UNION) {
			int[] fromChildren = fromState.children;
			for(int i : fromChildren) {
				if(isIntersection(i,fromSign,toIndex,toSign)) {
					return true;
				}
			}
			return false;
		} else if(toKind == K_UNION) {
			int[] toChildren = toState.children;
			for(int j : toChildren) {
				if(isIntersection(fromIndex,fromSign,j,toSign)) {
					return true;
				}
			}
			return false;
		} else if(fromKind == K_INTERSECTION) {
			int[] fromChildren = fromState.children;
			for (int i : fromChildren) {
				if(!isIntersection(i,fromSign,toIndex,toSign)) {
					return false;
				}
			}
			return true;
		} else if(toKind == K_INTERSECTION) {
			int[] toChildren = toState.children;
			for (int j : toChildren) {
				if(!isIntersection(fromIndex,fromSign,j,toSign)) {
					return false;
				}
			}
			return true;
		} else if(fromKind == K_ANY || toKind == K_ANY){
			return true;
		}

		return !fromSign || !toSign;
	}

	private static Map<String, String> buildLifetimeSubstitution(List<String> lifetimeParameters) {
		if (lifetimeParameters.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> substitution = new HashMap<String, String>();
		int i = 0;
		for (String lifetime : lifetimeParameters) {
			substitution.put(lifetime, "$" + (i++));
		}
		return substitution;
	}

	private static void prependLifetimes(Map<String, String> substitution) {
		for (Map.Entry<String, String> entry : substitution.entrySet()) {
			entry.setValue("$" + entry.getValue());
		}
	}

	private String applyFromLifetimeSubstitution(String lifetime) {
		String replacement = fromLifetimeSubstitution.get(lifetime);
		if (replacement != null) {
			return replacement;
		}
		return lifetime;
	}

	private String applyToLifetimeSubstitution(String lifetime) {
		String replacement = toLifetimeSubstitution.get(lifetime);
		if (replacement != null) {
			return replacement;
		}
		return lifetime;
	}

	private List<String> applyFromLifetimeSubstitution(List<String> lifetimes) {
		if (lifetimes.isEmpty() || fromLifetimeSubstitution.isEmpty()) {
			return lifetimes;
		}
		List<String> replacement = new ArrayList<String>(lifetimes.size());
		for (String lifetime : lifetimes) {
			replacement.add(applyFromLifetimeSubstitution(lifetime));
		}
		return replacement;
	}

	private List<String> applyToLifetimeSubstitution(List<String> lifetimes) {
		if (lifetimes.isEmpty() || toLifetimeSubstitution.isEmpty()) {
			return lifetimes;
		}
		List<String> replacement = new ArrayList<String>(lifetimes.size());
		for (String lifetime : lifetimes) {
			replacement.add(applyToLifetimeSubstitution(lifetime));
		}
		return replacement;
	}

	/**
	 * <p>
	 * Check for intersection between two states with kind K_RECORD. The
	 * distinction between open and closed records adds complexity here.
	 * </p>
	 *
	 * <p>
	 * Intersection between <b>closed</b> records is the easiest case. The main
	 * examples are:
	 * </p>
	 * <ul>
	 * <li><code>{T1 f, T2 g} & {T3 f, T4 g} = if T1&T3 and T2&T4</code>.</li>
	 * <li><code>{T1 f, T2 g} & {T3 f, T4 h} = false</code>.</li>
	 * <li><code>{T1 f, T2 g} & !{T3 f, T4 g} = if T1&!T3 or T2&!T4</code>.</li>
	 * <li><code>{T1 f, T2 g} & !{T3 f} = false</code>.</li>
	 * <li><code>!{T1 f} & !{T2 f} = true</code>.</li>
	 * </ul>
	 * <p>
	 * Intersection between a <b>closed</b> and <b>open</b> record is similar. The main examples are:
	 * </p>
	 * <ul>
	 * <li><code>{T1 f, T2 g, ...} & {T3 f, T4 g} = if T1&T3 and T2&T4</code>.</li>
	 * <li><code>{T1 f, ...} & {T2 f, T3 g} = if T1&T2</code>.</li>
	 * <li><code>{T1 f, T2 g, ...} & {T3 f, T4 h} = false</code>.</li>
	 * <li><code>{T1 f, T2 g, ...} & !{T3 f, T4 g} = if T1&!T3 or T2&!T4</code>.</li>	 *
	 * <li><code>!{T1 f, T2 g, ...} & {T3 f, T4 g} = if T1&!T3 or T2&!T4</code>.</li>	 *
	 * <li><code>{T1 f, ...} & !{T2 f, T3 g} = true</code>.</li>
	 * <li><code>{T1 f, T2 g, ...} & !{T3 f, T4 h} = false</code>.</li>
     * <li><code>!{T1 f,...} & !{T2 f} = true</code>.</li>
	 * </ul>
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
	protected boolean intersectRecords(int fromIndex, boolean fromSign, int toIndex, boolean toSign) {
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		if(fromSign || toSign) {
			int[] fromChildren = fromState.children;
			int[] toChildren = toState.children;
			Type.Record.State fromFields = (Type.Record.State) fromState.data;
			Type.Record.State toFields = (Type.Record.State) toState.data;

			boolean fromOpen = fromFields.isOpen;
			boolean toOpen = toFields.isOpen;

			if (fromChildren.length < toChildren.length && !fromOpen) {
				return !fromSign || !toSign;
			} else if (fromChildren.length > toChildren.length  && !toOpen) {
				return !fromSign || !toSign;
			} else if (!fromSign && !fromOpen && toOpen) {
				return true; // guaranteed true!
			} else if (!toSign && !toOpen && fromOpen) {
				return true; // guaranteed true!
			}

			boolean andChildren = true;
			boolean orChildren = false;

			int fi=0;
			int ti=0;
			while(fi != fromFields.size() && ti != toFields.size()) {
				boolean v;
				String fn = fromFields.get(fi);
				String tn = toFields.get(ti);
				int c = fn.compareTo(tn);
				if(c == 0) {
					int fromChild = fromChildren[fi++];
					int toChild = toChildren[ti++];
					v = isIntersection(fromChild, fromSign, toChild,
							toSign);
				} else if(c < 0 && toOpen) {
					fi++;
					v = toSign;
				} else if(c > 0 && fromOpen) {
					ti++;
					v = fromSign;
				} else {
					return !fromSign || !toSign;
				}
				andChildren &= v;
				orChildren |= v;
			}

			if(fi < fromFields.size()) {
				if(toOpen) {
					// assert fromSign || fromOpen
					orChildren |= toSign;
					andChildren &= toSign;
				} else {
					return !fromSign || !toSign;
				}
			} else if(ti < toFields.size()) {
				if(fromOpen) {
					// assert toSign || toOpen
					orChildren |= fromSign;
					andChildren &= fromSign;
				} else {
					return !fromSign || !toSign;
				}
			}

			if(!fromSign || !toSign) {
				return orChildren;
			} else {
				return andChildren;
			}
		}
		return true;
	}

	private int indexOf(int fromIndex, boolean fromSign,
			int toIndex, boolean toSign) {
		int to_size = to.size();
		if(fromSign) {
			fromIndex += from.size();
		}
		if(toSign) {
			toIndex += to_size;
		}
		return (fromIndex*to_size*2) + toIndex;
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
}
