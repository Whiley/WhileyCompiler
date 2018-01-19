// Copyright 2011 The Whiley Project Developers
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
package wyil.type.util;

import wyc.lang.WhileyFile.Type;
import wyc.util.ErrorMessages;

import static wyc.lang.WhileyFile.*;
import static wyc.util.ErrorMessages.errorMessage;

import wycc.util.ArrayUtils;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.subtyping.SubtypeOperator;

import java.util.Arrays;
import wybs.lang.CompilationUnit;
import wybs.lang.NameResolver;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError;
import wybs.lang.NameResolver.ResolutionError;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.SemanticType;

/**
 * <p>
 * A generic foundation for "convential" type extractors. That is, those which
 * extract type information from types (rather than, for example, information
 * about type invariants, etc). The approach consists of two phases:
 * </p>
 *
 * <ol>
 * <li><b>(Conversion)</b>. First we convert the given type into <i>Disjunctive
 * Normal Form (DNF)</i>. That is a type with a specific structure made up from
 * one or more "conjuncts" which are disjuncted together. Each conjunct contains
 * one or more signed atoms which are intersected. For example,
 * "<code>int&(null|bool)</code>" is converted into
 * "<code>(int&null)|(int&bool)</code>".</li>
 * <li><b>(Construction)</b>. Second, we attempt to construct the DNF type into
 * an instance of the target type. This requires that all types in a given
 * conjunct match the given target type (or are negations thereof). One
 * exception, however, is when a given conjunct gives a contradiction (i.e.
 * reduces to void). Such conjuncts can be safely ignored. For example, consider
 * the type "<code>!null & (null|{int f})</code>". This expands to the conjuncts
 * "<code>!null & null</code>" and "<code>(!null)&{int f}</code>". Since the
 * former reduces to "<code>void</code>", we are left with
 * "<code>(!null)&{int f}</code>" which generates the extraction
 * "<code>{int f}</code>".</li>
 * </ol>
 *
 * <p>
 * During construction the concrete subclass is called to try and construct an
 * instance of the target type from a given atom. This gives the subclass the
 * ability to determine what the target type is. Furthermore, the subclass must
 * provide appropriate methods for combining instances of the target type though
 * <i>union</i> , <i>intersection</i> and <i>subtraction</i> operators.
 * </p>
 * <p>
 * For further reading on the operation of this algorithm, the following is
 * suggested:
 * <ul>
 * <li><b>Sound and Complete Flow Typing with Unions, Intersections and
 * Negations</b>. David J. Pearce. In Proceedings of the Conference on
 * Verification, Model Checking, and Abstract Interpretation (VMCAI), volume
 * 7737 of Lecture Notes in Computer Science, pages 335--354, 2013.</li>
 * </ul>
 * </p>
 *
 * @author David J. Pearce
 *
 * @param <T>
 */
public abstract class AbstractTypeExtractor<T extends SemanticType> {
	private final NameResolver resolver;
	private final SubtypeOperator subtypeOperator;

	public AbstractTypeExtractor(NameResolver resolver, SubtypeOperator subtypeOperator) {
		this.resolver = resolver;
		this.subtypeOperator = subtypeOperator;
	}

	public T apply(SemanticType type, LifetimeRelation lifetimes) {
		//
		// First, convert type into conjunctive normal form. This allows all atom
		// combinations to be tried and potentially reduced to void which, in turn,
		// allows further simplifications.
		Disjunct dnf = toDisjunctiveNormalForm(type);
		// Now, convert from DNF back into a type whilst, hopefully, preserving the
		// underlying form we are looking for (e.g. an array or record, etc).
		return construct(dnf, lifetimes);
	}

	// ====================================================================================
	// toDisjunctiveNormalForm
	// ====================================================================================

	/**
	 * Convert an arbitrary type to <i>Disjunctive Normal Form (DNF)</i>. That is a
	 * type with a specific structure made up from one or more "conjuncts" which are
	 * unioned together. Each conjunct contains one or more signed atoms which are
	 * intersected. For example, <code>int&(null|bool)</code> is converted into
	 * <code>(int&null)|(int&bool)</code>.
	 *
	 * @param type
	 * @param atoms
	 * @return
	 * @throws ResolutionError
	 */
	protected Disjunct toDisjunctiveNormalForm(SemanticType type) {
		switch (type.getOpcode()) {
		case TYPE_null:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
			return toDisjunctiveNormalForm((Type.Primitive) type);
		case SEMTYPE_array:
		case TYPE_array:
			return toDisjunctiveNormalForm((SemanticType.Array) type);
		case TYPE_record:
		case SEMTYPE_record:
			return toDisjunctiveNormalForm((SemanticType.Record) type);
		case TYPE_reference:
		case TYPE_staticreference:
		case SEMTYPE_reference:
		case SEMTYPE_staticreference:
			return toDisjunctiveNormalForm((SemanticType.Reference) type);
		case TYPE_nominal:
			return toDisjunctiveNormalForm((Type.Nominal) type);
		case TYPE_union:
		case SEMTYPE_union:
			return toDisjunctiveNormalForm((SemanticType.Union) type);
		case SEMTYPE_intersection:
			return toDisjunctiveNormalForm((SemanticType.Intersection) type);
		case SEMTYPE_difference:
			return toDisjunctiveNormalForm((SemanticType.Difference) type);
		default:
			throw new IllegalArgumentException("unknown type encountered: " + type);
		}
	}

	protected Disjunct toDisjunctiveNormalForm(Type.Primitive type) {
		return new Disjunct(type);
	}

	protected Disjunct toDisjunctiveNormalForm(Type.Callable type) {
		return new Disjunct(type);
	}

	protected Disjunct toDisjunctiveNormalForm(Type.Nominal nominal) {
		try {
			Decl.Type decl = resolver.resolveExactly(nominal.getName(), Decl.Type.class);
			return toDisjunctiveNormalForm(decl.getVariableDeclaration().getType());
		} catch (ResolutionError e) {
			return syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, nominal.getName().toString()), nominal);
		}
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Record type) {
		return new Disjunct(type);
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Reference type) {
		return new Disjunct(type);
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Array type) {
		return new Disjunct(type);
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Difference type) {
		Disjunct lhs = toDisjunctiveNormalForm(type.getLeftHandSide());
		Disjunct rhs = toDisjunctiveNormalForm(type.getRightHandSide());
		return lhs.intersect(rhs.negate());
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Union type) {
		Disjunct result = null;
		//
		for (int i = 0; i != type.size(); ++i) {
			Disjunct child = toDisjunctiveNormalForm(type.get(i));
			if (result == null) {
				result = child;
			} else {
				result = result.union(child);
			}
		}
		//
		return result;
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Intersection type) {
		Disjunct result = null;
		//
		for (int i = 0; i != type.size(); ++i) {
			Disjunct child = toDisjunctiveNormalForm(type.get(i));
			if (result == null) {
				result = child;
			} else {
				result = result.intersect(child);
			}
		}
		//
		return result;
	}

	// ====================================================================================
	// construct
	// ====================================================================================

	/**
	 * Construct a given target type from a given type in DisjunctiveNormalForm.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	protected T construct(Disjunct type, LifetimeRelation lifetimes) {
		T result = null;
		Conjunct[] conjuncts = type.conjuncts;
		for (int i = 0; i != conjuncts.length; ++i) {
			Conjunct conjunct = conjuncts[i];
			if (!isVoid(conjunct, lifetimes)) {
				T tmp = construct(conjunct);
				if (tmp == null) {
					// This indicates one of the conjuncts did not generate a proper
					// extraction.  In this case, we can simply ignore it.
				} else if (result == null) {
					result = tmp;
				} else {
					result = union(result, tmp);
				}
			}
		}
		return result;
	}

	/**
	 * Determine whether a given conjunct is equivalent to <code>void</code> or not.
	 * For example, <code>int & !int</code> is equivalent to <code>void</code>.
	 * Likewise, <code>{int f} & !{any f}</code> is equivalent to <code>void</code>.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	protected boolean isVoid(Conjunct type, LifetimeRelation lifetimes) {
		// FIXME: do we really need to defensively copy all this data here?
		SemanticType.Atom[] positives = Arrays.copyOf(type.positives, type.positives.length);
		SemanticType.Atom[] negatives = Arrays.copyOf(type.negatives, type.negatives.length);
		SemanticType.Intersection lhs = new SemanticType.Intersection(positives);
		SemanticType.Union rhs = new SemanticType.Union(negatives);
		try {
			//
			return subtypeOperator.isVoid(new SemanticType.Difference(lhs, rhs), lifetimes);
		} catch (ResolutionError e) {
			// FIXME: not quite sure what the best way to handle this is.
			throw new RuntimeException(e);
		}
	}

	protected T construct(Conjunct type) {
		T result = null;
		// First, combine the positive terms together
		SemanticType.Atom[] positives = type.positives;
		for (int i = 0; i != positives.length; ++i) {
			SemanticType.Atom pos = positives[i];
			T tmp = construct(pos);
			if (tmp == null) {
				return null;
			} else if (result == null) {
				result = tmp;
			} else {
				result = intersect(result, tmp);
			}
		}
		if (result != null) {
			// Second, subtract all the negative types
			SemanticType.Atom[] negatives = type.negatives;
			for (int i = 0; i != negatives.length; ++i) {
				T tmp = construct(negatives[i]);
				if (tmp != null) {
					result = subtract(result, tmp);
				}
			}
		}
		return result;
	}

	protected abstract T construct(SemanticType.Atom type);

	protected abstract T union(T lhs, T rhs);

	protected abstract T intersect(T lhs, T rhs);

	protected abstract T subtract(T lhs, T rhs);

	// ====================================================================================
	// Disjunct
	// ====================================================================================

	protected static class Disjunct {
		private final Conjunct[] conjuncts;

		public Disjunct(SemanticType.Atom atom) {
			conjuncts = new Conjunct[] { new Conjunct(atom) };
		}

		public Disjunct(Conjunct... conjuncts) {
			for (int i = 0; i != conjuncts.length; ++i) {
				if (conjuncts[i] == null) {
					throw new IllegalArgumentException("conjuncts cannot contain null");
				}
			}
			this.conjuncts = conjuncts;
		}

		public Disjunct union(Disjunct other) {
			Conjunct[] otherConjuncts = other.conjuncts;
			int length = conjuncts.length + otherConjuncts.length;
			Conjunct[] combinedConjuncts = Arrays.copyOf(conjuncts, length);
			System.arraycopy(otherConjuncts, 0, combinedConjuncts, conjuncts.length, otherConjuncts.length);
			return new Disjunct(combinedConjuncts);
		}

		public Disjunct intersect(Disjunct other) {
			Conjunct[] otherConjuncts = other.conjuncts;
			int length = conjuncts.length * otherConjuncts.length;
			Conjunct[] combinedConjuncts = new Conjunct[length];
			int k = 0;
			for (int i = 0; i != conjuncts.length; ++i) {
				Conjunct ith = conjuncts[i];
				for (int j = 0; j != otherConjuncts.length; ++j) {
					Conjunct jth = otherConjuncts[j];
					combinedConjuncts[k++] = ith.intersect(jth);
				}
			}
			return new Disjunct(combinedConjuncts);
		}

		public Disjunct negate() {
			Disjunct result = null;
			for (int i = 0; i != conjuncts.length; ++i) {
				Disjunct d = conjuncts[i].negate();
				if (result == null) {
					result = d;
				} else {
					result = result.intersect(d);
				}
			}
			return result;
		}

		@Override
		public String toString() {
			String r = "";
			for (int i = 0; i != conjuncts.length; ++i) {
				if (i != 0) {
					r += " \\/ ";
				}
				r += conjuncts[i];
			}
			return r;
		}
	}

	protected static class Conjunct {
		private final SemanticType.Atom[] positives;
		private final SemanticType.Atom[] negatives;

		public Conjunct(SemanticType.Atom positive) {
			positives = new SemanticType.Atom[] { positive };
			negatives = new SemanticType.Atom[0];
		}

		public Conjunct(SemanticType.Atom[] positives, SemanticType.Atom[] negatives) {
			this.positives = positives;
			this.negatives = negatives;
		}

		public Conjunct intersect(Conjunct other) {
			SemanticType.Atom[] combinedPositives = ArrayUtils.append(positives, other.positives);
			SemanticType.Atom[] combinedNegatives = ArrayUtils.append(negatives, other.negatives);
			return new Conjunct(combinedPositives, combinedNegatives);
		}

		public Disjunct negate() {
			int length = positives.length + negatives.length;
			Conjunct[] conjuncts = new Conjunct[length];
			for (int i = 0; i != positives.length; ++i) {
				SemanticType.Atom positive = positives[i];
				conjuncts[i] = new Conjunct(EMPTY_ATOMS, new SemanticType.Atom[] { positive });
			}
			for (int i = 0, j = positives.length; i != negatives.length; ++i, ++j) {
				SemanticType.Atom negative = negatives[i];
				conjuncts[j] = new Conjunct(negative);
			}
			return new Disjunct(conjuncts);
		}

		@Override
		public String toString() {
			String r = "(";
			for (int i = 0; i != positives.length; ++i) {
				if (i != 0) {
					r += " /\\ ";
				}
				r += positives[i];
			}
			r += ") - (";
			for (int i = 0; i != negatives.length; ++i) {
				if (i != 0) {
					r += " \\/ ";
				}
				r += negatives[i];
			}
			return r + ")";
		}

		private static final Type.Atom[] EMPTY_ATOMS = new Type.Atom[0];
	}

	// ===============================================================
	// Misc
	// ===============================================================

	/**
	 * Provides a simplistic form of type union which, in some cases, does
	 * slightly better than simply creating a new union. For example, unioning
	 * <code>int</code> with <code>int</code> will return <code>int</code>
	 * rather than <code>int|int</code>.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends SemanticType> T unionHelper(T lhs, T rhs) {
		if(lhs.equals(rhs)) {
			return lhs;
		} else if(lhs instanceof Type.Void) {
			return rhs;
		} else if(rhs instanceof Type.Void) {
			return lhs;
		} else if(lhs instanceof Type && rhs instanceof Type) {
			return (T) new Type.Union(new Type[]{(Type)lhs,(Type)rhs});
		} else {
			return (T) new SemanticType.Union(new SemanticType[]{lhs,rhs});
		}
	}

	/**
	 * Provides a simplistic form of type intersect which, in some cases, does
	 * slightly better than simply creating a new intersection. For example,
	 * intersecting <code>int</code> with <code>int</code> will return
	 * <code>int</code> rather than <code>int&int</code>.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	protected SemanticType intersectionHelper(SemanticType lhs, SemanticType rhs) {
		if(lhs.equals(rhs)) {
			return lhs;
		} else if(lhs instanceof Type.Void) {
			return lhs;
		} else if(rhs instanceof Type.Void) {
			return rhs;
		} else {
			return new SemanticType.Intersection(new SemanticType[]{lhs,rhs});
		}
	}

	private <T> T syntaxError(String msg, SyntacticItem e) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntaxError(msg, cu.getEntry(), e);
	}
}
