// Copyright 2017 David J. Pearce
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyc.type.subtyping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;

import wycc.util.Pair;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wyc.type.SubtypeOperator;
import wyc.type.TypeSystem;

import static wyc.lang.WhileyFile.*;
import static wyc.lang.WhileyFile.Name;

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
public class StrictSubtypeOperator implements SubtypeOperator {
	private final TypeSystem typeSystem;

	public StrictSubtypeOperator(TypeSystem typeSystem) {
		this.typeSystem = typeSystem;
	}

	@Override
	public Result isSubtype(Type parent, Type child) throws ResolutionError {
		// FIXME: we can do better in some situations here. For example, if we
		// have the same nominal types they can cancel each other.
		HashSetAssumptions assumptions = new HashSetAssumptions();
		Term<?> lhsMaxTerm = new Term<>(false, parent, true);
		Term<?> rhsMaxTerm = new Term<>(true, child, true);
		boolean max = isVoidTerm(lhsMaxTerm, rhsMaxTerm, assumptions);
		//
		// FIXME: I don't think this logic is correct yet for some reason.
		if(!max) {
			return Result.False;
		} else {
			Term<?> lhsMinTerm = new Term<>(false, parent, false);
			Term<?> rhsMinTerm = new Term<>(true, child, false);
			boolean min = isVoidTerm(lhsMinTerm, rhsMinTerm, assumptions);
			if(min) {
				return Result.True;
			} else {
				return Result.Unknown;
			}
		}
	}

	protected boolean isVoidTerm(Term<?> lhs, Term<?> rhs, Assumptions assumptions) throws ResolutionError {
		//
		if (assumptions.isAssumedVoid(lhs, rhs)) {
			// This represents the "coinductive" case. That is, we have
			// encountered a pair of recursive types whose "voidness" depends
			// circularly on itself. In such case, we assume they are indeed
			// void.
			return true;
		} else {
			assumptions.setAssumedVoid(lhs, rhs);
			ArrayList<Atom<?>> truths = new ArrayList<>();
			Worklist worklist = new Worklist();
			worklist.push(lhs);
			worklist.push(rhs);
			boolean r = isVoid(truths, worklist, assumptions);
			assumptions.clearAssumedVoid(lhs, rhs);
			return r;
		}
	}

	/**
	 * Determine whether or not the intersection of a given list of types (the
	 * worklist) reduces to void or not. This is performed in the context of a
	 * number of ground "atoms" which are known to hold. In essence, this
	 * algorithm exhaustively expands all items on the worklist to form atoms.
	 * The expanded atoms are then checked for consistency.
	 *
	 * is type is equivalent to void. This is a relatively complex operation
	 * which builds up a list of clauses known to hold.
	 *
	 * @param truths
	 *            The set of truths which have been established.
	 * @param worklist
	 *            The set of types currently being expanded
	 * @param assumptions
	 *            The set of assumed subtype relationships
	 * @return
	 * @throws ResolutionError
	 */
	protected boolean isVoid(ArrayList<Atom<?>> truths, Worklist worklist, Assumptions assumptions) throws ResolutionError {
		// FIXME: there is a bug in the following case which needs to be
		// addressed:
		//
		// {int|null f} & !{int f} & !{null f}
		//
		// The problem is that we need the "pairwise consistency property" in
		// order for this algorithm to be complete. To get that, we must expand
		// records containing union types. Thus, the above should be expanded
		// to:
		//
		// ({int f} & !{int f} & !{null f}) || ({null f} & !{int f} & !{null f})
		//
		// This will now produce the correct result.

		if (worklist.size() == 0) {
			// At this point, we have run out of terms to expand further.
			// Therefore, we have accumulated the complete list of "truths" and
			// we must now attempt to establish whether or not this is
			// consistent. For example, "int & !bool & !int" is not consistent
			// because "int & !int" is not consistent.
			//
			// Therefore, we consider each possible pair of truths looking for
			// consistency.
			//
			for (int i = 0; i != truths.size(); ++i) {
				Atom<?> ith = truths.get(i);
				for (int j = i + 1; j != truths.size(); ++j) {
					Atom<?> jth = truths.get(j);
					if (isVoidAtom(ith, jth, assumptions)) {
						return true;
					}
				}
			}
			return false;
		} else {
			// In this case, we still have items on the worklist which need to
			// be processed. That is, broken down into "atomic" terms.
			Term<Type> item = worklist.pop();
			Type t = item.type;
			boolean conjunct = item.sign;
			//
			switch (t.getOpcode()) {
			case TYPE_or:
				conjunct = !conjunct;
			case TYPE_and: {
				Type.Combinator ut = (Type.Combinator) t;
				Type[] operands = ut.toArray(Type.class);
				if (conjunct) {
					// Conjunction
					worklist.push(item.sign, operands, item.maximise);
				} else {
					// Disjunction
					for (int i = 0; i != operands.length; ++i) {
						Worklist tmp = (Worklist) worklist.clone();
						tmp.push(item.sign, operands[i], item.maximise);
						if (!isVoid((ArrayList<Atom<?>>) truths.clone(), tmp, assumptions)) {
							// If a single clause of the disjunct is definitely
							// not void, then the whole thing is not void.
							return false;
						}
					}
					return true;
				}
				break;
			}
			case TYPE_not: {
				Type.Negation nt = (Type.Negation) t;
				worklist.push(!item.sign, nt.getElement(), !item.maximise);
				break;
			}
			case TYPE_nom: {
				Type.Nominal nom = (Type.Nominal) t;
				Declaration.Type decl = typeSystem.resolveExactly(nom.getName(),Declaration.Type.class);
				if (item.maximise || decl.getInvariant().size() == 0) {
					worklist.push(item.sign, decl.getType(), item.maximise);
				} else if (item.sign) {
					// Corresponds to void, so we're done on this path.
					return true;
				}
				break;
			}
			default:
				truths.add(new Atom(item.sign, (Type.Atom) item.type, item.maximise));
			}
			return isVoid(truths, worklist, assumptions);
		}
	}

	protected Name[] append(Name[] lhs, Name rhs) {
		if (rhs == null) {
			return lhs;
		} else {
			lhs = Arrays.copyOf(lhs, lhs.length + 1);
			lhs[lhs.length - 1] = rhs;
			return lhs;
		}
	}

	/**
	 * Determine whether the intersection of two arbitrary atoms results in void
	 * or not. Each atom is either a "positive" or "negative" term. The latter
	 * corresponds to negated terms, such as !int or !{int f}.
	 *
	 * @param a
	 * @param b
	 * @param assumptions
	 *            The set of assumed subtype relationships
	 * @return
	 * @throws ResolutionError
	 */
	protected boolean isVoidAtom(Atom<?> a, Atom<?> b, Assumptions assumptions) throws ResolutionError {
		// At this point, we have several cases left to consider.
		boolean aSign = a.sign;
		boolean bSign = b.sign;
		int aOpcode = a.type.getOpcode();
		int bOpcode = b.type.getOpcode();
		//
		if (aOpcode == bOpcode) {
			// In this case, we are intersecting two atoms of the same kind, of
			// which at least one is positive. For primitive types, this reduces
			// to void is one of them is negative. For non-primitive types, it
			// requires further investigation.
			switch (aOpcode) {
			case TYPE_void:
				// void & void => void
				// !void & void => void
				return true;
			case TYPE_any:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				// any & !any => void
				// int & !int => void
				return (aSign != bSign) ? true : false;
			case TYPE_arr:
				return isVoidArray((Atom<Type.Array>) a, (Atom<Type.Array>) b, assumptions);
			case TYPE_rec:
				return isVoidRecord((Atom<Type.Record>) a, (Atom<Type.Record>) b, assumptions);
			case TYPE_ref:
				return isVoidReference((Atom<Type.Reference>) a, (Atom<Type.Reference>) b, assumptions);
			case TYPE_fun:
			case TYPE_meth:
			case TYPE_property:
				return isVoidFunction((Atom<Type.Callable>) a, (Atom<Type.Callable>) b, assumptions);
			default:
				throw new RuntimeException("invalid type encountered: " + aOpcode);
			}
		} else if (aSign && bSign) {
			// We have two positive atoms of different kind. For example, int
			// and {int f}, or int and !bool. This always reduces to void,
			// unless one of them is any.
			return (aOpcode != TYPE_any && bOpcode != TYPE_any) ? true : false;
		} else if (aSign) {
			// We have a positive atom and a negative atom of different kinds.
			// For example, int and !bool or int and !(bool[]). This only
			// reduces to void in the case that one of them is equivalent to
			// void (i.e. is void or !any).
			return (aOpcode == TYPE_void || bOpcode == TYPE_any) ? true : false;
		} else if (bSign) {
			// We have a negative atom and a positive atom of different kinds.
			// For example, !int and bool or !(int[]) and bool[]. This only
			// reduces to void in the case that one of them is equivalent to
			// void (i.e. is void or !any).
			return (aOpcode == TYPE_any || bOpcode == TYPE_void) ? true : false;
		} else {
			// We have two negative atoms of different kinds. For example, !int
			// and !bool or !int[] and !bool. This only reduces to void in the
			// case that one of them is equivalent to void (i.e. is !any).
			return (aOpcode == TYPE_any || bOpcode == TYPE_any) ? true : false;
		}
	}

	/**
	 * <p>
	 * Determine whether the intersection of two array types is void or not. For
	 * example, <code>int[]</code> intersecting with <code>bool[]</code> gives
	 * void. In contrast, intersecting <code>(int|null)[]</code> with
	 * <code>int[]</code> does not give void. Likewise, <code>int[]</code>
	 * intersecting with <code>!(int[])</code> gives void, whilst intersecting
	 * <code>int[]</code> with <code>!(bool[])</code> does not give void.
	 * </p>
	 *
	 * @param lhsSign
	 *            The sign of the first type being intersected. If true, we have
	 *            a positive atom. Otherwise, we have a negative atom.
	 * @param lhs.
	 *            The first type being intersected, referred to as the
	 *            "left-hand side".
	 * @param rhsSign
	 *            The sign of the second type being intersected. If true, we
	 *            have a positive atom. Otherwise, we have a negative atom.
	 * @param rhs
	 *            The second type being intersected, referred to as the
	 *            "right-hand side".
	 * @param assumptions
	 *            The set of assumed subtype relationships
	 * @return
	 * @throws ResolutionError
	 */
	protected boolean isVoidArray(Atom<Type.Array> lhs, Atom<Type.Array> rhs, Assumptions assumptions)
			throws ResolutionError {
		if (lhs.sign || rhs.sign) {
			Term<?> lhsTerm = new Term<>(lhs.sign, lhs.type.getElement(), lhs.maximise);
			Term<?> rhsTerm = new Term<>(rhs.sign, rhs.type.getElement(), rhs.maximise);
			// In this case, we are intersecting two array types, of which at
			// least one is positive. This is void only if there is no
			// intersection of the underlying element types. For example, int[]
			// and bool[] is void, whilst (int|null)[] and int[] is not.
			return isVoidTerm(lhsTerm, rhsTerm, assumptions);
		} else {
			// In this case, we are intersecting two negative array types. For
			// example, !(int[]) and !(bool[]). This never reduces to void.
			return false;
		}
	}

	/**
	 * <p>
	 * Determine whether the intersection of two record types is void or not.
	 * For example, <code>{int f}</code> intersecting with <code>{int g}</code>
	 * gives void. In contrast, intersecting <code>{int|null f}</code> with
	 * <code>{int f}</code> does not give void. Likewise, <code>{int f}</code>
	 * intersecting with <code>!{int f}</code> gives void, whilst intersecting
	 * <code>{int f}</code> with <code>!{int g}</code> does not give void.
	 * </p>
	 *
	 * @param lhsSign
	 *            The sign of the first type being intersected. If true, we have
	 *            a positive atom. Otherwise, we have a negative atom.
	 * @param lhs.
	 *            The first type being intersected, referred to as the
	 *            "left-hand side".
	 * @param rhsSign
	 *            The sign of the second type being intersected. If true, we
	 *            have a positive atom. Otherwise, we have a negative atom.
	 * @param rhs
	 *            The second type being intersected, referred to as the
	 *            "right-hand side".
	 * @param assumptions
	 *            The set of assumed subtype relationships
	 * @return
	 * @throws ResolutionError
	 */
	protected boolean isVoidRecord(Atom<Type.Record> lhs, Atom<Type.Record> rhs, Assumptions assumptions)
			throws ResolutionError {
		Tuple<Declaration.Variable> lhsFields = lhs.type.getFields();
		Tuple<Declaration.Variable> rhsFields = rhs.type.getFields();
		//
		if (lhs.sign || rhs.sign) {
			// The sign indicates whether were in the pos-pos case, or in the
			// pos-neg case.
			boolean sign = (lhs.sign == rhs.sign);
			// Attempt to match all fields In the positive-positive case this
			// reduces to void if the fields in either of these differ (e.g.
			// {int f} and {int g}), or if there is no intersection between the
			// same field in either (e.g. {int f} and {bool f}).
			int matches = 0;
			//
			for (int i = 0; i != lhsFields.size(); ++i) {
				Declaration.Variable lhsField = lhsFields.getOperand(i);
				Term<?> lhsTerm = new Term<>(lhs.sign, lhsField.getType(), lhs.maximise);
				for (int j = 0; j != rhsFields.size(); ++j) {
					Declaration.Variable rhsField = rhsFields.getOperand(j);
					if (!lhsField.getName().equals(rhsField.getName())) {
						continue;
					} else {
						Term<?> rhsTerm = new Term<>(rhs.sign, rhsField.getType(), rhs.maximise);
						if (sign == isVoidTerm(lhsTerm, rhsTerm, assumptions)) {
							// For pos-pos case, there is no intersection
							// between these fields and, hence, no intersection
							// overall; for pos-neg case, there is some
							// intersection between these fields which means
							// that some intersections exists overall. For
							// example, consider the case {int f, int|null g} &
							// !{int f, int g}. There is no intersection for
							// field f (i.e. since int & !int = void), whilst
							// there is an intersection for field g (i.e. since
							// int|null & !int = null). Hence, we can conclude
							// that there is an intersection between them with
							// {int f, null g}.
							return sign;
						} else {
							matches = matches + 1;
						}
					}
				}
			}
			//
			if (matches < lhsFields.size() && !rhs.type.isOpen()) {
				// We have matched fewer fields than contained in the lhs.
				// However, if the rhs is an open record, then it can match the
				// remainder. Otherwise, there is no match here. In the pos-pos
				// case, this means there is no intersection. In the pos-neg
				// case, this means there is an intersection.
				return sign;
			} else if (matches < rhsFields.size() && !lhs.type.isOpen()) {
				// We have matched fewer fields than contained in the rhs.
				// However, if the lhs is an open record, then it can match the
				// remainder. Otherwise, there is no match here. In the pos-pos
				// case, this means there is no intersection. In the pos-neg
				// case, this means there is an intersection.
				return sign;
			} else if(!lhs.sign && !lhs.type.isOpen() && rhs.type.isOpen()) {
				// Matches e.g. !{int x} & {int x, ...}.
				return false;
			} else if(!rhs.sign && !rhs.type.isOpen() && lhs.type.isOpen()) {
				// Matches e.g. {int x, ...} & !{int x}.
				return false;
			} else {
				// If we get here, then: for pos-pos case, all fields have
				// intersection; for pos-neg case, no fields have intersection.
				return !sign;
			}
		} else {
			// In this case, we are intersecting two negative record types. For
			// example, !({int f}) and !({int g}). This never reduces to void.
			return false;
		}
	}

	/**
	 * <p>
	 * Determine whether the intersection of two reference types is void or not.
	 * Reference types are "invariant", meaning that element types must match
	 * exactly for an intersection to arise. For example, <code>&int</code>
	 * intersecting with <code>&bool</code> gives void. In contrast,
	 * intersecting <code>&int</code> with <code>&int</code> does not give void.
	 * Hoever, <code>&int</code> intersecting with <code>&(int|bool)</code>
	 * gives void.
	 * </p>
	 *
	 * @param lhsSign
	 *            The sign of the first type being intersected. If true, we have
	 *            a positive atom. Otherwise, we have a negative atom.
	 * @param lhs.
	 *            The first type being intersected, referred to as the
	 *            "left-hand side".
	 * @param rhsSign
	 *            The sign of the second type being intersected. If true, we
	 *            have a positive atom. Otherwise, we have a negative atom.
	 * @param rhs
	 *            The second type being intersected, referred to as the
	 *            "right-hand side".
	 * @param assumptions
	 *            The set of assumed subtype relationships
	 * @return
	 * @throws ResolutionError
	 */
	protected boolean isVoidReference(Atom<Type.Reference> lhs, Atom<Type.Reference> rhs, Assumptions assumptions)
			throws ResolutionError {
		// FIXME: need to look at lifetime parameters
		Term<?> lhsTrueTerm = new Term<>(true, lhs.type.getElement(), lhs.maximise);
		Term<?> rhsTrueTerm = new Term<>(true, rhs.type.getElement(), rhs.maximise);
		Term<?> lhsFalseTerm = new Term<>(false, lhs.type.getElement(), lhs.maximise);
		Term<?> rhsFalseTerm = new Term<>(false, rhs.type.getElement(), rhs.maximise);
		// Check whether lhs :> rhs (as (!lhs & rhs) == 0)
		boolean elemLhsSubsetRhs = isVoidTerm(lhsFalseTerm, rhsTrueTerm, assumptions);
		// Check whether rhs :> lhs (as (!rhs & lhs) == 0)
		boolean elemRhsSubsetLhs = isVoidTerm(rhsFalseTerm, lhsTrueTerm, assumptions);
		// Calculate whether lhs == rhs
		boolean elemEqual = elemLhsSubsetRhs && elemRhsSubsetLhs;
		//
		if (lhs.sign && rhs.sign) {
			// (&T1 & &T2) == 0 iff T1 != T2
			return !elemEqual;
		} else if (!lhs.sign || !rhs.sign) {
			// (!(&T1) & &T2) == 0 iff T1 == T2
			return elemEqual;
		} else {
			return true;
		}
	}

	/**
	 * <p>
	 * Determine whether the intersection of two function types is void or not.
	 * For example, <code>function(int)->(int)</code> intersecting with
	 * <code>function(bool)->(int)</code> gives void. In contrast, intersecting
	 * <code>function(int|null)->(int)</code> with
	 * <code>function(int)->(int)</code> does not give void. Likewise,
	 * <code>function(int)->(int)</code> intersecting with
	 * <code>!function(int)->(int)</code> gives void, whilst intersecting
	 * <code>function(int)->(int)</code> with
	 * <code>!function(bool)->(int)</code> does not give void.
	 * </p>
	 *
	 *
	 * @param lhsSign
	 *            The sign of the first type being intersected. If true, we have
	 *            a positive atom. Otherwise, we have a negative atom.
	 * @param lhs.
	 *            The first type being intersected, referred to as the
	 *            "left-hand side".
	 * @param rhsSign
	 *            The sign of the second type being intersected. If true, we
	 *            have a positive atom. Otherwise, we have a negative atom.
	 * @param rhs
	 *            The second type being intersected, referred to as the
	 *            "right-hand side".
	 * @param assumptions
	 *            The set of assumed subtype relationships private boolean
	 * @throws ResolutionError
	 */
	protected boolean isVoidFunction(Atom<Type.Callable> lhs, Atom<Type.Callable> rhs, Assumptions assumptions)
			throws ResolutionError {
		if (lhs.sign || rhs.sign) {
			// The sign indicates whether were in the pos-pos case, or in the
			// pos-neg case.
			Tuple<Type> lhsParameters = lhs.type.getParameters();
			Tuple<Type> rhsParameters = rhs.type.getParameters();
			Tuple<Type> lhsReturns = lhs.type.getParameters();
			Tuple<Type> rhsReturns = rhs.type.getParameters();
			//
			// FIXME: should maximise be flipped for parameters as well?
			//
			boolean pr = isVoidParameters(!lhs.sign, lhs.maximise, lhsParameters, !rhs.sign, rhs.maximise,
					rhsParameters, assumptions);
			boolean rr = isVoidParameters(lhs.sign, lhs.maximise, lhsReturns, rhs.sign, rhs.maximise, rhsReturns,
					assumptions);
			return pr || rr;
		} else {
			// In this case, we are intersecting two negative function types.
			// For example, !(function(int)->(int)) and
			// !(function(bool)->(int)). This never reduces to void.
			return false;
		}
	}

	protected boolean isVoidParameters(boolean lhsSign, boolean lhsMax, Tuple<Type> lhs, boolean rhsSign, boolean rhsMax,
			Tuple<Type> rhs, Assumptions assumptions) throws ResolutionError {
		boolean sign = lhsSign == rhsSign;
		//
		if (lhs.size() != rhs.size()) {
			// Different number of parameters. In either pos-pos or neg-neg
			// cases, this reduces to void. Otherwise, it doesn't.
			return false;
		} else {
			//
			for (int i = 0; i != lhs.size(); ++i) {
				Type lhsParameter = lhs.getOperand(i);
				Type rhsParameter = rhs.getOperand(i);
				Term<?> lhsTerm = new Term<>(lhsSign, lhsParameter, lhsMax);
				Term<?> rhsTerm = new Term<>(rhsSign, rhsParameter, rhsMax);
				if (sign == isVoidTerm(lhsTerm, rhsTerm, assumptions)) {
					// For pos-pos / neg-neg case, there is no intersection
					// between this parameterand, hence, no intersection
					// overall; for pos-neg case, there is some
					// intersection between these parameters which means
					// that some intersections exists overall. For example,
					// consider the case (int,int|null) & !(int,int). There is
					// no intersection for first parameter (i.e. since int &
					// !int = void), whilst there is an intersection for second
					// parameter (i.e. since int|null & !int = null). Hence, we
					// can conclude that there is an intersection between them
					// with (int,null).
					return sign;
				}
			}
			if (sign == true) {
				// for pos-pos case, all parameters have intersection. Hence,
				// there is a possible intersection.
				return false;
			} else {
				// for pos-neg case, no parameters have intersection. Hence, no
				// possible intersection.
				return true;
			}
		}
	}

	// ========================================================================
	// Helpers
	// ========================================================================

	private final static class Worklist extends ArrayList<Term<Type>> {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public Term<Type> top() {
			return get(size() - 1);
		}

		public void push(Term item) {
			add(item);
		}

		public void push(boolean sign, Type type, boolean maximise) {
			add(new Term(sign, type, maximise));
		}

		public void push(boolean sign, Type[] types, boolean maximise) {
			for (int i = 0; i != types.length; ++i) {
				add(new Term(sign, types[i], maximise));
			}
		}

		public Term<Type> pop() {
			Term<Type> i = get(size() - 1);
			remove(size() - 1);
			return i;
		}

		@Override
		public Worklist clone() {
			Worklist wl = new Worklist();
			wl.addAll(this);
			return wl;
		}
	}

	protected static class Term<T extends Type> {
		public final boolean sign;
		public final T type;
		public final boolean maximise;

		public Term(boolean sign, T type, boolean maximise) {
			this.type = type;
			this.sign = sign;
			this.maximise = maximise;
		}

		@Override
		public String toString() {
			return type.toString() + ":" + sign + ":" + maximise;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Term) {
				Term t = (Term) o;
				return sign == t.sign && maximise == t.maximise && type.equals(t.type);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return type.hashCode();
		}
	}

	protected static class Atom<T extends Type.Atom> extends Term<T> {
		public Atom(boolean sign, T type, boolean maximise) {
			super(sign, type, maximise);
		}

		@Override
		public String toString() {
			String r = type.toString();
			if (sign) {
				return r;
			} else {
				return "!" + r;
			}
		}
	}

	protected interface Assumptions {
		public boolean isAssumedVoid(Term<?> lhs, Term<?> rhs);

		public void setAssumedVoid(Term<?> lhs, Term<?> rhs);

		public void clearAssumedVoid(Term<?> lhs, Term<?> rhs);
	}

	private static final class HashSetAssumptions implements Assumptions {
		private final HashSet<Pair<Term,Term>> assumptions;

		public HashSetAssumptions() {
			this.assumptions = new HashSet<>();
		}

		@Override
		public boolean isAssumedVoid(Term<?> lhs, Term<?> rhs) {
			return assumptions.contains(new Pair<>(lhs,rhs));
		}

		@Override
		public void setAssumedVoid(Term<?> lhs, Term<?> rhs) {
			assumptions.add(new Pair<>(lhs,rhs));
		}

		@Override
		public void clearAssumedVoid(Term<?> lhs, Term<?> rhs) {
			assumptions.remove(new Pair<>(lhs,rhs));
		}
	}

	private static final class BitSetAssumptions implements Assumptions {
		private final BitSet assumptions;

		public BitSetAssumptions(int size) {
			this.assumptions = new BitSet(size);
		}

		public BitSetAssumptions(BitSet assumptions) {
			this.assumptions = assumptions;
		}

		public int size() {
			return assumptions.size();
		}

		@Override
		public boolean isAssumedVoid(Term<?> lhs, Term<?> rhs) {
			if (assumptions != null) {
				return assumptions.get(indexOf(lhs.sign, lhs.type, rhs.sign, rhs.type));
			} else {
				return false;
			}
		}

		@Override
		public void setAssumedVoid(Term<?> lhs, Term<?> rhs) {
			if (assumptions != null) {
				assumptions.set(indexOf(lhs.sign, lhs.type, rhs.sign, rhs.type));
			}
		}

		@Override
		public void clearAssumedVoid(Term<?> lhs, Term<?> rhs) {
			if (assumptions != null) {
				assumptions.clear(indexOf(lhs.sign, lhs.type, rhs.sign, rhs.type));
			}
		}

		protected int indexOf(boolean lhsSign, Type lhs, boolean rhsSign, Type rhs) {
			int lhsSize = lhs.getHeap().size();
			int rhsSize = rhs.getHeap().size();
			int lhsIndex = lhs.getIndex();
			int rhsIndex = rhs.getIndex();
			if (lhsSign) {
				lhsIndex += lhsSize;
			}
			if (rhsSign) {
				rhsIndex += rhsSize;
			}
			return (lhsIndex * rhsSize * 2) + rhsIndex;
		}
	}
}
