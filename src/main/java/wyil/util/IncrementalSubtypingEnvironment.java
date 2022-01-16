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
package wyil.util;

import static wyil.lang.WyilFile.TYPE_any;
import static wyil.lang.WyilFile.TYPE_array;
import static wyil.lang.WyilFile.TYPE_bool;
import static wyil.lang.WyilFile.TYPE_byte;
import static wyil.lang.WyilFile.TYPE_existential;
import static wyil.lang.WyilFile.TYPE_function;
import static wyil.lang.WyilFile.TYPE_int;
import static wyil.lang.WyilFile.TYPE_method;
import static wyil.lang.WyilFile.TYPE_nominal;
import static wyil.lang.WyilFile.TYPE_null;
import static wyil.lang.WyilFile.TYPE_property;
import static wyil.lang.WyilFile.TYPE_record;
import static wyil.lang.WyilFile.TYPE_reference;
import static wyil.lang.WyilFile.TYPE_tuple;
import static wyil.lang.WyilFile.TYPE_union;
import static wyil.lang.WyilFile.TYPE_universal;
import static wyil.lang.WyilFile.TYPE_unknown;
import static wyil.lang.WyilFile.TYPE_void;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import wycc.util.ArrayUtils;
import wycc.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.QualifiedName;
import wyil.lang.WyilFile.Template;
import wyil.lang.WyilFile.Type;
import wyil.util.Subtyping.Constraints;
import wyil.util.Subtyping.LowerBoundConstraint;
import wyil.util.Subtyping.UpperBoundConstraint;

/**
 * <p>
 * Provides default implementations for <code>Subtyping.Environment</code>. The
 * intention is that these be overriden to provide different variants (e.g.
 * relaxed subtype operators, etc).
 * </p>
 * <p>
 * <b>(Subtyping)</b> The default subtype operator checks whether one type is a
 * <i>strict subtype</i> of another. Unlike other subtype operators, this takes
 * into account the invariants on types. Consider these two types:
 *
 * <pre>
 * type nat is (int x) where x >= 0
 * type pos is (nat x) where x > 0
 * type tan is (int x) where x >= 0
 * </pre>
 *
 * In this case, we have <code>nat <: int</code> since <code>int</code> is
 * explicitly included in the definition of <code>nat</code>. Observe that this
 * applies transitively and, hence, <code>pos <: nat</code>. But, it does not
 * follow that <code>nat <: int</code> and, likewise, that
 * <code>pos <: nat</code>. Likewise, <code>nat <: tan</code> does not follow
 * (despite this being actually true) since we cannot reason about invariants.
 * </p>
 * <p>
 * <b>(Binding)</b> An important task is computing a "binding" between a
 * function, method or property declaration and a given set of concrete
 * arguments types. For example, consider:
 * </p>
 *
 * <pre>
 * template<T>
 * function get(T[] items, int i) -> T:
 *    return items[i]
 *
 *  function f(int[] items) -> int:
 *     return get(items,0)
 * </pre>
 *
 * <p>
 * At the point of the invocation for <code>get()</code> we must resolve the
 * declared type <code>function(T[],int)->(T)</code> against the declared
 * parameter types <code>(int[],int)</code>, yielding a binding
 * <code>T=int</code>.
 * </p>
 * <p>
 * Computing the binding between two types is non-trivial in Whiley. In addition
 * to template arguments (as above), we must handle lifetime arguments. For
 * example:
 * </p>
 *
 * <pre>
 * method <a> m(&a:int x) -> int:
 *    return *a
 *
 * ...
 *   &this:int ptr = new 1
 *   return m(ptr)
 * </pre>
 * <p>
 * At the invocation to <code>m()</code>, we need to infer the binding
 * <code>a=this</code>. A major challenge is the presence of union types. For
 * example, consider this binding problem:
 * </p>
 *
 * <pre>
 * template<S,T>
 * function f(S x, S|T y) -> S|T:
 *    return y
 *
 * function g(int p, bool|int q) -> (bool|int r):
 *    return f(p,q)
 * </pre>
 * <p>
 * At the invocation to <code>f</code> we must generate the binding
 * <code>S=int,T=bool</code>. When binding <code>bool|int</code> against
 * <code>S|T</code> we need to consider both cases where
 * <code>S=bool,T=int</code> and <code>S=int,T=bool</code>. Otherwise, we cannot
 * be sure to consider the right combination.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class IncrementalSubtypingEnvironment implements Subtyping.Environment {
	/**
	 * A constant representing the set of empty constraints.
	 */
	public final SubtypeConstraints TOP = new SubtypeConstraints(this);

	/**
	 * The empty constraint set which is, by construction, invalid.
	 */
	public final SubtypeConstraints BOTTOM = new SubtypeConstraints(0, (ConcreteSolution) null,
			true, null, null);

	/**
	 * A constant representing the set of empty constraint sets.
	 */
	public final IncrementalSubtypingEnvironment.AbstractConstraintsSet EMPTY_CONSTRAINT_SET = new AbstractConstraintsSet(TOP);

	/**
	 * A constant representing an invalidated set of constraints
	 */
	public final IncrementalSubtypingEnvironment.AbstractConstraintsSet BOTTOM_CONSTRAINT_SET = new AbstractConstraintsSet(BOTTOM);

	protected final Map<String, String[]> withins;

	public IncrementalSubtypingEnvironment() {
		this.withins = new HashMap<>();
	}

	public IncrementalSubtypingEnvironment(Map<String, String[]> withins) {
		this.withins = new HashMap<>(withins);
	}

	@Override
	public boolean isSatisfiableSubtype(Type t1, Type t2) {
		Subtyping.Constraints constraints = isSubtype(t1, t2);
		return constraints.isSatisfiable();
	}

	@Override
	public Subtyping.Constraints isSubtype(Type t1, Type t2) {
		return isSubtype(t1, t2, null);
	}

	@Override
	public boolean isEmpty(QualifiedName nid, Type type) {
		return isContractive(nid, type, null);
	}

	// ===========================================================================
	// Contractivity
	// ===========================================================================

	@Override
	public boolean isWithin(String inner, String outer) {
		//
		if (outer.equals("*") || inner.equals(outer)) {
			// Cover easy cases first
			return true;
		} else {
			String[] outers = withins.get(inner);
			return outers != null && (ArrayUtils.firstIndexOf(outers, outer) >= 0);
		}
	}

	@Override
	public IncrementalSubtypingEnvironment declareWithin(String inner, String... outers) {
		IncrementalSubtypingEnvironment nenv = new IncrementalSubtypingEnvironment(withins);
		nenv.withins.put(inner, outers);
		return nenv;
	}

	@Override
	public String toString() {
		String r = "{";
		boolean firstTime = true;
		for (Map.Entry<String, String[]> w : withins.entrySet()) {
			if (!firstTime) {
				r += ", ";
			}
			firstTime = false;
			r = r + w.getKey() + " < " + Arrays.toString(w.getValue());
		}
		return r + "}";
	}

	// ===========================================================================
	// Contractivity
	// ===========================================================================

	/**
	 * Provides a helper implementation for isContractive.
	 *
	 * @param name
	 * @param type
	 * @param visited
	 * @return
	 */
	static boolean isContractive(QualifiedName name, Type type, HashSet<QualifiedName> visited) {
		switch (type.getOpcode()) {
		case TYPE_void:
		case TYPE_null:
		case TYPE_bool:
		case TYPE_int:
		case TYPE_property:
		case TYPE_byte:
		case TYPE_universal:
		case TYPE_unknown:
		case TYPE_function:
		case TYPE_method:
			return true;
		case TYPE_reference: {
			Type.Reference t = (Type.Reference) type;
			return isContractive(name, t.getElement(), visited);
		}
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			return isContractive(name, t.getElement(), visited);
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> fields = t.getFields();
			for (int i = 0; i != fields.size(); ++i) {
				if (isContractive(name, fields.get(i).getType(), visited)) {
					return true;
				}
			}
			return false;
		}
		case TYPE_union: {
			Type.Union c = (Type.Union) type;
			for (int i = 0; i != c.size(); ++i) {
				if (isContractive(name, c.get(i), visited)) {
					return true;
				}
			}
			return false;
		}
		default:
		case TYPE_nominal:
			Type.Nominal n = (Type.Nominal) type;
			Decl.Link<Decl.Type> link = n.getLink();
			Decl.Type decl = link.getTarget();
			QualifiedName nid = decl.getQualifiedName();
			if (nid.equals(name)) {
				// We have identified a non-contractive type.
				return false;
			} else if (visited != null && visited.contains(nid)) {
				// NOTE: this identifies a type (other than the one we are looking for) which is
				// not contractive. It may seem odd then, that we pretend it is in fact
				// contractive. The reason for this is simply that we cannot tell here with the
				// type we are interested in is contractive or not. Thus, to improve the error
				// messages reported we ignore this non-contractiveness here (since we know
				// it'll be caught down the track anyway).
				return true;
			} else {
				// Lazily construct the visited set as, in the vast majority of cases, this is
				// never required.
				visited = (visited != null) ? visited : new HashSet<>();
				// Add the name to prevent infinite loops
				visited.add(nid);
			}
			return isContractive(name, decl.getType(), visited);
		}
	}

	// ===========================================================================
	// Subtyping
	// ===========================================================================

	/**
	 * A subtype operator aimed at checking whether one type is a <i>strict
	 * subtype</i> of another. Unlike other subtype operators, this takes into
	 * account the invariants on types. Consider these two types:
	 *
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * type pos is (nat x) where x > 0
	 * type tan is (int x) where x >= 0
	 * </pre>
	 *
	 * In this case, we have <code>nat <: int</code> since <code>int</code> is
	 * explicitly included in the definition of <code>nat</code>. Observe that this
	 * applies transitively and, hence, <code>pos <: nat</code>. But, it does not
	 * follow that <code>nat <: int</code> and, likewise, that
	 * <code>pos <: nat</code>. Likewise, <code>nat <: tan</code> does not follow
	 * (despite this being actually true) since we cannot reason about invariants.
	 *
	 * @author David J. Pearce
	 *
	 */
	protected Subtyping.Constraints isSubtype(Type t1, Type t2, BinaryRelation<Type> cache) {
		final Subtyping.Constraints result;
		// FIXME: only need to check for coinductive case when both types are recursive.
		// If either is not recursive, then are guaranteed to eventually terminate.
		if (cache != null && cache.get(t1, t2)) {
			return TOP;
		} else if (cache == null) {
			// Lazily construct cache.
			cache = new BinaryRelation.HashSet<>();
		}
		cache.set(t1, t2, true);
		// Normalise opcodes to align based on class
		int t1_opcode = normalise(t1.getOpcode());
		int t2_opcode = normalise(t2.getOpcode());
		//
		if (t1_opcode == t2_opcode) {
			switch (t1_opcode) {
			case TYPE_any:
			case TYPE_void:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				return TOP;
			case TYPE_array:
				result = isSubtype((Type.Array) t1, (Type.Array) t2, cache);
				break;
			case TYPE_tuple:
				result = isSubtype((Type.Tuple) t1, (Type.Tuple) t2, cache);
				break;
			case TYPE_record:
				result = isSubtype((Type.Record) t1, (Type.Record) t2, cache);
				break;
			case TYPE_nominal:
				result = isSubtype((Type.Nominal) t1, (Type.Nominal) t2, cache);
				break;
			case TYPE_union:
				result = isSubtype(t1, (Type.Union) t2, cache);
				break;
			case TYPE_reference:
				result = isSubtype((Type.Reference) t1, (Type.Reference) t2, cache);
				break;
			case TYPE_method:
			case TYPE_function:
			case TYPE_property:
				result = isSubtype((Type.Callable) t1, (Type.Callable) t2, cache);
				break;
			case TYPE_universal:
				result = isSubtype((Type.Universal) t1, (Type.Universal) t2, cache);
				break;
			case TYPE_existential:
				result = isSubtype(t1, (Type.Existential) t2, cache);
				break;
			default:
				throw new IllegalArgumentException("unexpected type encountered: " + t1);
			}
		} else if (t1_opcode == TYPE_any || t2_opcode == TYPE_void) {
			result = TOP;
		} else if (t1_opcode == TYPE_existential) {
			result = isSubtype((Type.Existential) t1, t2, cache);
		} else if (t2_opcode == TYPE_existential) {
			result = isSubtype(t1, (Type.Existential) t2, cache);
		} else if (t2_opcode == TYPE_nominal && ((Type.Nominal) t2).isAlias()) {
			// NOTE: this case is curious, but necessary. Explicit syntax for aliases would
			// help here.
			result = isSubtype(t1, (Type.Nominal) t2, cache);
		} else if (t2_opcode == TYPE_union) {
			result = isSubtype(t1, (Type.Union) t2, cache);
		} else if (t1_opcode == TYPE_union) {
			result = isSubtype((Type.Union) t1, t2, cache);
		} else if (t2_opcode == TYPE_nominal) {
			result = isSubtype(t1, (Type.Nominal) t2, cache);
		} else if (t1_opcode == TYPE_nominal) {
			result = isSubtype((Type.Nominal) t1, (Type.Atom) t2, cache);
		} else {
			// Nothing else works except void
			result = BOTTOM;
		}
		// Reset cache
		cache.set(t1, t2, false);
		//
		return result;
	}

	protected Subtyping.Constraints isSubtype(Type.Array t1, Type.Array t2, BinaryRelation<Type> cache) {
		return isSubtype(t1.getElement(), t2.getElement(), cache);
	}

	protected Subtyping.Constraints isSubtype(Type.Tuple t1, Type.Tuple t2, BinaryRelation<Type> cache) {
		Subtyping.Constraints constraints = TOP;
		// Check elements one-by-one
		for (int i = 0; i != t1.size(); ++i) {
			Subtyping.Constraints ith = isSubtype(t1.get(i), t2.get(i), cache);
			if (ith == null || constraints == null) {
				return BOTTOM;
			} else {
				constraints = constraints.intersect(ith);
			}
		}
		// Done
		return constraints;
	}

	protected Subtyping.Constraints isSubtype(Type.Record t1, Type.Record t2, BinaryRelation<Type> cache) {
		Tuple<Type.Field> t1_fields = t1.getFields();
		Tuple<Type.Field> t2_fields = t2.getFields();
		// Sanity check number of fields are reasonable.
		if (t1_fields.size() > t2_fields.size()) {
			return BOTTOM;
		} else if (t2.isOpen() && !t1.isOpen()) {
			return BOTTOM;
		} else if (!t1.isOpen() && t1_fields.size() != t2.getFields().size()) {
			return BOTTOM;
		}
		Subtyping.Constraints constraints = TOP;
		// NOTE: the following is O(n^2) but, in reality, will be faster than the
		// alternative (sorting fields into an array). That's because we expect a very
		// small number of fields in practice.
		for (int i = 0; i != t1_fields.size(); ++i) {
			Type.Field f1 = t1_fields.get(i);
			boolean matched = false;
			for (int j = 0; j != t2_fields.size(); ++j) {
				Type.Field f2 = t2_fields.get(j);
				if (f1.getName().equals(f2.getName())) {
					Subtyping.Constraints other = isSubtype(f1.getType(), f2.getType(), cache);
					// Matched field
					matched = true;
					constraints = constraints.intersect(other);
				}
			}
			// Check we actually matched the field!
			if (!matched) {
				return BOTTOM;
			}
		}
		// Done
		return constraints;
	}

	protected Subtyping.Constraints isSubtype(Type.Reference t1, Type.Reference t2, BinaryRelation<Type> cache) {
		Subtyping.Constraints first = isWidthSubtype(t1.getElement(), t2.getElement(), cache);
		// Sanity check what's going on
		if (first.isSatisfiable()) {
			return first;
		} else {
			return areEquivalent(t1.getElement(), t2.getElement(), cache);
		}
	}

	protected Subtyping.Constraints isWidthSubtype(Type t1, Type t2, BinaryRelation<Type> cache) {
		// NOTE: this method could be significantly improved by allowing recursive width
		// subtyping.
		if (t1 instanceof Type.Nominal) {
			Type.Nominal n1 = (Type.Nominal) t1;
			return isWidthSubtype(n1.getConcreteType(), t2, cache);
		} else if (t2 instanceof Type.Nominal) {
			Type.Nominal n2 = (Type.Nominal) t2;
			return isWidthSubtype(t1, n2.getConcreteType(), cache);
		} else if (t1 instanceof Type.Record && t2 instanceof Type.Record) {
			Subtyping.Constraints constraints = TOP;
			Type.Record r1 = (Type.Record) t1;
			Type.Record r2 = (Type.Record) t2;
			Tuple<Type.Field> r1_fields = r1.getFields();
			Tuple<Type.Field> r2_fields = r2.getFields();
			if (r1.isOpen() && r1_fields.size() <= r2_fields.size()) {
				for (int i = 0; i != r1_fields.size(); ++i) {
					Type.Field f1 = r1_fields.get(i);
					Type.Field f2 = r2_fields.get(i);
					if (!f1.getName().equals(f2.getName())) {
						// Fields have differing names
						return BOTTOM;
					}
					Subtyping.Constraints other = areEquivalent(f1.getType(), f2.getType(), cache);
					constraints = constraints.intersect(other);
				}
				return constraints;
			}
		}
		return BOTTOM;
	}

	protected Subtyping.Constraints isSubtype(Type.Callable t1, Type.Callable t2, BinaryRelation<Type> cache) {
		Type t1_params = t1.getParameter();
		Type t2_params = t2.getParameter();
		Type t1_return = t1.getReturn();
		Type t2_return = t2.getReturn();
		// Eliminate easy cases first
		if(!isCallableSubtype(t1.getOpcode(),t2.getOpcode())) {
			return BOTTOM;
		}
		// Check parameters (contra-variant)
		Subtyping.Constraints c_params = isSubtype(t2_params, t1_params, cache);
		// Check returns (co-variant)
		Subtyping.Constraints c_returns = isSubtype(t1_return, t2_return, cache);
		// Done
		return c_params.intersect(c_returns);
	}

	protected Subtyping.Constraints isSubtype(Type.Universal t1, Type.Universal t2,
			BinaryRelation<Type> cache) {
		if (t1.getOperand().equals(t2.getOperand())) {
			return TOP;
		} else {
			return BOTTOM;
		}
	}

	protected Subtyping.Constraints isSubtype(Type.Existential t1, Type t2, BinaryRelation<Type> cache) {
		if(t2 instanceof Type.Void) {
			return TOP;
		} else {
			return new SubtypeConstraints(this, new LowerBoundConstraint(this, t1, t2));
		}
	}

	protected Subtyping.Constraints isSubtype(Type t1, Type.Existential t2, BinaryRelation<Type> cache) {
		if(t1 instanceof Type.Any) {
			return TOP;
		} else {
			return new SubtypeConstraints(this, new UpperBoundConstraint(this, t1, t2));
		}
	}

	protected Subtyping.Constraints isSubtype(Type t1, Type.Union t2, BinaryRelation<Type> cache) {
		Subtyping.Constraints constraints = TOP;
		for (int i = 0; i != t2.size(); ++i) {
			Subtyping.Constraints other = isSubtype(t1, t2.get(i), cache);
			constraints = constraints.intersect(other);
		}
		return constraints;
	}

	protected Subtyping.Constraints isSubtype(Type.Union t1, Type t2, BinaryRelation<Type> cache) {
		Subtyping.Constraints winner = BOTTOM;
		// Select the winning bound. In principle, we actually could do better here but
		// this would mean allowing for disjunctions of constraints.
		for (int i = 0; i != t1.size(); ++i) {
			Subtyping.Constraints ith = isSubtype(t1.get(i), t2, cache);
			// Check whether we found a match or not
			if(ith != BOTTOM && winner != BOTTOM) {
				return BOTTOM;
			} else if(ith != BOTTOM) {
				winner = ith;
			}
		}
		//
		return winner;
	}

	protected Subtyping.Constraints isSubtype(Type.Nominal t1, Type.Nominal t2, BinaryRelation<Type> cache) {
		Decl.Type d1 = t1.getLink().getTarget();
		Decl.Type d2 = t2.getLink().getTarget();
		//
		Tuple<Expr> t1_invariant = d1.getInvariant();
		Tuple<Expr> t2_invariant = d2.getInvariant();
		// Dispatch easy cases
		if (d1 == d2) {
			// NOTE: at this point it is possible to only consider the template arguments
			// provided. However, doing this requires knowledge of the variance requirements
			// for each template parameter position. Such information is currently
			// unavailable though, in principle, could be inferred.
			Tuple<Template.Variable> template = d1.getTemplate();
			Tuple<Type> t1_params = t1.getParameters();
			Tuple<Type> t2_params = t2.getParameters();
			Subtyping.Constraints constraints = TOP;
			for (int i = 0; i != template.size(); ++i) {
				Template.Variable ith = template.get(i);
				Template.Variance v = ith.getVariance();
				Type t1_ith_param = t1_params.get(i);
				Type t2_ith_param = t2_params.get(i);
				// Apply constraints
				if (v == Template.Variance.COVARIANT || v == Template.Variance.INVARIANT) {
					constraints = constraints.intersect(isSubtype(t1_ith_param, t2_ith_param, cache));
				}
				if (v == Template.Variance.CONTRAVARIANT || v == Template.Variance.INVARIANT) {
					constraints = constraints.intersect(isSubtype(t2_ith_param, t1_ith_param, cache));
				}
			}
			return constraints;
		} else if(isAncestorOf(t1,t2)) {
			return isSubtype(t1.getConcreteType(), t2.getConcreteType());
		} else {
			boolean left = isSubtype(t1_invariant, t2_invariant);
			boolean right = isSubtype(t2_invariant, t1_invariant);
			if (left || right) {
				Type tt1 = left ? t1.getConcreteType() : t1;
				Type tt2 = right ? t2.getConcreteType() : t2;
				return isSubtype(tt1, tt2, cache);
			} else {
				return BOTTOM;
			}
		}
	}

	/**
	 * Check whether a nominal type is a subtype of an atom (i.e. not a nominal or
	 * union). For example, <code>int :> nat</code> or <code>{nat f} :> rec</code>.
	 * This is actually easy as an invariants on the nominal type can be ignored
	 * (since they already imply it is a subtype).
	 *
	 * @param t1
	 * @param t2
	 * @param lifetimes
	 * @return
	 */
	protected Subtyping.Constraints isSubtype(Type t1, Type.Nominal t2, BinaryRelation<Type> cache) {
		return isSubtype(t1, t2.getConcreteType(), cache);
	}

	/**
	 * Check whether a nominal type is a supertype of an atom (i.e. not a nominal or
	 * union). For example, <code>int <: nat</code> or <code>{nat f} <: rec</code>.
	 * This is harder because the invariant cannot be reasoned about. In fact, the
	 * only case where this can hold true is when there is no invariant.
	 *
	 * @param t1
	 * @param t2
	 * @param lifetimes
	 * @return
	 */
	protected Subtyping.Constraints isSubtype(Type.Nominal t1, Type t2, BinaryRelation<Type> cache) {
		//
		Decl.Type d1 = t1.getLink().getTarget();
		Tuple<Expr> t1_invariant = d1.getInvariant();
		// Dispatch easy cases
		if (isSubtype(t1_invariant, EMPTY_INVARIANT)) {
			return isSubtype(t1.getConcreteType(), t2, cache);
		} else {
			return BOTTOM;
		}
	}

	/**
	 * Determine whether one invariant is a subtype of another. In other words, the
	 * subtype invariant implies the supertype invariant.
	 *
	 * @param lhs The "super" type
	 * @param rhs The "sub" type
	 * @return
	 */
	protected boolean isSubtype(Tuple<Expr> lhs, Tuple<Expr> rhs) {
		// NOTE: in principle, we could potentially do more here.
		return lhs.size() == 0 || lhs.equals(rhs);
	}

	/**
	 * Determine whether two types are "equivalent" or not.
	 *
	 * @param t1
	 * @param t2
	 * @param lifetimes
	 * @return
	 */
	private Subtyping.Constraints areEquivalent(Type t1, Type t2, BinaryRelation<Type> cache) {
		// NOTE: this is a temporary solution.
		Subtyping.Constraints left = isSubtype(t1, t2, cache);
		Subtyping.Constraints right = isSubtype(t2, t1, cache);
		//
		return left.intersect(right);
	}

	private static boolean isCallableSubtype(int lhs, int rhs) {
		switch(lhs) {
		case TYPE_method:
			return true;
		case TYPE_function:
			return rhs == TYPE_function || rhs == TYPE_property;
		default:
			return rhs == TYPE_property;
		}
	}


	// ===========================================================================
	// AbstractConstraintsSet
	// ===========================================================================

	/**
	 * Provides a straightforward implementation of <code>Typing</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class AbstractConstraintsSet implements Constraints.Set {
		/**
		 * Square matrix of typing environments
		 */
		private final Subtyping.Constraints[] rows;

		public AbstractConstraintsSet(Subtyping.Constraints... rows) {
			this.rows = rows;
		}

		/**
		 * Return flag indicating whether there are no valid typings remaining, or not.
		 * This is equivalent to <code>height() == 0</code>.
		 *
		 * @return
		 */
		@Override
		public boolean empty() {
			return rows.length == 0;
		}

		/**
		 * Return the number of typings that remain under consideration. If this is
		 * zero, then there are no valid typings and the original expression cannot be
		 * typed. On the other hand, if there is more than one valid typing (at the end)
		 * then the original expression is ambiguous.
		 *
		 * @return
		 */
		@Override
		public int height() {
			return rows.length;
		}

		@Override
		public Subtyping.Constraints get(int ith) {
			return rows[ith];
		}

		@Override
		public Constraints.Set fold(Comparator<Subtyping.Constraints> comparator) {
			if (rows.length <= 1) {
				return this;
			} else {
				Subtyping.Constraints[] nrows = Arrays.copyOf(rows, rows.length);
				for (int i = 0; i != nrows.length; ++i) {
					Subtyping.Constraints ith = nrows[i];
					if (ith == null) {
						continue;
					}
					for (int j = i + 1; j < nrows.length; ++j) {
						Subtyping.Constraints jth = nrows[j];
						if (jth == null) {
							continue;
						}
						int c = comparator.compare(ith, jth);
						if (c < 0) {
							nrows[j] = null;
						} else if (c > 0) {
							nrows[i] = null;
						}
					}
				}
				nrows = ArrayUtils.removeAll(nrows, null);
				return new AbstractConstraintsSet(nrows);
			}
		}

		/**
		 * Apply a given function to all rows of the typing matrix producing a
		 * potentially updated set of typing constraints. As part of this process, rows
		 * may be invalidated if they fail to meet some criteria.
		 *
		 * @param fn The mapping function which is applied to each row. This returns
		 *           either an updated row, or <code>null</code>, if the row is
		 *           invalidated.
		 * @return
		 */
		@Override
		public Constraints.Set map(Function<Subtyping.Constraints, Subtyping.Constraints> fn) {
			Subtyping.Constraints[] nrows = rows;
			//
			for (int i = 0; i < rows.length; i = i + 1) {
				final Subtyping.Constraints before = nrows[i];
				Subtyping.Constraints after = fn.apply(before);
				if (before == after) {
					// No change, so do nothing
					continue;
				} else if (nrows == rows) {
					// something changed, so clone
					nrows = Arrays.copyOf(rows, rows.length);
				}
				nrows[i] = after.isSatisfiable() ? after : null;
			}
			// Remove all invalid rows
			nrows = ArrayUtils.removeAll(nrows, null);
			// Create new typing
			return new AbstractConstraintsSet(nrows);
		}

		/**
		 * Project each row of the typing matrix into zero or more rows, thus producing
		 * a potentially updated constraint set. As part of this process, rows may be
		 * added or removed based on various criteria.
		 *
		 * @param fn The mapping function which is applied to each row. This returns
		 *           zero or more updated rows. Note that it should not return
		 *           <code>null</code>.
		 * @return
		 */
		@Override
		public Constraints.Set project(Function<Subtyping.Constraints, Subtyping.Constraints[]> fn) {
			ArrayList<Subtyping.Constraints> nrows = new ArrayList<>();
			//
			for (int i = 0; i < rows.length; i = i + 1) {
				final Subtyping.Constraints before = rows[i];
				Subtyping.Constraints[] after = fn.apply(before);
				for (int j = 0; j != after.length; ++j) {
					Subtyping.Constraints jth = after[j];
					if (jth.isSatisfiable()) {
						nrows.add(jth);
					}
				}
			}
			//
			Subtyping.Constraints[] arr = nrows.toArray(new Subtyping.Constraints[nrows.size()]);
			// Create new typing
			return new AbstractConstraintsSet(arr);
		}

		@Override
		public void foreach(Consumer<Subtyping.Constraints> fn) {
			for (int i = 0; i != rows.length; ++i) {
				fn.accept(rows[i]);
			}
		}

		@Override
		public String toString() {
			if (rows.length == 0) {
				return "_|_";
			} else {
				String r = "";
				for (Subtyping.Constraints row : rows) {
					r = r + row;
				}
				return r;
			}
		}
	}

	// ===========================================================================
	// Solution
	// ===========================================================================

	/**
	 * A constant representing an invalid solution to a set of subtyping
	 * constraints. For example, given a solution <code>[?0 :> int]</code> if we
	 * then constrain <code>bool :> ?0</code> we end up with an invalid solution.
	 */
	public final IncrementalSubtypingEnvironment.ConcreteSolution INVALID_SOLUTION = new ConcreteSolution(this,null,null);

	/**
	 * A constant representing an empty solution to a set of subtyping constraints.
	 */
	public final IncrementalSubtypingEnvironment.ConcreteSolution EMPTY_SOLUTION = new ConcreteSolution(this);

	/**
	 * Represents a current best solution for a given typing problem. Specifically,
	 * for a given set of <code>n</code> type variables, each variable has given
	 * concrete <i>upper</i> and <i>lower</i> bounds. For example, we might have
	 * <code>[?0 :> int, int :> ?01]</code>.
	 *
	 * Observe that every type within the lower and upper bounds are concrete (i.e.
	 * do not contain existentials).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class ConcreteSolution implements Constraints.Solution {
		/**
		 * The environment is needed in order to determine when this solution becomes
		 * invalid, and also to combine upper and lower bounds using the
		 * <code>leastUpperBound</code> and <code>greatestLowerBound</code> operators.
		 */
		private final IncrementalSubtypingEnvironment environment;
		/**
		 * The current set of upper bounds for given all known type variables.
		 */
		private final Type[] upperBounds;
		/**
		 * The current set of lower bounds for given all known type variables.
		 */
		private final Type[] lowerBounds;

		public ConcreteSolution(IncrementalSubtypingEnvironment environment) {
			this.environment = environment;
			this.upperBounds = new Type[0];
			this.lowerBounds = new Type[0];
		}

		private ConcreteSolution(IncrementalSubtypingEnvironment environment, Type[] upperBounds, Type[] lowerBounds) {
			this.environment = environment;
			this.upperBounds = upperBounds;
			this.lowerBounds = lowerBounds;
		}

		@Override
		public Type get(int i) {
			Type f = floor(i);
			if(f instanceof Type.Void) {
				return ceil(i);
			} else {
				return f;
			}
		}

		@Override
		public Type floor(int i) {
			if (lowerBounds == null || i >= lowerBounds.length) {
				return Type.Void;
			} else {
				return lowerBounds[i];
			}
		}

		@Override
		public Type ceil(int i) {
			if (upperBounds == null || i >= upperBounds.length) {
				return Type.Any;
			} else {
				return upperBounds[i];
			}
		}

		@Override
		public int size() {
			return lowerBounds == null ? 0 : lowerBounds.length;
		}

		/**
		 * Check whether a given solution is fully satisfied or not. In short, whether
		 * or not any variables remain which have neither an upper or lower bound. Such
		 * variables are problematic because we cannot determine a valid type for them
		 * (i.e. as neither <code>any</code> nor <code>void</code> are valid types). If
		 * the solution is satisfiable but not yet satisfied, then it means we need to
		 * continue atttempt to find a solution. Note, however, than an unsatisfiable
		 * solution is considered to be satisfied for simplicity.
		 *
		 * @param n
		 * @return
		 */
		@Override
		public boolean isComplete(int n) {
			if(lowerBounds == null) {
				return true;
			} else {
				for (int i = 0; i < n; ++i) {
					Type upper = ceil(i);
					Type lower = floor(i);
					// NOTE: potential performance improvement here to avoid unnecessary subtype
					// checks by only testing bounds which have actually changed.
					if (upper instanceof Type.Any || lower instanceof Type.Void) {
						return false;
					}
				}
				return true;
			}
		}

		/**
		 * Check whether the given solution is satisfiable or not. That is, whether or
		 * not there are any bounds which definitely cannot be satisfied. For example,
		 * <code>{?0 :> int}</code> is satisfiable with <code>?0=int</code>. However,
		 * <code>{bool :> ?0 :> int}</code> is not satisfiable.
		 *
		 * @param n
		 * @param env
		 * @return
		 */
		@Override
		public boolean isUnsatisfiable() {
			return lowerBounds == null;
		}

		/**
		 * Constrain the solution of a given variable with a given (concrete) lower
		 * bound. If the solution becomes invalid, return BOTTOM.
		 *
		 * @param i           Variable to be constrained
		 * @param nLowerBound New lowerbound to constrain with
		 * @return
		 */
		@Override
		public IncrementalSubtypingEnvironment.ConcreteSolution constrain(int i, Type nLowerBound) {
			if(!Subtyping.isConcrete(nLowerBound)) {
				throw new IllegalArgumentException("Upper bound should be concrete");
			} else if(lowerBounds == null) {
				// Intersecting an invalid solution always returns an invalid solution
				return this;
			}
			Type[] nUpperBounds = upperBounds;
			Type[] nLowerBounds = lowerBounds;
			Type lub;
			// Sanity check enough space
			if(i >= nLowerBounds.length) {
				nUpperBounds = expand(nUpperBounds, i+1, Type.Any);
				nLowerBounds = expand(nLowerBounds, i+1, Type.Void);
				// Lowerbound easy as nothing to do
				lub = nLowerBound;
			} else {
				// Compute lower bound
				Type lowerBound = nLowerBounds[i];
				lub = environment.leastUpperBound(lowerBound,nLowerBound);
				// Check whether anything actually changed
				if(lub == lowerBound) {
					// No change in lower bound. Therefore, if solution valid before, it is still
					// valid now.
					return this;
				}
				// Clone lower bounds as will definitely update them
				nLowerBounds = Arrays.copyOf(nLowerBounds, nLowerBounds.length);
			}
			// Sanity check updated solution is still valid
			if (!environment.isSatisfiableSubtype(nUpperBounds[i], lub) || lub instanceof Type.Any) {
				return environment.INVALID_SOLUTION;
			} else {
				nLowerBounds[i] = lub;
				return new ConcreteSolution(environment, nUpperBounds, nLowerBounds);
			}
		}

		/**
		 * Constraint the solution of a given variable with a given (concrete) upper
		 * bound. If the solution becomes invalid, return BOTTOM.
		 *
		 * @param nUpperBound New upperbound to constrain with
		 * @param i           Variable to be constrained
		 * @return
		 */
		@Override
		public IncrementalSubtypingEnvironment.ConcreteSolution constrain(Type nUpperBound, int i) {
			if(!Subtyping.isConcrete(nUpperBound)) {
				throw new IllegalArgumentException("Upper bound should be concrete");
			} else if(lowerBounds == null) {
				// Intersecting an invalid solution always returns an invalid solution
				return this;
			}
			Type[] nUpperBounds = upperBounds;
			Type[] nLowerBounds = lowerBounds;
			Type glb;
			// Sanity check enough space
			if(i >= nLowerBounds.length) {
				nUpperBounds = expand(nUpperBounds, i+1, Type.Any);
				nLowerBounds = expand(nLowerBounds, i+1, Type.Void);
				// Lowerbound easy as nothing to do
				glb = nUpperBound;
			} else {
				// Compute lower bound
				Type upperBound = nUpperBounds[i];
				glb = environment.greatestLowerBound(upperBound,nUpperBound);
				// Check whether anything actually changed
				if(glb == upperBound) {
					// No change in lower bound. Therefore, if solution valid before, it is still
					// valid now.
					return this;
				}
				// Clone lower bounds as will definitely update them
				nUpperBounds = Arrays.copyOf(nUpperBounds, nUpperBounds.length);
			}
			// Sanity check updated solution is still valid
			if(!environment.isSatisfiableSubtype(glb,nLowerBounds[i]) || glb instanceof Type.Void) {
				return environment.INVALID_SOLUTION;
			} else {
				nUpperBounds[i] = glb;
				return new ConcreteSolution(environment, nUpperBounds, nLowerBounds);
			}
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof IncrementalSubtypingEnvironment.ConcreteSolution) {
				IncrementalSubtypingEnvironment.ConcreteSolution s = (IncrementalSubtypingEnvironment.ConcreteSolution) o;
				return Arrays.equals(lowerBounds, s.lowerBounds) && Arrays.equals(upperBounds, s.upperBounds);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(lowerBounds) ^ Arrays.hashCode(upperBounds);
		}

		@Override
		public String toString() {
			if(lowerBounds == null) {
				return "⊥";
			} else {
				String r = "[";
				int n = Math.max(lowerBounds.length, upperBounds.length);
				for (int i = 0; i != n; ++i) {
					if (i != 0) {
						r += ";";
					}
					if(i < upperBounds.length && i < lowerBounds.length && upperBounds[i].equals(lowerBounds[i])) {
						r += "?" + i + "=" + upperBounds[i];
					} else {
						if (i < upperBounds.length) {
							Type upper = upperBounds[i];
							if (!(upper instanceof Type.Any)) {
								r += upper + " :> ";
							}
						}
						r += "?" + i;
						if (i < lowerBounds.length) {
							Type lower = lowerBounds[i];
							if (!(lower instanceof Type.Void)) {
								r += " :> " + lower;
							}
						}
					}
				}
				return r + "]";
			}
		}
	}

	// ===========================================================================
	// Greatest Lower Bound
	// ===========================================================================

	@Override
	public Type greatestLowerBound(Type t1, Type t2) {
		int t1_opcode = normalise(t1.getOpcode());
		int t2_opcode = normalise(t2.getOpcode());
		//
		if(t1_opcode == t2_opcode) {
			switch(t1_opcode) {
			case TYPE_void:
			case TYPE_any:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				return t1;
			case TYPE_array:
				return greatestLowerBound((Type.Array) t1,(Type.Array) t2);
			case TYPE_tuple:
				return greatestLowerBound((Type.Tuple) t1,(Type.Tuple) t2);
			case TYPE_record:
				return greatestLowerBound((Type.Record) t1,(Type.Record) t2);
			case TYPE_reference:
				return greatestLowerBound((Type.Reference) t1,(Type.Reference) t2);
			case TYPE_universal:
				return greatestLowerBound((Type.Universal) t1,(Type.Universal) t2);
			case TYPE_nominal:
				return greatestLowerBound((Type.Nominal) t1,(Type.Nominal) t2);
			case TYPE_method:
			case TYPE_function:
			case TYPE_property:
				return greatestLowerBound((Type.Callable) t1,(Type.Callable) t2);
			case TYPE_union:
				return greatestLowerBound((Type.Union) t1, (Type.Union) t2);
			}
		}
		// Handle special forms for t1
		if(t1_opcode == TYPE_void || t2_opcode == TYPE_any) {
			return t1;
		} else if(t1_opcode == TYPE_any || t2_opcode == TYPE_void) {
			return t2;
		} else if(t1_opcode == TYPE_union) {
			return greatestLowerBound((Type.Union) t1, t2);
		} else if(t2_opcode == TYPE_union) {
			return greatestLowerBound(t1, (Type.Union) t2);
		} else if(t1_opcode == TYPE_nominal) {
			return greatestLowerBound((Type.Nominal) t1, t2);
		} else if(t2_opcode == TYPE_nominal) {
			return greatestLowerBound(t1, (Type.Nominal) t2);
		}
		// Default case, nothing else fits.
		return Type.Void;
	}

	public Type greatestLowerBound(Type.Array t1, Type.Array t2) {
		Type e1 = t1.getElement();
		Type e2 = t2.getElement();
		Type glb = greatestLowerBound(e1,e2);
		//
		if (e1 == glb) {
			return t1;
		} else if (e2 == glb) {
			return t2;
		} else if (glb instanceof Type.Void) {
			return Type.Void;
		} else {
			return new Type.Array(glb);
		}
	}

	public Type greatestLowerBound(Type.Tuple t1, Type.Tuple t2) {
		if (t1.size() == t2.size()) {
			boolean left = true;
			boolean right = true;
			Type[] types = new Type[t1.size()];
			for (int i = 0; i != t1.size(); ++i) {
				Type l = t1.get(i);
				Type r = t2.get(i);
				Type glb = types[i] = greatestLowerBound(l, r);
				left &= (l == glb);
				right &= (r == glb);
			}
			//
			if (left) {
				return t1;
			} else if (right) {
				return t2;
			} else {
				return Type.Tuple.create(types);
			}
		}
		return Type.Void;
	}

	public Type greatestLowerBound(Type.Record t1, Type.Record t2) {
		final Tuple<Type.Field> t1_fields = t1.getFields();
		final Tuple<Type.Field> t2_fields = t2.getFields();
		//
		final int n = t1_fields.size();
		final int m = t2_fields.size();
		// Santise input for simplicity
		if(n < m) {
			return greatestLowerBound(t2,t1);
		} else if(!subset(t2,t2)) {
			return Type.Void;
		} else if (!t2.isOpen() && n != m) {
			return Type.Void;
		}
		// Check matching fields
		boolean left = true;
		boolean right = true;
		Type[] types = new Type[n];
		for(int i=0;i!=types.length;++i) {
			Type.Field f = t1_fields.get(i);
			Type t1f = f.getType();
			Type t2f = t2.getField(f.getName());
			// NOTE: for open records t2f can be null
			Type glb = (t2f == null) ? t1f : greatestLowerBound(t1f, t2f);
			//
			if(glb instanceof Type.Void) {
				return Type.Void;
			} else {
				types[i] = glb;
				left &= (glb == t1f);
				right &= (glb == t2f);
			}
		}
		//
		if(left) {
			return t1;
		} else if(right && m == n) {
			return t2;
		}
		// Create record
		Type.Field[] nFields = new Type.Field[n];
		for(int i=0;i!=nFields.length;++i) {
			Type.Field f = t1_fields.get(i);
			nFields[i] = new Type.Field(f.getName(),types[i]);
		}
		return new Type.Record(t1.isOpen() & t2.isOpen(),new Tuple<>(nFields));
	}

	public Type greatestLowerBound(Type.Reference t1, Type.Reference t2) {
		Type e1 = t1.getElement();
		Type e2 = t2.getElement();
		if(isSatisfiableSubtype(e1,e2) && isSatisfiableSubtype(e2,e1)) {
			return t1;
		} else {
			return Type.Void;
		}
	}

	public Type greatestLowerBound(Type.Universal t1, Type.Universal t2) {
		if (t1.getOperand().get().equals(t2.getOperand().get())) {
			return t1;
		} else {
			return Type.Void;
		}
	}

	public Type greatestLowerBound(Type.Callable t1, Type.Callable t2) {
		int t1_opcode = t1.getOpcode();
		Type t1_param = t1.getParameter();
		Type t1_return = t1.getReturn();
		int t2_opcode = t2.getOpcode();
		Type t2_param = t2.getParameter();
		Type t2_return = t2.getReturn();
		//
		Type param = leastUpperBound(t1_param,t2_param);
		Type ret = greatestLowerBound(t1_return,t2_return);
		int opcode = greatestLowerBound(t1_opcode, t2_opcode);
		//
		if(t1_param == param && t1_return == ret && t1_opcode == opcode) {
			return t1;
		} else if(t2_param == param && t2_return == ret && t2_opcode == opcode) {
			return t2;
		} else if(param instanceof Type.Void || ret instanceof Type.Void) {
			return Type.Void;
		} else if(opcode == TYPE_property){
			return new Type.Property(param, ret);
		} else if(opcode == TYPE_function) {
			return new Type.Function(param, ret);
		} else if(t1 instanceof Type.Method && t2 instanceof Type.Method) {
			throw new IllegalArgumentException("IMPLEMENT ME");
		} else {
			return Type.Void;
		}
	}

	private static int greatestLowerBound(int lhs, int rhs) {
		if(lhs == TYPE_method || rhs == TYPE_method) {
			return TYPE_method;
		} else if(lhs == TYPE_function || rhs == TYPE_function) {
			return TYPE_function;
		} else {
			return TYPE_property;
		}
	}

	public Type greatestLowerBound(Type.Union t1, Type.Union t2) {
		// NOTE: this could be made more efficient. For example, by sorting bounds.
		Type t = greatestLowerBound(t1, (Type) t2);
		// FIXME: what an ugly solution :(
		if (t.equals(t1)) {
			return t1;
		} else if (t.equals(t2)) {
			return t2;
		} else {
			return t;
		}
	}

	public Type greatestLowerBound(Type.Nominal t1, Type.Nominal t2) {
		Decl.Type d1 = t1.getLink().getTarget();
		Decl.Type d2 = t2.getLink().getTarget();
		// Determine whether alias or not.
		final boolean d1_alias = t1.isAlias();
		final boolean d2_alias = t2.isAlias();
		//
		if (isSatisfiableSubtype(t2, t1)) {
			return t1;
		} else if (isSatisfiableSubtype(t1, t2)) {
			return t2;
		} else if (d1_alias) {
			return greatestLowerBound(t1.getConcreteType(), t2);
		} else if (d2_alias) {
			return greatestLowerBound(t1, t2.getConcreteType());
		} else {
			return Type.Void;
		}
	}

	public Type greatestLowerBound(Type.Union t1, Type t2) {
		Type[] bounds = new Type[t1.size()];
		boolean changed = false;
		for (int i = 0; i != bounds.length; ++i) {
			Type before = t1.get(i);
			Type after = greatestLowerBound(before, t2);
			bounds[i] = after;
			changed |= (before != after);
		}
		if(changed) {
			return Type.Union.create(bounds);
		} else {
			return t1;
		}
	}

	public Type greatestLowerBound(Type t1, Type.Union t2) {
		Type[] bounds = new Type[t2.size()];
		boolean changed = false;
		for (int i = 0; i != bounds.length; ++i) {
			Type before = t2.get(i);
			Type after = greatestLowerBound(t1, before);
			bounds[i] = after;
			changed |= (before != after);
		}
		if (changed) {
			return Type.Union.create(bounds);
		} else {
			return t2;
		}
	}

	public Type greatestLowerBound(Type.Nominal t1, Type t2) {
		// REQUIRES: !(t2 instanceof Type.Nominal) && !(t2 instanceof Type.Union)
		Decl.Type d1 = t1.getLink().getTarget();
		// Determine whether alias or not.
		final boolean alias = t1.isAlias();
		//
		if(isSatisfiableSubtype(t2, t1)) {
			return t1;
		} else if (alias) {
			return greatestLowerBound(t1.getConcreteType(), t2);
		} else {
			return Type.Void;
		}
	}

	public Type greatestLowerBound(Type t1, Type.Nominal t2) {
		// REQUIRES: !(t1 instanceof Type.Nominal) && !(t1 instanceof Type.Union)
		Decl.Type d2 = t2.getLink().getTarget();
		// Determine whether alias or not.
		final boolean alias = t2.isAlias();
		//
		if (isSatisfiableSubtype(t1, t2)) {
			return t2;
		} else if (alias) {
			return greatestLowerBound(t1, t2.getConcreteType());
		} else {
			return Type.Void;
		}
	}

	// ===========================================================================
	// Least Upper Bound
	// ===========================================================================

	@Override
	public Type leastUpperBound(Type t1, Type t2) {
		int t1_opcode = normalise(t1.getOpcode());
		int t2_opcode = normalise(t2.getOpcode());
		//
		if(t1_opcode == t2_opcode) {
			switch(t1_opcode) {
			case TYPE_void:
			case TYPE_any:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				return t1;
			case TYPE_array:
				return leastUpperBound((Type.Array) t1,(Type.Array) t2);
			case TYPE_tuple:
				return leastUpperBound((Type.Tuple) t1,(Type.Tuple) t2);
			case TYPE_record:
				return leastUpperBound((Type.Record) t1,(Type.Record) t2);
			case TYPE_reference:
				return leastUpperBound((Type.Reference) t1,(Type.Reference) t2);
			case TYPE_universal:
				return leastUpperBound((Type.Universal) t1,(Type.Universal) t2);
			case TYPE_nominal:
				return leastUpperBound((Type.Nominal) t1,(Type.Nominal) t2);
			case TYPE_method:
			case TYPE_function:
			case TYPE_property:
				return leastUpperBound((Type.Callable) t1,(Type.Callable) t2);
			case TYPE_union:
				return leastUpperBound((Type.Union) t1,(Type.Union) t2);
			}
		}

		if (t1_opcode == TYPE_void || t2_opcode == TYPE_any) {
			return t2;
		} else if (t1_opcode == TYPE_any || t2_opcode == TYPE_void) {
			return t1;
		} else if(t1_opcode == TYPE_union && t2_opcode == TYPE_union) {
			return leastUpperBound((Type.Union) t1, (Type.Union) t2);
		} else if(t1_opcode == TYPE_union) {
			return leastUpperBound((Type.Union) t1, t2);
		} else if(t2_opcode == TYPE_union) {
			return leastUpperBound(t1, (Type.Union) t2);
		} else if(t1_opcode == TYPE_nominal && t2_opcode == TYPE_nominal) {
			return leastUpperBound((Type.Nominal) t1, (Type.Nominal) t2);
		} else if(t1_opcode == TYPE_nominal) {
			return leastUpperBound((Type.Nominal) t1, t2);
		} else if(t2_opcode == TYPE_nominal) {
			return leastUpperBound(t1, (Type.Nominal) t2);
		}
		// Default case, nothing else fits.
		return new Type.Union(t1,t2);
	}

	public Type leastUpperBound(Type.Array t1, Type.Array t2) {
		Type e1 = t1.getElement();
		Type e2 = t2.getElement();
		Type lub = leastUpperBound(e1,e2);
		//
		if (e1 == lub) {
			return t1;
		} else if (e2 == lub) {
			return t2;
		} else {
			return new Type.Array(lub);
		}
	}

	public Type leastUpperBound(Type.Tuple t1, Type.Tuple t2) {
		if (t1.size() != t2.size()) {
			return new Type.Union(t1, t2);
		} else {
			boolean left = true;
			boolean right = true;
			Type[] types = new Type[t1.size()];
			for (int i = 0; i != t1.size(); ++i) {
				Type l = t1.get(i);
				Type r = t2.get(i);
				Type lub = types[i] = leastUpperBound(l, r);
				left &= (l == lub);
				right &= (r == lub);
			}
			//
			if (left) {
				return t1;
			} else if (right) {
				return t2;
			} else {
				return Type.Tuple.create(types);
			}
		}
	}

	public Type leastUpperBound(Type.Record t1, Type.Record t2) {
		Tuple<Type.Field> t1_fields = t1.getFields();
		Tuple<Type.Field> t2_fields = t2.getFields();
		// Order fields for simplicity
		if(t1_fields.size() > t2_fields.size()) {
			return leastUpperBound(t2,t1);
		} else if(!subset(t2,t1)) {
			// Some fields in t1 not in t2
			return new Type.Union(t1,t2);
		} else if(!subset(t1,t2)) {
			// Some fields in t1 not in t2
			return new Type.Union(t1,t2);
		}
		// ASSERT: |t1_fields| == |t2_fields|
		Type.Field[] fields = new Type.Field[t1_fields.size()];
		boolean left = true;
		boolean right = true;
		for(int i=0;i!=t1_fields.size();++i) {
			Type.Field f = t1_fields.get(i);
			Type t1f = f.getType();
			Type t2f = t2.getField(f.getName());
			Type lub = leastUpperBound(t1f,t2f);
			fields[i] = new Type.Field(f.getName(),lub);
			left &= (t1f == lub);
			right &= (t2f == lub);
		}
		if(left) {
			return t1;
		} else if(right) {
			return t2;
		} else {
			boolean isOpen = t1.isOpen() | t2.isOpen();
			return new Type.Record(isOpen,new Tuple<>(fields));
		}
	}

	/**
	 * Check that lhs fields are a subset of rhs fields
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public boolean subset(Type.Record lhs, Type.Record rhs) {
		Tuple<Type.Field> lhs_fields = lhs.getFields();
		for(int i=0;i!=lhs_fields.size();++i) {
			Type.Field ith = lhs_fields.get(i);
			if(rhs.getField(ith.getName()) == null) {
				return false;
			}
		}
		return true;
	}

	public Type leastUpperBound(Type.Reference t1, Type.Reference t2) {
		Type e1 = t1.getElement();
		Type e2 = t2.getElement();
		if(isSatisfiableSubtype(e1,e2) && isSatisfiableSubtype(e2,e1)) {
			return t1;
		} else {
			return new Type.Union(t1,t2);
		}
	}

	public Type leastUpperBound(Type.Universal t1, Type.Universal t2) {
		if (t1.getOperand().get().equals(t2.getOperand().get())) {
			return t1;
		} else {
			return new Type.Union(t1, t2);
		}
	}

	public Type leastUpperBound(Type.Callable t1, Type.Callable t2) {
		if(t1.getOpcode() != t2.getOpcode()) {
			return new Type.Union(t1,t2);
		} else {
			Type t1_param = t1.getParameter();
			Type t1_return = t1.getReturn();
			Type t2_param = t2.getParameter();
			Type t2_return = t2.getReturn();
			Type glb_param = leastUpperBound(t1_param,t2_param);
			Type glb_return = leastUpperBound(t1_return,t2_return);
			//
			if(glb_param == t1_param && glb_return == t1_return) {
				return t1;
			} else if(glb_param == t2_param && glb_return == t2_return) {
				return t2;
			} else if(t1 instanceof Type.Function) {
				return new Type.Function(glb_param, glb_return);
			} else if(t1 instanceof Type.Property) {
				return new Type.Property(glb_param, glb_return);
			} else  {
				Type.Method m1 = (Type.Method) t1;
				Type.Method m2 = (Type.Method) t2;
				// FIXME: this is broken
				return new Type.Method(glb_param, glb_return);
			}
		}
	}

	public Type leastUpperBound(Type.Union t1, Type.Union t2) {
		Type[] types = new Type[t1.size() * t2.size()];
		int k = 0;
		for(int i=0;i!=t1.size();++i) {
			Type ith = t1.get(i);
			for(int j=0;j!=t2.size();++j) {
				types[k++] = leastUpperBound(ith,t2.get(j));
			}
		}
		// NOTE: this is needed to adhere to the contract for lub
		Type r = Type.Union.create(types);
		if (r.equals(t1)) {
			return t1;
		} else if (r.equals(t2)) {
			return t2;
		} else {
			//
			return r;
		}
	}

	public Type leastUpperBound(Type.Nominal t1, Type.Nominal t2) {
		Decl.Type d1 = t1.getLink().getTarget();
		Decl.Type d2 = t2.getLink().getTarget();
		// Determine whether alias or not.
		final boolean d1_alias = (d1.getInvariant().size() == 0);
		final boolean d2_alias = (d2.getInvariant().size() == 0);
		//
		if (isSatisfiableSubtype(t1, t2)) {
			return t1;
		} else if (isSatisfiableSubtype(t2, t1)) {
			return t2;
		} else if (d1_alias) {
			return leastUpperBound(t1.getConcreteType(), t2);
		} else if (d2_alias) {
			return leastUpperBound(t1, t2.getConcreteType());
		} else {
			return new Type.Union(t1, t2);
		}
	}

	public Type leastUpperBound(Type.Nominal t1, Type t2) {
		Decl.Type d1 = t1.getLink().getTarget();
		// Determine whether alias or not.
		final boolean d1_alias = (d1.getInvariant().size() == 0);
		//
		if (isSatisfiableSubtype(t2, t1)) {
			return t2;
		}  else if (d1_alias) {
			return leastUpperBound(t1.getConcreteType(), t2);
		} else {
			return new Type.Union(t1, t2);
		}
	}

	public Type leastUpperBound(Type.Union t1, Type t2) {
		// FIXME: this is freakin' broken
		Type[] types = ArrayUtils.removeDuplicates(ArrayUtils.append(t1.getAll(), t2));
		if(types.length == t1.size()) {
			return t1;
		} else {
			return new Type.Union(types);
		}
	}

	public Type leastUpperBound(Type t1, Type.Nominal t2) {
		Decl.Type d2 = t2.getLink().getTarget();
		// Determine whether alias or not.
		final boolean d2_alias = (d2.getInvariant().size() == 0);
		//
		if (isSatisfiableSubtype(t1, t2)) {
			return t2;
		}  else if (d2_alias) {
			return leastUpperBound(t1,t2.getConcreteType());
		} else {
			return new Type.Union(t1, t2);
		}

	}

	public Type leastUpperBound(Type t1, Type.Union t2) {
		// FIXME: this is freakin' broken
		Type[] types = ArrayUtils.removeDuplicates(ArrayUtils.append(t1, t2.getAll()));
		if (types.length == t2.size()) {
			return t2;
		} else {
			return new Type.Union(types);
		}
	}

	// ===============================================================================
	// Type Subtraction
	// ===============================================================================

	/**
	 * Subtract one type from another.
	 *
	 * @param variable
	 * @param lowerBound
	 * @return
	 */
	@Override
	public Type subtract(Type t1, Type t2) {
		return subtract(t1, t2, new BinaryRelation.HashSet<>());
	}

	private Type subtract(Type t1, Type t2, BinaryRelation<Type> cache) {
		// FIXME: only need to check for coinductive case when both types are recursive.
		// If either is not recursive, then are guaranteed to eventually terminate.
		if (cache != null && cache.get(t1, t2)) {
			return Type.Void;
		} else if (cache == null) {
			// Lazily construct cache.
			cache = new BinaryRelation.HashSet<>();
		}
		cache.set(t1, t2, true);
		//
		int t1_opcode = t1.getOpcode();
		int t2_opcode = t2.getOpcode();
		//
		if (t1.equals(t2)) {
			// Easy case
			return Type.Void;
		} else if (t1_opcode == t2_opcode) {
			switch (t1_opcode) {
			case TYPE_void:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				return Type.Void;
			case TYPE_array:
			case TYPE_reference:
			case TYPE_method:
			case TYPE_function:
			case TYPE_property:
				return t1;
			case TYPE_record:
				return subtract((Type.Record) t1, (Type.Record) t2, cache);
			case TYPE_nominal:
				return subtract((Type.Nominal) t1, (Type.Nominal) t2, cache);
			case TYPE_union:
				return subtract((Type.Union) t1, t2, cache);
			default:
				throw new IllegalArgumentException("unexpected type encountered: " + t1);
			}
		} else if (t2_opcode == TYPE_union) {
			return subtract(t1, (Type.Union) t2, cache);
		} else if (t1_opcode == TYPE_union) {
			return subtract((Type.Union) t1, t2, cache);
		} else if (t2_opcode == TYPE_nominal) {
			return subtract(t1, (Type.Nominal) t2, cache);
		} else if (t1_opcode == TYPE_nominal) {
			return subtract((Type.Nominal) t1, t2, cache);
		} else {
			return t1;
		}
	}

	/**
	 * Subtraction of records is possible in a limited number of cases.
	 *
	 * @param t1
	 * @param t2
	 * @return
	 */
	public Type subtract(Type.Record t1, Type.Record t2, BinaryRelation<Type> cache) {
		Tuple<Type.Field> t1_fields = t1.getFields();
		Tuple<Type.Field> t2_fields = t2.getFields();
		if (t1_fields.size() != t2_fields.size() || t1.isOpen() || t1.isOpen()) {
			// Don't attempt anything
			return t1;
		}
		Type.Field[] r_fields = new Type.Field[t1_fields.size()];
		boolean found = false;
		for (int i = 0; i != t1_fields.size(); ++i) {
			Type.Field f1 = t1_fields.get(i);
			Type.Field f2 = t2_fields.get(i);
			if (!f1.getName().equals(f2.getName())) {
				// Give up
				return t1;
			}
			if (!f1.getType().equals(f2.getType())) {
				if (found) {
					return t1;
				} else {
					found = true;
					Type tmp = subtract(f1.getType(), f2.getType(), cache);
					r_fields[i] = new Type.Field(f1.getName(), tmp);
				}
			} else {
				r_fields[i] = f1;
			}
		}
		return new Type.Record(false, new Tuple<>(r_fields));
	}

	public Type subtract(Type.Nominal t1, Type.Nominal t2, BinaryRelation<Type> cache) {
		//
		Decl.Type d1 = t1.getLink().getTarget();
		// NOTE: the following invariant check is essentially something akin to
		// determining whether or not this is a union.
		if (d1.getInvariant().size() == 0) {
			return subtract(t1.getConcreteType(), (Type) t2, cache);
		} else {
			return t1;
		}
	}

	public Type subtract(Type t1, Type.Nominal t2, BinaryRelation<Type> cache) {
		Decl.Type d2 = t2.getLink().getTarget();
		// NOTE: the following invariant check is essentially something akin to
		// determining whether or not this is a union.
		if (d2.getInvariant().size() == 0) {
			return subtract(t1, t2.getConcreteType(), cache);
		} else {
			return t1;
		}
	}

	public Type subtract(Type.Nominal t1, Type t2, BinaryRelation<Type> cache) {
		Decl.Type d1 = t1.getLink().getTarget();
		// NOTE: the following invariant check is essentially something akin to
		// determining whether or not this is a union.
		if (d1.getInvariant().size() == 0) {
			return subtract(t1.getConcreteType(), t2, cache);
		} else {
			return t1;
		}
	}

	public Type subtract(Type t1, Type.Union t2, BinaryRelation<Type> cache) {
		for (int i = 0; i != t2.size(); ++i) {
			t1 = subtract(t1, t2.get(i), cache);
		}
		return t1;
	}

	public Type subtract(Type.Union t1, Type t2, BinaryRelation<Type> cache) {
		Type[] types = new Type[t1.size()];
		for (int i = 0; i != t1.size(); ++i) {
			types[i] = subtract(t1.get(i), t2, cache);
		}
		// Remove any selected cases
		return Type.Union.create(ArrayUtils.removeAll(types, Type.Void));
	}

	// ================================================================================
	// Constrains
	// ================================================================================

	/**
	 * Represents a set of constraints of the form <code>? :> T</code> or
	 * <code>T :> ?</code> and a valid solution. <i>symbolic constraints</i> are
	 * those where <code>T</code> itself contains existential variables. In
	 * contrast, <i>concrete constraints</i> are those where <code>T</code> itself
	 * is concrete. In the current implementation, symbolic constraints are kept as
	 * is whilst concrete constraints are immediately applied to the active
	 * solution.
	 *
	 * @author David J. Pearce
	 *
	 */
	private class SubtypeConstraints implements Subtyping.Constraints {

		/**
		 * A parent pointer to the enclosing environment. This is necessary for access
		 * to the subtype operator.
		 */
		private final IncrementalSubtypingEnvironment environment;
		/**
		 * The set of subtyping constraints that this class embodies. The key is that we
		 * want to able to solve these constraints easily to determine whether or not
		 * they are satisfiable.
		 */
		private Subtyping.Constraint[] constraints;
		/**
		 * A cache of the maximum number of variables used in any of the constraints.
		 * This just helps us know when we have a complete solution or not.
		 */
		private final int nVariables;
		/**
		 * The best current (valid) solution to the given set of constraints. This can
		 * be <code>null</code> if none computed yet. It can also be out-of-date with
		 * respect to the given set of constraints.
		 */
		private Subtyping.Constraints.Solution candidate;
		/**
		 * Dirty flag indicates whether or not the candidate solution is up to date with
		 * the given constraints. This allows us to be lazy in closing over constraints
		 * whilst they are being accumulated through intersection operations prior to an
		 * satisfiability query.
		 */
		private boolean dirty;

		public SubtypeConstraints(IncrementalSubtypingEnvironment environment, Subtyping.Constraint... constraints) {
			this.environment = environment;
			this.constraints = ArrayUtils.removeDuplicates(constraints);
			this.candidate = null;
			this.nVariables = Subtyping.numberOfVariables(constraints);
		}

		private SubtypeConstraints(int n, Subtyping.Constraints.Solution candidate, boolean dirty,
				Subtyping.Constraint[] constraints, IncrementalSubtypingEnvironment environment) {
			this.environment = environment;
			this.constraints = constraints;
			this.candidate = candidate;
			this.dirty = dirty;
			this.nVariables = n;
		}

		@Override
		public boolean isSatisfiable() {
			// NOTE: this is a very simple and largely broken formulation.
			if (constraints == null) {
				return false;
			} else if (dirty || candidate == null) {
				// NOTE: this is a hack
				this.candidate = close(environment.EMPTY_SOLUTION, constraints, environment);
				this.dirty = false;
			}
			return !candidate.isUnsatisfiable();
		}

		@Override
		public int size() {
			if (constraints == null) {
				return 0;
			} else {
				return constraints.length;
			}
		}

		@Override
		public int maxVariable() {
			return nVariables - 1;
		}

		@Override
		public SubtypeConstraints fresh(int n) {
			return new SubtypeConstraints(nVariables + n, candidate, dirty, constraints, environment);
		}

		@Override
		public Subtyping.Constraint get(int ith) {
			return constraints[ith];
		}

		@Override
		public SubtypeConstraints intersect(Subtyping.Constraint... oconstraints) {
			if (constraints == null) {
				return BOTTOM;
			} else {
				// NOTE: performance optimisation possible here
				Subtyping.Constraint[] nconstraints = ArrayUtils.append(constraints, oconstraints);
				// Remove all duplicates
				nconstraints = ArrayUtils.removeDuplicates(nconstraints);
				// Check what happened
				if (nconstraints.length == constraints.length) {
					// Nothing changed!
					return this;
				} else {
					int max = Math.max(nVariables, Subtyping.numberOfVariables(oconstraints));
					return new SubtypeConstraints(max, candidate, true, nconstraints, environment);
				}
			}
		}

		@Override
		public SubtypeConstraints intersect(Subtyping.Constraints other) {
			// FIXME: we could do better here.
			return intersect((SubtypeConstraints) other);
		}

		/**
		 * Intersect this constraint set with another. This essentially determines the
		 * cross-product of rows in the two constraint sets.
		 *
		 * @param other
		 * @return
		 */
		public SubtypeConstraints intersect(SubtypeConstraints other) {
			if (constraints == null || other.constraints == null) {
				return BOTTOM;
			} else {
				// NOTE: performance optimisation possible here
				Subtyping.Constraint[] nconstraints = ArrayUtils.append(constraints, other.constraints);
				// Remove all duplicates
				nconstraints = ArrayUtils.removeDuplicates(nconstraints);
				// Check what happened
				if (nconstraints.length == constraints.length) {
					// Nothing changed!
					return this;
				} else {
					// FIXME: could attempt to update solution here which might provide some
					// performance gains.
					return new SubtypeConstraints(Math.max(nVariables, other.nVariables), candidate, true,
							nconstraints, environment);
				}
			}
		}

		@Override
		public Subtyping.Constraints.Solution solve(int n) {
			return (candidate != null) ? candidate : environment.EMPTY_SOLUTION;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(constraints);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof SubtypeConstraints) {
				SubtypeConstraints r = (SubtypeConstraints) o;
				return Arrays.equals(constraints, r.constraints);
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			if (constraints == null) {
				return "⊥";
			} else {
				String r = "";
				for (Subtyping.Constraint constraint : constraints) {
					if (!r.equals("")) {
						r += ",";
					}
					r += constraint;
				}
				String c = (candidate == null) ? "_" : candidate.toString();
				return "{" + r + "}" + c;
			}
		}

		private Subtyping.Constraints.Solution solve(Subtyping.Constraints.Solution solution) {
			solution = close(solution, constraints, environment);
			// Sanity check whether we've finished or not
			if (!solution.isComplete(nVariables)) {
				// Solution not satisfiable yet. This maybe because there are unsolved
				// variables. To resolve these, we have to find "half-open" variables and close
				// them. Unfortunately, the order in which we do this matters. Therefore, in the
				// worst case, we may have to try all possible orderings.
				for (int i = 0; i < nVariables; i++) {
					Type upper = solution.ceil(i);
					Type lower = solution.floor(i);
					boolean u = (upper instanceof Type.Any);
					boolean l = (lower instanceof Type.Void);
					// Check for half-open cases which allow an obvious guess. Guessing is critical
					// for solving some constraint forms.
					if (!u && l) {
						Subtyping.Constraints.Solution guess = solve(solution.constrain(i, upper));
						if (guess.isComplete(nVariables) && !guess.isUnsatisfiable()) {
							return guess;
						}
					} else if (u && !l) {
						Subtyping.Constraints.Solution guess = solve(solution.constrain(lower, i));
						if (guess.isComplete(nVariables) && !guess.isUnsatisfiable()) {
							return guess;
						}
					}
				}
			}
			return solution;
		}
	}
	// ===============================================================================
	// Constraint Solving
	// ===============================================================================

	/**
	 * <p>
	 * Apply a given set of symbolic constraints to a given solution until a
	 * fixpoint is reached. For example, consider this set of constraints and
	 * solution:
	 * </p>
	 *
	 * <pre>
	 * { #0 :> #1 }[int|bool :> #0; #1 :> int]
	 * </pre>
	 *
	 * <p>
	 * For each constraint, there are two directions of flow: <i>upwards</i> and
	 * <i>downwards</i>. In this case, <code>int|bool</code> flows downwards from
	 * <code>#0</code> to <code>#1</code>. Likewise, <code>int</code> flows upwards
	 * from <code>#1</code> to <code>#0</code>.
	 * </p>
	 *
	 * <p>
	 * To implement flow in a given direction we employ substitution. For example,
	 * to flow downwards through <code>#0 :> #1</code> we substitute <code>#0</code>
	 * for its current upper bound (i.e. <code>int|bool</code>). We then employ the
	 * subtype operator to generate appropriate constraints (or not). In this case,
	 * after substitution we'd have <code>int|bool :> #1</code> which, in fact, is
	 * the constraint that will be reported.
	 * </p>
	 *
	 * @param solution
	 * @param constraints
	 * @param subtyping
	 * @param lifetimes
	 * @return
	 */
	private static int CLOSING_COUNT = 0;

	private static Subtyping.Constraints.Solution close(Subtyping.Constraints.Solution solution,
			Subtyping.Constraint[] constraints, Subtyping.Environment env) {
		//			System.out.println(tab(sTab) + ">>> CLOSING(" + CLOSING_COUNT++ +"): " + solution + " " + Arrays.toString(constraints));
		boolean changed = true;
		int k = 0;
		// NOTE: this is a very HOT loop on benchmarks with large array
		// initialCLOSINGisers.
		// The bound is introduced to prevent against infinite loops.
		while (changed && k < 10) {
			changed = false;
			final Subtyping.Constraints.Solution s = solution;
			//
			for (int i = 0; i < constraints.length; ++i) {
				Subtyping.Constraint ith = constraints[i];
				solution = ith.apply(solution);
				//					System.out.println(tab(sTab) + "SOLUTION[" + k + "](" + i + "/" + constraints.length + "): " + ith + " & " + solution + " : " + (s!=solution));
			}
			// Update changed status
			changed |= (s != solution);
			k++;
		}
		//			System.out.println(tab(sTab) + "<<< CLOSING : " + solution);
		return solution;
	}

	/**
	 * Determine whether one declaration is an "ancestor" of another. This happens
	 * in a very specific situation where the child is given the type of the
	 * ancestor with additional constraints. For example:
	 *
	 * <pre>
	 * type nat is (int n) where n >= 0
	 * type pos1 is (nat p) where p > 0
	 * type pos2 is (int p) where p > 0
	 * </pre>
	 *
	 * Here, we'd say that <code>nat</code> is an ancestor of <code>pos1</code> but
	 * not <code>pos2</code>.
	 *
	 * @param parent
	 * @param child
	 * @return
	 */
	private static boolean isAncestorOf(Type parent, Type child) {
		int t1_opcode = normalise(parent.getOpcode());
		int t2_opcode = normalise(child.getOpcode());
		if (parent.equals(child)) {
			// NOTE: seems a bit inefficient as can perform the equality check during this
			// traversal.
			return true;
		} else if (t1_opcode == t2_opcode) {
			switch (child.getOpcode()) {
			case TYPE_void:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
			case TYPE_any:
				return true;
			case TYPE_array: {
				Type.Array p = (Type.Array) parent;
				Type.Array c = (Type.Array) child;
				return isAncestorOf(p.getElement(), c.getElement());
			}
			case TYPE_reference: {
				Type.Reference p = (Type.Reference) parent;
				Type.Reference c = (Type.Reference) child;
				// FIXME: what about lifetimes?
				return p.getElement().equals(c.getElement());
			}
			case TYPE_record: {
				Type.Record p = (Type.Record) parent;
				Type.Record c = (Type.Record) child;
				Tuple<Type.Field> p_fields = p.getFields();
				Tuple<Type.Field> c_fields = c.getFields();
				// FIXME: support open records
				if (p_fields.size() == c_fields.size()) {
					for (int i = 0; i != p_fields.size(); ++i) {
						Type.Field f = p_fields.get(i);
						Type pt = f.getType();
						Type ct = c.getField(f.getName());
						if (ct == null || !isAncestorOf(pt, ct)) {
							return false;
						}
					}
					return true;
				}
				break;
			}
			case TYPE_tuple: {
				Type.Tuple p = (Type.Tuple) parent;
				Type.Tuple c = (Type.Tuple) child;
				//
				if (p.size() == c.size()) {
					for (int i = 0; i != p.size(); ++i) {
						if (!isAncestorOf(p.get(i), c.get(i))) {
							return false;
						}
					}
					return true;
				}
				break;
			}
			case TYPE_function: {
				Type.Function p = (Type.Function) parent;
				Type.Function c = (Type.Function) child;
				return isAncestorOf(c.getParameter(), p.getParameter()) && isAncestorOf(p.getReturn(), c.getReturn());
			}
			case TYPE_method: {
				Type.Method p = (Type.Method) parent;
				Type.Method c = (Type.Method) child;
				// FIXME: what about lifetimes?
				return isAncestorOf(c.getParameter(), p.getParameter()) && isAncestorOf(p.getReturn(), c.getReturn());
			}
			case TYPE_property: {
				Type.Property p = (Type.Property) parent;
				Type.Property c = (Type.Property) child;
				return isAncestorOf(c.getParameter(), p.getParameter()) && isAncestorOf(p.getReturn(), c.getReturn());
			}
			case TYPE_nominal: {
				Type.Nominal n = (Type.Nominal) child;
				return isAncestorOf(parent, n.getConcreteType());
			}
			}
		} else if (t2_opcode == TYPE_nominal) {
			Type.Nominal n = (Type.Nominal) child;
			return isAncestorOf(parent, n.getConcreteType());
		}
		return false;
	}

	// ================================================================================
	// Helpers
	// ================================================================================

	private static final Tuple<Expr> EMPTY_INVARIANT = new Tuple<>();

	/**
	 * Expand a given array whilst using a given element to fill the new spaces.
	 *
	 * @param src
	 * @param n
	 * @param t
	 * @return
	 */
	public static Type[] expand(Type[] src, int n, Type t) {
		final int m = src.length;
		Type[] nSrc = new Type[n];
		System.arraycopy(src, 0, nSrc, 0, m);
		for (int i = m; i < n; ++i) {
			nSrc[i] = t;
		}
		return nSrc;
	}

	/**
	 * Normalise opcode for sake of simplicity. This allows us to compare the types
	 * of two operands more accurately using a switch.
	 *
	 * @param opcode
	 * @return
	 */
	public static int normalise(int opcode) {
		switch (opcode) {
		case TYPE_method:
		case TYPE_property:
		case TYPE_function:
			return TYPE_function;
		}
		//
		return opcode;
	}
}